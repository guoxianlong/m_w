<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://www.maimaibao.com/core" prefix="mmb" %>
<html>
<head>
<title>买卖宝后台</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/productStock2.js"></script>
</head>
<body>
<div style="width: 100%;">
	<fieldset>
		<legend>查询栏</legend>
		<form id="form" method="post">
		<table>
			<tr>
				<td><span style="font-size: 12px;">产品编号：</span><input type="text" style="width:152px;" size=12 name="code" id="code"/></td>
				<td><span style="font-size: 12px;">产品名称：</span><input type="text" size=20 name="name" style="width:152px;" id="name"/><span style="font-size: 12px;">（模糊）</span></td>
				<td><span style="font-size: 12px;">状&nbsp;&nbsp;态&nbsp;&nbsp;：</span><input name="status" style="width:152px;" class="easyui-combobox" editable="false" id="status"></td>
			</tr>
			<tr>
				<td><span style="font-size: 12px;">一级分类：</span><input name="parentId1" style="width:152px;" class="easyui-combobox" editable="false" id="parentId1"></td>
				<td><span style="font-size: 12px;">二级分类：</span><input name="parentId2" style="width:152px;" class="easyui-combobox" editable="false" id="parentId2"></td>
				<td><span style="font-size: 12px;">三级分类：</span><input name="parentId3" style="width:152px;" class="easyui-combobox" editable="false" id="parentId3"></td>
			</tr>
			<tr>
				<td><span style="font-size: 12px;">库&nbsp;类&nbsp;型：</span><input name="stockType" style="width:152px;" class="easyui-combobox" editable="false" id="stockType"></td>
				<td><span style="font-size: 12px;">库&nbsp;区&nbsp;域：</span><input name="stockArea" style="width:152px;" class="easyui-combobox" editable="false" id="stockArea"></td>
			</tr>
			<tr>
				<mmb:permit value="94"><td><span style="font-size: 12px;">供&nbsp;应&nbsp;商：</span><input name="suppliertext" style="width:152px;" class="easyui-combobox" editable="false" id="suppliertext"></td></mmb:permit>
				<td align="center"><a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="queryProductstock">查询</a>
				<td>
<!-- 					<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="queryAllProductstock">查询全部</a> -->
					<a class="easyui-linkbutton" data-options="iconCls:'icon-redo'" plain="true" id="export">导出列表</a>
					<input type="hidden" id="page_hidden"/></td>
			</tr>
		</table>
		</form>
	</fieldset>
	<div id="messages"></div>
