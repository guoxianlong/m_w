package adultadmin.action.stock;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockPrintLogBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 *  <code>OrderStockExprotPrint.java</code>
 *  <p>功能:发货清单导出和打印Action 新加
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-6-13 下午01:50:52	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class OrderStockExprotPrintAction extends BaseAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int flag = StringUtil.StringToId(request.getParameter("flag"));
		String batch=request.getParameter("batch");
		String date=request.getParameter("date");
		BufferedReader reader =null;
		if(batch==null){
			reader = new BufferedReader(new StringReader(request.getParameter("orders")));
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		
		WareService wareService = new WareService(dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IBatchBarcodeService batchBarcodeService=ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, dbOp);
		IBatchBarcodeService batchBarcodeService2=ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		String tmp = null; 
		try{
			String batchOrderCode="";
			if(batch!=null){//来自按批次打印页面
//				String sql="batch="+batch+" and order_date like '"+date+"%'";
				String sql="batch="+batch+" and order_date >= '"+date+" 00:00:00' and order_date < '"+date+" 23:59:59'";
				List orderCustomerList=batchBarcodeService2.getOrderCustomerList(sql, -1, -1, null);
				for(int i=0;i<orderCustomerList.size();i++){
					OrderCustomerBean ocBean=(OrderCustomerBean)orderCustomerList.get(i);
					batchOrderCode+="'";
					batchOrderCode+=ocBean.getOrderCode();
					batchOrderCode+="',";
				}
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select *, (select group_concat(p.name) from product p join user_order_product uop on p.id=uop.product_id");
			sql.append(" where uop.order_id=uo.id ) product_name, (select group_concat(p.name) from product p join ");
			sql.append(" user_order_present uop on p.id=uop.product_id where uop.order_id=uo.id ) present_name from user_order ");
			sql.append(" uo join order_stock os on uo.code=os.order_code "); 
			sql.append(" left join order_customer oc on os.order_code=oc.order_code ");
			sql.append(" where os.status in (2,5) and os.order_code in(");
			while(batch==null&&(tmp=reader.readLine())!=null){
				sql.append("'").append(tmp).append("',");
			}
			if(batchOrderCode.length()>0){
				sql.append(batchOrderCode);
			}
			sql.replace(sql.length()-1, sql.length(), ")");
//			if(flag!=2){//导出和查询
//				sql.append(" order by os.last_oper_time desc ");
//			}else//打印
				sql.append(" order by oc.batch asc,oc.serial_number asc");
			List list = new ArrayList();
			Map productMap = new HashMap();
			ResultSet rs = dbOp_slave.executeQuery(sql.toString());
			voOrder vo = null;
	        while (rs.next()) {
	                vo = new voOrder();
	                vo.setId(rs.getInt("uo.id"));
	                vo.setName(rs.getString("uo.name"));
	                vo.setPhone(rs.getString("phone"));
	                vo.setAddress(rs.getString("address"));
	                vo.setPostcode(rs.getString("postcode"));
	                vo.setBuyMode(rs.getInt("buy_mode"));
	                vo.setOperator(rs.getString("operator"));
	                vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
	                vo.setUserId(rs.getInt("user_id"));
	                vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
	                vo.setStatus(rs.getInt("status"));
	                vo.setCode(rs.getString("code"));
	                vo.setPrice(rs.getFloat("price"));
	                vo.setDprice(rs.getFloat("dprice"));
	                vo.setDiscount(rs.getFloat("uo.discount"));
	                vo.setDeliverType(rs.getInt("deliver_type"));
	                vo.setRemitType(rs.getInt("remit_type"));
	                vo.setStockout(rs.getInt("stockout"));
	                vo.setPhone2(rs.getString("phone2"));
	                vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
	                vo.setFr(rs.getInt("fr"));
	                vo.setAgent(rs.getInt("agent"));
	                vo.setAgentMark(rs.getString("agent_mark"));
	                vo.setAgentRemark(rs.getString("agent_remark"));
	                vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
	                vo.setIsReimburse(rs.getInt("is_reimburse"));
	                vo.setRealPay(rs.getFloat("real_pay"));
	                vo.setPostage(rs.getInt("postage"));
	                vo.setIsOrder(rs.getInt("is_order"));
	                vo.setImages(rs.getString("images"));
	                vo.setAreano(rs.getInt("areano"));
	                vo.setPrePayType(rs.getInt("pre_pay_type"));
	                vo.setIsOlduser(rs.getInt("is_olduser"));
	                vo.setSuffix(rs.getFloat("suffix"));
	                vo.setContactTime(rs.getInt("contact_time"));
	                vo.setUnitedOrders(rs.getString("united_orders"));
	                vo.setRemark(rs.getString("remark"));
	                vo.setFlat(rs.getInt("flat"));
	                vo.setHasAddPoint(rs.getInt("has_add_point"));
	                vo.setGender(rs.getInt("gender"));
	                vo.setWebRemark(rs.getString("web_remark"));
	                vo.setEmail(rs.getString("email"));
	                vo.setOriginOrderId(rs.getInt("origin_order_id"));
	                vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
	                vo.setNewOrderId(rs.getInt("new_order_id"));
	                vo.setStockoutRemark(rs.getString("stockout_remark"));
	                vo.setDeliver(rs.getInt("deliver"));
	                vo.setProductType(rs.getInt("product_type"));
	                vo.setSerialNumber(rs.getInt("oc.serial_number"));
	                vo.setBatchNum(rs.getInt("oc.batch"));
	                String productName = StringUtil.convertNull(rs.getString("product_name"));
	                String presentName = StringUtil.convertNull(rs.getString("present_name"));
	                vo.setProducts(productName + presentName);
	                OrderStockBean os = new OrderStockBean();
	                os.setId(rs.getInt("os.id"));
	                os.setStatus(rs.getInt("os.status"));
	                os.setStockArea(rs.getInt("os.stock_area"));
	                StockAdminHistoryBean his = service.getStockAdminHistory("log_id=" + os.getId() + " and log_type=" + StockAdminHistoryBean.ORDER_STOCK_STATUS2 + " and type=" + StockAdminHistoryBean.CHANGE );
	                if(his != null){
	                	os.setLastOperTime(his.getOperDatetime());
	                } else {
	                	os.setLastOperTime("");
	                }
	                vo.setOrderStock(os);

	                if(service.getOrderStockCount("order_code='" + vo.getCode() + "' and status=" + OrderStockBean.STATUS4) > 0){
	                	vo.setStockDeleted(true);
	                }
	                list.add(vo);
	            }
	        if(list.size()==0){
	        	 if(flag==2){  
	        		 response.setContentType("text/html; charset=utf-8");
	        		 StringBuffer sb = new StringBuffer();
	        		 sb.append("<script type='text/javascript'>");
	        		 sb.append("alert('请输入有效的订单编号！');");
	        		 sb.append("window.close();");
	        		 sb.append("</script>");
	        		 PrintWriter pw = response.getWriter();
	        		 pw.write(sb.toString());
	        		 pw.flush();
	        		 response.flushBuffer();;
	        		 pw.close();
	        		 return null;
	        	 }
	        	 request.setAttribute("tip", "请输入有效的订单编号！");
	        	 return mapping.findForward("failure");
	        	 
	        }
	        Iterator iter = list.listIterator();
            List orderProductList = null;
            List orderPresentList = null;
	        List result = new ArrayList();
            int totalWeightBJ = 0;
            int totalCountBJ = 0;
            int totalWeightGD = 0;
            int totalCountGD = 0;
            String orders="";
            while (iter.hasNext()) {
				vo = (voOrder) iter.next();
				OrderStockBean os = vo.getOrderStock();
				orderProductList = wareService.getOrderProductsSplit(vo.getId());
				orderPresentList = wareService.getOrderPresentsSplit(vo.getId());
				orderProductList.addAll(orderPresentList);
				List detailList = new ArrayList();
				
				Iterator detailIter = orderProductList.listIterator();
				while(detailIter.hasNext()){
					voOrderProduct vop = (voOrderProduct)detailIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if(product.getIsPackage() == 1){ // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while(ppIter.hasNext()){
							ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							
							//货位信息
							OrderStockProductBean osp = service.getOrderStockProduct("order_stock_id = "+os.getId()+" and product_id = "+tempVOP.getProductId());
							List ospcList = service.getOrderStockProductCargoList("order_stock_product_id = "+osp.getId(), -1, -1, "id asc");
							osp.setOspcList(ospcList);
							tempVOP.setOrderStockProduct(osp);
							
							detailList.add(tempVOP);
							vo.setBaozhuangzhongliang(vo.getBaozhuangzhongliang() + tempProduct.getBzzhongliang());
						}
					} else {
						vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						
						//货位信息
						OrderStockProductBean osp = service.getOrderStockProduct("order_stock_id = "+os.getId()+" and product_id = "+vop.getProductId());
						List ospcList = service.getOrderStockProductCargoList("order_stock_product_id = "+osp.getId(), -1, -1, "id asc");
						osp.setOspcList(ospcList);
						vop.setOrderStockProduct(osp);
						
						vo.setBaozhuangzhongliang(vo.getBaozhuangzhongliang() + product.getBzzhongliang());
						detailList.add(vop);
					}
				}
				if(vo.getDeliver() == 7){
					totalWeightBJ += vo.getBaozhuangzhongliang();
					totalCountBJ++;
				} else if(vo.getDeliver() == 8){
					totalWeightGD += vo.getBaozhuangzhongliang();
					totalCountGD++;
				}
				result.add(vo);
				orders+=vo.getCode();
				orders+=",";
				productMap.put(Integer.valueOf(vo.getId()), detailList);
            }
            if(orders.length()>0){
            	orders=orders.substring(0,orders.length()-1);
            }
            //set 发货重量平均值
            request.setAttribute("perWeightBJ", (totalCountBJ > 0)?String.valueOf(totalWeightBJ/totalCountBJ):"");
            request.setAttribute("perWeightGD", (totalCountGD > 0)?String.valueOf(totalWeightGD/totalCountGD):"");
            request.setAttribute("orderList", result);
            request.setAttribute("productMap", productMap);
            if(flag==2){//打印发货清单，把result逆序
            	List newResult=new ArrayList();
            	for(int i=0;i<result.size();i++){
            		newResult.add(result.get(result.size()-i-1));
            	}
            	request.setAttribute("orderList", newResult);
            }
            if(batch!=null){ //按批次打印，重新按货位号排序
            	Object[][] orderArray=new Object[result.size()][2];//orderId,order,cargoCode
				for(int i=0;i<result.size();i++){
					voOrder order=(voOrder)result.get(i);
					List productList = (List)productMap.get(Integer.valueOf(order.getId()));
					Iterator iter2 = productList.listIterator();
					if(iter2.hasNext()){
						voOrderProduct op = (voOrderProduct) iter2.next();
						List ospcList = op.getOrderStockProduct().getOspcList();
						OrderStockProductCargoBean ospc=new OrderStockProductCargoBean();
						ospc = (OrderStockProductCargoBean)ospcList.get(0);
						orderArray[i][0]=order;
						orderArray[i][1]=ospc.getCargoWholeCode();
					}else{
						request.setAttribute("tip", "订单"+order.getCode()+"产品数据异常，操作失败！");
						return mapping.findForward("failure");
					}
				}
				for(int i=0;i<orderArray.length-1;i++){//将orderArray中的货位号按升序排列
					for(int j=0;j<orderArray.length-i-1;j++){
						char[] s1=orderArray[j][1].toString().toCharArray();
						char[] s2=orderArray[j+1][1].toString().toCharArray();
						for(int k=0;k<s1.length;k++){
							if(s1[k]==s2[k]){
								continue;
							}else if(s1[k]>s2[k]){
								voOrder tempOrder=(voOrder)orderArray[j+1][0];
								String tempCode=orderArray[j+1][1].toString();
								orderArray[j+1][0]=orderArray[j][0];
								orderArray[j+1][1]=orderArray[j][1];
								orderArray[j][0]=tempOrder;
								orderArray[j][1]=tempCode;
								break;
							}else if(s1[k]<s2[k]){
								break;
							}
						}
					}
				}
				List newResult=new ArrayList();
				batchBarcodeService.getDbOp().startTransaction();
				for(int i=orderArray.length-1;i>=0;i--){
					voOrder order=(voOrder)orderArray[i][0];
					order.setSerialNumber(i+1);
					newResult.add(order);
					//更新客户信息排序序号
					batchBarcodeService.updateOrderCustomer("serial_number = "+(i+1),"order_code = '"+order.getCode()+"'");
				}
				batchBarcodeService.getDbOp().commitTransaction();
				
            }
            if(batch!=null&&flag==2){//补打印，添加打印记录
            	voUser user = (voUser)request.getSession().getAttribute("userView");
            	OrderStockPrintLogBean logBean=new OrderStockPrintLogBean();
                logBean.setBatch(Integer.parseInt(batch));
                logBean.setType(2);
                logBean.setUserId(user.getId());
                logBean.setUserName(user.getUsername());
                logBean.setTime(DateUtil.getNow());
                logBean.setRemark(orders);//该次打印时的订单编号
                service.addOrderStockPrintLog(logBean);
            }
            if(flag==1){	    //按地址导出excel文件
            	String areano = StringUtil.convertNull(request.getParameter("areano"));
            	String buymode = StringUtil.convertNull(request.getParameter("buymode"));
            	String stockState = StringUtil.convertNull(request.getParameter("stockState"));
            	String action = StringUtil.convertNull(request.getParameter("action"));
            	int printType = StringUtil.StringToId(request.getParameter("printType"));
            	orderStockExportPrint(areano, buymode, stockState, action, printType, result, response);
            	return null;
            }else if(flag==2){  // 打印发货清单
            	return mapping.findForward("lineprint");
            }else{              // 查询
            	return mapping.findForward(IConstants.SUCCESS_KEY);
            }
         }catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			service.releaseAll();
			dbOp_slave.release();
			dbOp.release();
			
		}
	
	}
	
	public void orderStockExportPrint (String areano, String buymode, String stockState, String action, int printType, List orderList , HttpServletResponse response) throws Exception {
		
		voOrder vo = null;
		int bianjie = 0;
		int size = 0;
		
		String now = DateUtil.getNow().substring(0, 10);
		String area = null;
		if(areano.equals("0")){
			area = "北京";
		} else {
			area = "广东";
		}
		String strBuymode = null;
		if(buymode.equals("0")){
			strBuymode = "货到付款";
		} else if(buymode.equals("1")){
			strBuymode = "邮购";
		} else if(buymode.equals("2")){
			strBuymode = "上门自取";
		} else {
			strBuymode = "";
		}
		String fileName = now;
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		
		if((areano.equals("0") || areano.equals("1") || areano.equals("2") || areano.equals("3")) && printType == 0){
			header.add("序号");
			header.add("订单号码");
			header.add("产品分类");
			header.add("应收货款");
			header.add("客户姓名");
			header.add("客户地址");
			header.add("联系电话");
			header.add("邮政编码");
			header.add("支付方式");
			header.add("交寄日期");
			header.add("快递公司");
			header.add("收寄局");
			header.add("寄达城市");
			header.add("邮件号码");
			header.add("邮件重量");
			header.add("备注信息");
			header.add("成品重量");
			
			size = header.size();
			
			if (orderList != null && orderList.size() > 0) {
				int x = orderList.size();
				for (int i = 0; i < x; i++) {
					vo =  (voOrder) orderList.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add((vo.getSerialNumber()==0)?"":vo.getBatchNum()+"-"+vo.getSerialNumber());
					tmp.add(vo.getCode());
					tmp.add(vo.getProductTypeName());
					tmp.add(NumberUtil.priceOrder(vo.getDprice()));
					tmp.add(vo.getName());
					tmp.add(vo.getAddress());
					tmp.add(vo.getPhone());
					tmp.add(vo.getPostcode());
					tmp.add("");
					tmp.add("");
					tmp.add(vo.getDeliverName());
					tmp.add("");
					tmp.add("");
					tmp.add("");
					tmp.add("");
					tmp.add(vo.getRemark());
					tmp.add("" + vo.getBZZL());
					bodies.add(tmp);
				}
			}
		} 
		//由于不知道以下是什么情况，不做处理
		else if((areano.equals("0") || areano.equals("1") || areano.equals("2") || areano.equals("3") || areano.equals("-1")) && printType == 1){
			
		} else if((areano.equals("1") || areano.equals("2") || areano.equals("3")) && printType == 0){
			
		} else if(areano.equals("1") || areano.equals("2") || areano.equals("3")){
			
		} else {
			
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
	}
}
