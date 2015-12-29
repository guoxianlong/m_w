<%@ page contentType="text/html;charset=utf-8"%><%@page
	import="adultadmin.bean.stock.ProductStockBean"%><%@page
	import="java.util.Map,adultadmin.action.vo.*,adultadmin.bean.*,adultadmin.bean.cargo.*"%>
<%@page import="adultadmin.bean.bybs.*"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%>
<%@page import="adultadmin.bean.bybs.BsbyProductBean" %>
<%@page import="java.util.List"%>
<%@page import="adultadmin.util.StringUtil"%><%@include
	file="../taglibs.jsp"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ page isELIgnored="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
  CargoInfoAreaBean area = (CargoInfoAreaBean)request.getAttribute("stockArea");
 	List bsbyReasonList = (List)request.getAttribute("bsbyReasonList");//报损原因列表
%>
<html>
	<head>
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>

<script type="text/javascript">

	function addproduct(code,pid,price,proxyId)
	{
		document.addbybsProductForm.productCode.value=code;
		var valueGD = 1;
		valueGD = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
		
		if(valueGD != null){
			document.addbybsProductForm.planCountGD.value = valueGD;
			document.addbybsProductForm.submit();
		}
	}function addproduct(code,cargoCode)
	{
		document.addbybsProductForm.productCode.value=code;
		document.addbybsProductForm.cargoCode.value=cargoCode;
		var valueGD = 1;
		valueGD = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
		
		if(valueGD != null){
			document.addbybsProductForm.planCountGD.value = valueGD;
			document.addbybsProductForm.submit();
		}
	}
	function button1()
	{
		var type = document.getElementById("type").value;
		var remark = document.getElementById("remark").value;
		var opid = document.getElementById("id").value;
		var biaodantype = document.getElementById("biaodantype").value;
		document.location.href="<%=request.getContextPath() %>/admin/bsby/updateCurrentType.jsp?type="+type+"&opid="+opid+"&remark="+remark+"&biaodantype="+biaodantype;
	}
	function button2()
	{
		var remark = document.getElementById("remark").value;
		var type1= document.getElementById("type1").value;
		var opid = document.getElementById("id").value;
		var biaodantype = document.getElementById("biaodantype").value;
		document.location.href="<%=request.getContextPath() %>/admin/bsby/updateCurrentType.jsp?type1="+type1+"&opid="+opid+"&remark="+remark+"&biaodantype="+biaodantype;
	}
	

