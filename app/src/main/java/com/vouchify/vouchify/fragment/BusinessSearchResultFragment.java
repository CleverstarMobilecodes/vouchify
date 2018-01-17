package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.SearchBusinessAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/12/16.
 */
@SuppressWarnings("deprecation")
public class BusinessSearchResultFragment extends BaseFragment {

	private static final String BUNDLE_BUSINESSES_TEXT_SEARCH = "bundle_businesses_text_search";
	private static final String BUNDLE_BUSINESS_TEXT_SEARCH = "bundle_business_text_search";
	private static final String BUNDLE_BUSINESS_TEXT_LOCATION = "bundle_business_text_location";
	private static final String BUNDLE_BUSINESS_LOCATION_ID = "bundle_business_location_id";
	private static final String BUNDLE_BUSINESS_LONGITUDE = "bundle_business_longitude";
	private static final String BUNDLE_BUSINESS_LATITUDE = "bundle_business_latitude";

	@BindView(R.id.search_result_txt_result)
	TextView txtResult;

	@BindView(R.id.search_result_btn_new)
	TextView btnAddNew;

	@BindView(R.id.search_result_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.search_result_txt_sorry)
	TextView txtNotResult;

	@BindView(R.id.search_result_main)
	NestedScrollView rlMain;

	private String mTxtSearch;
	private ArrayList<String> mTxtSearches;
	private String mTxtLocation;
	private double mTxtLongitude;
	private double mTxtLatitude;
	private long mLocationId;
	private Dialog mDialog;
	private List<BusinessEntity> mBusiness;
	private List<BusinessEntity> mBusinesses;

