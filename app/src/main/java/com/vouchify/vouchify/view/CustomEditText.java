package com.vouchify.vouchify.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.vouchify.vouchify.utility.Utilities;

/**
 * 1/19/15.
 */
public class CustomEditText extends EditText {

	public CustomEditText(Context context) {
		super(context);
		setFont();
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFont();
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFont();
	}

	@Override
	public void setError(CharSequence error, Drawable icon) {
		setCompoundDrawables(null, null, icon, null);
	}

	public void setFont() {

		if (isInEditMode()) {

			return;
		}

		int style = 0;
		Typeface typeFace = getTypeface();
		if (typeFace != null) {

			style = typeFace.getStyle();
		}

		typeFace = Utilities.getTypeFace(getContext(), style);
		setTypeface(typeFace);
	}
}
