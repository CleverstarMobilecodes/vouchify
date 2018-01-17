package com.vouchify.vouchify.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.CategoriesAdapter;
import com.vouchify.vouchify.adapter.SearchBusinessServiceAdapter;
import com.vouchify.vouchify.adapter.SearchSuburbAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.CategoryEntity;
import com.vouchify.vouchify.entity.SuburbEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.CustomPreferences;
import com.vouchify.vouchify.utility.GPSTracker;
import com.vouchify.vouchify.utility.LogUtil;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.utility.utiInterface.GPSTrackerLocationChangeCallBack;
import com.vouchify.vouchify.view.CenteredImageSpan;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/18/16.
 */
public class SearchFragment extends BaseFragment
		implements
			View.OnFocusChangeListener,
			TextWatcher,
			TextView.OnEditorActionListener,
			GPSTrackerLocationChangeCallBack,
			View.OnTouchListener {

	@BindView(R.id.search_edt_filter)
	AutoCompleteTextView edtFilter;

	@BindView(R.id.search_btn_close)
	ImageView btnClose;

	@BindView(R.id.search_ll_location)
	LinearLayout llLocation;

	@BindView(R.id.search_edt_filter_location)
	AutoCompleteTextView edtSuburb;

	@BindView(R.id.search_btn_close_location)
	ImageView btnCloseLocation;

	@BindView(R.id.search_location_recycler_view)
	RecyclerView recyclerViewLocation;

	@BindView(R.id.search_business_recycler_view)
	RecyclerView recyclerViewSearchBusiness;

	@BindView(R.id.search_category_recycler_view)
	RecyclerView categoryRecyclerView;

	private long mDefaultSuburbId;
	private BusinessEntity mBusiness;
	private boolean mSearchLocationFocus;
	private List<SuburbEntity> mSuburbs;
	private List<BusinessEntity> mBusinesses;
	private boolean isOnClickItem;

	private long mCurrentSuburbId = -1;
	private GPSTracker mGPS;
	private double mLongitude = 0;
	private double mLatitude = 0;

	private TextWatcher twLocation = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i2,
				int i3) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i2,
				int i3) {

			if (charSequence.length() == 0) {

				btnCloseLocation.setVisibility(View.INVISIBLE);

				getSearchSuburb("");
				if (mSuburbs != null) {

					mSuburbs.clear();
				}
				mCurrentSuburbId = -1;
				return;
			}

			btnCloseLocation.setVisibility(View.VISIBLE);

			if (isOnClickItem) {

				isOnClickItem = false;
				return;
			}

			if (mSearchLocationFocus
					&& !charSequence.toString().contains(
							getString(R.string.current_location))) {

				getSearchSuburb(charSequence.toString());
			}
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	};

	public static SearchFragment getInstance() {

		return new SearchFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_search;
	}

	@Override
	public void setToolBar() {
		super.setToolBar();
		assert txtTittle != null;
		txtTittle.setText(R.string.search);
	}

	@Override
	protected void initView() {
		super.initView();

		mGPS = new GPSTracker(mAct, this);
		btnClose.setOnClickListener(this);
		edtFilter.setThreshold(1);
		edtFilter.addTextChangedListener(this);
		edtFilter.setOnFocusChangeListener(this);
		edtFilter.setOnEditorActionListener(this);

		btnCloseLocation.setOnClickListener(this);
		edtSuburb.setThreshold(1);
		edtSuburb.addTextChangedListener(twLocation);
		edtSuburb.setOnFocusChangeListener(this);
		edtSuburb.setOnEditorActionListener(this);
		recyclerViewLocation.setOnTouchListener(this);
		recyclerViewSearchBusiness.setOnTouchListener(this);

		categoryRecyclerView.setLayoutManager(new GridLayoutManager(mAct, 2));
		categoryRecyclerView.setVisibility(View.VISIBLE);
		getCategories();
		getUser();
	}

	@Override
	public void onResume() {
		super.onResume();

		updateToolbar(false);
		if (mGPS.canGetLocation() && mGPS.getLongitude() != 0
				&& mGPS.getLatitude() != 0) {

			if (edtSuburb.getText().toString().equals("")) {

				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append(getString(R.string.current_location)).append(
						"  ");
				edtSuburb.setText(setImageSpan(builder));
			}

			if (mSuburbs == null) {

				mSuburbs = new ArrayList<>();

			}

			if (mSuburbs.size() == 0 || mSuburbs.get(0).getSuburbId() != 0) {

				mSuburbs.add(0, new SuburbEntity(0,
						getString(R.string.current_location)));
			}

			setDataSuburbView(edtSuburb.getText().toString());
		} else {

			if (edtSuburb.getText().toString()
					.contains(getString(R.string.current_location))) {

				edtSuburb.setText("");
				edtSuburb.clearFocus();
			}

			if (mSuburbs != null && mSuburbs.size() > 0
					&& mSuburbs.get(0).getSuburbId() == 0) {

				mSuburbs.remove(0);
				setDataSuburbView(edtSuburb.getText().toString());
			}
		}

		Location location = mGPS.getLocation();
		if (location != null) {

			mLongitude = location.getLongitude();
			mLatitude = location.getLatitude();
            CustomPreferences.setPreferences(Constants.PREF_LONGITUDE, mLongitude);
            CustomPreferences.setPreferences(Constants.PREF_LATITUDE, mLatitude);
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.toolbar_txt_right :

				updateToolbar(false);
				break;
			case R.id.search_btn_close :

				if (edtFilter.requestFocus()) {

					edtFilter.setSelection(0);
				}
				if (edtFilter.getText().toString().isEmpty()) {

					if (mBusiness != null) {

						mBusiness = null;
					}
					updateToolbar(false);
					return;
				}

				edtFilter.setText("");
				if (mBusiness != null) {

					mBusiness = null;
				}
				btnClose.setVisibility(View.INVISIBLE);
				recyclerViewSearchBusiness.setVisibility(View.GONE);
				break;

			case R.id.search_btn_close_location :

				if (edtSuburb.requestFocus()) {

					edtSuburb.setSelection(0);
				}
				edtSuburb.setText("");
				if (mSuburbs != null) {

					mSuburbs.clear();
				}
				mCurrentSuburbId = -1;
				btnCloseLocation.setVisibility(View.INVISIBLE);
				recyclerViewLocation.setVisibility(View.GONE);
				break;
		}
	}

	/**
	 * Update toolbar
	 */
	private void updateToolbar(boolean isOpen) {

		assert txtRight != null;
		txtRight.setText(R.string.cancel);
		txtRight.setOnClickListener(this);
		if (isOpen) {

			txtRight.setVisibility(View.VISIBLE);
			llLocation.setVisibility(View.VISIBLE);
			categoryRecyclerView.setVisibility(View.GONE);
			return;
		}

		edtFilter.clearFocus();
		edtSuburb.clearFocus();
		btnClose.setVisibility(View.INVISIBLE);
		txtRight.setVisibility(View.INVISIBLE);
		recyclerViewSearchBusiness.setVisibility(View.GONE);
		recyclerViewLocation.setVisibility(View.GONE);
		categoryRecyclerView.setVisibility(View.VISIBLE);
		llLocation.setVisibility(View.INVISIBLE);
		Utilities.dismissKeyboard(mAct, edtFilter);
	}

	@Override
	public void onFocusChange(View view, boolean b) {

		switch (view.getId()) {

			case R.id.search_edt_filter :

				mSearchLocationFocus = false;
				if (b) {

					updateToolbar(true);
					llLocation.setVisibility(View.VISIBLE);
					recyclerViewLocation.setVisibility(View.GONE);
					recyclerViewSearchBusiness.setVisibility(View.VISIBLE);

					if (edtFilter.getText().toString().length() > 0) {

						btnClose.setVisibility(View.VISIBLE);

						if (mBusinesses != null && mBusinesses.size() > 0) {

							setDataBusinessServiceView(edtFilter.getText()
									.toString());
						}
						return;
					}

					if (mBusiness != null) {

						mBusiness = null;
					}
				}
				break;

			case R.id.search_edt_filter_location :

				if (b) {

					mSearchLocationFocus = true;
					recyclerViewLocation.setVisibility(View.VISIBLE);
					recyclerViewSearchBusiness.setVisibility(View.GONE);
					if (edtSuburb.getText().toString().length() > 0) {

						btnCloseLocation.setVisibility(View.VISIBLE);

						if (mSuburbs != null && mSuburbs.size() > 0) {

							setDataSuburbView(edtSuburb.getText().toString());
						}
						return;
					}

					if (mSuburbs != null) {

						mSuburbs.clear();
					}
					mCurrentSuburbId = -1;
					getSearchSuburb("");
					if (mBusiness != null) {

						mBusiness = null;
					}
				}

				break;
		}

	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1,
			int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		LogUtil.e(R.id.search_edt_filter + " onTextChanged "
				+ R.id.search_edt_filter_location, i + " i " + i1 + " i1 " + i2);
		updateToolbar(true);
		if (charSequence.length() == 0) {

			SearchBusinessServiceAdapter searchAdapter = new SearchBusinessServiceAdapter(
					SearchFragment.this, null, "");
			recyclerViewSearchBusiness.setAdapter(searchAdapter);

			if (mBusiness != null) {

				mBusiness = null;
			}
			btnClose.setVisibility(View.INVISIBLE);
			return;
		}

		btnClose.setVisibility(View.VISIBLE);

		if (isOnClickItem) {

			isOnClickItem = false;
			return;
		}

		getSearchBusinessOrService(charSequence.toString());

	}

	@Override
	public void afterTextChanged(Editable editable) {

	}

	private void getSearchBusinessOrService(final String txtSearch) {

		recyclerViewLocation.setVisibility(View.GONE);
		recyclerViewSearchBusiness.setVisibility(View.VISIBLE);
		categoryRecyclerView.setVisibility(View.GONE);
		new DataApi(false).getInterface().getCompleteSearch(txtSearch)
				.enqueue(new Callback<JsonObject>() {
					@Override
					public void onResponse(Call<JsonObject> call,
							Response<JsonObject> response) {

						JsonObject jsonObject = response.body()
								.getAsJsonObject("result");
						JsonArray jsonArray = jsonObject
								.getAsJsonArray("businesses");
						JsonArray jsonArrayBs = jsonObject
								.getAsJsonArray("business_services");

						Type listType = new TypeToken<ArrayList<BusinessEntity>>() {
						}.getType();

						mBusinesses = new ArrayList<>();
						List<BusinessEntity> itemBusinessService = new Gson()
								.fromJson(jsonArrayBs, listType);

						if (itemBusinessService.size() > 0) {

							mBusinesses.add(new BusinessEntity(0, "", -100,
									getString(R.string.business_service)));
							for (BusinessEntity business : itemBusinessService) {

								mBusinesses.add(business);
							}
						}

						List<BusinessEntity> itemBusinesses = new Gson()
								.fromJson(jsonArray, listType);
						if (itemBusinesses.size() > 0) {

							mBusinesses.add(new BusinessEntity(-100,
									getString(R.string.businesses), 0, ""));
							for (BusinessEntity business : itemBusinesses) {

								mBusinesses.add(business);
							}
						}

						setDataBusinessServiceView(txtSearch);
					}

					@Override
					public void onFailure(Call<JsonObject> call, Throwable t) {

					}
				});
	}

	private void setDataBusinessServiceView(String txtSearch) {

		Utilities.setLayoutManager(mAct, recyclerViewSearchBusiness, true,
				true, false);
		SearchBusinessServiceAdapter searchAdapter = new SearchBusinessServiceAdapter(
				SearchFragment.this, mBusinesses, txtSearch);
		recyclerViewSearchBusiness.setAdapter(searchAdapter);
	}

	private void getSearchSuburb(final String txtSearch) {

		recyclerViewLocation.setVisibility(View.VISIBLE);
		recyclerViewSearchBusiness.setVisibility(View.GONE);
		categoryRecyclerView.setVisibility(View.GONE);
		new DataApi(false).getInterface().getSuburbs(txtSearch)
				.enqueue(new Callback<JsonObject>() {
					@Override
					public void onResponse(Call<JsonObject> call,
							Response<JsonObject> response) {

						JsonObject jsonObject = response.body()
								.getAsJsonObject("result");
						JsonArray jsonArray = jsonObject
								.getAsJsonArray("suburbs");
						Type listType = new TypeToken<ArrayList<SuburbEntity>>() {
						}.getType();

						mSuburbs = new ArrayList<>();
						List<SuburbEntity> items = new Gson().fromJson(
								jsonArray, listType);
						if (mGPS.canGetLocation() && mGPS.getLongitude() != 0
								&& mGPS.getLatitude() != 0) {

							mSuburbs.add(new SuburbEntity(0,
									getString(R.string.current_location)));

							for (SuburbEntity suburb : items) {

								mSuburbs.add(suburb);
							}
						} else {

							mSuburbs = items;
						}

						setDataSuburbView(txtSearch);
					}

					@Override
					public void onFailure(Call<JsonObject> call, Throwable t) {

					}
				});
	}

	private void setDataSuburbView(String txtSearch) {

		Utilities.setLayoutManager(mAct, recyclerViewLocation, true, true,
				false);
		SearchSuburbAdapter mSuburbAdapter = new SearchSuburbAdapter(mAct,
				suburbOnItemClick, mSuburbs, txtSearch);
		recyclerViewLocation.setAdapter(mSuburbAdapter);
	}

	private OnItemClickListener suburbOnItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(Object object, int pos) {

			isOnClickItem = true;
			SuburbEntity mSuburb = (SuburbEntity) object;
			Utilities.dismissKeyboard(mAct, edtSuburb);
			recyclerViewLocation.setVisibility(View.GONE);
			if (mSuburb.getDisplay().contains(
					getString(R.string.current_location))) {

				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append(getString(R.string.current_location)).append(
						"  ");
				edtSuburb.setText(setImageSpan(builder));
			} else {

				edtSuburb.setText(mSuburb.getDisplay());
			}
			edtSuburb.clearFocus();
			mSearchLocationFocus = false;

			if (mBusiness != null && mBusiness.getBusinessId() != 0) {

				edtFilter.setText(mBusiness.getBusinessName());
				goToBusiness(mBusiness);
				return;
			}

			if (mBusiness != null && mBusiness.getServiceId() != 0) {

				goToStartSearch();
				return;
			}

			if (!edtFilter.getText().toString().isEmpty()) {

				goToStartSearch();
				return;
			}

			if (edtFilter.requestFocus()) {

				edtFilter.setSelection(0);
			}
		}
	};

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			openGPS();
		}
	}

	private void getUser() {

		ApiUtility.getUser(mAct, new BaseCallBack<UserEntity>() {
			@Override
			public void validResponse(Call<UserEntity> call,
					Response<UserEntity> response) {

				UserEntity user = response.body();
				if (user.getStat().equals(Constants.RESULT_FAIL)) {

					mAct.showError(user.getErrMessage());
					return;
				}

				user = user.getResult();
				if (user.getDefaultSearchSuburbId() > 0
						&& user.getDefaultSearchSuburbDisplay() != null) {

					mDefaultSuburbId = user.getDefaultSearchSuburbId();
				}

				String[] permissions = new String[]{
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION};
				if (!Utilities.hasPermissions(mAct, permissions)) {

					requestPermissions(permissions, 0);
					return;
				}
				openGPS();
			}

			@Override
			public void onFailure(Call<UserEntity> call, Throwable t) {

			}
		});
	}

	private void getCategories() {

		new DataApi(true).getInterface().getSubcategoriesOrServices()
				.enqueue(new Callback<JsonObject>() {
					@Override
					public void onResponse(Call<JsonObject> call,
							Response<JsonObject> response) {

						JsonObject jsonObject = response.body()
								.getAsJsonObject("result");
						JsonArray jsonArray = jsonObject
								.getAsJsonArray("categories");
						Type listType = new TypeToken<ArrayList<CategoryEntity>>() {
						}.getType();

						List<CategoryEntity> mCategories = new Gson().fromJson(
								jsonArray, listType);
						categoryRecyclerView.setAdapter(new CategoriesAdapter(
								mAct, categoryClick, mCategories));
					}

					@Override
					public void onFailure(Call<JsonObject> call, Throwable t) {

					}
				});
	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);

		isOnClickItem = true;
		mSearchLocationFocus = false;
		mBusiness = (BusinessEntity) object;
		Utilities.dismissKeyboard(mAct, edtFilter);
		edtFilter.clearFocus();
		recyclerViewSearchBusiness.setVisibility(View.GONE);
		if (mBusiness.getBusinessId() != 0) {

			edtFilter.setText(mBusiness.getBusinessName());
			goToBusiness(mBusiness);
			return;
		}

		edtFilter.setText(mBusiness.getServiceName());

		goToStartSearch();
	}

	private void goToBusiness(BusinessEntity search) {

		updateToolbar(false);
		addChildFragment(BusinessFragment.getInstance(search), true);
	}

	private void getCurrentLocation() {

		if (mDefaultSuburbId > 0) {

			mCurrentSuburbId = mDefaultSuburbId;
			return;
		}

		if (mCurrentSuburbId < 0 && mSuburbs != null) {

			for (SuburbEntity suburb : mSuburbs) {

				if (edtSuburb.getText().toString().equals(suburb.getDisplay())) {

					mCurrentSuburbId = suburb.getSuburbId();
					break;
				}
			}
		}

		if (mCurrentSuburbId < 0
				&& mGPS.canGetLocation()
				&& edtSuburb.getText().toString()
						.contains(getString(R.string.current_location))) {

			mCurrentSuburbId = 0;
		}

	}
	private void goToStartSearch() {

		getCurrentLocation();

		if (mCurrentSuburbId < 0 && !mGPS.canGetLocation()) {

			if (edtSuburb.requestFocus()) {

				edtSuburb.setSelection(0);
			}
			return;
		}
		updateToolbar(false);
		long locationId = 0;
		double longitude = 0;
		double latitude = 0;
		if (mGPS.canGetLocation()
				&& edtSuburb.getText().toString()
						.contains(getString(R.string.current_location))) {

			if (mGPS.getLongitude() == 0 && mGPS.getLatitude() == 0) {

				longitude = mLongitude;
				latitude = mLatitude;
			} else {

				longitude = mGPS.getLongitude();
				latitude = mGPS.getLatitude();
			}

		} else {

			locationId = mCurrentSuburbId;
		}

		String txtLocation;
		if (edtSuburb.getText().toString()
				.contains(getString(R.string.current_location))) {

			txtLocation = getString(R.string.current_location);
		} else {

			txtLocation = edtSuburb.getText().toString();
		}

		addChildFragment(BusinessSearchResultFragment.getInstance(edtFilter
				.getText().toString(), txtLocation, locationId, longitude,
				latitude), true);
	}

	@Override
	public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

		getCurrentLocation();
		switch (textView.getId()) {

			case R.id.search_edt_filter :

				recyclerViewSearchBusiness.setVisibility(View.GONE);
				recyclerViewLocation.setVisibility(View.GONE);
				updateToolbar(false);
				if (edtSuburb.getText().toString()
						.contains(getString(R.string.current_location))
						&& mGPS.canGetLocation()) {

					if (edtFilter.getText().toString().equals("")) {

						if (edtFilter.requestFocus()) {

							edtFilter.setSelection(0);
						}
						mAct.showError(getString(R.string.business_or_business_service_is_required));
						return false;
					}

					edtFilter.clearFocus();
					goToStartSearch();
					return false;
				}

				if (mCurrentSuburbId > -1) {

					if (edtFilter.getText().toString().equals("")) {

						updateToolbar(false);

						if (edtFilter.requestFocus()) {

							edtFilter.setSelection(0);
						}
						mAct.showError(getString(R.string.business_or_business_service_is_required));
						return false;
					}

					edtFilter.clearFocus();
					goToStartSearch();
					return false;
				}

				if (edtFilter.getText().toString().equals("")) {

					updateToolbar(false);

					if (edtFilter.requestFocus()) {

						edtFilter.setSelection(0);
					}
					mAct.showError(getString(R.string.business_or_business_service_is_required));
					return false;
				}

				// if (edtSuburb.requestFocus()) {
				//
				// edtSuburb.setSelection(0);
				// }
				goToStartSearch();
				break;

			case R.id.search_edt_filter_location :

				recyclerViewSearchBusiness.setVisibility(View.GONE);
				recyclerViewLocation.setVisibility(View.GONE);
				updateToolbar(false);
				if (mBusiness != null && mBusiness.getBusinessId() != 0) {

					edtSuburb.clearFocus();
					goToBusiness(mBusiness);
					return false;
				}

				if (mCurrentSuburbId > -1) {

					if (edtFilter.getText().toString().equals("")) {

						if (edtFilter.requestFocus()) {

							edtFilter.setSelection(0);
						}
						return false;
					}

					edtSuburb.clearFocus();
					goToStartSearch();
					return false;
				}

				if (edtSuburb.getText().toString()
						.contains(getString(R.string.current_location))
						&& mGPS.canGetLocation()) {

					edtFilter.clearFocus();
					goToStartSearch();
					return false;
				}

				if (edtSuburb.getText().toString().equals("")) {

					updateToolbar(false);
					mAct.showError(getString(R.string.location_is_required));
					return false;
				}

				break;
		}
		return false;
	}

	private void openGPS() {

		if (mGPS.canGetLocation() && mGPS.getLongitude() != 0
				&& mGPS.getLatitude() != 0
				&& edtSuburb.getText().toString().equals("")) {

			SpannableStringBuilder builder = new SpannableStringBuilder();
			builder.append(getString(R.string.current_location)).append("  ");
			edtSuburb.setText(setImageSpan(builder));
		}

		Location location = mGPS.getLocation();
		if (location != null) {

			mLongitude = location.getLongitude();
			mLatitude = location.getLatitude();
            CustomPreferences.setPreferences(Constants.PREF_LONGITUDE, mLongitude);
            CustomPreferences.setPreferences(Constants.PREF_LATITUDE, mLatitude);
		}

	}

	@Override
	public void locationChange(Location location) {

		mLongitude = location.getLongitude();
		mLatitude = location.getLatitude();
        CustomPreferences.setPreferences(Constants.PREF_LONGITUDE, mLongitude);
        CustomPreferences.setPreferences(Constants.PREF_LATITUDE, mLatitude);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {

		Utilities.dismissKeyboard(mAct, view);
		return false;
	}

	private SpannableStringBuilder setImageSpan(SpannableStringBuilder builder) {

		builder.setSpan(new CenteredImageSpan(getActivity(),
				R.drawable.ic_edt_search_location), builder.length() - 1,
				builder.length(), 0);
		return builder;
	}

	private OnItemClickListener categoryClick = new OnItemClickListener() {
		@Override
		public void onItemClick(Object object, int pos) {

			CategoryEntity cate = (CategoryEntity) object;
			addChildFragment(SubCategoryFragment.getInstance(cate), true);
		}
	};
}
