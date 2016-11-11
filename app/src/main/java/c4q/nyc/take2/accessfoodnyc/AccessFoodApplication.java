package c4q.nyc.take2.accessfoodnyc;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

import c4q.nyc.take2.accessfoodnyc.api.yelp.models.YelpResponse;

public class AccessFoodApplication extends Application {

    private static AccessFoodApplication sInstance;
    public static YelpResponse sYelpResponse;

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
}

