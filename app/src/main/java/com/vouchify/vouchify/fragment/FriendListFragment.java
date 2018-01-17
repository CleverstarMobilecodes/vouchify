package com.vouchify.vouchify.fragment;

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
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.vouchify.vouchify.activity.MainActivity;
import com.vouchify.vouchify.adapter.FriendListAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/7/16.
 */
@SuppressWarnings("unchecked")
public class FriendListFragment extends BaseFragment
		implements
			RadioGroup.OnCheckedChangeListener,
			SearchView.OnQueryTextListener {

	private static final String BUNDLE_IS_FRIEND_FB = "bundle_is_friend_fb";
	private static final String BUNDLE_ALL_CONNECTED = "bundle_all_connected";

	@BindView(R.id.friend_search_view)
	SearchView searchView;

	@BindView(R.id.friend_rg_friend_type)
	RadioGroup rgFriendType;

	@BindView(R.id.friend_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.friend_rb_contacts)
	RadioButton rbContacts;

	private boolean isAllConnected;
	private FriendListAdapter mAdapter;
	private List<UserEntity> mFbFriends, mContacts;
	private boolean hasShareFb;
	private boolean hasSharePh;
	private CallbackManager mCallbackManager;
	private final List<String> mPermissions = Arrays.asList("public_profile",
			"user_friends", "email");

	public static FriendListFragment getInstance(boolean isFriendFb,
			boolean allConnected) {

		FriendListFragment fragment = new FriendListFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(BUNDLE_IS_FRIEND_FB, isFriendFb);
		bundle.putBoolean(BUNDLE_ALL_CONNECTED, allConnected);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_friend_list;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.friends);
		if (isAllConnected) {

			super.setToolBar();
			return;
		}

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		super.initView();

		mCallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(mCallbackManager,
				facebookCallback);
		mFbFriends = new ArrayList<>();
		mContacts = new ArrayList<>();

		boolean isFriendFb = getArguments().getBoolean(BUNDLE_IS_FRIEND_FB,
				false);
		isAllConnected = getArguments().getBoolean(BUNDLE_ALL_CONNECTED, false);
		if (!isFriendFb) {

			rbContacts.setChecked(true);
		}

		Utilities.setLayoutManager(mAct, recyclerView, true, true, true);
		rgFriendType.setOnCheckedChangeListener(this);
		searchView.setOnQueryTextListener(this);

		getFriend();
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int i) {

		switch (i) {

			case R.id.friend_rb_fb :

				Utilities.dismissKeyboard(mAct, radioGroup);
				if (hasShareFb) {

					searchView.setQuery("", false);
					searchView.setIconified(true);
					displayFriends();
				} else {

					LoginManager.getInstance().logInWithReadPermissions(this,
							mPermissions);
				}

				break;

			case R.id.friend_rb_contacts :

				Utilities.dismissKeyboard(mAct, radioGroup);
				if (hasSharePh) {

					searchView.setQuery("", false);
					searchView.setIconified(true);
					displayFriends();
				} else {

					String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
					if (!Utilities.hasPermissions(mAct, permissions)) {

						requestPermissions(permissions, 0);
						return;
					}

					getContacts();
				}

				break;

			default :

				searchView.setQuery("", false);
				searchView.setIconified(true);
				Utilities.dismissKeyboard(mAct, radioGroup);
				displayFriends();
		}

	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {

		if (mAdapter != null && mAdapter.getFilter() != null) {

			mAdapter.getFilter().filter(newText);
		}

		return false;
	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);
		UserEntity user = (UserEntity) object;
		addChildFragment(FriendDetailFragment.getInstance(user), true);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				if (isAllConnected) {

					super.onClick(view);
					break;
				}

				mAct.onBackPressed();
				break;
			default :

				super.onClick(view);
		}
	}

	/**
	 * Display friend list
	 */
	private void displayFriends() {

		switch (rgFriendType.getCheckedRadioButtonId()) {

			case R.id.friend_rb_contacts :

				mAdapter = new FriendListAdapter(mAct, this, mContacts);
				break;

			default :

				mAdapter = new FriendListAdapter(mAct, this, mFbFriends);
		}

		recyclerView.setAdapter(mAdapter);
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
						if (base.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(base.getErrMessage());
							return;
						}

						mFbFriends = base.getResult().getFriendFbConnections();
						mContacts = base.getResult().getFriendPhConnections();

						hasSharePh = base.getResult().isHasSharedPh();
						hasShareFb = base.getResult().isHasSharedFb();

						displayFriends();
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	@Override
	public boolean onBackPressed() {

		FragmentManager fm = getChildFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {

			return super.onBackPressed();
		}

		if (isAllConnected) {

			if (((MainActivity) mAct).closeDrawer()) {

				return true;
			}

			mAct.finish();
			return true;
		}

		return super.onBackPressed();
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
								searchView.setQuery("", false);
								searchView.setIconified(true);
								getFriend();
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
						searchView.setQuery("", false);
						searchView.setIconified(true);
						getFriend();
					}

					@Override
					public void onFailure(Call<UserEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}
}
