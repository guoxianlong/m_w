<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<title>待解封商品列表</title>
</head>
<script type="text/javascript">
	var datagrid;
	var dailog;
	$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		dialog = $("#dailogDiv").show().dialog({
			title:'提示',
			modal: true
		}).dialog('close');
		
		datagrid = $("#afterSaleSealProductList").datagrid({
			toolbar : '#tb',
			idField:'after_sale_seal_product_id',
			fitColumns:true,
			fit : true,
			pageSize:20,
			pageList:[10,20,30,40],
			url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleSealProductList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:false,
			pagination:true,
			frozenColumns:[[
					{field:'after_sale_seal_product_id',title:'待解封列表id',checkbox:true}
			]],
			columns:[[
			        {field:'product_name',title:'商品名称',width:200,align:'center'},
			        {field:'after_sale_order_code',title:'售后单号',width:100,align:'center'},
			        {field:'after_sale_detect_product_code',title:'售后处理单号',width:100,align:'center'},
			        {field:'after_sale_seal_code',title:'封箱编号',width:100,align:'center'},
			        {field:'seal_date',title:'封箱日期',width:100,align:'center'},
			        {field:'seal_control',title:'操作',width:100,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);"  class="reopenedProduct" onclick="reopenedProduct('+index+')"></a>';
			        	}
			        }
			 ]],
			 onLoadSuccess : function(data) {
				//改变datagrid中按钮的class
				$(".reopenedProduct").linkbutton(
					{ 
						text:'解封'
					}
				);
			}
		});
		$('#areaId').combobox({
	      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
	      	valueField:'id',
			textField:'text',
			editable:false
	    });
		$("#query").click(function(){
			var startDate = $("#startDate").datebox('getValue');
			var endDate = $("#endDate").datebox('getValue');
			if(startDate!='' && endDate!=''){
				if(!validateDate(endDate,startDate)){
					alert('开始日期必须小于结束日期');
					$('#startDate').focus();
					return false;
				}
			}
			var afterSaleOrderCode = $("#afterSaleOrderCode").val();
			var afterSaleDetectCode = $("#afterSaleDetectCode").val();
			var afterSaleSealCode = $("#afterSaleSealCode").val();
			var areaId = $('#areaId').combobox('getValue');
			datagrid = $("#afterSaleSealProductList").datagrid({
				url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleSealProductList.mmx',
				queryParams:{
						startDate:startDate,
						endDate:endDate,
						afterSaleOrderCode:afterSaleOrderCode,
						afterSaleSealCode:afterSaleSealCode,
						areaId : areaId,
				},
			});
		});
		
	});
	
	function reopenedProduct(index){
		if (index != undefined) {
				$('#afterSaleSealProductList').datagrid('selectRow', index);
			}
			var row = $('#afterSaleSealProductList').datagrid('getSelected');
		$.ajax({
			url:'${pageContext.request.contextPath}/admin/AfStock/batchReopened.mmx',
			data:{sealProductIds:row.after_sale_seal_product_id},
			cache:false,
			type:"post",
			dataType:'text',
			success:function(result){
				var re = eval('('+result+')');
				if(re['result']=="success"){
					$("#dailogDiv").show().dialog('open');
					datagrid = $("#afterSaleSealProductList").datagrid({
						url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleSealProductList.mmx'
					});
					var sealIds = re['sealIds'];
					$("#sealIds").val(re['sealIds']);
				}else{
					$.messager.show({
						title:'提示',
						msg:re['tip'],
						timeout:3000,
						showType:'slide'
					});
				}
			}
		});
	}
	
	function batchReopened(){
			var items = datagrid.datagrid('getSelections');
			if(items.length!=0){
				var sealProListIds ="";
				$.each(items,function(index,item){
					sealProListIds +=item.after_sale_seal_product_id+",";
				});
				$.ajax({
					url:'${pageContext.request.contextPath}/admin/AfStock/batchReopened.mmx',
					data:{sealProductIds:sealProListIds.substring(0,sealProListIds.length-1)},
					cache:false,
					type:"post",
					dataType:'text',
					success:function(result){
						var re = eval('('+result+')');
						if(re['result']=="success"){
							$("#dailogDiv").show().dialog('open');
							datagrid = $("#afterSaleSealProductList").datagrid({
								url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleSealProductList.mmx'
							});
							var sealIds = re['sealIds'];
							$("#sealIds").val(re['sealIds']);
						}else{
							$.messager.show({
								title:'提示',
								msg:re['tip'],
								timeout:3000,
								showType:'slide'
							});
						}
					}
				});	
			}else{
				$.messager.show({
					title:'提示',
					msg:'请至少选择一个待解封商品！',
					timeout:3000,
					showType:'slide'
				});
			}
		}
		
		function printSealInventory(){
			window.open("${pageContext.request.contextPath}/admin/AfStock/printSealInventory.mmx?sealIds=" + $("#sealIds").val());
		}
	function excel(){
		var startDate = $('#startDate').datebox('getValue');
		var endDate = $('#endDate').datebox('getValue');
		var afterSaleOrderCode =  $('#tb input[name=afterSaleOrderCode]').val();
		var afterSaleDetectCode =  $('#tb input[name=afterSaleDetectCode]').val();
		var afterSaleSealCode = $('#tb input[name=afterSaleSealCode]').val();
		var areaId = $('#areaId').combobox('getValue');
		location.href = "${pageContext.request.contextPath}/admin/AfStock/excelSealProduct.mmx?startDate=" 
			+ startDate + "&endDate=" + endDate + "&afterSaleOrderCode=" + afterSaleOrderCode + "&afterSaleDetectCode=" + afterSaleDetectCode 
			+ "&afterSaleSealCode=" + afterSaleSealCode + "&areaId=" + areaId;
	}
</script>
<body>
		<div id="tb" style="height: auto;">
			<fieldset>
			<legend>待解封商品列表</legend>
				<table>
					<tr>
						<td><span style="font-size: 12px;">售后单号：</span>
						<input type="text" class="easyui-validatebox" name="afterSaleOrderCode" id="afterSaleOrderCode"></td>
						<td><span style="font-size: 12px;">处理单号：</span>
						<input type="text" class="easyui-validatebox" name="afterSaleDetectCode" id="afterSaleDetectCode"></td>
					</tr>
					<tr>
						<td><span style="font-size: 12px;">封箱编号：</span>
						<input type="text" class="easyui-validatebox" name="afterSaleSealCode" id="afterSaleSealCode"></td>
						<td><span style="font-size: 12px;">售后地区：</span>
						<input type="text" class="easyui-validatebox" name="areaId" id="areaId" style="width: 155px"></td>
						<td><span style="font-size: 12px;">封箱日期：</span>
						<input name="startDate"  class="easyui-datebox" editable="false" id="startDate">--</td>
						<td><input name="endDate"  class="easyui-datebox" editable="false" id="endDate"></td>
						<td>
							<a id="query" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a>
							<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
						</td>
					</tr>
				</table>
			</fieldset>
			<a class="easyui-linkbutton" iconCls="icon-remove" href="javascript:void(0);" onclick="batchReopened();">批量解封</a>
		</div>
		<table id="afterSaleSealProductList"></table>
		<br/>
		<div id="dailogDiv"  style="width:600;height:200px;padding:10px;display: none;" align="center">
			<span style="font-size: 14px;padding:20px;">已将商品从封箱清单移出</span>&nbsp;
			<a id="print" class="easyui-linkbutton" href="javascript:void(0);" onclick="printSealInventory();">重新打印封箱清单</a>
			<input type="hidden" id="sealIds"/> 
		</div>
</body>
</html>
