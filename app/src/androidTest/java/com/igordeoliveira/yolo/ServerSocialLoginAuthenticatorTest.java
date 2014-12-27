package com.igordeoliveira.yolo;

import com.igordeoliveira.yolo.lib.HttpConnection;
import com.igordeoliveira.yolo.lib.ServerSocialLoginAuthenticator;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


/**
 * Created by igor on 07/12/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest = Config.NONE)
public class ServerSocialLoginAuthenticatorTest {

    @Test
    public void testPostSuccessfully() throws IOException, JSONException {
        // Setting the mocks up
        HttpConnection connection = Mockito.mock(HttpConnection.class);
        when(connection.post(anyString(), Matchers.<List<NameValuePair>>anyObject())).thenReturn("{\"status\":\"ok\", \"message\":\"Success!\"}");

        String url = "http://onlinesociallogin.herokuapp.com/token_authentication/authenticate.json";
        String email = "validemail";
        String token = "validtoken";

        // Testing
        ServerSocialLoginAuthenticator ssla = new ServerSocialLoginAuthenticator(connection, url, email, token);
        String response = ssla.doInBackground();

        JSONTokener tokener = new JSONTokener(response);
        JSONObject obj = new JSONObject(tokener);

        assertEquals("ok", obj.getString("status"));
        assertEquals("Success!", obj.getString("message"));
    }
}
