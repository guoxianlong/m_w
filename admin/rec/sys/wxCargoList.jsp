<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<head>
<title>无锡仓货位列表</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="width: 100%;">
		<fieldset>
			<legend>查询栏</legend>
			<form id="form" method="post">
			<table>
				<tr>
					<td><span style="font-size: 12px;">货&nbsp;&nbsp;位&nbsp;&nbsp;号：</span><input type="text" style="width:152px;" size=12 name="wholeCode" id="wholeCode"/><span style="font-size: 12px;">左精确右模糊查询</span></td>
					<td><span style="font-size: 12px;">产品编号：&nbsp;&nbsp;</span><input type="text" style="width:152px;" size=12 name="productCode" id="productCode"/></td>
					<td><span style="font-size: 12px;">产品原名称：&nbsp;&nbsp;</span><input type="text" size=20 name="productName" id="productName"/><span style="font-size: 12px;">精确</span></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">存放类型：&nbsp;&nbsp;</span><input name="storeType" style="width:152px;" class="easyui-combobox" editable="false" id="storeType"></td>
					<td><span style="font-size: 12px;">货位产品线：</span><input name="productLineId" style="width:152px;" class="easyui-combobox" editable="false" id="productLineId"></td>
					<td><span style="font-size: 12px;">货&nbsp;位&nbsp;类&nbsp;型：&nbsp;</span><input name="type" style="width:152px;" class="easyui-combobox" editable="false" id="type"></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">仓&nbsp;&nbsp;库&nbsp;&nbsp;号：</span><input name="storageId" style="width:152px;" class="easyui-combobox" editable="false" id="storageId"></td>
					<td><span style="font-size: 12px;">仓库区域：&nbsp;&nbsp;</span><input name="stockAreaId" style="width:152px;" class="easyui-combobox" editable="false" id="stockAreaId"></td>
					<td><span style="font-size: 12px;">货位当前库存：</span><input type="text" style="width:73px;" name="stockCountStart" id="stockCountStart" size=3/><span style="font-size: 12px;">至</span><input type="text" style="width:73px;" id="stockCountEnd" name="stockCountEnd" size=3 /></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">货架代号：&nbsp;&nbsp;</span><input name="shelfCode" id="shelfCode"><span style="font-size: 12px;">（如：01）</span></td>
					<td><span style="font-size: 12px;">第几层：&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" style="width:152px;" size=5 name="floorNum" id="floorNum"/></td>
					<td><span style="font-size: 12px;">货&nbsp;位&nbsp;状&nbsp;态：</span><input type="checkbox" name="status" value="0" checked="checked"/><span style="font-size: 12px;">使用中</span>
						<input type="checkbox" name="status" value="1" /><span style="font-size: 12px;">未使用</span>
						<input type="checkbox" name="status" value="2" /><span style="font-size: 12px;">未开通</span></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">装箱单编号：</span><input type="text"  name="cartonningCode" id="cartonningCode"/></td>
					<td><a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="queryZccargo">查找</a>
						<input type="hidden" id="page_hidden"/></td>
				</tr>
			</table>
			</form>
		</fieldset>
		<table id="zccargoList"></table>
		<div><a class="easyui-linkbutton" data-options="iconCls:'icon-redo'" plain="true" id="export">导出全部货位</a></div>
	</div>
