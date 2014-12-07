package com.igordeoliveira.sociallogin;

import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by igor on 06/12/14.
 */
public class FacebookGraph extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

    public static final String PARAM_SESSION = "SESSION";
    public static final String PARAM_EMAIL = "EMAIL";

    public HashMap<String, Object> ret = new HashMap<String, Object>();

    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String, Object>... params) {

        HashMap<String, Object> map = params[0];

        Request me = Request.newMeRequest((com.facebook.Session) map.get(PARAM_SESSION), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                ret.put(PARAM_EMAIL, graphResponse.optString("email"));
            }
        });

        Bundle pars = me.getParameters();
        pars.putString("fields", "email,name");
        me.setParameters(pars);
        me.executeAndWait();

        return ret;
    }
}
