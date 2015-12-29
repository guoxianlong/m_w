package adultadmin.util;

public class BitUtil {

	/**
	 * 设置value的某一位为1<br/>
	 * pos从0开始<br/>
	 * @param value
	 * @param pos
	 * @return
	 */
	public static int setBit(int value, int pos){
		int i = 1;
		i = i << pos;
		value = value | i;
		return value;
	}

	/**
	 * 设置value的某一位为0<br/>
	 * pos从0开始<br/>
	 * @param value
	 * @param pos
	 * @return
	 */
	public static int unsetBit(int value, int pos){
		int i = ~(1 << pos);
		value = value & i;
		return value;
	}

	public static void main(String[] args){
		int i = setBit(4, 0);
		System.out.println(Integer.toBinaryString(i));
		i = unsetBit(i, 0);
		System.out.println(Integer.toBinaryString(i));
	}
}
