<%@ page language="java" pageEncoding="UTF-8"%>
<%@page	import="adultadmin.bean.stock.ProductStockBean"%>
<%@page	import="java.util.Map,adultadmin.action.vo.*,adultadmin.bean.*,adultadmin.bean.cargo.*"%>
<%@page import="adultadmin.bean.bybs.*"%>
<%@page import="adultadmin.bean.bybs.BsbyProductBean" %>
<%@page import="java.util.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@ page import="net.sf.json.JSONObject"%>
<%@ page import="mmb.rec.sys.easyui.*" %>
<%
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	CargoInfoAreaBean area = (CargoInfoAreaBean)request.getAttribute("stockArea");
	String title = (String)request.getAttribute("title");
	String buttonString = (String)request.getAttribute("buttonString");
	String buttonString1 = (String)request.getAttribute("buttonString1");
	String type = (String)request.getAttribute("type");
	String type1 = (String)request.getAttribute("type1");
	BsbyOperationnoteBean bsbyOperationnoteBean = (BsbyOperationnoteBean) request.getAttribute("bsbyOperationnoteBean"); 
	
	List bsbyProductList = null;
	if (request.getAttribute("bsbyProductList")!=null) {
		bsbyProductList = (List)request.getAttribute("bsbyProductList");
	} else {
		bsbyProductList = new ArrayList();
	}
	EasyuiDataGridJson bsbyData = new EasyuiDataGridJson();
 	bsbyData.setTotal((long)bsbyProductList.size());
 	bsbyData.setRows(bsbyProductList);
 	String bsbyRows = JSONObject.fromObject(bsbyData).toString();
