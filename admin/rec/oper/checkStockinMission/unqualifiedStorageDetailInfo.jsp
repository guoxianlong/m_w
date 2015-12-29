<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%
	int productLine = 0;
	String productCode = "";
	String productName = "";
	String stockinTime = "";
	String exchangeTime = "";
	String exchangeCode = "";
	String buyStockCode = "";
	int wareArea = -1;
	String checkStockMissionCode = "";
	List productLineList = (List) request.getAttribute("productLineList");
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
	productLine = StringUtil.toInt(request.getParameter("productLine"));
	stockinTime = StringUtil.convertNull(request.getParameter("stockinTime"));
	exchangeTime = StringUtil.convertNull(request.getParameter("exchangeTime"));
	exchangeCode = StringUtil.convertNull(request.getParameter("exchangeCode"));
	buyStockCode = StringUtil.convertNull(request.getParameter("buyStockCode"));
	productCode = StringUtil.convertNull(request.getParameter("productCode"));
	productName = StringUtil.convertNull(request.getParameter("productName"));
	wareArea = StringUtil.toInt(request.getParameter("wareArea"));
	checkStockMissionCode = StringUtil.convertNull(request.getParameter("checkStockMissionCode"));
	List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request, -1);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>质检入库不合格商品明细</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
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
	function searchLoadPage() {
   		var wareArea = $("#wareArea").val();
   		$("#hwareArea").val(wareArea);
   		var productCode = $("#productCode").val();
   		$("#hproductCode").val(productCode);
   		var productName = $("#productName").val();
   		$("#hproductName").val(productName);
   		var productLine = $("#productLine").val();
   		$("#hproductLine").val(productLine);
   		var stockinTime = $("#stockinTime").val();
   		$("#hstockinTime").val(stockinTime);
   		var exchangeTime = $("#exchangeTime").val();
   		$("#hexchangeTime").val(exchangeTime);
   		var exchangeCode = $("#exchangeCode").val();
   		$("#hexchangeCode").val(exchangeCode);
   		var checkStockMissionCode = $("#checkStockMissionCode").val();
   		$("#hcheckStockMissionCode").val(checkStockMissionCode);
   		var buyStockCode = $("#buyStockCode").val();
   		$("#hbuyStockCode").val(buyStockCode);
   		$("#info_table").datagrid('load',{  
    	    wareArea:$("#hwareArea").val(),
    	    productCode:$("#hproductCode").val(),
    	    productName:$("#hproductName").val(),
    	    productLine:$("#hproductLine").val(),
    	    stockinTime:$("#hstockinTime").val(),
    	    exchangeTime:$("#hexchangeTime").val(),
    	    exchangeCode:$("#hexchangeCode").val(),
    	    checkStockMissionCode:$("#hcheckStockMissionCode").val(),
    	    buyStockCode:$("#hbuyStockCode").val()
    		});
 	}
	
		function exportTarget() {
			var form1 = document.getElementById('form1');
			form1.action='exportUnqualifiedStorageDetailInfo2.mmx';
			form1.submit();
		}
		
		function printTarget() {
			var form1 = document.getElementById('form1');
			form1.action='printUnqualifiedStorageDetailInfo2.mmx';
			form1.submit();
		}
	</script>
	
