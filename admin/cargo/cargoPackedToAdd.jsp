<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@page import="java.util.Iterator"%>
<%
	String path = request.getContextPath();
	pageContext.setAttribute("path",path);
 %>
<html>
<head>
<title>补货作业单列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css"/>
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
<style type="text/css">
	th{
		color="#FFFFFF";
	}
	th td{
		white-space: nowrap; 
	}
</style>
<script type="text/javascript">
	function link(url){
		var ii = 0;
		var sid = document.getElementsByName("sid");
		var query = "&sid=";
		for(var i=0;i<sid.length;i++){
			if(sid[i].checked == true){
				ii++;
				query += ""+sid[i].value;
				if(i+1 < sid.length){
					query += ",";
				}
			}
		}
		if(ii == 0){
			alert("请选择要补货的货位");
			return;
		}
		window.location.href = url + query;
	}
</script>
</head>
<body>
	<div style="margin-left: 30px;">
		添加补货单 <br/>
		提示:请选择货位再生成补货单<br/>
	</div>
<form method=post action="" onsubmit="document.all.d1.style.display='block';return true;">
<fieldset>
	<legend>补货作业单查询</legend>
			货&nbsp;位&nbsp;号: <input type="text" name="cargoId"/> 左精确右模糊查询  
			<br/>
			产品编号: <input type="text" name="cargoId"/>
			<br/>
			 货位当前库存:<input  size="10" />至 <input size="10"/>	 <input type="submit" value="查询">
</fieldset>
</form>
	<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
		<thead>
			<tr bgcolor="#4688D6">
				<th>序号</th> <th>产品一级分类</th> <th>产品编号</th> <th>产品原名称</th> 
				<th>散件区货符号</th> <th>当前库存</th> <th>整件区库存</th> <th>货位警戒线</th>
				<th>货位最大容量</th> <th>货位产品线</th> <th>货位类型</th> <th>货位尺寸</th><th>备注</th>
			</tr>
		</thead>
		<tbody>
			 <tr>
			 	<td><input type="checkbox" name="sid" value="1">1</td> 
			 		<td>鞋子</td> 	<td>1500HF</td>	<td>耐克</td>
			 	<td>xf758</td> 	<td>10</td> <td>8</td> <td>5</td>
			 	<td>10</td> 	<td>鞋子</td> <td>热销</td> <td>长/宽/高</td> <td>备注</td>
			 </tr>
			 <!-- [{product_id=4580, remark=王涛添加..散件区数据, product_name=仿真-樱花巢二代, width=12, product_oriname=樱花穴(成熟型), zjq_stock_count=70, warn_stock_count=15, type=0, 
			 whole_code=GZF07-A01302, stock_count=80, max_stock_count=20, length=12, high=12, product_line name=服装}] -->
			<c:forEach items="${items}" var="item" varStatus="vs">
			 	<tr>
			 		<td><input type="checkbox" name="sid" value='${item["cargoId"]}'>${vs.index+2}</td> 
			 		<td>${item["product_name"]}</td> 	<td>${item["product_id"]}</td>
			 		<td>${item["product_oriname"]}</td>	<td>${item["whole_code"]}</td> 
			 		<td>${item["stock_count"]}</td> <td>${item["zjq_stock_count"]}</td> <td>${item["warn_stock_count"]}</td>
			 		<td>${item["max_stock_count"]}</td> 	<td>${item["product_line_name"]}</td>
			 		<td>${item["type"]}</td> <td>${item["length"]}/${item["width"]}/${item["high"]}</td> <td>${item["remark"]}</td>
		 		</tr>
			 </c:forEach>
		</tbody>
	</table>
	<div style="margin-left: 30px;">
		<a href="#" onclick="link('${path}/admin/cargoPacked.do?method=cargoPackedToAdd');">选中货位生成补货单</a>
	</div>
</body>
</html>