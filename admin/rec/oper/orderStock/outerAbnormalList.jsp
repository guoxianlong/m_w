<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	$("#tb input[id=source_id]").combobox({
      	valueField:'id',
		textField:'text',
		editable: false,
		data:[{
			id:0,
			text:'请选择'
		},{
			id:4,
			text:"京东"
		}]
    });
	$('#dialogDiv').show().dialog({
		modal : true,
		title:'订单价格',
	}).dialog('close');
	datagrid = $('#outAbnormalDatagrid').datagrid({
	    url:'${pageContext.request.contextPath}/OrderStockController/getOuterAbnormalInfo.mmx',
	    toolbar : '#tb',
		idField : 'id',
		fit : true,
		fitColumns : true,
		striped : true,
		nowrap : false,
		loadMsg : '正在努力为您加载..',
		pagination : true,
		rownumbers : true,
		singleSelect : true,
		pageSize : 10,
		pageList : [ 10,20,30,40,50 ],
	    columns:[[  
			{field:'outer_order_code',title:'单据编号',width:60,align:'center'},
			{field:'sourceName',title:'单据来源',width:60,align:'center'},
			{field:'keep_time',title:'记录时间',width:60,align:'center'},
			{field:'handle_time',title:'处理时间',width:60,align:'center'},
			{field:'handle_user_name',title:'处理人',width:60,align:'center'},
			{field:'status',title:'状态',width:60,align:'center',
	        	formatter : function(value, row, index) {
	        		return value == 2 ? '已处理' : '未处理';
	        	}
			},
			{field:'reason',title:'原因',width:60,align:'center'},
			{field:'remark',title:'备注',width:60,align:'center'},
			{field:'action',title:'操作',width:60,align:'center',
	        	formatter : function(value, row, index) {
	        		if (row.status == 1) {
	        			return '<a href="javascript:void(0);" class="addbutton" onclick="addOuterOrder(\''+row.outer_order_code+'\',\'' + row.id + '\')"></a>';
	        		} else {
	        			return '';
	        		}
				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			if (data['tip']) {
				$.messager.show({
					msg : data['tip'],
					title : '提示'
				});
			}
			if (data.footer[0] ) {
				$(".addbutton").linkbutton(
					{ 
						text:'生成订单', 
						plain:true,
					}
				);
			}
		}
	}); 
});

function searchFun() {
	if (!checkSubmit()) {
		return false;
	}
	var status = new Array();
	var i = 0;
	$("input[name='status']:checked").each(function(){
	    if ($(this).attr("checked")) {
	    	status[i] = $(this).attr("value");
	    	i++;
	    }
	});
	datagrid.datagrid("load", {
		outerOrderCode:$("#tb input[id=outerOrderCode]").val(),
		sourceId:$("#tb input[id=source_id]").combobox("getValue"),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		endTime:$("#tb input[id=endTime]").datebox("getValue"),
		status:status.toString()
	});
}
function checkSubmit() {
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	if ($.trim(startTime) != "" && $.trim(endTime) != "") {
		var days = getValidateSubDays(endTime, startTime);
		if (days < 0) {
			$.messager.show({
				msg : "结束时间必须大于开始时间",
				title : '提示'
			});
			return false;
		}
		if (days>30){
			$.messager.show({
				msg : "日期时间段不得超过31天,请重新填写！",
				title : '提示'
			});
			return false;
		}
		return true;
	}
	if ($.trim(startTime) != "" && $.trim(endTime) == "") {
		$.messager.show({
			msg : "日期时间区间必须填写完整！",
			title : '提示'
		});
		return false;
	}
	if ($.trim(startTime) == "" && $.trim(endTime) != "") {
		$.messager.show({
			msg : "日期时间区间必须填写完整！",
			title : '提示'
		});
		return false;
	}
	return true;
// 	else {
// 		$.messager.show({
// 			msg : "请输入时间区间作为查询条件！",
// 			title : '提示'
// 		});
// 		return false;
// 	}
}

function getEod(){  
    var date=new Date();  
    var i_milliseconds=date.getTime();  
    i_milliseconds-=1000*60*60*24;  
    var t_date = new Date();  
    t_date.setTime(i_milliseconds);  
    var i_year = t_date.getFullYear();  
    var i_month = ("0"+(t_date.getMonth()+1)).slice(-2);  
    var i_day = ("0"+t_date.getDate()).slice(-2);  
    return i_year+"-"+i_month+"-"+i_day;  
}  

function addOuterOrder(outerOrderCode, id) {
	$.ajax({
		url : '${pageContext.request.contextPath}/OrderStockController/ajaxJudgeAddOuterOrder.mmx',
		data : {
			'outerOrderCode':outerOrderCode
		},
		type : 'post',
		success : function(result){
			try {
				var r = $.parseJSON(result);
				if (r.success&&r.obj) {
					$("#dialogDiv input[id=orderId]").val(id);
					$("#dialogDiv input[id=outerOrderCodeCode]").val(outerOrderCode);
					$("#dialogDiv").dialog("open");
   				} else if (r.success){
   					submitOrder(outerOrderCode,id,"none", "none");
				} else {
					$.messager.show({
   						title : '提示',
   						msg : decodeURI(r.msg)
   					});
				}
			} catch (e) {
				$.messager.show({
					title : '提示',
					msg : result
				});
			}
		}
	});
}
function closeFun() {
	$("#dialogDiv").dialog("close");
}
function completeOrder() {
	closeFun();
	var id= $("#dialogDiv input[id=orderId]").val();
	var outerOrderCode = $("#dialogDiv input[id=outerOrderCodeCode]").val();
	var before =  $("#dialogDiv input[id=before]").numberbox("getValue");
	var after = $("#dialogDiv input[id=after]").numberbox("getValue");
	if (before == "" || after== "") {
		$.messager.show({
			title : '提示',
			msg : "订单折扣前和折扣后价格必填"
		});
		return false;
	}
	if(before ==0 || after == 0) {
		$.messager.show({
			title : '提示',
			msg : "订单折扣前和折扣后价格必须大于0"
		});
		return false;
	}
	submitOrder(outerOrderCode,id,before,after);
}

function submitOrder(outerOrderCode,id,before,after) {
	$.messager.prompt("操作提示", "确定将该条记录生成MMB订单吗？</br>备注：", function (data) {
        if (data==undefined) {
        } else if (data != "") {
        	$.ajax({
        		url : '${pageContext.request.contextPath}/OrderStockController/addOuterOrder.mmx',
        		data : {
        			'outerOrderCode':outerOrderCode,
        			'id':id,
        			'remark':data,
        			'before':before,
        			'after':after
        		},
        		type : 'post',
        		success : function(result){
        			try {
        				var r = $.parseJSON(result);
        				if (r.success) {
        					$.messager.show({
        						title : '提示',
        						msg : "生成订单成功！"
        					});
        					$("#dialogDiv input[id=orderId]").val("");
        					$("#dialogDiv input[id=outerOrderCodeCode]").val("");
        					$("#dialogDiv input[id=before]").numberbox("setValue","");
        					$("#dialogDiv input[id=after]").numberbox("setValue","");
        					$('#outAbnormalDatagrid').datagrid("reload");
        				} else {
        					$.messager.show({
        						title : '提示',
        						msg : decodeURI(r.msg)
        					});
        				}
        			} catch (e) {
        				$.messager.show({
        					title : '提示',
        					msg : result
        				});
        			}
        		}
        	});
        } else {
        	$.messager.show({
				title : '提示',
				msg : "请填写备注"
			});
        }
    });
}
</script>
</head>
<body>
	<table id="outAbnormalDatagrid"></table> 
	<div id="tb"  style="height: auto;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>单据编号：</th>
					<td align="left">
						<input id="outerOrderCode" name="outerOrderCode" style="width: 116px;"/>
					</td>
					<th>单据来源：</th>
					<td align="left">
						<input id="source_id" name="source_id" style="width: 116px;"/>
					</td>
					<th>记录时间：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
					<td align="left"  colspan="3">
						<input type="checkbox" name="status" value="1" /> 未处理
						<input type="checkbox" name="status" value="2" /> 已处理
					</td>
					<td>
						<mmb:permit value="2202">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="dialogDiv" style="display:none">
		<input type="hidden" id="orderId" name="orderId">
		<input type="hidden" id="outerOrderCodeCode" name="outerOrderCodeCode">
		折扣前：<input id="before" name="before" class="easyui-numberbox" data-options="min:0,max:99999999"/>
		折扣后：<input id="after" name="after" class="easyui-numberbox" data-options="min:0,max:99999999"/>
		</br>
		</br>
		<div align="center">
		<a class="easyui-linkbutton"  onclick="completeOrder();" href="javascript:void(0);" >确定</a>
		<a class="easyui-linkbutton"  onclick="closeFun();" href="javascript:void(0);">取消</a>
		</div>
	</div>
</body>
</html>