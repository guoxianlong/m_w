<%@ page language="java" pageEncoding="UTF-8"%>
<%
String msg = (String)request.getAttribute("msg");
if(msg != null){
	%>
	<script type="text/javascript">
	<!--
	alert("<%=msg %>");
	history.back();
	//-->
	</script>
	<%
	return;
}
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>
  <c:choose>
   <c:when test="${empty bsbyOperationnote }">新建报损报溢单</c:when>
   <c:otherwise>编辑报损报溢单</c:otherwise>
  </c:choose>
</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">

$(function(){
	$("#saveForm input[name='reason']").keypress(function(e) {
	    var key = window.event ? e.keyCode : e.which;
	    if (key.toString() == "13") {
	        return false;
	    }
	});
	
	$('#bsbyEditTab').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSessionAfterSaleBsbyInfo.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    columns:[[  
	  			{field:'productName',title:'商品名称',width:60,align:'center'},
	  			{field:'productCode',title:'商品编号',width:100,align:'center'},
	  			{field:'afterSaleDetectProductCode',title:'售后处理单号',width:100,align:'center'},
	  			{field:'afterSaleDetectProductStatusName',title:'售后处理单状态 ',width:100,align:'center'},
	  			{field:'imei',title:'IMEI码 ',width:100,align:'center'},
	  			{field:'wholeCode',title:'货位号',width:100,align:'center'},
	  	        {field:'action',title:'操作',width:60,align:'center',
	  	        	formatter : function(value, row, index) {
	          			return '<a href="javascript:del('+index+');" class="editDetectTypeDetail" iconCls="icon-remove"></a>';
	  				}
	  			}
	  	    ] ],
		onLoadSuccess : function(data) {
			//改变datagrid中按钮的class
			$(".editDetectTypeDetail").linkbutton(
				{ 
					text:'删除'
				}
			);
		}
	});
	
	var stockType = $('#stockType');
	if(stockType != null){
		stockType.combobox({
			url : '${pageContext.request.contextPath}/Combobox/getAfBsbyStockType.mmx',
	      	valueField:'id',
			textField:'text',
			editable:false,
			required:true
		});
	}
	
	var bsbyType = $('#bsbyType');
	if(bsbyType != null){
		bsbyType.combobox({
			url : '${pageContext.request.contextPath}/Combobox/getAfBsbyType.mmx',
	      	valueField:'id',
			textField:'text',
			editable:false,
			required:true,
			onSelect : function(record){
				$('#reason').combobox('reload','${pageContext.request.contextPath}/Combobox/getReason.mmx?type='+record.id+'');  
			}
		});
	}
	
	$("#reason").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getReason.mmx',
	    valueField:'text',
		textField:'text',
		editable:false,
		required:true
	});
	if(${empty bsbyOperationnote }==false){
		var type = ${bsbyOperationnote.type }+"";
		var remark = "${bsbyOperationnote.remark }";
		$("#reason").combobox({
			url : '${pageContext.request.contextPath}/Combobox/getReason.mmx?type='+type+'',
		    valueField:'text',
			textField:'text',
			editable:false,
			required:true,
			onLoadSuccess: function (data) {
	             if (data) {
	            	 $("#reason").combobox('setValue',remark);
	             }
	        }
		});
	}
	
});

function del(index){
	if (index != undefined) {
		$('#bsbyEditTab').datagrid('selectRow', index);
	}
	var row = $('#bsbyEditTab').datagrid('getSelected');
	
	$.ajax({
		url:'${pageContext.request.contextPath}/admin/AfStock/remSessionAfterSaleBsbyInfo.mmx',
		data:{code:row.afterSaleDetectProductCode},
		cache:false,
		dataType:'text',
		type:"post",
		success:function(result){
			var r = $.parseJSON(result);
			if(r.success){
				$('#bsbyEditTab').datagrid('reload');
			}
			$.messager.show({
				title:'提示',
				msg:r.msg,
				timeout:3000,
				showType:'slide'
			});
		}
	});
	
}


