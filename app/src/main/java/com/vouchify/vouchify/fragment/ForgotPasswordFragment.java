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
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/27/16.
 */
public class ForgotPasswordFragment extends BaseFragment {

	@BindView(R.id.forgot_pw_btn_submit)
	TextView btnSubmit;

	@BindView(R.id.forgot_pw_edt_email)
	EditText edtEmail;

	public static ForgotPasswordFragment getInstance() {

		return new ForgotPasswordFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_forgot_password;
	}

	@Override
	protected void initView() {
		super.initView();

		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.for_got_password);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				backFragment(false);
				break;

			case R.id.forgot_pw_btn_submit :

				submit();
				break;
			default :
				super.onClick(view);
		}
	}

	/**
	 * Submit
	 */
	private void submit() {

		String strEmail = edtEmail.getText().toString().trim();
		if (strEmail.isEmpty()) {

			Utilities.showAlertDialog(mAct, "",
					getString(R.string.err_email_required),
					getString(R.string.ok), "", null, null, true);
			return;
		}

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject object = new JsonObject();
		object.addProperty("username", strEmail);
		new UserApi(false).getInterface().resetPassword(object)
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

						Utilities.showAlertDialog(mAct,
								getString(R.string.please_check_your_inbox),
								getString(R.string.reset_password_success),
								getString(R.string.ok), "",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {

										backFragment(false);
									}
								}, null, true);
					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
