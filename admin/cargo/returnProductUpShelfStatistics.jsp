<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../js/easyui.jsp"></jsp:include>
</head>
<script type="text/javascript">
$(function(){
	$('#productLine').combobox({   
		url:'<%=request.getContextPath()%>/admin/returnPackage.do?method=getProductLine',   
		valueField:'id',   
		textField:'text',
		editable : false,
		Value:'-1'
	}); 
	$('#areaId').combobox({   
		url:'<%=request.getContextPath()%>/admin/returnPackage.do?method=getLimitArea',   
		valueField:'id',   
		textField:'text',
		editable : false,
		Value:'-1'
	}); 
});  
function searchFun(){
	var time = $('#time').datebox('getValue');
	var areaId = $('#areaId').combobox('getValue');
	var productLine = $('#productLine').combobox('getValue');
	var userId = $('#userId').val();
	var productCode = $('#productCode').val();
	jQuery.post('<%=request.getContextPath()%>/admin/returnPackage.do?method=returnProductUpShelfStatistics&time='+time 
			+ '&areaId=' + areaId + '&productLine=' + productLine + '&userId=' + userId +'&productCode=' + productCode,
	function(result){
		jQuery("#resultInfo").html(result);
		jQuery("#resultInfo").fadeIn();
	});	
}
function hiddenSelf(){
	jQuery("#resultInfo").fadeOut();
}
</script>
<body>
退货上架汇总单统计：地区：<input id="areaId" name="areaId" style="width:150px" value=""/><br>
<fieldset style="border:#06c dashed 1px; width: 70%">
	<legend></legend>
	退货上架汇总单统计：
	<table>
		<tr align="center">
			<td>
				制单时间：<input id="time" name="time" class="easyui-datebox" required="true" editable="false" style="width: 150px;" />
			</td>
			<td>
				产品线：<input id="productLine" name="productLine" value=""/>
			</td>
			<td>
				制单人：<input id="userId" name="userId" value="" />
			</td>
		</tr>
		<tr>
			<td>
				产品编号：<input id="productCode" name="productCode" value="" />
			</td>
			<td>
				&nbsp;
			</td>
			<td align="right">
				<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a>
			</td>
		</tr>
	</table>
</fieldset>
	<div id="resultInfo" style="display:none;width:70%; float:left; margin-left:15px;height:50px; border-style:solid;border-width:1pt;"></div>
</body>
</html>
