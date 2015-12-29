<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	List list = (List) request.getAttribute("list");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
	List checkEffectList = (ArrayList) request.getAttribute("checkEffectList");
	List productWareTypeList = (ArrayList) request.getAttribute("productWareTypeList");
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String productBarCode = StringUtil.convertNull(request.getParameter("productBarCode"));
		int checkEffectId = StringUtil.parstInt(request.getParameter("checkEffect"));
		if( checkEffectId == 0 ) {
			checkEffectId = -1;
		}
		//int standardCount = StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		if( wareType == 0 ) {
			wareType = -1;
		}
%>
<html>
  <head>
    
    <title>商品物流属性</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function toAddPage() {
   			window.location="<%= request.getContextPath()%>/admin/cargo/addCheckEffect.jsp";
   		}
   		
   		function deleteColum(id, name){
			if(!confirm("确认要删除该商品的物流属性吗？")){
				return;
			}
			window.location="productWarePropertyAction.do?method=deleteCheckEffect&id="+id + "&name="+ name;
			return;
		}
		function check() {
			return true;
		}
		
		function deleteColum(id, productId){
			if(!confirm("您确定要删除该条目么？")){
				return;
			}
			window.location="productWarePropertyAction.do?method=deleteProductWareProperty&productWarePropertyId="+id + "&productId="+ productId;
			return;
		}
		function toAddPage() {
			window.location="productWarePropertyAction.do?method=toAddProductWareProperty";
		}
	</script>

  </head>
  <body>
  <div align="center">
  <h2>商品物流属性</h2>
  </div>
  <div style="margin-left:3%;width:94%;height:140px;border-style:solid;border-width:1px;border-color:#000000;">
   			<form action="<%= request.getContextPath()%>/admin/productWarePropertyAction.do?method=getProductWarePropertyInfo" method="post" onsubmit="return check();">
   					产品编号：<input type="text" size="11" name="productCode" id="productCode" onchange="autoFillWithProductCode();" value="<%= productCode%>"/>
   					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   					商品条码：<input type="text" size="11" name="productBarCode" id="productBarCode" onchange="autoFillWithProductBarCode();" value="<%= productBarCode%>" />
   					<br/>
   					<br/>
   					<b style="font-size:14px;">入库属性：</b>
   					质检分类：<select name="checkEffect" id="checkEffect" >
   							<%  if (checkEffectList == null ||checkEffectList.size() == 0 ) { %>
   								<option value="-1">没有质检分类可用</option>
   							<%
   								} else {
   								%>
   								<option value="-1" <%= checkEffectId == -1 ? "selected" : ""%>>请选择分类</option>
   								<%
   								int x = checkEffectList.size();
   								for( int i = 0; i < x ; i++ ) { 
   								CheckEffectBean cfb = (CheckEffectBean) checkEffectList.get(i);
   							%>
   								<option value="<%= cfb.getId()%>" <%= checkEffectId == cfb.getId() ? "selected" : ""%>><%= cfb.getName()%></option>
   							<%
   								}
   								}
   							%>
   							 </select>
   					<br/>
   					<br/>
   					<b style="font-size:14px;">出库属性：</b>
   					商品物流分类：<select name="wareType" id="wareType">
   									<%  if (productWareTypeList == null || productWareTypeList.size() == 0 ) { %>
   								<option value="-1">没有质检分类可用</option>
   							<%
   								} else {
   								%>
   								<option value="-1" <%= wareType == -1 ? "selected" : ""%>>请选择分类</option>
   								<%
   								int x = productWareTypeList.size();
   								for( int i = 0; i < x ; i++ ) { 
   								ProductWareTypeBean pwtBean = (ProductWareTypeBean) productWareTypeList.get(i);
   							%>
   								<option value="<%= pwtBean.getId()%>" <%= wareType == pwtBean.getId() ? "selected" : ""%>><%= pwtBean.getName()%></option>
   							<%
   								}
   								}
   							%>
   							 </select>
   					<div style="left:70%;top:140px;width:100px;position:absolute;">
   						<input type="submit" value="  查询  " />
   					</div>
   			</form>
   		</div>
   		<br/>
   		<div style="margin-left:10%;width:80%;">
   		<button onclick="toAddPage();">添加新商品物流属性</button>
   		</div>
   		<br/>
  	<table align="center" width="94%" border="0" cellspacing="1px" bgcolor="#D8D8D5" cellpadding="1px" >
		<tr bgcolor="#484891" >
			<td align="center" rowspan="2">
			<font color="#FFFFFF">商品编号</font>
			</td>
			<td align="center" rowspan="2">
			<font color="#FFFFFF">原名称</font>
			</td>
			<td align="center" colspan="2">
			<font color="#FFFFFF">入库属性</font>
			</td>
			<td align="center" colspan="2">
			<font color="#FFFFFF">仓储属性</font>
			</td>
			
			<td align="center" colspan="2">
			<font color="#FFFFFF">出库属性</font>
			</td>
			<td align="center" rowspan="2">
			<font color="#FFFFFF">操作</font>
			</td>
		</tr>
		<tr bgcolor="#484891" >
			
			<td align="center">
			<font color="#FFFFFF">质检分类</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">标准装箱量</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">最小包装尺寸</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">重量</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">可辨识信息</font>
			</td>
			<td align="center">
			<font color="#FFFFFF">商品物流分类</font>
			</td>
			
		</tr>
		
		<% if( list != null && list.size() != 0 ) {
			 for( int i = 0; i < list.size(); i++ ) {
			 ProductWarePropertyBean pwpBean = (ProductWarePropertyBean) list.get(i);
		 %>
			<tr bgcolor= "<%= i%2 == 0 ? "#EEE9D9" : "#FFFFCE"%>" >
			<td align="center">
			<%= pwpBean.getProduct().getCode()%>
			</td>
			<td align="center">
			<%= pwpBean.getProduct().getOriname()%>
			</td>
			<td align="center">
			<%= pwpBean.getCheckeEffect().getName()%>
			</td>
			<td align="center"><%= pwpBean.getCartonningStandardCount()%></td>
			<td align="center">
			<%= pwpBean.getLength()%>cm×<%= pwpBean.getWidth() %>cm×<%= pwpBean.getHeight() %>cm
			</td>
			<td align="center"><%= pwpBean.getWeight()%></td>
			<td align="center">
			<%= pwpBean.getIdentityInfo() %>
			</td>
			<td align="center">
			<%= pwpBean.getProductWareType().getName()%>
			</td>
			<td align="center">
			<a href="productWarePropertyAction.do?method=preEditProductWareProperty&productWarePropertyId=<%= pwpBean.getId()%>">修改</a>
			&nbsp;|&nbsp;
			<a href="javascript:deleteColum(<%= pwpBean.getId() %>, <%= pwpBean.getProductId()%>);">删除</a>
			&nbsp;|&nbsp;
			<a href="productWarePropertyAction.do?method=getProductWarePropertyLogInfo&productWarePropertyId=<%= pwpBean.getId()%>&productCode=<%=pwpBean.getProduct().getCode()%>">操作日志</a>
			</td>
		</tr>
		<%
		 }
		 }else {%>
			<tr bgcolor="#FFFF93" >
			<td align="center" colspan="7">
				没有商品物流属性记录
			</td>
		</tr>
		<% }%>
	</table>
  	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
  
</body>
</html>
