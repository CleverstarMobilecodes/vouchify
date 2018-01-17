package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/22/16.
 */
public class EditProfileFragment extends BaseFragment {

	private static final String BUNDLE_USER = "bundle_user";

	@BindView(R.id.edit_profile_edt_phone)
	EditText edtPhone;

	@BindView(R.id.edit_profile_edt_email)
	EditText edtEmail;

	@BindView(R.id.edit_profile_edt_first_name)
	EditText edtFirstName;

	@BindView(R.id.edit_profile_edt_last_name)
	EditText edtLastName;

	@BindView(R.id.edit_profile_btn_change_pw)
	TextView btnChangePw;

	public static EditProfileFragment getInstance(UserEntity user) {

		EditProfileFragment fragment = new EditProfileFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_USER, user);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_edit_profile;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.edit_user);

		assert txtRight != null;
		txtRight.setVisibility(View.VISIBLE);
		txtRight.setText(R.string.done);
		txtRight.setOnClickListener(this);

		assert txtLeft != null;
		txtLeft.setVisibility(View.VISIBLE);
		txtLeft.setText(R.string.cancel);
		txtLeft.setOnClickListener(this);
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

		updateUI(user);
	}

	@Override
	public void onClick(View view) {

        Utilities.dismissKeyboard(mAct, view);
		switch (view.getId()) {

			case R.id.toolbar_txt_left :

				mAct.onBackPressed();
				break;

			case R.id.toolbar_txt_right :

				submit();
				break;

			case R.id.edit_profile_btn_change_pw :

				addChildFragment(ChangePasswordFragment.getInstance(), true);
				break;
			default :

				super.onClick(view);
		}
	}

	private void updateUI(UserEntity user) {

		edtEmail.setText(user.getEmail());
		edtPhone.setText(user.getMobile());
		edtLastName.setText(user.getLastName());
		edtFirstName.setText(user.getFirstName());
		if (user.isHasPassword()) {

			btnChangePw.setVisibility(View.VISIBLE);
			btnChangePw.setOnClickListener(this);
		}
	}

	/**
	 * Submit
	 */
	private void submit() {

		UserEntity user = mAct.getUser();
		final String strFirstName = edtFirstName.getText().toString().trim();
		if (strFirstName.isEmpty()) {

			mAct.showError(getString(R.string.err_first_name_required));
			return;
		}
		final String strLastName = edtLastName.getText().toString().trim();
		if (strLastName.isEmpty()) {

			mAct.showError(getString(R.string.err_last_name_required));
			return;
		}
		final String strEmail = edtEmail.getText().toString().trim();
		if (strEmail.isEmpty()) {

			mAct.showError(getString(R.string.err_email_required));
			return;
		}

		final String strMobile = edtPhone.getText().toString().trim();
		if (strMobile.isEmpty()) {

			user.setMobile(null);
		} else {

			user.setMobile(strMobile);
		}

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject object = new JsonObject();
		object.addProperty("email", strEmail);
		object.addProperty("first_name", strFirstName);
		object.addProperty("last_name", strLastName);
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
						mAct.getUser().setEmail(strEmail);
						mAct.getUser().setMobile(strMobile);
						mAct.getUser().setLastName(strLastName);
						mAct.getUser().setFirstName(strFirstName);
						mAct.getUser().setUserId(user.getUserId());
						Utilities.saveUser(mAct.getUser());
						LocalBroadcastManager
								.getInstance(mAct)
								.sendBroadcast(
										new Intent(
												Constants.INTENT_SHOULD_UPDATE_PROFILE_UI));
						mAct.onBackPressed();
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
