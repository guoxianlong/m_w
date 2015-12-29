<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>仓库区域列表</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>仓库区域列表</legend>
			<table>
				<tr>
					<td><span style="font-size: 12px;">所属城市：</span><input name="citys" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="citys"></td>
					<td><span style="font-size: 12px;">所属地区：</span><input name="areas" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="areas"></td>
					<td></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">所属仓库：</span><input name="storages" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="storages"></td>
					<td><span style="font-size: 12px;">区域代号：</span><input name="codes" id="codes"></td>
					<td><a id="queryStockarea" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a></td>
				</tr>
			</table>
<!-- 			所属城市：<input name="citys" class="easyui-combobox" id="citys">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属地区：<input name="areas" style="width:80px;" class="easyui-combobox" id="areas">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属仓库：<input name="storages" style="width:120px;" class="easyui-combobox" id="storages">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			区域代号：<input name="codes" id="codes">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			<a id="queryStockarea" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a> -->
		</fieldset>
		<div style="padding: 5px;">
		<a id="add" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加仓库区域</a>
		<a id="delete" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除仓库区域</a>
		</div>
		<table id="stockareaList"></table>
	
	<div id="addstockarea" style="padding:5px;width:400px;height:250px;">
		<table>
			<tr>
				<td>
					所属仓库<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="storageIds" id="storageIds" style="width: 88px;">
				</td>
			</tr>
			<tr>
				<td>
					库存类型<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="stockTypes" id="stockTypes" style="width: 88px;">
				</td>			
			</tr>
			<tr>
				<td>
					区域代号<font color="red">*</font>：<input type="text" id="newStockAreaCode" name="newStockAreaCode" size=14>（1位大写字母）<br/>
				</td>
			</tr>
			<tr>
				<td>
					区域名称<font color="red">*</font>：<input type="text" id="stockAreaName" name="stockAreaName" size=14>(允许输入至多10个汉字)<br/>
				</td>
			</tr>
			<tr>
				<td>
					巷道个数<font color="red">*</font>：<input type="text" id="passageCount" name="passageCount" size=14/>(允许输入两位纯数字)<br/>
					<input type="hidden" id="page_hidden" value="">
				</td>
			</tr>
		</table>
	</div>
	</div>