</div>
<table id="mbDatagrid" class="easyui-datagrid" style="overflow-y：scroll"></table>
<script type="text/javascript">
	var x = 0;
	$(function(){
		//状态		
		$("#status").combobox({
			valueField:'id',
			textField:'text',
			data:[{id:'0',text:'全部',selected:'true'},
			      {id:'5',text:'促销'},
			      {id:'10',text:'热销'},
			      {id:'50',text:'普通'},
			      {id:'100',text:'下架'},
			      {id:'120',text:'隐藏'},
			      {id:'130',text:'缺货'}]
		});
		//searchProductStockController/querySelectParentId1.mmx
		//一级分类
		$("#parentId1").combobox({
			url:'<%=request.getContextPath()%>/searchProductStockController/querySelectParentId1NoPermission.mmx',			
			valueField:'id',
			textField:'text',
			onChange:function(rec){
				if(x!=0){
// 					$("#parentId3").combobox("loadData",[{id:'',text:''}]);
// 					$("#parentId3").combobox("clear");
					$.ajax({
						url:'<%=request.getContextPath()%>/searchProductStockController/querySelectParentId2.mmx',
						data:{parentId1:rec},
						cache:false,
						dataType:'text',
						success:function(result){
							var y = 0;
							var re = eval('('+result+')');
							// 二级分类
							$("#parentId2").combobox({
								valueField:'id',
								textField:'text',
								data:re['parentId2'],
								onChange:function(rec2){
									if(y!=0){
										//选中二级分类后列出三级分类
										$("#parentId3").combobox({
											url:'<%=request.getContextPath()%>/searchProductStockController/querySelectParentId3.mmx?parentId2='+rec2,
											valueField:'id',
											textField:'text'
										});
									}
									y=1;
								}
							});
							//三级分类--开始选中一级分类后显示的内容
							$("#parentId3").combobox({
								valueField:'id',
								textField:'text',
								data:re['parentId3']
							});
						}
					});			
				}
				x=1;
			}
		});
		//供应商
		$("#suppliertext").combobox({
			url:'<%=request.getContextPath()%>/searchProductStockController/querySelectSupplier.mmx',
			valueField:'id',
			textField:'text'
		});
		//库类型
		$("#stockType").combobox({
			url:'<%=request.getContextPath()%>/CargoController/querySelectStockType.mmx?flag=notAll',
			valueField:'id',
			textField:'text',
			onLoadSuccess:function(){
				//默认选择第一个
				$("#stockArea").combobox({
					valueField:'id',
					textField:'text',
					url:'<%=request.getContextPath()%>/CargoController/querySelectStockAreaAccess.mmx?stockType='+$("#stockType").combobox('getValue')
				});
			},
			onChange:function(rec){
				$("#stockArea").combobox({
					valueField:'id',
					textField:'text',
					url:'<%=request.getContextPath()%>/CargoController/querySelectStockAreaAccess.mmx?stockType='+rec
				});
			}
		});
		
		// 条件查询
		$("#queryProductstock").click(function(){
			$("#news").remove();
			var code = $("#code").val();
			var name = $("#name").val();
			var status = $("#status").combobox('getValue');
			var parentId1 = $("#parentId1").combobox('getValue');
			var parentId2 = $("#parentId2").combobox('getValue');
			var parentId3 = $("#parentId3").combobox('getValue');
			var stockType = $("#stockType").combobox('getValue');
			var stockArea = $("#stockArea").combobox('getValue');
			var suppliertext = $("#suppliertext").combobox('getValue');
			var params = "code="+code+"&name="+name+"&status="+status+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3
						+"&stockType="+stockType+"&stockArea="+stockArea+"&suppliertext="+suppliertext;
			$("#page_hidden").val(params);
			var parameter = {code:code,name:name,status:status,parentId1:parentId1,parentId2:parentId2,parentId3:parentId3,
						stockType:stockType,stockArea:stockArea,suppliertext:suppliertext};
			loadDataGrid(parameter);
		});
		
		$("#export").click(function(){
			window.location.href="${pageContext.request.contextPath}/searchProductStockController/searchProductStockAmountExport.mmx?"+$("#page_hidden").val();
		});
	});
	function getData(z){
		var str = "[";
		for (i = 1; i <= sps[z].length; i ++){
			if(i!=1){
				str += "{'id':'"+sps[z][i-1].value+"','text':'"+sps[z][i-1].text+"'}"
			}else{
				str += "{'id':'"+sps[z][i-1].value+"','text':'"+sps[z][i-1].text+"','selected':'true'}"
			}
			if(i==sps[z].length){
				str += "]";				
			}else{
				str += ",";
			}
		}
		
		return str;
	}
	function loadDataGrid(parameter){
		$.ajax({
			url:'<%=request.getContextPath()%>/searchProductStockController/searchProductStockAmount.mmx',
			cache:false,
			dataType:'text',
			type:'post',
			data:parameter,
			success:function(dd){
				var result = eval('('+dd+')');
				if(result['result']=='failure'){
					$.messager.show({
						title:'提示',
						msg:result['tip'],
						timeout:3000,
						showType:'slide'
					});
				}else{
					var row = result['rows'];
//	 				var rr = eval('('+row+')');
					var column = result['columns'];
//	 				var cc = eval('('+column+')');
					if(result['boo']=='true'){
						$("#messages").append("<span id='news' style='font-size: 12px;'>共有："+result['size']+"种产品 &nbsp;&nbsp;"+"总金额："+result['totalPrice']+"</span>");
					}
					$("#mbDatagrid").datagrid({
						columns:column,
						title:"库存列表",
						iconCls:'icon-ok',
						width:'auto',
						height:'auto',
						fitColumns:true,
						loadMsg:'数据加载中...',
						rownumbers:true,
						singleSelect:true
					});
					$("#mbDatagrid").datagrid('loadData',row);
				}
			}
		});
	}
</script>
</body>

</html>
