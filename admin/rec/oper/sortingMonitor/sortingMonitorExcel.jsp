<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%
List teamList = (List)request.getAttribute("teamList");
List xiaojiList = (List)request.getAttribute("xiaojiList");
SortingBatchGroupBean zongjiBean = (SortingBatchGroupBean)request.getAttribute("zongjiBean");
String completeGroupCount=(String)request.getAttribute("completeGroupCount");
String completeOrderCount=(String)request.getAttribute("completeOrderCount");
String noReceiveGroupCount=(String)request.getAttribute("noReceiveGroupCount");
String noReceiveOrderCount=(String)request.getAttribute("noReceiveOrderCount");
String noDisposeOrderCount=(String)request.getAttribute("noDisposeOrderCount");
String overTimeOrderCount=(String)request.getAttribute("overTimeOrderCount"); 
String laterDatetime=(String)request.getAttribute("laterDatetime");
String firstDatetime=(String)request.getAttribute("firstDatetime");
%>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<script language="JavaScript" src="/adult-admin/js/My97DatePicker/WdatePicker.js"></script>
<head>
<script type="text/javascript">
function showPackage(id){
	$("#"+id).slideToggle();
}
</script>
<title>分拣监控页面</title>
</head>
<%
	response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	String fileName = "分拣监控导出";
	response.setHeader("Content-disposition", "attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%>
<body>
	<div align="center"></div>
	<table  width="99%" border="0" cellpadding="3" cellspacing="1" bgcolor="#4c6e92" align="center">
	<%if(zongjiBean!=null) {
	%>
		 <tr bgcolor="#00CCFF">
			<td><div align="center"><strong>总计:</strong></div></td>
			<td><div align="center"><strong><%=zongjiBean.getStaffCount() %>人</strong></div></td>
			<td><div align="center"><strong><font color='red'><%=zongjiBean.getOverTimeOrderCount()%></font></strong></div></td>
			<td><div align="center"></div></td>
			<td><div align="center"></div></td>
			<td><div align="center"><strong><%=zongjiBean.getGroupCount()%></strong></div></td>
			<td><div align="center"><strong><%=zongjiBean.getCompleteOrderCount()%></strong></div></td>
			<td><div align="center"><strong><font color='red'><%=zongjiBean.getNoCompleteOrderCount()%></font></strong></div></td>
			<td><div align="center"><strong><%=zongjiBean.getSkuCount()%></strong></div></td>
			<td><div align="center"><strong><%=zongjiBean.getProductCount()%></strong></div></td>
			<td><div align="center"><strong><%=zongjiBean.getPassageCount()%></strong></div></td>
		</tr><%} %>
		<tr bgcolor="#e8e8e8">
			<td><div align="center"><strong>姓名</strong></div></td>
			<td><div align="center"><strong>员工号</strong></div></td>
			<td><div align="center"><strong>分拣超时订单</strong></div></td>
			<td><div align="center"><strong>作业开始时间</strong></div></td>
			<td><div align="center"><strong>最后领单时间</strong></div></td>
			<td><div align="center"><strong>完成波次</strong></div></td>
			<td><div align="center"><strong>完成订单</strong></div></td>
			<td><div align="center"><strong>未完成订单</strong></div></td>
			<td><div align="center"><strong>完成SKU</strong></div></td>
			<td><div align="center"><strong>完成商品</strong></div></td>
			<td><div align="center"><strong>巷道数</strong></div></td>
		</tr>
		<%if(teamList!=null){
		for(int i=0;i<teamList.size();i++){
			SortingBatchGroupBean xiaojiBean =(SortingBatchGroupBean)xiaojiList.get(i);
		%>
		<tr bgcolor="#e8e8e8">
			<td colspan="11" bgcolor="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%if(i==0){ %>第<%=i+1%>班:(开始领单时间<%=StringUtil.cutString(firstDatetime,10,16) %>&nbsp;--<%=StringUtil.cutString(laterDatetime,10,16)%>)<%}else{ %>第<%=i+1%>班:(开始领单时间<%=StringUtil.cutString(xiaojiBean.getTeamBeginTime(),10,16) %>&nbsp;--<%=StringUtil.cutString(xiaojiBean.getTeamEndTime(),10,16)%>)<%} %></td>
		</tr>
		<%
			List l=(List)teamList.get(i);
			for(int j=0;j<l.size();j++){
			SortingBatchGroupBean bean =(SortingBatchGroupBean)l.get(j);
		%>
		<tr bgcolor="#e8e8e8">
			<td><div align="center"><%=bean.getStaffName()%></div></td>
			<td><div align="center"><%=bean.getStaffCode()%></div></td>
			<td><div align="center"><%if(bean.getOverTimeOrderCount()!=0) {%><a href="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingOvertimeOrderList&staffCode=<%=bean.getStaffCode()%>" target="_blank"><font color='red'><%=bean.getOverTimeOrderCount()%></font></a><%}else{ %><%=bean.getOverTimeOrderCount()%><%} %></div></td>
			<td><div align="center"><%=StringUtil.cutString(bean.getBegindatetime(),10,16)%></div></td>
			<td><div align="center"><%=StringUtil.cutString(bean.getFinallReceiveOrderTime(),10,16)%></div></td>
			<td><div align="center"><%=bean.getGroupCount()%></div></td>
			<td><div align="center"><%=bean.getCompleteOrderCount() %></div></td>
			<td><div align="center"><font color='red'><%=bean.getNoCompleteOrderCount() %></font></div></td>
			<td><div align="center"><%=bean.getSkuCount()%></div></td>
			<td><div align="center"><%=bean.getProductCount()%></div></td>
			<td><div align="center"><%=bean.getPassageCount()%></div></td>
		</tr><%} %>
		<tr bgcolor="#FFFF99">
			<td><div align="center">小计:</div></td>
			<td><div align="center"><%=xiaojiBean.getStaffCount()%>人</div></td>
			<td><div align="center"><font color='red'><%=xiaojiBean.getOverTimeOrderCount()%></font></div></td>
			<td><div align="center"></div></td>
			<td><div align="center"></div></td>
			<td><div align="center"><%=xiaojiBean.getGroupCount()%></div></td>
			<td><div align="center"><%=xiaojiBean.getCompleteOrderCount()%></div></td>
			<td><div align="center"><font color='red'><%=xiaojiBean.getNoCompleteOrderCount()%></font></div></td>
			<td><div align="center"><%=xiaojiBean.getSkuCount()%></div></td>
			<td><div align="center"><%=xiaojiBean.getProductCount()%></div></td>
			<td><div align="center"><%=xiaojiBean.getPassageCount()%></div></td>
		</tr>
		<%} }%>
	</table>
</body>
</html>