package mmb.finance.service;

import java.util.List;

import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.db.DbOperation;

/**
 * 初始化核销数据Service
 * @description:每个sku生成一条核销数据
 * @create:2014年5月16日 下午3:39:40
 * @author:mawanjun
 */
public interface InitVerificationService extends IBaseService {
	/**
	 * 添加入库单核销数据
	 * @description:
	 * @param bean
	 * @returnType: void
	 * @create:2014年5月16日 下午3:41:56
	 * @author:mawanjun
	 */
	void insertRInitData(List<BuyStockinProductBean> beanList,int userID,DbOperation dbop)throws Exception;
	/**
	 * TH、T单的初始数据在adminBuyReturn里加
	 */
}
