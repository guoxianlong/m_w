<%@page contentType="text/html;charset=utf-8"%><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*, adultadmin.util.*" %>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.action.bybs.ByBsAction"%>
<%@page import="adultadmin.bean.bybs.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="../taglibs.jsp"%>
<%! int x = 0; %>
<%voUser user = (voUser)request.getSession().getAttribute("userView");
 int userid = user.getId();
 UserGroupBean group = user.getGroup();
	Map stockTypeMap = ProductStockBean.stockTypeMap;
	Map areaMap = ProductStockBean.areaMap;
	
String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String remark = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("remark")));
String startTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startTime")));
String endTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endTime")));
String auditStartTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("auditStartTime")));
String auditEndTime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("auditEndTime")));
String _type = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("type")));
String warehouseArea = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("warehouseArea")));
int area = StringUtil.toInt(warehouseArea);
String warehouseType = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("warehouseType")));
String sourceCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("sourceCode")));
String productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
String productCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
productName = Encoder.decrypt(productName);//解码为中文
if(productName==null){//解码失败,表示已经为中文,则返回默认
	productName =StringUtil.dealParam(request.getParameter("productName"));//名称
}
if (productName==null) productName="";
String[] statuss = request.getParameterValues("status");
String status = "";
if(statuss!=null){
	for(int i=0;i<statuss.length;i++){
		status = status + statuss[i]+",";
	}
}
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>My JSP 'list.jsp' starting page</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script type="text/javascript">
	function searchCheck(){
	var startTime = document.searchForm.startTime.value;
	var endTime = document.searchForm.endTime.value;
	if((startTime == '' && endTime != '')||(startTime != '' && endTime == '')){
		alert('生成时间段必须填写完整');
		return false;
	}
	
	
	if(startTime != '' && endTime != ''){
	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;
    if (!re.test(startTime) || !re.test(endTime))
     {
         alert('日期格式不合法');
         return false;
     }
     
     if(startTime > endTime){
		alert('开始时间不能大于结束时间');
		return false;
	}
	}
	
	if($("#word").val().length==0){selectOption(document.getElementById('proxy'), '0');}
	return true;
	}
	
	function checkSubmit(addForm){
		var warehouse_area = document.getElementById("warehouse_area").value;
		var warehouse_type = document.getElementById("warehouse_type").value;
		if(warehouse_type==-1)
		{
		 	alert("请选择库类型!");
		 	return false;
		}
		if(warehouse_area==-1)
		{
			alert("请选择库区域!");
		 	return false;
		}
	}
function checkboxChecked(checkbox,value){
	values = value.split(",");
	for(var j = 0; j < values.length; j++){
		for(var i = 0; i < checkbox.length; i++){
			if(checkbox[i].value == values[j]){
				checkbox[i].checked = true;
			}
		}
	}
}
	
	/*
	function setStockArea2(sptSrc, sptObj){
	x = sptSrc.selectedIndex;
	for (var m = sptObj.options.length - 1; m > 0; m --){
		sptObj.options[m] = null;
	}
	sptObj.options[0]=new Option("全部", "-1");
	for (i = 0; i < ps_spts[x].length; i ++){
		sptObj.options[i + 1]=new Option(ps_spts[x][i].text, ps_spts[x][i].value);
	}
	sptObj.options[0].selected=true;
}
*/
$(function () {
	loadArea($("#warehouseType").val(),<%= area %>);
		$("#warehouseType").change(function(){
			$("#warehouseArea").val(-1);
			loadArea($("#warehouseType").val(),-1);
		});
		$("#warehouse_type").change(function(){
			$("#warehouse_area").val(-1);
			loadArea2($("#warehouse_type").val(),-1);
		});
});
function loadArea(stockTypeId,tag){
		$.ajax({
			url:'${pageContext.request.contextPath}/CargoController/querySelectStockAreaAccess.mmx?stockType='+stockTypeId,
			cache:false,
			dataType:'text',
			type:'post',
			success:function(dd){
				$("#warehouseArea").empty();
				var arr = eval('('+dd+')');
				for(var i=0;i<arr.length;i++){
					var opt = $("<option>").text(arr[i].text).val(arr[i].id);
					$("#warehouseArea").append(opt);
				}
				if(tag!=-1){
					//查询后库地区默认选中
					$("#warehouseArea").val(tag);
				}
			}
		});
	}
