package com.vouchify.vouchify.fragment;

import android.widget.TextView;

import com.vouchify.vouchify.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Hai Nguyen - 8/9/16.
 */
public class AuthFragment extends BaseFragment {

	@BindView(R.id.auth_btn_sign_up)
	TextView btnSignUp;

	public static AuthFragment getInstance() {

		return new AuthFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_auth;
	}

	@Override
	protected void initView() {
		super.initView();

		btnSignUp.setSelected(true);
	}

	@Override
	public void setToolBar() {

	}

	@OnClick(R.id.auth_btn_sign_up)
	void onSignUp() {

		addFragment(SignUpFragment.getInstance(), true);
	}

	@OnClick(R.id.auth_btn_login)
	void onLogIn() {

		addFragment(LogInFragment.getInstance(), true);
	}
}
