package com.vb.vaultbox.Utills;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    private static String NAME = "NAME";
    private static String FirstNAME = "FIRSTNAME";
    private static String LastNAME = "LASTNAME";
    private static String EMAILID = "EMAILID";
    private static String mobile = "MOBILE";
    private static String user_id = "USER_ID";
    private static String SESSION_CHECK_LOGIN = "SESSION_CHECK_LOGIN";
    private static String DOB = "DOB";
    private static String Address = "ADDRESS";
    private static String city = "CITY";
    private static String image = "IMAGE";
    private static String country = "COUNTRY";
    private static String zipcode = "ZIPCODE";
    private static String firebaseid = "FIREBASEID";
    private static String Password = "Password";
    private static String user_type = "USER_TYPE";
    private static String gender = "GENDER";
    private static String followers = "FOLLOWERS";
    private static String following = "FOLLOWING";
    private static String Bio = "BIO";

    public static void save_password(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, Password, value);
    }

    public static String get_password(SharedPreferences prefs) {
        return prefs.getString(Password, "");
    }


    private static String mlastname = "mlastname";

    public static void save_name(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, NAME, value);
    }

    public static String get_name(SharedPreferences prefs) {
        return prefs.getString(NAME, "");
    }

    public static void save_mlastname(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mlastname, value);
    }

    public static String get_mlastname(SharedPreferences prefs) {
        return prefs.getString(mlastname, "");
    }

    private static String mdob = "mdob";

    public static void save_mdob(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mdob, value);
    }

    public static String get_mdob(SharedPreferences prefs) {
        return prefs.getString(mdob, "");
    }

    private static String mdod = "mdod";

    public static void save_mdod(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mdod, value);
    }

    public static String get_mdod(SharedPreferences prefs) {
        return prefs.getString(mdod, "");
    }

    private static String mfathername = "mfathername";

    public static void save_mfathername(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mfathername, value);
    }

    public static String get_mfathername(SharedPreferences prefs) {
        return prefs.getString(mfathername, "");
    }

    private static String mmothername = "mmothername";

    public static void save_mmothername(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mmothername, value);
    }

    public static String get_mmothername(SharedPreferences prefs) {
        return prefs.getString(mmothername, "");
    }

    public static void save_firebaseid(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, firebaseid, value);
    }

    public static String get_firebaseid(SharedPreferences prefs) {
        return prefs.getString(firebaseid, "");
    }


    private static String mprovince = "mprovince";

    public static void save_mprovince(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mprovince, value);
    }

    public static String get_mprovince(SharedPreferences prefs) {
        return prefs.getString(mprovince, "");
    }

    private static String mpincode = "mpincode";

    public static void save_mpincode(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mpincode, value);
    }

    public static String get_mpincode(SharedPreferences prefs) {
        return prefs.getString(mpincode, "");
    }

    private static String mmobilenum = "mmobilenum";

    public static void save_mmobilenum(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mmobilenum, value);
    }

    public static String get_mmobilenum(SharedPreferences prefs) {
        return prefs.getString(mmobilenum, "");
    }

    private static String midmark = "midmark";

    public static void save_midmark(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, midmark, value);
    }

    public static String get_midmark(SharedPreferences prefs) {
        return prefs.getString(midmark, "");
    }

    private static String mweight = "mweight";

    public static void save_mweight(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mweight, value);
    }

    public static String get_mweight(SharedPreferences prefs) {
        return prefs.getString(mweight, "");
    }

    private static String mheight = "mheight";

    public static void save_mheight(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mheight, value);
    }

    public static String get_mheight(SharedPreferences prefs) {
        return prefs.getString(mheight, "");
    }

    private static String meyecolr = "meyecolr";

    public static void save_meyecolr(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, meyecolr, value);
    }

    public static String get_meyecolr(SharedPreferences prefs) {
        return prefs.getString(meyecolr, "");
    }

    private static String mhaircolor = "mhaircolor";

    public static void save_mhaircolor(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mhaircolor, value);
    }

    public static String get_mhaircolor(SharedPreferences prefs) {
        return prefs.getString(mhaircolor, "");
    }

    private static String manysuspicius = "manysuspicius";

    public static void save_manysuspicius(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, manysuspicius, value);
    }

    public static String get_manysuspicius(SharedPreferences prefs) {
        return prefs.getString(manysuspicius, "");
    }

    private static String mcdate = "mcdate";

    public static void save_mcdate(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mcdate, value);
    }

    public static String get_mcdate(SharedPreferences prefs) {
        return prefs.getString(mcdate, "");
    }

    private static String mcomplainby = "mcomplainby";

    public static void save_mcomplainby(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mcomplainby, value);
    }

    public static String get_mcomplainby(SharedPreferences prefs) {
        return prefs.getString(mcomplainby, "");
    }

    private static String mreward = "mreward";

    public static void save_mreward(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mreward, value);
    }

    public static String get_mreward(SharedPreferences prefs) {
        return prefs.getString(mreward, "");
    }

    private static String mrewardinfo = "mrewardinfo";

    public static void save_mrewardinfo(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mrewardinfo, value);
    }

    public static String get_mrewardinfo(SharedPreferences prefs) {
        return prefs.getString(mrewardinfo, "");
    }

    public static void savePreference(SharedPreferences prefs, String key, Boolean value) {
        Editor e = prefs.edit();
        e.putBoolean(key, value);
        e.commit();
    }

    public static void savePreference(SharedPreferences prefs, String key, int value) {
        Editor e = prefs.edit();
        e.putInt(key, value);
        e.commit();
    }

    public static void savePreference(SharedPreferences prefs, String key, String value) {
        Editor e = prefs.edit();
        e.putString(key, value);
        e.apply();
    }

    public static void dataclear(SharedPreferences prefs) {
        Editor e = prefs.edit();
        e.clear();
        e.commit();
    }

    public static void save_check_login(SharedPreferences prefs, Boolean value) {
        SessionManager.savePreference(prefs, SESSION_CHECK_LOGIN, value);
    }

    public static Boolean get_check_login(SharedPreferences prefs) {
        return prefs.getBoolean(SESSION_CHECK_LOGIN, false);
    }

    public static void save_fisrtName(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, FirstNAME, value);
    }

    public static String get_fisrtName(SharedPreferences prefs) {
        return prefs.getString(FirstNAME, "");
    }

    public static void save_lastName(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, LastNAME, value);
    }

    public static String get_lastName(SharedPreferences prefs) {
        return prefs.getString(LastNAME, "");
    }

    public static void save_emailid(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, EMAILID, value);
    }

    public static String get_emailid(SharedPreferences prefs) {
        return prefs.getString(EMAILID, "");
    }

    public static void save_mobile(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, mobile, value);
    }

    public static String get_mobile(SharedPreferences prefs) {
        return prefs.getString(mobile, "");
    }

    public static void save_user_id(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, user_id, value);
    }

    public static String get_user_id(SharedPreferences prefs) {
        return prefs.getString(user_id, "");
    }

    public static void save_dob(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, DOB, value);
    }

    public static String get_dob(SharedPreferences prefs) {
        return prefs.getString(DOB, "");
    }

    public static void save_address(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, Address, value);
    }

    public static String get_address(SharedPreferences prefs) {
        return prefs.getString(Address, "");
    }

    public static void save_city(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, city, value);
    }

    public static String get_city(SharedPreferences prefs) {
        return prefs.getString(city, "");
    }

    public static void save_image(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, image, value);
    }

    public static String get_image(SharedPreferences prefs) {
        return prefs.getString(image, "");
    }

    public static void save_country(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, country, value);
    }

    public static String get_country(SharedPreferences prefs) {
        return prefs.getString(country, "");
    }

    public static void save_zipcode(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, zipcode, value);
    }

    public static String get_zipcode(SharedPreferences prefs) {
        return prefs.getString(zipcode, "");
    }


    public static void save_usertype(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, user_type, value);
    }

    public static String get_usertype(SharedPreferences prefs) {
        return prefs.getString(user_type, "");
    }

    public static void save_gender(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, gender, value);
    }

    public static String get_gender(SharedPreferences prefs) {
        return prefs.getString(gender, "");
    }


    public static void save_followers(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, followers, value);
    }

    public static String get_followers(SharedPreferences prefs) {
        return prefs.getString(followers, "");
    }

    public static void save_following(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, following, value);
    }

    public static String get_following(SharedPreferences prefs) {
        return prefs.getString(following, "");
    }

    public static void save_Bio(SharedPreferences prefs, String value) {
        SessionManager.savePreference(prefs, Bio, value);
    }

    public static String get_Bio(SharedPreferences prefs) {
        return prefs.getString(Bio, "");
    }

}