function saveFun() {
	$('#saveForm').form('submit' , {
		url : '${pageContext.request.contextPath}/admin/AfStock/afterSaleBsbyInfo.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			if (!isValid) {
				return false;
			}
			var reason = $('#saveForm textarea[name=codes]').val();
			if ($.trim(reason) == "") {
				$.messager.show({
					title : '提示',
					msg : "处理单号不能为空！"
				});
				return false;
			}
		},
		success : function(result) {
			try {
				if (result != null ) {
					var r = $.parseJSON(result);
					if (r.success) {
						resetForm();
						$('#bsbyEditTab').datagrid('reload');
					}
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

function resetForm() {
	$('#saveForm textarea[id=codes]').val("");
}

function editAfterSaleBsby(){
	$('#saveForm').form('submit' , {
		url : '${pageContext.request.contextPath}/admin/AfStock/editAfterSaleBsby.mmx',
		onSubmit : function() {
			var isValid = $(this).form('validate');
			if (!isValid) {
				return false;
			}
			var reason = $.trim($('#saveForm input[name=reason]').val());
			if (reason == "") {
				$.messager.show({
					title : '提示',
					msg : "报损报溢原因不能为空！"
				});
				return false;
			}
			if (reason.length > 50) {
				$.messager.show({
					title : '提示',
					msg : "报损报溢原因限制50长度"
				});
				return false;
			}
		},
		success : function(result) {
			try {
				if (result != null ) {
					var r = $.parseJSON(result);
					if (r.success) {
						resetForm();
						$('#bsbyEditTab').datagrid('reload');
					}
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
<table id="bsbyEditTab"></table>
<div id="tb" style="height: auto;">
<form id="saveForm" method="post">
<input type="hidden" name="strId" value="${bsbyOperationnote.id }"/>

<fieldset>
<legend>
  <c:choose>
   <c:when test="${empty bsbyOperationnote }">新建报损报溢单</c:when>
   <c:otherwise>编辑报损报溢单</c:otherwise>
  </c:choose>
</legend>
<table class="tableForm">

<c:choose>
 <c:when test="${empty bsbyOperationnote }">
 <tr>
  <td>单据类型</td>
  <td>
   <input id="bsbyType" name="bsbyType" style="width: 116px;"/>
  </td>
  <td>库类型</td>
  <td>
   <input id="stockType" name="stockType" style="width: 116px;"/>
  </td>
 </tr>
 </c:when>
 <c:otherwise>
 <tr>
  <td>单据类型</td>
  <td>
   ${bsbyOperationnote.typeName}
   <input type="hidden" name="bsbyType" value="${bsbyOperationnote.type}"/>
  </td>
  <td>库类型</td>
  <td>
   ${bsbyOperationnote.warehouse_type_name }
   <input  type="hidden" name="stockType" value="${bsbyOperationnote.warehouse_type }"/>
  </td>
 </tr>
 </c:otherwise>
</c:choose>

 
 <tr>
  <td>报损报溢原因</td>
  <td colspan="3">
   <select id="reason" class="easyui-combobox" name="reason" style="width:200px;">
   		<option value="aa">${bsbyOperationnote.remark}</option>  
   </select>
  </td>
 </tr>
 
 <tr>
  <td colspan="4">添加处理单号</td>
 </tr>
 <tr>
  <td colspan="4">
   <textarea id="codes" name="codes" rows="4" cols="40"></textarea>
  </td>
 </tr>
 <tr>
  <td colspan="4" align="center">
  <c:choose>
   <c:when test="${empty bsbyOperationnote }">
	<a class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true" href="javascript:saveFun();">添加</a>
   </c:when>
   <c:otherwise>
    <a class="easyui-linkbutton"  data-options="iconCls:'icon-edit',plain:true" href="javascript:saveFun();">编辑</a>
   </c:otherwise>
  </c:choose>
  </td>
 </tr>
</table>
</fieldset>
</form>
<h3>已添加选项：</h3>
<div id="toolbar">
 <a href="javascript:editAfterSaleBsby();" class="easyui-linkbutton" iconCls="icon-ok" plain="true">提交审核</a>
</div>
</div>
</body>
</html>