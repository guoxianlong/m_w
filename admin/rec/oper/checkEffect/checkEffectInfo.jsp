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
    
    <title>商品质检分类与效率</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
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
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function toAddPage() {
   			window.location="<%= request.getContextPath()%>/admin/rec/oper/checkEffect/addCheckEffect.jsp";
   		}
   		
   		function deleteColum(id, name){
   			jQuery.messager.confirm("问题", "您确定要删除该条目么？", function (r) {
   				if( r ) {
   					window.location="deleteCheckEffect.mmx?id="+id + "&name="+ encodeURI(name);
   				} else {
   					
   				}
   			});
		}
	</script>

  </head>
  <body fit="true">
  	<div id="tb">
  		<table align="center" width="100%" border="0" cellspacing="1px" bgcolor="#99CCFF" cellpadding="1px">
  			<tr align="center">
  				<td bgcolor="#CCFFFF" width="50%" align="center" valign="middle">
  				<h2>
  				<a href="<%= request.getContextPath()%>/admin/toCheckEffectInfo.mmx">商品质检分类与效率
  				</a>
  				</h2>
  				</td>
  				<td bgcolor="#FFFFFF" align="center" valign="middle">
  				<h2>
  					<a href="<%= request.getContextPath()%>/admin/getCheckStaffWorkPlanInfo.mmx">质检排班计划</a>
  				</h2>
  				</td>
  			</tr>
  		</table>
  		<a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls= "icon-add" plain= "true" onclick= "ceAdd();">添加质检效率</a>
        <a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls= "icon-edit" plain= "true" onclick= "ceEdit();">编辑 </a> 
        <a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls= "icon-remove" plain= "true" onclick= "ceDell();">删除</a>   
  		
  	</div>
  	
  	<!-- <a href="javascript:toAddPage();" class="easyui-linkbutton" data-options="iconCls:'icon-add'">添加</a> 
  	<br/>
  	-->
  	<div id= "menu" class= "easyui-menu" style= "width:120px;display: none;" >
             <div onclick= "ceAdd();" iconCls= "icon-add" >添加质检效率</div>
             <div onclick= "ceEdit();" iconCls= "icon-edit" >编辑 </div>
             <div onclick= "ceDell();" iconCls= "icon-remove" >删除 </div>
    </div>
  	<script type="text/javascript" >
  		function editCheckEffect(checkEffectId,checkEffectName,checkEffectEffect) {
  			window.location = "<%= request.getContextPath()%>/admin/rec/oper/checkEffect/editCheckEffect.jsp?id="
			+ checkEffectId + "&name=" + checkEffectName
			+ "&effect=" + checkEffectEffect;
  		}
  	
		$(function(){
			$('#info_table').datagrid({
                   onRowContextMenu : function (e, rowIndex, rowData) {
                              e.preventDefault();
                              $( this ).datagrid('unselectAll' );
                              $( this ).datagrid('selectRow' , rowIndex);
                              $( '#menu' ).menu('show' , {
                                    left : e.pageX,
                                    top : e.pageY
                              });
                        }
                  });
		   $('#info_table').datagrid({   
		    fit:true,
		    fitColumns : true,
		    border : true,  
		    pageNumber:1,  
		    pageSize:10,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'../admin/getCheckEffectInfo.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false,
		    singleSelect:true,
		    columns:[[
		     {title:'商品质检分类',field:'check_effect_name',width:365,rowspan:3,align:'center',sortable:true},
		     {title:'产品线',field:'product_lines',width:300,rowspan:3,align:'center',sortable:true},
		     {title:'效率(件/小时)',field:'check_effect_effect',width:300,rowspan:3,align:'center',sortable:true}
		    ]],
		    toolbar:"#tb",
		    pagination:true,
		    rownumbers:true, //是否有行号。
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
		  
		  function ceAdd() {
		  	toAddPage();
		  }
		  function ceEdit() {
		  	var rowselect = $("#info_table").datagrid("getSelected");
		            			if( rowselect == null ) {
		            				 $.messager.alert("提示", "没有选择任何条目！");
		            			}
		            			editCheckEffect(rowselect['check_effect_id'],rowselect['check_effect_name'],rowselect['check_effect_effect']);
		  }
		  function ceDell() {
		  	var rowselect = $("#info_table").datagrid("getSelected");
		            			if( rowselect == null ) {
		            				 $.messager.alert("提示", "没有选择任何条目！");
		            			}
		            			deleteColum( rowselect['check_effect_id'],rowselect['check_effect_name']);
		  }
	</script>
	<table align="center" id="info_table" >
	</table>
</body>
</html>
