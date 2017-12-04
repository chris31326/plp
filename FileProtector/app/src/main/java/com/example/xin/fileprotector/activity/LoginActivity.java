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
    private final AppCompatActivity activity = LoginActivity.this;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private AppCompatButton appCompatButtonLogin;
    private AppCompatTextView textViewLinkRegister;
    private InputValidation inputValidation;
    private DBHelper databaseHelper;

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
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
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
        if (!verifyFromSQLite()) {
            Toast.makeText(getBaseContext(), "Error: some fields are invalid", Toast.LENGTH_LONG).show();
            return;
        }

        final String email = textInputEditTextEmail.getText().toString().trim();
        final String password = textInputEditTextPassword.getText().toString();
        final String hashedPassword = Hashing.getHexString(password.trim());

        if (!databaseHelper.userTable.checkUser(email, hashedPassword)) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

            final String registeredEmail = databaseHelper.userTable.getRegisteredUserEmail();
            if (registeredEmail != null) {
                new GMailSendAsyncTask(
                        registeredEmail,
                        "unauthorized access",
                        "Someone tried to login as " + email + " and failed.")
                        .execute();
            }
            return;
        }

        final Intent accountsIntent = new Intent(activity, MainActivity.class);
        startActivity(accountsIntent);
        finish();
    }

    private boolean verifyFromSQLite() {
        boolean valid = true;

        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            valid = false;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            valid = false;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            valid = false;
        }

        return valid;
    }
}