function loadArea2(stockTypeId,tag){
		$.ajax({
			url:'${pageContext.request.contextPath}/CargoController/querySelectStockAreaAccess.mmx?stockType='+stockTypeId,
			cache:false,
			dataType:'text',
			type:'post',
			success:function(dd){
				$("#warehouse_area").empty();
				var arr = eval('('+dd+')');
				for(var i=0;i<arr.length;i++){
					var opt = $("<option>").text(arr[i].text).val(arr[i].id);
					$("#warehouse_area").append(opt);
				}
				if(tag!=-1){
					//查询后库地区默认选中
					$("#warehouse_area").val(tag);
				}
			}
		});
	}

	</script>
	</head>

	<body>
	<center>
				<strong>报损报溢操作记录</strong>
	</center>
		

<fieldset>
<legend>查询条件</legend>
<form name="searchForm" action="<%=request.getContextPath()%>/admin/searchBsby.do" method="post" onSubmit="return searchCheck();">
   
   单据号：<input type="text" name="code" value="<%=code %>" size=14/>&nbsp;&nbsp;
   盘点作业单编号：<input type="text" name="sourceCode" value="<%=sourceCode %>" size=14/>&nbsp;&nbsp;
   <script>selectOption(document.getElementById('payStatus'), '<%=0%>');</script>
   生成时间段：<input type="text" size=14 name="startTime" value="<%=startTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
  到<input type="text" size=14 name="endTime" value="<%=endTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
<mmb:permit value="413">
审批时间：<input type="text" size=14 name="auditStartTime" value="<%=auditStartTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
到<input type="text" size=14 name="auditEndTime" value="<%=auditEndTime %>" onclick="SelectDate(this,'yyyy-MM-dd');"/>
</mmb:permit>
<br/>
  库类型:  <select name="warehouseType" id="warehouseType" >
							<option value="">全部</option>
<%
	HashMap stockMap = ProductStockBean.stockTypeMap;
	Iterator stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER ){
			continue;
		}
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
	<%} %>
							
						</select>&nbsp;&nbsp;&nbsp;
						<script>selectOption(document.getElementById('warehouseType'), '<%=warehouseType%>');</script>
						库区域:  <select name="warehouseArea" id="warehouseArea">
							<option value="">全部</option>
						</select>&nbsp;&nbsp;&nbsp;
						<script>
						//setStockArea2(document.getElementById('warehouseType'), document.getElementById('warehouseArea'));selectOption(document.getElementById('warehouseArea'), '<%=warehouseArea%>');
						</script>
	单据类型:  <select name="type" id="type">
							<option selected value="">
								全部
							</option>
							<option value="0">
								报损
							</option>
							<option value="1">
								报溢
							</option>
						</select>&nbsp;&nbsp;&nbsp;
						<script>selectOption(document.getElementById('type'), '<%=_type%>');</script> 
	报损报溢原因:
	
	<select name='remark' id='remark'>
	<option value="" >全部</option>  
						   <%
						   List bsbyReasonList = (List)request.getAttribute("bsbyReasonList");//报损原因列表
						   if(bsbyReasonList!=null && bsbyReasonList.size()>0){ 
							   for(int i=0;i<bsbyReasonList.size();i++){
								   BsbyReason bean =(BsbyReason)bsbyReasonList.get(i);
							%>
								<option value="<%=bean.getReason()%>" ><%=bean.getReason()%></option>  
						   <%}} %>
				</select>
				<script>selectOption(document.getElementById('remark'), '<%=remark%>');</script> 					
   <br/>
   状态：
   <input type="checkbox" name="status" value="0">处理中&nbsp;&nbsp;
   <input type="checkbox" name="status" value="1">审核中&nbsp;&nbsp;
   <input type="checkbox" name="status" value="6">运营审核通过&nbsp;&nbsp;
   <input type="checkbox" name="status" value="5">运营审核未通过&nbsp;&nbsp;
   <input type="checkbox" name="status" value="2">财务审核未通过&nbsp;&nbsp;
   <input type="checkbox" name="status" value="4">已完成&nbsp;&nbsp;
   <script>checkboxChecked(document.getElementsByName('status'),'<%=status%>');</script>
