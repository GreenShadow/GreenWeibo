package com.greenshadow.openweibo.views;

import com.greenshadow.openweibo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends RelativeLayout {
	private ImageButton back;
	private TextView title;

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this);
		init();
	}

	public TitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleBar(Context context) {
		this(context, null);
	}

	private void init() {
		back = (ImageButton) findViewById(R.id.btn_title_bar_back);
		title = (TextView) findViewById(R.id.tv_title_bar_title);
	}

	public void setBackClickListener(OnClickListener l) {
		back.setOnClickListener(l);
	}

	public void setTitle(String titleString) {
		title.setText(titleString);
	}
}
