<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page
	import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*"%>
<%
	List areaList = (List) request.getAttribute("areaList");
    String tr = (String) request.getAttribute("str");
	String batchId = (String) request.getParameter("batchId");
	String groupId = new String();
	String orderType = new String();
	String shengwaiCount = request.getParameter("shengwaiCount");
	String shengneiCount = request.getParameter("shengneiCount");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<!DOCTYPE html>
<%
	int hour = 24;
	int m = 5;
%>
<html>
<head>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	var addBrand;
	var userForm;
	var passwordInput;
	var userRoleDialog;
	var userRoleForm;
	var editDiv;
	var editForm;
	var datagrid;
	var ind = [];
	var indexNum;
//	height:auto;width:auto; display: none;"
//	
//	pageSize ="10"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
//	toolbar="#sucTB" rownumbers="true" pagination="true" singleSelect="true"> 
	function loadCombobox(){
		if(ind.length > 0){
			for(var i=0;i<ind.length;i++){
				var ddd = '#typ' + i;
				$(ddd).combobox({
		         	url : '${pageContext.request.contextPath}/SortingController/getProductTypeList1.mmx',
		         	valueField : 'id',
					textField : 'productTypeName',
					onSelect : function() {
				    $('#datagrid').datagrid(
					'selectRow', indexNum)
				}
		         });
			}
		}
	}
	function ss(ind){
		indexNum = ind;
	}
	$(function() {
			datagrid = $('#datagrid').datagrid({
			rownumbers:'true',
			striped:'true',
			url : '${pageContext.request.contextPath}/SortingController/sortingBatchOrderList.mmx?batchId=<%=batchId%>',
							toolbar : '#toolbar',
							title : '分拣批次订单列表',
							fit : true,
							fitColumns : true,
							pagination : true,
							pageSize : 20,
							pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
							height:'auto',
							width:'auto',
							nowrap : false,
							border : false,
							idField : 'id',//记住翻页之后已选中相
							fixRowHeight : function(index) {
								selectRow(index).height = 50;
							},

							frozenColumns : [ [ {
								title : 'id',
								field : 'id',
								width : 50,
								align : 'center',
								checkbox : true
							}, {
								field : 'orderCode',
								title : '订单编号',
								align : 'center',
								sortable : true,
								width : '100',
							    formatter : function(value, row, index) {
						        	return '<a href="javascript:void(0);" class="editbutton" onclick="orderInfo('+row.id+')">'+row.orderCode+'</a>';
								}
							} ] ],
							columns : [ [
									{
										field : 'orderStockCode',
										title : '出库单编号',
										align : 'center',
										width : '100'
									},
									{
										field : 'ckTime',
										align : 'center',
										title : '申请出库时间',
										sortable : true,
										width : '150',
										formatter : function(value, rowData,
												rowIndex) {
											if (rowData.ckTime != ''&& rowData.ckTime != null) {
												return rowData.ckTime.substring(0, 19);
											}
										}
									},
									{
										field : 'deliverName',
										title : '快递公司',
										align : 'center',
										width : '80',
									},
									{
										field : 'address',
										title : '订单地址',
										align : 'center',
										width : '180',
									},
									{
										field : 'productNames',
										title : '商品名称',
										align : 'center',
										width : '100'
									},
									{
										field : 'orderType',
										title : '产品分类',
										align : 'center',
										width:'150',
										formatter : function(value, rowData,rowIndex) {
											ind.push(rowIndex);
											return "<label onclick=\"ss(" + rowIndex + ")\"><input id=\"typ"+ rowIndex +"\" value=\"" + value +"\" /></label>";
										}
									}, {
										field : 'Dprice',
										title : '应收款',
										align : 'center',
										width : '50'

									}, {
										field : 'name',
										title : '客户名称',
										align : 'center',
										width : 100,
									}, {
										field : 'phone',
										title : '电话',
										align : 'center',
										width :50,
										formatter : function(value, rowData,
												rowIndex) {
											if (rowData.phone != ''
													&& rowData.phone != null &&rowData.phone.length>7) {
												return rowData.phone
														.substring(0, 3)+"****"+rowData.phone
														.substring(7);
											}
										}
									} ] ],
							onLoadSuccess : function(data) {
								try {
									if (data.footer[0].batchCode != 'undefined'&& data.footer[0].batchCode != null&& data.footer[0].batchCode != "") {
										$("#batchCode").html(
														"<font color='blue'>所属分拣批次:"+ data.footer[0].batchCode+ "</font>");
									} else {
										$("#batchCode").html("");
									}
									if (data.footer[0].noAssignTypeOrderCount != 'undefined'&& data.footer[0].noAssignTypeOrderCount != null&& data.footer[0].noAssignTypeOrderCount != "") {
										$("#noAssignTypeOrderCount").html(
														"<font color='red'>未指定产品分类的:"+ data.footer[0].noAssignTypeOrderCount+ "</font>");
									} else {
										$("#noAssignTypeOrderCount").html("");
									}
									if (data.footer[0].noAssignDeliverOrderCount != 'undefined'&& data.footer[0].noAssignDeliverOrderCount != null&& data.footer[0].noAssignDeliverOrderCount != "") {
										$("#noAssignDeliverOrderCount").html(
														"<font color='red'>未分配归属物流的:"+ data.footer[0].noAssignDeliverOrderCount+ "</font>");
									} else {
										$("#noAssignDeliverOrderCount").html("");
									}
									if (data.footer[0].orderCount != 'undefined'&& data.footer[0].orderCount != null&& data.footer[0].orderCount != "") {
										$("#orderCount").html(
												"<font color='red'>订单数:"+ data.footer[0].orderCount+ "</font>");
									} else {
										$("#ordercount").html("");
									}
									if (data.footer[0].SKUcount != 'undefined'&& data.footer[0].SKUcount != null&& data.footer[0].SKUcount != "") {
										$("#SKUcount").html(
														"SKU数:"+ data.footer[0].SKUcount);
									} else {
										$("#SKUcount").html("");
									}
									if (data.footer[0].productCount != 'undefined'&& data.footer[0].productCount != null&& data.footer[0].productCount != "") {
										$("#productCount").html(
														"商品个数:"+ data.footer[0].productCount);
									} else {
										$("#productCount").html("");
									}
									for ( var i = 0; i < $("#datagrid").datagrid("getRows").length; i++) {
										loadCombobox();
									}
									for ( var i = 0; i < $("#datagrid").datagrid("getRows").length; i++) {
										$("#datagrid").datagrid("beginEdit", i);
									}
								} catch (e) {
									$.messager.alert("提示", "错误", "info");
								}
							}
						});
			$('#searchProductType').combobox({   
				url : '${pageContext.request.contextPath}/SortingController/getProductTypeList.mmx',
				valueField : 'id',   
				textField : 'productTypeName',
				panelHeight:'auto'
			}); 
	});
	function edit() {
		var rows = datagrid.datagrid('getSelections');
			var id = new Array();
			var orderType = new Array();
		for ( var i = 0; i < rows.length; i++) {
			id[i]= rows[i].id;
			orderType[i] = $('#typ' + i).combobox('getValue');
		}
		var ids = id.join("-");
		var orderTypes = orderType.join("-");
		window.location.href = '<%=request.getContextPath()%>/SortingController/modifyOrder.mmx?batchId=<%=batchId%>&ids='+ids+'&orderTypes='+orderTypes;
		$('#datagrid').datagrid('reload');  
	}
	function searchOrder() {
		datagrid.datagrid('load', {
			code : $('#searchForm').find('[name=code]').val(),
			deliver : $('#searchForm').find('[name=deliver]').val(),
			searchProductType : $('#searchForm').find('[name=searchProductType]').val()
		});
	};
    function orderInfo(id){ 
		window.location.href = '<%=request.getContextPath()%>/admin/order.do?id='+id;
    }
    function addGroup(id){
    	window.location.href = '<%=request.getContextPath()%>/SortingController/makeSortingBatchGroup3.mmx?batchId=<%=batchId%>';
	}
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" border="false" style="overflow: hidden;">
		<div>
			<form id="form1" name="form1" method="post">
				<table id="datas" width="99%" border="0" cellspacing="0">
					<tr>
						<td style="color: #000"><h4 id="batchCode"></h4> </td>
						<td style="color: #000"><h4 id="noAssignTypeOrderCount"></h4></td>
						<td style="color: #000"><h4 id="noAssignDeliverOrderCount"></h4></td>
					    <td>  <%if(group.isFlag(590)){%><input type="button" value="生成分拣波次" onclick="addGroup(<%=batchId%>)"><%} %></td>
					</tr>
					<tr>
						<td width="25%"><h3 id="orderCount"></h3></td>
						<td width="25%"><h3 id="SKUcount"></h3></td>
						 <td width="25%"><h3 id="productCount"></h3></td>
					</tr>
				</table>
			</form>
		</div>
		<div id="tt" class="easyui-tabs" style="width: auto; height: 180px;">
		<div title="批量修改归属物流" style="padding: 20px;">
				<form id="form3" name="form3" method="post"
					action="<%=request.getContextPath()%>/SortingController/modifyOrderDeliver.mmx?batchId=<%=batchId%>">
					<table width="55%" border="0">
						<tr>
							<td rowspan="2"><textarea name="ordersDeliver"
									id="ordersDeliver" cols="45" rows="5"></textarea> 
								<input type="hidden" name="batchId" value="<%=batchId%>">
							</td>
							<td><font color='red'>输入格式：(可从excel中复制两列，粘贴至下面输入框中)<br>
									QD090101249181&nbsp;广州宅急送<br> D090101240066&nbsp;广速省外<br></font>
							</td>
						</tr>
						<tr>
							<td><input type="submit" name="button" id="button"
								value="提交"></td>
						</tr>
					</table>
				</form>
				 <form method="post" action="<%=request.getContextPath()%>/SortingController/modifyOrderDeliverToEMS.mmx?batchId=<%=batchId%>">
	   	<input type="submit" name="button" id="button" value="分配剩余订单给EMS"><%if(tr!=null){%><%=tr %><%} %>
	   	<%if((shengwaiCount!=null&&shengwaiCount.length()>0)||(shengneiCount!=null&&shengneiCount.length()>0)) {%>
	   	<font color="black">分配省内订单 <%=shengneiCount %> 个，省外 <%=shengwaiCount %>个</font>
	   	<%} %>
	   </form>
			</div>
			<div title="查询" data-options="closable:true" style="overflow: auto; padding: 20px; ">
       <form id=searchForm name="searchForm" method="post" action="">
       <table border="0">
        <tr>
	        <td>快递公司：
		        <select name="deliver">
				<option value="-1">全部</option>
				<option value="0">未分配</option>
				<option value="9">广速省外</option>
				<option value="10">广州宅急送</option>
				<option value="11">广东省速递局</option>
				<option value="12">广州顺丰</option>
				<option value="13">深圳自建</option>
				<option value="14">路通速递</option>
				<option value="16">如风达</option>
				<option value="17">赛澳递江苏</option>
				<option value="18">赛澳递上海</option>
			    </select>
		    </td>
	        <td> 
	                                产品分类:<select name="searchProductType" id="searchProductType" class="easyui-combobox" panelHeight="auto" style="width:100px"></select>
	        </td>
          	<td>
          		<input type="text" name="code" id="code" size="30" value="订单编号/分拣波次号/分拣批次号" onfocus="if(this.value=='订单编号/分拣波次号/分拣批次号'){this.value=''}">
          	</td>
          	<td>
          	 <a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchOrder();" href="javascript:void(0);">查找</a>
          	</td>
        </tr>
          </table>
  </form>			
            </div>
			
		</div>
		<div id="toolbar" class="datagrid-toolbar"
			style="height: auto; ">
			<a class="easyui-linkbutton" iconCls="icon-save" onclick="edit();"
				href="javascript:void(0);">修改所选订单的商品种类</a>
		</div>
		<table id="datagrid"></table>
	</div>

</body>
</html>