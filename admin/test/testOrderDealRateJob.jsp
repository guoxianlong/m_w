<%@page import="java.util.*,adultadmin.bean.order.OrderDealRateBean,adultadmin.util.DateUtil"%>
<%@page import="adultadmin.service.infc.IDealRateService"%>
<%@page import="java.sql.ResultSet,java.sql.SQLException,adultadmin.service.*,adultadmin.service.infc.IBaseService,adultadmin.util.StatUtil,adultadmin.util.NumberUtil" %>

<%@ page contentType="text/html;charset=utf-8"%>
<%
	 //测试定时任务
	// 成人用品——3,手机——1,数码——2,电脑——5,服装——4,鞋子——6,护肤品——7,  其他——9 保健品--13  小家电-8 行货手机订单-10 饰品-11 包-12
    String[] orderTypes = {"3","1","2","5","4","6","7","8,9,10,11,12,13"};
	float[] dealRates = new float[orderTypes.length]; 
	String[] areaNo={"", " and areano=9"," and areano <>9"}; //    9 为无锡订单 !=9为北京订单  user_order中
	
	Calendar nextRun = null;	
	Calendar lastRun = null; 

	nextRun = Calendar.getInstance();
	lastRun = Calendar.getInstance();

	if(0<=nextRun.get(Calendar.MINUTE)&&nextRun.get(Calendar.MINUTE)<30){
		nextRun.set(Calendar.MINUTE, 0);
	}else{
		nextRun.set(Calendar.MINUTE, 30);
	}

	nextRun.set(Calendar.SECOND, 0);

	lastRun.set(Calendar.HOUR_OF_DAY, 0);
	lastRun.set(Calendar.MINUTE, 0);
	lastRun.set(Calendar.SECOND, 0);


	IDealRateService service = null;

	try{
		Thread.sleep(30);

		int hour = nextRun.get(Calendar.HOUR_OF_DAY);
		int minute = nextRun.get(Calendar.MINUTE);

		ResultSet rs;
		int dealedOrder = 0;
		int totalOrder = 0;
		float dealRate = 0;

		if((8<=hour && hour<=23 ) || (hour==0 && minute==0)){

			service = ServiceFactory.createDealRateService(IBaseService.CONN_IN_SERVICE, null);
			try{

				OrderDealRateBean bean = new OrderDealRateBean();
				String date = DateUtil.formatDate(nextRun.getTime(),DateUtil.normalTimeFormat);
				String lastDate = DateUtil.formatDate(lastRun.getTime(),DateUtil.normalTimeFormat);
				int minId = StatUtil.getDayFirstOrderId(DateUtil.formatDate(lastRun.getTime(),DateUtil.normalDateFormat));

				int id = service.getNumber("id", "order_deal_rate", "max", "id > 0") + 1;
				
				bean.setStatisticDatetime(DateUtil.formatDate(nextRun.getTime(), DateUtil.normalTimeFormat));
				String sql="";
				//订单区域
				for(int j=0;j<areaNo.length;j++){
					//总成交率
					sql = "select sum(t1.c),sum(t20.c),sum(t40.c) from (select operator da,count(*) c from user_order where id >= "+minId+areaNo[j]+" group by da) t1 left outer join"+ 
					"(select operator da,count(*) c,sum(price*discount) p from user_order where status in (3,6,9,12,14) and id >= "+minId+areaNo[j]+" and create_datetime between '"+lastDate+"' and '"+date+"' group by da) t20 on t1.da=t20.da left outer join"+ 
					"(select operator da,count(*) c,sum(price*discount) p from user_order where status<>10 and id >= "+minId+areaNo[j]+" and  create_datetime between '"+lastDate+"' and '"+date+"' group by da) t40 on t1.da=t40.da";
					rs = service.getDbOp().executeQuery(sql);
					if(rs.next()){
						dealedOrder = rs.getInt(2);
						totalOrder = rs.getInt(3);
					}
					dealRate = Float.parseFloat(NumberUtil.div(dealedOrder * 100, totalOrder));
					bean.setTotalDealRate(dealRate);
					//计算分类成交率
					for(int i=0;i<orderTypes.length;i++){
						sql = "select sum(t1.c),sum(t20.c),sum(t40.c) from (select operator da,count(*) c from user_order where id >= "+minId+" and order_type in ("+orderTypes[i]+") "+areaNo[j]+" group by da) t1 left outer join"+ 
						"(select operator da,count(*) c,sum(price*discount) p from user_order where status in (3,6,9,12,14) and id >= "+minId+" and create_datetime between '"+lastDate+"' and '"+date+"' and order_type in ("+orderTypes[i]+") "+areaNo[j]+" group by da) t20 on t1.da=t20.da left outer join"+ 
						"(select operator da,count(*) c,sum(price*discount) p from user_order where status<>10 and id >= "+minId+" and create_datetime between '"+lastDate+"' and '"+date+"' and order_type in ("+orderTypes[i]+") "+areaNo[j]+" group by da) t40 on t1.da=t40.da";
						System.out.println(sql);
						rs = service.getDbOp().executeQuery(sql);
						if(rs.next()){
							dealedOrder = rs.getInt(2);
							totalOrder = rs.getInt(3);
						}else{
							dealedOrder = 0;
							totalOrder = 0;
						}
						dealRate = Float.parseFloat(NumberUtil.div(dealedOrder * 100, totalOrder));
						dealRates[i] = dealRate;

					}
					if(j==0){
						bean.setId(id);
						bean.setAreaNo(0);
					}else{
						bean.setId(id+j);
						bean.setAreaNo(7+j);  //0为总统计 8为无锡 9为北京 
					}
					
					bean.setAdultDealRate(dealRates[0]);
					bean.setPhoneDealRate(dealRates[1]);
					bean.setDigitalDealRate(dealRates[2]);
					bean.setComputerDealRate(dealRates[3]);
					bean.setDressDealRate(dealRates[4]);
					bean.setShoeDealRate(dealRates[5]);
					bean.setSkincareDealRate(dealRates[6]);
					bean.setOtherDealRate(dealRates[7]);
					
					service.addOrderDealRate(bean);

					rs.close();
				}
				if(hour==0 && minute==0){
					lastRun.setTime(nextRun.getTime());
					lastRun.set(Calendar.SECOND, 0);
				}
				out.print("执行成功");
			}catch(SQLException sqle) {
				sqle.printStackTrace();
			}finally {
				service.releaseAll();
				service = null;
			}
		}
	}catch(InterruptedException ie){
		ie.printStackTrace();
	}finally {
		if(service != null){
			service.releaseAll();
		}
	}
%>
 