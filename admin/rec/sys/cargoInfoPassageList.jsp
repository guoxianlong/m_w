<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>巷道列表</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="width: 80%;">
		<fieldset>
			<legend>查询栏</legend>
			<table>
				<tr>
					<td><span style="font-size: 12px;">所属城市：</span><input name="citys" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="citys"></td>
					<td><span style="font-size: 12px;">所属地区：</span><input name="areas" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="areas"></td>
					<td></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">所属仓库：</span><input name="storages" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="storages"></td>
					<td><span style="font-size: 12px;">所属区域：</span><input name="stockAreas" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="stockAreas"></td>
					<td></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">区域代号：</span><input name="stockAreaCode" id="stockAreaCode"><span style="font-size: 12px;">（如：GZF07-A）</span></td>
					<td><span style="font-size: 12px;">巷道号：</span><input name="passageCode" id="passageCode"><span style="font-size: 12px;">（如：GZF07-A01）</span></td>
					<td><a id="queryPassage" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a></td>
				</tr>
			</table>
<!-- 			所属城市：<input name="citys" class="easyui-combobox" id="citys">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属地区：<input name="areas" style="width:80px;" class="easyui-combobox" id="areas">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属仓库：<input name="storages" style="width:120px;" class="easyui-combobox" id="storages">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属区域：<input name="stockAreas" style="width:120px;" class="easyui-combobox" id="stockAreas">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			区域代号：<input name="stockAreaCode" id="stockAreaCode">（如：GZF07-A）&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			巷道号：<input name="passageCode" id="passageCode">（如：GZF07-A01）&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			<a id="queryPassage" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a> -->
		</fieldset>
		<div style="padding: 5px;">
		<a id="add" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加新巷道</a>
		<a id="delete" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除巷道</a>
		</div>
		<table id="passageList"></table>
	
	<div id="addpassage" style="padding:5px;width:400px;height:200px;">
		<table>
			<tr>
				<td>
					所属仓库<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="storageIds" id="storageIds" style="width: 88px;">
				</td>
			</tr>
			<tr>
				<td>
					所属区域<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="stockArea2" id="stockArea2" style="width: 88px;">
				</td>			
			</tr>
			<tr>
				<td>
					巷&nbsp;道&nbsp;号&nbsp;<font color="red">*</font>：<input type="text" id="newPassageCode" name="newPassageCode" size=10>
				</td>
			</tr>
			<tr>
				<td>
					货架个数<font color="red">*</font>：<input type="text" id="shelfCount" name="shelfCount" size=3/>
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
		$("#addpassage").hide();
		combobox = $("#citys").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=choice',
			valueField:'id',
			textField:'text',
			onSelect:function(rec){
				$("#storages").combobox("loadData",[{id:'',text:''}]);
				$("#storages").combobox("clear");
				$("#stockAreas").combobox("loadData",[{id:'',text:''}]);
				$("#stockAreas").combobox("clear");
				$("#areas").combobox({
					url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+rec.id,
					valueField:'id',
					textField:'text',
					onSelect:function(rec2){
						$("#stockAreas").combobox("loadData",[{id:'',text:''}]);
						$("#stockAreas").combobox("clear");
						$("#storages").combobox({
							url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?areaId='+rec2.id,
							valueField:'id',
							textField:'text',
							onSelect:function(rec3){
								$("#stockAreas").combobox({
									url:'<%=request.getContextPath()%>/CargoController/querySelectstockAreas.mmx?storageId='+rec3.id,
									valueField:'id',
									textField:'text'
								});
							}
						});
					}
				});
			}
		});
		datagrid = $("#passageList").datagrid({
			title:"巷道列表",
			iconCls:'icon-ok',
			width:353,
			height:'auto',
			fitColumns:true,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cargoInfoPassageList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			frozenColumns:[[
			                {field:'passage_id',title:'passageId',hidden:true}
			                ]],
			columns:[[
			        {field:'passage_code',title:'巷道号',width:80,align:'center'},
			        {field:'belong_area',title:'所属区域',width:80,align:'center'},
			        {field:'stock_type',title:'库存类型',width:80,align:'center'},
			        {field:'shelf_count',title:'货架数',width:80,align:'center'}
			        ]]
		});
