<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="adultadmin.action.stock.OrderStockAction"%>
<%@page import="adultadmin.service.IAdminService"%>
<%@page import="adultadmin.service.ServiceFactory"%>
<%@page import="adultadmin.service.infc.ICargoService"%>
<%@page import="adultadmin.service.infc.IBaseService"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%><html>
<head>
<title>2011年历史数据</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8" />
<%
	IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
int[][] data=new int[48][12];//最终数据，初始化为0
try{
	//采购入库卡片，销售出库卡片
	String sql="select sc.create_datetime,sc.stock_out_count,sc.stock_in_count,sc.card_type,pl.name from stock_card sc join product p on sc.product_id=p.id join product_line_catalog plc on p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id join product_line pl on plc.product_line_id=pl.id where sc.card_type between 1 and 2 and sc.create_datetime between '2011-01-01 00:00:00' and '2011-12-31 23:59:59'";
	Statement st = null;
	ResultSet rs = null;
	st = adminService.getDbOperation().getConn().createStatement();
	rs = st.executeQuery(sql);
	while(rs.next()){
		String createDatetime=rs.getString("sc.create_datetime");//入库时间
		int stockInCount=rs.getInt("sc.stock_in_count");//入库量
		int stockOutCount=rs.getInt("sc.stock_out_count");//出库量
		int cardType=rs.getInt("sc.card_type");
		int month=Integer.parseInt(createDatetime.substring(5,7));//月份，1-12
		String plName=rs.getString("pl.name");
		if(cardType==1){//采购入库卡片
	if(plName.equals("服装")){
		data[0][month-1]=data[0][month-1]+stockInCount;
	}else if(plName.equals("鞋子")){
		data[1][month-1]=data[1][month-1]+stockInCount;
	}else if(plName.equals("包")){
		data[2][month-1]=data[2][month-1]+stockInCount;
	}else if(plName.equals("护肤品")){
		data[3][month-1]=data[3][month-1]+stockInCount;
	}else if(plName.equals("礼品")){
		data[4][month-1]=data[4][month-1]+stockInCount;
	}else if(plName.equals("新奇特")){
		data[5][month-1]=data[5][month-1]+stockInCount;
	}else if(plName.equals("电脑")){
		data[6][month-1]=data[6][month-1]+stockInCount;
	}else if(plName.equals("手机数码")){
		data[7][month-1]=data[7][month-1]+stockInCount;
	}else if(plName.equals("手机数码配件")){
		data[8][month-1]=data[8][month-1]+stockInCount;
	}else if(plName.equals("保健品/内衣")){
		data[9][month-1]=data[9][month-1]+stockInCount;
	}else if(plName.equals("行货手机")){
		data[10][month-1]=data[10][month-1]+stockInCount;
	}else if(plName.equals("成人日用")){
		data[11][month-1]=data[11][month-1]+stockInCount;
	}else if(plName.equals("小家电")){
		data[12][month-1]=data[12][month-1]+stockInCount;
	}else if(plName.equals("饰品/配饰")){
		data[13][month-1]=data[13][month-1]+stockInCount;
	}else if(plName.equals("鞋配饰")){
		data[14][month-1]=data[14][month-1]+stockInCount;
	}else if(plName.equals("手表")){
		data[15][month-1]=data[15][month-1]+stockInCount;
	}
		}else if(cardType==2){//销售出库卡片
	if(plName.equals("服装")){
		data[32][month-1]=data[32][month-1]+stockOutCount;
	}else if(plName.equals("鞋子")){
		data[33][month-1]=data[33][month-1]+stockOutCount;
	}else if(plName.equals("包")){
		data[34][month-1]=data[34][month-1]+stockOutCount;
	}else if(plName.equals("护肤品")){
		data[35][month-1]=data[35][month-1]+stockOutCount;
	}else if(plName.equals("礼品")){
		data[36][month-1]=data[36][month-1]+stockOutCount;
	}else if(plName.equals("新奇特")){
		data[37][month-1]=data[37][month-1]+stockOutCount;
	}else if(plName.equals("电脑")){
		data[38][month-1]=data[38][month-1]+stockOutCount;
	}else if(plName.equals("手机数码")){
		data[39][month-1]=data[39][month-1]+stockOutCount;
	}else if(plName.equals("手机数码配件")){
		data[40][month-1]=data[40][month-1]+stockOutCount;
	}else if(plName.equals("保健品/内衣")){
		data[41][month-1]=data[41][month-1]+stockOutCount;
	}else if(plName.equals("行货手机")){
		data[42][month-1]=data[42][month-1]+stockOutCount;
	}else if(plName.equals("成人日用")){
		data[43][month-1]=data[43][month-1]+stockOutCount;
	}else if(plName.equals("小家电")){
		data[44][month-1]=data[44][month-1]+stockOutCount;
	}else if(plName.equals("饰品/配饰")){
		data[45][month-1]=data[45][month-1]+stockOutCount;
	}else if(plName.equals("鞋配饰")){
		data[46][month-1]=data[46][month-1]+stockOutCount;
	}else if(plName.equals("手表")){
		data[47][month-1]=data[47][month-1]+stockOutCount;
	}
		}else{//不是采购入库也不是销售出库，属于错误情况，开始查看下一个卡片
	continue;
		}
	}
	rs.close();
	st.close();
	//每月平均库存量
	String sql2="select psh.log_date,psh.stock,psh.stock_gd,pl.name from product_stock_history psh join product p on psh.product_id=p.id join product_line_catalog plc on p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id join product_line pl on plc.product_line_id=pl.id where log_date between '2011-01-01' and '2011-12-31'";
	Statement st2 = null;
	ResultSet rs2 = null;
	st2 = adminService.getDbOperation().getConn().createStatement();
	rs2 = st2.executeQuery(sql2);
	while(rs2.next()) {
		String logDate=rs2.getString("psh.log_date");//日期
		int stock=rs2.getInt("psh.stock");//产品库存
		int stockGD=rs2.getInt("psh.stock_gd");//产品库存
		String plName=rs2.getString("pl.name");
		int month=Integer.parseInt(logDate.substring(5,7));//月份，1-12
		if(plName.equals("服装")){
	data[16][month-1]=data[16][month-1]+stock+stockGD;
		}else if(plName.equals("鞋子")){
	data[17][month-1]=data[17][month-1]+stock+stockGD;
		}else if(plName.equals("包")){
	data[18][month-1]=data[18][month-1]+stock+stockGD;
		}else if(plName.equals("护肤品")){
	data[19][month-1]=data[19][month-1]+stock+stockGD;
		}else if(plName.equals("礼品")){
	data[20][month-1]=data[20][month-1]+stock+stockGD;
		}else if(plName.equals("新奇特")){
	data[21][month-1]=data[21][month-1]+stock+stockGD;
		}else if(plName.equals("电脑")){
	data[22][month-1]=data[22][month-1]+stock+stockGD;
		}else if(plName.equals("手机数码")){
	data[23][month-1]=data[23][month-1]+stock+stockGD;
		}else if(plName.equals("手机数码配件")){
	data[24][month-1]=data[24][month-1]+stock+stockGD;
		}else if(plName.equals("保健品/内衣")){
	data[25][month-1]=data[25][month-1]+stock+stockGD;
		}else if(plName.equals("行货手机")){
	data[26][month-1]=data[26][month-1]+stock+stockGD;
		}else if(plName.equals("成人日用")){
	data[27][month-1]=data[27][month-1]+stock+stockGD;
		}else if(plName.equals("小家电")){
	data[28][month-1]=data[28][month-1]+stock+stockGD;
		}else if(plName.equals("饰品/配饰")){
	data[29][month-1]=data[29][month-1]+stock+stockGD;
		}else if(plName.equals("鞋配饰")){
	data[30][month-1]=data[30][month-1]+stock+stockGD;
		}else if(plName.equals("手表")){
	data[31][month-1]=data[31][month-1]+stock+stockGD;
		}
	}
	for(int i=0;i<16;i++){
		for(int j=0;j<12;j++){
	int dayCount=1;
	if(j==0||j==2||j==4||j==6||j==7||j==9||j==11){
		dayCount=31;
	}else if(j==1){
		dayCount=28;
	}else if(j==3||j==5||j==8||j==10){
		dayCount=30;
	}
	if(data[i+16][j]%dayCount==0){
		data[i+16][j]=data[i+16][j]/dayCount;
	}else{
		data[i+16][j]=(data[i+16][j]-data[i+16][j]%dayCount)/dayCount+1;
	}
		}
	}
	rs2.close();
	st2.close();
}catch(Exception e){
	e.printStackTrace();
}finally{
	adminService.close();
}

