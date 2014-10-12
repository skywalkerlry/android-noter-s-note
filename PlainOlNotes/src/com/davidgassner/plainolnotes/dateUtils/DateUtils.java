package com.davidgassner.plainolnotes.dateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

public class DateUtils {
	
	@SuppressLint("SimpleDateFormat")
	public static String getDate() {
		Locale locale = new Locale("en_US");
		Locale.setDefault(locale);

		String pattern = "yyyy-MM-dd HH:mm:ss Z";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String date = formatter.format(new Date());
		
		return date;
	}
}