</head>
<body fit="true">
<div id="tb">
	<fieldset>
	<legend>不合格商品接收明细查询</legend>
	<table>
		<tr><td align="left">
	产品编号：
	<input type="text" size="13" name="productCode" id="productCode" value="<%= productCode %>" />
	<input type="hidden" size="13" id="hproductCode"  />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	原名称：
	<input type="text" size="20" name="productName" id="productName" value="<%= productName %>" />
	<input type="hidden" size="20" id="hproductName" value="" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	产品线：
		<select name="productLine" id="productLine">
								<option value="0">请选择</option>
							<%
							for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
								voProductLine proLineBean = (voProductLine) productLineList.get(p);
							%>
								<option value="<%= proLineBean.getId() %>" <%= proLineBean.getId() == productLine ? "selected" : "" %> ><%= proLineBean.getName() %></option>
							<%
							}
							%>
		</select>
		<input type="hidden" id="hproductLine" value="0"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td>
		</tr>
		<tr>
		<td>
	到货时间：
		<input type="text" size="13" name="stockinTime" id="stockinTime" onclick="WdatePicker();" value="<%= stockinTime%>" />
		<input type="hidden" size="13" id="hstockinTime"  value="" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	调拨时间：
		<input type="text" size="13" name="exchangeTime" id="exchangeTime" onclick="WdatePicker();" value="<%= exchangeTime %>"/>
		<input type="hidden" size="13" id="hexchangeTime"   value=""/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	调拨单号：
		<input type="text" name="exchangeCode" id="exchangeCode" size="13"  value="<%= exchangeCode%>"/>
		<input type="hidden" id="hexchangeCode" size="13"  value=""/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td></tr>
		<tr><td align="left">
		
	质检任务单号：
		<input type="text" name="checkStockMissionCode" id="checkStockMissionCode" value="<%= checkStockMissionCode%>" />
		<input type="hidden" id="hcheckStockMissionCode" value="" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	预计到货单号：
		<input type="text" name="buyStockCode" id="buyStockCode" size="13"  value="<%= buyStockCode%>"/>
		<input type="hidden" id="hbuyStockCode" size="13"  value=""/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
	库地区: 
		<%= wareAreaSelectLable %>
		<input type="hidden" id="hwareArea" value="-1" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="javascript:searchLoadPage();" class="easyui-linkbutton" iconCls="icon-search">查询</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		</td></tr>
		</table>
	</fieldset>
	<a href="javascript:exportTarget();" class="easyui-linkbutton" data-options="iconCls:'icon-save'" plain="true">导出明细单</a>
	<a href="javascript:printTarget();" class="easyui-linkbutton" data-options="iconCls:'icon-print'" plain="true">打印明细单</a>
	</div>
	<div id= "menu" class= "easyui-menu" style="width:120px;display: none;">
             <div onclick= "exportTarget();" iconCls= "icon-save">导出明细单 </div>
             <div onclick= "printTarget();" iconCls= "icon-print">打印明细单 </div>
  	</div>
	
	<script type="text/javascript" >
		$(function(){
			 $( '#info_table').datagrid({
                   onRowContextMenu : function(e, rowIndex, rowData) {
                              e.preventDefault();
                              $( this).datagrid('unselectAll' );
                              $( this).datagrid('selectRow' , rowIndex);
                              $( '#menu').menu('show' , {
                                    left : e.pageX,
                                    top : e.pageY
                              });
                        }
                  });
			
		   $('#info_table').datagrid({  
		   	fit:true,
		    fitColumns : true,
		    border : true,   
		    pageNumber:1,  
		    pageSize:10,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    url:'../admin/getUnqualifiedStorageDetailInfo2.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[
		     {title:'序号',field:'count_number',width:40,align:'center'},
		     {title:'产品编号',field:'product_code',width:90,align:'center'},
		     {title:'原名称',field:'ori_name',width:100,align:'center'},
		     {title:'数量',field:'count',width:50,align:'center'},
		     {title:'到货时间',field:'stockin_time',width:130,align:'center'},
		     {title:'调拨时间',field:'exchange_time',width:130,align:'center'},
		     {title:'地区',field:'ware_area',width:80,align:'center'},
		     {title:'调拨单号',field:'exchange_code',width:130,align:'center'},
		     {title:'采购入库单号',field:'buy_stockin_code',width:130,align:'center'},
		     {title:'预计到货单号',field:'buy_stock_code',width:130,align:'center'},
		     {title:'状态',field:'status',width:80,align:'center'}
		    ]],
		    toolbar:'#tb',
		    pagination:true,
		    rownumbers:false, //是否有行号。
		    singleSelect:true,
		    onLoadSuccess: function(data) {
		    	var vwareArea = $("#hwareArea").val();
		   		$("#wareArea").val(vwareArea);
		   		var vproductCode = $("#hproductCode").val();
		   		$("#productCode").val(vproductCode);
		   		var vproductName = $("#hproductName").val();
		   		$("#productName").val(vproductName);
		   		var vproductLine = $("#hproductLine").val();
		   		$("#productLine").val(vproductLine);
		   		var vstockinTime = $("#hstockinTime").val();
		   		$("#stockinTime").val(vstockinTime);
		   		var vexchangeTime = $("#hexchangeTime").val();
		   		$("#exchangeTime").val(vexchangeTime);
		   		var vexchangeCode = $("#hexchangeCode").val();
		   		$("#exchangeCode").val(vexchangeCode);
		   		var vcheckStockMissionCode = $("#hcheckStockMissionCode").val();
		   		$("#checkStockMissionCode").val(vcheckStockMissionCode);
		   		var vbuyStockCode = $("#hbuyStockCode").val();
		   		$("#buyStockCode").val(vbuyStockCode);
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
	</script>
	<form action="" method="post" id="form1" style="height:100%;">
	<table align="center" id="info_table" >
	</table>
	</form>
</body>
</html>
