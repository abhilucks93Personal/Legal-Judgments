package com.legaljudgements.Utils;

import com.legaljudgements.admin.judgements.model.JudgementModel;

/**
 * Created by abhishekagarwal on 2/9/17.
 */

public class Constants {


    public static final int ADMIN = 0;
    public static final int LAWYER = 1;

    public static final String DEVICE_ID = "deviceId";
    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS_MESSAGE = "statusMsg";
    public static final String STATUS_DATA = "data";
    public static final int PASSWORD_MIN_LENGTH = 5;
    public static String LoginCheck = "loginCheck";
    public static String isAdmin = "isAdmin";
    public static String isLawyer = "isLawyer";

    public static String userId = "userId";
    public static String userName = "userName";
    public static String password = "password";
    public static String name = "name";
    public static String email = "email";
    public static String phone = "phone";
    public static String address = "address";
    public static String membershipId = "membershipId";
    public static String isActive = "isActive";
    public static String membershipEndDate = "membershipEndDate";
    public static String membershipStartDate = "membershipStartDate";
    public static String userType = "userType";
    public static String membershipTitle = "membershipTitle";
    public static String userTypeUser = "USER";
    public static String userTypeAdmin = "ADMIN";
    public static String membershipValidity = "membershipValidity";
    public static String chamber = "chamber";

    public static String judgementId = "judgementId";
    public static String title = "title";
    public static String court = "court";
    public static String judgementType = "judgementType";
    public static String dateOfJudgement = "dateOfJudgement";
    public static String caseTitle = "caseTitle";
    public static String caseType = "caseType";
    public static String shortDescription = "shortDescription";
    public static String detailedDescription = "detailedDescription";
    public static String judgementDoc = "judgementDoc";
    public static String googleDriveUrl = "googleDriveUrl";
    public static String isPublic = "isPublic";
    public static String isFlagged = "isFlagged";
    public static String createdDate = "createdDate";
    public static String DEVICE_CHANGED = "Sorry! Your account isn't registered with this device.";

    public static String code_200 = "200";
    public static String code_404 = "404";
    public static String code_400 = "400";
    public static String code_401 = "401";
    public static String code_501 = "501";
    public static String code_502 = "502";
    public static String code_503 = "503";
    public static String code_800 = "800";
    public static String error_message_device_toke = "Unable to get device token! Please try after some time.";
    public static String error_message_invalid_user = "Please enter a valid Username and Password";
    public static String error_message_api_failed = "Something went wrong! Please try again later.";
    public static String error_message_device_not_registered = "Sorry! Your device isn't registered with us.";
    public static String reset_password_message = "Click on the push notification to reset your password";
    public static String error_message_Password_mandatory = "Password field is mandatory.";
    public static String error_message_Password_length = "Password should be of minimum 5 characters";
    public static String error_message_psswrd_mismatch = "Password and Confirm Password does not match.";
    public static String reset_password_success_message = "Password has been reset successfully. Login to continue.";
    public static String offline_msg = "Your device isn't connected with internet. Please connect and try again.";
    public static String UniqueDeviceId = "UniqueDeviceId";
    public static String MembershipExpired = "MembershipExpired";
    public static String ConfirmationContent = "ConfirmationContent";

    public static JudgementModel tempJudgmentModel = null;

    public static String tempDetailedDescription = "";

}
