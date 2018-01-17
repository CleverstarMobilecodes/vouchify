package com.vouchify.vouchify.constant;

/**
 * Hai Nguyen - 8/27/15.
 */
public class Constants {

	public static final String BASE_URL = "http://dev1-dxv2mwq6bp.elasticbeanstalk.com/api/";
	public static final String URL_ABOUT = "file:///android_asset/html/about.html";
	public static final String URL_POLICY = "file:///android_asset/html/policy.html";
	public static final String URL_TERMS = "file:///android_asset/html/terms.html";

	// Intent
	public static final String INTENT_LOGOUT = "intent_logout";
	public static final String INTENT_VOUCH_UPDATE = "intent_vouch_update";
	public static final String INTENT_SHOULD_UPDATE_PROFILE_UI = "intent_should_update_profile_ui";

	// public static final String KEY_EMAIL = "email";
	//
	// public static final String SESSION_TOKEN = "session_token";
	// public static final String ROLE = "role";

	// Result
	public static final String RESULT_FAIL = "fail";

	public static final String CONSUMER = "CONSUMER";
	// public static final String KEY_MOBILE = "mobile";
	// public static final String KEY_GENDER = "gender";
	// public static final String KEY_ADDRESS = "address";
	// public static final String KEY_USERNAME = "username";
	// public static final String KEY_PASSWORD = "password";
	// public static final String KEY_LAST_NAME = "last_name";
	// public static final String KEY_FIRST_NAME = "first_name";
	// public static final String KEY_IS_CONSUMER = "is_consumer";
	// public static final String KEY_IS_BUSINESS = "is_business";
	// public static final String FACEBOOK_TKN = "fb_user_token";
	public static final String KEY_PHONE_CONTACTS = "phone_contacts";
	public static final String KEY_ENCRYPTED_CONTACTS = "encrypted_contact";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_SESSION_TOKEN = "session_token";

	// PREFERENCES
	public static final String PREF_EMAIL = "pref_email";
	// public static final String PREF_GENDER = "pref_gender";
	public static final String PREF_USER_ID = "pref_user_id";
	public static final String PREF_PASSWORD = "pref_password";
	public static final String PREF_LAST_NAME = "pref_last_name";
	public static final String PREF_FIRST_NAME = "pref_first_name";
	// public static final String PREF_LOCATION_ID = "pref_location_id";
	// public static final String PREF_PHONE_NUMBER = "pref_phone_number";
	public static final String PREF_SESSION_TOKEN = "pref_session_token";
	public static final String PREF_FACEBOOK_TOKEN = "pref_facebook_token";
	public static final String PREF_IS_LOG_IN_WITH_FB = "pref_is_login_with_facebook";
	// public static final String PREF_CONTACTS_SHOULD_SEND =
	// "pref_contacts_should_send";
	public static final String PREF_VOUCH_UPDATE = "pref_vouch_update";
    public static final String PREF_LONGITUDE = "pref_longitude";
    public static final String PREF_LATITUDE = "pref_latitude";

	// Constants
	public static final int VOUCH_NO_UPDATE = 0;
	public static final int VOUCH_UPDATED = 1;
	public static final int VOUCH_DELETED = 2;

    // public static final String PACKAGE_GOOGLE_MAPS = "com.google.android.apps.maps";
}
