package com.legaljudgements.admin.judgements.view;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.judgements.adapter.JudgementDetailsAdapter;
import com.legaljudgements.admin.judgements.model.JudgementModel;
import com.loopj.android.http.JsonHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;
import static com.legaljudgements.Utils.Constants.isAdmin;
import static com.legaljudgements.Utils.Constants.userType;

/**
 * Created by ng on 2/15/2017.
 */

public class JudgementFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, RadioGroup.OnCheckedChangeListener {

    private ListView lv_users;
    private View view;
    private ProgressDialog progressDialog;
    private String deviceId;
    ArrayList<JudgementModel> allJudgements = new ArrayList<>();
    private JudgementDetailsAdapter adapter;
    private String strKeyword = "";
    private String strKeywordCategory = "";
    private String strUserId;
    String strUserType;
    private boolean flagged;
    private LinearLayout rl_search;
    TextView iv_tick;
    EditText et_search;
    private int pagNum = 1;
    boolean loading = true;
    private int size = 13;
    private int totalsize = 0;
    ProgressBar loader;
    private SwipeRefreshLayout swipeView;
    private String strId = null;
    private RadioGroup radioGroup;
    RadioButton rbAll;
    private TextView numWaterMark;
    private boolean isLoading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_judgements, container, false);
        findViewById();
        initData();
        adapter.setSearchText("");
        getJudgements();
        return view;

    }

    private void initData() {
        Utility.setWaterMark(getActivity(), numWaterMark);
        deviceId = Utility.getPreferences(getContext(), Constants.DEVICE_ID);
        strUserId = Utility.getPreferences(getContext(), Constants.userId);
        strUserType = Utility.getPreferences(getContext(), userType);
        adapter = new JudgementDetailsAdapter(getActivity(), allJudgements);

        flagged = getArguments().getBoolean("flagged");
        strId = getArguments().getString("id");
        if (strId != null && strId.length() > 0) {
            startActivityForResult(new Intent(getActivity(), JudgementDetails.class)
                    .putExtra("userType", strUserType)
                    .putExtra("id", strId), 200);
        }
        lv_users.setAdapter(adapter);
        lv_users.setOnItemClickListener(this);
        lv_users.setOnScrollListener(this);
    }

    public void getJudgements() {
        if (!isLoading) {
            Utility.hideKeyboard(getActivity());
            if (pagNum == 1) {
                isLoading = true;
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            } else {
                isLoading = true;
                loader.setVisibility(View.VISIBLE);
            }
            WebServices.getJudgements(Utility.getPreferences(getContext(), Constants.UniqueDeviceId),
                    flagged, deviceId, "" + pagNum, "" + size, strKeyword,
                    strUserId, strKeywordCategory, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            if (isAdded())
                                progressDialog.dismiss();
                            isLoading = false;
                            loader.setVisibility(View.GONE);
                            parseJudgementResponse(response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Utility.showToast(getContext(), Constants.error_message_api_failed);
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(getContext(), Constants.error_message_api_failed, Toast.LENGTH_SHORT).show();
                            loader.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            isLoading = false;
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Utility.showToast(getContext(), Constants.error_message_api_failed);
                            progressDialog.dismiss();
                            isLoading = false;
                            loader.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Utility.showToast(getContext(), Constants.error_message_api_failed);
                            progressDialog.dismiss();
                            isLoading = false;
                            loader.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void parseJudgementResponse(JSONObject response) {
        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            if (statusCode.equals("200")) {

                totalsize = Integer.parseInt(response.getString(Constants.STATUS_MESSAGE));
                if (pagNum == 1)
                    allJudgements.clear();
                loading = true;
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
                        String createdDate = mData.getString(Constants.createdDate);

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
                        judgementModel.setCreatedDate(Utility.convertedDate(createdDate));

                        allJudgements.add(judgementModel);
                    }
                }

                adapter.notifyDataSetChanged();
                if (pagNum == 1)
                    lv_users.smoothScrollToPosition(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        loader = (ProgressBar) view.findViewById(R.id.jm_pagination_loader);
        loader.setVisibility(View.GONE);
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(this);
        lv_users = (ListView) view.findViewById(R.id.judgements_lv_users);
        rl_search = (LinearLayout) view.findViewById(R.id.rl_search);
        et_search = (EditText) view.findViewById(R.id.et_search);
        iv_tick = view.findViewById(R.id.iv_tick);
        iv_tick.setOnClickListener(this);
        radioGroup = (RadioGroup) view.findViewById(R.id.rg_search);
        radioGroup.setOnCheckedChangeListener(this);
        rbAll = (RadioButton) view.findViewById(R.id.rb_all);
        numWaterMark = (TextView) view.findViewById(R.id.num_water_mark);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            startActivityForResult(new Intent(getActivity(), JudgementDetails.class)
                    .putExtra("userType", strUserType)
                    .putExtra("data", allJudgements.get(position))
                    .putExtra("pos", position), 200);
        } catch (Exception e) {
            Log.d("exception", e.toString());
            Constants.tempJudgmentModel = allJudgements.get(position);
            startActivityForResult(new Intent(getActivity(), JudgementDetails.class)
                    .putExtra("userType", strUserType)
                    .putExtra("pos", position), 200);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void search() {

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

        Constants.tempJudgmentModel = null;
        if (resultCode == RESULT_OK)
            if (Utility.getPreferences(getContext(), Constants.userType).equals(Constants.userTypeAdmin)) {
                String tag = data.getStringExtra("tag");
                if (requestCode == 200) {
                    if (tag.equals("add")) {
                        pagNum = 1;
                        adapter.setSearchText("");
                        getJudgements();
                    } else if (tag.equals("edit")) {

                        if (Utility.getPreferences(getActivity(), isAdmin, false)) {

                            pagNum = 1;
                            adapter.setSearchText("");
                            getJudgements();
                        }
                    } else if (tag.equals("delete")) {

                        if (Utility.getPreferences(getActivity(), isAdmin, false)) {
                            pagNum = 1;
                            adapter.setSearchText("");
                            getJudgements();
                        }
                    }
                }
            } else if (Utility.getPreferences(getContext(), Constants.userType).equals(Constants.userTypeUser)) {
                if (data.getBooleanExtra("flag_changed", false)) {
                    JudgementModel model = data.getParcelableExtra("data");
                    int pos = data.getIntExtra("pos", -1);
                    if (model != null && pos >= 0) {
                        allJudgements.set(pos, model);
                        adapter.notifyDataSetChanged();

                    } else {
                        pagNum = 1;
                        adapter.setSearchText("");
                        getJudgements();
                    }
                }
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_tick:
                searchJudgment(false);
                break;
        }
    }

    private String getKeywordCategory() {

        int selectedRadioButtonID = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonID != -1) {

            RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonID);
            String strCategory = selectedRadioButton.getText().toString();

            if (strCategory.equalsIgnoreCase("all"))
                strCategory = "";

            return strCategory;
        }
        return "";
    }

    public void setSearchView() {
        if (rl_search.getVisibility() == View.VISIBLE) {
            rl_search.setVisibility(View.GONE);
            strKeyword = "";
            strKeywordCategory = "";
            pagNum = 1;
            adapter.setSearchText("");
            getJudgements();
        } else {
            rl_search.setVisibility(View.VISIBLE);
            rbAll.setChecked(true);
            et_search.setText("");
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

                getJudgements();
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeView.setRefreshing(false);
        swipeView.isNestedScrollingEnabled();
        pagNum = 1;
        adapter.setSearchText("");
        getJudgements();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        searchJudgment(true);
    }

    private void searchJudgment(boolean isSearchTips) {
        strKeyword = et_search.getText().toString().trim();
        strKeywordCategory = getKeywordCategory();
        if (strKeyword.length() > 0 || isSearchTips) {
            pagNum = 1;
            adapter.setSearchText(strKeyword);
            getJudgements();
        } else {
            Utility.showToast(getActivity(), "Please enter search text!");
        }
    }
}