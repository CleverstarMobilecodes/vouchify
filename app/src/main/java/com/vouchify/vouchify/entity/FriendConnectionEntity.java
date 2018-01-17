package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 8/16/16.
 */
public class FriendConnectionEntity extends BaseEntity {

    @SerializedName("profile_photo")
    private String profileUrl;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("total_vouches")
    private int totalVouches;

	private FriendConnectionEntity friendConnection;

	public FriendConnectionEntity getFriendConnection() {
		return friendConnection;
	}

    public String getUserId() {
        return userId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getTotalVouches() {
        return totalVouches;
    }
}
