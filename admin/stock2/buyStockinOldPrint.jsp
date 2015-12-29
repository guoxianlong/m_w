<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.buy.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="jxl.*" %>
<%@ page import="jxl.format.*" %>
<%@ page import="jxl.format.Alignment" %>
<%@ page import="jxl.write.*" %>
<%@ page import="jxl.write.Number" %>
<%@ page import="java.io.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

response.setContentType("application/vnd.ms-excel");
String path = pageContext.getServletContext().getRealPath("/");

BuyStockinOldAction action = new BuyStockinOldAction();
action.printBuyStockinPrice(request, response);
List productList = (List) request.getAttribute("productList");
List bsipList = (List) request.getAttribute("bsipList");
BuyStockinBean bean = (BuyStockinBean) request.getAttribute("bean");
PrintSettingBean printSetting = (PrintSettingBean)request.getAttribute("printSetting");
String proxyName = (String)request.getAttribute("proxyName");
int i, count;
voProduct product = null;
BuyStockinProductBean bsip = null;
Iterator itr = null;

WorkbookSettings ws = new WorkbookSettings();
ws.setInitialFileSize(512 * 1024);
ws.setGCDisabled(true);
Workbook wbTemplate = Workbook.getWorkbook(new File(path + "/WEB-INF/buyStockTemplate.xls"), ws);
OutputStream targetFile = response.getOutputStream();
WritableWorkbook wwb = Workbook.createWorkbook(targetFile, wbTemplate);
WritableSheet wws = wwb.getSheet("Sheet1");

WritableFont font= new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD);
WritableFont titleFont= new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
NumberFormat format = new NumberFormat("###,##0.00"); //NumberFormat是jxl.write.NumberFormat
NumberFormat iFormat = new NumberFormat("###,##0");
WritableCellFormat cellFormat = new WritableCellFormat(font,format);
WritableCellFormat intFormat = new WritableCellFormat(font,iFormat);
WritableCellFormat noBorderFormat = new WritableCellFormat(font,format);
WritableCellFormat titleFormat = new WritableCellFormat(titleFont,format);
cellFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
titleFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
intFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
titleFormat.setAlignment(Alignment.CENTRE);

// 设置顶端 表标题、时间
Label a1 = (Label)wws.getWritableCell(0,0);
a1.setString("入库单(" + ProductStockBean.getAreaName(bean.getStockArea()) + ")        (" + proxyName + ")");
Label a2 = (Label)wws.getWritableCell(9,0);
a2.setString(bean.getCreateDatetime().substring(0, 10));

