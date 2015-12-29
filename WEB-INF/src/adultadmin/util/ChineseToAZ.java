package adultadmin.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChineseToAZ {

	private static final char[] alphatable = {

	'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',

	'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z'

	};

	/**
	 * 
	 * 汉字拼音首字母编码表，可以如下方法得到：
	 * 
	 * 字母Z使用了两个标签，这里有２７个值, i, u, v都不做声母, 跟随前面的字母(因为不可以出现，所以可以随便取)
	 * 
	 * private static final char[] chartable =
	 * 
	 * {
	 * 
	 * '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈',
	 * 
	 * '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然',
	 * 
	 * '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座'
	 * 
	 * };
	 * 
	 * 
	 * 
	 * private static final int[] table = new int[27];
	 * 
	 * static
	 * 
	 * {
	 * 
	 * for (int i = 0; i < 27; ++i) {
	 * 
	 * table[i] = gbValue(chartable[i]);
	 * 
	 * System.out.print(table[i]+" ");
	 * 
	 * }
	 * 
	 * }
	 */

	private static final int[] table = new int[] {

	45217, 45253, 45761, 46318, 46826,

	47010, 47297, 47614, 47614, 48119,

	49062, 49324, 49896, 50371, 50614,

	50622, 50906, 51387, 51446, 52218,

	52218, 52218, 52698, 52980, 53689, 54481, 55289 };

	/**
	 * 
	 * 主函数, 输入字符, 得到他的声母, 英文字母返回对应的大写字母 其他非简体汉字返回 '*'
	 */

	public static char Char2Alpha(char ch) {
		if (ch >= 'a' && ch <= 'z')

			return ch;

		if (ch >= 'A' && ch <= 'Z')

			return (char) (ch - 'A' + 'a');
		if(ch>= '0' && ch<='9' )
			return ch;
		
		int gb = gbValue(ch);

		if (gb < table[0])

			return '*';

		for (int i = 0; i < 26; ++i) {

			if (match(i, gb)) {

				if (i >= 26)

					return '*';

				else

					return alphatable[i];

			}

		}

		return '*';

	}

	/**
	 * 
	 * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
	 */

	public static String String2Alpha(String str) {

		String Result = "";

		try {

			for (int i = 0; i < str.length(); i++) {

				Result += Char2Alpha(str.charAt(i));

			}

		} catch (Exception e) {

			Result = " ";

		}

		return Result;

	}

	private static boolean match(int i, int gb) {

		if (gb < table[i])

			return false;

		int j = i + 1;

		// 字母Z使用了两个标签

		while (j < 26 && (table[j] == table[i]))

			++j;

		if (j == 26)

			return gb <= table[j];

		else

			return gb < table[j];

	}

	/**
	 * 
	 * 取出传入汉字的编码
	 */

	private static int gbValue(char ch) {

		String str = new String();

		str += ch;

		try {

			byte[] bytes = str.getBytes("GB2312");
			System.out.println(bytes);
			if (bytes.length < 2)

				return 0;

			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);

		} catch (Exception e) {

			return '*';

		}

	}
	 /**   
     * 汉字转换位汉语拼音首字母，英文字符不变   
     * @param chinese 汉字   
     * @return 拼音   
     */      
    public static String converterToFirstSpell(String chinese){
    	if(chinese==null) return "";
        String pinyinName = "";
		char[] nameChar = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);       
        for (int i = 0; i < nameChar.length; i++) {       
            if (StringUtil.isChainese(String.valueOf(nameChar[i]))) {       
                try {       
                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);       
                 } catch (BadHanyuPinyinOutputFormatCombination e) {       
                     e.printStackTrace();       
                 }       
             }else{       
                 pinyinName += nameChar[i];       
             }       
         }       
        return pinyinName;       
     }       
        
	

	/**
	 * 
	 * 测试输出
	 */

	public static void main(String[] args) {
		System.out.println("拼音首字母为 " + ChineseToAZ.converterToFirstSpell("q空间，件023456789"));
	}

}
