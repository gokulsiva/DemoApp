package com.example.admin.demoapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    HashMap<Marker,JSONObject> myVal = new HashMap<Marker, JSONObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        Boolean b= sharedPreferences.contains(MainActivity.LOGGEDIN_KEY);
        if(!sharedPreferences.contains(MainActivity.LOGGEDIN_KEY))
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                if(!myVal.containsKey(marker))
                {
                    return null;
                }

                View v = getLayoutInflater().inflate(R.layout.window_layout, null);
                JSONObject json = myVal.get(marker);
                TextView markerName = (TextView)v.findViewById(R.id.markerName);
                TextView markerCourse = (TextView)v.findViewById(R.id.markerCourse);
                TextView markerContact = (TextView)v.findViewById(R.id.markerContact);
                try {
                    markerName.setText(json.getString("name"));
                    markerCourse.setText(json.getString("knowledge"));
                    markerContact.setText(json.getString("whatsapp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return  v;
            }
        });
        mMap.setOnInfoWindowClickListener(this);
        sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(MainActivity.LOGGEDIN_KEY))
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        else {
            Log.v("LoggedInKey",sharedPreferences.getString(MainActivity.LOGGEDIN_KEY,""));
            LatLng myPos = new LatLng(Double.parseDouble(sharedPreferences.getString(MainActivity.LATTITUDE_KEY, "")), Double.parseDouble(sharedPreferences.getString(MainActivity.LONGITUDE_KEY, "")));
            if (sharedPreferences.getString(MainActivity.ACCOUNT_KEY, "").equalsIgnoreCase("Student")) {
                mMap.addMarker(new MarkerOptions().position(myPos).icon(BitmapDescriptorFactory.fromResource(R.drawable.student)).draggable(true)).setTitle(sharedPreferences.getString(MainActivity.NAME_KEY, ""));
            } else {
                mMap.addMarker(new MarkerOptions().position(myPos).icon(BitmapDescriptorFactory.fromResource(R.drawable.staff)).draggable(true)).setTitle(sharedPreferences.getString(MainActivity.NAME_KEY, ""));
            }
            mMap.setOnMarkerDragListener(this);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myPos)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            double radiusInMeters = 700.0;

            CircleOptions circleOptions = new CircleOptions().center(myPos).radius(radiusInMeters).fillColor(Color.parseColor("#500084d3")).strokeColor(Color.parseColor("#0000FF")).strokeWidth(2);
            mMap.addCircle(circleOptions);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            final String account = sharedPreferences.getString(MainActivity.ACCOUNT_KEY, "");
            String lattitude = sharedPreferences.getString(MainActivity.LATTITUDE_KEY, "");
            String longitude = sharedPreferences.getString(MainActivity.LONGITUDE_KEY, "");
            String dialog;
            String url = "http://gokulonlinedatabase.net16.net/jusPayDemo/details/getDetails.php?account=" + account + "&lat=" + lattitude + "&long=" + longitude;
            if (account.equalsIgnoreCase("Student")) {
                dialog = "Getting mentors";
            } else {
                dialog = "Getting students";
            }

            progressDialog = ProgressDialog.show(this, dialog, "Please wait", false, false);
            GetJson json = new GetJson(this, url);
            json.jsonRequest(new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    progressDialog.dismiss();
                    Log.v("Account", result);
                    try {
                        JSONObject obj = new JSONObject(result);
                        JSONArray arr = obj.getJSONArray("Result_Array");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject finalObject1 = arr.getJSONObject(i);
                            Double lat = finalObject1.getDouble("lattitude");
                            Double lng = finalObject1.getDouble("longitude");
                            String title = finalObject1.getString("name");
                            LatLng latLng = new LatLng(lat, lng);
                            Log.v("Latlng", latLng.toString());
                            MarkerOptions markerOptions;
                            markerOptions = new MarkerOptions().position(latLng
                            ).title(title);
                            if (account.equalsIgnoreCase("Student")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.staff));

                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.student));
                            }
                            Marker m =mMap.addMarker(markerOptions);
                            myVal.put(m,finalObject1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

            Toast.makeText(this,"You can change your location by long pressing marker and dragging to a new location.",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
        {
            sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!myVal.containsKey(marker))
        {

        } else
        {
            JSONObject json = myVal.get(marker);
            try {
                String number = json.getString("whatsapp");
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                if (isWhatsappInstalled) {
                    Uri uri = Uri.parse("smsto:" + number);
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.putExtra("sms_body", "");
                    i.setPackage("com.whatsapp");
                    startActivity(i);
                } else {
                    Toast.makeText(this, "WhatsApp not Installed",
                            Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goToMarket);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this,"Please place this marker in your location to Update location",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng position = marker.getPosition();
        final String latt = String.valueOf(position.latitude);
        final String lng = String.valueOf(position.longitude);
        sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String email= sharedPreferences.getString(MainActivity.EMAIL_KEY,"");
        String url = "http://gokulonlinedatabase.net16.net/jusPayDemo/update/update.php?email="+email+"lat="+latt+"&long="+lng;
        progressDialog = ProgressDialog.show(this, "Updating your Location", "Please wait", false, false);
        GetJson getJson = new GetJson(this,url);
        getJson.jsonRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject json = new JSONObject(result);
                    if(!json.isNull("Result_Array"))
                    {
                        Toast.makeText(MapsActivity.this,"Location changed",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(MainActivity.LATTITUDE_KEY,latt);
                        editor.putString(MainActivity.LONGITUDE_KEY,lng);
                        editor.commit();
                        Intent intent = new Intent(MapsActivity.this,MapsActivity.class);
                        startActivity(intent);

                    }else
                    {
                        Toast.makeText(MapsActivity.this,"Location unchanged",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