response.setContentType("application/vnd.ms-excel");
response.setHeader("Content-disposition","attachment; filename=\"" + new String("2011年历史数据".getBytes("GB2312"),"ISO-8859-1")+ ".xls\"");
%>
</head>
<body>
<table border="1">
	<tr>
		<td colspan="14" align="center">2011年历史数据</td>
	</tr>
	<tr align="center">
		<td></td>
		<td>月份</td>
		<td>1月</td>
		<td>2月</td>
		<td>3月</td>
		<td>4月</td>
		<td>5月</td>
		<td>6月</td>
		<td>7月</td>
		<td>8月</td>
		<td>9月</td>
		<td>10月</td>
		<td>11月</td>
		<td>12月</td>
	</tr>
	<tr align="center">
		<td></td>
		<td>产品线</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
	<%for(int i=0;i<48;i++){ %>
		<tr align="center">
			<%if(i==0){ %>
				<td rowspan="16"><br/><br/><br/><br/>每<br/>月<br/>采<br/>购<br/>入<br/>库<br/>量</td>
			<%}else if(i==16){ %>
				<td rowspan="16"><br/><br/><br/><br/>每<br/>月<br/>平<br/>均<br/>库<br/>存<br/>量</td>
			<%}else if(i==32){ %>
				<td rowspan="16"><br/><br/><br/><br/>每<br/>月<br/>销<br/>售<br/>出<br/>库<br/>量</td>
			<%} %>
			<%if(i%16==0){ %>
				<td>服装</td>
			<%}else if(i%16==1){ %>
				<td>鞋子</td>
			<%}else if(i%16==2){ %>
				<td>包</td>
			<%}else if(i%16==3){ %>
				<td>护肤品</td>
			<%}else if(i%16==4){ %>
				<td>礼品</td>
			<%}else if(i%16==5){ %>
				<td>新奇特</td>
			<%}else if(i%16==6){ %>
				<td>电脑</td>
			<%}else if(i%16==7){ %>
				<td>手机数码</td>
			<%}else if(i%16==8){ %>
				<td>手机数码配件</td>
			<%}else if(i%16==9){ %>
				<td>保健品/内衣</td>
			<%}else if(i%16==10){ %>
				<td>行货手机</td>
			<%}else if(i%16==11){ %>
				<td>成人日用</td>
			<%}else if(i%16==12){ %>
				<td>小家电</td>
			<%}else if(i%16==13){ %>
				<td>饰品/配饰</td>
			<%}else if(i%16==14){ %>
				<td>鞋配饰</td>
			<%}else if(i%16==15){ %>
				<td>手表</td>
			<%} %>
			<%for(int j=0;j<12;j++){ %>
				<td><%=data[i][j] %></td>
			<%} %>
		</tr>
	<%} %>
</table>
</body>
</html>