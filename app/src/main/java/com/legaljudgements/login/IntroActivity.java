package com.legaljudgements.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Utility;


/**
 * Created by sanjiv on 18/7/16.
 */
public class IntroActivity extends Activity implements View.OnClickListener {

    public static Animation fromBottom;
    LinearLayout mLayout;
    TextView mTextView_register, mTextView_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setStatusBarTranslucent(this, true);
        setContentView(R.layout.intro_screen);

        findViewById();
        setListner();
        call_animation();


    }


    private void findViewById() {
        mLayout = (LinearLayout) findViewById(R.id.intro_ll_registeration);
        mTextView_login = (TextView) findViewById(R.id.intro_tv_login);
        mTextView_register = (TextView) findViewById(R.id.intro_tv_register);
    }

    private void setListner() {
        mTextView_login.setOnClickListener(this);
        mTextView_register.setOnClickListener(this);
    }

    private void call_animation() {
        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom);
        mLayout.setVisibility(View.VISIBLE);
        mLayout.startAnimation(fromBottom);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.intro_tv_register:
                startActivity(new Intent(IntroActivity.this, SignUpActivity.class));
                break;

            case R.id.intro_tv_login:
                startActivity(new Intent(IntroActivity.this, LoginActivity.class)
                        .putExtra("action", "Login")
                        .putExtra("usr_login_type", ""));
                break;

        }
    }
}
