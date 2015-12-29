<%@page import="java.util.*,adultadmin.bean.cargo.*"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<html>
<title>小店后台管理 - 搜索产品</title>
<script>
function del_product()
{
	if(confirm('确认要删除选中的产品吗?')) {
		document.productForm.action="dproduct.do";
		return document.productForm.submit();
	}
}
</script>
<%
	List list = (List)request.getAttribute("productList");
	int type = StringUtil.StringToId(request.getParameter("type"));
	HashMap cpsListMap = (HashMap)request.getAttribute("cpsListMap");
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
	int stockAreaId = StringUtil.toInt(request.getParameter("stockAreaId"));
	int operType = StringUtil.toInt(request.getParameter("operType"));
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/pub.js"></script>
<body>
<%@include file="/header.jsp"%>
          <br><form method=post action="" name="productForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
              <tr bgcolor="#4688D6">
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">小店名称</font></td>
              <td align="center"><font color="#FFFFFF">原名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
              <td width="100" align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
              <%-- <td width="80" align="center"><font color="#FFFFFF">北库库存</font></td> --%>
              <td width="80" align="center"><font color="#FFFFFF">芳村库存</font></td>
              <td width="80" align="center"><font color="#FFFFFF">增城库存</font></td>
              <%--<td width="80" align="center"><font color="#FFFFFF">广速库存</font></td> --%>
              <td align="center"><font color="#FFFFFF">源货位</font></td>
              <td width="100" align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
<logic:present name="productList" > 
<logic:iterate name="productList" id="item" > 
<%adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;%>
		<tr bgcolor='#F8F8F8'>
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="oriname" /></a></td>
		<td align=right width="60"><bean:write name="item" property="price" />元</td>
		<td align=left width="100"><bean:write name="item" property="proxyName" /></td>
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<%-- <td align=right width="80"><%= voItem.getStock(0) %></td> --%>
		<td align=right width="80"><%= voItem.getStock(1) %></td>
		<td align=right width="80"><%= voItem.getStock(3) %></td>
		<%--<td align=right width="80"><%= voItem.getStock(2) %></td>--%>
        <td align=center width="100">
        <select name="cpsOut<%= voItem.getId() %>" id="cpsOut<%= voItem.getId() %>">
        	<option value=""></option>
        <%
        	List cpsList = voItem.getCargoPSList();
        	if(cpsList != null){
        		for(int i=0;i<cpsList.size();i++){
        			CargoProductStockBean cps = (CargoProductStockBean)cpsList.get(i);
        			if(cps.getCargoInfo().getStockType() != stockType || cps.getCargoInfo().getAreaId() != stockAreaId){
        				continue;
        			}
        			if(operType == 0 && cps.getStockCount() == 0){
        				continue;
        			}
        %>
        	<option value="<%=cps.getCargoInfo().getWholeCode() %>"><%=cps.getCargoInfo().getWholeCode() %>(<%=cps.getStockCount() %>)</option>
        <%			
        		}
        	}
        %>
        </select>
        </td>
		<td align=center width="100">
<%
			if(type == 1){
%>
			<a href="#" onclick="parent.addproduct('<bean:write name="item" property="code" />',document.getElementById('cpsOut<%= voItem.getId() %>').options[document.getElementById('cpsOut<%= voItem.getId() %>').selectedIndex].value);return false;">添加产品</a>
<%
			} else {
%>
			<br/>
			<a href="#" onclick="parent.addproduct('<bean:write name="item" property="code" />',<bean:write name="item" property="id" />, 1);return false;">添加北京库存</a>
			<br/><br/>
			<a href="#" onclick="parent.addproduct('<bean:write name="item" property="code" />',<bean:write name="item" property="id" />, 2);return false;">添加广东库存</a>
<%
			}
%>
		</td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
</body>
</html>