package com.vouchify.vouchify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.ActivityFeedEntity;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.viewinterface.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Hai Nguyen - 10/19/16.
 */

public class ActivitiesAdapter
		extends
			RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {

    private Context mCtx;
    private List<ActivityFeedEntity> mActivities;
    private OnItemClickListener mListener;

    public ActivitiesAdapter(Context ctx, OnItemClickListener listener,
                            List<ActivityFeedEntity> activities) {

        this.mCtx = ctx;
        this.mActivities = activities;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_activities, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        final VouchEntity vouch = mActivities.get(position).getVouch();
        UserEntity user = mActivities.get(position).getUser();
        BusinessEntity business = mActivities.get(position).getBusiness();
        // User

        String userName = "added a vouch";
        if (user.getUserName() != null) {

            userName = String.format("%s added a vouch",
                    user.getUserName());
        }

        holder.txtName.setText(userName);
        Utilities.displayImage(mCtx, holder.imvProfile, user.getProfileUrl());

        if (user.isConnected()) {

            holder.imvIsFriend.setVisibility(View.VISIBLE);
        }
        else {

            holder.imvIsFriend.setVisibility(View.INVISIBLE);
        }
        if (vouch != null) {
            holder.txtTime.setText(vouch.getVouchDate());
            holder.txtBusinessName.setText(business.getBusinessName());
            holder.txtBusinessDes.setText(vouch.getVouchDescription());
            Utilities.displayImage(mCtx, holder.imvBusiness,
                    business.getLogoLocation());
            holder.rlBusiness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mListener.onItemClick(vouch, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return mActivities == null ? 0 : mActivities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_vouches_imv_business)
        ImageView imvBusiness;

        @BindView(R.id.item_vouches_imv_is_friend)
        ImageView imvIsFriend;

        @BindView(R.id.item_vouches_imv_profile)
        ImageView imvProfile;

        @BindView(R.id.item_vouches_txt_time)
        TextView txtTime;

        @BindView(R.id.item_vouches_txt_business_name)
        TextView txtBusinessName;

        @BindView(R.id.item_vouches_txt_business_des)
        TextView txtBusinessDes;

        @BindView(R.id.item_vouches_txt_name)
        TextView txtName;

        @BindView(R.id.item_vouches_rl_business)
        RelativeLayout rlBusiness;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
