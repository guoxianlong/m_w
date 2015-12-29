<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>盘点日志列表</title>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
<script type="text/javascript" >
$(document).ready(function(){
	$('#area').combobox({
      	url : '<%=request.getContextPath()%>/dCheckController/getDeptAreaComboBoxB.mmx',
      	valueField:'id',
		textField:'text',
		onSelect: function(rec) {
		    $('#passage').combobox({   
				url:'<%=request.getContextPath()%>/dCheckController/getPassageForStockArea.mmx?stockAreaId=-1',  
				valueField : 'id',   
				textField : 'text',
			    editable:false
			});  
			$('#stockArea').combobox({   
				url:'<%=request.getContextPath()%>/dCheckController/getStockAreaForAreaB.mmx?areaId='+rec['id'],  
				valueField : 'id',   
				textField : 'text',
			    editable:false,
				onSelect: function(rec) {
					$('#passage').combobox({   
						url:'<%=request.getContextPath()%>/dCheckController/getPassageForStockArea.mmx?stockAreaId='+rec['id'],  
						valueField : 'id',   
						textField : 'text',
					    editable:false
					});
				}
		});
		}
    });
	initQueryData();  
	$('#datagrid').datagrid({    
		url:"<%=request.getContextPath()%>/dCheckController/getDCheckAssignLog.mmx",
		nowrap:false,
		border:false,
		idField:"id",
		fit:true,
		fitColumns:true,
		title:"",
		pageSize :20,
		pageList:[ 20, 50,100, 200, 300 ],
		toolbar:"#tb", 
		rownumbers:true,
		pagination:true,
		singleSelect:true,
	    rowStyler:function(index,row){    
	        return 'color:black;font-weight:bold'; 
	    },
	    onBeforeLoad:function(){
	    	if($("#searchFlag").val()=="0"){
	    		return false;
	    	}else{
	    		return true;
	    	}
	    },	    
	    columns:[[
		{field:'dynamicCheckCode',title:'盘点号', width:100,align:'center'}, 
		{field:'areaName',title:'仓库', width:80,align:'center'},   
		{field:'stockAreaCode',title:'库区', width:80,align:'center'},   
        {field:'passageCode',title:'巷道',width:80,align:'center'},   
        {field:'groupId',title:'小组',width:80,align:'center',
        	formatter: function(value,rec,index){
        		if(value=="1"){
        			return "B组";
        		}else{
        			return "A组";
        		}
        	}  
        },
        {field:'username',title:'操作人',width:100,align:'center'},
        {field:'id',title:'操作',width:100,align:'center',
        	formatter: function(value,rec,index){
        		return '<a href="javascript:delFun(\''+rec.id+'\');">删除</a>';
        	}        	
        }
	]]  
	});
});
function searchFun() {
	$("#searchFlag").val("1");
	$('#datagrid').datagrid('load',{
		area : $('#tb input[name=area]').val(),
		dCheckCode : $.trim($('#tb input[name=dCheckCode]').val()),
		stockArea : $('#tb input[name=stockArea]').val(),
		passage : $('#tb input[name=passage]').val(),
		username : $.trim($('#tb input[name=username]').val()),
		groupId : $.trim($('#tb #groupId').val())		
	});
}
function delFun(logId){
	if(confirm("确认要删除此项数据？")){
		$.ajax({
			url : '${pageContext.request.contextPath}/dCheckController/delDCheckAssignLog.mmx',
			data : {'logId':logId},
			type : 'post',
			dataType : 'json',
			success : function(data){
				if (data.success==true) {
					$.messager.show({
						title : '提示',
						msg : data.msg
					});
					$('#datagrid').datagrid("reload");
				} else {
					$.messager.show({
						title : '提示',
						msg : data.msg
					});
				}
			}
		});	
	}
}
function clearFun(){
	$('#tb input').val('');
	initQueryData();
}
function initQueryData(){
    $('#stockArea').combobox({   
		url:'<%=request.getContextPath()%>/dCheckController/getStockAreaForAreaB.mmx?areaId=-1',  
		valueField : 'id',   
		textField : 'text',
	    editable:false,
		onSelect: function(rec) {
			$('#passage').combobox({   
				url:'<%=request.getContextPath()%>/dCheckController/getPassageForStockArea.mmx?stockAreaId='+rec['id'],  
				valueField : 'id',   
				textField : 'text',
			    editable:false
			});
		}
	});
    $('#passage').combobox({   
		url:'<%=request.getContextPath()%>/dCheckController/getPassageForStockArea.mmx?stockAreaId=-1',  
		valueField : 'id',   
		textField : 'text',
	    editable:false
	});   
}
</script>
</head>
<body>
<input type="hidden" value="0" id="searchFlag">
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
		<legend>筛选</legend>
			<div align="left">
				&nbsp;&nbsp;
				库地区：<input id="area" name="area" style="width:152px;border:1px solid #ccc" editable="false"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				区域：<input id="stockArea" name="stockArea" style="width:152px;border:1px solid #ccc" editable="false"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				巷道：<input id="passage" name="passage" style="width:152px;border:1px solid #ccc" editable="false"/>

			</div>
			</br>
			<div align="left">
				&nbsp;&nbsp;
				小组：<select id="groupId"><option value="">请选择</option><option value="0">A组</option><option value="1">B组</option></select>			
				&nbsp;&nbsp;
				盘点号：<input name="dCheckCode" />
				&nbsp;&nbsp;
				用户名：<input name="username" />
				
			</div>
			</br>
			<div align="right">
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();">查询 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();">清空 </a>
			</div>
		</fieldset>
	</div>    
    <table id="datagrid" style="height:auto;width:auto;"> 
</table>
</script>
</body>
</html>