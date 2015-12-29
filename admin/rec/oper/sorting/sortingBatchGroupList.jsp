<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="http://www.jeasyui.com/easyui/datagrid-detailview.js"></script>
<%
	String batchId = request.getParameter("batchId");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<script type="text/javascript" charset="UTF-8">
	var status;
	var mailingBatchId;
	var code;
	var datagrid;
	var plDatagrid;
	$(function(){
		datagrid = $('#datagrid').datagrid({
			url:'${pageContext.request.contextPath}/SortingController/sortingBatchInfo.mmx?batchId=<%=batchId %>',
			onLoadSuccess : function(data) {
				try {
					if (data.footer[0].batch != 'undefined' && data.footer[0].batch !=null && data.footer[0].batch != "") {
						$("#batch").html("<font color='blue'>所属分拣批次:"+data.footer[0].batch+"</font>");
					} else {
						$("#batch").html("");
					}
					if (data.footer[0].createTime != 'undefined' && data.footer[0].createTime !=null && data.footer[0].createTime != "") {
						$("#createTime").html("<font color='red'>创建时间:"+data.footer[0].createTime.substring(0,19)+"</font>");
					} else {
						$("#createTime").html("");
					}
					if (data.footer[0].completeTime != 'undefined' && data.footer[0].completeTime !=null && data.footer[0].completeTime != "") {
						$("#completeTime").html("<font color='red'>完成时间:"+data.footer[0].completeTime.substring(0,19)+"</font>");
					} else {
						$("#completeTime").html("完成时间:");
					}
					if (data.footer[0].statusName != 'undefined' && data.footer[0].statusName !=null && data.footer[0].statusName != "") {
						$("#statusName").html("<font color='red'>当前状态:"+data.footer[0].statusName+"</font>");
					} else {
						$("#statusName").html("");
					}
					if (data.footer[0].completeCount != 'undefined' && data.footer[0].completeCount !=null && data.footer[0].completeCount != "") {
						$("#completeCount").html("已完成:"+data.footer[0].completeCount);
					} else {
						$("#completeCount").html("已完成:0");
					}
					if (data.footer[0].sortingCount != 'undefined' && data.footer[0].sortingCount !=null && data.footer[0].sortingCount != "") {
						$("#sortingCount").html("分拣中:"+data.footer[0].sortingCount);
					} else {
						$("#sortingCount").html("分拣中:0");
					}
					if (data.footer[0].noPrintCount != 'undefined' && data.footer[0].noPrintCount !=null && data.footer[0].noPrintCount != "") {
						$("#noPrintCount").html("未打单:"+data.footer[0].noPrintCount);
					} else {
						$("#noPrintCount").html("未打单:0");
					}
				} catch(e) {
					$.messager.alert("提示", "错误" ,"info");
				}
			}
		});
		plDatagrid = $('#plDatagrid').datagrid({
			view: detailview,
			detailFormatter:function(index,row){
				return '<div style="padding:1px"><table id="ddv-' + index + '"></table></div>';
			},
			rowStyler: function(index,row){
				return 'background-color:#CAE1FF;color:#000000;';
			},
			onExpandRow: function(index,row){
				delIndex = index;
				$('#ddv-'+index).datagrid({
					url:'${pageContext.request.contextPath}/SortingController/sortingBatchGroupDetail.mmx?groupId=' + row.id,
					toolbar : '#toolbar',
					fitColumns:true,
					singleSelect : true,
					rownumbers : true,
					loadMsg:'',
					height:'auto',
					frozenColumns : [ [ {
						title : 'id',
						field : 'id',
						width : 50,
						align :'center',
						checkbox : true
					}] ],
					columns:[[
						{field:'orderCode',title:'订单编号',width:50,align:'center'},
						{field:'orderStockCode',title:'出库单编号',width:50,align:'center'},
						{field:'statusName',title:'分拣状态',width:50,align:'center'},
						{field:'deliverName',title:'快递公司',width:50,align:'center'},
						{field:'orderTypeName',title:'商品分类',width:40,align:'center'},
						{field:'productCount',title:'SKU个数',width:40,align:'center'},
						{field:'productCode',title:'商品编号&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;货位号',width:120}
					]],
					onResize:function(){
						$('#plDatagrid').datagrid('fixDetailRowHeight',index);
					},
					onLoadSuccess:function(){
						setTimeout(function(){
							$('#plDatagrid').datagrid('fixDetailRowHeight',index);
						},0);
					}
				});
				$('#plDatagrid').datagrid('fixDetailRowHeight',index);
			}
		});
	});
	$('#searchProductType').combobox({   
		url : '${pageContext.request.contextPath}/SortingController/getProductTypeList.mmx',
		valueField : 'id',   
		textField : 'productTypeName',
		panelHeight:'auto'
	}); 
	function edit() {
		//for ( var i = 0; i < $("#datagrid").datagrid("getRows").length; i++) {
		//	$("#datagrid").datagrid("endEdit", i);
		//}
		var rows = datagrid.datagrid('getSelections');
			var id = new Array();
			var orderType = new Array();
		for ( var i = 0; i < rows.length; i++) {
			id[i]= rows[i].id;
			orderType[i] = rows[i].orderType;
		}
		var ids = id.join("-");
		var orderTypes = orderType.join("-");
		window.location.href = '<%=request.getContextPath()%>/SortingController/modifyOrder.mmx?batchId=<%=batchId%>&ids='+ids+'&orderTypes='+orderTypes;
		$('#datagrid').datagrid('reload');  
	}
	function searchGroup() {
		plDatagrid.datagrid('load', {
			text : $('#searchForm').find('[name=text]').val(),
		});
	};
	function inputCode1(sortingBatchGroupId,printType){
	  	  var code = window.prompt("员工号:","");
	  	  if(code==""){
		 	 inputCode1(sortingBatchGroupId,printType);
		  }else if(code){
		  	openPrintPage1(code,sortingBatchGroupId,printType);
		  }
	}
	function openPrintPage(sortingBatchGroupId,printType){
		if( window.confirm("是否继续操作")){
			window.location.href = '${pageContext.request.contextPath}/SortingController/sortingBatchGroupPrintLine.mmx?sortingBatchGroupId='+sortingBatchGroupId+'&printType=buda';
			return true;	 
		}else{
			 return false;
		}
	}
	function openPrintPage1(userCode,sortingBatchGroupId,printType,selectedOrder){
		if( window.confirm("是否继续操作")){
			window.location.href = '${pageContext.request.contextPath}/SortingController/sortingBatchGroupPrintLine.mmx?userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType=all';
			 return true;	 
		}else{
			 return false;
		}
	}
