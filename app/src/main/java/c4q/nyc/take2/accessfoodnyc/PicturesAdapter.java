package c4q.nyc.take2.accessfoodnyc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PicturesHolder> {

    private Context mContext;
    private List<ParseObject> mList;
    private int pixels;

    public PicturesAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        float dp = 250.0f;
        float scale = mContext.getResources().getDisplayMetrics().density;
        pixels = (int) (dp * scale + 0.5f);
    }

    public PicturesAdapter(Context context, List<ParseObject> list) {
        mContext = context;
        mList = list;
        float dp = 250.0f;
        float scale = mContext.getResources().getDisplayMetrics().density;
        pixels = (int) (dp * scale + 0.5f);
    }

    @Override
    public PicturesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.list_item_pictures, parent, false);
        return new PicturesHolder(row);
    }

    @Override
    public void onBindViewHolder(PicturesHolder holder, int position) {

        //Bitmap bitmap = BitmapFactory.decodeByteArray(getItem(position), 0, getItem(position).length);
        //holder.picture.setImageBitmap(bitmap);

        ParseObject item = mList.get(position);



        Picasso.with(mContext).load(item.getParseFile("data").getUrl()).resize(pixels, pixels).centerCrop().into(holder.picture);
    }

//    public byte[] getItem(int position) {
//        return mList.get(position);
//    }
//
//    public void addPicture(byte[] bytes) {
//        mList.add(bytes);
//        notifyItemInserted(mList.size() - 1);
//        notifyItemChanged(mList.size()-1);
//    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class PicturesHolder extends RecyclerView.ViewHolder {

        protected ImageView picture;

        public PicturesHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.imageview_details_pictures);
        }
    }
}
