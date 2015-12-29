package mmb.finance.service.impl;


import java.sql.PreparedStatement;
import java.util.List;

import mmb.finance.service.InitVerificationService;

import org.apache.log4j.Logger;

import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.db.DbOperation;

/**
 * 初始化核销数据Service
 * @description:每个sku生成一条核销数据
 * @create:2014年5月16日 下午3:39:40
 * @author:mawanjun
 */
public class InitVerificationServiceImpl extends BaseServiceImpl implements InitVerificationService {
	
	private static Logger log = Logger.getLogger(InitVerificationServiceImpl.class);

	@Override
	public void insertRInitData(List<BuyStockinProductBean> beanList,int userID,DbOperation conn) throws Exception{
		IBaseService service = null;
		try {
			conn.init();
			service = new BaseServiceImpl(IBalanceService.CONN_IN_SERVICE, conn);
			PreparedStatement ps = null;
			
			//修改发票订单信息
			//'单子类型:1-R,2-T,3
			String sql = "insert into invoice_verification set "
					+ "	order_code=(SELECT buy_order.CODE from buy_stockin LEFT JOIN buy_order "
					+ "on buy_stockin.buy_order_id=buy_order.id where buy_stockin.id=? LIMIT 0,1),"
					+ "association_code=(SELECT code from buy_stockin where id=?),"
					+ "association_type=1,product_code=(select code from product where id=?),"
					+ "product_name=(select name from product where id=?),"
					+ "finish_count=0,unfinish_count=?,status=0,price=(SELECT purchase_price from buy_order_product "
					+ "where buy_order_id=(select buy_order_id from buy_stockin where id=?) "
					+ "and product_id=? limit 0,1),amount=0,type=0,create_user_id=?";
			ps = service.getDbOp().getConn().prepareStatement(sql);
			for (BuyStockinProductBean bean : beanList) {
				ps.setInt(1, bean.getBuyStockinId());
				ps.setInt(2, bean.getBuyStockinId());
				ps.setInt(3, bean.getProductId());
				ps.setInt(4, bean.getProductId());
				ps.setInt(5, bean.getStockInCount());
				ps.setInt(6, bean.getBuyStockinId());
				ps.setInt(7, bean.getProductId());
				ps.setInt(8, userID);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("初始正向核销数据时出现异常：", e);
			throw e;
		}
	}
}
