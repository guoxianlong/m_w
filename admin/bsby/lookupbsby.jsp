<%@ page contentType="text/html;charset=utf-8"%><%@page
	import="adultadmin.bean.stock.ProductStockBean"%><%@page
	import="java.util.Map,adultadmin.action.vo.*,adultadmin.bean.*"%>
<%@page import="adultadmin.bean.bybs.BsbyOperationnoteBean"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%><%@include
	file="../taglibs.jsp"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ page isELIgnored="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup(); %>
<html>
	<head>
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
	<script type="text/javascript">
	function changeCurrentType()
	{
		var id = document.getElementById("id").value;
		window.location.href = "<%=request.getContextPath() %>/admin/bsby/updateToEnd.jsp?id="+id;
	}
	</script>

	</head>

	<body>
		<% 
			BsbyOperationnoteBean bsbyOperationnoteBean = (BsbyOperationnoteBean) request 
					.getAttribute("bsbyOperationnoteBean"); 
			if (bsbyOperationnoteBean != null) { 
				Map stockTypeMap = ProductStockBean.stockTypeMap; 
				Map areaMap = ProductStockBean.areaMap; 
				int type = bsbyOperationnoteBean.getType();
				String title = null;
				if(type==0)
				{
					title = "报损";
				
				}else{
					title = "报溢";
				}
		%>
		<form method="post" action="bsby/afterEdit.jsp">
		<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="id" id="id"/>
			编号:<%=bsbyOperationnoteBean.getReceipts_number()%>&nbsp;&nbsp;<%if(bsbyOperationnoteBean.getSource()>0){ %>盘点作业单编号:<%=bsbyOperationnoteBean.getSourceCode()%>&nbsp;&nbsp;<%} %>库类型：<%=stockTypeMap.get(Integer
								.valueOf(bsbyOperationnoteBean
										.getWarehouse_type()))%>&nbsp;&nbsp;
			库区域:<%=areaMap.get(Integer.valueOf(bsbyOperationnoteBean
								.getWarehouse_area()))%>&nbsp;&nbsp;
			状态:<%=bsbyOperationnoteBean.current_typeMap.get(Integer
								.valueOf(bsbyOperationnoteBean
										.getCurrent_type()))%>
			<%if((bsbyOperationnoteBean.getCurrent_type()==3&&group.isFlag(229))||(bsbyOperationnoteBean.getCurrent_type()==3&&user.getId()==bsbyOperationnoteBean.getOperator_id()) )
			{%>
			&nbsp;&nbsp;<input type="button" value="已完成" onclick="changeCurrentType();"/>
			<%} %>						
			&nbsp;&nbsp;
			<td width="100" align="left" colspan="7">
				&nbsp;&nbsp;&nbsp;
			制作人:<%=bsbyOperationnoteBean.getOperator_name() %>
			&nbsp;&nbsp;&nbsp;
			运营审核人:<%=bsbyOperationnoteBean.getFinAuditName() %>
			&nbsp;&nbsp;&nbsp;
			财务审核人:<%=bsbyOperationnoteBean.getEnd_oper_name() %>
			</td>
			<%-- 单子的初始状态为“处理中”，对应操作为“提交审核”，提交后状态改为“审核中”，“审核中”的单据对应操作“通过审核”和“未通过审核”，未通过审核状态改为“审核未通过”，对应的操作为“提交审核”，通过审核状态改为“已完成”--%>
			<%--谁添加的单据谁才有权限修改单据内的具体参数和将这个单据“提交审核”，当然具有审核权限的人也应据修改单据参数的权限 --%>
			
			<br>
			<%=title %>原因：
			<input type="text" name="remark" size="50" id="remark" value="${bsbyOperationnoteBean.remark }"/>
			&nbsp;&nbsp;&nbsp;
			运营审核意见：
			<input type="text" name="finAuditRemark" size="50" id="finAuditRemark" value="${bsbyOperationnoteBean.finAuditRemark }"/>
			<br/>
			财务审核意见：
			<input type="text" name="examineSuggestion" size="50" id="examineSuggestion" value="${bsbyOperationnoteBean.examineSuggestion }"/>
			<br />
			
		</form>
	<tr>

<fieldset>
   <legend>操作：</legend>
	<p align="center"><a href="bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> | <a href="<%=request.getContextPath()%>/admin/bybs.do?method=begin">返回报损报溢操作记录列表</a> |<a href="bsby/bsbyPrint.jsp?opid=${bsbyOperationnoteBean.id }&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" target="_blank"> 导出列表</a></p>
</fieldset>





<center>
<table width="95%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr>
  <td width="9%">产品编号</td>
  <%if(group.isFlag(413)) {%>
  <td align="center">产品线</td>
  <td align="center">一级分类</td>
  <td align="center">二级分类</td>
  <td align="center">三级分类</td>
  <%} %>
  <td width="24%">产品名称</td>
  <td width="24%">原名称</td>
  <%if(group.isFlag(413)) {%>
  <td>${title }单价 含税</td>
  <td>${title }单价 不含税</td>
  <td>${title }总额 含税</td>
  <td>${title }总额 不含税</td>
  <%} %>
  <td width="6%">${title }量</td>
  <td width="15%">${title }前(后)库存量  </td>
  <td width="10%">${title }货位号 </td>
  <td>查看进销存</td>	
</tr>

<logic:present name="bsbyProductList">
<logic:iterate name="bsbyProductList" id="item" indexId="index" type="adultadmin.bean.bybs.BsbyProductBean"> 

<tr>
  <td ><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=item.getProduct_id() %>" ><%=item.getProduct_code() %></a></td>
  <%if(group.isFlag(413)) {%>
  	<td><%=item.getProductLine() %></td>
	<td><%=item.getParentName1() %></td>
	<td><%=item.getParentName2() %></td>
	<td><%=item.getParentName3() %></td>
 <%} %>
  <td ><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=item.getProduct_id() %>" ><%=item.getProduct_name() %></a></td>
  <td ><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=item.getProduct_id() %>" ><%=item.getOriname() %></a></td>
  <%if(group.isFlag(413)) {%>
  	<td><%=item.getPrice() %></td>
  	<td><%=item.getNotaxPrice() %></td>
  	<td><%=item.getPrice()*item.getBsby_count() %></td>
  	<td><%=item.getNotaxPrice()*item.getBsby_count() %></td>
  <%} %>
  <td ><%=item.getBsby_count() %></td>
  <td ><%=item.getBefore_change() %>(<%=item.getAfter_change() %>)</td>  
  <td ><%=item.getBsbyCargo()==null?"":item.getBsbyCargo().getCargoInfo()==null?"":item.getBsbyCargo().getCargoInfo().getWholeCode() %></td>
  <td><%if(group.isFlag(232)){ %>
  <a href="bsby/stockCardList.jsp?pid=<%=item.getProduct_id() %>" target="_blank">查</a>
  <%} %></td>
</tr>
</logic:iterate>
</logic:present>
</table>
<%} %>
</center>
	</body>
</html>
