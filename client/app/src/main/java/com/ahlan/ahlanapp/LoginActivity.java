package com.ahlan.ahlanapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import commonLibrary.Message;
import commonLibrary.User;


/**
 * A login screen that offers sign up via name/password.
 */
public class LoginActivity extends AppCompatActivity {
    private AsyncTask<Void, Void, Boolean> mAuthTask = null;
    // UI references.
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mPhoneNumber;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        mPasswordView = (EditText) findViewById(R.id.password);
        mNameView = (EditText) findViewById(R.id.userName);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        Button mLoginButton = (Button) findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp(false);
            }
        });

        Button mSignUpButton = (Button) findViewById(R.id.registerButton);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp(true);
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progress);
    }


    /**
     * If type is true - Attempts to register the account specified by the login form.
     * If type us false - Attempts to log in specified by the login form.
     * If there are form errors (missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp(final boolean type) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);
        mPhoneNumber.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Validation.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user name.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!Validation.isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid user phone.
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumber.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumber;
            cancel = true;
        } else if (!Validation.isPhoneValid(phoneNumber)) {
            mPhoneNumber.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneNumber;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if (type) {


                //String mPhoneNumber = "0585259393";
                mAuthTask = new UserRegisterTask(name, password, phoneNumber);
                mAuthTask.execute((Void) null);
            }
            if (!type) {
                mAuthTask = new UserLoginTask(name, password, phoneNumber);
                mAuthTask.execute((Void) null);
            }
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous registration task used to register
     * the user.
     */
    protected class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;
        private final String mPhoneNumber;
        private String reasonOfFailure;

        UserRegisterTask(String name, String password, String phoneNumber) {
            mName = name;
            mPassword = password;
            mPhoneNumber = phoneNumber;
        }

        public void setFailure(String reasonOfFailure) {
            this.reasonOfFailure = reasonOfFailure;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return Network.getInstance().signUp(mName, mPassword, mPhoneNumber, this);
            } catch (Exception e) {

                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                MoveActivity(mPhoneNumber);
            } else {
                mPasswordView.setError(reasonOfFailure);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    protected class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;
        private final String mPhoneNumber;
        private String reasonOfFailure;


        UserLoginTask(String name, String password, String phoneNumber) {
            mName = name;
            mPassword = password;
            mPhoneNumber = phoneNumber;
        }

        public void setFailure(String reasonOfFailure) {
            this.reasonOfFailure = reasonOfFailure;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return Network.getInstance().signIn(mName, mPassword, mPhoneNumber, this);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                MoveActivity(mPhoneNumber);
            } else {
                mPasswordView.setError(reasonOfFailure);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void MoveActivity(String phoneNumber) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("phoneNumber", phoneNumber);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
