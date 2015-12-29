package adultadmin.action.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.OrderDeliversBean;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;
public class SearchOrdersDeliversAction {

	
	public void orderDeliverSearch(HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		String stockArea = StringUtil.convertNull(request.getParameter("stockArea"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String[] deliverValue = request.getParameterValues("deliverValue");
		String deliverSet = request.getParameter("deliverSet");
		if(stockArea=="" || stockArea.equals("")){
			request.setAttribute("message", "区域不能为空");
			return;
		}
		
		if(startTime=="" || startTime.equals("")){
			request.setAttribute("message", "开始时间不能为空");
			return;
		}
		if(endTime=="" || endTime.equals("")){
			request.setAttribute("message", "结束时间不能为空");
			return;
		}
		//2012-5-16需求：去除31天限制
//		int temps = DateUtil.getDaySub(startTime,endTime);
//		if(temps>30){
//			request.setAttribute("message", "时间段不得大于31天");
//			return;
//		}
		String temp="(";
		if(deliverValue==null){//没有选择中的时候 得到全部快递公司列表
			voOrder deliver = new voOrder();
			Iterator   it =deliver.deliverMapAll.entrySet().iterator();
			deliverValue = new String[deliver.deliverMapAll.size()];
			int i=0;
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry)it.next();
				deliverValue[i]=String.valueOf(entry.getKey());
				i++;
			}
			//request.setAttribute("deliverSetTemp", "123");
			//deliverSet="1";
		}
		for(int i=0;i<deliverValue.length;i++){
			temp+=deliverValue[i] ;
			if(i!=deliverValue.length-1){
				temp+=",";
			}
		}
		temp+=")";
		if(stockArea!=null && !stockArea.trim().equals("") && startTime!=null && endTime!=null){
			StringBuffer  query= new StringBuffer("select left(os.last_oper_time, 10) de, count(*) , uo.deliver, ifnull(group_concat(os.order_code),''),sum(ap.weight)" +
					" from order_stock os join user_order uo on os.order_id = uo.id join audit_package ap on uo.code=ap.order_code where os.status in (2,4,6,7,8)");
			query.append(" and os.stock_area="+stockArea);
			if(!temp.equals("()")){
				query.append(" and uo.deliver in "+ temp);
			}else{
				return;
			}
			query.append(" and os.last_oper_time>='"+startTime+"'  and os.last_oper_time<='"+endTime+" 23:59:59'");
			query.append("group by de DESC,uo.deliver;") ;
			List list = new ArrayList();
			DbOperation dbOp = new DbOperation();
			ResultSet rs=null;
            OrderDeliversBean bean = null;
            ResultSet rs1 =null;
            Connection conn1 = DbUtil.getConnection(DbOperation.DB_SLAVE);
        	Statement st1 = conn1.createStatement();
	        try {
	        	dbOp.init("adult_slave");
	        	dbOp.executeUpdate("set group_concat_max_len=1048576;");
	        	rs = dbOp.executeQuery(query.toString());
	            while (rs.next()) {
	            	bean = new OrderDeliversBean();
	            	bean.setDateTime(rs.getString(1));
	            	bean.setSumNum(rs.getInt(2));
	            	bean.setDeliver(rs.getInt(3));
	            	bean.setStr_code(rs.getString(4));
	            	bean.setSumWeight(rs.getFloat(5));
	            	if(bean.getStr_code()!=null && !bean.getStr_code().equals("")){
	            		StringBuffer query1 = new StringBuffer("select sum(price * discount) from user_order where code in ('");
	            		query1.append(bean.getStr_code().replace(",", "','")+"') ");
	            		rs1 = st1.executeQuery(query1.toString());
	            		rs1.next();
	            		bean.setSumMoney(rs1.getDouble(1));
	            		rs1.close();
	            		
	            		StringBuffer query2 = new StringBuffer("select count(id) ,sum(price * discount) from user_order where code in ('");
	            		query2.append(bean.getStr_code().replace(",", "','")+"') and status = 11");
	            		rs1 = st1.executeQuery(query2.toString());
	            		rs1.next();
	            		bean.setBackNum(rs1.getInt(1));
	            		bean.setBackMoney(rs1.getDouble(2));
	            		rs1.close();
	            		
	            	}else{
	            		bean.setSumMoney(0);
	            		bean.setBackNum(0);
	            		bean.setBackMoney(0);
	            	}
	                list.add(bean);
	            }
	            if(deliverSet.equals("-1") || deliverSet=="-1"){//按时间统计
	            	list = changeList(list,deliverValue); 
	            }else if(deliverSet.equals("1") || deliverSet=="1"){// 按快递公司合计
	            	list = countDeliver(list,deliverValue);
	            }
	            
	            request.setAttribute("orderDeliversList", list);
	        }catch(Exception e){
	        	e.printStackTrace();
	        	throw new Exception(e);
	        } finally {
	            dbOp.release();
	            if(conn1!=null) conn1.close();
	            if(st1!=null) st1.close();
	            if(rs1!=null) rs1.close();
	        }
			
		}
	}
	
 
	private List changeList(List list,String [] deliver){ //改变 list的排序方式 变成  集合 -- 对象里面包含子集合  如果时间相同。则 这把就把放到同时间第一个bean下 
		List temp_list = new ArrayList();
		int temp_int=0;
		boolean flag=true;
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				flag=true;//判断是否已经添加 子集
				OrderDeliversBean bean =(OrderDeliversBean)list.get(i);
				if(i!= list.size()-1){
					OrderDeliversBean bean1=(OrderDeliversBean)list.get(i+1);
					if(bean.getDateTime().equals(bean1.getDateTime())|| bean.getDateTime()== bean1.getDateTime()){
						temp_list.add(bean);
						for(int j=i+1;j<=deliver.length+i && j<list.size();j++){
							OrderDeliversBean bean2 =(OrderDeliversBean)list.get(j);
							if(bean.getDateTime().equals(bean2.getDateTime())|| bean.getDateTime()== bean2.getDateTime()){
								bean.getSubBeanList().add(bean2);
								temp_int = j;
							} else{
								break;
							}
						}
					}else{
						temp_list.add(bean);
					}
				}else{//最后一个是单独的。
					temp_list.add(bean);
				}
				if(temp_int!=0 && temp_int>i)
					i=temp_int ;
				
				 
			}
		}else{
			temp_list=list;
		}
		return temp_list;
	}
	
	private List countDeliver(List list,String [] deliver){
		List temp_list = new ArrayList();
		OrderDeliversBean[] bean = new OrderDeliversBean[deliver.length];
		if(list!=null && list.size()>0){
			for(int j=0;j<deliver.length;j++){
				bean[j] = new OrderDeliversBean();
				bean[j].setDeliver(Integer.parseInt(deliver[j]));
			}
		
			for(int i=0;i<list.size();i++){
				OrderDeliversBean temp =(OrderDeliversBean)list.get(i);
				for(int j=0;j<bean.length;j++){
					if(temp.getDeliver() == bean[j].getDeliver()){
						bean[j].setSumMoney(temp.getSumMoney()+bean[j].getSumMoney());
						bean[j].setSumNum(temp.getSumNum()+bean[j].getSumNum());
						bean[j].setBackMoney(temp.getBackMoney()+bean[j].getBackMoney());
						bean[j].setBackNum(temp.getBackNum()+bean[j].getBackNum());
						bean[j].setSumWeight(temp.getSumWeight()+bean[j].getSumWeight());
					}
				}
			}
		
			for(int i=0;i<bean.length;i++){
				temp_list.add(bean[i]);
			}
		}
		return temp_list;
	}
}
