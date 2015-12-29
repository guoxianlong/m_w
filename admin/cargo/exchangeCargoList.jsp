<%@page import="adultadmin.bean.cargo.*"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%
	List list = (List)request.getAttribute("list");
	voUser user=(voUser)request.getAttribute("user");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	String para="";
	if(request.getAttribute("para")!=null){
		para=request.getAttribute("para").toString();
	}
	UserGroupBean group = user.getGroup();
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
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/admin/js/supplierUtil.js"></script>
		<link href="<%=request.getContextPath()%>/css/global.css"
			rel="stylesheet" type="text/css">
		<style type="text/css">
		.tdtitle {
			text-align: center;
			color: #ffffff;
		}
		</style>
		<script type="text/javascript">
			<%if(request.getAttribute("delete")!=null){%>
				alert("删除成功！");
			<%}%>
		</script>
	</head>
	<body>
		<%@include file="../../header.jsp"%>
		<form name="searchAppForm" action="../admin/cargoOperation.do?method=exchangeCargoList" method="post" onSubmit="">
			<table width="850" cellpadding="3" cellspacing="1">
				<tr>
					<td align="left" colspan="1">
						货位间调拨作业单
					</td>
				</tr>
				<tr>
					<td colspan="1">
					<fieldset style="width:900px;"><legend>查询栏</legend>
						作业单编号：<input type="text" size="20" name="operCode" <%if(request.getParameter("operCode")!=null){ %>value="<%=request.getParameter("operCode")%>"<%} %>/>精确&nbsp;&nbsp;
						作业单状态：
						<input type="checkbox" name="status" value="28">未处理&nbsp;
   						<input type="checkbox" name="status" value="29">提交并确认&nbsp;
   						<input type="checkbox" name="status" value="30">交接阶段&nbsp;
   						<input type="checkbox" name="status" value="34">作业结束&nbsp;
   						<input type="checkbox" name="status" value="35">作业成功&nbsp;
   						<input type="checkbox" name="status" value="36">作业失败&nbsp;
   						<br/>
   						产品编号：<input type="text" size="10" name="productCode" <%if(request.getParameter("productCode")!=null){ %>value="<%=request.getParameter("productCode")%>"<%} %>/>精确&nbsp;&nbsp;
   						货位号：<input type="text" size="10" name="cargoCode" <%if(request.getParameter("cargoCode")!=null){ %>value="<%=request.getParameter("cargoCode")%>"<%} %>/>精确&nbsp;&nbsp;
   						<input type="submit" value="查询"/>&nbsp;&nbsp;
						<a href="cargoOperation.do?method=addExchangeCargoList">添加新的作业单</a>&nbsp;&nbsp;&nbsp;
						<%if(group.isFlag(544)){ %>
							<a href="cargoOperation.do?method=exchangeOperationAudit" target="_blank">货位调拨审核扫描</a>&nbsp;&nbsp;&nbsp;
						<%} %>
						<%if(group.isFlag(545)){ %>
							<a href="cargoOperation.do?method=exchangeOperationConfirm" target="_blank">货位调拨确认扫描</a>&nbsp;&nbsp;&nbsp;
						<%} %>
						<a href="<%=request.getContextPath()%>/admin/cargo/printExchangeCargoList.jsp" target="_blank">批量打印货位调拨单</a>
						</fieldset>
					</td>
				</tr>
			</table>
			<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="95%">
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">作业单编号</font></td>
			<td align="center"><font color="#FFFFFF">作业库</font></td>
			<td align="center"><font color="#FFFFFF">制单时间</font></td>
			<td align="center"><font color="#FFFFFF">制单人</font></td>
			<td align="center"><font color="#FFFFFF">审核时间</font></td>
			<td align="center"><font color="#FFFFFF">审核人</font></td>
			<td align="center"><font color="#FFFFFF">确认完成时间</font></td>
			<td align="center"><font color="#FFFFFF">操作人</font></td>
			<td align="center"><font color="#FFFFFF">作业单状态</font></td>
			<td align="center"><font color="#FFFFFF">操作</font></td>
		</tr>
		<%
			if(list!=null){
				for(int i=0;i<list.size();i++){
					CargoOperationBean cargoOperation = (CargoOperationBean)list.get(i);
		%>
		<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td align="center"><%=i+1 %></td>
			<td align="center">
				<a href="../admin/cargoOperation.do?method=exchangeCargo&cargoOperId=<%=cargoOperation.getId() %>"><%=cargoOperation.getCode() %></a>
			</td>
			<td align="center"><%=cargoOperation.getStorageCode() %></td>
			<td align="center"><%=cargoOperation.getCreateDatetime().substring(0,19) %></td>
			<td align="center"><%=cargoOperation.getCreateUserName() %></td>
			<td align="center"><%=cargoOperation.getAuditingDatetime()==null?"无":cargoOperation.getAuditingDatetime().substring(0,19)%></td>
			<td align="center"><%=cargoOperation.getAuditingUserName().equals("")?"无":cargoOperation.getAuditingUserName() %></td>
			<td align="center"><%=cargoOperation.getCompleteDatetime()==null?"无":cargoOperation.getCompleteDatetime().substring(0,19) %></td>
			<td align="center"><%=cargoOperation.getCompleteUsername().equals("")?"无":cargoOperation.getCompleteUsername() %></td>
			<td align="left"><%=cargoOperation.getStatusName() %></td>
			<td align="left">
				<a href="../admin/cargoOperation.do?method=exchangeCargo&cargoOperId=<%=cargoOperation.getId() %>"><%if(cargoOperation.getStatus()==0||cargoOperation.getStatus()==1){ %>编辑<%}else{ %>查看<%} %></a>
				<%if(user.getId()==cargoOperation.getCreateUserId()&&(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28)){ %>
					|<a onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')" href="../admin/cargoOperation.do?method=deleteExchangeCargo&cargoOperId=<%=cargoOperation.getId() %><%=para %>">删除</a>
				<%} %>
				|<a href="../admin/cargoOperation.do?method=printExchangeCargo&id=<%=cargoOperation.getId() %>" target="_blank">打印作业单<%if(cargoOperation.getPrintCount()>0){ %>(<%=cargoOperation.getPrintCount() %>)<%} %></a>
			</td>
		</tr>
		<%
				}
			}	
		%>
		</table>
		</form>
		<%if(paging!=null){ %>
			<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 6)%></p>
		<%} %>
		<%@include file="../../footer.jsp"%>
		<script type="text/javascript">
		<%String[] status=request.getParameterValues("status");%>
		<%if(status!=null){%>
			<%for(int i=0;i<status.length;i++){%>
				for(var j=0;j<document.getElementsByName("status").length;j++){
					if(document.getElementsByName("status")[j].value==<%=status[i]%>){
						document.getElementsByName("status")[j].checked=true;
					}
				}
			<%}%>
		<%}%>
	</script>
	</body>
</html>