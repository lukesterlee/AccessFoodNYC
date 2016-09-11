package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.FindFriendsViewHolder> {

    private Context mContext;
    private List<Friend> mList;
    private List<ParseUser> mCurrentPendingList;
    private List<Boolean> mPendingCheckList;

    public FindFriendsAdapter(Context mContext, List<Friend> friendslist, List<ParseUser> list, List<ParseUser> pendingList) {
        this.mContext = mContext;
        mCurrentPendingList = pendingList;

        boolean isEqual = false;
        mList = new ArrayList<>();
        for (Friend friend : friendslist) {
            for (ParseUser user : list) {
                if (friend.getId().equals(user.get("fbId"))) {
                    isEqual = true;
                    break;
                }
            }
            if (!isEqual) {
                mList.add(friend);
            }
            isEqual = false;
        }

        boolean isPending = false;
        mPendingCheckList = new ArrayList<>();
        for (Friend friend : mList) {
            for (ParseUser pendingUser : mCurrentPendingList) {
                if (friend.getId().equals(pendingUser.get("fbId"))) {
                    isPending = true;
                    break;
                }
            }
            if (isPending) {
                mPendingCheckList.add(true);
            } else {
                mPendingCheckList.add(false);
            }
            isPending = false;
        }
    }

    @Override
    public FindFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_find_friends, parent, false);
        return new FindFriendsViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final FindFriendsViewHolder holder, int position) {

        final Friend friend = mList.get(position);

        if (friend.getThumbnailUrl().length() == 0) {
            Picasso.with(mContext).load(R.drawable.default_profile).into(holder.thumbnail);
        } else {
            Picasso.with(mContext).load(friend.getThumbnailUrl()).into(holder.thumbnail);
        }

        holder.name.setText(friend.getName());


        // TODO : check if this friend is in pending list.
        if (mPendingCheckList.get(position)) {
            holder.request.setText("Sent");
            holder.request.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_white_24dp, 0, 0, 0);
            holder.request.setOnClickListener(null);
        } else {
            holder.request.setText("Add");
            holder.request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ParseUser user = ParseUser.getCurrentUser();
                    String name = user.get(Constants.FIRST_NAME) + " " + user.get(Constants.LAST_NAME);
                    try {
                        JSONObject data = new JSONObject("{\"alert\": \"" + name + " wants to be your friend!" + "\"," +
                                "\"profile_url\": \"" + friend.getThumbnailUrl() + "\"," +
                                "\"objectId\": \"" + user.getObjectId() + "\"," +
                                "\"title\": \"Friend Request\"}");
                        Toast.makeText(mContext, "Friend request is sent to " + friend.getName(), Toast.LENGTH_SHORT).show();
                        holder.request.setText("sent");
                        holder.request.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_white_24dp, 0, 0, 0);

                        ParseQuery query = ParseInstallation.getQuery();
                        query.whereEqualTo("fbId", friend.getId());
                        ParsePush push = new ParsePush();
                        push.setQuery(query);
                        push.setData(data);
                        push.sendInBackground();

                        // after request, add to the pending list.
                        ParseQuery<ParseUser> query1 = ParseQuery.getQuery("_User");
                        query1.whereEqualTo("fbId", friend.getId());
                        query1.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                ParseRelation<ParseUser> relation = user.getRelation("pending_friends");
                                relation.add(list.get(0));
                                user.saveInBackground();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView name;
        private Button request;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.imageView_friend);
            name = (TextView) itemView.findViewById(R.id.name_friend);
            request = (Button) itemView.findViewById(R.id.button_request);
        }
    }
}
