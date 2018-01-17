package com.vouchify.vouchify.utility;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.vouchify.vouchify.utility.utiInterface.GPSTrackerLocationChangeCallBack;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; // 0 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000*10; // 0 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;
	private GPSTrackerLocationChangeCallBack mGPSTrackerLocationChange;
	public GPSTracker(Context context,
			GPSTrackerLocationChangeCallBack gPSTrackerLocationChange) {

		this.mContext = context;
		mGPSTrackerLocationChange = gPSTrackerLocationChange;

		getLocation();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(context,
					Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(context,
							Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

				return;
			}
		}

		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	public Location getLocation() {

		try {

			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {

				return location;
			}

            Criteria myCriteria = new Criteria();
            myCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
            myCriteria.setPowerRequirement(Criteria.POWER_LOW);
            String myProvider = locationManager.getBestProvider(myCriteria, true);
			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {

				this.canGetLocation = true;
				if (location == null) {

                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return location;
                    }

					locationManager.requestLocationUpdates(
                            myProvider, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					LogUtil.e("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {

						location = locationManager
								.getLastKnownLocation(myProvider);

						if (location != null) {

							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}

			// First get location from Network Provider
			if (isNetworkEnabled) {

				if (location == null) {

					if (ActivityCompat.checkSelfPermission(mContext,
							Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(mContext,
									Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

						return location;
					}


					locationManager.requestLocationUpdates(
                            myProvider,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {

						location = locationManager
								.getLastKnownLocation(myProvider);
						if (location != null) {

							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return location;
	}

	// /**
	// * Stop using GPS listener Calling this function will stop using GPS in
	// your
	// * app
	// * */
	// public void stopUsingGPS() {
	//
	// if (locationManager != null) {
	//
	// locationManager.removeUpdates(GPSTracker.this);
	// }
	//
	// }

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {

		if (location != null) {

			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {

		if (location != null) {

			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }

		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// getting network status
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGPSEnabled && !isNetworkEnabled) {

			return false;
		} else {

			if (!this.canGetLocation) {

				if (location == null) {

					this.canGetLocation = true;
					if (ActivityCompat.checkSelfPermission(mContext,
							Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(mContext,
									Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

						return false;
					}

                    Criteria myCriteria = new Criteria();
                    myCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    myCriteria.setPowerRequirement(Criteria.POWER_LOW);
                    String myProvider = locationManager.getBestProvider(myCriteria, true);
					locationManager.requestLocationUpdates(
                            myProvider, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					LogUtil.e("canGetLocation GPS Enabled", "GPS Enabled");
					if (locationManager != null) {

						location = locationManager
								.getLastKnownLocation(myProvider);

						if (location != null) {

							latitude = location.getLatitude();
							longitude = location.getLongitude();

						}
					}

					if (isNetworkEnabled) {

						if (location == null) {

							locationManager.requestLocationUpdates(
                                    myProvider,
									MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
							if (locationManager != null) {

								location = locationManager
										.getLastKnownLocation(myProvider);
								if (location != null) {

									latitude = location.getLatitude();
									longitude = location.getLongitude();
								}
							}
						}
					}
				}

			}

			return true;
		}

	}

	// /**
	// * Function to show settings alert dialog On pressing Settings button will
	// * lauch Settings Options
	// * */
	// public void showSettingsAlert() {
	//
	// AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	//
	// // Setting Dialog Title
	// alertDialog.setTitle("GPS is settings");
	//
	// // Setting Dialog Message
	// alertDialog
	// .setMessage("GPS is not enabled. Do you want to go to settings menu?");
	//
	// // On pressing Settings button
	// alertDialog.setPositiveButton("Settings",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	//
	// Intent intent = new Intent(
	// Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	// mContext.startActivity(intent);
	// }
	// });
	//
	// // on pressing cancel button
	// alertDialog.setNegativeButton("Cancel",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	//
	// dialog.cancel();
	// }
	// });
	//
	// // Showing Alert Message
	// alertDialog.show();
	// }

	@Override
	public void onLocationChanged(Location location) {

		this.canGetLocation = true;

		this.mGPSTrackerLocationChange.locationChange(location);

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
