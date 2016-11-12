package c4q.nyc.take2.accessfoodnyc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Coordinate;
import com.yelp.clientlib.entities.SearchResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        DialogCallback, TouchableWrapper.UpdateMapAfterUserInterection {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LOCATION_KEY = "location-key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    public static Location mLastLocation;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;

    private RecyclerViewPager mRecyclerView;
    private VendorListAdapter mAdapter;
    public static String businessId;

    // Declare a variable for the cluster manager.
    ClusterManager<MarkerCluster> mClusterManager;

    private Toolbar mToolbar;

    private RelativeLayout mContainer;
    private RecyclerView mRecyclerViewList;
    private boolean isListed = false;
    private boolean isLocationOn;
    public HashMap<Marker, Integer> markerHashMap;
    private Integer previous;
    private boolean isMarkerClicked;
    private Button mButtonSearchThisArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MainApplication.getInstance().setupRetrofit();
        isListed = false;
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.white_orange);
        getSupportActionBar().setTitle("Access Food");

        markerHashMap = new HashMap<>();
        isMarkerClicked = false;

        if (checkLocationStatus()) {
            buildGoogleApiClient();
            createLocationRequest();
            mGoogleApiClient.connect();
        }

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeViews();
    }

    private void initializeViews() {
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mRecyclerView = (RecyclerViewPager) findViewById(R.id.recyclerView_grid);
        mRecyclerViewList = (RecyclerView) findViewById(R.id.recyclerView_list);
        mButtonSearchThisArea = (Button) findViewById(R.id.search_this_area);
        mButtonSearchThisArea.setBackgroundColor(Color.argb(125, 3, 169, 244));

        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layout);
        mRecyclerViewList.setLayoutManager(lm);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                // do something
                int childCount = mRecyclerView.getCurrentPosition();
                int position = mRecyclerView.getCurrentPosition();
                switch (scrollState) {
                    case RecyclerView.SCROLL_STATE_IDLE:

                        if (!isMarkerClicked) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(mAdapter.getItem(position).getMarker().getPosition()));
                            if (previous != null) {
                                mAdapter.getItem(previous).getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.food_truck_red));
                            }
                            mAdapter.getItem(position).getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.food_blue2));
                            previous = position;
                        }


                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                        break;

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int childCount = mRecyclerView.getChildCount();
                int width = mRecyclerView.getChildAt(0).getWidth();
                int padding = (mRecyclerView.getWidth() - width) / 2;
                //mCountText.setText("Count: " + childCount);


                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    float rate = 0;
                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * 0.1f);
                    } else {
                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                    }
                }
            }
        });
        // registering addOnLayoutChangeListener  aim to setScale at first layout action
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mRecyclerView.getChildCount() < 3) {
                    if (mRecyclerView.getChildAt(1) != null) {
                        View v1 = mRecyclerView.getChildAt(1);
                        v1.setScaleY(0.9f);
                    }
                } else {
                    if (mRecyclerView.getChildAt(0) != null) {
                        View v0 = mRecyclerView.getChildAt(0);
                        v0.setScaleY(0.9f);
                    }
                    if (mRecyclerView.getChildAt(2) != null) {
                        View v2 = mRecyclerView.getChildAt(2);
                        v2.setScaleY(0.9f);
                    }
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpListener(true);
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
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

    public void setUpListener(boolean isResumed) {
        if (isResumed) {
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            goToVendorInfoAcvitity(position);
                        }
                    })
            );
            mRecyclerViewList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            goToVendorInfoAcvitity(position);
                        }
                    })
            );
            mButtonSearchThisArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.clear();
                    searchVendors(mMap.getCameraPosition().target);
                    mButtonSearchThisArea.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUpListener(false);
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onStop() {
        try {
            mGoogleApiClient.disconnect();
        } catch (NullPointerException e) {

        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                logOut();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_list:
                if (isListed) {
                    mButtonSearchThisArea.setVisibility(View.VISIBLE);
                    mRecyclerViewList.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    isListed = false;
                } else {
                    mButtonSearchThisArea.setVisibility(View.GONE);
                    mRecyclerViewList.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    isListed = true;
                }
                break;
            case R.id.action_sort:
                if (checkLocationStatus()) {
                    sort();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng defaultLatLng = new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkLocationPermission();
    }

    private void forceUser() {
        Snackbar.make(mContainer,
                R.string.location_permission_message,
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Open Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent appSettings = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        appSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        appSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(appSettings);
                    }
                }).show();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                forceUser();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_LOCATION);
            }
        } else {
            ParseUser user = ParseUser.getCurrentUser();
            if (!mRequestingLocationUpdates) {
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    }
                };
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, locationListener);
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            final ParseGeoPoint point = new ParseGeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            user.put("location", point);
            user.saveInBackground();

            mAdapter = new VendorListAdapter(getApplicationContext(), point);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerViewList.setAdapter(mAdapter);
            searchVendors(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constants.REQUEST_LOCATION:
                    checkLocationPermission();
                    break;
            }
        } else {
            forceUser();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void searchVendors(LatLng currentLocation) {

        final ParseGeoPoint point = new ParseGeoPoint(currentLocation.latitude, currentLocation.longitude);
        final ParseUser user = ParseUser.getCurrentUser();

        mAdapter = new VendorListAdapter(getApplicationContext(), point);
        mRecyclerView.swapAdapter(mAdapter, true);
        mRecyclerViewList.swapAdapter(mAdapter, true);


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        final String today = "day" + Integer.toString(day);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
        query.whereNear("location", point).whereWithinMiles("location", point, 5.0).setLimit(50).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (final ParseObject vendor : list) {
                    if (vendor.getString("name") != null) {
                        final ParseGeoPoint vendorLocation = vendor.getParseGeoPoint("location");
                        LatLng position = new LatLng(vendorLocation.getLatitude(), vendorLocation.getLongitude());
                        final Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(vendor.getString("name")));
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.food_truck_red));
                        //markerHashMap.put(marker, vendor.getObjectId());
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
                                        ParseQuery<ParseObject> fav = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
                                        fav.whereEqualTo(Constants.PARSE_COLUMN_FOLLOWER, user).whereEqualTo(Constants.VENDOR, vendor);
                                        fav.getFirstInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(final ParseObject parseObject, ParseException e) {
                                                Vendor truck;
                                                if (parseObject == null) {
                                                    truck = new Vendor.Builder(vendor.getObjectId())
                                                            .setName(vendor.getString("name")).setAddress(vendor.getString("address"))
                                                            .isYelp(false)
                                                            .setFriends(list).setLocation(vendorLocation).setHours(json).setMarker(marker)
                                                            .setPicture(vendor.getString(Constants.PARSE_COLUMN_PROFILE)).setRating(vendor.getDouble("rating"))
                                                            .isLiked(false).build();
                                                } else {
                                                    truck = new Vendor.Builder(vendor.getObjectId())
                                                            .setName(vendor.getString("name")).setAddress(vendor.getString("address"))
                                                            .isYelp(false)
                                                            .setFriends(list).setLocation(vendorLocation).setHours(json).setMarker(marker)
                                                            .setPicture(vendor.getString(Constants.PARSE_COLUMN_PROFILE)).setRating(vendor.getDouble("rating"))
                                                            .isLiked(true).build();
                                                }
                                                mAdapter.addVendor(truck);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String postalCode = addresses.get(0).getPostalCode();

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("category_filter", "foodtrucks");
            queryMap.put("sort", "1");
            queryMap.put("limit", "20");
            MainApplication.getInstance().getYelpAPI().search(address + " " + postalCode, queryMap)
                    .enqueue(new Callback<SearchResponse>() {
                        @Override
                        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                            if (response.isSuccessful()) {
                                MainApplication.getInstance().setSearchResponse(response.body());
                                List<Business> yelpRawList = response.body().businesses();

                                final ParseUser user = ParseUser.getCurrentUser();
                                for (final Business business : yelpRawList) {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
                                    query.whereEqualTo(Constants.YELP_ID, business.id());
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(final ParseObject vendor, ParseException e) {
                                            if (vendor == null || vendor.getString("name") == null) {

                                                com.yelp.clientlib.entities.Location location = business.location();
                                                Coordinate coordinate = location.coordinate();
                                                final double latitude = coordinate.latitude();
                                                final double longitude = coordinate.longitude();
                                                // create marker
//                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(business.getName());
                                                final Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(business.name())); //...
//                MarkerCluster mc = new MarkerCluster(latitude, longitude, business.getName(),business.getId());
//                mClusterManager.addItem(mc);
                                                // Changing marker icon
                                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.food_truck_red));
                                                //markerHashMap.put(marker, business.getId());
                                                final List<String> address = DetailsFragment.addressGenerator(business);

                                                if (vendor == null) {
                                                    Vendor truck = new Vendor.Builder(business.id()).isYelp(true).isLiked(false)
                                                            .setName(business.name()).setFriends(new ArrayList<ParseObject>())
                                                            .setMarker(marker).setLocation(new ParseGeoPoint(latitude, longitude))
                                                            .setRating(business.rating()).setPicture(business.imageUrl())
                                                            .setAddress(address.get(0)).build();
                                                    mAdapter.addVendor(truck);
                                                } else {
                                                    ParseRelation<ParseUser> friends = user.getRelation("friends");
                                                    friends.getQuery().findInBackground(new FindCallback<ParseUser>() {
                                                        @Override
                                                        public void done(List<ParseUser> list, ParseException e) {

                                                            ParseQuery<ParseObject> favorites = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
                                                            favorites.include(Constants.PARSE_COLUMN_FOLLOWER);
                                                            favorites.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn(Constants.PARSE_COLUMN_FOLLOWER, list);
                                                            favorites.findInBackground(new FindCallback<ParseObject>() {
                                                                @Override
                                                                public void done(List<ParseObject> list, ParseException e) {
                                                                    Vendor truck = new Vendor.Builder(business.id()).isYelp(true).isLiked(false)
                                                                            .setName(business.name()).setFriends(new ArrayList<ParseObject>())
                                                                            .setMarker(marker).setLocation(new ParseGeoPoint(latitude, longitude))
                                                                            .setRating(business.rating()).setPicture(business.imageUrl())
                                                                            .setAddress(address.get(0)).build();
                                                                    mAdapter.addVendor(truck);
                                                                }
                                                            });

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                                }

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {

                                        isMarkerClicked = true;
                                        int position = mAdapter.getPosition(marker);
                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.food_blue));
                                        marker.showInfoWindow();
                                        if (previous != null) {
                                            mAdapter.getItem(previous).getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.food_truck_red));
                                        }
                                        previous = position;
                                        mRecyclerView.smoothScrollToPosition(position);
                                        isMarkerClicked = false;
                                        return true;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchResponse> call, Throwable t) {

                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mCurrentLocation);
        outState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(outState);
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

    private void sort() {
        new SortDialogFragment().show(getSupportFragmentManager(), "");
    }

    @Override
    public void dialogClicked(int which) {
        switch (which) {
            case 0:
                if (mAdapter != null) {
                    mAdapter.sortByDistance();
                }
                break;
            case 1:
                if (mAdapter != null) {
                    mAdapter.sortByRating();
                }
                break;
        }
    }

    @Override
    public void onUpdateMapAfterUserInterection() {
        mButtonSearchThisArea.setVisibility(View.VISIBLE);
    }

    public boolean checkLocationStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isLocationOn = false;

        try {
            isLocationOn = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }

        if (!isLocationOn) {
            LocationDialogFragment dialog = new LocationDialogFragment();
            dialog.show(getSupportFragmentManager(), "Location");
            return false;
        } else {
            return true;
        }
    }
}