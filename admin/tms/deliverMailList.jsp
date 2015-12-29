<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var date;
//var status = ${param.status};
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/deliverController/getDeliverMailList.mmx',
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
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
	    ]],
	    columns:[[  
	        {field:'name',title:'快递公司',width:30,align:'center'},
	        {field:'mail',title:'邮箱',width:30,align:'center'},  
	        {field:'transitCount',title:'当日交接',width:30,align:'center'},  
	        {field:'status',title:'发送状态',width:30,align:'center', 
	        formatter : function(value, row, index) {
				if(row.status==1){
					return '已发送';
				}else if(row.status==0){
				    return '未发送';
				}else{
					return '未发送';
				}
			}},
	        {field:'sendTime',title:'最后发送时间',width:30,align:'center'
			},
	        {
				field : 'action',
				title : '操作',
				align :'center',
				width : 100,
				formatter : function(value, row, index) {
							return '<a href="javascript:void(0);" class="editbutton" onclick="sendEmail('+row.id+')">手动发送</a>';
				}
			}
	    ]]
	}); 
});
function sendEmail(id){ 
	date = $('#date').datebox('getValue');
	location.href = '${pageContext.request.contextPath}/mailingBatchPackageController/sendEmail.mmx?deliverId='+id+"&date="+date;

}
function searchFun() {
	datagrid.datagrid('load', {
		date : $('#tb input[name=date]').val(),
	});
}
</script>
 <script>
//得到当前日期
formatterDate = function(date) {
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"+ (date.getMonth() + 1);
	return date.getFullYear() + '-' + month + '-' + day;
};

window.onload = function () { 
	$('#date').datebox('setValue', formatterDate(new Date()));
}
    </script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table>
				<tr>
					<th>日期：</th>
					<td><input id="date" name="date"   class="easyui-datebox" editable="false" style="width: 120px;" />
					<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>