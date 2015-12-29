<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="adultadmin.util.NumberUtil,adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.order.EachProductLineBean"%>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.bean.UserGroupBean" %>

<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String startTime = StringUtil.convertNull(request.getParameter("startTime"));
String endTime = StringUtil.convertNull(request.getParameter("endTime"));
if(startTime.equals("")){
	startTime = String.valueOf(request.getAttribute("startTime")!=null?request.getAttribute("startTime"):"");
	endTime = String.valueOf(request.getAttribute("endTime")!=null?request.getAttribute("endTime"):"");
}
List eachList=null;
if(request.getAttribute("eachList")!=null){
	eachList = (List)request.getAttribute("eachList") ;
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>各产品线销量查询</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
 	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
 	 
 	 <script type="text/javascript">
		
		function submitForm(){
			var startTime = document.getElementById('startTime').value;
			var endTime = document.getElementById('endTime').value;
			
			if((startTime.trim()=='' && endTime.trim()!='')||(startTime.trim()!='' && endTime.trim()=='') ){
				alert('日期不能只输入一个');
				return false;
			}else if(startTime.trim()!='' && endTime.trim()!=''){
			
				if(CheckDate(startTime)==false || CheckDate(endTime)==false){
					return false;
				}
				
				if(validateSubDate(endTime,startTime)==false){
					return false;
				}
			}
			 
			return true;
		}	
 	 </script>
  </head>
  <body>
  	<form action="<%=basePath%>admin/eachProductLine.do" method="post"> 
  		各产品线销量查询<br/>
	  	<fieldset style="width:850px;">
		    订单创建时间： <input type="text" id="startTime" name="startTime" value="<%=startTime%>" onclick="SelectDate(this,'yyyy-MM-dd');"  size="10" />&nbsp;至&nbsp;
		         <input type="text" id="endTime" name="endTime" value="<%=endTime%>" onclick="SelectDate(this,'yyyy-MM-dd');"  size="10"/>
		         (<font color="red">查询天数不得超过31天</font>) 
			 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit" value="查询" onclick="return submitForm();"/> 
	  	</fieldset>	
	</form>	 	
	<br/><br/>											
	<table cellpadding="0" cellspacing="1"  bgcolor="#e8e8e8" width="95%" border="0"  bordercolor="#D8D8D5" >
		<tr bgcolor="#4688D6">
			<td height="25px">&nbsp;</td>
			<td align="center" colspan="<%=group.isFlag(41)?"3":"2"%>"><font color="#FFFFFF">发货单（已发货、已妥投、已结算、已退回、待退回）</font></td>
			<td align="center" colspan="<%=group.isFlag(41)?"3":"2"%>"><font color="#FFFFFF">成交单（待发货、待查款、已发货、已妥投、已结算）</font></td>
			<td align="center" colspan="<%=group.isFlag(41)?"3":"2"%>"><font color="#FFFFFF">退回单（已退回）</font></td>
		</tr>
		<tr bgcolor="#4688D6">
			<td align="center" height="25px"><font color="#FFFFFF">产品线</font></td>
			<td align="center"><font color="#FFFFFF">商品数量</font></td>
			<td align="center"><font color="#FFFFFF">销售金额</font></td>
			<%if(group.isFlag(41)){ %> 
			<td align="center"><font color="#FFFFFF">成本金额</font></td>
			<%}%>
			<td align="center"><font color="#FFFFFF">商品数量</font></td>
			<td align="center"><font color="#FFFFFF">销售金额</font></td>
			<%if(group.isFlag(41)){ %>
			<td align="center"><font color="#FFFFFF">成本金额</font></td>
			<%}%>
			<td align="center"><font color="#FFFFFF">商品数量</font></td>
			<td align="center"><font color="#FFFFFF">销售金额</font></td>
			<%if(group.isFlag(41)){ %>
			<td align="center"><font color="#FFFFFF">成本金额</font></td>
			<%}%>
		</tr>
	<%if(eachList!=null){ 
		int sumShipped=0,sumDealOrder=0, sumBackOrder=0;
		double sumShippedCost=0,sumDealCost=0,sumBackCost=0;
		double sumShippedSale=0,sumDealSale=0,sumBackSale=0;
		for(int i=0;i<eachList.size();i++){
			EachProductLineBean bean = (EachProductLineBean)eachList.get(i);
			sumShipped+= bean.getShipped();
			sumDealOrder+=bean.getDealOrder();
			sumBackOrder+= bean.getBackOrder();
			sumShippedCost+=bean.getShippedCost();
			sumDealCost+= bean.getDealOrderCost();
			sumBackCost+= bean.getBackOrderCost();
			sumShippedSale+=bean.getShippedSale();
			sumDealSale+= bean.getDealOrderSale();
			sumBackSale+= bean.getBackOrderSale();
	%>
	    <tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td align="center" height="25px"><%=bean.getProductLineName()%></td>
			<td align="center"><%=bean.getShipped()%></td>
			<td align="right"><%=NumberUtil.priceOrderZero(bean.getShippedSale())%></td>
			<%if(group.isFlag(41)){ %>
			<td align="right"><%=NumberUtil.priceOrderZero(bean.getShippedCost())%></td>
			<%}%>
			<td align="center"><%=bean.getDealOrder()%></td>
			<td align="right"><%=NumberUtil.priceOrderZero(bean.getDealOrderSale())%></td>
			<%if(group.isFlag(41)){ %>
			<td align="right"><%=NumberUtil.priceOrderZero(bean.getDealOrderCost())%></td>
			<%} %>
			<td align="center"><%=bean.getBackOrder()%></td>
			<td align="right"><%=NumberUtil.priceOrderZero(bean.getBackOrderSale())%></td>
			<%if(group.isFlag(41)){ %>
			<td align="right"><%=NumberUtil.priceOrderZero(bean.getBackOrderCost())%></td>
			<%}%>
		</tr>
	<%	}
		if(eachList.size()>1){
			%>
		<tr>
			<td align="center" height="25px">总计</td>
			<td align="center"><%=sumShipped%></td>
			<td align="right"><%=NumberUtil.priceOrderZero(sumShippedSale)%></td>
			<%if(group.isFlag(41)){ %>
			<td align="right"><%=NumberUtil.priceOrderZero(sumShippedCost)%></td>
			<%}%>
			<td align="center"><%=sumDealOrder%></td>
			<td align="right"><%=NumberUtil.priceOrderZero(sumDealSale)%></td>
			<%if(group.isFlag(41)){ %>
			<td align="right"><%=NumberUtil.priceOrderZero(sumDealCost)%></td>
			<%}%>
			<td align="center"><%=sumBackOrder%></td>
			<td align="right"><%=NumberUtil.priceOrderZero(sumBackSale)%></td>
			<%if(group.isFlag(41)){ %>
			<td align="right"><%=NumberUtil.priceOrderZero(sumBackCost)%></td>
			<%}%>
		</tr>	
			<% 
		}
	}%>	
	</table>
  </body>
</html>
 