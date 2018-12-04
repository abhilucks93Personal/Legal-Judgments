package com.legaljudgements.admin.dashboard.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.dashboard.model.UserDetailsModel;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.legaljudgements.Utils.Constants.address;
import static com.legaljudgements.Utils.Constants.email;
import static com.legaljudgements.Utils.Constants.isActive;
import static com.legaljudgements.Utils.Constants.membershipEndDate;
import static com.legaljudgements.Utils.Constants.membershipId;
import static com.legaljudgements.Utils.Constants.membershipStartDate;
import static com.legaljudgements.Utils.Constants.membershipValidity;
import static com.legaljudgements.Utils.Constants.name;
import static com.legaljudgements.Utils.Constants.password;
import static com.legaljudgements.Utils.Constants.phone;
import static com.legaljudgements.Utils.Constants.userId;
import static com.legaljudgements.Utils.Constants.userName;
import static com.legaljudgements.Utils.Constants.userType;

/**
 * Created by ng on 2/15/2017.
 */
public class UserDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    UserDetailsModel data;
    TextView tv_name, tv_title, tv_phone, tv_address, tv_access, tv_startDate, tv_endDate, tv_status;
    TextView tv_activate;
    private TextView tv_dialog_startDate, tv_dialog_endDate;
    TextView tv_activate_;
    private int dateTag = 0;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private String deviceId;
    private String strUserId;
    private String strStartDate = "", strEndDate = "";
    private boolean isChanged = false;
    private int pos;
    private String strUserUpdateId;
    private RadioButton rb_private, rb_public;
    private String str_scope = "true";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_details);

        findViewById();

        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.delete_user, menu);


        return true;
    }

    private void initData() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deviceId = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);
        strUserId = Utility.getPreferences(getApplicationContext(), Constants.userId);

        pos = getIntent().getIntExtra("pos", -1);

        data = (UserDetailsModel) getIntent().getSerializableExtra("data");
        if (data != null)
            setData();

        strUserUpdateId = getIntent().getStringExtra("id");
        if (strUserUpdateId != null)
            loadData();


    }

    private void loadData() {
        progressDialog = new ProgressDialog(UserDetails.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WebServices.getUserDetailById(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                deviceId, strUserUpdateId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();

                        //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        parseActivateUserResponse(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
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
    }

    private void setData() {
        setTitle(data.getUserName());
        str_scope = data.getScope();
        if (Boolean.parseBoolean(data.getIsActive())) {
            strStartDate = Utility.convertedDate(data.getMembershipStartDate());
            strEndDate = Utility.convertedDate(data.getMembershipEndDate());
        } else {
            strStartDate = Utility.getCurrentDate("MM-dd-yyyy");
            strEndDate = Utility.getDateAfterTime(Integer.parseInt(data.getMembershipValidity()), data.getUnit(), "MM-dd-yyyy");
        }

        tv_name.setText(data.getName());
        tv_title.setText(data.getTitle());
        tv_phone.setText(data.getPhone());
        tv_address.setText(data.getAddress());
        tv_access.setText(parseAccess(data.getScope()));
        tv_startDate.setText(Utility.formatDateForDisplay(Utility.convertedDate(data.getMembershipStartDate())));
        tv_endDate.setText(Utility.formatDateForDisplay(Utility.convertedDate(data.getMembershipEndDate())));
        tv_status.setText(getStatus(data));
        tv_activate.setText(getClickText(data.getIsActive()));

    }

    private String parseAccess(String scope) {
        if (scope.equals("true"))
            return "Yes";
        else
            return "No";
    }


    private String getStatus(UserDetailsModel userDetailsModel) {


        if (Utility.checkExpiry(Utility.convertedDate(userDetailsModel.getMembershipEndDate()))) {
            return "Term Expiry";
        } else if (Boolean.parseBoolean(userDetailsModel.getIsActive())) {
            return "Active";
        } else {
            return "DeActive";
        }
    }

    private String getClickText(String str) {

        switch (str) {
            case "true":
                return "Deactivate";

            case "false":
                return "Activate";

        }
        return null;
    }

    private void findViewById() {
        tv_name = (TextView) findViewById(R.id.user_detail_tv_name);
        tv_title = (TextView) findViewById(R.id.user_detail_tv_title);
        tv_phone = (TextView) findViewById(R.id.user_detail_tv_phone);
        tv_address = (TextView) findViewById(R.id.user_detail_tv_address);
        tv_access = (TextView) findViewById(R.id.user_detail_tv_full_access);
        tv_startDate = (TextView) findViewById(R.id.user_detail_tv_startDate);
        tv_endDate = (TextView) findViewById(R.id.user_detail_tv_endDate);
        tv_status = (TextView) findViewById(R.id.user_detail_tv_status);

        tv_activate = (TextView) findViewById(R.id.user_detail_tv_activate);
        tv_activate.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("data", data);
                intent.putExtra("pos", pos);
                intent.putExtra("tag", "other");
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.update_jm_action_delete:

                if (data != null) {
                    deleteUserDialog(UserDetails.this, "Are you sure you want to delete this user.");
                } else {
                    Utility.showToast(UserDetails.this, "Unable to delete!");
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteUser() {
        progressDialog = new ProgressDialog(UserDetails.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WebServices.deleteUser(data.getUserId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();

                parseDeleteUserResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
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

    }

    private void parseDeleteUserResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);

            if (statusCode.equals("200")) {
                Intent intent = new Intent();
                intent.putExtra("data", data);
                intent.putExtra("pos", pos);
                intent.putExtra("tag", "delete");
                setResult(RESULT_OK, intent);
                finish();

            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", data);
        intent.putExtra("pos", pos);
        intent.putExtra("tag", "other");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.user_detail_tv_activate:
                if (Boolean.parseBoolean(data.getIsActive()))
                    deactivationDialog(UserDetails.this, "Are you sure you want to Deactivate this user");

                else
                    activationDialog();
                break;

            case R.id.textView5:
                dateTag = 0;
                Utility.datePickerDialog(UserDetails.this, this);
                break;

            case R.id.textView6:
                dateTag = 1;
                Utility.datePickerDialog(UserDetails.this, this);
                break;

            case R.id.button_activate:
                strStartDate = Utility.formatDateForDisplayReverse(tv_dialog_startDate.getText().toString());
                strEndDate = Utility.formatDateForDisplayReverse(tv_dialog_endDate.getText().toString());
                if (alertDialog.isShowing())
                    alertDialog.dismiss();

                if (rb_public.isChecked())
                    str_scope = "true";
                else
                    str_scope = "false";
                activateUser(true, strStartDate, strEndDate);
                break;

        }
    }

    public void deactivationDialog(Activity activity, String message) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText(message);
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateUser(false, "", "");
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    public void deleteUserDialog(Activity activity, String message) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText(message);
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    private void activateUser(Boolean status, String startDate, String endDate) {
        isChanged = true;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebServices.activateUser(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                deviceId, data.getUserId(), strStartDate, strEndDate, "" + status, str_scope,
                strUserId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        // Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                        parseActivateUserResponse(response);

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
    }

    private void parseActivateUserResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);

            if (statusCode.equals("200")) {

                JSONArray dataResponse = response.getJSONArray(Constants.STATUS_DATA);
                if (dataResponse.length() > 0) {
                    for (int i = 0; i < dataResponse.length(); i++) {
                        JSONObject mData = dataResponse.getJSONObject(i);

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
                        String str_membershipValidity = mData.getString(membershipValidity);
                        String str_access = mData.getString("fullAccess");
                        String str_title = mData.getString(Constants.membershipTitle);
                        String str_unit = mData.getString("validityUnit");

                        data = new UserDetailsModel();
                        data.setUserId(str_userId);
                        data.setUserName(str_userName);
                        data.setPassword(str_password);
                        data.setName(str_name);
                        data.setEmail(str_email);
                        data.setPhone(str_phone);
                        data.setAddress(str_address);
                        data.setMembershipId(str_membershipId);
                        data.setIsActive(str_isActive);
                        data.setMembershipStartDate(str_membershipStartDate);
                        data.setMembershipEndDate(str_membershipEndDate);
                        data.setUserType(str_userType);
                        data.setMembershipValidity(str_membershipValidity);
                        data.setScope(str_access);
                        data.setUnit(str_unit);
                        data.setTitle(str_title);
                        setData();

                    }
                }
            }
        } catch (JSONException e) {

        }
    }


    private void activationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_activate_user, null);
        dialogBuilder.setView(v);

        tv_dialog_startDate = (TextView) v.findViewById(R.id.textView5);
        tv_dialog_startDate.setText(Utility.formatDateForDisplay(strStartDate));
        tv_dialog_startDate.setOnClickListener(this);
        tv_dialog_endDate = (TextView) v.findViewById(R.id.textView6);
        tv_dialog_endDate.setText(Utility.formatDateForDisplay(strEndDate));
        tv_dialog_endDate.setOnClickListener(this);
        rb_private = (RadioButton) v.findViewById(R.id.full_access_private);
        rb_public = (RadioButton) v.findViewById(R.id.full_access_public);

        if (Boolean.parseBoolean(str_scope)) {
            rb_public.setChecked(true);
        } else {
            rb_private.setChecked(true);
        }
        tv_activate_ = (TextView) v.findViewById(R.id.button_activate);
        tv_activate_.setOnClickListener(this);

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String date = "" + (view.getMonth() + 1)
                + "-" + view.getDayOfMonth()
                + "-" + view.getYear();

        date = Utility.formatDateFromString("mm-dd-yyyy", "mm-dd-yyyy", date);

        if (dateTag == 0) {
            strStartDate = date;
            tv_dialog_startDate.setText(Utility.formatDateForDisplay(date));
        } else {
            strEndDate = date;
            tv_dialog_endDate.setText(Utility.formatDateForDisplay(date));
        }

    }
}
