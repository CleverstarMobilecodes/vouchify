package com.vouchify.vouchify.fragment;

import com.facebook.FacebookException;
import com.google.gson.JsonArray;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/9/16.
 */
public class ConnectFragment extends BaseFragment implements OnLoginListener {

	private static final String BUNDLE_IS_SHARED_FB = "bundle_is_shared_fb";

	@BindView(R.id.connect_btn_phone)
	LinearLayout btnConnectPhone;

	@BindView(R.id.connect_imv_phone)
	ImageView imvPhone;

	@BindView(R.id.connect_btn_fb)
	LinearLayout btnConnectFb;

	@BindView(R.id.connect_imv_fb)
	ImageView imvFb;

	@BindView(R.id.connect_btn_later)
	TextView btnLater;

	private FacebookFragment fmFacebook;
	private UserEntity mUser;
	private boolean hasSharePhConnection;
	private boolean hasShareFBConnection;

	public static ConnectFragment getInstance(boolean isSharedFb) {

		ConnectFragment fragment = new ConnectFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(BUNDLE_IS_SHARED_FB, isSharedFb);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_connect;
	}

	@Override
	protected void initView() {
		super.initView();

		mUser = mAct.getUser();
		if (mUser == null) {

			return;
		}

		boolean isSharedFb = getArguments().getBoolean(BUNDLE_IS_SHARED_FB,
				false);
		fmFacebook = (FacebookFragment) getChildFragmentManager()
				.findFragmentById(R.id.step3_fm_fb);

		if (isSharedFb) {

			hasShareFBConnection = true;
			btnConnectFb.setEnabled(false);
			imvFb.setVisibility(View.VISIBLE);
		}

		fmFacebook.setLoginListener(this);
		btnLater.setOnClickListener(this);
		btnConnectFb.setOnClickListener(this);
		btnConnectPhone.setOnClickListener(this);
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

			case R.id.connect_btn_fb :

				fmFacebook.connect();
				break;

			case R.id.connect_btn_phone :

				String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
				if (!Utilities.hasPermissions(mAct, permissions)) {

					requestPermissions(permissions, 0);
					return;
				}

				getContacts();
				break;
			case R.id.connect_btn_later :

				goToNextScreen();
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			getContacts();
		}
	}

	@Override
	public void onSocialLogin(UserEntity user) {

		mUser.setFbToken(user.getFbToken());
		final Dialog dialog = LoadingDialog.show(mAct);

		JsonObject object = new JsonObject();
		object.addProperty("email", mUser.getEmail());
		object.addProperty("mobile", mUser.getMobile());
		object.addProperty("last_name", mUser.getLastName());
		object.addProperty("first_name", mUser.getFirstName());
		object.addProperty("fb_user_token", mUser.getFbToken());
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

						mUser.setHasSharedFb(user.getResult().isHasSharedFb());
						mAct.setUser(mUser);
						facebookUpdateView(user.getResult().isHasSharedFb());
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	@Override
	public void onCanceled() {

	}

	@Override
	public void onSuccess() {

	}

	@Override
	public void onError(FacebookException fe) {

	}

	private void getContacts() {

		final Dialog dialog = LoadingDialog.show(mAct);
		ContentResolver contactResolver = mAct.getContentResolver();

		@SuppressLint("Recycle")
		Cursor cursor = contactResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		if (cursor != null && cursor.moveToFirst()) {

			JsonArray jArrContact = new JsonArray();
			while (cursor.moveToNext()) {

				String phone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phone = Utilities.convertMobileAndEncrypt(phone);
				if (phone != null && !phone.equals("")) {

					JsonObject jObjectPhone = new JsonObject();
					jObjectPhone.addProperty(Constants.KEY_ENCRYPTED_CONTACTS,
							phone);
					jArrContact.add(jObjectPhone);
				}
			}

			if (jArrContact.size() > 0) {

				JsonObject jObject = new JsonObject();
				jObject.add(Constants.KEY_PHONE_CONTACTS, jArrContact);
				new UserApi(true)
						.getInterface()
						.sendPhoneContacts(Utilities.addUserAuth(mAct, jObject))
						.enqueue(new BaseCallBack<BaseEntity>() {
							@Override
							public void validResponse(Call<BaseEntity> call,
									Response<BaseEntity> response) {

								Utilities.dismissDialog(dialog);
								BaseEntity base = response.body();
								if (base.getStat()
										.equals(Constants.RESULT_FAIL)) {
									mAct.showError(base.getErrMessage());
									return;
								}

								mUser.setContactShouldSend(false);
								mUser.setHasSharedPh(true);
								Utilities.saveUser(mUser);
								mAct.setUser(mUser);
								contactUpdateView();
							}

							@Override
							public void onFailure(Call<BaseEntity> call,
									Throwable t) {

								Utilities.dismissDialog(dialog);
							}
						});
			} else {

				Utilities.dismissDialog(dialog);
				contactUpdateView();
			}

		} else {

			Utilities.dismissDialog(dialog);
		}
	}

	private void contactUpdateView() {

		hasSharePhConnection = true;
		btnConnectPhone.setEnabled(false);
		imvPhone.setVisibility(View.VISIBLE);

		if (hasSharePhConnection && hasShareFBConnection) {

			goToNextScreen();
		}
	}

	private void facebookUpdateView(boolean shared) {

		if (shared) {
			hasShareFBConnection = true;
			btnConnectFb.setEnabled(false);
			imvFb.setVisibility(View.VISIBLE);

			if (hasSharePhConnection && hasShareFBConnection) {

				goToNextScreen();
			}
		}
	}

	/**
	 * Go to next screen
	 */
	private void goToNextScreen() {

		addFragment(FinishSignUpFragment.getInstance(), true);
	}
}
