package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.utility.CustomPreferences;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/22/16.
 */
public class ChangePasswordFragment extends BaseFragment {

	@BindView(R.id.change_pw_btn_cancel)
	TextView btnCancel;

	@BindView(R.id.change_pw_btn_change)
	TextView btnSubmit;

	@BindView(R.id.change_pw_edt_new_pw)
	EditText edtNewPw;

	@BindView(R.id.change_pw_edt_new_pw_confirm)
	EditText edtNewPwConfirm;

	@BindView(R.id.change_pw_edt_pw)
	EditText edtCurPw;

	public static ChangePasswordFragment getInstance() {

		return new ChangePasswordFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_change_password;
	}

	@Override
	protected void initView() {
		super.initView();
		btnCancel.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.change_password);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.change_pw_btn_change :

				changePassword();
				Utilities.dismissKeyboard(mAct, view);
				break;

			case R.id.change_pw_btn_cancel :
			case R.id.toolbar_btn_left :

				Utilities.dismissKeyboard(mAct, view);
				mAct.onBackPressed();
				break;
			default :

				super.onClick(view);
		}
	}

	/**
	 * Change password
	 */
	private void changePassword() {

		final String strPass = edtNewPw.getText().toString().trim();
		String strPassCfm = edtNewPwConfirm.getText().toString();
		if (!strPass.equals(strPassCfm)) {

			mAct.showError(getString(R.string.password_do_not_match));
			return;
		}

		final Dialog dialog = LoadingDialog.show(mAct);
		String curPass = edtCurPw.getText().toString().trim();
		JsonObject jObject = new JsonObject();
		jObject.addProperty("existing_password", curPass);
		jObject.addProperty("new_password", strPass);
		jObject.addProperty("user_id", mAct.getUser().getUserId());
		jObject.addProperty("session_token", mAct.getUser().getSessionTkn());
		new UserApi(true).getInterface().changePassword(jObject)
				.enqueue(new BaseCallBack<BaseEntity>() {
					@Override
					public void validResponse(Call<BaseEntity> call,
							Response<BaseEntity> response) {

						Utilities.dismissDialog(dialog);
						if (response.body().getStat()
								.equals(Constants.RESULT_FAIL)) {

							mAct.showError(response.body().getErrMessage());
							return;
						}

						// Save password
						CustomPreferences.setPreferences(
								Constants.PREF_PASSWORD, strPass);
						Utilities.showAlertDialog(mAct,
								getString(R.string.app_name),
								getString(R.string.password_was_changed),
								getString(R.string.ok), "",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {

										mAct.onBackPressed();
									}
								}, null, false);
					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
