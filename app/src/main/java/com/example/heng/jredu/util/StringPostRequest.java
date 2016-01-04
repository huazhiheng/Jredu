package com.example.heng.jredu.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heng on 2015/10/12.
 */
public class StringPostRequest extends StringRequest {
    private Map<String, String> myDate;

    public StringPostRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public StringPostRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST,url, listener, errorListener);
        myDate = new HashMap<>();
    }

    public void putParams(String key, String values) {
        myDate.put(key, values);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return myDate;
    }


}
