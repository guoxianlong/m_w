<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<%
	String menuId = request.getParameter("menuId");
%>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/ckeditor/ckeditor.js"></script>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	var applyDialog;
	var applyFrom;
	var editDialog;
	var remark;
	var rowData;
	var editor;
	var UUID;
	$(function() {
		applyFrom = $('#applyFrom').form();
		editFrom = $('#editFrom').form();
		datagrid = $('#datagrid').datagrid(
						{	url : '${pageContext.request.contextPath}/HelpContext/getHelpContexts.mmx',
							queryParams : {
							menuId : <%=menuId%>},							toolbar : '#tb',
							idField : 'id',
							width : 700,
							height : 350,
							fit : true,
							fitColumns : true,
							striped : true,
							nowrap : false,
							loadMsg : '正在努力为您加载..',
							pagination : true,
							rownumbers : true,
							singleSelect : true,
							pageSize : 10,
							pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
							frozenColumns : [ [ {
								field : 'id',
								title : 'ID',
								width : 20,
								hidden : true
							}, ] ],
							columns : [ [ {
								field : 'operation',
								title : '操作',
								width : 30,
								align : 'center',
								sortable : true
							}, {
								field : 'code',
								title : '编码',
								width : 20,
								align : 'center',
								sortable : true
							}, {
								field : 'context',
								title : '内容',
								width : 30,
								align : 'center',
								sortable : true
							}, {
								field : 'lastUpdateOne',
								title : '最后维护人',
								align : 'center',
								sortable : true
							}, {
								field : 'lastUpdateDate',
								title : '最后修改时间',
								align : 'center',
								sortable : true,
								formatter : function(value, rowData, rowIndex) {
									if (value != null) {
										return value.substring(0, 19);
									}
								}
							}, ] ],
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
		applyDialog = $('#applyDialog').show().dialog(
						{
							modal : true,
							minimizable : true,
							title : '文档说明',
							width : 850,
							height : 550,
							buttons : [ {
								text : '确定',
								handler : function() {
									applyFrom.form('submit',
										{url : '${pageContext.request.contextPath}/HelpContext/addAndEditHelpContext.mmx?menuId=<%=menuId%>' ,
										success : function(data) {
										console.info(data);
										var d = $.parseJSON(data);
										if (d) {
											applyDialog.dialog('close');
											$.messager.show({
												msg : d.msg,
												title : '提示'
											});
											datagrid.datagrid('reload');
										}
										}
										});
								}
							} ]
						}).dialog('close');
		editor = CKEDITOR.replace("context")
	});

	function showDialog() {
		$.ajax({
					type : "POST",
					cache : false,
					url : "${pageContext.request.contextPath}/HelpContext/loadMenuInfo.mmx",
					data : {menuId : <%=menuId%>},
					dataType : 'json',

					success : function(msg) {
						console.info(msg);
						if (msg) {
							remark = msg.obj.fromStr;
							$('#remarks').val(remark);

						}
					}
				});
	}
	function appendFun() {
		applyFrom.form('load', {
			id : '',
			remarks : '',
			code : '',
			operation : '',
		});
		editor.setData('');
		showDialog();
		applyDialog.dialog('open');
	}

	function editFun() {
		rowData = datagrid.datagrid('getSelections');
		if (rowData.length > 1) {
			$.messager.show({
				msg : '只能选择一行进行编辑',
				title : '提示'
			});
			return;
		}
		applyDialog.dialog('open');
		applyFrom.form('load', {
			id : rowData[0].id,
			remarks : rowData[0].remarks,
			code : rowData[0].code,
			operation : rowData[0].operation
		});

		editor.setData(rowData[0].context);
	}

	function deleteFun() {
		rowData = datagrid.datagrid('getSelections');
		$.messager
				.confirm(
						'询问',
						'您确定要删除此记录？',
						function(b) {
							if (b) {
								$
										.ajax({
											url : '${pageContext.request.contextPath}/HelpContext/delHelpContext.mmx',
											data : {
												id : rowData[0].id
											},
											cache : false,
											type:"post",
											dataType : "json",
											success : function(r) {
												if (r.success) {
													datagrid.datagrid('reload');
													$.messager.show({
														msg : r.msg,
														title : '提示'
													});
													editRow = undefined;
												} else {
													$.messager.show({
														msg : '删除菜单失败!',
														title : '提示'
													});
												}
											}
										});
							}
						});
	}
	function UUIDFun() {
		$.ajax({
			type : "POST",
			cache : false,
			url : "${pageContext.request.contextPath}/HelpContext/getUUID.mmx",
			dataType : 'json',

			success : function(msg) {
				console.info(msg);
				if (msg) {
					UUID = msg.obj.UUID;
					$('#code').val(UUID);
					$('#code').focus();
				}
			}
		});
	}
</script>

</head>
<body>
	<table id="datagrid">
	</table>
	<div id="applyDialog" style="display: none">
		<form id="applyFrom" method="post">
			<input id="id" name="id" type="hidden">
			<table class="tableForm" border="0" height="200" width="800">
				<tr>
					<th width="70px" height="20px" align="center">所属:</th>
					<td><input id="remarks" name="remarks" value="" type="text"
						class="easyui-validatebox" readonly="readonly" required="required"
						style="width: 300px;" /></td>
				</tr>
				<tr>
					<th width="70px" height="20px" align="center">编号:</th>
					<td><input name="code" id="code" type="text"
						class="easyui-validatebox" readonly="readonly" required="required"
						style="width: 260px;" /> <input type="button" value="自动生成"
						onclick="UUIDFun();"></td>
				</tr>
				<tr>
					<th width="70px" height="20px" align="center">操作:</th>
					<td><input name="operation" type="text"
						class="easyui-validatebox" required="required"
						style="width: 156px;" /></td>
				</tr>
				<tr>
					<th width="70px" align="center">使用说明:</th>
					<td><textarea name="context" cols="70" rows="10" id="context"></textarea>
						<!-- <script type="text/javascript">CKEDITOR.replace('context');</script> -->
					</td>
				</tr>
			</table>
		</form>
	</div>

	<div id="tb" style="height: auto; display: none;">
		<div>
			<a class="easyui-linkbutton" iconCls="icon-add"
				onclick="appendFun();" plain="true" href="javascript:void(0);">新建文档</a>
		</div>
	</div>

	<div id="menu" class="easyui-menu" style="width: 100px; display: none;">
		<div onclick="editFun();" iconCls="icon-edit">编辑</div>
		<div onclick="deleteFun();" iconCls="icon-remove">删除</div>
	</div>
</body>
</html>