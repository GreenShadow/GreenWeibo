package com.greenshadow.openweibo.ui;

import java.io.FileNotFoundException;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.greenshadow.openweibo.sdk.Constants;
import com.greenshadow.openweibo.ui.fragments.WeiboListFragment;
import com.greenshadow.openweibo.ui.fragments.PublicFragment;
import com.greenshadow.openweibo.ui.fragments.CommentToMeFragment;
import com.greenshadow.openweibo.views.LoadUrlImageView;
import com.greenshadow.openweibo.views.TitleBarMain;
import com.greenshadow.openweibo.views.UserDialog;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.models.User;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity implements RequestListener, OnClickListener {
	private static final int SEND_WEIBO = 0;

	private WeiboListFragment homeFragment, mentionFragment;
	private PublicFragment publicFragment;
	private CommentToMeFragment commentFragment;
	private FragmentManager fm;

	private DrawerLayout mDrawerLayout;
	private TitleBarMain titleBar;

	private Oauth2AccessToken mAccessToken;
	private WeiboParameters mParams;
	private NotificationManager nm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mAccessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
		mParams = new WeiboParameters(Constants.APP_KEY);
		mParams.put("access_token", mAccessToken.getToken());

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		titleBar = (TitleBarMain) findViewById(R.id.title_bar_main);

		fm = getSupportFragmentManager();
		homeFragment = new WeiboListFragment("https://api.weibo.com/2/statuses/friends_timeline.json");
		publicFragment = new PublicFragment();
		commentFragment = new CommentToMeFragment();
		mentionFragment = new WeiboListFragment("https://api.weibo.com/2/statuses/mentions.json");

		titleBar.setDrawerLayout(mDrawerLayout);
		titleBar.setSendWeiboListener(this);
		addClickEvent();

		showUser();

		attachNewFragment(homeFragment, "Main");
		titleBar.setTitle("首页");
	}

	private void showUser() {
		String url = "https://api.weibo.com/2/users/show.json";
		WeiboParameters mParams = new WeiboParameters(Constants.APP_KEY);
		mParams.put("access_token", mAccessToken.getToken());
		mParams.put("uid", Long.parseLong(mAccessToken.getUid()));
		new AsyncWeiboRunner(this).requestAsync(url, mParams, "GET", this);
	}

	private void addClickEvent() {
		findViewById(R.id.btn_home).setOnClickListener(this);
		findViewById(R.id.btn_hot).setOnClickListener(this);
		findViewById(R.id.btn_comment_to_me).setOnClickListener(this);
		findViewById(R.id.btn_mention).setOnClickListener(this);
		findViewById(R.id.btn_exit).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
			mDrawerLayout.closeDrawer(GravityCompat.START);
		switch (v.getId()) {
		case R.id.btn_title_bar_main_send_weibo:
			Intent i = new Intent(this, SendWeiboActivity.class);
			startActivityForResult(i, SEND_WEIBO);
			break;
		case R.id.btn_home:
			attachNewFragment(homeFragment, "Main");
			titleBar.setTitle("首页");
			break;
		case R.id.btn_hot:
			attachNewFragment(publicFragment, "Public");
			titleBar.setTitle("公共");
			break;
		case R.id.btn_comment_to_me:
			attachNewFragment(commentFragment, "Comment");
			titleBar.setTitle("评论");
			break;
		case R.id.btn_mention:
			attachNewFragment(mentionFragment, "Mention");
			titleBar.setTitle("@我");
			break;
		case R.id.btn_exit:
			AccessTokenKeeper.clear(getApplicationContext());
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		}
	}

	private void attachNewFragment(Fragment f, String tag) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.ll_fragment_container, f, tag);
		ft.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			titleBar.openMenu();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SEND_WEIBO) {
				String url;
				String text = data.getStringExtra("content");
				mParams.put("status", text);

				Uri uri = data.getData();

				if (uri != null) {
					url = "https://upload.api.weibo.com/2/statuses/upload.json";
					Bitmap image = null;
					try {
						image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (image != null)
						mParams.put("pic", image);
					else {
						Toast.makeText(this, "获取图片失败", Toast.LENGTH_LONG).show();
						return;
					}
				} else
					url = "https://api.weibo.com/2/statuses/update.json";

				Notification n = new Notification.Builder(this) //
						.setSmallIcon(R.drawable.ic_launcher) //
						.setContentTitle("GreenWeibo") //
						.setContentText("正在发送微博") //
						.getNotification();

				n.flags |= Notification.FLAG_ONGOING_EVENT;
				nm.notify(0, n);

				new AsyncWeiboRunner(this).requestAsync(url, mParams, "POST", new RequestListener() {
					@Override
					public void onComplete(String response) {
						if (!TextUtils.isEmpty(response)) {
							Toast.makeText(HomeActivity.this, "发送成功", Toast.LENGTH_LONG).show();
							nm.cancel(0);
						}
					}

					@Override
					public void onWeiboException(WeiboException e) {
						Toast.makeText(HomeActivity.this, "ERROR:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	}

	@Override
	public synchronized void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			if (response.startsWith("{\"id\":")) {
				final User user = User.parse(response);
				if (user != null) {
					((LoadUrlImageView) findViewById(R.id.iv_avatar)).setUrl(user.avatar_large);
					((TextView) findViewById(R.id.tv_nick_name)).setText(user.screen_name);
					findViewById(R.id.ll_drawer_user).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							new UserDialog(HomeActivity.this, user).show();
						}
					});
				}
			}
		}
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(this, "ERROR:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
	}
}
