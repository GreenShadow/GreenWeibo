package com.greenshadow.openweibo.adapters;

import java.util.ArrayList;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.ui.ForwardCommentActivity;
import com.greenshadow.openweibo.ui.WeiboContentActivity;
import com.greenshadow.openweibo.views.NumberImageButton;
import com.greenshadow.openweibo.views.WeiboView;
import com.greenshadow.openweibo.views.WeiboView.OnItemsClickListener;
import com.sina.weibo.sdk.openapi.models.Status;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class WeiboListAdapter extends AbsBaseListAdapter<Status> {
	public WeiboListAdapter(Context context, ArrayList<Status> list) {
		super(context, list);
	}

	@SuppressLint("InflateParams")
	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		WeiboHolder weiboHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_weibo, null);

			weiboHolder = new WeiboHolder();

			weiboHolder.weibo = (WeiboView) convertView.findViewById(R.id.weibo_view);
			weiboHolder.forward = (NumberImageButton) convertView.findViewById(R.id.btn_forward);
			weiboHolder.comment = (NumberImageButton) convertView.findViewById(R.id.btn_comment);
			convertView.setTag(weiboHolder);
		} else
			weiboHolder = (WeiboHolder) convertView.getTag();

		// 对控件的初始化
		final Status item = list.get(position);
		weiboHolder.weibo.setStatus(item);
		weiboHolder.forward.setNumber(item.reposts_count);
		weiboHolder.comment.setNumber(item.comments_count);

		weiboHolder.weibo.setOnItemsClickListener(new OnItemsClickListener() {
			@Override
			public void toSource() {
				if (item.retweeted_status == null)
					return;
				Intent i = new Intent(mContext, WeiboContentActivity.class);
				i.putExtra("status", item.retweeted_status);
				mContext.startActivity(i);
			}
		});
		weiboHolder.forward.setOnClickListener(new OnClickListener() { // 转发
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, ForwardCommentActivity.class);
				i.putExtra("status", item);
				i.putExtra("todo", ForwardCommentActivity.TO_FORWARD);
				mContext.startActivity(i);
			}
		});
		weiboHolder.comment.setOnClickListener(new OnClickListener() { // 评论
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, ForwardCommentActivity.class);
				i.putExtra("status", item);
				i.putExtra("todo", ForwardCommentActivity.TO_COMMENT);
				mContext.startActivity(i);
			}
		});

		return convertView;
	}

	/**
	 * 使用ViewHolder优化内存，减小系统开销，使ListView滑动更流畅
	 */
	private class WeiboHolder {
		public WeiboView weibo;
		public NumberImageButton forward, comment;
	}
}