</body>
<script type="text/javascript">
	var combobox;
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		$("#addstockarea").hide();
		combobox = $("#citys").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=choice',
			valueField:'id',
			textField:'text',
			onSelect:function(rec){
				$("#storages").combobox("loadData",[{id:'',text:''}]);
				$("#storages").combobox("clear");
				$("#areas").combobox({
					url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+rec.id,
					valueField:'id',
					textField:'text',
					onSelect:function(rec2){
						$("#storages").combobox({
							url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?areaId='+rec2.id,
							valueField:'id',
							textField:'text'
						});
					}
				});
			}
		});
		datagrid = $("#stockareaList").datagrid({
			title:"仓库区域列表",
			iconCls:'icon-ok',
			fitColumns:true,
			width:600,
			height:500,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cargoInfoStockAreaList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			frozenColumns:[[
			                {field:'stock_area_id',title:'stockAreaId',hidden:true}
			                ]],
			columns:[[
			        {field:'area_code',title:'区域代号',width:80,align:'center'},
			        {field:'storage_code',title:'所属仓库代号',width:80,align:'center'},
			        {field:'area_name',title:'区域名称',width:80,align:'center'},
			        {field:'stock_type',title:'库存类型',width:80,align:'center'},
			        {field:'passage_count',title:'巷道数',width:100,align:'center'}
			        ]]
		});
		// 条件查询
		$("#queryStockarea").click(function(){
			var city = $("#citys").combobox('getValue');//获取选中下拉选的值
			var area = $("#areas").combobox('getValue');//获取选中下拉选的值
			var storage = $("#storages").combobox('getValue');//获取选中下拉选的值
			var code = $("#codes").val();
			$("#page_hidden").val("cityId="+city+"&areaId="+area+"&storageId="+storage+"&stockAreaCode="+code);
			datagrid = $("#stockareaList").datagrid({//这里类似于重新加载datagrid传了参数
				queryParams:{cityId:city,areaId:area,storageId:storage,
						stockAreaCode:code},
				url:'<%=request.getContextPath()%>/CargoController/cargoInfoStockAreaList.mmx'
			});
			var p2 = $("#stockareaList").datagrid('getPager');
			p2.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
		var p = $("#stockareaList").datagrid('getPager');
		p.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
		//增加仓库区域
		$("#add").click(function(){
			$("#addstockarea").show();
			$("#storageIds").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?choice=all',
				valueField:'id',
				textField:'code'
			});
			$("#stockTypes").combobox({
				valueField:'value',
				textField:'text',
				data:[{value:'0',text:'合格库',selected:'true'},
				      {value:'1',text:'待验库'},
				      {value:'4',text:'退货库'},
				      {value:'3',text:'返厂库'},
				      {value:'2',text:'维修库'},
				      {value:'5',text:'残次品库'},
				      {value:'6',text:'样品库'},
				      {value:'9',text:'售后库'}]
			});
			$("#addstockarea").dialog({
				title:'添加仓库区域',
				buttons:[{
					text:'保存',
					iconCls:'icon-ok',
					handler:function(){
						addstockarea();
					}
				},{
					text:'取消',
					iconCls:'icon-cancel',
					handler:function(){
						$("#addstockarea").dialog('close');
					}
				}]
			});
		});
		//删除仓库区域
		$("#delete").click(function(){
			var node = datagrid.datagrid('getSelected');
			if(!node){
				$.messager.show({
					title:'温馨提示',
					msg:'请选择要进行删除操作的记录！',
					timeout:3000,
					showType:'slide'
				});
				return;
			}
			if(node&&node.passage_count=="0"){//判断是否可以进行删除操作
				$.messager.confirm('询问','如果确认删除，请单击确定，反之，请单击取消!',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/deleteStockArea.mmx',
							data:{stockAreaId:node.stock_area_id},
							cache:false,
							dataType:'text',
							success:function(result){
								var re = eval('('+result+')');
								if(re['result']=="success"){
									datagrid = $("#stockareaList").datagrid({//这里类似于重新加载datagrid传了参数
										url:'<%=request.getContextPath()%>/CargoController/cargoInfoStockAreaList.mmx?'+$("#page_hidden").val()
									});
									var p2 = $("#stockareaList").datagrid('getPager');
									p2.pagination({
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
					title:'温馨提示',
					msg:'该仓库区域下已添加货架，不能删除！',
					showType:'slide'
				});
			}
		});
	});
	//增加仓库区域
	function addstockarea(){
		var storageId = $("#storageIds").combobox('getValue');
		var stockType = $("#stockTypes").combobox('getValue');
		var newStockAreaCode = $("#newStockAreaCode").val();
		var stockAreaName = $("#stockAreaName").val();
		var passageCount = $("#passageCount").val();
		
		var data = {storageId:storageId,stockType:stockType,
					newStockAreaCode:newStockAreaCode,
					stockAreaName:stockAreaName,
					passageCount:passageCount};
		$.ajax({
			type:'post',//这个必须加，不加的话中文会出现乱码
			data:data,
			cache:false,
			url:'<%=request.getContextPath()%>/CargoController/addStockArea.mmx',
			dataType:'text',
			success:function(dd){
				var re = eval('('+dd+')');
				if(re['result']=="success"){
					datagrid = $("#stockareaList").datagrid({//这里类似于重新加载datagrid传了参数
						url:'<%=request.getContextPath()%>/CargoController/cargoInfoStockAreaList.mmx?'+$("#page_hidden").val()
					});
					var p3 = $("#stockareaList").datagrid('getPager');
					p3.pagination({
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
</script>
</html>