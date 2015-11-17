package com.greenshadow.openweibo.views;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.utils.TimeUtil;
import com.greenshadow.openweibo.utils.XMLUtil;
import com.sina.weibo.sdk.openapi.models.User;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserView extends LinearLayout implements View.OnClickListener {
	private LoadUrlImageView avator;
	private TextView nickName, device, time;
	private Context mContext;
	private User user;

	public UserView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.layout_user_view, this);
		avator = (LoadUrlImageView) findViewById(R.id.iv_item_avatar);
		nickName = (TextView) findViewById(R.id.tv_item_nick_name);
		device = (TextView) findViewById(R.id.tv_item_device);
		time = (TextView) findViewById(R.id.tv_item_time);
	}

	public UserView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UserView(Context context) {
		this(context, null);
	}

	public void setUser(User user, String deviceString, String timeString) {
		this.user = user;
		avator.setUrl(user.avatar_large);
		nickName.setText(user.screen_name);

		if (TextUtils.isEmpty(timeString))
			device.setVisibility(View.GONE);
		else {
			device.setVisibility(View.VISIBLE);
			device.setText(XMLUtil.getStringA(deviceString));
		}

		if (TextUtils.isEmpty(deviceString))
			time.setVisibility(View.GONE);
		else {
			time.setVisibility(View.VISIBLE);
			time.setText(TimeUtil.getTime(timeString));
		}

		avator.setOnClickListener(this);
		nickName.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		new UserDialog(mContext, user).show();
	}
}
