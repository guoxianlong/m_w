<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@page import="adultadmin.bean.order.OrderStockProductBean"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@page import="adultadmin.action.vo.voOrder"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="mmb.stock.stat.*"%>


<%@ page import="cache.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>

<html>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
SortingBatchBean batch=(SortingBatchBean)request.getAttribute("bean");
//PagingBean pageBean=(PagingBean)request.getAttribute("paging");
String batchId=(String)request.getParameter("batchId");
String count=(String)request.getAttribute("count");
String count1=(String)request.getAttribute("count1");
String count2=(String)request.getAttribute("count2");
DecimalFormat dcmFmt = new DecimalFormat("0.00");
String text=request.getParameter("text");
String deliver=request.getParameter("deliver");
PagingBean paging = (PagingBean) request.getAttribute("paging");
voUser adminUser = (voUser)session.getAttribute("userView");
String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
if(!catalogIds2.equals("")){
	String[] splits = catalogIds2.split(",");
	for(int i=0;i<splits.length;i++){
		voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
		if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
			catalogIds1 = catalog.getId() + "," + catalogIds1;
		}
	}
	if(catalogIds1.endsWith(",")){
		catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
	}
}
%>
<head>
<title>分拣波次列表页</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script type="text/javascript">

<%if(request.getAttribute("tip")!=null){%>
alert("<%=request.getAttribute("tip")%>");
<%}%>
function showPackage(id){
	$("#parcel"+id).slideToggle();
}
  function showPrintButtonDiv(id){
  	$("#"+id).slideToggle(50);
  }
  function inputCodePda(sortingBatchGroupId,printType,selectedOrder){
  	  var code = window.prompt("员工号:","");
  	  if(code==""){
  		inputCodePda(sortingBatchGroupId,printType,selectedOrder);
	  }else if(code){
	  	openPrintPagePda(code,sortingBatchGroupId,printType,selectedOrder);
	  }
  }
  function inputCode(sortingBatchGroupId,printType,selectedOrder){
  	  var code = window.prompt("员工号:","");
  	  if(code==""){
	 	 inputCode(sortingBatchGroupId,printType,selectedOrder);
	  }else if(code){
	  	openPrintPage(code,sortingBatchGroupId,printType,selectedOrder);
	  }
  }
  function openPrintPagePda(userCode,sortingBatchGroupId,printType,selectedOrder){
	 if( window.confirm("是否继续操作")){
		 window.location.href = 'sortingAction.do?method=sortingBatchGroupPrintLine&type=pda&userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType='+printType+'&selectedOrder='+selectedOrder+'&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex"))%>';
		 return true;	 
	 }else{
		 return false;
	 }
	}
  function openPrintPage(userCode,sortingBatchGroupId,printType,selectedOrder){
	 if( window.confirm("是否继续操作")){
		 window.location.href = 'sortingAction.do?method=sortingBatchGroupPrintLine&userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType='+printType+'&selectedOrder='+selectedOrder+'&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex"))%>';
		 return true;	 
	 }else{
		 return false;
	 }
	}
  function toPrintSelect(sortingBatchGroupId,checkBoxName){//准备打印所选的发货清单
  		var selectedOrder =  getSelected(checkBoxName);
		openPrintPage('',sortingBatchGroupId,'selected',selectedOrder);
	}

  function getSelected(checkBoxName){//获取选择的订单ID的字符串，以逗号分隔。
    var str ='';
  	$("[name="+checkBoxName+"]:checked").each(function(){  
		str+=$(this).val()+","; 
	})
	return str.substr(0,str.length-1);
  }
  function submitValue(id){
	  $('#showDivNum').val(id);
	 $('#sortingActionForm').submit();
  }	

</script>
</head>
<body bgcolor="#FFCC00">
<table width="99%" border="0">
<%if(batch!=null){ %>
  <tr>
    <td><strong>所属分拣批次:</strong><font color="blue"><%=batch.getCode() %></font></td>
    <td><strong>创建时间:</strong><font color="blue"><%if(batch.getCreateDatetime()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(batch.getCreateDatetime(),19) %><%} %></font></td>
    <td><strong>完成时间:</strong><font color="blue"><%if(batch.getCompleteDatetime()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(batch.getCompleteDatetime(),19) %><%} %></font></td>
    <td><strong>当前状态:</strong><font color="blue"><%=batch.getStatusName(batch.getStatus()) %></font></td>
  </tr>
  <%}else{ %>
  <tr>
    <td><strong>所属分拣批次:</strong><font color="blue"></font></td>
    <td><strong>创建时间:</strong><font color="blue"></font></td>
    <td><strong>完成时间:</strong><font color="blue"></font></td>
    <td><strong>当前状态:</strong><font color="blue"></font></td>
  </tr>
  <%} %>
   <tr>
    <td><strong>已完成:</strong><font color="blue"><%=count2%></font></td>
    <td><strong>分拣中:</strong><font color="blue"><%=count1%></font></td>
    <td><strong>未打单:</strong><font color="blue"><%=count%></font></td>
  </tr>
  