//顶部固定一个标题行，所以 lineIndex 从2开始
int lineIndex = 2;
count = productList.size(); // 总数
int half = 0;
int loopCount = count / 20; //有多少个20
if(count % 20 != 0){ // 如果不能被20整除， 说明要多一次循环
	loopCount += 1;
}
int sumCount = 20;
int stockinCount = 0;
float leftSum = 0f; //金额的合计数量，左侧一列
float rightSum = 0f; //金额的合计数量，右侧一列
int leftCount = 0; //数量合计，左侧一列
int rightCount = 0; //数量合计，右侧一列
for(int j=0; j<loopCount; j++){
	// 计算当前循环里面是否有20个商品
	// 如果当前循环有20个商品，则一半就是10
//	if(j < loopCount -1){
		half = 10;
//	} else {
		// 如果当前循环不足20个商品，则一半就是：
//		sumCount = count - (j * 20);
//		half = sumCount / 2 + sumCount % 2;
//	}
	// 商品列表中的索引值：loopIndex
	int loopIndex = j * 20;

	//如果不是第一个大循环，则添加一个表头
	if(j != 0){
		wws.mergeCells(0, lineIndex, 8, lineIndex);
		wws.mergeCells(9, lineIndex, 10, lineIndex);
		Label title1 = new Label(0, lineIndex, "入库单(" + ProductStockBean.getAreaName(bean.getStockArea()) + ")        (" + proxyName + ")");
		Label title2 = new Label(9, lineIndex, bean.getCreateDatetime().substring(0, 10));
		title1.setCellFormat(titleFormat);
		title2.setCellFormat(titleFormat);
		lineIndex++;
		Label content1 = new Label(0, lineIndex, "编号");
		Label content2 = new Label(1, lineIndex, "原名称");
//		Label content3 = new Label(2, lineIndex, "代理商");
		Label content4 = new Label(2, lineIndex, "数量");
		Label content5 = new Label(3, lineIndex, "采购价格");
		Label content6 = new Label(4, lineIndex, "金额");
		Label content7 = new Label(5, lineIndex, " ");
		Label content8 = new Label(6, lineIndex, "编号");
		Label content9 = new Label(7, lineIndex, "原名称");
//		Label content10 = new Label(8, lineIndex, "代理商");
		Label content11 = new Label(8, lineIndex, "数量");
		Label content12 = new Label(9, lineIndex, "采购价格");
		Label content13 = new Label(10, lineIndex, "金额");
		lineIndex++;
		content1.setCellFormat(titleFormat);
		content2.setCellFormat(titleFormat);
//		content3.setCellFormat(titleFormat);
		content4.setCellFormat(titleFormat);
		content5.setCellFormat(titleFormat);
		content6.setCellFormat(titleFormat);
		content7.setCellFormat(titleFormat);
		content8.setCellFormat(titleFormat);
		content9.setCellFormat(titleFormat);
//		content10.setCellFormat(titleFormat);
		content11.setCellFormat(titleFormat);
		content12.setCellFormat(titleFormat);
		content13.setCellFormat(titleFormat);
		wws.addCell(title1);
		wws.addCell(title2);
		wws.addCell(content1);
		wws.addCell(content2);
//		wws.addCell(content3);
		wws.addCell(content4);
		wws.addCell(content5);
		wws.addCell(content6);
		wws.addCell(content7);
		wws.addCell(content8);
		wws.addCell(content9);
//		wws.addCell(content10);
		wws.addCell(content11);
		wws.addCell(content12);
		wws.addCell(content13);
	}

	//出来当前大循环中的商品列表
	for(i = 0; i < half; i ++){
	    Label content1 = null;
		Label content2 = null;
		Label content3 = null;
		Number content4 = null;
		Number content5 = null;
		Number content6 = null;
		Label content7 = null;

		if(loopIndex + i < bsipList.size()){
			bsip = (BuyStockinProductBean) bsipList.get(loopIndex + i);
			product = bsip.getProduct();
		} else {
			bsip = null;
			product = null;
		}
		if(product != null){
		    stockinCount = bsip.getStockInCount();
		    leftSum += bsip.getPrice3() * (stockinCount);
		    leftCount += stockinCount;
		    content1 = new Label(0, lineIndex, String.valueOf(i + 1));
			content2 = new Label(1, lineIndex, bsip.getOriname());
//			content3 = new Label(2, lineIndex, inSh.getStockProduct().getProxyName());
			content4 = new Number(2, lineIndex, stockinCount);
			content5 = null;
			content6 = null;
			//if(group.isFlag(53) && printSetting.getParam("showPrice3") != null && printSetting.getParam("showPrice3").equals("1")){
			if(group.isFlag(53)){
				content5 = new Number(3, lineIndex, bsip.getPrice3());
				content6 = new Number(4, lineIndex, (bsip.getPrice3() * (stockinCount)));
			} else {
				content5 = new Number(3, lineIndex, 0);
				content6 = new Number(4, lineIndex, 0);
			}
			content7 = new Label(5, lineIndex, " ");
			content1.setCellFormat(cellFormat);
			content2.setCellFormat(cellFormat);
//			content3.setCellFormat(cellFormat);
			content4.setCellFormat(intFormat);
			content5.setCellFormat(cellFormat);
			content6.setCellFormat(cellFormat);
			content7.setCellFormat(cellFormat);
			wws.addCell(content1);
			wws.addCell(content2);
//			wws.addCell(content3);
			wws.addCell(content4);
			wws.addCell(content5);
			wws.addCell(content6);
			wws.addCell(content7);
		} else {
			content1 = new Label(0, lineIndex, " ");
			content2 = new Label(1, lineIndex, " ");
//			content3 = new Label(2, lineIndex, " ");
			content4 = new Number(2, lineIndex, 0);
			content5 = new Number(3, lineIndex, 0);
			content6 = new Number(4, lineIndex, 0);
			content7 = new Label(5, lineIndex, " ");
			content1.setCellFormat(cellFormat);
			content2.setCellFormat(cellFormat);
//			content3.setCellFormat(cellFormat);
			content4.setCellFormat(intFormat);
			content5.setCellFormat(cellFormat);
			content6.setCellFormat(cellFormat);
			content7.setCellFormat(cellFormat);
			wws.addCell(content1);
			wws.addCell(content2);
//			wws.addCell(content3);
			wws.addCell(content4);
			wws.addCell(content5);
			wws.addCell(content6);
			wws.addCell(content7);
		}

		if(loopIndex + half + i < bsipList.size()){
			bsip = (BuyStockinProductBean) bsipList.get(loopIndex + half + i);
			product = bsip.getProduct();
		} else {
			product = null;
		}
		if(product != null){
			stockinCount = bsip.getStockInCount();
		    rightSum += bsip.getPrice3() * (stockinCount);
		    rightCount += (stockinCount);

			content1 = new Label(6, lineIndex, String.valueOf(half + i + 1));
			content2 = new Label(7, lineIndex, bsip.getOriname());
//			content3 = new Label(9, lineIndex, bsip.getProxyName());
			content4 = new Number(8, lineIndex, (stockinCount));
//			if(group.isFlag(53) && printSetting.getParam("showPrice3") != null && printSetting.getParam("showPrice3").equals("1")){
			if(group.isFlag(53)){
				content5 = new Number(9, lineIndex, bsip.getPrice3());
				content6 = new Number(10, lineIndex, (bsip.getPrice3() * (stockinCount)));
			} else {
				content5 = new Number(9, lineIndex, 0);
				content6 = new Number(10, lineIndex, 0);
			}
			content7 = new Label(11, lineIndex, " ");
			content1.setCellFormat(cellFormat);
			content2.setCellFormat(cellFormat);
//			content3.setCellFormat(cellFormat);
			content4.setCellFormat(intFormat);
			content5.setCellFormat(cellFormat);
			content6.setCellFormat(cellFormat);
			content7.setCellFormat(cellFormat);
			wws.addCell(content1);
			wws.addCell(content2);
//			wws.addCell(content3);
			wws.addCell(content4);
			wws.addCell(content5);
			wws.addCell(content6);
		} else {
			content1 = new Label(6, lineIndex, " ");
			content2 = new Label(7, lineIndex, " ");
//			content3 = new Label(9, lineIndex, " ");
			content4 = new Number(8, lineIndex, 0);
			content5 = new Number(9, lineIndex, 0);
			content6 = new Number(10, lineIndex, 0);
			content7 = new Label(11, lineIndex, " ");
			content1.setCellFormat(cellFormat);
			content2.setCellFormat(cellFormat);
//			content3.setCellFormat(cellFormat);
			content4.setCellFormat(intFormat);
			content5.setCellFormat(cellFormat);
			content6.setCellFormat(cellFormat);
			wws.addCell(content1);
			wws.addCell(content2);
//			wws.addCell(content3);
			wws.addCell(content4);
			wws.addCell(content5);
			wws.addCell(content6);
		}
		lineIndex++;
	}
	// 每个大循环，都添加一个表底
	{
		Label sum = new Label(0, lineIndex, "合计：");
		//Label content3 = new Label(2, lineIndex, "");
		Label content4 = new Label(3, lineIndex, "");
		Label content5 = new Label(4, lineIndex, "");
		Label content7 = new Label(6, lineIndex, "");
		Label content8 = new Label(7, lineIndex, "");
		//Label content9 = new Label(8, lineIndex, "");
		Label content10 = new Label(9, lineIndex, "");
		Number left = null;
		Number right = null;
		Number total = null;
//		if(group.isFlag(53) && printSetting.getParam("showPrice3") != null && printSetting.getParam("showPrice3").equals("1")){
		if(group.isFlag(53)){
			left = new Number(4, lineIndex, leftSum);
			right = new Number(10, lineIndex, rightSum);
			total = new Number(1, lineIndex, leftSum + rightSum);
		} else {
			left = new Number(4, lineIndex, 0);
			right = new Number(10, lineIndex, 0);
			total = new Number(1, lineIndex, 0);
		}
		Number content3 = new Number(2, lineIndex, leftCount);
		Number content9 = new Number(8, lineIndex, rightCount);
		lineIndex++;
		sum.setCellFormat(cellFormat);
		left.setCellFormat(cellFormat);
		right.setCellFormat(cellFormat);
		total.setCellFormat(cellFormat);
		content3.setCellFormat(intFormat);
		content4.setCellFormat(cellFormat);
		content5.setCellFormat(cellFormat);
		content7.setCellFormat(cellFormat);
		content8.setCellFormat(cellFormat);
		content9.setCellFormat(intFormat);
		content10.setCellFormat(cellFormat);
		wws.addCell(sum);
		wws.addCell(content3);
		wws.addCell(content4);
		wws.addCell(content5);
		wws.addCell(content7);
		wws.addCell(content8);
		wws.addCell(content9);
		wws.addCell(content10);
		wws.addCell(left);
		wws.addCell(right);
		wws.addCell(total);
		leftSum = 0;
		rightSum = 0;
		leftCount = 0;
		rightCount = 0;
		Label footer1 = new Label(0,lineIndex, "采购签字：");
		Label footer2 = new Label(4,lineIndex, "库管签字：");
		Label footer3 = new Label(8,lineIndex, "主管签字：");
		lineIndex++;
		footer1.setCellFormat(noBorderFormat);
		footer2.setCellFormat(noBorderFormat);
		footer3.setCellFormat(noBorderFormat);
		wws.addCell(footer1);
		wws.addCell(footer2);
		wws.addCell(footer3);
	}
	{
		//插入4个空白行
		wws.insertRow(lineIndex);
		lineIndex++;
		wws.insertRow(lineIndex);
		lineIndex++;
		wws.insertRow(lineIndex);
		lineIndex++;
		wws.insertRow(lineIndex);
		lineIndex++;
	}
}
wwb.write();
wwb.close();
wbTemplate.close();
targetFile.close();
return;
%>