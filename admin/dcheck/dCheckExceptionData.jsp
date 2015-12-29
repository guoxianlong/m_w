<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>一盘复核列表</title>
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
		url:"<%=request.getContextPath()%>/dCheckController/getDCheckExceptionData.mmx",
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
		{field:'dCheckCode',title:'盘点号', width:100,align:'center'},  
		{field:'areaName',title:'仓库', width:100,align:'center'},   
		{field:'stockAreaCode',title:'库区', width:100,align:'center'},   
        {field:'passageCode',title:'巷道',width:100,align:'center'},   
        {field:'cargoWholeCode',title:'货位号',width:150,align:'center'},
        {field:'productCode',title:'产品编号',width:150,align:'center'},
        {field:'productName',title:'商品原名称',width:250,align:'left'}
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
		cargoCode : $.trim($('#tb input[name=cargoCode]').val()),
		productCode : $.trim($('#tb input[name=productCode]').val())
	});		
}
function clearFun(){
	$('#tb input').val('');
	initQueryData();
}
function printFun(){
	var param="";
	param+="?area="+$('#tb input[name=area]').val();
	param+="&dCheckCode="+$.trim($('#tb input[name=dCheckCode]').val());
	param+="&stockArea="+$('#tb input[name=stockArea]').val();
	param+="&passage="+$('#tb input[name=passage]').val();
	param+="&cargoCode="+$.trim($('#tb input[name=cargoCode]').val());
	param+="&productCode="+$.trim($('#tb input[name=productCode]').val());
	window.open("<%=request.getContextPath()%>/dCheckController/printDCheckExceptionData.mmx"+param,"_blank");
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
				盘点号：<input name="dCheckCode" />
				&nbsp;&nbsp;
				货位号：<input name="cargoCode" />
				&nbsp;&nbsp;
				产品编号：<input name="productCode" />
			</div>
			</br>
			<div align="right">
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();">查询 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();">清空 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="printFun();">导出 </a>
			</div>
		</fieldset>
	</div>    
    <table id="datagrid" style="height:auto;width:auto;"> 
</table>
</script>
</body>
</html>