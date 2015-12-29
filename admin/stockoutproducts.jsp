<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>买卖宝后台</title>
<script>
function del_bulletin()
{
	if(confirm('确认要删除选中的公告吗?')) {
		document.bulletinForm.action="mb-dbulletin.do";
		return document.bulletinForm.submit();
	}
}
function addproduct(code,pid){
	document.aproduct.productId.value=pid;
	if(confirm('将添加产品id为'+pid+'的产品，数量'+document.aproduct.count.value+'，确认？'))
		document.aproduct.submit();
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
          
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">创建时间</td>
		<td ><%=request.getParameter("date")%></td>
	</tr>
</table>

          <table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
              <tr bgcolor="#4688D6">
              <td align="center" width=80><font color="#FFFFFF">编号</font></td>
              <td align="center"><font color="#FFFFFF">名称</font></td>
		        <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
		        <td width="100" align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="40" align="center"><font color="#FFFFFF">数量</font></td>
              <td width="40" align="center"><font color="#FFFFFF">位置</font></td>
              <td width="40" align="center"><font color="#FFFFFF">库存<br/>(北京)</font></td>
              <td width="40" align="center"><font color="#FFFFFF">库存<br/>(广东)</font></td>
              <td align="center" width=80><font color="#FFFFFF">操作</font></td>
            </tr>           
<logic:present name="stockoutProductList" scope="request"> 
<logic:iterate name="stockoutProductList" id="item" > 
		<tr bgcolor='#F8F8F8'>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="productId" />" ><bean:write name="item" property="name" /></a></td>
		<td align='right'><bean:write name="item" property="price" /></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align=right width="40"><bean:write name="item" property="count" /></td>
		<td align=right width="40">
			<logic:equal name="item" property="type" value="1">北京</logic:equal>
			<logic:equal name="item" property="type" value="2">广东</logic:equal>
		</td>
		<td align=right width="40"><bean:write name="item" property="stock" /></td>
		<td align=right width="40"><bean:write name="item" property="stockGd" /></td>
		<td></td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          
		  <form method=post action="mstockinproduct.do" name="aproduct">
          <table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#F8F8F8">
              <td>

              </td>
            </tr>
          </table>
          </form>
          
          <br>   
        </td>
    </tr>
  </table>
</body>
</html>