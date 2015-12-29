<%@page import="adultadmin.action.vo.voSelect"%>
<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="cache.CatalogCache"%>
<%@ page import="adultadmin.action.vo.voCatalog"%>
<%@ page import="cache.ProductLinePermissionCache"%>
<%@ page import="java.util.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
List statusList = (List) request.getAttribute("statusList");
List proxyList = (List) request.getAttribute("proxyList");
voSelect productStatusBean = null;
String[] statuss = request.getParameterValues("product_status");
String proxy = request.getParameter("proxy");
String listSum = (String)request.getAttribute("listSum");
String proxyName = (String)request.getAttribute("proxyName");
String status = "";
if(statuss!=null){
	for(int i=0;i<statuss.length;i++){
		status = status + statuss[i]+",";
	}

}
%>
<html>
<title>小店后台管理 - 搜索产品</title>
<script>
function excel_product()
{
	document.productForm.action="searchproduct.do?forward=2Excel";
	return document.productForm.submit();
}
//导出所有搜索出来的商品
function excel_allProduct(){
    var list = document.getElementsByName("id");
    var idList="";
    for(var i=0;i<list.length-1;i++){
		if(idList==""){
			idList=list[i].value;
		}else{
			idList+=","+list[i].value
		}
    }
    var idElement=document.getElementById("importProductIds");
    idElement.value=idList;
	//window.location.href="searchproduct.do?forward=2Excel&id="+idList;
	document.importProducts.submit();
}

//提交表单打印条码
function onPrintSubmit(code,oriname,barcode){
	document.getElementById("codeId").value=code;
	document.getElementById("orinameId").value=oriname;
	document.getElementById("barcodeId").value=barcode;
	document.getElementById("printBarocdeFormId").submit();
}

function mProductRank(){
	if(confirm('确认要修改选中产品的等级吗?')) {
		document.productForm.action="./productProperty/mProductRank.jsp";
		document.productForm.target="_blank";
		document.productForm.submit();
		document.productForm.target="";
	}
}
function checkAllRank(checkform, checkbox , checked){
	var elements = document.getElementsByName(checkbox);
	for(i=0;i<elements.length;i++) {
		if(elements[i].type == "checkbox"){
			var pId = elements[i].value;
			var rankInput = document.getElementById(pId + "rank");
			if(rankInput != null){
				if(checked){
					rankInput.disabled = false;
				} else {
					rankInput.disabled = true;
				}
			}
		}
	}
}
function checkRank(objId){
	if(objId != null){
		var pId = objId.value;
		var rankInput = document.getElementById(pId + "rank");
		if(rankInput != null){
			if(objId.checked){
				rankInput.disabled = "";
			} else {
				rankInput.disabled = "disabled";
			}
		}
	}
}
function getDateFromString(strDate){
    var arrYmd   =  strDate.split("-");
    var numYear  =  parseInt(arrYmd[0],10);
    var numMonth =  parseInt(arrYmd[1],10)-1;
    var numDay   =  parseInt(arrYmd[2],10);
    var leavetime=new Date(numYear,  numMonth,  numDay);
    return leavetime;

}
function checksubmit(){

	var startTime = document.getElementById("startTime").value;
	var endTime = document.getElementById("endTime").value;
	
	var productId=trim(document.getElementById("productId").value);
	if(productId!=""){
	 	var patrn=/^\d+$/;
	 	if(!patrn.exec(productId)){
	 		alert("产品ID:只可输入大于等于零的整数！");
	 	   	return false;
	 	}
	 }

	 var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	    if(startTime.length!=0 && endTime.length!=0){
			if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
				   alert("添加时间，请输入正确的格式！如：2011-08-10")
				   return false;
		    }

		    if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
					alert("添加时间，请输入正确的格式！如：2011-08-10")
					return false;
				}
	        }
	     var day = (getDateFromString(endTime)-getDateFromString(startTime))/(1000*60*60*24);
		 if(day<0){
				alert("添加时间，起始日期不能大于截止日期。\n请重新输入！ ");
				return false;
		 }
		 if(day>30){
				alert("添加时间，两个日期之差不得大于31。\n请重新输入！");
				return false;
		 }
	 if(startTime.length!=0&&endTime.length==0 ){
		 alert("添加时间，请输入截止日期！")
			return false;
	 }
        if(startTime.length==0&&endTime.length!=0 ){
       	 alert("添加时间，请输入起始日期！")
			return false;
	 }
	 var minStock=trim(document.getElementById("minProductStock").value);
	 var maxStock=trim(document.getElementById("maxProductStock").value);
	 if(minStock!=""&&maxStock!=""){
	 	var patrn=/^\d+$/;
	 	if(!patrn.exec(minStock)){
	 		alert("可发货总数:只可输入大于等于零的整数！");
	 	   	return false;
	 	}
	 	if(!patrn.exec(maxStock)){
	 		alert("可发货总数:只可输入大于等于零的整数！");
	 	   	return false;
	 	}
	 	minStock=eval("("+minStock+")");
	 	maxStock=eval("("+maxStock+")");
	 	if(minStock>maxStock){
	 	    alert("可发货总数:第二个输入框大于等于第一输入框！");
	 	    return false;
	 	}
	 }else{
	 	if(""==minStock&&""!=maxStock){
	 		alert("两个框都要输入值，请输入可发货总数！");
	 		return false;
	 	}
	 	if(""!=minStock&&""==maxStock){
		 	alert("两个框都要输入值，请输入可发货总数！");
		 	return false;
	 	}
	 }
	return true;
}
//去掉前后空格
function trim(str) {
	return str.replace(/(^\s*)|(\s*$)/g, "");
}
function checkboxChecked(checkbox,value){
	var values = value.split(",");
	for(var j = 0; j < values.length; j++){
		for(var i = 0; i < checkbox.length; i++){
			if(checkbox[i].value == values[j]){
				checkbox[i].checked = true;
			}
		}
	}
}

