<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page
	import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*"%>
<%
    String tr = (String) request.getAttribute("info");
	String error = (String) request.getAttribute("error");
	String deliverId = (String) request.getParameter("deliverId");
%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
			$(function(){
				$("#delivers").combobox({
					url : '${pageContext.request.contextPath}/Combobox/getDeliverList.mmx',
					valueField:'id',
					textField:'text'
				});
			});
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" border="false" style="overflow: hidden;">
		<div id="tt" class="easyui-tabs" style="width: auto;">
		<div title="批量添加包裹单号" style="padding: 20px;">
				<form id="form3" name="form3" method="post" action="${pageContext.request.contextPath}/deliverController/insertPackageCode.mmx">
					<table width="55%" border="0">
						<tr>
							<td>
								 快递公司:<select name="delivers" id="delivers" class="easyui-combobox" panelHeight="auto" style="width:100px"></select>
							</td>
						</tr>
						<tr>
							<td><textarea name="packageCode"id="packageCode" cols="45" rows="5"></textarea> 
								<input type="submit" name="button" id="button"value="提交"><%if(tr==null){%><%}else{%><%=tr %><%} %><%if(error==null){%><%}else{%><%=error %><%} %>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
</body>
</html>