/*
 * Created on 2006-11-01
 *
 */
package adultadmin.util;

import org.apache.log4j.Logger;

/**
 * @author bomb
 * 
 */
public class LogUtil {

	public static Logger accessLogger = Logger.getLogger("access.Log");

	public static Logger privacyLogger = Logger.getLogger("privacy.Log");

	public static void logPrivacy(String str) {
		if (str == null) {
			return;
		}
		privacyLogger.error(str);
	}

	public static void logAccess(String str) {
		if (str == null) {
			return;
		}
		accessLogger.error(str);
	}
}
