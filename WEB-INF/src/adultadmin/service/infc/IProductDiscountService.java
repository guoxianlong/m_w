/**
 * 
 */
package adultadmin.service.infc;

import java.util.List;

import adultadmin.bean.productProperty.CashTicketInfoBean;

/**
 * 
 *
 */
public interface IProductDiscountService extends IBaseService {

    public List getResultInfoList(String condition,int index,int count, String orderBy);
    
    public CashTicketInfoBean getCashTicketInfoBean(String condition);
    
}
