package com.vouchify.vouchify.fragment;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/9/16.
 */
public class Step2Fragment extends BaseFragment {

	@BindView(R.id.step2_btn_submit)
	TextView btnSubmit;

	@BindView(R.id.step2_edt_phone_number)
	EditText edtPhone;

	@BindView(R.id.step2_btn_skip)
	TextView btnSkip;

	public static Step2Fragment getInstance() {

		return new Step2Fragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_step2;
	}

	@Override
	protected void initView() {
		super.initView();

		if (mAct.getUser() == null) {

			mAct.finish();
		}

		btnSkip.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.register);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.step2_btn_submit :

				next(true);
				break;
			case R.id.step2_btn_skip :

				next(false);
				break;
		}
	}

	/**
	 * Submit
	 * 
	 */
	private void next(final boolean verifyNumber) {

		String strPhone = edtPhone.getText().toString().trim();
		if (verifyNumber) {
			if (strPhone.isEmpty()) {

				mAct.showError(getString(R.string.err_mobile_phone_required));
				return;
			}

			mAct.getUser().setMobile(strPhone);
		} else {

			if (strPhone.isEmpty()) {

				mAct.getUser().setMobile(null);
			} else {

				mAct.getUser().setMobile(strPhone);
			}
		}

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject object = new JsonObject();
		object.addProperty("email", mAct.getUser().getEmail());
		object.addProperty("password", mAct.getUser().getPassword());
		object.addProperty("first_name", mAct.getUser().getFirstName());
		object.addProperty("last_name", mAct.getUser().getLastName());
		object.addProperty("mobile", mAct.getUser().getMobile());
		object.addProperty("fb_user_token", mAct.getUser().getFbToken());

		new UserApi(false).getInterface().signUp(object)
				.enqueue(new BaseCallBack<UserEntity>() {
					@Override
					public void validResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						Utilities.dismissDialog(dialog);
						UserEntity resUser = response.body();
						if (resUser.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(resUser.getErrMessage());
							return;
						}

						resUser = resUser.getResult();
						if (resUser.getSessionTkn() != null
								&& !resUser.getSessionTkn().isEmpty()) {

							mAct.getUser().setSessionTkn(
									resUser.getSessionTkn());
						}

						mAct.getUser().setUserId(resUser.getUserId());
						Utilities.saveUser(mAct.getUser());
						if (verifyNumber) {

							String number = Utilities
									.convertMobileToInternationalFormatAndValidate(mAct
											.getUser().getMobile());
							addFragment(PhoneVerifyFragment.getInstance(number,
									true), true);
							return;
						}

						addFragment(ConnectFragment.getInstance(resUser
								.isHasSharedFb()), true);
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
