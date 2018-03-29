package com.legaljudgements.admin.membership.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.membership.model.HeaderInfo;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ng on 2/8/2017.
 */
public class CreateMembershipActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_submit;
    EditText et_title, et_description, et_months, et_price;
    private ProgressDialog progressDialog;
    private String deviceId;
    private String userId;
    String membershipId;
    Boolean editMode = false;
    ArrayList<HeaderInfo> MembershipsList;
    private String str_scope = "true";
    private RadioButton rb_day, rb_month;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_membership);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById();
        getData();
        deviceId = Utility.getPreferences(this, Constants.DEVICE_ID);
        userId = Utility.getPreferences(this, Constants.userId);
        userId = "1";
    }

    private void getData() {
        HeaderInfo data = getIntent().getParcelableExtra("data");
        if (data != null) {
            editMode = true;
            setTitle("Edit Membership");
            membershipId = data.getId();
            et_title.setText(data.getTitle());
            et_description.setText(data.getDescription());
            et_months.setText(data.getDuration());
            et_price.setText(data.getPrice());
            str_scope = "" + data.getScope();
            if (data.getUnit().equals("MONTH")) {
                rb_month.setChecked(true);
            } else {
                rb_day.setChecked(true);
            }

        }
    }


    private void findViewById() {
        et_title = (EditText) findViewById(R.id.create_membership_et_title);
        et_description = (EditText) findViewById(R.id.create_membership_et_description);
        et_months = (EditText) findViewById(R.id.create_membership_et_duration);
        et_price = (EditText) findViewById(R.id.create_membership_et_price);
        tv_submit = (TextView) findViewById(R.id.create_membership_tv_submit);
        tv_submit.setOnClickListener(this);

        rb_day = (RadioButton) findViewById(R.id.rb_day);
        rb_month = (RadioButton) findViewById(R.id.rb_month);
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
            case R.id.create_membership_tv_submit:
                fetchData();
                break;
        }
    }

    private void fetchData() {
        String strTitle = et_title.getText().toString().trim();
        String strDesc = et_description.getText().toString().trim();
        String strMonth = et_months.getText().toString().trim();
        String strPrice = et_price.getText().toString().trim();
        String ValidityUnit = "DAY";
        if (rb_day.isChecked())
            ValidityUnit = "DAY";
        else if (rb_month.isChecked())
            ValidityUnit = "MONTH";

        if (isValidated(strTitle, strDesc, strMonth, strPrice)) {

            //addMembership(strTitle, strDesc, strMonth, strPrice);
            showScopeDialog(strTitle, strDesc, strMonth, strPrice, ValidityUnit);

        }
    }

    private void showScopeDialog(final String strTitle, final String strDesc, final String strMonth, final String strPrice, final String validityUnit) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.scope_dialog_layout);
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.scope_rg);
        RadioButton rb_private = (RadioButton) dialog.findViewById(R.id.rb_private);
        if (editMode && str_scope.equals("false")) {
            rb_private.setChecked(true);
        }
        TextView tv_submit = (TextView) dialog.findViewById(R.id.scope_tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getCheckedRadioButtonId() == R.id.rb_public)
                    str_scope = "true";
                else if (rg.getCheckedRadioButtonId() == R.id.rb_private)
                    str_scope = "false";
                dialog.dismiss();
                addMembership(strTitle, strDesc, strMonth, strPrice, validityUnit);
            }
        });
        dialog.show();
    }

    private void addMembership(String strTitle, String strDesc, String strMonth, String strPrice, String validityUnit) {
        Utility.hideKeyboard(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        WebServices.addUpdateMembership(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId), editMode, deviceId, strTitle, strDesc, strMonth, strPrice, "xyz", membershipId, userId, str_scope, validityUnit, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
               // Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                if (editMode)
                    parseEditResponse(response);
                else
                    parseAddResponse(response);

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

    private void parseEditResponse(JSONObject response) {
        try {

            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                HeaderInfo header = new HeaderInfo();
                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {

                    JSONObject mData = data.getJSONObject(0);

                    header.setTitle(mData.getString("title"));
                    header.setId(mData.getString("membershipId"));
                    header.setDescription(mData.getString("description"));
                    header.setDuration(mData.getString("validityInMonths"));
                    header.setPrice(mData.getString("price"));
                    header.setUnit(mData.getString("validityUnit"));

                }
                Intent intent = new Intent();
                intent.putExtra("data", header);
                intent.putExtra("tag", "edit");
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Utility.showSnackBar(this, "Something went wrong! Please try after some time.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseAddResponse(JSONObject response) {
        try {
            MembershipsList = new ArrayList<>();
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {

                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);

                        HeaderInfo header = new HeaderInfo();
                        header.setTitle(mData.getString("title"));
                        header.setId(mData.getString("membershipId"));
                        header.setDescription(mData.getString("description"));
                        header.setDuration(mData.getString("validityInMonths"));
                        header.setPrice(mData.getString("price"));
                        header.setUnit(mData.getString("validityUnit"));
                        MembershipsList.add(header);

                    }

                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("data", MembershipsList);
                intent.putExtra("tag", "add");
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Utility.showSnackBar(this, "Something went wrong! Please try after some time.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean isValidated(String strTitle, String strDesc, String strMonth, String strPrice) {
        if (strTitle.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (strDesc.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (strPrice.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (strMonth.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }


        return true;
    }
}
