package com.greenshadow.openweibo.ui;

import java.util.ArrayList;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.adapters.WeiboCommentListAdapter;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.greenshadow.openweibo.sdk.Constants;
import com.greenshadow.openweibo.views.ListFooterView;
import com.greenshadow.openweibo.views.ListFooterView.LoadMoreListener;
import com.greenshadow.openweibo.views.TitleBar;
import com.greenshadow.openweibo.views.WeiboView;
import com.greenshadow.openweibo.views.WeiboView.OnItemsClickListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class WeiboContentActivity extends Activity
		implements RequestListener, OnRefreshListener, LoadMoreListener, OnItemClickListener {
	private TitleBar titleBar;
	private ListView mList;
	private WeiboCommentListAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ArrayList<Comment> mData;
	private LinearLayout headerView;
	private WeiboView weibo;
	private ListFooterView footerView;
	private LinearLayout forward, comment;

	protected long sinceId = 0l;
	protected long maxId = 0l;
	protected boolean isRefresh = true;

	private Status status;

	private Oauth2AccessToken mAccessToken;

	private String url = "https://api.weibo.com/2/comments/show.json";
	WeiboParameters mParams;
	private String weiboId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_content);
		mAccessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
		mData = new ArrayList<Comment>();
		mParams = new WeiboParameters(Constants.APP_KEY);
		mParams.put("access_token", mAccessToken.getToken());
		init();
	}

	private void init() {
		Intent i = getIntent();
		status = (Status) i.getSerializableExtra("status");
		if (status == null) {
			finish();
			return;
		}

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setTitle("微博详情");
		titleBar.setBackClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		mSwipeRefreshLayout.setOnRefreshListener(this);

		mList = (ListView) findViewById(R.id.lv_swipe_refresh);
		mAdapter = new WeiboCommentListAdapter(this, mData);
		mList.setAdapter(mAdapter);

		headerView = (LinearLayout) LinearLayout.inflate(this, R.layout.header_weibo_content, null);
		weibo = (WeiboView) headerView.findViewById(R.id.weibo_view);
		weibo.setStatus(status);
		weibo.setOnItemsClickListener(new OnItemsClickListener() {
			@Override
			public void toSource() {
				if (status.retweeted_status == null)
					return;
				Intent i = new Intent(WeiboContentActivity.this, WeiboContentActivity.class);
				i.putExtra("status", status.retweeted_status);
				startActivity(i);
			}
		});
		weiboId = status.id;
		footerView = new ListFooterView(this);
		footerView.setLoadMoreListener(this);

		mList.addHeaderView(headerView, null, false);
		mList.addFooterView(footerView);
		mList.setOnItemClickListener(this);

		forward = (LinearLayout) findViewById(R.id.btn_forward);
		comment = (LinearLayout) findViewById(R.id.btn_comment);
		forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(WeiboContentActivity.this, ForwardCommentActivity.class);
				i.putExtra("status", status);
				i.putExtra("todo", ForwardCommentActivity.TO_FORWARD);
				startActivity(i);
			}
		});
		comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(WeiboContentActivity.this, ForwardCommentActivity.class);
				i.putExtra("status", status);
				i.putExtra("todo", ForwardCommentActivity.TO_COMMENT);
				startActivity(i);
			}
		});

		onRefresh();
		mSwipeRefreshLayout.setRefreshing(true);
	}

	@Override
	public void onRefresh() {
		mParams.put("id", weiboId);
		mParams.put("since_id", sinceId);
		mParams.put("max_id", 0l);
		mParams.put("uid", Long.parseLong(mAccessToken.getUid()));
		isRefresh = true;
		new AsyncWeiboRunner(this).requestAsync(url, mParams, "GET", this);
	}

	@Override
	public void onLodadMore() {
		mParams.put("id", weiboId);
		mParams.put("since_id", 0l);
		mParams.put("max_id", maxId);
		mParams.put("uid", Long.parseLong(mAccessToken.getUid()));
		isRefresh = false;
		new AsyncWeiboRunner(this).requestAsync(url, mParams, "GET", this);
	}

	// ===========WeiboAPI监听接口============
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
					if (!isRefresh)
						Toast.makeText(this, "没有更多了", Toast.LENGTH_LONG).show();
				}
			}
		} else {
			Toast.makeText(this, "服务器没有响应", Toast.LENGTH_LONG).show();
			return;
		}

		if (mData.size() > 0) {
			sinceId = Long.parseLong(mData.get(0).id) + 1l;
			maxId = Long.parseLong(mData.get(mData.size() - 1).id) - 1l;
		}
		if (!isRefresh)
			footerView.setLoading(false);

		mSwipeRefreshLayout.setRefreshing(false);
		mAdapter.notifyDataSetChanged();
		mList.invalidateViews();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(this, "ERROR:" + e.toString(), Toast.LENGTH_LONG).show();
		mSwipeRefreshLayout.setRefreshing(false);
	}
	// ===========WeiboAPI监听接口============

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(this, ReplyActivity.class);
		i.putExtra("id", status.id);
		i.putExtra("cid", mData.get(position).id);
		startActivity(i);
	}
}
