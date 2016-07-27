package albertodimartino.com.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import albertodimartino.com.popularmovies.models.Page;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_EXTRA = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Page.Movie movie = getIntent().getParcelableExtra(MOVIE_EXTRA);

        if (movie != null) {
            ImageView posterImageview = (ImageView) findViewById(R.id.poster_image_view);
            Picasso.with(this).load(Page.Movie.IMAGE_ENDPOINT + movie.getPosterPath()).into(posterImageview);

            TextView movieTitle = (TextView) findViewById(R.id.movie_title_title_text_view);
            movieTitle.setText(movie.getOriginalTitle());

            TextView releateDateTextView = (TextView) findViewById(R.id.movie_release_title_text_view);
            releateDateTextView.setText(movie.getReleaseDate());

            TextView averageRatingTextView = (TextView) findViewById(R.id.movie_average_rating_title_text_view);
            averageRatingTextView.setText(movie.getVoteAverage());

            TextView overviewTextView = (TextView) findViewById(R.id.movie_overview_title_text_view);
            overviewTextView.setText(movie.getOverview());
            overviewTextView.setMovementMethod(new ScrollingMovementMethod());

        }else {
            finish();
        }
    }
}
