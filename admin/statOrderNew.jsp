<%@ page contentType="text/html;charset=utf-8" %><%@ page contentType="text/html;charset=utf-8" %><%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="adultadmin.bean.stock.*"%><%@ page import="adultadmin.util.*,adultadmin.util.db.DbOperation,adultadmin.service.ServiceFactory,adultadmin.service.infc.*,java.sql.ResultSet"%>
<%@ page import="adultadmin.action.vo.voUser" %><%!
static String[] typeCond = {
	"buy_mode=0",	//	货到付款
	"buy_mode=1",	//	邮购
	"buy_mode=2",	//	上门自取
	"order_type = 5",//电脑
    "order_type = 3",//内衣
    "order_type in(1,10)",//手机
    "order_type = 1",//国产手机
    "order_type = 10",//行货手机
    "order_type = 2",//数码
    "order_type = 6",//运动鞋
    "order_type = 4",//服装
    "order_type = 7",//护肤
    "order_type = 13",//保健品 
    "order_type = 9",//其他
    "order_type in (11,15)",//饰品
    "order_type = 8",//小家电
    "order_type = 12",//包 
    "order_type = 14",//手表  
    "order_type = 16",//新奇特 
    "order_type = 17",//成人
    "order_type = 18"//时尚鞋
	        };
static String[] statusCond = {
	"1",		// 总数
	"status<>10",		// 去重
	"status in (6,11,12,13,14)",		// 实际发单(发货)
	"status in (3,6,9,12,14)", 	// 实际成交
	"status in (11,13)", 	// 退单
	        };
static String[] statusName1 = {
	"总单数",
	"去重单数",	
	"实际发货单数",
	"实际成交单数",
	"退单数",
	        };
static int getSum(int[][] p, int j){	// 计算商品线单数总和
	return p[3][j]+p[4][j]+p[5][j]+p[8][j]+p[9][j]+p[10][j]+p[11][j]+p[12][j]+p[13][j]+p[14][j]+p[15][j]+p[16][j]+p[17][j]+p[18][j]+p[19][j]+p[20][j];
}
static float getSum(float[][] p, int j){	// 计算商品线金额总和
	return p[3][j]+p[4][j]+p[5][j]+p[8][j]+p[9][j]+p[10][j]+p[11][j]+p[12][j]+p[13][j]+p[14][j]+p[15][j]+p[16][j]+p[17][j]+p[18][j]+p[19][j]+p[20][j];
}
%><%
voUser user = (voUser)session.getAttribute("userView");
String startDate = StringUtil.dealParam(request.getParameter("startDate"));
String endDate = StringUtil.dealParam(request.getParameter("endDate"));
String area = request.getParameter("a");
String[] isOldUser = request.getParameterValues("isOldUser");
String oldUserStrs=null;
String selectStr="";
if(isOldUser!=null){
	if(isOldUser.length==1)
		oldUserStrs=isOldUser[0];
	for(int i=0;i<isOldUser.length;i++){
		selectStr+=isOldUser[i]+",";
	}
}

int[][] counts=null;
float[][] prices=null;
if(startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
        
}else{

    DbOperation dbOp = new DbOperation();
    dbOp.init("adult_slave2");
    IUserService service = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, dbOp);
    try {
        String condition = null;
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        //根据库存操作查询
        if (startDate != null && startDate.length() > 0) {
        	//rs = service.getDbOp().executeQuery("select id from user_order where create_datetime > '" + startDate + "' order by id asc limit 1");
        	int userOrderId = 0;
       		userOrderId = StatUtil.getDayFirstOrderId(startDate);
        	if(userOrderId > 0){
        		buf.append("uo.id >= ").append(userOrderId).append(" ");
        	} else {
                buf.append(" uo.create_datetime > '");
                buf.append(startDate);
                buf.append("' ");
        	}
        }
        if (endDate != null && endDate.length() > 0) {
        	//rs = service.getDbOp().executeQuery("select id from user_order where create_datetime <= '" + endDate + " 23:59:59' order by id desc limit 1");
        	int userOrderId = 0;
        	userOrderId = StatUtil.getDayFirstOrderId(DateUtil.formatDate(DateUtil.rollDate(DateUtil.parseDate(endDate),1),DateUtil.normalDateFormat));
        	if(userOrderId > 0){
        		buf.append(" and uo.id < ").append(userOrderId).append(" ");
        	} else {
                buf.append(" and uo.create_datetime <= '");
                buf.append(endDate);
                buf.append(" 23:59:59' ");
        	}
        }
		if("2".equals(area)){	// 如果是参数2表示无锡单，否则都算北京
			buf.append(" and uo.areano=9 ");
		}else if("1".equals(area)){
			buf.append(" and uo.areano!=9 ");
		}
        if(oldUserStrs!=null && oldUserStrs.length()>0){
        	buf.append(" and uo.is_olduser =").append(oldUserStrs).append(" ");
        }
        condition = buf.toString();
        
		counts = new int[typeCond.length][statusCond.length];
		prices = new float[typeCond.length][statusCond.length];


		for(int i = 0;i < typeCond.length;i++)
			for(int j = 0;j < statusCond.length;j++) {
				rs = service.getDbOp().executeQuery("select count(*), sum(dprice) from user_order uo where buy_mode in (0,1,2) and "+condition+" and " + typeCond[i] + " and " + statusCond[j]);
		        if(rs.next()){
		        	counts[i][j] = rs.getInt(1);
		        	prices[i][j] = rs.getFloat(2);
		        }
			}

    } finally {
    	if(dbOp != null)
    		dbOp.release();
    }
}

