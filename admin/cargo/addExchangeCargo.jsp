<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean,mmb.stock.stat.*"%>
<%
	List list = (List) request.getAttribute("list");
	List canCheckList = (List) request.getAttribute("canCheckList");
	List operCountList = (List) request.getAttribute("operCountList");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	
	String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("area", "", request, -1, false,"");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="mmb.stock.cargo.CargoDeptAreaService"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%><html>
	<head>
		<title>买卖宝后台</title>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/js/jquery.js"></script>
		<link href="<%=request.getContextPath()%>/css/global.css"
			rel="stylesheet" type="text/css">
	</head>
	<body>
	<script type="text/javascript">
 		function checkAll(name) {     
		    var checkChagen =document.getElementsByName("checkID");
		    var cargoProducId = document.getElementsByName(name);
		    for(var i=0;i<cargoProducId.length;i++){
		    	cargoProducId[i].checked =checkChagen[0].checked ;
		    }
		}
 		
 		function check(){
			 var cargoProducId = document.getElementsByName('cargoProducStockId');
			 for(var i=0;i<cargoProducId.length;i++){
		    	if(cargoProducId[i].checked ==true){
		    		return true;
		    	}
		    }
		    alert("请选择货位，再生成调拨单！");
		    return false;
		}
 	</script>
		<%@include file="../../header.jsp"%>
		<form name="searchAppForm" action="../admin/cargoOperation.do?method=addExchangeCargoList" method="post" onSubmit="">
			<table width="95%" cellpadding="3" cellspacing="1">
				<tr>
					<td align="left" colspan="1">
						添加新的调拨作业单
					</td>
				</tr>
				<tr>
					<td colspan="1">
					<fieldset style="width:780px;"><legend>查询栏</legend>
						提示：请选择货位，再生成货位调拨单<br/>
						货位编号：<input type="text" size="15" name="cargoCode" <%if(request.getParameter("cargoCode")!=null){ %>value="<%=request.getParameter("cargoCode")%>"<%} %>/>左精确右模糊&nbsp;&nbsp;
						存放类型：<select name="storeType">
									<option value="">请选择</option>
									<option value="0" <%if(request.getParameter("storeType")!=null&&request.getParameter("storeType").equals("0")){ %>selected=selected<%} %>>散件区</option>
									<option value="1" <%if(request.getParameter("storeType")!=null&&request.getParameter("storeType").equals("1")){ %>selected=selected<%} %>>整件区</option>
									<option value="4" <%if(request.getParameter("storeType")!=null&&request.getParameter("storeType").equals("4")){ %>selected=selected<%} %>>混合区</option>
								</select>
						产品编号：<input type="text" size="10" name="productCode" <%if(request.getParameter("productCode")!=null){ %>value="<%=request.getParameter("productCode")%>"<%} %>/>精确&nbsp;&nbsp;<br/>
						当前存量：<input type="text" size="2" name="stockCount1" <%if(request.getParameter("stockCount1")!=null){ %>value="<%=request.getParameter("stockCount1")%>"<%} %>/>至<input type="text" size="2" name="stockCount2" <%if(request.getParameter("stockCount2")!=null){ %>value="<%=request.getParameter("stockCount2")%>"<%} %>/>&nbsp;&nbsp;
						库地区：<%= wareAreaLable%>
						<input type="submit" value="查询"/>&nbsp;&nbsp;
					</fieldset>
					</td>
				</tr>
			</table>
		</form>
		<form name="cargoForm" action="cargoOperation.do" method="post" onSubmit="return check();">
			<input type="hidden" name="method" value="addExchangeCargo"/>
			<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="95%" >
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">选</font><input type="checkBox" name="checkID" id="checkID" onclick="checkAll('cargoProductStockId');"/></td>
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center" width="110"><font color="#FFFFFF">货位编号</font></td>
			<td align="center"><font color="#FFFFFF">产品编号</font></td>
			<td align="center"><font color="#FFFFFF">产品原名称</font></td>
			<td align="center"><font color="#FFFFFF">当前库存（冻结）</font></td>
			<td align="center"><font color="#FFFFFF">货位最大容量</font></td>
			<td align="center"><font color="#FFFFFF">警戒线</font></td>
			<td align="center"><font color="#FFFFFF">货位类型</font></td>
			<td align="center"><font color="#FFFFFF">货位空间冻结</font></td>
			<td align="center"><font color="#FFFFFF">未完成作业单数</font></td>
		</tr>
				<%
					if(list!=null&&canCheckList!=null&&operCountList!=null){
						for(int i=0;i<list.size();i++){
							CargoProductStockBean cps = (CargoProductStockBean)list.get(i);
							String canCheck=canCheckList.get(i).toString();
				%>
				<tr align="left" <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
					<td>
						<%if(canCheck.equals("true")){ %>
						<input type="checkbox" id="cargoProductStockId" name="cargoProducStockId" value="<%=cps.getId()%>"/>
						<%}else{ %>
						<input type="checkbox" disabled="disabled"/>
						<%} %>
					</td>
					<td >
						<%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %>
					</td>
					<td >
						<a href="../admin/cargoInfo.do?method=updateCargoPage&cargoProductStockId=<%=cps.getId() %>&cargoId=<%=cps.getCargoInfo().getId()%>" target="_blank"><%=cps.getCargoInfo().getWholeCode() %></a>
					</td>
					<td >
						<a href="../admin/fproduct.do?id=<%=cps.getProductId() %>"><%=cps.getProduct().getCode() %></a>
					</td>
					<td >
						<a href="../admin/fproduct.do?id=<%=cps.getProductId() %>"><%=cps.getProduct().getOriname() %></a>
					</td>
					<td >
						<%=cps.getStockCount() %>（<%=cps.getStockLockCount() %>）
					</td>
					<td >
						<%=cps.getCargoInfo().getMaxStockCount() %>
					</td>
					<td >
						<%=cps.getCargoInfo().getWarnStockCount() %>
					</td>
					<td >
						<%=cps.getCargoInfo().getTypeName() %>
					</td>
					<td >
						<%=cps.getCargoInfo().getSpaceLockCount()%>
					</td>
					<td>
						<%=operCountList.get(i) %>
					</td>
				</tr>
				<%			
						}
				%>
					
				<%  } %>
				<tr>
					<td align="center" colspan="14"></td>
				</tr>
			</table>
			<input type="submit" value="对勾选货位生成货位调拨单"/>&nbsp;&nbsp;&nbsp;<%if(paging!=null){ %><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%><%}%>
		</form>
		<%@include file="../../footer.jsp"%>
	</body>
</html>