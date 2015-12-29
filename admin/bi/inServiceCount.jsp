<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.*" %>
<%
voUser user = (voUser)request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript">
	$.extend($.fn.validatebox.defaults.rules, {
		INT : {
			validator : function(value, param) {
				return /^(0|[1-9][0-9]*)$/.test(value);
			},
			message : '数字格式不正确'
		},
		FLOAT : {
			validator : function(value, param) {
				return /^(0|[1-9][0-9]*)(.[0-9]{0,2})?$/.test(value);
			},
			message : '数字格式不正确'
		}
	});
	
	var datagrid;
	function getList() {
		var areaId = $("#areaId2").combobox('getValue');
		if (areaId == null || areaId == -1) {
			alert("请选择仓库");
			$('#areaId').focus();
			return;
		}
		var startDate = $("#startDate").datebox('getValue');
		var endDate = $("#endDate").datebox('getValue');
		if (startDate == '' || endDate == '') {
			alert('请选择日期');
			return;
		}
		if(!validateDate(endDate,startDate)){
			alert('开始日期必须小于结束日期');
			$('#startDate').focus();
			return;
		}
		datagrid = $("#inServiceCountList").datagrid({
			title:"在职人力列表",
			idField : 'id',
			iconCls:'icon-ok',
			fitColumns:true,
			height:500,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'${pageContext.request.contextPath}/BIStoreController/getBIInServiceCountList.mmx',
			queryParams:{
				startDate: '' + startDate,
				endDate: '' + endDate,
				areaId: '' + areaId
			},
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns:[[
			        {field:'datetime',title:'日期',align:'center',
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
			        {field:'warehouse',title:'仓储部',align:'center'},
			        {field:'sendGoods',title:'发货部',align:'center'},
			        {field:'refundGoods',title:'退货部',align:'center'},
			        {field:'qualityChecking',title:'收货质检部',align:'center'},
			        {field:'delivery',title:'配送部',align:'center'},
			        {field:'operation',title:'运营部',align:'center'},
			        {field:'product',title:'产品部',align:'center'},
			        {field:'hr',title:'人事部',align:'center'},
			        {field:'administration',title:'行政部',align:'center'},
			        {field:'total',title:'在职位总人数',align:'center'},
			        {field:'status',title:'操作状态',align:'center',
			        	formatter : function(value,row,index){
			        		var temp = '';
			        		if(value == 0) {
			        			<% if (group.isFlag(2132)) { %>			        		
			        				temp += '<a href="javascript:void(0);" onclick="checkCount('+index+')">审核</a>&nbsp;&nbsp;';
		        				<% } %>
	        					temp += '<a href="javascript:void(0);" onclick="updateCount('+index+')">修改</a>';		        			
			        		} else {
				        		temp += '生效&nbsp;&nbsp;'; 
			        			<% if (group.isFlag(2133)) { %>			        		
			        				temp += '<a href="javascript:void(0);" onclick="deleteCount('+index+')">作废</a>';
	        					<% } %>				        		
			        		}      
			        		return temp;          
			        	}
			        }
			]]
		});
	}

	function addBean(){
		var areaId = $("#areaId").combobox('getValue');
		if (areaId == null || areaId == -1) {
			alert("请选择仓库");
			return;
		}
		$('#addForm').form('submit',{
			url : '${pageContext.request.contextPath}/BIStoreController/addBIInServiceCountBean.mmx',
			success : function(data) {
				var d = (data != null && data.success != null) ? data : $.parseJSON(data);
				if (d) {
					if (d.success) {
						var areaId = $("#areaId").combobox('getValue');
						var temp = $("#datetime").datebox('getValue');
						$('#addForm')[0].reset();						
						$("#areaId2").combobox('setValue', areaId);						
						$("#startDate").datebox('setValue', temp);
						$("#endDate").datebox('setValue', temp);						
						$("#getList").click();
					}
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
	function updateBean() {
		$('#updateForm').form('submit',{
			url : '${pageContext.request.contextPath}/BIStoreController/updateBIInServiceCountBean.mmx',	
			success : function(data) {
				var d = (data != null && data.success != null) ? data : $.parseJSON(data);
				if (d) {
					if (d.success) {
						$('#dialog').dialog('close');
						$("#inServiceCountList").datagrid('reload');
					}
					$.messager.show({
						msg : d.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
	function ajaxOper(index, eMsg, harfUrl){
		if (index == null)
			return;		
		$('#inServiceCountList').datagrid('selectRow', index);
		var row = $('#inServiceCountList').datagrid('getSelected');
		if(row == null)
			return;
		$.ajax({
			type : "post",
			url : "<%=request.getContextPath()%>/BIStoreController/" + harfUrl + "",
			dataType : "json",
			cache : false,
			data : {
				id : row.id,
				datetime : row.datetime,
				areaId : row.areaId,
				updateTime : row.updateTime
			},
			error : function(x, s, e) {
				$.messager.show({
					msg : '' + eMsg,
					title : '提示失败'
				});
			},
			success : function(json) {
				if (json != null) {
					if (json.success) {
						$("#inServiceCountList").datagrid('reload');
					}
					$.messager.show({
						msg : json.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
	function checkCount(index) {
		ajaxOper(index, '审核失败', 'checkBIInServiceCountBean.mmx');
	}
	
	function updateCount(index) {
		if (index == null)
			return;		
		$('#inServiceCountList').datagrid('selectRow', index);
		var row = $('#inServiceCountList').datagrid('getSelected');
		if (row == null)
			return;
		var $form = $("#updateForm");
		$("[name='id']", $form).val(row.id);		
		$("[name='datetime']", $form).val(row.datetime.substring(0,10));
		$("[name='warehouse']", $form).val(row.warehouse);
		$("[name='sendGoods']", $form).val(row.sendGoods);
		$("[name='refundGoods']", $form).val(row.refundGoods);
		$("[name='qualityChecking']", $form).val(row.qualityChecking);
		$("[name='delivery']", $form).val(row.delivery);
		$("[name='operation']", $form).val(row.operation);
		$("[name='product']", $form).val(row.product);
		$("[name='hr']", $form).val(row.hr);
		$("[name='administration']", $form).val(row.administration);

		$('#dialog').dialog('open');
	}

	function deleteCount(index) {
		$.messager.confirm('作废确认','您是否要作废该人力数据?',function(r){
		    if (r){
		    	ajaxOper(index, '作废失败', 'deleteBIInServiceCountBean.mmx');
		    }
		});		
	}
	
	$(function(){
		$('#areaId,#areaId2').combobox({
		    url:'${pageContext.request.contextPath}/BIStoreController/getBIArea.mmx',
		    valueField:'id',
		    textField:'text',
		    editable:false
		});
	});	
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>在职人力</title>
</head>
<body>
<% if (group.isFlag(2131)) { %>			        		
	<div style="padding:3px;height: auto;">
		<fieldset>
			<legend>录入在职人力</legend>
			<form id="addForm" method="post">
				<table>
					<tr>
						<td><span style="font-size: 12px;">仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId" name="areaId" style="width: 80px;" /></td>
						<td colspan="2"><span style="font-size: 12px;">日期&nbsp;&nbsp;&nbsp;</span><input id="datetime" name="datetime"  class="easyui-datebox" editable="false" id="datetime" style="width: 110px;" data-options="required:true">					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><span style="font-size: 12px;">仓储部&nbsp;</span><input validType="INT" type="text" name="warehouse" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">发货部&nbsp;</span><input validType="INT" type="text" name="sendGoods" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">退货部&nbsp;</span><input validType="INT" type="text" name="refundGoods" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">收货质检部&nbsp;</span><input validType="INT" type="text" name="qualityChecking" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">配送部&nbsp;</span><input validType="INT" type="text" name="delivery" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>					
					</tr>
					<tr>
						<td><span style="font-size: 12px; ">运营部&nbsp;</span><input validType="INT" type="text" name="operation" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">产品部&nbsp;</span><input validType="INT" type="text" name="product" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">人事部&nbsp;</span><input validType="INT" type="text" name="hr" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">行政部&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="administration" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td colspan="5"><a onclick="javascript:addBean();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a></td>					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</form>
		</fieldset>		
	<div>
<% } %>

	<div style="padding:3px;height: auto;">
			<table>
				<tr>
					<td><span style="font-size: 12px;">仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId2" style="width: 80px;" /></td>
					<td><span style="font-size: 12px;">日期&nbsp;</span><input class="easyui-datebox" editable="false" id="startDate">--</td>
					<td><input class="easyui-datebox" editable="false" id="endDate"></td>					
					<td><a id="getList" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" onclick="javascript:getList();" >查询</a></td>
				</tr>
			</table>
			<table id="inServiceCountList"></table>	 	
	<div>
	
	<div id="dialog" class="easyui-dialog" title="修改在职人力" style="width:720px;height:210px;"
        data-options="modal:true,closed:true">
    		<form id="updateForm" method="post">
				<table>
					<tr>
						<td colspan="2">
						<input type="hidden" name="id" />
						<span style="font-size: 12px;">日期&nbsp;</span><input name="datetime" readonly="readonly"/></td>
						<td>&nbsp;</td>					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><span style="font-size: 12px;">仓储部&nbsp;</span><input validType="INT" type="text" name="warehouse" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">发货部&nbsp;</span><input validType="INT" type="text" name="sendGoods" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">退货部&nbsp;</span><input validType="INT" type="text" name="refundGoods" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">收货质检部&nbsp;</span><input validType="INT" type="text" name="qualityChecking" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px;">配送部&nbsp;</span><input validType="INT" type="text" name="delivery" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>					
					</tr>
					<tr>
						<td><span style="font-size: 12px; ">运营部&nbsp;</span><input validType="INT" type="text" name="operation" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">产品部&nbsp;</span><input validType="INT" type="text" name="product" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">人事部&nbsp;</span><input validType="INT" type="text" name="hr" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td><span style="font-size: 12px; ">行政部&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input validType="INT" type="text" name="administration" class="easyui-validatebox" style="width: 80px;" data-options="required:true"/></td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td colspan="5"><a onclick="javascript:updateBean();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" href="javascript:void(0);" >保存</a></td>					
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
			</form>
	</div>
</body>
</html>