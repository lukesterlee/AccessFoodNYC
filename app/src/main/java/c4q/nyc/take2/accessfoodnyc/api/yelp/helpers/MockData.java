package c4q.nyc.take2.accessfoodnyc.api.yelp.helpers;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import c4q.nyc.take2.accessfoodnyc.R;
import c4q.nyc.take2.accessfoodnyc.api.yelp.models.YelpResponse;

/**
 * Created by Hoshiko on 8/16/15.
 */
public class MockData {

    public static YelpResponse getMockData(Context context) {
        //YelpResponse response = new YelpResponse();
        InputStream raw = context.getResources().openRawResource(R.raw.yelp_search_mock_data);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        YelpResponse response = gson.fromJson(rd, YelpResponse.class);
        return response;
    }


}