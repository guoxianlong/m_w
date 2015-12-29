package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IStockService;
import adultadmin.util.db.DbOperation;

public class SecondSortingSplitService extends BaseServiceImpl {

	
	public SecondSortingSplitService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public SecondSortingSplitService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	//分播订单商品信息
	public boolean addSortingBatchOrderProduct(SortingBatchOrderProductBean bean) {
		return addXXX(bean, "sorting_batch_order_product");
	}

	public List<SortingBatchOrderProductBean> getSortingBatchOrderProductList(String condition, int index, int count, String orderBy) {
		return (List<SortingBatchOrderProductBean>)getXXXList(condition, index, count, orderBy, "sorting_batch_order_product", "mmb.stock.stat.SortingBatchOrderProductBean");
	}
	
	public int getSortingBatchOrderProductCount(String condition) {
		return getXXXCount(condition, "sorting_batch_order_product", "id");
	}

	public SortingBatchOrderProductBean getSortingBatchOrderProduct(String condition) {
		return (SortingBatchOrderProductBean) getXXX(condition, "sorting_batch_order_product",
		"mmb.stock.stat.SortingBatchOrderProductBean");
	}

	public boolean updateSortingBatchOrderProduct(String set, String condition) {
		return updateXXX(set, condition, "sorting_batch_order_product");
	}

	public boolean deleteSortingBatchOrderProduct(String condition) {
		return deleteXXX(condition, "sorting_batch_order_product");
	}

	
	
