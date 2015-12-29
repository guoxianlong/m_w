<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>添加路线关系表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkEffectForLine.mmx',
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
	        {field:'trunkName',title:'干线公司',width:30,align:'center'},
	        {field:'deliverAdminName',title:'用户名',width:30,align:'center'},
	        {field:'stockAreaName',title:'发货仓',width:30,align:'center'},
	        {field:'deliverName',title:'目的地',width:30,align:'center'},
			{field:'operation2',title:'删除',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="javascript:delFun(\''+row.id+'\');">删除</a>';
				}
			}
	    ]]
	}); 
	
	$('#username').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:true
    });
	
	$('#trunkName').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		width : 150,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#username').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverAdminUser.mmx?deliverId='+id,
					valueField:'id',
					textField:'text',
					width : 150,
					editable :false,
					disabled:false
					
			    });
			}else{
				$('#username').combobox({
					width : 150,
					disabled:true
			    });
			}
			
		}
    });
	
	$('#stockAreaId').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getBIStockArea.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:false
    });
	
	$('#deliverId').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getDeliverAll.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:false
    });
	
	$('#username2').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:true
    });
	
	$('#trunkName2').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#username2').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverAdminUser.mmx?deliverId='+id,
					valueField:'id',
					textField:'text',
					width : 150,
					editable :false,
					disabled:false
					
			    });
			}else{
				$('#username2').combobox({
					width : 150,
					disabled:true
			    });
			}
			
		}
    });
	
	$('#deliverId2').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:true
    });
	
	$('#stockAreaId2').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getBIStockArea.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#deliverId2').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverByStockAreaId.mmx?stockAreaId='+id,
					valueField:'id',
					textField:'text',
					width : 150,
					editable :false,
					disabled:false
					
			    });
			}else{
				$('#deliverId2').combobox({
					width : 150,
					disabled:true
			    });
			}
			
		}
    });
});

function addFun(){
	var trunkId = $('#trunkName').combobox('getValue');
	var deliverAdminId = $('#username').combobox('getValue');
	var stockAreaId = $('#stockAreaId').combobox('getValue');
	var deliverId = $('#deliverId').combobox('getValue');
	if(username==''){
		$.messager.show({
			title : '提示',
			msg : '用户名不能为空',
		});
		return false;
	}
	
	$.ajax({
		url : '${pageContext.request.contextPath}/TrunkLineController/addTrunkEffect.mmx',
		data : {
			'trunkId':trunkId,
			'deliverAdminId':deliverAdminId,
			'stockAreaId':stockAreaId,
			'deliverId':deliverId
			},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success==true) {
					$('#trunkName').combobox('reload','<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx');
					$('#trunkName2').combobox('reload','<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx');
					datagrid.datagrid("reload",{});
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	})
}

function delFun(id){
	$.messager.confirm('确认','警告！删除后将无法在添加本条记录',function(r){    
	    if (r){    
	    	$.ajax({
	    		url : '${pageContext.request.contextPath}/TrunkLineController/delTrunkEffect.mmx',
	    		data : {'id':id},
	    		type : 'post',
	    		success : function(result){
	    			try {
	    				var r = $.parseJSON(result);
	    				if (r.success==true) {
	    					$('#trunkName2').combobox('reload','<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx');
	    					datagrid.datagrid("reload");
	    				} else {
	    					$.messager.show({
	    						title : '提示',
	    						msg : decodeURI(r.msg)
	    					});
	    				}
	    			} catch (e) {
	    				$.messager.alert('提示', result);
	    			}
	    		}
	    	})    
	    }    
	});  
}
 
 
function searchFun() {
	var trunkId = $('#trunkName2').combobox('getValue');
	var deliverAdminId  = $('#username2').combobox('getValue');
	var stockAreaId  = $('#stockAreaId2').combobox('getValue');
	var deliverId  = $('#deliverId2').combobox('getValue');
	datagrid.datagrid('load', {
		trunkId : trunkId,
		deliverAdminId : deliverAdminId,
		stockAreaId : stockAreaId,
		deliverId : deliverId
	});
}

</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
		<fieldset>
			<table>
				<tr>
					<td>干线公司:</td>
					<td><input id='trunkName' name='trunkName' type='text' maxlength="30" />&nbsp;&nbsp;</td>
					<td>用户名:</td>
					<td><input id='username' name='username' type='text' maxlength="30" />&nbsp;&nbsp;</td>
					<td>发货仓:</td>
					<td><input id='stockAreaId' name='stockAreaId' type='text' maxlength="30" />&nbsp;&nbsp;</td>
					<td>目的地:</td>
					<td><input id='deliverId' name='deliverId' type='text' maxlength="11" />&nbsp;&nbsp;</td>
					<td><a class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addFun();" href="javascript:void(0);">添加</a></td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<table>
				<tr>
					<td>干线公司:</td>
					<td><input id='trunkName2' name='trunkName'/>&nbsp;&nbsp;</td>
					<td>用户名:</td>
					<td><input id='username2' name='username' type='text' maxlength="30" />&nbsp;&nbsp;</td>
					<td>发货仓:</td>
					<td><input id='stockAreaId2' name='stockAreaId' type='text' maxlength="30" />&nbsp;&nbsp;</td>
					<td>目的地:</td>
					<td><input id='deliverId2' name='deliverId' type='text' maxlength="11" />&nbsp;&nbsp;</td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>