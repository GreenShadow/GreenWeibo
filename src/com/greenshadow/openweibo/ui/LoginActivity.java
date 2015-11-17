package com.greenshadow.openweibo.ui;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.greenshadow.openweibo.sdk.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity implements WeiboAuthListener {
	private Button btn_login;
	private AuthInfo mAuthinfo;
	private SsoHandler mSsoHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mAuthinfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
		mSsoHandler = new SsoHandler(this, mAuthinfo);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSsoHandler.authorize(LoginActivity.this);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null)
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	}

	@Override
	public void onComplete(Bundle bundle) {
		Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
		if (mAccessToken.isSessionValid()) {
			AccessTokenKeeper.writeAccessToken(getApplicationContext(), mAccessToken);
			Toast.makeText(this, "登陆成功", Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, HomeActivity.class));
			this.finish();
		} else
			Toast.makeText(this, "授权失败：\n错误代码：" + bundle.getString("code"), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCancel() {
		Toast.makeText(this, "授权取消", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(this, "授权失败\n错误信息：" + e.getMessage(), Toast.LENGTH_LONG).show();
	}
}
