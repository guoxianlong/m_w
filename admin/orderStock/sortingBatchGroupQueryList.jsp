<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.order.OrderStockProductBean"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@ page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.util.PageUtil"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="cache.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*, adultadmin.action.vo.voCatalog, adultadmin.util.*" %>
<%@ page import="mmb.stock.cargo.CargoDeptAreaService"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>

<html>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List parcelList=(List)request.getAttribute("list");
PagingBean pageBean=(PagingBean)request.getAttribute("paging");
String batchId=(String)request.getParameter("batchId");
String count=(String)request.getAttribute("count");
String count1=(String)request.getAttribute("count1");
String count2=(String)request.getAttribute("count2");
DecimalFormat dcmFmt = new DecimalFormat("0.00");
String code=request.getParameter("code");
String queryType=request.getParameter("queryType");
PagingBean paging = (PagingBean) request.getAttribute("paging");
voUser adminUser = (voUser)session.getAttribute("userView");
String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
String startTime = StringUtil.convertNull(request.getParameter("startTime"));
String endTime = StringUtil.convertNull(request.getParameter("endTime"));
String storage = (String)request.getParameter("storage");
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
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script type="text/javascript">
		function checksubmit(){
			var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
			var startTime = document.getElementById("startTime").value;
			var endTime = document.getElementById("endTime").value;
			var nDay_ms = 24*60*60*1000;
			var reg = new RegExp("-","g");
			var startDay = new Date(startTime.replace(reg,'/'));
			var endDay = new Date(endTime.replace(reg,'/'));
			var nDifTime = endDay.getTime()- startDay.getTime();
			if(nDifTime < 0){
				alert("起始日期不能大于结束日期！");
		    	return false;
			}
		    var nDifDay=Math.floor(nDifTime/nDay_ms);
		    if(nDifDay > 7){
		    	alert("日期间隔不能大于7天！");
		    	return false;
		    }
			if(startTime.length!=0&&endTime.length==0 ){
				 alert("起始时间,请输入完整时间段")
				 return false;
			}
	        if(startTime.length==0&&endTime.length!=0 ){
	           	 alert("结束时间,请输入完整时间段")
				 return false;
			}
	        if(startTime.length!=0 && endTime.length!=0){
				 if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
				     alert("请正确填写起始时间格式")
					 return false;
				 } 
				
				 if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
					 alert("请正确填写截止时间格式")
					 return false;
				 } 
	        }
			return true;
	   }

