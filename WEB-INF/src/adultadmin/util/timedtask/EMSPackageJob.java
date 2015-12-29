package adultadmin.util.timedtask;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPException;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.impl.StockServiceImpl;
import adultadmin.util.Base64;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.hollycrm.bigaccount.webservice.printservice.GetPrintDatas;
import com.hollycrm.bigaccount.webservice.printservice.GetPrintDatasPortType;
/**
 * 
 * 作者：张小磊
 * 
 * 创建日期：2013-3-23
 * 
 * 根据大客户唯一标识获取详情单号接口
 * 功能:通过大客户唯一标识，如某电商公司的配货单号获取详情单号
 * 
 * */
public class EMSPackageJob implements Job {
	private static final QName EMS_SERVICE_NAME = new QName("http://printService.webservice.bigaccount.hollycrm.com", "getPrintDatas");
	public static byte[] sortingBatchLock = new byte[0];
	private static final int PACKAGECODE_COUNT =98;//每次定时任务获取的EMS包裹单数量
	private static final int MAX_COUNT =80000;//EMS数据库中最大包裹单数量
	private static final int LNSW_MAX_COUNT =10000;//辽宁省外包裹单最大数量
	private static final int OTHER_MAX_COUNT =20000;//其他省外包裹单最大数量
	
	private static final String APPKEY="S9a9C8c73476e3AC5";
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		getOrderStock(9, 1, MAX_COUNT,StockServiceImpl.SW_USERNAME,StockServiceImpl.SW_PASSWORD);
		getOrderStock(25, 1, OTHER_MAX_COUNT,StockServiceImpl.GX_USERNAME,StockServiceImpl.GX_PASSWORD);
		getOrderStock(28, 1, OTHER_MAX_COUNT,StockServiceImpl.JX_USERNAME,StockServiceImpl.JX_PASSWORD);
		getOrderStock(29, 1, LNSW_MAX_COUNT,StockServiceImpl.SH_USERNAME,StockServiceImpl.SH_PASSWORD);
		getOrderStock(31, 1, OTHER_MAX_COUNT,StockServiceImpl.HN_USERNAME,StockServiceImpl.HN_PASSWORD);
		getOrderStock(32, 1, OTHER_MAX_COUNT,StockServiceImpl.HB_USERNAME,StockServiceImpl.HB_PASSWORD);
		getOrderStock(34, 1, OTHER_MAX_COUNT,StockServiceImpl.SXWXDS_USERNAME,StockServiceImpl.SXWXDS_PASSWORD);
		getOrderStock(34, 1, OTHER_MAX_COUNT,StockServiceImpl.SXGZDS_USERNAME,StockServiceImpl.SXGZDS_PASSWORD);
		getOrderStock(34, 1, OTHER_MAX_COUNT,StockServiceImpl.SXCDDS_USERNAME,StockServiceImpl.SXCDDS_PASSWORD);
		getOrderStock(35, 1, OTHER_MAX_COUNT,StockServiceImpl.GZ_USERNAME,StockServiceImpl.GZ_PASSWORD);
		getOrderStock(37, 1, OTHER_MAX_COUNT,StockServiceImpl.WX_USERNAME,StockServiceImpl.WX_PASSWORD);
		getOrderStock(44, 4, LNSW_MAX_COUNT,StockServiceImpl.LNGNDS_USERNAME,StockServiceImpl.LNGNDS_PASSWORD);
		getOrderStock(52, 1, OTHER_MAX_COUNT,StockServiceImpl.SCCD_USERNAME,StockServiceImpl.SCCD_PASSWORD);
		getOrderStock(51, 1, OTHER_MAX_COUNT,StockServiceImpl.YN_USERNAME,StockServiceImpl.YN_USERNAME);
		getOrderStock(54, 1, OTHER_MAX_COUNT,StockServiceImpl.FJ_USERNAME,StockServiceImpl.FJ_USERNAME);
		
		 
		
