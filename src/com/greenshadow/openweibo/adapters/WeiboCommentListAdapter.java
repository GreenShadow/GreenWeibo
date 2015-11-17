package com.greenshadow.openweibo.adapters;

import java.util.List;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.views.UserView;
import com.sina.weibo.sdk.openapi.models.Comment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WeiboCommentListAdapter extends AbsBaseListAdapter<Comment> {
	public WeiboCommentListAdapter(Context context, List<Comment> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		CommentHolder commentHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_weibo_comment, null);

			commentHolder = new CommentHolder();
			commentHolder.userView = (UserView) convertView.findViewById(R.id.user_view);
			commentHolder.commentContent = (TextView) convertView.findViewById(R.id.tv_comment_weibo_content);

			convertView.setTag(commentHolder);
		} else
			commentHolder = (CommentHolder) convertView.getTag();

		Comment item = list.get(position);
		commentHolder.userView.setUser(item.user, item.source, item.created_at);
		commentHolder.commentContent.setText(item.text);

		return convertView;
	}

	private class CommentHolder {
		public UserView userView;
		public TextView commentContent;
	}
}
