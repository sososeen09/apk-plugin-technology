package com.sososeen09.plugin.demo;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView view = new TextView(this);
//        view.setText(getPackageName());
//        setContentView(view);

        setContentView(R.layout.activity_main);
    }
}
