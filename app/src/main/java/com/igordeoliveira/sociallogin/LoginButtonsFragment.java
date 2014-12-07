package com.igordeoliveira.sociallogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class LoginButtonsFragment extends Fragment {

    private static final String TAG = "MainFragment";

    public View.OnClickListener getGoogleButtonsClickListener() {
        return googleButtonsClickListener;
    }

    public void setGoogleButtonsClickListener(View.OnClickListener googleButtonsClickListener) {
        this.googleButtonsClickListener = googleButtonsClickListener;
    }

    private View.OnClickListener googleButtonsClickListener;


    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper uiHelper;

    public LoginButtonsFragment() {/*Required empty public constructor */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status")); // asking permissions

        SignInButton btnSignInGoogle = (SignInButton)view.findViewById(R.id.sign_in_button);
        btnSignInGoogle.setOnClickListener(googleButtonsClickListener);

        Button btnSignOutGoogle = (Button)view.findViewById(R.id.sign_out_button);
        btnSignOutGoogle.setOnClickListener(googleButtonsClickListener);

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {

            String token = session.getAccessToken();

            // Requesting Facebook Me
            FacebookGraph facebookGraph = new FacebookGraph();
            HashMap<String, Object> graphParams = new HashMap<String, Object>();
            graphParams.put(facebookGraph.PARAM_SESSION, session);
            String email = "";
            try {
                HashMap<String, Object> graphRet =  facebookGraph.execute(graphParams).get();
                email = (String) graphRet.get(facebookGraph.PARAM_EMAIL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.v(TAG, email);

            // Server Authenticating
            ServerSocialLoginAuthenticator ssla = new ServerSocialLoginAuthenticator();
            HashMap<String, String> pars = new HashMap<String, String>();
            pars.put(ServerSocialLoginAuthenticator.PARAM_URL, "http://onlinesociallogin.herokuapp.com/token_authentication/authenticate.json");
            pars.put(ServerSocialLoginAuthenticator.PARAM_TOKEN, session.getAccessToken());
            pars.put(ServerSocialLoginAuthenticator.PARAM_EMAIL, email);

            String json = null;
            try {
                json = ssla.execute(pars).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "JSON: " + json);

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
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

}
