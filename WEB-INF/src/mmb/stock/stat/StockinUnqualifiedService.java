package mmb.stock.stat;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mmb.finance.service.InitVerificationService;
import mmb.finance.service.impl.InitVerificationServiceImpl;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.ware.WareService;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductSupplier;
import adultadmin.action.vo.voUser;
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
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class StockinUnqualifiedService extends BaseServiceImpl{

	public StockinUnqualifiedService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public StockinUnqualifiedService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public List getProductLineCatalogList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_line_catalog", "mmb.stock.stat.ProductLineCatalogBean");
	}
	
	/**
	 * 根据 产品线查出符合条件的 质检任务的 id
	 * @param parentIds1
	 * @param parentIds2
	 * @return
	 */
	public String getCheckBatchByProductLine(String parentIds1, String parentIds2) {
		String result = "";
		ResultSet rs = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		String parentIdCondition = "";
		if( parentIds1.length() > 0 && parentIds2.length() > 0) {
			parentIdCondition += "and (p.parent_id1 in ( "+ parentIds1 +") or p.parent_id2 in (" + parentIds2 + "))";
		} else if(parentIds1.length() > 0 && parentIds2.length() == 0 ) {
			parentIdCondition += "and p.parent_id1 in (" + parentIds1 +")";
		} else if( parentIds2.length() > 0 && parentIds1.length() == 0 ) {
			parentIdCondition += "and p.parent_id2 in (" + parentIds2 + ")";
		}
		if( parentIdCondition.length() == 0 ) {
			return result;
		}
		String sql = "select distinct(csmb.id) from check_stockin_mission_batch csmb, product p where csmb.product_id = p.id " + parentIdCondition;
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	/**
	 * 根据 产品线查询 符合条件的 不合格记录的id
	 * @param parentIds1
	 * @param parentIds2
	 * @return
	 */
	public String getUnqualifiedByProductLine(String parentIds1, String parentIds2)  {
		String result = "";
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		String parentIdCondition = "";
		if( parentIds1.length() > 0 && parentIds2.length() > 0) {
			parentIdCondition += "and (p.parent_id1 in ( "+ parentIds1 +") or p.parent_id2 in (" + parentIds2 + "))";
		} else if(parentIds1.length() > 0 && parentIds2.length() == 0 ) {
			parentIdCondition += "and p.parent_id1 in (" + parentIds1 +")";
		} else if( parentIds2.length() > 0 && parentIds1.length() == 0 ) {
			parentIdCondition += "and p.parent_id2 in (" + parentIds2 + ")";
		}
		if( parentIdCondition.length() == 0 ) {
			return result;
		} 
		String sql = "select distinct(csu.id) from check_stockin_unqualified csu, product p where csu.product_id = p.id " + parentIdCondition;
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	public ProductCodeInfoBean getProductInfoByCode(String code) {
		ProductCodeInfoBean pcib = null;
		// 数据库操作类
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return pcib;
		}
		ResultSet rs = null;

		// 构建查询语句
		String query = "select id, code, name from product";
		if (code != null) {
			query += " where code = '" + code + "'";
		}

		// 执行查询
		rs = dbOp.executeQuery(query);

		if (rs == null) {
			release(dbOp);
			return pcib;
		}

		try {
			if (rs.next()) {
				pcib = new ProductCodeInfoBean();
				pcib.setId(rs.getInt("id"));
				pcib.setCode(rs.getString("code"));
				pcib.setName(rs.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return pcib;
	}
	
	/**
	 * 生成要导出的 不合格品的 文档
	 * @param list
	 * @return
	 */
	public HSSFWorkbook exportCheckStockinUnqualifiedInfo(List list) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 在Excel 工作簿中建一工作表
		HSSFSheet sheet = workbook.createSheet("不合格接收明细表");
		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 11 * 256);
		sheet.setColumnWidth(2, 25 * 256);
		sheet.setColumnWidth(3, 6 * 256);
		sheet.setColumnWidth(4, 11 * 256);
		sheet.setColumnWidth(5, 11 * 256);
		sheet.setColumnWidth(6, 11 * 256);
		sheet.setColumnWidth(7, 13 * 256);
		sheet.setColumnWidth(8, 13 * 256);
		sheet.setColumnWidth(9, 13 * 256);
		sheet.setColumnWidth(10, 8 * 256);
		// 设置单元格格式(文本)
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));

		// 在索引0的位置创建行（第一行）
		HSSFRow row = sheet.createRow(0);

		HSSFCell cell1 = row.createCell(0);// 第一列
		HSSFCell cell2 = row.createCell(1);
		HSSFCell cell3 = row.createCell(2);
		HSSFCell cell4 = row.createCell(3);
		HSSFCell cell5 = row.createCell(4);
		HSSFCell cell6 = row.createCell(5);
		HSSFCell cell7 = row.createCell(6);
		HSSFCell cell8 = row.createCell(7);
		HSSFCell cell9 = row.createCell(8);
		HSSFCell cell10 = row.createCell(9);
		HSSFCell cell11 = row.createCell(10);
		// HSSFCell cell11 = row.createCell(10);
		// 定义单元格为字符串类型
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell5.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell6.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell7.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell8.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell9.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell10.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell11.setCellType(HSSFCell.CELL_TYPE_STRING);
		// cell11.setCellType(HSSFCell.CELL_TYPE_STRING);

		/*
		 * cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
		 * cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
		 * cell3.setEncoding(HSSFCell.ENCODING_UTF_16);
		 */
		// 在单元格中输入数据
		cell1.setCellValue("序号");
		cell2.setCellValue("产品编号");
		cell3.setCellValue("原名称");
		cell4.setCellValue("数量");
		cell5.setCellValue("到货时间");
		cell6.setCellValue("调拨时间");
		cell7.setCellValue("地区");
		cell8.setCellValue("调拨单号");
		cell9.setCellValue("采购入库单号");
		cell10.setCellValue("预计到货单号");
		cell11.setCellValue("状态");
		// cell11.setCellValue("查看详情");
		for (int i = 0; i < list.size(); i++) {
			CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean)list.get(i);
			row = sheet.createRow( i + 1);
			// 1 序号 String
			HSSFCell cellc1 = row.createCell(0);
			cellc1.setCellStyle(cellStyle);
			cellc1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			// cell.setEncoding();
			cellc1.setCellValue(i + 1);
			// 2产品编号id String
			HSSFCell cellc2 = row.createCell(1);
			cellc2.setCellStyle(cellStyle);
			cellc2.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc2.setCellValue(csub.getProduct().getCode());
			// 3 原名称String
			HSSFCell cellc3 = row.createCell(2);
			cellc3.setCellStyle(cellStyle);
			cellc3.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc3.setCellValue(csub.getProduct().getOriname());
			// 4 数量int
			HSSFCell cellc4 = row.createCell(3);
			cellc4.setCellStyle(cellStyle);
			cellc4.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			// cell.setEncoding();
			cellc4.setCellValue(csub.getCount());
			// 5 到货时间double
			HSSFCell cellc5 = row.createCell(4);
			cellc5.setCellStyle(cellStyle);
			cellc5.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc5.setCellValue(StringUtil.convertNull(StringUtil.cutString(csub.getStockinDatetime(), 19)));
			// 6 调拨时间 int
			HSSFCell cellc6 = row.createCell(5);
			cellc6.setCellStyle(cellStyle);
			cellc6.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc6.setCellValue(StringUtil.convertNull(StringUtil.cutString(csub.getExchangeDatetime(), 19)));
			//7地区
			HSSFCell cellc7 = row.createCell(6);
			cellc7.setCellStyle(cellStyle);
			cellc7.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc7.setCellValue(StringUtil.convertNull(csub.getAreaName()));
			// 7 调拨单号double
			HSSFCell cellc8 = row.createCell(7);
			cellc8.setCellStyle(cellStyle);
			cellc8.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc8.setCellValue(csub.getExchangeCode());
			// 8 采购入库单号 int
			HSSFCell cellc9 = row.createCell(8);
			cellc9.setCellStyle(cellStyle);
			cellc9.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc9.setCellValue(csub.getBuyStorageCode());
			// 9 预计到货单号double
			HSSFCell cellc10 = row.createCell(9);
			cellc10.setCellStyle(cellStyle);
			cellc10.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc10.setCellValue(csub.getBuyStockCode());
			// 10 状态double
			HSSFCell cellc11 = row.createCell(10);
			cellc11.setCellStyle(cellStyle);
			cellc11.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc11.setCellValue(csub.getStatusName());

		}

		return workbook;
	}
	
	/**
	 * 生成对应的要导出的质检报表的文件
	 * @param list
	 * @return
	 */
	public HSSFWorkbook exportCheckReportInfo(List list) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		// 在Excel 工作簿中建一工作表
		HSSFSheet sheet = workbook.createSheet("质检结果报表");
		sheet.setColumnWidth(0, 12 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 15 * 256);
		sheet.setColumnWidth(3, 30 * 256);
		sheet.setColumnWidth(4, 13 * 256);
		sheet.setColumnWidth(5, 10 * 256);
		sheet.setColumnWidth(6, 10 * 256);
		sheet.setColumnWidth(7, 10 * 256);
		sheet.setColumnWidth(8, 10 * 256);
		sheet.setColumnWidth(9, 8 * 256);
		sheet.setColumnWidth(10, 8 * 256);
		sheet.setColumnWidth(11, 8 * 256);
		sheet.setColumnWidth(12, 25 * 256);
		sheet.setColumnWidth(13, 25 * 256);
		// 设置单元格格式(文本)
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFCellStyle cellStyle2=workbook.createCellStyle();     
		cellStyle2.setWrapText(true);     

		// 在索引0的位置创建行（第一行）
		HSSFRow row = sheet.createRow(0);

		HSSFCell cell1 = row.createCell(0);// 第一列
		HSSFCell cell2 = row.createCell(1);
		HSSFCell cell3 = row.createCell(2);
		HSSFCell cell4 = row.createCell(3);
		HSSFCell cell5 = row.createCell(4);
		HSSFCell cell6 = row.createCell(5);
		HSSFCell cell7 = row.createCell(6);
		HSSFCell cell8 = row.createCell(7);
		HSSFCell cell9 = row.createCell(8);
		HSSFCell cell10 = row.createCell(9);
		HSSFCell cell11 = row.createCell(10);
		HSSFCell cell12 = row.createCell(11);
		HSSFCell cell13 = row.createCell(12);
		// HSSFCell cell11 = row.createCell(10);
		// 定义单元格为字符串类型
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell5.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell6.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell7.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell8.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell9.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell10.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell11.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell12.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell13.setCellType(HSSFCell.CELL_TYPE_STRING);
		// cell11.setCellType(HSSFCell.CELL_TYPE_STRING);

		 
		// 在单元格中输入数据
		cell1.setCellValue("产品编号");
		cell2.setCellValue("供应商名称");
		cell3.setCellValue("小店名称");
		cell4.setCellValue("原名称");
		cell5.setCellValue("预计到货单号");
		cell6.setCellValue("地区");
		cell7.setCellValue("商品线");
		cell8.setCellValue("预计到货量");
		cell9.setCellValue("实际到货量");
		cell10.setCellValue("合格数量");
		cell11.setCellValue("不合格数");
		cell12.setCellValue("不合格率");
		cell13.setCellValue("不合格原因说明");
		// cell11.setCellValue("查看详情");
		if(list != null ) {
			for (int i = 0; i < list.size(); i++) {
				CheckStockinMissionBatchBean csmbb = (CheckStockinMissionBatchBean)list.get(i);
				row = sheet.createRow( i + 1);
				// 1 产品编号 String
				HSSFCell cellc1 = row.createCell(0);
				cellc1.setCellStyle(cellStyle);
				cellc1.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc1.setCellValue(csmbb.getProduct().getCode());
				// 2供应商名称id String
				HSSFCell cellc2 = row.createCell(1);
				cellc2.setCellStyle(cellStyle);
				cellc2.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc2.setCellValue(csmbb.getSupplierName());
				// 3 小店名称String
				HSSFCell cellc3 = row.createCell(2);
				cellc3.setCellStyle(cellStyle);
				cellc3.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc3.setCellValue(csmbb.getProduct().getName());
				// 4原名称int
				HSSFCell cellc4 = row.createCell(3);
				cellc4.setCellStyle(cellStyle);
				cellc4.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc4.setCellValue(csmbb.getProduct().getOriname());
				// 5预计到货单号double
				HSSFCell cellc5 = row.createCell(4);
				cellc5.setCellStyle(cellStyle);
				cellc5.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc5.setCellValue(csmbb.getCheckStockinMission().getBuyStockinCode());
				//地区
				HSSFCell cellc6 = row.createCell(5);
				cellc6.setCellStyle(cellStyle);
				cellc6.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc6.setCellValue(csmbb.getCheckStockinMission().getWareAreaName());
				// 6 商品线 int
				HSSFCell cellc7 = row.createCell(6);
				cellc7.setCellStyle(cellStyle);
				cellc7.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc7.setCellValue(csmbb.getProduct().getProductLineName());
				// 7 预计到货量double
				HSSFCell cellc8 = row.createCell(7);
				cellc8.setCellStyle(cellStyle);
				cellc8.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				// cell.setEncoding();
				cellc8.setCellValue(csmbb.getBuyStockProduct().getBuyCount());
				// 8 实际到货量 int
				HSSFCell cellc9 = row.createCell(8);
				cellc9.setCellStyle(cellStyle);
				cellc9.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				// cell.setEncoding();
				cellc9.setCellValue(csmbb.getStockinCount());
				// 9 合格数量double
				HSSFCell cellc10 = row.createCell(9);
				cellc10.setCellStyle(cellStyle);
				cellc10.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc10.setCellValue(csmbb.getQualifiedCount());
				// 10不合格数量double
				HSSFCell cellc11 = row.createCell(10);
				cellc11.setCellStyle(cellStyle);
				cellc11.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc11.setCellValue(csmbb.getUnqualifiedNumber());
				// 11不合格率double
				HSSFCell cellc12 = row.createCell(11);
				cellc12.setCellStyle(cellStyle);
				cellc12.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc12.setCellValue(csmbb.getCheckCount() == 0 ? "" : df.format(((double)csmbb.getUnqualifiedNumber()/(double)csmbb.getCheckCount())*100) +"%");
				// 12不合格原因说明double
				HSSFCell cellc13 = row.createCell(12);
				cellc13.setCellStyle(cellStyle2);
				cellc13.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc13.setCellValue(csmbb.getUnqualifiedReasons());

			}
		}
		return workbook;
	}
	
	/**
	 * 这个入库单操作中 加入了出库存操作之外的所有操作，库存随后会被调拨单调走， 所以并没有实际上加上
	 * @param count1
	 * @param product
	 * @param stock
	 * @param user
	 * @param csmb 
	 * @param service
	 * @param wareService
	 * @param psService
	 * @param cargoService
	 * @param supplierService
	 * @param statService 
	 * @return
	 * @throws Exception
	 */
	public BuyStockinBean transformStockinFromStock(int count1,voProduct product, BuyStockBean stock, voUser user, CheckStockinMissionBean csmb, IStockService service, WareService wareService, IProductStockService psService, ICargoService cargoService, ISupplierService supplierService,FinanceReportFormsService fService, StatService statService, boolean complete) throws Exception {
		InitVerificationService initVerificationService = new InitVerificationServiceImpl();
		List<BaseProductInfo> bpiList = new ArrayList<BaseProductInfo>();
		//新采购进货单编号：CGJH20090601001
		String code = service.generateBuyStockinCodeBref();
		BuyStockinBean stockin = new BuyStockinBean();
		stockin.setBuyStockId(stock.getId());
		stockin.setCreateDatetime(DateUtil.getNow());
		stockin.setConfirmDatetime(DateUtil.getNow());
		stockin.setStatus(BuyStockinBean.STATUS0);
		stockin.setCode(code);
		//stockin.setId(service.getNumber("id", "buy_stockin", "max", "id > 0")+1);
		if(stock.getArea() == 0){
			stockin.setStockArea(ProductStockBean.AREA_BJ);
		}else if(stock.getArea()==1){
			stockin.setStockArea(ProductStockBean.AREA_GF);
		}else if(stock.getArea()==3){
			stockin.setStockArea(ProductStockBean.AREA_ZC);
		} else if( stock.getArea() == 4 ) {
			stockin.setStockArea(ProductStockBean.AREA_WX);
		}
		stockin.setStockType(ProductStockBean.STOCKTYPE_CHECK);
		stockin.setRemark("");
		stockin.setCreateUserId(user.getId());
		stockin.setSupplierId(stock.getSupplierId());//供应商id
		stockin.setBuyOrderId(stock.getBuyOrderId());
		if (!service.addBuyStockin(stockin)) {
			throw new Exception("添加采购入库单时 数据库操作失败！");
		}
		if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
			throw new Exception("添加采购入库单时 数据库操作失败！");
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
			throw new Exception("在添加生成入库单日志是，数据库操作出错！");
		}
		
		//添加对应的商品
		int buyStockProductId = product.getId();
		BuyStockProductBean bsp = service.getBuyStockProduct("buy_stock_id = " + stock.getId() +" and product_id = "+buyStockProductId);
		if( bsp == null ) {
			throw new Exception("在对应的采购预计单中 没有找到对应的商品！");
		}
		BuyStockinProductBean bsip = new BuyStockinProductBean();
		voProduct product1 = wareService.getProduct(bsp.getProductId());
		ProductStockBean psb = psService.getProductStock("product_id=" + product1.getId() + " and type=" + stockin.getStockType() + " and area=" + stockin.getStockArea());
		if(psb == null ) {
			throw new Exception("没有找到 商品:" + product.getCode() + "的库存信息！");
		}
		
		int bsipId = service.getNumber("id", "buy_stockin_product", "max", "id > 0") + 1;
		bsip.setCreateDatetime(DateUtil.getNow());
		bsip.setConfirmDatetime(DateUtil.getNow());
		bsip.setId(bsipId);
		bsip.setBuyStockinId(stockin.getId());
		bsip.setStockInId(psb.getId());
		bsip.setProductCode(product1.getCode());
		bsip.setProductId(product1.getId());
		bsip.setRemark("");
		bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
		bsip.setPrice3(bsp.getPurchasePrice());
		bsip.setProductProxyId(bsp.getProductProxyId());
		bsip.setOriname(product1.getOriname());
		bsip.setStockInCount(count1);
		
		List bsipList = new ArrayList();
		bsipList.add(bsip);
		if(!psService.checkBuyStockinProductCount(service.getDbOp(), stockin.getId(), count1, bsp.getBuyCount(), bsp.getProductId(), stock.getId())) {
			throw new Exception("采购入库单商品过多！");
		}

		//log记录
		log = new BuyAdminHistoryBean();
		log.setAdminId(user.getId());
		log.setAdminName(user.getUsername());
		log.setLogId(stockin.getId());
		log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
		log.setOperDatetime(DateUtil.getNow());
		log.setRemark("添加采购入库单商品["+product1.getCode()+"]");
		log.setType(BuyAdminHistoryBean.TYPE_ADD);
		if( !service.addBuyAdminHistory(log) ) {
			throw new Exception("添加日志时，数据库操作失败！");
		}
	
		//-------------审核采购入库单--------------
		
		  //审核通过  
		
		//获取税点
		BuyOrderBean buyOrder = service.getBuyOrder("id=(select buy_order_id from buy_stock where id="+stock.getId()+")");
		double taxPoint = 0;
		if(buyOrder!=null){
			taxPoint = buyOrder.getTaxPoint();
		}
		
		if( !service.updateBuyStockin("status="+BuyStockinBean.STATUS6+", auditing_user_id = " + user.getId(), "id=" + stockin.getId())) {
			throw new Exception ("更改入库单状态失败！");
		}
		//审核采购入库单
		stockin.setStatus(BuyStockinBean.STATUS6);
		stockin.setAuditingUserId(user.getId());
		//log记录
		double totalMoney=0;
		///财务入库明细表添加数据	//计算些入库单，入库的总金额
