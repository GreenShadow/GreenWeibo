package com.greenshadow.openweibo.views;

import java.util.ArrayList;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.ui.ImageCheckActivity;
import com.greenshadow.openweibo.utils.SpannableUtil;
import com.greenshadow.openweibo.views.WeiboImageGridView.OnItemClickListener;
import com.sina.weibo.sdk.openapi.models.Status;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeiboView extends LinearLayout implements View.OnClickListener {
	private UserView userView;
	private TextView content, contentSource;
	private WeiboImageGridView images;

	private OnItemsClickListener mListener;
	private Context mContext;

	public WeiboView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.layout_weibo_view, this);
		userView = (UserView) findViewById(R.id.user_view);
		content = (TextView) findViewById(R.id.tv_weibo_content);
		contentSource = (TextView) findViewById(R.id.tv_weibo_content_source);
		images = (WeiboImageGridView) findViewById(R.id.weibo_images);
	}

	public WeiboView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeiboView(Context context) {
		this(context, null);
	}

	public void setStatus(Status status) {
		userView.setUser(status.user, status.source, status.created_at);
		ArrayList<SpannableString> ssArray = SpannableUtil.getAllSpannable(mContext, status.text);
		content.setText("");
		for (SpannableString ss : ssArray)
			content.append(ss);
		content.setMovementMethod(LinkMovementMethod.getInstance());

		if (status.retweeted_status != null) { // 如果含有转发微博
			contentSource.setVisibility(View.VISIBLE);
			contentSource.setText("@" + status.retweeted_status.user.screen_name + ":");
			ssArray = SpannableUtil.getAllSpannable(mContext, status.retweeted_status.text);
			for (SpannableString ss : ssArray)
				contentSource.append(ss);
			contentSource.setMovementMethod(LinkMovementMethod.getInstance());
			contentSource.setOnClickListener(this);
			bindImages(status.retweeted_status);
		} else {
			contentSource.setVisibility(View.GONE);
			bindImages(status);
		}
	}

	private void bindImages(final Status s) {
		if (!TextUtils.isEmpty(s.thumbnail_pic)) { // 如果含有图片
			images.setVisibility(View.VISIBLE);
			images.setUrls(s.pic_urls);
			images.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(View v, int position) {
					Intent i = new Intent(mContext, ImageCheckActivity.class);
					i.putExtra("urls", s.pic_urls);
					i.putExtra("index", position);
					mContext.startActivity(i);
				}
			});
		} else
			images.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			switch (v.getId()) {
			case R.id.tv_weibo_content_source:
				if (mListener != null)
					mListener.toSource();
				break;
			}
		}
	}

	public void setOnItemsClickListener(OnItemsClickListener l) {
		mListener = l;
	}

	public interface OnItemsClickListener {
		public void toSource();
	}
}
