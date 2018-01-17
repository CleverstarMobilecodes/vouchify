package com.vouchify.vouchify.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.activity.BaseActivity;
import com.vouchify.vouchify.constant.Constants;
import com.vouchify.vouchify.entity.UserEntity;
import com.vouchify.vouchify.view.DividerItemDecoration;
import com.vouchify.vouchify.view.MyLayoutManager;

import java.security.MessageDigest;
import java.util.List;

/**
 * Hai Nguyen - 8/27/15.
 */
public class Utilities {

	/**
	 * Dismiss dialog
	 */
	public static void dismissDialog(Dialog dialog) {

		try {

			dialog.dismiss();
		} catch (Exception e) {

			LogUtil.e("Dismiss dialog", "Dismiss dialog");
		}
	}

	/**
	 * Show alert dialog
	 *
	 * @param title
	 *            Dialog title
	 * @param message
	 *            Dialog message
	 * @param positiveText
	 *            Positive button text
	 * @param negativeText
	 *            Negative button text
	 * @param positiveButtonClick
	 *            Positive button click listener
	 * @param negativeButtonClick
	 *            Negative button click listener
	 * @param isCancelAble
	 *            True if can cancel
	 */
	public static void showAlertDialog(Context context, String title,
			String message, String positiveText, String negativeText,
			DialogInterface.OnClickListener positiveButtonClick,
			DialogInterface.OnClickListener negativeButtonClick,
			boolean isCancelAble) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setCancelable(isCancelAble);
		dialogBuilder.setMessage(message);
		if (!title.equals("")) {

			dialogBuilder.setTitle(title);
		}

