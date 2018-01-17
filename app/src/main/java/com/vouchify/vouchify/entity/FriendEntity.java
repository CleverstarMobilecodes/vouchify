package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Hai Nguyen - 8/15/16.
 */
public class FriendEntity extends BaseEntity {

	@SerializedName("total_ph_connections")
	private int totalPhConnections;

    @SerializedName("total_ph_vouches")
    private int totalPhVouches;

    @SerializedName("total_fb_connections")
    private int totalFbConnections;

    @SerializedName("total_fb_vouches")
    private int totalFbVouches;

    @SerializedName("result")
    private FriendEntity friend;

	@SerializedName("has_shared_fb_connections")
	private boolean isFacebookConnected;

	@SerializedName("has_shared_ph_connections")
	private boolean isPhoneConnected;

    private List<FriendConnectionEntity> friendPhConnections;
    private List<FriendConnectionEntity> friendFbConnections;

    public List<FriendConnectionEntity> getFriendPhConnections() {
        return friendPhConnections;
    }

    public List<FriendConnectionEntity> getFriendFbConnections() {
        return friendFbConnections;
    }

    public FriendEntity getFriend() {
        return friend;
    }

    public int getTotalPhConnections() {
        return totalPhConnections;
    }

    public int getTotalPhVouches() {
        return totalPhVouches;
    }

    public int getTotalFbConnections() {
        return totalFbConnections;
    }

    public int getTotalFbVouches() {
        return totalFbVouches;
    }

    public boolean isSharedPhConnections() {
        return isPhoneConnected;
    }

    public boolean isSharedFbConnections() {
        return isFacebookConnected;
    }


}
