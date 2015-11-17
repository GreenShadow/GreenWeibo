package com.greenshadow.openweibo.ui.fragments;

import com.greenshadow.openweibo.adapters.WeiboListAdapter;
import com.greenshadow.openweibo.ui.WeiboContentActivity;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WeiboListFragment extends AbsListFragmentBase<Status> implements OnItemClickListener {
	private WeiboListAdapter mAdapter; // 适配器

	private String url; // 微博API接口地址

	public WeiboListFragment(String url) { // 构造方法
		this.url = url;
	}

	@Override
	protected void init() { // 初始化
		mAdapter = new WeiboListAdapter(getActivity(), mData);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);

		mSwipeRefreshLayout.setRefreshing(true);
		refresh(); // 自动刷新
	}

	/**
	 * 刷新事件回调 当刷新事件触发时访问该方法
	 */
	@Override
	protected void refresh() {
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("since_id", sinceId);
		mParams.put("max_id", 0l);
		mParams.put("count", 20);
		isRefresh = true; // 标志位
		new AsyncWeiboRunner(getActivity()).requestAsync(url, mParams, "GET", this); // 异步调用微博API接口
	}

	@Override
	protected void loadMore() {
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("since_id", 0l);
		mParams.put("max_id", maxId);
		mParams.put("count", loadCount);
		isRefresh = false;
		new AsyncWeiboRunner(getActivity()).requestAsync(url, mParams, "GET", this);
	}

	/**
	 * 服务器返回数据回调
	 * 
	 * @param response
	 *            服务器返回的JSON字符串
	 */
	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			StatusList statuses = StatusList.parse(response); // 将JSON字符串解析为StatusList对象
			if (statuses != null) {
				if (statuses.statusList != null && statuses.statusList.size() > 0) {
					if (isRefresh)
						mData.addAll(0, statuses.statusList);
					else
						mData.addAll(statuses.statusList);
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
				mAdapter.notifyDataSetChanged(); // 通知Adapter数据变化
				mList.invalidateViews(); // 刷新界面UI
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
		if (position >= mData.size())
			return;

		Intent i = new Intent(getActivity(), WeiboContentActivity.class);
		i.putExtra("status", mData.get(position));
		startActivity(i);
	}
}
