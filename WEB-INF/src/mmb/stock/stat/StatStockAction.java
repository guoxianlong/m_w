package mmb.stock.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.actions.DispatchAction;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StatUtil;
import adultadmin.util.db.DbOperation;
public class StatStockAction extends DispatchAction {
	/**
	 * 
	 * 可调拨发货的订单列表 芳村地区
	 */
	public void fcEnchangeOrderList(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(550)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			String startDate = DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 30);
        	int startOrderId = StatUtil.getDayFirstOrderId(startDate);
        	String sql = "SELECT a.id,a.code,a.status,a.stockout_deal ,c.is_package,sum(b.count),sum(d.count) FROM user_order a " +
        			" left join user_order_product b on a.id=b.order_id " +
        			" left join user_order_present d on a.id=d.order_id " +
        			" left join product c on b.product_id =c.id " +
        			" where a.status=3 and a.stockout_deal<>2 and a.id>"+startOrderId +
        			" group by a.id having (sum(b.count)>1) or (sum(b.count)=1 and  c.is_package=1)or (sum(b.count)=1 and sum(d.count)>0) order by a.create_datetime desc";
			PreparedStatement pst = wareService.getDbOp().getConn().prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			List list = new ArrayList();
			while (rs.next()) {
				int flag = 0;
				int zcflag = 0;
				int fcflag = 0;
				HashMap orderMap = new HashMap();//key:订单编号  value:订单所对应的产品列表
				int order_id=rs.getInt("a.id");//订单ID
				String code=rs.getString("a.code");//订单编号
				List orderProductList = wareService.getOrderProducts(order_id);//该订单下面的商品列表
				List orderPresentList = wareService.getOrderPresents(order_id);//该订单下面的赠品列表
				orderProductList.addAll(orderPresentList);//将赠品列表中的商品加到商品列表中
				List productList = new ArrayList();//用来存放该订单下面的所有商品和赠品信息
				Iterator productIter = orderProductList.listIterator();//迭代该订单下面的所有商品和赠品
				while (productIter.hasNext()) {//迭代该订单下面的所有商品和赠品
					voOrderProduct vop = (voOrderProduct) productIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {//迭代套装中的产品列表
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempProduct.setPsList(psService.getProductStockList("product_id=" + ppBean.getProductId(), -1, -1, null));
							ProductStockBean fcProductBean = new ProductStockBean();
							ProductStockBean zcProductBean = new ProductStockBean();
							List psList = tempProduct.getPsList();
							for(int i=0;i<psList.size();i++){
								ProductStockBean ps = (ProductStockBean)psList.get(i);
								if(ps.getType() == 0 && ps.getArea() == 4){
									fcProductBean = ps;
								}else if(ps.getType() == 0 && ps.getArea() == 3){
									zcProductBean = ps;
								}
							}
							voOrderProduct countBean = vop;
							if (countBean != null) {
								countBean.setCount(vop.getCount());
	     						product.setOrderProduct(countBean);
	     					}
							fcProductBean.setProduct(tempProduct);
							productList.add(fcProductBean);
							if (fcProductBean != null &&  (countBean != null)  && zcProductBean!=null) {
								if (countBean != null) {
									if ((fcProductBean.getStock() + zcProductBean.getStock()) >= countBean.getCount() && (fcProductBean.getStock() < countBean.getCount() && zcProductBean.getStock() < countBean.getCount())) {
										flag = 1;
									}
									if ((fcProductBean.getStock() + zcProductBean.getStock()) < countBean.getCount()) {
										flag = 2;
										fcflag = 2;
										zcflag = 2;
										break;
									}
									if (fcProductBean.getStock() < countBean.getCount() && (zcProductBean.getStock() >= countBean.getCount())) {
										zcflag = 1;
									}
									if (zcProductBean.getStock() < countBean.getCount() && (fcProductBean.getStock() >= countBean.getCount())) {
										fcflag = 1;
									}
								}
//								
							   }
						}
					} else {
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
						ProductStockBean fcProductBean = new ProductStockBean();
						ProductStockBean zcProductBean = new ProductStockBean();
						List psList = product.getPsList();
						for(int i=0;i<psList.size();i++){
							ProductStockBean ps = (ProductStockBean)psList.get(i);
							if(ps.getType() == 0 && ps.getArea() == 4){
								fcProductBean = ps;
							}else if(ps.getType() == 0 && ps.getArea() == 3){
								zcProductBean = ps;
							}
						}
						voOrderProduct countBean = vop;
     					if (countBean != null) {
     						product.setOrderProduct(countBean);
     					}
						fcProductBean.setProduct(product);
						productList.add(fcProductBean);
						if (fcProductBean != null && (countBean != null) && zcProductBean!=null) {
							if (countBean != null) {
								if ((fcProductBean.getStock() + zcProductBean.getStock()) >= countBean.getCount() && (fcProductBean.getStock() < countBean.getCount() && zcProductBean.getStock() < countBean.getCount())) {
									flag = 1;
								}
								if ((fcProductBean.getStock() + zcProductBean.getStock()) < countBean.getCount()) {
									flag = 2;
									fcflag = 2;
									zcflag = 2;
									break;
								}
								if (fcProductBean.getStock() < countBean.getCount() && (zcProductBean.getStock() >= countBean.getCount())) {
									zcflag = 1;
								}
								if (zcProductBean.getStock() < countBean.getCount() && (fcProductBean.getStock() >= countBean.getCount())) {
									fcflag = 1;
								}
							}
						}
					}
					if(flag==2){
						break;
					}
				}
				if(flag==1||(zcflag==1&&fcflag==1)){
					orderMap.put(code, productList);
					list.add(orderMap);
				}
			}
			 request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
	}
	/**
	 * 
	 * 	可调拨发货的订单列表 增城地区
	 */
	public void zcEnchangeOrderList(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(550)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			String startDate = DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 30);
        	int startOrderId = StatUtil.getDayFirstOrderId(startDate);
        	String sql = "SELECT a.id,a.code,a.status,a.stockout_deal ,c.is_package,sum(b.count),sum(d.count) FROM user_order a " +
			" left join user_order_product b on a.id=b.order_id " +
			" left join user_order_present d on a.id=d.order_id " +
			" left join product c on b.product_id =c.id " +
			" where a.status=3 and a.stockout_deal in (0,1) and a.id>"+startOrderId +
			" group by a.id having (sum(b.count)>1) or (sum(b.count)=1 and  c.is_package=1)or (sum(b.count)=1 and sum(d.count)>0) order by a.create_datetime desc";
			PreparedStatement pst = wareService.getDbOp().getConn().prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			List list = new ArrayList();
			while (rs.next()) {
				HashMap orderMap = new HashMap();
				int order_id=rs.getInt("a.id");
				String code=rs.getString("a.code");
				List orderProductList = wareService.getOrderProducts(order_id);
				List orderPresentList = wareService.getOrderPresents(order_id);
				orderProductList.addAll(orderPresentList);
				List productList = new ArrayList();
				Iterator productIter = orderProductList.listIterator();
				int flag = 0;
				int zcflag = 0;
				int fcflag = 0;
				while (productIter.hasNext()) {//订单中的产品列表
					voOrderProduct vop = (voOrderProduct) productIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {//套装中的产品列表
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempProduct.setPsList(psService.getProductStockList("product_id=" + ppBean.getProductId(), -1, -1, null));
//							ProductStockBean fcProductBean = psService.getProductStock("product_id=" + product.getId() + " and type=0 and area=1");
//							ProductStockBean zcProductBean = psService.getProductStock("product_id=" + product.getId() + " and type=0 and area=3");
							ProductStockBean fcProductBean = new ProductStockBean();
							ProductStockBean zcProductBean = new ProductStockBean();
							List psList = tempProduct.getPsList();
							for(int i=0;i<psList.size();i++){
								ProductStockBean ps = (ProductStockBean)psList.get(i);
								if(ps.getType() == 0 && ps.getArea() == 4){
									fcProductBean = ps;
								}else if(ps.getType() == 0 && ps.getArea() == 3){
									zcProductBean = ps;
								}
							}
//							if(fcProductBean==null||zcProductBean==null){
//								break;
//							}
							voOrderProduct countBean = new voOrderProduct();
//							voOrderProduct presentBean = adminService.getOrderPresent(order_id, product.getCode());
							if (countBean != null) {
								countBean.setCount(vop.getCount());
	     						product.setOrderProduct(countBean);
	     					}
//	     					if (presentBean != null) {
//	     						presentBean.setCount(vop.getCount());
//	     						product.setOrderProduct(presentBean);
//	     					}
							tempProduct.setOrderProduct(countBean);
							zcProductBean.setProduct(tempProduct);
							productList.add(zcProductBean);
							if (fcProductBean != null && (countBean != null) && zcProductBean!=null) {
								if (countBean != null) {
									if ((fcProductBean.getStock() + zcProductBean.getStock()) >= countBean.getCount() && (fcProductBean.getStock() < countBean.getCount() && zcProductBean.getStock() < countBean.getCount())) {
										// 如果芳村和增城发货量之和大于该订单需求量，并且芳村和增城的发货量均小于订单需求量，则显示该订单
										flag = 1;
									}
									if ((fcProductBean.getStock() + zcProductBean.getStock()) < countBean.getCount()) {
										flag = 2;
										fcflag = 2;
										zcflag = 2;
										break;
									}
									if (fcProductBean.getStock() < countBean.getCount() && (zcProductBean.getStock() >= countBean.getCount())) {
										zcflag = 1;
									}
									if (zcProductBean.getStock() < countBean.getCount() && (fcProductBean.getStock() >= countBean.getCount())) {
										fcflag =1;
								}
								}
//								if (presentBean != null) {
//									if ((fcProductBean.getStock() + zcProductBean.getStock()) >= presentBean.getCount() && (fcProductBean.getStock() < presentBean.getCount() && zcProductBean.getStock() < presentBean.getCount())) {
//										flag = 1;
//									}
//									if ((fcProductBean.getStock() + zcProductBean.getStock()) < presentBean.getCount()) {
//										flag = 2;
//										fcflag = 2;
//										zcflag = 2;
//										break;
//									}
//									if (fcProductBean.getStock() < presentBean.getCount() && (zcProductBean.getStock() >= presentBean.getCount())) {
//										zcflag = 1;
//									}
//									if (zcProductBean.getStock() < presentBean.getCount() && (fcProductBean.getStock() >= presentBean.getCount())) {
//										fcflag = 1;
//									}
//								}
							}
						}
						
					} else {
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
//						ProductStockBean fcProductBean = psService.getProductStock("product_id=" + product.getId() + " and type=0 and area=1");
//						ProductStockBean zcProductBean = psService.getProductStock("product_id=" + product.getId() + " and type=0 and area=3");
						ProductStockBean fcProductBean = new ProductStockBean();
						ProductStockBean zcProductBean = new ProductStockBean();
						List psList = product.getPsList();
						for(int i=0;i<psList.size();i++){
							ProductStockBean ps = (ProductStockBean)psList.get(i);
							if(ps.getType() == 0 && ps.getArea() == 4){
								fcProductBean = ps;
							}else if(ps.getType() == 0 && ps.getArea() == 3){
								zcProductBean = ps;
							}
						}
//						voOrderProduct countBean = adminService.getOrderProduct("order_id="+order_id+" and product_id="+product.getId());
//						voOrderProduct presentBean = adminService.getOrderPresent(order_id, product.getCode());
						voOrderProduct countBean = vop;
						if(fcProductBean==null||zcProductBean==null){
							break;
						}
						if (countBean != null) {
     						product.setOrderProduct(countBean);
     					}
//     					if (presentBean != null) {
//     						product.setOrderProduct(presentBean);
//     					}
						zcProductBean.setProduct(product);
						productList.add(zcProductBean);
						if (fcProductBean != null &&  (countBean != null)  && zcProductBean!=null) {
							if (countBean != null) {
								if ((fcProductBean.getStock() + zcProductBean.getStock()) >= countBean.getCount() && (fcProductBean.getStock() < countBean.getCount() && zcProductBean.getStock() < countBean.getCount())) {
									// 如果芳村和增城发货量之和大于该订单需求量，并且芳村和增城的发货量均小于订单需求量，则显示该订单
									flag = 1;
								}
								if ((fcProductBean.getStock() + zcProductBean.getStock()) < countBean.getCount()) {
									flag = 2;
									fcflag = 2;
									zcflag = 2;
									break;
								}
								if (fcProductBean.getStock() < countBean.getCount() && (zcProductBean.getStock() >= countBean.getCount())) {
									zcflag = 1;
								}
								if (zcProductBean.getStock() < countBean.getCount() && (fcProductBean.getStock() >= countBean.getCount())) {
									fcflag =1;
							}
							}
//							if (presentBean != null) {
//								if ((fcProductBean.getStock() + zcProductBean.getStock()) >= presentBean.getCount() && (fcProductBean.getStock() < presentBean.getCount() && zcProductBean.getStock() < presentBean.getCount())) {
//									flag = 1;
//								}
//								if ((fcProductBean.getStock() + zcProductBean.getStock()) < presentBean.getCount()) {
//									flag = 2;
//									fcflag = 2;
//									zcflag = 2;
//									break;
//								}
//								if (fcProductBean.getStock() < presentBean.getCount() && (zcProductBean.getStock() >= presentBean.getCount())) {
//									zcflag = 1;
//								}
//								if (zcProductBean.getStock() < presentBean.getCount() && (fcProductBean.getStock() >= presentBean.getCount())) {
//									fcflag = 1;
//								}
//							}
						}
					}
					if(flag==2){
						break;
					}
				}
				if(flag==1||(zcflag==1&&fcflag==1)){
					orderMap.put(code, productList);
					list.add(orderMap);
				}
			}
			
			 request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
	}
	/**
	 * 
	 * 导出选中商品的调拨量
	 */
	public void enchangeExcel(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(550)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return;
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		String[] productIds = request.getParameterValues("checkbox");//查询
		String flag = request.getParameter("flag");
		HashMap productMap=new HashMap();//key:productId,value:voProduct(该产品的相关信息)
		HashMap countMap=new HashMap();//key:productId,value:count(订单中的产品数量)
		HashMap stockMap=new HashMap();//key:productId,value:stock(该产品芳村与增城的可发货量之和)
		try {
			for (int i = 0; i < productIds.length; i++) {
				String key = productIds[i];// productId-count-stock
				String[] key2 = key.split("-");
				productMap.put(key2[0], wareService.getProduct(Integer.parseInt(key2[0].toString())));// 记录PRODUCT_ID
				if (countMap.get(key2[0]) != null) {
					int a = Integer.parseInt(countMap.get(key2[0]).toString());
					int b = Integer.parseInt(key2[1].toString());
					int c = Integer.parseInt((key2[2]).toString());
					if ((a + b) > c) {// 需要判断缺货数量与库存的关系
						countMap.put(key2[0], key2[2]);
					}
					else {
						int d = Integer.parseInt(countMap.get(key2[0]).toString())+Integer.parseInt(key2[1].toString());
						countMap.put(key2[0], d+"");
					}
				} else {
					countMap.put(key2[0], key2[1]);
				}
				stockMap.put(key2[0], key2[2]);
			}
			request.setAttribute("flag", flag);
			request.setAttribute("pList", productMap);
			request.setAttribute("cList", countMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
	}
}
