package c4q.nyc.take2.accessfoodnyc;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import c4q.nyc.take2.accessfoodnyc.api.yelp.models.Business;
import c4q.nyc.take2.accessfoodnyc.api.yelp.service.ServiceGenerator;
import c4q.nyc.take2.accessfoodnyc.api.yelp.service.YelpBusinessSearchService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class VendorInfoActivity extends AppCompatActivity implements ActionBar.TabListener {

    // Tab titles
    private static final String[] TABS = { "Details", "Twitter", "Reviews" };

    private DetailsFragment mCurrentDetailsFragment;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private boolean isYelp;
    private String objectId;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isYelp = getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_YELP, false);
        objectId = getIntent().getStringExtra(Constants.EXTRA_KEY_OBJECT_ID);

        setContentView(R.layout.activity_vendor_info);



        mToolbar = (Toolbar) findViewById(R.id.toolbar_vendor);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (isYelp) {
            YelpBusinessSearchService yelpBizService = ServiceGenerator.createYelpBusinessSearchService();
            yelpBizService.searchBusiness(objectId, new YelpBusinessSearchCallback());
        } else {
            setupViewPager(mViewPager);
            mViewPager.setOffscreenPageLimit(3);
            mTabLayout.setupWithViewPager(mViewPager);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    mToolbar.setTitle(parseObject.getString("name"));
                }
            });
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Twitter";
                case 2:
                    return "Reviews";
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle bundle = new Bundle();
            bundle.putString(Constants.EXTRA_KEY_OBJECT_ID, objectId);
            bundle.putBoolean(Constants.EXTRA_KEY_IS_YELP, isYelp);
            switch (position) {
                case 0:
                    mCurrentDetailsFragment = new DetailsFragment();
                    mCurrentDetailsFragment.setArguments(bundle);
                    return mCurrentDetailsFragment;
                case 1:
                    fragment = new TwitterFragment();
                    fragment.setArguments(bundle);
                    return fragment;
                case 2:
                    fragment = new ReviewsFragment();
                    fragment.setArguments(bundle);
                    return fragment;
            }
            return null;
        }
    }

    protected class YelpBusinessSearchCallback implements Callback<Business> {

        public String TAG = "YelpBusinessSearchCallback";

        @Override
        public void success(Business business, Response response) {
            Log.d(TAG, "Success");

            if (business != null) {

                setupViewPager(mViewPager);
                mViewPager.setOffscreenPageLimit(3);
                mTabLayout.setupWithViewPager(mViewPager);
                mToolbar.setTitle(business.getName());
//                mCurrentDetailsFragment.onYelpData(business);

                if (mCurrentDetailsFragment != null && isYelp) {

                    mCurrentDetailsFragment.onYelpData(business);
                } else {
                    Log.d("YelpDataGenerator", "mCurrentDetailsFragment was null!!!!");
                }
            }
        }
        @Override
        public void failure(RetrofitError error) {
            Log.e(TAG, error.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendor_info, menu);
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
