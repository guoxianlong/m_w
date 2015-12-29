<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>用户更新日志</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;

$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkEffectLog.mmx',
		toolbar : '#toolbar',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[  
	  			{field:'addTime',title:'时间',width:30,align:'center',
	  				formatter:function(value,row,index){
	                      return value; 
	  	        	}	
	  			},
	  			{field:'operationUserName',title:'操作人',width:30,align:'center'},
	  	        {field:'trunkName',title:'干线公司',width:30,align:'center'},
	  	        {field:'deliverAdminName',title:'用户名',width:30,align:'center'},
	  	        {field:'stockAreaName',title:'发货仓',width:30,align:'center'},
	  	        {field:'deliverName',title:'目的地',width:30,align:'center'},
	  	        {field:'mode',title:'配送方式',width:30,align:'center',
	  	        	formatter:function(value,row,index){
	  	        		if(row.mode=='1'){
	  	        			return '公路'; 
	  	        		}
	  	        		if(row.mode=='2'){
	  	        			return '铁路'; 
	  	        		}
	  	        		if(row.mode=='3'){
	  	        			return '空运'; 
	  	        		}
	  	        	}
	  	        },
	  	        {field:'time',title:'时效H',width:30,align:'center'}
	  	    ]]
	}); 
	
	$('#username').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getDeliverAdminUser.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:false
    });
	
	$('#trunkName').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
    });
	
	$('#stockAreaId').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getBIStockArea.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
    });
});

function searchFun() {
	var trunkId = $('#trunkName').combobox('getValue');
	var deliverAdminId  = $('#username').combobox('getValue');
	var stockArea = $('#stockAreaId').combobox('getValue');
	datagrid.datagrid('load', {
		TrunkId : trunkId,
		deliverAdminId : deliverAdminId,
		stockAreaId : stockArea,
	});
}

</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
		<table>
			<tr>
				<td>干线公司:</td>
				<td><input id='trunkName' name='trunkName'/>&nbsp;&nbsp;</td>
				<td>用户名:</td>
				<td><input id='username' name='username' type='text' maxlength="30" />&nbsp;&nbsp;</td>
				<td>发货仓:</td>
				<td><input id='stockAreaId' name='stockAreaId' type='text' maxlength="30" />&nbsp;&nbsp;</td>
				<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
			</tr>
		</table>
	</div>
	<table id="datagrid"></table> 
</body>
</html>