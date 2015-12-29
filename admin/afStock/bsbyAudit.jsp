<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	//初始化datagrid
	var id=${param.opid};
	initbsbyProductListDataGrid(id);
	initTbInfo(id);
	if ('${param.lookup}' == '') {
		auditPower(id);
	}
	$("#auditDiv input[id=bsbyId]").val(id);
	$("#finAuditDiv input[id=bsbyId]").val(id);
});

function initbsbyProductListDataGrid (id) {
	$('#bsbyProductListDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfBsByProductList.mmx',
	    queryParams: {
	    	id : id
	    },
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
	    pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[  
			{field:'productName',title:'商品名称',width:60,align:'center'},
			{field:'productCode',title:'商品编号',width:60,align:'center'},
			{field:'price',title:'报损单价含税（不含税）',width:60,align:'center'},
			{field:'afterSaleDetectProductCode',title:'售后处理单号',width:60,align:'center'},
			{field:'afterSaleDetectProductStatusName',title:'售后处理单状态',width:60,align:'center'},
			{field:'imei',title:'IMEI码',width:60,align:'center'},
			{field:'wholeCode',title:'货位号',width:100,align:'center'},
			{field:'action',title:'操作',width:60,align:'center',
  	        	formatter : function(value, row, index) {
          			return '<a class="print" href="${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductCodeForOnePrint.jsp?code='+row.afterSaleDetectProductCode+'&id='+id+'"></a>';
  				}
  			}
	    ] ],
		onLoadSuccess : function(data) {
			$(".print").linkbutton({ 
				text:'打印处理单号' 
			});
		}
	}); 
};

function initTbInfo(id) {
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getBsbyInfo.mmx',
		data : {'id':id},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					$("#bsbyInfo").html(r.obj);
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function auditPower(id) {
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getBsbyAudit.mmx',
		data : {'id':id},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					if (r.obj.auditFlag) {
						$("#auditDiv").show();
						$("#finAuditDiv").hide();
					} else if (r.obj.finAuditFlag) {
						$("#auditDiv").hide();
						$("#finAuditDiv").show();
					} else {
						$("#auditDiv").hide();
						$("#finAuditDiv").hide();
					}
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function auditFun(status) {
	var remark;
	remark = $.trim($("#auditDiv input[id=examineSuggestion]").val());
	if (remark == "") {
		$.messager.show({
			title : '提示',
			msg :  "必须填写财务审核意见"
		});
		return false;
	}
	if (remark.length > 50) {
		$.messager.show({
			title : '提示',
			msg : "财务审核意见长度不能大于50"
		});
		return false;
	}
	var id = $("#auditDiv input[id=bsbyId]").val();
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/auditBsby.mmx',
		data : {'id':id,"status":status,"remark":remark},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					window.location.href = '${pageContext.request.contextPath}/admin/afStock/bsbyList.jsp';
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}

function finAuditFun(status) {
	var remark;
	remark = $.trim($("#finAuditDiv input[id=finAuditRemark]").val());
	if (remark == "") {
		$.messager.show({
			title : '提示',
			msg : "必须填写运营审核意见"
		});
		return false;
	}
	if (remark.length > 50) {
		$.messager.show({
			title : '提示',
			msg : "运营审核意见长度不能大于50"
		});
		return false;
	}
	var id = $("#auditDiv input[id=bsbyId]").val();
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/finAuditBsby.mmx',
		data : {'id':id,"status":status,"remark":remark},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success) {
					window.location.href = '${pageContext.request.contextPath}/admin/afStock/bsbyList.jsp';
				} else {
					$.messager.show({
						title : '提示',
						msg : decodeURI(r.msg)
					});
				}
			} catch (e) {
				$.messager.alert('提示', result);
			}
		}
	});
}
</script>
</head>
<body>
	<table id="bsbyProductListDataGrid"></table>
	<div id="tb" style="padding:3px;height: auto;">
		<div id="bsbyInfo"></div>
		<div id="auditDiv" style="display:none">
			<form id="auditForm">
				<input type="hidden" id="bsbyId"/>
				<table>
					<tr>
						<th>财务审核意见：</th>
						<td>
							<input class="easyui-validatebox" id="examineSuggestion" name="examineSuggestion" data-options="required:true,validType:['length[1,50]']"/>
						</td>
						<td>
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="auditFun(1);" href="javascript:void(0);">财务审核通过</a>
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-cancel',plain:true" onclick="auditFun(0);" href="javascript:void(0);">财务审核不通过</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div id="finAuditDiv" style="display:none">
			<form id="finAuditForm">
				<input type="hidden" id="bsbyId"/>
				<table>
					<tr>
						<th>运营审核意见：</th>
						<td>
							<input class="easyui-validatebox" id="finAuditRemark" name="finAuditRemark" data-options="required:true,validType:['length[1,50]']"/>
						</td>
						<td>
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="finAuditFun(1);" href="javascript:void(0);">运营审核通过</a>
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-cancel',plain:true" onclick="finAuditFun(0);" href="javascript:void(0);">运营审核不通过</a>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>