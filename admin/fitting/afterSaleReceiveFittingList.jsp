<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<title>配件领用单列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<%
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
%>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/fittingController/getReceiveFittingDatagrid.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[  
			{field:'id',title:'ID',width:20,hidden:true},
	        {field:'receiveCode',title:'领用单编号',width:40,align:'center'},  
	        {field:'createUserName',title:'填表人',width:40,align:'center'},  
	        {field:'fittingCount',title:'配件数量',width:25,align:'center'},  
	        {field:'targetName',title:'用途',width:25,align:'center'},  
	        {field:'statusName',title:'领用单状态',width:20,align:'center'},  
	        {field:'createDatetime',title:'创建时间',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}}, 
   		   {field:'operation',title:'操作',width:30,align:'center',
     			formatter : function(value,rowData,rowIndex){
     				var but = "";
  	        		 if(rowData.status == 1){
  	        			<%if(group.isFlag(2103)){%>
	        					but = '<a class="linkbutton" onclick="auditFun(' + rowData.id +');" href="javascript:void(0);">审核</a>';
	        			<%}%>
  	        		} else if(rowData.status == 2){
  	        			<%if(group.isFlag(2105)){%>
  	        					but = '<a class="linkbutton" onclick="editFun(' + rowData.id +');" href="javascript:void(0);">修改</a>';
        				<%}%>
  	        		} else if(rowData.status == 3){
  	        			<%if(group.isFlag(2104)){%>
		  	        			but = '<a class="linkbutton" onclick="printFun(' + rowData.id +');" href="javascript:void(0);">打印</a>';
        				<%}%>
  	        		} 
 	        			return '<a class="linkbutton" onclick="lookFun(' + rowData.id +');" href="javascript:void(0);">查看</a>&nbsp;' + but;
         		}},  
	    ]]
	});
	$(".linkbutton").linkbutton({ 
		plain:true,
	});
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterSaleArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#status').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getReceiveStatus.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		fittingName :  $('#tb input[name=fittingName]').val(),
		areaId : $('#areaId').combobox('getValue'),
		status : $('#status').combobox('getValue'),
		createUserName :  $('#tb input[name=createUserName]').val(),
	});
}
function auditReceiveFitting(type,receiveId){
	if($('#remark').val() == ''){
		$.messager.alert('错误','审核意见必填!','info');
		return;
	}
	$.ajax({
		url :'${pageContext.request.contextPath}/fittingController/auditReceiveFitting.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		asynchronous : false,
		data : {
				type : type,
				receiveId : receiveId,
				remark : $('#remark').val(),
		},
		success : function(r){
			if(r.success == true){
				datagrid.datagrid({});
				$('#dialog').dialog('close');
				$.messager.show({msg : r.msg,title : '提示'});    
			} else {
				$.messager.alert('错误',r.msg,'info');
			}
		}
	});
}
function editFun(receiveId){
	window.open("${pageContext.request.contextPath}/admin/fitting/afterSaleReceiveFittingEdit.jsp?receiveId=" + receiveId);
}
function printFun(receiveId){
	window.open("${pageContext.request.contextPath}/fittingController/getReceiveFittingDetail.mmx?receiveFittingId=" + receiveId);
}
function lookFun(receiveId) {
	$('#dialog_look').dialog({    
	    title: '配件领用单查看',   
	    href: '${pageContext.request.contextPath}/admin/fitting/afterSaleReceiveFittingLook.jsp', 
	    width: 700,    
	    height: 400,    
	    closed: false,    
	    cache : false,
	    modal: true,
		onLoad : function() {
			loadDetailDatagridLook(receiveId);
		}
	});  
}
function auditFun(receiveId) {
	$('#dialog').dialog({    
	    title: '配件领用单审核',   
	    href: '${pageContext.request.contextPath}/admin/fitting/afterSaleReceiveFittingAudit.jsp', 
	    width: 700,    
	    height: 400,    
	    closed: false,   
	    cache : false,
	    modal: true,
	    maximizable : true,
	    buttons:[{
	    	id : 'but1',
			text:'审核不通过',
			handler:function(){
				auditReceiveFitting(1,receiveId);
			}
		},{
			id : 'but2',
			text:'审核通过',
			handler:function(){
				auditReceiveFitting(2,receiveId);
			}
		}],
		onLoad : function() {
			loadDetailDatagridAudit(receiveId);
		}
	});  
}
function loadDetailDatagridLook(receiveId){
	$('#datagrid_look').datagrid({
		 url:'${pageContext.request.contextPath}/fittingController/getReceiveFittingDetailDatagrid.mmx',
		    queryParams : { receiveId :receiveId},
		    toolbar : '#tb_look',
		    idField : 'id',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : false,
		    loadMsg : '正在努力为您加载..',
		    rownumbers : true,
		    columns:[[  
		        {field:'fittingName',title:'配件名称',width:40,align:'center'},  
		        {field:'fittingCount',title:'配件数量',width:25,align:'center'},  
		        {field:'detectCode',title:'处理单号',width:40,align:'center'},  
		    ]]
	});
	$.ajax({
		url :'${pageContext.request.contextPath}/fittingController/getReceiveFittingInfo.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		data : {
				receiveId : receiveId,
		},
		success : function(r){
			if(r){
				var createDatetime_lable_look;
				if(r.obj.createDatetime != ''){
					createDatetime_lable_look = r.obj.createDatetime.substring(0,10);
				}
				$('#createDatetime_lable_look').empty();
				$('#createUserName_lable_look').empty();
				$('#target_lable_look').empty();
				$('#remark_look').empty();
				$('#createDatetime_lable_look').append(createDatetime_lable_look);
				$('#createUserName_lable_look').append(r.obj.createUserName);
				$('#target_lable_look').append(r.obj.target);
				$('#remark_look').append(r.obj.remark);
			} else {
				$.messager.alert('警告',"程序异常,加载失败!",'info');
			}
		}
	});
}
function loadDetailDatagridAudit(receiveId){
	$('#datagrid_audit').datagrid({
		 url:'${pageContext.request.contextPath}/fittingController/getReceiveFittingDetailDatagrid.mmx',
		    queryParams : { receiveId :receiveId},
		    toolbar : '#tb_audit',
		    idField : 'id',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : false,
		    loadMsg : '正在努力为您加载..',
		    rownumbers : true,
		    columns:[[  
		        {field:'fittingName',title:'配件名称',width:40,align:'center'},  
		        {field:'fittingCount',title:'配件数量',width:25,align:'center'},  
		        {field:'detectCode',title:'处理单号',width:40,align:'center'},  
		    ]]
	});
	$.ajax({
		url :'${pageContext.request.contextPath}/fittingController/getReceiveFittingInfo.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		data : {
				receiveId : receiveId,
		},
		success : function(r){
			if(r){
				
				var createDatetime_lable;
				if(r.obj.createDatetime != ''){
					createDatetime_lable = r.obj.createDatetime.substring(0,10);
				}
				$('#createDatetime_lable').empty();
				$('#createUserName_lable').empty();
				$('#target_lable').empty();
				$('#createDatetime_lable').append(createDatetime_lable);
				$('#createUserName_lable').append(r.obj.createUserName);
				$('#target_lable').append(r.obj.target);
			} else {
				$.messager.alert('警告',"程序异常,加载失败!",'info');
			}
		}
	});
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table border="0">
				<tr>
					<th align="right">库区：</th>
					<td><input id="areaId" name="areaId" style="width: 120px;"/></td>
					<th align="right">配件名称：</th>
					<td><input id="fittingName" name="fittingName" style="width: 120px;"/></td>
					<th align="right">填表人：</th>
					<td><input id="createUserName" name="createUserName" style="width: 120px;"/></td>
					<th align="right">领用单状态：</th>
					<td><input id="status" name="status" style="width: 120px;"/></td>
					<%if(group.isFlag(2106)){%>
						<th align="right"></th>
						<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
					<%}%>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
	<div id="dialog"></div>  
	<div id="dialog_look"></div>  
</body>
</html>