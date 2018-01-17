package com.vouchify.vouchify.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.vouchify.vouchify.fragment.BaseFragment;
import com.vouchify.vouchify.fragment.FeedFragment;
import com.vouchify.vouchify.fragment.FriendsFragment;
import com.vouchify.vouchify.fragment.ProfileFragment;
import com.vouchify.vouchify.fragment.SearchFragment;

/**
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

	private String[] mTitles;
	SparseArray<BaseFragment> mFragments;

	/**
	 * Create pager adapter
	 *
	 */
	public ViewPagerAdapter(FragmentManager fm, String[] titles) {
		super(fm);
		this.mTitles = titles;
		this.mFragments = new SparseArray<>();
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {

			case 1 :

				return FeedFragment.getInstance();
			case 2 :

				return FriendsFragment.getInstance();
			case 3 :

				return ProfileFragment.getInstance();
			default :

				return SearchFragment.getInstance();
		}
	}

	@Override
	public int getCount() {
		return mTitles.length;
	}

	@Override
	public CharSequence getPageTitle(final int position) {
		return mTitles[position];
	}

	/**
	 * On each Fragment instantiation we are saving the reference of that
	 * Fragment in a Map It will help us to retrieve the Fragment by position
	 *
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		BaseFragment fragment = (BaseFragment) super.instantiateItem(container,
				position);
		mFragments.put(position, fragment);
		return fragment;
	}

	/**
	 * Remove the saved reference from our Map on the Fragment destroy
	 *
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		mFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	/**
	 * Get the Fragment by position
	 *
	 */
	public BaseFragment getRegisteredFragment(int position) {
		return mFragments.get(position);
	}
}
