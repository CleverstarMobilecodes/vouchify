package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 10/19/16.
 */

public class SubCategoriesAdapter
		extends
			RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder> {

	private Context mCtx;
	private List<BusinessEntity> mBusinesses;
	private OnItemClickListener mClickListener;

	public SubCategoriesAdapter(Context ctx, OnItemClickListener clickListener,
                                List<BusinessEntity> businesses) {
		this.mCtx = ctx;
		this.mBusinesses = businesses;
		this.mClickListener = clickListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(mCtx).inflate(R.layout.item_subcategories,
				parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

        final BusinessEntity business = mBusinesses.get(position);
		holder.tvActivity.setText(business.getServiceName());

        holder.tvActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClickListener.onItemClick(business, holder.getAdapterPosition());
            }
        });
	}

	@Override
	public int getItemCount() {
		return mBusinesses == null ? 0 : mBusinesses.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_activities_tv)
		TextView tvActivity;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
