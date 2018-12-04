package com.legaljudgements.admin.membership.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.membership.model.HeaderInfo;
import com.legaljudgements.admin.membership.controller.MyListAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static com.legaljudgements.Utils.Constants.isAdmin;
import static com.legaljudgements.Utils.Constants.isLawyer;

/**
 * Created by ng on 2/8/2017.
 */
public class ManageMembershipFragment extends android.support.v4.app.Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private String deviceId;
    private ProgressDialog progressDialog;
    private View view;
    private ExpandableListView expandableListView;
    private MyListAdapter listAdapter;
    private ArrayList<HeaderInfo> MembershipsList = new ArrayList<>();
    private Dialog dialog;
    String userId = "";
    private SwipeRefreshLayout swipeView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_membership, container, false);

        findViewById();
        init();
        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);
        getActiveMemberships();


        return view;
    }

    private void init() {

        userId = Utility.getPreferences(getContext(), Constants.userId);
        //create the adapter by passing your ArrayList data
        if (Utility.getPreferences(getContext(), isLawyer, false))
            listAdapter = new MyListAdapter(getActivity(), MembershipsList,1);
        else
            listAdapter = new MyListAdapter(getActivity(), MembershipsList, 2);
        //attach the adapter to the list
        expandableListView.setAdapter(listAdapter);
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expandableListView.getExpandableListPosition(position);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                if (Utility.getPreferences(getContext(), Constants.userType).equals(Constants.userTypeAdmin))
                    showUpdateMembershipDialog(groupPosition);
                return true;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                TextView tv_title = (TextView) v.findViewById(R.id.heading);
                TextView tv_price = (TextView) v.findViewById(R.id.heading_price);
                if (parent.isGroupExpanded(groupPosition)) {

                    tv_title.setSingleLine(true);
                    if (Utility.getPreferences(getActivity(), isAdmin, false))
                        tv_price.setVisibility(View.VISIBLE);
                    else
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

    private void showUpdateMembershipDialog(final int pos) {

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.membership_update_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_title = (TextView) dialog.findViewById(R.id.update_ms_tv_title);
        tv_title.setText(MembershipsList.get(pos).getTitle());
        TextView tv_edit = (TextView) dialog.findViewById(R.id.update_ms_tv_edit);
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
                startActivityForResult(new Intent(getActivity(), CreateMembershipActivity.class)
                        .putExtra("data", MembershipsList.get(pos)), 200);

            }
        });

        TextView tv_delete = (TextView) dialog.findViewById(R.id.update_ms_tv_delete);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
                deleteDialog(getActivity(), "Are you sure you want to delete this Membership", pos);
            }
        });
        dialog.show();
    }

    public void deleteDialog(Activity activity, String message, final int pos) {

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
                deleteMembership(pos);
                dialog.dismiss();

            }
        });
        dialog.show();

    }


    private void deleteMembership(int pos) {
        String id = MembershipsList.get(pos).getId();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebServices.DeleteMembership(Utility.getPreferences(getContext(), Constants.UniqueDeviceId), deviceId, id, userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                //   Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                MembershipsList.clear();
                parseMembershipResponse(response);
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

    private void findViewById() {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(this);
        expandableListView = (ExpandableListView) view.findViewById(R.id.manage_membership_elv_membershipList);
    }

    private void getActiveMemberships() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        WebServices.getMemberships(Utility.getPreferences(getContext(), Constants.UniqueDeviceId)
                , deviceId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        //   Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                        parseMembershipResponse(response);
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

    private void parseMembershipResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                MembershipsList.clear();
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
                        header.setScope(mData.getString("fullAccess"));
                        header.setUnit(mData.getString("validityUnit"));
                        MembershipsList.add(header);

                    }
                    listAdapter.notifyDataSetChanged();
                }

            } else {
                Utility.showSnackBar(getActivity(), "Please enter a valid Username and Password");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.update_ms_tv_edit:
                if (dialog.isShowing())
                    dialog.dismiss();

                break;

            case R.id.update_ms_tv_delete:


                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String tag = data.getStringExtra("tag");
            switch (requestCode) {

                case 200:
                    if (tag.equals("add")) {
                        ArrayList<HeaderInfo> membershipList = data.getParcelableArrayListExtra("data");
                        updateAllMembership(membershipList);
                    } else if (tag.equals("edit")) {
                        HeaderInfo header = data.getParcelableExtra("data");
                        updateSingleMembership(header);
                    }
                    break;
            }
        }
    }


    public void updateSingleMembership(HeaderInfo header) {
        int index = 0;
        for (HeaderInfo info : MembershipsList) {
            if (info.getId().equals(header.getId())) {
                MembershipsList.set(index, header);
                break;
            }
            index++;
        }
        listAdapter.notifyDataSetChanged();
    }

    public void updateAllMembership(ArrayList<HeaderInfo> membershipList) {
        MembershipsList.clear();
        MembershipsList.addAll(membershipList);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        swipeView.setRefreshing(false);
        swipeView.isNestedScrollingEnabled();

        getActiveMemberships();
    }
}