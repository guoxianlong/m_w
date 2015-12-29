<%@ page contentType="text/html;charset=utf-8" %>
<%int currentPageNum = ((Integer)request.getAttribute("currentPage")).intValue();%>
<%int totalPageNum = ((Integer)request.getAttribute("totalPage")).intValue();%>
<input id="currentPage_hidden" type="hidden" name="currentPage" value="<%=currentPageNum%>" >
<input id="totalPage_hidden" type="hidden" name="totalPage" value="<%=totalPageNum%>" >