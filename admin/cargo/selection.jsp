<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="adultadmin.bean.cargo.*"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="mmb.stock.cargo.CargoOperationTodoBean,adultadmin.action.vo.voProduct"%>
<% if(request.getParameter("selectIndex") != null && request.getParameter("selectIndex").equals("0")) {%>
	<% List deptList1 = (List) request.getAttribute("deptList1"); %>
		<select id="deptCode1" name="deptCode1" onchange="selectdept1();">
			<option value="">选择一级部门</option>
			<%if(deptList1!=null){ %>
			<%for(int i=0;i<deptList1.size();i++){ %>
				<%CargoDeptBean dept=(CargoDeptBean)deptList1.get(i); %>
				<option value="<%=dept.getCode()%>"><%=dept.getName() %></option>
			<%} %>
		<%} %>
	</select>
<% } else if(request.getParameter("selectIndex")!=null&&request.getParameter("selectIndex").equals("1")){%>
	<%List deptList2=(List)request.getAttribute("deptList2"); %>
	

<select id="deptCode2" name="deptCode2" onchange="selectdept2();">
		<option value="">选择二级部门</option>
		<%if(deptList2!=null){ %>
			<%for(int i=0;i<deptList2.size();i++){ %>
				<%CargoDeptBean dept=(CargoDeptBean)deptList2.get(i); %>
				<option value="<%=dept.getCode()%>"><%=dept.getName() %></option>
			<%} %>
		<%} %>
	</select>
<%}else if(request.getParameter("selectIndex")!=null&&request.getParameter("selectIndex").equals("2")){%>
	<%List deptList3=(List)request.getAttribute("deptList3"); %>
	<select id="deptCode3" name="deptCode3">
		<option value="">选择三级部门</option>
		<%if(deptList3!=null){ %>
			<%for(int i=0;i<deptList3.size();i++){ %>
				<%CargoDeptBean dept=(CargoDeptBean)deptList3.get(i); %>
				<option value="<%=dept.getCode()%>"><%=dept.getName() %></option>
			<%} %>
		<%} %>
	</select>
<%}else if(request.getParameter("selectIndex")!=null&&request.getParameter("selectIndex").equals("3")){%>
	<%List deptList=(List)request.getAttribute("deptList");%>
	<%String name=request.getAttribute("name").toString(); %>
	<%int dept2=StringUtil.StringToId(request.getParameter("dept2")); %>
	<select name="<%=name%>">
		<option value="">二级部门</option>
		<%for(int i=0;i<deptList.size();i++){ %>
			<%CargoDeptBean dept=(CargoDeptBean)deptList.get(i); %>
			<option value="<%=dept.getId()%>"<%if(dept2 == dept.getId()){ %> selected=selected<%} %>><%=dept.getName() %></option>
		<%} %>
	</select>
<%}else if(request.getParameter("selectIndex")!=null&&request.getParameter("selectIndex").equals("4")){%>
	<%List qualifiedStockDetailList=(List)request.getAttribute("qualifiedStockDetailList"); %>
	<%List cargoOperationCargoList=(List)request.getAttribute("cargoOperationCargoList"); %>
	<%String date=request.getParameter("date"); %>
	<%String num=request.getParameter("num"); %>
	<%String condition1=request.getParameter("condition1");//记录作业状态 %>
	<%String condition2=request.getParameter("condition2");//记录时效状态 %>
	<%PagingBean paging=(PagingBean)request.getAttribute("paging");%>
	<%int operType=StringUtil.toInt(request.getParameter("operType")); %>
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form<%=request.getParameter("num") %>" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post" onsubmit="return false;">
			<SELECT id="condition<%=operType %>1" name="condition1" onchange="selCondition(<%=operType %>);">
				<OPTION selected value="0" >全部作业状态</OPTION>
				<OPTION  value="1" <%if(condition1.equals("1")){ %>selected=selected<%} %>>生成阶段</OPTION>
				<OPTION  value="2" <%if(condition1.equals("2")){ %>selected=selected<%} %>>交接阶段</OPTION>
				<OPTION  value="3" <%if(condition1.equals("3")){ %>selected=selected<%} %>>作业完成</OPTION>
			</SELECT>
			<SELECT id="condition<%=operType %>2" name="condition2">
				<OPTION selected value="0">全部时效状态</OPTION>
			</SELECT>
			<input type="hidden" name="date" <%if(date!=null){ %>value="<%=date %>"<%} %>/>
			<input type="hidden" name="operType" value="<%=operType %>"/>
			<INPUT name="search" type="button"  value="筛选" onclick="shaixuan('<%=request.getParameter("num") %>',0);" >
			</form>
			</td>
		</tr>
		</table>
	<table cellpadding="3" border=1 style="border-collapse: collapse;" id="table<%=num %>"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">源仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">目的仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">提交时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态变更时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">时效状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">日志查询</td>
		</tr>
		<%	
		for(int i = 0; i < qualifiedStockDetailList.size(); i++){
			CargoOperationBean cob = (CargoOperationBean)qualifiedStockDetailList.get(i);	
			CargoOperationCargoBean cocb = (CargoOperationCargoBean)cargoOperationCargoList.get(i);
			int id = cob.getId();
			String code = cob.getCode();
			int type = cob.getType();
			String statusName = StringUtil.convertNull(cob.getStatusName());
			if("已出货".equals(statusName)){
				statusName = "<font color = \"red\">" + statusName + "</font>"	;
			}
			if("已入货".equals(statusName)){
				statusName = "<font color = \"blue\">" + statusName + "</font>"	;
			}
			if("待作业".equals(statusName)){
				statusName = "<font color = \"green\">" + statusName + "</font>";
			}
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%></td>
				<td align="center">
				<%
				switch(type){ 
					case 0:
				%>
					<a href="cargoOper.do?method=showEditCargoOperation&operationId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 1:
					%>
					<a href="cargoDownShelf.do?method=showDownShel&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 2:
					%>
					<a href="cargoOperation.do?method=refillCargo&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 3:
					%>
					<a href="cargoOperation.do?method=exchangeCargo&cargoOperId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
				<%
					break;
				} 
				%>
				</td>
				<td align="center"><%=cocb.getOutCargoWholeCode() == null ? "" : cocb.getOutCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cocb.getInCargoWholeCode() == null ? "" : cocb.getInCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cob.getConfirmDatetime() == null ? "" : cob.getConfirmDatetime().substring(0,16)%></td>
				<td align="center"><%=statusName%></td>
				<td align="center"><%=cob.getLastOperateDatetime() == null ? "" : cob.getLastOperateDatetime().substring(0,16)%></td>
				<td align="center"><%if(cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=cob.getEffectTimeName()%></font><%}else{ %><%=cob.getEffectTimeName()%><%} %></td>
				<td align="center">
				<a href="qualifiedStock.do?method=cargoOperLog&operCode=<%=cob.getCode()%>" target="_blank"><font color="#0000ff">作业日志</font></a>
				</td>
			</tr>
		<%} %>
	</table>
	<script type="text/javascript">
	<!--
	function toExcel(num){
		var textarea=document.getElementById("excel"+num);
		var texttable=document.getElementById("table"+num);
		textarea.value=texttable.innerHTML;
		alert(textarea.value);
	}
	//-->
</script>
	<form action="cargo/qualifiedStockDetailExcel.jsp" method="post" onsubmit="toExcel('<%=num %>');">
		<input type="text" id="excel<%=num %>" name="excel" style="display:none;"/>
		<input type="submit" value="导出本页列表到EXCEL"/>
	</form>
	<%if(paging!=null){ %>
		<center><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
<%}else if(request.getParameter("selectIndex")!=null&&request.getParameter("selectIndex").equals("5")){%>
	<%int num=0; 
	PagingBean paging=null;
	List cotList=null;
	if(request.getAttribute("refillList")!=null){ 
		num=0; 
		paging=(PagingBean)request.getAttribute("paging0");
		cotList=(List)request.getAttribute("refillList");
	}else if(request.getAttribute("upShelfList")!=null){
		num=1; 
		paging=(PagingBean)request.getAttribute("paging1");
		cotList=(List)request.getAttribute("upShelfList");
	}else if(request.getAttribute("downShelfList")!=null){
		num=2; 
		paging=(PagingBean)request.getAttribute("paging2");
		cotList=(List)request.getAttribute("downShelfList");
	}else if(request.getAttribute("exchangeList")!=null){
		num=3; 
		paging=(PagingBean)request.getAttribute("paging3");
		cotList=(List)request.getAttribute("exchangeList");
	}
	%>
	<table cellpadding="3" border=0 style="border-collapse: collapse;" bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
				<form id="form" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=cargoOperationTodo" method="post" onsubmit="return false;">
				<input type="text" name="code" value="输入关键字..." style="color:#cccccc;"
			onfocus="if(this.value=='输入关键字...'){this.value='';this.style.color='#000000';}" 
			onblur="if(this.value==''){this.value='输入关键字...';this.style.color='#cccccc';}"/>
				<SELECT id="status<%=num %>" name="status">
					<OPTION selected value="-1" >全部作业状态</OPTION>
					<OPTION  value="0">未分配</OPTION>
					<OPTION  value="1">已分配</OPTION>
				</SELECT>
				<input type="hidden" id="tempStatus<%=num %>" name="tempStatus" value="<%=request.getParameter("status")==null?"-1":request.getParameter("status")%>"/>
				<INPUT name="search" type="button"  value="查询" onclick="shaixuan('<%=num %>',0);"/>
				<input type="button" value="手动更新列表" onclick="window.location.reload();"/>
				</form>
			</td>
		</tr>
	</table> 
	<form id="form<%=num %>" action="qualifiedStock.do?method=submitCargoOperationTodo" method="post" target="_blank">
		<table id="table<%=num %>" cellpadding="3" border=1 style="border-collapse: collapse;" bordercolor="#D8D8D5" align="center" width="95%">
			<tr bgcolor="#00ccff" >
				<td align="center" style="font-weight:bold;color:#000000;">全部</td>
				<td align="center" style="font-weight:bold;color:#000000;"><%if(num==1){ %>装箱单号<%}else if(num==0||num==2||num==3){ %>商品编号<%} %></td>
				<td align="center" style="font-weight:bold;color:#000000;">状态</td>
				<td align="center" style="font-weight:bold;color:#000000;">源货位</td>
				<td align="center" style="font-weight:bold;color:#000000;"><%if(num==0){ %>可用装箱单<%}else if(num==1){ %>装箱数量<%}else if(num==2||num==3){ %>商品数量<%} %></td>
				<td align="center" style="font-weight:bold;color:#000000;">领取人</td>
			</tr>
			<%	
			for(int i = 0; i < cotList.size(); i++){
				CargoOperationTodoBean cot = (CargoOperationTodoBean)cotList.get(i);	
			%>		
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%><input type="checkbox" name="todo0" value="<%=cot.getStaffId()==0?"":"*" %><%=cot.getCargoProductStockId() %>"/></td>
				<td align="center"><%=cot.getProductCode() %></td>
				<td align="center"><%=CargoOperationTodoBean.getStatusName(cot.getStatus()) %></td>
				<td align="center"><%=cot.getCargoCode()%></td>
				<td align="center"><%=cot.getCount()%></td>
				<td align="center"><%=cot.getStaffName()==null?"":cot.getStaffName()%></td>
			</tr>
			<%} %>
		</table>
		<input type="hidden" id="staffCode<%=num %>" name="staffCode" />
		<input type="button" value="分配并打印当页已选作业清单" onclick="checkSubmitTodo2(<%=num %>);"/>
		<input type="hidden" name="type" value="<%=num %>"/>
	</form>
	<%if(paging!=null){ %>
		<center><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
<%}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("6")){
	ArrayList sibList = (ArrayList)request.getAttribute("sibList");
	if(sibList.size()>0){
		%>
			<OPTION  value="">全部</OPTION>
		<%
		 for(int i=0;i<sibList.size();i++){
			CargoStaffBean csb = (CargoStaffBean)sibList.get(i);
			%>
				<OPTION  value="<%=csb.getCode()%>"><%=csb.getName()%></OPTION>
		<% }
	}else{ %>
				<OPTION selected value="" >未找到</OPTION>
		<% } %>			
<% }else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("7")){
	ArrayList sibList = (ArrayList)request.getAttribute("sibList");
	String selectedName = (String)request.getAttribute("staffName");
	if(sibList.size()>0){
		%>
			<OPTION  value="">全部</OPTION>
		<%
		 	for(int i=0;i<sibList.size();i++){
			CargoStaffBean csb = (CargoStaffBean)sibList.get(i);
				if(selectedName.equals(csb.getName())){
				%>
					<OPTION selected value="<%=csb.getCode()%>"><%=csb.getName()%></OPTION>
			  <%}else{%>
					<OPTION   value="<%=csb.getCode()%>"><%=csb.getName()%></OPTION>
			  <%}
			 }
	}else{ %>
				<OPTION selected value="" >未找到</OPTION>
		<% } %>			
<% }else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("8")){
	ArrayList list = (ArrayList)request.getAttribute("list");
		if(list.size()>0){
		 	for(int i=0;i<list.size();i++){
			ReturnsReasonBean bean = (ReturnsReasonBean) list.get(i);%>
				<OPTION selected value="<%=bean.getId()%>"><%=bean.getReason()%></OPTION>
		 <%}
		}
		%>
				<option value="" selected >请选择原因</option>
			<%
	}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("9")){ 
		//物流员工考核
		String ranking =(String) request.getAttribute("ranking");
		String firstCount =(String) request.getAttribute("firstCount");
		String oneselfCount =(String) request.getAttribute("oneselfCount");
		String productCount =(String) request.getAttribute("productCount");
		String firstProductCount =(String) request.getAttribute("firstProductCount");
	%>
		<font size="2" color="red">你已装<%=oneselfCount %>箱,商品<%=productCount %>件,<%=ranking %>,冠军<%=firstCount %>箱,<%=firstProductCount %>件~</font>
	<%}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("10")){ 
		//物流员工考核
		String ranking =(String) request.getAttribute("ranking");
		String firstCount =(String) request.getAttribute("firstCount");
		String oneselfCount =(String) request.getAttribute("oneselfCount");
		String productCount =(String) request.getAttribute("productCount");
		String firstProductCount =(String) request.getAttribute("firstProductCount");
	%>
		<font size="2" color="red">你已上架<%=oneselfCount %>箱,商品<%=productCount %>件,<%=ranking %>,冠军<%=firstCount %>箱,<%=firstProductCount %>件</font>
	<%}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("11")){ 
		//物流员工考核
		String ranking =(String) request.getAttribute("ranking");
		String firstCount =(String) request.getAttribute("firstCount");
		String oneselfCount =(String) request.getAttribute("oneselfCount");
		String productCount =(String) request.getAttribute("productCount");
		String photoUrl = (String) request.getAttribute("photoUrl");
	%>
		<font size="4" color="red">你已复核<%=oneselfCount %>个,商品<%=productCount %>件,<%=ranking %>,冠军<%if(photoUrl != null){ %><img alt="" width="83" height="100" src="<%=photoUrl%>"/><%}%><%=firstCount %>个,继续加油~</font>
	<%}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("12")){ 
		//物流员工考核
		String ranking =(String) request.getAttribute("ranking");
		String firstCount =(String) request.getAttribute("firstCount");
		String oneselfCount =(String) request.getAttribute("oneselfCount");
		String productCount =(String) request.getAttribute("productCount");
		String firstProductCount =(String) request.getAttribute("firstProductCount");
	%>
		<font size="2" color="red">作业完成<%=oneselfCount %>箱,商品<%=productCount %>件,<%=ranking %>,冠军<%=firstCount %>箱,<%=firstProductCount %>件</font>
	<%}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("13")){ 
		//物流员工考核
		String ranking =(String) request.getAttribute("ranking");
		String firstCount =(String) request.getAttribute("firstCount");
		String oneselfCount =(String) request.getAttribute("oneselfCount");
		String productCount =(String) request.getAttribute("productCount");
		String firstProductCount =(String) request.getAttribute("firstProductCount");
	%>
		<font size="3" color="red">你目前已打汇总单<%=oneselfCount %>张,商品<%=productCount %>件,<%=ranking %>,冠军<%=firstCount %>张,<%=firstProductCount %>件</font>
	<%}else if(request.getParameter("selectIndex")!=null&& request.getParameter("selectIndex").equals("14")){ 
		//异常入库单
		List<voProduct> rpList =(ArrayList<voProduct>) request.getSession().getAttribute("rpList");
		String result = (String)request.getAttribute("result");
	%>
		<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
		<script type="text/javascript">
			<%if(result != null){%>
				alert("<%=result %>");
			<%}%>
			$(document).ready(function(){
			//删除实际退回商品
			$("input[name='del']").click(function(){
				var realCode = this.id;
				$.ajax({
					type: "GET",
					url: "<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=delRealProduct&selectIndex=14&realCode=" + realCode,
					cache: false,
					data: {type: "1"},
					success: function(msg, reqStatus){
						$("#real_td").empty();
						$("#real_td").html(msg);
						$('#realCode').val("");
						$('#realCount').val("");
						$('#realCode').focus();
					}
				});
			});
			$("input[name='realName']").change( function() {
				var realCount = $(this).val();
				var realCode = this.id;
				$.ajax({
					type: "GET",
					url: "<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=upRealProduct&selectIndex=14&realCode=" + realCode +"&realCount=" + realCount,
					cache: false,
					data: {type: "1"},
					success: function(msg, reqStatus){
						$("#real_td").empty();
						$("#real_td").html(msg);
						$('#realCode').val("");
						$('#realCount').val("");
						$('#realCode').focus();
					}
				});
			});
		});
		</script>
		<table border="1"   width="95%" align="center"  cellspacing="0" bgcolor="FFFFE0">
			<tr bgcolor="#00ccff" align="center">
				<td>实际退回商品</td><td>原名称</td><td>商品名称</td><td>数量</td><td>操作</td></tr>
			<tr align="center">
		<%if(rpList != null && rpList.size()>0){for (voProduct bean:rpList) { %>
			<tr align="center">
				<td><%=bean.getCode() %></td><td><%=bean.getOriname() %></td><td><%=bean.getName() %></td><td><input type="text" id="<%=bean.getCode() %>"name="realName" size="2" value="<%=bean.getCount()%>"></td><td><input id="<%=bean.getCode() %>" type="button" value="删除" name="del"></td></tr>
			<tr align="center">
		<%} }%>
		</table>
	<%}%>
	