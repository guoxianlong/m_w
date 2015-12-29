<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/ckeditor/ckeditor.js"></script>
<script type="text/javascript" charset="UTF-8">
	var datagrid;
	$(function() {
		datagrid = $('#datagrid').datagrid(
						{	url : '${pageContext.request.contextPath}/fittingController/buyStockinList.mmx',
							toolbar : '#tb',
							idField : 'id',
							width : 700,
							height : 350,
							fit : true,
							fitColumns : true,
							striped : true,
							nowrap : false,
							loadMsg : '正在努力为您加载..',
							pagination : true,
							rownumbers : true,
							singleSelect : true,
							pageSize : 10,
							pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
							frozenColumns : [ [ {
								field : 'id',
								title : 'ID',
								width : 20,
								hidden : true
							}, ] ],
							columns : [ [ {
								field : 'code',
								title : '入库单编号',
								width : 20,
								align : 'center',
								sortable : true
							}, {
								field : 'typeName',
								title : '入库类型',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'fittingTypeName',
								title : '配件类别',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'area',
								title : '入库库区',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'count',
								title : '配件总量',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'status',
								title : '状态',
								width : 10,
								align : 'center',
								sortable : true
							}, {
								field : 'createDatetime',
								title : '生成时间',
								align : 'center',
								width : 10,
								sortable : true,
								formatter : function(value, rowData, rowIndex) {
									if (value != null) {
										return value.substring(0, 19);
									}
								}
							},{
								field : 'createUserName',
								title : '生成人',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
										return value;
									}
								}
							},{
								field : 'affirmUserId',
								title : '入库确认人',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
										return value;
									}
								}
								
							},{
								field : 'auditingUserId',
								title : '入库审核人',
								width : 10,
								align : 'center',
								sortable : true,
								formatter: function(value,rowData,rowIndex){
									if(value=='null'){
										return "";
									}else{
										return value;
									}
								}
								
							},{
								field:'operation',title:'操作',width:10,align:'center',
								
					        	formatter: function(value,rowData,rowIndex){
					        		if(rowData.status=='确认未通过' || rowData.status=='审核未通过'){	
					        			if(rowData.bianji==true){
					        				return '<a href="javascript:editFunFun(\''+rowData.code+'\');">编辑</a>';	
					        			}
					        		}
					        		if(rowData.status=='待确认'){	        								        			
					        			if(rowData.queren==true){
					        				return '<a href="javascript:ConfirmationFun(\''+rowData.code+'\');">入库确认</a>';	
					        			}
					        		}
					        		
					        		if(rowData.status=='待审核'){	        			
					        			if(rowData.shenhe==true){
					        				return '<a href="javascript:AuditFun(\''+rowData.code+'\');">入库审核</a>';	
					        			}
					        		}

					        		if(rowData.status=='已完成'){	        			
					        			if(rowData.chakan==true){
					        				return '<a href="javascript:seeFun(\''+rowData.code+'\');">查看</a>';	
					        			}
					        		}
					        		
								}
							} ] ]
						});
			$('#stockInType').combobox({
				url : '${pageContext.request.contextPath}/Combobox/getFittingBuyStockInType.mmx?flag=query',
			  	valueField:'id',
				textField:'text',
				delay:500
			});
	});
	
	function searchFun() {
		
		var chk_value="";//定义一个数组    
        $('input[name="status"]:checked').each(function(){//遍历每一个名字为interest的复选框，其中选中的执行函数    
        	chk_value+=$(this).val()+',';//将选中的值添加到数组chk_value中    
        }); 
		datagrid.datagrid('load', {			
			area : $('#tb input[name=area]').val(),
			code : $('#tb input[name=code]').val(),
			beginDatetime : $('#tb input[name=beginDatetime]').val(),
			endDatetime : $('#tb input[name=endDatetime]').val(),
			Productcode : $('#tb input[name=Productcode]').val(),
			status : chk_value,
			type : $("#stockInType").combobox("getValue")
		});
	}

	function AuditFun(value){		
		window.location.href = "${pageContext.request.contextPath}/admin/fitting/fittingstockinaudit.jsp?code="+value;
	}
	
	function ConfirmationFun(value){		
		window.location.href = "${pageContext.request.contextPath}/admin/fitting/fittingstockinconfirm.jsp?code="+value;
	}
	
	function seeFun(value){		
		window.location.href = "${pageContext.request.contextPath}/admin/fitting/fittingstockinsee.jsp?code="+value;
	}
	
	function editFunFun(value){		
		window.location.href = "${pageContext.request.contextPath}/fittingBuyStockinController/toEditFittingBuyStockin.mmx?code="+value;
	}

</script>

</head>
<body>
	<table id="datagrid"></table>

<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>配件入库单列表</legend>
			<table class="tableForm"  border="0">
				<tr align="center" >
					<th >入库地区</th>
					<td align="left">
						<input id="cc" class="easyui-combobox"
						 name="area" 
						 data-options="valueField:'id',textField:'text',url:'${pageContext.request.contextPath}//Combobox/getSignArea.mmx'" /></td>
					<th>入库类型</th>
					<td>
						<input id="stockInType" name="stockInType"/>
					</td>
					<th >入库单号</th>
					<td align="left">
						<input id="code" name="code"  /></td>
					<th >入库单生成时间</th>
					<td align="left">
						<input id="beginDatetime" name="beginDatetime" class="easyui-datebox" />&nbsp-
						<input id="endDatetime" name="endDatetime" class="easyui-datebox" /></td>
					<th >配件编号</th>
					<td align="left">
						<input id="Productcode" name="Productcode" /></td>					
				</tr>
				<tr align="center"> 
					<th >入库单状态</th>
					<td align="left" colspan="6">
						<input type="checkbox" name="status" value="0" />待确认 &nbsp
						<input type="checkbox" name="status" value="1" />确认未通过 &nbsp
						<input type="checkbox" name="status" value="3" />待审核 &nbsp
						<input type="checkbox" name="status" value="5" />审核未通过 &nbsp
						<input type="checkbox" name="status" value="4" />已完成 &nbsp</td>
					<td align="right" >
					<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>