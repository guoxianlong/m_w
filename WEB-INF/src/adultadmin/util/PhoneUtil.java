package adultadmin.util;

public class PhoneUtil {

	public static String dealMobile(String mobile) {
		if(mobile.startsWith("86")) {
			mobile = mobile.replaceFirst("86", "");
		} else if(mobile.startsWith("+86")) {
			mobile = mobile.replaceFirst("\\+86", "");
		}
		return mobile;
	}

	public static void main(String[] args) {
		System.out.println(dealMobile("+8613693010101"));
		System.out.println(dealMobile("8613693010101"));
	}
}
