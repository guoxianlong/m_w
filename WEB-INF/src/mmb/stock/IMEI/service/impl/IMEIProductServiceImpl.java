package mmb.stock.IMEI.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.stock.IMEI.dao.IMEIBeanDao;
import mmb.stock.IMEI.model.ImeiProductLog;
import mmb.stock.IMEI.service.IMEIProductService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IMEIProductServiceImpl implements IMEIProductService {
	
	@Resource
	private IMEIBeanDao imeiBeanDao;
	
	/**
	 * 保存数据到数据库
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void saveImeiProductSet(List<ImeiProductLog> list,List<String> productCodeList) {
		imeiBeanDao.saveImeiProductLog(list);
		List<Integer> productIdList = imeiBeanDao.queryProductId(productCodeList);
		if(!productIdList.isEmpty()){
			imeiBeanDao.saveImeiProduct(productIdList); 
		}
	}
	
	/**
	 * 查询IMEI产品属性设置表
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@Override
	public List<ImeiProductLog> queryIMEIProductLog(int pageNo, int pageSize) {
		return imeiBeanDao.queryIMEIProductLog(pageNo,pageSize);
	}
	
	/**
	 * 查询IMEI产品属性设置表总记录数
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	@Override
	public int queryIMEIProductLogCount(Map<String,String> map) {
		return imeiBeanDao.queryIMEIProductLogCount(map);
	}
	
	/**
	 * 返回已经存在的productCodeList
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@Override
	public List<String> queryIMEIProductCode(List<String> productCodeList) {
		return imeiBeanDao.queryIMEIProductCode(productCodeList);
	}

}
