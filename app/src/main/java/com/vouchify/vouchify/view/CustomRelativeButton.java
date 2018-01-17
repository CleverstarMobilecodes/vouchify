package com.vouchify.vouchify.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Hai Nguyen - 8/9/16.
 */
public class CustomRelativeButton extends RelativeLayout {
	public CustomRelativeButton(Context context) {
		super(context);
	}

	public CustomRelativeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomRelativeButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
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