产品编号：<input type="text" name="productCode" value="<%=productCode %>"/>&nbsp;&nbsp;
产品原名称：<input type="text" name="productName" value="<%=productName %>"/>&nbsp;&nbsp;
<input type="hidden" id="excel" name="excel" value="0"/>
  <input type="submit" onclick="javascript:{document.getElementById('excel').value='0'; };" value="查询"/>&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="导出excel" onclick="javascript:{document.getElementById('excel').value='1';}"/>
   </form>
</fieldset>

<form action="bybs.do" name="bsbyForm" method="post" onsubmit="return checkSubmit(this);" >
			<input type="hidden" value="add" name="method"/>
						单据类型:  <select name="operationnoteType" id="operationnoteType">
							<option selected value="0">
								报损
							</option>
							<option value="1">
								报溢
							</option>
						</select>&nbsp;&nbsp;&nbsp;
						库类型:  <select name="warehouse_type" id="warehouse_type">
							<option value="-1">全部</option>
<%
	stockMap = ProductStockBean.stockTypeMap;
	stockKeyIter = stockMap.keySet().iterator();
	while(stockKeyIter.hasNext()){
		Integer key = (Integer)stockKeyIter.next();
		if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
				key.intValue() == ProductStockBean.STOCKTYPE_NIFFER){
			continue;
		}