		getOrderStock(37, 8, OTHER_MAX_COUNT,StockServiceImpl.WX_USERNAME,StockServiceImpl.WX_PASSWORD);
		getOrderStock(43, 8, LNSW_MAX_COUNT,StockServiceImpl.LNSNDS_USERNAME,StockServiceImpl.LNSNDS_PASSWORD);
		getOrderStock(44, 8, LNSW_MAX_COUNT,StockServiceImpl.LNGNDS_USERNAME,StockServiceImpl.LNGNDS_PASSWORD);
		getOrderStock(28, 8, OTHER_MAX_COUNT,StockServiceImpl.JX_USERNAME,StockServiceImpl.JX_PASSWORD);
		getOrderStock(31, 8, OTHER_MAX_COUNT,StockServiceImpl.HN_USERNAME,StockServiceImpl.HN_PASSWORD);
		getOrderStock(54, 8, OTHER_MAX_COUNT,StockServiceImpl.FJ_USERNAME,StockServiceImpl.FJ_USERNAME);
		getOrderStock(25, 8, OTHER_MAX_COUNT,StockServiceImpl.GX_USERNAME,StockServiceImpl.GX_PASSWORD);
		getOrderStock(52, 8, OTHER_MAX_COUNT,StockServiceImpl.SCCD_USERNAME,StockServiceImpl.SCCD_PASSWORD);
		getOrderStock(35, 8, OTHER_MAX_COUNT,StockServiceImpl.GZ_USERNAME,StockServiceImpl.GZ_PASSWORD);
		getOrderStock(51, 8, OTHER_MAX_COUNT,StockServiceImpl.YN_USERNAME,StockServiceImpl.YN_USERNAME);
		
