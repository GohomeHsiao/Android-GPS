package com.example.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager lms;
	private boolean getService = false; // 是否已開啟定位服務

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView dis = (TextView) findViewById(R.id.dis);		
		dis.setText(distance(24.176776,120.650227, 24.17901,120.650315) + " 公尺"); 
		// 經緯度越精確 距離越準		
		// https://maps.google.com.tw/maps?saddr=24.176776,120.650227&daddr=24.17901,120.650315&hl=zh-TW&sll=24.178469,120.650761&sspn=0.002195,0.004128&geocode=FYjocAEd8_kwBw%3BFULxcAEdS_owBw&brcurrent=3,0x34691605dc29179f:0xec9a9bf0eefd141e,0,0x346917dff97922ef:0x87523ee47ea6447f&mra=ls&t=m&z=18

		// 取得系統定位服務
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// 如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
			getService = true;
			locationServiceInitial();
		} else {
			Toast.makeText(MainActivity.this, "請開啟定位服務", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 開啟設定頁面
		}

	}

	private void locationServiceInitial() {
		lms = (LocationManager) getSystemService(LOCATION_SERVICE); // 取得系統定位服務
		Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 使用GPS定位座標
		getLocation(location);
	}

	private void getLocation(Location location) { // 將定位資訊顯示在畫面中
		if (location != null) {
			TextView longitude_txt = (TextView) findViewById(R.id.longitude);
			TextView latitude_txt = (TextView) findViewById(R.id.latitude);

			Double longitude = location.getLongitude(); // 取得經度
			Double latitude = location.getLatitude(); // 取得緯度

			longitude_txt.setText(String.valueOf(longitude));
			latitude_txt.setText(String.valueOf(latitude));
		} else {
			Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			getLocation(location);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public double distance(double n1, double e1, double n2, double e2)  
    {  
        double jl_jd = 102834.74258026089786013677476285;  
        double jl_wd = 111712.69150641055729984301412873;  
        double b = Math.abs((e1 - e2) * jl_jd);  
        double a = Math.abs((n1 - n2) * jl_wd);  
        return Math.sqrt((a * a + b * b)); 
    } 

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (getService) {
			lms.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
			// 服務提供者、更新頻率1000毫秒=1秒鐘、最短距離、地點改變時呼叫物件
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (getService) {
			lms.removeUpdates(locationListener); // 離開頁面時停止更新
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
