package com.legaljudgements.admin.judgements.view;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.judgements.adapter.JudgementDetailsAdapter;
import com.legaljudgements.admin.judgements.model.JudgementModel;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.legaljudgements.Utils.Constants.userType;

/**
 * Created by ng on 2/15/2017.
 */

public class FlaggedJudgementFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView lv_users;
    private View view;
    private ProgressDialog progressDialog;
    private String deviceId;
    ArrayList<JudgementModel> allJudgements = new ArrayList<>();
    private JudgementDetailsAdapter adapter;
    String strPageNo = "1";
    String strPageSize = "20";
    private String strKeyword = "";
    private String strUserId;
    String strUserType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_judgements, container, false);
        findViewById();
        initData();
        getJudgements();
        return view;

    }

    private void initData() {
        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);
        strUserId = Utility.getPreferences(getContext(), Constants.userId);
        strUserType = Utility.getPreferences(getContext(), userType);
        adapter = new JudgementDetailsAdapter(getActivity(), allJudgements);

        lv_users.setAdapter(adapter);
        lv_users.setOnItemClickListener(this);
    }

    public void getJudgements() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebServices.getJudgements(Utility.getPreferences(getContext(), Constants.UniqueDeviceId),
                true, deviceId, strPageNo, strPageSize, strKeyword, strUserId, "", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        //   Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                        parseJudgementResponse(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Utility.showToast(getContext(), Constants.error_message_api_failed);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Utility.showToast(getContext(), Constants.error_message_api_failed);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Utility.showToast(getContext(), Constants.error_message_api_failed);
                        progressDialog.dismiss();
                    }
                });
    }

    private void parseJudgementResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                allJudgements.clear();
                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);
                        JudgementModel judgementModel = new JudgementModel();

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

                        allJudgements.add(judgementModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        lv_users = (ListView) view.findViewById(R.id.judgements_lv_users);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivityForResult(new Intent(getActivity(), JudgementDetails.class)
                .putExtra("userType", strUserType)
                .putExtra("data", allJudgements.get(position))
                .putExtra("pos", position), 200);
    }

    public void updateItem(int pos, JudgementModel judgementDetail) {
        allJudgements.set(pos, judgementDetail);
        adapter.notifyDataSetChanged();
    }

    public void deleteItem(int pos) {
        allJudgements.remove(pos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Utility.getPreferences(getContext(), Constants.userType).equals(Constants.userTypeUser)) {
            strPageNo = "1";
            getJudgements();
        }


    }
}