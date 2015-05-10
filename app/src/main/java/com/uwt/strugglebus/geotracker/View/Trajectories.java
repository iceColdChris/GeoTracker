package com.uwt.strugglebus.geotracker.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.uwt.strugglebus.geotracker.R;


/**
 * This class is in charge of keeping
 * track of keeping track of the users
 * location.
 */
public class Trajectories extends ActionBarActivity {

    private static final String DB_NAME = "Trajectories";
    private static final String TABLE = "Locations";
    private static final int ROWS = 5;

    /**
     * {@inheritDoc}
     *
     * Ontop of the above functionality
     * this method sets up the trajectory
     * database.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectories);
        TableLayout table = (TableLayout) findViewById(R.id.traject_table);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        final SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Intent it = getIntent();
        long startTime = it.getLongExtra("startTime", 0);
        long endTime = it.getLongExtra("endTime", 0);

        String query = "SELECT * FROM " + TABLE + " WHERE time >= " + startTime + " AND time <= " + endTime + ";";
        Cursor c = db.rawQuery(query, null);
        while(c.moveToNext()) {
            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(rowParams);
            row.setPadding(5, 5, 5, 5);
            String[] values = new String[ROWS];
            values[0] = c.getFloat(0) + "";
            values[1] = c.getFloat(1) + "";
            values[2] = c.getFloat(2) + "";
            values[3] = c.getFloat(3) + "";
            values[4] = c.getInt(4) + "";

            for(int i = 0; i < ROWS; i++) {
                TextView temp = new TextView(getApplicationContext());
                temp.setBackgroundColor(Color.parseColor("#BBBBBB"));
                temp.setPadding(5,5,5,5);
                temp.setText(values[i], TextView.BufferType.NORMAL);
                temp.setTextColor(Color.BLACK);
                row.addView(temp);
            }
            table.addView(row);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trajectories, menu);
        return true;
    }

    /**
     *{@inheritDoc}
     */
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
