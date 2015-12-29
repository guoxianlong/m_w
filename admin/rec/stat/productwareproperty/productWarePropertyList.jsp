<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
	<meta charset="UTF-8">
	<title>质检结果初录</title>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script>
	
	$(document).ready(function(){
		$('#propertyList').datagrid({ 
			                
			                width:1050,   
			                height:450,   
			                nowrap: true,   
			                autoRowHeight: false,   
			                striped: true,   
			                collapsible:true,
			                loadMsg:'正在查询,请稍候...',
			                url:'getPropertyListAll.mmx',  
			                columns:[[   
			                    {field:'product.code',title:'商品编号', rowspan:2,
			                    	formatter:function(v,o){
			                    		return o.product.code;
			                    	}
			                    },   
			                    {field:'product.name',title:'原名称',rowspan:2,
			                    	formatter:function(v,o){
			                    		return o.product.name;
			                    	}
			                    },   
			                    {title:'物流属性',colspan:2},
			                    {title:'仓储属性',colspan:2},
			                    {title:'出库属性',colspan:1},
			                    {field:'productWareType.name',title:'商品物流分类',rowspan:2,
			                    	formatter:function(v,o){
			                    		return o.productWareType.name;
			                    	}
			                    },
			                    {field:'operation',title:'操作',rowspan:2,
			                    	formatter:function(v,o){
			                    		return "<a href='javascript:void(0)' onclick='editProductWareProperty("+o.id+")'>修改</a>       操作记录";
			                    	}
			                    }
			                    ],
			                   [
			                    {field:'checkeEffect.name',title:'质检分类',
			                    	formatter:function(v,o){
			                    		return o.checkeEffect.name;
			                    	}
			                    },   
			                    {field:'cartonningStandardCount',title:'标准装箱量'},
			                    {field:'abc',title:'包装尺寸',
			                    	formatter:function(v,o){
			                    		return o.length + "cm x " + o.width + "cm x " + o.height + "cm";
			                    	}
			                    },
			                    {field:'weight',title:'重量'},
			                    {field:'identityInfo',title:'可辨识信息'}			                    
			                ]], 
			                pagination:true,   
			                rownumbers:true
			            });   
			            
			            var p = $('#propertyList').datagrid('getPager');   
			            $(p).pagination({   
			            	
			            	rows:10,
			            	showPageList:false,
			                //pageList: [5,10,15],//可以设置每页记录条数的列表   
			                beforePageText: '第',//页数文本框前显示的汉字   
			                afterPageText: '页    共 {pages} 页',   
			                displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',  
			            });
	});
	
	</script>
	<script type="text/javascript">
		function editProductWareProperty(obj){		
			window.open("<%=request.getContextPath()%>/admin/rec/stat/productwareproperty/editProductWareProperty.jsp?id="+obj,"请确认","dialogWidth:700px;status=no;dialogHeight:350px;help=no;location=no;resizable");
		}
	</script>
</head>
<body>

	<div id="p" class="easyui-panel" title="查询" style="width:1050px;height:200px;padding:10px;">
		<form id="ff" method="post">
	    		<table>
	    			<tr>
	    				<td>商品编号：</td><td></td><td><input type="text" name="productCode" id="productCode"/></td>
	    			</tr>
	    			<tr>
	    				<td>商品条码：</td><td></td><td><input type="text" name="productBarCode" id="productBarCode"/></td>
	    			</tr>
	    			<tr>
	    				<td>入库属性：</td><td>质检分类：</td><td><input type="text" name="productCode" id="productCode"/></td>
	    			</tr>
	    			<tr>
	    				<td>出库属性：</td><td>商品分类：</td><td><input type="text" name="productCode" id="productCode"/></td>
	    			</tr>
	    		</table>
			&nbsp;&nbsp;&nbsp;&nbsp;<a class="easyui-linkbutton" herf="javascript:void(0)" onclick="searchData()">查询</a>
			<br>
			
	    </form>
	</div>

<br/>
	<a class="easyui-linkbutton" herf="javascript:void(0)" onclick="searchData()">添加商品物流属性</a>
			<br>
			<br>
	<table id="propertyList"></table>
</body>
</html>