package com.greenshadow.openweibo.ui;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	private static final int LOGIN = 0;
	private static final int HOME = 1;

	private static final long SPLASH_DELAY = 500; // SplashActivity停留时间

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent i = null;
			switch (msg.what) {
			case LOGIN:
				i = new Intent(SplashActivity.this, LoginActivity.class);
				break;
			case HOME:
				i = new Intent(SplashActivity.this, HomeActivity.class);
				break;
			}
			if (i != null) {
				startActivity(i);
				SplashActivity.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
		if (mAccessToken.isSessionValid())
			mHandler.sendEmptyMessageDelayed(HOME, SPLASH_DELAY);
		else
			mHandler.sendEmptyMessageDelayed(LOGIN, SPLASH_DELAY);
	}
}
