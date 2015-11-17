package com.greenshadow.openweibo.ui;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ReplyActivity extends Activity
		implements OnClickListener, TextWatcher, RequestListener, OnItemClickListener {
	private TitleBar titleBar;
	private EditText replyContent;
	private CheckBox sameTime;
	private TextView countTV;
	private ImageButton faceKeyboard, send;
	private GridView faceGrid;
	private RelativeLayout inputRoot;
	private int count = 140;

	private String url = "https://api.weibo.com/2/comments/reply.json";
	private Oauth2AccessToken mAccessToken;
	private WeiboParameters mParams;
	private String cid; // 评论id
	private String id; // 微博id

	private InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forward_comment);

		cid = getIntent().getStringExtra("cid");
		id = getIntent().getStringExtra("id");
		if (TextUtils.isEmpty(cid) || TextUtils.isEmpty(id)) {
			finish();
			return;
		}

		mAccessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
		mParams = new WeiboParameters(Constants.APP_KEY);
		mParams.put("access_token", mAccessToken.getToken());
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		replyContent = (EditText) findViewById(R.id.et_forward_content);
		countTV = (TextView) findViewById(R.id.tv_count);
		sameTime = (CheckBox) findViewById(R.id.cb_same_time);
		faceKeyboard = (ImageButton) findViewById(R.id.btn_face_keyboard);
		send = (ImageButton) findViewById(R.id.btn_send);
		faceGrid = (GridView) findViewById(R.id.gv_face);
		inputRoot = (RelativeLayout) findViewById(R.id.ll_input_root);

		sameTime.setVisibility(View.GONE);
		titleBar.setTitle("回复");
		titleBar.setBackClickListener(this);
		replyContent.setHint("回复");
		replyContent.addTextChangedListener(this);
		replyContent.setSelection(0);
		replyContent.setOnClickListener(this);
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
			inputMethodManager.showSoftInput(replyContent, InputMethodManager.SHOW_IMPLICIT);
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
				inputMethodManager.showSoftInput(replyContent, InputMethodManager.SHOW_IMPLICIT);
			}
			break;
		}
	}

	private void send() {
		mParams.put("cid", Long.parseLong(cid));
		mParams.put("id", Long.parseLong(id));
		mParams.put("comment", replyContent.getText().toString());
		new AsyncWeiboRunner(this).requestAsync(url, mParams, "POST", this);
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

	@Override
	public void onComplete(String response) {
		if (!TextUtils.isEmpty(response)) {
			try {
				JSONObject jo = new JSONObject(response);
				JSONObject reply = jo.optJSONObject("reply_comment");
				if (reply != null) {
					Toast.makeText(this, "评论成功", Toast.LENGTH_LONG).show();
					finish();
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Toast.makeText(this, "评论失败", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Toast.makeText(this, "评论失败\n" + e.toString(), Toast.LENGTH_LONG).show();
	}

	/**
	 * 表情点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = replyContent.getSelectionStart();
		Editable e = replyContent.getEditableText();
		if (index < 0 || index > e.length())
			e.append(FaceUtil.getFaceList().get(position));
		else
			e.insert(index, FaceUtil.getFaceList().get(position));
	}
}
