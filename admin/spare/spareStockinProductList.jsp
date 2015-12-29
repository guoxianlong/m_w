<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<title>入库单备用机列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockinId = ${param.stockinId};
var audit = ${param.audit};
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/spareManagerController/getSpareStockinProductList.mmx?stockinId='+stockinId,
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    frozenColumns : [[
					{field:'id',hidden:true}
	    ]],
	    columns:[[  
	    	{field:'code',title:'备用机号',width:50,align:'center'},
	    	{field:'imei',title:'IMEI码',width:50,align:'center'}
	    ]],
	    onLoadSuccess : function(){
	    	if(!audit){
				$("#tb").hide();
			}
	    }
	}); 
			
});

function confirm(value){
	var remark = $.trim($("#remark").val());	
	if(value==1){
		 window.location.href = "${pageContext.request.contextPath}/spareManagerController/auditStockIn.mmx?stockinId="+ stockinId +"&remark="+$("#remark").val()+"&audit=1";
	}
	if(value==0){	
		if(remark!=''){
			 window.location.href = "${pageContext.request.contextPath}/spareManagerController/auditStockIn.mmx?stockinId="+ stockinId +"&remark="+$("#remark").val()+"&audit=0";
		}else{
			$.messager.alert('提示','请输入审核意见!');
		}
	}
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
	<div id="tb" style="height: auto;">
	<mmb:permit value="3012">
		<fieldset>
			<legend>备用机入库列表</legend>
			<table class="" >
				<tr align="center" >
				  <th>审核意见：</th>
				  <td>
				  	<input type="text" id="remark" name="remark"  size="50" maxlength="50"/>
				  	<input type="submit" name="Submit" value="审核通过" onclick="confirm(1)"/>
					<input type="submit" name="Submit" value="审核不通过" onclick="confirm(0)" />
				  </td>
				</tr>
			</table>
		</fieldset>
	</mmb:permit>
	</div>
</body>
</html>