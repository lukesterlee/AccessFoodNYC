package c4q.nyc.take2.accessfoodnyc.api.yelp.service;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

/**
 * Created by Hoshiko on 8/16/15.
 */
public class ServiceGenerator {
    public static final String CONSUMER_KEY = "BrL42zhoUCPLZCO1D5b7LQ";
    public static final String CONSUMER_SECRET = "ZsBqxMKEI4QEipoFdlmwadnyb8Y";
    public static final String TOKEN = "ilXQIAE-HffHzEdxnasVZ1uNrePI8wM-";
    public static final String TOKEN_SECRET = "7I1jh-uEEJuq1akXSm5dkVn6U_w";

//    private static final String CONSUMER_KEY = "0FvZFCRzzi7WcgZK3nKE8Q";
//    private static final String CONSUMER_SECRET = "W8Agn8NkizF7aHzzYk8aLnfwR2E";
//    private static final String TOKEN = "I0A-izBz1griujxBU1-OKUUiGRZ5HLLP";
//    private static final String TOKEN_SECRET = "ZvB_3dJq3TJOTlQWCqrK0Oau1sI";

    public static YelpSearchService createYelpSearchService(){

        //use the library that extends OKHttpClient to sign the request using the token (as required by Yelp)
        RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        oAuthConsumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        //generating signature using oAuthConsumer (with key secret and token +token secret)
        OkClient httpClient = new SigningOkClient(oAuthConsumer);



        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(YelpSearchService.BASE_URL)
                .setClient(httpClient)
                .build();

        YelpSearchService service = restAdapter.create(YelpSearchService.class);

        return service;
    }

    public static YelpBusinessSearchService createYelpBusinessSearchService(){

        //use the library that extends OKHttpClient to sign the request using the token (as required by Yelp)
        RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        oAuthConsumer.setTokenWithSecret(TOKEN, TOKEN_SECRET);

        //generating signature using oAuthConsumer (with key secret and token +token secret)
        OkClient httpClient = new SigningOkClient(oAuthConsumer);



        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(YelpBusinessSearchService.BASE_URL)
                .setClient(httpClient)
                .build();

        YelpBusinessSearchService service = restAdapter.create(YelpBusinessSearchService.class);

        return service;
    }
}
