package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.SuburbEntity;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
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
@SuppressWarnings("deprecation")
public class SearchSuburbAdapter
		extends
			RecyclerView.Adapter<SearchSuburbAdapter.ViewHolder> {

	private Context mCtx;
	private OnItemClickListener mListener;
	private List<SuburbEntity> mSuburbs;
	private String mTxtSearch;

	public SearchSuburbAdapter(Context ctx, OnItemClickListener listener,
			List<SuburbEntity> suburbs, String txtSearch) {

		this.mCtx = ctx;
		this.mSuburbs = suburbs;
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

		final SuburbEntity suburb = mSuburbs.get(position);

		String name = suburb.getDisplay();

		if (name.contains(mTxtSearch.toUpperCase())) {

			name = name.replace(mTxtSearch.toUpperCase(),
					String.format("<b>%s</b>", mTxtSearch.toUpperCase()));
		}
		holder.txtName.setText(Html.fromHtml(name));
		if (suburb.getSuburbId() == 0) {

			holder.txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			holder.txtName.setTypeface(holder.txtName.getTypeface(),
					Typeface.BOLD);
			SpannableStringBuilder builder = new SpannableStringBuilder();
			builder.append(mCtx.getString(R.string.current_location)).append(
					" ");
			// builder.setSpan(new ImageSpan(mCtx,
			// R.drawable.ic_action_search_location,
			// ImageSpan.ALIGN_BASELINE), builder.length() - 1, builder
			// .length(), 0);
			holder.txtName.setText(builder);
			holder.txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					0, R.drawable.ic_action_search_location, 0);
		} else {

			holder.txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
					0, 0, 0);
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mListener.onItemClick(suburb, holder.getAdapterPosition());
			}
		});
	}

	@Override
	public int getItemCount() {
		return mSuburbs == null ? 0 : mSuburbs.size();
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
