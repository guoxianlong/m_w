<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.supplier.SupplierUserBean"%>
<%@ page import="adultadmin.bean.supplier.SupplierImprestPayBankBean"%>
<%@ page import="adultadmin.bean.supplier.SupplierImprestInfoBean"%>
<%@ page
	import="adultadmin.bean.supplier.SupplierImprestApplicationBean"%>
<%@ page
	import="adultadmin.bean.supplier.SupplierImprestApplicationLogBean"%>
<%@ page
	import="java.text.SimpleDateFormat,java.text.DateFormat,java.text.DecimalFormat"%>
<%
		voUser user = (voUser) request.getSession().getAttribute("userView");
	SupplierImprestApplicationBean bean = (SupplierImprestApplicationBean) request.getAttribute("beanInfo");
	SupplierUserBean suBean = bean.getSupplierUserBean();
	voUser vUser = suBean.getUser();
	SupplierImprestPayBankBean sipbBean = bean.getSupplierImprestPayBankBean();
	String bankName = "无";
	String bankAccount = "无";
	if (sipbBean != null) {
		bankName = sipbBean.getBank_name();
		if(bankName.trim().equals("")){
			bankName="无";
		}
		bankAccount = sipbBean.getBank_account();
		if(bankAccount.trim().equals("")){
			bankAccount="无";
		}
	}
	List logList = (List) request.getAttribute("logList");
	String statusTitle = (String) request.getAttribute("statusTitle");
	String statusButton = (String) request.getAttribute("statusButton");
	String doo = StringUtil.convertNull(request.getParameter("do"));
	String Url = request.getQueryString();
	String param = Url.indexOf("&search") > 0 ? Url.substring(Url.indexOf("&search")) : "";
	int Pcount=0;
	double money=bean.getAppMoney();
	DecimalFormat df2 = new DecimalFormat("###,##0.00");
								 
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
	
		
  			%>
		<br><br />


		<div id="tableDiv" style="display:none;">
			<table width="100%">
				<tr>
					<td colspan="2">


						<table cellpadding="3" cellspacing="0" border="1"
							style="border-collapse: collapse; border-style: solid; border-width: 2;"
							width="100%">
							<tr align="center" style="width: 460px">
								<td colspan="4" style="font-size: 21pt; font-weight: bold">
									请 款 单

								</td>

							</tr>
							<tr>
								<td colspan="1" style="font-weight: bold;" width="15%">
									申请单编号
								</td>
								<td colspan="2" width="85%" align="left">
									<%=bean. getCode()%>
								</td>
							</tr>
							<tr>
								<td rowspan="2" colspan="1" style="font-weight: bold;width: 15%">
									申请信息
								</td>
								<td colspan="1" style="width: 20%">
									申请人
								</td>
								<td colspan="1" style="width: 65%"><%=suBean==null?"":suBean.getName() %></td>

							</tr>
							<tr>
								<td style=" width: 20%">
									申请金额(RMB)
								</td>
								<td colspan="1" style="width: 65%" >
									<b>￥<%=df2.format(bean.getAppMoney()) %></b>

								</td>
							</tr>


							<tr>
								<td rowspan="2" colspan="1" style="font-weight: bold;width: 15%">
									申请人帐户
								</td>
								<td colspan="1" style="width: 20%">
									打款银行全称
								</td>
								<td colspan="1" style="65%"><%=bankName %></td>

							</tr>
							<tr>
								<td style="width: 20%">
									银行账号
								</td>
								<td colspan="1" style="font-weight: bold;width: 65%">
									<%
								
								int stree=0;
									int banknumlength=bankAccount.length();
									if(bankAccount.length()%4==0){
										stree=bankAccount.length()/4-1;
									}else{
									stree=bankAccount.length()/4;
									}
										for(int z=1;z<=stree;z++){
											bankAccount=bankAccount.substring(0,banknumlength-z*4)+"&nbsp;"+bankAccount.substring(banknumlength-z*4);
										}
										%>



									<%=bankAccount %>
								</td>
							</tr>

				<!-- 此处已去掉申请原因项，2012-5-18需求 -->
				
							<tr>
								<td colspan="3">
									申请审核记录
								</td>
							</tr>
							<tr>
								<td colspan="3"
									style="padding-left: 0; padding-right: 0; padding-top: 0; padding-bottom: 0">
									<table cellpadding="3" cellspacing="0" border="1"
										style="border-collapse: collapse; margin: -1px 0px; border-right-style: none"
										width="100%">
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
										
										int maxid[]={-1,-1,-1,-1};
											int[]indexI={-1,-1,-1,-1};
										if(logList.size()>0){
											for(int i=0;i<logList.size();i++){
												SupplierImprestApplicationLogBean sialBean=(SupplierImprestApplicationLogBean)logList.get(i);
												if(sialBean.getType()==0){
													if(sialBean.getApplication_opered_status()==1&&maxid[0]<sialBean.getId()){
														maxid[0]=sialBean.getId();
														indexI[0]=i;
													}
													if(sialBean.getApplication_opered_status()==2&&maxid[1]<sialBean.getId()){
														maxid[1]=sialBean.getId();
														indexI[1]=i;
													}
													if(sialBean.getApplication_opered_status()==4&&maxid[2]<sialBean.getId()){
														maxid[2]=sialBean.getId();
														indexI[2]=i;
													}
													if(sialBean.getApplication_opered_status()==6&&maxid[3]<sialBean.getId()){
														maxid[3]=sialBean.getId();
														indexI[3]=i;
													}
													
												}
												if(maxid[0]>maxid[1]){
													indexI[1]=-1;
													indexI[2]=-1;
													indexI[3]=-1;
												}else if(maxid[1]>maxid[2]){
													indexI[2]=-1;
													
												}else if(maxid[2]>maxid[3]){
													indexI[3]=-1;
												}
											}
											
										
											
												for(int j=0;j<indexI.length;j++){
													if(indexI[j]!=-1){
										              SupplierImprestApplicationLogBean sial=(SupplierImprestApplicationLogBean)logList.get(indexI[j]);
													%><tr>
											<td align="center"><%= j+1%></td>
											<td><%=sial.getOper_datetime().substring(0,19) %></td>
											<td><%=sial.getAdmin_name() %></td>
											<td><%=SupplierImprestApplicationBean.statusMap.get(Integer.valueOf(sial.getApplication_opered_status())) %></td>
										</tr>
										<%
													}
										
											}
											
											}
										 %>



									</table>
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<tr>
					<td>

						打印：<%=user.getUsername() %> &nbsp;
						<%DateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");%><%=f.format(new Date()) %>
					</td>
					<td style="font-size: 16px">
						总经理签字：______________
					</td>
				</tr>
			</table>

		</div>



		<br />


<script>
		var cssStyle = "<style>table{font-size:12px;}</style>";
  	 		
  	 		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		
		
		
			
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
			LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","25.8cm",cssStyle+document.getElementById("tableDiv").innerHTML);
			
		
				//LODOP.SET_PREVIEW_WINDOW(1,1,0,0,00,"打印发货清单.打印");	
				//LODOP.PREVIEWB();
				//LODOP.PREVIEW();
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.PRINTB();
				LODOP.NEWPAGE();
				//	alert(LODOP.SET_PRINT_MODE("PRINT_START_PAGE",2));
				//	alert(LODOP.SET_PRINT_MODE("PRINT_END_PAGE",1));
					//LODOP.PRINT_DESIGN();
			
			}else{
	
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
	window.history.back(-1);
</script>
		<!--  <input type="button" value="打印发货清单" onclick="initPrint();" id="in" />-->

	</body>
</html>
