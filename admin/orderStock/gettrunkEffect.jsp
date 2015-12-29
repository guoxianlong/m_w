<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>干线时效查询列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkEffectForTime.mmx',
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
	        {field:'mode',title:'配送方式',width:30,align:'center'},
	        {field:'time',title:'时效H',width:30,align:'center'},
	        {field:'operation',title:'修改',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="javascript:updateFun(\''+row.id+','+row.trunkName+','+row.deliverAdminName+','+row.stockAreaName+','+row.deliverName+','+row.mode+','+row.time+','+row.trunkId+','+row.deliverAdminId+','+row.stockAreaId+','+row.deliverId+'\');">修改</a>';
				}
			},
			{field:'operation2',title:'删除',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="javascript:delFun(\''+row.id+'\');">删除</a>';
				}
			},
			{field:'operation3',title:'查看',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="trunkEffectLog.jsp?trunkId='+row.trunkId+'" target="_blank">查看</a>';
				}
			}
	    ]]
	}); 
		
	$('#trunkName').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		width : 150,
		editable :false
    });
	$('#username').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 100,
		disabled:true
    });
	
	$('#trunkName').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		editable :false,
		width : 100,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#username').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverAdminUser.mmx?deliverId='+id,
					valueField:'id',
					textField:'text',
					width : 100,
					editable :false,
					disabled:false
					
			    });
			}else{
				$('#username').combobox({
					width : 100,
					disabled:true
			    });
			}
			
		}
    });
	
	$('#deliverId').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 100,
		disabled:true
    });
	
	$('#stockAreaId').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getBIStockArea.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 100,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#deliverId').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverByStockAreaId.mmx?stockAreaId='+id,
					valueField:'id',
					textField:'text',
					width : 100,
					editable :false,
					disabled:false
					
			    });
			}else{
				$('#deliverId').combobox({
					width : 100,
					disabled:true
			    });
			}
			
		}
    });
	
	$('#model').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getTrunkMode.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 100,
		disabled:false
    });
});

function delFun(id){	
	$.messager.confirm('确认','警告！删除后将无法在添加本条记录',function(r){    
	    if (r){
			$.ajax({
				url : '${pageContext.request.contextPath}/TrunkLineController/delTrunkEffectForTime.mmx',
				data : {'id':id},
				type : 'post',
				success : function(result){
					try {
						var r = $.parseJSON(result);
						if (r.success==true) {
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
 
function updateFun(row){	
	var arr=new Array();
	arr =row.split(',');
	$("#trunkName2").val(arr[1]);
	$("#username2").val(arr[2]);
	$('#stockAreaId2').val(arr[3]);
	$('#deliverId2').val(arr[4]);
	$('#mode2').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getTrunkMode.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:false
    });
	if(arr[6]=='undefined'){
		$("#time2").val();
	}else{
		$("#time2").val();
	}
	
	$('#dd').dialog({    
	    title: '修改',    
	    width: 300,    
	    height:250,    
	    closed: false,    
	    cache: false,    
	    modal: true,
	    buttons:[{
			text:'确定',
			handler:function(){
				$.ajax({
					url : '${pageContext.request.contextPath}/TrunkLineController/updateTrunkEffect.mmx',
					data : {'trunkEffectId':arr[0],
							'trunkId':arr[7],
							'trunkName':$("#trunkName2").val(),
							'deliverAdminName':$("#trunkName2").val(),
							'deliverAdminId':arr[8],
							'username' : $("#username2").val(),	
							'stockAreaId':arr[9],
							'deliverId':arr[10],
							'mode':$("#mode2").combobox('getValue'),
							'time':$("#time2").val()
					},
					type : 'post',
					success : function(result){
						try {
							var r = $.parseJSON(result);
							if (r.success==true) {
								//$('#trunkName2').combobox('reload','<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx');
								datagrid.datagrid("reload");
							} else {
								$.messager.show({
									title : '提示',
									msg : decodeURI(r.msg)
								});
							}
							$('#dd').dialog('close');
						} catch (e) {
							$.messager.alert('提示', result);
						}
					}
				})
			}
	    }]
	});    
} 
 
function searchFun() {
	var trunkId = $('#trunkName').combobox('getValue');
	var deliverAdminId  = $('#username').combobox('getValue');
	var stockAreaId = $('#stockAreaId').combobox('getValue');
	var deliverId = $('#deliverId').combobox('getValue');
	var model = $('#model').combobox('getValue');
	var time = $('#time').val();
	datagrid.datagrid('load', {
		trunkId : trunkId,
		deliverAdminId : deliverAdminId,
		stockAreaId : stockAreaId,
		deliverId : deliverId,
		mode : model,
		time : time
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
					<td>配送方式:</td>
					<td><input id='model' name='model' type='text' maxlength="11" />&nbsp;&nbsp;</td>
					<td>时效:</td>
					<td><input id='time' name='time' type='text' maxlength="5" size="5"/>小时&nbsp;&nbsp;</td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
	<div id="dd">
		<table>
			<tr>
				<td>干线公司:</td>
				<td><input id='trunkName2' name='trunkName2' disabled="disabled" maxlength="30"/></td>
			</tr>
			<tr>
				<td>用户名:</td>
				<td><input id='username2' name='username2'  disabled="disabled"  maxlength="30"/></td>
			</tr>
			<tr>
				<td>发货仓:</td>
				<td><input id='stockAreaId2' name='stockAreaId2' disabled="disabled"  maxlength="30"/></td>
			</tr>
			<tr>
				<td>目的地:</td>
				<td><input id='deliverId2' name='deliverId2' disabled="disabled"  maxlength="11"/></td>
			</tr>
			<tr>
				<td>配送方式:</td>
				<td><input id='mode2' name='mode2'  maxlength="11"/></td>
			</tr>
			<tr>
				<td>时效:</td>
				<td><input id='time2' name='time2'  maxlength="5"/></td>
			</tr>
		</table>
	</div>
</body>
</html>