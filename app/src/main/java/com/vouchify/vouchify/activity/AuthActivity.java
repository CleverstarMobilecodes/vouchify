package com.vouchify.vouchify.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.fragment.AuthFragment;
import com.vouchify.vouchify.fragment.BaseFragment;
import com.vouchify.vouchify.fragment.ConnectFragment;
import com.vouchify.vouchify.fragment.FinishSignUpFragment;
import com.vouchify.vouchify.fragment.LogInFragment;
import com.vouchify.vouchify.fragment.SignUpFragment;

/**
 * Hai Nguyen - 8/9/16.
 */
public class AuthActivity extends BaseActivity {

	@Override
	protected int addView() {
		return R.layout.activity_auth;
	}

	@Override
	protected void initView(@Nullable Bundle savedInstanceState) {
		super.initView(savedInstanceState);

		if (savedInstanceState == null) {

			boolean isLogout = getIntent().getBooleanExtra(
					Constants.INTENT_LOGOUT, false);
//			if (isLogout) {
//
//				addFragment(LogInFragment.getInstance(), false);
//				return;
//			}

			addFragment(AuthFragment.getInstance(), false);
		}
	}

	@Override
	public void onBackPressed() {

		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.activity_container);
		if (fragment != null
				&& (fragment instanceof ConnectFragment || fragment instanceof FinishSignUpFragment)) {

			return;
		}

		if (fragment instanceof AuthFragment) {

			finish();
			return;
		}

		boolean isLogout = getIntent().getBooleanExtra(Constants.INTENT_LOGOUT,
				false);
		if (fragment instanceof LogInFragment && isLogout) {

			addFragment(SignUpFragment.getInstance(), true);
		}

		super.onBackPressed();
	}
}
