package com.greenshadow.openweibo.ui.fragments;

import java.util.ArrayList;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.greenshadow.openweibo.sdk.Constants;
import com.greenshadow.openweibo.views.ListFooterView;
import com.greenshadow.openweibo.views.ListFooterView.LoadMoreListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public abstract class AbsListFragmentBase<T> extends Fragment implements OnRefreshListener, RequestListener {
	protected Oauth2AccessToken mAccessToken;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected ListFooterView footerView;

	protected ListView mList;
	protected ArrayList<T> mData;
	protected WeiboParameters mParams;

	protected long sinceId = 0l;
	protected long maxId = 0l;
	protected boolean isRefresh = true;

	protected final int loadCount = 20;

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.swipe_refresh_list_view, container, false);
		return v;
	}

	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mInit();
		init();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		e.printStackTrace();
		Toast.makeText(getActivity(), "错误：\n" + e.getMessage(), Toast.LENGTH_LONG).show();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	private void mInit() {
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		mList = (ListView) findViewById(R.id.lv_swipe_refresh);
		mAccessToken = AccessTokenKeeper.readAccessToken(getActivity().getApplicationContext());
		mParams = new WeiboParameters(Constants.APP_KEY);
		if (mData == null)
			mData = new ArrayList<T>();

		mSwipeRefreshLayout.setOnRefreshListener(this);
		footerView = new ListFooterView(getActivity());
		footerView.setLoadMoreListener(new LoadMoreListener() {
			@Override
			public void onLodadMore() {
				loadMore();
			}
		});
		mList.addFooterView(footerView);
	}

	@Override
	public final void onRefresh() {
		if (mAccessToken != null && mAccessToken.isSessionValid())
			refresh();
	}

	protected View findViewById(int id) {
		return getView().findViewById(id);
	}

	protected abstract void refresh();

	protected abstract void loadMore();

	protected abstract void init();
}
