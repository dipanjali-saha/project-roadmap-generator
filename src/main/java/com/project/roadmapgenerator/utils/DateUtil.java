package com.project.roadmapgenerator.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static Date addDays(Date inputDate, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
	
	public static String toDefaultString(Date inputDate) {
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		return (null != inputDate) ? formatter.format(inputDate) : "";
	}

}
