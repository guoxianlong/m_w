<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>干线公司列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkCorpInfo.mmx',
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
	        {field:'name',title:'干线公司',width:30,align:'center'},
	        {field:'addTime',title:'添加时间',width:30,align:'center',
	        	formatter: function(value,row,index){
					return value.substring(0,19);
				}
	        },
	        {field:'operation',title:'删除',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="javascript:delFun(\''+row.id+'\');">删除</a>';
				}
			}
	    ]]
	}); 
		
	$('#trunkName2').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text'
    });
});

function addFun(){
	var trunkName = $('#trunkName').val().trim();
	if(trunkName==''){
		$.messager.show({
			title : '提示',
			msg : '干线公司不能为空',
		});
		return false;
	}
	
	$.ajax({
		url : '${pageContext.request.contextPath}/TrunkLineController/addTrunk.mmx',
		data : {'trunkName':trunkName},
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

function delFun(id){	
	$.ajax({
		url : '${pageContext.request.contextPath}/TrunkLineController/upDateTrunkCorpInfo.mmx',
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
 
function searchFun() {
	var trunkName = $('#trunkName2').combobox('getText');
	datagrid.datagrid('load', {
		trunkName2 : trunkName
	});
}
function exportFun() {
	$("#searchForm").submit();
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
					<td><a class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addFun();" href="javascript:void(0);">添加</a></td>
				</tr>
			</table>
		</fieldset>
		<fieldset>
			<table>
				<tr>
					<td>干线公司:</td>
					<td><input id='trunkName2' name='trunkName'/>&nbsp;&nbsp;</td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>