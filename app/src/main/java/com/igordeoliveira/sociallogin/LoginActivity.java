package com.igordeoliveira.sociallogin;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private LoginButtonsFragment loginButtonsFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.igordeoliveira.sociallogin",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginButtonsFragment = new LoginButtonsFragment();
            loginButtonsFragment.setGoogleButtonsClickListener(this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loginButtonsFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            loginButtonsFragment = (LoginButtonsFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    //GOOGLE

    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public void onConnected(Bundle connectionHint) {
        // We've resolved any connection errors.  mGoogleApiClient can be used to
        // access Google APIs on behalf of the user.

        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult!= null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button
                && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            //resolveSignInError();
            mIntentInProgress = false;
            mGoogleApiClient.connect();
        }

        if (view.getId() == R.id.sign_out_button) {
            if (mGoogleApiClient.isConnected()) {
                mSignInClicked = false;
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                //mGoogleApiClient.connect();
            }
        }
    }

}
