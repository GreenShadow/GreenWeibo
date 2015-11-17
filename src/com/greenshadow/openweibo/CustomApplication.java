package com.greenshadow.openweibo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class CustomApplication extends Application {
	private static CustomApplication mInstance;
	private static RequestQueue quenes;

	public static CustomApplication getInstance() {
		return mInstance;
	}

	public static RequestQueue getHttpQuenes() {
		return quenes;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		quenes = Volley.newRequestQueue(getApplicationContext());
	}

	public int getAppversion() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
