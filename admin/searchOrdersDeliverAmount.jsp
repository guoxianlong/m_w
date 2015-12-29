<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voOrder,adultadmin.action.vo.voUser ,adultadmin.bean.UserGroupBean" %>
<%@ page import="adultadmin.action.admin.SearchOrdersDeliversAction" %>
<%@ page import="adultadmin.bean.OrderDeliversBean, adultadmin.util.StringUtil,adultadmin.util.DateUtil , adultadmin.util.NumberUtil,mmb.stock.stat.*" %>
<%@ include file="../taglibs.jsp"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%
		voUser user = (voUser)session.getAttribute("userView");
		UserGroupBean group = user.getGroup();
		voOrder deliver = new voOrder();
		String stockArea = StringUtil.convertNull(request.getParameter("stockArea"));//保留上页面的值
		String startTime =StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		SearchOrdersDeliversAction searchAction  = new SearchOrdersDeliversAction();
		searchAction.orderDeliverSearch(request,response);
		String deliverSet = StringUtil.convertNull(request.getParameter("deliverSet")); //分类显示
		String firstChecked = StringUtil.convertNull(request.getParameter("firstChecked"));//第一次进入页面 默认选中
		boolean firstChekedFlag=false; //第一次进入页面默认全选
		boolean acountFlag =true; //判断是小计还是统计
		if(firstChecked.equals("1")){
			firstChekedFlag=true;
		}
		
		if(deliverSet.equals("")){
			deliverSet="-1";
		}
		List list = (List)request.getAttribute("orderDeliversList");
		float sum_money=0 , back_money=0,sum_weight=0;//统计子集 的和
		int sum_num =0, back_num=0;
		String[] statuss = request.getParameterValues("deliverValue");
		String status = "";
		//boolean firstChecked =false;
		if(statuss!=null){
			for(int i=0;i<statuss.length;i++){
				status = status + statuss[i]+",";
			}
			acountFlag = deliver.deliverMapAll.size()==statuss.length;
		} 
		
		if(startTime.equals("")){//默认时间 当前月开始一天和最后一天
			String nowDate = DateUtil.getNow();
			String tempStr = nowDate.substring(0,8);
			String mouth = nowDate.substring(5,7);
			startTime = tempStr+"01";
			if(mouth.equals("05")||mouth.equals("01")||mouth.equals("03")||mouth.equals("07")||mouth.equals("08")||mouth.equals("10")||mouth.equals("12")){
				endTime =tempStr+"31";
			}else if(mouth.equals("02")){
				endTime =tempStr+"28";
			}else{
				endTime =tempStr+"30";
			}
		}
		
		int sa = StringUtil.toInt(stockArea);
		String wareAreaLable = ProductWarePropertyService.getWeraAreaAllCustomized("stockArea", "stockArea",sa,true,"");
		
	 %>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>发货订单分库查询</title>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<style type="text/css">
			.tableCss td{
				align:center;
				heigth:25px;
			}
		</style>
		<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
		<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
		<script type="text/javascript">
			function checkboxChecked(checkbox,value){
				var values = value.split(",");
				for(var j = 0; j < values.length; j++){
					for(var i = 0; i < checkbox.length; i++){
						if(checkbox[i].value == values[j]){
							checkbox[i].checked = true;
						}
					}
				}
			}	
			
			function firstChangeChecked(){
				var deliverAmount = document.getElementsByName("deliverAmount");
				var deliverValue =  document.getElementsByName("deliverValue");
				var flag = true; 
				for(var i=0;i<deliverValue.length;i++){
					if(deliverValue[i].checked==false){
						flag=false;
					}
				}
				deliverAmount[0].checked=flag;
			}
			
			function chageChecked(){
				var deliverAmount = document.getElementsByName("deliverAmount");
				var deliverValue =  document.getElementsByName("deliverValue"); 
				for(var i=0;i<deliverValue.length;i++){
					deliverValue[i].checked=deliverAmount[0].checked;
				}
				//document.getElementsByName("deliverSet")[0].checked=deliverAmount[0].checked
			}
			
			function escChecked(){
				var deliverAmount = document.getElementsByName("deliverAmount");
				var deliverValue =  document.getElementsByName("deliverValue");
				for(var i=0;i<deliverValue.length;i++){
					deliverValue[i].checked=false;
				}
				deliverAmount[0].checked=false;
			}
			
			function submitForm(){//js 验证
				var stockArea = document.getElementById("stockArea").value;
				var startTime = document.getElementById("startTime").value;
				var endTime = document.getElementById("endTime").value;
				if(stockArea==null || stockArea=='undefined' ||stockArea==""){
					alert("请先选择一个发货地区，再查询");
					return false;
				}
				if(startTime==null || startTime=='undefined' ||startTime.replace(/\s+/g,"")==""){
					alert("请先填写发货日期，再查询！");
					return false;
				}
				if(endTime==null || endTime=='undefined' ||endTime.replace(/\s+/g,"")==""){
					alert("请先填写发货日期，再查询！");
					return false;
				} 
				if(CheckDate(startTime)==false){
					return false;
				}
				if(CheckDate(endTime)==false){
					return false;
				}
				var day = (getDateFromString(endTime)-getDateFromString(startTime))/(1000*60*60*24);
				if(day<0){
					alert("结束时间必须大于开始时间");
					return false;
				}
				//2012-5-16需求：去除31天限制
				//2013-4-1需求：重新添加31天限制
				if(day>30){
					alert("发货日期时间段不得超过31天,请重新填写！");
					return false;
				} 
				return true;
				//document.getElementById("searchForm").submit();
			}
		  function getDateFromString(strDate){
		        var arrYmd   =  strDate.split("-");
		        var numYear  =  parseInt(arrYmd[0],10);
		        var numMonth =  parseInt(arrYmd[1],10)-1;
		        var numDay   =  parseInt(arrYmd[2],10);
		        var leavetime=new Date(numYear,  numMonth,  numDay);
		        return leavetime;
				       
		  }
		   	
	  	  function CheckDate(strDate){  
	  	  	if(strDate.length!=10){
	  	  		 alert("发货日期格式错误，请重新填写！");  
			     return false;
	  	  	}
			 var reg=/^([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)$/ig;
			  if(!reg.test(strDate)){  
			      alert("发货日期格式错误，请重新填写！");  
			      return false;  
			  }  
             return true;  
          }
		</script>
	</head>
	<body>
	 	<fieldset style="width:98%;">
			<legend>发货订单分库查询</legend>
			<form method="get" action="searchOrdersDeliverAmount.jsp" name="searchForm">
				 发货地区: <span style="color:red">*</span> &nbsp;&nbsp;
				 <%= wareAreaLable%>
				&nbsp;&nbsp;
				 
				 发货日期：<span style="color:red">*</span> &nbsp;&nbsp;
				 <input type="text" id="startTime" name="startTime" onclick="SelectDate(this,'yyyy-MM-dd');" value="<%=startTime==null?"":startTime %>" size="10"/> &nbsp;至&nbsp;&nbsp;
				 <input type="text"  id="endTime" name="endTime" onclick="SelectDate(this,'yyyy-MM-dd');" value="<%=endTime==null?"":endTime %>" size="10"/> 
			<br/> <br/>
				 
				<input type="checkbox"   id="deliverAmount"    onclick="chageChecked()"/>快递公司:&nbsp;&nbsp;
				<%
					Iterator   it =deliver.deliverMapAll.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry entry = (Map.Entry)it.next();
						%>
							<input type="checkbox" id="deliverValue" name="deliverValue" <%=firstChekedFlag?"checked='checked'":"" %> onclick="firstChangeChecked()" value="<%=entry.getKey()%>"/><%=entry.getValue()%>&nbsp;&nbsp; 
						<%
						
					}
				 %>
				<script>checkboxChecked(document.getElementsByName('deliverValue'),'<%=status%>'); firstChangeChecked()</script>
			 <br/><br/>
				显示方式:&nbsp;&nbsp;
				 <input type="radio" id="deliverSet" name="deliverSet" <%if(deliverSet.equals("-1")){ %>checked="checked"<%}%>   value="-1"/>按发货日期显示 
				&nbsp;&nbsp;<input type="radio"  id="deliverSet" name="deliverSet" <%if(deliverSet.equals("1")){ %>checked="checked"<%}%> value="1"/> 仅显示合计
				&nbsp;&nbsp;<input type="submit" value="查询"  onclick="return submitForm();"/>
	 		</form>
	 	</fieldset>	
	 	
 		<table border="1"   bordercolor="#D8D8D5" width="97%" style="margin:10px;border-collapse:collapse;" class="tableCss"> 
 			<tr bgcolor="#99CCFF">
 				<td height="30px" align="center">发货日期</td>
 				<td align="center">快递公司</td>
 				<td align="center">发货订单数量</td>
 				<%if(group.isFlag(28)){%>
 				<td align="center">发货订单总额</td>
 				<%} %>
 				<td align="center">发货订单总重量</td>
 				<td align="center">退回订单数量</td>
 				<%if(group.isFlag(28)){%>
 				<td align="center">退回订单总额</td>
 				<%}%>
 			</tr>
 			<%
			if(list!=null && list.size()>0){
				if(deliverSet.equals("-1")){
					for(int i=0;i<list.size();i++){
						OrderDeliversBean bean =(OrderDeliversBean)list.get(i);
						int temp_int = bean.getSubBeanList()!=null?bean.getSubBeanList().size()+1:0; //往下跨行数量 子集数量+ 本身数量+ 是否需要统计
						temp_int +=1;
						sum_money=0;sum_num=0;back_money=0;back_num=0;sum_weight=0;
						 
					%>
					<tr <%if(statuss==null){ %>style="display:none;"<%}%>>
						<td align="center" rowspan="<%=temp_int%>"><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>" target="_blank"><%=bean.getDateTime()%></a></td>
						<td align="center" height="20px"><%=deliver.deliverMapAll.get(String.valueOf(bean.getDeliver()))%></td>
						<td align="center"><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=<%=stockArea%>&deliver=<%=bean.getDeliver()%>" target="_blank"><%=bean.getSumNum()%></a></td>
						<%if(group.isFlag(28)){%>
						<td align="center"><%=NumberUtil.priceOrder(bean.getSumMoney())%></td>
						<%} %>
						<td align="center"><%=bean.getSumWeight()/1000 %>KG</td>
						<td align="center"><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=<%=stockArea%>&deliver=<%=bean.getDeliver()%>&orderStatus=11" target="_blank"><%=bean.getBackNum()%></a></td>
						<%if(group.isFlag(28)){%>
						<td align="center"><%=NumberUtil.priceOrder(bean.getBackMoney())%></td>
						<%}%>
						 
					</tr>	
						<%
						if(bean.getSubBeanList()!=null &&bean.getSubBeanList().size()>0){
							for(int j=0;j<bean.getSubBeanList().size();j++){
								OrderDeliversBean bean1 =(OrderDeliversBean)bean.getSubBeanList().get(j);
								if(deliverSet!=null && deliverSet.equals("-1")) {//统计 
									//sum_money += bean1.getSumMoney()-bean1.getBackMoney();
									//sum_num += bean1.getSumNum()-bean1.getBackNum();  发货单的数量不包括退回单的数量  金额 
									sum_money += bean1.getSumMoney();
									sum_num += bean1.getSumNum();
									back_money += bean1.getBackMoney();
									back_num +=bean1.getBackNum();
									sum_weight+=bean1.getSumWeight();
								}
								%>
								<tr <%if(statuss==null){ %>style="display:none;"<%}%>>
									<td align="center"><%=deliver.deliverMapAll.get(String.valueOf(bean1.getDeliver()))%><br></td>
									<td align="center"><a href="searchorder.do?stockDate=<%=bean1.getDateTime()%>&stockArea=<%=stockArea%>&deliver=<%=bean1.getDeliver()%>" target="_blank"><%=bean1.getSumNum()%></a><br></td>
									<%if(group.isFlag(28)){%>
									<td align="center"><%=NumberUtil.priceOrder(bean1.getSumMoney())%><br></td>
									<%}%>
									<td align="center"><%=bean1.getSumWeight()/1000 %>KG</td>
									<td align="center"><a href="searchorder.do?stockDate=<%=bean1.getDateTime()%>&stockArea=<%=stockArea%>&deliver=<%=bean1.getDeliver()%>&orderStatus=11" target="_blank"><%=bean1.getBackNum()%></a><br></td>
									<%if(group.isFlag(28)){%>
									<td align="center"><%=NumberUtil.priceOrder(bean1.getBackMoney())%><br></td>
									<%}%>
								</tr>
								<%
							}
						}%>
							<tr>
								<%if(statuss==null){
									%>
									<td align="center"><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>" target="_blank"><%=bean.getDateTime()%></a></td>
									<%
								}%>
								<%if(bean.getSubBeanList().size()>=1|| statuss==null){  %>
									<td align="center"><%if(statuss==null){%>总<% }else{%>小<%}%>计</td>
									<td align="center"><%=sum_num+(bean.getSumNum())%></td>
									<%if(group.isFlag(28)){%>
									<td align="center"><%=NumberUtil.priceOrder(sum_money+bean.getSumMoney())%></td>
									<%} %>
									<td align="center"><%=(sum_weight+bean.getSumWeight())/1000 %>KG</td>
									<td align="center"><%=back_num+bean.getBackNum()%></td>
									<%if(group.isFlag(28)){%>
									<td align="center"><%=NumberUtil.priceOrder(back_money+bean.getBackMoney())%></td>
									<%}%>
								<%}%>
							</tr>
					<%
					}
				}else if(deliverSet.equals("1")){
					sum_money=0;sum_num=0;back_money=0;back_num=0;
					int temp_int = list.size()+1; //往下跨行数量 子集数量+ 本身数量+ 是否需要统计
					temp_int +=1;
					for(int i=0;i<list.size();i++){
						OrderDeliversBean bean =(OrderDeliversBean)list.get(i);
						//sum_money+= bean.getSumMoney()-bean.getBackMoney();
						//sum_num+=bean.getSumNum()-bean.getBackNum();
						sum_money+= bean.getSumMoney();
						sum_num+=bean.getSumNum();
						back_money+=bean.getBackMoney();
						back_num+=bean.getBackNum();
						sum_weight+=bean.getSumWeight();
						%>
						<tr  <%if(statuss==null){ %>style="display:none;"<%}%>><%if(i==0){ %>
							<td align="center" rowspan="<%=temp_int%>"><%=startTime%>至<%=endTime%></td>
							<%}%>
							<td align="center"><%=deliver.deliverMapAll.get(String.valueOf(bean.getDeliver()))%></td>
							<td align="center"><%=bean.getSumNum()%></td>
							<%if(group.isFlag(28)){%>
							<td align="center"><%=NumberUtil.priceOrder(bean.getSumMoney())%></td>
							<%}%>
							<td align="center"><%=bean.getSumWeight()/1000 %>KG</td>
							<td align="center"><%=bean.getBackNum()%></td>
							<%if(group.isFlag(28)){%>
							<td align="center"><%=NumberUtil.priceOrder(bean.getBackMoney())%></td>
							<%}%>
						</tr>	
						<%
							
					}
					%>
					<tr <%if(list.size()==1){ %>style="display:none;"<%}%>>
						<%if(statuss==null){
							%>
							<td align="center" ><%=startTime%>至<%=endTime%></td>
							<%
						}%>
						<td align="center"><%=acountFlag?"总":"小" %>计</td>
						<td align="center"><%=sum_num%></td>
						<%if(group.isFlag(28)){%>
						<td align="center"><%=NumberUtil.priceOrder(sum_money)%></td>
						<%}%>
						<td align="center"><%=sum_weight/1000 %>KG</td>
						<td align="center"><%=back_num%></td>
						<%if(group.isFlag(28)){%>
						<td align="center"><%=NumberUtil.priceOrder(back_money)%></td>
						<%}%>
					</tr>	
					<%
				}
			}else if(list!=null){
				%>
				<tr>
					<td colspan="6" align="center">无数据</td>
				</tr>
				<%
			}
 			%>
 		</table>
	</body>
</html>
