package com.vouchify.vouchify.adapter;

import java.util.List;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 3/23/16.
 */
public class ListSpinnerServiceAdapter extends BaseAdapter {

    private List<BusinessEntity> mItems;
    private LayoutInflater mInflater;

    public ListSpinnerServiceAdapter(Context ctx, List<BusinessEntity> items) {

        this.mItems = items;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return getView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getView(position, convertView, parent, true);
    }

    private View getView(final int position, View convertView, ViewGroup parent,
                         boolean isDropDown) {

        final ViewHolder holder;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.item_list_spinner, parent,
                    false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if (isDropDown) {

            holder.imageView.setVisibility(View.GONE);
        } else {

            holder.imageView.setVisibility(View.VISIBLE);
        }

        BusinessEntity field = mItems.get(position);
        holder.textView.setText(field.getServiceName());

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.list_spn_item_txt)
        TextView textView;

        @BindView(R.id.list_spn_item_imv)
        ImageView imageView;

        public ViewHolder(View view) {

            ButterKnife.bind(this, view);
        }
    }
}
