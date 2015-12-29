package mmb.util;

import java.io.UnsupportedEncodingException;

// 自定义base64，使用不同的字母表
public final class Base64x {
	static private final int BASELENGTH = 255;

	static private final int LOOKUPLENGTH = 64;

	static private final int TWENTYFOURBITGROUP = 24;

	static private final int EIGHTBIT = 8;
	
	static private final int SIXTEENBIT = 16;

	static private final int FOURBYTE = 4;

	static private final int SIGN = -128;

	static private final byte PAD = (byte) ')';

	static private byte[] base64Alphabet = new byte[BASELENGTH];
	// 绝对不可修改
	private static byte[] lookUpBase64Alphabet = { 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'q', 'r', 's', 't', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'R', 'S', 'T', 'U', 'V', 'W', 'u', 'v', 'w', 'x', 'y', 'X',
			'Y', 'Z', '_', '+', 'L', 'M', 'N', 'O', 'P', 'Q' };

	static {
		for (int i = 0; i < BASELENGTH; i++) {
			base64Alphabet[i] = -1;
		}
		for (byte i = 0;i < LOOKUPLENGTH;i++) {
			base64Alphabet[lookUpBase64Alphabet[i]] = i;
		}
	}

	public static boolean isBase64(String isValidString) {
		return isArrayByteBase64(isValidString.getBytes());
	}

	public static boolean isBase64(byte octect) {
		//shall we ignore white space? JEFF??
		return (octect == PAD || base64Alphabet[octect] != -1);
	}

	public static boolean isArrayByteBase64(byte[] arrayOctect) {
		int length = arrayOctect.length;
		if (length == 0) {
			// shouldn't a 0 length array be valid base64 data?
			// return false;
			return true;
		}
		for (int i = 0; i < length; i++) {
			if (!Base64x.isBase64(arrayOctect[i]))
				return false;
		}
		return true;
	}

	/**
	 * Encodes hex octects into Base64.
	 *
	 * @param binaryData Array containing binary data to encode.
	 * @return Base64-encoded data.
	 */
	public static byte[] encode(byte[] binaryData) {
		int lengthDataBits = binaryData.length * EIGHTBIT;
		int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
		int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
		byte encodedData[] = null;

		if (fewerThan24bits != 0) {
			//data not divisible by 24 bit
			encodedData = new byte[(numberTriplets + 1) * 4];
		} else {
			// 16 or 8 bit
			encodedData = new byte[numberTriplets * 4];
		}

		byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

		int encodedIndex = 0;
		int dataIndex = 0;
		int i = 0;
		//log.debug("number of triplets = " + numberTriplets);
		for (i = 0; i < numberTriplets; i++) {
			dataIndex = i * 3;
			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			b3 = binaryData[dataIndex + 2];

			//log.debug("b1= " + b1 +", b2= " + b2 + ", b3= " + b3);

			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			encodedIndex = i * 4;
			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
			byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			//log.debug( "val2 = " + val2 );
			//log.debug( "k4   = " + (k<<4) );
			//log.debug(  "vak  = " + (val2 | (k<<4)) );
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | (k << 4)];
			encodedData[encodedIndex + 2] = lookUpBase64Alphabet[(l << 2) | val3];
			encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3f];
		}

		// form integral number of 6-bit groups
		dataIndex = i * 3;
		encodedIndex = i * 4;
		if (fewerThan24bits == EIGHTBIT) {
			b1 = binaryData[dataIndex];
			k = (byte) (b1 & 0x03);
			//log.debug("b1=" + b1);
			//log.debug("b1<<2 = " + (b1>>2) );
			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
			encodedData[encodedIndex + 2] = PAD;
			encodedData[encodedIndex + 3] = PAD;
		} else if (fewerThan24bits == SIXTEENBIT) {

			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

			encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
			encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | (k << 4)];
			encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
			encodedData[encodedIndex + 3] = PAD;
		}

		return encodedData;
	}

	/**
	 * Decodes Base64 data into octects
	 *
	 * @param base64Data Byte array containing Base64 data
	 * @return Array containing decoded data.
	 */
	public static byte[] decode(byte[] base64Data) {
		// handle the edge case, so we don't have to worry about it later
		if (base64Data.length == 0) {
			return new byte[0];
		}

		int numberQuadruple = base64Data.length / FOURBYTE;
		byte decodedData[] = null;
		byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

		// Throw away anything not in base64Data

		int encodedIndex = 0;
		int dataIndex = 0;
		{
			// this sizes the output array properly - rlw
			int lastData = base64Data.length;
			// ignore the '=' padding
			while (base64Data[lastData - 1] == PAD) {
				if (--lastData == 0) {
					return new byte[0];
				}
			}
			decodedData = new byte[lastData - numberQuadruple];
		}

		for (int i = 0; i < numberQuadruple; i++) {
			dataIndex = i * 4;
			marker0 = base64Data[dataIndex + 2];
			marker1 = base64Data[dataIndex + 3];

			b1 = base64Alphabet[base64Data[dataIndex]];
			b2 = base64Alphabet[base64Data[dataIndex + 1]];

			if (marker0 != PAD && marker1 != PAD) {
				//No PAD e.g 3cQl
				b3 = base64Alphabet[marker0];
				b4 = base64Alphabet[marker1];

				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
				decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
			} else if (marker0 == PAD) {
				//Two PAD e.g. 3c[Pad][Pad]
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
			} else if (marker1 == PAD) {
				//One PAD e.g. 3cQ[Pad]
				b3 = base64Alphabet[marker0];

				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
			}
			encodedIndex += 3;
		}
		return decodedData;
	}

	// digit表示位数，多余的用0补足。高位在前
	public static String encodeInt(int num, int digit) {
		byte[] data = new byte[digit];
		while(digit > 0) {
			digit--;
			data[digit] = lookUpBase64Alphabet[num & 0x3f];
			num >>= 6;
		}
		return new String(data);
	}
	public static String encodeInt(int num) {
		return encodeInt(num, 5);
	}
	public static int decodeInt(String data) {
		byte[] n = data.getBytes();
		int num = 0;
		for(int i = 0;i < n.length;i++) {
			int d = base64Alphabet[n[i]];
			if(d == -1)
				return -1;		// decode error
			num = (num << 6) + d;
		}
		return num;
	}
	// 前面的encode和decode的快捷函数
	public static String encodeString(String src) {
		try {
			return new String(encode(src.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	public static String decodeString(String src) {
		try {
			return new String(decode(src.getBytes()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
}
