<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$(function() {
	$.ajax({
		url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonStatisticChild.mmx',
		data : "flag=day",
		dataType : 'json',
		success : function(result) {
			try {
				var data = result;
				//处理返回结果，并显示数据表格
				var columnslength = data.columns[1].length;
				for (var i = 0 ; i < columnslength; i++) {
					if (i % 2 == 1) {
						data.columns[1][i].styler = cellStyler;
					}
				}
			    var options = {
		    		collapsible:true,
					fit : true,
					border : true,
					rownumbers : true,
					singleSelect : true,
					striped : true,
					idField : 'id',
					nowrap : false,
					showFooter:true,
					columns : data.columns
			    };
			    var dataGrid = $("#returnsReasonStatisticChildDataGrid");
			    dataGrid.datagrid(options);//根据配置选项，生成datagrid
			    dataGrid.datagrid("loadData", data.data.rows); //载入本地json格式的数据
			} catch (e) {
				$.messager.alert("提示", "异常", "info");
			}
		}
	});
});
function cellStyler(value,row,index){
	return 'color:red;';
}
</script>
<table id="returnsReasonStatisticChildDataGrid"></table>
