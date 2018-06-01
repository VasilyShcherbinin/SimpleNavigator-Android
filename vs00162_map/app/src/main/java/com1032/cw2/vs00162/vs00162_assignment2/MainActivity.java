package com1032.cw2.vs00162.vs00162_assignment2;

/**
 * Created by Vasily on 23/05/2016.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Responsible for launching and operating the MainActivity on application launch.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Defining local variables.
     */
    private Button toMap;
    private Button aboutButton;

    /**
     * Called when MainActivity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Adding an action for a FloatingActionButton.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Student Email: vs00162@surrey.ac.uk", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Add an action to the first button in MainActivity -> redirects to ListActivity.
        toMap = (Button) findViewById(R.id.buttonList);
        toMap.getBackground().setColorFilter(new LightingColorFilter(Color.BLACK, Color.BLACK));
        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listActivity = new Intent(MainActivity.this, MapActivity.class);
                startActivity(listActivity);
            }
        });

        //Add an action to the second button in MainActivity -> redirects to the About activity.
        aboutButton = (Button) findViewById(R.id.buttonAbout);
        aboutButton.getBackground().setColorFilter(new LightingColorFilter(Color.BLACK, Color.BLACK));
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutActivity = new Intent(MainActivity.this, About.class);
                startActivity(aboutActivity);
            }
        });

    }
}