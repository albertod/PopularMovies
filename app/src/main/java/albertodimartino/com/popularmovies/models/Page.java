package albertodimartino.com.popularmovies.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {

    String page;

    public List<Movie> getMovies() {
        return mMovies;
    }

    @SerializedName("results")
    List<Movie> mMovies;

    public static class Movie implements Parcelable {

        @SerializedName("original_title") private String mOriginalTitle;
        @SerializedName("poster_path") private String mPosterPath;
        @SerializedName("overview") private String mOverview;
        @SerializedName("release_date") private String mReleaseDate;
        @SerializedName("vote_average") private String mVoteAverage;

        // Constants
        public static final String IMAGE_ENDPOINT = "http://image.tmdb.org/t/p/w500";

        public Movie(String originaløTitle, String posterPath, String overview, String releaseDate, String voteAverage) {
            mOriginalTitle = originaløTitle;
            mPosterPath = posterPath;
            mOverview = overview;
            mReleaseDate = releaseDate;
            mVoteAverage = voteAverage;
        }

        public String getOriginalTitle() {
            return mOriginalTitle;
        }

        public void setOriginalTitle(String originaløTitle) {
            mOriginalTitle = originaløTitle;
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

        // Parcelable implementations
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mOriginalTitle);
            dest.writeString(this.mPosterPath);
            dest.writeString(this.mOverview);
            dest.writeString(this.mReleaseDate);
            dest.writeString(this.mVoteAverage);
        }

        protected Movie(Parcel in) {
            this.mOriginalTitle = in.readString();
            this.mPosterPath = in.readString();
            this.mOverview = in.readString();
            this.mReleaseDate = in.readString();
            this.mVoteAverage = in.readString();
        }

        public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
            @Override
            public Movie createFromParcel(Parcel source) {
                return new Movie(source);
            }

            @Override
            public Movie[] newArray(int size) {
                return new Movie[size];
            }
        };
    }
}


