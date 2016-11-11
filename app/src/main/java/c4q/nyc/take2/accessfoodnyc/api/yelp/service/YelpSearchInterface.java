package c4q.nyc.take2.accessfoodnyc.api.yelp.service;

import c4q.nyc.take2.accessfoodnyc.api.yelp.models.Business;
import c4q.nyc.take2.accessfoodnyc.api.yelp.models.YelpResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Hoshiko on 8/16/15.
 */
public interface YelpSearchInterface {

    @GET("/v2/search/?category_filter=foodtrucks&sort=1&limit=20")
    Call<YelpResponse> searchFoodCarts (@Query("location") String loc,
                                        @Query("category_filter") String category,
                                        @Query("sort") int sort,
                                        @Query("limit") int limit);

    @GET("/v2/business/{id}")
    Call<Business> searchBusiness(@Path("id") String businessId);
}
