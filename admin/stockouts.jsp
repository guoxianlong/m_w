oyou<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>

<html>
<title>买卖宝后台</title>
<script>
</script>
<script type="text/javascript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<form action="" method="post">

</form>
          <br><form method=post action="" name="stockinForm" target="_blank">
          <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="80" align="center"><font color="#FFFFFF">生成时间</font></td>
            </tr>           
<logic:present name="stockoutList" scope="request"> 
<logic:iterate name="stockoutList" id="item" > 
		<tr bgcolor='#F8F8F8'>
		<td align=left width="80"><a href="stockoutproducts.do?date=<bean:write name="item"/>"><bean:write name="item"/></a></td>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0" align=center>
            <tr>
              <td height="35">
            <input type=checkbox onclick="setAllCheck(stockinForm,'select',this.checked)">全选
              </td>
              <td height="35">
              </td>
            </tr>
          </table>

          <br>   
        </td>
    </tr>
  </table>
</body>
</html>