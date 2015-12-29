<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>批量开通货位</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="width: 80%;">
	<span style="font-size: 12px;">批量开通货位（仅查询未开通货位）</span>
		<fieldset style="padding:3px;height: auto;">
			<legend>查询栏</legend>
			<table>
				<tr>
					<td><span style="font-size: 12px;">货&nbsp;位&nbsp;号：</span><input type="text" style="width:150px;border:1px solid #ccc" name="wholeCode" id="wholeCode"/><span style="font-size: 12px;">左精确右模糊</span></td>
					<td><span style="font-size: 12px;">仓&nbsp;库&nbsp;号：</span><input name="storageId"  style="width:152px; border:1px solid #ccc" class="easyui-combobox"  editable="false" id="storageId"></td>
					<td><span style="font-size: 12px;">仓库区域：</span><input name="stockAreaId"  style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="stockAreaId"></td>
					<td><span style="font-size: 12px;">巷&nbsp;&nbsp;道&nbsp;&nbsp;：</span><input name="passageId"  style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="passageId"></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">货架代号：</span><input style="width:150px;border:1px solid #ccc" name="shelfCode" id="shelfCode"><span style="font-size: 12px;">（如：01）</span></td>
					<td><span style="font-size: 12px;">第&nbsp;几&nbsp;层：</span><input type="text" style="width:150px;border:1px solid #ccc" name="floorNum" id="floorNum"/></td>
					<td><span style="font-size: 12px;">库存类型：</span><input name="stockType" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="stockType"></td>
					<td><span style="font-size: 12px;">存放类型：</span><input name="storeType" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="storeType"></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">货位产品线：</span><input name="productLineId" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="productLineId"></td>
					<td><span style="font-size: 12px;">货位类型：</span><input name="type" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="type"></td>
					<td><a id="queryShelf" class="easyui-linkbutton" data-options="iconCls:'icon-search'" plain="true">查询</a><input type="hidden" id="page_hidden" value=""/></td>
				</tr>
			</table>
		</fieldset>
		<div style="padding: 5px;">
		<a id="queryOpencargo" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">批量开通货位</a>
		</div>
		<table id="opencargoList"></table>
	</div>
