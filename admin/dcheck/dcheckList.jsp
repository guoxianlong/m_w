<%@page import="adultadmin.util.DateUtil"%>
<%@page import="java.util.Date"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<% 
voUser user = (voUser)session.getAttribute("userView");
Date  date=new Date();
String currentDate=DateUtil.formatTime(date);
%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
<style type="text/css"> 
        #fm 
        { 
            margin: 0; 
            padding: 10px 30px; 
        } 
        .ftitle 
        { 
            font-size: 14px; 
            font-weight: bold; 
            padding: 5px 0; 
            margin-bottom: 10px; 
            border-bottom: 1px solid #ccc; 
        } 
        .fitem 
        { 
            margin-bottom: 3px; 
        } 
        .fitem label 
        { 
            display: inline-block; 
            width: 80px; 
        } 
</style> 

<script type="text/javascript"
	src="<%=request.getContextPath()%>/ckeditor/ckeditor.js"></script>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	$(function() {
		datagrid = $('#datagrid')
				.datagrid(
						{
							url : '${pageContext.request.contextPath}/dCheckController/getDynamicChecks.mmx',
							toolbar : '#tb',
							idField : 'id',
							fit : true,
							fitColumns : true,

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
							columns : [ [
									{
										field : 'areaName',
										title : '库地区',
										width : 20,
										align : 'center',
										sortable : true
									},
									{
										field : 'checkType',
										title : '盘点类型',
										width : 10,
										align : 'center',
										sortable : true,
										formatter : function(value, rowData,
												rowIndex) {
											if (value == 1) {
												return "动碰盘";
											}
											if (value == 2) {
												return "大盘";
											}
										}
									},
									{
										field : 'code',
										title : '盘点计划号',
										width : 10,
										align : 'center',
										sortable : true,
										formatter : function(value, rowData,
												rowIndex) {
											/**
											if(rowData.status=='确认未通过' || rowData.status=='审核未通过'){	
												if(rowData.bianji==true){
													return '<a href="javascript:editFunFun(\''+rowData.code+'\');">编辑</a>';	
												}
											}
											 */
											return '<a href="javascript:toDetails(\''
													+ rowData.id
													+ '\');">'
													+ rowData.code + '</a>';

										}
									},{

										field : 'completeCount',
										title : '商品货位盘点量',
										align : 'center',
										width : 10,
										sortable : true
									},{

										field : 'differenceCount',
										title : '商品货位有差异量',
										align : 'center',
										width : 10,
										sortable : true
									},{

										field : 'createTime',
										title : '创建时间',
										align : 'center',
										width : 10,
										sortable : true,
										formatter : function(value, rowData,
												rowIndex) {
											if (value != null) {
												return value.substring(0, 19);
											}
										}
									},
									{
										field : 'createUsername',
										title : '创建人',
										width : 10,
										align : 'center',
										sortable : true,
										formatter : function(value, rowData,
												rowIndex) {
											if (value == 'null') {
												return "";
											} else {
												return value;
											}
										}
									},
									{
										field : 'completeTime',
										title : '结束时间',
										width : 10,
										align : 'center',
										sortable : true,
										formatter : function(value, rowData,
												rowIndex) {
											if (value != null) {
												return value.substring(0, 19);
											}
										}

									},
									{
										field : 'status',
										title : '状态',
										width : 10,
										align : 'center',
										sortable : true,
										formatter : function(value, rowData,
												rowIndex) {
											if (value == 'null') {
												return "";
											} else {
												if (value == 1) {
													return "未盘点";
												}
												if (value == 2) {
													return "盘点中";
												}
												if (value == 3) {
													return "盘点结束";
												}
											}
										}

									},
									{
										field : 'operation',
										title : '操作',
										width : 10,
										align : 'center',

										formatter : function(value, rowData,
												rowIndex) {
											/**
											if(rowData.status=='确认未通过' || rowData.status=='审核未通过'){	
												if(rowData.bianji==true){
													return '<a href="javascript:editFunFun(\''+rowData.code+'\');">编辑</a>';	
												}
											}
											 */
											return '<a href="javascript:endcheckFun(\''
													+ rowData.id
													+ '\');">结束盘点</a>';

										}
									} ] ]
						})
	});

	function searchFun() {

		datagrid.datagrid('load', {
			areaId : $('#tb input[name=area]').val(),
			checkType : $('#tb select[name=checkType]').val(),
			beginDatetime : $('#tb input[name=beginDatetime]').val(),
			endDatetime : $('#tb input[name=endDatetime]').val(),
			code : $('#tb input[name=code]').val(),
		});
	}

	function toDetails(value) {
		window.location.href = "${pageContext.request.contextPath}/admin/dcheck/dcheckDetails.jsp?code="
				+ value;
	}

	function endcheckFun(value) {
		//结束盘点
		if (confirm("确定要结束盘点么?")) {
			$.ajax({
						type : "POST",
						dataType : 'json',
						url : '${pageContext.request.contextPath}/dCheckController/endDCheck.mmx',
						data : "id=" + value,
						success : function(msg) {
							$.messager.show({
								msg : msg.msg,
								title : '提示'
							});
							$('#datagrid').datagrid("reload")
						}
					});
		}
	}
	function toaddDCheck(type){
		var title;
	 if(type==2){
		 title="大盘"; 
	 }
	 if(type==1){
		 title="动碰盘"; 
	 }
	 $("#type").val(type);//临时保存盘点类型
	 
	 $("#dlg").dialog("open").dialog('setTitle', '添加'+title+"盘点计划"); ;
      
	}
	function addDCheck(){
		 var type=$("#type").val();
		  var areaid=$('input[name=addarea]').val();
		  
		if(areaid ==-1){
			alert("请选择地区!");
		}else{
		
			$.ajax({
				type : "POST",
				dataType : 'json',
				url : '${pageContext.request.contextPath}/dCheckController/addDCheck.mmx',
				data : {areaid:areaid,type:type},
				success : function(msg) {
					$.messager.show({
						msg : msg.msg,
						title : '提示'
					});
					$('#dlg').dialog('close');
					$('#datagrid').datagrid("reload")
				}
			});
			
		}
	 
	}
	
 function closeDCheck(){
	//$("#fm").form("clear");
	$('#dlg').dialog('close');
 }
