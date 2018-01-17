package com.vouchify.vouchify.fragment;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.activity.MainActivity;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.fragment.inf.OnLoginListener;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/9/16.
 */
public class LogInFragment extends BaseFragment implements OnLoginListener {

	@BindView(R.id.login_btn_sign_up)
	TextView btnSignUp;

	@BindView(R.id.login_btn_submit)
	TextView btnLogin;

	@BindView(R.id.login_edt_email)
	EditText edtEmail;

	@BindView(R.id.login_edt_password)
	EditText edtPassword;

	@BindView(R.id.login_btn_forgot)
	LinearLayout btnForgot;

	private Dialog mDialog;
	FacebookFragment fbFragment;

	public static LogInFragment getInstance() {

		return new LogInFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_login;
	}

	@Override
	protected void initView() {
		super.initView();

		fbFragment = (FacebookFragment) getChildFragmentManager()
				.findFragmentById(R.id.fm_facebook);
		fbFragment.setButtonText(getString(R.string.log_in_with_fb));

		btnSignUp.setPaintFlags(btnLogin.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		btnLogin.setOnClickListener(this);
		fbFragment.setLoginListener(this);
		btnSignUp.setOnClickListener(this);
		btnForgot.setOnClickListener(this);
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.log_in);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.login_btn_sign_up :

				addFragment(SignUpFragment.getInstance(), true);
				break;
			case R.id.login_btn_submit :

				login();
				break;
			case R.id.login_btn_forgot :

				addFragment(ForgotPasswordFragment.getInstance(), true);
				break;
		}
	}

	/**
	 * Login
	 */
	private void login() {

		String strEmail = edtEmail.getText().toString().trim();
		if (strEmail.isEmpty()) {

			mAct.showError(getString(R.string.err_email_required), btnForgot);
			return;
		}

		String strPassword = edtPassword.getText().toString().trim();
		if (strPassword.isEmpty()) {

			mAct.showError(getString(R.string.err_password_required), btnForgot);
			return;
		}

		mDialog = LoadingDialog.show(mAct);
		UserEntity user = new UserEntity(strEmail, strPassword);
		user.setRole(Constants.CONSUMER);
		ApiUtility.login(user, new MyCallback(user));
	}

	@Override
	public void onSocialLogin(UserEntity user) {

		ApiUtility.facebookLogin(user, new MyCallback(user));
	}

	@Override
	public void onCanceled() {

		Utilities.dismissDialog(mDialog);
	}

	@Override
	public void onSuccess() {

		mDialog = LoadingDialog.show(mAct);
	}

	@Override
	public void onError(FacebookException fe) {

		Utilities.dismissDialog(mDialog);
		mAct.showError(getString(R.string.err_facebook_login), btnForgot);
	}

	private class MyCallback extends BaseCallBack<UserEntity> {

		private UserEntity mUser;

		MyCallback(UserEntity user) {

			this.mUser = user;
		}

		@Override
		public void validResponse(Call<UserEntity> call,
				Response<UserEntity> response) {

			Utilities.dismissDialog(mDialog);
			UserEntity user = response.body();
			if (user.getStat().equals(Constants.RESULT_FAIL)) {

				if (AccessToken.getCurrentAccessToken() != null) {

					LoginManager.getInstance().logOut();
				}

				Utilities.saveUser(null);
				mAct.showError(user.getErrMessage(), btnForgot);
				return;
			}

			user = user.getResult();
			mUser.setUserId(user.getUserId());
			mUser.setLastName(user.getLastName());
			mUser.setFirstName(user.getFirstName());
			mUser.setSessionTkn(user.getSessionTkn());
            if (user.getFbToken() != null && !user.getFbToken().equals("")) {

                mUser.setFbToken(user.getFbToken());
            }

			mUser.setLoggedInWithFb(!(mUser.getFbToken() == null || mUser
					.getFbToken().isEmpty()));
			Utilities.saveUser(mUser);

			startActivity(new Intent(mAct, MainActivity.class));
			mAct.finish();
		}

		@Override
		public void onFailure(Call<UserEntity> call, Throwable t) {

			Utilities.dismissDialog(mDialog);
		}
	}
}