//		if(bsipList!=null&&bsipList.size()>0){
//			BuyStockinProductBean bsipb=null;
//			FinanceBuyProductBean fbpb=null;
//			ResultSet rs=null;
//			String sql="SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
//					+"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
//			for(int i=0;i<bsipList.size();i++){
//				bsipb=(BuyStockinProductBean)bsipList.get(i);
//				
//				fbpb=new FinanceBuyProductBean();
//			//	fbpb.setBalanceMode(buyOrder.getBalanceMode());
//				fbpb.setBillsNumCode(stockin.getCode());
//				fbpb.setBuyOrderCode(buyOrder.getCode());
//				fbpb.setCreateDateTime(DateUtil.getNow());
//				fbpb.setProductCount(bsipb.getStockInCount());
//				fbpb.setTaxPoint(Arith.round(buyOrder.getTaxPoint(),4));
//				fbpb.setTicket(buyOrder.getTicket());
//				//税后单价
//				fbpb.setProductPrice(Arith.round(Arith.mul(bsipb.getPrice3(), Arith.add(1,buyOrder.getTaxPoint())),3));
//				fbpb.setProductId(bsipb.getProductId());
//				fbpb.setSupplierId(bsipb.getProductProxyId());
//				fbpb.setType(0);//采购入库单用0表示，具体查看FinanceBuyPayBean类
//				rs=fService.getDbOp().executeQuery(sql+bsipb.getProductId());
//				while(rs.next()){
//					
//					fbpb.setProductLineId(rs.getInt(1));//添加产品线
//				}
//				if( !fService.addFinanceBuyProductBean(fbpb)){
//					throw new Exception("添加财务数据时，数据库操作失败！");
//				}
//				totalMoney=Arith.add(totalMoney, Arith.mul(bsipb.getStockInCount(), Arith.mul(bsipb.getPrice3(),Arith.add(1, taxPoint))));
//				totalMoney=Arith.round(totalMoney, 2);
//			}
//		}
//		
//		//入库单后，为财务表添加数据
//		
//		FinanceBuyPayBean fbp=new FinanceBuyPayBean();
//		fbp.setTaxPoint(Arith.round(buyOrder.getTaxPoint(), 4));
//		fbp.setTicket(buyOrder.getTicket());
//		fbp.setBillsNumCode(stockin.getCode());
//		fbp.setType(0);//采购入库单用0表示，具体查看FinanceBuyPayBean类
//		fbp.setSupplierId(buyOrder.getProxyId());
//		//fbp.setBalanceMode(buyOrder.getBalanceMode());
//		fbp.setBuyOrderCode(buyOrder.getCode());
//	
//		fbp.setCreateDateTime(DateUtil.getNow());
//		fbp.setMoney(totalMoney);
//		if( !fService.addFinanceBuyPayBean(fbp) ) {
//			throw new Exception("添加财务数据，数据库操作失败！");
//		}//财务表添加
		//---------------------------------
		BuyAdminHistoryBean log1 = new BuyAdminHistoryBean();
		log1.setAdminId(user.getId());
		log1.setAdminName(user.getUsername());
		log1.setLogId(stockin.getId());
		log1.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
		log1.setOperDatetime(DateUtil.getNow());
		log1.setRemark("审核通过");
		log1.setType(BuyAdminHistoryBean.TYPE_MODIFY);

		//--
		if( !service.addBuyAdminHistory(log1)) {
			throw new Exception("添加日志时，数据库操作失败！");
		}
		String set = null;
		ProductStockBean ps = null;
		float _price3 = bsip.getPrice3();
		bsip.setPrice3(Double.valueOf(String.valueOf(Arith.mul(bsip.getPrice3(),Arith.add(1,taxPoint)))).floatValue());
		
		if(bsip.getStockInCount() <= 0){
			throw new Exception("采购入库量不能为0，操作失败！");
		}

		if(product.getIsPackage()==1){
			throw new Exception("入库单中包含有套装产品，不能入库！");
		}
		product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
		ps = psService.getProductStock("id=" + bsip.getStockInId());
		float price5 = 0;
		float price3 = 0;
		price3 = bsip.getPrice3();
		int totalCount = product.getStockAll() + product.getLockCountAll();
		price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (bsip.getPrice3() * bsip.getStockInCount())) / (totalCount + bsip.getStockInCount()) * 1000))/1000;

		/*set = "status = " + BuyStockinProductBean.BUYSTOCKIN_DEALED
		+ ", remark = '操作前库存" + ps.getStock()
		+ ",操作后库存" + (ps.getStock() + bsip.getStockInCount())
		+ "', confirm_datetime = now()";
		service.updateBuyStockinProduct(set, "id = " + bsip.getId());*/
		bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
		bsip.setRemark("操作前库存" + ps.getStock()
		+ ",操作后库存" + (ps.getStock() + bsip.getStockInCount()));
		bsip.setConfirmDatetime(DateUtil.getNow());
		//psService.updateProductStock("stock=(stock + " + sh.getStockInCount() + ")", "id=" + sh.getStockInId());
		//--=改待验库库存=--
		/*if(!psService.updateProductStockCount(bsip.getStockInId(), bsip.getStockInCount())){
			request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
			request.setAttribute("result", "failure");
			statService.getDbOp().rollbackTransaction();
			return mapping.findForward(IConstants.FAILURE_KEY);
		}*/
