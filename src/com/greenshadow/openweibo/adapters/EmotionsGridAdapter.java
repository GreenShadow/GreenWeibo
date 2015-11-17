package com.greenshadow.openweibo.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.greenshadow.openweibo.R;
import com.greenshadow.openweibo.utils.FaceUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class EmotionsGridAdapter extends AbsBaseListAdapter<String> {
	private Context mContext;
	private Map<String, String> faceMap;

	public EmotionsGridAdapter(Context context) {
		this(context, FaceUtil.getFaceList());
		mContext = context;
		faceMap = FaceUtil.getFaceMap();
	}

	private EmotionsGridAdapter(Context context, List<String> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		FaceHolder faceHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_grid_view, null);

			faceHolder = new FaceHolder();
			faceHolder.face = (GifImageView) convertView.findViewById(R.id.gif_view_face);

			convertView.setTag(faceHolder);
		} else
			faceHolder = (FaceHolder) convertView.getTag();

		String item = list.get(position);
		String name = faceMap.get(item);
		try {
			InputStream is = mContext.getResources().getAssets().open("gif/" + name + ".gif");
			GifDrawable drawable = new GifDrawable(is);
			faceHolder.face.setImageDrawable(drawable);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	private class FaceHolder {
		GifImageView face;
	}
}
