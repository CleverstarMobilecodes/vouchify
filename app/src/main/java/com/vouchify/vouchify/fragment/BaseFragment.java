package com.vouchify.vouchify.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.activity.BaseActivity;
import com.vouchify.vouchify.activity.MainActivity;
import com.vouchify.vouchify.fragment.inf.BackPressImpl;
import com.vouchify.vouchify.fragment.inf.OnBackPressListener;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 8/28/15.
 */
public class BaseFragment extends Fragment
		implements
			View.OnClickListener,
			OnItemClickListener,
			OnBackPressListener {

	@Nullable
	@BindView(R.id.toolbar_btn_left)
	ImageView btnMenu;

	@Nullable
	@BindView(R.id.toolbar_txt_right)
	TextView txtRight;

	@Nullable
	@BindView(R.id.toolbar_txt_left)
	TextView txtLeft;

	@Nullable
	@BindView(R.id.toolbar_txt_title)
	TextView txtTittle;

	protected BaseActivity mAct;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		int viewId = addView();
		View view = inflater.inflate(viewId, container, false);

		ButterKnife.bind(this, view);
		initView();
		setToolBar();
		// view.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		//
		// Utilities.dismissKeyboard(mAct, view);
		// }
		// });
		return view;
	}

	/**
	 * Add fragment
	 */
	protected void addFragment(BaseFragment fragment, boolean addToStack) {

		mAct.addFragment(fragment, addToStack);
	}

	/**
	 * Back fragment
	 *
	 * @param backToHome
	 *            true if wanna back to home
	 */
	protected void backFragment(boolean backToHome) {

		mAct.backFragment(backToHome);
	}

	/**
	 * Add view
	 */
	protected int addView() {

		return 0;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		mAct = (BaseActivity) context;
	}

	/**
	 * Init child view
	 */
	protected void initView() {

		if (btnMenu != null) {

			btnMenu.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				((MainActivity) mAct).toggleDrawer();
				break;
		}
	}

	/**
	 * Set toolbar
	 */
	@SuppressWarnings("ConstantConditions")
	public void setToolBar() {

		if (btnMenu != null) {

			btnMenu.setVisibility(View.VISIBLE);
			btnMenu.setImageResource(R.drawable.ic_action_menu);
		}
	}

	@Override
	public void onItemClick(Object object, int pos) {

	}

	@Override
	public boolean onBackPressed() {

		return new BackPressImpl(this).onBackPressed();
	}

	/**
	 * add fragment
	 *
	 * @param fragment
	 *            Fragment
	 * @param isAddToBackStack
	 *            Add fragment to back stack or not
	 */
	public void addChildFragment(BaseFragment fragment, boolean isAddToBackStack) {

		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		BaseFragment currentFragment = (BaseFragment) getChildFragmentManager()
				.findFragmentById(R.id.fragment_container);
		ft.add(R.id.fragment_container, fragment, fragment.getClass().getName());
		if (isAddToBackStack) {

			ft.addToBackStack(null);
		}

		if (currentFragment != null) {

			ft.hide(currentFragment);
		}

		ft.commit();
	}
	public void addChildFragment(BaseFragment fragment, String backStack) {

		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		BaseFragment currentFragment = (BaseFragment) getChildFragmentManager()
				.findFragmentById(R.id.fragment_container);
		ft.add(R.id.fragment_container, fragment, fragment.getClass().getName());
//		if (isAddToBackStack) {

			ft.addToBackStack(backStack);
//		}

		if (currentFragment != null) {

			ft.hide(currentFragment);
		}

		ft.commit();
	}

	public void backChildFragment() {

		FragmentManager fm = getChildFragmentManager();
		if (fm.getBackStackEntryCount() <= 0) {

			return;
		}

		fm.popBackStack();
	}
	public void backChildFragment(String backStack) {

		FragmentManager fm = getChildFragmentManager();
		if (fm.getBackStackEntryCount() <= 0) {

			return;
		}

		fm.popBackStack(backStack, 0);
	}
}
