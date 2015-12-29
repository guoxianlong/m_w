<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
</script>
</head>
<body>
	<div style="height: 750px;">
	<div id="tb_look"  style="height: auto;display: none;">
		<table border="0">
			<tr>
			 	<td align="left">填表日期：</td>
			 	<td><label id="createDatetime_lable_look" style="color: blue"></label> </td>
	 		 	<td>填表人：<label id="createUserName_lable_look"  style="color: blue"></label> </td>
	   			<td>用途：<label id="target_lable_look"  style="color: blue"></label> </td> 
	 	  </tr>
	 	  <tr>
	 	  		<td rowspan="2" align="left"> 	审核意见：</td>
	 	  </tr>
	 	  <tr>
	 	  	<td colspan="3" rowspan="2"> <label id="remark_look" style="color: blue"></label></td>
	 	  </tr>
		</table>
	</div>
	<table id="datagrid_look"></table> 
	</div>
</body>
</html>