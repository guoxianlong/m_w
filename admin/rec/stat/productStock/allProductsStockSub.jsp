<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://www.maimaibao.com/core" prefix="mmb" %>
<html>
<title>买卖宝后台</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
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
				<mmb:permit value="87">
				<td><span style="font-size: 12px;">代&nbsp;理&nbsp;商：</span><input name="proxy" style="width:152px;" class="easyui-combobox" editable="false" id="proxy"></td>
				</mmb:permit>
				<td><a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="queryProductstock">查询</a>
				<td><a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="queryAllProductstock">查询全部</a>
					<input type="hidden" id="page_hidden"/></td>
			</tr>
		</table>
		</form>
	</fieldset>
	<div id="messages"><a class="easyui-linkbutton" data-options="iconCls:'icon-redo'" plain="true" id="export">导出列表</a></div>
</div>
<table id="mbDatagrid" class="easyui-datagrid"></table>
<script type="text/javascript">
	var x = 0;
	$(function(){
		/* $("#mbDatagrid").datagrid({
			title:"库存列表",
			iconCls:'icon-ok',
			width:'auto',
			height:'auto',
			pageNumber:1,
			pageSize:20,
			fitColumns:true,
			pageList:[5,10,15,20],
			striped:true,
			collapsible:true,
			loadMsg:'数据加载中...',
			rownumbers:true,
			singleSelect:true,//只选择一行后变色
			pagination:true,
			columns:[
					[{field:'code',title:'编号',rowspan:3,colspan:1},{field:'name',title:'名称', align:'center',rowspan:3, colspan:1},
					 {field:'',title:'库存数量', align:'center',rowspan:1, colspan:26},
					 {field:'',title:'库存标准', align:'center',rowspan:2, colspan:2},{field:'statusName',title:'状态', align:'center',rowspan:3, colspan:1},
					 {field:'parent1_name',title:'一级分类', align:'center',rowspan:3, colspan:1},{field:'parent2_name',title:'二级分类', align:'center',rowspan:3, colspan:1},
					 {field:'parent3_name',title:'三级分类', align:'center',rowspan:3, colspan:1},{field:'rank',title:'等级', align:'center',rowspan:3, colspan:1},
					 {field:'query',title:'库存记录', align:'center',rowspan:3, colspan:1}],
					[{field:'stock_count',title:'库存总数', align:'center',rowspan:2, colspan:1},{field:'',title:'待验库', align:'center',rowspan:1, colspan:3},
					 {field:'',title:'合格库', align:'center',rowspan:1, colspan:4},{field:'',title:'退货库', align:'center',rowspan:1, colspan:4},
					 {field:'',title:'返厂库', align:'center',rowspan:1, colspan:3},{field:'',title:'维修库', align:'center',rowspan:1, colspan:3},
					 {field:'',title:'残次品库', align:'center',rowspan:1, colspan:3},{field:'',title:'样品库', align:'center',rowspan:1, colspan:4},
					 {field:'',title:'售后库', align:'center',rowspan:1, colspan:1}],
					[{field:'dyk_fc',title:'芳村', align:'center',rowspan:1, colspan:1},{field:'dyk_zc',title:'增城', align:'center',rowspan:1, colspan:1},
					 {field:'dyk_wx',title:'无锡', align:'center',rowspan:1, colspan:1},{field:'hg_fc',title:'芳村', align:'center',rowspan:1, colspan:1},
					 {field:'hg_gs',title:'广速', align:'center',rowspan:1, colspan:1},{field:'hg_zc',title:'增城', align:'center',rowspan:1, colspan:1},
					 {field:'hg_wx',title:'无锡', align:'center',rowspan:1, colspan:1},{field:'th_bj',title:'北京', align:'center',rowspan:1, colspan:1},
					 {field:'th_fc',title:'芳村', align:'center',rowspan:1, colspan:1},{field:'th_zc',title:'增城', align:'center',rowspan:1, colspan:1},
					 {field:'th_wx',title:'无锡', align:'center',rowspan:1, colspan:1},{field:'fc_bj',title:'北京', align:'center',rowspan:1, colspan:1},
					 {field:'fc_fc',title:'芳村', align:'center',rowspan:1, colspan:1},{field:'fc_zc',title:'增城', align:'center',rowspan:1, colspan:1},
					 {field:'wx_bj',title:'北京', align:'center',rowspan:1, colspan:1},{field:'wx_fc',title:'芳村', align:'center',rowspan:1, colspan:1},
					 {field:'wx_zc',title:'增城', align:'center',rowspan:1, colspan:1},{field:'ccp_bj',title:'北京', align:'center',rowspan:1, colspan:1},
					 {field:'ccp_fc',title:'芳村', align:'center',rowspan:1, colspan:1},{field:'ccp_zc',title:'增城', align:'center',rowspan:1, colspan:1},
					 {field:'yp_bj',title:'北京', align:'center',rowspan:1, colspan:1},{field:'yp_fc',title:'芳村', align:'center',rowspan:1, colspan:1},
					 {field:'yp_zc',title:'增城', align:'center',rowspan:1, colspan:1},{field:'yp_wx',title:'无锡', align:'center',rowspan:1, colspan:1},
					 {field:'sh_fc',title:'芳村', align:'center',rowspan:1, colspan:1},
					 {field:'stockstandard_bj',title:'北京',align:'center',rowspan:1, colspan:1},{field:'stockstandard_fc',title:'芳村',align:'center',rowspan:1, colspan:1}]
					]
		}); */
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
			url:'<%=request.getContextPath()%>/searchProductStockController/querySelectParentId1Permission.mmx',			
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
		//代理商
		$("#proxy").combobox({
			url:'<%=request.getContextPath()%>/searchProductStockController/querySelectProxy.mmx',
			valueField:'id',
			textField:'text'
		});
		$("#queryAllProductstock").click(function(){
			loadDatagrid2("{}");
		});
		$("#queryProductstock").click(function(){
			var code = $("#code").val();
			var name = $("#name").val();
			var status = $("#status").combobox('getValue');
			var parentId1 = $("#parentId1").combobox('getValue');
			var parentId2 = $("#parentId2").combobox('getValue');
			var parentId3 = $("#parentId3").combobox('getValue');
			var proxy = $("#proxy").combobox('getValue');
			var params = "code="+code+"&name="+name+"&status="+status+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3
						+"&proxy="+proxy;
			$("#page_hidden").val(params);
			var parameter = {code:code,name:name,status:status,parentId1:parentId1,parentId2:parentId2,parentId3:parentId3,proxy:proxy};
			loadDatagrid2(parameter);
		});
		$("#export").click(function(){
			window.location.href="${pageContext.request.contextPath}/searchProductStockController/searchProductStockExport.mmx?"+$("#page_hidden").val();
		});
	});
	/* function loadDatagrid(para){
		$("#news").remove();
		$("#mbDatagrid").datagrid({
			url:'${pageContext.request.contextPath}/searchProductStockController/searchProductStock.mmx',
			queryParams:para,
			onLoadSuccess:function(data){
				if(data['boo']=="true"){
					$("#messages").append("<span id='news' style='font-size: 12px;'>总金额："+data['totalPrice']+"</span>");
				}
			}
		});
	} */
	//改成动态获取列，缘由如果先动态获取列，field没能记住规则，则rows对应的field也对应不上，所以需要全部同时加载
	function loadDatagrid2(para){
		$("#news").remove();
		$.ajax({
			url:'${pageContext.request.contextPath}/searchProductStockController/getProductStockSubColumns.mmx',
			cache:false,
			dataType:'text',
			type:'post',
			data:para,
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
					var column = result['columns'];
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
						pageNumber:1,
						pageSize:20,
						fitColumns:true,
						pageList:[5,10,15,20],
						striped:true,
						collapsible:true,
						loadMsg:'数据加载中...',
						rownumbers:true,
						singleSelect:true,//只选择一行后变色
						pagination:true,
					});
					$("#mbDatagrid").datagrid('loadData',row);
				}
			}
		});
	}
	
</script>
</body>

</html>
