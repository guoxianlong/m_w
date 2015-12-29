<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<html>
  <head>
    
    <title>商品物流分类优先级调整</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
  </head>
  <body>
	<table id="productWarelist">
		<thead>
			<tr>
				<th data-options="field:'product_ware_id',width:250,hidden:true">hidden</th>
				<th data-options="field:'product_ware_name',width:250,align:'center'">商品物流分类</th>
				<th data-options="field:'product_ware_sequence',width:250,editor:'text',align:'center'">优先级 </th>
			</tr>
		</thead>
	</table>
	<form id="form"></form>
  <script type="text/javascript">
	var pattern = /^[0-9]{1,10}$/;
	var pattern2 = /^[0-9]{1,}$/;
	var boo = true;
	var lastIndex;
  	$(function(){
  		$("#form").hide();
  		$("#productWarelist").datagrid({
			title:"商品物流分类优先级调整列表",
			iconCls:'icon-ok',
			width:860,
			height:'auto',
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			pagination:true,
			toolbar:[{
				text:'保存',
				iconCls:'icon-save',
				handler:function(){
					$('#productWarelist').datagrid('endEdit', lastIndex);
					lastIndex = null;
// 					判断最后一个editor
// 					if(boo&&lastIndex>=0){
// 						var namedd = $("#deptlist").datagrid('getEditor',{index:lastIndex,field:'cargo_dept_name'});
// 						var codedd = $("#deptlist").datagrid('getEditor',{index:lastIndex,field:'cargo_dept_code'});
// 						boo = checkedit($(namedd.target).val(),$(codedd.target).val());
// 					}
					var rows = $("#productWarelist").datagrid("getRows");
					var p;
					var q;
					for(var i=0;i<rows.length;i++){
						boo = checkedit(rows[i].product_ware_sequence);
						if(boo){
							q = "<input type='text' name='change' value='"+rows[i].product_ware_id+"'/>"
							p = "<input type='text' class='sequence' name='sequence_"+rows[i].product_ware_id+"' value='"+rows[i].product_ware_sequence+"'/>"
							$("#form").append(p).append(q);
						}else{
							$("input[name='change']").remove();
							$(".sequence").remove();
							return false;
						}
					}
					if(boo){
						$("#form").form('submit',({
							url:'<%=request.getContextPath()%>/productWarePropertyActionController/editProductWareTypeSequence.mmx',
							success:function(data){
								var json = eval('('+data+')');
								if(json['result']=='success'){
									$("#productWarelist").datagrid("reload");
								}
								$("input[name='change']").remove();
								$(".sequence").remove();
								$.messager.show({
									title:'提示',
									msg:json['tip'],
									timeout:3000,
									showType:'slide'
								});
							}
						}));
					}
					return boo;
				}
			}],
			onClickRow:function(rowIndex, rowData){
				if (lastIndex != rowIndex){
					$('#productWarelist').datagrid('endEdit', lastIndex);
					$('#productWarelist').datagrid('beginEdit', rowIndex);
				}
				lastIndex = rowIndex;
			},
			onAfterEdit:function(rowIndex, rowData, changes){
// 				alert(rowIndex+","+rowData+","+changes['cargo_dept_name']);
				//这里只能判断以前编辑过的，当前编辑的则需要到点击“保存”按钮时判断
				boo = checkedit(rowData['product_ware_sequence']);
			},
			url:'<%=request.getContextPath()%>/productWarePropertyActionController/getProductWareTypeInfo.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			fitColumns:true,
// 				columns:[[
// 				        {field:'cargo_dept_name',title:'部门名称',width:50,align:'center'},
// 				        {field:'cargo_dept_code',title:'部门代码',width:50,align:'center'}
// 				        ]],
			onLoadSuccess:function(data){
				$("#title").text(data['title']);
				$("#deptId").val(data['deptId']);
			}
		});
  		var p2 = $("#productWarelist").datagrid('getPager');
		p2.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
  	});
  	function checkedit(sequence){
		if(sequence==null||$.trim(sequence)==""){
			$.messager.show({
				title:'提示',
				msg:'有为空的优先级，不能提交！',
				timeout:3000,
				showType:'slide'
			});
			return false;
		}
		if(pattern.test($.trim(sequence))){
			
		}else if(pattern2.test($.trim(sequence))){
			$.messager.show({
				title:'提示',
				msg:'请不要输入大于10位的数字!',
				timeout:3000,
				showType:'slide'
			});
			return false;
		}else{
			$.messager.show({
				title:'提示',
				msg:'请填入整数！！',
				timeout:3000,
				showType:'slide'
			});
			return false;
		}
		return true;
	}
  </script>
</body>
</html>
