package com.vouchify.vouchify.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.view.VouchView;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/18/16.
 */
public class BusinessVouchesAdapter
		extends
			RecyclerView.Adapter<BusinessVouchesAdapter.ViewHolder> {

	private List<VouchEntity> mVouches;
    private OnItemClickListener mListener;

	public BusinessVouchesAdapter(List<VouchEntity> vouches, OnItemClickListener listener) {

		this.mVouches = vouches;
        this.mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_business_vouch, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

        final VouchEntity vouch = mVouches.get(position);
		holder.vouchView.setVouch(vouch, false, true);
        holder.vouchView.setOnClickListener(new View.OnClickListener() {
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

		@BindView(R.id.item_business_vouch)
		VouchView vouchView;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
