<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%
List areaList = (List) request.getAttribute("areaList");
%>
<!DOCTYPE html>
<%
int hour =24;
int m =5;
%>
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
	var editDiv;
	var editForm;
	$(function() {
			datagrid = $('#datagrid').datagrid({
			url : '${pageContext.request.contextPath}/SortingController/sortingBatchGroupPrintList.mmx',
			toolbar : '#toolbar',
			title : '分拣波次补打',
			striped:'true',
			rownumbers:'true',
			showFooter:'true',
			idField : 'id',
			columns : [ [ {
				field : 'staffName',
				title : '姓名',
				align :'center',
					width:'150'
			},{
				field : 'staffCode',
				align :'center',
				title : '员工号',
				sortable : true,
				width:'200'
			},{
				field : 'code',
				title : '分拣波次号',
				align :'center',
				width:'200'
			},{
				field : 'receiveDatetime',
				title : '领单时间',
				align :'center',
				width:'200'
			},{
				field : 'statusName',
				title : '分拣波次状态',
				align :'center',
				width:'100'
			},{
				field : 'action',
				title : '操作',
				align :'center',
				width : 100,
				formatter : function(value, row, index) {
					if(row.staffName!='总数：')
					    return '<a href="javascript:void(0);" onclick="openPrintPage(\'\','+row.id+',\'buda\',\'\')">补打</a>';
				}
		      }
			] ]		  
		});
	});
	
	function searchBrand() {
		datagrid.datagrid('load', {
			code : $('#searchForm').find('[name=code]').val(),
		});
	}
	function openPrintPage(userCode,sortingBatchGroupId,printType,selectedOrder){
		 if( window.confirm("是否继续操作")){
			 window.location.href = '${pageContext.request.contextPath}/SortingController/sortingBatchGroupPrintLine.mmx?userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType='+printType+'&selectedOrder='+selectedOrder+'&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex"))%>';
			 return true;	 
		 }else{
			 return false;
		 }
	}
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" border="false" style="overflow: hidden;">
		<div id="toolbar" class="datagrid-toolbar"  style="height: auto;display: none;">
		<form id="searchForm">
					
					


    <input type="text" name="code" size="55" value="员工号/分拣波次号/订单号" onfocus="if(this.value=='员工号/分拣波次号/订单号'){this.value=''}">
	<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchBrand();" href="javascript:void(0);">查找</a>




			</form>
		</div>
		<table id="datagrid" ></table>
	</div>
</body>
</html>