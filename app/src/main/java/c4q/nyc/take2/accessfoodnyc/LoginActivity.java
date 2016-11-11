package c4q.nyc.take2.accessfoodnyc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

public class LoginActivity extends Activity {

    CallbackManager callbackManager;

    protected EditText usernameEditText;
    protected ImageView usernameimage;
    protected EditText passwordEditText;
    protected ImageView passwordimage;
    protected EditText usernameEditText2;
    protected ImageView username2image;
    protected EditText passwordEditText2;
    protected ImageView passwordimage2;
    protected EditText emailField;
    protected ImageView emailimage;
    protected Button loginButton;
    protected Button signUpButton;
    protected SignInButton mSignInButton;
    protected Button backButton;
    protected Button continueButton;
    protected LinearLayout layout;
    protected MainApplication app;

    private LoginButton mButtonFacebookLogin;
    private ImageView nameIcon;
    private EditText first;
    private EditText last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // facebook stuff
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        FacebookSdk.clearLoggingBehaviors();

        app = new MainApplication();

        signUpButton = (Button)findViewById(R.id.signupButtonID);
        usernameEditText = (EditText)findViewById(R.id.usernameField);
        usernameimage = (ImageView) findViewById(R.id.usernamefielsimageid);
        passwordEditText = (EditText)findViewById(R.id.passwordField);
        passwordimage = (ImageView) findViewById(R.id.passwordFieldimageid);
        usernameEditText2 = (EditText)findViewById(R.id.usernameField2);
        username2image = (ImageView) findViewById(R.id.usernameField2imageid);
        passwordEditText2 = (EditText)findViewById(R.id.passwordField2);
        passwordimage2 = (ImageView) findViewById(R.id.passwordFieldimageid2);
        loginButton = (Button)findViewById(R.id.loginButton);
        emailField = (EditText) findViewById(R.id.emailFieldID);
        emailimage = (ImageView) findViewById(R.id.emailFieldIDimageid);
        backButton = (Button) findViewById(R.id.BackButtonID);
        continueButton = (Button) findViewById(R.id.ContinueButtonID);
        layout = (LinearLayout) findViewById(R.id.layoutID);
        mButtonFacebookLogin = (LoginButton) findViewById(R.id.login_button);
        nameIcon = (ImageView) findViewById(R.id.name_icon);
        first = (EditText) findViewById(R.id.first);
        last = (EditText) findViewById(R.id.last);

        mButtonFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email", "user_birthday"), new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException err) {
                        if (user == null) {
                            Toast.makeText(getApplicationContext(), "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_SHORT).show();
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Profile profile = Profile.getCurrentProfile();
                            user.put(Constants.FIRST_NAME, profile.getFirstName());
                            user.put(Constants.LAST_NAME, profile.getLastName());
                            user.put(Constants.PARSE_COLUMN_PROFILE, profile.getProfilePictureUri(300, 300).toString());
                            user.put("fbId", profile.getId());
                            user.saveInBackground();

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", user);
                            installation.put("fbId", profile.getId());
                            installation.saveInBackground();

                            Toast.makeText(getApplicationContext(), "User signed up and logged in through Facebook!", Toast.LENGTH_SHORT).show();
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            goToMapsActivity();
                        } else {
                            Profile profile = Profile.getCurrentProfile();
                            user.put(Constants.PARSE_COLUMN_PROFILE, profile.getProfilePictureUri(300, 300).toString());
                            user.saveInBackground();

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", user);
                            installation.put("fbId", profile.getId());
                            installation.saveInBackground();
                            Toast.makeText(getApplicationContext(), "User logged in through Facebook!", Toast.LENGTH_SHORT).show();
                            Log.d("MyApp", "User logged in through Facebook!");
                            goToMapsActivity();
                        }
                    }
                });
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    setProgressBarIndeterminateVisibility(true);

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            setProgressBarIndeterminateVisibility(false);

                            if (e == null) {
                                // Success!
                                goToMapsActivity();
                                //finish();
                            } else {
                                // Fail
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText2.getText().toString().trim();
                String password = passwordEditText2.getText().toString().trim();
                String email = emailField.getText().toString().trim();
                String firstName = first.getText().toString().trim();
                String lastName = last.getText().toString().trim();


                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    setProgressBarIndeterminateVisibility(true);

                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put(Constants.FIRST_NAME, firstName);
                    newUser.put(Constants.LAST_NAME, lastName);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);

                            if (e == null) {
                                // Success!
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUpListeners(boolean isResumed) {
        if (isResumed) {

        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpListeners(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUpListeners(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    public void showSignUpFields (View v){
        mButtonFacebookLogin.setVisibility(View.GONE);
        usernameEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        usernameEditText2.setVisibility(View.VISIBLE);
        passwordEditText2.setVisibility(View.VISIBLE);
        emailField.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        usernameimage.setVisibility(View.GONE);
        passwordimage.setVisibility(View.GONE);
        username2image.setVisibility(View.VISIBLE);
        passwordimage2.setVisibility(View.VISIBLE);
        emailimage.setVisibility(View.VISIBLE);
        first.setVisibility(View.VISIBLE);
        last.setVisibility(View.VISIBLE);
        nameIcon.setVisibility(View.VISIBLE);

    }

    public void back (View v){
        mButtonFacebookLogin.setVisibility(View.VISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        usernameimage.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        passwordimage.setVisibility(View.VISIBLE);
        usernameEditText2.setVisibility(View.GONE);
        username2image.setVisibility(View.GONE);
        passwordEditText2.setVisibility(View.GONE);
        passwordimage2.setVisibility(View.GONE);
        emailField.setVisibility(View.GONE);
        emailimage.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);
        continueButton.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        first.setVisibility(View.GONE);
        last.setVisibility(View.GONE);
        nameIcon.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void goToMapsActivity() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}










