<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="adultadmin.action.vo.voOrder,java.util.*,mmb.stock.stat.DeliverCorpInfoBean"%>
<%
Map map = voOrder.deliverInfoMapAll;
Iterator iter = map.entrySet().iterator();
while (iter.hasNext()) {
Map.Entry entry = (Map.Entry) iter.next();
Object key = entry.getKey();
DeliverCorpInfoBean val = (DeliverCorpInfoBean)entry.getValue();
%>
<%=key%>&nbsp;&nbsp;&nbsp;&nbsp;<%=val.getName()%>,<%=val.getName()%><br/>
<%
}
%>