<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>未妥投包裹列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#unCastPackageDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getUnCastPackageDataGrid.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[  
			{field:'packageCode',title:'包裹单号',width:60,align:'center'},
			{field:'receiveDatetime',title:'签收时间',width:100,align:'center',
				formatter : function(value, row, index) {
        			return value != null && value != undefined ? value.substring(0, 19) : '';
				}
			},
			{field:'receiveUserName',title:'接收人姓名',width:80,align:'center'},
	        {field:'afterSaleOrderCodes',title:'售后单号',width:60,align:'center',
	        	formatter : function(value, row, index) {
	        		return replaceAllStr(value, ",","<br\>");
				}
			},
			{field:'afterSaleDetectProductCodes',title:'售后处理号',width:60,align:'center',
	        	formatter : function(value, row, index) {
	        		return replaceAllStr(value, ",","<br\>");
				}
			}
	    ] ],
		onLoadSuccess : function(data) {
		}
	});
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});

function replaceAllStr(theStr, replaceStrA, replaceStrB) 
{ 
   var re=new RegExp(replaceStrA, "g"); 
   var newstart = theStr.replace(re, replaceStrB); 
   return newstart;
} 

function searchFun() {
	datagrid.datagrid("load", {
		packageCode:$("#tb input[id=packageCode]").val(),
		afterSaleDetectProductCode:$("#tb input[id=detectProductCode]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue"),
		areaId : $('#areaId').combobox('getValue'),
	});
}

</script>
</head>
<body>
	<table id="unCastPackageDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<input type="hidden" name="id" value="-1"/>
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>包裹单号：</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px;"/>
					</td>
					<th>售后处理号：</th>
					<td align="left">
						<input id="detectProductCode" name="detectProductCode" style="width: 116px;"/>
					</td>
					<th>售后地区：</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 116px;"/>
					</td>
					<th>签收时间：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-search',plain:true" onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>