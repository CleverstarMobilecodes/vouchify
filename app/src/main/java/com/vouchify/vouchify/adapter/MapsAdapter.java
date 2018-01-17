package com.vouchify.vouchify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vouchify.vouchify.R;
import com.vouchify.vouchify.entity.BusinessEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hai Nguyen - 9/20/16.
 */

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.ViewHolder> {

	private Context mCtx;
	private List<BusinessEntity> mBusinesses;
	public MapsAdapter(Context ctx, List<BusinessEntity> businesses) {

		this.mCtx = ctx;
		this.mBusinesses = businesses;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_map, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		// holder.mapLayout.setId(ViewId.generateViewId());
		// GoogleMapOptions options = new GoogleMapOptions();
		// options.liteMode(true);
		// SupportMapFragment mapFrag = SupportMapFragment.newInstance();
		//
		// // Create the the class that implements OnMapReadyCallback and set up
		// // your map
		// //mapFrag.getMapAsync(null);
		//
		// FragmentManager fm = mFragment.getChildFragmentManager();
		// fm.beginTransaction().add(holder.mapLayout.getId(),
		// mapFrag).commit();
		// holder.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		holder.initializeMapView(mBusinesses.get(position));
		if (holder.mMap != null) {

			holder.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}

	@Override
	public int getItemCount() {
		return mBusinesses == null? 0 : mBusinesses.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder
			implements
				OnMapReadyCallback {

		@BindView(R.id.lite_listrow_map)
		MapView mapView;

		private GoogleMap mMap;
		private BusinessEntity mBusiness;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}

		@Override
		public void onMapReady(GoogleMap googleMap) {

			MapsInitializer.initialize(mCtx);
			mMap = googleMap;

			// Marker
			LatLng latLng = new LatLng(mBusiness.getLatitude(),
					mBusiness.getLongitude());
			MarkerOptions options = new MarkerOptions();
			options.position(latLng);
			mMap.addMarker(options);

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(latLng).zoom(mBusiness.getMapZoomLevel()).build();
			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}

		/**
		 * Initialises the MapView by calling its lifecycle methods.
		 */
		void initializeMapView(BusinessEntity business) {

			this.mBusiness = business;

			if (mapView != null) {
				// Initialise the MapView
				mapView.onCreate(null);
				// Set the map ready callback to receive the GoogleMap object
				mapView.getMapAsync(this);
			}
		}
	}
}
