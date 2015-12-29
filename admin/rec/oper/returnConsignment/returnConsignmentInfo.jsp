<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="mmb.stock.stat.*"%>
<!DOCTYPE html>
<%
	String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request, -1);
%>
<html>
<head>
<title>退货库快销商品明细</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/locale/easyui-lang-zh_CN.js"></script>
	<style type="text/css">
	body {
		padding: 0px;
	}
	form {
		padding: 0px;
		margin-left: 0px;
		margin-right:0px;
	}
	</style>
</head>
<body>
<div id="tb">
  <fieldset>
			<legend>筛选</legend>
			<form id="form1" name="form1" action="" method="post">
   			地区: <%= wareAreaLable%>
   			商品编号:<input type="text" id="productCode" name="productCode" />
   			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="goSearch();" >查询 </a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="javascript:form1.reset();">清空 </a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addStockExchange();">生成调拨单</a> 
        		<br/>
        		<br/>
        		<div style="display:inline;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.对选中的商品的退货库所有可用库存生成调拨单调往返厂库</div>
        		<br/>
			</form>
   			</fieldset>
</div>
   		<table align="center" id="info_table" >
		</table>
	<script type="text/javascript" >
		function goSearch() {
			
			$('#info_table').datagrid('load', form2Json('form1'));
			
		}
		function form2Json(id) {
 
             var arr = $("#" + id).serializeArray()
             var jsonStr = "" ;
 
            jsonStr += '{';
             for (var i = 0; i < arr.length; i++) {
                jsonStr += '"' + arr[i].name + '":"' + arr[i].value + '",'
            }
            jsonStr = jsonStr.substring(0, (jsonStr.length - 1));
            jsonStr += '}'
 
             //var json = JSON.parse(jsonStr);

                    //注意这个JSON在ie上会报错所以修改成
                    var json = eval('(' + jsonStr +')');
             return json
    }
		
	
		$(function(){
		   $('#info_table').datagrid({
		    fitColumns:true,
		    fit:true,
			border : true,
		    pageNumber:1,  
		    pageSize:200,    
		    pageList:[100,200,300,500], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'<%=request.getContextPath()%>/consignmentProductController/getReturnConsignmentProduct.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    //singleSelect:true,
		    columns:[[   
	                    {field:'cargoInfoId',title:'id', rowspan:2,width:100,align:'center',sortable:true,checkbox:true},   
	                    {field:'productCode',title:'商品编号', rowspan:2,width:100,align:'center',sortable:true},   
	                    {field:'productName',title:'商品名称', rowspan:2,width:300,align:'center',sortable:true},   
	                    {field:'stockCount',title:'库存量(锁定量)',
	                    formatter: function(value,row,index){
							return "<b>"+row.stockCount+"(<a href=\"<%= request.getContextPath()%>/admin/cargoOperation.do?method=stockExchangeList&cargoWholeCode="+row.wholeCode+"&productCode="+row.productCode+"&type=stockLock\" target=\"_blank\">"+row.stockLockCount+"</a>)</b>";
						},rowspan:2,width:100,align:'center',sortable:true},   
	                    {field:'areaName',title:'库存地区',rowspan:2,width:100,align:'center',sortable:true},   
	                    ]],
	                toolbar:'#tb',
		    pagination:true,
		    rownumbers:true, //是否有行号。
		    selectOnCheck:true,
		    checkOnSelect:false,
		    onLoadSuccess: function(data) {
		    	if (data.rows.length > 0) { 
                 //循环判断操作为新增的不能选择 
                 for (var i = 0; i < data.rows.length; i++) { 
                     //根据operate让某些行不可选 
                     if (data.rows[i]['stockCount'] == 0 ) { 
                         $("input[type='checkbox']")[i + 1].disabled = true; 
                     } 
                 } 
             } 
             if( data['tip'] != null ) {
		    		jQuery.messager.alert("提示", data['tip']);
		    	}
		    },
	         onCheckAll:function(rows){ 
	             if (rows.length > 0) { 
	                 //循环判断操作为新增的不能选择 
	                 for (var i = 0; i < rows.length; i++) { 
	                     //根据operate让某些行不可选 
	                     if (rows[i]['stockCount'] == 0 ) { 
	                         $("input[type='checkbox']")[i + 1].checked = false; 
	                     } 
	                 }
	             }
	         }
		   });
		   });
		   
		   function addStockExchange(){
		   	var checked = $("#info_table").datagrid("getChecked");
		   	if( checked == null || checked == "" ) {
		   		 $.messager.alert("提示", "没有选择任何条目！");
		   		 return;
		   	} else {
		   		var param = "";
		   		var wareArea = checked[0]['areaId'];
		   		for( var i = 0 ;i < checked.length; i++ ) {
		   			var checkedRow = checked[i];
		   			if( wareArea != checkedRow['areaId'] ) {
		   				$.messager.alert("提示", "选择的库存属于不同地区，无法生成这样的调拨单！");
		   		 		return;
		   			}
		   			if( i == 0 ) {
		   				param +="?cargoProductStockIds="+checkedRow['cargoProductStockId'];
		   			} else {
		   				param +="&cargoProductStockIds="+checkedRow['cargoProductStockId'];
		   			}
		   		}
		   		param +="&wareArea="+wareArea;
		   		var targetUrl = "<%= request.getContextPath()%>/consignmentProductController/createStockExchange.mmx"+param;
		   		$.post( targetUrl,
		   			{x:1},
		   			function (data,textStatus) {
                        if( data['status'] == "success" ) {
                        	document.location=data['url'];
                        } else if (data['status'] == "fail" ) {
                        	$.messager.alert("提示", data['tip']);
                        } else {
                        	$.messager.alert("提示", "参数错误！");
                        }
            		},
            		"json");
		   	}
		   }
		   
		   </script>
		
</body>
</html>
