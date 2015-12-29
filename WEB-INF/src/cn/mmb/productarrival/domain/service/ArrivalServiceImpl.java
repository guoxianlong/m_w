/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:44:31 
 * @version V1.0   
 */
package cn.mmb.productarrival.domain.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import adultadmin.util.DateUtil;
import cn.mmb.productarrival.domain.model.ArrivalMessageModel;
import cn.mmb.productarrival.infrastructrue.persistence.ArrivalMapper;
import cn.mmb.productarrival.infrastructrue.transdto.EasyuiPage;
import cn.mmb.productarrival.infrastructrue.transdto.QueryParams;

/** 
 * @ClassName: ArrivalServiceImpl 
 * @Description: 商品到货信息service
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:44:31  
 */
@Service
public class ArrivalServiceImpl implements ArrivalService {
	
	@Resource
	private ArrivalMapper ArrivalMapper;

	/** 
	 * @Description: 获取供应商信息
	 * @return List<Map<String,Object>> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月14日 下午2:28:05 
	 */
	public List<Map<String, Object>> getSupplier() {
		return ArrivalMapper.getSupplier();
	}

	@Override
	public EasyuiPage<ArrivalMessageModel> getArrivalPage(QueryParams params,
			EasyuiPage<ArrivalMessageModel> page) {
		return ArrivalMapper.getArrivalPage(params, page);
	}

	@Override
	public void addArrivalMessage(ArrivalMessageModel model) {
		ArrivalMapper.addArrivalMessage(model);
	}

	public void delArrivalMessage(int id, String userName) {
		ArrivalMapper.delArrivalMessage(id, userName);
	}

	public void exportList(QueryParams params, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (StringUtils.isNotBlank(params.getReceiver())) {
				params.setReceiver(new String(params.getReceiver().getBytes("iso-8859-1"),"utf-8"));
			}
			InputStream is = new FileInputStream(this.getClass().getResource("/").toURI().resolve("../config").getPath() + "/product_arrival_message.xls");
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook workbook = new HSSFWorkbook(fs);
			HSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
			EasyuiPage<ArrivalMessageModel> data = ArrivalMapper.getArrivalPage(params, new EasyuiPage<ArrivalMessageModel>());
			List<ArrivalMessageModel> list = data.getResult();
			for (int i = 0;i<list.size();i++) {
				Row rowDate = sheet.createRow(i+1);
				ArrivalMessageModel md = list.get(i);
				rowDate.createCell(0).setCellValue(i+1);
				rowDate.createCell(1).setCellValue(md.getArrivalTime());
				rowDate.createCell(2).setCellValue(md.getAreaName());
				rowDate.createCell(3).setCellValue(md.getCodeFlagName());
				rowDate.createCell(4).setCellValue(md.getWaybillCode());
				rowDate.createCell(5).setCellValue(md.getDeliverCorpName());
				rowDate.createCell(6).setCellValue(md.getBuyPlanCode());
				rowDate.createCell(7).setCellValue(md.getSupplierName());
				rowDate.createCell(8).setCellValue(md.getArrivalCount());
				rowDate.createCell(9).setCellValue(md.getTemporaryCargo());
				rowDate.createCell(10).setCellValue(md.getProductLineName());
				rowDate.createCell(11).setCellValue(md.getBusinessUnit());
				rowDate.createCell(12).setCellValue(md.getReceiver());
				rowDate.createCell(13).setCellValue(md.getAddUser());
				rowDate.createCell(14).setCellValue(md.getAddTime());
				rowDate.createCell(15).setCellValue(md.getIsPrintBillName());
				rowDate.createCell(16).setCellValue(md.getArrivalException());
			}
			response.reset();
			String agent = request.getHeader("User-Agent");
			boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);
			String excelFileName = "商品到货信息列表" + DateUtil.getNow("yyyyMMddHHssmmSS");
			if (isMSIE) {
				excelFileName = URLEncoder.encode(excelFileName, "UTF-8");
			} else {
				excelFileName = new String(excelFileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			response.setContentType("application/msxls");
			response.addHeader("Content-Disposition", "attachment;filename=" + excelFileName + ".xls");
			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getCountByWayBillCode(String waybillCode) {
		return ArrivalMapper.getCountByWayBillCode(waybillCode);
	}

}
