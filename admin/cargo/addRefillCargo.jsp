<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<%
	List list = (List) request.getAttribute("list");
	List sellCountList = (List) request.getAttribute("sellCountList");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
	String stockCount1 = StringUtil.convertNull(request.getParameter("stockCount1"));
	String stockCount2 = StringUtil.convertNull(request.getParameter("stockCount2"));
	int type = StringUtil.StringToId(request.getParameter("type"));
	String productOriName=StringUtil.convertNull(request.getParameter("productOriName"));
	if(Encoder.decrypt(productOriName)!=null){
		productOriName=Encoder.decrypt(productOriName);
	}
	List productLineList=(List)request.getAttribute("productLineList");
	List storageList=(List)request.getAttribute("storageList");
	int productLineId=StringUtil.StringToId(request.getParameter("productLineId"));
	String cargoType=StringUtil.convertNull(request.getParameter("cargoType"));
	String orderType=StringUtil.convertNull(request.getParameter("orderType"));
	if(orderType.equals("")){
		orderType="1";
	}
	String dayCount=StringUtil.convertNull(request.getParameter("dayCount"));
	if(dayCount.equals("")){
		dayCount="1";
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
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
		    var checkChagen =document.getElementsByName(name);
		    var cargoProducId = document.getElementsByName('cargoProducStockId');
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
		    alert("请选择货位，再生成补货单");
		    return false;
		}
 	</script>
		<%@include file="../../header.jsp"%>
		
		<form action="cargoOperation.do" method="post" name="searchAppForm">
			<input type="hidden" name="method" value="addRefillCargoList"/>
			<table width="95%" cellpadding="3" cellspacing="1">
				<tr>
					<td align="left" colspan="1">
						添加补货单
					</td>
				</tr>
				<tr>
					<td colspan="1">
					<fieldset style="width:600px;"><legend>查询栏</legend>
						货位号：<input type="text" size="15" name="cargoCode" value="<%=cargoCode%>"/>左精确右模糊&nbsp;&nbsp;
						<%if(storageList!=null){ %>
							
						仓库名称：<select id="storageId" name="storageId">
							<%for(int i=0;i<storageList.size();i++){ %>
								<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
								<option value="<%=storage.getId() %>"><%=storage.getWholeCode() %></option>
							<%} %>
						</select>
						<script type="text/javascript">selectOption(document.getElementById("storageId"),'<%=request.getParameter("storageId")%>');</script>
						<%} %>
						<br/>
						产品编号：<input type="text" size="10" name="productCode" value="<%=productCode%>"/>精确&nbsp;&nbsp;
						产品原名称：<input type="text" size="20" name="productOriName" value="<%=productOriName %>"/><br/>
						货位产品线：<select name="productLineId">
						<option value="">请选择</option>
						<%for(int i=0;i<productLineList.size();i++){ %>
						<%voProductLine productLine=(voProductLine)productLineList.get(i); %>
						<option value="<%=productLine.getId() %>" <%if(productLineId==productLine.getId()){ %>selected=selected<%} %>><%=productLine.getName() %></option>
						<%} %>
						</select>&nbsp;&nbsp;
						货位类型：<select name="cargoType">
						<option value="">请选择</option>
						<option value="0" <%if(cargoType.equals("0")){ %>selected=selected<%} %>>普通</option>
						<option value="1" <%if(cargoType.equals("1")){ %>selected=selected<%} %>>热销</option>
						<option value="2" <%if(cargoType.equals("2")){ %>selected=selected<%} %>>滞销</option>
						</select><br/>
						货位当前库存：<input type="text" size="2" name="stockCount1" value="<%=stockCount1%>"/>至<input type="text" size="2" name="stockCount2" value="<%=stockCount2%>"/>&nbsp;&nbsp;<br/>
						散件区有无货位：
						<select name="type" id="type">
							<option value="0">有</option>
							<option value="1">无</option>
						</select><br/>
						<script>selectOption(document.getElementById('type'), '<%=type%>');</script>
						查询排序：<input type="radio" name="orderType" value="1" <%if(orderType.equals("1")){ %>checked=checked<%} %>/>按缺货量排序（从高到低）&nbsp;&nbsp;&nbsp;
								 <input type="radio" name="orderType" value="2" <%if(orderType.equals("2")){ %>checked=checked<%} %>/>按销量排序（从高到低）&nbsp;&nbsp;&nbsp;
						前<select name="dayCount">
							<%for(int i=1;i<=50;i++){ %>
								<option value='<%=i %>' <%if(dayCount.equals(i+"")){ %>selected="selected"<%} %>><%=i %></option>
							<%} %>
						</select>天销量
						<input type="submit" value="查询"/>&nbsp;&nbsp;
					</fieldset>
					</td>
				</tr>
			</table>
		</form>
		<form action="cargoOperation.do" name="cargoForm" method="post" onSubmit="return check();">
			<input type="hidden" name="method" value="addRefillCargo"/>
			<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="98%" >
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">选</font><input type="checkBox" name="checkID" id="checkID" onclick="checkAll('checkID')"/></td>
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">产品一级分类</font></td>
			<td align="center"><font color="#FFFFFF">产品编号</font></td>
			<td align="center"><font color="#FFFFFF">产品原名称</font></td>
			<td align="center"><font color="#FFFFFF">散件区货位号</font></td>
			<td align="center"><font color="#FFFFFF">当前库存(其中冻结量)</font></td>
			<td align="center"><font color="#FFFFFF">整件区库存(其中冻结量)</font></td>
			<td align="center"><font color="#FFFFFF">货位警戒线</font></td>
			<td align="center"><font color="#FFFFFF">货位最大容量</font></td>
			<td align="center"><font color="#FFFFFF">前<%=dayCount %>天销量</font></td>
			<td align="center"><font color="#FFFFFF">货位产品线</font></td>
			<td align="center"><font color="#FFFFFF">货位类型</font></td>
			<td align="center"><font color="#FFFFFF">货位尺寸/cm</font></td>
			<td align="center"><font color="#FFFFFF">备注</font></td>
		</tr>
				<%
					if(list!=null){
						for(int i=0;i<list.size();i++){
							CargoProductStockBean cps = (CargoProductStockBean)list.get(i);
				%>
				<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
					<td>
						<input type="checkbox" id="cargoProducStockId" name="cargoProducStockId" value="<%=cps.getId()%>"/>
					</td>
					<td align="center">
						<%=i+1 %>
					</td>
					<td align="center">
						<%=cps.getId()>0?cps.getProduct().getParent1().getName():"" %>
					</td>
					<td align="center">
						<%=cps.getProduct().getCode() %>
					</td>
					<td align="center">
						<%=cps.getProduct().getOriname() %>
					</td>
					<td align="center">
						<a href="../admin/cargoInfo.do?method=updateCargoPage&cargoProductStockId=<%=cps.getId() %>&cargoId=<%=cps.getCargoInfo().getId()%>" target="_blank"><%=cps.getCargoInfo().getStoreType()==0?cps.getCargoInfo().getWholeCode():"无" %></a>
					</td>
					<td align="center">
						<%=cps.getCargoInfo().getStoreType()==0?String.valueOf(cps.getStockCount()+cps.getStockLockCount())+"("+String.valueOf(cps.getStockLockCount())+")":"-" %>
					</td>
					<td align="center">
						<%=cps.getProduct().getCargoStock(CargoInfoBean.STORE_TYPE1) + cps.getProduct().getCargoLockStock(CargoInfoBean.STORE_TYPE1) %>(<%=cps.getProduct().getCargoLockStock(CargoInfoBean.STORE_TYPE1) %>)
					</td>
					<td align="center">
						<%=cps.getCargoInfo().getStoreType()==0?String.valueOf(cps.getCargoInfo().getWarnStockCount()):"-" %>
					</td>
					<td align="center">
						<%=cps.getCargoInfo().getStoreType()==0?String.valueOf(cps.getCargoInfo().getMaxStockCount()):"-" %>
					</td>
					<td align="center">
						<%=sellCountList.get(i) %>
					</td>
					<td align="center">
						<%=cps.getCargoInfo().getStoreType()==0&&cps.getCargoInfo().getProductLine()!=null?cps.getCargoInfo().getProductLine().getName():"-" %>
					</td>
					<td align="center">
						<%=cps.getCargoInfo().getStoreType()==0?cps.getCargoInfo().getTypeName():"-" %>
					</td>
					<td align="center">
						<%if(cps.getCargoInfo().getStoreType()==0){%><%=cps.getCargoInfo().getLength() %>/<%=cps.getCargoInfo().getWidth() %>/<%=cps.getCargoInfo().getHigh() %><%}else{ %>-<%} %>
					</td>
					<td>
						<%=cps.getCargoInfo().getStoreType()==0?cps.getCargoInfo().getRemark():"-" %>
					</td>
				</tr>
				<%			
						}
				%>
					
				<%  } %>
				<tr>
					<td align="center" colspan="14"><%if(paging!=null){ %><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%><%}%></td>
				</tr>
				<tr>
					<td align="left" colspan="14"><input type="submit" value="对勾选货位生成补货单"/></td>
				</tr>
			</table>
		</form>
		注：缺货量=货位警戒线－货位当前可用库存；前1天销量=昨天发货量<br/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;货位当前可用库存=当前库存－冻结量
		<%@include file="../../footer.jsp"%>
	</body>
</html>