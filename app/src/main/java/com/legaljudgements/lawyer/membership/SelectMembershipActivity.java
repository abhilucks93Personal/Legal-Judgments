package com.legaljudgements.lawyer.membership;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.membership.controller.MyListAdapter;
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
public class SelectMembershipActivity extends AppCompatActivity implements MyListAdapter.AdapterCallback {


    private String deviceId;
    private ArrayList<HeaderInfo> MembershipsList = new ArrayList<>();
    private MyListAdapter listAdapter;
    private ExpandableListView expandableListView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_manage_membership);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById();
        init();
        deviceId = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);
        getActiveMemberships();

    }

    private void getActiveMemberships() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        WebServices.getMemberships(Utility.getPreferences(getApplicationContext(),Constants.UniqueDeviceId),
                deviceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
               // Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                parseMembershipResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Utility.showToast(getApplicationContext(),Constants.error_message_api_failed);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utility.showToast(getApplicationContext(),Constants.error_message_api_failed);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utility.showToast(getApplicationContext(),Constants.error_message_api_failed);
                progressDialog.dismiss();
            }
        });

    }

    private void parseMembershipResponse(JSONObject response) {
        try {
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
                    listAdapter.notifyDataSetChanged();
                }

            } else {
                Utility.showSnackBar(this, "Please enter a valid Username and Password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        expandableListView = (ExpandableListView) findViewById(R.id.manage_membership_elv_membershipList);
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

    private void init() {

        //create the adapter by passing your ArrayList data
        listAdapter = new MyListAdapter(SelectMembershipActivity.this, MembershipsList, 0);
        //attach the adapter to the list
        expandableListView.setAdapter(listAdapter);


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                TextView tv_title = (TextView) v.findViewById(R.id.heading);
                TextView tv_price = (TextView) v.findViewById(R.id.heading_price);
                if (parent.isGroupExpanded(groupPosition)) {

                    tv_title.setSingleLine(true);
                    tv_price.setVisibility(View.GONE);
                } else {
                    tv_title.setSingleLine(false);
                    tv_price.setVisibility(View.GONE);
                    // Expanded ,Do your Staff

                }


                return false;
            }
        });
    }



    @Override
    public void onMethodCallback(int groupPosition) {
        Intent intent = new Intent();
        intent.putExtra("id", MembershipsList.get(groupPosition).getId());
        intent.putExtra("title", MembershipsList.get(groupPosition).getTitle());
        intent.putExtra("price", MembershipsList.get(groupPosition).getPrice());
        setResult(RESULT_OK, intent);
        finish();

    }
}