</body>
<script type="text/javascript">
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	function closeCargo(id){
		$.messager.confirm('询问','如果确认关闭该货位，请单击确定，反之请单击取消！',function(boo){
			if(boo){
				$.ajax({
					url:'<%=request.getContextPath()%>/CargoController/closeCargo.mmx',
					data:{cargoId:id},
					cache:false,
					dataType:'text',
					success:function(result){
						var re = eval('('+result+')');
						if(re['result']=="success"){
							$("#zccargoList").datagrid('reload');
						}
						$.messager.show({
							title:'结果提示',
							msg:re['tip'],
							timeout:3000,
							showType:'slide'
						});
					}
				});
			}
		});
	}
	function clearCargo(id,productId){
		$.messager.confirm('询问','如果确认清空该货位，请单击确定，反之请单击取消！',function(boo){
			if(boo){
				$.ajax({
					url:'<%=request.getContextPath()%>/CargoController/clearCargo.mmx',
					data:{cargoId:id,productId:productId},
					cache:false,
					dataType:'text',
					success:function(result){
						var re = eval('('+result+')');
						if(re['result']=="success"){
							$("#zccargoList").datagrid('reload');
						}
						$.messager.show({
							title:'结果提示',
							msg:re['tip'],
							timeout:3000,
							showType:'slide'
						});
					}
				});
			}
		});
	}
	$(function(){
		var str = "areaId=4&storeType=0&status=0";
		$("#page_hidden").val(str);
		$("#storeType").combobox({
			valueField:'id',
			textField:'text',
			data:[{id:'',text:'请选择'},
			      {id:'0',text:'散件区',selected:'true'},
			      {id:'1',text:'整件区'},
			      {id:'4',text:'混合区'}]
		});
		$("#productLineId").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectProductLine.mmx',
			valueField:'id',
			textField:'text'
		});
		$("#type").combobox({
			valueField:'id',
			textField:'text',
			data:[{id:'',text:'请选择',selected:'true'},
			      {id:'0',text:'普通'},
			      {id:'1',text:'热销'},
			      {id:'2',text:'滞销'}]
		});
		$("#storageId").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?areaId='+4,
			valueField:'id',
			textField:'code',
			onSelect:function(rec){
				$("#stockAreaId").combobox({
					url:'<%=request.getContextPath()%>/CargoController/querySelectstockAreas.mmx?storageId='+rec.id,
					valueField:'id',
					textField:'text'
				});
			}
		});
		$("#zccargoList").datagrid({
			title:"无锡货位列表",
			iconCls:'icon-ok',
			width:'100%',
			height:500,
			pageNumber:1,
			pageSize:10,
			fitColumns:true,
			pageList:[5,10,15,20],
			queryParams:{areaId:4,storeType:0,status:0},
			url:'<%=request.getContextPath()%>/CargoController/zcCargoList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
// 			frozenColumns:[[
// 			                {field:'shelf_id',title:'shelf_id',hidden:true},
// 			                {field:'del_flag',title:'del_flag',hidden:true}
// 			                ]],
			columns:[[
			        {field:'cargo_info_wholecode',title:'货位号',width:100,align:'center'},
			        {field:'product_line_name',title:'货位产品线',width:80,align:'center'},
			        {field:'product_code',title:'产品编号',width:60,align:'center'},
			        {field:'product_oriname',title:'产品原名称',width:80,align:'center'},
			        {field:'cargo_product_stock_stockcount',title:'当前货位库存（其中冻结量）',width:120,align:'center'},
			        {field:'cartonning_info_code',title:'装箱单号',width:120,align:'center'},
			        {field:'cartonning_product_info_productcount',title:'装箱数量',width:80,align:'center'},
			        {field:'cargo_info_spacelockcount',title:'货位空间冻结',width:80,align:'center'},
			        {field:'cargo_info_warnstockcount',title:'货位警戒线',width:80,align:'center'},
			        {field:'cargo_info_maxstockcount',title:'货位最大容量',width:80,align:'center'},
			        {field:'cargo_info_storetypename',title:'存放类型',width:60,align:'center'},
			        {field:'cargo_info_typename',title:'货位类型',width:80,align:'center'},
			        {field:'cargo_info_statusname',title:'货位状态',width:60,align:'center'},
			        {field:'cargo_info_remark',title:'备注',width:80,align:'center'},
			        {field:'cargo_product_stock_op',title:'未完成作业单数',width:80,align:'center'},
			        {field:'control',title:'操作',width:60,align:'center'},
			        {field:'query',title:'进销存',width:60,align:'center'}
			        ]]
		});
		var p = $("#zccargoList").datagrid('getPager');
		p.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
		$("#queryZccargo").click(function(){
			var regex = /^[0-9]*$/;
			var regex2 = /^[0-9]{2}$/;
			var stockCountStart = $("#stockCountStart").val();
			if(stockCountStart&&!regex.test(stockCountStart)){
				$.messager.show({
					title:'提示',
					msg:'货位当前库存，输入内容必须是数字，请重新输入！',
					timeout:3000,
					showType:'slide'
				});
				return;
			}
			var stockCountEnd = $("#stockCountEnd").val();
			if(stockCountEnd&&!regex.test(stockCountEnd)){
				$.messager.show({
					title:'提示',
					msg:'货位当前库存，输入内容必须是数字，请重新输入！',
					timeout:3000,
					showType:'slide'
				});
				return;
			}
			var shelfCode = $("#shelfCode").val();
			if(shelfCode&&!regex2.test(shelfCode)){
				$.messager.show({
					title:'提示',
					msg:'货架代号，输入内容必须是两位数字，请重新输入！',
					timeout:3000,
					showType:'slide'
				});
				return;
			}
			var floorNum = $("#floorNum").val();
			if(floorNum&&!regex.test(floorNum)){
				$.messager.show({
					title:'提示',
					msg:'货架层数，输入内容必须是数字，请重新输入！',
					timeout:3000,
					showType:'slide'
				});
				return;
			}
			var wholeCode = $("#wholeCode").val();
			var productCode = $("#productCode").val();
			var storeType = $("#storeType").combobox('getValue');
			var productLineId = $("#productLineId").combobox('getValue');
			var productName = $("#productName").val();
			var type = $("#type").combobox('getValue');
			var storageId = $("#storageId").combobox('getValue');
			var stockAreaId = $("#stockAreaId").combobox('getValue');
			var cartonningCode = $("#cartonningCode").val();
			var status="";
			$("input[name='status']:checked").each(function(i,n){
				status +=$(this).val()+",";
			});
			var temp = "wholeCode="+wholeCode+"&productCode="+productCode+"&stockCountStart="+stockCountStart
						+"&stockCountEnd="+stockCountEnd+"&storeType="+storeType
						+"&productLineId="+productLineId+"&productName="+productName+"&type="+type+"&storageId="+storageId
						+"&stockAreaId="+stockAreaId+"&shelfCode="+shelfCode+"&floorNum="+floorNum+"&areaId="+4
						+"&cartonningCode="+cartonningCode+"&status="+status;
			$("#page_hidden").val(temp);
			$("#zccargoList").datagrid({
				url:'<%=request.getContextPath()%>/CargoController/zcCargoList.mmx',
				queryParams:{
					wholeCode:wholeCode,
					productCode:productCode,
					stockCountStart:stockCountStart,
					stockCountEnd:stockCountEnd,
					storeType:storeType,
					productLineId:productLineId,
					productName:productName,
					type:type,
					storageId:storageId,
					stockAreaId:stockAreaId,
					shelfCode:shelfCode,
					floorNum:floorNum,
					areaId:4,
					cartonningCode:cartonningCode,
					status:status
					}
			});
			var p2 = $("#zccargoList").datagrid('getPager');
			p2.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
		$("#export").click(function(){
			window.location='<%=request.getContextPath()%>/CargoController/cargoListPrint.mmx?'+$("#page_hidden").val();
		});
	});
</script>
</html>