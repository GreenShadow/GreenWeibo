package com.greenshadow.openweibo.ui.fragments;

import com.greenshadow.openweibo.adapters.CommentToMeListAdapter;
import com.greenshadow.openweibo.ui.ReplyActivity;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CommentToMeFragment extends AbsListFragmentBase<Comment> implements OnItemClickListener {
	private CommentToMeListAdapter mAdapter;

	private String url = "https://api.weibo.com/2/comments/to_me.json";

	@Override
	protected void init() {
		mAdapter = new CommentToMeListAdapter(getActivity(), mData);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);

		mSwipeRefreshLayout.setRefreshing(true);
		refresh(); // 自动刷新
	}

	/**
	 * 回调方法
	 */
	@Override
	protected void refresh() {
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("max_id", maxId);
		mParams.put("since_id", 0l);
		mParams.put("count", loadCount);
		isRefresh = true; // 标志位
		new AsyncWeiboRunner(getActivity()).requestAsync(url, mParams, "GET", this);
	}

	@Override
	protected void loadMore() {
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("since_id", sinceId);
		mParams.put("max_id", 0l);
		mParams.put("count", loadCount);
		isRefresh = false;
		new AsyncWeiboRunner(getActivity()).requestAsync(url, mParams, "GET", this);
	}

	/**
	 * 回调方法
	 */
	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			CommentList comments = CommentList.parse(response);
			if (comments != null) {
				if (comments.commentList != null && comments.commentList.size() > 0) {
					if (isRefresh)
						mData.addAll(0, comments.commentList);
					else
						mData.addAll(comments.commentList);
				} else {
					if (isRefresh)
						Toast.makeText(getActivity(), "已是最新", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_LONG).show();
				}

				if (mData.size() > 0) {
					sinceId = Long.parseLong(mData.get(0).id) + 1l;
					maxId = Long.parseLong(mData.get(mData.size() - 1).id) - 1l;
				}
				mAdapter.notifyDataSetChanged();
				mList.invalidateViews();
			}
		} else
			Toast.makeText(getActivity(), "服务器没有响应", Toast.LENGTH_LONG).show();

		if (!isRefresh)
			footerView.setLoading(false);
		else
			mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getActivity(), ReplyActivity.class);
		i.putExtra("cid", mData.get(position).id); // 评论的ID
		i.putExtra("id", mData.get(position).status.id); // 微博的ID
		startActivity(i);
	}
}