		getOrderStock(34, 8, OTHER_MAX_COUNT,StockServiceImpl.SXWXDS_USERNAME,StockServiceImpl.SXWXDS_PASSWORD);
		getOrderStock(34, 8, OTHER_MAX_COUNT,StockServiceImpl.SXGZDS_USERNAME,StockServiceImpl.SXGZDS_PASSWORD);
		getOrderStock(34, 8, OTHER_MAX_COUNT,StockServiceImpl.SXCDDS_USERNAME,StockServiceImpl.SXCDDS_PASSWORD);

	}
	
	/**
	 * 通过大客户唯一标识，如某电商公司的配货单号获取详情单号
	 * @param deliver 快递公司ID
	 * @param businessType 业务类型，必填，1为标准快递，2为代收货款，3为收件人付费，4为经济快递（传数字）
	 * @throws maxCount 包裹单最大数量
	 */
	public boolean getOrderStock(int deliver, int businessType, int maxCount,String username, String password) {
		//System.out.println(DateUtil.getNow() + "快递公司id为"+deliver+"-EMS获取详情单号接口任务开始");
		synchronized (sortingBatchLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			
			String returnXml = null;
			String content = new String();//快递公司 日志内容
			try {
				//String sql = "select count(id) from deliver_package_code where  used=0 and deliver="+deliver;
				
				String sql="";
				if(businessType == 8){
					 sql = "select count(id) from deliver_package_code where  used=0 and deliver="+deliver+" and deliver_type=1";
				}
				else{
					 sql = "select count(id) from deliver_package_code where  used=0 and deliver="+deliver+" and deliver_type=0";
				}
				
				ResultSet rs = dbOp.executeQuery(sql);
				int count = 0;//ems可用包裹单数量
				if(rs.next()){
					count=rs.getInt(1);
				}
				rs.close();
				if(count<maxCount){
					for(int j=0;j<10;j++){
						java.lang.String string = "<orderId>" + "<bigAccountDataId>1234567890</bigAccountDataId>" + "</orderId>";
						for (int i = 0; i < PACKAGECODE_COUNT; i++) {
							string += "<orderId>" + "<bigAccountDataId>1234567890</bigAccountDataId>" + "</orderId>";
						}
						//System.out.println("Invoking getBillNo...");
						java.lang.String _getBillNo_xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
						"<XMLInfo>" + 
							"<sysAccount>"+username+"</sysAccount>" + 
							"<passWord>"+password+"</passWord>" + 
							"<appKey>"+APPKEY+"</appKey>" + 
							"<businessType>"+businessType+"</businessType>" + 
							"<orderIds>" + string + "</orderIds>" + 
						"</XMLInfo>";
						//System.out.println(_getBillNo_xmlStr);
						// 加密
						URL wsdlURL = GetPrintDatas.WSDL_LOCATION;
						GetPrintDatas ss = new GetPrintDatas(wsdlURL, EMS_SERVICE_NAME);
						GetPrintDatasPortType port = ss.getGetPrintDatasHttpSoap12Endpoint();
						// 设置超时
						Client client = ClientProxy.getClient(port);
						HTTPConduit http = (HTTPConduit) client.getConduit();
						HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
						httpClientPolicy.setConnectionTimeout(3000);
						httpClientPolicy.setAllowChunking(false);
						httpClientPolicy.setReceiveTimeout(30000);
						http.setClient(httpClientPolicy);
						java.lang.String _getBillNo__return = null;
						try{
							_getBillNo__return = port.getBillNo(new String(Base64.encode(_getBillNo_xmlStr.getBytes())));
						}catch(Exception e){
							//e.printStackTrace();    
						//上面的账号还是测试的，所有的接口都需要加入这个输出，用来区分是哪个接口的哪类错误！
							Throwable ta = e.getCause();    
							if(ta instanceof SocketTimeoutException){  
								content = "快递公司id为"+deliver+"-EMS省外回传接口--响应超时";
								System.out.println("=========快递公司id为"+deliver+"-EMS省外回传接口--响应超时=========");    
								}else if(ta instanceof HTTPException){ 
									content = "快递公司id为"+deliver+"-EMS省外回传接口--服务端地址无效404";
									System.out.println("============快递公司id为"+deliver+"-EMS省外回传接口--服务端地址无效404==========");    
									}else if(ta instanceof XMLStreamException){ 
										content = "快递公司id为"+deliver+"-EMS省外回传接口--连接超时";
										System.out.println("========快递公司id为"+deliver+"-EMS省外回传接口--连接超时===========");    
										}else{    
											content = "快递公司id为"+deliver+"-EMS省外回传接口--其他exception";
											System.out.println("============快递公司id为"+deliver+"-EMS省外回传接口--其他exception==========");    
							}
							return false;
						}
						// 解密
						returnXml = new String(Base64.decode(_getBillNo__return.getBytes()));
						//Document doc = null;
						Document doc = DocumentHelper.parseText(returnXml);
						Element rootElt = doc.getRootElement(); // 获取根节点
					
						Element a = rootElt.element("assignIds");
						if(a!=null){
							List assignIdList = a.elements("assignId");
							for (int i = 0; i < assignIdList.size(); i++) {
								Element assignId = (Element) assignIdList.get(i);
								Element billno = assignId.element("billno");
								//System.out.println(billno.getData());
								//String sqlEms = "INSERT INTO deliver_package_code (deliver,package_code,used)VALUES" + "("+deliver+"," + "'" + billno.getData() + "',0)";
								
								String sqlEms="";
								if(businessType == 8){
									sqlEms = "INSERT INTO deliver_package_code (deliver,package_code,used,deliver_type)VALUES" + "("+deliver+"," + "'" + billno.getData() + "',0,1)";
								}
								else{
									sqlEms = "INSERT INTO deliver_package_code (deliver,package_code,used,deliver_type)VALUES" + "("+deliver+"," + "'" + billno.getData() + "',0,0)";
								}
								
								dbOp.executeUpdate(sqlEms);
							}
						}else{
							content = "快递公司id为"+deliver+"-未获取包裹单号,接口返回值:"+returnXml;
							System.out.println("快递公司id为"+deliver+"-未获取包裹单号,接口返回值:"+returnXml);
							return false;
						}
					}
				}
				//System.out.println(DateUtil.getNow() + "快递公司id为"+deliver+"-EMS获取详情单号接口任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				if(returnXml!=null){
					content = "快递公司id为"+deliver+"-获取的EMS包裹单号插入失败！接口返回结果："+returnXml;
					System.out.println("快递公司id为"+deliver+"-获取的EMS包裹单号插入失败！接口返回结果："+returnXml);
				}
			}finally {
				if(content!=null && content.length()>50){
					content = content.substring(0, 50);
				}
				String deliverLog = "INSERT INTO deliver_log (deliver_id,create_datetime,content) VALUES ("+deliver+",'"+DateUtil.getNow()+"','"+content+"') ";
				dbOp.executeUpdate(deliverLog);
				dbOp.release();
			}
		}
		return true;
	}
}
