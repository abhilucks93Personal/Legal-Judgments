package com.legaljudgements.lawyer.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import static com.legaljudgements.Utils.Constants.address;
import static com.legaljudgements.Utils.Constants.email;
import static com.legaljudgements.Utils.Constants.name;
import static com.legaljudgements.Utils.Constants.phone;

/**
 * Created by ng on 2/26/2017.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {

    EditText et_name, et_email, et_phone, et_address, et_chamber;
    TextView tv_submit;
    private ProgressDialog progressDialog;
    private String deviceId, strUserName, strpassword, strMembershipId, strUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_profile_screen, container, false);
        findViewById(view);
        initData();
        return view;
    }

    private void initData() {
        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);
        strUserId = Utility.getPreferences(getContext(), Constants.userId);
        strUserName = Utility.getPreferences(getContext(), Constants.userName);
        strpassword = Utility.getPreferences(getContext(), Constants.password);
        strMembershipId = Utility.getPreferences(getContext(), Constants.membershipId);

        et_name.setText(strUserName);
        et_name.setEnabled(false);
        et_name.setClickable(false);
        et_email.setText(Utility.getPreferences(getContext(), Constants.email));
        et_phone.setText(Utility.getPreferences(getContext(), Constants.phone));
        et_address.setText(Utility.getPreferences(getContext(), Constants.address));
        et_chamber.setText(Utility.getPreferences(getContext(), Constants.name));

    }

    private void findViewById(View view) {
        et_name = (EditText) view.findViewById(R.id.edit_profile_et_name);
        et_email = (EditText) view.findViewById(R.id.edit_profile_et_email);
        et_phone = (EditText) view.findViewById(R.id.edit_profile_et_num);
        et_address = (EditText) view.findViewById(R.id.edit_profile_et_address);
        et_chamber = (EditText) view.findViewById(R.id.edit_profile_et_chamber);
        tv_submit = (TextView) view.findViewById(R.id.edit_profile_tv_submit);
        tv_submit.setOnClickListener(this);

    }

    private void fetchData() {

        String strNum = et_phone.getText().toString().trim();
        String strAddress = et_address.getText().toString().trim();


        if (isValidated("", strNum, "", strAddress)) {
            updateProfile("", strNum, "", strAddress);
        }

    }

    private void updateProfile(String strName, String strNum, String strEmail, String strAddress) {


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebServices.updateProfile(Utility.getPreferences(getContext(), Constants.UniqueDeviceId), deviceId, strUserName, strpassword, strName, strNum, strEmail, strAddress, strMembershipId, strUserId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                //  Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                parseUpdateProfileResponse(response);
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

    private void parseUpdateProfileResponse(JSONObject response) {
        try {
            Utility.hideKeyboard(getActivity());
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {

                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);

                        String str_name = mData.getString(name);
                        String str_email = mData.getString(email);
                        String str_phone = mData.getString(phone);
                        String str_address = mData.getString(address);

                        Utility.addPreferences(getContext(), name, str_name);
                        Utility.addPreferences(getContext(), email, str_email);
                        Utility.addPreferences(getContext(), phone, str_phone);
                        Utility.addPreferences(getContext(), address, str_address);

                        Utility.alertDialog(getActivity(), "Profile updated successfully !");

                    }
                }
            } else {
                Utility.showSnackBar(getActivity(), "Something went wrong! Please try again later.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private boolean isValidated(String strName, String strNum, String strEmail, String strAddress) {

       /* if (strName.length() == 0) {
            Utility.showSnackBar(getActivity(), "All the fields are mandatory");
            return false;
        }*/

        if (strNum.length() == 0) {
            Utility.showSnackBar(getActivity(), "All the fields are mandatory");
            return false;
        }

        /*if (strEmail.length() == 0) {
            Utility.showSnackBar(getActivity(), "All the fields are mandatory");
            return false;
        }*/

        /*if (!Utility.isValidEmail(strEmail)) {
            Utility.showSnackBar(getActivity(), "Please enter a valid Email address");
            return false;
        }*/
        if (strAddress.length() == 0) {
            Utility.showSnackBar(getActivity(), "All the fields are mandatory");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        fetchData();
    }
}