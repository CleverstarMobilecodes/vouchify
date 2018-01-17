package com.vouchify.vouchify.fragment.inf;

import com.facebook.FacebookException;
import com.vouchify.vouchify.entity.UserEntity;

/**
 * Hai Nguyen - 7/29/16.
 */
public interface OnLoginListener {

	void onSocialLogin(UserEntity user);

	void onCanceled();

    void onSuccess();

	void onError(FacebookException fe);
}
