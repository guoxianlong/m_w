package mmb.stock.cargo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.delivery.service.DeliveryService;
import mmb.util.ExcelUtil;
import mmb.util.excel.AbstractExcel;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sf.module.serviceprovide.service.OrderService;

import adultadmin.action.stock.AuditPackageAction;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.UserOrderServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


@RequestMapping("uploadDeliverOrderInfoController")
@Controller
public class UploadDeliverOrderInfoController {
	
	@Resource
	private DeliveryService deliveryService;

	private static Logger logger = Logger.getLogger(UploadDeliverOrderInfoController.class);
	
	/**
	 * 导入订单配送信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/upload")
	public String upload(HttpServletRequest request, HttpServletResponse response)throws Exception{

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您还没有登录");
			return "admin/orderStock/uploadDeliverOrderInfo";
		}
		
		if(user.getGroup() == null || !user.getGroup().isFlag(3057)){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您没有操作权限");
			return "admin/orderStock/uploadDeliverOrderInfo";
		}
		String pop = request.getParameter("pop");
		
		 // 转型为MultipartHttpRequest：   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        // 获得文件：   
        MultipartFile file = multipartRequest.getFile("attendance");
        // 获得输入流：   
        InputStream input = file.getInputStream();   
        // 获得文件名：   
        String attendanceFileName = file.getOriginalFilename();
		int index = attendanceFileName.lastIndexOf(".");
		String suffix = attendanceFileName.substring(index + 1, attendanceFileName.length());

		StringBuilder msg = new StringBuilder();
		
		List<String[]> rows = ExcelUtil.rosolveFile(input, suffix, 1);
        
		String result = null;
		
		if (rows != null  && rows.size() > 0) {
			int total = 0;
			int success = 0;
			GenResult gen = null;
			boolean empty = true;
			List<GenResult> list = new ArrayList<UploadDeliverOrderInfoController.GenResult>();
			for (int i = 0; i < rows.size(); i++) {			
				String[] row = rows.get(i);
				if (row == null || row.length == 0) {
					continue;
				}
				empty = true;
				for (int j = 0; j < row.length; j++) {
					if(!"".equals(row[j]))
						empty = false;
				}
				// 全部列为空不处理
				if(empty){
					continue;
				}				
				try {			
					total++;	
					if(row.length < 4){
						msg.append("第").append(i + 1).append("行, 数据列数不合法（至少4列）<br/>");
						continue;
					}
					String state = null;
					if ("0".equals(pop) && status.containsKey(row[1].trim())) {
						state = status.get(row[1].trim());
					} else if ("2".equals(pop) && statusPop.containsKey(row[1].trim())) {
						state = statusPop.get(row[1].trim());
					}
					if(state == null){
						msg.append("第").append(i + 1).append("行, 状态不存在<br/>");
						continue;
					}
					
					gen = new GenResult();
					gen.setNu(row[0].trim());
					gen.setStatus(Integer.valueOf(state));
					gen.setTime(row[2].trim());
					gen.getLongTime();// 校验时间格式
					if("".equals(row[3].trim()))
						throw new RuntimeException("配送信息为空");
					gen.setMessage(row[3].trim());				
				} catch (NullPointerException e) {
					msg.append("第").append(i + 1).append("行, 数据为空<br/>");
					continue;
				} catch (Exception e) {
					msg.append("第").append(i + 1).append("行, ").append(e.getMessage()).append("<br/>");
					continue;
				}
				
				gen.setIndex(i);
				list.add(gen);				
			}

			// 内存排序 按 status 升序排序
			Collections.sort(list, new Comparator<UploadDeliverOrderInfoController.GenResult>(){
				@Override
				public int compare(GenResult o1, GenResult o2) {
					// TODO Auto-generated method stub
					return o1.getStatus().compareTo(o2.getStatus());
				}				
			});
			
			int rowIndex = 0;
			for (int i = 0; i < list.size(); i++) {
				gen = list.get(i);
				// 排序后行号(i)已经不准确了
				rowIndex = gen.getIndex();
				try {
					String r = "";
					if ("0".equals(pop)) {
						r = save(gen,user);
					} else if ("2".equals(pop)) {
						r = savePop(gen,user);
					}
					if (!"OK".equals(r)) {
						msg.append("第").append(rowIndex + 1).append("行, ").append(r).append("<br/>");
					} else {
						success++;
					}
				} catch (Exception e) {
					msg.append("第").append(rowIndex + 1).append("行, ").append(e.getMessage()).append("<br/>");
				}
			}
			
			result = "共" + total + "行,成功" + success + "行,失败" + (total - success) + "行.";
		} else {
			result = "Excel内容为空";
			msg.append("Excel内容为空");
		}
		
		request.setAttribute("result", result);
		request.setAttribute("msg", msg.toString());
		return "admin/orderStock/uploadDeliverOrderInfo";
	}

	
	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) {
		try {
			ExportExcel excel = new ExportExcel(AbstractExcel.HSSF);
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			header.add("包裹单号");
			header.add("状态	");
			header.add("时间节点");
			header.add("配送信息");
			 
			headers.add(header);
			
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			excel.setMayMergeRow(mayMergeRow);
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();	 
			excel.setColMergeCount(headers.get(0).size());			
			List<Integer> row  = new ArrayList<Integer>();
			row.add(bodies.size()-1);
			row.add(bodies.size()-3);
			row.add(bodies.size()-5);			
			List<Integer> col  = new ArrayList<Integer>();			
			excel.setRow(row);
			excel.setCol(col);			
			excel.buildListHeader(headers);
			excel.setHeader(false);
			
			excel.buildListBody(bodies);
			
			response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename=import.xls");
			response.setContentType("application/vnd.ms-excel");
			response.setCharacterEncoding("utf-8");
			excel.getWorkbook().write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	

	private static HashMap<String, String> status = new HashMap<String, String>();
	
	static{
		status.put("已出库","0");
		status.put("已揽收","1");
		status.put("在途","2");
		status.put("到达当地","4");
		status.put("投递中", "5");
		status.put("未妥投已退回","6");
		status.put("已签收", "7");		
		status.put("未妥投开始退回","8");		
		status.put("疑难","9");		
		status.put("丢失","11");
		status.put("破损","12");
	}
	
	private static HashMap<String, String> statusPop = new HashMap<String, String>();
	
	static{
		statusPop.put("已出库","0");
		statusPop.put("已揽收","1");
		statusPop.put("在途","2");
		statusPop.put("已妥投", "7");
		statusPop.put("拒收","8");
	}
	
	
	/** 
	 * @Description: 调用pop商家配送信息接口
	 * @return String 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月4日 下午3:43:58 
	 */
	private String savePop(GenResult mGenResult,voUser user){
		DbOperation db = null;
		try {
			db = new DbOperation();
			// 获取订单id
			int id = db.getInt("select id from deliver_info_pop where deliver_code='" + StringUtil.toSql(mGenResult.getNu()) + "' order by id desc limit 1");
			if (id <= 0) {
				return "包裹单号不存在";
			}
			//调用物流配送接口
			StringBuilder json=new StringBuilder();
			json.append("{deliveryId:\"");
			json.append(mGenResult.getNu());
			json.append("\",pop:2,trace_api_dtos:[{ope_remark:\"");
			json.append(mGenResult.getMessage());
			json.append("\",ope_time:\"");
			json.append(mGenResult.getsdfTime());
			json.append("\",ope_status:").append(mGenResult.getStatus());
			json.append(",ope_name:\"").append(user.getUsername()).append("\"}]}");
			/**
			 @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
			 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
			 */
			deliveryService.processDeliverInformation(json.toString());
			
		} catch (Exception e) {
			System.err.println("getSingleInfo SQL Exception " + e.getMessage());
			return "操作失败";
		} finally {
			db.release();
		}
		return "OK";
	}

