package mmb.delivery.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * POP商家
 * @author likaige
 * @create 2015年5月15日 下午2:51:24
 */
public class PopBussiness {

	/** POP商家：买卖宝 */
	public static final int POP_MMB = 0;
	/** POP商家：京东 */
	public static final int POP_JD = 2;
	
	/** POP商家名称 */
	public static Map<Integer, String> popMap = new HashMap<Integer, String>();
	static{
		popMap.put(POP_MMB, "买卖宝");
		popMap.put(POP_JD, "京东");
	}
	
}
