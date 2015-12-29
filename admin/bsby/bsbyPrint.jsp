<%@ page contentType="text/html;charset=utf-8"%><%@page
	import="adultadmin.bean.stock.ProductStockBean"%><%@page
	import="java.util.Map,adultadmin.util.*"%>
<%@page import="adultadmin.bean.bybs.BsbyOperationnoteBean"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%><%@include
	file="../taglibs.jsp"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ page isELIgnored="false"%>
<%
ByBsAction action = new ByBsAction();
action.printBsBy(request,response);

String opid = request.getParameter("opid");
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
}

			BsbyOperationnoteBean bsbyOperationnoteBean = (BsbyOperationnoteBean) request 
					.getAttribute("bsbyOperationnoteBean"); 
			if (bsbyOperationnoteBean != null) { 
				Map stockTypeMap = ProductStockBean.stockTypeMap; 
				Map areaMap = ProductStockBean.areaMap; 
				response.setContentType("application/vnd.ms-excel");
				String now = DateUtil.getNow().substring(0,10);
				String fileName = now+" "+bsbyOperationnoteBean.getReceipts_number();
				response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
		%>

<table width="100%" cellpadding="3" cellspacing="1" border="1">
    <tr>
    	<td width="100" align="left" colspan="6"><strong>单据类型:${title }  &nbsp; &nbsp;&nbsp;&nbsp;   单据号:<%=bsbyOperationnoteBean.getReceipts_number()%></strong></td>
	</tr>

	<tr>
  <td width="100" align="center">产品编号</td>
  <td width="100" align="center">产品名称</td>
  <td width="100" align="center">原名称</td>
  <td width="100" align="left" >库类型：</td>
  <td width="100" align="left" >库区域:</td>
  <td width="100" align="center">数量</td>
    

</tr>
	
	<logic:present name="bsbyProductList" scope="request">
	<logic:iterate name="bsbyProductList" id="item" indexId="index" type="adultadmin.bean.bybs.BsbyProductBean">
	
<tr>
  <td width="100" align="center"><%=item.getProduct_code() %></td>
  <td width="100" align="center"><%=item.getProduct_name() %></td>
  <td width="100" align="center"><%=item.getOriname() %></td>
   <td width="100" align="center"><%=stockTypeMap.get(Integer
								.valueOf(bsbyOperationnoteBean
										.getWarehouse_type()))%></td>
										
										<td width="100" align="center"><%=areaMap.get(Integer.valueOf(bsbyOperationnoteBean
								.getWarehouse_area()))%></td>
										
  <td width="100" align="center"><%=item.getBsby_count() %></td>
    
 
</tr>
	</logic:iterate>
	</logic:present>
	<tr>
		
		<td width="100" align="left" colspan="6" >添加人:<%if(bsbyOperationnoteBean.getOperator_name()!=null){%><%=bsbyOperationnoteBean.getOperator_name() %><%}%>
		&nbsp;&nbsp;
		添加时间:<%=bsbyOperationnoteBean.getAdd_time().substring(0,16) %>
		&nbsp;&nbsp;		
		审核人:<%if(bsbyOperationnoteBean.getEnd_oper_name()!=null){%><%=bsbyOperationnoteBean.getEnd_oper_name()%><%}%>
		&nbsp;&nbsp;
		完成时间:<%=bsbyOperationnoteBean.getEnd_time().substring(0,16) %>
		&nbsp;&nbsp;
		</td>
	</tr>
</table>
<%}%>