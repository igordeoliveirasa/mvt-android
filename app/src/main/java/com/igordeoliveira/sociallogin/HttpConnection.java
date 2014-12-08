package com.igordeoliveira.sociallogin;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by igor on 08/12/14.
 */
public class HttpConnection {
    private HttpClient httpClient;

    public HttpConnection() {
        httpClient = new DefaultHttpClient(); // -> novo metodo, mudar no teste. HttpClientBuilder.create().build();
    }

    public HttpConnection(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String post(String url, List<NameValuePair> params) throws IOException {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = httpClient.execute(httppost);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

        StringBuilder ret = new StringBuilder();
        String line;

        while( (line = reader.readLine()) != null) {
            ret.append(line);
        }
        return ret.toString();
    }
}
