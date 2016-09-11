package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoritedFriendsAdapter extends RecyclerView.Adapter<FavoritedFriendsAdapter.FriendsHolder> {

    private Context mContext;
    private List<ParseUser> mList;

    public FavoritedFriendsAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void addFriend(ParseUser friend) {
        mList.add(friend);
        notifyItemInserted(mList.size() - 1);
        notifyItemChanged(mList.size() - 1);
    }

    @Override
    public FriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_details_friend_fav, parent, false);
        return new FriendsHolder(row);
    }

    @Override
    public void onBindViewHolder(FriendsHolder holder, int position) {
        ParseUser friend = mList.get(position);
        holder.name.setText(friend.getString(Constants.FIRST_NAME) + " " + friend.getString(Constants.LAST_NAME));
        Picasso.with(mContext).load(friend.getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class FriendsHolder extends RecyclerView.ViewHolder {
        protected ImageView picture;
        protected TextView name;

        public FriendsHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.details_friend_fav);
            name = (TextView) itemView.findViewById(R.id.textView_details_friend_name);
        }
    }


}
