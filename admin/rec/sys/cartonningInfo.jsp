<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<head>
<style>
	a{
		text-decoration:underline
		}
</style>
<title>装箱记录管理</title>
</head>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div>
		<fieldset>
			<legend>创建装箱记录</legend>
			<form id="form" method="post">
				<table style="width: 100%">
					<tr>
						<td><span style="font-size: 12px;">商品编号/商品条码:</span><input id="productCode2" type="text" name="productCode2" class="easyui-validatebox" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">装箱数量:</span><input id="count"  type="text" name="count" data-options="min:1,max:99999999,editable:true,required:true" value="1" class="easyui-numberspinner"/></td>
						<td><span style="font-size: 12px;">装箱原因:</span><input name="cause" style="width:120px;" class="easyui-combobox" editable="false" id="cause" data-options="required:true"></td>
						<td><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="create">创建装箱记录</a></td>
					</tr>
				</table>
			</form>
		</fieldset>
	</div>
	<div>
		<fieldset>
			<legend>装箱记录列表</legend>
			<table style="width: 80%">
				<tr>
					<td><span style="font-size: 12px;">装箱单编号:</span><input name="select" type="text" id="select"></td>
					<td><span style="font-size: 12px;">产品编号：</span><input name="productCode" type="text" id="productCode"></td>
					<td><span style="font-size: 12px;">生成人：</span><input name="userName" type="text" id="userName"></td>
					<td><span style="font-size: 12px;">状态：</span>
						<input type="checkbox" name="status" value="0" checked="checked"/><span style="font-size: 12px;">未打印</span>
						<input type="checkbox" name="status" value="1" checked="checked"/><span style="font-size: 12px;">已生效</span>
						<input type="checkbox" name="status" value="2"/><span style="font-size: 12px;">已作废</span></td>
				</tr>
				<tr>
					<td><span style="font-size: 12px;">生成时间：&nbsp;</span><input name="startTime" style="width:152px;" id="startTime" type="text" class="easyui-datebox"></td>
					<td><span style="font-size: 12px;">到&nbsp;&nbsp;&nbsp;&nbsp;</span><input name="endTime" style="width:152px;" id="endTime" type="text" class="easyui-datebox"></td>
					<td align="center"><a id="query" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询
						<input type="hidden" id="page_hidden"></a>
				</tr>
			</table>
		</fieldset>
		<table id="queryList"></table>
	</div>
