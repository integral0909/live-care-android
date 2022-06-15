package app.client.munchbear.munchbearclient.model.store;

import android.text.format.DateUtils;

import java.util.concurrent.TimeUnit;

import app.client.munchbear.munchbearclient.AppConfig;
import app.client.munchbear.munchbearclient.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Roman on 7/5/2018.
 */
public class ApiClient {

    public static final long READ_WRITE_TIMEOUT = 30 * DateUtils.SECOND_IN_MILLIS; // 30 sec
    public static final long CONNECTION_TIMEOUT = 20 * DateUtils.SECOND_IN_MILLIS; // 20 sec

    private static Retrofit retrofit;

    private static synchronized Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = getHttpBuilder();
            Authenticator authenticator = new Authenticator();

            retrofit = new Retrofit.Builder().client(builder.authenticator(authenticator).build())
                    .baseUrl(AppConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static Repository getRepository() {
        return getRetrofitInstance().create(Repository.class);
    }

    private static OkHttpClient.Builder getHttpBuilder() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder build = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(READ_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(httpLoggingInterceptor);
        return build;
    }

}
