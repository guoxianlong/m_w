package com.mmb.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
	public static boolean isNumber(String value) {
		if (value == null) {
			return false;
		}
		String regex = "-?[0-9]*";
		String reg1 = "[\\d]+";
		String reg2 = "^-?(\\d+)(\\.\\d+)?$";
		boolean result = false;
		Pattern pattern = Pattern.compile(reg1);
		Matcher matcher = pattern.matcher(value);
		result = matcher.matches();
		return result;
	}

	public static void main(String[] args) {
		System.out.println(isNumber(""));
	}
}
