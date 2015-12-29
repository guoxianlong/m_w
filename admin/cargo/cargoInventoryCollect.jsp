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
	PagingBean pageBean = (PagingBean) request.getAttribute("pageBean");
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
			document.noAssignForm.addAction.value='all';
			document.noAssignForm.submit();
		}
		function export1(){
			document.noAssignForm.act.value='export';
			document.noAssignForm.submit();
			document.noAssignForm.act.value='';
		}
		</script>
			<fieldset style="width:800px;">
				<form action="cargoInventory.do" method="post" name="noAssignForm">
				<input type="hidden" name="method" id="method" value="cargoInventoryCollect"/>
				<input type="hidden" name="id" value="<%=inventory.getId() %>"/>
				<input type="hidden" name="act" id="act" value=""/>
				<b>盘点表</b><br/><br/>
				<b>作业单编号：</b>
				<input type="text" name="code" value="<%=inventory.getCode() %>" size="16"/>(必填项)&nbsp;&nbsp;
				货架号：<input type="text" name="cargoShelfCode" value="<%=cargoShelfCode %>" size="4"/>&nbsp;&nbsp;
				货位号：<input type="text" name="cargoWholeCode" value="<%=cargoWholeCode %>" size="10"/>&nbsp;&nbsp;
				产品编号：<input type="text" name="productCode" value="<%=productCode %>" size="10"/>&nbsp;&nbsp;
				<br/><br/>
				货位范围：<input type="radio" name="type" value="0" checked="checked"/>全部&nbsp;<input type="radio" name="type" value="1"/>仅显示盘点有差异的货位&nbsp;
				<input type="radio" name="type" value="2"/>仅显示盘点无差异的货位&nbsp;<input type="radio" name="type" value="3"/>仅显示有库存调整的货位&nbsp;
				<script type="text/javascript">selectRadio(document.getElementsByName('type'),<%=type%>);</script>
				<input type="submit" value="查询"/>&nbsp;&nbsp;<input type="button" value="导出" onClick="export1()"/><br/><br/>
				<table width="90%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
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
						CargoInventoryMissionProductBean oriMissionProduct = new CargoInventoryMissionProductBean();
						if(oriMission.getCargoInventoryMissionProductList() != null && oriMission.getCargoInventoryMissionProductList().size()>0){
							oriMissionProduct = (CargoInventoryMissionProductBean)oriMission.getCargoInventoryMissionProductList().get(0);
						}
				%>
				<tr>
					<td align="center"><%=i %></td>
					<td align="center"><%=cargoCode %></td>
					<td align="center"><%=oriMissionProduct.getId()>0?CatalogCache.getCatalog(oriMissionProduct.getProductParentId1()).getName():"无" %></td>
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
					<td align="center">
					<%if((missionProduct.getStockCount()-oriMissionProduct.getStockCount())!=0 && mission.getStatus()!=7){ %>
					<font color="red"><%=mission.getStatus()==7?"-":""+(missionProduct.getStockCount()-oriMissionProduct.getStockCount()) %></font></td>
					<%}else{ %>
					<%=mission.getStatus()==7?"-":""+(missionProduct.getStockCount()-oriMissionProduct.getStockCount()) %>
					<%} %>										
					<%} %>
					<td align="center"><%=StringUtil.convertNull((String)bsbyMap.get(cargoCode)).equals("")?"-":StringUtil.convertNull((String)bsbyMap.get(cargoCode)) %></td>
				</tr>
				<%
					i++;
					}
					if (pageBean != null && (pageBean.getTotalCount() > pageBean.getCountPerPage())) {
						%>
						<tr>
							<td colspan="12">
								<p align="center"><%=PageUtil.fenye(pageBean, true, "&nbsp;&nbsp;",
									"pageIndex", 10)%></p>
							</td>
						</tr>
						<%
						}
						%>
				</table>
				<br/>
				<br/>
				</form>
			</fieldset>
		<%@include file="../../footer.jsp"%>
	</body>
</html>