</table>
<hr/>
 <form  method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchGroupDetail&batchId=<%=batchId%>">
   <!-- 快递公司：<select name="deliver">
		<option value="-1">全部</option>
		<option value="0">EMS</option>
		<option value="1">非EMS</option>
	</select>&nbsp;&nbsp;&nbsp;--><input name="text" type="text" <%if(text!=null&&text.length()>0) {%>value="<%=text %>"<%}else{%>value="请输入分拣波次号/订单号"<%} %> size="40" width="10"onfocus="if(this.value=='请输入分拣波次号/订单号'){this.value=''}"/>&nbsp;<input type="submit" name="button" id="button" value="查询" />
</form>
<form  method="post"  name="sortingActionForm" id="sortingActionForm" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=groupModifyOrder&batchId=<%=batchId%>">
<input type="hidden" name="flag" value="1">
<input type="hidden" name="showDivNum" value="" id="showDivNum">
<table width="99%" border="0" cellspacing="10">
  <%
if(batch!=null){
	int showDivNum = StringUtil.parstInt(request.getParameter("showDivNum"));
List parcelList=batch.getGroupList();
  if(parcelList!=null){
  for(int i=0;i<parcelList.size();i++){ %>
  	<%SortingBatchGroupBean parcel=(SortingBatchGroupBean)parcelList.get(i); 
  	CargoStaffBean cargostaff=parcel.getCargoStaff();
  	%>
  	<%List packageList=parcel.getOrderList(); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>分拣波次号：<font color="blue"><%=StringUtil.convertNull(parcel.getCode()) %></font></strong></td>
  				<td><strong>订单总数：<font color="blue"><%=parcel.getOrderCount()%></font></strong></td>
  				<td><strong>SKU数：<font color="blue"><%=parcel.getSkuCount() %></font></strong></td>
  				<td><strong>商品件数：<font color="blue"><%=parcel.getProductCount() %></font></strong></td>
  				<td><strong>状态：<font color="blue"><%=StringUtil.convertNull(parcel.getStatusName()) %></font></strong></td>
  			</tr>
  			<tr>
  				<td><strong>作业仓：<font color="blue"><%=StringUtil.convertNull(batch.getStorageName()) %></font></strong></td>
  				
  				<!-- <td><strong>归属物流：<font color="blue"><%if(parcel.getType2()==0) {%>EMS<%}else{%>非EMS<%} %></font></strong></td> -->
  				<td><strong>作业人：<font color="blue"><%=StringUtil.convertNull(parcel.getStaffName())%></font></strong></td>
  				<td><strong>领取时间：<font color="blue"><%if(parcel.getReceiveDatetime()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(parcel.getReceiveDatetime(),19) %><%} %></font></strong></td>
  				<td><strong>完成时间：<font color="blue"><%if(parcel.getCompleteDatetime()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(parcel.getCompleteDatetime(),19) %><%} %></span></font></strong></td>
			  <td><a href="javascript:showPackage(<%=parcel.getId()%>);"><font color="blue">查看明细</font></a>
			  <%if(parcel.getStatus()==0){ %>
			   		<% if(group.isFlag(592)){%>
			   		<input  onClick="inputCode(<%=parcel.getId()%>,'all','')" style="color:red; font-size:14px" type="button" value="打单">
			   		<input  onClick="inputCodePda(<%=parcel.getId()%>,'all','')" style="color:red; font-size:14px" type="button" value="PDA打单">
			   		<%} %>
			<%}else{%>
			  	<menu style="display: block; width:70px;position: relative; text-align:left">
		            <label><input onClick="openPrintPage('',<%=parcel.getId()%>,'buda','')" style="color:blue; font-size:14px" type="button" value="补打">
		            <input onClick="openPrintPagePda('',<%=parcel.getId()%>,'buda','')" style="color:blue; font-size:14px" type="button" value="PDA补打"></label>
		        </menu>
			  <%}%>
  			</tr>
  			<tr>
  				<td><strong>PDA分拣量：<font color="blue"><%=parcel.getSortingCount() %></font></strong></td>
  				<td><strong>分播量：<font color="blue"><%=parcel.getCompleteCount()%></font></strong></td>
  				<td></td>
  				<td></td>
  				<td></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="parcel<%=parcel.getId() %>" style="<%=showDivNum==parcel.getId()?"":"display:none"%>">
  						<hr/>
  						
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							 <% if(group.isFlag(664)){%>
  							<tr>
  							     <td colspan="12" style="color: #333">   快递公司：<select name="deliver1<%=parcel.getId()%>">
		<option value="0">全部</option>
		<option value="9">广东省外</option>
		<option value="11">广东省速递局</option>
		<option value="10">广州宅急送</option>
		<option value="12">广州顺丰</option>
		<option value="13">深圳自建</option>
		<option value="14">路通速递</option>
	</select>&nbsp;&nbsp;&nbsp;
	   产品分类:<select name="parentId1<%=parcel.getId()%>" class="bd">
	<option value="0">全部</option>
	<logic:iterate id="productType" name="productTypeMap">
	<option value="<bean:write name="productType" property="key" />"><bean:write name="productType" property="value" /></option>
	</logic:iterate>
	<%if(batch.getStatus()!=SortingBatchBean.STATUS4){ %>
	<input type="button" name="Submit3"  onclick="submitValue(<%=parcel.getId()%>)" value="修改所选" onclick="return confirm('此操作可能会影响分拣波次');"></td>
	<%}else{ %>
	<input type="button" name="Submit3" value="修改所选" disabled="disabled"></td>
	<%} %>
						  </tr><%} %>
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号<input name="checkboxall<%=parcel.getId()%>" type="checkbox" value="" onclick="setAllCheck(document.forms[1], 'checkbox<%=parcel.getId()%>', this.checked);"></td>
  								<td>订单编号</td>
  								<td>出库单编号</td>
  								<td>分拣状态</td>
  								<td>快递公司</td>
  								<td>商品分类</td>
  								<td>SKU个数</td>
  								<td>商品编号</td>
  								<td>货位号</td>
  								<td>分拣人</td>
  								<td>分拣量</td>
  								<td>分播量</td>
  							</tr>
  							
  							
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%
  								SortingBatchOrderBean packageBean=(SortingBatchOrderBean)packageList.get(j);
  								int num =packageBean.getSortingBatchOrderProductList().size();
  								
  								%>
  							<tr bgcolor="#ffffff" align="center">
  							    <td rowspan="<%=num%>"><%=j+1 %><%if(packageBean.getStatus()!=3){ %><input type="checkbox" name="checkbox<%=parcel.getId()%>" id="checkbox" value=<%= packageBean.getOrderId()%>><%} %></td>
  								<td rowspan="<%=num%>"><%=packageBean.getOrderCode() %></td>
  								<td rowspan="<%=num%>"><%=packageBean.getOrderStockCode() %></td>
  								<td rowspan="<%=num%>"><%=packageBean.getStatusName(packageBean.getStatus())%></td>
  							    <td rowspan="<%=num%>"><%if(packageBean.getDeliver()==0){%>未分配<%}else{%><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"")%><%} %></td>
                                <td rowspan="<%=num%>"><%=voOrder.productTypeMap.get(packageBean.getOrderType()+"")%></td>
  							    <td rowspan="<%=num%>"><%=packageBean.getSortingBatchOrderProductList().size()%></td>
  								<td><%=((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getProductCode()%></td>
  								<td><%if(((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getCargoWholeCode()==null){%>&nbsp;<%}else{ %><%=((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getCargoWholeCode()%><%} %></td>
  							    <td><%if(((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getSortingUsername()==null){%>&nbsp;<%}else{ %><%=((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getSortingUsername()%><%} %></td>
  							     <td><%=((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getSortingCount()%><br></td>
  								<td><%=((SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(0)).getCompleteCount()%><br></td>
  							</tr>
  								
  							  	<%
  							  	if( num >= 2 ) {
  							  	for(int m=1;m<packageBean.getSortingBatchOrderProductList().size();m++){
  							  	SortingBatchOrderProductBean bean = (SortingBatchOrderProductBean)packageBean.getSortingBatchOrderProductList().get(m);%>
  							    <tr bgcolor="#ffffff" align="center">
  								<td><%=bean.getProductCode()%></td>
  								<td><%if(bean.getCargoWholeCode()==null){%>&nbsp;<%}else{ %><%=bean.getCargoWholeCode()%><%} %></td>
  							    <td><%if(bean.getSortingUsername()==null){%>&nbsp;<%}else{ %><%=bean.getSortingUsername()%><%} %></td>
  							     <td><%=bean.getSortingCount()%><br></td>
  								<td><%=bean.getCompleteCount()%><br></td>
  							   </tr> 
  							<%} }%>
  							  	
  								<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%}}} %>
</table>
</form>
<script>
function load(){
	 selectOption(document.getElementById("text") ,"<%=text%>");
	 selectOption(document.getElementById("deliver") ,"<%=deliver%>");
}
load();
</script>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
</html>