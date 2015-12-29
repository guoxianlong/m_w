<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>买卖宝后台</title>
<link href="../css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pts.js"></script>
<script language="JavaScript" src="js/article_pts.js"></script>
<body>
<%@include file="../header.jsp"%>
        <form name="mlogForm" method="post" action="" >
          <table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8">
            <tr> 
              <td><table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
				  <tr>
                    <td height="30" align="center" bgcolor="#F8F8F8">ID：</td>
                    <td bgcolor="#F8F8F8"><bean:write name="log" property="id" /></td>
                  </tr>
                  <tr>
                    <td height="30" align="center" bgcolor="#F8F8F8">操作类型：</td>
                    <td bgcolor="#F8F8F8"><bean:write name="log" property="typeName" /></td>
                  </tr>
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">操作人员：</td>
                    <td bgcolor="#F8F8F8"><bean:write name="log" property="user.username" /></td>
                  </tr>
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">操作时间：</td>
                    <td bgcolor="#F8F8F8"><bean:write name="log" property="createDatetime" /></td>
                  </tr>
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">内容：</td>
                    <td bgcolor="#F8F8F8"><bean:write name="log" property="content" filter="false" /></td>
                  </tr>
                </table></td>
            </tr>
          </table>
          <br />
        </form>
<%@include file="../footer.jsp"%>
</body>
</html>