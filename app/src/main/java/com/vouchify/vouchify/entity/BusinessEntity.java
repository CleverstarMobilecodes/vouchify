package com.vouchify.vouchify.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Hai Nguyen - 9/8/16.
 */
public class BusinessEntity extends BaseEntity {

	@SerializedName("service_name")
	private String serviceName;

	@SerializedName("service_id")
	private int serviceId;

	@SerializedName("logo_location")
	private String logoLocation;

	@SerializedName("business_id")
	private int businessId;

	@SerializedName("business_name")
	private String businessName;

	@SerializedName("display_address")
	private String displayAddress;

	@SerializedName("business_services")
	private List<BusinessEntity> businessServices;

    @SerializedName("summary_public_info")
    private BusinessEntity summaryPublicInfo;

    @SerializedName("extended_pulic_info")
    private BusinessEntity extendedPublicInfo;

    @SerializedName("about_short")
    private String aboutShort;

    @SerializedName("about_long")
    private String aboutLong;

    @SerializedName("business_logo")
    private BusinessEntity businessLogo;

    @SerializedName("image_location")
    private String imageLocation;

    @SerializedName("business_contact_details")
    private List<BusinessEntity> businessContactDetails;

    @SerializedName("contact_info_type")
    private String contactInfoType;

    @SerializedName("contact_info")
    private String contactInfo;

    @SerializedName("businesses")
    private List<BusinessEntity> business;

    @SerializedName("vouches")
    private VouchEntity vouches;

    @SerializedName("logo_thumbnail")
    private String logoThumbnail;

    @SerializedName("distance_m")
    private float distanceM;

    @SerializedName("business_images")
    private List<BusinessEntity> businessImages;
//
//    @SerializedName("busimage")
//    private String busimage;

    @SerializedName("thumbnail_location")
    private String thumbnailLocation;

    @SerializedName("large_location")
    private String largeLocation;

    @SerializedName("image_id")
    private int ImageId;


    @SerializedName("business_addresses")
    private List<BusinessEntity> businessAddresses;

    @SerializedName("address_line")
    private String addressLine;

    @SerializedName("suburb")
    private SuburbEntity suburbs;

    @SerializedName("show_map_pin")
    private boolean isShowMapPin;

    @SerializedName("show_map")
    private boolean isShowMap;

    @SerializedName("lattitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("map_zoom_level")
    private int mapZoomLevel;

    @SerializedName("connection_string")
    private String connectionString;

    @SerializedName("total_vouches_for_service")
    private int totalVouchesForService;

    @SerializedName("friend_vouches_for_service")
    private int friendVouchesForService;


    private BusinessEntity result;

    public BusinessEntity(int businessId, String businessName, int serviceId, String serviceName) {

        this.businessId = businessId;
        this.businessName = businessName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public BusinessEntity getResult() {
        return result;
    }

	public String getServiceName() {
		return serviceName;
	}

	public int getServiceId() {
		return serviceId;
	}

	public List<BusinessEntity> getBusinessServices() {
		return businessServices;
	}

	public String getLogoLocation() {
		return logoLocation;
	}

	public String getBusinessName() {
		return businessName;
	}

	public int getBusinessId() {
		return businessId;
	}

	public String getDisplayAddress() {
		return displayAddress;
	}

    public BusinessEntity getSummaryPublicInfo() {
        return summaryPublicInfo;
    }

    public BusinessEntity getExtendedPublicInfo() {
        return extendedPublicInfo;
    }

    public String getAboutShort() {
        return aboutShort;
    }

    public String getAboutLong() {
        return aboutLong;
    }

    public BusinessEntity getBusinessLogo() {
        return businessLogo;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public List<BusinessEntity> getBusinessContactDetails() {
        return businessContactDetails;
    }

    public String getContactInfoType() {
        return contactInfoType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public List<BusinessEntity> getBusiness() {
        return business;
    }

    public VouchEntity getVouches() {
        return vouches;
    }

    public String getLogoThumbnail() {
        return logoThumbnail;
    }

    public float getDistanceM() {
        return distanceM;
    }

    public List<BusinessEntity> getBusinessImage() {
        return businessImages;
    }

    public int getImageId() {
        return ImageId;
    }

    public String getThumbnailLocation() {
        return thumbnailLocation;
    }

    public String getLargeLocation() {
        return largeLocation;
    }

    public List<BusinessEntity> getBusinessAddresses() {
        return businessAddresses;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public SuburbEntity getSuburbs() {
        return suburbs;
    }

    public boolean isShowMapPin() {
        return isShowMapPin;
    }

    public boolean isShowMap() {
        return isShowMap;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getMapZoomLevel() {
        return mapZoomLevel;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public int getTotalVouchesForService() {
        return totalVouchesForService;
    }

    public int getFriendVouchesForService() {
        return friendVouchesForService;
    }

}
