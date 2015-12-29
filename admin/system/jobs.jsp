<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.timedtask.TimedTaskDispatcher" %>
<%@ page import="org.quartz.*" %>
<%@ page import="java.util.*" %><%@ page import="adultadmin.framework.*" %><%@ page import="adultadmin.util.*" %>
<%
	CustomAction action = new CustomAction(request);
	
	Scheduler sch = TimedTaskDispatcher.getInstance().getScheduler();
	int act=action.getParameterInt("a");
	if(act>0){
		String name=action.getParameterString("name");
		String group=action.getParameterString("group");
		if(act==1){
			// 立即执行
			sch.triggerJob(name,group);
		}else if(act==2){
			sch.deleteJob(name,group);
		}else if(act==3){
			sch.pauseJob(name,group);
		}else if(act==4){
			sch.resumeJob(name,group);
		}
		response.sendRedirect("jobs.jsp");
		return;
	}
%>
<html>
<head>
<title>定时任务管理</title>
<link href="../../css/global.css" rel="stylesheet" type="text/css">
</head>
<body>

<table width="800px" cellpadding="5" cellspacing="1" bgcolor="#e8e8e8" align="center">
<tr><td></td><td>任务类</td><td>下次执行时间</td><td>操作</td></tr>
<%
int c=0;
	List list = new ArrayList(10);
	String[] groups = sch.getJobGroupNames();
	for(int i=0;i<groups.length;i++){
		String[] names=sch.getJobNames(groups[i]);
		for(int j=0;j<names.length;j++){
			JobDetail job = sch.getJobDetail(names[j],groups[i]);
			Trigger t = sch.getTrigger(names[j],groups[i]);
			int state = sch.getTriggerState(names[j],groups[i]);
%><tr bgcolor='#F8F8F8'>
<td><%=++c%></td>
<td><%=job.getJobClass().getName()%></td>
<td><%=DateUtil.formatDate(t.getNextFireTime(),"yyyy-MM-dd HH:mm:ss")%></td>
<td><%=TimedTaskDispatcher.getJobStatus(names[j])%></td>
<td><%if(state!=Trigger.STATE_PAUSED){%><a href="jobs.jsp?a=3&name=<%=job.getName()%>&group=<%=job.getGroup()%>">暂停</a>
<%}else{%>
<a href="jobs.jsp?a=4&name=<%=job.getName()%>&group=<%=job.getGroup()%>">恢复</a><%}%></td>
<td><a href="jobs.jsp?a=2&name=<%=job.getName()%>&group=<%=job.getGroup()%>" onclick="return confirm('确定删除?')">删除</a></td>
<td><a href="jobs.jsp?a=1&name=<%=job.getName()%>&group=<%=job.getGroup()%>" onclick="return confirm('确定立刻执行?')">执行</a></td>
</tr>
<%}}%>
</table>

</body>
</html>