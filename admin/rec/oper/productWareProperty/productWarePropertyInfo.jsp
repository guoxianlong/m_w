<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
	List checkEffectList = (ArrayList) request.getAttribute("checkEffectList");
	List productWareTypeList = (ArrayList) request.getAttribute("productWareTypeList");
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String productBarCode = StringUtil.convertNull(request.getParameter("productBarCode"));
		int checkEffectId = StringUtil.parstInt(request.getParameter("checkEffect"));
		if( checkEffectId == 0 ) {
			checkEffectId = -1;
		}
		//int standardCount = StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		if( wareType == 0 ) {
			wareType = -1;
		}
%>
<html>
  <head>
    
    <title>商品物流属性</title>
    
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
   		
		function check() {
			return true;
		}
		
		function checkSearchLoadPage() {
			var productCode = $("#productCode").val();
	   		$("#hproductCode").val(productCode);
	   		var productBarCode = $("#productBarCode").val();
	   		$("#hproductBarCode").val(productBarCode);
	   		var checkEffect = $("#checkEffect").val();
	   		$("#hcheckEffect").val(checkEffect);
	   		var wareType = $("#wareType").val();
	   		$("#hwareType").val(wareType);
	   		$("#info_table").datagrid('load',{  
	   			productCode:$("#hproductCode").val(),
		   		productBarCode:$("#hproductBarCode").val(),
		   		checkEffect:$("#hcheckEffect").val(),
		   		wareType:$("#hwareType").val()
	    	});
	   		return false;
	 	}
		function searchLoadPage() {
			var productCode = $("#productCode").val();
	   		$("#hproductCode").val(productCode);
	   		var productBarCode = $("#productBarCode").val();
	   		$("#hproductBarCode").val(productBarCode);
	   		var checkEffect = $("#checkEffect").val();
	   		$("#hcheckEffect").val(checkEffect);
	   		var wareType = $("#wareType").val();
	   		$("#hwareType").val(wareType);
	   		$("#info_table").datagrid('load',{  
	   			productCode:$("#hproductCode").val(),
		   		productBarCode:$("#hproductBarCode").val(),
		   		checkEffect:$("#hcheckEffect").val(),
		   		wareType:$("#hwareType").val()
	    	});
	 	}
		
		function deleteColum(id, productId){
			$.messager.confirm("问题", "你确认要删除该条目么？", function (r) {
				if( r ) {
					window.location="<%= request.getContextPath()%>/admin/deleteProductWareProperty.mmx?productWarePropertyId="+id + "&productId="+ productId;
					return;
				} else {
					return;
				}
			});
		}
		function toAddPage() {
			window.location="<%= request.getContextPath()%>/admin/toAddProductWareProperty.mmx";
		}
	</script>

  </head>
  <body calss="easyui-layout" fit="true">
  <div id="tb">
  <fieldset>
			<legend>筛选</legend>
   			<form action="" method="post" onsubmit="return checkSearchLoadPage();">
   					产品编号：<input type="text" size="11" name="productCode" id="productCode" onchange="autoFillWithProductCode();" value="<%= productCode%>"/>
   					<input type="hidden" id="hproductCode" value="<%= productCode%>"/>
   					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   					商品条码：<input type="text" size="11" name="productBarCode" id="productBarCode" onchange="autoFillWithProductBarCode();" value="<%= productBarCode%>" />
   					<input type="hidden" id="hproductBarCode" value="<%= productBarCode%>" />
   					<br/>
   					<b style="font-size:14px;">入库属性：</b>
   					质检分类：<select name="checkEffect" id="checkEffect" >
   							<%  if (checkEffectList == null ||checkEffectList.size() == 0 ) { %>
   								<option value="-1">没有质检分类可用</option>
   							<%
   								} else {
   								%>
   								<option value="-1" <%= checkEffectId == -1 ? "selected" : ""%>>请选择分类</option>
   								<%
   								int x = checkEffectList.size();
   								for( int i = 0; i < x ; i++ ) { 
   								CheckEffectBean cfb = (CheckEffectBean) checkEffectList.get(i);
   							%>
   								<option value="<%= cfb.getId()%>" <%= checkEffectId == cfb.getId() ? "selected" : ""%>><%= cfb.getName()%></option>
   							<%
   								}
   								}
   							%>
   							 </select>
   					<input type="hidden" id="hcheckEffect" value="-1" />
   					<br/>
   					<b style="font-size:14px;">出库属性：</b>
   					商品物流分类：<select name="wareType" id="wareType">
   									<%  if (productWareTypeList == null || productWareTypeList.size() == 0 ) { %>
   								<option value="-1">没有质检分类可用</option>
   							<%
   								} else {
   								%>
   								<option value="-1" <%= wareType == -1 ? "selected" : ""%>>请选择分类</option>
   								<%
   								int x = productWareTypeList.size();
   								for( int i = 0; i < x ; i++ ) { 
   								ProductWareTypeBean pwtBean = (ProductWareTypeBean) productWareTypeList.get(i);
   							%>
   								<option value="<%= pwtBean.getId()%>" <%= wareType == pwtBean.getId() ? "selected" : ""%>><%= pwtBean.getName()%></option>
   							<%
   								}
   								}
   							%>
   							 </select>
   					<input type="hidden" id="hwareType" value="-1" />
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   						<a href="javascript:searchLoadPage();" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
   			</form>
   			</fieldset>
   			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="pwpAdd();">添加商品物流属性</a>
	    	<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="pwpEdit();">编辑</a>    
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="pwpDell();">删除</a>  
	    	<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="pwpShow();">查看日志</a>   
   		</div>
   		<div id="menu" class="easyui-menu" style="width:120px;display: none;">
		<div onclick="pwpAdd();" iconCls="icon-add">添加商品物流属性</div>
		<div onclick="pwpEdit();" iconCls="icon-edit">编辑</div>
		<div onclick="pwpDell();" iconCls="icon-remove">删除</div>
		<div onclick="pwpShow();" iconCls="icon-print">查看日志</div>
	</div>
	<script type="text/javascript" >
		function editProductWareProperty(pwpId){
			window.location = "<%= request.getContextPath()%>/admin/preEditProductWareProperty.mmx?productWarePropertyId=" + pwpId; 
		}	
	
	
		$(function(){
			$('#info_table').datagrid({
			 onRowContextMenu : function(e, rowIndex, rowData) {
					e.preventDefault();
					$(this).datagrid('unselectAll');
					$(this).datagrid('selectRow', rowIndex);
					$('#menu').menu('show', {
						left : e.pageX,
						top : e.pageY
					});
				}
		 	});
		   $('#info_table').datagrid({
		    fitColumns : true,
		    fit:true,
			border : true,
		    pageNumber:1,  
		    pageSize:10,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'<%=request.getContextPath()%>/admin/getProductWarePropertyInfo.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    singleSelect:true,
		    columns:[[   
	                    {field:'product_code',title:'商品编号', rowspan:2,width:100,align:'center',sortable:true},   
	                    {field:'product_name',title:'原名称',rowspan:2,width:300,align:'center',sortable:true},   
	                    {title:'物流属性',colspan:2,width:200,align:'center'},
	                    {title:'仓储属性',colspan:2,width:240,align:'center'},
	                    {title:'出库属性',colspan:1,width:150,align:'center'},
	                    {field:'product_ware_type_name',title:'商品物流分类',rowspan:2,width:100,align:'center',sortable:true}
	                    ],
	                   [
	                    {field:'check_effect_name',title:'质检分类',width:100,align:'center',sortable:true},   
	                    {field:'cartonning_standard_count',title:'标准装箱量', width:100,align:'center',sortable:true},
	                    {field:'package_size',title:'包装尺寸',width:140,align:'center',sortable:true},
	                    {field:'weight',title:'重量',width:100,align:'center',sortable:true},
	                    {field:'identity_info',title:'可辨识信息',width:150,align:'center',sortable:true}			                    
	                ]],
	                toolbar:'#tb',
		    pagination:true,
		    rownumbers:true, //是否有行号。
		    onLoadSuccess: function(data) {
		    	var vproductCode = $("#hproductCode").val();
		   		var vproductBarCode = $("#hproductBarCode").val();
		   		var vcheckEffect = $("#hcheckEffect").val();
		   		var vwareType = $("#hwareType").val();
		   		$("#productCode").val(vproductCode);
		   		$("#productBarCode").val(vproductBarCode);
		   		$("#checkEffect").val(vcheckEffect);
		   		$("#wareType").val(vwareType);
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
		  function pwpAdd() {
		  	toAddPage();
		  }
		  function pwpDell() {
		  		var rowselect = $("#info_table").datagrid("getSelected");
	     		            			if( rowselect == null ) {
	     		            				 $.messager.alert("提示", "没有选择任何条目！");
	     		            			}
	     		            			deleteColum( rowselect['product_ware_property_id'], rowselect['product_ware_property_product_id'] );
		  
		  }
		function pwpEdit() {
			var rowselect = $("#info_table").datagrid("getSelected");
	     		            			if( rowselect == null ) {
	     		            				 $.messager.alert("提示", "没有选择任何条目！");
	     		            			}
	     		            			editProductWareProperty(rowselect['product_ware_property_id']);
		}
		function pwpShow() {
		var rowselect = $("#info_table").datagrid("getSelected");
	     		            			if( rowselect == null ) {
	     		            				 $.messager.alert("提示", "没有选择任何条目！");
	     		            			}
	     		            			showDialog("<%= request.getContextPath()%>/admin/toProductWarePropertyLogInfo.mmx?productWarePropertyId="
	     		       						+ rowselect['product_ware_property_id']
	     		       						+ "&productCode="
	     		       						+ rowselect['product_code']);
		}		
		function showDialog(url) {
			$('#productWarePropertyLogDiv').css("display","block");    // 修改div由不显示，改为显示。
			$('#productWarePropertyLogDiv').dialog({  
			    title: '查看操作日志 ',  // dialog的标题
			    width: 894,   	//宽度
			    height: 430,   //高度
			    closed: false,   // 关闭状态
			    cache: false,   //缓存,暂时不明白是要缓存什么东西但是与想象的有出入
			    href:url,
			    modal: true
			});  
		}
	</script>
	<table align="center" id="info_table" >
	</table>
	<div id="productWarePropertyLogDiv" style="display:none;">
	</div>
</body>
</html>
