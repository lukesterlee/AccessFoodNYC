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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CouponsActivity extends AppCompatActivity implements DialogCallback {

    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private CouponsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_coupons);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_coupons);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);

        refresh();
    }

    private void refresh() {
        ParseUser user = ParseUser.getCurrentUser();
        final Date today = Calendar.getInstance().getTime();
        ParseQuery<ParseObject> coupons = ParseQuery.getQuery(Constants.PARSE_CLASS_COUPON);
        coupons.include(Constants.VENDOR);
        coupons.whereEqualTo("customer", user).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() != 0) {
                    List<ParseObject> allCoupons = new ArrayList<ParseObject>();
                    for (ParseObject coupon : list) {
                        Date expiration = coupon.getDate("expiration");
                        if (expiration.before(today)) {
                            coupon.deleteInBackground();
                        } else {
                            allCoupons.add(coupon);
                        }
                    }
                    mAdapter = new CouponsAdapter(getApplicationContext(), allCoupons);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String objectId = mAdapter.getItem(position).getObjectId();
                        Bundle argument = new Bundle();
                        argument.putString(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                        RedeemDialogFragment dialog = new RedeemDialogFragment();
                        dialog.setArguments(argument);
                        dialog.show(getSupportFragmentManager(), "Redeem");
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coupons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_logout:
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

    @Override
    public void dialogClicked(int which) {
        refresh();
    }
}
