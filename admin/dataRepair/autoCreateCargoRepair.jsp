<%@page import="adultadmin.bean.cargo.*"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page
	import="adultadmin.bean.buy.*,adultadmin.service.*,adultadmin.service.infc.*"%><%@ include
	file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8"%>
<html>
<title>小店后台管理</title>
<script language="JavaScript" src="js/JS_functions.js"></script>
<link href="/adult-admin/css/global.css" rel="stylesheet"
	type="text/css">
<logic:notEmpty name="tip">
	<script language="JavaScript">
	alert('<bean:write name="tip" />');
</script>
</logic:notEmpty>
<body>
<%@include file="../../header.jsp"%>
<form method=post action="autoCreateCargoRepair.jsp" name="importForm">
<table width="30%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8"
	align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>2012-04-19，芳村新增货位临时处理2&nbsp;&nbsp;&nbsp;&nbsp; <input
			type="hidden" name="type" value="shelf" /> <input type=submit
			value=" 提 交 "></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center" colspan=2></td>
	</tr>
</table>
</form>
<%
	String type = StringUtil.convertNull(request.getParameter("type"));
	if (type.equals("shelf")) {
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, null);
		try {
			//根据库区域查询所有甬道
			String condition = "(id >= 18 and id <= 46) or (id >= 67 and id <= 76) or (id >= 101 and id <= 106) ";
			List shelfList = service.getCargoInfoShelfList(condition,
					-1, -1, "id asc");
			Iterator iter2 = shelfList.listIterator();
			while (iter2.hasNext()) {
				CargoInfoShelfBean shelf = (CargoInfoShelfBean) iter2
						.next();

				int cargoCount = 0;

				List cargoList = service.getCargoInfoList("shelf_id = "
						+ shelf.getId() + " and floor_num in (4,5)",
						-1, -1, "id asc");
				Iterator iter = cargoList.listIterator();
				String code = "";
				while (iter.hasNext()) {
					CargoInfoBean _cargoInfo = (CargoInfoBean) iter
							.next();

					cargoCount++;
					code = cargoCount < 10 ? "0" + cargoCount : ""
							+ cargoCount;

					service.updateCargoInfo("whole_code = '"
							+ shelf.getWholeCode()
							+ _cargoInfo.getFloorNum() + code
							+ "',code = '" + code + "'","id = "+_cargoInfo.getId());
					
					if(cargoCount == 24){
						cargoCount = 0;
					}
				}

			}
%>
货位添加成功
<%
	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
%>
</body>
</html>
