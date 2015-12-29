<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货上架指向管理-操作日志</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
<div>人员操作记录-退货上架逻辑编号:${param.directCode }</div>
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
		url : '${pageContext.request.contextPath}/returnedProductDirect/showDirectLog.mmx?directId=${param.directId}',
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
	        {field:'createDatetime',title:'时间',width:100,align:'center'},
	        {field:'content',title:'说明',width:460,align:'left'},  
	        {field:'username',title:'操作人',width:120,align:'center'}, 
	        {field:'activityDetail',title:'触发记录事件',width:120,align:'center'},
	    ]]
	});	
});

</script>
</body>