package c4q.nyc.take2.accessfoodnyc.twitter;

/**
 * Created by Hoshiko on 8/28/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import c4q.nyc.take2.accessfoodnyc.R;

public class TweetAdapter extends BaseAdapter {
    ArrayList<String> tweetList;

    public ArrayList<String> getTweetPics() {
        return tweetPics;
    }

    public void setTweetPics(ArrayList<String> tweetPics) {
        this.tweetPics = tweetPics;
    }

    ArrayList<String> tweetPics;
    Context context;

    public TweetAdapter(Context context, ArrayList<String> tweetList, ArrayList<String> tweetPics) {
        this.tweetList = tweetList;
        this.tweetPics = tweetPics;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tweetList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.tweet_list_item, null);
        }

        String tweet = tweetList.get(position);
        String tweetpicurl = tweetPics.get(position);
        TextView txtTweet = (TextView) convertView.findViewById(R.id.txtTweet);
        ImageView picTweet = (ImageView)convertView.findViewById(R.id.picTweet);

        txtTweet.setText(tweet);
//        txtTweetBy.setText(tweet);
        Picasso.with(context).load(tweetpicurl).fit().centerCrop().centerCrop().noFade().into(picTweet);

        return convertView;
    }
}