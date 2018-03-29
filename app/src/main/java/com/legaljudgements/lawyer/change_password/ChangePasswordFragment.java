package com.legaljudgements.lawyer.change_password;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.legaljudgements.Utils.Constants.name;
import static com.legaljudgements.Utils.Constants.password;


public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private EditText et_old, et_new, et_confirm;
    TextView tv_submit;
    private String strCurrentPassword;
    private ProgressDialog progressDialog;
    private String deviceId, strUserName, strMembershipId, strUserId, strEmail, strName, strPhone, strAddress;
    ImageView iv_eye, iv_eye2, iv_eye3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        findViewById(view);
        inItData();
        return view;
    }

    private void inItData() {
        strCurrentPassword = Utility.getPreferences(getContext(), Constants.password);

        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);
        strUserId = Utility.getPreferences(getContext(), Constants.userId);
        strUserName = Utility.getPreferences(getContext(), Constants.userName);
        strMembershipId = Utility.getPreferences(getContext(), Constants.membershipId);
        strEmail = Utility.getPreferences(getContext(), Constants.email);
        strName = Utility.getPreferences(getContext(), Constants.name);
        strPhone = Utility.getPreferences(getContext(), Constants.phone);
        strAddress = Utility.getPreferences(getContext(), Constants.address);
    }

    private void findViewById(View view) {
        et_old = (EditText) view.findViewById(R.id.cp_et_old);
        et_new = (EditText) view.findViewById(R.id.cp_et_new);
        et_confirm = (EditText) view.findViewById(R.id.cp_et_confirm);
        iv_eye = (ImageView) view.findViewById(R.id.psswrd_eye);
        iv_eye.setOnClickListener(this);
        iv_eye2 = (ImageView) view.findViewById(R.id.psswrd_eye2);
        iv_eye2.setOnClickListener(this);
        iv_eye3 = (ImageView) view.findViewById(R.id.psswrd_eye3);
        iv_eye3.setOnClickListener(this);
        tv_submit = (TextView) view.findViewById(R.id.cp_tv_submit);
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cp_tv_submit:
                fetchData();
                break;

            case R.id.psswrd_eye:
                Utility.password_visible(getActivity(), et_old, iv_eye);
                break;

            case R.id.psswrd_eye2:
                Utility.password_visible(getActivity(), et_new, iv_eye2);
                break;

            case R.id.psswrd_eye3:
                Utility.password_visible(getActivity(), et_confirm, iv_eye3);
                break;


        }
    }

    private void fetchData() {
        String strOld = et_old.getText().toString().trim();
        String strNew = et_new.getText().toString().trim();
        String strConfirm = et_confirm.getText().toString().trim();

        if (isValidated(strOld, strNew, strConfirm)) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            WebServices.updateProfile(Utility.getPreferences(getContext(), Constants.UniqueDeviceId), deviceId, strUserName, strNew, strName, strPhone, strEmail, strAddress, strMembershipId, strUserId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    progressDialog.dismiss();
                  //  Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    parseChangePasswordResponse(response);
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
    }

    private void parseChangePasswordResponse(JSONObject response) {
        try {
            Utility.hideKeyboard(getActivity());
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {

                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);

                        String str_name = mData.getString(password);
                        Utility.addPreferences(getContext(), name, str_name);


                        Utility.alertDialog(getActivity(), "Password updated successfully !");

                    }
                }
            } else {
                Utility.showSnackBar(getActivity(), "Something went wrong! Please try again later.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean isValidated(String strOld, String strNew, String strConfirm) {
        if (strOld.length() == 0) {
            Utility.showSnackBar(getActivity(), "Please enter the old password");
            return false;
        }

        if (!strOld.equals(strCurrentPassword)) {
            Utility.showSnackBar(getActivity(), "Please enter the correct old password");
            return false;
        }

        if (strNew.length() == 0) {
            Utility.showSnackBar(getActivity(), "Please enter the new password");
            return false;
        }

        if (strNew.length() < 6) {
            Utility.showSnackBar(getActivity(), "New password should be of min 6 characters");
            return false;
        }

        if (!strNew.equals(strConfirm)) {
            Utility.showSnackBar(getActivity(), "Confirm Password did not match");
            return false;
        }
        return true;
    }
}