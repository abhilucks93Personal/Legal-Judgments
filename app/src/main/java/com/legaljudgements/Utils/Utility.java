package com.legaljudgements.Utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.legaljudgements.R;
import com.legaljudgements.login.SplashActivity;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static com.legaljudgements.Utils.Constants.isLawyer;


/**
 * @author Wildnet technologies
 */
public class Utility {

    public static void setStatusBarTranslucent(Activity context, boolean makeTranslucent) {
        if (makeTranslucent) {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void datePickerDialog(Activity context, DatePickerDialog.OnDateSetListener listner) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(context, listner,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        dialog.show();
    }


    public static void showSnackBar(Activity context, String str) {
        hideKeyboard(context);
        Snackbar.make(context.findViewById(android.R.id.content), str, Snackbar.LENGTH_LONG).show();

    }

    public static String convertedDate(String str) {

        String[] separated = str.split("T");
        String date = formatDateFromString("yyyy-MM-dd", "MM-dd-yyyy", separated[0]);

        return date;
    }

    public static String convertedDateDisplay(String str) {

        String[] separated = str.split("T");
        String date = formatDateFromString("yyyy-MM-dd", "MMM dd, yyyy", separated[0]);

        return date;
    }

    public static String formatDateFromString(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    public static String formatDateForDisplay(String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat("MM-dd-yyyy", java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    public static String formatDateForDisplayReverse(String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat("MM-dd-yyyy", java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    public static void showEditTextsAsMandatory(TextInputLayout... ets) {
        for (TextInputLayout et : ets) {


            String hint = et.getHint().toString() + "  ";

            et.setHint(Html.fromHtml(hint + "<font color=\"#ff0000\">" + "* " + "</font>"));
        }
    }

    public static void showEditTextsAsMandatory(EditText... ets) {
        for (EditText et : ets) {


            String hint = et.getHint().toString() + "  ";

            et.setHint(Html.fromHtml(hint + "<font color=\"#ff0000\">" + "* " + "</font>"));
        }
    }

    public static void displayAlert(final Context context, String title, String msg) {
        new AlertDialog.Builder(context).setMessage(msg).setTitle(title).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do your code here
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        }).show();
    }

    private static void call() {


    }

    public static boolean checkPermission(Context context, String permission) {
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        //String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        String expression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void showDevelopmentToast(Context context) {
        Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
    }

    public static void addPreferences(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Preferences_", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void addPreferences(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Preferences_", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void addAllPreferences(Context context, String... key) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Preferences_", Context.MODE_PRIVATE).edit();
        for (String str : key) {
            editor.putString(str, str);
            editor.commit();
        }

    }

    public static String getPreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("Preferences_", Context.MODE_PRIVATE);
        String text = prefs.getString(key, "");
        return text;
    }

    public static Boolean getPreferences(Context context, String key, boolean defaut) {
        SharedPreferences prefs = context.getSharedPreferences("Preferences_", Context.MODE_PRIVATE);
        Boolean text = prefs.getBoolean(key, defaut);
        return text;
    }

    public static void clearPreferenceData(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("Preferences_", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static boolean lengthValidation(String str) {
        if (str.length() > 0)
            return true;
        else
            return false;
    }

    public static boolean isInternetConnected(Activity mContext) {

		/*
         * final ConnectivityManager connec = (ConnectivityManager)
		 * mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		 *
		 * if (connec != null&& (connec.getNetworkInfo(1).getState() ==
		 * NetworkInfo.State.CONNECTED)|| (connec.getNetworkInfo(0).getState()
		 * == NetworkInfo.State.CONNECTED)) { return true; } return false;
		 */

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            showSnackBar(mContext, Constants.offline_msg);
        }

        return false;
    }

    public static void showToast(Context mContext, String string) {
        Toast t = Toast.makeText(mContext, string, Toast.LENGTH_SHORT);
        t.show();

    }

    public static boolean isLocationEnabled(Context context) {

        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (gps_enabled == true && network_enabled == true) {
            return true;
        } else {
            return false;
        }

    }

    public static String printnotificationDifference(Date endDate) {

        //milliseconds
        StringBuilder updatedTime = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));
        Date startDate = null;
        try {
            startDate = dateFormat.parse(dateFormat.format(cal.getTime()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long different = startDate.getTime() - endDate.getTime();

        //       System.out.println("startDate : " + startDate);
        //       System.out.println("endDate : "+ endDate);
        //       System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
        if (elapsedDays > 0) {
            updatedTime.append(elapsedDays + " days");
            return updatedTime.toString();

        }
        if (elapsedHours > 0) {
            updatedTime.append(elapsedHours + " hour");
            return updatedTime.toString();


        }
        if (elapsedMinutes > 0) {
            updatedTime.append(elapsedMinutes + " minutes");
            return updatedTime.toString();


        }
        if (elapsedSeconds > 0) {
            updatedTime.append(elapsedSeconds + " second");
            return updatedTime.toString();


        }
        return updatedTime.toString();

    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }


    public static void password_visible(Activity activity, EditText et_password, ImageView iv) {
        if (et_password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            iv.setImageDrawable(activity.getResources().getDrawable(R.drawable.eye));
            et_password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            iv.setImageDrawable(activity.getResources().getDrawable(R.drawable.blue_eye));
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        et_password.setSelection(et_password.getText().length());
    }

    public static String getCurrentDate(String pattern) {

        String dateInString = new SimpleDateFormat(pattern).format(new Date());

        return dateInString;
    }

    public static String getDateAfterTime(int durationInMonth, String unit, String pattern) {

        Date current = new Date();
        System.out.println(current);
        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        if (unit.equals("MONTH"))
            cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) + durationInMonth));
        else
            cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) + durationInMonth));
        current = cal.getTime();
        System.out.println(current);
        String dateInString = new SimpleDateFormat(pattern).format(current);

        return dateInString;
    }

    public static void alertDialog(Activity activity, String message) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.common_dialog_layout);
        TextView tv_message = (TextView) dialog.findViewById(R.id.common_dialog_message);
        tv_message.setText(message);
        TextView tv_ok = (TextView) dialog.findViewById(R.id.common_dialog_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();

    }

    public static void setWaterMark(Activity context, TextView numWaterMark) {
        if (numWaterMark != null) {
            if (Utility.getPreferences(context, isLawyer, false)) {
                String number = Utility.getPreferences(context, Constants.phone);
                numWaterMark.setText(number);
            }

        }

    }
}
