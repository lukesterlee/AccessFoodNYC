package c4q.nyc.take2.accessfoodnyc;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RemoveDialogFragment extends DialogFragment {

    private String objectId;
    private DialogCallback mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectId = getArguments().getString(Constants.EXTRA_KEY_OBJECT_ID);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Remove Friend")
                .setMessage("Are you sure you want to remove this friend?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final ParseUser me = ParseUser.getCurrentUser();
                        try {
                            final JSONObject data = new JSONObject("{\"alert\": \" removed" + "\"," +
                                    "\"removeId\": \"" + me.getObjectId() + "\"}");

                            ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
                            query.whereEqualTo(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                            query.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> list, ParseException e) {
                                    ParseQuery query = ParseInstallation.getQuery();
                                    query.whereEqualTo("user", list.get(0));
                                    ParsePush push = new ParsePush();
                                    push.setQuery(query);
                                    push.setData(data);
                                    push.sendInBackground();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final ParseRelation<ParseUser> relation = me.getRelation("friends");
                        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
                        query.whereEqualTo(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                ParseUser notMyFriend = list.get(0);
                                relation.remove(notMyFriend);
                                me.saveInBackground();
                            }
                        });

                        mCallback.dialogClicked(0);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (DialogCallback) activity;
    }
}