</script>
	</head>

	<body>
		<% 
			BsbyOperationnoteBean bsbyOperationnoteBean = (BsbyOperationnoteBean) request 
					.getAttribute("bsbyOperationnoteBean"); 
			CargoInfoAreaBean stockArea = (CargoInfoAreaBean)request.getAttribute("stockArea");
					
			if (bsbyOperationnoteBean != null) { 
				Map stockTypeMap = ProductStockBean.stockTypeMap; 
				Map areaMap = ProductStockBean.areaMap; 
				
		%>
		<form method="post" action="bsby/afterEdit.jsp">
		<input type="hidden" name="biaodantype" id="biaodantype" value="<%=bsbyOperationnoteBean.getType() %>">
		<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="id" id="id"/>
			编号:<%=bsbyOperationnoteBean.getReceipts_number()%>&nbsp;&nbsp;<%if(bsbyOperationnoteBean.getSource()>0){ %>盘点作业单编号:<%=bsbyOperationnoteBean.getSourceCode()%>&nbsp;&nbsp;<%} %>库类型：<%=stockTypeMap.get(Integer
								.valueOf(bsbyOperationnoteBean
										.getWarehouse_type()))%>&nbsp;&nbsp;
			库区域:<%=areaMap.get(Integer.valueOf(bsbyOperationnoteBean
								.getWarehouse_area()))%>&nbsp;&nbsp;
			状态:<%=bsbyOperationnoteBean.current_typeMap.get(Integer
								.valueOf(bsbyOperationnoteBean
										.getCurrent_type()))%>
			&nbsp;&nbsp;
			<%-- 单子的初始状态为“处理中”，对应操作为“提交审核”，提交后状态改为“审核中”，“审核中”的单据对应操作“通过审核”和“未通过审核”，未通过审核状态改为“审核未通过”，对应的操作为“提交审核”，通过审核状态改为“已完成”--%>
			<%--谁添加的单据谁才有权限修改单据内的具体参数和将这个单据“提交审核”，当然具有审核权限的人也应据修改单据参数的权限 --%>
			<%if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3
					||(bsbyOperationnoteBean.getCurrent_type()==6&&!group.isFlag(413))
					||(bsbyOperationnoteBean.getCurrent_type()==1&&!group.isFlag(229))
					||(bsbyOperationnoteBean.getCurrent_type()==0&&user.getId()!=bsbyOperationnoteBean.getOperator_id())
					||(bsbyOperationnoteBean.getCurrent_type()==2&&user.getId()!=bsbyOperationnoteBean.getOperator_id()))
			{ }else{%>
			<logic:present name="buttonString">
				<input type="hidden" value="${type }" name="type" id="type"/>
				<input type="button" value="${buttonString }" onclick="button1()"/>
				
				
			</logic:present>
			<logic:present name="buttonString1">
				<input type="hidden" value="${type1 }" name="type1" id="type1"/>
				<input type="button" value="${buttonString1 }" onclick="button2()"/>				
			</logic:present>
			
			<%} %>
			&nbsp;&nbsp;&nbsp;
			制作人:<%=bsbyOperationnoteBean.getOperator_name() %>
			&nbsp;&nbsp;&nbsp;
			运营审核人:<%if(bsbyOperationnoteBean.getFinAuditName()!=null)%><%=bsbyOperationnoteBean.getFinAuditName() %>
			&nbsp;&nbsp;&nbsp;
			财务审核人:<%if(bsbyOperationnoteBean.getEnd_oper_name()!=null)%><%=bsbyOperationnoteBean.getEnd_oper_name() %>
			<br />
			<%//提交审核后 单据的添加人不能再修改备注 但如果单据添加人是有审核权限的 那么他还可以修改备注
			if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3){
				
			}
			else{ 
				if(bsbyOperationnoteBean.getType()==0)
				{%>
					报损原因:
				<%
				}else
				{%>
					报溢原因:
				<%
				} %>
				<select name='remark' id='remark'>
				<!-- 
					<option value="${bsbyOperationnoteBean.remark}"><%if(bsbyOperationnoteBean.getRemark()!=null){%><%= bsbyOperationnoteBean.getRemark()%><%} %></option>
					 -->
						   <%if(bsbyReasonList!=null && bsbyReasonList.size()>0){ 
							   for(int i=0;i<bsbyReasonList.size();i++){
								   BsbyReason bean =(BsbyReason)bsbyReasonList.get(i);
							%>
								<option value="<%=bean.getReason()%>" <%if(bean.getReason()==bsbyOperationnoteBean.getRemark()) {%>selected="selected"<%} %> ><%=bean.getReason()%></option>  
						   <%}} %>
				</select>
				&nbsp;&nbsp;&nbsp;
				<%if((bsbyOperationnoteBean.getCurrent_type()!=4&&group.isFlag(413))||(bsbyOperationnoteBean.getCurrent_type()==0&&!group.isFlag(413))){%>
					<input type="submit" value="修改" />
				<%} %>
			<%} %>
			
		</form>
		<%if(group.isFlag(229)||group.isFlag(413)||bsbyOperationnoteBean.getCurrent_type()==0
				||bsbyOperationnoteBean.getCurrent_type()==1||bsbyOperationnoteBean.getCurrent_type()==6
				||bsbyOperationnoteBean.getCurrent_type()==3||bsbyOperationnoteBean.getCurrent_type()==4){%>
			<%if(group.isFlag(229)&&(bsbyOperationnoteBean.getCurrent_type()==1||bsbyOperationnoteBean.getCurrent_type()==6||bsbyOperationnoteBean.getCurrent_type()==3||bsbyOperationnoteBean.getCurrent_type()==4)){ %>
				<form action="bsby/updateFinAuditRemark.jsp" method="post">
				运营审核意见:
				<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="opid" id="id"/>
				<input type="text" name="finAuditRemark" size="50" id="finAuditRemark" value="${bsbyOperationnoteBean.finAuditRemark }"/>
				&nbsp;&nbsp;&nbsp;
				<%if(group.isFlag(229)&&bsbyOperationnoteBean.getCurrent_type()==1){%>
					<input type="submit" value="修改"  />
				<%} %>
				</form>
			<%}
			if(group.isFlag(413)&&(bsbyOperationnoteBean.getCurrent_type()==6||bsbyOperationnoteBean.getCurrent_type()==4)){%>
				<form action="bsby/updateExamineSuggestion.jsp" method="post">
				财务审核意见:
				<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="opid" id="id"/>
				<input type="text" name="examineSuggestion" size="50" id="examineSuggestion" value="${bsbyOperationnoteBean.examineSuggestion }"/>
				&nbsp;&nbsp;&nbsp;
				<%if(group.isFlag(413)&&bsbyOperationnoteBean.getCurrent_type()==6){%>
					<input type="submit" value="修改"  />
				<%} %>
				</form>
			<%}%>
		<%} %>
		<br />
		
		<%if(bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==1){%>
			<fieldset>
   				<legend>操作：</legend>
				<p align="center"><a href="bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
				| <a href="<%=request.getContextPath()%>/admin/bybs.do?method=begin">返回报损报溢操作记录列表</a> 
				|<a href="bsby/bsbyPrint.jsp?opid=${bsbyOperationnoteBean.id }&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" target="_blank"> 导出列表</a>
				</p>
			</fieldset>	
		<%}
		
		}%>

