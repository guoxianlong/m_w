package mmb.stock.stat;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class TempNumberService extends BaseServiceImpl{

	private final Log logger = LogFactory.getLog(TempNumberService.class);
	
	
	public TempNumberService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public TempNumberService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	
	/**
	 * 添加暂存号
	 * @param bean
	 * @return
	 */
	public boolean addTemporaryNum(TemporaryNumberBean bean) {
		try{
			this.getDbOp().startTransaction();
			if(!addXXX(bean, "temporary_number")){
				this.getDbOp().rollbackTransaction();
				return false;
			}
			this.getDbOp().commitTransaction();
			return true;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method addTemporaryNum exception", e);
			}
			this.getDbOp().rollbackTransaction();
			return false;
		}
	}

	
	/**
	 * 查询暂存号
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getTemporaryNumList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "temporary_number", "mmb.stock.stat.TemporaryNumberBean");
	}
	
	public int getTemporaryNumCount(String condition) {
		return getXXXCount(condition, "temporary_number", "id");
	}

	public TemporaryNumberBean getTemporaryNum(String condition) {
		return (TemporaryNumberBean) getXXX(condition, "temporary_number",
		"mmb.stock.stat.TemporaryNumberBean");
	}

	public boolean updateTemporaryNum(String set, String condition) {
		return updateXXX(set, condition, "temporary_number");
	}

	public boolean deleteTemporaryNum(String condition) {
		return deleteXXX(condition, "temporary_number");
	}
	
}




