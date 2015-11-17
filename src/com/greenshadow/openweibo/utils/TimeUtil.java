package com.greenshadow.openweibo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {
	public static String getTime(String format) {
		String result = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(simpleDateFormat.parse(format));
			long l = System.currentTimeMillis() - calendar.getTimeInMillis();
			if (l <= 1000 * 60)
				result = "刚刚";
			else if (l > 1000 * 60 && l <= 1000 * 60 * 60)
				result = l / (1000 * 60) + "分钟";
			else if (l > 1000 * 60 * 60 && l < 1000 * 60 * 60 * 24)
				result = l / (1000 * 60 * 60) + "小时";
			else
				result = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
						+ calendar.get(Calendar.DATE) + "日 " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
						+ calendar.get(Calendar.MINUTE);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}
