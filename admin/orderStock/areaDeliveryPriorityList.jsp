<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#provinces').combotree({
		url : '${pageContext.request.contextPath}/Combobox/getSaleProvinces.mmx',
		valueField : 'id',
		textField : 'text',
		multiple: true, 
		editable : false,
		panelHeight : 'auto',
		onCheck:function(node,checked){
			showDivValue();
		}
	});
	initPriority();
	initInStockDurationDatagrid();
});

function showDivValue() {
	var divInt = 12;
	var nodes = $('#provinces').combotree('tree').tree("getChecked");
	var len = nodes.length;
	var returnVal = "";
	for (var i = 0; i<len ; i ++) {
		if (i != 0) {
			returnVal += ",";
		}
		if (i%divInt == 0 && i != 0) {
			returnVal += "<br/>";
		}
		returnVal += nodes[i].text;
	}
	$("#selectProvince").html(returnVal);
}

function initPriority() {
	$.ajax({
		url:"${pageContext.request.contextPath}/Combobox/getStockoutAvailableArea.mmx",
		dataType:"json",
		type : 'post',
		success: function(result) {
			var divInt = 5;
			var area = "";
			var priority = "";
			area += "<table>";
			priority += "<table>";
			var len = result.length;
			for (var i = 0; i < len ; i ++) {
				if (i%divInt == 0) {
					area += "<tr>";
					priority += "<tr>";
				}
				area = area + "<td><div class=\"item\"><input type=\"hidden\" name=\"areaId\" value=\"" + result[i].id +"\"/>" + result[i].text +"</div></td>";
				priority = priority + "<td>";
				priority = priority + "<table><tr><td>" + (i+1) + "</td></tr>";
				priority = priority + "<tr><td><div class=\"drop\"></div></td></tr></table>";
				priority = priority + "</td>";
				if ((i+1)%divInt == 0) {
					area += "</tr>";
					priority += "</tr>";
				}
			}
			area += "</table>";
			priority += "</table>";
			$("#area").html(area);
			$("#priority").html(priority);
			initClass();
		}
	});
}

function initClass() {
	$('.item').draggable({
        revert:true,
        proxy:'clone'
    });
    $('.drop').droppable({
        onDragEnter:function(){
            $(this).addClass('over');
        },
        onDragLeave:function(){
            $(this).removeClass('over');
        },
        onDrop:function(e,source){
            $(this).removeClass('over');
            if ($(source).hasClass('assigned')){
                var c = $(source).clone().addClass('assigned');
                $(this).empty().append(c);
                $(source).remove();
                console.info($(source).html())
                c.draggable({
                    revert:true
                });
            } else {
                var c = $(source).clone().addClass('assigned');
                $(this).empty().append(c);
                c.draggable({
                    revert:true
                });
            }
        }
    });
    $('.left').droppable({
        accept:'.assigned',
        onDragEnter:function(e,source){
            $(source).addClass('trash');
        },
        onDragLeave:function(e,source){
            $(source).removeClass('trash');
        },
        onDrop:function(e,source){
            $(source).remove();
        }
    });
}

function initInStockDurationDatagrid() {
	$('#areaDeliveryPriorityDatagrid').datagrid({
	    url:'${pageContext.request.contextPath}/OrderStockController/getAreaDeliveryPriorityList.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    columns:[[  
			{field:'province',title:'省份',width:60,align:'center'},
			{field:'priority',title:'发货优先级',width:120,align:'center'}
	    ] ],
		onLoadSuccess : function(data) {
			if(data['tip']) {
				$.messager.show({
					msg : data['tip'],
					title : '提示'
				});
			}
		}
	}); 
}

function saveFun() {
	var province = $("#provinces").combotree("getValues");
	if (!province) {
		$.messager.show({
			msg : "至少选择一个省！",
			title : '提示'
		});
		return false;
	}
	var flag = true;
	var submitAreas = ",";
	$("#priority input[name=areaId]").each(function(i,val){ 
		var t = "," + $(this).val() + ",";
		if (submitAreas.indexOf(t) != -1) {
			$.messager.show({
				msg : "每个地区只能选择一次！",
				title : '提示'
			});
			flag = false;
			$(this).parent().css("backgroundColor","red");
			return false;
		}
		submitAreas = submitAreas + $(this).val() + ",";
	});
	if (!flag) {
		return false;
	}
	if (submitAreas == ",") {
		$.messager.show({
			msg : "至少指定一个地区！",
			title : '提示'
		});
		return false;
	}
	if (flag) {
		$.ajax({
			url:"${pageContext.request.contextPath}/OrderStockController/saveAreaDeliveryPriority.mmx",
			data:{
				provinces:province.toString(),
				areas:submitAreas.substring(1, submitAreas.length-1)
			},
			dataType:"json",
			type : 'post',
			success: function(result) {
				if (result.success) {
					var msg = "";
					$.ajax({
						url:"${pageContext.request.contextPath}/admin/orderStock/initAreaDeliverPriorityAll.jsp",
						type : 'post',
						async: false,
						success: function(data) {
							msg = data;
						}
					})
					$("#provinces").combotree("clear");
					$("#selectProvince").html("");
					initPriority();
					$('#areaDeliveryPriorityDatagrid').datagrid("reload");
					$.messager.show({
						msg : msg,
						title : '提示'
					});
					return true;
				} else {
					$.messager.show({
						msg : result.msg,
						title : '提示'
					});
					return false;
				}
			}
		});
	}
}

</script>
</head>
<body>
	<table id="areaDeliveryPriorityDatagrid"></table> 
	<div id="tb"  style="height: auto;">
		<fieldset>
			<table class="tableclass">
				<tr align="center">
					<td>
						选择地区
					</td>
					<td>
						<div id="area" class="left">
				        </div>
			        </td>
		        </tr>
				<tr align="center" >
					<td>
						省份
					</td>
					<td>
						<div id="selectProvince"></div>
					</td>
				</tr>
				<tr align="center" >
					<td>
						<input id="provinces" name="provinces" style="width: 180px;"/>
					</td>
					<td>
						<div id="priority" class="right"></div>
					</td>
				</tr>
				<tr align="center" >
					<td colspan=2>
						<mmb:permit value="2182">
							<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="saveFun();">保存</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
<style type="text/css">
		.tableclass{
        	background:#E0ECFF;
        	width:100%;
        	border:1px solid ;
        	border-collapse:collapse;
        }
        .tableclass tr td{
        	border:1px solid #a1a1a1;
        }
        .tableclass td td{
        	border:0px; 
        }
       .left{
        }
        .left table{
            background:#E0ECFF;
        }
        .left td{
            background:#eee;
        }
        .right{
        }
        .right table{
            background:#E0ECFF;
            width:100%;
        }
        .right  td{
      		background:#E0ECFF;
            color:#444;
            text-align:center;
            padding:2px;
        }
        .right  td td{
            background:#E0ECFF;
            align:center;
            display:inline-block
        }
        .drop{
        	height:40px;
        	width:60px;
        	background:#fafafa;
        }
       .over{
            background:#FBEC88;
        }
        .item{
            border:1px solid #499B33;
            background:#fafafa;
            color:#444;
            width:60px;
            height:40px;
            text-align:center;
            line-height:40px;
        }
        .assigned{
            border:1px solid #BC2A4D;
        }
        .trash{
            background-color:red;
        }
        
    </style>
</html>