package com.greenshadow.openweibo.utils;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLUtil {
	public static String getStringA(String xml) {
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(new StringReader(xml));
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "a");
			xml = parser.nextText();
			parser.require(XmlPullParser.END_TAG, null, "a");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return xml;
	}
}
