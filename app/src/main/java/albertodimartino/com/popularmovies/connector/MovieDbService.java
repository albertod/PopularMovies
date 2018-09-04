package albertodimartino.com.popularmovies.connector;

import albertodimartino.com.popularmovies.connector.models.Page;
import albertodimartino.com.popularmovies.connector.models.Reviews;
import albertodimartino.com.popularmovies.connector.models.Videos;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieDbService {

    @GET("movie/{type}")
    Call<Page> movies(@Path("type") String type);

    @GET("movie/{id}/videos")
    Call<Videos> movieTrailers(@Path("id") String id);

    @GET("movie/{id}/reviews")
    Call<Reviews> reviews(@Path("id") String id);
}
