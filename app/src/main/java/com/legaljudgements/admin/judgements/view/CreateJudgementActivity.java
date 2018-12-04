package com.legaljudgements.admin.judgements.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.judgements.model.JudgementModel;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ng on 2/18/2017.
 */
public class CreateJudgementActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    String deviceId, userId;
    EditText et_title, et_short_desc, et_case_title, et_case_type, et_judgement_type, et_court;
    TextView tv_date, tv_submit;
    boolean editMode = false;
    ProgressDialog progressDialog;
    String str_doc = "null", str_drive_url = "null", str_scope = "true", str_judgement_id = "null";
    String str_full_desc = "";
    TextView tv_add_desc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_judgement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById();
        getData();
        deviceId = Utility.getPreferences(this, Constants.DEVICE_ID);
        userId = Utility.getPreferences(this, Constants.userId);
        if (userId.length() == 0)
            userId = "1";
    }

    private void getData() {
        JudgementModel data = (JudgementModel) getIntent().getParcelableExtra("data");
        if (data == null)
            data = Constants.tempJudgmentModel;

        if (data != null) {
            editMode = true;
            setTitle("Edit Judgement");

            tv_submit.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_add_desc.setText("Edit Judgement");

            str_scope = "" + data.getPublic();
            str_full_desc = data.getDetailedDescription();
            str_judgement_id = data.getJudgementId();
            et_title.setText(data.getTitle());
            et_judgement_type.setText(data.getJudgementType());
            et_short_desc.setText(data.getShortDescription());
            et_case_title.setText(data.getCaseTitle());
            et_case_type.setText(data.getCaseType());
            et_court.setText(data.getCourt());
            tv_date.setText(Utility.formatDateForDisplay(data.getDateOfJudgement()));
        }
    }

    private void findViewById() {
        et_title = (EditText) findViewById(R.id.create_judgements_et_title);
        et_short_desc = (EditText) findViewById(R.id.create_judgements_et_short_desc);
        et_case_title = (EditText) findViewById(R.id.create_judgements_et_case_title);
        et_case_type = (EditText) findViewById(R.id.create_judgements_et_case_type);
        et_judgement_type = (EditText) findViewById(R.id.create_judgements_et_judgement_type);
        et_court = (EditText) findViewById(R.id.create_judgements_et_court);
        tv_date = (TextView) findViewById(R.id.create_judgements_et_date);
        tv_date.setOnClickListener(this);
        tv_submit = (TextView) findViewById(R.id.create_judgements_tv_submit);
        tv_submit.setOnClickListener(this);
        tv_add_desc = (TextView) findViewById(R.id.create_judgements_tv_add_desc);
        tv_add_desc.setOnClickListener(this);
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
            case R.id.create_judgements_tv_submit:
                fetchData();
                break;

            case R.id.create_judgements_et_date:
                Utility.datePickerDialog(CreateJudgementActivity.this, this);
                break;

            case R.id.create_judgements_tv_add_desc:
                try {
                    startActivityForResult(new Intent(CreateJudgementActivity.this, AddJudgementDescriptionActivity.class)
                            .putExtra("desc", str_full_desc), 100);
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                    Constants.tempDetailedDescription = str_full_desc;
                    startActivityForResult(new Intent(CreateJudgementActivity.this, AddJudgementDescriptionActivity.class), 100);
                }
                break;
        }
    }

    private void fetchData() {
        //String str_title = et_title.getText().toString().trim();
        String str_title = "";
        String str_short_desc = et_short_desc.getText().toString().trim();
        String str_case_title = et_case_title.getText().toString().trim();
        String str_case_type = "null";
        String str_judgement_type = et_judgement_type.getText().toString().trim();
        String str_court = "null";
        String str_date = "Mar 25, 2017";

        if (isValidated(str_title, str_short_desc, str_full_desc, str_case_title, str_case_type, str_judgement_type, str_court, str_date)) {
            showScopeDialog(str_title, str_short_desc, str_full_desc, str_case_title, str_case_type, str_judgement_type, str_court, str_date);

        }
    }

    private void showScopeDialog(final String str_title, final String str_short_desc, final String str_full_desc, final String str_case_title, final String str_case_type, final String str_judgement_type, final String str_court, final String str_date) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.scope_dialog_layout);
        TextView tv_title = (TextView) dialog.findViewById(R.id.scope_dialog_tv_title);
        tv_title.setText("Scope");
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.scope_rg);
        RadioButton rb_private = (RadioButton) dialog.findViewById(R.id.rb_private);
        rb_private.setText("Private");
        RadioButton rb_public = (RadioButton) dialog.findViewById(R.id.rb_public);
        rb_public.setText("Public");
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
                createJudgement(str_title, str_short_desc, str_full_desc, str_case_title, str_case_type, str_judgement_type, str_court, Utility.formatDateForDisplayReverse(str_date));
            }
        });
        dialog.show();
    }

    private void createJudgement(String str_title, String str_short_desc, String str_full_desc, String str_case_title, String str_case_type, String str_judgement_type, String str_court, String str_date) {
        Utility.hideKeyboard(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        WebServices.addUpdateJudgement(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId), editMode, deviceId, str_title, str_court, str_judgement_type, str_date, str_case_title, str_case_type, str_short_desc, str_full_desc, str_doc, str_drive_url, str_scope, userId, str_judgement_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
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
                JudgementModel judgementModel = new JudgementModel();
                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {

                    JSONObject mData = data.getJSONObject(0);

                    String judgementId = mData.getString(Constants.judgementId);
                    String title = mData.getString(Constants.title);
                    String court = mData.getString(Constants.court);
                    String judgementType = mData.getString(Constants.judgementType);
                    String dateOfJudgement = Utility.convertedDate(mData.getString(Constants.dateOfJudgement));
                    String caseTitle = mData.getString(Constants.caseTitle);
                    String caseType = mData.getString(Constants.caseType);
                    String shortDescription = mData.getString(Constants.shortDescription);
                    String detailedDescription = mData.getString(Constants.detailedDescription);
                    String judgementDoc = mData.getString(Constants.judgementDoc);
                    String googleDriveUrl = mData.getString(Constants.googleDriveUrl);
                    Boolean isPublic = mData.getBoolean(Constants.isPublic);
                    Boolean isActive = mData.getBoolean(Constants.isActive);
                    Boolean isFlagged = mData.getBoolean(Constants.isFlagged);

                    judgementModel.setJudgementId(judgementId);
                    judgementModel.setTitle(title);
                    judgementModel.setCourt(court);
                    judgementModel.setJudgementType(judgementType);
                    judgementModel.setDateOfJudgement(dateOfJudgement);
                    judgementModel.setCaseTitle(caseTitle);
                    judgementModel.setCaseType(caseType);
                    judgementModel.setShortDescription(shortDescription);
                    judgementModel.setDetailedDescription(detailedDescription);
                    judgementModel.setJudgementDoc(judgementDoc);
                    judgementModel.setGoogleDriveUrl(googleDriveUrl);
                    judgementModel.setPublic(isPublic);
                    judgementModel.setActive(isActive);
                    judgementModel.setFlagged(isFlagged);

                }
                try {
                    Intent intent = new Intent();
                    intent.putExtra("data", judgementModel);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                    Constants.tempJudgmentModel = judgementModel;
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                Utility.showSnackBar(this, "Something went wrong! Please try after some time.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void parseAddResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                Intent intent = new Intent();
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

    private boolean isValidated(String str_title, String str_short_desc, String str_full_desc, String str_case_title, String str_case_type, String str_judgement_type, String str_court, String str_date) {


        if (str_short_desc.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (str_case_title.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        /*if (str_case_type.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }*/

        if (str_judgement_type.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        /*if (str_court.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }

        if (str_date.length() == 0) {
            Utility.showSnackBar(this, "All the fields are mandatory");
            return false;
        }*/


       /* if (str_full_desc.length() == 0) {
            Utility.showSnackBar(this, "Please add the Judgement");
            return false;
        }*/
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = "0" + (view.getMonth() + 1)
                + "-" + view.getDayOfMonth()
                + "-" + view.getYear();

        tv_date.setText(Utility.formatDateForDisplay(date));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            str_full_desc = Constants.tempDetailedDescription;

            tv_submit.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_add_desc.setText("Edit Judgement");
        }
    }
}
