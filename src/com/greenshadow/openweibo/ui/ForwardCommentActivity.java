package com.greenshadow.openweibo.ui;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.adapters.EmotionsGridAdapter;
import com.greenshadow.openweibo.sdk.AccessTokenKeeper;
import com.greenshadow.openweibo.sdk.Constants;
import com.greenshadow.openweibo.utils.FaceUtil;
import com.greenshadow.openweibo.views.TitleBar;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.models.Status;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ForwardCommentActivity extends Activity
		implements OnClickListener, TextWatcher, RequestListener, OnItemClickListener {
	public static final String TO_FORWARD = "转发";
	public static final String TO_COMMENT = "评论";

	private TitleBar titleBar;
	private EditText forwardContent;
	private TextView countTV;
	private CheckBox sameTime;
	private ImageButton faceKeyboard, send;
	private GridView faceGrid;
	private RelativeLayout inputRoot;
	private int count = 140;

	private Status currentStatus;
	private String todo;

	private String url; // 接口地址
	private Oauth2AccessToken mAccessToken; // 登陆信息
	private WeiboParameters mParams; // 微博参数

	private InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forward_comment);

		currentStatus = (Status) getIntent().getSerializableExtra("status");
		todo = getIntent().getStringExtra("todo");
		if (currentStatus == null || todo == null) {
			finish();
			return;
		}

		mAccessToken = AccessTokenKeeper.readAccessToken(getApplicationContext()); // 应用上下文
		mParams = new WeiboParameters(Constants.APP_KEY);
		mParams.put("access_token", mAccessToken.getToken());

		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		forwardContent = (EditText) findViewById(R.id.et_forward_content);
		countTV = (TextView) findViewById(R.id.tv_count);
		sameTime = (CheckBox) findViewById(R.id.cb_same_time);
		faceKeyboard = (ImageButton) findViewById(R.id.btn_face_keyboard);
		send = (ImageButton) findViewById(R.id.btn_send);
		faceGrid = (GridView) findViewById(R.id.gv_face);
		inputRoot = (RelativeLayout) findViewById(R.id.ll_input_root);

		if (todo.equals(TO_FORWARD)) {
			url = "https://api.weibo.com/2/statuses/repost.json";

			String text = "";
			if (currentStatus.retweeted_status != null) // 含有转发微博
				text = "//@" + currentStatus.user.screen_name + ":" + currentStatus.text;
			forwardContent.setText(text);
			count = 140 - text.length();
			if (count < 0)
				countTV.setTextColor(Color.RED);
			else
				countTV.setTextColor(Color.BLACK);
			countTV.setText("" + count);
		} else {
			url = "https://api.weibo.com/2/comments/create.json";
			sameTime.setVisibility(View.GONE);
		}

		titleBar.setTitle(todo);
		titleBar.setBackClickListener(this);

		forwardContent.addTextChangedListener(this);
		forwardContent.setSelection(0);
		forwardContent.setOnClickListener(this);

		faceKeyboard.setOnClickListener(this);

		faceGrid.setAdapter(new EmotionsGridAdapter(this));
		faceGrid.setOnItemClickListener(this);

		send.setOnClickListener(this);

		inputRoot.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_bar_back:
			finish();
			break;
		case R.id.btn_send:
			send();
			break;
		case R.id.ll_input_root:
		case R.id.et_forward_content:
			faceKeyboard.setImageResource(R.drawable.happy_face);
			faceGrid.setVisibility(View.GONE);
			inputMethodManager.showSoftInput(forwardContent, InputMethodManager.SHOW_IMPLICIT);
			break;
		case R.id.btn_face_keyboard:
			if (faceGrid.getVisibility() == View.GONE) { // 先关闭输入法，再打开表情面板
				faceKeyboard.setImageResource(R.drawable.keyboard);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				faceGrid.setVisibility(View.VISIBLE);
			} else { // 先关闭面板，再打开输入法
				faceKeyboard.setImageResource(R.drawable.happy_face);
				faceGrid.setVisibility(View.GONE);
				inputMethodManager.showSoftInput(forwardContent, InputMethodManager.SHOW_IMPLICIT);
			}
			break;
		}
	}

	/**
	 * 发送
	 */
	private void send() {
		mParams.put("id", Long.parseLong(currentStatus.id));
		String text = forwardContent.getText().toString();
		if (!TextUtils.isEmpty(text)) {
			if (todo.equals(TO_COMMENT))
				mParams.put("comment", text);
			else
				mParams.put("status", text);
		} else {
			if (todo.equals(TO_COMMENT)) {
				Toast.makeText(this, "请输入内容", Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (todo.equals(TO_FORWARD) && sameTime.isChecked())
			mParams.put("is_comment", 1);
		new AsyncWeiboRunner(this).requestAsync(url, mParams, "POST", this); // 异步调用微博接口
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (faceGrid.getVisibility() == View.VISIBLE) {
				faceGrid.setVisibility(View.GONE);
				faceKeyboard.setImageResource(R.drawable.happy_face);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// =====================TextWatcher======================
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		count = 140 - s.length();
		if (count < 0)
			countTV.setTextColor(Color.RED);
		else
			countTV.setTextColor(Color.BLACK);
		countTV.setText("" + count);
	}
	// =====================TextWatcher======================

	// ====================WeiboListener=====================
	@Override
	public void onComplete(String response) { // 服务器返回JSON字符串
		if (!TextUtils.isEmpty(response)) {
			Status ret = Status.parse(response);
			if (ret != null) {
				Toast.makeText(this, todo + "成功", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
		}
		Toast.makeText(this, todo + "失败", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(this, "ERROR:" + e.toString(), Toast.LENGTH_LONG).show();
	}
	// ====================WeiboListener=====================

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = forwardContent.getSelectionStart();
		Editable e = forwardContent.getEditableText();
		if (index < 0 || index > e.length())
			e.append(FaceUtil.getFaceList().get(position));
		else
			e.insert(index, FaceUtil.getFaceList().get(position));
	}
}