	private static String save(GenResult mGenResult,voUser user) {
		DbOperation db = null;
		WareService wareService =null;
		IAdminLogService logService = null;
		IUserOrderService userOrderService = null;
		IStockService stockService = null; 
		try {
			db = new DbOperation();
			wareService = new WareService(db);
			logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, db);
			userOrderService = new UserOrderServiceImpl(IBaseService.CONN_IN_SERVICE,db);
			stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, db); 
			// 获取订单id
			int id = db.getInt("select id from deliver_order where deliver_no='" + StringUtil.toSql(mGenResult.getNu()) + "' order by id desc limit 1");
			if (id <= 0) {
				return "包裹单号不存在";
			}
			
			// 导入的时间节点必须大于订单的交接时间，否则提示‘时间节点早于发货时间’：
			// 订单的交接时间可以取deliver_order_info里的状态是0的那个时间
			if (mGenResult.getStatus().intValue() != 0) {
				String selectTime = "SELECT i.deliver_time FROM  deliver_order AS o, deliver_order_info AS i WHERE o.id = i.deliver_id AND o.deliver_no = '" + StringUtil.toSql(mGenResult.getNu()) + "' AND i.deliver_state = 0  LIMIT 1";
				long longTime = -1;
				ResultSet rs = db.executeQuery(selectTime);
				if(rs != null){
					if(rs.next()){
						longTime = rs.getLong(1);
					}
					rs.close();
				}
				if(longTime <= 0){
					return "时间节点早于发货时间";
				}
				if(mGenResult.getLongTime() <= longTime){
					return "时间节点早于发货时间";
				}
			}
			
