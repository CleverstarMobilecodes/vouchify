package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/9/16.
 */
@SuppressWarnings("SuspiciousMethodCalls")
public class CreateVouchFragment extends BaseFragment {

	private static final String BUNDLE_VOUCH = "bundle_vouch";
    private static final String BUNDLE_BUSINESS = "bundle_business";

	@BindView(R.id.edit_vouch_text)
	EditText edtVouchDescription;

	@BindView(R.id.edit_vouch_btn_delete)
	TextView btnDelete;

	@BindView(R.id.edit_vouch_btn_continue)
	TextView btnContinue;

//	@BindView(R.id.fragment_container)
//	RelativeLayout fragment_container;
//
//	@BindView(R.id.vouch_pan_keyboard)
//	LinearLayout vouch_pan_keyboard;
//	@BindView(R.id.scrollView)
//	LinearLayout scrollView;

    @BindView(R.id.edit_vouch_btn_cancel)
    TextView btnCancel;

	@BindView(R.id.edit_vouch_ll_service)
	LinearLayout llService;

	private boolean isEditing;
	private VouchEntity mVouch;
    private BusinessEntity mBusiness;
	private List<BusinessEntity> mCheckedServices;
    private String mVouchId;

	public static CreateVouchFragment getInstance(VouchEntity vouch, BusinessEntity business) {

		CreateVouchFragment fragment = new CreateVouchFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_VOUCH, vouch);
        bundle.putSerializable(BUNDLE_BUSINESS, business);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		if (isEditing) {

			assert txtRight != null;
			txtTittle.setText(R.string.edit_vouch);
			txtRight.setVisibility(View.VISIBLE);
			txtRight.setText(R.string.done);
			txtRight.setOnClickListener(this);

			assert txtLeft != null;
			txtLeft.setVisibility(View.VISIBLE);
			txtLeft.setText(R.string.cancel);
			txtLeft.setOnClickListener(this);
		} else {

			txtTittle.setText(R.string.create_vouch);

			assert btnMenu != null;
			btnMenu.setImageResource(R.drawable.ic_action_back);
			btnMenu.setVisibility(View.VISIBLE);
			btnMenu.setOnClickListener(this);
		}
	}

	@Override
	protected int addView() {

		return R.layout.fragment_create_vouch;
	}

	@Override
	protected void initView() {
		super.initView();

		mVouch = (VouchEntity) getArguments().getSerializable(BUNDLE_VOUCH);
        mBusiness = (BusinessEntity) getArguments().getSerializable(BUNDLE_BUSINESS);
		mCheckedServices = new ArrayList<>();
		edtVouchDescription.setOnClickListener(this);
//		int height = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
//		vouch_pan_keyboard.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
//		scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height - 300));
		if (mVouch == null) {

			isEditing = false;
            btnContinue.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnContinue.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            updateUI();
			// Create vouch
			return;
		}

		// Edit vouch
		isEditing = true;
		getVouch(mVouch.getVouchId());
		btnDelete.setVisibility(View.VISIBLE);
		btnDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
//			case R.id.edit_vouch_text:
////				vouch_pan_keyboard.setBottom(vouch_pan_keyboard.getBottom()-);
//				break;
            case R.id.toolbar_btn_left:
				Utilities.dismissKeyboard(mAct, view);
				onBackPressed();
			case R.id.toolbar_txt_left :
//				if(isEditing){
					Utilities.dismissKeyboard(mAct, view);
					onBackPressed();
//				}
            case R.id.edit_vouch_btn_cancel :

				Utilities.dismissKeyboard(mAct, view);
				mAct.onBackPressed();
				break;

			case R.id.toolbar_txt_right :

				Utilities.dismissKeyboard(mAct, view);
				updateVouch();
				break;

			case R.id.edit_vouch_btn_delete :

				Utilities.showAlertDialog(mAct, "",
						mAct.getString(R.string.delete_vouch_alert),
						mAct.getString(R.string.yes),
						mAct.getString(R.string.no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {

								deleteVouch();
							}
						}, null, true);

				break;

            case R.id.edit_vouch_btn_continue :

                Utilities.dismissKeyboard(mAct, view);
                createVouch();

                break;
			default :

				super.onClick(view);
		}
	}

	/**
	 * Delete vouch
	 */
	private void deleteVouch() {

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject object = new JsonObject();
		object.addProperty(Constants.KEY_USER_ID, mAct.getUser().getUserId());
		object.addProperty(Constants.KEY_SESSION_TOKEN, mAct.getUser()
				.getSessionTkn());
		object.addProperty("vouch_id", mVouch.getVouch().getVouchId());
		new DataApi(true).getInterface().deleteVouch(object)
				.enqueue(new BaseCallBack<BaseEntity>() {
					@Override
					public void validResponse(Call<BaseEntity> call,
							Response<BaseEntity> response) {

						Utilities.dismissDialog(dialog);
						BaseEntity base = response.body();
						if (base.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(base.getErrMessage());
							return;
						}

						Utilities.showAlertDialog(mAct, "",
								mAct.getString(R.string.vouch_was_deleted),
								mAct.getString(R.string.ok), "",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {

										mAct.onBackPressed();
										finishUpdate(Constants.VOUCH_DELETED);
									}
								}, null, false);

					}

					@Override
					public void onFailure(Call<BaseEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	/**
	 * Update vouch
	 */
	private void updateVouch() {

		if (mCheckedServices.size() == 0) {

			Utilities.showAlertDialog(mAct, "",
					mAct.getString(R.string.edit_vouch_no_services),
					mAct.getString(R.string.yes), mAct.getString(R.string.no),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int i) {

							deleteVouch();
						}
					}, null, false);

			return;
		}

		String strVouchDes = edtVouchDescription.getText().toString().trim();
		if (strVouchDes.length() < 10) {

			Utilities
					.showAlertDialog(
							mAct,
							"",
							mAct.getString(R.string.edit_vouch_alert_not_input_correct),
							mAct.getString(R.string.ok), "", null, null, false);
			return;
		}

		boolean isEdited = !strVouchDes.equals(mVouch.getVouch()
				.getVouchDescription());
		if (isEdited || isServicesChanged()) {

			final Dialog dialog = LoadingDialog.show(mAct);
			JsonObject object = new JsonObject();
			object.addProperty("vouch_description", strVouchDes);
			object.addProperty("business_id", mVouch.getBusiness()
					.getBusinessId());
			object.add("business_services",
					new Gson().toJsonTree(mCheckedServices));
			object.addProperty("vouch_id", mVouch.getVouch().getVouchId());

			new DataApi(true).getInterface()
					.writeVouch(Utilities.addUserAuth(mAct, object))
					.enqueue(new BaseCallBack<VouchEntity>() {
						@Override
						public void validResponse(Call<VouchEntity> call,
								Response<VouchEntity> response) {

							Utilities.dismissDialog(dialog);
							BaseEntity base = response.body();
							if (base.getStat().equals(Constants.RESULT_FAIL)) {

								mAct.showError(base.getErrMessage());
								return;
							}

							mAct.onBackPressed();
							finishUpdate(Constants.VOUCH_UPDATED);
						}

						@Override
						public void onFailure(Call<VouchEntity> call, Throwable t) {

							Utilities.dismissDialog(dialog);
						}
					});

			return;
		}

		mAct.onBackPressed();
	}


    /**
     * Update vouch
     */
    private void createVouch() {

        if (mCheckedServices.size() == 0) {

            Utilities.showAlertDialog(mAct, "",
                    mAct.getString(R.string.edit_vouch_no_services),
                    mAct.getString(R.string.ok), "",
                    null, null, false);

            return;
        }

        String strVouchDes = edtVouchDescription.getText().toString().trim();
        if (strVouchDes.length() < 10) {

            Utilities
                    .showAlertDialog(
                            mAct,
                            "",
                            mAct.getString(R.string.edit_vouch_alert_not_input_correct),
                            mAct.getString(R.string.ok), "", null, null, false);
            return;
        }

        final Dialog dialog = LoadingDialog.show(mAct);
        JsonObject object = new JsonObject();
        object.addProperty("vouch_description", strVouchDes);
        object.addProperty("business_id", mBusiness.getSummaryPublicInfo().getBusinessId());
        object.add("business_services",
                new Gson().toJsonTree(mCheckedServices));

        if (mVouchId != null) {

            object.addProperty("vouch_id", mVouchId);
        }

        new DataApi(true).getInterface()
                .writeVouch(Utilities.addUserAuth(mAct, object))
                .enqueue(new BaseCallBack<VouchEntity>() {
                    @Override
                    public void validResponse(Call<VouchEntity> call,
                                              Response<VouchEntity> response) {

                        Utilities.dismissDialog(dialog);
                        VouchEntity base = response.body();
                        if (base.getStat().equals(Constants.RESULT_FAIL)) {

                            mAct.showError(base.getErrMessage());
                            return;
                        }

                        VouchEntity vouch = base.getResult();
                        mVouchId = vouch.getVouchId();

                        addChildFragment(VouchAddedFragment.getInstance(vouch), false);
                    }

                    @Override
                    public void onFailure(Call<VouchEntity> call, Throwable t) {

                        Utilities.dismissDialog(dialog);
                    }
                });

    }

	/**
	 * Get vouch
	 */
	private void getVouch(String vouchId) {

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject jObject = new JsonObject();
		jObject.addProperty("vouch_id", vouchId);
		new DataApi(true).getInterface()
				.getVouch(Utilities.addUserAuth(mAct, jObject))
				.enqueue(new BaseCallBack<VouchEntity>() {
					@Override
					public void validResponse(Call<VouchEntity> call,
							Response<VouchEntity> response) {

						Utilities.dismissDialog(dialog);
						VouchEntity vouch = response.body();
						if (vouch.getStat().equals(Constants.RESULT_FAIL)) {

							mAct.showError(vouch.getErrMessage());
							return;
						}

						mVouch = vouch.getResult();
						updateUI();
					}

					@Override
					public void onFailure(Call<VouchEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	/**
	 * Update UI
	 */
	private void updateUI() {

		if (mVouch == null && mBusiness == null) {

			backFragment(false);
			return;
		}

        LayoutInflater inflater = LayoutInflater.from(mAct);
        BusinessEntity business;
        if (mVouch != null) {

            VouchEntity vouch = mVouch.getVouch();
            business = mVouch.getBusiness();
            if (business.getBusinessServices().size() > 1) {

                llService.setVisibility(View.VISIBLE);
                for (BusinessEntity busi : business.getBusinessServices()) {

                    final AppCompatCheckBox cbx = (AppCompatCheckBox) inflater
                            .inflate(R.layout.item_edit_vouch_checkbox, null);
                    cbx.setText(busi.getServiceName());
                    cbx.setTag(busi);

                    if (vouch.getBusinessServices() != null) {

                        for (BusinessEntity vouchBus : vouch.getBusinessServices()) {

                            if (busi.getServiceId() == vouchBus.getServiceId()) {

                                mCheckedServices.add(busi);
                                cbx.setChecked(true);
                                break;
                            }
                        }
                    }

                    cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton,
                                                     boolean b) {

                            if (b) {

                                mCheckedServices.add((BusinessEntity) cbx.getTag());
                                return;
                            }

                            mCheckedServices.remove(cbx.getTag());
                        }
                    });

                    llService.addView(cbx);
                }
            } else {

                mCheckedServices.add(business.getBusinessServices().get(0));
            }

            edtVouchDescription.setText(vouch.getVouchDescription());
        }
        else {


            if (mBusiness.getSummaryPublicInfo().getBusinessServices().size() > 1) {

                llService.setVisibility(View.VISIBLE);
                for (BusinessEntity busi : mBusiness.getSummaryPublicInfo().getBusinessServices()) {

                    final AppCompatCheckBox cbx = (AppCompatCheckBox) inflater
                            .inflate(R.layout.item_edit_vouch_checkbox, null);
                    cbx.setText(busi.getServiceName());
                    cbx.setTag(busi);


                    cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton,
                                                     boolean b) {

                            if (b) {

                                mCheckedServices.add((BusinessEntity) cbx.getTag());
                                return;
                            }

                            mCheckedServices.remove(cbx.getTag());
                        }
                    });

                    llService.addView(cbx);
                }
            } else {

                mCheckedServices.add(mBusiness.getSummaryPublicInfo().getBusinessServices().get(0));
            }
        }
		// UserEntity user = mVouch.getUser();



	}

	/**
	 * Check is services changed
	 */
	private boolean isServicesChanged() {

		VouchEntity vouch = mVouch.getVouch();
		if (vouch.getBusinessServices().size() != mCheckedServices.size()) {

			return true;
		}

		for (BusinessEntity bus1 : vouch.getBusinessServices()) {

			boolean isFound = false;
			for (BusinessEntity bus2 : mCheckedServices) {

				if (bus1.getServiceId() == bus2.getServiceId()) {

					isFound = true;
					break;
				}
			}

			if (!isFound) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Finish update
	 */
	private void finishUpdate(int state) {

		Intent intent = new Intent(Constants.INTENT_VOUCH_UPDATE);
		intent.putExtra(Constants.PREF_VOUCH_UPDATE, state);
		LocalBroadcastManager.getInstance(mAct).sendBroadcast(intent);
	}
}
