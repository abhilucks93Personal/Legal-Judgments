package com.legaljudgements.admin.dashboard.view;

/**
 * Created by Ravi on 29/07/15.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;


public class DashboardFragment extends Fragment {

    private TextView tv_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        findViewById(view);
        inItData();
        return view;
    }

    private void inItData() {
        tv_name.setText(Utility.getPreferences(getContext(), Constants.name) + " !");
    }

    private void findViewById(View view) {
        tv_name = (TextView) view.findViewById(R.id.home_tv_name);
    }


}