<%
Integer listCount = (Integer)request.getAttribute("listCount");
if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3){}
else{
	if(StringUtil.toInt(listCount.toString())==0)
			{//如果单据中已经有了商品就不能再添加商品%>
			
<fieldset>
   <legend>产品信息</legend>
	<form method="post" name="addbybsProductForm" action="bsby/addbybsProduct.jsp">
	<p align="center">	
	<input type="hidden" name="opid" value="${bsbyOperationnoteBean.id }">
	<input type="hidden" name="opcode" value="<%=bsbyOperationnoteBean.getReceipts_number()%>">
	产品编号：<input type="text" name="productCode" value="" size="8"/>&nbsp;数量：<input type="text" name="planCountGD" value="" size="3"/>&nbsp;货位号：<input type="text" name="cargoCode" size="12"/>&nbsp;<input type="submit" value="添加"><br/>
	</p>
	<p align="center"><a href="bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
	| <a href="<%=request.getContextPath()%>/admin/bybs.do?method=begin">返回报损报溢操作记录列表</a> 
	|<a href="bsby/bsbyPrint.jsp?opid=${bsbyOperationnoteBean.id }&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" 	> 导出列表</a></p>
	</form>
</fieldset>


<fieldset>
	<legend>产品查询</legend>
	<form method=post action="<%=request.getContextPath() %>/admin/isearchproduct.do" target=sp onsubmit="document.all.d1.style.display='block';return true;">
		<input type="hidden" name="type" value="1" />
		<input type="hidden" name="operType" value="<%=bsbyOperationnoteBean.getType() %>" />
		<input type="hidden" name="stockType" value="<%=bsbyOperationnoteBean.getWarehouse_type() %>" />
		<input type="hidden" name="stockAreaId" value="<%=area.getId() %>" />
		<input type=hidden name="code" value="" size=12/>
		<input type="hidden" name="frombybs" value="yes"/>
		<p align="center">
			产品名：<input type=text name="name" value="" size=12>
			<input type=submit value="查询产品" onclick="return document.all.d1.style.display='block';">
			<input type=button value="关闭窗口" onclick="document.all.d1.style.display='none';window.close();">
		</p>
	</form>
	<div id="d1" style="display:none">
	<iframe name=sp width=90% height=300 align=center frameborder=0>
	</iframe>
	</div>
</fieldset><%}else if(bsbyOperationnoteBean.getCurrent_type()==0){
%>
<fieldset>
   <legend>操作：</legend>
	<p align="center"><a href="bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
	| <a href="<%=request.getContextPath()%>/admin/bybs.do?method=begin">返回报损报溢操作记录列表</a> 
	|<a href="bsby/bsbyPrint.jsp?opid=${bsbyOperationnoteBean.id }&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" target="_blank"> 导出列表</a></p>
</fieldset>	
<%
} %>
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
  <td width="6%">${title }量</td>
  <%if(group.isFlag(413)) {%>
  <td>${title }单价 含税</td>
  <td>${title }单价 不含税</td>
  <td>${title }总额 含税</td>
  <td>${title }总额 不含税</td>
  <%} %>
  <td width="15%">${title }前(后)可用库存量  </td>
  <td width="10%">${title }货位号 </td>
  <td width="10%">操作</td>
  <td width="10%">查进销存</td>
</tr>
<form method="post" action="bsby/updateBsbyProductCount.jsp">
<logic:present name="bsbyProductList">
<logic:iterate name="bsbyProductList" id="item" indexId="index" type="adultadmin.bean.bybs.BsbyProductBean"> 

<tr>
<input type="hidden" name="opid" value="${bsbyOperationnoteBean.id }"/>

<input type="hidden" name="opcode" value="<%=bsbyOperationnoteBean.getReceipts_number()%>">

  <td ><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=item.getProduct_id() %>" ><%=item.getProduct_code() %></a></td>
 <%if(group.isFlag(413)) {%>
  	<td><%=item.getProductLine() %></td>
	<td><%=item.getParentName1() %></td>
	<td><%=item.getParentName2() %></td>
	<td><%=item.getParentName3() %></td>
 <%} %>
  <td ><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=item.getProduct_id() %>" ><%=item.getProduct_name() %></a></td>  
  <td ><a href="<%=request.getContextPath() %>/admin/fproduct.do?id=<%=item.getProduct_id() %>" ><%=item.getOriname() %></a></td>

  <td ><%if(bsbyOperationnoteBean.getCurrent_type()==0||bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==5)
			{
			%><input size ="3"name="count<%=item.getId() %>" value="<%=item.getBsby_count() %>"  />
			<%}else
			{
			%>
			<%=item.getBsby_count() %>
			<%
			} %>
			</td>
  <%if(group.isFlag(413)) {%>
  	<td><%=item.getPrice() %></td>
  	<td><%=item.getNotaxPrice() %></td>
  	<td><%=item.getPrice()*item.getBsby_count() %></td>
  	<td><%=item.getNotaxPrice()*item.getBsby_count() %></td>
  <%} %>
  <td >
  <% 
  	BsbyOperationnoteBean bsbyOBean = (BsbyOperationnoteBean)request.getAttribute("bsbyOperationnoteBean");
    int x =ByBsAction.getProductCount(item.getProduct_id(),bsbyOBean
								.getWarehouse_area(),bsbyOBean.getWarehouse_type());
    if(bsbyOBean.getType() == 0 && bsbyOperationnoteBean.getCurrent_type() != 0 && 
    		bsbyOperationnoteBean.getCurrent_type() != 2) {
    	x = x + item.getBsby_count();
    }
    	%><%=x %>(<% int y = ByBsAction.updateProductCount(x,bsbyOBean.getType(),item.getBsby_count()); %><%=y %>) </td>  
  <td ><%=item.getBsbyCargo()==null?"":item.getBsbyCargo().getCargoInfo()==null?"":item.getBsbyCargo().getCargoInfo().getWholeCode() %></td>
  <td ><%if(bsbyOperationnoteBean.getCurrent_type()==0||bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==5){%><a href="bsby/delBsByProduct.jsp?pid=<%=item.getProduct_id() %>&bsbypid=<%=item.getId() %>&opid=<%=bsbyOperationnoteBean.getId() %>">删除</a><%} %></td>
  <td ><%if(group.isFlag(232)){ %>
  <a href="bsby/stockCardList.jsp?pid=<%=item.getProduct_id() %>" target="_blank">查</a>
  <%} %>
  </td>
</tr>
</logic:iterate>
</logic:present>
<tr>
<%if(bsbyOperationnoteBean.getCurrent_type()==0||bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==5)
			{
			%>
<td colspan="7" align="center"><input type="submit" value="修改"/></td>
<%} %>
</tr>
</form>
</table>
<%}%>
</center>
	</body>
</html>