</script>
</head>
<body>
		<div id="tb" class="datagrid-toolbar"  style="height: auto;">
	    <table>
		    <tr>
		        <td width="29%"><h4 id="batch" name="batch"></h4></td>
	            <td width="29%"><h4 id="createTime" name="createTime"></h4></td>
	            <td width="29%"><h4 id="completeTime" name="completeTime"></h4></td>
	            <td width="29%"><h4 id="statusName" name="statusName"></h4></td>
	       </tr>
	       <tr>
		        <td width="29%"><h4 id="completeCount" name="completeCount"></h4></td>
	            <td width="29%"><h4 id="sortingCount" name="sortingCount"></h4></td>
	            <td width="29%"><h4 id="noPrintCount" name="noPrintCount"></h4></td>
	            <td width="29%"></td>
		    </tr>
		    
	    </table>
			   <form id="searchForm">
			       <input name="text" type="text" value="请输入分拣波次号/订单号" size="40" width="10"onfocus="if(this.value=='请输入分拣波次号/订单号'){this.value=''}"/>&nbsp;
			       <a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchGroup();" href="javascript:void(0);">查找</a>
				</form>
	    </div>
	<table id="plDatagrid" class="easyui-datagrid" style="height: auto;width:auto; display: none;"
	        url="${pageContext.request.contextPath}/SortingController/sortingBatchGroupInfo.mmx?batchId=<%=batchId %>"  
			nowrap="false" border="false" idField="id" fit="true" fitColumns="false" title=""
			pageSize ="20"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
			 rownumbers="true" pagination="true" singleSelect="false"> 
					
		<thead>
			<tr>
				<th field="code" width="170"  align="center">分拣波次号</th>
				<th field="orderCount" width="50" align="center">订单总数</th>
				<th field="skuCount" width="50" align="center"data-options="">SKU数</th>
				<th field="productCount" width="50" align="center"data-options="">商品件数</th>
				<th field="statusName" width="80" align="center"data-options="">状态</th>
				<th field="storageName" width="80" align="center"data-options="">作业仓</th>
				<th field="staffName" width="80" align="center"data-options="">作业人</th>
				<th field="receiveDatetime" width="170" align="center"data-options="
				  formatter : function(value, rowData, rowIndex) {
								if(rowData.receiveDatetime != '' && rowData.receiveDatetime != null){
									return rowData.receiveDatetime.substring(0,19);
								}
							}">领取时间
				</th>
				<th field="completeDatetime" width="170" align="center"data-options="formatter : function(value, rowData, rowIndex) {
								if(rowData.completeDatetime != '' && rowData.completeDatetime != null){
									return rowData.completeDatetime.substring(0,19);
								}
							}">完成时间
				</th>
                                <% if(group.isFlag(592)){%>
				<th field="fff" width="120" align="center"
				data-options="formatter : function(value, rowData, rowIndex) {
                                if(rowData.status==0){
								return '<input  onClick=\'inputCode1('+ rowData.id + ')\'  type=\'button\' value=\'打单\'>';
								}else{
								return '<input  onClick=\'openPrintPage('+ rowData.id + ')\'  type=\'button\' value=\'补打\'>';
								}
							}">操作</th><%} %>
			</tr>
		</thead>
	</table>
    <table id="datagrid" ></table>
</body>

</html>