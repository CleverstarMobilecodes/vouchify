package com.vouchify.vouchify.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.fragment.BaseFragment;
import com.vouchify.vouchify.fragment.PhotoViewFragment;

import java.util.List;

/**
 * Hai Nguyen - 9/21/16.
 */

public class BusinessPhotoView extends RelativeLayout {

	private ImageView mPrevView;
	public BusinessPhotoView(Context context) {
		super(context);
	}

	public BusinessPhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BusinessPhotoView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public BusinessPhotoView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setPhotos(final BaseFragment fm,
			List<BusinessEntity> businesses, final int maxWidth) {

		this.removeAllViews();
		this.measure(0, 0);
		LayoutInflater inflater = LayoutInflater.from(getContext());

		for (final BusinessEntity business : businesses) {

			final ImageView imageView = (ImageView) inflater.inflate(
					R.layout.layout_business_photo, null);
			final LayoutParams imvParams = new LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			imageView.setPadding(
					getDimensionInPx(R.dimen.business_photo_imv_margin),
					getDimensionInPx(R.dimen.business_photo_imv_margin),
					getDimensionInPx(R.dimen.business_photo_imv_margin),
					getDimensionInPx(R.dimen.business_photo_imv_margin));
			Picasso.with(getContext()).load(business.getThumbnailLocation())
					.into(imageView, new com.squareup.picasso.Callback() {
						@Override
						public void onSuccess() {

							LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();

							// New image params
							imageView.measure(0, 0);
							imvParams.height = getDimensionInPx(R.dimen.business_photo_imv_height);
							int newWidth = imageView.getMeasuredWidth();
							imvParams.width = (newWidth * imvParams.height)
									/ imageView.getMeasuredHeight();
							imageView.setLayoutParams(imvParams);

							if (mPrevView != null) {

								mPrevView.measure(0, 0);
								LayoutParams oldParams = (LayoutParams) mPrevView
										.getLayoutParams();
								if (imvParams.width + oldParams.width
										+ mPrevView.getX() > maxWidth) {

									imageView.setX(0);
									imageView.setY(oldParams.height
											+ mPrevView.getY());
								} else {

									imageView.setX(oldParams.width
											+ mPrevView.getX());
									imageView.setY(mPrevView.getY());
								}
							}

							imageView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {

									fm.addChildFragment(PhotoViewFragment
											.getInstance(business
													.getLargeLocation()), true);
								}
							});

							mPrevView = imageView;
							params.height = (int) (imageView.getY() + imvParams.height);
							setLayoutParams(params);
							addView(imageView);
						}

						@Override
						public void onError() {

						}
					});

		}
	}
	/**
	 * Get dimension in px
	 */
	private int getDimensionInPx(int resId) {

        return  getContext().getResources().getDimensionPixelSize(resId);
	}
}
