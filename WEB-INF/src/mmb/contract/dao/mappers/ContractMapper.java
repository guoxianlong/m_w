/** 
* @Description: TODO
* @author: 叶二鹏  
* @date: 2014年8月22日 下午2:54:44 
* @version V1.0   
*/
package mmb.contract.dao.mappers;

import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.contract.dao.ContractDao;
import mmb.contract.dto.SupplierContractMoney;
import mmb.contract.dto.SupplierContractReturn;

/** 
 * @ClassName: ContractMapper 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2014年8月22日 下午2:54:44  
 */
@Repository
public class ContractMapper extends AbstractDaoSupport implements ContractDao {

	@Override
	public List<SupplierContractReturn> getreturnsByOrderId(int order) {
		return this.getSession().selectList(order);
	}

	@Override
	public Double getContractTotal(int contractId) {
		return this.getSession().selectOne(contractId);
	}
	
	@Override
    public Double getContractReturnTotal(int contractId) {
		return this.getSession().selectOne(contractId);
    }

	@Override
	public void insertContractMoney(SupplierContractMoney monyInfo) {
		this.getSession().insert(monyInfo);
	}

	@Override
	public Integer getSupplierIdFromContractInfo(int contractId) {
		return this.getSession().selectOne(contractId);
	}
	
	@Override
	public Double getSendedMoney(int contractId) {
		return this.getSession().selectOne(contractId);
	}

	@Override
	public void updateContractReturn(SupplierContractReturn rt) {
		this.getSession().update(rt);
	}

}
