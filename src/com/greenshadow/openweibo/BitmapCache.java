package com.greenshadow.openweibo;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache implements ImageCache {
	private static BitmapCache mInstance;

	private LruCache<String, Bitmap> cache;
	private static final int maxSize = 10 * 1024 * 1024; // 缓存大小为10M

	private BitmapCache() {
		cache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	public static BitmapCache getInstance() {
		if (mInstance == null) {
			synchronized (BitmapCache.class) {
				if (mInstance == null) {
					mInstance = new BitmapCache();
				}
			}
		}
		return mInstance;
	}

	@Override
	public Bitmap getBitmap(String key) {
		return cache.get(key);
	}

	@Override
	public void putBitmap(String key, Bitmap value) {
		cache.put(key, value);
	}
}
