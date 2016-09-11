package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PendingFriendsAdapter extends RecyclerView.Adapter<PendingFriendsAdapter.PendingFriendsHolder> {

    private Context mContext;
    private List<ParseUser> mList;


    public PendingFriendsAdapter(Context context, List<ParseUser> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public PendingFriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_pending_friends, parent, false);
        return new PendingFriendsHolder(row);
    }

    @Override
    public void onBindViewHolder(PendingFriendsHolder holder, int position) {
        final ParseUser user = mList.get(position);

        final String friendName = user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME);
        holder.name.setText(friendName);
        Picasso.with(mContext).load(user.getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.picture);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser me = ParseUser.getCurrentUser();
                ParseRelation<ParseUser> friends = me.getRelation("friends");
                friends.add(user);
                me.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(mContext, friendName + " is now your friend!", Toast.LENGTH_SHORT).show();

                        Intent acceptIntent = new Intent(mContext, ProfileActivity.class);
                        acceptIntent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, user.getObjectId());
                        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(acceptIntent);

                    }
                });

                ParseRelation<ParseUser> pending = me.getRelation("friend_requests");
                pending.remove(user);
                me.saveInBackground();

            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class PendingFriendsHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected Button accept;
        protected Button decline;
        protected ImageView picture;


        public PendingFriendsHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name_pending_friend);
            accept = (Button) itemView.findViewById(R.id.button_pending_accept);
            decline = (Button) itemView.findViewById(R.id.button_pending_decline);
            picture = (ImageView) itemView.findViewById(R.id.imageView_pending_friend);
        }
    }
}
