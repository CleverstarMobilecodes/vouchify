package com.vouchify.vouchify.view;
/**
 *
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author nvhaiwork
 *
 */
public class CustomImageButton extends ImageView {

	private boolean isDefaultBg;
	public CustomImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomImageButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEnabled(boolean enabled) {

		if (enabled) {

			setAlpha(1f);
		} else {

			setAlpha(0.5f);
		}

		super.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#setPressed(boolean)
	 */
	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		// if (pressed) {
		//
		// this.setAlpha(0.8f);
		// } else {
		//
		// this.setAlpha(1f);
		// }

		if (pressed) {

			// if (getBackground() == null || isDefaultBg) {
			//
			// isDefaultBg = true;
			// setBackgroundColor(getContext().getResources().getColor(
			// R.color.button_bg_clicked));
			// }

			setAlpha(0.7f);
		} else {

			// if (isDefaultBg) {
			//
			// setBackgroundColor(getContext().getResources().getColor(
			// android.R.color.transparent));
			// }

			setAlpha(1f);
		}

		super.setPressed(pressed);
	}
}
