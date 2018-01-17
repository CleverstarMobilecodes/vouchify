package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.graphics.Paint;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.fragment.inf.OnLoginListener;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/9/16.
 */
@SuppressWarnings("ALL")
public class SignUpFragment extends BaseFragment implements OnLoginListener {

	@BindView(R.id.sign_up_btn_login)
	TextView btnLogin;

	@BindView(R.id.sign_up_btn_submit)
	TextView btnSubmit;

	@BindView(R.id.sign_up_edt_email)
	EditText edtEmail;

	@BindView(R.id.sign_up_edt_password)
	EditText edtPassword;

	@BindView(R.id.sign_up_edt_password_cfm)
	EditText edtPasswordCfm;

	@BindView(R.id.sign_up_ll_bottom)
	LinearLayout llBottom;

	@BindView(R.id.sign_up_btn_policy)
	TextView btnPolicy;

	@BindView(R.id.sign_up_btn_terms)
	TextView btnTerms;

	private Dialog mDialog;
	private FacebookFragment fbFragment;
	public static SignUpFragment getInstance() {

		return new SignUpFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_singup;
	}

	@Override
	protected void initView() {
		super.initView();

		fbFragment = (FacebookFragment) getChildFragmentManager()
				.findFragmentById(R.id.fm_facebook);
		fbFragment.setButtonText(getString(R.string.sign_up_with_fb));

		Utilities.saveUser(null);

		// Set underline
		btnLogin.setPaintFlags(btnLogin.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		btnTerms.setPaintFlags(btnTerms.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);
		btnPolicy.setPaintFlags(btnPolicy.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);

		btnLogin.setOnClickListener(this);
		fbFragment.setLoginListener(this);
		btnTerms.setOnClickListener(this);
		btnPolicy.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void setToolBar() {

		txtTittle.setText(R.string.sign_up);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.sign_up_btn_login :

				addFragment(LogInFragment.getInstance(), true);
				break;
			case R.id.sign_up_btn_submit :

				next();
				break;
			case R.id.sign_up_btn_policy :

				addFragment(BrowserFragment.getInstance(Constants.URL_POLICY),
						true);
				break;

			case R.id.sign_up_btn_terms :

				addFragment(BrowserFragment.getInstance(Constants.URL_TERMS),
						true);
				break;
		}
	}

	private void next() {

		if (AccessToken.getCurrentAccessToken() != null) {

			LoginManager.getInstance().logOut();
		}

		String strEmail = edtEmail.getText().toString().trim();
		if (strEmail.isEmpty()) {

			mAct.showError(getString(R.string.err_email_required), llBottom);
			return;
		}

		String strPass = edtPassword.getText().toString().trim();
		if (strPass.isEmpty()) {

			mAct.showError(getString(R.string.err_password_required), llBottom);
			return;
		}

		String strPassCfm = edtPasswordCfm.getText().toString();
		if (!strPass.equals(strPassCfm)) {

			mAct.showError(getString(R.string.err_password_do_not_match),
					llBottom);
			return;
		}

		UserEntity user = new UserEntity();
		user.setEmail(strEmail);
		user.setPassword(strPass);
		user.setLoggedInWithFb(false);
		if (AccessToken.getCurrentAccessToken() != null) {

			LoginManager.getInstance().logOut();
		}

		mDialog = LoadingDialog.show(mAct);
		checkUserAvailable(user);
	}

	@Override
	public void onSocialLogin(UserEntity user) {

		user.setLoggedInWithFb(true);
		checkUserAvailable(user);
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
		mAct.showError(fe.getMessage(), llBottom);
	}

	/**
	 * Check if user is available
	 */
	private void checkUserAvailable(final UserEntity user) {

		JsonObject object = new JsonObject();
		if (user.isLoggedInWithFb()) {

			object.addProperty("fb_user_token", user.getFbToken());
		} else {

			object.addProperty("email", user.getEmail());
		}

		new UserApi(false).getInterface().checkUserAvailable(object)
				.enqueue(new BaseCallBack<BaseEntity>() {
					@Override
					public void validResponse(Call<BaseEntity> call,
							Response<BaseEntity> response) {

						Utilities.dismissDialog(mDialog);
						if (response.body().getStat()
								.equals(Constants.RESULT_FAIL)) {

							Utilities.saveUser(null);
							mAct.showError(response.body().getErrMessage(),
									llBottom);
							return;
						}

						mAct.setUser(user);
						addFragment(Step1Fragment.getInstance(), true);
					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

						Utilities.dismissDialog(mDialog);
					}
				});
	}
}
