package com.legaljudgements.admin.membership.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.admin.membership.model.HeaderInfo;

import java.util.ArrayList;

public class MyListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<HeaderInfo> membershipList;
    private int userType;
    private AdapterCallback mAdapterCallback;

    public MyListAdapter(Context context, ArrayList<HeaderInfo> membershipList, int userType) {
        this.context = context;
        this.membershipList = membershipList;
        this.userType = userType;
        this.mAdapterCallback = ((AdapterCallback) context);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        HeaderInfo details =
                membershipList.get(groupPosition);
        return details;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        HeaderInfo detailInfo = (HeaderInfo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.child_row, null);
        }
        TextView select = (TextView) view.findViewById(R.id.child_row_tv_select);
        if (userType == 0) {
            select.setVisibility(View.VISIBLE);
        }
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.onMethodCallback(groupPosition);
            }
        });
        TextView description = (TextView) view.findViewById(R.id.child_description);
        description.setText(detailInfo.getDescription().trim());
        TextView duration = (TextView) view.findViewById(R.id.child_duration);
        duration.setText("Validity : " + detailInfo.getDuration().trim() + " " + detailInfo.getUnit().toLowerCase() + "(s)");
        TextView price = (TextView) view.findViewById(R.id.child_tv_price);
        price.setText("Rs. " + detailInfo.getPrice().trim());
        if (userType == 2) {
            price.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return membershipList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return membershipList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    private int lastPosition = -1;

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
        final View result;
        if (view == null) {
            LayoutInflater inf = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.group_heading, null);
            result = view;
        } else {
            result = view;
        }

      /*  Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        result.startAnimation(animation);
        lastPosition = groupPosition;*/

        TextView heading = (TextView) view.findViewById(R.id.heading);
        heading.setText(headerInfo.getTitle().trim());

        TextView heading_price = (TextView) view.findViewById(R.id.heading_price);
        heading_price.setText("Rs. " + headerInfo.getPrice().trim());

        if (userType==0 || userType==1)
            heading_price.setVisibility(View.GONE);

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface AdapterCallback {
        void onMethodCallback(int groupPosition);
    }
}