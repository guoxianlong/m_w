<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="http://www.jeasyui.com/easyui/datagrid-detailview.js"></script>
<%
	String mailingBatchId = request.getParameter("mailingBatchId");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<script type="text/javascript" charset="UTF-8">
	var status;
	var indexE;
	var mailingBatchId;
	var code;
	var delIndex;
	$(function(){
		loadMailingBatchInfo();
		$('#plDatagrid').datagrid({
			view: detailview,
			detailFormatter:function(index,row){
				return '<div style="padding:1px"><table id="ddv-' + index + '"></table></div>';
			},
			onExpandRow: function(index,row){
				delIndex = index;
				if(indexE != index){
					$('#plDatagrid').datagrid('collapseRow',indexE); 
					indexE = index;
				}else{
					indexE = index;
				}
				$('#plDatagrid').datagrid('selectRecord',row.id);
				$('#ddv-'+index).datagrid({
					url:'${pageContext.request.contextPath}/MailingBatchController/getPackageDatagrid.mmx?parcelId=' + row.id,
					toolbar : '#toolbar',
					fitColumns:true,
					singleSelect : true,
					rownumbers : true,
					loadMsg:'',
					height:'auto',
					columns:[[
						{field:'id',title:'ID',hidden:true,width:20,align:'center'},
						{field:'orderCode',title:'订单号',width:50,align:'center'},
						{field:'packageCode',title:'包裹单号',width:50,align:'center'},
						{field:'createDatetime',title:'添加时间',width:70,align:'center',
							formatter : function(value, rowData, rowIndex) {
								if(rowData.createDatetime != '' || rowData.createDatetime != null){
									return rowData.createDatetime.substring(0,19);
								}
							}
						},
						{field:'deliver',title:'归属物流',width:50,align:'center',
							formatter : function(value, rowData, rowIndex) {
								return rowData.deliverName;
							}
						},
						{field:'address',title:'收件地址',width:100,align:'center'},
						{field:'buyMode',title:'付款方式',width:40,align:'center',
							formatter : function(value, rowData, rowIndex) {
								return rowData.buyModeName;
							}
						},
						{field:'totalPrice',title:'订单金额',width:40,align:'center',
							formatter : function(value, rowData, rowIndex) {
								return rowData.totalPriceStr;
							}},
						{field:'weight',title:'包裹重量',width:30,align:'center'},
					]],
					onResize:function(){
						$('#plDatagrid').datagrid('fixDetailRowHeight',index);
					},
					onLoadSuccess:function(){
						setTimeout(function(){
							$('#plDatagrid').datagrid('fixDetailRowHeight',index);
						},0);
					},
					onRowContextMenu : function(e, rowIndex, rowData) {
						e.preventDefault();
						$(this).datagrid('unselectAll');
						$(this).datagrid('selectRow', rowIndex);
						$('#pkMenu').menu('show', {
							left : e.pageX,
							top : e.pageY
						});
					}
				});
				$('#plDatagrid').datagrid('fixDetailRowHeight',index);
			},
			onRowContextMenu : function(e, rowIndex, rowData) {
				e.preventDefault();
				$(this).datagrid('unselectAll');
				$(this).datagrid('selectRow', rowIndex);
				$('#plMenu').menu('show', {
					left : e.pageX,
					top : e.pageY
				});
			},
		});
		$('#pkDialog').show().dialog({
			modal : true,
			minimizable : true,
			maximizable : true,
			title : '添加包裹',
			buttons : [ {
				text : '确定',
				handler : function() {
					if(status == '0'){
						<%if(group.isFlag(439)){ %>
							var rows = $('#plDatagrid').datagrid('getSelections');
							var oCode = $('#pkForm').find("input[name='orderCode']").val();
							var pCode = $('#pkForm').find("input[name='packageCode']").val();
							console.info(oCode);
							console.info(pCode);
							console.info(rows[0]);
							console.info(mailingBatchId);
							$.ajax({
								url : '${pageContext.request.contextPath}/MailingBatchController/addMailingBatchPackage.mmx',
								type : 'post',
								data : {
									packageCode : pCode,
									orderCode : oCode,
									mailingBatchId : mailingBatchId,
									parcelId : rows[0].id
								},
								cache : false,
								dataType : 'json',
								success : function(r) {
									$.messager.show({title : '提示',msg : r.msg,});
								}
							});
						<%}else{%>
							$.messager.show({title : '提示',msg : "您没有添加包裹权限!",});
						<%}%>
					}else{
						$.messager.show({title : '提示',msg : "该发货状态不允许添加包裹!",});
					}
				}
			} ]
		}).dialog('close');
	});
	function loadMailingBatchInfo(){
		$('#mbForm').form('clear');
		$('#codeD').empty();
		$('#createAdminNameD').empty();
		$('#carrierD').empty();
		$('#statusNameD').empty();
		$('#deliverNameD').empty();
		$('#orderCountD').empty();
		$('#totalWeightD').empty();
		$.ajax({
			url : '${pageContext.request.contextPath}/MailingBatchController/loadMailingBatchInfo.mmx',
			data : {
				mailingBatchId : <%=mailingBatchId %>
			},
			cache : false,
			dataType : "json",
			success : function(data) {
				if(data){
					$('#mbForm').form('load',data);
				}
				$('#codeD').append($('#mbForm').find("input[name='code']").val());
				$('#createAdminNameD').append($('#mbForm').find("input[name='createAdminName']").val());
				$('#carrierD').append($('#mbForm').find("input[name='carrier']").val());
				$('#statusNameD').append($('#mbForm').find("input[name='statusName']").val());
				$('#deliverNameD').append($('#mbForm').find("input[name='deliverName']").val());
				$('#orderCountD').append($('#mbForm').find("input[name='orderCount']").val());
				$('#totalWeightD').append($('#mbForm').find("input[name='totalWeight']").val()/1000 +　"KG");
				
				status = $('#mbForm').find("input[name='status']").val();
				if(status != '0'){
					$('#hiddenBt').empty();
				}
				mailingBatchId = $('#mbForm').find("input[name='id']").val();
				code = $('#mbForm').find("input[name='code']").val();
			}
		});
	}
	function addParcel(){
		if(status != '0'){
			$.messager.show({title : '提示',msg : "该发货状态不允许添加邮包!",});
			return;
		}
		<%if(!group.isFlag(434)){ %>
			$.messager.show({title : '提示',msg : "您没有添加发货邮包权限!",});
			return;
		<%}%>
		$.ajax({
			url : '${pageContext.request.contextPath}/MailingBatchController/addMailingBatchParcel.mmx',
			type : 'post',
			data : {
				mailingBatchId : mailingBatchId
			},
			cache : false,
			dataType : "json",
			success : function(r) {
				$('#plDatagrid').datagrid('unselectAll');
				$('#plDatagrid').datagrid('reload');
				$.messager.show({title : '提示',msg : r.msg,});
			}
		});
	}
	function transit(){
		<%if(!group.isFlag(433)){ %>
			$.messager.show({title : '提示',msg : "您没有交接完成操作权限!",});	
			return;
		<%}%>
		$.messager.confirm('请确认', '确认交接完成？', function(r) {
			if(r) {
				$.ajax({
					url : '${pageContext.request.contextPath}/MailingBatchController/transitMailingBatch.mmx',
					type : 'post',
					data : {
						mailingBatchId : mailingBatchId
					},
					cache : false,
					dataType : "json",
					success : function(r) {
						loadMailingBatchInfo();
						$('#plDatagrid').datagrid('unselectAll');
						$('#plDatagrid').datagrid('reload');
						$.messager.show({title : '提示',msg : r.msg,});
					}
				});
			}
		});
	}
	function deliveryReceiptPrint(){
		<%if(group.isFlag(435)){ %>
			window.open("${pageContext.request.contextPath}/MailingBatchController/cargoTransportDeliveryReceiptPrint.mmx?code=" + code,"_blank");
		<%}else{%>
			$.messager.show({title : '提示',msg : "您没有打印承运交接单权限!",});
		<%}%>
	}
	function deleteParcel(){
		if(status != '0'){
			$.messager.show({title : '提示',msg : "该发货状态不允许删除邮包!",});
			return;
		}
		<%if(!group.isFlag(437)){ %>
			$.messager.show({title : '提示',msg : "您没有删除邮包权限!",});
			return;
		<%}%>
		var ids = [];
		var rows = $('#plDatagrid').datagrid('getSelections');
		if (rows.length > 0) {
			$.messager.confirm('请确认', '您要删除当前所选邮包？', function(r) {
				if (r) {
					for ( var i = 0; i < rows.length; i++) {
						ids.push(rows[i].id);
					}
					$.ajax({
						url : '${pageContext.request.contextPath}/MailingBatchController/deleteParcel.mmx',
						data : {
							ids : ids.join(',')
						},
						cache : false,
						dataType : "json",
						success : function(r) {
							loadMailingBatchInfo();
							$('#plDatagrid').datagrid('unselectAll');
							$('#plDatagrid').datagrid('reload');
							$.messager.show({title : '提示',msg : r.msg,});
						}
					});
				}
			});
		} else {
			$.messager.alert('提示', '请选择要删除的邮包！', 'error');
		}
	}
	function deletePackage(){
		if(status != '0'){
			$.messager.show({title : '提示',msg : "该发货状态不允许删除包裹!",});
			return;
		}
		<%if(!group.isFlag(437)){ %>
			$.messager.show({title : '提示',msg : "您没有删除邮包权限!",});
			return;
	    <%}%>
	    var ids = [];
		var rows = $('#ddv-'+delIndex).datagrid('getSelections');
		if (rows.length > 0) {
			$.messager.confirm('请确认', '您要删除当前所选包裹？', function(r) {
				if (r) {
					for ( var i = 0; i < rows.length; i++) {
						ids.push(rows[i].id);
					}
					$.ajax({
						url : '${pageContext.request.contextPath}/MailingBatchController/deletePackage.mmx',
						data : {ids : ids.join(',')},
						cache : false,
						dataType : "json",
						success : function(r) {
							loadMailingBatchInfo();
							$('#ddv-'+delIndex).datagrid('unselectAll');
							$('#plDatagrid').datagrid('reload');
							$('#ddv-'+delIndex).datagrid('reload');
							$.messager.show({title : '提示',msg : r.msg,});
						}
					});
				}
			});
		} else {
			$.messager.alert('提示', '请选择要删除的包裹！', 'error');
		}
	}
	function printParcel(){
		<%if(!group.isFlag(438)){ %>
			$.messager.show({title : '提示',msg : "您没有删除邮包权限!",});
			return;
		<%}%>
		var rows = $('#plDatagrid').datagrid('getSelections');
		if (rows.length == 1) {
			window.open("${pageContext.request.contextPath}/admin/productStock/mailingBatchPrintLine.jsp?code="+rows[0].code,"_blank");
		}else if(rows.length > 1){
			$.messager.show({title : '提示',msg : '一次只能打印一个邮包条码!',});
		}else{
			$.messager.alert('提示', '请选择要删除的邮包！', 'error');
		}
	}
	function printParcelDetail(){
		<%if(!group.isFlag(440)){ %>
			$.messager.show({title : '提示',msg : "您没有打印邮包明细权限!",});
			return;
		<%}%>
		var rows = $('#plDatagrid').datagrid('getSelections');
		if (rows.length == 1) {
			window.open("${pageContext.request.contextPath}/MailingBatchController/mailingParcelDetailPrint.mmx?code=" + rows[0].code,"_blank");
		}else if(rows.length > 1){
			$.messager.show({title : '提示',msg : '只能选择一个邮包打印!',});
		}else{
			$.messager.alert('提示', '请选择要打印明细的邮包！', 'error');
		}
	}
	function excelParcelDetail(){
		<%if(!group.isFlag(440)){ %>
			$.messager.show({title : '提示',msg : "您没有导出邮包明细权限!",});
			return;
		<%}%>
		var rows = $('#plDatagrid').datagrid('getSelections');
		if (rows.length == 1) {
			location.href = "${pageContext.request.contextPath}/MailingBatchController/mailingParcelDetailExcel.mmx?code=" + rows[0].code;
		}else if(rows.length > 1){
			$.messager.show({title : '提示',msg : '只能选择一个邮包导出!',});
		}else{
			$.messager.alert('提示', '请选择要导出的邮包！', 'error');
		}
	}
	function append(){
		$('#pkDialog').dialog('open');
		$('#pkForm').form('clear');
	}
	function err(){
		$.messager.alert('提示', '此功能暂未开启!', 'error');
	}
