package com.igordeoliveira.yolo.lib;

import android.os.AsyncTask;

import com.igordeoliveira.yolo.lib.HttpConnection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 06/12/14.
 */
public class ServerSocialLoginAuthenticator extends AsyncTask<Void, Void, String> {

    public static final String PARAM_URL = "URL";
    public static final String PARAM_TOKEN = "TOKEN";
    public static final String PARAM_EMAIL = "EMAIL";

    private HttpConnection connection;
    private String url;
    private String email;
    private String socialLoginToken;

    public ServerSocialLoginAuthenticator(HttpConnection connection, String url, String email, String socialLoginToken) {
        this.connection = connection;
        this.url = url;
        this.email = email;
        this.socialLoginToken = socialLoginToken;
    }

    public ServerSocialLoginAuthenticator(String url, String email, String socialLoginToken) {
        this.connection = new HttpConnection();
        this.url = url;
        this.email = email;
        this.socialLoginToken = socialLoginToken;
    }

    @Override
    public String doInBackground(Void... params) {
        String ret = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("token", socialLoginToken));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            ret = connection.post(url,nameValuePairs);
        } catch (Exception ex) {
        }
        return ret;
    }

}
