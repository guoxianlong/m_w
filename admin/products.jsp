<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser" %><%@page import="adultadmin.service.AdminServiceImpl,adultadmin.action.standard.*"%>
<%@ page import="adultadmin.bean.*, adultadmin.util.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
AdminServiceImpl service = new AdminServiceImpl();

%>
<html>
<title>买卖宝后台</title>
<script>
function del_product()
{
	if(confirm('确认要删除选中的产品吗?')) {
		document.productForm.action="dproduct.do?catalogId=<%=request.getParameter("catalogId")%>";
		document.productForm.target="";
		return document.productForm.submit();
	}
}
function mProductRank(){
	if(confirm('确认要修改选中产品的等级吗?')) {
		document.productForm.action="./productProperty/mProductRank.jsp";
		document.productForm.target="_blank";
		document.productForm.submit();
		document.productForm.target="";
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
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<form action="" method="post">
<%@include file="../pageNum.jsp"%>
</form>
          <br><form method=post action="" name="productForm" target="">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="40" align="center"><font color="#FFFFFF">选项</font></td>
              <td width="60" align="center"><font color="#FFFFFF">ID</font></td>
              <td width="60" align="center"><font color="#FFFFFF">编号</font></td>
              <td width="60" align="center"><font color="#FFFFFF">等级</font></td>
              <td align="left"><font color="#FFFFFF">名称</font></td>
              <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
              <td width="50" align="center"><font color="#FFFFFF">代理商</font></td>
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
              <td width="60" align="left"><font color="#FFFFFF">mmb产品页生成方式</font></td>
              <td width="60" align="left"><font color="#FFFFFF">yyt产品页生成方式</font></td>
              <td width="80" align="center"><font color="#FFFFFF">录入时间</font></td>
              <td width="50" align="center"><font color="#FFFFFF">查看评论</font></td>
              <td width="25" align="center"><font color="#FFFFFF">多图</font></td>
              <td width="50" align="center"><font color="#FFFFFF">小图</font></td>
              <td width="50" align="center"><font color="#FFFFFF">大图</font></td>
<%--              <td width="50" align="center"><font color="#FFFFFF">小小图</font></td>

              <td width="50" align="center"><font color="#FFFFFF">操作</font></td>
--%>
            </tr>           
<logic:present name="productList" scope="request"> 
<logic:iterate name="productList" id="item" > 
<%adultadmin.action.vo.voProduct voItem = (adultadmin.action.vo.voProduct)item;%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type='checkbox' name='id' value='<bean:write name="item" property="id" />' onclick="checkRank(this);"></td>
		<td align=left width="60"><bean:write name="item" property="id" /></td>
		<td align=left width="60"><a href="fproduct.do?id=<bean:write name="item" property="id" />&simple=1" ><bean:write name="item" property="code" /></a></td>
		<td align='center'><input type="text" size="5" name="rank" id="<bean:write name="item" property="id" />rank" value="<bean:write name="item" property="rank"/>"  disabled="disabled" /></td>
		<td align='left'><a href="fproduct.do?id=<bean:write name="item" property="id" />" ><bean:write name="item" property="name" /></a><font color="red">标准化信息</font> <%
		int standardStatus = StandardUtil.getStandardStatus(voItem.getId());
		if(standardStatus == 0){%>无<%} else if(standardStatus == 1){%><font color="red">主</font><%} else if(standardStatus == 2){%><font color="blue">子</font><%}
		%>
		</td>
		<td align=right width="60"><bean:write name="item" property="price" />元</td>
		<td align=left width="50"><bean:write name="item" property="proxyName" /></td>
		<td align=right width="40"><bean:write name="item" property="statusName" /></td>
		<td align=right width="50"><% int type = service.findById(voItem.getId(),0).getPageMakeType(); if(type==0){%>小店后台自动生成<%}if(type==1){%>模版编辑<%}if(type==2){%>页面后台制作<%}%></td>
		<td align=right width="50"><% int ytype = service.findById(voItem.getId(),1).getPageMakeType(); if(ytype==0){%>小店后台自动生成<%}if(ytype==1){%>模版编辑<%}if(ytype==2){%>页面后台制作<%}%></td>
		<td align=right width="80"><bean:write name="item" property="createDatetime" /></td>
		<td align=left width="50"><a href="comments.do?productId=<bean:write name="item" property="id" />">查看(<bean:write name="item" property="commentCount" />)</a></td>
		<td align=left width="25"><a href="productpics.do?productId=<bean:write name="item" property="id" />">查看</a></td>
		<td width="50" align="center"><%if(voItem.getPic()==null||voItem.getPic().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic()+"'><image border=0 src='"+voItem.getFullPic()+"' width=20 height=20></a>");%></td>
		<td width="50" align="center"><%if(voItem.getPic2()==null||voItem.getPic2().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic2()+"'><image border=0 src='"+voItem.getFullPic2()+"' width=20 height=20></a>");%></td>
<%--		<td width="50" align="center"><%if(voItem.getPic3()==null||voItem.getPic3().length()<3)out.print("无");else out.print("<a href='"+voItem.getFullPic3()+"'><image border=0 src='"+voItem.getFullPic3()+"' width=20 height=20></a>");%></td>

		<td align="center"><%if(group.isFlag(51)){ %><a href="fProductMark.do?id=<bean:write name="item" property="id" />">编辑坏货/已返厂</a><%} %></td>
--%>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0">
            <tr>
              <td height="35">
              	<%if(group.isFlag(19)){ %>
              	<input type="button" onClick="window.navigate('fproduct.do')" value="添 加">
              	<input type="button" value=" 删 除 " onClick="return del_product()">
              	<%}service.close(); %>
              	<input type="button" value="修改商品等级" onClick="return mProductRank();" />
              </td>
            </tr>
          </table>
<%@include file="../page.jsp"%>
          <br>
</body>
</html>