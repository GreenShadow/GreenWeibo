package com.greenshadow.openweibo.ui.fragments;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.views.LoadUrlImageView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ImageCheckFragment extends Fragment {
	private String url;
	private LoadUrlImageView image;

	public ImageCheckFragment(String url) {
		this.url = url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_image_check, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		image = (LoadUrlImageView) findViewById(R.id.iv_image_check);
		image.setUrl(url.replaceAll("thumbnail", "large"));
	}

	private View findViewById(int id) {
		return getView().findViewById(id);
	}
}
