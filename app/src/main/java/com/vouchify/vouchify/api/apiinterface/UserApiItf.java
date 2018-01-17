package com.vouchify.vouchify.api.apiinterface;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VerifyEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Hai Nguyen - 11/3/15.
 */
public interface UserApiItf {

	@POST("login")
	Call<UserEntity> login(@Body JsonObject body);

	@POST("writeuser")
	Call<UserEntity> signUp(@Body JsonObject body);

	@POST("sendpasswordresetrequest")
	Call<BaseEntity> resetPassword(@Body JsonObject user);

	@POST("getuser")
	Call<UserEntity> getUser(@Body JsonObject body);

	@POST("checkforduplicateuser")
	Call<BaseEntity> checkUserAvailable(@Body JsonObject body);

	@POST("requestcontactinfoverification")
	Call<VerifyEntity> verifyEmail(@Body JsonObject body);

	@POST("sendphonecontacts")
	Call<BaseEntity> sendPhoneContacts(@Body JsonObject body);

	@POST("verifycontactinfo")
	Call<BaseEntity> verifyPhone(@Body JsonObject body);

	@POST("changepassword")
	Call<BaseEntity> changePassword(@Body JsonObject body);

	@POST("getuserconnections")
	Call<UserEntity> getUserConnections(@Body JsonObject body);

	@POST("logoff")
	Call<BaseEntity> logOut(@Body JsonObject body);

    @POST("relogin")
    Call<UserEntity> reLogin(@Body JsonObject body);
}
