package com.example.xin.fileprotector.email;

import android.os.AsyncTask;

public class GMailSendAsyncTask extends AsyncTask<Void, Void, Void> {
    private final String recipient;
    private final String subject;
    private final String message;

    public GMailSendAsyncTask(String recipient, String subject, String message) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Void doInBackground(final Void... voids) {
        GMailSMTP.sendEmail(recipient, subject, message);
        return null;
    }
}
