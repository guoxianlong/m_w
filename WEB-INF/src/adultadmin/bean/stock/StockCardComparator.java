package adultadmin.bean.stock;

import java.util.Comparator;
import java.util.Date;

import adultadmin.util.DateUtil;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-7-30
 * 
 * 说明：进销存卡片排序类，根据创建时间 顺序排列， 根据id 顺序排列
 */
public class StockCardComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		StockCardBean sc1 = (StockCardBean) arg0;
		StockCardBean sc2 = (StockCardBean) arg1;
		if (sc1.getCreateDatetime() != null && sc2.getCreateDatetime() != null) {
			Date d1 = DateUtil.parseDate(sc1.getCreateDatetime(), "yyyy-MM-dd kk:mm:ss");
			Date d2 = DateUtil.parseDate(sc2.getCreateDatetime(), "yyyy-MM-dd kk:mm:ss");
			if(d1 == null){
				d1 = new Date();
				System.out.println("数据异常："+sc1);
			}
			if(d2 == null){
				d2 = new Date();
				System.out.println("数据异常："+sc2);
			}
			if (d1.getTime() < d2.getTime()) {
				return 0;
			} else if(d1.getTime() == d2.getTime()){
				if(sc1.getId() <= sc2.getId()){
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		} else {
			return -1;
		}
	}

}