	/**
	 * 事先保存所有的商品信息到表中
	 * @param iservice
	 * @param vorder
	 * @param orderProducts
	 * @param sboBean
	 * @return
	 * @throws SQLException 
	 */
	public String saveOrderProductInfo(IStockService iservice, voOrder vorder, List orderStockProducts, SortingBatchOrderBean sboBean) throws SQLException {

		String result = "SUCCESS";
		if( orderStockProducts == null || orderStockProducts.size() == 0 ) {
			result = "存在有的订单无出库商品的情况！";
		} else {
			int x = orderStockProducts.size();
			for(int i = 0; i < x; i++ ) {
				OrderStockProductBean ospBean = (OrderStockProductBean) orderStockProducts.get(i);
				List<OrderStockProductCargoBean> ospcList = iservice.getOrderStockProductCargoList("order_stock_product_id = " + ospBean.getId(), -1, -1, null);
				if (ospcList == null || ospcList.size() == 0) {
					
				} else {
					int y = ospcList.size();
					for(int j = 0; j < y; j++ ) {
						OrderStockProductCargoBean ospcBean = ospcList.get(j);
						ResultSet rs = iservice.getDbOp().executeQuery("select cargo_id from cargo_product_stock where id="+ospcBean.getCargoProductStockId());
						if (rs.next()) {
							SortingBatchOrderProductBean sbopBean = new SortingBatchOrderProductBean();
							sbopBean.setSortingBatchGroupId(sboBean.getSortingGroupId());
							sbopBean.setSortingBatchOrderId(sboBean.getId());
							sbopBean.setCount(ospBean.getStockoutCount());
							sbopBean.setCompleteCount(0);
							sbopBean.setOrderSkuCount(x);
							sbopBean.setProductId(ospBean.getProductId());
							sbopBean.setBoxCode(sboBean.getGroupCode());
							sbopBean.setIsDelete(sboBean.getDeleteStatus());
							sbopBean.setCargoId(rs.getInt(1));
							sbopBean.setSortingCount(0);
							if( !addSortingBatchOrderProduct(sbopBean)) {
								result = "保存订单信息时失败！";
							}
							rs.close();
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 保存货位商品是否异常
	 * @param siservice
	 * @param sbBean
	 * @return
	 * @throws SQLException 
	 * 根据波次分组
	 */
	public String saveSortingBatchGroupExceptionInfo(SortingInfoService siservice,SecondSortingSplitService secondSortingSplitService, SortingBatchBean sbBean) throws SQLException {
		String result = "SUCCESS";
		List<SortingBatchGroupBean> sbgBeanList = siservice.getSortingBatchGroupList("sorting_batch_id = " + sbBean.getId(), -1, -1, null);
		if (sbgBeanList != null && sbgBeanList.size() != 0) {
			int x = sbgBeanList.size();
			for (int i = 0 ; i < x ; i ++) {
				List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id = " + sbgBeanList.get(i).getId() +" group by product_id,cargo_id", -1, -1, null);
				if (list != null && list.size() != 0) {
					for (int j = 0; j < list.size(); j ++) {
						SortingBatchGroupExceptionBean sbgeBean = new SortingBatchGroupExceptionBean();
						sbgeBean.setSortingBatchGroupId(list.get(j).getSortingBatchGroupId());
						sbgeBean.setCargoId(list.get(j).getCargoId());
						sbgeBean.setProductId(list.get(j).getProductId());
						sbgeBean.setIsException(0);
						if (!siservice.addSortingBatchGroupExceptionInfo(sbgeBean)) {
							result = "保存货位商品异常信息出错";
							return result;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 的到商品应该放入的下一个 格子号
	 * @param product
	 * @param sbgBean
	 * @return
	 */
	public String getBoxCodeByProduct(voProduct product, SortingBatchGroupBean sbgBean ) {

		List<SortingBatchOrderProductBean> list = getSortingBatchOrderProductList("count > complete_count and product_id="+product.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, "is_delete asc, order_sku_count asc");
		if( list == null || list.size() == 0 ) {
			return "错误SKU";
		} else {
			SortingBatchOrderProductBean sbopBean = list.get(0);
			if( !updateSortingBatchOrderProduct("complete_count=complete_count + 1", "id=" + sbopBean.getId())) {
				return "Fail";
			}
			return sbopBean.getBoxCode();
		}
	}
	
	/**
	 * pda分拣，产品分配按照分拣的结果，不能大于已分拣量，按订单订购数量从小到大分配
	 * @param product
	 * @param sbgBean
	 * @return
	 */
	public String getBoxCodeBySortingCount(voProduct product, SortingBatchGroupBean sbgBean ) {

		List<SortingBatchOrderProductBean> list = getSortingBatchOrderProductList("sorting_count > complete_count and product_id="+product.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, "is_delete asc, order_sku_count asc");
		if( list == null || list.size() == 0 ) {
			return "错误SKU";
		} else {
			SortingBatchOrderProductBean sbopBean = list.get(0);
			if( !updateSortingBatchOrderProduct("complete_count=complete_count + 1", "id=" + sbopBean.getId())) {
				return "Fail";
			}
			return sbopBean.getBoxCode();
		}
	}
	/**
	 * 的到商品应该放入的下一个 格子号
	 * @param product
	 * @param sbgBean
	 * @return
	 */
	public String getOnlyBoxCodeByProduct(voProduct product, SortingBatchGroupBean sbgBean ) {
		
		String condition = " count > complete_count ";		
		// @mengqy 如果为pda分拣，分播时所分配的数量 依据PDA分拣量 来分配
		if(sbgBean.getSortingType() == 1){
			condition = " sorting_count > complete_count ";
		}
		
		List<SortingBatchOrderProductBean> list = getSortingBatchOrderProductList(condition + " and product_id="+product.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, "is_delete asc, order_sku_count asc");
		if( list == null || list.size() == 0 ) {
			return "错误SKU";
		} else {
			SortingBatchOrderProductBean sbopBean = list.get(0);
			return sbopBean.getBoxCode();
		}
	}

	/**
	 * 返回颜色 map key 对应 编号， value 对应的数字 表示  0 灰 1 红 2 橙 3 绿
	 * @param sortingBatchOrderList
	 * @param sbgBean
	 * @return
	 */
	public Map<String, Integer> getAllBoxColor(List sortingBatchOrderList,
			SortingBatchGroupBean sbgBean) {
		Map<String,Integer> map = new HashMap<String, Integer>();
		int x = sortingBatchOrderList.size();
		for( int i = 0; i < x; i ++ ) {
			SortingBatchOrderBean sboBean = (SortingBatchOrderBean)sortingBatchOrderList.get(i);
			List<SortingBatchOrderProductBean> list1 = getSortingBatchOrderProductList("complete_count <> 0 and sorting_batch_order_id = " + sboBean.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, null);
			List<SortingBatchOrderProductBean> list2 = getSortingBatchOrderProductList("complete_count <> count and sorting_batch_order_id = " + sboBean.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, null);
			
			int a = list1.size();
			int b = list2.size();
			if( sboBean.getDeleteStatus() == SortingBatchOrderBean.DELETE_STATUS1) {
				map.put(sboBean.getGroupCode(), 1);
				continue;
			}
			if( a == 0 ) {
				//没有开始的
				map.put(sboBean.getGroupCode(), 1);
				continue;
			} 
			if( b == 0 ) {
				map.put(sboBean.getGroupCode(), 3);
				continue;
			}
			map.put(sboBean.getGroupCode(), 2);
		}
		return map;
	}
	
	/**
	 * 得到code 对应的 index
	 * @param boxCode
	 * @return
	 */
	public int getIndexByBoxCode(String boxCode) {
		int result = -1;
		if( boxCode.matches("[A,B,C,D,E]{1}-[0-6]{1}") ){
			if( boxCode.startsWith("A") ) {
				int number = getSubNumberBoxCode(boxCode);
				result = number;
			} else if (boxCode.startsWith("B")) {
				int number = getSubNumberBoxCode(boxCode);
				result = 6 + number;
			} else if(boxCode.startsWith("C")) {
				int number = getSubNumberBoxCode(boxCode);
				result = 12 + number;
			} else if(boxCode.startsWith("D")) {
				int number = getSubNumberBoxCode(boxCode);
				result = 18 + number;
			} else if(boxCode.startsWith("E")) {
				int number = getSubNumberBoxCode(boxCode);
				result = 24 + number;
			}
		}
		return (result - 1);
	}

	private int getSubNumberBoxCode(String boxCode) {
		int result = -1;
		String number = boxCode.substring(2, 3);
		result = Integer.parseInt(number);
		return result;
	}
	
	/**
	 * 根据格子号得到这个格子中的商品数量
	 * @param boxCode
	 * @param sbgBean
	 * @return
	 */
	public int getProductCountInBox(String boxCode,
			SortingBatchGroupBean sbgBean) {
		int result = 0;
		List<SortingBatchOrderProductBean> list = getSortingBatchOrderProductList("box_code='"+ boxCode + "' and sorting_batch_group_id="+sbgBean.getId(), -1, -1, null);
		if( list == null || list.size() == 0 ) {
			
		} else {
			int x = list.size();
			for(int i = 0 ; i < x; i ++ ) {
				SortingBatchOrderProductBean sbopBean = list.get(i);
				result += sbopBean.getCompleteCount(); 
			}
		}
		return result;
	}

	/**
	 * 得到两种状态颜色的代码，组成字符串,用来决定 哪些灯 在最后闪烁。
	 */
	public Map<String, String> getAllColorStatusMap(List sortingBatchOrderList,
			SortingBatchGroupBean sbgBean) {
		Map<String,String> result = new HashMap<String, String>();
		String redOrange = "";
		String green = "";
		int x = sortingBatchOrderList.size();
		for( int i = 0; i < x; i ++ ) {
			SortingBatchOrderBean sboBean = (SortingBatchOrderBean)sortingBatchOrderList.get(i);
			List<SortingBatchOrderProductBean> list1 = getSortingBatchOrderProductList("complete_count <> 0 and sorting_batch_order_id = " + sboBean.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, null);
			List<SortingBatchOrderProductBean> list2 = getSortingBatchOrderProductList("complete_count <> count and sorting_batch_order_id = " + sboBean.getId() + " and sorting_batch_group_id="+sbgBean.getId(), -1, -1, null);
			
			int a = list1.size();
			int b = list2.size();
			if( sboBean.getDeleteStatus() == SortingBatchOrderBean.DELETE_STATUS1) {
				redOrange += this.getIndexByBoxCode(sboBean.getGroupCode()) + ";";
				continue;
			}
			if( a == 0 ) {
				//没有开始的
				redOrange += this.getIndexByBoxCode(sboBean.getGroupCode()) + ";";
				continue;
			} 
			if( b == 0 ) {
				green += this.getIndexByBoxCode(sboBean.getGroupCode()) + ";";
				continue;
			}
			redOrange += this.getIndexByBoxCode(sboBean.getGroupCode()) + ";";
		}
		redOrange = dealLastSplitMark(redOrange);
		green = dealLastSplitMark(green);
		
		result.put("redOrange", redOrange);
		result.put("green", green);
		return result;
	}

	/**
	 * 处理最后分号，或者逗号的方法
	 * @param target
	 * @return
	 */
	private String dealLastSplitMark(String target) {
		String result = target;
		if( result.length() > 0 ) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * 获得所有格子当前的商品数量的方法， 前提是在sortingBatchOrderList中是按照box_code由小到大排列的
	 * @param sortingBatchOrderList
	 * @param sbgBean
	 * @return
	 */
	public String getAllBoxPCountByIndexOrder(List sortingBatchOrderList,
			SortingBatchGroupBean sbgBean) {
		String result = "";
		int x = sortingBatchOrderList.size();
		for( int i = 0; i < x; i ++ ) {
			SortingBatchOrderBean sboBean = (SortingBatchOrderBean)sortingBatchOrderList.get(i);
			int perCount = getProductCountInBox(sboBean.getGroupCode(), sbgBean);
			result += perCount + ";";
		}
		result = dealLastSplitMark(result);
		return result;
	}

	
}
