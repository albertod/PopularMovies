package albertodimartino.com.popularmovies.connector;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import albertodimartino.com.popularmovies.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private final static String BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY = BuildConfig.MOVIE_DB_API_KEY;
    private final static String KEY_API_KEY = "api_key";


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static final Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        // Add API-Key to every request
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter(KEY_API_KEY, API_KEY)
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        //TODO only for debug
        httpClient.addNetworkInterceptor((new StethoInterceptor()));

        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
