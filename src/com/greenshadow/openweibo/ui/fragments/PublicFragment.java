package com.greenshadow.openweibo.ui.fragments;

import com.greenshadow.openweibo.adapters.WeiboListAdapter;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.text.TextUtils;
import android.widget.Toast;

public class PublicFragment extends AbsListFragmentBase<Status> {
	protected WeiboListAdapter mAdapter;

	@Override
	protected void init() {
		mAdapter = new WeiboListAdapter(getActivity(), mData);
		mList.setAdapter(mAdapter);
		mList.removeFooterView(footerView); // 移除底部布局

		mSwipeRefreshLayout.setRefreshing(true);
		refresh();
	}

	@Override
	protected void refresh() {
		String url = "https://api.weibo.com/2/statuses/public_timeline.json";
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("count", 10);
		new AsyncWeiboRunner(getActivity()).requestAsync(url, mParams, "GET", this);
	}

	@Override
	protected void loadMore() {
	}

	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			StatusList statuses = StatusList.parse(response);
			if (statuses != null && statuses.total_number > 0) {
				if (mData.equals(statuses.statusList))
					Toast.makeText(getActivity(), "没有新的公共微博了", Toast.LENGTH_LONG).show();
				else {
					mData.clear();
					mData.addAll(statuses.statusList);
					Toast.makeText(getActivity(), "已更新", Toast.LENGTH_LONG).show();
				}
			}
		} else {
			Toast.makeText(getActivity(), "服务器没有响应", Toast.LENGTH_LONG).show();
			return;
		}
		mSwipeRefreshLayout.setRefreshing(false);
		mAdapter.notifyDataSetChanged();
		mList.invalidateViews();
	}
}
