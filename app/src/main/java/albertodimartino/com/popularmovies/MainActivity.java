package albertodimartino.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import albertodimartino.com.popularmovies.models.Page;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = MainActivity.class.toString();

    private final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;
    private final String POPULAR_MOVIES = "popular";
    private final String TOP_RATED_MOVIES = "top_rated";
    private final String KEY_API_KEY = "api_key";
    private final Gson gson = new Gson();
    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private List<Page.Movie> mMovieList;
    private Page movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gridview);
        mMovieList = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(this, mMovieList);
        mGridView.setAdapter(mMovieAdapter);

        if (isOnline()) {
            new DownloadMoviesTask().execute(POPULAR_MOVIES);

            mGridView.setOnItemClickListener((parent, v, position, id) -> {
                Page.Movie movie = (Page.Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_EXTRA, movie);
                startActivity(intent);
            });
        } else {
            Toast.makeText(this, "Can't load movies at this time", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            new DownloadMoviesTask().execute(POPULAR_MOVIES);

            mGridView.setOnItemClickListener((parent, v, position, id) -> {
                Page.Movie movie = (Page.Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_EXTRA, movie);
                startActivity(intent);
            });
        } else {
            Toast.makeText(this, "Can't load movies at this time", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.top_rated_order_menu_item:
                Toast.makeText(this, "TOP RATED", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.most_popular_order_menu_item:
                Toast.makeText(this, "MOST POPULAR", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Using function from stackoverflow posted on Udacity guide
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class DownloadMoviesTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = DownloadMoviesTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            try {
                fetchMovies(params[0]);
            }catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return null;
        }

        private void fetchMovies(String type) throws IOException {
            HttpURLConnection urlConnection = null;
            try {
                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(type.equals(POPULAR_MOVIES) ? POPULAR_MOVIES : TOP_RATED_MOVIES)
                        .appendQueryParameter(KEY_API_KEY, API_KEY)
                        .build();

                // Create request and open connection
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                String moviesJson = readIt(urlConnection.getInputStream());

                if (moviesJson != null && !moviesJson.isEmpty()) {
                    movies = gson.fromJson(moviesJson, Page.class);
                }

                mMovieList = movies.getMovies();

            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: "+ e.toString());
                Log.e(LOG_TAG, "Exception message: " + e.toString());
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mMovieList != null && !mMovieList.isEmpty()) {
                // Update Movie Adapter content
                mMovieAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(MainActivity.this, "Failed to fetch movies!", Toast.LENGTH_SHORT).show();
            }

        }

        // Reads an InputStream and converts it to a String.
        private String readIt(InputStream inputStream) throws IOException, UnsupportedEncodingException {

            StringBuffer buffer = new StringBuffer();
            BufferedReader reader;

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        }

    }

    private class MovieAdapter extends ArrayAdapter<Page.Movie> {
        private Context mContext;

        public MovieAdapter(Context context, List<Page.Movie> movies) {
            super(context, 0, movies);
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mMovieList.size();
        }

        @Override
        public Page.Movie getItem(int position) {
            return mMovieList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get movie item on position
            Page.Movie movie = getItem(position);
            if (convertView == null) {
                // This is a new view, we need to inflate the layout  or in this case initialize the view
                convertView = new ImageView(getContext());
            }
            Picasso.with(mContext).load(Page.Movie.IMAGE_ENDPOINT + movie.getPosterPath()).into((ImageView) convertView);
            return  convertView;
        }
    }
}
