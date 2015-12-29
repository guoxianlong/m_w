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
	int type = StringUtil.StringToId(request.getParameter("type"));
	String cargoWholeCode = StringUtil.convertNull(request.getParameter("cargoWholeCode"));
	String cargoShelfCode = StringUtil.convertNull(request.getParameter("cargoShelfCode"));
	
	response.setContentType("application/vnd.ms-excel;charset=gb2312");
	String fileName = "盘点导出-"+inventory.getCode()+"-"+DateUtil.getNowDateStr();
	response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GBK"), "iso8859-1") + ".xls\"");
%>
	<body>
				<table width="90%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr>
					<td class="tdtitle">序号</td>
					<td class="tdtitle">货位号</td>
					<td class="tdtitle">产品一级分类</td>
					<td class="tdtitle">产品编号</td>
					<td class="tdtitle">产品原名称</td>
					<td class="tdtitle">盘点前库存</td>
					<%
						int stage = 0;
						if(inventory.getStatus() == CargoInventoryBean.STATUS4){ 
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
					<td align="center"><%=missionProduct.getStockCount() %></td>										
					<%} %>
					<%
						for(int j=1;j<list.size();j++){
							CargoInventoryMissionBean mission = (CargoInventoryMissionBean)list.get(j);
							CargoInventoryMissionProductBean missionProduct = new CargoInventoryMissionProductBean();
							if(mission.getCargoInventoryMissionProductList() != null && mission.getCargoInventoryMissionProductList().size()>0){
								missionProduct = (CargoInventoryMissionProductBean)mission.getCargoInventoryMissionProductList().get(0);
							}
					%>
					<td align="center"><%=missionProduct.getStockCount()-oriMissionProduct.getStockCount() %></td>										
					<%} %>
				</tr>
				<%
					i++;
					}
				%>
				</table>
	</body>
