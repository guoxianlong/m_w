package mmb.common.service;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import mmb.common.dao.CommonDao;
import mmb.common.dao.OrderProductDao;
import mmb.common.dao.ProductBarcodeDao;
import mmb.common.dao.ProductDao;
import mmb.common.dao.UserOrderDao;
import mmb.common.dao.UserOrderExtendInfoDao;


/**
 * 新构造的使用mybatis的Service
 * @author Hao yabin
 *
 */
@Service
public class MWareService {

	@Autowired
	public ProductDao productMapper;
	@Autowired
	public UserOrderDao userOrderMapper;
	@Autowired
	public OrderProductDao orderProductMapper;
	@Autowired 
	public ProductBarcodeDao productBarcodeMapper;
	@Autowired
	public UserOrderExtendInfoDao userOrderExtendInfoMapper;
	@Autowired
	public CommonDao commonMapper;
	
	public voProduct getProduct(String condition) {
		return productMapper.getProduct(condition);
	}

	public voOrder getUserOrder(String condition) {
		return userOrderMapper.getUserOrder(condition);
	}
	
	public List<voOrderProduct> getOrderProductsSplit(int id) {
		return orderProductMapper.getOrderProductsSplit(id);
	}
	
	public List<voOrderProduct> getOrderPresentsSplit(int id) {
		return orderProductMapper.getOrderProductsSplit(id);
	}

	public ProductBarcodeVO getProductBarcode(String condition) {
		return productBarcodeMapper.getProductBarcode(condition);
	}

	//user_order_extend_info
	public int addUserOrderExtendInfo(voOrderExtendInfo orderExtendInfo) {
		return userOrderExtendInfoMapper.addUserOrderExtendInfo(orderExtendInfo);
	}

	public voOrderExtendInfo getUserOrderExtendInfo(String condition) {
		return userOrderExtendInfoMapper.getUserOrderExtendInfo(condition);
	}

	public List<voOrderExtendInfo> getUserOrderExtendInfoList(
			Map<String, String> paramMap) {
		return userOrderExtendInfoMapper.getUserOrderExtendInfoList(paramMap);
	}
	
	public int deleteUserOrderExtendInfo(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("user_order_extend_info", condition);
		return commonMapper.deleteCommon(paramMap);
	}
	
	public int updateUserOrderExtendInfo(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("user_order_extend_info", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getUserOrderExtendInfoCount(String condition ) {
		Map<String,String> paramMap = CommonService.constructCountMap("user_order_extend_info", condition);
		return commonMapper.getCommonCount(paramMap);
	}

}