			String status = mGenResult.getStatus().toString();
			String sql = "update deliver_order set deliver_info='" + StringUtil.toSql(mGenResult.getMessage()) + "',deliver_state=" + StringUtil.toSql(status)
					+ ",province='" + StringUtil.toSql(mGenResult.getProvince()) + "',city='" + StringUtil.toSql(mGenResult.getCity()) + "',district='"
					+ StringUtil.toSql(mGenResult.getDistrict()) + "',post='" + StringUtil.toSql(mGenResult.getPost())
					+ "' where id=" + id;

			ResultSet rs = db.executeQuery("SELECT post_time,receive_time,deliver_state,order_id FROM deliver_order WHERE id="+id);
			int deliverState= 0;//交接状态为0
			long postTime= 0;
			long receiveTime = 0;
			int orderId=0;
			if (rs.next()) {
				deliverState=rs.getInt("deliver_state");
				postTime= rs.getTimestamp("post_time").getTime();
				receiveTime = rs.getTimestamp("receive_time").getTime();
				orderId = rs.getInt("order_id");
			}
			
			if (status.equals("2") || status.equals("4") || status.equals("5")) {				
				if(deliverState==0){
			    	sql = "update deliver_order set deliver_info='" + StringUtil.toSql(mGenResult.getMessage()) + "',deliver_state=" + StringUtil.toSql(status)
						+ ",post_time='" + mGenResult.getsdfTime() + "',province='" + StringUtil.toSql(mGenResult.getProvince()) + "',city='"
						+ StringUtil.toSql(mGenResult.getCity()) + "',district='" + StringUtil.toSql(mGenResult.getDistrict()) + "',post='"
						+ StringUtil.toSql(mGenResult.getPost()) + "' where id=" + id;
				}
			}
			if (status.equals("7") || status.equals("8") || status.equals("6")) {
				if(deliverState==0){
			    	sql = "update deliver_order set deliver_info='" + StringUtil.toSql(mGenResult.getMessage()) + "',deliver_state=" + StringUtil.toSql(status)
						+ ",receive_time='" + mGenResult.getsdfTime() + "',province='" + StringUtil.toSql(mGenResult.getProvince()) + "',city='"
						+ StringUtil.toSql(mGenResult.getCity()) + "',district='" + StringUtil.toSql(mGenResult.getDistrict()) + "',post='"
						+ StringUtil.toSql(mGenResult.getPost()) + "' where id=" + id;
				}
			} 
			db.executeUpdate(sql);
			if(status.equals("7")){
				voOrder order = wareService.getOrder("id = " + orderId+" and status=6 ");
				if(order!=null){
					if(!userOrderService.updateXXX(" status=14 ", " id="+orderId, "user_order")){
						return "更新用户订单状态失败！";
					}
					if(!stockService.updateAuditPackage(" receive_datetime='"+mGenResult.getTime()+"' ", " order_id="+orderId)){
						return "更新核对包裹签收时间失败！";
					}
					
					OrderAdminLogBean log = new OrderAdminLogBean();
	        		log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
	        		log.setUserId(user.getId());
	        		log.setUsername(user.getUsername());
	        		log.setOrderId(orderId);
	        		log.setOrderCode(order.getCode());
	        		log.setCreateDatetime(DateUtil.getNow());
	        		log.setContent("[订单状态:6->14]");
	        		if(!logService.addOrderAdminLog(log)){
	        			return "添加订单修改日志失败！";
	        		}
				}
			}
			
