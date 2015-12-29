<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean ,adultadmin.bean.cargo.CargoOperationBean,adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.bean.cargo.CargoInfoBean" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String operationCode = StringUtil.convertNull(request.getParameter("operationCode"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));   
String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode"));
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
    <base href="<%=basePath%>">
    <title>下架作业单</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
 	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
 	<%
 		List list = (List)request.getAttribute("operationList");
 		PagingBean paging = (PagingBean) request.getAttribute("paging");
 		if(paging==null) paging = new PagingBean(0,0,0);
 	 %>
 	 <script type="text/javascript">
 	 	function checkboxChecked(checkbox,value){
				var values = value.split(",");
				for(var j = 0; j < values.length; j++){
					for(var i = 0; i < checkbox.length; i++){
						if(checkbox[i].value == values[j]){
							checkbox[i].checked = true;
						}
					}
				}
			}	
 	 </script>
  </head>
  <body>
  	<form action="<%=basePath%>admin/cargoDownShelf.do?method=shelfDownList" method="post"> 
  	产品下架作业单<br/>
  	<fieldset style="width:850px;"><legend>查询栏</legend>
    作业单编号:<input type="text" size="15" name="operationCode" id="operationCode" value="<%=operationCode%>"/>精确
	作业单状态:<input type="checkBox" name="status" id="status" value="10"/>未处理 &nbsp; 
			 <input type="checkBox" name="status" id="status" value="11"/>提交并确认 &nbsp;
			 <input type="checkBox" name="status" id="status" value="12"/>交接阶段 &nbsp;
			 <input type="checkBox" name="status" id="status" value="16"/>作业结束 &nbsp;
			 <br/>
			 <script>checkboxChecked(document.getElementsByName('status'),'<%=status%>') </script>
	产品编号&nbsp;:&nbsp;&nbsp;<input type="text" size="15" name="productCode" id="productId" value="<%=productCode%>"/>精确&nbsp;&nbsp;
	货位号:<input type="text" size="15" name="cargoCode" value="<%=cargoCode%>"/>精确&nbsp;&nbsp;<br/>
	<input type="submit" value="查询"/> &nbsp;&nbsp;&nbsp;
	<a href="<%=basePath%>admin/cargoDownShelf.do?method=cargoDownShelfList">添加新的作业单</a>		  
  	</fieldset>	
	</form>	 	
												
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="95%" >
		<tr bgcolor="#4688D6">
			<td align="center" height="25px"><font color="#FFFFFF">序号</font></td>
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
		<%if(list!=null && list.size()>0){ 
			for(int i=0;i<list.size();i++){
				CargoOperationBean bean = (CargoOperationBean)list.get(i);
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center" height="25px"> <%=(i + 1)+(paging.getCurrentPageIndex()*paging.getCountPerPage())%></td>
				<td align="center"><a href="<%=basePath%>admin/cargoDownShelf.do?method=showDownShel&id=<%=bean.getId()%>"><%=bean.getCode()%></a></td>
				<td align="center"><%=bean.getStorageCode()%></td>
				<td align="center"><%=StringUtil.convertNull(StringUtil.cutString(bean.getCreateDatetime(),16))%></td>
				<td align="center"><%=bean.getCreateUserName()%></td>
				<td align="center"><%=StringUtil.convertNull(StringUtil.cutString(bean.getAuditingDatetime(),16))%></td>
				<td align="center"><%=StringUtil.convertNull(bean.getAuditingUserName())%></td>
				<td align="center"><%=StringUtil.convertNull(StringUtil.cutString(bean.getCompleteDatetime(),16)) %></td>
				<td align="center"><%=StringUtil.convertNull(bean.getCompleteUsername())%></td>
				<td align="left"><%=bean.getStatusName()%></td>
				<td align="left" >
					<a href="<%=basePath%>admin/cargoDownShelf.do?method=showDownShel&id=<%=bean.getId()%>">编辑</a>&nbsp;
					<%if(bean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS10){ %>
					<a href="<%=basePath%>admin/cargoDownShelf.do?method=delShelfDown&id=<%=bean.getId()%><%=para %>&pageIndex=<%=request.getParameter("pageIndex")%>" onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')">删除</a>&nbsp;
					<%} %>
					|<a href="admin/cargoOperation.do?method=printDownShelfCargo&id=<%=bean.getId() %>" target="_blank">打印作业单<%if(bean.getPrintCount()>0){ %>(<%=bean.getPrintCount() %>)<%} %></a>
				</td>
			<!-- <%
					//List cargoInfoList = bean.getCargoInfoList();
				//	if(cargoInfoList!=null && cargoInfoList.size()>0){
					//	StringBuilder bs = new StringBuilder();
					//	for(int j=0;j<cargoInfoList.size();j++){
					//		CargoInfoBean cargoBean =(CargoInfoBean)cargoInfoList.get(j);
					//		bs.append(cargoBean.getWholeCode());
					//		if(j!=cargoInfoList.size()-1){
					//			bs.append("<br />");
					//		}
					//	}
					//	%>
					//	<td align="center"></td>
					//	<%
				//	}else{
				//		%><td align="center">&nbsp;</td><%
				//	}		
			//	%> -->	
			</tr>
		<% }
		} %>
	</table>
	<%if(paging!=null){ %>
		<center><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></center>
	<%} %>
  </body>
</html>
<script type="text/javascript">
document.getElementById('operationCode').focus();
</script>