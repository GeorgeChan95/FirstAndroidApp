package com.george.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.george.chapter05.watch.HideTextWatcher;

public class EditHideActivity extends AppCompatActivity {

    private EditText et_phone;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hide);

        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);

        et_phone.addTextChangedListener(new HideTextWatcher(EditHideActivity.this, et_phone, 11));
        et_password.addTextChangedListener(new HideTextWatcher(EditHideActivity.this, et_password, 6));
    }
}