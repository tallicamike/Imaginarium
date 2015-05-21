/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.imaginarium;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.common.activities.SampleActivityBase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase
        implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>, View.OnClickListener {

    public static final String TAG = "MainActivity";
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    private static final int RC_SIGN_IN = 0;

    private static final String SAVED_PROGRESS = "sign_in_progress";

    // Client ID for a web server that will receive the auth code and exchange it for a
    // refresh token if offline access is requested.
    private static final String WEB_CLIENT_ID = "WEB_CLIENT_ID";

    // Base URL for your token exchange server, no trailing slash.
    private static final String SERVER_BASE_URL = "SERVER_BASE_URL";

    // URL where the client should GET the scopes that the server would like granted
    // before asking for a serverAuthCode
    private static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";

    // URL where the client should POST the serverAuthCode so that the server can exchange
    // it for a refresh token,
    private static final String SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes";


    // GoogleApiClient wraps our service connection to Google Play services and
    // provides access to the users sign in state and Google's APIs.
    private GoogleApiClient mGoogleApiClient;

    // We use mSignInProgress to track whether user has clicked sign in.
    // mSignInProgress can be one of three values:
    //
    //       STATE_DEFAULT: The default state of the application before the user
    //                      has clicked 'sign in', or after they have clicked
    //                      'sign out'.  In this state we will not attempt to
    //                      resolve sign in errors and so will display our
    //                      Activity in a signed out state.
    //       STATE_SIGN_IN: This state indicates that the user has clicked 'sign
    //                      in', so resolve successive errors preventing sign in
    //                      until the user has successfully authorized an account
    //                      for our app.
    //   STATE_IN_PROGRESS: This state indicates that we have started an intent to
    //                      resolve an error, and so we should not start further
    //                      intents until the current intent completes.
    private int mSignInProgress;

    // Used to store the PendingIntent most recently returned by Google Play
    // services until the user clicks 'sign in'.
    private PendingIntent mSignInIntent;

    // Used to store the error code most recently returned by Google Play services
    // until the user clicks 'sign in'.
    private int mSignInError;

    // Used to determine if we should ask for a server auth code when connecting the
    // GoogleApiClient.  False by default so that this sample can be used without configuring
    // a WEB_CLIENT_ID and SERVER_BASE_URL.
    private boolean mRequestServerAuthCode = false;

    // Used to mock the state of a server that would receive an auth code to exchange
    // for a refresh token,  If true, the client will assume that the server has the
    // permissions it wants and will not send an auth code on sign in.  If false,
    // the client will request offline access on sign in and send and new auth code
    // to the server.  True by default because this sample does not implement a server
    // so there would be nowhere to send the code.
    private boolean mServerHasToken = true;

    private SignInButton mSignInButton;

    private boolean loginDisplayed;
    private boolean notifyServer;
//    private TextView mStatus;
//    private ListView mCirclesListView;
//    private ArrayAdapter<String> mCirclesAdapter;
//    private ArrayList<String> mCirclesList;


    private Person currentUser;
    private String gid;
    private String username;
    private String friendList;

    SlidingTabsColorsFragment slidingTabsColorsFragment;
    LoginFragment loginFragment;

    View separation_bar;

    ViewGroup parentView;

    private void signInUpdateLayout() {

        if (! loginDisplayed)
            return;

//        ViewGroup parentView = (ViewGroup) mSignInButton.getParent();
        parentView.removeView(mSignInButton);

//        mSignInButton.setVisibility(View.INVISIBLE);

//        parentView = (ViewGroup) welcome.getParent();
//        parentView.removeView(welcome);
//
//        parentView = (ViewGroup) instructions.getParent();
//        parentView.removeView(instructions);
//        separation_bar.setVisibility(View.VISIBLE);

        parentView.addView(separation_bar);
        loginDisplayed = false;
    }


    private void signOutUpdateLayout() {

        if (loginDisplayed)
            return;

        parentView.addView(mSignInButton);
        parentView.removeView(separation_bar);
//        separation_bar.setVisibility(View.INVISIBLE);

        loginDisplayed = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slidingTabsColorsFragment = new SlidingTabsColorsFragment();
        loginFragment =  new LoginFragment();

        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        separation_bar = findViewById(R.id.separation_bar);

        mSignInButton.setOnClickListener(this);
        parentView = (ViewGroup) mSignInButton.getParent();
        parentView.removeView(separation_bar);

        mGoogleApiClient = buildGoogleApiClient();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loginDisplayed = true;
        notifyServer = false;

        if (savedInstanceState == null) {

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                signInUpdateLayout();

                Bundle args = new Bundle();
                args.putString("gid", gid);
                args.putString("friends", friendList);
                slidingTabsColorsFragment.setArguments(args);
                transaction.replace(R.id.sample_content_fragment, slidingTabsColorsFragment);

            }
            else {
                signOutUpdateLayout();
                transaction.replace(R.id.sample_content_fragment, loginFragment);
            }
            transaction.commit();
        }

    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);

        return builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            // We only process button clicks when GoogleApiClient is not transitioning
            // between connected and not connected.
            switch (v.getId()) {
                case R.id.sign_in_button:
//                    mStatus.setText(R.string.status_signing_in);
                    mSignInProgress = STATE_SIGN_IN;
                    mGoogleApiClient.connect();
                    break;
            }
        }
    }

    /* onConnected is called when our Activity successfully connects to Google
     * Play services.  onConnected indicates that an account was selected on the
     * device, that the selected account has granted any requested permissions to
     * our app and that we were able to establish a service connection to Google
     * Play services.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Reaching onConnected means we consider the user signed in.
        Log.i(TAG, "onConnected");

        // Update the user interface to reflect that the user is signed in.
        mSignInButton.setEnabled(false);

        signInUpdateLayout();
        if (! notifyServer) {
           new HttpRequestTask().execute();
           notifyServer = true;
        }
        // Retrieve some profile information to personalize our app for the user.
        currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        gid = currentUser.getId();
        username = currentUser.getDisplayName();


        Log.d("[onConnected]", username);

        slidingTabsColorsFragment.setGid(gid);
        slidingTabsColorsFragment.setUsername(username);
        slidingTabsColorsFragment.setFriends(friendList);

//        mStatus.setText(String.format(
//                getResources().getString(R.string.signed_in_as),
//                currentUser.getDisplayName()));

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);

        // Indicate that the sign in process is complete.
        mSignInProgress = STATE_DEFAULT;

        //Toast.makeText(this, "[Login] Successful", Toast.LENGTH_LONG);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString("gid", gid);
        args.putString("friends", friendList);

        transaction.replace(R.id.sample_content_fragment, slidingTabsColorsFragment);
        transaction.commit();

    }

    /* onConnectionFailed is called when our Activity could not connect to Google
     * Play services.  onConnectionFailed indicates that the user needs to select
     * an account, grant permissions or resolve an error in order to sign in.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
            Log.w(TAG, "API Unavailable.");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            }
        }

        // In this sample we consider the user signed out whenever they do not have
        // a connection to Google Play services.
        onSignedOut();
    }

    /* Starts an appropriate intent or dialog for user interaction to resolve
     * the current error preventing the user from being signed in.  This could
     * be a dialog allowing the user to select an account, an activity allowing
     * the user to consent to the permissions being requested by your app, a
     * setting to enable device networking, etc.
     */
    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.
            createErrorDialog().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_DEFAULT;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {

        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {

            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            friendList = "";
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    friendList += personBuffer.get(i).getId() + ",";
                }
                friendList += personBuffer.get(count - 1).getId();
            } finally {
                personBuffer.close();
            }

        } else {
            Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
        }
    }

    private void onSignedOut() {
        // Update the UI to reflect that the user is signed out.
        mSignInButton.setEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, loginFragment);
        transaction.commit();

        signOutUpdateLayout();
