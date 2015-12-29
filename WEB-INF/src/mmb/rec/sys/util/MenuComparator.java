package mmb.rec.sys.util;

import java.util.Comparator;

import mmb.rec.sys.bean.WareMenuBean;

import org.apache.log4j.Logger;


/**
 * 菜单排序
 * 
 */
public class MenuComparator implements Comparator<WareMenuBean> {

	private static final Logger logger = Logger.getLogger(MenuComparator.class);

	public int compare(WareMenuBean o1, WareMenuBean o2) {
		int i1;
		if (o1.getSeq() != null)
			i1 = o1.getSeq().intValue();
		else
			i1 = -1;
		int i2;
		if (o2.getSeq() != null)
			i2 = o2.getSeq().intValue();
		else
			i2 = -1;
		logger.debug("i1[" + i1 + "]-i2[" + i2 + "]=" + (i1 - i2));
		return i1 - i2;
	}
}


