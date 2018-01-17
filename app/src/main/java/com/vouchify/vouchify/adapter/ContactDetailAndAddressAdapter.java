package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.SuburbEntity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 6/30/16.
 */
@SuppressWarnings("deprecation")
public class ContactDetailAndAddressAdapter
		extends
			RecyclerView.Adapter<ContactDetailAndAddressAdapter.ViewHolder> {

	private List<BusinessEntity> mItems;
	private Context mCtx;

	public ContactDetailAndAddressAdapter(Context ctx,
			List<BusinessEntity> items) {

		this.mItems = items;
		this.mCtx = ctx;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_contact_address_detail, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final BusinessEntity item = mItems.get(position);

		if (item.getContactInfo() != null) {

			String txtShow;
			switch (item.getContactInfoType()) {
				case "MOBILE" :
				case "PHONE" :

					holder.txtText.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.ic_action_phone, 0, 0, 0);
					txtShow = item.getContactInfo();
					break;
				case "EMAIL" :

					holder.txtText.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.ic_email_about, 0, 0, 0);
					txtShow = item.getContactInfo();
					break;
				default :

					holder.txtText.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.ic_action_planet, 0, 0, 0);
					holder.txtText
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {

									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(item.getContactInfo()));
									mCtx.startActivity(i);
								}
							});

					txtShow = String.format("<a href=\"%s\">%s</a>",
							item.getContactInfo(), item.getContactInfo());
					break;
			}

			holder.txtText.setMaxLines(1);
			holder.txtText.setText(Html.fromHtml(txtShow));
		} else if (item.getAddressLine() != null) {

			// SpannableStringBuilder builder = new SpannableStringBuilder();
			// builder.append(" ").append(" ");
			// builder.setSpan(new ImageSpan(mCtx,
			// R.drawable.ic_login_location), builder.length() - 1,
			// builder.length(),
			// 0);
			// builder.append(" ");
			// SuburbEntity suburb = item.getSuburbs();

			holder.txtText.setMaxLines(Integer.MAX_VALUE);
			holder.txtText.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_location_about, 0, 0, 0);
			SuburbEntity suburb = item.getSuburbs();
			SpannableStringBuilder builder = new SpannableStringBuilder();
			if (suburb != null) {

				builder.append(String.format("%s, %s, %s, %s",
						item.getAddressLine(), suburb.getSuburbName(),
						suburb.getState(), suburb.getPostCode()));
			} else {

				builder.append(item.getAddressLine());
			}

			holder.txtText.setText(builder);
		}
	}

	@Override
	public int getItemCount() {

		return mItems == null ? 0 : mItems.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_text)
		TextView txtText;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}
	}
}
