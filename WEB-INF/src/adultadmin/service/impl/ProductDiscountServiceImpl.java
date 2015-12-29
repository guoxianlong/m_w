/*
 * 
 */
package adultadmin.service.impl;

import java.util.List;

import adultadmin.bean.productProperty.CashTicketInfoBean;
import adultadmin.service.infc.IProductDiscountService;
import adultadmin.util.db.DbOperation;

/**
 * 
 * @author zhangtao
 *
 */
public class ProductDiscountServiceImpl extends BaseServiceImpl implements IProductDiscountService {
	
	public static final int EXECUTE_NUM=4;
	public ProductDiscountServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ProductDiscountServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}
	 
	public List getResultInfoList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cash_ticket_info", "adultadmin.bean.productProperty.CashTicketInfoBean");
	}
	
	public CashTicketInfoBean getCashTicketInfoBean(String condition){
		return (CashTicketInfoBean) getXXX(condition, "cash_ticket_info", "adultadmin.bean.productProperty.CashTicketInfoBean");
	}
}
