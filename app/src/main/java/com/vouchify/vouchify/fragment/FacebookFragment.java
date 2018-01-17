package com.vouchify.vouchify.fragment;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.fragment.inf.BackPressImpl;
import com.vouchify.vouchify.fragment.inf.OnBackPressListener;
import com.vouchify.vouchify.fragment.inf.OnLoginListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * HaiNguyen on 4/18/15.
 */
@SuppressWarnings("unchecked")
public class FacebookFragment extends Fragment
		implements
			View.OnClickListener,
			OnBackPressListener {

	private OnLoginListener mListener;
	private CallbackManager mCallbackManager;
	private final List<String> mPermissions = Arrays.asList("public_profile",
			"user_friends", "email");

	@BindView(R.id.facebook_btn_login)
	RelativeLayout btnLogin;

	@BindView(R.id.sign_up_txt)
	TextView textView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_facebook, container,
				false);
		ButterKnife.bind(this, view);

		// Facebook
		btnLogin.setOnClickListener(this);
		mCallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(mCallbackManager,
				facebookCallback);
		return view;
	}

	/**
	 * Set button text
	 */
	public void setButtonText(String text) {

		textView.setText(text);
	}

	@Override
	public void onClick(View view) {
		// super.onClick(view);
		switch (view.getId()) {

			case R.id.fm_facebook :

				LoginManager.getInstance().logInWithReadPermissions(this,
						mPermissions);
				break;
		}
	}

	public void setLoginListener(OnLoginListener listener) {
		this.mListener = listener;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private FacebookCallback facebookCallback = new FacebookCallback<LoginResult>() {

		@Override
		public void onSuccess(LoginResult loginResult) {

			mListener.onSuccess();
			makeRequest(loginResult);
		}

		@Override
		public void onCancel() {

			mListener.onCanceled();
		}

		@Override
		public void onError(FacebookException e) {

			mListener.onError(e);
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
						mListener.onSocialLogin(user);
					}
				});

		request.setParameters(parameters);
		request.executeAsync();
	}

	public void connect() {

		LoginManager.getInstance().logInWithReadPermissions(this, mPermissions);
	}

	@Override
	public boolean onBackPressed() {
		return new BackPressImpl(this).onBackPressed();
	}
}
