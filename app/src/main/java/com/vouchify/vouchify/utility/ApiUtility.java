package com.vouchify.vouchify.utility;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.activity.BaseActivity;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VerifyEntity;

/**
 * Hai Nguyen - 8/19/16.
 */
public class ApiUtility {

	/**
	 * Verify email
	 */
	public static void verifyInfo(UserEntity user, String info, String type,
			String businessId, BaseCallBack<VerifyEntity> callback) {

		JsonObject jObject = new JsonObject();
		jObject.addProperty("contact_info", info);
		jObject.addProperty("contact_info_type", type);
		jObject.addProperty("user_id", user.getUserId());
		jObject.addProperty("session_token", user.getSessionTkn());
		if (businessId != null) {

			jObject.addProperty("claimed_business_id", businessId);
		}

		new UserApi(true).getInterface().verifyEmail(jObject).enqueue(callback);
	}

	/**
	 * 
	 * Login
	 */
	public static void login(UserEntity user, BaseCallBack<UserEntity> callback) {

		JsonObject object = new JsonObject();
		object.addProperty("username", user.getEmail());
		object.addProperty("password", user.getPassword());
		object.addProperty("role", Constants.CONSUMER);
		new UserApi(false).getInterface().login(object).enqueue(callback);
	}

	/**
     *
     */
	public static void getUser(BaseActivity act,
			BaseCallBack<UserEntity> callback) {

		new UserApi(true).getInterface().getUser(Utilities.addUserAuth(act, null))
				.enqueue(callback);
	}

	/**
     *
     */
	public static void facebookLogin(UserEntity user,
			BaseCallBack<UserEntity> callback) {

		JsonObject object = new JsonObject();
		object.addProperty("role", Constants.CONSUMER);
		if (user.getFbToken() != null && !user.getFbToken().equals("")) {

			object.addProperty("fb_user_token", user.getFbToken());
		}
		new UserApi(false).getInterface().login(object).enqueue(callback);
	}
}
