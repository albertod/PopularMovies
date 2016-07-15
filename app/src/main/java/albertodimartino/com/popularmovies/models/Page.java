package albertodimartino.com.popularmovies.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {

    String page;

    public List<Movie> getMovies() {
        return mMovies;
    }

    @SerializedName("results")
    List<Movie> mMovies;

    public class Movie {

        @SerializedName("original_title") private String mOriginaløTitle;
        @SerializedName("poster_path") private String mPosterPath;
        @SerializedName("overview") private String mOverview;
        @SerializedName("release_date") private String mReleaseDate;
        @SerializedName("vote_average") private String mVoteAverage;


        public Movie(String originaløTitle, String posterPath, String overview, String releaseDate, String voteAverage) {
            mOriginaløTitle = originaløTitle;
            mPosterPath = posterPath;
            mOverview = overview;
            mReleaseDate = releaseDate;
            mVoteAverage = voteAverage;
        }

        public String getOriginaløTitle() {
            return mOriginaløTitle;
        }

        public void setOriginaløTitle(String originaløTitle) {
            mOriginaløTitle = originaløTitle;
        }

        public String getPosterPath() {
            return mPosterPath;
        }

        public void setPosterPath(String posterPath) {
            mPosterPath = posterPath;
        }

        public String getOverview() {
            return mOverview;
        }

        public void setOverview(String overview) {
            mOverview = overview;
        }

        public String getReleaseDate() {
            return mReleaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            mReleaseDate = releaseDate;
        }

        public String getVoteAverage() {
            return mVoteAverage;
        }

        public void setVoteAverage(String voteAverage) {
            mVoteAverage = voteAverage;
        }
    }
}