</script>
</head>
<body>
	<table id="plDatagrid" class="easyui-datagrid" style="height: auto;width:auto; display: none;"
			url="${pageContext.request.contextPath}/MailingBatchController/getMailingBatchDetailDatagrid.mmx?mailingBatchId=<%=mailingBatchId %>"  
			nowrap="false" border="false" idField="id" fit="true" fitColumns="false" title=""
			pageSize ="20"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
			toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true"> 
		<thead>
			<tr>
				<th field="id" hidden="true" width="20" align="center" checkbox="false">ID</th>
				<th field="code" width="300"  align="center">发货邮包编号</th>
				<th field="packageCount" width="220" align="center">订单包裹数量</th>
				<th field="totalWeight" width="220" align="center"
				data-options="formatter : function(value, rowData, rowIndex) {return rowData.totalWeight/1000 + 'kg';}">总重量(KG)</th>
				<th field="status" width="300" align="center" 
					data-options="formatter : function(value, rowData, rowIndex) {return rowData.statusName;}">发货邮包状态</th>
				<th field="totalPrice" width="220" align="center">代收货款总额</th>
			</tr>
		</thead>
	</table>
	<div id="tb" style="padding:3px;height: auto;">
		<form id="mbForm" method="post">
			<fieldset>
			<legend>波次信息</legend>
			<table border="0" class="tableForm">
				<tr>
					<th align="right">发货波次：</th>
					<td width="15%"><div id="codeD"></div>
						<input name="id" type="hidden">
						<input name="code" type="hidden"></td>
					<th align="right">创建人：</th>
					<td width="15%"><div id="createAdminNameD"></div>
						<input name="createAdminName" type="hidden"></td>
					<th align="right">承运商：</th>
					<td width="15%"><div id="carrierD"></div>
						<input name="carrier" type="hidden"></td>
					<th align="right">发货状态：</th>
					<td width="15%"><div id="statusNameD"></div>
						<input name="status" type="hidden">
						<input name="statusName" type="hidden"></td>
				</tr>
				<tr>
					<th align="right">归属物流渠道：</th>
					<td><div id="deliverNameD"></div>
						<input name="deliverName" type="hidden"></td>
					<th align="right">订单总数：</th>
					<td><div id="orderCountD"></div>
						<input name="orderCount" type="hidden"></td>
					<th align="right">总重量：</th>
					<td><div id="totalWeightD"></div>
						<input name="totalWeight" type="hidden"></td>
					<td colspan="2" align="right">
					<label id="hiddenBt"><a href="#" class="easyui-linkbutton" iconCls="icon-ok" plain="false" onclick="transit()">交接完成确认</a></label></td>
				</tr>
			</table>
			</fieldset>  
		</form>
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addParcel()">添加发货邮包 </a>
   		<a href="#" class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="deliveryReceiptPrint()">打印承运交接单</a>
   		|| 
   		<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteParcel()">删除邮包</a>
   		<a href="#" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="err()">重量复核</a>
   		<a href="#" class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="printParcel()">打印邮包条码</a>
   		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="excelParcelDetail();"  >导出邮包明细</a> 
		<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="printParcelDetail();"  >打印邮包明细</a>
	</div>
	<div id="plMenu" class="easyui-menu" style="width:120px;display: none;">
		<div onclick="printParcel();" iconCls="icon-print">打印条码</div>
		<div onclick="err();" iconCls="icon-back">重量复核</div>
		<div onclick="deleteParcel();" iconCls="icon-remove">删除邮包</div>
		<div onclick="excelParcelDetail();" iconCls="icon-redo">导出邮包明细</div>
		<div onclick="printParcelDetail();" iconCls="icon-print">打印邮包明细</div>
	</div>
	<div id="toolbar" class="datagrid-toolbar" style="height: auto;display: none;">
		<div>
			<a class="easyui-linkbutton" iconCls="icon-add" onclick="append();" plain="true" href="javascript:void(0);">增加包裹</a>
			<a class="easyui-linkbutton" iconCls="icon-back" onclick="err();" plain="true" href="javascript:void(0);">重量修正</a> 
			<a class="easyui-linkbutton" iconCls="icon-remove" onclick="deletePackage();" plain="true" href="javascript:void(0);">删除包裹</a> 
		</div>
	</div>
	<div id="pkMenu" class="easyui-menu" style="width:120px;display: none;">
		<div onclick="append();" iconCls="icon-add">增加包裹</div>
		<div onclick="err();" iconCls="icon-back">重量修正</div>
		<div onclick="deletePackage();" iconCls="icon-remove">删除包裹</div>
	</div>
	<div id="pkDialog" style="display: none;overflow: hidden;">
		<form id="pkForm" method="post">
			<table class="tableForm">
				<tr>
					<th>订单编号</th> 
					<td><input name="orderCode" required="required" style="width: 156px;" /></td>
				</tr>
				<tr>
					<th>包裹单号</th>
					<td><input name="packageCode" required="required"  style="width: 156px;" /></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>