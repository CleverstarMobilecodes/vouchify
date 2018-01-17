package com.vouchify.vouchify.fragment;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.MyVouchesAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/22/16.
 */
public class MyVouchesFragment extends BaseFragment {

	private static final String BUNDLE_USER = "bundle_user";

	@BindView(R.id.my_vouches_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.my_vouches_txt_empty)
	TextView txtEmpty;

	private UserEntity mUser;
	public static MyVouchesFragment getInstance(UserEntity user) {

		MyVouchesFragment fragment = new MyVouchesFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_USER, user);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_my_vouches;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.my_vouches);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	// @Override
	// protected void onBackStackChanged() {
	// super.onBackStackChanged();
	//
	// int vouchStatus = CustomPreferences.getPreferences(
	// Constants.PREF_VOUCH_UPDATE, Constants.VOUCH_NO_UPDATE);
	// if (vouchStatus == Constants.VOUCH_DELETED) {
	//
	// CustomPreferences.setPreferences(Constants.PREF_VOUCH_UPDATE,
	// Constants.VOUCH_NO_UPDATE);
	// getVouches();
	// }
	// }

	@Override
	protected void initView() {
		super.initView();

		mUser = (UserEntity) getArguments().getSerializable(BUNDLE_USER);
		if (mUser == null) {

			mAct.onBackPressed();
			return;
		}

		LocalBroadcastManager.getInstance(mAct).registerReceiver(
				mVouchUpdateReceiver,
				new IntentFilter(Constants.INTENT_VOUCH_UPDATE));
		Utilities.setLayoutManager(mAct, recyclerView, true, true, true);
		getVouches();
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
			default :

				super.onClick(view);
		}
	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);

		addChildFragment(VouchFragment.getInstance((VouchEntity) object), true);
	}

	/**
	 * Get vouches
	 */
	private void getVouches() {

		final Dialog dialog = LoadingDialog.show(mAct);
		JsonObject jObject = new JsonObject();
		jObject.addProperty(Constants.KEY_USER_ID, mUser.getUserId());
		jObject.addProperty(Constants.KEY_SESSION_TOKEN, mUser.getSessionTkn());
		new DataApi(true).getInterface().getVouches(jObject)
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

						vouch = vouch.getResult();
						if (vouch.getTotalVouches() > 0) {
							MyVouchesAdapter adapter = new MyVouchesAdapter(
									mAct, MyVouchesFragment.this, mUser, vouch
											.getVouches());
							recyclerView.setAdapter(adapter);
							return;
						}

						recyclerView.setVisibility(View.INVISIBLE);
						txtEmpty.setVisibility(View.VISIBLE);
					}

					@Override
					public void onFailure(Call<VouchEntity> call, Throwable t) {

						Utilities.dismissDialog(dialog);
					}
				});
	}

	private BroadcastReceiver mVouchUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			int vouchStatus = intent.getIntExtra(Constants.PREF_VOUCH_UPDATE,
					Constants.VOUCH_NO_UPDATE);
			if (vouchStatus == Constants.VOUCH_DELETED) {

				getVouches();
			}
		}
	};
}
