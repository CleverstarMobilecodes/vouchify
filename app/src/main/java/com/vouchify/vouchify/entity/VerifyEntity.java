package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 8/19/16.
 */
public class VerifyEntity extends BaseEntity {

	@SerializedName("contact_info_verification_id")
	private int verifyId;

	private VerifyEntity result;

	public int getVerifyId() {
		return verifyId;
	}

	public VerifyEntity getResult() {
		return result;
	}
}
