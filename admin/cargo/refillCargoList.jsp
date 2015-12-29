<%@page import="adultadmin.bean.cargo.CargoOperationBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%

	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	
	List list = (List)request.getAttribute("list");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	String operCode = StringUtil.convertNull(request.getParameter("operCode"));
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
	String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
	String[] statuss = request.getParameterValues("status");
	String para=request.getAttribute("para").toString();
	String status = "";
	if(statuss!=null){
		for(int i=0;i<statuss.length;i++){
			status = status + statuss[i]+",";
		}
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="adultadmin.bean.cargo.CargoOperationProcessBean"%><html>
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
	</head>
	<body>
		<script type="text/javascript">
		function checkboxChecked(checkbox,value){
			values = value.split(",");
			for(var j = 0; j < values.length; j++){
				for(var i = 0; i < checkbox.length; i++){
					if(checkbox[i].value == values[j]){
						checkbox[i].checked = true;
					}
				}
			}
		}
		</script>
		<%@include file="../../header.jsp"%>
		<form name="searchAppForm" action="cargoOperation.do" method="post" onSubmit="">
			<table width="850" cellpadding="3" cellspacing="1">
				<tr>
					<td align="left" colspan="1">
						补货作业单
					</td>
				</tr>
				<tr>
					<td colspan="1">
					<fieldset style="width:900px;"><legend>查询栏</legend>
						作业单编号：<input type="text" size="20" name="operCode" value="<%=operCode%>"/>精确&nbsp;&nbsp;
						作业单状态：
						<input type="checkbox" name="status" value="19">未处理&nbsp;
   						<input type="checkbox" name="status" value="20">提交并确认 &nbsp;
   						<input type="checkbox" name="status" value="21">交接阶段&nbsp;
   						<input type="checkbox" name="status" value="25">作业结束&nbsp;
   						<br/>
   						<script>checkboxChecked(document.getElementsByName('status'),'<%=status%>');</script>
   						产品编号：<input type="text" size="10" name="productCode" value="<%=productCode%>"/>精确&nbsp;&nbsp;
   						货位号：<input type="text" size="10" name="cargoCode" value="<%=cargoCode%>"/>精确&nbsp;&nbsp;
   						<input type="submit" value="查询"/>&nbsp;&nbsp;
						<a href="cargoOperation.do?method=addRefillCargoList">添加补货单</a>
						</fieldset>
					</td>
				</tr>
			</table>
			<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="95%" >
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
		<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>s>
			<td align="center"><%=i+1 %></td>
			<td align="center"><a href="cargoOperation.do?method=refillCargo&id=<%=cargoOperation.getId() %>"><%=cargoOperation.getCode() %></a></td>
			<td align="center"><%=cargoOperation.getStorageCode() %></td>
			<td align="center"><%=cargoOperation.getCreateDatetime().substring(0,16) %></td>
			<td align="center"><%=cargoOperation.getCreateUserName() %></td>
			<td align="center"><%=cargoOperation.getAuditingDatetime()==null?"":cargoOperation.getAuditingDatetime().subSequence(0,16) %></td>
			<td align="center"><%=StringUtil.convertNull(cargoOperation.getAuditingUserName()) %></td>
			<td align="center"><%=cargoOperation.getCompleteDatetime()==null?"":cargoOperation.getCompleteDatetime().subSequence(0,16) %></td>
			<td align="center"><%=StringUtil.convertNull(cargoOperation.getCompleteUsername()) %></td>
			<td align="left"><%=cargoOperation.getStatusName() %></td>
			<td align="left">
				<%if((cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS19 
						|| cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS20
						|| cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS21
						|| cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS22
						|| cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS23
						|| cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS24)
						&&cargoOperation.getCreateUserId()==user.getId()){%>
				<a href="cargoOperation.do?method=refillCargo&id=<%=cargoOperation.getId() %>">编辑</a>|
				<%}else{ %>
				<a href="cargoOperation.do?method=refillCargo&id=<%=cargoOperation.getId() %>">查看</a>|
				<%} %>
				<%if(cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS19&&cargoOperation.getCreateUserId()==user.getId()){%>
				<a href="cargoOperation.do?method=deleteRefillCargo&id=<%=cargoOperation.getId() %><%=para %>" onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')">删除</a>|<%} %>
				<a href="../admin/cargoOperation.do?method=printRefillCargo&id=<%=cargoOperation.getId() %>" target="_blank">打印作业单<%if(cargoOperation.getPrintCount()>0){ %>(<%=cargoOperation.getPrintCount() %>)<%} %></a>
			</td>
		</tr>
		<%
				}
			}	
		%>
		<tr>
			<td align="center" colspan="11"><%if(paging!=null){ %><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%><%}%></td>
		</tr>
		</table>
		<input type="hidden" name="method" value="refillCargoList"/>
		</form>
		<%@include file="../../footer.jsp"%>
	</body>
</html>