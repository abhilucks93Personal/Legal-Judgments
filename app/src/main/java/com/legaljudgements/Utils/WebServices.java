package com.legaljudgements.Utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * all webservice methods are mentioned here which are used from various classes and adapters
 */
public class WebServices {

    private static final String BASE_URL = "http://api.legaljudgments.in/LJWS/";

    private static String LOGIN = "UserLogin";

    private static String SIGN_UP = "UserRegistration";


    private static String UPDATE_PROFILE = "UpdateUser";
    private static String ADD_MEMBERSHIP = "AddMembership";
    private static String UPDATE_MEMBERSHIP = "UpdateMembership";
    private static String GET_MEMBERSHIP = "GetActiveMemberships";
    private static String CHECK_MEMBERSHIP = "CheckMembershipValidity";
    private static String FORGOT_PASSWORD = "ForgotPassword";
    private static String RESET_PASSWORD = "ResetPassword";
    private static String GET_USERS = "GetAllUsers";
    private static String DELETE_MEMBERSHIP = "DeleteMembership";
    private static String ADD_JUDGEMENT = "AddJudgement";
    private static String ADD_JUDGEMENT_TEST = "AddJudgementTest";
    private static String UPDATE_JUDGEMENT = "UpdateJudgement";
    private static String GET_JUDGEMENT = "GetActiveJudgements";
    private static String GET_FLAGGED_JUDGEMENT = "GetFlaggedJudgements";
    private static String DELETE_JUDGEMENT = "DeleteJudgement";
    private static String FLAG_JUDGEMENT = "ToggleFlag";
    private static String ACTIVATE_USER = "ActivateDeactivateUser";
    private static String JUDGEMENT_BY_ID = "GetJudgementDetails";
    private static String USER_DETAIL_BY_ID = "GetUserDetails";
    private static String RENEW_MEMBERSHIP = "Renewal";
    private static String TERMS_CONTENT = "GetTermsContent";
    private static String DELETE_USER = "DeleteUser";


    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String userTypeUser = "USER";
    private static String userTypeAdmin = "ADMIN";


    public static void userLogin(String UniqueDeviceId, String deviceId, String userName, String Password, JsonHttpResponseHandler jsonHttpResponseHandler) {

     /*  String apiUrl = getApiUrl(UniqueDeviceId, deviceId, userName, Password);*/
        RequestParams params = new RequestParams();
        params.put("UniqueDeviceId", UniqueDeviceId);
        params.put("DeviceId", deviceId);
        params.put("UserName", userName);
        params.put("Password", Password);
        WebServices._post(LOGIN, params, jsonHttpResponseHandler);
    }


    public static void userSignUp(String UniqueDeviceId, String deviceId, String strUserName, String strpassword, String strName, String strEmail, String strNum, String strAddress, String strChamber, String strMembershipId, JsonHttpResponseHandler jsonHttpResponseHandler) {
     /*  String apiUrl = getApiUrl(UniqueDeviceId, deviceId, strUserName, strpassword, strName, strEmail, strNum, strAddress, strMembershipId, userTypeUser);*/
        RequestParams params = new RequestParams();
        params.put("UniqueDeviceId", UniqueDeviceId);
        params.put("DeviceId", deviceId);
        params.put("UserName", strUserName);
        params.put("Password", strpassword);
        params.put("Name", strChamber);
        params.put("Email", strEmail);
        params.put("Phone", strNum);
        params.put("Address", strAddress);
        params.put("MembershipId", strMembershipId);
        params.put("UserType", userTypeUser);
        WebServices._post(SIGN_UP, params, jsonHttpResponseHandler);
    }

