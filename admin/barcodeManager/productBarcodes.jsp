<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.action.vo.voUser" %><%@page import="adultadmin.service.AdminServiceImpl"%>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.action.vo.ProductBarcodeVO" %>
<%@ page import="adultadmin.action.vo.voProduct" %>
<%@ page import="adultadmin.util.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
String productCode = (String)request.getParameter("productCode");
String procBarcode = (String)request.getParameter("procBarcode");
String productName = (String)request.getParameter("productName");
String orinameProc = (String)request.getParameter("orinameProc");
String type = (String)request.getParameter("type");
if(productCode == null){
	productCode = "";
}
if(procBarcode == null){
	procBarcode = "";
}
if(productName == null){
	productName = "";
}
if(orinameProc == null){
	orinameProc = "";
}
if(type!=null && !type.trim().equals("")){
			if(productName != null && !productName.trim().equals("")){
				productName = Encoder.decrypt(productName);
			}
			if(orinameProc != null && !orinameProc.trim().equals("")){
				orinameProc = Encoder.decrypt(orinameProc);
			}
	}
%>
<html>
<title>买卖宝后台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script>
function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}

function querysubmint(){
	var productCodeObj = document.getElementById("productCodeId");
	var procBarcodeObj = document.getElementById("procBarcodeId");
	var productNameObj = document.getElementById("productNameId");
	var orinameProcObj = document.getElementById("orinameProcId");
	var reg=new RegExp("^\\d{5,15}$");
	if(trim(productCodeObj.value).length==0 && trim(procBarcodeObj.value).length==0
			&& trim(productNameObj.value).length==0 && trim(orinameProcObj.value).length==0){
		alert("对不起，请至少填写一个过滤条件。");
		return false;
	}
	if(trim(procBarcodeObj.value)!=""){
		if(procBarcodeObj.value.indexOf("'")!=-1){
			alert("条形码不允许输入单引号(')，请重新输入！");
			procBarcodeObj.focus();
			return false;
		}
	}
	return true;
	
}
function dBarcad(trid,id){
	var barcodeId = document.getElementById("barcodeId").value;
	var productId = document.getElementById("productId").value;
	var oldBarcode = document.getElementById("oldBarcode").value;
	var tr = trid;
	var para = "id="+id+"&pid="+productId+"&oldBarcode="+oldBarcode;
	if(confirm('是否确认删除?')){
		$.ajax({
			type:"POST", 
			data:para,
			url:"dproductBarcode.do", 
			success:function (date){
				if(date=="success"){
					document.getElementById("flag").innerHTML="<font size='2' color='green'>操作成功!</font>";
					$('#'+ tr).hide();
				}else{
					document.getElementById("flag").innerHTML="<font size='2' color='red'>操作失败!</font>";
				}
			}
		});
	}
}
function editProductBarcodes(pid,id){
	var parmas = ""
	var productCode=document.getElementById("productCodeId").value;  
	var procBarcode =document.getElementById("procBarcodeId").value; 
	var productName = document.getElementById("productNameId").value; 
	var orinameProc = document.getElementById("orinameProcId").value; 
	if(productCode!=null && productCode!="" && productCode!="undefind")
		parmas+="&productCode="+productCode;
 	if(procBarcode!=null && procBarcode!="" && procBarcode!="undefind")		
		parmas+="&procBarcode="+procBarcode;
	if(productName!=null && productName!="" && productName!="undefind")
		parmas+="&eProductName="+encodeURIComponent(encodeURIComponent(productName));
	if(orinameProc!=null && orinameProc!="" && orinameProc!="undefind")
		parmas+="&eOrinameProc="+encodeURIComponent(encodeURIComponent(orinameProc));
	document.location.href="fproductBarcode.do?pid="+pid+"&id="+id+parmas ;
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="/header.jsp"%>

<fieldset>
<legend>查找-修改商品条码</legend>
<form name="searchBarcode" method="POST" action="<%=request.getContextPath()%>/admin/fproductBarcode.do" onsubmit="return querysubmint();">
	产品编号：<input type="text" name="productCode" id="productCodeId" value='<%=productCode%>'/>
	&nbsp;产品条形码：<input type="text" name="procBarcode" id="procBarcodeId" value='<%=procBarcode%>'/><br/>
	小店名称：<input type="text" name="productName" id="productNameId" value='<%=productName%>'/>
	&nbsp;原名称：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="orinameProc" id="orinameProcId" value='<%=orinameProc%>'/>
	<input type="submit" value="查 询"/>
</form>
</fieldset>	
	<div align="left" id="flag"></div>
    <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
              <tr bgcolor="#4688D6">
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td width="60" align="center"><font color="#FFFFFF">条码</font></td>
              <td width="60" align="center"><font color="#FFFFFF">条码状态</font></td>
              <td align="left"><font color="#FFFFFF">小店名称</font></td>
              <td align="left"><font color="#FFFFFF">原名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">市场价</font></td>
              <td width="50" align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="50" align="center"><font color="#FFFFFF">状态</font></td>
              <td width="60" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>           
<logic:present name="product" scope="request"> 
<% List barcodeList = (List)request.getAttribute("barcodeList"); 
	voProduct vp = (voProduct)request.getAttribute("product");
	boolean isDisable=false;
	ProductBarcodeVO barcodeVo=null;
	if(barcodeList!=null && barcodeList.size()>0){
		for(int i=0;i<barcodeList.size();i+=1){
			barcodeVo = (ProductBarcodeVO)barcodeList.get(i);
%>
		<tr bgcolor='#F8F8F8' id='<%=barcodeVo.getBarcode()%>'>
		<td align=left width="60"><a href="fproduct.do?id=<%=vp.getId() %>&simple=1&flag=0" ><%=vp.getCode() %></a></td>
		<td align='center'><%if(barcodeVo.getBarcodeStatus()==1){out.print(barcodeVo.getBarcode());
		if(i==barcodeList.size()-1)isDisable=true;}
		else{%> <a  style="cursor:pointer;" onclick="editProductBarcodes(<%=vp.getId()%>,<%=barcodeVo.getId()%>)"><%=barcodeVo.getBarcode()%></a><%}%>
		</td>
		<td align='left'><%=barcodeVo.getBarcodeStatus()==1?"无效":"有效" %></td>
		<td align='left'><%=vp.getName() %></td>
		<td align='left'><%=vp.getOriname() %></td>
		<td align=right width="60"><%=vp.getPrice() %>元</td>
		<td align=right width="60"><%=vp.getPrice2() %>元</td>
		<td align=left width="50"><%=vp.getProxyName() %>(<%=vp.getProxysName() %>)</td>
		<td align=right width="40"><%=vp.getStatusName() %></td>
		<td align=right width="60"><input type="hidden" id="barcodeId" value="<%=barcodeVo.getId()%>"/><input type="hidden" id="productId" value="<%=vp.getId()%>"/><input type="hidden" id="oldBarcode" value="<%=barcodeVo.getBarcode()%>"/>
		<%if(barcodeVo.getBarcodeStatus()==1){%><a href='javascript:void(0)' onclick="dBarcad('<%=barcodeVo.getBarcode()%>','<%=barcodeVo.getId()%>')">删除条码</a><%}
		else{%><a  style="cursor:pointer;" onclick="editProductBarcodes(<%=vp.getId()%>,<%=barcodeVo.getId()%>)">编辑条码</a><%}%>
		</td>
		</tr>
	<%}}if(barcodeList==null || barcodeList.size()==0 ||isDisable){ %>
		<tr bgcolor='#F8F8F8'>
		<td align="left" width="60"><a href="fproduct.do?id=<%=vp.getId() %>&simple=1&flag=0" ><%=vp.getCode() %></a></td>
		<td align="center"><a  style="cursor:pointer;" onclick="editProductBarcodes(<%=vp.getId()%>,-1)">添加</a></td>
		<td align='left'>&nbsp;</td>
		<td align='left'><%=vp.getName() %></td>
		<td align='left'><%=vp.getOriname() %></td>
		<td align=right width="60"><%=vp.getPrice() %>元</td>
		<td align=right width="60"><%=vp.getPrice2() %>元</td>
		<td align=left width="50"><%=vp.getProxyName() %>(<%=vp.getProxysName() %>)</td>
		<td align=right width="40"><%=vp.getStatusName() %></td>
		<td align=right width="60"><a  style="cursor:pointer;" onclick="editProductBarcodes(<%=vp.getId()%>,-1)">编辑条码</a></td>
		</tr>
		<%}%>
</logic:present> 
</table>
      
</body>
</html>