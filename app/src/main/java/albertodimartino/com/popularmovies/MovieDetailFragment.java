package albertodimartino.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import albertodimartino.com.popularmovies.connector.MovieDbService;
import albertodimartino.com.popularmovies.connector.ServiceGenerator;
import albertodimartino.com.popularmovies.connector.models.Page;
import albertodimartino.com.popularmovies.connector.models.Reviews;
import albertodimartino.com.popularmovies.connector.models.Tuple;
import albertodimartino.com.popularmovies.connector.models.Videos;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Tuple<String, List<Reviews.Review>>> {

    static final String DETAIL_URI = "URI";
    private ListView trailersListView;
    private Button youtubeButton;
    private String youtubeLink;
    private List<Reviews.Review> reviews;

    private static final Integer LOADER_ID = 22;
    public static final String OPERATION_MOVIE_ID_EXTRA = "movieId";
    public static final String NOT_MOVIE_TRAILER_AVAILABLE = "not_movie_trailer_available";

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

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

            // Youtube link and reviews fetching
            Bundle bundle = new Bundle();
            bundle.putString(OPERATION_MOVIE_ID_EXTRA, movie.getId().toString());
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();

            youtubeButton = (Button) rootView.findViewById(R.id.youtube_button);
            trailersListView = (ListView) rootView.findViewById(R.id.trailers_listView);

            return rootView;

        }else {
            getActivity().finish();
            return null;
        }
    }

    @Override
    public Loader<Tuple<String, List<Reviews.Review>>> onCreateLoader(int id, Bundle args) {
        return new DataDownloader(this.getContext(), args.getString("movieId"));
    }

    @Override
    public void onLoadFinished(Loader<Tuple<String, List<Reviews.Review>>> loader, Tuple<String, List<Reviews.Review>> tuple) {
        this.youtubeLink = tuple.getT1();
        this.reviews = tuple.getT2();

        if (!(youtubeLink.equals(NOT_MOVIE_TRAILER_AVAILABLE))) {
            // Set onClick listener for youtube button
            youtubeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeLink)));
                }
            });
            // Enable button
            youtubeButton.setEnabled(true);
        } else {
            youtubeButton.setText("No trailer available for this movie!");
        }

        ArrayAdapter<Reviews.Review> reviewListViewAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, reviews);
        // List view reviews
        trailersListView.setAdapter(reviewListViewAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Tuple<String, List<Reviews.Review>>> loader) { }

    /**
     * Async task that check in the DB if the movie has been saved to the db before
     * 1. If not, get the data for reviews/youtulink from API, add to DB and return to UI
     * 2. If it has been saved before return data to ui
     */
    protected static class DataDownloader extends AsyncTaskLoader<Tuple<String, List<Reviews.Review>>> {
        private final static String LOG_TAG = DataDownloader.class.getSimpleName();
        private MovieDbService moviesService;
        private String mMovieId;



        public DataDownloader(Context context, String movieId) {
            super(context);
            moviesService = ServiceGenerator.createService(MovieDbService.class);
            mMovieId = movieId;
        }

        @Override
        public Tuple<String, List<Reviews.Review>> loadInBackground() {
            try {
                Tuple<String, List<Reviews.Review>> resultTuple = new Tuple<>();
                Videos youtubeLinks = moviesService.movieTrailers(mMovieId).execute().body();
                Reviews reviews = moviesService.reviews(mMovieId).execute().body();

                if (youtubeLinks.getVideos() == null || youtubeLinks.getVideos().isEmpty()) {
                    Log.w(LOG_TAG, "No videos available for movie " + mMovieId);
                    resultTuple.setT1(NOT_MOVIE_TRAILER_AVAILABLE);
                } else {
                    resultTuple.setT1(youtubeLinks.getVideos().get(0).getKey());
                }

                if (reviews.getReviewList() == null || reviews.getReviewList().isEmpty()) {
                    Log.w(LOG_TAG, "No reviews available for movie " + mMovieId);
                    resultTuple.setT2(new ArrayList<>()); //emptyList
                } else {
                    resultTuple.setT2(reviews.getReviewList());
                }

                return resultTuple;

            } catch (IOException e) {
                Log.w(LOG_TAG ,"there was an error fetching the remote data");
                e.printStackTrace();
                return new Tuple<>(NOT_MOVIE_TRAILER_AVAILABLE, new ArrayList<>());
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
