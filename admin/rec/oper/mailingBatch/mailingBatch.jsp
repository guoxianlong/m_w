<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	$(function(){
		 $('#deliverQ').combobox({
         	url : '${pageContext.request.contextPath}/MailingBatchController/getDeliverComboBox.mmx',
         	valueField:'id',
  		    textField:'text',
         });
		 $('#mbDatagrid').datagrid({
			 onRowContextMenu : function(e, rowIndex, rowData) {
					e.preventDefault();
					$(this).datagrid('unselectAll');
					$(this).datagrid('selectRow', rowIndex);
					$('#menu').menu('show', {
						left : e.pageX,
						top : e.pageY
					});
				}
		 });
		 $('#mbDialog').show().dialog({
				modal : true,
				title : '添加波次',
				buttons : [ {
					text : '确定',
					handler : function() {
						var area = $('#mbForm input[name=area]').val();
						var deliver = $('#mbForm input[name=deliver]').val();
						var carrier = $('#mbForm input[name=carrier]').val();
						if(area=='' || deliver == ''){
							return;
						}
						$.ajax({
							url : '${pageContext.request.contextPath}/MailingBatchController/addMailingBatch.mmx',
							data : {
								area : area,
								deliver : deliver,
								carrier : carrier,
							},
							cache : false,
							dataType : "json",
							success : function(r) {
								$('#mbDialog').dialog('close');
								if(r){
									$.messager.show({
										msg : r.msg,
										title : '提示'
									});
									$('#mbDatagrid').datagrid('reload');
								}
							}
						});
					}
				} ]
			}).dialog('close');
	})
	function searchFun() {
		var deli = null;
		if($('#tb input[name=deliver]').val() != ''){
			deli = $('#tb input[name=deliver]').val();
		}else{
			deli = 0;
		}
		$('#mbDatagrid').datagrid('load', {
			code : $('#tb input[name=code]').val(),
			orderCode : $('#tb input[name=orderCode]').val(),
			store : $('#tb input[name=store]').val(),
			createDatetime : $('#tb input[name=createDatetime]').val(),
			deliver : deli,
		});
	}
	function clearFun() {
		$('#tb input').val('');
		$('#mbDatagrid').datagrid('load', {});
	}
	function append(){
		<%
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if(group.isFlag(428)){%>
			 $('#deliverA').combobox({
		         	url : '${pageContext.request.contextPath}/MailingBatchController/getDeliverComboBox.mmx',
		         	valueField:'id',
		  		    textField:'text' 
		     });
			 $('#areaA').combobox({
		         	url : '${pageContext.request.contextPath}/MailingBatchController/getCargoDeptAreaComboBox.mmx',
		         	valueField:'id',
		  		    textField:'text' 
		     });
			$('#mbDialog').dialog('open');
			$('#mbForm').form('clear');
		<%}else{%>
			$.messager.show({
				msg : "您没有权限进行此操作!",
				title : '提示'
			});
			
		<%}%>
	}
	function print(){
		var rows = $('#mbDatagrid').datagrid('getSelections');
		if(rows.length == 0) {
			$.messager.show({
				msg : '请先择一条记录,再进行打印操作!',
				title : '提示'
			});
		}else if(rows.length == 1) {
			window.open("${pageContext.request.contextPath}/admin/productStock/mailingBatchPrintLine.jsp?code="+rows[0].code,"_blank");
		}else if(rows.length > 1){
			$.messager.show({
				msg : '只能先择一条记录进行打印操作!',
				title : '提示'
			});
		}
	}
	function view(){
		var rows = $('#mbDatagrid').datagrid('getSelections');
		if(rows.length == 0) {
			$.messager.show({
				msg : '请先择一条记录,再进行查看操作!',
				title : '提示'
			});
		}else if(rows.length == 1) {
			location.href = "${pageContext.request.contextPath}/admin/rec/oper/mailingBatch/mailingBatchDetail.jsp?mailingBatchId="+rows[0].id;
		}else if(rows.length > 1){
			$.messager.show({
				msg : '只能先择一条记录进行查看操作!',
				title : '提示'
			});
		}
	}
	function del() {
		var rows = $('#mbDatagrid').datagrid('getSelections');
		if (rows.length > 0) {
			var ids = [];
			for ( var i = 0; i < rows.length; i++) {
				if(rows[i].status != 1){
					ids.push(rows[i].id);
				}
			}
			if(ids.length == 0){
				$.messager.alert('提示', '已出库波次不能被删除!', 'error');
				return;
			}
			$.messager.confirm('请确认', '您要删除当前所选项目？（注:已出库波次将不会被删除）', function(r) {
				if (r) {
					$.ajax({
						url : '${pageContext.request.contextPath}/MailingBatchController/deleteMailBatch.mmx',
						data : {
							ids : ids.join(',')
						},
						cache : false,
						dataType : "json",
						success : function(r) {
							$('#mbDatagrid').datagrid('unselectAll');
							$('#mbDatagrid').datagrid('reload');
							$.messager.show({
								title : '提示',
								msg : r.msg,
							});
						}
					});
				}
			});
		} else {
			$.messager.alert('提示', '请选择要删除的记录！', 'error');
		}
	}
