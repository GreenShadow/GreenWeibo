package com.greenshadow.openweibo.views;

import java.util.ArrayList;

import com.greenshadow.openweibo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;

public class WeiboImageGridView extends TableLayout {
	private ArrayList<String> mUrls;
	private OnItemClickListener mListener;

	public WeiboImageGridView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public WeiboImageGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.layout_weibo_image_grid, this);
	}

	public WeiboImageGridView(Context context) {
		this(context, null);
	}

	public void setUrls(ArrayList<String> urls) {
		if (mUrls == null)
			mUrls = urls;
		else if (!mUrls.equals(urls))
			mUrls = urls;

		int i = 0;
		for (; i < mUrls.size(); i++) {
			LoadUrlImageView image = (LoadUrlImageView) findViewById(R.id.iv_weibo_image_0 + i);
			image.setVisibility(View.VISIBLE);
			image.setUrl(mUrls.get(i));
			final int position = i;
			image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListener != null)
						mListener.onItemClick(v, position);
				}
			});
		}
		if (i == 9)
			return;
		for (; i < 9; i++)
			findViewById(R.id.iv_weibo_image_0 + i).setVisibility(View.GONE);
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		mListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(View v, int position);
	}
}
