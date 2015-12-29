<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
%>
<html>
  <head>
    
    <title>商品物流分类优先级调整</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<style type="text/css">
	body {
		padding: 0px;
	}
	form {
		padding: 0px;
		margin-left: 0px;
		margin-right:0px;
	}
</style>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,10}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   			$.messager.alert("提示","请不要输入大于10位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	   				$.messager.alert("提示","请填入整数！！");
	   			}
   			}
   		}	
   		
   		function deleteColum(id, name){
			$.messager.confirm("提问", "您确定要删除该条目么？", function (r) {
				if( r ) {
					window.location="productWarePropertyAction.do?method=deleteCheckEffect&id="+id + "&name="+ name;
					return;
				} else {
					return;
				}
				
			});
		}
		function addToChange(tail) {
			$("#change_"+tail).removeAttr("disabled");
		} 
		
		function check() {
			var x = "";
			<%
			 if( list != null && list.size() != 0 ) {
				 for( int i = 0; i < list.size(); i++ ) {
				 	ProductWareTypeBean pwtBean = (ProductWareTypeBean) list.get(i);
			%>
				x = $("#sequence_<%= pwtBean.getId()%>").val();
				if( x == null || x == "" ) {
					$.messager.alert("提示","有为空的优先级，不能提交！");
					$("#sequence_<%= pwtBean.getId()%>").focus();
					return;
				}
			<%
				}
			}
			%>
			document.changeForm.submit();
		}
		
	</script>

  </head>
  <body>
  <form name="changeForm" action="editProductWareTypeSequence.mmx" method="post">
  	<script type="text/javascript" >
		$(function(){
		   $('#info_table').datagrid({   
		    title:'调整商品物流分类优先级', 
		    iconCls:'icon-ok',
		    fitColumns : true,
		    border : true,    
		    pageNumber:1,  
		    pageSize:15,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'<%=request.getContextPath()%>/admin/getProductWareTypeInfo.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[   
	                    {field:'a',title:'商品物流分类', rowspan:2,width:240,align:'center'},   
	                    {field:'b',title:'优先级',rowspan:2,width:230,align:'center'} 
	                ]],
		    pagination:true,
		    rownumbers:false, //是否有行号。
		    onLoadSuccess: function(data) {
	    		if( data['tip'] != null ) {
		    		jQuery.messager.alert("提示", data['tip']);
		    	}
	    		$('#info_table').datagrid('getPager').pagination({
				    displayMsg:'当前显示从{from}到{to}共{total}记录',
				    onBeforeRefresh:function(pageNumber, pageSize){
				     $(this).pagination('loading');
				     $(this).pagination('loaded');
				    }
		    
		    	 });
		    }
		   });
		  });
	</script>
	<table align="center" id="info_table" >
	</table>
	<div align="center">
				<a href="javascript:check();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'">提交</a>
		</div>
	</form>
  
</body>
</html>
