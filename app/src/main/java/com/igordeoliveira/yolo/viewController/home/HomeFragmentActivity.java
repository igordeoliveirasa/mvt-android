package com.igordeoliveira.yolo.viewController.home;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.igordeoliveira.yolo.BuildConfig;
import com.igordeoliveira.yolo.Constants;
import com.igordeoliveira.yolo.R;
import com.igordeoliveira.yolo.viewController.login.LoginFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HomeFragmentActivity extends FragmentActivity {

    /* Request code used to invoke sign in user interactions. */
    private Menu menu;
    public Menu getMenu() {return this.menu;}
    private LoginFragment loginFragment;

    GoogleAnalytics analytics;
    Tracker tracker;
    public Tracker getTracker() {return this.tracker;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showAppHash();

        analytics = GoogleAnalytics.getInstance(getApplicationContext());
        analytics.setAppOptOut(BuildConfig.DEBUG);

        tracker = analytics.newTracker(R.xml.app_tracker);
        tracker.enableAdvertisingIdCollection(true);

        if (savedInstanceState == null) {
            loginFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, loginFragment)
                    .commit();
        } else {
            loginFragment = (LoginFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuItemLogout) {

            Session.getActiveSession().close();
            item.setVisible(false);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, new LoginFragment());
            ft.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAppHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    Constants.PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

}
