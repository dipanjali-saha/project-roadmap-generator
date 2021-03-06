package com.project.roadmapgenerator.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date addDaysWithoutWeekends(Date inputDate, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		for (int i = 0; i < days; i++) {
			cal.add(Calendar.DATE, 1);
			while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				cal.add(Calendar.DATE, 1);
			}
		}
		return cal.getTime();
	}
	
	public static Date subtractDaysWithoutWeekends(Date inputDate, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		for (int i = 0; i < days; i++) {
			cal.add(Calendar.DATE, -1);
			while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				cal.add(Calendar.DATE, -1);
			}
		}
		return cal.getTime();
	}

	public static String toDefaultString(Date inputDate) {
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		return (null != inputDate) ? formatter.format(inputDate) : "";
	}
	
	public static Date toDefaultDate(String dateString) {
		Date parsedDate = null;
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			parsedDate = (null != dateString) ? formatter.parse(dateString) : null;
		} catch (ParseException e) {
			parsedDate = null;
		}
		return parsedDate;
	}

}
