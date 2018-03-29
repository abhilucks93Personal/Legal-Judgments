package com.legaljudgements.lawyer.home;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.lawyer.membership.MembershipConfirmationActivity;
import com.legaljudgements.lawyer.membership.SelectMembershipActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static com.legaljudgements.Utils.Constants.UniqueDeviceId;

public class HomeFragment extends Fragment implements View.OnClickListener {

    TextView tv_name, tv_endDate, tv_title, tv_title_, tv_status, tv_renew;
    Boolean membershipExpired = true;
    String memberShipId = null;
    String memberShipTitle = "", memberShipPrice = "";
    private String _userId;
    private ProgressDialog progressDialog;
    private String deviceId, uniqueDeviceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findViewById(view);
        inItData();

        return view;
    }

    private void inItData() {
        membershipExpired = Utility.getPreferences(getContext(), Constants.MembershipExpired, false);
        // membershipExpired = true;
        deviceId = Utility.getPreferences(getActivity(), Constants.DEVICE_ID);
        uniqueDeviceId = Utility.getPreferences(getActivity(), UniqueDeviceId);
        _userId = Utility.getPreferences(getContext(), Constants.userId);
        tv_name.setText(Utility.getPreferences(getContext(), Constants.userName) + " !");
        if (membershipExpired) {
            tv_status.setText("Your membership has been expired.");
            tv_status.setTextColor(getResources().getColor(R.color.colorRed));
            tv_renew.setVisibility(View.VISIBLE);
            tv_title_.setVisibility(View.GONE);
        } else {
            tv_title.setText(Utility.getPreferences(getContext(), Constants.membershipTitle));
            tv_endDate.setText(Utility.formatDateForDisplay(Utility.convertedDate(Utility.getPreferences(getContext(), Constants.membershipEndDate))));
            tv_renew.setVisibility(View.GONE);
        }

    }

    private void findViewById(View view) {
        tv_name = (TextView) view.findViewById(R.id.home_tv_name);
        tv_endDate = (TextView) view.findViewById(R.id.home_tv_endDate);
        tv_title = (TextView) view.findViewById(R.id.home_tv_title);
        tv_title_ = (TextView) view.findViewById(R.id.home_tv_title_);
        tv_status = (TextView) view.findViewById(R.id.home_tv_membership_status);
        tv_renew = (TextView) view.findViewById(R.id.home_tv_renew);
        tv_renew.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_tv_renew:
                if (memberShipId == null)
                    startActivityForResult(new Intent(getActivity(), SelectMembershipActivity.class), 300);
                else
                    showPackageConfirmationDialog();
                break;
        }
    }

    private void renewMembership() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebServices.renewMembership(uniqueDeviceId, deviceId, _userId, memberShipId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
               // Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                parseRenewResponse(response);
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
                Toast.makeText(getContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "Something went wrong! Please try after sometime", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void showPackageConfirmationDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout3);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText("You had selected " + memberShipTitle + " Package");
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        TextView tv_discard = (TextView) dialog.findViewById(R.id.common_dialog_discard);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renewMembership();
                // parseRenewResponse(null);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            memberShipId = data.getStringExtra("id");
            memberShipTitle = data.getStringExtra("title");
            memberShipPrice = data.getStringExtra("price");

            if (memberShipId != null)
                showPackageConfirmationDialog();
        }
    }

    private void parseRenewResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            // String statusCode = "200";
            if (statusCode.equals("200"))
                startActivity(new Intent(getActivity(), MembershipConfirmationActivity.class)
                        .putExtra("renew", true));

        } catch (Exception e) {
            Log.e("", "");
        }
    }

}