/**
 * 
 */
package adultadmin.action.admin;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.stat.controller.SearchProductStockExcel;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

/**
 * @author shimingsong
 *
 */
public class AllProductsStockSubAmountAction  extends BaseAction{
	@SuppressWarnings("rawtypes")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		UserGroupBean group = adminUser.getGroup();
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService wareService = new WareService(dbOperation);
		boolean boo2 = group.isFlag(95);
		
		String statusIds = "-1";
		if(!"".equals(ProductLinePermissionCache.getProductLineSupplierIds(adminUser))){
			statusIds = ProductLinePermissionCache.getProductLineSupplierIds(adminUser);
		}
		List supplierList = wareService.getSelects("supplier_standard_info ", "where status = 1 and id in(" + statusIds + ")");
		request.setAttribute("supplierList", supplierList);
		if(request.getParameter("stockType") == null){	//以库类型参数判断是否为查询访问
			wareService.releaseAll();
			return  mapping.findForward(IConstants.SUCCESS_KEY);	
		}
		
		request.setCharacterEncoding("utf-8");
		
		int proxy = 0;	// 供应商id
		String suppliertext = StringUtil.convertNull(request.getParameter("suppliertext")).trim();	//供应商名称
		for (int i = 0; i < supplierList.size(); i++) {
			voSelect vo = (voSelect) supplierList.get(i);
			if(vo != null && suppliertext.equals(vo.getName())){
				proxy = vo.getId();
				break;
			}
		}
		request.setAttribute("proxy", String.valueOf(proxy));
		
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		int stockType =StringUtil.toInt(request.getParameter("stockType"));//库类型
		//System.out.println("---->"+stockType);
		int stockArea =StringUtil.toInt(request.getParameter("stockArea"));//库区域
		request.setAttribute("stockType", stockType);
		request.setAttribute("stockArea", stockArea);
		/*********************************************/
		List<StockAreaBean> stockAreaList = null;
		//获取所有的库地区，用来计算总金额与总库存
//		List<StockAreaBean> stockAreaAllWholeList = ProductStockBean.getStockAreaByType(stockType);
//		List<String> cargoAreaList = CargoDeptAreaService.getCargoDeptAreaList(request, stockType);
//		List<StockAreaBean> stockAreaAllList = new ArrayList<StockAreaBean>();
//		if(stockAreaAllWholeList!=null){
//			for(StockAreaBean bean:stockAreaAllWholeList){
//				for (String area : cargoAreaList) {
//					if (area.equals(bean.getId() + "")) {
//						stockAreaAllList.add(bean);
//						break;
//					}
//				}
//			}
//		}
		List<StockAreaBean> stockAreaAllList = ProductStockBean.getStockAreaByType(stockType);
		if(stockArea!=-1){
			String areaName = ProductStockBean.getAreaMap().get(stockArea);
			StockAreaBean bean = new StockAreaBean();
			bean.setId(stockArea);
			bean.setName(areaName);
			stockAreaList = new ArrayList<StockAreaBean>();
			stockAreaList.add(bean);
		}else{
			stockAreaList = stockAreaAllList;
		}
		request.setAttribute("header", stockAreaList);//表头
		/*********************************************/
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类3
		String toExcel =StringUtil.dealParam(request.getParameter("toExcel"));//编号
		
