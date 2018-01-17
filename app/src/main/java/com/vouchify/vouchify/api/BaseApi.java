package com.vouchify.vouchify.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.utility.CustomPreferences;
import com.vouchify.vouchify.utility.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Hai Nguyen - 8/27/15.
 */
class BaseApi {

	Retrofit mRetrofit;
	BaseApi(final boolean hasSession) {

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
				new HttpLoggingInterceptor.Logger() {
					@Override
					public void log(String message) {

						LogUtil.e("OkHttp", message);
					}
				});

		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(new Interceptor() {

					@Override
					public Response intercept(Chain chain) throws IOException {

						Request original = chain.request();
						Request.Builder builder = original.newBuilder()
								.header("Content-Type", "application/json")
								.header("Accept", "application/json")
								.method(original.method(), original.body());
						Request request;
						try {

							String postBodyString = bodyToString(original
									.body());
							JsonObject object = new Gson().fromJson(
									postBodyString, JsonObject.class);
							if (hasSession) {

								String strSession = CustomPreferences
										.getPreferences(
												Constants.PREF_SESSION_TOKEN,
												"");
								if (strSession != null && !strSession.isEmpty()) {

									object.addProperty(
											Constants.KEY_SESSION_TOKEN,
											strSession);
									postBodyString = object.toString();
								}
							}

							request = builder
                                    .method(original.method(), RequestBody.create(
											MediaType
													.parse("application/json;charset=UTF-8"),
											postBodyString)).build();
						} catch (Exception ex) {

							request = builder.build();
						}

						return chain.proceed(request);
					}
				}).addInterceptor(logging).build();

		mRetrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
				.client(httpClient)
				.addConverterFactory(GsonConverterFactory.create(gson)).build();
	}

	private String bodyToString(final RequestBody request) throws Exception {

		final Buffer buffer = new Buffer();
		if (request != null) {

			request.writeTo(buffer);
			return buffer.readUtf8();
		}

		return "";

	}
}
