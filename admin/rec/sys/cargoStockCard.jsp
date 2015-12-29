<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<html>
<head>
<title>货位进销存卡片</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/locale/easyui-lang-zh_CN.js"></script>
</head>
<body>
	<div style="width: 100%;">
		<fieldset>
			<legend>查询栏</legend>
			<form id="form" method="post">
			<table>
				<tr>
					<td><span>库类型：</span><input name="stockType" style="width:120px;" class="easyui-combobox" editable="false" id="stockType"></td>
					<td><span>库区域：</span><input name="stockArea" style="width:120px;" class="easyui-combobox" editable="false" id="stockArea"></td>
					<td><span>起始时间：</span><input name="startDate" id="startDate" size="10" class="easyui-datebox" /></td>
					<td><span>截止时间：</span><input name="endDate" id="endDate" size="10" class="easyui-datebox" /></td>
				</tr>
				<tr>
					<td><span>单据号：</span><input name="code" id="code" size="12" /></td>
					<td><span>产品编号：</span><input name="productCode" id="productCode" size="8" /></td>
					<td><span>小店名称：</span><input name="productName" id="productName" size="8" /></td>
					<td><span>原名称：</span><input name="productOriName" id="productOriName" size="8" /></td>
				</tr>
				<tr>
					<td><span>货位存放类型：</span><input name="cargoStoreType" style="width:120px;" class="easyui-combobox" editable="false" id="cargoStoreType"></td>
					<td><span>货位号：</span><input name="cargoWholeCode" id="cargoWholeCode"></td>
					<td colspan="2" align="center"><a id="queryStockcard" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">进销存查询</a>
				</tr>
			</table>
			</form>
		</fieldset>
		<div id="product_detail" style="color:FF0000;"></div>
		<table id="stockcardList"></table>
<!-- 		<div><a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" id="export">导出全部货位</a></div> -->
	</div>
