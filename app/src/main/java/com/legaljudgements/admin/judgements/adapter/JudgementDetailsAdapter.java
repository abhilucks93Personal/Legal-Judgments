package com.legaljudgements.admin.judgements.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.admin.judgements.model.JudgementModel;

import java.util.ArrayList;

/**
 * Created by ng on 2/19/2017.
 */
public class JudgementDetailsAdapter extends ArrayAdapter<JudgementModel> {

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtType;
        TextView txtDate;
    }

    public JudgementDetailsAdapter(Context context, ArrayList<JudgementModel> data) {
        super(context, R.layout.judgement_details_row_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JudgementModel judgementModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        viewHolder = new ViewHolder();
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.judgements_row_item, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.judgements_list_item_tv_title);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.judgements_list_item_tv_type);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.judgement_list_item_tv_date);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtTitle.setText(judgementModel.getJudgementType());
        viewHolder.txtType.setText(judgementModel.getShortDescription());
        viewHolder.txtDate.setText(Utility.formatDateForDisplay(judgementModel.getCreatedDate()));

        return convertView;
    }
}