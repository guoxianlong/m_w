<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*,adultadmin.bean.stock.*,adultadmin.bean.bybs.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="net.sf.json.JSONObject"%>
<%@ page import="mmb.rec.sys.easyui.*" %>
<%
	int rightMark = 0;
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	if( group.isFlag(760) ) {
		rightMark = 1;
	}
	ClaimsVerificationBean cvBean = (ClaimsVerificationBean) request.getAttribute("claimsVerificationBean");
	voOrder vorder = (voOrder) request.getAttribute("userOrder");
 	int userid = user.getId();
 	String orderPrice = (String)request.getAttribute("orderPrice");
 	String skuPrice =  (String)request.getAttribute("skuPrice");
 	String mailPrice = (String)request.getAttribute("mailPrice");
 	Map mapdata = (Map)request.getAttribute("data");
	EasyuiDataGridJson  rows = (EasyuiDataGridJson)mapdata.get("rows");
 	String data = JSONObject.fromObject(rows).toString();
 	EasyuiDataGridJson bsbyData = new EasyuiDataGridJson();
 	bsbyData.setTotal((long)cvBean.getBsbyList().size());
 	bsbyData.setRows(cvBean.getBsbyList());
 	String bsbyRows = JSONObject.fromObject(bsbyData).toString();
%>
<script  type="text/javascript">
var claimsVerificationInfoForm;
$(function() {
	initCurrentClaimsVerificationDataGrid();
	$("#currentClaimsVerificationDataGrid").datagrid("loadData", <%=data%>);
	
	initbsbyDataGrid();
	$("#bsbyDataGrid").datagrid("loadData", <%=bsbyRows%>);
	if ($("#bsbyDataGrid").datagrid("getRows").length <= 0) {
		$("#bsbyDiv").hide();
	}
});

function saveInfo() {
	if(!checkSubmit2()) {
		return false;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/SalesReturnController/addClaimsInfo.mmx',
		data : "id="+$("#claimsVerificationInfoForm [name=id]").val()+"&cvType="+$("#claimsVerificationInfoForm [name=cvType]:checked").val()+"&cvIsTicket="+$("#claimsVerificationInfoForm [name=cvIsTicket]:checked").val()+"&cvPrice="+$("#cvPrice").numberbox("getValue"),
		dataType : 'text',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.result == 'success') {
					var idvalue = $("#id_id").val();
					$.messager.alert("提示", d.tip, "info",function() {$("#closeEditButton").click(); editClaimsVerification(idvalue);});
				} else {
					$.messager.alert("错误", d.tip, "info");
				}
			} catch (e) {
				$.messager.alert("错误", "异常", "info");
			}
		}
	});
}

function initCurrentClaimsVerificationDataGrid() {
	var options = {
   			collapsible:true,
   			fit : true,
   			fitColumns : true,
   			border : true,
   			idField :'productId',
   			singleSelect : true,
   			striped : true,
   			nowrap : false,
   			columns : [ [{
   	 			field : 'productId',
   				title : '产品id',
   				align : 'center',
   				hidden : true
   			}, {
   	 			field : 'currentOrderCode',
   				title : '订单号',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'packageCode',
   				title : '包裹单号',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'deliverCompany',
   				title : '快递公司',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'oriName',
   				title : '原名称',
   				width : 200,
   				align : 'center'
   			}, {
   				field : 'productLine',
   				title : '产品线',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'productCode',
   				title : '产品编号',
   				width : 120,
   				align : 'center'
   			}, {
   				field : 'count',
   				title : '数量',
   				width : 80,
   				align : 'center'
   			}, {
   				field : 'isExist',
   				title : '有无实物',
   				width : 80,
   				align : 'center',
   				formatter : function(value, row, index) {
   					if (value == '1') {
   						return "有";
   					} else {
   						return "无";
   					}
   				}
   			} ] ],
   			onLoadSuccess : function(data) {
   				
   				var fields = [{
   					field : 'currentOrderCode'
   				},{
   					field : 'packageCode'
   				},{
   					field : 'deliverCompany'
   				}]
				var merges = [{
					index: 0,
					rowspan: data.rows.length
				}];
				for(var i=0; i<fields.length; i++){
					$(this).datagrid('mergeCells',{
						index: merges[0].index,
						field: fields[i].field,
						rowspan: merges[0].rowspan
					});
  				}
			}
   		}
	$("#currentClaimsVerificationDataGrid").datagrid(options);
}

