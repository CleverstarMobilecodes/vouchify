package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/8/16.
 */
public class SearchBusinessServiceAdapter
		extends
			RecyclerView.Adapter<SearchBusinessServiceAdapter.ViewHolder> {

	private OnItemClickListener mListener;
	private List<BusinessEntity> mBusinesses;
	private String mTxtSearch;

	public SearchBusinessServiceAdapter(OnItemClickListener listener,
			List<BusinessEntity> businesses, String txtSearch) {

		this.mBusinesses = businesses;
		this.mListener = listener;
		this.mTxtSearch = txtSearch;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_search_list, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final BusinessEntity business = mBusinesses.get(position);

		String name;
		if (business.getBusinessName() != null
				&& !business.getBusinessName().isEmpty()) {

			name = business.getBusinessName();
		} else {

			name = business.getServiceName();

		}

		SpannableStringBuilder builder = new SpannableStringBuilder(name);
		int startingIndex = name.toLowerCase().indexOf(mTxtSearch);
		int endingIndex = startingIndex + mTxtSearch.length();
		if (startingIndex >= 0 && endingIndex >= 0) {

			final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(
					89, 89, 89));
			builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex,
					endingIndex, 0);
			builder.setSpan(fcs, startingIndex, endingIndex, 0);
		}

		holder.txtName.setText(builder);
		if (business.getBusinessId() == -100 || business.getServiceId() == -100) {


			holder.txtName.setTextColor(Color.rgb(89, 89, 89));
			holder.txtName.setTypeface(holder.txtName.getTypeface(),
					Typeface.BOLD);
			holder.txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			return;
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

		@BindView(R.id.item_friend_txt_name)
		TextView txtName;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}

}
