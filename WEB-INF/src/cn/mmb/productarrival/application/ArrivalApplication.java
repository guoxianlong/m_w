/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:52:15 
 * @version V1.0   
 */
package cn.mmb.productarrival.application;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.mmb.productarrival.domain.model.ArrivalMessageModel;
import cn.mmb.productarrival.domain.service.ArrivalServiceImpl;

/** 
 * @ClassName: ArrivalApplication 
 * @Description: 商品到货信息应用服务层
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:52:15  
 */
@Component
public class ArrivalApplication {
	
	@Resource
	private ArrivalServiceImpl arrivalServiceImpl;

	@Transactional(rollbackFor=Exception.class)
	public void addArrivalMessage(ArrivalMessageModel model) throws Exception {
		arrivalServiceImpl.addArrivalMessage(model);
	}

}
