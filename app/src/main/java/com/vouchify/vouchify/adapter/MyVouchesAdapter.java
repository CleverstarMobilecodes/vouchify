package com.vouchify.vouchify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/8/16.
 */
public class MyVouchesAdapter
		extends
			RecyclerView.Adapter<MyVouchesAdapter.ViewHolder> {

	private Context mCtx;
	private UserEntity mUser;
	private List<VouchEntity> mVouches;
	private OnItemClickListener mListener;

	public MyVouchesAdapter(Context ctx, OnItemClickListener listener,
			UserEntity user, List<VouchEntity> vouches) {

		this.mCtx = ctx;
		this.mUser = user;
		this.mVouches = vouches;
		this.mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_my_vouches, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final VouchEntity vouche = mVouches.get(position);

		// User
		String userName = String.format("%s %s added a vouch",
				mUser.getFirstName(), mUser.getLastName());
		holder.txtName.setText(userName);
		Utilities.displayImage(mCtx, holder.imvProfile, mUser.getProfileUrl());

		holder.txtTime.setText(vouche.getVouchDate());
		holder.txtBusinessName.setText(vouche.getBusinessName());
		holder.txtBusinessDes.setText(vouche.getVouchDescription());
		Utilities.displayImage(mCtx, holder.imvBusiness,
				vouche.getLogoLocation());
		holder.rlBusiness.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(vouche, holder.getAdapterPosition());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mVouches == null ? 0 : mVouches.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_vouches_imv_business)
		ImageView imvBusiness;

		@BindView(R.id.item_vouches_imv_profile)
		ImageView imvProfile;

		@BindView(R.id.item_vouches_txt_time)
		TextView txtTime;

		@BindView(R.id.item_vouches_txt_business_name)
		TextView txtBusinessName;

		@BindView(R.id.item_vouches_txt_business_des)
		TextView txtBusinessDes;

		@BindView(R.id.item_vouches_txt_name)
		TextView txtName;

		@BindView(R.id.item_vouches_rl_business)
		RelativeLayout rlBusiness;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
