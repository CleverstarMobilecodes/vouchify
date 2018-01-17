package com.vouchify.vouchify.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vouchify.vouchify.MemUtils;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.api.DataApi;
import com.vouchify.vouchify.entity.BusinessEntity;
import com.vouchify.vouchify.utility.Utilities;
import com.vouchify.vouchify.view.CustomClickTextView;
import com.vouchify.vouchify.view.CustomTextView;
import com.vouchify.vouchify.view.LoadingDialog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Hai Nguyen - 9/21/16.
 */

public class AddImageFragment extends BaseFragment {

	private static final String BUNDLE_BUSINESS_ID = "bundle_business_id";
	private static final String BUNDLE_BUSINESS = "bundle_business";
	private static final int PICK_IMAGE = 101;

	private static final float BYTES_PER_PX = 4.0f;
	private static final int EXTERNAL_STORAGE_REQUEST_CODE = 123;
	@BindView(R.id.add_image_btn_done)
	CustomClickTextView btn_done;

	@BindView(R.id.business_add_image_view)
	FrameLayout add_image_view;

	@BindView(R.id.business_image_view)
	ImageView business_image_view;

	@BindView(R.id.business_camera_icon)
	ImageView camera_icon;

	@BindView(R.id.txt_add_image)
	CustomTextView txt_add_image;
	public Dialog dialog;
	int mBusinessId;
	public String uploadPath;
	public String mediaID;
	public Uri uri;
	public Bitmap bmp;
	String imagepath;
	public int scaleFactor;
	BusinessEntity mBusiness;
	public static AddImageFragment getInstance(BusinessEntity businessEntity, int businessid) {

		AddImageFragment fragment = new AddImageFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_BUSINESS_ID, businessid);
		bundle.putSerializable(BUNDLE_BUSINESS, businessEntity);

