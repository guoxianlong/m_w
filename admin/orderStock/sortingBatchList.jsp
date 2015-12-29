<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.order.OrderStockBean"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoAreaBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>

<%
	List list = (List) request.getAttribute("list");
	String lastTime = (String)request.getAttribute("lastTime");
	String nextTime = (String)request.getAttribute("nextTime");
	String noCompleteBatchCount = (String)request.getAttribute("noCompleteBatchCount");
	String historyNoCompleteCount = (String)request.getAttribute("historyNoCompleteCount");
	String completeBatchOrderCount = (String)request.getAttribute("completeBatchOrderCount"); 
	int todaySfOrderCount = StringUtil.toInt(request.getAttribute("todaySfOrderCount")+"");
	int sfMaxfhCount = StringUtil.toInt(request.getAttribute("sfMaxfhCount")+"");
	List areaList = (List) request.getAttribute("areaList");
	String storage = (String)request.getAttribute("storage");
	List<String> returnAreaList = (List)request.getAttribute("returnAreaList");
	String storage1=request.getParameter("storage");
	String status=request.getParameter("status");
	String select=request.getParameter("select");
	String text=request.getParameter("text");
	String startDay=request.getParameter("startDay");
	String startHour=request.getParameter("startHour");
	String startM=request.getParameter("startM");
	String endHour=request.getParameter("endHour");
	String endM=request.getParameter("endM");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	int hour =24;
	int m =5;
	String tip=(String)request.getAttribute("tip");
	int ordercount = StringUtil.parstInt(request.getAttribute("ordercount")+"");
	if(tip!=null && tip.length()>0){
 		%><script>alert('<%= tip %>');</script><%
 	}
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	int st = StringUtil.toInt(storage);
	String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("storage","storage", request,st, true, "-1");
%>
<html>
<head>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>

