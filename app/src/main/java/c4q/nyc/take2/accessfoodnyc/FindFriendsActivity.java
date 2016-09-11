package c4q.nyc.take2.accessfoodnyc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FindFriendsAdapter mAdapter;
    private List<Friend> mList;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_find_friends);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_find_friends);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);

        String fbId = null;
        try {
            fbId = ParseUser.getCurrentUser().getString("fbId");
        } catch (NullPointerException e) {

        }
        if (fbId != null) {
            mList = new ArrayList<>();
            AccessToken currentToken = AccessToken.getCurrentAccessToken();

            GraphRequest request = GraphRequest.newMyFriendsRequest(currentToken, new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject friend = jsonArray.getJSONObject(i);
                            String id = friend.getString("id");
                            String name = friend.getString("name");
                            String profile_url = "";
                            try {
                                JSONObject cover = friend.getJSONObject("cover");
                                profile_url = cover.getString("source");
                            } catch (JSONException e) {

                            }
                            Friend myFriend = new Friend(id, name, profile_url);
                            mList.add(myFriend);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    final ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseUser> relation = user.getRelation("friends");
                    relation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(final List<ParseUser> list, ParseException e) {

                            ParseRelation<ParseUser> relation1 = user.getRelation("pending_friends");
                            relation1.getQuery().findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> list2, ParseException e) {
                                    mAdapter = new FindFriendsAdapter(getApplicationContext(), mList, list, list2);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });


                        }
                    });


                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,cover");
            parameters.putString("limit", "50");
            //parameters.putString("edges", "mutualfriends");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            Toast.makeText(getApplicationContext(), "Can't find friends. Please connect your account with facebook", Toast.LENGTH_SHORT).show();
        }



    }
}
