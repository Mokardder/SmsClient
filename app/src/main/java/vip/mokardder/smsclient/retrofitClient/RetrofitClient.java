package vip.mokardder.smsclient.retrofitClient;


import static vip.mokardder.smsclient.ui.MainActivity.TAG;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vip.mokardder.smsclient.apiInterface.API;


public class RetrofitClient {
    public static Retrofit retrofit;
    API api;
    public static final String BASE_URL = "https://script.google.com/macros/s/";

    public static Retrofit getRetrofitInstance () {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()

                .addInterceptor(logging)
                .build();
        HttpLoggingInterceptor loggings = new HttpLoggingInterceptor(message -> Log.d(TAG, "log: " + message));


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();


        }
        return retrofit;
    };

}
