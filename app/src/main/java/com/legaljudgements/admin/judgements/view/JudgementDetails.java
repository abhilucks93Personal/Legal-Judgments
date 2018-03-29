package com.legaljudgements.admin.judgements.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by ng on 2/19/2017.
 */
public class JudgementDetails extends AppCompatActivity implements View.OnClickListener {

    private JudgementModel data_judgement;
    private TextView tv_title, tv_type, tv_short_desc, tv_case_title, tv_case_type, tv_court, tv_date;
    private int pos;
    private ProgressDialog progressDialog;
    private String deviceId, str_judgement_id, str_userId;
    private String user_type;
    private Menu menu;
    TextView tv_desc;
    String strJudgementId;
    boolean flag_changed = false;
    private TextView numWaterMark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.judgement_details);

        findViewById();

        initData();
    }

    private void initData() {
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user_type = getIntent().getStringExtra("userType");

        Utility.setWaterMark(JudgementDetails.this,numWaterMark);

        pos = getIntent().getIntExtra("pos", -1);
        deviceId = Utility.getPreferences(this, Constants.DEVICE_ID);
        str_userId = Utility.getPreferences(this, Constants.userId);

        data_judgement = (JudgementModel) getIntent().getParcelableExtra("data");

        if (data_judgement == null)
            data_judgement = Constants.tempJudgmentModel;

        if (data_judgement != null) {
            setData();
        }

        strJudgementId = getIntent().getStringExtra("id");
        if (strJudgementId != null) {
            loadData();
        }

    }

    private void loadData() {

        progressDialog = new ProgressDialog(JudgementDetails.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WebServices.getJudgementById(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                deviceId, strJudgementId, str_userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();

                        // Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        parseJudgementResponse(response);
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

    private void parseJudgementResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {

                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);
                        data_judgement = new JudgementModel();

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

                        data_judgement.setJudgementId(judgementId);
                        data_judgement.setTitle(title);
                        data_judgement.setCourt(court);
                        data_judgement.setJudgementType(judgementType);
                        data_judgement.setDateOfJudgement(dateOfJudgement);
                        data_judgement.setCaseTitle(caseTitle);
                        data_judgement.setCaseType(caseType);
                        data_judgement.setShortDescription(shortDescription);
                        data_judgement.setDetailedDescription(detailedDescription);
                        data_judgement.setJudgementDoc(judgementDoc);
                        data_judgement.setGoogleDriveUrl(googleDriveUrl);
                        data_judgement.setPublic(isPublic);
                        data_judgement.setActive(isActive);
                        data_judgement.setFlagged(isFlagged);


                    }
                    setData();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        str_judgement_id = data_judgement.getJudgementId();

        tv_title.setText(data_judgement.getTitle());
        tv_type.setText(data_judgement.getJudgementType());
        tv_short_desc.setText(data_judgement.getShortDescription());
        tv_case_title.setText(data_judgement.getCaseTitle());
        tv_case_type.setText(data_judgement.getCaseType());
        tv_court.setText(data_judgement.getCourt());
        if (data_judgement.getDetailedDescription().length() == 0) {
            tv_desc.setVisibility(View.INVISIBLE);
        }
        if (!Utility.getPreferences(getApplicationContext(), "FullAccess", false)) {
            tv_desc.setVisibility(View.INVISIBLE);
        }
        tv_date.setText(Utility.formatDateForDisplay(data_judgement.getDateOfJudgement()));
        if (menu != null)
            if (data_judgement.getFlagged())
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.filled_flag));
            else
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.flag));
    }

    private void findViewById() {

        tv_title = (TextView) findViewById(R.id.judgement_detail_tv_title);
        tv_type = (TextView) findViewById(R.id.judgement_detail_tv_type);
        tv_short_desc = (TextView) findViewById(R.id.judgement_detail_tv_short_description);
        tv_case_title = (TextView) findViewById(R.id.judgement_detail_tv_case_title);
        tv_case_type = (TextView) findViewById(R.id.judgement_detail_tv_case_type);
        tv_court = (TextView) findViewById(R.id.judgement_detail_tv_court);
        tv_date = (TextView) findViewById(R.id.judgement_detail_tv_date);
        tv_desc = (TextView) findViewById(R.id.judgement_detail_tv_view);
        tv_desc.setOnClickListener(this);
        numWaterMark= (TextView) findViewById(R.id.num_water_mark);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user_type.equalsIgnoreCase(Constants.userTypeAdmin)) {
            getMenuInflater().inflate(R.menu.judgement_update, menu);
            this.menu = null;
        } else {
            getMenuInflater().inflate(R.menu.judgement_flag, menu);
            this.menu = menu;
            if (data_judgement != null) {
                if (data_judgement.getFlagged())
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.filled_flag));
                else
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.flag));
            }

        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.judgement_detail_tv_view:
                try {
                    startActivity(new Intent(JudgementDetails.this, ViewJudgement.class)
                            .putExtra("desc", data_judgement.getDetailedDescription()));
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                    Constants.tempDetailedDescription = data_judgement.getDetailedDescription();
                    startActivity(new Intent(JudgementDetails.this, ViewJudgement.class));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backButtonPressed();
    }

    public void backButtonPressed() {
        Intent intent = new Intent();
        intent.putExtra("tag", "edit")
                .putExtra("pos", pos)
                .putExtra("flag_changed", flag_changed);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                backButtonPressed();
                return true;

            case R.id.update_jm_action_edit:
                if (pos >= 0)
                    try {
                        startActivityForResult(new Intent(JudgementDetails.this, CreateJudgementActivity.class)
                                .putExtra("data", data_judgement), 100);
                    } catch (Exception e) {
                        Log.d("exception", e.toString());
                        Constants.tempJudgmentModel = data_judgement;
                        startActivityForResult(new Intent(JudgementDetails.this, CreateJudgementActivity.class), 100);
                    }
                return true;

            case R.id.update_jm_action_flag:

                flagJudgement(!data_judgement.getFlagged());

                return true;


            case R.id.update_jm_action_delete:
                if (pos >= 0) {
                    deleteJudgementDialog(JudgementDetails.this, "Are you sure you want to delete this Judgement");
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteJudgementDialog(Activity activity, String message) {

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
                deleteJudgement();
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    private void flagJudgement(Boolean flagged) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        WebServices.flagJudgement(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                deviceId, str_judgement_id, str_userId, "" + flagged, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        parseFlagResponse(response);

                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
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
                        Utility.showToast(getApplicationContext(), Constants.error_message_api_failed);
                        progressDialog.dismiss();
                    }
                });


    }

    private void parseFlagResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                flag_changed = true;
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

                    data_judgement.setJudgementId(judgementId);
                    data_judgement.setTitle(title);
                    data_judgement.setCourt(court);
                    data_judgement.setJudgementType(judgementType);
                    data_judgement.setDateOfJudgement(dateOfJudgement);
                    data_judgement.setCaseTitle(caseTitle);
                    data_judgement.setCaseType(caseType);
                    data_judgement.setShortDescription(shortDescription);
                    data_judgement.setDetailedDescription(detailedDescription);
                    data_judgement.setJudgementDoc(judgementDoc);
                    data_judgement.setGoogleDriveUrl(googleDriveUrl);
                    data_judgement.setPublic(isPublic);
                    data_judgement.setActive(isActive);
                    data_judgement.setFlagged(isFlagged);
                    setData();

                }
            } else {
                Utility.showSnackBar(this, "Something went wrong! Please try after some time.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteJudgement() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        WebServices.deleteJudgement(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                deviceId, str_judgement_id, str_userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        //  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        parseDeleteResponse(response);

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

    private void parseDeleteResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                Intent intent = new Intent();
                intent.putExtra("tag", "delete")
                        .putExtra("pos", pos);
                setResult(RESULT_OK, intent);
                finish();
            }
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            data_judgement = (JudgementModel) data.getParcelableExtra("data");
            if (data_judgement == null) {
                data_judgement = Constants.tempJudgmentModel;
            }
            if (data_judgement != null) {
                setData();
            }
        }
    }
}
