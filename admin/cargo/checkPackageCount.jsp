<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	List<String> cdaList = (List<String>)request.getAttribute("cdaList");
	
	Map<String,String> map = (Map<String,String>)request.getAttribute("map");
	String result = (String)request.getAttribute("result");
	int packageCount = 0;
	int productCount = 0;
	int skuCount = 0;
	if(result!=null && !"".equals(result)){
		packageCount = Integer.valueOf(result.split(",")[0]);
		productCount = Integer.valueOf(result.split(",")[1]);
		skuCount = Integer.valueOf(result.split(",")[2]);
	}
	Date date = new Date();
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	String time = (String)request.getAttribute("querytime");
	boolean flag = true;
	if(time==null||"".equals(time)){
	  flag = false;
	  time=format.format(date);;
	  }
	String productCode = (String)request.getAttribute("productCode");
	productCode=productCode==null?"":productCode;
	//out.print(productCode==null);
	String areaId = (String)request.getAttribute("areaId");	
	areaId=areaId==null?"":areaId;
	String operationid = (String)request.getAttribute("operationId");
	operationid=operationid==null?"":operationid;
%>
<html>
<head>
	<title>入库作业统计</title>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script>
   function check(){
       var flag = true;
              
       if($("#startTime").val()==""){
       		alert("请选择一个时间!");
       		return false;
       }
       return flag;
   }
</script>
</head>
<form name="countResult" action="returnPackage.do?method=getCountResult" method="POST" onsubmit="return check();">
包裹核查统计：地区：<select name="areaId" id="areaId">
<option value="">请选择</option>
<% 
	 if(map!=null){
     Set<String> key = map.keySet();
     //System.out.print("--"+key.size());
     Iterator it = key.iterator(); 
     String area = "";
     String areaid = "";
       while (it.hasNext()) { 
        areaid = (String)it.next();
        area = map.get(areaid);
%>
<option value="<%=areaid%>" <%= areaId.equals(areaid) ? "selected": "" %>><%= area %></option>
<%
}
}
 %>
</select>
<table width="100%" border="1">
	<tr>
		<td>包裹核查统计：</td>
	</tr>
	<tr>
		<td>核查时间：<input type="text" size="15" name="startTime" id="startTime" value="<%= time %>" onclick="WdatePicker();" />&nbsp;&nbsp;&nbsp;&nbsp;入库操作人：<input type="text" value="<%= operationid %>" name="operationid" id="operationid"/>&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="查询"/></td>
	</tr>
</table>
</form>
<% if(flag){ %>
<table>
	<tr>
		<td><%= time%>共入库<%= packageCount %>个包裹，商品总计：<%= skuCount %>个sku,<%= productCount %>件商品</td>
	</tr>
</table>
<%
}
%>
</html>