</body>
<script type="text/javascript">
	function cancel(id,code){
// 		alert(id+","+code);
		$.messager.confirm('询问','确定作废'+code+'记录？',function(boo){
			if(boo){
				$.ajax({
					url:'<%=request.getContextPath()%>/CargoController/cancelCartonningInfo.mmx',
					data:{id:id},
					cache:false,
					dataType:'text',
					success:function(result){
						var re = eval('('+result+')');
						if(re['result']=="success"){
							$("#queryList").datagrid('reload');
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
	//关联货位
	function relate(code){
		//cartonningCargo
		$.ajax({
			url:'<%=request.getContextPath()%>/CargoController/cartonningCargo.mmx',
			data:{code:code},
			cache:false,
			dataType:'text',
			success:function(result){
				var re = eval('('+result+')');
				if(re['result']=="success"){
// 					$("#queryList").datagrid('reload');
<%-- 					window.location.href="<%=request.getContextPath()%>/CargoController/cartonningCargoJsp.mmx?code="+code; --%>
<%-- 					window.open("<%=request.getContextPath()%>/CargoController/cartonningCargoJsp.mmx?code="+code); --%>
					window.open("<%=request.getContextPath()%>/admin/rec/sys/cartonningCargo.jsp?code="+code);
					//此处不用.mmx跳转，是因为在新打开的页面中需要刷新此页面
				}else if(re['result']=="failure"){
					$.messager.show({
						title:'结果提示',
						msg:re['tip'],
						timeout:3000,
						showType:'slide'
					});
				}
			}
		});
	}
	//修改货位
	function update(code,flag){
		//cartonningCargo
		$.ajax({
			url:'<%=request.getContextPath()%>/CargoController/cartonningCargo.mmx',
			data:{code:code,flag:flag},
			cache:false,
			dataType:'text',
			success:function(result){
				var re = eval('('+result+')');
				if(re['result']=="success"){
// 					$("#queryList").datagrid('reload');
<%-- 					window.location.href="<%=request.getContextPath()%>/CargoController/cartonningCargoJsp.mmx?code="+code; --%>
<%-- 					window.open("<%=request.getContextPath()%>/CargoController/cartonningCargoJsp.mmx?code="+code); --%>
					window.open("<%=request.getContextPath()%>/admin/rec/sys/cartonningCargo.jsp?code="+code+"&flag="+flag);
				}else if(re['result']=="failure"){
					$.messager.show({
						title:'结果提示',
						msg:re['tip'],
						timeout:3000,
						showType:'slide'
					});
				}
			}
		});
	}
	$(function(){
		//装箱列表		
		$("#cause").combobox({
			valueField:'id',
			textField:'text',
			data:[{
				id:'-1',
				text:'请选择',
				selected:'true'
			},{
				id:'0',
				text:'入库作业'
			},{
				id:'1',
				text:'仓内作业'
			},{
				id:'2',
				text:'盘点抽检'
			},{
				id:'3',
				text:'日常理货'
			},{
				id:'4',
				text:'其他原因'
			}]
		});
		//查询 一开始就加载
		$("#queryList").datagrid({
			title:"装箱记录列表",
			iconCls:'icon-ok',
			width:'auto',
			height:'auto',
			pageNumber:1,
			pageSize:20,
			fitColumns:true,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/cartonningInfo.mmx',
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
			        {field:'cargo_info_code',title:'装箱单编号',width:150,align:'center'},
			        {field:'cargo_info_productcode',title:'商品编号',width:100,align:'center'},
			        {field:'cargo_info_productcount',title:'装箱数量',width:100,align:'center'},
			        {field:'cargo_info_cargowholecode',title:'货位号',width:150,align:'center'},
			        {field:'cargo_info_createtime',title:'创建时间',width:200,align:'center'},
			        {field:'cargo_info_name',title:'责任人',width:100,align:'center'},
			        {field:'cargo_info_statusname',title:'状态',width:100,align:'center'},
			        {field:'cargo_info_control',title:'操作',width:300,align:'center'}
			        ]]
		});
		// 条件查询
		$("#query").click(function(){
			var select = $("#select").val();
			var productCode = $("#productCode").val();
			var userName = $("#userName").val();
			var startTime = $("#startTime").datebox('getValue');
			var endTime = $("#endTime").datebox('getValue');
			var status="";
			$("input[name='status']:checked").each(function(i,n){
				status +=$(this).val()+",";
			});
			var temp = "select="+select+"&productCode="+productCode+"&userName="+userName+"&startTime="+startTime+"&endTime="+endTime
						+"&status="+status;
			$("#page_hidden").val(temp);
			$("#queryList").datagrid({//这里类似于重新加载datagrid传了参数
				queryParams:{select:select,productCode:productCode,userName:userName,
						startTime:startTime,endTime:endTime,status:status},
				url:'<%=request.getContextPath()%>/CargoController/cartonningInfo.mmx'
			});
			var p2 = $("#queryList").datagrid('getPager');
			p2.pagination({
				displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
				onBeforeRefresh:function(){
					$(this).pagination('loading');
					$(this).pagination('loaded');
				}
			});
		});
		var p = $("#queryList").datagrid('getPager');
		p.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
		$("#create").click(function(){
			$("#form").form('submit',{
				url:'<%=request.getContextPath()%>/CargoController/createCartonningInfo.mmx',
				onSubmit:function(){
					var isValid = $(this).form('validate');
					if(isValid&&$("#cause").combobox('getValue')=="-1"){
						isValid=false;
						$.messager.show({
							title:'提示',
							msg:'请选择装箱原因!',
							timeout:3000,
							showType:'slide'
						});
					}
					return isValid;
				},
				success:function(data){
					var json = eval('('+data+')');
					if(json['result']=='success'){
						$("#queryList").datagrid('reload');
					}else{
						$.messager.show({
							title:'提示',
							msg:json['tip'],
							timeout:3000,
							showType:'slide'
						});
					}
				}
			});
		});
	});
</script>
</html>