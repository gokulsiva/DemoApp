package com.example.admin.demoapp;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class GetJson {

    private String url;
    private Activity context;
    private String json = "";


    public GetJson(Activity context, String url) {
        this.url = url;
        this.context = context;
    }

    public void jsonRequest(final VolleyCallback callback){

        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        json = response;
                        Log.v("Response : ",response);
                        callback.onSuccess(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            json = context.getString(R.string.Timeout_Error);
                            callback.onSuccess(json);
                        } else if (error instanceof AuthFailureError) {
                            json = context.getString(R.string.Auth_Error);
                            callback.onSuccess(json);
                        } else if (error instanceof ServerError) {
                            json = context.getString(R.string.Server_Error);
                            callback.onSuccess(json);
                        } else if (error instanceof NetworkError) {
                            json = context.getString(R.string.Network_Error);
                            callback.onSuccess(json);
                        } else if (error instanceof ParseError) {
                            json = context.getString(R.string.Parse_Error);
                            callback.onSuccess(json);
                        }

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack());
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


}