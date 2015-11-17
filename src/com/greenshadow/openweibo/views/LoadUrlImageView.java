package com.greenshadow.openweibo.views;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.greenshadow.openweibo.BitmapCache;
import com.greenshadow.openweibo.CustomApplication;
import com.greenshadow.openweibo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LoadUrlImageView extends ImageView {
	public LoadUrlImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LoadUrlImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadUrlImageView(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
	}

	public void setUrl(final String url) {
		ImageLoader imageLoader = new ImageLoader(CustomApplication.getHttpQuenes(), BitmapCache.getInstance());
		ImageListener listener = ImageLoader.getImageListener(this, R.drawable.image_loading, R.drawable.ic_launcher);
		imageLoader.get(url, listener);
	}
}