//		service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId());
		
		//财务数据填充：finance_product表--------liuruilan---------
//		FinanceProductBean fProduct = fService.getFinanceProductBean("product_id = " + product.getId());
//		if(fProduct == null){
//			throw new Exception("查询异常，请与管理员联系！");
//		}
//		float priceSum = Arith.mul(price5, totalCount + bsip.getStockInCount());
//		float priceHasticket = 0;
//		float priceNoticket = 0;
//		float priceSumHasticket = fProduct.getPriceSumHasticket();
//		float priceSumNoticket = fProduct.getPriceSumNoticket();
//		int ticket = FinanceSellProductBean.queryTicket(service.getDbOp(), stock.getCode());	//是否含票
//		if(ticket == -1){
//			throw new Exception("查询异常，请与管理员联系！");
//		}
//		int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), bsip.getProductId(), ticket);
//		set = "price =" + price5 + ", price_sum =" + priceSum;
//		if(ticket == 0){	//0-有票
//			_price3 = (float) Arith.div(Arith.mul(_price3, Arith.add(1, taxPoint)), 1.17);
//			//计算公式：(fProduct.getPriceSumHasticket() + (_price3 * _count)) / (totalCount + _count)
//			priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(_price3, bsip.getStockInCount())), Arith.add(_count, bsip.getStockInCount())), 2);
//			priceSumHasticket = Arith.mul(priceHasticket,  bsip.getStockInCount() + _count);
//			set += ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//		}
//		if(ticket == 1){	//1-无票
//			_price3 = (float) Arith.mul(_price3, Arith.add(1, taxPoint));
//			priceNoticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumNoticket(), Arith.mul(_price3, bsip.getStockInCount())), Arith.add(_count, bsip.getStockInCount())), 2);
//			priceSumNoticket = Arith.mul(priceNoticket,  bsip.getStockInCount() + _count);
//			set += ", price_noticket =" + priceNoticket + ", price_sum_noticket =" + priceSumNoticket;
//		}
//		if( !fService.updateFinanceProductBean(set, "product_id = " + product.getId())){
//			throw new Exception("修改操作数据时，数据库操作失败！");
//		}
		//--------------liuruilan----------------
		
		//更新货位库存2011-04-19
		CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ps.getArea());
		CargoProductStockBean cps = null;
		CargoInfoBean cargo = null;
		List cocList = cargoService.getCargoAndProductStockList(
							"ci.stock_type = "+ps.getType()+" and ci.area_id = "+inCargoArea.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2+
							" and cps.product_id = "+bsip.getProductId(), -1, -1, "ci.id desc");
		
		//更改待验库对应货位库存
		if(cocList == null || cocList.size() == 0){//产品首次入库，无暂存区绑定货位库存信息
			 cargo = cargoService.getCargoInfo("stock_type = "+ps.getType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2);
			if(cargo == null){
				throw new Exception("目的待验库缓存区货位未设置，请先添加后再完成入库！");
			}
			cps = new CargoProductStockBean();
			cps.setCargoId(cargo.getId());
			cps.setProductId(bsip.getProductId());
			//注意这里不加货位库存了所以 stockCount 改为0直接加入到锁定中
			cps.setStockCount(0);
			cargoService.addCargoProductStock(cps);
			cps.setId(cargoService.getDbOp().getLastInsertId());
			
			if( !cargoService.updateCargoInfo("status = " + CargoInfoBean.STATUS0, "id = "+cargo.getId())) {
				throw new Exception("获取目的货位时，发生错误！");
			}
		}else{
			cps = (CargoProductStockBean)cocList.get(0);
			/*cargoService.updateCargoProductStockCount(cps.getId(), bsip.getStockInCount());*/
		}

		//更新订单已入库量和已入库总金额
		String area = "";
		if(stockin.getStockArea()==ProductStockBean.AREA_BJ){
			area = "bj";
		}else if(stockin.getStockArea()==ProductStockBean.AREA_GF){
			area = "gd";
		}else if(stockin.getStockArea()==ProductStockBean.AREA_ZC){//增城用北京的字段
			area = "bj";
		} else if ( stockin.getStockArea() == ProductStockBean.AREA_WX ) {
			area = "gd";
		} 
