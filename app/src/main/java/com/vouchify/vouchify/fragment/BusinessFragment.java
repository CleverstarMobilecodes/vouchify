package com.vouchify.vouchify.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.BusinessVouchesAdapter;
import com.vouchify.vouchify.adapter.ContactDetailAndAddressAdapter;
import com.vouchify.vouchify.adapter.ListSpinnerServiceAdapter;
import com.vouchify.vouchify.adapter.MapsAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.BusinessPhotoView;
import com.vouchify.vouchify.view.CustomTextView;
import com.vouchify.vouchify.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/11/16.
 */
@SuppressWarnings("deprecation")
public class BusinessFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

	private static final String BUNDLE_BUSINESS = "bundle_business";

	@BindView(R.id.business_imv_business)
	ImageView ivBusiness;

	@BindView(R.id.business_txt_business_name)
	TextView txtBusinessName;

	@BindView(R.id.business_txt_business_location)
	TextView txtLocation;

	@BindView(R.id.vouch_txt_business_des)
	TextView txtBusinessDes;

	@BindView(R.id.business_txt_vouches_count)
	TextView txtVouchesCount;

	@BindView(R.id.business_vouch)
	TextView btnAddVouch;

	@BindView(R.id.business_call)
	TextView btnCall;

	@BindView(R.id.business_enquire)
	TextView btnEmail;

	@BindView(R.id.business_rg_tab)
	RadioGroup rgTabs;

	@BindView(R.id.business_ll_about)
	LinearLayout llAbout;

	@BindView(R.id.business_ll_vouches)
	LinearLayout llVouches;

	@BindView(R.id.business_ll_photos)
	LinearLayout llPhotos;

	@BindView(R.id.business_vouches_recycler_view)
	RecyclerView vouchRecyclerView;

	@BindView(R.id.business_text_about_short)
	WebView txtAboutShort;

	@BindView(R.id.business_contact_detail_recycler_view)
	RecyclerView contactDetailRecyclerView;

	@BindView(R.id.business_address_recycler_view)
	RecyclerView addressRecyclerView;

	@BindView(R.id.business_text_not_photo)
	TextView txtNotPhoto;

	@BindView(R.id.business_vouch_filter)
	Spinner spnVouchFilter;

	@BindView(R.id.business_photo_view)
	BusinessPhotoView businessPhotoView;

	@BindView(R.id.business_map_recycler_view)
	RecyclerView mapsRecyclerView;

	@BindView(R.id.business_ll_main)
	LinearLayout llMain;

	@BindView(R.id.business_friend_vouch)
	LinearLayout llFriendVouch;

	@BindView(R.id.business_txt_friend_vouches_count)
	TextView txtFriendVouchesCount;

	@BindView(R.id.business_all_vouch)
	LinearLayout llAllVouch;

	@BindView(R.id.business_cb_vouches)
	RadioButton rbVouches;

	@BindView(R.id.business_scroll_view)
	NestedScrollView scrollView;

	@BindView(R.id.vouch_btn_add_an_image)
	CustomTextView btnaddimage;

	private Dialog mDialog;
	private VouchEntity mVouch;
	private List<VouchEntity> mVouches;
	private BusinessEntity mBusiness;
	int businessIds;
	public static BusinessFragment getInstance(BusinessEntity business) {

		BusinessFragment fragment = new BusinessFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_BUSINESS, business);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.business);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	protected int addView() {
		return R.layout.fragment_business;
	}

	@Override
	public void onResume(){
		super.onResume();
//		if (mBusiness != null) {
//
//			getBusiness(mBusiness.getBusinessId());
////			mAct.onBackPressed();
//		}

	}

	@Override
	protected void initView() {
		super.initView();

		llMain.setVisibility(View.GONE);
		mBusiness = (BusinessEntity) getArguments().getSerializable(BUNDLE_BUSINESS);
		if (mBusiness == null) {

			mAct.onBackPressed();
		}

		Utilities.setLayoutManager(mAct, vouchRecyclerView, true, false, false);
		Utilities.setLayoutManager(mAct, contactDetailRecyclerView, true, false, false);
		Utilities.setLayoutManager(mAct, mapsRecyclerView, true, true, false);
		Utilities.setLayoutManager(mAct, addressRecyclerView, true, false, false);
		getBusiness(mBusiness.getBusinessId());
		rgTabs.setOnCheckedChangeListener(this);
		btnaddimage.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;

			case R.id.business_vouch :

				addVouch();
				break;

			case R.id.business_call :

				String[] permissions = new String[]{Manifest.permission.CALL_PHONE};
				if (!Utilities.hasPermissions(mAct, permissions)) {

					requestPermissions(permissions, 0);
					return;
				}
				call();
				break;

			case R.id.business_enquire :

				email();
				break;

			case R.id.vouch_btn_add_an_image :

				addImageView(mBusiness, businessIds);
				break;

			case R.id.business_friend_vouch :

				rbVouches.setChecked(true);
				spnVouchFilter.setSelection(1);
				scroll();
				break;

			case R.id.business_all_vouch :

				rbVouches.setChecked(true);
				spnVouchFilter.setSelection(0);
				scroll();
				break;

			default :

				super.onClick(view);
		}
	}

	private void scroll() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				scrollView.smoothScrollTo(0,
						spnVouchFilter.getBottom() + (int) getResources().getDimension(R.dimen.business_bottom_scroll));
			}
		}, 200);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			call();
		}
	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);

		addChildFragment(VouchFragment.getInstance((VouchEntity) object), true);
	}

	public void	addImageView(BusinessEntity businessEntity, int object){

		addChildFragment(AddImageFragment.getInstance(businessEntity, object), true);
	}
	/**
	 * Get business
	 */
	private void getBusiness(int businessId) {

		mDialog = LoadingDialog.show(mAct);
		JsonObject jObject = new JsonObject();
		jObject.addProperty("business_id", businessId);
		jObject.addProperty("include_extended_public_info", true);
		jObject.addProperty("include_private_info", false);
		new DataApi(true).getInterface().getBusiness(Utilities.addUserAuth(mAct, jObject))
				.enqueue(new BaseCallBack<BusinessEntity>() {
					@Override
					public void validResponse(Call<BusinessEntity> call, Response<BusinessEntity> response) {

						businessIds = mBusiness.getBusinessId();

						if (response.body().getStat().equals(Constants.RESULT_FAIL)) {

							Utilities.dismissDialog(mDialog);
							mAct.showError(response.body().getErrMessage());
							return;
						}

						mBusiness = response.body().getResult();

						getVouches(businessIds);
					}

					@Override
					public void onFailure(Call<BusinessEntity> call, Throwable t) {

						Utilities.dismissDialog(mDialog);
					}
				});
	}

	/**
	 * Get vouches
	 * 
	 */
	private void getVouches(int businessId) {

		JsonObject jObject = new JsonObject();
		jObject.addProperty("business_id", businessId);
		new DataApi(true).getInterface().getVouchesForBusiness(Utilities.addUserAuth(mAct, jObject))
				.enqueue(new BaseCallBack<VouchEntity>() {
					@Override
					public void validResponse(Call<VouchEntity> call, Response<VouchEntity> response) {

						Utilities.dismissDialog(mDialog);

						if (response.body().getStat().equals(Constants.RESULT_FAIL)) {

							Utilities.dismissDialog(mDialog);
							mAct.showError(response.body().getErrMessage());
							return;
						}
						mVouch = response.body().getResult();
						updateUI();
					}

					@Override
					public void onFailure(Call<VouchEntity> call, Throwable t) {

						Utilities.dismissDialog(mDialog);
						updateUI();
					}
				});
	}

	/**
	 * Update ui
	 */
	private void updateUI() {

		if (mBusiness.getExtendedPublicInfo() != null) {

			if (mBusiness.getExtendedPublicInfo().getBusinessLogo() != null
					&& mBusiness.getExtendedPublicInfo().getBusinessLogo().getImageLocation() != null) {

				Utilities.displayImage(mAct, ivBusiness,
						mBusiness.getExtendedPublicInfo().getBusinessLogo().getImageLocation());
			}

			if (mBusiness.getExtendedPublicInfo().getDisplayAddress() != null) {

				txtLocation.setVisibility(View.VISIBLE);
				txtLocation.setText(mBusiness.getExtendedPublicInfo().getDisplayAddress());
			}

			if (mBusiness.getExtendedPublicInfo().getAboutLong() != null) {

				String htmlText = String.format("%s %s",
						"<style type=\"text/css\">\n" + "@font-face {\n" + "    font-family: MyFont;\n"
								+ "    src: url(\"file:///android_asset/fonts/MyFont-Regular.otf\")\n" + "}\n"
								+ "body {\n" + "    font-family: MyFont;\n" + "    font-size: 15;\n"
								+ "    color: #717171;\n" + "    text-align: justify;\n" + "}\n" + "</style>",
						mBusiness.getExtendedPublicInfo().getAboutLong());
				txtAboutShort.loadData(htmlText, "text/html", "UTF-8");
				// setText(Html.fromHtml(mBusiness
				// .getExtendedPublicInfo().getAboutLong(), null,
				// new MyTagHandler()));
			}

			// Contact detail
			if (mBusiness.getExtendedPublicInfo().getBusinessContactDetails() != null) {

				ContactDetailAndAddressAdapter contactDetailAdapter = new ContactDetailAndAddressAdapter(mAct,
						mBusiness.getExtendedPublicInfo().getBusinessContactDetails());
				contactDetailRecyclerView.setAdapter(contactDetailAdapter);
			}

			// address detail
			if (mBusiness.getExtendedPublicInfo().getBusinessAddresses() != null) {

				ContactDetailAndAddressAdapter addressAdapter = new ContactDetailAndAddressAdapter(mAct,
						mBusiness.getExtendedPublicInfo().getBusinessAddresses());
				addressRecyclerView.setAdapter(addressAdapter);
				mapsRecyclerView
						.setAdapter(new MapsAdapter(mAct, mBusiness.getExtendedPublicInfo().getBusinessAddresses()));
			}

			if (mBusiness.getExtendedPublicInfo().getBusinessImage() != null) {

				if (mBusiness.getExtendedPublicInfo().getBusinessImage().size() == 0) {

					txtNotPhoto.setVisibility(View.VISIBLE);
				} else {

					txtNotPhoto.setVisibility(View.GONE);
					ViewTreeObserver vto = llAbout.getViewTreeObserver();
					vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {

							llAbout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							int maxWidth = txtAboutShort.getMeasuredWidth();
							businessPhotoView.setPhotos(BusinessFragment.this,
									mBusiness.getExtendedPublicInfo().getBusinessImage(), maxWidth);
						}
					});
				}

			} else {

				txtNotPhoto.setVisibility(View.VISIBLE);
			}

			int numPhone = 0, numEmail = 0;

            if (mBusiness.getExtendedPublicInfo() != null
                    && mBusiness.getExtendedPublicInfo().getBusinessContactDetails() != null) {

                for (BusinessEntity business : mBusiness.getExtendedPublicInfo().getBusinessContactDetails()) {

                    if (business.getContactInfoType().equals("EMAIL")) {

                        numEmail++;
                    }

                    if (business.getContactInfoType().equals("MOBILE") || business.getContactInfoType().equals("PHONE")) {

                        numPhone++;
                    }
                }
            }


			if (numPhone > 0) {

				btnCall.setVisibility(View.VISIBLE);
			} else {

				btnCall.setVisibility(View.GONE);
			}

			if (numEmail > 0) {

				btnEmail.setVisibility(View.VISIBLE);
			} else {

				btnEmail.setVisibility(View.GONE);
			}

		}

		if (mBusiness.getSummaryPublicInfo() != null) {

			if (mBusiness.getSummaryPublicInfo().getBusinessName() != null) {

				txtBusinessName.setText(mBusiness.getSummaryPublicInfo().getBusinessName());
			}

			if (mBusiness.getSummaryPublicInfo().getBusinessServices() != null) {

				String businessDes = "";
				for (BusinessEntity business : mBusiness.getSummaryPublicInfo().getBusinessServices()) {

					if (businessDes.length() > 0) {

						businessDes = String.format("%s, %s", businessDes, business.getServiceName());
					} else {

						businessDes = business.getServiceName();
					}
				}

				txtBusinessDes.setText(businessDes);
			}

			// Show data for vouch
			List<BusinessEntity> listSpinnerService = new ArrayList<>();

			if (mVouch != null) {

				txtVouchesCount.setText(String.format("%s", mVouch.getTotalVouches()));

				mVouches = mVouch.getVouches();

				listSpinnerService.add(
						new BusinessEntity(0, "", -9999, String.format("All vouches (%s)", mVouch.getTotalVouches())));

				if (mVouch.getTotalFriendVouches() > 0) {

					txtFriendVouchesCount.setText(String.format("%s", mVouch.getTotalFriendVouches()));
					llFriendVouch.setVisibility(View.VISIBLE);

					listSpinnerService.add(new BusinessEntity(0, "", -99999,
							String.format("Vouches by friends (%s)", mVouch.getTotalFriendVouches())));
				} else {

					llFriendVouch.setVisibility(View.GONE);
				}

				if (mVouch.getBusinessServiceSummaries() != null) {

					if (mVouch.getTotalFriendVouches() > 0 || mVouch.getBusinessServiceSummaries().size() > 1) {

						for (BusinessEntity business : mVouch.getBusinessServiceSummaries()) {

							listSpinnerService.add(new BusinessEntity(0, "", business.getServiceId(),
									String.format("Vouches for %s (%s)", business.getServiceName(),
											business.getTotalVouchesForService())));
						}
					}

				}
				ListSpinnerServiceAdapter spnServiceAdapter = new ListSpinnerServiceAdapter(mAct, listSpinnerService);
				spnVouchFilter.setAdapter(spnServiceAdapter);

				spnVouchFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

						BusinessEntity business = (BusinessEntity) adapterView.getAdapter().getItem(position);

						BusinessVouchesAdapter vouchesAdapter;
						if (business.getServiceId() == -9999) {

							vouchesAdapter = new BusinessVouchesAdapter(mVouches, BusinessFragment.this);

						} else if (business.getServiceId() == -99999) {

							List<VouchEntity> FriendVouches = new ArrayList<>();

							if (mVouch.getBusinessServiceSummaries() != null) {

								for (BusinessEntity busi : mVouch.getBusinessServiceSummaries()) {

									if (busi.getFriendVouchesForService() > 0) {

										for (VouchEntity vouch : mVouch.getVouches()) {

											if (vouch.isConnected()) {

												for (BusinessEntity bus : vouch.getBusinessServices()) {

													if (busi.getServiceId() == bus.getServiceId()
															&& !FriendVouches.contains(vouch)) {

														FriendVouches.add(vouch);
														break;
													}
												}
											}
										}
									}
								}
							}
							vouchesAdapter = new BusinessVouchesAdapter(FriendVouches, BusinessFragment.this);
						} else {

							List<VouchEntity> vouches = new ArrayList<>();
							for (VouchEntity vouch : mVouch.getVouches()) {

								for (BusinessEntity bus : vouch.getBusinessServices()) {

									if (business.getServiceId() == bus.getServiceId()) {

										vouches.add(vouch);
										break;
									}
								}
							}
							vouchesAdapter = new BusinessVouchesAdapter(vouches, BusinessFragment.this);
						}

						vouchRecyclerView.setAdapter(vouchesAdapter);
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {

					}
				});
			}

		}

		btnAddVouch.setOnClickListener(this);
		btnCall.setOnClickListener(this);
		btnEmail.setOnClickListener(this);
		llFriendVouch.setOnClickListener(this);
		llAllVouch.setOnClickListener(this);
		llMain.setVisibility(View.VISIBLE);
	}

	private void addVouch() {

		mDialog = LoadingDialog.show(mAct);
		ApiUtility.getUser(mAct, new BaseCallBack<UserEntity>() {
			@Override
			public void validResponse(Call<UserEntity> call, Response<UserEntity> response) {

				Utilities.dismissDialog(mDialog);
				if (response.body().getStat().equals(Constants.RESULT_FAIL)) {

					mAct.showError(response.body().getErrMessage());
					return;
				}

				final UserEntity user = response.body().getResult();

				if (user.isPhoneVerified()) {

					addChildFragment(CreateVouchFragment.getInstance(null, mBusiness), true);
					return;
				}

				Utilities.showAlertDialog(mAct, "", mAct.getString(R.string.you_should_verify_mobile),
						mAct.getString(R.string.verify), mAct.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								if (user.getMobile() != null && user.getMobile().length() > 0) {

									addChildFragment(PhoneVerifyFragment.getInstance(user.getMobile(), false), true);
								}
								else{
									addChildFragment(EnterPhoneNumberFragment.getInstance(user, false), true);
								}
								// goto add mobile
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

	private void call() {

		sendNotifyEventRequestWithType("click on call button", mBusiness.getBusinessId());
		if (mBusiness.getExtendedPublicInfo() != null
				&& mBusiness.getExtendedPublicInfo().getBusinessContactDetails() != null) {

			List<String> arrPhone = new ArrayList<>();
			String strPhone = "";
			for (BusinessEntity business : mBusiness.getExtendedPublicInfo().getBusinessContactDetails()) {

				if (business.getContactInfoType().equals("MOBILE") || business.getContactInfoType().equals("PHONE")) {

					strPhone = business.getContactInfo();
					arrPhone.add(strPhone);
				}
			}

			if (arrPhone.size() == 1) {

				Utilities.startCall(mAct, String.format("tel:%s", strPhone.replaceAll("[^\\d.]", "")));
			} else {

				Utilities.showAlertDialogListDataCall(mAct, getString(R.string.choose_mobile_phone), arrPhone,
						getString(R.string.cancel), "", null, null, false);
			}
		}
	}

	private void email() {

		sendNotifyEventRequestWithType("click on enquire button", mBusiness.getBusinessId());
		if (mBusiness != null && mBusiness.getExtendedPublicInfo() != null
				&& mBusiness.getExtendedPublicInfo().getBusinessContactDetails() != null) {

			String subject = String.format("%s %s", getString(R.string.vouchify_enquiry_for),
					mBusiness.getSummaryPublicInfo().getBusinessName());

			List<String> arrEmail = new ArrayList<>();
			String strEmail = "";
			for (BusinessEntity business : mBusiness.getExtendedPublicInfo().getBusinessContactDetails()) {

				if (business.getContactInfoType().equals("EMAIL")) {

					strEmail = business.getContactInfo();
					arrEmail.add(strEmail);
				}
			}

			if (arrEmail.size() == 1) {

				Utilities.startSendMail(mAct, strEmail, subject);
			} else {

				startDialogChooseEmail(arrEmail, subject);
			}
		}
	}

	private void startDialogChooseEmail(List<String> arrEmail, final String subject) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mAct);
		dialogBuilder.setCancelable(false);

		final CharSequence[] Items = arrEmail.toArray(new CharSequence[arrEmail.size()]);
		dialogBuilder.setItems(Items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {

				Utilities.startSendMail(mAct, Items[i].toString(), subject);
				dialog.dismiss();
			}
		});

		dialogBuilder.setTitle(getString(R.string.choose_email_address));

		dialogBuilder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int i) {

		switch (i) {

			case R.id.business_cb_about :

				llPhotos.setVisibility(View.GONE);
				llVouches.setVisibility(View.GONE);
				llAbout.setVisibility(View.VISIBLE);
				break;

			case R.id.business_cb_vouches :

				llAbout.setVisibility(View.GONE);
				llPhotos.setVisibility(View.GONE);
				llVouches.setVisibility(View.VISIBLE);
				break;

			default :

				llAbout.setVisibility(View.GONE);
				llVouches.setVisibility(View.GONE);
				llPhotos.setVisibility(View.VISIBLE);
		}
	}

	private void sendNotifyEventRequestWithType(String strType, int businessId) {

		JsonObject jObject = new JsonObject();
		jObject.addProperty("business_id", businessId);
		jObject.addProperty("type", strType);
		new DataApi(true).getInterface().notifyEvent(Utilities.addUserAuth(mAct, jObject))
				.enqueue(new BaseCallBack<BaseEntity>() {
					@Override
					public void validResponse(Call<BaseEntity> call, Response<BaseEntity> response) {

					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

					}
				});

	}
}
