package c4q.nyc.take2.accessfoodnyc;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoScrollAdapter<T> {

    private Context mContext;
    private LayoutInflater mInflater;
    private LinearLayout mParentLayout;
    private List<T> mList;

    public NoScrollAdapter(Context mContext, LinearLayout parentLayout) {
        this.mContext = mContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mParentLayout = parentLayout;

    }

    public void addReviews(List<T> list) {
        mList = list;

        for (int i = 0; i < mList.size(); i++) {
            final View row = mInflater.inflate(R.layout.list_item_review, null);

            ParseObject review = (ParseObject) mList.get(i);
            ParseUser writer = review.getParseUser("writer");

            ImageView picture = (ImageView) row.findViewById(R.id.imageView_review_profile);
            TextView title = (TextView) row.findViewById(R.id.textView_review_title);
            TextView description = (TextView) row.findViewById(R.id.textView_review_description);
            TextView reviewer = (TextView) row.findViewById(R.id.textView_review_name);
            TextView date = (TextView) row.findViewById(R.id.textView_review_date);

            ImageView grade1 = (ImageView) row.findViewById(R.id.grade_1);
            ImageView grade2 = (ImageView) row.findViewById(R.id.grade_2);
            ImageView grade3 = (ImageView) row.findViewById(R.id.grade_3);
            ImageView grade4 = (ImageView) row.findViewById(R.id.grade_4);
            ImageView grade5 = (ImageView) row.findViewById(R.id.grade_5);

            grade1.setVisibility(View.GONE);
            grade2.setVisibility(View.GONE);
            grade3.setVisibility(View.GONE);
            grade4.setVisibility(View.GONE);
            grade5.setVisibility(View.GONE);

            try {
                Picasso.with(mContext).load(writer.getString(Constants.PARSE_COLUMN_PROFILE)).into(picture);
            } catch (Exception e) {
                Picasso.with(mContext).load(R.drawable.default_profile).into(picture);
            }

            reviewer.setText(writer.getString(Constants.FIRST_NAME) + " " + writer.getString(Constants.LAST_NAME));

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String dates = dateFormat.format(review.getCreatedAt());
            date.setText(dates);

            title.setText(review.getString("title"));
            description.setText(review.getString("description"));

            int rating = review.getInt("rating");
            switch (rating) {
                case 5:
                    grade5.setVisibility(View.VISIBLE);
                case 4:
                    grade4.setVisibility(View.VISIBLE);
                case 3:
                    grade3.setVisibility(View.VISIBLE);
                case 2:
                    grade2.setVisibility(View.VISIBLE);
                case 1:
                    grade1.setVisibility(View.VISIBLE);
            }

            mParentLayout.addView(row, i);
        }
    }

    public void addFavoritedFriends(List<T> list) {
        mList = list;

        for (int i = 0; i < mList.size(); i++) {
            final int position = i;
            final View row = mInflater.inflate(R.layout.list_item_details_friend_fav, null);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String objectId = ((ParseObject) mList.get(position)).getObjectId();
                    Intent intent = new Intent(mContext, FriendProfileActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                    mContext.startActivity(intent);
                }
            });

            ParseUser friend = (ParseUser) mList.get(i);

            ImageView picture = (ImageView) row.findViewById(R.id.details_friend_fav);
            TextView name = (TextView) row.findViewById(R.id.textView_details_friend_name);

            name.setText(friend.getString(Constants.FIRST_NAME) + " " + friend.getString(Constants.LAST_NAME));

            try {
                Picasso.with(mContext).load(friend.getString(Constants.PARSE_COLUMN_PROFILE)).into(picture);
            } catch (NullPointerException e) {
                Picasso.with(mContext).load(R.drawable.default_profile).into(picture);
            }

            mParentLayout.addView(row, i);
        }

    }
}
