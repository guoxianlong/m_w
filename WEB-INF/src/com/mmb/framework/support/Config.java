package com.mmb.framework.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 常量配置
 * 
 */
public final class Config {
	private static Log log = LogFactory.getLog(Config.class);

	public static String CONFIG_FILE = "config.properties";
	public static String CLASS_PATH = Config.class.getResource("/").getPath();

	private Config() {

	}

	private static Properties props = new Properties();

	static {
		try {
			props.load(new FileInputStream(CLASS_PATH + "/" + CONFIG_FILE));
		} catch (FileNotFoundException ex) {
			log.error(ex);
		} catch (IOException ex) {
			log.error(ex);
		}
	}

	public static Properties getProperties() {
		return (Properties) props.clone();
	}

	private static boolean changeToBoolean(String str) {
		String tmp = str.toLowerCase();
		if (tmp.equals("true")) {
			return true;
		} else if (tmp.equals("false")) {
			return false;
		} else {
			throw new RuntimeException("class not matching.");
		}
	}

	public static boolean getBoolean(String key) {
		String str = Config.getString(key);
		try {
			return Config.changeToBoolean(str);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		String str = Config.getString(key);
		try {
			return Config.changeToBoolean(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private static int changeToInt(String str) throws Exception {
		return Integer.parseInt(str);
	}

	public static int getInt(String key) {
		String str = Config.getString(key);
		try {
			return Config.changeToInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int getInt(String key, int defaultValue) {
		String str = Config.getString(key);
		try {
			return Config.changeToInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String getString(String key, String defaultValue) {

		String tmp = getString(key);
		if (tmp == null) {
			tmp = defaultValue;
		}
		return tmp;
	}

	public static String getString(String key) {
		String value = props.getProperty(key);
		if (value == null) {
			return null;
		}
		return value;
	}

	public static void main(String[] args) {
		Properties props = Config.getProperties();
		System.out.println(props);
	}
}
