package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;
import com.vouchify.vouchify.view.VouchView;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/8/16.
 */
public class VouchFragment extends BaseFragment {

	private static final String BUNDLE_VOUCH = "bundle_vouch";

	@BindView(R.id.vouch_imv_business)
	ImageView imvBusiness;

	@BindView(R.id.vouch_txt_business_name)
	TextView txtBusinessName;

	@BindView(R.id.vouch_txt_business_des)
	TextView txtServices;

	@BindView(R.id.vouch_txt_business_location)
	TextView txtLocation;

	@BindView(R.id.vouch_view)
	VouchView vouchView;

	@BindView(R.id.vouch_rl_business)
	RelativeLayout rlBusiness;

	private VouchEntity mVouch;
	public static VouchFragment getInstance(VouchEntity vouch) {

		VouchFragment fragment = new VouchFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_VOUCH, vouch);
		fragment.setArguments(bundle);
		return fragment;
	}

	public void setToolBar() {

		assert btnMenu != null;
		assert txtTittle != null;
		assert txtRight != null;
		assert txtLeft != null;

		txtTittle.setText(R.string.vouch);

		txtLeft.setVisibility(View.GONE);
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);

	}

	@Override
	protected int addView() {
		return R.layout.fragment_vouch;
	}

	@Override
	protected void initView() {
		super.initView();

		mVouch = (VouchEntity) getArguments().getSerializable(BUNDLE_VOUCH);
		if (mVouch == null) {

			backFragment(false);
			return;
		}

		getVouch(mVouch.getVouchId());
		LocalBroadcastManager.getInstance(mAct).registerReceiver(
				mVouchUpdateReceiver,
				new IntentFilter(Constants.INTENT_VOUCH_UPDATE));
		rlBusiness.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(mAct).unregisterReceiver(
				mVouchUpdateReceiver);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;
			case R.id.toolbar_txt_right :

				addChildFragment(
						CreateVouchFragment.getInstance(mVouch.getVouch(), null),
						true);
				break;
			case R.id.vouch_rl_business :

				addChildFragment(
						BusinessFragment.getInstance(mVouch.getBusiness()),
						true);
			default :

				super.onClick(view);
		}
	}

	/**
	 * Get vouch
	 */
	private void getVouch(String vouchId) {

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject jObject = new JsonObject();
		jObject.addProperty(Constants.KEY_USER_ID, mAct.getUser().getUserId());
		jObject.addProperty(Constants.KEY_SESSION_TOKEN, mAct.getUser()
				.getSessionTkn());
		jObject.addProperty("vouch_id", vouchId);
		new DataApi(false).getInterface().getVouch(jObject)
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

		if (mVouch == null) {

			backFragment(false);
			return;
		}

		setToolBar();

		// BusinessEntity business = mVouch.get
		final VouchEntity vouch = mVouch.getVouch();
		BusinessEntity business = mVouch.getBusiness();

		if (vouch.isEditable()) {

			assert txtRight != null;
			txtRight.setVisibility(View.VISIBLE);
			txtRight.setText(R.string.edit);
			txtRight.setOnClickListener(this);
		}

		vouchView.setVouch(mVouch, true, false);
		txtBusinessName.setText(business.getBusinessName());
		Utilities.displayImage(mAct, imvBusiness, business.getLogoLocation());
		String strServices = "";
		if (business.getBusinessServices() != null) {

			String sep = "";
			for (BusinessEntity busi : business.getBusinessServices()) {

				strServices += sep + busi.getServiceName();
				sep = ", ";
			}
		}

		txtServices.setText(strServices);
		if (business.getDisplayAddress() != null
				&& !business.getDisplayAddress().isEmpty()) {

			txtLocation.setVisibility(View.VISIBLE);
			txtLocation.setText(business.getDisplayAddress());
		}
	}

	private BroadcastReceiver mVouchUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			int vouchStatus = intent.getIntExtra(Constants.PREF_VOUCH_UPDATE,
					Constants.VOUCH_NO_UPDATE);
			if (vouchStatus == Constants.VOUCH_DELETED) {

				mAct.onBackPressed();
				return;
			}

			if (vouchStatus == Constants.VOUCH_UPDATED) {

				getVouch(mVouch.getVouch().getVouchId());
			}
		}
	};
}
