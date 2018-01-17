package com.vouchify.vouchify.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Hai Nguyen - 8/13/15.
 */
public class DisablePagingViewPager extends ViewPager {

	private boolean isEnabled;

	public DisablePagingViewPager(Context context) {
		super(context);
		this.isEnabled = false;
	}

	public DisablePagingViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.isEnabled = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return this.isEnabled && super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		return this.isEnabled && super.onInterceptTouchEvent(event);
	}

	public void setPagingEnabled(boolean enabled) {
		this.isEnabled = enabled;
	}
}
