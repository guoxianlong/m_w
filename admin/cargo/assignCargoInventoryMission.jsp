<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<%
	CargoInventoryBean inventory = (CargoInventoryBean)request.getAttribute("inventory");
	List staffList = (List)request.getAttribute("staffList");
	List noAssignMissionList = (List)request.getAttribute("noAssignMissionList");
	LinkedHashMap assignMissionMap = (LinkedHashMap)request.getAttribute("assignMissionMap");
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
		function deleteNoAsign(){
			document.noAssignForm.method.value='deleteCargoInventoryMission';
			if(confirm('确认删除？')){
				document.noAssignForm.submit();
			}
		}
		
		function deleteAsign(){
			if(confirm('确认删除？')){
				return true;
			}
			
			return false;
		}

		function init(){
			var radio = document.getElementsByName('staffId');
 			for(var i = 0; i < radio.length; i++){
 				if(radio[i].checked){
 					colorChangeRadio(radio[i],'red');
 				}
 			}
		}
		function changeShelfColor(shelfId){
			var shelfs = document.getElementsByName('shelfId');
			for(var i = 0; i < shelfs.length; i++){
				if(shelfs[i].value==shelfId){
					var shelfTds = document.getElementById('td1-'+shelfId);
					if(shelfs[i].checked){
						shelfTds.style.color = "red";
					}else{
						shelfTds.style.color = "black";
					}
					shelfTds = document.getElementById('td2-'+shelfId);
					if(shelfs[i].checked){
						shelfTds.style.color = "red";
					}else{
						shelfTds.style.color = "black";
					}
				}
			}
		}
		function changeAssignStaffColor(assignStaffId){
			var assignStaffIds = document.getElementsByName('assignStaffId');
			for(var i = 0; i < assignStaffIds.length; i++){
				if(assignStaffIds[i].value==assignStaffId){
					for(var j=1;j<=6;j++){
						var assignStaffTds = document.getElementById('td'+j+'-'+assignStaffId);
						if(assignStaffIds[i].checked){
							assignStaffTds.style.color = "red";
						}else{
							assignStaffTds.style.color = "black";
						}
					}
				}
			}
		}
		</script>
			<input type="hidden" name="method" value="addCargoInventory"/>
			<input type="hidden" name="action" value="add"/>
			<fieldset style="width:800px;">
				<b>作业单任务分配页</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="cargoInventory.do?method=cargoInventory&id=<%=inventory.getId() %>">返回作业单详细页</a><br/><br/>
				<b>作业单编号：</b><%=inventory.getCode() %> &nbsp;&nbsp;(<%=inventory.getStageName() %>：共<%=inventory.getStockAreaCount() %>个区域、<%=inventory.getShelfCount() %>排货架、<%=inventory.getCargoCount() %>个货位)<br/><br/>
				<input type="radio" name="type" value="1" checked="checked"/>人工手动分配&nbsp;&nbsp;<br/><br/>
				<form action="cargoInventory.do" method="post" name="noAssignForm">
				<input type="hidden" name="method" id="method" value="assignCargoInventoryMission"/>
				<input type="hidden" name="id" value="<%=inventory.getId() %>"/>
				<input type="hidden" name="missionStatus" value="0"/>
				1)未分配任务表：<br/><br/>
				请先选定一个人员：
				<%
					for(int i=0;i<staffList.size();i++){
						CargoStaffBean staff = (CargoStaffBean)staffList.get(i);
				%>
				<input type="radio" name="staffId" value="<%=staff.getId() %>"<%if(i==0){ %> checked="checked"<%} %> onClick="colorChangeRadio(this,'red');"/><%if(assignMissionMap.get(String.valueOf(staff.getId()))!=null){ %><span><font color="blue"><%=staff.getName() %>(<%=staff.getCode() %>)</font></span><%}else{ %><span><%=staff.getName() %>(<%=staff.getCode() %>)</span><%} %>
				&nbsp;
				<%} %><br/><br/>
				<table width="80%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<%int maxColumn = noAssignMissionList.size()>4?4:noAssignMissionList.size(); %>
					<%for(int i=0;i<maxColumn;i++){ %>
					<td class="tdtitle"><%if(i==0){ %><input type="checkbox" onclick="reserveCheck('shelfId')"/><%} %></td>
					<td class="tdtitle">货架号</td>
					<td class="tdtitle">货位数</td>
					<%} %>
				</tr>	
				<%	
					for(int i=0;i<noAssignMissionList.size();i++){
						HashMap map = (HashMap)noAssignMissionList.get(i);
						String shelfWholeCode = (String)map.get("shelfWholeCode");
						if(shelfWholeCode.startsWith("GZF")){
							shelfWholeCode = shelfWholeCode.substring(0,9);
						}else{
							shelfWholeCode = shelfWholeCode.substring(0,9)+"-"+shelfWholeCode.substring(9,11);
						}
				%>
				<%if(i%4==0){ %>
				<tr>
				<%} %>
					<td align="center"><input type="checkbox" name="shelfId" value="<%=map.get("shelfId") %>" onClick="changeShelfColor(<%=map.get("shelfId") %>)"/></td>
					<td align="center" id="td1-<%=map.get("shelfId") %>"><%=shelfWholeCode %></td>
					<td align="center" id="td2-<%=map.get("shelfId") %>"><%=map.get("cargoCount") %></td>
				<%if(i%4==3||i==noAssignMissionList.size()-1){ %>
				</tr>
				<%} %>
				<%} %>
				</table>
				<br/>
				<input type="submit" value="添加至已分配列表"/>&nbsp;&nbsp;<input type="button" value="从未分配列表中删除" onClick="deleteNoAsign();"/>
				</form>
				<br/><br/>
				<form action="cargoInventory.do" method="post" onSubmit="return deleteAsign();">
				<input type="hidden" name="method" value="deleteCargoInventoryMission"/>
				<input type="hidden" name="id" value="<%=inventory.getId() %>"/>
				<input type="hidden" name="missionStatus" value="1"/>
				2)已分配任务表：<br/>
				<table width="80%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<td class="tdtitle"><input type="checkbox" onclick="reserveCheck('assignStaffId')"/></td>
					<td class="tdtitle">序号</td>
					<td class="tdtitle">工号</td>
					<td class="tdtitle">姓名</td>
					<td class="tdtitle">负责货架</td>
					<td class="tdtitle">货架数</td>
					<td class="tdtitle">货位数</td>
				</tr>
				<%
					Iterator assignIter = assignMissionMap.entrySet().iterator();
					int i=0;
					while(assignIter.hasNext()){
						i++;
						Map.Entry entry = (Map.Entry)assignIter.next();
						HashMap map = (HashMap)entry.getValue();
						String shelfWholeCode = (String)map.get("shelfWholeCode");
						if(shelfWholeCode.startsWith("GZF")){
							shelfWholeCode = shelfWholeCode.substring(0,9);
						}else{
							shelfWholeCode = shelfWholeCode.substring(0,9)+"-"+shelfWholeCode.substring(9,11);
						}
				%>
				<tr>
					<td align="center"><input type="checkbox" name="assignStaffId" value="<%=map.get("staffId") %>" onClick="changeAssignStaffColor(<%=map.get("staffId") %>)"/></td>
					<td align="center" id="td1-<%=map.get("staffId") %>"><%=i+1 %></td>
					<td align="center" id="td2-<%=map.get("staffId") %>"><%=map.get("staffCode") %></td>
					<td align="center" id="td3-<%=map.get("staffId") %>"><%=map.get("staffName") %></td>
					<td align="center" id="td4-<%=map.get("staffId") %>"><%=shelfWholeCode %></td>
					<td align="center" id="td5-<%=map.get("staffId") %>"><%=map.get("shelfCount") %></td>
					<td align="center" id="td6-<%=map.get("staffId") %>"><%=map.get("cargoCount") %></td>
				</tr>
				<%} %>
				</table>
				<br/>
				<input type="submit" value="从已分配列表删除"/>
				</form>
			</fieldset>
		</form>
		<script type="text/javascript">init();</script>
		<%@include file="../../footer.jsp"%>
	</body>
</html>