<%@page import="adultadmin.action.vo.voCatalog"%>
<%@page import="java.util.HashMap"%>
<%@include file="taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.order.*,adultadmin.bean.buy.*" %>
<%@ page import="adultadmin.bean.afterSales.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%@page isELIgnored="false" %>

<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
 
	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部	
%>

<%@page import="adultadmin.bean.product.ProductCatalogBean"%><html>
<head>
<title>功能树</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!--<script type="text/javascript" src="js/xtree.js"></script>-->
<!--<script type="text/javascript" src="js/xloadtree.js"></script>-->
<script type="text/javascript" src="js/xtree2.js"></script> 
<script type="text/javascript" src="js/xmlextras.js"></script>
<script type="text/javascript" src="js/xloadtree2.js"></script> 
<script type="text/javascript" src="js/js_tree_viewer.js"></script> 

<link type="text/css" rel="stylesheet" href="js/xtree2.css" />
<!--<link type="text/css" rel="stylesheet" href="js/xtree.css" /> -->

<style>
.popup1 {
	cursor: default;
	text-decoration: none;
	color: #000000;
	width: 100px;
	border: 1px solid #ffffff;
}

.popup1 img{
	border:0;
	filter:	Alpha(Opacity=70);
}

.popup1:hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover img{
	border:0;
	filter:	Alpha(Opacity=100);
}

.msviLocalToolbar{
border:solid 1px #999;
background:#F1F1F1;

padding:2px 0px 1px 0px;

}
</style>

<style type="text/css">

body {
	background:	"#F0F0F0";
	color:		black;
	font-size: 18px;
	border-right:1px solid gray;
}

</style>
</head>
<body>
<div id="mainBody" style="display:block; " >
  <fieldset id="moveNodeFieldSet" style="display:none;">
  <legend>Move Node</legend>
	From:<div id="fromNode"></div>
	To:<div id="toNode"></div>
	<input type="button" value="ok" onclick="moveNodeOk();" />
	<input type="button" value="cancel" onclick="moveNodeCancel();"/><br/>
</fieldset>

<div id="menuTree">
<script type="text/javascript">

/// XP Look
webFXTreeConfig.rootIcon				= "js/images/root.gif";
webFXTreeConfig.openRootIcon		= "js/images/xp/openfolder.png";
webFXTreeConfig.folderIcon			= "js/images/xp/folder.png";
webFXTreeConfig.openFolderIcon		= "js/images/xp/openfolder.png";
webFXTreeConfig.fileIcon				= "js/images/xp/file.png";
webFXTreeConfig.lMinusIcon			= "js/images/xp/Lminus.png";
webFXTreeConfig.lPlusIcon				= "js/images/xp/Lplus.png";
webFXTreeConfig.tMinusIcon			= "js/images/xp/Tminus.png";
webFXTreeConfig.tPlusIcon				= "js/images/xp/Tplus.png";
webFXTreeConfig.iIcon					= "js/images/xp/I.png";
webFXTreeConfig.lIcon					= "js/images/xp/L.png";
webFXTreeConfig.tIcon					= "js/images/xp/T.png";
webFXTreeConfig.blankIcon				= "js/images/blank.png";
  
//var tree = new WebFXLoadTree("WebFXLoadTree", "tree1.xml");
//tree.setBehavior("classic");

function insertNode(parentNode,childNode,url, target)
{
	if(target==null)
		childNode.target="mainFrame";
	else
		childNode.target=target;
	childNode.action=url;
	if(parentNode)
		parentNode.add(childNode);
}

/**
 *  Refresh the selected node.刷新节点
 */

var node_root = new WebFXTree("<font size=3><b>买卖宝后台</b></font>");
node_root.openIcon="js/images/root.gif";

<%/******************************************统计信息***************************************/%>
<% 
//SecurityLevel
//10: 超级管理员 9: 高级管理员 5: 普通管理员
//Permission
//10: 超级管理员 9: 高级管理员 8: 平台运维部 7: 销售部 
//6: 商品部 5: 推广部 4: 运营中心 3: 客服部

