package adultadmin.util.timedtask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.namespace.QName;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import adultadmin.action.vo.voOrder;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.routdata.cussrvems.webservice.web.IOException;

/**
 * 
 * 作者：张小磊
 * 
 * 创建日期：2013-3-23
 * 
 * EMS省外省内详情单打印信息更新到EMS自助服务系统接口 功能:将详情单打印信息更新到自助服务系统
 * 
 * */
public class EMSPackageJob1 implements Job {
	private static final QName EMS_SERVICE_NAME = new QName("http://printService.webservice.bigaccount.hollycrm.com", "getPrintDatas");
	public static byte[] sortingBatchLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (sortingBatchLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService wareService = new WareService(dbOp);
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			DbOperation dbOp1 = new DbOperation();
			dbOp1.init("adult");
			WareService wareService2 = new WareService(dbOp1);
			int order_id = 0;
			try {
				//System.out.println(DateUtil.getNow() + "将EMS详情单打印信息更新到自助服务系统定时任务开始");
				String query = "select order_id,id from ems_order_message where '"+DateUtil.getTimeBeforeMinutes(-240)+"'< confirm_datetime order by id desc limit 1000";
				ResultSet rs = wareService.getDbOp().executeQuery(query);
				List orderIdList = new ArrayList();
				List idList = new ArrayList();
				while (rs.next()) {
					orderIdList.add(rs.getInt(1));
					idList.add(rs.getInt(2));
				}
				rs.close();
				if (orderIdList != null && orderIdList.size() > 0) {
					for (int i = 0; i < orderIdList.size(); i++) {
						voOrder orderBean = wareService.getOrder((Integer) orderIdList.get(i));
						order_id = orderBean.getId();
						OrderStockBean orderStock = stockService.getOrderStock("status!=3 and order_id=" + (Integer) orderIdList.get(i));
						// 获取包裹单号
						String query1 = "select package_code,weight from  audit_package where order_id=" + orderIdList.get(i);
						ResultSet rs1 = wareService.getDbOp().executeQuery(query1);
						String packageCode = "";
						float weight = 0;
						if (rs1.next()) {
							packageCode = rs1.getString(1);
							weight = rs1.getFloat(2);
							// System.out.println(packageCode);
						}
						rs1.close();
						// 更新包裹单使用状态
						// String query = "update " + tableName + " set " + set
						// + " where " + condition;
						String upPackageStatus = "update deliver_package_code set used=1 where package_code='" + packageCode + "'";
						wareService2.getDbOp().executeUpdate(upPackageStatus);
						// 获取省市区街道
//						StockServiceImpl stock = new StockServiceImpl();
//						AuditPackageBean apBean = stockService.getAuditPackage("order_id=" + orderIdList.get(i));
						String sqlAddress = "select b.add_id1,b.add_id2,b.add_id3,b.add_id4,b.add_5 from user_order a join user_order_extend_info  b on a.code=b.order_code where a.id=" + orderIdList.get(i);
						ResultSet rs2 = wareService.getDbOp().executeQuery(sqlAddress);
						String province = "";
						String city = "";
						String county = "";
						String street = "";
						String address = "";
						int province_id = 0;
						int city_id = 0;
						int county_id = 0;
						int street_id = 0;
						while (rs2.next()) {
							province_id = rs2.getInt("b.add_id1");
							city_id = rs2.getInt("b.add_id2");
							county_id = rs2.getInt("b.add_id3");
							street_id = rs2.getInt("b.add_id4");
							address = rs2.getString("b.add_5");
						}
						rs2.close();
						// 查找省
						if (province_id!=0) {
							String sqlProvince = "select name from provinces where id=" + province_id;
							ResultSet rsProvince = wareService.getDbOp().executeQuery(sqlProvince);
							while (rsProvince.next()) {
								province = rsProvince.getString("name");
							}
							rsProvince.close();
						}
						// 查找市
						if (city_id!=0) {
							String sqlProvince = "select city from province_city where id=" + city_id;
							ResultSet rsCity = wareService.getDbOp().executeQuery(sqlProvince);
							while (rsCity.next()) {
								city = rsCity.getString("city");
							}
							rsCity.close();
						}
						// 查找区县
						if (county_id!=0) {
							String sqlProvince = "select area from city_area where id=" + county_id;
							ResultSet rsCounty = wareService.getDbOp().executeQuery(sqlProvince);
							while (rsCounty.next()) {
								county = rsCounty.getString("area");
							}
							rsCounty.close();
						}
						// 查找街道
						if (street_id!=0) {
							String sqlStreet = "select street from area_street where id=" + street_id;
							ResultSet rsStreet = wareService.getDbOp().executeQuery(sqlStreet);
							while (rsStreet.next()) {
								street = rsStreet.getString("street");
							}
							rsStreet.close();
						}
						street=street+address;
						//广东省外
					    int deliver = orderStock.deliver;
						if (deliver==9 || deliver==11 || deliver==25 || deliver==28 || deliver == 29 || deliver == 31 || deliver == 32 || deliver == 34 || deliver == 35 || deliver == 37 || deliver == 43 || deliver == 44 || deliver == 52|| deliver == 51|| deliver == 54) {
							if (StockServiceImpl.emsSwInterface(orderBean, packageCode, weight,province, city, county, orderBean.getAddress(), orderStock.getStockArea(),orderStock.deliver)) {
								String deleteSql = "delete from ems_order_message where id=" + idList.get(i);
								wareService.getDbOp().executeUpdate(deleteSql);
							}else {
//								String updateSql = "UPDATE ems_order_message SET last_oper_datetime = '" + DateUtil.getNow() + "' WHERE  id=" + idList.get(i);
//								wareService.getDbOp().executeUpdate(updateSql);
							}
						}
//						else if(orderStock.getDeliver()==11){//省内
//							
//							if(StockServiceImpl.emsInterface3(orderBean, packageCode,weight, province, city, county, street)){
//				    				String deleteSql = "delete from ems_order_message where order_id="+orderIdList.get(i);
//				    				wareService.getDbOp().executeUpdate(deleteSql);
//							}else{
//				    				String updateSql ="UPDATE ems_order_message SET last_oper_datetime = '"+DateUtil.getNow()+"' WHERE  order_id="+orderIdList.get(i);
//				    				wareService.getDbOp().executeUpdate(updateSql);
//							}
//						}
					}
				}
				//System.out.println(DateUtil.getNow() + "将EMS详情单打印信息更新到自助服务系统定时任务结束");
			} catch (Exception e) {
				if (order_id != 0) {
//					String updateSql = "UPDATE ems_order_message SET last_oper_datetime = '" + DateUtil.getNow() + "' WHERE  order_id=" + order_id;
//					wareService.getDbOp().executeUpdate(updateSql);
				}
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
				wareService2.releaseAll();
			}
		}
	}
	/*
	 * gzip压缩
	 * 
	 * @param s
	 * @param charset 编码方式，为空默认为utf-8
	 * @return
	 * @throws IOException
	 */

	public static String gzipCompress(String s,String charset) throws Exception {
		if(charset==null)charset="UTF-8";
		ByteArrayInputStream input = new ByteArrayInputStream(s
				.getBytes(charset));
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
		GZIPOutputStream gzout = new GZIPOutputStream(output);

		byte[] buf = new byte[1024];
		int number;

		while ((number = input.read(buf)) != -1) {
			gzout.write(buf, 0, number);
		}

		gzout.close();
		input.close();
		String result = new BASE64Encoder().encode(output.toByteArray());
		output.close();
		return result;
	}
    
	/**
	 * gzip解压
	 * 
	 * @param data
	 * @param charset 编码方式，为空默认为utf-8
	 * @return
	 * @throws IOException
	 */

	public static String gzipDecompress(String data,String charset) throws Exception {
		if(charset==null)charset="UTF-8";
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
		ByteArrayInputStream input = new ByteArrayInputStream(
				new BASE64Decoder().decodeBuffer(data));
		GZIPInputStream gzinpt = new GZIPInputStream(input);
		byte[] buf = new byte[1024];
		int number = 0;
		while ((number = gzinpt.read(buf)) != -1) {
			output.write(buf, 0, number);
		}
		gzinpt.close();
		input.close();
		String result = new String(output.toString(charset));
		output.close();
		return result;
	}
		/**
	    * 对传入的字符串进行MD5加密
	    * @param plainText
	    * @return
	    */

	   public static String MD5(String plainText, String charset) throws Exception {
	     
	         MessageDigest md = MessageDigest.getInstance("MD5");
	         md.update(plainText.getBytes(charset));
	         byte b[] = md.digest();
	         int i;
	         StringBuffer buf = new StringBuffer("");
	         for (int offset = 0; offset < b.length; offset++) {
	            i = b[offset];
	            if (i < 0)
	               i += 256;
	            if (i < 16)
	               buf.append("0");
	            buf.append(Integer.toHexString(i));
	         }
	         return buf.toString();
	   }
	   // 签名程序代码片段
	   public static String encrypt(String content, String keyValue, String charset) throws Exception {
	   	if(keyValue != null) {
	               return base64(MD5(content + keyValue, charset), charset);
	       	} 
	       	return base64(MD5(content, charset), charset);
	   }
	   /**
	    *  base64编码
	    *  
	    * @param str
	    * @return
	    * @throws Exception 
	    */

	   public static String base64(String str, String charset) throws Exception{
			   return (new BASE64Encoder()).encode(str.getBytes(charset));
	   }
	   public static void main(String args[]) { 
	        System.out.println(DateUtil.getTimeBeforeMinutes(-240)); 
	    } 

}
