package com.vouchify.vouchify.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.MenuAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.fragment.BaseFragment;
import com.vouchify.vouchify.fragment.BrowserFragment;
import com.vouchify.vouchify.fragment.MainFragment;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.ImageViewWithText;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 11/2/15.
 */
public class MainActivity extends BaseActivity implements OnItemClickListener {

	@BindView(R.id.drawer_layout)
	DrawerLayout drawer;

	@BindView(R.id.menu_recycler_view)
	RecyclerView menuView;

	@BindView(R.id.activity_bottom_view)
	TabLayout viewBottom;

	@BindView(R.id.menu_item_imv_profile)
	ImageViewWithText imvProfile;

	@BindView(R.id.menu_item_txt_name)
	TextView txtName;

	@BindView(R.id.menu_item_logout)
	TextView btnLogout;

	@Override
	protected int addView() {
		return R.layout.activity_main;
	}

	@Override
	protected void initView(@Nullable Bundle savedInstanceState) {
		super.initView(savedInstanceState);

		// Set current user
		setUser(Utilities.getSavedUser());
		if (savedInstanceState == null) {

			addFragment(MainFragment.getInstance(), false);
		}

		setDrawerMenu();
		setMenuImage(getUser());
		// btnLeft.setVisibility(View.VISIBLE);
		viewBottom.setVisibility(View.INVISIBLE);
		// btnLeft.setImageResource(R.drawable.ic_action_menu);
		Utilities.setLayoutManager(this, menuView, true, true, false);

		// btnLeft.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

		// case R.id.toolbar_btn_left :
		//
		// toggleDrawer();
		// break;
			case R.id.menu_item_logout :

				toggleDrawer();
				logOut();
				break;
		}
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Toggle drawer
	 */
	public void toggleDrawer() {

		Utilities.dismissKeyboard(this, drawer);
		if (drawer.isDrawerOpen(GravityCompat.START)) {

			drawer.closeDrawer(GravityCompat.START);
			return;
		}

		drawer.openDrawer(GravityCompat.START);
	}

	/**
	 * Set drawer menu
	 */
	private void setDrawerMenu() {

		List<String> menus = new ArrayList<>();

		menus.add(getString(R.string.about));
		menus.add(getString(R.string.contact_us));
		menus.add(getString(R.string.privacy_policy));
		menus.add(getString(R.string.terms_of_use));
		menus.add(getString(R.string.add_a_business));

		// Set adapter
		MenuAdapter adapter = new MenuAdapter(this, menus);
		menuView.setAdapter(adapter);
	}

	/**
	 * Set menu image
	 */
	public void setMenuImage(UserEntity user) {

		String lastName = user.getLastName();
		String firstName = user.getFirstName();
		txtName.setText(String.format("%s %s", firstName, lastName));
		imvProfile.setTextSize(getResources().getDimension(
				R.dimen.menu_item_text));
		imvProfile.displayImage(user.getProfileUrl(), firstName, lastName);
	}
	@Override
	public void onItemClick(Object object, int pos) {

		switch (pos) {

			case 0 :

				addFragment(BrowserFragment.getInstance(Constants.URL_ABOUT),
						true);
				break;
			case 1 :
				List<String> arrEmail = new ArrayList<>();
				String strEmail = "";
				strEmail = getString(R.string.contact_us_email);
				arrEmail.add(strEmail);
				String subject = String.format(getString(R.string.contact_us_subject));
				Utilities.startSendMail(this, strEmail, subject);
				break;
			case 2 :

				addFragment(BrowserFragment.getInstance(Constants.URL_POLICY),
						true);
				break;
			case 3 :

				addFragment(BrowserFragment.getInstance(Constants.URL_TERMS),
						true);
				break;
			case 4 :

				break;
		}

		toggleDrawer();
	}

	@Override
	public void onBackPressed() {

		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.activity_container);
		if (fragment == null || !fragment.onBackPressed()) {

			if (closeDrawer()) {

				return;
			}

			super.onBackPressed();
		}
	}

	/**
     * 
     */
	public boolean closeDrawer() {

		if (drawer.isDrawerOpen(GravityCompat.START)) {

			toggleDrawer();
			return true;
		}

		return false;
	}

	/**
	 * Log out
	 */
	private void logOut() {

		new UserApi(true).getInterface()
				.logOut(Utilities.addUserAuth(this, null))
				.enqueue(new BaseCallBack<BaseEntity>() {
					@Override
					public void validResponse(Call<BaseEntity> call,
							Response<BaseEntity> response) {

					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

					}
				});

		Utilities.saveUser(null);
		if (AccessToken.getCurrentAccessToken() != null) {

			LoginManager.getInstance().logOut();
		}

		Intent intent = new Intent(this, AuthActivity.class);
		intent.putExtra(Constants.INTENT_LOGOUT, true);
		startActivity(intent);
		finish();
	}
}
