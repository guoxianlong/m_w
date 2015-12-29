<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%>
<html>
<title>买卖宝后台</title>
<script>
function del()
{
	if(confirm('确认要删除选中的内容吗?')) {
		document.poolForm.action="mpoolProps.do?action=delete";
		return document.poolForm.submit();
	}
}
function hid()
{
	if(confirm('确认要隐藏选中的内容吗?')) {
		document.poolForm.action="mpoolProps.do?action=hide";
		return document.poolForm.submit();
	}
}
function unhid()
{
	if(confirm('确认要撤销隐藏吗?')) {
		document.poolForm.action="mpoolProps.do?action=unhide";
		return document.poolForm.submit();
	}
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
          <br><form method=post action="" name="poolForm">
          <input type=hidden name="type" value="<%=request.getParameter("type")%>">
          <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="20" align="center"><font color="#FFFFFF">选</font></td>
              <td width="200" align="center"><font color="#FFFFFF">名称</font></td>
              <td width="40" align="center"><font color="#FFFFFF">类型</font></td>
              <td width="40" align="center"><font color="#FFFFFF">状态</font></td>
            </tr>           
<logic:present name="poolList" scope="request"> 
<logic:iterate name="poolList" id="item" > 
		<tr bgcolor='#F8F8F8'>
		<td align='center' width="20"><input type=checkbox name="select" value="<bean:write name="item" property="id" />"></td>
		<td align=left width="200"><bean:write name="item" property="name" /></td>
		<td align=left><bean:write name="item" property="typeName" /></td>
		<td align="center"><logic:equal name="item" property="hide" value="1" >隐藏</logic:equal><logic:equal name="item" property="hide" value="0" >普通</logic:equal></td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0" align=center>
            <tr>
              <td height="35" width=60>
            <input type="button" value="隐 藏" onClick="return hid()">
              </td>
              <td height="35" width=60>
            <input type="button" value="撤销隐藏" onClick="return unhid()">
              </td>
            <td height="35" width=60>
            	<input type="button" value=" 删 除 " onClick="return del()">
            </td>
            </tr>
          </table>
          <br>   
        </td>
    </tr>
  </table>
</body>
</html>