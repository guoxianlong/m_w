<%@ page language="java" import="java.util.*,adultadmin.bean.buy.SupplierPayApplicationBean,adultadmin.bean.buy.SupplierPayApplicationBuyOrderBean,adultadmin.bean.supplier.*,adultadmin.util.*,java.text.DecimalFormat" pageEncoding="utf-8"%>
<%@ page import="java.text.SimpleDateFormat,java.text.DateFormat" %>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.bean.buy.SupplierPayApplicationLogBean" %>
<%!
	static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>My JSP 'supplierFinanceFormPrint.jsp' starting page</title>
		<script language="javascript"
			src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
		<object id="LODOP"
			classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>
			<embed id="LODOP_EM" type="application/x-print-lodop" width=0
				height=0 pluginspage="install_lodop.exe"></embed>
		</object>
	</head>

	<body>
		<%
		voUser user = (voUser) request.getSession().getAttribute("userView");
		int bianjie=0;
		Map buyMap=(Map)request.getAttribute("buyOrderMap");
		List buyOrderList =null; 
		//(List) request.getAttribute("supplierApplicationBuyOrderList");
		List alist=null;
  if(request.getAttribute("alist")!=null){
  	 alist=(ArrayList)request.getAttribute("alist");
  	 
  
  	}
  	
  	int sumpage=0;
  	int countPage=0;//记录总页数
  	if(alist!=null&&alist.size()>0){
  		for(int i=0;i<alist.size();i++){
  		
  			SupplierPayApplicationBean bean=(SupplierPayApplicationBean)alist.get(i);//审请单bean
  			
  			buyOrderList=(List)(buyMap.get(bean.getCode()));//获取采购定单列表
  			buyOrderList.addAll(buyOrderList);
  			buyOrderList.addAll(buyOrderList);
  			
  			SupplierPayApplicationLogBean beanLog=null;//操作日志对象
  			
  			List spalLogList=bean.getSupplierPayApplicationLogList();//操作晶对象的list
  			
  			
  				SupplierBankAccountBean bankAccount = null;//银行卡的bean
  				SupplierStandardInfoBean sBean = bean
									.getSupplierStandardInfo();  //供应商
  			
				if(bean.getSupplierBankAccountList()!=null&&bean.getSupplierBankAccountList().size() > 0){
				if(bean.getSupplierBankAccountIds()!=null&&bean.getSupplierBankAccountIds().length()>0){
				String bankids=	bean.getSupplierBankAccountIds();
					String id[]=bankids.split(",");
					int bankid=StringUtil.StringToId(id[0]);
					for(int bid=0;bid<bean.getSupplierBankAccountList().size();bid++){
						bankAccount = (SupplierBankAccountBean)bean.getSupplierBankAccountList().get(bid);
						if(bankAccount.getId()==bankid){
							break;
						}
					}
				}
				
						//	bankAccount = (SupplierBankAccountBean)bean.getSupplierBankAccountList().get(0);
				}
  			
  			List spaboList = bean.getSupplierPayApplicationBuyOrderList();//采购定单bean
  		
  			int newpage=0;//一个审请单要分几页－－－－－－（上线时把2改为20）
  		
  		//占时把采购定单分页设为2
  		
  			if(spaboList.size()%20==0&& spaboList.size()!=0){
  				newpage=spaboList.size()/20;
  			}else if(spaboList.size()!=0){
  				newpage=spaboList.size()/20+1;
  			}else{
  		     	newpage=1;
  			}
  			
  			//countPage=countPage+newpage;//一共需要多少页原纸
  			
  			for(int j=1;j<=newpage;j++){
  			sumpage=sumpage+1;
  			countPage=countPage+1;
  			%>
  			<br/>
  			
  				
  				<div id="tableDiv<%=sumpage %>" style="display:none;">
			<table width="100%">
				<tr>
					<td colspan="2">


						<table cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse;border-style: solid; border-width:2;" width="100%">
							<tr align="center" style="width: 460px">
								<td colspan="3" style="font-size:x-large; font-weight: bold">
									请   款   单
								</td>

							</tr>
							<tr>
								<td colspan="1" style="font-weight: bold;" width="20%" >
									申请单编号
								</td>
								<td colspan="2" width="80%" align="left">
									<%=bean.getCode() %>
								</td>
							</tr>
							<tr>
								<td colspan="1" style="font-weight: bold;" width="20%">
									供应商名称
								</td>
								<td colspan="2" style=" width: 80%">
								<%double money=bean.getAppMoney();
									DecimalFormat df2 = new DecimalFormat("###,##0.00");
								 %>
									<%=sBean.getName() %>
									
								</td>
							</tr>
							<tr>
								<td rowspan="3" colspan="1" style="font-weight: bold;width: 20%">
									本次汇款账户
								</td>
								<td colspan="1" style="width: 20%">
									帐户名称
								</td>
								<td colspan="1" style="width: 60%">
								
									<%=bankAccount != null ? bankAccount.getAccount_name():"&nbsp;" %>
								</td>
							</tr>
							<tr>
								<td colspan="1" width="20%">
									银行帐号
								</td>
								<td colspan="1" style="font-weight: bold;width: 60%">
								<%
									String banknum=bankAccount!=null?bankAccount.getAccount_number():null;
									if(banknum!=null){
									int stree=0;
									int banknumlength=banknum.length();
									if(banknum.length()%4==0){
										stree=banknum.length()/4-1;
									}else{
									stree=banknum.length()/4;
									}
										for(int z=1;z<=stree;z++){
											banknum=banknum.substring(0,banknumlength-z*4)+"&nbsp;"+banknum.substring(banknumlength-z*4);
										}
										%>
										<%=banknum %>
										<%
									}else{
									%>
									<%="&nbsp;" %>
									<%
									}
								 %>
			
								</td>
							</tr>
							<tr>
								<td colspan="1" >
									开户行全称
								</td>
								<td colspan="1" >
									<%=bankAccount != null ? bankAccount.getBank_full_name():"&nbsp;" %>
								</td>
							</tr>
								<tr>
								<td rowspan="1" colspan="1" style="font-weight: bold;width: 20%">
									结算方式
								</td>
								<td colspan="2" style="width: 80%">
									<%=bean.getPayUseName()%>
								</td>
							</tr>
							
							<!-- 押金申请单金额显示 -->
							<%if(bean.getType() == 0){ %>
								<tr>
								<td rowspan="1" colspan="1" style="font-weight: bold;width: 20%">
									本次申请金额
								</td>
								<td colspan="2" style="width: 80%">
									<b>￥<%=df2.format(bean.getAppMoney())%></b>
								</td>
							</tr>
							<%} %>
							
							
							<%if(spaboList.size()!=0){
								double moneyOne=0;
								double moneyTwo=0;
								double moneyThree=0;
								double moneyFour=0;
								//double moneyD=0;
							 %>
							<tr >
							<td colspan="3" width="100%" style="padding-left: 0;padding-right: 0;padding-top: 0;padding-bottom: 0">
								<table   cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: -1 0px;border-right-style:none;border-bottom-style:none"   width="100%">
									<tr align="right">
										<td colspan="1"  width="25%"  >
											预计进货总额
										</td>
										<td colspan="1"  width="25%"  >
											已入库总额
										</td>
										<td colspan="1"  width="25%"  >
											货款已支付总额
										</td>
										<td colspan="1"  width="25%"  >
											本次申请金额
										</td>
									</tr>
								<%
								//j表示当前审请单的第几页
								
								for(int x=(j-1)*20;x<j*20;x++){
  									if(spaboList.size()>x){
  									//---------------------------------------------------------
  									
	  									HashMap buyOrderMap = (HashMap) buyOrderList.get(x);
	  									double lockMoney = Arith.add(Double.parseDouble(String.valueOf(buyOrderMap.get("lock_money")))
												, Double.parseDouble(String.valueOf(buyOrderMap.get("lock_deposit"))));//冻结金额
										double noLockMoney = Arith.add(Double.parseDouble(String.valueOf(buyOrderMap.get("money"))), 
												Double.parseDouble(String.valueOf(buyOrderMap.get("deposit"))));//未冻结金额
										double payTotal = Arith.add(lockMoney, noLockMoney);//已支付总金额
										double thsappmoney = Double.parseDouble(String.valueOf(buyOrderMap.get("thsappmoney")));//本次申请金额
										if (bean.getType() == SupplierPayApplicationBean.TYPE3 
												|| bean.getType() == SupplierPayApplicationBean.TYPE5) {
											if (bean.getStatus() == SupplierPayApplicationBean.STATUS1 || 
													bean.getStatus() == SupplierPayApplicationBean.STATUS2 || 
													bean.getStatus() == SupplierPayApplicationBean.STATUS4) {
												lockMoney = Arith.sub(lockMoney, Double.parseDouble(String.valueOf(buyOrderMap.get("thsappmoney"))));
											}
										}
										double depositTotal = Arith.add(Double.parseDouble(String.valueOf(buyOrderMap.get("deposit"))) 
													, Double.parseDouble(String.valueOf(buyOrderMap.get("lock_deposit"))));//定金总额
		  									
		  									//-------------------------------------------------------------
		  								//SupplierPayApplicationBuyOrderBean buyOrderBean=(SupplierPayApplicationBuyOrderBean)spaboList.get(x);
										
										moneyOne=moneyOne+Double.parseDouble(String.valueOf(buyOrderMap.get("order_total")));
										moneyTwo=moneyTwo+Double.parseDouble(String.valueOf(buyOrderMap.get("totalAfterStockin"))); 
										moneyThree=moneyThree+payTotal; 
										moneyFour=moneyFour+thsappmoney; 
							
									}else if(spaboList.size()==0){
										break;
									}
	 							}
								 %>
								<tr align="right" >
									<td colspan="1" >
										￥<%=df2.format(moneyOne) %>
									</td>
									<td colspan="1" >
										￥<%=df2.format(moneyTwo) %>
									</td>
									<td colspan="1"  >
										￥<%=df2.format(moneyThree)%>
									</td>
									<td colspan="1"  >
										￥<%=df2.format(moneyFour) %>
									</td>
								</tr>
								</table>
							</td>
							</tr>
							<% }%>
							
							<tr  >
								<td colspan="3" >
									申请审核记录
								</td >
							</tr>
							<tr>
								<td colspan="3" style="padding-left: 0;padding-right: 0;padding-top: 0;padding-bottom: 0">
									<table  cellpadding="3" cellspacing="0" border="1"
				style="border-collapse: collapse; margin: -1px 0px ;border-right-style: none" width="100%">
										<tr>
											<td colspan="1" align="center" style="width: 5%">
												序号
											</td>
											<td colspan="1" style="width: 20%">
												申请/审核时间
											</td>
											<td colspan="1" style="width: 15%">
												申请/审核人
											</td>
											<td colspan="1" style="width: 60%">
												状态
											</td>
										</tr>
									
							<%
							for(int marklog=0;marklog<spalLogList.size();marklog++){
								int[] status={1,8,2,4};
								String[] st={"已申请","采购产品线经理审核通过","部门经理审核通过","财务审核通过"};
								for(int loglist=0;loglist<spalLogList.size();loglist++){
								
									beanLog=(SupplierPayApplicationLogBean)spalLogList.get(loglist);
									if(beanLog.getApplicationOperedStatus()==status[marklog]){
								
							 %> 
										<tr>
											<td align="center" colspan="1" >
												<%=loglist+1 %>
											</td>
											<td colspan="1" >
												<%=beanLog.getOperDatetime().substring(0,(beanLog.getOperDatetime().replace(".0","").lastIndexOf(":")))%>
											</td>
											<td colspan="1" >
												<%=beanLog.getAdminName()+"&nbsp;" %>
											</td>
											<td colspan="1" >
												<%=st[marklog]+"&nbsp;"%>
											</td>
										</tr>
							<% }
							}
						}%>
							</table>
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
				<tr>
					<td>
						
						 打印：<%=user.getUsername() %> &nbsp; <%DateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");%><%=f.format(new Date()) %> 
					</td>
					<td style="font-size: 16px">
						总经理签字：______________
					</td>
				</tr>
			</table>

		</div>
  				
  			<%
  				}
  			%>
		<br/>
		<%
  		}
  	
  	}else{
  	
  	}
  	 %>
  	
  	 		<script type="text/javascript">
  	 		var cssStyle = "<style>table{font-size:12px;}</style>";
  	 		
  	 		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		
		<%
		
			for (int j = 1; j <=countPage; j++) {
		%>
		
			
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
			LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","25.8cm",cssStyle+document.getElementById("tableDiv<%=j%>").innerHTML);
			
		
//				LODOP.SET_PREVIEW_WINDOW(1,1,0,0,00,"打印发货清单.打印");	
				//LODOP.PREVIEWB();
				//LODOP.PREVIEW();
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.PRINTB();
				LODOP.NEWPAGE();
				//	alert(LODOP.SET_PRINT_MODE("PRINT_START_PAGE",2));
				//	alert(LODOP.SET_PRINT_MODE("PRINT_END_PAGE",1));
					//LODOP.PRINT_DESIGN();
					
			<%
			
				}
				
			%>
			}else{
	
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
	window.history.back(-1);
	
  	 		
  	 		
  	 		
  	 		
  	 		
function initPrint(){
	
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		
		<%
		
			for (int j = 1; j <=countPage; j++) {
		%>
		
			
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
			LODOP.ADD_PRINT_TABLE("0.8cm","1.1cm","18.2cm","25.8cm",document.getElementById("tableDiv<%=j%>").innerHTML);
			
		
//				LODOP.SET_PREVIEW_WINDOW(1,1,0,0,00,"打印发货清单.打印");	
				//LODOP.PREVIEWB();
				LODOP.PREVIEW();
			//	LODOP.PRINTB();
				LODOP.NEWPAGE();
				//	alert(LODOP.SET_PRINT_MODE("PRINT_START_PAGE",2));
				//	alert(LODOP.SET_PRINT_MODE("PRINT_END_PAGE",1));
					//LODOP.PRINT_DESIGN();
			<%
				}
	
			%>
			}else{
	
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}
</script>

<br/>

		<!--  <input type="button" value="打印发货清单" onclick="initPrint();" id="in" />-->

	</body>
</html>
