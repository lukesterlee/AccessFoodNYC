package c4q.nyc.take2.accessfoodnyc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PictureActivity extends AppCompatActivity {
    private Uri imageUri;
    private ImageView imageView;
    private Bitmap bitmap;
    private String objectId;
    private boolean isYelp;


    private ProgressDialog mProgressDialog;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        objectId = intent.getStringExtra(Constants.EXTRA_KEY_OBJECT_ID);
        isYelp = intent.getBooleanExtra(Constants.EXTRA_KEY_IS_YELP, true);

        int flag = getIntent().getIntExtra(Constants.EXTRA_PICTIRE, -1);


        switch (flag) {
            case 1:
                takePic();
                break;
            case 2:
                usePic();
                break;
        }



        setContentView(R.layout.activity_pic);
        progressBar = (ProgressBar) findViewById(R.id.pgid);
        imageView = (ImageView) findViewById(R.id.imageID);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void takePic() {

        String mediaStorageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile = new File(mediaStorageDir + File.separator + "IMG_" + timeStamp + ".jpg");
        imageUri = Uri.fromFile(mediaFile);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
        this.startActivityForResult(takePictureIntent, Constants.FLAG_CAMERA);



//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
    }
    public void usePic() {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, Constants.FLAG_GALLERY);
    }

    private void addPictureToGallery(Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.FLAG_CAMERA && resultCode == RESULT_OK) {

            //Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);


        }
        else if(requestCode == Constants.FLAG_GALLERY && resultCode == RESULT_OK) {

            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            imageView.setImageBitmap(bitmap);

            cursor.close();
        }
    }
    public void save (View v){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setMax(4);
        progressBar.setProgress(0);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        progressBar.setProgress(1);

        final ParseFile file = new ParseFile("picture.jpg", byteArray);

        file.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                final ParseObject picture = new ParseObject("Picture");
                picture.put("data", file);
                picture.put("uploader", ParseUser.getCurrentUser());
                //picture.saveInBackground();

                progressBar.setProgress(2);

                //Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_SHORT).show();

                if (isYelp){
                    final ParseObject newYelpVendor = new ParseObject(Constants.PARSE_CLASS_VENDOR);
                    newYelpVendor.put(Constants.YELP_ID, objectId);
                    progressBar.setProgress(3);
                    newYelpVendor.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            picture.put(Constants.VENDOR, newYelpVendor);
                            picture.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    ParseRelation<ParseObject> pictures = newYelpVendor.getRelation("pictures");
                                    pictures.add(picture);
                                    newYelpVendor.saveInBackground();
                                    progressBar.setProgress(4);

                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_CLASS_VENDOR);
                    progressBar.setProgress(3);
                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject vendor, ParseException e) {
                            picture.put(Constants.VENDOR, vendor);
                            picture.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    ParseRelation<ParseObject> pictures = vendor.getRelation("pictures");
                                    pictures.add(picture);
                                    vendor.saveInBackground();
                                    progressBar.setProgress(4);
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_SHORT).show();

                        }


                    });

                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
               // progressBar.getProgress(integer);

            }
        });
    }
}