<title>分拣批次管理</title>
<style type="text/css">
td,th {
	color: #FFF;
}
.STYLE1 {color: #FF0000}
</style>
<script type="text/javascript">
	function showSearchCondition(){
		if($('#select').val()=='0'){
			$('#showSearchDiv1').hide();	
			$('#showSearchDiv').show();
		}else{
			$('#showSearchDiv').hide();	
			$('#showSearchDiv1').show();
		}
		
		
	}
	//打印选定提交
	function printSelected(){
		var arrID = document.getElementsByName("id");
		if(arrID.length==0){alert("还没有单子，请先查找！");return ;}
		var lengthNum = arrID.length;
		for(var i=0;i<lengthNum;i+=1){
			if(arrID[i].checked){
				break;
			}else if(i==lengthNum-1){
				alert("请至少选择一个订单！");
				return ;
			}
		}
		document.getElementById('sum_action').value='print'; 
		document.getElementById('linePrintId2').value='lineprint';
		document.forms[2].submit();
	}
	
	function submitForm(sortingType,dq){
		var deliverAllId = "deliver" + sortingType;
		if (dq=='true') {
			deliverAllId += "dq";
		} 
		var deliver = "";
		if ($("#" + deliverAllId).checked) {
			deliver = "0";
		} else {
			deliver = getInputValueByName($("input[name='" + deliverAllId +"']"));
			if (deliver == "") {
				alert("请选择快递公司！");
				return false;
			}
			deliver = deliver.substring(0,deliver.length-1);
		}
		document.getElementById("form1").action="<%=request.getContextPath()%>/admin/sortingAction.do?method=createSortingBatch&sortingType=" + sortingType + "&dq=" + dq + "&deliver=" + deliver;
		document.form1.submit();
	}
	
	function getInputValueByName(items){
		var info = '';
		for (var i = 0; i < items.length; i++) {
	     // 如果i+1等于选项长度则取值后添加空字符串，否则为逗号
	     if (items.get(i).checked)
	     	info = info + items.get(i).value +",";
		}
		return info;
	}
	
	function openMenu(event, id) { 
		var el, x, y; 
		el = document.getElementById(id); 
		if (window.event) { 
			x = window.event.clientX + document.documentElement.scrollLeft 
				+ document.body.scrollLeft; 
			y = window.event.clientY + document.documentElement.scrollTop + 
				+ document.body.scrollTop; 
		} 
		else { 
			x = event.clientX + window.scrollX; 
			y = event.clientY + window.scrollY; 
		} 
		x -= 2; y -= 2; 
		el.style.left = x + "px"; 
		el.style.top = y + "px"; 
		el.style.visibility = "visible"; 
	} 
	function closeMenu(event,el) { 
		var current, related; 
		if (window.event) { 
			current = el; 
			related = window.event.toElement; 
		} 
		else { 
			current = event.currentTarget; 
			related = event.relatedTarget; 
		} 
		if (current != related && !contains(current, related)) 
			current.style.visibility = "hidden"; 
	} 
		function contains(a, b) { 
		// Return true if node a contains node b. 
			while (b.parentNode) 
				if ((b = b.parentNode) == a) 
					return true; 
			return false; 
		} 
		
		function selectAll(name,own) {
			if (own.checked) {
				$("input[name='" +name+"']").attr("checked",true);
			} else {
				$("input[name='" +name+"']").attr("checked",false);
			}
		}
		
		function unselectAll(id,own) {
			if (!own.checked) {
				$("#" + id).attr("checked",false);
			} 
		}
</script>
<STYLE type=text/css> 
.menu { BORDER-RIGHT: #ffffff 2px solid; BORDER-TOP: #ffffff 2px solid; LEFT: 0px; VISIBILITY: hidden; BORDER-LEFT: #ffffff 2px solid; BORDER-BOTTOM: #ffffff 2px solid; POSITION: absolute; TOP: 0px; BACKGROUND-COLOR: #ffffff ;white-space:nowrap;} 
</STYLE> 
</head>
<body bgcolor="#ffcc00">
<form id="form1" name="form1" method="post">
<table  width="99%" border="0" cellspacing="0">
  <tr>
    <td style="color: #000">上次生成时间:<%if(lastTime==null){%>&nbsp;<%}else{%><%=StringUtil.cutString(lastTime,19) %><%} %></td>
    <td style="color: #000"><strong>下次自动生成时间:<%=nextTime %></strong></td>
    <td style="color: #000">自动生成间隔:30分钟</td>
    <td style="color: #000"><strong>
  <%if(group.isFlag(596)){ %> 
		<%if (returnAreaList != null) {%>
			<%String t=""; %>
			<%for(String area : returnAreaList) {%> 
      			<% t += "<input type=\"button\" name=\"button" + area + "\" id=\"button" + area + "\" onclick=\"openMenu(event, 'areaDeliver" +area+"');return false;\"  value=\"";
      				t += ProductStockBean.stockoutAvailableAreaMap.get(StringUtil.toInt(area));
      				t += "手动生成分拣批次\" /></br>"; 
      				t += "<div id=\"areaDeliver" + area + "\" class='menu' onmouseout='closeMenu(event,this)'> ";
      				t += "<input type='checkbox' value='0' id='deliver" + area + "'  onclick=\"selectAll('deliver" + area + "',this);\"/>全选&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick=\"submitForm('" + area + "','false');\" value='确定'/>";
      				List<HashMap<String, String>> arealist = OrderStockBean.areaDeliverMap.get(Integer.valueOf(area));
      				for (HashMap<String, String> deliverMap : arealist) {
      					t += "</br><input type='checkbox' value='" + deliverMap.get("deliverId") + "' name='deliver" + area + "' onclick=\"unselectAll('deliver" + area + "',this);\"/>" + deliverMap.get("deliverName") + "";
      				}
      				t += "</div>";%>
       		<%} %>
    		<%= t%>
       <%} %>
   <%} %>
   
    </strong></td>
    <td style="color: #000"><strong>
  <%if(group.isFlag(596)){ %> 
		<%if (returnAreaList != null) {%>
			<%String t=""; %>
			<%for(String area : returnAreaList) {%> 
      			<% t += "<input type=\"button\" name=\"button" + area + "\" id=\"button" + area + "\" onclick=\"openMenu(event, 'areaDeliver" +area+"dq');return false;\"  value=\"";
      				t += ProductStockBean.stockoutAvailableAreaMap.get(StringUtil.toInt(area));
      				t += "手动生成分拣批次DQ\" /></br>";
      				t += "<div id=\"areaDeliver" + area + "dq\" class='menu' onmouseout='closeMenu(event,this)'> ";
      				t += "<input type='checkbox' value='0' id='deliver" + area + "dq' class='menuItem' onclick=\"selectAll('deliver" + area + "dq',this);\"/>全选&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' onclick=\"submitForm('" + area + "','true');\" value='确定'/>";
      				List<HashMap<String, String>> arealist = OrderStockBean.areaDeliverMap.get(Integer.valueOf(area));
      				for (HashMap<String, String> deliverMap : arealist) {
      					t += "</br><input type='checkbox' value='" + deliverMap.get("deliverId") + "' class='menuItem' name='deliver" + area + "dq'  onclick=\"unselectAll('deliver" + area + "dq',this);\"/>" + deliverMap.get("deliverName") + "";
      				}
      				t += "</div>";%>
       		<%} %>
      		<%= t%>
       <%} %>
   <%} %>
    </strong></td>
  </tr>
  <tr>
    <td width="25%"><a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchList&flag=0' ><font color='red'>今日未完成的分拣批次(<%=noCompleteBatchCount%>)</font></a> </td>
    <td width="25%"><a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchList&flag=1'  class="STYLE1"><font color='red'>历史未完成的分拣批次(<%=historyNoCompleteCount%>)</font></a></td>
    <td width="25%"><a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchList&flag=2' ><font color='blue'>今日已完成的分拣批次(<%=completeBatchOrderCount%>)</font></a></td>
    <td width="24%"><font color='red'>至今未处理的订单(<%=ordercount %>)</font></td>
    <td width="24%"></td>
  </tr>
</table>
</form>
<hr/>
<form id="form2" name="form2" method="post" action="<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchList">
  <table width="60%" border="0">
    <tr>
      <td width="30%"><div align="left">
        <%= wareAreaLable%>
			
      </div></td>
      <td width="25%"><div align="left">
        <select name="status" id="status">
         <option value="">全部状态</option>
         <option value="0">未处理</option>
         <option value="1">处理中</option>
         <option value="2">未分拣</option>
         <option value="3">分拣中</option>
         <option value="4">已完成</option>
        </select>
      </div></td>
      <td width="30%"><div align="left">
        <select name="select" id="select" onChange="showSearchCondition()">
          <option value="0">订单号/批次号/波次号</option>
          <option value="1">分拣批次生成时间</option>
        </select>
      </div></td>
      <td width="10%">
      	<div align="left" id="showSearchDiv">
      	  <input type="text" name="text" id="text" <%if(text!=null&&text.length()>0) {%>value="<%=text %>"<%}else{%>value="订单号/批次号/波次号"/<%} %> onfocus="if(this.value=='订单号/批次号/波次号'){this.value=''}">
      	</div>
      	<div align="left" id="showSearchDiv1" style="display: none">
      	  <input type=text name="startDay" id="startDay" size="10"
				    value="" onClick="SelectDate(this,'yyyy-MM-dd');"/>
		<select name="startHour" id="startHour">
		<%for(int i=0;i<hour ;i++){ %>
			<option value="<%=i%>"><%=i %></option>
		<%} %>
        </select>
        <select name="startM" id="startM">
        <%for(int i=1;i<=m ;i++){ %>
        	<option value="<%=i*10%>"><%=i*10%>分</option>
        <%} %>
        </select>至
        <select name="endHour" id="endHour">
        <%for(int i=0;i<hour ;i++){ %>
			<option value="<%=i%>"><%=i %></option>
		<%} %>
        </select>
        <select name="endM" id="endM">
        <%for(int i=1;i<=m ;i++){ %>
        	<option value="<%=i*10%>"><%=i*10%>分</option>
        <%} %>
        </select>
      	</div>
      </td>
      <td width="14%"><div align="left">
        <input type="submit" name="button2" id="button2" value="查询" />
      </div></td>
    </tr>
  </table>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"></div></td>
    <td><div align="center"><strong><font color="#00000">分拣批次号</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">作业仓</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">生成日期</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">生成时间</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">分拣波次数</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">订单总数</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">SKU数</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">完成时间</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">状态</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">操作</font></strong></div></td>
  </tr>
<%if(list!=null){
for(int i=0;i<list.size();i++){
   SortingBatchBean sbBean = (SortingBatchBean)list.get(i);
%>
  <tr <%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
    <td style="color: #000"><div align="center" ><b><font style="font-size:15"><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1%></font></b></div></td>
    <td style="color: #000"><div align="center">
      <%if((sbBean.getStatus()==SortingBatchBean.STATUS
    		  ||sbBean.getStatus()==SortingBatchBean.STATUS0
    		  ||sbBean.getStatus()==SortingBatchBean.STATUS1)){ %> 
     <b><font style="font-size:15"><%=sbBean.getCode()%></font></b>
       <%}else { %>
      <b><font style="font-size:15"><a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchGroupDetail&batchId=<%=sbBean.getId()%>'  ><%=sbBean.getCode()%></a></font></b>
      <%} %>
    </div></td>
    <td style="color: #000"><div align="center"><b><font style="font-size:15"><%=sbBean.getStorageName()%></font></b></div></td>
    <td style="color: #000"><div align="center"><b><font style="font-size:15"><%=StringUtil.cutString(sbBean.getCreateDatetime(),10)%></font></b></div></td>
    <td style="color: #000"><div align="center"><b><font style="font-size:15"><%=StringUtil.cutString(sbBean.getCreateDatetime(),10,19)%></font></b></div></td>
    <td style="color: #000">
      <div align="center">
        <%if(sbBean.getSortingBatchGroupCount()==0){%>
        <span class="STYLE1"><b><font style="font-size:15">--</font></b></span>
        <%}else{%>
        <b><font style="font-size:15"><%=sbBean.getSortingBatchGroupCount()%></font></b>
        <%} %>
        </div></td>
    <td style="color: #000"><%if(sbBean.getStatus()!=4&&sbBean.getStatus()!=3) {%><div align="center"><a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchOrderList&batchId=<%=sbBean.getId()%>&flag=0' ><b><font style="font-size:15"><%=sbBean.getOrderCount()%></font></b></a></div><%}else{ %><div align="center"><b><font style="font-size:15"><%=sbBean.getOrderCount()%></font></b></div> <%} %></td>
    <td style="color: #000"><div align="center"><b><font style="font-size:15"><%=sbBean.getSkuCount()%></font></b></div></td>
    <td style="color: #000"><div align="center">
      <%if(sbBean.getCompleteDatetime()==null){%>
      &nbsp;
      <%}else{%>
      <b><%=StringUtil.cutString(sbBean.getCompleteDatetime(),19)%></b>
      <%} %>
    </div></td>
    <td style="color: #000"><div align="center"><b><font style="font-size:15"><%=sbBean.getStatusName(sbBean.getStatus())%></font></b></div></td>
    <td style="color: #000"><div align="center">
    <%if(sbBean.getStatus()!=SortingBatchBean.STATUS5) {%>
     <%if(sbBean.getOrderCount()!=0){ %>
     		<%if(group.isFlag(594)){ %>
      				<a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingOrderStockPrint&deliver=all&batchId=<%=sbBean.getId()%>'><font color='blue'>导出全部订单</font></a>|
      				<%--<%if(sbBean.getSfCount()>0) {
      	 					<a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingOrderStockPrint&batchId=<%=sbBean.getId()%>&deliver=1' <%if(todaySfOrderCount>sfMaxfhCount){ %>onClick="return confirm('当天已分配给顺丰<%=todaySfOrderCount%>单，超出<%=sfMaxfhCount%>单的限制，是否继续导出？');"<%} %>><font color='green'>导顺丰</font> </a> |
      				<%} %>--%> 
      				<%if(sbBean.getStatus()!=4&&sbBean.getStatus()!=3) {%>
      						<a href='<%=request.getContextPath()%>/admin/sortingAction.do?method=sortingBatchOrderList&batchId=<%=sbBean.getId()%>&flag=0' >分类/物流</a>|
      				<%} %>
     <%}} %>
      <!--<% if(group.isFlag(592)  && (sbBean.getStatus()==1||sbBean.getStatus()==2||sbBean.getStatus()==3)){ %>	
           <%if(sbBean.getEmsSnOrderCount()+sbBean.getEmsSwOrderCount()>0){ %>
               <%if(sbBean.getEmsSnDdCount()+sbBean.getEmsSwDdCount()==0){%>
                   <a href='sortingAction.do?method=sortingBatchListPrintLine&sortingBatchId=<%=sbBean.getId()%>&deliver=ems&printType=dadan' ><font color='red'>打EMS</font></a>|
               <%}else{%>
                   <a href='sortingAction.do?method=sortingBatchListPrintLine&sortingBatchId=<%=sbBean.getId()%>&deliver=ems&printType=buda' onClick="return confirm('是否补打？');"><font color='blue'>补打EMS</font></a>|
               <%}%>
           <%}%>
           <%if(sbBean.getSfOrderCount()>0){ %>
               <%if(sbBean.getSfDdCount()==0){%>
                   <a href='sortingAction.do?method=sortingBatchListPrintLine&sortingBatchId=<%=sbBean.getId()%>&deliver=noems&printType=dadan'><font color='red'>打非EMS</font></a> 
               <%}else{%>
                   <a href='sortingAction.do?method=sortingBatchListPrintLine&sortingBatchId=<%=sbBean.getId()%>&deliver=noems&printType=buda' onClick="return confirm('是否补打？');"><font color='blue'>补打非EMS</font></a> 
               <%} %>
           <%}%>
      <%}%>--><%} %>
    </div>
    </td>
  </tr>
<%}}%>
</table>
</form>
<script>
function load(){
	 selectOption(document.getElementById("storage") ,"<%=storage1%>");
	 selectOption(document.getElementById("status") ,"<%=status%>");
	 selectOption(document.getElementById("select") ,"<%=select%>");
	 selectOption(document.getElementById("text") ,"<%=text%>");
	 selectOption(document.getElementById("startDay") ,"<%=startDay%>");
	 selectOption(document.getElementById("startHour") ,"<%=startHour%>");
	 selectOption(document.getElementById("startM") ,"<%=startM%>");
	 selectOption(document.getElementById("endHour") ,"<%=endHour%>");
	 selectOption(document.getElementById("endM") ,"<%=endM%>");
}
load();
</script>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
</html>