function initbsbyDataGrid() {
	var options = {
   			collapsible:true,
   			fit : true,
   			fitColumns : true,
   			border : true,
   			idField :'productId',
   			singleSelect : true,
   			striped : true,
   			nowrap : false,
   			columns : [ [{
   				field : 'receipts_number',
   				title : '报损单号',
   				width : 140,
   				align : 'center',
   				formatter : function(value, row, index) {
   					if (row.if_del == '1') {
   						return value;
   					} else {
   						if (row.lookup == "1") {
							return '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?lookup=1&opid='+row.id+'" target="_blank">'+value+'</a>';
						} else {
							return '<a href="<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid='+row.id+'" target="_blank">'+value+'</a>';
						}
   					}
   				}
   			}, {
   				field : 'current_type_name',
   				title : '状态',
   				width : 80,
   				align : 'center',
   				formatter : function(value, row, index) {
   					if (row.if_del == '1') {
   						return "已删除";
   					} else {
   						return value;
   					}
   				}
   			} ] ]
   		}
	$("#bsbyDataGrid").datagrid(options);
}

	function auditClaims(id, yesOrNo) {
		if ($("#remindSave").html() != "" && $("#remindSave").html() != null) {
			$.messager.alert("提示", "修改了理赔信息，请先保存！", "info");
			return;
		}
		var target = '<%= cvBean.getPrice()%>';
		if( parseInt(target) <= 0 ) {
			$.messager.alert("提示", "当前的理赔金额为0，请输入理赔金额并保存！", "info");
			return;
		}
		$.messager.confirm('确认', '你确认要审核理赔单么？', function(r) {
			if (r) {
				$.ajax({
					url : '${pageContext.request.contextPath}/SalesReturnController/auditClaimsVerification.mmx',
					data : "id="+id+"&yesno="+yesOrNo,
					dataType : 'text',
					success : function(data) {
						try {
							var d = $.parseJSON(data);
							if (d.result == 'success') {
								var idvalue = $("#id_id").val();
								$.messager.alert("提示", d.tip, "info", function() {$("#closeEditButton").click(); editClaimsVerification(idvalue);});
							} else {
								$.messager.alert("错误", d.tip, "info");
							}
						} catch (e) {
							$.messager.alert("错误", "异常", "info");
						}
					}
				});
			}
		});
	}
	
	function completeClaims(id) {
		if ($("#remindSave").html() != "" && $("#remindSave").html() != null) {
			$.messager.alert("提示", "修改了理赔信息，请先保存！", "info");
			return;
		}
		var target = '<%= cvBean.getPrice()%>';
		if( parseInt(target) <= 0 ) {
			$.messager.alert("提示", "当前的理赔金额为0，请输入理赔金额并保存！", "info");
			return;
		}
		$.messager.confirm('确认', '你确认要完成理赔单么？', function(r) {
			if (r) {
				$.ajax({
					url : '${pageContext.request.contextPath}/SalesReturnController/completeClaimsVerification.mmx',
					data : "id="+id,
					dataType : 'text',
					success : function(data) {
						try {
							var d = $.parseJSON(data);
							if (d.result == 'success') {
								var idvalue = $("#id_id").val();
								$.messager.alert("提示", d.tip, "info", function() {$("#closeEditButton").click(); editClaimsVerification(idvalue);});
							} else {
								$.messager.alert("错误", d.tip, "info");
							}
						} catch (e) {
							$.messager.alert("错误", "异常", "info");
						}
					}
				});
			}
		});
	}
	
	function changePrice(mark) {
		if( mark == 0 ) {
			$("#cvPrice").numberbox("setValue", "<%= orderPrice%>");
		} else if ( mark == 1 ) {
			$("#cvPrice").numberbox("setValue", "<%= skuPrice%>");
		} else if( mark == 2 ) {
			$("#cvPrice").numberbox("setValue", "<%= mailPrice%>");
		}
		
		if( mark != '<%= cvBean.getType() %>' || $("#cvPrice").numberbox("getValue") != parseInt('<%= cvBean.getPrice()%>')) {
			remindSave();
		} else {
			cleanRemind();
		}
	}
	
	function checkSubmit2() {
		if( !($("#cvType0").attr("checked") || $("#cvType1").attr("checked") || $("#cvType2").attr("checked")) ) {
			$.messager.alert("提示", "请选择理赔方式！", "info");
			return false;
		}
		if( $("#cvPrice").val() == "" )  {
			$.messager.alert("提示", "请填写或选择价格！", "info");
			return false;
		}
		if( !($("#cvIsTicket0").attr("checked") || $("#cvIsTicket1").attr("checked")) ) {
			$.messager.alert("提示", "请选择是开具发票！", "info");
			return false;
		}
		return true;
	}
	function checkRight() {
		<%
			if( rightMark == 0 ) {
			
		%>
			$.messager.alert("提示", "您没有理赔金额修改的权限！", "info", function() {$("#cvPrice").blur();});
		<%
			}
		%>
	}
	
	function checkFloat(obj) {
		var number = obj.value;
		if( parseInt(number) != parseInt('<%= cvBean.getPrice()%>') ){
			remindSave();
		} else {
			cleanRemind();
		}
	}
	function changeIsTicket(mark) {
		if( mark != '<%= cvBean.getIsTicket()%>' ) {
			remindSave();
		} else {
			cleanRemind();
		}
	}
	function remindSave() {
		$("#remindSave").html("<font color='red'>当前理赔信息有未保存的编辑!</font>");
	}
	function cleanRemind() {
		$("#remindSave").html("");
	}
