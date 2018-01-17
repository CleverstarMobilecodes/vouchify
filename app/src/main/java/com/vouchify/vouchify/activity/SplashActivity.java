package com.vouchify.vouchify.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.Utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

	@Override
	protected int addView() {
		return R.layout.activity_splash;
	}

	@Override
	protected void initView(@Nullable Bundle savedInstanceState) {
		super.initView(savedInstanceState);

		String str = printKeyHash(this);
		Log.d("Hash Key = ", str);
		// Add code to print out the key hash
		// try {
		// PackageInfo info = getPackageManager().getPackageInfo(
		// getPackageName(), PackageManager.GET_SIGNATURES);
		// for (Signature signature : info.signatures) {
		// MessageDigest md = MessageDigest.getInstance("SHA");
		// md.update(signature.toByteArray());
		// LogUtil.e("KeyHash:",
		// Base64.encodeToString(md.digest(), Base64.DEFAULT));
		// }
		// } catch (PackageManager.NameNotFoundException e) {
		//
		// } catch (NoSuchAlgorithmException e) {
		//
		// }
		final UserEntity user = Utilities.getSavedUser();

		setUser(Utilities.getSavedUser());
        //CustomPreferences.setPreferences(Constants.PREF_SESSION_TOKEN,"Shit");
		if (user.getUserId() == null || user.getUserId().isEmpty()
				|| user.getSessionTkn() == null
				|| user.getSessionTkn().isEmpty()) {

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					startNextScreen(false);
				}
			}, 1500);

			return;
		}

		ApiUtility.getUser(this, new BaseCallBack<UserEntity>() {

			@Override
			public void validResponse(Call<UserEntity> call,
					Response<UserEntity> response) {

				UserEntity usr = response.body();
				if (usr == null || usr.getStat().equals(Constants.RESULT_FAIL)) {

					if (user.getPassword() == null
							|| user.getPassword().isEmpty()) {

						ApiUtility.facebookLogin(user, mCallback);
					} else {

						ApiUtility.login(user, mCallback);
					}

					return;
				}

				setUser(Utilities.getSavedUser());
				startNextScreen(true);
			}
		});
	}
	public static String printKeyHash(Activity context) {
		PackageInfo packageInfo;
		String key = null;
		try {
			//getting application package name, as defined in manifest
			String packageName = context.getApplicationContext().getPackageName();

			//Retriving package info
			packageInfo = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);

			Log.d("Package Name=", context.getApplicationContext().getPackageName());

			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				key = new String(Base64.encode(md.digest(), 0));

				// String key = new String(Base64.encodeBytes(md.digest()));
				Log.d("Key Hash=", key);
			}
		} catch (PackageManager.NameNotFoundException e1) {
			Log.e("Name not found", e1.toString());
		}
		catch (NoSuchAlgorithmException e) {
			Log.e("No such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}

		return key;
	}
	private BaseCallBack<UserEntity> mCallback = new BaseCallBack<UserEntity>() {

		@Override
		public void validResponse(Call<UserEntity> call,
				Response<UserEntity> response) {

			UserEntity user = response.body();
			if (user.getStat().equals(Constants.RESULT_FAIL)) {

				startNextScreen(false);
				return;
			}

			getUser().setUserId(user.getResult().getUserId());
			getUser().setSessionTkn(user.getResult().getSessionTkn());
			startNextScreen(true);
		}

		@Override
		public void onFailure(Call<UserEntity> call, Throwable t) {

		}
	};

	/**
	 * Start main activity
	 * 
	 */
	private void startNextScreen(boolean loginSuccess) {

		if (loginSuccess) {

			Utilities.saveUser(getUser());
			startActivity(new Intent(SplashActivity.this, MainActivity.class));
		} else {

			Utilities.saveUser(null);
			startActivity(new Intent(SplashActivity.this, AuthActivity.class));
			finish();
		}

		finish();
	}
}