		//产品线权限限制
//		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
//		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
//		String catalogIdsTemp = "";
//		if(!catalogIds2.equals("")){
//			String[] splits = catalogIds2.split(",");
//			for(int i=0;i<splits.length;i++){
//				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
//				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
//					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
//				}
//			}
//			if(catalogIds1.endsWith(",")){
//				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
//			}
//		}
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(!"所有".equals(suppliertext) && !"".equals(suppliertext)){
			condition.append(" and d.id="+proxy);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
		if(stockType>=0){
			condition.append(" and ps.type="+stockType);
		}
		if(stockArea>=0){
			condition.append(" and ps.area="+stockArea);
		}

		//得出已处理中和待处理状态的产品集合
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		List list = null;
		try {
			list = wareService.getProductList2(condition.toString(), -1,-1, "a.id asc");
			
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));	
			}
			if(toExcel!=null&&toExcel.equals("to")){
				SearchProductStockExcel excel = new SearchProductStockExcel(SearchProductStockExcel.XSSF);
				List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
				ArrayList<String> header0 = new ArrayList<String>();//表头
				ArrayList<String> header2 = new ArrayList<String>();//表头
				//产品编号 	小店名称 	产品原名称
				
				header2.add("产品编号");
				header2.add("小店名称");
				header2.add("产品原名称");
				
				//field规则：库存数量用地区id，库存金额用地区id加_count
				if(stockArea==-1){
					for(StockAreaBean bean : stockAreaAllList){
						//先放库存数量
						header2.add(bean.getName());
						//判断是否有95的权限，然后显示库存对应的金额数
						if(boo2){
							header2.add("金额("+bean.getName()+")");
						}
					}
				}else{
					//先放库存数量
					header2.add(ProductStockBean.getAreaName(stockArea));
					//判断是否有95的权限，然后显示库存对应的金额数
					if(boo2){
						header2.add("金额("+ProductStockBean.getAreaName(stockArea)+")");
					}
				}
				header2.add("库存总数");
				if(boo2){
					header2.add("总金额");
				}
				header2.add("状态");
				header2.add("一级分类");
				header2.add("二级分类");
				header2.add("三级分类");
				
				List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
				ArrayList<String> body2 = new ArrayList<String>();//数据
				
				BigDecimal total2 = new BigDecimal(0);//记录总金额
				int stock_count = 0;//库存总数
				BigDecimal stock_amount2 = new BigDecimal(0);//总金额
				//city_count city_amount
				//计算总金额的中间变量  stockcounts  price5
				BigDecimal price5 = null;
				for(int i=0;i<list.size();i++){
					int city_count = 0;
					double city_amount = 0.0d;
					body2 = new ArrayList<String>();//数据
					voProduct item = (voProduct) list.get(i);
					
					body2.add(item.getCode());
					body2.add(item.getName());
					body2.add(item.getOriname());
					
					stock_count = 0;
					//1、先计算所选库类型的总数量 stock_count
					for(StockAreaBean bean:stockAreaAllList){
						stock_count = stock_count + this.getAvailableStock(item,bean.getId(),stockType);
					}
					if(stock_count<=0){
						for(StockAreaBean bean:stockAreaAllList){
							body2.add(0 + "");
							if(boo2)
								body2.add(0 + "");
						}
						body2.add(0 + "");
						if(boo2)
							body2.add(0 + "");
						body2.add(item.getStatusName());
						body2.add(item.getParentId1()==0  ? "无" : item.getParent1().getName());
						body2.add(item.getParentId2()==0  ? "无" : item.getParent2().getName());
						body2.add(item.getParentId3()==0  ? "无" : item.getParent3().getName());
						bodies.add(body2);
						continue;
					}
					price5 = new BigDecimal(item.getPrice5());
					//2 、 计算库存总金额 total2
					stock_amount2 = new BigDecimal(stock_count).multiply(price5).setScale(2, BigDecimal.ROUND_HALF_UP);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						total2 = total2.add(stock_amount2);
					}
					
					if(stockArea==-1){
						for(StockAreaBean bean:stockAreaAllList){
							
							city_count = this.getAvailableStock(item,bean.getId(),stockType);;
							if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
								city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							}
							body2.add(city_count+"");
							if(boo2)
								body2.add(city_amount+"");
						}
					}else{
						city_count = this.getAvailableStock(item,stockArea,stockType);;
						if(city_count<=0){
							body2.add(0 + "");
							if(boo2)
								body2.add(0 + "");
							body2.add(0 + "");
							if(boo2)
								body2.add(0 + "");
							body2.add(item.getStatusName());
							body2.add(item.getParentId1()==0  ? "无" : item.getParent1().getName());
							body2.add(item.getParentId2()==0  ? "无" : item.getParent2().getName());
							body2.add(item.getParentId3()==0  ? "无" : item.getParent3().getName());
							bodies.add(body2);
							continue;
						}
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body2.add(city_count+"");
						if(boo2)
							body2.add(city_amount+"");
					}
					body2.add(stock_count+"");
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						body2.add(stock_amount2.doubleValue()+"");
					}else{
						if(boo2)
							body2.add("0");
					}
					body2.add(item.getStatusName());
					body2.add(item.getParentId1()==0  ? "无" : item.getParent1().getName());
					body2.add(item.getParentId2()==0  ? "无" : item.getParent2().getName());
					body2.add(item.getParentId3()==0  ? "无" : item.getParent3().getName());
					bodies.add(body2);
				}
				if(group.isFlag(95)){
					header0.add("共有:"+list.size()+" 种产品, 总金额: "+total2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				} else {
					header0.add("共有:"+list.size()+" 种产品 ");
				}
				for(int i=0;i<header2.size()-1;i++){
					header0.add("");
				}
				headers.add(header0);
				/*允许合并列,下标从0开始，即0代表第一列*/
				List<Integer> mayMergeColumn = new ArrayList<Integer>();
				mayMergeColumn.add(0);
				mayMergeColumn.add(header0.size());
				excel.setMayMergeColumn(mayMergeColumn);
				
				/*允许合并行,下标从0开始，即0代表第一行*/
				List<Integer> mayMergeRow = new ArrayList<Integer>();
				mayMergeRow.add(0);
	            excel.setMayMergeRow(mayMergeRow);
				/*
				 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
				 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
				 * 
				 * */
				excel.setColMergeCount(header0.size());
//				headers.add(header);
				headers.add(header2);
				//调用填充表头方法
	            excel.buildListHeader(headers);
	            
	            //用于标记是否是表头数据,一般设置样式时是否表头会用到
	            excel.setHeader(false);
	            
	            //调用填充数据区方法
	            excel.buildListBody(bodies);
	            
	            //文件输出
	            excel.exportToExcel("分库库存查询导出_"+DateUtil.getNowDateStr(), response, "");
				return null;
			}else{
				BigDecimal total2 = new BigDecimal(0);
				int stock_count = 0;//库存总数
				BigDecimal stock_amount2 = new BigDecimal(0);//总金额
				//city_count city_amount
				//计算总金额的中间变量  stockcounts  price5
				BigDecimal price5 = null;
				//记录库类型、库区域下所有的地区数量与金额、总数量与总金额
				List<Object> stockCountAndPriceList = null;
				int count = 0;//记录显示多少条数据，有些需要过滤
				for(int i=0;i<list.size();i++){
					int city_count = 0;
					double city_amount = 0.0d;
					stockCountAndPriceList = new ArrayList<Object>();
					voProduct item = (voProduct) list.get(i);
//				Map<Integer,Map<Integer,Map<String,String>>> stockMap = voProduct.stockMap;
//				Map<Integer,Map<String,String>> areaMap = stockMap.get(stockType);
					stock_count = 0;
					//1、先计算所选库类型的总数量 stock_count
					for(StockAreaBean bean:stockAreaAllList){
						stock_count = stock_count + this.getAvailableStock(item,bean.getId(),stockType);
					}
					if(stock_count<=0){
						for(StockAreaBean bean:stockAreaAllList){
							stockCountAndPriceList.add(0);
							if(boo2)
								stockCountAndPriceList.add(0);
						}
						stockCountAndPriceList.add(0);
						if(boo2)
							stockCountAndPriceList.add(0);
						item.setStockCountAndPriceList(stockCountAndPriceList);
						count++;
						continue;
					}
					price5 = new BigDecimal(item.getPrice5());
					//2 、 计算库存总金额 total2
					stock_amount2 = new BigDecimal(stock_count).multiply(price5).setScale(2, BigDecimal.ROUND_HALF_UP);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						total2 = total2.add(stock_amount2);
					}
					
					if(stockArea==-1){
						for(StockAreaBean bean:stockAreaList){
							city_count = this.getAvailableStock(item,bean.getId(),stockType);
							if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
								city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							}
//						rows2.append("'"+bean.getId()+"':'").append(city_count+"',");
//						rows2.append("'"+bean.getId()+"_count':'").append(city_amount+"',");
							stockCountAndPriceList.add(city_count);
							if(boo2)
								stockCountAndPriceList.add(city_amount);
						}
					}else{
						city_count = this.getAvailableStock(item,stockArea,stockType);
						if(city_count<=0){
							stockCountAndPriceList.add(0);
							if(boo2)
								stockCountAndPriceList.add(0);
							continue;
						}
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
//					rows2.append("'"+stockArea+"':'").append(city_count+"',");
//					rows2.append("'"+stockArea+"_count':'").append(city_amount+"',");
						stockCountAndPriceList.add(city_count);
						if(boo2)
							stockCountAndPriceList.add(city_amount);
					}
					
