<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function($) {
	 Date.prototype.format = function(format) {
		    var o = {
		      "M+": this.getMonth() + 1, // month
		      "d+": this.getDate(), // day
		      "h+": this.getHours(), // hour
		      "m+": this.getMinutes(), // minute
		      "s+": this.getSeconds(), // second
		      "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
		      "S": this.getMilliseconds()
		      // millisecond
		    }
		    if (/(y+)/.test(format))
		      format = format.replace(RegExp.$1, (this.getFullYear() + "")
		        .substr(4 - RegExp.$1.length));
		    for (var k in o)
		      if (new RegExp("(" + k + ")").test(format))
		        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		    return format;
	};
	
	function parseToDate(value) {  
	    if (value == null || value == '') {  
	        return undefined;   
	    }  
	  
	    var dt;  
	    if (value instanceof Date) {  
	        dt = value;  
	    }  
	    else {  
	        if (!isNaN(value)) {  
	            dt = new Date(value);  
	        }  
	        else if (value.indexOf('/Date') > -1) {  
	            value = value.replace(/\/Date(−?\d+)\//, '$1');  
	            dt = new Date();  
	            dt.setTime(value);  
	        } else if (value.indexOf('/') > -1) {  
	            dt = new Date(Date.parse(value.replace(/-/g, '/')));  
	        } else {  
	            dt = new Date(value);  
	        }  
	    }  
	    return dt;  
	}	
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/qryTrunkOrderInfo.mmx',
		border: true,
		fitColumns : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    collapsible:true,
	    pageSize : 30,
	    pageList : [10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    onBeforeLoad:function(){
	    	if($("#searchFlag").val()=="0"){
	    		return false;
	    	}else{
	    		return true;
	    	}
	    },
	    columns:[[
	  	        {field:'nodeTime',title:'时间',width:150,align:'center',
	  	        	formatter:function(val,rec){ 
	  	        		if(val!=null&&val!=""){
	  	        			var dt = parseToDate(val);  
	  	        			return dt.format("yyyy-MM-dd hh:mm:ss");
	  	        		}else{
	  	        			return "";
	  	        		}
	  	        	}	
	  	        },
	  	        {field:'opUser',title:'操作人',width:150,align:'center',
	  	        	formatter:function(val,rec){ 
	  	        		if(rec.sysOpUser=='0'){
	  	        			return "系统生成";
	  	        		}else if(rec.sysOpUser){
	  	        			return rec.sysOpUserName;
	  	        		}else{
	  	        			return rec.opUserName;
	  	        		}
	  	        	}		
	  	        },  
	  	        {field:'code',title:'干线单号',width:150,align:'center'}, 
	  	        {field:'status',title:'配送状态',width:150,align:'center',
	  	        	formatter:function(val,rec){ 
	  	        		if(val=='1'){
	  	        			return "已交接";
	  	        		}else if(val=='2'){
	  	        			return "正常配载";
	  	        		}else if(val=='3'){
	  	        			return "运输中";
	  	        		}else if(val=='4'){
	  	        			return "派送中";
	  	        		}else if(val=='5'){
	  	        			return "派送完成";
	  	        		}
	  	        	}		
	  	        },
	  	        {field:'size',title:'体积m³',width:150,align:'center'},
	  	        {field:'weight',title:'重量kg',width:150,align:'center'},
	  	        {field:'mode',title:'配送方式',width:150,align:'center',
	  	        	formatter:function(val,rec){ 
	  	        		if(val=='1'){
	  	        			return "公路";
	  	        		}else if(val=='2'){
	  	        			return "铁路";
	  	        		}else if(val=='3'){
	  	        			return "空运";
	  	        		}
	  	        	}		
	  	        }
	  	    ]]
	});
	var flag = '${param.trunkOrderId}';
	if(flag!=""){
		searchFunB();
	}
});
function searchFun(){
	$("#searchFlag").val("1");
	if($.trim($("#trunkOrderCode").val())==''&&$.trim($("#orderCode").val())==''){
		$.messager.show({
			msg : '请至少输入一项查询条件',
			title : '提示'
		});
		return;
	}
	$('#datagrid').datagrid('load', {
		trunkOrderCode: $.trim($('#trunkOrderCode').val()),
		orderCode: $.trim($('#orderCode').val())
	});	
	
}

function searchFunB(){
	$("#searchFlag").val("1");
	$('#datagrid').datagrid('load', {
		trunkOrderId: '${param.trunkOrderId}'
	});	
}
</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
<input type="hidden" value="0" id="searchFlag">
			<fieldset>
			<table>
				<tr>
					<td>干线单号:</td>
					<td><input id='trunkOrderCode' name='trunkOrderCode' type="text"/>&nbsp;&nbsp;</td>
					<td>订单号:</td>
					<td><input id='orderCode' name='orderCode' type="text"/>&nbsp;&nbsp;</td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>		    
			</table>
			</fieldset>
	</div>
	<table id="datagrid"></table> 
	<div id="messager" style="display:none;">Dialog Content.</div> 		
</body>
</html>