</body>
<script type="text/javascript">
	var datagrid ;
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		$("#storageId").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?choice=all',
			valueField:'id',
			textField:'code',
			onSelect:function(rec){
				$("#passageId").combobox("loadData",[{id:'',text:''}]);
				$("#passageId").combobox("clear");
				$("#stockAreaId").combobox({
					url:'<%=request.getContextPath()%>/CargoController/querySelectstockAreas.mmx?storageId='+rec.id,
					valueField:'id',
					textField:'text',
					onSelect:function(rec2){
						$("#passageId").combobox({
							url:'<%=request.getContextPath()%>/CargoController/querySelectPassage.mmx?stockAreaId='+rec2.id,
							valueField:'id',
							textField:'text'
						});
					}
				});
			}
		});
		$("#stockType").combobox({
			valueField:'id',
			textField:'text',
			data:[{id:'0',text:'合格库',selected:'true'},
			      {id:'1',text:'待验库'},
			      {id:'4',text:'退货库'},
			      {id:'3',text:'返厂库'},
			      {id:'2',text:'维修库'},
			      {id:'5',text:'残次品库'},
			      {id:'6',text:'样品库'},
			      {id:'9',text:'售后库'}]
		});
		$("#storeType").combobox({
			valueField:'id',
			textField:'text',
			data:[{id:'',text:'请选择',selected:'true'},
			      {id:'0',text:'散件区'},
			      {id:'1',text:'整件区'},
			      {id:'2',text:'缓存区'},
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
		$("#queryShelf").click(function(){
			var wholeCode = $("#wholeCode").val();
			var storageId = $("#storageId").combobox('getValue');
			var stockAreaId = $("#stockAreaId").combobox('getValue');
			var passageId = $("#passageId").combobox('getValue');
			var shelfCode = $("#shelfCode").val();
			var floorNum = $.trim($("#floorNum").val());
			var reg = /^\d+$/;
			if(floorNum&&!floorNum.match(reg)){
				$.messager.show({
					title:'提示',
					msg:'层数输入错误，只能输入正整数！',
					timeout:3000,
					showType:'slide'
				});
				return;
				
			}
			var stockType = $("#stockType").combobox('getValue');
			var storeType = $("#storeType").combobox('getValue');
			var productLineId = $("#productLineId").combobox('getValue');
			var type = $("#type").combobox('getValue');
			var temp = "wholeCode="+wholeCode+"&storageId="+storageId+"&stockAreaId="+stockAreaId+"&passageId="+passageId
						+"&shelfCode="+shelfCode+"&floorNum="+floorNum+"&stockType="+stockType+"&storeType="+storeType
						+"&productLineId="+productLineId+"&type="+type;
			$("#page_hidden").val(temp);
			datagrid = $("#opencargoList").datagrid({
				title:"未开通货位列表",
				iconCls:'icon-ok',
				width:700,
				height:'auto',
				fitColumns:true,
				pageNumber:1,
				pageSize:20,
				pageList:[5,10,15,20],
				url:'<%=request.getContextPath()%>/CargoController/openCargoList.mmx',
				showFooter:true,
				striped:true,
				collapsible:true,
				loadMsg:'数据加载中...',
				rownumbers:true,
				singleSelect:false,//只选择一行后变色
				pagination:true,
				queryParams:{wholeCode:wholeCode,storageId:storageId,stockAreaId:stockAreaId,passageId:passageId,
							floorNum:floorNum,shelfCode:shelfCode,stockType:stockType,storeType:storeType,
							productLineId:productLineId,type:type},
				frozenColumns:[[
				                {field:'cargo_info_cargoId',title:'货位id',hidden:true},
				                {field:'ck',checkbox:true}
				                ]],
				columns:[[
				        {field:'cargo_info_wholecode',title:'货位号',width:120,align:'center'},
				        {field:'cargo_info_productline',title:'货位产品线',width:80,align:'center'},
				        {field:'cargo_info_warnstock',title:'货位警戒线',width:80,align:'center'},
				        {field:'cargo_info_maxstock',title:'货位最大容量',width:80,align:'center'},
				        {field:'cargo_info_storetype',title:'存放类型',width:80,align:'center'},
				        {field:'cargo_info_stocktype',title:'库存类型',width:80,align:'center'},
				        {field:'cargo_info_type',title:'货位类型',align:'center'},
				        {field:'cargo_info_remark',title:'备注',align:'center'}
				        ]]
			});
			var p = $("#opencargoList").datagrid('getPager');
			p.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
		$("#queryOpencargo").click(function(){
			var items = datagrid.datagrid('getChecked');
			if(items.length!=0){
				var cargoIds ="";
				$.each(items,function(index,item){
					cargoIds+=item.cargo_info_cargoId+",";
				});
				$.messager.confirm('询问','如果确认开通，请单击确定，反之，请单击取消！',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/openCargo.mmx',
							data:{cargoId:cargoIds.substring(0,cargoIds.length-1)},
							cache:false,
							dataType:'text',
							success:function(result){
								var re = eval('('+result+')');
								if(re['result']=="success"){
									datagrid = $("#opencargoList").datagrid({//这里类似于重新加载datagrid传了参数
										url:'<%=request.getContextPath()%>/CargoController/openCargoList.mmx?'+$("#page_hidden").val()
									});
									var p = $("#opencargoList").datagrid('getPager');
									p.pagination({
										displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
										onBeforeRefresh:function(){
											$(this).pagination('loading');
											$(this).pagination('loaded');
										}
									});
								}
								$.messager.show({
									title:'结果提示',
									msg:re['tip'],
									showType:'slide'
								});
							}
						});
					}
				});
			}else{
				$.messager.show({
					title:'提示',
					msg:'请至少选择一个货位！',
					timeout:3000,
					showType:'slide'
				});
			}
		});
	});
</script>
</html>