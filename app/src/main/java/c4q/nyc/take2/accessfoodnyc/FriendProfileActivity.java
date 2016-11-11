package c4q.nyc.take2.accessfoodnyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import c4q.nyc.take2.accessfoodnyc.api.yelp.models.Business;
import c4q.nyc.take2.accessfoodnyc.api.yelp.models.Coordinate;
import c4q.nyc.take2.accessfoodnyc.api.yelp.service.YelpSearchInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendProfileActivity extends AppCompatActivity implements DialogCallback {

    private String objectId;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private VendorListAdapter mAdapter;
    private CollapsingToolbarLayout mToolbarLayout;
    private Toolbar mToolbar;

    private LinearLayout mButtonFriends;
    private LinearLayout mButtonReviews;
    private LinearLayout mButtonFavorite;
    private TextView mTextViewFriends;
    private TextView mTextViewReviews;
    private TextView mTextViewFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_friend_profile);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mButtonFriends = (LinearLayout) findViewById(R.id.button_friends_list);
        mButtonReviews = (LinearLayout) findViewById(R.id.button_user_reviews);
        mButtonFavorite = (LinearLayout) findViewById(R.id.button_profile_favorite);

        mTextViewFavorite = (TextView) findViewById(R.id.profile_number_favorite);
        mTextViewFriends = (TextView) findViewById(R.id.profile_number_friends);
        mTextViewReviews = (TextView) findViewById(R.id.profile_number_reviews);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent() != null) {
            objectId = getIntent().getStringExtra(Constants.EXTRA_KEY_OBJECT_ID);
        } else {
            objectId = "";
        }

        mImageView = (ImageView) findViewById(R.id.imageView_friend_profile);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_friend_favorites);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        final String today = "day" + Integer.toString(day);


        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.getInBackground(objectId, new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser user, ParseException e) {

                String name = user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME);
                getSupportActionBar().setTitle(name + "'s Profile");
                mToolbarLayout.setTitle(name + "'s Profile");
                mToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
                mToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


                try {
                    Picasso.with(getApplicationContext()).load(user.getString(Constants.PARSE_COLUMN_PROFILE)).into(mImageView);
                } catch (NullPointerException e3) {
                    Picasso.with(getApplicationContext()).load(R.drawable.default_profile).into(mImageView);
                }

                ParseRelation<ParseUser> friendRelation = user.getRelation("friends");
                friendRelation.getQuery().countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, ParseException e) {
                        mTextViewFriends.setText(i + "");
                    }
                });

                ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery("Review");
                reviewQuery.whereEqualTo("writer", user).countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, ParseException e) {
                        mTextViewReviews.setText(i + "");
                    }
                });


                ParseGeoPoint point = user.getParseGeoPoint("location");
                mAdapter = new VendorListAdapter(getApplicationContext(), point);
                mRecyclerView.setAdapter(mAdapter);

                ParseQuery<ParseObject> favorites = ParseQuery.getQuery("Favorite");
                favorites.include(Constants.VENDOR);
                favorites.whereEqualTo(Constants.PARSE_COLUMN_FOLLOWER, user).findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        mTextViewFavorite.setText(list.size() + "");
                        for (ParseObject favorite : list) {
                            final ParseObject vendor = favorite.getParseObject(Constants.VENDOR);

                            if (vendor.getParseGeoPoint("location") == null) {

                                ParseRelation<ParseUser> friends = user.getRelation("friends");
                                friends.getQuery().findInBackground(new FindCallback<ParseUser>() {
                                    @Override
                                    public void done(List<ParseUser> list, ParseException e) {

                                        ParseQuery<ParseObject> favorites = ParseQuery.getQuery("Favorite");
                                        favorites.include(Constants.PARSE_COLUMN_FOLLOWER);
                                        favorites.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn(Constants.PARSE_COLUMN_FOLLOWER, list);
                                        favorites.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(final List<ParseObject> list, ParseException e) {
                                                YelpSearchInterface yelpInterface = AccessFoodApplication.getInstance().getRetrofit().create(YelpSearchInterface.class);
                                                yelpInterface.searchBusiness(vendor.getString(Constants.YELP_ID))
                                                        .enqueue(new Callback<Business>() {
                                                            @Override
                                                            public void onResponse(Call<Business> call, Response<Business> response) {
                                                                if (response.isSuccessful()) {
                                                                    Business business = response.body();
                                                                    Coordinate coordinate = business.getLocation().getCoordinate();
                                                                    ParseGeoPoint location = new ParseGeoPoint(coordinate.getLatitude(), coordinate.getLongitude());
                                                                    Vendor truck = new Vendor.Builder(business.getId())
                                                                            .isYelp(true).isLiked(true).setName(business.getName())
                                                                            .setAddress(DetailsFragment.addressGenerator(business).get(0))
                                                                            .setPicture(business.getImageUrl()).setRating(business.getRating())
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

                                        ParseQuery<ParseObject> favorites = ParseQuery.getQuery("Favorite");
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
        });

    }

    @Override
    public void dialogClicked(int which) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_remove_friend:
                FragmentManager manager = getSupportFragmentManager();
                RemoveDialogFragment dialog = new RemoveDialogFragment();
                Bundle argument = new Bundle();
                argument.putString(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                dialog.setArguments(argument);
                dialog.show(manager, "Remove Friend");
                break;
            case R.id.action_logout:
                logOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        LoginManager.getInstance().logOut(); // facebook logout
        ParseUser.logOut();
        Toast.makeText(getApplicationContext(), "Successfully logged out!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
