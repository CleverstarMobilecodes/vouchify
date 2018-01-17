package com.vouchify.vouchify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.CategoryEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 10/17/16.
 */

public class CategoriesAdapter
		extends
			RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

	private Context mCtx;
	private OnItemClickListener mItemClickListener;
	private List<CategoryEntity> mCategories;

	public CategoriesAdapter(Context ctx,
			OnItemClickListener itemClickListener,
			List<CategoryEntity> categories) {

		this.mCtx = ctx;
		this.mCategories = categories;
		this.mItemClickListener = itemClickListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_home, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final CategoryEntity category = this.mCategories.get(position);
		holder.tvName.setText(category.getCategoryName());
		Utilities.displayImage(mCtx, holder.imageView,
				category.getCategoryImageThumbnail());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mItemClickListener.onItemClick(category,
						holder.getAdapterPosition());
			}
		});
	}
	@Override
	public int getItemCount() {
		return this.mCategories == null ? 0 : this.mCategories.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_home_tv)
		TextView tvName;

		@BindView(R.id.item_home_imv)
		ImageView imageView;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
