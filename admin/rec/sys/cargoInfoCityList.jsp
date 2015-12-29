<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<title>城市列表</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="padding: 5px;">
		<a id="add" class="easyui-linkbutton" data-options="iconCls:'easyui-add'">添加城市</a>
		<a id="delete" class="easyui-linkbutton" data-options="iconCls:'easyui-cancel'">删除城市</a>
	</div>
	<table id="cityList"></table>
	<div id="addcity" style="padding:5px;width:400px;height:200px;top:150px">
		<table>
			<tr>
				<td>
					<span style="font-size: 12px;">城市代号</span><font color="red">*</font>：<input id="code" type="text" name="code" size=5/><span style="font-size: 12px;">(城市名称前两个字的拼音的首字母大写)</span>
				</td>
			</tr>
			<tr>
				<td>
					<span style="font-size: 12px;">城市名称</span><font color="red">*</font>：<input id="name" type="text" name="name" size=15/><span style="font-size: 12px;">(允许输入至多10个汉字)</span>
				</td>			
			</tr>
		</table>
	</div>
</body>
<script type="text/javascript">
	var datagrid;
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		$("#addcity").hide();
		datagrid = $("#cityList").datagrid({
			title:"城市列表",
			iconCls:'icon-ok',
			width:295,
			height:'auto',
			pageNumber:1,
			fitColumns:true,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cargoInfoCityList.mmx',
			showFooter:true,
			nowrap:false,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			remoteSort:false,
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			frozenColumns:[[
			                {field:'city_id',title:'id',hidden:true},
			                {field:'city_areaCount',title:'areaCount',hidden:true}
			                ]],
			columns:[[
			        {field:'city_code',title:'城市代号',width:80,align:'center'},
			        {field:'city_name',title:'城市名称',width:80,align:'center'},
			        {field:'city_count',title:'下属地区个数',width:100,align:'center'}
			        ]]
		});
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
			if(node&&node.city_areaCount=="0"){//判断是否可以进行删除操作
				$.messager.confirm('询问','如果确认删除，请单击确定，反之，请单击取消!',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/deleteCity.mmx',
							data:{cityId:node.city_id},
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
		$("#add").click(function(){
			$("#addcity").show();
			$("#addcity").dialog({
				title:'添加城市',
				buttons:[{
					text:'保存',
					iconCls:'icon-ok',
					handler:function(){
						addCity();
					}
				},{
					text:'取消',
					iconCls:'icon-cancel',
					handler:function(){
						$("#addcity").dialog('close');
					}
				}]
			});
		});
	});
	function addCity(){
		var code = $("#code").val();
		var name = $("#name").val();
		var data = {code:code,name:name};
		$.ajax({
			type:'post',//这个必须加，不加的话中文会出现乱码
			data:data,
			cache:false,
			url:'<%=request.getContextPath()%>/CargoController/addCity.mmx',
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
</body>
</html>