</script>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script>
<body>
<%@include file="../header.jsp"%>
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

String minPrice5 = (String)request.getParameter("minPrice5");
if(minPrice5 == null)minPrice5="";
String maxPrice5 = (String)request.getParameter("maxPrice5");
if(maxPrice5 == null)maxPrice5="";

int productId = StringUtil.toInt(StringUtil.dealParam(request.getParameter("productId")));

int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));
String startTime = StringUtil.convertNull(request.getParameter("startTime"));
String endTime = StringUtil.convertNull(request.getParameter("endTime"));

int minProductStock= StringUtil.StringToId(request.getParameter("minProductStock"));
int maxProductStock= StringUtil.StringToId(request.getParameter("maxProductStock"));
int  productType= StringUtil.StringToId(request.getParameter("products"));

int type = StringUtil.toInt(request.getParameter("type"));
boolean showType = false;
if(type == 1 || type == 2){
	showType = true;
}
%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<form method=post action="searchproduct.do" onSubmit="return checksubmit();">
<tr bgcolor="#F8F8F8">
 <td>销售类型：
  <select name="type">
   <option value="0">全部</option>
   <option value="1" <%if(type==1){%>selected<%}%>>代销</option>
   <option value="2" <%if(type==2){%>selected<%}%>>经销</option>
  </select>
 </td>
</tr>
<tr bgcolor="#F8F8F8"><td>
产品编号：<input type=text name="code" size="20" value="<%=code%>">&nbsp;&nbsp;
商品条码：<input type=text name="barcode" value="<%=barcode%>" >&nbsp;&nbsp;
产品名称：<input type=text name="name" size="20" value="<%=name%>">（模糊）<br/>
产品价格：<input type=text name="minPrice" size="20" value="<%=minPrice%>">到<input type=text name="maxPrice" size="20" value="<%=maxPrice%>">&nbsp;&nbsp;
添加时间：<input type=text name="startTime" size="10"
				    value="<%=startTime%>" onClick="SelectDate(this,'yyyy-MM-dd');"/>至<input type=text name="endTime" size="10" value="<%=endTime%>"
				    onclick="SelectDate(this,'yyyy-MM-dd');"/><br>
<%if(group.isFlag(41)){%>
库存价格：<input type=text name="minPrice5" size="20" value="<%=minPrice5%>">到<input type=text name="maxPrice5" size="20" value="<%=maxPrice5%>">&nbsp;&nbsp;&nbsp;
<%}%>
<br/>    
产品状态：<%
		for (int i = 0; i < statusList.size(); i++) {
			productStatusBean = (voSelect) statusList.get(i);
	    %>
           <input type="checkbox" id="product_status<%=productStatusBean.getId()  %>" name="product_status" value="<%=productStatusBean.getId() %>" />
           <%=productStatusBean.getName() %>&nbsp;
		<%}%>
		<br><br/><script>checkboxChecked(document.getElementById("product_status") ,"<%=status%>");</script>
<% 
if(productId==-1){
%>
产品ID：&nbsp;&nbsp;&nbsp;<input type=text name="productId" id="productId" size="20" >&nbsp;&nbsp;	<br/>
<%
}else{
%>
产品ID：&nbsp;&nbsp;&nbsp;<input type=text name="productId" id="productId" size="20" value="<%=productId%>">&nbsp;&nbsp;	<br/>
<%
}
 %>		