</script>
<div>
<br/>
<br/>
<input type="hidden" name="id" id="id_id" value="<%= cvBean.getId()%>" />
<div style="width:80%;margin-left:10%;">
理赔单号： <%= cvBean.getCode()%>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
库地区： <%= StringUtil.convertNull((String)(ProductStockBean.areaMap.get(new Integer(cvBean.getWareArea()))))%>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
状态：<%= cvBean.getStatusName()%> <%= cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_CONFIRM ? "<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-ok',plain:true\" onclick=\"auditClaims("+cvBean.getId()+", 1);\">审核通过</a>&nbsp;&nbsp;&nbsp;<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-cancel',plain:true\"   onclick=\"auditClaims("+cvBean.getId()+", 0);\">审核不通过</a>" : cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT_FAIL ? "" : cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT ? "<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-ok',plain:true\"   onclick=\"completeClaims("+cvBean.getId()+", 1);\">确认完成</a>" : ""%>
<br/>
<br/>
</div>
<div id="currentDiv" data-options="border:false" style="height:140px;width:84%;overflow: hidden;margin-left:8%;">
	<table id="currentClaimsVerificationDataGrid"></table>
</div>
<div align="left" style="margin-left:8%;">
	<br/>
	<br/>
	<%
		if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_COMPLETE ) {
	%>
	<fieldset style="width:64%;">
	<legend>理赔信息</legend>
	<form id="claimsVerificationInfoForm" >
		<input type="hidden" value="<%= cvBean.getId()%>" name="id" />
		理赔方式：   <input type="radio" name="cvType" id="cvType0" value="0" <%= cvBean.getType() == 0 ? "checked" : ""%> onclick="changePrice(0);"/>整单理赔&nbsp;&nbsp;&nbsp;
					<input type="radio" name="cvType" id="cvType1" value="1" <%= cvBean.getType() == 1 ? "checked" : ""%> onclick="changePrice(1);"/>按sku理赔&nbsp;&nbsp;&nbsp;
					<input type="radio" name="cvType" id="cvType2" value="2" <%= cvBean.getType() == 2 ? "checked" : ""%> onclick="changePrice(2);"/>运费3倍理赔
				<br/>
		理赔金额：<input class="easyui-numberbox"  data-options="min:0,max:9999999999.99,precision:2,required:true" value="<%= cvBean.getPrice()%>" name="cvPrice" id="cvPrice" onfocus="checkRight();" onchange="checkFloat(this);remindSave();"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			 是否需要开具发票: <input type="radio" name="cvIsTicket" id="cvIsTicket1" value="1" <%= cvBean.getIsTicket() == 1 ? "checked" : ""%> onclick="changeIsTicket(1);"/>是&nbsp;&nbsp;&nbsp;
							<input type="radio" name="cvIsTicket" id="cvIsTicket0" value="0" <%= cvBean.getIsTicket() == 0 ? "checked" : ""%> onclick="changeIsTicket(0);"/>否&nbsp;&nbsp;&nbsp;
							<br/>
		<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="saveInfo();">保存</a>
		&nbsp;&nbsp;&nbsp;
		<span id="remindSave" ></span>
	</form>
	</fieldset>
	<%
		}else{ 
	%>
	<fieldset style="width:38%;">
	<legend>理赔信息</legend>
		理赔方式：<%= cvBean.getType() == 0 ? "整单理赔" : cvBean.getType() == 1 ? "按sku理赔" : cvBean.getType() == 2 ? "运费3倍理赔" : ""%>
		<br/>
		理赔金额：<%= cvBean.getPrice()%>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		是否需要开具发票：<%= cvBean.getIsTicket() == 0 ? "否" : cvBean.getIsTicket() == 1 ? "是" : ""%>
	</fieldset>
	<%
		}
	%>
	<br/>
	<br/>
</div>	
<div align="left" style="margin-left:9%;">
	<br/><br/>
	<div id="bsbyDiv" data-options="border:false" style="height:140px;width:30%;overflow: hidden;">
	对应的报损单号<br/>
		<table id="bsbyDataGrid"></table>
	</div>
</div>
</div>
