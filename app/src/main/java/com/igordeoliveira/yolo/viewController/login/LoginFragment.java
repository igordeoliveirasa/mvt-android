package com.igordeoliveira.yolo.viewController.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.igordeoliveira.yolo.R;
import com.igordeoliveira.yolo.lib.ServerSocialLoginAuthenticator;
import com.igordeoliveira.yolo.viewController.home.HomeFragment;
import com.igordeoliveira.yolo.viewController.home.HomeFragmentActivity;
import com.igordeoliveira.yolo.viewController.splash.SplashActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private HomeFragmentActivity homeFragmentActivity;


    private View.OnClickListener googleButtonsClickListener;

    public View.OnClickListener getGoogleButtonsClickListener() {
        return googleButtonsClickListener;
    }

    public void setGoogleButtonsClickListener(View.OnClickListener googleButtonsClickListener) {
        this.googleButtonsClickListener = googleButtonsClickListener;
    }


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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status")); // asking permissions

        SignInButton btnSignInGoogle = (SignInButton)view.findViewById(R.id.sign_in_button);
        btnSignInGoogle.setOnClickListener(googleButtonsClickListener);

        //Button btnSignOutGoogle = (Button)view.findViewById(R.id.sign_out_button);
        //btnSignOutGoogle.setOnClickListener(googleButtonsClickListener);

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {

            final String token = session.getAccessToken();

            // Requesting Facebook Me
            Request me = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                    String email = graphResponse.optString("email");
                    Log.v(TAG, email);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(android.R.id.content, new HomeFragment());
                    ft.commit();


                    /*
                    Intent intent = new Intent(getActivity(), HomeFragmentActivity.class);
                    startActivity(intent);
                    getActivity().finish();
*/
                    // Server Authenticating
                    /*
                    ServerSocialLoginAuthenticator ssla = new ServerSocialLoginAuthenticator("http://onlinesociallogin.herokuapp.com/token_authentication/authenticate.json", token, email);
                    String json = null;
                    try {
                        json = ssla.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Log.v(TAG, "JSON: " + json);
                    */
                }
            });
            Bundle pars = me.getParameters();
            pars.putString("fields", "name, email");
            me.setParameters(pars);
            me.executeAsync();



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
