<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
<html>
<head>
<title>货架列表</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include></head>
<body>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>查询栏</legend>
			<table>
				<tr>
					<td><span style="font-size: 12px;">所属城市：</span><input name="citys" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="citys"></td>
					<td><span style="font-size: 12px;">所属地区：</span><input name="areas" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="areas"></td>
					<td><span style="font-size: 12px;">所属仓库：</span><input name="storages" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="storages"></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">所属区域：</span><input name="stockAreas" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="stockAreas"></td>
					<td><span style="font-size: 12px;">所属巷道：</span><input name="passages" style="width:152px; border:1px solid #ccc" class="easyui-combobox" editable="false" id="passages"></td>
					<td></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">区域代号：</span><input name="stockAreaCode" id="stockAreaCode"><span style="font-size: 12px;">（如：GZF07-A）</span></td>
					<td><span style="font-size: 12px;">货架代号：</span><input name="shelfCode" id="shelfCode"><span style="font-size: 12px;">（如：GZF07-A0101）</span></td>
					<td><a id="queryShelf" class="easyui-linkbutton" data-options="iconCls:'icon-search'" plain="true">查询</a>
						<input type="hidden" id="page_hidden"></td>
				</tr>
			</table>
<!-- 			所属城市：<input name="citys" class="easyui-combobox" id="citys">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属地区：<input name="areas" style="width:80px;" class="easyui-combobox" id="areas">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属仓库：<input name="storages" style="width:120px;" class="easyui-combobox" id="storages">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属区域：<input name="stockAreas" style="width:120px;" class="easyui-combobox" id="stockAreas">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			所属巷道：<input name="passages" style="width:120px;" class="easyui-combobox" id="passages">&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			区域代号：<input name="stockAreaCode" id="stockAreaCode">（如：GZF07-A）&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			货架代号：<input name="shelfCode" id="shelfCode">（如：GZF07-A0101）&nbsp;&nbsp;&nbsp;&nbsp; -->
<!-- 			<a id="queryShelf" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a> -->
		</fieldset>
		<div style="padding: 5px;">
		<a id="add" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加新货架</a>
		<a id="delete" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除货架</a>
		</div>
		<table id="shelfList"></table>
	
	<div id="addshelf" style="padding:5px;width:400px;height:300px;">
		<table>
			<tr>
				<td>
					<span>所属仓库</span><font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="storageIds" id="storageIds" style="width: 88px;">
				</td>
			</tr>
			<tr>
				<td>
					<span>所属区域</span><font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" name="stockArea2" id="stockArea2" style="width: 88px;">
				</td>			
			</tr>
			<tr>
				<td>
					<span>&nbsp;&nbsp;巷&nbsp;&nbsp;道&nbsp;&nbsp;</span><font color="red">*</font>：<input type="text" class="easyui-combobox" editable="false" id="newPassageId" name="newPassageId" style="width: 88px;">
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;货&nbsp;架&nbsp;号&nbsp;&nbsp;：<input type="text" name="newShelfCode" size=10 id="newShelfCode"/>
				</td>
			</tr>
			<tr>
				<td>
					货架层数<font color="red">*</font>：共<input name="floorCount" id="floorCount" type="text" size=3/>层
				</td>
			</tr>
			<tr>
				<td>
					本次添加货架个数<font color="red">*</font>：共<input type="text" name="shelfCount" id="shelfCount" size=3/>个货架
				</td>
			</tr>
		</table>
	</div>
	</div>
