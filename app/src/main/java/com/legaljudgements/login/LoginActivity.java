package com.legaljudgements.login;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.navigation.NavigationAdminActivity;
import com.legaljudgements.lawyer.NavigationLawyerActivity;
import com.legaljudgements.lawyer.membership.MembershipConfirmationActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.legaljudgements.Utils.Constants.ADMIN;
import static com.legaljudgements.Utils.Constants.DEVICE_CHANGED;
import static com.legaljudgements.Utils.Constants.LAWYER;
import static com.legaljudgements.Utils.Constants.address;
import static com.legaljudgements.Utils.Constants.chamber;
import static com.legaljudgements.Utils.Constants.code_200;
import static com.legaljudgements.Utils.Constants.code_404;
import static com.legaljudgements.Utils.Constants.code_502;
import static com.legaljudgements.Utils.Constants.code_503;
import static com.legaljudgements.Utils.Constants.email;
import static com.legaljudgements.Utils.Constants.isActive;
import static com.legaljudgements.Utils.Constants.membershipEndDate;
import static com.legaljudgements.Utils.Constants.membershipId;
import static com.legaljudgements.Utils.Constants.membershipStartDate;
import static com.legaljudgements.Utils.Constants.membershipTitle;
import static com.legaljudgements.Utils.Constants.name;
import static com.legaljudgements.Utils.Constants.password;
import static com.legaljudgements.Utils.Constants.phone;
import static com.legaljudgements.Utils.Constants.userId;
import static com.legaljudgements.Utils.Constants.userName;
import static com.legaljudgements.Utils.Constants.userType;

