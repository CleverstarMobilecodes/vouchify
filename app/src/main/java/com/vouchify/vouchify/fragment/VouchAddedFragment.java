package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.LoadingDialog;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 10/17/16.
 */

public class VouchAddedFragment extends BaseFragment {

	private static final String BUNDLE_VOUCH = "bundle_vouch";

    private VouchEntity mVouch;

    private CallbackManager mCallbackManager;
    private final List<String> mPermissions = Arrays.asList("publish_actions");

    public static VouchAddedFragment getInstance(VouchEntity vouch) {

		VouchAddedFragment fragment = new VouchAddedFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_VOUCH, vouch);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int addView() {

		return R.layout.fragment_vouch_added;
	}

	@Override
	protected void initView() {
		super.initView();

        mVouch = (VouchEntity) getArguments().getSerializable(BUNDLE_VOUCH);

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                facebookCallback);
        if (mVouch != null) {

            getVouch(mVouch.getVouchId());
        }
	}

    @Override
    public void setToolBar() {

        assert txtTittle != null;
        txtTittle.setText(R.string.create_vouch);
    }

    @OnClick(R.id.share_btn_share)
	void onShare() {

        LoginManager.getInstance().logInWithPublishPermissions(this,
                mPermissions);
	}
    @OnClick(R.id.share_btn_no)
    void onNoThanks() {
//        mAct.onBackPressed();
        addChildFragment(VouchNoThanksFragment.getInstance(mVouch), false);
    }


    // Facebook
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback facebookCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {

            makeRequest(loginResult);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

            mAct.showError(getString(R.string.err_facebook_login));
        }
    };

    private void makeRequest(LoginResult loginResult) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject,
                                            GraphResponse graphResponse) {

                        AccessToken token = AccessToken.getCurrentAccessToken();
                        UserEntity user = new UserEntity();

                        user.setFbToken(token.getToken());

                        mAct.getUser().setFbToken(user.getFbToken());
                        mAct.getUser().setHasSharedFb(true);
                        mAct.setUser(mAct.getUser());
                        shareVouchFb();
                    }
                });

        request.setParameters(parameters);
        request.executeAsync();
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


    private void shareVouchFb() {

        if (mVouch == null) {

            return;
        }
        final Dialog dialog = LoadingDialog.show(mAct);
        JsonObject object = new JsonObject();
        object.addProperty("vouch_description", mVouch.getVouch().getVouchDescription());
        object.addProperty("business_id", mVouch.getBusiness()
                .getBusinessId());
        object.add("business_services",
                new Gson().toJsonTree(mVouch.getVouch().getBusinessServices()));
        object.addProperty("vouch_id", mVouch.getVouch().getVouchId());

        JsonObject objectShareFb = new JsonObject();
        objectShareFb.addProperty("share_fb", true);
        objectShareFb.addProperty("fb_credential", mAct.getUser().getFbToken());

        object.add("facebook_sharing", objectShareFb);

        new DataApi(true).getInterface()
                .writeVouch(Utilities.addUserAuth(mAct, object))
                .enqueue(new BaseCallBack<VouchEntity>() {
                    @Override
                    public void validResponse(Call<VouchEntity> call,
                                              Response<VouchEntity> response) {

                        Utilities.dismissDialog(dialog);
                        BaseEntity base = response.body();
                        if (base.getStat().equals(Constants.RESULT_FAIL)) {

                            mAct.showError(base.getErrMessage());
                            return;
                        }

                        addChildFragment(VouchNoThanksFragment.getInstance(mVouch), false);
//                        mAct.onBackPressed();
                    }

                    @Override
                    public void onFailure(Call<VouchEntity> call, Throwable t) {

                        Utilities.dismissDialog(dialog);
                    }
                });
    }
}
