package albertodimartino.com.popularmovies.connector;

import albertodimartino.com.popularmovies.connector.models.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieDbService {

    @GET("movie/{type}")
    Call<Page> movies(
            @Path("type") String type);
}
