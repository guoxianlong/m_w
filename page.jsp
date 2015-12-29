<%@ page contentType="text/html;charset=utf-8" %>
<%int currentPage = ((Integer)request.getAttribute("currentPage")).intValue();%>
<%int totalPage = ((Integer)request.getAttribute("totalPage")).intValue();%>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/dividepage.js"></script>
<form name="dividepage">
<table width="460" cellpadding=1 cellspacing=1 >
	<tr >
	<td>第<%=currentPage+1%>页 共<%=totalPage+1%>页</td>
	<td align=right>
		<input id=first_button type="button" name="first" value="第一页" onclick="firstPage(document.all.currentPage_hidden.form)">
        <input id=previous_button type="button" name="previous" value="上一页" onclick="previousPage(document.all.currentPage_hidden.form)"> 
        <input id=next_button type="button" name="next" value="下一页" onclick="nextPage(document.all.currentPage_hidden.form)"> 
        <input id=last_button type="button" name="last" value="末一页" onclick="lastPage(document.all.currentPage_hidden.form)">
    </td>
    <td>
		<input id="pages" type="text" name="pages" value="" size="3" />
    	<input id="goto_page" type="button" name="goto_" value="跳转到" onclick="gotoPage(document.all.currentPage_hidden.form, this.form)" />
    </td>
	</tr>
</table>
</form>
<script language="javascript">
   buttonControl(document.all.currentPage.form);
</script>