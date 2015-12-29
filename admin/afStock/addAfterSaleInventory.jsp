<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>新建盘点作业</title>
</head>
<body>
	<fieldset>
		<form id="checkForm" method="post">
			盘点库类型：
			<select id="stockType" name="stockType">
				<option value="">请选择库类型</option>
				<option value="9">售后库</option>
				<option value="10">客户库</option>
			</select>
			售后地区：<input id="areaId" name="areaId" style="width: 121px" />
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">生成盘点编号</a>
		</form>
		<hr/>
		<table id="inventoryDataGrid"></table>
	</fieldset>
</body>
</html>
<script type="text/javascript">
	var datagrid;
	$(function(){
		datagrid = $("#inventoryDataGrid").datagrid({
			title:"未完成盘点作业单列表",
			idField : 'id',
			iconCls:'icon-ok',
			fitColumns:true,
			width:800,
			height:500,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleInventoryList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns:[[
			        {field:'code',title:'盘点编号',width:80,align:'center'},
			        {field:'stock_type',title:'库类型',width:80,align:'center'},
			        {field:'area',title:'售后地区',width:80,align:'center'},
			        {field:'operate',title:'操作',width:200,align:'center'}
			]]
		});
		
		$('#areaId').combobox({
	      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
	      	valueField:'id',
			textField:'text',
			editable:false
	    });
	});
		
	function submitForm(){
		var stockType = $("#stockType").val();
		if(stockType==''){
			$.messager.show({
				msg : '请选择库类型!',
				title : '提示'
			});
			return false;
		}
		var areaId = $('#areaId').combobox('getValue');
		if(areaId==''){
			$.messager.show({
				msg : '请选择售后地区!',
				title : '提示'
			});
			return false;
		}
		$.ajax({
		    url : "${pageContext.request.contextPath}/admin/AfStock/addAfterSaleInventory.mmx",
			type : "post",
			dataType :'json',
			cache: false,
			data : {stockType:stockType,areaId:areaId},
			success: function(d){
				$("#inventoryDataGrid").datagrid("reload");
				if (d) {
					$("#stockType").val("");
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
	function opeateInventory(id,type,flag){
		if(id==""){
			$.messager.show({
				msg : '不存在此盘点作业单',
				title : '提示'
			});
			return false;
		}
		if(type=="complete"){
			$.messager.confirm('提示', '确定本次盘点作业已经全部完成?', function(r){  
                if (r){  
                     updateInventory(id,type,flag);
                }  
            }); 
		}else{
			updateInventory(id,type,flag);
		}
	}
	function updateInventory(id,type,flag){
		$.ajax({
		    url : "${pageContext.request.contextPath}/admin/AfStock/updateInventory.mmx",
			type : "post",
			dataType :'json',
			cache: false,
			data : {
				id:id,
				type:type,
				flag:flag},
			success: function(d){
				$("#inventoryDataGrid").datagrid("reload");
				if (d) {
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
</script>
