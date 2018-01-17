package com.vouchify.vouchify.fragment;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.ViewPagerAdapter;
import com.vouchify.vouchify.fragment.inf.OnBackPressListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 7/30/16.
 */
public class MainFragment extends BaseFragment {

	@BindView(R.id.main_view_pager)
	ViewPager viewPager;

	@BindView(R.id.main_view_tab_layout)
	TabLayout tabLayout;

	private ViewPagerAdapter mAdapter;

	public static MainFragment getInstance() {

		return new MainFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_main;
	}

	@Override
	protected void initView() {
		super.initView();

		String[] titles = getResources().getStringArray(R.array.tab_titles);
		TypedArray tabDrawables = getResources().obtainTypedArray(
				R.array.tab_drawables);
		mAdapter = new ViewPagerAdapter(getChildFragmentManager(), titles);
		viewPager.setAdapter(mAdapter);
		tabLayout.setupWithViewPager(viewPager);
		viewPager.setOffscreenPageLimit(4);

		// Set tab view
		LayoutInflater inflater = LayoutInflater.from(mAct);
		for (int i = 0; i < tabLayout.getTabCount(); i++) {

			@SuppressLint("InflateParams")
			View view = inflater.inflate(R.layout.item_tab, null);
			TextView textView = ButterKnife.findById(view, R.id.item_tab_txt);
			ImageView imageView = ButterKnife.findById(view, R.id.item_tab_imv);
			textView.setText(titles[i]);
			imageView.setImageResource(tabDrawables.getResourceId(i,
					R.mipmap.ic_launcher));
			TabLayout.Tab tab = tabLayout.getTabAt(i);
			if (tab != null) {

				tab.setCustomView(view);
			}
		}

		tabDrawables.recycle();
		tabLayout
				.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
					@Override
					public void onTabSelected(TabLayout.Tab tab) {

						BaseFragment fragment = mAdapter
								.getRegisteredFragment(0);
						fragment.backChildFragment();
					}

					@Override
					public void onTabUnselected(TabLayout.Tab tab) {

					}

					@Override
					public void onTabReselected(TabLayout.Tab tab) {

						if (tab.getPosition() == 0) {

							BaseFragment fragment = mAdapter
									.getRegisteredFragment(0);
							fragment.backChildFragment();
						}
					}
				});
	}

	@Override
	public boolean onBackPressed() {

		OnBackPressListener fragment = mAdapter.getRegisteredFragment(viewPager
				.getCurrentItem());
		return fragment != null && fragment.onBackPressed();
	}
}
