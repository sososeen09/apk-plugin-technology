package com.sososeen09.binder.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sososeen09.binder.hook.utils.SPHelper;

public class LoginActivity extends AppCompatActivity {

    TextView tvOrigin;

    EditText username;
    EditText password;
    private String mClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvOrigin = findViewById(R.id.tv_origin_target);
        username = findViewById(R.id.et_name);
        password = findViewById(R.id.et_password);
        Intent realWantedIntent = getIntent().getParcelableExtra(HookUtils.MREAL_WANTED_INTENT);

        if (realWantedIntent != null) {
            mClassName = realWantedIntent.getComponent().getClassName();
            tvOrigin.setText(mClassName);
        }
    }


    public void login(View view) {
        if ((username.getText() == null || password.getText() == null)) {
            Toast.makeText(this, "username and password must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        SPHelper.setBoolean("login", true);
        if (mClassName != null) {
            ComponentName componentName = new ComponentName(this, mClassName);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            startActivity(intent);
            finish();
        }

    }
}
