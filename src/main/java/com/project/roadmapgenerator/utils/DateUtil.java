package com.project.roadmapgenerator.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static Date addDays(Date inputDate, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

}
