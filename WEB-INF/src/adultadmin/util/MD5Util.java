package adultadmin.util;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public static byte[] getKeyedDigest(byte[] buffer, byte[] key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(buffer);
			return md5.digest(key);
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String getKeyedDigest(String strSrc, String key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strSrc.getBytes("UTF8"));

			String result = "";
			byte[] temp;
			temp = md5.digest(key.getBytes("UTF8"));
			for (int i = 0; i < temp.length; i++) {
				result += Integer.toHexString(
						(0x000000ff & temp[i]) | 0xffffff00).substring(6);
			}

			return result;

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 圆通接口加密方式(带base64编码)
	* @Description: 
	* @author ahc
	 */
	public static String getKeyedDigestForYt(String strSrc) {
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
			messagedigest.update((strSrc).getBytes("UTF-8"));
			byte[] abyte0 = messagedigest.digest();
			String data_digest = new String(Base64.encodeBase64(abyte0));
			return data_digest;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	/**
	 * 如风达接口加密方式
	* @Description: 
	* @author ahc
	 */
	 public static String md5Encode(String inStr) throws Exception {
	        MessageDigest md5 = null;
	        try {
	            md5 = MessageDigest.getInstance("MD5");
	        } catch (Exception e) {
	            System.out.println(e.toString());
	            e.printStackTrace();
	            return "";
	        }

	        byte[] byteArray = inStr.getBytes("UTF-8");
	        byte[] md5Bytes = md5.digest(byteArray);
	        StringBuffer hexValue = new StringBuffer();
	        for (int i = 0; i < md5Bytes.length; i++) {
	            int val = ((int) md5Bytes[i]) & 0xff;
	            if (val < 16) {
	                hexValue.append("0");
	            }
	            hexValue.append(Integer.toHexString(val));
	        }
	        return hexValue.toString();
	    }

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String mi;
		String s = "123";

		// 第二个参数请填空字符串
		mi = MD5Util.getKeyedDigest(s, "");

		System.out.println("mi:" + mi);
		
		System.out.println(URLDecoder.decode("5%E5%88%86%E9%92%9F","utf-8"));
	}

}
