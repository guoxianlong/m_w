<%@page import="adultadmin.bean.bybs.BsbyOperationnoteBean"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<%
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();

	CargoInventoryBean inventory = (CargoInventoryBean)request.getAttribute("inventory");
	LinkedHashMap missionMap = (LinkedHashMap)request.getAttribute("missionMap");
	LinkedHashMap missionMap2 = (LinkedHashMap)request.getAttribute("missionMap2");
	List bsbyList = (List)request.getAttribute("bsbyList");
	HashMap bsbyMap = (HashMap)request.getAttribute("bsbyMap");
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
		<form action="cargoInventory.do" method="post" name="searchAppForm">
			<input type="hidden" name="method" value="cargoInventoryCollect"/>
			<fieldset style="width:800px;">
				<b>作业单详细页</b><br/><br/>
				<b>作业单编号：</b><%=inventory.getCode() %> &nbsp;&nbsp;(<%=ProductStockBean.getStockTypeName(inventory.getStockType())%><%=inventory.getTypeName() %>)<br/><br/>
				<b>作业仓库：</b><%=inventory.getStorageCode()==null?"无":inventory.getStorageCode() %> &nbsp;(<%=inventory.getStockAreaCode()==null?"无":inventory.getStockAreaCode() %>)&nbsp;&nbsp;&nbsp;<b>货位数：</b>共<%=inventory.getShelfCount() %>排货架、<%=inventory.getCargoCount() %>个货位<br/><br/>
				<b>状态：</b><%=inventory.getStatusName() %>&nbsp;&nbsp;&nbsp;
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS1){ %><a href="cargoInventory.do?method=assignCargoInventoryMissionPage&id=<%=inventory.getId() %>">分配任务</a><%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS2){ %><a href="cargoInventory.do?method=startCargoInventory&id=<%=inventory.getId() %>" onClick="javascript:return confirm('确认启动<%=inventory.getStage()==1?"初盘":"复盘"+(inventory.getStage()-1)%>任务？')">启动任务</a><%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS3){ %><a href="cargoInventory.do?method=endCargoInventory&id=<%=inventory.getId() %>" onClick="javascript:return confirm('是否确定强制结束任务？')">强制结束</a><%} %>
				<br/><br/>
				<%if(inventory.getStatus()>2){ %>
				<b>初盘时间：</b><%=StringUtil.convertNull(inventory.getStartDatetime()).equals("")?"":StringUtil.convertNull(inventory.getStartDatetime()).substring(0,19) %>&nbsp;至&nbsp;<%=StringUtil.convertNull(inventory.getEndDatetime()).equals("")?"":StringUtil.convertNull(inventory.getEndDatetime()).substring(0,19) %>
				<br/><br/>
				<%} %>
				<b>备注：</b><%=StringUtil.toHtml(inventory.getRemark()) %><br/><br/>
				<%
					Iterator mapIter = missionMap.entrySet().iterator();
					while(mapIter.hasNext()){
						Map.Entry entry = (Map.Entry)mapIter.next();
						int stage = StringUtil.StringToId((String)entry.getKey());
						HashMap missionCountMap = (HashMap)missionMap2.get(String.valueOf(stage));
				%>
				>><b><%=stage==1?"初盘":"复盘"+(stage-1) %></b>分配任务表：
				<%if(stage < inventory.getStage() || (stage == inventory.getStage() && inventory.getStatus()>=CargoInventoryBean.STATUS4)){ %>
				<a href="cargoInventory.do?method=cargoInventoryCollect&code=<%=inventory.getCode() %>" target="_blank">盘点表</a>
				<%} %>
				<br/>
				<table width="80%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<td class="tdtitle">序号</td>
					<td class="tdtitle">工号</td>
					<td class="tdtitle">姓名</td>
					<td class="tdtitle">货架号</td>
					<td class="tdtitle">货位数</td>
					<%if(inventory.getStatus()!=CargoInventoryBean.STATUS0&&inventory.getStatus()!=CargoInventoryBean.STATUS1){ %>
					<td class="tdtitle">未完成</td>
					<td class="tdtitle">任务启动时间</td>
					<td class="tdtitle">任务结束时间</td>
					<%} %>
				</tr>
				<%
					
					List missionList1 = (List)entry.getValue();
					for(int i=0;i<missionList1.size();i++){
						HashMap map = (HashMap)missionList1.get(i);
				%>
				<tr>
					<td align="center"><%=i+1 %></td>
					<td align="center"><%=map.get("staffCode") %></td>
					<td align="center"><%=map.get("staffName") %></td>
					<td align="center"><%=map.get("shelfCode") %></td>
					<td align="center"><%=map.get("cargoCount") %></td>
					<%if(inventory.getStatus()!=CargoInventoryBean.STATUS0&&inventory.getStatus()!=CargoInventoryBean.STATUS1){ %>
					<td align="center"><%=map.get("unCompleteCount") %></td>
					<td align="center"><%=StringUtil.convertNull((String)map.get("startDatetime")).equals("")?"":StringUtil.convertNull((String)map.get("startDatetime")).substring(0,19) %></td>
					<td align="center"><%=StringUtil.convertNull((String)map.get("endDatetime")).equals("")?"":StringUtil.convertNull((String)map.get("endDatetime")).substring(0,19) %></td>
					<%} %>
				</tr>
				<%} %>
				</table>
				<br/>
				<%if(stage == 1){ %>
				<%if(stage == inventory.getStage()){ %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS0 || inventory.getStatus() == CargoInventoryBean.STATUS1){ %>
				<font color="red">
					提示：已分配<%=StringUtil.convertNull((String)missionCountMap.get("assignCargoCount")) %>个货位，未分配<%=StringUtil.convertNull((String)missionCountMap.get("noAssignCargoCount")) %>个货位，请单击<a href="cargoInventory.do?method=assignCargoInventoryMissionPage&id=<%=inventory.getId() %>">分配任务</a>。未分配货位个数为0才能开始盘点作业。
				</font>
				<%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS2){ %>
				<font color="red">
					提示：已分配<%=StringUtil.convertNull((String)missionCountMap.get("assignCargoCount")) %>个货位，未分配<%=StringUtil.convertNull((String)missionCountMap.get("noAssignCargoCount")) %>个货位，请单击<a href="cargoInventory.do?method=startCargoInventory&id=<%=inventory.getId() %>" onClick="javascript:return confirm('确认启动初盘任务？')">启动</a>开始执行任务。若重新分配，请单击<a href="cargoInventory.do?method=assignCargoInventoryMissionPage&id=<%=inventory.getId() %>">分配任务</a>。
				</font>
				<%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS3){ %>
				<font color="red">
					提示：已完成<%=StringUtil.convertNull((String)missionCountMap.get("completeCargoCount")) %>个货位，剩余<%=StringUtil.convertNull((String)missionCountMap.get("unCompleteCargoCount")) %>个货位。若强制终止任务，请单击<a href="cargoInventory.do?method=endCargoInventory&id=<%=inventory.getId() %>" onClick="javascript:return confirm('是否确定强制结束任务？')">结束</a>。
				</font>
				<%} %>
				<%if(inventory.getStatus() >= CargoInventoryBean.STATUS4){ %>
				<font color="red">
					提示：任务已结束，共<%=StringUtil.convertNull((String)missionCountMap.get("cargoCount")) %>个货位，有<%=StringUtil.convertNull((String)missionCountMap.get("differentCargoCount")) %>个货位其盘点结果与库存有差异。请单击<a href="cargoInventory.do?method=assignCargoReinventoryMissionPage&type=1&id=<%=inventory.getId() %>">生成复盘任务</a>。
				</font>
				<%} %>
				<%}else{ %>
					注：任务已结束，共<%=StringUtil.convertNull((String)missionCountMap.get("cargoCount")) %>个货位，有<%=StringUtil.convertNull((String)missionCountMap.get("differentCargoCount")) %>个货位其盘点结果与库存有差异。
				<%} %>
				<%}else{ %>
				<%if(stage == inventory.getStage()){ %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS0 || inventory.getStatus() == CargoInventoryBean.STATUS1){ %>
				<font color="red">
					提示：复盘货位共<%=StringUtil.convertNull((String)missionCountMap.get("cargoCount")) %>个，已分配<%=StringUtil.convertNull((String)missionCountMap.get("assignCargoCount")) %>个，未分配<%=StringUtil.convertNull((String)missionCountMap.get("noAssignCargoCount")) %>个货位，请单击<a href="cargoInventory.do?method=assignCargoInventoryMissionPage&id=<%=inventory.getId() %>">分配任务</a>。
					若继续添加货位，请单击<a href="cargoInventory.do?method=assignCargoReinventoryMissionPage&type=1&id=<%=inventory.getId() %>">生成复盘任务</a>。
				</font>
				<%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS2){ %>
				<font color="red">
					提示：已分配<%=StringUtil.convertNull((String)missionCountMap.get("assignCargoCount")) %>个货位，未分配<%=StringUtil.convertNull((String)missionCountMap.get("noAssignCargoCount")) %>个货位，请单击<a href="cargoInventory.do?method=startCargoInventory&id=<%=inventory.getId() %>" onClick="javascript:return confirm('确认启动复盘<%=stage-1%>任务？')">启动</a>开始执行任务。若重新分配，请单击<a href="cargoInventory.do?method=assignCargoInventoryMissionPage&id=<%=inventory.getId() %>">分配任务</a>。
				</font>
				<%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS3){ %>
				<font color="red">
					提示：已完成<%=StringUtil.convertNull((String)missionCountMap.get("completeCargoCount")) %>个货位，剩余<%=StringUtil.convertNull((String)missionCountMap.get("unCompleteCargoCount")) %>个货位。若强制终止任务，请单击<a href="cargoInventory.do?method=endCargoInventory&id=<%=inventory.getId() %>" onClick="javascript:return confirm('是否确定强制结束任务？')">结束</a>。
				</font>
				<%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS4){ %>
				<font color="red">
					提示：任务已结束，共<%=StringUtil.convertNull((String)missionCountMap.get("cargoCount")) %>个货位，有<%=StringUtil.convertNull((String)missionCountMap.get("differentCargoCount")) %>个货位其盘点结果与库存有差异。
					请单击<a href="cargoInventory.do?method=assignCargoReinventoryMissionPage&type=1&id=<%=inventory.getId() %>">生成复盘任务</a>或<a href="cargoInventory.do?method=cargoInventoryDifferent&code=<%=inventory.getCode() %>">生成库存调整单</a>。
				</font>
				<%} %>
				<%if(inventory.getStatus() > CargoInventoryBean.STATUS4){ %>
					注：任务已结束，共<%=StringUtil.convertNull((String)missionCountMap.get("cargoCount")) %>个货位，有<%=StringUtil.convertNull((String)missionCountMap.get("differentCargoCount")) %>个货位其盘点结果与库存有差异。
				<%} %>
				<%}else{ %>
				注：任务已结束，共<%=StringUtil.convertNull((String)missionCountMap.get("cargoCount")) %>个货位，有<%=StringUtil.convertNull((String)missionCountMap.get("differentCargoCount")) %>个货位其盘点结果与库存有差异。
				<%} %>
				<%} %>
				<br/><br/>
				<%} %>
				<%if(inventory.getStatus() == CargoInventoryBean.STATUS5){ %>
				>><b>库存调整：</b>
				<br/>
				<table width="80%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<td class="tdtitle">序号</td>
					<td class="tdtitle">报损、报溢单编号</td>
					<td class="tdtitle">状态</td>
					<td class="tdtitle">操作</td>
					<td class="tdtitle">货位数</td>
					<td class="tdtitle">库存调整量</td>
				</tr>
				<%
					boolean addBsby = false;
					if(bsbyList!=null){
						for(int i=0;i<bsbyList.size();i++){
							BsbyOperationnoteBean bean = (BsbyOperationnoteBean)bsbyList.get(i);
							HashMap map = (HashMap)bsbyMap.get(String.valueOf(bean.getId()));
							if(bean.getCurrent_type() == 0 || bean.getCurrent_type() == 2 || bean.getCurrent_type() == 5){
								addBsby = true;
							}
				%>
				<tr>
					<td align="center"><%=i+1 %></td>
					<td align="center">
					<%int type = bean.getCurrent_type();
					  int userid = user.getId();
						if((type==0||type==2||type==5)&&(userid==bean.getOperator_id()||group.isFlag(229))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=bean.getId() %>" target="_blank"><%=bean.getReceipts_number() %></a>
						<%}else if((type==1||type==6)&&group.isFlag(229)){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=bean.getId() %>" target="_blank"><%=bean.getReceipts_number() %></a>
						<%}else if(type==3||type==4){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=bean.getId() %>" target="_blank"><%=bean.getReceipts_number() %></a> 
						<%}else if(!group.isFlag(229)){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=bean.getId() %>" target="_blank"><%=bean.getReceipts_number() %></a>
						<%}%>
					</td>
					<td align="center"><%=BsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type()))%></td>
					<td align="center">
					<%
							//没有完成以前都可以编辑和删除单据，如果提交审核后，只有有权限的人才能修改单据内的具体参数，当单据被审核打回时，添加人能修改单据内的具体参数同时能删除单据
							if((type==0||type==2||type==5)&&(userid==bean.getOperator_id()||group.isFlag(229))){%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=bean.getId() %>">编辑</a>
							<%}else if((type==1 || type==3||type==6)&&(userid==bean.getOperator_id()||group.isFlag(229))){%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=bean.getId() %>">审核</a>
							<%}else if(type==4){//已经完成的单据
							%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=bean.getId() %>" >查看</a> | <a href="bsby/bsbyPrint.jsp?opid=<%=bean.getId() %>&opcode=<%=bean.getReceipts_number() %>" > 打印</a> | <a href="bsby/printRecord.jsp?opid=<%=bean.getId() %>" target="_blank">打印<%=bean.getPrint_sum() %>次</a>
							<%}else if(!group.isFlag(229)){%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=bean.getId() %>" >查看</a>
							<%} %>
					</td>
					<td align="center"><%=map.get("countCargo") %></td>
					<td align="center"><%=map.get("sumBsbyCount") %></td>
				</tr>
				<%	
						}
					}
					if(bsbyList == null || bsbyList.size() == 0){
						addBsby = true;
					}
				%>
				</table>
				<br/>
				<%if(addBsby){ %>
				<font color="red">
					提示：请单击<a href="cargoInventory.do?method=cargoInventoryDifferent&code=<%=inventory.getCode() %>">生成库存调整单</a>，继续添加。
				</font>
				<%} %>
				<%} %>
			</fieldset>
		</form>
		<%@include file="../../footer.jsp"%>
	</body>
</html>