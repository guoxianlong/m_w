<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/easyui/jquery-easyui-1.3.4/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/easyui/jquery-easyui-1.3.4/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/easyui/jquery-easyui-1.3.4/demo/demo.css">
 <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.4.4.min.js"></script>
 <script type="text/javascript" src="${pageContext.request.contextPath}/easyui/jquery-easyui-1.3.4/detailview/jquery.easyui.min.js"></script>
 <script type="text/javascript" src="${pageContext.request.contextPath}/easyui/jquery-easyui-1.3.4/detailview/datagrid-detailview.js"></script>
 <script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
 	<style type="text/css">
		.products{
			padding:0px;
			height:40%;
			width:60%;
			float:left;
			border:0px solid #333;
		}
		.products li{
			display:inline;
			float:left;
			margin:3px;
			border: 0px solid;
		}
		.item{
			display:block;
			text-decoration:none;
			text-align:center;
		}
		.item img{
			border:1px solid #333;
		}
		.item p{
			margin:0;
			font-weight:bold;
			text-align:center;
			color:#c3c3c3;
		}
		.cart{
			width:300px;
			background:#ccc;
			padding:0px 10px;
			float:right;
			height:100%;
		}
		h1{
			text-align:center;
			color:#555;
		}
		.total{
			margin:0;
			text-align:right;
			padding-right:20px;
		}
	</style>
	<script type="text/javascript" charset="UTF-8">
	var datagrid;
	var applyFrom;
	var editFrom;
	var data = {"rows":[]};
	var totalCost = 0;
		$(function(){
			getTableLength();
			applyFrom = $('#applyFrom').form();
			editFrom = $('#editFrom').form();
			datagrid = $('#datagrid').datagrid({
				view: detailview,    
				width : 1100,
				height : 350,
			    detailFormatter:function(index,row){    
			        return '<div style="padding:2px"><table id="ddv-' + index + '"></table></div>';    
			    },
			    onExpandRow: function(index,row){ 
			        $('#ddv-'+index).datagrid({    
			            url:'${pageContext.request.contextPath}/areaController/getStockAreaSubTempList.mmx?id='+row.id,    
			            fitColumns:true,    
			            singleSelect:true,    
			            rownumbers:true,    
			            loadMsg:'',    
			            height:'auto', 
			            columns:
			            	[[{
			            		field:'name',
			            		title:'库类型',
			            		width:100
			            	},{
		            			field:'status',
		            			title:'使用情况',
		            			width:100,
		            			formatter : function(value, rowData, rowIndex) {
		    						if(value==1){
		    							return '使用中';
		    						}else{
		    							return '未使用';
		    						}
		    					}
			            	}
			            ]],    
			            onResize:function(){    
			                $('#dg').datagrid('fixDetailRowHeight',index);    
			            },    
			            onLoadSuccess:function(){    
			                setTimeout(function(){    
			                    $('#dg').datagrid('fixDetailRowHeight',index);    
			                },0);    
			            }    
			        });    
			        $('#datagrid').datagrid('fixDetailRowHeight',index);    
			    },
				url : '${pageContext.request.contextPath}/areaController/getStockArea.mmx',
				toolbar : '#tb',
			    idField : 'id',
			    fit : true,
			    fitColumns : true,
			    striped : true,
			    nowrap : true,
			    loadMsg : '正在努力为您加载..',
			    pagination : true,
			    rownumbers : true,
			    singleSelect : true,
			    pageSize : 20,
			    pageList : [ 10,20 ],
				frozenColumns : [ [ {
					field : 'id',
					title : 'ID',
					width : 30,
					hidden : true
				}, ] ],
				columns : [ [ {
					field : 'id',
					title : '库ID',
					width : 110,
					align : 'center',
					sortable : true
				},{
					field : 'name',
					title : '名称',
					width : 110,
					align : 'center',
					sortable : true
				}, {
					field : 'type',
					title : '是否发货仓',
					width : 150,
					align : 'center',
					sortable : true,
					formatter : function(value, rowData, rowIndex) {
						if(value==false){
							return '否';
						}else{
							return '是';
						}
					}
				}, {
					field : 'attribute',
					title : '是否我司仓',
					width :150,
					align : 'center',
					sortable : true,
					formatter : function(value, rowData, rowIndex) {
						if(value==false){
							return '是';
						}else{
							return '否';
						}
					}
				}, {
					field : 'areaType',
					title : '库类型',
					width :150,
					align : 'center',
					sortable : true,
					formatter : function(value, rowData, rowIndex) {
						return '<a href="javascript:void(0);" class="editbutton" onclick="editFun2(\''+rowData.id+'\')">修改库类型</a>'
					}
				}, {
					field : 'attributeModifiers',
					title : '属性修改',
					width :150,
					align : 'center',
					sortable : true,
					formatter : function(value, rowData, rowIndex) {
						return '<a href="javascript:void(0);" class="editbutton" onclick="editFun(\''+rowData.id+'\',\'' +rowData.name+ '\',\'' +rowData.type+'\',\'' +rowData.attribute+'\')">修改地区属性</a>'
					}
				} ] ],
			    onLoadSuccess : function(data){
			    	$('.editbutton').linkbutton({    
			    		iconCls:'icon-edit',
						plain:true
			    	});          
			    	$('.delbutton').linkbutton({  
			    		iconCls:'icon-cancel',
						plain:true
			    	}); 
			    }
			 });
			
			$('#cartcontent').datagrid({
				singleSelect:true,
				onDblClickRow:function(rowIndex,rowData){
					
					$('#cartcontent').datagrid('deleteRow',rowIndex);
				}
			});
			
			$('.item').draggable({
				revert:true,
				proxy:'clone',
				onStartDrag:function(){
					$(this).draggable('options').cursor = 'not-allowed';
					$(this).draggable('proxy').css('z-index',10);
				},
				onStopDrag:function(){
					$(this).draggable('options').cursor='move';
				}
			});
			$('.cart').droppable({
				onDragEnter:function(e,source){
					$(source).draggable('options').cursor='auto';
				},
				onDragLeave:function(e,source){
					$(source).draggable('options').cursor='not-allowed';
				},
				onDrop:function(e,source){
					var name = $(source).find('p:eq(0)').html();
					var id = $(source).find('p:eq(1)').html();
					addProduct(name,id);
				}
			});
		});
		
		function getTableLength(){
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/areaController/getStockType.mmx",
	            async:false,
				success : function(j) {
					var rows = jQuery.parseJSON(j);
					var ul = $("#dd2").find('form').find('ul');
					var li="";
					for(var i = 0 ; i < rows.obj.length ; i++){
						li+="<li>";
						li+="<a href='#' class='item'>";
						li+="<img src='../images/area/yangpinku.gif'/>";
						li+="<div>";
						li+="<p>"+rows.obj[i].name+"</p>";	
						li+="<p style='display: none;'>"+rows.obj[i].id+"</p>";	
						li+="</div>";						
						li+="</a>";					
						li+="</li>";								
					}
					console.info(rows);
					ul.html(li);
				}
			});
		}
		
		function addProduct(name,id){
			function add(){
				for(var i=0; i<data.rows.length; i++){
					var row = data.rows[i];
					if (row.name == name){
						return;
					}
				}
				data.rows.push({
					name:name,
					id:id
				});
			}
			add();
			$('#cartcontent').datagrid('loadData', data);
		}
		
		function appendFun() {
			applyFrom.form('load', {
				id : '',
				name : '',
				type : '1',
				attribute : '0'
			}),
			$('#dd').show().dialog(
					{
						modal : true,
						minimizable : true,
						title : '新增库地区',
						width : 365,
						height : 295,
						modal: true,
						closed: false,    
					    cache: false,
						buttons : [ {
							text : '确定',
							handler : function() {
								applyFrom.form('submit',
									{
									url : '${pageContext.request.contextPath}/areaController/addOrEditStockArea.mmx',
									success : function(data) {
									var d = $.parseJSON(data);
										if(d){
												$('#dd').dialog('close');
												$.messager.show({
													msg : d.msg,
													title : '提示'
												});
												datagrid.datagrid('reload');
											}	
										}
									});
							}
						}, {
							text : '取消',
							handler : function() {
								$('#dd').dialog('close');
							}
						} ]
					});
			$("#id").attr("disabled", false);
			}
		
		function editFun(id,name,type,attribute) {
			$('#dd').show().dialog(
				{
					modal : true,
					minimizable : true,
					title : '修改地区属性',
					width : 365,
					height : 295,
					modal: true,
					closed: false,    
				    cache: false,
					buttons : [ {
						text : '确定',
						handler : function() {
							applyFrom.form('submit',
									{
									url : '${pageContext.request.contextPath}/areaController/addOrEditStockArea.mmx?method=edit&id='+id,
									success : function(data) {
									var d = $.parseJSON(data);
										if(d){
												$('#dd').dialog('close');
												$.messager.show({
													msg : d.msg,
													title : '提示'
												});
												datagrid.datagrid('reload');
											}	
										}
									});
						}
					},{
						text : '取消',
						handler : function() {
							$('#dd').dialog('close');
						}
					} ]
				}),applyFrom.form('load', {
					id : id,
					name : name,
					type : type,
					attribute : attribute
				}),
				$("#id").attr("disabled", true);
				;
		}
		
		function editFun2(id){
			$.ajax({
				type : "POST",
				cache : false,
				url : "${pageContext.request.contextPath}/areaController/getStockTypeNameList.mmx?id="+id,
				dataType : 'json',
				success : function(j) {
					$.each(j.obj,function(index,value){
						if(j.obj[index]!=null){
							data.rows.push({
								name:value.name,
								id:value.id
							});
						}
					});
					$('#cartcontent').datagrid('loadData', data);
				}
			});
			$('#dd2').show().dialog(
					{
						modal : true,
						minimizable : false,
						title : '修改库类型',
						width : 1000,
						height : 650,
						modal: true,
						closed: false, 
						closable:false,
					    cache: false,
						buttons : [ {
							text : '确定',
							handler : function() {
								var rows =$('#cartcontent').datagrid('getRows'); 
								var typeName=[];
								$.each(rows,function(index,value){
									typeName.push(value.id);
								});
								$.ajax({
									type : "POST",
									cache : false,
									url : '${pageContext.request.contextPath}/areaController/editStockAreaType.mmx?id='+id,
									traditional:true,//如果不加这个属性，将导致后台接收不到数组
									data:{'typeName':typeName},
									dataType : 'json',
									success : function(obj) {
										if(obj.success){
												$('#dd2').dialog('close');
												$.messager.show({
													msg : obj.msg,
													title : '提示'
												});
												data = {"rows":[]};
												$('#cartcontent').datagrid('loadData', data);
										}
									}
								});
							}
						},{
							text : '取消',
							handler : function() {
								data = {"rows":[]};
								$('#cartcontent').datagrid('loadData', data);
								$('#dd2').dialog('close');
							}
						} ]
					})
			}
		
		 /* 添加选择的项 */   
	    function Add(ObjSource, ObjTarget) {   
	       if(ObjSource.val() ==null) return;// 如果没有选择则退出函数，无这句话的话IE6会报错   
	            ObjTarget.append(ObjSource.find("option:selected"));   
	           
	        ObjSource.find("option:selected").remove();  // 原列表中选中的值删除   
	    }   
	    /* 添加全部 */   
	    function AddAll(ObjSource, ObjTarget) {   
	        ObjTarget.append(ObjSource.html());  // 目标列表的HTML加上原列表的所有HTML   
	        ObjSource.empty();  // 原列表清空   
	    }   
		
	</script>
