<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>售后配件列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var flag = false;
var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleFittingsDatagrid.mmx',
	    toolbar : '#tb',
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
	    pageList : [ 20, 30,40, 50, 60, 70, 80, 90, 100, 200 ],
	    columns :[[   
	               {field:'fittingName',title:'配件名称', rowspan:2,width:100,align:'center',sortable:true},   
	               {field:'fittingCode',title:'配件编号',rowspan:2,width:300,align:'center',sortable:true},   
	               {field:'pname2',title:'二级分类',rowspan:2,width:200,align:'center'},
	               {field:'pname3',title:'三级分类',rowspan:2,width:240,align:'center'},
	               {field:'sumcount',title:'总量',rowspan:2,width:150,align:'center'},
	               {title:'深圳',colspan:2,width:150,align:'center'},
	               {title:'芳村',colspan:2,width:150,align:'center'}
	              
	               ],
	              [
	               {field:'swh',title:'完好数量',width:100,align:'center',sortable:true},
	               {field:'scc',title:'残次数量',width:100,align:'center',sortable:true},   
	               {field:'fwh',title:'完好数量', width:100,align:'center',sortable:true},
	               {field:'fcc',title:'残次数量', width:100,align:'center',sortable:true}
	              		                    
	           ]],
	}); 
	applyFrom = $('#applyFrom').form();
	$('#parentId2').combobox({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleCatalogNames.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		onSelect : function(record){
			flag = true;
			$('#parentId3').combobox({
				url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleCatalogNames.mmx?perentId='+record.id,
				valueField : 'id',
				textField : 'text',
				editable : false
			});
		}
	
	});
});
function searchFun() {
	var parentId3;
	if(flag){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	//alert(parentId3);
	datagrid.datagrid('load', {
		fittingName : $('#tb input[id=fittingName]').val(),
	    parentId2 : $('#parentId2').combobox('getValue'),
		parentId3 : parentId3
	 });
}
</script>

</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>售后配件列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
					<th >配件分类</th>
					<td align="left">
		         		<input id="parentId2" name="parentId2" class="easyui-combobox" style="width: 120px;"/>
						<input id="parentId3" name="parentId3" class="easyui-combobox"  style="width: 120px;"/></td>
					<th >配件名称</th>
					<td align="left">
						<input id="fittingName" name="fittingName" style="width: 116px "/></td>
			
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</form>
		</fieldset>
		
	</div>
</body>
</html>