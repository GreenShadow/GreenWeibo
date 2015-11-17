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

public class MentionFragment extends AbsListFragmentBase<Status> implements OnItemClickListener {
	private WeiboListAdapter mAdapter;

	private String url = "https://api.weibo.com/2/statuses/mentions.json";

	@Override
	protected void init() {
		mAdapter = new WeiboListAdapter(getActivity(), mData);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);

		mSwipeRefreshLayout.setRefreshing(true);
		refresh();
	}

	@Override
	protected void refresh() {
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("since_id", sinceId);
		mParams.put("max_id", 0l);
		mParams.put("count", 20);
		isRefresh = true;
		new AsyncWeiboRunner(getActivity()).requestAsync(url, mParams, "GET", this);
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

	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			StatusList statuses = StatusList.parse(response);
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
		if (position >= mData.size())
			return;

		Intent i = new Intent(getActivity(), WeiboContentActivity.class);
		i.putExtra("status", mData.get(position));
		startActivity(i);
	}
}
