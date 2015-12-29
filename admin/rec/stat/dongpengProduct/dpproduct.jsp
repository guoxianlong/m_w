<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ page isELIgnored="false" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*,adultadmin.framework.*,java.math.*" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*,java.net.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="cache.CatalogCache"%>
<%@ page import="adultadmin.action.vo.voCatalog"%>
<%@ page import="java.util.*,adultadmin.action.stat.SearecDpproductAction" %>
<%@ page import="adultadmin.bean.supplier.*" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
List<SupplierStandardInfoBean> supplierList = (List<SupplierStandardInfoBean>)request.getAttribute("supplierList");
	voUser adminUser = (voUser)session.getAttribute("userView");

	//数据库大查询锁，等待3秒
	if (!DbLock.slaveServerQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp");
		return;
	}
	Connection conn = null;
	UserGroupBean group = adminUser.getGroup();
	boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (adminUser.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (adminUser.getSecurityLevel() == 5);	//普通管理员
	boolean isPingtaiyunwei = (adminUser.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (adminUser.getPermission() == 7);	//销售部
	boolean isShangpin = (adminUser.getPermission() == 6);	//商品部
	boolean isTuiguang = (adminUser.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (adminUser.getPermission() == 4);	//运营中心
	boolean isKefu = (adminUser.getPermission() == 3);	//客服部	
	DbLock.slaveServerOperator = adminUser.getUsername() + "_动碰商品统计_" + DateUtil.getNow();
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
	int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
    int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));
    String startDate = (String) request.getAttribute("startDate");
    String endDate = (String) request.getAttribute("endDate");
%>
<html>
  <head>
    <title>动碰商品统计</title> 
<%
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
$.messager.alert('提示','<%=tip%>');
history.back(-1);
</script>
<%
	return;
}
else if("success".equals(result)){
%>
<script>
self.close();
window.opener.location.reload();
</script>
<%
	return;
}%>
    
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%= request.getContextPath() %>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%= request.getContextPath() %>/admin/js/pts.js"></script>
<script language="JavaScript" src="<%= request.getContextPath() %>/admin/js/pub.js"></script>
<SCRIPT src="<%= request.getContextPath() %>/js/sorttable.js" type="text/javascript"></SCRIPT>  
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/demo/demo.css">
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<style type="text/css">
	body {
		padding: 0px;
	}
	form {
		padding: 0px;
		margin-left: 0px;
		margin-right:0px;
	}
</style>
<script type="text/javascript">
function searchLoadPage() {
		var parentId1 = $("#parentId1").val();
		var parentId2 = $("#parentId2").val();
		var proxy = $("#proxy").val();
		var startDate = $("#startDate").val();
		var endDate = $("#endDate").val();
		$("#hparentId1").val(parentId1);
		$("#hparentId2").val(parentId2);
		$("#hproxy").val(proxy);
		$("#hstartDate").val(startDate);
		$("#hendDate").val(endDate);
		$("#info_table").datagrid('load',{  
			parentId1:$("#hparentId1").val(),
			parentId2:$("#hparentId2").val(),
			proxy:$("#hproxy").val(),
			startDate:$("#hstartDate").val(),
			endDate:$("#hendDate").val()
		});
}
</script>

</head>
<body>
<osCache:cache scope="application" time="900">
<div id="tb">
<fieldset>
	<legend>动碰商品查询</legend>
<form name="sForm" method=post action="">

一级分类：<select  name="parentId1" id="parentId1" class="bd" style="width:140" onchange="javascript:sredirect(this.options.selectedIndex - 1);">
	<option value="0">全部</option>
			<%
				HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list = (List)map.get(Integer.valueOf(0));
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					voCatalog catalog = (voCatalog)iter.next();
			 %>
			 <option value="<%=catalog.getId() %>"><%=catalog.getName() %></option>
			<%} %>
			<option value="145">内衣</option>
			<option value="151">护肤品</option>
			<option value="163">新奇特商品</option>
		</select>
	<input type="hidden" id="hparentId1" value="0"/>
    二级分类：<select name="parentId2" id="parentId2" class="bd" style="width:140">
	<option value="0">全部</option>
