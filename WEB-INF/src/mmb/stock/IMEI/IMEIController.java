package mmb.stock.IMEI;

import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.stock.IMEI.model.ImeiProductLog;
import mmb.stock.IMEI.service.IMEIProductService;
import mmb.util.ExcelUtil;
import mmb.ware.WareService;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@RequestMapping("admin/IMEI")
@Controller
public class IMEIController {
	private static byte[] lock = new byte[0];
	private static Logger log = Logger.getLogger(IMEIController.class);
	@Resource
	private IMEIProductService iMEIProductService;
	/**
	 * @return 替换订单imei码
	 * @author syuf
	 */
	@RequestMapping("/replaceOrderImei")
	@ResponseBody
	public Json replaceOrderImei(HttpServletRequest request,String orderCode,String oldImeiCode,String newImeiCode){
		Json j = new Json();
		System.out.print(request.getHeaderNames());
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			IMEIBean imei = imeiService.getIMEI("code='" + StringUtil.toSql(StringUtil.checkNull(newImeiCode)) + "'");
			if(imei == null){
				j.setMsg("新的imei码不存在!");
				return j;
			}
			if(imei.getStatus() != IMEIBean.IMEISTATUS2){
				j.setMsg("新的imei码不可出库!");
				return j;
			}
			Map<String,String> map = imeiService.getIMEIAndOrder("vo.code='" + StringUtil.toSql(StringUtil.checkNull(orderCode)) 
					+ "' and iuo.imei_code='" + StringUtil.toSql(StringUtil.checkNull(oldImeiCode)) + "'");
			if(map == null || map.size() == 0){
				j.setMsg("旧IMEI码与订单不匹配!");
				return j;
			}
			dbOp.startTransaction();
			StringBuffer set = new StringBuffer();
			set.append("imei_code='" + newImeiCode + "'");
			if(!imeiService.updateIMEIUserOrder(set.toString(), "id=" + map.get("imeiUserOrderId"))){
				dbOp.rollbackTransaction();
				j.setMsg("订单IMEI码更换失败!");
				return j;
			}
			StringBuffer buff = new StringBuffer();
			buff.append("status=" + IMEIBean.IMEISTATUS2);
			if(!imeiService.updateIMEI(buff.toString(), "code='" + map.get("oldImeiCode") + "'")){
				dbOp.rollbackTransaction();
				j.setMsg("修改旧的imei码状态失败!");
				return j;
			}
			StringBuffer sb = new StringBuffer();
			sb.append("status=" + IMEIBean.IMEISTATUS3);
			if(!imeiService.updateIMEI(sb.toString(), "code='" + newImeiCode + "'")){
				dbOp.rollbackTransaction();
				j.setMsg("修改新的imei码状态失败!");
				return j;
			}
			IMEILogBean log = new IMEILogBean();
			log.setCreateDatetime(DateUtil.getNow());
			log.setContent("订单[" + orderCode + "]旧的imei码[" + oldImeiCode + "](状态变为可出库)更换为新的imei码[" + newImeiCode + "](状态变为已出库)");
			log.setOperType(IMEILogBean.OPERTYPE7);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			log.setIMEI(oldImeiCode);
			if(!imeiService.addIMEILog(log)){
				dbOp.rollbackTransaction();
				j.setMsg("imei日志添加失败!");
				return j;
			}
			log = new IMEILogBean();
			log.setCreateDatetime(DateUtil.getNow());
			log.setContent("订单[" + orderCode + "]旧的imei码[" + oldImeiCode + "](状态变为可出库)更换为新的imei码[" + newImeiCode + "](状态变为已出库)");
			log.setOperType(IMEILogBean.OPERTYPE7);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			log.setIMEI(newImeiCode);
			if(!imeiService.addIMEILog(log)){
				dbOp.rollbackTransaction();
				j.setMsg("imei日志添加失败!");
				return j;
			}
			dbOp.commitTransaction();
			j.setMsg("imei码更换成功!");
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	@RequestMapping("addIMEI")
	@ResponseBody()
	public Map<String, String> addIMEI(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		synchronized (lock) {
			String IMEI = StringUtil.convertNull(request.getParameter("IMEICode"));
			int productId = StringUtil.toInt(request.getParameter("productId"));
			int buyStockinId = StringUtil.toInt(request
					.getParameter("buyStockinId"));
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				map.put("status", "fail");
				map.put("tip", "当前没有登录，操作失败！");
				return map;
			}
			if (IMEI.equals("")) {
				map.put("status", "fail");
				map.put("tip", "IMEI码不可以为空！");
				return map;
			}
			if (productId == -1) {
				map.put("status", "fail");
				map.put("tip", "商品信息缺失！");
				return map;
			}
			if (buyStockinId == -1) {
				map.put("status", "fail");
				map.put("tip", "采购入库单信息缺失！");
				return map;
			}
			DbOperation dbop = new DbOperation();
			dbop.init(DbOperation.DB_WARE);
			IMEIService iMEIService = new IMEIService(
					IBaseService.CONN_IN_SERVICE, dbop);
			try {
				iMEIService.getDbOp().startTransaction();
				String result = iMEIService.addBuyStockinIMEIInfo(IMEI,
						productId, buyStockinId, user);
				if( result.equals("SUCCESS") ) {
					map.put("status", "success");
					map.put("tip", "操作成功！");
					iMEIService.getDbOp().commitTransaction();
					return map;
				} else {
					map.put("status", "fail");
					map.put("tip", result);
					iMEIService.getDbOp().rollbackTransaction();
					return map;
				}

			} catch (Exception e) {
				boolean auto = true;
				try {
					auto = iMEIService.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {
				}
				if (!auto) {
					iMEIService.getDbOp().rollbackTransaction();
				}
				map.put("status", "fail");
				map.put("tip", "系统异常！");
				e.printStackTrace();
			} finally {
				iMEIService.releaseAll();
			}
			return map;
		}
	}
	
	/**
	 * 依次添加多个imei码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("addMultiIMEI")
	@ResponseBody()
	public Map<String, String> addMultiIMEI(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		synchronized (lock) {
			String IMEI = StringUtil.convertNull(request.getParameter("multiIMEICode"));
			int productId = StringUtil.toInt(request.getParameter("productId"));
			int buyStockinId = StringUtil.toInt(request
					.getParameter("buyStockinId"));
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				map.put("status", "fail");
				map.put("tip", "当前没有登录，操作失败！");
				return map;
			}
			
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(949)) {
				map.put("status", "fail");
				map.put("tip", "您没有批量添加IMEI码的权限！");
				return map;
			}
			if (IMEI.equals("")) {
				map.put("status", "fail");
				map.put("tip", "IMEI码不可以为空！");
				return map;
			}
			if (productId == -1) {
				map.put("status", "fail");
				map.put("tip", "商品信息缺失！");
				return map;
			}
			if (buyStockinId == -1) {
				map.put("status", "fail");
				map.put("tip", "采购入库单信息缺失！");
				return map;
			}
			
			String[] imeis = IMEI.split("\n");
			
			List<String> imeisList = removeDumplicateString(imeis);
			
			DbOperation dbop = new DbOperation();
			dbop.init(DbOperation.DB_WARE);
			IMEIService iMEIService = new IMEIService(
					IBaseService.CONN_IN_SERVICE, dbop);
			try {
				iMEIService.getDbOp().startTransaction();
				String result = "SUCCESS";
				for (String s : imeisList) {
					result = iMEIService.addBuyStockinIMEIInfo(s,
							productId, buyStockinId, user);
					if( !result.equals("SUCCESS") ) {
						map.put("status", "fail");
						map.put("tip", result + "IMEI码：" + s);
						iMEIService.getDbOp().rollbackTransaction();
						return map;
					}
				}

				map.put("status", "success");
				map.put("tip", "操作成功！");
				map.put("imeis", list2String(imeisList, ","));
				iMEIService.getDbOp().commitTransaction();
				return map;
			} catch (Exception e) {
				boolean auto = true;
				try {
					auto = iMEIService.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {
				}
				if (!auto) {
					iMEIService.getDbOp().rollbackTransaction();
				}
				map.put("status", "fail");
				map.put("tip", "系统异常！");
				e.printStackTrace();
			} finally {
				iMEIService.releaseAll();
			}
			return map;
		}
	}
	
	@RequestMapping("deleteIMEIBuyStockin")
	@ResponseBody()
	public Map<String, String> deleteIMEIBuyStockin(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		synchronized (lock) {
			String[] codes = request.getParameterValues("codes");
			int productId = StringUtil.toInt(request.getParameter("productId"));
			int buyStockinId = StringUtil.toInt(request
					.getParameter("buyStockinId"));
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				map.put("status", "fail");
				map.put("tip", "当前没有登录，操作失败！");
				return map;
			}
			if (productId == -1) {
				map.put("status", "fail");
				map.put("tip", "商品信息缺失！");
				return map;
			}
			if (buyStockinId == -1) {
				map.put("status", "fail");
				map.put("tip", "采购入库单信息缺失！");
				return map;
			}
			DbOperation dbop = new DbOperation();
			dbop.init(DbOperation.DB_WARE);
			IMEIService iMEIService = new IMEIService(
					IBaseService.CONN_IN_SERVICE, dbop);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop);
			try {
				iMEIService.getDbOp().startTransaction();
				String result = iMEIService.deleteCodes(codes,productId, buyStockinId, user);
				if( result.equals("SUCCESS") ) {
					map.put("status", "success");
					map.put("tip", "操作成功！");
					map.put("count", new Integer(codes.length).toString());
					iMEIService.getDbOp().commitTransaction();
					return map;
				} else {
					map.put("status", "fail");
					map.put("tip", result);
					iMEIService.getDbOp().rollbackTransaction();
					return map;
				}

			} catch (Exception e) {
				boolean auto = true;
				try {
					auto = iMEIService.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {
				}
				if (!auto) {
					iMEIService.getDbOp().rollbackTransaction();
				}
				map.put("status", "fail");
				map.put("tip", "系统异常！");
				e.printStackTrace();
			} finally {
				iMEIService.releaseAll();
			}
			return map;
		}
	}
	
	public List<String> removeDumplicateString(String[] str) {
		List<String> list = new ArrayList<String>(); 
		for (String s : str) {
			if (!list.contains(s.trim())) {
				list.add(s.trim());
			}
		}
		return list;
	}
	
	public String list2String(List<String> list, String separator) {
		return org.apache.commons.lang.StringUtils.join(list.toArray(),separator);
	}
	
	
	
	
	/**
	 * hepeng
	 * 根据订单查询imei码
	 * 或者根据IMEI码查询订单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("queryCodeOrIMEI")
	@ResponseBody()
	public EasyuiDataGridJson queryCodeOrIMEI(HttpServletRequest request,
			HttpServletResponse response,EasyuiDataGrid easyuiPage) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();     
		Json j = new Json();
		       
			String radio_type = request.getParameter("radio_type");
			String IMEI = StringUtil.convertNull(request.getParameter("multiIMEICode"));
			
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				j.setMsg("当前没有登录，操作失败！");
				  if(datagrid.getTotal() ==null || datagrid.getRows()==null){
			        	datagrid.setTotal((long)0);
			        	List<Object> list=new ArrayList<Object>();
			        	datagrid.setRows(list);
			        }
				return datagrid;
			}
			String[] imeisOrcodes = IMEI.split("\n");
			
			if (imeisOrcodes.length >200) {
				j.setMsg("填写数量超过200条！");
				  if(datagrid.getTotal() ==null || datagrid.getRows()==null){
			        	datagrid.setTotal((long)0);
			        	List<Object> list=new ArrayList<Object>();
			        	datagrid.setRows(list);
			        }
				return datagrid;
				
			}
			StringBuffer buff = new StringBuffer();
			boolean isresu=false;
			 DbOperation dbop = new DbOperation();
			 List<OrderStockBean> templist=new ArrayList<OrderStockBean>();
			 List<OrderStockBean> tempAlllist=new ArrayList<OrderStockBean>();//保存所有的集合
			  try {
			    dbop.init(DbOperation.DB);
				IMEIService iMEIService = new IMEIService(
				IBaseService.CONN_IN_SERVICE, dbop);
				 IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop);
				 String joinType="left";//表连接类型
				 if("imei".equals(radio_type)){
					 joinType="left";//表连接类型
				buff.append(" and iu.imei_code in (");
			List<String> imeisList = removeDumplicateString(imeisOrcodes);
			if (imeisOrcodes.length != imeisList.size()) {
				j.setMsg("IMEI码存在重复！");
				//return datagrid;
			}
			
			 boolean isde=false;
               for (String s : imeisList) {
					if (s.trim().equals("")) {
						
						j.setMsg("存在空的IMEI码！");
						//return datagrid;
					}
					//判断imei码是否已存在
					IMEIBean iMEIBean = iMEIService.getIMEI("code='" + StringUtil.toSql(s.trim()) +"'");
					OrderStockBean orderStockBean=new OrderStockBean();
					orderStockBean.setCode(StringUtil.toSql(s.trim()));
					if (iMEIBean == null) {
						templist.add(orderStockBean);
						j.setMsg("IMEI码：" + s + "不存在！");
						//return datagrid;
					}else{
						isresu=true;
						isde=true;
						buff.append("'"+StringUtil.toSql(s.trim())+"',");	
					}
					tempAlllist.add(orderStockBean);
				
				
				}
               if(isde){
				   buff.delete(buff.length()-1, buff.length());
	               }
               buff.append(") ");
			}else if("code".equals(radio_type)){
				joinType="right";//表连接类型
				buff.append(" and os.code in (");
				List<String> imeisList = removeDumplicateString(imeisOrcodes);
				if (imeisOrcodes.length != imeisList.size()) {
					j.setMsg("订单存在重复！");
					//return datagrid;
				}
				   boolean isde=false;
	               for (String s : imeisList) {
						if (s.trim().equals("")) {
							
							j.setMsg("存在空的订单！");
							//return datagrid;
						}
						//判断订单号是否已存在
						OrderStockBean orderStockBean=stockService.getOrderStock(" order_code='" + StringUtil.toSql(s.trim()) + "'");
						OrderStockBean orderIMEIStockBean=new OrderStockBean();
						orderIMEIStockBean.setOrderCode(StringUtil.toSql(s.trim()));
						if (orderStockBean == null) {
						  templist.add(orderIMEIStockBean);
							j.setMsg("订单：" + s + "不存在！");
							//return datagrid;
						}else{
							isresu=true;
							isde=true;
							buff.append("'"+StringUtil.toSql(s.trim())+"',");	
						}
						 tempAlllist.add(orderIMEIStockBean);
					
					}
	               if(isde){
				   buff.delete(buff.length()-1, buff.length());
	               }
	               buff.append(") ");
			}
			
				List<OrderStockBean> maps=new ArrayList<OrderStockBean>();
	        	if(isresu){
				int totalCount = iMEIService.getImeiOrCodeCount(joinType,buff.toString());
				datagrid.setTotal((long)totalCount);
				maps = iMEIService.getImeiOrCodeList(joinType,buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), null);
	        	}
				if(templist.size()>0){
					if((datagrid.getTotal()==null)){
						datagrid.setTotal((long)templist.size());
						for (OrderStockBean orderStockBean : templist) {
							maps.add(orderStockBean);
						}
					}else if(datagrid.getTotal() > 0){
						datagrid.setTotal(datagrid.getTotal()+(long)templist.size());
						for (OrderStockBean orderStockBean : templist) {
							maps.add(orderStockBean);
						}
					}else if(datagrid.getTotal() == 0){
						datagrid.setTotal((long)tempAlllist.size());
						for (OrderStockBean orderStockBean : tempAlllist) {
							maps.add(orderStockBean);
						}
					}
					
				}
				datagrid.setRows(maps);
	        	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				dbop.release();//释放数据连接
			}
	        
	        if(datagrid.getTotal() ==null || datagrid.getRows()==null){
	        	datagrid.setTotal((long)0);
	        	List<Object> list=new ArrayList<Object>();
	        	datagrid.setRows(list);
	        }
		    //	j.setSuccess(true);
		    return datagrid;
		
	}
	
	/**
	 * 直接添加多个imei码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("saveMultiIMEI")
	@ResponseBody()
	public Json saveMultiIMEI(HttpServletRequest request,
			HttpServletResponse response) {
		Json j = new Json();
		synchronized (lock) {
			String IMEI = StringUtil.convertNull(request.getParameter("multiIMEICode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));
			int imeiCount = StringUtil.toInt(request.getParameter("imeiCount"));
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				j.setMsg("当前没有登录，操作失败！");
				return j;
			}
			
			if (productCode.equals("")) {
				j.setMsg("商品编号不能为空！");
				return j;
			}
			if (IMEI.equals("")) {
				j.setMsg("IMEI码不可以为空！");
				return j;
			}
			if (imeiCount == -1) {
				j.setMsg("数量不能为空！");
				return j;
			}
			
			String[] imeis = IMEI.split("\n");
			
			if (imeis.length != imeiCount) {
				if ("".equals(imeis[imeis.length-1])) {
					if (imeis.length - 1 != imeiCount) {
						j.setMsg("IMEI码填写数量和所填数量不等！");
						return j;
					}
				} else {
					j.setMsg("IMEI码填写数量和所填数量不等！");
					return j;
				}
			}
			
			List<String> imeisList = removeDumplicateString(imeis);
			if (imeis.length != imeisList.size()) {
				j.setMsg("IMEI码存在重复！");
				return j;
			}
			
			DbOperation dbop = new DbOperation();
			dbop.init(DbOperation.DB);
			IMEIService iMEIService = new IMEIService(
					IBaseService.CONN_IN_SERVICE, dbop);
			WareService wareService = new WareService(dbop);
			try {
				voProduct product = wareService.getProduct(StringUtil.toSql(productCode));
				if (product == null) {
					j.setMsg("产品不存在！");
					return j;
				}
				if (!iMEIService.isProductMMBMobile(product.getId())) {
					j.setMsg("该产品不是IMEI码商品！");
					return j;
				}
				iMEIService.getDbOp().startTransaction();
				for (String s : imeisList) {
					if (s.trim().equals("")) {
						iMEIService.getDbOp().rollbackTransaction();
						j.setMsg("存在空的IMEI码！");
						return j;
					}
					//判断imei码是否已存在
					IMEIBean iMEIBean = iMEIService.getIMEI("code='" + StringUtil.toSql(s.trim()) +"'");
					if (iMEIBean != null) {
						iMEIService.getDbOp().rollbackTransaction();
						j.setMsg("IMEI码：" + s + "已存在！");
						return j;
					}
					iMEIBean = new IMEIBean();
					iMEIBean.setCode(s.trim());
					iMEIBean.setCreateDatetime(DateUtil.getNow());
					iMEIBean.setProductId(product.getId());
					iMEIBean.setStatus(IMEIBean.IMEISTATUS2);
					if(!iMEIService.addIMEI(iMEIBean) ) {
						iMEIService.getDbOp().rollbackTransaction();
						j.setMsg("添加IMEI码失败！");
						return j;
					}
					IMEILogBean ilBean = new IMEILogBean();
					ilBean.setOperCode("");
					ilBean.setCreateDatetime(DateUtil.getNow());
					ilBean.setIMEI(iMEIBean.getCode());
					ilBean.setOperType(IMEILogBean.OPERTYPE6);
					ilBean.setUserId(user.getId());
					ilBean.setUserName(user.getUsername());
					ilBean.setContent("备用机添加,添加IMEI码：" + iMEIBean.getCode());
					if( !iMEIService.addIMEILog(ilBean) ) {
						iMEIService.getDbOp().rollbackTransaction();
						j.setMsg("添加IMEI码日志失败！");
						return j;
					}
				}

				iMEIService.getDbOp().commitTransaction();
				j.setMsg("批量添加IMEI码成功！");
				j.setSuccess(true);
				return j;
			} catch (Exception e) {
				boolean auto = true;
				try {
					auto = iMEIService.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {
				}
				if (!auto) {
					iMEIService.getDbOp().rollbackTransaction();
				}
				j.setMsg("系统异常！");
				e.printStackTrace();
			} finally {
				iMEIService.releaseAll();
			}
			return j;
		}
	}
	
	/**
	 * 订单添加imei码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("userOrderAddImei")
	@ResponseBody()
	public Json userOrderAddImei(HttpServletRequest request,
			HttpServletResponse response) {
		Json j = new Json();
		synchronized (lock) {
			String IMEI = StringUtil.convertNull(request.getParameter("imeiCodes"));
			String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				j.setMsg("当前没有登录，操作失败！");
				return j;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(2203)) {
				j.setMsg("你没有添加订单IMEI码权限！");
				return j;
			}
			if (orderCode.equals("")) {
				j.setMsg("订单编号不能为空！");
				return j;
			}
			if (IMEI.equals("")) {
				j.setMsg("IMEI码不可以为空！");
				return j;
			}
			
			String[] imeis = IMEI.split(",");
			
			List<String> imeisList = removeDumplicateString(imeis);
			if (imeis.length != imeisList.size()) {
				j.setMsg("IMEI码存在重复！");
				return j;
			}
			
			DbOperation dbop = new DbOperation();
			dbop.init(DbOperation.DB);
			IMEIService imeiService = new IMEIService(
					IBaseService.CONN_IN_SERVICE, dbop);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop);
			try {
				OrderStockBean bean = service.getOrderStock("order_code='" + orderCode +"' and status <>" + OrderStockBean.STATUS4);
				if (bean == null) {
					j.setMsg("订单不存在!");
					return j;
				}
				List<OrderStockProductBean> list = service.getOrderStockProductList("order_stock_id="+bean.getId(), -1, -1, null);
				if (list == null || list.size() <= 0) {
					j.setMsg("订单中没有商品!");
					return j;
				}
				imeiService.getDbOp().startTransaction();
				for(String imeiCode : imeis){
					IMEIBean imei = imeiService.getIMEI("code='" + imeiCode + "'");
					if (imei == null) {
						dbop.rollbackTransaction();
						j.setMsg("IMEI码不存在!");
						return j;
					}
					//判断状态是否为可出库
					if (imei.getStatus() != IMEIBean.IMEISTATUS2) {
						dbop.rollbackTransaction();
						j.setMsg("[" + imeiCode+ "]不是可出库状态!");
						return j;
					}
					boolean flag = false;
					//判断是否为order_stock_product中商品
					for (OrderStockProductBean ospBean : list) {
						if (imei.getProductId() == ospBean.getProductId()) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						dbop.rollbackTransaction();
						j.setMsg("订单中没有[" + imeiCode+ "]对应的商品!");
						return j;
					}
					
					//判断该订单输入imei码是否已存在
					IMEIUserOrderBean iuoBean = imeiService.getIMEIUserOrder(" order_id= " + bean.getOrderId() + " and imei_code='" + imeiCode + "' ");
					if (iuoBean != null) {
						dbop.rollbackTransaction();
						j.setMsg("订单中已有[" + imeiCode+ "]!");
						return j;
					}
					flag = false;
					
					//判断订单商品对应imei码是否已足够
					List<IMEIUserOrderBean> iuoList = imeiService.getIMEIUserOrderList(" order_id= " + bean.getOrderId() + " and product_id=" + imei.getProductId() + " ", -1, -1, null);
					if (iuoList != null && iuoList.size() > 0) {
						for (OrderStockProductBean ospBean : list) {
							if (imei.getProductId() == ospBean.getProductId()) {
								if (ospBean.getStockoutCount() <= iuoList.size()) {
									dbop.rollbackTransaction();
									j.setMsg("订单中已有足够[" + imeiCode+ "]对应的商品IMEI码!");
									return j;
								}
							}
						}
					}
					
					IMEIUserOrderBean imeiBean = new IMEIUserOrderBean();
					imeiBean.setImeiCode(imei.getCode());
					imeiBean.setOrderId(bean.getOrderId());
					imeiBean.setProductId(imei.getProductId());
					if(!imeiService.addIMEIUserOrder(imeiBean)){
						service.getDbOp().rollbackTransaction();
						j.setMsg("添加IMEI码user_order记录失败!");
						return j;
					}
					IMEILogBean imeiLog = new IMEILogBean();
					imeiLog.setContent("订单,订单补充添加IMEI码,imei码[" + imei.getCode() +"]由[可出库]变成[已出库]"+",地区：["+ProductStockBean.areaMap.get(bean.getStockArea())+"]");
		
					imeiLog.setCreateDatetime(DateUtil.getNow());
					imeiLog.setIMEI(imei.getCode());
					imeiLog.setUserId(user.getId());
					imeiLog.setUserName(user.getUsername());
					imeiLog.setOperType(IMEILogBean.OPERTYPE2);
					imeiLog.setOperCode(bean.getOrderCode());
					if(!imeiService.addIMEILog(imeiLog)){
						service.getDbOp().rollbackTransaction();
						j.setMsg("添加IMEI码日志失败!");
						return j;
					}
					if(!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS3, "id=" + imei.getId())){
						service.getDbOp().rollbackTransaction();
						j.setMsg("更新IMEI码状态失败!");
						return j;
					}
				}
				dbop.commitTransaction();
				j.setSuccess(true);
				j.setMsg("添加成功！");
				return j;
			} catch (Exception e) {
				boolean auto = true;
				try {
					auto = imeiService.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {
				}
				if (!auto) {
					imeiService.getDbOp().rollbackTransaction();
				}
				j.setMsg("系统异常！");
				e.printStackTrace();
				return j;
			} finally {
				imeiService.releaseAll();
			}
		}
	}
	
	@RequestMapping("/toIMEIProductLog")
	public ModelAndView toIMEIPropertyLog(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("admin/imei/iMEIProductLog");
	}
	
	/**
	 * 保存数据到数据库
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	@RequestMapping("/upLoadData")
	public ModelAndView upLoadData(HttpServletRequest request,HttpServletResponse response){
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Map<String,Object> model = new HashMap<String, Object>();
		 
		try{
			// 转型为MultipartHttpRequest：   
			MultipartHttpServletRequest multipartHttpservletRequest = (MultipartHttpServletRequest) request;
	        // 获得文件：   
	        MultipartFile file = multipartHttpservletRequest.getFile("attendance");
	        // 获得输入流：   
	        InputStream input = file.getInputStream();   
	        // 获得文件名：   
	        String attendanceFileName = file.getOriginalFilename();
	        if(attendanceFileName == null||attendanceFileName.equals("")){
	        	model.put("tip", "请导入文件！");
	        	return new ModelAndView("admin/imei/iMEIProductLog",model);
	        }
			int index = attendanceFileName.lastIndexOf(".");
			String suffix = attendanceFileName.substring(index + 1, attendanceFileName.length());
			String fileName = attendanceFileName.substring(0,index);
			model.put("fileName", fileName);
			
			Workbook xssfWorkbook = null;
			if ("xls".equals(suffix)) {
				xssfWorkbook = new HSSFWorkbook(input);
			} else if ("xlsx".equals(suffix)) {
				xssfWorkbook = new XSSFWorkbook(input);
			}
			Sheet sheet = xssfWorkbook.getSheetAt(0);
			if (sheet == null) {
				model.put("tip", "没有数据!");
				return new ModelAndView("admin/imei/iMEIProductLog",model);
			}
			if(sheet.getLastRowNum()<1){
				model.put("tip", "模板中没有数据：模板数据为空，导入失败！");
				return new ModelAndView("admin/imei/iMEIProductLog",model);
			}
			List<ImeiProductLog> imeiProductSetList = new ArrayList<ImeiProductLog>();
			List<String> productCodeList = new ArrayList<String>();
			boolean flag = false;
			int sumCount = sheet.getLastRowNum();
			
			for (int rowNum = 1; rowNum <= sumCount; rowNum++) {
				if (sheet.getRow(rowNum) != null) {
					Row row = sheet.getRow(rowNum);
					ImeiProductLog info = new ImeiProductLog();
					String productId = ExcelUtil.getValue(row.getCell(0)==null?row.createCell(0):row.getCell(0));
					String productCode = ExcelUtil.getValue(row.getCell(1)==null?row.createCell(1):row.getCell(1));
					String storeName = ExcelUtil.getValue(row.getCell(2)==null?row.createCell(2):row.getCell(2));
					if(productCode.equals("")){
						flag = true;
						continue;
					}
					productCodeList.add(productCode);
					info.setProductId(Integer.valueOf((productId.trim().equals("")?"0":productId)));
					info.setProductCode(productCode);
					info.setStoreName(storeName);
					info.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					info.setOperator(user.getUsername());
					imeiProductSetList.add(info);
				}
			}
			//返回已经存在的productCode
			List<String> returnProductCodeList = iMEIProductService.queryIMEIProductCode(productCodeList);
			
			for (Iterator<ImeiProductLog> iterator = imeiProductSetList.iterator(); iterator.hasNext();) {
				ImeiProductLog ImeiProductLog = iterator.next();
				for (String productCode : returnProductCodeList) {
					if(ImeiProductLog.getProductCode().equals(productCode)){
						iterator.remove();
					}
				}
			}
			//将excel中数据存入imei_product_log表中
			if(!imeiProductSetList.isEmpty()){
				productCodeList.removeAll(returnProductCodeList);
				iMEIProductService.saveImeiProductSet(imeiProductSetList,productCodeList);
			}
			
			if(flag||returnProductCodeList.size()>0){
				model.put("tip","导入失败！");
			}else{
				model.put("tip","导入成功！");
			}
			 
		}catch(Exception e){
			e.printStackTrace();
			log.error("上传数据时出现异常", e);
			model.put("tip","导入失败！");
		}
		return new ModelAndView("admin/imei/iMEIProductLog",model);
	}
	
	/**
	 * 下载excel模版
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@RequestMapping("/downloadTemplate")
	public void downloadTemplate(HttpServletRequest request,HttpServletResponse response){
		Workbook workbook = new SXSSFWorkbook();
		try {
			Sheet sheet = workbook.createSheet();
			for (int colNum=0; colNum<3; colNum++) {
	    		sheet.setColumnWidth(colNum, 15*256);
			}
	    	// 背景色:浅绿色、字体颜色：黑色
	    	CellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setBorderRight(CellStyle.BORDER_THIN);
			style.setBorderTop(CellStyle.BORDER_THIN);
			Font font = workbook.createFont();
			font.setFontName("黑体");
			font.setColor(HSSFColor.BLACK.index);
			style.setFont(font);
			
			// 居中对齐
			CellStyle style4 = workbook.createCellStyle();
			style4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style4.setBorderBottom(CellStyle.BORDER_THIN);
			style4.setBorderLeft(CellStyle.BORDER_THIN);
			style4.setBorderRight(CellStyle.BORDER_THIN);
			style4.setBorderTop(CellStyle.BORDER_THIN);
			
	    	Row row = sheet.createRow(0);
	    	int j = 0;
	    	
	    	row.createCell(j++).setCellValue("产品ID");
			row.createCell(j++).setCellValue("产品编号");
	    	row.createCell(j++).setCellValue("小店名称");
	    	 
	    	// 表头样式设置
			for (int k=0;k<3;k++) {
				Cell c = row.getCell(k);
				c.setCellStyle(style);
			}
	    	
			String fileName = "数据模版";
			String agent = request.getHeader("User-Agent");
			boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);

			if (isMSIE) {
			    fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
			    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xlsx\"");
			response.setContentType("application/msxls");
			workbook.write(response.getOutputStream());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("下载模版时出现异常", e);
		}
	}
	
	/**
	 * 查询IMEI产品属性设置表
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	@RequestMapping("/queryIMEIProductLog")
	public ModelAndView queryIMEIProductLog(HttpServletRequest request,HttpServletResponse response){
		
		Map<String, Object> model = new HashMap<String, Object>();
		List<ImeiProductLog> list = new ArrayList<ImeiProductLog>();
		try {
			// 分页参数
			int pageNo = ServletRequestUtils.getIntParameter(request,"pager.offset", 0);
			int pageSize = ServletRequestUtils.getIntParameter(request,"pager.limit", 20);
			// 列表数据
			list = iMEIProductService.queryIMEIProductLog(pageNo, pageSize);
			int count = iMEIProductService.queryIMEIProductLogCount(null);
			model.put("list", list);
			model.put("totalCount", count);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}
		return new ModelAndView("admin/imei/iMEIProductLog",model);
	}
}