<%@page import="cache.CatalogCache"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<%
	CargoInventoryBean inventory = (CargoInventoryBean)request.getAttribute("inventory");
	LinkedHashMap map = (LinkedHashMap)request.getAttribute("map");
	HashMap bsbyMap = (HashMap)request.getAttribute("bsbyMap");
	int type = StringUtil.StringToId(request.getParameter("type"));
	String cargoWholeCode = StringUtil.convertNull(request.getParameter("cargoWholeCode"));
	String cargoShelfCode = StringUtil.convertNull(request.getParameter("cargoShelfCode"));
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
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
		<%@include file="../../header.jsp"%>
		<style type="text/css">
		.tdtitle {
			text-align: center;
			color: #ffffff;
		}
		</style>
		<script type="text/javascript">
		function addAll(){
			document.noAssignForm.method.value='cargoReinventoryToBsby';
			var cargos = document.getElementsByName("cargoId");
			for(var i = 0; i < cargos.length; i++){
				cargos[i].checked = true;
			}
			document.noAssignForm.submit();
		}
		function toBsby(){
			document.noAssignForm.method.value='cargoReinventoryToBsby';
			document.noAssignForm.submit();
		}
		</script>
			<fieldset style="width:800px;">
				<form action="cargoInventory.do" method="post" name="noAssignForm">
				<input type="hidden" name="method" id="method" value="cargoInventoryDifferent"/>
				<input type="hidden" name="id" value="<%=inventory.getId() %>"/>
				<b>库存调整单添加页</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="cargoInventory.do?method=cargoInventory&id=<%=inventory.getId() %>">返回作业单详细页</a><br/><br/>
				<b>作业单编号：</b>
				<%=inventory.getCode() %>&nbsp;&nbsp;
				<input type="hidden" name="code" value="<%=inventory.getCode() %>"/>
				货架号：<input type="text" name="cargoShelfCode" value="<%=cargoShelfCode %>" size="4"/>&nbsp;&nbsp;
				货位号：<input type="text" name="cargoWholeCode" value="<%=cargoWholeCode %>" size="10"/>&nbsp;&nbsp;
				产品编号：<input type="text" name="productCode" value="<%=productCode %>" size="10"/>&nbsp;&nbsp;<input type="submit" value="查询"/><br/><br/>
				<font color="red">提示：请选择货位后，单击添加按钮，生成库存调整单。凡是已添加至库存调整单的货位，不再显示在下面列表中。</font><br/><br/>
				<table width="90%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<td class="tdtitle"><input type="checkbox" onclick="reserveCheck('cargoId')"/></td>
					<td class="tdtitle">序号</td>
					<td class="tdtitle">货位号</td>
					<td class="tdtitle">产品一级分类</td>
					<td class="tdtitle">产品编号</td>
					<td class="tdtitle">产品原名称</td>
					<td class="tdtitle">盘点前库存</td>
					<%
						int stage = 0;
						if(inventory.getStatus() >= CargoInventoryBean.STATUS4){ 
							stage = inventory.getStage();
						}else if(inventory.getStatus() == CargoInventoryBean.STATUS1){
							stage = inventory.getStage()-1;
						}
						for(int i=0;i<stage;i++){
					%>
					<td class="tdtitle"><%=i==0?"初盘":"复盘"+i %></td>
					<%  
						}
						for(int i=0;i<stage;i++){
					%>
					<td class="tdtitle"><%=i==0?"初盘-库存":"复盘"+i+"-库存" %></td>
					<%  } %>
					<td class="tdtitle">库存调整</td>
				</tr>
				<%
					Iterator iter = map.entrySet().iterator();
					int i = 1;
					while(iter.hasNext()){
						Map.Entry entry = (Map.Entry)iter.next();
						String cargoCode = (String)entry.getKey();
						List list = (List)entry.getValue();
						CargoInventoryMissionBean oriMission = (CargoInventoryMissionBean)list.get(0);
						CargoInventoryMissionProductBean oriMissionProduct = (CargoInventoryMissionProductBean)oriMission.getCargoInventoryMissionProductList().get(0);
				%>
				<tr>
					<td align="center"><input type="checkbox" name="cargoId" value="<%=oriMission.getCargoId() %>"/></td>
					<td align="center"><%=i %></td>
					<td align="center"><%=cargoCode %></td>
					<td align="center"><%=CatalogCache.getCatalog(oriMissionProduct.getProductParentId1()).getName() %></td>
					<td align="center"><%=oriMissionProduct.getProductCode() %></td>
					<td align="center"><%=oriMissionProduct.getProductOriname() %></td>
					<td align="center"><%=oriMissionProduct.getStockCount() %></td>
					<%
						for(int j=1;j<list.size();j++){
							CargoInventoryMissionBean mission = (CargoInventoryMissionBean)list.get(j);
							CargoInventoryMissionProductBean missionProduct = new CargoInventoryMissionProductBean();
							if(mission.getCargoInventoryMissionProductList() != null && mission.getCargoInventoryMissionProductList().size()>0){
								missionProduct = (CargoInventoryMissionProductBean)mission.getCargoInventoryMissionProductList().get(0);
							}
					%>
					<td align="center"><%=mission.getStatus()==7?"-":""+missionProduct.getStockCount() %></td>										
					<%} %>
					<%
						int bsbyCount = 0;
						for(int j=1;j<list.size();j++){
							CargoInventoryMissionBean mission = (CargoInventoryMissionBean)list.get(j);
							CargoInventoryMissionProductBean missionProduct = new CargoInventoryMissionProductBean();
							if(mission.getCargoInventoryMissionProductList() != null && mission.getCargoInventoryMissionProductList().size()>0){
								missionProduct = (CargoInventoryMissionProductBean)mission.getCargoInventoryMissionProductList().get(0);
							}
							if(mission.getStatus()!=7){
								bsbyCount = missionProduct.getStockCount()-oriMissionProduct.getStockCount();
							}
					%>
					<td align="center"><%=mission.getStatus()==7?"-":""+(missionProduct.getStockCount()-oriMissionProduct.getStockCount()) %><%if(j+1 == list.size()){ %><input type="hidden" name="differentStockCount<%=oriMission.getCargoId() %>" value="<%=bsbyCount %>"/><%}	 %></td>
					<%} %>
					<td align="center"><%=StringUtil.convertNull((String)bsbyMap.get(cargoCode)).equals("")?"-":StringUtil.convertNull((String)bsbyMap.get(cargoCode)) %></td>
				</tr>
				<%
					i++;
					}
				%>
				</table>
				<br/>
				<input type="button" value="选中货位添加至报损、报溢单" onClick="toBsby();"/>&nbsp;&nbsp;<input type="button" value="全部添加" onClick="addAll();"/>
				<br/>
				</form>
			</fieldset>
		<%@include file="../../footer.jsp"%>
	</body>
</html>