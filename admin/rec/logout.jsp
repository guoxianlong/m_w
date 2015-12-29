<%@ include file="taglibs.jsp"%>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*"%><%@ page contentType="text/html;charset=utf-8" %><%@ page import="adultadmin.action.vo.voUser" %><%
voUser user = (voUser)session.getAttribute("userView");
if(user!=null) {
	CookieUtil ck = new CookieUtil(request,response);
	ck.removeCookie("opau");
	ck.removeCookie("opap");
	DbOperation dbOp = new DbOperation();
	dbOp.init(DbOperation.DB);
	try{
		dbOp.executeUpdate("update admin_user set cookie_hash='' where id=" + user.getId());// 清理数据库cookie记录，让自动登录的cookie失效
	}finally{
		dbOp.release();
	}
	
	session.invalidate();
}
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<script>
parent.location="<%= request.getContextPath()%>/admin/login.mmx";
</script>
<html xmlns="http://www.w3.org/1999/xhtml">
登出
</html>