/**
 *
 */
package com.vouchify.vouchify.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author nvhaiwork
 */
public class CustomClickTextView extends CustomTextView {

	private boolean isDefaultBg;
	public CustomClickTextView(Context paramContext) {
		super(paramContext);
		// setFont();
		init();
	}

	public CustomClickTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		// setFont();
		init();
	}

	public CustomClickTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		// setFont();
		init();
	}

	/**
	 * Init
	 */
	private void init() {

		if (!isEnabled()) {

			setAlpha(0.7f);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#setPressed(boolean)
	 */
	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		if (pressed) {

			setAlpha(0.7f);
		} else {

			setAlpha(1f);
		}

		super.setPressed(pressed);
	}
}
