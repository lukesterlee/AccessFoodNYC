package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.CouponViewHolder> {

    private Context mContext;
    private List<ParseObject> mList;

    public CouponsAdapter(Context context, List<ParseObject> list) {
        mContext = context;
        mList = list;
        Collections.sort(mList, new Comparator<ParseObject>() {
            @Override
            public int compare(ParseObject lhs, ParseObject rhs) {
                return lhs.getDate("expiration").compareTo(rhs.getDate("expiration"));
            }
        });
    }

    public ParseObject getItem(int position) {
        return mList.get(position);
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_coupon, parent, false);
        return new CouponViewHolder(row);
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        ParseObject coupon = mList.get(position);
        ParseObject vendor = coupon.getParseObject(Constants.VENDOR);
        Picasso.with(mContext).load(vendor.getString(Constants.PARSE_COLUMN_PROFILE)).into(holder.picture);

        double rate = vendor.getDouble("rating");
        holder.rating.setText(vendor.getDouble("rating") + "");
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


        holder.name.setText(vendor.getString("name"));
        holder.address.setText(vendor.getString("address"));

        String type = coupon.getString("type");
        String amount = coupon.getString("amount");
        String detail;
        if (type.equals("%")) {
            detail = amount + type + " off";
        } else {
            detail = type + amount + " off";
        }

        Date expiration = coupon.getDate("expiration");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        holder.expiration.append(dateFormat.format(expiration));
        holder.details.setText(detail);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {

        private ImageView picture;
        private TextView name;
        private TextView rating;
        private TextView expiration;
        private TextView details;
        private TextView address;

        public CouponViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.imageView_vendor);
            name = (TextView) itemView.findViewById(R.id.vendor_name);
            rating = (TextView) itemView.findViewById(R.id.vendor_rating);
            expiration = (TextView) itemView.findViewById(R.id.expiration_date);
            details = (TextView) itemView.findViewById(R.id.coupon_details);
            address = (TextView) itemView.findViewById(R.id.textView_address);
        }
    }
}