/**
 * Created by ng on 1/28/2017.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_submit, tv_forgot_psswrd, tv_userType, tv_officeUse;
    int user_type = LAWYER;
    private EditText et_userName, et_password;
    ProgressDialog progressDialog;
    ImageView iv_eye;
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String deviceId;
    boolean gcmTokenRecieved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gcmTokenRecieved = false;
        String device_id = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);

        if (device_id != null) {
            if (device_id.length() > 0) {
                gcmTokenRecieved = true;
                deviceId = device_id;
            } else {
                registerGcm();
            }
        } else {
            registerGcm();
        }
        findViewById();

        deviceId = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);
    }

    private void registerGcm() {


        String token = FirebaseInstanceId.getInstance().getToken();
        deviceId = token;
        gcmTokenRecieved = true;
        Utility.addPreferences(getApplicationContext(), Constants.DEVICE_ID, token);


    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("Login", "onResume");

    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("Login", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_submit:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                getToken();

                break;

            case R.id.psswrd_eye:
                Utility.password_visible(LoginActivity.this, et_password, iv_eye);
                break;

            case R.id.login_tv_userType:
                if (user_type == LAWYER) {
                    tv_forgot_psswrd.setVisibility(View.GONE);
                    tv_userType.setText("Login as User");
                    tv_officeUse.setVisibility(View.VISIBLE);
                    setTitle("Login (Admin)");
                    user_type = ADMIN;
                } else if (user_type == ADMIN) {
                    tv_forgot_psswrd.setVisibility(View.VISIBLE);
                    tv_userType.setText("Login as Admin");
                    tv_officeUse.setVisibility(View.GONE);
                    user_type = LAWYER;
                    setTitle("Login");
                }
                break;

            case R.id.login_tv_forgot_psswrd:
                if (gcmTokenRecieved) {
                    forgotPassword();
                } else {
                    Utility.showToast(getApplicationContext(), Constants.error_message_device_toke);
                }
                break;
        }
    }

    private void getToken() {
        if (gcmTokenRecieved) {
            if (progressDialog != null)
                progressDialog.dismiss();
            fetchData();
        } else {
            registerGcm();
            getToken();
        }
    }

    private void forgotPassword() {
        if (deviceId.length() > 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            WebServices.forgotPassword(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                    deviceId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            progressDialog.dismiss();
                            parsePasswordResponse(response);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            progressDialog.dismiss();
                            Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            progressDialog.dismiss();
                            Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            progressDialog.dismiss();
                            Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                        }
                    });
        } else {
            Utility.showToast(getApplicationContext(), Constants.error_message_device_toke);
        }
    }

    private void parsePasswordResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals(code_200)) {
                Utility.alertDialog(this, Constants.reset_password_message);
            } else if (statusCode.equals(code_404)) {
                Utility.showSnackBar(this, Constants.error_message_device_not_registered);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        iv_eye = (ImageView) findViewById(R.id.psswrd_eye);
        iv_eye.setOnClickListener(this);
        et_userName = (EditText) findViewById(R.id.login_et_userName);
        et_password = (EditText) findViewById(R.id.login_et_password);
        tv_forgot_psswrd = (TextView) findViewById(R.id.login_tv_forgot_psswrd);
        tv_forgot_psswrd.setOnClickListener(this);
        tv_submit = (TextView) findViewById(R.id.login_tv_submit);
        tv_submit.setOnClickListener(this);
        tv_userType = (TextView) findViewById(R.id.login_tv_userType);
        tv_userType.setOnClickListener(this);
        tv_officeUse = (TextView) findViewById(R.id.login_tv_officeUse);
        tv_officeUse.setOnClickListener(this);
    }

    private void fetchData() {
        String userName = et_userName.getText().toString();
        String password = et_password.getText().toString();
        if (isValidated(userName, password))
            if (Utility.isInternetConnected(this))
                userLogin(userName, password);
    }

    private boolean isValidated(String userName, String password) {

        if (userName.length() == 0) {
            Utility.showSnackBar(this, "Username can't be blank");
            return false;
        }
        if (password.length() == 0) {
            Utility.showSnackBar(this, "Password can't be blank");
            return false;
        }

        if (password.length() < Constants.PASSWORD_MIN_LENGTH) {
            Utility.showSnackBar(this, "Password should be of min. 5 characters");
            return false;
        }

        return true;
    }

    private void userLogin(String userName, String password) {
        Utility.hideKeyboard(this);
        if (deviceId.length() > 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            WebServices.userLogin(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                    deviceId, userName, password, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            progressDialog.dismiss();
                            // Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                            parseLoginResponse(response);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Utility.showToast(getApplicationContext(), Constants.error_message_device_toke);
        }
    }

    private void parseLoginResponse(JSONObject response) {

        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            String message = response.getString(Constants.STATUS_MESSAGE);
            String content = response.getString("content");
            Utility.addPreferences(getApplicationContext(), Constants.ConfirmationContent, content);
            if (message.equals("false") || message.equals("true"))
                Utility.addPreferences(getApplicationContext(), "FullAccess", Boolean.parseBoolean(message));
            if (statusCode.equals(Constants.code_200)) {
                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);

                        String str_userId = mData.getString(userId);
                        String str_userName = mData.getString(userName);
                        String str_password = mData.getString(password);
                        String str_name = mData.getString(name);
                        String str_email = mData.getString(email);
                        String str_phone = mData.getString(phone);
                        String str_address = mData.getString(address);
                        String str_membershipId = mData.getString(membershipId);
                        String str_isActive = mData.getString(isActive);
                        String str_membershipStartDate = mData.getString(membershipStartDate);
                        String str_membershipEndDate = mData.getString(membershipEndDate);
                        String str_userType = mData.getString(userType);
                        String str_membershipTitle = mData.getString(membershipTitle);

                        Utility.addPreferences(getApplicationContext(), userId, str_userId);
                        Utility.addPreferences(getApplicationContext(), userName, str_userName);
                        Utility.addPreferences(getApplicationContext(), password, str_password);
                        Utility.addPreferences(getApplicationContext(), name, str_name);
                        Utility.addPreferences(getApplicationContext(), email, str_email);
                        Utility.addPreferences(getApplicationContext(), phone, str_phone);
                        Utility.addPreferences(getApplicationContext(), address, str_address);
                        Utility.addPreferences(getApplicationContext(), membershipId, str_membershipId);
                        Utility.addPreferences(getApplicationContext(), isActive, str_isActive);
                        Utility.addPreferences(getApplicationContext(), membershipEndDate, str_membershipEndDate);
                        Utility.addPreferences(getApplicationContext(), membershipStartDate, str_membershipStartDate);
                        Utility.addPreferences(getApplicationContext(), userType, str_userType);
                        Utility.addPreferences(getApplicationContext(), membershipTitle, str_membershipTitle);
                        Utility.addPreferences(getApplicationContext(), Constants.MembershipExpired, false);
                        if (str_userType.equalsIgnoreCase("ADMIN")) {
                            startActivity(new Intent(LoginActivity.this, NavigationAdminActivity.class));
                        } else if (str_userType.equalsIgnoreCase("USER")) {
                            startActivity(new Intent(LoginActivity.this, NavigationLawyerActivity.class));
                        }
                        finishAffinity();
                    }
                }
            } else if (statusCode.equals(Constants.code_800)) {
                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);

                        String str_userId = mData.getString(userId);
                        String str_userName = mData.getString(userName);
                        String str_password = mData.getString(password);
                        String str_name = mData.getString(name);
                        String str_email = mData.getString(email);
                        String str_phone = mData.getString(phone);
                        String str_address = mData.getString(address);
                        String str_membershipId = mData.getString(membershipId);
                        String str_isActive = mData.getString(isActive);
                        String str_membershipStartDate = mData.getString(membershipStartDate);
                        String str_membershipEndDate = mData.getString(membershipEndDate);
                        String str_userType = mData.getString(userType);
                        String str_membershipTitle = mData.getString(membershipTitle);

                        Utility.addPreferences(getApplicationContext(), userId, str_userId);
                        Utility.addPreferences(getApplicationContext(), userName, str_userName);
                        Utility.addPreferences(getApplicationContext(), password, str_password);
                        Utility.addPreferences(getApplicationContext(), name, str_name);
                        Utility.addPreferences(getApplicationContext(), email, str_email);
                        Utility.addPreferences(getApplicationContext(), phone, str_phone);
                        Utility.addPreferences(getApplicationContext(), address, str_address);
                        Utility.addPreferences(getApplicationContext(), membershipId, str_membershipId);
                        Utility.addPreferences(getApplicationContext(), isActive, str_isActive);
                        Utility.addPreferences(getApplicationContext(), membershipEndDate, str_membershipEndDate);
                        Utility.addPreferences(getApplicationContext(), membershipStartDate, str_membershipStartDate);
                        Utility.addPreferences(getApplicationContext(), userType, str_userType);
                        Utility.addPreferences(getApplicationContext(), membershipTitle, str_membershipTitle);
                        Utility.addPreferences(getApplicationContext(), Constants.MembershipExpired, true);

                        if (str_userType.equalsIgnoreCase("ADMIN")) {
                            startActivity(new Intent(LoginActivity.this, NavigationAdminActivity.class));
                        } else if (str_userType.equalsIgnoreCase("USER")) {
                            startActivity(new Intent(LoginActivity.this, NavigationLawyerActivity.class));
                        }
                        finishAffinity();
                    }
                }

            } else if (statusCode.equals(code_502)) {
                startActivity(new Intent(LoginActivity.this, MembershipConfirmationActivity.class)
                        .putExtra("code", statusCode));
                finishAffinity();
            } else if (statusCode.equals(code_404)) {
                Utility.showSnackBar(this, Constants.error_message_invalid_user);
            } else if (statusCode.equals(code_503)) {
                Utility.alertDialog(this, DEVICE_CHANGED);
            }
        } catch (
                JSONException e
                )

        {
            e.printStackTrace();
        }


    }

}