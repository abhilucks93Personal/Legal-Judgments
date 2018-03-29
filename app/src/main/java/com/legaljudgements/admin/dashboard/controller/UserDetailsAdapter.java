package com.legaljudgements.admin.dashboard.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.admin.dashboard.model.UserDetailsModel;

import java.util.ArrayList;

public class UserDetailsAdapter extends ArrayAdapter<UserDetailsModel> {


    // View lookup cache
    private static class ViewHolder {
        TextView txtUserName;
        TextView txtStatus;
        TextView txtPhoneNumber, txtDistrict;
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
            viewHolder.txtUserName = (TextView) convertView.findViewById(R.id.users_list_item_tv_userName);
            viewHolder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.users_list_item_tv_phoneNumber);
            viewHolder.txtDistrict = (TextView) convertView.findViewById(R.id.users_list_item_tv_district);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.users_list_item_tv_status);

            convertView.setTag(viewHolder);

           /* Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_from_bottom);
            convertView.startAnimation(animation);*/
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            UserDetailsModel userDetailsModel = getItem(position);

            viewHolder.txtUserName.setText(userDetailsModel.getUserName());
            viewHolder.txtPhoneNumber.setText(userDetailsModel.getPhone());
            viewHolder.txtDistrict.setText(userDetailsModel.getAddress());

            String strStatus;
            if (Boolean.parseBoolean(userDetailsModel.getIsActive())) {
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
}