</body>
<script type="text/javascript">
	$(function(){
		if("<%=request.getParameter("zz")%>"!="null"){
			$("#stockcardList").datagrid({
				title:"货位进销存列表",
				iconCls:'icon-ok',
// 				width:700,
				height:'auto',
				pageNumber:1,
				fitColumns:true,
				pageSize:20,
				pageList:[5,10,15,20],
				url:'<%=request.getContextPath()%>/CargoController/cargoStockCard.mmx',
				showFooter:true,
				striped:true,
				collapsible:true,
				loadMsg:'数据加载中...',
				rownumbers:true,
// 				singleSelect:false,//只选择一行后变色
				pagination:true,
				queryParams:{productCode:"<%=request.getParameter("productCode")%>",cargoWholeCode:"<%=request.getParameter("cargoWholeCode")%>"},
				frozenColumns:[[
				                {field:'product_detail',title:'产品详情',hidden:true}
				                ]],
				columns:[[
				        {field:'cargo_stock_card_stocktype',title:'库类型',width:80,align:'center'},
				        {field:'cargo_stock_card_stockarea',title:'库区域',width:80,align:'center'},
				        {field:'cargo_stock_card_code',title:'单据号',width:120,align:'center'},
				        {field:'cargo_stock_card_cardtype',title:'卡片类型',width:120,align:'center'},
				        {field:'cargo_stock_card_createdatetime',title:'创建时间',width:180,align:'center'},
				        {field:'cargo_stock_card_stockincount',title:'入库量',width:80,align:'center'},
				        {field:'cargo_stock_card_inpricesum',title:'入库金额',width:80,align:'center'},
				        {field:'cargo_stock_card_outcount',title:'出库量',width:80,align:'center'},
				        {field:'cargo_stock_card_outpricesum',title:'出库金额',width:80,align:'center'},
				        {field:'cargo_stock_card_wholecode',title:'货位号',width:120,align:'center'},
				        {field:'cargo_stock_card_storetype',title:'货位存放类型',width:80,align:'center'},
				        {field:'cargo_stock_card_currentcargostock',title:'当前货位库存',width:80,align:'center'},
				        {field:'cargo_stock_card_currentstock',title:'本区域本库类总库存',width:80,align:'center'},
				        {field:'cargo_stock_card_allstock',title:'全库总库存',width:80,align:'center'},
				        {field:'cargo_stock_card_stockprice',title:'库存单价',width:80,align:'center'},
				        {field:'cargo_stock_card_allstockpricesum',title:'结存总额',width:80,align:'center'}
				        ]],
				onLoadSuccess:function(data){
					$("#productCode").val("<%=request.getParameter("productCode")%>");
					$("#cargoWholeCode").val("<%=request.getParameter("cargoWholeCode")%>");
					$("#stockcardList").datagrid('selectRow',0);
					var node = $("#stockcardList").datagrid('getSelected');
					if(node){
						$("#product_detail").html(node.product_detail);
					}
				}
			});
			var p = $("#stockcardList").datagrid('getPager');
			p.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		}else{
			if("<%=request.getParameter("productCode")%>"!="null"&&"<%=request.getParameter("cargoWholeCode")%>"!="null"){
				$("#productCode").val("<%=request.getParameter("productCode")%>");
				$("#cargoWholeCode").val("<%=request.getParameter("cargoWholeCode")%>");
			}
		}
		$("#stockType").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectStockType.mmx',
			valueField:'id',
			textField:'text',
			onSelect:function(rec){
				var dd = getData(rec.index);
				var jj = eval('('+dd+')');
				$("#stockArea").combobox({
					valueField:'id',
					textField:'text',
					data:jj
				});
			}
		});
		$("#cargoStoreType").combobox({
			valueField:'id',
			textField:'text',
			data:[
			      {id:'',text:'请选择',selected:'true'},
			      {id:'0',text:'散件区'},
			      {id:'1',text:'整件区'},
			      {id:'2',text:'缓存区'},
			      {id:'4',text:'混合区'}
			      ]
		});
		// 条件查询
		$("#queryStockcard").click(function(){
			var i = $.trim($("#productCode").val()).length;
			var j = $.trim($("#productName").val()).length;
			var k = $.trim($("#productOriName").val()).length;
			if(i == 0 && j == 0 && k == 0){
				$.messager.show({
					title:'提示',
					msg:'产品编号、小店名称、原名称必须填写一项！',
					timeout:3000,
					showType:'slide'
				});
				return false;
			}
			var stockType = $("#stockType").combobox('getValue');
			var stockArea = $("#stockArea").combobox('getValue');
			var startDate = $("#startDate").datebox('getValue');
			var endDate = $("#endDate").datebox('getValue');
			var code = $("#code").val();
			var productCode = $("#productCode").val();
			var productName = $.trim($("#productName").val());
			var productOriName = $.trim($("#productOriName").val());
			var cargoStoreType = $("#cargoStoreType").datebox('getValue');
			var cargoWholeCode = $.trim($("#cargoWholeCode").val());
// 			var temp = "wholeCode="+wholeCode+"&storageId="+storageId+"&stockAreaId="+stockAreaId+"&passageId="+passageId
// 						+"&shelfCode="+shelfCode+"&floorNum="+floorNum+"&stockType="+stockType+"&storeType="+storeType
// 						+"&productLineId="+productLineId+"&type="+type;
// 			$("#page_hidden").val(temp);
			var datagrid = $("#stockcardList").datagrid({
				title:"货位进销存列表",
				iconCls:'icon-ok',
// 				width:700,
				height:'auto',
				pageNumber:1,
				pageSize:20,
				pageList:[5,10,15,20],
				url:'<%=request.getContextPath()%>/CargoController/cargoStockCard.mmx',
				showFooter:true,
				striped:true,
				collapsible:true,
				loadMsg:'数据加载中...',
				rownumbers:true,
// 				singleSelect:false,//只选择一行后变色
				pagination:true,
				queryParams:{stockType:stockType,stockArea:stockArea,startDate:startDate,endDate:endDate,code:code,
							productCode:productCode,productName:productName,productOriName:productOriName,
							cargoStoreType:cargoStoreType,cargoWholeCode:cargoWholeCode},
				frozenColumns:[[
				                {field:'product_detail',title:'产品详情',hidden:true}
				                ]],
				columns:[[
				        {field:'cargo_stock_card_stocktype',title:'库类型',width:80,align:'center'},
				        {field:'cargo_stock_card_stockarea',title:'库区域',width:80,align:'center'},
				        {field:'cargo_stock_card_code',title:'单据号',width:120,align:'center'},
				        {field:'cargo_stock_card_cardtype',title:'卡片类型',width:120,align:'center'},
				        {field:'cargo_stock_card_createdatetime',title:'创建时间',width:180,align:'center'},
				        {field:'cargo_stock_card_stockincount',title:'入库量',width:80,align:'center'},
				        {field:'cargo_stock_card_inpricesum',title:'入库金额',width:80,align:'center'},
				        {field:'cargo_stock_card_outcount',title:'出库量',width:80,align:'center'},
				        {field:'cargo_stock_card_outpricesum',title:'出库金额',width:80,align:'center'},
				        {field:'cargo_stock_card_wholecode',title:'货位号',width:120,align:'center'},
				        {field:'cargo_stock_card_storetype',title:'货位存放类型',width:80,align:'center'},
				        {field:'cargo_stock_card_currentcargostock',title:'当前货位库存',width:80,align:'center'},
				        {field:'cargo_stock_card_currentstock',title:'本区域本库类总库存',width:80,align:'center'},
				        {field:'cargo_stock_card_allstock',title:'全库总库存',width:80,align:'center'},
				        {field:'cargo_stock_card_stockprice',title:'库存单价',width:80,align:'center'},
				        {field:'cargo_stock_card_allstockpricesum',title:'结存总额',width:80,align:'center'}
				        ]],
				onLoadSuccess:function(data){
					$("#stockcardList").datagrid('selectRow',0);
					var node = $("#stockcardList").datagrid('getSelected');
					if(node){
						$("#product_detail").html(node.product_detail);
					}
				}
			});
			var p = $("#stockcardList").datagrid('getPager');
			p.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
	});
	function getData(x){
		var str = "[{'id':'','text':'全部','selected':'true'},";
		for (i = 1; i <= ps_spts[x].length; i ++){
			str += "{'id':'"+ps_spts[x][i-1].value+"','text':'"+ps_spts[x][i-1].text+"'}"
			if(i==ps_spts[x].length){
				str += "]";				
			}else{
				str += ",";
			}
		}
		return str;
	}
</script>
</html>