package com.legaljudgements.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.navigation.NavigationAdminActivity;
import com.legaljudgements.lawyer.membership.MembershipConfirmationActivity;
import com.legaljudgements.lawyer.membership.SelectMembershipActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.legaljudgements.Utils.Constants.address;
import static com.legaljudgements.Utils.Constants.code_502;
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
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_user_name, et_psswrd, et_confirm_psswrd, et_address, et_num;
    TextView tv_submit;
    private ProgressDialog progressDialog;
    private String memberShipId = null, memberShipTitle = "", memberShipPrice = "";
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String deviceId;
    boolean gcmTokenRecieved = false;
    private ImageView iv_eye;
    private ImageView iv_eye2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    }

    private void findViewById() {
        iv_eye = (ImageView) findViewById(R.id.psswrd_eye);
        iv_eye.setOnClickListener(this);
        iv_eye2 = (ImageView) findViewById(R.id.psswrd_eye2);
        iv_eye2.setOnClickListener(this);
        et_user_name = (EditText) findViewById(R.id.signup_et_user_name);
        et_psswrd = (EditText) findViewById(R.id.signup_et_psswrd);
        et_confirm_psswrd = (EditText) findViewById(R.id.signup_et_confirm_psswrd);
        et_num = (EditText) findViewById(R.id.signup_et_num);
       /* et_name = (EditText) findViewById(R.id.signup_et_name);

        et_email = (EditText) findViewById(R.id.signup_et_email);*/
        et_address = (EditText) findViewById(R.id.signup_et_address);


        tv_submit = (TextView) findViewById(R.id.signup_tv_submit);
        tv_submit.setOnClickListener(this);

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

            case R.id.psswrd_eye:
                Utility.password_visible(SignUpActivity.this, et_psswrd, iv_eye);
                break;

            case R.id.psswrd_eye2:
                Utility.password_visible(SignUpActivity.this, et_confirm_psswrd, iv_eye2);
                break;

            case R.id.signup_tv_submit:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                getToken();
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

    private void fetchData() {
        String strUserName = et_user_name.getText().toString().trim();
        String strpassword = et_psswrd.getText().toString().trim();
        String strConfirmPassword = et_confirm_psswrd.getText().toString().trim();
        String strName = "";
        String strNum = et_num.getText().toString().trim();
        String strEmail = "";
        String strAddress = et_address.getText().toString().trim();


        if (isValidated(strUserName, strpassword, strConfirmPassword, strName, strNum, strEmail, strAddress)) {
            if (memberShipId == null)
                startActivityForResult(new Intent(SignUpActivity.this, SelectMembershipActivity.class), 300);
            else
                showPackageConfirmationDialog(memberShipTitle, memberShipPrice, strUserName, strpassword, strName, strNum, strEmail, strAddress, memberShipId);
            // userSignUp(strUserName, strpassword, strName, strNum, strEmail, strAddress, memberShipId);

        }

    }


    private void registerGcm() {


        String token = FirebaseInstanceId.getInstance().getToken();
        deviceId = token;
        Utility.addPreferences(getApplicationContext(), Constants.DEVICE_ID, token);

        // Gcm removed


        //Initializing our broadcast receiver
      /*  mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    gcmTokenRecieved = true;
                    String token = intent.getStringExtra("token");
                    deviceId = token;
                    Utility.addPreferences(getApplicationContext(), Constants.DEVICE_ID, token);
                    //Displaying the token as toast
                    // Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().contains(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    gcmTokenRecieved = false;
                    Toast.makeText(getApplicationContext(), "GCM registration error!\n" + intent.getAction(), Toast.LENGTH_LONG).show();
                } else {
                    gcmTokenRecieved = false;
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        }*/

    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();

        // Gcm removed

      /*  Log.w("Login", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));*/
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("Login", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            memberShipId = data.getStringExtra("id");
            memberShipTitle = data.getStringExtra("title");
            memberShipPrice = data.getStringExtra("price");
            String strUserName = et_user_name.getText().toString().trim();
            String strpassword = et_psswrd.getText().toString().trim();
            String strConfirmPassword = et_confirm_psswrd.getText().toString().trim();
            String strName = "";
            String strNum = et_num.getText().toString().trim();
            String strEmail = "";
            String strAddress = et_address.getText().toString().trim();


            if (isValidated(strUserName, strpassword, strConfirmPassword, strName, strNum, strEmail, strAddress)) {
                if (memberShipId != null)
                    showPackageConfirmationDialog(memberShipTitle, memberShipPrice, strUserName, strpassword, strName, strNum, strEmail, strAddress, memberShipId);
                //  userSignUp(strUserName, strpassword, strName, strNum, strEmail, strAddress, memberShipId);

            }
        }
    }

    private void userSignUp(String strUserName, String strpassword, String strName, String strNum, String strEmail, String strAddress, String strMembershipId) {

        if (deviceId.length() > 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            WebServices.userSignUp(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                    deviceId, strUserName, strpassword, strName, strEmail, strNum,
                    strAddress, strMembershipId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            progressDialog.dismiss();
                            //   Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                            parseSignUpResponse(response);
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
                            Toast.makeText(getApplicationContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Toast.makeText(getApplicationContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Utility.showToast(getApplicationContext(), Constants.error_message_device_toke);
        }
    }

    private void showPackageConfirmationDialog(String title, String Price, final String strUserName, final String strpassword, final String strName, final String strNum, final String strEmail, final String strAddress, String strMembershipId) {

        final Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout3);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText("You had selected " + title + " Package");
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        TextView tv_discard = (TextView) dialog.findViewById(R.id.common_dialog_discard);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignUp(strUserName, strpassword, strName, strNum, strEmail, strAddress, memberShipId);
                dialog.dismiss();

            }
        });
        tv_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberShipId = null;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void parseSignUpResponse(JSONObject response) {


        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            String content = response.getString("content");
            Utility.addPreferences(getApplicationContext(), Constants.ConfirmationContent, content);
            if (statusCode.equals("200")) {

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

                        if (str_userType.equalsIgnoreCase("ADMIN")) {
                            startActivity(new Intent(SignUpActivity.this, NavigationAdminActivity.class));
                        } else if (str_userType.equalsIgnoreCase("USER")) {
                            startActivity(new Intent(SignUpActivity.this, MembershipConfirmationActivity.class)
                                    .putExtra("code", code_502));
                        }
                        finishAffinity();
                    }
                }
            } else if (statusCode.equals("400")) {
                Utility.showSnackBar(this, "Username is already taken");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private boolean isValidated(String strUserName, String strpassword, String strConfirmPassword, String strName, String strNum, String strEmail, String strAddress) {

        if (strUserName.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (strpassword.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (strpassword.length() < Constants.PASSWORD_MIN_LENGTH) {
            Utility.showSnackBar(this, "Password should be of min. 5 characters");
            return false;
        }

        if (strConfirmPassword.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }
        if (!strConfirmPassword.equals(strpassword)) {
            Utility.showSnackBar(this, "Password and Confirm Password mismatch");
            return false;
        }

        if (strNum.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (strAddress.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        return true;
    }

}
