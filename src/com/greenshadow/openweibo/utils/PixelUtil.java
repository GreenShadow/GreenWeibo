package com.greenshadow.openweibo.utils;

import com.greenshadow.openweibo.CustomApplication;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class PixelUtil {

	private static Context mContext = CustomApplication.getInstance();

	public static DisplayMetrics getScreenSize() {
		return mContext.getResources().getDisplayMetrics();
	}

	public static int dp2px(float value) {
		final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
		return (int) (value * (scale / 160) + 0.5f);
	}

	public static int sp2px(float value) {
		Resources r;
		if (mContext == null) {
			r = Resources.getSystem();
		} else {
			r = mContext.getResources();
		}
		float spvalue = value * r.getDisplayMetrics().scaledDensity;
		return (int) (spvalue + 0.5f);
	}
}
