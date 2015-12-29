<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/deliverController/getDeliverPackageCodeList.mmx',
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
	  	        {field:'count',title:'剩余量',width:30,align:'center',
	     	    	formatter : function(value, row, index) {
	     	    		if(row.count==null){
							return '0';
	     	    		}else{
	     	    			return row.count;
	     	    		}
					}	
	  	        },  
	  	        {field:'add_package_code_count',title:'最后一次注入量',width:30,align:'center'},  
	  	        {field:'add_package_code_datetime',title:'最后一次注入时间',width:30,align:'center',
	  	        	formatter : function(value, row, index) {
	  					var d = new Date();
	  				    d.setTime(row.add_package_code_datetime);
	  				    return d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate()+' '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds();
	  					}
	  	        },
	  	        {
	  				field : 'action',
	  				title : '接口日志',
	  				align :'center',
	  				width : 100,
	  				formatter : function(value, row, index) {
	  							return '<a href="javascript:void(0);" class="editbutton" onclick="deliverLog('+row.id+')">日志</a>';
	  				}
	  			}
	    ]]
	}); 
});
function deliverLog(id){
	location.href = '${pageContext.request.contextPath}/deliverController/queryDeliverLog.mmx?type=1&deliverId='+id
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
</body>
</html>