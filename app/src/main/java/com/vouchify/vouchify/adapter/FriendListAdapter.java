package com.vouchify.vouchify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/8/16.
 */
public class FriendListAdapter
		extends
			RecyclerView.Adapter<FriendListAdapter.ViewHolder>
		implements
			Filterable {

	private Context mCtx;
	private OnItemClickListener mListener;
	private List<UserEntity> mUsers, mFilteredUsers;

	public FriendListAdapter(Context ctx, OnItemClickListener listener,
			List<UserEntity> users) {

		this.mCtx = ctx;
		this.mUsers = users;
		this.mListener = listener;
		this.mFilteredUsers = mUsers;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_friend_list, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final UserEntity user = mFilteredUsers.get(position);
		holder.txtName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
		holder.txtVoucher.setText("" + user.getTotalVouches());
		Utilities.displayImage(mCtx, holder.imvProfile, user.getProfileUrl());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(user, holder.getAdapterPosition());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mFilteredUsers == null ? 0 : mFilteredUsers.size();
	}

	@Override
	public Filter getFilter() {

		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				final FilterResults oReturn = new FilterResults();
				final ArrayList<UserEntity> results = new ArrayList<>();
				if (constraint != null) {
					if (mUsers != null && mUsers.size() > 0) {
						for (final UserEntity user : mUsers) {

							if (user.getFirstName().toLowerCase()
									.contains(constraint)
									|| user.getLastName().toLowerCase()
											.contains(constraint)) {

								results.add(user);
							}
						}
					}

					oReturn.values = results;
				}

				return oReturn;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				mFilteredUsers = (ArrayList<UserEntity>) results.values;
				notifyDataSetChanged();
			}
		};
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_friend_imv_profile)
		ImageView imvProfile;

		@BindView(R.id.item_friend_txt_name)
		TextView txtName;

		@BindView(R.id.item_friend_txt_voucher)
		TextView txtVoucher;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}

}
