package adultadmin.util;

public class ArrayUtil {
	
	public static int indexOfInt(int[] array, int n){
		int index = -1;
		if(array != null){
			for(int i=0; i<array.length; i++){
				if(array[i] == n){
					index = i;
					break;
				}
			}
		}
		return index;
	}
	
}
