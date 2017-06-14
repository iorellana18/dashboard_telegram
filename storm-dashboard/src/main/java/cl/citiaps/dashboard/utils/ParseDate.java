package cl.citiaps.dashboard.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ParseDate {
	public static String parse(Long timestamp) {
		Date timeStamp = new Date(timestamp * 1000);
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		dateFormat.setTimeZone(timeZone);
		return dateFormat.format(timeStamp);
	}
}
