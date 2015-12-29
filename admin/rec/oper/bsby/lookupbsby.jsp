<%@ page language="java" pageEncoding="UTF-8"%>
<%@page	import="adultadmin.bean.stock.ProductStockBean"%>
<%@page	import="java.util.Map,adultadmin.action.vo.*,adultadmin.bean.*,adultadmin.bean.cargo.*"%>
<%@page import="adultadmin.bean.bybs.*"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%>
<%@page import="adultadmin.bean.bybs.BsbyProductBean" %>
<%@page import="java.util.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@ page import="net.sf.json.JSONObject"%>
<%@ page import="mmb.rec.sys.easyui.*" %>
<%
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	String title = (String)request.getAttribute("title");
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
	});
	
	function initJSP() {
		initBsByEditGrid();
	};
	
	function initBsByEditGrid() {
			var options = {
		   			collapsible:true,
		   			fit : true,
		   			fitColumns : true,
		   			border : true,
		   			idField :'id',
		   			toolbar : '#tb',
		   			singleSelect : true,
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
		   				align : 'center'
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
		   				field : 'search',
		   				title : '查看进销存',
		   				width : 100,
		   				align : 'center',
		   				formatter : function(value, row, rowIndex) {
		   					<%if(group.isFlag(232)){ %>
		   						return '<a href="<%=request.getContextPath() %>/admin/rec/oper/bsby/stockCardList.jsp?pid='+row.product_id+'" target="_blank">查</a>'; 
		   					<%} else {%> 
		   						return '';
		   					<%} %>
		   				}
		   			}] ]
		   		}
			$("#bsbyEditGrid").datagrid(options);
			$("#bsbyEditGrid").datagrid("loadData", <%=bsbyRows%>);
	}
</script>
</head>
<body>
<div id='tb' style="padding:3px;height: auto;">
<% 
			
	if (bsbyOperationnoteBean != null) { 
		Map stockTypeMap = ProductStockBean.stockTypeMap; 
		Map areaMap = ProductStockBean.areaMap; %>
		<form method="post" id="afterEditForm" action="${pageContext.request.contextPath}/ByBsController/editRemark.mmx">
			<input type="hidden" name="biaodantype" id="biaodantype" value="<%=bsbyOperationnoteBean.getType() %>">
			<input type="hidden" value="<%=bsbyOperationnoteBean.getId() %>" name="id" id="id"/>
			<table class="tableForm">
			<tr>
			<td>
			编号:<%=bsbyOperationnoteBean.getReceipts_number()%>&nbsp;&nbsp;
			<%if(bsbyOperationnoteBean.getSource()>0){ %>
				盘点作业单编号:<%=bsbyOperationnoteBean.getSourceCode()%>&nbsp;&nbsp;
			<%} %>
			库类型：<%=stockTypeMap.get(Integer.valueOf(bsbyOperationnoteBean.getWarehouse_type()))%>&nbsp;&nbsp;
			库区域:<%=areaMap.get(Integer.valueOf(bsbyOperationnoteBean.getWarehouse_area()))%>&nbsp;&nbsp;
			状态:<%=bsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(bsbyOperationnoteBean.getCurrent_type()))%>&nbsp;&nbsp;
				<strong>生成人/审核人：</strong><%if(bsbyOperationnoteBean.getOperator_name()!=null){%><%=bsbyOperationnoteBean.getOperator_name() %><%}%>/<%if(bsbyOperationnoteBean.getEnd_oper_name()!=null){%><%=bsbyOperationnoteBean.getEnd_oper_name()%><%}%>
				&nbsp;&nbsp;&nbsp;&nbsp;<%if(bsbyOperationnoteBean.getCurrent_type()==4){ %><strong>财务部审核：</strong><%=bsbyOperationnoteBean.getFinAuditName() %><%}%>
			</td>
			</tr>
			</table>
			<table class="tableForm">
			<tr>
			<td>
			<%=title %>原因：
			<input type="text" name="remark" size="50" id="remark" value="${bsbyOperationnoteBean.remark }"/>
			&nbsp;&nbsp;&nbsp;
			财务审核意见：
			<input type="text" name="finAuditRemark" size="50" id="finAuditRemark" value="${bsbyOperationnoteBean.finAuditRemark }"/>
			</td>
			</tr>
			</table>
			<table class="tableForm">
			<tr>
			<td>
			审核意见：
			<input type="text" name="examineSuggestion" size="50" id="examineSuggestion" value="${bsbyOperationnoteBean.examineSuggestion }"/>
			</td>
			</tr>
			</table>
			<br />
		</form>
		
			<fieldset>
   				<legend>操作：</legend>
				<p align="center">
					<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/operation_record.jsp?opid=<%=bsbyOperationnoteBean.getId() %>" target="_blank">人员操作记录</a> 
					|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/list.jsp">返回报损报溢操作记录列表</a> 
					|<a href="<%=request.getContextPath()%>/admin/rec/oper/bsby/bsbyPrint.jsp?opid=<%=bsbyOperationnoteBean.getId() %>&opcode=<%=bsbyOperationnoteBean.getReceipts_number()%>" target="_blank"> 导出列表</a>
				</p>
			</fieldset>	
	<% }%>

</div>
		<table id="bsbyEditGrid">
		</table>
</body>
</html>
