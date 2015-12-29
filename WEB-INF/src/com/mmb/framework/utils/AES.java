package com.mmb.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

public class AES {

	public static String key = "1234567890abcDEF";

	public static String Decrypt(String sSrc, String sKey) throws Exception {
		try {
			// 判断Key是否正确
			if (StringUtils.isBlank(sKey)) {
				System.out.print("Key为空null");
				return null;
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				System.out.print("Key长度不是16位");
				return null;
			}
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(sSrc);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original);
				return originalString;
			} catch (Exception e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return null;
		}
	}

	// 判断Key是否正确
	public static String Encrypt(String sSrc, String sKey) throws Exception {
		if (StringUtils.isBlank(sKey)) {
			System.out.print("Key为空null");
			return null;
		}
		// 判断Key是否为16位
		if (sKey.length() != 16) {
			System.out.print("Key长度不是16位");
			return null;
		}
		byte[] raw = sKey.getBytes("ASCII");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
		return byte2hex(encrypted).toLowerCase();
	}

	public static byte[] hex2byte(String strhex) {
		if (StringUtils.isBlank(strhex)) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1) {
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
					16);
		}
		return b;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
	
	public static void main(String [] args){
		try {
			//需要加密的字串
			String cSrc = "root";
			//加密
			long lStart = System.currentTimeMillis();
			String enString = AES.Encrypt(cSrc, AES.key);
			System.out.println("加密后的字串是：" + enString);
			long lUseTime = System.currentTimeMillis() - lStart;
			System.out.println("加密耗时：" + lUseTime + "毫秒");
			//解密
			lStart = System.currentTimeMillis();
			String DeString = AES.Decrypt(enString, AES.key);
			System.out.println("解密后的字串是：" + DeString);
			lUseTime = System.currentTimeMillis() - lStart;
			System.out.println("解密耗时：" + lUseTime + "毫秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
