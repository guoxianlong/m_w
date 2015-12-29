package adultadmin.action.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.util.comparator.ComparatorDpproductStat;
import adultadmin.bean.stat.DpproductStatBean;
import adultadmin.framework.BaseAction;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class SearecDpproductAction extends BaseAction {
	public void SearecDpproductStat2(HttpServletRequest request,
			HttpServletResponse response) {	      
		String startDate = StringUtil.dealParam(request.getParameter("startDate"));//开始时间
		if(startDate==null)startDate="";
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));//结束时间
		if(endDate==null)endDate="";
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2	      
		//System.out.println(startDate+"==="+endDate+"=="+proxy+"=="+parentId1+"=="+parentId2);
		if(startDate.equals("")&&endDate.equals("")&&proxy==-1&&parentId1==0&&parentId2==0){
			//System.out.println("默认为空返回");
			return;
		}
		//时间格式校验
		Date date1 = null;
		Date date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (startDate.length() > 0) {
			try {
				date1 = sdf.parse(startDate);
			} catch (Exception e) {
				request.setAttribute("result", "failure");
				request.setAttribute("tip", "开始时间格式不对！");
				return;
			}
		}
		if (endDate.length() > 0) {
			try {
				date2 = sdf.parse(endDate);
			} catch (Exception e) {
				request.setAttribute("result", "failure");
				request.setAttribute("tip", "结束时间格式不对！");
				return;
			}
		}
		if (date1 != null && date2 != null) {
			if (date2.before(date1)) {
				request.setAttribute("result", "failure");
				request.setAttribute("tip", "结束时间必须在开始时间之后！");
				return;
			}
		}	      

		//拼装查询条件
		StringBuffer condition=new StringBuffer();
		String outTime="";//产品出货时间
		String creatTime="";//产品入库时间
		String returnTime="";//产品的销售退货时间
		if(startDate!=null){
			outTime=(" and left(os.last_oper_time,10) >= '"+startDate+"' ");
			creatTime=(" and left(bs.create_datetime, 10) >= '"+startDate+"' ");
			returnTime=(" and left(so.create_datetime, 10) >= '"+startDate+"' ");
			request.setAttribute("startDate", startDate);
		}
		if(endDate!=null){
			outTime=outTime+(" and left(os.last_oper_time,10)<= '"+endDate+"' ");
			creatTime=creatTime+(" and left(bs.create_datetime,10)<= '"+endDate+"' ");
			returnTime=returnTime+(" and left(so.create_datetime,10)<= '"+endDate+"' ");
			request.setAttribute("endDate", endDate);
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(parentId1>0){
			condition.append(" and p.parent_id1="+parentId1);
			request.setAttribute("parentId1", ""+parentId1);
		}
		if(parentId2>0){
			condition.append(" and p.parent_id2="+parentId2);
			request.setAttribute("parentId2", ""+parentId2);
		}
		//开启数据库连接
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		DpproductStatBean bean = null;
		List dpproductList = new ArrayList();
		String query="select s8.product_id, s8.code,s8.name,s8.out_count,s8.in_count,s8.or_count,s9.last_time,s8.price5 from("
				+"select s3.product_id,s3.code,s3.name,s3.price5,s3.out_count,s3.in_count,s5.or_count from"
				+" (select s2.product_id,s2.code,s2.name,s2.price5,s2.out_count,s2.in_count from"
				+" (select s1.product_id,s1.code,s1.name,s1.price5,s1.out_count,s4.in_count from"
				//销量统计	  
				+" (select s.product_id,p.code,p.name,p.price5,sum(s.stockout_count) out_count from"
				+" (select osp.product_id ,osp.stockout_count ,os.last_oper_time last_time"
				+" from order_stock_product osp, order_stock os"
				+" where osp.order_stock_id = os.id"
				+" and os.status=2"
				+outTime+") s left join product p on s.product_id = p.id left outer join product_supplier c on p.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id  "
				+" where 1=1 "
				+condition.toString()
				+" group by s.product_id) s1"
				//采购入库统计
				+" left join(select s6.product_id, sum(s6.stockin_count) in_count"
				+" from (select bsp.product_id, bsp.stockin_count"
				+" from buy_stockin_product bsp,buy_stockin  bs"
				+" where bsp.buy_stockin_id = bs.id"
				+" and bs.status in (4,6)"
				+creatTime+") s6"
				+" group by s6.product_id) s4"
				+" on s1.product_id=s4.product_id) s2 ) s3"
				//销售退货统计
				+" left join (select s12.product_id,sum(s12.stock_bj) or_count "
				+" from (select sh.product_id,sh.stock_bj"
				+" from stock_history sh,stock_operation so"
				+" where sh.oper_id=so.id"
				+" and so.type=6 " 
				+returnTime+")s12"
				+" group by s12.product_id) s5"
				+" on s3.product_id=s5.product_id) s8"
				//产品最近出货时间 
				+" left join (select osp.product_id,max(os.last_oper_time) last_time"
				+" from order_stock_product osp, order_stock os"
				+" where osp.order_stock_id = os.id"
				+" and os.status = 2 and left(os.last_oper_time, 10) <= '"+endDate+"' "
				+" group by osp.product_id) s9"
				+" on s8.product_id=s9.product_id"
				+" order by s9.last_time desc";
		//System.out.println("-->"+query);
		ResultSet rs = dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				bean=new DpproductStatBean();
				bean.setProductId(rs.getInt(1));//商品ID
				bean.setCode(rs.getString(2));//编号
				bean.setName(rs.getString(3));//名称
				bean.setOutCount(rs.getInt(4));//销量
				bean.setStockinCount(rs.getInt(5));//采购入库量
				// bean.setReturnCount(rs.getInt(6));//采购退货量
				bean.setOutReturnCount(rs.getInt(6));//销量退货量
				bean.setLastTime(rs.getString(7));//产品最近出货时间
				bean.setPrice5(rs.getFloat(8));
				dpproductList.add(bean);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("count", ""+dpproductList.size());
		//关联各个库存数量
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		try{
			Iterator iter = dpproductList.listIterator();
			while(iter.hasNext()){
				DpproductStatBean dpproductStat = (DpproductStatBean)iter.next();			
				dpproductStat.setPsList(psService.getProductStockList("product_id=" + dpproductStat.getProductId(), -1, -1, null));				
			}
		} finally {
			dbOp.release();//释放所有资源
			psService.releaseAll();
		}
		request.setAttribute("dpproductList", dpproductList);
	}

	public void SearecDpproductStat(HttpServletRequest request,
			HttpServletResponse response) {
		String startDate = StringUtil.dealParam(request.getParameter("startDate"));//开始时间
		if(startDate==null)startDate="";
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));//结束时间
		if(endDate==null)endDate="";
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2	      
		//System.out.println(startDate+"==="+endDate+"=="+proxy+"=="+parentId1+"=="+parentId2);
		if(startDate.equals("")&&endDate.equals("")&&proxy==-1&&parentId1==0&&parentId2==0){
			//System.out.println("默认为空返回");
			return;
		}
		//时间格式校验
		Date date1 = null;
		Date date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (startDate.length() > 0) {
			try {
				date1 = sdf.parse(startDate);
			} catch (Exception e) {
				request.setAttribute("result", "failure");
				request.setAttribute("tip", "开始时间格式不对！");
				return;
			}
		}
		if (endDate.length() > 0) {
			try {
				date2 = sdf.parse(endDate);
			} catch (Exception e) {
				request.setAttribute("result", "failure");
				request.setAttribute("tip", "结束时间格式不对！");
				return;
			}
		}
		if (date1 != null && date2 != null) {
			if (date2.before(date1)) {
				request.setAttribute("result", "failure");
				request.setAttribute("tip", "结束时间必须在开始时间之后！");
				return;
			}
		}

		//拼装查询条件
		StringBuffer condition=new StringBuffer();
		if(startDate!=null){
			condition.append(" and sc.create_datetime>='");
			condition.append(startDate);
			condition.append("'");
			request.setAttribute("startDate", startDate);
		}
		if(endDate!=null){
			condition.append(" and sc.create_datetime<='");
			condition.append(endDate);
			condition.append("'");
			request.setAttribute("endDate", endDate);
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(parentId1>0){
			condition.append(" and p.parent_id1="+parentId1);
			request.setAttribute("parentId1", ""+parentId1);
		}
		if(parentId2>0){
			condition.append(" and p.parent_id2="+parentId2);
			request.setAttribute("parentId2", ""+parentId2);
		}
		//开启数据库连接
		DbOperation dbOp = new DbOperation();
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		dbOp.init("adult_slave2");
		List dpproductList = new ArrayList();
		String query="select sc.product_id,p.code,p.oriname,sc.stock_in_count,sc.stock_out_count,sc.create_datetime,sc.stock_price,sc.card_type from stock_card sc join product p on sc.product_id=p.id"
				+(proxy>0?" join product_supplier c on p.id=c.product_id join supplier_standard_info d on d.id=c.supplier_id":"")
				+" where sc.card_type in (1,2,5)"
				+condition
				+" order by create_datetime desc";
		ResultSet rs = dbOp.executeQuery(query);
		try{
			HashMap dppMap=new HashMap();
			while(rs.next()){
				int productId=rs.getInt(1);
				String productCode=rs.getString(2);
				String productName=rs.getString(3);
				int stockInCount=rs.getInt(4);
				int stockOutCount=rs.getInt(5);
				String createDatetime=rs.getString(6);
				float stockPrice=rs.getFloat(7);//库存单价
				int cardType=rs.getInt(8);//进销存卡片类型

				if(dppMap.get(productId+"")==null){
					DpproductStatBean bean = new DpproductStatBean();

					bean.setProductId(productId);//商品ID
					bean.setCode(productCode);//编号
					bean.setName(productName);//名称
					if(cardType==1){//采购入库
						bean.setOutCount(0);//销量
						bean.setStockinCount(stockInCount);//采购入库量
						bean.setOutReturnCount(0);//销量退货量
					}else if(cardType==2){//销售出库
						bean.setOutCount(stockOutCount);//销量
						bean.setStockinCount(0);//采购入库量
						bean.setOutReturnCount(0);//销量退货量
					}else if(cardType==5){//销售退货入库
						bean.setOutCount(0);//销量
						bean.setStockinCount(0);//采购入库量
						bean.setOutReturnCount(stockInCount);//销量退货量
					}
					bean.setLastTime(createDatetime);//产品最近出货时间
					bean.setPrice5(stockPrice);
					bean.setFrequencyCount(1);
					dpproductList.add(bean);
					dppMap.put(productId+"", bean);
				}else{
					DpproductStatBean bean = (DpproductStatBean)dppMap.get(productId+"");
					if(cardType==1){//采购入库
						bean.setStockinCount(bean.getStockinCount()+stockInCount);//采购入库量
					}else if(cardType==2){//销售出库
						bean.setOutCount(bean.getOutCount()+stockOutCount);//销量
					}else if(cardType==5){//销售退货入库
						bean.setOutReturnCount(bean.getOutReturnCount()+stockInCount);//销量退货量
					}
					bean.setFrequencyCount(bean.getFrequencyCount()+1);
				}
			}
			request.setAttribute("count", ""+dpproductList.size());
			//关联各个库存数量
			Iterator iter = dpproductList.listIterator();
			while(iter.hasNext()){
				DpproductStatBean dpproductStat = (DpproductStatBean)iter.next();			
				dpproductStat.setPsList(psService.getProductStockList("product_id=" + dpproductStat.getProductId(), -1, -1, null));				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			dbOp.release();//释放所有资源
			psService.releaseAll();
		}
		
		ComparatorDpproductStat comparator = new ComparatorDpproductStat();
		Collections.sort(dpproductList,comparator);
		//按照动碰频率排序
		
		request.setAttribute("dpproductList", dpproductList);
	}
}
