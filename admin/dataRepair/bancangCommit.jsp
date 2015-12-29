<%@ include file="../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="java.io.StringReader"%>
<%@page import="java.io.BufferedReader"%>

<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.service.IAdminService"%>
<%@page import="adultadmin.service.ServiceFactory"%>
<%@page import="adultadmin.service.infc.ICargoService"%>
<%@page import="adultadmin.service.infc.IBaseService"%>
<%@page import="adultadmin.service.infc.IProductStockService"%>
<%@page import="adultadmin.bean.cargo.CargoOperationBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperationProcessBean"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperationCargoBean"%>
<%@page import="adultadmin.bean.cargo.CargoProductStockBean"%>
<%@page import="adultadmin.action.vo.voProduct"%>
<%@page import="adultadmin.bean.cargo.CargoInfoBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperationLogBean"%>
<%@page import="adultadmin.util.DateUtil"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperLogBean"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%><html>
<head>
<title>搬仓确认提交调拨单</title>
</head>
<body>
<form action="" method="post">
	调拨单号：<br/>
	<textarea rows="20" cols="100" name="operCode"></textarea>
	<input type="submit" value="提交"/>
</form>
<%
	int count=0;
if(request.getParameter("operCode")!=null){
	String operCodeList=request.getParameter("operCode");
	List operList=new ArrayList();//有问题的调拨单
	List operList2=new ArrayList();//没有问题的调拨单
	BufferedReader br = new BufferedReader(new StringReader(operCodeList));
	String operCode = null;
	IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
	ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	
	try{
		while((operCode = br.readLine()) != null ){
	service.getDbOp().startTransaction();
	voUser user = (voUser)request.getSession().getAttribute("userView");
	if(user == null){
		break;
	}
		CargoOperationBean cargoOperation = service.getCargoOperation("code = '"+operCode+"'");
		if(cargoOperation == null){
	operList.add(operCode+",调拨单不存在");
	service.getDbOp().rollbackTransaction();
	continue;
		}
		if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28){
	operList.add(operCode+",调拨单已确认");
	service.getDbOp().rollbackTransaction();
	continue;
		}
		boolean nextLine=false;
		
		//只有源货位的coc列表
		List outCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 1 and in_cargo_product_stock_id=0", -1, -1, "id asc");
		for(int i=0;i<outCocList.size();i++){
	CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
	CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
	voProduct product=adminService.getProduct(outCoc.getProductId());//商品
	CargoInfoBean outCargoBean=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");//源货位
	CargoOperationLogBean logBean=new CargoOperationLogBean();//作业单操作记录 
	logBean.setOperId(cargoOperation.getId());
	logBean.setOperDatetime(DateUtil.getNow());
	logBean.setOperAdminId(user.getId());
	logBean.setOperAdminName(user.getUsername());
	logBean.setRemark("确认提交");
	service.addCargoOperationLog(logBean);
	
	int productCount=0;
	//源货位为outCoc的目的货位列表
	List inCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and out_cargo_product_stock_id = "
			+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
	for(int j=0;j<inCocList.size();j++){
		CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
		CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
		CargoInfoBean inCi=service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
		if(inCps!=null&&outCps!=null){
			//散件区调拨单提交时不用锁定源货位
				if(!service.updateCargoProductStockCount(outCps.getId(), -inCoc.getStockCount())){
					operList.add(operCode+",源货位库存不足");
					nextLine=true;
	                    }
				if(!service.updateCargoProductStockLockCount(outCps.getId(), inCoc.getStockCount())){
					operList.add(operCode+",源货位库存不足");
					nextLine=true;
	                    }
			if(!service.updateCargoSpaceLockCount(inCi.getId(), inCoc.getStockCount())){
				operList.add(operCode+",目的货位库存不足");
				nextLine=true;
		                    }
			//调整合格库库存
			if(outCargoBean.getAreaId()!=inCi.getAreaId()){
				productCount+=inCoc.getStockCount();//计算调拨的产品总数
			}
		}else{
			operList.add(operCode+",作业单产品信息异常");
			nextLine=true;
		}
		
	}
	//调整合格库库存
	CargoInfoBean outCi=service.getCargoInfo("id="+outCps.getCargoId());
	CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
	ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
	if(outProductStock==null){
		operList.add(operCode+",合格库库存数据异常");
		nextLine=true;
	}
	if (!psService.updateProductStockCount(outProductStock.getId(),-productCount)) {
		operList.add(operCode+",源货位合格库库存不足");
		nextLine=true;
	}
	if (!psService.updateProductLockCount(outProductStock.getId(),productCount)) {
		operList.add(operCode+",合格库库存锁定量数据异常");
		nextLine=true;
	}
	
	//删除coc无用数据
	service.deleteCargoOperationCargo("oper_id = "+cargoOperation.getId()+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 0");
		}
		
		service.updateCargoOperation("status = "+CargoOperationProcessBean.OPERATION_STATUS29+",effect_status=0,last_operate_datetime='"+DateUtil.getNow()+"',confirm_datetime = '"+DateUtil.getNow()+"',confirm_user_name = '"+user.getUsername()+"'", "id = "+cargoOperation.getId());
		
		//修改上一操作日志的时效
		CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+cargoOperation.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
		if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
	CargoOperationProcessBean tempProcess=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//生成作业单
	int effectTime=tempProcess.getEffectTime();//生成阶段时效
	String lastOperateTime=lastLog.getOperDatetime();
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	long date1=sdf.parse(lastOperateTime).getTime();
	long date2=sdf.parse(DateUtil.getNow()).getTime();
	if(date1+effectTime*60*1000<date2){//已超时
		service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
	}
		}
		
		CargoOperLogBean operLog=new CargoOperLogBean();
		operLog.setOperId(cargoOperation.getId());
		operLog.setOperCode(cargoOperation.getCode());
		CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS28);
		CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS29);
		operLog.setOperName(process2.getOperName());
		operLog.setOperDatetime(DateUtil.getNow());
		operLog.setOperAdminId(user.getId());
		operLog.setOperAdminName(user.getUsername());
		operLog.setHandlerCode("");
		operLog.setEffectTime(0);
		operLog.setRemark("");
		operLog.setPreStatusName(process.getStatusName());
		operLog.setNextStatusName(process2.getStatusName());
		service.addCargoOperLog(operLog);
		count++;
		operList2.add(operCode);
		if(nextLine==true){
	service.getDbOp().rollbackTransaction();
	continue;
		}else{
	service.getDbOp().commitTransaction();
		}
		}
		
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}finally{
		service.releaseAll();
	}
	for(int i=0;i<operList.size();i++){
%>
		<%=operList.get(i).toString() %><br/>
	<%}%>
	操作完成： <%=count %>个作业单<br/>
	<%for(int i=0;i<operList2.size();i++){%>
		<%=operList2.get(i).toString() %><br/>
	<%} %>
<%}
%>

</body>
</html>