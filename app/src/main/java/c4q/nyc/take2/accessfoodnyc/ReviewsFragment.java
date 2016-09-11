package c4q.nyc.take2.accessfoodnyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.List;

public class ReviewsFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton mButtonReview;
    private RecyclerView mRecyclerView;
    private ReviewAdapter mAdapter;
    private String objectId;
    private boolean isYelp;

    private TextView mTextViewNoReview;
    private TextView mTextViewRatingScore;
    private TextView mTextViewReviewsCount;

    private LinearLayout oneBar;
    private LinearLayout twoBar;
    private LinearLayout threeBar;
    private LinearLayout fourBar;
    private LinearLayout fiveBar;

    private LinearLayout ratingInto;

//    private ArrayList<Integer> ratingNums;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        mButtonReview = (FloatingActionButton) rootView.findViewById(R.id.button_review);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_review);
        mTextViewNoReview = (TextView) rootView.findViewById(R.id.textView_no_review);
        mTextViewRatingScore = (TextView) rootView.findViewById(R.id.rating_score);
        mTextViewReviewsCount = (TextView) rootView.findViewById(R.id.reviews_count);
        ratingInto = (LinearLayout) rootView.findViewById(R.id.rating_info);

        oneBar = (LinearLayout) rootView.findViewById(R.id.text_1star);
        twoBar = (LinearLayout) rootView.findViewById(R.id.text_2star);
        threeBar = (LinearLayout) rootView.findViewById(R.id.text_3star);
        fourBar = (LinearLayout) rootView.findViewById(R.id.text_4star);
        fiveBar = (LinearLayout) rootView.findViewById(R.id.text_5star);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lm);

        ParseUser user = ParseUser.getCurrentUser();

        objectId = getArguments().getString(Constants.EXTRA_KEY_OBJECT_ID);
        isYelp = getArguments().getBoolean(Constants.EXTRA_KEY_IS_YELP);

        refresh();

        return rootView;
    }

    private void refresh() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
        if (isYelp) {

            query.whereEqualTo(Constants.YELP_ID, objectId).findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list.size() != 0) {
                        final ParseObject vendor = list.get(0);
                        ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery(Constants.PARSE_CLASS_REVIEW);
                        reviewQuery.include("writer");
                        reviewQuery.whereEqualTo(Constants.VENDOR, vendor).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> reviews, ParseException e) {
                                if (reviews.size() == 0) {
                                    ratingInto.setVisibility(View.GONE);
                                    mTextViewNoReview.setText("No reviews yet. \nWhy don't you write one?");
                                    mTextViewNoReview.setVisibility(View.VISIBLE);
                                } else {
                                    mAdapter = new ReviewAdapter(getActivity(), reviews);
                                    mRecyclerView.setAdapter(mAdapter);
                                    calculateReviews(vendor, reviews);
                                }
                            }
                        });
                    } else {
                        ratingInto.setVisibility(View.GONE);
                        mTextViewNoReview.setText("No reviews yet. \nWhy don't you write one?");
                        mTextViewNoReview.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject parseObject, ParseException e) {
                    ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery(Constants.PARSE_CLASS_REVIEW);
                    reviewQuery.include("writer");
                    reviewQuery.whereEqualTo(Constants.VENDOR, parseObject).findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> reviews, ParseException e) {
                            if (reviews.size() == 0) {
                                ratingInto.setVisibility(View.GONE);
                                mTextViewNoReview.setText("No reviews yet. \nWhy don't you write one?");
                                mTextViewNoReview.setVisibility(View.VISIBLE);
                            } else {
                                calculateReviews(parseObject, reviews);
                                mAdapter = new ReviewAdapter(getActivity(), reviews);
                                mRecyclerView.setAdapter(mAdapter);
                                double ratingsum = 0;
                                for (final ParseObject review : reviews) {
                                    ratingsum += (Integer) review.get("rating");
                                }
//                                Toast.makeText(getActivity(), String.valueOf(ratingsum), Toast.LENGTH_SHORT).show();
                                double averageRating = Math.round((ratingsum / (reviews.size())) * 10.0) / 10.0;

                                //Toast.makeText(getActivity(), String.valueOf(averageRating), Toast.LENGTH_SHORT).show();
                                parseObject.put("rating", averageRating);
                                parseObject.put("ratingCount", reviews.size());
                                parseObject.saveInBackground();
                            }
                        }
                    });

                }

            });

        }
    }


    private void setUpListener(boolean isResumed) {
        if (isResumed) {
            mButtonReview.setOnClickListener(this);
        } else {
            mButtonReview.setOnClickListener(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpListener(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setUpListener(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_review:
                writeReview();
                break;
        }
    }

    private void writeReview() {
        final FragmentManager manager = getActivity().getSupportFragmentManager();
        if (isYelp) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
            query.whereEqualTo(Constants.YELP_ID, objectId).findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    ReviewDialogFragment dialog = new ReviewDialogFragment();
                    Bundle argument = new Bundle();
                    if (list.size() == 0) {
                        argument.putBoolean(Constants.EXTRA_KEY_IS_YELP, true);
                    } else {
                        objectId = list.get(0).getObjectId();
                        argument.putBoolean(Constants.EXTRA_KEY_IS_YELP, false);
                    }
                    argument.putString(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                    dialog.setArguments(argument);
                    dialog.setTargetFragment(ReviewsFragment.this, 0);
                    dialog.show(manager, "Review");
                }
            });
        } else {
            ReviewDialogFragment dialog = new ReviewDialogFragment();
            Bundle argument = new Bundle();
            argument.putString(Constants.EXTRA_KEY_OBJECT_ID, objectId);
            argument.putBoolean(Constants.EXTRA_KEY_IS_YELP, false);
            dialog.setArguments(argument);
            dialog.setTargetFragment(ReviewsFragment.this, 0);
            dialog.show(manager, "Review");


        }
    }

    private void calculateReviews(ParseObject vendor, List<ParseObject> reviews) {
        ratingInto.setVisibility(View.VISIBLE);
        int count = reviews.size();
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int five = 0;
        double sum = 0;
        for (ParseObject review : reviews) {
            int rate = review.getInt("rating");
            sum += rate;
            switch (rate) {
                case 1:
                    one++;
                    break;
                case 2:
                    two++;
                    break;
                case 3:
                    three++;
                    break;
                case 4:
                    four++;
                    break;
                case 5:
                    five++;
                    break;
            }
        }

        int totalLength = oneBar.getLayoutParams().width;

        ViewGroup.LayoutParams params = oneBar.getLayoutParams();
        params.width = (totalLength * one) / count;
        oneBar.setLayoutParams(params);

        ViewGroup.LayoutParams params2 = twoBar.getLayoutParams();
        params2.width = (totalLength * two) / count;
        twoBar.setLayoutParams(params2);

        ViewGroup.LayoutParams params3 = threeBar.getLayoutParams();
        params3.width = (totalLength * three) / count;
        threeBar.setLayoutParams(params3);

        ViewGroup.LayoutParams params4 = fourBar.getLayoutParams();
        params4.width = (params4.width * four) / count;
        fourBar.setLayoutParams(params4);

        ViewGroup.LayoutParams params5 = fiveBar.getLayoutParams();
        params5.width = (params5.width * five) / count;
        fiveBar.setLayoutParams(params5);


        DecimalFormat df = new DecimalFormat("0.0");
        double score = sum / count;
        mTextViewRatingScore.setText(df.format(score));
        mTextViewReviewsCount.setText(count + "");
        vendor.put("rating", score);
        vendor.saveInBackground();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                refresh();
                break;
        }
    }
}