voOrder vo = null;
%>
<html>
<title>买卖宝后台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<pre>
规则说明:
1、时间：订单生成日期
2、总单数：所有显示的用户订单数（无论订单是否有效，包括垃圾订单在内）；
   实际发货单数：最终被快递取走的“实际”订单总数，括号北京和广东
   实际成交订单数：用户确定交易成交的订单数(去除退单)
   实际成交率：(成交订单数/非重总单数)
   实际成交额：按照实际发货单数计算的成交额；
   单笔订单成交额：实际成交额/实际发货单数
   退单数：由于用户拒收或其他原因导致的退单数
   退单率：退单数/发货单数
   额度退单率=退单金额/发货金额
   退单额：按照退货单数计算的总额
3、货到付款+邮购+上门自取=总数=成人用品/内衣+手机+数码产品+服装+保健品及其他 
</pre>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<%
startDate = StringUtil.convertNull(request.getParameter("startDate"));
endDate = StringUtil.convertNull(request.getParameter("endDate"));
String[][] result = (String[][])request.getAttribute("result");
%>
<form method="post" action="statOrderNew.jsp?a=<%=area%>">
<table>
<tr><td>
订单创建时间：</td><td>
<input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">
</td>
<td>
&nbsp;&nbsp;&nbsp;
<input type="checkbox" name="isOldUser" value="1">老用户&nbsp;&nbsp;&nbsp;
<input type="checkbox" name="isOldUser" value="0">新用户
<script type="text/javascript">checkboxChecked(document.getElementsByName('isOldUser'),'<%=selectStr%>')</script>
</td>
</tr>
</table>
<input type=submit value=" 统 计 "><br><br>
</form>
</td></tr>
</table>
</td></tr></table>
<table border="1" cellpadding="2" align="center" width="95%">
	<thead>
		<tr>
			<th>订单属性</th>
			<th>货到付款</th>
			<th>钱包支付</th>
			<th>银行汇款</th>
			<th>电脑</th>
			<th>内衣</th>
			<th>手机</th>
			<th>国产手机</th>
			<th>行货手机</th>
			<th>数码产品</th>
			<th>运动鞋</th>
			<th>服装</th>
			<th>护肤品</th>
			<th>保健品</th>
			<th>其他</th>
			<th>饰品</th>
			<th>小家电</th>
			<th>包</th>
			<th>手表</th>
			<th>新奇特</th>
			<th>成人</th>
			<th>时尚鞋</th>
			<th>总数</th>
		</tr>
	</thead>
	<tbody style="font-weight:bold;">
<%if(prices != null){ 
int j = 0;
%>

<%j=0;%><tr>
	<td align="right"><%=statusName1[j]%></td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= counts[i][j] %></td>
<%}%>
	<td align="right"><%= counts[0][j]+counts[1][j]+counts[2][j] %></td>
</tr>

<%j=1;%><tr>
	<td align="right"><%=statusName1[j]%></td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= counts[i][j] %></td>
<%}%>
	<td align="right"><%= counts[0][j]+counts[1][j]+counts[2][j] %></td>
</tr>

<%j=2;%><tr>
	<td align="right"><%=statusName1[j]%></td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= counts[i][j] %></td>
<%}%>
	<td align="right"><%= getSum(counts,j) %></td>
</tr>

<%j=3;%><tr>
	<td align="right"><%=statusName1[j]%></td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= counts[i][j] %></td>
<%}%>
	<td align="right"><%= getSum(counts,j) %></td>
</tr>


<tr>
	<td align="right">新成交率<br/>(成交订单数/非重总单数)</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%=NumberUtil.div(counts[i][3] * 100, counts[i][1])%>%</td>
<%}%>
	<td align="right"><%=NumberUtil.div((getSum(counts,3)) * 100, getSum(counts,1))%>%</td>
</tr>

<%j=3;%><tr>
	<td align="right">实际成交额</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= prices[i][j] %></td>
<%}%>
	<td align="right"><%= getSum(prices,j) %></td>
</tr>

<%j=3;%><tr>
	<td align="right">实际单笔订单成交额</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= NumberUtil.div(prices[i][j],counts[i][j]) %></td>
<%}%>
	<td align="right"><%= NumberUtil.div(getSum(prices,j),getSum(counts,j)) %></td>
</tr>



<%j=4;%><tr>
	<td align="right"><%=statusName1[j]%></td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= counts[i][j] %></td>
<%}%>
	<td align="right"><%= getSum(counts,j) %></td>
</tr>

<tr>
	<td align="right">退单率<br/>(退单数/发货单数)</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%=NumberUtil.div(counts[i][4] * 100, counts[i][2])%>%</td>
<%}%>
	<td align="right"><%=NumberUtil.div((getSum(counts,4)) * 100, getSum(counts,2))%>%</td>
</tr>

<%j=4;%><tr>
	<td align="right">退单额</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= prices[i][j] %></td>
<%}%>
	<td align="right"><%= getSum(prices,j) %></td>
</tr>

<tr>
	<td align="right">额度退单率<br/>(退单金额/发货金额)</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%=NumberUtil.div(prices[i][4] * 100, prices[i][2])%>%</td>
<%}%>
	<td align="right"><%=NumberUtil.div((getSum(prices,4)) * 100, getSum(prices,2))%>%</td>
</tr>

<%j=4;%><tr>
	<td align="right">退单单笔订单额</td>
<%for(int i = 0;i < typeCond.length;i++) {%>
	<td align="right"><%= NumberUtil.div(prices[i][j],counts[i][j]) %></td>
<%}%>
	<td align="right"><%= NumberUtil.div(getSum(prices,j),getSum(counts,j)) %></td>
</tr>

<%} %>
	</tbody>
	<tfoot>
	</tfoot>
</table>
          <br>   
</body>
</html>