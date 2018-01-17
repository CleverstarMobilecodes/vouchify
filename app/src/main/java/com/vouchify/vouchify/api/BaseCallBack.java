package com.vouchify.vouchify.api;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.LogUtil;
import com.vouchify.vouchify.utility.Utilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/28/16.
 */

@SuppressWarnings("unchecked")
public abstract class BaseCallBack<T> implements Callback<T> {

	@Override
	public void onResponse(final Call<T> callMain, final Response<T> responseMain) {

		try {

			BaseEntity base = (BaseEntity) responseMain.body();
			if (base.getErrCode() == 3001) {

				LogUtil.e("Invalid session", "=========== 3001 ==========");

				final BaseCallBack callBackMain = this;
				UserEntity user = Utilities.getSavedUser();
				JsonObject object = new JsonObject();
				object.addProperty("username", user.getEmail());
				object.addProperty("password", user.getPassword());
				object.addProperty("role", Constants.CONSUMER);

				if (user.getFbToken() != null && !user.getFbToken().equals("")) {

					object.addProperty("fb_user_token", user.getFbToken());
				}

				new UserApi(false).getInterface().login(object)
						.enqueue(new BaseCallBack<UserEntity>() {
							@Override
							public void validResponse(Call<UserEntity> call,
									Response<UserEntity> response) {

								UserEntity user = response.body();
								if (user.getStat()
										.equals(Constants.RESULT_FAIL)) {

									//Utilities.saveUser(null);
                                    callBackMain.validResponse(call, responseMain);
									// Should start broadcast or callback to
									// open login screen and finish current
									// screen.
									return;
								}

								UserEntity savedUser = Utilities.getSavedUser();
								savedUser.setSessionTkn(user.getResult()
										.getSessionTkn());
								Utilities.saveUser(savedUser);

								// Should update session token for callMain
								// here.
								// Else app will use old(expired) session token
								// => error again.

								callMain.clone().enqueue(new Callback<T>() {
                                    @Override
                                    public void onResponse(Call<T> reCall,
                                                           Response<T> reResponse) {

                                        callBackMain.validResponse(reCall,
                                                reResponse);
                                    }

                                    @Override
                                    public void onFailure(Call<T> call,
                                                          Throwable t) {

                                        callBackMain.validResponse(call, responseMain);
                                    }
                                });
							}
						});

				return;
			}
		} catch (Exception ignore) {

		}

		validResponse(callMain, responseMain);
	}

	@Override
	public void onFailure(Call<T> call, Throwable t) {

	}

	public abstract void validResponse(Call<T> call, Response<T> response);
}
