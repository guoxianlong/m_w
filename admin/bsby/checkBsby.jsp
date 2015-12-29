<%@page contentType="text/html;charset=utf-8"%><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*, adultadmin.util.*" %>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="../taglibs.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>运营批量审核</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
	<script type="text/javascript">
   function checkFileType(theform){ 
		if(theform.file.value=="") 
		{
			alert("请点击浏览按钮，选择您要上传的xls或xlsx文件!");
			return (false);    
		}
		if(theform.content.value==""||theform.content.value=="请填写运营审核意见"){
			alert("请填写运营审核意见!");
			return (false);    
		}
		return (true);
	}
	
	</script>
	</head>
    <body>
		<center>
				<strong>批量快速审核报损报益</strong>
				
	</center>
	<form name="uploadfile" method="post" action="<%=request.getContextPath()%>/admin/checkbsby.do" enctype="multipart/form-data" onsubmit="return checkFileType(this)">
	<table>
	<tr>
	
	<td><input id="file" name="file" type="file" ></td>
	<td><input id="content" name="content" value="请填写运营审核意见" onclick="this.value=''"></td>
	<td><input type="submit" name="Submit" value="审核提交"></td>
	</tr>
	</table>
	</form>
		<table  cellpadding="1" cellspacing="0" rules="all">
		<tr>
	    <font color=ff0000 size="3"><c:out value='${msg}'/></font>
		
		</tr>
				<tr>
					        <td>单据号</td>
							<td>产品编号</td>
							<td>报损数量</td>
							<td>报溢数量</td>
							<td>货位号</td>
							<td>错误原因</td>
						
					</tr>
					
	 				<tbody>
                        <c:forEach items="${errorlist}" var="checkBsbyInfo">
						<tr class="griditem">
							<td nowrap align="center" class="padleft" id="bsbyCode"><c:out value='${checkBsbyInfo.bsbyCode}'/></td>
							<td nowrap align="center" class="padleft" id="productCode"><c:out value='${checkBsbyInfo.productCode}'/></td>
							<td nowrap align="center" class="padleft" id="bsCount"><c:out value='${checkBsbyInfo.bsCount}'/></td>
							<td nowrap align="center" class="padleft" id="byCount"><c:out value='${checkBsbyInfo.byCount}'/></td>
							<td nowrap align="center" class="padleft" id="cargoCode"><c:out value='${checkBsbyInfo.cargoCode}'/></td>
							<td nowrap align="center" class="padleft" id="remark"><c:out value='${checkBsbyInfo.remark}'/></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
	</body>
</html>
