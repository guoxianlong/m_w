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
	int type = StringUtil.StringToId(request.getParameter("type"));
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
			document.noAssignForm.addAction.value='all';
			document.noAssignForm.submit();
		}
		function search(){
			document.noAssignForm.method.value='assignCargoReinventoryMissionPage';
			document.noAssignForm.submit();
		}
		</script>
			<input type="hidden" name="method" value="addCargoInventory"/>
			<input type="hidden" name="action" value="add"/>
			<fieldset style="width:800px;">
				<form action="cargoInventory.do" method="post" name="noAssignForm">
				<input type="hidden" name="method" id="method" value="assignCargoReinventoryMission"/>
				<input type="hidden" name="id" value="<%=inventory.getId() %>"/>
				<input type="hidden" name="addAction" id="addAction" value=""/>
				<b>复盘任务添加页</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="cargoInventory.do?method=cargoInventory&id=<%=inventory.getId() %>">返回作业单详细页</a><br/><br/>
				<b>作业单编号：</b><%=inventory.getCode() %> &nbsp;&nbsp;<br/><br/>
				货位范围：<input type="checkbox" name="type" value="1"<%if(type==1){ %> checked="checked"<%} %>/>仅显示初盘与库存有差异的货位&nbsp;<input type="button" value="查询" onClick="search()"/><br/><br/>
				<font color="red">提示：请选择货位后，单击添加按钮，生成复盘任务。凡是已添加至复盘任务的货位，不再显示在下面列表中。</font><br/><br/>
				<table width="80%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<td class="tdtitle"><input type="checkbox" onclick="reserveCheck('cargoId')"/></td>
					<td class="tdtitle">序号</td>
					<td class="tdtitle">货位号</td>
					<td class="tdtitle">产品编号</td>
					<td class="tdtitle">产品原名称</td>
					<td class="tdtitle">盘点前库存</td>
					<%
						int stage = 0;
						if(inventory.getStatus() >= CargoInventoryBean.STATUS4){ 
							stage = inventory.getStage();
						}else{
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
				</tr>
				<%
					Iterator iter = map.entrySet().iterator();
					int i = 1;
					while(iter.hasNext()){
						Map.Entry entry = (Map.Entry)iter.next();
						String cargoCode = (String)entry.getKey();
						List list = (List)entry.getValue();
						CargoInventoryMissionBean oriMission = (CargoInventoryMissionBean)list.get(0);
						CargoInventoryMissionProductBean oriMissionProduct = new CargoInventoryMissionProductBean();
						if(oriMission.getCargoInventoryMissionProductList() != null && oriMission.getCargoInventoryMissionProductList().size()>0){
							oriMissionProduct = (CargoInventoryMissionProductBean)oriMission.getCargoInventoryMissionProductList().get(0);
						}
				%>
				<tr>
					<td align="center"><input type="checkbox" name="cargoId" value="<%=oriMission.getCargoId() %>"/></td>
					<td align="center"><%=i %></td>
					<td align="center"><%=cargoCode %></td>
					<td align="center"><%=oriMissionProduct.getId()>0?oriMissionProduct.getProductCode():"无" %></td>
					<td align="center"><%=oriMissionProduct.getId()>0?oriMissionProduct.getProductOriname():"无" %></td>
					<td align="center"><%=oriMissionProduct.getStockCount() %></td>
					<%
						for(int j=1;j<list.size();j++){
							CargoInventoryMissionBean mission = (CargoInventoryMissionBean)list.get(j);
							CargoInventoryMissionProductBean missionProduct = new CargoInventoryMissionProductBean();
							if(mission.getCargoInventoryMissionProductList() != null && mission.getCargoInventoryMissionProductList().size()>0){
								for(int k=0;k<mission.getCargoInventoryMissionProductList().size();k++){
									missionProduct = (CargoInventoryMissionProductBean)mission.getCargoInventoryMissionProductList().get(k);
									if(missionProduct.getProductId() != oriMissionProduct.getProductId()){
										missionProduct = new CargoInventoryMissionProductBean();
									}else{
										break;
									}
								}
							}
					%>
					<td align="center"><%=mission.getStatus()==7?"-":""+missionProduct.getStockCount() %></td>										
					<%} %>
					<%
						for(int j=1;j<list.size();j++){
							CargoInventoryMissionBean mission = (CargoInventoryMissionBean)list.get(j);
							CargoInventoryMissionProductBean missionProduct = new CargoInventoryMissionProductBean();
							if(mission.getCargoInventoryMissionProductList() != null && mission.getCargoInventoryMissionProductList().size()>0){
								for(int k=0;k<mission.getCargoInventoryMissionProductList().size();k++){
									missionProduct = (CargoInventoryMissionProductBean)mission.getCargoInventoryMissionProductList().get(k);
									if(missionProduct.getProductId() != oriMissionProduct.getProductId()){
										missionProduct = new CargoInventoryMissionProductBean();
									}else{
										break;
									}
								}
							}
					%>
					<td align="center"><%=mission.getStatus()==7?"-":""+(missionProduct.getStockCount()-oriMissionProduct.getStockCount()) %></td>										
					<%} %>
				</tr>
				<%
					i++;
					}
				%>
				</table>
				<br/>
				<input type="submit" value="选中货位添加至复盘任务"/>&nbsp;&nbsp;<input type="button" value="全部添加" onClick="addAll();"/>
				<br/>
				</form>
			</fieldset>
		</form>
		<%@include file="../../footer.jsp"%>
	</body>
</html>