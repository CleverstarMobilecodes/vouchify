package com.vouchify.vouchify.fragment;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.activity.MainActivity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

/**
 * Hai Nguyen - 8/9/16.
 */
public class FinishSignUpFragment extends BaseFragment {

	@BindView(R.id.finish_btn_start)
	TextView btnStart;

	public static FinishSignUpFragment getInstance() {

		return new FinishSignUpFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_finish_sign_up;
	}

	@Override
	protected void initView() {
		super.initView();
		btnStart.setOnClickListener(this);
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.register);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

			case R.id.finish_btn_start :

				startActivity(new Intent(mAct, MainActivity.class));
				mAct.finish();
				break;
		}
	}
}
