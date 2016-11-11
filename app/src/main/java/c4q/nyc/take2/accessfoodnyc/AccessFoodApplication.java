package c4q.nyc.take2.accessfoodnyc;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

import c4q.nyc.take2.accessfoodnyc.api.yelp.models.YelpResponse;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccessFoodApplication extends Application {

    private static AccessFoodApplication sInstance;
    public static YelpResponse sYelpResponse;
    private Retrofit mRetrofit;

    private static final String BASE_URL = "https://api.yelp.com";
    public static final String CONSUMER_KEY = "BrL42zhoUCPLZCO1D5b7LQ";
    public static final String CONSUMER_SECRET = "ZsBqxMKEI4QEipoFdlmwadnyb8Y";
    public static final String TOKEN = "ilXQIAE-HffHzEdxnasVZ1uNrePI8wM-";
    public static final String TOKEN_SECRET = "7I1jh-uEEJuq1akXSm5dkVn6U_w";

    public static AccessFoodApplication getInstance() {
        return sInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
		/*
		 * Add Parse initialization code here
		 */
        Parse.initialize(this);
        ParseFacebookUtils.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);

    }

    public void setupRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient client = httpClient.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}

