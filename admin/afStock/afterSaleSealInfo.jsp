<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>封箱商品清单</title>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
</head>
<body>
	 <div id="infoDiv" style="overflow-y:auto; overflow-x:auto;">
			<table id="sealInfoTable" class="tableForm">
				 <tr align="center">
				 	<th colspan="3" align="center">封箱商品清单</th>
				 </tr>
				 <tr align="center">
				 	<td align="center">操作人：<label id="operator"></label></td>
				 	<td align="center">封箱日期：<label id="sealDate"></label></td>
				 	<td align="center">封箱编号：<label id="sealCode"></label></td>
				 </tr>
			  </table>
			 <h3>封箱商品明细</h3>
			<table id="sealInfoDataGrid"></table>
		</div>
</body>
</html>
