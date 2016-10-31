package albertodimartino.com.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import albertodimartino.com.popularmovies.connector.models.Page;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Page.Movie movie = getActivity().getIntent().getParcelableExtra(MovieDetailActivity.MOVIE_EXTRA);

        if (movie != null) {

            View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);

            ImageView posterImageview = (ImageView) rootView.findViewById(R.id.poster_image_view);
            Picasso.with(getActivity()).load(Page.Movie.IMAGE_ENDPOINT + movie.getPosterPath()).into(posterImageview);

            TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_title_title_text_view);
            movieTitle.setText(movie.getOriginalTitle());

            TextView releateDateTextView = (TextView) rootView.findViewById(R.id.movie_release_title_text_view);
            releateDateTextView.setText(movie.getReleaseDate());

            TextView averageRatingTextView = (TextView) rootView.findViewById(R.id.movie_average_rating_title_text_view);
            averageRatingTextView.setText(movie.getVoteAverage());

            TextView overviewTextView = (TextView) rootView.findViewById(R.id.movie_overview_title_text_view);
            overviewTextView.setText(movie.getOverview());
            return rootView;

        }else {
            getActivity().finish();
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
