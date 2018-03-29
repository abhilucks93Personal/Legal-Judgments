package com.legaljudgements.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;
import com.legaljudgements.Utils.WebServices;
import com.legaljudgements.admin.navigation.NavigationAdminActivity;
import com.legaljudgements.lawyer.NavigationLawyerActivity;
import com.legaljudgements.lawyer.membership.MembershipConfirmationActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.legaljudgements.Utils.Constants.code_502;
import static com.legaljudgements.Utils.Constants.code_800;
import static com.legaljudgements.Utils.Constants.isAdmin;
import static com.legaljudgements.Utils.Constants.isLawyer;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.setStatusBarTranslucent(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ShortcutBadger.removeCount(this);

        Utility.addPreferences(this, Constants.DEVICE_ID, FirebaseInstanceId.getInstance().getToken());


        String UniqueDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Utility.addPreferences(getApplicationContext(), Constants.UniqueDeviceId, UniqueDeviceId);

        if (UniqueDeviceId.length() > 0) {
            if (Utility.getPreferences(SplashActivity.this, Constants.LoginCheck, false)) {
                String deviceId = Utility.getPreferences(SplashActivity.this, Constants.DEVICE_ID);
                if (deviceId.length() > 0) {

                    String ticker_text = getIntent().getStringExtra("tag");
                    final String jdg_id = getIntent().getStringExtra("id");
                    if (ticker_text == null) {
                        if (Utility.getPreferences(SplashActivity.this, isLawyer, false)) {
                            if (Utility.isInternetConnected(this)) {
                                WebServices.checkMembership(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                                        deviceId, Utility.getPreferences(getApplicationContext(), Constants.userId), new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                super.onSuccess(statusCode, headers, response);
                                                parseMembershipResponse(response, null, false);

                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                super.onFailure(statusCode, headers, responseString, throwable);
                                                Utility.showToast(getApplicationContext(), "membership check failed");
                                                callLoginPage();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                                Utility.showToast(getApplicationContext(), "membership check failed");
                                                callLoginPage();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                                Utility.showToast(getApplicationContext(), "membership check failed");
                                                callLoginPage();
                                            }
                                        });
                            } else {
                                netConnectionDialog();
                            }
                        } else if (Utility.getPreferences(SplashActivity.this, isAdmin, false)) {
                            Intent intent = new Intent(SplashActivity.this, NavigationAdminActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent;
                        switch (ticker_text) {

                            case "UserRegistration":
                                if (Utility.getPreferences(SplashActivity.this, isLawyer, false)) {
                                    if (Utility.isInternetConnected(this)) {
                                        WebServices.checkMembership(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                                                deviceId, Utility.getPreferences(getApplicationContext(), Constants.userId), new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        super.onSuccess(statusCode, headers, response);
                                                        parseMembershipResponse(response, jdg_id, false);

                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                        super.onFailure(statusCode, headers, responseString, throwable);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }
                                                });
                                    } else {
                                        netConnectionDialog();
                                    }
                                } else if (Utility.getPreferences(SplashActivity.this, isAdmin, false)) {
                                    intent = new Intent(this, NavigationAdminActivity.class);
                                    intent.putExtra("tag", false);
                                    intent.putExtra("id", jdg_id);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                                break;

                            case "Renewal":
                                if (Utility.getPreferences(SplashActivity.this, isLawyer, false)) {
                                    if (Utility.isInternetConnected(this)) {
                                        WebServices.checkMembership(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                                                deviceId, Utility.getPreferences(getApplicationContext(), Constants.userId), new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        super.onSuccess(statusCode, headers, response);
                                                        parseMembershipResponse(response, jdg_id, false);

                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                        super.onFailure(statusCode, headers, responseString, throwable);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }
                                                });
                                    } else {
                                        netConnectionDialog();
                                    }
                                } else if (Utility.getPreferences(SplashActivity.this, isAdmin, false)) {
                                    intent = new Intent(this, NavigationAdminActivity.class);
                                    intent.putExtra("tag", false);
                                    intent.putExtra("id", jdg_id);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                                break;

                            case "NewJudgement":
                                if (Utility.getPreferences(SplashActivity.this, isLawyer, false)) {
                                    if (Utility.isInternetConnected(this)) {
                                        WebServices.checkMembership(Utility.getPreferences(getApplicationContext(), Constants.UniqueDeviceId),
                                                deviceId, Utility.getPreferences(getApplicationContext(), Constants.userId), new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                        super.onSuccess(statusCode, headers, response);
                                                        parseMembershipResponse(response, jdg_id, true);

                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                        super.onFailure(statusCode, headers, responseString, throwable);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                                        Utility.showToast(getApplicationContext(), "membership check failed");
                                                        callLoginPage();
                                                    }
                                                });
                                    } else {
                                        netConnectionDialog();
                                    }
                                } else if (Utility.getPreferences(SplashActivity.this, isAdmin, false)) {
                                    intent = new Intent(this, NavigationAdminActivity.class);
                                    intent.putExtra("tag", true);
                                    intent.putExtra("id", jdg_id);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                                break;

                            default:
                                finish();
                        }
                    }

                } else {
                    callLoginPage();
                }
            } else {

                String ticker_text = getIntent().getStringExtra("tag");
                if (ticker_text != null) {
                    Intent intent;
                    switch (ticker_text) {

                        case "UserRegistration":
                            intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            break;
                        case "Renewal":
                            intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            break;

                        case "AccountActivate":
                            intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            break;
                        case "NewJudgement":
                            intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            break;
                        case "Registration":
                            intent = new Intent(this, SignUpActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            break;
                        case "ResetPassword":
                            intent = new Intent(this, ResetPasswordActivity.class);
                            startActivityForResult(intent, 132);

                            break;

                        default:
                            callLoginPage();
                    }
                } else {
                    callLoginPage();
                }
            }
        } else {
            finish();
        }
    }


    private void netConnectionDialog() {

        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText("Your device isn't connected to internet. Please connect and try again");
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private void callLoginPage() {

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivityForResult(intent, 100);

                }
            }
        };

        timerThread.start();
    }

    private void parseMembershipResponse(JSONObject response, String judgementId, Boolean tag) {

        try {
            String statusCode = response.getString(Constants.STATUS_CODE);
            String message = response.getString(Constants.STATUS_MESSAGE);
            String content = response.getString("content");
            Utility.addPreferences(getApplicationContext(), Constants.ConfirmationContent, content);
            if (message.equals("false") || message.equals("true"))
                Utility.addPreferences(getApplicationContext(), "FullAccess", Boolean.parseBoolean(message));
            if (statusCode.equals(Constants.code_200)) {
                if (Utility.getPreferences(SplashActivity.this, isAdmin, false)) {
                    Intent intent = new Intent(SplashActivity.this, NavigationAdminActivity.class);
                    intent.putExtra("tag", tag);
                    intent.putExtra("id", judgementId);
                    startActivity(intent);
                    finishAffinity();
                } else if (Utility.getPreferences(SplashActivity.this, isLawyer, false)) {
                    Utility.addPreferences(getApplicationContext(), Constants.MembershipExpired, false);
                    Intent intent = new Intent(SplashActivity.this, NavigationLawyerActivity.class);
                    intent.putExtra("id", judgementId);
                    intent.putExtra("tag", tag);
                    startActivity(intent);
                    finishAffinity();
                }
            } else if (statusCode.equals(code_502)) {
                startActivityForResult(new Intent(SplashActivity.this, MembershipConfirmationActivity.class)
                        .putExtra("code", statusCode), 132);

            } else if (statusCode.equals(code_800)) {
                Utility.addPreferences(getApplicationContext(), Constants.MembershipExpired, true);
                Intent intent = new Intent(SplashActivity.this, NavigationLawyerActivity.class);
                intent.putExtra("id", judgementId);
                intent.putExtra("tag", tag);
                startActivity(intent);
                finishAffinity();

               /* String deviceId = Utility.getPreferences(getApplicationContext(), Constants.DEVICE_ID);
                Utility.clearPreferenceData(getApplicationContext());
                Utility.addPreferences(getApplicationContext(), Constants.DEVICE_ID, deviceId);
                Utility.addPreferences(getApplicationContext(), Constants.LoginCheck, false);
                Utility.addPreferences(getApplicationContext(), Constants.isAdmin, false);
                Utility.addPreferences(getApplicationContext(), Constants.isLawyer, false);
                startActivity(new Intent(SplashActivity.this, MembershipConfirmationActivity.class)
                        .putExtra("code", statusCode));
                finishAffinity();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 132)
            callLoginPage();
        else
            finish();
    }
}
