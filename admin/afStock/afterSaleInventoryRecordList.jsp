<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<title>盘点记录</title>
</head>
<body>
	<div style="padding:3px;height: auto;">
		<fieldset>
			<table>
				<tr>
					<td><span style="font-size: 12px;">盘点日期：</span><input name="startDate"  class="easyui-datebox" editable="false" id="startDate">--</td>
					<td><input name="endDate"  class="easyui-datebox" editable="false" id="endDate"></td>
					<td><span style="font-size: 12px;">盘点编号：</span><input type="text" class="easyui-validatebox" name="inventoryCode" id="inventoryCode"></td>
					<td><span style="font-size: 12px;">售后地区：</span><input id="areaId" name="areaId" style="width: 121px" /></td>
					<td><a id="query" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a></td>
				</tr>
			</table>
		</fieldset>
		<table id="afterSaleInventoryRecordList"></table>
	<div>
</body>
</html>
<script type="text/javascript">
	var datagrid;
	$(function(){
		datagrid = $("#afterSaleInventoryRecordList").datagrid({
			title:"盘点记录",
			idField : 'id',
			iconCls:'icon-ok',
			fitColumns:true,
			width:1200,
			height:500,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleInventoryRecordList.mmx',
			showFooter:true,
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns:[[
					{field:'endDate',title:'盘点时间',width:60,align:'center'},
			        {field:'afterSaleInventroyCode',title:'盘点编号',width:60,align:'center'},
			        {field:'inventoryCount',title:'盘点次数',width:60,align:'center'},
			        {field:'stockAreaStr',title:'库地区',width:60,align:'center'},
			        {field:'stockTypeStr',title:'库类型',width:60,align:'center'},
			        {field:'stockCount',title:'库存商品总量',width:80,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);" onclick="stockDetail('+index+')">'+ row.stockCount +'</a>';
			        	}
			        },
			        {field:'inventoryProductCount',title:'已盘点商品数量',width:80,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);" onclick="inventoryProductDetail('+index+')">'+ row.inventoryProductCount +'</a>';
			        	}
			        },
			        {field:'backSupplierCount',title:'返厂商品数量',width:80,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);" onclick="backSuppilerProductDetail('+index+')">'+ row.backSupplierCount +'</a>';
			        	}
			        },
			        {field:'bsCount',title:'有库存记录缺少实物数量',width:120,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);" onclick="bsProductDetail('+index+')">'+ row.bsCount +'</a>';
			        	}
			        },
			        {field:'byCount',title:'报损过的商品数量',width:120,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);" onclick="byProductDetail('+index+')">'+ row.byCount +'</a>';
			        	}
			        },
			        {field:'unSendOutCount',title:'漏发商品数量',width:80,align:'center'},
			        {field:'differentWholeCodeCount',title:'记录和实际货位不一致商品数量',width:120,align:'center',
			        	formatter : function(value,row,index){
			        		return '<a href="javascript:void(0);" onclick="differentWholeCodeDetail('+index+')">'+ row.differentWholeCodeCount +'</a>';
			        	}
			        },
			]]
		});
		
		$('#areaId').combobox({
	      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
	      	valueField:'id',
			textField:'text',
			editable:false
	    });
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
		var inventoryCode = $("#inventoryCode").val();
		var areaId = $('#areaId').combobox('getValue');
		datagrid = $("#afterSaleInventoryRecordList").datagrid({
			queryParams:{startDate:startDate,
								endDate:endDate,
								inventoryCode:inventoryCode,
								areaId : areaId},
			url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleInventoryRecordList.mmx'
		});
	});
	
	function stockDetail(index){
		if (index != undefined) {
			$('#afterSaleInventoryRecordList').datagrid('selectRow', index);
		}
		var row = $('#afterSaleInventoryRecordList').datagrid('getSelected');
		if(row.stockType=="10"){
			window.open('${pageContext.request.contextPath}/admin/afStock/afterSaleCustomerProduct.jsp');
		}else if(row.stockType=="9"){
			window.open('${pageContext.request.contextPath}/admin/afStock/afterSaleStockProduct.jsp');
		}
	}
	
	function backSuppilerProductDetail(index){
		if (index != undefined) {
			$('#afterSaleInventoryRecordList').datagrid('selectRow', index);
		}
		var row = $('#afterSaleInventoryRecordList').datagrid('getSelected');
		window.open('${pageContext.request.contextPath}/admin/afStock/afterSaleBackSupplierProduct.jsp');
	}
	
	function inventoryProductDetail(index){
		if (index != undefined) {
			$('#afterSaleInventoryRecordList').datagrid('selectRow', index);
		}
		var row = $('#afterSaleInventoryRecordList').datagrid('getSelected');
		$('<div/>').dialog({
			href : '${pageContext.request.contextPath}/admin/afStock/afterSaleInventoryProductDetail.jsp',
			width : 900,
			height : 520,
			modal : true,
			title : '已盘点商品',
			onClose : function() {
				$(this).dialog('destroy');
			},
			onLoad : function(){
				$("#toolbar input[id=inventoryRecordId]").val(row.id);
				$("#toolbar input[id=type]").val('1,3,4,5');
				initinventoryProducts(row.id);
			}
		});
	}
	
	//报溢商品
	function byProductDetail(index){
		if (index != undefined) {
			$('#afterSaleInventoryRecordList').datagrid('selectRow', index);
		}
		
		var row = $('#afterSaleInventoryRecordList').datagrid('getSelected');
		$('<div/>').dialog({
			href : '${pageContext.request.contextPath}/admin/afStock/afterSaleInventoryByProductDetail.jsp',
			width : 900,
			height : 520,
			modal : true,
			title : '报损过的商品数量  ',
			onClose : function() {
				$(this).dialog('destroy');
			},
			onLoad : function(){
				$("#conditionTable input[id=inventoryRecordId]").val(row.id);
				$("#conditionTable input[id=type]").val('3');
				initchkInventoryProducts(row.id);
			}
		});
	}
	
	function bsProductDetail(index){
		if (index != undefined) {
			$('#afterSaleInventoryRecordList').datagrid('selectRow', index);
		}
		var row = $('#afterSaleInventoryRecordList').datagrid('getSelected');
		$('<div/>').dialog({
			href : '${pageContext.request.contextPath}/admin/afStock/afterSaleInventoryBsProductDetail.jsp',
			width : 900,
			height : 520,
			modal : true,
			title : '有库存记录没有实物商品',
			onClose : function() {
				$(this).dialog('destroy');
			},
			onLoad : function(){
				$("#toolbar input[id=inventoryRecordId]").val(row.id);
				$("#toolbar input[id=type]").val('2');
				initchkInventoryProducts(row.id);
			}
		});
	}
	
	function initinventoryProducts(recordId){
		$("#afterSaleInventoryProductList").datagrid({
			url:'${pageContext.request.contextPath}/admin/AfStock/queryInventoryProductList.mmx?type='+$("#toolbar input[id=type]").val()+'&inventoryRecordId=' + recordId,
			toolbar : '#toolbar',
			idField : 'afterSaleInventoryProductId',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : true,
		    loadMsg : '正在努力为您加载..',
		    pagination : true,
		    rownumbers : true,
		    singleSelect : true,
		    pageSize:20,
			pageList:[5,10,15,20],
			columns:[[
			    	{field:'afterSaleDetectCode',title:'售后处理单号',width:80,align:'center'},
			    	{field:'afterSaleDetectProductStatus',title:'售后处理单状态',width:80,align:'center'},
			    	{field:'afterSaleOrderCode',title:'售后单号',width:100,align:'center'},
			    	{field:'productCode',title:'商品编号',width:100,align:'center'},
			    	{field:'productName',title:'商品名称',width:80,align:'center'},
			    	{field:'recordWholeCode',title:'货位号',width:100,align:'center'}
			    ]]
			});
		}
	
	function initchkInventoryProducts(recordId){
		$("#afterSaleInventoryProductList").datagrid({
			url:'${pageContext.request.contextPath}/admin/AfStock/queryInventoryProductList.mmx?type='+$("#toolbar input[id=type]").val()+'&inventoryRecordId=' + recordId,
			toolbar : '#toolbar',
			idField : 'afterSaleInventoryProductId',
		    fit : true,
		    fitColumns : true,
		    striped : true,
		    nowrap : true,
		    loadMsg : '正在努力为您加载..',
		    pagination : true,
		    rownumbers : true,
		    checkOnSelect : false,
			selectOnCheck : false,
		    pageSize:20,
			pageList:[5,10,15,20],
			columns:[[
			        {field:'checkbox',checkbox:true,width:40,align:'center',value:"id"},
			    	{field:'afterSaleDetectCode',title:'售后处理单号',width:80,align:'center'},
			    	{field:'afterSaleDetectProductStatus',title:'售后处理单状态',width:80,align:'center'},
			    	{field:'afterSaleOrderCode',title:'售后单号',width:100,align:'center'},
			    	{field:'productCode',title:'商品编号',width:100,align:'center'},
			    	{field:'productName',title:'商品名称',width:80,align:'center'},
			    	{field:'recordWholeCode',title:'货位号',width:100,align:'center'}
			    ]]
			});
		}
		
		function queryInventoryProductList(){
			var productCode = $("#toolbar input[id=productCode]").val();
			var afterSaleDetectCode = $("#toolbar input[id=afterSaleDetectCode]").val();
			var type = $("#toolbar input[id=type]").val();
			var inventoryRecordId = $("#toolbar input[id=inventoryRecordId]").val();
			if ($("#toolbar input[id=hiddenproductCode]")) {
				$("#toolbar input[id=hiddenproductCode]").val(productCode);
				$("#toolbar input[id=hiddenafterSaleDetectCode]").val(afterSaleDetectCode);
			}
			$("#afterSaleInventoryProductList").datagrid("load", {
				productCode:productCode,
				afterSaleDetectCode:afterSaleDetectCode,
				type:type,
				inventoryRecordId:inventoryRecordId
			});
		}
		
		function byProduct() {
			var selectrows = $("#afterSaleInventoryProductList").datagrid("getChecked");
			var selectlength = selectrows.length;
			if (selectlength <= 0) {
				$.messager.show({
					title : '提示',
					msg : "没有要报溢的商品！"
				});
				return  false;
			}
			var products = new Array();
			for (var i = 0 ; i < selectlength ; i ++) {
				products[i]=selectrows[i].afterSaleInventoryProductId;
			} 
			var inventoryRecordId = $.trim($("#inventoryRecordId").val());
			$.ajax({
                type: "post", //调用方式  post 还是 get
                url: "${pageContext.request.contextPath}/admin/AfStock/addMultiByProduct.mmx",
                data : "products=" + products+"&inventoryRecordId=" +inventoryRecordId,
                dataType: "text", //返回的数据的形式
                success: function(data) { 
                	try {
        				var r = $.parseJSON(data);
       					$.messager.show({
       						title : '提示',
       						msg : decodeURI(r.msg)
       					});
        			} catch (e) {
        				$.messager.alert('提示', result);
        			}
                }
			})
		}
		
		function bsProduct() {
			var selectrows = $("#afterSaleInventoryProductList").datagrid("getChecked");
			var selectlength = selectrows.length;
			if (selectlength <= 0) {
				$.messager.show({
					title : '提示',
					msg : "没有要报损的商品！"
				});
				return  false;
			}
			var products = new Array();
			for (var i = 0 ; i < selectlength ; i ++) {
				products[i]=selectrows[i].afterSaleInventoryProductId;
			} 
			var inventoryRecordId = $.trim($("#inventoryRecordId").val());
			$.ajax({
                type: "post", //调用方式  post 还是 get
                url: "${pageContext.request.contextPath}/admin/AfStock/addMultiBsProduct.mmx",
                data : "products=" + products +"&inventoryRecordId=" +inventoryRecordId,
                dataType: "text", //返回的数据的形式
                success: function(data) { 
                	try {
        				var r = $.parseJSON(data);
       					$.messager.show({
       						title : '提示',
       						msg : decodeURI(r.msg)
       					});
        			} catch (e) {
        				$.messager.alert('提示', result);
        			}
                }
			})
		}
		
		function exportProduct() {
			var productCode = $("#toolbar input[id=hiddenproductCode]").val();
			var afterSaleDetectCode = $("#toolbar input[id=hiddenafterSaleDetectCode]").val();
			var type = $("#toolbar input[id=type]").val();
			var inventoryRecordId = $("#toolbar input[id=inventoryRecordId]").val();
			var param = "productCode=" + productCode + "&afterSaleDetectCode=" + afterSaleDetectCode + "&type=" + type + "&inventoryRecordId=" + inventoryRecordId;
			window.location.href="${pageContext.request.contextPath}/admin/AfStock/exportInventoryProductList.mmx?" + param;
		}
		
		
		function differentWholeCodeDetail(index){
			if (index != undefined) {
				$('#afterSaleInventoryRecordList').datagrid('selectRow', index);
			}
			var row = $('#afterSaleInventoryRecordList').datagrid('getSelected');
			$('<div/>').dialog({
				href : '${pageContext.request.contextPath}/admin/afStock/afterSaleInventoryProductDetail.jsp',
				width : 900,
				height : 520,
				modal : true,
				title : '实物与记录货位不一致商品',
				onClose : function() {
					$(this).dialog('destroy');
				},
				onLoad : function(){
					$("#operateDiv").show();
					$("#toolbar input[id=inventoryRecordId]").val(row.id);
					$("#toolbar input[id=type]").val('4');
					initDifferentProducts(row.id);
				}
			});
		}
		
		function initDifferentProducts(recordId){
			$("#afterSaleInventoryProductList").datagrid({
				url:'${pageContext.request.contextPath}/admin/AfStock/queryInventoryProductList.mmx?type=4&inventoryRecordId=' + recordId,
				idField : 'afterSaleInventoryProductId',
				fitColumns : true,
				striped : true,
				nowrap : true,
				loadMsg : '正在努力为您加载..',
				pageNumber:1,
				pageSize:20,
				pageList:[5,10,15,20],
				frozenColumns:[[
					{field:'afterSaleInventoryProductId',title:'盘点商品id',checkbox:true}
				]],
				rownumbers:true,
				singleSelect:true,//只选择一行后变色
				columns:[[
				    	{field:'afterSaleDetectCode',title:'售后处理单号',width:80,align:'center'},
				    	{field:'afterSaleDetectProductStatus',title:'售后处理单状态',width:80,align:'center'},
				    	{field:'afterSaleOrderCode',title:'售后单号',width:100,align:'center'},
				    	{field:'productCode',title:'商品编号',width:100,align:'center'},
				    	{field:'productName',title:'商品名称',width:80,align:'center'},
				    	{field:'recordWholeCode',title:'记录货位号',width:100,align:'center'},
				    	{field:'realWholeCode',title:'实际货位号',width:100,align:'center'}
				    ]]
			});
		}
		
		function modifyRealCargo(){
			var items = $("#afterSaleInventoryProductList").datagrid('getSelections');
			if(items.length!=0){
				var inventoryProductIds="";
				$.each(items,function(index,item){
					inventoryProductIds = inventoryProductIds + item.afterSaleInventoryProductId+",";
				});
				var inventoryRecordId = $("#toolbar input[id=inventoryRecordId]").val();
				$.ajax({
					url:'${pageContext.request.contextPath}/admin/AfStock/modifyRealCargoCode.mmx',
					data:{
						inventoryProductIds:inventoryProductIds.substring(0,inventoryProductIds.length-1),
						inventoryRecordId:inventoryRecordId},
					cache:false,
					dataType:'json',
					type:'post',
					success:function(result){
						if(result){
							$.messager.show({
								title:'提示',
								msg:result.msg,
								timeout:3000,
								showType:'slide'
							});
							$("#afterSaleInventoryProductList").datagrid("reload");
						}
					}
				});	
			}else{
				$.messager.show({
					title:'提示',
					msg:'请至少选择一个商品！',
					timeout:3000,
					showType:'slide'
				});
			}
		}
</script>
