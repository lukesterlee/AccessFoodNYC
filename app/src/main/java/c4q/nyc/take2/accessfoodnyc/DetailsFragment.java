package c4q.nyc.take2.accessfoodnyc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Location;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {

    private static final String TAG = DetailsFragment.class.getName();

    private ParseUser user;

    private TextView mVendorNameText;
    private TextView mTextViewVendorAddress;
    private TextView mSnippetText;

    private ImageView mVendorPicImage;
    private Button add;
    private ParseObject selectedVendor;
    private ImageView yelpLogo;
    private static List <String> addList;

    private static String mId;
    private RecyclerView mRecyclerViewPictures;
    private PicturesAdapter mPicturesAdapter;
    private boolean isYelp;
    private String objectId;
    private Button mButtonTakePicture;

    private LinearLayout mParentFavoritedFriends;
    private NoScrollAdapter<ParseUser> mFavoritedFriendsAdapter;

    private TextView countFavs;
    private TextView ratings;

    private LinearLayout mParentLayout;
    private NoScrollAdapter<ParseObject> mNoScrollAdapter;

    private TextView here;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
    }

    private void initializeViews(View rootView) {
        add = (Button) rootView.findViewById(R.id.button_add);
        mButtonTakePicture = (Button) rootView.findViewById(R.id.cbid);
        yelpLogo = (ImageView) rootView.findViewById(R.id.yelp_logo);
        mVendorNameText = (TextView) rootView.findViewById(R.id.vendor_name);
        mVendorPicImage = (ImageView) rootView.findViewById(R.id.vendor_pic);
        mSnippetText = (TextView) rootView.findViewById(R.id.snippet_text);
        mTextViewVendorAddress = (TextView) rootView.findViewById(R.id.vendor_address);
        here = (TextView) rootView.findViewById(R.id.here);
        mRecyclerViewPictures = (RecyclerView) rootView.findViewById(R.id.recyclerView_details_pictures);
        countFavs = (TextView) rootView.findViewById(R.id.count_favs);
        ratings = (TextView) rootView.findViewById(R.id.ratings);
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.review_container);
        mParentFavoritedFriends = (LinearLayout) rootView.findViewById(R.id.parent_favorited_friends);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        initializeViews(rootView);

        objectId = getArguments().getString(Constants.EXTRA_KEY_OBJECT_ID);
        isYelp = getArguments().getBoolean(Constants.EXTRA_KEY_IS_YELP);

        mNoScrollAdapter = new NoScrollAdapter<>(getActivity(), mParentLayout);
        mFavoritedFriendsAdapter = new NoScrollAdapter<>(getActivity(), mParentFavoritedFriends);



        LinearLayoutManager lm2 = new LinearLayoutManager(getActivity());
        lm2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewPictures.setLayoutManager(lm2);
        mPicturesAdapter = new PicturesAdapter(getActivity());
        mRecyclerViewPictures.setAdapter(mPicturesAdapter);

        final ParseRelation<ParseUser> relation = user.getRelation("friends");

        if (isYelp) {

            yelpLogo.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> findVendor = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
            findVendor.whereEqualTo(Constants.YELP_ID, objectId).getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject vendor, ParseException e) {
                    if (vendor != null) {

                        getCountFavs(vendor);
                        relation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                if (list.size() != 0) {
                                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery(Constants.PARSE_CLASS_REVIEW);
                                    query1.include("writer");
                                    query1.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn("writer", list);
                                    query1.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> list, ParseException e) {
                                            if (list.size() != 0) {
                                                here.setVisibility(View.VISIBLE);
                                                mNoScrollAdapter.addReviews(list);
                                            } else {
                                                here.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    addFavoritedFriends(vendor, list);
                                }
                            }
                        });
                    }
                }
            });

        } else {

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject vendor, ParseException e) {

                    getCountFavs(vendor);
                    setRatings(vendor.getDouble("rating"));

                    mVendorNameText.setText(vendor.getString("name"));
                    mTextViewVendorAddress.setText(vendor.getString("address"));
                    mSnippetText.setText(vendor.getString("description"));


                    ParseQuery<ParseObject> pictures = ParseQuery.getQuery(Constants.PARSE_CLASS_PICTURE);
                    pictures.whereEqualTo(Constants.VENDOR, vendor).findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (list.size() != 0) {
                                mPicturesAdapter = new PicturesAdapter(getActivity(), list);
                                mRecyclerViewPictures.setAdapter(mPicturesAdapter);
                                mRecyclerViewPictures.setVisibility(View.VISIBLE);
                                mVendorPicImage.setVisibility(View.GONE);

                            } else {
                                Picasso.with(getActivity()).load(vendor.getString(Constants.PARSE_COLUMN_PROFILE)).into(mVendorPicImage);
                            }
                        }
                    });


                }
            });

            relation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(final List<ParseUser> list, ParseException e) {

                    ParseQuery<ParseObject> findVendorQuery = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
                    findVendorQuery.getInBackground(objectId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject vendor, ParseException e) {

                            if (list.size() != 0) {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery(Constants.PARSE_CLASS_REVIEW);
                                query1.include("writer");
                                query1.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn("writer", list);
                                query1.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (list.size() != 0) {
                                            here.setVisibility(View.VISIBLE);
                                            mNoScrollAdapter.addReviews(list);
                                        } else {
                                            here.setVisibility(View.GONE);
                                        }
                                    }
                                });

                                addFavoritedFriends(vendor, list);

                                ParseQuery<ParseObject> favorites2 = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
                                favorites2.whereEqualTo(Constants.VENDOR, vendor).whereEqualTo(Constants.PARSE_COLUMN_FOLLOWER, user);
                                favorites2.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (parseObject == null) {
                                            addButtonUnfavorited();
                                        } else {
                                            addButtonFavorited();
                                        }
                                    }
                                });
                            }

                        }
                    });

                }
            });
        }
        return rootView;
    }

    private void addFavoritedFriends(ParseObject vendor, List<ParseUser> list) {
        ParseQuery<ParseObject> favorites = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
        favorites.include(Constants.PARSE_COLUMN_FOLLOWER);
        favorites.whereEqualTo(Constants.VENDOR, vendor).whereContainedIn(Constants.PARSE_COLUMN_FOLLOWER, list);
        favorites.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list.size() != 0) {
                    List<ParseUser> friends = new ArrayList<ParseUser>();
                    for (ParseObject favorite : list) {
                        ParseUser friend = favorite.getParseUser(Constants.PARSE_COLUMN_FOLLOWER);
                        friends.add(friend);
                    }
                    mFavoritedFriendsAdapter.addFavoritedFriends(friends);
                }
            }
        });
    }

    public void onYelpData(Business business) {
        mVendorNameText.setText(business.name());
        mVendorPicImage.setVisibility(View.VISIBLE);
        String businessImgUrl = (business.imageUrl());
        Picasso.with(getActivity()).load(businessImgUrl).into(mVendorPicImage);
        List<String> address = addressGenerator(business);
        if (address.size() >= 2) {
            mTextViewVendorAddress.append(address.get(0) + ", " + address.get(1));
        } else if (address.size() == 1) {
            mTextViewVendorAddress.append(address.get(0));
        }
        mSnippetText = (TextView)getActivity().findViewById(R.id.snippet_text);
        mId = business.id();
        setRatings(business.rating());
    }

    public void setRatings(Double rating) {
        ratings.setText(rating + "");
        if (rating >= 4.5) {
            ratings.setBackgroundResource(R.drawable.circle_5);
        } else if (rating >= 4.0) {
            ratings.setBackgroundResource(R.drawable.circle_4);
        } else if (rating >= 3.5) {
            ratings.setBackgroundResource(R.drawable.circle_3);
        } else if (rating >= 3.0) {
            ratings.setBackgroundResource(R.drawable.circle_2);
        } else {
            ratings.setBackgroundResource(R.drawable.circle_1);
        }
    }


    public static List<String> addressGenerator(Business business){
        Location bizLocation = business.location();
        addList = bizLocation.displayAddress();
        return addList;
    }

    @Override
    public void onResume() {
        super.onResume();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String currentText = add.getText().toString();
                if (currentText.equals("FAVORITE")) {

                    if (isYelp) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
                        query.whereStartsWith(Constants.YELP_ID, mId);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(final ParseObject vendor, ParseException e) {
                                if (e == null) {
                                    //object exists
                                    addToFavorite(user, vendor);
                                } else {
                                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                        //object doesn't exist
                                        //add yelpID as new vendor in parse.com
                                        selectedVendor = new ParseObject(Constants.PARSE_CLASS_VENDOR);
                                        selectedVendor.put(Constants.YELP_ID, mId);
                                        selectedVendor.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                addToFavorite(user, selectedVendor);
                                            }
                                        });
                                    } else {
                                        //unknown error, debug
                                    }
                                }
                            }
                        });
                    } else {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
                        query.getInBackground(objectId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject vendor, ParseException e) {
                                addToFavorite(user, vendor);
                            }
                        });
                    }

                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject vendor, ParseException e) {
                            removeFromFavorite(user, vendor);

                        }
                    });

                }

            }
        });
        mButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        PictureDialogFragment picDialog = new PictureDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_KEY_OBJECT_ID, objectId);
        bundle.putBoolean(Constants.EXTRA_KEY_IS_YELP, isYelp);
        picDialog.setArguments(bundle);
        picDialog.show(getActivity().getSupportFragmentManager(), "picD");
    }

    private void removeFromFavorite(ParseUser user, ParseObject vendor) {
        ParsePush.unsubscribeInBackground(vendor.getObjectId());
        final ParseQuery<ParseObject> favorites = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
        favorites.whereEqualTo(Constants.PARSE_COLUMN_FOLLOWER, user).whereEqualTo(Constants.VENDOR, vendor);
        favorites.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject favorite, ParseException e) {
                if (favorite != null) {
                    favorite.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            addButtonUnfavorited();
                            Toast.makeText(getActivity(), "Removed from favorites!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void addToFavorite(ParseUser user, ParseObject vendor) {
        ParsePush.subscribeInBackground(vendor.getObjectId());
        ParseObject favorite = new ParseObject(Constants.PARSE_CLASS_FAVORITE);
        favorite.put(Constants.PARSE_COLUMN_FOLLOWER, user);
        favorite.put(Constants.VENDOR, vendor);
        favorite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                addButtonFavorited();
                Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCountFavs(ParseObject vendor) {
        ParseQuery<ParseObject> fav = ParseQuery.getQuery(Constants.PARSE_CLASS_FAVORITE);
        fav.whereEqualTo(Constants.VENDOR, vendor).countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                countFavs.setText(i + "");
            }
        });
    }

    private void addButtonFavorited() {
        add.setText("FAVORITED!");
        add.setTextColor(getResources().getColor(R.color.white));
        add.setBackgroundResource(R.drawable.rounded_corner);
        add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_white_24dp, 0, 0, 0);
        add.setPaddingRelative(40,0,0,0);

    }

    private void addButtonUnfavorited() {
        add.setText("FAVORITE");
        add.setTextColor(getResources().getColor(R.color.accentColor));
        add.setBackgroundResource(R.drawable.buttons);
        add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp, 0, 0, 0);
        add.setPaddingRelative(40, 0, 0, 0);
    }
}