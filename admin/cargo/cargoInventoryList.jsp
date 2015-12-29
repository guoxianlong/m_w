<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="adultadmin.bean.cargo.CargoInventoryBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%!
	static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser) request.getSession().getAttribute("userView");
	List list = (List) request.getAttribute("list");
	PagingBean pageBean = (PagingBean) request.getAttribute("pageBean");
	UserGroupBean group = user.getGroup();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>买卖宝后台</title>
		<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
		<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
		<script language="JavaScript" src="<%=request.getContextPath()%>/js/pub.js"></script>
		<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierUtil.js"></script>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<style type="text/css">
		.tdtitle {
			text-align: center;
			color: #ffffff;
		}
		</style>
		<script type="text/javascript">
		function checkForm() {
			var sCreateDate = document.searchAppForm.sCreateDate.value;
			var eCreateDate = document.searchAppForm.eCreateDate.value;
			var sAppMoney = document.searchAppForm.sAppMoney.value;
			var eAppMoney = document.searchAppForm.eAppMoney.value;
			var sLastPayDate = document.searchAppForm.sLastPayDate.value;
			var eLastPayDate = document.searchAppForm.eLastPayDate.value;
			var error = "";
			if ((sCreateDate == '' && sCreateDate != '') 
				|| (sCreateDate != '' && sCreateDate == '')) {
				error += "申请日期段必须填写完整！\n";
			}
			if ((sAppMoney == '' && eAppMoney != '') 
				|| (sAppMoney != '' && eAppMoney == '')) {
				error += "申请金额段必须填写完整！\n";
			}
			if ((sLastPayDate == '' && eLastPayDate != '') 
				|| (sLastPayDate != '' && eLastPayDate == '')) {
				error += "最迟到账时间段必须填写完整！\n";
			}
			if (sCreateDate != '' && eCreateDate != '') {
				if(!checkDate(trim(sCreateDate)) || !checkDate(trim(eCreateDate))) {
					error += "申请日期段的日期格式(yyyy-mm-dd)不合法！\n";
				} else {
					if (sCreateDate > eCreateDate) {
						error += "申请日期段的起始日期不能大于结束日期！\n";
					} else {
						if (DateDiff(eCreateDate, sCreateDate)*1 > 31) {
							error += "申请日期的最大搜索范围不得超过31天！\n";
						}
					}
				}
			}
			if (sAppMoney != '' && eAppMoney != '') {
				if (isNaN(sAppMoney) || isNaN(eAppMoney)) {
					error += "申请金额段必须为数字！\n";
				} else {
					if (sAppMoney*1 > eAppMoney*1) {
						error += "申请金额的起始金额不能大于结束金额！\n";
					}
				}
			}
			if (sLastPayDate != '' && sLastPayDate != '') {
				if(!checkDate(trim(sLastPayDate)) || !checkDate(trim(sLastPayDate))) {
					error += "最迟到账时间段的日期格式(yyyy-mm-dd)不合法！\n";
				} else {
					if (sLastPayDate > eLastPayDate) {
						error += "最迟到账时间段的起始日期不能大于结束日期！\n";
					} else {
						if (DateDiff(eLastPayDate, sLastPayDate)*1 > 31) {
							error += "最迟到账时间的最大搜索范围不得超过31天！\n";
						}
					}
				}
			}
			if (error != "" && error.length > 0) {
				alert(error);
				return false;
			} else {
				return true;
			}
		}
		</script>
	</head>
	<body>
		<%@include file="../../header.jsp"%>
		<form name="searchAppForm" action="imprestFinance.do?method=ImprestApplicationList" method="post" onSubmit="return checkForm();">
			<p align="center">盘点作业单列表</p>
			<%--
			<fieldset>
				<legend>
						查询条件
				</legend>
				申请单编号：<input type="text" name="appCode" value="<%=appCode %>"  size="15" />&nbsp;&nbsp;
				<%if(isOnly) {%><input type="hidden" name="appUserId" value="<%=appUserId %>" /><%} %>
				申请人：<select <%if(isOnly) {%>name="appUser" disabled<%} else {%>name="appUserId"<%}%>>
					<option value="-1">全部</option>
					<%
					for(int i=0;appUserList!=null&&i<appUserList.size();i++) {
						SupplierUserBean suBean = (SupplierUserBean) appUserList.get(i);
						voUser vUser = suBean.getUser();
						if(vUser == null){
							continue;
						}
					%>
						<option value="<%=vUser.getId() %>" <%if(appUserId == vUser.getId()) {%> selected <%} %>><%=suBean.getName() %></option>
					<%
					}
					%>
				</select>&nbsp;&nbsp;
				申请日期：<input type="text" name="sCreateDate" value="<%=sCreateDate %>" onClick="SelectDate(this,'yyyy-MM-dd');" size="10" />至<input type="text" name="eCreateDate" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eCreateDate %>" size="10" />&nbsp;&nbsp;
				申请金额：<input type="text" name="sAppMoney" value="<%=sAppMoney %>" size="10" />至<input type="text" name="eAppMoney" value="<%=eAppMoney %>" size="10" /><br/>
				最迟到账时间：<input type="text" name="sLastPayDate" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sLastPayDate %>" size="10" />至<input type="text" name="eLastPayDate" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eLastPayDate %>" size="10" />&nbsp;&nbsp;
				银行简称：<select name="sbankId">
					<option value="-1">全部</option>
					<%
					for(int i=0;bankList!=null&&i<bankList.size();i++) {
						SupplierBankBean sbBean = (SupplierBankBean) bankList.get(i);
					%>
						<option value="<%=sbBean.getId() %>" <%if(sbankId == sbBean.getId()) {%> selected <%} %>><%=sbBean.getName() %></option>
					<%
					}
					%>
				</select>&nbsp;&nbsp;
				申请单状态：<select name="appStatus">
					<option value="-1">全部</option>
				<%
					HashMap statusMap = SupplierImprestApplicationBean.statusMap;
					Iterator statusKeyIter = statusMap.keySet().iterator();
					while(statusKeyIter.hasNext()) {
						Integer key = (Integer) statusKeyIter.next();
						if (key.intValue() != SupplierImprestApplicationBean.STATUS0) {
					 	%>
					  		<option value="<%=key.intValue()%>" <%if(status == key.intValue()) {%> selected <%} %>><%=statusMap.get(Integer.valueOf(key.intValue())) %></option>
					  	<%
						}
					}
				%>
				</select>&nbsp;&nbsp;申请单类型：<select name="appType">
					<option value="-1">全部</option>
				<%
					HashMap appTypeMap = SupplierImprestApplicationBean.appTypeMap;
					Iterator typeKeyIter = appTypeMap.keySet().iterator();
					while(typeKeyIter.hasNext()) {
						Integer key = (Integer) typeKeyIter.next();
					 	%>
					  		<option value="<%=key.intValue()%>" <%if(type == key.intValue()) {%> selected <%} %>><%=appTypeMap.get(Integer.valueOf(key.intValue())) %></option>
					  	<%
					}
				%>
				</select>&nbsp;
				<input type="submit" name="search" value="查询" />
			</fieldset>
			</form>
			 --%>
			<table width="99%" cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: 0 5px;" bordercolor="#000000">
				<tr bgcolor="#4688D6">
					<td class="tdtitle">序号</td>
					<td class="tdtitle">作业单编号</td>
					<td class="tdtitle">作业类型</td>
					<td class="tdtitle">仓库编号</td>
					<td class="tdtitle">库存类型</td>
					<td class="tdtitle">区域编号</td>
					<td class="tdtitle">货架/货位个数</td>
					<td class="tdtitle">人数</td>
					<td class="tdtitle">盘点时间</td>
					<td class="tdtitle">任务阶段</td>
					<td class="tdtitle">任务状态</td>
					<td class="tdtitle">操作</td>
				</tr>
				<%if(list!=null){ %>	
				<%
				for (int i=0;i<list.size();i++) {
					CargoInventoryBean inventory = (CargoInventoryBean)list.get(i);
				%>
				<tr>
					<td align="center"><%=i+1 %></td>
					<td align="center"><a href="cargoInventory.do?method=cargoInventory&id=<%=inventory.getId() %>"><%=inventory.getCode() %></a></td>
					<td align="center"><%=inventory.getTypeName() %></td>
					<td align="center"><%=inventory.getStorageCode()==null?"无":inventory.getStorageCode() %></td>
					<td align="center"><%=ProductStockBean.getStockTypeName(inventory.getStockType()) %></td>
					<td align="center"><%=inventory.getStockAreaCode()==null?"无":inventory.getStockAreaCode() %></td>
					<td align="center"><%=inventory.getShelfCount() %>/<%=inventory.getCargoCount() %></td>
					<td align="center"><%=inventory.getMemberCount() %></td>
					<td align="center">
						<%=StringUtil.convertNull(inventory.getStartDatetime()).equals("")?"":StringUtil.convertNull(inventory.getStartDatetime()).substring(0,19) %>
						至<%=StringUtil.convertNull(inventory.getEndDatetime()).equals("")?"":StringUtil.convertNull(inventory.getEndDatetime()).substring(0,19) %>
					</td>
					<td align="center"><%=inventory.getStageName() %></td>
					<td align="center"><%=inventory.getStatusName() %></td>
					<td align="center">
					<%if(inventory.getStatus() < CargoInventoryBean.STATUS3 && (user.getId() == inventory.getCreateAdminId() || group.isFlag(412)) && inventory.getStage() <= 1){ %>
					<a href="cargoInventory.do?method=deleteCargoInventory&id=<%=inventory.getId() %>">删除任务</a>
					<%} %>
					</td>
				</tr>
				<%
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
				<%} %>
			</table>
		<%@include file="../../footer.jsp"%>
	</body>
</html>