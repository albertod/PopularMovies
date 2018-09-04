package albertodimartino.com.popularmovies;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = MainActivity.class.toString();

    private final String POPULAR_MOVIES = "popular";
    private final String TOP_RATED_MOVIES = "top_rated";
    private final String MOVIES_SORT_SAVED_ON_BUNDLE = "bundle_movies";
    private String currentSorting = POPULAR_MOVIES; // The default is always popular movies

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO only for debug
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(MOVIES_SORT_SAVED_ON_BUNDLE, currentSorting);
    }
}