<%//if(group.isFlag(143))  {%>
一级分类：<select name="parentId1" class="bd" style="width:180" onChange="sredirect(this.options.selectedIndex-1);">
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
		</select>
二级分类：<select name="parentId2" class="bd" style="width:180" onChange="tredirect(this.options.selectedIndex-1);">
	<option value="0"></option></select>&nbsp;&nbsp;
三级分类：<select name="parentId3" class="bd" style="width:180">
	<option value="0"></option></select><br/>
供应商：&nbsp;&nbsp;&nbsp;<select name="proxy" id="proxy" >
			<option value=""></option>
			<%
				Iterator it = proxyList.listIterator();
				while(it.hasNext()){
					voSelect select = (voSelect)it.next();
			%>
  			<option value="<%= select.getId() %>"><%= select.getName() %></option>
			<%} %>
		</select><br/>
		<% 
		if(group.isFlag(19)){  %>
			主子商品：<%if(0==productType){ %>
			<input type="radio" name="products" checked="checked" value="0"/> 全部
			<%}else{%>
			<input type="radio" name="products" value="0"/> 全部
			<%}if(1==productType){%>
			<input type="radio" name="products" checked="checked" value="1"/> 主商品
			<%}else{%>
			<input type="radio" name="products" value="1"/> 主商品
			<%}
			if(2==productType){%>
			<input type="radio" name="products" checked="checked" value="2"/> 子商品
			<%}else{%>
			<input type="radio" name="products" value="2"/> 子商品
			<%}%>
			&nbsp;&nbsp;
			可发货总数: 从 
			<% if(maxProductStock>0){ %>
			<input type=text name="minProductStock" id="minProductStock" size="20" value="<%= minProductStock %>"> 
			<%}else{%>
			<input type=text name="minProductStock" id="minProductStock" size="20"> 
			<%} %>
			到 
			<% if(maxProductStock>0){ %>
			<input type=text name="maxProductStock" id="maxProductStock" size="20" value="<%= maxProductStock %>"><br>
			<%}else{%>
			<input type=text name="maxProductStock" id="maxProductStock" size="20"> 
			<%} %>
		<br/><br/>
		<% } %>
<script>
<!--
var parentId1 = document.forms[0].parentId1
var spt = document.forms[0].parentId2
function sredirect(x){
  if( x<=0){
	  document.forms[0].parentId2.options[0].selected=true;
	  document.forms[0].parentId3.length=1;
	  document.forms[0].parentId3.options[0].selected=true;
  }
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null
  if(x < 0){
	return;
  }
  spt.options[0]=new Option("", "0");
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i+1]=new Option(spts[x][i].text, spts[x][i].value)
  }
  spt.options[0].selected=true
  tredirect(0);
}

var tpt = document.forms[0].parentId3
function tredirect(y){
  x = document.forms[0].parentId1.options.selectedIndex-1;
  for (m = tpt.options.length - 1; m >= 0; m --)
      tpt.options[m] = null
      if(x < 0){
    		return;
    	  }
  tpt.options[0]=new Option("", "0");
  for (i = 0; i < tpts[x][y].length; i ++){
      tpt.options[i+1]=new Option(tpts[x][y][i].text, tpts[x][y][i].value)
  }
  tpt.options[0].selected=true;
}


selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex-1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
tredirect(document.forms[0].parentId2.selectedIndex-1);
selectOption(document.forms[0].parentId3, '<%= parentId3 %>');
selectOption(document.forms[0].proxy, '<%= proxy %>');
</script>
<%//} %>

<input type=submit value="查询产品">
</td></tr>
</form>
</table>
</td></tr></table>
          <br>
          <span class="STYLE1">&nbsp;&nbsp;&nbsp;共<%=listSum %>个产品</span><br>
          <form method=post action="" name="productForm" >
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="40" align="center"><font color="#FFFFFF">选项</font><br/><input type="checkbox" onclick="setAllCheck(this.form, 'id', this.checked );checkAllRank(this.form, 'id', this.checked);" /></td>
              <td width="60" align="center"><font color="#FFFFFF">ID</font></td>
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">等级</font></td>
                 <td align="center"><font color="#FFFFFF">供应商</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">团购价格</font></td>
              <td width="60" align="center"><font color="#FFFFFF">市场价</font></td>
              <td width="60" align="center"><font color="#FFFFFF">库存价格</font></td>
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
              <%--
              <td width="80" align="center"><font color="#FFFFFF">录入时间</font></td>
              --%>
              <td width="60" align="center"><font color="#FFFFFF">查看评论</font></td>
              <td width="25" align="center"><font color="#FFFFFF">多图</font></td>
              <%-- z
              <td width="50" align="center"><font color="#FFFFFF">小图</font></td>
              --%>
              <td width="50" align="center"><font color="#FFFFFF">大图</font></td>
              <%--
              <td width="50" align="center"><font color="#FFFFFF">小小图</font></td>
              --%>
              <td width="50" align="center"><font color="#FFFFFF">可发货总数</font></td>
              <td width="50" align="center"><font color="#FFFFFF">库存总数</font></td>

			  <%if(showType){
				  %><td width="50" align="center"><font color="#FFFFFF">销售类型</font></td><%
			  }
			  %>
              
              <td width="50" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
