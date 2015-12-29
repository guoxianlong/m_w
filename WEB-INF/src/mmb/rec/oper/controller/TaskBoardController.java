package mmb.rec.oper.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


@Controller
@RequestMapping("/TaskBoardController")
public class TaskBoardController {
	@RequestMapping("/getWXTaskBoard")
	public String getWXTaskBoard (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int areaId = StringUtil.toInt(request.getParameter("area"));
		//昨日出库订单
		int beforeOutOrderCount = 0;
		//当前已复核的订单
		int scanAllOrderCount = 0;
		//已接到订单
		int receiveOrderCount = 0;
		//已撤销订单
		int cancelOrderCount = 0;
		//已导出订单
		int exportOrderCount = 0;
		//已复核订单
		int scanOrderCount = 0;
		//已分播订单
		int secondSplitOrderCount = 0;
		//已交接订单
		int joinOrderCount = 0;
		//待导出订单
		int waitExportOrderCount = 0;
		//待打印订单
		int waitPrintOrderCount = 0;
		//分拣中订单
		int sortingOrderCount = 0;
		//待交接订单
		int waitJoinOrderCount = 0;
		//分拣异常单
		int abnormalOrderCount = 0;
		
		//右侧
		//分拣组
		List sortingGroup = new ArrayList();
		//分播组
		List secondSplit = new ArrayList();
		//复核组
		List scanOrder = new ArrayList();
		//出库组
		List outCargo = new ArrayList();
		//上架组
		List upshelfGroup = new ArrayList();
		UserGroupBean group = user.getGroup();
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService=new WareService(dbOp);
		String nowDate = DateUtil.getNowDateStr();
		String nowBeginTime = nowDate + " 00:00:00";
		String nowEndTime = nowDate + " 23:59:59";
		try {
			//已接到订单
			ResultSet receiveOrderCountrs = wareService.getDbOp().executeQuery("select count(os.id) from order_stock os where os.stock_area = "+areaId+" and os.create_datetime  between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (receiveOrderCountrs.next()) {
				receiveOrderCount = receiveOrderCountrs.getInt(1);
			}
			receiveOrderCountrs.close();
			request.setAttribute("receiveOrderCount", "" + receiveOrderCount);
			//已撤销订单
			ResultSet cancelOrderCountrs = wareService.getDbOp().executeQuery("select count(os.id) from order_stock os where os.status =" + OrderStockBean.STATUS4 + " and os.stock_area = "+areaId+" and os.last_oper_time  between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (cancelOrderCountrs.next()) {
				cancelOrderCount = cancelOrderCountrs.getInt(1);
			}
			cancelOrderCountrs.close();
			request.setAttribute("cancelOrderCount", "" + cancelOrderCount);
			//已导出订单
			ResultSet exportOrderCountrs = wareService.getDbOp().executeQuery("select count(sbo.id) from sorting_batch_order sbo,sorting_batch sb where sb.id=sbo.sorting_batch_id and sb.storage = "+areaId+" and sb.create_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (exportOrderCountrs.next()) {
				exportOrderCount = exportOrderCountrs.getInt(1);
			}
			exportOrderCountrs.close();
			request.setAttribute("exportOrderCount", "" + exportOrderCount);
			//已复核订单
			ResultSet scanOrderCountrs = wareService.getDbOp().executeQuery("select count(ap.id) from audit_package ap, order_stock os where ap.order_id=os.order_id and os.status<>"+OrderStockBean.STATUS4+" and  os.stock_area = "+areaId+" and ap.check_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (scanOrderCountrs.next()) {
				scanOrderCount = scanOrderCountrs.getInt(1);
			}
			scanOrderCountrs.close();
			request.setAttribute("scanOrderCount", "" + scanOrderCount);
			//已分播订单
			ResultSet secondSplitOrderCountrs = wareService.getDbOp().executeQuery("select count(sbo.id) from sorting_batch_order sbo, sorting_batch_group sbg where sbg.id=sbo.sorting_group_id and sbg.storage = "+areaId+"  and sbg.status2 = "+ SortingBatchGroupBean.SORTING_STATUS2 + " and sbg.receive_datetime2 between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (secondSplitOrderCountrs.next()) {
				secondSplitOrderCount = secondSplitOrderCountrs.getInt(1);
			}
			secondSplitOrderCountrs.close();
			request.setAttribute("secondSplitOrderCount", "" + secondSplitOrderCount);
			//已交接订单
			ResultSet joinOrderCountrs = wareService.getDbOp().executeQuery("select count(mbp.id) from mailing_batch mb, mailing_batch_package mbp where mb.id=mbp.mailing_batch_id and mb.area = "+areaId+" and mb.status<>0 and mbp.create_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (joinOrderCountrs.next()) {
				joinOrderCount = joinOrderCountrs.getInt(1);
			}
			joinOrderCountrs.close();
			request.setAttribute("joinOrderCount", "" + joinOrderCount);
			//当前已复核的订单
			scanAllOrderCount = scanOrderCount;
			request.setAttribute("scanAllOrderCount", "" + scanAllOrderCount);
			//待导出订单
			ResultSet waitExportOrderCountrs = wareService.getDbOp().executeQuery("select count(os.id) from order_stock os where os.status =" + OrderStockBean.STATUS2 + " and os.stock_area = "+areaId+" and os.create_datetime  between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (waitExportOrderCountrs.next()) {
				waitExportOrderCount = waitExportOrderCountrs.getInt(1);
			}
			waitExportOrderCountrs.close();
			request.setAttribute("waitExportOrderCount", "" + waitExportOrderCount);
			//待打印订单
			ResultSet waitPrintOrderCountrs = wareService.getDbOp().executeQuery("select count(sbo.id) from sorting_batch_order sbo, sorting_batch sb where sb.id=sbo.sorting_batch_id and sb.storage = "+areaId+" and sbo.status in ("+SortingBatchOrderBean.STATUS1+","+SortingBatchOrderBean.STATUS0+") and sbo.delete_status=0 and sb.create_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (waitPrintOrderCountrs.next()) {
				waitPrintOrderCount = waitPrintOrderCountrs.getInt(1);
			}
			waitPrintOrderCountrs.close();
			request.setAttribute("waitPrintOrderCount", "" + waitPrintOrderCount);
			//分拣中订单
			ResultSet sortingOrderCountrs = wareService.getDbOp().executeQuery("select count(sbo.id) from sorting_batch_order sbo, sorting_batch_group sbg where sbg.id=sbo.sorting_group_id and sbg.storage = "+areaId+" and sbo.status="+SortingBatchOrderBean.STATUS2+" and  sbo.delete_status=0 and sbg.status2 = " + SortingBatchGroupBean.SORTING_STATUS0 + " and sbg.receive_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (sortingOrderCountrs.next()) {
				sortingOrderCount = sortingOrderCountrs.getInt(1);
			}
			sortingOrderCountrs.close();
			request.setAttribute("sortingOrderCount", "" + sortingOrderCount);
			//待交接订单
			ResultSet waitJoinOrderCountrs = wareService.getDbOp().executeQuery("select count(mbp.id) from mailing_batch mb, mailing_batch_package mbp where mb.id=mbp.mailing_batch_id and mb.area = "+areaId+" and mb.status=0 and mbp.create_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (waitJoinOrderCountrs.next()) {
				waitJoinOrderCount = waitJoinOrderCountrs.getInt(1);
			}
			waitJoinOrderCountrs.close();
			request.setAttribute("waitJoinOrderCount", "" + waitJoinOrderCount);
			//分拣异常单
			ResultSet abnormalOrderCountrs = wareService.getDbOp().executeQuery("select count(sa.id) from sorting_abnormal sa where sa.ware_area = "+areaId+" and sa.create_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'");
			if (abnormalOrderCountrs.next()) {
				abnormalOrderCount = abnormalOrderCountrs.getInt(1);
			}
			abnormalOrderCountrs.close();
			request.setAttribute("abnormalOrderCount", "" + abnormalOrderCount);
			String beforeDate = DateUtil.getBackFromDate(nowDate, 1);
			String beforeBeginTime = beforeDate + " 00:00:00";
			String beforeEndTime = beforeDate + " 23:59:59";
			//昨日出库订单
			ResultSet beforeOutOrderCountrs = wareService.getDbOp().executeQuery("select count(mbp.id) from mailing_batch mb, mailing_batch_package mbp where mb.id=mbp.mailing_batch_id and mb.area = "+areaId+"  and mb.status<>0  and mbp.create_datetime between '" + beforeBeginTime + "' and '" + beforeEndTime + "'");
			if (beforeOutOrderCountrs.next()) {
				beforeOutOrderCount = beforeOutOrderCountrs.getInt(1);
			}
			beforeOutOrderCountrs.close();
			request.setAttribute("beforeOutOrderCount", "" + beforeOutOrderCount);
			
			//右侧
			//分拣量
			ResultSet sortingGroupRS = wareService.getDbOp().executeQuery("select count(sbge.id) count,sbg.staff_name from sorting_batch_group_exception sbge, sorting_batch_group sbg where sbge.sorting_batch_group_id=sbg.id and sbg.storage = "+areaId+" and sbg.receive_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "' group by sbg.staff_name order by count desc;");
			//完成量
			int sortingGroupCount = 0;
			//人数
			int sortingGroupPeopleCount = 0;
			while (sortingGroupRS.next()) {
				sortingGroupCount += sortingGroupRS.getInt(1);
				if ( sortingGroupPeopleCount == 0 ) {
					sortingGroup.add(sortingGroupRS.getString(2));
					sortingGroup.add(sortingGroupRS.getInt(1));
				}
				sortingGroupPeopleCount ++;
			}
			if (sortingGroup.size() <= 0) {
				sortingGroup.add("无");
				sortingGroup.add(0);
				sortingGroup.add(0);
				sortingGroup.add(0);
			} else {
				sortingGroup.add(sortingGroupCount);
				sortingGroup.add(sortingGroupPeopleCount);
			}
			request.setAttribute("sortingGroup", sortingGroup);
			//分播量
			ResultSet secondSplitRS = wareService.getDbOp().executeQuery("select count(sbo.id) count,cs.name from sorting_batch_order sbo, sorting_batch_group sbg,cargo_staff cs where sbo.sorting_group_id=sbg.id and sbg.staff_id2=cs.id and sbg.storage = "+areaId+" and sbg.receive_datetime2 between '" + nowBeginTime + "' and '" + nowEndTime + "' group by cs.name order by count desc;");
			//完成量
			int secondSplitCount = 0;
			//人数
			int secondSplitPeopleCount = 0;
			while (secondSplitRS.next()) {
				secondSplitCount += secondSplitRS.getInt(1);
				if ( secondSplitPeopleCount == 0 ) {
					secondSplit.add(secondSplitRS.getString(2));
					secondSplit.add(secondSplitRS.getInt(1));
				}
				secondSplitPeopleCount ++;
			}
			if (secondSplit.size() <= 0) {
				secondSplit.add("无");
				secondSplit.add(0);
				secondSplit.add(0);
				secondSplit.add(0);
			} else {
				secondSplit.add(secondSplitCount);
				secondSplit.add(secondSplitPeopleCount);
			}
			request.setAttribute("secondSplit", secondSplit);
			//复核组
			ResultSet scanOrderRS = wareService.getDbOp().executeQuery("select count(ap.id) count, case when cs.name is null then ap.check_user_name else cs.name end from audit_package ap left join cargo_staff cs on ap.check_user_name=cs.user_name, order_stock os  where ap.order_id=os.order_id and os.status<>"+OrderStockBean.STATUS4+" and  os.stock_area = "+areaId+" and ap.check_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "' group by ap.check_user_name  order by count desc");
			//完成量
			int scanOrderGroupCount = 0;
			//人数
			int scanOrderPeopleCount = 0;
			while (scanOrderRS.next()) {
				scanOrderGroupCount += scanOrderRS.getInt(1);
				if ( scanOrderPeopleCount == 0 ) {
					scanOrder.add(scanOrderRS.getString(2));
					scanOrder.add(scanOrderRS.getInt(1));
				}
				scanOrderPeopleCount ++;
			}
			if (scanOrder.size() <= 0) {
				scanOrder.add("无");
				scanOrder.add(0);
				scanOrder.add(0);
				scanOrder.add(0);
			} else {
				scanOrder.add(scanOrderGroupCount);
				scanOrder.add(scanOrderPeopleCount);
			}
			request.setAttribute("scanOrder", scanOrder);
			//出库组
			ResultSet outCargoRS = wareService.getDbOp().executeQuery("select count(mbp.id) count, case when cs.name is null then mb.create_admin_name else cs.name end from mailing_batch mb left join cargo_staff cs on mb.create_admin_name=cs.user_name left join mailing_batch_package mbp on mb.id=mbp.mailing_batch_id  where mb.area = "+areaId+" and mb.create_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "' group by mb.create_admin_name order by count desc");
			//完成量
			int outCargoGroupCount = 0;
			//人数
			int outCargoPeopleCount = 0;
			while (outCargoRS.next()) {
				outCargoGroupCount += outCargoRS.getInt(1);
				if ( outCargoPeopleCount == 0 ) {
					outCargo.add(outCargoRS.getString(2));
					outCargo.add(outCargoRS.getInt(1));
				}
				outCargoPeopleCount ++;
			}
			if (outCargo.size() <= 0) {
				outCargo.add("无");
				outCargo.add(0);
				outCargo.add(0);
				outCargo.add(0);
			} else {
				outCargo.add(outCargoGroupCount);
				outCargo.add(outCargoPeopleCount);
			}
			request.setAttribute("outCargo", outCargo);
			//上架组
			ResultSet upshelfGroupRS = wareService.getDbOp().executeQuery("select count(distinct co.id) count, case when cs.name is null then co.complete_user_name else cs.name end from cargo_operation co left join cargo_staff cs on co.complete_user_name=cs.user_name,cargo_operation_cargo coc,cargo_info ci where co.id=coc.oper_id and  ci.whole_code=coc.in_cargo_whole_code and ci.area_id ="+areaId+" and  co.type = 0 and co.complete_datetime between '" + nowBeginTime + "' and '" + nowEndTime + "'  and co.status in (7,8) group by co.complete_user_name order by count desc");
			//完成量
			int upshelfGroupGroupCount = 0;
			//人数
			int upshelfGroupPeopleCount = 0;
			while (upshelfGroupRS.next()) {
				upshelfGroupGroupCount += upshelfGroupRS.getInt(1);
				if ( upshelfGroupPeopleCount == 0 ) {
					upshelfGroup.add(upshelfGroupRS.getString(2));
					upshelfGroup.add(upshelfGroupRS.getInt(1));
				}
				upshelfGroupPeopleCount ++;
			}
			if (upshelfGroup.size() <= 0) {
				upshelfGroup.add("无");
				upshelfGroup.add(0);
				upshelfGroup.add(0);
				upshelfGroup.add(0);
			} else {
				upshelfGroup.add(upshelfGroupGroupCount);
				upshelfGroup.add(upshelfGroupPeopleCount);
			}
			request.setAttribute("upshelfGroup", upshelfGroup);
			request.getRequestDispatcher("/admin/rec/oper/wxTaskBoard.jsp").forward(request, response);
			return null;
		} catch (Exception e ) {
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		} finally {
			wareService.releaseAll();
		}
	}
}
