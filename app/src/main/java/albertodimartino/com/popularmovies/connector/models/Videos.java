package albertodimartino.com.popularmovies.connector.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos {
    String id;
    @SerializedName("results") List<Video> videos;

    public String getId() {
        return id;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public class Video {
        String id;
        String key;

        public String getId() {
            return id;
        }

        public String getKey() {
            return key;
        }
    }
}
