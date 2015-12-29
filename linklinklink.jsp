<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voPVStat" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.text.*"%>
<%@ page import="org.jfree.chart.JFreeChart"%>
<%@ page import="org.jfree.chart.ChartFactory"%>
<%@ page import="org.jfree.chart.labels.StandardPieSectionLabelGenerator"%>
<%@ page import="org.jfree.chart.axis.NumberAxis"%>
<%@ page import="org.jfree.chart.plot.PiePlot"%>
<%@ page import="org.jfree.data.time.TimeSeries"%>
<%@ page import="org.jfree.data.time.TimeSeriesCollection"%>
<%@ page import="org.jfree.data.xy.XYDataset"%>
<%@ page import="org.jfree.data.time.Hour"%>
<%@ page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="org.jfree.data.time.Day"%>
<%@ page import="java.util.*, cache.*" %>
<%
String curDate = request.getParameter("date");
if(curDate == null)
	curDate = DateUtil.formatDate(new Date());

int accessLogId = StatCache.getAccessLogId(curDate);
int onlineLogId = StatCache.getOnlineLogId(curDate);	

Connection conn = DbUtil.getConnection("java:/comp/env/jdbc/adult");
	Statement st = conn.createStatement();	
	PVMap pvmap = new PVMap();

String condition = "";
int hour = -1;
try {
	hour = Integer.parseInt(request.getParameter("hour"));
} catch (Exception e){
}
condition = "(fr >= 151 and fr <= 200)";
if(hour >= 0){
	String s = null;
	if(hour < 10){
		s = "0" + hour;
	}
	else {
		s = "" + hour;
	}
	condition = "(fr >= 151 and fr <= 200 and left(time, 13) = '" + curDate + " " + s + "')";
}

try{
	ResultSet rs = null;
	rs = st.executeQuery("select fr d,count(*) from access_log where left(time,10)='" + curDate + "' and id >= " + accessLogId + " and " + condition + " group by d");
	System.out.println("select fr d,count(*) from access_log where left(time,10)='" + curDate + "' and id >= " + accessLogId + " and " + condition + " group by d");
	while(rs.next())
		pvmap.getadd(rs.getInt(1)).allpv = rs.getInt(2);
	rs.close();

	rs = st.executeQuery("select fr d,count(*) from online_log where left(time,10)='" + curDate + "' and id >= " + onlineLogId + " and " + condition + " group by d");
	
	while(rs.next())
		pvmap.getadd(rs.getInt(1)).visitcount = rs.getInt(2);
	rs.close();

	rs = st.executeQuery("select fr d,count(distinct(cp)) from online_log where cp!='' and left(time,10)='" + curDate + "' and id >= " + onlineLogId + " and " + condition + " group by d");
	while(rs.next())
		pvmap.getadd(rs.getInt(1)).phonecount = rs.getInt(2);
	rs.close();
	/*
	rs = st.executeQuery("select fr d,count(distinct(cp)) from online_log a,user_status b where left(time,10)='" + curDate + "' and a.cp=b.phone and b.create_datetime>'" + curDate + "' and a.id >= " + onlineLogId + " and " + condition + " group by d");
	while(rs.next())
		pvmap.getadd(rs.getInt(1)).newphonecount = rs.getInt(2);
	rs.close();

	rs = st.executeQuery("select fr d,count(distinct(cp)) from online_log a,user_status b where left(time,10)='" + curDate + "' and a.cp=b.phone and b.create_datetime<'" + curDate + "' and a.id >= " + onlineLogId + " and " + condition + " group by d");
	while(rs.next())
		pvmap.getadd(rs.getInt(1)).oldphonecount = rs.getInt(2);
	rs.close();
	*/

	st.close();
} catch(Exception e){e.printStackTrace();}
conn.close();

%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left width="350"><%=curDate%></td>
	</tr>
</table> <br/>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="150">来源</td>
		<td align=center  width="150">PV总数</td>
		<td align=center  width="150">访问人次</td>
		<td align=center  width="150">独立手机号总数</td>
		<td align=center  width="150">新独立手机号</td>
		<td align=center  width="150">旧独立手机号</td>
	</tr>
<%Iterator iter = pvmap.keySet().iterator();
while(iter.hasNext()){
Integer key = (Integer)iter.next();
voPVStat vo = (voPVStat)pvmap.get(key);
%>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="150"><%=key%></td>
		<td align=center width="150"><%=vo.allpv%></td>
		<td align=center width="150"><%=vo.visitcount%></td>
		<td align=center width="150"><%=vo.phonecount%></td>
		<td align=center width="150"><%=vo.newphonecount%></td>
		<td align=center width="150"><%=vo.oldphonecount%></td>
	</tr>
<%}%>
</table>
        </td>
    </tr>
  </table>
<p align="center">
<%
for(int i = 0; i < 24; i ++){
%><a href="linklinklink.jsp?hour=<%=i%>"><%=i%></a><%
	if(i != 23){
	    out.print("|");
    }
}
%>
</p>
</body>
</html>