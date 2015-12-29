<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	var addBrand;
	var userForm;
	var passwordInput;
	var userRoleDialog;
	var userRoleForm;
	var datagrid;
	var editDiv;
	var editForm;
	$(function() {
			datagrid = $('#datagrid').datagrid({
			url:'<%=request.getContextPath()%>/SortingController/sortingOvertimeOrderList.mmx',
			columns : [ [ {
				field : 'staffName',
				title : '员工',
				width : 200,
				align :'center',
				sortable : true,
				formatter : function(value, row, index) {
					    return row.staffName+"("+row.staffCode+")";
				}
			},{
				field : 'code',
				title : '波次号',
				align :'center',
				width : 200
			}, {
				field : 'groupCount',
				align :'center',
				title : '波次订单数',
				width : 200,
				sortable : true
			}, {
				field : 'receiveDatetime',
				align :'center',
				title : '领单时间',
				width : 200,
				sortable : true
			}, {
				field : 'orderCode',
				align :'center',
				title : '订单号',
				width : 200,
				sortable : true
			}] ]
		  });
	})
	
</script>
</head>

<body>
<h4>分拣超时订单</h4>
<div>
<table id="datagrid" ></table>
</div>
</body>
</html>