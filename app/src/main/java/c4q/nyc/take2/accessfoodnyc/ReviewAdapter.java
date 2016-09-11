package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private Context mContext;
    private List<ParseObject> mList;

    public ReviewAdapter(Context context, List<ParseObject> list) {
        mContext = context;
        mList = list;


    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_review, parent, false);
        return new ReviewHolder(row);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        ParseObject review = mList.get(position);

        ParseUser user = review.getParseUser("writer");
        String profile = user.getString(Constants.PARSE_COLUMN_PROFILE);
        if (profile.length() == 0 || profile.isEmpty()) {
            Picasso.with(mContext).load(R.drawable.default_profile).into(holder.picture);
        } else {
            Picasso.with(mContext).load(profile).into(holder.picture);
        }


        holder.name.setText(user.getString(Constants.FIRST_NAME) + " " + user.getString(Constants.LAST_NAME));

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String dates = dateFormat.format(review.getCreatedAt());
        holder.date.setText(dates);

        holder.title.setText(review.getString("title"));
        holder.description.setText(review.getString("description"));

        int rating = review.getInt("rating");
        switch (rating) {
            case 1:
                holder.grade2.setVisibility(View.GONE);
            case 2:
                holder.grade3.setVisibility(View.GONE);
            case 3:
                holder.grade4.setVisibility(View.GONE);
            case 4:
                holder.grade5.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }




    public static class ReviewHolder extends RecyclerView.ViewHolder {

        protected ImageView picture;
        protected TextView name;
        protected TextView date;
        protected TextView title;
        protected TextView description;


        protected ImageView grade1;
        protected ImageView grade2;
        protected ImageView grade3;
        protected ImageView grade4;
        protected ImageView grade5;

        public ReviewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.imageView_review_profile);
            grade1 = (ImageView) itemView.findViewById(R.id.grade_1);
            grade2 = (ImageView) itemView.findViewById(R.id.grade_2);
            grade3 = (ImageView) itemView.findViewById(R.id.grade_3);
            grade4 = (ImageView) itemView.findViewById(R.id.grade_4);
            grade5 = (ImageView) itemView.findViewById(R.id.grade_5);
            name = (TextView) itemView.findViewById(R.id.textView_review_name);
            date = (TextView) itemView.findViewById(R.id.textView_review_date);
            title = (TextView) itemView.findViewById(R.id.textView_review_title);
            description = (TextView) itemView.findViewById(R.id.textView_review_description);

        }
    }
}
