package adultadmin.action.bybs;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.StockOperationAction;
import mmb.ware.WareService;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.bybs.CheckBsbyInfo;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class CheckBsbyCargoAction extends Action {
	private static byte[] stockLock = new byte[0];
	private Workbook wb;
	private Sheet sheet;
	private Row row;

	/**
	 * 上传文件
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
			BsbyForm uf = (BsbyForm) form;
			String content = uf.getContent();
			FormFile file = uf.getFile();
	
			String filename = file.getFileName();
	
			String fileType = filename.substring(filename.lastIndexOf("."),
					filename.length());
			InputStream is = file.getInputStream();
			synchronized(stockLock){
				readExcelContent(is, fileType, content, request, response);
			}
			return mapping.findForward("success");
	}

	public void readExcelContent(InputStream is, String fileType,
			String content, HttpServletRequest request, HttpServletResponse response)
			throws java.io.IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
		}

		String str = "";
		// 创建工作文档对象
		if (fileType.equals(".xls")) {
			wb = new HSSFWorkbook(is);
		} else if (fileType.equals(".xlsx")) {
			wb = new XSSFWorkbook(is);
		} else {
			System.out.println("您的文档格式不正确！");
		}
		ArrayList<CheckBsbyInfo> errorlist = new ArrayList<CheckBsbyInfo>();

		sheet = (Sheet) wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();

		DbOperation dbOp = new DbOperation();
		try {
			
			dbOp.init("adult");

			IBsByServiceManagerService service = ServiceFactory
					.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,
							dbOp);
			ICargoService cargoService = ServiceFactory.createCargoService(
					IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			WareService wareService = new WareService(dbOp);

			// 正文内容应该从第二行开始,第一行为表头的标题
			for (int i = 1; i <= rowNum; i++) {
				row = sheet.getRow(i);
				int j = 0;
				CheckBsbyInfo checkBsbyInfo = new CheckBsbyInfo();
				voProduct product = null;
				int ben_id = 0;
				String planCountBSGD = "0";
				String planCountBYGD = "0";
				BsbyOperationnoteBean ben=null;
				while (j < colNum) {

					/**
					 * 校验 是否符合条件
					 * 
					 */

					String tempstr = getCellFormatValue(row.getCell((short) j))
							.trim();

					if (j == 0) {
						checkBsbyInfo.setBsbyCode(tempstr);

						ben = service
								.getBuycode("receipts_number ='" + tempstr.trim()
										+ "' and current_type=6 "); // 根据报损报益的code获取信息

						if (ben == null) {	
							checkBsbyInfo.setRemark("您所上传的单据号不存在,或者已经审核通过！");
							request.setAttribute("msg", "您所上传的单据号不存在,或者已经审核通过！");
							j = colNum;
							continue;
						} else {
							ben_id = ben.getId();
							ResultSet rs = service.getDbOp().executeQuery("select id from after_sale_bsby_product where bsby_operationnote_id=" + ben_id);
							if (rs.next()) {
								checkBsbyInfo.setRemark("售后库的报损报溢审核请到售后仓内管理-报损报溢-报损报溢列表查找"+tempstr.trim()+"的报损报溢单进行审核操作！");
								request.setAttribute("msg", "您所上传的单据号为售后报损报溢单，不能在此审核！");
								j = colNum;
								ben_id = 0;
								rs.close();
								continue;
							}
							rs.close();
						}
					}
					if (j == 1) {
						product = wareService.getProduct(tempstr.trim());
						if (product == null) {
							checkBsbyInfo.setRemark("与邮件发送的产品编号不一样");
							j = colNum;

							continue;

						} else {
							List list = service.getBsbyProductList("operation_id=" + ben_id, -1, -1, null);
							if (list != null && list.size() > 0) {
								BsbyProductBean bsbyProduct = (BsbyProductBean)list.get(0);
								if (bsbyProduct.getProduct_code().equals(product.getCode())) {
									checkBsbyInfo.setProductCode(product.getCode());
								} else {
									checkBsbyInfo.setRemark("与邮件发送的产品编号不一样");
									j = colNum;
									continue;
								}
							} else {
								checkBsbyInfo.setRemark("与邮件发送的产品编号不一样");
								j = colNum;
								continue;
							}
						}
					}
					if (j == 2 && "0".equals(planCountBSGD)) {
						if ("".equals(tempstr) || tempstr == null) {
							tempstr = "0";
						}
						checkBsbyInfo.setBsCount(Integer.valueOf(tempstr));
						planCountBSGD = tempstr;
					}
					if (j == 3 && "0".equals(planCountBYGD)) {
						if ("".equals(tempstr) || tempstr == null) {
							tempstr = "0";
						}
						checkBsbyInfo.setByCount(Integer.valueOf(tempstr));
						planCountBYGD = tempstr;
					}
					if (j == 4) {
						if (product == null) {
							
							checkBsbyInfo.setRemark("与邮件发送的产品编号不一样");

							j = colNum;
							continue;

						}
						if (ben_id == 0) {
							checkBsbyInfo.setRemark("您所上传的单据号不存在！");
							request.setAttribute("msg", "您所上传的单据号不存在！");
							j = colNum;
							continue;
						} else {
							checkBsbyInfo.setCargoCode(tempstr);
							ben = service.getBuycode("id ="
									+ ben_id + ""); // 根据id获取信息
							int x = getProductCount(product.getId(),
									ben.getWarehouse_area(),
									ben.getWarehouse_type(), dbOp);
							String bsbycount = "0";
							int countbsby = getBsByCount(dbOp, ben_id);// 根据单据id获取报损报溢的数量
							if (ben.getType() == 0) {

								if (StringUtil.toInt(planCountBYGD) > 0
										|| countbsby != StringUtil
												.toInt(planCountBSGD)) {
									checkBsbyInfo.setRemark("与邮件发送的报损数量不一样");

									j = colNum;
									continue;
								}
								// 报损
								bsbycount = planCountBSGD;
							} else {
								if (StringUtil.toInt(planCountBSGD) > 0
										|| countbsby != StringUtil
												.toInt(planCountBYGD)) {
									checkBsbyInfo.setRemark("与邮件发送的报溢数量不一样");
									j = colNum;
									continue;
								}
								bsbycount = planCountBYGD;
							}
						}

					}
					j++;
				}

				updateCheckRemark(content, request, user, errorlist, service,
						checkBsbyInfo, ben_id,psService,cargoService,ben,response);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	

		request.setAttribute("errorlist", errorlist);// 显示未通过的单据信息
	}


	private void updateCheckRemark(String content, HttpServletRequest request,
			voUser user, ArrayList<CheckBsbyInfo> errorlist,
			IBsByServiceManagerService service, CheckBsbyInfo checkBsbyInfo,
			int ben_id,IProductStockService psService,ICargoService cargoService,BsbyOperationnoteBean bean,HttpServletResponse response) {
		String zhuangtai = "";
		int fin_audit_status = 2;// 财务审核未通过
		service.getDbOp().startTransaction();
		// 添加集合 判断是否全部通过 根据remark值
		if (checkBsbyInfo.getRemark() != null) { // 说明记录的错误原因
			errorlist.add(checkBsbyInfo);
			//财务审核未通过--操作库存
			//运营审核通过的报损单需要解锁库存，售后库的单据不处理
			if (bean!=null&&bean.getType()==0&&ben_id!=0) {//报损单财务审核未通过，操作库存锁定量
				//报损单中的所有产品
				List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
				Iterator it = bsbyList.iterator();
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("msg", "货位信息异常，操作失败，请与管理员联系！");
	                    request.setAttribute("result", "failure");
	                    return;
					}
					String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
								+ "area = " + bean.getWarehouse_area() + " and type = "
								+ bean.getWarehouse_type();
					ProductStockBean psBean = psService.getProductStock(sql);
					//增加库存
					if(!psService.updateProductStockCount(psBean.getId(), bsbyProductBean.getBsby_count())){
						service.getDbOp().rollbackTransaction();
                    	request.setAttribute("msg", "库存操作失败，可能是库存不足，请与管理员联系！");
	                    request.setAttribute("result", "failure");
	                    return;
                    }
					//减去库存锁定量
					if (!psService.updateProductLockCount(psBean.getId(), -bsbyProductBean.getBsby_count())) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("msg", "库存操作失败，可能是库存不足，请与管理员联系！");
	                    request.setAttribute("result", "failure");
	                    return;
					}
					
					//解锁货位库存
					if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
						request.setAttribute("msg", "货位库存操作失败，货位冻结库存不足！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
						request.setAttribute("msg", "货位库存操作失败，货位冻结库存不足！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					
					if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
						//更新订单缺货状态
						this.updateLackOrder(bsbyProductBean.getProduct_id());
					}
				}
			}
			request.setAttribute("msg", "对不起,部分审核无法通过");
			zhuangtai = "财务审核未通过";
			fin_audit_status = 2;// 财务审核未通过
			content = checkBsbyInfo.getRemark(); // 记录错误原因
		} else {// 说明匹配合格
			zhuangtai = "已完成";
			fin_audit_status = 4;// 财务审核已经通过
			/**
			 * 单据状态改为完成 就要改变库存 如果是报损就要剪掉库存 如果是报溢就要添加批次 如果没有调整的产品
			 * 就不执行这个方法
			 */
			BsbyProductBean bsbyProductBean =  service.getBsbyProductBean("operation_id="+ben_id);
			int beforeChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type(),service.getDbOp());
			if(!ByBsAction.updateStock(bean, request, response, service.getDbOp())){
				request.setAttribute("msg", request.getAttribute("tip"));
			}
			
			/**
			 * 更改为完成后,要将最后的库存和改变后的库存的量记录
			 */
			int afterChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type(),service.getDbOp());


			if(!service.updateBsbyProductBean("before_change="+beforeChangeProductCount+", after_change="+afterChangeProductCount, "id="+bsbyProductBean.getId()))
			{
			  request.setAttribute("msg", "数据库操作失败");
			}
		}
		// 判断 errorlist的size等于0 说明全部通过
		if (errorlist.size() == 0) {
			request.setAttribute("msg", "棒极了,全部通过审核");
		}
		if (ben_id == 0) {
			if(bean==null){
				checkBsbyInfo.setRemark("您所上传的单据号不存在！");
				request.setAttribute("msg", "您所上传的单据号不存在！");
			}
		} else {
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());

			bsbyOperationRecordBean.setInformation("修改单据:"
					+ checkBsbyInfo.getBsbyCode() + "的状态为:" + zhuangtai);
			bsbyOperationRecordBean.setOperation_id(ben_id);
			if (!service.addBsbyOperationRecord(bsbyOperationRecordBean)) {

				request.setAttribute("msg", "数据库操作失败");

			}
			// 更改状态
			// 添加财务审核的信息
			if (!service.updateBsbyOperationnoteBean(
					"current_type=" + fin_audit_status
							+ " ,examineSuggestion='" + content +"', end_time='"
					+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
					+ user.getUsername() + "'",
					"id=" + ben_id)) {

				request.setAttribute("msg", "数据库操作失败");

			}
			
		}
		service.getDbOp().commitTransaction();

	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellFormatValue(Cell cell) {
		String cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			// 如果当前Cell的Type为NUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue = sdf.format(date);

				}
				// 如果是纯数字
				else {
					// 取得当前Cell的数值
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cellvalue =cell.getStringCellValue();
				}
				break;
			}
			// 如果当前Cell的Type为STRIN
			case HSSFCell.CELL_TYPE_STRING:
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			// 默认的Cell值
			default:
				cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}

	/**
	 * 根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 2010-02-22
	 * 
	 * @param productCode
	 * @param area
	 * @param type
	 * @return
	 */
	public static int getProductCount(int productid, int area, int type,
			DbOperation dbOp) {

		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory
				.createProductStockService(IBaseService.CONN_IN_SERVICE,
						service.getDbOp());
		int x = 0;
		try {
			voProduct product = wareService.getProduct(productid);
			product.setPsList(psService.getProductStockList("product_id="
					+ productid, -1, -1, null));
			// x = product.getStock(area, type) + product.getLockCount(area,
			// type);
			x = product.getStock(area, type);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
//			wareService.releaseAll();
		}
		return x;

	}

	/**
	 * 根据报损报溢单id 获取报损报益的数量
	 * 
	 * 
	 */
	public int getBsByCount(DbOperation dbOp, int bsbyId) {
		ResultSet rs = null;
		int count = 0;
		try {
			String sqlcargocode = "select count,whole_code from bsby_product_cargo "
					+ " join cargo_info on cargo_id=cargo_info.id "
					+ "where bsby_oper_id=" + bsbyId;
			String wholeCode = "";
			dbOp.prepareStatement(sqlcargocode);
			rs = dbOp.getPStmt().executeQuery();
			if (rs.next()) {
				count = rs.getInt("count");
				wholeCode = rs.getString("whole_code");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return count;
	}
	 public static void updateLackOrder(int productId){
	    	DbOperation dbOp = new DbOperation();
	    	dbOp.init("adult_slave");
	    	DbOperation dbOp2 = new DbOperation();
	    	dbOp2.init();
	    	WareService wareService = new WareService(dbOp);
	    	IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
	    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
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
//						dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
//								"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 3600 and uo.is_olduser=1 and uo.id = "+order.getId());
//						dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
//								"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+order.getId());
//						dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
//								"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+order.getId());
						StockOperationAction.updateOrderLackStatu(dbOp2,order.getId());
					}
				
	    		}
	    	}catch (Exception e) {
	    		e.printStackTrace();
			}finally{
				dbOp.release();
				dbOp2.release();
			}
	    	
	    }
	 
	 public static boolean checkStock(List orderProductList,int area) {
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
}
