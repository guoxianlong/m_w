<%@page import="adultadmin.bean.cargo.CargoInfoShelfBean"%>
<%@page import="adultadmin.bean.cargo.CargoInfoPassageBean"%>
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
<form method=post action="autoCreateCargo2.jsp" name="importForm">
<table width="30%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		前缀：<input type="text" name="aCode"/>&nbsp;&nbsp;库类型：<input type="text" name="stockType"/>&nbsp;&nbsp;<br/>
		货架层数：<input type="text" name="floorCount"/>&nbsp;&nbsp;库区域ID：<input type="text" name="stockAreaId"/>&nbsp;&nbsp;<br/>
		仓库ID：<input type="text" name="storageId"/>&nbsp;&nbsp;地区ID：<input type="text" name="areaId"/>&nbsp;&nbsp;<br/>
		城市ID：<input type="text" name="cityId"/>&nbsp;&nbsp;添加货架个数：<input type="text" name="count"/>&nbsp;&nbsp;<br/>
		甬道ID:<input type="text" name="passageIds"/>
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
if(type.equals("shelf")){
	int stockType = StringUtil.StringToId(request.getParameter("stockType"));
	int floorCount = StringUtil.StringToId(request.getParameter("floorCount"));
	int stockAreaId = StringUtil.StringToId(request.getParameter("stockAreaId"));
	int storageId = StringUtil.StringToId(request.getParameter("storageId"));
	int areaId = StringUtil.StringToId(request.getParameter("areaId"));
	int cityId = StringUtil.StringToId(request.getParameter("cityId"));
	int count = StringUtil.StringToId(request.getParameter("count"));
	String passageIds = StringUtil.convertNull(request.getParameter("passageIds"));
	ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,null);
	try{
		//根据库区域查询所有甬道
		String condition = "stock_area_id = "+stockAreaId;
		if(!passageIds.equals("")){
			condition = condition + " and id in ("+passageIds+")";
		}
		List passageList = service.getCargoInfoPassageList(condition,-1,-1,"id asc");
		Iterator iter = passageList.listIterator();
		while(iter.hasNext()){
			CargoInfoPassageBean passage = (CargoInfoPassageBean)iter.next();
			String code = "";
			for(int i=1;i<=count;i++){
				code = i<10?"0"+i:""+i;
				
				CargoInfoShelfBean shelf = new CargoInfoShelfBean();
				shelf.setCode(code);
				shelf.setWholeCode(passage.getWholeCode()+code);
				shelf.setStockType(stockType);
				shelf.setFloorCount(floorCount);
				shelf.setStockAreaId(stockAreaId);
				shelf.setStorageId(storageId);
				shelf.setAreaId(areaId);
				shelf.setCityId(cityId);
				shelf.setPassageId(passage.getId());
				service.addCargoInfoShelf(shelf);
			}
		}
%>
货架添加成功
<%
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		service.releaseAll();
	}
}
 %>
</body>
</html> 