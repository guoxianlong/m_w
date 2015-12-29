<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>快递公司列表页面</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var quoteItem = 0;
$(function(){
	initCombobox();
	$.extend($.fn.datagrid.methods, {
		autoMergeCells : function (jq, fields) {
			return jq.each(function () {
				var target = $(this);
				if (!fields) {
					fields = target.datagrid("getColumnFields");
				}
				var rows = target.datagrid("getRows");
				var i = 0,
				j = 0,
				temp = {};
				for (i; i < rows.length; i++) {
					var row = rows[i];
					j = 0;
					for (j; j < fields.length; j++) {
						var field = fields[j];
						var tf = temp[field];
						if (!tf) {
							tf = temp[field] = {};
							tf[row[field]] = [i];
						} else {
							var tfv = tf[row[field]];
							if (tfv) {
								tfv.push(i);
							} else {
								tfv = tf[row[field]] = [i];
							}
						}
					}
				}
				$.each(temp, function (field, colunm) {
					$.each(colunm, function () {
						var group = this;
						
						if (group.length > 1) {
							var before,
							after,
							megerIndex = group[0];
							for (var i = 0; i < group.length; i++) {
								before = group[i];
								after = group[i + 1];
								if (after && (after - before) == 1) {
									continue;
								}
								var rowspan = before - megerIndex + 1;
								if (rowspan > 1) {
									target.datagrid('mergeCells', {
										index : megerIndex,
										field : field,
										rowspan : rowspan
									});
								}
								if (after && (after - before) != 1) {
									megerIndex = after;
								}
							}
						}
					});
				});
			});
		}
	});
	datagrid = $('#confDeliverDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/confDeliverController/getDeliverSendConfList.mmx',
	    queryParams: {
	    	area:$('#area').combobox("getValue")
	    },
	    toolbar : '#tb',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    columns:[[  
			{field:'areaName',title:'地区',width:40,align:'center'},
			{field:'provinceName',title:'省',width:60,align:'center'},
			{field:'deliverName',title:'快递公司',width:60,align:'center'},
			{field:'countLimit',title:'单量限制',width:60,align:'center'},
			{field:'priority',title:'优先级',width:60,align:'center'},
			{field:'wholeAreaName',title:'是否全境',width:60,align:'center'},
			{field:'provinceId',title:'操作',width:60,align:'center',
	        	formatter : function(value, row, index) {
	        		return '<a href="javascript:void(0);" class="editbutton" onclick="editInfo(' + index + ')"></a>';
				}
			}
	    ] ],
		onLoadSuccess : function(data) {
			$(".editbutton").linkbutton(
				{ 
					text:"编辑",
					plain:true,
					iconCls:'icon-edit'
				}
			);
			$(this).datagrid("autoMergeCells",['areaName','provinceName','provinceId']);
			if(data["tip"]) {
				$.messager.show({
					title:'提示',
					msg:data["tip"]
				});
			}
		}
	}); 
});

function initCombobox() {
	$('#area').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getStockoutAvailableArea.mmx',
      	valueField:'id',
		textField:'text',
		editable : false
    });
	$('#area').combobox("setValue", 0);
	$('#province').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getProvincesCombobox.mmx',
      	valueField:'id',
		textField:'text',
		editable : false
    });
}

function searchFun() {
	var area = $("#area").combobox("getValue");
	datagrid.datagrid("load", {
		area:area,
		provinceId:$("#province").combobox("getValue")
	});
}

function editInfo(index) {
	$('<div/>').dialog({
		href : '${pageContext.request.contextPath}/admin/tms/confDeliver.jsp',
		width : 1100,
		height : 520,
		modal : true,
		title : '分配快递公司',
		buttons : [ {
			text:"保存",
			handler : function() {
				var d = $(this).closest('.window-body');
				var flag = false;
				var prioritytemp = ",";
				var deliverIdtemp = ",";
				var maxPriority = -1;
				var maxCountLimitId = -1;
				$("#confDeliver input[name=deliverId]").each(
					function(index) {
						//快递公司不能重复
						if ($("#confDeliver input[name=deliverId]").eq(index).val() == "-1") {
							$.messager.show({
								title : '提示',
								msg : '必须选择快递公司！'
							});
							flag = true;
							return false;
						}
						if (deliverIdtemp.indexOf("," + $("#confDeliver input[name=deliverId]").eq(index).val() + ",") >= 0) {
							$.messager.show({
								title : '提示',
								msg : '快递公司不能重复！'
							});
							flag = true;
							return false;
						}
						deliverIdtemp = deliverIdtemp +$("#confDeliver input[name=deliverId]").eq(index).val() +",";
						//优先级不能重复
						if ($("#confDeliver input[name=priority]").eq(index).val() == "") {
							$.messager.show({
								title : '提示',
								msg : '优先级不能为空！'
							});
							flag = true;
							return false;
						}
						if (prioritytemp.indexOf("," + $("#confDeliver input[name=priority]").eq(index).val() + ",") >= 0) {
							$.messager.show({
								title : '提示',
								msg : '优先级不能重复！'
							});
							flag = true;
							return false;
						}
						prioritytemp = prioritytemp +$("#confDeliver input[name=priority]").eq(index).val() +",";
					}
				);
				if (flag) {
					return false;
				}
				var deliverIds = getInputValueByName($("#confDeliver input[name='deliverId']"));
				var countLimits = getInputValueByName($("#confDeliver input[name='countLimit']"));;
				var prioritys = getInputValueByName($("#confDeliver input[name='priority']"));
				var wholeAreas =  getInputValueByName($("#confDeliver input[name='wholeArea']"));
				$.ajax({
					url:"${pageContext.request.contextPath}/confDeliverController/editDeliverSendConf.mmx",
					data:{
						area:$("#areaId").val(),
						provinceId:$("#provinceId").val(),
						deliverIds:deliverIds,
						countLimits:countLimits,
						prioritys:prioritys,
						wholeAreas:wholeAreas
					},
					dataType:"json",
					success: function(result) {
						if (!result.success) {
							$.messager.show({
								title : '提示',
								msg : result.msg
							});
							return false;
						} else {
							var msg = "";
							$.ajax({
								url:"${pageContext.request.contextPath}/admin/orderStock/initDeliverSendConfAll.jsp",
								type : 'post',
								async: false,
								success: function(data) {
									msg = data;
								}
							})
							$.messager.show({
								msg : msg,
								title : '提示'
							});
							d.dialog('destroy');
							quoteItem = 0;
							datagrid.datagrid("reload");
						}
					}
				})
			}
		},{
			text:"关闭",
			handler : function() {
				var d = $(this).closest('.window-body');
				d.dialog('destroy');
				quoteItem = 0;
			}
		} ],
		onClose : function() {
			$(this).dialog('destroy');
			quoteItem = 0;
		},
		onLoad : function() {
			initConfDeliver(index);
		}
	});
}