%>
<!DOCTYPE html>
<html>
<head>
<title>报损报溢</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
	$(function() {
		initJSP();
		$("#addbybsProductForm [name=productCode]").keypress(function(e) {
	        var key = window.event ? e.keyCode : e.which;
	        if (key.toString() == "13") {
	        	$("#addProductbutton").click();
	            return false;
	        }
	    });
		$("#addbybsProductForm [name=planCountGD]").keypress(function(e) {
	        var key = window.event ? e.keyCode : e.which;
	        if (key.toString() == "13") {
	        	$("#addProductbutton").click();
	            return false;
	        }
	    });
		$("#addbybsProductForm [name=cargoCode]").keypress(function(e) {
	        var key = window.event ? e.keyCode : e.which;
	        if (key.toString() == "13") {
	        	$("#addProductbutton").click();
	            return false;
	        }
	    });
		$("#searchProductForm [name=name]").keypress(function(e) {
	        var key = window.event ? e.keyCode : e.which;
	        if (key.toString() == "13") {
	        	$("#searchProductButton").click();
	            return false;
	        }
	    });
	});
	
	function initJSP() {
		initBsByEditGrid();
	};
	
	function initBsByEditGrid() {
	<%if (bsbyOperationnoteBean!=null) {
		if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3){}
		else {%>
			var options = {
		   			collapsible:true,
		   			fit : true,
		   			fitColumns : true,
		   			border : true,
		   			idField :'id',
		   			singleSelect : true,
		   			toolbar : '#tb',
		   			striped : true,
		   			nowrap : false,
		   			columns : [ [{
		   	 			field : 'product_code',
		   				title : '产品编号',
		   				width : 100,
		   				align : 'center',
		   				formatter : function(value, row, rowIndex) {
		   					return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.product_id+'" target="_blank">'+value+'</a>'; 
		   				}
		   			}, {
		   	 			field : 'product_name',
		   				title : '产品名称',
		   				width : 250,
		   				align : 'center',
		   				formatter : function(value, row, rowIndex) {
		   					return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.product_id+'" target="_blank">'+value+'</a>'; 
		   				}
		   			}, {
		   				field : 'oriname',
		   				title : '原名称',
		   				width : 250,
		   				align : 'center',
		   				formatter : function(value, row, rowIndex) {
		   					return '<a href="<%=request.getContextPath() %>/admin/fproduct.do?id='+row.product_id+'" target="_blank">'+value+'</a>'; 
		   				}
		   			}, {
		   				field : 'bsby_count',
		   				title : '<%=title %>量',
		   				width : 80,
		   				align : 'center',
						editor : {
							type : 'numberbox',
							options : {
								min:0,
								required:true
							}
						}
		   			}, {
		   				field : 'bsby_before_after_count',
		   				title : '<%=title %>前(后)可用库存量',
		   				width : 250,
		   				align : 'center'
		   			}, {
		   				field : 'whole_code',
		   				title : '<%=title %>货位号',
		   				width : 100,
		   				align : 'center'
		   			}, {
		   				field : 'action',
		   				title : '操作',
		   				width : 100,
		   				align : 'center',
		   				formatter : function(value, row, rowIndex) {
		   					<%if(bsbyOperationnoteBean.getCurrent_type()==0||bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==5){%> 
		   						return '<a href="#" class="delProductButton" onclick="delBsbyProduct('+row.product_id+','+row.id+',<%=bsbyOperationnoteBean.getId() %>);">删除</a>';
		   					<%} else {%> 
		   						return '';
		   					<%} %>
		   				}
		   			}, {
		   				field : 'search',
		   				title : '查进销存',
		   				width : 100,
		   				align : 'center',
		   				formatter : function(value, row, rowIndex) {
		   					<%if(group.isFlag(232)){ %>
		   						return '<a href="<%=request.getContextPath() %>/admin/rec/oper/bsby/stockCardList.jsp?pid='+row.product_id+'" target="_blank">查</a>'; 
		   					<%} else {%> 
		   						return '';
		   					<%} %>
		   				}
		   			}] ],
		   			onLoadSuccess : function(data) {
		   				//改变datagrid中按钮的class
		   				$(".delProductButton").linkbutton(
		   					{ 
		   						text:'删除', 
		   						plain:true, 
		   						iconCls:'icon-cancel' 
		   					}
		   				);
		   			}
		   		}
			$("#bsbyEditGrid").datagrid(options);
			$("#bsbyEditGrid").datagrid("loadData", <%=bsbyRows%>);
			<%if(bsbyOperationnoteBean.getCurrent_type()==0||bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==5){%>
				$("#bsbyEditGrid").datagrid("beginEdit", 0);
			<%}%>
		<%}
		}%>
	}
	function button1()
	{
		var type = $("#type").val();
		var remark = $("#remark").val();
		var opid = $("#id").val();
		var biaodantype = $("#biaodantype").val();
		window.location.href="<%=request.getContextPath() %>/ByBsController/updateCurrentType.mmx?type="+type+"&opid="+opid+"&remark="+remark+"&biaodantype="+biaodantype;
	}
	function button2()
	{
		var type1 = $("#type1").val();
		var remark = $("#remark").val();
		var opid = $("#id").val();
		var biaodantype = $("#biaodantype").val();
		window.location.href="<%=request.getContextPath() %>/ByBsController/updateCurrentType.mmx?type1="+type1+"&opid="+opid+"&remark="+remark+"&biaodantype="+biaodantype;
	}
	function submitForm(formName) {
		$('#'+formName).submit();
	}
	function updateBsbyProductCount() {
		var rows = $("#bsbyEditGrid").datagrid("getRows");
		if (rows.length > 0 ) {
			if ($("#bsbyEditGrid").datagrid("validateRow", 0)) {
				$("#bsbyEditGrid").datagrid("endEdit", 0);
				$("#editCount").attr("value" , rows[0].bsby_count);
				$("#bsbyEditGrid").datagrid("beginEdit", 0);
				$('#updateBsbyProductCountForm').submit();
			}
		} else {
			$.messager.alert("提示", "请先添加商品！", "info");
		}
	}
	function delBsbyProduct(pid,bsbypid,opid) {
		$("#delBsbyProductForm [id=pid]").attr("value" , pid);
		$("#delBsbyProductForm [id=bsbypid]").attr("value" , bsbypid);
		$("#delBsbyProductForm [id=opid]").attr("value" , opid);
		$('#delBsbyProductForm').submit();
	}
	
	function addproduct(code,cargoCode)
	{
		$("#addbybsProductForm [name=productCode]").attr("value", code);
		$("#addbybsProductForm [name=cargoCode]").attr("value", cargoCode);
		var valueGD = 1;
		$.messager.prompt('请确认', '将添加产品编号为'+code+'的产品，请输入数量，并确认', function(r){
			if (r){
				$("#addbybsProductForm [name=planCountGD]").attr("value", r);
				$("#addbybsProductForm").submit();
			}
		});
	}
	
