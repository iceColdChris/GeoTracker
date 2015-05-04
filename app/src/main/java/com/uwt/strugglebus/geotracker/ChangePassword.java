package com.uwt.strugglebus.geotracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uwt.strugglebus.geotracker.View.MyAccount;


public class ChangePassword extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button accept = (Button) findViewById(R.id.pass_accept);
        Button cancel = (Button) findViewById(R.id.pass_cancel);
        final SharedPreferences prefs = getSharedPreferences(getString(R.string.SHARED_PREFERENCES)
                , Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        final EditText oldPass = (EditText) findViewById(R.id.old_password);
        final EditText newPass = (EditText) findViewById(R.id.new_password);
        final EditText newConfirm = (EditText) findViewById(R.id.new_confirm_password);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old = prefs.getString(getString(R.string.password), "");
                if(!old.equals(oldPass.getText().toString())){
                    Toast.makeText(getApplicationContext(), R.string.invalid_old_password, Toast.LENGTH_LONG).show();
                } else if(!newPass.getText().toString().equals(newConfirm.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_confirm, Toast.LENGTH_LONG).show();
                } else {
                    editor.putString(getString(R.string.password), newPass.getText().toString());
                    editor.commit();
                    Intent account = new Intent(getApplicationContext(), MyAccount.class);
                    startActivity(account);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent account = new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
