package com.example.xin.fileprotector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.example.xin.fileprotector.R;
import com.example.xin.fileprotector.db.DBHelper;
import com.example.xin.fileprotector.email.GMailSendAsyncTask;
import com.example.xin.fileprotector.util.Hashing;
import com.example.xin.fileprotector.util.InputValidation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private final AppCompatActivity activity = LoginActivity.this;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextPassword;
    private AppCompatButton appCompatButtonLogin;
    private AppCompatTextView textViewLinkRegister;
    private InputValidation inputValidation;
    private DBHelper databaseHelper;
    private int failedLoginAttempts;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);
        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);
    }

    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    private void initObjects() {
        databaseHelper = DBHelper.getInstance(activity);
        inputValidation = new InputValidation(activity);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                login();
                break;
            case R.id.textViewLinkRegister:
                if (databaseHelper.userTable.hasUsersAlready()) {
                    Toast.makeText(this, "Error: user already registered.", Toast.LENGTH_LONG).show();
                } else {
                    // Navigate to RegisterActivity
                    Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intentRegister);
                }
                break;
        }
    }

    public void login() {
        if (!validateInput()) {
            Toast.makeText(getBaseContext(), "Error: some fields are invalid", Toast.LENGTH_LONG).show();
            return;
        }

        final String password = textInputEditTextPassword.getText().toString();
        final String hashedPassword = Hashing.getHexString(password.trim());

        if (failedLoginAttempts >= MAX_LOGIN_ATTEMPTS || !databaseHelper.userTable.checkPassword(hashedPassword)) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

            if (failedLoginAttempts <= MAX_LOGIN_ATTEMPTS) {
                ++failedLoginAttempts;
            }

            if (failedLoginAttempts != MAX_LOGIN_ATTEMPTS) {
                return;
            }

            final String registeredEmail = databaseHelper.userTable.getRegisteredUserEmail();
            if (registeredEmail != null) {
                new GMailSendAsyncTask(
                        registeredEmail,
                        "unauthorized access",
                        "Someone tried to login at least five times and failed.")
                        .execute();
            }
            return;
        }

        final Intent accountsIntent = new Intent(activity, MainActivity.class);
        startActivity(accountsIntent);
        finish();
    }

    private boolean validateInput() {
        boolean valid = true;

        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            valid = false;
        }

        return valid;
    }
}
