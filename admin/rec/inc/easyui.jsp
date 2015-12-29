<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- my97日期控件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
<!-- easyui控件 -->
<link id="easyuiTheme" rel="stylesheet" href="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/themes/<c:out value="${cookie.easyuiThemeName.value}" default="default"/>/easyui.css" type="text/css"></link>
<link rel="stylesheet" href="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js" charset="utf-8"></script>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/changeEasyuiTheme.js" charset="utf-8"></script> --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-1.3.1/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
<!-- easyui portal插件 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-portal/portal.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery-easyui-portal/jquery.portal.js" charset="utf-8"></script>
<!-- cookie插件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/jquery.cookie.js" charset="utf-8"></script>
<!-- 自己定义的样式和JS扩展 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/admin/rec/js/jsCss.css" type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/common/ajaxDefault.js" charset="utf-8"></script>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/admin/rec/js/easyuiUtils.js" charset="utf-8"></script> --%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<script type="text/javascript"> var sysPath = "${path}" </script>