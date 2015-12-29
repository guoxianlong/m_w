<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>包装理赔设置</title>
<script type="text/javascript" charset="utf-8">
	$(function(){
		$('#datagrid').datagrid({ 
		    url:'<%= request.getContextPath()%>/claims/getClaimsExpenses.mmx',
		    toolbar : '#tb',
		    width : 700,
			height : 350,
			fit : false,
			fitColumns : true,
			striped : true,
			nowrap : false,
			loadMsg : '正在努力为您加载..',
			pagination : true,
			rownumbers : true,
			singleSelect : true,
			pageSize : 20,
			pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 ],
		    columns:[[    
		        {field:'productLineName',title:'产品线',width:100},    
		        {field:'price',title:'理赔金额',width:100},    
		        {field:'createUserName',title:'添加人',width:100,align:'right'},  
		        {field:'createDateTime',title:'添加时间',width:150,align:'right',
		        	formatter: function(value,rowData,rowIndex){
		        		return value.substring(0,19);
		        	}	
		        },
		        {field:'operation',title:'操作',width:100,align:'right',
		        	formatter: function(value,rowData,rowIndex){
		        		if(value==undefined){	        			
		        			return '<a href="javascript:deleteFun('+rowData.id+');">删除</a>';	
		        		}
					}
				}  
		    ]],
		    onRowContextMenu : function(e, rowIndex, rowData) {
				e.preventDefault();
				$(this).datagrid('unselectAll');
				$(this).datagrid('selectRow', rowIndex);
				$('#menu').menu('show', {
					left : e.pageX - 1,
					top : e.pageY - 1
				});

			}
		});	
	});
	
	function save() {
		var id =$('#cc').combobox('getValue');
		var price = $('#price').val();
		if(id=="请选择"){
			$.messager.show({
				msg : '请选择产品线',
				title : '提示'
			});
			return false;
		}
		if(isNaN(price)){ 
			$.messager.show({
				msg : '理赔金额应为数字',
				title : '提示'
			});
			return false;
		}
		if(price<0){
			$.messager.show({
				msg : '理赔金额不能为负数',
				title : '提示'
			});
			return false;
		}
		$.ajax({
		url:'<%= request.getContextPath()%>/claims/saveclaimsPackagePrice.mmx',
		data : {
			id : id,
			price:price
		},
		cache : false,
		type:"post",
		dataType : "json",
			success : function(r) {
				if (r.success) {
					$('#datagrid').datagrid('reload');
						$.messager.show({
							msg : r.msg,
							title : '提示'
						});
					editRow = undefined;
				} else {
					$.messager.show({
						msg : r.msg,
						title : '提示'
					});
				}
			}
		});
	}
		


	function deleteFun(id) {
		$.messager.confirm('询问','您确定要删除此记录？',
			function(b) {
				if (b) {
					$.ajax({
					url : '<%= request.getContextPath()%>/claims/deleteclaimsPackagePrice.mmx',
					data : {
						id : id
					},
					cache : false,
					dataType : "json",
						success : function(r) {
							if (r.success) {
								$('#datagrid').datagrid('reload');
									$.messager.show({
										msg : r.msg,
										title : '提示'
									});
								editRow = undefined;
							} else {
								$.messager.show({
									msg : r.msg,
									title : '提示'
								});
							}
						}
					});
				}
			});
	}
	
</script>
</head>
<body>
<form id="ff" method="post">   
    产品线:<input id="cc" class="easyui-combobox" name="dept" data-options="
    value:'请选择',valueField:'id',
    textField:'text',
    url:'<%= request.getContextPath()%>/Combobox/getProductLine.mmx',"/>      
    理赔金额：<input type="text" name="price" id="price"/>
    <a class="easyui-linkbutton" data-options="" onclick="save();" >保存</a>     
</form>
	<table id="datagrid"></table>	
</body>
</html>