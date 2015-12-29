<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="http://www.jeasyui.com/easyui/datagrid-detailview.js"></script>
<%
	String batchId = request.getParameter("batchId");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<script type="text/javascript" charset="UTF-8">
	var status;
	var mailingBatchId;
	var code;
	var datagrid;
	var plDatagrid;
	$(function(){
		plDatagrid = $('#plDatagrid').datagrid({
			view: detailview,
			detailFormatter:function(index,row){
				return '<div style="padding:1px"><table id="ddv-' + index + '"></table></div>';
			},
			onExpandRow: function(index,row){
				delIndex = index;
				$('#ddv-'+index).datagrid({
					url:'${pageContext.request.contextPath}/SortingController/sortingBatchGroupDetail.mmx?groupId=' + row.id,
					toolbar : '#toolbar',
					fitColumns:true,
					singleSelect : true,
					rownumbers : true,
					loadMsg:'',
					height:'auto',
					frozenColumns : [ [ {
						title : 'id',
						field : 'id',
						width : 50,
						align :'center',
						checkbox : true
					}] ],
					columns:[[
						{field:'orderCode',title:'订单编号',width:50,align:'center'},
						{field:'orderStockCode',title:'出库单编号',width:100,align:'center'},
						{field:'statusName',title:'分拣状态',width:80,align:'center'},
						{field:'deliverName',title:'快递公司',width:50,align:'center'},
						{field:'orderTypeName',title:'商品分类',width:40,align:'center'},
						{field:'productCount',title:'SKU个数',width:40,align:'center'},
						{field:'productCode',title:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商品编号&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;货位号',width:120,align:'center'}
					]],
					onResize:function(){
						$('#plDatagrid').datagrid('fixDetailRowHeight',index);
					},
					onLoadSuccess:function(){
						setTimeout(function(){
							$('#plDatagrid').datagrid('fixDetailRowHeight',index);
						},0);
					}
				});
				$('#plDatagrid').datagrid('fixDetailRowHeight',index);
			}
		});
	$('#storage').combobox({   
		url:'<%=request.getContextPath()%>/SortingController/getStorageList.mmx',
		valueField : 'id',   
		textField : 'storageName',
		panelHeight:'auto'
	}); 
	 $('#startTime').datebox({ 
			required:true,
			editable:false,
		}); 
	    $('#endTime').datebox({ 
	    	required:true,
	    	editable:false,
		}); 
	});
	function searchBrand() {
		plDatagrid.datagrid('load', {
			startTime : $('#searchForm').find('[name=startTime]').val(),
			endTime : $('#searchForm').find('[name=endTime]').val(),
			storage : $('#searchForm').find('[name=storage]').val(),
			status : $('#searchForm').find('[name=status]').val(),
			code : $('#searchForm').find('[name=code]').val(),
			queryType : $('#searchForm').find('[name=queryType]').val(),
		});
	}
	function inputCode1(sortingBatchGroupId,printType){
	  	  var code = window.prompt("员工号:","");
	  	  if(code==""){
		 	 inputCode1(sortingBatchGroupId,printType);
		  }else if(code){
		  	openPrintPage1(code,sortingBatchGroupId,printType);
		  }
	}
	function openPrintPage(sortingBatchGroupId,printType){
		if( window.confirm("是否继续操作")){
			window.location.href = '${pageContext.request.contextPath}/SortingController/sortingBatchGroupPrintLine.mmx?sortingBatchGroupId='+sortingBatchGroupId+'&printType=buda&pageFrom=query';
			return true;	 
		}else{
			 return false;
		}
	}
	function openPrintPage1(userCode,sortingBatchGroupId,printType,selectedOrder){
		if( window.confirm("是否继续操作")){
			window.location.href = '${pageContext.request.contextPath}/SortingController/sortingBatchGroupPrintLine.mmx?userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType=all&pageFrom=query';
			 return true;	 
		}else{
			 return false;
		}
	}
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" border="false" style="overflow: hidden;">
   <form id="searchForm">
    <div align="center">
    <select name="storage" id="storage" class="easyui-combobox" panelHeight="auto" style="width:100px"></select>
    <input type=text  id='startTime' name='startTime' size="10" value="" />至
		<input type=text  id='endTime' name='endTime'  size="10" value="" />&nbsp;&nbsp;&nbsp;&nbsp;
     <select name="queryType" id="queryType" style="height:22px;font-size:15px;">
       <option value="1">订单号</option>
       <option value="2">波次号</option>
       <option value="3">分拣员工号</option>
       <option value="4">分播员工号</option>
      </select>
     <input style="height:22px;font-size:18px;" name="code" id="code" type="text" value="请输入..." size="40" width="10"onfocus="if(this.value=='请输入...'){this.value=''}"/>
         <a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchBrand();" href="javascript:void(0);">查找</a>	    
   </div>
	</form>
	<table id="plDatagrid" class="easyui-datagrid" style="height: auto;width:auto; "
	        url="${pageContext.request.contextPath}/SortingController/sortingBatchGroupQueryList.mmx"  
	        toolbar="#toolbar" 
	        fitColumns:"true"
			nowrap="false" border="false" idField="id"  title=""
			pagination="true" pageSize ="10"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
			 rownumbers="true" singleSelect="false"> 
					
		<thead>
			<tr>
				<th field="code"   align="center">分拣波次号</th>
				<th field="orderCount"  align="center">订单总数</th>
				<th field="skuCount"  align="center"data-options="">SKU数</th>
				<th field="productCount"  align="center"data-options="">商品件数</th>
				<th field="statusName" align="center"data-options="">状态</th>
				<th field="storageName" align="center"data-options="">作业仓</th>
				<th field="staffName"  align="center"data-options="">分拣员</th>
				<th field="receiveDatetime"  align="center"data-options="formatter : function(value, rowData, rowIndex) {
								if(rowData.receiveDatetime!= '' && rowData.receiveDatetime != null){
									return rowData.receiveDatetime.substring(0,19);
								}
							}" >领取时间</th>
				<th field="completeDatetime"  align="center"data-options="formatter : function(value, rowData, rowIndex) {
								if(rowData.completeDatetime!= '' && rowData.completeDatetime != null){
									return rowData.completeDatetime.substring(0,19);
								}
							}">完成时间</th>
				<th field="receiveDatetime2" align="center"data-options="formatter : function(value, rowData, rowIndex) {
								if(rowData.receiveDatetime2!= '' && rowData.receiveDatetime2 != null){
									return rowData.receiveDatetime2.substring(0,19);
								}
							}">分播时间</th>
				<th field="completeDatetime2"  align="center"data-options="formatter : function(value, rowData, rowIndex) {
								if(rowData.completeDatetime2!= '' && rowData.completeDatetime2 != null){
									return rowData.completeDatetime2.substring(0,19);
								}
							}">结批时间</th>
				<th field="staffName2"  align="center"data-options="">分播员</th>
				<th field="secondeSortingTime" width="100" align="center"data-options="">分播耗时（分钟）</th>
				                 <% if(group.isFlag(592)){%>
				<th field="fff"  align="center"
				data-options="formatter : function(value, rowData, rowIndex) {
                                if(rowData.status==0){
								return '<input  onClick=\'inputCode1('+ rowData.id + ')\'  type=\'button\' value=\'打单\'>';
								}else{
								return '<input  onClick=\'openPrintPage('+ rowData.id + ')\'  type=\'button\' value=\'补打\'>';
								}
							}">操作</th><%} %>
			</tr>
		</thead>
	</table></div>
	<div id="toolbar" class="datagrid-toolbar"
			style="height: auto; ">
		</div>
</body>
</html>