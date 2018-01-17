package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.CategoriesAdapter;
import com.vouchify.vouchify.adapter.SubCategoriesAdapter;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.CategoryEntity;
import com.vouchify.vouchify.utility.CustomPreferences;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/18/16.
 */
public class SubCategoryFragment extends BaseFragment {

	private static final String BUNDLE_CATEGORY = "bundle_category";
	List<BusinessEntity> mBusinessServices;
	@BindView(R.id.subcategory_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.subcategory_tv_title)
	TextView tvTitle;

	private CategoryEntity mCategory;
	public static SubCategoryFragment getInstance(CategoryEntity cate) {

		SubCategoryFragment fragment = new SubCategoryFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_CATEGORY, cate);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_subcategory;
	}

	@Override
	public void setToolBar() {
		super.setToolBar();

		assert txtTittle != null;
		txtTittle.setText(mCategory.getCategoryName());
		if (mCategory.getCategoryName().length() > 25){
			txtTittle.setTextSize(12);
		}
		else {
			txtTittle.setTextSize(16);
		}

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		super.initView();

		tvTitle.setVisibility(View.GONE);
		mCategory = (CategoryEntity) getArguments().getSerializable(BUNDLE_CATEGORY);
		if (mCategory == null) {

			mAct.onBackPressed();
			return;
		}
		getSubCategory(mCategory.getCategoryId());

	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);

		BusinessEntity business = (BusinessEntity) object;

		long locationId = 0;
		String txtLocation;
		double longitude = CustomPreferences.getPreferences(Constants.PREF_LONGITUDE, 0.0);
		double latitude = CustomPreferences.getPreferences(Constants.PREF_LATITUDE, 0.0);
		if (longitude != 0 && latitude != 0) {

			txtLocation = getString(R.string.current_location);

		} else {

            //Need to get information of user
			txtLocation = "test";
			locationId = 1;
		}
		if (business.getServiceName().equals("- Show all -")){
			ArrayList<String> sServiceNames = new ArrayList<>();

			for (BusinessEntity businessEntity : mBusinessServices){
					sServiceNames.add(businessEntity.getServiceName());
			}
			addChildFragment(BusinessSearchResultFragment.getInstance(sServiceNames, txtLocation, locationId,
					longitude, latitude), true);
		}
		else {
			addChildFragment(BusinessSearchResultFragment.getInstance(business.getServiceName(), txtLocation, locationId,
					longitude, latitude), true);
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;
			default :

				super.onClick(view);
		}
	}

	private void getSubCategory(int parentCategoryId) {

		final Dialog dialog = LoadingDialog.show(mAct);
		new DataApi(true).getInterface().getSubCategory(parentCategoryId).enqueue(new Callback<CategoryEntity>() {
			@Override
			public void onResponse(Call<CategoryEntity> call, Response<CategoryEntity> response) {

				Utilities.dismissDialog(dialog);
				CategoryEntity categoryBase = response.body();
				if (categoryBase.getStat().equals(Constants.RESULT_FAIL)) {

					mAct.showError(categoryBase.getErrMessage());
					return;
				}

				List<CategoryEntity> mCategories = categoryBase.getResult().getCategories();

				if (mCategories != null) {

					tvTitle.setVisibility(View.VISIBLE);
					recyclerView.setLayoutManager(new GridLayoutManager(mAct, 2));
					recyclerView.setAdapter(new CategoriesAdapter(mAct, categoryClick, mCategories));
					return;
				}

				mBusinessServices = categoryBase.getResult().getBusinessServices();
				if (mBusinessServices != null) {
					BusinessEntity allBussiness = new BusinessEntity(0, "- Show All -", 0, "- Show all -");
					mBusinessServices.add(0, allBussiness);
				}
				Utilities.setLayoutManager(mAct, recyclerView, true, true, true);
				recyclerView.setAdapter(new SubCategoriesAdapter(mAct, SubCategoryFragment.this, mBusinessServices));

			}

			@Override
			public void onFailure(Call<CategoryEntity> call, Throwable t) {

				Utilities.dismissDialog(dialog);
			}
		});
	}

	private OnItemClickListener categoryClick = new OnItemClickListener() {
		@Override
		public void onItemClick(Object object, int pos) {

			CategoryEntity cate = (CategoryEntity) object;
			addChildFragment(SubCategoryFragment.getInstance(cate), true);
		}
	};
}
