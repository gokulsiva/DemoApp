package com.example.admin.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ProgressDialog loading;


    MultiAutoCompleteTextView course;
    Spinner account;
    EditText email;
    EditText password;
    EditText reEnterPassword;
    EditText name;
    EditText whatsapp;
    EditText location;
    Button ok;

    String emailVal="";
    String passwordVal="";
    String reEnterPassVal="";
    String nameVal="";
    String whatsAppVal="";
    String accountVal = "";
    String courseVal = "";
    String locationVal ="";
    String lattitude = "";
    String longitude = "";
    String url = "";

    String[] languages={"Android ","Java","IOS","SQL","JDBC","Web services","C","C++"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        email = (EditText) findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        reEnterPassword = (EditText) findViewById(R.id.reenter_password);
        name = (EditText)findViewById(R.id.name);
        whatsapp = (EditText)findViewById(R.id.whatsapp_number);
        location = (EditText)findViewById(R.id.location);
        ok = (Button)findViewById(R.id.button);

        account = (Spinner) findViewById(R.id.accountType);

        // Spinner click listener spinner.setOnItemSelectedListener(());
        account.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select your Account");
        categories.add("Mentor");
        categories.add("Student");


        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        account.setAdapter(spinnerAdapter);

        course = (MultiAutoCompleteTextView)findViewById(R.id.courses);

        ArrayAdapter multiCompleteAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,languages);
        course.setAdapter(multiCompleteAdapter);
        course.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Bundle gt=getIntent().getExtras();

        if(gt!=null)
        {

            email.setText(gt.getString(MainActivity.EMAIL_KEY));
            name.setText(gt.getString(MainActivity.NAME_KEY));
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailVal = email.getText().toString().trim();
                passwordVal = password.getText().toString().trim();
                reEnterPassVal = reEnterPassword.getText().toString().trim();
                nameVal = name.getText().toString().trim();
                courseVal = course.getText().toString().trim();
                locationVal = location.getText().toString().toString();
                if (courseVal.charAt(courseVal.length()-1)==',')
                {
                    courseVal=courseVal.substring(0,courseVal.length()-1);
                }
                whatsAppVal = whatsapp.getText().toString().trim();
                int number_checker = whatsAppVal.length();


                if((emailVal=="")||(passwordVal=="")||(reEnterPassVal=="")||(nameVal=="")||(whatsAppVal=="")||(accountVal=="Select your Account")||(courseVal=="")||(locationVal==""))
                {
                    Toast.makeText(DetailsActivity.this,"Please fill all the details",Toast.LENGTH_LONG).show();
                }
                else if(number_checker<10)
                {
                    Toast.makeText(DetailsActivity.this,"Please enter a valid mobile number",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(passwordVal.equals(reEnterPassVal))
                    {
                        //Handle button click Add the details to database
                        loading = ProgressDialog.show(DetailsActivity.this,"Getting Location.....","Please wait",false,false);
                        final String latlongUrl = "http://gokulonlinedatabase.net16.net/jusPayDemo/map/map.php?address="+locationVal;

                        GetJson json = new GetJson(DetailsActivity.this,latlongUrl);
                        json.jsonRequest(new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    loading.dismiss();
                                    JSONObject json = new JSONObject(result);
                                    JSONArray array = json.getJSONArray("Result_Array");
                                    JSONObject finalObject = array.getJSONObject(0);
                                    lattitude=finalObject.getString("lattitude");
                                    longitude=finalObject.getString("longitude");
                                    if((lattitude.equalsIgnoreCase("null"))||(longitude.equalsIgnoreCase("null")))
                                    {
                                        Toast.makeText(DetailsActivity.this,"Invalid Address",Toast.LENGTH_LONG).show();

                                    }else
                                    {
                                        url = "http://gokulonlinedatabase.net16.net/jusPayDemo/signup/signUp.php?email="+emailVal+"&password="+passwordVal+"&name="+nameVal+"&whatsapp="+whatsAppVal+"&account="+accountVal+"&knowledge="+courseVal+"&location="+locationVal+"&lattitude="+lattitude+"&longitude="+longitude;
                                        url=url.replaceAll(" ","%20");
                                        loading = ProgressDialog.show(DetailsActivity.this,"Creating account.....","Please wait",false,false);
                                        GetJson json1 = new GetJson(DetailsActivity.this,url);
                                        json1.jsonRequest(new VolleyCallback() {
                                            @Override
                                            public void onSuccess(String result) {
                                                loading.dismiss();
                                                try {
                                                    JSONObject obj = new JSONObject(result);
                                                    JSONArray arr = obj.getJSONArray("Result_Array");
                                                    JSONObject finalObject1 = arr.getJSONObject(0);
                                                    String signUpResult = finalObject1.getString("result");
                                                    if(signUpResult.equalsIgnoreCase("exists")||signUpResult.equalsIgnoreCase("try_later"))
                                                    {
                                                        //User already exists
                                                        if(signUpResult.equalsIgnoreCase("exists"))
                                                        {
                                                            Toast.makeText(DetailsActivity.this,"Email ID already exists please Login",Toast.LENGTH_LONG).show();
                                                        } else
                                                        {
                                                            Toast.makeText(DetailsActivity.this,"Please try later",Toast.LENGTH_LONG).show();
                                                        }

                                                    }else
                                                    {
                                                        //Successfully registered
                                                        SharedPreferences sharedPreferences = null;
                                                        sharedPreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString(MainActivity.LOGGEDIN_KEY,"true");
                                                        editor.putString(MainActivity.LATTITUDE_KEY,lattitude);
                                                        editor.putString(MainActivity.LONGITUDE_KEY,longitude);
                                                        editor.putString(MainActivity.LOCATION_KEY,locationVal);
                                                        editor.putString(MainActivity.WHATS_APP_KEY,whatsAppVal);
                                                        editor.putString(MainActivity.COURSE_KEY,courseVal);
                                                        editor.putString(MainActivity.ACCOUNT_KEY,accountVal);
                                                        editor.commit();
                                                        Intent intent = new Intent(DetailsActivity.this,MapsActivity.class);
                                                        startActivity(intent);

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });




                    }else
                    {
                        Toast.makeText(DetailsActivity.this,"Passwords don't match",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

   //for multiAutoComplete

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        courseVal = course.toString();
        return super.onOptionsItemSelected(item);
    }

    //for spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        accountVal = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
