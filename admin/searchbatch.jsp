<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@ include file="../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voProduct"%>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="java.util.*" %>
<%
voUser adminUser = (voUser)session.getAttribute("userView");
UserGroupBean group = adminUser.getGroup();
%>
<html>
<head>
<title>小店后台管理 - 搜索文章</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
<script type="text/javascript">
function check(){
	var code = document.getElementById("code").value;
	var name = document.getElementById("name").value;
	
	if(code==""&&name==""){
		window.alert("请输入查询条件");
		return false;
	}else{
		return true;
	}
}
</script>
<%@include file="../header.jsp"%>
<table width="95%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center>
<tr>
<td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr bgcolor="#F8F8F8"><td>
<form method=post action="searchbatch.do" onSubmit="return check();">
<%
String code = (String)request.getParameter("code");
if(code == null)code="";
String name = (String)request.getParameter("name");
if(name == null)name="";
String isError = (String)request.getAttribute("isError");
voProduct product = (voProduct)request.getAttribute("product");
List psList = (List)request.getAttribute("psList");
List showBatchList = (List)request.getAttribute("showBatchList");

HashMap psMap = new HashMap();
if(psList!=null){
Iterator psIter = psList.listIterator();
while(psIter.hasNext()){
	ProductStockBean ps = (ProductStockBean)psIter.next();
	psMap.put(ps.getArea()+"_"+ps.getType(),Integer.valueOf(ps.getId()));
}
}
%>
产品编号：<input type=text name="code" id="code" size="20" value="<%=code%>"><br>
产品名称：<input type=text name="name" id="name" size="20" value="<%=name%>"><br>
<input type=submit value="查询批次">
</form>
</td>
</tr>
</table>
</td>
</tr>
<%if(product!=null){%>
<tr>
	<td>
	<%if(isError != null){%>
		<font color="red"><%=isError %></font>
	<%}else{ %>
			<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
				<tr bgcolor="#4688D6">
					<td rowspan="3" align="center" valign="middle"><font color="#FFFFFF">批次号</font></td>
					<%if(group.isFlag(41)){ %>
					<td rowspan="3" align="center" valign="middle"><font color="#FFFFFF">批次价</font></td>
					<%} %>
					<td colspan="24" align="center"><font color="#FFFFFF">批次量</font></td>
					<%if(group.isFlag(41)){ %>
					<td rowspan="3" align="center" valign="middle"><font color="#FFFFFF">批次结存</font></td>
					<%} %>
					<td rowspan="3" align="center" valign="middle" width="15%"><font color="#FFFFFF">生成时间</font></td>
					<td rowspan="3" align="center" valign="middle" width="4%"><font color="#FFFFFF">批次操作记录</font></td>
				</tr>
				<tr bgcolor="#4688D6">
					<td rowspan="2" align="center" width="4%"><font color="#FFFFFF">批次库存量</font></td>
					<td colspan="2" align="center"><font color="#FFFFFF">待验库</font></td>
					<td colspan="3" align="center"><font color="#FFFFFF">合格库</font></td>
					<td colspan="3" align="center"><font color="#FFFFFF">退货库</font></td>
					<td colspan="3" align="center"><font color="#FFFFFF">返厂库</font></td>
					<td colspan="3" align="center"><font color="#FFFFFF">维修库</font></td>
					<td colspan="4" align="center"><font color="#FFFFFF">残次品库</font></td>
					<td colspan="3" align="center"><font color="#FFFFFF">样品库</font></td>
					<td colspan="2" align="center"><font color="#FFFFFF">售后库</font></td>
				</tr>
				<tr bgcolor="#4688D6">
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">广速</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">广速</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">北京</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">北京</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">北京</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">广速</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">北京</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">增城</font></td>
					<td><font color="#FFFFFF" align="center">芳村</font></td>
					<td><font color="#FFFFFF" align="center">&nbsp;</font></td>
				</tr>
				<%
				for(int i=0;i<showBatchList.size();i++){
					HashMap batchMap = (HashMap)showBatchList.get(i);
					String batchTime = (String)batchMap.get("batchTime");
					if(batchTime.length() > 19){
						batchTime = batchTime.substring(0,19);
					}
					
				%>
				<tr>
					<td><%=batchMap.get("code") %></td>
					<%if(group.isFlag(41)){ %>
					<td><%=batchMap.get("batchPrice") %></td>
					<%} %>
					<td><%=batchMap.get("batchCountTotal") %></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_1"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_1")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_1"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_1")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_0"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_0")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("2_0"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("2_0")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_0"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_0")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_4"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_4")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("2_4"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("2_4")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_4"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_4")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_3"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_3")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_3"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_3")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_3"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_3")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_2"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_2")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_2"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_2")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_2"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_2")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_5"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_5")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_5"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_5")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("2_5"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("2_5")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_5"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_5")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_6"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("0_6")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_6"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_6")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_6"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("3_6")):0%></td>
					<td><%=batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_9"))!=null?batchMap.get(batchMap.get("code")+"_"+(Integer)psMap.get("1_9")):0%></td>
					<td>&nbsp;&nbsp;&nbsp;</td>
					<%if(group.isFlag(41)){ %>
					<td><%=((Integer)batchMap.get("batchCountTotal")).intValue()*((Float)batchMap.get("batchPrice")).floatValue()%></td>
					<%} %>
					<td><%=batchTime %></td>
					<td align="center"><a href="stock2/stockBatchLog.jsp?productId=<%=product.getId() %>&batchCode=<%=batchMap.get("code") %>">查看</a></td>
				</tr>
				<%} %>
			</table>
	</td>
	</tr>
<%}}%>
</table>
          <br>
          
          
          <br>   
</body>
</html>