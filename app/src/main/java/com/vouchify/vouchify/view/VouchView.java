package com.vouchify.vouchify.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/18/16.
 */
public class VouchView extends LinearLayout {

	@BindView(R.id.vouch_rl_tags)
	RelativeLayout rlTags;

	@BindView(R.id.vouch_txt_time)
	TextView txtVouchDate;

	@BindView(R.id.voucher_txt_vouch)
	TextView txtVouch;

	@BindView(R.id.vouch_txt_name)
	TextView txtUserName;

	@BindView(R.id.vouch_imv_is_friend)
	ImageView imvFriend;

	@BindView(R.id.vouch_imv_profile)
	ImageView imvProfile;

	private int mTagHolderWidth;
	public VouchView(Context context) {
		super(context);
		init();
	}

	public VouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VouchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public VouchView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {

		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.layout_vouch_view, this);
		ButterKnife.bind(this, view);
	}

	public void setVouch(final VouchEntity vouch, boolean isNested,
			boolean isLimit) {

		if (vouch == null) {

			return;
		}

		String vouchName = "", vouchDes, vouchTime, profileUrl = "";
		final List<BusinessEntity> businessServices;
		if (isNested) {
			UserEntity user = vouch.getUser();
			if (user != null) {

				if (user.isConnected()) {

					imvFriend.setVisibility(View.VISIBLE);
				}

				profileUrl = user.getProfileUrl();
				vouchName = user.getVoucherName();
			}

			final VouchEntity childVouch = vouch.getVouch();
			if (childVouch == null) {

				return;
			}

			vouchTime = childVouch.getVouchDate();
			vouchDes = childVouch.getVouchDescription();
			businessServices = childVouch.getBusinessServices();
		} else {

			if (vouch.isConnected()) {

				imvFriend.setVisibility(View.VISIBLE);
			}
			profileUrl = vouch.getProfileUrl();
			vouchName = vouch.getVouchName();
			vouchTime = vouch.getVouchDate();
			vouchDes = vouch.getVouchDescription();
			businessServices = vouch.getBusinessServices();
		}

		ViewTreeObserver vto = rlTags.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				rlTags.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				mTagHolderWidth = getMeasuredWidth();
				addTagViews(businessServices);
			}
		});

		if (isLimit) {

			if (vouchDes.length() > 250) {

				vouchDes = vouchDes.substring(0, 247) + "...";
			}
		}

		txtVouch.setText(vouchDes);
		txtVouchDate.setText(vouchTime);
		if (vouchName != null && !vouchName.isEmpty()) {

			txtUserName.setText(String.format("%s:", vouchName));
		} else {

			txtUserName.setVisibility(GONE);
		}

		Utilities.displayImage(getContext(), imvProfile, profileUrl);
	}

	private void addTagViews(List<BusinessEntity> business) {

		LayoutInflater inflater = LayoutInflater.from(getContext());
		rlTags.removeAllViews();
		LinearLayout prevView = null;
		rlTags.measure(0, 0);
		for (BusinessEntity busi : business) {

			LinearLayout view = (LinearLayout) inflater.inflate(
					R.layout.layout_vouch_tag, null);
			TextView txtTags = ButterKnife.findById(view, R.id.view_tag_txt);
			txtTags.setText(busi.getServiceName());
			if (prevView != null) {

				view.measure(0, 0);
				prevView.measure(0, 0);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlTags
						.getLayoutParams();

				int newWidth = view.getMeasuredWidth();
				int prevWidth = prevView.getMeasuredWidth();
				if (newWidth + prevWidth + prevView.getX() > mTagHolderWidth) {

					view.setX(0);
					view.setY(prevView.getMeasuredHeight() + prevView.getY());
				} else {

					view.setX(prevView.getMeasuredWidth() + prevView.getX());
					view.setY(prevView.getY());
				}

				params.height = (int) (view.getY() + view.getMeasuredHeight());
				rlTags.setLayoutParams(params);
			}

			prevView = view;
			rlTags.addView(view);
		}
	}
}
