<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<title>仓库列表</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>仓库列表</legend>
			<span style="font-size: 12px;">所属城市：</span>
			<input name="citys" class="easyui-combobox" editable="false" id="citys" style="width: 120px;">
			<span style="font-size: 12px;">所属地区：</span><input name="areas" class="easyui-combobox" editable="false" id="areas" style="width: 120px;">
			<a id="queryStorage" class="easyui-linkbutton" iconCls="icon-search" plain="true" >查询</a>
		</fieldset>
		<div style="padding: 5px;">
		<a id="add" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加仓库</a>
		<a id="delete" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除仓库</a>
	</div>
	<table id="storageList"></table>
	<div id="addstorage" style="padding:5px;width:400px;height:200px;">
		<table>
			<tr>
				<td>
					所属城市<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="citys2" id="citys2" style="width: 88px;">
				</td>
			</tr>
			<tr>
				<td>
					所属地区<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="areaId" id="areas2" style="width: 88px;">
				</td>			
			</tr>
			<tr>
				<td>
					仓库代号<font color="red">*</font>：<input type="text" name="storageCode" id="storageCode" size=14>(2位数字组成)
				</td>
			</tr>
			<tr>
				<td>
					仓库名称<font color="red">*</font>：<input type="text" name="storageName" id="storageName" size=14>(允许输入至多10个汉字)
				</td>
			</tr>
		</table>
	</div>
	</div>
</body>
<script type="text/javascript">
	var datagrid;
	var combobox;
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		$("#addstorage").hide();
		combobox = $("#citys").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=choice',
			valueField:'id',
			textField:'text',
			onSelect:function(rec){
				$("#areas").combobox({
					url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+rec.id,
					valueField:'id',
					textField:'text'
				});
			}
		});
		$("#citys").change(function(){
			$("#areas").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+$("#citys").combobox('getValue'),
				valueField:'id',
				textField:'text'
			});
		});
		datagrid = $("#storageList").datagrid({
			title:"仓库列表",
			iconCls:'icon-ok',
			width:460,
			height:'auto',
			fitColumns:true,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cargoInfoStorageList.mmx',
			showFooter:true,
			nowrap:false,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			frozenColumns:[[
			                {field:'storage_id',title:'id',hidden:true}
			                ]],
			columns:[[
			        {field:'storage_code',title:'仓库代号',width:80,align:'center'},
			        {field:'storage_name',title:'地区名称',width:80,align:'center'},
			        {field:'storage_belong_area',title:'所属地区',width:80,align:'center'},
			        {field:'storage_belong_city',title:'所属城市',width:80,align:'center'},
			        {field:'storage_count',title:'仓库内区域个数',width:100,align:'center'}
			        ]]
		});
		// 条件查询
		$("#queryStorage").click(function(){
			var city = $("#citys").combobox('getValue');//获取选中下拉选的值
			var area = $("#areas").combobox('getValue');//获取选中下拉选的值
			datagrid = $("#storageList").datagrid({//这里类似于重新加载datagrid传了参数
				queryParams:{cityId:city,areaId:area},
				url:'<%=request.getContextPath()%>/CargoController/cargoInfoStorageList.mmx'
			});
		});
		// 删除
		$("#delete").click(function(){
// 			$('a.easyui-linkbutton').linkbutton('enable');
			var node = datagrid.datagrid('getSelected');
			if(!node){
				$.messager.show({
					title:'温馨提示',
					msg:'请选择要进行删除的记录！',
					timeout:3000,
					showType:'slide'
				});
				return;
			}
			if(node&&node.storage_count=="0"){//判断是否可以进行删除操作
				$.messager.confirm('询问','如果确认删除，请单击确定，反之，请单击取消!',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/deleteStorage.mmx',
							data:{storageId:node.storage_id},
							cache:false,
							dataType:'text',
							success:function(result){
								var re = eval('('+result+')');
								if(re['result']=="success"){
									datagrid.datagrid('reload');
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
					msg:'该仓库下已添加仓库区域，不能删除！',
					showType:'slide'
				});
			}
		});
		//添加仓库
		$("#add").click(function(){
			$("#addstorage").show();
			//以下是获取combobox的数据
// 			var cc = combobox.combobox('getData');
// 			var arr = new Array();
// 			for(var i=0;i<cc.length;i++){
// 				arr[i] = cc[i].id;
// 			}
			var city = $("#citys").combobox('getValue');
			var area = $("#areas").combobox('getValue');
			//$("#citys2").combobox('setValues',arr);
			$("#citys2").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=choice&id='+city,
				valueField:'id',
				textField:'text',
				onSelect:function(rec){
					$("#areas2").combobox({
						url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+rec.id,
						valueField:'id',
						textField:'text'
					});
				}
			});
			$("#areas2").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+$("#citys").combobox('getValue'),
				valueField:'id',
				textField:'text'
			});
			$("#addstorage").dialog({
				title:'添加仓库',
				buttons:[{
					text:'保存',
					iconCls:'icon-ok',
					handler:function(){
						addstorage();
					}
				},{
					text:'取消',
					iconCls:'icon-cancel',
					handler:function(){
						$("#addstorage").dialog('close');
					}
				}]
			});
		});
	});
	function addstorage(){
		var cityId = $("#citys2").combobox('getValue');
		var areaId = $("#areas2").combobox('getValue');
		var storageCode = $("#storageCode").val();
		var storageName = $("#storageName").val();
		
		var data = {cityId:cityId,areaId:areaId,
					storageCode:storageCode,
					storageName:storageName};
		$.ajax({
			type:'post',//这个必须加，不加的话中文会出现乱码
			data:data,
			cache:false,
			url:'<%=request.getContextPath()%>/CargoController/addStorage.mmx',
			dataType:'text',
			success:function(dd){
				var re = eval('('+dd+')');
				if(re['result']=="success"){
					parent.datagrid.datagrid('reload');
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