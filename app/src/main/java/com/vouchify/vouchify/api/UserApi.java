package com.vouchify.vouchify.api;

import com.vouchify.vouchify.api.apiinterface.UserApiItf;

/**
 * Hai Nguyen - 11/3/15.
 */
public class UserApi extends BaseApi {

	private UserApiItf mInterface;

	public UserApi(boolean hasSession) {
        super(hasSession);

        mInterface = mRetrofit.create(UserApiItf.class);
	}

	public UserApiItf getInterface() {
		return mInterface;
	}
}
