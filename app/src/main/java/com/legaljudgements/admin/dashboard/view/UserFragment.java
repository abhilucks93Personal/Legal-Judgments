package com.legaljudgements.admin.dashboard.view;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.dashboard.controller.UserDetailsAdapter;
import com.legaljudgements.admin.dashboard.model.UserDetailsModel;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static com.legaljudgements.Utils.Constants.address;
import static com.legaljudgements.Utils.Constants.email;
import static com.legaljudgements.Utils.Constants.isActive;
import static com.legaljudgements.Utils.Constants.membershipEndDate;
import static com.legaljudgements.Utils.Constants.membershipId;
import static com.legaljudgements.Utils.Constants.membershipStartDate;
import static com.legaljudgements.Utils.Constants.membershipValidity;
import static com.legaljudgements.Utils.Constants.name;
import static com.legaljudgements.Utils.Constants.password;
import static com.legaljudgements.Utils.Constants.phone;
import static com.legaljudgements.Utils.Constants.userId;
import static com.legaljudgements.Utils.Constants.userName;
import static com.legaljudgements.Utils.Constants.userType;


public class UserFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    ListView lv_users;
    View view;
    ProgressDialog progressDialog;
    String deviceId;
    ArrayList<UserDetailsModel> allUsers = new ArrayList<>();
    UserDetailsAdapter adapter;
    UserDetailsModel userDetailsModel;
    int pagNum = 1;
    boolean loading = true;
    int size = 13;
    int totalsize = 0;
    String strKeyword = "";
    String strKeywordCategory = "";
    String activeStatus = "";
    String memberShipId = "";
    ProgressBar loader;
    LinearLayout rl_search;
    EditText et_search;
    ImageView iv_tick;
    SwipeRefreshLayout swipeView;
    private String strId;
    private RadioGroup radioGroup;
    private RadioButton rbAll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        findViewById();
        initData();
        getUsers();

        return view;
    }

    public void setSearchView() {
        if (rl_search.getVisibility() == View.VISIBLE) {
            rl_search.setVisibility(View.GONE);
            strKeyword = "";
            strKeywordCategory = "";
            rbAll.setChecked(true);
            pagNum = 1;
            getUsers();
        } else {
            rl_search.setVisibility(View.VISIBLE);
            et_search.setText("");
            rbAll.setChecked(true);
        }
    }

    private void initData() {
        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);

        adapter = new UserDetailsAdapter(getActivity(), allUsers);
        strId = getArguments().getString("id");
        if (strId != null && strId.length() > 0) {
            startActivityForResult(new Intent(getActivity(), UserDetails.class)
                    .putExtra("id", strId), 102);
        }
        lv_users.setAdapter(adapter);
        lv_users.setOnItemClickListener(this);
        lv_users.setOnScrollListener(this);
    }

    private void getUsers() {
        if (pagNum == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        } else {
            loader.setVisibility(View.VISIBLE);
        }
        WebServices.getUsers(Utility.getPreferences(getContext(), Constants.UniqueDeviceId),
                deviceId, "USER", "" + pagNum, "" + size, strKeyword, activeStatus, memberShipId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();

                        //      Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                        loader.setVisibility(View.GONE);
                        parseUserResponse(response);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Utility.showToast(getContext(), Constants.error_message_api_failed);
                        loader.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Utility.showToast(getContext(), Constants.error_message_api_failed);
                        progressDialog.dismiss();
                        loader.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Utility.showToast(getContext(), Constants.error_message_api_failed);
                        progressDialog.dismiss();
                    }
                });
    }

    private void parseUserResponse(JSONObject response) {
        try {

            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {
                totalsize = Integer.parseInt(response.getString(Constants.STATUS_MESSAGE));
                if (pagNum == 1)
                    allUsers.clear();
                loading = true;
                JSONArray data = response.getJSONArray(Constants.STATUS_DATA);
                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject mData = data.getJSONObject(i);
                        userDetailsModel = new UserDetailsModel();

                        String str_userId = mData.getString(userId);
                        String str_userName = mData.getString(userName);
                        String str_password = mData.getString(password);
                        String str_name = mData.getString(name);
                        String str_email = mData.getString(email);
                        String str_phone = mData.getString(phone);
                        String str_address = mData.getString(address);
                        String str_membershipId = mData.getString(membershipId);
                        String str_isActive = mData.getString(isActive);
                        String str_membershipStartDate = mData.getString(membershipStartDate);
                        String str_membershipEndDate = mData.getString(membershipEndDate);
                        String str_userType = mData.getString(userType);
                        String str_membershipValidity = mData.getString(membershipValidity);
                        String str_access = mData.getString("fullAccess");
                        String str_title = mData.getString(Constants.membershipTitle);
                        String str_unit = mData.getString("validityUnit");


                        userDetailsModel.setUserId(str_userId);
                        userDetailsModel.setUserName(str_userName);
                        userDetailsModel.setPassword(str_password);
                        userDetailsModel.setName(str_name);
                        userDetailsModel.setEmail(str_email);
                        userDetailsModel.setPhone(str_phone);
                        userDetailsModel.setAddress(str_address);
                        userDetailsModel.setMembershipId(str_membershipId);
                        userDetailsModel.setIsActive(str_isActive);
                        userDetailsModel.setMembershipStartDate(str_membershipStartDate);
                        userDetailsModel.setMembershipEndDate(str_membershipEndDate);
                        userDetailsModel.setUserType(str_userType);
                        userDetailsModel.setMembershipValidity(str_membershipValidity);
                        userDetailsModel.setScope(str_access);
                        userDetailsModel.setUnit(str_unit);
                        userDetailsModel.setTitle(str_title);

                        allUsers.add(userDetailsModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        loader = (ProgressBar) view.findViewById(R.id.dashboard_pagination_loader);
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(this);
        loader.setVisibility(View.GONE);
        et_search = (EditText) view.findViewById(R.id.et_search);
        iv_tick = (ImageView) view.findViewById(R.id.iv_tick);
        iv_tick.setOnClickListener(this);
        rl_search = (LinearLayout) view.findViewById(R.id.rl_search);
        lv_users = (ListView) view.findViewById(R.id.dashboard_lv_users);
        radioGroup = (RadioGroup) view.findViewById(R.id.rg_search);
        rbAll = (RadioButton) view.findViewById(R.id.rb_all);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_tick:
                strKeyword = et_search.getText().toString().trim();
                if (strKeyword.length() > 0) {
                    pagNum = 1;
                    getUsers();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivityForResult(new Intent(getActivity(), UserDetails.class)
                .putExtra("data", allUsers.get(position))
                .putExtra("pos", position), 102);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                UserDetailsModel userDetailsModel = (UserDetailsModel) data.getSerializableExtra("data");
                int pos = data.getIntExtra("pos", -1);
                if (pos >= 0) {
                    if (userDetailsModel != null) {
                        String tag = data.getStringExtra("tag");
                        if (tag.equals("delete")) {
                            allUsers.remove(pos);
                            adapter.notifyDataSetChanged();
                        } else {
                            allUsers.set(pos, userDetailsModel);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        onRefresh();
                    }

                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount > 0 && loading) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount && (pagNum * size < totalsize)) {
                pagNum = pagNum + 1;
                loading = false;
                getUsers();
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeView.setRefreshing(false);
        swipeView.isNestedScrollingEnabled();
        setSearchView();
        pagNum = 1;
        getUsers();
    }
}