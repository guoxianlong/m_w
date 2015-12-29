/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:42:21 
 * @version V1.0   
 */
package cn.mmb.productarrival.domain.service;

import cn.mmb.productarrival.domain.model.ArrivalMessageModel;
import cn.mmb.productarrival.infrastructrue.transdto.EasyuiPage;
import cn.mmb.productarrival.infrastructrue.transdto.QueryParams;

/** 
 * @ClassName: ProductArrivalService 
 * @Description: 商品到货服务接口
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:42:21  
 */
public interface ArrivalService {
	
	/** 
	 * @Description: 查询到货信息列表
	 * @return EasyuiPage<ArrivalMessageModel> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月14日 下午3:10:16 
	 */
	public EasyuiPage<ArrivalMessageModel> getArrivalPage(QueryParams params,
			EasyuiPage<ArrivalMessageModel> page);
	
	/** 
	 * @Description: 添加到货信息
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月15日 下午3:49:39 
	 */
	public void addArrivalMessage(ArrivalMessageModel model);

}
