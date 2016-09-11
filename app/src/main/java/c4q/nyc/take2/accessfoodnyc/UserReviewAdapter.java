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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.UserReviewHolder> {

    private Context mContext;
    private List<Review> mList;

    public UserReviewAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public UserReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_user_review, parent, false);
        return new UserReviewHolder(row);
    }

    @Override
    public void onBindViewHolder(UserReviewHolder holder, int position) {

        DecimalFormat df = new DecimalFormat("#0.0");
        Review review = getItem(position);
        Vendor vendor = review.getVendor();

        holder.vendorName.setText(vendor.getName());
        holder.address.setText(vendor.getAddress());
        Picasso.with(mContext).load(vendor.getPicture()).into(holder.vendorPicture);

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



        String hour = vendor.getHours();
        if (!hour.equals("closed")) {
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
        } else {
            holder.hour.setText("Closed today");
        }

        if (vendor.isYelp()) {
            holder.hour.setVisibility(View.GONE);
            holder.yelpLogo.setVisibility(View.VISIBLE);
        } else {
            holder.hour.setVisibility(View.VISIBLE);
            holder.yelpLogo.setVisibility(View.GONE);
        }


        holder.title.setText(review.getTitle());
        holder.description.setText(review.getDescription());

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String date = dateFormat.format(review.getDate());
        holder.date.setText(date);

        ParseUser user = ParseUser.getCurrentUser();
        String profile = user.getString(Constants.PARSE_COLUMN_PROFILE);
        if (profile.length() == 0 || profile.isEmpty()) {
            Picasso.with(mContext).load(R.drawable.default_profile).into(holder.writerPicture);
        } else {
            Picasso.with(mContext).load(profile).into(holder.writerPicture);
        }

        holder.howmany.setText(user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME) + " gave");

        holder.grade1.setVisibility(View.GONE);
        holder.grade2.setVisibility(View.GONE);
        holder.grade3.setVisibility(View.GONE);
        holder.grade4.setVisibility(View.GONE);
        holder.grade5.setVisibility(View.GONE);

        switch(review.getRating()) {
            case 5:
                holder.grade5.setVisibility(View.VISIBLE);
            case 4:
                holder.grade4.setVisibility(View.VISIBLE);
            case 3:
                holder.grade3.setVisibility(View.VISIBLE);
            case 2:
                holder.grade2.setVisibility(View.VISIBLE);
            case 1:
                holder.grade1.setVisibility(View.VISIBLE);
        }


    }

    public void addReview(Review review) {
        mList.add(review);
        notifyItemInserted(mList.size() - 1);
        notifyItemChanged(mList.size() - 1);
    }

    public Review getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class UserReviewHolder extends RecyclerView.ViewHolder {

        protected ImageView vendorPicture;
        protected TextView vendorName;
        protected TextView address;
        protected TextView rating;
        protected TextView hour;
        protected TextView userRatings;
        protected ImageView writerPicture;
        protected TextView date;
        protected TextView title;
        protected TextView description;
        protected ImageView yelpLogo;
        protected TextView howmany;
        protected ImageView grade1;
        protected ImageView grade2;
        protected ImageView grade3;
        protected ImageView grade4;
        protected ImageView grade5;

        public UserReviewHolder(View itemView) {
            super(itemView);

            userRatings = (TextView) itemView.findViewById(R.id.user_review_how_many);
            date = (TextView) itemView.findViewById(R.id.user_review_date);
            title = (TextView) itemView.findViewById(R.id.user_review_title);
            description = (TextView) itemView.findViewById(R.id.user_review_description);
            writerPicture = (ImageView) itemView.findViewById(R.id.user_review_user_picture);
            vendorName = (TextView) itemView.findViewById(R.id.vendor_name);
            address = (TextView) itemView.findViewById(R.id.textView_address);
            rating = (TextView) itemView.findViewById(R.id.vendor_rating);
            hour = (TextView) itemView.findViewById(R.id.textView_hour);
            vendorPicture = (ImageView) itemView.findViewById(R.id.imageView_vendor);
            yelpLogo = (ImageView) itemView.findViewById(R.id.yelp_logo);

            howmany = (TextView) itemView.findViewById(R.id.user_review_how_many);
            grade1 = (ImageView) itemView.findViewById(R.id.grade_1);
            grade2 = (ImageView) itemView.findViewById(R.id.grade_2);
            grade3 = (ImageView) itemView.findViewById(R.id.grade_3);
            grade4 = (ImageView) itemView.findViewById(R.id.grade_4);
            grade5 = (ImageView) itemView.findViewById(R.id.grade_5);
        }
    }
}
