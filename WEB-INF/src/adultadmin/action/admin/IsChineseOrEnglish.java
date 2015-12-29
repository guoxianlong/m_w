package adultadmin.action.admin;




public class IsChineseOrEnglish {

	//  GENERAL_PUNCTUATION 判断中文的“号

	//  CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号

	//  HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号

	public static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

			return true;

		}

		return false;

	}



	public static boolean isChinese(String strName) {

		char[] ch = strName.toCharArray();
		boolean falg = false;
		for (int i = 0; i < ch.length; i++) {

			char c = ch[i];

			if (isChinese(c) == true) {
				falg = true;
				break;

			} 

		}
		return falg;
	}

//	public static void main(String[] args) {
//		System.out.println(isChinese("き"));
//
//		System.out.println(isChinese("北a"));
//
//	}



}