// 		 para=(cityId.equals("")?"":"&cityId="+cityId)
//			+(areaId.equals("")?"":"&areaId="+areaId)
//			+(storageId.equals("")?"":"&storageId="+storageId)
//			+(stockAreaId.equals("")?"":"&stockAreaId="+stockAreaId)
//			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
//			+(passageCode.equals("")?"":"&passageCode="+passageCode)
		// 条件查询
		$("#queryPassage").click(function(){
			var city = $("#citys").combobox('getValue');//获取选中下拉选的值
			var area = $("#areas").combobox('getValue');
			var storage = $("#storages").combobox('getValue');
			var stockAreaId = $("#stockAreas").combobox('getValue');
			var stockAreaCode = $("#stockAreaCode").val();
			var passageCode = $("#passageCode").val();
			var temp = "cityId="+city+"&areaId="+area+"&storageId="+storage+"&stockAreaId="+stockAreaId+"&stockAreaCode="+stockAreaCode
						+"&passageCode"+passageCode;
			$("#page_hidden").val(temp);
			datagrid = $("#passageList").datagrid({//这里类似于重新加载datagrid传了参数
				queryParams:{cityId:city,areaId:area,storageId:storage,
							stockAreaId:stockAreaId,stockAreaCode:stockAreaCode,
							passageCode:passageCode},
				url:'<%=request.getContextPath()%>/CargoController/cargoInfoPassageList.mmx'
			});
			var p2 = $("#passageList").datagrid('getPager');
			p2.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
		var p = $("#passageList").datagrid('getPager');
		p.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
		//增加巷道
		$("#add").click(function(){
			$("#addpassage").show();
			$("#storageIds").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?choice=all',
				valueField:'id',
				textField:'code',
				onSelect:function(rec){
					$("#stockArea2").combobox({
						url:'<%=request.getContextPath()%>/CargoController/querySelectstockAreas.mmx?storageId='+rec.id,
						valueField:'id',
						textField:'text'
					});
				}
			});
			$("#addpassage").dialog({
				title:'添加新巷道',
				buttons:[{
					text:'保存',
					iconCls:'icon-ok',
					handler:function(){
						addpassage();
					}
				},{
					text:'取消',
					iconCls:'icon-cancel',
					handler:function(){
						$("#addpassage").dialog('close');
					}
				}]
			});
		});
		//删除巷道
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
			if(node.shelf_count=="0"){//判断是否可以进行删除操作
				$.messager.confirm('询问','如果确认删除，请单击确定，反之，请单击取消!',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/deletePassage.mmx',
							data:{passageId:node.passage_id},
							cache:false,
							dataType:'text',
							success:function(result){
								var re = eval('('+result+')');
								if(re['result']=="success"){
									datagrid = $("#passageList").datagrid({//这里类似于重新加载datagrid传了参数
										url:'<%=request.getContextPath()%>/CargoController/cargoInfoPassageList.mmx?'+$("#page_hidden").val()
									});
									var p = $("#passageList").datagrid('getPager');
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
					title:'温馨提示',
					msg:'该巷道中已添加货架，不能删除！',
					showType:'slide'
				});
			}
		});
	});
	//增加仓库区域
	function addpassage(){
		var storageId = $("#storageIds").combobox('getValue');
		var stockAreaId = $("#stockArea2").combobox('getValue');
		var newPassageCode = $("#newPassageCode").val();
		var shelfCount = $("#shelfCount").val();
		
		var data = {storageId:storageId,stockAreaId:stockAreaId,
					newPassageCode:newPassageCode,
					shelfCount:shelfCount};
		$.ajax({
			type:'post',//这个必须加，不加的话中文会出现乱码
			data:data,
			cache:false,
			url:'<%=request.getContextPath()%>/CargoController/addPassage.mmx',
			dataType:'text',
			success:function(dd){
				var re = eval('('+dd+')');
				if(re['result']=="success"){
					datagrid = $("#passageList").datagrid({//这里类似于重新加载datagrid传了参数
						url:'<%=request.getContextPath()%>/CargoController/cargoInfoPassageList.mmx?'+$("#page_hidden").val()
					});
					var p = $("#passageList").datagrid('getPager');
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
</script>
</html>