package adultadmin.action.orderSale;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderDeliversBean;
import adultadmin.framework.IConstants;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

public class StockStatFangAction extends DispatchAction{
	
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int p = StringUtil.StringToId(request.getParameter("p"));
		String hasNextPage="yes";
		voOrder deliver = new voOrder();
		Iterator   it =deliver.deliverMapAll.entrySet().iterator();
		int i=0;
		String temp="(";
		String[] deliverValue = new String[deliver.deliverMapAll.size()];
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			temp+="'"+entry.getKey()+"'" ;
			deliverValue[i]=String.valueOf(entry.getKey());
			i++;
			if(i!=deliver.deliverMapAll.size())
				temp+=",";
		}
		temp+=")";
		if(temp.equals("()")){
			request.setAttribute("tip", "获取快递公司失败");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
	    Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
		Statement st = conn.createStatement();
		Connection conn1 = DbUtil.getConnection(DbOperation.DB_SLAVE2);
		Statement st1 = conn1.createStatement();
		
		List list = new ArrayList();
		int total=3*365;//时间数定为3年
		try {
			ResultSet rs=null;
			ResultSet rs1 =null;
			st.executeUpdate("set group_concat_max_len=1048576;");
			
			//根据页码算出order_stock的last_oper_time的范围
			SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
			String startDate="";
			String endDate="";
			Calendar cal1=Calendar.getInstance();
			Calendar cal2=Calendar.getInstance();
			cal1.add(Calendar.DATE, -(p+1)*30+1);
			cal2.add(Calendar.DATE, -p*30);
			startDate=sd.format(cal1.getTime())+" 00:00:00";
			endDate=sd.format(cal2.getTime())+" 23:59:59";
			StringBuffer  query= new StringBuffer("select left(os.last_oper_time, 10) de, count(*) , uo.deliver, sum(uo.dprice)" +
			" from order_stock os join user_order uo on os.order_id = uo.id where os.status in (2,4,6,7,8) and os.stock_area=1 ");
			query.append(" and os.last_oper_time between '");
			query.append(startDate);
			query.append("' and '");
			query.append(endDate);
			query.append("'");
			query.append(" and uo.deliver in "+ temp);
			query.append(" group by de DESC , uo.deliver ") ;
			
//			StringBuffer countQuery = new StringBuffer("select count(te.de) maxId from (select left(os.last_oper_time, 10) de " +
//				" from order_stock os join user_order uo on os.order_id = uo.id where os.status in (2,4,6,7,8) and os.stock_area=1");
//			countQuery.append(" and uo.deliver in "+ temp);
//			countQuery.append(" group by de) te");
//			
//			rs = st.executeQuery(countQuery.toString());
//			rs.next();
//			total=rs.getInt("maxId");
//			rs.close();
			
			LinkedHashMap odMap=new LinkedHashMap();
			rs = st.executeQuery(query.toString());
		    while (rs.next()) {
		    	OrderDeliversBean bean = new OrderDeliversBean();
		    	bean.setDateTime(rs.getString(1));
		    	bean.setSumNum(rs.getInt(2));
		    	bean.setDeliver(rs.getInt(3));
//		    	bean.setStr_code(rs.getString(4));
		    	bean.setSumMoney(rs.getDouble(4));
		    	
		    	odMap.put(bean.getDeliver()+"_"+bean.getDateTime(), bean);
		    	
//		    	if(bean.getStr_code()!=null && !bean.getStr_code().equals("")){
////		    		StringBuffer query1 = new StringBuffer("select sum(price * discount) from user_order where code in ('");
////		    		query1.append(bean.getStr_code().replace(",", "','")+"') ");
////		    		rs1 = st1.executeQuery(query1.toString());
////		    		rs1.next();
////		    		bean.setSumMoney(rs1.getDouble(1));
////		    		rs1.close();
//		    		
//		    		StringBuffer query2 = new StringBuffer("select count(id) ,sum(price * discount) from user_order where code in ('");
//		    		query2.append(bean.getStr_code().replace(",", "','")+"') and status = 11");
//		    		rs1 = st1.executeQuery(query2.toString());
//		    		rs1.next();
//		    		bean.setBackNum(rs1.getInt(1));
//		    		bean.setBackMoney(rs1.getDouble(2));
//		    		rs1.close();
//		    		
//		    	}else{
//		    		bean.setSumMoney(0);
//		    		bean.setBackNum(0);
//		    		bean.setBackMoney(0);
//		    	}
//		        list.add(bean);
		    }
		    
		    StringBuffer query2 = new StringBuffer("select left(os.last_oper_time, 10) de, count(*) , uo.deliver, sum(uo.dprice)" +
			" from order_stock os join user_order uo on os.order_id = uo.id where uo.status=11 and os.stock_area=1 ");
			query2.append(" and os.last_oper_time between '");
			query2.append(startDate);
			query2.append("' and '");
			query2.append(endDate);
			query2.append("'");
			query2.append(" and uo.deliver in "+ temp);
			query2.append(" group by de DESC , uo.deliver ") ;
    		rs1 = st1.executeQuery(query2.toString());
    		while(rs1.next()){
    			String datetime=rs1.getString(1);
    			int backNum=rs1.getInt(2);
    			int deliverNum=rs1.getInt(3);
    			double backMoney=rs1.getDouble(4);
    			
    			OrderDeliversBean bean = new OrderDeliversBean();
    			bean=(OrderDeliversBean)odMap.get(deliverNum+"_"+datetime);
    			if(bean!=null){
    				bean.setBackNum(backNum);
    				bean.setBackMoney(backMoney);
    			}
    		}
    		rs1.close();
		    rs.close();
		    st1.close();
		    st.close();
		    Iterator iter=odMap.keySet().iterator();
		    while(iter.hasNext()){
		    	String key=iter.next().toString();
		    	list.add(odMap.get(key));
		    }
		    list = changeList(list,deliverValue); 
		    if((p+1)*30>=total){
		    	hasNextPage="No";
		    }
		    request.setAttribute("orderDeliversList", list);
		    request.setAttribute("hasNextPage", hasNextPage);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(conn != null){
				conn.close();
			}
			if(conn1 != null){
				conn1.close();
			}
		}
		
		return mapping.findForward("success");
	}
	
	/**
	 * 
	 * 功能:改变list的排序， 集合时间相同的 对象。 若跟快递公司一样数量 则不新增
	 * <p>作者 李双 Jul 19, 2011 5:14:07 PM
	 * @param list
	 * @param deliver
	 * @return
	 */
	private List changeList(List list,String [] deliver){ 
		List temp_list = new ArrayList();
		int temp_int=0;
 		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
 				OrderDeliversBean bean =(OrderDeliversBean)list.get(i);
 				bean.getSubBeanList().add(bean);  //
				if(i!= list.size()-1){
					OrderDeliversBean bean1=(OrderDeliversBean)list.get(i+1);
					if(bean.getDateTime().equals(bean1.getDateTime())|| bean.getDateTime()== bean1.getDateTime()){
						temp_list.add(bean);
						for(int j=i+1;j<=deliver.length+i ;j++){
							if(j>=list.size()){
								temp_int = j;
								for(int x=0;x<deliver.length;x++){
									if(bean.getDeliver()!=Integer.parseInt(deliver[x]) && !contianDelivers(bean.getSubBeanList(),Integer.parseInt(deliver[x])))
										bean.getSubBeanList().add(new OrderDeliversBean(Integer.parseInt(deliver[x])));
								}
								break;
							}
							OrderDeliversBean bean2 =(OrderDeliversBean)list.get(j);
							if(bean.getDateTime().equals(bean2.getDateTime())|| bean.getDateTime()== bean2.getDateTime()){
								bean.getSubBeanList().add(bean2);
								temp_int = j;
							} else{
								for(int x=0;x<deliver.length;x++){
									if(bean.getDeliver()!=Integer.parseInt(deliver[x]) && !contianDelivers(bean.getSubBeanList(),Integer.parseInt(deliver[x])))
										bean.getSubBeanList().add(new OrderDeliversBean(Integer.parseInt(deliver[x])));
								}
								break;
							}
						}
					}else{
						temp_list.add(bean);
						for(int x=0;x<deliver.length;x++){
							if(bean.getDeliver()!=Integer.parseInt(deliver[x])&& !contianDelivers(bean.getSubBeanList(),Integer.parseInt(deliver[x])))
								bean.getSubBeanList().add(new OrderDeliversBean(Integer.parseInt(deliver[x])));
						}
					}
				}else{//最后一个是单独的。
					temp_list.add(bean);
					for(int x=0;x<deliver.length;x++){
						if(bean.getDeliver()!=Integer.parseInt(deliver[x])&& !contianDelivers(bean.getSubBeanList(),Integer.parseInt(deliver[x])))
							bean.getSubBeanList().add(new OrderDeliversBean(Integer.parseInt(deliver[x])));
					}
				}
				if(temp_int!=0 && temp_int>i){
					i=temp_int ;
					temp_int=0;
				}
			}
		}else{
			temp_list=list;
		}
		return temp_list;
	}
	
	/**
	 * 
	 * 功能:判断 list中是否存在的该快递公司
	 * <p>作者 李双 Jul 25, 2011 1:31:27 PM
	 * @return
	 */
	private boolean contianDelivers(List list,int deliver){
		boolean flag =false;
		for(int i=0;i<list.size();i++){
			OrderDeliversBean bean =(OrderDeliversBean)list.get(i);
			if(bean.getDeliver()==deliver){
				flag=true;
				break;
			}
		}
		return flag;
	}
}