    public static void updateProfile(String UniqueDeviceId, String deviceId, String strUserName, String strpassword, String strName, String strNum, String strEmail, String strAddress, String strMembershipId, String strUserId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, strUserName, strpassword, strName, strEmail, strNum, strAddress, strMembershipId, strUserId, strUserId);
        WebServices.post(UPDATE_PROFILE, apiUrl, jsonHttpResponseHandler);
    }


    public static void addUpdateMembership(String UniqueDeviceId, Boolean edit, String deviceId, String title, String description, String validity, String price, String image, String membershipId, String createdBy, String scope, String validityUnit, JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("UniqueDeviceId", UniqueDeviceId);
        params.put("DeviceId", deviceId);
        params.put("Title", title);
        params.put("Description", description);
        params.put("ValidityInMonths", validity);
        params.put("Price", price);
        params.put("ImageName", image);
        params.put("FullAccess", scope);

        if (!edit) {
            params.put("CreatedBy", createdBy);
            params.put("ValidityUnit", validityUnit);
            WebServices._post(ADD_MEMBERSHIP, params, jsonHttpResponseHandler);
        } else {
            params.put("UpdatedBy", createdBy);
            params.put("MembershipId", membershipId);
            params.put("ValidityUnit", validityUnit);
            WebServices._post(UPDATE_MEMBERSHIP, params, jsonHttpResponseHandler);
        }
    }


    public static void addUpdateJudgement(String UniqueDeviceId, boolean editMode, String deviceId, String str_title, String str_court, String str_judgement_type, String str_date, String str_case_title, String str_case_type, String str_short_desc, String str_full_desc, String str_doc, String str_drive_url, String str_scope, String userId, String str_judgement_id, JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams paramslist = new RequestParams();
        paramslist.put("UniqueDeviceId", UniqueDeviceId);
        paramslist.put("DeviceId", deviceId);
        paramslist.put("Title", str_title);
        paramslist.put("Court", str_court);
        paramslist.put("Judgementtype", str_judgement_type);
        paramslist.put("DateOfJudgement", str_date);
        paramslist.put("CaseTitle", str_case_title);
        paramslist.put("CaseType", str_case_type);
        paramslist.put("ShortDescription", str_short_desc);
        paramslist.put("DetailedDescription", str_full_desc);
        paramslist.put("JudgementDoc", str_doc);
        paramslist.put("GoogleDriveUrl", str_drive_url);
        paramslist.put("IsPublic", str_scope);

        if (!editMode) {
            paramslist.put("CreatedBy", userId);
            WebServices._post(ADD_JUDGEMENT, paramslist, jsonHttpResponseHandler);
        } else {
            paramslist.put("UpdatedBy", userId);
            paramslist.put("JudgementId", str_judgement_id);
            WebServices._post(UPDATE_JUDGEMENT, paramslist, jsonHttpResponseHandler);
        }
    }


    public static void getMemberships(String UniqueDeviceId, String deviceId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId);
        WebServices.post(GET_MEMBERSHIP, apiUrl, jsonHttpResponseHandler);
    }

    public static void checkMembership(String UniqueDeviceId, String deviceId, String userId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, userId);
        WebServices.post(CHECK_MEMBERSHIP, apiUrl, jsonHttpResponseHandler);
    }

    public static void forgotPassword(String UniqueDeviceId, String deviceId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, "ANDROID");
        WebServices.post(FORGOT_PASSWORD, apiUrl, jsonHttpResponseHandler);
    }

    public static void resetPassword(String UniqueDeviceId, String deviceId, String str_pswrd, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, str_pswrd);
        WebServices.post(RESET_PASSWORD, apiUrl, jsonHttpResponseHandler);
    }

    public static void getUsers(String UniqueDeviceId, String deviceId, String userType, String page, String size, String strKeyword, String activeStatus, String memberShipId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, userType, page, size, strKeyword, activeStatus, memberShipId);
        WebServices.post(GET_USERS, apiUrl, jsonHttpResponseHandler);
    }

    public static void activateUser(String UniqueDeviceId, String deviceId, String userId, String startDate, String endDate, String status, String scope, String strUserId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, userId, startDate, endDate, status, strUserId, scope);
        WebServices.post(ACTIVATE_USER, apiUrl, jsonHttpResponseHandler);
    }

    public static void getJudgements(String UniqueDeviceId, Boolean flagged, String deviceId, String strPageNo, String strPageSize, String strkeyword, String strUserId, String strKeywordCategory, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, strPageNo, strPageSize, strkeyword, strUserId, strKeywordCategory);
        String methodName;
        if (flagged)
            methodName = GET_FLAGGED_JUDGEMENT;
        else
            methodName = GET_JUDGEMENT;
        WebServices.post(methodName, apiUrl, jsonHttpResponseHandler);
    }

    public static void DeleteMembership(String UniqueDeviceId, String deviceId, String id, String userId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, id, userId);
        WebServices.post(DELETE_MEMBERSHIP, apiUrl, jsonHttpResponseHandler);
    }

    public static void deleteJudgement(String UniqueDeviceId, String deviceId, String id, String userId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, id, userId);
        WebServices.post(DELETE_JUDGEMENT, apiUrl, jsonHttpResponseHandler);
    }


    public static void getJudgementById(String uniqueDeviceId, String deviceId, String strJudgementId, String userId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(uniqueDeviceId, deviceId, strJudgementId, userId);
        WebServices.post(JUDGEMENT_BY_ID, apiUrl, jsonHttpResponseHandler);
    }

    public static void getUserDetailById(String uniqueDeviceId, String deviceId, String strUserUpdateId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(uniqueDeviceId, deviceId, strUserUpdateId);
        WebServices.post(USER_DETAIL_BY_ID, apiUrl, jsonHttpResponseHandler);
    }

    public static void getTermsContent(String methodName, String uniqueDeviceId, String deviceId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(uniqueDeviceId, deviceId);
        WebServices.post(methodName, apiUrl, jsonHttpResponseHandler);
    }

    public static void renewMembership(String uniqueDeviceId, String deviceId, String userId, String memberShipId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(uniqueDeviceId, deviceId, userId, memberShipId);
        WebServices.post(RENEW_MEMBERSHIP, apiUrl, jsonHttpResponseHandler);
    }

    public static void flagJudgement(String UniqueDeviceId, String deviceId, String id, String userId, String status, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(UniqueDeviceId, deviceId, id, userId, status);
        WebServices.post(FLAG_JUDGEMENT, apiUrl, jsonHttpResponseHandler);
    }

    public static void deleteUser(String userId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String apiUrl = getApiUrl(userId);
        WebServices.post(DELETE_USER, apiUrl, jsonHttpResponseHandler);
    }


    private static String getApiUrl(String... str) {
        String apiUrl = "/";
        try {
            for (String strr : str) {
                if (strr.length() > 0) {
                    apiUrl += URLEncoder.encode(strr, "UTF-8") + "/";
                    apiUrl = apiUrl.replace("+", "%20");
                } else {
                    apiUrl += "null/";
                }
            }
            if (apiUrl.length() > 0 && apiUrl.charAt(apiUrl.length() - 1) == '/') {
                apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return apiUrl;
    }


    private static void post(String methodName, String apiNameUrl, JsonHttpResponseHandler jsonHttpResponseHandler) {
        client.setTimeout(60000);
        RequestParams params = new RequestParams();
        String finalUrl = BASE_URL + methodName + apiNameUrl;
        Log.e("Post url...", "" + finalUrl);
        client.post(finalUrl, params, jsonHttpResponseHandler);
    }

    public static void get(String apiNameUrl, RequestParams params, JsonHttpResponseHandler jsonHttpResponseHandler) {
        client.setTimeout(60000);
        String finalUrl = BASE_URL + apiNameUrl;
        Log.e("Post url...", "" + finalUrl);
        client.get(finalUrl, params, jsonHttpResponseHandler);
    }

    public static void _post(String methodName, RequestParams params, JsonHttpResponseHandler jsonHttpResponseHandler) {
        client.setTimeout(60000);
        String finalUrl = BASE_URL + methodName;
        Log.v("Post url...", "" + finalUrl);
        Log.v("Post Parameters", "" + params);
        client.post(finalUrl, params, jsonHttpResponseHandler);
    }


    public interface WebServiceListener {

        public void onSuccess(String arg0);

        public void onFailure(String arg1);

    }

    /**
     * dont call this method
     */
    public static void onInit() {
        client.addHeader("ContentType", "application/x-www-form-urlencoded");
        client.addHeader("Access", "application/json");
        client.setTimeout(60000);
    }

}

