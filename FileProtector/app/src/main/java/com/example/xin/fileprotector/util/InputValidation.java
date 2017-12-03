package com.example.xin.fileprotector.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class InputValidation {

    private final Context context;

    public InputValidation(final Context context) {
        this.context = context;
    }

    public boolean isInputEditTextFilled(
            final TextInputEditText textInputEditText,
            final TextInputLayout textInputLayout,
            final String message) {
        final String value = textInputEditText.getText().toString().trim();

        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isInputEditTextEmail(
            final TextInputEditText textInputEditText,
            final TextInputLayout textInputLayout,
            final String message) {
        final String value = textInputEditText.getText().toString().trim();

        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isInputEditTextMatches(
            final TextInputEditText textInputEditText1,
            final TextInputEditText textInputEditText2,
            final TextInputLayout textInputLayout,
            final String message) {
        final String value1 = textInputEditText1.getText().toString().trim();
        final String value2 = textInputEditText2.getText().toString().trim();

        if (!value1.contentEquals(value2)) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText2);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public void setManualError(final TextInputEditText editText, final TextInputLayout layout, final String message) {
        layout.setError(message);
        hideKeyboardFrom(editText);
    }

    private void hideKeyboardFrom(final View view) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
