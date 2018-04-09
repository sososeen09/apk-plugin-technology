package com.sososeen09.plugin.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView view = new TextView(this);
//        view.setText(getPackageName());
//        setContentView(view);

        setContentView(R.layout.activity_main);
    }
}
