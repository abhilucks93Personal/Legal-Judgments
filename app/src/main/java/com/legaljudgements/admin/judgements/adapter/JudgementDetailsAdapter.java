package com.legaljudgements.admin.judgements.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.admin.judgements.model.JudgementModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ng on 2/19/2017.
 */
public class JudgementDetailsAdapter extends ArrayAdapter<JudgementModel> {

    private ArrayList<String> mSearchTexts = new ArrayList<>();

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtType;
        TextView txtDate;
    }

    public void setSearchText(String mSearchText) {
        mSearchTexts.clear();
        if (mSearchText != null && mSearchText.length() > 0) {
            mSearchTexts.add(mSearchText);
            String[] searchTextArray = mSearchText.split(" ");
            ArrayList<String> stringList = new ArrayList<>(Arrays.asList(searchTextArray));
            mSearchTexts.addAll(stringList);
        }
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

        String fullText = judgementModel.getShortDescription();
        // highlight search text

       /* Spannable spannable = new SpannableString(fullText);
        ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLACK});
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.YELLOW);


            for (String str : mSearchTexts) {
                int startPos = fullText.toLowerCase(Locale.US).indexOf(str.toLowerCase(Locale.US));
                int endPos = startPos + str.length();

                if (startPos != -1) {

                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(backgroundColorSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }*/

        viewHolder.txtType.setText(highlightSearchKey(fullText));

        return convertView;
    }


    private Spannable highlightSearchKey(String title) {
        Spannable highlight;
        Pattern pattern;
        Matcher matcher;
        String title_str;

        title_str = Html.fromHtml(title).toString();
        highlight = (Spannable) Html.fromHtml(title);

        for (String str : mSearchTexts) {
            pattern = Pattern.compile("(?i)" + str);
            matcher = pattern.matcher(title_str);
            while (matcher.find()) {
                highlight.setSpan(
                        new BackgroundColorSpan(Color.YELLOW),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return highlight;
    }
}