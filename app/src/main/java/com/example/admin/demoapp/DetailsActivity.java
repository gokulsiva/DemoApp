package com.example.admin.demoapp;

import android.app.ProgressDialog;
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

            email.setText(gt.getString(MainActivity.Email));
            name.setText(gt.getString(MainActivity.Name));
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


                if((emailVal=="")||(passwordVal=="")||(reEnterPassVal=="")||(nameVal=="")||(whatsAppVal=="")||(accountVal=="Select your Account")||(courseVal=="")||(locationVal==""))
                {
                    Toast.makeText(DetailsActivity.this,"Please fill all the details",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(passwordVal.equals(reEnterPassVal))
                    {
                        //Handle button click Add the details to database
                        loading = ProgressDialog.show(DetailsActivity.this,"Creating Account.....","Please wait",false,false);


                        


                        url = "http://gokulonlinedatabase.net16.net/jusPayDemo/signup/signUp.php?email="+emailVal+"&password="+passwordVal+"&name="+nameVal+"&whatsapp="+whatsAppVal+"&account="+accountVal+"&knowledge="+courseVal+"&location="+locationVal+"&lattitude="+lattitude+"&longitude="+longitude;


                        GetJson getJson = new GetJson(DetailsActivity.this,url);


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