		fragment.setArguments(bundle);
		return fragment;
	}
	public static AddImageFragment getInstance() {

		AddImageFragment fragment = new AddImageFragment();
		return fragment;
	}

	@Override
	public void setToolBar() {

		assert txtTittle != null;
		txtTittle.setText(R.string.add_business_image);

		assert btnMenu != null;
		btnMenu.setImageResource(R.drawable.ic_action_back);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);
	}

	@Override
	protected int addView() {
		return R.layout.fragment_add_business_image;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			case R.id.toolbar_btn_left :

				mAct.onBackPressed();
				break;

			case R.id.add_image_btn_done :

				uploadImageUrl(uri);
				break;

			case R.id.business_add_image_view :

				Intent intent = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, PICK_IMAGE);

				break;

			default :

				super.onClick(view);
		}
	}

	@Override
	protected void initView() {
		super.initView();
		btn_done.setOnClickListener(this);
		add_image_view.setOnClickListener(this);
		mBusinessId = getArguments().getInt(BUNDLE_BUSINESS_ID);
		mBusiness = (BusinessEntity) getArguments().getSerializable(BUNDLE_BUSINESS);
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST_CODE);
		} else {
		}//		if (mBusinessId == null) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
			if (data == null) return;
			uri = data.getData();

			business_image_view.setVisibility(View.VISIBLE);
			camera_icon.setVisibility(View.GONE);
			txt_add_image.setVisibility(View.GONE);

			imagepath = getRealPathFromURI(uri);
			setPic();
		}
	}
	private void setPic() {

		if (readBitmapInfo() > MemUtils.megabytesFree()) {
			subSampleImage(scaleFactor);
		} else {
			if (MemUtils.megabytesAvailable() < 130){

				subSampleImage(2);
			}else {
				subSampleImage(1);
			}
		}
	}
	private void subSampleImage(int powerOf2) {
		if (powerOf2 < 1 || powerOf2 > 32) {
			Log.e("ScaleBeforeLoad", "trying to appply upscale or excessive downscale: " + powerOf2);
			powerOf2 = 32;
		}

		final Resources res = this.getResources();
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = powerOf2;
		bmp = BitmapFactory.decodeFile(imagepath, options);

		business_image_view.setImageBitmap(bmp);
	}
	private float readBitmapInfo() {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagepath, options);
		int targetW = 200;
		int targetH = 200;
		final float imageHeight = options.outHeight;
		final float imageWidth = options.outWidth;
		final String imageMimeType = options.outMimeType;
		scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = (int) Math.min(imageWidth / targetW, imageHeight / targetH);
		}

		Log.d("ScaleBeforeLoad", "w, h, type: " + imageWidth + ", " + imageHeight + ", " + imageMimeType);
		Log.d("ScaleBeforeLoad", "estimated memory required in MB: " + imageWidth * imageHeight * BYTES_PER_PX / MemUtils.BYTES_IN_MB);

		return imageWidth * imageHeight * BYTES_PER_PX / MemUtils.BYTES_IN_MB;
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };

		//This method was deprecated in API level 11
		//Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				contentUri, proj, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		int column_index =
				cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void uploadImageUrl(final Uri imageUri) {

		JsonObject jObject = new JsonObject();
		jObject.addProperty("file_extension", "jpg");
		jObject.addProperty("content_type", "busimage");
		jObject.addProperty("business_id", mBusinessId);
		jObject.addProperty("session_token", "4sbhsjtugo4g0jnaq490583evr");
		jObject.addProperty("temp_bus_token", "dgdkjnlkdngg44r4nrlkndfwfs");


		dialog = LoadingDialog.show(mAct);


		new DataApi(true).getInterface().uploadBusinessImageUrl(Utilities.addUserAuth(mAct, jObject))
				.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

				JsonObject jsonObject = response.body()
						.getAsJsonObject("result");
				uploadPath = jsonObject
						.get("upload_url").toString();
				mediaID = jsonObject
						.get("media_id").toString();

				new uploadingImageTask().execute(null, null, null);

			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {

				Utilities.dismissDialog(dialog);
			}
		});
	}

	public class uploadingImageTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params)
		{
			int length = uploadPath.length();
			uploadPath = uploadPath.substring(1, length - 1);
			postData(bmp);
			return null;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(String unsigned) {
			JsonObject jObject = new JsonObject();
			jObject.addProperty("upload_url", uploadPath);
			jObject.addProperty("media_id", mediaID);
			new DataApi(true).getInterface().uploadBusinessImageConfirm(Utilities.addUserAuth(mAct, jObject))
					.enqueue(new Callback<JsonObject>() {
						@Override
						public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

							JsonElement state = response.body().get("stat");
							String str = state.toString().substring(1, state.toString().length()-1);
							if (str.equals("ok")){

								Log.d("uploadImage:  ", "uploadSuccess");
								Utilities.dismissDialog(dialog);

								addChildFragment(
										BusinessFragment.getInstance(mBusiness),
										true);
//								mAct.onBackPressed();
							}
							else{
								Log.d("uploadImage:  ", "uploadFailed");
								Utilities.dismissDialog(dialog);
							}

						}

						@Override
						public void onFailure(Call<JsonObject> call, Throwable t) {
							Log.d("uploadImage:  ", "Failed");
							Utilities.dismissDialog(dialog);
						}
					});

		}
	}

	public void postData(Bitmap imageToSend) {
		try
		{
			URL url = new URL(uploadPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");

			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");

			conn.setRequestProperty("session_token", "4sbhsjtugo4g0jnaq490583evr");
			conn.setRequestProperty("temp_bus_token", "dgdkjnlkdngg44r4nrlkndfwfs");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-type", "image/jpeg");
			conn.setRequestProperty("fileName", "logo");

			conn.setReadTimeout(35000);
			conn.setConnectTimeout(35000);

			// directly let .compress write binary image data
			// to the output-stream
			OutputStream os = conn.getOutputStream();
			imageToSend.compress(Bitmap.CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();

			System.out.println("Response Code: " + conn.getResponseCode());

			InputStream in = new BufferedInputStream(conn.getInputStream());
			Log.d("sdfs", "sfsd");
			BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = responseStreamReader.readLine()) != null)
				stringBuilder.append(line).append("\n");
			responseStreamReader.close();

			String response = stringBuilder.toString();
			System.out.println(response);

			conn.disconnect();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
