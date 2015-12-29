<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend >地区列表</legend>
			<span style="font-size: 12px;">所属城市：</span>
			<input name="citys" class="easyui-combobox" editable="false" id="citys" style="width:152px; border:1px solid #ccc">
			<a id="queryArea" class="easyui-linkbutton" iconCls="icon-search" plain="true">查询</a>
		</fieldset>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="add">添加地区</a>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="delete">删除地区</a>
	<table id="areaList"></table>
	<div id="addarea" style="padding:5px;width:450px;height:200px;">
		<table>
			<tr>
				<td>
					所属城市<font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="citys2" id="citys2" style="width: 88px;">
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;地&nbsp;区&nbsp;id&nbsp;&nbsp;：<input type="text" name="areaId" id="areaId">
				</td>			
			</tr>
			<tr>
				<td>
					地区代号<font color="red">*</font>：<input type="text" id="areaCode" name="areaCode" size=14>(地区名称第一个字的拼音的首字母大写)
				</td>
			</tr>
			<tr>
				<td>
					地区名称<font color="red">*</font>：<input type="text" id="areaName" name="areaName" size=14>(允许输入至多10个汉字)
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
		$("#addarea").hide();
		combobox = $("#citys").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=all',
			valueField:'id',
			textField:'text'
		});
		datagrid = $("#areaList").datagrid({
			title:"地区列表",
			iconCls:'icon-ok',
			width:375,
			height:'auto',
			pageNumber:1,
			pageSize:20,
			fitColumns:true,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cargoInfoAreaList.mmx',
			showFooter:true,
			nowrap:false,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			frozenColumns:[[
			                {field:'area_id',title:'id',hidden:true}
			                ]],
			columns:[[
			        {field:'area_code',title:'地区代号',width:80,align:'center'},
			        {field:'area_name',title:'地区名称',width:80,align:'center'},
			        {field:'area_belong_city',title:'所属城市',width:80,align:'center'},
			        {field:'area_count',title:'下属仓库个数',width:100,align:'center'}
			        ]]
		});
		// 条件查询
		$("#queryArea").click(function(){
			var city = $("#citys").combobox('getValue');//获取选中下拉选的值
			datagrid = $("#areaList").datagrid({//这里类似于重新加载datagrid传了参数
				queryParams:{cityId:city},
				url:'<%=request.getContextPath()%>/CargoController/cargoInfoAreaList.mmx'
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
			if(node&&node.area_count=="0"){//判断是否可以进行删除操作
				$.messager.confirm('询问','如果确认删除，请单击确定，反之，请单击取消!',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/deleteArea.mmx',
							data:{areaId:node.area_id},
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
					msg:'所选择城市有下属地区，不能删除！',
					showType:'slide'
				});
			}
		});
		//添加地区
		$("#add").click(function(){
			$("#addarea").show();
			//以下是获取combobox的数据
// 			var cc = combobox.combobox('getData');
// 			var arr = new Array();
// 			for(var i=0;i<cc.length;i++){
// 				arr[i] = cc[i].id;
// 			}
			var city = $("#citys").combobox('getValue');
			//$("#citys2").combobox('setValues',arr);
			$("#citys2").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=choice&id='+city,
				valueField:'id',
				textField:'text'
			});
			$("#addarea").dialog({
// 				modal:true,//遮罩层
				title:'添加地区',
				buttons:[{
					text:'保存',
					iconCls:'icon-ok',
					handler:function(){
						addArea();
					}
				},{
					text:'取消',
					iconCls:'icon-cancel',
					handler:function(){
						$("#addarea").dialog('close');
					}
				}]
			});
		});
	});
	function addArea(){
		var cityId = $("#citys2").combobox('getValue');
		var areaId = $("#areaId").val();
		var areaCode = $("#areaCode").val();
		var areaName = $("#areaName").val();
		
		var data = {cityId:cityId,areaId:areaId,
					areaCode:areaCode,areaName:areaName};
		$.ajax({
			type:'post',//这个必须加，不加的话中文会出现乱码
			data:data,
			cache:false,
			url:'<%=request.getContextPath()%>/CargoController/addArea.mmx',
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