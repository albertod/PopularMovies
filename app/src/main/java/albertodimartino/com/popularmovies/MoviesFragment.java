package albertodimartino.com.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import albertodimartino.com.popularmovies.connector.models.Page;
import albertodimartino.com.popularmovies.service.MoviesSyncService;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String POPULAR_MOVIES = "popular";
    private final String TOP_RATED_MOVIES = "top_rated";
    private final String MOVIES_SORT_SAVED_ON_BUNDLE = "bundle_movies";
    private String currentSorting = POPULAR_MOVIES; // The default is always popular movies

    private ContentResolver mResolver;
    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private List<Page.Movie> mMovieList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getActivity().getContentResolver();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Let the OS know it has a menu
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.movies_fragment, container, false);

        mMovieList = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null)
            currentSorting = savedInstanceState.getString(MOVIES_SORT_SAVED_ON_BUNDLE);
        if (currentSorting == null)
            currentSorting = TOP_RATED_MOVIES;

        mGridView.setOnItemClickListener((parent, v, position, id) -> {
            Page.Movie movie = (Page.Movie) parent.getItemAtPosition(position);
            Intent intentDetailView = new Intent(getContext(), MovieDetailActivity.class);
            intentDetailView.putExtra(MovieDetailActivity.MOVIE_EXTRA, movie);
            startActivity(intentDetailView);
        });

        // Filter to link response from MoviesSyncService with ResultReceiver
        IntentFilter mStatusIntentFilter = new IntentFilter(MoviesSyncService.Constants.BROADCAST_ACTION);
        ResultReceiver mResultReceiver = new ResultReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mResultReceiver, mStatusIntentFilter);

        if (isOnline()) {
            Intent downloadMoviesIntent = new Intent(getActivity(), MoviesSyncService.class);
            downloadMoviesIntent.putExtra(MoviesSyncService.TYPE, currentSorting);
            getActivity().startService(downloadMoviesIntent);
        } else {
            Toast.makeText(getContext(), "Can't load movies at this time", Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    public class ResultReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private ResultReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            List<Page.Movie> moviesFromService = intent.getParcelableArrayListExtra(MoviesSyncService.Constants.MOVIES_DATA);
            if (moviesFromService != null) {
                mMovieList = moviesFromService;
                mMovieAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.top_rated_order_menu_item:
                if (isOnline()) {
                    Intent downloadMoviesIntent = new Intent(getActivity(), MoviesSyncService.class);
                    downloadMoviesIntent.putExtra(MoviesSyncService.TYPE, TOP_RATED_MOVIES);
                    getActivity().startService(downloadMoviesIntent);
                    currentSorting = TOP_RATED_MOVIES;
                }
                else
                    Toast.makeText(getContext(), "Can't load movies at this time", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.most_popular_order_menu_item:
                if (isOnline()){
                    Intent downloadMoviesIntent = new Intent(getActivity(), MoviesSyncService.class);
                    downloadMoviesIntent.putExtra(MoviesSyncService.TYPE, POPULAR_MOVIES);
                    getActivity().startService(downloadMoviesIntent);
                    currentSorting = POPULAR_MOVIES;
                }
                else
                    Toast.makeText(getContext(), "Can't load movies at this time", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIES_SORT_SAVED_ON_BUNDLE, currentSorting);
    }

    // Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        return;
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

    // Helpers

    // Using function from stackoverflow posted on Udacity guide
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
