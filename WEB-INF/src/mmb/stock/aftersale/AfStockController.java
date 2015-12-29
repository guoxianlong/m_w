package mmb.stock.aftersale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.easyui.EasyuiComBoBoxBean;
import mmb.easyui.EasyuiDataGridBean;
import mmb.easyui.EasyuiPageBean;
import mmb.easyui.Json;
import mmb.msg.TemplateMarker;
import mmb.rec.oper.bean.ProductSellPropertyBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.IMEI.IMEIStockExchangeBean;
import mmb.stock.IMEI.IMEIUserOrderBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;
import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.bybs.ByBsAction;
import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.afterSales.AfterSaleOrderProduct;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.bybs.BsbyReason;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.service.IAdminService;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.test.IMEIUtil;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;
import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;
@RequestMapping("admin/AfStock")
@Controller
public class AfStockController {
	private static byte[] lock = new byte[0];
	private static Integer importPackageCodeLock=0;
	public Log stockLog = LogFactory.getLog("stock.Log");
	
	/**
	 * 售后调拨货位调整(内部使用)
	 * @author syuf
	 */
	@RequestMapping("/afterSaleTransferWholeAdjust")
	@ResponseBody
	public Json afterSaleTransferWholeAdjust(String detectCodes,String exchangeCodes)throws Exception{
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String failDetectCode = "";
		try {
			if("".equals(StringUtil.checkNull(detectCodes)) || "".equals(StringUtil.checkNull(exchangeCodes))){
				j.setMsg("处理单和调拨单都不能为空!");
				return j;
			}
			List<String> detectCodeList = Arrays.asList(detectCodes.split("\n"));
			List<String> exchangeCodeList = Arrays.asList(exchangeCodes.split("\n"));
			int i = 0;
			for(String detectCode: detectCodeList){
				AfterSaleDetectProductBean detect = afStockService.getAfterSaleDetectProduct("code='" + detectCode + "'");
				if(detect == null){
					failDetectCode += detectCode + ",";
					continue;
				}
				String wholeCode = afStockService.getStockExchangeInWholeCodeList("se.code='" + exchangeCodeList.get(i) + "' and sep.product_id=" + detect.getProductId());
				if(wholeCode == null){
					failDetectCode += detectCode + ",";
					continue;
				}
				if(!afStockService.updateAfterSaleDetectProduct(" cargo_whole_code='" + wholeCode + "'", "id=" + detect.getId())){
					j.setMsg("更新处理单[" + detectCode + "]失败!");
					return j;
				}
				i++;
			}
			if(failDetectCode.length() > 0){
				j.setMsg("调整成功,其中[" + failDetectCode + "]由于数据缺失,未能调整!");
			}else {
				j.setMsg("全部调整成功!");
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 审核返厂维修更换单
	 * @author syuf
	 */
	@RequestMapping("/auditBackSuppilerReplace")
	@ResponseBody
	public Json auditBackSuppilerReplace(HttpServletRequest request,String flag,String ids){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("您当前没有登录！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2184)) {
			j.setMsg("您没有审核权限！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			if(!"".equals(StringUtil.checkNull(ids))){
				dbOp.startTransaction();
				for(String id : ids.split(",")){
					String result = afStockService.auditBackSuppilerReplace(dbOp,user,StringUtil.checkNull(flag),id);
					if(result != null){
						dbOp.rollbackTransaction();
						j.setMsg(result);
						return j;
					}
				}
				dbOp.commitTransaction();
			}
			j.setMsg("审核成功!");
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 获取返厂维修更换单datagrid
	 * @author syuf
	 */
	@RequestMapping("/getBackSuppilerReplace")
	@ResponseBody
	public EasyuiDataGridBean getBackSuppilerReplace(EasyuiPageBean pageBean, String detectCode,
			String status,String auditStatus,String createUserName,String oldImei,String newImei,String oldProductCode,
			String newProductCode,String startTime,String endTime,String auditStartTime,String auditEndTime,String supplierId)throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			String condition = this.getBackSuppilerReplaceCondition(detectCode,
					status, auditStatus, createUserName, oldImei, newImei,
					oldProductCode, newProductCode, startTime, endTime,
					auditStartTime, auditEndTime, supplierId);
			List<Map<String,String>> rows = afStockService.getAftersaleBacksupplierProductReplaceMaps(condition,(pageBean.getPage()-1)*pageBean.getRows(),pageBean.getRows()," asbspr.create_datetime");
			datagrid.setRows(rows);
			int total = afStockService.getAftersaleBacksupplierProductReplaceTotal(condition);
			datagrid.setTotal((long) total);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 导出返厂维修更换单
	 */
	@RequestMapping("/exportBackSuppilerReplace")
	public String exportBackSuppilerReplace(HttpServletRequest request,HttpServletResponse response,String detectCode,
			String status,String auditStatus,String createUserName,String oldImei,String newImei,String oldProductCode,
			String newProductCode,String startTime,String endTime,String auditStartTime,String auditEndTime,String supplierId)throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2184)) {
			request.setAttribute("msg", "您没有相关导出权限！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}	
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			String condition = this.getBackSuppilerReplaceCondition(detectCode,
					status, auditStatus, createUserName, oldImei, newImei,
					oldProductCode, newProductCode, startTime, endTime,
					auditStartTime, auditEndTime, supplierId);
			this.exportBackSuppilerReplaceData(response, 
					afStockService.getAftersaleBacksupplierProductReplaceMaps(condition, 0,-1, " asbspr.create_datetime"));
	} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return null;
	}
	
	/**
	 * 获取返厂维修更换单sql condition
	 */
	private String getBackSuppilerReplaceCondition(String detectCode,
			String status,String auditStatus,String createUserName,String oldImei,String newImei,String oldProductCode,
			String newProductCode,String startTime,String endTime,String auditStartTime,String auditEndTime,String supplierId){
		StringBuffer buff = new StringBuffer();
		buff.append(" 1=1 ");
		if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
			buff.append(" and left(asbspr.create_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
		}
		if(!"".equals(StringUtil.checkNull(auditStartTime)) && !"".equals(StringUtil.checkNull(auditEndTime))){
			buff.append(" and left(asbspr.audit_datetime,10) BETWEEN '" + StringUtil.toSql(auditStartTime) + "' and '" + StringUtil.toSql(auditEndTime) + "'");
		}
		if(!"".equals(StringUtil.checkNull(detectCode))){
			buff.append(" and asdp.code = '");
			buff.append(detectCode + "'");
		}
		if(!"".equals(StringUtil.checkNull(status))){
			buff.append(" and asbsp.status = ");
			buff.append(status);
		}
		if(!"".equals(StringUtil.checkNull(auditStatus))){
			buff.append(" and asbspr.audit_status = ");
			buff.append(auditStatus);
		}
		if(!"".equals(StringUtil.checkNull(supplierId))){
			buff.append(" and asbspr.supplier_id = ");
			buff.append(supplierId);
		}
		if(!"".equals(StringUtil.checkNull(createUserName))){
			buff.append(" and asbspr.create_user_name = '");
			buff.append(createUserName + "'");
		}
		if(!"".equals(StringUtil.checkNull(oldImei))){
			buff.append(" and asbspr.old_imei = '");
			buff.append(oldImei + "'");
		}
		if(!"".equals(StringUtil.checkNull(newImei))){
			buff.append(" and asbspr.new_imei = '");
			buff.append(newImei + "'");
		}
		if(!"".equals(StringUtil.checkNull(newProductCode))){
			buff.append(" and np.code = '");
			buff.append(newProductCode + "'");
		}
		if(!"".equals(StringUtil.checkNull(oldProductCode))){
			buff.append(" and op.code = '");
			buff.append(oldProductCode + "'");
		}
		return buff.toString();
	}
	
	/**
	 * 导出返厂维修更换单数据
	 */
	private void exportBackSuppilerReplaceData(HttpServletResponse response,List<Map<String,String>> list){
		
		ExportExcel excel = new ExportExcel(1);
		//设置表头
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		header.add("序号");
		header.add("处理单号");
		header.add("维修更换单");
		header.add("原IMEI码");
		header.add("原商品编号");
		header.add("新IMEI码");
		header.add("新商品编号");
		header.add("维修厂家");
		header.add("添加日期");
		header.add("添加人");
		header.add("审核日期");
		header.add("审核人");
		header.add("原品入库均价");
		header.add("新品入库均价");
		header.add("新品实时均价");
		header.add("返厂状态");
		header.add("审核状态");
		headers.add(header);
		//设置body
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		if(list!=null&&list.size()>0){
			int count=0;
			for(Map<String,String> map:list){
				//每行数据
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(String.valueOf(++count));
				temp.add(map.get("detectCode"));
				temp.add(map.get("replaceCode"));
				temp.add(map.get("oldImei"));
				temp.add(map.get("oldProductCode"));
				temp.add(map.get("newImei"));
				temp.add(map.get("newProductCode"));
				temp.add(map.get("supplierName"));
				if(map.get("createDatetime")!=null&&!"".equals(map.get("createDatetime"))){
					temp.add(DateUtil.formatTime(DateUtil.parseDate(map.get("createDatetime"), "yyyy-MM-dd HH:mm:ss")));
				}else{
					temp.add("");
				}
				temp.add(map.get("createUserName"));
				if(map.get("auditDatetime")!=null&&!"".equals(map.get("auditDatetime"))){
					temp.add(DateUtil.formatTime(DateUtil.parseDate(map.get("auditDatetime"), "yyyy-MM-dd HH:mm:ss")));
				}else{
					temp.add("");
				}
				temp.add(map.get("auditUserName"));
				temp.add(map.get("oldPrice"));
				temp.add(map.get("newPrice"));
				temp.add(map.get("nowPrice"));
				temp.add(map.get("statusName"));
				temp.add(map.get("auditStatusName"));
				bodies.add(temp);
			}
		}else{
			ArrayList<String> temp = new ArrayList<String>();
			for(int i=0;i<header.size();i++){
				temp.add("没有查询到匹配的厂家维修更换商品列表！");
			}
			bodies.add(temp);
			
			/* 允许合并列,下标从0开始，即0代表第一列 */
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			for(int i=0;i<header.size();i++){
				mayMergeColumn.add(i);
			}
			excel.setMayMergeColumn(mayMergeColumn);

			/* 允许合并行,下标从0开始，即0代表第一行 */
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			mayMergeRow.add(1);
			excel.setMayMergeRow(mayMergeRow);

			/*
			 * 该行为固定写法 （设置该值为导出excel最大列宽 ,下标从1开始）
			 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
			 */
			excel.setColMergeCount(header.size());
			
		}
		// 调用填充表头方法
		excel.buildListHeader(headers);
		// 调用填充数据区方法
		excel.buildListBody(bodies);
		// 文件输出
		try {
			excel.exportToExcel("厂家维修更换商品列表_"+DateUtil.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd-HH-mm-ss"), response, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 添加不合格原因列表
	 * @author syuf
	 */
	@RequestMapping("/addUnqualifiedReason")
	@ResponseBody
	public Json addUnqualifiedReason(AfterSaleUnqualifiedReason reason)throws Exception{
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			if(afStockService.getAfterSaleUnqualifiedReason("name='" + reason.getName().trim() + "'") != null){
				j.setMsg("不合格原因不能重复添加!");
				return j;
			}
			if(!afStockService.addAfterSaleUnqualifiedReason(reason)){
				j.setMsg("添加不合格原因失败!");
				return j;
			}
			j.setMsg("添加成功!");
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 修改不合格原因
	 * @author syuf
	 */
	@RequestMapping("/editUnqualifiedReason")
	@ResponseBody
	public Json editUnqualifiedReason(AfterSaleUnqualifiedReason reason)throws Exception{
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			if(!afStockService.editAfterSaleUnqualifiedReason("name='" + reason.getName() + "'","id=" + reason.getId())){
				j.setMsg("修改不合格原因失败!");
				return j;
			}
			j.setMsg("修改成功!");
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 删除不合格原因
	 * @author syuf
	 */
	@RequestMapping("/delUnqualifiedReason")
	@ResponseBody
	public Json delUnqualifiedReason(String reasonId)throws Exception{
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			if(!afStockService.delAfterSaleUnqualifiedReason("id=" + reasonId)){
				j.setMsg("删除不合格原因失败!");
				return j;
			}
			j.setMsg("删除成功!");
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 获取不合格原因列表
	 * @author syuf
	 */
	@RequestMapping("/getUnqualifiedReasonDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getUnqualifiedReasonDatagrid(EasyuiPageBean page)throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			String condition = " 1=1 ";
			int index = (page.getPage()-1)*page.getRows();
			int count = page.getRows();
			List<AfterSaleUnqualifiedReason> reasons = afStockService.getAfterSaleUnqualifiedReasonList(condition,index,count,null);
			datagrid.setRows(reasons);
			int total = afStockService.getAfterSaleUnqualifiedReasonCount(condition);
			datagrid.setTotal((long) total);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 导入包裹单号
	 * @author syuf
	 */
	@RequestMapping("/importPackageCode")
	@ResponseBody
	public Json importPackageCode(HttpServletRequest request, HttpServletResponse response,String deliverId,String packageCodes) {
		Json j = new Json();
		synchronized (importPackageCodeLock) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String result = null;
		try {
			result = afStockService.addAfterSalePackageCode(StringUtil.checkNull(deliverId),StringUtil.checkNull(packageCodes));
			if(result != null){
				j.setMsg(result);
				return j;
			}
			j.setMsg("导入成功!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		}
		return j;
	}
	/**
	 * 添加配件关联商品
	 * @author syuf
	 */
	@RequestMapping("/addMatchProduct")
	@ResponseBody
	public Json addMatchProduct(HttpServletRequest request, HttpServletResponse response,String ids,String fid)throws Exception{
		Json j = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			j = afStockService.addMatchProduct(StringUtil.checkNull(ids),StringUtil.checkNull(fid));
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 编辑配件关联商品
	 * @author syuf
	 */
	@RequestMapping("/editMatchProduct")
	@ResponseBody
	public Json editMatchProduct(HttpServletRequest request, HttpServletResponse response,String ids,String oldIds,String fittingId)throws Exception{
		Json j = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			j = afStockService.editMatchProduct(StringUtil.checkNull(ids),StringUtil.checkNull(oldIds),StringUtil.checkNull(fittingId));
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 批量导入 配件
	 * @author syuf
	 */
	@RequestMapping("/importFittingBatch")
	@ResponseBody
	public Json importFittingBatch(HttpServletRequest request, HttpServletResponse response,String fittingInfos)throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2059)) {
			j.setMsg("您没有权限进行此操作!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			j = afStockService.importFittingBatch(fittingInfos);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * 搜索配件商品 生成combobox 模糊查询
	 * @author syuf
	 */
	@RequestMapping("/searchMatchProduct")
	@ResponseBody
	public List<EasyuiComBoBoxBean> searchMatchProduct(HttpServletRequest request, HttpServletResponse response,String searchContent,String oldIds)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		List<EasyuiComBoBoxBean> comboboxList = null;
		try {
			comboboxList = afStockService.searchMatchProduct(searchContent,oldIds);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboboxList;
	}
	/**
	 * 加载已关联的配件商品
	 * @author syuf
	 */
	@RequestMapping("/loadMatchProduct")
	@ResponseBody
	public List<EasyuiComBoBoxBean> loadMatchProduct(HttpServletRequest request, HttpServletResponse response,String fittingCode)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		List<EasyuiComBoBoxBean> comboboxList = null;
		try {
			comboboxList = afStockService.loadMatchProduct(fittingCode);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboboxList;
	}
	/**
	 * 获取配件列表 关联商品
	 * @author syuf
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getAfterSaleFittingDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getAfterSaleFittingDatagrid(HttpServletRequest request,HttpServletResponse response,mmb.easyui.EasyuiPageBean page
			,String parentId2,String parentId3,String productName,String fittingName) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2059)) {
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" f.parent_id1 = 1536");
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and f.parent_id2 = ");
				buff.append(parentId2);
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and f.parent_id3 = ");
				buff.append(parentId3);
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.name = '");
				buff.append(productName + "'");
			}
			if(!"".equals(StringUtil.checkNull(fittingName))){
				buff.append(" and f.name = '");
				buff.append(fittingName + "'");
			}
			datagrid = afStockService.getAfterSaleFittingDatagrid(buff.toString(),page);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 历史调拨查询
	 * @author syuf
	 */
	@RequestMapping("/getAfterSaleExchangeDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleExchangeDatagrid(HttpServletRequest request,EasyuiPageBean page,String stockType,
			String startTime,String endTime,String afterSaleDetectCode,String createUserName, String areaId,String exchangeCode,
			String productCode,String exchangeStatus,String outStockType,String outAreaId) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" 1=1 ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(se.create_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(se.create_datetime,10) = '" + StringUtil.toSql(startTime) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(se.create_datetime,10) = '" + StringUtil.toSql(endTime) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				StringBuffer sb = new StringBuffer(); 
				int index = 0;
				for(String code : afterSaleDetectCode.split("\n")){
					if(index != 0){
						sb.append(",");
					}
					sb.append("'");
					sb.append(code);
					sb.append("'");
					index++;
				}
				buff.append(" and asdp.code in (" + sb.toString() + ")");
			}
			if(!"".equals(StringUtil.checkNull(exchangeCode))){
				buff.append(" and se.code='").append(exchangeCode).append("'");
			}
			if(!"-1".equals(StringUtil.checkNull(areaId)) && !"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and se.stock_in_area=" + StringUtil.toSql(areaId));
			}
			if(!"-1".equals(StringUtil.checkNull(stockType)) && !"".equals(StringUtil.checkNull(stockType))){
				buff.append(" and se.stock_in_type=" + StringUtil.toSql(stockType));
			}
			if(!"".equals(StringUtil.checkNull(createUserName))){
				buff.append(" and se.create_user_name='" + StringUtil.toSql(createUserName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.code='").append(StringUtil.toSql(productCode)).append("'");
			}
			if(!"-1".equals(StringUtil.checkNull(exchangeStatus)) && !"".equals(StringUtil.checkNull(exchangeStatus))){
				buff.append(" and se.status=").append(StringUtil.toSql(exchangeStatus));
			}
			if(!"-1".equals(StringUtil.checkNull(outAreaId)) && !"".equals(StringUtil.checkNull(outAreaId))){
				buff.append(" and se.stock_out_area=" + StringUtil.toSql(outAreaId));
			}
			if(!"-1".equals(StringUtil.checkNull(outStockType)) && !"".equals(StringUtil.checkNull(outStockType))){
				buff.append(" and se.stock_out_type=" + StringUtil.toSql(outStockType));
			}
			//总记录数
			int total = afStockService.getAfterSaleExchangeCount(buff.toString());
			datagrid.setTotal((long)total);
			//数据集
			List<Map<String,String>> rows = afStockService.getAfterSaleExchangeList(buff.toString(), (page.getPage()-1)*page.getRows(), page.getRows(), " se.create_datetime desc");
			for(Map<String,String> row : rows){
				StockExchangeBean bean = new StockExchangeBean();
				bean.setStatus(StringUtil.toInt(row.get("status")));
				row.put("exchangeStatusName", bean.getStatusName());
			}
			datagrid.setRows(rows);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 获取售后配件列表 
	 * @author hp
	 */
	@RequestMapping("/getAfterSaleFittingsDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getAfterSaleFittingsDatagrid(HttpServletRequest request,HttpServletResponse response,mmb.easyui.EasyuiPageBean page
			,String parentId2,String parentId3,String fittingName){
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			
			String status=request.getParameter("status");
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2 = ");
				buff.append(parentId2);
			
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3 = ");
				buff.append(parentId3);
				
			}
			
			if(!"".equals(StringUtil.checkNull(fittingName))){
				buff.append(" and p.name = '");
				buff.append(fittingName + "'");
				
			}
			
			datagrid = afStockService.getAfterSaleFittingsDatagrid(buff.toString(),page,status);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;  
		
	}
	/**
	 * 根据 一级分类id  
	 * 获取 2级 或3级分类
	 * hp
	 */
	@RequestMapping("/getAfterSaleCatalogNames")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterSaleCatalogNames(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
	
		String perentId=request.getParameter("perentId");
	
			if(perentId==null || "".equals(perentId)){
				perentId="1536";//配件一级分类
			}
			
		
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			List<voCatalog> list= afStockService.getCatalogNames(Integer.parseInt(perentId));
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for (int i=0;i<list.size();i++) {
				voCatalog voCatalog=list.get(i);
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(voCatalog.getId()+"");
				bean.setText(voCatalog.getName());
//				if(i==0){
//				bean.setSelected(true);
//				}
				comboBoxList.add(bean);
			}
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			dbOp.release();	
		}
		return comboBoxList;
		
	}
	/**
	 * @return 批量生成售后处理单号
	 * @author syuf
	 */
	@RequestMapping("/createDetectCode")
	@ResponseBody
	public Json createDetectCode(HttpServletRequest request,HttpServletResponse response,int count){
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			if(count <= 0){
				j.setMsg("数量必须大于0!");
				return j;
			}
			dbOp.startTransaction();
			List<String> codes = createDetectCodes(afStockService,count);
			dbOp.commitTransaction();
			if(codes == null || codes.size() == 0){
				j.setMsg("没有生成处理单号!");
				return j;
			}
			j.setSuccess(true);
			j.setObj(codes);
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			j.setMsg("生成处理单号失败!");
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * @return 售后处理单详细页检测
	 * @author syuf
	 */
	@RequestMapping("/detectAfterSaleProduct")
	@ResponseBody
	public Json detectAfterSaleProduct(HttpServletRequest request,String backSupplierProductId,String faultDescription,String flag,String detectCode){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			AfterSaleBackSupplierProduct backSupplierProduct = afStockService.getAfterSaleBackSupplierProduct("id=" + StringUtil.toSql(StringUtil.checkNull(backSupplierProductId)));
			if(backSupplierProduct == null ){
				j.setMsg("售后返厂商品数据不存在!");
				return j;
			}
			dbOp.startTransaction();
			StringBuffer set = new StringBuffer();
			String content = "";
			if("1".equals(flag)){
				set.append("status=" + AfterSaleBackSupplierProduct.STATUS5);
				content = "检测厂商寄回售后处理单[" + detectCode + "]的商品结果：合格";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE19,detectCode,null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
			}else if("2".equals(flag)){
				int stockType = 0;
				ResultSet rs = afStockService.getDbOp().executeQuery("select ci.stock_type from after_sale_detect_product asdp join cargo_info ci on asdp.cargo_whole_code=ci.whole_code where asdp.id=" + backSupplierProduct.getAfterSaleDetectProductId());
				if(rs!=null && rs.next()){
					stockType = rs.getInt(1);
				}
				if(stockType==ProductStockBean.STOCKTYPE_CUSTOMER){
					//客户库商品生成新的返厂记录
					AfterSaleBackSupplierProduct bean = backSupplierProduct;
					bean.setId(0);
					bean.setCreateDatetime(DateUtil.getNow());
					bean.setUserId(user.getId());
					bean.setUserName(user.getUsername());
					bean.setFirstRepair(1);//返修
					bean.setStatus(AfterSaleBackSupplierProduct.STATUS3);
					bean.setReturnDatetime(null);
					bean.setReturnUserId(0);
					bean.setReturnUserName("");
					if(!afStockService.addAfterSaleBackSupplierProduct(bean)){
						dbOp.rollbackTransaction();
						j.setMsg("添加返厂商品记录失败!");
						return j;
					}
				}
				if("请选择/请选择/请选择".equals(faultDescription)){
					faultDescription="";
				}
				if(StringUtil.checkNull(faultDescription).equals("")){
					dbOp.rollbackTransaction();
					j.setMsg("检测故障描述不能为空!");
					return j;
				}
				String faultDescriptions[] = faultDescription.replaceAll("请选择", "").split("/");
				if(faultDescriptions.length==1){
					set.append("status=" + AfterSaleBackSupplierProduct.STATUS6 + ",unqualified_reason_name='" + faultDescriptions[0] + "'");
				}
				if(faultDescriptions.length==2){
					set.append("status=" + AfterSaleBackSupplierProduct.STATUS6 + ",unqualified_reason_name='" + faultDescriptions[0] + "', unqualified_reason_name2='" + faultDescriptions[1] + "'");
				}
				if(faultDescriptions.length==3){
					set.append("status=" + AfterSaleBackSupplierProduct.STATUS6 + ",unqualified_reason_name='" + faultDescriptions[0] + "', unqualified_reason_name2='" + faultDescriptions[1] + "', unqualified_reason_name3='" + faultDescriptions[2] + "'");
				}
				
				content = "检测厂商寄回售后处理单[" + detectCode + "]的商品结果：不合格";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE19,detectCode,null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				
				//增加一条检测日志
				AfterSaleDetectLogBean log = new AfterSaleDetectLogBean();
				log.setAfterSaleDetectProductId(backSupplierProduct.getAfterSaleDetectProductId());
				log.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.FAULT_DESCRIPTION);
				if(faultDescriptions.length==1){
					log.setContent(faultDescriptions[0]);
				}
				if(faultDescriptions.length==2){
					log.setContent(faultDescriptions[0]);
					log.setContent2(faultDescriptions[1]);
				}
				if(faultDescriptions.length==3){
					log.setContent(faultDescriptions[0]);
					log.setContent2(faultDescriptions[1]);
					log.setContent3(faultDescriptions[2]);
				}
				log.setCreateDatetime(DateUtil.getNow());
				log.setUserId(user.getId());
				log.setUserName(user.getUsername());
				if(!afStockService.addAfterSaleDetectLog(log)){
					dbOp.rollbackTransaction();
					j.setMsg("新增检测日志失败!");
					return j;
				}
			}
			if("".equals(set.toString())){
				j.setMsg("数据异常!");
				return j;
			}
			if(!afStockService.updateAfterSaleBackSupplierProduct(set.toString(), "id=" + backSupplierProductId)){
				dbOp.rollbackTransaction();
				j.setMsg("更新售后返厂商品表失败!");
				return j;
			}
			dbOp.commitTransaction();
			j.setMsg("操作成功!");
			j.setSuccess(true);
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * @return 厂商包裹签收
	 * @author syuf
	 * @param flag 参数废弃
	 */
	@RequestMapping("/supplierPackageSign")
	@ResponseBody
	public Json supplierPackageSign(HttpServletRequest request,String afterSaleCode,String imeiCode, String productCode, int type,boolean flag){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		try {
			CommonLogic logic = new CommonLogic();
			return logic.receiveBackSupplierProduct(afterSaleCode, imeiCode, productCode, type, user, 1);			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}
	
	/**
	 * @return 返厂签收获取code
	 * @author mengqy
	 */
	@RequestMapping("/getCodeForBackSupplier")
	@ResponseBody
	public Json getCodeForBackSupplier(HttpServletRequest request,String afterSaleCode,String imeiCode, String productCode){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		try {
			CommonLogic logic = new CommonLogic();
			return logic.getCodeForBackSupplier(imeiCode, afterSaleCode, productCode);			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}
	
	/**
	 * @return 未妥投包裹签收
	 * @author syuf
	 */
	@RequestMapping("/noReceivePackageSign")
	@ResponseBody
	public Json noReceivePackageSign(HttpServletRequest request,String packageCode){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		if(!StringUtil.isNoBlank(packageCode)){
			j.setMsg("包裹单号不合法");
			return j;
		}
		try {
			if("".equals(StringUtil.checkNull(packageCode))){
				j.setMsg("包裹单号 不能为空!");
				return j;
			}
			CommonLogic logic = new CommonLogic();
			String result = logic.receiveBackuserPackage(packageCode, user, 1);
			if(result == null){
				result = "操作成功!";
			}
			j.setMsg(result);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}
		return j;
	}
	/**
	 * @return 客户寄回包裹签收
	 * @author syuf
	 */
	@RequestMapping("/customerPackageSign")
	@ResponseBody
	public Json customerPackageSign(HttpServletRequest request,AfterSaleDetectPackageBean bean){
		Json j = new Json();
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		if(!StringUtil.isNoBlank(bean.getPackageCode())){
			j.setMsg("包裹单号不合法");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		synchronized (lock) {
			try {
				
				if(bean == null){
					j.setMsg("接收数据异常!");
					return j;
				}
				if(!afStockService.checkAfterSaleUserGroup(user, bean.getAreaId())){
					j.setMsg("没有该地区售后仓内作业权限!");
					return j;
				}
				bean.setPackageCode(bean.getPackageCode().trim());
				if (afStockService.getAfterSaleDetectPackage(" package_code = '" + bean.getPackageCode() + "' ") != null){
					j.setMsg("该包裹已签收!");
					return j;
				}
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setCreateUserId(user.getId());
				bean.setCreateUserName(user.getUsername());
				
				dbOp.startTransaction();
				if(!afStockService.addAfterSaleDetectPackage(bean)){
					dbOp.rollbackTransaction();
					j.setMsg("添加失败!");
					return j;
				}
				AfterSaleWarehourcePackageListBean b = new AfterSaleWarehourcePackageListBean();
				b.setCreateDatetime(DateUtil.getNow());
				b.setCreateUserId(user.getId());
				b.setCreateUserName(user.getUsername());
				b.setDeliverId(bean.getDeliverId());
				b.setFreight(bean.getFreight());
				b.setPackageCode(bean.getPackageCode());
				b.setPayType(bean.getReturnType());
				b.setPostType(AfterSaleWarehourcePackageListBean.POSTTYPE2);
				if(!afStockService.addAfterSaleWarehourcePackage(b)){
					dbOp.rollbackTransaction();
					j.setMsg("添加失败!");
					return j;
				}
				String content = "客户寄回包裹签收，包裹单号【" + bean.getPackageCode() + "】";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE1,bean.getPackageCode(),null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				dbOp.commitTransaction();
				j.setMsg("添加成功!");
			} catch (Exception e) {
				dbOp.rollbackTransaction();
				System.out.print(DateUtil.getNow());e.printStackTrace();
			} finally {
				dbOp.release();
			}
		}
		return j;
	}
	/**
	 * 查询处理单日志
	 * @author syuf
	 */
	@RequestMapping("/getAfterSaleLogDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getAfterSaleLogDatagrid(EasyuiPageBean page, String detectCode){
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append("oper_code='" + StringUtil.checkNull(detectCode) + "'");
			int total = afStockService.getAfterSaleLogCount(buff.toString());
			datagrid.setTotal((long) total);
			List<AfterSaleLogBean> logs = afStockService.getAfterSaleLogList(buff.toString(),(page.getPage()-1)*page.getRows(),page.getRows(),"create_datetime desc");
			datagrid.setRows(logs);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 获取售后商品列表
	 * @author syuf
	 */
	@RequestMapping("/getAfterSaleProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleProductDatagrid(HttpServletRequest request,HttpServletResponse response,
			EasyuiPageBean page,String stockType,String startTime,String endTime,String afterSaleCode,String productName,
			String afterSaleDetectCode,String parentId1,String parentId2,String parentId3,String productCode, String afterSaleStockinType, 
			String mainProductStatus, String sellType,String areaId) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" asdp.status in (0,1,2,3,4,5,6,7,10,12,13,14,15,16,17,18,19,24) and asdp.lock_status!=2 ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(asdp.create_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(startTime) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(endTime) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))){
				buff.append(" and asdp.after_sale_order_code='" + StringUtil.toSql(afterSaleCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				buff.append(" and asdp.code='" + StringUtil.toSql(afterSaleDetectCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=" + StringUtil.toSql(areaId));
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(stockType))){
				buff.append(" and ci.stock_type=" + StringUtil.toSql(stockType));
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.name='" + StringUtil.toSql(productName) + "'");
			}
			if(StringUtil.toInt(afterSaleStockinType) != -1){
				buff.append(" and ass.type=" + afterSaleStockinType);
			}
			if (StringUtil.toInt(sellType) != -1) {
				buff.append(" and psp.type=" + sellType);
			}
			if(!"".equals(StringUtil.checkNull(mainProductStatus))){
				buff.append(" and asdl.content='" + StringUtil.toSql(mainProductStatus) + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=").append(StringUtil.toSql(areaId));
			}
			int total = afStockService.getAfterSaleProductCount(buff.toString());
			datagrid.setTotal((long)total);
			List<Map<String,String>> rows = afStockService.getAfterSaleProductList(buff.toString(), (page.getPage()-1)*page.getRows(), page.getRows(), " asdp.create_datetime desc");
			//增加问题分类和故障描述内容 2014-05-29 李宁
			if(rows!=null && rows.size()>0){
				for(int i=0;i<rows.size();i++){
					Map<String,String> row = rows.get(i);
					int detectId = StringUtil.StringToId(row.get("id"));
					if(detectId>0){
						row.put("questionDescription", afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.QUESTION_DESCRIPTION,detectId));
						row.put("faultDescription",afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION,detectId));
					}
				}
			}
			datagrid.setRows(rows);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 获取客户商品库存列表
	 * @author zy
	 */
	@RequestMapping("/getCustomerAfterSaleProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getCustomerAfterSaleProductDatagrid(HttpServletRequest request,HttpServletResponse response,
			EasyuiPageBean page,String stockType,String startTime,String endTime,String afterSaleCode,String productName,
			String afterSaleDetectCode,String parentId1,String parentId2,String parentId3,String productCode,String customerPhone,String saleType,String areaId) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" asdp.status in (0,1,2,3,4,5,6,7,10,12,13,14,15,16,17,18) ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(asdp.create_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(startTime) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(endTime) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))){
				buff.append(" and asdp.after_sale_order_code='" + StringUtil.toSql(afterSaleCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				buff.append(" and asdp.code='" + StringUtil.toSql(afterSaleDetectCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1 in(" + StringUtil.toSql(parentId1) + ") ");
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2 in(" + StringUtil.toSql(parentId2) + ") ");
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3 in(" + StringUtil.toSql(parentId3) + ") ");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(stockType))){
				buff.append(" and ci.stock_type=" + StringUtil.toSql(stockType));
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.name='" + StringUtil.toSql(productName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=").append(StringUtil.toSql(areaId));
			}
			if(!"".equals(StringUtil.checkNull(customerPhone))){
				buff.append(" and aso.customer_phone='").append(StringUtil.toSql(customerPhone)).append("'");
			}
			if(!"".equals(StringUtil.checkNull(saleType))){
				buff.append(" and psp.type=").append(StringUtil.toSql(saleType));
			}
			int total = afStockService.getCustomerAfterSaleProductCount(buff.toString());
			datagrid.setTotal((long)total);
			List<Map<String,String>> rows = afStockService.getCustomerAfterSaleProductList(buff.toString(), (page.getPage()-1)*page.getRows(), page.getRows(), " asdp.create_datetime desc");
			if(rows.size()>0){
				for(int i=0;i<rows.size();i++){
					Map<String,String> row = rows.get(i);
					int afterSaleProductId = StringUtil.parstInt(row.get("id"));
					//查询寄回配件信息
					String fitting = afStockService.getAfterSaleFitting(afterSaleProductId,0);
					//查询检测原因
					String content = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, afterSaleProductId);
					fitting = fitting.replaceAll("\n", "<br/>");
					row.put("fitting", fitting);
					row.put("content", content);
				}
			}
			datagrid.setRows(rows);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 获取以换代修商品列表
	 * @author syuf
	 */
	@RequestMapping("/getChangeRepairProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getChangeRepairProductDatagrid(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page,
			String afterSaleDetectCode,String productName,String imeiCode,String status,String productCode,String afterSaleCode,String supplier){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" 1=1 ");
			buff.append(" and ass.type=" + AfterSaleStockin.TYPE3);
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				buff.append(" and asdp.code='" + StringUtil.toSql(afterSaleDetectCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))){
				buff.append(" and asdp.after_sale_order_code='" + StringUtil.toSql(afterSaleCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.`name`='" + StringUtil.toSql(productName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(imeiCode))){
				buff.append(" and asdp.IMEI='" + StringUtil.toSql(imeiCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(status))){
				buff.append(" and asbsp.`status`='" + StringUtil.toSql(status) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(supplier)) && !"-1".equals(StringUtil.checkNull(supplier))){
				buff.append(" and asbs.id=" + supplier);
			}
			int total = afStockService.getAfterSaleChangeRepairProductCount(buff.toString());
			datagrid.setTotal((long)total);
			List<Map<String,String>> rows = afStockService.getAfterSaleChangeRepairProductList(buff.toString(), (page.getPage()-1)*page.getRows(), page.getRows(), " create_datetime desc");
			if(rows != null && rows.size() > 0){
				for(Map<String,String> row : rows){
					//2015-03-17 故障描述
					int detecttypeId = StringUtil.StringToId(row.get("id"));
					String faultDescript = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, detecttypeId);
					row.put("problemRmark", faultDescript);
				}
			}
			datagrid.setRows(rows);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 获取返厂商品列表
	 * @author syuf
	 */
	@RequestMapping("/getBackSupplierProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getBackSupplierProductDatagrid(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page,
			String startTime,String endTime,String packageCode,String afterSaleDetectCode,String productName,String imeiCode,
			String productCode,String declareStatus,String productType,String supplier,String status,String orderCode,String areaId,
			String startTime_detect,String endTime_detect,String parentId1){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			StringBuffer sb = null;
			buff.append(" 1=1 ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(asbsp.send_datetime,10) BETWEEN '" + StringUtil.toSql(StringUtil.checkNull(startTime)) + "' and '" + StringUtil.toSql(StringUtil.checkNull(endTime)) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(asbsp.send_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(startTime)) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(asbsp.send_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(endTime)) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(startTime_detect)) && !"".equals(StringUtil.checkNull(endTime_detect))){
				buff.append(" and left(asdp.create_datetime,10) BETWEEN '" + StringUtil.toSql(StringUtil.checkNull(startTime_detect)) + "' and '" + StringUtil.toSql(StringUtil.checkNull(endTime_detect)) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime_detect))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(startTime_detect)) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime_detect))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(endTime_detect)) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(status))){
				buff.append(" and asbsp.`status`=" + StringUtil.toSql(status));
			} else {
				buff.append(" and (asbsp.`status`=" + AfterSaleBackSupplierProduct.STATUS0 + " or asbsp.status=" + AfterSaleBackSupplierProduct.STATUS4 + ") ");
			}
			if(!"".equals(StringUtil.checkNull(packageCode))){
				buff.append(" and asbsp.package_code='" + StringUtil.toSql(packageCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(orderCode))){
				sb = new StringBuffer();
				int i = 0;
				sb.append("(");
				for(String s : orderCode.split("\n")){
					i++;
					if(!"".equals(s.trim())){
						sb.append("'" + StringUtil.toSql(s) + "'");
						if(i < orderCode.split("\n").length){
							sb.append(",");
						}
					}
				}
				sb.append(")");
				buff.append(" and aso.order_code in " +sb.toString());
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				sb = new StringBuffer();
				int i = 0;
				sb.append("(");
				for(String s : afterSaleDetectCode.split("\n")){
					i++;
					if(!"".equals(s.trim())){
						sb.append("'" + StringUtil.toSql(s) + "'");
						if(i < afterSaleDetectCode.split("\n").length){
							sb.append(",");
						}
					}
				}
				sb.append(")");
				buff.append(" and asdp.code in " + sb.toString());
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.`name`='" + StringUtil.toSql(productName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(imeiCode))){
				buff.append(" and asbsp.IMEI='" + StringUtil.toSql(imeiCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(supplier)) && !"-1".equals(StringUtil.checkNull(supplier))){
				buff.append(" and asbs.id=" + supplier);
			}
			if(!"".equals(StringUtil.checkNull(productType))){
				buff.append(" and ci.stock_type=" + productType);
			}else{
				buff.append(" and (ci.stock_type=" + CargoInfoBean.STOCKTYPE_AFTER_SALE + " or ci.stock_type=" + CargoInfoBean.STOCKTYPE_CUSTOMER + ")");
			}
			//if(!"".equals(StringUtil.checkNull(declareStatus))){
			//	buff.append(" and asdl.content='" + StringUtil.toSql(declareStatus) + "'");
			//}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=").append(StringUtil.toSql(areaId));
			}
			
			int total = afStockService.getAfterSaleBackSupplierAndProductCount(buff.toString());
			datagrid.setTotal((long)total);
			List<Map<String,String>> rows = afStockService.getAfterSaleBackSupplierAndProductList(buff.toString(), (page.getPage()-1)*page.getRows(), page.getRows(), " asbsp.create_datetime desc");
			if(rows != null && rows.size() > 0){
				for(Map<String,String> row : rows){
					String problemRmark = afStockService.getAfterSaleDetectContentByDetect("asdl.after_sale_detect_product_id=" + row.get("detectId") 
							+ " and asdt.id=" + AfterSaleDetectTypeBean.FAULT_DESCRIPTION,"asdl.create_datetime desc");
					row.put("problemRmark", problemRmark);
					
					String problemCode = afStockService.getAfterSaleDetectContentByDetect("asdl.after_sale_detect_product_id=" + row.get("detectId") 
							+ " and asdt.id=" + AfterSaleDetectTypeBean.FAULT_CODE,"asdl.create_datetime desc");
					row.put("problemCode", problemCode);
					
					declareStatus = afStockService.getAfterSaleDetectContentByDetect("asdl.after_sale_detect_product_id=" + row.get("detectId") 
							+ " and asdt.id=" + AfterSaleDetectTypeBean.REPORT_STATUS,"asdl.create_datetime desc");
					row.put("declareStatus", declareStatus);
				}
			}
			datagrid.setRows(rows);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 导出返厂商品列表
	 * @author syuf
	 */
	@RequestMapping("/excelBackSupplierProduct")
	@ResponseBody
	public void excelBackSupplierProduct(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page,
			String startTime,String endTime,String packageCode,String afterSaleDetectCode,String productName,String imeiCode,
			String productCode,String declareStatus,String productType,String supplier,String status,String orderCode,String areaId,
			String startTime_detect,String endTime_detect,String parentId1){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			StringBuffer sb = null;
			buff.append(" 1=1 ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(asbsp.send_datetime,10) BETWEEN '" + StringUtil.toSql(StringUtil.checkNull(startTime)) + "' and '" + StringUtil.toSql(StringUtil.checkNull(endTime)) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(asbsp.send_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(startTime)) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(asbsp.send_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(endTime)) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(startTime_detect)) && !"".equals(StringUtil.checkNull(endTime_detect))){
				buff.append(" and left(asdp.create_datetime,10) BETWEEN '" + StringUtil.toSql(StringUtil.checkNull(startTime_detect)) + "' and '" + StringUtil.toSql(StringUtil.checkNull(endTime_detect)) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime_detect))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(startTime_detect)) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime_detect))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(StringUtil.checkNull(endTime_detect)) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(status))){
				buff.append(" and asbsp.`status`=" + StringUtil.toSql(status));
			} else {
				buff.append(" and (asbsp.`status`=" + AfterSaleBackSupplierProduct.STATUS0 + " or asbsp.status=" + AfterSaleBackSupplierProduct.STATUS4 + ") ");
			}
			if(!"".equals(StringUtil.checkNull(packageCode))){
				buff.append(" and asbsp.package_code='" + StringUtil.toSql(packageCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(orderCode))){
				sb = new StringBuffer();
				int i = 0;
				sb.append("(");
				for(String s : orderCode.split("\n")){
					i++;
					if(!"".equals(s.trim())){
						sb.append("'" + s + "'");
						if(i < orderCode.split("\n").length){
							sb.append(",");
						}
					}
				}
				sb.append(")");
				buff.append(" and aso.order_code in " +sb.toString());
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				sb = new StringBuffer();
				int i = 0;
				sb.append("(");
				for(String s : afterSaleDetectCode.split("\n")){
					i++;
					if(!"".equals(s.trim())){
						sb.append("'" + s + "'");
						if(i < afterSaleDetectCode.split("\n").length){
							sb.append(",");
						}
					}
				}
				sb.append(")");
				buff.append(" and asdp.code in " + sb.toString());
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.`name`='" + StringUtil.toSql(productName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(imeiCode))){
				buff.append(" and asdp.IMEI='" + StringUtil.toSql(imeiCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(supplier)) && !"-1".equals(StringUtil.checkNull(supplier))){
				buff.append(" and asbs.id=" + supplier);
			}
			if(!"".equals(StringUtil.checkNull(productType))){
				buff.append(" and ci.stock_type=" + productType);
			}else{
				buff.append(" and (ci.stock_type=" + CargoInfoBean.STOCKTYPE_AFTER_SALE + " or ci.stock_type=" + CargoInfoBean.STOCKTYPE_CUSTOMER + ")");
			}
			if(!"".equals(StringUtil.checkNull(declareStatus))){
				buff.append(" and asdl.content='" + declareStatus + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=").append(StringUtil.toSql(areaId));
			}
			List<Map<String,String>> rows = afStockService.getAfterSaleBackSupplierAndProductList(buff.toString(), -1, -1, " asbsp.create_datetime desc");
			if(rows != null && rows.size() > 0){
				for(Map<String,String> row : rows){
					String problemRmark = afStockService.getAfterSaleDetectContentByDetect("asdl.after_sale_detect_product_id=" + row.get("detectId") 
							+ " and asdt.id=" + AfterSaleDetectTypeBean.FAULT_DESCRIPTION,"asdl.create_datetime desc");
					row.put("problemRmark", problemRmark);
					String problemCode = afStockService.getAfterSaleDetectContentByDetect("asdl.after_sale_detect_product_id=" + row.get("detectId") 
							+ " and asdt.id=" + AfterSaleDetectTypeBean.FAULT_CODE,"asdl.create_datetime desc");
					row.put("problemCode", problemCode);
				}
			}
			afStockService.excelBackSupplierProduct(response,rows);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	/**
	 * @return 检测选项comboBox
	 * @author zy
	 */
	@RequestMapping("/getAfterSaleDetectType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterSaleDetectType(HttpServletRequest request,HttpServletResponse response,
			String detectTypeId) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		int id = StringUtil.StringToId(detectTypeId);
		try {
			List<AfterSaleDetectTypeBean> list = afStockService.getAfterSaleDetectTypeList(" type=" + AfterSaleDetectTypeBean.TYPE1, -1, -1, null);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			if(id <= 0){
				bean.setSelected(true);
			}
			comboBoxList.add(bean);
			if(list != null && list.size() > 0){
				for(AfterSaleDetectTypeBean afterSaleDetectType : list){
					bean = new EasyuiComBoBoxBean();
					if(!"17".equals(afterSaleDetectType.getId()+"")){
						bean.setId(afterSaleDetectType.getId() + "");
						bean.setText(afterSaleDetectType.getName());
						if(id > 0){
							if(afterSaleDetectType.getId() == id){
								bean.setSelected(true);
							}
						}
					}
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboBoxList;
	}
	/**
	 * 一级分类
	 */
	@RequestMapping("/getParentId1")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getParentId1(HttpServletRequest request, HttpServletResponse response,String parentId1)throws Exception{
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService service = new WareService(db);
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		int id = StringUtil.StringToId(parentId1);
		try {
			List firstList = service.getCatalogs("parent_id=0");
			Iterator iter = firstList.listIterator();
			EasyuiComBoBoxBean bean = null;
			bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			if(id <= 0){
				bean.setSelected(true);
			}
			easyuilist.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("全部");
			easyuilist.add(bean);
			while(iter.hasNext()){
				voCatalog catalog = (voCatalog)iter.next();
				bean = new EasyuiComBoBoxBean();
				bean.setId("" + catalog.getId());
				bean.setText(catalog.getName());
				if(id > 0){
					if(id == catalog.getId()){
						bean.setSelected(true);
					}
				}
				easyuilist.add(bean);
			}
		} catch(Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return easyuilist;
	}
	
	/**
	 * 获取一级、二级内容分类
	 * 用于添加内容分类时
	 * @param request
	 * @param response
	 * @param easyuiPage
	 * @param detectTypeParentId1
	 * @param detectTypeParentId2
	 * @return
	 * 2015年1月6日
	 * lining
	 */
	@RequestMapping("/getDetectTypeDetails")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDetectTypeDetails(HttpServletRequest request, HttpServletResponse response,String detectTypeDetailId,boolean flag){
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,db);
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
			int detailId = StringUtil.StringToId(detectTypeDetailId);
			if(detailId <= 0){
				request.setAttribute("msg", "获取内容分类失败!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			AfterSaleDetectTypeDetailBean detailBean = afStockService.getAfterSaleDetectTypeDetail("id=" + detailId);
			StringBuilder condition = new StringBuilder();
			condition.append("after_sale_detect_type_id=").append(detailBean.getAfterSaleDetectTypeId()).append(" and parent_id1=").append(detailBean.getParentId1())
						.append(" and detect_type_parent_id2=0");
			if(flag){
				condition.append(" and detect_type_parent_id1=").append(detailBean.getDetectTypeParentId1());
			}else{
				condition.append(" and detect_type_parent_id1=0");
			}
			
			List<AfterSaleDetectTypeDetailBean> list = afStockService.getAfterSaleDetectTypeDetailList(condition.toString(), -1, -1, null);
			EasyuiComBoBoxBean bean = null;
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("请选择");
			easyuilist.add(bean);
			if(list != null && list.size() > 0){
				for(int i=0;i<list.size();i++){
					AfterSaleDetectTypeDetailBean detail = list.get(i);				
					bean = new EasyuiComBoBoxBean();
					bean.setId("" + detail.getId());
					bean.setText(detail.getContent());
					if(detail.getId() == detailId){
						bean.setSelected(true);
					}else if(detailBean.getDetectTypeParentId1() == detail.getId()){
						bean.setSelected(true);
					}
					easyuilist.add(bean);
				}
			}
		} catch(Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return easyuilist;
	}
	
	/**
	 * 获取二级、三级内容分类
	 * 用于显示查询
	 * @param request
	 * @param response
	 * @param easyuiPage
	 * @param detectTypeParentId1
	 * @param detectTypeParentId2
	 * @return
	 * 2015年1月6日
	 * lining
	 */
	@RequestMapping("/getDetectTypeDetailsLevel")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDetectTypeDetailsLevel(HttpServletRequest request, HttpServletResponse response,
			String afterSaleDetectTypeId, String parentId1,String detectTypeParentId1,String detectTypeParentId2){
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,db);
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
			int detectType = StringUtil.StringToId(afterSaleDetectTypeId);
			int catalogId = StringUtil.StringToId(parentId1);
			int detectTypeDetailId1 = StringUtil.StringToId(detectTypeParentId1);
			int detectTypeDetailId2 = StringUtil.StringToId(detectTypeParentId2);
			StringBuilder condition = new StringBuilder();
			condition.append(" detect_type_parent_id1=").append(detectTypeDetailId1).append(" and detect_type_parent_id2=").append(detectTypeDetailId2);
			if(detectType > 0){
				condition.append(" and after_sale_detect_type_id=").append(detectType);
			}
			if(catalogId > 0){
				condition.append(" and parent_id1=").append(catalogId);
			}
			List<AfterSaleDetectTypeDetailBean> list = afStockService.getAfterSaleDetectTypeDetailList(condition.toString(), -1, -1, null);
			EasyuiComBoBoxBean bean = null;
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("请选择");
			bean.setSelected(true);
			easyuilist.add(bean);
			if(list != null && list.size() > 0){
				for(int i=0;i<list.size();i++){
					AfterSaleDetectTypeDetailBean detail = list.get(i);				
					bean = new EasyuiComBoBoxBean();
					bean.setId("" + detail.getId());
					bean.setText(detail.getContent());
					easyuilist.add(bean);
				}
			}
		} catch(Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return easyuilist;
	}
	/**
	 * 检测选项设置
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDetectTypeDetailDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getDetectTypeDetailDatagrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage, 
			String afterSaleDetectTypeId, String parentId1,String detectTypeParentId1,String detectTypeParentId2,String detectTypeDetailId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			int detectTypeId = StringUtil.toInt(afterSaleDetectTypeId);
			if (detectTypeId != -1 && detectTypeId !=0) {
				buff.append(" and asdtd.after_sale_detect_type_id=" + detectTypeId);
			}
			int catalogId = StringUtil.toInt(parentId1);
			if (catalogId != -1 && catalogId != 0) {
				buff.append(" and asdtd.parent_id1=" + parentId1);
			}
			int detectTypeParent1 = StringUtil.parstBackMinus(detectTypeParentId1);
			if (detectTypeParent1 > -1) {
				buff.append(" and asdtd.detect_type_parent_id1=" + detectTypeParent1);
			}
			int detectTypeParent2 = StringUtil.parstBackMinus(detectTypeParentId2);
			if (detectTypeParent2 > -1) {
				buff.append(" and asdtd.detect_type_parent_id2=" + detectTypeParent2);
			}
			int id = StringUtil.StringToId(detectTypeDetailId);
			if(id > 0){
				buff.append(" and asdtd.id=").append(id);
			}
			int totalCount = afStockService.getDetectTypeDetailCount(detectTypeParent1,detectTypeParent2,buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleDetectTypeDetailBean> maps = afStockService.getDetectTypeDetailList(detectTypeParent1,detectTypeParent2,buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "asdtd.id desc");
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return datagrid;
	}
	/**
	 * 保存检测选项
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/saveDetectTypeDetail")
	@ResponseBody
	public Json saveDetectTypeDetail (HttpServletRequest request, HttpServletResponse response, AfterSaleDetectTypeDetailBean detailBean) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
    	if(!user.getGroup().isFlag(1456)){
    		j.setMsg("没有检测内容选项设置的权限!");
            return j;
    	}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		if(detailBean == null){
			j.setMsg("程序异常,接收数据失败!");
			return j;
		}
		String content = StringUtil.checkNull(detailBean.getContent()).trim();
		if(content.length() <= 0){
			j.setMsg("内容分类不能为空!");
			return j;
		}
		if(content.length() > 50){
			j.setMsg("不能超过50字!");
			return j;
		}
		try{
    		synchronized(lock){
    			int count = afStockService.getAfterSaleDetectTypeDetailCount("after_sale_detect_type_id="+detailBean.getAfterSaleDetectTypeId()
    					+" and parent_id1="+detailBean.getParentId1()+" and detect_type_parent_id1="+detailBean.getDetectTypeParentId1()
    					+" and detect_type_parent_id2=" + detailBean.getDetectTypeParentId2()
    					+ " and content='" + StringUtil.toSql(content) + "'");
    			if(count>0){
    				j.setMsg("有重复的内容分类,请重新填写内容!");
    				return j;
    			}
    			afStockService.getDbOp().startTransaction();
    			if(!afStockService.updateAfterSaleDetectTypeDetail("content='"+StringUtil.toSql(content)+"'", "id="+detailBean.getId())){
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("保存失败!");
    				return j;
    			}
    			afStockService.getDbOp().commitTransaction();
    			j.setSuccess(true);
    			j.setMsg("编辑成功!");
    			return j;
    		}
		} catch(Exception e) {
			j.setMsg("异常！");
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return j;
	}
	/**
	 * 获取待检测包裹列表
	 * hepeng
	 * @return 1
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDetectPackAgegrid")
	@ResponseBody
	public EasyuiDataGridJson getDetectPackAgegrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			AfterSaleDetectPackageBean afterSaleDetectPackageBean,String afterSaleCode,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			
			String startTime=request.getParameter("startTime");
			
			String endTime=request.getParameter("endTime");
		
			StringBuffer buff = new StringBuffer();
			buff.append(" asdp.status in (1) ");
			if(!"".equals(StringUtil.checkNull(afterSaleDetectPackageBean.getPackageCode()))) {
				buff.append(" and asdp.package_code like '%" + afterSaleDetectPackageBean.getPackageCode()+"%'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and asdp.area_id=" + areaId);
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectPackageBean.getOrderCode()))) {
				buff.append(" and asdp.order_code='" + afterSaleDetectPackageBean.getOrderCode()+"'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))) {
				buff.append(" and asdpr.after_sale_order_code='" + afterSaleCode +"'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectPackageBean.getCreateUserName()))) {
				buff.append(" and asdp.create_user_name='" + afterSaleDetectPackageBean.getCreateUserName()+"'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime<='" + endTime + " 23:59:59'");
			}
			int totalCount = afStockService.getDetectPackAgeCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleDetectPackageBean> maps = afStockService.getDetectPackAgeList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " create_datetime desc ");
			for (AfterSaleDetectPackageBean bean : maps) {
				bean.setAfterSaleOrderCodes(afStockService.getAftersaleOrderIds(bean.getId(), dbOp));
			}
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 待再次检测商品列表
	 * hepeng
	 * @return 2
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDetectProductgrid")
	@ResponseBody
	public EasyuiDataGridJson getDetectProductgrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String code,String content,String afterSaleOrderCode,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(code))) {
				buff.append(" and ap.code like '%" + code+"%'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleOrderCode))) {
				buff.append(" and ap.after_sale_order_code like '%" + afterSaleOrderCode +"%'");
			}
			if(!"".equals(StringUtil.checkNull(content))) {
				buff.append(" and p.code = '" + content+"'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id = " + areaId);
			}
			int totalCount = afStockService.getDetectProductCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleDetectProductBean> maps = afStockService.getDetectProductList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " ap.create_datetime desc ");
			
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 待寄回用户商品列表
	 * hepeng
	 * @return 5
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBackUsergrid")
	@ResponseBody
	public EasyuiDataGridJson getBackUsergrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			AfterSaleBackSupplierProduct afterSaleBackSupplierProduct,String areaId,String spareCode) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id = " + areaId);
			}
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getCode()))) {
				buff.append(" and ap.code = '" + afterSaleBackSupplierProduct.getCode()+"'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getAfterSaleOrderCode()))) {
				buff.append(" and ap.after_sale_order_code like '%" + afterSaleBackSupplierProduct.getAfterSaleOrderCode()+"%'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getProductCode()))) {
				buff.append(" and p.code = '" + afterSaleBackSupplierProduct.getProductCode()+"'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getType()))) {
				buff.append(" and afbp.type = " +Integer.parseInt(afterSaleBackSupplierProduct.getType())+"");
			}
			if(!"".equals(StringUtil.checkNull(spareCode))) {
				buff.append(" and asrnpr.spare_code = '" + spareCode+"'");
			}
			int totalCount = afStockService.getBackUserCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleBackUserProduct> maps = afStockService.getBackUserList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "ap.create_datetime desc ");
			if(maps.size()>0){
				for(int i=0;i<maps.size();i++){
					AfterSaleBackUserProduct bean = maps.get(i);
					//查询寄回配件信息
					String fitting = afStockService.getAfterSaleFitting(bean.getAfterSaleDetectProductId(),1);
					fitting = fitting.replaceAll("\n", "<br/>");
					bean.setFittings(fitting);
				}
			}
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 售后库入库列表
	 * hepeng
	 * @return 6
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getSaleStockgrid")
	@ResponseBody
	public EasyuiDataGridJson getSaleStockgrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage, 
			String code,String areaId,String startTime,String endTime,String status,String parentId1,String parentId2,String parentId3) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			
			String idStr;
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(code))) {
				buff.append(" and code like '%" + code +"%'");
			}
			// 接收返回的处理单id 字符串
			if(buff.length()>2){
			    idStr=afStockService.getAfterSaleIds(buff.toString());
			    buff.delete(0, buff.length());
			    if(!"0".equals(idStr) && !"".equals(StringUtil.checkNull(idStr))){
			    	buff.append(" and ats.after_sale_detect_product_id in("+idStr+")");  
				}
			}
			
			if (!"".equals(StringUtil.checkNull(startTime))) {
				buff.append(" and ats.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (!"".equals(StringUtil.checkNull(endTime))) {
				buff.append(" and ats.create_datetime<='" + endTime + " 23:59:59'");
			}
			if (!"".equals(StringUtil.checkNull(status))) {
				buff.append(" and ap.status =" + status + "");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id =" + areaId);
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			int totalCount = afStockService.getSaleStockCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleStockin> maps = afStockService.getSaleStockList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " ats.create_datetime desc");
			
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 售后库上架列表
	 * hepeng
	 * @return 7
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getSaleUpshelfgrid")
	@ResponseBody
	public EasyuiDataGridJson getSaleUpshelfgrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String code,String startTime,String endTime,String status,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
		
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(code))) {
				buff.append(" and ap.code like '%" + code +"%'");
			}
			if (!"".equals(StringUtil.checkNull(startTime))) {
				buff.append(" and ap.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (!"".equals(StringUtil.checkNull(endTime))) {
				buff.append(" and ap.create_datetime<='" + endTime + " 23:59:59'");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id =" + areaId + "");
			}
			if (!"".equals(StringUtil.checkNull(status))) {
				buff.append(" and ap.status =" + status + "");
			}
			int totalCount = afStockService.getSaleUpshelfCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleStockin> maps = afStockService.getSaleUpshelfList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " ap.create_datetime desc ");
			
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 说明：获取等待返厂商品列表
	 * 日期：2014-06-27
	 * @author syuf
	 */
	@RequestMapping("/getWaitBackSupplierProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getWaitBackSupplierProductDatagrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			AfterSaleBackSupplierProduct afterSaleBackSupplierProduct,String areaId,String content,String parentId1,String parentId2,String parentId3,String status) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			String idStr = "";
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getAfterSaleOrderCode()))) {
				buff.append(" and after_sale_order_code like '%" + afterSaleBackSupplierProduct.getAfterSaleOrderCode()+"%'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getCode()))) {
				buff.append(" and code like '%" + afterSaleBackSupplierProduct.getCode()+"%'");
			}
			// 接收返回的处理单id 字符串
			if(buff.length()>2){
				idStr = afStockService.getAfterSaleIds(buff.toString());
				buff.delete(0, buff.length());
				if(!"0".equals(idStr) && !"".equals(StringUtil.checkNull(idStr))){
					buff.append(" and absp.after_sale_detect_product_id in("+idStr+")");  
				}
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and asdp.area_id = " + areaId);
			}
			if(!"".equals(StringUtil.checkNull(status)) && !"-1".equals(StringUtil.checkNull(status))) {
				buff.append(" and absp.status =" + status);
			}else {
				buff.append(" and absp.status =" + AfterSaleBackSupplierProduct.STATUS3);
			}
			if(!"".equals(StringUtil.checkNull(content))) {
				buff.append(" and p.code = '" + content+"'");
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			int totalCount = afStockService.getWaitBackSupplierProductCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleBackSupplierProduct> maps = afStockService.getWaitBackSupplierProductList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " create_datetime desc ");
			if(maps!=null && maps.size()>0){
				for(int i=0;i<maps.size();i++){
					AfterSaleBackSupplierProduct backSupplierProduct = maps.get(i);
					//2015-03-17 修改故障描述
					String faultDescription = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, backSupplierProduct.getAfterSaleDetectProductId());
					backSupplierProduct.setFaultDescription(faultDescription);
				}
			}
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 厂商寄回商品上架任务列表
	 * hepeng
	 * @return 3  2-28
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getSupplierProductgrid")
	@ResponseBody
	public EasyuiDataGridJson getSupplierProductgrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			AfterSaleBackSupplierProduct afterSaleBackSupplierProduct,String areaId,String startTime_send,String endTime_send) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			String startTime=request.getParameter("startTime");
			String idStr = "";
			String endTime=request.getParameter("endTime");
			String status=request.getParameter("status");
			if("".equals(StringUtil.checkNull(status))){
				status="1";
			}else {
				status="0";
			   if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getAfterSaleOrderCode()))) {
					buff.append(" and after_sale_order_code like '%" + afterSaleBackSupplierProduct.getAfterSaleOrderCode()+"%'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getCode()))) {
				buff.append(" and code like '%" + afterSaleBackSupplierProduct.getCode()+"%'");
			}
		
			// 接收返回的处理单id 字符串
			if(buff.length()>2){
			    idStr = afStockService.getAfterSaleIds(buff.toString());
			    buff.delete(0, buff.length());
			    if(!"0".equals(idStr) && !"".equals(StringUtil.checkNull(idStr))){
			    	buff.append(" and absp.after_sale_detect_product_id in("+idStr+")");  
				}
			}
			if("0".equals(status)){
				if(!"".equals(StringUtil.checkNull(areaId))) {
					   buff.append(" and asdp.area_id = " + areaId);
				}
			} else {
				if(!"".equals(StringUtil.checkNull(areaId))) {
					   buff.append(" and ap.area_id = " + areaId);
				}
			}
			String content=request.getParameter("content");
			if(!"".equals(StringUtil.checkNull(content))) {
				buff.append(" and p.code = '" + content+"'");
			}
			String parentId1=request.getParameter("parentId1");
			String parentId2=request.getParameter("parentId2");
			String parentId3=request.getParameter("parentId3");
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and return_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and return_datetime<='" + endTime + " 23:59:59'");
			}
			if (!"".equals(StringUtil.checkNull(startTime_send))) {
				buff.append(" and absp.create_datetime >= '" + startTime_send + " 00:00:00'");
			}
			if (!"".equals(StringUtil.checkNull(endTime_send))) {
				buff.append(" and absp.create_datetime <= '" + endTime_send + " 23:59:59'");
			}
			
			int totalCount = afStockService.getSupplierProductCount(buff.toString(),status);
			datagrid.setTotal((long)totalCount);
			List<AfterSaleBackSupplierProduct> maps = afStockService.getBackSupplierProductList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " return_datetime desc ",status);
			
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 已检测商品上架任务列表
	 * hepeng
	 * @return 4
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getProductUpshelfgrid")
	@ResponseBody
	public EasyuiDataGridJson getProductUpshelfgrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage, 
			AfterSaleBackSupplierProduct afterSaleBackSupplierProduct,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			
			String startTime=request.getParameter("startTime");
			
			String endTime=request.getParameter("endTime");
	
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(afterSaleBackSupplierProduct.getCode()))) {
				buff.append(" and ap.code like '%" + afterSaleBackSupplierProduct.getCode()+"%'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and ap.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and ap.create_datetime<='" + endTime + " 23:59:59'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id = " + areaId);
			}

			String content=request.getParameter("content");
			if(!"".equals(StringUtil.checkNull(content))) {
				buff.append(" and p.code = '" + content+"'");
			}

			int totalCount = afStockService.getProductUpShelfCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleDetectUpShelfBean> maps = afStockService.getProductUpShelfList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " ap.create_datetime desc");
			
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 查询封箱列表
	 *@author 李宁
	 *@date 2014-2-11 下午4:26:09
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getAfterSaleSealList")
	@ResponseBody
	public Object getAfterSaleSealList(HttpServletRequest request,
			HttpServletResponse response,EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String begin = StringUtil.dealParam(request.getParameter("startDate"));
		String end = StringUtil.dealParam(request.getParameter("endDate"));
		String afterSaleOrderCode = StringUtil.convertNull(request.getParameter("afterSaleOrderCode"));
		String afterSaleDetectCode = StringUtil.convertNull(request.getParameter("afterSaleDetectCode"));
		String afterSaleSealCode = StringUtil.convertNull(request.getParameter("afterSaleSealCode"));
		String areaId = StringUtil.checkNull(request.getParameter("areaId"));
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try{
			StringBuilder condition = new StringBuilder();
			if(!(StringUtil.isNull(begin) || StringUtil.isNull(end))){
				begin = begin + " 00:00:00";
				end = end + " 23:59:59";
				condition.append(" and ass.create_datetime between '").append(begin).append("' and '")
							.append(end).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleSealCode))){
				condition.append(" and ass.code='").append(afterSaleSealCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleDetectCode))){
				condition.append(" and asdp.code='").append(afterSaleDetectCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleOrderCode))){
				condition.append(" and asdp.after_sale_order_code='").append(afterSaleOrderCode).append("' ");
			}
			if(!(StringUtil.isNull(areaId))){
				condition.append(" and asdp.area_id=").append(areaId);
			}
			
			int totalCount = afStockService.getSealCount(condition.toString());
			resultMap.put("total",totalCount+"");
			
			List<AfterSaleSeal> sealList = afStockService.getAfterSaleSealList(condition.toString(), "ass.id",(easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			if(sealList!=null && sealList.size()>0){
				for(int i=0;i<sealList.size();i++){
					AfterSaleSeal sealBean = sealList.get(i);
					map = new HashMap<String,String>();
					map.put("seal_id", sealBean.getId()+"");
					map.put("seal_date", sealBean.getCreateDatetime());
					map.put("seal_code", sealBean.getCode());
					map.put("seal_product_count", sealBean.getSealProductCount()+"");
					map.put("operator", sealBean.getUserName());
					lists.add(map);
				}
			}
			resultMap.put("rows", lists);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 查询封箱商品清单
	 *@author 李宁
	 *@date 2014-2-12 下午2:34:48
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/querySealInfo")
	@ResponseBody
	public EasyuiDataGridJson querySealInfo(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			int sealId = StringUtil.StringToId(request.getParameter("id"));
			if(sealId>0){
				String query = "SELECT p.`name`,p.oriname,asdp.IMEI,asdp.after_sale_order_code,assp.after_sale_order_status," +
						"asdp.code,assp.after_sale_order_detect_product_status " +
						"from `after_sale_seal_product` assp left join product p on assp.product_id=p.id " +
						"left join after_sale_detect_product asdp on asdp.id = assp.after_sale_detect_product_id ";
				List<AfterSaleSealProduct> products = afStockService.getAfterSaleSealProducts(query," assp.status in(1,2) and assp.after_sale_seal_id=" + sealId,"assp.after_sale_detect_product_id");
				datagrid.setTotal((long)products.size());
				datagrid.setRows(products);
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return datagrid;
	}
	/**
	 * 查询待解封商品列表
	 *@author 李宁
	 *@date 2014-2-11 下午4:26:09
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getAfterSaleSealProductList")
	@ResponseBody
	public Object getAfterSaleSealProductList(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiPage,String areaId) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String begin = StringUtil.dealParam(request.getParameter("startDate"));
		String end = StringUtil.dealParam(request.getParameter("endDate"));
		String afterSaleOrderCode = StringUtil.convertNull(request.getParameter("afterSaleOrderCode"));
		String afterSaleDetectCode = StringUtil.convertNull(request.getParameter("afterSaleDetectCode"));
		String afterSaleSealCode = StringUtil.convertNull(request.getParameter("afterSaleSealCode"));
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		resultMap.put("total","0");
		resultMap.put("rows",lists);
		try{
			StringBuilder condition = new StringBuilder();
			if(!(StringUtil.isNull(begin) || StringUtil.isNull(end))){
				begin = begin + " 00:00:00";
				end = end + " 23:59:59";
				condition.append(" and ass.create_datetime between '").append(begin).append("' and '")
							.append(end).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleSealCode))){
				condition.append(" and ass.code='").append(afterSaleSealCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleDetectCode))){
				condition.append(" and afdp.code='").append(afterSaleDetectCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleOrderCode))){
				condition.append(" and afdp.after_sale_order_code='").append(afterSaleOrderCode).append("' ");
			}
			if(!(StringUtil.isNull(areaId))){
				condition.append(" and afdp.area_id=").append(areaId);
			}
			int totalCount = afStockService.getAfterSaleSealProductsCount(condition.toString());
			resultMap.put("total",totalCount+"");
			lists = afStockService.getAfterSaleSealProductsList(condition.toString(),(easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			if(lists!=null && lists.size()>0){
				resultMap.put("rows", lists);
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 批量解封商品
	 *@author 李宁
	 *@date 2014-2-14 下午6:09:27
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/batchReopened")
	public void batchReopened(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(lock){
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
			try{
				afStockService.getDbOp().startTransaction();
				String sealProductIds = StringUtil.convertNull(request.getParameter("sealProductIds"));
				String[] sealProductIdList = sealProductIds.split(",");
				Set<Integer> sealSet = new HashSet<Integer>();
				for(int i=0;i<sealProductIdList.length;i++){
					String sealProductId = sealProductIdList[i];
					//status==3是已解封状态
					if(!afStockService.updateAfterSaleSealProduct("status=3", "id="+sealProductId)){
						afStockService.getDbOp().rollbackTransaction();
						result.append("{result:'failure',tip:'解封商品操作失败，数据库更新失败!'}");
						response.getWriter().write(result.toString());
						return;
					}
					AfterSaleSealProduct sealProduct = afStockService.getAfterSaleSealProduct("id="+sealProductId);
					//物流处理单状态改为封箱前的状态
					int detectStatus = sealProduct.getAfterSaleOrderDetectProductStatus();
					if(!afStockService.updateAfterSaleDetectProduct("status=" + detectStatus, "id=" + sealProduct.getAfterSaleDetectProductId())){
						afStockService.getDbOp().rollbackTransaction();
						result.append("{result:'failure',tip:'更新物流处理单状态失败!'}");
						response.getWriter().write(result.toString());
						return;
					}
					
					AfterSaleWarehourceProductRecordsBean bean = afStockService.getAfterSaleWarehourceProductRecord("id=" + sealProduct.getAfterSaleDetectProductId());
					if(bean!=null){
						//销售处理单状态不变，is_vanning改为0，未装箱
						StringBuffer updateSql = new StringBuffer();
						updateSql.append(" UPDATE after_sale_warehource_product_records ");
						updateSql.append(" SET is_vanning = 0 , ");
						updateSql.append(" modify_user_id = ");
						updateSql.append(user.getId());
						updateSql.append(" ,  modify_user_name = '");
						updateSql.append(StringUtil.dealParam(user.getUsername()));
						updateSql.append("' , modify_datetime = '");
						updateSql.append(DateUtil.getNow());
						updateSql.append("' WHERE id = '");
						updateSql.append(sealProduct.getAfterSaleDetectProductId());
						updateSql.append("' ");
						if(!dbOp.executeUpdate(updateSql.toString())){
							afStockService.getDbOp().rollbackTransaction();
							result.append("{result:'failure',tip:'更新销售处理单失败!'}");
							response.getWriter().write(result.toString());
							return;
						}
					}else{
						dbOp.rollbackTransaction();
						result.append("{result:'failure',tip:'没有id为"+ sealProduct.getAfterSaleDetectProductId()  +"的销售处理单!'}");
						response.getWriter().write(result.toString());
						return;
					}
					
					int afterSaleSealId = sealProduct.getAfterSaleSealId();
					//查询解封商品对应的封箱单中剩余封箱状态下产品的数量
					int count = afStockService.getAfterSaleSealProductCount("after_sale_seal_id=" + afterSaleSealId +" and status in (1,2)");
					if(count<=0){
						//封箱单中没有处于封箱状态的产品，需要将封箱清单状态变为已废弃
						if(!afStockService.updateAfterSaleSeal("status=1", "id=" + afterSaleSealId)){
							afStockService.getDbOp().rollbackTransaction();
							result.append("{result:'failure',tip:'更新封箱单失败!'}");
							response.getWriter().write(result.toString());
							return;
						}
					}
					sealSet.add(afterSaleSealId);
					//售后仓内作业日志
					AfterSaleDetectProductBean detect =  afStockService.getAfterSaleDetectProduct("id=" + sealProduct.getAfterSaleDetectProductId());
					if(detect == null){
						afStockService.getDbOp().rollbackTransaction();
						result.append("{result:'failure',tip:'处理单不存在" + sealProduct.getAfterSaleDetectProductId() + "(ID)!'}");
						response.getWriter().write(result.toString());
						return;
					}
					String content = "解封售后处理单[" + detect.getCode() + "]的商品";
					if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE15,detect.getCode(),null)){
						afStockService.getDbOp().rollbackTransaction();
						result.append("{result:'failure',tip:'处理单不存在" + sealProduct.getAfterSaleDetectProductId() + "(ID)!'}");
						response.getWriter().write(result.toString());
						return;
					}
				}
				afStockService.getDbOp().commitTransaction();
				StringBuilder sealIds = new StringBuilder();
				for(Integer id : sealSet){
					sealIds.append(id).append(",");
				}
				sealIds = sealIds.deleteCharAt(sealIds.length()-1);
				result.append("{result:'success',sealIds:'").append(sealIds).append("'}");
				response.getWriter().write(result.toString());
				return;
			}catch (Exception e) {
				System.out.print(DateUtil.getNow());e.printStackTrace();
				afStockService.getDbOp().rollbackTransaction();
			}finally{
				afStockService.releaseAll();
			}
		}
	}
	/**
	 * 通过售后处理单号添加商品到装箱清单
	 *@author 李宁
	 *@date 2014-2-15 下午5:44:50
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/seal")
	public String seal(HttpServletRequest request,HttpServletResponse response)throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String detectCodes = StringUtil.dealParam(request.getParameter("detectCode"));
		//存放售后处理单号
		Set<String> detetctCodeSet = (Set<String>) request.getSession().getAttribute("detectCodes");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			if(detectCodes==null || detectCodes.equals("")){
				request.setAttribute("tip", "没有输入售后处理单号,请输入!");
				return "/admin/afStock/addAfterSaleSeal";
			}else{
				//售后处理单号
				String code = null;
				BufferedReader br = new BufferedReader(new StringReader(detectCodes));
				List<AfterSaleSealProduct> totalProductList = new ArrayList<AfterSaleSealProduct>();
				while ((code = br.readLine()) != null) {
					if(code.trim().length()>0){
						//处理单需要在after_sale_seal_product中查询对应信息且after_sale_seal_id=0
						AfterSaleDetectProductBean detectProduct = afStockService.getAfterSaleDetectProduct("code='"+code+"'");
						if(detectProduct!=null){
							AfterSaleSealProduct sealProduct = afStockService.getAfterSaleSealProduct("after_sale_detect_product_id=" + detectProduct.getId());
							if(sealProduct!=null){
								if(sealProduct.getAfterSaleSealId() != 0){
									request.setAttribute("tip", "此售后处理单" + code + "已经进行了封箱操作!");
								}else{
									if(detetctCodeSet!=null && detetctCodeSet.size()>0){
										if(detetctCodeSet.contains(code)){
											request.setAttribute("tip", "已经添加过此单号了!!");
										}else{
											detetctCodeSet.add(code);
										}
									}else{
										detetctCodeSet = new HashSet<String>();
										detetctCodeSet.add(code);
									}
								}
							}else{
								request.setAttribute("tip", "此售后处理单" + code + "不在封箱商品列表中!");
							}
						}else{
							request.setAttribute("tip", "此售后处理单" + code + "不存在!");
						}
					}
				}
				for(String detectCode : detetctCodeSet){
					String query = "select p.name,p.oriname,asdp.imei,asdp.after_sale_order_code,aso.status,asdp.code,asdp.status,asdp.id,asdp.product_id " +
							" from after_sale_detect_product asdp left join  product p on asdp.product_id=p.id " +
							"left join after_sale_order aso on asdp.after_sale_order_id=aso.id";
					List<AfterSaleSealProduct> products = afStockService.getAfterSaleSealProducts(query," asdp.code='" + detectCode + "'",null);
					if(products!=null && products.size()>0){
						totalProductList.addAll(products);
					}
				}
				request.setAttribute("productList", totalProductList);
				request.setAttribute("operator", user.getUsername());
				request.setAttribute("sealDate", DateUtil.getNowDateStr());
				request.getSession().setAttribute("detectCodes", detetctCodeSet);
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return "/admin/afStock/addAfterSaleSeal";
	}
	
	/**
	 * 添加封箱商品清单并打印清单
	 *@author 李宁
	 *@date 2014-2-17 下午4:03:28
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/addSealInventory")
	public String addSealInventory(HttpServletRequest request,HttpServletResponse response)throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String result = "/admin/afStock/addAfterSaleSeal";
		Set<String> detectCodeSet = (Set<String>) request.getSession().getAttribute("detectCodes");
		if(detectCodeSet==null){
			request.setAttribute("tip", "请输入售后处理单号!");
			return result;
		}
		request.getSession().setAttribute("detectCodes", null);//清空session里的处理单号
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
			IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
			
			List resultList = new ArrayList();//存放打印结果
			Map item = new HashMap();//存放打印的封箱商品清单
			try{
				if(detectCodeSet!=null&& detectCodeSet.size()>0){
					afStockService.getDbOp().startTransaction();
					//增加封箱单
					AfterSaleSeal sealBean = new AfterSaleSeal();
					sealBean.setCreateDatetime(DateUtil.getNow());
					sealBean.setStatus(0);//已封箱
					sealBean.setUserId(user.getId());
					sealBean.setUserName(user.getUsername());
					
					//封箱单号规则：F+8位时间+4位序号
					SimpleDateFormat sdfc = new SimpleDateFormat("yyyyMMdd");
					Calendar cal = Calendar.getInstance();
					String sealCode = "F"+sdfc.format(cal.getTime());
					List sealList = afStockService.getAfterSaleSealList(" code like '"+sealCode+"%' ", 0, 1, " id desc");
					if(sealList==null||sealList.size()==0){
						sealCode+="0001";
					}else{
						String _code = ((AfterSaleSeal)(sealList.get(0))).getCode();
						int number = Integer.parseInt(_code.substring(_code.length()-4));
						number++;
						sealCode += String.format("%04d",new Object[]{new Integer(number)});
					}
					
					sealBean.setCode(sealCode);
					if(!afStockService.addAfterSaleSeal(sealBean)){
						request.setAttribute("tip", "数据库操作失败:添加封箱单失败!");
						return result;
					}
					int sealId = afStockService.getDbOp().getLastInsertId();//获得封箱清单的id
					item.put("afterSaleSeal", sealBean);
					List<AfterSaleSealProduct> totalProductList = new ArrayList<AfterSaleSealProduct>();//页面打印的商品列表
				
					for(String detectCode : detectCodeSet){
						AfterSaleDetectProductBean product = afStockService.getAfterSaleDetectProduct(" code = '" + detectCode + "' ");
						if (product == null) {
							dbOp.rollbackTransaction();
							request.setAttribute("tip", "未查询到售后处理单[" + detectCode + "]");
							return result;
						}
						AfterSaleOrderBean order = afterSaleService.getAfterSaleOrder(" id = " + product.getAfterSaleOrderId());
						if (order == null) {
							dbOp.rollbackTransaction();
							request.setAttribute("tip", "未查询到售后处理单[" + detectCode + "]所关联的售后单");
							return result;
						}
						// 13，封箱已完成 更新售后处理单状态
						if (!afStockService.updateAfterSaleDetectProduct(" status = " + 13, " id = " + product.getId())) {
							dbOp.rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败:updateAfterSaleDetectProduct");
							return result;
						}
						
						//销售处理单状态不变，is_vanning改为2，已装箱。
						StringBuffer updateSql = new StringBuffer();
						updateSql.append(" UPDATE after_sale_warehource_product_records ");
						updateSql.append(" SET is_vanning = 2 , ");
						updateSql.append(" modify_user_id = ");
						updateSql.append(user.getId());
						updateSql.append(" ,  modify_user_name = '");
						updateSql.append(StringUtil.dealParam(user.getUsername()));
						updateSql.append("' , modify_datetime = '");
						updateSql.append(DateUtil.getNow());

						updateSql.append("' WHERE code = '");
						updateSql.append(product.getCode());
						updateSql.append("' ");
						if(!dbOp.executeUpdate(updateSql.toString())){
							dbOp.rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败:更新销售处理单失败!");
							return result;
						}
						String query = "select p.name,p.oriname,asdp.imei,asdp.after_sale_order_code,aso.status,asdp.code,asdp.status,asdp.id,asdp.product_id,assp.id " +
								" from after_sale_detect_product asdp left join after_sale_seal_product assp on asdp.id=assp.after_sale_detect_product_id" +
								" left join  product p on asdp.product_id=p.id " +
								"left join after_sale_order aso on asdp.after_sale_order_id=aso.id";
						
						List<AfterSaleSealProduct> products = afStockService.getAfterSaleSealProducts(query," asdp.code='" + detectCode + "'",null);
						if(products!=null && products.size()>0){
							AfterSaleSealProduct sealProduct = products.get(0);
							//更新封箱商品的after_sale_seal_id和status
							if(!afStockService.updateAfterSaleSealProduct("after_sale_seal_id=" + sealId + ",status=1", "id=" + sealProduct.getId())){
								request.setAttribute("tip", "数据库操作失败:更新封箱商品失败!");
								return "/admin/afStock/addAfterSaleSeal";
							}
							totalProductList.addAll(products);
						}
						String content = "封箱售后处理单[" + detectCode + "]的商品，封箱单号【" + sealBean.getCode() + "】";
						if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE15,detectCode,null)){
							dbOp.rollbackTransaction();
							request.setAttribute("tip", "添加售后日志失败!");
							return result;
						}
					}
					afStockService.getDbOp().commitTransaction();
					item.put("afterSaleSealProductList", totalProductList);
					resultList.add(item);
					request.setAttribute("resultList", resultList);
				}
			}catch (Exception e) {
				System.out.print(DateUtil.getNow());e.printStackTrace();
				afStockService.getDbOp().rollbackTransaction();
			}finally{
				afStockService.releaseAll();
			}
		}
		return "/admin/afStock/afterSaleSealInventoryPrint";
	}

	/**
	 * 打印封装清单商品信息
	 *@author 李宁
	 *@date 2014-2-18 上午11:10:58
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/printSealInventory")
	public String printSealInventory(HttpServletRequest request,HttpServletResponse response)throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String sealIds = StringUtil.convertNull(request.getParameter("sealIds"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		List resultList = new ArrayList();//存放打印结果
		try{
			if(sealIds!=null && sealIds.length()>0){
				String[] sealArray = sealIds.split(",");
				for(int i=0;i<sealArray.length;i++){
					int id = StringUtil.StringToId(sealArray[i]);
					if(id>0){
						Map map = new HashMap();//存放封箱清单和封箱商品列表
						AfterSaleSeal sealBean = afStockService.getAfterSaleSeal("id=" + sealArray[i]);
						String query = "SELECT p.`name`,p.oriname,asdp.IMEI,asdp.after_sale_order_code,assp.after_sale_order_status," +
								"asdp.code,assp.after_sale_order_detect_product_status " +
								"from `after_sale_seal_product` assp left join product p on assp.product_id=p.id " +
								"left join after_sale_detect_product asdp on asdp.id = assp.after_sale_detect_product_id ";
						List<AfterSaleSealProduct> products = afStockService.getAfterSaleSealProducts(query," assp.status in (1,2) and assp.after_sale_seal_id=" + id,"assp.id");
						if(products!=null && products.size()>0){
							map.put("afterSaleSeal", sealBean);
							map.put("afterSaleSealProductList", products);
							resultList.add(map);
						}
					}
				}
				request.setAttribute("resultList", resultList);
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return "/admin/afStock/afterSaleSealInventoryPrint";
	}
	
	/**
	 * 根据售后处理单号、备用机号获取用户的手机号和地址
	 * @author 李宁  syuf 修改
	 * @date 2014-2-19 下午2:32:43
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/getUserPhoneAddress")
	@ResponseBody
	public Json getUserPhoneAddress(HttpServletRequest request,HttpServletResponse response) throws IOException{
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录，请登录后操作!");
			return j;
		}
		String code = StringUtil.dealParam(request.getParameter("detectCode"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_METHOD, dbOp);
		Map<String,String> map = new HashMap<String, String>();
		boolean flag = false;//是否是备用机号
		try{
			String query = "select aso.customer_name,aso.customer_phone,aso.customer_address,aso.id,aso.customer_post_code " +
					"from after_sale_detect_product asdp " +
					"left join after_sale_order aso on asdp.after_sale_order_id=aso.id " +
					"where asdp.code='" + code + "'";
			ResultSet rs = dbOp.executeQuery(query);
			if(rs!=null){
				if(rs.next()){
					String phoneNumber = rs.getString(2);
					if(phoneNumber != null && phoneNumber.length() > 10){
						phoneNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, 11);
					}
					map.put("name", rs.getString(1));
					map.put("phone", phoneNumber);
					map.put("userPhone", rs.getString(2));
					map.put("address", rs.getString(3));
					map.put("afterSaleId", rs.getInt(4) + "");
					map.put("postCode", rs.getString(5));
				}else{
					//联查换新机记录
					query = "select aso.customer_name,aso.customer_phone,aso.customer_address,aso.id,aso.customer_post_code,asdp.code " +
							"from after_sale_replace_new_product_record asrnpr " +
							"join after_sale_detect_product asdp on asrnpr.after_sale_detect_product_id=asdp.id " +
							"join after_sale_order aso on asdp.after_sale_order_id=aso.id " +
							"where asrnpr.spare_code='" + code + "'";
					rs = dbOp.executeQuery(query);
					if(rs!=null){
						if(rs.next()){
							String phoneNumber = rs.getString(2);
							if(phoneNumber != null && phoneNumber.length() > 10){
								phoneNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, 11);
							}
							map.put("name", rs.getString(1));
							map.put("phone", phoneNumber);
							map.put("userPhone", rs.getString(2));
							map.put("address", rs.getString(3));
							map.put("afterSaleId", rs.getInt(4) + "");
							map.put("postCode", rs.getString(5));
							map.put("detectCode", rs.getString(6));
							flag = true;
						}
					}
				}
			}
			if(map.size() == 0){
				j.setMsg("处理单号不正确!");
				return j;
			}
			//获取同一售后单下所有待寄回用户处理单号
			List<Map<String,String>> list = afService.getBackUserPackageCodeOfAfterSaleOrder("aso.id=" + map.get("afterSaleId"));
			String msg = null;
			StringBuffer buff = new StringBuffer();
			StringBuffer sb = new StringBuffer();
			//售后处理单号
			String detectCode = code;
			if(flag){
				detectCode = map.get("detectCode");
			}
			buff.append(code);
			for(Map<String,String> m : list){
				if(!detectCode.equals(m.get("detectCode"))){
					sb.append(m.get("detectCode"));
					sb.append(",");
					buff.append("\n");
					buff.append(m.get("detectCode"));
				}
			}
			if(sb.length() > 0){
				msg = "在待寄回用户商品列表中还有" + sb.toString() + "为同一售后单/订单商品，本次是否要一同寄出？";
				map.put("detectCodeAll", buff.toString());
			}
			j.setSuccess(true);
			j.setMsg(msg);
			j.setObj(map);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 添加原品&维修商品包裹返回信息
	 * @author 李宁  update syuf
	 * @date 2014-2-18 下午5:46:14
	 * @param request
	 * @param bean
	 * @return
	 */
	@RequestMapping("/addOriginalOrRepairBackPackage")
	@ResponseBody
	public Json addOriginalOrRepairBackPackage(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_METHOD, dbOp);
		
		String detectCodes = StringUtil.dealParam(request.getParameter("detectCodes"));
		int deliverId = StringUtil.StringToId(request.getParameter("deliverId"));
		float freight = StringUtil.toFloat(request.getParameter("freight"));
		String customerName = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("customerName")));
		String phoneNumber = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("userPhone")));
		String address = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("userAddress")));
		String remark = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("remark")));
		String postCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("postCode")));
		try {
			float weight=0;
			String tempWeight=request.getParameter("weight");
			if(tempWeight!=null && tempWeight.endsWith("kg")){
				tempWeight = tempWeight.substring(0, tempWeight.length()-2).trim();
			}
			String weightReg="^\\s+\\d+\\s+\\d{1,5}\\.\\d{1,3}$";//电子秤输出格式
			String weightReg2="^\\d{1,5}\\.\\d{1,3}$";//人工输出格式
			
			if(tempWeight.matches(weightReg)){//电子秤输出格式
				String weightReg3="\\s+\\d+\\s+";
				if(tempWeight.split(weightReg3).length<2){
	        		j.setMsg("重量格式错误!");
	        		return j;
				}else{
					weight=StringUtil.toFloat(tempWeight.split(weightReg3)[1]);
				}
			}else if (tempWeight.matches(weightReg2)){//人工输出格式
				weight=StringUtil.toFloat(tempWeight);
			}else{
				j.setMsg("重量格式错误!");
        		return j;
			}
			
			String[] codeArray = detectCodes.split("\r\n");
			List<String> codeList = new ArrayList<String>();
			for(int i=0;i<codeArray.length;i++){
				if(codeArray[i]!=null && !codeArray[i].equals("") && codeArray[i].length()>0){
					codeList.add(codeArray[i]);
				}
			} 
			AfterSaleDetectProductBean detect = afService.getAfterSaleDetectProduct("code='" + codeList.get(0) + "'");
			if(detect == null){
				detect = afService.getDetectProductBySpareCode(codeList.get(0));
				if(detect==null){
					j.setMsg("处理单号[" + codeList.get(0) + "]不存在!");
					return j;
				}
			}
			AfterSaleOrderBean asoBean = afService.getAfterSaleOrder(" id = " + detect.getAfterSaleOrderId());
			if(asoBean == null){
				j.setMsg("处理单[" + codeList.get(0) + "]所属售后单不存在!");
				return j;
			}
			String result = afService.addOriginalOrRepairBackPackage(codeList, freight, deliverId, phoneNumber, weight,address, customerName, user,remark);
			if(!result.startsWith("#")){
				j.setMsg(result);
				return j;
			}
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("packageCode", result.split("#")[1]);
			map.put("postCode",postCode);
			j.setSuccess(true);
			j.setObj(map);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 添加返厂产品之前所做的验证、查询商品列表功能
	 *@author 李宁
	 *@date 2014-2-20 下午2:48:47
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/preAddbackSupplierProduct")
	public String preAddbackSupplierProduct(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String detectCodes = StringUtil.dealParam(request.getParameter("detectCodes"));
		//标志是否是删除操作
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		Set<String> detectCodeSet = (Set<String>) request.getSession().getAttribute("backSupplierProductDetectCodesSet");
		Integer value = (Integer)request.getSession().getAttribute("stockType");
		if(value==null){
			value = new Integer(-1);
		}
		int stockType = value.intValue();
		Integer areaValue = (Integer)request.getSession().getAttribute("areaId");
		if(areaValue==null){
			areaValue = new Integer(-1);
		}
		int areaId = areaValue.intValue();
		String result = "/admin/afStock/addBackSupplierProduct";
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			//返厂商品明细列表
			List<AfterSaleBackRepairProductDetail> productDetailList = new ArrayList<AfterSaleBackRepairProductDetail>();
			if(detectCodes==null || detectCodes.equals("")){
				request.setAttribute("tip", "没有输入售后处理单号,请输入!");
				return result;
			}else{
				if("delete".equals(flag)){
					if(detectCodeSet!=null && detectCodeSet.size()>0){
						if(detectCodeSet.contains(detectCodes)){
							detectCodeSet.remove(detectCodes);
						}else{
							request.setAttribute("tip", "没有此售后处理单号!");
							return result;
						}
					}else{
						request.setAttribute("tip", "返厂清单中还没有添加商品!");
						return result;
					}
				}else{
					String code = null;//售后处理单号
					BufferedReader br = new BufferedReader(new StringReader(detectCodes));
					while ((code = br.readLine()) != null) {
						if(code.trim().length()>0){
							AfterSaleDetectProductBean bean = afStockService.getAfterSaleDetectProduct("code='"+code+"'");
							if(bean != null){
								if(!afStockService.checkAfterSaleUserGroup(user, bean.getAreaId())){
									request.setAttribute("tip", "没有该处理单[" + bean.getCode() + "]所属地区的售后仓内作业权限!");
									return result;
								}
								if (bean.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
									request.setAttribute("tip", "此售后处理单"+code+"已锁定!");
									return result;
								} else {
									CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + bean.getCargoWholeCode() + "' ");
									if (cargoInfo == null) {
										request.setAttribute("tip", "未查询到售后处理单[" + code + "]所对应的货位信息");
										return result;
									}
									if (stockType == -1) {
										stockType = cargoInfo.getStockType();
										request.getSession().setAttribute("stockType", stockType);
									} else {
										if (stockType != cargoInfo.getStockType()) {
											request.setAttribute("tip", "不同库类型中商品不可以添加到同一张返厂清单中");
											return result;
										}
									}
									if(areaId == -1){
										areaId = bean.getAreaId();
										request.getSession().setAttribute("areaId", areaId);
									}else{
										if(bean.getAreaId()!=areaId){
											request.setAttribute("tip", "不同库地区中商品不可以添加到同一张返厂清单中");
											return result;
										}
									}
									//获取最近一条返厂商品记录
									List<AfterSaleBackSupplierProduct> backSupplierProductList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + bean.getId(),-1,1,"id desc");
									AfterSaleBackSupplierProduct backSupplierProduct = null;
									if(backSupplierProductList!=null &&backSupplierProductList.size()==1){
										backSupplierProduct = backSupplierProductList.get(0);
									}
									//验证是否是售后商品--售后商品也可返厂 2014-05-12 李宁
									if(cargoInfo.getStockType() == CargoInfoBean.STOCKTYPE_AFTER_SALE){
										if(backSupplierProduct != null && backSupplierProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS0){
											request.setAttribute("tip", "售后处理单[" + code + "]已返厂");
											return result;
										}
										if(detectCodeSet!=null){
											if(detectCodeSet.contains(code)){
												request.setAttribute("tip", "此售后处理单"+code+"已经添加过了!");
												return result;
											}else{
												detectCodeSet.add(code);
											}
										}else{
											detectCodeSet = new LinkedHashSet<String>();
											detectCodeSet.add(code);
										}
									}else{
										//可以扫描的处理单应该是After_sale_back_supplier_product表中已经存在且状态是等待返厂的处理单
										if(backSupplierProduct != null && backSupplierProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS3){
											if(detectCodeSet!=null){
												if(detectCodeSet.contains(code)){
													request.setAttribute("tip", "此售后处理单"+code+"已经添加过了!");
													return result;
												}else{
													detectCodeSet.add(code);
												}
											}else{
												detectCodeSet = new LinkedHashSet<String>();
												detectCodeSet.add(code);
											}
										}else{
											request.setAttribute("tip", "此售后处理单"+code+"不是处于等待返厂的处理单!");
											return result;
										}
									}
								}
							}else{
								request.setAttribute("tip", "此售后处理单"+code+"不存在!");
								return result;
							}
						}
					}
				}

				//判断是否正在盘点
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK 
						+ " and stock_area=" + areaId + " and stock_type=" + stockType);
				if (afterSaleInventory != null) {
					request.setAttribute("tip", "存在尚未完成的盘点单，请先盘点!");
					return result;
				}
				//将输入的售后处理单号缓存到session中
				request.getSession().setAttribute("backSupplierProductDetectCodesSet", detectCodeSet);
				for(String detectCode : detectCodeSet){
					AfterSaleDetectProductBean bean = afStockService.getAfterSaleDetectProduct("code='"+detectCode+"'");
					AfterSaleBackRepairProductDetail productDetailBean = new AfterSaleBackRepairProductDetail();
					productDetailBean.setImei(bean.getIMEI());
					productDetailBean.setDetectCode(bean.getCode());
					productDetailBean.setAfterSaleDetectProductId(bean.getId());
					productDetailBean.setProductId(bean.getProductId());
					
					int count = afStockService.getAfterSaleBackSupplierProductCount("after_sale_detect_product_id=" + bean.getId() +" and status!=3");
					if(count>1){
						productDetailBean.setFirstRepair(1);//返修商品
					}else{
						productDetailBean.setFirstRepair(0);//第一次维修
					}
					ResultSet rs = dbOp.executeQuery("select name,oriname from product where id=" + productDetailBean.getProductId());
					if(rs!=null && rs.next()){
						productDetailBean.setProductName(rs.getString(1));
						productDetailBean.setProductOriname(rs.getString(2));
					}
					rs.close();
					
					//故障代码 2015-03-17 
					String faultCode = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_CODE, productDetailBean.getAfterSaleDetectProductId());
					productDetailBean.setFaultCode(faultCode);
					
					////故障描述 2015-03-17 修改为 多级显示
					String faultDescript = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, productDetailBean.getAfterSaleDetectProductId());
					productDetailBean.setFaultDescript(faultDescript);
					
					//申报状态 2015-03-17
					String reportStatus = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.REPORT_STATUS, productDetailBean.getAfterSaleDetectProductId());
					productDetailBean.setReportStatus(reportStatus);
					
					rs = dbOp.executeQuery("select ci.stock_type from after_sale_detect_product asdp inner join cargo_info ci " +
							"on asdp.cargo_whole_code=ci.whole_code where asdp.id=" + productDetailBean.getAfterSaleDetectProductId());
					if(rs!=null && rs.next()){
						productDetailBean.setBackRepairProductType(rs.getInt(1));
					}
					rs.close();
					productDetailList.add(productDetailBean);
				}
				request.setAttribute("productDetailList", productDetailList);
				request.setAttribute("totalCount", productDetailList.size());
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return result;
	}
	
	/**
	 * 添加返厂商品
	 *@author 李宁
	 *@date 2014-2-21 下午4:16:35
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/addbackSupplierProduct")
	@ResponseBody
	public Json addbackSupplierProduct(HttpServletRequest request,HttpServletResponse response,
			int backSupplierId,String contract,String packageCode,String remark,
			String contractPhone,String deliveryAddress,String zipCode) throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		
		Set<String> detectCodesSet = (Set<String>) request.getSession().getAttribute("backSupplierProductDetectCodesSet");
		if(detectCodesSet==null){
			j.setMsg("请输入售后处理单号!");
			return j;
		}
		
		//保存送修清单的内容
		BackRepairListBean repairListBean = new BackRepairListBean();
		repairListBean.setContract(contract);
		repairListBean.setPackageCode(packageCode);
		repairListBean.setRemark(remark);
		repairListBean.setContractPhone(contractPhone);
		repairListBean.setDeliveryAddress(deliveryAddress);
		repairListBean.setZipCode(zipCode);
		repairListBean.setShipDate(DateUtil.getNow().substring(0, 10));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IAfterSalesService iService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,db);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			synchronized (lock) {
				Set<String> userPhoneSet = new HashSet<String>();
				AfterSaleBackSupplier supplier = afStockService.getAfterSaleBackSupplier("id=" + backSupplierId);
				if (supplier == null) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("返厂厂家不存在！");
					return j;
				}
				repairListBean.setSupplierName(supplier.getName());
				afStockService.getDbOp().startTransaction();
				CommonLogic logic = new CommonLogic();
				for(String code : detectCodesSet){
					AfterSaleDetectProductBean bean = afStockService.getAfterSaleDetectProduct("code='"+code+"'");
					if(bean==null){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("不存在此售后处理单号"+ code +"的处理单！");
						return j;
					}
					voOrder order = null;
					if(bean.getAfterSaleOrderId()!=0){
						AfterSaleOrderBean afterSaleOrderBean = iService.getAfterSaleOrder("id=" + bean.getAfterSaleOrderId());
						if(afterSaleOrderBean==null){
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("此处理单" + code +"对应的售后单不存在!");
							return j;
						}
						order = adminService.getOrder(afterSaleOrderBean.getOrderId());
						if(order==null){
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("此处理单" + code +"对应的订单不存在!");
							return j;
						}
					}
					
					CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + bean.getCargoWholeCode() + "' ");
					
					//获取最近一条返厂商品记录
					List<AfterSaleBackSupplierProduct> backSupplierProductList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + bean.getId(),-1,1,"id desc");
					AfterSaleBackSupplierProduct backSupplierProduct = null;
					if(backSupplierProductList!=null &&backSupplierProductList.size()==1){
						backSupplierProduct = backSupplierProductList.get(0);
					}
					
					//手动输入的产品类型
					String oriname = StringUtil.convertNull(request.getParameter("oriname_" + bean.getId()));
					//售后处理单的IMEI
					String detectIMEI = bean.getIMEI();
					//获取手动输入的iemi码
					String imei = StringUtil.convertNull(request.getParameter("imei_" + bean.getId()));
					if (cargoInfo.getStockType() == CargoInfoBean.STOCKTYPE_AFTER_SALE) {
						AfterSaleBackSupplierProduct backProductBean = afStockService.getAfterSaleBackSupplierProduct("after_sale_detect_product_id=" + bean.getId() + " and status =" + AfterSaleBackSupplierProduct.STATUS0);
						if(backProductBean != null){
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("此处理单" + code +"已经存在状态[等待厂商寄回]记录!");
							return j;
						}
						// 售后库商品返厂增加返厂商品信息 2014-02-12 李宁
						backProductBean = new AfterSaleBackSupplierProduct();
						backProductBean.setAfterSaleDetectProductId(bean.getId());
						backProductBean.setProductId(bean.getProductId());
						backProductBean.setGuarantee(AfterSaleBackSupplierProduct.GUARANTEE0);
						backProductBean.setUserId(user.getId());
						backProductBean.setUserName(user.getUsername());
						backProductBean.setCreateDatetime(DateUtil.getNow());
						// 状态 已返厂
						backProductBean.setStatus(AfterSaleBackSupplierProduct.STATUS4);						
						backProductBean.setSupplierId(backSupplierId);
						backProductBean.setSenderId(user.getId());
						backProductBean.setSenderName(user.getUsername());
						backProductBean.setSendDatetime(DateUtil.getNow());
						backProductBean.setContract(contract);
						backProductBean.setPackageCode(packageCode);
						backProductBean.setDeliveryAddress(deliveryAddress);
						backProductBean.setZipCode(zipCode);
						backProductBean.setContractPhone(contractPhone);
						
						if(detectIMEI!=null && detectIMEI.length()>0){
							backProductBean.setIMEI(bean.getIMEI());
						}else{
							if(imei!=null && imei.trim().length()>0){
								if(IMEIUtil.imeiExist(imei)){
									afStockService.getDbOp().rollbackTransaction();
									j.setMsg("您输入的IMEI码"+ imei +"已经存在,请重新输入！");
									return j;
								}else{
									backProductBean.setIMEI(imei.trim());
								}
							}
						}
						
						if(oriname!=null && oriname.trim().length()>0){
							backProductBean.setProductOriname(oriname.trim());
						}
	
						if (!afStockService.addAfterSaleBackSupplierProduct(backProductBean)) {
							dbOp.rollbackTransaction();
							j.setMsg("添加返厂商品信息失败！");
							return j;
						}						
					}else{
						//售后处理单商品返厂
						StringBuilder set = new StringBuilder();
						String dateNow = DateUtil.getNow();
						set.append("supplier_id=").append(backSupplierId).append(",sender_id=").append(user.getId())
							.append(",sender_name='").append(user.getUsername()).append("',send_datetime='")
							.append(dateNow).append("',contract='").append(contract).append("',remark='")
							.append(remark).append("',package_code='").append(packageCode).append("',status=4,")
							.append("delivery_address='").append(deliveryAddress).append("',contract_phone='")
							.append(contractPhone).append("',zip_code='").append(zipCode).append("'");
						if(detectIMEI!=null && detectIMEI.length()>0){
							set.append(",imei='").append(detectIMEI).append("' ");
						}else{
							if(imei!=null && imei.trim().length()>0){
								if(IMEIUtil.imeiExist(imei)){
									afStockService.getDbOp().rollbackTransaction();
									j.setMsg("您输入的IMEI码"+ imei +"已经存在,请重新输入！");
									return j;
								}else{
									set.append(",imei='").append(imei.trim()).append("' ");
								}
							}
						}
						if(oriname!=null && oriname.trim().length()>0){
							set.append(",product_oriname='").append(oriname.trim()).append("'");
						}
						
						int count = afStockService.getAfterSaleBackSupplierProductCount("after_sale_detect_product_id=" + bean.getId() +" and status!=3");
						if(count>0){
							set.append(",first_repair=1");//返修
						}else{
							set.append(",first_repair=0");
						}
						
						if(backSupplierProduct!=null){
							if(!afStockService.updateAfterSaleBackSupplierProduct(set.toString(), "id=" + backSupplierProduct.getId())){
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("更新返厂商品失败！");
								return j;
							}
						}else{
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("此售后处理单" + bean.getCode() +"没有等待返厂的商品记录!");
							return j;
						}
						ResultSet rs = dbOp.executeQuery("select customer_phone from after_sale_order where id=" + bean.getAfterSaleOrderId());
						if(rs!=null && rs.next()){
							String phone = rs.getString(1);
							if(!userPhoneSet.contains(phone)){
								//发送短信
								TemplateMarker tm =TemplateMarker.getMarker();
								String content=tm.getOutString(TemplateMarker.BACK_SUPPLIER_MESSAGE_NAME, new HashMap());
								if(order.isDaqOrder()){
									SenderSMS3.send(user.getId(), phone , content,65);
								}else{
									SenderSMS3.send(user.getId(), phone , content);
								}
							}
						}
						rs.close();
					}
					// @mengqy 修改IMEI码状态为维修中
					String result = logic.updateIMEIForBackSupplier(user, db, bean);
					if (result != null) {
						db.rollbackTransaction();
						j.setMsg(result);
						return j;
					}
					String content = "将售后处理单[" + code + "]的商品返厂";
					if(!afStockService.writeAfterSaleLog(user, content, detectCodesSet.size(), AfterSaleLogBean.TYPE12,code,null)){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加售后日志（返厂）失败!");
						return j;
					}
				}
				request.getSession().setAttribute("backRepairListBean", repairListBean);
				afStockService.getDbOp().commitTransaction();
				j.setMsg("添加成功!");
				j.setSuccess(true);	
			}
		}catch (Exception e) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			db.release();
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 打印送修清单
	 *@author 李宁
	 *@date 2014-2-21 下午5:38:43
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/printRepairList")
	public String printRepairList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Set<String> detectCodeSet = (Set<String>) request.getSession().getAttribute("backSupplierProductDetectCodesSet");
		request.getSession().setAttribute("backSupplierProductDetectCodesSet", null);//清空session
		request.getSession().setAttribute("stockType", null);
		request.getSession().setAttribute("areaId", null);
		BackRepairListBean repairListBean = (BackRepairListBean) request.getSession().getAttribute("backRepairListBean");
		request.getSession().setAttribute("backRepairListBean", null);
		String result = "/admin/afStock/backSupplierProductListPrint";//返回页面
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			if(detectCodeSet!=null && detectCodeSet.size()>0){
				//返厂商品明细列表
				List<AfterSaleBackRepairProductDetail> productDetailList = new ArrayList<AfterSaleBackRepairProductDetail>();
				for(String code : detectCodeSet){
					if(code.trim().length()>0){
						AfterSaleDetectProductBean bean = afStockService.getAfterSaleDetectProduct("code='"+code+"'");
						if(bean!=null){
							AfterSaleBackRepairProductDetail productDetailBean = new AfterSaleBackRepairProductDetail();
							productDetailBean.setDetectCode(bean.getCode());
							productDetailBean.setAfterSaleDetectProductId(bean.getId());
							productDetailBean.setProductId(bean.getProductId());
							AfterSaleBackSupplierProduct supplierProduct = afStockService.getAfterSaleBackSupplierProduct("after_sale_detect_product_id=" + bean.getId());
							if(supplierProduct!=null){
								productDetailBean.setImei(supplierProduct.getIMEI());
								productDetailBean.setProductOriname(supplierProduct.getProductOriname());
							}
							int count = afStockService.getAfterSaleBackSupplierProductCount("after_sale_detect_product_id=" + bean.getId() +" and status!=3");
							if(count>1){
								productDetailBean.setFirstRepair(1);//返修商品
							}else{
								productDetailBean.setFirstRepair(0);//第一次维修
							}
							
							ResultSet rs = dbOp.executeQuery("select name from product where id=" + productDetailBean.getProductId());
							if(rs!=null && rs.next()){
								productDetailBean.setProductName(rs.getString(1));
							}
							rs.close();
							rs = dbOp.executeQuery("select content from after_sale_detect_log where "
									 + "after_sale_detect_product_id=" + productDetailBean.getAfterSaleDetectProductId()
									+ " and after_sale_detect_type_id=8 order by create_datetime desc limit 1");//故障代码
							if(rs!=null && rs.next()){
								productDetailBean.setFaultCode(rs.getString(1));
							}
							rs.close();
							rs = dbOp.executeQuery("select content from after_sale_detect_log where "
									 + "after_sale_detect_product_id=" + productDetailBean.getAfterSaleDetectProductId()
									+ " and after_sale_detect_type_id=4 order by create_datetime desc limit 1");//故障描述
							if(rs!=null && rs.next()){
								productDetailBean.setFaultDescript(rs.getString(1));
							}
							rs.close();
							rs = dbOp.executeQuery("select content from after_sale_detect_log where "
									 + "after_sale_detect_product_id=" + productDetailBean.getAfterSaleDetectProductId()
									+ " and after_sale_detect_type_id=5 order by create_datetime desc limit 1");//申报状态
							if(rs!=null && rs.next()){
								productDetailBean.setReportStatus(rs.getString(1));
							}
							rs.close();
							productDetailList.add(productDetailBean);
						}
					}
				}

				request.setAttribute("productDetailList", productDetailList);
				request.setAttribute("totalCount", productDetailList.size());
				request.setAttribute("repairListBean", repairListBean);
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return result;
	}
	
	/**
	 * 通过包裹单号打印送修清单
	 *@author 李宁
	 *@date 2014-2-21 下午5:38:43
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/printRepairListByPackageCode")
	public String printRepairListByPackageCode(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String packageCode = StringUtil.dealParam(request.getParameter("packageCode"));
		String mark = StringUtil.convertNull(request.getParameter("mark"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String result = "/admin/afStock/backSupplierProductListPrint";//返回页面
		//mark=1时，仅仅展示返厂清单不打印
		if("1".equals(mark)){
			result = "/admin/afStock/backSupplierProductList";
		}
		try{
			if(packageCode!=null && packageCode.length()>0){
				List<AfterSaleBackSupplierProduct> backSupplierProList =  afStockService.getAfterSaleBackSupplierProductList("package_code='" + packageCode + "'" , -1, -1, null);
				if(backSupplierProList!=null && backSupplierProList.size()>0){
					BackRepairListBean repairListBean = new BackRepairListBean();
					//返厂商品明细列表
					List<AfterSaleBackRepairProductDetail> productDetailList = new ArrayList<AfterSaleBackRepairProductDetail>();
					int flag = 0;
					for(int i=0;i<backSupplierProList.size();i++){
						AfterSaleDetectProductBean bean = afStockService.getAfterSaleDetectProduct("id=" + backSupplierProList.get(i).getAfterSaleDetectProductId());
						if(bean!=null){
							AfterSaleBackRepairProductDetail productDetailBean = new AfterSaleBackRepairProductDetail();
							productDetailBean.setDetectCode(bean.getCode());
							productDetailBean.setAfterSaleDetectProductId(bean.getId());
							productDetailBean.setProductId(bean.getProductId());
							AfterSaleBackSupplierProduct supplierProduct = afStockService.getAfterSaleBackSupplierProduct("after_sale_detect_product_id=" + bean.getId());
							//手动填的iemi码可以获取到 如果没有返厂商品则显示售后处理单的imei码 
							if(supplierProduct!=null){
								productDetailBean.setImei(supplierProduct.getIMEI());
								productDetailBean.setProductOriname(supplierProduct.getProductOriname());
							}
							int count = afStockService.getAfterSaleBackSupplierProductCount("after_sale_detect_product_id=" + bean.getId() +" and status!=3");
							if(count>1){
								productDetailBean.setFirstRepair(1);//返修商品
							}else{
								productDetailBean.setFirstRepair(0);//第一次维修
							}
							ResultSet rs = dbOp.executeQuery("select name from product where id=" + productDetailBean.getProductId());
							if(rs!=null && rs.next()){
								productDetailBean.setProductName(rs.getString(1));
							}
							rs.close();
							rs = dbOp.executeQuery("select content from after_sale_detect_log where "
									 + "after_sale_detect_product_id=" + productDetailBean.getAfterSaleDetectProductId()
									+ " and after_sale_detect_type_id=8 order by create_datetime desc limit 1");//故障代码
							if(rs.next()){
								productDetailBean.setFaultCode(rs.getString(1));
							}
							rs.close();
							rs = dbOp.executeQuery("select content from after_sale_detect_log where "
									 + "after_sale_detect_product_id=" + productDetailBean.getAfterSaleDetectProductId()
									+ " and after_sale_detect_type_id=4 order by create_datetime desc limit 1");//故障描述
							if(rs!=null && rs.next()){
								productDetailBean.setFaultDescript(rs.getString(1));
							}
							rs.close();
							rs = dbOp.executeQuery("select content from after_sale_detect_log where "
									 + "after_sale_detect_product_id=" + productDetailBean.getAfterSaleDetectProductId()
									+ " and after_sale_detect_type_id=5 order by create_datetime desc limit 1");//申报状态
							if(rs!=null && rs.next()){
								productDetailBean.setReportStatus(rs.getString(1));
							}
							rs.close();
							productDetailList.add(productDetailBean);
							
							if(flag<=0){
								repairListBean.setContract(backSupplierProList.get(i).getContract());
								repairListBean.setRemark(backSupplierProList.get(i).getRemark());
								repairListBean.setPackageCode(packageCode);
								repairListBean.setContractPhone(backSupplierProList.get(i).getContractPhone());
								repairListBean.setDeliveryAddress(backSupplierProList.get(i).getDeliveryAddress());
								repairListBean.setZipCode(backSupplierProList.get(i).getZipCode());
								AfterSaleBackSupplier supplier = afStockService.getAfterSaleBackSupplier("id=" + backSupplierProList.get(i).getSupplierId());
								repairListBean.setSupplierName(supplier.getName());
								String sendDatetime = backSupplierProList.get(i).getSendDatetime();
								if(sendDatetime!=null && !sendDatetime.equals("")){
									repairListBean.setShipDate(sendDatetime.substring(0,10));
								}
								flag++;
							}
						}
					}

					request.setAttribute("productDetailList", productDetailList);
					request.setAttribute("totalCount", productDetailList.size());
					request.setAttribute("repairListBean", repairListBean);
				}
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return result;
	}
	
	/**
	 * @return 签收包裹状态comboBox
	 * @author zy
	 */
	@RequestMapping("/getDetectPackageStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDetectPackageStatus(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> statusMap = AfterSaleDetectPackageBean.statusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(int i : statusMap.keySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(i + "");
				bean.setText(statusMap.get(i));
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}
		return comboBoxList;
	}

	/**
	 * 保存寄件人信息
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/saveSenderInfo")
	@ResponseBody
	public Json saveSenderInfo (HttpServletRequest request, HttpServletResponse response,
			int id, String orderCode, String phone, String senderName, String senderAddress) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
    		synchronized(lock){
    			afStockService.getDbOp().startTransaction();
    			if (!afStockService.updateAfterSaleDetectPackage("order_code='" + StringUtil.toSql(orderCode) + "',phone='" + StringUtil.toSql(phone) + "',sender_name='" + StringUtil.toSql(senderName) + "',sender_address='" + StringUtil.toSql(senderAddress) + "'", "id=" + id)) {
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("更新签收包裹信息失败！");
    				return j;
    			}
    			j.setMsg("更新成功！");
    			j.setSuccess(true);
    			afStockService.getDbOp().commitTransaction();
    		}
		} catch(Exception e) {
			afStockService.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return j;
	}
	
	/**
	 * 客户寄回包裹列表
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getSendBackDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getSendBackDatagrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String packageCode, String createUserName, String senderName, String senderAddress,String startTime, String endTime, int status, String phone
			,String afterSaleCode,String orderCode,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" 1=1 ");
			if(!"".equals(StringUtil.checkNull(orderCode))) {
				buff.append(" and asdp.order_code='" + orderCode + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and asdp.area_id=" + areaId);
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))) {
				buff.append(" and asdpr.after_sale_order_code='" + afterSaleCode +"'");
			}	
			if (packageCode != null && !packageCode.trim().equals("")) {
				buff.append(" and asdp.package_code='" + StringUtil.toSql(packageCode) + "'");
			}
			if (createUserName != null && !createUserName.trim().equals("")) {
				buff.append(" and asdp.create_user_name='" + StringUtil.toSql(createUserName) + "'");
			}
			if (senderName != null && !senderName.trim().equals("")) {
				buff.append(" and asdp.sender_name='" + StringUtil.toSql(senderName) + "'");
			}
			if (senderAddress != null && !senderAddress.trim().equals("")) {
				buff.append(" and sender_address like '%" + StringUtil.toSql(senderAddress) + "%'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime<='" + endTime + " 23:59:59'");
			}
			if (status != -1) {
				buff.append(" and asdp.status=" + status);
			}
			if (phone != null && !phone.trim().equals("")) {
				buff.append(" and asdp.phone like '%" + StringUtil.toSql(phone) + "%'");
			}
			int totalCount = afStockService.getDetectPackAgeCountLeft(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleDetectPackageBean> maps = afStockService.getDetectPackAgeListLeft(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "id desc");
			for (AfterSaleDetectPackageBean bean : maps) {
				bean.setStatusName(AfterSaleDetectPackageBean.statusMap.get(bean.getStatus()));
				String afterSaleOrderCodes = "";
				String afterSaleOrderIds = "";
				ResultSet rs = afStockService.getDbOp().executeQuery("select distinct aso.after_sale_order_code,aso.id from after_sale_detect_product asdp,after_sale_order aso where asdp.after_sale_order_id=aso.id and asdp.after_sale_detect_package_id=" + bean.getId());
				while (rs.next()) {
					if (afterSaleOrderCodes.equals("")) {
						afterSaleOrderCodes = rs.getString(1);
						afterSaleOrderIds = rs.getInt(2) + "";
					} else {
						afterSaleOrderCodes += "," + rs.getString(1);
						afterSaleOrderIds += "," + rs.getInt(2);
					}
				}
				rs.close();
				bean.setAfterSaleOrderCodes(afterSaleOrderCodes);
				bean.setAfterSaleOrderIds(afterSaleOrderIds);
				String orderCodes = "";
				ResultSet rs2 = afStockService.getDbOp().executeQuery("select distinct aso.order_code from after_sale_detect_product asdp,after_sale_order aso where asdp.after_sale_order_id=aso.id and asdp.after_sale_detect_package_id=" + bean.getId());
				while (rs2.next()) {
					if (orderCodes.equals("")) {
						orderCodes = rs2.getString(1);
					} else {
						orderCodes += "," + rs2.getString(1);
					}
				}
				rs2.close();
				bean.setOrderCodes(orderCodes);
				String productOriNames = "";
				String remarks = "";
				ResultSet rs3 = afStockService.getDbOp().executeQuery("select group_concat(product_name),group_concat(remark) from after_sale_match_fail_package_product where package_id=" + bean.getId());
				if(rs3!=null && rs3.next()){
					productOriNames = rs3.getString(1);
					remarks = rs3.getString(2);
				}
				bean.setProductOrinames(productOriNames);
				//失败商品的备注，之前包裹的备注字段取消了2014-09-16
				bean.setRemark(remarks);
			}
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 售后单记录
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getAfterSaleDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleDatagrid(HttpServletRequest request, HttpServletResponse response,
			String orderCode, String phone, String senderName, String senderAddress) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService iAfterSalesService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if ((orderCode == null || orderCode.trim().equals("")) && 
					(phone == null || phone.trim().equals("")) &&
					(senderName == null || senderName.trim().equals("")) &&
					(senderAddress == null || senderAddress.trim().equals("")) ) {
				datagrid.setTotal((long)0);
				datagrid.setRows(new ArrayList());
				return datagrid;
			}
			String[] orderCodes = orderCode.split("\n");
			String[] phones = phone.split("\n");
			StringBuffer buff = new StringBuffer();
			buff.append("status=" + AfterSaleOrderBean.STATUS_等待包裹寄回);
			if (orderCode != null && !orderCode.trim().equals("")) {
				if (orderCode.trim().length() != 0 ) {
					buff.append(" and (");
					for(int i = 0 ; i < orderCodes.length ;i++ ){
						voOrder order = wareService.getOrder(" code='"+orderCodes[i]+"'");
						if(order==null){
							datagrid.setTotal((long)0);
							datagrid.setRows(new ArrayList());
							return datagrid;
						}
						if(orderCodes[i].trim().length()>0){
							buff.append("order_id='");
							buff.append(order.getId());
							buff.append("' || ");
						}
					}
					buff.replace(buff.length()-3, buff.length(), "");
					buff.append(")");
				}
			}
			if (phone != null && !phone.trim().equals("")) {
				if (phone.trim().length() != 0 ) {
					buff.append(" and (");
					for(int i = 0 ; i < phones.length ;i++ ){
						if(phones[i].trim().length()>0){
							buff.append("customer_phone='");
							buff.append(phones[i].trim().replaceAll("\n", ""));
							buff.append("' || ");
						}
					}
					buff.replace(buff.length()-3, buff.length(), "");
					buff.append(")");
				}
			}
			if (senderName != null && !senderName.trim().equals("")) {
				buff.append(" and customer_name='" + StringUtil.toSql(senderName) + "'");
			}
			if (senderAddress != null && !senderAddress.trim().equals("")) {
				buff.append(" and customer_address='" + StringUtil.toSql(senderAddress) + "'");
			}
			int totalCount = iAfterSalesService.getAfterSaleOrderCount(buff.toString());
			datagrid.setTotal((long)totalCount);
			List<AfterSaleOrderBean> maps = iAfterSalesService.getAfterSaleOrderList(buff.toString(), -1, -1, null);
			for (AfterSaleOrderBean bean : maps) {
				bean.setStatusName("" + AfterSaleOrderBean.STATUS_MAP.get(bean.getStatus()));
				String productNames = "";
				String productCodes = "";
				ResultSet rs = afStockService.getDbOp().executeQuery("select p.name,p.code from product p,after_sale_order_product asop where p.id=asop.product_id and asop.after_sale_order_id=" + bean.getId());
				while (rs.next()) {
					if (productNames.equals("")) {
						productNames = rs.getString(1);
						productCodes = rs.getString(2);
					} else {
						productNames += "," + rs.getString(1);
						productCodes += "," + rs.getString(2);
					}
				}
				rs.close();
				bean.setProductNames(productNames);
				bean.setProductCodes(productCodes);
				
				String mark = "";
				ResultSet rs2 = afStockService.getDbOp().executeQuery("select mark from customer_opinion_record where after_sale_order_id = " + bean.getId());
				while (rs2.next()) {
					if (!mark.equals("")) {
						mark += "<br/>";
					}
					mark += rs2.getString(1);
				}
				rs2.close();
				bean.setQuestionDis(mark == null || mark.equals("null") ? "" : mark);
				
				voOrder order = wareService.getOrder(bean.getOrderId());
				if (order == null) {
					bean.setBuyModeName("");
					bean.setFlatName("");
				} else {
					bean.setBuyModeName("" + voOrder.buyModeMap.get("" + order.getBuyMode()));
					bean.setFlatName(voOrder.flatMap.get(order.getFlat()));
				}
			}
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 匹配失败
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/matchFailPackage")
	@ResponseBody
	public Json matchFailPackage (HttpServletRequest request, HttpServletResponse response,
			int id, String remark) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
    		synchronized(lock){
    			afStockService.getDbOp().startTransaction();
    			AfterSaleDetectPackageBean asdp = afStockService.getAfterSaleDetectPackage("id=" + id);
    			if (asdp == null) {
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("该包裹不存在！");
    				return j;
    			}
    			
    			if (asdp.getStatus() != AfterSaleDetectPackageBean.STATUS0) {
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("该包裹不是未匹配包裹，不能进行匹配失败操作！");
    				return j;
    			}
    			if(remark == null || remark.trim().equals("")) {
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("匹配失败备注不能为空！");
    				return j;
    			}
    			if (!afStockService.updateAfterSaleDetectPackage("remark='" + StringUtil.toSql(remark) + "',status=" + AfterSaleDetectPackageBean.STATUS2, "id=" + id)) {
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("匹配失败操作失败！");
    				return j;
    			}
    			j.setMsg("匹配失败操作成功！");
    			j.setSuccess(true);
    			afStockService.getDbOp().commitTransaction();
    		}
		} catch(Exception e) {
			afStockService.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return j;
	}
	
	/**
	 * 加载包裹对应商品信息
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/addAfterSaleDetectProducts")
	public void addAfterSaleDetectProducts (HttpServletRequest request, HttpServletResponse response,
			String afterSaleOrderIds,boolean flag) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService iService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,dbOp);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
		String[] afterSaleOrderIdsArray = afterSaleOrderIds.split(",");
		StringBuffer sb = new StringBuffer(); 
		try{
			for (String afterSaleOrderId : afterSaleOrderIdsArray) {
				AfterSaleOrderBean asobean = iService.getAfterSaleOrder("id="+afterSaleOrderId);
				if (asobean == null) {
					request.setAttribute("msg", "售后单不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return;
				}
				List<AfterSaleOrderProduct> asopList = iService.getAfterSaleOrderProductList("after_sale_order_id="+afterSaleOrderId, -1, -1, null);
				
				sb.append("<div style='height:auto'>");
				sb.append("<fieldset>");
				sb.append("<div id=\"toolbar"+afterSaleOrderId+"\"  style=\"height: auto;\">");
				sb.append("<h3>售后单号：" + asobean.getAfterSaleOrderCode() + "</h3>");
				sb.append("<hr/>");
				sb.append("售后单记录<br/>");
				sb.append("问题描述：");
				ResultSet recordRS = wareService.getDbOp().executeQuery("select mark from customer_opinion_record where after_sale_order_id = " + afterSaleOrderId );
				String mark = "";
				while (recordRS.next()) {
					if (!mark.equals("")) {
						mark += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}
					mark += recordRS.getString(1);
				}
				recordRS.close();
				sb.append(mark == null || mark.equals("null") ? "" : mark);
				sb.append("</div>");
				sb.append("<table id='afterSaleOrderDataGrid_" + afterSaleOrderId + "'></table></fieldset></div>");
				sb.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
				List<AfterSaleOrderProduct> packageProductList = new ArrayList<AfterSaleOrderProduct>();
				List<AfterSaleOrderProduct> removeList = new ArrayList<AfterSaleOrderProduct>();
				for (AfterSaleOrderProduct asopBean : asopList) {
					voProduct product = wareService.getProduct(asopBean.getProductId());
					if( product != null ) {
						if(product.getIsPackage() == 1){ // 如果这个产品是套装
							removeList.add(asopBean);
							List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
							if(ppList == null || ppList.size() == 0){
								continue;
							} else {
								Iterator ppIter = ppList.listIterator();
								while(ppIter.hasNext()){
									ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
									AfterSaleOrderProduct asop = new AfterSaleOrderProduct();
									asop.setId(asopBean.getId());
									asop.setAfterSaleOrderId(asopBean.getAfterSaleOrderId());
									voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
									asop.setProductId(ppBean.getProductId());
									asop.setProductCode(tempProduct.getCode());
									asop.setProductName(tempProduct.getName());
									ProductBarcodeVO bBean = bService.getProductBarcode("product_id=" + asop.getProductId() + " and barcode_status=0");
									if (bBean != null) {
										asop.setBarcode(bBean.getBarcode());
									}
									packageProductList.add(asop);
								}
							}
						} else {
							asopBean.setProductName(product.getName());
							asopBean.setProductCode(product.getCode());
							ProductBarcodeVO bBean = bService.getProductBarcode("product_id=" + asopBean.getProductId() + " and barcode_status=0");
							if (bBean != null) {
								asopBean.setBarcode(bBean.getBarcode());
							}
						}
					}
				}
				asopList.removeAll(removeList);
				asopList.addAll(packageProductList);
				
				sb.append("$('#afterSaleOrderDataGrid_" + afterSaleOrderId + "').datagrid({");
				sb.append("toolbar:'toolbar" + afterSaleOrderId + "',fit : true,fitColumns : true,striped : true,nowrap : true,loadMsg : '正在努力为您加载..',singleSelect : true,");
				sb.append("columns:[[");
				sb.append("{field:'id',width:80,align:'center',hidden:true},");
				sb.append("{field:'afterSaleOrderId',width:80,align:'center',hidden:true},");
				sb.append("{field:'productId',width:80,align:'center',hidden:true},");
				sb.append("{field:'barcode',width:80,align:'center',hidden:true},");
				sb.append("{field:'action',title:'选择',width:60,align:'center',formatter : function(value, row, index) {if(value!='')return '<a href=\"javascript:void(0);\" class=\"addProducts\" onclick=\"addProduct(\\\'afterSaleOrderDataGrid_" + afterSaleOrderId + "\\\','+index+')\"></a>';}},");
				sb.append("{field:'productName',title:'小店名称',width:80,align:'center'},");
				sb.append("{field:'productCode',title:'商品编号',width:80,align:'center',editor : {type : 'validatebox',options:{required:true}},formatter : function(value, row, index) {return \"<a href='javascript:void(0);' onclick='openProductDetail(\"+ row.productId +\")' > \"+ value +\"</a>\";}}");
				sb.append("] ],");
				sb.append("onLoadSuccess : function(data) {$('.addProducts').linkbutton({plain:true,iconCls:'icon-add'});}");
				sb.append("});");
				sb.append("$('#afterSaleOrderDataGrid_" + afterSaleOrderId + "').datagrid(\"loadData\",").append(JSONArray.fromObject(asopList).toString()).append(");");
				sb.append("addLastRow('afterSaleOrderDataGrid_" + afterSaleOrderId + "');");
				sb.append("</script>");
			}
			
			sb.append("<hr/>");
			sb.append("<h3>签收商品记录</h3>");
			sb.append("<div style='height:250px'>");
			sb.append("<div id=\"packageProductsToolbar\"  style=\"height: auto;\">");
			if(flag){
				sb.append("<a class=\"addDetectProductsClass\" onclick=\"modifyDetectProducts();\" href=\"javascript:void(0);\">提交</a>");
			}else{
				sb.append("<a class=\"addDetectProductsClass\" onclick=\"addDetectProducts();\" href=\"javascript:void(0);\">提交</a>");
			}
			sb.append("</div>");
			sb.append("<table id='packageProductsDataGrid'></table></div>");
			sb.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
			sb.append("initPackageProducts();");
			sb.append("</script>");
			response.getWriter().write(sb.toString());
			
		} catch(Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 判断是否为商品
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/validateAddOwnProduct")
	@ResponseBody
	public Json validateAddOwnProduct (HttpServletRequest request, HttpServletResponse response,
			String productCode, int afterSaleOrderId) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			if (productCode == null || productCode.trim().equals("")) {
				j.setMsg("商品编号不能为空!");
				return j;
			}
			ProductBarcodeVO bBean= bService.getProductBarcode("barcode='"+StringUtil.toSql(productCode)+"'");
			voProduct product = null;
			if( bBean == null || bBean.getBarcode() == null ) {
				product = wareService.getProduct(StringUtil.toSql(productCode));
			} else {
				product = wareService.getProduct(bBean.getProductId());
			}
			if( product == null ) {
				j.setMsg("条码有误！");
				return j;
			}
			if(product.getIsPackage() == 1){ // 如果这个产品是套装
				j.setMsg("不能添加套装产品！");
				return j;
			}
			AfterSaleOrderProduct asop = new AfterSaleOrderProduct();
			asop.setId(-1);
			asop.setAfterSaleOrderId(afterSaleOrderId);
			asop.setProductCode(product.getCode());
			asop.setProductName(product.getName());
			asop.setProductId(product.getId());
			if (bBean != null) {
				asop.setBarcode(bBean.getBarcode());
			}
			j.setSuccess(true);
			j.setObj(asop);
			return j;
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			wareService.releaseAll();	
		}
	}
	
	/**
	 * 判断商品是否属于售后单、订单
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/validatePackageProduct")
	@ResponseBody
	public Json validatePackageProduct(HttpServletRequest request, HttpServletResponse response,
			int id, int productId, int afterSaleOrderId) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IAfterSalesService iService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			AfterSaleDetectProductBean asdp = new AfterSaleDetectProductBean();
			
			//判断是否在售后单中
			if (id == -1) {
				asdp.setInBuyOrder(AfterSaleDetectProductBean.INBUYORDER0);
				//判断是否在订单中
				AfterSaleOrderBean asoBean = iService.getAfterSaleOrder("id=" + afterSaleOrderId);
				if (asoBean == null) {
					asdp.setInUserOrder(AfterSaleDetectProductBean.INUSERORDER0);
				} else {
					OrderStockBean osBean = stockService.getOrderStock("order_id=" + asoBean.getOrderId() + " and status <> " + OrderStockBean.STATUS4);
					if (osBean == null) {
						asdp.setInUserOrder(AfterSaleDetectProductBean.INUSERORDER0);
					} else {
						OrderStockProductBean ospBean = stockService.getOrderStockProduct("order_stock_id=" + osBean.getId() + " and product_id=" + productId + " and status <> " + OrderStockProductBean.DELETED);
						if (ospBean == null) {
							asdp.setInUserOrder(AfterSaleDetectProductBean.INUSERORDER0);
						} else {
							asdp.setInUserOrder(AfterSaleDetectProductBean.INUSERORDER1);
						}
					}
				}
				
			} else {
				asdp.setInBuyOrder(AfterSaleDetectProductBean.INBUYORDER1);
				asdp.setInUserOrder(AfterSaleDetectProductBean.INUSERORDER1);
			}
			asdp.setInBuyOrderName(AfterSaleDetectProductBean.inBuyOrderMap.get(asdp.getInBuyOrder()));
			asdp.setInUserOrderName(AfterSaleDetectProductBean.inUserOrderMap.get(asdp.getInUserOrder()));
			
			asdp.setAfterSaleOrderId(afterSaleOrderId);
			asdp.setProductId(productId);
			voProduct product = wareService.getProduct(productId);
			if (product != null) {
				asdp.setProductName(product.getName());
				asdp.setProductCode(product.getCode());
			}
			if(product.getIsPackage() == 1){ // 如果这个产品是套装
				j.setMsg("不能添加套装产品！");
				return j;
			}
			asdp.setRemark("");
			
			j.setSuccess(true);
			j.setObj(asdp);
			return j;
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			wareService.releaseAll();	
		}
	}
	
	/**
	 * 匹配售后单-生成售后处理单
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/addDetectProducts")
	@ResponseBody
	public Json addDetectProducts( HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg( "当前没有登录,操作失败!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService iService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		int afterSaleDetectPackageId = StringUtil.toInt(request.getParameter("afterSaleDetectPackageId"));
		String afterSaleDetectProducts = request.getParameter("afterSaleDetectProducts");
		AfterSaleDetectProductBean asdpBean = new AfterSaleDetectProductBean();
		List<AfterSaleDetectProductBean> rpList = new ArrayList<AfterSaleDetectProductBean>();
		String[] theArray = afterSaleDetectProducts.split(",");
		
		try {
			synchronized (lock) {
				for (int i = 0; i < theArray.length; i++) {
					switch (i%5) {
						case 0: asdpBean.setRemark(theArray[i]);
							break;
						case 1: asdpBean.setAfterSaleOrderId(StringUtil.toInt(theArray[i]));
							break;
						case 2: asdpBean.setProductId(StringUtil.toInt(theArray[i]));
							break;
						case 3: asdpBean.setInBuyOrder(StringUtil.toInt(theArray[i]));
							break;
						case 4: asdpBean.setInUserOrder(StringUtil.toInt(theArray[i]));
							rpList.add(asdpBean);
							asdpBean = new AfterSaleDetectProductBean();
							break;
						default:
							break;
					}
				}
				AfterSaleDetectPackageBean asdpackageBean = afStockService.getAfterSaleDetectPackage("id=" + afterSaleDetectPackageId);
				
				if (asdpackageBean == null) {
					j.setMsg("寄回包裹不存在!");
					return j;
				}
				if(!afStockService.checkAfterSaleUserGroup(user, asdpackageBean.getAreaId())){
					j.setMsg("没有该包裹所在地区售后仓内作业权限!");
					return j;
				}
				if (asdpackageBean.getStatus() != AfterSaleDetectPackageBean.STATUS0 && asdpackageBean.getStatus() != AfterSaleDetectPackageBean.STATUS2 ) {
					j.setMsg("包裹必须为未匹配或匹配失败时才能进行匹配操作!");
					return j;
				}
				Map<Integer, String> afterSaleOrderIdMap = new HashMap<Integer, String>();
				afStockService.getDbOp().startTransaction();
				for (AfterSaleDetectProductBean asdp : rpList) {
					AfterSaleOrderBean asoBean = iService.getAfterSaleOrder("id = " + asdp.getAfterSaleOrderId()); 
					if (asoBean == null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("售后单不存在！");
						return j;
					}
					if (asoBean.getStatus() != AfterSaleOrderBean.STATUS_等待包裹寄回) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("售后单状态不是等待包裹寄回！");
						return j;
					}
					asdp.setAfterSaleDetectPackageId(afterSaleDetectPackageId);
					asdp.setAfterSaleOrderCode(asoBean.getAfterSaleOrderCode());
					afterSaleOrderIdMap.put(asdp.getAfterSaleOrderId(), "");
					asdp.setCode("");
					asdp.setStatus(AfterSaleDetectProductBean.STATUS0);
					asdp.setLockStatus(AfterSaleDetectProductBean.LOCK_STATUS0);
					asdp.setAreaId(asdpackageBean.getAreaId());
					if (!afStockService.addAfterSaleDetectProduct(asdp)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加售后处理单失败!");
						return j;
					}
				}
				
				if (!afStockService.updateAfterSaleDetectPackage("status=" + AfterSaleDetectPackageBean.STATUS1, "id=" + afterSaleDetectPackageId)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("签收包裹更新状态失败!");
					return j;
				}
				String afterSaleOrderCodes = "";
				for (int afterSaleOrderId : afterSaleOrderIdMap.keySet()) {
					if (!iService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_售后检测中, "id="+ afterSaleOrderId)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后单状态失败!");
						return j;
					}
					AfterSaleOrderBean asoBean = iService.getAfterSaleOrder("id=" + afterSaleOrderId);
					if (asoBean == null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("售后单不存在!");
						return j;
					}
					voOrder order = adminService.getOrder(asoBean.getOrderId());
					if(order==null){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("订单不存在!");
						return j;
					}
					afterSaleOrderCodes += asoBean.getAfterSaleOrderCode() + ",";
					//发送短信
//					String templateName=TemplateMarker.DETECT_PACKAGE_MESSAGE_NAME;
//					Map<String,Object> paramMap = new HashMap<String,Object>();
//					
//					TemplateMarker tm =TemplateMarker.getMarker();
//					String content2=tm.getOutString(templateName, paramMap);
//					if(order.isDaqOrder()){
//						SenderSMS3.send(user.getId(), asoBean.getCustomerPhone(), content2,65);//无user，userId用0代替
//					}else{
//						SenderSMS3.send(user.getId(), asoBean.getCustomerPhone(), content2);//无user，userId用0代替
//					}
				}
				String content = "包裹匹配,将售后单[" + afterSaleOrderCodes + "]从[未匹配]状态改为[已匹配]状态";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE13,asdpackageBean.getPackageCode(),null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				afStockService.getDbOp().commitTransaction();
			} 
		}catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常!");
			return j;
		} finally {
			afStockService.releaseAll();
		}
		j.setMsg("匹配成功！");
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 包裹售后单记录
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/packageAfterSaleDatagrid")
	@ResponseBody
	public Json packageAfterSaleDatagrid(HttpServletRequest request, HttpServletResponse response, String packageCode) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService iAfterSalesService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if (packageCode == null || packageCode.trim().equals("")) {
				j.setMsg("包裹单号不能为空！");
				return j;
			}
			AfterSaleDetectPackageBean packageBean = afStockService.getAfterSaleDetectPackage("package_code='" + StringUtil.toSql(packageCode) + "'");
			if (packageBean == null) {
				j.setMsg("包裹单不存在！");
				return j;
			}
			
			if (packageBean.getStatus() == AfterSaleDetectPackageBean.STATUS3) {
				j.setMsg("包裹单已检测完毕！");
				return j;
			}
			
			if (packageBean.getStatus() != AfterSaleDetectPackageBean.STATUS1) {
				j.setMsg("包裹单尚未匹配！");
				return j;
			}
			
			String afterSaleOrderIds = "";
			
			ResultSet rs = afStockService.getDbOp().executeQuery("select distinct asdp.after_sale_order_id from after_sale_detect_product asdp where asdp.after_sale_detect_package_id=" + packageBean.getId() + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 + ")");
			while (rs.next()) {
				if (afterSaleOrderIds.equals("")) {
					afterSaleOrderIds = rs.getString(1);
				} else {
					afterSaleOrderIds += "," + rs.getString(1);
				}
			}
			rs.close();
			if (afterSaleOrderIds.equals("")) {
				j.setMsg("没有符合条件的售后单！");
				return j;
			}
			
			Map<Integer, String> textResMap = new HashMap<Integer, String>();
			ResultSet rs3 = afStockService.getDbOp().executeQuery("select id,content from text_res where type=4");
			while (rs3.next()) {
				textResMap.put(rs3.getInt(1), rs3.getString(2));
			}
			rs3.close();
			List<AfterSaleOrderBean> maps = iAfterSalesService.getAfterSaleOrderList("id in (" + afterSaleOrderIds + ")", -1, -1, null);
			StringBuffer sb = new StringBuffer("<br><table class=\"gridtable\" style=\"width:100%\">");
			for (AfterSaleOrderBean bean : maps) {
				bean.setStatusName("" + AfterSaleOrderBean.STATUS_MAP.get(bean.getStatus()));
				
				String mark = "";
				String content = "";
				ResultSet recordRS = afStockService.getDbOp().executeQuery("select content,mark from customer_opinion_record where after_sale_order_id = " + bean.getId() + " order by id desc " );
				while (recordRS.next()) {
					if (!mark.equals("")) {
						mark += "\n";
					}
					mark += recordRS.getString(2);
					//客户要求只获取最后一条
					if(content.trim().length()==0){
						content = recordRS.getString(1);
					}
				}
				recordRS.close();
				bean.setQuestionDis(mark == null  || mark.equals("null")? "" : mark);
				bean.setDemand(content == null  || content.equals("null")? "" : content);
				ResultSet complainSet = afStockService.getDbOp().executeQuery("select content from sys_dict where id = " + bean.getComplaintTypeId());
				if (complainSet.next()) {
					bean.setComplaintTypeName(complainSet.getString(1) == null ? "" : complainSet.getString(1));
				} else {
					bean.setComplaintTypeName("");
				}
				complainSet.close();
				sb.append("<tr>");
				sb.append("<td>");
				sb.append("<input type=\"radio\" id=\"afterSaleId" + bean.getId() + "\" name=\"afterSaleOrder\" value=\"" + bean.getId() + "\" onclick=\"getAfterSaleRadio(" + bean.getId() + ")\">");
				sb.append("售后单号：").append("<a href=\"javascript:void(0);\" onclick=\"openAfterSaleOrder(" + bean.getId() + ")\">" +bean.getAfterSaleOrderCode() + "</a>");
				sb.append("</td>");
				sb.append("<td>");
				sb.append("订单号：").append("<a href=\"javascript:void(0);\" onclick=\"openOrder(" + bean.getOrderId() + ")\">" +bean.getOrderCode() + "</a>");
				sb.append("</td>");
				sb.append("<td>");
				sb.append("客户要求：").append(bean.getDemand());
				sb.append("</td>");
				sb.append("<td>");
				sb.append("订单签收时间：").append(bean.getOrderConfirmTime() == null || bean.getOrderConfirmTime().equals("") ? "" : bean.getOrderConfirmTime().substring(0, 10));
				sb.append("</td>");
				sb.append("<td>");
				sb.append("投诉分类：").append(bean.getComplaintTypeName());
				sb.append("</td>");
				sb.append("<td>");
				sb.append("问题描述：").append(bean.getProblemDescription());
				sb.append("</td>");
				sb.append("<td>");
				sb.append("客户问题描述：").append(bean.getQuestionDis());
				sb.append("</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			j.setSuccess(true);
			j.setObj(sb.toString());
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 是否显示修改签收商品按钮
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/isShowModify")
	@ResponseBody
	public Json isShowModify(HttpServletRequest request, HttpServletResponse response,
			int id,String packageCode) throws ServletException, IOException{
			Json json = new Json();
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "当前没有登录,操作失败！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE);
			AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
			IAfterSalesService iAfterSalesService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				AfterSaleOrderBean asoBean = iAfterSalesService.getAfterSaleOrder("id="+id);
				if (asoBean == null) {
					request.setAttribute("msg", "售后单不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				
				AfterSaleDetectPackageBean packageBean = afStockService.getAfterSaleDetectPackage("package_code='" + StringUtil.convertNull(packageCode) + "'");
				if(packageBean==null){
					request.setAttribute("msg", "签收包裹不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				//出现条件:售后单状态为“检测中”包裹状态为“已匹配”；其他状态下不显示该按钮
				if(AfterSaleOrderBean.STATUS_售后检测中==asoBean.getStatus() && AfterSaleDetectPackageBean.STATUS1==packageBean.getStatus()){
					json.setSuccess(true);
					json.setObj(packageBean);
				}
			}catch (Exception e) {
				System.out.print(DateUtil.getNow());e.printStackTrace();
			}finally{
				dbOp.release();
			}
			return json;
	}
	
	/**
	 * 售后单需检测商品
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDetectProductDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getDetectProductDatagrid(HttpServletRequest request, HttpServletResponse response, int id) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService iAfterSalesService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			AfterSaleOrderBean asoBean = iAfterSalesService.getAfterSaleOrder("id="+id);
			if (asoBean == null) {
				request.setAttribute("msg", "售后单不存在！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			voOrder order = wareService.getOrder(asoBean.getOrderId());
			if (order == null ) {
				request.setAttribute("msg", "订单不存在！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			Map<Integer, voCatalog> catalogs = new HashMap<Integer, voCatalog>(); 
			
			List firstList = wareService.getCatalogs("parent_id=0");
			Iterator firstIterator = firstList.listIterator();
    		voCatalog vcatalog = null;
    		while(firstIterator.hasNext()){
    			vcatalog = (voCatalog)firstIterator.next();
    			catalogs.put(Integer.valueOf(vcatalog.getId()), vcatalog);
    		}
			
			List<AfterSaleDetectProductBean> maps = afStockService.getAfterSaleDetectProductList("after_sale_order_id=" + asoBean.getId(), -1, -1, null);
			for (AfterSaleDetectProductBean bean : maps) {
				voProduct product = wareService.getProduct(bean.getProductId());
				if (product == null) {
					request.setAttribute("msg", "商品不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				bean.setProductName(product.getName());
				bean.setProductCode(product.getCode());
				bean.setParentId1(product.getParentId1());
				voCatalog catalog = ((voCatalog) catalogs.get(product.getParentId1()));
				if (catalog == null) {
					request.setAttribute("msg", "商品一级分类不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				bean.setParentId1Name(catalog.getName());
				bean.setPrice5(product.getPrice5());
				
				bean.setInBuyOrderName(AfterSaleDetectProductBean.inBuyOrderMap.get(bean.getInBuyOrder()));
				bean.setInUserOrderName(AfterSaleDetectProductBean.inUserOrderMap.get(bean.getInUserOrder()));
				
				ResultSet rs = wareService.getDbOp().executeQuery("select type from product_sell_property where product_id=" + product.getId());
				if (rs.next()) {
					bean.setSellTypeName(ProductSellPropertyBean.typeMap.get(rs.getInt(1)));
				}
				bean.setFlatName(voOrder.flatMap.get(order.getFlat()));
			}
			datagrid.setTotal((long)maps.size());
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 检测商品
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/addDetectProduct")
	@ResponseBody
	public Json addDetectProductInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		boolean msgStatus = false;
		String afterSaleDetectProductCode = StringUtil.convertNull(request.getParameter("afterSaleDetectProductCode"));
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int id = StringUtil.toInt(request.getParameter("id"));
		int afterSaleDetectPackageId = StringUtil.toInt(request.getParameter("afterSaleDetectPackageId"));
		int afterSaleOrderId = StringUtil.toInt(request.getParameter("afterSaleOrderId"));
		String mainProductStatus = StringUtil.convertNull(request.getParameter("mainProductStatus"));
		String handle = StringUtil.convertNull(request.getParameter("handle")).trim();
		String IMEI = StringUtil.convertNull(request.getParameter("IMEI")).trim();
		String remark = StringUtil.convertNull(request.getParameter("remark")).trim();
		String questionDescription = StringUtil.convertNull(request.getParameter("questionDescription").replaceAll("/请选择","")).trim();
		String damaged = StringUtil.convertNull(request.getParameter("damaged").replaceAll("/请选择","")).trim();
		String giftAll = StringUtil.convertNull(request.getParameter("giftAll").replaceAll("/请选择","")).trim();
		String reportStatus = StringUtil.convertNull(request.getParameter("reportStatus").replaceAll("/请选择","")).trim();
		String faultCode = StringUtil.convertNull(request.getParameter("faultCode")).trim();
		String faultDescription = StringUtil.convertNull(request.getParameter("faultDescription").replaceAll("/请选择","")).trim();
		String detectException = StringUtil.convertNull(request.getParameter("detectException")).trim();
		String exceptionReason = StringUtil.convertNull(request.getParameter("exceptionReason").replaceAll("/请选择","")).trim();
		String quoteItem = StringUtil.convertNull(request.getParameter("quoteItem")).trim();
		String quote = StringUtil.convertNull(request.getParameter("quote")).trim();
		String debitNote = StringUtil.convertNull(request.getParameter("debitNote")).trim();
		String[] quoteItemAdd = request.getParameterValues("quoteItemadd");
		String[] quoteAdd = request.getParameterValues("quoteadd");
		String fittingNames = request.getParameter("fittingsNames");
		String intactFittingCounts = request.getParameter("intactFittingsCounts");
		String fittingIds = request.getParameter("fittingsIds");
		String badFittingCounts = request.getParameter("badFittingsCounts");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp); 
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAfterSalesService asoService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			synchronized (request.getSession().getId()) {
				String[] AfterSaleDetectTypeDetailContents=new String[3];
				String faultDescriptionIds[] = faultDescription.split("/");
				for(int i = 0 ; i < faultDescriptionIds.length ; i++){
					AfterSaleDetectTypeDetailBean asBean = afStockService.getAfterSaleDetectTypeDetail(" id=" + faultDescriptionIds[i]);
					if(asBean != null){
						AfterSaleDetectTypeDetailContents[i] = asBean.getContent();
					}else{
						j.setMsg("所选故障描述不存在！");
						return j;
					}
				}
				if("请选择".equals(exceptionReason)){
					exceptionReason="";
				}
				if (handle.equals(AfterSaleDetectProductBean.HANDLE1)) {
					if (exceptionReason.equals("")) {
						j.setMsg("处理意见是检测异常时，异常原因必须填写！");
						return j;
					}
					if (detectException.equals("")) {
						j.setMsg("处理意见是检测异常时，检测异常必须勾选！");
						return j;
					}
				} else if(handle.equals(AfterSaleDetectProductBean.HANDLE9)){
					if(!(parentId1==111 || parentId1 ==113 || parentId1 ==130)){
						j.setMsg("此商品分类不能选择处理建议为\"换新机\"！请重新选择！");
						return j;
					}
				}else {
					if (!exceptionReason.equals("")) {
						j.setMsg("处理意见不是检测异常时，异常原因不能填写！");
						return j;
					}
					if (!detectException.equals("")) {
						j.setMsg("处理意见不是检测异常时，检测异常不能勾选！");
						return j;
					}
				}
				
				StringBuffer quoteItemBuf = new StringBuffer();
				
				if (StringUtil.convertNull(quoteItem).trim().equals("") && !StringUtil.convertNull(quote).trim().equals("")) {
					j.setMsg("报价项为空，报价也得为空！");
					return j;
				} else if (!StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
					j.setMsg("报价为空，报价项也得为空！");
					return j;
				} else if (StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
				} else {
					quoteItemBuf.append(StringUtil.convertNull(quoteItem).trim()).append(" ").append(StringUtil.convertNull(quote).trim()).append("\n");
				}
				if (quoteItemAdd != null) {
					for (int i = 0; i < quoteItemAdd.length; i ++) {
						if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && !StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							j.setMsg("报价项为空，报价也得为空！");
							return j;
						} else if (!StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							j.setMsg("报价为空，报价项也得为空！");
							return j;
						} else if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							continue;
						} else {
							String [] quoteItemstr = quoteItemAdd[i].split(",");
							String [] quotestr = quoteAdd[i].split(",");
							
							if(quoteItemstr.length != quotestr.length){
								j.setMsg("报价项与报价个数不匹配！");
								return j;
							}
							for (int k = 0; k < quoteItemstr.length; k++) {
								quoteItemBuf.append(StringUtil.convertNull(quoteItemstr[k]).trim()).append(" ").append(quotestr[k].trim()).append("\n");
							}
						}
					}
				}
				//处理结果为换新机时，报价项、报价为必填项
				if(handle.equals(handle.equals(AfterSaleDetectProductBean.HANDLE9))){
					if(quoteItemBuf.length()<=0){
						j.setMsg("换新机时报价项和报价为必填项!");
						return j;
					}
				}
				
				//配件id
				String[] fittingIdArray = null;
				if(fittingIds!=null && fittingIds.length()>0){
					fittingIdArray = fittingIds.split(",");
				}
				
				//完好配件
				StringBuffer intactFittingBuf = null;
				String[] intactFittingCountArray = null; 
				String[] fittingNameArray = null;
				if(fittingNames != null && fittingNames.length()>0){
					intactFittingBuf = new StringBuffer();
					fittingNameArray = fittingNames.split(",");
					intactFittingCountArray = intactFittingCounts.split(",");
					for(int i=0;i<intactFittingCountArray.length;i++){
						int count = StringUtil.StringToId(intactFittingCountArray[i].trim());
						if(count>0){
							intactFittingBuf.append(fittingNameArray[i].trim()).append(" (")
							.append(count).append(")\n");
						}
					}
				}
				//损坏配件
				StringBuffer badFittingBuf = null;
				String[] badFittingCountArray = null; 
				if(fittingNameArray != null && fittingNameArray.length>0){
					badFittingBuf = new StringBuffer();
					badFittingCountArray = badFittingCounts.split(",");
					for(int i=0;i<badFittingCountArray.length;i++){
						int count = StringUtil.toInt(badFittingCountArray[i].trim());
						if(count>0){
							badFittingBuf.append(StringUtil.convertNull(fittingNameArray[i]).trim()).append(" (")
							.append(count).append(")\n");
						}
					}
				}
				//售后处理单
				AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id + " and after_sale_detect_package_id=" + afterSaleDetectPackageId + " and after_sale_order_id=" + afterSaleOrderId);
				if (detectProductBean == null) {
					j.setMsg("售后处理单不存在！");
					return j;
				}			
				
				if(handle.equals(handle.equals(AfterSaleDetectProductBean.HANDLE9))){
					ProductStockBean ps =psService.getProductStock(" product_id="+detectProductBean.getProductId()+" and area= "+detectProductBean.getAreaId() +" and type = "+ProductStockBean.STOCKTYPE_SPARE);
					if(ps==null){
						j.setMsg("此商品对应的备用机库存不存在！");
						return j;
					}
					if(ps.getStock()<=0){
						j.setMsg("此sku的备用机当前地区库存为0，不能进行换新机！");
						return j;
					}
				}
//				CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProductBean.getCargoWholeCode() +"'");
//				if(cargo==null){
//					j.setMsg("售后处理单商品对应的货位不存在！");
//					return j;
//				}
				//判断是否正在盘点
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK
						+ " and stock_area=" + detectProductBean.getAreaId());
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				
				//签收包裹单
				AfterSaleDetectPackageBean packageBean = afStockService.getAfterSaleDetectPackage("id=" + afterSaleDetectPackageId);
				if (packageBean == null) {
					j.setMsg("签收包裹单不存在！");
					return j;
				}
				
				if (packageBean.getStatus() != AfterSaleDetectPackageBean.STATUS1) {
					j.setMsg("签收包裹单状态不是已匹配！");
					return j;
				}
				//售后单
				AfterSaleOrderBean saleOrderBean = asoService.getAfterSaleOrder("id=" + afterSaleOrderId);
				if (saleOrderBean == null) {
					j.setMsg("售后单不存在！");
					return j;
				}
				
				if (saleOrderBean.getStatus() != AfterSaleOrderBean.STATUS_售后检测中) {
					j.setMsg("售后单状态不是检测中！");
					return j;
				}
				
				if(!afStockService.checkAfterSaleUserGroup(user, detectProductBean.getAreaId())){
					j.setMsg("没有该处理单所属地区售后仓内作业权限!");
					return j;
				}
				
				if (detectProductBean.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
					j.setMsg("此售后处理单"+detectProductBean.getCode()+"已锁定!");
					return j;
				}
				
				if (detectProductBean.getStatus() != AfterSaleDetectProductBean.STATUS0 && detectProductBean.getStatus() != AfterSaleDetectProductBean.STATUS1) {
					j.setMsg("售后处理单已进行过检测！");
					return j;
				}
				
				afStockService.getDbOp().startTransaction();
				//我司仓发货的订单，检测需要验证imei码；非我司订单不需要对imei码进行相关校验(已废弃2014-11-10)
				//2014-11-10 imei码的都需要验证不区分是否我司仓
				//imei码相关校验
				boolean hasIMEI = imeiService.isProductMMBMobile(detectProductBean.getProductId());
				if (hasIMEI) {
					if (IMEI.trim().equals("")) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("IMEI码商品必须填写IMEI码！");
						return j;
					} else {
						IMEIBean imei = imeiService.getIMEI("code = '" + IMEI.trim() + "'");
						if (imei == null) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("输入的IMEI码不存在！");
							return j; 
						} else {
							IMEIUserOrderBean imeiUOBean = imeiService.getIMEIUserOrder("order_id=" + saleOrderBean.getOrderId() + " and imei_code='" + IMEI.trim()+ "' and product_id=" + detectProductBean.getProductId());
							if (imeiUOBean == null) {
								if (imei.getStatus() == IMEIBean.IMEISTATUS2) {
									afStockService.getDbOp().rollbackTransaction();
									j.setMsg("该IMEI码状态可出库，并且未与当前订单关联，请联系物流处理！");
									return j; 
								} else if (imei.getStatus() != IMEIBean.IMEISTATUS3) {
									afStockService.getDbOp().rollbackTransaction();
									j.setMsg("IMEI码状态不对！");
									return j; 
								} else {
									int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id="+ saleOrderBean.getId() + " and IMEI='" + IMEI.trim() + "'");
									if (count > 0) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("该售后单已存在输入的IMEI码！");
										return j; 
									}
										
									if (!handle.equals(AfterSaleDetectProductBean.HANDLE8)) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("该IMEI码已出库，但是未与当前订单关联，建议原品返回！");
										return j; 
									}
									j.setMsg("操作成功，该IMEI码已出库，但是未与当前订单关联，建议原品返回！");
									msgStatus = true;
								}
							} else {
								if (imei.getStatus() == IMEIBean.IMEISTATUS2) {
									if (!imeiService.updateIMEI("status= " + IMEIBean.IMEISTATUS3, "code='" + IMEI.trim() + "'")) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("修改IMEI码状态失败！");
										return j;
									}
									IMEILogBean imeiLog = new IMEILogBean();
									imeiLog.setIMEI(IMEI.trim());
									imeiLog.setOperCode("");
									imeiLog.setOperType(IMEILogBean.OPERTYPE9);
									imeiLog.setCreateDatetime(DateUtil.getNow());
									imeiLog.setContent("售后判断，imei码关联了订单，但状态为可出库，修改状态由可出库变成已出库"+",地区："+ProductStockBean.areaMap.get(1));
									imeiLog.setUserId(user.getId());
									imeiLog.setUserName(user.getUsername());
										
									if (!imeiService.addIMEILog(imeiLog)) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("添加IMEI日志失败！");
										return j;
									}
									j.setMsg("操作成功，IMEI码状态错误，已修改为“已出库”状态，如有问题请联系技术部！");
									msgStatus = true;
								} else if (imei.getStatus() != IMEIBean.IMEISTATUS3) {
									afStockService.getDbOp().rollbackTransaction();
									j.setMsg("IMEI码状态不对！");
									return j; 
								} else {
									int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id="+ saleOrderBean.getId() + " and IMEI='" + IMEI.trim() + "'");
									if (count > 0) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("该售后单已存在输入的IMEI码！");
										return j; 
									}
								}
							}
						}
							
					}
				} else {
					if (!IMEI.trim().equals("")) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("非IMEI码商品，不能输入IMEI码！");
						return j; 
					}
				}
				
				//判断售后处理单号是否存在
				AfterSaleDetectProductCodeBean detectProductCodeBean = afStockService.getAfterSaleDetectProductCode("code='" + StringUtil.toSql(afterSaleDetectProductCode) + "'");
				if (detectProductCodeBean == null) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("该售后处理单号不存在！");
					return j; 
				}
				//判断售后处理单号是否已使用
				AfterSaleDetectProductBean detectBean = afStockService.getAfterSaleDetectProduct("code='" + StringUtil.toSql(afterSaleDetectProductCode) + "'");
				if (detectBean != null) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("该售后处理单号已使用！");
					return j; 
				}
				
				detectProductBean.setCode(afterSaleDetectProductCode);
				
				CargoInfoBean cib = null;
				CargoInfoBean intactCargo = null;//完好配件货位
				CargoInfoBean badCargo = null;//损坏配件货位
				//直接退货的
				if (handle.equals(AfterSaleDetectProductBean.HANDLE2)) {
					//货位
					cib = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE + " and store_type=" + CargoInfoBean.STORE_TYPE2);
					if(cib==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到货位！");
						return j;
					}
					
					intactCargo = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING + " and store_type=" 
									+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE3);
					if(intactCargo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到完好配件货位！");
						return j;
					}
					
					badCargo = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE4);
					if(badCargo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到损坏配件货位！");
						return j;
					}
					//售后入库单
					AfterSaleStockin stockin = new AfterSaleStockin();
					stockin.setAfterSaleDetectProductId(detectProductBean.getId());
					stockin.setProductId(detectProductBean.getProductId());
					stockin.setOutCargoWholeCode("");
					stockin.setInCargoWholeCode(cib.getWholeCode());
					stockin.setStatus(AfterSaleStockin.STATUS1);
					stockin.setCreateUserId(user.getId());
					stockin.setCreateUserName(user.getUsername());
					stockin.setCreateDatetime(DateUtil.getNow());
					stockin.setCompleteUserId(user.getId());
					stockin.setCompleteUserName(user.getUsername());
					stockin.setCompleteDatetime(DateUtil.getNow());
					stockin.setType(AfterSaleStockin.TYPE0);
					stockin.setOrderCode(saleOrderBean.getOrderCode());
					
					if (!afStockService.addAfterSaleStockin(stockin)) {
						j.setMsg("添加售后入库单失败");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}

					if (!afStockService.updateAfterSaleDetectProduct("code='" + StringUtil.toSql(afterSaleDetectProductCode) + "',imei='" + StringUtil.toSql(IMEI) + "',cargo_whole_code='"+ cib.getWholeCode()+
							"',status=" + AfterSaleDetectProductBean.STATUS2+",create_datetime='" +DateUtil.getNow() + "',create_user_id=" + user.getId() + ",create_user_name='" + user.getUsername() + "'", "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单信息失败！");
						return j;
					}
					int afterSaleWarehourcePackageId = 0;
					ResultSet rs = afStockService.getDbOp().executeQuery("select id from after_sale_warehource_package_list where package_code='" + packageBean.getPackageCode() + "'");
					if (rs.next()) {
						afterSaleWarehourcePackageId = rs.getInt("id");
					} else {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("销售后台售后包裹信息不存在！");
						rs.close();
						return j;
					}
					rs.close();
					String insertSql = "insert into after_sale_warehource_product_records(id,code,after_sale_warehource_package_id,after_sale_order_id,product_id,in_buy_order,in_user_order," +
							"IMEI,remark,type,status,is_vanning,create_user_id,create_user_name,create_datetime,modify_user_id,modify_user_name,modify_datetime) values " +
							"(" + detectProductBean.getId() + ",'" + detectProductBean.getCode() + "'," + afterSaleWarehourcePackageId + "," + detectProductBean.getAfterSaleOrderId() + "," + detectProductBean.getProductId() + "," +
							detectProductBean.getInBuyOrder() + "," + detectProductBean.getInUserOrder() + ",'" + StringUtil.toSql(IMEI) + "','" + detectProductBean.getRemark() + "',1,2,0," + user.getId() + ",'" +
							user.getUsername() + "','" + DateUtil.getNow() + "'," + user.getId() + ",'" + user.getUsername() + "','" + DateUtil.getNow() + "');";
					
					if (!afStockService.getDbOp().executeUpdate(insertSql)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台售后处理单信息失败！");
						return j;
					}
					
					if( hasIMEI ) {
						if (!imeiService.updateIMEI("status= " + IMEIBean.IMEISTATUS2, "code='" + IMEI.trim() + "'")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("修改IMEI码状态失败！");
							return j;
						}
						
						IMEILogBean imeiLog = new IMEILogBean();
						imeiLog.setIMEI(IMEI.trim());
						imeiLog.setOperCode("");
						imeiLog.setOperType(IMEILogBean.OPERTYPE8);
						imeiLog.setCreateDatetime(DateUtil.getNow());
						imeiLog.setContent("由已出库变成可出库");
						imeiLog.setUserId(user.getId());
						imeiLog.setUserName(user.getUsername());
						
						if (!imeiService.addIMEILog(imeiLog)) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加IMEI日志失败！");
							return j;
						}
						
					}
					//修改订单信息
					String s = afStockService.updateOrderAfterSale(saleOrderBean.getOrderId(), afStockService.getDbOp(), user);
					if ( s != null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg(s);
						return j;
					}
				} else if (handle.equals(AfterSaleDetectProductBean.HANDLE7)){
					//货位
					cib = cargoService.getCargoInfo("area_id=" + detectProductBean.getAreaId() +" and stock_type="+CargoInfoBean.STOCKTYPE_CUSTOMER + " and store_type=" + CargoInfoBean.STORE_TYPE2);
					if(cib==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到货位！");
						return j;
					}
					
					intactCargo = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE3);
					if(intactCargo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到完好配件货位！");
						return j;
					}
					
					badCargo = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE4);
					if(badCargo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到损坏配件货位！");
						return j;
					}
					
					if (!afStockService.updateAfterSaleDetectProduct("code='" + StringUtil.toSql(afterSaleDetectProductCode) + "',imei='" + StringUtil.toSql(IMEI) + "',cargo_whole_code='"+ cib.getWholeCode()+
							"',status=" + AfterSaleDetectProductBean.STATUS4 + ",create_datetime='" +DateUtil.getNow() + "',create_user_id=" + user.getId() + ",create_user_name='" + user.getUsername() + "'", "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单信息失败！");
						return j;
					}
					
					//返厂商品信息
					AfterSaleBackSupplierProduct backSupplierProduct = new AfterSaleBackSupplierProduct();
					backSupplierProduct.setAfterSaleDetectProductId(detectProductBean.getId());
					backSupplierProduct.setProductId(detectProductBean.getProductId());
					backSupplierProduct.setGuarantee(AfterSaleBackSupplierProduct.GUARANTEE1);
					backSupplierProduct.setUserId(user.getId());
					backSupplierProduct.setUserName(user.getUsername());
					backSupplierProduct.setCreateDatetime(DateUtil.getNow());
					backSupplierProduct.setStatus(AfterSaleBackSupplierProduct.STATUS3);
					
					if (!afStockService.addAfterSaleBackSupplierProduct(backSupplierProduct)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加返厂商品信息失败！");
						return j;
					}
					
					int afterSaleWarehourcePackageId = 0;
					ResultSet rs = afStockService.getDbOp().executeQuery("select id from after_sale_warehource_package_list where package_code='" + packageBean.getPackageCode() + "'");
					if (rs.next()) {
						afterSaleWarehourcePackageId = rs.getInt("id");
					} else {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("销售后台售后包裹信息不存在！");
						rs.close();
						return j;
					}
					rs.close();
					
					String insertSql = "insert into after_sale_warehource_product_records(id,code,after_sale_warehource_package_id,after_sale_order_id,product_id,in_buy_order,in_user_order," +
							"IMEI,remark,type,status,is_vanning,create_user_id,create_user_name,create_datetime,modify_user_id,modify_user_name,modify_datetime) values " +
							"(" + detectProductBean.getId() + ",'" + detectProductBean.getCode() + "'," + afterSaleWarehourcePackageId + "," + detectProductBean.getAfterSaleOrderId() + "," + detectProductBean.getProductId() + "," +
							detectProductBean.getInBuyOrder() + "," + detectProductBean.getInUserOrder() + ",'" + StringUtil.toSql(IMEI) + "','" + detectProductBean.getRemark() + "',6,5,0," + user.getId() + ",'" +
							user.getUsername() + "','" + DateUtil.getNow() + "'," + user.getId() + ",'" + user.getUsername() + "','" + DateUtil.getNow() + "');";
					
					if (!afStockService.getDbOp().executeUpdate(insertSql)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台售后处理单信息失败！");
						return j;
					}
					
				} else {
					//货位库存
					cib = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+CargoInfoBean.STOCKTYPE_CUSTOMER + " and store_type=" + CargoInfoBean.STORE_TYPE2);
					if(cib==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到货位！");
						return j;
					}
					
					intactCargo = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE3);
					if(intactCargo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到完好配件货位！");
						return j;
					}
					
					badCargo = cargoService.getCargoInfo("area_id=" +  detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE4);
					if(badCargo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到损坏配件货位！");
						return j;
					}
					
					if (!afStockService.updateAfterSaleDetectProduct("code='" + StringUtil.toSql(afterSaleDetectProductCode) + "',imei='" + StringUtil.toSql(IMEI) + "',cargo_whole_code='"+ cib.getWholeCode()+
							"',status=" + AfterSaleDetectProductBean.STATUS2 + ",create_datetime='" +DateUtil.getNow() + "',create_user_id=" + user.getId() + ",create_user_name='" + user.getUsername() + "'", "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单信息失败！");
						return j;
					}
					
					int afterSaleWarehourcePackageId = 0;
					ResultSet rs = afStockService.getDbOp().executeQuery("select id from after_sale_warehource_package_list where package_code='" + packageBean.getPackageCode() + "'");
					if (rs.next()) {
						afterSaleWarehourcePackageId = rs.getInt("id");
					} else {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("销售后台售后包裹信息不存在！");
						rs.close();
						return j;
					}
					rs.close();
					
					Integer type = AfterSaleDetectProductBean.handleMap.get(handle);
					if (type == null ) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("该处理意见不存在！");
						return j;
					}
					
					String insertSql = "insert into after_sale_warehource_product_records(id,code,after_sale_warehource_package_id,after_sale_order_id,product_id,in_buy_order,in_user_order," +
							"IMEI,remark,type,status,is_vanning,create_user_id,create_user_name,create_datetime,modify_user_id,modify_user_name,modify_datetime) values " +
							"(" + detectProductBean.getId() + ",'" + detectProductBean.getCode() + "'," + afterSaleWarehourcePackageId + "," + detectProductBean.getAfterSaleOrderId() + "," + detectProductBean.getProductId() + "," +
							detectProductBean.getInBuyOrder() + "," + detectProductBean.getInUserOrder() + ",'" + StringUtil.toSql(IMEI) + "','" + detectProductBean.getRemark() + "'," + type + ",2,0," + user.getId() + ",'" +
							user.getUsername() + "','" + DateUtil.getNow() + "'," + user.getId() + ",'" + user.getUsername() + "','" + DateUtil.getNow() + "');";
					
					if (!afStockService.getDbOp().executeUpdate(insertSql)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台售后处理单信息失败！");
						return j;
					}
				}
				
				//操作库存
				voProduct product = wareService.getProduct(detectProductBean.getProductId());
				
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,null));
				ProductStockBean ps = psService.getProductStock("product_id=" + detectProductBean.getProductId() + " and area="+ detectProductBean.getAreaId() + " and type=" + cib.getStockType());
				if (ps == null) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("没有找到产品库存，操作失败！");
					return j;
				}
				
				CargoProductStockBean cpsb = cargoService.getCargoProductStock("product_id="+product.getId()+" and cargo_id="+cib.getId());
				if (cpsb == null) {
					cpsb = new CargoProductStockBean();
					cpsb.setCargoId(cib.getId());
					cpsb.setProductId(product.getId());
					cpsb.setStockCount(0);
					cpsb.setStockLockCount(0);
					if(!cargoService.addCargoProductStock(cpsb)){
						j.setMsg("数据库操作失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					cpsb.setId(service.getDbOp().getLastInsertId());
				}
				
				if (!psService.updateProductStockCount(ps.getId(), 1)) {
					j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
					afStockService.getDbOp().rollbackTransaction();
					return j;
				}
				if(!cargoService.updateCargoProductStockCount(cpsb.getId(), 1)){
					j.setMsg("货位库存操作失败，货位库存不足！");
					afStockService.getDbOp().rollbackTransaction();
					return j;
				}
				AfterSaleOrderBean afterSaleOrder = afStockService.getAfterSaleOrder("id=" + detectProductBean.getAfterSaleOrderId());
				if(afterSaleOrder == null){
					j.setMsg("售后单不存在！");
					afStockService.getDbOp().rollbackTransaction();
					return j;
				}
				float afterSalePrice = product.getPrice5(); 
				float price5 = product.getPrice5();
				voOrderProduct orderProduct = wareService.getOrderProductSplit(afterSaleOrder.getOrderId(), product.getCode());
				if(orderProduct == null){
					orderProduct = wareService.getOrderPresentSplit(afterSaleOrder.getId(), product.getCode());
				}
				//出库库存价丢失补救****
				if(orderProduct != null&&orderProduct.getPrice3() == 0){
					//获取出货前最后一条进销存记录
					int outId = service.getNumber("id", "stock_card", null, "code = '"+ afterSaleOrder.getOrderCode() +"' and card_type = "+StockCardBean.CARDTYPE_ORDERSTOCK+" and product_id = " + product.getId());
					int scId = service.getNumber("id", "stock_card", "max", "id < "+outId+" and product_id = "+ product.getId());
					StockCardBean stockCard = psService.getStockCard("id = "+scId);
					orderProduct.setPrice3(stockCard.getStockPrice());
				}
				if(orderProduct != null){
					int totalCount = product.getStockAll() + product.getLockCountAll();
					//StockBatchLogBean batchLog = service.getStockBatchLog("code='"+ afterSaleOrder.getOrderCode() +"' and product_id="+orderProduct.getProductId());
					price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * 1)) / (totalCount + 1) * 1000))/1000;
					afterSalePrice = orderProduct.getPrice3();
				} 
				//记录进销存卡片
				//入库
				StockCardBean sc = new StockCardBean();

				sc.setCardType(StockCardBean.CARDTYPE_AFTERSALESDETECT);
				sc.setCode(detectProductBean.getCode());

				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(cib.getStockType());
				sc.setStockArea(detectProductBean.getAreaId());
				sc.setProductId(detectProductBean.getProductId());
				sc.setStockId(ps.getId());
				sc.setStockInCount(1);
				sc.setStockInPriceSum((new BigDecimal(sc.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(afterSalePrice))).doubleValue());
				
				sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
				sc.setStockAllArea(product.getStock(sc.getStockArea()) + product.getLockCount(sc.getStockArea()));
				sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
				sc.setAllStock(product.getStockAll() + product.getLockCountAll());
				sc.setAllStock(product.getStockAll() + product.getLockCountAll());
				sc.setStockPrice(price5);
				sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				
				if(!psService.addStockCard(sc)){
					j.setMsg("进销存记录添加失败，请重新尝试操作！");
					afStockService.getDbOp().rollbackTransaction();
					return j;
				}
				
				//货位入库卡片
				CargoStockCardBean csc = new CargoStockCardBean();
				csc.setCardType(CargoStockCardBean.CARDTYPE_AFTERSALESDETECT);
				csc.setCode(detectProductBean.getCode());
				csc.setCreateDatetime(DateUtil.getNow());
				csc.setStockType(cib.getStockType());
				csc.setStockArea(detectProductBean.getAreaId());
				csc.setProductId(detectProductBean.getProductId());
				csc.setStockId(cpsb.getId());
				csc.setStockInCount(1);
				csc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
				csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
				csc.setAllStock(product.getStockAll() + product.getLockCountAll());
				csc.setCurrentCargoStock(cpsb.getStockCount()+cpsb.getStockLockCount());
				csc.setCargoStoreType(cib.getStoreType());
				csc.setCargoWholeCode(cib.getWholeCode());
				csc.setStockPrice(product.getPrice5());
				csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
				if(!cargoService.addCargoStockCard(csc)){
					j.setMsg("货位进销存记录添加失败，请重新尝试操作！");
					afStockService.getDbOp().rollbackTransaction();
					return j;
				}
				
				if (cib.getStockType() != CargoInfoBean.STOCKTYPE_CUSTOMER) {
					// 查询销售订单
					voOrder vOrder = wareService.getOrder(saleOrderBean.getOrderId());
					if (vOrder == null) {
						j.setMsg("售后处理单所关联的订单不存在");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					// 修改库存批次
					CommonLogic c = new CommonLogic();
					String result = c.updateStockBatchForAfterSaleStockin(detectProductBean.getAreaId(),afStockService.getDbOp(), product.getId(), detectProductBean.getAfterSaleOrderCode(), vOrder, user);
					if (result != null) {
						j.setMsg(result);
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
				}
				
				//完好配件入库操作
				if(fittingIdArray!=null && fittingIdArray.length>0){
					for(int i=0;i<fittingIdArray.length;i++){
						int count = StringUtil.parstInt(intactFittingCountArray[i]);
						if(count>0){
							int stockCardType = StockCardBean.CARDTYPE_AFTERSALE_FITTING;
							int cargoStockCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_FITTING;
							if(intactCargo.getStockType()==CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING){
								stockCardType = StockCardBean.CARDTYPE_CUSTOMER_FITTING;
								cargoStockCardType = CargoStockCardBean.CARDTYPE_CUSTOMER_FITTING;
							}
							int fittingId = StringUtil.StringToId(fittingIdArray[i]);
							j = operateFittingStock(user,wareService, afStockService, cargoService, service, psService, fittingId,
									stockCardType,cargoStockCardType,detectProductBean, saleOrderBean, intactCargo, count);
							if(j.getMsg()!=null && j.getMsg().length()>0){
								return j;
							}
						}
					}	
				}
				//损坏配件入库操作
				if(fittingIdArray!=null && fittingIdArray.length>0){
					for(int i=0;i<fittingIdArray.length;i++){
						int count = StringUtil.parstInt(badFittingCountArray[i]);
						if(count>0){
							int stockCardType = StockCardBean.CARDTYPE_AFTERSALE_FITTING;
							int cargoStockCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_FITTING;
							if(badCargo.getStockType()==CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING){
								stockCardType = StockCardBean.CARDTYPE_CUSTOMER_FITTING;
								cargoStockCardType = CargoStockCardBean.CARDTYPE_CUSTOMER_FITTING;
							}
							int fittingId = StringUtil.StringToId(fittingIdArray[i]);
							j = operateFittingStock(user,wareService, afStockService, cargoService, service, psService, fittingId,
									stockCardType,cargoStockCardType,detectProductBean, saleOrderBean, badCargo, count);
							if(j.getMsg()!=null && j.getMsg().length()>0){
								return j;
							}
						}
					}	
				}
				//检测信息添加
				AfterSaleDetectLogBean asdLog = new AfterSaleDetectLogBean();
				asdLog.setAfterSaleDetectProductId(detectProductBean.getId());
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.QUESTION_DESCRIPTION);
				String questionDescriptions[] = questionDescription.split("/"); 
				if(questionDescriptions.length==1){
					asdLog.setContent(questionDescriptions[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(questionDescriptions.length==2){
					asdLog.setContent(questionDescriptions[0]);
					asdLog.setContent2(questionDescriptions[1]);
					asdLog.setContent3("");
				}
				if(questionDescriptions.length==3){
					asdLog.setContent(questionDescriptions[0]);
					asdLog.setContent2(questionDescriptions[1]);
					asdLog.setContent3(questionDescriptions[2]);
				}
				
				asdLog.setCreateDatetime(DateUtil.getNow());
				asdLog.setUserId(user.getId());
				asdLog.setUserName(user.getUsername());
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加问题分类失败！");
					return j;
				}
				
				int asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + questionDescription.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台问题分类失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.DAMAGED);
				String damageds[] = damaged.split("/");
				if(damageds.length==1){
					asdLog.setContent(damageds[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(damageds.length==2){
					asdLog.setContent(damageds[0]);
					asdLog.setContent2(damageds[1]);
					asdLog.setContent3("");
				}
				if(damageds.length==3){
					asdLog.setContent(damageds[0]);
					asdLog.setContent2(damageds[1]);
					asdLog.setContent3(damageds[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加包装失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
					
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + damaged.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台包装失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.GIFT_ALL);
				String giftAlls[] = giftAll.split("/");
				if(giftAlls.length==1){
					asdLog.setContent(giftAlls[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(giftAlls.length==2){
					asdLog.setContent(giftAlls[0]);
					asdLog.setContent2(giftAlls[1]);
					asdLog.setContent3("");
				}
				if(giftAlls.length==3){
					asdLog.setContent(giftAlls[0]);
					asdLog.setContent2(giftAlls[1]);
					asdLog.setContent3(giftAlls[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加赠品失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + giftAll.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台赠品失败！");
					return j;
				}
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.FAULT_DESCRIPTION);
				StringBuffer faultDescriptionContents = new StringBuffer();
				if(faultDescriptionIds.length==1){
					asdLog.setContent(AfterSaleDetectTypeDetailContents[0]);
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(faultDescriptionIds.length==2){
					asdLog.setContent(AfterSaleDetectTypeDetailContents[0]);
					asdLog.setContent2(AfterSaleDetectTypeDetailContents[1]);
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[0]);
					faultDescriptionContents.append("/");
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[1]);
					asdLog.setContent3("");
				}
				if(faultDescriptionIds.length==3){
					asdLog.setContent(AfterSaleDetectTypeDetailContents[0]);
					asdLog.setContent2(AfterSaleDetectTypeDetailContents[1]);
					asdLog.setContent3(AfterSaleDetectTypeDetailContents[2]);
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[0]);
					faultDescriptionContents.append("/");
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[1]);
					faultDescriptionContents.append("/");
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[2]);
				}
				
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加故障描述失败！");
					return j;
				}
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + faultDescriptionContents + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台故障描述失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.REPORT_STATUS);
				String reportStatuses[] = reportStatus.split("/");
				if(reportStatuses.length==1){
					asdLog.setContent(reportStatuses[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(reportStatuses.length==2){
					asdLog.setContent(reportStatuses[0]);
					asdLog.setContent2(reportStatuses[1]);
					asdLog.setContent3("");
				}
				if(reportStatuses.length==3){
					asdLog.setContent(reportStatuses[0]);
					asdLog.setContent2(reportStatuses[1]);
					asdLog.setContent3(reportStatuses[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加申报状态失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + reportStatus.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台申报状态失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.QUOTE_ITEM);
				asdLog.setContent(quoteItemBuf.toString());
				asdLog.setContent2("");
				asdLog.setContent3("");
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加报价项失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台报价项失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.EXCEPTION_REASON);
				String exceptionReasons[] = exceptionReason.split("/");
				if(exceptionReasons.length==1){
					asdLog.setContent(exceptionReasons[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(exceptionReasons.length==2){
					asdLog.setContent(exceptionReasons[0]);
					asdLog.setContent2(exceptionReasons[1]);
					asdLog.setContent3("");
				}
				if(exceptionReasons.length==3){
					asdLog.setContent(exceptionReasons[0]);
					asdLog.setContent2(exceptionReasons[1]);
					asdLog.setContent3(exceptionReasons[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加异常原因失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + exceptionReason.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台异常原因失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.FAULT_CODE);
				asdLog.setContent(faultCode);
				asdLog.setContent2("");
				asdLog.setContent3("");
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加故障代码失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台故障代码失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.IMEI);
				asdLog.setContent(IMEI);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加IMEI失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台IMEI失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.REMARK);
				asdLog.setContent(remark);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加备注失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台备注失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.HANDLE);
				asdLog.setContent(handle);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加处理意见失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台处理意见失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.MAIN_PRODUCT_STATUS);
				asdLog.setContent(mainProductStatus);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加主商品状态失败！");
					return j;
				}
				
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加主商品状态失败！");
					return j;
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.DEBIT_NOTE);
				asdLog.setContent(debitNote);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加发票失败！");
					return j;
				}
				asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加发票失败！");
					return j;
				}
				
				if(intactFittingBuf!=null){
					asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.INTACT_FITTING);
					asdLog.setContent(intactFittingBuf.toString());
					if (!afStockService.addAfterSaleDetectLog(asdLog)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加完好配件失败！");
						return j;
					}
					
					asdlId = afStockService.getDbOp().getLastInsertId();
					
					if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加完好配件失败！");
						return j;
					}
				}
				
				if(badFittingBuf!=null){
					asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.BAD_FITTING);
					asdLog.setContent(badFittingBuf.toString());
					if (!afStockService.addAfterSaleDetectLog(asdLog)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加损坏配件失败！");
						return j;
					}
					
					asdlId = afStockService.getDbOp().getLastInsertId();
					
					if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加损坏配件失败！");
						return j;
					}
				}
				
				//需要增加检测记录、销售那边的检测记录；ps：需要新增一条type=18（换新机商品id）content字段直接写product_id（售后处理单中对应的）
				if(handle.equals(AfterSaleDetectProductBean.HANDLE9)){
					asdLog.setAfterSaleDetectProductId(detectProductBean.getId());
					asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.CHANGE);
					asdLog.setContent(detectProductBean.getProductId()+"");
					asdLog.setCreateDatetime(DateUtil.getNow());
					asdLog.setUserId(user.getId());
					asdLog.setUserName(user.getUsername());
					
					if (!afStockService.addAfterSaleDetectLog(asdLog)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加换新机商品失败！");
						return j;
					}
					asdlId = afStockService.getDbOp().getLastInsertId();
					
					if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台换新机商品失败！");
						return j;
					}
				}
				
				//添加售后处理单检测记录配件信息表
				if(fittingIdArray!=null && fittingIdArray.length>0){
					for(int i=0;i<fittingIdArray.length;i++){
						String sql = "insert into after_sale_detect_product_fitting (after_sale_detect_product_id,fitting_id,fitting_name,intact_count,damage_count) " 
								+ " values (" + detectProductBean.getId() + "," + fittingIdArray[i] + ",'" + fittingNameArray[i] + "'," 
								+ StringUtil.StringToId(intactFittingCountArray[i]) + "," + StringUtil.StringToId(badFittingCountArray[i]) + ")";
						if(!afStockService.getDbOp().executeUpdate(sql)){
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("售后处理单检测记录配件信息失败！");
							return j;
						}
					}
				}
				
				//判断该售后单是否全部检测完毕
				int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id=" + saleOrderBean.getId() + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 +")");
				if (count <= 0) {
					ResultSet rs  = afStockService.getDbOp().executeQuery("select count(asdl.id) from after_sale_detect_log asdl, after_sale_detect_product asdp where asdl.after_sale_detect_product_id=asdp.id and asdp.after_sale_order_id=" + saleOrderBean.getId() + " and asdl.after_sale_detect_type_id=" + AfterSaleDetectTypeBean.HANDLE + " and asdl.content<>'" + AfterSaleDetectProductBean.HANDLE7 + "'");
					if (rs.next()) {
						count = rs.getInt(1);
					}
					rs.close();
					//判断是否存在有保修的，售后单状态以等待客户确认优先
					if (count <= 0) {
						if (!asoService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_售后处理中, "id=" + saleOrderBean.getId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新售后单信息失败！");
							return j;
						}
					} else {
						if (!asoService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_等待客户确认, "id=" + saleOrderBean.getId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新售后单信息失败！");
							return j;
						}
					}
				}
				
				//判断包裹单是否检测完毕
				count = afStockService.getAfterSaleDetectProductCount("after_sale_detect_package_id=" + afterSaleDetectPackageId + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 +")");
				if (count <= 0) {
					if (!afStockService.updateAfterSaleDetectPackage("status=" + AfterSaleDetectPackageBean.STATUS3, "id=" + afterSaleDetectPackageId)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新签收包裹状态失败！");
						return j;
					}
				}
				//添加售后仓内作业日志-检测
				String content = "检测售后单[" + detectProductBean.getAfterSaleOrderCode() + "]商品，生成售后处理单[" + detectProductBean.getCode() + "]," +
						"售后处理单状态[等待客户确认],处理意见[" + handle + "]";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE4,detectProductBean.getCode(),null)){
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加售后仓内作业日志-检测失败！");
					return j;
				}
				//记录一条签收时间的日志
				AfterSaleLogBean saleLogSign = afStockService.getAfterSaleLog("oper_code='" + packageBean.getPackageCode() + "' and type=" + AfterSaleLogBean.TYPE1);
				if(saleLogSign != null){
					if(!afStockService.writeAfterSaleLog(user, saleLogSign.getContent(), 1, AfterSaleLogBean.TYPE1,detectProductBean.getCode(),saleLogSign.getCreateDatetime())){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加售后仓内作业日志(签收时间)-检测失败！");
						return j;
					}
				}
				//记录一条匹配时间的日志
				AfterSaleLogBean saleLogMatch = afStockService.getAfterSaleLog("oper_code='" + packageBean.getPackageCode() + "' and type=" + AfterSaleLogBean.TYPE13);
				if(saleLogMatch != null){
					if(!afStockService.writeAfterSaleLog(user, saleLogMatch.getContent(), 1, AfterSaleLogBean.TYPE13,detectProductBean.getCode(),saleLogMatch.getCreateDatetime())){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加售后仓内作业日志(匹配时间)-检测失败！");
						return j;
					}
				}
				//记录一条匹配失败时间的日志
				AfterSaleLogBean saleLogMatchFail = afStockService.getAfterSaleLog("oper_code='" + packageBean.getPackageCode() + "' and type=" + AfterSaleLogBean.TYPE18);
				if(saleLogMatchFail != null){
					if(!afStockService.writeAfterSaleLog(user, saleLogMatchFail.getContent(), 1, AfterSaleLogBean.TYPE18,detectProductBean.getCode(),saleLogMatchFail.getCreateDatetime())){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加售后仓内作业日志(匹配时间)-检测失败！");
						return j;
					}
				}
				afStockService.getDbOp().commitTransaction();
				if (!msgStatus) {
					j.setMsg("检测完成！");
				}
				j.setSuccess(true);
				j.setObj(detectProductBean.getCode());
				return j;
			}
		} catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("检测异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 操作配件库存
	 * @param user
	 * @param j
	 * @param wareService
	 * @param afStockService
	 * @param cargoService
	 * @param service
	 * @param psService
	 * @param fittingIdArray
	 * @param detectProductBean
	 * @param saleOrderBean
	 * @param intactCargo
	 * @param product
	 * @param i
	 * @param count
	 * @author lining
	* @date 2014-7-7
	 */
	private Json operateFittingStock(voUser user, WareService wareService, AfStockService afStockService,ICargoService cargoService, 
			IStockService service, IProductStockService psService,int fittingId,int stockCardType,int cargoStockCardType,
			AfterSaleDetectProductBean detectProductBean, AfterSaleOrderBean saleOrderBean, CargoInfoBean cargo,int count) {
		Json j = new Json();
		//操作库存
		voProduct fitting = wareService.getProduct(fittingId);
		
		fitting.setPsList(psService.getProductStockList("product_id=" + fittingId, -1, -1,null));
		ProductStockBean fittingPs = psService.getProductStock("product_id=" + fittingId + " and area="
				+ detectProductBean.getAreaId() + " and type=" + cargo.getStockType());
		if (fittingPs == null) {
			afStockService.getDbOp().rollbackTransaction();
			j.setMsg("没有找到产品库存，操作失败！");
			return j;
		}
		
		CargoProductStockBean fittingCargoPs = cargoService.getCargoProductStock("product_id="+fittingId+" and cargo_id="+cargo.getId());
		if (fittingCargoPs == null) {
			fittingCargoPs = new CargoProductStockBean();
			fittingCargoPs.setCargoId(cargo.getId());
			fittingCargoPs.setProductId(fittingId);
			fittingCargoPs.setStockCount(0);
			fittingCargoPs.setStockLockCount(0);
			if(!cargoService.addCargoProductStock(fittingCargoPs)){
				j.setMsg("数据库操作失败！");
				afStockService.getDbOp().rollbackTransaction();
				return j;
			}
			fittingCargoPs.setId(service.getDbOp().getLastInsertId());
		}
		
		if (!psService.updateProductStockCount(fittingPs.getId(), count)) {
			j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
			afStockService.getDbOp().rollbackTransaction();
			return j;
		}
		if(!cargoService.updateCargoProductStockCount(fittingCargoPs.getId(), count)){
			j.setMsg("货位库存操作失败，货位库存不足！");
			afStockService.getDbOp().rollbackTransaction();
			return j;
		}
		
		//记录进销存卡片
		//入库
		StockCardBean fittingStockCard = new StockCardBean();

		fittingStockCard.setCardType(stockCardType);
		fittingStockCard.setCode(detectProductBean.getCode());

		fittingStockCard.setCreateDatetime(DateUtil.getNow());
		fittingStockCard.setStockType(cargo.getStockType());
		fittingStockCard.setStockArea(detectProductBean.getAreaId());
		fittingStockCard.setProductId(fittingId);
		fittingStockCard.setStockId(fittingPs.getId());
		fittingStockCard.setStockInCount(count);
		fittingStockCard.setStockInPriceSum((new BigDecimal(fittingStockCard.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(fitting.getPrice5()))).doubleValue());
		fittingStockCard.setCurrentStock(fitting.getStock(fittingStockCard.getStockArea(), fittingStockCard.getStockType())
				+ fitting.getLockCount(fittingStockCard.getStockArea(), fittingStockCard.getStockType()));
		fittingStockCard.setStockAllArea(fitting.getStock(fittingStockCard.getStockArea())
				+ fitting.getLockCount(fittingStockCard.getStockArea()));
		fittingStockCard.setStockAllType(fitting.getStockAllType(fittingStockCard.getStockType())
				+ fitting.getLockCountAllType(fittingStockCard.getStockType()));
		fittingStockCard.setAllStock(fitting.getStockAll() + fitting.getLockCountAll());
		fittingStockCard.setStockPrice(fitting.getPrice5());
		fittingStockCard.setAllStockPriceSum((new BigDecimal(fittingStockCard.getAllStock())).multiply(
				new BigDecimal(StringUtil.formatDouble2(fittingStockCard.getStockPrice()))).doubleValue());
		if(!psService.addStockCard(fittingStockCard)){
			j.setMsg("进销存记录添加失败，请重新尝试操作！");
			afStockService.getDbOp().rollbackTransaction();
			return j;
		}
		
		//货位入库卡片
		CargoStockCardBean fittingCargoStockCard = new CargoStockCardBean();
		fittingCargoStockCard.setCardType(cargoStockCardType);
		fittingCargoStockCard.setCode(detectProductBean.getCode());
		fittingCargoStockCard.setCreateDatetime(DateUtil.getNow());
		fittingCargoStockCard.setStockType(cargo.getStockType());
		fittingCargoStockCard.setStockArea(detectProductBean.getAreaId());
		fittingCargoStockCard.setProductId(fittingId);
		fittingCargoStockCard.setStockId(fittingCargoPs.getId());
		fittingCargoStockCard.setStockInCount(count);
		fittingCargoStockCard.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(fitting.getPrice5()))).doubleValue());
		fittingCargoStockCard.setCurrentStock(fitting.getStock(fittingCargoStockCard.getStockArea(), fittingCargoStockCard.getStockType()) + fitting.getLockCount(fittingCargoStockCard.getStockArea(), fittingCargoStockCard.getStockType()));
		fittingCargoStockCard.setAllStock(fitting.getStockAll() + fitting.getLockCountAll());
		fittingCargoStockCard.setCurrentCargoStock(fittingCargoPs.getStockCount()+fittingCargoPs.getStockLockCount());
		fittingCargoStockCard.setCargoStoreType(cargo.getStoreType());
		fittingCargoStockCard.setCargoWholeCode(cargo.getWholeCode());
		fittingCargoStockCard.setStockPrice(fitting.getPrice5());
		fittingCargoStockCard.setAllStockPriceSum((new BigDecimal(fittingCargoStockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(fittingCargoStockCard.getStockPrice()))).doubleValue());
		if(!cargoService.addCargoStockCard(fittingCargoStockCard)){
			j.setMsg("货位进销存记录添加失败，请重新尝试操作！");
			afStockService.getDbOp().rollbackTransaction();
			return j;
		}
		
		if (cargo.getStockType() != CargoInfoBean.STOCKTYPE_CUSTOMER) {
			// 查询销售订单
			voOrder vOrder = wareService.getOrder(saleOrderBean.getOrderId());
			if (vOrder == null) {
				j.setMsg("售后处理单所关联的订单不存在");
				afStockService.getDbOp().rollbackTransaction();
				return j;
			}
			// 修改库存批次
			CommonLogic c = new CommonLogic();
			String result = c.updateStockBatchForAfterSaleStockin(detectProductBean.getAreaId(),afStockService.getDbOp(), fitting.getId(), detectProductBean.getAfterSaleOrderCode(), vOrder, user);
			if (result != null) {
				j.setMsg(result);
				afStockService.getDbOp().rollbackTransaction();
				return j;
			}
		}
		return j;
	}
	
	/**
	 * 未妥投包裹列表
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getUnCastPackageDataGrid")
	@ResponseBody
	public EasyuiDataGridJson getUnCastPackageDataGrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String packageCode, String startTime, String endTime, String afterSaleDetectProductCode,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		DbOperation dbOp2 = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService2 = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp2);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append("status=" + AfterSaleBackUserPackage.STATUS2);
			if(!"".equals(StringUtil.convertNull(packageCode))) {
				buff.append(" and package_code = '" + StringUtil.toSql(packageCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(areaId))) {
				buff.append(" and area_id = " + StringUtil.toSql(areaId));
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and receive_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and receive_datetime<='" + endTime + " 23:59:59'");
			}
			if(!"".equals(StringUtil.convertNull(afterSaleDetectProductCode))) {
				buff.append(" and exists(select 1 from after_sale_detect_product asdp,after_sale_back_user_product asb where asdp.id=asb.after_sale_detect_product_id and asb.package_id=asbup.id and asdp.code='" + StringUtil.toSql(afterSaleDetectProductCode) + "')");
			}
			String query = "select id,package_code,receive_datetime, receive_user_name from after_sale_back_user_package asbup where " + buff.toString();
			query = DbOperation.getPagingQuery(query, (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			ResultSet trs = afStockService.getDbOp().executeQuery(query);
			List<AfterSaleBackUserPackage> maps = new ArrayList<AfterSaleBackUserPackage>();
			AfterSaleBackUserPackage asdupBean = null;
			while (trs.next()) {
				asdupBean = new AfterSaleBackUserPackage();
				asdupBean.setId(trs.getInt(1));
				asdupBean.setPackageCode(trs.getString(2));
				asdupBean.setReceiveDatetime(trs.getString(3));
				asdupBean.setReceiveUserName(trs.getString(4));
				String afterSaleOrderCodes = "";
				ResultSet rs = afStockService2.getDbOp().executeQuery("select distinct asdp.after_sale_order_code from after_sale_detect_product asdp,after_sale_back_user_product asdup where asdp.id=asdup.after_sale_detect_product_id and asdup.package_id=" + asdupBean.getId());
				while (rs.next()) {
					if (afterSaleOrderCodes.equals("")) {
						afterSaleOrderCodes = rs.getString(1);
					} else {
						afterSaleOrderCodes += "," + rs.getString(1);
					}
				}
				rs.close();
				asdupBean.setAfterSaleOrderCodes(afterSaleOrderCodes);
				String afterSaleDetectProductCodes = "";
				ResultSet rs2 = afStockService2.getDbOp().executeQuery("select distinct asdp.code from after_sale_detect_product asdp,after_sale_back_user_product asdup where asdp.id=asdup.after_sale_detect_product_id and asdup.package_id=" + asdupBean.getId());
				while (rs2.next()) {
					if (afterSaleDetectProductCodes.equals("")) {
						afterSaleDetectProductCodes = rs2.getString(1);
					} else {
						afterSaleDetectProductCodes += "," + rs2.getString(1);
					}
				}
				rs2.close();
				asdupBean.setAfterSaleDetectProductCodes(afterSaleDetectProductCodes);
				maps.add(asdupBean);
			}
			trs.close();
			
			ResultSet rst = afStockService.getDbOp().executeQuery("select count(*) from after_sale_back_user_package asbup where " + buff.toString());
			if (rst.next()) {
				datagrid.setTotal((long)rst.getInt(1));
			}
			rst.close();
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
			dbOp2.release();
		}
		return datagrid;
	}
	
	/**
	 * 已检测商品列表
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDetectProductsDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getDetectProductsDatagrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String packageCode, String startTime, String endTime, String afterSaleOrderCode, String productCode, String createUserName,String parentId1) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.convertNull(packageCode))) {
				buff.append(" and aspack.package_code = '" + StringUtil.toSql(packageCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(afterSaleOrderCode))) {
				buff.append(" and asdp.after_sale_order_code = '" + StringUtil.toSql(afterSaleOrderCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(productCode))) {
				buff.append(" and p.code = '" + StringUtil.toSql(productCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(createUserName))) {
				buff.append(" and asdp.create_user_name = '" + StringUtil.toSql(createUserName) +"'");
			}
			if(!"".equals(StringUtil.convertNull(parentId1))) {
				buff.append(" and p.parent_id1 = " + StringUtil.toSql(parentId1));
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime<='" + endTime + " 23:59:59'");
			}
			buff.append(" and asdp.status<>" + AfterSaleDetectProductBean.STATUS0 + " and asdp.status <>" + AfterSaleDetectProductBean.STATUS1 + " order by asdp.id desc");
			String query = "select asdp.id,p.code productCode,p.name productName,asdp.code,asdp.after_sale_order_code,aspack.package_code,asdp.create_datetime,asdp.create_user_name,asdp.after_sale_order_id,c.name " +
							"from after_sale_detect_product asdp,after_sale_detect_package aspack, product p,catalog c where asdp.after_sale_detect_package_id=aspack.id and p.parent_id1 = c.id and asdp.product_id=p.id " + buff.toString();
			query = DbOperation.getPagingQuery(query, (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			ResultSet rs = afStockService.getDbOp().executeQuery(query);
			List<AfterSaleDetectProductBean> maps = new ArrayList<AfterSaleDetectProductBean>();
			AfterSaleDetectProductBean asdpBean = null;
			while (rs.next()) {
				asdpBean = new AfterSaleDetectProductBean();
				asdpBean.setId(rs.getInt(1));
				asdpBean.setProductCode(rs.getString(2));
				asdpBean.setProductName(rs.getString(3));
				asdpBean.setCode(rs.getString(4));
				asdpBean.setAfterSaleOrderCode(rs.getString(5));
				asdpBean.setPackageCode(rs.getString(6));
				asdpBean.setCreateDatetime(rs.getString(7));
				asdpBean.setCreateUserName(rs.getString(8));
				asdpBean.setAfterSaleOrderId(rs.getInt(9));
				asdpBean.setParentId1Name(rs.getString(10));
				maps.add(asdpBean);
			}
			rs.close();
			ResultSet rs2 = afStockService.getDbOp().executeQuery("select count(asdp.id) counts from after_sale_detect_product asdp,after_sale_detect_package aspack, product p,catalog c where asdp.after_sale_detect_package_id=aspack.id and  p.parent_id1 = c.id and asdp.product_id=p.id " + buff.toString());
			if (rs2.next()) {
				datagrid.setTotal((long)rs2.getInt(1));
			}
			rs2.close();
			
			datagrid.setRows(maps);
			boolean flag = false;
			UserGroupBean group = user.getGroup();
			if (group.isFlag(1411)) {
				flag = true;
			}
			List footer = new ArrayList();
			footer.add(flag);
			datagrid.setFooter(footer);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 寄回客户包裹列表
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBackUserPackageDataGrid")
	@ResponseBody
	public EasyuiDataGridJson getBackUserPackageDataGrid(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String userPhone, String packageCode, String startTime, String endTime, String afterSaleOrderCode, String afterSaleDetectProductCode,
			String orderCode,String areaId,String deliverId,String spareCode) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.convertNull(packageCode))) {
				buff.append(" and aspack.package_code = '" + StringUtil.toSql(packageCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(areaId))) {
				buff.append(" and aspack.area_id = " + StringUtil.toSql(areaId));
			}
			if(!"".equals(StringUtil.convertNull(afterSaleOrderCode))) {
				buff.append(" and aso.after_sale_order_code = '" + StringUtil.toSql(afterSaleOrderCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(spareCode))) {
				buff.append(" and asrnpr.spare_code = '" + StringUtil.toSql(spareCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(afterSaleDetectProductCode))) {
				buff.append(" and asdp.code = '" + StringUtil.toSql(afterSaleDetectProductCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(orderCode))) {
				buff.append(" and aso.order_code = '" + StringUtil.toSql(orderCode) +"'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and aspack.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and aspack.create_datetime<='" + endTime + " 23:59:59'");
			}
			if(!"".equals(StringUtil.convertNull(userPhone))) {
				buff.append(" and aspack.user_phone = '" + StringUtil.toSql(userPhone) +"'");
			}
			if(!"".equals(StringUtil.convertNull(deliverId))) {
				buff.append(" and aspack.deliver_id = " + StringUtil.toSql(deliverId));
			}
			if(buff.length()!=0){
				buff.delete(0, 4);
				buff.insert(0, " where ");
			}
			String query = "SELECT aspack.create_datetime,aspack.customer_name,aspack.package_code,aspack.price,aspack.deliver_id,aspack.user_name,aso.order_code,aso.after_sale_order_code,aspack.user_address,asdp. CODE after_sale_detect_product_code,asbup.type,aspack.remark,c. NAME,p. NAME,aspack.user_phone,aspack.weight,aso.customer_post_code,asrnpr.spare_code " +
					"FROM after_sale_back_user_package aspack join after_sale_back_user_product asbup on asbup.package_id=aspack.id " +
					"join after_sale_detect_product asdp on asdp.id=asbup.after_sale_detect_product_id " +
					"join after_sale_order aso on asdp.after_sale_order_id=aso.id " +
					"join product p on p.id=asbup.product_id " +
					"join catalog c on c.id=p.parent_id1 " +
					"left join after_sale_replace_new_product_record asrnpr on asrnpr.after_sale_detect_product_id=asbup.after_sale_detect_product_id" + buff.toString() + " order by aspack.create_datetime desc";
			query = DbOperation.getPagingQuery(query, (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			ResultSet rs = afStockService.getDbOp().executeQuery(query);
			List<AfterSaleBackUserPackage> maps = new ArrayList<AfterSaleBackUserPackage>();
			AfterSaleBackUserPackage asPackBean = null;
			while (rs.next()) {
				asPackBean = new AfterSaleBackUserPackage();
				asPackBean.setCreateDatetime(rs.getString(1));
				asPackBean.setCustomerName(rs.getString(2));
				asPackBean.setPackageCode(rs.getString(3));
				asPackBean.setPrice(rs.getFloat(4));
				asPackBean.setDeliverId(rs.getInt(5));
				asPackBean.setDeliverName(SysDict.deliverMap.get(rs.getInt(5)));
				asPackBean.setUserName(rs.getString(6));
				asPackBean.setOrderCode(rs.getString(7));
				asPackBean.setAfterSaleOrderCode(rs.getString(8));
				asPackBean.setUserAddress(rs.getString(9));
				asPackBean.setAfterSaleDetectProductCode(rs.getString(10));
				asPackBean.setTypeName(AfterSaleBackUserProduct.typeMap.get(rs.getInt(11)));
				asPackBean.setRemark(rs.getString(12));
				asPackBean.setParentName(rs.getString(13));
				asPackBean.setProductName(rs.getString(14));
				asPackBean.setUserPhone(rs.getString(15));
				asPackBean.setWeight(rs.getFloat(16));
				asPackBean.setCustomerPostCode(rs.getString(17));
				asPackBean.setSpareCode(rs.getString(18));
				maps.add(asPackBean);
			}
			rs.close();
			String queryTotalCount =  "SELECT count(aspack.id) FROM after_sale_back_user_package aspack " +
					"join after_sale_back_user_product asbup on asbup.package_id=aspack.id " +
					"join after_sale_detect_product asdp on asdp.id=asbup.after_sale_detect_product_id " +
					"join after_sale_order aso on asdp.after_sale_order_id=aso.id " +
					"join product p on p.id=asbup.product_id " +
					"join catalog c on c.id=p.parent_id1 " +
					"left join after_sale_replace_new_product_record asrnpr on asrnpr.after_sale_detect_product_id=asbup.after_sale_detect_product_id" + buff.toString();
			rs = dbOp.executeQuery(queryTotalCount);
			int total = 0;
			if(rs!=null && rs.next()){
				total = rs.getInt(1);
			}
			rs.close();
			datagrid.setTotal((long)total);
			datagrid.setRows(maps);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 导出寄回客户包裹列表
	 * @author syuf
	 */
	@RequestMapping("/exportBackUserPackage")
	@ResponseBody
	public void exportBackUserPackage(HttpServletRequest request, HttpServletResponse response,String userPhone, String packageCode, String startTime, String endTime, String afterSaleOrderCode, String afterSaleDetectProductCode,
			String orderCode,String areaId,String deliverId) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.convertNull(packageCode))) {
				buff.append(" and aspack.package_code = '" + StringUtil.toSql(packageCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(areaId))) {
				buff.append(" and aspack.area_id = " + StringUtil.toSql(areaId));
			}
			if(!"".equals(StringUtil.convertNull(afterSaleOrderCode))) {
				buff.append(" and aso.after_sale_order_code = '" + StringUtil.toSql(afterSaleOrderCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(afterSaleDetectProductCode))) {
				buff.append(" and asdp.code = '" + StringUtil.toSql(afterSaleDetectProductCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(orderCode))) {
				buff.append(" and aso.order_code = '" + StringUtil.toSql(orderCode) +"'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and aspack.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and aspack.create_datetime<='" + endTime + " 23:59:59'");
			}
			if(!"".equals(StringUtil.convertNull(userPhone))) {
				buff.append(" and aspack.user_phone = '" + StringUtil.toSql(userPhone) +"'");
			}
			if(!"".equals(StringUtil.convertNull(deliverId))) {
				buff.append(" and aspack.deliver_id = " + StringUtil.toSql(deliverId));
			}
			String query = "select aspack.create_datetime,aspack.customer_name,aspack.package_code,aspack.price,aspack.deliver_id,aspack.user_name,aso.order_code,aso.after_sale_order_code," +
					"aspack.user_address,asdp.code after_sale_detect_product_code,asbup.type,aspack.remark,c.name,p.name " +
					"from after_sale_back_user_package aspack,after_sale_back_user_product asbup,after_sale_detect_product asdp,after_sale_order aso,catalog c,product p " +
					"where asbup.after_sale_detect_product_id=asdp.id and asbup.package_id=aspack.id and asdp.after_sale_order_id=aso.id and p.id=asbup.product_id and c.id=p.parent_id1" + buff.toString();
			query = DbOperation.getPagingQuery(query, -1, -1);
			ResultSet rs = afStockService.getDbOp().executeQuery(query);
			List<AfterSaleBackUserPackage> list = new ArrayList<AfterSaleBackUserPackage>();
			AfterSaleBackUserPackage asPackBean = null;
			while (rs.next()) {
				asPackBean = new AfterSaleBackUserPackage();
				asPackBean.setCreateDatetime(rs.getString(1));
				asPackBean.setCustomerName(rs.getString(2));
				asPackBean.setPackageCode(rs.getString(3));
				asPackBean.setPrice(rs.getFloat(4));
				asPackBean.setDeliverId(rs.getInt(5));
				asPackBean.setDeliverName(SysDict.deliverMap.get(rs.getInt(5)));
				asPackBean.setUserName(rs.getString(6));
				asPackBean.setOrderCode(rs.getString(7));
				asPackBean.setAfterSaleOrderCode(rs.getString(8));
				asPackBean.setUserAddress(rs.getString(9));
				asPackBean.setAfterSaleDetectProductCode(rs.getString(10));
				asPackBean.setTypeName(AfterSaleBackUserProduct.typeMap.get(rs.getInt(11)));
				asPackBean.setRemark(rs.getString(12));
				asPackBean.setParentName(rs.getString(13));
				asPackBean.setProductName(rs.getString(14));
				list.add(asPackBean);
			}
			rs.close();
			ArrayList<String> header = new ArrayList<String>();
			header.add("邮寄时间");
			header.add("姓名");
			header.add("包裹单号");
			header.add("运费金额");
			header.add("快递公司");
			header.add("发件人");
			header.add("订单号");
			header.add("售后单");
			header.add("收件地址");
			header.add("售后处理号");
			header.add("产品名称");
			header.add("一级分类");
			header.add("分类");
			header.add("重量");
			header.add("备注");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleBackUserPackage bean : list) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getCreateDatetime());
				printList.add(bean.getCustomerName());
				printList.add(bean.getPackageCode());
				printList.add(bean.getPrice() + "");
				printList.add(bean.getDeliverName());
				printList.add(bean.getUserName());
				printList.add(bean.getOrderCode());
				printList.add(bean.getAfterSaleOrderCode());
				printList.add(bean.getUserAddress());
				printList.add(bean.getAfterSaleDetectProductCode());
				printList.add(bean.getProductName());
				printList.add(bean.getParentName());
				printList.add(bean.getTypeName());
				printList.add(bean.getWeight() + "");
				printList.add(bean.getRemark());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 处理单条码打印
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/afterSaleDetectProductCodePrint")
	public String afterSaleDetectProductCodePrint(HttpServletRequest request, HttpServletResponse response,String code) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
	    	request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	        return null;
		}
		request.setAttribute("code", code);
		return "/admin/afStock/afterSaleDetectProductCodePrint";
	}
	/**
	 * @return 厂商管理 2014-2-18
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getSupplierList")
	@ResponseBody
	public EasyuiDataGridJson getSupplierList(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiPage,
			AfterSaleBackSupplier supplier) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService AfService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			int totalCount = AfService.getAfterSaleBackSupplierCount("id>0");
			datagrid.setTotal((long)totalCount);
			List list = AfService.getAfterSaleBackSupplierList("id>0", (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "id desc");
			datagrid.setRows(list);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * @return 添加售后厂商 2014-2-18
	 * @author zhangxiaolei
	 */
	@RequestMapping("/addSupplier")
	@ResponseBody
	public Json addSupplier (HttpServletRequest request, HttpServletResponse response, String supplierName,String supplierType,String address) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
//		supplierName  = new String(supplierName.getBytes("ISO-8859-1"), "UTF-8"); 
		if("".equals(StringUtil.checkNull(supplierName))){
			j.setMsg("厂商名称不能为空");
			return j;
		}
		if("-1".equals(supplierType)){
			j.setMsg("厂商类型不能为空");
			return j;
		}
		supplierName = java.net.URLDecoder.decode(supplierName,"utf-8");
		address = java.net.URLDecoder.decode(StringUtil.checkNull(address).trim(),"utf-8");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService AfService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			AfterSaleBackSupplier Bean = AfService.getAfterSaleBackSupplier("name='"+supplierName+"'");
			if(Bean!=null){
				j.setMsg("该厂商名称已经存在");
				return j;
			}
			//生成厂商编号
			String code="";
			AfterSaleBackSupplier asbsBean = AfService.getAfterSaleBackSupplier("id>0 order by id desc limit 1");
			if(asbsBean == null){
				code = code + "0001";
			}else{
				String _code = asbsBean.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-4));
				number++;
				code += String.format("%04d",new Object[]{new Integer(number)});
			}
			AfterSaleBackSupplier supplier = new AfterSaleBackSupplier();
			supplier.setCode(code);
			supplier.setName(supplierName);
			supplier.setType(StringUtil.parstInt(supplierType));
			supplier.setAddress(address);
			AfService.addAfterSaleBackSupplier(supplier);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		j.setMsg("添加成功！");	
		j.setSuccess(true);
		return j;
	}
	/**
	 * @return 修改厂商2014-2-18
	 * @author zhangxiaolei
	 */
	@RequestMapping("/modifySupplier")
	@ResponseBody
	public Json modifySupplier(HttpServletRequest request,HttpServletResponse response,int supplierId, String modifyName,String modifyType,String modifyAddress) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
//		modifyName  = new String(modifyName.getBytes("ISO-8859-1"), "UTF-8"); 
		if("".equals(StringUtil.checkNull(modifyName))){
			j.setMsg("厂商名称不能为空");
			return j;
		}
		if("-1".equals(modifyType)){
			j.setMsg("厂商类型不能为空");
			return j;
		}
		modifyAddress = StringUtil.checkNull(modifyAddress).trim();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService AfService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			AfterSaleBackSupplier Bean = AfService.getAfterSaleBackSupplier("name='"+modifyName+"' and id !="+supplierId);
			if(Bean!=null){
				j.setMsg("该厂商名称已经存在");
				return j;
			}
			if(!AfService.updateAfterSaleBackSupplier("name='"+modifyName+"',type="+modifyType+",address='"+modifyAddress+"'" , "id=" + supplierId)){
				dbOp.rollbackTransaction();
				j.setMsg("修改厂商失败!");
				return j;
			}else{
				j.setMsg("修改厂商成功!");
				j.setSuccess(true);
			}
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * @return 获取售后商品列表2014-2-18
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getAfterSaleDetectProductList")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleDetectProductList(HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,EasyuiPageBean page,String AfterSaleOrderCode,String AfterSaleDetectProductCode,
			String status,String productName,String productCode,String IMEI,String areaId){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" 1=1 ");
			
			if(!"".equals(StringUtil.checkNull(AfterSaleOrderCode))){
				buff.append(" and b.after_sale_order_code='" + StringUtil.toSql(AfterSaleOrderCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(AfterSaleDetectProductCode))){
				buff.append(" and a.code='" + StringUtil.toSql(AfterSaleDetectProductCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(status))){
				if("16".equals(status)){
					buff.append(" and (a.status=15 or a.status=16)");
				}else{
					buff.append(" and a.status=" + StringUtil.toSql(status));
				}
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and a.area_id=" + StringUtil.toSql(areaId) );
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and c.name='" + StringUtil.toSql(productName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and c.code='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(IMEI))){
				buff.append(" and a.IMEI='" + StringUtil.toSql(IMEI) + "'");
			}
			List<Map<String,String>> rows = afStockService.getAfterSaleDetectProductData(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), " a.id desc");
			int total = afStockService.getAfterSaleDetectProductCounts(buff.toString());
			datagrid.setTotal((long)total);
			datagrid.setRows(rows);
			List foot =new ArrayList();
			foot.add(AfterSaleDetectProductBean.statusMap.get(Integer.valueOf(status)));
			datagrid.setFooter(foot);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * @return 售后处理单详细2014-2-23
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getAfterSaleDetectProductDetail")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleDetectProductDetail(HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,EasyuiPageBean page,String id){
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("msg", "当前没有登录，操作失败！");
		    	request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		        return null;
			}
			AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id);
			if (detectProductBean == null) {
				request.setAttribute("msg", "没有此售后单信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProductBean.getCargoWholeCode() + "'");
			if(cargo==null){
				request.setAttribute("msg", "没有此售后单对应的货位信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			j = afStockService.getAfterSaleDetectProductDetail(id);
			HashMap map = (HashMap)j.getFooter().get(0);
			boolean repairQuote = false;
			boolean canRepair = false;
			boolean checkIng = false;
			boolean returnButton = false;
			if (detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS1||detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS15||detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS16||detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS17) {
				checkIng = true;
			}
			AfterSaleBackSupplierProduct asbup = null;
			List<AfterSaleBackSupplierProduct> asbupList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + (map.get("afterSaleDetectProductId")), -1, 1, "id desc");
			if (asbupList != null && asbupList.size() > 0) {
				asbup = asbupList.get(0);
				if (asbup != null) {
					if(cargo.getStockType()==ProductStockBean.STOCKTYPE_AFTER_SALE){
						if(asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS4){
							repairQuote = true;
						}
					}else if(cargo.getStockType()==ProductStockBean.STOCKTYPE_CUSTOMER){
						if ((Integer.valueOf(String.valueOf(map.get("detectProductStatus"))) == AfterSaleDetectProductBean.STATUS7 
								|| Integer.valueOf(String.valueOf(map.get("detectProductStatus"))) == AfterSaleDetectProductBean.STATUS4 )
								&& asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS4) {
							repairQuote = true;
						}
					}
					if (Integer.valueOf(String.valueOf(map.get("detectProductStatus"))) == AfterSaleDetectProductBean.STATUS4 && asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS4) {
						canRepair = true;
					}
					//换货退货
					if (((Integer.valueOf(String.valueOf(map.get("detectProductStatus"))) == AfterSaleDetectProductBean.STATUS4||Integer.valueOf(String.valueOf(map.get("detectProductStatus"))) == AfterSaleDetectProductBean.STATUS7)) && asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS4 ) {
						returnButton = true;
					}
				}
			} 
			map.put("repairQuote", repairQuote);
			map.put("canRepair", canRepair);
			map.put("checkIng", checkIng);
			map.put("returnButton", returnButton);
			List list = new ArrayList();
			list.add(map);
			j.setFooter(list);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * @return 售后处理单检测记录2014-2-23
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getAfterSaleDetectLogDetail")
	public void getAfterSaleDetectLogDetail(HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,EasyuiPageBean page,String id){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String str = new String();
		try {
			str = afStockService.getAfterSaleDetectLogDetails(id);
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			response.getWriter().write(str);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return;
	}
	/**
	 * @return 获取不合格检测记录
	 * @author syuf
	 */
	@RequestMapping("/getUnqualifiedReasonLog")
	@ResponseBody
	public Json getUnqualifiedReasonLog(String id){
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			List<Map<String,String>> maps = afStockService.getUnqualifiedReasonLog(id);
			if(maps.size() > 0){
				j.setObj(maps);
				j.setSuccess(true);
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	/**
	 * @return 售后处理单寄回包裹信息2014-2-23
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getAfterSaleDetectPackageInfo")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleDetectPackageInfo(HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,EasyuiPageBean page,String id){
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			 j = afStockService.getAfterSaleDetectPackageInfo(id);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	
	/**
	 *发送寄回包裹短信
	 * @author ahc
	 */
	@RequestMapping("/sendMessage")
	@ResponseBody
	public Json sendMessage(HttpServletRequest request,HttpServletResponse response,String id){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		try {
			if (user == null) {
				j.setMsg("当前没有登录，操作失败！");
				j.setSuccess(false);
		        return j;
			}
			
			AfterSaleBackUserPackage asbup = afStockService.getAfterSaleBackUserPackageInfo(id);
			boolean flag = true;
			voOrder order = adminService.getOrder(asbup.getOrderId());
			if(order == null){
				j.setMsg("订单不存在！");
				j.setSuccess(false);
				return j;
			}
			if(!order.isDaqOrder()){
				flag = false;
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("deliver", SysDict.deliverMap.get(asbup.getDeliverId()));
			paramMap.put("packageNum", asbup.getPackageCode());

			TemplateMarker tm = TemplateMarker.getMarker();
			String content = tm.getOutString(TemplateMarker.BACK_USER_PRODUCT_MESSAGE_NAME, paramMap);
			if(flag){
				String result =SenderSMS3.send(user.getId(), asbup.getUserPhone(), content,65);
				if("提醒短信发送成功".equals(result)){
					j.setMsg("发送成功！");
					j.setSuccess(true);
				}else{
					j.setMsg("发送失败！");
					j.setSuccess(false);
				}
			}else{
				String result =SenderSMS3.send(user.getId(), asbup.getUserPhone(), content);
				if("提醒短信发送成功".equals(result)){
					j.setMsg("发送成功！");
					j.setSuccess(true);
				}else{
					j.setMsg("发送失败！");
					j.setSuccess(false);
				}
			}
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 再次检测
	 * @return 售后处理单添加检测项2014-2-23
	 * @author zhangxiaolei
	 */
	@RequestMapping("/addAfterSaleDetectLog")
	@ResponseBody
	public Json addAfterSaleDetectLog(HttpServletRequest request, HttpServletResponse response) throws IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		boolean msgStatus = false;
		int id = StringUtil.toInt(request.getParameter("id"));
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int afterSaleDetectPackageId = StringUtil.toInt(request.getParameter("afterSaleDetectPackageId"));
		int afterSaleOrderId = StringUtil.toInt(request.getParameter("afterSaleOrderId"));
		String mainProductStatus = StringUtil.convertNull(request.getParameter("mainProductStatus"));
		String handle = StringUtil.convertNull(request.getParameter("handle")).trim();
		String IMEI = StringUtil.convertNull(request.getParameter("IMEI")).trim();
		String remark = StringUtil.convertNull(request.getParameter("remark")).trim();
		String questionDescription =request.getParameter("questionDescription").replaceAll("/请选择","");
		String damaged = StringUtil.convertNull(request.getParameter("damaged").replaceAll("/请选择","")).trim();
		String giftAll = StringUtil.convertNull(request.getParameter("giftAll").replaceAll("/请选择","")).trim();
		String reportStatus = StringUtil.convertNull(request.getParameter("reportStatus").replaceAll("/请选择","")).trim();
		String faultCode = StringUtil.convertNull(request.getParameter("faultCode")).trim();
		String faultDescription = StringUtil.convertNull(request.getParameter("faultDescription").replaceAll("/请选择","")).trim();
		String detectException = StringUtil.convertNull(request.getParameter("detectException")).trim();
		String exceptionReason = StringUtil.convertNull(request.getParameter("exceptionReason").replaceAll("/请选择","")).trim();
		String quoteItem = StringUtil.convertNull(request.getParameter("quoteItem1")).trim();
		String quote = StringUtil.convertNull(request.getParameter("quote1")).trim();
		String debitNote = StringUtil.convertNull(request.getParameter("debitNote")).trim();
		String[] quoteItemAdd = request.getParameterValues("quoteItemadd");
		String[] quoteAdd = request.getParameterValues("quoteadd");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp); 
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAfterSalesService asoService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			synchronized (lock) {
				String[] AfterSaleDetectTypeDetailContents=new String[3];
				String faultDescriptionIds[] = faultDescription.split("/");
				for(int i = 0 ; i < faultDescriptionIds.length ; i++){
					AfterSaleDetectTypeDetailBean asBean = afStockService.getAfterSaleDetectTypeDetail(" id=" + faultDescriptionIds[i]);
					if(asBean != null){
						AfterSaleDetectTypeDetailContents[i] = asBean.getContent();
					}else{
						j.setMsg("所选故障描述不存在！");
						return j;
					}
				}
				if("请选择".equals(exceptionReason)){
					exceptionReason="";
				}
				
				if (handle.equals("检测异常")) {
					if (exceptionReason.equals("")) {
						j.setMsg("处理意见是检测异常时，异常原因必须填写！");
						return j;
					}
					if (detectException.equals("")) {
						j.setMsg("处理意见是检测异常时，检测异常必须勾选！");
						return j;
					}
				} else {
					if (!exceptionReason.equals("")) {
						j.setMsg("处理意见不是检测异常时，异常原因不能填写！");
						return j;
					}
					if (!detectException.equals("")) {
						j.setMsg("处理意见不是检测异常时，检测异常不能勾选！");
						return j;
					}
				}
				StringBuffer quoteItemBuf = new StringBuffer();
				if (StringUtil.convertNull(quoteItem).trim().equals("") && !StringUtil.convertNull(quote).trim().equals("")) {
					j.setMsg("报价项为空，报价也得为空！");
					return j;
				} else if (!StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
					j.setMsg("报价为空，报价项也得为空！");
					return j;
				} else if (StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
				} else {
					quoteItemBuf.append(StringUtil.convertNull(quoteItem).trim()).append(" ").append(StringUtil.convertNull(quote).trim()).append("\n");
				}
				if (quoteItemAdd != null) {
					for (int i = 0; i < quoteItemAdd.length; i ++) {
						if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && !StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							j.setMsg("报价项为空，报价也得为空！");
							return j;
						} else if (!StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							j.setMsg("报价为空，报价项也得为空！");
							return j;
						} else if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							continue;
						} else {
							String [] quoteItemstr = quoteItemAdd[i].split(",");
							String [] quotestr = quoteAdd[i].split(",");
							
							if(quoteItemstr.length != quotestr.length){
								j.setMsg("报价项与报价个数不匹配！");
								return j;
							}
							for (int k = 0; k < quoteItemstr.length; k++) {
								quoteItemBuf.append(StringUtil.convertNull(quoteItemstr[k]).trim()).append(" ").append(quotestr[k].trim()).append("\n");
							}
						}
					}
				}

				//售后处理单
				AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id + " and after_sale_detect_package_id=" + afterSaleDetectPackageId + " and after_sale_order_id=" + afterSaleOrderId);
				if (detectProductBean == null) {
					j.setMsg("售后处理单不存在！");
					return j;
				}
				
				CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProductBean.getCargoWholeCode() +"'");
				if(cargo==null){
					j.setMsg("此售后处理单"+detectProductBean.getCode()+"对应的货位库存不存在!");
					return j;
				}
				
				//判断是否正在盘点
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK 
						+ " and stock_area=" + detectProductBean.getAreaId() + " and stock_type=" + cargo.getStockType());
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				
				//签收包裹单
				AfterSaleDetectPackageBean packageBean = afStockService.getAfterSaleDetectPackage("id=" + afterSaleDetectPackageId);
				if (packageBean == null) {
					j.setMsg("签收包裹单不存在！");
					return j;
				}
				//售后单
				AfterSaleOrderBean saleOrderBean = asoService.getAfterSaleOrder("id=" + afterSaleOrderId);
				if (saleOrderBean == null) {
					j.setMsg("售后单不存在！");
					return j;
				}
				if ((detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS1||detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS15||detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS16||detectProductBean.getStatus()== AfterSaleDetectProductBean.STATUS17)==false) {
					j.setMsg("售后处理单状态错误！");
					return j;
				}
				
				if (detectProductBean.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
					j.setMsg("此售后处理单"+detectProductBean.getCode()+"已锁定!");
					return j;
				}
				
				//判断是否是京东的订单
				OrderStockBean stockBean = service.getOrderStock("status!=3 and order_id=" + saleOrderBean.getOrderId());
				if(stockBean==null){
					j.setMsg("订单" + saleOrderBean.getOrderCode() + "出库记录不存在!");
					return j;
				}
				StockAreaBean areaBean = psService.getStockArea("id=" + stockBean.getStockArea());
				if(areaBean == null){
					j.setMsg("订单" + saleOrderBean.getOrderCode() + "出库记录中记录的库存地区不存在!");
					return j;
				}
				afStockService.getDbOp().startTransaction();
				boolean hasIMEI = false;
				//我司仓发货的订单，检测需要验证imei码；非我司订单不需要对imei码进行相关校验
				if(areaBean.getAttribute() == StockAreaBean.OUR_WARE){
					//imei码相关校验
					hasIMEI = imeiService.isProductMMBMobile(detectProductBean.getProductId());
					if (hasIMEI) {
						if (IMEI.trim().equals("")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("IMEI码商品必须填写IMEI码！");
							return j;
						} else {
							IMEIBean imei = imeiService.getIMEI("code = '" + IMEI.trim() + "'");
							if (imei == null) {
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("输入的IMEI码不存在！");
								return j; 
							} else {
								IMEIUserOrderBean imeiUOBean = imeiService.getIMEIUserOrder("order_id=" + saleOrderBean.getOrderId() + " and imei_code='" + IMEI.trim()+ "' and product_id=" + detectProductBean.getProductId());
								if (imeiUOBean == null) {
									if (imei.getStatus() == IMEIBean.IMEISTATUS2) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("该IMEI码状态可出库，并且未与当前订单关联，请联系物流处理！");
										return j; 
									} else if (imei.getStatus() != IMEIBean.IMEISTATUS3) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("IMEI码状态不对！");
										return j; 
									} else {
										int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id="+ saleOrderBean.getId() + " and IMEI='" + IMEI.trim() + "' and id<>" + detectProductBean.getId());
										if (count > 0) {
											afStockService.getDbOp().rollbackTransaction();
											j.setMsg("该售后单已存在输入的IMEI码！");
											return j; 
										}
										
										if (!handle.equals(AfterSaleDetectProductBean.HANDLE8)) {
											afStockService.getDbOp().rollbackTransaction();
											j.setMsg("该IMEI码已出库，但是未与当前订单关联，建议原品返回！");
											return j; 
										}
										j.setMsg("操作成功，该IMEI码已出库，但是未与当前订单关联，建议原品返回！");
										msgStatus = true;
									}
								} else {
									if (imei.getStatus() == IMEIBean.IMEISTATUS2) {
										if (!imeiService.updateIMEI("status= " + IMEIBean.IMEISTATUS3, "code='" + IMEI.trim() + "'")) {
											afStockService.getDbOp().rollbackTransaction();
											j.setMsg("修改IMEI码状态失败！");
											return j;
										}
										IMEILogBean imeiLog = new IMEILogBean();
										imeiLog.setIMEI(IMEI.trim());
										imeiLog.setOperCode("");
										imeiLog.setOperType(IMEILogBean.OPERTYPE9);
										imeiLog.setCreateDatetime(DateUtil.getNow());
										imeiLog.setContent("售后判断，imei码关联了订单，但状态为可出库，修改状态由可出库变成已出库"+",地区："+ProductStockBean.areaMap.get(1));
										imeiLog.setUserId(user.getId());
										imeiLog.setUserName(user.getUsername());
										
										if (!imeiService.addIMEILog(imeiLog)) {
											afStockService.getDbOp().rollbackTransaction();
											j.setMsg("添加IMEI日志失败！");
											return j;
										}
										j.setMsg("操作成功，IMEI码状态错误，已修改为“已出库”状态，如有问题请联系技术部！");
										msgStatus = true;
									} else if (imei.getStatus() != IMEIBean.IMEISTATUS3) {
										afStockService.getDbOp().rollbackTransaction();
										j.setMsg("IMEI码状态不对！");
										return j; 
									} else {
										int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id="+ saleOrderBean.getId() + " and IMEI='" + IMEI.trim() + "' and id<>" + detectProductBean.getId());
										if (count > 0) {
											afStockService.getDbOp().rollbackTransaction();
											j.setMsg("该售后单已存在输入的IMEI码！");
											return j; 
										}
									}
								}
							}
							
						}
					} else {
						if (!IMEI.trim().equals("")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("非IMEI码商品，不能输入IMEI码！");
							return j; 
						}
					}
				}
				
				//直接退货的
				if (handle.equals(AfterSaleDetectProductBean.HANDLE2)) {
					//原库
					CargoInfoBean outCargoInfo = cargoService.getCargoInfo("area_id=" + detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER + " and whole_code='"+ detectProductBean.getCargoWholeCode() + "'");
					if(outCargoInfo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到货位！");
						return j;
					}
					
					//目的库
					CargoInfoBean inCargoInfo = cargoService.getCargoInfo("area_id="+ detectProductBean.getAreaId() +" and stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE + " and store_type=" + CargoInfoBean.STORE_TYPE2);
					if(inCargoInfo==null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("未找到目的货位！");
						return j;
					}
					
					//售后入库单
					AfterSaleStockin stockin = new AfterSaleStockin();
					stockin.setAfterSaleDetectProductId(detectProductBean.getId());
					stockin.setProductId(detectProductBean.getProductId());
					stockin.setOutCargoWholeCode(outCargoInfo.getWholeCode());
					stockin.setInCargoWholeCode(inCargoInfo.getWholeCode());
					stockin.setStatus(AfterSaleStockin.STATUS1);
					stockin.setCreateUserId(user.getId());
					stockin.setCreateUserName(user.getUsername());
					stockin.setCreateDatetime(DateUtil.getNow());
					stockin.setCompleteUserId(user.getId());
					stockin.setCompleteUserName(user.getUsername());
					stockin.setCompleteDatetime(DateUtil.getNow());
					stockin.setType(AfterSaleStockin.TYPE0);
					stockin.setOrderCode(saleOrderBean.getOrderCode());
					
					if (!afStockService.addAfterSaleStockin(stockin)) {
						j.setMsg("添加售后入库单失败");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}

					if (!afStockService.updateAfterSaleDetectProduct("imei='" + StringUtil.toSql(IMEI) + "',cargo_whole_code='"+ inCargoInfo.getWholeCode()+
							"',status=" + AfterSaleDetectProductBean.STATUS2+",create_datetime='" +DateUtil.getNow() + "',create_user_id=" + user.getId() + ",create_user_name='" + user.getUsername() + "'", "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单信息失败！");
						return j;
					}
					String updateSql = "IMEI='" + StringUtil.toSql(IMEI) + "',remark='" + detectProductBean.getRemark() + "',type=1,status=2,modify_user_id=" + user.getId() + ",modify_user_name='" + user.getUsername() + "',modify_datetime='" + DateUtil.getNow() + "'";
					
					if (!afStockService.updateAfterSaleWarehourceProductRecords(updateSql, "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台售后处理单信息失败！");
						return j;
					}
					
					if( hasIMEI ) {
						if (!imeiService.updateIMEI("status= " + IMEIBean.IMEISTATUS2, "code='" + IMEI.trim() + "'")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("修改IMEI码状态失败！");
							return j;
						}
						IMEILogBean imeiLog = new IMEILogBean();
						imeiLog.setIMEI(IMEI.trim());
						imeiLog.setOperCode("");
						imeiLog.setOperType(IMEILogBean.OPERTYPE8);
						imeiLog.setCreateDatetime(DateUtil.getNow());
						imeiLog.setContent("由已出库变成可出库");
						imeiLog.setUserId(user.getId());
						imeiLog.setUserName(user.getUsername());
						
						if (!imeiService.addIMEILog(imeiLog)) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加IMEI日志失败！");
							return j;
						}
					}
					
					//操作库存
					voProduct product = wareService.getProduct(detectProductBean.getProductId());
					
					product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,null));
					ProductStockBean outPs = psService.getProductStock("product_id=" + detectProductBean.getProductId() + " and area="
							+ detectProductBean.getAreaId() + " and type=" + outCargoInfo.getStockType());
					if (outPs == null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("没有找到原库产品库存，操作失败！");
						return j;
					}
					ProductStockBean inPs = psService.getProductStock("product_id=" + detectProductBean.getProductId() + " and area="
							+ detectProductBean.getAreaId() + " and type=" + inCargoInfo.getStockType());
					if (inPs == null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("没有找到目的库产品库存，操作失败！");
						return j;
					}
					
					CargoProductStockBean outCpsb = cargoService.getCargoProductStock("product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId());
					if (outCpsb == null) {
						outCpsb = new CargoProductStockBean();
						outCpsb.setCargoId(outCargoInfo.getId());
						outCpsb.setProductId(product.getId());
						outCpsb.setStockCount(0);
						outCpsb.setStockLockCount(0);
						if(!cargoService.addCargoProductStock(outCpsb)){
							j.setMsg("数据库操作失败！");
							afStockService.getDbOp().rollbackTransaction();
							return j;
						}
						outCpsb.setId(service.getDbOp().getLastInsertId());
					}
					
					CargoProductStockBean inCpsb = cargoService.getCargoProductStock("product_id="+product.getId()+" and cargo_id="+inCargoInfo.getId());
					if (inCpsb == null) {
						inCpsb = new CargoProductStockBean();
						inCpsb.setCargoId(inCargoInfo.getId());
						inCpsb.setProductId(product.getId());
						inCpsb.setStockCount(0);
						inCpsb.setStockLockCount(0);
						if(!cargoService.addCargoProductStock(inCpsb)){
							j.setMsg("数据库操作失败！");
							afStockService.getDbOp().rollbackTransaction();
							return j;
						}
						inCpsb.setId(service.getDbOp().getLastInsertId());
					}
					
					if(!cargoService.updateCargoProductStockCount(outCpsb.getId(), -1)){
						j.setMsg("货位库存操作失败，货位库存不足！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					if(!cargoService.updateCargoProductStockCount(inCpsb.getId(), 1)){
						j.setMsg("目的货位库存操作失败，货位库存不足！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}

					if (!psService.updateProductStockCount(outPs.getId(), -1)) {
						j.setMsg("原库存操作失败，可能是库存不足，请与管理员联系！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					if (!psService.updateProductStockCount(inPs.getId(), 1)) {
						j.setMsg("目的库存操作失败，请与管理员联系！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					AfterSaleOrderBean afterSaleOrder = afStockService.getAfterSaleOrder("id=" + detectProductBean.getAfterSaleOrderId());
					if(afterSaleOrder == null){
						j.setMsg("售后单不存在！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					float afterSalePrice = product.getPrice5(); 
					float price5 = product.getPrice5();
					voOrderProduct orderProduct = wareService.getOrderProductSplit(afterSaleOrder.getOrderId(), product.getCode());
					if(orderProduct == null){
						orderProduct = wareService.getOrderPresentSplit(afterSaleOrder.getId(), product.getCode());
					}
					//出库库存价丢失补救****
					if(orderProduct != null&&orderProduct.getPrice3() == 0){
						//获取出货前最后一条进销存记录
						int outId = service.getNumber("id", "stock_card", null, "code = '"+ afterSaleOrder.getOrderCode() +"' and card_type = "+StockCardBean.CARDTYPE_ORDERSTOCK+" and product_id = " + product.getId());
						int scId = service.getNumber("id", "stock_card", "max", "id < "+outId+" and product_id = "+ product.getId());
						StockCardBean stockCard = psService.getStockCard("id = "+scId);
						orderProduct.setPrice3(stockCard.getStockPrice());
					}
					if(orderProduct != null){
						int totalCount = product.getStockAll() + product.getLockCountAll();
						//StockBatchLogBean batchLog = service.getStockBatchLog("code='"+ afterSaleOrder.getOrderCode() +"' and product_id="+orderProduct.getProductId());
						price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * 1)) / (totalCount + 1) * 1000))/1000;
						afterSalePrice = orderProduct.getPrice3();
					} 
					//进销存卡片
					//入库
					StockCardBean sci = new StockCardBean();

					sci.setCardType(StockCardBean.CARDTYPE_AFTERSALESDETECT);
					sci.setCode(detectProductBean.getCode());

					sci.setCreateDatetime(DateUtil.getNow());
					sci.setStockType(inCargoInfo.getStockType());
					sci.setStockArea(inCargoInfo.getAreaId());
					sci.setProductId(detectProductBean.getProductId());
					sci.setStockId(inPs.getId());
					sci.setStockInCount(1);
					sci.setStockInPriceSum((new BigDecimal(sci.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(afterSalePrice))).doubleValue());
					sci.setCurrentStock(product.getStock(sci.getStockArea(), sci.getStockType())+ product.getLockCount(sci.getStockArea(), sci.getStockType()));
					sci.setStockAllArea(product.getStock(sci.getStockArea())+ product.getLockCount(sci.getStockArea()));
					sci.setStockAllType(product.getStockAllType(sci.getStockType())+ product.getLockCountAllType(sci.getStockType()));
					sci.setAllStock(product.getStockAll() + product.getLockCountAll());
					sci.setStockPrice(price5);
					sci.setAllStockPriceSum((new BigDecimal(sci.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sci.getStockPrice()))).doubleValue());
					if(!psService.addStockCard(sci)){
						j.setMsg("进销存记录添加失败，请重新尝试操作！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					//出库
					StockCardBean sco = new StockCardBean();
					
					sco.setCardType(StockCardBean.CARDTYPE_AFTERSALESDETECT);
					sco.setCode(detectProductBean.getCode());
					sco.setCreateDatetime(DateUtil.getNow());
					sco.setStockType(outCargoInfo.getStockType());
					sco.setStockArea(outCargoInfo.getAreaId());
					sco.setProductId(detectProductBean.getProductId());
					sco.setStockId(outPs.getId());
					sco.setStockOutCount(1);
					sco.setStockOutPriceSum((new BigDecimal(sco.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					sco.setCurrentStock(product.getStock(sco.getStockArea(), sco.getStockType())+ product.getLockCount(sco.getStockArea(), sco.getStockType()));
					sco.setStockAllArea(product.getStock(sco.getStockArea())+ product.getLockCount(sco.getStockArea()));
					sco.setStockAllType(product.getStockAllType(sco.getStockType())+ product.getLockCountAllType(sco.getStockType()));
					sco.setAllStock(product.getStockAll() + product.getLockCountAll());
					sco.setStockPrice(product.getPrice5());
					sco.setAllStockPriceSum((new BigDecimal(sco.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sco.getStockPrice()))).doubleValue());
					if(!psService.addStockCard(sco)){
						j.setMsg("进销存记录添加失败，请重新尝试操作！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					//货位入库卡片
					CargoStockCardBean csc = new CargoStockCardBean();
					csc.setCardType(CargoStockCardBean.CARDTYPE_AFTERSALESDETECT);
					csc.setCode(detectProductBean.getCode());
					csc.setCreateDatetime(DateUtil.getNow());
					csc.setStockType(inCargoInfo.getStockType());
					csc.setStockArea(detectProductBean.getAreaId());
					csc.setProductId(detectProductBean.getProductId());
					csc.setStockId(inCpsb.getId());
					csc.setStockInCount(1);
					csc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
					csc.setAllStock(product.getStockAll() + product.getLockCountAll());
					csc.setCurrentCargoStock(inCpsb.getStockCount()+inCpsb.getStockLockCount());
					csc.setCargoStoreType(inCargoInfo.getStoreType());
					csc.setCargoWholeCode(inCargoInfo.getWholeCode());
					csc.setStockPrice(product.getPrice5());
					csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(csc)){
						j.setMsg("货位进销存记录添加失败，请重新尝试操作！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//货位出库卡片
					CargoStockCardBean outCsc = new CargoStockCardBean();
					outCsc.setCardType(CargoStockCardBean.CARDTYPE_AFTERSALESDETECT_OUT);
					outCsc.setCode(detectProductBean.getCode());
					outCsc.setCreateDatetime(DateUtil.getNow());
					outCsc.setStockType(outCargoInfo.getStockType());
					outCsc.setStockArea(detectProductBean.getAreaId());
					outCsc.setProductId(detectProductBean.getProductId());
					outCsc.setStockId(outCpsb.getId());
					outCsc.setStockOutCount(1);
					outCsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					outCsc.setCurrentStock(product.getStock(outCsc.getStockArea(), outCsc.getStockType()) + product.getLockCount(outCsc.getStockArea(), outCsc.getStockType()));
					outCsc.setAllStock(product.getStockAll() + product.getLockCountAll());
					outCsc.setCurrentCargoStock(outCpsb.getStockCount()+outCpsb.getStockLockCount());
					outCsc.setCargoStoreType(outCargoInfo.getStoreType());
					outCsc.setCargoWholeCode(outCargoInfo.getWholeCode());
					outCsc.setStockPrice(product.getPrice5());
					outCsc.setAllStockPriceSum((new BigDecimal(outCsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outCsc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(outCsc)){
						j.setMsg("货位进销存记录添加失败，请重新尝试操作！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					// 查询销售订单
					voOrder vOrder = wareService.getOrder(saleOrderBean.getOrderId());
					if (vOrder == null) {
						j.setMsg("售后处理单所关联的订单不存在");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					// 修改库存批次
					CommonLogic c = new CommonLogic();
					String result = c.updateStockBatchForAfterSaleStockin(detectProductBean.getAreaId(),afStockService.getDbOp(), product.getId(), detectProductBean.getAfterSaleOrderCode(), vOrder, user);
					if (result != null) {
						j.setMsg(result);
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//修改订单信息
					String s = afStockService.updateOrderAfterSale(saleOrderBean.getOrderId(), afStockService.getDbOp(), user);
					if ( s != null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg(s);
						return j;
					}
					
				} else if (handle.equals(AfterSaleDetectProductBean.HANDLE7)){
					
					if (!afStockService.updateAfterSaleDetectProduct("imei='" + StringUtil.toSql(IMEI) + 
							"',status=" + AfterSaleDetectProductBean.STATUS4 + ",create_datetime='" +DateUtil.getNow() + "',create_user_id=" + user.getId() + ",create_user_name='" + user.getUsername() + "'", "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单信息失败！");
						return j;
					}
					
					//返厂商品信息
					AfterSaleBackSupplierProduct backSupplierProduct = new AfterSaleBackSupplierProduct();
					backSupplierProduct.setAfterSaleDetectProductId(detectProductBean.getId());
					backSupplierProduct.setProductId(detectProductBean.getProductId());
					backSupplierProduct.setGuarantee(AfterSaleBackSupplierProduct.GUARANTEE1);
					backSupplierProduct.setUserId(user.getId());
					backSupplierProduct.setUserName(user.getName());
					backSupplierProduct.setCreateDatetime(DateUtil.getNow());
					backSupplierProduct.setStatus(AfterSaleBackSupplierProduct.STATUS3);
					
					if (!afStockService.addAfterSaleBackSupplierProduct(backSupplierProduct)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加返厂商品信息失败！");
						return j;
					}
					
					String set = "IMEI='" + StringUtil.toSql(IMEI) + "',remark='" + detectProductBean.getRemark() + "',type=6,status=5,modify_user_id=" + user.getId() + ",modify_user_name='" + user.getUsername() + "',modify_datetime='" + DateUtil.getNow() + "'";
					
					if (!afStockService.updateAfterSaleWarehourceProductRecords(set, "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台售后处理单信息失败！");
						return j;
					}
					
				} else {
					
					if (!afStockService.updateAfterSaleDetectProduct("imei='" + StringUtil.toSql(IMEI) +
							"',status=" + AfterSaleDetectProductBean.STATUS2 + ",create_datetime='" +DateUtil.getNow() + "',create_user_id=" + user.getId() + ",create_user_name='" + user.getUsername() + "'", "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单信息失败！");
						return j;
					}
					
					Integer type = AfterSaleDetectProductBean.handleMap.get(handle);
					if (type == null ) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("该处理意见不存在！");
						return j;
					}
					
					String set = "IMEI='" + StringUtil.toSql(IMEI) + "',remark='" + detectProductBean.getRemark() + "',type=" + type + ",status=2,modify_user_id=" + user.getId() + ",modify_user_name='" + user.getUsername() + "',modify_datetime='" + DateUtil.getNow() + "'";
					
					if (!afStockService.updateAfterSaleWarehourceProductRecords(set, "id=" + detectProductBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加销售后台售后处理单信息失败！");
						return j;
					}
					
				}
				
				//检测信息添加
				AfterSaleDetectLogBean asdLog = new AfterSaleDetectLogBean();
				asdLog.setAfterSaleDetectProductId(detectProductBean.getId());
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.QUESTION_DESCRIPTION);
				
				String questionDescriptions[] = questionDescription.split("/"); 
				if(questionDescriptions.length==1){
					asdLog.setContent(questionDescriptions[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(questionDescriptions.length==2){
					asdLog.setContent(questionDescriptions[0]);
					asdLog.setContent2(questionDescriptions[1]);
					asdLog.setContent3("");
				}
				if(questionDescriptions.length==3){
					asdLog.setContent(questionDescriptions[0]);
					asdLog.setContent2(questionDescriptions[1]);
					asdLog.setContent3(questionDescriptions[2]);
				}
				
				asdLog.setCreateDatetime(DateUtil.getNow());
				asdLog.setUserId(user.getId());
				asdLog.setUserName(user.getUsername());
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加问题分类失败！");
					return j;
				}
				
				ResultSet asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + questionDescription.replaceAll("//","") + "'  where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台问题分类失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + questionDescription.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台问题分类失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.DAMAGED);
				String damageds[] = damaged.split("/");
				if(damageds.length==1){
					asdLog.setContent(damageds[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(damageds.length==2){
					asdLog.setContent(damageds[0]);
					asdLog.setContent2(damageds[1]);
					asdLog.setContent3("");
				}
				if(damageds.length==3){
					asdLog.setContent(damageds[0]);
					asdLog.setContent2(damageds[1]);
					asdLog.setContent3(damageds[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加包装失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + damaged.replaceAll("//","") + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台包装失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + damaged.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台包装失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.GIFT_ALL);
				String giftAlls[] = giftAll.split("/");
				if(giftAlls.length==1){
					asdLog.setContent(giftAlls[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(giftAlls.length==2){
					asdLog.setContent(giftAlls[0]);
					asdLog.setContent2(giftAlls[1]);
					asdLog.setContent3("");
				}
				if(giftAlls.length==3){
					asdLog.setContent(giftAlls[0]);
					asdLog.setContent2(giftAlls[1]);
					asdLog.setContent3(giftAlls[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加赠品失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + giftAll.replaceAll("//","") + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台赠品失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + giftAll.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台赠品失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.FAULT_DESCRIPTION);
				StringBuffer faultDescriptionContents = new StringBuffer();
				if(faultDescriptionIds.length==1){
					asdLog.setContent(AfterSaleDetectTypeDetailContents[0]);
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(faultDescriptionIds.length==2){
					asdLog.setContent(AfterSaleDetectTypeDetailContents[0]);
					asdLog.setContent2(AfterSaleDetectTypeDetailContents[1]);
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[0]);
					faultDescriptionContents.append("/");
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[1]);
					asdLog.setContent3("");
				}
				if(faultDescriptionIds.length==3){
					asdLog.setContent(AfterSaleDetectTypeDetailContents[0]);
					asdLog.setContent2(AfterSaleDetectTypeDetailContents[1]);
					asdLog.setContent3(AfterSaleDetectTypeDetailContents[2]);
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[0]);
					faultDescriptionContents.append("/");
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[1]);
					faultDescriptionContents.append("/");
					faultDescriptionContents.append(AfterSaleDetectTypeDetailContents[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加故障描述失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + faultDescriptionContents + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台故障描述失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + faultDescriptionContents + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台故障描述失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.REPORT_STATUS);
				String reportStatuses[] = reportStatus.split("/");
				if(reportStatuses.length==1){
					asdLog.setContent(reportStatuses[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(reportStatuses.length==2){
					asdLog.setContent(reportStatuses[0]);
					asdLog.setContent2(reportStatuses[1]);
					asdLog.setContent3("");
				}
				if(reportStatuses.length==3){
					asdLog.setContent(reportStatuses[0]);
					asdLog.setContent2(reportStatuses[1]);
					asdLog.setContent3(reportStatuses[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加申报状态失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + reportStatus.replaceAll("//","") + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台申报状态失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + reportStatus.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台申报状态失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.QUOTE_ITEM);
				asdLog.setContent(quoteItemBuf.toString());
				asdLog.setContent2("");
				asdLog.setContent3("");
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加报价项失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台报价项失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台报价项失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.EXCEPTION_REASON);
				String exceptionReasons[] = exceptionReason.split("/");
				if(exceptionReasons.length==1){
					asdLog.setContent(exceptionReasons[0]);
					asdLog.setContent2("");
					asdLog.setContent3("");
				}
				if(exceptionReasons.length==2){
					asdLog.setContent(exceptionReasons[0]);
					asdLog.setContent2(exceptionReasons[1]);
					asdLog.setContent3("");
				}
				if(exceptionReasons.length==3){
					asdLog.setContent(exceptionReasons[0]);
					asdLog.setContent2(exceptionReasons[1]);
					asdLog.setContent3(exceptionReasons[2]);
				}
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加异常原因失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + exceptionReason.replaceAll("//","") + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台异常原因失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + exceptionReason.replaceAll("//","") + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台异常原因失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.FAULT_CODE);
				asdLog.setContent(faultCode);
				asdLog.setContent2("");
				asdLog.setContent3("");
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加故障代码失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台故障代码失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台故障代码失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.IMEI);
				asdLog.setContent(IMEI);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加IMEI失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台IMEI失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台IMEI失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.REMARK);
				asdLog.setContent(remark);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加备注失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台备注失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台备注失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.HANDLE);
				asdLog.setContent(handle);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加处理意见失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台处理意见失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台处理意见失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.MAIN_PRODUCT_STATUS);
				asdLog.setContent(mainProductStatus);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加主商品状态失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台主商品状态失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台主商品状态失败！");
							return j;
						}
					}
				}
				
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.DEBIT_NOTE);
				asdLog.setContent(debitNote);
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加发票失败！");
					return j;
				}
				
				asdRS = afStockService.getDbOp().executeQuery("select count(id) from after_sale_warehource_record_detail where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId());
				if (asdRS.next()) {
					int count = asdRS.getInt(1);
					if (count > 0) {
						if (!afStockService.getDbOp().executeUpdate("update after_sale_warehource_record_detail set content = '" + asdLog.getContent() + "' where after_sale_warehource_record_id=" + detectProductBean.getId() + " and after_sale_detect_type_id=" + asdLog.getAfterSaleDetectTypeId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新销售后台发票失败！");
							return j;
						}
					} else {
						int asdlId = afStockService.getDbOp().getLastInsertId();
						
						if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("添加销售后台发票失败！");
							return j;
						}
					}
				}
				
			
				asdRS.close();
				//判断该售后单是否全部检测完毕
				int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id=" + saleOrderBean.getId() + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 +")");
				if (count <= 0) {
					ResultSet rs  = afStockService.getDbOp().executeQuery("select count(asdl.id) from after_sale_detect_log asdl, after_sale_detect_product asdp where asdl.after_sale_detect_product_id=asdp.id and asdp.after_sale_order_id=" + saleOrderBean.getId() + " and asdl.content<>'" + AfterSaleDetectProductBean.HANDLE7 + "'");
					if (rs.next()) {
						count = rs.getInt(1);
					}
					rs.close();
					//判断是否存在有保修的，售后单状态以等待客户确认优先
					if (count <= 0) {
						if (!asoService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_售后处理中, "id=" + saleOrderBean.getId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新售后单信息失败！");
							return j;
						}
					} else {
						if (!asoService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_等待客户确认, "id=" + saleOrderBean.getId())) {
							afStockService.getDbOp().rollbackTransaction();
							j.setMsg("更新售后单信息失败！");
							return j;
						}
					}
				}
				
				//判断包裹单是否检测完毕
				count = afStockService.getAfterSaleDetectProductCount("after_sale_detect_package_id=" + afterSaleDetectPackageId + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 +")");
				if (count <= 0) {
					if (!afStockService.updateAfterSaleDetectPackage("status=" + AfterSaleDetectPackageBean.STATUS3, "id=" + afterSaleDetectPackageId)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新签收包裹状态失败！");
						return j;
					}
				}
				String content = "再次检测,售后处理单[" +detectProductBean.getCode() + "]商品,售后处理单状态改为[等待客户确认]";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE5,detectProductBean.getCode(),null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				afStockService.getDbOp().commitTransaction();
				j.setMsg("添加完成！");
				j.setSuccess(true);
				j.setObj(detectProductBean.getCode());
				return j;
			}
		} catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("检测异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}
	/**
	 * 退货换货
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/canReturn")
	@ResponseBody
	public Json canReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		int id = StringUtil.toInt(request.getParameter("detectProductId"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp); 
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				
				//售后处理单
				AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id);
				if (detectProductBean == null) {
					j.setMsg("售后处理单不存在！");
					return j;
				}
				
				if (detectProductBean.getStatus() != AfterSaleDetectProductBean.STATUS4) {
					j.setMsg("售后处理单不是保修！");
					return j;
				}
				AfterSaleBackSupplierProduct asbup = null;
				List<AfterSaleBackSupplierProduct> asbupList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + id, -1, -1, "id desc");
				if (asbupList != null && asbupList.size() > 0) {
					asbup = asbupList.get(0);
					if (asbup == null) {
						j.setMsg("返厂商品不存在！");
						return j;
					}
					if (asbup.getStatus() != AfterSaleBackSupplierProduct.STATUS4) {
						j.setMsg("返厂商品状态不是已返厂！");
						return j;
					}
				} else {
					j.setMsg("返厂商品不存在！");
					return j;
				}
				
				afStockService.getDbOp().startTransaction();
				if (!afStockService.updateAfterSaleDetectProduct("status=" + AfterSaleDetectProductBean.STATUS2, "id=" + id)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新售后处理单失败！");
					return j;
				}
				
				
				if (!afStockService.updateAfterSaleBackSupplierProduct("status=" + AfterSaleBackSupplierProduct.STATUS0, "id=" + asbup.getId())) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新返厂商品信息失败！");
					return j;
				}
				return j;
			}
		} catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("操作异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}
	/**
	 * 添加维修报价
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/repairQuote")
	@ResponseBody
	public Json repairQuote(HttpServletRequest request, HttpServletResponse response) throws IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		String flag = StringUtil.convertNull(request.getParameter("flag")).trim();
		int id = StringUtil.toInt(request.getParameter("detectProductId"));
		String afterSaleDetectProductCode = StringUtil.convertNull(request.getParameter("afterSaleDetectProductCode")).trim();
		String quoteItem = StringUtil.convertNull(request.getParameter("quoteItem")).trim();
		String quote = StringUtil.convertNull(request.getParameter("quote")).trim();
		String[] quoteItemAdd = request.getParameterValues("quoteItemadd");
		String[] quoteAdd = request.getParameterValues("quoteadd");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp); 
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAfterSalesService asoService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		double totalPrice = 0;
		try {
			synchronized (lock) {
				StringBuffer quoteItemBuf = new StringBuffer();
				if (StringUtil.convertNull(quoteItem).trim().equals("") && !StringUtil.convertNull(quote).trim().equals("")) {
					j.setMsg("报价项为空，报价也得为空！");
					return j;
				} else if (!StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
					j.setMsg("报价为空，报价项也得为空！");
					return j;
				} else if (StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
				} else {
					totalPrice += Double.parseDouble(quote);
					quoteItemBuf.append(StringUtil.convertNull(quoteItem).trim()).append(" ").append(StringUtil.convertNull(quote).trim()).append("\n");
				}
				if (quoteItemAdd != null) {
					for (int i = 0; i < quoteItemAdd.length; i ++) {
						if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && !StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							j.setMsg("报价项为空，报价也得为空！");
							return j;
						} else if (!StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							j.setMsg("报价为空，报价项也得为空！");
							return j;
						} else if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
							continue;
						} else {
							String [] quoteItemstr = quoteItemAdd[i].split(",");
							String [] quotestr = quoteAdd[i].split(",");
							
							if(quoteItemstr.length != quotestr.length){
								j.setMsg("报价项与报价个数不匹配！");
								return j;
							}
							for (int k = 0; k < quoteItemstr.length; k++) {
								quoteItemBuf.append(StringUtil.convertNull(quoteItemstr[k]).trim()).append(" ").append(quotestr[k].trim()).append("\n");
								totalPrice += Double.parseDouble(quotestr[k]);
							}
							
						}
					}
				}
				
				if (quoteItemBuf.length() <= 0) {
					j.setMsg("必须填写维修报价信息！");
					return j;
				}
				//售后处理单
				AfterSaleDetectProductBean detectProductBean = null;
				if(flag.equals("1")){
					detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id);
				}else if(flag.equals("2")){
					detectProductBean = afStockService.getAfterSaleDetectProduct("code='" + afterSaleDetectProductCode + "'");
				}
				if (detectProductBean == null) {
					j.setMsg("售后处理单不存在！");
					return j;
				}
				CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProductBean.getCargoWholeCode() + "'");
				if(cargo==null){
					j.setMsg("售后处理单对应的货位不存在！");
					return j;
				}
				if(cargo.getStockType()==ProductStockBean.STOCKTYPE_CUSTOMER){
					if (!(detectProductBean.getStatus() == AfterSaleDetectProductBean.STATUS7  || detectProductBean.getStatus() == AfterSaleDetectProductBean.STATUS4)) {
						j.setMsg("售后处理单不是付费维修或保修不能添加维修报价！");
						return j;
					}
				}
				
				AfterSaleBackSupplierProduct asbup = null;
				List<AfterSaleBackSupplierProduct> asbupList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + detectProductBean.getId(), -1, -1, "id desc");
				if (asbupList != null && asbupList.size() > 0) {
					asbup = asbupList.get(0);
					if (asbup == null) {
						j.setMsg("返厂商品不存在！");
						return j;
					}
					if (asbup.getStatus() != AfterSaleBackSupplierProduct.STATUS4) {
						j.setMsg("返厂商品状态不是已返厂！");
						return j;
					}
				} else {
					j.setMsg("返厂商品不存在！");
					return j;
				}
				afStockService.getDbOp().startTransaction();
				//更新语句   只有是第一次返厂并且是客户机的返厂商品,处理单商品是付费维修的，可以更新type=10
				StringBuilder set = new StringBuilder();
				set.append("modify_user_id=").append(user.getId()).append(",modify_user_name='").append(user.getUsername())
				.append("',modify_datetime='").append(DateUtil.getNow()).append("'");
				if(asbupList!=null && asbupList.size()<=1){
					if(totalPrice == 0){
						set.append(",type=14");
					} else {
						if(cargo.getStockType()==CargoInfoBean.STOCKTYPE_CUSTOMER && detectProductBean.getStatus() == AfterSaleDetectProductBean.STATUS7){
							set.append(",type=10");
						}
					}
				}
				
				if (!afStockService.updateAfterSaleWarehourceProductRecords(set.toString(), "id=" + detectProductBean.getId())) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("销售售后处理单状态修改失败！");
					return j;
				}
				//报价项信息添加
				AfterSaleDetectLogBean asdLog = new AfterSaleDetectLogBean();
				asdLog.setAfterSaleDetectProductId(detectProductBean.getId());
				asdLog.setCreateDatetime(DateUtil.getNow());
				asdLog.setUserId(user.getId());
				asdLog.setUserName(user.getUsername());
				asdLog.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.SUPPLIER_PRICE);
				asdLog.setContent(quoteItemBuf.toString());
				if (!afStockService.addAfterSaleDetectLog(asdLog)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加厂商报价失败！");
					return j;
				}
				int asdlId = afStockService.getDbOp().getLastInsertId();
				
				if (!afStockService.getDbOp().executeUpdate("insert into after_sale_warehource_record_detail values(" + asdlId + "," + detectProductBean.getId() + "," + asdLog.getAfterSaleDetectTypeId() + ",'" + asdLog.getContent() + "'," + asdLog.getUserId() + ",'" + asdLog.getUserName() + "','" + asdLog.getCreateDatetime() + "')")) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("添加销售后台厂商报价失败！");
					return j;
				}
				if (!afStockService.updateAfterSaleBackSupplierProduct("status=" + AfterSaleBackSupplierProduct.STATUS0, "id=" + asbup.getId())) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新返厂商品信息失败！");
					return j;
				}
				String productName = "";
				ResultSet rs = afStockService.getDbOp().executeQuery("select name from product where id=" + detectProductBean.getProductId());
				if(rs.next()){
					productName = rs.getString(1);
				}
				rs.close();
				//客户机发送短信
				if(cargo.getStockType() == ProductStockBean.STOCKTYPE_CUSTOMER){
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("productName", productName);
					
					TemplateMarker tm =TemplateMarker.getMarker();
					String content=tm.getOutString(TemplateMarker.REPAIR_PRODUCT_MESSAGE_NAME, paramMap);
					AfterSaleOrderBean asoBean = asoService.getAfterSaleOrder("id=" + detectProductBean.getAfterSaleOrderId());
					if (asoBean == null) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("售后单不存在!");
						return j;
					}
					voOrder order = adminService.getOrder(asoBean.getOrderId());
					if(order==null){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("订单不存在!");
						return j;
					}
					if(order.isDaqOrder()){
						SenderSMS3.send(user.getId(), asoBean.getCustomerPhone(), content,65);
					}else{
						SenderSMS3.send(user.getId(), asoBean.getCustomerPhone(), content);//无user，userId用0代替
					}
				}
				if(!afStockService.writeAfterSaleLog(user, "添加售后处理单[" + detectProductBean.getCode() + "]的商品厂商维修报价", 1, AfterSaleLogBean.TYPE21,detectProductBean.getCode(),null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				afStockService.getDbOp().commitTransaction();
				j.setMsg("添加成功！");
				j.setSuccess(true);
				j.setObj(id);
				return j;
			}
		} catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 可以维修
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/canRepair")
	@ResponseBody
	public Json canRepair(HttpServletRequest request, HttpServletResponse response) throws IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		int id = StringUtil.toInt(request.getParameter("detectProductId"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp); 
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAfterSalesService asoService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		try {
			synchronized (lock) {
				
				//售后处理单
				AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id);
				if (detectProductBean == null) {
					j.setMsg("售后处理单不存在！");
					return j;
				}
				
				if (detectProductBean.getStatus() != AfterSaleDetectProductBean.STATUS4) {
					j.setMsg("售后处理单不是保修！");
					return j;
				}
				AfterSaleBackSupplierProduct asbup = null;
				List<AfterSaleBackSupplierProduct> asbupList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + id, -1, -1, "id desc");
				if (asbupList != null && asbupList.size() > 0) {
					asbup = asbupList.get(0);
					if (asbup == null) {
						j.setMsg("返厂商品不存在！");
						return j;
					}
					if (asbup.getStatus() != AfterSaleBackSupplierProduct.STATUS4) {
						j.setMsg("返厂商品状态不是已返厂！");
						return j;
					}
				} else {
					j.setMsg("返厂商品不存在！");
					return j;
				}
				
				afStockService.getDbOp().startTransaction();
				
				if (!afStockService.updateAfterSaleBackSupplierProduct("status=" + AfterSaleBackSupplierProduct.STATUS0, "id=" + asbup.getId())) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新返厂商品信息失败！");
					return j;
				}
				String productName = "";
				ResultSet rs = afStockService.getDbOp().executeQuery("select name from product where id=" + detectProductBean.getProductId());
				if(rs.next()){
					productName = rs.getString(1);
				}
				rs.close();
				//发送短信
				Map<String,Object> paramMap = new HashMap<String,Object>();
				paramMap.put("productName", productName);
				
				TemplateMarker tm =TemplateMarker.getMarker();
				String content=tm.getOutString(TemplateMarker.REPAIR_PRODUCT_MESSAGE_NAME, paramMap);
				AfterSaleOrderBean asoBean = asoService.getAfterSaleOrder("id=" + detectProductBean.getAfterSaleOrderId());
				if (asoBean == null) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("售后单不存在!");
					return j;
				}
				voOrder order = adminService.getOrder(asoBean.getOrderId());
				if (order == null) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("订单不存在!");
					return j;
				}
				if(order.isDaqOrder()){
					SenderSMS3.send(user.getId(), asoBean.getCustomerPhone(), content,65);
				}else{
					SenderSMS3.send(user.getId(), asoBean.getCustomerPhone(), content);//无user，userId用0代替
				}
				if(!afStockService.writeAfterSaleLog(user, "收到售后处理单[" + detectProductBean.getCode() + "]的商品厂商检测结果可以维修", 1, AfterSaleLogBean.TYPE22,detectProductBean.getCode(),null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				afStockService.getDbOp().commitTransaction();
				j.setMsg("操作成功！");
				j.setSuccess(true);
				j.setObj(id);
				return j;
			}
		} catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("操作异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 无法维修建议退货 无法维修建议换货
	 * @param request
	 * @param response
	 * @throws IOException
	 * zhangxiaolei
	 */
	@RequestMapping("/canntRepair")
	@ResponseBody
	public Json canntRepair(HttpServletRequest request, HttpServletResponse response) throws IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		int id = StringUtil.toInt(request.getParameter("detectProductId"));
		int type = StringUtil.toInt(request.getParameter("type"));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp); 
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				//售后处理单
				AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + id);
				if (detectProductBean == null) {
					j.setMsg("售后处理单不存在！");
					return j;
				}
				if (detectProductBean.getStatus() != AfterSaleDetectProductBean.STATUS4&&detectProductBean.getStatus() != AfterSaleDetectProductBean.STATUS7) {
					j.setMsg("售后处理单不是保修或者付费维修！");
					return j;
				}
				AfterSaleBackSupplierProduct asbup = null;
				List<AfterSaleBackSupplierProduct> asbupList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + id, -1, -1, "id desc");
				if (asbupList != null && asbupList.size() > 0) {
					asbup = asbupList.get(0);
					if (asbup == null) {
						j.setMsg("返厂商品不存在！");
						return j;
					}
					if (asbup.getStatus() != AfterSaleBackSupplierProduct.STATUS4) {
						j.setMsg("返厂商品状态不是已返厂！");
						return j;
					}
				} else {
					j.setMsg("返厂商品不存在！");
					return j;
				}
				
				afStockService.getDbOp().startTransaction();
				
				if (!afStockService.updateAfterSaleDetectProduct("status=" + AfterSaleDetectProductBean.STATUS2, "id=" +id)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新处理单信息失败！");
					return j;
				}
				if (!afStockService.updateAfterSaleBackSupplierProduct("status=" + AfterSaleBackSupplierProduct.STATUS0, "id=" + asbup.getId())) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新返厂商品信息失败！");
					return j;
				}
				if (!afStockService.updateAfterSaleWarehourceProductRecords("status=2,type="+type+",modify_user_id="+user.getId()+",modify_user_name='"+user.getName()+"',modify_datetime='"+DateUtil.getNow()+"'", "id=" + id)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("更新售后处理单失败！");
					return j;
				}
				String content = "";
				if(type == 8){
					content = "收到售后处理单[" + detectProductBean.getCode() + "]的商品厂商检测结果无法修理建议退货";
				} else if(type == 9){
					content = "收到售后处理单[" + detectProductBean.getCode() + "]的商品厂商检测结果无法修理建议换货";
				} else {
					content = "程序异常,既不是退货也不是换货";
				}
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE14,detectProductBean.getCode(),null)){
					dbOp.rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				afStockService.getDbOp().commitTransaction();
				j.setMsg("操作成功！");
				j.setSuccess(true);
				j.setObj(id);
				return j;
			}
		} catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("操作异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}

	/**
	 * 清空session
	 * @param request
	 * @param response
	 */
	@RequestMapping("/clearSession")
	public void clearSession(HttpServletRequest request, HttpServletResponse response){
		String paramName = StringUtil.convertNull(request.getParameter("paramName"));
		String stockType = StringUtil.convertNull(request.getParameter("stockType"));
		String areaId = StringUtil.convertNull(request.getParameter("areaId"));
		if(paramName!=null && paramName.length()>0){
			request.getSession().setAttribute(paramName, null);
		}
		if(stockType!=null && stockType.length()>0){
			request.getSession().setAttribute(stockType, null);
		}
		if(areaId!=null && areaId.length()>0){
			request.getSession().setAttribute(areaId, null);
		}
	}
	
	/**
	 * 获取未完成盘点作业列表
	 * @return
	 * @author 李宁
	* @date 2014-4-17
	 */
	@RequestMapping("/getAfterSaleInventoryList")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleInventoryList(HttpServletRequest request, HttpServletResponse response){
		EasyuiDataGridJson dataResult = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			List<AfterSaleInventory> inventoryList = afStockService.getAfterSaleInventoryList("status!=" + AfterSaleInventory.COMPLETE_CHECK, -1, -1, null);
			List<Map<String,String>> result = new ArrayList<Map<String,String>>();
			if(inventoryList!=null && inventoryList.size()>0){
				for(int i=0;i<inventoryList.size();i++){
					Map<String,String> inventoryMap = new HashMap<String,String>();
					AfterSaleInventory bean = inventoryList.get(i);
					inventoryMap.put("id", bean.getId()+"");
					inventoryMap.put("code", bean.getCode());
					int stockType = bean.getStockType();
					inventoryMap.put("stock_type",ProductStockBean.getStockTypeMap().get(stockType));
					int areaId = bean.getStockArea();
					inventoryMap.put("area",ProductStockBean.getAreaMap().get(areaId));
					int invertoryCount = bean.getInventoryCount();
					int count = invertoryCount;
					String invertoryCountStr = StringUtil.transfer(invertoryCount);
					int status = bean.getStatus();
					//操作 不同状态下显示的可进行的操作不同
					StringBuilder operate = new StringBuilder();
					if(AfterSaleInventory.STATUS_GENERATE==status){
						operate.append("<a href='javascript:void(0);' onclick='opeateInventory(\""+ bean.getId() +"\",\"start\")'>").append(invertoryCountStr).append("盘开始").append("</a>&nbsp;&nbsp;")
						.append(invertoryCountStr).append("盘结束&nbsp;&nbsp;");
						if(invertoryCount==1){
							operate.append("盘点完成");
						}else{
							operate.append("<a href='javascript:void(0);' onclick='opeateInventory(\""+ bean.getId() +"\",\"complete\")'>盘点完成<a>");
						}
					}else if(AfterSaleInventory.STATUS_CHECK_START==status){
						operate.append(invertoryCountStr).append("盘开始&nbsp;&nbsp;")
						.append("<a href='javascript:void(0);' onclick='opeateInventory(\""+ bean.getId() +"\",\"end\")'>").append(invertoryCountStr).append("盘结束")
						.append("</a>&nbsp;&nbsp;").append("盘点完成");
					}else if(AfterSaleInventory.STATUS_CHECK_END==status){
						count++;
						String temp = StringUtil.transfer(count);
						operate.append("<a href='javascript:void(0);' onclick='opeateInventory(\""+ bean.getId() +"\",\"start\",\"true\")'>")
						.append(temp).append("盘开始").append("</a>&nbsp;&nbsp;")
						.append(temp).append("盘结束&nbsp;&nbsp;")
						.append("<a href='javascript:void(0);' onclick='opeateInventory(\""+ bean.getId() +"\",\"complete\")'>盘点完成<a>");
					}
					inventoryMap.put("operate", operate.toString());
					result.add(inventoryMap);
				}
			}
			dataResult.setRows(result);
			dataResult.setTotal((long)result.size());
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return dataResult;
	}
	
	/**
	 * 新建盘点作业
	 * @return
	 * @author 李宁
	* @date 2014-4-18
	 */
	@RequestMapping("/addAfterSaleInventory")
	@ResponseBody
	public Json addAfterSaleInventory(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			json.setMsg("当前没有登录,操作失败!");
			return json;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			int stockType = StringUtil.parstInt(StringUtil.convertNull(request.getParameter("stockType")));
			if(stockType<=0){
				json.setMsg("请选择库类型!");
				return json;
			}
			int areaId = StringUtil.parstInt(StringUtil.convertNull(request.getParameter("areaId")));
			if(areaId<=0){
				json.setMsg("请选择售后地区!");
				return json;
			}
			//生成盘点作业单的限制：
			//1.所选库类型系统中有未完成的报损报溢单，不能生成新的盘点任务		
			//为了和报损报溢单及其他单子生成条件统一，最后讨论决定这个前提条件是：
			//所选库类型中的售后处理单中的lock字段不能有已锁定的状态的商品---2014-04-28
			//2.一个库类型只能有一个未完成的盘点任务
			int count = afStockService.getAfterSaleDetectCount(" and asdp.lock_status=1 and ci.stock_type=" + stockType + " and asdp.area_id=" + areaId);
			if(count>0){
				json.setMsg("售后处理单中有已锁定状态的商品不能生成盘点任务!");
				return json;
			}
			List<AfterSaleInventory> inventoryList = afStockService.getAfterSaleInventoryList("stock_type="+stockType+ " and stock_area=" + areaId + " and status!=" + AfterSaleInventory.COMPLETE_CHECK, -1, -1, null);
			if(inventoryList!=null && inventoryList.size()>0){
				json.setMsg("一个库类型只能有一个未完成的盘点任务!");
				return json;
			}
			//生成盘点作业单的盘点编号
			//* 生成规则：六位日期+3为流水号 例如：140416001
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String code = sdf.format(cal.getTime()).replace("-", "");//盘点编号
			inventoryList = afStockService.getAfterSaleInventoryList("stock_type="+ stockType +" and code like '"+code+"%' ", 0, 1, " id desc");
			if(inventoryList==null||inventoryList.size()==0){
				code+="001";
			}else{
				String _code = inventoryList.get(0).getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-3));
				number++;
				code += String.format("%03d",new Object[]{new Integer(number)});
			}
			//生成盘点作业单
			AfterSaleInventory inventoryBean = new AfterSaleInventory();
			inventoryBean.setCode(code);
			inventoryBean.setCreateDatetime(DateUtil.getNow());
			inventoryBean.setCreateUserId(user.getId());
			inventoryBean.setCreateUserName(user.getUsername());
			inventoryBean.setStockArea(areaId);
			inventoryBean.setStockType(stockType);
			inventoryBean.setInventoryCount(1);
			inventoryBean.setStatus(AfterSaleInventory.STATUS_GENERATE);
			boolean result = afStockService.addAfterSaleInventory(inventoryBean);
			if(result){
				json.setMsg("生成盘点作业单成功!");
				json.setSuccess(true);
			}else{
				json.setMsg("生成盘点作业单失败!");
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return json;
	}
	
	/**
	 * 更新盘点作业单的状态
	 * @return
	 * @author 李宁
	* @date 2014-4-18
	 */
	@RequestMapping("/updateInventory")
	@ResponseBody
	public Json updateInventory(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			json.setMsg("当前没有登录,操作失败!");
			return json;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			int id = StringUtil.StringToId(request.getParameter("id"));
			AfterSaleInventory bean = afStockService.getAfterSaleInventory("id=" + id);
			//type决定是盘点开始，结束，完成
			String type = StringUtil.convertNull(request.getParameter("type"));
			//flag==true 说明是新一轮的盘点 inventoryCount++（盘点次数加1）
			String flag = StringUtil.convertNull(request.getParameter("flag"));
			if(bean==null){
				json.setMsg("此盘点作业单不存在!");
			}else{
				synchronized(lock){
					afStockService.getDbOp().startTransaction();
					//更新语句
					StringBuilder set = new StringBuilder();
					if("start".equalsIgnoreCase(type)){//盘点开始
						set.append("status=").append(AfterSaleInventory.STATUS_CHECK_START);
						int inventoryCount = bean.getInventoryCount();
						if("true".equalsIgnoreCase(flag)){
							inventoryCount++;
							set.append(",inventory_count=").append(inventoryCount);
						}
						//盘点开始需要增加一条空的盘点记录
						AfterSaleInventoryRecord record = new AfterSaleInventoryRecord();
						record.setAfterSaleInventoryId(bean.getId());
						record.setAfterSaleInventroyCode(bean.getCode());
						record.setStockArea(bean.getStockArea());
						record.setStockType(bean.getStockType());
						record.setInventoryCount(inventoryCount);
						record.setStatus(AfterSaleInventoryRecord.STATUS_START);
						record.setBeginDatetime(DateUtil.getNow());
						record.setBeginUserId(user.getId());
						record.setBeginUserName(user.getUsername());
						
						if(!afStockService.addAfterSaleInventoryRecord(record)){
							afStockService.getDbOp().rollbackTransaction();
							json.setMsg("生成盘点记录失败!");
							return json;
						}
						
					}else if("end".equalsIgnoreCase(type)){//盘点结束
						set.append("status=").append(AfterSaleInventory.STATUS_CHECK_END);
						//盘点结束需要更新盘点记录中的各个数量，并插入有库存记录缺少实物数量的盘点商品
						StringBuilder condition = new StringBuilder();
						condition.append("after_sale_inventory_id=").append(bean.getId()).append(" and stock_area=").append(bean.getStockArea())
									.append(" and stock_type=").append(bean.getStockType()).append(" and status=").append(AfterSaleInventoryRecord.STATUS_START);
						AfterSaleInventoryRecord record = afStockService.getAfterSaleInventoryRecord(condition.toString());
						if(record==null){
							afStockService.getDbOp().rollbackTransaction();
							json.setMsg("没有相应的盘点记录!");
							return json;
						}
						
						if(!updateInventoryRecord(user, afStockService,record)){
							afStockService.getDbOp().rollbackTransaction();
							json.setMsg("更新盘点记录失败!");
							return json;
						}
						
						String result = addInventoryProduct(json, user, afStockService, record);
						if(result!=null && !"".equals(result)){
							json.setMsg(result);
							return json;
						}
					}else if("complete".equalsIgnoreCase(type)){//盘点完成
						set.append("status=").append(AfterSaleInventory.COMPLETE_CHECK).append(",complete_user_id=").append(user.getId())
						.append(",complete_user_name='").append(user.getUsername()).append("',complete_datetime='")
						.append(DateUtil.getNow()).append("'");
					}
					if(set!=null && set.length()>0){
						if(afStockService.updateAfterSaleInventory(set.toString(), "id=" + id)){
							json.setMsg("操作成功!");
							json.setSuccess(true);
						}else{
							afStockService.getDbOp().rollbackTransaction();
							json.setMsg("更新盘点作业单失败!");
							return json;
						}
					}
					afStockService.getDbOp().commitTransaction();
				}
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			afStockService.getDbOp().rollbackTransaction();
		}finally{
			afStockService.releaseAll();
		}
		return json;
	}
	
	/**
	 * 插入有库存记录缺少实物的盘点商品
	 * @param json
	 * @param user
	 * @param afStockService
	 * @param record
	 * @author 李宁
	* @date 2014-4-21
	 */
	private String addInventoryProduct(Json json, voUser user, AfStockService afStockService, AfterSaleInventoryRecord record) {
		String result = "";
		//所有库存商品
		List<AfterSaleDetectProductBean> detectProductList = afStockService.getStockCountProducts(" and ci.stock_type="+record.getStockType()+ " AND ci.area_id = " + record.getStockArea());

		//所有已盘点商品数量
		List<AfterSaleInventoryProduct> inventoryProductList = afStockService.getAfterSaleInventoryProductList("after_sale_inventory_record_id="+record.getId() +" and type!=2", -1, -1, null);
		HashMap<String,AfterSaleInventoryProduct> inventoryMap = new HashMap<String,AfterSaleInventoryProduct>();
		if(inventoryProductList!=null && inventoryProductList.size()>0){
			for(int i=0;i<inventoryProductList.size();i++){
				AfterSaleInventoryProduct product = inventoryProductList.get(i);
				inventoryMap.put(product.getAfterSaleDetectProductCode(), product);
			}
		}
		//查找缺少实物的商品
		if(detectProductList!=null && detectProductList.size()>0){
			for(int i=0;i<detectProductList.size();i++){
				AfterSaleDetectProductBean detectProduct = detectProductList.get(i);
				if(!inventoryMap.containsKey(detectProduct.getCode())){
					AfterSaleInventoryProduct inventoryProduct = new AfterSaleInventoryProduct();
					inventoryProduct.setAfterSaleDetectProductCode(detectProduct.getCode());
					inventoryProduct.setAfterSaleInventoryRecordId(record.getId());
					inventoryProduct.setAfterSaleDetectProductStatus(detectProduct.getStatus());
					inventoryProduct.setAfterSaleOrderCode(detectProduct.getAfterSaleOrderCode());
					inventoryProduct.setProductId(detectProduct.getProductId());
					inventoryProduct.setRecordWholeCode(detectProduct.getCargoWholeCode());
					inventoryProduct.setRealWholeCode(detectProduct.getCargoWholeCode());
					inventoryProduct.setUserId(user.getId());
					inventoryProduct.setUserName(user.getUsername());
					inventoryProduct.setType(AfterSaleInventoryProduct.TYPE2);
					if(!afStockService.addAfterSaleInventoryProduct(inventoryProduct)){
						afStockService.getDbOp().rollbackTransaction();
						result ="插入有库存记录缺少实物的盘点商品失败!";
						return result;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 更新盘点记录
	 * @param user
	 * @param afStockService
	 * @param bean
	 * @author 李宁
	* @date 2014-4-21
	 */
	private boolean updateInventoryRecord(voUser user, AfStockService afStockService, AfterSaleInventoryRecord record) {
		//库存商品总量=处理单中不是已完成状态的数量
		List<AfterSaleDetectProductBean> list = afStockService.getStockCountProducts(" and ci.stock_type="+record.getStockType()+ " AND ci.area_id = " + record.getStockArea());
		int stockCount = 0;
		if(list!=null && list.size()>0){
			stockCount = list.size();
		}
		//已盘点商品数量
		int inventoryProductCount = afStockService.getAfterSaleInventoryProductCount("after_sale_inventory_record_id=" + record.getId() +" and type!=2");
		//返厂商品数量
		int backSupplierCount = afStockService.getbackSuppilerProductCount(" and ci.stock_type="+record.getStockType()+ " AND ci.area_id = " + record.getStockArea());
		//有库存记录缺少实物数量
		int bsCount = afStockService.getbsCount(record.getStockType(),record.getId(),record.getStockArea());
		//报损过盘点找回的实物数量
		int byCount = afStockService.getAfterSaleInventoryProductCount("after_sale_inventory_record_id="+record.getId()+" and type=3");
		//未寄出商品数量 
		int unSendOutCount = afStockService.getAfterSaleInventoryProductCount("after_sale_inventory_record_id="+record.getId()+" and type=5");
		//记录和实际货位不一致商品数量
		int differentWholeCodeCount = afStockService.getAfterSaleInventoryProductCount("after_sale_inventory_record_id="+record.getId()+" and type=4");
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("stock_count=").append(stockCount).append(",inventory_product_count=").append(inventoryProductCount).append(",back_supplier_count=")
					.append(backSupplierCount).append(",bs_count=").append(bsCount).append(",by_count=").append(byCount).append(",different_whole_code_count=")
					.append(differentWholeCodeCount).append(",un_send_out_count=").append(unSendOutCount).append(",end_user_id=").append(user.getId())
					.append(",end_user_name='").append(user.getUsername()).append("',end_datetime='").append(DateUtil.getNow()).append("',status=")
					.append(AfterSaleInventoryRecord.STATUS_END);
		
		return afStockService.updateAfterSaleInventoryRecord(updateSql.toString(), "id="+record.getId());
	}
	
	/**
	 * 查询盘点记录
	 * @param request
	 * @param response
	 * @author 李宁
	 * @throws IOException 
	 * @throws ServletException 
	* @date 2014-4-18
	 */
	@RequestMapping("/getAfterSaleInventoryRecordList")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleInventoryRecordList(HttpServletRequest request, HttpServletResponse response,EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		EasyuiDataGridJson dataResult = new EasyuiDataGridJson();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String begin = StringUtil.dealParam(request.getParameter("startDate"));
		String end = StringUtil.dealParam(request.getParameter("endDate"));
		String inventoryCode = StringUtil.convertNull(request.getParameter("inventoryCode"));
		String areaId = StringUtil.convertNull(request.getParameter("areaId"));
		
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		try{
			//查询盘点结束的记录
			StringBuilder condition = new StringBuilder("status=" + AfterSaleInventoryRecord.STATUS_END);
			if(!(StringUtil.isNull(begin) || StringUtil.isNull(end))){
				begin = begin + " 00:00:00";
				end = end + " 23:59:59";
				condition.append(" and end_datetime between '").append(begin).append("' and '")
							.append(end).append("' ");
			}
			if(!(StringUtil.isNull(inventoryCode))){
				condition.append(" and after_sale_inventroy_code='").append(inventoryCode).append("' ");
			}
			if(!(StringUtil.isNull(areaId))){
				condition.append(" and stock_area=").append(areaId);
			}
			int count = afStockService.getAfterSaleInventoryRecordCount(condition.toString());
			List<AfterSaleInventoryRecord> recordList = afStockService.getAfterSaleInventoryRecordList(condition.toString(),(easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(),"id desc");
			if(recordList!=null && recordList.size()>0){
				for(int i=0;i<recordList.size();i++){
					AfterSaleInventoryRecord record = recordList.get(i);
					Map<String,String> map = new HashMap<String,String>();
					map.put("id",String.valueOf(record.getId()));
					map.put("endDate",record.getEndDatetime().substring(0, 10));
					map.put("afterSaleInventroyCode",record.getAfterSaleInventroyCode());
					map.put("inventoryCount",StringUtil.transfer(record.getInventoryCount())+"盘");
					map.put("stockArea", record.getStockArea()+"");
					map.put("stockAreaStr", ProductStockBean.getAreaMap().get(record.getStockArea()));
					String stockTypeStr = record.getStockType()==ProductStockBean.STOCKTYPE_AFTER_SALE?"售后商品库":record.getStockType()==ProductStockBean.STOCKTYPE_CUSTOMER?"客户商品库":"";
					map.put("stockType", String.valueOf(record.getStockType()));
					map.put("stockTypeStr", stockTypeStr);
					map.put("stockCount",String.valueOf(record.getStockCount()));
					map.put("inventoryProductCount",String.valueOf(record.getInventoryProductCount()));
					map.put("backSupplierCount",String.valueOf(record.getBackSupplierCount()));
					map.put("bsCount",String.valueOf(record.getBsCount()));
					map.put("byCount",String.valueOf(record.getByCount()));
					map.put("differentWholeCodeCount",String.valueOf(record.getDifferentWholeCodeCount()));
					map.put("unSendOutCount", String.valueOf(record.getUnSendOutCount()));
					lists.add(map);
				}
			}
			dataResult.setRows(lists);
			dataResult.setTotal((long) count);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return dataResult;
	}
	
	/**
	 * 查询已盘点商品
	 * @param request
	 * @param response
	 * @param easyuiPage
	 * @return
	 * @author 李宁
	* @date 2014-4-18
	 */
	@RequestMapping("/queryInventoryProductList")
	@ResponseBody
	public EasyuiDataGridJson queryInventoryProductList(HttpServletRequest request, HttpServletResponse response,EasyuiDataGrid easyuiPage){
		EasyuiDataGridJson dataResult = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String detectCode = StringUtil.convertNull(request.getParameter("afterSaleDetectCode"));
		int inventoryRecordId = StringUtil.StringToId(request.getParameter("inventoryRecordId"));
		String type = StringUtil.convertNull(request.getParameter("type"));
		try{
			//查询盘点结束的记录
			StringBuilder condition = new StringBuilder();
			if(!(StringUtil.isNull(type))){
				condition.append(" and afip.type in (").append(type).append(") ");
			}
			if(inventoryRecordId>0){
				condition.append(" and afip.after_sale_inventory_record_id=").append(inventoryRecordId).append(" ");
			}
			if(!(StringUtil.isNull(productCode))){
				condition.append(" and p.code='").append(productCode).append("' ");
			}
			if(!(StringUtil.isNull(detectCode))){
				condition.append(" and afip.after_sale_detect_product_code='").append(detectCode).append("' ");
			}
			List<Map<String,String>> recordList = afStockService.getAfterSaleInventoryProductList(condition.toString(),(easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			dataResult.setRows(recordList);
			int count = afStockService.getAfterSaleInventoryProductListCount(condition.toString());
			dataResult.setTotal((long)count);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return dataResult;
	}

	/**
	 * 新增报损报溢  添加修改
	 * @param request
	 * @param response
	 * @param reason
	 * @param strId
	 * @param bsbyType
	 * @param stockType
	 * @return
	 * gw
	 */
	@RequestMapping("/editAfterSaleBsby")
	@ResponseBody
	public Json editAfterSaleBsby(HttpServletRequest request, HttpServletResponse response,String reason,String strId,int bsbyType,int stockType){
		Json j = new Json();
		j.setSuccess(false);
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败");
			return j;
		}
		
		if(bsbyType != BsbyOperationnoteBean.TYPE0 && bsbyType != BsbyOperationnoteBean.TYPE1){
			j.setMsg("请选择单据类型");
			return j;
		}
		
		if(stockType != ProductStockBean.STOCKTYPE_AFTER_SALE && stockType != ProductStockBean.STOCKTYPE_CUSTOMER){
			j.setMsg("请选择库类型");
			return j;
		}
		
		reason = StringUtil.toSql(StringUtil.convertNull(reason));
		if(reason.length() <= 0){
			j.setMsg("请填写报损报溢原因");
			return j;
		}
		
		if(stockType== ProductStockBean.STOCKTYPE_AFTER_SALE && bsbyType==BsbyOperationnoteBean.TYPE1){
			j.setMsg("售后库不能增加报溢单!");
			return j;
		}
		
		Map<String, AfterSaleBsbyProduct> map = new HashMap<String, AfterSaleBsbyProduct>();
		Object obj = request.getSession().getAttribute("afterSaleBsbyMap");
		if(obj != null){
			map = (Map<String, AfterSaleBsbyProduct>)obj;
		}
		if(map.size() <= 0){
			j.setMsg("未查找到需要提交的信息 请刷新重试");
			return j;
		}
		
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
			AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
			int bsbyOperationnoteId = StringUtil.toInt(strId);
			
			boolean isUpdate = false;
			
			if(bsbyOperationnoteId > 0){
				isUpdate = true;
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(1471)) {
					j.setMsg("您没有编辑权限！");
					return j;
				}
			}
			int warehouse_type = stockType;
			try {
				service.getDbOp().startTransaction();
				BsbyOperationnoteBean bsbyOperationnoteBean = null;
				//新增
				String receipts_number = "";
				if(!isUpdate){
					String title = "";// 日志的内容
					String content = "";
					if (bsbyType == BsbyOperationnoteBean.TYPE0) {
						// 报损
						String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
						receipts_number = ByBsAction.createCode(code);
						title = "创建新的报损表" + receipts_number;
					} else {
						String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
						receipts_number = ByBsAction.createCode(code);
						title = "创建新的报溢表" + receipts_number;
					}
					int count = 0;
					String code = "";//处理单code
					for(String detectCode : map.keySet()){
						if(count==0){
							code = detectCode;
							count += 1;
						}
					}
					AfterSaleDetectProductBean detectProduct = afStockService.getAfterSaleDetectProduct("code='"+code+"'");
					String nowTime = DateUtil.getNow();
					bsbyOperationnoteBean = new BsbyOperationnoteBean();
					bsbyOperationnoteBean.setAdd_time(nowTime);
					bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.audit_ing);
					bsbyOperationnoteBean.setOperator_id(user.getId());
					bsbyOperationnoteBean.setOperator_name(user.getUsername());
					bsbyOperationnoteBean.setReceipts_number(receipts_number);
					bsbyOperationnoteBean.setWarehouse_area(detectProduct.getAreaId());
					bsbyOperationnoteBean.setWarehouse_type(stockType);
					bsbyOperationnoteBean.setType(bsbyType);
					bsbyOperationnoteBean.setIf_del(0);
					bsbyOperationnoteBean.setFinAuditId(0);
					bsbyOperationnoteBean.setFinAuditName("");
					bsbyOperationnoteBean.setFinAuditRemark("");
					bsbyOperationnoteBean.setRemark(reason);
					
					int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
					bsbyOperationnoteBean.setId(maxid + 1);
					boolean falg = service.addBsbyOperationnoteBean(bsbyOperationnoteBean);
					if (falg) {
						request.setAttribute("opid", Integer.valueOf(bsbyOperationnoteBean.getId()));// 添加成功将id传到下个页面
						bsbyOperationnoteId = bsbyOperationnoteBean.getId();
						// 添加操作日志
						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(nowTime);
						bsbyOperationRecordBean.setInformation(title);
						bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteId);
						boolean b = service.addBsbyOperationRecord(bsbyOperationRecordBean);
						if(!b){
							service.getDbOp().rollbackTransaction();
							j.setMsg("添加报损报溢单操作日志失败！");
							return j;
						}
					} else {
						service.getDbOp().rollbackTransaction();
						j.setMsg("添加报损报溢单操作失败!");
						return j;
					}
				}else{
					bsbyOperationnoteBean = service.getBsbyOperationnoteBean(" id = " + bsbyOperationnoteId);
					if(bsbyOperationnoteBean == null){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("不存在报损报溢单");
						return j;
					}
					
					boolean b = service.updateBsbyOperationnoteBean(" current_type = " + BsbyOperationnoteBean.audit_ing, " id = " + bsbyOperationnoteId);
					if(!b){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("报损报溢单状态更新失败");
						return j;
					}
					warehouse_type = bsbyOperationnoteBean.getWarehouse_type();
				}
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory("status <>" + AfterSaleInventory.COMPLETE_CHECK
						+ " and stock_area=" + bsbyOperationnoteBean.getWarehouse_area() + " and stock_type=" + warehouse_type);
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					afStockService.getDbOp().rollbackTransaction();
					return j;
				}
				//报损单：售后处理单必须是未锁定；报溢单：售后处理单必须是报损完成
				int lockStatus = bsbyType == BsbyOperationnoteBean.TYPE0 ? AfterSaleDetectProductBean.LOCK_STATUS0 : AfterSaleDetectProductBean.LOCK_STATUS2;
				int updateStatus = AfterSaleDetectProductBean.LOCK_STATUS1;
				
				if(isUpdate){
					List<AfterSaleBsbyProduct> list = afStockService.getAfterSaleBsbyProductList("bsby_operationnote_id = " + bsbyOperationnoteBean.getId(), -1, -1, null);
					if (list != null && list.size() > 0) {
						for (AfterSaleBsbyProduct asbp : list) {
							//还原状态
							if (!afStockService.updateAfterSaleDetectProduct("lock_status="+lockStatus, "code='" + asbp.getAfterSaleDetectProductCode() + "'")) {
								j.setMsg("更新售后处理单状态失败！");
								afStockService.getDbOp().rollbackTransaction();
								return j;
							}
						}
					}
					boolean b = afStockService.deleteAfterSaleBsbyProduct(" bsby_operationnote_id = " + bsbyOperationnoteId);
					if(!b){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("删除报损报溢单商品失败！");
						return j;
					}
					
					if (!service.deleteBsbyProduct("operation_id= " + bsbyOperationnoteBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("删除报损报溢单商品失败！");
						return j;
					}
					
					if (!service.deleteBsbyProductCargo("bsby_oper_id=" + bsbyOperationnoteBean.getId())) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("删除报损报溢单商品失败！");
						return j;
					}
				}
				Iterator<Entry<String, AfterSaleBsbyProduct>> iterator = map.entrySet().iterator();
				while(iterator.hasNext()){
					
					Entry<String, AfterSaleBsbyProduct> next = iterator.next();
					String mapKey = next.getKey();
					AfterSaleBsbyProduct mapValue = next.getValue();
					
					String condition = " code = '" + mapKey + "' and lock_status = " + lockStatus;
					AfterSaleDetectProductBean afterSaleDetectProduct = afStockService.getAfterSaleDetectProduct(condition);
					
					if(afterSaleDetectProduct == null){
						j.setMsg(BsbyOperationnoteBean.typeMap.get(bsbyType) + "单添加的售后处理单必须是" + AfterSaleDetectProductBean.lockStatusMap.get(lockStatus));
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					if(mapValue.getStockType() != warehouse_type){
						j.setMsg("售后处理单" + mapKey + "与所选库类型不一致");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					AfterSaleBsbyProduct asbp = afStockService.getAfterSaleBsbyProduct("after_sale_detect_product_code='" + mapKey + "' and status=" + AfterSaleBsbyProduct.STATUS0);
					if (asbp != null) {
						j.setMsg("售后处理单" + mapKey + "存在尚未处理完成的报损报溢单！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//报损报溢商品
					mapValue.setBsbyOperationnoteId(bsbyOperationnoteId);
					mapValue.setStatus(AfterSaleBsbyProduct.STATUS0);
					mapValue.setAfterSaleDetectProductStatus(afterSaleDetectProduct.getStatus());
					
					boolean b = afStockService.addAfterSaleBsbyProduct(mapValue);
					if(!b){
						j.setMsg("添加报损报溢单商品操作失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					if (!afStockService.updateAfterSaleDetectProduct("lock_status="+updateStatus, "code='" + mapKey + "'")) {
						j.setMsg("更新售后处理单状态失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					if(!isUpdate){
						//售后仓内作业日志
						String remark = "";
						if (bsbyType == BsbyOperationnoteBean.TYPE0) {
							remark = "添加售后处理单[" + afterSaleDetectProduct.getCode() + "]到报损单[" + receipts_number + "]";
						} else {
							remark = "添加售后处理单[" + afterSaleDetectProduct.getCode() + "]到报溢单[" + receipts_number + "]";
						}
						if(!afStockService.writeAfterSaleLog(user, remark, 1, AfterSaleLogBean.TYPE16,afterSaleDetectProduct.getCode(),null)){
							j.setMsg("添加售后操作日志失败！");
							afStockService.getDbOp().rollbackTransaction();
							return j;
						}
					}
					voProduct product = wareService.getProduct(mapValue.getProductId());

					if (product == null) {
						j.setMsg("该商品不存在！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					BsbyProductBean bsbyProductBean = service.getBsbyProductBean("operation_id=" + bsbyOperationnoteBean.getId() + " and product_id=" + product.getId());
					int productCount = 1;
					if (bsbyProductBean != null) {
						productCount = bsbyProductBean.getBsby_count() + 1;
					}
					int x = getProductCount(product.getId(), bsbyOperationnoteBean.getWarehouse_area(), bsbyOperationnoteBean.getWarehouse_type());
					int result = updateProductCount(x, bsbyOperationnoteBean.getType(), productCount);
					if (result < 0 ) {
						j.setMsg("您所添加商品的库存不足！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//新货位管理判断
					CargoProductStockBean cps = null;
					if(bsbyType == BsbyOperationnoteBean.TYPE0){
				        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
				        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bsbyOperationnoteBean.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+mapValue.getWholeCode()+"'", -1, -1, "ci.id asc");
				        if(cpsOutList == null || cpsOutList.size()==0){
				        	j.setMsg("货位号"+mapValue.getWholeCode()+"无效，请重新输入！");
				            service.getDbOp().rollbackTransaction();
				            return j;
				        }
				        cps = (CargoProductStockBean)cpsOutList.get(0);
				        if(1 > cps.getStockCount()){
				        	j.setMsg("该货位"+mapValue.getWholeCode()+"库存为" + cps.getStockCount() + "，库存不足！");
				            service.getDbOp().rollbackTransaction();
				            return j;
				        }
					}else{
						CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
						CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+bsbyOperationnoteBean.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+mapValue.getWholeCode()+"' and status <> "+CargoInfoBean.STATUS3);
				        if(cargo == null){
				        	j.setMsg("货位号"+mapValue.getWholeCode()+"无效，请重新输入！");
				            service.getDbOp().rollbackTransaction();
				            return j;
				        }
				        if(cargo.getStatus() == CargoInfoBean.STATUS2){
				        	j.setMsg("货位"+mapValue.getWholeCode()+"未开通，请重新输入！");
				            service.getDbOp().rollbackTransaction();
				            return j;
				        }
				        List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
				        if(cpsOutList == null || cpsOutList.size()==0){
				        	cps = new CargoProductStockBean();
				        	cps.setCargoId(cargo.getId());
				        	cps.setProductId(product.getId());
				        	cps.setStockCount(0);
				        	cps.setStockLockCount(0);
				        	if(!cargoService.addCargoProductStock(cps)){
								service.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
				        	}
				        	cps.setId(cargoService.getDbOp().getLastInsertId());
				        	
				        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId())) {
					        	service.getDbOp().rollbackTransaction();
					        	j.setMsg("数据库操作失败");
					        	return j;
				        	}
				        }else{
				        	cps = (CargoProductStockBean)cpsOutList.get(0);
				        }
					}
					//不含税价格
					float notaxProductPrice = iBsByservice.returnFinanceProductPrice(product.getId());
					if (bsbyProductBean == null) {
						bsbyProductBean = new BsbyProductBean();
						bsbyProductBean.setBsby_count(1);
						bsbyProductBean.setOperation_id(bsbyOperationnoteBean.getId());
						bsbyProductBean.setProduct_code(product.getCode());
						bsbyProductBean.setProduct_id(product.getId());
						bsbyProductBean.setProduct_name(product.getName());
						bsbyProductBean.setOriname(product.getOriname());
						bsbyProductBean.setAfter_change(result);
						bsbyProductBean.setBefore_change(x);
						bsbyProductBean.setPrice(product.getPrice5());
						bsbyProductBean.setNotaxPrice(notaxProductPrice);
						boolean falg = service.addBsbyProduct(bsbyProductBean);
						if (!falg) {
							j.setMsg("添加失败！");
							service.getDbOp().rollbackTransaction();
							return j;
						}
						BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
						bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
						bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
						bsbyCargo.setCount(1);
						bsbyCargo.setCargoProductStockId(cps.getId());
						bsbyCargo.setCargoId(cps.getCargoId());
						if(!service.addBsbyProductCargo(bsbyCargo)) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
					} else {
						if (!service.updateBsbyProductBean("bsby_count=" + productCount + ", before_change=" + x
								+ " , after_change=" + result + ",price=" + product.getPrice5() + ",notax_price=" + notaxProductPrice, "id=" + bsbyProductBean.getId())) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
						
						BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id=" + bsbyProductBean.getId() + " and cargo_product_stock_id=" + cps.getId());
						if (bsbyCargo == null) {
							bsbyCargo = new BsbyProductCargoBean();
							bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
							bsbyCargo.setBsbyProductId(bsbyProductBean.getId());
							bsbyCargo.setCount(1);
							bsbyCargo.setCargoProductStockId(cps.getId());
							bsbyCargo.setCargoId(cps.getCargoId());
							if(!service.addBsbyProductCargo(bsbyCargo)) {
								service.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
							}
						} else {
							if(!service.updateBsbyProductCargo("count=count+1" , "id=" + bsbyCargo.getId())) {
								service.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
							}
						}
					}
					
					if (bsbyType == BsbyOperationnoteBean.TYPE0) {
						String sql = "product_id = " + mapValue.getProductId() + " and "
								+ "area = " + bsbyOperationnoteBean.getWarehouse_area() + " and type = "
								+ bsbyOperationnoteBean.getWarehouse_type();
						ProductStockBean psBean = psService.getProductStock(sql);
						if(psBean == null){
							service.getDbOp().rollbackTransaction();
							j.setMsg("商品库存信息不存在");
							return j;
						}
						if(!psService.updateProductStockCount(psBean.getId(), -1)){
							service.getDbOp().rollbackTransaction();
							j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
							return j;
						}
						if(!psService.updateProductLockCount(psBean.getId(), 1)) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
							return j;
						}
						
						if(!cargoService.updateCargoProductStockCount(cps.getId(), -1)){
							service.getDbOp().rollbackTransaction();
							j.setMsg("货位库存操作失败，货位库存不足！");
							return j;
						}
						if(!cargoService.updateCargoProductStockLockCount(cps.getId(), 1)){
							service.getDbOp().rollbackTransaction();
							j.setMsg("货位库存操作失败，货位冻结库存不足！");
							return j;
						}
					}
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				System.out.print(DateUtil.getNow());e.printStackTrace();
				j.setMsg("异常");
				return j;
			} finally {
				service.releaseAll();
			}
		}
		request.getSession().removeAttribute("afterSaleBsbyMap");
		request.getSession().removeAttribute("areaId");
		j.setSuccess(true);
		j.setMsg("操作成功");
		return j;
	}
	
	/**
	 * 编辑之前的信息返回
	 * @param request
	 * @param response
	 * @param strId
	 * @return
	 */
	@RequestMapping("/beforeUpdateAfterSaleBsby")
	public String beforeAfterSaleBsby(HttpServletRequest request, HttpServletResponse response,String strId){
		
		String from = "/admin/afStock/bsbyEdit";
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败");
			return from;
		}
		
		int bsbyOperationnoteId = StringUtil.toInt(strId);
		if(bsbyOperationnoteId <= 0){
			request.setAttribute("msg", "未获取信息 请重试");
			return from;
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean(" id = " + bsbyOperationnoteId);
			if(bsbyOperationnoteBean == null){
				request.setAttribute("msg", "参数获取异常 请重试");
				return from;
			}
			
			List<AfterSaleBsbyProduct> list = afStockService.getAfterSaleBsbyProductORProductListInfo(bsbyOperationnoteId,dbOp);
			
			Map<String, AfterSaleBsbyProduct> sessionMap = new HashMap<String, AfterSaleBsbyProduct>();
			
			if(list != null && list.size() > 0){
				for (AfterSaleBsbyProduct bean : list) {
					sessionMap.put(bean.getAfterSaleDetectProductCode(), bean);
				}	
			}
			
			int type = bsbyOperationnoteBean.getType();
			String remark = bsbyOperationnoteBean.getRemark();
			BsbyReason br =service.getBsbyReasonByCondition("type = "+type+" and reason = '"+remark+"'");
			String typeName = BsbyOperationnoteBean.typeMap.get(type);
			bsbyOperationnoteBean.setTypeName(typeName);
			
			int warehouseType = bsbyOperationnoteBean.getWarehouse_type();
			
			String name = "非售后 客户库";
			if(warehouseType == ProductStockBean.STOCKTYPE_AFTER_SALE){
				name = "售后库";
			}else if(warehouseType == ProductStockBean.STOCKTYPE_CUSTOMER){
				name = "客户库";
			}
			bsbyOperationnoteBean.setWarehouse_type_name(name);
			request.setAttribute("bsbyOperationnote", bsbyOperationnoteBean);
			request.getSession().setAttribute("afterSaleBsbyMap", sessionMap);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return from;
	}
	

	/**
	 * 清空session 中 需要报损报溢的信息
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
	@RequestMapping("/remSessionAfterSaleBsbyInfo")
	@ResponseBody
	public Json remSessionAfterSaleBsbyInfo(HttpServletRequest request, HttpServletResponse response,String code){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败");
			return j;
		}
		
		if(code == null || code.length() <= 0 ){
			j.setMsg("操作失败");
			return j;
		}
		
		Map<String, AfterSaleBsbyProduct> map = new HashMap<String, AfterSaleBsbyProduct>();
		Object obj = request.getSession().getAttribute("afterSaleBsbyMap");
		if(obj != null){
			map = (Map<String, AfterSaleBsbyProduct>)obj;
		}
		if(map.containsKey(code)){
			map.remove(code);
			if(map.size()==0){
				request.getSession().removeAttribute("areaId");
			}
			j.setSuccess(true);
			j.setMsg("操作成功");
			return j;
		}else{
			j.setMsg("操作失败");
			return j;
		}
	}
	
	/**
	 * 获取session 中 需要报损报溢的信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getSessionAfterSaleBsbyInfo")
	@ResponseBody
	public EasyuiDataGridJson getSessionAfterSaleBsbyInfo(HttpServletRequest request, HttpServletResponse response){
		Map<String, AfterSaleBsbyProduct> map = new HashMap<String, AfterSaleBsbyProduct>();
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		Object obj = request.getSession().getAttribute("afterSaleBsbyMap");
		if(obj != null){
			map = (Map<String, AfterSaleBsbyProduct>)obj;
		}
		List<AfterSaleBsbyProduct> list = new ArrayList<AfterSaleBsbyProduct>();
		Iterator<Entry<String, AfterSaleBsbyProduct>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, AfterSaleBsbyProduct> next = iterator.next();
			AfterSaleBsbyProduct value = next.getValue();
			list.add(value);
		}
		j.setRows(list);
		return j;
	}
	
	/**
	 * 新增报损报溢 根据售后处理单号 获取 售后信息以及商品信息
	 * @param request
	 * @param response
	 * @param codes
	 * @return
	 */
	@RequestMapping("/afterSaleBsbyInfo")
	@ResponseBody
	public Json afterSaleBsbyInfo(HttpServletRequest request, HttpServletResponse response,String codes,int stockType){
		Json j = new Json();
		j.setSuccess(false);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		if(stockType != ProductStockBean.STOCKTYPE_AFTER_SALE && stockType != ProductStockBean.STOCKTYPE_CUSTOMER){
			j.setMsg("请选择库类型");
			return j;
		}
		codes = StringUtil.toSql(StringUtil.convertNull(codes));
		if(codes.length() <= 0){
			j.setMsg("请填写处理单号！");
			return j;
		}
		
		String[] split = codes.split("\r\n");
		codes = "";
		for (int i = 0; i < split.length; i++) {
			codes += "'" + split[i] + "',";
		}
		int len = codes.length();
		if(len <= 0){
			j.setMsg("请填写处理单号！");
			return j;
		}
		codes = codes.substring(0,len - 1);
		
		String stockTypeName = stockType == ProductStockBean.STOCKTYPE_AFTER_SALE ? "售后库" : "客户库";
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			List<AfterSaleBsbyProduct> list = afStockService.getAfterSaleBsbyProductAndProductInfo(codes,stockType,dbOp);
			
			if(list.size() > 0){
				HttpSession session = request.getSession();
				Map<String, AfterSaleBsbyProduct> sessionMap = (Map<String, AfterSaleBsbyProduct>)session.getAttribute("afterSaleBsbyMap");
				
				Integer areaValue = (Integer)session.getAttribute("areaId");
				if(areaValue==null){
					areaValue = new Integer(-1);
				}
				int areaId = areaValue.intValue();
				if(sessionMap == null){
					sessionMap = new HashMap<String, AfterSaleBsbyProduct>();
				}
				
				stockType = -1;
				Iterator<Entry<String, AfterSaleBsbyProduct>> iterator = sessionMap.entrySet().iterator();
				while(iterator.hasNext()){
					AfterSaleBsbyProduct afterSaleBsbyProduct = iterator.next().getValue();
					stockType = afterSaleBsbyProduct.getStockType();//session中库类型
					break;
				}
				
				for (AfterSaleBsbyProduct bean : list) {
					String afterSaleDetectProductCode = bean.getAfterSaleDetectProductCode();
					AfterSaleDetectProductBean detectBean = afStockService.getAfterSaleDetectProduct("code='"+afterSaleDetectProductCode+"'");
					if(detectBean==null){
						j.setMsg("售后处理单[" + afterSaleDetectProductCode + "]不存在!");
						return j;
					}
					if(!sessionMap.containsKey(afterSaleDetectProductCode)){
						//库类型 必须和添加的库类型一致
						if(stockType > 0 && stockType != bean.getStockType()){
							stockTypeName = stockType == ProductStockBean.STOCKTYPE_AFTER_SALE ? "售后库" : "客户库";
							j.setMsg("售后处理单[" + afterSaleDetectProductCode + "]所选库类型与列表库类型[" + stockTypeName + "]不一致");
							return j;
						}
						if(areaId==-1){
							areaId = detectBean.getAreaId();
							session.setAttribute("areaId", areaId);
						}
						if(detectBean.getAreaId()!=areaId){
							j.setMsg("售后处理单[" +afterSaleDetectProductCode+"]与以上所添加的处理单库地区不一致");
							return j;
						}
						sessionMap.put(afterSaleDetectProductCode, bean);
					}
				}
				
				request.getSession().setAttribute("afterSaleBsbyMap", sessionMap);
				j.setMsg("添加成功！");
			}else{
				j.setMsg("["+stockTypeName+"]未查询到所填售后处理单信息");
			}
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		j.setSuccess(true);
		return j;
	}

	
	/**
	 * 售后报损报溢单列表
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getAfBsByList")
	@ResponseBody
	public EasyuiDataGridJson getAfBsByList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		UserGroupBean group = user.getGroup();
		
		String code = StringUtil.convertNull(request.getParameter("code"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		int currentType = StringUtil.toInt(request.getParameter("currentType"));
		int type = StringUtil.toInt(request.getParameter("type"));
		int warehouseType = StringUtil.toInt(request.getParameter("warehouseType"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		WareService wareService = new WareService(dbOp);
		try {
			StringBuilder buff = new StringBuilder();
			if(!code.equals("")){
				buff.append("receipts_number = '"+code+"'");
				buff.append(" and ");
			}
			if(!startTime.equals("")&&!endTime.equals("")){
				startTime = startTime + " 00:00:00";
				endTime = endTime + " 23:59:59";
				buff.append("add_time between '"+startTime+"' and '"+endTime+"'");
				buff.append(" and ");
			}
			if(currentType != -1){
				buff.append("current_type = " + currentType);
				buff.append(" and ");
			}
			if(type != -1){
				buff.append("type = "+type);
				buff.append(" and ");
			}
			if(warehouseType != -1){
				buff.append("warehouse_type = "+warehouseType);
				buff.append(" and ");
			} else {
				buff.append("warehouse_type in (" + ProductStockBean.STOCKTYPE_AFTER_SALE + "," + ProductStockBean.STOCKTYPE_CUSTOMER + ")");
				buff.append(" and ");
			}
			
			buff.append(" if_del=0 ");
			
			int totalCount = iBsByservice.getByBsOperationnoteCount(buff.toString());
			datagrid.setTotal((long) totalCount);
			if (totalCount > 0) {
				List<BsbyOperationnoteBean> list= iBsByservice.getByBsOperationnoteList(buff.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "add_time desc");
				for (BsbyOperationnoteBean bsby : list) {
					
					bsby.setWarehouse_type_name(bsby.getWarehouse_type() == ProductStockBean.STOCKTYPE_AFTER_SALE ? "售后库" : "客户库");
					bsby.setCurrent_type_name((String)BsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(bsby.getCurrent_type())));
					bsby.setTypeName((String)BsbyOperationnoteBean.typeMap.get(Integer.valueOf(bsby.getType())));
					List<BsbyProductBean> list2 = iBsByservice.getBsbyProductList("operation_id = " + bsby.getId(), -1, -1, "id desc");
					if(list2!=null){
						StringBuffer productCodes = new StringBuffer();//获取商品编号列表
						StringBuffer price = new StringBuffer();//获取含税单价
						StringBuffer priceNotOfTax = new StringBuffer();//获取不含税单价
						StringBuffer allPrice = new StringBuffer();//获取含税总价
						StringBuffer allPriceNotOfTax = new StringBuffer();//获取不含税总价
						StringBuffer oriNames = new StringBuffer();//获取全部商品原名称
						StringBuffer counts = new StringBuffer();//单品数量
						for(BsbyProductBean bsbyProductBean : list2){
							voProduct product = wareService.getProduct(bsbyProductBean.getProduct_id());
							productCodes.append(product.getCode());
							productCodes.append("<br>");
							counts.append(bsbyProductBean.getBsby_count());
							counts.append("<br>");
							
							price.append(bsbyProductBean.getPrice());
							price.append("<br>");
							priceNotOfTax.append(bsbyProductBean.getNotaxPrice());
							priceNotOfTax.append("<br>");
							
							allPrice.append(bsbyProductBean.getBsby_count()*bsbyProductBean.getPrice());
							allPrice.append("<br>");
							allPriceNotOfTax.append(bsbyProductBean.getBsby_count()*bsbyProductBean.getNotaxPrice());
							allPriceNotOfTax.append("<br>");
							
							oriNames.append(product.getOriname());
							oriNames.append("<br>");
						}
						 
						bsby.setProductCode(productCodes.toString());
						bsby.setCounts(counts.toString());
						bsby.setPrice(price.toString());
						bsby.setPriceNotOfTax(priceNotOfTax.toString());
						bsby.setAllPrice(allPrice.toString());
						bsby.setAllPriceNotOfTax(allPriceNotOfTax.toString());
						bsby.setOriname(oriNames.toString());
					}
				}
				
				datagrid.setRows(list);
				List<Map<String, Boolean>> footer = new ArrayList<Map<String, Boolean>>();
				HashMap<String, Boolean> map = new HashMap<String, Boolean>();
				boolean finAuditFlag = false;//运营审核
				boolean auditFlag = false;//财务审核
				boolean editFlag = false;
				if (group.isFlag(1470)) {
					finAuditFlag = true;
				}
				if (group.isFlag(1469)) {
					auditFlag = true;
				}
				if (group.isFlag(1471)) {
					editFlag = true;
				}
				map.put("finAuditFlag", finAuditFlag);
				map.put("auditFlag", auditFlag);
				map.put("editFlag", editFlag);
				footer.add(map);
				datagrid.setFooter(footer);
			} else {
				datagrid.setRows(new ArrayList<BsbyOperationnoteBean>());
			}
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 售后报损报溢单对应售后单信息
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getAfBsByProductList")
	@ResponseBody
	public EasyuiDataGridJson getAfBsByProductList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		int id = StringUtil.toInt(request.getParameter("id"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			if (id == -1) {
				request.setAttribute("msg", "报损报溢单id错误！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			BsbyOperationnoteBean bsbyBean = iBsByservice.getBsbyOperationnoteBean("id=" + id);
			if (bsbyBean == null) {
				request.setAttribute("msg", "报损报溢单不存在！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			
			int totalCount = afStockService.getAfterSaleBsbyProductCount("bsby_operationnote_id = " + bsbyBean.getId());
			datagrid.setTotal((long) totalCount);
			if (totalCount > 0) {
				List<AfterSaleBsbyProduct> afterSaleBsbyProductList = afStockService.getAfterSaleBsbyProductList("bsby_operationnote_id = " + bsbyBean.getId(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), null);
				for (AfterSaleBsbyProduct afterSaleBsbyProduct : afterSaleBsbyProductList) {
					voProduct product = wareService.getProduct(afterSaleBsbyProduct.getProductId());
					if (product != null) {
						afterSaleBsbyProduct.setProductName(product.getName());
						afterSaleBsbyProduct.setProductCode(product.getCode());
						int bsbyOperationNoteId = afterSaleBsbyProduct.getBsbyOperationnoteId();
						StringBuffer sb = new StringBuffer();
						BsbyProductBean spb = iBsByservice.getBsbyProductBean("product_id = "+product.getId()+" and operation_id = "+bsbyOperationNoteId);
						sb.append(spb.getPrice());
						sb.append("("+spb.getNotaxPrice()+")");
						afterSaleBsbyProduct.setPrice(sb.toString());
					}
					afterSaleBsbyProduct.setAfterSaleDetectProductStatusName(AfterSaleDetectProductBean.statusMap.get(afterSaleBsbyProduct.getAfterSaleDetectProductStatus()));
				}
				datagrid.setRows(afterSaleBsbyProductList);
			} else {
				datagrid.setRows(null);
			}
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 报损报溢单信息
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBsbyInfo")
	@ResponseBody
	public Json getBsbyInfo(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		int id = StringUtil.toInt(request.getParameter("id"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			if (id == -1) {
				j.setMsg("报损报溢单id错误！");
				return j;
			}
			
			BsbyOperationnoteBean bsbyBean = iBsByservice.getBsbyOperationnoteBean("id=" + id);
			if (bsbyBean == null) {
				j.setMsg("报损报溢单不存在！");
				return j;
			}
			String typeName = BsbyOperationnoteBean.typeMap.get(bsbyBean.getType());
			StringBuffer buff = new StringBuffer("<table class=\"tableForm\">");
			buff.append("<tr align=\"center\" >");
			buff.append("<th>单据编号：</th>");
			buff.append("<td>").append(bsbyBean.getReceipts_number()).append("</td>");
			buff.append("<th>单据类型：</th>");
			buff.append("<td>").append(typeName).append("</td>");
			buff.append("<th>库类型：</th>");
			buff.append("<td>").append(bsbyBean.getWarehouse_type() == ProductStockBean.STOCKTYPE_AFTER_SALE ? "售后库" : "客户库").append("</td>");
			buff.append("<th>商品数量：</th>");
			int count = afStockService.getAfterSaleBsbyProductCount("bsby_operationnote_id = " + bsbyBean.getId());
			bsbyBean.setProductCount(count);
			buff.append("<td>").append(bsbyBean.getProductCount()).append("</td>");
			buff.append("<th>生成时间：</th>");
			buff.append("<td>").append(bsbyBean.getAdd_time().substring(0, 10)).append("</td>");
			buff.append("<th>生成人：</th>");
			buff.append("<td>").append(bsbyBean.getOperator_name()).append("</td>");
			buff.append("</tr>");
			buff.append("<tr align=\"center\" >");
			buff.append("<th>").append(typeName).append("原因：</th>");
			buff.append("<td>").append(bsbyBean.getRemark()).append("</td>");
			buff.append("</tr>");
			buff.append("</table>");
			
			j.setSuccess(true);
			j.setObj(buff.toString());
			return j;
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 报损报溢单审核权限判断
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBsbyAudit")
	@ResponseBody
	public Json getBsbyAudit(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		
		int id = StringUtil.toInt(request.getParameter("id"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			if (id == -1) {
				j.setMsg("报损报溢单id错误！");
				return j;
			}
			
			BsbyOperationnoteBean bsbyBean = iBsByservice.getBsbyOperationnoteBean("id=" + id);
			if (bsbyBean == null) {
				j.setMsg("报损报溢单不存在！");
				return j;
			}
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			if (bsbyBean.getCurrent_type() == BsbyOperationnoteBean.fin_audit_sus && group.isFlag(1469)) {
				map.put("auditFlag", true);
				map.put("finAuditFlag", false);
			} else if (bsbyBean.getCurrent_type() == BsbyOperationnoteBean.audit_ing && group.isFlag(1470)) {
				map.put("finAuditFlag", true);
				map.put("auditFlag", false);
			} else {
				map.put("auditFlag", false);
				map.put("finAuditFlag", false);
			}
			j.setSuccess(true);
			j.setObj(map);
			return j;
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	
	/**
	 * 财务审核售后报损报溢单
	 * status:0为不通过，1为通过
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/auditBsby")
	@ResponseBody
	public Json auditBsby(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(1469)) {
			j.setMsg("您没有财务审核权限！");
			return j;
		}
		
		int id = StringUtil.toInt(request.getParameter("id"));
		int status = StringUtil.toInt(request.getParameter("status"));
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try {
			synchronized (lock) {
				if (id == -1) {
					j.setMsg("报损报溢单id错误！");
					return j;
				}
				
				if (status != 0 && status != 1) {
					j.setMsg("status错误！");
					return j;
				}
				
				int type = status == 0 ? BsbyOperationnoteBean.audit_Fail : BsbyOperationnoteBean.audit_end;
				
				if (remark.trim().equals("")) {
					j.setMsg("审核意见不能为空！");
					return j;
				}
				
				BsbyOperationnoteBean bsbyBean = iBsByservice.getBsbyOperationnoteBean("id=" + id);
				if (bsbyBean == null) {
					j.setMsg("报损报溢单不存在！");
					return j;
				}
				
				if (bsbyBean.getCurrent_type() != BsbyOperationnoteBean.fin_audit_sus) {
					j.setMsg("单据已经是"+BsbyOperationnoteBean.current_typeMap.get(bsbyBean.getCurrent_type())+"!");
					return j;
				}
				
				if(bsbyBean.getWarehouse_type()==ProductStockBean.STOCKTYPE_AFTER_SALE && bsbyBean.getType()==BsbyOperationnoteBean.TYPE1){
					j.setObj(91);
				}
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory("status <>" + AfterSaleInventory.COMPLETE_CHECK
						+ " and stock_area=" + bsbyBean.getWarehouse_area() + " and stock_type=" + bsbyBean.getWarehouse_type());
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				iBsByservice.getDbOp().startTransaction();
				//审核通过更新库存
				if (status == 1) {
					Json json = updateStock(bsbyBean, request, response, iBsByservice.getDbOp());
					if (!json.isSuccess()) {
						iBsByservice.getDbOp().rollbackTransaction();
						j.setMsg(json.getMsg());
						return j;
					}
					
					/**
					 * 单据状态改为完成 就要改变库存 如果是报损就要剪掉库存 如果是报溢就要添加批次 如果没有调整的产品
					 * 就不执行这个方法
					 */
					List<BsbyProductBean> bsbyProductBeanList =  iBsByservice.getBsbyProductList("operation_id="+id, -1, -1, null);
					for (BsbyProductBean bsbyProductBean : bsbyProductBeanList) {
						int beforeChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bsbyBean.getWarehouse_area(), bsbyBean.getWarehouse_type());
	
	
						/**
						 * 更改为完成后,要将最后的库存和改变后的库存的量记录
						 */
						int afterChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bsbyBean.getWarehouse_area(), bsbyBean.getWarehouse_type());
	
	
						if(!iBsByservice.updateBsbyProductBean("before_change="+beforeChangeProductCount+", after_change="+afterChangeProductCount, "id="+bsbyProductBean.getId()))
						{
							iBsByservice.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
					}
				} else {
					if (bsbyBean.getType() == BsbyOperationnoteBean.TYPE0) {
						List<AfterSaleBsbyProduct> list = afStockService.getAfterSaleBsbyProductList("bsby_operationnote_id=" + bsbyBean.getId(), -1, -1, null);
						if (list != null && list.size() > 0) {
							for (AfterSaleBsbyProduct asbp : list) {
								String sql = "product_id = " + asbp.getProductId() + " and "
										+ "area = " + bsbyBean.getWarehouse_area() + " and type = "
										+ bsbyBean.getWarehouse_type();
								ProductStockBean psBean = psService.getProductStock(sql);
								//增加库存
								if(!psService.updateProductStockCount(psBean.getId(), 1)){
									iBsByservice.getDbOp().rollbackTransaction();
									j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
									return j;
								}
								//减去库存锁定量
								if (!psService.updateProductLockCount(psBean.getId(), -1)) {
									iBsByservice.getDbOp().rollbackTransaction();
									j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
									return j;
								}
								
								CargoInfoBean cib = cargoService.getCargoInfo("whole_code='" + asbp.getWholeCode() + "'");
								if (cib == null) {
									iBsByservice.getDbOp().rollbackTransaction();
									j.setMsg("没有找到货位！");
									return j;
								}
								
								CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id=" + cib.getId() + " and product_id=" + asbp.getProductId());
								if (cpsb == null) {
									iBsByservice.getDbOp().rollbackTransaction();
									j.setMsg("没有找到货位库存信息!");
									return j;
								}
								
								//解锁货位库存
								if(!cargoService.updateCargoProductStockCount(cpsb.getId(), 1)){
									iBsByservice.getDbOp().rollbackTransaction();
									j.setMsg("货位库存操作失败，货位库存不足！");
									return j;
								}
								if(!cargoService.updateCargoProductStockLockCount(cpsb.getId(), -1)){
									iBsByservice.getDbOp().rollbackTransaction();
									j.setMsg("货位库存操作失败，货位冻结库存不足！");
									return j;
								}
							}
						}
					}
				}
				// 如果是改为已完成 就要添加审核人的信息
				if(!iBsByservice.updateBsbyOperationnoteBean("fin_audit_remark='" + StringUtil.toSql(remark) + "',current_type=" + type + " , fin_audit_datetime='"
						+ DateUtil.getNow() + "' , fin_audit_id=" + user.getId() + " , fin_audit_name='"
						+ user.getUsername() + "'", "id=" + id)){
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("报损报溢单更新失败！");
					return j;
				}
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bsbyBean.getReceipts_number() + "的状态为:"+BsbyOperationnoteBean.current_typeMap.get(type) + ",审核意见为:" + StringUtil.toSql(remark));
				bsbyOperationRecordBean.setOperation_id(bsbyBean.getId());
				if (!iBsByservice.addBsbyOperationRecord(bsbyOperationRecordBean)) {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("报损报溢单操作日志记录失败！");
					return j;
				}
				
				if (!afStockService.updateAfterSaleBsbyProduct("status=" + AfterSaleBsbyProduct.STATUS2, "bsby_operationnote_id="+bsbyBean.getId())) {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("更新报损报溢单状态失败！");
					return j;
				}
				
				iBsByservice.getDbOp().commitTransaction();
				j.setSuccess(true);
				j.setMsg("财务审核完成！");
				return j;
			}
		} catch (Exception e) {
			iBsByservice.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 运营审核售后报损报溢单
	 * status:0为不通过，1为通过
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/finAuditBsby")
	@ResponseBody
	public Json finAuditBsby(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(1470)) {
			j.setMsg("您没有运营审核权限！");
			return j;
		}
		
		int id = StringUtil.toInt(request.getParameter("id"));
		int status = StringUtil.toInt(request.getParameter("status"));
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try {
			synchronized (lock) {
				if (id == -1) {
					j.setMsg("报损报溢单id错误！");
					return j;
				}
				
				if (status != 0 && status != 1) {
					j.setMsg("status错误！");
					return j;
				}
				
				int type = status == 0 ? BsbyOperationnoteBean.fin_audit_Fail : BsbyOperationnoteBean.fin_audit_sus;
				
				if (remark.trim().equals("")) {
					j.setMsg("运营审核意见不能为空！");
					return j;
				}
				
				BsbyOperationnoteBean bsbyBean = iBsByservice.getBsbyOperationnoteBean("id=" + id);
				if (bsbyBean == null) {
					j.setMsg("报损报溢单不存在！");
					return j;
				}
				
				if (bsbyBean.getCurrent_type() != BsbyOperationnoteBean.audit_ing) {
					j.setMsg("单据已经是" + BsbyOperationnoteBean.current_typeMap.get(bsbyBean.getCurrent_type())+"!");
					return j;
				}
				
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory("status <>" + AfterSaleInventory.COMPLETE_CHECK
						+ " and stock_area=" + bsbyBean.getWarehouse_area() + " and stock_type=" + bsbyBean.getWarehouse_type());
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				iBsByservice.getDbOp().startTransaction();
				
				if (!iBsByservice.updateBsbyOperationnoteBean("examineSuggestion='" + StringUtil.toSql(remark) + "',current_type=" + type + ",end_time='"+DateUtil.getNow()
						+"',end_oper_id="+user.getId()+",end_oper_name='"+user.getUsername()+"'", "id=" + id)) {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("报损报溢单更新失败！");
					return j;
				}
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bsbyBean.getReceipts_number() + "的状态为:" + BsbyOperationnoteBean.current_typeMap.get(type) + ",财务审核意见为:" + StringUtil.toSql(remark));
				bsbyOperationRecordBean.setOperation_id(bsbyBean.getId());
				if (!iBsByservice.addBsbyOperationRecord(bsbyOperationRecordBean)) {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("报损报溢单操作日志记录失败！");
					return j;
				}
				
				if (status == 0 && bsbyBean.getType() == BsbyOperationnoteBean.TYPE0) {
					List<AfterSaleBsbyProduct> list = afStockService.getAfterSaleBsbyProductList("bsby_operationnote_id=" + bsbyBean.getId(), -1, -1, null);
					if (list != null && list.size() > 0) {
						for (AfterSaleBsbyProduct asbp : list) {
							String sql = "product_id = " + asbp.getProductId() + " and "
									+ "area = " + bsbyBean.getWarehouse_area() + " and type = "
									+ bsbyBean.getWarehouse_type();
							ProductStockBean psBean = psService.getProductStock(sql);
							//增加库存
							if(!psService.updateProductStockCount(psBean.getId(), 1)){
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
								return j;
							}
							//减去库存锁定量
							if (!psService.updateProductLockCount(psBean.getId(), -1)) {
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
								return j;
							}
							
							CargoInfoBean cib = cargoService.getCargoInfo("whole_code='" + asbp.getWholeCode() + "'");
							if (cib == null) {
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("没有找到货位！");
								return j;
							}
							
							CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id=" + cib.getId() + " and product_id=" + asbp.getProductId());
							if (cpsb == null) {
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("没有找到货位库存信息!");
								return j;
							}
							
							//解锁货位库存
							if(!cargoService.updateCargoProductStockCount(cpsb.getId(), 1)){
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("货位库存操作失败，货位库存不足！");
								return j;
							}
							if(!cargoService.updateCargoProductStockLockCount(cpsb.getId(), -1)){
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("货位库存操作失败，货位冻结库存不足！");
								return j;
							}
						}
					}
				}

				iBsByservice.getDbOp().commitTransaction();
				j.setSuccess(true);
				j.setMsg("运营审核完成！");
				return j;
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			iBsByservice.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 当审核完毕后 就要根据报损和报溢 变化库存
	 * 
	 * @param bean
	 */
	public static Json updateStock(BsbyOperationnoteBean bean, HttpServletRequest request, HttpServletResponse response, DbOperation dbOp) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		WareService wareService = new WareService(dbOp);
		IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, bsbyservice.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,
				service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		 IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		//财务基础数据
		List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
		try {
			int bybs_type = bean.getType();// 单据类型

			// 得到这个单据中的所有的要修改库存的商品
			List<AfterSaleBsbyProduct> list = afStockService.getAfterSaleBsbyProductList("bsby_operationnote_id=" + bean.getId(), -1, -1, null);
			if (list.size() != 0) {
				Iterator it = list.iterator();
				for (; it.hasNext();) {
					AfterSaleBsbyProduct bsbyProductBean = (AfterSaleBsbyProduct) it.next();
					int productId = bsbyProductBean.getProductId();
					// 每一个单据中的产品 依次进行修改库存操作
					String titleString = "";
					voProduct product = wareService.getProduct(productId);
					// 得到这个产品的所有库存的列表
					product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
					
					BsbyProductBean bsbyProduct = iBsByservice.getBsbyProductBean("operation_id=" + bean.getId() + " and product_id=" + productId);
//					 float price = iBsBys
					// 出库 报损就是出库
					if (bybs_type == BsbyOperationnoteBean.TYPE0) {
						titleString = "报损";

						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(DateUtil.getNow());
						bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存"
								+ product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
						bsbyOperationRecordBean.setOperation_id(bean.getId());
						bsbyOperationRecordBean.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean)){
							j.setMsg("添加报损报溢单操作日志失败！");
							return j;
						}

						// 更新指定库的库存

						ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area="
								+ bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
								null));

						if (ps == null) {
							j.setMsg("没有找到产品库存，操作失败！");
							return j;
						}
						//审核完成，清除库存锁定量
						if (!psService.updateProductLockCount(ps.getId(), -1)) {
							j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
							return j;
						}
						CargoInfoBean cib = cargoService.getCargoInfo("whole_code='" + bsbyProductBean.getWholeCode() + "'");
						if (cib == null) {
							j.setMsg("没有找到货位！");
							return j;
						}
						
						CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id=" + cib.getId() + " and product_id=" + bsbyProductBean.getProductId());
						if (cpsb == null) {
							j.setMsg("没有找到货位库存信息!");
							return j;
						}
						//审核完成，减货位库存锁定量
						if(!cargoService.updateCargoProductStockLockCount(cpsb.getId(), -1)){
							j.setMsg("货位库存操作失败，货位冻结库存不足！");
							return j;
						}

						BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();

						bsbyOperationRecordBean1.setOperator_id(user.getId());
						bsbyOperationRecordBean1.setOperator_name(user.getUsername());
						bsbyOperationRecordBean1.setTime(DateUtil.getNow());
						bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品"
								+ product.getCode() + "出库1");
						bsbyOperationRecordBean1.setOperation_id(bean.getId());
						bsbyOperationRecordBean1.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1)){
							j.setMsg("添加报损报溢单操作日志失败");
							return j;
						}

						if (bean.getWarehouse_type() != ProductStockBean.STOCKTYPE_CUSTOMER) {
							
							//财务基础数据列表
							BaseProductInfo base = new BaseProductInfo();
							base.setId(productId);
							base.setProductStockId(ps.getId());
							base.setOutCount(1);
							base.setPrice(bsbyProduct.getPrice());
							base.setNotaxPrice(bsbyProduct.getNotaxPrice());
							baseList.add(base);
							
							// 更新批次记录
//							List sbList = service.getStockBatchList("product_id=" + productId + " and stock_type="
//									+ bean.getWarehouse_type() + " and stock_area=" + bean.getWarehouse_area(), -1, -1,
//							"id asc");
//							float stockOutPrice = 0;
//							if (sbList != null && sbList.size() != 0) {
//								int stockExchangeCount = 1;//要报损的产品数量
//								int index = 0;
//								int batchCount = 0;//当次要报损的数量
//	
//								do {
//									StockBatchBean batch = (StockBatchBean) sbList.get(index);
//									if (stockExchangeCount >= batch.getBatchCount()) {
//										//如果报损的数量大于当前批次的数量 就删除这个批次. 那这次要报损的数量就是这个批次的数量
//										if(!service.deleteStockBatch("id=" + batch.getId())){
//											j.setMsg("删除批次操作失败！");
//											return j;
//										}
//										batchCount = batch.getBatchCount();
//									} else {
//										//如果报损数量小于当前批次数量, 那就改变这个批次中的存货厕数量,那这次出货的数量就是剩下的数量
//										if(!service.updateStockBatch("batch_count = batch_count-" + stockExchangeCount,
//												"id=" + batch.getId())){
//											j.setMsg("更新批次操作失败！");
//											return j;
//										}
//										batchCount = stockExchangeCount;
//									}
//	
//									// 添加批次操作记录
//									StockBatchLogBean batchLog = new StockBatchLogBean();
//									batchLog.setCode(bean.getReceipts_number());
//									batchLog.setStockType(batch.getStockType());
//									batchLog.setStockArea(batch.getStockArea());
//									batchLog.setBatchCode(batch.getCode());
//									batchLog.setBatchCount(1);
//									batchLog.setBatchPrice(batch.getPrice());
//									batchLog.setProductId(batch.getProductId());
//									batchLog.setRemark("报损");
//									batchLog.setCreateDatetime(DateUtil.getNow());
//									batchLog.setUserId(user.getId());
//									if(!service.addStockBatchLog(batchLog)){
//										j.setMsg("批次操作记录添加失败，请重新尝试操作！");
//										return j;
//									}
//									
//									//财务
//									int ticket = FinanceSellProductBean.queryTicket(dbOp, batch.getCode());	//是否含票 
//									if(ticket == -1){
//										j.setMsg("查询异常，请与管理员联系！");
//										return j;
//									}
//									FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//									if(fProduct == null){
//										j.setMsg("查询异常，请与管理员联系！");
//										return j;
//									}
//									int _count = FinanceProductBean.queryCountIfTicket(dbOp, batch.getProductId(), ticket);
//									float price5 = product.getPrice5();
//			    					int totalCount = product.getStockAll() + product.getLockCountAll();
//									float priceSum = Arith.mul(price5, totalCount);
//									float priceHasticket = fProduct.getPriceHasticket();
//									float priceNoticket = fProduct.getPriceNoticket();
//									float priceSumHasticket = 0;
//									float priceSumNoticket = 0;
//									String set = "price_sum =" + priceSum;
//									if(ticket == 0){	//0-有票
//										priceSumHasticket = Arith.mul(priceHasticket,  _count);
//										set += ", price_sum_hasticket =" + priceSumHasticket;
//									}
//									if(ticket == 1){	//1-无票
//										priceSumNoticket = Arith.mul(priceNoticket,  _count);
//										set += ", price_sum_noticket =" + priceSumNoticket;
//									}
//									if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
//										j.setMsg("数据库操作失败");
//										return j;
//									}
//									
//									//财务进销存卡片
//									product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
//											null));
//									int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), bean.getWarehouse_area(), bean.getWarehouse_type(), ticket, batch.getProductId());
//									int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, bean.getWarehouse_type(), ticket, batch.getProductId());
//									int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(),  bean.getWarehouse_area(), -1,ticket, batch.getProductId());
//									FinanceStockCardBean fsc = new FinanceStockCardBean();
//									fsc.setCardType(StockCardBean.CARDTYPE_LOSE);
//									fsc.setCode(bean.getReceipts_number());
//									fsc.setCreateDatetime(DateUtil.getNow());
//									fsc.setStockType(bean.getWarehouse_type());
//									fsc.setStockArea(bean.getWarehouse_area());
//									fsc.setProductId(batch.getProductId());
//									fsc.setStockId(ps.getId());
//									fsc.setStockInCount(batchCount);
//									fsc.setStockAllArea(stockAllArea);
//									fsc.setStockAllType(stockAllType);
//									fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//									fsc.setStockPrice(product.getPrice5());
//									
//									fsc.setCurrentStock(currentStock);
//									fsc.setType(fsc.getCardType());
//									fsc.setIsTicket(ticket);
//									fsc.setStockBatchCode(batch.getCode());
//									fsc.setBalanceModeStockCount(_count);
//									if(ticket == 0){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), batchCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//									}
//									if(ticket == 1){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), batchCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//									}
//									double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//									fsc.setAllStockPriceSum(tmpPrice);
//									if(!frfService.addFinanceStockCardBean(fsc))
//									{
//										j.setMsg("数据库操作失败");
//										return j;
//									}
//									//---------------财务-----------
//									
//									//出货的总金额
//									stockOutPrice = stockOutPrice + batch.getPrice() * batchCount;
//	
//									stockExchangeCount -= batch.getBatchCount();
//									index++;
//								} while (stockExchangeCount > 0 && index < sbList.size());
//	
//							}
						}

						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
								null));
						CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+cpsb.getId());

						// 出库卡片
						StockCardBean sc = new StockCardBean();

						sc.setCardType(StockCardBean.CARDTYPE_LOSE);// 出库就是报损
						sc.setCode(bean.getReceipts_number());

						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(bean.getWarehouse_type());
						sc.setStockArea(bean.getWarehouse_area());
						sc.setProductId(productId);
						sc.setStockId(ps.getId());
						sc.setStockOutCount(1);
						sc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType())
								+ product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
						sc.setStockAllArea(product.getStock(bean.getWarehouse_area())
								+ product.getLockCount(bean.getWarehouse_area()));
						sc.setStockAllType(product.getStockAllType(sc.getStockType())
								+ product.getLockCountAllType(sc.getStockType()));
						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
						sc.setStockPrice(product.getPrice5());
						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
								new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!psService.addStockCard(sc)){
							j.setMsg("进销存记录添加失败，请重新尝试操作！");
							return j;
						}
						
						//货位出库卡片
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_LOSE);
						csc.setCode(bean.getReceipts_number());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(bean.getWarehouse_type());
						csc.setStockArea(bean.getWarehouse_area());
						csc.setProductId(productId);
						csc.setStockId(cps.getId());
						csc.setStockOutCount(1);
						csc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
						csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
						csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						if(!cargoService.addCargoStockCard(csc)){
							j.setMsg("货位进销存记录添加失败，请重新尝试操作！");
							return j;
						}

					}
					// 入库
					else {
						titleString = "报溢";

						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(DateUtil.getNow());
						bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存"
								+ product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
						bsbyOperationRecordBean.setOperation_id(bean.getId());
						bsbyOperationRecordBean.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean)){
							j.setMsg("添加报损单操作日志失败！");
							return j;
						}
						product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));

						ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area="
								+ bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
						if (ps == null) {
							j.setMsg("没有找到产品库存，操作失败！");
							return j;
						}
						if (!psService.updateProductStockCount(ps.getId(), 1)) {
							j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
							return j;
						}
						
						CargoInfoBean cib = cargoService.getCargoInfo("whole_code='" + bsbyProductBean.getWholeCode() + "'");
						if (cib == null) {
							j.setMsg("没有找到货位！");
							return j;
						}
						
						CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id=" + cib.getId() + " and product_id=" + bsbyProductBean.getProductId());
						if (cpsb == null) {
							j.setMsg("没有找到货位库存信息!");
							return j;
						}
						
						//审核完成，增加货位库存量
						if(!cargoService.updateCargoProductStockCount(cpsb.getId(), 1)){
							j.setMsg("货位库存操作失败，货位库存不足！");
							return j;
						}

						BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();
						bsbyOperationRecordBean1.setOperator_id(user.getId());
						bsbyOperationRecordBean1.setOperator_name(user.getUsername());
						bsbyOperationRecordBean1.setTime(DateUtil.getNow());
						bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品"
								+ product.getCode() + "入库1");
						bsbyOperationRecordBean1.setOperation_id(bean.getId());
						bsbyOperationRecordBean1.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1)){
							j.setMsg("报损报溢单添加操作日志失败！");
							return j;
						}
						
						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
						CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = " + cpsb.getId());

						// 入库卡片
						StockCardBean sc = new StockCardBean();
						sc.setCardType(StockCardBean.CARDTYPE_GET);
						sc.setCode(bean.getReceipts_number());

						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(bean.getWarehouse_type());
						sc.setStockArea(bean.getWarehouse_area());
						sc.setProductId(productId);
						sc.setStockId(ps.getId());
						sc.setStockInCount(1);
						sc.setStockInPriceSum(product.getPrice5()*1);

						sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType())
								+ product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
						sc.setStockAllArea(product.getStock(bean.getWarehouse_area())
								+ product.getLockCount(bean.getWarehouse_area()));
						sc.setStockAllType(product.getStockAllType(sc.getStockType())
								+ product.getLockCountAllType(sc.getStockType()));
						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
						sc.setStockPrice(product.getPrice5());// 新的库存价格
						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
								new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!psService.addStockCard(sc)){
							j.setMsg("进销存记录添加失败，请重新尝试操作！");
							return j;
						}
						
						//货位入库卡片
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_GET);
						csc.setCode(bean.getReceipts_number());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(bean.getWarehouse_type());
						csc.setStockArea(bean.getWarehouse_area());
						csc.setProductId(productId);
						csc.setStockId(cps.getId());
						csc.setStockInCount(1);
						csc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
						csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
						csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!cargoService.addCargoStockCard(csc)){
							j.setMsg("货位进销存添加失败，请重新尝试操作！");
							return j;
						}
						
						//售后库报溢审核通过后，需要生成售后处理单的售后入库单
						if(cib.getStockType()==ProductStockBean.STOCKTYPE_AFTER_SALE){
							AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("code='" + bsbyProductBean.getAfterSaleDetectProductCode() + "'");
							if(detectProductBean==null){
								j.setMsg("售后处理单"+bsbyProductBean.getAfterSaleDetectProductCode()+"不存在!");
								return j;
							}
							AfterSaleStockin stockin = new AfterSaleStockin();
							stockin.setAfterSaleDetectProductId(detectProductBean.getId());
							stockin.setProductId(productId);
							stockin.setOutCargoWholeCode("");
							stockin.setInCargoWholeCode(cps.getCargoInfo().getWholeCode());
							stockin.setStatus(AfterSaleStockin.STATUS1);
							stockin.setCreateUserId(user.getId());
							stockin.setCreateUserName(user.getUsername());
							stockin.setCreateDatetime(DateUtil.getNow());
							stockin.setCompleteUserId(user.getId());
							stockin.setCompleteUserName(user.getUsername());
							stockin.setCompleteDatetime(DateUtil.getNow());
							stockin.setType(AfterSaleStockin.TYPE5);
							stockin.setOrderCode("");
							
							if (!afStockService.addAfterSaleStockin(stockin)) {
								j.setMsg("添加售后入库单失败");
								return j;
							}
						}
						if (bean.getWarehouse_type() != ProductStockBean.STOCKTYPE_CUSTOMER) {
							//财务基础数据
							BaseProductInfo base = new BaseProductInfo();
							base.setId(productId);
							base.setInCount(1);//一个处理单就有一个商品
							base.setInPrice(product.getPrice5());
							base.setProductStockId(ps.getId());
							base.setPrice(bsbyProduct.getPrice());
							base.setNotaxPrice(bsbyProduct.getNotaxPrice());
							baseList.add(base);
						}
						
//						if (bean.getWarehouse_type() != ProductStockBean.STOCKTYPE_CUSTOMER) {
//							// 添加批次记录
//							String code = "Q" + DateUtil.getNow().substring(0, 10).replace("-", "");
//							StockBatchBean batch;
//							batch = service.getStockBatch("code like '" + code + "%'");
//							int ticket = 0;
//							if (batch == null) {
//								// 当日第一份批次记录，编号最后三位 001
//								code += "001";
//							} else {
//								// 获取当日计划编号最大值
//								int maxid = service.getNumber("id", "stock_batch", "max", "id > 0 and code like '"
//										+ code + "%'");
//								batch = service.getStockBatch("id =" + maxid);
//								String _code = batch.getCode();
//								int number = Integer.parseInt(_code.substring(_code.length() - 3));
//								number++;
//								code += String.format("%03d", new Object[] { new Integer(number) });
//							}
//							batch = new StockBatchBean();
//							batch.setCode(code);
//							batch.setProductId(productId);
//							batch.setPrice(product.getPrice5());// 报溢的产品的价格是现有价格
//							batch.setBatchCount(1);
//							batch.setProductStockId(ps.getId());
//							batch.setStockArea(bean.getWarehouse_area());
//							batch.setStockType(bean.getWarehouse_type());
//							batch.setCreateDateTime(DateUtil.getNow());
//							batch.setTicket(ticket);
//							if(!service.addStockBatch(batch)){
//								j.setMsg("批次添加失败，请重新尝试操作！");
//								return j;
//							}
//	
//							// 添加批次操作记录
//							StockBatchLogBean batchLog = new StockBatchLogBean();
//							batchLog.setCode(bean.getReceipts_number());
//							batchLog.setStockType(batch.getStockType());
//							batchLog.setStockArea(batch.getStockArea());
//							batchLog.setBatchCode(batch.getCode());
//							batchLog.setBatchCount(batch.getBatchCount());
//							batchLog.setBatchPrice(batch.getPrice());
//							batchLog.setProductId(batch.getProductId());
//							batchLog.setRemark("报溢");
//							batchLog.setCreateDatetime(DateUtil.getNow());
//							batchLog.setUserId(user.getId());
//							if(!service.addStockBatchLog(batchLog)){
//								j.setMsg("批次操作记录添加失败，请重新尝试操作！");
//								return j;
//							}
//							
//							//财务
//	    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + productId);
//	    					if(fProduct == null){
//								j.setMsg("查询异常，请与管理员联系！");
//								return j;
//							}
//	    					float price5 = product.getPrice5();
//	    					int totalCount = product.getStockAll() + product.getLockCountAll();
//							float priceSum = Arith.mul(price5, totalCount);
//							int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), productId, ticket);
//							int stockinCount = 1;
//							float priceHasticket = fProduct.getPriceHasticket();
//							float priceSumHasticket = Arith.mul(priceHasticket,  _count);
//							String set = "price =" + price5 + ", price_sum =" + priceSum + ", price_sum_hasticket =" + priceSumHasticket;
//							if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())) {
//								j.setMsg("数据操作失败！");
//								return j;
//							}
//							//财务进销存卡片
//	    					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, productId);
//	    					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, batchLog.getStockType(), ticket,productId);
//							int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), -1,ticket, productId);
//	    					FinanceStockCardBean fsc = new FinanceStockCardBean();
//	    					fsc.setCardType(StockCardBean.CARDTYPE_GET);//StockCardBean.CARDTYPE_CANCELORDERSTOCKIN-->StockCardBean.CARDTYPE_GET
//	    					fsc.setCode(bean.getReceipts_number());
//	    					fsc.setCreateDatetime(DateUtil.getNow());
//	    					fsc.setStockType(batchLog.getStockType());
//	    					fsc.setStockArea(batchLog.getStockArea());
//	    					fsc.setProductId(productId);
//	    					fsc.setStockId(ps.getId());
//	    					fsc.setStockInCount(batchLog.getBatchCount());	
//	    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//	    					fsc.setStockAllArea(stockAllArea);
//	    					fsc.setStockAllType(stockAllType);
//	    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//	    					fsc.setStockPrice(price5);
//	    					
//	    					fsc.setType(fsc.getCardType());
//	    					fsc.setIsTicket(ticket);
//	    					fsc.setStockBatchCode(batchLog.getBatchCode());
//	    					fsc.setBalanceModeStockCount(_count);
//	    					fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(priceHasticket, stockinCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//	    					double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//	    					fsc.setAllStockPriceSum(tmpPrice);
//	    					if(!frfService.addFinanceStockCardBean(fsc)) {
//	    						j.setMsg("数据操作失败！");
//	    						return j;
//	    					}
//	    					//-----------财务-------------
//						}
					}
					
					int updateStatus = bybs_type == BsbyOperationnoteBean.TYPE0 ? AfterSaleDetectProductBean.LOCK_STATUS2 : AfterSaleDetectProductBean.LOCK_STATUS0;
					String set = "lock_status=" + updateStatus;
					//2014-11-12 李宁 售后库报溢单审核成功需要更新售后处理单状态为报溢已完成
					if(bybs_type==BsbyOperationnoteBean.TYPE1 && bean.getWarehouse_type()==ProductStockBean.STOCKTYPE_AFTER_SALE){
						set += ",status=" + AfterSaleDetectProductBean.STATUS24;
					}
					if (!afStockService.updateAfterSaleDetectProduct(set, "code='" + bsbyProductBean.getAfterSaleDetectProductCode() + "'")) {
						j.setMsg("更新售后处理单状态失败！");
						return j;
					}
				}

				//报损报溢出调用财务接口
				if(baseList.size() > 0){
					if(bybs_type == 0){
						//报损
						FinanceBaseDataService bsBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_LOSE, service.getDbOp().getConn());
						bsBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
					}else{
						//报溢
						FinanceBaseDataService byBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_GET, service.getDbOp().getConn());
						byBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
					}
				}
				
				
				
				// 操作完成记录 bsby

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "完成更改库存操作");
				bsbyOperationRecordBean.setOperation_id(bean.getId());
				bsbyOperationRecordBean.setLog_type(0);
				if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean)) {
					j.setMsg("报损报溢单添加操作日志失败！");
					return j;
				}
			}
			j.setSuccess(true);
			return j;
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		}
	}
	
	/**
	 * 删除售后报损报溢单
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/deleteBsby")
	@ResponseBody
	public Json deleteBsby(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(1471)) {
			j.setMsg("您没有删除权限！");
			return j;
		}
		
		int id = StringUtil.toInt(request.getParameter("id"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			synchronized (lock) {
				if (id == -1) {
					j.setMsg("报损报溢单id错误！");
					return j;
				}
				
				BsbyOperationnoteBean bsbyBean = iBsByservice.getBsbyOperationnoteBean("id=" + id);
				if (bsbyBean == null) {
					j.setMsg("报损报溢单不存在！");
					return j;
				}
				
				if (bsbyBean.getCurrent_type() != BsbyOperationnoteBean.audit_Fail && bsbyBean.getCurrent_type() != BsbyOperationnoteBean.fin_audit_Fail) {
					j.setMsg("单据已提交审核，无法删除!");
					return j;
				}
				
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory("status <>" + AfterSaleInventory.COMPLETE_CHECK
						+ " and stock_area=" + bsbyBean.getWarehouse_area() + " and stock_type=" + bsbyBean.getWarehouse_type());
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				iBsByservice.getDbOp().startTransaction();
				
				if (!iBsByservice.updateBsbyOperationnoteBean("if_del=1", "id=" + id)) {
					j.setMsg("单据删除失败!");
					iBsByservice.getDbOp().rollbackTransaction();
					return j;
				}
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("删除" + bsbyBean.getReceipts_number());
				bsbyOperationRecordBean.setOperation_id(bsbyBean.getId());
				if (!iBsByservice.addBsbyOperationRecord(bsbyOperationRecordBean)) {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("报损报溢单操作日志记录失败！");
					return j;
				}
				
				if (!afStockService.updateAfterSaleBsbyProduct("status=" + AfterSaleBsbyProduct.STATUS1, "bsby_operationnote_id=" + id)) {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("单据商品删除失败！");
					return j;
				}
				//售后库报溢单不用更新锁定状态--新建时就已锁定
				if(!(bsbyBean.getType()==BsbyOperationnoteBean.TYPE1 && bsbyBean.getWarehouse_type()==ProductStockBean.STOCKTYPE_AFTER_SALE)){
					if (!afStockService.getDbOp().executeUpdate("update after_sale_detect_product asdp,after_sale_bsby_product asbp set asdp.lock_status=" + (bsbyBean.getType() == BsbyOperationnoteBean.TYPE0 ? AfterSaleDetectProductBean.LOCK_STATUS0:AfterSaleDetectProductBean.LOCK_STATUS2)  + " where asdp.code=asbp.after_sale_detect_product_code and asbp.bsby_operationnote_id=" + id)) {
						iBsByservice.getDbOp().rollbackTransaction();
						j.setMsg("更新售后处理单状态失败！");
						return j;
					}
				}

				iBsByservice.getDbOp().commitTransaction();
				j.setSuccess(true);
				j.setMsg("删除成功！");
				return j;
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			iBsByservice.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 盘点记录下 有实物没有库存记录数量 报溢
	 * @param request
	 * @param response
	 * @param reason
	 * @param strId
	 * @param bsbyType
	 * @param stockType
	 * @return
	 * gw
	 */
	@RequestMapping("/addMultiByProduct")
	@ResponseBody
	public Json addMultiByProduct(HttpServletRequest request, HttpServletResponse response,String reason){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败");
			return j;
		}

		String productIds = StringUtil.convertNull(request.getParameter("products"));
		if (productIds.equals("")) {
			j.setMsg("传值错误！");
			return j;
		}
		int inventoryRecordId = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("inventoryRecordId")));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		int afterSaleInventoryRecordId = 0;
		synchronized (lock) {
			try{
				service.getDbOp().startTransaction();
				//盘点未完成不可报溢
				AfterSaleInventoryRecord record = afStockService.getAfterSaleInventoryRecord("id=" + inventoryRecordId);
				if(record==null){
					j.setMsg("盘点记录不存在!");
					return j;
				}
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory("status <>" + AfterSaleInventory.COMPLETE_CHECK 
						+ " and stock_area=" + record.getStockArea() + " and stock_type=" + record.getStockType());
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					service.getDbOp().rollbackTransaction();
					return j;
				}
				
				BsbyOperationnoteBean bsbyOperationnoteBean = null;
				int bsbyOperationnoteId = 0;
				//新增报溢单
				String receipts_number = "";
				String title = "";// 日志的内容
				
				int by = BsbyOperationnoteBean.TYPE1;
				String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
				receipts_number = ByBsAction.createCode(code);
				title = "创建新的报溢表" + receipts_number;
				
				String nowTime = DateUtil.getNow();
				bsbyOperationnoteBean = new BsbyOperationnoteBean();
				bsbyOperationnoteBean.setAdd_time(nowTime);
				bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.audit_ing);
				bsbyOperationnoteBean.setOperator_id(user.getId());
				bsbyOperationnoteBean.setOperator_name(user.getUsername());
				bsbyOperationnoteBean.setReceipts_number(receipts_number);
				bsbyOperationnoteBean.setWarehouse_area(record.getStockArea());
				bsbyOperationnoteBean.setWarehouse_type(record.getStockType());
				bsbyOperationnoteBean.setType(by);
				bsbyOperationnoteBean.setIf_del(0);
				bsbyOperationnoteBean.setFinAuditId(0);
				bsbyOperationnoteBean.setFinAuditName("");
				bsbyOperationnoteBean.setFinAuditRemark("");
				bsbyOperationnoteBean.setRemark("盘点报溢");
				
				int maxid = afStockService.getNumber("id", "bsby_operationnote", "max", "id > 0");
				bsbyOperationnoteBean.setId(maxid + 1);
				boolean falg = service.addBsbyOperationnoteBean(bsbyOperationnoteBean);
				if (falg) {
					bsbyOperationnoteId = bsbyOperationnoteBean.getId();
					// 添加操作日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(nowTime);
					bsbyOperationRecordBean.setInformation(title);
					bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteId);
					boolean b = service.addBsbyOperationRecord(bsbyOperationRecordBean);
					if(!b){
						service.getDbOp().rollbackTransaction();
						j.setMsg("添加报损报溢单操作日志失败！");
						return j;
					}
				} else {
					service.getDbOp().rollbackTransaction();
					j.setMsg("添加报损报溢单操作失败!");
					return j;
				}
				String[] productsString = productIds.split(",");
				for (String strId : productsString) {
					
					int id = StringUtil.toInt(strId);
					AfterSaleInventoryProduct asip = afStockService.getAfterSaleInventoryProduct("id=" + id);
					if (asip == null) {
						j.setMsg("盘点商品不存在!");
						service.getDbOp().rollbackTransaction();
						return j;
					}
					
					AfterSaleInventoryRecord asir = afStockService.getAfterSaleInventoryRecord("id=" + asip.getAfterSaleInventoryRecordId());
					if (asir == null) {
						j.setMsg("盘点记录不存在!");
						service.getDbOp().rollbackTransaction();
						return j;
					}
					
					if (afterSaleInventoryRecordId != 0) {
						if (afterSaleInventoryRecordId != asir.getId()) {
							j.setMsg("不是同一盘点记录数据!");
							service.getDbOp().rollbackTransaction();
							return j;
						}
					} else {
						afterSaleInventoryRecordId = asir.getId();
					}
					AfterSaleInventory asi = afStockService.getAfterSaleInventory("id=" + asir.getAfterSaleInventoryId());
					if (asi == null) {
						j.setMsg("盘点单不存在!");
						service.getDbOp().rollbackTransaction();
						return j;
					}
					if (asi.status != AfterSaleInventory.COMPLETE_CHECK) {
						j.setMsg("状态不是盘点完成，不能进行报溢操作!");
						service.getDbOp().rollbackTransaction();
						return j;
					}
					
					//判断是不是最后一次盘点
					if (asir.getInventoryCount() != asi.getInventoryCount()) {
						j.setMsg("不是最后一次盘点，不能再生成报溢单!");
						service.getDbOp().rollbackTransaction();
						return j;
					}
					
					List<AfterSaleInventory> asiCheck = afStockService.getAfterSaleInventoryList("stock_area=" + asi.getStockArea() + " and stock_type=" + asi.getStockType(), -1, -1, "complete_datetime desc");
					if (asiCheck != null && asiCheck.size() > 0) {
						AfterSaleInventory asiCheckBean = asiCheck.get(0);
						if (asiCheckBean.getId() != asi.getId()) {
							j.setMsg("不是最后一次盘点，不能再生成报溢单!");
							service.getDbOp().rollbackTransaction();
							return j;
						}
					}
					
					int lockStatus = AfterSaleDetectProductBean.LOCK_STATUS2;
					//判断处理单是不是报损完成
					String condition = " code = '" + asip.getAfterSaleDetectProductCode() + "' and lock_status = " + lockStatus;
					AfterSaleDetectProductBean afterSaleDetectProduct = afStockService.getAfterSaleDetectProduct(condition);
					
					if(afterSaleDetectProduct == null){
						j.setMsg(BsbyOperationnoteBean.typeMap.get(by) + "单添加的售后处理单必须是" + AfterSaleDetectProductBean.lockStatusMap.get(lockStatus));
						service.getDbOp().rollbackTransaction();
						return j;
					}
					
					AfterSaleBsbyProduct asbp = afStockService.getAfterSaleBsbyProduct("after_sale_detect_product_code='" + asip.getAfterSaleDetectProductCode() + "' and status=" + AfterSaleBsbyProduct.STATUS0);
					if (asbp != null) {
						j.setMsg("售后处理单" + asip.getAfterSaleDetectProductCode() + "存在尚未处理完成的报损报溢单！");
						service.getDbOp().rollbackTransaction();
						return j;
					}
					
					//报溢商品
					AfterSaleBsbyProduct afterSaleBsbyProduct = new AfterSaleBsbyProduct();
					afterSaleBsbyProduct.setAfterSaleDetectProductCode(afterSaleDetectProduct.getCode());
					afterSaleBsbyProduct.setAfterSaleOrderCode(afterSaleDetectProduct.getAfterSaleOrderCode());
					afterSaleBsbyProduct.setImei(afterSaleDetectProduct.getIMEI());
					afterSaleBsbyProduct.setProductId(afterSaleDetectProduct.getProductId());
					afterSaleBsbyProduct.setWholeCode(afterSaleDetectProduct.getCargoWholeCode());
					afterSaleBsbyProduct.setBsbyOperationnoteId(bsbyOperationnoteId);
					afterSaleBsbyProduct.setStatus(AfterSaleBsbyProduct.STATUS0);
					afterSaleBsbyProduct.setAfterSaleDetectProductStatus(afterSaleDetectProduct.getStatus());
					
					boolean b = afStockService.addAfterSaleBsbyProduct(afterSaleBsbyProduct);
					if(!b){
						j.setMsg("添加报损报溢单商品操作失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					if (!afStockService.updateAfterSaleDetectProduct("lock_status="+AfterSaleDetectProductBean.LOCK_STATUS1, "id=" + afterSaleDetectProduct.getId())) {
						j.setMsg("更新售后处理单状态失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					voProduct product = wareService.getProduct(afterSaleBsbyProduct.getProductId());

					if (product == null) {
						j.setMsg("该商品不存在！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					BsbyProductBean bsbyProductBean = service.getBsbyProductBean("operation_id=" + bsbyOperationnoteBean.getId() + " and product_id=" + product.getId());
					int productCount = 1;
					if (bsbyProductBean != null) {
						productCount = bsbyProductBean.getBsby_count() + 1;
					}
					int x = getProductCount(product.getId(), bsbyOperationnoteBean.getWarehouse_area(), bsbyOperationnoteBean.getWarehouse_type());
					int result = updateProductCount(x, bsbyOperationnoteBean.getType(), productCount);
					if (result < 0 ) {
						j.setMsg("您所添加商品的库存不足！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//新货位管理判断
					CargoProductStockBean cps = null;
					CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
					CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+bsbyOperationnoteBean.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+afterSaleBsbyProduct.getWholeCode()+"' and status <> "+CargoInfoBean.STATUS3);
			        if(cargo == null){
			        	j.setMsg("货位号"+afterSaleBsbyProduct.getWholeCode()+"无效，请重新输入！");
			            service.getDbOp().rollbackTransaction();
			            return j;
			        }
			        if(cargo.getStatus() == CargoInfoBean.STATUS2){
			        	j.setMsg("货位"+afterSaleBsbyProduct.getWholeCode()+"未开通，请重新输入！");
			            service.getDbOp().rollbackTransaction();
			            return j;
			        }
			        List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	cps = new CargoProductStockBean();
			        	cps.setCargoId(cargo.getId());
			        	cps.setProductId(product.getId());
			        	cps.setStockCount(0);
			        	cps.setStockLockCount(0);
			        	if(!cargoService.addCargoProductStock(cps)){
							service.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
			        	}
			        	cps.setId(cargoService.getDbOp().getLastInsertId());
			        	
			        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId())) {
				        	service.getDbOp().rollbackTransaction();
				        	j.setMsg("数据库操作失败");
				        	return j;
			        	}
			        }else{
			        	cps = (CargoProductStockBean)cpsOutList.get(0);
			        }
					
					
					if (bsbyProductBean == null) {
						bsbyProductBean = new BsbyProductBean();
						bsbyProductBean.setBsby_count(1);
						bsbyProductBean.setOperation_id(bsbyOperationnoteBean.getId());
						bsbyProductBean.setProduct_code(product.getCode());
						bsbyProductBean.setProduct_id(product.getId());
						bsbyProductBean.setProduct_name(product.getName());
						bsbyProductBean.setOriname(product.getOriname());
						bsbyProductBean.setAfter_change(result);
						bsbyProductBean.setBefore_change(x);
						if (!service.addBsbyProduct(bsbyProductBean)) {
							j.setMsg("添加失败！");
							service.getDbOp().rollbackTransaction();
							return j;
						}
						BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
						bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
						bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
						bsbyCargo.setCount(1);
						bsbyCargo.setCargoProductStockId(cps.getId());
						bsbyCargo.setCargoId(cps.getCargoId());
						if(!service.addBsbyProductCargo(bsbyCargo)) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
					} else {
						if (!service.updateBsbyProductBean("bsby_count=" + productCount + ", before_change=" + x
								+ " , after_change=" + result, "id=" + bsbyProductBean.getId())) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
						
						BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id=" + bsbyProductBean.getId() + " and cargo_product_stock_id=" + cps.getId());
						if (bsbyCargo == null) {
							bsbyCargo = new BsbyProductCargoBean();
							bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
							bsbyCargo.setBsbyProductId(bsbyProductBean.getId());
							bsbyCargo.setCount(1);
							bsbyCargo.setCargoProductStockId(cps.getId());
							bsbyCargo.setCargoId(cps.getCargoId());
							if(!service.addBsbyProductCargo(bsbyCargo)) {
								service.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
							}
						} else {
							if(!service.updateBsbyProductCargo("count=count+1" , "id=" + bsbyCargo.getId())) {
								service.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
							}
						}
					}
					
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				System.out.print(DateUtil.getNow());e.printStackTrace();
				j.setMsg("异常");
				return j;
			} finally {
				service.releaseAll();
			}
		}
		j.setSuccess(true);
		j.setMsg("操作成功");
		return j;
	}
		
	/**
	 * 添加报损单
	 * @author zy
	 * @return
	 */
	@RequestMapping("/addMultiBsProduct")
	@ResponseBody
	public Json addMultiBsProduct(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		String productIds = StringUtil.convertNull(request.getParameter("products"));
		if (productIds.equals("")) {
			j.setMsg("传值错误！");
			return j;
		}
		String[] productsString = productIds.split(",");
		Map<String, Integer> productsMap = new HashMap<String, Integer>();
		for (int i = 0; i < productsString.length; i++) {
			int tempint = StringUtil.toInt(productsString[i]);
			if (tempint == -1) {
				j.setMsg("传值错误！");
				return j;
			}
			productsMap.put("id", tempint);
		}
		int inventoryRecordId = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("inventoryRecordId")));
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		int afterSaleInventoryRecordId = 0;
		try {
			synchronized (lock) {
				AfterSaleInventoryRecord record = afStockService.getAfterSaleInventoryRecord("id=" + inventoryRecordId);
				if(record==null){
					j.setMsg("盘点记录不存在!");
					return j;
				}
				AfterSaleInventory afterSaleInventory = afStockService.getAfterSaleInventory("status <>" + AfterSaleInventory.COMPLETE_CHECK 
						+ " and stock_area=" + record.getStockArea() + " and stock_type=" + record.getStockType());
				
				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				
				afStockService.getDbOp().startTransaction();
				
				int bsbyType = BsbyOperationnoteBean.TYPE0;
				String receipts_number = "";
				String title = "";// 日志的内容
				// 报损
				String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
				receipts_number = ByBsAction.createCode(code);
				title = "创建新的报损表" + receipts_number;
				
				String nowTime = DateUtil.getNow();
				BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
				bsbyOperationnoteBean.setAdd_time(nowTime);
				bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.audit_ing);
				bsbyOperationnoteBean.setOperator_id(user.getId());
				bsbyOperationnoteBean.setOperator_name(user.getUsername());
				bsbyOperationnoteBean.setReceipts_number(receipts_number);
				bsbyOperationnoteBean.setWarehouse_area(record.getStockArea());
				bsbyOperationnoteBean.setWarehouse_type(record.getStockType());
				bsbyOperationnoteBean.setType(bsbyType);
				bsbyOperationnoteBean.setIf_del(0);
				bsbyOperationnoteBean.setFinAuditId(0);
				bsbyOperationnoteBean.setFinAuditName("");
				bsbyOperationnoteBean.setFinAuditRemark("");
				bsbyOperationnoteBean.setRemark("盘点报损");
				
				int maxid = iBsByservice.getNumber("id", "bsby_operationnote", "max", "id > 0");
				bsbyOperationnoteBean.setId(maxid + 1);
				int bsbyOperationnoteId = bsbyOperationnoteBean.getId();
				boolean falg = iBsByservice.addBsbyOperationnoteBean(bsbyOperationnoteBean);
				if (falg) {
					request.setAttribute("opid", Integer.valueOf(bsbyOperationnoteBean.getId()));// 添加成功将id传到下个页面
					bsbyOperationnoteId = bsbyOperationnoteBean.getId();
					// 添加操作日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(nowTime);
					bsbyOperationRecordBean.setInformation(title);
					bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteId);
					boolean b = iBsByservice.addBsbyOperationRecord(bsbyOperationRecordBean);
					if(!b){
						iBsByservice.getDbOp().rollbackTransaction();
						j.setMsg("添加报损报溢单操作日志失败！");
						return j;
					}
				} else {
					iBsByservice.getDbOp().rollbackTransaction();
					j.setMsg("添加报损报溢单操作失败!");
					return j;
				}
				for (String key : productsMap.keySet()) {
					int id = productsMap.get(key);
					AfterSaleInventoryProduct asip = afStockService.getAfterSaleInventoryProduct("id=" + id);
					if (asip == null) {
						j.setMsg("盘点商品不存在!");
						iBsByservice.getDbOp().rollbackTransaction();
						return j;
					}
					
					AfterSaleInventoryRecord asir = afStockService.getAfterSaleInventoryRecord("id=" + asip.getAfterSaleInventoryRecordId());
					if (asir == null) {
						j.setMsg("盘点记录不存在!");
						iBsByservice.getDbOp().rollbackTransaction();
						return j;
					}
					
					if (afterSaleInventoryRecordId != 0) {
						if (afterSaleInventoryRecordId != asir.getId()) {
							j.setMsg("不是同一盘点记录数据!");
							iBsByservice.getDbOp().rollbackTransaction();
							return j;
						}
					} else {
						afterSaleInventoryRecordId = asir.getId();
					}
					AfterSaleInventory asi = afStockService.getAfterSaleInventory("id=" + asir.getAfterSaleInventoryId());
					if (asi == null) {
						j.setMsg("盘点单不存在!");
						iBsByservice.getDbOp().rollbackTransaction();
						return j;
					}
					if (asi.status != AfterSaleInventory.COMPLETE_CHECK) {
						j.setMsg("状态不是盘点完成，不能进行报损操作!");
						iBsByservice.getDbOp().rollbackTransaction();
						return j;
					}
					
					//判断是不是最后一次盘点
					if (asir.getInventoryCount() != asi.getInventoryCount()) {
						j.setMsg("不是最后一次盘点，不能再生成报损单!");
						iBsByservice.getDbOp().rollbackTransaction();
						return j;
					}
					
					List<AfterSaleInventory> asiCheck = afStockService.getAfterSaleInventoryList("stock_area=" + asi.getStockArea() + " and stock_type=" + asi.getStockType(), -1, -1, "complete_datetime desc");
					if (asiCheck != null && asiCheck.size() > 0) {
						AfterSaleInventory asiCheckBean = asiCheck.get(0);
						if (asiCheckBean.getId() != asi.getId()) {
							j.setMsg("不是最后一次盘点，不能再生成报损单!");
							iBsByservice.getDbOp().rollbackTransaction();
							return j;
						}
					}
					
					int lockStatus = AfterSaleDetectProductBean.LOCK_STATUS0;
					//判断处理单是不是未锁定
					String condition = " code = '" + asip.getAfterSaleDetectProductCode() + "' and lock_status = " + lockStatus;
					AfterSaleDetectProductBean afterSaleDetectProduct = afStockService.getAfterSaleDetectProduct(condition);
					
					if(afterSaleDetectProduct == null){
						j.setMsg(BsbyOperationnoteBean.typeMap.get(bsbyType) + "单添加的售后处理单必须是" + AfterSaleDetectProductBean.lockStatusMap.get(lockStatus));
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					AfterSaleBsbyProduct asbp = afStockService.getAfterSaleBsbyProduct("after_sale_detect_product_code='" + asip.getAfterSaleDetectProductCode() + "' and status=" + AfterSaleBsbyProduct.STATUS0);
					if (asbp != null) {
						j.setMsg("售后处理单" + asip.getAfterSaleDetectProductCode() + "存在尚未处理完成的报损报溢单！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//报损报溢商品
					asbp = new AfterSaleBsbyProduct();
					asbp.setBsbyOperationnoteId(bsbyOperationnoteId);
					asbp.setStatus(AfterSaleBsbyProduct.STATUS0);
					asbp.setAfterSaleDetectProductCode(afterSaleDetectProduct.getCode());
					asbp.setAfterSaleOrderCode(afterSaleDetectProduct.getAfterSaleOrderCode());
					asbp.setAfterSaleDetectProductStatus(afterSaleDetectProduct.getStatus());
					asbp.setProductId(afterSaleDetectProduct.getProductId());
					asbp.setImei(afterSaleDetectProduct.getIMEI());
					asbp.setWholeCode(afterSaleDetectProduct.getCargoWholeCode());
					
					boolean b = afStockService.addAfterSaleBsbyProduct(asbp);
					if(!b){
						j.setMsg("添加报损报溢单商品操作失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					if (!afStockService.updateAfterSaleDetectProduct("lock_status="+AfterSaleDetectProductBean.LOCK_STATUS1, "id=" + afterSaleDetectProduct.getId())) {
						j.setMsg("更新售后处理单状态失败！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					voProduct product = wareService.getProduct(asbp.getProductId());

					if (product == null) {
						j.setMsg("该商品不存在！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					BsbyProductBean bsbyProductBean = iBsByservice.getBsbyProductBean("operation_id=" + bsbyOperationnoteBean.getId() + " and product_id=" + asbp.getProductId());
					int productCount = 1;
					if (bsbyProductBean != null) {
						productCount = bsbyProductBean.getBsby_count() + 1;
					}
					int x = getProductCount(product.getId(), bsbyOperationnoteBean.getWarehouse_area(), bsbyOperationnoteBean.getWarehouse_type());
					int result = updateProductCount(x, bsbyOperationnoteBean.getType(), productCount);
					if (result < 0 ) {
						j.setMsg("您所添加商品的库存不足！");
						afStockService.getDbOp().rollbackTransaction();
						return j;
					}
					
					//新货位管理判断
					CargoProductStockBean cps = null;
			        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
			        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bsbyOperationnoteBean.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+asbp.getWholeCode()+"'", -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	j.setMsg("货位号"+asbp.getWholeCode()+"无效，请重新输入！");
			        	iBsByservice.getDbOp().rollbackTransaction();
			            return j;
			        }
			        cps = (CargoProductStockBean)cpsOutList.get(0);
			        if(1 > cps.getStockCount()){
			        	j.setMsg("该货位"+asbp.getWholeCode()+"库存为" + cps.getStockCount() + "，库存不足！");
			        	iBsByservice.getDbOp().rollbackTransaction();
			            return j;
			        }
					
					
					if (bsbyProductBean == null) {
						bsbyProductBean = new BsbyProductBean();
						bsbyProductBean.setBsby_count(1);
						bsbyProductBean.setOperation_id(bsbyOperationnoteBean.getId());
						bsbyProductBean.setProduct_code(product.getCode());
						bsbyProductBean.setProduct_id(product.getId());
						bsbyProductBean.setProduct_name(product.getName());
						bsbyProductBean.setOriname(product.getOriname());
						bsbyProductBean.setAfter_change(result);
						bsbyProductBean.setBefore_change(x);
						if (! iBsByservice.addBsbyProduct(bsbyProductBean)) {
							j.setMsg("添加失败！");
							iBsByservice.getDbOp().rollbackTransaction();
							return j;
						}
						BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
						bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
						bsbyCargo.setBsbyProductId(iBsByservice.getDbOp().getLastInsertId());
						bsbyCargo.setCount(1);
						bsbyCargo.setCargoProductStockId(cps.getId());
						bsbyCargo.setCargoId(cps.getCargoId());
						if(!iBsByservice.addBsbyProductCargo(bsbyCargo)) {
							iBsByservice.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
					} else {
						if (!iBsByservice.updateBsbyProductBean("bsby_count=" + productCount + ", before_change=" + x
								+ " , after_change=" + result, "id=" + bsbyProductBean.getId())) {
							iBsByservice.getDbOp().rollbackTransaction();
							j.setMsg("数据库操作失败");
							return j;
						}
						
						BsbyProductCargoBean bsbyCargo = iBsByservice.getBsbyProductCargo("bsby_product_id=" + bsbyProductBean.getId() + " and cargo_product_stock_id=" + cps.getId());
						if (bsbyCargo == null) {
							bsbyCargo = new BsbyProductCargoBean();
							bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());
							bsbyCargo.setBsbyProductId(bsbyProductBean.getId());
							bsbyCargo.setCount(1);
							bsbyCargo.setCargoProductStockId(cps.getId());
							bsbyCargo.setCargoId(cps.getCargoId());
							if(!iBsByservice.addBsbyProductCargo(bsbyCargo)) {
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
							}
						} else {
							if(!iBsByservice.updateBsbyProductCargo("count=count+1" , "id=" + bsbyCargo.getId())) {
								iBsByservice.getDbOp().rollbackTransaction();
								j.setMsg("数据库操作失败");
								return j;
							}
						}
					}
					String sql = "product_id = " + asbp.getProductId() + " and "
							+ "area = " + bsbyOperationnoteBean.getWarehouse_area() + " and type = "
							+ bsbyOperationnoteBean.getWarehouse_type();
					ProductStockBean psBean = psService.getProductStock(sql);
					if(psBean == null){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("商品库存信息不存在");
						return j;
					}
					if(!psService.updateProductStockCount(psBean.getId(), -1)){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
						return j;
					}
					if (!psService.updateProductLockCount(psBean.getId(), 1)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
						return j;
					}
					
					if(!cargoService.updateCargoProductStockCount(cps.getId(), -1)){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("货位库存操作失败，货位库存不足！");
						return j;
					}
					if(!cargoService.updateCargoProductStockLockCount(cps.getId(), 1)){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("货位库存操作失败，货位冻结库存不足！");
						return j;
					}
				}
				
				iBsByservice.getDbOp().commitTransaction();
				j.setSuccess(true);
				j.setMsg("报损成功！");
				return j;
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			iBsByservice.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 导出盘点商品
	 * @param request
	 * @param response
	 * @return
	 * @author zy
	 */
	@RequestMapping("/exportInventoryProductList")
	@ResponseBody
	public void exportInventoryProductList(HttpServletRequest request, HttpServletResponse response){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String detectCode = StringUtil.convertNull(request.getParameter("afterSaleDetectCode"));
		int inventoryRecordId = StringUtil.StringToId(request.getParameter("inventoryRecordId"));
		String type = StringUtil.convertNull(request.getParameter("type"));
		try{
			//查询盘点结束的记录
			StringBuilder condition = new StringBuilder();
			if(!(StringUtil.isNull(type))){
				condition.append(" and afip.type in (").append(type).append(") ");
			}
			if(inventoryRecordId>0){
				condition.append(" and afip.after_sale_inventory_record_id=").append(inventoryRecordId).append(" ");
			}
			if(!(StringUtil.isNull(productCode))){
				condition.append(" and p.code='").append(productCode).append("' ");
			}
			if(!(StringUtil.isNull(detectCode))){
				condition.append(" and afip.after_sale_detect_product_code='").append(detectCode).append("' ");
			}
			List<Map<String,String>> recordList = afStockService.getAfterSaleInventoryProductList(condition.toString(), -1, -1);
			
			String now = DateUtil.getNow().substring(0, 10);
			String fileName = now;
			//设置表头
		    ExportExcel excel = new ExportExcel();
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			header.add("序号");
			header.add("售后处理单号");
			header.add("售后处理单状态");
			header.add("售后单号");
			header.add("商品编号");
			header.add("商品名称");
			header.add("货位号");
				
			int size = header.size();
				
			if (recordList != null && recordList.size() > 0) {
				int x = recordList.size();
				for (int i = 0; i < x; i++) {
					Map<String, String> map =  (Map<String,String>) recordList.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(i + 1 + "");
					tmp.add(map.get("afterSaleDetectCode"));
					tmp.add(map.get("afterSaleDetectProductStatus"));
					tmp.add(map.get("afterSaleOrderCode"));
					tmp.add(map.get("productCode"));
					tmp.add(map.get("productName"));
					tmp.add(map.get("recordWholeCode"));
					bodies.add(tmp);
				}
			}
			headers.add(header);

			/*允许合并列,下标从0开始，即0代表第一列*/
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			/*允许合并行,下标从0开始，即0代表第一行*/
			List<Integer> mayMergeRow = new ArrayList<Integer>();
	        excel.setMayMergeRow(mayMergeRow);
	        
			/*
			 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
			 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
			 * 
			 * */
			excel.setColMergeCount(size);
	        
			
			/*
			 * 设置需要自己设置样式的行，以每个bodies为参照
			 * 具体的样式设置参考 DemoExcel.java中的setStyle方法
			 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
			 */
	        List<Integer> row  = new ArrayList<Integer>();
	        
	        /*设置需要自己设置样式的列，以每个bodies为参照*/
	        List<Integer> col  = new ArrayList<Integer>();
	        
	        excel.setRow(row);
	        excel.setCol(col);
	        
	        //调用填充表头方法
	        excel.buildListHeader(headers);
	        
	        //调用填充数据区方法
	        excel.buildListBody(bodies);
	        //文件输出
	        excel.exportToExcel(fileName, response, "");
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
	}
	/**
	 * 更改商品的实际货位
	 * 选中需要更改货位商品，生成一个已完成的仓内调拨单，源货位为系统记录的货位号，目的货位为实际货位；把商品货位更改为实际货位
	 * @return
	 * @author 李宁
	* @date 2014-4-21
	 */
	@RequestMapping("/modifyRealCargoCode")
	@ResponseBody
	public Json modifyRealCargoCode(HttpServletRequest request, HttpServletResponse response){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		WareService wareService = new WareService(dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int inventoryRecordId = StringUtil.StringToId(request.getParameter("inventoryRecordId"));
		String inventoryProductIds = StringUtil.convertNull(request.getParameter("inventoryProductIds"));
		try{
			synchronized (lock) {
				if("".equals(inventoryProductIds)){
					j.setMsg("请选择要更改货位的盘点商品!");
					return j;
				}
				AfterSaleInventoryRecord record = afStockService.getAfterSaleInventoryRecord("id=" + inventoryRecordId);
				if(record==null){
					j.setMsg("盘点记录不存在,不能更改货位!");
					return j;
				}
				//前提条件：1.当前库类型最后一次盘点结束的盘点记录可以更改货位
				//之后改为:售后处理单商品需要lock=0（未锁定状态）时，才可以调拨，更改货位 2014-04-28
				//2.商品的实际货位是否与记录货位一致，如果不一致则可以更改货位
				int count = afStockService.getAfterSaleDetectCount(" and asdp.lock!=0 and ci.stock_type=" + record.getStockType());
				if(count>0){
					j.setMsg("售后处理单中有已锁定状态的商品不能更改货位!");
					return j;
				}
				afStockService.getDbOp().startTransaction();
				List<AfterSaleInventoryProduct> inventoryproductList = afStockService.getAfterSaleInventoryProductList("id in (" + inventoryProductIds + ")" , -1, -1, null);
				if(inventoryproductList!=null && inventoryproductList.size()>0){
					//是否要更改货位
					boolean flag = false;
					for(int i=0;i<inventoryproductList.size();i++){
						AfterSaleInventoryProduct inventoryProduct = inventoryproductList.get(i);
						if(!inventoryProduct.getRealWholeCode().equalsIgnoreCase(inventoryProduct.getRecordWholeCode())){
							flag = true;
							break;
						}
					}
					if(flag){
						for(int i=0;i<inventoryproductList.size();i++){
							AfterSaleInventoryProduct inventoryProduct = inventoryproductList.get(i);
							if(!inventoryProduct.getRealWholeCode().equalsIgnoreCase(inventoryProduct.getRecordWholeCode())){
								//生成一个已完成的仓内调拨单，源货位为系统记录的货位号，目的货位为实际货位,修改商品记录货位为实际货位
								
								CargoOperationBean operationBean = new CargoOperationBean();
								CargoInfoBean inCargoInfo = cargoService.getCargoInfo(" whole_code = '" + inventoryProduct.getRealWholeCode() + "' ");
								if (inCargoInfo == null){
									j.setMsg("目的货位不可用");
									return j;
								}
								CargoInfoBean outCargoInfo = cargoService.getCargoInfo(" whole_code = '" + inventoryProduct.getRecordWholeCode() + "' ");
								if (outCargoInfo == null){
									j.setMsg("源货位不可用");
									return j;
								}
								operationBean.setStatus(CargoOperationProcessBean.OPERATION_STATUS35);
								operationBean.setStockInType(inCargoInfo.getStoreType());
								operationBean.setStockOutType(outCargoInfo.getStoreType());
								
								String cargoOperCode = "HWD" + DateUtil.getNow().substring(2, 10).replace("-", "");
								CargoOperationBean oldCargoOper = cargoService.getCargoOperation("code like '" + cargoOperCode + "%' order by id desc limit 1");
								if (oldCargoOper == null) {
									cargoOperCode = cargoOperCode + "00001";
								} else {// 获取当日计划编号最大值
									String _code = oldCargoOper.getCode();
									int number = Integer.parseInt(_code.substring(_code.length() - 5));
									number++;
									cargoOperCode += String.format("%05d", new Object[] { new Integer(number) });
								}
								operationBean.setCode(cargoOperCode);
								operationBean.setStorageCode("GZF");
								operationBean.setType(CargoOperationBean.TYPE3);
								operationBean.setCreateDatetime(DateUtil.getNow());
								operationBean.setRemark("售后盘点记录中更改实际货位操作");
								operationBean.setConfirmDatetime(DateUtil.getNow());
								operationBean.setCreateUserId(user.getId());
								operationBean.setCreateUserName(user.getUsername());
								operationBean.setAuditingDatetime(DateUtil.getNow());
								operationBean.setAuditingUserId(user.getId());
								operationBean.setAuditingUserName(user.getUsername());
								operationBean.setLastOperateDatetime(DateUtil.getNow());
								if (!cargoService.addCargoOperation(operationBean)) {
									afStockService.getDbOp().rollbackTransaction();
									j.setMsg("生成仓内调拨单失败!");
									return j;
								}
								operationBean.setId(cargoService.getDbOp().getLastInsertId());
								
								String result = modifyStockCount(j, afStockService, cargoService, wareService, psService, record,
										inventoryProduct, operationBean, inCargoInfo, outCargoInfo);
								if(result!=null && !"".equals(result)){
									j.setMsg(result);
									return j;
								}
							}
						}
					}else{
						j.setMsg("货位不用更改!");
						return j;
					}
					afStockService.getDbOp().commitTransaction();
					j.setMsg("更改实际货位成功!");
					return j;
				}else{
					j.setMsg("没有查到相应的盘点商品列表!");
					return j;
				}
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			afStockService.getDbOp().rollbackTransaction();
		}finally{
			afStockService.releaseAll();
		}
		return j;
	}
	
	/**
	 * 增加修改的库存相关的记录
	 * @param j
	 * @param afStockService
	 * @param cargoService
	 * @param wareService
	 * @param psService
	 * @param inventoryRecordList
	 * @param inventoryProduct
	 * @param operationBean
	 * @param inCargoInfo
	 * @param outCargoInfo
	 * @return
	 * @author 李宁
	* @date 2014-4-21
	 */
	private String modifyStockCount(Json j, AfStockService afStockService, ICargoService cargoService, WareService wareService,
			IProductStockService psService, AfterSaleInventoryRecord inventoryRecord, AfterSaleInventoryProduct inventoryProduct,
			CargoOperationBean operationBean, CargoInfoBean inCargoInfo, CargoInfoBean outCargoInfo) {
		String result = "";
		// 修改货位库存
		CargoProductStockBean outCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + outCargoInfo.getId() + " AND product_id=" + inventoryProduct.getProductId());
		if (outCargoProductStock == null || outCargoProductStock.getStockCount() <= 0) {
			afStockService.getDbOp().rollbackTransaction();
			j.setMsg(inventoryProduct.getRecordWholeCode() + "源货位库存不足");
		}
		if (!cargoService.updateCargoProductStockCount(outCargoProductStock.getId(), -1)) {
			afStockService.getDbOp().rollbackTransaction();
			result="修改货位库存失败!";
			return result;
		}
		
		CargoProductStockBean inCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + inCargoInfo.getId() + " AND product_id=" + inventoryProduct.getProductId());
		if (inCargoProductStock == null) {
			inCargoProductStock = new CargoProductStockBean();
			inCargoProductStock.setCargoId(inCargoInfo.getId());
			inCargoProductStock.setProductId(inventoryProduct.getProductId());
			inCargoProductStock.setStockCount(1);
			inCargoProductStock.setStockLockCount(0);
			if (!cargoService.addCargoProductStock(inCargoProductStock)){
				afStockService.getDbOp().rollbackTransaction();
				result="数据库操作失败：addCargoProductStock";
				return result;
			}
			inCargoProductStock.setId(cargoService.getDbOp().getLastInsertId());
		} else {
			if (!cargoService.updateCargoProductStockCount(inCargoProductStock.getId(), 1)) {
				afStockService.getDbOp().rollbackTransaction();
				result="数据库操作失败：updateCargoProductStockCount";
				return result;
			}
		}
		
		
		CargoOperationCargoBean outCargoBean = new CargoOperationCargoBean();
		//源货位商品
		outCargoBean.setType(1);
		outCargoBean.setUseStatus(0);
		outCargoBean.setOutCargoProductStockId(outCargoProductStock.getId());
		outCargoBean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
		outCargoBean.setOperId(operationBean.getId());
		outCargoBean.setProductId(inventoryProduct.getProductId());
		outCargoBean.setStockCount(1);
		
		
		// 调拨单商品--目的货位商品
		CargoOperationCargoBean inCargoBean = new CargoOperationCargoBean();
		inCargoBean.setOperId(operationBean.getId());
		inCargoBean.setProductId(inventoryProduct.getProductId());
		inCargoBean.setInCargoProductStockId(inCargoProductStock.getId());
		inCargoBean.setInCargoWholeCode(inCargoInfo.getWholeCode());
		inCargoBean.setOutCargoProductStockId(outCargoProductStock.getId());
		inCargoBean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
		inCargoBean.setType(0);
		inCargoBean.setUseStatus(1);
		if (!cargoService.addCargoOperationCargo(outCargoBean)) {
			afStockService.getDbOp().rollbackTransaction();
			result="数据库操作失败：addCargoOperationCargo";
			return result;
		}
		
		if (!cargoService.addCargoOperationCargo(inCargoBean)) {
			afStockService.getDbOp().rollbackTransaction();
			result="数据库操作失败：addCargoOperationCargo";
			return result;
		}
		
		//修改盘点商品的记录货位为实际货位
		if(!afStockService.updateAfterSaleInventoryProduct("record_whole_code='" + inventoryProduct.getRealWholeCode() + "'" , "id="+inventoryProduct.getId())){
			afStockService.getDbOp().rollbackTransaction();
			result="修改盘点商品的记录货位为实际货位失败!";
			return result;
		}
		
		//更新售后处理单的货位号
		if(!afStockService.updateAfterSaleDetectProduct("cargo_whole_code='" + inventoryProduct.getRealWholeCode() + "'", "code='"+inventoryProduct.getAfterSaleDetectProductCode()+"'")){
			afStockService.getDbOp().rollbackTransaction();
			result="修改售后处理单货位号失败!";
			return result;
		}
		
		// 添加 货位进销存卡片
		voProduct vProduct = wareService.getProduct(inventoryProduct.getProductId());
		vProduct.setPsList(psService.getProductStockList("product_id=" + inventoryProduct.getId(), -1, -1, null));

		CargoStockCardBean outcsc = new CargoStockCardBean();
		outcsc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
		outcsc.setCode(operationBean.getCode());
		outcsc.setCreateDatetime(DateUtil.getNow());
		outcsc.setStockType(inventoryRecord.getStockType());
		outcsc.setStockArea(inventoryRecord.getStockArea());
		outcsc.setProductId(inventoryProduct.getId());
		outcsc.setStockId(outCargoProductStock.getId());
		outcsc.setStockOutCount(1);
		outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
		outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
		outcsc.setCargoStoreType(outCargoInfo.getStoreType());
		outcsc.setCargoWholeCode(outCargoInfo.getWholeCode());
		outcsc.setStockPrice(vProduct.getPrice5());
		outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(outcsc)) {
			afStockService.getDbOp().rollbackTransaction();
			result="数据库操作失败：addCargoStockCard";
			return result;
		}
		// 货位入库卡片
		CargoStockCardBean incsc = new CargoStockCardBean();
		incsc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
		incsc.setCode(operationBean.getCode());
		incsc.setCreateDatetime(DateUtil.getNow());
		incsc.setStockType(inventoryRecord.getStockType());
		incsc.setStockArea(inventoryRecord.getStockArea());
		incsc.setProductId(inventoryProduct.getId());
		incsc.setStockId(inCargoProductStock.getId());
		incsc.setStockInCount(1);
		incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
		incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
		incsc.setCargoStoreType(inCargoInfo.getStoreType());
		incsc.setCargoWholeCode(inCargoInfo.getWholeCode());
		incsc.setStockPrice(vProduct.getPrice5());
		incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(incsc)) {
			afStockService.getDbOp().rollbackTransaction();
			result="数据库操作失败：addCargoStockCard";
			return result;
		}
		return result;
	}
	
	/**
	 *  售后库存列表批量调拨为
	 * @param inStockArea 目的库库地区
	 * @param inStockType 目的库库类型
	 * @param codes 售后处理单号列表
	 * @param area 当前库地区，当前应该为1
	 * @param user 当前操作用户
	 * @param flag 标记，PDA：1，其他情况待定 售后库存列表批量调拨为2
	 * @author gw
	 */
	@RequestMapping("/createAfterSaleAllot")
	@ResponseBody
	public Json createAfterSaleAllot(HttpServletRequest request, HttpServletResponse response){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		int inStockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("areaType")));
		if(inStockArea <= -1){
			j.setMsg("请选择目的库地区");
			return j;
		}
		
		int inStockType = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockType")));
		if(inStockType <= -1){
			j.setMsg("请选择库类型");
			return j;
		}
		String codeStrs = StringUtil.convertNull(request.getParameter("codes"));
		String[] split = codeStrs.split(",");
		if(split.length <= 0){
			j.setMsg("请选择需要调拨的售后处理单");
			return j;
		}
		List<String> codes = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			codes.add(split[i]);
		}
		
		CommonLogic c = new CommonLogic();
		Json json = c.createAfterSaleAllot(inStockArea, inStockType, codes, user, 2, -1);
		
		j.setSuccess(json.isSuccess());
		j.setMsg(json.getMsg());
		return j;
	}
	
	/**
	 * 售后调拨新增，提交审核
	 * @author zy
	 */
	@RequestMapping("/createAfterSaleStockExchange")
	@ResponseBody
	public Json createAfterSaleStockExchange(HttpServletRequest request, HttpServletResponse response){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		int inStockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		if(inStockArea <= -1){
			j.setMsg("请选择目的库地区");
			return j;
		}
		
		int inStockType = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockType")));
		if(inStockType <= -1){
			j.setMsg("请选择库类型");
			return j;
		}
		HttpSession session = request.getSession();
		Map<String, Map<String, String>> sessionMap = (LinkedHashMap<String, Map<String, String>>)session.getAttribute("afterSaleStockExchangeMap");
		if(sessionMap == null){
			j.setMsg("请选择库类型");
			return j;
		}
		
		List<String> codes = new ArrayList<String>();
		
		for (String code : sessionMap.keySet()) {
			codes.add(code);
		}
		
		int stockExchangeId = StringUtil.toInt(request.getParameter("stockExchangeId"));
		
		CommonLogic c = new CommonLogic();
		Json json = c.createAfterSaleAllot(inStockArea, inStockType, codes, user, 2, stockExchangeId);
		
		if (json.isSuccess()) {
			session.removeAttribute("afterSaleStockExchangeMap");
		}
		
		j.setSuccess(json.isSuccess());
		j.setMsg(json.getMsg());
		return j;
	}
	
	/**
	 * 根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品2010-02-22
	 * 
	 * @param productCode
	 * @param area
	 * @param type
	 * @return
	 */
	public static int getProductCount(int productid, int area, int type) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service
				.getDbOp());
		int x = 0;
		try {
			voProduct product = wareService.getProduct(productid);
			product.setPsList(psService.getProductStockList("product_id=" + productid, -1, -1, null));
			Iterator iter = product.getPsList().listIterator();
			while(iter.hasNext()){
				ProductStockBean ps = (ProductStockBean) iter.next();
				if(ps.getArea() == area && ps.getType() == type){
					if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
							&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER 
							&& ps.getType() != ProductStockBean.STOCKTYPE_SPARE){
						x += ps.getStock();
					}
				}
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return x;

	}

	/**
	 * 得到报损或者报溢后的产品的数量 2010-02-22
	 * 
	 * @param x
	 * @param Type
	 * @return
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}
	
	/**
	 * 售后——添加调拨商品session
	 * @param request
	 * @param response
	 * @param codes
	 * @return
	 */
	@RequestMapping("/afterSaleStockExchangeInfo")
	@ResponseBody
	public Json afterSaleStockExchangeInfo(HttpServletRequest request, HttpServletResponse response,String codes){
		Json j = new Json();
		j.setSuccess(false);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		String allCode[] = StringUtil.convertNull(codes.trim()).split("\r\n");
		if(allCode.length <= 0){
			j.setMsg("请填写处理单号！");
			return j;
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			for(String code:allCode){
				List<Map<String, String>> list = afStockService.getAfterSaleStockExchangeProductInfo(code, dbOp);
				
				if(list != null && list.size() > 0){
					HttpSession session = request.getSession();
					Map<String, Map<String, String>> sessionMap = (LinkedHashMap<String, Map<String, String>>)session.getAttribute("afterSaleStockExchangeMap");
					if(sessionMap == null){
						sessionMap = new LinkedHashMap<String, Map<String, String>>();
					}
					
					for (Map<String, String> map : list) {
						String afterSaleDetectProductCode = map.get("afterSaleDetectProductCode");
						LinkedHashMap<String,String> linkedMap = new LinkedHashMap<String, String>(map);
						sessionMap.put(afterSaleDetectProductCode, linkedMap);
					}
					
					request.getSession().setAttribute("afterSaleStockExchangeMap", sessionMap);
					
				}else{
					j.setMsg("售后处理单["+code+"]不存在！");
					return j;
				}
			}
			j.setMsg("添加成功！");
			j.setSuccess(true);
			return j;
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 售后——编辑加载调拨商品
	 * @param request
	 * @param response
	 * @param codes
	 * @return
	 */
	@RequestMapping("/setStockExchangeProductSessionInfo")
	@ResponseBody
	public Json getSessionAfterSaleStockExchangeInfo(HttpServletRequest request, HttpServletResponse response,String codes){
		Json j = new Json();
		j.setSuccess(false);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}		
		
		int stockExchangeId = StringUtil.toInt(request.getParameter("stockExchangeId"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			List<Map<String, String>> list = afStockService.getAfterSaleStockExchangeProductInfo(stockExchangeId, dbOp);
			
			if(list != null && list.size() > 0){
				HttpSession session = request.getSession();
				Map<String, Map<String, String>> sessionMap = (LinkedHashMap<String, Map<String, String>>)session.getAttribute("afterSaleStockExchangeMap");
				if(sessionMap == null){
					sessionMap = new LinkedHashMap<String, Map<String, String>>();
				}
				
				for (Map<String, String> map : list) {
					String afterSaleDetectProductCode = map.get("afterSaleDetectProductCode");
					LinkedHashMap<String,String> linkedMap = new LinkedHashMap<String, String>(map);
					sessionMap.put(afterSaleDetectProductCode, linkedMap);
				}
				
				request.getSession().setAttribute("afterSaleStockExchangeMap", sessionMap);
				j.setMsg("添加成功！");
				j.setSuccess(true);
				return j;
			}else{
				j.setMsg("售后处理单信息不存在！");
				return j;
			}
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			afStockService.releaseAll();
		}
		
	}
	
	/**
	 * 获取session 中 需要调拨的处理单的信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getSessionAfterSaleStockExchangeInfo")
	@ResponseBody
	public EasyuiDataGridJson getSessionAfterSaleStockExchangeInfo(HttpServletRequest request, HttpServletResponse response){
		Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		if (!"".equals(flag)) {
			request.getSession().removeAttribute("afterSaleStockExchangeMap");
		}
		Object obj = request.getSession().getAttribute("afterSaleStockExchangeMap");
		if(obj != null){
			map = (Map<String, Map<String, String>>)obj;
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Iterator<Entry<String, Map<String, String>>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Map<String, String>> next = iterator.next();
			Map<String, String> value = next.getValue();
			list.add(value);
		}
		Collections.reverse(list);//让list反序
		j.setRows(list);
		return j;
	}
	
	/**
	 * 清空session 中 需要调拨单商品的信息
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
	@RequestMapping("/removeSessionAfterSaleStockExchangeInfo")
	@ResponseBody
	public Json removeSessionAfterSaleStockExchangeInfo(HttpServletRequest request, HttpServletResponse response,String code){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录，操作失败");
			return j;
		}
		
		if(code == null || code.length() <= 0 ){
			j.setMsg("操作失败");
			return j;
		}
		
		Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
		Object obj = request.getSession().getAttribute("afterSaleStockExchangeMap");
		if(obj != null){
			map = (Map<String, Map<String, String>>)obj;
		}
		if(map.containsKey(code)){
			map.remove(code);
			j.setSuccess(true);
			j.setMsg("操作成功");
			return j;
		}else{
			j.setMsg("操作失败");
			return j;
		}
	}
	
	/**
	 * 审核库存调配单——审核出库、审核入库
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/auditingStockExchange")
	@ResponseBody
	public Json auditingStockExchange(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		j.setMsg("当前没有登录，操作失败！");
            return j;
    	}
  
    	String audintType = StringUtil.convertNull(request.getParameter("audintType"));
    	synchronized(lock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
            int mark = StringUtil.StringToId(request.getParameter("mark"));
            WareService wareService = new WareService();
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            if (bean == null) {
	            	j.setMsg("调拨单不存在！");
	            	return j;
	            }

	            //开始事务
	            service.getDbOp().startTransaction();
	            
	            //同步控制， 入库审核时 一个页面审核失败。 另一个页面审核通过 应该提示。 审核不通过调拨单的状态会变回出库审核
	            if(bean.getStatus() == StockExchangeBean.STATUS2 && audintType.equals("audintingIn")){
	            	j.setMsg( bean.getName() + "  不是入库审核状态，不能审核！");
	            	service.getDbOp().rollbackTransaction();
					return j;
	            }
	            
				if (bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS6) {
					j.setMsg( bean.getName() + "  不是待审核状态，不能审核！");
					service.getDbOp().rollbackTransaction();
					return j;
				}
				if (bean.getStatus() == StockExchangeBean.STATUS2) {
					if (mark == 1) {
						// 审核通过，要出库
						String result = auditOutStockExchangePass(user, bean, wareService.getDbOp());
						if (result != null) {
							j.setMsg(result);
							service.getDbOp().rollbackTransaction();
							return j;
						}
						
						if (!service.updateStockExchangeProduct("status=" + StockExchangeProductBean.STOCKIN_DEALED, "stock_exchange_id=" + bean.getId())) {
							j.setMsg("更新调拨商品状态失败！");
							service.getDbOp().rollbackTransaction();
							return j;
						}
					} else {
						// 审核没通过，要把状态转为处理中审核未通过
						// 同时 把已经出库，并锁定库存的 商品 还原回去
						String result = auditOutStockExchangeNoPass(user, bean, wareService.getDbOp());
						if (result != null) {
							j.setMsg(result);
							service.getDbOp().rollbackTransaction();
							return j;
						}
						request.getSession().removeAttribute("afterSaleStockExchangeMap");
					}
				} else if (bean.getStatus() == StockExchangeBean.STATUS6) {
					 String condition = "stock_exchange_id = " + bean.getId()
								+ " and status = "
								+ StockExchangeProductBean.STOCKIN_DEALED;
					if (mark == 1) {
						// 审核通过，要入库
						String result = auditInStockExchangePass(user, bean, wareService.getDbOp(), request, condition);
						if (result != null) {
							j.setMsg(result);
							service.getDbOp().rollbackTransaction();
							return j;
						}
						if (!wareService.executeUpdate("update after_sale_detect_product asdp,after_sale_stock_exchange_product assep,cargo_info ci set asdp.cargo_whole_code=ci.whole_code , asdp.area_id = ci.area_id, asdp.lock_status= "+ AfterSaleDetectProductBean.LOCK_STATUS0 + " where asdp.id=assep.after_sale_detect_product_id and assep.in_cargo_id=ci.id and assep.stock_exchange_id=" + bean.getId())) {
							j.setMsg("更新处理单锁定状态失败！");
							service.getDbOp().rollbackTransaction();
							return j;
						}
						
					} else {
						// 审核没通过，要把状态转为处理中审核未通过
						//需求变更：审核没通过的要吧状态转化为 出库审核中
						String result = auditInStockExchangeNoPass(user, bean, wareService.getDbOp(), condition);
						if (result != null) {
							service.getDbOp().rollbackTransaction();
							j.setMsg(result);
							return j;
						}
						request.getSession().removeAttribute("afterSaleStockExchangeMap");
					}
				}
				// 提交事务
				service.getDbOp().commitTransaction();
				j.setMsg("操作成功！");
				j.setSuccess(true);
				return j;
	        } catch (Exception e) {
	        	System.out.print(DateUtil.getNow());e.printStackTrace();
	        	service.getDbOp().rollbackTransaction();
	        	stockLog.error(StringUtil.getExceptionInfo(e));
	        	j.setMsg("异常！");
	        	return j;
			} finally {
	            service.releaseAll();
	        }
    	}
	}
	
    /**
     * 调拨出库审核通过
     * @param user
     * @param bean
     * @param dbOp
     * @return
     */
    public String auditOutStockExchangePass(voUser user, StockExchangeBean bean, DbOperation dbOp) {
    	UserGroupBean group = user.getGroup();
    	boolean auditingOut = group.isFlag(80);
    	if (!auditingOut) {
    		return "您没有此操作权限！";
    	}
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        try {
	    	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS3 + ", auditing_user_id=" + user.getId()+",auditing_user_name='"+user.getUsername()+"'", "id=" + bean.getId())){
	            return "更新调拨单信息失败！";
			}
			if(!service.updateStockExchangeProduct("status=" + StockExchangeProductBean.STOCKIN_UNDEAL, "stock_exchange_id=" + bean.getId())){
	            return "更新调拨单商品信息失败！";
			}
			// log记录
			StockAdminHistoryBean log = new StockAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("商品调配操作：" + bean.getName() + "：出库审核通过");
			log.setType(StockAdminHistoryBean.CHANGE);
			if(!stockService.addStockAdminHistory(log)){
	            return "添加调拨日志失败！";
			}
        } catch (Exception e) {
        	System.out.print(DateUtil.getNow());e.printStackTrace();
        	stockLog.error(StringUtil.getExceptionInfo(e));
        	return "异常!";
        }
			
		return null;
    }
    
    /**
     * 调拨出库审核不通过
     * @param user
     * @param bean
     * @param dbOp
     * @return
     */
    public String auditOutStockExchangeNoPass(voUser user, StockExchangeBean bean, DbOperation dbOp) {
    	UserGroupBean group = user.getGroup();
    	boolean auditingOut = group.isFlag(80);
    	if (!auditingOut) {
    		return "您没有此操作权限！";
    	}
    	WareService wareService = new WareService(dbOp);
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        try {
	    	String condition = "stock_exchange_id = " + bean.getId() + " and status = " + StockExchangeProductBean.STOCKOUT_DEALED;
			ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
			Iterator itr = sepList.iterator();
			StockExchangeProductBean sep = null;
			voProduct product = null;
			ProductStockBean psOut = null;
			String set = null;
			while (itr.hasNext()) {
				sep = (StockExchangeProductBean) itr.next();
				product = wareService.getProduct(sep.getProductId());
				psOut = service.getProductStock("id=" + sep.getStockOutId());
				
				if (sep.getStockOutCount() > psOut.getLockCount()) {
					return product.getCode() + "的锁定库存不足，无法从锁定库存还原到正常库存！";
				}
				set = "status = "
						+ StockExchangeProductBean.STOCKOUT_UNDEAL
						+ ", remark = '操作前库存"
						+ psOut.getStock() + ",操作后库存"
						+ (psOut.getStock() + sep.getStockOutCount())
						+ "', confirm_datetime = now()";
				if(!service.updateStockExchangeProduct(set, "id = " + sep.getId())){ 
					return "修改调拨商品信息失败！";
				}
				if(!service.updateProductStockCount(sep.getStockOutId(), sep.getStockOutCount())){
					return "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！";
				}
				if(!service.updateProductLockCount(sep.getStockOutId(), -sep.getStockOutCount())){
					return "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！";
				}
				
				// log记录
				StockAdminHistoryBean log = new StockAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]从出库锁定还原到正常库存");
				log.setType(StockAdminHistoryBean.CHANGE);
				if(!stockService.addStockAdminHistory(log)){
					return "更新库存操作日志失败！";
				}
				
				//还原出库锁定货位库存
				List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
				for(int i=0;i<sepcOutList.size();i++){
					StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
					if(!cargoService.updateCargoProductStockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
	                	return "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！";
	                }
					if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
	                	return "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！";
	                }
				}
				
				if(bean.getStockOutType() == ProductStockBean.STOCKTYPE_QUALIFIED){
					//更新订单缺货状态
					this.updateLackOrder(sep.getProductId());
				}
			}
			if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS4 + ", auditing_user_id=" + user.getId()+",auditing_user_name='"+user.getUsername()+"'", "id=" + bean.getId())){
	            return "更新调拨单信息失败！";
			}
	
			// log记录
			StockAdminHistoryBean log = new StockAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("商品调配操作：" + bean.getName() + "：出库审核未通过");
			log.setType(StockAdminHistoryBean.CHANGE);
			if(!stockService.addStockAdminHistory(log)){
				return "更新库存操作日志失败！";
			}
        } catch (Exception e) {
        	System.out.print(DateUtil.getNow());e.printStackTrace();
        	stockLog.error(StringUtil.getExceptionInfo(e));
        	return "异常！";
        }
        return null;
    }
    
    /**
     * 调拨入库审核通过
     * @param user
     * @param bean
     * @param dbOp
     * @param request
     * @param condition
     * @return
     */
    public String auditInStockExchangePass(voUser user, StockExchangeBean bean, DbOperation dbOp, HttpServletRequest request, String condition) {
    	UserGroupBean group = user.getGroup();
    	boolean auditingOut = group.isFlag(81);
    	if (!auditingOut) {
    		return "您没有此操作权限！";
    	}
    	WareService wareService = new WareService(dbOp);
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        
        //财务基础数据
      	List<BaseProductInfo> financeBaseData = new ArrayList<BaseProductInfo>();
        try {
	    	ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
			Iterator itr = sepList.iterator();
			StockExchangeProductBean sep = null;
			voProduct product = null;
			ProductStockBean psOut = null;
			ProductStockBean psIn = null;
			String set = null;
			while (itr.hasNext()) {
				sep = (StockExchangeProductBean) itr.next();
				product = wareService.getProduct(sep.getProductId());
				psIn = service.getProductStock("id=" + sep.getStockInId());
				psOut = service.getProductStock("id=" + sep.getStockOutId());
				if(!CargoDeptAreaService. hasCargoDeptArea(request, psIn.getArea(), psIn.getType())){
					return "用户只能审核自己所属库地区和库类型的调拨单";
				}
				// 入库
				set = "status = " + StockExchangeProductBean.STOCKIN_DEALED
						+ ", remark = '操作前库存"
						+ psIn.getStock() + ",操作后库存"
						+ (psIn.getStock() + sep.getStockInCount())
						+ "', confirm_datetime = now()";
				if(!service.updateStockExchangeProduct(set, "id = " + sep.getId())){
	                return "更新调拨商品信息失败！";
				}
				if(!service.updateProductLockCount(sep.getStockOutId(), -sep.getStockOutCount())){
					return "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！";
				}
				
				if(!service.updateProductStockCount(sep.getStockInId(), sep.getStockInCount())){
					return "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！";
				}
				
				
				// log记录
				StockAdminHistoryBean log = new StockAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("商品调配操作：" + bean.getName() + ": 将商品[" + product.getCode() + "]入库");
				log.setType(StockAdminHistoryBean.CHANGE);
				if(!stockService.addStockAdminHistory(log)){
	                return "添加库存操作日志失败！";
				}
	
				
				//更新调拨单未上架数量
				if(!service.updateStockExchangeProduct("no_up_cargo_count = "+sep.getStockOutCount(), "id = "+sep.getId())){
	                return "更新调拨单未上架数量失败！";
				}
	
				
				//财务基础数据
				BaseProductInfo base = new BaseProductInfo();
				base.setId(sep.getProductId());
				base.setProductStockId(psIn.getId());
				base.setProductStockOutId(psOut.getId());
				base.setOutCount(sep.getStockOutCount());
				financeBaseData.add(base);
				
				//更新批次记录、添加调拨出、入库批次记录
//				List sbList = stockService.getStockBatchList("product_id="+psOut.getProductId()+" and stock_type="+psOut.getType()+" and stock_area="+psOut.getArea(), -1, -1, "id asc");
//				double stockinPrice = 0;
//				double stockoutPrice = 0;
//				if(sbList!=null&&sbList.size()!=0){
//					int stockExchangeCount = sep.getStockOutCount();
//					int index = 0;
//					int stockBatchCount = 0;
//					
//					do {
//						//出库
//						StockBatchBean batch = (StockBatchBean)sbList.get(index);
//						int ticket = FinanceSellProductBean.queryTicket(service.getDbOp(), batch.getCode());	//是否含票 
//						int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), ticket);
//						if(stockExchangeCount>=batch.getBatchCount()){
//							if(!stockService.deleteStockBatch("id="+batch.getId())){
//				                return "删除库存批次失败！";
//							}
//							stockBatchCount = batch.getBatchCount();
//						}else{
//							if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//				                return "更新库存批次失败!";
//							}
//							stockBatchCount = stockExchangeCount;
//						}
//						
//						//添加批次操作记录
//						StockBatchLogBean batchLog = new StockBatchLogBean();
//						batchLog.setCode(bean.getCode());
//						batchLog.setStockType(batch.getStockType());
//						batchLog.setStockArea(batch.getStockArea());
//						batchLog.setBatchCode(batch.getCode());
//						batchLog.setBatchCount(stockBatchCount);
//						batchLog.setBatchPrice(batch.getPrice());
//						batchLog.setProductId(batch.getProductId());
//						batchLog.setRemark("调拨出库");
//						batchLog.setCreateDatetime(DateUtil.getNow());
//						batchLog.setUserId(user.getId());
//						if(!stockService.addStockBatchLog(batchLog)){
//				             return "添加库存批次操作日志失败！";
//						}
//						
//						//财务进销存卡片---liuruilan-----
//						FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//						product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
//						int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), psOut.getArea(), psOut.getType(), ticket, batch.getProductId());
//						int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, psOut.getType(), ticket, batch.getProductId());
//						int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), psOut.getArea(), -1,ticket, batch.getProductId());
//						FinanceStockCardBean fsc = new FinanceStockCardBean();
//						fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//						fsc.setCode(bean.getCode());
//						fsc.setCreateDatetime(DateUtil.getNow());
//						fsc.setStockType(psOut.getType());
//						fsc.setStockArea(psOut.getArea());
//						fsc.setProductId(batch.getProductId());
//						fsc.setStockId(sep.getStockInId());
//						fsc.setStockInCount(stockBatchCount);
//						fsc.setStockAllArea(stockAllArea);
//						fsc.setStockAllType(stockAllType);
//						fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//						fsc.setStockPrice(product.getPrice5());
//						
//						fsc.setCurrentStock(currentStock);
//						fsc.setType(fsc.getCardType());
//						fsc.setIsTicket(ticket);
//						fsc.setStockBatchCode(batch.getCode());
//						fsc.setBalanceModeStockCount(_count - stockBatchCount);
//						if(ticket == 0){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//						}
//						if(ticket == 1){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//						}
//						double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//						fsc.setAllStockPriceSum(tmpPrice);
//						
//						if(!frfService.addFinanceStockCardBean(fsc))
//						{
//							return "添加财务进销存卡片失败！";
//						}
//						
//						//---------------liuruilan-----------
//						
//						stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//						
//						//入库
//						StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+psIn.getType()+" and stock_area="+psIn.getArea());
//						if(batchBean!=null){
//							if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//				                return "更新库存批次数量失败！";
//							}
//						}else{
//							int _ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
//							StockBatchBean newBatch = new StockBatchBean();
//							newBatch.setCode(batch.getCode());
//							newBatch.setProductId(batch.getProductId());
//							newBatch.setPrice(batch.getPrice());
//							newBatch.setBatchCount(stockBatchCount);
//							newBatch.setProductStockId(psIn.getId());
//							newBatch.setStockArea(bean.getStockInArea());
//							newBatch.setStockType(psIn.getType());
//							newBatch.setTicket(_ticket);
//							newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//							if(!stockService.addStockBatch(newBatch)){
//								return "添加库存批次失败！";
//							}
//						}
//						
//						//添加批次操作记录
//						batchLog = new StockBatchLogBean();
//						batchLog.setCode(bean.getCode());
//						batchLog.setStockType(psIn.getType());
//						batchLog.setStockArea(bean.getStockInArea());
//						batchLog.setBatchCode(batch.getCode());
//						batchLog.setBatchCount(stockBatchCount);
//						batchLog.setBatchPrice(batch.getPrice());
//						batchLog.setProductId(batch.getProductId());
//						batchLog.setRemark("调拨入库");
//						batchLog.setCreateDatetime(DateUtil.getNow());
//						batchLog.setUserId(user.getId());
//						if(!stockService.addStockBatchLog(batchLog)){
//							return "库存批次日志添加失败！";
//						}
//						
//						//财务进销存卡片---liuruilan-----2012-11-02-----
//						int stockinCount = stockBatchCount;
//						product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
//						currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, batch.getProductId());
//						 stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1,batchLog.getStockType(), ticket, batch.getProductId());
//						 stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(),batchLog.getStockArea(), -1,ticket, batch.getProductId());
//						fsc = new FinanceStockCardBean();
//						fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//						fsc.setCode(bean.getCode());
//						fsc.setCreateDatetime(DateUtil.getNow());
//						fsc.setStockType(batchLog.getStockType());
//						fsc.setStockArea(batchLog.getStockArea());
//						fsc.setProductId(batch.getProductId());
//						fsc.setStockId(sep.getStockInId());
//						fsc.setStockInCount(stockinCount);	
//						fsc.setCurrentStock(currentStock);	//只记录分库总库存
//						fsc.setStockAllArea(stockAllArea);
//						fsc.setStockAllType(stockAllType);
//						fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//						fsc.setStockPrice(product.getPrice5());
//						
//						fsc.setType(fsc.getCardType());
//						fsc.setIsTicket(ticket);
//						fsc.setStockBatchCode(batchLog.getBatchCode());
//						fsc.setBalanceModeStockCount(_count - stockBatchCount + stockinCount);
//						if(ticket == 0){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockinCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//						}
//						if(ticket == 1){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockinCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//						}
//						tmpPrice = Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket())));
//						fsc.setAllStockPriceSum(tmpPrice);
//						if(!frfService.addFinanceStockCardBean(fsc))
//						{
//							return "添加财务进销存卡片失败！";
//						}
//						//-----------liuruilan-------------
//						
//						
//						stockExchangeCount -= batch.getBatchCount();
//						index++;
//						
//						stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//					} while (stockExchangeCount>0&&index<sbList.size());
//				}
				
				//处理进销存卡片
				product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
				//添加进销存卡片
				// 出库卡片
				StockCardBean sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
				sc.setCode(bean.getCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(bean.getStockOutType());
				sc.setStockArea(bean.getStockOutArea());
				sc.setProductId(sep.getProductId());
				sc.setStockId(sep.getStockOutId());
				sc.setStockOutCount(sep.getStockOutCount());
				sc.setStockOutPriceSum((new BigDecimal(sep.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(bean.getStockOutArea(), bean.getStockOutType()) + product.getLockCount(bean.getStockOutArea(), bean.getStockOutType()));
				sc.setStockAllArea(product.getStock(bean.getStockOutArea()) + product.getLockCount(bean.getStockOutArea()));
				sc.setStockAllType(product.getStockAllType(bean.getStockOutType()) + product.getLockCountAllType(bean.getStockOutType()));
				sc.setAllStock(product.getStockAll() + product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				if(!service.addStockCard(sc)){
					return "添加出库进销存卡片失败！";
				}
				
				// 入库卡片
				sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
				sc.setCode(bean.getCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(bean.getStockInType());
				sc.setStockArea(bean.getStockInArea());
				sc.setProductId(sep.getProductId());
				sc.setStockId(sep.getStockInId());
				sc.setStockInCount(sep.getStockInCount());
				sc.setStockInPriceSum((new BigDecimal(sep.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(bean.getStockInArea(), bean.getStockInType()) + product.getLockCount(bean.getStockInArea(), bean.getStockInType()));
				sc.setStockAllArea(product.getStock(bean.getStockInArea()) + product.getLockCount(bean.getStockInArea()));
				sc.setStockAllType(product.getStockAllType(bean.getStockInType()) + product.getLockCountAllType(bean.getStockInType()));
				sc.setAllStock(product.getStockAll() + product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				if(!service.addStockCard(sc)){
					return "添加入库进销存卡片失败！";
				}
				
				//处理货位库存
				product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
				
				//源货位
				List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
				for(int i=0;i<sepcOutList.size();i++){
					StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
					if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
						return "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！";
					}
					
					CargoProductStockBean cpsOut = cargoService.getCargoAndProductStock("cps.id = "+sepcOut.getCargoProductStockId());
					
					//货位出库卡片
					CargoStockCardBean csc = new CargoStockCardBean();
					csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
					csc.setCode(bean.getCode());
					csc.setCreateDatetime(DateUtil.getNow());
					csc.setStockType(bean.getStockOutType());
					csc.setStockArea(bean.getStockOutArea());
					csc.setProductId(sep.getProductId());
					csc.setStockId(cpsOut.getId());
					csc.setStockOutCount(sep.getStockOutCount());
					csc.setStockOutPriceSum((new BigDecimal(sep.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
					csc.setAllStock(product.getStockAll() + product.getLockCountAll());
					csc.setCurrentCargoStock(cpsOut.getStockCount()+cpsOut.getStockLockCount());
					csc.setCargoStoreType(cpsOut.getCargoInfo().getStoreType());
					csc.setCargoWholeCode(cpsOut.getCargoInfo().getWholeCode());
					csc.setStockPrice(product.getPrice5());
					csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(csc)){
						return "添加货位出库卡片失败！";
					}
				}
				
				//目的货位
				List sepcInList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 1", -1, -1, "id asc");
				for(int i=0;i<sepcInList.size();i++){
					StockExchangeProductCargoBean sepcIn = (StockExchangeProductCargoBean)sepcInList.get(i);
					if(!cargoService.updateCargoProductStockCount(sepcIn.getCargoProductStockId(), sepcIn.getStockCount())){
						return "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！";
					}
	
					// 审核通过，就加 进销存卡片
					CargoProductStockBean cpsIn = cargoService.getCargoAndProductStock("cps.id = "+sepcIn.getCargoProductStockId());
					
					//货位入库卡片
					CargoStockCardBean csc = new CargoStockCardBean();
					csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
					csc.setCode(bean.getCode());
					csc.setCreateDatetime(DateUtil.getNow());
					csc.setStockType(bean.getStockInType());
					csc.setStockArea(bean.getStockInArea());
					csc.setProductId(sep.getProductId());
					csc.setStockId(cpsIn.getId());
					csc.setStockInCount(sep.getStockInCount());
					csc.setStockInPriceSum((new BigDecimal(sep.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
					csc.setAllStock(product.getStockAll() + product.getLockCountAll());
					csc.setCurrentCargoStock(cpsIn.getStockCount()+cpsIn.getStockLockCount());
					csc.setCargoStoreType(cpsIn.getCargoInfo().getStoreType());
					csc.setCargoWholeCode(cpsIn.getCargoInfo().getWholeCode());
					csc.setStockPrice(product.getPrice5());
					csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(csc)){
						return "添加货位入库卡片失败！";
					}
				}
				
				if(bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED){
					//更新订单缺货状态
					this.updateLackOrder(sep.getProductId());
				}
			}
			
			//财务数据
			if(financeBaseData != null && financeBaseData.size() > 0){
				FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
				baseService.acquireFinanceBaseData(financeBaseData, bean.getCode(), user.getId(), 0, 0);
			}
			
			
			
			if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS7 + ", auditing_user_id2=" + user.getId()+", auditing_user_name2='"+user.getUsername()+"'", "id=" + bean.getId())){
				return "更新调拨信息失败！";
			}
			if(bean.getStockInType() != 0){
				if(!service.updateStockExchange("up_shelf_status = 2", "id=" + bean.getId()))
				{
					return "数据库操作失败";
				}
			}
			
			// log记录
			StockAdminHistoryBean log = new StockAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("商品调配操作：" + bean.getName() + "：入库审核通过");
			log.setType(StockAdminHistoryBean.CHANGE);
			if(!stockService.addStockAdminHistory(log)){
				return "添加库存操作日志失败！";
			}
			//目的库是返厂库，则需将imei码的状态变为返厂中
			if (bean.getStockInType() == ProductStockBean.STOCKTYPE_BACK) {
				List<IMEIStockExchangeBean> imeiStockExchangeList = imeiService.getIMEIStockExchangeList("stock_exchange_id="+bean.getId(), -1, -1, null);
				if (imeiStockExchangeList != null && imeiStockExchangeList.size() != 0) {
					
					IMEILogBean imeiLog = null;
					for (IMEIStockExchangeBean imeiStockExchange : imeiStockExchangeList) {
						IMEIBean imei = imeiService.getIMEI("code='"+imeiStockExchange.getIMEI()+"'");
						if (imei == null) {
							return "IMEI码不存在！";
						}
						if (imei.status != IMEIBean.IMEISTATUS2) {
							return "存在不是【可出库】状态的IMEI码商品！";
						}
						imeiLog = new IMEILogBean();
						imeiLog.setIMEI(imei.getCode());
						imeiLog.setOperCode(bean.getCode());
						imeiLog.setOperType(IMEILogBean.OPERTYPE3);
						imeiLog.setCreateDatetime(DateUtil.getNow());
						imeiLog.setUserId(user.getId());
						imeiLog.setUserName(user.getUsername());
						imeiLog.setContent("调拨，入库审核通过，IMEI码:"+imei.getCode()+"状态由【可出库】变为【返厂中】"+",地区："+ProductStockBean.areaMap.get(1));
						if (!imeiService.addIMEILog(imeiLog)) {
							return "记录IMEI码操作日志失败！";
						}
						if (!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS4, "id=" + imei.getId())) {
							return "更新imei码状态失败！";
						}
					}
				}
			}
        } catch (Exception e) {
        	System.out.print(DateUtil.getNow());e.printStackTrace();
        	stockLog.error(StringUtil.getExceptionInfo(e));
        	return "异常！";
        }
        return null;
    }
    
    /**
     * 调拨入库审核不通过
     * @param user
     * @param bean
     * @param dbOp
     * @param condition
     * @return
     */
    public String auditInStockExchangeNoPass(voUser user, StockExchangeBean bean, DbOperation dbOp, String condition) {
    	UserGroupBean group = user.getGroup();
    	boolean auditingOut = group.isFlag(81);
    	if (!auditingOut) {
    		return "您没有此操作权限！";
    	}
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
    	try {
	    	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS2 + ", auditing_user_id2=" + user.getId(), "id=" + bean.getId())){
				return "更新调拨单信息失败！";
			}
			if(!service.updateStockExchangeProduct("status=" + StockExchangeProductBean.STOCKOUT_DEALED, condition)){
				return "更新调拨单商品信息失败！";
			}
			// log记录
			StockAdminHistoryBean log = new StockAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("商品调配操作：" + bean.getName() + "：入库审核未通过");
			log.setType(StockAdminHistoryBean.CHANGE);
			if(!stockService.addStockAdminHistory(log)){
				return "添加库存操作日志信息失败！";
			}
    	} catch (Exception e) {
    		System.out.print(DateUtil.getNow());e.printStackTrace();
        	stockLog.error(StringUtil.getExceptionInfo(e));
        	return "异常！";
    	}
    	return null;
    }
    
    /**
	 * 说明：确认库存调配——确认出库、确认入库
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
    @RequestMapping("/completeStockExchange")
    @ResponseBody
    public Json completeStockExchange(HttpServletRequest request, HttpServletResponse response) {
    	Json j = new Json();
    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		j.setMsg("当前没有登录，操作失败！");
    		return j;
    	}
    	UserGroupBean group = user.getGroup();
    	boolean edit = false;
    	boolean stockIn = group.isFlag(83);
    	boolean stockOut = group.isFlag(82);

    	synchronized(lock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
            int sepId = StringUtil.StringToId(request.getParameter("sepId"));//StockExchangeProductId
            int confirm = StringUtil.StringToId(request.getParameter("confirm"));
            int hisStatus = StringUtil.toInt(request.getParameter("hisStatus"));
	        String back = StringUtil.dealParam(request.getParameter("back"));
	        WareService wareService = new WareService();
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	j.setMsg("该调拨单不存在或已删除!");
	                return j;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }

	            //当 调配单 的状态 不是 “未处理” 或者 “检测处理中” 的时候，不能进行确认操作
	            if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS5 && bean.getStatus() != StockExchangeBean.STATUS6 && bean.getStatus() != StockExchangeBean.STATUS8) {
	                j.setMsg("该操作已经完成，不能再更改！");
	                return j;
	            }
	            if(service.getStockExchangeProductCount("stock_exchange_id=" + bean.getId()) <= 0){
	            	j.setMsg("没有要调拨的商品，不能执行该操作！");
	                return j;
	            }

	            //开始事务
	            service.getDbOp().startTransaction();
	            //根据 调配单的不同状态，进行不同的处理
	            if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS2 || bean.getStatus() == StockExchangeBean.STATUS4){
	            	if(!stockOut){
	            		j.setMsg("你没有权限进行该操作，请与管理员联系！");
		                return j;
	            	}
	            	if(!edit){
	            		j.setMsg("对不起，你没有权限编辑其他人的调拨单！");
		                return j;
		            }
	            	//如果当前状态为 未处理，则要将相关产品的 处理状态 改为 第一步处理完成
	            	StringBuilder buf = new StringBuilder();
		            String condition = null;
		            if(confirm == 1){
			            buf.append("stock_exchange_id = ");
			            buf.append(bean.getId());
			            buf.append(" and status = ");
			            buf.append(StockExchangeProductBean.STOCKOUT_UNDEAL);
			            if (sepId > 0) {
			                buf.append(" and id = ");
			                buf.append(sepId);
			            }
			            condition = buf.toString();
		            	int count = service.getStockExchangeProductCount(condition);
		            	if(count > 0){
		            		j.setMsg("还有没确认的商品，不能完成出库操作！");
			                return j;
		            	}
		            	String set = "status = " + StockExchangeBean.STATUS2 + ", confirm_datetime = now(), stock_out_oper=" + user.getId()+", stock_out_oper_name='"+user.getUsername()+"'";
		            	if(!service.updateStockExchange(set, "id = " + bean.getId()))
		            	{
		            	  service.getDbOp().rollbackTransaction();
		            	  j.setMsg("数据库操作失败");
		            	  return j;
		            	}
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成出库操作");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log))
						{
						  service.getDbOp().rollbackTransaction();
						  j.setMsg("数据库操作失败");
						  return j;
						}
		            } else {
			            buf.append("stock_exchange_id = ");
			            buf.append(bean.getId());
			            if (sepId > 0) {
			                buf.append(" and id = ");
			                buf.append(sepId);
			            }
			            condition = buf.toString();
		                if(hisStatus == -1){
		                	hisStatus = StockExchangeProductBean.STOCKOUT_DEALED;
		                }else if(hisStatus == StockExchangeProductBean.STOCKIN_DEALED || hisStatus == StockExchangeProductBean.STOCKIN_UNDEAL){
		                	j.setMsg("调拨单状态错误，操作失败");
							service.getDbOp().rollbackTransaction();
							return j;
		                }
			            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
			            Iterator itr = sepList.iterator();
			            StockExchangeProductBean sep = null;
			            voProduct product = null;
			            String set = null;
			            while (itr.hasNext()) {
			                sep = (StockExchangeProductBean) itr.next();
			                product = wareService.getProduct(sep.getProductId());
							if(sep.getReason() <= 0 || StringUtil.isNull(sep.getReasonText())){
								j.setMsg(product.getName() + "没有设置调拨原因！");
								service.getDbOp().rollbackTransaction();
								return j;
							}

							ProductStockBean psOut = null;
							ProductStockBean psIn = null;
							// 如果是 没处理的 记录，在确认出库的时候，就要把 库存数量锁定
							if (hisStatus == StockExchangeProductBean.STOCKOUT_DEALED && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
								product = wareService.getProduct(sep.getProductId());
								psIn = service.getProductStock("id=" + sep.getStockInId());
								psOut = service.getProductStock("id=" + sep.getStockOutId());
								
								if (sep.getStockOutCount() > psOut.getStock()) {
									j.setMsg( product.getName() + "的库存不足！");
									service.getDbOp().rollbackTransaction();
									return j;
								}
								set = "remark = '操作前库存"
										+ psOut.getStock() + ",操作后库存"
										+ (psOut.getStock() - sep.getStockOutCount())
										+ "', confirm_datetime = now()";
								if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
								{
								  service.getDbOp().rollbackTransaction();
								  j.setMsg("数据库操作失败");
								  return j;
								}
								//service.updateProductStock("stock=(stock - " + sep.getStockOutCount() + "), lock_count=(lock_count + " + sep.getStockOutCount() + ")", "id=" + sep.getStockOutId());
								if(!service.updateProductStockCount(sep.getStockOutId(), -sep.getStockOutCount())){
									service.getDbOp().rollbackTransaction();
									j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
				                    return j;
								}
								if(!service.updateProductLockCount(sep.getStockOutId(), sep.getStockOutCount())){
									service.getDbOp().rollbackTransaction();
									j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
				                    return j;
								}

								//如果是从合格库调拨出去，则需要自动检测库存，如果合格库的库存为0，则隐藏商品
								if(psOut.getType()==ProductStockBean.STOCKTYPE_QUALIFIED && psIn.getType() != ProductStockBean.STOCKTYPE_QUALIFIED 
										&& (product.getParentId1() == 123 || product.getParentId1() == 143
										|| product.getParentId1() == 316 || product.getParentId1() == 317
										|| product.getParentId1() == 119 || product.getParentId1() == 340
										|| product.getParentId1() == 1385 || product.getParentId1() == 1425 
										|| product.getParentId1() == 544 || product.getParentId1() == 545
										|| product.getParentId1() == 458 || product.getParentId1() == 459 
										|| product.getParentId1() == 401 || product.getParentId1() == 136 
										|| product.getParentId2() == 203 || product.getParentId2() == 204 
										|| product.getParentId2() == 205 || product.getParentId2() == 699
										|| product.getParentId1() == 145 || product.getParentId1() == 151
										|| product.getParentId1() == 197 || product.getParentId1() == 505
										|| product.getParentId1() == 163 || product.getParentId1() == 690
										|| product.getParentId1() == 908|| product.getParentId1() == 752
										|| product.getParentId1() == 803
										|| product.getParentId1() == 183 || product.getParentId1() == 184
										|| product.getParentId1() == 1093 || product.getParentId1() == 1094
										|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222)){
									service.checkProductStatus(sep.getProductId());
								}

								// log记录
								StockAdminHistoryBean log = new StockAdminHistoryBean();
								log.setAdminId(user.getId());
								log.setAdminName(user.getUsername());
								log.setLogId(bean.getId());
								log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
								log.setOperDatetime(DateUtil.getNow());
								log.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]出库");
								log.setType(StockAdminHistoryBean.CHANGE);
								if(!stockService.addStockAdminHistory(log))
								{
								  service.getDbOp().rollbackTransaction();
								  j.setMsg("数据库操作失败");
								  return j;
								}
								
								//锁定货位库存
								//出库
								List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
								for(int i=0;i<sepcOutList.size();i++){
									StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
									if(!cargoService.updateCargoProductStockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
										j.setMsg("商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
				                    	service.getDbOp().rollbackTransaction();
				        				return j;
				                    }
									if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
										j.setMsg("商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
				                    	service.getDbOp().rollbackTransaction();
				        				return j;
				                    }
								}
							}

			                set = "status = " + hisStatus + ", confirm_datetime = now()";
		                    if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      j.setMsg("数据库操作失败");
		                      return j;
		                    }
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							if(hisStatus == StockExchangeProductBean.STOCKOUT_DEALED){
								log.setRemark("商品调配操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]出库");
							} else if(hisStatus == StockExchangeProductBean.STOCKOUT_UNDEAL) {
								log.setRemark("商品调配操作：" + bean.getName() + ": 取消商品[" + product.getCode() + "]的确认出库");
							}
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log))
							{
							  service.getDbOp().rollbackTransaction();
							  j.setMsg("数据库操作失败");
							  return j;
							}
			            }
			            if(bean.getStatus() == StockExchangeBean.STATUS4){
			            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
			            	{
			            	  service.getDbOp().rollbackTransaction();
			            	  request.setAttribute("tip", "数据库操作失败");
			            	  return j;
			            	}
			            }
		            }
	            } else if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5 || bean.getStatus() == StockExchangeBean.STATUS8){
	            	StringBuilder buf = new StringBuilder();
		            String condition = null;
		            buf.append("stock_exchange_id = ");
		            buf.append(bean.getId());
		            buf.append(" and status = ");
		            buf.append(StockExchangeProductBean.STOCKIN_UNDEAL);
		            if (sepId > 0) {
		                buf.append(" and id = ");
		                buf.append(sepId);
		            }
		            if(!stockIn){
	            		j.setMsg("你没有权限进行该操作，请与管理员联系！");
		                return j;
	            	}
		            condition = buf.toString();
		            if(confirm == 1){
		            	int count = service.getStockExchangeProductCount(condition);
		            	if(count > 0){
		            		j.setMsg("还有没确认的商品，不能完成入库操作！");
			                return j;
		            	}
		            	String set = "status = " + StockExchangeBean.STATUS6 + ", confirm_datetime = now(), stock_in_oper=" + user.getId()+", stock_in_oper_name='"+user.getUsername()+"'";
	                    if(!service.updateStockExchange(set, "id = " + bean.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      j.setMsg("数据库操作失败");
	                      return j;
	                    }
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成入库操作");
						log.setType(StockAdminHistoryBean.CHANGE);
						 if(!stockService.addStockAdminHistory(log))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      j.setMsg("数据库操作失败");
		                      return j;
		                    }
		            } else {
		                if(hisStatus == -1){
		                	hisStatus = StockExchangeProductBean.STOCKIN_DEALED;
		                }else if(hisStatus == StockExchangeProductBean.STOCKOUT_DEALED || hisStatus == StockExchangeProductBean.STOCKOUT_UNDEAL){
		                	j.setMsg("调拨单状态错误，操作失败");
							service.getDbOp().rollbackTransaction();
							return j;
		                }
			            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
			            Iterator itr = sepList.iterator();
			            StockExchangeProductBean sep = null;
			            voProduct product = null;
			            String set = null;
			            while (itr.hasNext()) {
			                sep = (StockExchangeProductBean) itr.next();
			                product = wareService.getProduct(sep.getProductId());
		                    set = "status = " + hisStatus + ", confirm_datetime = now()";
		                    if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      j.setMsg("数据库操作失败");
		                      return j;
		                    }
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品检测操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]入库");
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      j.setMsg("数据库操作失败");
		                      return j;
		                    }
							
			            }
			            if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS8){
			            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS5, "id=" + bean.getId()))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      j.setMsg("数据库操作失败");
		                      return j;
		                    }
			            }
		            }
	            }

	            //提交事务
	            service.getDbOp().commitTransaction();
	            j.setMsg("操作成功！");
	            j.setSuccess(true);
	            return j;
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				System.out.print(DateUtil.getNow());e.printStackTrace();
				j.setMsg("异常！");
				return j;
			} finally {
	            service.releaseAll();
	        }
    	}
    }
    
    public void updateLackOrder(int productId){
    	DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
    	DbOperation dbOp = new DbOperation(DbOperation.DB);
    	WareService wareService = new WareService(dbOp_slave);
    	IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
    	try{
    		String productIds = productId+"";
    		//查询父商品
    		List ppList = ppService.getProductPackageList("product_id=" + productId, -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
    			productIds = productIds + "," + ppBean.getParentId();
			}
    		
    		List lackOrders = wareService.getOrdersByProducts("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null);
    		lackOrders.addAll(wareService.getOrdersByPresents("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null));
    		Iterator iter = lackOrders.listIterator();
    		while(iter.hasNext()){
    			voOrder order = (voOrder)iter.next();

				// 判断订单中商品的库存是否满足，根据库存状态，设置订单发货状态
				List orderProductList = wareService.getOrderProducts(order.getId());
				List orderPresentList = wareService.getOrderPresents(order.getId());
				orderProductList.addAll(orderPresentList);

				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(service.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							detailList.add(tempVOP);
						}
					} else {
						vop.setPsList(service.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						detailList.add(vop);
					}
				}
				orderProductList = detailList;

				if (checkStock(orderProductList,ProductStockBean.AREA_GF) || checkStock(orderProductList,ProductStockBean.AREA_ZC)) {
					dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+order.getId());
					dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+order.getId());
				}
			
    		}
    	}catch (Exception e) {
    		System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			dbOp_slave.release();
			dbOp.release();
		}
    	
    }
    
    public boolean checkStock(List orderProductList,int area) {
		if (orderProductList == null) {
			return false;
		}

		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.STOCKTYPE_QUALIFIED,area) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
    
    /**
	 * 调拨单信息
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getStockExchangeInfo")
	@ResponseBody
	public Json getStockExchangeInfo(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		int id = StringUtil.toInt(request.getParameter("stockExchangeId"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try {
			if (id == -1) {
				j.setMsg("调拨单id错误！");
				return j;
			}
			
			StockExchangeBean stockExchangeBean = service.getStockExchange("id=" + id);
			if (stockExchangeBean == null) {
				j.setMsg("调拨单不存在！");
				return j;
			}
			StringBuffer buff = new StringBuffer();
			List<String> list = new ArrayList<String>();
			//编辑、确认提交
			if(stockExchangeBean.getStatus() == StockExchangeBean.STATUS4 ) {
				list.add("edit");
				buff.append("<th>目的库：</th>");
				buff.append("<th>库区：</th>");
				buff.append("<td>").append(ProductStockBean.areaMap.get(stockExchangeBean.getStockInArea())).append("</td>");
				buff.append("<th>库类型：</th>");
				buff.append("<td>").append(ProductStockBean.stockTypeMap.get(stockExchangeBean.getStockInType())).append("</td>");
				buff.append("<input id=\"stockArea\" name=\"stockArea\" type=\"hidden\" value=\"" + stockExchangeBean.getStockInArea() + "\"/>");
				buff.append("<input id=\"stockType\" name=\"stockType\" type=\"hidden\" value=\"" + stockExchangeBean.getStockInType() + "\"/>");
				buff.append("<input id=\"stockExchangeId\" name=\"stockExchangeId\" type=\"hidden\" value=\"" + stockExchangeBean.getId() + "\"/>");
			} else {
				list.add("audit");
				buff.append("<input id=\"stockExchangeId\" name=\"stockExchangeId\" type=\"hidden\" value=\"" + stockExchangeBean.getId() + "\"/>");
				buff.append("<table class=\"tableForm\">");
				buff.append("<tr align=\"center\" >");
				buff.append("<th>调拨单编号：</th>");
				buff.append("<td>").append(stockExchangeBean.getCode()).append("</td>");
				buff.append("<th>目的库类型：</th>");
				buff.append("<td>").append(ProductStockBean.stockTypeMap.get(stockExchangeBean.getStockInType())).append("</td>");
				buff.append("<th>目的库地区：</th>");
				buff.append("<td>").append(ProductStockBean.areaMap.get(stockExchangeBean.getStockInArea())).append("</td>");
				buff.append("<th>商品数量：</th>");
				int count = afStockService.getAfterSaleStockExchangeProductCount("stock_exchange_id = " + stockExchangeBean.getId());
				buff.append("<td>").append(count).append("</td>");
				buff.append("<th>生成时间：</th>");
				buff.append("<td>").append(stockExchangeBean.getCreateDatetime().substring(0, 10)).append("</td>");
				buff.append("<th>状态：</th>");
				buff.append("<td>").append(stockExchangeBean.getStatusName()).append("</td>");
				buff.append("</tr>");
				buff.append("</table>");
			}
			
			
			list.add(buff.toString());
			
			j.setSuccess(true);
			j.setObj(list);
			return j;
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 调拨单信息
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getAfStockExchangeProductCargoList")
	@ResponseBody
	public EasyuiDataGridJson getAfStockExchangeProductCargoList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		int id = StringUtil.toInt(request.getParameter("id"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		try {
			if (id == -1) {
				request.setAttribute("msg", "调拨单id错误！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			StockExchangeBean stockExchangeBean = service.getStockExchange("id=" + id);
			if (stockExchangeBean == null) {
				request.setAttribute("msg", "调拨单不存在！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			int count = service.getStockExchangeProductCargoCount("stock_exchange_id=" + stockExchangeBean.getId() + " and type=1");
			datagrid.setTotal((long) count);
			
			List<StockExchangeProductCargoBean> list = service.getStockExchangeProductCargoList("stock_exchange_id=" + stockExchangeBean.getId() + " and type=1",  (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), null);
			List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
			Map<String, String> map = null;
			for (StockExchangeProductCargoBean sepcBean : list) {
				StockExchangeProductBean sepBean = service.getStockExchangeProduct("id=" + sepcBean.getStockExchangeProductId());
				if (sepBean == null) {
					request.setAttribute("msg", "调拨单商品不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				voProduct product = wareService.getProduct(sepBean.getProductId());
				if (product == null) {
					request.setAttribute("msg", "商品不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				CargoInfoBean ci = cargoService.getCargoInfo("id=" + sepcBean.getCargoInfoId());
				if (ci == null) {
					request.setAttribute("msg", "货位不存在！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
						null));
				int cargoStockCount = 0;
				ResultSet rs = service.getDbOp().executeQuery("select sum(stock_count) from cargo_product_stock where id=" + sepcBean.getCargoProductStockId());
				if (rs.next()) {
					cargoStockCount = rs.getInt(1);
				}
				rs.close();
				map = new HashMap<String, String>();
				map.put("oriName", product.getOriname());
				map.put("productCode", product.getCode());
				map.put("stockCount", sepcBean.getStockCount() + "");
				map.put("stock", product.getStock(stockExchangeBean.getStockInArea(), stockExchangeBean.getStockInArea()) + "");
				map.put("inWholeCode", ci.getWholeCode() + "(" + cargoStockCount + ")");
				returnList.add(map);
			}
			datagrid.setRows(returnList);
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 调拨单权限判断
	 * @author zy
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getStockExchangePower")
	@ResponseBody
	public Json getStockExchangePower(HttpServletRequest request, HttpServletResponse response) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		
		int id = StringUtil.toInt(request.getParameter("id"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		try {
			if (id == -1) {
				j.setMsg("调拨单id错误！");
				return j;
			}
			
			StockExchangeBean bean = service.getStockExchange("id=" + id);
			if (bean == null) {
				j.setMsg("调拨单不存在！");
				return j;
			}
			boolean edit = false;
			if (bean.getCreateUserId() == user.getId()) {
				edit = true;
			}
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4){
				if (group.isFlag(82) && edit){ 
					//确认出库
					map.put("outConfirmFlag", true);
				}
			} else if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5){
				if(group.isFlag(83)){ 
					//确认入库
					map.put("inConfirmFlag", true);
				}
			} else if(bean.getStatus() == StockExchangeBean.STATUS2){
				if(group.isFlag(80)){
					//审核出库
					map.put("outAuditFlag", true);
				}
			} else if(bean.getStatus() == StockExchangeBean.STATUS6){
				if(group.isFlag(81)){
					//审核入库
					map.put("inAuditFlag", true);
				}
			}
			j.setSuccess(true);
			j.setObj(map);
			return j;
			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常！");
			return j;
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 
	 * 说明：删除商品调配单
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/deleteStockExchange")
    public String deleteStockExchange(HttpServletRequest request, HttpServletResponse response) {
		String s = "admin/productStock/stockExchangeList";
    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return s;
    	}
    	UserGroupBean group = user.getGroup();
    	boolean edit = false;

    	synchronized(lock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            if(bean.getCreateUserId() == user.getId()||group.isFlag(0)){
	            	edit = true;
	            }
            	if(!edit){
	            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	            if (bean.getStatus() != StockExchangeBean.STATUS4 ) {
	                request.setAttribute("tip", "该操作已经完成，不能再修改！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	            service.getDbOp().startTransaction();
	            if (!service.deleteStockExchange("id = " + exchangeId)) {
	            	service.getDbOp().rollbackTransaction();
	            	request.setAttribute("tip", "删除调拨单失败！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	            
	            if (!service.deleteStockExchangeProduct("stock_exchange_id = " + exchangeId)) {
	            	service.getDbOp().rollbackTransaction();
	            	request.setAttribute("tip", "删除调拨单商品失败！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	            
	            if (!service.deleteStockExchangeProductCargo("stock_exchange_id = " + exchangeId)) {
	            	service.getDbOp().rollbackTransaction();
	            	request.setAttribute("tip", "删除调拨单商品货位信息失败！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	              
	            if (!afStockService.getDbOp().executeUpdate("update after_sale_detect_product asdp,after_sale_stock_exchange_product assep set asdp.lock_status=" + AfterSaleDetectProductBean.LOCK_STATUS0 + " where asdp.id=assep.after_sale_detect_product_id and assep.stock_exchange_id=" + exchangeId)) {
	            	service.getDbOp().rollbackTransaction();
	            	request.setAttribute("tip", "更新处理单状态失败！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	            
	            if (!afStockService.deleteAfterSaleStockExchangeProduct("stock_exchange_id = " + exchangeId)) {
	            	service.getDbOp().rollbackTransaction();
	            	request.setAttribute("tip", "删除调拨单售后关系信息失败！");
	                request.setAttribute("result", "failure");
	                return s;
	            }
	            
	            //log记录
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setAdminId(user.getId());
	            log.setAdminName(user.getUsername());
	            log.setLogId(bean.getId());
	            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
	            log.setOperDatetime(DateUtil.getNow());
	            log.setRemark("删除商品调配操作：" + bean.getName());
	            log.setType(StockAdminHistoryBean.DELETE);
	            if(!stockService.addStockAdminHistory(log))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return s;
	            }
	            if(!stockService.updateStockAdminHistory("deleted = 1", "log_type = " + StockAdminHistoryBean.STOCK_EXCHANGE + " and log_id = " + bean.getId()))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return s;
	            }
	            service.getDbOp().commitTransaction();
	        } catch (Exception e) {
	        	System.out.print(DateUtil.getNow());e.printStackTrace();
	        	service.getDbOp().rollbackTransaction();
			} finally {
				dbOp.release();
	        }
    	}
    	return s;
    }
	
	/**
	 * 售后处理单查询
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/searchDetectProduct")
	@ResponseBody
	public EasyuiDataGridJson searchDetectProduct(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			//sql获取
			Map<String,String> sqlMap = this.getSearchDetectProductSql(request, areaId);
			if(sqlMap==null){
				return datagrid;
			}
			
			//查询总数
			String countSql = sqlMap.get("countSql");
			ResultSet rs2 = afStockService.getDbOp().executeQuery(countSql);
			if (rs2.next()) {
				datagrid.setTotal((long)rs2.getInt(1));
			}
			rs2.close();
			//分页查询
			String query = DbOperation.getPagingQuery(sqlMap.get("querySql"), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows());
			datagrid.setRows( this.getSearchDetectProductRS(request, afStockService, query));
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
    
	/**
	 * 售后处理单导出
	 */
	@RequestMapping("/searchDetectProductExport")
	public String searchDetectProductExport(HttpServletRequest request, HttpServletResponse response, String areaId) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3073)) {
			request.setAttribute("msg", "您没有“查询售后处理单导出功能”权限！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}		
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			//sql获取
			Map<String,String> sqlMap = this.getSearchDetectProductSql(request, areaId);
			//导出
			if(sqlMap==null){
				return null;
			}else{
				this.exportSearchDetectProductData(response, this.getSearchDetectProductRS(request, afStockService, sqlMap.get("querySql")));
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return null;
	}

	
	/**
	 * 售后处理单查询sql
	 */
	private Map<String,String> getSearchDetectProductSql(HttpServletRequest request, String areaId){
		Map<String,String> map = new HashMap<String,String>();
		
		String afterSaleDetectProductCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("afterSaleDetectProductCode")));
		String[] afterSaleDetectProductCodes = afterSaleDetectProductCode.split("\n");
		String afterSaleOrderCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("afterSaleOrderCode")));
		String[] afterSaleOrderCodes = afterSaleOrderCode.split("\n");
		String orderCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("orderCode")));
		String[] orderCodes = orderCode.split("\n");
		String productCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("productCode")));
		String detectProductUserName = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("detectProductUserName")));
		int nextStep = StringUtil.toInt(StringUtil.convertNull(request.getParameter("nextStep")));
		int parentId1 = StringUtil.toInt(StringUtil.convertNull(request.getParameter("parentId1")));
		int detectProductStatus = StringUtil.toInt(StringUtil.convertNull(request.getParameter("detectProductStatus")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String phone = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("phone")));
		String orderConfirmStartTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("orderConfirmStartTime")));
		String orderConfirmEndTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("orderConfirmEndTime")));
		String content = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("content")));
		
		//空条件返回空置
		if (afterSaleDetectProductCode.trim().length() == 0 && afterSaleOrderCode.trim().length() == 0 && orderCode.trim().length() == 0 
				&& "".equals(productCode) && "".equals(detectProductUserName) && detectProductStatus == -1 && startTime.trim().equals("")
				&& endTime.trim().equals("") && phone.trim().equals("") && orderConfirmStartTime.trim().equals("") && orderConfirmEndTime.trim().equals("") 
				&& parentId1 == -1 && content.trim().equals("") && nextStep == -1) {
			return null;
		}
		
		StringBuffer buff = new StringBuffer();
		if (afterSaleDetectProductCode.trim().length() != 0 ) {
			buff.append(" and (");
			for(int i = 0 ; i < afterSaleDetectProductCodes.length ;i++ ){
				if(afterSaleDetectProductCodes[i].trim().length()>0){
					buff.append("asdp.code='");
					buff.append(afterSaleDetectProductCodes[i].trim().replaceAll("\n", ""));
					buff.append("' || ");
				}
			}
			buff.replace(buff.length()-3, buff.length(), "");
			buff.append(")");
		}
		if (afterSaleOrderCode.trim().length() != 0 ) {
			buff.append(" and (");
			for(int i = 0 ; i < afterSaleOrderCodes.length ;i++ ){
				if(afterSaleOrderCodes[i].trim().length()>0){
					buff.append("aso.after_sale_order_code='");
					buff.append(afterSaleOrderCodes[i].trim().replaceAll("\n", ""));
					buff.append("' || ");
				}
			}
			buff.replace(buff.length()-3, buff.length(), "");
			buff.append(")");
		}
		if (orderCode.trim().length() != 0 ) {
			buff.append(" and (");
			for(int i = 0 ; i < orderCodes.length ;i++ ){
				if(orderCodes[i].trim().length()>0){
					buff.append("aso.order_code='");
					buff.append(orderCodes[i].trim().replaceAll("\n", ""));
					buff.append("' || ");
				}
			}
			buff.replace(buff.length()-3, buff.length(), "");
			buff.append(")");
		}
		if(!"".equals(productCode)) {
			buff.append(" and p.code = '" + StringUtil.toSql(productCode) +"'");
		}
		if(!"".equals(detectProductUserName)) {
			buff.append(" and asdp.create_user_name = '" + StringUtil.toSql(detectProductUserName) +"'");
		}
		if(detectProductStatus != -1) {
			buff.append(" and asdp.status = " + detectProductStatus);
		}
		if(!"".equals(StringUtil.checkNull(areaId))){
			buff.append(" and asdp.area_id = " + areaId);
		}
		if (startTime != null && !startTime.trim().equals("")) {
			buff.append(" and asdp.create_datetime>='" + startTime + " 00:00:00'");
		}
		if (endTime != null && !endTime.trim().equals("")) {
			buff.append(" and asdp.create_datetime<='" + endTime + " 23:59:59'");
		}
		if (!phone.trim().equals("")) {
			buff.append(" and aso.customer_phone='" + phone + "'");
		}
		if (orderConfirmStartTime != null && !orderConfirmStartTime.trim().equals("")) {
			buff.append(" and aso.order_confirm_time>='" + orderConfirmStartTime + " 00:00:00'");
		}
		if (orderConfirmEndTime != null && !orderConfirmEndTime.trim().equals("")) {
			buff.append(" and aso.order_confirm_time<='" + orderConfirmEndTime + " 23:59:59'");
		}
		
		if (parentId1 != -1) {
			buff.append(" and p.parent_id1=" + parentId1);
		}
		
		if (!content.trim().equals("")) {
			buff.append(" and aswrd.content='" + content + "'");
		}
		switch(nextStep) {
		case 1:
			buff.append(" and asdp.status=" + AfterSaleDetectProductBean.STATUS1);
			break;
		case 2:
			buff.append(" and asbp.status=" + AfterSaleBackSupplierProduct.STATUS3);
			break;
		case 3:
			buff.append(" and assp.status=2");
			break;
		case 4:
			buff.append(" and ast.status=0");
			break;
		case 5:
			buff.append(" and asbup.package_id=0");
			break;
		}
		String where = " from after_sale_detect_product asdp " 
					+ " left join after_sale_back_supplier_product  asbp on asdp.id=asbp.after_sale_detect_product_id and asbp.status=" + AfterSaleBackSupplierProduct.STATUS3
					+ " left join after_sale_seal_product assp on asdp.id=assp.after_sale_detect_product_id and assp.status=2 " 
					+ " left join after_sale_stockin ast on asdp.id=ast.after_sale_detect_product_id and ast.status=0 " 
					+ " left join after_sale_back_user_product asbup on asbup.after_sale_detect_product_id=asdp.id and asbup.package_id=0 " 
					+ "LEFT JOIN after_sale_warehource_product_records aswpr ON aswpr.id = asdp.id "
					+ "LEFT JOIN after_sale_order aso ON asdp.after_sale_order_id = aso.id "
					+ "LEFT JOIN product p ON asdp.product_id = p.id "
					+ "LEFT JOIN catalog c ON p.parent_id1 = c.id AND c.parent_id = 0 "
					+ "LEFT JOIN after_sale_warehource_record_detail aswrd ON aswpr.id = aswrd.after_sale_warehource_record_id AND aswrd.after_sale_detect_type_id =" + AfterSaleDetectTypeBean.HANDLE				
					+ " where 1=1 " + buff.toString() + " order by asdp.create_datetime desc";
		//查询sql
		String querySql ="select asdp.code afterSaleDetectProductCode,aso.after_sale_order_code afterSaleOrderCode,aso.order_code orderCode,p.code productCode,p.name productName," +
				"aso.problem_description,aso.customer_name,aso.customer_phone,asdp.create_user_name,asdp.create_datetime,aso.order_confirm_time,aswrd.content,aso.status asoStatus,asdp.status asdpStatus,asdp.cargo_whole_code,c.name,asdp.id " 
				+ ",CASE  WHEN asbp.status is NULL THEN -1 ELSE   asbp.status END as abspStatus " 
				+ ",CASE  WHEN assp.status is NULL THEN -1 ELSE   assp.status END as asspStatus " 
				+ ",CASE  WHEN ast.STATUS is NULL THEN -1 ELSE   ast.STATUS END as astStatus " 
				+ ",CASE  WHEN asbup.package_id is NULL THEN -1 ELSE  asbup.package_id END as packageId "  + where;
				
		//查询总数sql
		String countSql = "select count(asdp.id) counts " + where;
		map.put("querySql", querySql);
		map.put("countSql", countSql);
		return map;
	}
	
	/**
	 * 售后处理单查询数据
	 * @throws SQLException 
	 */
	private List<Map<String, String>> getSearchDetectProductRS(HttpServletRequest request,AfStockService afStockService,String sql) throws SQLException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		int nextStep = StringUtil.toInt(StringUtil.convertNull(request.getParameter("nextStep")));
		String nextStepString = "";
		String nextStepString2 = "";
		switch(nextStep) {
		case 1:
			nextStepString2 = "待再次检测";
			break;
		case 2:
			nextStepString2 = "等待返厂";
			break;
		case 3:
			nextStepString2 = "待解封";
			break;
		case 4:
			nextStepString2 = "入售后库";
			break;
		case 5:
			nextStepString2 = "寄回用户";
			break;
		}
		
		ResultSet rs = afStockService.getDbOp().executeQuery(sql);
		Map<String, String> map = null;
		while (rs.next()) {
			map = new HashMap<String, String>();
			map.put("afterSaleDetectProductCode", rs.getString(1));
			map.put("afterSaleOrderCode", rs.getString(2));
			map.put("orderCode", rs.getString(3));
			map.put("productCode", rs.getString(4));
			map.put("productName", rs.getString(5));
			map.put("problemDescription", rs.getString(6));
			map.put("customerName", rs.getString(7));
			if(rs.getString(8) != null){
				map.put("customerPhone",StringUtil.concealPhone(rs.getString(8)));
			}else {
				map.put("customerPhone", rs.getString(8));
			}
			map.put("createUserName", rs.getString(9));
			map.put("createDatetime", rs.getString(10));
			map.put("orderConfirmTime", rs.getString(11));
			map.put("content", rs.getString(12));
			map.put("asoStatusName", AfterSaleOrderBean.STATUS_MAP.get(rs.getInt(13)));
			map.put("asdpStatusName", AfterSaleDetectProductBean.statusMap.get(rs.getInt(14)));
			map.put("cargoWholeCode", rs.getString(15));
			map.put("parentId1Name",rs.getString(16));
			map.put("id",rs.getInt(17) + "");
			if (nextStepString.equals("")) {
				int asdpStatus = rs.getInt("asdpStatus");
				int	abspStatus = rs.getInt("abspStatus");
				int	asspStatus = rs.getInt("asspStatus");
				int	astStatus = rs.getInt("astStatus");
				int	packageId = rs.getInt("packageId");
				if(asdpStatus==AfterSaleDetectProductBean.STATUS23 || asdpStatus==AfterSaleDetectProductBean.STATUS24){
					//如果是报溢或报溢已完成，售后单状态、一检人、一检时间为空
					map.put("asoStatusName", "");
					map.put("createUserName", "");
					map.put("createDatetime", "");
				}
				if (asdpStatus == AfterSaleDetectProductBean.STATUS1) {
					//待再次检测
					nextStepString = "待再次检测";
				} else if (abspStatus == AfterSaleBackSupplierProduct.STATUS3) {
					//等待返厂
					nextStepString = "等待返厂";
				} else if (asspStatus == 2) {
					//待解封
					nextStepString = "待解封";
				} else if (astStatus == 0) {
					//入售后库
					nextStepString = "入售后库";
				} else if (packageId == 0) {
					//寄回用户
					nextStepString = "寄回用户";
				} else {
					nextStepString = "——";
				}
			}
			map.put("nextStep", nextStepString2.equals("") ? nextStepString : nextStepString2);
			nextStepString = "";
			list.add(map);
		}
		rs.close();		
		return list;
	}
	
	/**
	 * 导出售后处理单数据
	 */
	private void exportSearchDetectProductData(HttpServletResponse response,List<Map<String,String>> list){
		
		ExportExcel excel = new ExportExcel(1);
		//设置表头
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		header.add("序号");
		header.add("售后处理单号");
		header.add("售后单号");
		header.add("订单编号");
		header.add("商品编号");
		header.add("小店名称");
		header.add("下一步操作");
		header.add("商品一级分类");
		header.add("客户问题描述");
		header.add("姓名");
		header.add("联系电话");
		header.add("一检人");
		header.add("一检时间");
		header.add("订单签收时间");
		header.add("处理建议");
		header.add("售后单状态");
		header.add("售后处理单状态");
		header.add("货位号");
		headers.add(header);
		//设置body
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		if(list!=null&&list.size()>0){
			int count=0;
			for(Map<String,String> map:list){
				//每行数据
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(String.valueOf(++count));
				temp.add(map.get("afterSaleDetectProductCode"));
				temp.add(map.get("afterSaleOrderCode"));
				temp.add(map.get("orderCode"));
				temp.add(map.get("productCode"));
				temp.add(map.get("productName"));
				temp.add(map.get("nextStep"));
				temp.add(map.get("parentId1Name"));
				temp.add(map.get("problemDescription"));
				temp.add(map.get("customerName"));
				temp.add(map.get("customerPhone"));
				temp.add(map.get("createUserName"));
				if(map.get("createDatetime")!=null&&!"".equals(map.get("createDatetime"))){
					temp.add(DateUtil.formatTime(DateUtil.parseDate(map.get("createDatetime"), "yyyy-MM-dd HH:mm:ss")));
				}else{
					temp.add("");
				}
				if(map.get("orderConfirmTime")!=null&&!"".equals(map.get("orderConfirmTime"))){
					temp.add(DateUtil.formatDate(DateUtil.parseDate(map.get("orderConfirmTime"), "yyyy-MM-dd")));
				}else{
					temp.add("");
				}
				temp.add(map.get("content"));
				temp.add(map.get("asoStatusName"));
				temp.add(map.get("asdpStatusName"));
				temp.add(map.get("cargoWholeCode"));
				bodies.add(temp);
			}
		}else{
			ArrayList<String> temp = new ArrayList<String>();
			for(int i=0;i<header.size();i++){
				temp.add("没有查询到匹配的售后处理单！");
			}
			bodies.add(temp);
			
			/* 允许合并列,下标从0开始，即0代表第一列 */
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			for(int i=0;i<header.size();i++){
				mayMergeColumn.add(i);
			}
			excel.setMayMergeColumn(mayMergeColumn);

			/* 允许合并行,下标从0开始，即0代表第一行 */
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			mayMergeRow.add(1);
			excel.setMayMergeRow(mayMergeRow);

			/*
			 * 该行为固定写法 （设置该值为导出excel最大列宽 ,下标从1开始）
			 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
			 */
			excel.setColMergeCount(header.size());
			
		}
		// 调用填充表头方法
		excel.buildListHeader(headers);
		// 调用填充数据区方法
		excel.buildListBody(bodies);
		// 文件输出
		try {
			excel.exportToExcel("售后处理单查询结果_"+DateUtil.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd-HH-mm-ss"), response, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	/**
	 *  获取返厂清单
	 * @param request
	 * @param response
	 * @param easyuiPage
	 * @param startTime
	 * @param endTime
	 * @param senderName
	 * @param supplierId
	 * @return
	 * @author 李宁
	* @date 2014-6-4
	 */
	@RequestMapping("/getBackSupplierProductList")
	@ResponseBody
	public EasyuiDataGridJson getBackSupplierProductList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,String startTime,
			String endTime,String senderName,String supplierId){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			StringBuffer condition = new StringBuffer("1=1");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				condition.append(" and left(send_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					condition.append(" and left(send_datetime,10) = '" + StringUtil.toSql(startTime) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					condition.append(" and left(send_datetime,10) = '" + StringUtil.toSql(endTime) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(senderName))){
				condition.append(" and sender_name='").append(senderName).append("'");
			}
			if(!"".equals(StringUtil.checkNull(supplierId))){
				condition.append(" and supplier_id=").append(supplierId);
			}

			List<AfterSaleBackSupplierProduct> productList = afStockService.getAfterSaleBackSupplierProductList(condition.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), "send_datetime desc");
			Map<String,List<AfterSaleBackSupplierProduct>> maps = new LinkedHashMap<String,List<AfterSaleBackSupplierProduct>>();
			if(productList!=null && productList.size()>0){
				for(int i=0;i<productList.size();i++){
					AfterSaleBackSupplierProduct bean = productList.get(i);
					String packageCode = StringUtil.convertNull(bean.getPackageCode());
					if(packageCode.length()>0){
						if(maps.containsKey(packageCode)){
							maps.get(packageCode).add(bean);
						}else{
							List<AfterSaleBackSupplierProduct> list = new ArrayList<AfterSaleBackSupplierProduct>();
							list.add(bean);
							maps.put(packageCode, list);
						}
					}
				}
			}
			List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
			if(maps.size()>0){
				for(String key : maps.keySet()){
					List<AfterSaleBackSupplierProduct> value = maps.get(key);
					Map<String,String> resultMap = new LinkedHashMap<String,String>();
					resultMap.put("packageCode", key);
					resultMap.put("sendTime", value.get(0).getSendDatetime());
					AfterSaleBackSupplier supplier = afStockService.getAfterSaleBackSupplier("id="+value.get(0).getSupplierId());
					if(supplier!=null){
						resultMap.put("supplierName", supplier.getName());
					}
					resultMap.put("sendName", value.get(0).getSenderName());
					resultMap.put("productCount", value.size()+"");
					resultList.add(resultMap);
				}
			}
			datagrid.setTotal((long)resultList.size());
			datagrid.setRows(resultList);
			
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return datagrid;
	}
	/**
	 * 打印维修返回用户商品的发货清单
	 * @param request
	 * @param response
	 * @author 李宁
	* @date 2014-5-26
	 */
	@RequestMapping("/printShippingList")
	public String printShippingList(HttpServletRequest request, HttpServletResponse response){
		String result = "/admin/afStock/repairProductListPrint";
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService saleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
		WareService wareService = new WareService(dbOp);
		try{
			String packageCode = StringUtil.checkNull(request.getParameter("packageCode"));
			if(packageCode.length()>0){
				//包裹信息
				AfterSaleBackUserPackage backPackage = afStockService.getAfterSaleBackUserPackage("package_code='" + packageCode + "'");
				int deliverId = backPackage.getDeliverId();
				if(SysDict.deliverMap.containsKey(deliverId)){
					backPackage.setDeliverName(SysDict.deliverMap.get(deliverId));
				}
				request.setAttribute("backPackage", backPackage);
				//寄回商品列表
				List<AfterSaleBackUserProduct> list = afStockService.getAfterSaleBackUserProductList("type=1 and package_id=" + backPackage.getId(), -1, -1, null);
				List<AfterSaleShippingListBean> shippingList = new ArrayList<AfterSaleShippingListBean>();
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						AfterSaleBackUserProduct backProduct = list.get(i);
						AfterSaleShippingListBean shippingBean = new AfterSaleShippingListBean();
						voProduct product = afStockService.getProductById(backProduct.getProductId(), dbOp);
						shippingBean.setProductName(product.getName());
						shippingBean.setProductCode(product.getCode());
						
						AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("id=" + backProduct.getAfterSaleDetectProductId());
						shippingBean.setImei(detectProductBean.getIMEI());
						if(i==0){
							AfterSaleOrderBean afterSaleOrderBean = saleService.getAfterSaleOrder("id=" + detectProductBean.getAfterSaleOrderId());
							request.setAttribute("orderCode", afterSaleOrderBean.getOrderCode());
							request.setAttribute("customerName", afterSaleOrderBean.getCustomerName());
							request.setAttribute("afterSaleOrderCode", afterSaleOrderBean.getAfterSaleOrderCode());
							voOrder order = wareService.getOrder(afterSaleOrderBean.getOrderId());
							if(order!=null){
								if(order.isDaqOrder()){
									request.setAttribute("isDaqOrder", true);
								}
							}
						}
						
						ResultSet rs = dbOp.executeQuery("select content from after_sale_detect_log where "
								 + "after_sale_detect_product_id=" + backProduct.getAfterSaleDetectProductId()
								+ " and after_sale_detect_type_id=4 order by create_datetime desc limit 1");//故障描述
						if(rs!=null && rs.next()){
							shippingBean.setFaultDescription(rs.getString(1));
						}
						rs.close();
						rs = dbOp.executeQuery("select content from after_sale_detect_log where "
								 + "after_sale_detect_product_id=" + backProduct.getAfterSaleDetectProductId()
								+ " and after_sale_detect_type_id=12 order by create_datetime desc limit 1");//维修报价
						if(rs!=null && rs.next()){
							shippingBean.setRepairCost(rs.getString(1));
						}
						String fittingNames = afStockService.getAfterSaleFittings("p.id=" + backProduct.getProductId());
						if(StringUtil.checkNull(fittingNames).length()>0){
							shippingBean.setFittings(fittingNames.replaceAll(",","<br/>"));
						}
						shippingList.add(shippingBean);
					}
				}
				request.setAttribute("shippingList", shippingList);
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return result;
	}
	
	/**
	 * 获取主商品的配件信息
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @author 李宁
	* @date 2014-5-28
	 */
	@RequestMapping("/getAfterSaleFittings")
	@ResponseBody
	public EasyuiDataGridJson getAfterSaleFittings(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			int id = StringUtil.StringToId(request.getParameter("id"));
			if(id>0){
				AfterSaleDetectProductBean bean = afStockService.getAfterSaleDetectProduct("id=" + id);
				List<Map<String,String>> list = afStockService.getAfterSaleFittingListInner("p.id=" + bean.getProductId(), -1, -1, null);
				datagrid.setFooter(list);
			}
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return datagrid;
	}
	
	/**
	 * 寄出给用户的包裹数据导入
	 * @param request
	 * @param response
	 * @return
	 * @author 李宁
	 * @throws IOException 
	 * @throws ServletException 
	* @date 2014-6-10
	 */
	@RequestMapping("/importPackage")
	public void importPackage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.setContentType("text/html;charset=UTF-8");
		StringBuilder result = new StringBuilder();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return;
		}
		String content = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("content")));
		if(content==null || content.equals("")){
			result.append("导入数据为空，请输入后导入!");
			response.getWriter().write(result.toString());
			return;
		}
		
		synchronized (lock) {
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);
			AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, db);
			String packageInfo = null;//售后处理单号
			BufferedReader br = new BufferedReader(new StringReader(content));
			try{
				while ((packageInfo = br.readLine()) != null) {
					if(packageInfo.trim().length()>0){
						String[] info = packageInfo.split("\\t",10);
						for(String str : info){
							str.replaceAll("\\r\\n", "");
						}
						String detectCode = info[8];
						if(detectCode!=null && detectCode.trim().length()>0){
							String query = "select aspack.create_datetime,aspack.customer_name,aspack.price,aspack.deliver_id," +
									"aspack.user_name,aspack.user_phone,aspack.user_address,asbup.type,aspack.id " +
									"from after_sale_back_user_package aspack,after_sale_back_user_product asbup,after_sale_detect_product asdp,after_sale_order aso " +
									"where asbup.after_sale_detect_product_id=asdp.id and asbup.package_id=aspack.id and asdp.after_sale_order_id=aso.id" +
									" and asdp.code='" + detectCode + "'";
							ResultSet rs = service.getDbOp().executeQuery(query);
							AfterSaleBackUserPackage asPackBean = null;
							if(rs!=null && rs.next()){
								asPackBean = new AfterSaleBackUserPackage();
								asPackBean.setCreateDatetime(rs.getString(1));
								asPackBean.setCustomerName(rs.getString(2));
								asPackBean.setPrice(rs.getFloat(3));
								asPackBean.setDeliverId(rs.getInt(4));
								asPackBean.setDeliverName(SysDict.deliverMap.get(rs.getInt(4)));
								asPackBean.setUserName(rs.getString(5));
								asPackBean.setUserPhone(rs.getString(6));
								asPackBean.setUserAddress(rs.getString(7));
								asPackBean.setTypeName(AfterSaleBackUserProduct.typeMap.get(rs.getInt(8)));
								asPackBean.setId(rs.getInt(9));
							}
							String sql = "select after_sale_warehource_package_id from after_sale_warehource_product_records where code='" + detectCode + "'";
							ResultSet resultSet = service.getDbOp().executeQuery(sql);
							int packageId = 0;
							if(resultSet!=null && resultSet.next()){
								packageId = resultSet.getInt(1);
							}
							if(asPackBean==null){
								result.append(detectCode).append("没有对应的包裹信息!<br/>");
								continue;
							}
							if(!asPackBean.getCreateDatetime().substring(0, 19).equals(info[0])){
								result.append(detectCode).append("对应的包裹信息的邮寄时间和数据库不同!<br/>");
								continue;
							}
							if(!asPackBean.getCustomerName().equalsIgnoreCase(info[1])){
								result.append(detectCode).append("对应的包裹信息的客户姓名和数据库不同!<br/>");
								continue;
							}
							if(!(asPackBean.getPrice()==Float.parseFloat(info[3]))){
								result.append(detectCode).append("对应的包裹信息的运费金额和数据库不同!<br/>");
								continue;
							}
							if(!asPackBean.getDeliverName().equals(info[4])){
								result.append(detectCode).append("对应的包裹信息的快递公司和数据库不同!<br/>");
								continue;
							}
							if(!asPackBean.getUserName().equalsIgnoreCase(info[5])){
								result.append(detectCode).append("对应的包裹信息的发件人和数据库不同!<br/>");
								continue;
							}
							if(!asPackBean.getUserPhone().equals(info[6])){
								result.append(detectCode).append("对应的包裹信息的客户电话和数据库不同!<br/>");
								continue;
							}
							if(!asPackBean.getUserAddress().equals(info[7])){
								result.append(detectCode).append("对应的包裹信息的收件地址和数据库不同!<br/>");
								continue;
							}
							if(!asPackBean.getTypeName().equals(info[9])){
								result.append(detectCode).append("对应的包裹信息的寄出类型和数据库不同!<br/>");
								continue;
							}
							if(info[2].trim().length()<=0){
								result.append(detectCode).append("对应的包裹信息中没有包裹单号!<br/>");
								continue;
							}
							if(packageId<=0){
								result.append(detectCode).append("销售售后库对应的包裹列表中没有相应的包裹信息!<br/>");
								continue;
							}
							service.getDbOp().startTransaction();
							if(!service.updateAfterSaleBackUserPackage("package_code='" + info[2] + "'", "id=" + asPackBean.getId())){
								result.append(detectCode).append("对应的包裹单号更新失败!<br/>");
								service.getDbOp().rollbackTransaction();
								continue;
							}
							String updateSql = "update after_sale_warehource_package_list set package_code='" + info[2] + "' where id=" + packageId;
							if(!service.getDbOp().executeUpdate(updateSql)){
								result.append(detectCode).append("销售售后库中对应的包裹单号更新失败!<br/>");
								service.getDbOp().rollbackTransaction();
								continue;
							}
							service.getDbOp().commitTransaction();
						}
					}
				}
				if(result.length()==0){
					result.append("导入的包裹单号全部更新成功!");
				}
				response.getWriter().write(result.toString());
			}catch (Exception e) {
				try {
					if(service.getDbOp().getConn().getAutoCommit()){
						service.getDbOp().rollbackTransaction();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.out.print(DateUtil.getNow());e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
	}
	
	
	/**
	 * 导出客户库商品列表
	 * @author 李宁
	* @date 2014-6-23
	 */
	@RequestMapping("/excelCustomerProduct")
	@ResponseBody
	public void excelCustomerProduct(HttpServletRequest request,HttpServletResponse response,
			String startTime,String endTime,String productCode,String afterSaleCode,String afterSaleDetectCode,String areaId,String parentId1,
			String parentId2,String parentId3,String customerPhone,String saleType,String stockType){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" asdp.status in (0,1,2,3,4,5,6,7,10,12,13,14,15,16,17,18) ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(asdp.create_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(startTime) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(endTime) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))){
				buff.append(" and asdp.after_sale_order_code='" + StringUtil.toSql(afterSaleCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				buff.append(" and asdp.code='" + StringUtil.toSql(afterSaleDetectCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1 in (" + StringUtil.toSql(parentId1) + ") ");
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2 in (" + StringUtil.toSql(parentId2) + ") ");
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3 in (" + StringUtil.toSql(parentId3) + ") ");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(stockType))){
				buff.append(" and ci.stock_type=" + StringUtil.toSql(stockType));
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=").append(StringUtil.toSql(areaId));
			}
			if(!"".equals(StringUtil.checkNull(customerPhone))){
				buff.append(" and aso.customer_phone='").append(StringUtil.toSql(customerPhone)).append("'");
			}
			if(!"".equals(StringUtil.checkNull(saleType))){
				buff.append(" and psp.type=").append(StringUtil.toSql(saleType));
			}

			List<Map<String,String>> rows = afStockService.getCustomerAfterSaleProductList(buff.toString(), -1, -1, " asdp.create_datetime desc");
			if(rows.size()>0){
				for(int i=0;i<rows.size();i++){
					Map<String,String> row = rows.get(i);
					int afterSaleProductId = StringUtil.parstInt(row.get("id"));
					//查询寄回配件信息
					String fitting = afStockService.getAfterSaleFitting(afterSaleProductId, 0);
					fitting = fitting.replace("\n", ",");
					row.put("fitting", fitting);
					
					//2015-03-17 添加故障描述
					String content = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, afterSaleProductId);
					row.put("content", content);
				}
			}

			ArrayList<String> header = new ArrayList<String>();
			header.add("商品编号");
			header.add("小店名称");
			header.add("原名称");
			header.add("货位编号");
			header.add("故障描述");
			header.add("售后处理单号");
			header.add("售后处理状态");
			header.add("配件名称");
			header.add("售后单号");
			header.add("入库日期");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			if(rows!=null && rows.size()>0){
				for(Map<String, String> map : rows){
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(map.get("productCode"));
					printList.add(map.get("productName"));
					printList.add(map.get("productOriName"));
					printList.add(map.get("wholeCode"));
					printList.add(map.get("content"));
					printList.add(map.get("afterSaleDetectCode"));
					printList.add(map.get("statusName"));
					printList.add(map.get("fitting"));
					printList.add(map.get("afterSaleCode"));
					printList.add(map.get("createDatetime"));
					bodies.add(printList);
				}
			}
			
			importExcel(header, bodies,response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * @return 导出售后商品列表
	 * @author 李宁
	 */
	@RequestMapping("/excelStockProduct")
	@ResponseBody
	public void excelStockProduct(HttpServletRequest request,HttpServletResponse response,
			String stockType,String startTime,String endTime,String afterSaleCode,String productName,
			String afterSaleDetectCode,String parentId1,String parentId2,String parentId3,String productCode, String afterSaleStockinType, 
			String mainProductStatus, String sellType,String areaId) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" asdp.status in (0,1,2,3,4,5,6,7,10,12,13,14,15,16,17,18,19,24) and asdp.lock_status!=2 ");
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and left(asdp.create_datetime,10) BETWEEN '" + StringUtil.toSql(startTime) + "' and '" + StringUtil.toSql(endTime) + "'");
			}else{
				if(!"".equals(StringUtil.checkNull(startTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(startTime) + "'");
				}
				if(!"".equals(StringUtil.checkNull(endTime))){
					buff.append(" and left(asdp.create_datetime,10) = '" + StringUtil.toSql(endTime) + "'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))){
				buff.append(" and asdp.after_sale_order_code='" + StringUtil.toSql(afterSaleCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				buff.append(" and asdp.code='" + StringUtil.toSql(afterSaleDetectCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=" + StringUtil.toSql(areaId));
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(stockType))){
				buff.append(" and ci.stock_type=" + StringUtil.toSql(stockType));
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.name='" + StringUtil.toSql(productName) + "'");
			}
			if(StringUtil.toInt(afterSaleStockinType) != -1){
				buff.append(" and ass.type=" + afterSaleStockinType);
			}
			if (StringUtil.toInt(sellType) != -1) {
				buff.append(" and psp.type=" + sellType);
			}
			if(!"".equals(StringUtil.checkNull(mainProductStatus))){
				buff.append(" and asdl.content='" + StringUtil.toSql(mainProductStatus) + "'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				buff.append(" and asdp.area_id=").append(StringUtil.toSql(areaId));
			}
			List<Map<String,String>> rows = afStockService.getAfterSaleProductList(buff.toString(), -1, -1, " asdp.create_datetime desc");
			if(rows!=null && rows.size()>0){
				for(int i=0;i<rows.size();i++){
					Map<String,String> row = rows.get(i);
					int detectId = StringUtil.StringToId(row.get("id"));
					if(detectId>0){
						row.put("questionDescription", afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.QUESTION_DESCRIPTION,detectId));
						row.put("faultDescription",afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION,detectId));
					}
				}
			}
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品编号");
			header.add("小店名称");
			header.add("原名称");
			header.add("商品质量");
			header.add("问题分类");
			header.add("故障描述");
			header.add("销售属性");
			header.add("货位编号");
			header.add("售后处理单号");
			header.add("售后单号");
			header.add("入库日期");
			header.add("入库类型");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			if(rows!=null && rows.size()>0){
				for(Map<String, String> map : rows){
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(map.get("productCode"));
					printList.add(map.get("productName"));
					printList.add(map.get("productOriName"));
					printList.add(map.get("mainProductStatus"));
					printList.add(map.get("questionDescription"));
					printList.add(map.get("faultDescription"));
					printList.add(map.get("sellTypeName"));
					printList.add(map.get("wholeCode"));
					printList.add(map.get("afterSaleDetectCode"));
					printList.add(map.get("afterSaleCode"));
					printList.add(map.get("createDatetime"));
					printList.add(map.get("afterSaleStockinTypeName"));
					bodies.add(printList);
				}
			}
			
			importExcel(header, bodies,response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 *导出以换代修商品列表
	 * @author lining
	 */
	@RequestMapping("/excelChangeRepairProduct")
	@ResponseBody
	public void excelChangeRepairProduct(HttpServletRequest request,HttpServletResponse response,
			String afterSaleDetectCode,String productName,String imeiCode,String status,String productCode,String afterSaleCode,String supplier){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" 1=1 ");
			buff.append(" and ass.type=" + AfterSaleStockin.TYPE3);
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))){
				buff.append(" and asdp.code='" + StringUtil.toSql(afterSaleDetectCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))){
				buff.append(" and asdp.after_sale_order_code='" + StringUtil.toSql(afterSaleCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productName))){
				buff.append(" and p.`name`='" + StringUtil.toSql(productName) + "'");
			}
			if(!"".equals(StringUtil.checkNull(imeiCode))){
				buff.append(" and asdp.IMEI='" + StringUtil.toSql(imeiCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(status))){
				buff.append(" and asbsp.`status`='" + StringUtil.toSql(status) + "'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code`='" + StringUtil.toSql(productCode) + "'");
			}
			if(!"".equals(StringUtil.checkNull(supplier)) && !"-1".equals(StringUtil.checkNull(supplier))){
				buff.append(" and asbs.id=" + supplier);
			}
			List<Map<String,String>> rows = afStockService.getAfterSaleChangeRepairProductList(buff.toString(), -1, -1, " create_datetime desc");
			if(rows != null && rows.size() > 0){
				for(Map<String,String> row : rows){
					
					//故障描述 2015-03-17
					int detecttypeId = StringUtil.StringToId(row.get("id"));
					String problemRmark = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, detecttypeId);
					row.put("problemRmark", problemRmark);
				}
			}
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品名称");
			header.add("型号");
			header.add("售后单号");
			header.add("售后处理单号");
			header.add("IMEI");
			header.add("故障描述");
			header.add("发货日期");
			header.add("状态");			
			header.add("寄回日期");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			if(rows!=null && rows.size()>0){
				for(Map<String, String> map : rows){
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(map.get("productName"));
					printList.add(map.get("productOriName"));
					printList.add(map.get("afterSaleCode"));
					printList.add(map.get("afterSaleDetectCode"));
					printList.add(map.get("imeiCode"));
					printList.add(map.get("problemRmark"));
					printList.add(map.get("createDatetime"));
					printList.add(map.get("statusName"));
					printList.add(map.get("returnDatetime"));
					bodies.add(printList);
				}
			}
			
			importExcel(header,bodies,response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 导出封箱列表
	 *@author 李宁
	 *@date 2014-06-23
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/excelSealList")
	@ResponseBody
	public void excelSealList(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String begin = StringUtil.dealParam(request.getParameter("startDate"));
		String end = StringUtil.dealParam(request.getParameter("endDate"));
		String afterSaleOrderCode = StringUtil.convertNull(request.getParameter("afterSaleOrderCode"));
		String afterSaleDetectCode = StringUtil.convertNull(request.getParameter("afterSaleDetectCode"));
		String afterSaleSealCode = StringUtil.convertNull(request.getParameter("afterSaleSealCode"));
		String areaId = StringUtil.checkNull(request.getParameter("areaId"));
		
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try{
			StringBuilder condition = new StringBuilder();
			if(!(StringUtil.isNull(begin) || StringUtil.isNull(end))){
				begin = begin + " 00:00:00";
				end = end + " 23:59:59";
				condition.append(" and ass.create_datetime between '").append(begin).append("' and '")
							.append(end).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleSealCode))){
				condition.append(" and ass.code='").append(afterSaleSealCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleDetectCode))){
				condition.append(" and asdp.code='").append(afterSaleDetectCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleOrderCode))){
				condition.append(" and asdp.after_sale_order_code='").append(afterSaleOrderCode).append("' ");
			}
			if(!(StringUtil.isNull(areaId))){
				condition.append(" and asdp.area_id=").append(areaId);
			}
			
			List<AfterSaleSeal> sealList = afStockService.getAfterSaleSealList(condition.toString(), "ass.id",-1, -1);
			if(sealList!=null && sealList.size()>0){
				for(int i=0;i<sealList.size();i++){
					AfterSaleSeal sealBean = sealList.get(i);
					map = new HashMap<String,String>();
					map.put("seal_id", sealBean.getId()+"");
					map.put("seal_date", sealBean.getCreateDatetime());
					map.put("seal_code", sealBean.getCode());
					map.put("seal_product_count", sealBean.getSealProductCount()+"");
					map.put("operator", sealBean.getUserName());
					lists.add(map);
				}
			}
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("封箱日期");
			header.add("封箱编号");
			header.add("封箱商品数量");
			header.add("操作人");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			
			if(lists!=null && lists.size()>0){
				for(Map<String, String> row : lists){
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(row.get("seal_date"));
					printList.add(row.get("seal_code"));
					printList.add(row.get("seal_product_count"));
					printList.add(row.get("operator"));
					bodies.add(printList);
				}
			}
			importExcel(header,bodies,response);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 生成excel表
	 * @param header
	 * @param bodies
	 * @return
	 * @author 李宁
	 * @throws IOException 
	* @date 2014-6-23
	 */
	private void importExcel(ArrayList<String> header,List<ArrayList<String>> bodies,HttpServletResponse response) throws IOException{
		ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
		excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(headers.get(0).size());
		
		excel.buildListHeader(headers);
		excel.setHeader(false);
		excel.buildListBody(bodies);
		
		String fileName = "product_" + DateUtil.getNow().substring(0, 10);
		response.reset();
		response.addHeader("Content-Disposition","attachment;filename="+ fileName+ ".xlsx");
		response.setContentType("application/msxls");
		response.setCharacterEncoding("utf-8");
		excel.getWorkbook().write(response.getOutputStream());
	}
	/**
	 * 导出待检测包裹列表
	 * @author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelDetectPackageProduct")
	@ResponseBody
	public void excelDetectPackageProduct(HttpServletRequest request, HttpServletResponse response,
			String startTime,String endTime,String packageCode,String createUserName,String orderCode,
			String afterSaleCode,String areaId) throws ServletException, IOException{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" asdp.status in (1) ");
			if(!"".equals(StringUtil.checkNull(packageCode))) {
				buff.append(" and asdp.package_code like '%" + packageCode+"%'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and asdp.area_id=" + areaId);
			}
			if(!"".equals(StringUtil.checkNull(orderCode))) {
				buff.append(" and asdp.order_code='" +orderCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleCode))) {
				buff.append(" and asdpr.after_sale_order_code='" + afterSaleCode +"'");
			}
			if(!"".equals(StringUtil.checkNull(createUserName))) {
				buff.append(" and asdp.create_user_name='" + createUserName+"'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime<='" + endTime + " 23:59:59'");
			}
			List<AfterSaleDetectPackageBean> maps = afStockService.getDetectPackAgeList(buff.toString(), -1, -1, " create_datetime desc ");
			for (AfterSaleDetectPackageBean bean : maps) {
				bean.setAfterSaleOrderCodes(afStockService.getAftersaleOrderIds(bean.getId(), dbOp));
			}
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("包裹单号");
			header.add("签收时间");
			header.add("签收人");
			header.add("售后单号");
			header.add("订单号");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleDetectPackageBean bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getPackageCode());
				printList.add(bean.getCreateDatetime());
				printList.add(bean.getCreateUserName());
				printList.add(bean.getAfterSaleOrderCodes());
				printList.add(bean.getOrderCode());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 导出待再次检测商品列表
	 * @author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelAgainDetectProduct")
	@ResponseBody
	public void excelAgainDetectProduct(HttpServletRequest request, HttpServletResponse response, 
			String afterSaleDetectCode,String productCode,String afterSaleOrderCode,String areaId) throws ServletException, IOException{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))) {
				buff.append(" and ap.code like '%" + afterSaleDetectCode +"%'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleOrderCode))) {
				buff.append(" and ap.after_sale_order_code like '%" + afterSaleOrderCode +"%'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))) {
				buff.append(" and p.code = '" + productCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id = " + areaId);
			}
			List<AfterSaleDetectProductBean> maps = afStockService.getDetectProductList(buff.toString(),-1, -1, " ap.create_datetime desc ");
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("售后处理单号");
			header.add("售后单号");
			header.add("货位号");
			header.add("小店名称");
			header.add("商品编号");
			header.add("最后操作人");
			header.add("最后操作时间");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleDetectProductBean bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getCode());
				printList.add(bean.getAfterSaleOrderCode());
				printList.add(bean.getCargoWholeCode());
				printList.add(bean.getProductName());
				printList.add(bean.getProductCode());
				printList.add(bean.getCreateUserName());
				printList.add(bean.getCreateDatetime());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 导出等待返厂商品列表
	 * @author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelSupplierProduct")
	@ResponseBody
	public void excelSupplierProduct(HttpServletRequest request, HttpServletResponse response,
			String afterSaleDetectCode,String productCode,String afterSaleOrderCode,String parentId1,String parentId2,
			String parentId3,String areaId,String status,String startTime,String endTime,
			String startTime_send,String endTime_send) throws ServletException, IOException{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			String idStr = "";
			if("".equals(StringUtil.checkNull(status))){
				status="1";
			}else {
				status="0";
			   if(!"".equals(StringUtil.checkNull(afterSaleOrderCode))) {
					buff.append(" and after_sale_order_code like '%" + afterSaleOrderCode+"%'");
				}
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))) {
				buff.append(" and code like '%" + afterSaleDetectCode+"%'");
			}
		
			if(buff.length()>2){
			    idStr = afStockService.getAfterSaleIds(buff.toString());
			    buff.delete(0, buff.length());
			    if(!"0".equals(idStr) && !"".equals(StringUtil.checkNull(idStr))){
			    	buff.append(" and absp.after_sale_detect_product_id in("+idStr+")");  
				}
			}
			if("0".equals(status)){
				if(!"".equals(StringUtil.checkNull(areaId))) {
					   buff.append(" and asdp.area_id = " + areaId);
				}
			} else {
				if(!"".equals(StringUtil.checkNull(areaId))) {
					   buff.append(" and ap.area_id = " + areaId);
				}
			}
			String content=request.getParameter("content");
			if(!"".equals(StringUtil.checkNull(content))) {
				buff.append(" and p.code = '" + content+"'");
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and return_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and return_datetime<='" + endTime + " 23:59:59'");
			}
			if (!"".equals(StringUtil.checkNull(startTime_send))) {
				buff.append(" and absp.create_datetime >= '" + startTime_send + " 00:00:00'");
			}
			if (!"".equals(StringUtil.checkNull(endTime_send))) {
				buff.append(" and absp.create_datetime <= '" + endTime_send + " 23:59:59'");
			}
			
			List<AfterSaleBackSupplierProduct> maps = afStockService.getBackSupplierProductList(buff.toString(), -1, -1, " return_datetime desc ",status);
			ArrayList<String> header = new ArrayList<String>();
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			
			if(status.equals("0")){
				header.add("售后处理单号");
				header.add("售后单号");
				header.add("货位号");
				header.add("小店名称");
				header.add("商品编号");
				header.add("状态");
				header.add("故障描述");
				header.add("最后操作人");
				header.add("最后操作时间");
				
				for (AfterSaleBackSupplierProduct bean : maps) {				
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(bean.getCode());
					printList.add(bean.getAfterSaleOrderCode());
					printList.add(bean.getCargoWholeCode());
					printList.add(bean.getShopName());
					printList.add(bean.getProductCode());
					
					//2015-03-17 添加故障描述、状态显示
					printList.add(AfterSaleBackSupplierProduct.statusMap.get(bean.getStatus()));
					String faultDescription = afStockService.getAfterSaleDetectLogContent(AfterSaleDetectTypeBean.FAULT_DESCRIPTION, bean.getAfterSaleDetectProductId());
					printList.add(faultDescription);
					
					printList.add(bean.getUserName());
					printList.add(bean.getCreateDatetime());
					bodies.add(printList);
				}
			}else if(status.equals("1")){
				header.add("商品编号");
				header.add("小店名称");
				header.add("售后处理单号");
				header.add("处理意见");
				header.add("处理单状态");
				header.add("售后单号");
				header.add("货位号");
				header.add("发货日期");
				header.add("厂商寄回时间");
				
				for (AfterSaleBackSupplierProduct bean : maps) {
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(bean.getProductCode());
					printList.add(bean.getShopName());
					printList.add(bean.getCode());
					printList.add(bean.getHandling());
					printList.add(bean.getHandlingstatus());
					printList.add(bean.getAfterSaleOrderCode());
					printList.add(bean.getCargoWholeCode());
					String createDatetime = bean.getCreateDatetime();
					if(createDatetime!=null && createDatetime.trim().length()>0){
						printList.add(createDatetime.substring(0,10));
					}
					printList.add(bean.getReturnDatetime());
					bodies.add(printList);
				}	
			}
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 查询待解封商品列表
	 *@author 李宁
	 *@date 2014-2-11 下午4:26:09
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/excelSealProduct")
	@ResponseBody
	public void excelSealProduct(HttpServletRequest request,HttpServletResponse response,String areaId) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		//查询条件
		String begin = StringUtil.dealParam(request.getParameter("startDate"));
		String end = StringUtil.dealParam(request.getParameter("endDate"));
		String afterSaleOrderCode = StringUtil.convertNull(request.getParameter("afterSaleOrderCode"));
		String afterSaleDetectCode = StringUtil.convertNull(request.getParameter("afterSaleDetectCode"));
		String afterSaleSealCode = StringUtil.convertNull(request.getParameter("afterSaleSealCode"));
		
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		try{
			StringBuilder condition = new StringBuilder();
			if(!(StringUtil.isNull(begin) || StringUtil.isNull(end))){
				begin = begin + " 00:00:00";
				end = end + " 23:59:59";
				condition.append(" and ass.create_datetime between '").append(begin).append("' and '")
							.append(end).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleSealCode))){
				condition.append(" and ass.code='").append(afterSaleSealCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleDetectCode))){
				condition.append(" and afdp.code='").append(afterSaleDetectCode).append("' ");
			}
			if(!(StringUtil.isNull(afterSaleOrderCode))){
				condition.append(" and afdp.after_sale_order_code='").append(afterSaleOrderCode).append("' ");
			}
			if(!(StringUtil.isNull(areaId))){
				condition.append(" and afdp.area_id=").append(areaId);
			}
			lists = afStockService.getAfterSaleSealProductsList(condition.toString(),-1,-1);
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品名称");
			header.add("售后单号");
			header.add("售后处理单号");
			header.add("封箱编号");
			header.add("封箱日期");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			if(lists!=null && lists.size()>0){
				for(Map<String, String> row : lists){
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(row.get("product_name"));
					printList.add(row.get("after_sale_order_code"));
					printList.add(row.get("after_sale_detect_product_code"));
					printList.add(row.get("after_sale_seal_code"));
					printList.add(row.get("seal_date"));
					bodies.add(printList);
				}
			}
			importExcel(header, bodies, response);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
	}
	
	/**
	 * 导出已检测商品上架任务列表
	 *@author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelProductUpshelf")
	@ResponseBody
	public void excelProductUpshelf(HttpServletRequest request, HttpServletResponse response,
			String startTime,String endTime,String afterSaleDetectCode,String productCode,String areaId) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))) {
				buff.append(" and ap.code like '%" + afterSaleDetectCode+"%'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and ap.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and ap.create_datetime<='" + endTime + " 23:59:59'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id = " + areaId);
			}

			if(!"".equals(StringUtil.checkNull(productCode))) {
				buff.append(" and p.code = '" + productCode+"'");
			}

			List<AfterSaleDetectUpShelfBean> maps = afStockService.getProductUpShelfList(buff.toString(), -1, -1, " ap.create_datetime desc");
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品编号");
			header.add("小店名称");
			header.add("售后单号");
			header.add("售后单状态");
			header.add("售后处理单号");
			header.add("检测时间");
			header.add("返厂状态");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleDetectUpShelfBean bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getProductCode());
				printList.add(bean.getShopName());
				printList.add(bean.getAfterSaleOrderCode());
				printList.add(bean.getAfterSaleStatus());
				printList.add(bean.getAfterSaleCode());
				printList.add(bean.getCheckTime());
				printList.add(bean.getBackSupplierStatus());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 售后库上架列表
	 * @author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelSaleUpshelf")
	@ResponseBody
	public void excelSaleUpshelf(HttpServletRequest request, HttpServletResponse response,
			String afterSaleDetectCode,String startTime,String endTime,String status,String areaId) throws ServletException, IOException{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
		
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))) {
				buff.append(" and ap.code like '%" + afterSaleDetectCode +"%'");
			}
			if (!"".equals(StringUtil.checkNull(startTime))) {
				buff.append(" and ap.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (!"".equals(StringUtil.checkNull(endTime))) {
				buff.append(" and ap.create_datetime<='" + endTime + " 23:59:59'");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id =" + areaId + "");
			}
			if (!"".equals(StringUtil.checkNull(status))) {
				buff.append(" and ap.status =" + status + "");
			}
			List<AfterSaleStockin> maps = afStockService.getSaleUpshelfList(buff.toString(),-1, -1, " ap.create_datetime desc ");
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品编号");
			header.add("小店名称");
			header.add("售后处理单号");
			header.add("售后处理单状态");
			header.add("原货位号");
			header.add("目的货位号");
			header.add("任务生成时间");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleStockin bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getProductCode());
				printList.add(bean.getShopName());
				printList.add(bean.getCode());
				printList.add(bean.getHandlingstatus());
				printList.add(bean.getOutCargoWholeCode());
				printList.add(bean.getInCargoWholeCode());
				printList.add(bean.getCreateDatetime());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 导出待寄回用户商品列表
	 * @author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelBackUserProduct")
	@ResponseBody
	public void excelBackUserProduct(HttpServletRequest request, HttpServletResponse response,
			String afterSaleDetectCode,String productCode,String afterSaleOrderCode,String type,String areaId,String spareCode) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id = " + areaId);
			}
			if(!"".equals(StringUtil.checkNull(afterSaleDetectCode))) {
				buff.append(" and ap.code = '" + afterSaleDetectCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(afterSaleOrderCode))) {
				buff.append(" and ap.after_sale_order_code='" + afterSaleOrderCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))) {
				buff.append(" and p.code = '" + productCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(type))) {
				buff.append(" and afbp.type = " +Integer.parseInt(type)+"");
			}
			if(!"".equals(StringUtil.checkNull(spareCode))) {
				buff.append(" and asrnpr.spare_code = '" + spareCode+"'");
			}
			List<AfterSaleBackUserProduct> maps = afStockService.getBackUserList(buff.toString(), -1, -1, "ap.create_datetime desc ");
			if(maps.size()>0){
				for(int i=0;i<maps.size();i++){
					AfterSaleBackUserProduct bean = maps.get(i);
					//查询寄回配件信息
					String fitting = afStockService.getAfterSaleFitting(bean.getAfterSaleDetectProductId(), 0);
					fitting = fitting.replaceAll("\n", ",");
					bean.setFittings(fitting);
				}
			}
			ArrayList<String> header = new ArrayList<String>();
			header.add("售后处理单号");
			header.add("售后单号");
			header.add("原备用机号");
			header.add("货位号");
			header.add("配件名称");
			header.add("小店名称");
			header.add("商品编号");
			header.add("最后操作人");
			header.add("最后操作时间");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleBackUserProduct bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getAfterSaleDetectProductCode());
				printList.add(bean.getAfterSaleOrderCode());
				printList.add(bean.getSpareCode());
				printList.add(bean.getCargoWholeCode());
				printList.add(bean.getFittings());
				printList.add(bean.getProductName());
				printList.add(bean.getProductCode());
				printList.add(bean.getUserName());
				printList.add(bean.getCreateDatetime());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 导出已检测商品列表
	 * @author 李宁
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelDetectProducts")
	@ResponseBody
	public void excelDetectProducts(HttpServletRequest request, HttpServletResponse response,
			String packageCode, String startTime, String endTime, String afterSaleOrderCode, String productCode, 
			String createUserName,String parentId1) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.convertNull(packageCode))) {
				buff.append(" and aspack.package_code = '" + StringUtil.toSql(packageCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(afterSaleOrderCode))) {
				buff.append(" and asdp.after_sale_order_code = '" + StringUtil.toSql(afterSaleOrderCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(productCode))) {
				buff.append(" and p.code = '" + StringUtil.toSql(productCode) +"'");
			}
			if(!"".equals(StringUtil.convertNull(createUserName))) {
				buff.append(" and asdp.create_user_name = '" + StringUtil.toSql(createUserName) +"'");
			}
			if (startTime != null && !startTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (endTime != null && !endTime.trim().equals("")) {
				buff.append(" and asdp.create_datetime<='" + endTime + " 23:59:59'");
			}
			if(!"".equals(StringUtil.convertNull(parentId1))) {
				buff.append(" and p.parent_id1 = " + StringUtil.toSql(parentId1));
			}
			buff.append(" and asdp.status<>" + AfterSaleDetectProductBean.STATUS0 + " and asdp.status <>" + AfterSaleDetectProductBean.STATUS1 + " order by asdp.id desc");
			String query = "select asdp.id,p.code productCode,p.name productName,asdp.code,asdp.after_sale_order_code,aspack.package_code,asdp.create_datetime,asdp.create_user_name,asdp.after_sale_order_id,c.name " +
							"from after_sale_detect_product asdp,after_sale_detect_package aspack, product p,catalog c where asdp.after_sale_detect_package_id=aspack.id and p.parent_id1 = c.id and asdp.product_id=p.id " + buff.toString();
			query = DbOperation.getPagingQuery(query, -1, -1);
			ResultSet rs = afStockService.getDbOp().executeQuery(query);
			List<AfterSaleDetectProductBean> maps = new ArrayList<AfterSaleDetectProductBean>();
			AfterSaleDetectProductBean asdpBean = null;
			while (rs.next()) {
				asdpBean = new AfterSaleDetectProductBean();
				asdpBean.setId(rs.getInt(1));
				asdpBean.setProductCode(rs.getString(2));
				asdpBean.setProductName(rs.getString(3));
				asdpBean.setCode(rs.getString(4));
				asdpBean.setAfterSaleOrderCode(rs.getString(5));
				asdpBean.setPackageCode(rs.getString(6));
				asdpBean.setCreateDatetime(rs.getString(7));
				asdpBean.setCreateUserName(rs.getString(8));
				asdpBean.setAfterSaleOrderId(rs.getInt(9));
				asdpBean.setParentId1Name(rs.getString(10));
				maps.add(asdpBean);
			}
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品名称");
			header.add("商品编号");
			header.add("一级分类");
			header.add("售后处理单号");
			header.add("售后单号");
			header.add("包裹单号");
			header.add("检测时间");
			header.add("检测人");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleDetectProductBean bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getProductName());
				printList.add(bean.getProductCode());
				printList.add(bean.getParentId1Name());
				printList.add(bean.getCode());
				printList.add(bean.getAfterSaleOrderCode());
				printList.add(bean.getPackageCode());
				printList.add(bean.getCreateDatetime());
				printList.add(bean.getCreateUserName());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	 /** 导出售后库入库列表
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/excelSaleStockProduct")
	@ResponseBody
	public void excelSaleStockProduct(HttpServletRequest request, HttpServletResponse response,
			String code,String areaId,String startTime,String endTime,String status,String parentId1,String parentId2,String parentId3) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			
			String idStr;
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(code))) {
				buff.append(" and code like '%" + code +"%'");
			}
			// 接收返回的处理单id 字符串
			if(buff.length()>2){
			    idStr=afStockService.getAfterSaleIds(buff.toString());
			    buff.delete(0, buff.length());
			    if(!"0".equals(idStr) && !"".equals(StringUtil.checkNull(idStr))){
			    	buff.append(" and ats.after_sale_detect_product_id in("+idStr+")");  
				}
			}
			
			if (!"".equals(StringUtil.checkNull(startTime))) {
				buff.append(" and ats.create_datetime>='" + startTime + " 00:00:00'");
			}
			if (!"".equals(StringUtil.checkNull(endTime))) {
				buff.append(" and ats.create_datetime<='" + endTime + " 23:59:59'");
			}
			if (!"".equals(StringUtil.checkNull(status))) {
				buff.append(" and ap.status =" + status + "");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ap.area_id =" + areaId);
			}
			if(!"".equals(StringUtil.checkNull(parentId1))){
				buff.append(" and p.parent_id1=" + StringUtil.toSql(parentId1));
			}
			if(!"".equals(StringUtil.checkNull(parentId2))){
				buff.append(" and p.parent_id2=" + StringUtil.toSql(parentId2) );
			}
			if(!"".equals(StringUtil.checkNull(parentId3))){
				buff.append(" and p.parent_id3=" + StringUtil.toSql(parentId3) );
			}
			List<AfterSaleStockin> maps = afStockService.getSaleStockList(buff.toString(),-1, -1, " ats.create_datetime desc");
			
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品编号");
			header.add("小店名称");
			header.add("售后处理单号");
			header.add("售后处理单状态");
			header.add("任务生成时间");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			for (AfterSaleStockin bean : maps) {
				ArrayList<String> printList = new ArrayList<String>();
				printList.add(bean.getProductCode());
				printList.add(bean.getShopName());
				printList.add(bean.getCode());
				printList.add(bean.getHandlingstatus());
				printList.add(bean.getCreateDatetime());
				bodies.add(printList);
			}
			
			importExcel(header, bodies, response);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 根据包裹单号获取已匹配的商品
	 * @param request
	 * @param response
	 * @param packageCode
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @author lining
	* @date 2014-8-5
	 */
	@RequestMapping("/getMatchedProducts")
	@ResponseBody
	public EasyuiDataGridBean getMatchedProducts(HttpServletRequest request, HttpServletResponse response,String packageCode) throws ServletException, IOException{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		WareService wareService = new WareService(dbOp);
		try{
			AfterSaleDetectPackageBean packageBean = afStockService.getAfterSaleDetectPackage("package_code='" + StringUtil.checkNull(packageCode) + "'");
			if(packageBean==null){
				request.setAttribute("msg", "没有找到相应的寄回包裹信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			List<AfterSaleDetectProductBean> rows = afStockService.getAfterSaleDetectProductList("after_sale_detect_package_id=" + packageBean.getId(), -1, -1, "id");
			datagrid.setRows(rows);
			if(rows!=null && rows.size()>0){
				for(int i=0;i<rows.size();i++){
					AfterSaleDetectProductBean asdp = rows.get(i);
					asdp.setInBuyOrderName(AfterSaleDetectProductBean.inBuyOrderMap.get(asdp.getInBuyOrder()));
					asdp.setInUserOrderName(AfterSaleDetectProductBean.inUserOrderMap.get(asdp.getInUserOrder()));
					voProduct product = wareService.getProduct(asdp.getProductId());
					if (product != null) {
						asdp.setProductName(product.getName());
						asdp.setProductCode(product.getCode());
					}
				}
				datagrid.setTotal((long)rows.size());
			}else{
				datagrid.setTotal(new Long(0));
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datagrid;
	}
	/**
	 * 修改签收商品
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/modifyDetectProducts")
	@ResponseBody
	public Json modifyDetectProducts( HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg( "当前没有登录,操作失败!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IAfterSalesService iService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE,dbOp);
		int afterSaleDetectPackageId = StringUtil.toInt(request.getParameter("afterSaleDetectPackageId"));
		String afterSaleDetectProducts = StringUtil.checkNull(request.getParameter("afterSaleDetectProducts"));
		AfterSaleDetectProductBean asdpBean = new AfterSaleDetectProductBean();
		List<AfterSaleDetectProductBean> rpList = new ArrayList<AfterSaleDetectProductBean>();
		String[] theArray = afterSaleDetectProducts.split(",");
		for (int i = 0; i < theArray.length; i++) {
			switch (i%6) {
			case 0: asdpBean.setRemark(theArray[i]);
				break;
			case 1: asdpBean.setAfterSaleOrderId(StringUtil.toInt(theArray[i]));
				break;
			case 2: asdpBean.setProductId(StringUtil.toInt(theArray[i]));
				break;
			case 3: asdpBean.setInBuyOrder(StringUtil.toInt(theArray[i]));
				break;
			case 4 : asdpBean.setInUserOrder(StringUtil.toInt(theArray[i])); 
				break;
			case 5: asdpBean.setId(StringUtil.toInt(theArray[i]));
				rpList.add(asdpBean);
				asdpBean = new AfterSaleDetectProductBean();
				break;
			default:
				break;
			}
		}
		try {
			synchronized (request.getSession().getId()) {
				AfterSaleDetectPackageBean asdpackageBean = afStockService.getAfterSaleDetectPackage("id=" + afterSaleDetectPackageId);
				if (asdpackageBean == null) {
					j.setMsg("寄回包裹不存在!");
					return j;
				}
				if(!afStockService.checkAfterSaleUserGroup(user, asdpackageBean.getAreaId())){
					j.setMsg("没有该包裹所在地区售后仓内作业权限!");
					return j;
				}
				if(!user.getGroup().isFlag(2160)){
					j.setMsg("没有修改签收商品的权限!");
					return j;
				}
				if (asdpackageBean.getStatus() != AfterSaleDetectPackageBean.STATUS1) {
					j.setMsg("包裹必须为已匹配时才能进行修改操作!");
					return j;
				}
				
				//已匹配的商品列表
				Map<Integer,AfterSaleDetectProductBean> detectProductsMap = new HashMap<Integer,AfterSaleDetectProductBean>(); 
				List<AfterSaleDetectProductBean> detectProducts = afStockService.getAfterSaleDetectProductList("after_sale_detect_package_id="  + afterSaleDetectPackageId , -1, -1, "id");
				if(detectProducts!=null && detectProducts.size()>0){
					for(AfterSaleDetectProductBean asdp : detectProducts){
						detectProductsMap.put(asdp.getId(), asdp);
					}
				}
				Map<Integer, String> afterSaleOrderIdMap = new HashMap<Integer, String>();
				int addCount = 0;//新增匹配商品的数量
				afStockService.getDbOp().startTransaction();
				if(rpList!=null && rpList.size()>0){
					for (AfterSaleDetectProductBean asdp : rpList) {
						if(detectProductsMap.containsKey(asdp.getId())){//过滤已匹配过的处理单商品
							detectProductsMap.remove(asdp.getId());
							afterSaleOrderIdMap.put(asdp.getAfterSaleOrderId(), "");
							continue;
						}else{
							if(asdp.getId()<0){
								asdp.setId(0);
							}
							asdp.setAfterSaleDetectPackageId(afterSaleDetectPackageId);
							AfterSaleOrderBean asoBean = iService.getAfterSaleOrder("id = " + asdp.getAfterSaleOrderId()); 
							if (asoBean == null) {
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("售后单不存在！");
								return j;
							}
							if (asoBean.getStatus() != AfterSaleOrderBean.STATUS_售后检测中) {
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("售后单状态不是售后检测中！");
								return j;
							}
							afterSaleOrderIdMap.put(asdp.getAfterSaleOrderId(), "");
							
							asdp.setAfterSaleOrderCode(asoBean.getAfterSaleOrderCode());
							asdp.setCode("");
							asdp.setStatus(AfterSaleDetectProductBean.STATUS0);
							asdp.setLockStatus(AfterSaleDetectProductBean.LOCK_STATUS0);
							asdp.setAreaId(asdpackageBean.getAreaId());
							if (!afStockService.addAfterSaleDetectProduct(asdp)) {
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("添加售后处理单失败!");
								return j;
							}
							voProduct product = afStockService.getProductById(asdp.getProductId(), dbOp);
							if(product==null){
								j.setMsg("处理单关联的商品不存在!");
								return j;
							}
							AfterSaleLogBean log = new AfterSaleLogBean();
							log.setCount(1);
							log.setType(AfterSaleLogBean.TYPE23);
							log.setContent(user.getUsername()+"添加商品" + product.getOriname());
							log.setOperCode(asdpackageBean.getPackageCode());
							log.setCreateDatetime(DateUtil.getNow());
							log.setCreateUserId(user.getId());
							log.setCreateUserName(user.getUsername());
							if(!afStockService.addAfterSaleLogBean(log)){
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("添加售后处理单日志失败!");
								return j;
							}
							addCount += 1;
							detectProductsMap.remove(asdp.getId());
						}
					}
					if(detectProductsMap.size()>0){
						//把之前已匹配过的商品经过修改去掉了---删除已匹配的商品
						for(Integer key : detectProductsMap.keySet()){
							if(!afStockService.deleteAfterSaleDetectProduct("id=" + key)){
								j.setMsg("修改已匹配商品失败!");
								return j;
							}
							voProduct product = afStockService.getProductById(detectProductsMap.get(key).getProductId(), dbOp);
							if(product==null){
								j.setMsg("处理单关联的商品不存在!");
								return j;
							}
							AfterSaleLogBean log = new AfterSaleLogBean();
							log.setCount(1);
							log.setType(AfterSaleLogBean.TYPE24);
							log.setContent(user.getUsername()+"删除商品" + product.getOriname());
							log.setOperCode(asdpackageBean.getPackageCode());
							log.setCreateDatetime(DateUtil.getNow());
							log.setCreateUserId(user.getId());
							log.setCreateUserName(user.getUsername());
							if(!afStockService.addAfterSaleLogBean(log)){
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("添加售后处理单日志失败!");
								return j;
							}
						}
					}
				}
				for(int asoId : afterSaleOrderIdMap.keySet()){
					//判断该售后单是否全部检测完毕
					int count = afStockService.getAfterSaleDetectProductCount("after_sale_order_id=" + asoId + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 +")");
					if (count <= 0) {
						ResultSet rs  = afStockService.getDbOp().executeQuery("select count(asdl.id) from after_sale_detect_log asdl, after_sale_detect_product asdp where asdl.after_sale_detect_product_id=asdp.id and asdp.after_sale_order_id=" + asoId + " and asdl.after_sale_detect_type_id=" + AfterSaleDetectTypeBean.HANDLE + " and asdl.content<>'" + AfterSaleDetectProductBean.HANDLE7 + "'");
						if (rs.next()) {
							count = rs.getInt(1);
						}
						rs.close();
						//判断是否存在有保修的，售后单状态以等待客户确认优先
						if (count <= 0) {
							if (!iService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_售后处理中, "id=" + asoId)) {
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("更新售后单信息失败！");
								return j;
							}
						} else {
							if (!iService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_等待客户确认, "id=" + asoId)) {
								afStockService.getDbOp().rollbackTransaction();
								j.setMsg("更新售后单信息失败！");
								return j;
							}
						}
					}
				}	
				//判断包裹单是否检测完毕
				int count = afStockService.getAfterSaleDetectProductCount("after_sale_detect_package_id=" + afterSaleDetectPackageId + " and (status=" + AfterSaleDetectProductBean.STATUS0 + " or status=" + AfterSaleDetectProductBean.STATUS1 +")");
				if (count <= 0) {
					if (!afStockService.updateAfterSaleDetectPackage("status=" + AfterSaleDetectPackageBean.STATUS3, "id=" + afterSaleDetectPackageId)) {
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("更新签收包裹状态失败！");
						return j;
					}
				}
				afStockService.getDbOp().commitTransaction();
			} 
		}catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常!");
			return j;
		} finally {
			afStockService.releaseAll();
		}
		j.setMsg("修改签收商品成功！");
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 获取商品信息
	 */
	@RequestMapping("/getProduct")
	@ResponseBody
	public Json getProduct(HttpServletRequest request,HttpServletResponse response,String productCode){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg( "当前没有登录,操作失败!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		try{
			if(!"".equals(StringUtil.checkNull(productCode))){
				AfterSaleMatchFailPackageProduct asmfpBean = new AfterSaleMatchFailPackageProduct();
				voProduct product = wareService.getProduct(productCode);
				if(product==null){
					j.setMsg("您输入的商品编号对应的商品不存在!");
					return j;
				}
				
				asmfpBean.setProductName(product.getName());
				asmfpBean.setProductCode(product.getCode());
				asmfpBean.setProductId(product.getId());
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					j.setMsg("不能添加套装产品!");
					return j;
				}
				
				asmfpBean.setRemark("");
				j.setSuccess(true);
				j.setObj(asmfpBean);
				return j;
			}else{
				j.setMsg("请输入包裹单号!");
				return j;
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 生成匹配失败包裹商品
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/addMatchFailDetectProducts")
	@ResponseBody
	public Json addMatchFailDetectProducts( HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg( "当前没有登录,操作失败!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		int afterSaleDetectPackageId = StringUtil.toInt(request.getParameter("afterSaleDetectPackageId"));
		String afterSaleMatchFailPackageProducts = request.getParameter("afterSaleMatchFailPackageProducts");
		AfterSaleMatchFailPackageProduct bean = new AfterSaleMatchFailPackageProduct();
		List<AfterSaleMatchFailPackageProduct> list = new ArrayList<AfterSaleMatchFailPackageProduct>();
		String[] theArray = afterSaleMatchFailPackageProducts.split(",");
		for (int i = 0; i < theArray.length; i++) {
			switch (i%4) {
			case 0: bean.setRemark(theArray[i]);
				break;
			case 1: bean.setProductId(StringUtil.StringToId(theArray[i]));
				break;
			case 2: bean.setProductCode(StringUtil.checkNull(theArray[i]));
				break;
			case 3: bean.setProductName(StringUtil.checkNull(theArray[i]));
						list.add(bean);
						bean = new AfterSaleMatchFailPackageProduct();
				break;
			default:
				break;
			}
		}
		try {
			synchronized (lock) {
				
				AfterSaleDetectPackageBean asdpackageBean = afStockService.getAfterSaleDetectPackage("id=" + afterSaleDetectPackageId);
				if (asdpackageBean == null) {
					j.setMsg("寄回包裹不存在!");
					return j;
				}
				if(!afStockService.checkAfterSaleUserGroup(user, asdpackageBean.getAreaId())){
					j.setMsg("没有该包裹所在地区售后仓内作业权限!");
					return j;
				}
				if (asdpackageBean.getStatus() != AfterSaleDetectPackageBean.STATUS0) {
					j.setMsg("包裹必须为未匹配时才能添加未查明包裹内商品!");
					return j;
				}
				
				afStockService.getDbOp().startTransaction();
				for (AfterSaleMatchFailPackageProduct asmfpBean : list) {
					asmfpBean.setPackageId(afterSaleDetectPackageId);
					if(!afStockService.addMatchFailPackageProduct(asmfpBean)){
						afStockService.getDbOp().rollbackTransaction();
						j.setMsg("添加未查明包裹商品信息失败!");
						return j;
					}
				}
				if (!afStockService.updateAfterSaleDetectPackage("status=" + AfterSaleDetectPackageBean.STATUS2, "id=" + afterSaleDetectPackageId)) {
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("签收包裹更新状态失败!");
					return j;
				}
				String content = "匹配失败，包裹单号【" + asdpackageBean.getPackageCode() + "】";
				if(!afStockService.writeAfterSaleLog(user, content, 1, AfterSaleLogBean.TYPE18,asdpackageBean.getPackageCode(),null)){
					afStockService.getDbOp().rollbackTransaction();
					j.setMsg("售后日志添加失败!");
					return j;
				}
				afStockService.getDbOp().commitTransaction();
			}
		}catch ( Exception e ) {
			afStockService.getDbOp().rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("异常!");
			return j;
		} finally {
			afStockService.releaseAll();
		}
		j.setMsg("添加未查明包裹内商品信息成功！");
		j.setSuccess(true);
		return j;
	}
	/**
	 * 根据imei码获取售后处理单code
	 * @param request
	 * @param response
	 * @param detectCode
	 * @return
	 */
	@RequestMapping("/getAfterSaleDetectProduct")
	@ResponseBody
	public Json getAfterSaleDetectProduct(HttpServletRequest request, HttpServletResponse response,String imei,String detectCode,boolean flag){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			AfterSaleDetectProductBean detectProduct = null;
			if(flag){
				if("".equals(StringUtil.checkNull(imei))){
					j.setMsg("原imei不能为空!");
					return j;
				}
				detectProduct =  afStockService.getAfterSaleDetectProduct("IMEI='" + imei + "'");
			}else{
				if("".equals(StringUtil.checkNull(detectCode))){
					j.setMsg("原售后处理单号不能为空!");
					return j;
				}
				detectProduct =  afStockService.getAfterSaleDetectProduct("code='" + detectCode + "'");
			}
			
			if(detectProduct==null){
				j.setMsg("原售后处理单商品不存在!");
				return j;
			}
			j.setSuccess(true);
			j.setObj(detectProduct);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 厂家维修更换商品
	 * @param request
	 * @param response
	 * @param detectCode
	 * @return
	 */
	@RequestMapping("/replaceBackSupplierProduct")
	@ResponseBody
	public Json replaceBackSupplierProduct(HttpServletRequest request, HttpServletResponse response,
			String type,String oriImei,String detectCode,String newImei,String productCode){
		if(newImei != null && !"".equals(newImei)){
			newImei = newImei.trim();
		}
		
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		if(!user.getGroup().isFlag(2177)){
			j.setMsg("没有厂商维修更换商品匹配的权限!");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE,dbOp);
		WareService wareService = new WareService(dbOp);
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IAfterSalesService asoService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			synchronized (lock) {
				if(StringUtil.checkNull(detectCode).length()==0){
					j.setMsg("原售后处理单号不能为空!");
					return j;
				}
				AfterSaleDetectProductBean detectProduct =  afStockService.getAfterSaleDetectProduct("code='" + detectCode + "'");
				if(detectProduct==null){
					j.setMsg("原售后处理单商品不存在!");
					return j;
				}
				//获取最近一条返厂商品记录
				List<AfterSaleBackSupplierProduct> backSupplierProductList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + detectProduct.getId(),-1,1,"id desc");
				AfterSaleBackSupplierProduct backSupplierProduct = null;
				if(backSupplierProductList!=null &&backSupplierProductList.size()==1){
					backSupplierProduct = backSupplierProductList.get(0);
				}
				if(backSupplierProduct==null){
					j.setMsg("原售后处理单商品对应的返厂商品记录不存在!");
					return j;
				}
				//返厂商品状态判定
				if(!(backSupplierProduct.getStatus()==AfterSaleBackSupplierProduct.STATUS0 || backSupplierProduct.getStatus()==AfterSaleBackSupplierProduct.STATUS4)){
					j.setMsg("非法操作!");
					return j;
				}
				dbOp.startTransaction();
				if("1".equals(type)){//同sku更换
					//更新售后处理单商品与返厂商品的imei码，如果是大q手机需要记录imei码的log
					if(oriImei.equalsIgnoreCase(newImei)){
						j.setMsg("未更换，无需提交!");
						return j;
					}
					
					//新imei码是否是在使用中
					IMEIBean imeiBean = null;
					if(StringUtil.checkNull(newImei).length()>0){
						imeiBean = imeiService.getIMEI("code='" + newImei + "'");
						if(imeiBean!=null){
							if(imeiBean.getStatus()!=IMEIBean.IMEISTATUS4){
								j.setMsg("新IMEI码在使用中,不允许更换!");
								return j;
							}
							if(imeiBean.getProductId()!=detectProduct.getProductId()){
								j.setMsg("输入的新IMEI码与旧IMEI码不是同sku!");
								return j;
							}
						}
					}
					
					if(!afStockService.updateAfterSaleBackSupplierProduct("imei='" + newImei + "'", "id=" + backSupplierProduct.getId())){
						dbOp.rollbackTransaction();
						j.setMsg("解除原IMEI与返厂商品的关联关系失败!");	
						return j;
					}
					if(imeiService.isDaqByIEMI(oriImei)){
						if(!afStockService.updateAfterSaleDetectProduct("imei='" + newImei + "'", "id=" + detectProduct.getId())){
							dbOp.rollbackTransaction();
							j.setMsg("解除大q手机原imei码与售后处理单关联关系操作失败!");	
							return j;
						}
						//记录imei码状态变化及日志
						j = recordOldImei(oriImei, newImei, j, user, dbOp, afStockService,imeiService, detectProduct,true);
						if(j.getMsg()!=null && j.getMsg().length()>0){
							return j;
						}
						j = recordNewImei(oriImei, newImei, j, user, dbOp, imeiService, detectProduct.getProductId(), imeiBean);
						if(j.getMsg()!=null && j.getMsg().length()>0){
							return j;
						}
					}
				}else if("2".equals(type)){//更换sku
					//更换sku的前提条件：1、客户库商品不允许做“维修更换SKU”操作；
					//2、售后机在对应售后单完结前不允许做“维修更换SKU”操作；
					CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProduct.getCargoWholeCode() + "'");
					if(cargo==null){
						dbOp.rollbackTransaction();
						j.setMsg("售后处理单" + detectProduct.getCode() + "所对应的货位不存在!");
						return j;
					}
					if(cargo.stockType==CargoInfoBean.STOCKTYPE_CUSTOMER){
						dbOp.rollbackTransaction();
						j.setMsg("客户库商品不允许维修更换SKU!");
						return j;
					}else if(cargo.stockType == CargoInfoBean.STOCKTYPE_AFTER_SALE){
						AfterSaleOrderBean asoBean = asoService.getAfterSaleOrder("id=" + detectProduct.getAfterSaleOrderId());
						if(asoBean==null){
							dbOp.rollbackTransaction();
							j.setMsg("售后处理单" + detectProduct.getCode() + "所对应的售后单不存在!");
							return j;
						}
						if(asoBean.getStatus()!=AfterSaleOrderBean.STATUS_售后已完成){
							dbOp.rollbackTransaction();
							j.setMsg("售后机在对应售后单为未完结状态,不允许做\"维修更换SKU\"操作!");
							return j;
						}
					}
					
					IMEIBean imeiBean = null;
					//新imei码是否是在使用中
					if(StringUtil.checkNull(newImei).length()>0){
						imeiBean = imeiService.getIMEI("code='" + newImei + "'");
						if(imeiBean!=null){
							if(imeiBean.getStatus()!=IMEIBean.IMEISTATUS4){
								j.setMsg("新IMEI码在使用中,不允许更换!");
								return j;
							}
						}
					}
					voProduct product = wareService.getProduct(detectProduct.getProductId());
					if(oriImei.equalsIgnoreCase(newImei) && productCode.equalsIgnoreCase(product.getCode())){
						dbOp.rollbackTransaction();
						j.setMsg("未更换，无需提交!");	
						return j;
					}
					//新产品编号对应的商品是否为在售商品
					voProduct newProduct = wareService.getProduct(productCode);
					if(newProduct==null){
						dbOp.rollbackTransaction();
						j.setMsg("商品不存在，无法提交!");
						return j;
					}
					if(newProduct.getStatus()==100){
						dbOp.rollbackTransaction();
						j.setMsg("非在售商品，无法提交!");	
						return j;
					}
					//新imei码是否是大q手机
					boolean flag = false;
					//判断新商品是否是大q手机，是，则新IMEI是必填项
					if(imeiService.isDaqByProductId(newProduct.getId())){
						flag = true;
						if(StringUtil.checkNull(newImei).length()==0){
							dbOp.rollbackTransaction();
							j.setMsg("新IMEI码不能为空!");	
							return j;
						}
					}
					//新旧采购价对比
					float diff = newProduct.getPrice5() - product.getPrice5();
					AfterSaleBackSupplierProductReplace asbsprBean = new AfterSaleBackSupplierProductReplace();
					
					String code = getRepairRecordCode(afStockService);
					asbsprBean.setCode(code);
					asbsprBean.setNewImei(newImei);
					asbsprBean.setOldImei(oriImei);
					asbsprBean.setNewProductId(newProduct.getId());
					asbsprBean.setOldProductId(product.getId());
					asbsprBean.setNewPrice(newProduct.getPrice5());
					asbsprBean.setOldPrice(product.getPrice5());
					asbsprBean.setCreateDatetime(DateUtil.getNow());
					asbsprBean.setBackSupplierProductId(backSupplierProduct.getId());
					asbsprBean.setSupplierId(backSupplierProduct.getSupplierId());
					AfterSaleBackSupplier backSupplier = afStockService.getAfterSaleBackSupplier("id=" + backSupplierProduct.getSupplierId());
					if(backSupplier!=null){
						asbsprBean.setSupplierName(backSupplier.getName());
					}
					asbsprBean.setCreateUserId(user.getId());
					asbsprBean.setCreateUserName(user.getUsername());
					asbsprBean.setDetectId(detectProduct.getId());
					
					if(diff>=0){
						asbsprBean.setAuditStatus(AfterSaleBackSupplierProductReplace.AUDIT_STATUS2);
						asbsprBean.setAuditUserId(user.getId());
						asbsprBean.setAuditUserName(user.getUsername());
						asbsprBean.setAuditDatetime(DateUtil.getNow());
						if(!afStockService.addAfterSaleBackSupplierProductReplace(asbsprBean)){
							dbOp.rollbackTransaction();
							j.setMsg("添加维修更换单失败!");	
							return j;
						}
//						//库存变更
//						j = changeStock(j, dbOp, stockService, cargoService,detectProduct, product, newProduct,user,asbsprBean.getCode());
//						if(j.getMsg()!=null&&j.getMsg().length()>0){
//							return j;
//						}
						
						//取消售后处理单号与原SKU产品编号及IMEI关联关系，新增该售后处理单与新SKU产品编号及IMEI关联关系
						if(!afStockService.updateAfterSaleBackSupplierProduct("imei='" + newImei + "',product_id=" + newProduct.getId(), "id=" + backSupplierProduct.getId())){
							dbOp.rollbackTransaction();
							j.setMsg("解除原IMEI与返厂商品的关联关系失败!");	
							return j;
						}
						
						String set = "imei='" + newImei + "',product_id=" + newProduct.getId();
						if(!afStockService.updateAfterSaleDetectProduct(set, "id=" + detectProduct.getId())){
							dbOp.rollbackTransaction();
							j.setMsg("解除原IMEI与处理单商品的关联关系失败!");	
							return j;
						}
						AfterSaleLogBean afLog = new AfterSaleLogBean();
						afLog.setContent("厂家维修更换商品匹配,解除原IMEI与处理单商品的关联关系");
						afLog.setCount(1);
						afLog.setCreateDatetime(DateUtil.getNow());
						afLog.setCreateUserId(user.getId());
						afLog.setCreateUserName(user.getUsername());
						afLog.setType(AfterSaleLogBean.TYPE17);
						afLog.setOperCode(detectProduct.getCode());
						
						if(!afStockService.addAfterSaleLogBean(afLog)){
							dbOp.rollbackTransaction();
							j.setMsg("新增售后处理单商品日志失败!");	
							return j;
						}
						String sql = "update after_sale_warehource_product_records set product_id=" + newProduct.getId() + " where id=" + detectProduct.getId();
						if(!dbOp.executeUpdate(sql)){
							dbOp.rollbackTransaction();
							j.setMsg("更新销售后台售后处理单商品失败!");	
							return j;
						}
						
						//记录imei码状态变化及日志
						if(StringUtil.checkNull(oriImei).length()>0 &&  imeiService.isDaqByIEMI(oriImei)){
							j = recordOldImei(oriImei, newImei, j, user, dbOp, afStockService,imeiService, detectProduct,flag);
							if(j.getMsg()!=null && j.getMsg().length()>0){
								return j;
							}
						}
						if(StringUtil.checkNull(newImei).length()>0 && flag){
							j = recordNewImei(oriImei, newImei, j, user, dbOp, imeiService, newProduct.getId(), imeiBean);
							if(j.getMsg()!=null && j.getMsg().length()>0){
								return j;
							}
						}
						
						
						//返厂维修更换单审核通过时系统自动初始化生成状态为已完成的报损报溢单 #2536 2015-02-25
						IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
						String nowTime = DateUtil.getNow();
						
						BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();				
						bsbyOperationnoteBean.setOperator_id(asbsprBean.getCreateUserId());//操作人id（添加）
						bsbyOperationnoteBean.setOperator_name(asbsprBean.getCreateUserName());//操作人
						bsbyOperationnoteBean.setAdd_time(asbsprBean.getCreateDatetime());//添加时间				
						bsbyOperationnoteBean.setEnd_oper_id(asbsprBean.getAuditUserId());//完成操作人id(运营)
						bsbyOperationnoteBean.setEnd_oper_name(asbsprBean.getAuditUserName());//最后修改人
						bsbyOperationnoteBean.setEnd_time(asbsprBean.getAuditDatetime());//完成时间（运营）
						bsbyOperationnoteBean.setFinAuditId(asbsprBean.getAuditUserId());//财务审核人id
						bsbyOperationnoteBean.setFinAuditName(asbsprBean.getCreateUserName());//财务审核人姓名
						bsbyOperationnoteBean.setFinAuditDatetime(asbsprBean.getAuditDatetime());//财务审核时间
						bsbyOperationnoteBean.setFinAuditRemark("");//财务审核意见			
						bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.audit_end);//状态:已完成
						bsbyOperationnoteBean.setWarehouse_area(detectProduct.getAreaId());//库地区
						bsbyOperationnoteBean.setWarehouse_type(ProductStockBean.STOCKTYPE_AFTER_SALE);//库类型
						bsbyOperationnoteBean.setIf_del(0);//删除状态
						bsbyOperationnoteBean.setRemark("厂家维修更换产品编号（售后专用）");//注释
						
						//报损
						String codeBsBy = "BS" + nowTime.substring(0, 10).replace("-", "");
						codeBsBy = ByBsAction.createCode(codeBsBy);// BS+年月日+4位自动增长数
						bsbyOperationnoteBean.setType(0);//报损
						bsbyOperationnoteBean.setReceipts_number(codeBsBy);//编码
						
						float oldNotaxPrice = bsbyService.returnFinanceProductPrice(product.getId());//不含税金额
						
						CargoProductStockBean oldCargoStock = cargoService.getCargoProductStock("product_id=" + product.getId() + " and cargo_id=" + cargo.getId());
						if(oldCargoStock == null){
							dbOp.rollbackTransaction();
							j.setMsg("找不到可用原商品货位库存!");	
							return j;
						}
						
						String resultStr = afStockService.addBsbyOperationnoteAndProduct(bsbyService,bsbyOperationnoteBean,detectProduct,product,oldCargoStock,String.valueOf(oldNotaxPrice),user,true);
						if(resultStr != null && !"".equals(resultStr)){
							dbOp.rollbackTransaction();
							j.setMsg(resultStr);	
							return j;
						}
						
						//更新处理单bean的数据
						detectProduct.setIMEI(newImei);
						detectProduct.setProductId(newProduct.getId());
						
						//报溢
						codeBsBy = "BY" + nowTime.substring(0, 10).replace("-", "");
						codeBsBy = ByBsAction.createCode(codeBsBy);// BY+年月日+4位自动增长数
						bsbyOperationnoteBean.setType(1);//报溢
						bsbyOperationnoteBean.setReceipts_number(codeBsBy);//编码
						float newNotaxPrice = bsbyService.returnFinanceProductPrice(newProduct.getId());//不含税金额
						
						CargoProductStockBean newCargoStock = cargoService.getCargoProductStock("product_id=" + newProduct.getId() + " and cargo_id=" + cargo.getId());
						if(newCargoStock == null){
							newCargoStock = new CargoProductStockBean();
							newCargoStock.setCargoId(cargo.getId());
							newCargoStock.setProductId(newProduct.getId());
							newCargoStock.setStockCount(0);
							newCargoStock.setStockLockCount(0);
							if (!cargoService.addCargoProductStock(newCargoStock)) {
								cargoService.getDbOp().rollbackTransaction();
								j.setMsg("新商品增加货位库存失败!");	
								return j;
							}
							newCargoStock.setId(cargoService.getDbOp().getLastInsertId());
						}
						resultStr = afStockService.addBsbyOperationnoteAndProduct(bsbyService,bsbyOperationnoteBean,detectProduct,newProduct,newCargoStock,String.valueOf(newNotaxPrice),user,true);
						if(resultStr != null && !"".equals(resultStr)){
							dbOp.rollbackTransaction();
							j.setMsg(resultStr);	
							return j;
						}
					}else{
						asbsprBean.setAuditStatus(AfterSaleBackSupplierProductReplace.AUDIT_STATUS1);
						if(!afStockService.addAfterSaleBackSupplierProductReplace(asbsprBean)){
							dbOp.rollbackTransaction();
							j.setMsg("添加维修更换单失败!");	
							return j;
						}
						//冻结库存、货位库存
						j = freezeStock(j, dbOp, stockService, cargoService,detectProduct, product);
						if(j.getMsg()!=null && j.getMsg().length()>0){
							return j;
						}
						//锁定售后处理单
						if(!afStockService.updateAfterSaleDetectProduct("lock_status=1", "id=" + detectProduct.getId())){
							dbOp.rollbackTransaction();
							j.setMsg("锁定售后处理单失败!");	
							return j;
						}
					}
				}
				
				dbOp.commitTransaction();
				j.setSuccess(true);
				j.setMsg("厂家维修更换商品匹配成功!");
			}
		}catch (Exception e) {
			dbOp.rollbackTransaction();
			System.out.print(DateUtil.getNow());e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	/**
	 * 库存变更，增加进销存卡片，增加批次处理
	 * @param j
	 * @param dbOp
	 * @param stockService
	 * @param cargoService
	 * @param detectProduct
	 * @param product
	 * @param newProduct
	 * @return
	 */
	/*private Json changeStock(Json j, DbOperation dbOp,
			IProductStockService stockService, ICargoService cargoService,
			AfterSaleDetectProductBean detectProduct, voProduct product,
			voProduct newProduct,voUser user,String replaceCode) {
		product.setPsList(stockService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
		CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProduct.getCargoWholeCode() + "'");
		if(cargo==null){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "的商品,所对应的货位不存在!");	
			return j;
		}
		ProductStockBean stockBean = stockService.getProductStock("product_id=" + product.getId() + " and area=" + detectProduct.getAreaId() + " and type=" + cargo.getStockType());
		if(stockBean==null){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "的商品,所对应的商品库存不存在!");	
			return j;
		}
		if(!stockService.updateProductStockCount(stockBean.getId(), -1)){
			dbOp.rollbackTransaction();
			j.setMsg("商品" + product.getCode() + "修改产品库存数量操作失败,库存不足!");	
			return j;
		}
		
		CargoProductStockBean cargoStockBean = cargoService.getCargoProductStock("cargo_id=" + cargo.getId() + " and product_id=" + product.getId());
		if(cargoStockBean==null){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "的商品,所对应的货位库存不存在!");	
			return j;
		}
		if(!cargoService.updateCargoProductStockCount(cargoStockBean.getId(), -1)){
			dbOp.rollbackTransaction();
			j.setMsg("商品" + product.getCode() + "修改产品货位库存数量操作失败,货位库存不足!");	
			return j;
		}
		//进销存卡片--出库
		StockCardBean oldStockCard = new StockCardBean();
		oldStockCard.setCardType(StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_OUT);
		oldStockCard.setCode(detectProduct.getCode());
		oldStockCard.setCreateDatetime(DateUtil.getNow());
		oldStockCard.setStockType(cargo.getStockType());
		oldStockCard.setStockArea(detectProduct.getAreaId());
		oldStockCard.setProductId(product.getId());
		oldStockCard.setStockId(stockBean.getId());
		oldStockCard.setStockOutCount(1);
		oldStockCard.setStockOutPriceSum(new BigDecimal(1).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		oldStockCard.setCurrentStock(product.getStock(oldStockCard.getStockArea(),oldStockCard.getStockType()) + product.getLockCount(oldStockCard.getStockArea(),oldStockCard.getStockType()));
		oldStockCard.setStockAllArea(product.getStock(oldStockCard.getStockArea()) + product.getLockCount(oldStockCard.getStockArea()));
		oldStockCard.setStockAllType(product.getStockAllType(oldStockCard.getStockType()) + product.getLockCountAllType(oldStockCard.getStockType()));
		oldStockCard.setAllStock(product.getStockAll() + product.getLockCountAll());
		oldStockCard.setStockPrice(product.getPrice5());
		oldStockCard.setAllStockPriceSum(new BigDecimal(oldStockCard.getAllStock()).multiply(new BigDecimal(StringUtil.formatDouble2(oldStockCard.getStockPrice()))).doubleValue());
		if(!stockService.addStockCard(oldStockCard)){
			dbOp.rollbackTransaction();
			j.setMsg("添加进销存卡片失败!");
			return j;
		}
		//货位入库卡片--出库
		CargoStockCardBean oldCargoStockCard = new CargoStockCardBean();
		oldCargoStockCard.setCardType(CargoStockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_OUT);
		oldCargoStockCard.setCode(detectProduct.getCode());
		oldCargoStockCard.setCreateDatetime(DateUtil.getNow());
		oldCargoStockCard.setStockType(cargo.getStockType());
		oldCargoStockCard.setStockArea(detectProduct.getAreaId());
		oldCargoStockCard.setProductId(product.getId());
		oldCargoStockCard.setStockId(cargoStockBean.getId());
		oldCargoStockCard.setStockOutCount(1);
		oldCargoStockCard.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		oldCargoStockCard.setCurrentStock(product.getStock(oldCargoStockCard.getStockArea(), oldCargoStockCard.getStockType()) + product.getLockCount(oldCargoStockCard.getStockArea(), oldCargoStockCard.getStockType()));
		oldCargoStockCard.setAllStock(product.getStockAll() + product.getLockCountAll());
		oldCargoStockCard.setCurrentCargoStock(cargoStockBean.getStockCount()+cargoStockBean.getStockLockCount());
		oldCargoStockCard.setCargoStoreType(cargo.getStoreType());
		oldCargoStockCard.setCargoWholeCode(cargo.getWholeCode());
		oldCargoStockCard.setStockPrice(product.getPrice5());
		oldCargoStockCard.setAllStockPriceSum((new BigDecimal(oldCargoStockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(oldCargoStockCard.getStockPrice()))).doubleValue());
		if(!cargoService.addCargoStockCard(oldCargoStockCard)){
			j.setMsg("货位进销存记录添加失败!");
			dbOp.rollbackTransaction();
			return j;
		}
		//批次
		StockCommon stockCommon = new StockCommon();
		StockServiceImpl service = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		StockBatchBean stockBatch = service.getStockBatch("product_id=" + product.getId() + " and stock_area=" + detectProduct.getAreaId() + " and stock_type=" + cargo.getStockType());
		if(stockBatch==null){
			dbOp.rollbackTransaction();
			j.setMsg("商品" + product.getCode() + "所对应的批次不存在!");
			return j;
		}
		int batchCount = stockCommon.updateStockBatch(dbOp, user, stockBatch, 1, replaceCode, "厂家维修更换商品出库");
		if(batchCount < 0){
			dbOp.rollbackTransaction();
			j.setMsg("更新库存批次及添加库存批次操作日志失败_出库");
			return j;
		}
		//财务进销存卡片_出库
		String result = stockCommon.addFinanceStockCardOut(dbOp, stockBatch, stockBean.getId(), batchCount, StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_OUT, replaceCode);
		if(result != null){
			dbOp.rollbackTransaction();
			j.setMsg(result);
			return j;
		}
		
		//新商品库存及货位存库修改
		ProductStockBean newStock = stockService.getProductStock("product_id=" + newProduct.getId() + " and area=" + detectProduct.getAreaId() + " and type=" + cargo.getStockType());
		if(newStock == null){
			dbOp.rollbackTransaction();
			j.setMsg("新商品库存信息不存在!");
			return j;
		}
		if(!stockService.updateProductStockCount(newStock.getId(), 1)){
			dbOp.rollbackTransaction();
			j.setMsg("更新新商品库存失败!");
			return j;
		}
		
		//新商品的货位用售后处理单商品中记录的货位
		CargoProductStockBean newCargoStock = cargoService.getCargoProductStock("cargo_id=" + cargo.getId() + " and product_id=" + newProduct.getId());
		if(newCargoStock == null){
			newCargoStock = new CargoProductStockBean();
			newCargoStock.setCargoId(cargo.getId());
			newCargoStock.setProductId(newProduct.getId());
			newCargoStock.setStockCount(1);
			if (!cargoService.addCargoProductStock(newCargoStock)) {
				dbOp.rollbackTransaction();
				j.setMsg("添加新商品货位库存失败!");
				return j;
			}
			newCargoStock.setId(dbOp.getLastInsertId());
		} else {
			if(!cargoService.updateCargoProductStockCount(newCargoStock.getId(), 1)){
				dbOp.rollbackTransaction();
				j.setMsg("更新新商品货位库存失败!");
				return j;
			}
		}
		
		//进销存卡片--入库
		StockCardBean newStockCard = new StockCardBean();
		newStockCard.setCardType(StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_IN);
		newStockCard.setCode(detectProduct.getCode());
		newStockCard.setCreateDatetime(DateUtil.getNow());
		newStockCard.setStockType(cargo.getStockType());
		newStockCard.setStockArea(detectProduct.getAreaId());
		newStockCard.setProductId(newProduct.getId());
		newStockCard.setStockId(newStock.getId());
		newStockCard.setStockInCount(1);
		newStockCard.setStockInPriceSum(new BigDecimal(1).multiply(new BigDecimal(StringUtil.formatDouble2(newProduct.getPrice5()))).doubleValue());
		newStockCard.setCurrentStock(newProduct.getStock(newStockCard.getStockArea(),newStockCard.getStockType()) + newProduct.getLockCount(newStockCard.getStockArea(),newStockCard.getStockType()));
		newStockCard.setStockAllArea(newProduct.getStock(newStockCard.getStockArea()) + newProduct.getLockCount(newStockCard.getStockArea()));
		newStockCard.setStockAllType(newProduct.getStockAllType(newStockCard.getStockType()) + newProduct.getLockCountAllType(newStockCard.getStockType()));
		newStockCard.setAllStock(newProduct.getStockAll() + newProduct.getLockCountAll());
		newStockCard.setStockPrice(newProduct.getPrice5());
		newStockCard.setAllStockPriceSum(new BigDecimal(newStockCard.getAllStock()).multiply(new BigDecimal(StringUtil.formatDouble2(newStockCard.getStockPrice()))).doubleValue());
		if(!stockService.addStockCard(newStockCard)){
			dbOp.rollbackTransaction();
			j.setMsg("添加进销存卡片失败!");
			return j;
		}
		//货位入库卡片
		CargoStockCardBean newCargoStockCard = new CargoStockCardBean();
		newCargoStockCard.setCardType(CargoStockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_IN);
		newCargoStockCard.setCode(detectProduct.getCode());
		newCargoStockCard.setCreateDatetime(DateUtil.getNow());
		newCargoStockCard.setStockType(newStock.getType());
		newCargoStockCard.setStockArea(newStock.getArea());
		newCargoStockCard.setProductId(newProduct.getId());
		newCargoStockCard.setStockId(newCargoStock.getId());
		newCargoStockCard.setStockInCount(1);
		newCargoStockCard.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(newProduct.getPrice5()))).doubleValue());
		newCargoStockCard.setCurrentStock(newProduct.getStock(newCargoStockCard.getStockArea(), newCargoStockCard.getStockType()) + newProduct.getLockCount(newCargoStockCard.getStockArea(), newCargoStockCard.getStockType()));
		newCargoStockCard.setAllStock(newProduct.getStockAll() + newProduct.getLockCountAll());
		newCargoStockCard.setCurrentCargoStock(cargoStockBean.getStockCount()+cargoStockBean.getStockLockCount());
		newCargoStockCard.setCargoStoreType(cargo.getStoreType());
		newCargoStockCard.setCargoWholeCode(cargo.getWholeCode());
		newCargoStockCard.setStockPrice(newProduct.getPrice5());
		newCargoStockCard.setAllStockPriceSum((new BigDecimal(newCargoStockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(newCargoStockCard.getStockPrice()))).doubleValue());
		if(!cargoService.addCargoStockCard(newCargoStockCard)){
			j.setMsg("货位进销存记录添加失败!");
			dbOp.rollbackTransaction();
			return j;
		}
		
		String condition = "product_id=" + newProduct.getId() + " and stock_type=" + detectProduct.getStockType() + " and stock_area=" + detectProduct.getAreaId();
		StockBatchBean batchIn = service.getStockBatch(condition);
		if(batchIn == null){
			result = stockCommon.addStockBatch(dbOp, user, newProduct.getId(), 1, detectProduct.getAreaId(),detectProduct.getStockType(), replaceCode, "");
			if(result != null){
				dbOp.rollbackTransaction();
				j.setMsg(result);
				return j;
			}
		} else {
			batchCount = stockCommon.updateStockBatch(dbOp, user, batchIn, -1, replaceCode, "厂家维修更换商品入库");
			//更新库存批次及添加库存批次操作日志
			if(batchCount < 0){
				dbOp.rollbackTransaction();
				j.setMsg("更新库存批次及添加库存批次操作日志失败_入库");
				return j;
			}
		}
		//财务进销存卡片_入库
		batchIn = service.getStockBatch(condition);
		result = stockCommon.addFinanceStockCardIn(dbOp, batchIn, newStock.getId(), 1, StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_IN, replaceCode);
		if(result != null){
			dbOp.rollbackTransaction();
			j.setMsg(result);
			return j;
		}
		
		return j;
	}*/
	
	/**
	 * 冻结库存、货位库存
	 * 说明：减可用量加锁定量
	 * @param j
	 * @param dbOp
	 * @param stockService
	 * @param cargoService
	 * @param detectProduct
	 * @param product
	 * @return
	 */
	private Json freezeStock(Json j, DbOperation dbOp,
			IProductStockService stockService, ICargoService cargoService,
			AfterSaleDetectProductBean detectProduct, voProduct product) {
		CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProduct.getCargoWholeCode() + "'");
		if(cargo==null){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "的商品,所对应的货位不存在!");	
			return j;
		}
		ProductStockBean stockBean = stockService.getProductStock("product_id=" + product.getId() + " and area=" + detectProduct.getAreaId() + " and type=" + cargo.getStockType());
		if(stockBean==null){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "的商品,所对应的商品库存不存在!");	
			return j;
		}
		if(!stockService.updateProductStockCount(stockBean.getId(),-1)){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "修改产品库存冻结数量操作失败，库存不足!");	
			return j;
		}
		if(!stockService.updateProductLockCount(stockBean.getId(), 1)){
			dbOp.rollbackTransaction();
			j.setMsg("商品" + product.getCode() + "修改产品库存冻结数量操作失败!");	
			return j;
		}
		
		CargoProductStockBean cargoStockBean = cargoService.getCargoProductStock("cargo_id=" + cargo.getId() + " and product_id=" + product.getId());
		if(cargoStockBean==null){
			dbOp.rollbackTransaction();
			j.setMsg("编号为" + product.getCode() + "的商品,所对应的货位库存不存在!");	
			return j;
		}
		if(!cargoService.updateCargoProductStockCount(cargoStockBean.getId(), -1)){
			dbOp.rollbackTransaction();
			j.setMsg("商品" + product.getCode() + "修改产品货位库存冻结数量操作失败,货位库存不足!");	
			return j;
		}
		if(!cargoService.updateCargoProductStockLockCount(cargoStockBean.getId(), 1)){
			dbOp.rollbackTransaction();
			j.setMsg("商品" + product.getCode() + "修改产品货位库存冻结数量操作失败!");	
			return j;
		}
		return j;
	}
	/**
	 * 获取维修更换单号码
	 * @param afStockService
	 * @return
	 */
	private String getRepairRecordCode(AfStockService afStockService) {
		//维修更换单号规则：GH+8位时间+4位序号
		SimpleDateFormat sdfc = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		String code = "GH"+sdfc.format(cal.getTime());
		List list = afStockService.getAfterSaleBackSupplierProductReplaceList(" code like '"+code+"%' ", 0, 1, " id desc");
		if(list==null||list.size()==0){
			code+="0001";
		}else{
			String _code = ((AfterSaleBackSupplierProductReplace)(list.get(0))).getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-4));
			number++;
			code += String.format("%04d",new Object[]{new Integer(number)});
		}
		return code;
	}

	/**
	 * 记录旧IMEI码的状态变化及日志
	 * @param oriImei
	 * @param newImei
	 * @param j
	 * @param user
	 * @param dbOp
	 * @param afStockService
	 * @param imeiService
	 * @param detectProduct
	 * @param log
	 * @return
	 */
	private Json recordOldImei(String oriImei, String newImei, Json j,
			voUser user, DbOperation dbOp, AfStockService afStockService,
			IMEIService imeiService, AfterSaleDetectProductBean detectProduct,boolean flag) {
		
		if(!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS4, "product_id=" + detectProduct.getProductId() + " and code='" + oriImei + "'")){
			dbOp.rollbackTransaction();
			j.setMsg("更新imei码状态失败!");	
			return j;
		}
		IMEILogBean log = new IMEILogBean();
		if(flag){
			log.setContent("厂家维修更换商品匹配,同sku更换,IMEI码更换为" + newImei);
		}else{
			log.setContent("厂家维修更换商品匹配,更换sku,IMEI码更换为" + newImei);
		}
		log.setCreateDatetime(DateUtil.getNow());
		log.setIMEI(oriImei);
		log.setOperCode("");
		log.setOperType(IMEILogBean.OPERTYPE10);
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		if(!imeiService.addIMEILog(log)){
			dbOp.rollbackTransaction();
			j.setMsg("新增imei日志失败!");	
			return j;
		}
		return j;
	}
	/**
	 * 记录新IMEI码的状态变化及日志记录
	 * @param oriImei
	 * @param newImei
	 * @param j
	 * @param dbOp
	 * @param imeiService
	 * @param detectProduct
	 * @param imeiBean
	 * @param log
	 */
	private Json recordNewImei(String oriImei, String newImei, Json j,
			voUser user, DbOperation dbOp, IMEIService imeiService,
			int newProductId, IMEIBean imeiBean) {
		if(imeiBean!=null){
			if(!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS6, "product_id=" + imeiBean.getProductId() + " and code='" + newImei + "'")){
				dbOp.rollbackTransaction();
				j.setMsg("更新imei码状态失败!");	
				return j;
			}
		}else{
			IMEIBean bean = new IMEIBean();
			bean.setCode(newImei);
			bean.setStatus(IMEIBean.IMEISTATUS6);
			bean.setProductId(newProductId);
			bean.setCreateDatetime(DateUtil.getNow());
			if(!imeiService.addIMEI(bean)){
				dbOp.rollbackTransaction();
				j.setMsg("新增imei码失败!");	
				return j;
			}
		}
		
		IMEILogBean log = new IMEILogBean();
		log.setContent("售后维修替换" + oriImei + "入库,入库状态是维修中.");
		log.setIMEI(newImei);
		log.setCreateDatetime(DateUtil.getNow());
		log.setOperCode("");
		log.setOperType(IMEILogBean.OPERTYPE10);
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		if(!imeiService.addIMEILog(log)){
			dbOp.rollbackTransaction();
			j.setMsg("新增imei日志失败!");	
			return j;
		}
		return j;
	}
	/**
	 * 获取处理单信息
	 * @param request
	 * @param response
	 * @param afterSaleDetectProductCode
	 * @param flag 1--输入的是imei码，2--输入的是售后处理单号
	 * @param operate 1--添加厂商报价的功能的限制条件，2--厂商寄回商品检测功能的限制条件
	 * @param imei
	 * @return
	 */
	@RequestMapping("/getDetectProductInfo")
	@ResponseBody
	public Json getDetectProductInfo(HttpServletRequest request, HttpServletResponse response,String afterSaleDetectProductCode,String flag,String imei,String operate){
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		j.setMsg("当前没有登录，操作失败!");
            return j;
    	}
    	DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
    	try{
    		AfterSaleDetectProductBean detectProduct = null;
    		if(StringUtil.checkNull(flag).equals("1")){
    			if(StringUtil.checkNull(imei).equals("")){
    	    		j.setMsg("请输入IMEI码!");
    	    		return j;
    	    	}
    			detectProduct = afStockService.getAfterSaleDetectProduct("IMEI='" + imei + "'");
    		}else if(StringUtil.checkNull(flag).equals("2")){
    			if(StringUtil.checkNull(afterSaleDetectProductCode).equals("")){
    	    		j.setMsg("请输入售后处理单号!");
    	    		return j;
    	    	}
    			detectProduct = afStockService.getAfterSaleDetectProduct("code='" + afterSaleDetectProductCode + "'");
    		}
    		if(detectProduct==null){
    			j.setMsg("您输入的处理单号" + afterSaleDetectProductCode + "所对应的处理单商品不存在!");
    			return j;
    		}
    		CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + detectProduct.getCargoWholeCode() + "'");
    		if(cargo==null){
    			j.setMsg("次售后处理单所对应的货位不存在!");
    			return j;
    		}
    		AfterSaleBackSupplierProduct asbup = null;
			List<AfterSaleBackSupplierProduct> asbupList = afStockService.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + detectProduct.getId(), -1, -1, "id desc");
			if (asbupList != null && asbupList.size() > 0) {
				asbup = asbupList.get(0);
				if (asbup != null) {
					detectProduct.setBackSupplierProductId(asbup.getId());
					operate = StringUtil.checkNull(operate);
					if(operate.equals("1")){//添加厂商报价功能
						//条件：客户库--处理单状态未付费维修或者保修，返厂商品状态为已返厂可添加
						//售后库--返厂商品状态为已返厂可添加
						if(cargo.getStockType()==ProductStockBean.STOCKTYPE_CUSTOMER){
							if (!((detectProduct.getStatus() == AfterSaleDetectProductBean.STATUS7 
									|| detectProduct.getStatus() == AfterSaleDetectProductBean.STATUS4 ) 
									&& asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS4)) {
								j.setMsg("此处理单对应的返厂商品不能添加返厂报价!");
								return j;
							}
						}else if(cargo.getStockType()==ProductStockBean.STOCKTYPE_AFTER_SALE){
							if (!(asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS4)) {
								j.setMsg("此处理单对应的返厂商品不能添加返厂报价!");
								return j;
							}
						}else{
							j.setMsg("错误");
							return j;
						}
					}else if(operate.equals("2")){//厂商寄回商品检测功能
						if (!(asbup.getStatus() == AfterSaleBackSupplierProduct.STATUS1)) {
							j.setMsg("此处理单对应的返厂商品不是厂商已寄回状态,不能进行检测!");
							return j;
						}
					}else{
						j.setMsg("错误");
						return j;
					}
				}else{
					j.setMsg("此处理单对应的返厂商品不存在!");
					return j;
				}
			}else{
				j.setMsg("此处理单对应的返厂商品不存在!");
				return j;
			}
			voProduct product = afStockService.getProductById(detectProduct.getProductId(), dbOp);
			if(product==null){
				j.setMsg("此处理单记录的商品信息不存在!");
				return j;
			}
			detectProduct.setParentId1(product.getParentId1());
			detectProduct.setProductOriname(product.getOriname());
			j.setSuccess(true);
			j.setObj(detectProduct);
    	}catch (Exception e) {
    		e.printStackTrace();
    	}finally{
    		afStockService.releaseAll();
    	}
		return j;
	}
	
	/**
	 * 打印寄回用户的小面单
	 * @param request
	 * @param response
	 * @author 李宁
	* @date 2014-5-26
	 */
	@RequestMapping("/printAfterSalePackageInfo")
	public String printAfterSalePackageInfo(HttpServletRequest request, HttpServletResponse response){
		String result = "/admin/afStock/printAfterSalePackage";
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			String packageCode = StringUtil.checkNull(request.getParameter("packageCode"));
			String postCode = StringUtil.checkNull(request.getParameter("postCode"));
			if(packageCode.length()>0){
				//包裹信息
				AfterSaleBackUserPackage backPackage = afStockService.getAfterSaleBackUserPackage("package_code='" + packageCode + "'");
				int deliverId = backPackage.getDeliverId();
				if(SysDict.deliverMap.containsKey(deliverId)){
					backPackage.setDeliverName(SysDict.deliverMap.get(deliverId));
				}
				backPackage.setCustomerPostCode(postCode);
				backPackage.setPrintTime(DateUtil.getNowDateStr());
				request.setAttribute("backPackage", backPackage);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return result;
	}
	
	/**
	 * 获取仓内作业合格时间设置列表
	 * @author lining
	 */
	@RequestMapping("/getQualifiedTimeList")
	@ResponseBody
	public EasyuiDataGridBean getQualifiedTimeList(HttpServletRequest request,HttpServletResponse response)throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("msg", "开始时间不能晚于结束时间!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			List<AfterSaleWareJobQualifiedTime> rows = afStockService.getAfterSaleWareJobQualifiedTimeList(null,-1,-1,"id desc");
			if(rows!=null && rows.size()>0){
				for(int i=0;i<rows.size();i++){
					AfterSaleWareJobQualifiedTime bean = rows.get(i);
					bean.setQualifiedTimeStr(DateUtil.formatDiff(bean.getQualifiedTime()));
				}
			}
			datagrid.setRows(rows);
			int total = afStockService.getAfterSaleWareJobQualifiedTimeCount(null);
			datagrid.setTotal((long) total);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 获取仓内作业合格时间设置的操作日志
	 * @author lining
	 */
	@RequestMapping("/getQualifiedTimeLog")
	@ResponseBody
	public EasyuiDataGridBean getQualifiedTimeLog(HttpServletRequest request,HttpServletResponse response,String qualifiedTimeId)throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("msg", "开始时间不能晚于结束时间!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			int id = StringUtil.StringToId(StringUtil.checkNull(qualifiedTimeId));
			List<AfterSaleWareJobQualifiedTimeLog> rows = afStockService.getAfterSaleWareJobQualifiedTimeLogList("operation_qualified_time_id=" + id,-1,-1,"id desc");
			datagrid.setRows(rows);
			long total = 0;
			if(rows!=null && rows.size()>0){
				total = (long)rows.size();
			}
			datagrid.setTotal((long) total);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return datagrid;
	}
	
	/**
	 * 编辑仓内作业合格时间
	 * @param day
	 * @param hour
	 * @param second
	 * @param qualifiedTimeId
	 * @return
	 * @author lining
	 */
	@RequestMapping("/editQualifiedTime")
	@ResponseBody
	public Json editQualifiedTime(HttpServletRequest request,HttpServletResponse response,
			String day,String hour,String second,String qualifiedTimeId){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		if(!user.getGroup().isFlag(2205)){
			j.setMsg("没有仓内作业合格时间设置的权限,操作失败！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			int id = StringUtil.StringToId(StringUtil.checkNull(qualifiedTimeId));
			AfterSaleWareJobQualifiedTime bean = afStockService.getAfterSaleWareJobQualifiedTime("id=" + id);
			if(bean == null){
				j.setMsg("没有对应的仓内作业合格时间记录!");
				return j;
			}
			long oriQualifiedTime = bean.getQualifiedTime();
			int dayNum = StringUtil.StringToId(StringUtil.checkNull(day));
			int hourNum = StringUtil.StringToId(StringUtil.checkNull(hour));
			int secondNum = StringUtil.StringToId(StringUtil.checkNull(second));
			long time = dayNum * 24 * 60 * 60 + hourNum * 60 * 60 + secondNum * 60; //获得秒数
			dbOp.startTransaction();
			if(!afStockService.editAfterSaleWareJobQualifiedTime("qualified_time=" + time,"id="+id)){
				dbOp.rollbackTransaction();
				j.setMsg("更新仓内作业合格时间记录失败!");
				return j;
			};
			AfterSaleWareJobQualifiedTimeLog log = new AfterSaleWareJobQualifiedTimeLog();
			log.setOperateTime(DateUtil.getNow());
			log.setUserId(user.getId());
			log.setUsername(user.getUsername());
			log.setOperationQualifiedTimeId(id);
			log.setRemark("合格时间由" + DateUtil.formatDiff(oriQualifiedTime) + "变为" + DateUtil.formatDiff(time));
			if(!afStockService.addAfterSaleWareJobQualifiedTimeLog(log)){
				dbOp.rollbackTransaction();
				j.setMsg("新增仓内作业合格时间日志失败!");
				return j;
			}
			dbOp.commitTransaction();
			j.setMsg("更新成功!");
			j.setSuccess(true);
		}catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	
	
	/**
	 * 查询售后周期耗时
	 * @param request
	 * @param response
	 * @param easyuiPage
	 * @return
	 */
	@RequestMapping("/getAfterSaleCycle")
	@ResponseBody
	public EasyuiDataGridBean getAfterSaleCycle(HttpServletRequest request, HttpServletResponse response,EasyuiDataGrid easyuiPage){
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		String afterSaleOrderCode = StringUtil.checkNull(request.getParameter("afterSaleOrderCode"));
		String afterSaleDetectProductCode = StringUtil.checkNull(request.getParameter("afterSaleDetectProductCode"));
		int afterSaleType = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("afterSaleType")));
		try{
			if(startTime!=null && startTime.trim().length()>0 && endTime!=null && endTime.length()>0){
				int index = DateUtil.compareDate(endTime,startTime);
				if(index<0){
					request.setAttribute("msg", "开始时间不能晚于结束时间!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}else{
					index = DateUtil.getDaySubReplace(startTime, endTime);
					if(index>30){
						request.setAttribute("msg", "查询时间段不能超过30天!");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}else{
						StringBuilder condition = new StringBuilder();
						condition.append(" after_sale_create_datetime between '").append(startTime).append(" 00:00:00' and '")
							.append(endTime).append(" 23:59:59' ");
						if(afterSaleOrderCode!=null && afterSaleOrderCode.trim().length()>0){
							condition.append(" and after_sale_order_code='").append(StringUtil.toSql(afterSaleOrderCode.trim())).append("'");
						}
						if(afterSaleDetectProductCode!=null && afterSaleDetectProductCode.trim().length()>0){
							condition.append(" and after_sale_detect_product_code='").append(StringUtil.toSql(afterSaleDetectProductCode.trim())).append("'");
						}
						if(afterSaleType>0){
							condition.append(" and after_sale_type=").append(afterSaleType);
						}
						String sort = easyuiPage.getSort();
						if(sort!=null && sort.length()>0 && sort.endsWith("Str")){
							sort = sort.substring(0, sort.length()-3);
							sort = StringUtil.underscoreName(sort);
						}else{
							sort = "id";
						}
						String order = "asc";
						order = easyuiPage.getOrder();
						List<AfterSaleCycleStatBean> rows = afStockService.getAfterSaleCycleStatBeanList(condition.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), sort + " " + order);
						if(rows!=null && rows.size()>0){
							for(int i=0;i<rows.size();i++){
								AfterSaleCycleStatBean bean = rows.get(i);
								bean.setTotalConsumingStr(DateUtil.formatDiff(bean.getTotalConsuming()));
								bean.setCustomerReturnConsumingStr(DateUtil.formatDiff(bean.getCustomerReturnConsuming()));
								bean.setPreAfterSaleConsumingStr(DateUtil.formatDiff(bean.getPreAfterSaleConsuming()));
								bean.setCustomerConfirmConsumingStr(DateUtil.formatDiff(bean.getCustomerConfirmConsuming()));
								bean.setConfirmCostsConsumingStr(DateUtil.formatDiff(bean.getConfirmCostsConsuming()));
								bean.setsApplyDeliveryConsumingStr(DateUtil.formatDiff(bean.getsApplyDeliveryConsuming()));
								bean.setQualitySupportConsumingStr(DateUtil.formatDiff(bean.getQualitySupportConsuming()));
								bean.setMatchPackageConsumingStr(DateUtil.formatDiff(bean.getMatchPackageConsuming()));
								bean.setDetectConsumingStr(DateUtil.formatDiff(bean.getDetectConsuming()));
								bean.setEnterAfterSaleStockConsumingStr(DateUtil.formatDiff(bean.getEnterAfterSaleStockConsuming()));
								bean.setAfterSaleShippingConsumingStr(DateUtil.formatDiff(bean.getAfterSaleShippingConsuming()));
								bean.setBackSupplierConsumingStr(DateUtil.formatDiff(bean.getBackSupplierConsuming()));
								bean.setFinancialRefundConsumingStr(DateUtil.formatDiff(bean.getFinancialRefundConsuming()));
								bean.setRepairsConsumingStr(DateUtil.formatDiff(bean.getRepairsConsuming()));
								bean.setCustomerPayMoneyConsumingStr(DateUtil.formatDiff(bean.getCustomerPayMoneyConsuming()));
								bean.setsShippingConsumingStr(DateUtil.formatDiff(bean.getsShippingConsuming()));
							}
						}
						int total = afStockService.getAfterSaleCycleStatBeanCount(condition.toString());
						datagrid.setTotal((long)total);
						datagrid.setRows(rows);
					}
				}
			}else{
				request.setAttribute("msg", "请输入创建开始或结束时间!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return datagrid;
	}
	
	/**
	 * 售后耗时分布图
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/afterSaleConsumingDistributed")
	@ResponseBody
	public Json afterSaleConsumingDistributed(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		int afterSaleType = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("afterSaleType")));
		try{
			if(startTime!=null && startTime.trim().length()>0 && endTime!=null && endTime.length()>0){
				int index = DateUtil.compareDate(endTime,startTime);
				if(index<0){
					request.setAttribute("msg", "开始时间不能晚于结束时间!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}else{
					index = DateUtil.getDaySubReplace(startTime, endTime);
					if(index>30){
						request.setAttribute("msg", "查询时间段不能超过30天!");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}else{
						List<Object> returnList = new ArrayList<Object>();
						long num1 = 4 * 60 * 60;
						long num2 = 8 * 60 * 60;
						long num3 = 2 * 24 * 60 * 60;
						long num4 = 5 * 24 * 60 * 60;
						long num5 = 35 * 24 * 60 * 60;
						
						StringBuilder condition = new StringBuilder();
						condition.append(" and after_sale_create_datetime between '").append(startTime).append(" 00:00:00' and '")
							.append(endTime).append(" 23:59:59' ");
						if(afterSaleType>0){
							condition.append(" and after_sale_type=" + afterSaleType);
						}
						int count1 = afStockService.getAfterSaleCycleStatBeanCount("total_consuming>0 and total_consuming<="+num1 + condition.toString());
						int count2 = afStockService.getAfterSaleCycleStatBeanCount("total_consuming>"+ num1 +" and total_consuming<="+num2 + condition.toString());
						int count3 = afStockService.getAfterSaleCycleStatBeanCount("total_consuming>"+ num2 +" and total_consuming<="+num3 + condition.toString());
						int count4 = afStockService.getAfterSaleCycleStatBeanCount("total_consuming>"+ num3 +" and total_consuming<="+num4 + condition.toString());
						int count5 = afStockService.getAfterSaleCycleStatBeanCount("total_consuming>"+ num4 +" and total_consuming<"+num5 + condition.toString());
						int count6 = afStockService.getAfterSaleCycleStatBeanCount("total_consuming>"+ num5 + condition.toString());
						int[] amounts = new int[]{count1,count2,count3,count4,count5,count6};					
						returnList.add(amounts);
						
						float[] percents = new float[amounts.length];
						int sumCount = count1 + count2 + count3 + count4 + count5 + count6;
						if(sumCount>0){
							for(int i=0;i<amounts.length;i++){
								percents[i] = Arith.div(amounts[i]*100, sumCount, 1);
							}
						}
						returnList.add(percents);
						json.setObj(returnList);
						json.setMsg(String.valueOf(sumCount));
						json.setSuccess(true);
						return json;
					}
				}
			}else{
				request.setAttribute("msg", "请输入创建开始或结束时间!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return json;
	}	
	
	/**
	 * 客服平均耗时
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getAverageCustomerConsuming")
	@ResponseBody
	public Json getAverageCustomerConsuming(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		int afterSaleType = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("afterSaleType")));
		try{
			if(startTime!=null && startTime.trim().length()>0 && endTime!=null && endTime.length()>0){
				int index = DateUtil.compareDate(endTime,startTime);
				if(index<0){
					request.setAttribute("msg", "开始时间不能晚于结束时间!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}else{
					index = DateUtil.getDaySubReplace(startTime, endTime);
					if(index>30){
						request.setAttribute("msg", "查询时间段不能超过30天!");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}else{
						List<Object> returnList = new ArrayList<Object>();
						
						StringBuilder condition = new StringBuilder();
						condition.append("after_sale_create_datetime between '").append(startTime).append(" 00:00:00' and '")
							.append(endTime).append(" 23:59:59' ");
						if(afterSaleType>0){
							condition.append(" and after_sale_type=" + afterSaleType);
						}

						float preAfterSaleConsumingAve = 0;
						float customerConfirmConsumingAve = 0;
						float confirmCostsConsumingAve = 0;
						float sApplyDeliveryConsumingAve = 0;
						
						float preAfterSaleConsumingMax = 0;
						float customerConfirmConsumingMax = 0;
						float confirmCostsConsumingMax = 0;
						float sApplyDeliveryConsumingMax = 0;
						
						float preAfterSaleConsumingMin = 0;
						float customerConfirmConsumingMin = 0;
						float confirmCostsConsumingMin = 0;
						float sApplyDeliveryConsumingMin = 0;
						
						List<AfterSaleCycleStatBean> list = afStockService.getAfterSaleCycleStatBeanList(condition.toString(), -1, -1, "id");
						if(list!=null && list.size()>0){
							int len = list.size();
							int divisor= list.size() * 60 * 60;
							long preAfterSaleConsumingSum = 0;
							long customerConfirmConsumingSum = 0;
							long confirmCostsConsumingSum = 0;
							long sApplyDeliveryConsumingSum = 0;
							for(int i=0;i<list.size();i++){
								AfterSaleCycleStatBean bean = list.get(i);
								preAfterSaleConsumingSum += bean.getPreAfterSaleConsuming();
								customerConfirmConsumingSum += bean.getCustomerConfirmConsuming();
								confirmCostsConsumingSum += bean.getConfirmCostsConsuming();
								sApplyDeliveryConsumingSum += bean.getsApplyDeliveryConsuming();
							}
							preAfterSaleConsumingAve = Arith.div(preAfterSaleConsumingSum, divisor,1);
							customerConfirmConsumingAve = Arith.div(customerConfirmConsumingSum,divisor ,1);
							confirmCostsConsumingAve = Arith.div(confirmCostsConsumingSum,divisor ,1);
							sApplyDeliveryConsumingAve = Arith.div(sApplyDeliveryConsumingSum, divisor,1);
							sort(list,"getPreAfterSaleConsuming","desc");
							preAfterSaleConsumingMax = Arith.div(list.get(0).getPreAfterSaleConsuming(), 60*60,1);
							preAfterSaleConsumingMin = Arith.div(list.get(len-1).getPreAfterSaleConsuming(), 60*60,1);
							
							sort(list,"getCustomerConfirmConsuming","desc");
							customerConfirmConsumingMax = Arith.div(list.get(0).getCustomerConfirmConsuming(), 60*60,1);
							customerConfirmConsumingMin = Arith.div(list.get(len-1).getCustomerConfirmConsuming(), 60*60,1);
							
							sort(list,"getConfirmCostsConsuming","desc");
							confirmCostsConsumingMax = Arith.div(list.get(0).getCustomerConfirmConsuming(), 60*60,1);
							confirmCostsConsumingMin = Arith.div(list.get(len-1).getConfirmCostsConsuming(), 60*60,1);
							
							sort(list,"getsApplyDeliveryConsuming","desc");
							sApplyDeliveryConsumingMax = Arith.div(list.get(0).getsApplyDeliveryConsuming(), 60*60,1);
							sApplyDeliveryConsumingMin = Arith.div(list.get(len-1).getsApplyDeliveryConsuming(), 60*60,1);
						}
						float[] aveConsumint = new float[]{preAfterSaleConsumingAve,customerConfirmConsumingAve,confirmCostsConsumingAve,sApplyDeliveryConsumingAve};
						float[] maxConsuming = new float[]{preAfterSaleConsumingMax,customerConfirmConsumingMax,confirmCostsConsumingMax,sApplyDeliveryConsumingMax};
						float[] minConsuming = new float[]{preAfterSaleConsumingMin,customerConfirmConsumingMin,confirmCostsConsumingMin,sApplyDeliveryConsumingMin};
						returnList.add(aveConsumint);
						returnList.add(maxConsuming);
						returnList.add(minConsuming);
						json.setObj(returnList);
						json.setSuccess(true);
						return json;
					}
				}
			}else{
				request.setAttribute("msg", "请输入创建开始或结束时间!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return json;
	}	
	
	/**
	 * 售后仓内平均耗时
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getAverageAfterSaleStockConsuming")
	@ResponseBody
	public Json getAverageAfterSaleStockConsuming(HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		int afterSaleType = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("afterSaleType")));
		try{
			if(startTime!=null && startTime.trim().length()>0 && endTime!=null && endTime.length()>0){
				int index = DateUtil.compareDate(endTime,startTime);
				if(index<0){
					request.setAttribute("msg", "开始时间不能晚于结束时间!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}else{
					index = DateUtil.getDaySubReplace(startTime, endTime);
					if(index>30){
						request.setAttribute("msg", "查询时间段不能超过30天!");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}else{
						List<Object> returnList = new ArrayList<Object>();
						
						StringBuilder condition = new StringBuilder();
						condition.append("after_sale_create_datetime between '").append(startTime).append(" 00:00:00' and '")
							.append(endTime).append(" 23:59:59' ");
						if(afterSaleType>0){
							condition.append(" and after_sale_type=" + afterSaleType);
						}

						float qualitySupportConsumingAve = 0;
						float matchPackageConsumingAve = 0;
						float detectConsumingAve = 0;
						float enterAfterSaleStockConsumingAve = 0;
						float afterSaleShippingConsumingAve = 0;
						float backSupplierConsumingAve = 0;
						
						float qualitySupportConsumingMax = 0;
						float matchPackageConsumingMax = 0;
						float detectConsumingMax = 0;
						float enterAfterSaleStockConsumingMax = 0;
						float afterSaleShippingConsumingMax = 0;
						float backSupplierConsumingMax = 0;
						
						float qualitySupportConsumingMin = 0;
						float matchPackageConsumingMin = 0;
						float detectConsumingMin = 0;
						float enterAfterSaleStockConsumingMin = 0;
						float afterSaleShippingConsumingMin = 0;
						float backSupplierConsumingMin = 0;
						
						List<AfterSaleCycleStatBean> list = afStockService.getAfterSaleCycleStatBeanList(condition.toString(), -1, -1, "id");
						if(list!=null && list.size()>0){
							int len = list.size();
							int divisor= list.size() * 60 * 60;
							long qualitySupportConsumingSum = 0;
							long matchPackageConsumingSum = 0;
							long detectConsumingSum = 0;
							long enterAfterSaleStockConsumingSum = 0;
							long afterSaleShippingConsumingSum = 0;
							long backSupplierConsumingSum = 0;
							for(int i=0;i<list.size();i++){
								AfterSaleCycleStatBean bean = list.get(i);
								qualitySupportConsumingSum += bean.getQualitySupportConsuming();
								matchPackageConsumingSum += bean.getMatchPackageConsuming();
								detectConsumingSum += bean.getDetectConsuming();
								enterAfterSaleStockConsumingSum += bean.getEnterAfterSaleStockConsuming();
								afterSaleShippingConsumingSum += bean.getAfterSaleShippingConsuming();
								backSupplierConsumingSum += bean.getBackSupplierConsuming();
							}
							qualitySupportConsumingAve = Arith.div(qualitySupportConsumingSum, divisor,1);
							matchPackageConsumingAve = Arith.div(matchPackageConsumingSum,divisor ,1);
							detectConsumingAve = Arith.div(detectConsumingSum,divisor ,1);
							enterAfterSaleStockConsumingAve = Arith.div(enterAfterSaleStockConsumingSum,divisor ,1);
							afterSaleShippingConsumingAve = Arith.div(afterSaleShippingConsumingSum, divisor,1);
							backSupplierConsumingAve = Arith.div(backSupplierConsumingSum, divisor,1);
							sort(list,"getQualitySupportConsuming","desc");
							qualitySupportConsumingMax = Arith.div(list.get(0).getQualitySupportConsuming(), 60*60,1);
							qualitySupportConsumingMin = Arith.div(list.get(len-1).getQualitySupportConsuming(), 60*60,1);
							
							sort(list,"getMatchPackageConsuming","desc");
							matchPackageConsumingMax = Arith.div(list.get(0).getMatchPackageConsuming(), 60*60,1);
							matchPackageConsumingMin = Arith.div(list.get(len-1).getMatchPackageConsuming(), 60*60,1);
							
							sort(list,"getDetectConsuming","desc");
							detectConsumingMax = Arith.div(list.get(0).getDetectConsuming(), 60*60,1);
							detectConsumingMin = Arith.div(list.get(len-1).getDetectConsuming(), 60*60,1);
							
							sort(list,"getEnterAfterSaleStockConsuming","desc");
							enterAfterSaleStockConsumingMax = Arith.div(list.get(0).getEnterAfterSaleStockConsuming(), 60*60,1);
							enterAfterSaleStockConsumingMin = Arith.div(list.get(len-1).getEnterAfterSaleStockConsuming(), 60*60,1);
							
							sort(list,"getAfterSaleShippingConsuming","desc");
							afterSaleShippingConsumingMax = Arith.div(list.get(0).getAfterSaleShippingConsuming(), 60*60,1);
							afterSaleShippingConsumingMin = Arith.div(list.get(len-1).getAfterSaleShippingConsuming(), 60*60,1);
							
							sort(list,"getBackSupplierConsuming","desc");
							backSupplierConsumingMax = Arith.div(list.get(0).getBackSupplierConsuming(), 60*60,1);
							backSupplierConsumingMin = Arith.div(list.get(len-1).getBackSupplierConsuming(), 60*60,1);
						}
						float[] aveConsumint = new float[]{qualitySupportConsumingAve,matchPackageConsumingAve,detectConsumingAve,enterAfterSaleStockConsumingAve,afterSaleShippingConsumingAve,backSupplierConsumingAve};
						float[] maxConsuming = new float[]{qualitySupportConsumingMax,matchPackageConsumingMax,detectConsumingMax,enterAfterSaleStockConsumingMax,afterSaleShippingConsumingMax,backSupplierConsumingMax};
						float[] minConsuming = new float[]{qualitySupportConsumingMin,matchPackageConsumingMin,detectConsumingMin,enterAfterSaleStockConsumingMin,afterSaleShippingConsumingMin,backSupplierConsumingMin};
						returnList.add(aveConsumint);
						returnList.add(maxConsuming);
						returnList.add(minConsuming);
						json.setObj(returnList);
						json.setSuccess(true);
						return json;
					}
				}
			}else{
				request.setAttribute("msg", "请输入创建开始或结束时间!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return json;
	}	
	
	/**
	 * 排序
	 *@author 李宁
	 *@date 2013-8-28 下午6:51:16
	 * @param list
	 * @param method
	 * @param sort
	 */
	@SuppressWarnings("unchecked")
	public void sort(List<AfterSaleCycleStatBean> list, final String method, final String sort){  
        Collections.sort(list, new Comparator() {             
            public int compare(Object obj1, Object obj2) {  
                int index = 0;  
                try{  
                    Method m1 = ((AfterSaleCycleStatBean)obj1).getClass().getMethod(method, null);  
                    Method m2 = ((AfterSaleCycleStatBean)obj2).getClass().getMethod(method, null);  
                    long value1 = ((Long)m2.invoke(((AfterSaleCycleStatBean)obj2), null)).longValue();
                	long value2 = ((Long)m1.invoke(((AfterSaleCycleStatBean)obj1), null)).longValue();
					if(sort != null && "desc".equals(sort)){//倒序 
                    	if(value1>value2){
                    		index = 1;
                    	}else if(value1==value2){
                    		index = 0;
                    	}else{
                    		index = -1;
                    	}
					}else{//正序  
                        if(value1>value2){
                        	index = -1;
                        }else if(value1==value2){
                        	index = 0;
                        }else{
                        	index = 1;
                        }
					}
                }catch(NoSuchMethodException ne){  
                    ne.printStackTrace();
                }catch(IllegalAccessException ie){  
                    ie.printStackTrace();
                }catch(InvocationTargetException it){  
                    it.printStackTrace();
                }  
                return index;  
            }  
         });  
    }
	
	/**
	 * 未完结售后单分布图
	 * @author lining
	 * @return
	 */
	@RequestMapping("/getAfterSaleUnfinishedOrder")
	@ResponseBody
	public Json getAfterSaleUnfinishedOrder(){
		Json j = new Json();
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		ResultSet rs = null;
		 int waitCustomerReturnCount = 0;//等待用户寄回
		 int customerPayCount = 0;//用户打款中
		 int afterSaleContactCount = 0;//售后联系中
		 int waitCustomerConfirmCount = 0;//等待客户确认
		 int waitsApplyDeliveryCount = 0;//s单待发货
		 int waitConfirmCostsCount = 0;//待重新申请确认费用
		 int qualitySupportCount = 0;//质检支撑中
		 int waitMatchCount = 0;//待匹配
		 int waitDetectCount = 0;//待检测
		 int waitEnterAfterSaleStockCount = 0;//待入售后库
		 int afterSaleWaitShippingCount = 0;//售后待发货
		 int waitBackSupplierCount = 0;//待返厂
		 int financialRefundCount = 0;//财务退款中
		 int repairCount = 0;//维修中
		 int sShippingCount = 0;//s单发货中
		try{
			 rs = db.executeQuery("select status,count(id) from after_sale_order where status in (0,1,2) group by status");
			 while(rs!=null && rs.next()){
				 int status = rs.getInt(1);
				 if(status==0){
					 qualitySupportCount = rs.getInt(2);
				 }else if(status==1){
					 afterSaleContactCount = rs.getInt(2);
				 }else{
					 waitCustomerReturnCount = rs.getInt(2);
				 }
			 }
			 rs.close();
			 
			 String sql = "select count(id) from after_sale_warehource_product_records where status=2";
			 waitCustomerConfirmCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(aso.id) from after_sale_order aso join after_sale_charge_list ascl on aso.id=ascl.after_sale_order_id " +
			 		"where pay_to=2 and ascl.state=4";
			 customerPayCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(aso.id) from after_sale_order aso join after_sale_charge_list ascl on aso.id=ascl.after_sale_order_id " +
				 		"where ascl.state=6";
			 waitConfirmCostsCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_operation_list where state=2";
			 waitsApplyDeliveryCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_detect_package where `status`=0";
			 waitMatchCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_detect_product where status in (0,1)";
			 waitDetectCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_back_user_product where package_id=0";
			 afterSaleWaitShippingCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_stockin where type in(1,2) and status = 0";
			 waitEnterAfterSaleStockCount = getCount(sql,db,rs);
			 rs.close();
			
			 sql = "select count(id) from after_sale_back_supplier_product where `status`=3";
			 waitBackSupplierCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_back_supplier_product where `status` in (0,4)";
			 repairCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(aso.id) from after_sale_order aso join after_sale_charge_list ascl on aso.id=ascl.after_sale_order_id " +
				 		"where pay_to=1 and ascl.state=4";
			 financialRefundCount = getCount(sql,db,rs);
			 rs.close();
			 
			 sql = "select count(id) from after_sale_operation_list where state=4";
			 sShippingCount = getCount(sql,db,rs);
			 rs.close();
			 
			 int[] result = new int[]{
				waitCustomerReturnCount,customerPayCount,afterSaleContactCount,waitCustomerConfirmCount,
				waitsApplyDeliveryCount,waitConfirmCostsCount,qualitySupportCount,waitMatchCount,waitDetectCount,
				waitEnterAfterSaleStockCount,afterSaleWaitShippingCount,waitBackSupplierCount,financialRefundCount,
				repairCount,sShippingCount
			 };
			 j.setSuccess(true);
			 j.setObj(result);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.release();
		}
		return j;
	}
	
	private int getCount(String sql,DbOperation db,ResultSet rs){
		int result = 0;
		rs = db.executeQuery(sql);
		 try {
			if(rs!=null && rs.next()){
				 result = rs.getInt(1);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result; 
	}
	
	/**
	 * 仓内作业达标率
	 * @param request
	 * @param response
	 * @return
	 * @author lining
	 */
	@RequestMapping("/getComplianceRate")
	@ResponseBody
	public Json getComplianceRate(HttpServletRequest request, HttpServletResponse response){
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		try{
			if(startTime!=null && startTime.trim().length()>0 && endTime!=null && endTime.length()>0){
				int index = DateUtil.compareDate(endTime,startTime);
				if(index<0){
					j.setMsg("开始时间不能晚于结束时间!");
					return j;
				}else{
					index = DateUtil.getDaySubReplace(startTime, endTime);
					if(index>30){
						j.setMsg("查询时间段不能超过30天!");
						return j;
					}
					StringBuilder condition = new StringBuilder();
					condition.append(" after_sale_create_datetime between '").append(startTime).append(" 00:00:00' and '")
						.append(endTime).append(" 23:59:59' ");
					
					List<Object> result = new ArrayList<Object>();
					int matchCountQualified = 0;
					int matchCountUnQualified = 0;
					int detectCountQualified = 0;
					int detectCountUnQualified = 0;
					int backUserCountQualified = 0;
					int backUserCountUnQualified = 0;
					int backSupplierCountQualified = 0;
					int backSupplierCountUnQualified = 0;
					int enterAfterSaleStockCountQualified = 0;
					int enterAfterSaleStockCountUnQualified = 0;
					int repairCountQualified = 0;
					int repairCountUnQualified = 0;
					List<AfterSaleCycleStatBean> rows = afStockService.getAfterSaleCycleStatBeanList(condition.toString(), -1,-1, "id");
					if(rows!=null && rows.size()>0){
						List<AfterSaleWareJobQualifiedTime> timeList = afStockService.getAfterSaleWareJobQualifiedTimeList(null, -1, -1, "id");
						if(timeList!=null&&timeList.size()>0){
							for(int i=0;i<rows.size();i++){
								AfterSaleCycleStatBean bean = rows.get(i);
								long diff = bean.getMatchPackageConsuming() - timeList.get(0).getQualifiedTime();
								if(diff<0){
									matchCountQualified++;
								}else{
									matchCountUnQualified++;
								}
								
								diff = bean.getDetectConsuming() - timeList.get(1).getQualifiedTime();
								if(diff<0){
									detectCountQualified++;
								}else{
									detectCountUnQualified++;
								}
								
								diff = bean.getAfterSaleShippingConsuming() - timeList.get(2).getQualifiedTime();
								if(diff<0){
									backUserCountQualified++;
								}else{
									backUserCountUnQualified++;
								}
								
								diff = bean.getBackSupplierConsuming() - timeList.get(3).getQualifiedTime();
								if(diff<0){
									backSupplierCountQualified++;
								}else{
									backSupplierCountUnQualified++;
								}
								
								diff = bean.getEnterAfterSaleStockConsuming() - timeList.get(4).getQualifiedTime();
								if(diff<0){
									enterAfterSaleStockCountQualified++;
								}else{
									enterAfterSaleStockCountUnQualified++;
								}
								
								diff = bean.getRepairsConsuming() - timeList.get(5).getQualifiedTime();
								if(diff<0){
									repairCountQualified++;
								}else{
									repairCountUnQualified++;
								}
							}
						}else{
							j.setMsg("需要设置仓内作业合格时间");
							return j;
						}
					}
					//合格数据
					int[] qualifiedAmount = new int[]{
							matchCountQualified,detectCountQualified,backUserCountQualified,
							backSupplierCountQualified,enterAfterSaleStockCountQualified,repairCountQualified};
					result.add(qualifiedAmount);
					//不合格数据
					int[] unQualifiedAmount = new int[]{
							matchCountUnQualified,detectCountUnQualified,backUserCountUnQualified,
							backSupplierCountUnQualified,enterAfterSaleStockCountUnQualified,repairCountUnQualified};
					result.add(unQualifiedAmount);
					//合格率数据
					float[] percentAmount = new float[]{
						Arith.div(matchCountQualified*100, matchCountQualified+matchCountUnQualified,1),	
						Arith.div(detectCountQualified*100, detectCountQualified+detectCountUnQualified,1),
						Arith.div(backUserCountQualified*100, backUserCountQualified+backUserCountUnQualified,1),
						Arith.div(backSupplierCountQualified*100, backSupplierCountQualified+backSupplierCountUnQualified,1),
						Arith.div(enterAfterSaleStockCountQualified*100, enterAfterSaleStockCountQualified+enterAfterSaleStockCountUnQualified,1),
						Arith.div(repairCountQualified*100, repairCountQualified+repairCountUnQualified,1)
					};
					result.add(percentAmount);
					j.setObj(result);
					j.setSuccess(true);
					return j;
				}
			}else{
				j.setMsg("请输入创建开始或结束时间!");
				return j;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			afStockService.releaseAll();
		}
		return j;
	}
	
	/**
	 * 新增售后库的报溢单
	 * @param request
	 * @param response
	 * @return
	 * 2014-11-12
	 * lining
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/addAfterSaleByOperationRecord")
	@ResponseBody
	public Json addAfterSaleByOperationRecord(HttpServletRequest request, HttpServletResponse response,String reason,int areaId){
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		j.setMsg("当前没有登录，操作失败！");
            return j;
    	}
    	if(!user.getGroup().isFlag(3025)){
    		j.setMsg("没有新建售后库报溢单权限!");
            return j;
    	}
		List<AfterSaleBsbyProduct> list = (List<AfterSaleBsbyProduct>) request.getSession().getAttribute("list");
    	DbOperation db = new DbOperation();
    	db.init(DbOperation.DB);
    	AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,db);
    	IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, db);
    	IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, db);
    	ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
    	IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,db);
    	WareService wareService = new WareService(db);
    	try{
    		synchronized (request.getSession().getId()) {
    			if(areaId==-1){
            		j.setMsg("请选择库地区!");
            		return j;
            	}
    			reason = StringUtil.checkNull(reason).trim();
    			if(reason.equals("")){
    				j.setMsg("请输入报溢原因!");
    				return j;
    			}
    			if(list!=null && list.size()>0){
            		db.startTransaction();
            		//生成报溢单
            		String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
            		String receipts_number = ByBsAction.createCode(code);
            		String title = "创建新的报溢表" + receipts_number;// 日志的内容
            		
            		String nowTime = DateUtil.getNow();
            		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
            		bsbyOperationnoteBean.setAdd_time(nowTime);
            		bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.audit_ing);
            		bsbyOperationnoteBean.setOperator_id(user.getId());
            		bsbyOperationnoteBean.setOperator_name(user.getUsername());
            		bsbyOperationnoteBean.setReceipts_number(receipts_number);
            		bsbyOperationnoteBean.setWarehouse_area(areaId);
            		bsbyOperationnoteBean.setWarehouse_type(ProductStockBean.STOCKTYPE_AFTER_SALE);
            		bsbyOperationnoteBean.setType(BsbyOperationnoteBean.TYPE1);
            		bsbyOperationnoteBean.setIf_del(0);
            		bsbyOperationnoteBean.setFinAuditId(0);
            		bsbyOperationnoteBean.setFinAuditName("");
            		bsbyOperationnoteBean.setFinAuditRemark("");
            		bsbyOperationnoteBean.setRemark(StringUtil.checkNull(reason).trim());
            		
            		int maxid = bsbyService.getNumber("id", "bsby_operationnote", "max", "id > 0");
            		bsbyOperationnoteBean.setId(maxid + 1);
            		boolean falg = bsbyService.addBsbyOperationnoteBean(bsbyOperationnoteBean);
            		if (falg) {
            			request.setAttribute("opid", Integer.valueOf(bsbyOperationnoteBean.getId()));// 添加成功将id传到下个页面
            			int bsbyOperationnoteId = bsbyOperationnoteBean.getId();
            			// 添加操作日志
            			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
            			bsbyOperationRecordBean.setOperator_id(user.getId());
            			bsbyOperationRecordBean.setOperator_name(user.getUsername());
            			bsbyOperationRecordBean.setTime(nowTime);
            			bsbyOperationRecordBean.setInformation(title);
            			bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteId);
            			boolean b = bsbyService.addBsbyOperationRecord(bsbyOperationRecordBean);
            			if(!b){
            				afStockService.getDbOp().rollbackTransaction();
            				j.setMsg("添加报损报溢单操作日志失败！");
            				return j;
            			}
            		} else {
            			afStockService.getDbOp().rollbackTransaction();
            			j.setMsg("添加报损报溢单操作失败!");
            			return j;
            		}
            		//报溢商品
            		HashMap<Integer,List<AfterSaleBsbyProduct>> byProductMap = new HashMap<Integer,List<AfterSaleBsbyProduct>>();
        			CargoInfoBean cib = null;//货位
            		for(int i=0;i<list.size();i++){
            			AfterSaleBsbyProduct bean = list.get(i);
            			//报溢单商品
            			if(byProductMap.containsKey(bean.getProductId())){
        					List<AfterSaleBsbyProduct> bsbyList = byProductMap.get(bean.getProductId());
        					bsbyList.add(bean);
        				}else{
        					List<AfterSaleBsbyProduct> bsbyList = new ArrayList<AfterSaleBsbyProduct>();
        					bsbyList.add(bean);
        					byProductMap.put(bean.getProductId(), bsbyList);
        				}
            			
            			String result = dealImeiBean(areaId, user, imeiService,bean,bsbyOperationnoteBean.getReceipts_number());
                		if(result!=null && StringUtil.checkNull(result).trim().length()>0){
                			j.setMsg(result);
                			return j;
                		}
            			if(cib==null){
            				cib = cargoService.getCargoInfo("area_id=" +  areaId +" and stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE + " and store_type=" + CargoInfoBean.STORE_TYPE2);
            				if(cib==null) {
            					afStockService.getDbOp().rollbackTransaction();
            					j.setMsg("未找到货位！");
            					return j;
            				}
            			}
            			int count = bean.getCount();//商品数量
            			//生成售后处理单号
                		List<String> codes = createDetectCodes(afStockService,count);
            			if(codes == null || codes.size() == 0){
            				j.setMsg("生成处理单号失败!");
            				return j;
            			}
            			for(int k=0;k<count;k++){
            				//新增售后处理单
                			AfterSaleDetectProductBean detectBean = new AfterSaleDetectProductBean();
                			detectBean.setProductId(bean.getProductId());
                			detectBean.setIMEI(bean.getImei());
                			detectBean.setAfterSaleOrderCode("");
                			detectBean.setAfterSaleOrderId(0);
                			detectBean.setStatus(AfterSaleDetectProductBean.STATUS23);
                			detectBean.setAreaId(areaId);
                			detectBean.setCargoWholeCode(cib.getWholeCode());
                			detectBean.setCode(codes.get(k));
                			detectBean.setCreateDatetime(DateUtil.getNow());
                			detectBean.setCreateUserId(user.getId());
                			detectBean.setCreateUserName(user.getUsername());
                			detectBean.setLockStatus(AfterSaleDetectProductBean.LOCK_STATUS1);
                			if(!afStockService.addAfterSaleDetectProduct(detectBean)){
                				afStockService.getDbOp().rollbackTransaction();
            					j.setMsg("新增售后处理单失败!");
            					return j;
                			}
                				
                			AfterSaleLogBean log = new AfterSaleLogBean();
                			log.setContent("售后报溢");
                			log.setCount(1);
                			log.setCreateDatetime(DateUtil.getNow());
                			log.setCreateUserId(user.getId());
                			log.setCreateUserName(user.getUsername());
                			log.setOperCode(detectBean.getCode());
                			log.setType(AfterSaleLogBean.TYPE25);
                			if(!afStockService.addAfterSaleLogBean(log)){
                				afStockService.getDbOp().rollbackTransaction();
            					j.setMsg("新增售后处理单日志失败!");
            					return j;
                			}
                				
                			//售后报损报溢商品
                			AfterSaleBsbyProduct bsbyProduct = new AfterSaleBsbyProduct();
                			bsbyProduct.setAfterSaleDetectProductCode(detectBean.getCode());
                			bsbyProduct.setAfterSaleDetectProductStatus(detectBean.getStatus());
                			bsbyProduct.setAfterSaleOrderCode("");
                			bsbyProduct.setBsbyOperationnoteId(bsbyOperationnoteBean.getId());
                			bsbyProduct.setImei(bean.getImei());
                			bsbyProduct.setProductId(bean.getProductId());
                			bsbyProduct.setWholeCode(detectBean.getCargoWholeCode());
                			if(!afStockService.addAfterSaleBsbyProduct(bsbyProduct)){
                				afStockService.getDbOp().rollbackTransaction();
            					j.setMsg("新增售后报损报溢商品失败!");
            					return j;
                			}
            			}
            		}
            		String result = addBsByProducts(bsbyService,cargoService,areaId,byProductMap,cib.getId(),bsbyOperationnoteBean.getId(),wareService,iBsByservice);
            		if(result!=null && StringUtil.checkNull(result).trim().length()>0){
        				j.setMsg(result);
        				return j;
        			}
            		db.commitTransaction();
            		 request.getSession().removeAttribute("list");
            		j.setMsg("提交审核成功!");
            		j.setSuccess(true);
            		return j;
            	}else{
            		j.setMsg("没有添加报溢商品，请添加!");
            		return j;
            	}
			}
    	}catch (Exception e) {
    		j.setMsg("添加售后库报溢单失败!");
    		afStockService.getDbOp().rollbackTransaction();
    		e.printStackTrace();
    	}finally{
    		afStockService.getDbOp().release();
    	}
    	return j;
	}
	
	/**
	 * 新增售后库报溢单商品
	 * @param bsbyService
	 * @param cargoService
	 * @param areaId
	 * @param byProductMap
	 * @param cargoId
	 * @param bsbyOpreationId
	 * @return
	 * 2014-11-12
	 * lining
	 */
	private String addBsByProducts(IBsByServiceManagerService bsbyService, ICargoService cargoService,int areaId,HashMap<Integer, 
			List<AfterSaleBsbyProduct>> byProductMap,int cargoId,int bsbyOpreationId,WareService wareService,IBsByServiceManagerService iBsByservice) {
		//新增报溢单商品
		if(byProductMap.size()>0){
			for(int productId : byProductMap.keySet()){
				List<AfterSaleBsbyProduct> byList = byProductMap.get(productId);
				int byProductCount = 0;//报溢的数量
				AfterSaleBsbyProduct byProduct = null;
				for(int i=0;i<byList.size();i++){
					byProductCount+=byList.get(i).getCount();
					if(byProduct==null){
						byProduct = byList.get(i);
					}
				}
				int x = getProductCount(byProduct.getProductId(), areaId, ProductStockBean.STOCKTYPE_AFTER_SALE);
				int result = updateProductCount(x, BsbyOperationnoteBean.TYPE1, byProductCount);
				if (result < 0 ) {
					bsbyService.getDbOp().rollbackTransaction();
					return "您所添加商品的库存不足！";
				}
				
				CargoProductStockBean cpsb = cargoService.getCargoProductStock("product_id="+byProduct.getProductId()+" and cargo_id="+cargoId);
				if (cpsb == null) {
					cpsb = new CargoProductStockBean();
					cpsb.setCargoId(cargoId);
					cpsb.setProductId(byProduct.getProductId());
					cpsb.setStockCount(0);
					cpsb.setStockLockCount(0);
					if(!cargoService.addCargoProductStock(cpsb)){
						bsbyService.getDbOp().rollbackTransaction();
						return "数据库操作失败！";
					}
					cpsb.setId(bsbyService.getDbOp().getLastInsertId());
				}
				voProduct product = wareService.getProduct(byProduct.getProductId());
				float notaxProductPrice = iBsByservice.returnFinanceProductPrice(product.getId());
				BsbyProductBean bsbyProduct = new BsbyProductBean();
				bsbyProduct.setAfter_change(result);
				bsbyProduct.setBefore_change(x);
				bsbyProduct.setProduct_id(byProduct.getProductId());
				bsbyProduct.setProduct_code(byProduct.getProductCode());
				bsbyProduct.setProduct_name(byProduct.getProductName());
				bsbyProduct.setOriname(byProduct.getProductOriname());
				bsbyProduct.setOperation_id(bsbyOpreationId);
				bsbyProduct.setBsby_count(byProductCount);
				bsbyProduct.setPrice(product.getPrice5());
				bsbyProduct.setNotaxPrice(notaxProductPrice);
				if (!bsbyService.addBsbyProduct(bsbyProduct)) {
					bsbyService.getDbOp().rollbackTransaction();
					return "添加报损报溢单商品失败！";
				}
				
				BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
				bsbyCargo.setBsbyOperId(bsbyOpreationId);
				bsbyCargo.setBsbyProductId(bsbyService.getDbOp().getLastInsertId());
				bsbyCargo.setCount(byProductCount);
				bsbyCargo.setCargoProductStockId(cpsb.getId());
				bsbyCargo.setCargoId(cpsb.getCargoId());
				if(!bsbyService.addBsbyProductCargo(bsbyCargo)) {
					bsbyService.getDbOp().rollbackTransaction();
					return "数据库操作失败";
				}
			}
		}
		return null;
	}
	/**
	 * 新增售后报溢单操作：提交审核时新增或更新imeiBean
	 * @param areaId
	 * @param user
	 * @param imeiService
	 * @param bean
	 * @param byByCode 报损报溢单code
	 * @return
	 * 2014-11-12
	 * lining
	 */
	private String dealImeiBean(int areaId, voUser user,IMEIService imeiService,AfterSaleBsbyProduct bean,String byByCode) {
		String imei = StringUtil.checkNull(bean.getImei()).trim();
		IMEIBean imeiBean = imeiService.getIMEI("code='"+imei+"'");
		if(imeiBean!=null){
			int preStatus = imeiBean.getStatus();
			if(!imeiService.updateIMEI("status="+IMEIBean.IMEISTATUS2, "id=" + imeiBean.getId())){
				imeiService.getDbOp().rollbackTransaction();
				return "更新IMEI码状态失败!";
			}
			IMEILogBean log = new IMEILogBean();
			log.setIMEI(imeiBean.getCode());
			log.setContent("售后库报溢，提交审核，IMEI码:"+imeiBean.getCode()+"状态由【"+ IMEIBean.IMEIStatusMap.get(preStatus) +"】变为【可出库】"+",地区："+ProductStockBean.areaMap.get(areaId));
			log.setCreateDatetime(DateUtil.getNow());
			log.setOperCode(byByCode);
			log.setOperType(IMEILogBean.OPERTYPE14);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			if(!imeiService.addIMEILog(log)){
				imeiService.getDbOp().rollbackTransaction();
				return "新增IMEI码日志失败!";
			}
		}else{
			//是否是IMEI码商品
			boolean hasImei = imeiService.isProductMMBMobile(bean.getProductId());
			if(hasImei){
				imeiBean = new IMEIBean();
				imeiBean.setCode(imei);
				imeiBean.setCreateDatetime(DateUtil.getNow());
				imeiBean.setProductId(bean.getProductId());
				imeiBean.setStatus(IMEIBean.IMEISTATUS2);
				if(!imeiService.addIMEI(imeiBean)){
					imeiService.getDbOp().rollbackTransaction();
					return "新增imei码失败!";
				}
				IMEILogBean log = new IMEILogBean();
				log.setIMEI(imeiBean.getCode());
				log.setContent("售后库报溢，提交审核，新增IMEI码:"+imeiBean.getCode()+"状态【可出库】"+",地区："+ProductStockBean.areaMap.get(areaId));
				log.setCreateDatetime(DateUtil.getNow());
				log.setOperCode(byByCode);
				log.setOperType(IMEILogBean.OPERTYPE14);
				log.setUserId(user.getId());
				log.setUserName(user.getUsername());
				if(!imeiService.addIMEILog(log)){
					imeiService.getDbOp().rollbackTransaction();
					return "新增IMEI码日志失败!";
				}
			}else{
				if(!imei.equals("")){
					imeiService.getDbOp().rollbackTransaction();
					return "商品编号为"+bean.getProductCode()+"的商品是非IMEI码商品,不能填写IMEI码!";
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据数量生成售后处理单号
	 * @param afStockService
	 * @param count
	 * @return
	 * 2014-11-12
	 * lining
	 */
	private List<String> createDetectCodes(AfStockService afStockService,int count){
		List<String> list = new ArrayList<String>();
		// 处理单号
		String code = DateUtil.getNow().substring(2, 10).replace("-", "");
		// 生成编号
		AfterSaleDetectProductCodeBean detect = afStockService.getAfterSaleDetectProductCode("code like '" + code
							+ "%' order by id desc limit 1");
		int number = 0;
		if (detect != null) {
			// 获取当日计划编号最大值
			String _code = detect.getCode();
			number = Integer.parseInt(_code.substring(_code.length() - 8));
		}
		for (int i = 0; i < count; i++) {
			number++;
			String detectCode = code + String.format("%08d", new Object[] { new Integer(number)});
			AfterSaleDetectProductCodeBean bean = new AfterSaleDetectProductCodeBean();
			bean.setCode(detectCode);
			if (!afStockService.addAfterSaleDetectProductCode(bean)) {
				afStockService.getDbOp().rollbackTransaction();
				return null;
			}
			list.add(detectCode);
		}
		return list;
	}
	
	/**
	 * 检查是否是IMEI码商品
	 * @author ahc
	 */
	@RequestMapping("/isImei")
	@ResponseBody
	public Json isImei(HttpServletRequest request, HttpServletResponse response,String productCode){
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			if(!"".equals(StringUtil.checkNull(productCode))){
				voProduct product = wareService.getProduct(productCode);
				if(product==null){
					j.setMsg("没有该商品");
					j.setSuccess(true);
					return j;
				}
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					j.setMsg("不能添加套装产品！");
					j.setSuccess(true);
					return j;
				}
				boolean hasIMEI = imeiService.isProductMMBMobile(product.getId());
				if (hasIMEI) {
					j.setMsg("此商品需要填写IMEI码！");
					j.setSuccess(true);
					return j;
				}
			}			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		j.setSuccess(false);
		return j;
	}
	
	/**
	 * 保存报溢单信息
	 * @author ahc
	 */
	@RequestMapping("/saveAdd")
	@ResponseBody
	public Json saveAdd(HttpServletRequest request, HttpServletResponse response,String[] productCode,String[] imei,String[] count){
		Json j = new Json();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		List<AfterSaleBsbyProduct> list = null;
		if(request.getSession().getAttribute("list")==null){
			list =new ArrayList<AfterSaleBsbyProduct>();
		}else{
			list = (List<AfterSaleBsbyProduct>) request.getSession().getAttribute("list");
		}
		try {
			for(int i = 0 ; i < productCode.length ; i++){
				if("".equals(StringUtil.checkNull(count[i]))){
					j.setMsg("数量不能为空！");
					j.setSuccess(true);
					return j;
				}
				AfterSaleBsbyProduct asbp = new AfterSaleBsbyProduct();
				voProduct product = wareService.getProduct(productCode[i]);
				if(product==null){
					j.setMsg("没有该商品");
					j.setSuccess(true);
					return j;
				}
				asbp.setProductId(product.getId());
				asbp.setProductName(product.getName());
				asbp.setProductOriname(product.getOriname());
				asbp.setProductCode(productCode[i]);
				if("-".equals(imei[i])){
					asbp.setImei("");
				}else{
					asbp.setImei(imei[i]);
				}
				
				asbp.setCount(Integer.parseInt(count[i]));
				list.add(asbp);
			}
			request.getSession().setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return j;
	}
	
	/**
	 * 获取添加商品信息session
	 * @author ahc
	 */
	@RequestMapping("/getSessionBYinfo")
	@ResponseBody
	public EasyuiDataGridJson getSessionBYinfo(HttpServletRequest request, HttpServletResponse response){
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		List<AfterSaleBsbyProduct> list = null;
		if(request.getSession().getAttribute("list")==null){
			list =new ArrayList<AfterSaleBsbyProduct>();
		}else{
			list = (List<AfterSaleBsbyProduct>) request.getSession().getAttribute("list");
		}
		j.setRows(list);
		j.setTotal((long)list.size());
		return j;
	}
	
	/**
	 * 删除一条添加商品信息记录session
	 * @author ahc
	 */
	@RequestMapping("/delSessionBYinfo")
	@ResponseBody
	public Json delSessionBYinfo(HttpServletRequest request, HttpServletResponse response,String index){
		Json j = new Json();
		List<AfterSaleBsbyProduct> list = (List<AfterSaleBsbyProduct>) request.getSession().getAttribute("list");
		list.remove(Integer.parseInt(index));
		request.getSession().setAttribute("list", list);
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 新增检测设置选项内容
	 * @param request
	 * @param response
	 * @return
	 * 2015年1月5日
	 * @author lining
	 */
	@RequestMapping("/addDetectTypeDetail")
	@ResponseBody
	public Json addDetectTypeDetail(HttpServletRequest request, HttpServletResponse response,String detectTypeId,String productParentId1,
			String detectTypeDetailParentId1,String detectTypeDetailParentId2,String content){
		response.setContentType("text/html;charset=UTF-8");
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		j.setMsg("当前没有登录，操作失败！");
            return j;
    	}
    	if(!user.getGroup().isFlag(1456)){
    		j.setMsg("没有检测内容选项设置的权限!");
            return j;
    	}
    	if(StringUtil.toInt(detectTypeId) <= 0){
			j.setMsg("请选择选项!");
			return j;
		}
    	if(StringUtil.toInt(detectTypeId)==AfterSaleDetectTypeBean.QUOTE_ITEM && (StringUtil.StringToId(detectTypeDetailParentId1)>0 || StringUtil.StringToId(detectTypeDetailParentId2)>0)){
    		j.setMsg("报价项不能添加二级以下内容分类!");
    		return j;
    	}
    	productParentId1 = StringUtil.checkNull(productParentId1).trim();
		if(productParentId1.length()<0 || productParentId1.equals("-1")){
			j.setMsg("请选择产品一级分类!");
			return j;
 		}
    	content = StringUtil.checkNull(content).trim(); 
    	if(content.length()<=0){
    		j.setMsg("添加的内容不能为空!");
    		return j;
    	}
    	String[] contentArray = content.split("\n");
    	if(contentArray.length>200){
    		j.setMsg("添加选项内容不能超过200行!");
    		return j;
    	}
    	
    	DbOperation dbOp = new DbOperation();
    	dbOp.init(DbOperation.DB);
    	WareService wareService = new WareService(dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
    	try{
    		String[] parentId1s = productParentId1.split(",");
			boolean containsAll = false;
	    	for(String parentId1 : parentId1s){
	    		if (parentId1.equals("0")) {
	    			containsAll = true;
	    		}
	    	}
	    	AfterSaleDetectTypeDetailBean asdtd = new AfterSaleDetectTypeDetailBean();
	    	asdtd.setAfterSaleDetectTypeId(StringUtil.toInt(detectTypeId));
	    	
	    	afStockService.getDbOp().startTransaction();
	    	for(String temp : contentArray){
	    		temp = StringUtil.checkNull(temp).trim();
	    		if(temp.length() <= 0){
	    			continue;
	    		}else{
		    		if(temp.length() > 50){
		    			afStockService.getDbOp().rollbackTransaction();
		    			j.setMsg("内容的长度不能超过50!");
		    			return j;
		    		}else{
		    			asdtd.setContent(temp);
		    		}
	    		}
	    		if(containsAll){
	    			List<voCatalog> list = wareService.getCatalogs("parent_id = 0");
	    			for(voCatalog catalog : list){
	    				String result = addDetectTypeDetail(StringUtil.StringToId(detectTypeDetailParentId1),StringUtil.StringToId(detectTypeDetailParentId2), afStockService, asdtd, temp, catalog.getId());
		    			if(result!=null){
		    				j.setMsg(result);
		    				return j;
		    			}
	    			}
	    		}else{
	    			for(String parentId1 : parentId1s){
	    				if (parentId1.equals("-1")) {
			    			continue;
			    		} else {
			    			String result = addDetectTypeDetail(StringUtil.StringToId(detectTypeDetailParentId1),StringUtil.StringToId(detectTypeDetailParentId2), afStockService, asdtd, temp, Integer.parseInt(parentId1));
			    			if(result!=null){
			    				j.setMsg(result);
			    				return j;
			    			}
			    		}
	    			}
	    		}
	    	}
	    	afStockService.getDbOp().commitTransaction();
	    	j.setMsg("保存成功！");	
	    	j.setSuccess(true);
    	}catch(Exception e) {
			afStockService.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
		return j;
	}
	private String addDetectTypeDetail(int detectTypeDetailParentId1,int detectTypeDetailParentId2,
			AfStockService afStockService, AfterSaleDetectTypeDetailBean asdtd,
			String temp, int parentId1) {
		asdtd.setParentId1(parentId1);
		int count = 0;
		//判断是否有商品一级分类的检测选项设置内容
		if(detectTypeDetailParentId1 > 0){
			asdtd.setDetectTypeParentId1(detectTypeDetailParentId1);
			AfterSaleDetectTypeDetailBean parent1DetailBean = afStockService.getAfterSaleDetectTypeDetail("id=" + detectTypeDetailParentId1);
			if(parent1DetailBean==null){
				afStockService.getDbOp().rollbackTransaction();
				return "产品一级分类下的内容一级分类不存在!";
			}else{
				AfterSaleDetectTypeDetailBean bean = afStockService.getAfterSaleDetectTypeDetail("after_sale_detect_type_id=" + asdtd.getAfterSaleDetectTypeId() 
				+  " and parent_id1=" + asdtd.getParentId1() + " and detect_type_parent_id1 = 0 and content='" + StringUtil.toSql(StringUtil.checkNull(parent1DetailBean.getContent())) + "'");
				if(bean == null){
					afStockService.getDbOp().rollbackTransaction();
					return "产品一级分类下的内容一级分类不存在!";
				}
			}
			if(detectTypeDetailParentId2 > 0){
				asdtd.setDetectTypeParentId2(detectTypeDetailParentId2);
				AfterSaleDetectTypeDetailBean parent2DetailBean = afStockService.getAfterSaleDetectTypeDetail("id=" + detectTypeDetailParentId2);
				if(parent2DetailBean==null){					
					afStockService.getDbOp().rollbackTransaction();
					return "产品一级分类下的内容二级分类不存在!";
				}else{
					AfterSaleDetectTypeDetailBean bean = afStockService.getAfterSaleDetectTypeDetail("after_sale_detect_type_id=" + asdtd.getAfterSaleDetectTypeId() 
							+  " and parent_id1=" + asdtd.getParentId1() + " and detect_type_parent_id2 = 0 and detect_type_parent_id1=" + detectTypeDetailParentId1 + " and content='" + StringUtil.toSql(StringUtil.checkNull(parent2DetailBean.getContent())) + "'");
					if(bean == null){
						afStockService.getDbOp().rollbackTransaction();
						return "产品一级分类下的内容二级分类不存在!";
					}
				}
			}
			
		}
		
		String condition = " detect_type_parent_id1=" + detectTypeDetailParentId1 + " and detect_type_parent_id2=" + detectTypeDetailParentId2;
		count = afStockService.getAfterSaleDetectTypeDetailCount("after_sale_detect_type_id=" + asdtd.getAfterSaleDetectTypeId() 
				+  " and parent_id1=" + asdtd.getParentId1() + " and " + condition + " and content='" + StringUtil.toSql(temp) + "'");
		if(count > 0){
			afStockService.getDbOp().rollbackTransaction();
			return "此检测选项内容已经存在!";
		}
		if (!afStockService.addAfterSaleDetectTypeDetail(asdtd)) {
			afStockService.getDbOp().rollbackTransaction();
			return "添加失败！";
		}
		
		return null;
	}
	
	/**
	 * 批量删除内容分类（需要把其子内容分类一起删除）
	 * @param request
	 * @param response
	 * @param ids
	 * @return
	 * 2015年1月8日
	 * lining
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/batchDeleteDetectTypeDetail")
	@ResponseBody
	public Json batchDeleteDetectTypeDetail(HttpServletRequest request, HttpServletResponse response,String ids){
		response.setContentType("text/html;charset=UTF-8");
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		j.setMsg("当前没有登录，操作失败！");
            return j;
    	}
    	if(!user.getGroup().isFlag(1456)){
    		j.setMsg("没有检测内容选项设置的权限!");
            return j;
    	}
    	ids = StringUtil.checkNull(ids).trim();
    	if(ids.length()<0){
    		j.setMsg("先选择要删除的内容分类!");
    		return j;
    	}
    	String[] detectTypeDetailIds = ids.split(",");
    	DbOperation dbOp = new DbOperation();
    	dbOp.init(DbOperation.DB);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		StringBuilder deleteIds = new StringBuilder();
    	try{
    		for(String detailId : detectTypeDetailIds){
    			int id = StringUtil.StringToId(detailId);
    			if(id > 0){
    				AfterSaleDetectTypeDetailBean detailBean = afStockService.getAfterSaleDetectTypeDetail("id=" + id);
    				if(detailBean!=null){
    					deleteIds.append(id).append(",");
    					if(!(detailBean.getDetectTypeParentId2() > 0 && detailBean.getDetectTypeParentId1()>0)){
    						//查子类删除
    						String condition = "after_sale_detect_type_id=" + detailBean.getAfterSaleDetectTypeId()+" and parent_id1=" + detailBean.getParentId1();
    						if(detailBean.getDetectTypeParentId1() > 0){//二级
    							List<AfterSaleDetectTypeDetailBean> list = afStockService.getAfterSaleDetectTypeDetailList(
    									condition + " and detect_type_parent_id1=" + detailBean.getDetectTypeParentId1() + " and detect_type_parent_id2="+id, -1, -1, null);
    							if(list!=null && list.size()>0){
    								for(int i=0;i<list.size();i++){
    									deleteIds.append(list.get(i).getId()).append(",");
    								}
    							}
    						}else{//一级
    							List<AfterSaleDetectTypeDetailBean> secondList = afStockService.getAfterSaleDetectTypeDetailList(
    									condition + " and detect_type_parent_id1=" + id + " and after_sale_detect_type_id=" + detailBean.getAfterSaleDetectTypeId(),
    									-1, -1, null);
    							if(secondList!=null && secondList.size()>0){
    								for(int i=0;i<secondList.size();i++){
    									deleteIds.append(secondList.get(i).getId()).append(",");
    									List<AfterSaleDetectTypeDetailBean> ThirdList = afStockService.getAfterSaleDetectTypeDetailList(
    	    									condition + " and detect_type_parent_id1=" + id + " and detect_type_parent_id2="+secondList.get(i).getId(), -1, -1, null);
    	    							if(ThirdList!=null && ThirdList.size()>0){
    	    								for(int k=0;k<ThirdList.size();k++){
    	    									deleteIds.append(ThirdList.get(k).getId()).append(",");
    	    								}
    	    							}
    								}
    							}
    						}
    					
    					}
    				}
    			}
    		}
    		if(deleteIds.length()>0){
    			deleteIds = deleteIds.deleteCharAt(deleteIds.length()-1);
    			afStockService.getDbOp().startTransaction();
    			if(!afStockService.batchDeteleAfterSaleDetectTypeDetail(deleteIds.toString())){
    				afStockService.getDbOp().rollbackTransaction();
    				j.setMsg("删除内容分类失败!");
    				return j;
    			}
        		afStockService.getDbOp().commitTransaction();
        		j.setSuccess(true);
        		j.setMsg("删除成功!");
    		}
    	}catch(Exception e) {
			afStockService.getDbOp().rollbackTransaction();
			j.setMsg("异常！");
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} finally {
			afStockService.releaseAll();
		}
    	return j;
	}

}
