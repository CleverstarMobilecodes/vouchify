package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 8/16/16.
 */
public class SuburbEntity extends BaseEntity {

	@SerializedName("suburb_id")
	private long suburbId;

	@SerializedName("suburb_display")
	private String display;

	@SerializedName("post_code")
	private String postCode;

	@SerializedName("suburb_name")
	private String suburbName;

	private String state;
	private String country;
	private SuburbEntity suburb;

    public SuburbEntity(long suburbId, String display) {

        this.suburbId = suburbId;
        this.display = display;
    }
	public String getPostCode() {
		return postCode;
	}

	public String getSuburbName() {
		return suburbName;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getDisplay() {
		return display;
	}

	public long getSuburbId() {
		return suburbId;
	}

	public SuburbEntity getSuburb() {
		return suburb;
	}
}
