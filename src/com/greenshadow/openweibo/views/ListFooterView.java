package com.greenshadow.openweibo.views;

import com.greenshadow.openweibo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListFooterView extends RelativeLayout {
	private TextView footerClickToLoad;
	private LinearLayout footerLoading;
	private LoadMoreListener mLoadMoreListener;

	public ListFooterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.footer_load_more, this);
		setOnClickListener(null);
		footerLoading = (LinearLayout) findViewById(R.id.ll_footer_loading);
		footerClickToLoad = (TextView) findViewById(R.id.tv_footer_click_to_load);
	}

	public ListFooterView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ListFooterView(Context context) {
		this(context, null);
	}

	public void setLoading(boolean isloading) {
		if (isloading) {
			footerClickToLoad.setVisibility(View.GONE);
			footerLoading.setVisibility(View.VISIBLE);
		} else {
			footerLoading.setVisibility(View.GONE);
			footerClickToLoad.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 传入参数无作用，请使用setLoadMoreListener()
	 * 
	 * @param l
	 * 
	 * @see ListFooterView#setLoadMoreListener(LoadMoreListener)
	 */
	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (footerClickToLoad.getVisibility() != View.VISIBLE)
					return;

				setLoading(true);
				if (mLoadMoreListener != null)
					mLoadMoreListener.onLodadMore();
			}
		});
	}

	/**
	 * 加载更多监听器
	 */
	public void setLoadMoreListener(LoadMoreListener l) {
		mLoadMoreListener = l;
	}

	public interface LoadMoreListener {
		public void onLodadMore();
	}
}
