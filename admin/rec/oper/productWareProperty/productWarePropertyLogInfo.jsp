<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	String productCode = (String)request.getAttribute("productCode");
	String productWarePropertyId = (String) request.getAttribute("productWarePropertyId");
%>
<html>
  <head>
    
    <title>商品物流属性操作日志</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
	jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
	</script>

  </head>
  <body>
  <br/>
  <div align="center">
  <h4>人员操作记录--产品编号<%= productCode %>商品物流属性</h4>
  </div>
		<script type="text/javascript">
		$(function(){
		   $('#log_table').datagrid({   
		    title:'人员操作记录--产品编号<%= productCode %>商品物流属性', 
		    width:840,    
		    pageNumber:1,  
		    pageSize:10,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'<%=request.getContextPath()%>/admin/getProductWarePropertyLogInfo.mmx?productWarePropertyId=<%= productWarePropertyId%>', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[   
                   {field:'time',title:'时间',width:200,align:'center'},   
                   {field:'oper_name',title:'操作人员',width:180,align:'center'},   
                   {field:'oper_info',title:'操作内容',width:420,align:'center'}
	        ]],
		    pagination:true,
		    rownumbers:true, //是否有行号。
		    onLoadSuccess: function(data) {
	    		if( data['tip'] != null ) {
		    		jQuery.messager.alert("提示", data['tip']);
		    	}
	    		$('#log_table').datagrid('getPager').pagination({
				    displayMsg:'当前显示从{from}到{to}共{total}记录',
				    onBeforeRefresh:function(pageNumber, pageSize){
				     $(this).pagination('loading');
				     $(this).pagination('loaded');
				    }
		    
		    	 });
		    }
		   });
		  });
	</script>
	<div align="center">
	<table align="center" id="log_table" >
	</table>
	</div>
</body>
</html>