</script>
</head>
<body>
<div id="tb" style="padding:3px;height: auto;">
<% 
	CargoInfoAreaBean stockArea = (CargoInfoAreaBean)request.getAttribute("stockArea");
			
	if (bsbyOperationnoteBean != null) { 
		Map stockTypeMap = ProductStockBean.stockTypeMap; 
		Map areaMap = ProductStockBean.areaMap; %>
		<table class="tableForm">
		<tr>
		<td>
		<form method="post" id="afterEditForm" action="${pageContext.request.contextPath}/ByBsController/editRemark.mmx">
			<input type="hidden" name="biaodantype" id="biaodantype" value="<%=bsbyOperationnoteBean.getType() %>">
			<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="id" id="id"/>
			编号:<%=bsbyOperationnoteBean.getReceipts_number()%>&nbsp;&nbsp;
			<%if(bsbyOperationnoteBean.getSource()>0){ %>
				盘点作业单编号:<%=bsbyOperationnoteBean.getSourceCode()%>&nbsp;&nbsp;
			<%} %>
			库类型：<%=stockTypeMap.get(Integer.valueOf(bsbyOperationnoteBean.getWarehouse_type()))%>&nbsp;&nbsp;
			库区域:<%=areaMap.get(Integer.valueOf(bsbyOperationnoteBean.getWarehouse_area()))%>&nbsp;&nbsp;
			状态:<%=bsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(bsbyOperationnoteBean.getCurrent_type()))%>&nbsp;&nbsp;
			<%-- 单子的初始状态为“处理中”，对应操作为“提交审核”，提交后状态改为“审核中”，“审核中”的单据对应操作“通过审核”和“未通过审核”，未通过审核状态改为“审核未通过”，对应的操作为“提交审核”，通过审核状态改为“已完成”--%>
			<%--谁添加的单据谁才有权限修改单据内的具体参数和将这个单据“提交审核”，当然具有审核权限的人也应据修改单据参数的权限 --%>
			<%if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3
					||(bsbyOperationnoteBean.getCurrent_type()==6&&!group.isFlag(229))
					||(bsbyOperationnoteBean.getCurrent_type()==1&&!group.isFlag(413))
					||(bsbyOperationnoteBean.getCurrent_type()==0&&user.getId()!=bsbyOperationnoteBean.getOperator_id())
					||(bsbyOperationnoteBean.getCurrent_type()==2&&user.getId()!=bsbyOperationnoteBean.getOperator_id()))
			{ 
				
			} else {
				if (buttonString != null) {%>
					<input type="hidden" value="<%=type%>" name="type" id="type"/>
					<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true"   onclick="button1()"><%=buttonString%></a>
				<%}
				if (buttonString1 != null) { %>
					<input type="hidden" value="<%=type1%>" name="type1" id="type1"/>
					<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"    onclick="button2()"><%=buttonString1%></a>
				<%}				
			} %>
			</td>
			</tr>
			</table>
			<table class="tableForm">
			<tr>
			<td>
			<%//提交审核后 单据的添加人不能再修改备注 但如果单据添加人是有审核权限的 那么他还可以修改备注
			if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3){
			} else { 
				if(bsbyOperationnoteBean.getType()==0){%>
					报损原因:
				<%}else{%>
					报溢原因:
				<%} %>
				<input type="text" name="remark" size="50" id="remark" value="<%=StringUtil.convertNull(bsbyOperationnoteBean.getRemark()) %>"/>&nbsp;&nbsp;&nbsp;
				<%if((bsbyOperationnoteBean.getCurrent_type()!=4&&group.isFlag(229))||(bsbyOperationnoteBean.getCurrent_type()==0&&!group.isFlag(229))){%>
					<a href="#" class="easyui-linkbutton"  data-options="iconCls:'icon-edit',plain:true"   onclick="submitForm('afterEditForm')">修改</a>
				<%} %>
			<%} %>
			</td>
			</tr>
			</table>
		</form>
		<%if(group.isFlag(229)||group.isFlag(413)||bsbyOperationnoteBean.getCurrent_type()==0
				||bsbyOperationnoteBean.getCurrent_type()==1||bsbyOperationnoteBean.getCurrent_type()==6
				||bsbyOperationnoteBean.getCurrent_type()==3||bsbyOperationnoteBean.getCurrent_type()==4){%>
			<%if(group.isFlag(413)&&
					(bsbyOperationnoteBean.getCurrent_type()==1||bsbyOperationnoteBean.getCurrent_type()==6
					||bsbyOperationnoteBean.getCurrent_type()==3||bsbyOperationnoteBean.getCurrent_type()==4)){ %>
				<form id="updateFinAuditRemarkForm" method="post" action="${pageContext.request.contextPath}/ByBsController/updateFinAuditRemark.mmx">
				<table class="tableForm">
				<tr>
				<td>
					财务审核意见:
					<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="opid" id="id"/>
					<input type="text" name="finAuditRemark" size="50" id="finAuditRemark" value="<%=StringUtil.convertNull(bsbyOperationnoteBean.getFinAuditRemark()) %>"/>&nbsp;&nbsp;&nbsp;
					<%if(group.isFlag(413)&&bsbyOperationnoteBean.getCurrent_type()==1){%>
						<a href="#" class="easyui-linkbutton"  data-options="iconCls:'icon-edit',plain:true"  onclick="submitForm('updateFinAuditRemarkForm');">修改</a>
					<%} %>
				</td>
				</tr>
				</table>
				</form>
			<%}
			if(group.isFlag(229)&&(bsbyOperationnoteBean.getCurrent_type()==6||bsbyOperationnoteBean.getCurrent_type()==4)){%>
				<form id= "updateExamineSuggestionForm" method="post"  action="${pageContext.request.contextPath}/ByBsController/updateExamineSuggestion.mmx">
					<table class="tableForm">
					<tr>
					<td>
					审核意见:
					<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="opid" id="id"/>
					<input type="text" name="examineSuggestion" size="50" id="examineSuggestion" value="<%=StringUtil.convertNull(bsbyOperationnoteBean.getExamineSuggestion())  %>"/>&nbsp;&nbsp;&nbsp;
					<%if(group.isFlag(229)&&bsbyOperationnoteBean.getCurrent_type()==6){%>
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true"  onclick="submitForm('updateExamineSuggestionForm');">修改</a>
					<%} %>
					</td>
					</tr>
					</table>
				</form>
			<%}%>
		<%} %>
		<br />
		
		<%if(bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==1||bsbyOperationnoteBean.getCurrent_type()==5||bsbyOperationnoteBean.getCurrent_type()==6){%>
			<fieldset>
   				<legend>操作：</legend>
				<p align="center">
					<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
					|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/list.jsp">返回报损报溢操作记录列表</a> 
					|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/bsbyPrint.jsp?opid=<%=bsbyOperationnoteBean.getId() %>&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" target="_blank"> 导出列表</a>
				</p>
			</fieldset>	
		<%}
	}%>

	<%
	Integer listCount = (Integer)request.getAttribute("listCount");
	if(bsbyOperationnoteBean.getCurrent_type()==4||bsbyOperationnoteBean.getCurrent_type()==3){}
	else{
		if(StringUtil.toInt(listCount.toString())==0){//如果单据中已经有了商品就不能再添加商品%>
			<fieldset>
			    <legend>产品信息</legend>
				<form method="post" id="addbybsProductForm" action="${pageContext.request.contextPath}/ByBsController/addByBsProduct.mmx">
					<p align="center">	
						<input type="hidden" name="opid" value="<%=bsbyOperationnoteBean.getId()  %>">
						<input type="hidden" name="opcode" value="<%=bsbyOperationnoteBean.getReceipts_number()%>">
						产品编号：<input type="text" name="productCode" value="" size="8"/>&nbsp;
						数量：<input type="text" name="planCountGD" value="" size="3"/>&nbsp;
						货位号：<input type="text" name="cargoCode" size="12"/>&nbsp;
						<a href="#" class="easyui-linkbutton" type="submit" id="addProductbutton"  data-options="iconCls:'icon-add',plain:true"  onclick="submitForm('addbybsProductForm');">添加</a>
					</p>
					<p align="center">
						<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
						|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/list.jsp">返回报损报溢操作记录列表</a> 
						|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/bsbyPrint.jsp?opid=<%=bsbyOperationnoteBean.getId()  %>&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" 	> 导出列表</a>
					</p>
				</form>
			</fieldset>

			<fieldset>
				<legend>产品查询</legend>
				<form method=post action="<%=request.getContextPath() %>/admin/rec/oper/bsby/isearchproduct.jsp" target="sp" onsubmit="$('#d1').show();return true;">
					<input type="hidden" name="type" value="1" />
					<input type="hidden" name="operType" value="<%=bsbyOperationnoteBean.getType() %>" />
					<input type="hidden" name="stockType" value="<%=bsbyOperationnoteBean.getWarehouse_type() %>" />
					<input type="hidden" name="stockAreaId" value="<%=area.getId() %>" />
					<input type=hidden name="code" value="" size=12/>
					<input type="hidden" name="frombybs" value="yes"/>
					<p align="center">
						产品名：<input type=text name="name" value="" size=12>
						<a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-search'" onclick="$('#searchProductButton').click();">查询产品</a>
						<input style="display:none" type=submit id="searchProductButton"  value="查询产品" onclick="$('#d1').show();">
						<a href="#" class="easyui-linkbutton"  data-options="iconCls:'icon-cancel',plain:true"  onclick="$('#d1').hide();">关闭窗口</a>
					</p>
				</form>
				<div id="d1" style="display:none">
					<iframe name=sp width=90% height=300 align=center frameborder=0>
					</iframe>
				</div>
			</fieldset>
		<%} else if (bsbyOperationnoteBean.getCurrent_type()==0){%>
			<fieldset>
			   <legend>操作：</legend>
				<p align="center">
					<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
					|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/list.jsp">返回报损报溢操作记录列表</a> 
					|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/bsbyPrint.jsp?opid=<%=bsbyOperationnoteBean.getId()  %>&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" target="_blank"> 导出列表</a>
				</p>
			</fieldset>	
		<%} %>
		<%if(bsbyOperationnoteBean.getCurrent_type()==0||bsbyOperationnoteBean.getCurrent_type()==2||bsbyOperationnoteBean.getCurrent_type()==5){%>
			<div id="editProducttb">
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" onclick="updateBsbyProductCount();">修改</a>
			</div>
		<%} else { %>
			<div id="editProducttb">
			</div>
		<%} %>
	
</div>
		<table id="bsbyEditGrid"></table>
		<form id="updateBsbyProductCountForm" action="${pageContext.request.contextPath}/ByBsController/updateBsbyProductCount.mmx">
		<input type="hidden" name="opid" value="<%=bsbyOperationnoteBean.getId() %>"/>
		<input type="hidden" name="opcode" value="<%=bsbyOperationnoteBean.getReceipts_number()%>">
		<input type="hidden" name="editCount" id="editCount"/>
		</form>
		<form id="delBsbyProductForm" action="${pageContext.request.contextPath}/ByBsController/delByBsProduct.mmx">
		<input type="hidden" name="opid" id="opid"/>
		<input type="hidden" name="pid" id="pid"/>
		<input type="hidden" name="bsbypid" id="bsbypid"/>
		</form>
		<%}%>
</body>
</html>
