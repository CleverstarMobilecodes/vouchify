package com.vouchify.vouchify.fragment;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.adapter.ActivitiesAdapter;
import com.vouchify.vouchify.api.BaseCallBack;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.ActivityFeedEntity;
import com.vouchify.vouchify.entity.VouchEntity;
import com.vouchify.vouchify.utility.CustomPreferences;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.CustomTextView;
import com.vouchify.vouchify.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Hai Nguyen - 8/18/16.
 */
public class FeedFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

	@BindView(R.id.feed_rg_type)
	RadioGroup rgFeedType;

	@BindView(R.id.feed_recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.feed_rb_near)
	RadioButton rbNear;

	@BindView(R.id.feed_txt_no_acivity)
	CustomTextView feedtxtnoacivity;

	private double mLongitude = 0;
	private double mLatitude = 0;
    private List<ActivityFeedEntity> mActivities;
    private boolean isFirstTime = true;
    private boolean isRefresh;
	private boolean isNearMe = false;

	public static FeedFragment getInstance() {

		return new FeedFragment();
	}

	@Override
	protected int addView() {
		return R.layout.fragment_feed;
	}

	@Override
	public void setToolBar() {
		super.setToolBar();
		assert txtTittle != null;
		txtTittle.setText(R.string.feed);
	}

	@Override
	protected void initView() {
		super.initView();

        mLongitude = CustomPreferences.getPreferences(Constants.PREF_LONGITUDE, 0.0);
        mLatitude = CustomPreferences.getPreferences(Constants.PREF_LATITUDE, 0.0);
		Utilities.setLayoutManager(mAct, recyclerView, true, true, true);
		rgFeedType.setOnCheckedChangeListener(this);

        mActivities = new ArrayList<>();
        if (isFirstTime) {

            isRefresh = false;
        }
		getActivityFeed(true);
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int i) {

		switch (i) {

			case R.id.feed_rb_friend :

				Utilities.dismissKeyboard(mAct, radioGroup);
                isRefresh = true;
                getActivityFeed(true);
				isNearMe = false;

				break;

			case R.id.feed_rb_near :

				Utilities.dismissKeyboard(mAct, radioGroup);
                isRefresh = !isFirstTime;
                getActivityFeed(false);
				isNearMe = true;
				break;

			default :

		}
	}

	/**
	 * Display feed list
	 */
	private void displayFeed() {
		if (mActivities == null || mActivities.size() == 0){
			if (isNearMe) {
				feedtxtnoacivity.setText(R.string.no_activity_near_you);
			}
			else {
				feedtxtnoacivity.setText(R.string.no_activity_by_your_friends);
			}
			feedtxtnoacivity.setVisibility(View.VISIBLE);
		}
		else {
			feedtxtnoacivity.setVisibility(View.INVISIBLE);
		}
			ActivitiesAdapter mAdapter = new ActivitiesAdapter(mAct, this, mActivities);
			recyclerView.setAdapter(mAdapter);
	}

    @Override
    public void onItemClick(Object object, int pos) {

        addChildFragment(VouchFragment.getInstance((VouchEntity) object), true);
    }

    private void getActivityFeed(final boolean type) {

		final Dialog dialog;
		if (isRefresh) {

			dialog = LoadingDialog.show(mAct);
		} else {

			dialog = null;
		}

		JsonObject jObject = new JsonObject();
		jObject.addProperty("connected_only", type);
		jObject.addProperty("include_images", true);
		jObject.addProperty("include_messages", true);
		if (mLatitude != 0 && mLongitude != 0) {

			jObject.addProperty("longitude", mLongitude);
			jObject.addProperty("latitude", mLatitude);
		}

		new DataApi(true).getInterface().getActivityFeed(Utilities.addUserAuth(mAct, jObject))
				.enqueue(new BaseCallBack<ActivityFeedEntity>() {
                    @Override
                    public void validResponse(Call<ActivityFeedEntity> call, Response<ActivityFeedEntity> response) {

                        if (isRefresh) {

                            Utilities.dismissDialog(dialog);
                        }

                        ActivityFeedEntity activity = response.body();
                        if (activity.getStat().equals(Constants.RESULT_FAIL)) {

                            mAct.showError(activity.getErrMessage());
                            return;
                        }

                        mActivities = activity.getResult().getActivities();

                        if (mActivities.size() < 3 && type && isFirstTime) {

                            rbNear.setChecked(true);

                        }
                        else {

                            isFirstTime = false;
                            displayFeed();
                        }


                    }

                    @Override
                    public void onFailure(Call<ActivityFeedEntity> call, Throwable t) {

                        if (isRefresh) {

                            Utilities.dismissDialog(dialog);
                        }
                    }
                });
	}
}