			db.executeUpdate("insert into deliver_order_info(deliver_id,deliver_info,deliver_time,deliver_state,add_time,province,city,district,post,source) values("
					+ id + ",'" + StringUtil.toSql(mGenResult.getMessage()) + "'," + mGenResult.getLongTime() + "," + StringUtil.toSql(status) + ","
					+ System.currentTimeMillis() + ",'" + StringUtil.toSql(mGenResult.getProvince()) + "','" + StringUtil.toSql(mGenResult.getCity()) + "','"
					+ StringUtil.toSql(mGenResult.getDistrict()) + "','" + StringUtil.toSql(mGenResult.getPost()) + "', 1)");
			logger.info("get:" + mGenResult);
		} catch (Exception e) {
			System.err.println("getSingleInfo SQL Exception " + e.getMessage());
			return "操作失败";
		} finally {
			db.release();
		}
		return "OK";
	}
	
	private static class GenResult {	
		private Integer status = 0;
		private String time = "";
		private String message = "";
		private String nu = "";
		private String key = "";
		private String province = "";
		private String city = "";
		private String district = "";
		private String post = "";
		
		/**
		 * 行的索引（从0开始）
		 */
		private int index;
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public GenResult() {
	
		}
		
		public Integer getStatus() {
			return status;
		}
	
		public void setStatus(Integer status) {
			this.status = status;
		}
	
		public String getTime() {
			return time;
		}
	
		public void setTime(String time) {
			this.time = time;
		}
	
		public String getMessage() {
			return message;
		}
	
		public void setMessage(String message) {
			this.message = message;
		}
	
		public String getNu() {
			return nu;
		}
	
		public void setNu(String nu) {
			this.nu = nu;
		}
	
		public String getKey() {
			return key;
		}
	
		public void setKey(String key) {
			this.key = key;
		}
	
		public String getProvince() {
			return province;
		}
	
		public void setProvince(String province) {
			this.province = province;
		}
	
		public String getCity() {
			return city;
		}
	
		public void setCity(String city) {
			this.city = city;
		}
	
		public String getDistrict() {
			return district;
		}
	
		public void setDistrict(String district) {
			this.district = district;
		}
	
		public String getPost() {
			return post;
		}
	
		public void setPost(String post) {
			this.post = post;
		}
	
		static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		public String getsdfTime() {
			try {
				long ftimeLong = sdf.parse(getTime()).getTime();
				if (ftimeLong < 0 || ftimeLong > System.currentTimeMillis()) {
					return sdf.format(new Date());
				}
				return getTime();
			} catch (Exception e) {
				throw new RuntimeException("时间格式有误");
			}
		}
	
		public long getLongTime() {
			try {
				long ftimeLong = sdf.parse(getTime()).getTime();
				if (ftimeLong < 0) {
					return System.currentTimeMillis();
				}
				return ftimeLong > System.currentTimeMillis() ? System.currentTimeMillis() : ftimeLong;
			} catch (Exception e) {
				throw new RuntimeException("时间格式有误");
			}
		}
	}
}
