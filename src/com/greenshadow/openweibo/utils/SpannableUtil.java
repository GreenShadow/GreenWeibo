package com.greenshadow.openweibo.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greenshadow.openweibo.span.MentionSpan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;

public class SpannableUtil {
	private static String urlRegex = "http://t.cn/[^\\w]*"; // 匹配微博短链地址
	private static String faceRegex = "\\[.+?\\]";
	private static String mentionRegex = "@.+?[ |:]";
	private static String regex = "(" + urlRegex + ")|(" + faceRegex + ")|(" + mentionRegex + ")";

	public static ArrayList<SpannableString> getAllSpannable(Context context, String text) {
		ArrayList<String> split = splitRegex(text);
		ArrayList<SpannableString> ssArray = new ArrayList<SpannableString>();
		for (String sub : split) {
			SpannableString ss = new SpannableString(sub);
			if (sub.matches(urlRegex))
				ss.setSpan(new URLSpan(sub), 0, sub.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			else if (sub.matches(faceRegex)) {
				String faceFileName = FaceUtil.getFaceMap().get(sub);
				Bitmap b = null;
				try {
					b = BitmapFactory
							.decodeStream(context.getResources().getAssets().open("gif/" + faceFileName + ".gif"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (b != null) {
					b = Bitmap.createScaledBitmap(b, 40, 40, false);
					ss.setSpan(new ImageSpan(context, b), 0, sub.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} else if (sub.matches(mentionRegex))
				ss.setSpan(new MentionSpan(context, sub), 0, sub.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssArray.add(ss);
		}

		return ssArray;
	}

	public static ArrayList<String> splitRegex(String text) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		ArrayList<String> splitResult = new ArrayList<String>();
		int startIndex = 0;
		while (m.find()) {
			String urlText = m.group();
			String backwardString = text.substring(startIndex, text.indexOf(urlText));
			splitResult.add(backwardString);
			splitResult.add(urlText);
			text = text.substring(startIndex + backwardString.length() + urlText.length());
		}
		splitResult.add(text.substring(startIndex));

		return splitResult;
	}
}
