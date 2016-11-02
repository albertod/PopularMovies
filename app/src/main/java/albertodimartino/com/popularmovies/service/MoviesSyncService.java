package albertodimartino.com.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import albertodimartino.com.popularmovies.connector.MovieDbService;
import albertodimartino.com.popularmovies.connector.ServiceGenerator;
import albertodimartino.com.popularmovies.connector.models.Page;

public class MoviesSyncService extends IntentService {

    private final String LOG_TAG = MoviesSyncService.class.getSimpleName();
    public static final String TYPE = "type";

    private MovieDbService mMovieDbService;
    private ArrayList<Page.Movie> mMovieList;

    public MoviesSyncService() {
        super("MoviesSyncService'");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            final String type = intent.getStringExtra(TYPE);
            mMovieDbService = ServiceGenerator.createService(MovieDbService.class);
            Page movies = mMovieDbService.movies(type).execute().body();
            mMovieList = (ArrayList<Page.Movie>) movies.getMovies();
            if (mMovieList != null && !mMovieList.isEmpty()) {
                Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
                resultIntent.putParcelableArrayListExtra(Constants.MOVIES_DATA, mMovieList);
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
            }else {
                Toast.makeText(this, "Failed to fetch movies!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "exception making HTTP call");
            e.printStackTrace();
        }
    }

    public final class Constants {
        // Defines a custom Intent action
        public static final String BROADCAST_ACTION =
                "albertodimartino.com.popularmovies.service.BROADCAST";
        // Defines the key for the status "extra" in an Intent
        public static final String MOVIES_DATA =
                "albertodimartino.com.popularmovies.service.STATUS";
    }
}
