package c4q.nyc.take2.accessfoodnyc.api.yelp.service;

import c4q.nyc.take2.accessfoodnyc.api.yelp.models.YelpResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Hoshiko on 8/16/15.
 */
public interface YelpSearchService {

    public static final String BASE_URL = "https://api.yelp.com";

    @GET("/v2/search/?category_filter=foodtrucks&sort=1&limit=20")
    void searchFoodCarts (@Query("location") String loc, Callback<YelpResponse> responseResult);

}
