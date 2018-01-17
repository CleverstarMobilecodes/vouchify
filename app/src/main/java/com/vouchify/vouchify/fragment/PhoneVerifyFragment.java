package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VerifyEntity;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/19/16.
 */
public class PhoneVerifyFragment extends BaseFragment implements TextWatcher {

	private static final String BUNDLE_NUMBER = "bundle_number";
	private static final String BUNDLE_FROM_REGISTER = "bundle_from_register";

	@BindView(R.id.verify_txt_intro)
	TextView txtIntro;

	@BindView(R.id.verify_btn_submit)
	TextView btnSubmit;

	@BindView(R.id.verify_btn_refresh)
	TextView btnRefresh;

	@BindViews({R.id.verify_edt_number_1, R.id.verify_edt_number_2,
			R.id.verify_edt_number_3, R.id.verify_edt_number_4})
	List<EditText> mEdtNumbers;

	private int mVerifyId;
	private String mNumber;
	private boolean isFromRegister;
	public static PhoneVerifyFragment getInstance(String number,
			boolean fromRegister) {

		PhoneVerifyFragment fragment = new PhoneVerifyFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_NUMBER, number);
		bundle.putBoolean(BUNDLE_FROM_REGISTER, fromRegister);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_phone_verify;
	}

	@Override
	protected void initView() {
		super.initView();

		mNumber = getArguments().getString(BUNDLE_NUMBER, "");
		isFromRegister = getArguments().getBoolean(BUNDLE_FROM_REGISTER, false);

		setUpClickText();
		requestVerifyCode();
		btnSubmit.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
		for (EditText edt : mEdtNumbers) {

			edt.addTextChangedListener(this);
		}
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.verify_phone);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.verify_btn_submit :

				doVerifyCode();
				break;
			case R.id.verify_btn_refresh :

				requestVerifyCode();
				break;
			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;
			default :

				super.onClick(view);
		}
	}

	private void setUpClickText() {

		String strIntro = String.format(getString(R.string.phone_verify_title),
				mNumber);
		SpannableString ss = new SpannableString(strIntro);
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View textView) {

				requestVerifyCode();
			}

			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);

				ds.setUnderlineText(false);
			}
		};

		int startPos = strIntro.lastIndexOf("Didn't receive a code?");
		ss.setSpan(clickableSpan, startPos, strIntro.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		txtIntro.setText(ss);
		txtIntro.setMovementMethod(LinkMovementMethod.getInstance());
		txtIntro.setHighlightColor(Color.TRANSPARENT);
	}

	/**
	 * Request verify code
	 */
	private void requestVerifyCode() {

		ApiUtility.verifyInfo(mAct.getUser(), mNumber, "MOBILE", null,
				new BaseCallBack<VerifyEntity>() {
					@Override
					public void validResponse(Call<VerifyEntity> call,
							Response<VerifyEntity> response) {

						if (response.body().getStat()
								.equals(Constants.RESULT_FAIL)) {

							mAct.showError(response.body().getErrMessage());
						}

						mVerifyId = response.body().getResult().getVerifyId();
					}

					@Override
					public void onFailure(Call<VerifyEntity> call, Throwable t) {

					}
				});
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1,
			int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		EditText nextEdt = null;
		View view = mAct.getCurrentFocus();
		if (view == null) {

			return;
		}

		switch (view.getId()) {

			case R.id.verify_edt_number_2 :

				nextEdt = mEdtNumbers.get(2);
				break;

			case R.id.verify_edt_number_3 :

				nextEdt = mEdtNumbers.get(3);
				break;

			case R.id.verify_edt_number_1 :

				nextEdt = mEdtNumbers.get(1);
		}

		if (nextEdt != null && charSequence.length() == 1) {

			nextEdt.requestFocus();
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

	/**
	 * Verify code
	 */
	private void doVerifyCode() {

		final Dialog dialog = LoadingDialog.show(mAct);
		String code = String.format("%s%s%s%s", mEdtNumbers.get(0).getText()
				.toString(), mEdtNumbers.get(1).getText().toString(),
				mEdtNumbers.get(2).getText().toString(), mEdtNumbers.get(3)
						.getText().toString());
		JsonObject jObject = new JsonObject();
		jObject.addProperty("contact_verification_code", code);
		// jObject.addProperty("user_id", mAct.getUser().getUserId());
		jObject.addProperty("contact_info_verification_id", mVerifyId);
		// jObject.addProperty("session_token", mAct.getUser().getSessionTkn());
		new UserApi(false).getInterface().verifyPhone(jObject)
				.enqueue(new BaseCallBack<BaseEntity>() {
					@Override
					public void validResponse(Call<BaseEntity> call,
							Response<BaseEntity> response) {

						Utilities.dismissDialog(dialog);
						if (response.body().getStat()
								.equals(Constants.RESULT_FAIL)) {

							mAct.showError(response.body().getErrMessage());
							for (EditText editText : mEdtNumbers) {

								editText.setText("");
							}

							return;
						}

						// backFragment(false);
						if (isFromRegister) {

							getUserConnection();
						} else {

							LocalBroadcastManager
									.getInstance(mAct)
									.sendBroadcast(
											new Intent(
													Constants.INTENT_SHOULD_UPDATE_PROFILE_UI));
							mAct.onBackPressed();
						}
					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	/**
	 * Get user connection
	 */
	private void getUserConnection() {

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject object = new JsonObject();
		object.addProperty("session_token", mAct.getUser().getSessionTkn());
		new UserApi(true).getInterface().getUserConnections(object)
				.enqueue(new BaseCallBack<UserEntity>() {
					@Override
					public void validResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						Utilities.dismissDialog(dialog);
						if (response.body().getStat()
								.equals(Constants.RESULT_FAIL)) {

							mAct.showError(response.body().getErrMessage());
							return;
						}

						addFragment(
								ConnectFragment.getInstance(response.body()
										.getResult().isHasSharedFb()), true);
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
