package mmb.stock.stat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ProductWarePropertyAction extends DispatchAction {
	
	private static byte[] lock = {};
	
	/**
	 * 
	 * 得到质检排班计划列表
	 * @return
	 */
	public ActionForward getCheckStaffWorkPlanInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String monthDate = StringUtil.toSql(StringUtil.convertNull(request.getParameter("monthDate")));
		String areaId = StringUtil.toSql(StringUtil.convertNull(request.getParameter("areaId")));
		if(areaId == null || "".equals(areaId)) {
			List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			if(areaList != null && areaList.size()>0){
				areaId = (String)areaList.get(0);
			}else{
				request.setAttribute("tip", "权限遮罩功能显示您当前没有任何地区的权限！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}
		List list = new ArrayList();
		if( monthDate == null || monthDate.equals("")) {
			Date now = new Date();
			monthDate = DateUtil.formatDate(now, "yyyy-MM");
		}
		try {
			int pointCut = monthDate.indexOf("-");
			int year = Integer.parseInt( monthDate.substring(0,pointCut));
			int month = Integer.parseInt( monthDate.substring(pointCut + 1));
			Calendar cal = Calendar.getInstance(); 
			cal.set(Calendar.YEAR,year); 
			cal.set(Calendar.MONTH, month - 1);//Java月份才0开始算 
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE); 
			for( int i = 1; i < dateOfMonth + 1; i ++ ) {
				String temp = year +"-"+ String.format("%02d", new Object[]{new Integer(month)}) + "-" + String.format("%02d", new Object[]{new Integer(i)});
				list.add(temp);
			}
		} catch (Exception e ) {
			request.setAttribute("tip", "所传日期参数有误");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		List dayList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			//根据list 遍历拿出对应的记录 如果没有就给哥空的
			for( int i = 0; i < list.size(); i ++ ) {
				String temp = (String) list.get(i);
		 		String time = temp + " 00:00:00";
				CheckStaffBean checkStaffBean = (CheckStaffBean)statService.getCheckStaff("date='" + time + "' and area_id=" + Integer.parseInt(areaId));
				//把值赋给 bean
				if( checkStaffBean == null ) {
					checkStaffBean = new CheckStaffBean();
					checkStaffBean.setDate(time);
					checkStaffBean.setMonthCount(0);
					checkStaffBean.setDayCount(0);
				}
				checkStaffBean.setNikeName(temp);
				dayList.add(checkStaffBean);
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("dateList", dayList);
		
		ActionForward actionForward = new ActionForward(); 
		actionForward.setPath("/admin/cargo/checkStaffWorkPlanInfo.jsp?monthDate=" + monthDate + "&areaId=" + areaId); 
		return actionForward; 
	}
	
	/**
	 * 
	 * 根据填写的月在编人数修改记录
	 * @return
	 */
	public ActionForward addMonthStaffCount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean addMonthRight = group.isFlag(691);
		if( !addMonthRight ) {
			request.setAttribute("tip", "您没有编辑月在编人数的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String monthDate = StringUtil.toSql(StringUtil.convertNull(request.getParameter("monthDate2")));
		int monthCount = StringUtil.parstInt(request.getParameter("mStaffCount"));
		int areaId = StringUtil.parstInt(request.getParameter("areaIdHidden"));
		List list = new ArrayList();
		if( monthDate == null || monthDate.equals("")) {
			Date now = new Date();
			monthDate = DateUtil.formatDate(now, "yyyy-MM");
		}
		try {
			int pointCut = monthDate.indexOf("-");
			int year = Integer.parseInt( monthDate.substring(0,pointCut));
			int month = Integer.parseInt( monthDate.substring(pointCut + 1));
			Calendar cal = Calendar.getInstance(); 
			cal.set(Calendar.YEAR,year); 
			cal.set(Calendar.MONTH, month - 1);//Java月份才0开始算 
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE); 
			for( int i = 1; i < dateOfMonth + 1; i ++ ) {
				String temp = year +"-"+ String.format("%02d", new Object[]{new Integer(month)}) + "-" + String.format("%02d", new Object[]{new Integer(i)});
				list.add(temp);
			}
		} catch (Exception e ) {
			request.setAttribute("tip", "所传日期参数有误");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String tip = "";
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				//加上时间验证的 地方  不可以修改 这个月以前的 时间的记录
				if(productWarePropertyService.isChangeAvailForMonth(monthDate)) {
					productWarePropertyService.addCheckStaffMonth(monthCount,list, statService,areaId);
					tip = "可以修改的已修改";
				} else {
					tip = "不可以更改过去月份的月在编人数";
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		}  catch(MyRuntimeException mre ) {
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常！");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", tip);
		request.setAttribute("url", "productWarePropertyAction.do?method=getCheckStaffWorkPlanInfo&monthDate="+monthDate + "&areaId=" + areaId);
		return mapping.findForward("tip");
	}
	
	
	/**
	 * 
	 * 根据修改的日在编人数 修改记录
	 * @return
	 */
	public ActionForward editDayStaffCount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean addDayRight = group.isFlag(692);
		if( !addDayRight ) {
			request.setAttribute("tip", "您没有编辑日在岗人数的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String monthDate = StringUtil.toSql(StringUtil.convertNull(request.getParameter("monthDate3")));
		int areaId = StringUtil.toInt((StringUtil.convertNull(request.getParameter("area"))));
		List list = new ArrayList();
		Map map = new HashMap();
		if( monthDate == null || monthDate.equals("")) {
			Date now = new Date();
			monthDate = DateUtil.formatDate(now, "yyyy-MM");
		}
		try {
			int pointCut = monthDate.indexOf("-");
			int year = Integer.parseInt( monthDate.substring(0,pointCut));
			int month = Integer.parseInt( monthDate.substring(pointCut + 1));
			Calendar cal = Calendar.getInstance(); 
			cal.set(Calendar.YEAR,year); 
			cal.set(Calendar.MONTH, month - 1);//Java月份才0开始算 
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE); 
			for( int i = 1; i < dateOfMonth + 1; i ++ ) {
				String temp = year +"-"+ String.format("%02d", new Object[]{new Integer(month)}) + "-" + String.format("%02d", new Object[]{new Integer(i)});
				list.add(temp);
				int tempInt = ProductWarePropertyService.toInt(request.getParameter("dStaffCount_" + temp));
				if( tempInt != -1 ) {
					map.put(temp, new Integer(tempInt));
				}
			}
		} catch (Exception e ) {
			request.setAttribute("tip", "所传日期参数有误");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String tip = "";
		boolean change = false;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(lock) {
				statService.getDbOp().startTransaction();
				int x = list.size();
				for( int i = 0; i < x; i ++ ) {
					String temp = (String)list.get(i);
					String time = temp + " 00:00:00";
					if( map.containsKey(temp)) {
						int dayCount = ((Integer)map.get(temp)).intValue();
						if( productWarePropertyService.isChangeAvailForDay(temp)) {
							if( !productWarePropertyService.addCheckStaffDay(dayCount, temp, areaId,statService)){
								tip += temp+"数据库操作出错";
							} else {
								change = true;
							}
						} else {
							tip += temp + "已过期不可修改,";
						}
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				if(change) {
					if( tip.equals("")) {
						tip = "修改成功!";
					} else {
						tip += "其余修改已成功！";
					}
				} else {
					if( tip.equals("")) {
						tip = "未修改数值";
					}
				}
			}
		} catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", e.getMessage());
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", tip);
		request.setAttribute("url", "productWarePropertyAction.do?method=getCheckStaffWorkPlanInfo&monthDate="+monthDate + "&areaId=" + areaId);
		return mapping.findForward("tip");
	}
	
	
	/**
	 * 
	 * 查询商品质检分类效率
	 * @return
	 */
	public ActionForward getCheckEffectInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 20;
		List checkEffectList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;
			int totalCount = statService.getCheckEffectCount("id<>0");
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			checkEffectList = statService.getCheckEffectList("id<>0", paging.getCurrentPageIndex()*countPerPage, countPerPage, "id asc");
			int x = checkEffectList.size();
			for ( int i = 0; i < x; i++) {
				CheckEffectBean cfBean = (CheckEffectBean) checkEffectList.get(i);
				List productWarePropertyList = statService.getProductWarePropertyList("check_effect_id = " + cfBean.getId(), -1, -1, "id asc");
				String productLines = "";
				Map tempMap = new HashMap();
				int y = productWarePropertyList.size();
				for( int j = 0; j < y; j ++ ) {
					ProductWarePropertyBean pwpBean = (ProductWarePropertyBean) productWarePropertyList.get(j);
					voProduct product = wareService.getProduct(pwpBean.getProductId());
					//确定产品线
					String productLine = "";// 产品线
					String productLineNameCondition = "  (product_line_catalog.catalog_id = (" 
						+ product.getParentId1() + ") and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id "
						+ " = (" + product.getParentId2() + "))";
					voProductLine vpl = wareService.getProductLine(productLineNameCondition);
					if( vpl != null ){
						productLine = vpl.getName();
					}
					if( !productLine.equals("") && !tempMap.containsKey(productLine) ) {
						productLines += productLine + ",";
						tempMap.put(productLine, "");
					}
					
				}
				if( productLines.length() > 0 ) {
					productLines = productLines.substring(0,(productLines.length() - 1));
				}
				cfBean.setProductLines(productLines);
			}
			
			paging.setPrefixUrl("productWarePropertyAction.do?method=getCheckEffectInfo");
			request.setAttribute("paging", paging);
			request.setAttribute("list", checkEffectList);	
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("checkEffectInfo");
	}
	
	/**
	 * 
	 * 添加商品质检分类效率
	 * @return
	 */
	public ActionForward addCheckEffect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean addCheckEffectRight = group.isFlag(693);
		if( !addCheckEffectRight ) {
			request.setAttribute("tip", "您没有添加商品质检分类与效率的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String name = StringUtil.toSql(StringUtil.convertNull(request.getParameter("name")));
		int effect = StringUtil.parstInt(request.getParameter("effect"));
		name = name.trim();
		if( name.equals("")) {
			request.setAttribute("tip", "请填写分类效率名称！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(lock) {
				statService.getDbOp().startTransaction();
				CheckEffectBean cfb = statService.getCheckEffect("name='" + name + "'");
				if( cfb != null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "已经有以这个名字命名的质检分类了");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					cfb = new CheckEffectBean();
					cfb.setName(name);
					cfb.setEffect(effect);
					if( !statService.addCheckEffect(cfb)) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "操作数据库失败");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "添加成功");
		request.setAttribute("url", request.getContextPath()+"/admin/cargo/addCheckEffect.jsp");
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 编辑商品质检分类效率
	 * @return
	 */
	public ActionForward editCheckEffect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean editCheckEffectRight = group.isFlag(694);
		if( !editCheckEffectRight ) {
			request.setAttribute("tip", "您没有编辑商品质检分类与效率的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String name = StringUtil.toSql(StringUtil.convertNull(request.getParameter("name")));
		int effect = StringUtil.parstInt(request.getParameter("effect"));
		int id = StringUtil.parstInt(request.getParameter("id"));
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				CheckEffectBean cfb = statService.getCheckEffect("id=" + id);
				if( cfb == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到这个要编辑的质检分类条目");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( cfb.getName().equals(name) && cfb.getEffect() == effect) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "并没有做修改");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( cfb.getName().equals(name) ) {
					if( !statService.updateCheckEffect("effect=" + effect, "id=" + id)) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "操作数据库失败");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					}
				} else {
					CheckEffectBean cfb2 = statService.getCheckEffect("name='" + name + "'");
					if( cfb2 != null ) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "已经有以这个名字命名的质检分类了");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						if(!statService.updateCheckEffect("name='" + name + "',"+"effect=" + effect, "id=" + id)) {
							statService.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "操作数据库失败");
				            request.setAttribute("result", "failure");
				            return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "修改成功");
		request.setAttribute("url", "productWarePropertyAction.do?method=getCheckEffectInfo");
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 删除商品质检分类效率
	 * @return
	 */
	public ActionForward deleteCheckEffect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String name = StringUtil.toSql(StringUtil.convertNull(request.getParameter("name")));
		int id = StringUtil.parstInt(request.getParameter("id"));
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				CheckEffectBean cfb = statService.getCheckEffect("id=" + id +" and name='" + name + "'");
				if( cfb == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到这个要删除的质检分类条目");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					//******************注意加上对于 商品物流属性的 相关的验证，如果 有关联 是让还是不让编辑
					if( statService.getProductWarePropertyCount("check_effect_id = " + id) > 0) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "该质检效率已经有商品物流属性关联了，不可以删除！");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!statService.deleteCheckEffect("id=" + id)){
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "在删除质检效率时，操作数据库失败！");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				
			}
		} catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "删除成功");
		request.setAttribute("url", "productWarePropertyAction.do?method=getCheckEffectInfo");
		return mapping.findForward("tip");
	}
	
	
	/**
	 * 
	 * 进入添加商品物流属性页面前要准备的数据
	 * @return
	 */
	public ActionForward toAddProductWareProperty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean addProductWarePropertyRight = group.isFlag(696);
		if( !addProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有添加商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			
			checkEffectList = statService.getCheckEffectList("id<>0", -1, -1, "id asc");
			productWareTypeList = productWarePropertyService.getProductWareTypeList("id<>0", -1, -1, "id asc");
			/*productWareTypeList = statService.getUserOrderPackageTypeList("id<>0", -1, -1, "type_id asc");
			productWareTypeList = productWarePropertyService.removeDuplicateInList(productWareTypeList);*/
			
			request.setAttribute("productWareTypeList", productWareTypeList);
			request.setAttribute("checkEffectList", checkEffectList);
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("addProductWareProperty");
	}
	
	/**
	 * 验证商品信息
	 *
	 */
	public void getProductInfoByCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			if( !productCode.equals("")) {
				product = wareService.getProduct(productCode);
				if( product == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到对应该编号的商品!'}");
				} else {
					List productWareList = statService.getProductWarePropertyList("product_id = " + product.getId(), -1, -1, "id asc");
					if( productWareList.size() == 0) {
						
						ProductBarcodeVO pbcb = bService.getProductBarcode("product_id = " + product.getId());
						if( pbcb == null ) {
							response.getWriter().write("{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success'";
							//条码
							ProductBarcodeVO bBean= bService.getProductBarcode("product_id=" + product.getId());
							if(bBean == null ) {
								js += ",productBarCode:''";
							} else {
								js += ",productBarCode:'" + bBean.getBarcode() + "'";
							}
							//物流分类
							
							js += ",wareType:'-1'";
							
							
							//标准装箱量
							/*CartonningStandardCountBean cscount = cartonningInfoservice.getCartonningStandardCount("product_id="+product.getId());
							if(cscount != null){
								js += ",standardCount:'" + cscount.getStandard() + "'";
							}else{
								js += ",standardCount:'0'";
							}*/
							
							js +="}";
							response.getWriter().write(js);
						}
						
					} else {
						response.getWriter().write("{status:'fail', tip:'该商品已经有商品物流属性了，不要重复添加！'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品编号!'}");
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 验证商品信息
	 *
	 */
	public void getProductInfoByBarCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productBarCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			if( !productBarCode.equals("")) {
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productBarCode)+"'");
				
				if( bBean == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到对应该条码的商品!'}");
				} else {
					product = wareService.getProduct(bBean.getProductId());
					List productWareList = statService.getProductWarePropertyList("product_id = " + product.getId(), -1, -1, "id asc");
					if( productWareList.size() == 0) {
						ProductBarcodeVO pbcb = bService.getProductBarcode("product_id = " + product.getId());
						if( pbcb == null ) {
							response.getWriter().write("{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success',productCode:'" + product.getCode() + "'";
							//物流分类
							js += ",wareType:'-1'";
							
							//标准装箱量
							/*CartonningStandardCountBean cscount = cartonningInfoservice.getCartonningStandardCount("product_id="+product.getId());
							if(cscount != null){
								js += ",standardCount:'" + cscount.getStandard() + "'";
							}else{
								js += ",standardCount:'0'";
							}*/
							
							js +="}";
							response.getWriter().write(js);
						}
						
					} else {
						response.getWriter().write("{status:'fail', tip:'该商品已经有商品物流属性了，不要重复添加!'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品条码!'}");
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 验证商品信息
	 *
	 */
	public void getProductInfoByCode2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		voProduct product = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			System.out.println("1");
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			if( !productCode.equals("")) {
				product = wareService.getProduct(productCode);
				if( product == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到对应该编号的商品!'}");
				} else {
					List productWareList = statService.getProductWarePropertyList("product_id = " + product.getId(), -1, -1, "id asc");
					if( productWareList.size() == 1) {
						
						ProductBarcodeVO pbcb = bService.getProductBarcode("product_id = " + product.getId());
						if( pbcb == null ) {
							response.getWriter().write("{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success'";
							//条码
							ProductBarcodeVO bBean= bService.getProductBarcode("product_id=" + product.getId());
							if(bBean == null ) {
								js += ",productBarCode:''";
							} else {
								js += ",productBarCode:'" + bBean.getBarcode() + "'";
							}
							//物流分类
							js += ",wareType:'-1'";
							
							
							//标准装箱量
							/*CartonningStandardCountBean cscount = cartonningInfoservice.getCartonningStandardCount("product_id="+product.getId());
							if(cscount != null){
								js += ",standardCount:'" + cscount.getStandard() + "'";
							}else{
								js += ",standardCount:'0'";
							}*/
							
							js +="}";
							response.getWriter().write(js);
						}
						
					} else {
						response.getWriter().write("{status:'fail', tip:'自动获取数据出错！'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品编号!'}");
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	/**
	 * 验证商品信息
	 *
	 */
	public void getProductInfoByBarCode2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
		
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productBarCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			System.out.println("2");
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			if( !productBarCode.equals("")) {
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productBarCode)+"'");
				
				if( bBean == null ) {
					response.getWriter().write("{status:'fail', tip:'没有找到对应该条码的商品!'}");
				} else {
					product = wareService.getProduct(bBean.getProductId());
					List productWareList = statService.getProductWarePropertyList("product_id = " + product.getId(), -1, -1, "id asc");
					if( productWareList.size() == 1) {
						ProductBarcodeVO pbcb = bService.getProductBarcode("product_id = " + product.getId());
						if( pbcb == null ) {
							response.getWriter().write("{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success',productCode:'" + product.getCode() + "'";
							//物流分类
							js += ",wareType:'-1'";
							
							//标准装箱量
							/*CartonningStandardCountBean cscount = cartonningInfoservice.getCartonningStandardCount("product_id="+product.getId());
							if(cscount != null){
								js += ",standardCount:'" + cscount.getStandard() + "'";
							}else{
								js += ",standardCount:'0'";
							}*/
							
							js +="}";
							response.getWriter().write(js);
						}
						
					} else {
						response.getWriter().write("{status:'fail', tip:'自动获取数据出错！'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品条码!'}");
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}
	
	
	/**
	 * 
	 * 添加商品物流属性
	 * @return
	 */
	public ActionForward addProductWareProperty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean addProductWarePropertyRight = group.isFlag(696);
		if( !addProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有添加商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request.getParameter("checkEffect"));
		int length = StringUtil.parstInt(request.getParameter("length"));
		int width = StringUtil.parstInt(request.getParameter("width"));
		int height = StringUtil.parstInt(request.getParameter("height"));
		//int standardCount = StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		int weight = StringUtil.parstInt(request.getParameter("weight"));
		String identityInfo = StringUtil.toSql(StringUtil.convertNull(request.getParameter("identityInfo")));
		int binning = StringUtil.parstInt(request.getParameter("binning"));//标准装箱量
		voProduct product = null;
		ProductBarcodeVO bBean = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				
				if( productCode.equals("") && productBarCode.equals("")) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "请至少输入商品编号和商品条码中的一个！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				} else if( !productCode.equals("") && productBarCode.equals("")) {
					product = wareService.getProduct(productCode);
					if(product == null ) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应编号的商品！");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					}
				} else if( productCode.equals("") && !productBarCode.equals("")) {
					bBean= bService.getProductBarcode("barcode="+"'"+productBarCode+"'");
					if(bBean == null ) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应的条码记录！");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						product = wareService.getProduct(bBean.getProductId());
						if( product == null ) {
							request.setAttribute("tip", "没有找到对应的条码的商品！");
				            request.setAttribute("result", "failure");
				            return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				} else {
					//两个都填了
					//先要验证产品，productCode  productBarCode  是否存在 是否对应于一个
					product = productWarePropertyService.isCodeAndBarCodeMatched(productCode, productBarCode, bService, statService, wareService);
				}
				
				
				
				//要验证是否已经存在该商品的 商品物流属性
				ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id = " + product.getId());
				if( pwpBean != null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "该商品已经有商品物流属性了！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//然后验证 质检分类是否存在
				CheckEffectBean cfBean = statService.getCheckEffect("id = " + checkEffectId);
				if( cfBean == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的质检分类！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//对应的 物流分类是否存在
				ProductWareTypeBean pwtBean = productWarePropertyService.getProductWareType("id=" + wareType);
				if( pwtBean == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的商品物流分类！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				/*UserOrderPackageTypeBean uoptBean2 = userOrderService.getUserOrderPackageType("type_id=" + wareType + " and product_catalog=" + product.getParentId1());
				if( uoptBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "商品不属于所选的物流分类！ ");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}*/
				//寻找或修改装箱数量
				//productWarePropertyService.manageCartonningStandardCount(user,product,standardCount, cartonningInfoService);
				
				//首先要在 数据库 product_ware_property 之中 添加一条记录
				//productWarePropertyService.saveProductWareProperty(product,user,cfBean,pwtBean,length,width,height,weight,statService);
				productWarePropertyService.saveProductWareProperty(product,user,cfBean,pwtBean,length,width,height,weight,identityInfo,statService,binning);
				
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		}   catch(MyRuntimeException mre ) {
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}  catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常！");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "添加成功!");
		request.setAttribute("url", request.getContextPath()+"/admin/productWarePropertyAction.do?method=toAddProductWareProperty");
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 查询商品物流属性
	 * @return
	 */
	public ActionForward getProductWarePropertyInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if( !seeProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有查看商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request.getParameter("checkEffect"));
		//int standardCount = StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 20;
		List productWarePropertyList = new ArrayList();
		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		StringBuilder condition = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			PagingBean paging = null;
			if( !productCode.equals("")) {
				params.append("&");
				params.append("productCode=" + productCode);
				
				voProduct product = wareService.getProduct(productCode);
				if( product == null ) {
					if(condition.length() > 0 ) {
						condition.append(" and");
						condition.append(" id = -1");
					} else {
						condition.append(" id = -1");
					}
				} else {
					if(condition.length() > 0 ) {
						condition.append(" and");
						condition.append(" product_id = " + product.getId());
					} else {
						condition.append(" product_id = " + product.getId());
					}
				}
			}
			
			if( !productBarCode.equals("")) {
				params.append("&");
				params.append("productBarCode=" + productBarCode);
				
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productBarCode)+"'");
				if( bBean == null ) {
					if(condition.length() > 0 ) {
						condition.append(" and");
						condition.append(" id = -1");
					} else {
						condition.append(" id = -1");
					}
				} else {
					voProduct product = wareService.getProduct(bBean.getProductId());
					if( product == null ) {
						if(condition.length() > 0 ) {
							condition.append(" and");
							condition.append(" id = -1");
						} else {
							condition.append(" id = -1");
						}
					} else {
						if(condition.length() > 0 ) {
							condition.append(" and");
							condition.append(" product_id = " + product.getId());
						} else {
							condition.append(" product_id = " + product.getId());
						}
					}
				}
			}
			
			if( checkEffectId != 0 && checkEffectId != -1 ) {
				params.append("&");
				params.append("checkEffect=" + checkEffectId);
				
				if(condition.length() > 0 ) {
					condition.append(" and");
					condition.append(" check_effect_id = " + checkEffectId);
				} else {
					condition.append(" check_effect_id = " + checkEffectId);
				}
				
			}
			
			if( wareType != 0 && wareType != -1 ) {
				params.append("&");
				params.append("wareType=" + wareType);
				
				if(condition.length() > 0 ) {
					condition.append(" and");
					condition.append(" product_type_id = " + wareType);
				} else {
					condition.append(" product_type_id = " + wareType);
				}
				
			}
			String conditionS = "id<>0";
			if( condition.length() > 0 ) {
				conditionS = condition.toString();
			}
			
			int totalCount = statService.getProductWarePropertyCount(conditionS);
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			productWarePropertyList = statService.getProductWarePropertyList(conditionS, paging.getCurrentPageIndex()*countPerPage, countPerPage, "id asc");
			int x = productWarePropertyList.size();
			for( int i = 0; i < x; i++ ) {
				ProductWarePropertyBean pwpBean = (ProductWarePropertyBean) productWarePropertyList.get(i);
				voProduct temp = wareService.getProduct(pwpBean.getProductId());
				if(temp == null ) {
					temp = new voProduct();
				} 
				pwpBean.setProduct(temp);
				CheckEffectBean cfBean = statService.getCheckEffect("id=" + pwpBean.getCheckEffectId());
				if( cfBean == null ) {
					cfBean = new CheckEffectBean();
				}
				pwpBean.setCheckeEffect(cfBean);
				ProductWareTypeBean pwtBean = productWarePropertyService.getProductWareType("id=" + pwpBean.getProductTypeId());
				if( pwtBean == null ) {
					pwtBean = new ProductWareTypeBean();
					pwtBean.setName("");
				}
				pwpBean.setProductWareType(pwtBean);
			}
			checkEffectList = statService.getCheckEffectList("id<>0", -1, -1, "id asc");
			productWareTypeList = productWarePropertyService.getProductWareTypeList("id<>0", -1, -1, "id asc");
			/*productWareTypeList = statService.getUserOrderPackageTypeList("id<>0", -1, -1, "type_id asc");
			productWareTypeList = productWarePropertyService.removeDuplicateInList(productWareTypeList);*/
			
			request.setAttribute("productWareTypeList", productWareTypeList);
			request.setAttribute("checkEffectList", checkEffectList);
			paging.setPrefixUrl("productWarePropertyAction.do?method=getProductWarePropertyInfo"+params.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("list", productWarePropertyList);	
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("productWarePropertyInfo");
	}
	
	
	/**
	 * 
	 * before 修改商品物流属性
	 * @return
	 */
	public ActionForward preEditProductWareProperty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(697);
		if( !editProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有修改商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		int productWarePropertyId = StringUtil.parstInt(request.getParameter("productWarePropertyId"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			ProductWarePropertyBean pwpBean = statService.getProductWareProperty("id = " + productWarePropertyId);
			if( pwpBean == null ) {
				request.setAttribute("tip", "没有找到对应的商品物流属性信息！");
	            request.setAttribute("result", "failure");
	            return mapping.findForward(IConstants.FAILURE_KEY);
			} else {
				voProduct temp = wareService.getProduct(pwpBean.getProductId());
				if(temp == null ) {
					temp = new voProduct();
				} 
				pwpBean.setProduct(temp);
				ProductBarcodeVO bBean= bService.getProductBarcode("product_id=" + temp.getId());
				if(bBean == null ) {
					request.setAttribute("barCode", "");
				} else {
					request.setAttribute("barCode", bBean.getBarcode());
				}
				request.setAttribute("productWareProperty", pwpBean);
				
				checkEffectList = statService.getCheckEffectList("id<>0", -1, -1, "id asc");
				productWareTypeList = productWarePropertyService.getProductWareTypeList("id<>0", -1, -1, "id asc");
				/*productWareTypeList = statService.getUserOrderPackageTypeList("id<>0", -1, -1, "type_id asc");
				productWareTypeList = productWarePropertyService.removeDuplicateInList(productWareTypeList);*/
				
				request.setAttribute("productWareTypeList", productWareTypeList);
				request.setAttribute("checkEffectList", checkEffectList);
			}
		} catch(Exception e ) {
			e.printStackTrace();
			
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("editProductWareProperty");
	}
	
	/**
	 * 
	 * 修改商品物流属性
	 * @return
	 */
	public ActionForward editProductWareProperty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(697);
		if( !editProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有修改商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int productWarePropertyId = StringUtil.parstInt(request.getParameter("productWarePropertyId"));
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request.getParameter("checkEffect"));
		int length = StringUtil.parstInt(request.getParameter("length"));
		int width = StringUtil.parstInt(request.getParameter("width"));
		int height = StringUtil.parstInt(request.getParameter("height"));
		//int standardCount = StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		int weight = ProductWarePropertyService.toInt(request.getParameter("weight"));
		String identityInfo = StringUtil.toSql(StringUtil.convertNull(request.getParameter("identityInfo")));
		int binning = StringUtil.parstInt(request.getParameter("binning"));
		voProduct product = null;
		ProductBarcodeVO bBean = null;
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				
				if( productCode.equals("") && productBarCode.equals("")) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "请至少输入商品编号和商品条码中的一个！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				} else if( !productCode.equals("") && productBarCode.equals("")) {
					product = wareService.getProduct(productCode);
					if(product == null ) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应编号的商品！");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					}
				} else if( productCode.equals("") && !productBarCode.equals("")) {
					bBean= bService.getProductBarcode("barcode="+"'"+productBarCode+"'");
					if(bBean == null ) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应的条码记录！");
			            request.setAttribute("result", "failure");
			            return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						product = wareService.getProduct(bBean.getProductId());
						if( product == null ) {
							request.setAttribute("tip", "没有找到对应的条码的商品！");
				            request.setAttribute("result", "failure");
				            return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				} else {
					//两个都填了
					//先要验证产品，productCode  productBarCode  是否存在 是否对应于一个
					product = productWarePropertyService.isCodeAndBarCodeMatched(productCode, productBarCode, bService, statService, wareService);
				}
				
				//要验证是否已经存在该商品的 商品物流属性
				ProductWarePropertyBean pwpBean = statService.getProductWareProperty("id=" + productWarePropertyId);
				if( pwpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到要修改的商品物流属性的信息！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					CheckEffectBean cfBean2 = statService.getCheckEffect("id=" + pwpBean.getCheckEffectId());
					if( cfBean2 == null ) {
						cfBean2 = new CheckEffectBean();
						cfBean2.setName("");
					}
					pwpBean.setCheckeEffect(cfBean2);
					
					ProductWareTypeBean pwtBean2 = productWarePropertyService.getProductWareType("id=" + pwpBean.getProductTypeId());
					if( pwtBean2 == null ) {
						pwtBean2 = new ProductWareTypeBean();
						pwtBean2.setName("");
					}
					pwpBean.setProductWareType(pwtBean2);
				}
				ProductWarePropertyBean pwpBeanOther = statService.getProductWareProperty("product_id = " + product.getId());
				if( pwpBeanOther != null && pwpBeanOther.getId() != productWarePropertyId ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "该商品已经有别的商品物流属性了，请去修改对应的商品物流属性！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				//然后验证 质检分类是否存在
				CheckEffectBean cfBean = statService.getCheckEffect("id = " + checkEffectId);
				if( cfBean == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的质检分类！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//对应的 物流分类是否存在
				ProductWareTypeBean pwtBean = productWarePropertyService.getProductWareType("id=" + wareType);
				if( pwtBean == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的商品物流分类！");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				/*UserOrderPackageTypeBean uoptBean2 = userOrderService.getUserOrderPackageType("type_id=" + wareType + " and product_catalog=" + product.getParentId1());
				if( uoptBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "商品不属于所选的物流分类！ ");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}*/
				//寻找或修改装箱数量
				//productWarePropertyService.manageCartonningStandardCount(user,product,standardCount, cartonningInfoService);
				
				//修改 对应的商品物流属性
				//productWarePropertyService.updateProductWareProperty(pwpBean, user, product,cfBean,pwtBean,length,width,height,weight,statService);
				productWarePropertyService.updateProductWareProperty(pwpBean, user, product,cfBean,pwtBean,binning,length,width,height,weight,identityInfo,statService,binning);
				
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		}   catch(MyRuntimeException mre ) {
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}  catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常！");
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "修改成功!");
		request.setAttribute("url", request.getContextPath()+"/admin/productWarePropertyAction.do?method=getProductWarePropertyInfo");
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 删除商品物流属性
	 * @return
	 */
	public ActionForward deleteProductWareProperty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean deleteProductWarePropertyRight = group.isFlag(698);
		if( !deleteProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有删除商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int productWarePropertyId = StringUtil.parstInt(request.getParameter("productWarePropertyId"));
		int productId = StringUtil.parstInt(request.getParameter("productId"));
		
		WareService wareService = new WareService(); 
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				//要验证是否已经存在该商品的 商品物流属性
				ProductWarePropertyBean pwpBean = statService.getProductWareProperty("id = " + productWarePropertyId);
				if( pwpBean == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到要删除的商品物流属性!");
		            request.setAttribute("result", "failure");
		            return mapping.findForward(IConstants.FAILURE_KEY);
				}
				ProductWarePropertyBean pwpBean2 = statService.getProductWareProperty("id = " + productWarePropertyId + " and product_id = " + productId);
				if( pwpBean2 == null ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "要删除的商品物流属性的商品关联已经发生变更，暂时不能删除!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( !statService.deleteProductWareProperty("id=" + productWarePropertyId) ) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "在删除商品物流属性时，数据库操作出错!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				voProduct product = wareService.getProduct(pwpBean.getProductId());
				if( product == null ) {
					product = new voProduct();
					product.setCode("");
				}
				pwpBean.setProduct(product);
				//添加日志
				ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
				pwplBean.setProductWarePropertyId(pwpBean.getId());
				pwplBean.setOperDetail("删除了商品" + product.getCode() + "的物流属性");
				pwplBean.setOperId(user.getId());
				pwplBean.setOperName(user.getUsername());
				pwplBean.setTime(DateUtil.getNow());
				if( !productWarePropertyService.addProductWarePropertyLog(pwplBean)) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "在删除商品物流属性时，数据库操作出错!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(Exception e ) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", e.getMessage());
            request.setAttribute("result", "failure");
            return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "删除成功");
		request.setAttribute("url", request.getContextPath()+"/admin/productWarePropertyAction.do?method=getProductWarePropertyInfo");
		return mapping.findForward("tip");
	}
	
	/**
	 * 
	 * 查询商品物流属性日志
	 * @return
	 */
	public ActionForward getProductWarePropertyLogInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if( !seeProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有查看商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String productCode = StringUtil.toSql(StringUtil.convertNull(request.getParameter("productCode")));
		int productWarePropertyId = StringUtil.toInt(request.getParameter("productWarePropertyId"));
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 40;
		List productWarePropertyLogList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;
			
			int totalCount = productWarePropertyService.getProductWarePropertyLogCount("product_ware_property_id=" + productWarePropertyId);
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			productWarePropertyLogList = productWarePropertyService.getProductWarePropertyLogList("product_ware_property_id="+productWarePropertyId, paging.getCurrentPageIndex()*countPerPage, countPerPage, "time desc");
			
			paging.setPrefixUrl("productWarePropertyAction.do?method=getProductWarePropertyLogInfo");
			request.setAttribute("paging", paging);
			request.setAttribute("list", productWarePropertyLogList);
			request.setAttribute("productCode", productCode);
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("productWarePropertyLogInfo");
	}
	
	/**
	 * 
	 * 添加商品物流分类
	 * @return
	 */
	public ActionForward addProductWareType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean addProductWarePropertyRight = group.isFlag(725);
		if( !addProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有添加商品物流分类的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String name = StringUtil.toSql(StringUtil.convertNull(request.getParameter("name")));
		WareService wareService = new WareService(); 
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				productWarePropertyService.getDbOp().startTransaction();
				int has = productWarePropertyService.getProductWareTypeCount("name='" + name + "'");
				if( has > 0 ) {
					request.setAttribute("tip", "该商品物流分类已存在！");
					request.setAttribute("result", "failure");
					productWarePropertyService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					ProductWareTypeBean pwtBean = new ProductWareTypeBean();
					pwtBean.setName(name);
					pwtBean.setSequence(1);
					if( !productWarePropertyService.addProductWareType(pwtBean)) {
						request.setAttribute("tip", "在添加商品物流分类时，数据库操作失败！");
						request.setAttribute("result", "failure");
						productWarePropertyService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				productWarePropertyService.getDbOp().commitTransaction();
				productWarePropertyService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(Exception e ) {
			e.printStackTrace();
			request.setAttribute("tip", "添加商品物流分类时发生了错误！");
			request.setAttribute("result", "failure");
			productWarePropertyService.getDbOp().rollbackTransaction();
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			productWarePropertyService.releaseAll();
		}
		request.setAttribute("tip", "添加成功!");
		request.setAttribute("url", request.getContextPath()+"/admin/cargo/addProductWareType.jsp");
		return mapping.findForward("tip");
	}
	
	
	/**
	 * 
	 * 查询商品物流分类
	 * @return
	 */
	public ActionForward getProductWareTypeInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 20;
		List productWareTypeList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;
			int totalCount = productWarePropertyService.getProductWareTypeCount("id<>0");
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			productWareTypeList = productWarePropertyService.getProductWareTypeList("id<>0", paging.getCurrentPageIndex()*countPerPage, countPerPage, "id asc");
			
			paging.setPrefixUrl("productWarePropertyAction.do?method=getProductWareTypeInfo");
			request.setAttribute("paging", paging);
			request.setAttribute("list", productWareTypeList);	
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("productWareTypeInfo");
	}
	
	/**
	 * 
	 * 修改商品物流分类优先级
	 * @return
	 */
	public ActionForward editProductWareTypeSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) { 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(708);
		if( !editProductWarePropertyRight ) {
			request.setAttribute("tip", "您没有修改商品物流分类优先级的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String[] change = request.getParameterValues("change");
		boolean hasChange = false;
		WareService wareService = new WareService(); 
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				if( change != null ) {
					productWarePropertyService.getDbOp().startTransaction();
					int changeCount = change.length;
					for ( int i = 0; i < changeCount; i++ ) {
						String temp = change[i];
						int id = StringUtil.parstInt(temp);
						int sequence = StringUtil.parstInt(request.getParameter("sequence_" + id));
						if( id == 0 || sequence <= 0 ) {
							request.setAttribute("tip", "所传参数有误!");
							request.setAttribute("result", "failure");
							productWarePropertyService.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//查找是否还存在要修改的项目
						ProductWareTypeBean pwtBean = productWarePropertyService.getProductWareType("id="+id);
						if( pwtBean == null ) {
							request.setAttribute("tip", "未找到要修改的项目!");
							request.setAttribute("result", "failure");
							productWarePropertyService.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( pwtBean.getSequence() != sequence ) {
							//执行修改
							if(!productWarePropertyService.updateProductWareType("sequence=" + sequence, "id=" + id)) {
								request.setAttribute("tip", "修改时，数据库操作失败!");
								request.setAttribute("result", "failure");
								productWarePropertyService.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							hasChange = true;
						}
					}
					productWarePropertyService.getDbOp().commitTransaction();
					productWarePropertyService.getDbOp().getConn().setAutoCommit(true);
				}
			}
		} catch(Exception e ) {
			e.printStackTrace();
			request.setAttribute("tip", "修改商品物流分类优先级时发生了错误！");
			request.setAttribute("result", "failure");
			productWarePropertyService.getDbOp().rollbackTransaction();
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			productWarePropertyService.releaseAll();
		}
		if( hasChange ) {
			request.setAttribute("tip", "修改优先级成功!");
			request.setAttribute("url", request.getContextPath()+"/admin/productWarePropertyAction.do?method=getProductWareTypeInfo");
			return mapping.findForward("tip");
		} else {
			request.setAttribute("tip", "并没有任何修改!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
	}
	
}
