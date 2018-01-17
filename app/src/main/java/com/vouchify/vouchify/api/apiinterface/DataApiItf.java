package com.vouchify.vouchify.api.apiinterface;

import com.google.gson.JsonObject;
import com.vouchify.vouchify.entity.ActivityFeedEntity;
import com.vouchify.vouchify.entity.BaseEntity;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.entity.CategoryEntity;
import com.vouchify.vouchify.entity.VouchEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Hai Nguyen - 8/8/16.
 */
public interface DataApiItf {

	@GET("autocompletesuburb")
	Call<JsonObject> getSuburbs(@Query("suburb_text") String filter);

	@POST("getvouchesforuser")
	Call<VouchEntity> getVouches(@Body JsonObject body);

	@POST("getvouchesforbusiness")
	Call<VouchEntity> getVouchesForBusiness(@Body JsonObject body);

	@POST("getvouch")
	Call<VouchEntity> getVouch(@Body JsonObject body);

	@POST("deletevouch")
	Call<BaseEntity> deleteVouch(@Body JsonObject body);

	@POST("writevouch")
	Call<VouchEntity> writeVouch(@Body JsonObject body);

	@POST("getbusiness")
	Call<BusinessEntity> getBusiness(@Body JsonObject body);

    @GET("getsubcategoriesorservices")
    Call<JsonObject> getSubcategoriesOrServices();

    @GET("autocompletesearch")
    Call<JsonObject> getCompleteSearch(@Query("search_text") String filter);

    @POST("searchbusinesses")
    Call<BusinessEntity> searchBusiness(@Body JsonObject body);

    @POST("notifyevent")
    Call<BaseEntity> notifyEvent(@Body JsonObject body);

    @GET("getsubcategoriesorservices")
    Call<CategoryEntity> getSubCategory(@Query("parent_category_id") int categoryId);

    @POST("getactivityfeed")
    Call<ActivityFeedEntity> getActivityFeed(@Body JsonObject body);

	@POST("requestmediauploadurl")
	Call<JsonObject> uploadBusinessImageUrl(@Body JsonObject body);

	@POST("confirmmediaupload")
	Call<JsonObject> uploadBusinessImageConfirm(@Body JsonObject body);
}