//		set = "stockin_count_"+area+"=(stockin_count_"+area+" + "+bsip.getStockInCount()+")," +
//		"stockin_total_price =(stockin_total_price+"+bsip.getStockInCount()*bsip.getPrice3()+")";
//		String condition2 = "buy_order_id="+stock.getBuyOrderId()+" and product_id="+product.getId();
//		if( !service.updateBuyOrderProduct(set, condition2)) {
//			throw new Exception("修改采购订单数据时，数据库操作失败！");
//		}

		//添加批次记录
		//添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
//		StockBatchBean batch = null;
//		batch = service.getStockBatch("code='" + stock.getCode() + "' and product_id = " + bsip.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_CHECK + " and stock_area = " + stockin.getStockArea());
//		if( batch == null ) {
//			batch = new StockBatchBean();
//			batch.setCode(stock.getCode());
//			batch.setProductId(bsip.getProductId());
//			batch.setPrice(bsip.getPrice3());
//			batch.setBatchCount(bsip.getStockInCount());
//			batch.setProductStockId(ps.getId());
//			batch.setStockArea(stockin.getStockArea());
//			batch.setStockType(ProductStockBean.STOCKTYPE_CHECK);
//			batch.setCreateDateTime(DateUtil.getNow());
//			batch.setTicket(ticket);
//			service.addStockBatch(batch);
//		} else {
//			if( !service.updateStockBatch("batch_count = " + (batch.getBatchCount() + bsip.getStockInCount()), "code='" + stock.getCode() + "' and product_id = " + bsip.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_CHECK + " and stock_area = " + stockin.getStockArea())) {
//				throw new Exception("修改批次 信息时， 数据库操作失败！");
//			}
//		}
//		
		//添加批次操作记录
