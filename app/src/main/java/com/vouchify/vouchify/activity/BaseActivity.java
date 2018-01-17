package com.vouchify.vouchify.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.fragment.BaseFragment;
import com.vouchify.vouchify.utility.CustomPreferences;
import com.vouchify.vouchify.utility.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 8/27/15.
 */
public class BaseActivity extends AppCompatActivity
		implements
			View.OnClickListener {

	private UserEntity mUser;

	@Nullable
	@BindView(R.id.toolbar_btn_left)
	ImageView btnBarLeft;

	@Nullable
	@BindView(R.id.toolbar_btn_right)
	ImageView btnBarRight;

	@Nullable
	@BindView(R.id.toolbar_txt_title)
	TextView txtBarTitle;

	@Nullable
	@BindView(R.id.error_txt)
	TextView txtError;

	private Handler mHandler;
	private Runnable mRunnable;
	private View[] mViews = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// UI
		int layoutId = addView();
		setContentView(layoutId);
		ButterKnife.bind(this);
		CustomPreferences.init(this);
		initView(savedInstanceState);
		getSupportFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {
					public void onBackStackChanged() {

						FragmentManager manager = getSupportFragmentManager();
						if (manager != null) {

							BaseFragment fragment = (BaseFragment) manager
									.findFragmentById(R.id.activity_container);
							fragment.setToolBar();
						}
					}
				});
	}
	/**
	 * Add layout view for activity
	 *
	 * @return Layout view id
	 */
	protected int addView() {

		return 0;
	}

	protected void initView(@Nullable Bundle savedInstanceState) {

		mHandler = new Handler();
		mRunnable = new Runnable() {
			@Override
			public void run() {

				if (mViews != null) {

					for (View view : mViews) {

						view.setVisibility(View.VISIBLE);
					}
				}

				if (txtError == null) {

					return;
				}

				txtError.setVisibility(View.GONE);
			}
		};
	}

	@Override
	public void onClick(View view) {

	}

	/**
	 * add fragment
	 *
	 * @param fragment
	 *            Fragment
	 * @param isAddToBackStack
	 *            Add fragment to back stack or not
	 */
	public void addFragment(BaseFragment fragment, boolean isAddToBackStack) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.activity_container);
		ft.add(R.id.activity_container, fragment, fragment.getClass().getName());
		if (isAddToBackStack) {

			ft.addToBackStack(null);
		}

		if (currentFragment != null) {

			ft.hide(currentFragment);
		}

		View current = getCurrentFocus();
		if (current != null) {

			Utilities.dismissKeyboard(this, current);
		}

		ft.commit();
	}

	/**
	 * Back to previous fragment
	 */
	public void backFragment(boolean backToHome) {

		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() <= 0) {

			finish();
			return;
		}

		if (backToHome) {

			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else {

			fm.popBackStack();
		}
	}

	/*
	 * Show error
	 */
	public void showError(String text, View... views) {

		mViews = views;
		if (txtError == null) {

			return;
		}

		if (txtError.getVisibility() == View.VISIBLE) {

			txtError.setVisibility(View.GONE);
			mHandler.removeCallbacks(mRunnable);
		}

		if (mViews != null) {

			for (View view : mViews) {

				view.setVisibility(View.INVISIBLE);
			}
		}

		txtError.setText(text);
		txtError.setVisibility(View.VISIBLE);
		mHandler.postDelayed(mRunnable, 3500);
	}

	public UserEntity getUser() {
		return mUser;
	}

	public void setUser(UserEntity mUser) {
		this.mUser = mUser;
	}
}