function initConfDeliver(index) {
	datagrid.datagrid("selectRow", index);
	var row = datagrid.datagrid("getSelected");
	$.ajax({
		url:'${pageContext.request.contextPath}/confDeliverController/getDeliverSendConfList.mmx',
		type:"post",
		data: {
			area:row.areaId,
			provinceId:row.provinceId
		},
		dataType:"json",
		success: function(result) {
			if(result["tip"]) {
				$.messager.show({
					title:'提示',
					msg:data["tip"]
				});
			} else {
				for (var t in result["rows"]) {
					if (result["rows"][t].deliverId) {
						addQuoteHtml(result["rows"][t].deliverId,result["rows"][t].countLimit,result["rows"][t].priority,result["rows"][t].wholeArea);
					} else {
						addQuoteHtml(-1,0,1,1);
					}
					if (t == 0) {
						$("#areaId").val(result["rows"][t].areaId);
						$("#areaName").val(result["rows"][t].areaName);
						$("#provinceId").val(result["rows"][t].provinceId);
						$("#provinceName").val(result["rows"][t].provinceName);
					}
				}
			}
		}
	})
}

function addQuoteHtml(deliverId,countLimit,priority,wholeArea) {
	quoteItem += 1;
	var tr = $("#addItem");
	var addItem = "";
	addItem+=('<tr align="center">');
	addItem+=('<th>快递公司：</th>');
	addItem+=('<td align="left" >');
	addItem+=('<input id="deliverId'+ quoteItem +'" class="easyui-combobox" name="deliverId" style="width: 150px;"/>');
	addItem+=('</td>');
	addItem+=('<th>单量限制：</th>');
	addItem+=('<td align="left">');
	addItem+=('<input id="countLimit'+quoteItem+'" name="countLimit" style="width: 116px;" class="xnumberbox"/>&nbsp;');
	addItem+=('</td>');
	addItem+=('<th>优先级：</th>');
	addItem+=('<td align="left">');
	addItem+=('<input id="priority'+quoteItem+'" name="priority" style="width: 116px;" class="prioritynumberbox"/>&nbsp;');
	addItem+=('</td>');
	addItem+=('<th>是否全境：</th>');
	addItem+=('<td align="left">');
	addItem+=('<input id="wholeArea'+quoteItem+'" name="wholeArea" style="width: 116px;" class="easyui-combobox"/>&nbsp;');
	addItem+=('<a class="removeItemClass" onclick="removeQuoteHtml('+quoteItem+');" href="javascript:void(0);"></a>');
	addItem+=('</td>');
	addItem+=("</tr>");
	tr.before(addItem);
	
	$("#deliverId" + quoteItem).combobox({
      	url : '${pageContext.request.contextPath}/SalesReturnController/getDeliverJSON.mmx?deliverId=' + deliverId,
      	valueField:'id',
		textField:'name',
		editable : false
    });
	$("#wholeArea" + quoteItem).combobox({
      	url : '${pageContext.request.contextPath}/confDeliverController/getWholeArea.mmx?wholeArea=' + wholeArea,
      	valueField:'id',
		textField:'text',
		editable : false
    });
	$("#countLimit" + quoteItem).val(countLimit);
	$("#priority" + quoteItem).val(priority);
	$(".removeItemClass").linkbutton(
		{ 
			plain:true,
			iconCls:'icon-remove'
		}
	);
	$(".xnumberbox").numberbox (
		{
			min:0,
			max:999999
		}
	)
	
	$(".prioritynumberbox").numberbox (
		{
			min:1,
			max:999
		}
	)
}

function removeQuoteHtml(index) {
	$("#deliverId"+index).parent().parent().remove();
}

function getInputValueByName(items){
	var info = '';
	for (var i = 0; i < items.length; i++) {
     // 如果i+1等于选项长度则取值后添加空字符串，否则为逗号
     info = (info + items.get(i).value) + (((i + 1)== items.length) ? '':',');
	}
	return info;
}
</script>
</head>
<body>
	<table id="confDeliverDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>地区：</th>
					<td align="left">
						<input id="area" name="area" style="width: 150px;"/>
					</td>
					<th>省：</th>
					<td align="left">
						<input id="province" name="province" style="width: 150px;"/>
					</td>
					<td>
					 	<a href="javascript:void(0);" class="easyui-linkbutton"  onclick="searchFun();" iconCls="icon-search" plain="true">查询</a>
					 </td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>
