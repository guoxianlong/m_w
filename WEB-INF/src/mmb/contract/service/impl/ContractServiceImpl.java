/** 
* @Description: TODO
* @author: 叶二鹏  
* @date: 2014年8月22日 下午2:53:08 
* @version V1.0   
*/
package mmb.contract.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mmb.contract.dao.ContractDao;
import mmb.contract.dto.SupplierContractMoney;
import mmb.contract.dto.SupplierContractReturn;
import mmb.contract.service.ContractService;

/** 
 * @ClassName: ContractServiceImpl 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2014年8月22日 下午2:53:08  
 */
@Service
public class ContractServiceImpl implements ContractService {
	
	@Autowired
	private ContractDao commonDao;

	@Override
	public boolean dealContractMony(int order) {
		List<SupplierContractReturn> returnType = commonDao
				.getreturnsByOrderId(order);
		if (returnType.size() == 1) {
			SupplierContractReturn rt = returnType.get(0);
			int supplierId = commonDao.getSupplierIdFromContractInfo(rt.getContractId());
			SupplierContractMoney monyInfo = new SupplierContractMoney();
			double money = commonDao.getContractTotal(rt.getContractId());// 获取订单所有金额
			double returnMony = commonDao.getContractReturnTotal(rt.getContractId());// 获取订单退回总金额
			// 订单总金额
			double allMoney = money - returnMony;
			// 返利金额
			double contractMony = 0;
			if (rt.getType() == SupplierContractReturn.NO_LATTER) {// 不阶梯
				// 不阶梯并且只有一个条件
				if (allMoney < rt.getTriggerMoneyCash()) {// 如果达不到返利标准，则不记录
					return true;
				} else if (rt.getIsReturned() == SupplierContractReturn.IS_RETURNED_YES) {
					//如果范送过，则不在计算
					return true;
				}else if (rt.getReturnCash() > 0) {// 返现金
					contractMony = rt.getReturnCash();
				} else {// 反百分比
					contractMony = rt.getTriggerMoneyCash()
							* rt.getReturnPercent() / 100;
				}
				monyInfo.setReturnType(SupplierContractReturn.LATTER_NO_ONE);
				monyInfo.setApplicationCode("不阶梯满足条件返利");
			} else {// 阶梯
				if (rt.getReturnCash() > 0) {// 返现金
					contractMony = rt.getReturnCash()
							* ((int) allMoney / (int) rt.getTriggerMoneyCash());
				} else {// 反百分比
					contractMony = rt.getTriggerMoneyCash()
							* rt.getReturnPercent() / 100
							* ((int) allMoney / (int) rt.getTriggerMoneyCash());
				}
				double sendedMoney = commonDao.getSendedMoney(rt.getContractId());//已返送过的金额
				contractMony = contractMony - sendedMoney;
				if (contractMony <= 0.0) {//如果计算出的返利金额为0或小于0，说明此次入库达不到生成新返利的条件
					return true;
				}
				monyInfo.setReturnType(SupplierContractReturn.LATTER_YES);
				monyInfo.setApplicationCode("阶梯满足条件返利");
			}
			BigDecimal  bd = new  BigDecimal(contractMony);
			BigDecimal  bd1 = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
			contractMony = bd1.doubleValue();
			monyInfo.setSupplierId(supplierId);
			monyInfo.setContractId(rt.getContractId());
			monyInfo.setOrderMoney(allMoney);
			monyInfo.setContractMoney(contractMony);
			monyInfo.setRestMoney(contractMony);
			monyInfo.setDataType(SupplierContractReturn.TYPE_ADD);
			monyInfo.setShareMoney(0);
			commonDao.insertContractMoney(monyInfo);
			rt.setIsReturned(SupplierContractReturn.IS_RETURNED_YES);
			commonDao.updateContractReturn(rt);
		}
		return true;
	}
	

}
