<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
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
			作业单编号<input name=""/>	库存类型
				<select name="">
					<option>全部</option>
					<option>合格库</option>
					<option>待验库</option>
				</select> 
		<br/> 
		作业单状态 
				<input type="checkbox" >未处理
				<input type="checkbox" >处理中
				<input type="checkbox" >已确认
				<input type="checkbox" >审核通过
				<input type="checkbox" >审核未通过
				<input type="checkbox" >已完成
		<br/>
		仓库代号 
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
		<br/> 存放类型 
				<select>
					<option>散件区 </option>
					<option>整件区 </option>
					<option>缓存区 </option>
				</select>
			 产品分类 
				<select>
					<option>鞋子 </option>
				</select>
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
			 	产品原名称 
				<input size="8px;"/>
			<input type="radio">精确	<input type="radio">模糊
		<br/>
			制单人 <input size="8px;"/>精确&nbsp;&nbsp;
			审核人<input size="8px;"/>精确&nbsp;
			 操作人<input size="8px;"/>精确
		<br/>
			 制单时间<input  size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');"/>至
			 <input  size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');"/>
			 审核时间<input  size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');"/>至
			 <input  size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');"/>
			 制作完成时间<input  size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');"/>至
			 <input size="10" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');"/>
			 
			 <input type="submit" value="查询">
			 <a href="#">添加新的补货单</a>
</fieldset>
</form>
	<table>
		<thead>
			<tr>
				<th>序号</th> <th>作业单编号</th> <th>作业库</th> 
				<th>制作时间</th> <th>制单人</th> <th>审核人</th>
				<th>确认完成时间</th> <th>操作人</th> <th>作业单状态</th>  
				<th>操作</th>
			</tr>
		</thead>
	</table>
</body>
</html>