</body>
<script type="text/javascript">
	var datagrid; 
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		$("#addshelf").hide();
		combobox = $("#citys").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectCitys.mmx?flag=choice',
			valueField:'id',
			textField:'text',
			onSelect:function(rec){
				$("#storages").combobox("loadData",[{id:'',text:''}]);
				$("#storages").combobox("clear");
				$("#stockAreas").combobox("loadData",[{id:'',text:''}]);
				$("#stockAreas").combobox("clear");
				$("#passages").combobox("loadData",[{id:'',text:''}]);
				$("#passages").combobox("clear");
				$("#areas").combobox({
					url:'<%=request.getContextPath()%>/CargoController/querySelectAreas.mmx?cityId='+rec.id,
					valueField:'id',
					textField:'text',
					onSelect:function(rec2){
						$("#stockAreas").combobox("loadData",[{id:'',text:''}]);
						$("#stockAreas").combobox("clear");
						$("#passages").combobox("loadData",[{id:'',text:''}]);
						$("#passages").combobox("clear");
						$("#storages").combobox({
							url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?areaId='+rec2.id,
							valueField:'id',
							textField:'text',
							onSelect:function(rec3){
								$("#passages").combobox("loadData",[{id:'',text:''}]);
								$("#passages").combobox("clear");
								$("#stockAreas").combobox({
									url:'<%=request.getContextPath()%>/CargoController/querySelectstockAreas.mmx?storageId='+rec3.id,
									valueField:'id',
									textField:'text',
									onSelect:function(rec4){
										$("#passages").combobox({
											url:'<%=request.getContextPath()%>/CargoController/querySelectPassage.mmx?stockAreaId='+rec4.id,
											valueField:'id',
											textField:'text'
										});
									}
								});
							}
						});
					}
				});
			}
		});
		//查询
		datagrid = $("#shelfList").datagrid({
			title:"货架列表",
			iconCls:'icon-ok',
			width:560,
			height:'auto',
			fitColumns:true,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cargoInfoShelfList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			frozenColumns:[[
			                {field:'shelf_id',title:'shelf_id',hidden:true},
			                {field:'del_flag',title:'del_flag',hidden:true}
			                ]],
			columns:[[
			        {field:'shelf_code',title:'货架号',width:80,align:'center'},
			        {field:'stock_area_code2',title:'所属区域',width:80,align:'center'},
			        {field:'shelf_whole_code',title:'巷道号',width:80,align:'center'},
			        {field:'shelf_stock_type_name',title:'库存类型',width:80,align:'center'},
			        {field:'shelf_floor_count',title:'货架层数',width:80,align:'center'},
			        {field:'shelf_shelf_floor',title:'货位数/层',align:'center'}
			        ]]
		});
		// 条件查询
		$("#queryShelf").click(function(){
			var city = $("#citys").combobox('getValue');//获取选中下拉选的值
			var area = $("#areas").combobox('getValue');
			var storage = $("#storages").combobox('getValue');
			var stockAreaId = $("#stockAreas").combobox('getValue');
			var passageId = $("#passages").combobox('getValue');
			var stockAreaCode = $("#stockAreaCode").val();
			var shelfCode = $("#shelfCode").val();
			var temp = "cityId="+city+"&areaId="+area+"&storageId="+storage+"&stockAreaId="+stockAreaId+"&passageId="+passageId
						+"&stockAreaCode="+stockAreaCode+"&shelfCode="+shelfCode;
			$("#page_hidden").val(temp);
			datagrid = $("#shelfList").datagrid({//这里类似于重新加载datagrid传了参数
				queryParams:{cityId:city,areaId:area,storageId:storage,
							stockAreaId:stockAreaId,passageId:passageId,
							stockAreaCode:stockAreaCode,shelfCode:shelfCode},
				url:'<%=request.getContextPath()%>/CargoController/cargoInfoShelfList.mmx'
			});
			var p2 = $("#shelfList").datagrid('getPager');
			p2.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
		var p = $("#shelfList").datagrid('getPager');
		p.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
		//增加新货架
		$("#add").click(function(){
			$("#addshelf").show();
			$("#storageIds").combobox({
				url:'<%=request.getContextPath()%>/CargoController/querySelectStorages.mmx?choice=all',
				valueField:'id',
				textField:'code',
				onSelect:function(rec){
					$("#newPassageId").combobox('loadData',[{id:'',text:''}]);
					$("#newPassageId").combobox('clear');
					$("#stockArea2").combobox({
						url:'<%=request.getContextPath()%>/CargoController/querySelectstockAreas.mmx?storageId='+rec.id,
						valueField:'id',
						textField:'text',
						onSelect:function(rec2){
							$("#newPassageId").combobox({
								url:'<%=request.getContextPath()%>/CargoController/querySelectPassage.mmx?stockAreaId='+rec2.id,
								valueField:'id',
								textField:'text'
							});
						}
					});
				}
			});
			$("#addshelf").dialog({
				title:'添加新货架',
				buttons:[{
					text:'保存',
					iconCls:'icon-ok',
					handler:function(){
						addshelf();
					}
				},{
					text:'取消',
					iconCls:'icon-cancel',
					handler:function(){
						$("#addshelf").dialog('close');
					}
				}]
			});
		});
		//删除货架
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
			if(node.del_flag=="true"){//判断是否可以进行删除操作
				$.messager.confirm('询问','如果确认删除，请单击确定，反之，请单击取消!',function(boo){
					if(boo){
						//选择true
						$.ajax({
							url:'<%=request.getContextPath()%>/CargoController/deleteShelf.mmx',
							data:{shelfId:node.shelf_id},
							cache:false,
							dataType:'text',
							success:function(result){
								var re = eval('('+result+')');
								if(re['result']=="success"){
									datagrid = $("#shelfList").datagrid({//这里类似于重新加载datagrid传了参数
										url:'<%=request.getContextPath()%>/CargoController/cargoInfoShelfList.mmx?'+$("#page_hidden").val()
									});
									var p = $("#shelfList").datagrid('getPager');
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
					msg:'该货架下已添加货位，不能删除！',
					showType:'slide'
				});
			}
		});
	});
	
	//增加新货架
	function addshelf(){
		var storageId = $("#storageIds").combobox('getValue');
		var stockAreaId = $("#stockArea2").combobox('getValue');
		var passageId = $("#newPassageId").combobox('getValue');
		var newShelfCode = $("#newShelfCode").val();
		var floorCount = $("#floorCount").val();
		var shelfCount = $("#shelfCount").val();
		var data = {storageId:storageId,stockAreaId:stockAreaId,
					passageId:passageId,newShelfCode:newShelfCode,
					floorCount:floorCount,shelfCount:shelfCount};
		$.ajax({
			type:'post',//这个必须加，不加的话中文会出现乱码
			data:data,
			cache:false,
			url:'<%=request.getContextPath()%>/CargoController/addShelf.mmx',
			dataType:'text',
			success:function(dd){
				var re = eval('('+dd+')');
				if(re['result']=="success"){
					datagrid = $("#shelfList").datagrid({//这里类似于重新加载datagrid传了参数
						url:'<%=request.getContextPath()%>/CargoController/cargoInfoShelfList.mmx?'+$("#page_hidden").val()
					});
					var p = $("#shelfList").datagrid('getPager');
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