</select>
<input type="hidden" id="hparentId2" value="0"/>
<script type="text/javascript"></script>
<script>
var spt = document.forms[0].parentId2
function sredirect(x){
  for (m = spt.options.length - 1; m > 0; m --)
      spt.options[m] = null;
  if(x < 0){
	return;
  }
  spt.options[0]=new Option("全部", "0");
  for (i = 0; i < spts[x].length; i ++){	 
      spt.options[i + 1]=new Option(spts[x][i].text, spts[x][i].value)
  }
  spt.options[0].selected=true
}

selectOption(document.forms[0].parentId1, '<%= parentId1 %>');
sredirect(document.forms[0].parentId1.selectedIndex - 1);
selectOption(document.forms[0].parentId2, '<%= parentId2 %>');
</script>
代理商：<select name="proxy" id="proxy" style="width:130" >
		    <option value="0">所有</option>
		<%
		 	for(int i = 0; i< supplierList.size(); i++ ){
		 		SupplierStandardInfoBean ssiBean = supplierList.get(i);
		%><option value="<%=ssiBean.getId()%>" ><%=ssiBean.getName()%></option>
		<%}%>
		</select><br/><br/>
<input type="hidden" id="hproxy" value="0"/>
时间范围：<input type=text name="startDate" id="startDate" size="20" <%if(startDate!=null) {%> value="<%=startDate %>"<%} %> onclick="SelectDate(this,'yyyy-MM-dd');">
到
<input type=text name="endDate" id="endDate" size="20" <%if(endDate!=null) {%> value="<%=endDate %>"<%} %> onclick="SelectDate(this,'yyyy-MM-dd');"><br/><br/>
<input type="hidden" id="hstartDate" value="" />
<input type="hidden" id="hendDate" value="" />
<a href="javascript:searchLoadPage();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" >查询</a>
</form>
</fieldset>
</div>
共有:<span id="totalCount2">0</span>种产品 &nbsp;&nbsp;&nbsp;
<%if(group.isFlag(127)){ %>
总金额:<span id="totalCount1">0</span>
<%} %>&nbsp;&nbsp;
</osCache:cache>  
<script type="text/javascript" >
		$(function(){
		   $('#info_table').datagrid({  
		   	fit:true, 
		    fitColumns : true,
		    border : true,    
		    pageNumber:1,  
		    pageSize:20,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'<%= request.getContextPath()%>/admin/stat/searchTouchProductStat.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[
		     {title:'编号',field:'product_code',width:110,rowspan:3,align:'center'},
		     {title:'名称',field:'product_name',width:270,rowspan:3,align:'center'},
		     {title:'采购入库量',field:'buy_stockin_count',width:90,rowspan:3,align:'center'},
		     {title:'销量',field:'sale_count',width:90,rowspan:3,align:'center'},
		     {title:'销售退货量',field:'sale_return_count',width:80,rowspan:3,align:'center'},
		     {title:'动碰次数',field:'touch_time',width:90,rowspan:3,align:'center'},
		     {title:'待验库',field:'check_stock',width:90,rowspan:3,align:'center'},
		     {title:'合格库',field:'qualify_stock',width:90,rowspan:3,align:'center'},
		     {title:'退货库',field:'return_stock',width:80,rowspan:3,align:'center'},
		     {title:'返厂库',field:'back_stock',width:80,rowspan:3,align:'center'},
		     {title:'维修库',field:'fix_stock',width:80,rowspan:3,align:'center'},
		     {title:'残次品库',field:'bad_stock',width:80,rowspan:3,align:'center'},
		     {title:'样品库',field:'sample_stock',width:80,rowspan:3,align:'center'}
		    ]],
		    toolbar:'#tb',
		    //pagination:true,
		    rownumbers:false, //是否有行号。
		    onLoadSuccess: function(data) {
		    	var parentId1 = $("#hparentId1").val();
				var parentId2 = $("#hparentId2").val();
				var proxy = $("#hproxy").val();
				var startDate = $("#hstartDate").val();
				var endDate = $("#hendDate").val();
				$("#parentId1").val(parentId1);
				$("#parentId2").val(parentId2);
				$("#proxy").val(proxy);
				$("#startDate").val(startDate);
				$("#endDate").val(endDate);
		    	if( data['tip'] != null && data['tip'] != "" ) {
		    		jQuery.messager.alert("提示", data['tip']);
		    	}
	    		$("#totalCount1").html(data['totalCount1']);
	    		$("#totalCount2").html(data['totalCount2']);
		    }
		   });
		   
		  });
	</script>
	<table align="center" id="info_table" >
	</table>
</body>
</html>