</head>
<body>
<table id="datagrid" ></table>  
	<div id="tb"  style="height: auto;display: none;">
		<a class="easyui-linkbutton" iconCls="icon-add" onclick="appendFun();" plain="true" href="javascript:void(0);">新增库地区</a>
	</div>
	
	<div id="dd" style="display: none">
		<form id="applyFrom" method="post">
			<table class="tableForm" border="0" height="220" width="330">
				<tr>
				  <th width="117" bgcolor="#CCCCCC"><label> ID:</label></th>
				  <td width="197" bgcolor="#CCCCCC"><input name="id" type="text" class="easyui-validatebox" id="id" style="width: 60px;" size="10" maxlength="10"  required="required" /></td>
				</tr>
				<tr>
				  <th width="117" bgcolor="#CCCCCC"><label>名称:</label></th>
					<td bgcolor="#CCCCCC"><input name="name" type="text" class="easyui-validatebox" id="name" style="width: 60px;" size="15" maxlength="10"  required="required" /></td>
				</tr>
				<tr>
				  <th width="117" bgcolor="#CCCCCC"><label>是否发货仓:</label></th>
					<td bgcolor="#CCCCCC"><input name="type" value="1" type="radio"  checked="checked" class="easyui-validatebox" required="required"/>是
						<input name="type" value="0" type="radio"  class="easyui-validatebox" required="required"/>否				  </td>
				</tr>
				<tr>
				  <th width="117" bgcolor="#CCCCCC"><label>是否是我司仓:</label></th>
					<td bgcolor="#CCCCCC"><input name="attribute" value="0" type="radio" checked="checked" class="easyui-validatebox" required="required"/>是
						<input name="attribute" value="1" type="radio" class="easyui-validatebox" required="required"/>否				  </td>
				</tr>
  </table>
		</form>
	</div>  

	<div id='dd2' style="display: none; ">
		<form id="editFrom" method="post">
			<ul class="products">
			</ul>
	</form>
	<div class="cart">
		<h1>已有库类型</h1>
		<div style="background:#fff">
		<table id="cartcontent">
			<thead>
				<tr>
					<th field="id" hidden="true" width=20>代码</th>
					<th field="name" resizable="false" width=280>库名称</th>
				</tr>
			</thead>
		</table>
		</div>
	</div>
	</div>
</body>
</html>