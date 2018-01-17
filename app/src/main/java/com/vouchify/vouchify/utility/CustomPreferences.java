package com.vouchify.vouchify.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Custom Share Preferences. Include Set and Get
 *
 * <pre>
 * CustomSharedPreferences(Context)
 * Context is null will return by preferences type
 * 1. String : null
 * 2. Boolean: false
 * 3. Integer, Long, Float: 0
 * </pre>
 *
 * @author DucLH
 * @version 2011:0517
 * @since 2.1
 *
 */

public class CustomPreferences {
	/** KEY SHARE PREFERENCES **/
	public static final String CUSTOM_SHARED_PREFERENCES = "scutifysharepref";

	/** FIELD OF CLASS **/
	private static SharedPreferences mSharedPre;
	private static SharedPreferences.Editor mEditor;
	private static Context mContext;

	public static void init(final Context context) {

		mContext = context;
		if (null != context) {

			if (mSharedPre == null) {

				mSharedPre = context.getSharedPreferences(
						CUSTOM_SHARED_PREFERENCES, 0);
			}

			mEditor = mSharedPre.edit();
		} else {
			mEditor = null;
			mSharedPre = null;
		}
	}

	private static void refresh() {
		if (null != mContext) {

			mSharedPre = mContext.getSharedPreferences(
					CUSTOM_SHARED_PREFERENCES, 0);
			mEditor = mSharedPre.edit();
		} else {

			mEditor = null;
			mSharedPre = null;
		}
	}

	/**
	 * Set data for String
	 *
	 * @param preName
	 *            Preferences name
	 * @param value
	 *            String input
	 */
	public synchronized static void setPreferences(final String preName,
			final String value) {
		refresh();
		if (null != mEditor) {
			mEditor.putString(preName, value);
			mEditor.commit();
		}
	}

	/**
	 * Get data for String
	 *
	 * @param preName
	 *            Preferences name
	 * @return String or 0 if Name not existed
	 */
	public static String getPreferences(final String preName,
			final String defaultValue) {
		refresh();
		if (null != mSharedPre) {

			return mSharedPre.getString(preName, defaultValue);
		} else {

			return null;
		}
	}

	/**
	 * Set data for boolean
	 *
	 * @param preName
	 *            Preferences name
	 * @param value
	 *            boolean input
	 */
	public synchronized static void setPreferences(final String preName,
			final boolean value) {
		if (null != mEditor) {
			mEditor.putBoolean(preName, value);
			mEditor.commit();
		}
	}

	/**
	 * Get data for boolean
	 *
	 * @param preName
	 *            Preferences name
	 * @return boolean or 0 if Name not existed
	 */
	public static boolean getPreferences(final String preName,
			final boolean defaultValue) {
		if (null != mSharedPre) {
			return mSharedPre.getBoolean(preName, defaultValue);
		}

		return defaultValue;
	}

	/**
	 * Set data for Integer
	 *
	 * @param preName
	 *            Preferences name
	 * @param value
	 *            Integer input
	 */
	public synchronized static void setPreferences(final String preName,
			final int value) {
		if (null != mEditor) {
			mEditor.putInt(preName, value);
			mEditor.commit();
		}
	}

	/**
	 * Get data for Integer
	 *
	 * @param preName
	 *            Preferences name
	 * @return Integer or 0 if Name not existed
	 */
	public static int getPreferences(final String preName,
			final int defaultValue) {
		if (null != mSharedPre) {
			return mSharedPre.getInt(preName, defaultValue);
		} else {
			return 0;
		}
	}

	/**
	 * Set data for Long
	 *
	 * @param preName
	 *            Preferences name
	 * @param value
	 *            Long input
	 */
	public synchronized static void setPreferences(final String preName,
			final long value) {
		if (null != mEditor) {
			mEditor.putLong(preName, value);
			mEditor.commit();
		}
	}

	/**
	 * Get data for Long
	 *
	 * @param preName
	 *            Preferences name
	 * @return Long or 0 if Name not existed
	 */
	public static long getPreferences(final String preName,
			final long defaultValue) {
		if (null != mSharedPre) {
			return mSharedPre.getLong(preName, defaultValue);
		} else {
			return 0;
		}
	}

	/**
	 * Set data for Float
	 *
	 * @param preName
	 *            Preferences name
	 * @param value
	 *            Float input
	 */
	public synchronized static void setPreferences(final String preName,
			final float value) {
		if (null != mEditor) {
			mEditor.putFloat(preName, value);
			mEditor.commit();
		}
	}


    /**
     * Set data for double
     *
     * @param preName
     *            Preferences name
     * @param value
     *            Float input
     */
    public synchronized static void setPreferences(final String preName,
                                                   final double value) {
        if (null != mEditor) {
            mEditor.putLong(preName, Double.doubleToRawLongBits(value));
            mEditor.commit();
        }
    }

    /**
     * Get data for double
     *
     * @param preName
     *            Preferences name
     * @return Float or 0 if Name not existed
     */
    public static double getPreferences(final String preName,
                                       final double defaultValue) {
        if (null != mSharedPre) {

            return Double.longBitsToDouble(mSharedPre.getLong(preName, 0));
        } else {
            return defaultValue;
        }

    }

	/**
	 * Get data for Float
	 *
	 * @param preName
	 *            Preferences name
	 * @return Float or 0 if Name not existed
	 */
	public static float getPreferences(final String preName,
			final float defaultValue) {
		if (null != mSharedPre) {
			return mSharedPre.getFloat(preName, defaultValue);
		} else {
			return 0;
		}

	}
}
