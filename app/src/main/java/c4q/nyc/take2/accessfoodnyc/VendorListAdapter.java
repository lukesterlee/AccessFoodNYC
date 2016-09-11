package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.VendorViewHolder> {

    private Context mContext;
    private List<Vendor> mList;
    private ParseGeoPoint mPoint;


    public VendorListAdapter(Context context, ParseGeoPoint point) {
        mContext = context;
        mList = new ArrayList<>();
        mPoint = point;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addVendor(Vendor vendor) {
        mList.add(vendor);
        notifyItemInserted(mList.size() - 1);
        notifyItemChanged(mList.size() - 1);
    }

    public int getPosition(Marker marker) {
        for (int i = 0; i < mList.size(); i++) {
            Vendor vendor = mList.get(i);
            if (vendor.getMarker().equals(marker))
                return i;
        }
        return -1;
    }

    public Vendor getItem(int position) {
        return mList.get(position);
    }

    public void sortByDistance() {
        Collections.sort(mList, new Comparator<Vendor>() {
            @Override
            public int compare(Vendor lhs, Vendor rhs) {
                double distanceLhs = mPoint.distanceInMilesTo(lhs.getLocation());
                double distanceRhs = mPoint.distanceInMilesTo(rhs.getLocation());
                return Double.compare(distanceLhs, distanceRhs);
            }
        });
        notifyDataSetChanged();
    }

    public void sortByRating() {
        Collections.sort(mList, new Comparator<Vendor>() {
            @Override
            public int compare(Vendor lhs, Vendor rhs) {
                return Double.compare(rhs.getRating(), lhs.getRating());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public VendorViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_vendor, parent, false);
        return new VendorViewHolder(row);
    }


    @Override
    public void onBindViewHolder(final VendorViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#0.0");

        final Vendor vendor = getItem(position);

        holder.name.setText(vendor.getName());
        holder.address.setText(vendor.getAddress());

        if (vendor.isLiked()) {
            holder.like.setVisibility(View.VISIBLE);
            holder.like.setImageResource(R.drawable.ic_favorite_white_24dp);
            int color = Color.parseColor("#ff0000");
            holder.like.setColorFilter(color);
        } else {
            holder.like.setVisibility(View.GONE);
        }

        double rate = vendor.getRating();
        String rating = df.format(rate);
        holder.rating.setText(rating);
        if (rate >= 4.5) {
            holder.rating.setBackgroundResource(R.drawable.round_5);
        } else if (rate >= 4.0) {
            holder.rating.setBackgroundResource(R.drawable.round_4);
        } else if (rate >= 3.5) {
            holder.rating.setBackgroundResource(R.drawable.round_3);
        } else if (rate >= 3.0) {
            holder.rating.setBackgroundResource(R.drawable.round_2);
        } else {
            holder.rating.setBackgroundResource(R.drawable.round_1);
        }

        ParseGeoPoint location = vendor.getLocation();
        double distance = mPoint.distanceInMilesTo(location);
        holder.distance.setText(df.format(distance) + " miles away");

        Picasso.with(mContext).load(vendor.getPicture()).into(holder.thumbnail);

        String hour = vendor.getHours();
        if (!hour.equals("closed") && hour.length() != 0) {
            try {
                JSONObject info = new JSONObject(hour);
                String opening = info.getString("openAt");
                String closing = info.getString("closeAt");
                long current = System.currentTimeMillis();
                Date open = new Date(current);
                Date close = new Date(current);
                Date now = new Date(current);
                open.setHours(Integer.valueOf(opening.substring(0,2)));
                open.setMinutes(Integer.valueOf(opening.substring(2)));
                close.setHours(Integer.valueOf(closing.substring(0,2)));
                close.setMinutes(Integer.valueOf(closing.substring(2)));
                String minutes = close.getMinutes() + "";
                if (now.after(open) && now.before(close)) {
                    if (close.getMinutes() == 0) {
                        minutes = "00";
                    }
                    if (close.getHours() > 12) {
                        holder.hour.setText("Open until " + (close.getHours() - 12) + ":" + minutes + " PM");
                    } else {
                        holder.hour.setText("Open until " + close.getHours() + ":" + minutes + " AM");
                    }
                } else {
                    holder.hour.setText("Closed now");
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } else if (hour.equals("closed")) {
            holder.hour.setText("Closed today");
        } else if (hour.length() == 0) {
            holder.icon.setVisibility(View.GONE);
            holder.hour.setVisibility(View.GONE);
        }

        if (vendor.isYelp()) {
            holder.icon.setVisibility(View.GONE);
            holder.hour.setVisibility(View.GONE);
            holder.yelpLogo.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
            holder.hour.setVisibility(View.VISIBLE);
            holder.yelpLogo.setVisibility(View.GONE);
        }


        int size = vendor.getFriends().size();
        if (size == 0) {
            holder.friend5.setVisibility(View.GONE);
            holder.friend4.setVisibility(View.GONE);
            holder.friend3.setVisibility(View.GONE);
            holder.friend2.setVisibility(View.GONE);
            holder.friend1.setVisibility(View.GONE);
            holder.more.setVisibility(View.GONE);
        } else if (size == 1) {
            holder.friend5.setVisibility(View.GONE);
            holder.friend4.setVisibility(View.GONE);
            holder.friend3.setVisibility(View.GONE);
            holder.friend2.setVisibility(View.GONE);
            holder.friend1.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.GONE);
            Picasso.with(mContext).load(vendor.getFriends().get(0).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend1);
        } else if (size == 2) {
            holder.friend5.setVisibility(View.GONE);
            holder.friend4.setVisibility(View.GONE);
            holder.friend3.setVisibility(View.GONE);
            holder.friend2.setVisibility(View.VISIBLE);
            holder.friend1.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.GONE);
            Picasso.with(mContext).load(vendor.getFriends().get(0).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend1);
            Picasso.with(mContext).load(vendor.getFriends().get(1).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend2);
        } else if (size == 3) {
            holder.friend5.setVisibility(View.GONE);
            holder.friend4.setVisibility(View.GONE);
            holder.friend3.setVisibility(View.VISIBLE);
            holder.friend2.setVisibility(View.VISIBLE);
            holder.friend1.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.GONE);
            Picasso.with(mContext).load(vendor.getFriends().get(0).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend1);
            Picasso.with(mContext).load(vendor.getFriends().get(1).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend2);
            Picasso.with(mContext).load(vendor.getFriends().get(2).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend3);
        } else if (size == 4) {
            holder.friend5.setVisibility(View.GONE);
            holder.friend4.setVisibility(View.VISIBLE);
            holder.friend3.setVisibility(View.VISIBLE);
            holder.friend2.setVisibility(View.VISIBLE);
            holder.friend1.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.GONE);
            Picasso.with(mContext).load(vendor.getFriends().get(0).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend1);
            Picasso.with(mContext).load(vendor.getFriends().get(1).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend2);
            Picasso.with(mContext).load(vendor.getFriends().get(2).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend3);
            Picasso.with(mContext).load(vendor.getFriends().get(3).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend4);
        } else if (size == 5) {
            holder.friend5.setVisibility(View.VISIBLE);
            holder.friend4.setVisibility(View.VISIBLE);
            holder.friend3.setVisibility(View.VISIBLE);
            holder.friend2.setVisibility(View.VISIBLE);
            holder.friend1.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.GONE);
            Picasso.with(mContext).load(vendor.getFriends().get(0).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend1);
            Picasso.with(mContext).load(vendor.getFriends().get(1).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend2);
            Picasso.with(mContext).load(vendor.getFriends().get(2).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend3);
            Picasso.with(mContext).load(vendor.getFriends().get(3).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend4);
            Picasso.with(mContext).load(vendor.getFriends().get(4).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend5);
        } else {
            holder.friend5.setVisibility(View.VISIBLE);
            holder.friend4.setVisibility(View.VISIBLE);
            holder.friend3.setVisibility(View.VISIBLE);
            holder.friend2.setVisibility(View.VISIBLE);
            holder.friend1.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(vendor.getFriends().get(0).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend1);
            Picasso.with(mContext).load(vendor.getFriends().get(1).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend2);
            Picasso.with(mContext).load(vendor.getFriends().get(2).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend3);
            Picasso.with(mContext).load(vendor.getFriends().get(3).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend4);
            Picasso.with(mContext).load(vendor.getFriends().get(4).getParseUser(Constants.PARSE_COLUMN_FOLLOWER).getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.friend5);
            holder.more.setText("+" + (size-5));
        }


    }

    public static class VendorViewHolder extends RecyclerView.ViewHolder {

        protected ImageView thumbnail;
        protected TextView name;
        protected TextView address;
        protected ImageView yelpLogo;
        protected TextView hour;
        protected TextView rating;
        protected ImageView icon;
        protected TextView distance;

        protected ImageView friend1;
        protected ImageView friend2;
        protected ImageView friend3;
        protected ImageView friend4;
        protected ImageView friend5;
        protected TextView more;
        protected ImageButton like;

        public VendorViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.imageView_vendor);
            name = (TextView) itemView.findViewById(R.id.vendor_name);
            address = (TextView) itemView.findViewById(R.id.textView_address);
            yelpLogo = (ImageView) itemView.findViewById(R.id.yelp_logo);
            hour = (TextView) itemView.findViewById(R.id.textView_hour);
            rating = (TextView) itemView.findViewById(R.id.maps_vendor_rating);
            icon = (ImageView) itemView.findViewById(R.id.schedule_icon);
            distance = (TextView) itemView.findViewById(R.id.maps_distance);
            like = (ImageButton) itemView.findViewById(R.id.vendor_like);

            friend1 = (ImageView) itemView.findViewById(R.id.friend1);
            friend2 = (ImageView) itemView.findViewById(R.id.friend2);
            friend3 = (ImageView) itemView.findViewById(R.id.friend3);
            friend4 = (ImageView) itemView.findViewById(R.id.friend4);
            friend5 = (ImageView) itemView.findViewById(R.id.friend5);
            more = (TextView) itemView.findViewById(R.id.friend_more);
        }

    }


    private void addToLikedList(ParseObject vendor) {
        ParseUser user = ParseUser.getCurrentUser();
        ParsePush.subscribeInBackground(vendor.getObjectId());
        ParseObject favorite = new ParseObject(Constants.PARSE_CLASS_FAVORITE);
        favorite.put(Constants.PARSE_COLUMN_FOLLOWER, user);
        favorite.put(Constants.VENDOR, vendor);
        favorite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(mContext, "Added to your liked list!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromLikedList(ParseObject vendor) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> find = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
        find.whereEqualTo(Constants.VENDOR, vendor).whereEqualTo(Constants.PARSE_COLUMN_FOLLOWER, user);
        find.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject favorite, ParseException e) {
                favorite.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(mContext, "Removed from your liked list!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
