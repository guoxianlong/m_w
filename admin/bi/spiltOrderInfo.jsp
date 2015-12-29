<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;

function searchFun() {
	if (!
	checkSubmit()) {
			return false;
	}
	loadInfo();
}

	function loadInfo() {
		$.ajax({
			 url:'${pageContext.request.contextPath}/BIController/getBiSplitOrderInfoDatagrid.mmx',
					data : {
						dataType : $("#dataType").val(),
						startYear : $("#tb input[id=startYear]").val(),
						startMonth : $("#tb input[id=startMonth]").val(),
						startTime : $("#tb input[id=startTime]").datebox("getValue"),
						endTime : $("#tb input[id=endTime]").datebox("getValue")
					},
					dataType : 'json',
					cache : false,
					success : function(result) {
						try {
							var type = $("#dataType").val()=="cd" ? 1 : 2;
							var data = result;
							var columns = new Array();
							columns[0] = new Array();
							columns[0].field = 'date';
							columns[0].title = '日期';
							columns[0].width = document.body.clientWidth * 0.3;
							columns[0].align = 'center';
							columns[1] = new Array();
							columns[1].field = 'totalcount';
							columns[1].title = '总量';
							columns[1].width = document.body.clientWidth * 0.3;
							columns[1].align = 'center';
							columns[1].formatter = function(value, row,index) {
								return '<a href="javascript:void(0);" class="editbutton" onclick="toSpiltOrderDetail(\''+row.date+'\','+type+',-1)">'+value+'</a>';
							};
							var x = 2
							for (i in data[0]) {
								(function() {
									var t = i;
									columns[x] = new Array();
									columns[x].field = 'count' + i;
									columns[x].title = data[0][i];
									columns[x].width = document.body.clientWidth * 0.3;
									columns[x].align = 'center';
									columns[x].formatter = function(value, row,index) {
										return '<a href="javascript:void(0);" class="editbutton" onclick="toSpiltOrderDetail(\''+row.date+'\','+type+','+t+')">'+value+'</a>';
									};
								})();
								x++;
							}
							//处理返回结果，并显示数据表格
							var options = {
								toolbar : '#tb',
								idField : 'id',
								fit : true,
								fitColumns : true,
								striped : true,
								nowrap : true,
								loadMsg : '正在努力为您加载..',
								rownumbers : true,
								singleSelect : true,
								showFooter : true,
								columns : [ columns ]
							};
							datagrid = $("#intradayOrderCompleteDatagrid");
							datagrid.datagrid(options);//根据配置选项，生成datagrid
							datagrid.datagrid("loadData", data[1]); //载入本地json格式的数据
							datagrid.datagrid("reloadFooter", data[2]);
						} catch (e) {
						}
					}
				});
	}
function toSpiltOrderDetail(date,type,stockArea) {
	var params="date=" + date 
		+ "&stockArea=" + stockArea
		+ "&type=" + type;
	window.open("${pageContext.request.contextPath}/admin/bi/spiltOrderInfoDetail.jsp?" + params,"_blank");
}
function checkSubmit() {
	var dataType = $("#dataType").val();
	var startYear=$("#tb input[id=startYear]").val();
	var startMonth=$("#tb input[id=startMonth]").val();
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	if($.trim(startYear)!=""){
		return true;
	}
	if($.trim(startMonth)!=""){
		return true;
	}
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
	if ($.trim(startYear) != "") {
		return true;
	}
	$.messager.show({
		msg : "请输入时间区间作为查询条件！",
		title : '提示'
	});
	return false;
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
</script>
</head>
<body>
	<table id="intradayOrderCompleteDatagrid"></table> 
	<div id="tb"  style="height: auto;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>数据类型：</th>
					<td align="left">
						<select id="dataType">
						     <option value ="cd">拆单量</option>
						     <option value ="yc">越仓发货量</option>
						</select>
					</td>
					<th>年：</th>
					<td align="left"  colspan="3">
						<input id="startYear" name="startYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
					</td>
					<th>年月：</th>
					<td align="left"  colspan="3">
						<input id="startMonth" name="startMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
					</td>
					<th>日期：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
					<td>
						<mmb:permit value="2129">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>