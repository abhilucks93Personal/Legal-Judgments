package com.legaljudgements.lawyer;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by ng on 3/8/2017.
 */
public class ContactUsFragment extends Fragment {


    private ProgressDialog progressDialog;
    private String uniqueDeviceId, deviceId;
    RichEditor view_text;
    private String methodName = "GetContactUsContent";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_conditions_screen, container, false);

        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);
        uniqueDeviceId = Utility.getPreferences(getContext(), Constants.UniqueDeviceId);
        view_text = (RichEditor) view.findViewById(R.id.doc_view);
        view_text.setSelected(false);
        view_text.dispatchSetSelected(false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        WebServices.getTermsContent(methodName, uniqueDeviceId, deviceId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                //  Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                String _statusCode = null;
                try {
                    _statusCode = response.getString(Constants.STATUS_CODE);
                    if (_statusCode.equals("200")) {
                        JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                        if (data.length() > 0) {
                            JSONObject mData = data.getJSONObject(0);
                            String content = mData.getString("pageContent");
                            view_text.loadData(content,  "text/html; charset=UTF-8", null);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Utility.showToast(getContext(), Constants.error_message_api_failed);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable
                    throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utility.showToast(getContext(), Constants.error_message_api_failed);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable
                    throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utility.showToast(getContext(), Constants.error_message_api_failed);
                progressDialog.dismiss();
            }
        });

        return view;
    }
}
