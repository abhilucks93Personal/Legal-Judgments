package com.legaljudgements.admin.dashboard.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.admin.dashboard.model.UserDetailsModel;

import java.util.ArrayList;

public class UserDetailsAdapter extends ArrayAdapter<UserDetailsModel> {


    // View lookup cache
    private static class ViewHolder {
        TextView txtUserName;
        TextView txtStatus;
        TextView txtPhoneNumber, txtDistrict, tvChamber, tvFullAccess, tvMembership;
    }

    public UserDetailsAdapter(Context context, ArrayList<UserDetailsModel> data) {
        super(context, R.layout.user_details_row_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_details_row_item, parent, false);
            viewHolder.txtUserName = convertView.findViewById(R.id.users_list_item_tv_userName);
            viewHolder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.users_list_item_tv_phoneNumber);
            viewHolder.txtDistrict = (TextView) convertView.findViewById(R.id.users_list_item_tv_district);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.users_list_item_tv_status);
            viewHolder.tvChamber = (TextView) convertView.findViewById(R.id.user_detail_tv_chamber);
            viewHolder.tvFullAccess = (TextView) convertView.findViewById(R.id.user_detail_tv_full_access);
            viewHolder.tvMembership = (TextView) convertView.findViewById(R.id.user_detail_tv_membership);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            UserDetailsModel userDetailsModel = getItem(position);

            viewHolder.txtUserName.setText(userDetailsModel.getUserName());
            viewHolder.txtPhoneNumber.setText(userDetailsModel.getPhone());
            viewHolder.txtDistrict.setText(userDetailsModel.getAddress());
            viewHolder.tvFullAccess.setText(parseAccess(userDetailsModel.getScope()));
            viewHolder.tvChamber.setText(userDetailsModel.getName());

            String strStartDate = Utility.formatDateForDisplay(Utility.convertedDate(userDetailsModel.getMembershipStartDate()));
            String strEndDate = Utility.formatDateForDisplay(Utility.convertedDate(userDetailsModel.getMembershipEndDate()));

            viewHolder.tvMembership.setText(strStartDate + " - " + strEndDate);

            String strStatus;
            if (Utility.checkExpiry(Utility.convertedDate(userDetailsModel.getMembershipEndDate()))) {
                strStatus = "Term Expiry";
                viewHolder.txtStatus.setTextColor(getContext().getResources().getColor(R.color.colorRed));
            } else if (Boolean.parseBoolean(userDetailsModel.getIsActive())) {
                strStatus = "Active";
                viewHolder.txtStatus.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
            } else {
                strStatus = "DeActive";
                viewHolder.txtStatus.setTextColor(getContext().getResources().getColor(R.color.colorRed));
            }
            viewHolder.txtStatus.setText(strStatus);
        } catch (Exception e) {
            Log.e("", "");
        }
        return convertView;
    }


    private String parseAccess(String scope) {
        if (scope.equals("true"))
            return "Yes";
        else
            return "No";
    }
}