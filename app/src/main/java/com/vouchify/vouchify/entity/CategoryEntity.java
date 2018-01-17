package com.vouchify.vouchify.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 8/15/16.
 */
public class CategoryEntity extends BaseEntity {

	@SerializedName("categories")
	private List<CategoryEntity> categories;

    @SerializedName("business_services")
    private List<BusinessEntity> businessServices;

    @SerializedName("category_image")
    private String categoryImage;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("result")
    private CategoryEntity result;

	@SerializedName("category_image_thumbnail")
	private String categoryImageThumbnail;

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public List<BusinessEntity> getBusinessServices() {
        return businessServices;
    }

    public CategoryEntity getResult() {
        return result;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryImageThumbnail() {
        return categoryImageThumbnail;
    }


}
