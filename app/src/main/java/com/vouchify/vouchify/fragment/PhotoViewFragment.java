package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.utility.LogUtil;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.BindView;

/**
 * Hai Nguyen - 9/21/16.
 */

public class PhotoViewFragment extends BaseFragment {

	private static final String BUNDLE_URL = "bundle_url";

	@BindView(R.id.photo_view_imv)
	ImageView imageView;

	public static PhotoViewFragment getInstance(String url) {

		PhotoViewFragment fragment = new PhotoViewFragment();
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_URL, url);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.photo);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	protected int addView() {
		return R.layout.fragment_photo_view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;

			default :

				super.onClick(view);
		}
	}

	@Override
	protected void initView() {
		super.initView();

        final Dialog dialog = LoadingDialog.show(mAct);
		String url = getArguments().getString(BUNDLE_URL, "");
		LogUtil.e("PhotoViewFragment", url);
		Utilities.displayImagePhoto(mAct, imageView, url, new Callback() {
            @Override
            public void onSuccess() {

                Utilities.dismissDialog(dialog);
            }

            @Override
            public void onError() {

                Utilities.dismissDialog(dialog);
            }
        });
	}
}
