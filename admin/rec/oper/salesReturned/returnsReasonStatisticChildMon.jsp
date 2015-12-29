<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$(function() {
	$.ajax({
		url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonStatisticChild.mmx',
		data : "flag=moon",
		dataType : 'json',
		cache : false,
		success : function(result) {
			try {
				var data = result;
				var columnslength = data.columns[1].length;
				for (var i = 0 ; i < columnslength; i++) {
					if (i % 2 == 1) {
						data.columns[1][i].styler = cellStyler;
					}
				}
				//处理返回结果，并显示数据表格
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
			    var dataGrid = $("#returnsReasonStatisticChildMonDataGrid");
			    dataGrid.datagrid(options);//根据配置选项，生成datagrid
			    dataGrid.datagrid("loadData", data.data.rows); //载入本地json格式的数据
			} catch (e) {
				$.messager.alert("提示", e, "info");
			}
		}
	});
});
function cellStyler (value,row,index){
	return 'color:red';
}
</script>
<table id="returnsReasonStatisticChildMonDataGrid"></table>
