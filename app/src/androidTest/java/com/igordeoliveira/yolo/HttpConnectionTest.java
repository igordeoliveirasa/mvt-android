package com.igordeoliveira.yolo;

import com.igordeoliveira.yolo.lib.HttpConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.*;


/**
 * Created by igor on 07/12/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest = Config.NONE)
public class HttpConnectionTest {

    @Test
    public void testPostSuccessfully() throws IOException, JSONException {

        // Setting the mocks up
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        HttpEntity httpEntity = Mockito.mock(HttpEntity.class);
        InputStream inputStream = new ByteArrayInputStream( "{\"status\":\"ok\", \"message\":\"Success!\"}".getBytes() );
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(inputStream);

        // Testing
        HttpConnection con = new HttpConnection(httpClient);
        String url = "http://onlinesociallogin.herokuapp.com/token_authentication/authenticate.json";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("TOKEN", "validtoken"));
        params.add(new BasicNameValuePair("EMAIL", "validemail"));
        String response = null;

        try {
            response = con.post(url, params);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        JSONTokener tokener = new JSONTokener(response);
        JSONObject obj = new JSONObject(tokener);

        assertEquals("ok", obj.getString("status"));
        assertEquals("Success!", obj.getString("message"));
    }
}
