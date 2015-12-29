<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
 <title>添加干线用户名列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/getTrunkUser.mmx',
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
	        {field:'username',title:'用户名',width:30,align:'center'},
	        {field:'password',title:'密码',width:30,align:'center'},
	        {field:'phone',title:'电话号码',width:30,align:'center'},
	        {field:'createDatetime',title:'添加时间',width:30,align:'center',
	        	formatter:function(value,row,index){
                    return value; 
	        	}},
	        {field:'lastModifyDatetime',title:'最后修改时间',width:30,align:'center',
        		formatter:function(value,row,index){
                    return value; 
	        	}},
	        {field:'operation',title:'修改',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="javascript:updateFun(\''+row.id+','+row.name+','+row.username+','+row.password+','+row.phone+','+row.trunkId+'\');">修改</a>';
				}
			},
			{field:'operation2',title:'删除',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="javascript:delFun(\''+row.id+'\');">删除</a>';
				}
			},
			{field:'operation3',title:'查看',width:20,align:'center',
	        	formatter: function(value,row,index){
	        		return '<a href="trunkUserLog.jsp?trunkId='+row.trunkId+'" target="_blank">查看</a>';
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
});

function addFun(){
	var username = $('#username').val().trim();
	var password = $('#password').val().trim();
	var phone = $('#phone').val().trim();
	var trunkEffectId = $('#trunkName').combobox('getValue');
	var trunkEffectName = $('#trunkName').combobox('getText');
	if(username==''){
		$.messager.show({
			title : '提示',
			msg : '用户名不能为空',
		});
		return false;
	}
	
	if(password==''){
		$.messager.show({
			title : '提示',
			msg : '密码不能为空',
		});
		return false;
	}
	
	$.ajax({
		url : '${pageContext.request.contextPath}/TrunkLineController/addTrunkUser.mmx',
		data : {
			'trunkEffectId':trunkEffectId,
			'trunkEffectName':trunkEffectName,
			'username':username,
			'password':password,
			'phone':phone
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
				url : '${pageContext.request.contextPath}/TrunkLineController/delTrunkUser.mmx',
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
 
function updateFun(row){	
	var arr=new Array();
	arr =row.split(',');
	$("#trunkName3").val(arr[1]);
	$("#username3").val(arr[2]);
	$("#password3").val(arr[3]);
	$("#phone3").val(arr[4]);
	$('#dd').dialog({    
	    title: '修改',    
	    width: 280,    
	    height: 200,    
	    closed: false,    
	    cache: false,    
	    modal: true,
	    buttons:[{
			text:'确定',
			handler:function(){
				$.ajax({
					url : '${pageContext.request.contextPath}/TrunkLineController/updateTrunkUser.mmx',
					data : {'trunkEffectId':arr[0],
							'username' : $("#username3").val(),	
							'password': $("#password3").val(),
							'phone':$("#phone3").val(),
							'trunkId':arr[5]
					},
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
	var trunkName = $('#trunkName2').combobox('getValue');
	var username  = $('#username2').combobox('getText');
	var phone = $("#phone2").val();
	datagrid.datagrid('load', {
		trunkName2 : trunkName,
		username2 : username,
		phone2 : phone
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
					<td>密码:</td>
					<td><input id='password' name='password' type='text' maxlength="30" />&nbsp;&nbsp;</td>
					<td>电话号码:</td>
					<td><input id='phone' name='phone' type='text' maxlength="11" />&nbsp;&nbsp;</td>
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
					<td>电话号码:</td>
					<td><input id='phone2' name='phone' type='text' maxlength="30" />&nbsp;&nbsp;</td>
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
				<td><input id='trunkName3' name='trunkName' disabled="disabled" maxlength="30"/></td>
			</tr>
			<tr>
				<td>用户名:</td>
				<td><input id='username3' name='username' disabled="disabled"  maxlength="30"/></td>
			</tr>
			<tr>
				<td>密码:</td>
				<td><input id='password3' name='password'  maxlength="30"/></td>
			</tr>
			<tr>
				<td>电话号码:</td>
				<td><input id='phone3' name='phone'  maxlength="11"/></td>
			</tr>
		</table>
	</div>  
</body>
</html>