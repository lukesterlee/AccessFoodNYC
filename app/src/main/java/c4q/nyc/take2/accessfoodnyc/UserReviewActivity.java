package c4q.nyc.take2.accessfoodnyc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yelp.clientlib.entities.Business;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReviewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private UserReviewAdapter mAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_user_reviews);
        setSupportActionBar(mToolbar);
        ParseUser user = ParseUser.getCurrentUser();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME) + "'s Reviews");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_user_reviews);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);

        mAdapter = new UserReviewAdapter(getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        final String today = "day" + Integer.toString(day);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_REVIEW);
        query.include(Constants.VENDOR);
        query.whereEqualTo("writer", user).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                for (final ParseObject review : list) {
                    final ParseObject vendor = review.getParseObject(Constants.VENDOR);
                    if (vendor.getParseGeoPoint("location") == null) {
                        MainApplication.getInstance().getYelpAPI().getBusiness(vendor.getString(Constants.YELP_ID))
                                .enqueue(new Callback<Business>() {
                                    @Override
                                    public void onResponse(Call<Business> call, Response<Business> response) {
                                        if (response.isSuccessful()) {
                                            Business business = response.body();
                                            Vendor truck = new Vendor.Builder(business.id())
                                                    .setRating(business.rating())
                                                    .setPicture(business.imageUrl())
                                                    .setAddress(DetailsFragment.addressGenerator(business).get(0))
                                                    .isYelp(true).setName(business.name()).build();
                                            final Review item = new Review();
                                            item.setTitle(review.getString("title"));
                                            item.setDescription(review.getString("description"));
                                            item.setRating(review.getInt("rating"));
                                            item.setDate(review.getCreatedAt());
                                            item.setVendor(truck);
                                            mAdapter.addReview(item);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Business> call, Throwable t) {

                                    }
                                });
                    } else {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put(Constants.VENDOR, vendor);
                        ParseCloud.callFunctionInBackground("averageRatings", params, new FunctionCallback<Float>() {
                            @Override
                            public void done(Float rate, ParseException e) {
                                if (rate == null) {
                                    rate = 4.0f;
                                }
                                Vendor truck = new Vendor.Builder(vendor.getObjectId())
                                        .setRating(rate)
                                        .setPicture(vendor.getString(Constants.PARSE_COLUMN_PROFILE))
                                        .setAddress(vendor.getString("address"))
                                        .setHours(vendor.getString(today))
                                        .isYelp(false).setName(vendor.getString("name")).build();
                                final Review item = new Review();
                                item.setTitle(review.getString("title"));
                                item.setDescription(review.getString("description"));
                                item.setRating(review.getInt("rating"));
                                item.setDate(review.getCreatedAt());
                                item.setVendor(truck);
                                mAdapter.addReview(item);

                            }
                        });


                    }

                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Vendor vendor = mAdapter.getItem(position).getVendor();
                        String objectId = vendor.getId();
                        Intent intent = new Intent(getApplicationContext(), VendorInfoActivity.class);
                        intent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                        if (vendor.isYelp()) {
                            intent.putExtra(Constants.EXTRA_KEY_IS_YELP, true);
                        } else {
                            intent.putExtra(Constants.EXTRA_KEY_IS_YELP, false);
                        }
                        startActivity(intent);
                    }
                })
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        LoginManager.getInstance().logOut();
        ParseUser.logOut();
        Toast.makeText(getApplicationContext(), "Successfully logged out!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
