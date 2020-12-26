package com.example.zingakart.Helper;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Ashu on 05-sep-2017.
 */

public class CustomRequest extends Request<JSONObject> {

    private Listener listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String, String> params, Listener<JSONObject> responseListener, ErrorListener listener) {
        super(Method.POST, url, listener);
        this.listener = responseListener;
        this.params = params;

    }

    public CustomRequest(int method, String url, Map<String, String> params, Listener<JSONObject> responseListener, ErrorListener listener) {
        super(method, url, listener);
        this.listener = responseListener;
        this.params = params;


    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}
