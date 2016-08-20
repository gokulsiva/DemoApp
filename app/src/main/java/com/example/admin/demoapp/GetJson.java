package com.example.admin.demoapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class GetJson {

    private String url;
    private Activity context;
    private String json = "";


    public GetJson(Activity context, String url) {
        this.url = url;
        this.context = context;
    }

    public void jsonRequest(final VolleyCallback callback) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Error", error.getMessage());

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsObjRequest);


    }
}