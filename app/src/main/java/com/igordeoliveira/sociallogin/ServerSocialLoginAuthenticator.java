package com.igordeoliveira.sociallogin;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by igor on 06/12/14.
 */
public class ServerSocialLoginAuthenticator extends AsyncTask<HashMap<String,String>, Void, String> {

    public static final String PARAM_URL = "URL";
    public static final String PARAM_TOKEN = "TOKEN";
    public static final String PARAM_EMAIL = "EMAIL";

    @Override
    protected String doInBackground(HashMap<String,String>... params) {
        String ret = null;
        try {
            HttpClient httpclient = new DefaultHttpClient(); // -> novo metodo, mudar no teste. HttpClientBuilder.create().build();
            HashMap<String, String> map = ((HashMap<String, String>) params[0]);
            String url = map.get(PARAM_URL);//[PARAM_URL];// "http://onlinesociallogin.herokuapp.com/token_authentication/authenticate.json";
            //url = "http://172.20.10.3:3000";
            HttpPost httppost = new HttpPost(url);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("token", map.get(PARAM_TOKEN)));
            nameValuePairs.add(new BasicNameValuePair("email", map.get(PARAM_EMAIL)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            ret = reader.readLine();
            //JSONTokener tokener = new JSONTokener(json);
            //JSONArray finalResult = new JSONArray(tokener);
        } catch (Exception ex) {
        }
        return ret;
    }
}
