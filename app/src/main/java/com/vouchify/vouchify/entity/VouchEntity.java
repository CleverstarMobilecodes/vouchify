package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Hai Nguyen - 9/8/16.
 */
public class VouchEntity extends BaseEntity {

	@SerializedName("vouch_id")
	private String vouchId;

	@SerializedName("business_name")
	private String businessName;

	@SerializedName("vouch_date")
	private String vouchDate;

	@SerializedName("logo_location")
	private String logoLocation;

	@SerializedName("vouch_description")
	private String vouchDescription;

	@SerializedName("total_vouches")
	private int totalVouches;

	@SerializedName("allow_delete")
	private boolean allowDelete;

	@SerializedName("is_editable")
	private boolean isEditable;

	@SerializedName("friend_vouches")
	private int totalFriendVouches;

	@SerializedName("number_of_vouches")
	private int numberVouches;

	@SerializedName("profile_photo")
	private String profileUrl;

	@SerializedName("voucher_name")
	private String vouchName;

    @SerializedName("is_connected")
    private boolean isConnected;

    @SerializedName("business_services")
    private List<BusinessEntity> businessServices;

    @SerializedName("business_service_summaries")
    private List<BusinessEntity> businessServiceSummaries;

	private UserEntity user;
	private VouchEntity vouch;
	private VouchEntity result;
	private BusinessEntity business;
	private List<VouchEntity> vouches;

	public String getVouchId() {
		return vouchId;
	}

	public int getTotalVouches() {
		return totalVouches;
	}

	public VouchEntity getResult() {
		return result;
	}

	public List<VouchEntity> getVouches() {
		return vouches;
	}

	public List<BusinessEntity> getBusinessServices() {
		return businessServices;
	}

	public String getBusinessName() {
		return businessName;
	}

	public String getVouchDate() {
		return vouchDate;
	}

	public String getLogoLocation() {
		return logoLocation;
	}

	public String getVouchDescription() {
		return vouchDescription;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public UserEntity getUser() {
		return user;
	}

	public VouchEntity getVouch() {
		return vouch;
	}

	public BusinessEntity getBusiness() {
		return business;
	}

	public int getTotalFriendVouches() {
		return totalFriendVouches;
	}

	public int getNumberVouches() {
		return numberVouches;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

    public String getVouchName() {
        return vouchName;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<BusinessEntity> getBusinessServiceSummaries() {
        return businessServiceSummaries;
    }
}
