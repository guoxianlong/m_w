<%@ page language="java" import="java.util.*" contentType="text/xml; charset=utf-8" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voSelect"%> 

<words>
<%
List list = (List)request.getAttribute("word");
if(list.size()!=0)
{
Iterator it = list.iterator();
for(;it.hasNext();)
{
voSelect vo = (voSelect)it.next();
%>
<word><%=vo.getName()%></word>
<%
}

}
%>
</words>