if(group.isFlag(1)) 
//	if(isSystem		//超级管理员 	
//    || (isXiaoshou || isKefu)	//销售部、客服部的高级管理员
//	|| (isYunyingzhongxin && (isGaojiAdmin || isAdmin))	//运营中心的高级管理员和普通管理员
//	|| (isTuiguang && (isGaojiAdmin || isAdmin))	//推广部的高级管理员和普通管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部的高级管理员和普通管理员	
//)   
{ 
%>




		var node_stat_realtime=new WebFXTreeItem("今日实时统计信息");
		node_root.add(node_stat_realtime);
		<%if(group.isFlag(2)){%>
			<%if(group.isFlag(444)){%>
				var node_stat_order_realtime=new WebFXTreeItem("订单统计");
				insertNode(node_stat_realtime, node_stat_order_realtime, "stat/order.jsp");
			var node_stat_order_realtime2=new WebFXTreeItem("北京");
			insertNode(node_stat_order_realtime, node_stat_order_realtime2, "stat/order.jsp?s=nq");
			var node_stat_order_realtime3=new WebFXTreeItem("无锡");
		
			insertNode(node_stat_order_realtime, node_stat_order_realtime3, "stat/order.jsp?s=q");
			<%}%>
			<%if(group.isFlag(445)){%>
				var node_stat_order_adult_realtime=new WebFXTreeItem("成人/内衣订单统计");
				insertNode(node_stat_realtime, node_stat_order_adult_realtime, "stat/adultorder.jsp");
			<%}%>
			<%if(group.isFlag(446)){%>
				var node_stat_order_phone_realtime=new WebFXTreeItem("手机订单统计");
				insertNode(node_stat_realtime, node_stat_order_phone_realtime, "stat/phoneorder.jsp");
				var node_stat_order_digital_realtime=new WebFXTreeItem("数码订单统计");
				insertNode(node_stat_realtime, node_stat_order_digital_realtime, "stat/digitalorder.jsp");
				var node_stat_order_computer_realtime=new WebFXTreeItem("电脑订单统计");
				insertNode(node_stat_realtime, node_stat_order_computer_realtime, "stat/computerorder.jsp");
			<%}%>
				<%if(group.isFlag(447)){%>
				var node_stat_order_dress_realtime=new WebFXTreeItem("服装订单统计");
				insertNode(node_stat_realtime, node_stat_order_dress_realtime, "stat/dressorder.jsp");
				<%}%>
				<%if(group.isFlag(448)){%>
				var node_stat_order_shoe_realtime=new WebFXTreeItem("鞋子订单统计");
				insertNode(node_stat_realtime, node_stat_order_shoe_realtime, "stat/shoeorder.jsp");
				<%}%>
				<%if(group.isFlag(449)){%>
				var node_stat_order_skinCare_realtime=new WebFXTreeItem("护肤品订单统计");
				insertNode(node_stat_realtime, node_stat_order_skinCare_realtime, "stat/skinCareorder.jsp");
				<%}%>
				<%if(group.isFlag(450)){%>
				var node_stat_order_baoJian_realtime=new WebFXTreeItem("保健品订单统计");
				insertNode(node_stat_realtime, node_stat_order_baoJian_realtime, "stat/baoJianPinOrder.jsp");
				<%}%>
				<%if(group.isFlag(451)){%>
				var node_stat_order_jewelry_realtime=new WebFXTreeItem("饰品订单统计");
				insertNode(node_stat_realtime, node_stat_order_jewelry_realtime, "stat/jewelryorder.jsp");
				<%}%>
				<%if(group.isFlag(446)){%>
				var node_stat_order_hanghuoPhone_realtime=new WebFXTreeItem("行货手机订单统计");
				insertNode(node_stat_realtime, node_stat_order_hanghuoPhone_realtime, "stat/hanghuoPhoneOrder.jsp");
				<%}%>
				<%if(group.isFlag(452)){%>
				var node_stat_order_bao_realtime=new WebFXTreeItem("包类订单统计");
				insertNode(node_stat_realtime, node_stat_order_bao_realtime, "stat/baoOrder.jsp");
				<%}%>
				<%if(group.isFlag(453)){%>
				var node_stat_order_xiaoJiaDian_realtime=new WebFXTreeItem("小家电订单统计");
				insertNode(node_stat_realtime, node_stat_order_xiaoJiaDian_realtime, "stat/xiaoJiaDianOrder.jsp");
					<%}%>
				<%if(group.isFlag(493)){%>
				var node_stat_order_xiaoJiaDian_realtime=new WebFXTreeItem("配饰订单统计");
				insertNode(node_stat_realtime, node_stat_order_xiaoJiaDian_realtime, "stat/peishiOrder.jsp");
					<%}%>
					<%if(group.isFlag(454)){%>
				var node_stat_order_other_realtime=new WebFXTreeItem("其他订单统计");
				insertNode(node_stat_realtime, node_stat_order_other_realtime, "stat/otherorder.jsp");
					<%}%>
					<%if(group.isFlag(455)){%>
				var node_stat_order_watch_realtime=new WebFXTreeItem("手表订单统计");
				insertNode(node_stat_realtime, node_stat_order_watch_realtime, "stat/watchorder.jsp");
				<%}%>
		<%}%>
		<%if(group.isFlag(328)){%>
				var node_stat_sms_order_deal_rate_realtime=new WebFXTreeItem("电话失败发短信成交率");
			    insertNode(node_stat_realtime, node_stat_sms_order_deal_rate_realtime, "stat_nocache/smsDealRate.jsp");	
			    var node_stat_sms_order_message_time_realtime=new WebFXTreeItem("电话失败发短信时间数据");
				insertNode(node_stat_realtime, node_stat_sms_order_message_time_realtime, "stat_nocache/smsOrderMessageTime.jsp");
		<%}%>
		<%if(group.isFlag(2)){%>
				var node_stat_sms_order_states0_order_query=new WebFXTreeItem("未处理订单统计");
				insertNode(node_stat_realtime, node_stat_sms_order_states0_order_query, "stat/status0OrderQuery.jsp");
		<%}%>

		

		var node_stat=new WebFXTreeItem("统计信息");
		node_root.add(node_stat);
	<%if(group.isFlag(2)){%>
		<%if(group.isFlag(444)){%>
			var node_stat_order=new WebFXTreeItem("订单统计");
			insertNode(node_stat, node_stat_order, "stat_cache/order.jsp");
			var node_stat_order2=new WebFXTreeItem("北京");
			insertNode(node_stat_order, node_stat_order2, "stat_cache/order.jsp?s=nq");
			var node_stat_order3=new WebFXTreeItem("无锡");
			insertNode(node_stat_order, node_stat_order3, "stat_cache/order.jsp?s=q");
			<%}%>
			<%if(group.isFlag(445)){%>
			var node_stat_order_adult=new WebFXTreeItem("成人/内衣订单统计");
			insertNode(node_stat, node_stat_order_adult, "stat_cache/adultorder.jsp");
			<%}%>
			<%if(group.isFlag(446)){%>
			var node_stat_order_phone=new WebFXTreeItem("手机订单统计");
			insertNode(node_stat, node_stat_order_phone, "stat_cache/phoneorder.jsp");
			var node_stat_order_digital=new WebFXTreeItem("数码订单统计");
			insertNode(node_stat, node_stat_order_digital, "stat_cache/digitalorder.jsp");
			var node_stat_order_computer=new WebFXTreeItem("电脑订单统计");
			insertNode(node_stat, node_stat_order_computer, "stat_cache/computerorder.jsp");
			<%}%>
			<%if(group.isFlag(447)){%>
			var node_stat_order_dress=new WebFXTreeItem("服装订单统计");
			insertNode(node_stat, node_stat_order_dress, "stat_cache/dressorder.jsp");
			<%}%>
			<%if(group.isFlag(448)){%>
			var node_stat_order_shoe=new WebFXTreeItem("鞋子订单统计");
			insertNode(node_stat, node_stat_order_shoe, "stat_cache/shoeorder.jsp");
			<%}%>
			<%if(group.isFlag(449)){%>
			var node_stat_order_skinCare=new WebFXTreeItem("护肤品订单统计");
			insertNode(node_stat, node_stat_order_skinCare, "stat_cache/skinCareorder.jsp");
				<%}%>
				<%if(group.isFlag(446)){%>		
			var node_stat_order_hanghuoPhone=new WebFXTreeItem("行货手机订单统计");
			insertNode(node_stat, node_stat_order_hanghuoPhone, "stat_cache/hanghuoPhoneOrder.jsp");
			<%}%>
			<%if(group.isFlag(450)){%>
			var node_stat_order_baoJian=new WebFXTreeItem("保健品订单统计");
			insertNode(node_stat, node_stat_order_baoJian, "stat_cache/baoJianPinOrder.jsp");
			<%}%>
			<%if(group.isFlag(451)){%>
			var node_stat_order_jewelry=new WebFXTreeItem("饰品订单统计");
			insertNode(node_stat, node_stat_order_jewelry, "stat_cache/jewelryorder.jsp");
			<%}%>
			<%if(group.isFlag(452)){%>
			var node_stat_order_bao=new WebFXTreeItem("包类订单统计");
			insertNode(node_stat, node_stat_order_bao, "stat_cache/baoOrder.jsp");
				<%}%>
			<%if(group.isFlag(453)){%>	
			var node_stat_order_xiaoJiaDian=new WebFXTreeItem("小家电订单统计");
			insertNode(node_stat, node_stat_order_xiaoJiaDian, "stat_cache/xiaoJiaDianOrder.jsp");	
			<%}%>		
			<%if(group.isFlag(493)){%>	
			var node_stat_order_xiaoJiaDian=new WebFXTreeItem("配饰订单统计");
			insertNode(node_stat, node_stat_order_xiaoJiaDian, "stat_cache/peishiOrder.jsp");	
			<%}%>		
			<%if(group.isFlag(454)){%>
			var node_stat_order_other=new WebFXTreeItem("其他订单统计");
			insertNode(node_stat, node_stat_order_other, "stat_cache/otherorder.jsp");	
				<%}%>
					
			var node_statProductOrder=new WebFXTreeItem("订单分产品线状态占比查询");
			insertNode(node_stat, node_statProductOrder, "stat2/statProductOrder.jsp");
			<%if(group.isFlag(455)){%>
			var node_watch_stat=new WebFXTreeItem("手表订单统计");
			insertNode(node_stat, node_watch_stat, "stat_cache/watchorder.jsp");
			<%}%>
			
	<%}%>
	<%if(group.isFlag(125)){%> 
			var node_dp_product=new WebFXTreeItem("动碰商品统计");
			insertNode(node_stat, node_dp_product, "stat_cache/dpproduct.jsp","_blank");<%}%>
	<%if(group.isFlag(126)){%>
			var node_nodp_product=new WebFXTreeItem("滞销统计");
			insertNode(node_stat, node_nodp_product, "stat_cache/nodpproduct.jsp","_blank");<%}%>
	<%if(group.isFlag(123)) {%>
			var orderStockStatRealTime=new WebFXTreeItem("即时发货状态统计");
			insertNode(node_stat, orderStockStatRealTime, "stat_cache/orderStockStatRealTime.jsp");
	<%}%>
	<%if(group.isFlag(405)){%>
			var productBrand=new WebFXTreeItem("品牌统计");
			insertNode(node_stat, productBrand, "stat/productBrandStat1.jsp");
	<%}%>
	<%if(group.isFlag(15)) //if(isSystem || (isXiaoshou && isGaojiAdmin))
		{%>
			var node_sell_stat=new WebFXTreeItem("实际销售统计");
			insertNode(node_stat, node_sell_stat, "stat/sellStat.jsp");
	<%}%>
	<%if(group.isFlag(4)) //if(!(isTuiguang || isPingtaiyunwei))
		{%>
			var node_stat_order1=new WebFXTreeItem("发货统计");
			insertNode(node_stat, node_stat_order1, "stat_nocache/stockStat.jsp");
	<%}%>
	<%if(group.isFlag(375)) //if(!(isTuiguang || isPingtaiyunwei))
		{%>
			var node_stat_order_fangcun=new WebFXTreeItem("芳村发货统计（按快递公司）");
			insertNode(node_stat, node_stat_order_fangcun, "stockStatFang.do?checkStr=1");
	<%}%>		
	<%if(group.isFlag(5)) //if(!(isTuiguang || (isShangpin && isAdmin) || isPingtaiyunwei))
		{%>
			var node_stat_order_buymode=new WebFXTreeItem("发货分类统计");
			insertNode(node_stat, node_stat_order_buymode, "stat_nocache/stockStatBuymode.jsp");
	<%}%>
	<%if(group.isFlag(25)){%>
		    var node_stat_stock=new WebFXTreeItem("所有商品库存统计");
			insertNode(node_stat, node_stat_stock, "stat_nocache/allProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(329)){%>
		    var node_stat_stock=new WebFXTreeItem("成人用品库存统计");
			insertNode(node_stat, node_stat_stock, "stat_nocache/productStockStat.jsp");
	<%}%>
	<%if(group.isFlag(330)){%>
			var node_stat_stock_sj=new WebFXTreeItem("手机商品库存统计");
			insertNode(node_stat, node_stat_stock_sj, "stat_nocache/mobileProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(331)){%>
			var node_stat_stock_dress=new WebFXTreeItem("服装库存统计");
			insertNode(node_stat, node_stat_stock_dress, "stat_nocache/dressProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(332)){%>
			var node_stat_stock_shoe=new WebFXTreeItem("鞋子库存统计");
			insertNode(node_stat, node_stat_stock_shoe, "stat_nocache/shoeProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(333)){%>
			var node_stat_stock_skinCare=new WebFXTreeItem("护肤品库存统计");
			insertNode(node_stat, node_stat_stock_skinCare, "stat_nocache/skinCareProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(334)){%>
			var node_stat_stock_dress=new WebFXTreeItem("数码商品库存统计");
			insertNode(node_stat, node_stat_stock_dress, "stat_nocache/digitalProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(335)){%>
			var node_stat_stock_computer=new WebFXTreeItem("电脑商品库存统计");
			insertNode(node_stat, node_stat_stock_computer, "stat_nocache/computerProductStockStat.jsp");
	<%}%>
	<%if(group.isFlag(369)){%>
	        var node_stat_stock_bao=new WebFXTreeItem("包类库存统计");
	        insertNode(node_stat, node_stat_stock_bao, "stat_nocache/baoProductStockStat.jsp");
    <%}%>
    <%if(group.isFlag(370)){%>
	        var node_stat_stock_xiaoJiaDian=new WebFXTreeItem("小家电库存统计");
	        insertNode(node_stat, node_stat_stock_xiaoJiaDian, "stat_nocache/xiaoJiaDianProductStockStat.jsp");
    <%}%>
	
<%if(group.isFlag(32)){ %>
		var node_stato=new WebFXTreeItem("订单成交退单率");
		node_stat.add(node_stato);
			var node_stat_po1=new WebFXTreeItem("最近订单成交率");
			insertNode(node_stato, node_stat_po1, "personOderCount.do?in=4&type=1");
			var node_stat_po5=new WebFXTreeItem("最近发货退单率");
			insertNode(node_stato, node_stat_po5, "personOderCount.do?in=5&type=2");
			var node_stat_po2=new WebFXTreeItem("个人订单月成交率");
			insertNode(node_stato, node_stat_po2, "personOderCount.do?in=4&type=3");
			var node_stat_po3=new WebFXTreeItem("销售|月退单率");
			insertNode(node_stato, node_stat_po3, "personOderCount.do?in=4&type=4");
			var node_stat_po4=new WebFXTreeItem("发货人|月退单率");
			insertNode(node_stato, node_stat_po4, "personOderCount.do?in=5&type=5");
	<%}%>
	<%if(group.isFlag(328))
		{%>	
			var node_stat_sms_order_deal_rate=new WebFXTreeItem("电话失败发短信成交率");
			insertNode(node_stat, node_stat_sms_order_deal_rate, "stat_cache/smsDealRate.jsp");
			var node_stat_sms_order_message_time=new WebFXTreeItem("电话失败发短信时间数据");
			insertNode(node_stat, node_stat_sms_order_message_time, "stat_cache/smsOrderMessageTime.jsp");						
	<%}%>
	<%if(group.isFlag(42)){ %>
			var node_stat_forum=new WebFXTreeItem("论坛发帖统计");
			insertNode(node_stat, node_stat_forum, "stat/forumStat.jsp");
	<%}%>
	<%if(group.isFlag(371)&&false){%>
	var node_stat_supplier_backtrack = new WebFXTreeItem("供应商返厂记录汇总");
	insertNode(node_stat, node_stat_supplier_backtrack, "supplierSummarizing.do?method=supplierBacktrackRecall&type=1");
	<%}if(group.isFlag(372)){%>
	var node_stat_supplier_restitution = new WebFXTreeItem("供应商退货记录汇总");
	insertNode(node_stat, node_stat_supplier_restitution, "supplierSummarizing.do?method=supplierBacktrackRecall&type=2");
	<%}if(group.isFlag(373)){%>
	var node_stat_supplier_trade = new WebFXTreeItem("供应商交易汇总");
	insertNode(node_stat, node_stat_supplier_trade, "supplierSummarizing.do?method=supplierTradeCollect");
	<%}%>
	<%if(group.isFlag(396)){%>
	var node_stat_checkorder=new WebFXTreeItem("复核量统计");
	node_stat.add(node_stat_checkorder);
		var node_stat_checkorder_date=new WebFXTreeItem("每日发货复核量统计");
		insertNode(node_stat_checkorder, node_stat_checkorder_date, "checkOrderStat.do?method=checkOrderStatByDate");
		var node_stat_checkorder_hour=new WebFXTreeItem("每小时发货复核量统计");
		insertNode(node_stat_checkorder, node_stat_checkorder_hour, "checkOrderStat.do?method=checkOrderStatByHour");
		var node_stat_checkorder_name=new WebFXTreeItem("个人发货复核量统计");
		insertNode(node_stat_checkorder, node_stat_checkorder_name, "checkOrderStat.do?method=checkOrderStatByName");
	<%}%>
<%}%>
<%if(group.isFlag(298)) {%>
var node_stat_product_line=new WebFXTreeItem("产品线销售统计");
node_root.add(node_stat_product_line);
<%if(group.isFlag(299)) {%>
var node_spl_brand_shoes=new WebFXTreeItem("品牌鞋销售统计");
insertNode(node_stat_product_line, node_spl_brand_shoes, "stat/productLineSellStat123.jsp");
<%}%>
<%if(group.isFlag(358)){%>
var node_spl_brand_phone=new WebFXTreeItem("品牌手机销售统计");
insertNode(node_stat_product_line, node_spl_brand_phone, "orderSale.do?method=brandPhoneAcount&firstIn=first&firstCheck=1");

var node_spl_price1_phone=new WebFXTreeItem("品牌手机价格段销售统计(1)");
insertNode(node_stat_product_line, node_spl_price1_phone, "orderSale.do?method=phoneAcountByPrice&firstIn=first");

var node_spl_price2_phone=new WebFXTreeItem("品牌手机价格段销售统计(2)");
insertNode(node_stat_product_line, node_spl_price2_phone, "orderSale.do?method=phoneAcountByPrice2&firstIn=first");
<%}%>
<%}%>
<%if(group.isFlag(262)) {%>
var node_stat2=new WebFXTreeItem("订单统计");
node_root.add(node_stat2);
	var node_stat2_order=new WebFXTreeItem("订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order");
	var node_stat2_order=new WebFXTreeItem("成人/内衣订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=1");
	var node_stat2_order=new WebFXTreeItem("手机订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=2");
	var node_stat2_order=new WebFXTreeItem("数码订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=3");
	var node_stat2_order=new WebFXTreeItem("电脑订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=4");
	var node_stat2_order=new WebFXTreeItem("服装订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=5");
	var node_stat2_order=new WebFXTreeItem("鞋子订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=6");
	var node_stat2_order=new WebFXTreeItem("护肤品订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=7");
	var node_stat2_order=new WebFXTreeItem("保健品及其他订单统计");
	insertNode(node_stat2, node_stat2_order, "stat2/orderStat.jsp?tableName=user_order&type=8");
	var node_stat2_order=new WebFXTreeItem("成交订单及销售额综合查询");
	insertNode(node_stat2, node_stat2_order, "stat2/statOrder.jsp");

<%}%>

<%/***************************************池子管理******************************************/%>

<%
if(group.isFlag(22)) 
//	if(isSystem	//系统管理员
//    || (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| isPingtaiyunwei	//平台运维部
//)
{%>
		var node_pool=new WebFXTreeItem("池子管理");
		insertNode(node_root,node_pool, "searchFpool.jsp");
			var node_new_pool=new WebFXTreeItem("添加池子");
			insertNode(node_pool, node_new_pool, "findTypePoolTwo.do?type=0");
			var node_new_pool_two=new WebFXTreeItem("添加池子二级分类");
			insertNode(node_pool, node_new_pool_two, "addPoolTwo.jsp");
			
			var pool_products_root=new WebFXLoadTreeItem("产品池子","fAjaxPoolTwo.do?type=1&parantId=0");
			insertNode(node_pool,pool_products_root,"findPoolTwos.do?action=search&type=1&parantId=0");
			//var node_new_poolproduct=new WebFXTreeItem("添加池子产品");
			//insertNode(pool_products_root, node_new_poolproduct, "fpoolproduct.do");

			var node_pool_article=new WebFXLoadTreeItem("文章池子","fAjaxPoolTwo.do?type=2&parantId=0");
			insertNode(node_pool,node_pool_article,"findPoolTwos.do?action=search&type=2&parantId=0");
			//var node_new_poolarticle=new WebFXTreeItem("添加池子文章");
			//insertNode(node_pool_article, node_new_poolarticle, "fpoolarticle.do");
			

			var node_pool_common=new WebFXLoadTreeItem("普通池子","fAjaxPoolTwo.do?type=0&parantId=0");
			insertNode(node_pool,node_pool_common,"findPoolTwos.do?action=search&type=0&parantId=0");
			//var node_new_poolinfo=new WebFXTreeItem("添加池子链接");
			//insertNode(node_pool_common, node_new_poolinfo, "fpoollink.do");

			
			var node_pool_parse=new WebFXLoadTreeItem("产品解析池子","fAjaxPoolTwo.do?type=3&parantId=0");
			insertNode(node_pool,node_pool_parse,"findPoolTwos.do?action=search&type=3&parantId=0");
			//var node_new_parsepoolinfo=new WebFXTreeItem("添加池子链接");
			//insertNode(node_pool_parse, node_new_parsepoolinfo, "fParsePoolInfo.do");

			var node_pool_Clothing=new WebFXLoadTreeItem("服装鞋普通池子","fAjaxPoolTwo.do?type=4&parantId=0");
			insertNode(node_pool,node_pool_Clothing,"findPoolTwos.do?action=search&type=4&parantId=0");
			//var node_new_ClothingInfo=new WebFXTreeItem("添加池子链接");
			//insertNode(node_pool_Clothing, node_new_ClothingInfo, "new_ClothingInfo.do");

			var node_pool_ClothingParse=new WebFXLoadTreeItem("服装鞋产品解析池子","fAjaxPoolTwo.do?type=5&parantId=0");
			insertNode(node_pool,node_pool_ClothingParse,"findPoolTwos.do?action=search&type=5&parantId=0");
			//var node_new_ClothingParseinfo=new WebFXTreeItem("添加池子链接");
			//insertNode(node_pool_ClothingParse, node_new_ClothingParseinfo, "new_ClothingParse.do");

<%}%>

<%/******************************************商品库管理********************************************/%>
<%
if(group.isFlag(253)) {
%>
           		var product_library=new WebFXTreeItem("商品库管理");
		        node_root.add(product_library);
 
		       	<%if(group.isFlag(456)){%>
		       		var node_product_catalog=new WebFXTreeItem("商品分类管理");
			    	insertNode(product_library,node_product_catalog,"productCatalog.do?method=productCatalogList&type=1");
	           		var node_all_uploadproduct1=new WebFXTreeItem("商品品牌管理");
	           		insertNode(product_library, node_all_uploadproduct1, "productBrand.do?method=productBrandList");
		       	<%}%>
			    
			    <%if(group.isFlag(19)){%>
			    		var node_product_add=new WebFXTreeItem("商品录入");
			    		insertNode(product_library,node_product_add,"#");	
			    		var node_newproduct_add=new WebFXTreeItem("新品添加");
			    		insertNode(node_product_add,node_newproduct_add,"addProductInfo.do?method=getProductOtherInfos");
			    		var node_product_search=new WebFXTreeItem("商品搜索");
			    		insertNode(node_product_add,node_product_search,"productSearchByShop.do?method=searchProduct");
			    		var node_search_product=new WebFXTreeItem("产品查询");
				        insertNode(node_product_add, node_search_product, "searchproduct.do");
			    <%}%>
				
				<%if(group.isFlag(305)){%>
						    var node_product_make=new WebFXTreeItem("商品管理");
						    insertNode(product_library,node_product_make,"#");	
						    		    
						    var node_product_list=new WebFXTreeItem("商品列表");
						    insertNode(node_product_make,node_product_list,"productSearchByZhiZuo.do?method=searchProduct");
						    var node_search_product=new WebFXTreeItem("产品查询");
				            insertNode(node_product_make, node_search_product, "searchproduct.do");
				<%}%>
	<% } %>			
<%/****************************************商品库管理    结束*****************************************/%>

<%/****************************************产品管理*****************************************/%>

<%
if(group.isFlag(253)) 
//	if(isSystem	//超级管理员
//    || ((isXiaoshou || isKefu) && (isAdmin || isGaojiAdmin))	//销售、客服部的普通管理员以上
//	|| (isYunyingzhongxin && (isGaojiAdmin || isAdmin))	//运营中心的高级管理员和普通管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//)
{
%>
		var product_0=new WebFXTreeItem("产品管理");
		node_root.add(product_0);
<%
if(group.isFlag(0)) //if(isSystem)
{	//只对超级管理员开放
%>
			var node_all_uploadproduct=new WebFXTreeItem("导入产品信息");
			insertNode(product_0, node_all_uploadproduct, "fuploadProduct.do");
<%}
%>
<%--
if(group.isFlag(51))
{
%>
			var node_export_badrepair = new WebFXTreeItem("导出坏的和已返修产品");
			insertNode(product_0, node_export_badrepair, "exportstockproducts.do?type=1", "_blank");
<%}
--%>
<%--
if(group.isFlag(25)) //if(isSystem || (isYunyingzhongxin && isGaojiAdmin) || (isShangpin && isGaojiAdmin))
{	//商品部、运营中心，高级管理员
%>
		    var node_all_product_stock=new WebFXTreeItem("所有产品库存");
			insertNode(product_0, node_all_product_stock, "allProductsStock.do");
		    var node_all_product=new WebFXTreeItem("所有在架上产品");
			insertNode(product_0, node_all_product, "allProducts.do");
			var node_all_product1=new WebFXTreeItem("所有已下架产品");
			insertNode(product_0, node_all_product1, "allProducts.do?status=120");
<%
    }
--%>
<%
if(group.isFlag(19)) //if(isSystem || isYunyingzhongxin || isPingtaiyunwei || (isShangpin && isGaojiAdmin))
{	//销售、客服部，不开放，运营中心、平台运维，开放
%>
			var node_new_product=new WebFXTreeItem("添加新产品");
			insertNode(product_0, node_new_product, "fproduct.do");
<%
}
%>
<%
if(group.isFlag(19)) //if(isSystem || isYunyingzhongxin || isPingtaiyunwei || (isShangpin && isGaojiAdmin))
{	//销售、客服部，不开放，运营中心、平台运维，开放
%>
			var node_product_relation=new WebFXTreeItem("管理商品父子关系");
			insertNode(product_0, node_product_relation, "productRelationList.do");
<%
}
%>
<%if(group.isFlag(304)){%>
	var node_product_relation=new WebFXTreeItem("批量设定产品可发货量");
	insertNode(product_0, node_product_relation, "./productProperty/qcbsProductProperty.jsp");
	var node_product_qcbs_param=new WebFXTreeItem("产品自动上架参数设置");
	insertNode(product_0, node_product_qcbs_param, "./productProperty/qcbsParamList.jsp");
	var node_product_qcbs_log=new WebFXTreeItem("产品自动上架记录查询");
	insertNode(product_0, node_product_qcbs_log, "./productProperty/qcbsLogList.jsp");
<%}%>
<%if(group.isFlag(305)){%>
var node_product_property=new WebFXTreeItem("产品属性管理");
product_0.add(node_product_property);
var node_product_color=new WebFXTreeItem("产品颜色管理");
insertNode(node_product_property, node_product_color, "productcolor.do?method=productColor");
var node_product_size1=new WebFXTreeItem("服装尺码管理");
insertNode(node_product_property, node_product_size1, "productProperty.do?method=clothesSize");
var node_product_size2=new WebFXTreeItem("鞋尺码管理");
insertNode(node_product_property, node_product_size2, "productProperty.do?method=shoesSize");
var node_product_brand=new WebFXTreeItem("产品品牌管理");
insertNode(node_product_property,node_product_brand,"productbrand.do?method=productBrand");
<%}%>
var node_product_catalog=new WebFXLoadTreeItem("产品分类","tree/productCatalog.jsp");
insertNode(product_0,node_product_catalog,"productbrand.do?method=productBrand");
<%}%>
<%
if(group.isFlag(495)) //if(isSystem || isYunyingzhongxin || isPingtaiyunwei || (isShangpin && isGaojiAdmin))
{	//销售、客服部，不开放，运营中心、平台运维，开放
%>
	var product_book=new WebFXTreeItem("客户关注");
    insertNode(product_0,product_book,"productBook.do?method=productBookList");
<%
}
%>
<%if(group.isFlag(326)) {%>
var product_discount=new WebFXTreeItem("产品优惠价管理");
node_root.add(product_discount);
var node_product_discount_list=new WebFXTreeItem("优惠活动列表");
insertNode(product_discount, node_product_discount_list, "productProperty/productDiscountList.jsp");
var node_product_discount_price_add=new WebFXTreeItem("添加产品优惠价");
insertNode(product_discount, node_product_discount_price_add, "productProperty/editProductDiscountPrice.jsp");
var node_product_discount_price_list=new WebFXTreeItem("产品优惠价列表");
insertNode(product_discount, node_product_discount_price_list, "productProperty/productDiscountPriceList.jsp");
<%}%>
<%if(group.isFlag(390)) {%>
var node_product_preference=new WebFXTreeItem("优惠管理");
node_root.add(node_product_preference);
<%if(group.isFlag(391)) {%>
var node_product_preference_manjian=new WebFXTreeItem("满减折扣管理");
node_product_preference.add(node_product_preference_manjian);
var manjian_add=new WebFXTreeItem("满减折扣制定");
insertNode(node_product_preference_manjian, manjian_add, "productPreference.do?method=addProductPreference");
var manjian_search=new WebFXTreeItem("满减折扣查询");
insertNode(node_product_preference_manjian, manjian_search, "productPreference.do?method=searchProductPreference");
<%}%>
<%if(group.isFlag(391)) {%>
var node_money_present=new WebFXTreeItem("现金劵赠送活动管理");
insertNode(node_product_preference, node_money_present, "cashTicketInfo.do?method=queryCashTicket");
<%}%>
<%}%>
<%if(group.isFlag(324)) {%>
var new_productBarcode=new WebFXTreeItem("商品条码管理");
node_root.add(new_productBarcode);
<%if(group.isFlag(292)){%>
var node_product_barcode=new WebFXTreeItem("修改商品条码");
insertNode(new_productBarcode, node_product_barcode, "barcodeManager/productBarcodes.jsp");
<%}%>
<%}%>
<%/*********************************代理商管理**************************************/%>
<%if(group.isFlag(88)) {%>
var new_proxy=new WebFXTreeItem("代理商管理");
node_root.add(new_proxy);
var new_proxy_0=new WebFXTreeItem("代理商管理");
insertNode(new_proxy,new_proxy_0,"proxyInfo.jsp");
var new_proxy_1=new WebFXTreeItem("账号绑定代理商");
insertNode(new_proxy,new_proxy_1,"proxyBind.jsp");
<%}%>


<%/*********************************赠品管理**************************************/%>
<%if(group.isFlag(234)) {%>
	var present_manage = new WebFXTreeItem("赠品管理");
	node_root.add(present_manage);
	
	var present_manage_0=new WebFXTreeItem("赠品列表");
	insertNode(present_manage,present_manage_0,"presentProduct.do?method=search");
	
	<%if(group.isFlag(408)){%>//改成408
			var batch_persent_product=new WebFXTreeItem("批量添加/删除赠品");
			insertNode(present_manage, batch_persent_product, "batchDoPersent.jsp");	
	<%}%>
<%}%>

<%if(group.isFlag(234)) {%>
var present_manage_0=new WebFXTreeItem("自动发放赠品管理");
insertNode(node_root,present_manage_0,"autoPresent.do?method=search");
<%}%>


<%if(group.isFlag(243)) {%>
var present_manage_1=new WebFXTreeItem("秒杀管理");
insertNode(node_root,present_manage_1,"seckill.do?method=search");
<%}%>


<%/***************************************文章管理******************************************/%>
<%if(false&&group.isFlag(22)) 
//	if(isSystem	//系统管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{%>
		var article_0=new WebFXTreeItem("文章管理");
		node_root.add(article_0);
				var node_new_article=new WebFXTreeItem("添加新文章");
				insertNode(article_0, node_new_article, "farticle.do");
		
<logic:present name="catalogList" scope="request">
<logic:iterate id="element" name="catalogList">
<logic:equal name="element" property="hide" value="0">
		var article_<bean:write name="element" property="id" />=new WebFXTreeItem("<bean:write name="element" property="name" />");
		insertNode(article_<bean:write name="element" property="parentId" />,article_<bean:write name="element" property="id" />,"articles.do?catalogId=<bean:write name="element" property="id" />");
</logic:equal>
</logic:iterate>
</logic:present>
<%}%>

<%/***************************************资讯平台******************************************/%>

<%if(group.isFlag(22)) 
//	if(isSystem	//系统管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{%>
		var new_article_0=new WebFXLoadTreeItem("资讯平台","tree/articleCatalog.jsp");
		node_root.add(new_article_0);
<%}%>

<%/***************************************视频管理******************************************/%>

<%if(false&&group.isFlag(22)) 
//	if(isSystem	//系统管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{%>
		var video_0=new WebFXTreeItem("视频管理");
		node_root.add(video_0);

<logic:present name="catalogList" scope="request">
<logic:iterate id="element" name="catalogList">
		var video_<bean:write name="element" property="id" />=new WebFXTreeItem("<bean:write name="element" property="name" />");
		insertNode(video_<bean:write name="element" property="parentId" />,video_<bean:write name="element" property="id" />,"videos.do?catalogId=<bean:write name="element" property="id" />");
</logic:iterate>
</logic:present>
<%}%>

<%/****************************************广告管理*****************************************/%>
<%--
<%if(group.isFlag(22)) 
//	if(isSystem	//系统管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//推广部的普通管理员和高级管理员
//)
{%>
		var advertisement_0=new WebFXTreeItem("广告管理");
		node_root.add(advertisement_0);
		var advertisement_1=new WebFXTreeItem("添加广告");
		insertNode(advertisement_0,advertisement_1,"fadvertisement.do");
		var advertisement_2=new WebFXTreeItem("广告列表");
		insertNode(advertisement_0,advertisement_2,"advertisements.do");
<%}%>
--%>
<%/******************************************黑名单管理***************************************/%>

<%
if(group.isFlag(10)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
<%--
		var dealPhoneLog_0=new WebFXTreeItem("电话记录管理");
		node_root.add(dealPhoneLog_0);
		var node_new_dealPhoneLog=new WebFXTreeItem("添加新电话记录");
		insertNode(dealPhoneLog_0, node_new_dealPhoneLog, "fdealPhoneLog.do");
		var dealPhoneLog_list=new WebFXTreeItem("电话记录列表");
		insertNode(dealPhoneLog_0,dealPhoneLog_list,"dealPhoneLogs.do");
--%>
        var blackList_0=new WebFXTreeItem("黑名单管理");
		node_root.add(blackList_0);
		var node_new_blackList=new WebFXTreeItem("添加黑名单");
		insertNode(blackList_0, node_new_blackList, "fBlackList.do");
		var node_black_list=new WebFXTreeItem("黑名单列表");
		insertNode(blackList_0,node_black_list,"blackList.do");
		var node_black_search=new WebFXTreeItem("查询黑名单");
		insertNode(blackList_0,node_black_search,"blackList.do#search");
<%}%>

<%/******************************************公告管理***************************************/%>

<%if(group.isFlag(23)) 
//	if(isSystem	//系统管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部的高级管理员
//)
{%>
		var bulletin_0=new WebFXTreeItem("公告管理");
		node_root.add(bulletin_0);
		var bulletin_1=new WebFXTreeItem("添加公告");
		insertNode(bulletin_0,bulletin_1,"fbulletin.do");
		var bulletin_2=new WebFXTreeItem("公告列表");
		insertNode(bulletin_0,bulletin_2,"bulletins.do");

		var message2all_0=new WebFXTreeItem("全站信件管理");
		node_root.add(message2all_0);
		var message2all_1=new WebFXTreeItem("添加全站信件");
		insertNode(message2all_0,message2all_1,"fMessage2All.do");
		var message2all_2=new WebFXTreeItem("全站信件列表");
		insertNode(message2all_0,message2all_2,"message2Alls.do");
<%}%>

<%/*****************************************封禁列表管理****************************************/%>

<%
if(group.isFlag(10)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
		var forbid_0=new WebFXTreeItem("封禁列表管理");
		node_root.add(forbid_0);

		var forbid_1=new WebFXTreeItem("添加封禁");
		insertNode(forbid_0,forbid_1,"fforbid.do");

		var forbid_2=new WebFXTreeItem("封禁列表");
		insertNode(forbid_0,forbid_2,"forbidlist.do");
<%}%>

<%/*************************************用户管理********************************************/%>

<%
if(group.isFlag(12)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部的高级管理员
//)
{
%>
		var user_0=new WebFXTreeItem("用户管理");
		node_root.add(user_0);
		var user_1=new WebFXTreeItem("添加用户");
		insertNode(user_0,user_1,"fuser.do");
<%if(group.isFlag(0)) //if(isSystem)
{%>
		var user_2=new WebFXTreeItem("用户列表");
		insertNode(user_0,user_2,"users.do");
<%}%>
		var user_3=new WebFXTreeItem("普通会员列表");
		insertNode(user_0,user_3,"users.do?rank=1");
		var user_4=new WebFXTreeItem("VIP会员列表");
		insertNode(user_0,user_4,"users.do?rank=2");
		var node_search_user=new WebFXTreeItem("用户查询");
		insertNode(user_0, node_search_user, "searchuser.do");
<%--
		var node_point=new WebFXTreeItem("导入积分");
		insertNode(user_0, node_point, "importPoint.jsp");
--%>
<%}%>

<%/****************************************打折卡管理*****************************************/%>

<%
if(group.isFlag(12)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部的高级管理员
//)
{
%>
		var card_0=new WebFXTreeItem("打折卡管理");
		node_root.add(card_0);
		var card_1=new WebFXTreeItem("添加打折卡");
		insertNode(card_0,card_1,"fusercard.do");
		var card_2=new WebFXTreeItem("用户打折卡列表");
		insertNode(card_0,card_2,"usercards.do");
		var card_2=new WebFXTreeItem("卡历史纪录列表");
		insertNode(card_0,card_2,"cardhistory.do");
		var node_search_card=new WebFXTreeItem("用户打折卡查询");
		insertNode(card_0, node_search_card, "searchusercard.do");
		var node_test_card=new WebFXTreeItem("调查所得优惠卡查询");
		insertNode(card_0, node_test_card, "card/searchCard.jsp");
		<%if(group.isFlag(272)){%>
		var node_voucher1=new WebFXTreeItem("添加代金券");
		insertNode(card_0, node_voucher1, "card/addVoucher.jsp");
		var node_voucher2=new WebFXTreeItem("代金券列表");
		insertNode(card_0, node_voucher2, "card/voucherList.jsp");
		var node_voucher3=new WebFXTreeItem("代金券使用统计");
		insertNode(card_0, node_voucher3, "card/voucherStat.jsp");
		<%}%>
<%}%>

<%/****************************************客户回访管理*****************************************/%>
<%
if(group.isFlag(16)) 
//	if(isSystem	//超级管理员
//	|| isKefu	//销售部
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部高级管理员
//	|| (isXiaoshou && isGaojiAdmin)	//销售部高级管理员
//)
{
%>
		var track_0=new WebFXTreeItem("客户回访管理");
		node_root.add(track_0);
		var track_1=new WebFXTreeItem("客户回访列表");
		insertNode(track_0,track_1,"userTracks.do");
<%
if(group.isFlag(27)) 
//	if(isSystem	//超级管理员
//	|| (isKefu && isGaojiAdmin)	//销售部
//)
{
%>
		var no_track_user=new WebFXTreeItem("未分配客户列表");
		insertNode(track_0,no_track_user,"noTrackUsers.do");
<%}%>
		var track_users=new WebFXTreeItem("普通用户");
		insertNode(track_0,track_users,"userTracks.do?rank=0");
		track_users=new WebFXTreeItem("普通会员");
		insertNode(track_0,track_users,"userTracks.do?rank=1");
		track_users=new WebFXTreeItem("VIP会员");
		insertNode(track_0,track_users,"userTracks.do?rank=2");
		track_users=new WebFXTreeItem("活跃客户");
		insertNode(track_0,track_users,"userTracks.do?activity=2");
		track_users=new WebFXTreeItem("一般客户");
		insertNode(track_0,track_users,"userTracks.do?activity=1");
		track_users=new WebFXTreeItem("无效客户");
		insertNode(track_0,track_users,"userTracks.do?activity=0");
<%}%>

<%-- 老用户 打折 优惠 ， 商品折扣设置 --%>
<%if(group.isFlag(196)){%>
		var frequentUser = new WebFXTreeItem("老用户优惠设置");
		node_root.add(frequentUser);
		<%if(group.isFlag(197)){%>
			var catalogDiscount = new WebFXTreeItem("商品折扣管理");
			insertNode(frequentUser,catalogDiscount,"frequent/CatalogDiscount.do?method=list");
		<%}%>
<%}%>

<%/****************************************论坛管理*****************************************/%>

<%
if(group.isFlag(10)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
		var forumMessage_0=new WebFXTreeItem("论坛管理");
		node_root.add(forumMessage_0);
		var forumMessage_1=new WebFXTreeItem("添加常见购物问题帖子");
		insertNode(forumMessage_0,forumMessage_1,"fforumMessage.do?forumIds=1");
		var forumMessage_2=new WebFXTreeItem("常见购物问题帖子列表");
		insertNode(forumMessage_0,forumMessage_2,"forumMessages.do?forumIds=1");
		var forumMessage_3=new WebFXTreeItem("添加求医问药帖子");
		insertNode(forumMessage_0,forumMessage_3,"fforumMessage.do?forumIds=2");
		var forumMessage_4=new WebFXTreeItem("求医问药帖子列表");
		insertNode(forumMessage_0,forumMessage_4,"forumMessages.do?forumIds=2");
		var forumMessage_5=new WebFXTreeItem("添加手机咨询帖子");
		insertNode(forumMessage_0,forumMessage_5,"fforumMessage.do?forumIds=3");
		var forumMessage_6=new WebFXTreeItem("手机咨询帖子列表");
		insertNode(forumMessage_0,forumMessage_6,"forumMessages.do?forumIds=3");
		var forumMessage_7=new WebFXTreeItem("添加售后服务专区帖子");
		insertNode(forumMessage_0,forumMessage_7,"fforumMessage.do?forumIds=4");
		var forumMessage_8=new WebFXTreeItem("售后服务专区帖子列表");
		insertNode(forumMessage_0,forumMessage_8,"forumMessages.do?forumIds=4");
		var forumMessage_9=new WebFXTreeItem("添加咨询性用品帖子");
		insertNode(forumMessage_0,forumMessage_9,"fforumMessage.do?forumIds=5");
		var forumMessage_10=new WebFXTreeItem("咨询性用品帖子列表");
		insertNode(forumMessage_0,forumMessage_10,"forumMessages.do?forumIds=5");
		var forumMessage_11=new WebFXTreeItem("添加购衣咨询帖子");
		insertNode(forumMessage_0,forumMessage_11,"fforumMessage.do?forumIds=6");
		var forumMessage_12=new WebFXTreeItem("购衣咨询帖子列表");
		insertNode(forumMessage_0,forumMessage_12,"forumMessages.do?forumIds=6");
		var forumMessage_13=new WebFXTreeItem("添加电脑咨询帖子");
		insertNode(forumMessage_0,forumMessage_13,"fforumMessage.do?forumIds=7");
		var forumMessage_14=new WebFXTreeItem("电脑咨询帖子列表");
		insertNode(forumMessage_0,forumMessage_14,"forumMessages.do?forumIds=7");
		var forumMessage_15=new WebFXTreeItem("添加护肤咨询帖子");
		insertNode(forumMessage_0,forumMessage_15,"fforumMessage.do?forumIds=8");
		var forumMessage_16=new WebFXTreeItem("护肤咨询帖子列表");
		insertNode(forumMessage_0,forumMessage_16,"forumMessages.do?forumIds=8");
		var forumMessage_17=new WebFXTreeItem("添加钱包支付与充值帖子");
		insertNode(forumMessage_0,forumMessage_17,"fforumMessage.do?forumIds=9");
		var forumMessage_18=new WebFXTreeItem("钱包支付与充值帖子列表");
		insertNode(forumMessage_0,forumMessage_18,"forumMessages.do?forumIds=9");
		var forumMessage_19=new WebFXTreeItem("添加彩票俱乐部帖子");
		insertNode(forumMessage_0,forumMessage_19,"fforumMessage.do?forumIds=10");
		var forumMessage_20=new WebFXTreeItem("彩票俱乐部帖子列表");
		insertNode(forumMessage_0,forumMessage_20,"forumMessages.do?forumIds=10");
<%}%>

<%/**************************************产品评论管理*******************************************/%>

<%
if(group.isFlag(11)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员	
//)
{
%>
		var comment_0=new WebFXTreeItem("产品评论管理");
		node_root.add(comment_0);
		var comment_2=new WebFXTreeItem("成人用品评论列表");
		insertNode(comment_0,comment_2,"comments.do?type=0");
		var comment_5=new WebFXTreeItem("手机商品评论列表");
		insertNode(comment_0,comment_5,"comments.do?type=1");
		var comment_5=new WebFXTreeItem("随机评论商品");
		insertNode(comment_0,comment_5,"comments.do?type=2");
//		var comment_3=new WebFXTreeItem("WEB评论列表");
//		insertNode(comment_0,comment_3,"webcomments.do");
//		var comment_3=new WebFXTreeItem("WEB评论管理");
//		insertNode(comment_0,comment_3,"webCommentList.jsp");
<%}%>

<%/****************************************产品分类管理*****************************************/%>

<%
if(group.isFlag(22)) 
//	if(isSystem	//超级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{
%>
		var catalog_0=new WebFXTreeItem("产品分类管理");
		node_root.add(catalog_0);
		var catalog_1=new WebFXTreeItem("添加分类");
		insertNode(catalog_0,catalog_1,"fcatalog.do");
		var catalog_2=new WebFXTreeItem("产品分类列表");
		insertNode(catalog_0,catalog_2,"catalogs.do");
		var catalog_3=new WebFXTreeItem("所有产品分类列表");
		insertNode(catalog_0,catalog_3,"catalogs.do?parentId=-1");
		var node_top_product=new WebFXTreeItem("点击率前100的产品");
		insertNode(catalog_0, node_top_product, "topProducts.do");
/********************************************产品交叉推荐函数内容管理***********************************************/
	<%	if(group.isFlag(228))
		{
	%>
			var relatedProduct=new WebFXTreeItem("产品交叉推荐内容管理");
			insertNode(catalog_0,relatedProduct,"relatedProductManager.do?method=list");
	<%
		}
	}	
	%>
	
<%/****************************************产品线管理*****************************************/%>

<%
if(group.isFlag(301))
{
%>
	var proline_0=new WebFXTreeItem("产品线管理");
	node_root.add(proline_0);
	var proline_1=new WebFXTreeItem("添加产品线");
	insertNode(proline_0,proline_1,"productLine.do?method=AddProductLine");
	var proline_2=new WebFXTreeItem("产品线列表");
	insertNode(proline_0,proline_2,"productLine.do?method=ProductLineList");
<%
}
%>

<%/***************************************网站地图管理******************************************/%>

<%
if(group.isFlag(22)) 
//	if(isSystem	//超级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员	
//)
{
%>
		var mapitem_0=new WebFXTreeItem("网站地图管理");
		node_root.add(mapitem_0);
		var mapitem_1=new WebFXTreeItem("添加地图元素");
		insertNode(mapitem_0,mapitem_1,"fmapItem.do");
		var mapitem_2=new WebFXTreeItem("地图元素列表");
		insertNode(mapitem_0,mapitem_2,"mapItems.do");
<%--
		var mapitem_3=new WebFXTreeItem("计算地图元素等级");
		insertNode(mapitem_0,mapitem_3,"dealMapItemLevel.do");
		var map_1=new WebFXTreeItem("添加地图关系");
		insertNode(mapitem_0,map_1,"fmap.do");
		var map_2=new WebFXTreeItem("地图关系列表");
		insertNode(mapitem_0,map_2,"maps.do");
--%>
		var map_3=new WebFXTreeItem("添加分类地图关系");
		insertNode(mapitem_0,map_3,"fcatalogMap.do");
		var map_4=new WebFXTreeItem("分类地图关系列表");
		insertNode(mapitem_0,map_4,"catalogMaps.do");
		var map_5=new WebFXTreeItem("添加池子地图关系");
		insertNode(mapitem_0,map_5,"fpoolMap.do");
		var map_6=new WebFXTreeItem("池子地图关系列表");
		insertNode(mapitem_0,map_6,"poolMaps.do");

		var articlemapitem_0=new WebFXTreeItem("云雨斋地图管理");
		node_root.add(articlemapitem_0);
		var articlemapitem_1=new WebFXTreeItem("添加地图元素");
		insertNode(articlemapitem_0,articlemapitem_1,"farticleMapItem.do");
		var articlemapitem_2=new WebFXTreeItem("地图元素列表");
		insertNode(articlemapitem_0,articlemapitem_2,"articleMapItems.do");
<%--
		var articlemapitem_3=new WebFXTreeItem("计算地图元素等级");
		insertNode(articlemapitem_0,articlemapitem_3,"dealArticleMapItemLevel.do");
		var articlemap_1=new WebFXTreeItem("添加地图关系");
		insertNode(articlemapitem_0,articlemap_1,"farticleMap.do");
		var articlemap_2=new WebFXTreeItem("地图关系列表");
		insertNode(articlemapitem_0,articlemap_2,"articleMaps.do");
--%>
		var articlemap_3=new WebFXTreeItem("添加文章分类地图关系");
		insertNode(articlemapitem_0,articlemap_3,"farticleCatalogMap.do");
		var articlemap_4=new WebFXTreeItem("文章分类地图关系列表");
		insertNode(articlemapitem_0,articlemap_4,"articleCatalogMaps.do");
		var articlemap_5=new WebFXTreeItem("添加文章池子地图关系");
		insertNode(articlemapitem_0,articlemap_5,"farticlePoolMap.do");
		var articlemap_6=new WebFXTreeItem("文章池子地图关系列表");
		insertNode(articlemapitem_0,articlemap_6,"articlePoolMaps.do");
<%}%>

<%/**************************************文章评论管理*******************************************/%>

<%
if(group.isFlag(11)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员	
//)
{
%>
		var article_comment_0=new WebFXTreeItem("文章评论管理");
		node_root.add(article_comment_0);
		var article_comment_2=new WebFXTreeItem("评论列表");
		insertNode(article_comment_0,article_comment_2,"articleComments.do");
<%}%>

<%/****************************************文章分类管理*****************************************/%>

<%
if(group.isFlag(22)) 
//	if(isSystem	//超级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{
%>
		var articlecatalog_0=new WebFXTreeItem("文章分类管理");
		node_root.add(articlecatalog_0);
		var articlecatalog_1=new WebFXTreeItem("添加文章分类");
		insertNode(articlecatalog_0,articlecatalog_1,"farticleCatalog.do");
		var articlecatalog_2=new WebFXTreeItem("文章分类列表");
		insertNode(articlecatalog_0,articlecatalog_2,"articleCatalogs.do");
		var articlecatalog_3=new WebFXTreeItem("所有文章分类列表");
		insertNode(articlecatalog_0,articlecatalog_3,"articleCatalogs.do?parentId=-1");
<%}%>

<%/*******************************************测试题管理************************************/%>
<%
if(group.isFlag(22)) 
//	if(isSystem	//超级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{
%>
		var node_test_0=new WebFXTreeItem("测试题管理");
		node_root.add(node_test_0);
		var node_test_1=new WebFXTreeItem("测试题分类列表");
		insertNode(node_test_0,node_test_1,"testCatalogs.do");
		var node_test_2=new WebFXTreeItem("测试题列表");
		insertNode(node_test_0,node_test_2,"tests.do");
<%}%>


<%/*******************************************订单管理**************************************/%>

<%
if(group.isFlag(8)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && (isGaojiAdmin || isAdmin))	//运营中心的高级管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部的高级管理员
//)
{
%>

		var node_order_deal=new WebFXTreeItem("处理订单");
		node_root.add(node_order_deal);
<%--
				var node_order_digital1=new WebFXTreeItem("手机数码(未处理)");
				insertNode(node_order_deal, node_order_digital1, "ftransact.do?status=0&orderType=1,2", "_blank");
				var node_order_dress1=new WebFXTreeItem("服装(未处理)");
				insertNode(node_order_deal, node_order_dress1, "ftransact.do?status=0&orderType=4", "_blank");
				var node_order_adult1=new WebFXTreeItem("成人保健(未处理)");
				insertNode(node_order_deal, node_order_adult1, "ftransact.do?status=0&orderType=0,3,5,6,7,8,9", "_blank");
				var node_order_digital2=new WebFXTreeItem("手机数码到付(电话失败)");
				insertNode(node_order_deal, node_order_digital2, "ftransact.do?status=1&buymode=0&orderType=1,2", "_blank");
				var node_order_dress2=new WebFXTreeItem("服装到付(电话失败)");
				insertNode(node_order_deal, node_order_dress2, "ftransact.do?status=1&buymode=0&orderType=4", "_blank");
				var node_order_adult2=new WebFXTreeItem("成人保健到付(电话失败)");
				insertNode(node_order_deal, node_order_adult2, "ftransact.do?status=1&buymode=0&orderType=0,3,5,6,7,8,9", "_blank");
				var node_order_digital2=new WebFXTreeItem("手机数码银行汇款(电话失败)");
				insertNode(node_order_deal, node_order_digital2, "ftransact.do?status=1&buymode=1,2&orderType=1,2", "_blank");
				var node_order_dress2=new WebFXTreeItem("服装邮购(电话失败)");
				insertNode(node_order_deal, node_order_dress2, "ftransact.do?status=1&buymode=1,2&orderType=4", "_blank");
				var node_order_adult2=new WebFXTreeItem("成人保健邮购(电话失败)");
				insertNode(node_order_deal, node_order_adult2, "ftransact.do?status=1&buymode=1,2&orderType=0,3,5,6,7,8,9", "_blank");
--%>
				var node_order_digital1=new WebFXTreeItem("未处理订单");
				insertNode(node_order_deal, node_order_digital1, "ftransact.do?status=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12", "_blank");
				var node_order_digital2=new WebFXTreeItem("电话失败(货到付款)");
				insertNode(node_order_deal, node_order_digital2, "ftransact.do?status=1&buymode=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12", "_blank");
				var node_order_digital3=new WebFXTreeItem("电话失败(钱包支付)");
				insertNode(node_order_deal, node_order_digital3, "ftransact.do?status=1&buymode=1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12", "_blank");
<%--
				var node_order_digital4=new WebFXTreeItem("处理发货成功");
				insertNode(node_order_deal, node_order_digital4, "ftransact.do?status=3&stockoutDeal=2&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9", "_blank");
--%>
				<%if(group.isFlag(421)){%>
				var node_order_out=new WebFXTreeItem("分批放出未处理订单");
				insertNode(node_order_deal, node_order_out, "dealOrder.do?method=checkoutOrder", "_blank");
				<%}%>
				<%if(group.isFlag(458)){%>
				var order_manage=new WebFXTreeItem("订单处理分配管理");
				insertNode(node_order_deal, order_manage, "set/index.jsp");
				<%}%>	

		var node_order_deal_time_quantum=new WebFXTreeItem("按时段处理订单");
		node_root.add(node_order_deal_time_quantum);
		var node_order_digital1=new WebFXTreeItem("5时之前未处理订单");
		insertNode(node_order_deal_time_quantum, node_order_digital1, "ftransact.do?status=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12&endHour=5", "_blank");
		var node_order_digital1=new WebFXTreeItem("5时~13时未处理订单");
		insertNode(node_order_deal_time_quantum, node_order_digital1, "ftransact.do?status=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12&startHour=5&endHour=13", "_blank");
		var node_order_digital1=new WebFXTreeItem("13时~17时未处理订单");
		insertNode(node_order_deal_time_quantum, node_order_digital1, "ftransact.do?status=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12&startHour=13&endHour=17", "_blank");
		var node_order_digital1=new WebFXTreeItem("17时~21时未处理订单");
		insertNode(node_order_deal_time_quantum, node_order_digital1, "ftransact.do?status=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12&startHour=17&endHour=21", "_blank");
		var node_order_digital1=new WebFXTreeItem("21时之后未处理订单");
		insertNode(node_order_deal_time_quantum, node_order_digital1, "ftransact.do?status=0&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12&startHour=21&endHour=5", "_blank");

		var node_order_list3=new WebFXLoadTreeItem("银行汇款订单列表","tree/orderStatus.jsp?mode=2");
		node_root.add(node_order_list3);

		var node_order_manage_root3=new WebFXTreeItem("银行汇款订单管理");
		node_root.add(node_order_manage_root3);
<%--
				var node_order_manage3=new WebFXTreeItem("汇总订单");
				insertNode(node_order_manage_root3, node_order_manage3, "collect.do?buymode=2", "_blank");
				var node_order_manage3=new WebFXTreeItem("截止汇总订单");
				insertNode(node_order_manage_root3, node_order_manage3, "collect2.do?buymode=2&status=4", "_blank");
--%>
<%if(group.isFlag(380)){%>		
				var node_order_manage4=new WebFXTreeItem("添加新订单");
				insertNode(node_order_manage_root3, node_order_manage4, "faorder.do?buymode=2", "_blank");
<%if(group.isFlag(190)){%>
				var node_order_manage5=new WebFXTreeItem("添加新团购订单");
				insertNode(node_order_manage_root3, node_order_manage5, "faorder.do?buymode=2&t=1", "_blank");
<%}%><%}%>

		var node_order_list=new WebFXLoadTreeItem("货到付款订单列表","tree/orderStatus.jsp?mode=0");
		node_root.add(node_order_list);

		var node_order_manage=new WebFXTreeItem("货到付款订单管理");
		node_root.add(node_order_manage);
<%--
				var node_order_manage1=new WebFXTreeItem("处理订单(未处理)");
				insertNode(node_order_manage, node_order_manage1, "ftransact.do?status=0", "_blank");
				var node_order_manage5=new WebFXTreeItem("处理订单(电话失败)");
				insertNode(node_order_manage, node_order_manage5, "ftransact.do?status=1&buymode=0", "_blank");
				var node_order_manage2=new WebFXTreeItem("汇总订单");
				insertNode(node_order_manage, node_order_manage2, "collect.do?buymode=0", "_blank");
				var node_order_manage3=new WebFXTreeItem("截止汇总订单");
				insertNode(node_order_manage, node_order_manage3, "collect2.do?buymode=0&status=4", "_blank");
--%>
				var node_order_manage4=new WebFXTreeItem("添加新订单");
				insertNode(node_order_manage, node_order_manage4, "faorder.do?buymode=0", "_blank");
<%if(group.isFlag(190)){%>
				var node_order_manage5=new WebFXTreeItem("添加新团购订单");
				insertNode(node_order_manage, node_order_manage5, "faorder.do?buymode=0&t=1", "_blank");
<%}%>

		var node_order_list2=new WebFXLoadTreeItem("钱包支付订单列表","tree/orderStatus.jsp?mode=1");
		node_root.add(node_order_list2);


		var node_order_manage_root2=new WebFXTreeItem("钱包支付订单管理");
		node_root.add(node_order_manage_root2);
<%--
				var node_order_manage5=new WebFXTreeItem("处理订单(电话失败)");
				insertNode(node_order_manage_root2, node_order_manage5, "ftransact.do?status=1&buymode=1,2", "_blank");
				var node_order_manage2=new WebFXTreeItem("汇总订单");
				insertNode(node_order_manage_root2, node_order_manage2, "collect.do?buymode=1", "_blank");
				var node_order_manage3=new WebFXTreeItem("截止汇总订单");
				insertNode(node_order_manage_root2, node_order_manage3, "collect2.do?buymode=1&status=4", "_blank");
--%>

				var node_order_manage4=new WebFXTreeItem("添加新订单");
				insertNode(node_order_manage_root2, node_order_manage4, "faorder.do?buymode=1", "_blank");
<%if(group.isFlag(190)){%>
				var node_order_manage5=new WebFXTreeItem("添加新团购订单");
				insertNode(node_order_manage_root2, node_order_manage5, "faorder.do?buymode=1&t=1", "_blank");
<%}%>
<%if(group.isFlag(490)){%>
				var excption_order=new WebFXTreeItem("查看支付异常订单");
				insertNode(node_order_manage_root2, excption_order, "ExceptionOrder.do?method=selExcOrder");
<%}%>
<%--
				var node_order_manage5=new WebFXTreeItem("处理订单(电话失败)");
				insertNode(node_order_manage_root2, node_order_manage5, "ftransact.do?status=1&buymode=1,2", "_blank");
--%>

		var node_order_list4=new WebFXTreeItem("售后换货订单列表");
		node_root.add(node_order_list4);
				var node_order20=new WebFXTreeItem("已到款订单");
				insertNode(node_order_list4, node_order20, "orders.do?buymode=3&status=3");
				var node_order20=new WebFXTreeItem("已发货订单");
				insertNode(node_order_list4, node_order20, "orders.do?buymode=3&status=6");
				var node_order20=new WebFXTreeItem("已取消订单");
				insertNode(node_order_list4, node_order20, "orders.do?buymode=3&status=7");
				var node_order20=new WebFXTreeItem("已退回订单");
				insertNode(node_order_list4, node_order20, "orders.do?buymode=3&status=11");
				var node_order20=new WebFXTreeItem("待退回订单");
				insertNode(node_order_list4, node_order20, "orders.do?buymode=3&status=13");
				var node_order20=new WebFXTreeItem("已妥投订单");
				insertNode(node_order_list4, node_order20, "orders.do?buymode=3&status=14");

<%}%>

<%if(group.isFlag(145)){%>
		var node_mailing_balance_root=new WebFXTreeItem("物流结算");
		node_root.add(node_mailing_balance_root);
	<%if(group.isFlag(259)){%>
		var node_mb_3=new WebFXTreeItem("结算时间及结算周期设置");
		insertNode(node_mailing_balance_root, node_mb_3, "BalanceCycleList.do");
	<%}%>
	<%if(group.isFlag(147)){%>
		var node_mb_2=new WebFXTreeItem("结算数据导入(普通)");
		insertNode(node_mailing_balance_root, node_mb_2, "importOrderBalance.do");
	<%}%>
	<%if(group.isFlag(241)){%>
		var node_mb_2=new WebFXTreeItem("结算数据导入(运费)");
		insertNode(node_mailing_balance_root, node_mb_2, "importCarriage.do");
	<%}%>
	<%if(group.isFlag(148)){%>
		var node_mb_2=new WebFXTreeItem("结算数据确认");
		insertNode(node_mailing_balance_root, node_mb_2, "mailingBalance.do?method=mailingBalanceAuditingList");
	<%}%>
	<%if(group.isFlag(146)){%>
		var node_mb_1=new WebFXTreeItem("结算数据查询");
		insertNode(node_mailing_balance_root, node_mb_1, "balance/SearchJieSuanShuJu.jsp");
	<%}%>
	<%if(group.isFlag(235)){%>
		var node_mb_1=new WebFXTreeItem("结算数据明细");
		insertNode(node_mailing_balance_root, node_mb_1, "balance/balanceChargeDetails.jsp");
	<%}%>
	<%if(group.isFlag(149)){%>
		var node_mb_4=new WebFXTreeItem("分析总结");
		insertNode(node_mailing_balance_root, node_mb_4, "balance/statBalanceData.jsp");
	<%}%>
	<%if(group.isFlag(150)){%>
		var node_mb_5=new WebFXTreeItem("详细退单率统计");
		insertNode(node_mailing_balance_root, node_mb_5, "balance/returnOrderRateStatic.jsp");
	<%}%>
	<%if(group.isFlag(261)){%>
		var node_mb_6=new WebFXTreeItem("物流成本统计");
		insertNode(node_mailing_balance_root, node_mb_6, "balance/logisticCostStat.jsp");
	<%}%>
	<%if(group.isFlag(261)){%>
		var node_mb_7=new WebFXTreeItem("物流成本统计(按结算状态)");
		insertNode(node_mailing_balance_root, node_mb_7, "balance/logisticCostStat2.jsp");
	<%}%>
	<%if(group.isFlag(147)){%>
		var node_mb_2=new WebFXTreeItem("结算订单导出");
		insertNode(node_mailing_balance_root, node_mb_2, "mailingBalance.do?method=exportOrderBalance");
	<%}%>
<%}%>
<%
if(group.isFlag(129)&&group.isFlag(316)){ 
%>
		var node_order_manage_root3=new WebFXTreeItem("发货订单管理");
		node_root.add(node_order_manage_root3);
<%
if(group.isFlag(254)&&group.isFlag(316)){ 
%>
		var node_order_digital4=new WebFXTreeItem("昨天发货未处理");
		insertNode(node_order_manage_root3, node_order_digital4, "ftransact.do?status=3&stockoutDeal=0&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14&orderType2=0&day=yesterday", "_blank");
		var node_order_digital4=new WebFXTreeItem("当天发货未处理");
		insertNode(node_order_manage_root3, node_order_digital4, "ftransact.do?status=3&stockoutDeal=0&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14&orderType2=0&day=today", "_blank");
		var node_order_digital4=new WebFXTreeItem("处理发货失败");
		insertNode(node_order_manage_root3, node_order_digital4, "ftransact.do?status=3&stockoutDeal=1&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14&orderType2=0", "_blank");
<%}%>
<%if(group.isFlag(130)&&group.isFlag(316)){%>
		var node_order20=new WebFXTreeItem("未处理订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=0&orderBy=a.id desc&orderType2=0");
<%}%>
<%
if(group.isFlag(254)&&group.isFlag(316)){ 
%>
		var node_order20=new WebFXTreeItem("失败订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=1&orderBy=a.id desc&orderType2=0");
		<%--
		var node_order20=new WebFXTreeItem("成功订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=2&orderType2=0");--%>
<%}%>
<%if(group.isFlag(162)&&group.isFlag(316)){%>
		var node_order20=new WebFXTreeItem("缺货订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=4&orderType2=0");
<%}%>

<%} // 发货订单管理%>


<%
if(group.isFlag(129)&&group.isFlag(317)){ 
%>
		var node_order_manage_root3=new WebFXTreeItem("无锡单发货订单管理");
		node_root.add(node_order_manage_root3);
<%
if(group.isFlag(254)&&group.isFlag(317)){ 
%>
		var node_order_digital4=new WebFXTreeItem("昨天发货未处理");
		insertNode(node_order_manage_root3, node_order_digital4, "ftransact.do?status=3&stockoutDeal=0&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14&orderType2=1&day=yesterday", "_blank");
		var node_order_digital4=new WebFXTreeItem("当天发货未处理");
		insertNode(node_order_manage_root3, node_order_digital4, "ftransact.do?status=3&stockoutDeal=0&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14&orderType2=1&day=today", "_blank");
		var node_order_digital4=new WebFXTreeItem("处理发货失败");
		insertNode(node_order_manage_root3, node_order_digital4, "ftransact.do?status=3&stockoutDeal=1&buymode=0,1,2&orderType=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14&orderType2=1", "_blank");
<%}%>
<%if(group.isFlag(130)&&group.isFlag(317)){%>
		var node_order20=new WebFXTreeItem("未处理订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=0&orderBy=a.id desc&orderType2=1");
<%}%>
<%
if(group.isFlag(254)&&group.isFlag(317)){ 
%>
		var node_order20=new WebFXTreeItem("失败订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=1&orderBy=a.id desc&orderType2=1");
		<%--
		var node_order20=new WebFXTreeItem("成功订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=2&orderType2=1");--%>
<%}%>
<%if(group.isFlag(162)&&group.isFlag(317)){%>
		var node_order20=new WebFXTreeItem("缺货订单列表");
		insertNode(node_order_manage_root3, node_order20, "stockoutOrders.do?stockoutStatus=4&orderType2=1");
<%}%>

<%} // 发货订单管理%>

<%
if(group.isFlag(8)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && (isGaojiAdmin || isAdmin))	//运营中心的高级管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部的高级管理员
//)
{
%>
<%
if(group.isFlag(131)){ 
%>
		var node_order_manage_root3=new WebFXLoadTreeItem("结算订单管理","tree/orderBalance.jsp");
		node_root.add(node_order_manage_root3);

<%} // 结算订单管理%>
<%}%>

<%/*****************************************搜索查询****************************************/%>

<%
if(group.isFlag(7)) 
//	if(isSystem	//超级管理员
//	|| (isXiaoshou || isKefu)	//销售部、客服部
//	|| (isYunyingzhongxin && (isGaojiAdmin || isAdmin))	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//)
{
%>
		var node_search=new WebFXTreeItem("搜索查询");
		//insertNode(node_root, node_search, "search.do");
		node_root.add(node_search);
	<%/***************************************销售中心绩效考核查询****************************************/%>
	<%if(group.isFlag(404)){ %>
	var performance_examine_query = new WebFXTreeItem("销售中心绩效考核查询");
	node_search.add(performance_examine_query);
	<%if(group.isFlag(389)){ %>
	var performance_examine_query_management = new WebFXTreeItem("绩效考核查询管理");
	performance_examine_query.add(performance_examine_query_management);
	var group_management = new WebFXTreeItem("组别管理");
	insertNode(performance_examine_query_management, group_management,"performanceExamineQuery/groupManagement.jsp");
	var Staff_management = new WebFXTreeItem("员工管理");
	insertNode(performance_examine_query_management, Staff_management,"performanceExamineQuery/staffManagement.jsp");
	var performanceExamineStandardManagement = new WebFXTreeItem("绩效考核标准管理");
	insertNode(performance_examine_query_management, performanceExamineStandardManagement,"performanceExamineQuery/standardManagement.jsp");
	var staffGradeManagement = new WebFXTreeItem("员工评分管理");
	insertNode(performance_examine_query_management, staffGradeManagement,"performanceExamineQuery/staffGradeManagement1.jsp");
	var serviceGradeManagement = new WebFXTreeItem("服务分绩效考核标准");
	insertNode(performance_examine_query_management, serviceGradeManagement,"performanceExamineQuery/serviceGradeManagement.jsp");
	<%}%>
	var staffPerformanceQuery = new WebFXTreeItem("绩效考核查询");
	insertNode(performance_examine_query, staffPerformanceQuery,"performanceExamineQuery/staffPerformanceQuery.jsp?firstIn=1");
	<%}%>
<%if(group.isFlag(142)) //if(isSystem || isXiaoshou || isKefu || isYunyingzhongxin || isPingtaiyunwei || isShangpin)
{	%>
				var node_search_product=new WebFXTreeItem("产品查询");
				insertNode(node_search, node_search_product, "searchproduct.do");
				var node_search_product_barcodes=new WebFXTreeItem("产品查询(批量条码)");
				insertNode(node_search, node_search_product_barcodes, "barcodeManager/fproductBarcode.jsp");
<%}%>
<%if(group.isFlag(300)){%>
	var node_search_stock=new WebFXTreeItem("申请出库订单查询");
	insertNode(node_search,node_search_stock,"searchorderstockamount.jsp");
<%}%>
<%if(group.isFlag(327)){%>
	var node_search_deliver_count = new WebFXTreeItem("发货订单分库查询");
	insertNode(node_search,node_search_deliver_count,"searchOrdersDeliverAmount.jsp?firstChecked=1");
<%}%>	
<%
if(group.isFlag(22)) 
//	if(isSystem	//超级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))	//平台运维部的高级管理员和普通管理员
//)
{
%>
				var node_search_article=new WebFXTreeItem("文章查询");
				insertNode(node_search, node_search_article, "searcharticle.do");
<%}%>
<%if(group.isFlag(9)) //if(isSystem || isXiaoshou || isKefu || isYunyingzhongxin || (isPingtaiyunwei && isGaojiAdmin) || isShangpin)
{	%>
				var node_search_order=new WebFXTreeItem("订单查询");
				insertNode(node_search, node_search_order, "searchorder.do");
<%}%>
<%if(group.isFlag(337)){%>
				var search_order4_sales=new WebFXTreeItem("销售部订单查询");
			    insertNode(node_search, search_order4_sales, "searchOrder4Sales.jsp");
<%}%>
<%if(group.isFlag(336)){%>
			    var search_order4_operations=new WebFXTreeItem("运营部订单查询");
			    insertNode(node_search, search_order4_operations, "searchOrder4Operations.jsp");
<%}%>
<%if(group.isFlag(134))
{	%>
				var node_search_order=new WebFXTreeItem("批次查询");
				insertNode(node_search, node_search_order, "searchbatch.do");
<%}%>
<%if(group.isFlag(157))
{	%>
				var node_search_order=new WebFXTreeItem("成交率查询");
				insertNode(node_search, node_search_order, "searchdealrate.do?pq=0");
				var node_search_order1=new WebFXTreeItem("北京");
				insertNode(node_search_order, node_search_order1, "searchdealrate.do?pq=9");
				var node_search_order2=new WebFXTreeItem("无锡");
				insertNode(node_search_order, node_search_order2, "searchdealrate.do?pq=8");
				
			   var node_proportion_order = new WebFXTreeItem("订单占比查询");
			   insertNode(node_search, node_proportion_order,"searchProportion.do?pq=0");
			   var node_proportion_order1 = new WebFXTreeItem("北京");
			   insertNode(node_proportion_order, node_proportion_order1,"searchProportion.do?pq=9");
			   var node_proportion_order2 = new WebFXTreeItem("无锡");
			   insertNode(node_proportion_order, node_proportion_order2,"searchProportion.do?pq=8");
<%}%>
<%--if(group.isFlag(12)) //if(isSystem || isKefu || (isYunyingzhongxin && isGaojiAdmin) || (isPingtaiyunwei && isGaojiAdmin) || isXiaoshou)
{	/*对客服部开放*/%>
				var node_search_oc=new WebFXTreeItem("老顾客列表");
				insertNode(node_search, node_search_oc, "oldCustomers.do");
<%}--%>
			   
<%if(group.isFlag(20)) //if(isSystem || isYunyingzhongxin || (isShangpin) || (isPingtaiyunwei && isGaojiAdmin))
{	/*对运营中心、商品部管理员开放*/%>
                var node_search_order_history=new WebFXTreeItem("成交订单销量查询");
				insertNode(node_search, node_search_order_history, "searchOrderHistory.do");
				var node_search_cj_order=new WebFXTreeItem("成交订单产品查询");
				insertNode(node_search, node_search_cj_order, "searchCjOrder.do");
				var each_productLine_saleCount = new WebFXTreeItem("各产品线销量查询");
	            insertNode(node_search,each_productLine_saleCount,"productline/eachProductLineCount.jsp"); 
<%}%>
<%if(group.isFlag(49)) //if(isSystem || isYunyingzhongxin || (isShangpin) || (isPingtaiyunwei && isGaojiAdmin))
{	/*对运营中心、商品部管理员开放*/%>
				var node_search_order_history2=new WebFXTreeItem("历史销量查询");
				insertNode(node_search, node_search_order_history2, "searchOrderHistory2.do");
<%}%>
<%if(group.isFlag(21)) //if(isSystem || isYunyingzhongxin || (isShangpin && isGaojiAdmin) || (isPingtaiyunwei && isGaojiAdmin))
{	/*对运营中心、商品部高级管理员开放*/%>
				var node_search_order_product_history=new WebFXTreeItem("非重订单销量查询");
				insertNode(node_search, node_search_order_product_history, "searchOrderProductHistory.do");
<%}%>
<%if(group.isFlag(338)) //if(isSystem || (isYunyingzhongxin && isGaojiAdmin) || (isShangpin && isGaojiAdmin))
{	/*对运营中心、商品部高级管理员开放*/%>
				var node_stat_stock1=new WebFXTreeItem("进出货统计");
			    insertNode(node_search, node_stat_stock1, "stat_nocache/stockInOutStat.jsp");
<%}%>
<%if(group.isFlag(16)) 
//	if(isSystem	//超级管理员
//	|| isKefu	//销售部
//	|| (isPingtaiyunwei && isGaojiAdmin)	//平台运维部高级管理员
//	|| (isXiaoshou && isGaojiAdmin)	//销售部高级管理员
//)
{%>
				var node_stat_seller=new WebFXTreeItem("销售业绩查询");
			    insertNode(node_search, node_stat_seller, "searchseller.do");
<%}%>
<%if(group.isFlag(14)) 
//	if(isSystem	//超级管理员
//	|| isXiaoshou // 销售部
//	|| isShangpin // 商品部
//)
{%>
				var node_order_stock=new WebFXTreeItem("两地发货查询");
			    insertNode(node_search, node_order_stock, "searchOrderStock.do");
<%}%>
<%if(group.isFlag(48)) 
// 原权限 19
//	if(isSystem	//超级管理员
//	|| isYunyingzhongxin 
//	|| (isShangpin && isGaojiAdmin) 
//	|| (isPingtaiyunwei)
//)
{%>
				var node_sell_product=new WebFXTreeItem("分类商品销量查询");
			    insertNode(node_search, node_sell_product, "searchSellProduct.do");
<%}%>
	
 
<%if(group.isFlag(18)) 
//	if(isSystem	//超级管理员
//	|| isYunyingzhongxin  //运营中心
//)
{%>
			
				var node_stat_order=new WebFXTreeItem("成交订单及销售额综合");
			    insertNode(node_search, node_stat_order, "statOrderNew.jsp");
				var node_stat_order2=new WebFXTreeItem("成交订单及销售额综合(北京)");
			    insertNode(node_search, node_stat_order2, "statOrderNew.jsp?a=1");
				var node_stat_order3=new WebFXTreeItem("成交订单及销售额综合(无锡)");
			    insertNode(node_search, node_stat_order3, "statOrderNew.jsp?a=2");
<%}%>
<%if(group.isFlag(6)) 
//	if(isSystem	//超级管理员
//	|| isXiaoshou // 销售部
//	|| isShangpin // 商品部
//	|| isKefu	// 客服部
//	|| isYunyingzhongxin	// 运营中心
//)
{%>
				var node_order_stock=new WebFXTreeItem("包裹单号导入");
			    insertNode(node_search, node_order_stock, "importOrderPackageNum.do");
<%}%>
<%}%>
<%/**************************************拍卖信息管理****************************************/%>
<%if(group.isFlag(18))
//	if(isSystem	//超级管理员
//	|| isYunyingzhongxin	// 运营中心
//)
{%>
		var node_auction=new WebFXTreeItem("拍卖信息管理");
		node_root.add(node_auction);

			var node_auction_list=new WebFXTreeItem("拍卖信息");
			insertNode(node_auction, node_auction_list, "auctions.do");
<%}%>
<%/**************************************供应商信息管理****************************************/%>
<%if (group.isFlag(322)) {%>
var supplier_management = new WebFXTreeItem("供应商管理");
node_root.add(supplier_management);
<%if (group.isFlag(323)) {%>
var add_management = new WebFXTreeItem("添加供应商");
insertNode(supplier_management, add_management,
		"supplierManagement/addSupplier.jsp");

<%}%>
var supplier_verify_info = new WebFXTreeItem("资格审核");
insertNode(supplier_management, supplier_verify_info,
		"supplierManagement/supplierVerifyInfo.jsp");

<%if (group.isFlag(383)) {%>
var supplier_info_management = new WebFXTreeItem("供应商列表");
insertNode(supplier_management, supplier_info_management,
		"supplierManagement/supplierInfoManagement.jsp?flag=no");
<%}%>
var grade_management = new WebFXTreeItem("供应商评级管理");
insertNode(supplier_management, grade_management,
		"supplierManagement/supplierGradeApplyInfo.jsp");

var badnessRecor_management = new WebFXTreeItem("供应商不良记录管理");
insertNode(supplier_management, badnessRecor_management,
		"supplierManagement/supplierBadnessRecordInfo.jsp?flag=no");


<%/***************************************供应商属性管理****************************************/%>
<%if (group.isFlag(325)) {%>

var supplier_property_management = new WebFXTreeItem("供应商属性管理");
supplier_management.add(supplier_property_management);
var supplier_grade_management = new WebFXTreeItem("供应商等级管理");
insertNode(supplier_property_management, supplier_grade_management,
		"supplierManagement/supplierGradeManagement.jsp");


var supplier_bank_info = new WebFXTreeItem("银行名称管理");
insertNode(supplier_property_management, supplier_bank_info,
		"supplierManagement/supplierBankNameManagement.jsp");



var supplier_user_management = new WebFXTreeItem("供应商管理用户列表");
insertNode(supplier_property_management, supplier_user_management,
		"supplierManagement/supplierUserManagement.jsp");
<%}%>
<%}%>
<%/****************************************备用金管理*******************************************/%>
<%
if (group.isFlag(353) || group.isFlag(354)||group.isFlag(355))
{
%>
	var imprest_management=new WebFXTreeItem("备用金管理");
	node_root.add(imprest_management);
	var imprest_balance=new WebFXTreeItem("备用金余额查询");
	insertNode(imprest_management, imprest_balance, "imprestFinance.do?method=ImprestBalance");
	var imprest_details=new WebFXTreeItem("备用金明细查询");
	insertNode(imprest_management, imprest_details, "imprestFinance.do?method=ImprestUseDetailsList");
	var imprest_app=new WebFXTreeItem("备用金申请");
	insertNode(imprest_management, imprest_app, "supplier/addImprestApplication.jsp");
	var imprest_appList=new WebFXTreeItem("备用金申请记录");
	insertNode(imprest_management, imprest_appList, "imprestFinance.do?method=ImprestApplicationList");
	var imprest_appreturn=new WebFXTreeItem("备用金退回申请");
	insertNode(imprest_management, imprest_appreturn, "supplier/addImprestReturnApplication.jsp");
	var imprest_appcost=new WebFXTreeItem("备用金费用单申请");
	insertNode(imprest_management, imprest_appcost, "supplier/addImprestCostApplication.jsp");
	var imprest_appcostList=new WebFXTreeItem("备用金费用单申请列表");
	insertNode(imprest_management,imprest_appcostList,"supplierFinance.do?method=imprestApplicationList");
<%
}
%>
<%/****************************************商品采购管理*****************************************/%>
<%
if(group.isFlag(62) || group.isFlag(63)||group.isFlag(78)||group.isFlag(122)|group.isFlag(117)||group.isFlag(398)) 
{
%>
		var node_buy=new WebFXTreeItem("商品采购");
		node_root.add(node_buy);
<%
if(group.isFlag(62))
{
%>
		    var node_buy_plan_list=new WebFXTreeItem("采购计划列表");
			insertNode(node_buy, node_buy_plan_list, "stock2/buyPlanList.jsp");
<%}%>
<%
if(group.isFlag(78))
{
%>
			var node_buy_order_list=new WebFXTreeItem("采购订单列表");
			insertNode(node_buy, node_buy_order_list, "stock2/buyOrderList.jsp");
<%}%>
<%
if(group.isFlag(63))
{
%>
			var node_buy_stock_list=new WebFXTreeItem("预计到货列表");
			insertNode(node_buy, node_buy_stock_list, "stock2/buyStockList.jsp");
<%}%>
<%
if(group.isFlag(122))
{
%>
			var node_buy_stock_list=new WebFXTreeItem("采购入库列表");
			insertNode(node_buy, node_buy_stock_list, "stock2/buyStockinList.jsp");
<%}%>
<%
if(group.isFlag(117))
{
%>
			var node_buy_stock_list=new WebFXTreeItem("采购退货列表");
			insertNode(node_buy, node_buy_stock_list, "stock2/buyReturnList.jsp");
<%}%>


<%
if(group.isFlag(398))
{
%>
	var buy_return_reason = new WebFXTreeItem("采购退货原因统计");
	node_buy.add(buy_return_reason);
	
	<%
		if(group.isFlag(399)){
		%>
		var buy_return_reason_manager1 = new WebFXTreeItem("采购退货原因管理");
		insertNode(buy_return_reason, buy_return_reason_manager1,"stock2/buyReturnReasonList.jsp?type=5");
		<%
		}
	%>

	var buy_return_reason_manager2 = new WebFXTreeItem("采购退货原因统计");
	insertNode(buy_return_reason, buy_return_reason_manager2,"stock2/buyReturnReasonListTongJi.jsp?type=5");
<%}%>
<%
if(group.isFlag(159))
{
%>
			var node_buy_stock_list=new WebFXTreeItem("采购入库价调整");
			insertNode(node_buy, node_buy_stock_list, "stock2/stockBatchPriceList.jsp");
<%}%>
<%
if(group.isFlag(309))
{
%>
			var node_buy_stock_list=new WebFXTreeItem("采购打款申请列表");
			insertNode(node_buy, node_buy_stock_list, "supplierFinance.do?method=PayApplicationList");
<%}%>
<%if(group.isFlag(SupplierPayApplicationBean.OPWER1))
{
%>
			var node_buy_stock_list=new WebFXTreeItem("添加打/退款申请单");
			insertNode(node_buy, node_buy_stock_list, "supplierFinance.do?method=AddApplicationPage");
<%}%>
<%}%>

<%/****************************************库存管理*****************************************/%>
<%
if(group.isFlag(13)) 
//	if(isSystem	//超级管理员
//    || ((isXiaoshou || isKefu) && (isGaojiAdmin || isAdmin))	//销售部、客服部的高级管理员、普通管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
<%--
		var node_stock=new WebFXTreeItem("库存管理");
		insertNode(node_root, node_stock, "stockproducts.do");
			var node_stockin=new WebFXTreeItem("入库");
			insertNode(node_stock, node_stockin, "stockin.do");
			var node_stockins=new WebFXTreeItem("历史入库");
			insertNode(node_stock, node_stockins, "stockins.do");
			var node_stockout=new WebFXTreeItem("出库");
			insertNode(node_stock, node_stockout, "searchorder.do");
			var node_stockouts=new WebFXTreeItem("历史出库");
			insertNode(node_stock, node_stockouts, "stockouts.do");			
--%>		
		var node_stock1=new WebFXTreeItem("新库存管理");
		node_root.add(node_stock1);
<%
if(group.isFlag(79)){
%>
		var node_stock_prepare=new WebFXTreeItem("库存调拨管理");
		node_stock1.add(node_stock_prepare);
		var node_stock_exchange=new WebFXTreeItem("库存调拨");
		insertNode(node_stock_prepare, node_stock_exchange, "./productStock/stockExchangeList.jsp");
<%--
		var node_stock_prepare_check=new WebFXTreeItem("检测管理");
		insertNode(node_stock_prepare, node_stock_prepare_check, "./stockPrepare/stockCheckList.jsp");
		var node_stock_prepare_repair=new WebFXTreeItem("维修管理");
		insertNode(node_stock_prepare, node_stock_prepare_repair, "./stockPrepare/stockRepairList.jsp");
		var node_stock_prepare_back=new WebFXTreeItem("返厂管理");
		insertNode(node_stock_prepare, node_stock_prepare_back, "./stockPrepare/stockBackList.jsp");
--%>
<%}%>
<%
if(group.isFlag(97))
{
%>
		    var node_all_product_stock_sub=new WebFXTreeItem("库存量调整");
			insertNode(node_stock1, node_all_product_stock_sub, "productStockTune/stockTuneList.jsp");
<%}%>
<%
if(group.isFlag(98)) 
//	if(isSystem	//超级管理员
//		|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//		|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//	)
{
%>
		    var node_all_product_stock_sub=new WebFXTreeItem("库存查询");
			insertNode(node_stock1, node_all_product_stock_sub, "allProductsStockSub.jsp");
<%}%>
<%
if(group.isFlag(93)) {%>
			var node_all_product_stock_sub=new WebFXTreeItem("分库库存查询");
			insertNode(node_stock1, node_all_product_stock_sub, "allProductsStockSubAmount.jsp");
<%}%>
<%--
if(group.isFlag(17)) 
//	if(isSystem	//超级管理员
//    || ((isXiaoshou || isKefu) && isGaojiAdmin)	//销售部、客服部的高级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
			var productGroup=new WebFXTreeItem("产品组");
			insertNode(node_stock1, productGroup, "stock/productGroupList.jsp");
<%
}
--%>
<%
if(group.isFlag(252)) 
//	if(isSystem	//超级管理员
//    || ((isXiaoshou || isKefu) && (isGaojiAdmin || isAdmin))	//销售部、客服部的高级管理员、普通管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
			var orderStock=new WebFXTreeItem("订单出货");
			insertNode(node_stock1, orderStock, "orderStock/orderStockList.jsp");
<%if(group.isFlag(133)){%>
			var orderStock=new WebFXTreeItem("复核列表");
			insertNode(node_stock1, orderStock, "orderStock/checkOrderStockList.jsp");
<%}%>
<%if(group.isFlag(359)){%>
			var orderStock=new WebFXTreeItem("发货单导出及打印");
			insertNode(node_stock1, orderStock, "orderStock/orderStockExportPrint.jsp");
<%}%>
<%--
			var orderStock=new WebFXTreeItem("旧的——订单出货");
			insertNode(node_stock1, orderStock, "stock/orderStockList.jsp");
--%>
<%}%>
<%if(group.isFlag(251)){%>
			var orderStock=new WebFXTreeItem("进销存卡片");
			insertNode(node_stock1, orderStock, "productStock/stockCardList.jsp");
<%}%>
<%if(group.isFlag(339)){%>
var buyStock=new WebFXTreeItem("采购入库");
insertNode(node_stock1, buyStock, "stock2/buyStockinListOld.jsp");
<%}%>
<%if(group.isFlag(340)){%>
var buyStockHistory=new WebFXTreeItem("历史采购入库查询");
insertNode(node_stock1, buyStockHistory, "searchStockinHistory.do");
<%}%>
<%
if(group.isFlag(17)) 
//	if(isSystem	//超级管理员
//    || ((isXiaoshou || isKefu) && isGaojiAdmin)	//销售部、客服部的高级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isShangpin && (isGaojiAdmin || isAdmin))	//商品部的高级管理员和普通管理员
//	|| (isPingtaiyunwei && isGaojiAdmin)	//运营中心的高级管理员
//)
{
%>
			var cancelStockHistory=new WebFXTreeItem("历史退/换货入库查询");
			insertNode(node_stock1, cancelStockHistory, "searchCancelStockinHistory.do");

<%--
			var gdBj=new WebFXTreeItem("两地调货");
			insertNode(node_stock1, gdBj, "stock/bjGdList.jsp");
--%>
			var gdBjHistory=new WebFXTreeItem("历史两地调货查询");
			insertNode(node_stock1, gdBjHistory, "searchBjGdHistory.do");
<%if(group.isFlag(230)){%>			
			var bsby=new WebFXTreeItem("报损报溢");
			insertNode(node_stock1, bsby, "bybs.do?method=begin");
			<%}%>
			
<%--
			var regroup=new WebFXTreeItem("库存组合");
			insertNode(node_stock1, regroup, "stock/regroupList.jsp");
			var productPackage=new WebFXTreeItem("套装组合");
			insertNode(node_stock1, productPackage, "stock/productPackageList.jsp");
--%>
<%if(group.isFlag(43)){%>
			var others=new WebFXTreeItem("其他出入库");
			insertNode(node_stock1, others, "stock/othersList.jsp");
<%}%>
<%}%>
<%if(group.isFlag(250)){%>
			var cancelStock=new WebFXTreeItem("退/换货入库");
			insertNode(node_stock1, cancelStock, "stock/cancelStockinList.jsp");
<%}%>
<%
if(group.isFlag(3))
{%>
<%if(false&&group.isFlag(135))
{%>
			var node_productstockbj=new WebFXTreeItem("北京成人保健警戒产品");
			insertNode(node_stock1, node_productstockbj, "productstock.do?dateType=0&allProduct=1&areaId=0&showHide=1&catalogIds=83,2,3,4,5,7,1,8,93,43,94,145,690&productLineName=beijingchengrenbaojianpin&securityLine="+<%=ProductLineMap.SECURITYLINE0%>);
<%}%>
<%if(group.isFlag(136))
{%>
			var node_productstockgz=new WebFXTreeItem("保健品/内衣警戒产品");
			insertNode(node_stock1, node_productstockgz, "productstock.do?dateType=0&allProduct=1&areaId=1&showHide=1&catalogIds=94,145&productLineName=baojianpin&securityLine="+<%=ProductLineMap.SECURITYLINE1%>);
<%}%>
<%if(group.isFlag(403))
{%>
			var node_productstockcr=new WebFXTreeItem("成人日用警戒产品");
			insertNode(node_stock1, node_productstockcr, "productstock.do?dateType=0&allProduct=1&areaId=1&showHide=1&catalogIds=690&productLineName=chengrenriyongpin&securityLine="+<%=ProductLineMap.SECURITYLINE16%>);
<%}%>
<%if(group.isFlag(137))
{%>
			var node_productstock=new WebFXTreeItem("手机数码警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogId2s=105,114,300,118,806,807,808,809,810,811&showHide=1&productLineName=shoujishuma&securityLine="+<%=ProductLineMap.SECURITYLINE2%>);
<%}%>
<%if(group.isFlag(307))
{%>
			var node_productstock=new WebFXTreeItem("行货手机警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogId2s=110&showHide=1&productLineName=hanghuoshouji&securityLine="+<%=ProductLineMap.SECURITYLINE11%>);
<%}%>
<%if(group.isFlag(138))
{%>
			var node_productstock=new WebFXTreeItem("手机数码配件警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogId2s=107,117&showHide=1&showBad=1&showRepair=1&productLineName=shoujishumapeijian&securityLine="+<%=ProductLineMap.SECURITYLINE3%>);
<%}%>
<%if(group.isFlag(139))
{%>
			var node_productstock=new WebFXTreeItem("电脑警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=130&showHide=1&productLineName=diannao&securityLine="+<%=ProductLineMap.SECURITYLINE4%>);
<%}%>
<%if(group.isFlag(140))
{%>
			var node_productstock=new WebFXTreeItem("服装警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=119,699,544,545,430,401,458,459&showHide=2&productLineName=fuzhuang&securityLine="+<%=ProductLineMap.SECURITYLINE5%>);
<%}%>
<%if(group.isFlag(141))
{%>
			var node_productstock=new WebFXTreeItem("鞋子警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=123,316,317&showHide=2&productLineName=xiezi&securityLine="+<%=ProductLineMap.SECURITYLINE6%>);
<%}%>
<%if(group.isFlag(366))
{%>
			var node_productstock=new WebFXTreeItem("鞋配件警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=143&showHide=2&productLineName=xiepeijian&securityLine="+<%=ProductLineMap.SECURITYLINE6%>);
<%}%>
<%if(group.isFlag(156))
{%>
			var node_productstock=new WebFXTreeItem("护肤品警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=6,151,183,184&showHide=1&productLineName=hufupin&securityLine="+<%=ProductLineMap.SECURITYLINE7%>);
<%}%>
<%if(group.isFlag(160))
{%>
			var node_productstock=new WebFXTreeItem("礼品警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=102&showHide=1&productLineName=lipin&securityLine="+<%=ProductLineMap.SECURITYLINE8%>);
<%}%>
<%if(group.isFlag(164))
{%>
			var node_productstock=new WebFXTreeItem("新奇特警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=163&showHide=1&productLineName=xinqite&securityLine="+<%=ProductLineMap.SECURITYLINE9%>);
<%}%>
<%if(group.isFlag(360))
{%>
			var node_productstock=new WebFXTreeItem("小家电警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=197&showHide=1&productLineName=xiaojiadian&securityLine="+<%=ProductLineMap.SECURITYLINE12%>);
<%}%>
<%if(group.isFlag(362))
{%>
			var node_productstock=new WebFXTreeItem("饰品警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=208&showHide=2&productLineName=shipin&securityLine="+<%=ProductLineMap.SECURITYLINE13%>);
<%}%>
<%if(group.isFlag(175))
{%>
			var node_productstock=new WebFXTreeItem("包警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=136,138&showHide=2&productLineName=bao&securityLine="+<%=ProductLineMap.SECURITYLINE10%>);
<%}%>
<%if(group.isFlag(393))
{%>
			var node_productstock=new WebFXTreeItem("手表警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=505&showHide=2&productLineName=shoubiao&securityLine="+<%=ProductLineMap.SECURITYLINE15%>);
<%}%>
<%if(group.isFlag(486))
{%>
			var node_productstock=new WebFXTreeItem("日用百货警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=752&showHide=2&productLineName=riyongbaihuo&securityLine="+<%=ProductLineMap.SECURITYLINE17%>);
<%}%>
<%if(group.isFlag(488))
{%>
			var node_productstock=new WebFXTreeItem("食品警戒产品");
			insertNode(node_stock1, node_productstock, "productstock.do?dateType=0&allProduct=1&areaId=1&catalogIds=803&showHide=2&productLineName=food&securityLine="+<%=ProductLineMap.SECURITYLINE18%>);
<%}%>
<%if(group.isFlag(318)){%>
	var node_productbatch_print=new WebFXTreeItem("产品批次条码打印");
	insertNode(node_stock1, node_productbatch_print, "batchBarcode/searchBathBarcode.jsp");

<%}}%>
<%}%>
<%if(group.isFlag(294)){%>
var node_scan_order=new WebFXTreeItem("扫描发货清单");
insertNode(node_stock1, node_scan_order, "orderStock/scanCheckOrderStock.jsp");
<%}%>
<%if(group.isFlag(294)){%>
var node_scan_order_customer=new WebFXTreeItem("打印客户信息");
insertNode(node_stock1, node_scan_order_customer, "barcodeManager/scanCheckCustomerInfo.jsp");
<%}%>
<%if(group.isFlag(386)){%>
var node_print_package=new WebFXTreeItem("打印包裹单");
insertNode(node_stock1, node_print_package, "printPackage.do?method=printPackage");
<%}%>
<%if(group.isFlag(387)){%>
var node_order_package=new WebFXTreeItem("扫描包裹单号");
insertNode(node_stock1, node_order_package, "orderPackage.do?method=orderPackage");
<%}%>
<%if(group.isFlag(381)){%>
var node_audit_package=new WebFXTreeItem("核对包裹");
insertNode(node_stock1, node_audit_package, "auditPackage.do?method=auditPackage");
<%}%>
<%/****************************************订单出库管理*****************************************/%>
<%if(group.isFlag(470)){%>
var node_mailing_batch=new WebFXTreeItem("订单出库管理");
node_root.add(node_mailing_batch);
var node_mailing_batch_list=new WebFXTreeItem("发货波次管理");
insertNode(node_mailing_batch,node_mailing_batch_list,"mailingBatch.do?method=mailingBatchList");
<%}%>
<%/***************************************订单配送管理**********************************************/%>
<%if(group.isFlag(476)){%>
var node_deliver_batch=new WebFXTreeItem("订单配送管理");
node_root.add(node_deliver_batch);
<%if(group.isFlag(468)){%>
var order_deliver_list=new WebFXTreeItem("订单配送管理");
insertNode(node_deliver_batch,order_deliver_list,"mailingBatch.do?method=orderDeliverList");
<%}%>
<%if(group.isFlag(469)){%>
var not_sign_in_package_list=new WebFXTreeItem("未签收包裹列表库");
insertNode(node_deliver_batch,not_sign_in_package_list,"mailingBatch.do?method=notSignInPackageList");
<%}%>
<%if(group.isFlag(464)){%>
var mailing_balance_order_return=new WebFXTreeItem("订单返库管理");
insertNode(node_deliver_batch,mailing_balance_order_return,"mailingBatch.do?method=mailingBalanceOrderReturn");
<%}%>
<%if(group.isFlag(473)){%>
var confirm_mailing_balance_order_return=new WebFXTreeItem("订单包裹退回确认");
insertNode(node_deliver_batch,confirm_mailing_balance_order_return,"mailingBatch.do?method=confirmMailingBalanceOrderReturn");
<%}%>
<%if(group.isFlag(463)){%>
var node_mailing_balance_auditing_list=new WebFXTreeItem("订单应收款结算管理");
insertNode(node_deliver_batch,node_mailing_balance_auditing_list,"mailingBatch.do?method=mailingBalanceAuditingList&balanceType=7");
<%}%>
<%}%>
<%/****************************************货位管理*****************************************/%>
<%if(group.isFlag(52)){%>
	var node_stock_pos=new WebFXTreeItem("货位管理");
	node_root.add(node_stock_pos);
		var node_stock_pos_property=new WebFXTreeItem("货位属性管理");
		node_stock_pos.add(node_stock_pos_property);
		var node_qualified_stock=new WebFXTreeItem("合格库作业管理");
		node_stock_pos.add(node_qualified_stock);
		var node_qualified_stock_detail=new WebFXTreeItem("合格库作业动态明细");
		insertNode(node_qualified_stock,node_qualified_stock_detail,"qualifiedStock.do?method=qualifiedStockDetail");
		var node_cargo_operation_process=new WebFXTreeItem("作业操作及时效设置");
		insertNode(node_qualified_stock,node_cargo_operation_process,"qualifiedStock.do?method=cargoOperationProcessList");
		var node_cargo_oper_log=new WebFXTreeItem("作业日志查询");
		insertNode(node_qualified_stock,node_cargo_oper_log,"qualifiedStock.do?method=cargoOperLog");
		var node_cargo_oper_fac=new WebFXTreeItem("交接阶段-设备扫描界面");
		insertNode(node_qualified_stock,node_cargo_oper_fac,"qualifiedStock.do?method=cargoOperFac");
		var node_stock_staff=new WebFXTreeItem("物流员工管理");
		insertNode(node_stock_pos,node_stock_staff,"qualifiedStock.do?method=staffManagement");
		<%if(group.isFlag(0)){%>
			var node_stock_pos_city_list=new WebFXTreeItem("城市列表");
			insertNode(node_stock_pos_property,node_stock_pos_city_list,"./cargoInfo.do?method=cargoInfoCityList");
			var node_stock_pos_area_list=new WebFXTreeItem("地区列表");
			insertNode(node_stock_pos_property,node_stock_pos_area_list,"./cargoInfo.do?method=cargoInfoAreaList");
		<%}%>
		<%if(group.isFlag(376)){%>
			var node_stock_pos_storage_list=new WebFXTreeItem("仓库列表");
			insertNode(node_stock_pos_property,node_stock_pos_storage_list,"./cargoInfo.do?method=cargoInfoStorageList");
			var node_stock_pos_storage_list=new WebFXTreeItem("仓库区域列表");
			insertNode(node_stock_pos_property,node_stock_pos_storage_list,"./cargoInfo.do?method=cargoInfoStockAreaList");
			var node_stock_pos_shelf_list=new WebFXTreeItem("货架列表");
			insertNode(node_stock_pos_property,node_stock_pos_shelf_list,"./cargoInfo.do?method=cargoInfoShelfList");
			var node_stock_pos_add_cargo=new WebFXTreeItem("批量添加货位");
			insertNode(node_stock_pos_property,node_stock_pos_add_cargo,"./cargoInfo.do?method=addCargoList");
			var node_stock_pos_open_cargo=new WebFXTreeItem("批量开通货位");
			insertNode(node_stock_pos_property,node_stock_pos_open_cargo,"./cargoInfo.do?method=openCargoList");
			var node_stock_pos_delete_cargo=new WebFXTreeItem("批量删除货位");
			insertNode(node_stock_pos_property,node_stock_pos_delete_cargo,"./cargoInfo.do?method=deleteCargoList");
		<%}%>
		<%if(group.isFlag(377)){%>
		var node_stock_pos_cargo_product=new WebFXTreeItem("货位绑定产品");
		insertNode(node_stock_pos_property,node_stock_pos_cargo_product,"./cargoInfo.do?method=toCargoProduct");
		<%}%>
		var node_change_cargo_property=new WebFXTreeItem("批量修改货位属性");
		insertNode(node_stock_pos_property,node_change_cargo_property,"./cargoInfo.do?method=changeCargoPropertyList");
		<%if(group.isFlag(378)){%>
			var node_stock_pos_fangcun=new WebFXTreeItem("芳村仓货位列表");
			insertNode(node_stock_pos,node_stock_pos_fangcun,"./cargoInfo.do?method=fangcunCargoList&areaId=1&storeType=0&status=0");
		<%}%>
			var node_stock_operUp_add_cargo=new WebFXTreeItem("产品上架");
			insertNode(node_stock_pos,node_stock_operUp_add_cargo,"./cargoUpOper.do?method=shelfUpList&status=1&status=2&status=3");			
			var node_stock_shelf_add_cargo=new WebFXTreeItem("产品下架");
			insertNode(node_stock_pos,node_stock_shelf_add_cargo,"./cargoDownShelf.do?method=shelfDownList&status=10&status=11&status=12");
	var node_cargo_refill_cargo=new WebFXTreeItem("货位补货");
	insertNode(node_stock_pos,node_cargo_refill_cargo,"./cargoOperation.do?method=refillCargoList&status=19&status=20&status=21");
	var node_cargo_refill_cargo=new WebFXTreeItem("散件区缺货列表");
	insertNode(node_stock_pos,node_cargo_refill_cargo,"./cargoOperation.do?method=lackProductList");
	var node_cargo_exchange_cargo=new WebFXTreeItem("货位间调拨");
	insertNode(node_stock_pos,node_cargo_exchange_cargo,"./cargoOperation.do?method=exchangeCargoList&status=28&status=29&status=30");
	<%if(group.isFlag(379)){%>
	var node_cargo_stock_card=new WebFXTreeItem("货位进销存");
	insertNode(node_stock_pos,node_cargo_stock_card,"./cargoInfo.do?method=cargoStockCard");
	<%}%>
	var node_cargo_inventory=new WebFXTreeItem("盘点导出");
	insertNode(node_stock_pos,node_cargo_inventory,"./cargoInfo.do?method=cargoInventory");
<%}%>
<%if(group.isFlag(409)){%>
var node_cargo_inventory=new WebFXTreeItem("盘点管理");
node_root.add(node_cargo_inventory);
<%if(group.isFlag(410)){%>
var node_cargo_inventory_add=new WebFXTreeItem("添加盘点作业单");
insertNode(node_cargo_inventory,node_cargo_inventory_add,"cargoInventory.do?method=addCargoInventory");
<%}%>
<%if(group.isFlag(411)){%>
var node_cargo_inventory_list=new WebFXTreeItem("盘点作业单列表");
insertNode(node_cargo_inventory,node_cargo_inventory_list,"cargoInventory.do?method=cargoInventoryList");
<%}%>
<%}%>
<%/****************************************WEB管理*****************************************/%>
<%--
<%
if(group.isFlag(22)) 
//	if(isSystem	//超级管理员
//	|| (isYunyingzhongxin && isGaojiAdmin)	//运营中心的高级管理员
//	|| (isPingtaiyunwei && (isGaojiAdmin || isAdmin))
//)
{
%>
		var wproduct_0=new WebFXTreeItem("web产品管理");
		node_root.add(wproduct_0);
		    //var node_all_product=new WebFXTreeItem("所有在架上产品");
			//insertNode(wproduct_0, node_all_product, "../wadmin/allProducts.do");
			//var node_all_product1=new WebFXTreeItem("所有已下架产品");
			//insertNode(wproduct_0, node_all_product1, "../wadmin/allProducts.do?status=120");
			//var node_new_product=new WebFXTreeItem("添加新产品");
			//insertNode(wproduct_0, node_new_product, "../wadmin/fproduct.do");
		
<logic:present name="wcatalogList" scope="request">
<logic:iterate id="element" name="wcatalogList">
<logic:equal name="element" property="hide" value="0">
		var wproduct_<bean:write name="element" property="id" />=new WebFXTreeItem("<bean:write name="element" property="name" />");
		insertNode(wproduct_<bean:write name="element" property="parentId" />,wproduct_<bean:write name="element" property="id" />,"../wadmin/products.do?catalogId=<bean:write name="element" property="id" />");
</logic:equal>
</logic:iterate>
</logic:present>

		var warticle_0=new WebFXTreeItem("web文章管理");
		node_root.add(warticle_0);
			var node_new_warticle=new WebFXTreeItem("添加新文章");
			insertNode(warticle_0, node_new_warticle, "../wadmin/aarticle.jsp");
<logic:present name="wcatalogList" scope="request">
<logic:iterate id="element" name="wcatalogList">
<logic:equal name="element" property="hide" value="0">
		var warticle_<bean:write name="element" property="id" />=new WebFXTreeItem("<bean:write name="element" property="name" />");
		insertNode(warticle_<bean:write name="element" property="parentId" />,warticle_<bean:write name="element" property="id" />,"../wadmin/articles.jsp?catalogId=<bean:write name="element" property="id" />");
</logic:equal>
</logic:iterate>
</logic:present>


		var wpool_0=new WebFXTreeItem("web池子管理");
		node_root.add(wpool_0);
<logic:present name="wpoolList" scope="request">
<logic:iterate id="element" name="wpoolList">
		var wpool_<bean:write name="element" property="id" />=new WebFXTreeItem("<bean:write name="element" property="name" />");
		insertNode(wpool_<bean:write name="element" property="parentId" />,wpool_<bean:write name="element" property="id" />,"../wadmin/pool.jsp?id=<bean:write name="element" property="id" />");
</logic:iterate>
</logic:present>

<%}%>--%>
<%if(group.isFlag(45) || group.isFlag(50) || group.isFlag(46) ){%>
		var node_sms=new WebFXTreeItem("短信");
		node_root.add(node_sms);
<%if(group.isFlag(45)){%>
			var mass_send=new WebFXTreeItem("短信群发");
			insertNode(node_sms,mass_send,"sms/send.jsp");
<%}%>
<%if(group.isFlag(50) ){%>
			var multi_send=new WebFXTreeItem("批量群发");
			insertNode(node_sms,multi_send,"sms/multiSend.jsp");
<%}%>
<%if(group.isFlag(385)){%>
var undeal_sms=new WebFXTreeItem("未处理订单群发短信");
insertNode(node_sms,undeal_sms,"./SMSAction.do?method=undealSMS");
<%}%>
<%if(group.isFlag(46)){%>
			var sms_log=new WebFXTreeItem("短信发送记录");
			insertNode(node_sms,sms_log,"debug/debugLogs.jsp");
<%}%>
		var mass_send=new WebFXTreeItem("用户短信回复记录");
		insertNode(node_sms,mass_send,"sms/orderReceive.jsp");
		var order_receive_analyse=new WebFXTreeItem("短信回复内容分析");
		insertNode(node_sms,order_receive_analyse,"sms/orderReceiveAnalyse.jsp");
<%if(group.isFlag(257)){%>
		var mass_send=new WebFXTreeItem("短信自动回复设置");
		insertNode(node_sms,mass_send,"./SMSAction.do?method=getSendMessageAuto");
<%}%>
		var order_receive_sms3=new WebFXTreeItem("查货短信记录");
		insertNode(node_sms,order_receive_sms3,"SMSAction.do?method=orderReceiveSMS3&status=0");
		var order_receive_sms2=new WebFXTreeItem("短信群发回复记录");
		insertNode(node_sms,order_receive_sms2,"SMSAction.do?method=orderReceiveSMS2&status=0");
		var ivr_delivery_send_sms = new WebFXTreeItem("IVR发货短信统计");
		insertNode(node_sms,ivr_delivery_send_sms,"sms/ivrDeliveryMessageCount.jsp");
<%}%>
<%--
<%if(group.isFlag(46)){%>
		var node_debug=new WebFXTreeItem("调试");
		node_root.add(node_debug);
			var debug_log=new WebFXTreeItem("短信发送记录");
			insertNode(node_debug,debug_log,"debug/debugLogs.jsp");
<%}%>
--%>
<%if(group.isFlag(47)){%>
		var node_chat=new WebFXTreeItem("即时通讯");
		node_root.add(node_chat);
			var chat_index=new WebFXTreeItem("通信信息");
			insertNode(node_chat,chat_index,"chat/index.html");
			var chat_manage=new WebFXTreeItem("聊天信息管理");
			insertNode(node_chat,chat_manage,"./ChatMessages.do?type=4");
			var quickAnswer_manage=new WebFXTreeItem("快捷回复管理");
			insertNode(node_chat,quickAnswer_manage,"./QuickAnswerList.do");
<%}%>
<%if(group.isFlag(64) || group.isFlag(65)){%>
var node_phone=new WebFXTreeItem("电话记录管理");
		node_root.add(node_phone);
<%if(group.isFlag(64)){%>
			var upload_phone=new WebFXTreeItem("上传电话记录");
			insertNode(node_phone,upload_phone,"sell/uploadPhoneLog.jsp");
<%}%>
<%if(group.isFlag(65)){%>
			var search_phone=new WebFXTreeItem("电话记录查询");
			insertNode(node_phone,search_phone,"./searchPhoneLog.do");
<%}%>
<%}%>
<%
if(group.isFlag(73)){ 
%>
		var adultAdmin=new WebFXTreeItem("前台管理");
		node_root.add(adultAdmin);
/*
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"adm/clearCache.jsp", "_blank");
*/
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台地图缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearMapCache.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台产品缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearProductCache.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台系统资源缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearSystemResourceCache.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台标准化信息缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearProductISOCache.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台满减折扣活动缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearProductPreferenceCache.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台树状页面ID商品ID映射缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearColumnProductMap.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台自动发放赠品缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearAutoPresentCache.jsp", "_blank");
		var adultAdmin_clearCache=new WebFXTreeItem("清理前台新商品信息缓存");
		insertNode(adultAdmin,adultAdmin_clearCache,"cache/clearSpiProductCache.jsp", "_blank");
<%}%>
<%
if(group.isFlag(85)){ 
%>
		var systemResAdmin=new WebFXTreeItem("系统资源管理");
		node_root.add(systemResAdmin);
<%
if(group.isFlag(85)){ 
%>
		var systemResAdmin_textRes=new WebFXTreeItem("调拨原因管理");
		insertNode(systemResAdmin,systemResAdmin_textRes,"system/textResList.jsp?type=1");
		var area_street_in=new WebFXTreeItem("导入区域街道信息");
		insertNode(systemResAdmin,area_street_in,"inputCityArea.jsp");
<%}%>
<%
if(group.isFlag(297)){ 
%>
		var barcode_create_manager=new WebFXTreeItem("条码生成规则管理");
		insertNode(systemResAdmin,barcode_create_manager,"javascript:void(0);");
		var product_catalog=new WebFXTreeItem("产品分类编号");
		insertNode(barcode_create_manager,product_catalog,"barcodeCreateManager.do?action=catalogs");
		var product_standards=new WebFXTreeItem("产品规格表");
		insertNode(barcode_create_manager,product_standards,"productStandards.do?action=standards");
		var product_weight=new WebFXTreeItem("产品重量对照表");
		insertNode(barcode_create_manager,product_weight,"productStandards.do?action=standardInfos&infoType=1");
		var product_color_table=new WebFXTreeItem("产品颜色对照表");
		insertNode(barcode_create_manager,product_color_table,"productStandards.do?action=standardInfos&infoType=2");
<%}%>
<%}%>
<%if(group.isFlag(71)){%>
		var test=new WebFXTreeItem("测试数据(测试专用)");
		node_root.add(test);
		var test_searchproduct = new WebFXTreeItem("产品查询");
		insertNode(test,test_searchproduct,"test/searchproduct.jsp");
		var historicalSub0 = new WebFXTreeItem("滞销手动统计");
		insertNode(test,historicalSub0,"test/historicalStatistics.jsp");
		var userOrderProduct = new WebFXTreeItem("订单产品销量手动统计");
		insertNode(test,userOrderProduct,"test/userOrderProductStat.jsp");
		var userOrderProduct = new WebFXTreeItem("产品库存手动统计");
		insertNode(test,userOrderProduct,"test/productStockStat.jsp");
		var sub01 = new WebFXTreeItem("订单商品分拆数据查询(订单)");
		insertNode(test,sub01,"test/userOrderProductList.jsp");
		var order_receive_sms_test=new WebFXTreeItem("短信回复信息添加(测试专用)");
		insertNode(test,order_receive_sms_test,"sms/testReceiveMessage.jsp");
		var test_mailing_balance = new WebFXTreeItem("结算数据");
		insertNode(test,test_mailing_balance,"balance/testMailingBalanceList.jsp");
		
		var finance_test = new WebFXTreeItem("财务相关");
		test.add(finance_test);
		var financeSub04 = new WebFXTreeItem("采购订单应付余额账龄数据");
		insertNode(finance_test,financeSub04,"finance/dataView5.jsp");
		var financeSub03 = new WebFXTreeItem("采购订单应付账款数据");
		insertNode(finance_test,financeSub03,"finance/dataView4.jsp");
		var financeSub02 = new WebFXTreeItem("采购订单预付款数据");
		insertNode(finance_test,financeSub02,"finance/dataView3.jsp");
		var financeSub01 = new WebFXTreeItem("结算分拆数据查询");
		insertNode(finance_test,financeSub01,"finance/dataView2.jsp");
		var financeSub00 = new WebFXTreeItem("订单商品分拆数据查询(财务)");
		insertNode(finance_test,financeSub00,"finance/dataView.jsp");
		var financeSub0 = new WebFXTreeItem("财务手动数据统计");
		insertNode(finance_test,financeSub0,"finance/financeStat.jsp");
		
		var supplier_test = new WebFXTreeItem("供货商相关");
		test.add(supplier_test);
		var supplierSub01 = new WebFXTreeItem("添加供货商");
		insertNode(supplier_test,supplierSub01,"testSupplier.do?method=supplierList");
		var supplierSub02 = new WebFXTreeItem("供货商账款信息");
		insertNode(supplier_test,supplierSub02,"test/supplierFinance.jsp");
		var supplierSub03 = new WebFXTreeItem("采购订单定金及付款查询");
		insertNode(supplier_test,supplierSub03,"test/searchBuyOrderFinanceDetail.jsp");
<%}%>
<%if(group.isFlag(71)){%>
		var private3 = new WebFXTreeItem("数据处理(内部专用)");
		node_root.add(private3);
		var repair_balance_data2=new WebFXTreeItem("结算数据修复2");
		insertNode(private3,repair_balance_data2,"dataRepair/repairBalanceData2.jsp");
		var repair_old_data=new WebFXTreeItem("采购入库旧数据处理1");
		insertNode(private3,repair_old_data,"dataRepair/repairOldBuyStockin.jsp");
		var add_product_stock=new WebFXTreeItem("添加商品库存记录");
		insertNode(private3,add_product_stock,"test/addProductStock.jsp");
		var transform_count_test = new WebFXTreeItem("同步商品采购中各种单子的转换次数");
		insertNode(private3,transform_count_test, "test/updateTransformCount.jsp");
<%}%>
<%if(group.isFlag(0)){%>
		var permission=new WebFXTreeItem("权限管理");
		insertNode(node_root,permission,"perm/index.jsp");
<%}%>
<%if(group.isFlag(0)){%>
var adult_admin=new WebFXTreeItem("后台管理");
node_root.add(adult_admin);
var adminCache=new WebFXTreeItem("后台缓存管理");
insertNode(adult_admin,adminCache,"adm/cacheAdmin.jsp", "_blank");
var productLineCache=new WebFXTreeItem("清理产品线权限缓存");
insertNode(adult_admin,productLineCache,"adm/productLineCacheClear.jsp", "_blank");
var productLineCache=new WebFXTreeItem("清理订单ID缓存");
insertNode(adult_admin,productLineCache,"adm/clearOrderIdCache.jsp", "_blank");
<%}%>
<%
if(group.isFlag(8)){ 
%>
		var permission=new WebFXTreeItem("伪订单查看");
		insertNode(node_root,permission,"new/orders.jsp");
<%}%>

<%/****************************************钱包管理*****************************************/%>
<%if(group.isFlag(90)||group.isFlag(96)||group.isFlag(172)||group.isFlag(177)||group.isFlag(178)||group.isFlag(179)||group.isFlag(180)||group.isFlag(187)){%>
	var wallet = new WebFXTreeItem("我的钱包管理");
	node_root.add(wallet);
	<%if(group.isFlag(90)){%>
		var searchWallet = new WebFXTreeItem("查找钱包");
		insertNode(wallet,searchWallet,"wallet/searchWallet.jsp");
	<%}%>
	<%if(group.isFlag(96)||group.isFlag(172)){%>
		var scoreList = new WebFXTreeItem("钱包进出帐统计");
		insertNode(wallet,scoreList,"wallet/scoreList.jsp");
	<%}%>
	<%if(group.isFlag(96)){%>
		var scoreList = new WebFXTreeItem("钱包充值成功统计");
		insertNode(wallet,scoreList,"wallet/instatistics.jsp");
	<%}%>
	<%if(group.isFlag(177)){%>
		var top100 = new WebFXTreeItem("钱包总额和百强排行榜");
		insertNode(wallet,top100,"wallet/top100.jsp");
	<%}%>
	<%if(group.isFlag(178)||group.isFlag(179)||group.isFlag(180)){%>
		var blacklist = new WebFXTreeItem("钱包黑名单");
		insertNode(wallet,blacklist,"wallet/blacklist.jsp");
	<%}%>
	<%if(group.isFlag(96)||group.isFlag(172)){%>
		var allAmount = new WebFXTreeItem("钱包余额统计");
		insertNode(wallet,allAmount,"wallet/allAmount.jsp");
	<%}%>
<%}%>
<%if(group.isFlag(173)||group.isFlag(174)){%>
		var hfzc = new WebFXTreeItem("话费_点卡");
		node_root.add(hfzc);
		<%if(group.isFlag(173)){%>
			var productsList = new WebFXTreeItem("话费产品列表");
			insertNode(hfzc,productsList,"hfzc/productsList.jsp");
			var update = new WebFXTreeItem("更新话费产品");
			insertNode(hfzc,update,"hfzc/update.jsp");
			var productsList1 = new WebFXTreeItem("点卡产品列表");
			insertNode(hfzc,productsList1,"dianka/productsList.jsp");
			var update1 = new WebFXTreeItem("点卡订单统计");
			insertNode(hfzc,update1,"dianka/orderStat.jsp","_blank");
		<%}%>
		<%if(group.isFlag(174)){%>
			var statistics = new WebFXTreeItem("订单统计");
			insertNode(hfzc,statistics,"hfzc/statistics.jsp");
			var orderList = new WebFXTreeItem("订单列表");
			insertNode(hfzc,orderList,"hfzc/orderList.jsp");
		<%}%>
<%}%>
<%if(group.isFlag(188)){%>
		var groupRate = new WebFXTreeItem("团购");
		node_root.add(groupRate);
		<%if(group.isFlag(189)){%>
			var searchGroupLeader = new WebFXTreeItem("团购验证信息管理");
			insertNode(groupRate,searchGroupLeader,"groupRate/searchGroupLeader.jsp");
		<%}%>
		<%if(group.isFlag(246)){%>
			var _groupRate = new WebFXTreeItem("团购管理");
			insertNode(groupRate,_groupRate,"groupRate.do?method=search");
		<%}%>
		<%if(group.isFlag(246)){%>
			var productCollection = new WebFXTreeItem("商品征集");
			insertNode(groupRate,productCollection,"groupRateCollection.do");
		<%}%>
<%}%>
<%if(group.isFlag(426)){%>
			var DMSpread = new WebFXTreeItem("DM业务推广");
			node_root.add(DMSpread);
			var batchMsg = new WebFXTreeItem("群发短信");
			insertNode(DMSpread,batchMsg,"dm/DMSendMsg.jsp");
			var checkResp = new WebFXTreeItem("查看短信回复");
			insertNode(DMSpread,checkResp,"dm/collectUserSms.jsp");
			var collectNew = new WebFXTreeItem("收集新用户");
			insertNode(DMSpread,collectNew,"dm/collectNewUsers.jsp");
<%}%>
<%if(group.isFlag(263)){%>
var autoOrder = new WebFXTreeItem("订单自动分配");
node_root.add(autoOrder);
<%if(group.isFlag(264)){%>
var nameMappingSetting = new WebFXTreeItem("客服名单设置");
insertNode(autoOrder,nameMappingSetting,"../flex/NameMappingSetting.html");
<%}%>
<%if(group.isFlag(265)){%>
var groupSetting = new WebFXTreeItem("人员分组设置");
insertNode(autoOrder,groupSetting,"../flex/GroupSetting.html");
<%}%>
<%if(group.isFlag(266)){%>
var assignRuleSetting = new WebFXTreeItem("班次及分配规则设置");
insertNode(autoOrder,assignRuleSetting,"../flex/OrderAssignRule.html");
<%}%>
<%if(group.isFlag(267)){%>
var schedulingTableSetting = new WebFXTreeItem("排班表设置");
insertNode(autoOrder,schedulingTableSetting,"../flex/SchedulingTableSetting.html");
<%}%>
<%if(group.isFlag(269)){%>
var switchStatus = new WebFXTreeItem("处理及分配方式设置");
insertNode(autoOrder,switchStatus,"../schedulingtablesetting/setting.jsp");
<%}%>
<%if(group.isFlag(271)){%>
var unDistOrder = new WebFXTreeItem("非无锡单未分配订单列表");
insertNode(autoOrder, unDistOrder,"../manualDist/unDistOrders.jsp?type=0");
var unDistOrder = new WebFXTreeItem("无锡单未分配订单列表");
insertNode(autoOrder, unDistOrder,"../manualDist/unDistOrders.jsp?type=1");
var distOrder = new WebFXTreeItem("非无锡单已分配订单列表");
insertNode(autoOrder, distOrder,"../manualDist/distOrders.jsp?type=0");
var distOrder = new WebFXTreeItem("无锡单已分配订单列表");
insertNode(autoOrder, distOrder,"../manualDist/distOrders.jsp?type=1");
<%}%>
<%if(group.isFlag(268)){%>
var distDetail = new WebFXTreeItem("当前排班指标设置");
insertNode(autoOrder, distDetail,"../manualDist/distDetail.jsp");
var distSettingLog = new WebFXTreeItem("排班指标设置日志");
insertNode(autoOrder, distSettingLog,"../schedulingtablesetting/settingLogs.jsp");
<%}%>
<%if(group.isFlag(270)){%>
var detail = new WebFXTreeItem("个人订单详情");
insertNode(autoOrder, detail,"../manualDist/detail.jsp");
<%}%>
<%}%>

<%if(group.isFlag(247)){%>
	var afterSale = new WebFXTreeItem("售后");
	node_root.add(afterSale);
	var addNewCallWorkOrder = new WebFXTreeItem("新建话务工单");
	insertNode(afterSale,addNewCallWorkOrder,"afterSales/searchHisUserRecorde.jsp");
	var acallWorkOrderManager = new WebFXTreeItem("话务工单管理");
	insertNode(afterSale,acallWorkOrderManager,"afterSales/callWorkManagement.jsp?firstIn=true");
	var afterSaleReport = new WebFXTreeItem("售后报表查询");
	insertNode(afterSale,afterSaleReport,"afterSales/callWorkReport.jsp");
	<%if(group.isFlag(200)){%>
		var addAfterSaleOrder = new WebFXTreeItem("生成售后单");
		insertNode(afterSale,addAfterSaleOrder,"afterSales/aorder.jsp","_blank");
	<%}%>
	<%if(group.isFlag(249)){%>
		var searchAfterSaleOrder = new WebFXTreeItem("查询售后单");
		insertNode(afterSale,searchAfterSaleOrder,"afterSales/sorder.jsp");
	<%}%>
	<%if(group.isFlag(248)){%>
		var afterSaleOrderList = new WebFXTreeItem("售后单列表");
		afterSale.add(afterSaleOrderList);
	<%}%>
	<%if(group.isFlag(202)){%>
		var list_1 = new WebFXTreeItem("售后联系中");
		insertNode(afterSaleOrderList,list_1,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_售后联系中%>");
	<%}%>
	<%if(group.isFlag(201)){%>
		var list_0 = new WebFXTreeItem("质检支撑时");
		insertNode(afterSaleOrderList,list_0,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_质检支撑时%>");
	<%}%>
	<%if(group.isFlag(203)){%>
		var list_2 = new WebFXTreeItem("包裹返途中");
		insertNode(afterSaleOrderList,list_2,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_包裹返途中%>");
	<%}%>
	<%if(group.isFlag(204)){%>
		var list_3 = new WebFXTreeItem("检测中");
		insertNode(afterSaleOrderList,list_3,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_检测中%>");
	<%}%>
	<%if(group.isFlag(205)){%>
		var list_4 = new WebFXTreeItem("质检确认");
		insertNode(afterSaleOrderList,list_4,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_质检确认%>");
	<%}%>
	<%if(group.isFlag(206)){%>
		var list_5 = new WebFXTreeItem("维修费用财务确认");
		insertNode(afterSaleOrderList,list_5,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_维修费用财务确认%>");
	<%}%>
	<%if(group.isFlag(207)){%>
		var list_6 = new WebFXTreeItem("实质处理");
		insertNode(afterSaleOrderList,list_6,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_实质处理%>");
	<%}%>
	<%if(group.isFlag(208)){%>
		var list_7 = new WebFXTreeItem("等待返厂");
		insertNode(afterSaleOrderList,list_7,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_等待返厂%>");
	<%}%>
	<%if(group.isFlag(209)){%>
		var list_8 = new WebFXTreeItem("返厂");
		insertNode(afterSaleOrderList,list_8,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_返厂%>");
	<%}%>
	<%if(group.isFlag(210)){%>
		var list_9 = new WebFXTreeItem("维修");
		insertNode(afterSaleOrderList,list_9,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_维修%>");
	<%}%>
	<%if(group.isFlag(211)){%>
		var list_10 = new WebFXTreeItem("返回用户");
		insertNode(afterSaleOrderList,list_10,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_返回用户%>");
	<%}%>
	<%if(group.isFlag(212)){%>
		var list_11 = new WebFXTreeItem("退货");
		insertNode(afterSaleOrderList,list_11,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_退货%>");
	<%}%>
	<%if(group.isFlag(213)){%>
		var list_12 = new WebFXTreeItem("退款财务确认");
		insertNode(afterSaleOrderList,list_12,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_退款财务确认%>");
	<%}%>
	<%if(group.isFlag(214)){%>
		var list_13 = new WebFXTreeItem("换货");
		insertNode(afterSaleOrderList,list_13,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_换货%>");
	<%}%>
	<%if(group.isFlag(215)){%>
		var list_14 = new WebFXTreeItem("差额财务确认");
		insertNode(afterSaleOrderList,list_14,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_差额财务确认%>");
	<%}%>
	<%if(group.isFlag(216)){%>
		var list_15 = new WebFXTreeItem("换货处理");
		insertNode(afterSaleOrderList,list_15,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_换货处理%>");
	<%}%>
	<%if(group.isFlag(217)){%>
		var list_16 = new WebFXTreeItem("换货订单管理");
	insertNode(afterSaleOrderList,list_16,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_处理售后换货%>");
	<%}%>
	<%if(group.isFlag(218)){%>
		var list_17 = new WebFXTreeItem("售后已完成");
		insertNode(afterSaleOrderList,list_17,"afterSales.do?method=list&status=<%=AfterSaleOrderBean.STATUS_售后已完成%>");
	<%}%>
	<%if(group.isFlag(219)){%>
		var disposeSuggest=new WebFXTreeItem("处理建议管理");
		insertNode(afterSale,disposeSuggest,"afterSales/disposeSuggestList.jsp?type=2");
	<%}%>
	<%if(group.isFlag(220)){%>
		var sqtStock = new WebFXTreeItem("质检库存查询");
		insertNode(afterSale,sqtStock,"afterSales.do?method=sqtstock");
	<%}%>
	<%if(group.isFlag(221)){%>
		var afterSaleRefundOrderList = new WebFXTreeItem("退换货订单列表");
		insertNode(afterSale,afterSaleRefundOrderList,"afterSales.do?method=nlist");
	<%}%>
	<%if(group.isFlag(224)){%>
		var sreorder = new WebFXTreeItem("退换货单查询");
		insertNode(afterSale,sreorder,"afterSales/sreorder.jsp");
	<%}%>
	<%if(group.isFlag(225)){%>
		var snorder = new WebFXTreeItem("换货订单查询");
		insertNode(afterSale,snorder,"afterSales/snorder.jsp");	
	<%}%>
	<%if(group.isFlag(226)||group.isFlag(227)||group.isFlag(223)){%>
		var fittingsManager = new WebFXTreeItem("配件管理");
		afterSale.add(fittingsManager);
		<%if(group.isFlag(226)){%>
			var apjproduct = new WebFXTreeItem("添加配件信息");
			insertNode(fittingsManager,apjproduct,"afterSales/aFittings.jsp");	
		<%}%>
		<%if(group.isFlag(223)){%>
			var pjLevelList = new WebFXTreeItem("配件分类列表");
			insertNode(fittingsManager,pjLevelList,"afterSales/Fittings.do?func=FittingsCatalogList&level=1");
		<%}%>
		<%if(group.isFlag(227)){%>
			var apjproductlist = new WebFXTreeItem("配件信息列表");
			insertNode(fittingsManager,apjproductlist,"afterSales/Fittings.do?func=showPGList");
		<%}%>
	<%}%>
		
		
	
<%}%>
<%if(group.isFlag(260)){%>
	var finance = new WebFXTreeItem("财务管理");
	node_root.add(finance);
	var financeReport = new WebFXTreeItem("财务报表");
	finance.add(financeReport);
	<%if(group.isFlag(273)){%>
		var financeSub1 = new WebFXTreeItem("入库价调整数据差值");
		insertNode(financeReport,financeSub1,"finance/stockBatchPriceDvalue.jsp");
	<%}%>
	<%if(group.isFlag(274)){%>
	var financeSub7 = new WebFXTreeItem("销售及成本明细");
	insertNode(financeReport,financeSub7,"finance/sellStat.jsp");
	<%}%>
	<%if(group.isFlag(275)){%>
	var financeSub2 = new WebFXTreeItem("报损报溢明细");
	insertNode(financeReport,financeSub2,"finance/bsbyStat.jsp");
	<%}%>
	<%if(group.isFlag(276)){%>
	var financeSub3 = new WebFXTreeItem("库存进销存");
	insertNode(financeReport,financeSub3,"finance/stockStat.jsp");
	<%}%>
	<%if(group.isFlag(277)){%>
	var financeSub4 = new WebFXTreeItem("产品回款明细");
	insertNode(financeReport,financeSub4,"finance/balanceStat.jsp");
	<%}%>
	<%if(group.isFlag(278)){%>
	var financeSub5 = new WebFXTreeItem("应收余额账龄");
	insertNode(financeReport,financeSub5,"finance/balanceStat2.jsp");
	<%}%>
	<%if(group.isFlag(279)){%>
	var financeSub6 = new WebFXTreeItem("应收余额类别账龄");
	insertNode(financeReport,financeSub6,"finance/balanceStat3.jsp");
	<%}%>
	<%if(group.isFlag(280)){%>
	var financeSub8 = new WebFXTreeItem("预付账款产品明细");
	insertNode(financeReport,financeSub8,"finance/buyStat.jsp");
	<%}%>
	<%if(group.isFlag(281)){%>
	var financeSub9 = new WebFXTreeItem("预付账款代理商明细");
	insertNode(financeReport,financeSub9,"finance/buyStat1.jsp");
	<%}%>
	<%if(group.isFlag(282)){%>
	var financeSub10 = new WebFXTreeItem("应付账款产品明细");
	insertNode(financeReport,financeSub10,"finance/buyStat2.jsp");
	<%}%>
	<%if(group.isFlag(283)){%>
	var financeSub11 = new WebFXTreeItem("应付账款代理商明细");
	insertNode(financeReport,financeSub11,"finance/buyStat3.jsp");
	<%}%>
	<%if(group.isFlag(284)){%>
	var financeSub13 = new WebFXTreeItem("应付余额账龄");
	insertNode(financeReport,financeSub13,"finance/buyStat5.jsp");
	<%}%>
	<%if(group.isFlag(285)){%>
	var financeSub12 = new WebFXTreeItem("应付余额类别账龄");
	insertNode(financeReport,financeSub12,"finance/buyStat4.jsp");
	<%}%>
	
	<%if(group.isFlag(457)){%>
	var financeAllSub = new WebFXTreeItem("库存账龄统计");
//	insertNode(financeReport,financeAllSub,"finance/productStockFinanceStat.jsp");
	insertNode(financeReport,financeAllSub,"finance/buyStat6.jsp");
	<%}%>
	
	<%if(group.isFlag(315)){%>
	var financeAuditing = new WebFXTreeItem("财务审核");
	finance.add(financeAuditing);
	<%if(group.isFlag(309)){%>
	var financeAuditing01 = new WebFXTreeItem("采购订单审核");
	insertNode(financeAuditing,financeAuditing01,"supplierFinance.do?method=BatchAuditApplicationPage&group=finance");
	<%}%>
	<%if(group.isFlag(356) || group.isFlag(357)) {%>
	var imprestAuditing = new WebFXTreeItem("备用金审核");
	insertNode(financeAuditing,imprestAuditing,"imprestFinance.do?method=ImprestFinanceApplicationList");
	var imprestAuditing = new WebFXTreeItem("备用金费用单审核");
	insertNode(financeAuditing,imprestAuditing,"supplierFinance.do?method=BatchAuditApplicationPage2&group=finance");
	var imprest_balance=new WebFXTreeItem("备用金余额查询");
	insertNode(financeAuditing, imprest_balance, "imprestFinance.do?method=ImprestBalance");
	<%}%>
	<%if(group.isFlag(423)){%>
		var financeFind = new WebFXTreeItem("财务查询");
		finance.add(financeFind);
			var findceFind1=new WebFXTreeItem("供应商查询");
			insertNode(financeFind, findceFind1, "supplierSummarizing.do?method=supplierTradeCollect&come=suppliercollect");
			<%if(group.isFlag(425)){%>
				var findceFind2=new WebFXTreeItem("采购订单应付金额明细查询");
				insertNode(financeFind, findceFind2, "supplier/orderDetailMoneyFind.jsp");
			<%}%>
	<%}%>
	<%if(group.isFlag(312)){%>
	var financeCalibrate = new WebFXTreeItem("校准单列表");
	insertNode(finance,financeCalibrate,"supplierFinance.do?method=financeCalibrateApplicationList");
	<%}%>
	
	<%}%>
	<%if(group.isFlag(414)){%>
	var financeCalibrate = new WebFXTreeItem("备用金费用单审核(tangx运费)");
	insertNode(finance,financeCalibrate,"supplierFinance.do?method=BatchAuditApplicationPage2&come=page3");
	<%}%>
<%}%>
<%if(group.isFlag(287)){%>
		var score = new WebFXTreeItem("打分管理");
		node_root.add(score);
		<%if(group.isFlag(288)){%>
			var score01 = new WebFXTreeItem("产品评分查询");
			insertNode(score,score01,"score/orderProductScore.jsp");
		<%}%>
		<%if(group.isFlag(289)){%>
			var score02 = new WebFXTreeItem("客服评分查询");
			insertNode(score,score02,"score/orderAdminScore.jsp");
		<%}%>
<%}%>
<%if(group.isFlag(427)){%>
		var promotionscore = new WebFXTreeItem("3月地推活动");
		node_root.add(promotionscore);
		var promotionscore01 = new WebFXTreeItem("验证");
		insertNode(promotionscore,promotionscore01,"promotion/checkCode.jsp");
		var promotionscore02 = new WebFXTreeItem("用户信息查询");
		insertNode(promotionscore,promotionscore02,"promotion/selOrder.jsp");
		var promotionscore02 = new WebFXTreeItem("兑奖统计");
		insertNode(promotionscore,promotionscore02,"promotion/exchangePrize.jsp");
	
<%}%>	
		//Write the hole Tree
//		document.write(node_root);
	node_root.write();
	node_root.expand();
</script>
</div>
<table class="msviLocalToolbar" id="popup" width="100"
	style="display: none; position:absolute; z-index:100;
	background-color:white; left: 231px; top: 349px; ">

<tr>
<td style="font-size: 12px;" onclick="hidePopup();refreshNode();" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';" title="刷新选择的节点">
<img src="js/images/refresh.png" width="16" height="16" align="absmiddle" />&nbsp;刷新</td>
</tr>
<tr>
<td style="font-size: 12px;" onclick="hidePopup();openNode();" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';" title="在新窗口里打开">
<img src="js/images/file.png" width="16" height="16" align="absmiddle" />&nbsp;新窗口打开</td>
</tr>
</table>
</div>
<script type="text/javascript">
/**
 * 注册事件
 */
try {
	var sssdf = document.getElementById('menuTree');
   document.getElementById('menuTree').oncontextmenu = oncontextmenu;
   document.onclick = OnDocumentClick;
} catch(ex) {
}

</script>
</body>
</html>