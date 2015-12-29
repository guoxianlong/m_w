/*
 * Created on 2009-5-6
 *
 */
package adultadmin.action.stock;
/**
 * 此Action为采购入库操作
 */
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mmb.contract.dao.ContractDao;
import mmb.contract.dto.SupplierContractMoney;
import mmb.contract.dto.SupplierContractReturn;
import mmb.contract.service.ContractService;
import mmb.finance.service.InitVerificationService;
import mmb.finance.service.impl.InitVerificationServiceImpl;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.IMEI.IMEIBuyStockinBean;
import mmb.stock.IMEI.IMEIService;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;
import com.mmb.framework.support.SpringHandler;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voProductSupplier;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.PrintLogBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.service.infc.ISystemService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

public class BuyStockinAction {

	private static byte[] buyStockinLock = new byte[0];
	private static DecimalFormat df = new DecimalFormat("0.##");
	public Log stockLog = LogFactory.getLog("stock.Log");

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-5-7
	 * 
	 * 说明：采购入库单列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void buyStockinList(HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		//判断是不是我司仓
		int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(56);
		int id = StringUtil.StringToId(request.getParameter("id"));//这个是在点击。。。具体的采购入库单时，，，传过来的ID编号。。
		int countPerPage = 50;
		DbOperation dbop_slave2 = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbop_slave2);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		try {
			//得到查询   条件值
			String action = StringUtil.convertNull(request.getParameter("search"));
			String stockinCode = StringUtil.convertNull(request.getParameter("stockinCode"));
			String sCreateDate = StringUtil.convertNull(request.getParameter("sCreateDate"));
			String eCreateDate = StringUtil.convertNull(request.getParameter("eCreateDate"));
			String createUser = StringUtil.convertNull(request.getParameter("createUser"));
			String auditUser = StringUtil.convertNull(request.getParameter("auditUser"));
			String sBuyDate = StringUtil.convertNull(request.getParameter("sBuyDate"));
			String eBuyDate = StringUtil.convertNull(request.getParameter("eBuyDate"));
			String buyStockCode = StringUtil.convertNull(request.getParameter("buyStockCode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));
			String productName = StringUtil.convertNull(request.getParameter("productName"));
			productName = Encoder.decrypt(productName);//解码为中文
			if(productName == null){//解码失败,表示已经为中文,则返回默认
				productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
			}
			String[] statuss = request.getParameterValues("status");
			int productLine = StringUtil.StringToId(request.getParameter("productLine"));
			int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));// 供应商id
			//新增加区域查询----
			int areaId = StringUtil.toInt(request.getParameter("qarea"));//地区id
			//结束
			
			
			if(action.equals("search")){//判断当前的操作是不是模糊查询。。。
				 String condition = "status != "+BuyStockinBean.STATUS8;//设置状态不是被删除状态。。。
				condition = condition + " and type=" + isBTwoC;    //是否是我司仓
				StringBuilder buf = new StringBuilder();
				StringBuilder search = new StringBuilder();
				StringBuilder param = new StringBuilder();//是接在   url后的参数条件。。。是为了在分页当中起到作用。。
				buf.append(" and code not like 'RK%'");
				if(areaId>=0){
					param.append("qarea="+areaId+"&");
					buf.append(" and stock_area="+areaId);
				}
				if(id>0){
					buf.append(" and id=");
					buf.append(id);
				}
				if(!viewAll){ //没有查看所有进货单的权限
					buf.append(" and create_user_id=");
					buf.append(user.getId());
				}
				if(buf.length() > 0){
					condition = condition + buf.toString();
				}
				if(!StringUtil.isNull(stockinCode)) {//判断模糊查询中的   采购入库编号是否为空、、
					param.append("stockinCode="+stockinCode+"&");
					search.append(" and code = '" + stockinCode + "'");//开始设置模糊查询的条件。。(采购入库编号条件)
				}
				if(!StringUtil.isNull(buyStockCode)) {// 判断采购预计到货单编号   是否为空。。
					param.append("buyStockCode="+buyStockCode+"&");
					int buyStockId = 0;
					BuyStockBean buyStock = service.getBuyStock("code = '"+buyStockCode+"'");//根据输入的采购预计到货编号  得到  采购预计到货对象。。从而得到采购预计到货ID
					if(buyStock != null){
						buyStockId = buyStock.getId();
					}
					search.append(" and buy_stock_id = "+buyStockId);//开始设置模糊查询的条件。。（采购预计到货编号条件）
				}
				if(!StringUtil.isNull(sCreateDate) && !StringUtil.isNull(eCreateDate)){//开始判断查询条件的添加日期  是否为空
					param.append("sCreateDate="+sCreateDate+"&");
					param.append("eCreateDate="+eCreateDate+"&");
					search.append(" and left(create_datetime, 10) between '" + sCreateDate +"'");//开始设置条件（  时间条件。。）
					search.append(" and '" + eCreateDate + "'");
				}
				if(!StringUtil.isNull(createUser)){//开始判断生产人  是否为空。。
					param.append("createUser="+createUser+"&");
					search.append(" and create_user_id in (select u.id from user u"); //根据生产人姓名得到生成人的ID
					search.append(" where u.username like '%" + createUser +"%')");
				}
				if(!StringUtil.isNull(auditUser)){//开始判断  审核人  是否为空
					param.append("auditUser="+auditUser+"&");
					search.append(" and auditing_user_id in (select u.id from user u");
					search.append(" where u.username like '%" + auditUser +"%')");// 根据审核人  姓名 得到审核人ID
				}
				if(!StringUtil.isNull(sBuyDate) && !StringUtil.isNull(eBuyDate)){//开始判断  采购完成时间  是否为空
					param.append("sBuyDate="+sBuyDate+"&");
					param.append("eBuyDate="+eBuyDate+"&");
					search.append(" and left(confirm_datetime, 10) between '" + sBuyDate +"'");
					search.append(" and '" + eBuyDate + "'"); //设置采购完成时间的条件。。
				}
				String status = "";
				if(statuss != null && statuss.length > 0){//开始判断采购入库单状态   是否为空  （状态可能为多个值）
					for (int i=0;i<statuss.length;i++) {
						status += (statuss[i] + ",");
					}
					status = status.substring(0, status.length() - 1);
					param.append("status="+status+"&");
					search.append(" and status in ("+status+")"); //开始设定条件。。
				}
				
				//*****
				String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);//根据用户名  得到  一级分类编号
				String parent2Id = ProductLinePermissionCache.getCatalogIds2(user); //根据用户  。。得到二级分类编号。。
				parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);//因为分类编号。。是从那-1 开始写的。。
				parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
				//根据入库的产品。。得到入库编号。。
				String idsSql = "select bsip.buy_stockin_id from buy_stockin_product bsip join product p on bsip.product_id = p.id";
				
