<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.bean.order.*, adultadmin.bean.buy.*,adultadmin.service.*,adultadmin.service.infc.*,adultadmin.service.impl.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%
	
	String startDate = StringUtil.dealParam(request.getParameter("startDate"));
	String endDate = StringUtil.dealParam(request.getParameter("endDate"));
	String startTime = StringUtil.dealParam(request.getParameter("startTime"));
	String endTime = StringUtil.dealParam(request.getParameter("endTime"));
	String start = DateUtil.getNow().substring(0, 10)+" 09:00";
	String end = DateUtil.getNow().substring(0, 16);
	String areaNo = StringUtil.dealParam(request.getParameter("areaNo"));
	if(startDate != null){
		start = startDate + " " + startTime;
	}
	if(endDate != null){
		end = endDate + " " + endTime;
	}
		
		
	IDealRateService service = ServiceFactory.createDealRateService(IBaseService.CONN_IN_SERVICE, null);
	try{
		
	List dealRateList = service.getOrderDealRateList("areano="+areaNo+" and statistic_datetime between '"+start+"' and '"+end+"'", -1, -1, "id");
	if(dealRateList==null||dealRateList.size()==0){
%>
<script>
alert("无需要导出的数据");
history.back(-1);
</script>

<%  }else{
	response.setContentType("application/vnd.ms-excel;charset=GBK");
	String now = DateUtil.formatDate(DateUtil.getNowDate(),"yyyyMMdd");
	String fileName = now;
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
	<tr bgcolor="#4688D6">
					<td align="center"><font color="#FFFFFF">时间</font></td>
					<td align="center"><font color="#FFFFFF">总成交率</font></td>
					<td align="center"><font color="#FFFFFF">成人</font></td>
					<td align="center"><font color="#FFFFFF">手机</font></td>
					<td align="center"><font color="#FFFFFF">数码</font></td>
					<td align="center"><font color="#FFFFFF">电脑</font></td>
					<td align="center"><font color="#FFFFFF">服装</font></td>
					<td align="center"><font color="#FFFFFF">鞋子</font></td>
					<td align="center"><font color="#FFFFFF">护肤品</font></td>
					<td align="center"><font color="#FFFFFF">其他</font></td>
					<td align="center"><font color="#FFFFFF">小家电</font></td>
					<td align="center"><font color="#FFFFFF">内衣</font></td>
					<td align="center"><font color="#FFFFFF">行货手机订单</font></td>
					<td align="center"><font color="#FFFFFF">包</font></td>
					<td align="center"><font color="#FFFFFF">保健品</font></td>
					<td align="center"><font color="#FFFFFF">手表</font></td>
					<td align="center"><font color="#FFFFFF">配饰</font></td>
					<td align="center"><font color="#FFFFFF">新奇特</font></td>
					<td align="center"><font color="#FFFFFF">内衣</font></td>
				</tr>
				<%						
				//内衣——3,手机——1,数码——2,电脑——5,服装——4,鞋子——6,护肤品——7,  其他——9  小家电-8 行货手机订单-10 饰品-11 包-12
				//保健品 13  手表-14 	配饰-15	新奇特-16		成人-17
					Iterator iter = dealRateList.listIterator();
					while(iter.hasNext()){
						OrderDealRateBean bean = (OrderDealRateBean)iter.next();
				 %>
				 <tr>
				 	<td align="center"><%=bean.getStatisticDatetime().substring(0,16)%></td>
				 	<td align="center"><%=bean.getTotalDealRate() %></td>
				 	<td align="center"><%=bean.getAdultDealRate() %></td>
				 	<td align="center"><%=bean.getPhoneDealRate() %></td>
				 	<td align="center"><%=bean.getDigitalDealRate() %></td>
				 	<td align="center"><%=bean.getComputerDealRate() %></td>
				 	<td align="center"><%=bean.getDressDealRate() %></td>
				 	<td align="center"><%=bean.getShoeDealRate() %></td>
				 	<td align="center"><%=bean.getSkincareDealRate() %></td>
				 	<td align="center"><%=bean.getOtherDealRate() %></td>
				 	<td align="center"><%=bean.getAppliances() %></td>
				 	<td align="center"><%=bean.getLicensedPhone() %></td>
				 	<td align="center"><%=bean.getAccessories() %></td>
				 	<td align="center"><%=bean.getBagDealRate() %></td>
				 	<td align="center"><%=bean.getHealthDealRate() %></td>
				 	<td align="center"><%=bean.getWatchDealRate() %></td>
				 	<td align="center"><%=bean.getPeishiDealRate() %></td>
				 	<td align="center"><%=bean.getXinqite() %></td>
				 	<td align="center"><%=bean.getUnderWareDealRate() %></td>
				 </tr>
				 <% } %>
</table>
<%}
}finally{
		service.releaseAll();
  }%>