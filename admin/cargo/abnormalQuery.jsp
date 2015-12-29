<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/easyui/jquery-easyui-1.3.4/detailview/datagrid-detailview.js"></script> 
	<script type="text/javascript" charset="UTF-8">
	var datagrid;
		$(function(){
			datagrid = $('#datagrid').datagrid({
				view: detailview,    
				width : 1100,
				height : 350,
			    detailFormatter:function(index,row){    
			        return '<div style="padding:2px"><table id="ddv-' + index + '"></table></div>';    
			    },
			    onExpandRow: function(index,row){ 
			        $('#ddv-'+index).datagrid({    
			            url:'${pageContext.request.contextPath}/abnormalQuery/getProductList.mmx?code='+row.code,    
			            fitColumns:true,    
			            singleSelect:true,    
			            rownumbers:true,    
			            loadMsg:'',    
			            height:'auto', 
			            columns:[[     
			                {field:'code',title:'商品编号',width:100},    
			                {field:'name',title:'商品名称',width:100},
			                {field:'buyCount',title:'数量',width:100}
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
			        $('#dg').datagrid('fixDetailRowHeight',index);    
			    },
				url : '${pageContext.request.contextPath}/abnormalQuery/getAbnormalQueryList.mmx',
				toolbar : '#tb',
			    idField : 'id',
			    fit : false,
			    fitColumns : false,
			    striped : true,
			    nowrap : true,
			    loadMsg : '正在努力为您加载..',
			    pagination : true,
			    rownumbers : true,
			    singleSelect : true,
			    pageSize : 1,
			    pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
				frozenColumns : [ [ {
					field : 'id',
					title : 'ID',
					width : 30,
					hidden : true
				}, ] ],
				columns : [ [ {
					field : 'code',
					title : '订单编号',
					width : 110,
					align : 'center',
					sortable : true
				}, {
					field : 'createDatetime',
					title : '生成日期',
					width : 150,
					align : 'center',
					sortable : true,
					formatter : function(value, rowData, rowIndex) {
						if (value != null) {
							return value.substring(0, 19);
						}
					}
				}, {
					field : 'status',
					title : '订单状态',
					width : 80,
					align : 'center',
					sortable : true,
					formatter : function(value,rowData,rowIndex){
						if(value=='3'){
							return '已到款';
						}
						if(value=='6'){
							return '已发货';
						}
						if(value=='9'){
							return '待查款';
						}
						if(value=='10'){
							return '重复';
						}
						if(value=='11'){
							return '已退回';
						}
						if(value=='12'){
							return '已结算';
						}
						if(value=='13'){
							return '待退回';
						}
						
					}
				}, {
					field : 'phone',
					title : '电话',
					width :150,
					align : 'center',
					sortable : true
				}, {
					field : 'name',
					title : '姓名',
					width : 150,
					align : 'center',
					sortable : true,
					
				},{
					field : 'address',
					title : '最近收货地址',
					width : 400,
					align : 'center',
					sortable : true,
					
				} ] ]
			 })
		});
		
		function searchFun() {
			if($('#tb input[name=code]').val()==""){
				$.messager.show({
					msg : '请输入查询订单号',
					title : '提示'
				});
				return;
			}
			datagrid.datagrid('load', {
				code : $('#tb input[name=code]').val(),
			});
		}
	</script>
</head>
<body>
	<table id="datagrid" ></table>  		
		<div id="tb"  style="height: auto;display: none;">
			<table class="tableForm" >
				<tr align="center" >
					<th >订单编号</th>
					<td align="left">
						<input id="code" name="code" /></td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
				</tr>
			</table> 
		</div>
</form>
</body>
</html>