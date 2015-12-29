package com.mmb.framework.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtils {

	private static Log log = LogFactory.getLog(DateUtils.class);
	
	public static final String YYYYMMDD = "yyyy-MM-dd";
	public static final String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	
	
	public static String formatDate(Date date,String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	public static Date formatString(String dateStr,String format){
		Date date = null;
		DateFormat df = new SimpleDateFormat(format);
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		return date;
	}

	public static String getInterval(Date date1,Date date2){
		return String.valueOf((date1.getTime()-date2.getTime())/(1000*60*60*24)+1);
	}
	
	public static void main(String[] args){
	}
	
}
