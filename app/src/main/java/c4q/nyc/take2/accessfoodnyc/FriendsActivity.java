package c4q.nyc.take2.accessfoodnyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private FriendsAdapter mAdapter;

    private LinearLayout mPendingParent;
    private LinearLayout mPendingContainer;
    private TextView mTextViewFriendsNumber;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ParseUser user = ParseUser.getCurrentUser();

        mPendingParent = (LinearLayout) findViewById(R.id.pending_parent);
        mPendingContainer = (LinearLayout) findViewById(R.id.pending_container);
        mTextViewFriendsNumber = (TextView) findViewById(R.id.textView_friends);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_friends);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME));


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_friends);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);

        ParseRelation<ParseUser> pending = user.getRelation("friend_requests");
        pending.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (list.size() == 0) {
                    mPendingParent.setVisibility(View.GONE);
                } else {
                    for (final ParseUser pending : list) {
                        View row = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item_pending_friends, mPendingContainer, false);
                        ImageView picture = (ImageView) row.findViewById(R.id.imageView_pending_friend);
                        TextView name = (TextView) row.findViewById(R.id.name_pending_friend);
                        Button accept = (Button) row.findViewById(R.id.button_pending_accept);
                        Button decline = (Button) row.findViewById(R.id.button_pending_decline);

                        final String friendName = pending.getString(Constants.FIRST_NAME) + " " + pending.getString(Constants.LAST_NAME);
                        Picasso.with(getApplicationContext()).load(pending.getString(Constants.PARSE_COLUMN_PROFILE)).into(picture);
                        name.setText(friendName);
                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ParseUser me = ParseUser.getCurrentUser();
                                ParseRelation<ParseUser> friends = me.getRelation("friends");
                                ParseRelation<ParseUser> pendings = me.getRelation("friend_requests");
                                pendings.remove(pending);
                                me.saveInBackground();
                                friends.add(pending);
                                me.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(getApplicationContext(), friendName + " is now your friend!", Toast.LENGTH_SHORT).show();

                                        Intent acceptIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                        acceptIntent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, pending.getObjectId());
                                        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(acceptIntent);
                                        finish();
                                    }
                                });

                            }
                        });
                        decline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        mPendingContainer.addView(row);
                    }
                }
            }
        });

        ParseRelation<ParseUser> relation = user.getRelation("friends");
        relation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                mAdapter = new FriendsAdapter(getApplicationContext(), list);
                mRecyclerView.setAdapter(mAdapter);
                mTextViewFriendsNumber.append(" (" + list.size() + ")");
                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String objectId = mAdapter.getItem(position).getObjectId();
                        Intent intent = new Intent(getApplicationContext(), FriendProfileActivity.class);
                        intent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                        startActivity(intent);
                    }
                }));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecyclerView.setOnClickListener(null);
    }
}
