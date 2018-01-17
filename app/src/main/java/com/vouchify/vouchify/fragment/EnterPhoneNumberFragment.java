package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.CustomEditText;
import com.vouchify.vouchify.view.CustomTextView;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/22/16.
 */
public class EnterPhoneNumberFragment extends BaseFragment {

	private static final String BUNDLE_USER = "bundle_user";
	private static final String BOOLEAN_VERIFY = "boolean_verify";

	@BindView(R.id.verify_btn_submit)
	CustomTextView verifyMobile;
	@BindView(R.id.edit_profile_edt_phone)
	CustomEditText edtPhone;
	public static EnterPhoneNumberFragment getInstance(UserEntity user) {

		EnterPhoneNumberFragment fragment = new EnterPhoneNumberFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_USER, user);
		fragment.setArguments(bundle);
		return fragment;
	}

	public static EnterPhoneNumberFragment getInstance(UserEntity user, boolean vboolean) {

		EnterPhoneNumberFragment fragment = new EnterPhoneNumberFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_USER, user);
		bundle.putBoolean(BOOLEAN_VERIFY, vboolean);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_mobile;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.mobile);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
		verifyMobile.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		super.initView();

		UserEntity user = (UserEntity) getArguments().getSerializable(
				BUNDLE_USER);
		if (user == null) {

			mAct.onBackPressed();
			return;
		}
		boolean vboolean = (boolean) getArguments().getBoolean(BOOLEAN_VERIFY);
		updateUI(user);
	}

	@Override
	public void onClick(View view) {

        Utilities.dismissKeyboard(mAct, view);
		switch (view.getId()) {

			case R.id.toolbar_btn_left:

				mAct.onBackPressed();
				break;

			case R.id.verify_btn_submit :

				submit();
				break;

			default :

				super.onClick(view);
		}
	}

	private void updateUI(UserEntity user) {

		edtPhone.setText(user.getMobile());
	}

	/**
	 * Submit
	 */
	private void submit() {

		UserEntity user = mAct.getUser();

		final String strMobile = edtPhone.getText().toString().trim();
		if (strMobile.isEmpty()) {

			user.setMobile(null);
		} else {

			user.setMobile(strMobile);
		}

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject object = new JsonObject();
		object.addProperty("email", user.getEmail());
		object.addProperty("first_name", user.getFirstName());
		object.addProperty("last_name", user.getLastName());
		object.addProperty("mobile", user.getMobile());
		if (user.getFbToken() != null && !user.getFbToken().equals("")) {

			object.addProperty("fb_user_token", user.getFbToken());
		}

		new UserApi(true).getInterface()
				.signUp(Utilities.addUserAuth(mAct, object))
				.enqueue(new BaseCallBack<UserEntity>() {
					@Override
					public void validResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						Utilities.dismissDialog(dialog);
						UserEntity user = response.body();
						if (user.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(user.getErrMessage());
							return;
						}

						user = user.getResult();
						mAct.getUser().setEmail(user.getEmail());
						mAct.getUser().setMobile(strMobile);
						mAct.getUser().setLastName(user.getFirstName());
						mAct.getUser().setFirstName(user.getLastName());
						mAct.getUser().setUserId(user.getUserId());
						Utilities.saveUser(mAct.getUser());
						LocalBroadcastManager
								.getInstance(mAct)
								.sendBroadcast(
										new Intent(
												Constants.INTENT_SHOULD_UPDATE_PROFILE_UI));
						addChildFragment(PhoneVerifyFragment.getInstance(user.getMobile(), false), true);
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
