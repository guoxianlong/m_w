<%@ page contentType="text/html;charset=gb2312"%> 
<%@page import="mmb.order.ivr.*"%>
<%  
if(ZhichongUtil.bftIVRNotify(request, response)){
    out.print(request.getAttribute("resultMess").toString());
} else {
	out.print(request.getAttribute("resultMess").toString());
}
%>