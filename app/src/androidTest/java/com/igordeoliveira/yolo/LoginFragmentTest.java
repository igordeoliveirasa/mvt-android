package com.igordeoliveira.yolo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.igordeoliveira.yolo.viewController.login.LoginFragment;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by igor on 07/12/14.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18)
public class LoginFragmentTest {

    public static void startFragment( Fragment fragment )
    {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class)
                .create()
                .start()
                .resume()
                .get();

        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add( fragment, null );
        fragmentTransaction.commit();
    }

    @Test
    public void testGettersAndSetters() throws IOException, JSONException {
        // Setting the mocks up
        LoginFragment fragment = new LoginFragment();
        View.OnClickListener myClickListener = new View.OnClickListener() {@Override public void onClick(View v) {}};
        fragment.setGoogleButtonsClickListener(myClickListener);
        assertEquals(myClickListener, fragment.getGoogleButtonsClickListener());

        UiLifecycleHelper lifecycleHelper = Mockito.mock(UiLifecycleHelper.class);
        fragment.setUiHelper(lifecycleHelper);
        assertEquals(lifecycleHelper, fragment.getUiHelper());
    }

    @Test
    public void testOnCreateView() throws IOException, JSONException {
        // Setting the mocks up
        LoginButton loginButton = Mockito.mock(LoginButton.class);
        SignInButton signInButton = Mockito.mock(SignInButton.class);
        Button signOutButton = Mockito.mock(Button.class);

        View mockView = Mockito.mock(View.class);
        when(mockView.findViewById(R.id.authButton)).thenReturn(loginButton);
        when(mockView.findViewById(R.id.sign_in_button)).thenReturn(signInButton);
        //when(mockView.findViewById(R.id.sign_out_button)).thenReturn(signOutButton);

        ViewGroup container = Mockito.mock(ViewGroup.class);

        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        when(inflater.inflate(R.layout.fragment_login, container, false)).thenReturn(mockView);

        Bundle savedInstanceState = Mockito.mock(Bundle.class);

        LoginFragment fragment = new LoginFragment();
        View view = fragment.onCreateView(inflater, container, savedInstanceState);

        // assertations
        verify(loginButton).setFragment(fragment);
        verify(inflater).inflate(R.layout.fragment_login, container, false);
        verify(signInButton).setOnClickListener(fragment.getGoogleButtonsClickListener());
        verify(signOutButton).setOnClickListener(fragment.getGoogleButtonsClickListener());

        assertEquals(mockView, view);
    }
/*
    @Test
    public void testOnResume() throws IOException, JSONException {
        LoginButtonsFragment fragment = new LoginButtonsFragment();
        startFragment(fragment);
    }
    */
}
