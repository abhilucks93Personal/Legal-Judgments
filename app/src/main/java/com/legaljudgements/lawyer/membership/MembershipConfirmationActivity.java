package com.legaljudgements.lawyer.membership;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;

/**
 * Created by ng on 2/8/2017.
 */
public class MembershipConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_ok;
    private TextView tv_msg;
    TextView tv_contactNum1, tv_contactNum2;
    private boolean isRenew;
    private TextView tv_accnt_details;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.membership_confirmation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById();

        tv_accnt_details.setText(Html.fromHtml(Utility.getPreferences(getApplicationContext(), Constants.ConfirmationContent)));

        isRenew = getIntent().getBooleanExtra("renew", false);
        if (isRenew)
            tv_msg.setText(getString(R.string.membership_renew_msg));


    }

    private void findViewById() {
        tv_accnt_details = (TextView) findViewById(R.id.tv_account_details);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_ok = (TextView) findViewById(R.id.membership_confirmation_tv_submit);
        tv_ok.setOnClickListener(this);

        tv_contactNum1 = (TextView) findViewById(R.id.contactNum1);
        tv_contactNum1.setOnClickListener(this);
        tv_contactNum2 = (TextView) findViewById(R.id.contactNum2);
        tv_contactNum2.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                /*if (!isRenew) {
                    startActivity(new Intent(MembershipConfirmationActivity.this, SplashActivity.class));
                    finishAffinity();
                } else*/
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
       /* if (!isRenew) {
            startActivity(new Intent(MembershipConfirmationActivity.this, SplashActivity.class));
            finishAffinity();
        } else*/
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.membership_confirmation_tv_submit:
              /*  if (!isRenew) {
                    startActivity(new Intent(MembershipConfirmationActivity.this, SplashActivity.class));
                    finishAffinity();
                } else*/
                super.onBackPressed();
                break;

            case R.id.contactNum1:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tv_contactNum1.getText()));
                startActivity(intent);
                break;

            case R.id.contactNum2:
                Intent intent2 = new Intent(Intent.ACTION_DIAL);
                intent2.setData(Uri.parse("tel:" + tv_contactNum2.getText()));
                startActivity(intent2);
                break;

        }
    }
}
