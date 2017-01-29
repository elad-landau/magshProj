package com.ahlan.ahlanapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.*;

/**
 * A login screen that offers login via name/password.
 */
public class LoginActivity extends AppCompatActivity {
//קקי
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mNameView;
    private EditText mPasswordView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mNameView = (EditText) findViewById(R.id.name);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attemptLogin()){

                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid name, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean attemptLogin() {
        if (mAuthTask != null) {
            return false;
        }

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(name, password);
            mAuthTask.execute((Void) null);
            return mAuthTask.getSucsses();
        }
    }

    private boolean isNameValid(String name) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9|_]");
        return !p.matcher(name).find() && name.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9|_]");
        return !p.matcher(password).find() && password.length() >= 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;
        private boolean mSucceeded;

        UserLoginTask(String name, String password) {
            mName = name;
            mPassword = password;
            mSucceeded = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            /*try {
                //Network access.
                // TODO: לממש את החרא הזה!!.
                return network.singinMessage(name, password);
            } catch () {

            }*/
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                mSucceeded = success;
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                mSucceeded = success;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        public boolean getSucsses()
        {
            return mSucceeded;
        }
    }
}

