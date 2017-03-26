package com.ahlan.ahlanapp;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainBar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainBar);

        TextView mName = (TextView)findViewById(R.id.textView);
        Bundle b = getIntent().getExtras();
        String value = "fail";
        value = b.getString("userName");
        mName.setText(value);
        try{
            Thread.sleep(3000);
        }catch (Exception e) {
        }
    }
}
