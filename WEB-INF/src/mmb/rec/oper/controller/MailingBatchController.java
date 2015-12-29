package mmb.rec.oper.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.rec.sys.util.BeanColumnToTableColumn;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.MailingBatchBean;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.bean.stock.MailingBatchParcelBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/MailingBatchController")
public class MailingBatchController {
	public static byte[] MailingBatchLock = new byte[0];
	/**
	 *说明：导出邮包明细
	 *@author 石远飞
	 */
	@RequestMapping("/mailingParcelDetailPrint")
	public String mailingParcelDetailPrint(HttpServletRequest request,HttpServletResponse response,String code) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(440)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			StringBuilder url = new StringBuilder();
		    url.append("mailingBatch.do?method=mailingParcelDetailPrint");
			    List<MailingBatchPackageBean> mbpList = new ArrayList<MailingBatchPackageBean>();
				String sql = "SELECT a.store,uo.buy_mode,c.create_datetime,c.weight,c.order_code,c.package_code,c.address,c.total_price FROM mailing_batch a left join mailing_batch_parcel  b on a.code=b.mailing_batch_code  left join mailing_batch_package c on b.mailing_batch_code=c.mailing_batch_code left join user_order uo on c.order_id=uo.id where c.mailing_batch_parcel_code='"+StringUtil.toSql(code)+"' group by c.id";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					MailingBatchPackageBean batchPackageBean = new MailingBatchPackageBean();
					MailingBatchBean mbb = new MailingBatchBean();
					batchPackageBean.setCreateDatetime(rs.getString("c.create_datetime").substring(0,11));
					batchPackageBean.setOrderCode(rs.getString("c.order_code"));
					batchPackageBean.setAddress(rs.getString("c.address"));
					batchPackageBean.setWeight(rs.getFloat("c.weight"));
					batchPackageBean.setTotalPrice(rs.getFloat("c.total_price"));
					batchPackageBean.setBuyMode(rs.getInt("uo.buy_mode"));
					batchPackageBean.setPackageCode(rs.getString("c.package_code"));
					mbb.setStore(rs.getString("a.store"));
					batchPackageBean.setMailingBatchBean(mbb);
					mbpList.add(batchPackageBean);
				}
			request.setAttribute("list", mbpList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		 return "admin/productStock/mailingParcelDetailPrint";
	}
	/**
	 *说明：打印邮包明细
	 *@author 石远飞
	 */
	@RequestMapping("/mailingParcelDetailExcel")
	public String mailingParcelDetailExcel(HttpServletRequest request,String code) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			return "admin/rec/err";
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(440)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			return "admin/rec/err";
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			StringBuilder url = new StringBuilder();
		    url.append("mailingBatch.do?method=mailingParcelDetailPrint");
			    List<MailingBatchPackageBean> mbpList = new ArrayList<MailingBatchPackageBean>();
				String sql = "SELECT a.store,uo.buy_mode,c.create_datetime,c.weight,c.order_code,c.package_code,c.address,c.total_price FROM mailing_batch a left join mailing_batch_parcel  b on a.code=b.mailing_batch_code  left join mailing_batch_package c on b.mailing_batch_code=c.mailing_batch_code left join user_order uo on c.order_id=uo.id where c.mailing_batch_parcel_code='"+StringUtil.toSql(code)+"' group by c.id";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					MailingBatchPackageBean batchPackageBean = new MailingBatchPackageBean();
					MailingBatchBean mbb = new MailingBatchBean();
					batchPackageBean.setCreateDatetime(rs.getString("c.create_datetime").substring(0,11));
					batchPackageBean.setOrderCode(rs.getString("c.order_code"));
					batchPackageBean.setAddress(rs.getString("c.address"));
					batchPackageBean.setWeight(rs.getFloat("c.weight"));
					batchPackageBean.setTotalPrice(rs.getFloat("c.total_price"));
					batchPackageBean.setPackageCode(rs.getString("c.package_code"));
					batchPackageBean.setBuyMode(rs.getInt("uo.buy_mode"));
					mbb.setStore(rs.getString("a.store"));
					batchPackageBean.setMailingBatchBean(mbb);
					mbpList.add(batchPackageBean);
				}
			request.setAttribute("list", mbpList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return "admin/productStock/mailingParcelDetailExcel";
	}
	/**
	 *说明：货物运输交接单打印
	 *@author 石远飞
	 */
	@RequestMapping("/cargoTransportDeliveryReceiptPrint")
	public String cargoTransportDeliveryReceiptPrint(HttpServletRequest request,String code) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败!");
			return "admin/rec/err";
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(435)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			return "admin/rec/err";
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			List<MailingBatchParcelBean> mbpList = new ArrayList<MailingBatchParcelBean>();
				String sql = "SELECT a.code,b.code,a.store,count(c.id)counts,sum(c.weight)weight FROM mailing_batch a left join mailing_batch_parcel " +
						" b on a.code=b.mailing_batch_code  left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where a.code='"+StringUtil.toSql(code)+"' group by b.id";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					MailingBatchParcelBean batchParcelBean = new MailingBatchParcelBean();
					MailingBatchBean mbb = new MailingBatchBean();
					batchParcelBean.setMailingBatchCode(rs.getString("a.code"));
					batchParcelBean.setCode(rs.getString("b.code"));
					mbb.setStore(rs.getString("a.store"));
					batchParcelBean.setPackageCount(rs.getInt("counts"));
					batchParcelBean.setTotalWeight(rs.getFloat("weight"));
					batchParcelBean.setMbb(mbb);
					mbpList.add(batchParcelBean);
				}
		    MailingBatchBean bean = service.getMailingBatch("code='"+code+"'");
		    if(bean!=null){
		    	request.setAttribute("carrier", bean.getCarrier());
		    }
			request.setAttribute("list", mbpList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return "admin/productStock/cargoTransportDeliveryReceiptPrint";
	}
	/**
	 *说明：添加包裹
	 *@author 石远飞
	 */
	@RequestMapping("/addMailingBatchPackage")
	@ResponseBody
	public Json addMailingBatchPackage(HttpServletRequest request,HttpServletResponse response,String orderCode,
			String packageCode,int mailingBatchId,int parcelId){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+mailingBatchId);
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
			if(mailingBatch==null){
				j.setMsg("该发货波次不存在!");
				return j;
			}
			if(mailingBatch.getStatus()!=0){
				j.setMsg("该发货波次已交接完成!");
				return j;
			}
			if(parcel==null){
				j.setMsg("该发货邮包不存在!");
				return j;
			}
			
			OrderStockBean orderStockBean = stockService.getOrderStock("order_code='"+StringUtil.toSql(orderCode)+"'" + " and status!="+OrderStockBean.STATUS4);
			voOrder order = null;
			if(orderStockBean != null){
				if(mailingBatch.getArea()!=orderStockBean.getStockArea()){
					j.setMsg("订单与发货波次不属于同一地区!");
					return j;
				}
				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
			}else{
				j.setMsg("出库信息错误!");
				return j;
			}
			if(order==null){
				j.setMsg("无此订单!");
				return j;
			}
			MailingBalanceBean mb=baService.getMailingBalance("order_code='"+order.getCode()+"' and packagenum='"+StringUtil.toSql(packageCode)+"'");
			if(mb==null){
				j.setMsg("订单号与包裹单编号不匹配，请核实!");
				return j;
			}
			if(order.getDeliver()!=mailingBatch.getDeliver()){
				j.setMsg("该订单快递公司与波次物流不匹配!");
				return j;
			}
			int packageCount=stockService.getMailingBatchPackageCount("order_code='"+order.getCode()+"' and package_code='"+StringUtil.toSql(packageCode)+"' and balance_status<>3");
			if(packageCount!=0){
				j.setMsg("该包裹已被添加!");
				return j;
			}
			
			AuditPackageBean apBean=stockService.getAuditPackage("order_code='"+order.getCode()+"'");//用于提取包裹重量
			if(apBean==null){
				j.setMsg("包裹信息错误!");
				return j;
			}
			
			MailingBatchPackageBean packageBean=new MailingBatchPackageBean();
			packageBean.setMailingBatchId(mailingBatch.getId());
			packageBean.setMailingBatchCode(mailingBatch.getCode());
			packageBean.setMailingBatchParcelId(parcel.getId());
			packageBean.setMailingBatchParcelCode(parcel.getCode());
			packageBean.setOrderCode(order.getCode());
			packageBean.setPackageCode(packageCode);
			packageBean.setCreateDatetime(DateUtil.getNow());
			packageBean.setAddress(order.getAddress());
			packageBean.setWeight(apBean.getWeight());
			packageBean.setTotalPrice(order.getDprice());
			packageBean.setDeliver(order.getDeliver());
			packageBean.setOrderId(order.getId());
			packageBean.setStockInDatetime("1111-11-11 11:11:11");
			packageBean.setAssignTime("1111-11-11 11:11:11");
			packageBean.setPostStaffId(0);
			packageBean.setPostStaffName("");
			packageBean.setStockInAdminId(0);
			packageBean.setStockInAdminName("");
			packageBean.setMailingStatus(0);
			packageBean.setReturnStatus(0);
			packageBean.setBalanceStatus(0);
			if(!stockService.addMailingBatchPackage(packageBean)){
				j.setMsg("添加包裹失败!");
				return j;
			}
			j.setSuccess(true);
			j.setMsg("添加包裹成功!");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	/**
	 *说明：删除包裹
	 *@author 石远飞
	 */
	@RequestMapping("/deletePackage")
	@ResponseBody
	public Json deletePackage(HttpServletRequest request,HttpServletResponse response,String ids){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(442)){
			j.setMsg("您没有权限进行此操作!");
			return j;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			if(ids == null || "".equals(ids)){
				j.setMsg("未收到要删除的ID!");
				return j;
			}
			dbOp.startTransaction();
			for(String packageId : ids.split(",")){
				MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+packageId);
				if(packageBean==null){
					dbOp.rollbackTransaction();
					j.setMsg("该包裹不存在!");
					return j;
				}
				MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+packageBean.getMailingBatchParcelId());
				if(parcel==null){
					dbOp.rollbackTransaction();
					j.setMsg("该发货邮包不存在!");
					return j;
				}
				MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+packageBean.getMailingBatchId());
				if(mailingBatch==null){
					dbOp.rollbackTransaction();
					j.setMsg("该发货波次不存在!");
					return j;
				}
				if(mailingBatch.getStatus()!=0){
					dbOp.rollbackTransaction();
					j.setMsg("该发货波次已交接完成!");
					return j;
				}
				if(!stockService.deleteMailingBatchPackage("id="+packageId)){
					dbOp.rollbackTransaction();
					j.setMsg("删除包裹失败!");
					return j;
				}
			}
			dbOp.commitTransaction();
			j.setSuccess(true);
			j.setMsg("删除包裹成功!");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	/**
	 *说明：删除邮包
	 *@author 石远飞
	 */
	@RequestMapping("/deleteParcel")
	@ResponseBody
	public Json deleteParcel(HttpServletRequest request,HttpServletResponse response,String ids){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(437)){
			j.setMsg("您没有权限进行此操作!");
			return j;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		int mailingBatchId=0;
		synchronized (MailingBatchLock) {
			try{
				if(ids == null || "".equals(ids)){
					j.setMsg("未收到要删除的ID!");
					return j;
				}
				dbOp.startTransaction();
				for(String parcelId : ids.split(",")){
					MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
					if(parcel==null){
						dbOp.rollbackTransaction();
						j.setMsg("该邮包不存在!");
						return j;
					}
					mailingBatchId=parcel.getMailingBatchId();
					MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+mailingBatchId);
					if(mailingBatch==null){
						dbOp.rollbackTransaction();
						j.setMsg("发货波次不存在!");
						return j;
					}
					if(mailingBatch.getStatus()!=0){
						dbOp.rollbackTransaction();
						j.setMsg("该发货波次已交接完成!");
						return j;
					}
					if(!stockService.deleteMailingBatchParcel("id="+parcelId)){
						dbOp.rollbackTransaction();
						j.setMsg("删除邮包失败!");
						return j;
					}
					MailingBatchPackageBean pack = stockService.getMailingBatchPackage("mailing_batch_parcel_id="+parcelId);
					if(pack != null){
						if(!stockService.deleteMailingBatchPackage("mailing_batch_parcel_id="+parcelId)){
							dbOp.rollbackTransaction();
							j.setMsg("删除邮包失败!");
							return j;
						}
					}
				}
				dbOp.commitTransaction();
				j.setSuccess(true);
				j.setMsg("删除邮包成功!");
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				wareService.releaseAll();
			}
		}
		return j;
	}
	/**
	 *说明：交接完成确认
	 *@author 石远飞
	 */
	@RequestMapping("/transitMailingBatch")
	@ResponseBody
	public Json transitMailingBatch(HttpServletRequest request,HttpServletResponse response,int mailingBatchId){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(433)){
			j.setMsg("您没有权限进行此操作!");
			return j;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		synchronized (MailingBatchLock) {
		try{
			dbOp.startTransaction();
			MailingBatchBean batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				j.setMsg("该发货波次不存在!");
				return j;
			}
			if(batch.getStatus()!=0){
				j.setMsg("该发货波次已交接!");
				return j;
			}
			//出库后所有邮包状态改为已复核
			@SuppressWarnings("unchecked")
			List<MailingBatchParcelBean> parcelList=stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), -1, -1, null);
			for(int i=0;i<parcelList.size();i++){
				MailingBatchParcelBean parcel=(MailingBatchParcelBean)parcelList.get(i);
				if(!stockService.updateMailingBatchParcel("status=1", "id="+parcel.getId())){
					dbOp.rollbackTransaction();
					j.setMsg("交接完成失败!");
					return j;
				}
			}
			if(!stockService.updateMailingBatch("status=1,transit_admin_id="+user.getId()+",transit_admin_name='"+user.getUsername()+"'", "id="+mailingBatchId)){
				dbOp.rollbackTransaction();
				j.setMsg("交接完成失败!");
				return j;
			}
			dbOp.commitTransaction();
			j.setSuccess(true);
			j.setMsg("交接完成成功!");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		}
		return j;
	}
	/**
	 *说明：添加邮包
	 *@author 石远飞
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/addMailingBatchParcel")
	@ResponseBody
	public Json addMailingBatchParcel(HttpServletRequest request,HttpServletResponse response,int mailingBatchId){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(434)){
			j.setMsg("您没有权限进行此操作!");
			return j;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			MailingBatchBean batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				j.setMsg("该发货波次不存在!");
				return j;
			}
			if(batch.getStatus()!=0){
				j.setMsg("该发货波次已交接!");
				return j;
			}
			MailingBatchParcelBean parcel=new MailingBatchParcelBean();
			parcel.setMailingBatchId(batch.getId());
			parcel.setMailingBatchCode(batch.getCode());
			
			List<MailingBatchParcelBean> parcelList = stockService.getMailingBatchParcelList("mailing_batch_id="+mailingBatchId, -1, -1, "id desc");
			if(parcelList.size()>0){
				MailingBatchParcelBean parcelBean=(MailingBatchParcelBean)parcelList.get(0);
				if(parcelBean.getCode().substring(14,16).equals("99")){
					j.setMsg("邮包最多添加99个!");
					return j;
				}
			}
			if(parcelList.size()==0){
				parcel.setCode(batch.getCode()+"01");
			}else{
				MailingBatchParcelBean firstParcel=(MailingBatchParcelBean)parcelList.get(0);
				String parcelCode=firstParcel.getCode();
				int parcelNum=Integer.parseInt(parcelCode.substring(parcelCode.length()-2,parcelCode.length()))+1;
				parcel.setCode(batch.getCode()+(parcelNum<10?"0":"")+parcelNum);
			}
			if(!stockService.addMailingBatchParcel(parcel)){
				j.setMsg("添加邮包失败!");
				return j;
			}
			j.setSuccess(true);
			j.setMsg("添加邮包成功!");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return j;
	}
	/**
	 *说明：获取包裹列表
	 *@author 石远飞
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getPackageDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getPackageDatagrid(HttpServletRequest request,HttpServletResponse response,int parcelId) throws ServletException, IOException {
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
			
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(430)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		dbOp_slave.init("adult_slave");
		WareService wareService = new WareService(dbOp_slave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		try{
			//包裹列表
			List<MailingBatchPackageBean> packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcelId+" and balance_status<>3", -1, -1, null);//包裹列表
			if(packageList != null && packageList.size() > 0){
				for(MailingBatchPackageBean packag : packageList){
					String buyModeName = "";
					if(packag.getBuyMode()==0){
						buyModeName = "货到付款";
					}else{
						buyModeName = "已到款";
					}
					packag.setBuyModeName(buyModeName);
					packag.setDeliverName(voOrder.deliverMapAll.get(packag.getDeliver()+"")+"");
					packag.setWeight(packag.getWeight()/1000);
					String address = "";
					if(packag.getAddress().indexOf("自治区")>0){
						address = packag.getAddress().substring(0,12);
					}else{
						address = packag.getAddress().substring(0,8);
					}
					packag.setAddress(address);
					DecimalFormat dcmFmt = new DecimalFormat("0.00");
					packag.setTotalPriceStr(dcmFmt.format(packag.getTotalPrice()));
				}
			}
			datagridJson.setRows(packageList);
			datagridJson.setFooter(null);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return datagridJson;
	}
	/**
	 *说明：获取邮包列表
	 *@author 石远飞
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getMailingBatchDetailDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getMailingBatchDetailDatagrid(HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,MailingBatchBean mailingBatch,int mailingBatchId) throws ServletException, IOException {
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
			
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(430)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		dbOp_slave.init("adult_slave");
		WareService wareService = new WareService(dbOp_slave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		try{
			MailingBatchBean batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				request.setAttribute("msg", "该发货波次不存在!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			int totalCount = stockService.getMailingBatchParcelCount("mailing_batch_id="+batch.getId());
			datagridJson.setTotal(Long.parseLong(totalCount+""));
			String orderBy = null;
			if (easyuiPage.getSort() != null && !"".equals(easyuiPage.getSort().trim())) {// 设置排序
				orderBy = "";
				orderBy+=BeanColumnToTableColumn.Transform(easyuiPage.getSort()) + " " + easyuiPage.getOrder();
			}
			if(orderBy == null || "".equals(orderBy)){
				orderBy = "id desc";
			}
			//邮包列表
			List<MailingBatchParcelBean> parcelList = stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), orderBy);
			if(parcelList != null && parcelList.size() > 0){
				for(MailingBatchParcelBean parcel : parcelList){
					List<MailingBatchPackageBean> packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcel.getId()+" and balance_status<>3", -1, -1, null);//包裹列表
					parcel.setMailingBatchPackageList(packageList);
					for(int j=0;j<packageList.size();j++){
						MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j);
						float weight=packageBean.getWeight();
						BigDecimal weightB=new BigDecimal(weight);
						BigDecimal parcelTotalWeight=new BigDecimal(parcel.getTotalWeight());
						
						float totalPrice=packageBean.getTotalPrice();
						BigDecimal totalPriceB=new BigDecimal(totalPrice);
						BigDecimal parcelTotalPrice=new BigDecimal(parcel.getTotalPrice());
						
						String orderCode=packageBean.getOrderCode();
						voOrder order=wareService.getOrder("code='"+orderCode+"'");
						if(order!=null){
							packageBean.setOrderId(order.getId());
							packageBean.setBuyMode(order.getBuyMode());
						}
						parcel.setPackageCount(parcel.getPackageCount()+1);
						parcel.setTotalWeight(parcelTotalWeight.add(weightB).floatValue());
						parcel.setTotalPrice(parcelTotalPrice.add(totalPriceB).floatValue());
					}
				}
			}
			datagridJson.setRows(parcelList);
			datagridJson.setFooter(null);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return datagridJson;
	}
	/**
	 *说明：加载MailingBatchBean
	 *@author 石远飞
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/loadMailingBatchInfo")
	@ResponseBody
	public MailingBatchBean loadMailingBatchInfo(HttpServletRequest request,HttpServletResponse response,
			int mailingBatchId) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(430)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		dbOp_slave.init("adult_slave");
		WareService wareService = new WareService(dbOp_slave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		MailingBatchBean batch = null;
		try{
			batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				return null;
			}
			batch.setDeliverName((String) voOrder.deliverMapAll.get(batch.getDeliver()+""));
			batch.setStatusName(MailingBatchBean.statusNameMap.get(batch.getStatus()));
			List parcelListAll=stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), -1, -1, "id desc");
			for(int i=0;i<parcelListAll.size();i++){
				MailingBatchParcelBean parcel=(MailingBatchParcelBean)parcelListAll.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcel.getId()+" and balance_status<>3", -1, -1, null);//包裹列表
				parcel.setMailingBatchPackageList(packageList);
				for(int j=0;j<packageList.size();j++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j);
					float weight=packageBean.getWeight();
					BigDecimal weightB=new BigDecimal(weight);
					BigDecimal parcelTotalWeight=new BigDecimal(parcel.getTotalWeight());
					
					float totalPrice=packageBean.getTotalPrice();
					BigDecimal totalPriceB=new BigDecimal(totalPrice);
					BigDecimal parcelTotalPrice=new BigDecimal(parcel.getTotalPrice());
					
					String orderCode=packageBean.getOrderCode();
					voOrder order=wareService.getOrder("code='"+orderCode+"'");
					if(order!=null){
						packageBean.setOrderId(order.getId());
					}
					parcel.setPackageCount(parcel.getPackageCount()+1);
					parcel.setTotalWeight(parcelTotalWeight.add(weightB).floatValue());
					parcel.setTotalPrice(parcelTotalPrice.add(totalPriceB).floatValue());
					batch.setOrderCount(batch.getOrderCount()+1);
					batch.setTotalWeight(new BigDecimal(batch.getTotalWeight()).add(weightB).floatValue());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return batch;
	}
	/**
	 *说明：获取发货波次列表
	 *@author 石远飞
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getMailingBatchDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getMailingBatchDatagrid(HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,MailingBatchBean mailingBatch) throws ServletException, IOException {
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(429)){
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			StringBuilder condition = new StringBuilder();
			condition.append("id>0");
			//mailingBatch不为空 说明有查询条件
			if(mailingBatch != null){
				if(mailingBatch.getCode() != null && !"".equals(mailingBatch.getCode())){
					condition.append(" and code='" + mailingBatch.getCode() + "'");
				}
				if(mailingBatch.getOrderCode() != null && !"".equals(mailingBatch.getOrderCode())){
					String query = "select a.code from mailing_batch a left join mailing_batch_parcel b on a.code=b.mailing_batch_code " +
						       "left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where c.order_code='" + mailingBatch.getOrderCode() + "';";
					ResultSet rs = service.getDbOp().executeQuery(query);	
					String flag ="false";
					while (rs.next()) {
						condition.append(" and code='" + rs.getString("a.code") + "'");
						flag="true";
					}
					if(flag.equals("false")){
						condition.append(" and id=0");
					}
				}
				if(mailingBatch.getDeliver()!= 0){
					condition.append(" and deliver=" + mailingBatch.getDeliver());
				}
				if(mailingBatch.getStore() != null && !"".equals(mailingBatch.getStore())){
					condition.append(" and store='" + mailingBatch.getStore() + "'");
				}
				if(mailingBatch.getCreateDatetime() != null && !"".equals(mailingBatch.getCreateDatetime())){
					condition.append(" and left(create_datetime,10)='" + mailingBatch.getCreateDatetime() + "'");
				}
			}
			List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request); 
			StringBuffer buffer = new StringBuffer();
			if(areaList!=null && areaList.size()>0){
				buffer.append(" and (");
				for(int i=0;i<areaList.size();i++){
					buffer.append( " area=" + areaList.get(i) + " or ");
				}
				buffer.append(" 1=2)");
			}else{
				buffer.append(" and 1=2");
			}
			condition.append(buffer);
			// 分页
			String totalCount = service.getMailingBatchCount(condition.toString())+"";
			//将总页数放入json
			json.setTotal(Long.parseLong(totalCount));
			String order = null;
			if (easyuiPage.getSort() != null && !"".equals(easyuiPage.getSort().trim())) {// 设置排序
				order = "";
				order+=BeanColumnToTableColumn.Transform(easyuiPage.getSort()) + " " + easyuiPage.getOrder();
			}
			if(order == null || "".equals(order)){
				order = "id desc";
			}
			List<MailingBatchBean> mbList = service.getMailingBatchList(condition.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), order);
			if(mbList != null && mbList.size() > 0){
				for(MailingBatchBean batch : mbList){
					batch.setDeliverName((String) voOrder.deliverMapAll.get(batch.getDeliver()+""));
					batch.setStatusName(MailingBatchBean.statusNameMap.get(batch.getStatus()));
				}
			}
			json.setRows(mbList);
			json.setFooter(null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return json;
	}
	/**
	 *说明：添加发货波次
	 *@author 石远飞
	 */
	@RequestMapping("/addMailingBatch")
	@ResponseBody
	public Json addMailingBatch(HttpServletRequest request,HttpServletResponse response,String area,String deliver,String carrier){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		CargoDeptBean cdBean = null;
		area = StringUtil.checkNull(area);
		deliver = StringUtil.checkNull(deliver);
		carrier = StringUtil.checkNull(carrier);
		try {
			// 仓库代码
			CargoStaffBean csBean = cargoService.getCargoStaff("status=0 and user_id=" + user.getId());
			if (csBean != null) {
				cdBean = cargoService.getCargoDept("id=" + csBean.getDeptId());
			}
			if (cdBean != null) {
				CargoDeptBean deptBean = cargoService.getCargoDept("id="+cdBean.getParentId0());
				if(deptBean == null ){
					j.setMsg("该账号不能添加!");
					return j;
				}
				String storageCode = CargoDeptBean.storeMap.get(deptBean.getCode()).toString();
				// 发货波次号
				String code = "SW" + DateUtil.getNow().substring(2, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10)+storageCode;
				// 计算位序号
				int maxid = service.getNumber("id", "mailing_batch", "max", "id > 0 and code like '" + code +"%'");
				MailingBatchBean lastMailingBatch = service.getMailingBatch("code like '" + code + "%'");
				if(lastMailingBatch == null){
					//当日第一份单据，编号最后三位 001
					code += "001";
				}else {
					//获取当日计划编号最大值
					lastMailingBatch = service.getMailingBatch("id =" + maxid); 
					String _code = lastMailingBatch.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-3));
						if(number==999){
							j.setMsg("当天波次最大数不能超过999！");
							return j;
						}
					number++;
					code+= String.format("%03d",new Object[]{new Integer(number)});
				}
				MailingBatchBean bean = new MailingBatchBean();
				bean.setCode(code);
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setDeliver(StringUtil.toInt(deliver));
				bean.setCreateAdminId(user.getId());
				bean.setCreateAdminName(user.getUsername());
				if(carrier!=null && !carrier.equals("选填")){
					bean.setCarrier(carrier);
				}
				bean.setStatus(0);
				bean.setStore(cdBean.getName());
				bean.setArea(StringUtil.toInt(area));
				if(!service.addMailingBatch(bean)){
					j.setMsg("创建波次失败!");
					return j;
				}
			} else {
				j.setMsg("您不能进行添加操作!");
				return j;
			}
			j.setSuccess(true);
			j.setMsg("添加成功!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return j;
	}
	/**
	 *说明：删除发货波次
	 *@author 石远飞
	 */
	@RequestMapping("/deleteMailBatch")
	@ResponseBody
	public Json deleteMailBatch(HttpServletRequest request,HttpServletResponse response,String ids){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(432)){
			j.setMsg("您没有权限进行此操作!");
			return j;
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			service.getDbOp().startTransaction();
			if(ids == null || "".equals(ids)){
				j.setMsg("没有收到要删除的ID!");
				return j;
			}
			for(String id : ids.split(",")){
				//删除表 发货波次：mailing_batch数据
				if(!service.deleteMailingBatch("id="+id)){
					service.getDbOp().rollbackTransaction();
					j.setMsg("删除mailing_batch数据失败!");
					return j;
				}
				//删除表 发货邮包：mailing_batch_parcel数据
				MailingBatchParcelBean parcel = service.getMailingBatchParcel("mailing_batch_id="+id);
				if(parcel != null){
					if(!service.deleteMailingBatchParcel("mailing_batch_id="+id)){
						service.getDbOp().rollbackTransaction();
						j.setMsg("删除mailing_batch_parcel数据失败!");
						return j;
					}
				}
				//删除表 邮包中的包裹：mailing_batch_package 的数据
				MailingBatchPackageBean packag = service.getMailingBatchPackage("mailing_batch_id="+id);
				if(packag != null){
					if(!service.deleteMailingBatchPackage("mailing_batch_id="+id)){
						service.getDbOp().rollbackTransaction();
						j.setMsg("删除mailing_batch_package数据失败!");
						return j;
					}
				}
			}
			service.getDbOp().commitTransaction();
			j.setSuccess(true);
			j.setMsg("删除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}
		return j;
	}
	/**
	 *说明：获取快递公司列表
	 *@author 石远飞
	 */
	@RequestMapping("/getDeliverComboBox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeliverComboBox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			@SuppressWarnings("unchecked")
			Map<String,String> deliverMapAll = voOrder.deliverMapAll;
			if(deliverMapAll.containsKey("-1")){
				deliverMapAll.remove("-1");
			}
			for(Map.Entry<String, String> entry : deliverMapAll.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey());
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 *说明：获取权限遮罩地区列表
	 *@author 石远飞
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/getCargoDeptAreaComboBox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getCargoDeptAreaComboBox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			if(areaList != null && areaList.size() > 0){
				ProductStockBean psBean = new ProductStockBean();
				comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
				for(String s : areaList){
					EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
					bean.setId(s);
					bean.setText(StringUtil.convertNull(psBean.getAreaName(StringUtil.toInt(s))));
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
}
