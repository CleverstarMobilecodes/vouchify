package com.vouchify.vouchify.api;

import com.vouchify.vouchify.api.apiinterface.DataApiItf;

/**
 * Hai Nguyen - 11/3/15.
 */
public class DataApi extends BaseApi {

	private DataApiItf mInterface;

	public DataApi(boolean hasSession) {
        super(hasSession);

        mInterface = mRetrofit.create(DataApiItf.class);
	}

	public DataApiItf getInterface() {
		return mInterface;
	}
}
