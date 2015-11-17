package com.greenshadow.openweibo.views;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.greenshadow.openweibo.sdk.Constants;
import com.greenshadow.openweibo.utils.PixelUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.models.User;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UserDialog extends AlertDialog {
	private User user;

	private ProgressBar loading;
	private LinearLayout userRoot;
	private LoadUrlImageView avator;
	private ImageView sexImg;
	private TextView nickName, city, relationship, weiboCount, followCount, fansCount, description;

	public UserDialog(Context context, User user) {
		super(context);
		this.user = user;
	}

	public UserDialog(Context context, String screenName) {
		super(context);
		String url = "https://api.weibo.com/2/users/show.json";
		Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(context.getApplicationContext());
		WeiboParameters mParams = new WeiboParameters(Constants.APP_KEY);
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("screen_name", screenName);
		new AsyncWeiboRunner(context).requestAsync(url, mParams, "GET", new RequestListener() {
			@Override
			public void onComplete(String response) {
				if (!TextUtils.isEmpty(response)) {
					User u = User.parse(response);
					if (u != null) {
						user = u;
						bindUserToView();
					} else
						Log.e("USER", response);
				}
			}

			@Override
			public void onWeiboException(WeiboException e) {
				Log.e("USER", e.toString());
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_user);
		init();
		if (user != null)
			bindUserToView();
		getWindow().setLayout(PixelUtil.dp2px(300), -2);
	}

	private void init() {
		loading = (ProgressBar) findViewById(R.id.pb_user_loading);
		userRoot = (LinearLayout) findViewById(R.id.ll_user_root);
		avator = (LoadUrlImageView) findViewById(R.id.iv_avatar);
		sexImg = (ImageView) findViewById(R.id.iv_sex);
		nickName = (TextView) findViewById(R.id.tv_nick_name);
		city = (TextView) findViewById(R.id.tv_city);
		relationship = (TextView) findViewById(R.id.tv_relationship);
		weiboCount = (TextView) findViewById(R.id.tv_weibo_count);
		followCount = (TextView) findViewById(R.id.tv_follow_count);
		fansCount = (TextView) findViewById(R.id.tv_fans_count);
		description = (TextView) findViewById(R.id.tv_description);
	}

	private void bindUserToView() {
		avator.setUrl(user.avatar_hd);
		String sex = user.gender;
		if (sex.equals("m"))
			sexImg.setImageResource(R.drawable.male);
		else if (sex.equals("f"))
			sexImg.setImageResource(R.drawable.female);
		else
			sexImg.setVisibility(View.GONE);
		nickName.setText(user.screen_name);
		city.setText(user.location);
		if (user.following) {
			if (user.follow_me)
				relationship.setText("互相关注");
			else
				relationship.setText("我关注TA");
		} else {
			if (user.follow_me)
				relationship.setText("TA关注我");
			else
				relationship.setText("还不认识");
		}
		weiboCount.setText(user.statuses_count + "");
		followCount.setText(user.friends_count + "");
		fansCount.setText(user.followers_count + "");
		if (TextUtils.isEmpty(user.description))
			description.setText("还没有简介");
		else
			description.setText(user.description);

		loading.setVisibility(View.GONE);
		userRoot.setVisibility(View.VISIBLE);
	}
}