//        mStatus.setText(R.string.status_signed_out);
//
//        mCirclesList.clear();
//        mCirclesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();
    }

    private Dialog createErrorDialog() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    this,
                    RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.e(TAG, "Google Play services resolution cancelled");
                            mSignInProgress = STATE_DEFAULT;
//                            mStatus.setText(R.string.status_signed_out);
                        }
                    });
        } else {
            return new AlertDialog.Builder(this)
                    .setMessage(R.string.play_services_error)
                    .setPositiveButton(R.string.close,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, "Google Play services error could not be "
                                            + "resolved: " + mSignInError);
                                    mSignInProgress = STATE_DEFAULT;
//                                    mStatus.setText(R.string.status_signed_out);
                                }
                            }).create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mif = getMenuInflater();

        mif.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout_button:
                // We clear the default account on sign out so that Google Play
                // services will not return an onConnected callback without user
                // interaction.
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
                onSignedOut();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
//                final String url = "http://tallica.koding.io:8080/create_user?gid=" + gid;
                Log.d("[Main]", "Send URL");
                final String url = "http://tallica.koding.io:8080/create_user?gname=" +
                        username +
                        "&gid=" + gid;

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getForObject(url, Integer.class);


            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
//            TextView greetingIdText = (TextView) findViewById(R.id.id_value);
//            TextView greetingContentText = (TextView) findViewById(R.id.content_value);
//            greetingIdText.setText(greeting.getId());
//            greetingContentText.setText(greeting.getContent());
//              Log.d("[Recieved]", userinfo.getId() + " " + userinfo.getName());
        }

    }
}

