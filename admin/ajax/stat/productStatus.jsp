<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="java.util.*" %><%@ page import="adultadmin.action.vo.*" %><%@ page import="adultadmin.bean.*,adultadmin.util.db.DbOperation" %>
<%@ page import="adultadmin.service.*, adultadmin.service.infc.*" %>
<%
	response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

	voUser user = (voUser)session.getAttribute("userView");

    UserGroupBean group = user.getGroup();

	if(!group.isFlag(74))
		return;
		
	int id = StringUtil.toInt(request.getParameter("id"));
	int status = StringUtil.toInt(request.getParameter("status"));

	StringBuilder bur = new StringBuilder(32);
	IProductService service = ServiceFactory.createProductService(IBaseService.CONN_IN_SERVICE, null);
	IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
	try {
		voProduct product = adminService.getProduct(id);
		if(product==null || product.getStatus()==status)	// 商品不存在或者商品状态没有变化
	return;
		
		// 修改商品状态
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		try {
	dbOp.executeUpdate("update product set status=" + status + " where id=" + id);
		} catch (Exception e) {
	return;
		} finally {
	dbOp.release();
		}
		
		
		// 加入商品修改日志
		
		
		// 产品状态status
		StringBuilder sb = new StringBuilder(64);
		sb.append("修改了产品编号");
		sb.append(product.getCode());
		sb.append("的产品状态(");
		sb.append(product.getStatus());
		sb.append("-");
		sb.append(status);
		sb.append(")");

		String currentTime = DateUtil.getNow();
		ProductAdminHistoryBean log = new ProductAdminHistoryBean();
		log.setAdmin_name(user.getUsername());
		log.setOper_datetime(currentTime);
		log.setProduct_id(id);
		log.setRemark(sb.toString());

		service.addProductAdminHistory(log);

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		service.releaseAll();
		adminService.close();
	}
%>