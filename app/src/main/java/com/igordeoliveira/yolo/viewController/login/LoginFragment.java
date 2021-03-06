package com.igordeoliveira.yolo.viewController.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.igordeoliveira.yolo.Constants;
import com.igordeoliveira.yolo.R;
import com.igordeoliveira.yolo.model.User;
import com.igordeoliveira.yolo.viewController.home.HomeActivity;
import com.igordeoliveira.yolo.viewController.home.HomeFragment;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    public static final int RC_SIGN_IN = 0;

    private GoogleApiClient googleApiClient;
    public GoogleApiClient getGoogleApiClient() {return this.googleApiClient;}

    private boolean intentInProgress;
    private boolean signInClicked;

    private ConnectionResult mConnectionResult;


    private static final String TAG = "MainFragment";
    private HomeActivity homeActivity;


    private View.OnClickListener googleButtonsClickListener;


    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper uiHelper;
    public UiLifecycleHelper getUiHelper() {return uiHelper;}
    public void setUiHelper(UiLifecycleHelper uiHelper) {this.uiHelper = uiHelper;}


    public LoginFragment() {/*Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        homeActivity = (HomeActivity)getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);




        LoginButton btnSignInFacebook = (LoginButton) view.findViewById(R.id.authButton);
        btnSignInFacebook.setFragment(this);
        btnSignInFacebook.setReadPermissions(Arrays.asList("user_likes", "user_status", "email")); // asking permissions


        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();


        SignInButton btnSignInGoogle = (SignInButton)view.findViewById(R.id.sign_in_button);
        btnSignInGoogle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!googleApiClient.isConnecting()) {
                    signInClicked = true;
                    //resolveSignInError();
                    intentInProgress = false;
                    googleApiClient.connect();
                }
            }
        });

        return view;
    }

    public void showHomeFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, new HomeFragment());
        ft.commit();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {

            Log.v(TAG, "Fb: Logged in...");
            final String token = session.getAccessToken();

            final LoginFragment thizz = this;
            // Requesting Facebook Me
            Request me = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    GraphObject graphObject = response.getGraphObject();
                    if (graphObject!=null) {
                        JSONObject graphResponse = graphObject.getInnerJSONObject();

                        // saving preferences
                        String email = graphResponse.optString("email");
                        String image = "http://graph.facebook.com/" + user.getId() + "/picture";

                        User sessionUser = new User("facebook", new Long(user.getId()), user.getName(), email, image);
                        homeActivity.setUserSession(sessionUser);
                        homeActivity.getApplicationPreferences().edit().putString("provider", sessionUser.getProvider())
                                .putLong("uid", sessionUser.getUid())
                                .putString("name", sessionUser.getName())
                                .putString("email", sessionUser.getEmail())
                                .putString("image", sessionUser.getImage())
                                .commit();

                        homeActivity.getTracker().send(new HitBuilders.EventBuilder()
                                .setCategory("Authentication")
                                .setAction("LoggedIn")
                                .setLabel("Facebook")
                                .build());

                        showHomeFragment();
                    } else {
                        Session.getActiveSession().close();

                        AlertDialog dialog = new AlertDialog.Builder(thizz.getView().getContext())
                                .setTitle("Atenção")
                                .setMessage("Verifique se seu dispositivo encontra-se conectado com a internet corretamente.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
            Bundle pars = me.getParameters();
            pars.putString("fields", "name, email");
            me.setParameters(pars);
            me.executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Fb: Logged out...");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();

        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginFragment.RC_SIGN_IN) {
            if (resultCode != getActivity().RESULT_OK) {
                signInClicked = false;
            }

            intentInProgress = false;

            if (!getGoogleApiClient().isConnecting()) {
                getGoogleApiClient().connect();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }





    // GOOGLE

    //GOOGLE
    public void onStart() {
        super.onStart();

    }

    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    protected void resolveSignInError() {
        if (mConnectionResult!= null && mConnectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                getActivity().startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!intentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (signInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public void onConnected(Bundle connectionHint) {
        // We've resolved any connection errors.  googleApiClient can be used to
        // access Google APIs on behalf of the user.
        signInClicked = false;
        showHomeFragment();
    }

    public void onConnectionSuspended(int cause) {
        googleApiClient.connect();
    }
}
