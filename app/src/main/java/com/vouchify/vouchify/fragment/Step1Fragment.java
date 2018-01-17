package com.vouchify.vouchify.fragment;

import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.UserEntity;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;

/**
 * Hai Nguyen - 8/9/16.
 */
public class Step1Fragment extends BaseFragment {

	@BindView(R.id.step1_btn_submit)
	TextView btnSubmit;

	@BindView(R.id.step1_edt_first_name)
	EditText edtFirstName;

	@BindView(R.id.step1_edt_last_name)
	EditText edtLastName;

	@BindView(R.id.step1_edt_email)
	EditText edtEmail;

	@BindView(R.id.step1_ll_email)
	LinearLayout llEmail;

	@BindView(R.id.step1_ll_last_name)
	LinearLayout llLastName;

	@BindView(R.id.step1_ll_first_name)
	LinearLayout llFirstName;

	@BindView(R.id.step1_txt_title_email)
	TextView txtTitleEmail;

	@BindView(R.id.step1_txt_title_name)
	TextView txtTitleName;

	private UserEntity mUser;
	public static Step1Fragment getInstance() {

		return new Step1Fragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_step1;
	}

	@Override
	protected void initView() {
		super.initView();

		if (mAct.getUser() == null) {

			mAct.finish();
		}

		mUser = mAct.getUser();
		if (mUser.isLoggedInWithFb() || mUser.getEmail() == null
				|| mUser.getEmail().isEmpty()) {

			llEmail.setVisibility(View.VISIBLE);
			txtTitleEmail.setVisibility(View.VISIBLE);
			edtEmail.setText(mUser.getEmail());
		}

		if (mUser.getFirstName() == null || mUser.getFirstName().isEmpty()) {

			txtTitleName.setVisibility(View.VISIBLE);
			llFirstName.setVisibility(View.VISIBLE);
			edtFirstName.setText(mUser.getFirstName());
		}

		if (mUser.getLastName() == null || mUser.getLastName().isEmpty()) {

			txtTitleName.setVisibility(View.VISIBLE);
			llLastName.setVisibility(View.VISIBLE);
			edtLastName.setText(mUser.getLastName());
		}

		btnSubmit.setOnClickListener(this);
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

			case R.id.step1_btn_submit :

				next();
				break;
		}
	}

	private void next() {

		String strFirstName = edtFirstName.getText().toString().trim();
		if (strFirstName.isEmpty()
				&& (mUser.getFirstName() == null || mUser.getFirstName()
						.isEmpty())) {

			mAct.showError(getString(R.string.err_first_name_required));
			return;
		}

		String strLastName = edtLastName.getText().toString().trim();
		if (strLastName.isEmpty()
				&& (mUser.getLastName() == null || mUser.getLastName()
						.isEmpty())) {

			mAct.showError(getString(R.string.err_last_name_required));
			return;
		}

		String strEmail = edtEmail.getText().toString().trim();
		if (strEmail.isEmpty() && mUser.getEmail().isEmpty()) {

			mAct.showError(getString(R.string.err_email_required));
			return;
		}

		if (mUser.getFirstName() == null || mUser.getFirstName().isEmpty()) {

			mUser.setFirstName(strFirstName);
		}

		if (mUser.getLastName() == null || mUser.getLastName().isEmpty()) {

			mUser.setLastName(strLastName);
		}

		if (mUser.getEmail() == null || mUser.getEmail().isEmpty()) {

			mUser.setEmail(strEmail);
		}

		addFragment(Step2Fragment.getInstance(), true);
	}
}