				//开始对   产品表product的  一级分类编号。。跟二级分类编号。。开始赋值。。
				String productCondition = " (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + ")) and ";
				
				if(!StringUtil.isNull(productName)||!StringUtil.isNull(productCode)){//开始判断 产品编号。跟产品原名称  是否为空。。
					if(!StringUtil.isNull(productName)){
						param.append("productName="+Encoder.encrypt(productName)+"&");
//						Encoder.encrypt(productName) 设置编码。。
						productCondition = productCondition + " p.oriname like '%"+productName+"%' and ";
					}
					if(!StringUtil.isNull(productCode)){
						param.append("productCode="+productCode+"&");
						productCondition = productCondition + " p.code = '"+productCode+"' and ";
					}
				}
				
				
				if(productLine > 0){//如果产品线  不为空。。
					param.append("productLine="+productLine+"&");
					productCondition = productCondition + "p.id in (select p.id from product p, product_line_catalog plc "
						+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
						+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
						+ "product_line_id = " + productLine + ") and ";
				}
				if(supplierId > 0){//对 buy_stockin_product表的代理商编号  赋值。。
					param.append("supplierId="+supplierId+"&");
					productCondition = productCondition + "bsip.product_proxy_id = "+supplierId+" and ";
				}
				
				//***
				if(productCondition.length() > 0){
					productCondition = productCondition.substring(0,productCondition.length()-5);
					idsSql = idsSql + " where " + productCondition;
					search.append(" and id in (" + idsSql + ")");//根据多中条件得到    采购入库的编号。。
				}
				// 如果当前的操作是  查询。。。则把条件更改为   多条件的模糊查询、、、、如果不是在进行查询。。。则  直接使用  最先前的   不被删除的条件。。。得到   采购入库信息
				if(search.length() > 0){
					condition += search.toString();
				}
				//总数
				int totalCount = service.getBuyStockinCount(condition);
				//页码
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				List list = service.getBuyStockinList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				if(list!=null){
					Iterator iter = list.listIterator();
					while(iter.hasNext()){
						BuyStockinBean shBean = (BuyStockinBean)iter.next();
						//得到打印的次数。。
						shBean.setPrintCount(service.getPrintLogCount("oper_id=" + shBean.getId() + " and type=" + PrintLogBean.PRINT_LOG_TYPE_BUYSTOCKIN));
						
						//得到采购进货单。。
						BuyStockBean stock = service.getBuyStock("id="+shBean.getBuyStockId());
						shBean.setBuyStock(stock);

						//产品类别
						String productType = "";
						String parentId1 = "";
						String parentId2 = "";
						ArrayList typeKeyList = service.getFieldList(//得到当前入库了的产品的   一级分类编号
								"p.parent_id1", 
								"buy_stockin_product bsp, product p" , 
								"bsp.product_id = p.id and bsp.buy_stockin_id ="+shBean.getId(),
								-1, -1, "p.parent_id1", null, "String");
						
						Iterator typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							parentId1 += (key == null ? "-1" : key) + ", ";
						}
						typeKeyList = service.getFieldList(//得到  当前入库了的产品的二级分类编号。。（根据入库编号。。。。buy_stockin_product表使入库表跟产品表连接。。）
								"p.parent_id2", 
								"buy_stockin_product bsp, product p" , 
								"bsp.product_id = p.id and bsp.buy_stockin_id ="+shBean.getId(),
								-1, -1, "p.parent_id2", null, "String");
						typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							parentId2 += (key == null ? "-1" : key) + ", ";
						}
						if (parentId1 != null && parentId1.length() > 0) {
							parentId1 = parentId1.substring(0, parentId1.length() -2);//得到一级分类编号。。每一个编号用 ,号隔开
						} else {
							parentId1 = "-1";
						}
						if (parentId2 != null && parentId2.length() > 0) {//得到二级分类编号。。每一个编号用 ,号隔开
							parentId2 = parentId2.substring(0, parentId2.length() -2);
						} else {
							parentId2 = "-1";
						}
						
						//生成    可以得到产品线  类型  的sql语句。。。
						String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
						+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
						+ " in (" + parentId2 + ")))";
						typeKeyList = service.getFieldList(//得到产品线集合、、。、
								"pl.name", 
								"product_line pl, product_line_catalog plc", 
								productLineSql, -1, -1, "pl.id", "pl.id", "String");
						typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							productType += (key == null ? "" : key + ", ");
						}
						if (productType != null && productType.length() > 0) {
							productType = productType.substring(0, productType.length() - 2);
						} else {
							productType = "无";
						}
						
						shBean.setProductType(productType);
						//代理商
						//String proxyName = adminService.getString("pp.name", "buy_stockin_product bsp, product_proxy pp", 
						//		"bsp.product_proxy_id = pp.id and bsp.buy_stockin_id = "+shBean.getId());
						String proxyName = wareService.getString("ssi.name", "buy_stockin_product bsp, supplier_standard_info ssi", 
								"bsp.product_proxy_id = ssi.id and bsp.buy_stockin_id = "+shBean.getId());
						if (proxyName == null || proxyName.length() == 0) {
							proxyName = "无";
						}
						shBean.setProxyName(proxyName);

						//操作人
						voUser creatUser = admiService.getAdmin(shBean.getCreateUserId());
						voUser auditingUser = admiService.getAdmin(shBean.getAuditingUserId());
						shBean.setCreatUser(creatUser);
						shBean.setAuditingUser(auditingUser);
					}
				}
				param.append("isBTwoC="+isBTwoC + "&");
				param.append("search=search&");
				paging.setPrefixUrl("buyStockinList.jsp" + (param.length() > 0 ? "?" + param.substring(0, param.length() -1) : ""));
				request.setAttribute("paging", paging);
				request.setAttribute("list", list);
			}
			//得到供应商 集合   和  产品线集合的方式。。
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			
			
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("isBTwoC", isBTwoC);
			request.setAttribute("areaList", StockUtil.getProductStockArea(isBTwoC));
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			dbop_slave2.release();
		}
	}
	
	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-6-16
	 * 
	 * 说明：转换 预计到货表->采购入库单
	 * @param request
	 * @param response
	 */
	public void transformToBuyStockin(HttpServletRequest request,
			HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(114);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权转换采购入库！");
			request.setAttribute("result", "failure");
			return;
		}
		synchronized(buyStockinLock){
			int stockId = StringUtil.toInt(request.getParameter("stockId"));
			int buyOrderId = StringUtil.toInt(request.getParameter("buyOrderId"));
			//判断是我司仓还是非我司仓
			int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
			request.setAttribute("isBTwoC", isBTwoC);
			String[] buyStockProductIds = request.getParameterValues("buyStockProductId");
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IMEIService iMEIService = new IMEIService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
			try {
				service.getDbOp().startTransaction();
				BuyStockBean stock = service.getBuyStock("id=" + stockId);
				if(stock == null){    
					request.setAttribute("tip", "没有这个预计到货表");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				if (!service.updateBuyStock("transform_count = transform_count + 1" , "id = " + stockId)) {
					request.setAttribute("tip", "更新转换采购入库单次数失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				//采购入库单编号
				String code = service.generateBuyStockinCodeBref();
				
				BuyStockinBean stockin = new BuyStockinBean();
				stockin.setBuyStockId(stockId);
				stockin.setBuyOrderId(buyOrderId);
				stockin.setCreateDatetime(DateUtil.getNow());
				stockin.setConfirmDatetime(DateUtil.getNow());
				stockin.setStatus(BuyStockinBean.STATUS0);
				stockin.setType(isBTwoC);
				stockin.setCode(code);
				if (ProductStockBean.areaMap.get(stock.getArea()) != null) {
					stockin.setStockArea(stock.getArea());
				}
				//京东入库为合格库
				if(isBTwoC == 1){
					stockin.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
				}else{
					stockin.setStockType(ProductStockBean.STOCKTYPE_CHECK);
				}
				stockin.setRemark("");
				stockin.setCreateUserId(user.getId());
				stockin.setSupplierId(stock.getSupplierId());//供应商id
				if (!service.addBuyStockin(stockin)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				code = stockin.getCode();

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stockin.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("转换成采购入库单，来源预计到货表：" + stock.getCode());
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
				if( !service.addBuyAdminHistory(log) ) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				boolean hasMMBMobile = false;
				int productKind = 0;
				String MMBMobileName = "";
				for(int i=0;i<buyStockProductIds.length;i++){
					String buyStockProductId = buyStockProductIds[i];
					BuyStockProductBean bsp = service.getBuyStockProduct("id="+buyStockProductId);
					BuyStockinProductBean bsip = new BuyStockinProductBean();
					voProduct product = wareService.getProduct(bsp.getProductId());
					//这里加上对于商品是否是属于买卖宝手机的判断
					String result = iMEIService.isProductMMBMobile(product);
					if( result.equals("YES") ) {
						hasMMBMobile = true;
						MMBMobileName = product.getName();
					}
					ProductStockBean ps = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockin.getStockType() + " and area=" + stockin.getStockArea());
					if (ps == null) {
						request.setAttribute("tip", "商品库存不存在！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					int bsipId = service.getNumber("id", "buy_stockin_product", "max", "id > 0") + 1;
					bsip.setCreateDatetime(DateUtil.getNow());
					bsip.setConfirmDatetime(DateUtil.getNow());
					bsip.setId(bsipId);
					bsip.setBuyStockinId(stockin.getId());
					bsip.setStockInId(ps.getId());
					bsip.setProductCode(product.getCode());
					bsip.setProductId(product.getId());
					bsip.setRemark("");
                    bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
					BuyOrderBean buyOrder = service
							.getBuyOrder("id=" + stock.getBuyOrderId());
					double taxPoint = 0;
					if (buyOrder != null) {
						taxPoint = buyOrder.getTaxPoint();
					}
					bsip.setPrice3(Double.valueOf(
							String.valueOf(Arith.mul(bsp.getPurchasePrice(),
									Arith.add(1, taxPoint)))).floatValue());
					bsip.setProductProxyId(bsp.getProductProxyId());
					bsip.setOriname(product.getOriname());

					//已入库量
					int stockinCount = 0;
					List bispList = service.getBuyStockinProductList("buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+stock.getId()+
							" and (status = "+BuyStockinBean.STATUS4+" or status = "+BuyStockinBean.STATUS6+"))" +
							" and product_id = "+product.getId(), -1, -1, null);
					Iterator bispIterator = bispList.listIterator();
					while(bispIterator.hasNext()){
						BuyStockinProductBean bisp = (BuyStockinProductBean)bispIterator.next();
						stockinCount += bisp.getStockInCount();
					}
					bsip.setStockInCount((bsp.getBuyCount()-stockinCount)>0?(bsp.getBuyCount()-stockinCount):0);
					String isMMBMobile = iMEIService.isProductMMBMobile(product);
					if( isMMBMobile.equals("YES") ) {
						bsip.setStockInCount(0);
					}
					
					if (!service.addBuyStockinProduct(bsip)) {
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					} else {
						productKind += 1;
					}

					//log记录
					log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(stockin.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("添加采购入库单商品["+product.getCode()+"]");
					log.setType(BuyAdminHistoryBean.TYPE_ADD);
					if( !service.addBuyAdminHistory(log) ) {
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
				}
				if( hasMMBMobile && productKind > 1 ) {
					request.setAttribute("tip", "买卖宝手机分类的商品：" + MMBMobileName + "只能单独一种添加采购入库单！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				service.getDbOp().commitTransaction();
				request.setAttribute("stockinId", Integer.valueOf(stockin.getId()));
			} catch(Exception e ) {
				boolean auto = true;
				try {auto = service.getDbOp().getConn().getAutoCommit();} catch (SQLException e1) {}
				if( !auto ) {
					service.getDbOp().rollbackTransaction();
				}
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-06-18
	 * 
	 * 说明：采购入库单待转换列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void transformBuyStockinList(HttpServletRequest request, HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		int stockinId = StringUtil.StringToId(request.getParameter("stockinId"));
		String stockinCode = StringUtil.convertNull(request.getParameter("stockinCode"));
		String startDate = StringUtil.convertNull(request.getParameter("startDate"));
		String endDate = StringUtil.convertNull(request.getParameter("endDate"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String oriName = StringUtil.convertNull(request.getParameter("oriName"));
		String createUser = StringUtil.convertNull(request.getParameter("createUser"));
		String auditUser = StringUtil.convertNull(request.getParameter("auditUser"));
		int productLine = StringUtil.StringToId(request.getParameter("productLine"));
		int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
		oriName = Encoder.decrypt(oriName);//解码为中文
		if(oriName == null){//解码失败,表示已经为中文,则返回默认
			oriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("oriName")));
		}
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ISystemService service1 = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String condition = "(status = "+BuyStockinBean.STATUS4+" or status = "+BuyStockinBean.STATUS6+") and code not like 'RK%'";
			StringBuilder param = new StringBuilder();
			StringBuilder search = new StringBuilder();
			if(!StringUtil.isNull(stockinCode)) {
				param.append("stockinCode="+stockinCode+"&");
				search.append(" and code = '" + stockinCode + "'");
			}
			if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
				param.append("startDate="+startDate+"&");
				param.append("endDate="+endDate+"&");
				search.append(" and left(create_datetime, 10) between '" + startDate +"'");
				search.append(" and '" + endDate + "'");
			}
			if(!StringUtil.isNull(createUser)){
				param.append("createUser="+createUser+"&");
				search.append(" and create_user_id in (select u.id from user u");
				search.append(" where u.username like '%" + createUser +"%')");
			}
			if(!StringUtil.isNull(auditUser)){
				param.append("auditUser="+auditUser+"&");
				search.append(" and auditing_user_id in (select u.id from user u");
				search.append(" where u.username like '%" + auditUser +"%')");
			}
			
			String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);
			String parent2Id = ProductLinePermissionCache.getCatalogIds2(user);
			parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);
			parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
			String idsSql = "select bsip.buy_stockin_id from buy_stockin_product bsip join product p on bsip.product_id = p.id";
			String productCondition = " (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + ")) and ";
			if(!StringUtil.isNull(oriName)||!StringUtil.isNull(productCode)){
				if(!StringUtil.isNull(oriName)){
					param.append("oriName="+Encoder.encrypt(oriName)+"&");
					productCondition = productCondition + " p.oriname like '%"+oriName+"%' and ";
				}
				if(!StringUtil.isNull(productCode)){
					param.append("productCode="+productCode+"&");
					productCondition = productCondition + " p.code = '"+productCode+"' and ";
				}
			}
			if(productLine > 0){
				param.append("productLine="+productLine+"&");
				productCondition = productCondition + "p.id in (select p.id from product p, product_line_catalog plc "
					+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
					+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
					+ "product_line_id = " + productLine + ") and ";
			}
			if(supplierId > 0){
				param.append("supplierId="+supplierId+"&");
				productCondition = productCondition + "bsip.product_proxy_id = "+supplierId+" and ";
			}
			//***
			if(productCondition.length() > 0){
				productCondition = productCondition.substring(0,productCondition.length()-5);
				idsSql = idsSql + " where " + productCondition;
				search.append(" and id in (" + idsSql + ")");
			}
			if (search.length() > 0) {
				condition += search.toString();
			}
			//总数
			int totalCount = service.getBuyStockinCount(condition);
			int countPerPage = 10;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List buyStockinList = service.getBuyStockinList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = buyStockinList.listIterator();
			while(iter.hasNext()){
				BuyStockinBean stockinBean = (BuyStockinBean)iter.next();

				//产品类别
				String productType = "";
				String parentId1 = "";
				String parentId2 = "";
				ArrayList typeKeyList = service.getFieldList(
						"p.parent_id1", 
						"buy_stockin_product bsp, product p" , 
						"bsp.product_id = p.id and bsp.buy_stockin_id ="+stockinBean.getId(),
						-1, -1, "p.parent_id1", null, "String");
				Iterator typeKeyIter = typeKeyList.listIterator();
				while(typeKeyIter.hasNext()){
					String key = (String) typeKeyIter.next();
					parentId1 += (key == null ? "" : key + ",");
				}
				typeKeyList = service.getFieldList(
						"p.parent_id2", 
						"buy_stockin_product bsp, product p" , 
						"bsp.product_id = p.id and bsp.buy_stockin_id ="+stockinBean.getId(),
						-1, -1, "p.parent_id2", null, "String");
				typeKeyIter = typeKeyList.listIterator();
				while(typeKeyIter.hasNext()){
					String key = (String) typeKeyIter.next();
					parentId2 += (key == null ? "" : key + ",");
				}
				if (parentId1 != null && parentId1.length() > 0) {
					parentId1 = parentId1.substring(0, parentId1.length() -1);
				} else {
					parentId1 = "-1";
				}
				if (parentId2 != null && parentId2.length() > 0) {
					parentId2 = parentId2.substring(0, parentId2.length() -1);
				} else {
					parentId2 = "-1";
				}
				String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
					+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
					+ " in (" + parentId2 + ")))";
				typeKeyList = service.getFieldList("pl.name", 
						"product_line pl, product_line_catalog plc", 
						productLineSql, -1, -1, "pl.id", "pl.id", "String");
				typeKeyIter = typeKeyList.listIterator();
				while (typeKeyIter.hasNext()) {
					String key = (String) typeKeyIter.next();
					productType += (key == null ? "" : key + ", ");
				}
				if (productType != null && productType.length() > 0) {
					productType = productType.substring(0, productType.length() - 2);
				} else {
					productType = "无";
				}
				stockinBean.setProductType(productType);

				//代理商
				String proxyName = wareService.getString("ssi.name", "buy_stockin_product bsp, supplier_standard_info ssi", 
						"bsp.product_proxy_id = ssi.id and bsp.buy_stockin_id = "+stockinBean.getId());
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				stockinBean.setProxyName(proxyName);

				//操作人
				voUser creatUser = admiService.getAdmin(stockinBean.getCreateUserId());
				voUser auditingUser = admiService.getAdmin(stockinBean.getAuditingUserId());
				stockinBean.setCreatUser(creatUser);
				stockinBean.setAuditingUser(auditingUser);
			}

			if(stockinId!=0){
				if(service.getBuyStockin("id="+stockinId)==null){
					request.setAttribute("tip", "没有这个采购入库单！");
					request.setAttribute("result", "failure");
					return;
				}

				BuyStockinBean stockin = service.getBuyStockin("id="+stockinId);

				condition = "buy_stockin_id = "+stockinId;
				List buyStockinProductList = service.getBuyStockinProductList(condition, -1, -1, "id desc");
				iter = buyStockinProductList.listIterator();
				while (iter.hasNext()) {
					BuyStockinProductBean bpp = (BuyStockinProductBean) iter.next();
					voProduct product = wareService.getProduct(bpp.getProductId());
					if(product == null){

					} else {
						condition = "pl.id = plc.product_line_id and ("
							+ "(plc.catalog_type = 1 and plc.catalog_id = " + product.getParentId1() + ") or "
							+ "(plc.catalog_type = 2 and plc.catalog_id = " + product.getParentId2() + "))";
						List productLineList = service.getFieldList("pl.name", "product_line pl,product_line_catalog plc", 
								condition, -1, -1, "pl.id", "pl.id", "String");
						Iterator proLineIter = productLineList.listIterator();
						String proLineName = "";
						while (proLineIter.hasNext()) {
							String key = (String) proLineIter.next();
							proLineName += (key == null ? "" : key + ", ");
						}
						if (proLineName != null && proLineName.length() > 0) {
							proLineName = proLineName.substring(0, proLineName.length() - 2);
						} else {
							proLineName = "无";
						}
						bpp.setProductLineName(proLineName);
						product.setPsList(stockService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
						bpp.setProduct(product);
					}
				}

				String proxyName = wareService.getString("ssi.name", "buy_stockin_product bsp, supplier_standard_info ssi", 
						"bsp.product_proxy_id = ssi.id and bsp.buy_stockin_id = "+stockinId);
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				request.setAttribute("buyStockinProductList", buyStockinProductList);
				request.setAttribute("proxyName", proxyName);
				request.setAttribute("stockin", stockin);

			}
			String params = "";
			if(param.length() > 0) {
				params = param.substring(0, param.length() - 1).toString();
			}
			paging.setPrefixUrl("transformBuyStockinList.jsp" + (params.length() > 0 ? "?" + params : params));
			if(request.getAttribute("url")!=null){
				paging.setPrefixUrl(request.getAttribute("url").toString() + (params.length() > 0 ? "?" + params : params));
			}
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			
			/**
			 * 查询  获得所有的采购退货原因。。
			 */
			
			List reasonList = service1.getTextResList(" type = 5 ", -1, -1, "id desc");
			request.setAttribute("param", (params.length() > 0 ? "&" + params : params));
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("buyStockinList", buyStockinList);
			request.setAttribute("paging", paging);
			request.setAttribute("reasonList", reasonList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp_slave.release();
		}
	}
	
	/**
	 * 作者：hyb
	 * 
	 * 创建日期：2012-10-09
	 * 
	 * 说明：新采购入库单待转换列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void transformBuyStockinList2(HttpServletRequest request, HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String singleCode = StringUtil.convertNull(request.getParameter("singleCode"));
		String stockCode = StringUtil.convertNull(request.getParameter("stockCode"));
		String startDate = StringUtil.convertNull(request.getParameter("startDate"));
		String endDate = StringUtil.convertNull(request.getParameter("endDate"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String oriName = StringUtil.convertNull(request.getParameter("oriName"));
		int productLine = StringUtil.StringToId(request.getParameter("productLine"));
		int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
		String codes = null;
		oriName = Encoder.decrypt(oriName);//解码为中文
		if(oriName == null){//解码失败,表示已经为中文,则返回默认
			oriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("oriName")));
		}
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ISystemService service1 = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String condition = "code not like 'RK%' and (code like 'R%' or code like 'J%')";
			StringBuilder param = new StringBuilder();
			StringBuilder search = new StringBuilder();
			if(!StringUtil.isNull(stockCode)) {
				param.append("stockCode="+stockCode+"&");
				search.append(" and code = '" + stockCode + "'");
			}
			if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
				param.append("startDate="+startDate+"&");
				param.append("endDate="+endDate+"&");
				search.append(" and left(create_datetime, 10) between '" + startDate +"'");
				search.append(" and '" + endDate + "'");
			}
			String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);
			String parent2Id = ProductLinePermissionCache.getCatalogIds2(user);
			parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);
			parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
			String idsSql = "select sb.id from stock_batch sb join product p on sb.product_id = p.id";
			String productCondition = " (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + ")) and ";
			if(!StringUtil.isNull(oriName)||!StringUtil.isNull(productCode)){
				if(!StringUtil.isNull(oriName)){
					param.append("oriName="+Encoder.encrypt(oriName)+"&");
					productCondition = productCondition + " p.oriname like '%"+oriName+"%' and ";
				}
				if(!StringUtil.isNull(productCode)){
					param.append("productCode="+productCode+"&");
					productCondition = productCondition + " p.code = '"+productCode+"' and ";
				}
			}
			if(productLine > 0){
				param.append("productLine="+productLine+"&");
				productCondition = productCondition + "p.id in (select p.id from product p, product_line_catalog plc "
					+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
					+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
					+ "product_line_id = " + productLine + ") and ";
			}
			if(supplierId > 0){
				param.append("supplierId="+supplierId+"&");
				List codeList = service.getBuyStockList("supplier_id = " + supplierId, -1, -1, "id");
				codes = "";
				for( int i = 0 ; i < codeList.size(); i++ ) {
					BuyStockBean bsb = (BuyStockBean)codeList.get(i);
					codes += "'" + bsb.getCode() + "',";
				}
				if( codes.length() > 0 ) {
					codes = codes.substring(0, codes.length() - 1);
				}
				search.append(" and code in ( " + codes + ")");
			}
			//***
			if(productCondition.length() > 0){
				productCondition = productCondition.substring(0,productCondition.length()-5);
				idsSql = idsSql + " where " + productCondition;
				search.append(" and id in (" + idsSql + ")");
			}
			if (search.length() > 0) {
				condition += search.toString();
			}
			condition += " group by (code)";
			//总数
			
			int totalCount = service.getGroupCount(condition);
			int countPerPage = 10;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List batchList = service.getStockBatchList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "create_datetime desc");
			Iterator iter = batchList.listIterator();
			while(iter.hasNext()){
				StockBatchBean stockBatchBean = (StockBatchBean)iter.next();
				List tempList = service.getStockBatchList("code = '" + stockBatchBean.getCode() + "' group by (product_id)", -1, -1, "product_id asc");
				String allType = "";
				Map types = new HashMap();
				if( tempList.size() > 1 ) {
					for( int i = 0 ; i < tempList.size(); i++ ) {
						StockBatchBean stockBatchBean2 = (StockBatchBean) tempList.get(i);
						String productType = "";
						String parentId1 = "";
						String parentId2 = "";
						ArrayList typeKeyList = service.getFieldList(
								"p.parent_id1", 
								"stock_batch sb, product p" , 
								"sb.product_id = p.id and sb.id ="+stockBatchBean2.getId(),
								-1, -1, "p.parent_id1", null, "String");
						Iterator typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							parentId1 += (key == null ? "" : key + ",");
						}
						typeKeyList = service.getFieldList(
								"p.parent_id2", 
								"stock_batch sb, product p" , 
								"sb.product_id = p.id and sb.id ="+stockBatchBean2.getId(),
								-1, -1, "p.parent_id2", null, "String");
						typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							parentId2 += (key == null ? "" : key + ",");
						}
						if (parentId1 != null && parentId1.length() > 0) {
							parentId1 = parentId1.substring(0, parentId1.length() -1);
						} else {
							parentId1 = "-1";
						}
						if (parentId2 != null && parentId2.length() > 0) {
							parentId2 = parentId2.substring(0, parentId2.length() -1);
						} else {
							parentId2 = "-1";
						}
						String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
							+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
							+ " in (" + parentId2 + ")))";
						typeKeyList = service.getFieldList("pl.name", 
								"product_line pl, product_line_catalog plc", 
								productLineSql, -1, -1, "pl.id", "pl.id", "String");
						typeKeyIter = typeKeyList.listIterator();
						while (typeKeyIter.hasNext()) {
							String key = (String) typeKeyIter.next();
							productType += (key == null ? "" : key );
						}
						if(!types.containsKey(productType) && !productType.equals("")) {
							allType += productType+",";
							types.put(productType, "");
						}
					}
				} else {
					String productType = "";
					String parentId1 = "";
					String parentId2 = "";
					ArrayList typeKeyList = service.getFieldList(
							"p.parent_id1", 
							"stock_batch sb, product p" , 
							"sb.product_id = p.id and sb.id ="+stockBatchBean.getId(),
							-1, -1, "p.parent_id1", null, "String");
					Iterator typeKeyIter = typeKeyList.listIterator();
					while(typeKeyIter.hasNext()){
						String key = (String) typeKeyIter.next();
						parentId1 += (key == null ? "" : key + ",");
					}
					typeKeyList = service.getFieldList(
							"p.parent_id2", 
							"stock_batch sb, product p" , 
							"sb.product_id = p.id and sb.id ="+stockBatchBean.getId(),
							-1, -1, "p.parent_id2", null, "String");
					typeKeyIter = typeKeyList.listIterator();
					while(typeKeyIter.hasNext()){
						String key = (String) typeKeyIter.next();
						parentId2 += (key == null ? "" : key + ",");
					}
					if (parentId1 != null && parentId1.length() > 0) {
						parentId1 = parentId1.substring(0, parentId1.length() -1);
					} else {
						parentId1 = "-1";
					}
					if (parentId2 != null && parentId2.length() > 0) {
						parentId2 = parentId2.substring(0, parentId2.length() -1);
					} else {
						parentId2 = "-1";
					}
					String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
						+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
						+ " in (" + parentId2 + ")))";
					typeKeyList = service.getFieldList("pl.name", 
							"product_line pl, product_line_catalog plc", 
							productLineSql, -1, -1, "pl.id", "pl.id", "String");
					typeKeyIter = typeKeyList.listIterator();
					while (typeKeyIter.hasNext()) {
						String key = (String) typeKeyIter.next();
						productType += (key == null ? "" : key);
					}
					allType = productType + ",";
				}
				
				//产品类别
				if (allType != null && allType.length() > 0 && !allType.equals(",")) {
					allType = allType.substring(0, (allType.length() - 1));
				} else {
					allType = "无";
				}
				
				stockBatchBean.setProductLineName(allType);

				//代理商
				String proxyName = wareService.getString("ssi.name", "buy_stock bs, supplier_standard_info ssi", 
						"bs.supplier_id = ssi.id and bs.code = '" + stockBatchBean.getCode() + "'");
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				stockBatchBean.setSupplierName(proxyName);
			}

			if(singleCode != null && !singleCode.equals("") ){
				if(service.getStockBatch("code='"+ singleCode +"'")==null){
					request.setAttribute("tip", "没有这个批次！");
					request.setAttribute("result", "failure");
					return;
				}

				StockBatchBean stockBatchBean = service.getStockBatch("code='"+ singleCode +"'");

				//限制可转换的只能是 返厂库的 批次
				condition = "code = '"+singleCode +"'" + " and stock_type="+ ProductStockBean.STOCKTYPE_BACK + " and batch_count > 0";
				List stockBatchList = service.getStockBatchList(condition, -1, -1, "id desc");
				if( stockBatchList == null ) {
					stockBatchList = new ArrayList();
				}
				iter = stockBatchList.listIterator();
				while (iter.hasNext()) {
					StockBatchBean sbb = (StockBatchBean) iter.next();
					voProduct product = wareService.getProduct(sbb.getProductId());
					if(product == null){

					} else {
						condition = "pl.id = plc.product_line_id and ("
							+ "(plc.catalog_type = 1 and plc.catalog_id = " + product.getParentId1() + ") or "
							+ "(plc.catalog_type = 2 and plc.catalog_id = " + product.getParentId2() + "))";
						List productLineList = service.getFieldList("pl.name", "product_line pl,product_line_catalog plc", 
								condition, -1, -1, "pl.id", "pl.id", "String");
						Iterator proLineIter = productLineList.listIterator();
						String proLineName = "";
						while (proLineIter.hasNext()) {
							String key = (String) proLineIter.next();
							proLineName += (key == null ? "" : key + ", ");
						}
						if (proLineName != null && proLineName.length() > 0) {
							proLineName = proLineName.substring(0, proLineName.length() - 2);
						} else {
							proLineName = "无";
						}
						sbb.setProductLineName(proLineName);
						product.setPsList(stockService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
						sbb.setProduct(product);
						
						ProductStockBean psb = stockService.getProductStock("product_id = " + sbb.getProductId() + " and type = " + ProductStockBean.STOCKTYPE_BACK + " and area = " + sbb.getStockArea());
						int totalReturnCount = 0;
						
						sbb.setApplierCount(totalReturnCount);
						if( psb != null ) {
							if( (sbb.getBatchCount() - psb.getLockCount()) >= 0 ) {
								sbb.setAvailableCount(sbb.getBatchCount() - psb.getLockCount());
							} else {
								sbb.setAvailableCount(0);
							}
						} else {
							sbb.setAvailableCount(0);
						}
						
					}
					
				}

				String proxyName = wareService.getString("ssi.name", "buy_stock bs, supplier_standard_info ssi", 
						"bs.supplier_id = ssi.id and bs.code = '" + stockBatchBean.getCode() + "'");
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				request.setAttribute("stockBatchList", stockBatchList);
				request.setAttribute("proxyName", proxyName);
				request.setAttribute("stockBatch", stockBatchBean);

			}
			String params = "";
			if(param.length() > 0) {
				params = param.substring(0, param.length() - 1).toString();
			}
			paging.setPrefixUrl("transformBuyStockinList.jsp" + (params.length() > 0 ? "?" + params : params));
			if(request.getAttribute("url")!=null){
				paging.setPrefixUrl(request.getAttribute("url").toString() + (params.length() > 0 ? "?" + params : params));
			}
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			
			/**
			 * 查询  获得所有的采购退货原因。。
			 */
			
			List reasonList = service1.getTextResList(" type = 5 ", -1, -1, "id desc");
			request.setAttribute("param", (params.length() > 0 ? "&" + params : params));
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("batchList", batchList);
			request.setAttribute("paging", paging);
			request.setAttribute("reasonList", reasonList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp_slave.release();
		}
	}

	public void editBuyStockin(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(56);
		boolean auditing = group.isFlag(116);
		boolean bianji = group.isFlag(169);

		synchronized(buyStockinLock){
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			String remark = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("remark")));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try {
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				if(bean == null){
					request.setAttribute("tip", "没有这个采购入库单！");
					request.setAttribute("result", "failure");
					return;
				}
				BuyStockBean stock = service.getBuyStock("id="+bean.getBuyStockId());
				if(!viewAll&&stock.getAssignUserId()!=0 && stock.getAssignUserId()!=user.getId()){
					request.setAttribute("tip", "你无权修改这个采购入库单");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockinBean.STATUS4){
					request.setAttribute("tip", "该操作已完成，不能再修改！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus()==BuyStockinBean.STATUS3&&!(auditing&&bianji)){
					request.setAttribute("tip", "你没有权限修改这个采购入库单");
					request.setAttribute("result", "failure");
					return;
				}
				StringBuilder buf = new StringBuilder();
				buf.append("remark='");
				buf.append(remark);
				if(bean.status == BuyStockinBean.STATUS0){
					buf.append("',status=");
					buf.append(BuyStockinBean.STATUS1);
				}else{
					buf.append("'");
				}
				service.updateBuyStockin(buf.toString(), "id = " + bean.getId());
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-7
	 * 
	 * 说明：查看采购入库的进货价格，在价格编辑页面显示（旧）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void buyStockinPrice(HttpServletRequest request,
			HttpServletResponse response) {
		int countPerPage = 50;
		int id = StringUtil.StringToId(request.getParameter("id"));
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		WareService wareService = new WareService(service.getDbOp());
		try {
			BuyStockinBean bean = service.getBuyStockin("id = " + id);

			//相关的入库记录
			String condition = "buy_stockin_id = " + bean.getId();
			//总数
			int totalCount = service.getBuyStockinProductCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("buyStockin.jsp?id=" + id);
			ArrayList bsipList = service.getBuyStockinProductList(condition, -1, -1, "id desc");

			List productList = new ArrayList();
			Iterator iter = bsipList.listIterator();
			int stockinCount = 0;
			float totalPrice = 0;
			boolean hasDif = false;
			Map difMap = new HashMap();
			while (iter.hasNext()) {
				BuyStockinProductBean bsip = (BuyStockinProductBean) iter.next();
				stockinCount += bsip.getStockInCount();
				voProduct product = wareService.getProduct(bsip.getProductId());
				if (product != null) {
					productList.add(product);
				}
				bsip.setProduct(product);
				totalPrice += bsip.getPrice3() * bsip.getStockInCount();
				if(bean.getBuyStockId() > 0){
					BuyStockProductBean bsp = service.getBuyStockProduct("buy_stock_id=" + bean.getBuyStockId() + " and product_id=" + bsip.getProductId());
					if(bean.getStockArea() == 0 && bsp.getBuyCount() != bsip.getStockInCount()){
						difMap.put(Integer.valueOf(bsip.getId()), "hasDif");
						hasDif = true;
					}
				}
			}
			List proxyList = wareService.getSelects("product_proxy", "order by id");

			request.setAttribute("totalPrice", String.valueOf(StringUtil.formatFloat(totalPrice)));
			request.setAttribute("paging", paging);
			request.setAttribute("bean", bean);
			request.setAttribute("bsipList", bsipList);
			request.setAttribute("stockinCount", String.valueOf(stockinCount));
			request.setAttribute("productList", productList);
			request.setAttribute("proxyList", proxyList);
			request.setAttribute("hasDif", Boolean.valueOf(hasDif));
			request.setAttribute("difMap", difMap);

			BuyStockBean plan = service.getBuyStock("id=" + bean.getBuyStockId());
			if(plan != null){
				List buyStockProductList = service.getBuyStockProductList("buy_stock_id=" + plan.getId(), -1, -1, "id desc");
				iter = buyStockProductList.listIterator();
				int planCount = 0;
				float totalStockPurchasePrice = 0;
				while(iter.hasNext()){
					BuyStockProductBean bpp = (BuyStockProductBean)iter.next();
					planCount += bpp.getBuyCount();
					totalStockPurchasePrice += bpp.getPurchasePrice() * bpp.getBuyCount();
					voProduct product = wareService.getProduct(bpp.getProductId());
					if(product == null){

					} else {
						bpp.setProduct(product);
					}
				}
				request.setAttribute("stockCount", String.valueOf(planCount));
				request.setAttribute("totalStockPurchasePrice", String.valueOf(totalStockPurchasePrice));
				request.setAttribute("buyStock", plan);
				request.setAttribute("buyStockProductList", buyStockProductList);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-5-8
	 * 
	 * 说明：查看采购入库单的详细内容
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void buyStockin(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		String isBTwoCs = request.getParameter("isBTwoC");
		if(isBTwoCs == null || "".equals(isBTwoCs) || "null".equals(isBTwoCs)||"${isBTwoC }".equals(isBTwoCs)) {
			request.setAttribute("tip", "参数有误！");
			request.setAttribute("result", "failure");
			return;
		}
		int isBTwoC = StringUtil.StringToId(isBTwoCs);
		int countPerPage = 50;
		int id = StringUtil.StringToId(request.getParameter("id"));
		DbOperation dbOp_slave2 = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave2);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave2);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IMEIService iMEIService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try {
			BuyStockinBean bean = service.getBuyStockin("id = " + id);
			if(bean == null){
				request.setAttribute("tip", "没有这个采购入库单");
				request.setAttribute("result", "failure");
				return;
			}
			
			//------------因为买卖宝手机 加入的代码  Haoyabin 2013.12.17
			boolean isMMBMobile = false;
			String isMMBString = iMEIService.isBuyStockinMMBMobile(bean);
			if( isMMBString.equals("YES") ) {
				isMMBMobile = true;
			} else if( isMMBString.equals("NO") ) {
				
			} else {
				request.setAttribute("tip", isMMBString);
				request.setAttribute("result", "failure");
				return;
			}
			//-------------

			//相关的入库记录
			String condition = "buy_stockin_id = " + bean.getId();
			//总数
			int totalCount = service.getBuyStockinProductCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("buyStockin.jsp?id=" + id);
			ArrayList bsipList = service.getBuyStockinProductList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = bsipList.listIterator();
			float totalPrice = 0;
			int proxyId = 0;
			ArrayList errorProductList = new ArrayList();
			while (iter.hasNext()) {
				BuyStockinProductBean bsip = (BuyStockinProductBean) iter.next();
				voProduct product = wareService.getProduct(bsip.getProductId());

				if (product != null) {
					product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
					bsip.setProduct(product);
					if( isMMBMobile ) {
						request.setAttribute("productId", product.getId());
					}
				}

				proxyId = bsip.getProductProxyId();

				if(product.getIsPackage()==1){
                	errorProductList.add(Integer.valueOf(product.getId()));
                }
				
				//产品线名称
				voProductLine pl = null;
				voProductLineCatalog plc = null;
				List list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId1());
				if(list2.size()>0){
					plc = (voProductLineCatalog)list2.get(0);
					pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
					bsip.setProductLineName(pl.getName());
				}
				if(StringUtil.convertNull(bsip.getProductLineName()).equals("")){
					list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId2());
					if(list2.size()>0){
						plc = (voProductLineCatalog)list2.get(0);
						pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
						bsip.setProductLineName(pl.getName());
					}
				}
				if(StringUtil.convertNull(bsip.getProductLineName()).equals("")){
					bsip.setProductLineName("无");
				}
			}

			List proxyList = wareService.getSelects("supplier_standard_info", "where status = 1 order by id");

			//来源预计到货表、采购订单、采购计划编号、ID
			BuyStockBean stock = service.getBuyStock("id="+bean.getBuyStockId());
			if(stock != null){
				request.setAttribute("buyStockCode", stock.getCode());
				request.setAttribute("stockId", Integer.valueOf(stock.getId()));
			}
			if( isMMBMobile ) {
				List<IMEIBuyStockinBean> imeiList = iMEIService.getIMEIBuyStockinList("buy_stockin_id=" + bean.getId(), -1, -1, "id asc");
				request.setAttribute("imeiList", imeiList);
			}
			//代理商ID


			request.setAttribute("totalPrice", String.valueOf(StringUtil.formatFloat(totalPrice)));
			request.setAttribute("paging", paging);
			request.setAttribute("bean", bean);
			request.setAttribute("bsipList", bsipList);
			request.setAttribute("proxyId", Integer.valueOf(proxyId));
			request.setAttribute("proxyList", proxyList);
			request.setAttribute("errorProductList", errorProductList);
			request.setAttribute("isBTwoC", isBTwoC);
			if( isMMBMobile ) {
				request.setAttribute("isMMBMobile", "YES");
			} else {
				request.setAttribute("isMMBMobile", "NO");
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-4-21
	 * 
	 * 说明：打印记录列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void printLogList(HttpServletRequest request, HttpServletResponse response) {
		int countPerPage = 50;
		int operId = StringUtil.StringToId(request.getParameter("operId"));
		int type = StringUtil.StringToId(request.getParameter("type"));
		DbOperation dbop_slave = new DbOperation(DbOperation.DB_SLAVE);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop_slave);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbop_slave);
		try {
			String condition = "oper_id=" + operId + " and type = "+type;
			//总数
			int totalCount = service.getPrintLogCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("printLog.jsp?operId=" + operId+"&type="+type);
			List printLogList = service.getPrintLogList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc" );
			Iterator iter = printLogList.listIterator();
			while(iter.hasNext()){
				PrintLogBean plBean = (PrintLogBean)iter.next();
				plBean.setUser(admiService.getAdmin(plBean.getUserId()));
			}

			request.setAttribute("paging", paging);
			request.setAttribute("printLogList", printLogList);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			dbop_slave.release();
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2008-4-21
	 * 
	 * 说明：打印采购入库信息
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void printBuyStockinPrice(HttpServletRequest request,
			HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		
		int stockinId = StringUtil.StringToId(request.getParameter("stockinId"));
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp);
		WareService wareService = new WareService(dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try {
			BuyStockinBean bean = service.getBuyStockin("id = " + stockinId);
			if(bean == null){
				request.setAttribute("tip", "没有这个采购入库单");
				request.setAttribute("result", "failure");
				return;
			}
			if(bean.getStatus()!=BuyStockinBean.STATUS6 && bean.getStatus()!=BuyStockinBean.STATUS4){
				request.setAttribute("tip", "采购入库还未通过审核，不能够导出");
				request.setAttribute("result", "failure");
				return;
			}
			if(bean.getAuditingUserId() == 0){
				request.setAttribute("tip", "采购入库未进行过审核，不能够导出");
				request.setAttribute("result", "failure");
				return;
			}

			//相关的入库记录
			String condition = "buy_stockin_id = " + bean.getId();
			ArrayList bsipList = service.getBuyStockinProductList(condition, -1, -1, "id desc");
			Iterator iter = bsipList.listIterator();
			int proxyId = 0;
			while (iter.hasNext()) {
				BuyStockinProductBean bsip = (BuyStockinProductBean) iter.next();
				voProduct product = wareService.getProduct(bsip.getProductId());

				if (product != null) {
					product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
					bsip.setProduct(product);
				}

				proxyId = bsip.getProductProxyId();
				//产品线名称
				voProductLine pl = null;
				voProductLineCatalog plc = null;
				List list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId1());
				if(list2.size()>0){
					plc = (voProductLineCatalog)list2.get(0);
					pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
					bsip.setProductLineName(pl.getName());
				}
				if(StringUtil.convertNull(bsip.getProductLineName()).equals("")){
					list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId2());
					if(list2.size()>0){
						plc = (voProductLineCatalog)list2.get(0);
						pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
						bsip.setProductLineName(pl.getName());
					}
				}
				if(StringUtil.convertNull(bsip.getProductLineName()).equals("")){
					bsip.setProductLineName("无");
				}
			}
			
			//log记录
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("打印/导出列表");
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			service.addBuyAdminHistory(log);
			
			//代理商
			String proxyName = wareService.getString("ssi.name", "buy_stockin_product bsp, supplier_standard_info ssi", 
					"bsp.product_proxy_id = ssi.id and bsp.buy_stockin_id = "+bean.getId());
			if (proxyName == null || proxyName.length() == 0) {
				proxyName = "无";
			}
			bean.setProxyName(proxyName);
			
			//获取税点、付款人
			BuyOrderBean buyOrder = service.getBuyOrder("id = (select buy_order_id from buy_stock where id = "+bean.getBuyStockId()+")");
			
			//操作人
			voUser creatUser = admiService.getAdmin(bean.getCreateUserId());
			voUser auditingUser = admiService.getAdmin(bean.getAuditingUserId());
			bean.setCreatUser(creatUser);
			bean.setAuditingUser(auditingUser);
			
			//来源采购订单
			
			request.setAttribute("bean", bean);
			request.setAttribute("buyOrder", buyOrder);
			request.setAttribute("bsipList", bsipList);
			request.setAttribute("proxyId", Integer.valueOf(proxyId));

			PrintLogBean plBean = new PrintLogBean();
			plBean.setOperId(bean.getId());
			plBean.setUserId(user.getId());
			plBean.setCreateDatetime(DateUtil.getNow());
			service.addPrintLog(plBean);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}


	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-5-8
	 * 
	 * 说明：修改采购入库单中的入库商品信息
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void editBuyStockinItem(HttpServletRequest request, HttpServletResponse response) {

		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if (admin == null) {
			request.setAttribute("tip", "当前没有登录，添加失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = admin.getGroup();
		boolean auditing = group.isFlag(116);
		boolean bianji = group.isFlag(169);

		synchronized(buyStockinLock){
			//判断是我司仓还是非我司仓
			int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
			request.setAttribute("isBTwoC", isBTwoC);
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			
			try {
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				if(bean == null){
					request.setAttribute("tip", "没有这个采购入库单！");
					request.setAttribute("result", "failure");
					return;
				}
				if(!bianji&&bean.getCreateUserId()!=admin.getId()){
					request.setAttribute("tip", "你无权修改这个采购入库单");
					request.setAttribute("result", "failure");
					return;
				}
				if (bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6) {
					request.setAttribute("tip", "该操作已经完成，不能再修改！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus()==BuyStockinBean.STATUS3&&!(auditing&&bianji)){
					request.setAttribute("tip", "你没有权限修改这个采购入库单");
					request.setAttribute("result", "failure");
					return;
				}

				//相关的产品
				int stockinCount = 0;
				//相关的入库记录
				String condition = "buy_stockin_id = " + bean.getId();
				//总数
				int totalCount = service.getBuyStockinProductCount(condition);
				int countPerPage = 50;
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				ArrayList bsipList = service.getBuyStockinProductList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				Iterator itr = bsipList.listIterator();
				//开始事务
				service.getDbOp().startTransaction();
				voProduct product = null;
				BuyStockinProductBean bsip = null;
				int stockArea=StringUtil.toInt(request.getParameter("stockArea"));//地区
				if(stockArea==0){
					request.setAttribute("tip", "请选择库地区！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				while (itr.hasNext()) {
					bsip = (BuyStockinProductBean) itr.next();
					product = wareService.getProduct(bsip.getProductId());
					if (product == null) {
						request.setAttribute("tip", "找不到该商品！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}

					//该产品已经处理完成
					if (bsip.getStatus() == BuyStockinProductBean.BUYSTOCKIN_DEALED) {
						continue;
					}

					stockinCount = StringUtil.StringToId(request.getParameter("stockinCount" + product.getId()));

					if (stockinCount <= 0) {
						request.setAttribute("tip", "请输入正确的入库量！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}

					//入库记录
					if(bsip.getStockInCount() != stockinCount){
						BuyStockProductBean bsp = service.getBuyStockProduct("buy_stock_id = " + bean.getBuyStockId() + " and product_id = " + product.getId());
						if (bsp == null) {
							request.setAttribute("tip", "没有找到预计到货单商品！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						if(!psService.checkBuyStockinProductCount(service.getDbOp(), bsip.getBuyStockinId(), stockinCount, bsp.getBuyCount(), product.getId(), bean.getBuyStockId())) {
							request.setAttribute("tip", "采购入库单商品过多！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						if (!service.updateBuyStockinProduct("stockin_count = " + stockinCount, "id = " + bsip.getId())) {
							request.setAttribute("tip", "采购入库单商品更新数量失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}

						BuyAdminHistoryBean log = new BuyAdminHistoryBean();
						log.setAdminId(admin.getId());
						log.setAdminName(admin.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("修改了采购入库单商品["+product.getCode()+"]的入库量("+bsip.getStockInCount()+"-"+stockinCount+")");
						log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
						if (!service.addBuyAdminHistory(log)) {
							request.setAttribute("tip", "添加采购入库单更新日志失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					                                   
				}
				if(bean.getStatus() == BuyStockinBean.STATUS0){
					if (!service.updateBuyStockin("status="+BuyStockinBean.STATUS1, "id="+bean.getId()) ) {
						request.setAttribute("tip", "更新采购入库单状态失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
				}
				
				//提交事务
				service.getDbOp().commitTransaction();
			} catch(Exception e){
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}


	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-06-18
	 * 
	 * 说明：确认采购入库
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void completeBuyStockin(HttpServletRequest request, HttpServletResponse response){

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(115);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权确认这个采购入库！");
			request.setAttribute("result", "failure");
			return;
		}

		//判断是不是我司仓
		int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
		request.setAttribute("isBTwoC", isBTwoC);
		synchronized(buyStockinLock){
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				service.getDbOp().startTransaction();
				BuyStockinBean bean = service.getBuyStockin("id = "+buyStockinId);
				if (bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6) {
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				service.getDbOp().startTransaction();
				List<BuyStockProductBean> bspList = service.getBuyStockProductList("buy_stock_id = " + bean.getBuyStockId(), -1, -1, null);
				if (bspList != null && bspList.size() != 0) {
					for (BuyStockProductBean bsp : bspList) {
						if(!psService.checkBuyStockinProductCount(service.getDbOp(), -1, 0, bsp.getBuyCount(), bsp.getProductId(), bean.getBuyStockId())) {
							request.setAttribute("tip", "采购入库单商品过多！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
				}
				
				
				if (!service.updateBuyStockin("status="+BuyStockinBean.STATUS3+", affirm_datetime=now() , affirm_user_id="+ user.getId(), "id="+buyStockinId)) {
					request.setAttribute("tip", "更新采购入库单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(buyStockinId);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("确认采购入库单");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				if (!service.addBuyAdminHistory(log)) {
					request.setAttribute("tip", "添加采购入库单更新日志失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}
			finally {
				service.releaseAll();
			}
		}

	}

	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-06-23
	 * 
	 * 说明：审核采购入库
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void confirmBuyStockin(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(116);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权审核这个采购入库！");
			request.setAttribute("result", "failure");
			return;
		}
		
		//判断是我司仓还是非我司仓
		int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
		request.setAttribute("isBTwoC", isBTwoC);
		synchronized(buyStockinLock){
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			int mark = StringUtil.toInt(request.getParameter("mark"));
			WareService wareService = new WareService();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			ISupplierService supplierService = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IMEIService iMEIService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			InitVerificationService initVerificationService = new InitVerificationServiceImpl();
			try {
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				String isMMBMobileS = iMEIService.isBuyStockinMMBMobile(bean);
				boolean isMMBMobile = false;
				if( isMMBMobileS.equals("YES") ) {
					isMMBMobile = true;
				} else if( isMMBMobileS.equals("NO") ) {
					isMMBMobile = false;
				} else {
					request.setAttribute("tip", isMMBMobileS);
					request.setAttribute("result", "failure");
					return;
				}
				if (bean == null) {
					request.setAttribute("tip", "没有这个采购入库单");
					request.setAttribute("result", "failure");
					return;
				}
				if (bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6) {
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}

				if (mark == 0) { // 审核未通过
					service.getDbOp().startTransaction();
					if(!service.updateBuyStockin("status = " + BuyStockinBean.STATUS5, "id = " + buyStockinId))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					//log记录
					BuyAdminHistoryBean log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(buyStockinId);
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("审核未通过");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					if(!service.addBuyAdminHistory(log))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					//--1-1-1 如果是MMBMobile的话，要加上删除对应的 IMEI码的操作
					if (isMMBMobile) {
						String result = iMEIService.disposeRelationWithBuyStockin(buyStockinId,user);
						if(!result.equals("SUCCESS") ) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", result);
							request.setAttribute("result", "failure");
							return;
						}
					}
					service.getDbOp().commitTransaction();
				} else {  //审核通过  
					int productId = StringUtil.StringToId(request.getParameter("productId"));
					String condition = "buy_stockin_id = " + bean.getId() + " and status = " + BuyStockinProductBean.BUYSTOCKIN_UNDEAL;
					if (productId > 0) {
						condition += " and product_id = " + productId;
					}
					ArrayList shList = service.getBuyStockinProductList(condition, 0, -1, "id");
					Iterator itr = shList.iterator();
					if(shList.size() == 0){
						request.setAttribute("tip", "没有需要执行的数据");
						request.setAttribute("result", "failure");
						return;
					}
					
					List<BuyStockProductBean> bspList = service.getBuyStockProductList("buy_stock_id = " + bean.getBuyStockId(), -1, -1, null);
					if (bspList != null && bspList.size() != 0) {
						for (BuyStockProductBean bsp : bspList) {
							if(!psService.checkBuyStockinProductCount(service.getDbOp(), -1, 0, bsp.getBuyCount(), bsp.getProductId(), bean.getBuyStockId())) {
								request.setAttribute("tip", "采购入库单商品过多！");
								request.setAttribute("result", "failure");
								return;
							}
						}
					}
					
					//获取税点
					BuyOrderBean buyOrder = service.getBuyOrder("id=(select buy_order_id from buy_stock where id="+bean.getBuyStockId()+")");
					double taxPoint = 0;
					if(buyOrder!=null){
						taxPoint = buyOrder.getTaxPoint();
					}
					BuyStockinBean bsib=service.getBuyStockin(" id="+buyStockinId);//获得采购入库单的信息
					List bsipList=	service.getBuyStockinProductList(" buy_stockin_id="+buyStockinId, 0, -1, null);//入库单入的那些商品
				
					//开始事务
					service.getDbOp().startTransaction();

					//审核采购入库单
					if(!service.updateBuyStockin("status = " + BuyStockinBean.STATUS4 + ", confirm_datetime = now(), auditing_user_id=" + user.getId(), "id = " + buyStockinId))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					
					BuyAdminHistoryBean log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("审核通过");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					if(!service.addBuyAdminHistory(log)) {
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					
					log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("入库已完成");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					if(!service.addBuyAdminHistory(log)) {
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return;
					}
					
					BuyStockinProductBean sh = null;
					voProduct product = null;
					String set = null;
					ProductStockBean ps = null;
					int count = 0;
					
					//用于处理批次和进销存卡片的参数
					List<BaseProductInfo> bpiList = new ArrayList<BaseProductInfo>();
					BuyStockBean stock = service.getBuyStock("id="+bean.getBuyStockId());
					while (itr.hasNext()) {
						count++;
						sh = (BuyStockinProductBean) itr.next();
//						sh.setPrice3(Double.valueOf(String.valueOf(Arith.mul(sh.getPrice3(),Arith.add(1,taxPoint)))).floatValue());
						
						if(sh.getStockInCount() <= 0){
							request.setAttribute("tip", "采购入库量不能为0，操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}

						product = wareService.getProduct(sh.getProductId());
						if(product.getIsPackage()==1){
							request.setAttribute("tip", "入库单中包含有套装产品，不能入库！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
						ps = psService.getProductStock("id=" + sh.getStockInId());
						float price5 = 0;
						int totalCount = product.getStockAll() + product.getLockCountAll();
						price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (sh.getPrice3() * sh.getStockInCount())) / (totalCount + sh.getStockInCount()) * 1000))/1000;

						set = "status = " + BuyStockinProductBean.BUYSTOCKIN_DEALED
						+ ", remark = '操作前库存" + ps.getStock()
						+ ",操作后库存" + (ps.getStock() + sh.getStockInCount())
						+ "', confirm_datetime = now()";
						if(!service.updateBuyStockinProduct(set, "id = " + sh.getId()))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						if(!psService.updateProductStockCount(sh.getStockInId(), sh.getStockInCount())){
							request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						
						//更新货位库存2011-04-19
						CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ps.getArea());
						CargoProductStockBean cps = null;
						List cocList = null;
						//京东仓：直接入到合格库，缓存区
						if(isBTwoC == 1){
							cocList = cargoService.getCargoAndProductStockList(
									"ci.stock_type =0  and ci.area_id ="+inCargoArea.getId()+"  and ci.store_type = "+CargoInfoBean.STORE_TYPE4+
									" and cps.product_id = "+sh.getProductId(), -1, -1, "ci.id desc");
						}else{
							cocList = cargoService.getCargoAndProductStockList(
									"ci.stock_type = "+ps.getType()+" and ci.area_id = "+inCargoArea.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2+
									" and cps.product_id = "+sh.getProductId(), -1, -1, "ci.id desc");
						}
						
						if(cocList == null || cocList.size() == 0){//产品首次入库，无暂存区绑定货位库存信息
							CargoInfoBean cargo = null;
							if(isBTwoC == 1){
								cargo = cargoService.getCargoInfo("stock_type =0  and area_id ="+inCargoArea.getId()+"  and store_type = "+CargoInfoBean.STORE_TYPE4);
								if(cargo == null){
									request.setAttribute("tip", ProductStockBean.areaMap.get(inCargoArea.getId()) + "合格库的混合区货位未设置，请先添加后再完成入库！！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}else{
								cargo = cargoService.getCargoInfo("stock_type = "+ps.getType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2);
								if(cargo == null){
									request.setAttribute("tip", "目的待验库缓存区货位未设置，请先添加后再完成入库！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							
							cps = new CargoProductStockBean();
							cps.setCargoId(cargo.getId());
							cps.setProductId(sh.getProductId());
							cps.setStockCount(sh.getStockInCount());
							if(!cargoService.addCargoProductStock(cps)){
								request.setAttribute("tip", "添加货位库存记录失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							cps.setId(cargoService.getDbOp().getLastInsertId());
							
							if(!cargoService.updateCargoInfo("status = 0", "id = "+cargo.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
						}else{
							cps = (CargoProductStockBean)cocList.get(0);
							if(!cargoService.updateCargoProductStockCount(cps.getId(), sh.getStockInCount()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
						}

						//更新订单已入库量和已入库总金额
						String area = "";
						if(bean.getStockArea()==ProductStockBean.AREA_BJ){
							area = "bj";
						}else if(bean.getStockArea()==ProductStockBean.AREA_GF){
							area = "gd";
						}else if(bean.getStockArea()==ProductStockBean.AREA_ZC){//增城用北京的字段
							area = "bj";
						}else if(bean.getStockArea()==ProductStockBean.AREA_WX){//无锡用北京的字段
							area = "bj";
						}else if(bean.getStockArea() == ProductStockBean.AREA_JD){//京东记到广东
							area = "gd";
						}else if(bean.getStockArea()==ProductStockBean.AREA_XA){//西安用北京的字段
							area = "bj";
						} else {
							area = "gd";
						}
						//BuyStockBean stock = service.getBuyStock("id="+bean.getBuyStockId());
						set = "stockin_count_"+area+"=(stockin_count_"+area+" + "+sh.getStockInCount()+")";
						condition = "buy_order_id="+stock.getBuyOrderId()+" and product_id="+product.getId();
						if(!service.updateBuyOrderProduct(set, condition))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						
//					批次操作放到fpa jar包中	
//						StockBatchBean batch = null;
//						if(isBTwoC == 1){
//							batch = service.getStockBatch("code='" + stock.getCode() + "' and product_id = " + sh.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_QUALIFIED + " and stock_area = " + bean.getStockArea());
//						}else{
//							batch = service.getStockBatch("code='" + stock.getCode() + "' and product_id = " + sh.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_CHECK + " and stock_area = " + bean.getStockArea());	
//						}
//						if( batch == null ) {
//							batch = new StockBatchBean();
//							batch.setCode(stock.getCode());
//							batch.setProductId(sh.getProductId());
//							batch.setPrice(sh.getPrice3());
//							batch.setBatchCount(sh.getStockInCount());
//							batch.setProductStockId(ps.getId());
//							batch.setStockArea(bean.getStockArea());
//							if(isBTwoC == 1){
//								batch.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
//							}else{
//								batch.setStockType(ProductStockBean.STOCKTYPE_CHECK);	
//							}
//							batch.setCreateDateTime(DateUtil.getNow());
//							batch.setTicket(ticket);
//							if(!service.addStockBatch(batch))
//							{
//							  service.getDbOp().rollbackTransaction();
//							  request.setAttribute("tip", "数据库操作失败");
//							  request.setAttribute("result", "failure");
//							  return;
//							}
//						} else {
//							if(isBTwoC == 1){
//								if( !service.updateStockBatch("batch_count = " + (batch.getBatchCount() + sh.getStockInCount()), "code='" + stock.getCode() + "' and product_id = " + sh.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_QUALIFIED + " and stock_area = " + bean.getStockArea())) {
//									request.setAttribute("tip", "修改批次数量 时据库操作失败！");
//									request.setAttribute("result", "failure");
//									service.getDbOp().rollbackTransaction();
//									return;
//								}
//							}else{
//								if( !service.updateStockBatch("batch_count = " + (batch.getBatchCount() + sh.getStockInCount()), "code='" + stock.getCode() + "' and product_id = " + sh.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_CHECK + " and stock_area = " + bean.getStockArea())) {
//									request.setAttribute("tip", "修改批次数量 时据库操作失败！");
//									request.setAttribute("result", "failure");
//									service.getDbOp().rollbackTransaction();
//									return;
//								}
//							}	
//						}
//						
//						//添加批次操作记录
//						StockBatchLogBean batchLog = new StockBatchLogBean();
//						batchLog.setCode(batch.getCode());
//						batchLog.setStockType(batch.getStockType());
//						batchLog.setStockArea(batch.getStockArea());
//						batchLog.setBatchCode(batch.getCode());
//						batchLog.setBatchCount(sh.getStockInCount());
//						batchLog.setBatchPrice(batch.getPrice());
//						batchLog.setProductId(batch.getProductId());
//						batchLog.setRemark("采购入库");
//						batchLog.setCreateDatetime(DateUtil.getNow());
//						batchLog.setUserId(user.getId());
//						if(!service.addStockBatchLog(batchLog))
//						{
//						  service.getDbOp().rollbackTransaction();
//						  request.setAttribute("tip", "数据库操作失败");
//						  request.setAttribute("result", "failure");
//						  return;
//						}
						//判断并插入供货商关联信息 
						voProductSupplier productSupplier = supplierService.getProductSupplierInfo("product_id = "+sh.getProductId()+" and supplier_id = "+sh.getProductProxyId());
						if(productSupplier == null){
							SupplierStandardInfoBean supplierStandardInfo = supplierService.getSupplierStandardInfo("id = "+sh.getProductProxyId());
							
							productSupplier = new voProductSupplier();
							productSupplier.setProduct_id(sh.getProductId());
							productSupplier.setSupplier_id(sh.getProductProxyId());
							if(supplierStandardInfo!=null){
								productSupplier.setSupplier_name(supplierStandardInfo.getName());
							}
							if(!supplierService.addProductSupplierInfo(productSupplier))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
						}

						
						//--1-1-1 在这里加IMEI码状态修改， 另外需要加IMEI码操作日志
						if( isMMBMobile ) {
							String resu = iMEIService.stockinIMEI(buyStockinId,sh,bsib.getCode(),user);
							if( !resu.equals("SUCCESS") ) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", resu);
								request.setAttribute("result", "failure");
								return;
							}
						}
						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
						cps = (CargoProductStockBean)cargoService.getCargoAndProductStockList("cps.id = "+cps.getId(), 0, 1, "cps.id asc").get(0);

						//计算入库金额
						double totalPrice = sh.getStockInCount()*sh.getPrice3();
						
						// 入库卡片
						StockCardBean sc = new StockCardBean();
						sc.setCardType(StockCardBean.CARDTYPE_BUYSTOCKIN);
						sc.setCode(bean.getCode());
						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(bean.getStockType());
						sc.setStockArea(bean.getStockArea());
						sc.setProductId(sh.getProductId());
						sc.setStockId(sh.getStockInId());
						sc.setStockInCount(sh.getStockInCount());
						sc.setStockInPriceSum(totalPrice);
//						sc.setStockInPriceSum((new BigDecimal(sh.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(sh.getPrice3()))).doubleValue());
						sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
						sc.setStockAllArea(product.getStock(bean.getStockArea()) + product.getLockCount(bean.getStockArea()));
						sc.setStockAllType(product.getStockAllType(bean.getStockType()) + product.getLockCountAllType(bean.getStockType()));
						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
						sc.setStockPrice(price5);
						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!psService.addStockCard(sc))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}					
						//货位入库卡片
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_BUYSTOCKIN);
						csc.setCode(bean.getCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(bean.getStockType());
						csc.setStockArea(bean.getStockArea());
						csc.setProductId(sh.getProductId());
						csc.setStockId(cps.getId());
						csc.setStockInCount(sh.getStockInCount());
						csc.setStockInPriceSum(totalPrice);
						csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
						csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
						csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
						csc.setStockPrice(price5);
						csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!cargoService.addCargoStockCard(csc))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						
						//封装接口所需的数据
						BaseProductInfo bpi = new BaseProductInfo();
						//product_stock的id
						bpi.setProductStockId(ps.getId());
						//入库量
						bpi.setInCount(sh.getStockInCount());
						//入库价
						bpi.setInPrice(sh.getPrice3());
						//产品id
						bpi.setId(sh.getProductId());
						bpiList.add(bpi);
						
					}

					if (count == 0) {
						request.setAttribute("tip", "该操作没有任何库存变动，不能执行！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					
					initVerificationService.insertRInitData(bsipList, user.getId(), wareService.getDbOp());
					

					if(bpiList.size() > 0){
						//获取入库工厂类
						FinanceBaseDataService buyStockinBase = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(
															FinanceStockCardBean.CARDTYPE_BUYSTOCKIN, service.getDbOp().getConn());
						//处理批次和进销存卡片
						buyStockinBase.acquireFinanceBaseData(bpiList, bean.getCode(), user.getId(),bean.getStockType(),bean.getStockArea());
					}
					
					
						
					String flag = StringUtil.convertNull(request.getParameter("completeFlag"));
					if(flag!=null && flag.equals("0")){
						if(!service.completeBuyOrder(user, stock, buyOrder, true)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "完成采购订单失败！");
							request.setAttribute("result", "failure");
							return;
						}
					} else {
						if(!service.completeBuyOrder(user, stock, buyOrder, false)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "完成采购订单失败");
							request.setAttribute("result", "failure");
							return;
						}
					}
					
					//提交事务
					service.getDbOp().commitTransaction();
					
					//计算返利金额
					ContractService contractService = SpringHandler.getBean(ContractService.class);
					if(!contractService.dealContractMony(bean.getBuyOrderId())) {
						request.setAttribute("tip", "计算返利金额失败");
						return;
					}
				}
			}catch (Exception e) {
				request.setAttribute("tip", "操作失败，请联系管理员");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				service.releaseAll();
			}
		}
	}
	

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-8
	 * 
	 * 说明：删除采购入库单中的入库商品信息
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void deleteBuyStockinItem(HttpServletRequest request, HttpServletResponse response) {

		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if (admin == null) {
			request.setAttribute("tip", "当前没有登录，添加失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = admin.getGroup();
		boolean bianji = group.isFlag(169);
		boolean shenhe = group.isFlag(116);
		
		synchronized(buyStockinLock){
			//判断是我司仓还是非我司仓
			int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
			request.setAttribute("isBTwoC", isBTwoC);
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			int productId = StringUtil.StringToId(request.getParameter("productId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try {
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				if(bean == null){
					request.setAttribute("tip", "没有这个采购入库单！");
					request.setAttribute("result", "failure");
					return;
				}
				if(((bean.getStatus()!=BuyStockinBean.STATUS6&&bean.getStatus()!=BuyStockinBean.STATUS4)&&(!bianji&&bean.getCreateUserId()!=admin.getId()))
						||(bean.getStatus()==BuyStockinBean.STATUS3&&!(bianji&&shenhe))){
					request.setAttribute("tip", "你无权删除产品");
					request.setAttribute("result", "failure");
					return;
				}
				String condition = "buy_stockin_id = " + bean.getId() + " and product_id = " + productId + " and status = " + BuyStockinProductBean.BUYSTOCKIN_DEALED;
				if (service.getBuyStockinProductCount(condition) > 0) {
					request.setAttribute("tip", "该产品已经入库了，不能删除！");
					request.setAttribute("result", "failure");
					return;
				}

				WareService wareService = new WareService();
				voProduct product = wareService.getProduct(productId);
				wareService.releaseAll();

				service.getDbOp().startTransaction();
				if (!service.deleteBuyStockinProduct("buy_stockin_id = " + buyStockinId + " and product_id = " + productId)) {
					request.setAttribute("tip", "删除采购入库单商品失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}

				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(admin.getId());
				log.setAdminName(admin.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("删除采购入库商品[" + product.getCode() + "]");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				if (!service.addBuyAdminHistory(log)) {
					request.setAttribute("tip", "添加采购入库单更新日志失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}

				if(bean.getStatus() == BuyStockinBean.STATUS0){
					if (!service.updateBuyStockin("status="+BuyStockinBean.STATUS1, "id="+bean.getId())) {
						request.setAttribute("tip", "更新采购入库单失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
				}

				service.getDbOp().commitTransaction();
			} catch (Exception e ) {
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}
			finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-8
	 * 
	 * 说明：删除采购入库单
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void deleteBuyStockin(HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean auditing = group.isFlag(116);
		boolean bianji = group.isFlag(169);
		
		//判断是我司仓还是非我司仓
		int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));

		synchronized(buyStockinLock){
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			IMEIService iMEIService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			
			try {
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				if(bean == null){
					request.setAttribute("tip", "没有这个采购入库单！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus()==BuyStockinBean.STATUS3&&!(auditing&&bianji)){
					request.setAttribute("tip", "你没有权限修改这个采购入库单");
					request.setAttribute("result", "failure");
					return;
				}
				if (bean.getStatus() != BuyStockinBean.STATUS0 && bean.getStatus() != BuyStockinBean.STATUS1) {
					request.setAttribute("tip", "该操作已处理，不能再删除！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockinBean.STATUS1 && service.getBuyStockinProductCount("buy_stockin_id="+bean.getId())>0){
					request.setAttribute("tip", "采购入库单中存在商品，不能删除！");
					request.setAttribute("result", "failure");
					return;
				}
				service.getDbOp().startTransaction();
//				service.deleteBuyStockin("id = " + buyStockinId);
//				service.deleteBuyStockinProduct("buy_stockin_id = " + buyStockinId);
				
				if(!service.updateBuyStockin("status = "+BuyStockinBean.STATUS8, "id = "+buyStockinId)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "删除入库单失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				String isMMBMobileS = iMEIService.isBuyStockinMMBMobile(bean);
				boolean isMMBMobile = false;
				if( isMMBMobileS.equals("YES") ) {
					isMMBMobile = true;
				} else if( isMMBMobileS.equals("NO") ) {
					isMMBMobile = false;
				} else {
					request.setAttribute("tip", isMMBMobileS);
					request.setAttribute("result", "failure");
					return;
				}
				if (isMMBMobile) {
					String result = iMEIService.disposeRelationWithBuyStockin(buyStockinId,user);
					if(!result.equals("SUCCESS") ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						return;
					}
				}
				
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("删除采购入库");
				log.setType(BuyAdminHistoryBean.TYPE_DELETE);
				if (!service.addBuyAdminHistory(log)) {
					request.setAttribute("tip", "添加采购入库单删除日志失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				if (!service.updateBuyAdminHistory("deleted = 1", "log_type = " + BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN + " and log_id = " + bean.getId())) {
					request.setAttribute("tip", "更新采购入库单操作日志失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				service.getDbOp().commitTransaction();
				request.setAttribute("isBTwoC", isBTwoC);
			} catch(Exception e){ 
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}
}
