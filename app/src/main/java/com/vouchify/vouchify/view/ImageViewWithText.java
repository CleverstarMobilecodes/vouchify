package com.vouchify.vouchify.view;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vouchify.vouchify.R;

import java.util.Locale;

/**
 * HaiNguyen on 3/12/15.
 */
public class ImageViewWithText extends RelativeLayout {

	private TextView mTextView;
	private ImageView mImageView;

	/**
	 * Display image
	 * 
	 */
	public void displayImage(String url, String firstName, String lastName) {

		String fChar = "", lChar = "";
		if (firstName != null && !firstName.isEmpty()) {

			fChar = firstName.substring(0, 1);
		}

		if (lastName != null && firstName != null && !lastName.isEmpty()
				&& !firstName.isEmpty()) {

			lChar = lastName.substring(0, 1);
		}

		setText(String.format(Locale.US, "%s%s", fChar, lChar));
		if (url == null || url.isEmpty()) {

			url = "-";
		}

		Picasso.with(getContext()).load(url).into(mImageView, new Callback() {
			@Override
			public void onSuccess() {

				mImageView.setVisibility(VISIBLE);
				mTextView.setVisibility(INVISIBLE);
			}

			@Override
			public void onError() {

				mImageView.setVisibility(INVISIBLE);
				mTextView.setVisibility(VISIBLE);
			}
		});
	}

	public ImageViewWithText(Context context) {
		super(context);
		init(context);
	}

	public ImageViewWithText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ImageViewWithText(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	/**
	 * Initiation view
	 */
	private void init(Context context) {

		inflate(context, R.layout.layout_image_with_text, this);
		mTextView = (TextView) findViewById(R.id.text_view);
		mImageView = (ImageView) findViewById(R.id.image_view);
	}

	/**
	 * Set display text
	 */
	public void setText(String text) {

		ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
		drawable.getPaint().setColor(
				ContextCompat.getColor(getContext(),
						R.color.color_description_text));
		mTextView.setText(text.toUpperCase());
		mTextView.setBackground(drawable);
	}

	/**
	 * Set text size
	 */
	public void setTextSize(float size) {

		if (mTextView != null) {

			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
	}
}
