<%@ include file="../../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.action.vo.voOrder" %>
<%@ page import="adultadmin.bean.*" %>

<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
 	
	int p = StringUtil.StringToId(request.getParameter("p"));
	String checkStr = StringUtil.convertNull(request.getParameter("checkStr"));
	String hasNextPage = StringUtil.convertNull(request.getAttribute("hasNextPage")+"");
	boolean nextPageFlag=true;
	if(hasNextPage.equals("No")){
		nextPageFlag=false;
	}
	boolean checkFlag=false;
	if(checkStr!="" &&!checkStr.equals("")){
		checkFlag =true;
	}
	voOrder deliver = new voOrder();
	Iterator   it =deliver.deliverMapAll.entrySet().iterator();
	List deliverList = new ArrayList();
	String deliverValues="";
	while(it.hasNext()){
		Map.Entry entry = (Map.Entry)it.next();
		deliverValues+=entry.getKey()+",";
		deliverList.add(entry.getKey());
	}
	deliverValues = deliverValues.substring(0,deliverValues.length()-1);
	List list = (List)request.getAttribute("orderDeliversList");
	
%>
<html>
	<head>
		<title>买卖宝后台</title>
	    <%@include file="../../header.jsp"%>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
			function showTheAmount(value,listLength){
				var showTd = document.getElementById('showTd');
				var values = value.split(",");
				if(showTd!="undefind" && showTd!=null ){
					if(showTd.checked==true){
						for(var j = 0; j < values.length; j++){
							document.getElementById('showAmount'+values[j]+'a').style.display='block';
							document.getElementById('showAmount'+values[j]+'b').style.display='block';
							for(var i=0;i<listLength;i++){
								document.getElementById('showAmount'+i+values[j]+'aa').style.display='block';
								document.getElementById('showAmount'+i+values[j]+'bb').style.display='block';
							}
						}
						
					}else{
						for(var j = 0; j < values.length; j++){
							document.getElementById('showAmount'+values[j]+'a').style.display='none';
							document.getElementById('showAmount'+values[j]+'b').style.display='none';
							
							for(var i=0;i<listLength;i++){
								document.getElementById('showAmount'+i+values[j]+'aa').style.display='none';
								document.getElementById('showAmount'+i+values[j]+'bb').style.display='none';
							}
						}
					}
				}
			}
			
			function forwardPage(type){
				var showTd = document.getElementById('showTd');
				if(showTd!="undefind" && showTd!=null ){
					if(showTd.checked==true){
						if(type==1){
							document.location.href="stockStatFang.do?p=<%=p-1%>&checkStr=1";
						}else if(type==2){
							document.location.href="stockStatFang.do?p=<%=p+1%>&checkStr=1";
						}
					}else{
						if(type==1){
							document.location.href="stockStatFang.do?p=<%=p-1%>";
						}else if(type==2){
							document.location.href="stockStatFang.do?p=<%=p+1%>";
						}
					}
				}else{
					if(type==1){
						document.location.href="stockStatFang.do?p=<%=p-1%>";
					}else if(type==2){
						document.location.href="stockStatFang.do?p=<%=p+1%>";
					}
				}
			}
			function numberFormat(val){
				
			}
		</script>
	</head>
	<body>
		&nbsp;&nbsp;&nbsp;芳村发货统计（按快递公司）<br />
		&nbsp;&nbsp;&nbsp;<%if(p>0){%><a style="cursor:pointer;" onclick="forwardPage(1)">上一页</a><%}else{%>上一页<%}%>
		&nbsp;<%if(nextPageFlag){%><a style="cursor:pointer;" onclick="forwardPage(2)">下一页</a><%}else{%>下一页<%}%>
		<%if(group.isFlag(28)){%> 
		<input type="checkbox" id="showTd" onclick="showTheAmount('<%=deliverValues%>','<%=list!=null?list.size():0%>')" <%=checkFlag?"checked='checked'":""%>/>
		<%}%>
		显示各快递公司的发单（退单）总金额 <br />
		<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
			<tr bgcolor='#F8F8F8'>
				<td align="center" width="10%">发货日期</td>
				<td align="center" width="3%">总发单</td>
				<%if(group.isFlag(28)){%>
				<td align="center" width="5%">总金额</td>
				<%}%>
				<td align="center" width="3%">退单</td>
				<%if(group.isFlag(28)){%>		
				<td align="center" width="5%">金额</td>
				<%}%>
				<%  
					it =deliver.deliverMapAll.entrySet().iterator();
					while(it.hasNext()){
					Map.Entry entry = (Map.Entry)it.next();
				%>
				<td align="center" width="3%"><%=entry.getValue()%>发单</td>
				<td align="center" width="4%">占比</td>
					<%if(group.isFlag(28)){%>
				<td align="center" id="showAmount<%=entry.getKey()%>a" width="5%">金额</td>
					<%}%>
				<td align="center" width="3%">退单</td>
				<td align="center" width="4%">占比</td>
					<%if(group.isFlag(28)){%>
				<td align="center" id="showAmount<%=entry.getKey()%>b" width="5%">金额</td>
					<%}%>
				<%} %>
			</tr>
	<%
		if(list!=null&& list.size()>0){
			for(int i=0;i<list.size();i++){
				OrderDeliversBean bean =(OrderDeliversBean)list.get(i);	
				int sumNum=0,blackNum=0;
				double sumMoney=0,blackMoney=0;
				
				for(int j =0;j<bean.getSubBeanList().size();j++){
					OrderDeliversBean deliverBean = (OrderDeliversBean)bean.getSubBeanList().get(j);
					sumNum += deliverBean.getSumNum();
					blackNum+=deliverBean.getBackNum();
					sumMoney += deliverBean.getSumMoney();
					blackMoney+=deliverBean.getBackMoney();
				}
				
				%>
				<tr bgcolor='#F8F8F8'>
					<td align="center" ><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=1" target="_blank"><%=bean.getDateTime()%></a></td>
					<td align="center"><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=1" target="_blank" ><%=sumNum%></a></td>
					<%if(group.isFlag(28)){%>
					<td align="center"><%=NumberUtil.priceOrder1(sumMoney)%></td>
					<%}%>
					<td align="center"><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=1&orderStatus=11" target="_blank"><%=blackNum%></a></td>
					<%if(group.isFlag(28)){%>
					<td align="center"><%=NumberUtil.priceOrder1(blackMoney)%></td>
					<%}%>
					<%  
					for(int x=0;x<deliverList.size();x++){
						String deliverStr = String.valueOf(deliverList.get(x));
						for(int j =0;j<bean.getSubBeanList().size();j++){
							OrderDeliversBean deliverBean = (OrderDeliversBean)bean.getSubBeanList().get(j);
							if(deliverBean.getDeliver()==Integer.parseInt(deliverStr)){
								%>
					<td align="center" ><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=1&deliver=<%=deliverBean.getDeliver()%>" target="_blank"><%=deliverBean.getSumNum()%></a></td>
					<td align="center" ><%=sumNum!=0?NumberUtil.priceOrder1(NumberUtil.percentDiv(deliverBean.getSumNum(),sumNum)*100):0%>%</td>
					<%if(group.isFlag(28)){%>
					<td align="center" id="showAmount<%=i%><%=deliverBean.getDeliver()%>aa"><%=NumberUtil.priceOrder1(deliverBean.getSumMoney())%></td>
					<%}%>
					<td align ="center" ><a href="searchorder.do?stockDate=<%=bean.getDateTime()%>&stockArea=1&deliver=<%=deliverBean.getDeliver()%>&orderStatus=11" target="_blank"><%=deliverBean.getBackNum()%></a></td>
					<td align="center" ><%=blackNum!=0?NumberUtil.priceOrder1((NumberUtil.percentDiv(deliverBean.getBackNum(),blackNum))*100):0%>%</td>
					<%if(group.isFlag(28)){%>			
					<td align="center" id="showAmount<%=i%><%=deliverBean.getDeliver()%>bb"><%=NumberUtil.priceOrder1(deliverBean.getBackMoney()) %></td>
					<%}%>			
								<%
							}
						} 
					}
					%>
				</tr>
				<%
				
			}
		}
	%>
		</table> 
		<script type="text/javascript">
			<%if(!checkFlag){%>
				showTheAmount('<%=deliverValues%>','<%=list!=null?list.size():0%>');
			<%}%>
		</script>
		<br/><br/>
		<div style="margin-left:24px">
			注：<br/>
			&nbsp;&nbsp;1、(各快递公司发单)占比=(各快递公司发单量/总发单量)×100<br/> 
			&nbsp;&nbsp;2、(各快递公司退单)占比=(各快递公司退单量/总退单量)×100
		</div>
	</body>
</html>
