package com.greenshadow.openweibo.adapters;

import java.util.List;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.utils.TimeUtil;
import com.greenshadow.openweibo.utils.XMLUtil;
import com.greenshadow.openweibo.views.UserView;
import com.sina.weibo.sdk.openapi.models.Comment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommentToMeListAdapter extends AbsBaseListAdapter<Comment> {
	public CommentToMeListAdapter(Context context, List<Comment> list) {
		super(context, list);
	}

	@SuppressLint("InflateParams")
	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		CommentHolder commentHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_comment_to_me, null);

			commentHolder = new CommentHolder();
			commentHolder.userView = (UserView) convertView.findViewById(R.id.user_view);
			commentHolder.comment = (TextView) convertView.findViewById(R.id.tv_weibo_comment_content);
			commentHolder.weiboContent = (TextView) convertView.findViewById(R.id.tv_comment_to_me_content);
			convertView.setTag(commentHolder);
		} else
			commentHolder = (CommentHolder) convertView.getTag();

		Comment item = list.get(position);
		if (commentHolder.avatorUrl == null) {
			commentHolder.avatorUrl = item.user.avatar_large;
			convertView.setTag(commentHolder);
		} else if (!commentHolder.avatorUrl.equals(item.user.avatar_large)) {
			commentHolder.avatorUrl = item.user.avatar_large;
			convertView.setTag(commentHolder);
		}
		commentHolder.userView.setUser(item.user, XMLUtil.getStringA(item.source), TimeUtil.getTime(item.created_at));
		commentHolder.comment.setText(item.text);
		commentHolder.weiboContent.setText("@" + item.status.user.screen_name + ":" + item.status.text);

		return convertView;
	}

	private class CommentHolder {
		public String avatorUrl;
		public UserView userView;
		public TextView comment, weiboContent;
	}
}
