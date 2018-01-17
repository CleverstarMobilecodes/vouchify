package com.vouchify.vouchify.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 8/15/16.
 */
public class ActivityFeedEntity extends BaseEntity {

	@SerializedName("activities")
	private List<ActivityFeedEntity> activities;

    @SerializedName("vouch")
    private VouchEntity vouch;

    @SerializedName("activity_type")
    private String activityType;

    @SerializedName("business")
    private BusinessEntity business;

    @SerializedName("user")
    private UserEntity user;

    @SerializedName("result")
    private ActivityFeedEntity result;

	@SerializedName("category_image_thumbnail")
	private String categoryImageThumbnail;

    public List<ActivityFeedEntity> getActivities() {
        return activities;
    }

    public ActivityFeedEntity getResult() {
        return result;
    }

    public String getActivityType() {
        return activityType;
    }

    public VouchEntity getVouch() {
        return vouch;
    }

    public BusinessEntity getBusiness() {
        return business;
    }

    public UserEntity getUser() {
        return user;
    }


}
