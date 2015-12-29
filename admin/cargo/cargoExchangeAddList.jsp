<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@page import="java.util.Iterator"%>
<html>
<head>
<title>补货作业单列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css"/>
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
</head>
<body>
<form method=post action="../searchProductHasStock.do?forward=Exchange&exchangeId="
		 target=sp onsubmit="document.all.d1.style.display='block';return true;">
<fieldset >
	<legend>补货作业单查询</legend>
			所属仓库
				<select>
					<option>GZF01</option>
				</select>
			 所属区域 
				<select>
					<option>GZF01</option>
				</select>
			 货架编号 
				<select>
					<option>GZF01</option>
				</select>
			 第几层 
				<select>
					<option>GZF01</option>
				</select>
		<br/>
			摊位产品线
				<select>
					<option>鞋子 </option>
				</select>
			 摊位类型
				<select>
					<option>散件区 </option>
					<option>整件区 </option>
					<option>缓存区 </option>
				</select>
			摊位编号
				<input size="10">精确
		 <br/>
			产品分类  &nbsp;&nbsp;一级分类
				<select>
					<option>全部 </option>
				</select>
				二级分类
				<select>
					<option>全部 </option>
				</select>
				 产品编号 
				<input type="text" size="8px;"/>
			 	产品原名
				<input size="8px;"/>
			<input type="radio">精确	<input type="radio">模糊
		<br/>
			摊位当前库序<input  size="10" />至 <input  size="10"/>
			 摊位警戒线<input  size="10" />至	 <input  size="10"/>
			 摊位最大容量<input  size="10" />至 <input size="10"/>
			 <input type="submit" value="查询">
</fieldset>
</form>
	<table>
		<thead>
			<tr>
				<th>序号</th> <th>货位号</th> <th>产品编号</th> <th>产品原名</th> 
				<th>当前库存</th> <th>整件区库存</th> <th>摊位警戒线</th>
				<th>摊位最大容量</th> <th>摊位产品线</th> <th>摊位类型</th>  
				<th>货位尺寸</th><th>备注</th>
			</tr>
		</thead>
		<tbody>
			<%
				List ls = null;
				if(ls != null){
					int index = 0; 
					for(Iterator it=ls.iterator();it.hasNext();){
						
					}
				}
			 %>
		</tbody>
	</table>
	<a href="">选中货位生成补货单</a>
</body>
</html>