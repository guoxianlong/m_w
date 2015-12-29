package mmb.rec.stat.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockCardComparator;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("admin/productStock")
public class ProductStockController {
	
	/*
	 * 郝亚斌
	 * 2013-10-16
	 * 
	 * 将进销存卡片的导出 由原来的页面导出 改为框架式的poi导出
	 */
	@RequestMapping("stockCardExport")
	public void stockCardExport(HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if( user == null ) {
			return;
		}
		UserGroupBean group = user.getGroup();
		String requestTime = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
		int stockType = StringUtil.toInt(request.getParameter("stockType"));
        int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
        int stockCardType = StringUtil.toInt(request.getParameter("stockCardType"));
        String code = StringUtil.dealParam(request.getParameter("code"));
        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
        String productName = StringUtil.dealParam(request.getParameter("productName"));
        String productOriName = StringUtil.dealParam(request.getParameter("productOriName"));
        String startDate = StringUtil.dealParam(request.getParameter("startDate"));
        String endDate = StringUtil.dealParam(request.getParameter("endDate"));

        productName = Encoder.decrypt(productName);//解码为中文
		if(productName==null){//解码失败,表示已经为中文,则返回默认
			productName =StringUtil.dealParam(request.getParameter("productName"));//名称
		}
		if (productName==null) productName="";

		productOriName = Encoder.decrypt(productOriName);//解码为中文
		if(productOriName==null){//解码失败,表示已经为中文,则返回默认
			productOriName =StringUtil.dealParam(request.getParameter("productOriName"));//名称
		}
		if (productOriName==null) productOriName="";

		DbOperation dbOp_slave2 = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave2);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave2);
        try {
        	if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
        		Date start = DateUtil.parseDate(startDate);
        		Date end = DateUtil.parseDate(endDate);
        		if(end.getTime() < start.getTime()){
            		request.setAttribute("tip", "截止时间不能早于起始时间！");
                    request.setAttribute("result", "failure");
                    return;
        		}
        	}
            String condition = null;
            StringBuilder buf = new StringBuilder();
            StringBuilder paramBuf = new StringBuilder();
            boolean canQuery = false;
            if(stockType >= 0){
            	buf.append("stock_type=").append(stockType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockType=").append(stockType);
            }
            if(stockArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_area=").append(stockArea);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockArea=").append(stockArea);
            }
            if(stockCardType >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("card_type=").append(stockCardType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockCardType=").append(stockCardType);
            }
            if(!StringUtil.isNull(code)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("code='").append(code).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("code=").append(code);
            }
            if(!StringUtil.isNull(startDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime >='").append(startDate).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("startDate=").append(startDate);
            }
            if(!StringUtil.isNull(endDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime  <='").append(endDate).append(" 23:59:59'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("endDate=").append(endDate);
            }
            voProduct p = null;
            StringBuilder pBuf = new StringBuilder();
            if(!StringUtil.isNull(productCode)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.code='").append(productCode).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productCode=").append(productCode);
            }
            if(!StringUtil.isNull(productName)){
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productName=").append(Encoder.encrypt(productName));
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.name='").append(productName).append("'");
            }
        	if(!StringUtil.isNull(productOriName)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.oriname='").append(productOriName).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productOriName=").append(Encoder.encrypt(productOriName));
        	}
        	if(pBuf.length() > 0){
        		p = wareService.getProduct2(pBuf.toString());
            	if(p != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("product_id=").append(p.getId());
            		canQuery = true;
            		request.setAttribute("product", p);
            	}
        	}
            if(buf.length() > 0){
            	condition = buf.toString();
            }
            List list = null;
            if(canQuery && condition != null){
	            list = service.getStockCardList(condition, -1, -1, "create_datetime asc");
	            Collections.sort(list, new StockCardComparator());

	            request.setAttribute("list", list);
            }
            
            ExportExcel excel = new ExportExcel();
		    
		    //设置表头
		    List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		    if( p != null ) {
				ArrayList<String> header = new ArrayList<String>();
				header.add("商品信息：");
				header.add("商品编号：" + p.getCode());
				header.add("商品编号：" + p.getCode());
				header.add("商品编号：" + p.getCode());
				if (group.isFlag(182)) {
					header.add("商品编号：" + p.getCode());
				}
				header.add("小店名称：" + StringUtil.toWml(p.getName()));
				header.add("小店名称：" + StringUtil.toWml(p.getName()));
				header.add("原名称：" + StringUtil.toWml(p.getOriname()));
				header.add("原名称：" + StringUtil.toWml(p.getOriname()));
				if (group.isFlag(182)) {
					header.add("原名称：" + StringUtil.toWml(p.getOriname()));
					header.add("原名称：" + StringUtil.toWml(p.getOriname()));
				}
				header.add("状态：" + p.getStatusName());
				header.add("状态：" + p.getStatusName());
				header.add("状态：" + p.getStatusName());
				if (group.isFlag(182)) {
					header.add("状态：" + p.getStatusName());
				}
		    	headers.add(header);
		    	ArrayList<String> header1 = new ArrayList<String>();
		    	header1.add("库类型");
		    	header1.add("库区域");
		    	header1.add("单据号");
		    	header1.add("来源");
		    	header1.add("时间");
		    	header1.add("入库数量");
		    	if(group.isFlag(182)){ 
		    		header1.add("入库金额");
		    	} 
		    	header1.add("出库数量");
		    	if(group.isFlag(182)){ 
		    		header1.add("出库金额");
		    	} 
		    	header1.add("当前结存");
		    	header1.add("本库区域总结存");
		    	header1.add("本库类总结存");
		    	header1.add("全库总结存");
		    	if(group.isFlag(182)){
		    		header1.add("库存单价");
		    		header1.add("结存总额");
		    	}
		    	headers.add(header1);
		    } else {
		    	ArrayList<String> header = new ArrayList<String>();
				header.add("商品信息");
				header.add("没有找到商品信息");
				header.add("没有找到商品信息");
				header.add("没有找到商品信息");
				if (group.isFlag(182)) {
					header.add("没有找到产品信息");
				}
				header.add("无商品信息");
				header.add("无商品信息");
				if (group.isFlag(182)) {
					header.add("无产品信息");
				}
				header.add("无商品信息");
				header.add("无商品信息");
				if (group.isFlag(182)) {
					header.add("无商品信息");
				}
				header.add("无商品信息");
				header.add("无商品信息");
				header.add("无商品信息");
				if (group.isFlag(182)) {
					header.add("无商品信息");
				}
		    	headers.add(header);
		    }

			
			/*允许合并列,下标从0开始，即0代表第一列*/
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			/*允许合并行,下标从0开始，即0代表第一行*/
			List<Integer> mayMergeRow = new ArrayList<Integer>();
            mayMergeRow.add(0);
            excel.setMayMergeRow(mayMergeRow);

			//构建bodies , excell表可以拼接多个bodies
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			int m = 3;
			if( list == null ) {
				
			} else {
				for (int i = 0; i < list.size(); i++) {
					//添加对应列得到 信息在一个list中
					StockCardBean bean = (StockCardBean) list.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getStockTypeName(bean.getStockType()));
					tmp.add(bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getAreaName(bean.getStockArea()));
					tmp.add(bean.getCode());
					tmp.add(bean.getCardTypeName());
					tmp.add(StringUtil.cutString(bean.getCreateDatetime(), 19));
					tmp.add((bean.getStockInCount() > 0)?String.valueOf(bean.getStockInCount()):"-");
					if(group.isFlag(182)){ 
						tmp.add((bean.getStockInPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockInPriceSum()):"-");
					} 
					tmp.add((bean.getStockOutCount() > 0)?String.valueOf(bean.getStockOutCount()):"-");
					if(group.isFlag(182)){ 
						tmp.add((bean.getStockOutPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockOutPriceSum()):"-");
					} 
					tmp.add(bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":new Integer(bean.getCurrentStock()).toString());
					tmp.add(bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":new Integer(bean.getStockAllArea()).toString());
					tmp.add(bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":new Float(bean.getStockAllType()).toString());
					tmp.add(new Integer(bean.getAllStock()).toString());
					if(group.isFlag(182)){
						tmp.add(new Float(bean.getStockPrice()).toString());
						tmp.add(StringUtil.formatDouble2(bean.getAllStockPriceSum()));
					}
					bodies.add(tmp);
				}
			}
			
			
			/*
			 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
			 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
			 * 
			 * */
			excel.setColMergeCount(headers.get(0).size());
			
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
            excel.setWidthStandard();
            
            //用于标记是否是表头数据,一般设置样式时是否表头会用到
            excel.setHeader(false);
            
            //调用填充数据区方法
            excel.buildListBody(bodies);
			
            response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ toUtf8String("进销存卡片" + requestTime,
									request) + ".xlsx");
			response.setContentType("application/msxls");
			response.setCharacterEncoding("utf-8");
			excel.getWorkbook().write(response.getOutputStream());
        } catch( IOException e) {
        	e.printStackTrace();
        } finally {
        	dbOp_slave2.release();
        }
		
	}
	
	public static String toUtf8String(String s, HttpServletRequest request) {
		String browserType = request.getHeader("user-agent");
		if (browserType.indexOf("Firefox") > -1) {
			String result = "";
			try {
				result = new String(s.getBytes("utf-8"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c >= 0 && c <= 255) {
					sb.append(c);
				} else {
					byte[] b;
					try {
						b = Character.toString(c).getBytes("utf-8");
					} catch (Exception ex) {
						b = new byte[0];
					}
					for (int j = 0; j < b.length; j++) {
						int k = b[j];
						if (k < 0)
							k += 256;
						sb.append("%" + Integer.toHexString(k).toUpperCase());
					}
				}
			}
			return sb.toString();
		}
	}

}
