/**
 *
 */
package com.vouchify.vouchify.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * @author ubuntu
 * 
 */
public class SquareImageView extends CustomImageButton {

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();

		if (d != null) {

			setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
		} else {

			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		}
	}
}
