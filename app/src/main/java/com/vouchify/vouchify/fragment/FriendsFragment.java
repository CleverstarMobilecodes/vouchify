package com.vouchify.vouchify.fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/9/16.
 */
public class FriendsFragment extends BaseFragment {

	@BindView(R.id.friends_btn_facebook)
	LinearLayout btnFbConnect;

	@BindView(R.id.friends_btn_phone)
	LinearLayout btnPhoneConnect;

	private CallbackManager mCallbackManager;
	private final List<String> mPermissions = Arrays.asList("public_profile",
			"user_friends", "email");

	private boolean hasShareFb;
	private boolean hasSharePh;
	private boolean isLoaded;

	public static FriendsFragment getInstance() {

		return new FriendsFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_friends;
	}

	@Override
	public void setToolBar() {
		super.setToolBar();
		assert txtTittle != null;
		txtTittle.setText(R.string.friends);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initView() {
		super.initView();

		if (mAct.getUser() == null) {

			return;
		}

		mCallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(mCallbackManager,
				facebookCallback);

		btnFbConnect.setOnClickListener(this);
		btnPhoneConnect.setOnClickListener(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && !isLoaded) {

			isLoaded = true;
			getFriend();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
			case R.id.friends_btn_facebook :

				if (hasShareFb) {

					showFriend(true);
				} else {

					LoginManager.getInstance().logInWithReadPermissions(this,
							mPermissions);
				}
				break;
			case R.id.friends_btn_phone :

				if (hasSharePh) {

					showFriend(false);
				} else {

					String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
					if (!Utilities.hasPermissions(mAct, permissions)) {

						requestPermissions(permissions, 0);
						return;
					}

					getContacts();
				}
				break;
		}
	}

	private void connectFacebook(final UserEntity user) {

		final Dialog dialog = LoadingDialog.show(mAct);

		JsonObject object = new JsonObject();
		object.addProperty("email", mAct.getUser().getEmail());
        if (mAct.getUser().getMobile() != null && !mAct.getUser().getMobile().isEmpty()) {

            object.addProperty("mobile", mAct.getUser().getMobile());
        }
		object.addProperty("last_name", mAct.getUser().getLastName());
		object.addProperty("first_name", mAct.getUser().getFirstName());
		object.addProperty("fb_user_token", user.getFbToken());

		new UserApi(true).getInterface()
				.signUp(Utilities.addUserAuth(mAct, object))
				.enqueue(new BaseCallBack<UserEntity>() {
					@Override
					public void validResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						Utilities.dismissDialog(dialog);
						UserEntity reUser = response.body();
						if (reUser.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(reUser.getErrMessage());
							return;
						}

						mAct.getUser().setFbToken(user.getFbToken());
                        mAct.getUser().setHasSharedFb(true);
						hasShareFb = true;
						mAct.setUser(mAct.getUser());
						showFriend(true);
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
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

								UserEntity user = mAct.getUser();
								user.setContactShouldSend(false);
								user.setHasSharedPh(true);
								hasSharePh = true;
								mAct.setUser(user);
								showFriend(false);
							}

							@Override
							public void onFailure(Call<BaseEntity> call,
									Throwable t) {

								Utilities.dismissDialog(dialog);
							}
						});
			} else {

				Utilities.dismissDialog(dialog);
			}

		} else {

			Utilities.dismissDialog(dialog);
		}

	}

	private void showFriend(boolean isFriendFb) {

		addChildFragment(
				FriendListFragment.getInstance(isFriendFb, hasSharePh
						&& hasShareFb), true);
	}

	private void getFriend() {

		final Dialog dialog = LoadingDialog.show(mAct);
		new UserApi(true)
				.getInterface()
				.getUserConnections(
						Utilities.addUserAuth(mAct, new JsonObject()))
				.enqueue(new BaseCallBack<UserEntity>() {
					@Override
					public void validResponse(Call<UserEntity> call,
							Response<UserEntity> response) {

						Utilities.dismissDialog(dialog);
						UserEntity base = response.body();
						if (base == null) {

							return;
						}

						if (base.getStat() == null) {

							return;
						}

						if (base.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(base.getErrMessage());
							return;
						}

						hasSharePh = base.getResult().isHasSharedPh();
						hasShareFb = base.getResult().isHasSharedFb();

						if (hasSharePh && hasShareFb) {

							showFriend(base.getResult().getTotalFbConnections() > 0);
						}

					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	// Facebook
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private FacebookCallback facebookCallback = new FacebookCallback<LoginResult>() {

		@Override
		public void onSuccess(LoginResult loginResult) {

			makeRequest(loginResult);
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onError(FacebookException e) {

			mAct.showError(getString(R.string.err_facebook_login));
		}
	};

	private void makeRequest(LoginResult loginResult) {

		Bundle parameters = new Bundle();
		parameters.putString("fields", "id,first_name,last_name,email");
		GraphRequest request = GraphRequest.newMeRequest(
				loginResult.getAccessToken(),
				new GraphRequest.GraphJSONObjectCallback() {
					@Override
					public void onCompleted(JSONObject jsonObject,
							GraphResponse graphResponse) {

						AccessToken token = AccessToken.getCurrentAccessToken();
						UserEntity user = new UserEntity();

						user.setFbToken(token.getToken());
						user.setRole(Constants.CONSUMER);
						user.setGender(null);
						user.setEmail(jsonObject.optString("email"));
						user.setLastName(jsonObject.optString("last_name"));
						user.setFirstName(jsonObject.optString("first_name"));
						connectFacebook(user);
					}
				});

		request.setParameters(parameters);
		request.executeAsync();
	}
}
