package com.vouchify.vouchify.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Hai Nguyen - 8/16/16.
 */
public class DelayAutoCompleteTextView extends AutoCompleteTextView {

	private static final int MESSAGE_TEXT_CHANGED = 100;
	private static final int DEFAULT_AUTOCOMPLETE_DELAY = 500;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DelayAutoCompleteTextView.super.performFiltering(
					(CharSequence) msg.obj, msg.arg1);
		}
	};

	public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void performFiltering(CharSequence text, int keyCode) {

		mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
		mHandler.sendMessageDelayed(
				mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text),
				DEFAULT_AUTOCOMPLETE_DELAY);
	}

	@Override
	public void onFilterComplete(int count) {

		super.onFilterComplete(count);
	}
}
