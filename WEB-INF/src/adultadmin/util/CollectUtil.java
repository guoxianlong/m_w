/*
 * Created on 2006-10-9
 *
 */
package adultadmin.util;

import java.util.Iterator;
import java.util.List;

import adultadmin.action.vo.voOrder;

/**
 * @author bomb
 *  
 */
public class CollectUtil {
	
	static public float sumOrderPrice(List orderList) {
		Iterator iter = orderList.iterator();
		float sum = 0;
		while(iter.hasNext()) {
			voOrder vo = (voOrder)iter.next();
			sum += vo.getDprice();
		}
		return sum;
	}
}