//		StockBatchLogBean batchLog = new StockBatchLogBean();
//		batchLog.setCode(batch.getCode());
//		batchLog.setStockType(batch.getStockType());
//		batchLog.setStockArea(batch.getStockArea());
//		batchLog.setBatchCode(batch.getCode());
//		batchLog.setBatchCount(bsip.getStockInCount());
//		batchLog.setBatchPrice(batch.getPrice());
//		batchLog.setProductId(batch.getProductId());
//		batchLog.setRemark("采购入库");
//		batchLog.setCreateDatetime(DateUtil.getNow());
//		batchLog.setUserId(user.getId());
//		if( !service.addStockBatchLog(batchLog)) {
//			throw new Exception("添加批次信息时，数据库操作失败！");
//		}

		//判断并插入供货商关联信息
		voProductSupplier productSupplier = supplierService.getProductSupplierInfo("product_id = "+bsip.getProductId()+" and supplier_id = "+bsip.getProductProxyId());
		if(productSupplier == null){
			SupplierStandardInfoBean supplierStandardInfo = supplierService.getSupplierStandardInfo("id = "+bsip.getProductProxyId());
			
			productSupplier = new voProductSupplier();
			productSupplier.setProduct_id(bsip.getProductId());
			productSupplier.setSupplier_id(bsip.getProductProxyId());
			if(supplierStandardInfo!=null){
				productSupplier.setSupplier_name(supplierStandardInfo.getName());
			}
			if( !supplierService.addProductSupplierInfo(productSupplier)) {
				throw new Exception("添加供货商信息时，数据库操作失败！");
			}
		}
		
		if (!service.addBuyStockinProduct(bsip)) {
			throw new Exception("添加添加采购入库单商品时 数据库操作失败！");
		}
		
		// 审核通过，就加 进销存卡片
		product.setPsList(psService.getProductStockList("product_id=" + bsip.getProductId(), -1, -1, null));
		cps = (CargoProductStockBean)cargoService.getCargoAndProductStockList("cps.id = "+cps.getId(), 0, 1, "cps.id asc").get(0);

		//计算入库金额
		double totalPrice = bsip.getStockInCount()*_price3;
		
		//采购入库明细
		CheckStockinMissionDetailBean csmdBean = new CheckStockinMissionDetailBean();
		csmdBean.setBuyStockinCode(stockin.getCode());
		csmdBean.setBuyStockinCount(bsip.getStockInCount());
		csmdBean.setBuyStockinCreateDateTime(DateUtil.getNow());
		csmdBean.setBuyStockinId(stockin.getId());
		csmdBean.setMissionId(csmb.getId());
		csmdBean.setProductCode(product.getCode());
		csmdBean.setProductId(product.getId());
		if(!statService.addCheckStockinMissionDetail(csmdBean)){
			throw new Exception("添加入库明细失败！");
		}
		
		// 入库卡片
		StockCardBean sc = new StockCardBean();
		sc.setCardType(StockCardBean.CARDTYPE_BUYSTOCKIN);
		sc.setCode(stockin.getCode());
		sc.setCreateDatetime(DateUtil.getNow());
		sc.setStockType(stockin.getStockType());
		sc.setStockArea(stockin.getStockArea());
		sc.setProductId(bsip.getProductId());
		sc.setStockId(bsip.getStockInId());
		sc.setStockInCount(bsip.getStockInCount());
		sc.setStockInPriceSum(totalPrice);
