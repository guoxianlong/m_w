/**
 * 
 */
package mmb.util;

public final class  Base64{
	public static String getBASE64(String s) { 
		if (s == null) return null; 
		return (new sun.misc.BASE64Encoder()).encode( s.getBytes() ); 
	} 

}
