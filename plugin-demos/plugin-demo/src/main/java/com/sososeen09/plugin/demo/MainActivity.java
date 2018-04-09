package com.sososeen09.plugin.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView view = new TextView(this);
//        view.setText(getPackageName());
//        setContentView(view);

        setContentView(R.layout.activity_main);
    }
}
