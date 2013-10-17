package com.tonycube.googlemapdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Google Map and GPS Demo
 * @author Tony
 * @date 2013/6/7
 * @version 1.1
 *
 */
public class MainActivity extends Activity {
	private final String TAG = "=== Map Demo ==>";
	
	/**�x�_101*/
	final LatLng TAIPEI101 = new LatLng(25.033611, 121.565000);
	/**�x�_������*/
	final LatLng TAIPEI_TRAIN_STATION = new LatLng(25.047924, 121.517081);
	/**��ߥx�W�ժ��]*/
	final LatLng NATIONAL_TAIWAN_MUSEUM = new LatLng(25.042902, 121.515030);
	/**���B*/
	final LatLng KENTING = new LatLng(21.946567, 120.798713);
	/**����*/
	final LatLng ZINTUN = new LatLng(23.851676, 120.902008);
	
	/** Map */
	private GoogleMap mMap;
	private TextView txtOutput;
	private Marker markerMe;

	/** �O���y�� */
	private ArrayList<LatLng> traceOfMe;

	/** GPS */
	private LocationManager locationMgr;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initView();
		initMap();
		if (initLocationProvider()) {
			whereAmI();
		}else{
			txtOutput.setText("�ж}�ҩw��I");
		}
	}


	@Override
	protected void onStop() {
		locationMgr.removeUpdates(locationListener);
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initMap();
		drawPolyline();
	}
	
	private void initView(){
		txtOutput = (TextView) findViewById(R.id.txtOutput);
	}

	
	/************************************************
	 * 
	 * 						Map����
	 * 
	 ***********************************************/
	/**
	 * Map��l��
	 * �إ�3�ӼаO
	 */
	private void initMap(){
		if (mMap == null) {
			mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			if (mMap != null) {
				//�]�w�a������
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				
				//Marker1
				MarkerOptions markerOpt = new MarkerOptions();
				markerOpt.position(TAIPEI101);
				markerOpt.title("�x�_101");
				markerOpt.snippet("��1999�~�ʤu�A2004�~12��31�駹�u�ҥΡA�Ӱ�509.2���ءC");
				markerOpt.draggable(false);
				markerOpt.visible(true);
				markerOpt.anchor(0.5f, 0.5f);//�]���Ϥ�����
				markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
				
				mMap.addMarker(markerOpt);
				
				//Marker2
				MarkerOptions markerOpt2 = new MarkerOptions();
				markerOpt2.position(TAIPEI_TRAIN_STATION);
				markerOpt2.title("�x�_������");
				
				mMap.addMarker(markerOpt2);
				
				//Marker3
				MarkerOptions markerOpt3 = new MarkerOptions();
				markerOpt3.position(NATIONAL_TAIWAN_MUSEUM);
				markerOpt3.title("��ߥx�W�ժ��]");
				markerOpt3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				mMap.addMarker(markerOpt3);
			}
		}
		
	}
	
	/**
	 * �e�u
	 */
	private void drawPolyline(){
		PolylineOptions polylineOpt = new PolylineOptions();
		polylineOpt.add(new LatLng(25.033611, 121.565000));
		polylineOpt.add(new LatLng(25.032728, 121.565137));
		polylineOpt.add(new LatLng(25.033739, 121.527886));
		polylineOpt.add(new LatLng(25.038716, 121.517758));
		polylineOpt.add(new LatLng(25.045656, 121.519636));
		polylineOpt.add(new LatLng(25.046200, 121.517533));
		
		polylineOpt.color(Color.BLUE);
		
		Polyline polyline = mMap.addPolyline(polylineOpt);
		polyline.setWidth(10);
	}
	
	/**
	 * ���s:������v������B
	 * @param v
	 */
	public void moveOnClick(View v){
		//move camera
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KENTING, 15));
	}
	
	/**
	 * ���s:��j�a��
	 * @param v
	 */
	public void zoomInOnClick(View v){
		//zoom in
		mMap.animateCamera(CameraUpdateFactory.zoomIn());
	}
	
	/**
	 * ���s:�Y�p�a��
	 * @param v
	 */
	public void zoomToOnClick(View v){
		//zoom to level 10, animating with a duration of 3 seconds
		mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 3000, null);
	}

	/**
	 * ���s:��v�����ʨ����
	 * @param v
	 */
	public void animToOnClick(View v){
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(ZINTUN)      		// Sets the center of the map to ZINTUN
	    .zoom(13)                   // Sets the zoom
	    .bearing(90)                // Sets the orientation of the camera to east
	    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	    .build();                   // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	/************************************************
	 * 
	 * 						GPS����
	 * 
	 ***********************************************/
	/**
	 * GPS��l�ơA���o�i�Ϊ���m���Ѿ�
	 * @return
	 */
	private boolean initLocationProvider() {
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//1.��̨ܳδ��Ѿ�
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		
//		provider = locationMgr.getBestProvider(criteria, true);
//		
//		if (provider != null) {
//			return true;
//		}
		
		
		
		//2.��ܨϥ�GPS���Ѿ�
		if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			return true;
		}
		
		
		
		//3.��ܨϥκ������Ѿ�
//		if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//			provider = LocationManager.NETWORK_PROVIDER;
//			return true;
//		}
		
		return false;
	}
	
	/**
	 * ����"��"�b����
	 * 1.�إߦ�m���ܰ�ť��
	 * 2.�w����ܤW�����w����m
	 */
	private void whereAmI(){
//		String provider = LocationManager.GPS_PROVIDER;
		
		//���o�W���w������m
		Location location = locationMgr.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		
		//GPS Listener
		locationMgr.addGpsStatusListener(gpsListener);
		
		
		//Location Listener
		long minTime = 5000;//ms
		float minDist = 5.0f;//meter
		locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);
	}
	
	/**
	 * ���"��"�b����
	 * @param lat
	 * @param lng
	 */
	private void showMarkerMe(double lat, double lng){
		if (markerMe != null) {
			markerMe.remove();
		}
		
		MarkerOptions markerOpt = new MarkerOptions();
		markerOpt.position(new LatLng(lat, lng));
		markerOpt.title("�ڦb�o��");
		markerMe = mMap.addMarker(markerOpt);
		
		Toast.makeText(this, "lat:" + lat + ",lng:" + lng, Toast.LENGTH_SHORT).show();
	}
	
	private void cameraFocusOnMe(double lat, double lng){
		CameraPosition camPosition = new CameraPosition.Builder()
										.target(new LatLng(lat, lng))
										.zoom(16)
										.build();
		
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
	}
	
	private void trackToMe(double lat, double lng){
		if (traceOfMe == null) {
			traceOfMe = new ArrayList<LatLng>();
		}
		traceOfMe.add(new LatLng(lat, lng));
		
		PolylineOptions polylineOpt = new PolylineOptions();
		for (LatLng latlng : traceOfMe) {
			polylineOpt.add(latlng);
		}
		
		polylineOpt.color(Color.RED);
		
		Polyline line = mMap.addPolyline(polylineOpt);
		line.setWidth(10);
	}
	
	/**
	 * ��s����ܷs��m
	 * @param location
	 */
	private void updateWithNewLocation(Location location) {
		String where = "";
		if (location != null) {
			//�g��
			double lng = location.getLongitude();
			//�n��
			double lat = location.getLatitude();
			//�t��
			float speed = location.getSpeed();
			//�ɶ�
			long time = location.getTime();
			String timeString = getTimeString(time);
			
			where = "�g��: " + lng + 
					"\n�n��: " + lat + 
					"\n�t��: " + speed + 
					"\n�ɶ�: " + timeString +
					"\nProvider: " + provider;
			
			//�аO"��"
			showMarkerMe(lat, lng);
			cameraFocusOnMe(lat, lng);
			trackToMe(lat, lng);
			
			//������v�����"��"
//			CameraPosition cameraPosition = new CameraPosition.Builder()
//		    .target(new LatLng(lat, lng))      		// Sets the center of the map to ZINTUN
//		    .zoom(13)                   // Sets the zoom
//		    .bearing(90)                // Sets the orientation of the camera to east
//		    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//		    .build();                   // Creates a CameraPosition from the builder
//			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
//			CameraPosition camPosition = new CameraPosition.Builder()
//											.target(new LatLng(lat, lng))
//											.zoom(16)
//											.build();
//
//			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
			
		}else{
			where = "No location found.";
		}
		
		//��m�������
		txtOutput.setText(where);
	}
	
	
	GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
	        case GpsStatus.GPS_EVENT_STARTED:
	        	Log.d(TAG, "GPS_EVENT_STARTED");
	        	Toast.makeText(MainActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
	            break;

	        case GpsStatus.GPS_EVENT_STOPPED:
	        	Log.d(TAG, "GPS_EVENT_STOPPED");
	        	Toast.makeText(MainActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
	            break;

	        case GpsStatus.GPS_EVENT_FIRST_FIX:
	        	Log.d(TAG, "GPS_EVENT_FIRST_FIX");
	        	Toast.makeText(MainActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
	            break;

	        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	        	Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
	            break;
			}
		}
	};
	
	
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
		    case LocationProvider.OUT_OF_SERVICE:
		        Log.v(TAG, "Status Changed: Out of Service");
		        Toast.makeText(MainActivity.this, "Status Changed: Out of Service",
		                Toast.LENGTH_SHORT).show();
		        break;
		    case LocationProvider.TEMPORARILY_UNAVAILABLE:
		        Log.v(TAG, "Status Changed: Temporarily Unavailable");
		        Toast.makeText(MainActivity.this, "Status Changed: Temporarily Unavailable",
		                Toast.LENGTH_SHORT).show();
		        break;
		    case LocationProvider.AVAILABLE:
		        Log.v(TAG, "Status Changed: Available");
		        Toast.makeText(MainActivity.this, "Status Changed: Available",
		                Toast.LENGTH_SHORT).show();
		        break;
		    }
		}
		
	};
	
	private String getTimeString(long timeInMilliseconds){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timeInMilliseconds);
	}
	
	
//	private boolean checkGooglePlayServices(){
//		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//		switch (result) {
//			case ConnectionResult.SUCCESS:
//				Log.d(TAG, "SUCCESS");
//				return true;
//	
//			case ConnectionResult.SERVICE_INVALID:
//				Log.d(TAG, "SERVICE_INVALID");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_INVALID, this, 0).show();
//				break;
//				
//			case ConnectionResult.SERVICE_MISSING:
//				Log.d(TAG, "SERVICE_MISSING");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_MISSING, this, 0).show();
//				break;
//				
//			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
//				Log.d(TAG, "SERVICE_VERSION_UPDATE_REQUIRED");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, this, 0).show();
//				break;
//				
//			case ConnectionResult.SERVICE_DISABLED:
//				Log.d(TAG, "SERVICE_DISABLED");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_DISABLED, this, 0).show();
//				break;
//		}
//		
//		return false;
//	}
}
