<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="cache.CatalogCache"%>
<%@ page import="adultadmin.action.vo.voCatalog"%>
<%@ page import="java.util.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<html>
<title>货位绑定产品——查询产品</title>
<script>
function submitProduct(){
	var selected=false;
	for(var i=0;i<document.getElementsByName("id").length;i++){
		var product=document.getElementsByName("id")[i];
		if(product.checked==true){
			selected=true;
			window.opener.document.getElementById("productCode").value=document.getElementsByName("id")[i].value;
			if(confirm("选择了产品："+document.getElementsByName("id")[i].value+"\r\r"+"单击“确定”将选中该产品并关闭本页面  ")){
				window.close();
			}
		}
	}
	if(selected==false){
		alert("请先选择产品！");
	}
}

//提交表单打印条码
function onPrintSubmit(code,oriname,barcode){
	document.getElementById("codeId").value=code;
	document.getElementById("orinameId").value=oriname;
	document.getElementById("barcodeId").value=barcode;
	document.getElementById("printBarocdeFormId").submit();
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
<body>
<%@include file="../../header.jsp"%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form method="post" action="cargoInfo.do?method=selectProduct">
<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String barcode = (String)request.getParameter("barcode");
if(barcode == null)barcode="";
String name = request.getParameter("name");
if(name == null)name="";
name = StringUtil.toWml(name);
String price = request.getParameter("price");
if(price == null)price="";
price = StringUtil.toWml(price);
String minPrice = (String)request.getParameter("minPrice");
if(minPrice == null)minPrice="";
String maxPrice = (String)request.getParameter("maxPrice");
if(maxPrice == null)maxPrice="";
int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
%>
产品编号：<input type=text name="code" size="20" value="<%=code%>"><br>
商品条码：<input type=text name="barcode" value="<%=barcode%>" ><br>
产品名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br>
产品价格：<input type=text name="price" size="20" value="<%=price%>"><br>
价格：从<input type=text name="minPrice" size="20" value="<%=minPrice%>">到<input type=text name="maxPrice" size="20" value="<%=maxPrice%>"><br>
<%if(group.isFlag(143))  {%>
一级分类：<select name="parentId1" class="bd" style="width:180" onChange="sredirect(this.options.selectedIndex - 1);">
			<option value="0"></option>								  
			 <%
				HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
		</select><br/>
二级分类：<select name="parentId2" class="bd" style="width:180">
	<option value="0"></option>
</select><br/>

<script>
<!--
var spt = document.forms[0].parentId2
function sredirect(x){
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null;
  if(x < 0){
	return;
  }
  spt.options[0]=new Option("", "0");
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i + 1]=new Option(spts[x][i].text, spts[x][i].value)
       //alert(x+'--'+spts[x][i].text+'--'+spts[x][i].value);
  }
  spt.options[0].selected=true
}

selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex - 1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
</script>
<%} %>
<input type=submit value="查询产品">
</form>
</td></tr>
</table>
</td></tr></table>

          <br><form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="40" align="center"><font color="#FFFFFF">选项</font><br/></td>
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">团购价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">市场价</font></td>
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
              <%--
              <td width="80" align="center"><font color="#FFFFFF">录入时间</font></td>
              --%>
              <td width="60" align="center"><font color="#FFFFFF">查看评论</font></td>
              <td width="25" align="center"><font color="#FFFFFF">多图</font></td>
              <%--
              <td width="50" align="center"><font color="#FFFFFF">小图</font></td>
              --%>
              <td width="50" align="center"><font color="#FFFFFF">大图</font></td>
              <%--
              <td width="50" align="center"><font color="#FFFFFF">小小图</font></td>
              --%>
              <td width="50" align="center"><font color="#FFFFFF">可发货总数</font></td>
              <td width="50" align="center"><font color="#FFFFFF">库存总数</font></td>
              <td width="50" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>           
<logic:present name="productList" scope="request"> 
<logic:iterate name="productList" id="item" > 
<%adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type='radio' name='id' value='<bean:write name="item" property="code" />'></td>
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />&simple=1" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a><font color="red">标准化信息</font><%
		int standardStatus = voItem.getId();
		if(standardStatus == 0){%>无<%} else if(standardStatus == 1){%><font color="red">主</font><%} else if(standardStatus == 2){%><font color="blue">子</font><%}
		%></td>
		<td align=right width="60"><bean:write name="item" property="price" />元</td>
		<td align=right width="60"><bean:write name="item" property="groupBuyPrice" />元</td>
		<td align=right width="60"><bean:write name="item" property="price2" />元</td>
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<%--
		<td align=right width="80"><bean:write name="item" property="createDatetime" /></td>
		--%>
		<td align=left width="60"><a href="comments.do?productId=<bean:write name="item" property="id" />">查看(<bean:write name="item" property="commentCount" />)</a></td>
		<td align=left width="25"><a href="productpics.do?productId=<bean:write name="item" property="id" />">查看</a></td>
		<%--
		<td width="50" align="center"><%if(voItem.getPic()==null||voItem.getPic().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic()+"'><image border=0 src='"+voItem.getFullPic()+"' width=20 height=20></a>");%></td>
		--%>
		<td width="50" align="center"><%if(voItem.getPic2()==null||voItem.getPic2().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic2()+"'><image border=0 src='"+voItem.getFullPic2()+"' width=20 height=20></a>");%></td>
		<%--
		<td width="50" align="center"><%if(voItem.getPic3()==null||voItem.getPic3().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic3()+"'><image border=0 src='"+voItem.getFullPic3()+"' width=20 height=20></a>");%></td>
		--%>
		<td width="50" align="center"><%= voItem.getStock(0, 0) + voItem.getStock(1, 0) + voItem.getStock(2, 0) %></td>
		<td width="50" align="center"><%= voItem.getStock(0) + voItem.getStock(1) + voItem.getStock(2) + voItem.getLockCount(0) + voItem.getLockCount(1) + voItem.getLockCount(2) %></td>
		<td align="center"><%if(group.isFlag(51)){ %><a href="fProductMark.do?id=<bean:write name="item" property="id" />">编辑坏货/已返厂</a><%} %>
		<%if(group.isFlag(293)&& voItem.getProductBarcodeVO()!=null && voItem.getProductBarcodeVO().getBarcode()!=null){ %> |<a href="javascript:void(0);" onclick="onPrintSubmit('<bean:write name="item" property="code" />','<bean:write name="item" property="oriname" />','<%=voItem.getProductBarcodeVO().getBarcode() %>'); return false;">打印条码</a><%} %>	
		</td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0">
            <tr>
              <td height="35">
              <input type="button" value="选中该产品" onClick="submitProduct();">
              </td>
            </tr>
          </table>
          <br>   
   <form name="printBarocdeForm" id="printBarocdeFormId" action="barcodeManager/printProcBarcode.jsp" method="post"  target="_blank">
   		<input id="codeId" type="hidden"  name="code"/>
   		<input id="orinameId" type="hidden"  name="oriname"/>
   		<input id="barcodeId" type="hidden"  name="barcode"/>
   		<input type="hidden" name="pageTitle" value="打印产品条码"> 
   </form>
</body>
</html>