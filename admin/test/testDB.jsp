<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="adultadmin.util.db.DbOperation" %>
<%
	DbOperation db = new DbOperation();
	db.init("adult");
	try{
%>
数据库版本：<%=db.getConn().getMetaData().getDatabaseProductVersion()%><br/>
jdbc驱动版本：<%=db.getConn().getMetaData().getDriverVersion()%>
<%
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		db.release();
	}
%>