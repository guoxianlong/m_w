<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<title>作业单操作页-添加补货单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css"/>
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
</head>
<body>
<form method=post action="../searchProductHasStock.do?forward=Exchange&exchangeId="
		 target=sp onsubmit="document.all.d1.style.display='block';return true;">
	<table>
		<thead>
			<tr>
				<th>序号</th> <th>货位号</th> <th>产品编号</th> <th>产品原名</th> 
				<th>当前库存</th> <th>空间冻结 </th> <th>摊位警戒线</th>
				<th>摊位最大容量</th> <th> 整件区货位号以及补货量 <th>备注</th>
			</tr>
		</thead>
		<tbody>
			
		</tbody>
	</table>
	<a href="">选中货位生成补货单</a>
 </form>	
</body>
</html>