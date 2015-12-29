<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <%@ include file="/taglibs.jsp"%>
<%@ page import="adultadmin.action.vo.voUser" %><%@page import="adultadmin.service.AdminServiceImpl"%>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.action.barcode.FProductBarcodeAction" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>批量查询产品条形码</title>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
	%><script>alert("<%=tip%>");</script><%
}
String barcodes = (String)request.getParameter("barcodes");
if(barcodes == null)barcodes="";
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function checksubmit(){
	var reg=new RegExp("^(\\d)|(\\n)$");
	var barcodes = document.getElementById("barcodes");
	if(barcodes.value==""){
		alert("产品条码不能为空！");
		barcodes.focus();
		return false;
	}
	var str = barcodes.value.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>30){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && trim(str[i]).length>0)
				count++;
		}
	}
	if(count>30){
		alert("一次最多可扫描30个条码！");
		barcodes.focus();	
		return false;
	}
	return true;
}
function trim(str){   
	return str.replace(/^\s+|\s+$/g,"");   
}

window.onload=function (){
	document.getElementById("barcodes").focus();
};

// 提交表单打印条码
function onPrintSubmit(code,oriname,barcode){
	document.getElementById("codeId").value=code;
	document.getElementById("orinameId").value=oriname;
	document.getElementById("barcodeId").value=barcode;
	document.getElementById("printBarocdeFormId").submit();
}
</script>
</head>
<body>
<fieldset>
<legend>产品查询（批量条码）</legend>
<form action="<%=request.getContextPath()%>/admin/fproductBarcode.do" method="post" onsubmit="return checksubmit();">
<input type="hidden" name="actionFlag" value="serache"/>
  <table width="95%" cellpadding="0" cellspacing="0">
   <tr><td>产品条形码：</td>
   <td><textarea cols="50" rows="4" id="barcodes" name="barcodes"><%=barcodes%></textarea></td>
   </tr>
  </table>
<input type="submit" value="查询">
<br/>注：可连续扫描多个产品条码
</form>
</fieldset>
<br/>
<form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="40" align="center"><font color="#FFFFFF">选项</font></td>
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td width="80" align="center"><font color="#FFFFFF">条码</font></td>
              <td width="60" align="center"><font color="#FFFFFF">等级</font></td>
              <td align="left"><font color="#FFFFFF">名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
<!--              <td width="50" align="center"><font color="#FFFFFF">代理商</font></td>-->
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
              <td width="60" align="left"><font color="#FFFFFF">mmb产品页生成方式</font></td>
              <td width="60" align="left"><font color="#FFFFFF">yyt产品页生成方式</font></td>
              <td width="80" align="center"><font color="#FFFFFF">录入时间</font></td>
              <td width="50" align="center"><font color="#FFFFFF">查看评论</font></td>
              <td width="25" align="center"><font color="#FFFFFF">多图</font></td>
              <td width="50" align="center"><font color="#FFFFFF">小图</font></td>
              <td width="50" align="center"><font color="#FFFFFF">大图</font></td>
               <td width="50" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>           
<logic:present name="productList" scope="request"> 
<logic:iterate name="productList" id="item" > 
<%adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type='checkbox' name='id' value='<bean:write name="item" property="id" />'></td>
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />&simple=1" ><bean:write name="item" property="code" /></a></td>
		<td><%if(voItem.getProductBarcodeVO()!=null && voItem.getProductBarcodeVO().getBarcode()!=null){out.print(voItem.getProductBarcodeVO().getBarcode());}%></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
		<td align=right width="60"><bean:write name="item" property="price" />元</td>
		<td align=right width="60"><bean:write name="item" property="groupBuyPrice" />元</td>
		<td align=right width="60"><bean:write name="item" property="price2" />元</td>
<!--		<td align=left width="100"><bean:write name="item" property="proxyName" />(<bean:write name="item" property="proxysName" />)</td>-->
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<td align=left width="60"><a href="comments.do?productId=<bean:write name="item" property="id" />">查看(<bean:write name="item" property="commentCount" />)</a></td>
		<td align=left width="25"><a href="productpics.do?productId=<bean:write name="item" property="id" />">查看</a></td>
		<td width="50" align="center"><%if(voItem.getPic2()==null||voItem.getPic2().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic2()+"'><image border=0 src='"+voItem.getFullPic2()+"' width=20 height=20></a>");%></td>
		<td width="50" align="center"><%= voItem.getStock(0, 0) + voItem.getStock(1, 0) + voItem.getStock(2, 0) %></td>
		<td width="50" align="center"><%= voItem.getStock(0) + voItem.getStock(1) + voItem.getStock(2) + voItem.getLockCount(0) + voItem.getLockCount(1) + voItem.getLockCount(2) %></td>
		<td align="center"><%if(group.isFlag(51)){ %><a href="fProductMark.do?id=<bean:write name="item" property="id" />">编辑坏货/已返厂</a><%} %>
		<%if(group.isFlag(293) && voItem.getProductBarcodeVO()!=null && voItem.getProductBarcodeVO().getBarcode()!=null){ %><a href="javascript:void(0);" onclick="onPrintSubmit('<bean:write name="item" property="code" />','<bean:write name="item" property="oriname" />','<%=voItem.getProductBarcodeVO().getBarcode() %>'); return false;">打印条码</a><%} %>	

		</td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
   <form name="printBarocdeForm" id="printBarocdeFormId" action="barcodeManager/printProcBarcode.jsp" method="post"  target="_blank">
   		<input id="codeId" type="hidden"  name="code"/>
   		<input id="orinameId" type="hidden"  name="oriname"/>
   		<input id="barcodeId" type="hidden"  name="barcode"/>
   		<input type="hidden" name="pageTitle" value="打印产品条码"> 
   </form>
</body>
</html>