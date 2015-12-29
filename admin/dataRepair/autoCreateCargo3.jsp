<%@page import="adultadmin.bean.cargo.*"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.buy.*,adultadmin.service.*,adultadmin.service.infc.*"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>买卖宝后台</title>
<script language="JavaScript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<logic:notEmpty name="tip">
<script language="JavaScript">
alert('<bean:write name="tip" />');
</script>
</logic:notEmpty>
<body>
<%@include file="../../header.jsp"%>
<form method=post action="autoCreateCargo3.jsp" name="importForm">
<table width="30%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		前缀：<input type="text" name="aCode"/>&nbsp;&nbsp;库类型：<input type="text" name="stockType"/>&nbsp;&nbsp;<br/>
		货架层数：<input type="text" name="floorCount"/>&nbsp;&nbsp;库区域ID：<input type="text" name="stockAreaId"/>&nbsp;&nbsp;<br/>
		仓库ID：<input type="text" name="storageId"/>&nbsp;&nbsp;地区ID：<input type="text" name="areaId"/>&nbsp;&nbsp;<br/>
		城市ID：<input type="text" name="cityId"/>&nbsp;&nbsp;每层货位个数：<input type="text" name="count"/>&nbsp;&nbsp;<br/>
		甬道ID:<input type="text" name="passageIds"/>&nbsp;&nbsp;货位存贮类型：<input type="text" name="storeType"/>&nbsp;&nbsp;<br/>
		<input type="hidden" name="type" value="shelf"/>
		<input type=submit value=" 提 交 ">
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center" colspan=2></td>
	</tr>
</table>
</form>
<%
	String type = StringUtil.convertNull(request.getParameter("type"));
	if (type.equals("shelf")) {
		int stockType = StringUtil.StringToId(request
				.getParameter("stockType"));
		int stockAreaId = StringUtil.StringToId(request
				.getParameter("stockAreaId"));
		int storageId = StringUtil.StringToId(request
				.getParameter("storageId"));
		int areaId = StringUtil.StringToId(request
				.getParameter("areaId"));
		int cityId = StringUtil.StringToId(request
				.getParameter("cityId"));
		int count = StringUtil
				.StringToId(request.getParameter("count"));
		int storeType = StringUtil.StringToId(request
				.getParameter("storeType"));
		String passageIds = StringUtil.convertNull(request
				.getParameter("passageIds"));
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, null);
		try {
			//根据库区域查询所有甬道
			String condition = "stock_area_id = " + stockAreaId;
			if (!passageIds.equals("")) {
				condition = condition + " and id in (" + passageIds
						+ ")";
			}
			List passageList = service.getCargoInfoPassageList(
					condition, -1, -1, "id asc");
			Iterator iter = passageList.listIterator();
			while (iter.hasNext()) {
				CargoInfoPassageBean passage = (CargoInfoPassageBean) iter
						.next();
				List shelfList = service.getCargoInfoShelfList(
						"passage_id = " + passage.getId(), -1, -1,
						"id asc");
				Iterator iter2 = shelfList.listIterator();
				while (iter2.hasNext()) {
					CargoInfoShelfBean shelf = (CargoInfoShelfBean) iter2
							.next();

					int cargoCount = 0;
					for (int j = 1; j <= shelf.getFloorCount(); j++) {
						String code = "";
						for (int i = 1; i <= count; i++) {
							cargoCount++;
							code = cargoCount < 10 ? "0" + cargoCount
									: "" + cargoCount;

							CargoInfoBean cargo = new CargoInfoBean();
							cargo.setCode(code);
							cargo.setWholeCode(shelf.getWholeCode()+j+ code);
							cargo.setStockType(stockType);
							cargo.setFloorNum(j);
							cargo.setStockAreaId(stockAreaId);
							cargo.setStorageId(storageId);
							cargo.setAreaId(areaId);
							cargo.setCityId(cityId);
							cargo.setPassageId(passage.getId());
							cargo.setShelfId(shelf.getId());
							cargo.setMaxStockCount(100);
							cargo.setStatus(1);
							cargo.setRemark("");
							cargo.setStoreType(storeType);
							service.addCargoInfo(cargo);
							
							if(cargoCount == count){
								cargoCount = 0;
							}
						}
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