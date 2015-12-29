<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<title>封箱列表</title>
</head>
<body>
	<div id="tb" style="height: auto;">
			<fieldset>
			<legend>封箱列表</legend>
				<table>
					<tr>
					<td>
						<span style="font-size: 12px;">封箱日期：</span><input name="startDate"  class="easyui-datebox" editable="false" id="startDate">--<input name="endDate"  class="easyui-datebox" editable="false" id="endDate">
					</td>
					<td><span style="font-size: 12px;">售后单号：</span><input type="text" class="easyui-validatebox" name="afterSaleOrderCode" id="afterSaleOrderCode"></td>
					<td><span style="font-size: 12px;">售后处理单号：</span><input type="text" class="easyui-validatebox" name="afterSaleDetectCode" id="afterSaleDetectCode"></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">封箱编号：</span><input type="text" class="easyui-validatebox" name="afterSaleSealCode" id="afterSaleSealCode"></td>
					<td><span style="font-size: 12px;">售后地区：</span><input id="areaId" name="areaId"/></td>
					<td>
						<a id="querySealList" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
					</tr>
				</table>
			</fieldset>
		</div>
		<table id="afterSaleSealList"></table>
</body>
<script type="text/javascript">
	var datagrid;
	$(function(){
		datagrid = $("#afterSaleSealList").datagrid({
			toolbar:'#tb',
			idField : 'seal_id',
			iconCls:'icon-ok',
			fitColumns:true,
			fit:true,
			pageSize:20,
			pageList:[10,20,30,40],
			url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleSealList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns:[[
			        {field:'seal_date',title:'封箱日期',width:80,align:'center'},
			        {field:'seal_code',title:'封箱编号',width:80,align:'center'},
			        {field:'seal_product_count',title:'封箱商品数量',width:80,align:'center'},
			        {field:'operator',title:'操作人',width:80,align:'center'},
			        {field:'seal_control',title:'操作',width:200,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);"  class="querySealInfo" onclick="querySealInfo('+index+')"></a>&nbsp;&nbsp;' + 
			        		          '<a href="javascript:void(0);"  class="printSealInfo" onclick="printSealInfo('+index+')"></a>';
			        	}
			        }
			    ]],
			    onLoadSuccess : function(data) {
				//改变datagrid中按钮的class
				$(".querySealInfo").linkbutton(
					{ 
						text:'查看封箱清单'
					}
				);
				$(".printSealInfo").linkbutton(
					{ 
						text:'打印封箱清单'
					}
				);
			}
		});
		
		$("#querySealList").click(function(){
			var startDate = $("#startDate").datebox('getValue');
			var endDate = $("#endDate").datebox('getValue');
			if(startDate!='' && endDate!=''){
					if(!validateDate(endDate,startDate)){
						alert('开始日期必须小于结束日期');
						$('#startDate').focus();
						return false;
					}
				}
			var afterSaleOrderCode = $("#afterSaleOrderCode").val();
			var afterSaleDetectCode = $("#afterSaleDetectCode").val();
			var afterSaleSealCode = $("#afterSaleSealCode").val();
			var areaId = $('#areaId').combobox('getValue');
			datagrid = $("#afterSaleSealList").datagrid('load',{
				startDate:startDate,
				endDate:endDate,
				afterSaleOrderCode:afterSaleOrderCode,
				afterSaleSealCode:afterSaleSealCode,
				areaId:areaId
			});
		});
		$('#areaId').combobox({
	      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
	      	valueField:'id',
			textField:'text',
			editable:false
	    });
	});
	
	function querySealInfo(index){
			if (index != undefined) {
				$('#afterSaleSealList').datagrid('selectRow', index);
			}
			var row = $('#afterSaleSealList').datagrid('getSelected');
			$('<div/>').dialog({
				href : '${pageContext.request.contextPath}/admin/afStock/afterSaleSealInfo.jsp',
				width : 900,
				height : 520,
				modal : true,
				title : '封箱清单',
				onClose : function() {
					$(this).dialog('destroy');
				},
				onLoad : function() {
					$("#sealInfoTable label[id=operator]").html(row.operator);
					$("#sealInfoTable label[id=sealDate]").html(row.seal_date);
					$("#sealInfoTable label[id=sealCode]").html(row.seal_code);
					initSealInfoDataGrid(row.seal_id);
				}
			});
		}
		
		function printSealInfo(index){
			if (index != undefined) {
				$('#afterSaleSealList').datagrid('selectRow', index);
			}
			var row = $('#afterSaleSealList').datagrid('getSelected');
			window.open('${pageContext.request.contextPath}/admin/AfStock/printSealInventory.mmx?sealIds=' + row.seal_id);
		}
		
		function initSealInfoDataGrid(sealId){
			$("#sealInfoDataGrid").datagrid({
				url:'${pageContext.request.contextPath}/admin/AfStock/querySealInfo.mmx?id=' + sealId ,
			    fitColumns : true,
			    striped : true,
			    nowrap : true,
			    loadMsg : '正在努力为您加载..',
			    rownumbers:true,
				singleSelect:true,//只选择一行后变色
			    columns:[[
			    	{field:'productName',title:'商品名称',width:80,align:'center'},
			    	{field:'productOriname',title:'型号',width:80,align:'center'},
			    	{field:'imei',title:'IMEI',width:100,align:'center'},
			    	{field:'aferSaleOrderCode',title:'售后单号',width:100,align:'center'},
			    	{field:'afterSaleOrderStatusName',title:'封箱时售后单状态',width:80,align:'center'},
			    	{field:'afterSaleDetectProductCode',title:'售后处理单号',width:100,align:'center'},
			    	{field:'afterSaleOrderDetectProductStatusName',title:'封箱时售后处理单状态',width:80,align:'center'}
			    ]]
			});
		}
		
		function clearFun() {
			$('#startDate').datebox('setValue',"");
			$('#endDate').datebox('setValue',"");
			$('#afterSaleOrderCode').val('');
			$('#afterSaleDetectCode').val('');
			$('#afterSaleSealCode').val('');
			$('#areaId').combobox('setValue','');
			datagrid.datagrid('load', {});
		}
		
	function excel(){
		var startDate = $('#startDate').datebox('getValue');
		var endDate = $('#endDate').datebox('getValue');
		var afterSaleOrderCode = $('#afterSaleOrderCode').val();
		var afterSaleDetectCode = $('#afterSaleDetectCode').val();
		var afterSaleSealCode = $('#afterSaleSealCode').val();
		var areaId = $('#areaId').combobox('getValue');
		location.href = "${pageContext.request.contextPath}/admin/AfStock/excelSealList.mmx?startDate=" + 
		startDate + "&endDate=" + endDate + "&afterSaleDetectCode=" + afterSaleDetectCode + "&afterSaleOrderCode=" 
			+ afterSaleOrderCode + "&areaId=" + areaId;
	}
</script>
</html>
