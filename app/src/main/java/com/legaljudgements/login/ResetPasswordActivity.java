package com.legaljudgements.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.legaljudgements.Utils.Constants.code_200;


/**
 * Created by sanjiv on 18/7/16.
 */
public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText et_password, et_cnfrm_password;
    private TextView tv_submit;
    private String deviceId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reset_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById();
        deviceId = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);

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

    private void findViewById() {
        et_password = (EditText) findViewById(R.id.reset_et_password);
        et_cnfrm_password = (EditText) findViewById(R.id.reset_et_confirm_password);
        tv_submit = (TextView) findViewById(R.id.reset_tv_submit);
        tv_submit.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.reset_tv_submit:
                fetchData();
                break;


        }
    }

    private void fetchData() {
        String str_pswrd = et_password.getText().toString();
        String str_cnfrm_pswrd = et_cnfrm_password.getText().toString();
        if (isValidated(deviceId, str_pswrd, str_cnfrm_pswrd)) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            WebServices.resetPassword(Utility.getPreferences(getApplicationContext(),Constants.UniqueDeviceId),
                    deviceId, str_pswrd, new JsonHttpResponseHandler() {
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

        }

    }

    private void parsePasswordResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals(code_200)) {
                Utility.alertDialog(this, Constants.reset_password_success_message);
            } else {
                Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidated(String deviceId, String str_pswrd, String str_cnfrm_pswrd) {

        if (deviceId.length() == 0) {
            Utility.showToast(getApplicationContext(), Constants.error_message_device_toke);
            return false;
        }

        if (str_pswrd.length() == 0) {
            Utility.showToast(getApplicationContext(), Constants.error_message_Password_mandatory);
            return false;
        }

        if (str_pswrd.length() < 5) {
            Utility.showToast(getApplicationContext(), Constants.error_message_Password_length);
            return false;
        }

        if (!str_cnfrm_pswrd.equals(str_pswrd)) {
            Utility.showToast(getApplicationContext(), Constants.error_message_psswrd_mismatch);
            return false;
        }


        return true;
    }

}
