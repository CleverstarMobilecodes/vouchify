package com.vouchify.vouchify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 6/30/16.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

	private List<String> mItems;
	private OnItemClickListener mClickListener;

	public MenuAdapter(Context ctx, List<String> items) {

		this.mItems = items;
		this.mClickListener = (OnItemClickListener) ctx;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_menu, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		holder.txtText.setText(mItems.get(position));
		// holder.txtText.setTypeface(holder.txtText.getTypeface(),
		// Typeface.NORMAL);
		// holder.txtText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCtx
		// .getResources().getDimension(R.dimen.menu_item_text));

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mClickListener.onItemClick(null, holder.getAdapterPosition());
			}
		});
	}

	@Override
	public int getItemCount() {

		return mItems == null ? 0 : mItems.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_menu_text)
		TextView txtText;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
