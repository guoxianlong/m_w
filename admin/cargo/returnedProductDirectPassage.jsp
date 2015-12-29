<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货上架指向管理-巷道明细</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
<div style="text-align:center;width:800px;">巷道明细</div>
<br>
<div><table id="datagrid"></table></div>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.7.1.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript">
$(function($) {
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/returnedProductDirect/showPassageDetail.mmx?directId=${param.directId}',
		width : 800,
		border: true,
		fitColumns : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    collapsible:true,
	    pageSize : 30,
	    pageList : [10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[]
	    ],
	    columns:[[
	        {field:'code',title:'巷道号',width:150,align:'center'},
	        {field:'wholeCode',title:'所属区域',width:150,align:'center'},  
	        {field:'stockTypeName',title:'库存类型',width:150,align:'center'}, 
	        {field:'shelfNum',title:'货架数',width:150,align:'center'}
	    ]]
	});	
});

</script>
</body>