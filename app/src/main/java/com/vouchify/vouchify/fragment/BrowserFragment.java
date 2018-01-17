package com.vouchify.vouchify.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.constant.Constants;

import butterknife.BindView;

/**
 * Hai Nguyen - 8/27/16.
 */
public class BrowserFragment extends BaseFragment {

	private static final String BUNDLE_URL = "bundel_url";

	@BindView(R.id.broswer_web_view)
	WebView webView;

	public static BrowserFragment getInstance(String url) {

		BrowserFragment fragment = new BrowserFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_URL, url);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {
		return R.layout.fragment_browser;
	}

	@Override
	public void setToolBar() {

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initView() {
		super.initView();

		String url = getArguments().getString(BUNDLE_URL, "");
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient());
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.getSettings().setJavaScriptEnabled(true);
		if (Build.VERSION.SDK_INT >= 19) {

			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else {

			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		assert txtTittle != null;
		switch (url) {

			case Constants.URL_ABOUT :

				txtTittle.setText(R.string.about);
				break;
			case Constants.URL_POLICY :

				txtTittle.setText(R.string.privacy_policy);
				break;
			case Constants.URL_TERMS :

				txtTittle.setText(R.string.terms_of_use);
				break;
		}

		webView.loadUrl(url);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				backFragment(false);
				break;
			default :
				super.onClick(view);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		webView.onPause();
	}
}
