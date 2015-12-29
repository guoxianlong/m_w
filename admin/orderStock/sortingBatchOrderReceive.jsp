<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%
//物流员工作业效率排名
String ranking =(String) request.getSession().getAttribute("ranking");
String photoUrl =(String) request.getSession().getAttribute("photoUrl");
String firstCount =(String) request.getSession().getAttribute("firstCount");
String oneselfCount =(String) request.getSession().getAttribute("oneselfCount");

int assignedCount1 = StringUtil.StringToId((String) request.getAttribute("assignedCount1"));
int assignedCount2 = StringUtil.StringToId((String) request.getAttribute("assignedCount2"));
int notAssignedCount1 = StringUtil.StringToId((String) request.getAttribute("notAssignedCount1"));
int notAssignedCount2 = StringUtil.StringToId((String) request.getAttribute("notAssignedCount2"));
CargoStaffBean staffBean =(CargoStaffBean) request.getAttribute("staffBean");
List areaList = (List) request.getAttribute("areaList");
String tip =(String) request.getAttribute("tip");
String success =(String) request.getAttribute("success");
SortingBatchGroupBean groupBean =(SortingBatchGroupBean) request.getAttribute("groupBean"); 
String staffCode =StringUtil.convertNull(request.getParameter("staffCode"));
String ems=request.getParameter("ems");
int id =0;
if(groupBean!=null){
	id=groupBean.getId();
}
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<head>
<title>分拣波次订单领取页</title>
</head>
<script type="text/javascript">
	function inputCode(sortingBatchGroupId,printType,selectedOrder){
		  var code = window.prompt("员工号:","");
		  if(code==""){
		 	 inputCode(sortingBatchGroupId,printType,selectedOrder);
		  }else if(code){
		  	openPrintPage(code,sortingBatchGroupId,printType,selectedOrder);
		  }
	}
	function openPrintPage(userCode,sortingBatchGroupId,printType,selectedOrder){
		 if( window.confirm("是否继续操作")){
			 window.location.href = 'sortingAction.do?method=sortingBatchGroupPrintLine&userCode='+userCode+'&sortingBatchGroupId='+sortingBatchGroupId+'&printType='+printType+'&selectedOrder='+selectedOrder+'&pageFrom=sortingBatchOrderReceive&success=1&staffCode=<%=staffCode%>&sortingBatchGroupId=<%=id%>';
			 return true;	 
		 }else{
			 return false;
		 }
	}
	function load(){
		
			document.getElementsByName("staffCode")[0].focus(); 
			selectOption(document.getElementById("ems") ,"<%=ems%>");

	}
</script>
<body onload="load()">
<div align="left"><font size="7" color="red">手工分拣波次领取</font></div>
<hr>
<table border="0" >
	<tr>
		<td><%if(ranking!=null){ %>
		&nbsp;&nbsp;&nbsp;<font color="red" size="4">你目前已领取<%=oneselfCount %>个,<%=ranking %>,冠军<%if(photoUrl != null){ %><img alt="" width="83" height="100" src="<%=Constants.STAFF_PHOTO_URL %>/<%=photoUrl%>"/><%}%><%=firstCount %>个,继续加油哦~</font><br><br>
		<%} %>	&nbsp;&nbsp;&nbsp;<font size='3'>今日已分配分拣波次数：</font><font color='blue' size='3'><%=assignedCount1 %></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br><br>
		    &nbsp;&nbsp;&nbsp;<font size='3'>待分配的分拣波次数：</font><font color='blue' size='3'><%=notAssignedCount1 %></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br><br>
		 <%if(group.isFlag(644)){ %> <a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingStatisticsList'><font color='blue' size='3'>【分拣统计量】</font></a><%} %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		 <%if(group.isFlag(643)){ %> <a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchGroupPrintList'><font color='red' size='3'>【分拣波次补打】</font></a><%} %>
		</td>
	</tr>
</table>
<form method="post" action="<%=request.getContextPath()+"/admin/sortingAction.do?method=sortingBatchOrderReceive" %>">
	<table>
		<tr>
			<td height="10"><font size='3'><!-- <select name="ems" id="ems" ><option value="0">EMS-单SKU</option><option value="1">非EMS-单SKU</option></select> -->员工号：</font><input type='text' name='staffCode' height="10"/>&nbsp;<input type='submit' name='' value='领单'/>
			<select name="areaId" id="areaId">
          	<%if(areaList!=null&&areaList.size()>0){%>
          		<%for(int j=0;j<areaList.size();j++){%>
					<option value="<%=areaList.get(j)%>"><%=ProductStockBean.areaMap.get
                   (Integer.valueOf(areaList.get(j).toString()))%></option>
				<%}} %>
		  </select>
			</td>
		</tr>
	</table>
</form>
<%if(("1").equals(success)) {%>
&nbsp;&nbsp;&nbsp;<%=staffBean.getName() %>(<%=staffCode%>),波次号:<%=groupBean.getCode()%>领取成功!&nbsp;&nbsp;&nbsp;<input onClick="openPrintPage('',<%=groupBean.getId()%>,'buda','')" style="color:blue; font-size:14px" type="button" value="补打">
<%}else if(("-1").equals(success)){%><%if(staffBean==null){%>&nbsp;&nbsp;&nbsp;<font color='red'>员工号为<%=staffCode%>的员工不存在！</font><%}else{ %><font color='red'><%=staffBean.getName()%>(<%=staffBean.getCode()%>),领取失败，请重新刷卡领单！</font><%} %>
<%}%>
</body>
</html>