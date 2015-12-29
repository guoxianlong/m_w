<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.bean.UserGroupBean"%>
<%@ page import="mmb.stock.cargo.CargoDeptAreaService,java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
%>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#area').combobox({
      	url : '${pageContext.request.contextPath}/AbnormalCargoCheckController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text' 
    });
});
function searchFun(){
	$('#datagrid').datagrid('load',{
		code : $('#tb input[name=code]').val(),
		area : $('#tb input[name=area]').val(),
		status : $('#tb input[name=status]').val(),
	});
}
function clearFun(){
	$('#tb input').val('');
	$('#datagrid').datagrid('load',{});
}
function generateCheck(areaId){
	$.ajax({
		url : '${pageContext.request.contextPath}/AbnormalCargoCheckController/generateCheck.mmx',
		type : 'post',
		data : 'areaId=' + areaId,
		dataType : 'json',
		cache : false,
		success : function(r){
			$('#datagrid').datagrid('reload');
			$.messager.show({
				title : '提示',
				msg : r.msg
			});
		}
	});
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
		<legend>筛选</legend>
			<div align="left">
				
				库地区: <input id="area" name="area" editable="false">
				状态： <select name="status" id="status" class="easyui-combobox" editable="false"> 
						 <option value=""></option>
				         <option value="0">未开始</option>
				         <option value="1">一盘中</option>
				         <option value="2">二盘中</option>
				         <option value="3">终盘中</option>
				     </select>
				盘点计划：<input name="code">    
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun()">查询 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun()">清空 </a>
			</div>
		</fieldset>
		<%if(group.isFlag(781)){ %>
		<div>
			<a href="<%= request.getContextPath()%>/AbnormalCargoCheckController/toGenerateBySKU.mmx">按SKU生成异常货位盘点计划</a>
		<%if(areaList.contains("3")){%>
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-undo" plain="true" onclick="generateCheck('3')">生成增城异常货位盘点计划 </a>
			 <% }
			    if(areaList.contains("4")){ %>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="generateCheck('4')">生成无锡异常货位盘点计划 </a>
			 <%} %>
		</div>
		<%} %>
	</div>    
    <table id="datagrid" class="easyui-datagrid" style="height:auto;width:auto; display: none;"
	url="${pageContext.request.contextPath}/AbnormalCargoCheckController/getAbnormalCargoChecDatagrid.mmx"  
	nowrap="false" border="false" idField="id" fit="true" fitColumns="true" title=""
	pageSize ="50"pageList="[ 50, 100, 150, 200, 250, 300, 350, 400, 450, 500 ]"
	toolbar="#tb" rownumbers="true" pagination="true" singleSelect="true"> 
	<thead>
	<tr>
		<th field="id" width="20" align="center" hidden="true" checkbox="false">ID</th>
		<th field="code" width="150"  align="center"
			data-options="formatter : function(value, rowData, rowIndex) 
					{return '<a href=\'${pageContext.request.contextPath}/admin/rec/oper/abnormalCargo/abnormalCargoCheckDetail.jsp?code=' + rowData.code + '&accId='+ rowData.id +'\'>'+ value + '</a>'}">异常货位盘点计划号</th>
		<th field="createDatetime" width="90"  align="center"
			data-options="formatter : function(value, rowData, rowIndex) 
					{return value.substring(0,19)}">创建时间</th>
		<th field="createUserName" width="100"  align="center">创建人</th>
		<th field="operCreateUserName" width="70"  align="center">报损报溢单生成人</th>
		<th field="areaName" width="70"  align="center">库地区</th>
		<th field="statusName" width="70"  align="center">状态</th>
	</tr>
	</thead>
</table>
</body>
</html>