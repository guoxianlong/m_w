<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<% 
String code=request.getParameter("code");
%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/ckeditor/ckeditor.js"></script>
<script type="text/javascript" charset="UTF-8">
    var code=<%=code %>;
	var datagrid;
	$(function() {
		datagrid = $('#datagrid').datagrid(
						{	
							url : '${pageContext.request.contextPath}/dCheckController/getDCheckDetails.mmx?code='+code,
							toolbar : '#tb',
							idField : 'id',
							fit : true,
							fitColumns : true,
							nowrap : false,
							loadMsg : '正在努力为您加载..',
							pagination : true,
							rownumbers : true,
							singleSelect : false,
							pageSize : 10,
							pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
							frozenColumns : [ [ 
							{field:'id',checkbox:true}
							] ],
							columns : [ [ {
								field : 'cargoWholeCode',
								title : '货位号',
								width : 20,
								align : 'center',
								sortable : true
							}, {
								field : 'productCode',
								title : '产品编号',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'productName',
								title : '产品原名称',
								width : 10,
								align : 'center',
								sortable : true
							},{
							
								field : 'difference1',
								title : '一盘差异数',
								align : 'center',
								width : 10,
								sortable : true
							},{
								field : 'checkUsername1',
								title : '一盘人',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
									    return value;
									}
								}
							},{
								field : 'difference2',
								title : '二盘差异数',
								width : 10,
								align : 'center',
								sortable : true
							},{
								field : 'checkUsername2',
								title : '二盘人',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
									    return value;
									}
								}
							},{
								field : 'difference3',
								title : '终盘差异数',
								width : 10,
								align : 'center',
								sortable : true
							},{
								field : 'checkUsername3',
								title : '终盘人',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
									    return value;
									}
								}
							},{
								field : 'endCheckTimes',
								title : '终盘次数',
								width : 10,
								align : 'center',
								sortable : true
							},{
								field : 'checkResult',
								title : '盘点结果',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
										if (value == -1) {
											return "未盘点";
										}
										if (value == 0) {
											return "待盘点";
										}
										if (value == 1) {
											return "无差异";
										}
										if (value == 2) {
											return "盘盈";
										}
										if (value == 3) {
											return "盘亏";
										}
									}
								}
							},{
								field : 'status',
								title : '状态',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
										if (value == -1) {
											return "未盘点";
										}
										if (value == 0) {
											return "待盘点";
										}
										if (value == 1) {
											return "待二盘";
										}
										if (value == 2) {
											return "待终盘";
										}
										if (value == 3) {
											return "盘点完成";
										}
									}
								}
								
							}
							 ] ]
						})
	});
	
	function searchFun() {
		
		
		datagrid.datagrid('load', {			
			cargoInfoStockAreaId : $('#tb input[name=cargoInfoStockAreaId]').val(),
			status : $('#tb select[name=status]').val(),
			checkResult : $('#tb select[name=checkResult]').val(),
			cargoWholeCode : $('#tb input[name=cargoWholeCode]').val(),
			productCode : $('#tb input[name=productCode]').val(),
		});
	}

	function exportDCheckDetails(){
		
		window.location.href = "${pageContext.request.contextPath}/dCheckController/exportDCheckDetails.mmx?code="+code
			+"&cargoInfoStockAreaId="+$('#tb input[name=cargoInfoStockAreaId]').val()+"&status="+ $('#tb select[name=status]').val()
			+"&checkResult="+$('#tb select[name=checkResult]').val()+"&cargoWholeCode="+$('#tb input[name=cargoWholeCode]').val()
			+"&productCode="+$('#tb input[name=productCode]').val();
	}
	
	function updateDCheck(){		
		
		var checkedItems = $('#datagrid').datagrid('getChecked');
		var names = [];
		$.each(checkedItems, function(index, item){
		names.push(item.id);
		}); 
		if(names.length==0){
			alert("请选择数据!");
		}else{
			if(confirm("确定要重新终盘么?")){
				$.ajax({
					   type: "POST",
					   dataType : 'json',
					   url: '${pageContext.request.contextPath}/dCheckController/afreshDCheck.mmx',
					   data: "checkids="+names,
					   success: function(msg){
						    $.messager.show({
									msg : msg.msg,
									title : '提示'
								});
						    $('#datagrid').datagrid("reload")
					   }
					});
			}
		}
	}
</script>

</head>
<body>
	<table id="datagrid"></table>

<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>盘点明细列表</legend>
			<table class="tableForm"  border="0">
				<tr align="center" >
					
					<td align="left">
						<select name="status" id="status" />
						<option value="-1" />状态不限 </option>
						<option value="0" />待盘点 </option>
						<option value="1" />待二盘 </option>
						<option value="2" />待终盘 </option>
						<option value="3" />盘点完成 </option>
						</select>
					</td>
					<td align="left">
						<input id="cc" class="easyui-combobox"
						 name="cargoInfoStockAreaId" 
						 data-options="valueField:'id',textField:'text',url:'${pageContext.request.contextPath}/Combobox/getAllArea.mmx'" /></td>
					<td align="left">
						<select name="checkResult" id="checkResult" />
						<option value="-1" />盘点结果不限 </option>
						<option value="0" />待盘点 </option>
						<option value="1" />无差异 </option>
						<option value="2" />盘盈 </option>
						<option value="3" />盘亏 </option>
						</select>
					</td>
					<th >产品编号</th>
					<td align="left">
						<input id="productCode" name="productCode" /></td>	
					<th >货位号</th>
					<td align="left">
						<input id="cargoWholeCode" name="cargoWholeCode" /></td>	
				  <td align="right" >
					<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>				
				</tr>
				<tr align="center" >
				 <td align="right" >
					<a class="easyui-linkbutton"   onclick="updateDCheck();" href="javascript:void(0);">对所选记录重新终盘</a>
					</td>
					 <td align="right" >
					<a class="easyui-linkbutton"   onclick="exportDCheckDetails();" href="javascript:void(0);">导出查询记录</a>
					</td>
				
				</tr>
					
					
				
			</table>
		</fieldset>
	</div>
</body>
</html>