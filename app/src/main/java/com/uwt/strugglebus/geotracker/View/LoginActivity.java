package com.uwt.strugglebus.geotracker.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.Model.Registration;
import com.uwt.strugglebus.geotracker.Model.ResetPassword;
import com.uwt.strugglebus.geotracker.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains the logic needed for the login page.
 * The necessary buttons are initialized and the appropriate logic to let the user log-in.
 */
public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.login);
        Button register = (Button) findViewById(R.id.register);
        Button forgot = (Button) findViewById(R.id.forgot_password);

        final  SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES),
                getApplicationContext().MODE_PRIVATE);

        int uid = prefs.getInt("uid", -1);
        if(uid != -1) { //user exists in shared prefs
            final EditText email = ((EditText) findViewById(R.id.email));
            email.setText(prefs.getString(getString(R.string.email), "email"));

            final EditText pw = ((EditText) findViewById(R.id.password));
            pw.setText(prefs.getString(getString(R.string.password), "password"));
        }

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                String mEmail = prefs.getString(getString(R.string.email), "email");
                String mPass = prefs.getString(getString(R.string.password), "password");

                String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                if(!matcher.matches()) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_email_format, Toast.LENGTH_LONG).show();
                } else if (!email.equals(mEmail) ) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_email, Toast.LENGTH_LONG).show();
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_password_format, Toast.LENGTH_LONG).show();
                } else if (!password.equals(mPass)) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_LONG).show();
                } else {
                    Intent login = new Intent(getApplicationContext(), MyAccount.class);
                    startActivity(login);
                    finish();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent register = new Intent(getApplicationContext(), Registration.class);
                startActivity(register);
                finish();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent forgot = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(forgot);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}