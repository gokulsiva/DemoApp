package com.example.admin.demoapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String NAME_KEY = "nameKey";
    public static final String LOGGEDIN_KEY = "loggedin";
    public static final String WHATS_APP_KEY = "whatsAppKey";
    public static final String EMAIL_KEY = "emailKey";
    public static final String LOCATION_KEY = "locationKey";
    public static final String LATTITUDE_KEY = "lattitudeKey";
    public static final String LONGITUDE_KEY = "longitudeKey";
    public static final String COURSE_KEY = "courseKey";
    public static final String ACCOUNT_KEY = "accountKey";

    SharedPreferences sharedpreferences;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;


    EditText emailIdLogin;
    EditText passwordLogin;
    Button loginButton;
    Button signUpButton;

    public static boolean fromMain = true;

    //SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedpreferences.contains(LOGGEDIN_KEY))
        {
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
        }

        // EditText and Buttons

        emailIdLogin = (EditText) findViewById(R.id.emailIdLogin);
        passwordLogin = (EditText) findViewById(R.id.passwordLogin);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);


        // Google signIn Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
          //  showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            hideProgressDialog();
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(NAME_KEY, acct.getDisplayName());
            editor.putString(EMAIL_KEY, acct.getEmail());
            editor.commit();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    // [END signIn]

   // [ Start Login]

    private void logIn(){

        String email = emailIdLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if(email.equals("")||password.equals(""))
        {
            Toast.makeText(this,"Please provide EmailId and Password",Toast.LENGTH_LONG).show();
        } else
        {
            mProgressDialog = ProgressDialog.show(this,"Signing In.....","Please wait",false,false);

            String url = "http://gokulonlinedatabase.net16.net/jusPayDemo/login/loginDemo.php?email="+email+"&password="+password;
            GetJson json = new GetJson(this,url);
            json.jsonRequest(new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    mProgressDialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(result);

                        if(json.isNull("Result_Array"))
                        {
                            Toast.makeText(MainActivity.this,"Invalid Account Credentials",Toast.LENGTH_LONG).show();Toast.makeText(MainActivity.this,"Invalid Account Credentials",Toast.LENGTH_LONG).show();
                        }else
                        {
                            JSONArray array = json.getJSONArray("Result_Array");
                            JSONObject finalObject = array.getJSONObject(0);
                             SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(MainActivity.LOGGEDIN_KEY,"true");
                            editor.putString(MainActivity.EMAIL_KEY,finalObject.getString("email"));
                            editor.putString(MainActivity.NAME_KEY,finalObject.getString("name"));
                            editor.putString(MainActivity.LATTITUDE_KEY,finalObject.getString("lattitude"));
                            editor.putString(MainActivity.LONGITUDE_KEY,finalObject.getString("longitude"));
                            editor.putString(MainActivity.LOCATION_KEY,finalObject.getString("location"));
                            editor.putString(MainActivity.WHATS_APP_KEY,finalObject.getString("whatsapp"));
                            editor.putString(MainActivity.COURSE_KEY,finalObject.getString("knowledge"));
                            editor.putString(MainActivity.ACCOUNT_KEY,finalObject.getString("account"));
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                            startActivity(intent);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
         //   mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

                if(mGoogleApiClient!=null)
                {
                    updateUI(false);
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(

                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    // [START_EXCLUDE]
                                    updateUI(false);
                                    // [END_EXCLUDE]
                                    // mProgressDialog.dismiss();
                                }
                            });
                        activityCreator_googleSignIn();
            }

        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

        }
    }

    private void activityCreator_googleSignIn()
    {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String url = "http://gokulonlinedatabase.net16.net/jusPayDemo/login/googleSign.php?email="+sharedpreferences.getString(EMAIL_KEY,"xyz");
        Log.v("URL",url);
        GetJson json = new GetJson(this,url);
        json.jsonRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.v("Result",result);
                    JSONObject json = new JSONObject(result);

                    if(json.isNull("Result_Array"))
                    {
                        EditText email = (EditText) findViewById(R.id.email);
                        EditText name = (EditText) findViewById(R.id.name);
                        Intent i = new Intent(MainActivity.this,DetailsActivity.class);
                        i.putExtra(NAME_KEY,sharedpreferences.getString(NAME_KEY,""));
                        i.putExtra(EMAIL_KEY,sharedpreferences.getString(EMAIL_KEY,""));
                        startActivity(i);
                    }else
                    {
                        JSONArray array = json.getJSONArray("Result_Array");
                        JSONObject finalObject = array.getJSONObject(0);
                        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(MainActivity.LOGGEDIN_KEY,"true");
                        editor.putString(MainActivity.EMAIL_KEY,finalObject.getString("email"));
                        editor.putString(MainActivity.NAME_KEY,finalObject.getString("name"));
                        editor.putString(MainActivity.LATTITUDE_KEY,finalObject.getString("lattitude"));
                        editor.putString(MainActivity.LONGITUDE_KEY,finalObject.getString("longitude"));
                        editor.putString(MainActivity.LOCATION_KEY,finalObject.getString("location"));
                        editor.putString(MainActivity.WHATS_APP_KEY,finalObject.getString("whatsapp"));
                        editor.putString(MainActivity.COURSE_KEY,finalObject.getString("knowledge"));
                        editor.putString(MainActivity.ACCOUNT_KEY,finalObject.getString("account"));
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //Google SignIn button Action
                signIn();
                break;
            case R.id.loginButton:
                //loginButton Action
                logIn();
                break;
            case R.id.signUpButton:
                //signupButton Action
                Intent i = new Intent(this,DetailsActivity.class);
                startActivity(i);
                break;

        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}