<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperationCargoBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperationBean"%><html>
<head>
<title>批量确认提交退货上架单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
	</object> 
</head>
<body>
<h3>批量确认提交退货上架单</h3>
<form action="<%=request.getContextPath() %>/admin/cargoOperation.do?method=allConfirmCargoOperation" method="post">
	<textarea name="data" rows="10" cols="40"></textarea>
	<input type="submit" value="批量确认提交退货上架单"/>
</form>
<%if(request.getAttribute("msg")!=null){ %>
	<font color="red"><%=request.getAttribute("msg") %></font>
<%} %>
<%if(request.getAttribute("errMsg")!=null){ %>
	<font color="red"><%=request.getAttribute("errMsg") %></font>
<%} %>
</body>
</html>