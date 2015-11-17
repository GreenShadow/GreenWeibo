package com.greenshadow.openweibo.span;

import com.greenshadow.openweibo.views.UserDialog;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MentionSpan extends ClickableSpan {
	private Context mContext;
	private String userName;

	public MentionSpan(Context context, String userName) {
		mContext = context;
		if (userName.startsWith("@")) {
			userName = userName.trim();
			userName = userName.substring(1);
		}
		this.userName = userName;
	}

	@Override
	public void onClick(View widget) {
		userName = userName.replaceAll(":", "");
		new UserDialog(mContext, userName).show();
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(Color.parseColor("#4eddf7"));
	}
}
