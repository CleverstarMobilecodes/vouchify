package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.FriendDetailVouchesAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.api.UserApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.ImageViewWithText;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/8/16.
 */
public class FriendDetailFragment extends BaseFragment {

	private static final String BUNDLE_USER = "bundle_user";

	@BindView(R.id.friend_detail_imv_profile)
	ImageViewWithText imvAvatar;

	@BindView(R.id.friend_detail_txt_name)
	TextView txtName;

	@BindView(R.id.friend_detail_txt_joined)
	TextView txtJoined;

	@BindView(R.id.friend_detail_txt_num_vouches)
	TextView txtNumVouches;

	@BindView(R.id.friend_detail_txt_your_activity)
	TextView txtYourActivity;

	@BindView(R.id.friend_detail_voucher_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.friend_detail_voucher_txt_empty)
	TextView txtEmpty;

	private UserEntity mFriend;
	private Dialog mDialog;
	private String strFriendId;

	public static FriendDetailFragment getInstance(UserEntity user) {

		FriendDetailFragment fragment = new FriendDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_USER, user);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_friend_detail;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.friend);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		super.initView();

		mFriend = (UserEntity) getArguments().getSerializable(BUNDLE_USER);
		if (mFriend == null) {

			backFragment(false);
			return;
		}

		txtNumVouches.setOnClickListener(this);
		recyclerView.setNestedScrollingEnabled(false);
		Utilities.setLayoutManager(mAct, recyclerView, true, true, false);

		strFriendId = mFriend.getUserId();// "fb_387";//mFriend.getUserId()
		getUser();
		// getVouches();
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;

			case R.id.friend_detail_txt_num_vouches :

				addChildFragment(MyVouchesFragment.getInstance(mFriend), true);
				break;
			default :

				super.onClick(view);
		}
	}

	private void getUser() {

		mDialog = LoadingDialog.show(mAct);
		if (strFriendId.contains("fb_")) {

			getVouches();
		} else {

			JsonObject object = new JsonObject();
			object.addProperty("user_id", strFriendId);
			new UserApi(true).getInterface().getUser(object)
					.enqueue(new BaseCallBack<UserEntity>() {
						@Override
						public void validResponse(Call<UserEntity> call,
								Response<UserEntity> response) {

							UserEntity user = response.body();
							if (user.getStat().equals(Constants.RESULT_FAIL)) {

								Utilities.dismissDialog(mDialog);
								updateUI(mFriend, null);
								mAct.showError(user.getErrMessage());
								// backFragment(false);
								return;
							}

							mFriend = user.getResult();
							getVouches();

						}

						@Override
						public void onFailure(Call<UserEntity> call, Throwable t) {

							updateUI(mFriend, null);
							Utilities.dismissDialog(mDialog);
						}
					});
		}

	}

	private void getVouches() {

		JsonObject object = new JsonObject();
		object.addProperty("user_id", strFriendId);
		new DataApi(true).getInterface().getVouches(object)
				.enqueue(new BaseCallBack<VouchEntity>() {
					@Override
					public void validResponse(Call<VouchEntity> call,
							Response<VouchEntity> response) {

						Utilities.dismissDialog(mDialog);
						try {

							VouchEntity vouch = response.body();
							if (vouch.getStat().equals(Constants.RESULT_FAIL)) {

								updateUI(mFriend, null);
								mAct.showError(vouch.getErrMessage());
								return;
							}

							updateUI(mFriend, vouch.getResult());
						} catch (Exception ignore) {

						}
					}

					@Override
					public void onFailure(Call<VouchEntity> call, Throwable t) {

						updateUI(mFriend, null);
						Utilities.dismissDialog(mDialog);
					}
				});
	}

	@Override
	public void onItemClick(Object object, int pos) {
		super.onItemClick(object, pos);

		addChildFragment(VouchFragment.getInstance((VouchEntity) object), true);
	}

	private void updateUI(UserEntity user, VouchEntity vouchEntity) {

		String fullName;
		String firstName, lastName;
		if (user.getLastName() != null) {

			fullName = String.format("%s %s", user.getFirstName(),
					user.getLastName());
			firstName = user.getFirstName();
			lastName = user.getLastName();
		} else if (user.getLastNameInitial() != null) {

			fullName = String.format("%s %s", user.getFirstName(),
					user.getLastNameInitial());
			firstName = user.getFirstName();
			lastName = user.getLastNameInitial();
		} else {

			fullName = String.format("%s", user.getFirstName());
			firstName = user.getFirstName();
			lastName = user.getFirstName();
		}

		txtName.setText(fullName);
		imvAvatar.displayImage(user.getProfileUrl(), firstName, lastName);
		if (user.getDateJoined() != null && !user.getDateJoined().equals("")
				&& !user.getDateJoined().isEmpty()) {

			txtJoined.setText(String.format("Joined %s", user.getDateJoined()));
		} else {

			txtJoined.setVisibility(View.GONE);
		}

		txtYourActivity.setText(String.format("%s \'s activity",
				user.getFirstName()));

		if (vouchEntity != null) {

			if (vouchEntity.getTotalVouches() == 0) {

				txtNumVouches.setEnabled(false);
				recyclerView.setVisibility(View.INVISIBLE);
				txtEmpty.setVisibility(View.VISIBLE);
			}

			if (vouchEntity.getTotalVouches() == 1) {

				txtNumVouches.setText(String.format("%s Vouch",
						vouchEntity.getTotalVouches()));
			} else {

				txtNumVouches.setText(String.format("%s Vouches",
						vouchEntity.getTotalVouches()));
			}

			if (vouchEntity.getTotalVouches() > 0) {

				FriendDetailVouchesAdapter adapter = new FriendDetailVouchesAdapter(
						mAct, FriendDetailFragment.this,
						vouchEntity.getVouches());
				recyclerView.setAdapter(adapter);
			}
		} else {

			txtNumVouches.setEnabled(false);
			recyclerView.setVisibility(View.INVISIBLE);
			txtEmpty.setVisibility(View.VISIBLE);
			txtNumVouches.setText(String.format("%s Vouches", 0));
		}

	}
}