<logic:present name="productList" scope="request">
<logic:iterate name="productList" id="item" >
<%adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type='checkbox' name='id' value='<bean:write name="item" property="id" />' onclick="checkRank(this);"></td>
		<td align=left width="60"><bean:write name="item" property="id" /></td>
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />&simple=1" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><input type="text" size="5" name="rank" id="<bean:write name="item" property="id" />rank" value="<bean:write name="item" property="rank"/>"  disabled="disabled" /></td>
		<td align='center'><bean:write name="item" property="proxyName"/></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a> <font color="red">标准化信息</font>
		</td>
		<td align=right width="60"><bean:write name="item" property="price" />元</td>
		<td align=right width="60"><bean:write name="item" property="groupBuyPrice" />元</td>
		<td align=right width="60"><bean:write name="item" property="price2" />元</td>
		<%  
		//库存价格 ---延用权限ID：41（批发价）。无权限的人员查看时，列表字段库存价格用“--”显示
		//要记得还有一个限制
			String catalogIds = ProductLinePermissionCache.getCatalogIds(user);
			HashMap map1 = (HashMap)CatalogCache.catalogLevelList.get(0);
			List list1 = (List)map1.get(Integer.valueOf(0));
			Iterator iter1 = list1.listIterator();
			//第一级老分类ID
			String oldCatalogFirstId=voItem.getParentId1()+"";
			boolean flag=false;
			while(iter1.hasNext()){
				voCatalog catalog = (voCatalog)iter1.next();
				if(StringUtil.hasStrArray(catalogIds.split(","),String.valueOf(catalog.getId()))){
					if(catalog.getId()==StringUtil.StringToId(oldCatalogFirstId)){
						flag=true;
						break;
					}
				}
			}
		if(group.isFlag(41)&&flag){
		%>
		<td width="60" align="center"><bean:write name="item" property="price5" />元</td>
		<%  }else{%>
		<td width="60" align="center">--</td>
		<%}%>
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
		<td width="50" align="center"><%= voItem.getStock(0, 0) + voItem.getStock(1, 0) + voItem.getStock(2, 0)+voItem.getStock(3, 0) + voItem.getStock(4, 0)  %></td>
		<td width="50" align="center"><%= voItem.getStock(0) + voItem.getStock(1) + voItem.getStock(2)+voItem.getStock(3)+voItem.getStock(4) + voItem.getLockCount(0) + voItem.getLockCount(1) + voItem.getLockCount(2) + voItem.getLockCount(3) + voItem.getLockCount(4) %></td>
		<%if(showType){
				  %>
				  <td width="50" align="center">
				  <%if(type == 1){
					  %>代销<%
				  }else{
					  %>经销<%
				  }
				  %>
				  </td><%
			  }
			  %>
		<td align="center"><%if(group.isFlag(51)){ %><a href="fProductMark.do?id=<bean:write name="item" property="id" />">编辑坏货/已返厂</a><%} %>
		<%if(group.isFlag(293)&& voItem.getProductBarcodeVO()!=null && voItem.getProductBarcodeVO().getBarcode()!=null){ %> |<a href="javascript:void(0);" onclick="onPrintSubmit('<bean:write name="item" property="code" />','<bean:write name="item" property="oriname" />','<%=voItem.getProductBarcodeVO().getBarcode() %>'); return false;">打印条码</a><%} %>
		<%if(voItem.getParentId1()==111){ %>|<a href="<%=request.getContextPath()%>/admin/productFaq.do?method=selFAQ&productId=<%=voItem.getId()%>&flag=1" target="_blank">FAQ</a><% } %>
		</td>
		</tr>
</logic:iterate> </logic:present>
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0">
            <tr>
              <td height="35">
              <input type="button" value="导出已选项" onClick="return excel_product()">  
              <% if(group.isFlag(19)){ %> 
                 <input type="button" value="导出全部" onClick="return excel_allProduct()">
                 <% } %>
              	<input type="button" value="修改商品等级" onClick="return mProductRank();" />
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
   <form name="importProducts" action="searchproduct.do?forward=2Excel" method="post">
   	<input id="importProductIds" name="id" type="hidden">
   </form>
</body>
</html>