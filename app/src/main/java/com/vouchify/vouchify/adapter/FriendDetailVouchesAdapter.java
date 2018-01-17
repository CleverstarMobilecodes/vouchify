package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/8/16.
 */
public class FriendDetailVouchesAdapter
		extends
			RecyclerView.Adapter<FriendDetailVouchesAdapter.ViewHolder> {

	private Context mCtx;
	private List<VouchEntity> mVouches;
	private OnItemClickListener mListener;

	public FriendDetailVouchesAdapter(Context ctx, OnItemClickListener listener, List<VouchEntity> vouches) {

		this.mCtx = ctx;
		this.mVouches = vouches;
		this.mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_vouches_friend_detail, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final VouchEntity vouch = mVouches.get(position);

		// User
		holder.txtName.setText(mCtx.getString(R.string.added_a_vouch));
		holder.txtTime.setText(vouch.getVouchDate());
		holder.txtBusinessName.setText(vouch.getBusinessName());
		holder.txtBusinessDes.setText(vouch.getVouchDescription());
		Utilities.displayImage(mCtx, holder.imvBusiness,
                vouch.getLogoLocation());
		holder.rlBusiness.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(vouch, holder.getAdapterPosition());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mVouches == null ? 0 : mVouches.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_voucher_imv_business)
		ImageView imvBusiness;

		@BindView(R.id.item_voucher_txt_time)
		TextView txtTime;

		@BindView(R.id.item_voucher_txt_business_name)
		TextView txtBusinessName;

		@BindView(R.id.item_voucher_txt_business_des)
		TextView txtBusinessDes;

		@BindView(R.id.item_voucher_txt_name)
		TextView txtName;

		@BindView(R.id.item_voucher_rl_business)
		RelativeLayout rlBusiness;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
