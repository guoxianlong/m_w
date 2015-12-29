<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%
	List productLineList = (List) request.getAttribute("productLineList");
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request, -1);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>未查明包裹</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
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
	<script type="text/javascript">
	jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
	</script>
</head>
<body fit="true">
<div id="tb">
	<br/>
	<form name="form2" id="form2" method="post">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%= wareAreaSelectLable %>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		产品编号:<input type="text" name="productCode" id="productCode" value=""/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		商品条码:<input type="text" name="productBarCode" id="productBarCode" value=""/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		产品线：
		<select name="productLine" id="productLine">
								<option value="0">全部</option>
							<%
							for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
								voProductLine proLineBean = (voProductLine) productLineList.get(p);
							%>
								<option value="<%= proLineBean.getId() %>"><%= proLineBean.getName() %></option>
							<%
							}
							%>
		</select>
		<input type="hidden" name="areaId" id="areaId" value="-1" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="javascript:searchSubmit();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" >查询</a>
		</form>
	<a href="javascript:batchGenerateBySKU();" class="easyui-linkbutton" data-options="iconCls:'icon-add'" plain="true" >对所选SKU生成异常货位盘点计划</a>
	</div>
	<script type="text/javascript" >
		$(function(){
		   $('#info_table').datagrid({  
		   	fit:true,
		    fitColumns : true,
		    border : true,   
		    pageNumber:1,  
		    pageSize:50,    
		    pageList:[50,100,200], 
		    nowrap:false,  
		    striped: true,
		    url:'<%= request.getContextPath()%>/AbnormalCargoCheckController/getSortingAbnormalInfoForSKU.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    rownumbers:true,
		    columns:[[
		     {field:'ck',title:'勾选',checkbox:true },
		     {title:'产品编号',field:'product_code',width:60,align:'center'},
		     {title:'商品条码',field:'product_bar_code',width:70,align:'center'},
		     {title:'冻结量',field:'lock_count',width:50,align:'center'},
		     {title:'货位',field:'cargo_whole_code',width:130,align:'center'}
		    ]],
		    toolbar:'#tb',
		    pagination:true,
		    singleSelect:false,
		    onCheck: function (rowIndex,rowData) {
				//alert(rowIndex);
				//alert(rowData['package_code']);
				//$('#info_table').datagrid('beginEdit', rowIndex);
		    },
		    onUncheck: function (rowIndex,rowData) {
				//alert(rowIndex);
				//alert(rowData['package_code']);
				//$('#info_table').datagrid('endEdit', rowIndex);
		    },
		    onBeforeEdit:function(index,row){  
        		//row.editing = true;  
        		//$('#info_table').datagrid('refreshRow', index);  
    		},  
    		onAfterEdit:function(index,row){  
      		 	//row.editing = false;  
       		 	//$('#info_table').datagrid('refreshRow', index);  
   			 },  
   			 onCancelEdit:function(index,row){  
        		//row.editing = false;  
       			//$('#info_table').datagrid('refreshRow', index);  
    		},
		    //checkOnSelect:false,
		    //selectOnCheck:false,
		    onLoadSuccess: function(data) {
		    	if( data['tip'] != null ) {
		    		jQuery.messager.alert("提示", data['tip']);
		    	}
		   		$('#info_table').datagrid('getPager').pagination({
				    displayMsg:'当前显示从{from}到{to}共{total}记录',
				    onBeforeRefresh:function(pageNumber, pageSize){
				     $(this).pagination('loading');
				     $(this).pagination('loaded');
				    }
		    	 });
		    }
		   });
		  });
		
		function showSearchDialog() {
		      $( '#search-dialog').css("display" ,"block" );    // 修改div由不显示，改为显示 。
		      $( '#search-dialog').dialog({ 
		          width: 550,         
		          height: 300, 
		          title : '查询条件',  
		          closed: false,  
		          cache: false,   
		          modal: true
     		 }); 
       		$("#packageCodes").focus();
		}
		function showAddUnrecognisePackageDialog() {
		      $( '#addUnrecognisePackage-dialog').css("display" ,"block" );    // 修改div由不显示，改为显示 。
		      $( '#addUnrecognisePackage-dialog').dialog({ 
		          width: 550,         
		          height: 300, 
		          title : '查询条件',  
		          closed: false,  
		          cache: false,   
		          modal: true
     		 }); 
     		 $("#packageCode").focus();
		}
		jQuery.fn.datebox.defaults.formatter = function(date){
		var y = date.getFullYear();
		var m = date.getMonth()+1;
		var d = date.getDate();
		var result = y+'-';
		if (m >= 10 ) 
        { 
         result += m + "-"; 
        } 
        else
        { 
         result += "0" + m + "-"; 
        } 
        if (d >= 10 ) 
        { 
         result += d ; 
        } 
        else
        { 
         result += "0" + d ; 
        } 
		return result;
	} 
	
	function searchSubmit() {
		/*
		alert($("#form2").serialize());
		var params = $("#form2").serialize();
		$.post(
			"<%= request.getContextPath()%>/AbnormalCargoCheckController/getSortingAbnormalInfoForSKU.mmx",
			$("#form2").serialize(),
			function (data) {
				if(data['tip'] != null && data['tip'] != "" ) {
					$.messager.alert('提示',data['tip'],'error', function () {
					
					});
				}
				$("#info_table").datagrid('reload', parames);
				$("#search-dialog").dialog('close');
			},
			'json'
		);
		*/
		var area = $("#wareArea").val();
		$("#areaId").val(area);
		$("#info_table").datagrid('reload', form2Json("form2"));
	}
	function form2Json(id) {
            var arr = $("#" + id).serializeArray()
            var jsonStr = "";
 
            jsonStr += '{';
            for (var i = 0; i < arr.length; i++) {
                jsonStr += '"' + arr[i].name + '":"' + arr[i].value + '",'
            }
            jsonStr = jsonStr.substring(0, (jsonStr.length - 1));
            jsonStr += '}'
 
            var json = eval('('+jsonStr+')');
            return json
    }
	String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
	
	/*
		批量添加到售后单
	*/
	function batchGenerateBySKU() {
		var checkedRows = $("#info_table").datagrid("getChecked");
		var x = checkedRows.length;
		if( x == 0 ) {
			$.messager.alert('提示',"没有勾选任何行！",'error', function () {
						
			});
			return;
		}
		var params = "";
		for( var i = 0 ; i < x; i ++ )  {
			var checkedRow = checkedRows[i];
			if( i == 0 ) {
				params += "?productIds=" + checkedRow['product_id'];
			} else {
				params += "&productIds=" + checkedRow['product_id'];
			}
		}
		var areaId = $("#areaId").val();
		params += "&areaId=" + areaId;
		$('#info_table').datagrid('loading');
		$.post(
			"<%= request.getContextPath()%>/AbnormalCargoCheckController/generateBySKU.mmx" +params,
			{
				x:1
			},
			function (data) {
				if( data['status'] == "fail" ) {
					$.messager.alert('提示',data['tip'],'error', function () {
						$('#info_table').datagrid('loaded');
					});
				} else {
					$.messager.alert('提示',data['tip'],'tip', function () {
						$('#info_table').datagrid('reload');
					});
				}
			},
			"json"
		);
		
		
	}
	
	</script>
	<table align="center" id="info_table" >
	</table>
</body>
</html>