</script>

</head>
<body>
	<table id="datagrid"></table>

	<div id="tb" style="height: auto; display: none;">
		<fieldset>
			<legend>盘点计划列表</legend>
			<table class="tableForm" border="0">
				<tr align="center">

					<td align="left"><input id="cc" class="easyui-combobox"
						name="area"
						data-options="valueField:'id',textField:'text',url:'${pageContext.request.contextPath}/Combobox/getAllArea.mmx'" />
						</td>


					<td align="left"><select name="checkType" id="checkType" />
						<option value="0" />请选择盘点类型
						</option>
						<option value="1" />动碰盘
						</option>
						<option value="2" />大盘
						</option> </select></td>
					<th>创建时间</th>
					<td align="left"><input id="beginDatetime"
						name="beginDatetime" class="easyui-datebox" />&nbsp;到&nbsp; <input
						id="endDatetime" name="endDatetime" class="easyui-datebox" /></td>
					<th>盘点计划号</th>
					<td align="left"><input id="code" name="code" /></td>
					<td align="right"><a class="easyui-linkbutton"
						iconCls="icon-search" plain="true" onclick="searchFun();"
						href="javascript:void(0);">查询</a></td>
				</tr>
				<tr align="center">
					<td align="right"><a class="easyui-linkbutton"
						iconCls="icon-add" plain="true" onclick="toaddDCheck(2);"
						href="javascript:void(0);">新建大盘盘点计划</a></td>
					<td align="right"><a class="easyui-linkbutton"
						iconCls="icon-add" plain="true" onclick="toaddDCheck(1);"
						href="javascript:void(0);">新建动碰盘盘点计划</a></td>

				</tr>
			</table>
		</fieldset>
	</div>
	
<div id="dlg" class="easyui-dialog" style="width: 320px; height: 200px; padding: 3px 5px;"
       closed="true" buttons="#dlg-buttons"> 
    <form id="fm" method="post"> 
       <div class="fitem"> 
           <label> 
               库地区
           </label> 
          <input id="cc" class="easyui-combobox"
						name="addarea"
						data-options="valueField:'id',textField:'text',url:'${pageContext.request.contextPath}//Combobox/getAllArea.mmx'" />
       </div> 
       <div class="fitem"> 
           <label> 
               创建人</label> 
           <input name="AccountName" id="AccountName" value="<%=user.getUsername()%>"  readonly style="width: 123px; height: 16px"/> 
       </div> 
     
       <div class="fitem"> 
           <label> 
               创建时间</label> 
           <input name="CreateTime" id="CreateTime"  value="<%=currentDate %>" readonly style="width: 123px; height: 16px"/> 
       </div> 
         <input type="hidden" name="type" id="type"  />
     </form> 
   </div> 
<div id="dlg-buttons"> 
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="addDCheck()" iconcls="icon-save">保存</a> 
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="closeDCheck()" iconcls="icon-cancel">取消</a> 
    </div> 
</body>
</html>