%>
	<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
	<%} %>
							
						</select>&nbsp;&nbsp;&nbsp;
						库区域:  <select name="warehouse_area" id="warehouse_area">
							<option value="-1">全部</option>
						</select>&nbsp;&nbsp;&nbsp;
						<input type="submit" value="添加报损报溢单" />
		</form>
		
		<table cellpadding="3" cellspacing="1" border="1" style="border-collapse:collapse;" width="100%">
			<tr>
				<td align="center">
					序号
				</td>
				<td align="center">
					单据号
				</td>
				<td align="center">
					报损报溢原因
				</td>
				<td align="center">
					相关盘点作业单
				</td>
				<td align="center">
					库类型
				</td>
				<td align="center">
					库区域
				</td>
				<td align="center">
				 	产品编号
				</td>
				<%if(group.isFlag(413)) {%>
				<td align="center">
				 	产品线
				</td>
				<td align="center">
				 	一级分类
				</td>
				<td align="center">
				 	二级分类
				</td>
				<td align="center">
				 	三级分类
				</td><%} %>
				<td align="center">
				 	数量
				</td>
				<%if(group.isFlag(413)) {%>
				<td align="center">
				    报损报溢单价 含税
				</td>
				<td align="center">
				    报损报溢单价 不含税
				</td>
				<td align="center">
				    报损报溢总额 含税
				</td>
				<td align="center">
				    报损报溢总额 不含税
				</td><%} %>
				<td align="center">
				   原名称
				</td>
				<td align="center">
					添加时间
				</td>
				<td align="center">
					制作人
				</td>
				<td align="center">
					状态
				</td>
				<td align="center">
					运营审核人/时间
				</td>
				<td align="center">
					财务审核人/时间
				</td>
				<td align="center">
					操作
				</td>
			</tr>
			
				<logic:present name="list" scope="request">
					<logic:iterate name="list" id="item" indexId="index"
						type="adultadmin.bean.bybs.BsbyOperationnoteBean">
					
						<tr <%x++;if(x%2==0){%> bgcolor="#EEE9D9"<%}%>><td><%=index + 1%></td>
						<td>
						<%int type = item.getCurrent_type();
						if((type==0||type==1||type==2||type==5)&&(userid==item.getOperator_id()||group.isFlag(229))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=item.getId() %>"><bean:write name="item" property="receipts_number" /></a>
						<%}else if(type==6&&(userid==item.getOperator_id()||group.isFlag(413))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=item.getId() %>"><bean:write name="item" property="receipts_number" /></a>
						<%}else {%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=item.getId() %>" ><bean:write name="item" property="receipts_number" /></a>
						<%}%>
							
						</td>
						<td><%if(item.getRemark()!=null)%><%=item.getRemark() %></td>
						<td><a href="<%=request.getContextPath()%>/admin/cargoInventory.do?method=cargoInventory&id=<%=item.getSource()%>" target="_blank"><%=StringUtil.convertNull(item.getSourceCode()) %></a></td>
						<td><%=stockTypeMap.get(Integer.valueOf(item
									.getWarehouse_type()))%></td>
						<td><%=areaMap.get(Integer.valueOf(item
									.getWarehouse_area()))%></td>
						<td><%=item.getProductCode() %></td>
						<%if(group.isFlag(413)) {%>
						<td><%=item.getProductLine() %></td>
						<td><%=item.getParentName1() %></td>
						<td><%=item.getParentName2() %></td>
						<td><%=item.getParentName3() %></td>
						<%} %>
						<td><%=item.getBsbyCount() %></td>
						<%if(group.isFlag(413)) {%>
						<td><%=item.getPrice() %></td>
						<td><%=item.getPriceNotOfTax() %></td>
						<td><%=item.getAllPrice() %></td>
						<td><%=item.getAllPriceNotOfTax() %></td>
						<%}%>
						<td><%=item.getOriname() %></td>
						<td>
							<%=item.getAdd_time().substring(0,16) %>
						</td>
						<td>
							<bean:write name="item" property="operator_name" />
						</td>
						<td><%=item.current_typeMap.get(Integer.valueOf(item
									.getCurrent_type()))%></td>
						<td><%if(item.getFinAuditDatetime()!=null){%>
								<%=item.getFinAuditName() %>/<%=item.getFinAuditDatetime().substring(0,19) %>
							<%} %>
						</td>
						<td><%if(item.getEnd_time()!=null){%>
								<%=item.getEnd_oper_name() %>/<%=item.getEnd_time().substring(0,19) %>
							<%} %>
						</td>
						
						<td>
							<%
							//没有完成以前都可以编辑和删除单据，如果提交审核后，只有有权限的人才能修改单据内的具体参数，当单据被审核打回时，添加人能修改单据内的具体参数同时能删除单据
							if((type==0||type==1||type==2||type==5)&&(userid==item.getOperator_id()||group.isFlag(229))){%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=item.getId() %>">编辑</a>
								<%if(type!=1){//提交审核后不能删除 %>
									<a href="<%=request.getContextPath()%>/admin/bsby/delByOpid.jsp?code=<%=item.getReceipts_number() %>&opid=<%=item.getId() %>" onclick="return confirm('确认删除？')">删除</a>
								<%}%>
							<%}else if(type==6&&(userid==item.getOperator_id()||group.isFlag(413))){%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=item.getId() %>">编辑</a>
							<%}else if(type==3||type==4){//已经完成的单据
							%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=item.getId() %>" >查看</a> | <a href="bsby/bsbyPrint.jsp?opid=<%=item.getId() %>&opcode=<%=item.getReceipts_number() %>" > 打印</a> | <a href="bsby/printRecord.jsp?opid=<%=item.getId() %>" target="_blank">打印<%=item.getPrint_sum() %>次</a>
							<%}else{%>
								<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=item.getId() %>" >查看</a>
							<%} %>
						</td></tr>
					</logic:iterate>
				</logic:present>
			
			<tr>
				<td colspan="14">
					<%
						PagingBean paging = (PagingBean) request.getAttribute("paging");
						if (paging != null) {
					%>
					<p align="left"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;",
								"pageIndex", 10)%></p>
					<%
						}
					%>
				</td>
			</tr>
		</table>
		
	</body>
</html>
