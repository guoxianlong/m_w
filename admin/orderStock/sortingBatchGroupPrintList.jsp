<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%
List staffList = (List)request.getAttribute("staffList");
String code=request.getParameter("code");
%>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<head>
<title>分拣波次补打页</title>
</head>
<script type="text/javascript">
	function inputCode(sortingBatchGroupId,printType,selectedOrder){
		  var code = window.prompt("员工号:","");
		  if(code==""){
		 	 inputCode(sortingBatchGroupId,printType,selectedOrder);
		  }else if(code){
		  	openPrintPage(code,sortingBatchGroupId,printType,selectedOrder);
		  }
	}
	function openPrintPage(userCode,sortingBatchGroupId,printType,selectedOrder){
		 if( window.confirm("是否继续操作")){
			 window.location.href = 'sortingAction.do?method=sortingBatchGroupPrintLine&userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType='+printType+'&selectedOrder='+selectedOrder+'&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex"))%>';
			 return true;	 
		 }else{
			 return false;
		 }
	}
</script>
<body>
	<form method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchGroupPrintList">
	<div align="center">
	<input type="text" name="code" size="55" <%if(code!=null&&code.length()>0) {%>value="<%=code %>"<%}else{%>value="员工号/分拣波次号/订单号"/<%} %> onfocus="if(this.value=='员工号/分拣波次号/订单号'){this.value=''}">
	&nbsp;&nbsp;<input type='submit' value='查询'/>
	</div><br>
	<table  width="99%" border="0" cellpadding="3" cellspacing="1" bgcolor="#4c6e92" align="center">
		 <tr bgcolor="Yellow">
			<td><div align="center"><strong>员工号</strong></div></td>
			<td><div align="center"><strong>姓名</strong></div></td>
			<td><div align="center"><strong>分拣波次号</strong></div></td>
			<td><div align="center"><strong>领单时间</strong></div></td>
			<td><div align="center"><strong>分拣波次状态</strong></div></td>
			<td><div align="center"><strong>操作</strong></div></td>
		</tr>
		<%if(staffList!=null){
			for(int i=0;i<staffList.size();i++){
				SortingBatchGroupBean bean =(SortingBatchGroupBean)staffList.get(i);
			%>
		<tr bgcolor="#e8e8e8">
			<td><div align="center"><%=bean.getStaffCode() %></div></td>
			<td><div align="center"><%=bean.getStaffName() %></div></td>
			<td><div align="center"><%=bean.getCode() %></div></td>
			<td><div align="center"><%=StringUtil.convertNull(StringUtil.cutString(bean.getReceiveDatetime(),0,19))%></div></td>
			<td><div align="center"><%=bean.getStatusName()%></div></td>
			<td><div align="center"><input onClick="openPrintPage('',<%=bean.getId()%>,'buda','')" style="color:blue; font-size:14px" type="button" value="补打"></div></td>
		</tr>
		<%}}%>
	</table>
</form>
</body>
</html>