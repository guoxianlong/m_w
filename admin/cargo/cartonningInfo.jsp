<%@ page import="mmb.stock.cargo.CartonningInfoBean"%>
<%@ page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.util.Encoder"%>
<%@ page import ="adultadmin.util.db.DbOperation"%>
<%@ page import="adultadmin.service.infc.IStockService"%>
<%@ page import="adultadmin.service.ServiceFactory"%>
<%@ page import="adultadmin.service.infc.IBaseService"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>装箱记录管理</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE6 {color: #FF0000}
.STYLE7 {color: #00CC00}
.STYLE9 {color: #000000}
.STYLE10 {color: #CCCCCC}
-->
</style>
</head>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List cartonningList = (List)request.getAttribute("cartonningList");
Map deliverMapAll = voOrder.deliverMapAll;
PagingBean paging = (PagingBean) request.getAttribute("paging");
String url = (String)request.getAttribute("url");
String select=StringUtil.convertNull(request.getParameter("select"));
String[] status=(String[])request.getAttribute("status");
%>
<script>
function IsNum(num){
	  var reNum=/^\d*$/;
	  return(reNum.test(num));
	}
function check(){
	var mailingBatchCode=trim(document.getElementById("mailingBatchCode").value);
	var parcelCode=trim(document.getElementById("parcelCode").value);
	
	document.getElementById("mailingBatchCode2").value=mailingBatchCode;
	document.getElementById("parcelCode2").value=parcelCode;
}

function focusmailingBatchCode(){
	var mailingBatchCode=document.getElementById("mailingBatchCode");
}
function blurmailingBatchCode(){
	var mailingBatchCode=document.getElementById("mailingBatchCode");
}
function focusparcelCode(){
	var parcelCode=document.getElementById("parcelCode");
}
function blurparcelCode(){
	var parcelCode=document.getElementById("parcelCode");
}
function submitOrderCode(){
	document.getElementById("mailingBatchCode").focus();
	return false;
}
function submitmailingBatchCode(){
	document.getElementById("parcelCode").focus();
	return false;
}
function submitparcelCode(){
	document.getElementById("mailingBatchCode2").value=document.getElementById("mailingBatchCode").value;
	document.getElementById("parcelCode2").value=document.getElementById("parcelCode").value;
	return false;
}
function checksubmit(){
	with(add){
		if(productCode.value.length==0||productCode.value.replace(/\s/g,"")==""){
			alert("商品编号不能为空")
			return false;
		}
		if(!IsNum(count.value)||count.value==0||count.value.length==0||count.value.replace(/\s/g,"")==""){
			alert("装箱数量不正确")
			return false;
		}
		if(count.value.length>9){
			alert("装箱数量长度不能大于9")
			return false;
		}
		if(cause.value==-1){
			alert("请选择装箱原因")
			return false;
		}
		
	}
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">

</script>
<body bgcolor="#ffcc00" onLoad="document.getElementById('mailingBatchCode').focus();">
<p><font size="4">&nbsp;&nbsp;&nbsp;<strong>装箱记录管理</strong></font></p>
<fieldset style="width:auto;"><legend>创建装箱记录</legend>
<table width="800" border="0">
  <tr>
    <td align="left" valign="baseline">
    	<form name="form1" method="post" action=""onSubmit="return submitmailingBatchCode();">
  	       	   商品编号/商品条码:<input id="mailingBatchCode" type="text" onFocus="focusmailingBatchCode();" onBlur="blurmailingBatchCode();"/>
        </form>
    </td>
    <td align="left" valign="baseline">
    	<form name="form2" method="post" action=""  onSubmit="return submitparcelCode();">
     		&nbsp;&nbsp;装箱数量:<input id="parcelCode" type="text" onFocus="focusparcelCode();" onBlur="blurparcelCode();"/>&nbsp;&nbsp;
        </form>
    </td>
    <td align="left" valign="baseline">
    	<form id="add" action=<%=request.getContextPath()+"/admin/cartonningInfoAction.do?method=createCartonningInfo" %> method="post" onSubmit="return checksubmit();">
        	&nbsp;&nbsp;装箱原因:
      		<select name="cause" id="select">
       			 <option value=-1>请选择</option>
        		 <option value=0>入库作业</option>
       			 <option value=1>仓内作业</option>
      			 <option value=2>盘点抽检</option>
      			 <option value=3>日常理货</option>
      			 <option value=4>其他原因</option>
     		</select>&nbsp;&nbsp;
 			<input type="hidden" id="mailingBatchCode2" name="productCode" value=""/>
			<input type="hidden" id="parcelCode2" name="count" value=""/>
			<input type="submit" value="创建装箱记录" onClick="return check();"/>
 			
 			
        </form>
		<script type="text/javascript">  
    		selectOption(document.getElementById("select") ,"<%=select%>");
 		</script>&nbsp;&nbsp;
    </td>
  </tr>
</table></fieldset>
<fieldset style="width:auto;"><legend>装箱记录列表</legend>
<form action=<%=request.getContextPath()+"/admin/cartonningInfoAction.do?method=cartonningInfo" %> method="post" >
<table>
	<tr>
		<td  align="right">装箱单编号:</td><td><input name="select" type="text"></td>
		<td  align="right"> 产品编号：</td><td><input name="productCode" type="text"></td>
		<td  align="right">生成人：</td><td><input name="userName" type="text"></td>
		<td  align="right">状态：</td>
		<td>
			<input type="checkbox" name="status" value="0"/>未打印&nbsp;
			<input type="checkbox" name="status" value="1"/>已生效&nbsp;
			<input type="checkbox" name="status" value="2"/>已作废
		</td>
	</tr>
	<tr>
		<td  align="right">生成时间：</td><td><input name="startTime" type="text" onclick="WdatePicker();"></td>
		<td align="center"> 到</td><td><input name="endTime" type="text" onclick="WdatePicker();"></td>
		<td>&nbsp;</td><td align="right"><input name="Submit" type="Submit" value="搜索"><br></td>
	</tr>
</table>
<br>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">装箱单编号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">商品编号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">装箱数量</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">货位号</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">创建时间</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">责任人</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">状态</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">操作</font></span></div></td>
  </tr>
  <%if(cartonningList!=null){
		for (int i = 0; i < cartonningList.size(); i++) {
			CartonningInfoBean bean = (CartonningInfoBean)cartonningList.get(i);
		
	    %>
  <tr  bgcolor="#FFFFCC">
    <td><div align="center"><%=paging.getCountPerPage()*(paging.getCurrentPageIndex())+i+1%></div></td>
    <td><div align="center"><%=bean.getCode()%></div></td>
    <td><div align="center"><%if(group.isFlag(549)){ %><a href="fproduct.do?id=<%=bean.getProductBean().getProductId()%>" target="blank"><%=bean.getProductBean().getProductCode()%></a><%}else{ %><%=bean.getProductBean().getProductCode()%><%} %></div></td>
    <td><div align="center"><%=bean.getProductBean().getProductCount()%></div></td>
    <td><div align="center"><%=bean.getCargoWholeCode()%></div></td>
    <td><div align="center"><%=bean.getCreateTime().substring(0, 19) %></div></td>
    <td><div align="center"><%=bean.getName()%></div></td>
    <td><div align="center"> <%if(bean.getStatus()==0){%><span class="STYLE6"><%=bean.getStatusName()%></span><%}%><%if(bean.getStatus()==1){%><span class="STYLE7"><%=bean.getStatusName()%></span><%} %><%if(bean.getStatus()==2){%><%=bean.getStatusName()%><%} %></div></td>
    <td><div align="center">
    	<%if(bean.getStatus()!=2){%><a href='<%=request.getContextPath()%>/admin/cartonningInfoAction.do?method=PrintCartonningInfo&id=<%=bean.getId()%>' target="blank" class="STYLE9">打印装箱单</a>&nbsp;<a href='<%=request.getContextPath()%>/admin/cartonningInfoAction.do?method=cancelCartonningInfo&id=<%=bean.getId()%>' class="STYLE9" onClick="return confirm('确定作废<%=bean.getCode()%>记录?');"><%if(group.isFlag(556)){ %>作废<%} %></a>
        <%}else{ %>
        <span class="STYLE10">打印装箱单&nbsp;<%if(group.isFlag(556)){ %>作废<%} %></span>
        <%} %>
        <%if(bean.getCargoId()==0&&bean.getStatus()!=2){ %>
       		<a href="<%=request.getContextPath()%>/admin/cartonningInfoAction.do?method=cartonningCargo&code=<%=bean.getCode() %>" target="_blank">关联货位</a>
        <%}else{ %>
        	 <span class="STYLE10">关联货位</span>
        <%} %>
         <%if(bean.getStatus()!=2 && bean.getCargoId()!=0&&group.isFlag(557)){ %>
             <a href="<%=request.getContextPath()%>/admin/cartonningInfoAction.do?method=cartonningCargo&code=<%=bean.getCode() %>&flag=1" target="_blank">修正货位</a>
         <%} else{%> <span class="STYLE10">修正货位</span><%} %>
        </div>
        </td>
  </tr>
  <%}}%>
</table>
<br>
</form></fieldset>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%}%>
<script type="text/javascript">
		<%if(status!=null){%>
			<%for(int i=0;i<status.length;i++){%>
				document.getElementsByName("status")[<%=status[i]%>].checked=true;
			<%}%>
		<%}%>
	</script>
</body>
</html>