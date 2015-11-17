package com.greenshadow.openweibo.adapters;

import java.util.ArrayList;

import com.greenshadow.openweibo.ui.fragments.ImageCheckFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImageCheckViewPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<ImageCheckFragment> fragments;

	public ImageCheckViewPagerAdapter(FragmentManager fm, ArrayList<String> urls) {
		super(fm);
		fragments = new ArrayList<ImageCheckFragment>();
		for (String url : urls)
			fragments.add(new ImageCheckFragment(url));
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}
}
