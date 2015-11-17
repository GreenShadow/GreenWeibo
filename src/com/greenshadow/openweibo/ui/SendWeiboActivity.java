package com.greenshadow.openweibo.ui;

import java.io.File;
import java.io.IOException;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.adapters.EmotionsGridAdapter;
import com.greenshadow.openweibo.utils.FaceUtil;
import com.greenshadow.openweibo.views.TitleBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class SendWeiboActivity extends Activity implements OnClickListener, TextWatcher, OnItemClickListener {
	private static final int CAMERA = 0;
	private static final int PHOTO = 1;

	private TitleBar titleBar;
	private EditText weiboContent;
	private TextView countTV;
	private ImageButton faceKeyboard, pic, send;
	private GridView faceGrid;
	private LinearLayout inputRoot;
	private ImageView weiboPic;
	private PopupWindow popupWindow;
	private int count = 140;

	private int screenWidth; // 屏幕宽度 像素

	private InputMethodManager inputMethodManager; // 输入法管理器

	private Uri uri; // 用于拍照时储存照片的路径
	private Bitmap image; // 界面上显示的微博图片

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_weibo);

		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 获得屏幕宽度

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		weiboContent = (EditText) findViewById(R.id.et_forward_content);
		countTV = (TextView) findViewById(R.id.tv_count);
		faceKeyboard = (ImageButton) findViewById(R.id.btn_face_keyboard);
		pic = (ImageButton) findViewById(R.id.btn_pic);
		send = (ImageButton) findViewById(R.id.btn_send);
		faceGrid = (GridView) findViewById(R.id.gv_face);
		inputRoot = (LinearLayout) findViewById(R.id.ll_input_root);
		weiboPic = (ImageView) findViewById(R.id.iv_weibo_pic);

		titleBar.setTitle("发送微博");
		titleBar.setBackClickListener(this);

		weiboContent.addTextChangedListener(this);
		weiboContent.setOnClickListener(this);

		faceKeyboard.setOnClickListener(this);
		pic.setOnClickListener(this);
		faceGrid.setAdapter(new EmotionsGridAdapter(this));
		faceGrid.setOnItemClickListener(this);
		send.setOnClickListener(this);
		inputRoot.setOnClickListener(this);

		View content = View.inflate(this, R.layout.layout_popup, null);
		content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss(); // content显示的时候一定是popupWindow显示的时候
			}
		});
		content.findViewById(R.id.btn_camera).setOnClickListener(this);
		content.findViewById(R.id.btn_photo).setOnClickListener(this);
		popupWindow = new PopupWindow(content, -1, -1, true); // -1为填充父容器，-2为自适应
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_bar_back:
			finish();
			break;
		case R.id.btn_send:
			String text = weiboContent.getText().toString();
			if (TextUtils.isEmpty(text) && uri == null) {
				Toast.makeText(this, "请输入内容", Toast.LENGTH_LONG).show();
				return;
			}
			if (weiboContent.getText().toString().length() > 140) {
				Toast.makeText(this, "内容太长", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(text) && uri != null)
				text = "分享图片";

			Intent i = new Intent();
			i.putExtra("content", text);
			i.setData(uri);
			setResult(RESULT_OK, i); // 设置Result
			finish();
			break;
		case R.id.ll_input_root:
		case R.id.et_forward_content:
			faceKeyboard.setImageResource(R.drawable.happy_face);
			faceGrid.setVisibility(View.GONE);
			inputMethodManager.showSoftInput(weiboContent, InputMethodManager.SHOW_IMPLICIT);
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
				inputMethodManager.showSoftInput(weiboContent, InputMethodManager.SHOW_IMPLICIT);
			}
			break;
		case R.id.btn_pic:
			if (faceGrid.getVisibility() == View.VISIBLE) {
				faceGrid.setVisibility(View.GONE);
				faceKeyboard.setImageResource(R.drawable.happy_face);
			}
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			if (weiboPic.getVisibility() == View.VISIBLE) {
				weiboPic.setVisibility(View.GONE);
				pic.setImageResource(R.drawable.pic);
				image.recycle(); // 回收图片对象
				image = null;
				break;
			} else
				popupWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content),
						Gravity.BOTTOM, 0, 0);
			break;
		case R.id.btn_camera:
			if (popupWindow != null)
				popupWindow.dismiss();
			camera();
			break;
		case R.id.btn_photo:
			if (popupWindow != null)
				popupWindow.dismiss();
			photo();
			break;
		}
	}

	/**
	 * 调用相机
	 */
	private void camera() {
		File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "GreebnWeibo");
		if (!dir.exists())
			dir.mkdirs();

		// Activity隐式启动
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(dir, System.currentTimeMillis() + ".jpg");
		uri = Uri.fromFile(file);

		i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

		startActivityForResult(i, CAMERA);
	}

	private void photo() {
		// 隐式启动Activity
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // 过滤，只查看图片
		startActivityForResult(i, PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAMERA:
				showImage();
				break;
			case PHOTO:
				uri = data.getData();
				showImage();
				break;
			}
		}
	}

	private void showImage() {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inPreferredConfig = Config.RGB_565; // 设置编码格式
			o.inJustDecodeBounds = true; // 只读取图片的宽高，不把图片加载至内存
			image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o);
			float s = o.outWidth * 1.0f / screenWidth * 1.0f;
			if (s > 1)
				o.inSampleSize = (int) (s + 0.5f); // 四舍五入
			o.inJustDecodeBounds = false; // 图片加载至内存

			image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o);
		} catch (IOException e) {
			e.printStackTrace(); // 逻辑上已经避免了FileNotFoundException，故不处理异常
		}

		if (image != null) {
			weiboPic.setVisibility(View.VISIBLE);
			weiboPic.setImageBitmap(image);
			pic.setImageResource(R.drawable.close);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (faceGrid.getVisibility() == View.VISIBLE) {
				faceGrid.setVisibility(View.GONE);
				faceKeyboard.setImageResource(R.drawable.happy_face);
				return true; // 返回true表示这里消耗了这个按键事件
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		if (getCurrentFocus() != null)
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		super.onResume();
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = weiboContent.getSelectionStart();
		Editable e = weiboContent.getEditableText();
		if (index < 0 || index > e.length())
			e.append(FaceUtil.getFaceList().get(position));
		else
			e.insert(index, FaceUtil.getFaceList().get(position));
	}
}
