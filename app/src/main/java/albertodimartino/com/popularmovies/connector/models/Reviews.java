package albertodimartino.com.popularmovies.connector.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {
    private String id;
    @SerializedName("results") private List<Review> mReviewList;

    public String getId() {
        return id;
    }

    public List<Review> getReviewList() {
        return mReviewList;
    }

    public class Review {
        private String author;
        private String content;

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "By: " + author + "\n\n"
                    + content + "\n\n";
        }
    }
}
