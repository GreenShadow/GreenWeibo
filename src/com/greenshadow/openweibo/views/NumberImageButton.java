package com.greenshadow.openweibo.views;

import com.greenshadow.openweibo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberImageButton extends LinearLayout {
	private ImageView icon;
	private TextView content, number;
	private boolean isLikeMode = false;
	private boolean isLike = false;

	public NumberImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public NumberImageButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NumberImageButton(Context context) {
		this(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.lauout_number_image_button, this, true);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberImageButton);
		icon = (ImageView) findViewById(R.id.iv_image_number_button_image);
		content = (TextView) findViewById(R.id.tv_image_number_button_content);
		number = (TextView) findViewById(R.id.tv_image_number_button_number);

		isLikeMode = ta.getBoolean(R.styleable.NumberImageButton_isLike, false);
		if (isLikeMode) {
			isLike = ta.getBoolean(R.styleable.NumberImageButton_isLike, false);
			setAttitudes(isLike);
		} else {
			Drawable d = ta.getDrawable(R.styleable.NumberImageButton_src);
			if (d != null)
				icon.setImageDrawable(d);
			else
				icon.setVisibility(View.GONE);
		}

		String s = ta.getString(R.styleable.NumberImageButton_content);
		if (!TextUtils.isEmpty(s))
			content.setText(s);
		else
			content.setVisibility(View.GONE);

		int i = ta.getInt(R.styleable.NumberImageButton_number, -1);
		if (i >= 0)
			number.setText("" + i);
		else
			number.setVisibility(View.GONE);

		ta.recycle();
	}

	public void setAttitudes(boolean isLike) {
		if (isLikeMode) {
			this.isLike = isLike;
			if (isLike)
				icon.setImageResource(R.drawable.like_true);
			else
				icon.setImageResource(R.drawable.like);
		}
	}

	public boolean getAttitudes() {
		return isLike;
	}

	public void setNumber(int customNumber) {
		number.setText("" + customNumber);
		if (number.getVisibility() != View.VISIBLE)
			number.setVisibility(View.VISIBLE);
	}
}