</script>
</head>
<body>
	<table id="mbDatagrid" class="easyui-datagrid" style="height: auto;width:auto; display: none;"
			url="${pageContext.request.contextPath}/MailingBatchController/getMailingBatchDatagrid.mmx"
			nowrap="false" border="false" idField="id" fit="true" fitColumns="true" title=""
			pageSize ="20"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
			toolbar="#tb" rownumbers="true" pagination="true" singleSelect="false"> 
		<thead>
			<tr>
				<th field="id" width="20" align="center" checkbox="true">ID</th>
				<th field="code" width="50"  align="center"
					data-options="formatter : function(value, rowData, rowIndex) 
					{return '<a href=\'${pageContext.request.contextPath}/admin/rec/oper/mailingBatch/mailingBatchDetail.jsp?mailingBatchId='+ rowData.id +'\'>'+ value + '</a>'}">发货波次号</th>
				<th field="createDatetime" width="50" align="center">创建时间</th>
				<th field="deliver" width="50" align="center"
					data-options="formatter : function(value, rowData, rowIndex) {return rowData.deliverName;}">配送渠道</th>
				<th field="carrier" width="50" align="center">承运商</th>
				<th field="createAdminId" width="50" align="center" 
					data-options="formatter : function(value, rowData, rowIndex) {return rowData.createAdminName;}">创建人</th>
				<th field="transitAdminId" width="50" align="center"
					data-options="formatter : function(value, rowData, rowIndex) {return rowData.transitAdminName;}">交接人</th>
				<th field="store" width="40" align="center">发货仓库</th>
				<th field="status" width="30" align="center"
					data-options="formatter : function(value, rowData, rowIndex) {return rowData.statusName;}">发货状态</th>
			</tr>
		</thead>
	</table>
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
			<legend>筛选</legend>
			<span>发货波次:</span>
			<input name="code" style="width:150px;border:1px solid #ccc">
			<span>订单编号:</span>
			<input name="orderCode" style="width:150px;border:1px solid #ccc"><br>
			<span>发货仓库:</span>
			<input name="store" style="width:150px;border:1px solid #ccc">
			<span>配送渠道:</span>
			<input id="deliverQ" name="deliver" style="width:152px; border:1px solid #ccc">
			<span>创建日期:</span>
			<input name="createDatetime" class="easyui-datebox" style="width:150px;border:1px solid #ccc">
			
			<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun()">查找</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun()">清空</a>
		</fieldset>  
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="append()">新增发货波次</a>
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="del()">删除</a>  
	    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="view()">查看</a>    
	    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="print()">打印条码</a>    
	</div>
	<div id="menu" class="easyui-menu" style="width:120px;display: none;">
		<div onclick="append();" iconCls="icon-add">新增发货波次</div>
		<div onclick="print();" iconCls="icon-print">打印条码</div>
		<div onclick="view();" iconCls="icon-search">查看</div>
		<div onclick="del();" iconCls="icon-remove">删除</div>
	</div>
	<div id="mbDialog" style="display: none;overflow: hidden;">
		<form id="mbForm" method="post">
			<table class="tableForm">
				<tr>
					<th>指定归属仓库</th>
					<td colspan="3"><input id="areaA" name="area" editable="false" required="required" style="width: 156px" /></td>
				</tr>
				<tr>
					<th>指定归属配送渠道</th> 
					<td><input id="deliverA" name="deliver" type="text" editable="false" required="required" style="width: 156px;" /></td>
				</tr>
				<tr title="选填~">
					<th>指定归属承运商</th>
					<td><input name="carrier"  style="width: 156px;" /></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>