<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="java.util.Iterator"%>
<html>
<head>
<title>作业单操作页-添加补货单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css"/>
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
<style type="text/css">
	th{
		color="#FFFFFF";
	}
</style>
</head>
<body>
<div style="margin-left: 30px;"> 
	a作业单操作页 <br/>
	作业单编号：HW1104024604   作业单状态：<font color="red">未处理</font>  人员操作记录 <br/>
	作业单类型：补货 （整件区—&gt;散件区） <br/>
</div>
<form method=post action="../searchProductHasStock.do?forward=Exchange&exchangeId="
		 target=sp onsubmit="document.all.d1.style.display='block';return true;">
	<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
		<thead >
			<tr bgcolor="#4688D6">
				<th>序号</th> <th>产品原名</th> <th>产品编号</th> <th>当前货位号</th>
				<th>当前库存</th> <th>空间冻结 </th> <th>摊位警戒线</th>
				<th>摊位最大容量</th> <th>整件区货位号以及补货量</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		 <c:forEach items="${items}" var="item" varStatus="vs">
			 	<tr>
			 		<td>${vs.index+1}</td>  
			 		<td>${item["product_name"]}</td> 	
			 		<td>${item["product_id"]}</td>
			 		<td>${item["product_oriname"]}</td>	 
			 		<td>${item["stock_count"]}</td>
			 		<td>${item["stock_lock_count"]}</td>  
			 		<td>${item["warn_stock_count"]}</td>
			 		<td>${item["max_stock_count"]}</td> 	 
			 		<td>	
						<table style="border: 2px;">
				 		<tr> <td>批次号</td><td>	货位号</td><td>本次补货量</td><td>该货位库存</td> </tr>
				 		<%
				 			Map map = (Map)pageContext.findAttribute("item");
				 			int productId = Integer.valueOf(map.get("product_id")+"").intValue();
				 			
				 		 %>
				 		</table>
					 </td>
					 <td><a href="#">删除</a> </td>  
		 		</tr>
			 </c:forEach>
					
					 
		 
		</tbody>
	</table>
	<div style="margin-left: 30px;">
		<input type="button" onclick="" value="保存编辑"/> 
		<input type="button" onclick="" value="确认提交"/> 
		<br/>
		注：1、如果要保存编辑的信息，请单击‘保存编辑’。如果想提交审核，请单击‘确认提交’。
	</div>
 </form>	
</body>
</html>