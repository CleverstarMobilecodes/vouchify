package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Hai Nguyen - 8/27/15.
 */
public class BaseEntity implements Serializable {

	protected String stat;

	@SerializedName("error_message")
	protected String errMessage;

    @SerializedName("error_code")
    protected int errCode;

	public BaseEntity() {
	}

	public String getStat() {
		return stat;
	}

	public String getErrMessage() {
		return errMessage;
	}

    public int getErrCode() {
        return errCode;
    }
}
