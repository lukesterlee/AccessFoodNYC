package c4q.nyc.take2.accessfoodnyc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import c4q.nyc.take2.accessfoodnyc.Constants;
import c4q.nyc.take2.accessfoodnyc.PictureActivity;
import c4q.nyc.take2.accessfoodnyc.R;

public class PictureDialogFragment extends DialogFragment {
    static final int EXTERNAL_CONTENT_URI = 0;
    Uri targetUri;
    private String objectId;
    private boolean isYelp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Base_Theme_AppCompat_Light_Dialog);
        objectId = getArguments().getString(Constants.EXTRA_KEY_OBJECT_ID);
        isYelp = getArguments().getBoolean(Constants.EXTRA_KEY_IS_YELP);


        builder.setTitle("Pics")
                .setPositiveButton("Take Pic", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Intent intent = new Intent(getActivity(), PictureActivity.class);
                        intent.putExtra(Constants.EXTRA_PICTIRE, Constants.FLAG_CAMERA);
                        intent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                        intent.putExtra(Constants.EXTRA_KEY_IS_YELP, isYelp);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Camera Roll", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), PictureActivity.class);
                        intent.putExtra(Constants.EXTRA_PICTIRE, Constants.FLAG_GALLERY);
                        intent.putExtra(Constants.EXTRA_KEY_OBJECT_ID, objectId);
                        intent.putExtra(Constants.EXTRA_KEY_IS_YELP, isYelp);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }
}