		// Positive button
		if (!positiveText.equals("")) {

			if (positiveButtonClick != null) {

				dialogBuilder.setPositiveButton(positiveText,
						positiveButtonClick);
			} else {

				dialogBuilder.setPositiveButton(positiveText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
			}
		}

		// Negative button
		if (!negativeText.equals("")) {

			if (negativeButtonClick != null) {

				dialogBuilder.setNegativeButton(negativeText,
						negativeButtonClick);
			} else {

				dialogBuilder.setNegativeButton(negativeText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
			}
		}

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	/**
	 * Show alert dialog
	 *
	 * @param title
	 *            Dialog title
	 * @param messages
	 *            Dialog message
	 * @param positiveText
	 *            Positive button text
	 * @param negativeText
	 *            Negative button text
	 * @param positiveButtonClick
	 *            Positive button click listener
	 * @param negativeButtonClick
	 *            Negative button click listener
	 * @param isCancelAble
	 *            True if can cancel
	 */
	public static void showAlertDialogListDataCall(final Context context,
			String title, List<String> messages, String positiveText,
			String negativeText,
			DialogInterface.OnClickListener positiveButtonClick,
			DialogInterface.OnClickListener negativeButtonClick,
			boolean isCancelAble) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setCancelable(isCancelAble);

		final CharSequence[] Items = messages.toArray(new CharSequence[messages
				.size()]);
		dialogBuilder.setItems(Items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {

				startCall(
						context,
						String.format("tel:%s",
								Items[i].toString().replaceAll("[^\\d.]", "")));
				dialog.dismiss();
			}
		});

		if (!title.equals("")) {

			dialogBuilder.setTitle(title);
		}

		// Positive button
		if (!positiveText.equals("")) {

			if (positiveButtonClick != null) {

				dialogBuilder.setPositiveButton(positiveText,
						positiveButtonClick);
			} else {

				dialogBuilder.setPositiveButton(positiveText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
			}
		}

		// Negative button
		if (!negativeText.equals("")) {

			if (negativeButtonClick != null) {

				dialogBuilder.setNegativeButton(negativeText,
						negativeButtonClick);
			} else {

				dialogBuilder.setNegativeButton(negativeText,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
			}
		}

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	public static void startCall(Context ctx, String phone) {

		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
		ctx.startActivity(intent);
	}

	public static void startSendMail(Context ctx, String email, String subject) {

		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(String
				.format("mailto:%s", email)));
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		ctx.startActivity(intent);
	}

	/**
	 * Get type face
	 */
	public static Typeface getTypeFace(Context context, int style) {

		Typeface typeFace;
		switch (style) {
			case 1 :

				typeFace = Typeface.createFromAsset(context.getAssets(),
						"fonts/Montserrat-Bold.ttf");
				break;
			case 2 :

				typeFace = Typeface.createFromAsset(context.getAssets(),
						"fonts/Montserrat-Regular.ttf");
				break;

			default :

				typeFace = Typeface.createFromAsset(context.getAssets(),
						"fonts/MyFont-Regular.otf");
				break;
		}

		return typeFace;
	}

	/**
	 * Display network image
	 */
	public static void displayImage(Context ctx, ImageView imageView, String url) {

		if (url == null || url.equals("")) {

			url = "-";
		}

		Picasso.with(ctx).load(url).placeholder(R.drawable.ic_place_holder)
				.into(imageView);
	}

	/**
	 * Display network image photo
	 */
	public static void displayImagePhoto(Context ctx,
			final ImageView imageView, String url,
			com.squareup.picasso.Callback callback) {

		if (url == null || url.equals("")) {

			url = "-";
		}

		Picasso.with(ctx).load(url).into(imageView, callback);
	}

	/**
	 * Set recycler view layout manager
	 *
	 * @param ctx
	 *            Context
	 */
	public static RecyclerView.LayoutManager setLayoutManager(Context ctx,
			RecyclerView view, boolean isVertical, boolean isFixedSize,
			boolean hasDivider) {
		int orientation;
		if (isVertical) {

			orientation = RecyclerView.VERTICAL;
		} else {

			orientation = RecyclerView.HORIZONTAL;
		}

		MyLayoutManager mLayoutManager = new MyLayoutManager(ctx, view,
				orientation, false);
		view.setHasFixedSize(isFixedSize);
		view.setLayoutManager(mLayoutManager);
		if (hasDivider) {

			DividerItemDecoration divider = new DividerItemDecoration(ctx, null);
			view.addItemDecoration(divider);
		}

		return mLayoutManager;
	}

	/**
	 * Save user information
	 */
	public static void saveUser(UserEntity user) {

		CustomPreferences.setPreferences(Constants.PREF_EMAIL, "");
		CustomPreferences.setPreferences(Constants.PREF_USER_ID, "");
		// CustomPreferences.setPreferences(Constants.PREF_GENDER, "M");
		CustomPreferences.setPreferences(Constants.PREF_PASSWORD, "");
		CustomPreferences.setPreferences(Constants.PREF_LAST_NAME, "");
		CustomPreferences.setPreferences(Constants.PREF_FIRST_NAME, "");
		// CustomPreferences.setPreferences(Constants.PREF_LOCATION_ID, 0L);
		// CustomPreferences.setPreferences(Constants.PREF_PHONE_NUMBER, "");
		CustomPreferences.setPreferences(Constants.PREF_SESSION_TOKEN, "");
		CustomPreferences.setPreferences(Constants.PREF_FACEBOOK_TOKEN, "");
		CustomPreferences.setPreferences(Constants.PREF_IS_LOG_IN_WITH_FB,
				false);
		// CustomPreferences.setPreferences(Constants.PREF_CONTACTS_SHOULD_SEND,
		// false);
		if (user == null) {

			return;
		}

		CustomPreferences.setPreferences(Constants.PREF_EMAIL, user.getEmail());
		CustomPreferences.setPreferences(Constants.PREF_USER_ID,
				user.getUserId());
		// CustomPreferences.setPreferences(Constants.PREF_GENDER,
		// user.getGender());
		CustomPreferences.setPreferences(Constants.PREF_PASSWORD,
				user.getPassword());
		CustomPreferences.setPreferences(Constants.PREF_LAST_NAME,
				user.getLastName());
		CustomPreferences.setPreferences(Constants.PREF_FIRST_NAME,
				user.getFirstName());
		// CustomPreferences.setPreferences(Constants.PREF_LOCATION_ID,
		// user.getAddressId());
		// CustomPreferences.setPreferences(Constants.PREF_PHONE_NUMBER,
		// user.getPhone());
		CustomPreferences.setPreferences(Constants.PREF_SESSION_TOKEN,
				user.getSessionTkn());
		CustomPreferences.setPreferences(Constants.PREF_FACEBOOK_TOKEN,
				user.getFbToken());
		CustomPreferences.setPreferences(Constants.PREF_IS_LOG_IN_WITH_FB,
				user.isLoggedInWithFb());
		// CustomPreferences.setPreferences(Constants.PREF_CONTACTS_SHOULD_SEND,
		// user.isPhoneConnected());
	}

	/**
	 * Get saved user
	 */
	public static UserEntity getSavedUser() {

		UserEntity user = new UserEntity();
		user.setEmail(CustomPreferences
				.getPreferences(Constants.PREF_EMAIL, ""));
		user.setPassword(CustomPreferences.getPreferences(
				Constants.PREF_PASSWORD, ""));
		user.setLastName(CustomPreferences.getPreferences(
				Constants.PREF_LAST_NAME, ""));
		user.setFirstName(CustomPreferences.getPreferences(
				Constants.PREF_FIRST_NAME, ""));
		// user.setAddressId(CustomPreferences.getPreferences(
		// Constants.PREF_LOCATION_ID, 0L));
		// user.setPhone(CustomPreferences.getPreferences(
		// Constants.PREF_PHONE_NUMBER, ""));
		user.setSessionTkn(CustomPreferences.getPreferences(
				Constants.PREF_SESSION_TOKEN, ""));
		user.setFbToken(CustomPreferences.getPreferences(
				Constants.PREF_FACEBOOK_TOKEN, ""));
		user.setUserId(CustomPreferences.getPreferences(Constants.PREF_USER_ID,
				""));
		// user.setGender(CustomPreferences.getPreferences(Constants.PREF_GENDER,
		// "M"));
		// user.setPhoneConnected(CustomPreferences.getPreferences(
		// Constants.PREF_CONTACTS_SHOULD_SEND, false));
		user.setLoggedInWithFb(CustomPreferences.getPreferences(
				Constants.PREF_IS_LOG_IN_WITH_FB, false));
		return user;
	}

	/**
	 * Dismiss keyboard
	 */
	public static void dismissKeyboard(Context ctx, View view) {

		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 *
	 */
	// public static String getAddress(SuburbEntity address) {
	//
	// if (address == null) {
	//
	// return "";
	// }
	//
	// return String
	// .format("%s, %s %s, %s", address.getSuburbName(),
	// address.getState(), address.getPostCode(),
	// address.getCountry());
	// }

	/**
	 * Check user permission
	 *
	 * @param context
	 *            Application context
	 * @param permissions
	 *            List of permissions
	 * @return true if has permission
	 */
	public static boolean hasPermissions(Context context, String... permissions) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				&& context != null && permissions != null) {

			for (String permission : permissions) {

				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Get number of phone
	 * 
	 * @param number
	 *            string phone number
	 * @return number
	 */
	private static String convertMobileToDomesticFormatAndValidate(String number) {

		if (number == null) {

			return null;
		}

		if (!number.equals("")) {

			if (number.length() >= 10) {

				// remove all non numeric characters
				number = number.replaceAll("[^\\d.]", "");

				// if starts with 614 replace with 04
				if (number.startsWith("614")) {

					number = String.format("04%s", number.substring(3));
				} else if (number.startsWith("6104")) { // if starts with 6104
														// replace with 04

					number = String.format("04%s", number.substring(4));
				}
			}
			if (number.length() != 10 || !number.startsWith("04")) {

				number = "";
			}
		}

		return number;
	}

	public static String convertMobileAndEncrypt(String number) {

		number = convertMobileToInternationalFormatAndValidate(number);
		if (number == null || number.equals("")) {

			return null;
		}

		number = String.format("VouchifySecureSalt08%s", number);
		return sha256(number);
	}

	private static String sha256(String number) {

		MessageDigest digest;
		try {

			digest = MessageDigest.getInstance("SHA-256");
			digest.update(number.getBytes("UTF-8"));
			number = MyBase64.encode(digest.digest());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return number;
	}

	/**
	 * 
	 * convertMobileToInternationalFormatAndValidate
	 */
	public static String convertMobileToInternationalFormatAndValidate(
			String number) {

		number = convertMobileToDomesticFormatAndValidate(number);
		if (number == null || number.isEmpty()) {

			return null;
		}

		number = number.substring(1);
		return String.format("+61%s", number);
	}

	/**
	 *
	 */
	public static JsonObject addUserAuth(BaseActivity act, JsonObject object) {

		if (object == null) {

			object = new JsonObject();
		}

		if (act.getUser() == null) {

			return null;
		}

		// if (act.getUser().getSessionTkn() != null &&
		// !act.getUser().getSessionTkn().equals("")) {
		//
		// object.addProperty(Constants.KEY_SESSION_TOKEN,
		// act.getUser().getSessionTkn());
		// } else {
		//
		// object.addProperty(Constants.KEY_SESSION_TOKEN,
		// CustomPreferences.getPreferences(Constants.PREF_SESSION_TOKEN, ""));
		// }

		object.addProperty(Constants.KEY_USER_ID, act.getUser().getUserId());
		return object;
	}

	/**
	 * Check application is installed or not
	 *
	 * @param uri
	 *            package uri
	 * @param context
	 *            {@link Context}
	 * @return true if installed
	 */
	public static boolean isAppInstalled(String uri, Context context) {

		PackageManager pm = context.getPackageManager();
		boolean installed;
		try {

			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {

			installed = false;
		}

		return installed;
	}
}
