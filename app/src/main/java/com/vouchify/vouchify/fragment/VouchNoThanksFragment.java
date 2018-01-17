package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 10/17/16.
 */

public class VouchNoThanksFragment extends BaseFragment {

	private static final String BUNDLE_VOUCH = "bundle_vouch";

    private VouchEntity mVouch;

//    private CallbackManager mCallbackManager;
//    private final List<String> mPermissions = Arrays.asList("publish_actions");

    public static VouchNoThanksFragment getInstance(VouchEntity vouch) {

		VouchNoThanksFragment fragment = new VouchNoThanksFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_VOUCH, vouch);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {

		return R.layout.fragment_vouch_nothanks;
	}

	@Override
	protected void initView() {
		super.initView();
//
//        mVouch = (VouchEntity) getArguments().getSerializable(BUNDLE_VOUCH);
//
//        if (mVouch != null) {
//
//            getVouch(mVouch.getVouchId());
//        }

	}

    @Override
    public void setToolBar() {

        assert txtTittle != null;
        txtTittle.setText(R.string.create_vouch);

        assert txtRight != null;
        txtRight.setVisibility(View.VISIBLE);
        txtRight.setText(R.string.done);
        txtRight.setOnClickListener(this);
    }
    @OnClick(R.id.share_btn_done)
    void onDone() {
//        addFragment(ProfileFragment.getInstance(), false);
        mAct.onBackPressed();
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.toolbar_txt_right :

                mAct.onBackPressed();
                break;
            default :

                super.onClick(view);
        }
    }


        /**
         * Get vouch
         */
    private void getVouch(String vouchId) {

        final Dialog dialog = LoadingDialog.show(mAct);
        JsonObject jObject = new JsonObject();
        jObject.addProperty("vouch_id", vouchId);
        new DataApi(true).getInterface()
                .getVouch(Utilities.addUserAuth(mAct, jObject))
                .enqueue(new BaseCallBack<VouchEntity>() {
                    @Override
                    public void validResponse(Call<VouchEntity> call,
                                              Response<VouchEntity> response) {

                        Utilities.dismissDialog(dialog);
                        VouchEntity vouch = response.body();
                        if (vouch.getStat().equals(Constants.RESULT_FAIL)) {

                            mAct.showError(vouch.getErrMessage());
                            return;
                        }

                        mVouch = vouch.getResult();
                    }

                    @Override
                    public void onFailure(Call<VouchEntity> call, Throwable t) {

                        Utilities.dismissDialog(dialog);
                    }
                });
    }

}
