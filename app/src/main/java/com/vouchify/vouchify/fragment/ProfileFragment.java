package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.activity.MainActivity;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.ApiUtility;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.ImageViewWithText;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/18/16.
 */
public class ProfileFragment extends BaseFragment {

	@BindView(R.id.profile_txt_email)
	TextView txtEmail;

	@BindView(R.id.profile_txt_name)
	TextView txtName;

	@BindView(R.id.profile_txt_phone)
	TextView txtPhone;

	@BindView(R.id.profile_btn_phone_verify)
	TextView btnPhoneVerify;

	@BindView(R.id.profile_imv_avatar)
	ImageViewWithText imvAvatar;

	@BindView(R.id.profile_btn_voucher)
	TextView btnVouches;

	private Dialog mDialog;
	private boolean isLoaded;
	private UserEntity mUser;
	public static ProfileFragment getInstance() {

		return new ProfileFragment();
	}

	@Override
	protected int addView() {

		return R.layout.fragment_profile;
	}

	@Override
	protected void initView() {
		super.initView();

		LocalBroadcastManager.getInstance(mAct).registerReceiver(
				mUpdateReceiver,
				new IntentFilter(Constants.INTENT_SHOULD_UPDATE_PROFILE_UI));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(mAct).unregisterReceiver(
				mUpdateReceiver);
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

				mUser = user.getResult();
				updateUI(mUser);
				Utilities.dismissDialog(mDialog);
			}

			@Override
			public void onFailure(Call<UserEntity> call, Throwable t) {

				Utilities.dismissDialog(mDialog);
			}
		});
	}

	@Override
	public void setToolBar() {
		super.setToolBar();
		assert txtTittle != null;
		txtTittle.setText(R.string.personal_details);

		assert txtRight != null;
		txtRight.setVisibility(View.VISIBLE);
		txtRight.setText(R.string.edit);
		txtRight.setOnClickListener(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && !isLoaded) {

            mDialog = LoadingDialog.show(mAct);
			isLoaded = true;
			getUser();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			case R.id.profile_btn_phone_verify :

				addChildFragment(PhoneVerifyFragment.getInstance(txtPhone
						.getText().toString(), false), true);
				break;
			case R.id.toolbar_txt_right :

				addChildFragment(EditProfileFragment.getInstance(mUser), true);
				break;

			case R.id.profile_btn_voucher :

				addChildFragment(MyVouchesFragment.getInstance(mUser), true);
				break;
			default :

				super.onClick(view);
		}
	}

	private void updateUI(UserEntity user) {

		String lastName = user.getLastName();
		String firstName = user.getFirstName();
		((MainActivity) mAct).setMenuImage(user);

		txtEmail.setText(user.getEmail());
		txtName.setText(String.format("%s %s", firstName, lastName));
		imvAvatar.displayImage(user.getProfileUrl(), firstName, lastName);

		if (user.getMobile() == null || user.getMobile().isEmpty()) {

			txtPhone.setText("-");
			btnPhoneVerify.setVisibility(View.GONE);
		} else {

			txtPhone.setText(user.getMobile());
			if (user.isPhoneVerified()) {

				btnPhoneVerify.setVisibility(View.GONE);
			} else {

				btnPhoneVerify.setVisibility(View.VISIBLE);
			}
		}

		btnVouches.setOnClickListener(this);
		btnPhoneVerify.setOnClickListener(this);
	}

	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					mDialog = LoadingDialog.show(mAct);
					getUser();
				}
			}, 200);
		}
	};
}