	public static BusinessSearchResultFragment getInstance(String textSearch,
														   String textLocation, long locationId, double longitude,
														   double latitude) {

		BusinessSearchResultFragment fragment = new BusinessSearchResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_BUSINESS_TEXT_SEARCH, textSearch);
		bundle.putString(BUNDLE_BUSINESS_TEXT_LOCATION, textLocation);
		bundle.putLong(BUNDLE_BUSINESS_LOCATION_ID, locationId);
		bundle.putDouble(BUNDLE_BUSINESS_LONGITUDE, longitude);
		bundle.putDouble(BUNDLE_BUSINESS_LATITUDE, latitude);
		fragment.setArguments(bundle);
		return fragment;
	}
	public static BusinessSearchResultFragment getInstance(ArrayList<String> mBusinessServicesNmaes,
														   String textLocation, long locationId, double longitude,
														   double latitude) {

		BusinessSearchResultFragment fragment = new BusinessSearchResultFragment();
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(BUNDLE_BUSINESSES_TEXT_SEARCH, mBusinessServicesNmaes);
		bundle.putString(BUNDLE_BUSINESS_TEXT_SEARCH, mBusinessServicesNmaes.get(0));
		bundle.putString(BUNDLE_BUSINESS_TEXT_LOCATION, textLocation);
		bundle.putLong(BUNDLE_BUSINESS_LOCATION_ID, locationId);
		bundle.putDouble(BUNDLE_BUSINESS_LONGITUDE, longitude);
		bundle.putDouble(BUNDLE_BUSINESS_LATITUDE, latitude);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_business_search_result;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.search_result);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;

			case R.id.search_result_btn_new :

				getUser();
				break;
			default :

				super.onClick(view);
		}
	}

	@Override
	protected void initView() {
		super.initView();

		mTxtSearch = getArguments().getString(BUNDLE_BUSINESS_TEXT_SEARCH);
		if (mTxtSearch.equals("- Show all -")){
			mTxtSearches = getArguments().getStringArrayList(BUNDLE_BUSINESSES_TEXT_SEARCH);
			int del = 0;
			for (int i = 0; i< mTxtSearches.size(); i++){
				if (mTxtSearches.get(i).equals("- Show all -")){
					del = i;
				}
			}
			mTxtSearches.remove(del);

		}
		mTxtLocation = getArguments().getString(BUNDLE_BUSINESS_TEXT_LOCATION);
		mTxtLongitude = getArguments().getDouble(BUNDLE_BUSINESS_LONGITUDE, 0);
		mTxtLatitude = getArguments().getDouble(BUNDLE_BUSINESS_LATITUDE, 0);
		mLocationId = getArguments().getLong(BUNDLE_BUSINESS_LOCATION_ID, 0);

		Utilities.setLayoutManager(mAct, recyclerView, true, true, true);
		if (mTxtSearch.equals("- Show all -")){
			searchBusinesses();
		}else {
			searchBusiness();
		}
	}

	private void updateUi() {

		String resultString;
		if (mBusiness == null) {

			txtNotResult.setVisibility(View.VISIBLE);
			resultString = String.format(
					"You searched for <b>%s</b><br><b>0</b> Results found",
					mTxtSearch);

			if (mTxtLocation.contains(getString(R.string.current_location))) {

				resultString = String
						.format("You searched for <b>%s</b><br>near <b>%s</b><br><b>0</b> Results found",
								mTxtSearch, mTxtLocation.toLowerCase());
			} else if (mTxtLocation.length() > 0) {

				resultString = String
						.format("You searched for <b>%s</b><br>near <b>%s</b><br><b>0</b> Results found",
								mTxtSearch, mTxtLocation);
			}
		} else if (mTxtLongitude != 0 && mTxtLatitude != 0) {

			if (mBusiness.size() == 0) {

				txtNotResult.setVisibility(View.VISIBLE);
			} else {

				txtNotResult.setVisibility(View.GONE);
			}

			if (mBusiness.size() == 1) {

				resultString = String
						.format("You searched for <b>%s</b><br>near <b>%s</b><br><b>%s</b> Result found",
								mTxtSearch, mTxtLocation.toLowerCase(),
								mBusiness.size());
			} else {

				resultString = String
						.format("You searched for <b>%s</b><br>near <b>%s</b><br><b>%s</b> Results found",
								mTxtSearch, mTxtLocation.toLowerCase(),
								mBusiness.size());
			}

		} else if (mLocationId == -1) {

			if (mBusiness.size() == 0) {

				txtNotResult.setVisibility(View.VISIBLE);
			} else {

				txtNotResult.setVisibility(View.GONE);
			}

			if (mBusiness.size() == 1) {

				resultString = String.format(
						"You searched for <b>%s</b><br><b>%s</b> Result found",
						mTxtSearch, mBusiness.size());
			} else {

				resultString = String
						.format("You searched for <b>%s</b><br><b>%s</b> Results found",
								mTxtSearch, mBusiness.size());
			}

		} else {

			if (mBusiness.size() == 0) {

				txtNotResult.setVisibility(View.VISIBLE);
			} else {

				txtNotResult.setVisibility(View.GONE);
			}

			if (mBusiness.size() == 1) {

				resultString = String
						.format("You searched for <b>%s</b><br>near <b>%s</b><br><b>%s</b> Result found",
								mTxtSearch, mTxtLocation, mBusiness.size());
			} else {

				resultString = String
						.format("You searched for <b>%s</b><br>near <b>%s</b><br><b>%s</b> Results found",
								mTxtSearch, mTxtLocation, mBusiness.size());
			}

		}
		txtResult.setText(Html.fromHtml(resultString));

		SearchBusinessAdapter adapter = new SearchBusinessAdapter(mAct, this,
				mBusiness);
		recyclerView.setAdapter(adapter);
		btnAddNew.setOnClickListener(this);
		rlMain.setVisibility(View.VISIBLE);
	}

	private void searchBusiness() {

		mDialog = LoadingDialog.show(mAct);
		JsonObject jObject = new JsonObject();
		jObject.addProperty("search_text", mTxtSearch);
		jObject.addProperty(Constants.KEY_SESSION_TOKEN, mAct.getUser()
				.getSessionTkn());
		jObject.addProperty(Constants.KEY_USER_ID, mAct.getUser().getUserId());
		if (mLocationId == 0) {

			jObject.addProperty("longitude", mTxtLongitude);
			jObject.addProperty("latitude", mTxtLatitude);
		} else {

			jObject.addProperty("location_suburb_id", mLocationId);
		}

		new DataApi(true).getInterface().searchBusiness(jObject)
				.enqueue(new BaseCallBack<BusinessEntity>() {
					@Override
					public void validResponse(Call<BusinessEntity> call,
											  Response<BusinessEntity> response) {

						Utilities.dismissDialog(mDialog);
						if (response.body().getStat()
								.equals(Constants.RESULT_FAIL)) {

							updateUi();
							if (response.body().getErrCode() == -1009) {

								mAct.showError(getString(R.string.err_no_internet_connection));
							}
							return;
						}

						mBusiness = response.body().getResult().getBusiness();
						if (mBusiness == null) {

							mBusiness = new ArrayList<>();
						}
						updateUi();
					}

					@Override
					public void onFailure(Call<BusinessEntity> call, Throwable t) {

						Utilities.dismissDialog(mDialog);
						updateUi();
					}
				});
	}
	private void searchBusinesses() {

		mDialog = LoadingDialog.show(mAct);
		final int numberOfAll = mTxtSearches.size();
		int i = 0;
		for (String mtxtsearches: mTxtSearches) {
			mBusinesses = new ArrayList<>();
			JsonObject jObject = new JsonObject();
			jObject.addProperty("search_text", mtxtsearches);
			jObject.addProperty(Constants.KEY_SESSION_TOKEN, mAct.getUser()
					.getSessionTkn());
			jObject.addProperty(Constants.KEY_USER_ID, mAct.getUser().getUserId());
			if (mLocationId == 0) {

				jObject.addProperty("longitude", mTxtLongitude);
				jObject.addProperty("latitude", mTxtLatitude);
			} else {

				jObject.addProperty("location_suburb_id", mLocationId);
			}
			i++;
			final int finalI = i;
			new DataApi(true).getInterface().searchBusiness(jObject)
					.enqueue(new BaseCallBack<BusinessEntity>() {
						@Override
						public void validResponse(Call<BusinessEntity> call,
												  Response<BusinessEntity> response) {

							if (response.body().getStat()
									.equals(Constants.RESULT_FAIL)) {

								Utilities.dismissDialog(mDialog);
								updateUi();
								if (response.body().getErrCode() == -1009) {

									mAct.showError(getString(R.string.err_no_internet_connection));
								}
								return;
							}

							mBusinesses = response.body().getResult().getBusiness();
							if (mBusiness == null) {

								mBusiness = new ArrayList<>();
							}
							if(mBusinesses != null) {
								if (mBusinesses.size() > 0) {
									for (BusinessEntity businessEntity : mBusinesses) {
										if (businessEntity != null) {
											BusinessEntity bbb = businessEntity;
											mBusiness.add(bbb);
										}
									}
								}
							}
								if (finalI == numberOfAll){
									Utilities.dismissDialog(mDialog);

									mBusiness = modifyBusiness(mBusiness);
									updateUi();
								}
								else
								{

									mBusiness = modifyBusiness(mBusiness);
									updateUi();
								}
						}

						@Override
						public void onFailure(Call<BusinessEntity> call, Throwable t) {

							Utilities.dismissDialog(mDialog);
							updateUi();
						}
					});
		}

	}
	private List<BusinessEntity> modifyBusiness(List<BusinessEntity> businessEntity){
		List<BusinessEntity> aBusiness = null;
//		List<BusinessEntity> bBusiness;
//		bBusiness = businessEntity;
 		if (businessEntity != null) {
			if (businessEntity.size() >= 0) {
				for (int k = 0; k < businessEntity.size(); k++) {
					if (aBusiness == null){
						aBusiness = new ArrayList<>();
					}
					if (aBusiness.size() == 0){
						aBusiness.add(businessEntity.get(k));
					}
					else if (aBusiness.size() > 0){
						boolean bBoolean = false;
						for (int j = 0; j < aBusiness.size(); j++){
							if (aBusiness.get(j).getBusinessName().equals(businessEntity.get(k).getBusinessName())){
								bBoolean = true;
								break;
							}
						}
						if (!bBoolean) {
							aBusiness.add(businessEntity.get(k));
						}
					}
				}
			}
		}
		return aBusiness;
	}
	private void getUser() {

		mDialog = LoadingDialog.show(mAct);
		ApiUtility.getUser(mAct, new BaseCallBack<UserEntity>() {
			@Override
			public void validResponse(Call<UserEntity> call,
					Response<UserEntity> response) {

				Utilities.dismissDialog(mDialog);
				if (response.body().getStat().equals(Constants.RESULT_FAIL)) {

					mAct.showError(response.body().getErrMessage());
					return;
				}

				final UserEntity user = response.body().getResult();

				if (user.isPhoneVerified()) {

					addChildFragment(CreateVouchFragment.getInstance(null, null),
							true);
					return;
				}

				Utilities.showAlertDialog(mAct, "",
						mAct.getString(R.string.you_should_verify_mobile),
						mAct.getString(R.string.verify),
						mAct.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {

								if (user.getMobile() != null
										&& user.getMobile().length() > 0) {

									addChildFragment(PhoneVerifyFragment
											.getInstance(user.getMobile(),
													false), true);
								}
								// else {
								//
								// // goto add mobile
								// }
							}
						}, null, true);
			}

			@Override
			public void onFailure(Call<UserEntity> call, Throwable t) {

				Utilities.dismissDialog(mDialog);
			}
		});
	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);

		BusinessEntity business = (BusinessEntity) object;
		addChildFragment(BusinessFragment.getInstance(business), true);
	}
}