//				rows2.append("'stock_count':'").append(stock_count+"',");
					stockCountAndPriceList.add(stock_count);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
//					rows2.append("'stock_amount':'").append(stock_amount2.doubleValue()+"',");
						stockCountAndPriceList.add(stock_amount2.doubleValue());
					}else{
						if(boo2)
							stockCountAndPriceList.add(0);
					}
//				rows2.append("'status_name':'").append(item.getStatusName()+"',")
//					.append("'parent_id1':'").append((item.getParentId1()==0  ? "无" : item.getParent1().getName())+"',")
//					.append("'parent_id2':'").append((item.getParentId2()==0  ? "无" : item.getParent2().getName())+"',")
//					.append("'parent_id3':'").append((item.getParentId3()==0  ? "无" : item.getParent3().getName())+"',")
//					.append("'query':'").append("<a href=\""+path+"/admin/productStock/stockCardList.jsp?productCode="+item.getCode()+"\" target=\"_blank\">查</a>'},");
					item.setStockCountAndPriceList(stockCountAndPriceList);
					count++;
				}
				
				request.setAttribute("productList", list);
				request.setAttribute("status",""+status);
				request.setAttribute("count", ""+count);
				request.setAttribute("totalPrice", ""+total2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				request.setAttribute("date","status="+status+"&proxy="+proxy+"&code="+code+"&name="+name+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3);
			}
		} finally {
			wareService.releaseAll();
			psService.releaseAll();
		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
	@SuppressWarnings("rawtypes")
	private int getAvailableStock(voProduct item,int areaId,int stockType){
		int stockCount = 0;
		int lockStockCount = 0;
		List psList = item.getPsList();
		if(psList != null){
			Iterator itr = psList.listIterator();
			while(itr.hasNext()){
				ProductStockBean ps = (ProductStockBean) itr.next();
				if(ps.getArea() == areaId && ps.getType() == stockType){
					if(ps.getType() != ProductStockBean.STOCKTYPE_QUALITYTESTING 
							&& ps.getType() != ProductStockBean.STOCKTYPE_NIFFER){
						stockCount += ps.getStock();
						lockStockCount += ps.getLockCount();
					}
				}
			}
		}
		return stockCount + lockStockCount;
	}
}
