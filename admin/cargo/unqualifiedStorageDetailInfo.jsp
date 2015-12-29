<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%
	int productLine = 0;
	String productCode = "";
	String productName = "";
	String stockinTime = "";
	String exchangeTime = "";
	String exchangeCode = "";
	String buyStockCode = "";
	int wareArea = -1;
	String checkStockMissionCode = "";
	List productLineList = (List) request.getAttribute("productLineList");
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
	productLine = StringUtil.toInt(request.getParameter("productLine"));
	stockinTime = StringUtil.convertNull(request.getParameter("stockinTime"));
	exchangeTime = StringUtil.convertNull(request.getParameter("exchangeTime"));
	exchangeCode = StringUtil.convertNull(request.getParameter("exchangeCode"));
	buyStockCode = StringUtil.convertNull(request.getParameter("buyStockCode"));
	productCode = StringUtil.convertNull(request.getParameter("productCode"));
	productName = StringUtil.convertNull(request.getParameter("productName"));
	wareArea = StringUtil.toInt(request.getParameter("wareArea"));
	checkStockMissionCode = StringUtil.convertNull(request.getParameter("checkStockMissionCode"));
	String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request,wareArea);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'returnOrderInfo.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		function printTarget() {
		
		}
		function exportTarget() {
			var form1 = document.getElementById('form1');
			form1.action='checkStockinMissionAction.do?method=exportUnqualifiedStorageDetailInfo';
			form1.submit();
		}
		
		function printTarget() {
			var form1 = document.getElementById('form1');
			form1.action='checkStockinMissionAction.do?method=printUnqualifiedStorageDetailInfo';
			form1.submit();
		}
	</script>
	
</head>
<body>
<div align="center">
<h2>不合格品接收明细表</h2>
<div style="border-style:solid;border-color:#000000;border-width:1px;width:95%">
	<form action="getUnqualifiedStorageDetailInfo.mmx?method=getUnqualifiedStorageDetailInfo" method="post">
	<table>
	
		<tr><td align="left">
	产品编号：
	<input type="text" size="13" name="productCode" id="productCode" value="<%= productCode %>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	原名称：
	<input type="text" size="20" name="productName" id="productName" value="<%= productName %>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	产品线：
		<select name="productLine">
								<option value="0">请选择</option>
							<%
							for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
								voProductLine proLineBean = (voProductLine) productLineList.get(p);
							%>
								<option value="<%= proLineBean.getId() %>" <%= proLineBean.getId() == productLine ? "selected" : "" %> ><%= proLineBean.getName() %></option>
							<%
							}
							%>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td>
		</tr>
		<tr>
		<td>
	到货时间：
		<input type="text" size="13" name="stockinTime" onclick="WdatePicker();" value="<%= stockinTime%>" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	调拨时间：
		<input type="text" size="13" name="exchangeTime" onclick="WdatePicker();" value="<%= exchangeTime %>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	调拨单号：
		<input type="text" name="exchangeCode" size="13"  value="<%= exchangeCode%>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td></tr>
		<tr><td align="left">
		
	质检任务单号：
		<input type="text" name="checkStockMissionCode" id="checkStockMissionCode" value="<%= checkStockMissionCode%>" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	预计到货单号：
		<input type="text" name="buyStockCode" size="13"  value="<%= buyStockCode%>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
	库地区: 
		<%= wareAreaLable%>
		<input type="submit"  value="  查 询   " />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		</td></tr>
		</table>
		</form>
	</div>
	<table align="center" width="43%">
		<tr><td align="center">
			<button onclick="javascript:exportTarget();">导出明细单</button>
			</td>
			<td align="center">
			<button onclick="javascript:printTarget();">打印明细单</button>
			
		</td></tr>
	</table>
	<table align="center" width="95%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px" >
		<tbody>
		<tr bgcolor="#95CACA" >
			<td align="center">
			序号
			</td>
			<td align="center">
			产品编号
			</td>
			<td align="center">
			原名称
			</td>
			<td align="center">
			数量
			</td>
			<td align="center">
			到货时间
			</td>
			<td align="center">
			调拨时间
			</td>
			<td align="center">
			地区
			</td>
			<td align="center">
			调拨单号
			</td>
			<td align="center">
			采购入库单号
			</td>
			<td align="center">
			预计到货单号
			</td>
			<td align="center">
			状态
			</td>
		</tr>
		<form action="" method="post" id="form1">
		<% if( list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++ ) {
			
				CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) list.get(i);
		%>
		
		<tr bgcolor="#FFFFB9" >
			<td align="center">
			<span><input type="checkbox" name="exportIds" value="<%= csub.getId()%>"/><%= (paging.getCurrentPageIndex() * paging.getCountPerPage()) + i + 1%></span>
			</td>
			<td align="center">
			<%= csub.getProduct().getCode()%>
			</td>
			<td align="center">
			<%= csub.getProduct().getOriname()%>
			</td>
			<td align="center">
			<%= csub.getCount()%>
			</td>
			<td align="center">
			<%=StringUtil.convertNull(StringUtil.cutString(csub.getStockinDatetime(), 19))%>
			</td>
			<td align="center">
			<%=StringUtil.convertNull(StringUtil.cutString(csub.getExchangeDatetime(), 19))%>
			</td>
			<td align="center">
			<%= csub.getAreaName()%>
			</td>
			<td align="center">
			<%= csub.getExchangeCode()%>
			</td>
			<td align="center">
			<%= csub.getBuyStorageCode()%>
			</td>
			<td align="center">
			<%= csub.getBuyStockCode()%>
			</td>
			<td align="center">
			<%= csub.getStatusName()%>
			</td>
		</tr>
		
		<%
				}
			} else { 
		%>
		<tr bgcolor="#FFFF93" >
			<td align="center" colspan="11">
				没有不合格记录或没有符合查询条件的记录
			</td>
		</tr>
		<%
			}
		%>
		</form>
		</tbody>
	</table>

	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</div>
</body>
</html>
