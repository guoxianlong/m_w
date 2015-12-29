package mmb.aftersale;


import java.sql.ResultSet;

import adultadmin.action.vo.voUser;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

/**
 * 作者：曹续
 * 
 * 创建日期：2009-9-1
 * 
 * WalletUtil.java
 * 
 */
public class WalletUtil {

	
	/**
	 * 作者：曹续
	 *
	 * 创建时间：2009-11-27
	 * 
	 * 钱包退款，只用于后台，传入一个RefundOrderBean
	 *
	 * 参数及返回值说明：
	 *
	 * @param bean
	 * @param dbOp
	 * @return
	 */
	public int refund(RefundOrderBean bean , DbOperation dbOp){
		Object lock = LockUtil.userLock.getLock(bean.getUserId());
		synchronized (lock) {
			WalletService service = null;
			if (dbOp != null) {
				service = new WalletService(IBaseService.CONN_FROM_OUTSIDE,
						dbOp);
				// 不用开始事务，事务应该在本函数外面开始及提交
			}
			// 函数内部自己用一个数据库连接
			else {
				service = new WalletService(IBaseService.CONN_IN_SERVICE,
						null);
				// 开始事务
				if (!service.getDbOp().startTransaction()) {
					// 释放掉数据库连接（同时事务会不被执行）
					service.releaseAll();
					return WalletCodeMap.DATA_BASE_ERROR;
				}
			}
			MainInfoBean mainInfo = service.getMainInfo("user_id="
					+ bean.getUserId());
			if(mainInfo==null){
				if (dbOp == null) {
					service.releaseAll();
				}
				return -11; 
			}
			float totalAmountOld = mainInfo.getTotalAmount();
			float totalAmountNew = totalAmountOld + bean.getRefundAmount();
			float amountOld = mainInfo.getAmount();
			float amountNew = mainInfo.getAmount()+ bean.getRefundAmount();
			float freezeOld = mainInfo.getFreezeAmount();
			float freezeNew = freezeOld;
			String now = DateUtil.getNow();
			String today = DateUtil.getNow().substring(0, 10);
			// 添加操作记录。
			OperLogBean operLog = new OperLogBean();
			operLog.setUserId(bean.getUserId());
			operLog.setActType(WalletCodeMap.OPER_LOG_ACT_TYPE_REFUND); // -1退款
			operLog.setOrderId(bean.getId());
			operLog.setOrderType(bean.getOrderType());
			operLog.setOrderAmount(bean.getRefundAmount());
			operLog.setTime(now);
			operLog.setAmountOld(amountOld);
			operLog.setAmountNew(amountNew);
			operLog.setFreezeOld(freezeOld);
			operLog.setFreezeNew(freezeNew);
			operLog.setLogDate(today);
			if (!service.addOperLog(operLog)) {
				// 不是跟外部共用一个数据库连接
				if (dbOp == null) {
					// 释放掉数据库连接（同时事务会不被执行）
					service.releaseAll();
				}
				return WalletCodeMap.DATA_BASE_ERROR;
			}
			//添加流水记录
			int doneOrderId = service.getNumber("id", "wallet.done_order", "max", "id>0")+1;
			DoneOrderBean doneOrder = new DoneOrderBean();
			doneOrder.setId(doneOrderId);
			doneOrder.setUserId(bean.getUserId());
			doneOrder.setActType(WalletCodeMap.ORDER_ACT_TYPE_REFUND); // 退款
			doneOrder.setOrderId(bean.getId());
			doneOrder.setOrderType(bean.getOrderType());
			doneOrder.setOrderAmount(bean.getRefundAmount());
			doneOrder.setTime(now);
			doneOrder.setTotalAmountOld(totalAmountOld);
			doneOrder.setTotalAmountNew(totalAmountNew);
			doneOrder.setLogDate(today);
			if (!service.addDoneOrder(doneOrder)) {
				// 不是跟外部共用一个数据库连接
				if (dbOp == null) {
					// 释放掉数据库连接（同时事务会不被执行）
					service.releaseAll();
				}
				return WalletCodeMap.DATA_BASE_ERROR;
			}
			// 更新用户余额
			String set = "amount = " + amountNew + ",freeze_amount = " + freezeNew
					+ ",total_amount = " + totalAmountNew ;
			String condition = "user_id = " + bean.getUserId();
			if (!service.updateMainInfo(set, condition)) {
				// 不是跟外部共用一个数据库连接
				if (dbOp == null) {
					// 释放掉数据库连接（同时事务会不被执行）
					service.releaseAll();
				}
				return WalletCodeMap.DATA_BASE_ERROR;
			}
			
			// 函数内部自己用一个数据库连接
			if (dbOp == null) {
				// 需要提交事务
				if (!service.getDbOp().commitTransaction()) {
					service.releaseAll();
					return WalletCodeMap.DATA_BASE_ERROR;
				}
				// 需要释放数据库连接
				service.releaseAll();
			}
			return WalletCodeMap.SUCCESS;
		}
	}
	
	/**
	 * 
	 * 功能:激活用户钱包
	 * <p>作者 李双 Mar 12, 2013 5:48:18 PM
	 * @param dbOp
	 * @param userId
	 * @return
	 */
	
	public static boolean activetionWallet(DbOperation dbOp,int userId){
		  WalletService walletService = new WalletService(
                  IBaseService.CONN_FROM_OUTSIDE, dbOp);
          
          MainInfoBean mainInfo =null;
          SecurityInfoBean securityInfo = null;
     
          mainInfo = new MainInfoBean();
          mainInfo.setUserId(userId);
          securityInfo = new SecurityInfoBean();
          securityInfo.setUserId(userId);
          
          if(walletService.getMainInfoCount("user_id="+userId)>0){//已经激活了
        	  return true;
          }
          
          if(!walletService.addMainInfo(mainInfo)){
        	  return false;
          }
          
          if(!walletService.addSecurityInfo(securityInfo)){
        	  return false;
          }
          
          return true;
	}
	
	/**
	 * 
	 * 功能:售后自动退款
	 * <p>作者 李双 Mar 12, 2013 5:51:05 PM
	 * @param username  dbOpOutSide ,如果参数dbOpOutSide==null说明是退款内部控制事务，如果!=null,说明在外部控制事务
	 * @return -1 不存在该用户
	 */
	public int afterSaleRefund(String username,int orderId,float refundAmount,DbOperation dbOpOutSide){
		DbOperation dbOp = null;
		if(dbOpOutSide != null){
			dbOp = dbOpOutSide;
		}else{
			dbOp = new  DbOperation();
			dbOp.init(DbOperation.DB);
		}
		IUserService userService = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try{
			voUser vo = userService.getUser("username ='"+username+"'");
			if(vo==null){
				return -1;
			}
			if(!activetionWallet(dbOp,vo.getId())){
				return -12;
			}
			
			RefundOrderBean bean = new RefundOrderBean();
			ResultSet rs = dbOp.executeQuery("select code,order_type from user_order where id = "+orderId);
			if(rs.next()){
				bean.setOrderCode(rs.getString("code"));
				bean.setOrderType(rs.getInt("order_type"));
			}
			bean.setUserId(vo.getId());
			bean.setRefundAmount(refundAmount);
			bean.setId(orderId);
			dbOp.executeUpdate("update user_order set user_id="+vo.getId()+" where id = "+orderId);
			if(dbOpOutSide != null){
				return refund(bean ,dbOpOutSide);
			}else{
				return refund(bean ,null);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOpOutSide == null){//如果不是外部传进来的数据连接,需要内部释放
				userService.releaseAll();
			}
		}
		return -1 ;
	}
}

