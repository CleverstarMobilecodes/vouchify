package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Hai Nguyen - 8/15/16.
 */
public class UserEntity extends BaseEntity {

	private String role;
	private String gender;
	private String mobile;
	private String password;

	private boolean isLoggedInWithFb;
	private boolean isContactShouldSend = true;

	@SerializedName("mobile_verified")
	private boolean isPhoneVerified;

	@SerializedName("has_password")
	private boolean hasPassword;

	@SerializedName("profile_photo")
	private String profileUrl;

	@SerializedName("is_consumer")
	private boolean isConsumer;

	@SerializedName("is_business")
	private boolean isBusiness;

	@SerializedName("username")
	private String email;

    @SerializedName("user_name")
    private String userName;

	@SerializedName("fb_user_token")
	private String fbToken;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("session_token")
	private String sessionTkn;

	// Key for api result
	@SerializedName("has_shared_fb_connections")
	private boolean hasSharedFb;

	// Key for api result
	@SerializedName("has_shared_ph_connections")
	private boolean hasSharedPh;

	@SerializedName("is_connected")
	private boolean isConnected;

	// For get Friend
	@SerializedName("total_ph_connections")
	private int totalPhConnections;

	@SerializedName("total_ph_vouches")
	private int totalPhVouches;

	@SerializedName("total_fb_connections")
	private int totalFbConnections;

	@SerializedName("total_fb_vouches")
	private int totalFbVouches;

	@SerializedName("ph_connections")
	private List<UserEntity> friendPhConnections;

	@SerializedName("fb_connections")
	private List<UserEntity> friendFbConnections;

	// For get list Friend and detail Friend
	@SerializedName("total_vouches")
	private int totalVouches;

	// For get detail Friend
	@SerializedName("last_name_initial")
	private String lastNameInitial;

	@SerializedName("date_joined")
	private String dateJoined;

	@SerializedName("voucher_name")
	private String voucherName;

    // For search
    @SerializedName("default_search_suburb_id")
    private int defaultSearchSuburbId;

    @SerializedName("default_search_suburb_display")
    private String defaultSearchSuburbDisplay;

	private UserEntity result;

	public UserEntity() {
	}

	public UserEntity(String username, String password) {
		this.email = username;
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

    public String getUserName() {
        return userName;
    }


	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFbToken() {
		return fbToken;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getSessionTkn() {
		return sessionTkn;
	}

	public UserEntity getResult() {
		return result;
	}

	public void setFbToken(String fbToken) {
		this.fbToken = fbToken;
	}

	public boolean isLoggedInWithFb() {
		return isLoggedInWithFb;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setLoggedInWithFb(boolean loggedInWithFb) {
		isLoggedInWithFb = loggedInWithFb;
	}

	public void setSessionTkn(String sessionTkn) {
		this.sessionTkn = sessionTkn;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public boolean isHasSharedFb() {
		return hasSharedFb;
	}

	public void setHasSharedFb(boolean hasSharedFb) {
		this.hasSharedFb = hasSharedFb;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setContactShouldSend(boolean isSend) {
		this.isContactShouldSend = isSend;
	}

	public boolean isContactShouldSend() {
		return isContactShouldSend;
	}

	public boolean isHasPassword() {
		return hasPassword;
	}

	public boolean isPhoneVerified() {
		return isPhoneVerified;
	}

	public boolean isHasSharedPh() {
		return hasSharedPh;
	}

	public void setHasSharedPh(boolean hasSharedPh) {
		this.hasSharedPh = hasSharedPh;
	}

	// Friend
	public List<UserEntity> getFriendPhConnections() {
		return friendPhConnections;
	}

	public List<UserEntity> getFriendFbConnections() {
		return friendFbConnections;
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

	public int getTotalVouches() {
		return totalVouches;
	}

	// For Friend detail
	public String getLastNameInitial() {
		return lastNameInitial;
	}

	public String getDateJoined() {
		return dateJoined;
	}

	public String getVoucherName() {
		return voucherName;
	}

	public boolean isConnected() {
		return isConnected;
	}

    //For Search
    public int getDefaultSearchSuburbId() {
        return defaultSearchSuburbId;
    }

    public String getDefaultSearchSuburbDisplay() {
        return defaultSearchSuburbDisplay;
    }
}
