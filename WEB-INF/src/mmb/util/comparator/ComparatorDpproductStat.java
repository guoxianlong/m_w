package mmb.util.comparator;

import java.util.Comparator;

import adultadmin.bean.stat.DpproductStatBean;
import adultadmin.util.NumberUtil;

public class ComparatorDpproductStat implements Comparator<DpproductStatBean> {
	
	@Override
	public int compare(DpproductStatBean o1, DpproductStatBean o2) {
		int result = -NumberUtil.compare(o1.getFrequencyCount(), o2.getFrequencyCount());
		if(result == 0){
			result = NumberUtil.compare(o1.getProductId(), o2.getProductId());
		}
		return result;
	}
	
}