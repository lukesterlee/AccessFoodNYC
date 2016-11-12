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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Coordinate;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFavoriteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private VendorListAdapter mAdapter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite);

        final ParseUser user = ParseUser.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_user_favorite);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME) + "'s Favorites");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        final String today = "day" + Integer.toString(day);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_user_favorite);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);


        ParseGeoPoint point = user.getParseGeoPoint("location");

        mAdapter = new VendorListAdapter(getApplicationContext(), point);
        mRecyclerView.setAdapter(mAdapter);

        ParseQuery<ParseObject> favorites = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
        favorites.include(Constants.VENDOR);
        favorites.whereEqualTo(Constants.PARSE_COLUMN_FOLLOWER, user).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (ParseObject favorite : list) {
                    final ParseObject vendor = favorite.getParseObject(Constants.VENDOR);

                    if (vendor.getParseGeoPoint("location") == null) {

                        ParseRelation<ParseUser> friends = user.getRelation("friends");
                        friends.getQuery().findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {

                                ParseQuery<ParseObject> favorites = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
                                favorites.include(Constants.PARSE_COLUMN_FOLLOWER);
                                favorites.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn(Constants.PARSE_COLUMN_FOLLOWER, list);
                                favorites.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(final List<ParseObject> list, ParseException e) {
                                        MainApplication.getInstance().getYelpAPI().getBusiness(vendor.getString(Constants.YELP_ID))
                                                .enqueue(new Callback<Business>() {
                                                    @Override
                                                    public void onResponse(Call<Business> call, Response<Business> response) {
                                                        if (response.isSuccessful()) {
                                                            Business business = response.body();
                                                            Coordinate coordinate = business.location().coordinate();
                                                            ParseGeoPoint location = new ParseGeoPoint(coordinate.latitude(), coordinate.longitude());
                                                            Vendor truck = new Vendor.Builder(business.id())
                                                                    .isYelp(true).isLiked(true).setName(business.name())
                                                                    .setAddress(DetailsFragment.addressGenerator(business).get(0))
                                                                    .setPicture(business.imageUrl()).setRating(business.rating())
                                                                    .setLocation(location).setFriends(list).build();
                                                            mAdapter.addVendor(truck);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Business> call, Throwable t) {

                                                    }
                                                });

                                    }
                                });

                            }
                        });

                    } else {

                        final String json = vendor.getString(today);
                        ParseRelation<ParseUser> friends = user.getRelation("friends");
                        friends.getQuery().findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> friends, ParseException e) {

                                ParseQuery<ParseObject> favorites = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
                                favorites.include(Constants.PARSE_COLUMN_FOLLOWER);
                                favorites.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn(Constants.PARSE_COLUMN_FOLLOWER, friends);
                                favorites.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(final List<ParseObject> list, ParseException e) {

                                        Vendor truck = new Vendor.Builder(vendor.getObjectId())
                                                .setName(vendor.getString("name")).setAddress(vendor.getString("address"))
                                                .isYelp(false)
                                                .setFriends(list).setLocation(vendor.getParseGeoPoint("location")).setHours(json)
                                                .setPicture(vendor.getString(Constants.PARSE_COLUMN_PROFILE)).setRating(vendor.getDouble("rating"))
                                                .isLiked(true).build();
                                        mAdapter.addVendor(truck);

                                    }
                                });

                            }
                        });

                    }

                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        goToVendorInfoAcvitity(position);
                    }
                })
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_favorite, menu);
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

    public void goToVendorInfoAcvitity(int position) {
        Intent intent = new Intent(getApplicationContext(), VendorInfoActivity.class);
        Vendor vendor = mAdapter.getItem(position);
        intent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, vendor.getId());
        if (vendor.isYelp()) {
            intent.putExtra(Constants.EXTRA_KEY_IS_YELP, true);
        } else {
            intent.putExtra(Constants.EXTRA_KEY_IS_YELP, false);
        }
        startActivity(intent);
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
