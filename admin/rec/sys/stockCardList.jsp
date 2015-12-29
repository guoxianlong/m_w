<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
<title>进销存卡片</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="width: 100%;">
		<fieldset>
			<legend>查询栏</legend>
			<form id="form" method="post" action="">
			<table>
				<tr>
					<td><span style="font-size: 12px;">库类型：</span><input name="stockType" style="width:152px;" class="easyui-combobox" editable="false" id="stockType"></td>
					<td><span style="font-size: 12px;">库&nbsp;区&nbsp;域：</span><input name="stockArea" style="width:152px;" class="easyui-combobox" editable="false" id="stockArea"></td>
					<td><span style="font-size: 12px;">起始时间：</span><input name="startDate" id="startDate" style="width:152px;" size="10" class="easyui-datebox" /></td>
					<td><span style="font-size: 12px;">截止时间：</span><input name="endDate" id="endDate" style="width:152px;" size="10" class="easyui-datebox" /></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">单据号：</span><input name="code" id="code" style="width:152px;" size="12" /></td>
					<td><span style="font-size: 12px;">产品编号：</span><input name="productCode" style="width:152px;" id="productCode" size="8" /></td>
					<td><span style="font-size: 12px;">小店名称：</span><input name="productName" style="width:152px;" id="productName" size="8" /></td>
					<td><span style="font-size: 12px;">原&nbsp;名&nbsp;称：</span><input name="productOriName" style="width:152px;" id="productOriName" size="8" /></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><a class="easyui-linkbutton" iconCls="icon-search" plain="true" id="queryStockcard">进销存查询</a>
					<td colspan="2" align="center"><a id="exportStockcard" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-ok'">进销存导出</a>
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
				fitColumns:true,
				pageList:[5,10,15,20],
				url:'<%=request.getContextPath()%>/CargoController/stockCardList.mmx',
				showFooter:true,
				striped:true,
				collapsible:true,
				loadMsg:'数据加载中...',
				rownumbers:true,
// 				singleSelect:false,//只选择一行后变色
				pagination:true,
				queryParams:{stockType:stockType,stockArea:stockArea,startDate:startDate,endDate:endDate,code:code,
							productCode:productCode,productName:productName,productOriName:productOriName},
				frozenColumns:[[
				                {field:'product_detail',title:'产品详情',hidden:true}
				                ]],
				columns:[[
				        {field:'stock_card_stocktype',title:'库类型',width:80,align:'center'},
				        {field:'stock_card_stockarea',title:'库区域',width:80,align:'center'},
				        {field:'stock_card_code',title:'单据号',width:120,align:'center'},
				        {field:'stock_card_cardtype',title:'来源',width:120,align:'center'},
				        {field:'stock_card_createdatetime',title:'时间',width:160,align:'center'},
				        {field:'stock_card_stockincount',title:'入库数量',width:80,align:'center'},
				        {field:'stock_card_stockinprice',title:'入库金额',width:80,align:'center'},
				        {field:'stock_card_stockoutcount',title:'出库数量',width:80,align:'center'},
				        {field:'stock_card_stockoutpricesum',title:'出库金额',width:80,align:'center'},
				        {field:'stock_card_currentstock',title:'当前结存',width:80,align:'center'},
				        {field:'stock_card_stockallarea',title:'本库区域总结存',width:80,align:'center'},
				        {field:'stock_card_stockalltype',title:'本库类总结存',width:80,align:'center'},
				        {field:'stock_card_allstock',title:'全库总结存',width:80,align:'center'},
				        {field:'stock_card_stockprice',title:'库存单价',width:80,align:'center'},
				        {field:'stock_card_allstockpricesum',title:'结存总额',width:80,align:'center'}
				        ]],
				onLoadSuccess:function(data){
					$("#stockcardList").datagrid('selectRow',0);
					var node = $("#stockcardList").datagrid('getSelected');
					$("#product_detail").html(node.product_detail);
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
		$("#exportStockcard").click(function(){
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
			document.forms[0].action = 'stockCardListExport.jsp';
			document.forms[0].submit();
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