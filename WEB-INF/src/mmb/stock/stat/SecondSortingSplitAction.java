package mmb.stock.stat;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.sale.order.stat.OrderAdminStatusLogBean;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class SecondSortingSplitAction extends DispatchAction {
	
	private static Object secondSortingLock = new Object();
	
	/**
	 * @name 波次商品分播
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward splitGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		//第一步  一进入这个方法 先找操作者信息
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(742);
		if( !permission ) {
			request.setAttribute("tip", "您没有分播墙操作的权限！");
			request.setAttribute("result", "failure");
		}
		
		CargoStaffBean csBean = user.getCargoStaffBean();
		if( csBean == null ) {
			request.setAttribute("tip", "没有找到物流员工信息，请尝试重新登录！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} 
		int submitType = StringUtil.toInt(request.getParameter("submitType"));
		//如果找到了 是物流员工  查看 是否有 他正在分拣的 波次
		WareService wareService = new WareService();
		SortingInfoService sortingInfoService = new SortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
		try {
			//synchronized (secondSortingLock) {
				SortingBatchGroupBean sbgBean = sortingInfoService.getSortingBatchGroupInfo("staff_id2 = " + csBean.getId() + " and status2 = " + 1);
				if( sbgBean != null ) {
					int finishNow = StringUtil.toInt(request.getParameter("finishNow"));
					int resetNow = StringUtil.toInt(request.getParameter("resetNow"));
					int cancel = StringUtil.toInt(request.getParameter("cancelFinish"));
					String productCode = StringUtil.convertNull(request.getParameter("productCode"));
					if (sbgBean.getCode().equals(productCode)) {
						finishNow = 1;
					}
					if( finishNow == -1 && resetNow == -1 ) {
						if( submitType != -1 ) {
							if( productCode.equals("") ) {
								request.setAttribute("tip", "未填写商品编号或条码！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							voProduct product = null;
							ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
							if( bBean == null || bBean.getBarcode() == null ) {
								product = wareService.getProduct(StringUtil.toSql(productCode));
							} else {
								product = wareService.getProduct(bBean.getProductId());
							}
							//根据编号 或条码 来找 对应的商品信息	
							if( product == null ) {
								request.setAttribute("tips", "未找到商品！");
								request.setAttribute("productName", "");
								request.setAttribute("boxCode", "无商品信息");
							} else {
								//判断是否是pda分拣
								if (sbgBean.getSortingType() == 1 ) {
									//判断是否是pda分拣已完成
									//产品分配按照分拣的结果，不能大于已分拣量，按订单订购数量从小到大分配
									if (sbgBean.getSortingStatus() == 2) {
										//确定这个商品应该放在哪个 格子中
										String boxCode = secondSortingSplitService.getBoxCodeBySortingCount(product, sbgBean);
										request.setAttribute("boxCode", boxCode);
										if( boxCode.equals("错误SKU") ) {
											request.setAttribute("tips", "该商品分拣错误！");
										} else if (boxCode.equals("Fail") ) {
											boxCode = "";
											request.setAttribute("tips", "数据库操作失败！");
										}
									} else {
										request.setAttribute("tips", "该波次商品pda分拣未完成！");
									}
								} else {
									//确定这个商品应该放在哪个 格子中
									String boxCode = secondSortingSplitService.getBoxCodeByProduct(product, sbgBean);
									request.setAttribute("boxCode", boxCode);
									if( boxCode.equals("错误SKU") ) {
										request.setAttribute("tips", "该商品分拣错误！");
									} else if (boxCode.equals("Fail") ) {
										boxCode = "";
										request.setAttribute("tips", "数据库操作失败！");
									}
								}
								String productName = product.getName();
								request.setAttribute("productName", productName);
							}
						}
						//不论这个商品是否扫成功了 都要重新计算各盒子的颜色
						List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
						int orderCount = sortingBatchOrderList.size();
						List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
						int needCount = 0;
						int completeCount = 0;
						for( int i = 0; i < list.size(); i ++ ) {
							SortingBatchOrderProductBean sbopBean = list.get(i);
							needCount += sbopBean.getCount();
							completeCount += sbopBean.getCompleteCount();
						}
						Map<String,Integer> map = secondSortingSplitService.getAllBoxColor(sortingBatchOrderList, sbgBean);
						request.setAttribute("colorMap", map);
						request.setAttribute("needCount", needCount);
						request.setAttribute("completeCount", completeCount);
						request.setAttribute("orderCount", orderCount);
						request.setAttribute("SortingBatchGroupBean", sbgBean);
						return new ActionForward("/admin/cargo/secondSortingSplit.jsp?type=2");
					}else if(cancel == 1){//取消结批
						if( sbgBean.getStatus2() != SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次未结批，不可以进行取消结批操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}else{
							boolean flag = false;
							//所有订单
							List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
							if(sortingBatchOrderList!=null && sortingBatchOrderList.size()>0){
								String orderCode = "";
								
								for(int i=0;i<sortingBatchOrderList.size();i++){
									orderCode = ((SortingBatchOrderBean)sortingBatchOrderList.get(i)).getOrderCode();
									//根据orderCOde判断是否通过复核
									flag=sortingInfoService.getOrderStatus(orderCode);
									if(flag){
										break;
									}
								}
							}
							if(!flag){
								//sortingInfoService.deleteSortBatchOrderProduct("sorting_batch_group_id="+sbgBean.getId());
								sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ", complete_datetime2='" + DateUtil.getNow() + "'", "id=" + sbgBean.getId());
							}
						}
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup&cancelFinish=-1");
					}else if( finishNow == 1 ) {
						//------------------------ 或者传来了 结批 信号 完成这个 波次
						if(user.getCargoStaffBean().getId()!=sbgBean.getStaffId2()){
							request.setAttribute("tip", "您无权结批此波次！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次已经结批，不可以再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//判断是否是pda分拣
						if (sbgBean.getSortingType() == 1 ) {
							//判断是否是pda分拣已完成
							//产品分配按照分拣的结果，不能大于已分拣量，按订单订购数量从小到大分配
							if (sbgBean.getSortingStatus() != 2) {
								request.setAttribute("tip", "该波次商品pda分拣未完成！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						
						if( !sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS2 + ", complete_datetime2='" + DateUtil.getNow() + "'", "id=" + sbgBean.getId())){
							request.setAttribute("tip", "数据库操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
						int size = sortingBatchOrderList.size();
						for (int i = 0; i < size; i ++) {
							SortingBatchOrderBean sboBean = (SortingBatchOrderBean) sortingBatchOrderList.get(i);
							OrderAdminStatusLogBean statusLog = new OrderAdminStatusLogBean();
							statusLog.setCreateDatetime(DateUtil.getNow());
							statusLog.setOrderId(sboBean.getOrderId());
							statusLog.setNewStatus(2);
							statusLog.setOriginStatus(1);
							statusLog.setType(3);
							statusLog.setUsername(user.getUsername());
							logService.addOrderAdminStatusLog(statusLog);
						}
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup&sortingGroupCode="+sbgBean.getCode()+"&submitType=1&finishNow=-1&resetNow=-1");
					} else if( resetNow == 1 ) {
						//----------------------重置信号
						if( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次已经结批，不可以再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						sortingInfoService.getDbOp().startTransaction();
						//再次添加的逻辑
						List groupOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+ sbgBean.getId(), -1, -1, "id asc");
						if( groupOrderList == null ) {
							sortingInfoService.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "没有找到波次关联订单信息！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
							if( list == null || list.size() == 0 ) {
								sortingInfoService.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "错误SKU！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							} else {
								for (int i = 0; i < list.size(); i ++) {
									SortingBatchOrderProductBean sbopBean = list.get(i);
									if( !secondSortingSplitService.updateSortingBatchOrderProduct("complete_count=0", "id=" + sbopBean.getId())) {
										sortingInfoService.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "更新失败！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
							}
							sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ",staff_id2 = " + csBean.getId(), "id=" + sbgBean.getId());
						}
						sortingInfoService.getDbOp().commitTransaction();
						sortingInfoService.getDbOp().getConn().setAutoCommit(true);
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup&resetNow=-1");
					}
				} else {
					String nextPage = "/admin/cargo/secondSortingSplit.jsp?type=2";
					String sortingGroupCode = StringUtil.convertNull(request.getParameter("sortingGroupCode"));
					String sortingGroupCode2 = StringUtil.convertNull(request.getParameter("sortingGroupCode2"));
//					String productCode = StringUtil.convertNull(request.getParameter("productCode"));
					int finishNow = StringUtil.toInt(request.getParameter("finishNow"));
					int resetNow = StringUtil.toInt(request.getParameter("resetNow"));
					int cancel = StringUtil.toInt(request.getParameter("cancelFinish"));
//					if( !productCode.equals("")) {
//						request.setAttribute("tip", "你不是这个波次的操作人，不可以操作！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
//					}
					
					if( finishNow != -1 ) {
						SortingBatchGroupBean sbgBean1 = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode2) + "' and status2 = " + SortingBatchGroupBean.SORTING_STATUS2 + " and staff_id2 = " + csBean.getId());
						if( sbgBean1 != null ) {
							request.setAttribute("tip", "该波次已结批！不可再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							request.setAttribute("tip", "你不是这个波次的操作人，不可以结批这个波次！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					 if(cancel == 1){//取消结批
						 SortingBatchGroupBean sbgBean3 = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode2) + "' and status2 = " + SortingBatchGroupBean.SORTING_STATUS2);
						if( sbgBean3.getStatus2() != SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次未结批，不可以进行取消结批操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}else{
							boolean flag = false;
							//所有订单
							List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean3.getId(), -1, -1, null);
							if(sortingBatchOrderList!=null && sortingBatchOrderList.size()>0){
								String orderCode = "";
								
								for(int i=0;i<sortingBatchOrderList.size();i++){
									orderCode = ((SortingBatchOrderBean)sortingBatchOrderList.get(i)).getOrderCode();
									//根据orderCOde判断是否通过复核
									flag=sortingInfoService.getOrderStatus(orderCode);
									if(flag){
										break;
									}
								}
							}
							if(!flag){
								//sortingInfoService.deleteSortBatchOrderProduct("sorting_batch_group_id="+sbgBean3.getId());
								sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ", complete_datetime2='" + DateUtil.getNow() + "'", "id=" + sbgBean3.getId());
								request.setAttribute("tips", "该波次已经取消结批！");
							}else{
								request.setAttribute("tip", "取消结批失败！有订单已经通过复核！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					 	return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup&cancelFinish=-1");
					}
					if( resetNow != -1 ) {
						SortingBatchGroupBean sbgBean2 = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode2) + "' and status2 = " + SortingBatchGroupBean.SORTING_STATUS2 + " and staff_id2 = " + csBean.getId());
						if( sbgBean2 != null ) {
							request.setAttribute("tip", "该波次已结批！不可再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							request.setAttribute("tip", "你不是这个波次的操作人，不可以重置这个波次！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					if( submitType == -1 ) {
						return new ActionForward("/admin/cargo/secondSortingSplit.jsp?type=1");
					}
					if( sortingGroupCode.equals("")) {
						request.setAttribute("tip", "未填写波次编号！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
					sbgBean = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode) + "'" );
					if( sbgBean == null ) {
						request.setAttribute("tip", "波次号有误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
//					if( sbgBean.getType1() == 0 ) {
//						request.setAttribute("tip", "该波次不是多sku订单波次！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
//					}
					//判断是否是pda分拣
					if (sbgBean.getSortingType() == 1 ) {
						//判断是否是pda分拣已完成
						//产品分配按照分拣的结果，不能大于已分拣量，按订单订购数量从小到大分配
						if (sbgBean.getSortingStatus() != 2) {
							request.setAttribute("tip", "该波次商品pda分拣未完成！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					//获得波次 号  找到对应的 波次
					if( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS0 ) {
						sortingInfoService.getDbOp().startTransaction();
						//2如果这个 波次 还未开始分播
						//直接建立关联 来操作
						List groupOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+ sbgBean.getId(), -1, -1, "id asc");
						if( groupOrderList == null ) {
							sortingInfoService.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "没有找到波次关联订单信息！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ",staff_id2 = " + csBean.getId() + ", receive_datetime2='" + DateUtil.getNow()+"'", "id=" + sbgBean.getId());
							request.setAttribute("SortingBatchGroupBean", sbgBean);
						}
						sortingInfoService.getDbOp().commitTransaction();
						sortingInfoService.getDbOp().getConn().setAutoCommit(true);
					} else if ( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS1 ) {
						//1如果 这个波次正在分播还未完成....
						request.setAttribute("tips", "这个波次已经分配了操作人！");
						request.setAttribute("SortingBatchGroupBean", sbgBean);
						nextPage = "/admin/cargo/secondSortingSplit.jsp?type=3";
					} else {
						request.setAttribute("tips", "该波次已经二次分拣完成！");
						/*request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);*/
						request.setAttribute("SortingBatchGroupBean", sbgBean);
						nextPage = "/admin/cargo/secondSortingSplit.jsp?type=3";
					}
					List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
					int orderCount = sortingBatchOrderList.size();
					List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
					int needCount = 0;
					int completeCount = 0;
					for( int i = 0; i < list.size(); i ++ ) {
						SortingBatchOrderProductBean sbopBean = list.get(i);
						needCount += sbopBean.getCount();
						completeCount += sbopBean.getCompleteCount();
					}
					Map<String,Integer> map = secondSortingSplitService.getAllBoxColor(sortingBatchOrderList, sbgBean);
					request.setAttribute("colorMap", map);
					request.setAttribute("needCount", needCount);
					request.setAttribute("completeCount", completeCount);
					request.setAttribute("orderCount", orderCount);
					return new ActionForward(nextPage);
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sortingInfoService.releaseAll();
			logService.releaseAll();
		}
		return null;
	}
	
	
	/**
	 * @name 波次商品分播--电子分播墙功能
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward splitGroup2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		//第一步  一进入这个方法 先找操作者信息
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(788);
		if( !permission ) {
			request.setAttribute("tip", "您没有电子分播墙操作的权限！");
			request.setAttribute("result", "failure");
		}
		
		CargoStaffBean csBean = user.getCargoStaffBean();
		if( csBean == null ) {
			request.setAttribute("tip", "没有找到物流员工信息，请尝试重新登录！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} 
		int submitType = StringUtil.toInt(request.getParameter("submitType"));
		//如果找到了 是物流员工  查看 是否有 他正在分拣的 波次
		WareService wareService = new WareService();
		SortingInfoService sortingInfoService = new SortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
		try {
			//synchronized (secondSortingLock) {
				SortingBatchGroupBean sbgBean = sortingInfoService.getSortingBatchGroupInfo("staff_id2 = " + csBean.getId() + " and status2 = " + 1);
				if( sbgBean != null ) {
					int finishNow = StringUtil.toInt(request.getParameter("finishNow"));
					int resetNow = StringUtil.toInt(request.getParameter("resetNow"));
					int cancel = StringUtil.toInt(request.getParameter("cancelFinish"));
					String productCode = StringUtil.convertNull(request.getParameter("productCode"));
					if (sbgBean.getCode().equals(productCode)) {
						finishNow = 1;
					}
					if( finishNow == -1 && resetNow == -1 ) {
						if( submitType != -1 ) {
							if( productCode.equals("") ) {
								request.setAttribute("tip", "未填写商品编号或条码！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							voProduct product = null;
							ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
							if( bBean == null || bBean.getBarcode() == null ) {
								product = wareService.getProduct(StringUtil.toSql(productCode));
							} else {
								product = wareService.getProduct(bBean.getProductId());
							}
							//根据编号 或条码 来找 对应的商品信息	
							if( product == null ) {
								request.setAttribute("tips", "未找到商品！");
								request.setAttribute("productName", "");
								request.setAttribute("boxCode", "无商品信息");
							} else {
								//判断是否是pda分拣
								if (sbgBean.getSortingType() == 1 ) {
									//判断是否是pda分拣已完成
									//产品分配按照分拣的结果，不能大于已分拣量，按订单订购数量从小到大分配
									if (sbgBean.getSortingStatus() == 2) {
										//确定这个商品应该放在哪个 格子中
										String boxCode = secondSortingSplitService.getBoxCodeBySortingCount(product, sbgBean);
										request.setAttribute("boxCode", boxCode);
										if( boxCode.equals("错误SKU") ) {
											request.setAttribute("tips", "该商品分拣错误！");
										} else if (boxCode.equals("Fail") ) {
											boxCode = "";
											request.setAttribute("tips", "数据库操作失败！");
										}
									}else {
										request.setAttribute("tips", "该波次商品pda分拣未完成！");
									}
								} else {
									//确定这个商品应该放在哪个 格子中
									String boxCode = secondSortingSplitService.getBoxCodeByProduct(product, sbgBean);
									request.setAttribute("boxCode", boxCode);
									if( boxCode.equals("错误SKU") ) {
										request.setAttribute("tips", "该商品分拣错误！");
									} else if (boxCode.equals("Fail") ) {
										boxCode = "";
										request.setAttribute("tips", "数据库操作失败！");
									}
									String productName = product.getName();
									request.setAttribute("productName", productName);
								}
							}
						}
						//不论这个商品是否扫成功了 都要重新计算各盒子的颜色
						List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, "group_code asc");
						int orderCount = sortingBatchOrderList.size();
						List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
						int needCount = 0;
						int completeCount = 0;
						for( int i = 0; i < list.size(); i ++ ) {
							SortingBatchOrderProductBean sbopBean = list.get(i);
							needCount += sbopBean.getCount();
							completeCount += sbopBean.getCompleteCount();
						}
						String allBoxPCount = secondSortingSplitService.getAllBoxPCountByIndexOrder(sortingBatchOrderList,sbgBean);
						Map<String,Integer> map = secondSortingSplitService.getAllBoxColor(sortingBatchOrderList, sbgBean);
						request.setAttribute("allBoxPCount", allBoxPCount);
						request.setAttribute("colorMap", map);
						request.setAttribute("needCount", needCount);
						request.setAttribute("completeCount", completeCount);
						request.setAttribute("orderCount", orderCount);
						request.setAttribute("SortingBatchGroupBean", sbgBean);
						return new ActionForward("/admin/cargo/secondSortingSplit2.jsp?type=2");
					}else if(cancel == 1){//取消结批
						if( sbgBean.getStatus2() != SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次未结批，不可以进行取消结批操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}else{
							boolean flag = false;
							//所有订单
							List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
							if(sortingBatchOrderList!=null && sortingBatchOrderList.size()>0){
								String orderCode = "";
								
								for(int i=0;i<sortingBatchOrderList.size();i++){
									orderCode = ((SortingBatchOrderBean)sortingBatchOrderList.get(i)).getOrderCode();
									//根据orderCOde判断是否通过复核
									flag=sortingInfoService.getOrderStatus(orderCode);
									if(flag){
										break;
									}
								}
							}
							if(!flag){
								//sortingInfoService.deleteSortBatchOrderProduct("sorting_batch_group_id="+sbgBean.getId());
								sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ", complete_datetime2='" + DateUtil.getNow() + "'", "id=" + sbgBean.getId());
							}
						}
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup2&cancelFinish=-1");
					}else if( finishNow == 1 ) {
						//------------------------ 或者传来了 结批 信号 完成这个 波次
						if(user.getCargoStaffBean().getId()!=sbgBean.getStaffId2()){
							request.setAttribute("tip", "您无权结批此波次！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次已经结批，不可以再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						if( !sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS2 + ", complete_datetime2='" + DateUtil.getNow() + "'", "id=" + sbgBean.getId())){
							request.setAttribute("tip", "数据库操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
						int size = sortingBatchOrderList.size();
						for (int i = 0; i < size; i ++) {
							SortingBatchOrderBean sboBean = (SortingBatchOrderBean) sortingBatchOrderList.get(i);
							OrderAdminStatusLogBean statusLog = new OrderAdminStatusLogBean();
							statusLog.setCreateDatetime(DateUtil.getNow());
							statusLog.setOrderId(sboBean.getOrderId());
							statusLog.setNewStatus(2);
							statusLog.setOriginStatus(1);
							statusLog.setType(3);
							statusLog.setUsername(user.getUsername());
							logService.addOrderAdminStatusLog(statusLog);
						}
						
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup2&sortingGroupCode="+sbgBean.getCode()+"&submitType=1&finishNow=-1&resetNow=-1&lightStep=3");
					} else if( resetNow == 1 ) {
						//----------------------重置信号
						if( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次已经结批，不可以再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						sortingInfoService.getDbOp().startTransaction();
						//再次添加的逻辑
						List groupOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+ sbgBean.getId(), -1, -1, "id asc");
						if( groupOrderList == null ) {
							sortingInfoService.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "没有找到波次关联订单信息！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
							if( list == null || list.size() == 0 ) {
								sortingInfoService.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "错误SKU！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							} else {
								for (int i = 0; i < list.size(); i ++) {
									SortingBatchOrderProductBean sbopBean = list.get(i);
									if( !secondSortingSplitService.updateSortingBatchOrderProduct("complete_count=0", "id=" + sbopBean.getId())) {
										sortingInfoService.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "更新失败！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
							}
							sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ",staff_id2 = " + csBean.getId(), "id=" + sbgBean.getId());
						}
						sortingInfoService.getDbOp().commitTransaction();
						sortingInfoService.getDbOp().getConn().setAutoCommit(true);
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup2&resetNow=-1");
					}
				} else {
					String nextPage = "/admin/cargo/secondSortingSplit2.jsp?type=2";
					String sortingGroupCode = StringUtil.convertNull(request.getParameter("sortingGroupCode"));
					String sortingGroupCode2 = StringUtil.convertNull(request.getParameter("sortingGroupCode2"));
//					String productCode = StringUtil.convertNull(request.getParameter("productCode"));
					int finishNow = StringUtil.toInt(request.getParameter("finishNow"));
					int resetNow = StringUtil.toInt(request.getParameter("resetNow"));
					int cancel = StringUtil.toInt(request.getParameter("cancelFinish"));
//					if( !productCode.equals("")) {
//						request.setAttribute("tip", "你不是这个波次的操作人，不可以操作！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
//					}
					
					if( finishNow != -1 ) {
						SortingBatchGroupBean sbgBean1 = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode2) + "' and status2 = " + SortingBatchGroupBean.SORTING_STATUS2 + " and staff_id2 = " + csBean.getId());
						if( sbgBean1 != null ) {
							request.setAttribute("tip", "该波次已结批！不可再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							request.setAttribute("tip", "你不是这个波次的操作人，不可以结批这个波次！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					if(cancel == 1){//取消结批
						SortingBatchGroupBean sbgBean3 = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode2) + "' and status2 = " + SortingBatchGroupBean.SORTING_STATUS2);
						if( sbgBean3.getStatus2() != SortingBatchGroupBean.SORTING_STATUS2 ) {
							request.setAttribute("tip", "这个波次未结批，不可以进行取消结批操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}else{
							boolean flag = false;
							//所有订单
							List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean3.getId(), -1, -1, null);
							if(sortingBatchOrderList!=null && sortingBatchOrderList.size()>0){
								String orderCode = "";
								
								for(int i=0;i<sortingBatchOrderList.size();i++){
									orderCode = ((SortingBatchOrderBean)sortingBatchOrderList.get(i)).getOrderCode();
									//根据orderCOde判断是否通过复核
									flag=sortingInfoService.getOrderStatus(orderCode);
									if(flag){
										break;
									}
								}
							}
							if(!flag){
								//sortingInfoService.deleteSortBatchOrderProduct("sorting_batch_group_id="+sbgBean3.getId());
								sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ", complete_datetime2='" + DateUtil.getNow() + "'", "id=" + sbgBean3.getId());
								request.setAttribute("tips", "该波次已经取消结批！");
							}else{
								request.setAttribute("tip", "取消结批失败！有订单已经通过复核！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						return new ActionForward("/admin/secondSortingSplitAction.do?method=splitGroup2&cancelFinish=-1");
					}
					if( resetNow != -1 ) {
						SortingBatchGroupBean sbgBean2 = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode2) + "' and status2 = " + SortingBatchGroupBean.SORTING_STATUS2 + " and staff_id2 = " + csBean.getId());
						if( sbgBean2 != null ) {
							request.setAttribute("tip", "该波次已结批！不可再操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							request.setAttribute("tip", "你不是这个波次的操作人，不可以重置这个波次！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					if( submitType == -1 ) {
						return new ActionForward("/admin/cargo/secondSortingSplit2.jsp?type=1");
					}
					if( sortingGroupCode.equals("")) {
						request.setAttribute("tip", "未填写波次编号！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
					sbgBean = sortingInfoService.getSortingBatchGroupInfo("code='" + StringUtil.toSql(sortingGroupCode) + "'" );
					if( sbgBean == null ) {
						request.setAttribute("tip", "波次号有误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
//					if( sbgBean.getType1() == 0 ) {
//						request.setAttribute("tip", "该波次不是多sku订单波次！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
//					}
					//判断是否是pda分拣
					if (sbgBean.getSortingType() == 1 ) {
						//判断是否是pda分拣已完成
						//产品分配按照分拣的结果，不能大于已分拣量，按订单订购数量从小到大分配
						if (sbgBean.getSortingStatus() != 2) {
							request.setAttribute("tip", "该波次商品pda分拣未完成！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					//获得波次 号  找到对应的 波次
					if( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS0 ) {
						sortingInfoService.getDbOp().startTransaction();
						//2如果这个 波次 还未开始分播
						//直接建立关联 来操作
						List groupOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+ sbgBean.getId(), -1, -1, "id asc");
						if( groupOrderList == null ) {
							sortingInfoService.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "没有找到波次关联订单信息！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							sortingInfoService.updateSortingBatchGroupInfo("status2=" + SortingBatchGroupBean.SORTING_STATUS1 + ",staff_id2 = " + csBean.getId() + ", receive_datetime2='" + DateUtil.getNow()+"'", "id=" + sbgBean.getId());
							request.setAttribute("SortingBatchGroupBean", sbgBean);
						}
						sortingInfoService.getDbOp().commitTransaction();
						sortingInfoService.getDbOp().getConn().setAutoCommit(true);
					} else if ( sbgBean.getStatus2() == SortingBatchGroupBean.SORTING_STATUS1 ) {
						//1如果 这个波次正在分播还未完成....
						request.setAttribute("tips", "这个波次已经分配了操作人！");
						request.setAttribute("SortingBatchGroupBean", sbgBean);
						nextPage = "/admin/cargo/secondSortingSplit2.jsp?type=3";
					} else {
						int lightStep = StringUtil.toInt(request.getParameter("lightStep"));
						if( lightStep == 3 ) {
							List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
							Map<String, String> colorStatusMap = secondSortingSplitService.getAllColorStatusMap(sortingBatchOrderList,sbgBean);
							request.setAttribute("greenIndexs", colorStatusMap.get("green"));
							request.setAttribute("redOrangeIndexs", colorStatusMap.get("redOrange"));
						}
						request.setAttribute("tips", "该波次已经二次分拣完成！");
						/*request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);*/
						request.setAttribute("SortingBatchGroupBean", sbgBean);
						nextPage = "/admin/cargo/secondSortingSplit2.jsp?type=3&lightStep=3";
					}
					
					
					List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, "group_code asc");
					int orderCount = sortingBatchOrderList.size();
					List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
					int needCount = 0;
					int completeCount = 0;
					for( int i = 0; i < list.size(); i ++ ) {
						SortingBatchOrderProductBean sbopBean = list.get(i);
						needCount += sbopBean.getCount();
						completeCount += sbopBean.getCompleteCount();
					}
					String allBoxPCount = secondSortingSplitService.getAllBoxPCountByIndexOrder(sortingBatchOrderList,sbgBean);
					Map<String,Integer> map = secondSortingSplitService.getAllBoxColor(sortingBatchOrderList, sbgBean);
					request.setAttribute("allBoxPCount", allBoxPCount);
					request.setAttribute("colorMap", map);
					request.setAttribute("needCount", needCount);
					request.setAttribute("completeCount", completeCount);
					request.setAttribute("orderCount", orderCount);
					return new ActionForward(nextPage);
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sortingInfoService.releaseAll();
			logService.releaseAll();
		}
		return null;
	}
	
	
	/**
	 * Ajax 方式调用只返回BoxCode的方法
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	public void justGetBoxCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding( "UTF-8");
		int submitType = StringUtil.toInt(request.getParameter("submitType"));
		//如果找到了 是物流员工  查看 是否有 他正在分拣的 波次
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		SortingInfoService sortingInfoService = new SortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			//synchronized (secondSortingLock) {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				if (user == null) {
					response.getWriter().write("{status:'fail', tip:'没有登录不可操作!',  boxCode:''}");
					return;
				}
				UserGroupBean group = user.getGroup();
				boolean permission = group.isFlag(788);
				if( !permission ) {
					response.getWriter().write("{status:'fail', tip:'您没有电子分播墙操作的权限！',  boxCode:''}");
					return;
				}
				
				CargoStaffBean csBean = user.getCargoStaffBean();
				if( csBean == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到物流员工信息，请尝试重新登录！',  boxCode:''}");
					return;
				}
				SortingBatchGroupBean sbgBean = sortingInfoService.getSortingBatchGroupInfo("staff_id2 = " + csBean.getId() + " and status2 = " + 1);
				if( sbgBean != null ) {
					String productCode = StringUtil.convertNull(request.getParameter("productCode"));
						if( submitType != -1 ) {
							if( productCode.equals("") ) {
								response.getWriter().write("{status:'fail', tip:'未填写商品编号或条码！',  boxCode:''}");
								return;
							}
							voProduct product = null;
							ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
							if( bBean == null || bBean.getBarcode() == null ) {
								product = wareService.getProduct(StringUtil.toSql(productCode));
							} else {
								product = wareService.getProduct(bBean.getProductId());
							}
							//根据编号 或条码 来找 对应的商品信息	
							if( product == null ) {
								response.getWriter().write("{status:'fail', tip:'未找到商品信息！',  boxCode:'无商品信息！'}");
								return;
							} else {
								//确定这个商品应该放在哪个 格子中
								String boxCode = secondSortingSplitService.getOnlyBoxCodeByProduct(product, sbgBean);
								request.setAttribute("boxCode", boxCode);
								if( boxCode.equals("错误SKU") ) {
									response.getWriter().write("{status:'fail', tip:'分拣错误！', boxCode:'" + boxCode + "'}");
									return;
								}
								int index = secondSortingSplitService.getIndexByBoxCode(boxCode);
								int boxPCount = secondSortingSplitService.getProductCountInBox(boxCode,sbgBean);
								String productName = product.getName();
								//不论这个商品是否扫成功了 都要重新计算各盒子的颜色
								/*List sortingBatchOrderList = sortingInfoService.getSortingBatchOrderList("sorting_group_id="+sbgBean.getId(), -1, -1, null);
								int orderCount = sortingBatchOrderList.size();
								List<SortingBatchOrderProductBean> list = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id=" + sbgBean.getId(), -1, -1, null);
								int needCount = 0;
								int completeCount = 0;
								for( int i = 0; i < list.size(); i ++ ) {
									SortingBatchOrderProductBean sbopBean = list.get(i);
									needCount += sbopBean.getCount();
									completeCount += sbopBean.getCompleteCount();
								}
								Map<String,Integer> map = secondSortingSplitService.getAllBoxColor(sortingBatchOrderList, sbgBean);
								request.setAttribute("colorMap", map);
								request.setAttribute("needCount", needCount);
								request.setAttribute("completeCount", completeCount);
								request.setAttribute("orderCount", orderCount);
								request.setAttribute("SortingBatchGroupBean", sbgBean);*/
								response.getWriter().write("{status:'success', boxCode:'"  + boxCode +  "', index:'" + index + "', productName:'" + claimsVerificationService.changeStringForJson(productName)  + "', boxPCount:'" + boxPCount + "'}");
								return;
							}
						}
				} else {
					response.getWriter().write("{status:'fail', tip:'当前没有正在分播的波次！'}");
					return;
				}
			//}
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			sortingInfoService.releaseAll();
		}
	}

}
