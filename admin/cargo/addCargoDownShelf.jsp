<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean ,adultadmin.bean.cargo.CargoDownShelfBean,adultadmin.util.*,mmb.stock.stat.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

List downShelfList = (List)request.getAttribute("downShelfList");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String message = (String)request.getAttribute("message");
String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));
String stockCountStart = StringUtil.convertNull(request.getParameter("stockCountStart"));
String stockCountEnd=StringUtil.convertNull(request.getParameter("stockCountEnd"));

String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("area", "", request, -1, false,"");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="mmb.stock.cargo.CargoDeptAreaService"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%><html>
  <head>
    <base href="<%=basePath%>">
    <title>添加下架作业单</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
 	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
 	<script type="text/javascript">
 	
 <% if(message!=null && !message.equals("")){
		request.setAttribute("message","");
		Integer operationId = (Integer)request.getAttribute("id");
%>
		alert("添加成功");
		 
		document.location.href="<%=basePath%>admin/cargoDownShelf.do?method=showDownShel&id="+<%=operationId%>;
		 
		<%}
	%>
 		function checkAll(name) {     
		    var checkChagen =document.getElementsByName(name);
		    var cargoProducId = document.getElementsByName('cargoProducId');
		    for(var i=0;i<cargoProducId.length;i++){
		    	cargoProducId[i].checked =checkChagen[0].checked ;
		    }
		}
		
		function checkForm(){
			 var cargoProducId = document.getElementsByName('cargoProducId');
			 for(var i=0;i<cargoProducId.length;i++){
		    	if(cargoProducId[i].checked ==true){
		    		return true;
		    	}
		    }
		    alert("请选择货位，再生成下架作业单");
		    return false;
		}
 	</script>
  </head>
  <body>
  	<form action="<%=basePath%>admin/cargoDownShelf.do?method=cargoDownShelfList" method="post"> 
	  	添加下架作业单<br/>
		<fieldset style="width:780px;"><legend>查询栏</legend>
	    货位编号:<input type="text" name="cargoCode" size="15" value="<%=cargoCode%>"/>左精确右模糊&nbsp;&nbsp;
		产品编号:<input type="text" name="productCode" size="15" value="<%=productCode%>"/>精确&nbsp;&nbsp;
		当前存量:<input type="text" name="stockCountStart" size="5" value="<%=stockCountStart%>"/>
		      至<input type="text" name="stockCountEnd" size="5" value="<%=stockCountEnd%>"/> &nbsp;&nbsp;&nbsp;
		 库地区：<%= wareAreaLable%>
		<input type="submit" value="查询"/> 
		</fieldset>
	</form>
	<form action="<%=basePath%>admin/cargoDownShelf.do?method=addDownShel" method="post"> 
		<input type="hidden" name="area" value="<%=request.getParameter("area")==null?"":request.getParameter("area") %>"/>	 	
		<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="95%">
			<tr bgcolor="#4688D6">
				<td align="center"><font color="#FFFFFF">选</font><input type="checkBox" name="checkID" id="checkID" onclick="checkAll('checkID')"/></td>
				<td align="center"><font color="#FFFFFF">序号</font></td>
				<td align="center"><font color="#FFFFFF">货位编号</font></td>
				<td align="center"><font color="#FFFFFF">产品编号</font></td>
				<td align="center"><font color="#FFFFFF">产品原名称</font></td>
				<td align="center"><font color="#FFFFFF">当前库存（冻结）</font></td>
				<td align="center"><font color="#FFFFFF">货位最大容量</font></td>
				<td align="center"><font color="#FFFFFF">警戒线</font></td>
				<td align="center"><font color="#FFFFFF">货位类型</font></td>
				<td align="center"><font color="#FFFFFF">货位尺寸/cm</font></td>
				<td align="center"><font color="#FFFFFF">备注</font></td>
			</tr>
			<%
			if(downShelfList!=null && downShelfList.size()>0){
				for(int i=0;i<downShelfList.size();i++){
					CargoDownShelfBean bean = (CargoDownShelfBean)downShelfList.get(i);
					%>
					<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
						<td align="center"><input type="checkbox" id="cargoProducId" name="cargoProducId" value="<%=bean.getId()%>"/></td>
						<td align="center"><%=(i + 1)+(paging.getCurrentPageIndex()*paging.getCountPerPage())%></td>
						<td align="center"><a href="<%=basePath%>admin/cargoInfo.do?method=updateCargoPage&cargoProductStockId=<%=bean.getId()%>&cargoId=<%=bean.getCargoId()%>" target="_blank"><%=bean.getCargoCode()%></a></td>
						<td align="center"><%=bean.getProductCode()%></td>
						<td align="center"><%=bean.getProductName()%></td>
						<td align="center"><%=(bean.getStockCount()+bean.getStockCountLock())%>(<%=bean.getStockCountLock()%>)</td>
						<td align="center"><%=bean.getMaxStockCount()%></td>
						<td align="center"><%=bean.getWarStockCount()%></td>
						<td align="center"><%=bean.getCargoTypeName(bean.getCargoType())%></td>
						<td align="center"><%=bean.getVolume()%></td>
						<td align="center"><%=bean.getCargoMark()%></td>
					</tr>	
					<%
				}
			}else{
				%>
					<tr>
						<td colspan="12" align="center">暂无数据</td>
					</tr>
				<%
			}
			 %>
		</table>
		<div>
			<div style="float:left width: 200px"><input type="submit" value="选中货位生成下架作业单" onclick="return checkForm();"/></div>
			 
			<%if(paging!=null){ %>
			<div align='center'><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></div>
			<%} %>
		</div>
	</form>
  </body>
</html>
