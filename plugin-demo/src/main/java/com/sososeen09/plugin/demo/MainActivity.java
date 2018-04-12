package com.sososeen09.plugin.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    private MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView view = new TextView(this);
//        view.setText(getPackageName());
//        setContentView(view);

        setContentView(R.layout.activity_main);

    }

    public void register(View view) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.sososeen.09.demo.my.receiver");
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    public void send(View view) {
        Intent intent = new Intent();
        intent.setAction("com.sososeen.09.demo.my.receiver");
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
