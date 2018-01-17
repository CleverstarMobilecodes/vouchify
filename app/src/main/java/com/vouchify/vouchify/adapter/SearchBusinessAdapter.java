package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/8/16.
 */
public class SearchBusinessAdapter
		extends
			RecyclerView.Adapter<SearchBusinessAdapter.ViewHolder> {

	private Context mCtx;
	private OnItemClickListener mListener;
	private List<BusinessEntity> mBusinesses;

	public SearchBusinessAdapter(Context ctx, OnItemClickListener listener,
			List<BusinessEntity> businesses) {

		this.mCtx = ctx;
		this.mBusinesses = businesses;
		this.mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_search_business, parent, false);
		return new ViewHolder(view);
	}

	@SuppressLint({"DefaultLocale", "SetTextI18n"})
	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final BusinessEntity business = mBusinesses.get(position);
		holder.txtName.setText(business.getBusinessName());
		holder.txtVoucher
				.setText("" + business.getVouches().getNumberVouches());
		if (business.getDistanceM() == 0) {

			holder.txtDistance.setText("");
		} else if (business.getDistanceM() < 1000) {

			holder.txtDistance.setText(String.format("(%.01fkm)",
					business.getDistanceM() / 10000));
		} else {

			holder.txtDistance.setText(String.format("(%.0fkm)",
					business.getDistanceM() / 1000));
		}

		Utilities.displayImage(mCtx, holder.imvProfile,
				business.getLogoThumbnail());

		if (business.getConnectionString() != null) {

			holder.txtConnection.setVisibility(View.VISIBLE);
			holder.txtConnection.setText(business.getConnectionString());
		} else {

			holder.txtConnection.setVisibility(View.GONE);
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(business, holder.getAdapterPosition());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mBusinesses == null ? 0 : mBusinesses.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_friend_imv_profile)
		ImageView imvProfile;

		@BindView(R.id.item_friend_txt_name)
		TextView txtName;

		@BindView(R.id.item_friend_txt_voucher)
		TextView txtVoucher;

		@BindView(R.id.item_friend_txt_distance)
		TextView txtDistance;

		@BindView(R.id.item_friend_txt_connection)
		TextView txtConnection;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}

}