//		sc.setStockInPriceSum((new BigDecimal(sh.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(sh.getPrice3()))).doubleValue());
		//由于前面并没有把库存实际的加到 库存当中去 而是在后面 的调拨中直接锁在了 库存当中 所以这里的库存会存在出入 要加上入库的存量
		sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()) + bsip.getStockInCount());
		sc.setStockAllArea(product.getStock(stockin.getStockArea()) + product.getLockCount(stockin.getStockArea()) + bsip.getStockInCount());
		sc.setStockAllType(product.getStockAllType(stockin.getStockType()) + product.getLockCountAllType(stockin.getStockType()) + bsip.getStockInCount());
		sc.setAllStock(product.getStockAll() + product.getLockCountAll() + bsip.getStockInCount());
		sc.setStockPrice(price5);
		sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
		if( !psService.addStockCard(sc)) {
			throw new Exception("添加入库卡片时， 数据库操作失败！");
		}
		
//		//财务进销存卡片---liuruilan-----
//		FinanceStockCardBean fsc = new FinanceStockCardBean();
//		int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), sc.getStockArea(), sc.getStockType(), ticket, sc.getProductId());
//		int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, sc.getStockType(), ticket, sc.getProductId());
//		int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), sc.getStockArea(), -1, ticket, sc.getProductId());
//		fsc.setCardType(sc.getCardType());
//		fsc.setCode(sc.getCode());
//		fsc.setCreateDatetime(DateUtil.getNow());
//		fsc.setStockType(sc.getStockType());
//		fsc.setStockArea(sc.getStockArea());
//		fsc.setProductId(sc.getProductId());
//		fsc.setStockId(sc.getStockId());
//		fsc.setStockInCount(sc.getStockInCount());	
//		fsc.setCurrentStock(currentStock);	//只记录分库总库存
//		fsc.setStockAllArea(stockAllArea);
//		fsc.setStockAllType(stockAllType);
//		fsc.setAllStock(sc.getAllStock());
//		fsc.setStockPrice(price5);
////		fsc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
//		
//		fsc.setType(fsc.getCardType());
//		fsc.setIsTicket(ticket);
//		fsc.setStockBatchCode(stock.getCode());
//		fsc.setBalanceModeStockCount(bsip.getStockInCount() + _count);
//		if(ticket == 0){
//			fsc.setStockInPriceSum(Arith.round(Arith.div(totalPrice, 1.17), 2));
//			fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//		}
//		if(ticket == 1){
//			fsc.setStockInPriceSum(Arith.round(totalPrice, 2));
//			fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceNoticket)));
//		}
//		double tmpPrice = Double.parseDouble(String.valueOf(Arith.add(priceSumHasticket, priceSumNoticket)));
//		fsc.setAllStockPriceSum(tmpPrice);
//		if( !fService.addFinanceStockCardBean(fsc)) {
//			throw new Exception("添加财务卡片时， 数据库操作失败！");
//		}
//		
		
		//货位入库卡片
		CargoStockCardBean csc = new CargoStockCardBean();
		csc.setCardType(CargoStockCardBean.CARDTYPE_BUYSTOCKIN);
		csc.setCode(stockin.getCode());
		csc.setCreateDatetime(DateUtil.getNow());
		csc.setStockType(stockin.getStockType());
		csc.setStockArea(stockin.getStockArea());
		csc.setProductId(bsip.getProductId());
		csc.setStockId(cps.getId());
		csc.setStockInCount(bsip.getStockInCount());
		csc.setStockInPriceSum(totalPrice);
		csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType())+ bsip.getStockInCount());
		csc.setAllStock(product.getStockAll() + product.getLockCountAll()+ bsip.getStockInCount());
		csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount()+ bsip.getStockInCount());
		csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
		csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
		csc.setStockPrice(price5);
		csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
		if( !cargoService.addCargoStockCard(csc)) {
			throw new Exception("添加货位入库卡片时，数据库操作失败！");
		}

		
		//拼装 bean 为打印时提供全部信息
		bsip.setTotalStockBeforeStockin(sc.getAllStock() - bsip.getStockInCount());
		if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1())!=null){
			String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()).getName();
			product.setProductLineName(productLineName);
		}else if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId2())!=null){
			String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId2()).getName();
			product.setProductLineName(productLineName);
		}else if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId3())!=null){
			String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId3()).getName();
			product.setProductLineName(productLineName);
		}else{
			product.setProductLineName("");
		}
		bsip.setProduct(product);
		List stockinProductList = new ArrayList();
		stockinProductList.add(bsip);
		stockin.setStockinProductList(stockinProductList);
		initVerificationService.insertRInitData(stockinProductList, user.getId(), wareService.getDbOp());
		
		//封装接口所需的数据
		BaseProductInfo bpi = new BaseProductInfo();
		//product_stock的id
		bpi.setProductStockId(ps.getId());
		//入库量
		bpi.setInCount(bsip.getStockInCount());
		//入库价
		bpi.setInPrice(_price3);
		//产品id
		bpi.setId(bsip.getProductId());
		bpiList.add(bpi);
		//获取入库工厂类
		FinanceBaseDataService buyStockinBase = 
			FinanceBaseDataServiceFactory.constructFinanceBaseDataService(
						FinanceStockCardBean.CARDTYPE_BUYSTOCKIN, service.getDbOp().getConn());
		//处理批次和进销存卡片
		buyStockinBase.acquireFinanceBaseData(bpiList, stockin.getCode(), user.getId(), stockin.getStockType(), stockin.getStockArea());
	
		
		
		if(!service.completeBuyOrder(user, stock, buyOrder,complete)){
			wareService.getDbOp().rollbackTransaction();
			throw new Exception("自动完成订单失败！");
		}
		return stockin;
	}
	
	
	public StockExchangeBean buildExchangeToBackWare(int count1, voProduct product, voUser user, BuyStockinBean stockin, IStockService service, IProductStockService psService, ICargoService cargoService, StatService statService) throws Exception {
		StockExchangeBean bean = new StockExchangeBean();
        bean.setCreateDatetime(DateUtil.getNow());
        String day = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        String name = day + "库存调拨";
        bean.setName(name);
        bean.setRemark("");
        bean.setStatus(StockExchangeBean.STATUS2);
        bean.setConfirmDatetime(DateUtil.getNow());
        Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String brefCode = "DB" + sdf.format(cal.getTime());
        bean.setCode(brefCode);
        bean.setCreateUserId(user.getId());
        bean.setCreateUserName(user.getUsername());
        bean.setStockOutOperName("");
        bean.setAuditingUserName("");
        bean.setStockInOperName("");
        bean.setAuditingUserName2("");
        bean.setStockInArea(stockin.getStockArea());
        bean.setStockOutArea(stockin.getStockArea());
        bean.setStockInType(CargoInfoBean.STOCKTYPE_BACK);
        bean.setStockOutType(stockin.getStockType());
        bean.setPriorStatus(StockExchangeBean.PRIOR_STATUS0);
        if (!psService.addStockExchange(bean)) {
            throw new Exception("添加调拨单失败！");
        }
        int id = service.getDbOp().getLastInsertId();
        
		bean.setId(id);
		
		//此处修改调拨单Code
		String newCode = null;
		if(id > 9999){
			String strId = String.valueOf(id);
			newCode = strId.substring(strId.length()- 4, strId.length());
		} else {
			DecimalFormat df2 = new DecimalFormat("0000");
			newCode = df2.format(id);
		}
		String totalCode = brefCode + newCode;
		StringBuilder updateBuf = new StringBuilder();
		updateBuf.append("update stock_exchange set code='" + totalCode + "' where id=").append(id);
		if( !service.getDbOp().executeUpdate(updateBuf.toString())) {
			throw new Exception("添加调拨单失败！");
		}
        bean.setCode(totalCode);
        
        //log记录
        StockAdminHistoryBean log2 = new StockAdminHistoryBean();
        log2.setAdminId(user.getId());
        log2.setAdminName(user.getUsername());
        log2.setLogId(id);
        log2.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
        log2.setOperDatetime(DateUtil.getNow());
        log2.setRemark("新建商品调配操作：" + bean.getName());
        log2.setType(StockAdminHistoryBean.CREATE);
        if ( !service.addStockAdminHistory(log2)) {
        	throw new Exception("添加日志时，数据库操作失败！");
        }
        
        //--------给调拨单里添加商品----------
        // 只有 未处理、出库处理中、出库审核未通过 三种状态下 可以 添加 商品库存调货记录
        int stockInArea = bean.getStockInArea();
        int stockInType = bean.getStockInType();
        int stockOutArea = bean.getStockOutArea();
        int stockOutType = bean.getStockOutType();
        if(stockInArea == stockOutArea && stockInType == stockOutType){
        	throw new Exception("不能在同一个库中调配商品！");
        }
        //新货位管理判断
        CargoInfoAreaBean inCargoArea2 = cargoService.getCargoInfoArea("old_id = "+bean.getStockInArea());
        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockOutArea());
        List cpsOutList = null;
        if(stockOutType == ProductStockBean.STOCKTYPE_CHECK){
        	cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockOutType()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = 2", -1, -1, "ci.id asc");
        } else {
        	throw new Exception("调拨单源货位类型异常!");
        }
        if(cpsOutList == null || cpsOutList.size()==0){
        	throw new Exception("源货位号无效，请重新输入！");
        }
        CargoProductStockBean cpsOut = (CargoProductStockBean)cpsOutList.get(0);
        CargoInfoBean ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea2.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2); 
        if(ciIn == null){
        	throw new Exception("目的库无缓存区货位信息，请先添加货位后，再进行调拨操作！");
        }
        //----------给不合格记录添加调拨单和入库单的信息-------------
		//采购入库单号
		//将所有add和update集中起来包括一些查询的最后id的地方
		ProductStockBean psIn = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockInType + " and area=" + stockInArea);
        ProductStockBean psOut = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockOutType + " and area=" + stockOutArea);
        if(psOut == null){
        	//如果 出货的地方 没有存放这个商品的库，则添加一个空的库，库存为0
//        	psOut = new ProductStockBean();
//        	psOut.setProductId(product.getId());
//        	psOut.setType(stockOutType);
//        	psOut.setArea(stockOutArea);;
//        	psOut.setStock(0);
//        	if( !psService.addProductStock(psOut)) {
        		throw new Exception("商品没有库存记录！");
//        	}
//        	psOut = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockOutType + " and area=" + stockOutArea);
        }
        //这里加这个数字 是为了后面记录日志的时候使用方便
        psOut.setStock(psOut.getStock() + count1);
        if(psIn == null){
        	//如果 入库的地方 没有存放这个商品的库，则添加一个空的库，库存为0
//        	psIn = new ProductStockBean();
//        	psIn.setProductId(product.getId());
//        	psIn.setType(stockInType);
//        	psIn.setArea(stockInArea);;
//        	psIn.setStock(0);
//        	if( !psService.addProductStock(psIn)) {
        		throw new Exception("商品没有库存记录！");
//        	}
//        	psIn = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockInType + " and area=" + stockInArea);
        }
        
      //添加调配记录
        StockExchangeProductBean sep = null;
        sep = new StockExchangeProductBean();
        sep.setCreateDatetime(DateUtil.getNow());
        sep.setConfirmDatetime(null);
        sep.setStockExchangeId(bean.getId());
        sep.setProductId(product.getId());
        sep.setRemark("");
        sep.setStatus(StockExchangeProductBean.STOCKOUT_DEALED);
        sep.setStockOutCount(count1);
        sep.setStockInCount(count1);
        sep.setStockOutId(psOut.getId());
        sep.setStockInId(psIn.getId());
        sep.setReason(1);
        sep.setReasonText("");
		
		//添加调拨单商品
		if (!psService.addStockExchangeProduct(sep)) {
            throw new Exception("调拨单商品添加失败！");
        }
		int stockExchangeId = psService.getDbOp().getLastInsertId();
		sep.setId(stockExchangeId);
		//添加调拨产品货位信息
        int sepId = psService.getDbOp().getLastInsertId();
        List cpsInList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockInType()+" and ci.area_id = "+inCargoArea2.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2, 0, 1, "ci.id asc");
        CargoProductStockBean cpsIn = null;
        if(cpsInList == null || cpsInList.size() == 0){
        	cpsIn = new CargoProductStockBean();
        	cpsIn.setCargoId(ciIn.getId());
        	cpsIn.setProductId(product.getId());
        	if( !cargoService.addCargoProductStock(cpsIn)) {
        		throw new Exception("添加调拨单信息时，数据库操作失败！");
        	}
        	cpsIn.setId(cargoService.getDbOp().getLastInsertId());
        	
			if( !cargoService.updateCargoInfo("status = 0", "id = "+ciIn.getId())) {
				throw new Exception("添加调拨单信息时，数据库操作失败！");
			}
        }else{
        	cpsIn = (CargoProductStockBean)cpsInList.get(0);
        }
        StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
        sepcOut.setStockExchangeProductId(sepId);
        sepcOut.setStockExchangeId(bean.getId());
        sepcOut.setStockCount(count1);
        sepcOut.setCargoProductStockId(cpsOut.getId());
        sepcOut.setCargoInfoId(cpsOut.getCargoId());
        sepcOut.setType(0);
        if( !psService.addStockExchangeProductCargo(sepcOut)) {
        	throw new Exception("添加调拨单信息时，数据库操作失败！");
        }
        StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
        sepcIn.setStockExchangeProductId(sepId);
        sepcIn.setStockExchangeId(bean.getId());
        sepcIn.setStockCount(count1);
        sepcIn.setCargoProductStockId(cpsIn.getId());
        sepcIn.setCargoInfoId(cpsIn.getCargoId());
        sepcIn.setType(1);
        if( !psService.addStockExchangeProductCargo(sepcIn)) {
        	throw new Exception("添加调拨单信息时，数据库操作失败！");
        }
		//log添加调拨商品的记录
        StockAdminHistoryBean log3 = new StockAdminHistoryBean();
        log3.setAdminId(user.getId());
        log3.setAdminName(user.getUsername());
        log3.setLogId(bean.getId());
        log3.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
        log3.setOperDatetime(DateUtil.getNow());
        log3.setRemark("修改商品调配操作：" + bean.getName() + ",添加了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:" + sep.getStockOutCount());
        log3.setType(StockAdminHistoryBean.CHANGE);
        if( !service.addStockAdminHistory(log3)) {
        	throw new Exception("添加日志信息时，数据库操作失败！");
        }
		String set = null;
        //调拨单改为已确认
        //-------------------------------------------------
		set = "remark = '操作前库存"
				+ psOut.getStock() + ",操作后库存"
				+ (psOut.getStock() - sep.getStockOutCount())
				+ "', confirm_datetime = now()";
		if( !psService.updateStockExchangeProduct(set, "id = " + sep.getId())) {
			throw new Exception("添加日志信息时，数据库操作失败！");
		}
		//service.updateProductStock("stock=(stock - " + sep.getStockOutCount() + "), lock_count=(lock_count + " + sep.getStockOutCount() + ")", "id=" + sep.getStockOutId());
		if(!psService.updateProductLockCount(sep.getStockOutId(), sep.getStockOutCount())){
			throw new Exception("库存操作失败，可能是库存不足，请与管理员联系！");
		}
		
		// log记录 确认出库记录
		StockAdminHistoryBean log4 = new StockAdminHistoryBean();
		log4.setAdminId(user.getId());
		log4.setAdminName(user.getUsername());
		log4.setLogId(bean.getId());
		log4.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		log4.setOperDatetime(DateUtil.getNow());
		log4.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]出库");
		log4.setType(StockAdminHistoryBean.CHANGE);
		if ( !service.addStockAdminHistory(log4)) {
			throw new Exception("添加日志信息时，数据库操作失败！");
		}
		
		//锁定货位库存
		//出库
		List sepcOutList = psService.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sepId+" and type = 0", -1, -1, "id asc");
		for(int i=0;i<sepcOutList.size();i++){
			sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
			if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
            	throw new Exception("商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
            }
		}
		
		return bean;
	}

	public List getCheckStockinUnqualifiedList(String sql, int index, int count,
			String orderBy) {
		List result = new ArrayList();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		if( orderBy != null && !orderBy.equals("")) {
			sql += " order by " + orderBy;
		}
		sql = DbOperation.getPagingQuery(sql, index, count);
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				CheckStockinUnqualifiedBean csuBean = new CheckStockinUnqualifiedBean();
				csuBean.setId(rs.getInt("id"));
				csuBean.setMissionId(rs.getInt("mission_id"));
				csuBean.setMissionBatchId(rs.getInt("mission_batch_id"));
				csuBean.setProductId(rs.getInt("product_id"));
				csuBean.setCount(rs.getInt("count"));
				csuBean.setStockinDatetime(rs.getString("stockin_date_time"));
				csuBean.setExchangeDatetime(rs.getString("exchange_date_time"));
				csuBean.setExchangeCode(rs.getString("exchange_code"));
				csuBean.setBuyStorageCode(rs.getString("buy_storage_code"));
				csuBean.setBuyStockCode(rs.getString("buy_stock_code"));
				csuBean.setStatus(rs.getInt("status"));
				csuBean.setRemark(rs.getString("remark"));
				csuBean.setArea(rs.getInt("area"));
				result.add(csuBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public int getCheckStockinUnqualifiedCount(String sql) {
		
		int result = 0;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		rs = dbOp.executeQuery(sql);
		try {
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public String getMissionIdsBySupplierName(String supplierName) {
		String result = "-1,";
		String sql = "select id from check_stockin_mission where supplier_name like '" + supplierName + "%'";
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getString("id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public int getCheckStockinMissionBatchCount(String sqlCount) {
		int result = 0;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		rs = dbOp.executeQuery(sqlCount);
		try {
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public List getCheckStockinMissionBatchList(String sql, int index,
			int count, String orderBy) {
		List result = new ArrayList();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		if( orderBy != null && !orderBy.equals("")) {
			sql += " order by " + orderBy;
		}
		sql = DbOperation.getPagingQuery(sql, index, count);
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				CheckStockinMissionBatchBean csmbBean = new CheckStockinMissionBatchBean();
				csmbBean.setId(rs.getInt("id"));
				csmbBean.setMissionId(rs.getInt("mission_id"));
				csmbBean.setBuyStockinId(rs.getInt("buy_stockin_id"));
				csmbBean.setProductId(rs.getInt("product_id"));
				csmbBean.setBuyCount(rs.getInt("buy_count"));
				csmbBean.setStockinCount(rs.getInt("stockin_count"));
				csmbBean.setCheckCount(rs.getInt("check_count"));
				csmbBean.setQualifiedCount(rs.getInt("qualified_count"));
				csmbBean.setStockinDatetime(rs.getString("stockin_date_time"));
				csmbBean.setCompleteDatetime(rs.getString("complete_date_time"));
				csmbBean.setStatus(rs.getInt("status"));
				csmbBean.setSupplierId(rs.getInt("supplier_id"));
				csmbBean.setSupplierName(rs.getString("supplier_name"));
				result.add(csmbBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public void checkBuyOrderStatus( BuyOrderBean bob ) throws Exception {
		if( bob.getStatus() == BuyOrderBean.STATUS6) {
			throw new Exception("对应的采购订单状态是采购已完成，不能继续进行操作");
		}
		if( bob.getStatus() == BuyOrderBean.STATUS7) {
			throw new Exception("对应的采购订单状态是申请完成，不能继续进行操作");
		}
		if( bob.getStatus() == BuyOrderBean.STATUS8) {
			throw new Exception("对应的采购订单状态是已经删除，不能继续进行操作");
		}
	}

	public void checkBuyStockStatus(BuyStockBean bsBean) throws Exception {
		if( bsBean.getStatus() == BuyStockBean.STATUS6) {
			throw new Exception("对应的采购预计单状态是采购已完成，不能继续进行操作");
		}
		if( bsBean.getStatus() == BuyStockBean.STATUS8) {
			throw new Exception("对应的采购预计单状态是已经删除，不能继续进行操作");
		}
	}

	
}
