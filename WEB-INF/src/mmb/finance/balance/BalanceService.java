package mmb.finance.balance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import mmb.finance.stat.FinanceSellBean;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class BalanceService extends  BaseServiceImpl{
	
		protected Connection conn = null;
		
		public BalanceService(int useConnType, DbOperation dbOp){
			this.useConnType = useConnType;
			this.dbOp = dbOp;
			this.conn = dbOp.getConn();
		}
		public BalanceService(){
			this.useConnType = CONN_IN_SERVICE;
		}
		
		/**
		 * 说明：异常结算数据导入
		 * 
		 * 作者：刘瑞兰
		 * 
		 * 时间：2012-11-29
		 */
		public ArrayList getFinanceMailingBalanceBeanList(String condition, int index, int count, String orderBy) {
			return getXXXList(condition, index, count, orderBy, "finance_mailing_balance",
			"mmb.finance.balance.FinanceMailingBalanceBean");
		}
		
		public FinanceMailingBalanceBean getFinanceMailingBalanceBean(String condition) {
			return (FinanceMailingBalanceBean) getXXX(condition, "finance_mailing_balance",
			"mmb.finance.balance.FinanceMailingBalanceBean");
		}
		
		public boolean addFinanceMailingBalanceBean(FinanceMailingBalanceBean financeMailingBalanceBean) {
			return addXXX(financeMailingBalanceBean, "finance_mailing_balance");
		}
		
		public boolean updateFinanceMailingBalanceBean(String set,String condition){
			return updateXXX(set, condition, "finance_mailing_balance");
		}
		
		public int getFinanceMailingBalanceCount(String condition){
			return getXXXCount(condition, "finance_mailing_balance", "id");
		}
		
		public boolean deleteFinanceMailingBalanceBean(String condition) {
			return deleteXXX(condition, "finance_mailing_balance");
		}

}	
