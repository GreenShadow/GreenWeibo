package com.greenshadow.openweibo.views;

import com.greenshadow.openweibo.R;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBarMain extends RelativeLayout {
	private ImageButton menu, sendWeibo;
	private TextView title;
	private int left = -1;

	public TitleBarMain(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.layout_title_bar_main, this);
		init();
	}

	public TitleBarMain(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleBarMain(Context context) {
		this(context, null);
	}

	private void init() {
		menu = (ImageButton) findViewById(R.id.btn_title_bar_main_menu);
		title = (TextView) findViewById(R.id.tv_title_bar_main_title);
		sendWeibo = (ImageButton) findViewById(R.id.btn_title_bar_main_send_weibo);
		menu.post(new Runnable() {
			@Override
			public void run() {
				left = menu.getLeft();
			}
		});
	}

	public void setSendWeiboListener(OnClickListener l) {
		sendWeibo.setOnClickListener(l);
	}

	public void setDrawerLayout(final DrawerLayout drawerLayout) {
		drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerStateChanged(int state) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				if (left < 0)
					return;
				menu.setLeft(left - (int) (slideOffset * 20));
			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
			}
		});

		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (drawerLayout.isDrawerOpen(GravityCompat.START))
					drawerLayout.closeDrawer(GravityCompat.START);
				else
					drawerLayout.openDrawer(GravityCompat.START);
			}
		});
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void openMenu() {
		menu.performClick();
	}
}