<%if(request.getAttribute("tip")!=null){%>
alert("<%=request.getAttribute("tip")%>");
<%}%>
function showPackage(id){
	$("#parcel"+id).slideToggle();
}
  function showPrintButtonDiv(id){
  	$("#"+id).slideToggle(50);
  }
  function inputCode(sortingBatchGroupId,printType,selectedOrder){
  	  var code = window.prompt("员工号:","");
  	  if(code==""){
	 	 inputCode(sortingBatchGroupId,printType,selectedOrder);
	  }else if(code){
	  	openPrintPage(code,sortingBatchGroupId,printType,selectedOrder);
	  }
  }
  function openPrintPage(userCode,sortingBatchGroupId,printType,selectedOrder){
	 if( window.confirm("是否继续操作")){
		 window.open('sortingAction.do?method=sortingBatchGroupPrintLine&pageFrom=sortingBatchGroupQueryList&userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType='+printType+'&selectedOrder='+selectedOrder+'&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex"))%>');
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
<fieldset>
 <form  method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchGroupQueryList" onSubmit="return checksubmit();">
   <div align="center">
     <select name="storage" id="storage">
          	<%if(areaList!=null&&areaList.size()>0){%>
          		<%if(areaList.size()>1){ %>
          		<%for(int j=0;j<areaList.size();j++){%>
					<option value="<%=areaList.get(j)%>"><%=ProductStockBean.areaMap.get

                (Integer.valueOf(areaList.get(j).toString()))%></option>
				<%}}} %>
		  </select>&nbsp;&nbsp;&nbsp;
     <input type=text name="startTime" style="height:22px;font-size:18px;" size="10"
				    value="<%=startTime%>" onClick="SelectDate(this,'yyyy-MM-dd');"/>
     <font size="4.5">至</font>
     <input type=text name="endTime" size="10" style="height:22px;font-size:18px;" value="<%=endTime%>" 
				    onclick="SelectDate(this,'yyyy-MM-dd');"/>&nbsp;&nbsp;
     <select name="queryType" id="queryType" style="height:22px;font-size:15px;">
       <option value="1">订单号</option>
       <option value="2">波次号</option>
       <option value="3">分拣员工号</option>
       <option value="4">分播员工号</option>
      </select>
     <input style="height:22px;font-size:18px;" name="code" id="code" type="text" <%if(code!=null&&code.length()>0) {%>value="<%=code %>"<%}else{%>value="请输入..."<%} %> size="40" width="10"onfocus="if(this.value=='请输入...'){this.value=''}"/>
     &nbsp;
     <input type="submit" style="height:22px;font-size:18px;" name="button" id="button" value="查询" />
   </div>
</form>
</fieldset>
<input type="hidden" name="flag" value="1">
<input type="hidden" name="showDivNum" value="" id="showDivNum">
<table width="99%" border="0" cellspacing="10">
  <%

	int showDivNum = StringUtil.parstInt(request.getParameter("showDivNum"));

  if(parcelList!=null){
  for(int i=0;i<parcelList.size();i++){ %>
  	<%SortingBatchGroupBean parcel=(SortingBatchGroupBean)parcelList.get(i); %>
  	<%List packageList=parcel.getOrderList(); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
   				<td height="30"><strong>&nbsp;&nbsp;分拣波次号：<font color="blue"><%=StringUtil.convertNull(parcel.getCode()) %></font></strong></td>
   				<td><strong>作业仓：<font color="blue"><%=StringUtil.convertNull(parcel.getStorageName()) %></font></strong></td>
   				<td><strong>SKU数：<font color="blue"><%=parcel.getSkuCount() %></font></strong></td>
    			<td><strong>商品件数：<font color="blue"><%=parcel.getProductCount() %></font></strong></td>
    			<td><strong>订单总数：<font color="blue"><%=parcel.getOrderCount()%></font></strong></td>
   				<td><strong>状态：<font color="blue"><%=StringUtil.convertNull(parcel.getStatusName()) %></font></strong></td>
   				<td><a href="javascript:showPackage(<%=parcel.getId()%>);"><font color="blue">查看明细</font></a></td>
            </tr>
			  <tr>
			    <td height="30"><strong>&nbsp;&nbsp;领取时间：<font color="blue"><%if(parcel.getReceiveDatetime()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(parcel.getReceiveDatetime(),19) %><%} %></font></strong></td>
			    <td colspan="3"><strong>完成时间：<font color="blue"><%if(parcel.getCompleteDatetime()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(parcel.getCompleteDatetime(),19) %><%} %></font></strong></td>
			    <td><strong>分拣员：<font color="blue"><%=StringUtil.convertNull(parcel.getStaffName())%></font></strong></td>
			    <td>&nbsp;</td>
			    <td colspan="2"></td>
			  </tr>
			  <tr>
			    <td height="30"><strong>&nbsp;&nbsp;分播时间：<font color="blue"><%if(parcel.getReceiveDatetime2()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(parcel.getReceiveDatetime2(),19) %><%} %></font></strong></td>
			    <td colspan="3"><strong>结批时间：<font color="blue"><%if(parcel.getCompleteDatetime2()==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(parcel.getCompleteDatetime2(),19) %><%} %></font></strong></td>
			    <td><strong>分播员：<font color="blue"><%=StringUtil.convertNull(parcel.getStaffName2())%></font></strong></td>
			    <td><strong>分播耗时：<font color="blue"><%if(parcel.getSecondeSortingTime()!=0){%><%=parcel.getSecondeSortingTime()%>&nbsp;分钟<%} %></font></strong></td>
			    <td colspan="2"> <%if(parcel.getStatus()==0){ %>

			   <% if(group.isFlag(592)){%>	<input  onClick="inputCode(<%=parcel.getId()%>,'all','')" style="color:red; font-size:14px" type="button" value="打单"><%} %>
			  <%}else{%>
		            <input onClick="openPrintPage('',<%=parcel.getId()%>,'buda','')" style="color:blue; font-size:14px" type="button" value="补打">
			   <%}%></td>
			  </tr>
  			<tr>
  				<td colspan="4">
  					<div id="parcel<%=parcel.getId() %>" style="<%=showDivNum==parcel.getId()?"":"display:none"%>">
  						<hr/>
  						
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号<input name="checkboxall<%=parcel.getId()%>" type="checkbox" value="" onclick="setAllCheck(document.forms[1], 'checkbox<%=parcel.getId()%>', this.checked);"></td>
  								<td>订单编号</td>
  								<td>分拣状态</td>
  								<td>快递公司</td>
  								<td>商品分类</td>
  								<td>SKU个数</td>
  								<td>商品编号</td>
  								<td>货位号</td>
  							</tr>
  							
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%SortingBatchOrderBean packageBean=(SortingBatchOrderBean)packageList.get(j);
  								int num =packageBean.getProductList().size();%>
  							<tr bgcolor="#ffffff" align="center">
  							    <td rowspan="<%=num%>"><%=j+1 %><%if(packageBean.getStatus()!=3){ %><input type="checkbox" name="checkbox<%=parcel.getId()%>" id="checkbox" value=<%= packageBean.getOrderId()%>><%} %></td>
  								<td rowspan="<%=num%>"><%=packageBean.getOrderCode() %></td>
  								<td rowspan="<%=num%>"><%=packageBean.getStatusName(packageBean.getStatus())%></td>
  							    <td rowspan="<%=num%>"><%if(packageBean.getDeliver()==0){%>未分配<%}else{%><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"")%><%} %></td>
                                <td rowspan="<%=num%>"><%=voOrder.productTypeMap.get(packageBean.getOrderType()+"")%></td>
  							    <td rowspan="<%=num%>"><%=packageBean.getProductList().size()%></td>
  								<td><%=((OrderStockProductBean)packageBean.getProductList().get(0)).getProductCode()%></td>
  								<td><%if(((OrderStockProductBean)packageBean.getProductList().get(0)).getCargoCode()==null){%>&nbsp;<%}else{ %><%=((OrderStockProductBean)packageBean.getProductList().get(0)).getCargoCode()%><%} %></td>
  							</tr>
  							<%for(int k=1;k<packageBean.getProductList().size();k++){
  								OrderStockProductBean bean = (OrderStockProductBean)packageBean.getProductList().get(k);%>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=bean.getProductCode()%><br></td>
  								<td><%if(bean.getCargoCode()!=null){ %><%=bean.getCargoCode()%><%} %></td>
  								<%} %>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%}}%>
</table>
<script>
function load(){
	 selectOption(document.getElementById("queryType") ,"<%=queryType%>");
	 selectOption(document.getElementById("code") ,"<%=code%>");
	 selectOption(document.getElementById("storage") ,"<%=storage%>");
	 selectOption(document.getElementById("endTime") ,"<%=endTime%>");
	 selectOption(document.getElementById("startTime") ,"<%=startTime%>");
}
load();
</script>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
</html>