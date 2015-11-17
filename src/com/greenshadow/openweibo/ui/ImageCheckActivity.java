package com.greenshadow.openweibo.ui;

import java.util.ArrayList;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.adapters.ImageCheckViewPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class ImageCheckActivity extends FragmentActivity {

	private ViewPager viewPager;
	private ArrayList<String> urls;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_check);

		urls = getIntent().getStringArrayListExtra("urls");
		index = getIntent().getIntExtra("index", -1);

		if (urls == null || index < 0) {
			finish();
			return;
		}

		viewPager = (ViewPager) findViewById(R.id.vp_image_check);
		viewPager.setAdapter(new ImageCheckViewPagerAdapter(getSupportFragmentManager(), urls));
		viewPager.setCurrentItem(index);
	}
}
