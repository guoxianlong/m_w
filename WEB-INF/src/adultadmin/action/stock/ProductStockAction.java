/*
 * Created on 2009-4-28
 *
 */
package adultadmin.action.stock;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.IMEI.IMEIStockExchangeBean;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleStockExchangeProduct;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockCardComparator;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.bean.system.TextResBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISystemService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ProductStockAction {

	private static byte[] stockLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-4-29
	 * 
	 * 说明：库存调配单列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void stockExchangeList(HttpServletRequest request, HttpServletResponse response) {
        int countPerPage = 20;

        int stockOutType = StringUtil.toInt(request.getParameter("stockOutType"));
        int stockInType = StringUtil.toInt(request.getParameter("stockInType"));
        int stockOutArea = StringUtil.toInt(request.getParameter("stockOutArea"));
        int stockInArea = StringUtil.toInt(request.getParameter("stockInArea"));
        String code = StringUtil.dealParam(request.getParameter("code"));
        String stockOutOper = StringUtil.dealParam(request.getParameter("stockOutOper"));
        String stockInOper = StringUtil.dealParam(request.getParameter("stockInOper"));
        String stockOutAudit = StringUtil.dealParam(request.getParameter("stockOutAudit"));
        String stockInAudit = StringUtil.dealParam(request.getParameter("stockInAudit"));
        int status = StringUtil.toInt(request.getParameter("status"));
        String createDate = StringUtil.dealParam(request.getParameter("createDate"));
        String dealDate = StringUtil.dealParam(request.getParameter("dealDate"));
        int priorStatus = StringUtil.toInt(request.getParameter("priorStatus"));

        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
        IUserService userService = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        try {
        	if(!StringUtil.isNull(createDate) && !StringUtil.isNull(dealDate)){
        		Date create = DateUtil.parseDate(createDate);
        		Date deal = DateUtil.parseDate(dealDate);
        		if(deal.getTime() < create.getTime()){
            		request.setAttribute("tip", "完成时间不能早于创建时间！");
                    request.setAttribute("result", "failure");
                    return;
        		}
        	}
            String condition = null;
            StringBuilder buf = new StringBuilder();
            StringBuilder paramBuf = new StringBuilder();
            if(stockOutType >= 0){
            	buf.append("stock_out_type=").append(stockOutType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockOutType=").append(stockOutType);
            }
            if(stockInType >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_in_type=").append(stockInType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockInType=").append(stockInType);
            }
            if(stockOutArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_out_area=").append(stockOutArea);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockOutArea=").append(stockOutArea);
            }
            if(stockInArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_in_area=").append(stockInArea);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockInArea=").append(stockInArea);
            }
            if(!StringUtil.isNull(code)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("code='").append(code).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("code=").append(code);
            }
            if(!StringUtil.isNull(createDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("left(create_datetime, 10)>='").append(createDate).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("createDate=").append(createDate);
            }
            if(!StringUtil.isNull(dealDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("left(confirm_datetime, 10)<='").append(dealDate).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("dealDate=").append(dealDate);
            }
            if(status >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("status=").append(status);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("status=").append(status);
            }
            if(!StringUtil.isNull(stockOutOper)){
            	voUser user = userService.getAdminUser("username='" + stockOutOper + "'");
            	if(user != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("create_user_id=").append(user.getId());
            	}
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockOutOper=").append(stockOutOper);
            }
            if(!StringUtil.isNull(stockInOper)){
            	voUser user = userService.getAdminUser("username='" + stockInOper + "'");
            	if(user != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("stock_in_oper=").append(user.getId());
            	}
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockInOper=").append(stockInOper);
            }
            if(!StringUtil.isNull(stockOutAudit)){
            	voUser user = userService.getAdminUser("username='" + stockOutAudit + "'");
            	if(user != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("auditing_user_Id=").append(user.getId());
            	}
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockOutAudit=").append(stockOutAudit);
            }
            if(!StringUtil.isNull(stockInAudit)){
            	voUser user = userService.getAdminUser("username='" + stockInAudit + "'");
            	if(user != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("auditing_user_id2=").append(user.getId());
            	}
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockInAudit=").append(stockInAudit);
            }
            if(priorStatus >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("prior_status=").append(priorStatus);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("priorStatus=").append(priorStatus);
            }
            if(buf.length() > 0){
            	condition = buf.toString();
            }
            //总数
            int totalCount = service.getStockExchangeCount(condition);
            //页码
            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
            List list = service.getStockExchangeList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
            paging.setPrefixUrl("stockExchangeList.jsp?" + paramBuf.toString());

            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	StockExchangeBean se = (StockExchangeBean)iter.next();
            	se.setCreateUser(userService.getAdminUser("id=" + se.getCreateUserId()));
            	se.setAuditingUser(userService.getAdminUser("id=" + se.getAuditingUserId()));
            	se.setAuditingUser2(userService.getAdminUser("id=" + se.getAuditingUserId2()));
            	se.setStockOutOperUser(userService.getAdminUser("id=" + se.getStockOutOper()));
            	se.setStockInOperUser(userService.getAdminUser("id=" + se.getStockInOper()));
            	int count = afStockService.getAfterSaleStockExchangeProductCount("stock_exchange_id=" + se.getId());
            	se.setAfterSaleFlag(count > 0 ? true : false);
            }

            request.setAttribute("paging", paging);
            request.setAttribute("list", list);
        } finally {
            service.releaseAll();
        }
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：修改库存调配单属性
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void editStockExchange(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	boolean edit = false;

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        String remark = StringUtil.dealParam(request.getParameter("remark"));
	        String back = StringUtil.dealParam(request.getParameter("back"));

	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);

	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }

	            if(!edit){
	            	request.setAttribute("tip", "对不起，你没有权限编辑其他人的调拨单！");
	                request.setAttribute("result", "failure");
	                return;
	            }

	            //开始事务
	            service.getDbOp().startTransaction();
	            
	            if(!service.updateStockExchange("remark = '" + remark + "'", "id = " + exchangeId))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return;
	            }
	            //log记录
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setAdminId(user.getId());
	            log.setAdminName(user.getUsername());
	            log.setLogId(exchangeId);
	            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
	            log.setOperDatetime(DateUtil.getNow());
	            log.setRemark("修改商品调配操作：" + bean.getName());
	            log.setType(StockAdminHistoryBean.CHANGE);
	            if(! stockService.addStockAdminHistory(log))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return;
	            }
	            // 提交事务
	            service.getDbOp().commitTransaction();
	            request.setAttribute("back", back + "?exchangeId=" + exchangeId);
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：添加库存调配单
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void addStockExchange(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}

    	synchronized(stockLock){
	        String name = StringUtil.dealParam(request.getParameter("name"));
	        /*
	        if (StringUtil.convertNull(name).equalsIgnoreCase("")) {
	            request.setAttribute("tip", "请输入操作名称！");
	            request.setAttribute("result", "failure");
	            return;
	        }*/

	        int stockInArea = StringUtil.toInt(request.getParameter("stockInArea"));
	        int stockInType = StringUtil.toInt(request.getParameter("stockInType"));
	        int stockOutArea = StringUtil.toInt(request.getParameter("stockOutArea"));
	        int stockOutType = StringUtil.toInt(request.getParameter("stockOutType"));
	        int addPriorStatus = StringUtil.toInt(request.getParameter("addPriorStatus"));
//	        if(stockOutType == 9 && stockOutArea != 1){
//	        	request.setAttribute("tip", "源库类型为“售后库”，库地区不是“芳村”，不能新建调拨单！");
//	        	request.setAttribute("result", "failure");
//	        	return;
//	        }
//	        if((stockInArea == 7 && stockInType == 9) || (stockOutArea == 7 && stockOutType == 9)){
//	        	request.setAttribute("tip", "源库地区是深圳、库类型是售后库 或 目的库地区是深圳、库类型是售后库 不能新建调拨单！");
//	        	request.setAttribute("result", "failure");
//	        	return;
//	        }
	        
	        if(stockOutType == CargoInfoBean.STOCKTYPE_AFTER_SALE){
	        	request.setAttribute("tip", "源库类型为“售后库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        if(stockInType == CargoInfoBean.STOCKTYPE_AFTER_SALE){
	        	request.setAttribute("tip", "目的库类型为“售后库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        
	        if(stockOutType == CargoInfoBean.STOCKTYPE_CUSTOMER){
	        	request.setAttribute("tip", "源库类型为“客户库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        if(stockInType == CargoInfoBean.STOCKTYPE_CUSTOMER){
	        	request.setAttribute("tip", "目的库类型为“客户库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        
	        if(stockOutType == CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING){
	        	request.setAttribute("tip", "源库类型为“配件售后库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        if(stockInType == CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING){
	        	request.setAttribute("tip", "目的库类型为“配件售后库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        
	        if(stockOutType == CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING){
	        	request.setAttribute("tip", "源库类型为“配件客户库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        if(stockInType == CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING){
	        	request.setAttribute("tip", "目的库类型为“配件客户库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        
	        if(stockOutType == CargoInfoBean.STOCKTYPE_SPARE){
	        	request.setAttribute("tip", "源库类型为“备用机库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }
	        if(stockInType == CargoInfoBean.STOCKTYPE_SPARE){
	        	request.setAttribute("tip", "目的库类型为“备用机库”，不能新建调拨单！");
	        	request.setAttribute("result", "failure");
	        	return;
	        }	        
	        
	        if(!CargoDeptAreaService. hasCargoDeptArea(request, stockOutArea, stockOutType)){
				request.setAttribute("tip", "用户只能添加自己所属库地区和库类型’的调拨单");
				request.setAttribute("result", "failure");
		        return;
			}
	        if(stockInArea < 0 || stockInType < 0 || stockOutArea < 0 || stockOutType < 0){
	        	request.setAttribute("tip", "请选择库的所在地和类型！");
	            request.setAttribute("result", "failure");
	            return;
	        }

	        if(stockInArea == stockOutArea && stockInType == stockOutType){
	        	request.setAttribute("tip", "不能在同一个库中调配商品！");
	            request.setAttribute("result", "failure");
	            return;
	        }

	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            service.getDbOp().startTransaction();

	            StockExchangeBean bean = new StockExchangeBean();
	            bean.setCreateDatetime(DateUtil.getNow());
	            bean.setName(name);
	            bean.setRemark("");
	            bean.setStatus(StockExchangeBean.STATUS0);
	            //bean.setCode(CodeUtil.getStockExchangeCode());
	            /*String code = "DB"+DateUtil.getNow().substring(2,10).replace("-", "");
	            StockExchangeBean tempBean = service.getStockExchange("code like '"+code+"%' order by id desc limit 1");
	            if(tempBean == null){
					code = code + "0001";
				}else{
					//获取当日调拨单编号最大值
					String _code = tempBean.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-4));
					number++;
					code += String.format("%04d",new Object[]{new Integer(number)});
				}*/
	            Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
				String brefCode = "DB" + sdf.format(cal.getTime());
	            bean.setCode(brefCode);
	            bean.setCreateUserId(user.getId());
	            bean.setCreateUserName(user.getUsername());
	            bean.setStockOutOperName("");
	            bean.setAuditingUserName("");
	            bean.setStockInOperName("");
	            bean.setAuditingUserName2("");
	            bean.setStockInArea(stockInArea);
	            bean.setStockOutArea(stockOutArea);
	            bean.setStockInType(stockInType);
	            bean.setStockOutType(stockOutType);
	            bean.setPriorStatus(addPriorStatus);
	            if (!service.addStockExchange(bean)) {
	            	service.getDbOp().rollbackTransaction();
	                request.setAttribute("tip", "添加失败！");
	                request.setAttribute("result", "failure");
	                return;
	            }

	            int id = service.getDbOp().getLastInsertId();
				bean.setId(id);
				
				//此处修改调拨单
				String newCode = null;
				if(id > 9999){
					String strId = String.valueOf(id);
					newCode = strId.substring(strId.length()- 4, strId.length());
				} else {
					DecimalFormat df2 = new DecimalFormat("0000");
					newCode = df2.format(id);
				}
				String totalCode = brefCode + newCode;
				StringBuilder updateBuf = new StringBuilder();
				updateBuf.append("update stock_exchange set code='" + totalCode + "' where id=").append(id);
				if( !service.getDbOp().executeUpdate(updateBuf.toString())) {
					service.getDbOp().rollbackTransaction();
	                request.setAttribute("tip", "添加失败！");
	                request.setAttribute("result", "failure");
	                return;
				}
	            bean.setCode(totalCode);
	            //log记录
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setAdminId(user.getId());
	            log.setAdminName(user.getUsername());
	            log.setLogId(id);
	            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
	            log.setOperDatetime(DateUtil.getNow());
	            log.setRemark("新建商品调配操作：" + bean.getName());
	            log.setType(StockAdminHistoryBean.CREATE);
	            if(!stockService.addStockAdminHistory(log))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return;
	            }
	            service.getDbOp().commitTransaction();

	            request.setAttribute("exchangeId", String.valueOf(id));
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：查看库存调配单
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void stockExchange(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}

    	int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
    	DbOperation dbOp = new DbOperation(DbOperation.DB);
    	WareService wareService = new WareService(dbOp);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        ISystemService sService = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        List<IMEIStockExchangeBean> list = null;
        List<IMEIStockExchangeBean> imeiStockExchangeList = new ArrayList<IMEIStockExchangeBean>();
        try {
            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
            if(bean == null){
        		request.setAttribute("tip", "没有找到这条记录，操作失败！");
                request.setAttribute("result", "failure");
                return;
            }

            //相关的商品调配库记录
            String condition = "stock_exchange_id = " + bean.getId();
            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id asc");
            Map productMap = new HashMap();
            Iterator sepIter = sepList.listIterator();
            Set keySet = new HashSet();
            int stockInCount = 0;
            int stockOutCount = 0;
            while (sepIter.hasNext()) {
                StockExchangeProductBean sep = (StockExchangeProductBean) sepIter.next();
                String productId = String.valueOf(sep.getProductId());
                if (!keySet.contains(productId)) {
                    keySet.add(productId);
                    voProduct product = (voProduct) wareService.getProduct(sep.getProductId());
                    if (product != null) {
                        productMap.put(Integer.valueOf(product.getId()), product);
                    }
                }
                sep.setPsIn(service.getProductStock("id=" + sep.getStockInId()));
                sep.setPsOut(service.getProductStock("id=" + sep.getStockOutId()));
                stockOutCount += sep.getStockOutCount();
                stockInCount += sep.getStockInCount();
                
                //货位信息
                StockExchangeProductCargoBean sepcIn = service.getStockExchangeProductCargo("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 1");
                StockExchangeProductCargoBean sepcOut = service.getStockExchangeProductCargo("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0");
                CargoProductStockBean cpsIn = null;
                CargoProductStockBean cpsOut = null;
                CargoInfoBean ciIn = null;
                CargoInfoBean ciOut = null;
                if(sepcIn != null && sepcOut != null){
                	List cpsInList = cargoService.getCargoAndProductStockList("cps.id = "+sepcIn.getCargoProductStockId(),0,1,"cps.id asc");
                	List cpsOutList = cargoService.getCargoAndProductStockList("cps.id = "+sepcOut.getCargoProductStockId(),0,1,"cps.id asc");
                	if(cpsInList != null && cpsInList.size() > 0){
                		cpsIn = (CargoProductStockBean)cargoService.getCargoAndProductStockList("cps.id = "+sepcIn.getCargoProductStockId(),0,1,"cps.id asc").get(0);
                	}
                	if(cpsOutList != null && cpsOutList.size() > 0){
                		cpsOut = (CargoProductStockBean)cargoService.getCargoAndProductStockList("cps.id = "+sepcOut.getCargoProductStockId(),0,1,"cps.id asc").get(0);
                	}
                	ciIn = cargoService.getCargoInfo("id = "+sepcIn.getCargoInfoId());
                	ciOut = cargoService.getCargoInfo("id = "+sepcOut.getCargoInfoId());
                	sepcOut.setCargoProductStock(cpsOut);
                    sepcOut.setCargoInfo(ciOut);
                    sep.setSepcOut(sepcOut);
                    sepcIn.setCargoProductStock(cpsIn);
                    sepcIn.setCargoInfo(ciIn);
                    sep.setSepcIn(sepcIn);
                }
                
                //查找是否该商品是否存在IMEI码
                list = imeiService.getIMEIStockExchangeList("stock_exchange_id = " + sep.getStockExchangeId() + " and product_id=" + productId, -1, -1, null);
                if (list != null && list.size() != 0) {
                	for (IMEIStockExchangeBean ibean : list) {
                		ibean.setStockExchangeProductBean(sep);
                		ibean.setImeiBean(imeiService.getIMEI("code='"+ibean.getIMEI() + "'"));
                		imeiStockExchangeList.add(ibean);
                	}
                }
            }

            List reasonList = sService.getTextResList("type=1", -1, -1, null);

            request.setAttribute("bean", bean);
            request.setAttribute("sepList", sepList);
            request.setAttribute("productMap", productMap);
            request.setAttribute("stockInCount", String.valueOf(stockInCount));
            request.setAttribute("stockOutCount", String.valueOf(stockOutCount));
            request.setAttribute("reasonList", reasonList);
            request.setAttribute("imeiStockExchangeList", imeiStockExchangeList);
        } finally {
            service.releaseAll();
        }
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：添加库存调配商品
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void addStockExchangeItem(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	boolean edit = false;

    	synchronized(stockLock){
	        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
	        if (StringUtil.convertNull(productCode).equals("")) {
//	            request.setAttribute("tip", "请输入产品编号或IMEI码！");
	            request.setAttribute("tip", "请输入产品编号！");
	            request.setAttribute("result", "failure");
	            return;
	        }
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
	        
	        int stockOutCount = StringUtil.parstInt(request.getParameter("stockOutCount").trim());
	        int stockInCount = stockOutCount;
	        if (stockOutCount == 0 && stockInCount == 0) {
	            request.setAttribute("tip", "请输入出入库量！");
	            request.setAttribute("result", "failure");
	            return;
	        }
	        
	        IMEIBean imeiBean = null;

	        WareService wareService = new WareService();

	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        voProduct product = wareService.getProduct(productCode);
	        

	        try {
	        	StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	        	if(bean==null){
	        		request.setAttribute("tip", "该调拨单不存在或已删除!");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
//	        	if(bean.getCreateUserId() == user.getId()){
//	        		edit = true;
//	        	}
//
//	        	if(!edit){
//	        		request.setAttribute("tip", "对不起，你没有权限编辑其他人的调拨单！");
//	        		request.setAttribute("result", "failure");
//	        		return;
//	        	}

	        	// 只有 未处理、出库处理中、出库审核未通过 三种状态下 可以 添加 商品库存调货记录
	        	if (bean.getStatus() != StockExchangeBean.STATUS0 && bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS4) {
	        		request.setAttribute("tip", "该操作已经确认，不能再更改！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	//已添加imei数量
	        	
	        	int imeiCount = 0;
	        	
	        	if (product == null) {
					request.setAttribute("tip", "不存在这个编号的产品！");
					request.setAttribute("result", "failure");
					return;
				}
	        	
	        	/*
	        	//商品不存在则传入值可能为IMEI码
	        	if (product == null) {
	        		imeiBean = imeiService.getIMEI("code='" + productCode +"'");
	        		if (imeiBean != null) {
	        			if( bean.getStockOutType() == ProductStockBean.STOCKTYPE_BACK && bean.getStockInType() == ProductStockBean.STOCKTYPE_BACK ) {
	        				product = wareService.getProduct(imeiBean.getProductId());
	        				if (product == null) {
	        					request.setAttribute("tip", "不存在这个编号或IMEI码的产品！");
	        					request.setAttribute("result", "failure");
	        					return;
	        				}
	        			} else {
	        				if (imeiBean.getStatus() == IMEIBean.IMEISTATUS2) {
		        				product = wareService.getProduct(imeiBean.getProductId());
		        				if (product == null) {
		        					request.setAttribute("tip", "不存在这个编号或IMEI码的产品！");
		        					request.setAttribute("result", "failure");
		        					return;
		        				}
		        			} else {
		        					request.setAttribute("tip", "IMEI码的产品不是可出库状态！");
		        					request.setAttribute("result", "failure");
		        					return;
		        			}
	        			}
	        		} else {
	        			request.setAttribute("tip", "不存在这个编号或IMEI码的产品！");
	        			request.setAttribute("result", "failure");
	        			return;
	        		}
	        	}
	        	*/
	        	
	        	 else {
	        		imeiBean = imeiService.getIMEI("product_id=" + product.getId());
//	        		if (imeiBean != null) {
//	        			request.setAttribute("tip", "输入商品编号有IMEI码，必须输入IMEI码！");
//	        			request.setAttribute("result", "failure");
//	        			return;
//	        		}
	        	}

	        	//如果是IMEI码，判断数量是否为1
	        	if (imeiBean != null) {
//	        		if (stockOutCount != 1) {
//	        			request.setAttribute("tip", "输入的是IMEI码，数量只能为出库量只能为1！");
//	        			request.setAttribute("result", "failure");
//	        			return;
//	        		}
	        		imeiCount = imeiService.getIMEIStockExchangeCount("stock_exchange_id=" + exchangeId + " and product_id=" + product.getId());
	        	}
	        	

	        	

	        	

	        	int stockInArea = bean.getStockInArea();
	        	int stockInType = bean.getStockInType();
	        	int stockOutArea = bean.getStockOutArea();
	        	int stockOutType = bean.getStockOutType();

	        	if(stockInArea == stockOutArea && stockInType == stockOutType){
	        		request.setAttribute("tip", "不能在同一个库中调配商品！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	StockAreaBean outArea = service.getStockArea("id=" + stockOutArea);
	        	StockAreaBean inArea = service.getStockArea("id=" + stockInArea);
	        	if(outArea.getAttribute() == StockAreaBean.NO_OUR_WARE && inArea.getAttribute() == StockAreaBean.OUR_WARE){
	        		CargoInfoBean ciOut = cargoService.getCargoInfo("stock_type = "+bean.getStockOutType()+" and area_id = "+ outArea.getId()); 
	        		if(ciOut == null){
	        			request.setAttribute("tip", "找不到非我司仓原货位！");
		        		request.setAttribute("result", "failure");
		        		return;
	        		} 
	        		cargoCode = ciOut.getWholeCode();
	        	} else {
	        		if(stockOutType == ProductStockBean.STOCKTYPE_QUALIFIED&&cargoCode.trim().equals("")){
		        		request.setAttribute("tip", "源库为合格库，货位号不能为空！");
		        		request.setAttribute("result", "failure");
		        		return;
		        	}
	        	}
	        	
	        	if (imeiBean != null) {
	        		//判断源库是不是返厂库
	        		if(stockOutType == ProductStockBean.STOCKTYPE_BACK ){
	        			if(stockInType == ProductStockBean.STOCKTYPE_BACK ) {
	        				
	        			} else {
	        				request.setAttribute("tip", "有IMEI码的商品不能从返厂库调拨！");
	        				request.setAttribute("result", "failure");
	        				return;
	        			}
	        		}
	        	}


	        	ProductStockBean psIn = service.getProductStock("product_id=" + product.getId() + " and type=" + stockInType + " and area=" + stockInArea);
	        	ProductStockBean psOut = service.getProductStock("product_id=" + product.getId() + " and type=" + stockOutType + " and area=" + stockOutArea);

	        	if(psOut == null){
	        		//如果 出货的地方 没有存放这个商品的库，则添加一个空的库，库存为0
	        		request.setAttribute("tip", "库存信息异常，请联系管理员！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	if(psIn == null){
	        		//如果 入库的地方 没有存放这个商品的库，则添加一个空的库，库存为0
	        		request.setAttribute("tip", "库存信息异常，请联系管理员！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}

	        	if (stockOutCount > psOut.getStock()) {
	        		request.setAttribute("tip", "该产品的库存为" + psOut.getStock() + "，库存不足！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}

	        	//添加检验退货库 源库的产品数量是足够--------------------------------------
	        	/* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED ) {
		        	if( !service.checkUnqualifyReturnedProductNumber(productCode, ReturnedProductBean.APPRAISAL_UNQUALIFY, stockOutCount)) {
		        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的不合格商品数量不足库存不足， 不可外调！");
			            request.setAttribute("result", "failure");
			            return;
		        	}
		        }*/
	        	//添加从退货库到合格库的调拨的判断
	        	/* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED ) {
		        	if( !service.checkUnqualifyReturnedProductNumber(productCode, ReturnedProductBean.APPRAISAL_QUALIFY, stockOutCount)) {
		        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的合格商品数量不足库存不足， 不可外调！");
			            request.setAttribute("result", "failure");
			            return;
		        	}
		        }*/

	        	//新货位管理判断
	        	CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockInArea());
	        	CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockOutArea());
	        	List cpsOutList = null;
	        	if(stockOutType == ProductStockBean.STOCKTYPE_QUALIFIED){
	        		cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockOutType()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
	        	}else{
	        		cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockOutType()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = 2", -1, -1, "ci.id asc");
	        	}
	        	if(cpsOutList == null || cpsOutList.size()==0){
	        		request.setAttribute("tip", "源货位号无效，请重新输入！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	CargoProductStockBean cpsOut = (CargoProductStockBean)cpsOutList.get(0);
	        	if(bean.getStockOutType() == ProductStockBean.STOCKTYPE_QUALIFIED && cpsOut.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
	        		request.setAttribute("tip", "合格库不能从缓存区直接调拨！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	if(stockOutCount > cpsOut.getStockCount()){
	        		request.setAttribute("tip", "该货位"+cargoCode+"库存为" + cpsOut.getStockCount() + "，库存不足！");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	CargoInfoBean ciIn = null;
	        	if(outArea.getAttribute() ==  StockAreaBean.OUR_WARE && inArea.getAttribute() ==  StockAreaBean.NO_OUR_WARE){
	        		ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea.getId()); 
	        		if(ciIn == null){
		        		request.setAttribute("tip", "目的库无可用货位，请先添加货位后，再进行调拨操作！");
		        		request.setAttribute("result", "failure");
		        		return;
		        	}
	        	} else {
	        		ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2); 
	        		if(ciIn == null){
		        		request.setAttribute("tip", "目的库无缓存区货位信息，请先添加货位后，再进行调拨操作！");
		        		request.setAttribute("result", "failure");
		        		return;
		        	}
	        	}

	        	//如果是IMEI码，判断是否已添加IMEI码
	        	if (imeiBean != null) {
	        		if (imeiService.getIMEIStockExchange(" stock_exchange_id=" + bean.getId() + " and product_id="+imeiBean.getProductId() + " and imei='"+imeiBean.getCode() +"'") != null) {
	        			request.setAttribute("tip", "该IMEI码已添加，不用重复添加！");
	        			request.setAttribute("result", "failure");
	        			return;
	        		}
	        	} else {
	        		if (service.getStockExchangeProduct("stock_exchange_id = " + exchangeId + " and product_id = " + product.getId() + " and stock_in_id=" + psIn.getId() + " and stock_out_id=" + psOut.getId()) != null) {
	        			request.setAttribute("tip", "该调配操作已经添加，直接修改即可，不用重复添加！");
	        			request.setAttribute("result", "failure");
	        			return;
	        		}
	        	}

	        	List lastInsert = service.getStockExchangeProductList("stock_exchange_id = " + exchangeId, 0, 1, "id asc");
	        	StockExchangeProductBean lastSep = null;
	        	if(lastInsert != null && lastInsert.size() > 0){
	        		lastSep = (StockExchangeProductBean)lastInsert.get(0);
	        	}

	        	//开始事务
	        	service.getDbOp().startTransaction();
	        	StockExchangeProductBean sep = null;
	        	//添加过则更新记录即可，未添加过需添加
	        	if (imeiCount > 0) {
	        		//更新调配记录
	        		if (!service.updateStockExchangeProduct("stock_out_count=stock_out_count+1,stock_in_count=stock_in_count+1", "stock_exchange_id="+exchangeId+" and product_id="+product.getId())) {
	        			request.setAttribute("tip", "更新失败！");
	        			request.setAttribute("result", "failure");
	        			service.getDbOp().rollbackTransaction();
	        			return;
	        		}

	        		sep = service.getStockExchangeProduct("stock_exchange_id="+exchangeId+" and product_id="+product.getId());
	        		if (sep == null) {
	        			request.setAttribute("tip", "没有找到调拨产品信息！");
	        			request.setAttribute("result", "failure");
	        			service.getDbOp().rollbackTransaction();
	        			return;
	        		}
	        		if( sep.getStatus() == StockExchangeProductBean.STOCKOUT_DEALED ) {
	        			request.setAttribute("tip", "商品已经提交，不可以再修改！");
	        			request.setAttribute("result", "failure");
	        			service.getDbOp().rollbackTransaction();
	        			return;
	        		}
	        		if(! service.updateStockExchangeProductCargo("stock_count = stock_count+1", "stock_exchange_product_id=" + sep.getId()))
	        		{
	        			service.getDbOp().rollbackTransaction();
	        			request.setAttribute("tip", "数据库操作失败");
	        			request.setAttribute("result", "failure");
	        			return;
	        		}
	        	} else {
	        		//添加调配记录
	        		sep = new StockExchangeProductBean();
	        		sep.setCreateDatetime(DateUtil.getNow());
	        		sep.setConfirmDatetime(null);
	        		sep.setStockExchangeId(exchangeId);
	        		sep.setProductId(product.getId());
	        		sep.setRemark("");
	        		sep.setStatus(StockExchangeProductBean.STOCKOUT_UNDEAL);
	        		sep.setStockOutCount(stockOutCount);
	        		sep.setStockInCount(stockInCount);
	        		sep.setStockOutId(psOut.getId());
	        		sep.setStockInId(psIn.getId());
	        		if(lastSep == null){
	        			sep.setReason(0);
	        			sep.setReasonText("");
	        		} else {
	        			sep.setReason(lastSep.getReason());
	        			sep.setReasonText(lastSep.getReasonText());
	        		}
	        		if (!service.addStockExchangeProduct(sep)) {
	        			request.setAttribute("tip", "添加失败！");
	        			request.setAttribute("result", "failure");
	        			service.getDbOp().rollbackTransaction();
	        			return;
	        		}

	        		int sepId = service.getDbOp().getLastInsertId();
	        		List cpsInList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockInType()+" and ci.area_id = "+inCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = "+ciIn.getStoreType(), 0, 1, "ci.id asc");
	        		//添加调拨产品货位信息
	        		CargoProductStockBean cpsIn = null;
	        		if(cpsInList == null || cpsInList.size() == 0){
	        			cpsIn = new CargoProductStockBean();
	        			cpsIn.setCargoId(ciIn.getId());
	        			cpsIn.setProductId(product.getId());
	        			if(!cargoService.addCargoProductStock(cpsIn))
	        			{
	        				service.getDbOp().rollbackTransaction();
	        				request.setAttribute("tip", "数据库操作失败");
	        				request.setAttribute("result", "failure");
	        				return;
	        			}
	        			cpsIn.setId(cargoService.getDbOp().getLastInsertId());
	        			if(!cargoService.updateCargoInfo("status = 0", "id = "+ciIn.getId()))
	        			{
	        				service.getDbOp().rollbackTransaction();
	        				request.setAttribute("tip", "数据库操作失败");
	        				request.setAttribute("result", "failure");
	        				return;
	        			}
	        		}else{
	        			cpsIn = (CargoProductStockBean)cpsInList.get(0);
	        		}
	        		StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
	        		sepcOut.setStockExchangeProductId(sepId);
	        		sepcOut.setStockExchangeId(bean.getId());
	        		sepcOut.setStockCount(stockOutCount);
	        		sepcOut.setCargoProductStockId(cpsOut.getId());
	        		sepcOut.setCargoInfoId(cpsOut.getCargoId());
	        		sepcOut.setType(0);
	        		service.addStockExchangeProductCargo(sepcOut);
	        		StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
	        		sepcIn.setStockExchangeProductId(sepId);
	        		sepcIn.setStockExchangeId(bean.getId());
	        		sepcIn.setStockCount(stockOutCount);
	        		sepcIn.setCargoProductStockId(cpsIn.getId());
	        		sepcIn.setCargoInfoId(cpsIn.getCargoId());
	        		sepcIn.setType(1);

	        		if(! service.addStockExchangeProductCargo(sepcIn))
	        		{
	        			service.getDbOp().rollbackTransaction();
	        			request.setAttribute("tip", "数据库操作失败");
	        			request.setAttribute("result", "failure");
	        			return;
	        		}
	        	}

	        	//添加IMEI码与调拨单关系
	        	if (imeiBean != null) {
	        		IMEIStockExchangeBean imeiStockExchangeBean = new IMEIStockExchangeBean();
	        		imeiStockExchangeBean.setIMEI(imeiBean.getCode());
	        		imeiStockExchangeBean.setStockExchangeId(exchangeId);
	        		imeiStockExchangeBean.setProductId(product.getId());
	        		if (!imeiService.addIMEIStockExchange(imeiStockExchangeBean)) {
	        			request.setAttribute("tip", "添加失败！");
	        			request.setAttribute("result", "failure");
	        			service.getDbOp().rollbackTransaction();
	        			return;
	        		}
	        	}

	        	// 如果这个 库存调配单的状态时 未处理，改为出库处理中
	        	if(bean.getStatus() == StockExchangeBean.STATUS0){
	        		if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
	        		{
	        			service.getDbOp().rollbackTransaction();
	        			request.setAttribute("tip", "数据库操作失败");
	        			request.setAttribute("result", "failure");
	        			return;
	        		}
	        	}

	        	//log记录
	        	StockAdminHistoryBean log = new StockAdminHistoryBean();
	        	log.setAdminId(user.getId());
	        	log.setAdminName(user.getUsername());
	        	log.setLogId(bean.getId());
	        	log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
	        	log.setOperDatetime(DateUtil.getNow());
	        	log.setRemark("修改商品调配操作：" + bean.getName() + ",添加了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:" + sep.getStockOutCount());
	        	log.setType(StockAdminHistoryBean.CHANGE);
	        	if(!stockService.addStockAdminHistory(log))
	        	{
	        		service.getDbOp().rollbackTransaction();
	        		request.setAttribute("tip", "数据库操作失败");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	service.getDbOp().commitTransaction();
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    public  static String getFailureProductId(String[] temp , int x){
    	StringBuffer sb = new StringBuffer("");
    	for(int i=x;i<temp.length;i++){
    		sb.append(temp[i]);
    		sb.append("<br/>");
    	}
    	return sb.toString();
    }
    
    /**
     * 
     * 功能:批量新增调拨产品
     * @param request
     * @param response
     */
    public void batchStockExchangeItem(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();
    	boolean edit = false;

    	synchronized(stockLock){
	        String productIdsAndCount = StringUtil.dealParam(request.getParameter("productIdsAndCount"));
	        if (StringUtil.convertNull(productIdsAndCount).equals("")) {
	            request.setAttribute("tip", "请输入产品编号和入库量");
	            request.setAttribute("result", "failure");
	            return;
	        }
	        String[] productCodes = productIdsAndCount.split("\\r\\n");

//	        if(productCodes.length>100){
//	        	request.setAttribute("tip", "最多批量提交一百条数据,超过了"+(productCodes.length-100)+"条数据");
//                request.setAttribute("result", "failure");
//                return;
//	        }
	        
	        String productCode="";
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        //String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
	        int stockOutCount = 0;
	        int stockInCount = 0;
 
	        DbOperation db=new DbOperation();
	        db.init("adult");
	        WareService wareService = new WareService(db);
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	        	IMEIBean imeiBean = null;
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	request.setAttribute("tip", "该调拨单不存在或已删除！");
	                request.setAttribute("result", "noExist");
	                return;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }

	            if(!edit){
	            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
	                request.setAttribute("result", "failure");
	                return;
	            }

	            // 只有 未处理、出库处理中、出库审核未通过 三种状态下 可以 添加 商品库存调货记录
	            if (bean.getStatus() != StockExchangeBean.STATUS0 && bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS4) {
	                request.setAttribute("tip", "该操作已经确认，不能再更改！");
	                request.setAttribute("result", "failure");
	                return;
	            }

		        int stockInArea = bean.getStockInArea();
		        int stockInType = bean.getStockInType();
		        int stockOutArea = bean.getStockOutArea();
		        int stockOutType = bean.getStockOutType();

		        if(stockInArea == stockOutArea && stockInType == stockOutType){
		        	request.setAttribute("tip", "不能在同一个库中调配商品！");
		            request.setAttribute("result", "failure");
		            return;
		        }
		        
		        if(stockOutType == ProductStockBean.STOCKTYPE_QUALIFIED){
		        	request.setAttribute("tip", "批量添加源库不能为合格库！");
		            request.setAttribute("result", "failure");
		            return;
		        }
		        int outAreaAttribute=0;
		        int inAreaAttribute=0;
		        String sqlOut="select attribute from stock_area where id="+stockOutArea+";";
		        String sqlIn="select attribute from stock_area where id="+stockInArea+";";
		        ResultSet rsOut=wareService.getDbOp().executeQuery(sqlOut);
		        if(rsOut.next()){
		        	outAreaAttribute=rsOut.getInt(1);
		        }
		        rsOut.close();
		        ResultSet rsIn=wareService.getDbOp().executeQuery(sqlIn);
		        if(rsIn.next()){
		        	inAreaAttribute=rsIn.getInt(1);
		        }
		        rsIn.close();
	        	if(outAreaAttribute == StockAreaBean.NO_OUR_WARE && inAreaAttribute == StockAreaBean.OUR_WARE){
	        		CargoInfoBean ciOut = cargoService.getCargoInfo("stock_type = "+bean.getStockOutType()+" and area_id = "+ stockOutArea); 
	        		if(ciOut == null){
	        			request.setAttribute("tip", "找不到非我司仓原货位！");
		        		request.setAttribute("result", "failure");
		        		return;
	        		} 
	        	}
		        
		        //已添加imei数量
	        	int imeiCount = 0;
		        for(int i=0;i<productCodes.length;i++){
		        	imeiCount = 0;
		        	String[] productCodeTemp =productCodes[i].split(" "); // \\s+ 手动输入 只能是一个空格
			        if(productCodeTemp.length==1){//如果是从excel中粘贴过来的
			        	productCodeTemp=productCodes[i].split("\\t");
			        }
		        	productCode = productCodeTemp[0].trim();
		        	try{
		        		stockOutCount = Integer.parseInt(productCodeTemp[1].trim());
		        	}catch(Exception e1){
		        		request.setAttribute("tip", "输入格式错误!");
		        		request.setAttribute("message", i+","+(productCodes.length-i));
		        		request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            request.setAttribute("result", "failure");
			            return ;
		        	}
		        	stockInCount = stockOutCount;
		        	
			        if (stockOutCount <= 0 && stockInCount <= 0) {
			            request.setAttribute("tip", "产品"+productCode+" 请输入出入库量！");
			            request.setAttribute("result", "failure");
			            request.setAttribute("message", i+","+(productCodes.length-i));
			            request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            return;
			        }
		        	
		        	voProduct product = wareService.getProduct(productCode);
		        	
		        	//商品不存在则传入值可能为IMEI码
		        	if (product == null) {
		        		if(!group.isFlag(949)){
		        			request.setAttribute("tip", "不存在"+productCode+"这个编号的产品！");
				 	        request.setAttribute("result", "failure");
				 	        request.setAttribute("message", i+","+(productCodes.length-i));
		         	 	    request.setAttribute("productCodes", getFailureProductId(productCodes,i));
				 	        return;
		        		}
		        	}
		        	/*
		        	if (product == null) {
		        		if(!group.isFlag(949)){
		        			request.setAttribute("tip", "不存在"+productCode+"这个编号的产品！");
				 	        request.setAttribute("result", "failure");
				 	        request.setAttribute("message", i+","+(productCodes.length-i));
		         	 	    request.setAttribute("productCodes", getFailureProductId(productCodes,i));
				 	        return;
		        		}
		        		
		        		else{
		        			imeiBean = imeiService.getIMEI("code='" + productCode +"'");
			        		if (imeiBean != null) {
			        			if( bean.getStockOutType() == ProductStockBean.STOCKTYPE_BACK && bean.getStockInType() == ProductStockBean.STOCKTYPE_BACK ) {
			        				product = wareService.getProduct(imeiBean.getProductId());
			        				if (product == null) {
			        					request.setAttribute("tip", "不存在"+productCode+"这个编号或IMEI码的产品！");
			    		 	            request.setAttribute("result", "failure");
			    		 	            request.setAttribute("message", i+","+(productCodes.length-i));
			             	 	        request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			        					return;
			        				}
			        			} else {
			        				if (imeiBean.getStatus() == IMEIBean.IMEISTATUS2) {
				        				product = wareService.getProduct(imeiBean.getProductId());
				        				if (product == null) {
				        					request.setAttribute("tip", "不存在"+productCode+"这个编号或IMEI码的产品！");
				    		 	            request.setAttribute("result", "failure");
				    		 	            request.setAttribute("message", i+","+(productCodes.length-i));
				             	 	        request.setAttribute("productCodes", getFailureProductId(productCodes,i));
				        					return;
				        				}
				        			} else {
				        					request.setAttribute("tip", productCode+"的IMEI码产品不是可出库状态！");
				        					request.setAttribute("result", "failure");
				        					request.setAttribute("message", i+","+(productCodes.length-i));
				        					request.setAttribute("productCodes", getFailureProductId(productCodes,i));
				        					return;
				        			}
			        			}
			        		} else {
			        			request.setAttribute("tip", "不存在"+productCode+"这个编号或IMEI码的产品！");
			        			request.setAttribute("result", "failure");
			        			request.setAttribute("message", i+","+(productCodes.length-i));
	             	 	        request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			        			return;
			        		}
		        		}
		        	}*/
		        	
		        	 else {
		        		imeiBean = imeiService.getIMEI("product_id=" + product.getId());
//		        		if (imeiBean != null) {
//		        			request.setAttribute("tip", productCode+"这个编号的产品有IMEI码，必须输入IMEI码！");
//		        			request.setAttribute("result", "failure");
//		        			request.setAttribute("message", i+","+(productCodes.length-i));
//             	 	        request.setAttribute("productCodes", getFailureProductId(productCodes,i));
//		        			return;
//		        		}
		        	}
		        	
		        	
		        	
		        	//如果是IMEI码，判断数量是否为1
		        	if (imeiBean != null) {
//		        		if (stockOutCount != 1) {
//		        			request.setAttribute("tip", productCode + "是IMEI码，数量只能为只能为1！");
//		        			request.setAttribute("result", "failure");
//		        			request.setAttribute("message", i+","+(productCodes.length-i));
//             	 	        request.setAttribute("productCodes", getFailureProductId(productCodes,i));
//		        			return;
//		        		}
		        		imeiCount = imeiService.getIMEIStockExchangeCount("stock_exchange_id=" + exchangeId + " and product_id=" + product.getId());
		        	}
		        	
		        	if (imeiBean != null) {
		        		//判断源库是不是返厂库
		        		if(stockOutType == ProductStockBean.STOCKTYPE_BACK){
		        			if( stockInType == ProductStockBean.STOCKTYPE_BACK ) {
		        			} else {
		        				request.setAttribute("tip", productCode + "是IMEI码的商品不能从返厂库调拨！");
		        				request.setAttribute("result", "failure");
		        				request.setAttribute("message", i+","+(productCodes.length-i));
		        				request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        				return;
		        			}
		        		}
		        	}
		        	
		            ProductStockBean psIn = service.getProductStock("product_id=" + product.getId() + " and type=" + stockInType + " and area=" + stockInArea);
		            ProductStockBean psOut = service.getProductStock("product_id=" + product.getId() + " and type=" + stockOutType + " and area=" + stockOutArea);
	
		            if(psOut == null){
		            	//如果 出货的地方 没有存放这个商品的库，则添加一个空的库，库存为0
		            	request.setAttribute("tip", "库存信息异常，请联系管理员！");
			            request.setAttribute("result", "failure");
			            request.setAttribute("message", i+","+(productCodes.length-i));
		                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            return;
		            }
		            if(psIn == null){
		            	//如果 入库的地方 没有存放这个商品的库，则添加一个空的库，库存为0
		            	request.setAttribute("tip", "库存信息异常，请联系管理员！");
			            request.setAttribute("result", "failure");
			            request.setAttribute("message", i+","+(productCodes.length-i));
		                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            return;
		            }
	
			        if (stockOutCount > psOut.getStock()) {
			            request.setAttribute("tip", "产品"+productCode+"的库存为" + psOut.getStock() + "，库存不足！");
			            request.setAttribute("message", i+","+(productCodes.length-i));
			            request.setAttribute("result", "failure");
			            request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            return;
		            }
			        
			        //添加退货库库存检验的地方-----------------------------------
			       /* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED) {
			        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_UNQUALIFY, stockOutCount)) {
			        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的不合格商品数量不足库存不足， 不可外调！");
				            request.setAttribute("result", "failure");
				            return;
			        	}
			        }*/
			        
			      //添加从退货库到合格库的调拨的判断
			        /*if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED ) {
			        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_QUALIFY, stockOutCount)) {
			        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的合格商品数量不足库存不足， 不可外调！");
				            request.setAttribute("result", "failure");
				            return;
			        	}
			        }*/
			        
			        //新货位管理判断
			        CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockInArea());
			        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockOutArea());
			        List cpsOutList = null;
			     
			        cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockOutType()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = 2", -1, -1, "ci.id asc");
			         
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	request.setAttribute("tip", "源货位号无效，请重新输入！");
			        	request.setAttribute("message", i+","+(productCodes.length-i));
			        	request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            request.setAttribute("result", "failure");
			            return;
			        }
			        CargoProductStockBean cpsOut = (CargoProductStockBean)cpsOutList.get(0);
 
			        if(stockOutCount > cpsOut.getStockCount()){
			        	request.setAttribute("tip", "该货位"+cpsOut.getCargoInfo().getCode()+"库存为" + cpsOut.getStockCount() + "，库存不足！");
			        	request.setAttribute("message", i+","+(productCodes.length-i));
			        	request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			            request.setAttribute("result", "failure");
			            return;
			        }
			        CargoInfoBean ciIn = null;
			        if(outAreaAttribute ==  StockAreaBean.OUR_WARE && inAreaAttribute ==  StockAreaBean.NO_OUR_WARE){
		        		ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea.getId()); 
		        		if(ciIn == null){
			        		request.setAttribute("tip", "目的库无可用货位，请先添加货位后，再进行调拨操作！");
			        		request.setAttribute("result", "failure");
			        		return;
			        	}
		        	} else {
		        		ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2); 
		        		if(ciIn == null){
			        		request.setAttribute("tip", "目的库无缓存区货位信息，请先添加货位后，再进行调拨操作！");
			        		request.setAttribute("result", "failure");
			        		return;
			        	}
		        	}
	
		            //如果是IMEI码，判断是否已添加IMEI码
		        	if (imeiBean != null) {
		        		if (imeiService.getIMEIStockExchange(" stock_exchange_id=" + bean.getId() + " and product_id="+imeiBean.getProductId() + " and imei='"+imeiBean.getCode() +"'") != null) {
		        			request.setAttribute("tip", "IMEI码"+imeiBean.getCode()+"已添加，不能重复添加！");
		        			request.setAttribute("result", "failure");
		        			request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        			return;
		        		}
		        	} else {
		        		if (service.getStockExchangeProduct("stock_exchange_id = " + exchangeId + " and product_id = " + product.getId() + " and stock_in_id=" + psIn.getId() + " and stock_out_id=" + psOut.getId()) != null) {
		        			request.setAttribute("tip", "商品"+product.getCode()+"已经添加，直接修改即可，不用重复添加！");
			                request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			                request.setAttribute("result", "failure");
		        			return;
		        		}
		        	}
	
		            List lastInsert = service.getStockExchangeProductList("stock_exchange_id = " + exchangeId, 0, 1, "id asc");
		            StockExchangeProductBean lastSep = null;
		            if(lastInsert != null && lastInsert.size() > 0){
		            	lastSep = (StockExchangeProductBean)lastInsert.get(0);
		            }
	
		            //开始事务
		            service.getDbOp().startTransaction();
		            StockExchangeProductBean sep = null;
		            //添加过则更新记录即可，未添加过需添加
		        	if (imeiCount > 0) {
		        		//更新调配记录
		        		if (!service.updateStockExchangeProduct("stock_out_count=stock_out_count+1,stock_in_count=stock_in_count+1", "stock_exchange_id="+exchangeId+" and product_id="+product.getId())) {
		        			request.setAttribute("tip", "更新失败！");
		        			request.setAttribute("result", "failure");
		        			request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        			service.getDbOp().rollbackTransaction();
		        			return;
		        		}

		        		sep = service.getStockExchangeProduct("stock_exchange_id="+exchangeId+" and product_id="+product.getId());
		        		if (sep == null) {
		        			request.setAttribute("tip", "没有找到调拨产品信息！");
		        			request.setAttribute("result", "failure");
		        			request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        			service.getDbOp().rollbackTransaction();
		        			return;
		        		}
		        		if( sep.getStatus() == StockExchangeProductBean.STOCKOUT_DEALED ) {
		        			request.setAttribute("tip", "这种调拨商品已经提交不可以再修改！");
		        			request.setAttribute("result", "failure");
		        			request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        			service.getDbOp().rollbackTransaction();
		        			return;
		        		}

		        		if(! service.updateStockExchangeProductCargo("stock_count = stock_count+1", "stock_exchange_product_id=" + sep.getId()))
		        		{
		        			service.getDbOp().rollbackTransaction();
		        			request.setAttribute("tip", "数据库操作失败");
		        			request.setAttribute("result", "failure");
		        			request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        			return;
		        		}
		        	} else {
			            //添加调配记录
			            sep = new StockExchangeProductBean();
			            sep.setCreateDatetime(DateUtil.getNow());
			            sep.setConfirmDatetime(null);
			            sep.setStockExchangeId(exchangeId);
			            sep.setProductId(product.getId());
			            sep.setRemark("");
			            sep.setStatus(StockExchangeProductBean.STOCKOUT_UNDEAL);
			            sep.setStockOutCount(stockOutCount);
			            sep.setStockInCount(stockInCount);
			            sep.setStockOutId(psOut.getId());
			            sep.setStockInId(psIn.getId());
			            if(lastSep == null){
				            sep.setReason(0);
				            sep.setReasonText("");
			            } else {
			            	sep.setReason(lastSep.getReason());
				            sep.setReasonText(lastSep.getReasonText());
			            }
			            if (!service.addStockExchangeProduct(sep)) {
			                request.setAttribute("tip", "添加失败！");
			                request.setAttribute("result", "failure");
			                request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
			                service.getDbOp().rollbackTransaction();
			                return;
			            }
			            
			            //添加调拨产品货位信息
			            int sepId = service.getDbOp().getLastInsertId();
			            List cpsInList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockInType()+" and ci.area_id = "+inCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2, 0, 1, "ci.id asc");
			            CargoProductStockBean cpsIn = null;
				        if(cpsInList == null || cpsInList.size() == 0){
				        	cpsIn = new CargoProductStockBean();
				        	cpsIn.setCargoId(ciIn.getId());
				        	cpsIn.setProductId(product.getId());
				        	cargoService.addCargoProductStock(cpsIn);
				        	cpsIn.setId(cargoService.getDbOp().getLastInsertId());
							if(!cargoService.updateCargoInfo("status = 0", "id = "+ciIn.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  request.setAttribute("message", i+","+(productCodes.length-i));
				              request.setAttribute("productCodes", getFailureProductId(productCodes,i));
							  return;
							}
				        }else{
				        	cpsIn = (CargoProductStockBean)cpsInList.get(0);
				        }
				        StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
				        sepcOut.setStockExchangeProductId(sepId);
				        sepcOut.setStockExchangeId(bean.getId());
				        sepcOut.setStockCount(stockOutCount);
				        sepcOut.setCargoProductStockId(cpsOut.getId());
				        sepcOut.setCargoInfoId(cpsOut.getCargoId());
				        sepcOut.setType(0);
				        if(!service.addStockExchangeProductCargo(sepcOut))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  request.setAttribute("message", i+","+(productCodes.length-i));
			              request.setAttribute("productCodes", getFailureProductId(productCodes,i));
						  return;
						}
				        StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
				        sepcIn.setStockExchangeProductId(sepId);
				        sepcIn.setStockExchangeId(bean.getId());
				        sepcIn.setStockCount(stockOutCount);
				        sepcIn.setCargoProductStockId(cpsIn.getId());
				        sepcIn.setCargoInfoId(cpsIn.getCargoId());
				        sepcIn.setType(1);
				        if(! service.addStockExchangeProductCargo(sepcIn))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  request.setAttribute("message", i+","+(productCodes.length-i));
			              request.setAttribute("productCodes", getFailureProductId(productCodes,i));
						  return;
						}
		        	}
		        	
		        	//添加IMEI码与调拨单关系
		        	if (imeiBean != null) {
		        		IMEIStockExchangeBean imeiStockExchangeBean = new IMEIStockExchangeBean();
		        		imeiStockExchangeBean.setIMEI(imeiBean.getCode());
		        		imeiStockExchangeBean.setStockExchangeId(exchangeId);
		        		imeiStockExchangeBean.setProductId(product.getId());
		        		if (!imeiService.addIMEIStockExchange(imeiStockExchangeBean)) {
		        			request.setAttribute("tip", "添加失败！");
		        			request.setAttribute("result", "failure");
		        			request.setAttribute("message", i+","+(productCodes.length-i));
			                request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		        			service.getDbOp().rollbackTransaction();
		        			return;
		        		}
		        	}
		        	
		            // 如果这个 库存调配单的状态时 未处理，改为出库处理中
		            if(bean.getStatus() == StockExchangeBean.STATUS0){
		            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
		            	{
		            	  service.getDbOp().rollbackTransaction();
		            	  request.setAttribute("tip", "数据库操作失败");
		            	  request.setAttribute("result", "failure");
		            	  request.setAttribute("message", i+","+(productCodes.length-i));
			              request.setAttribute("productCodes", getFailureProductId(productCodes,i));
		            	  return;
		            	}
		            	
		            }
	
		            //log记录
		            StockAdminHistoryBean log = new StockAdminHistoryBean();
		            log.setAdminId(user.getId());
		            log.setAdminName(user.getUsername());
		            log.setLogId(bean.getId());
		            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		            log.setOperDatetime(DateUtil.getNow());
		            log.setRemark("修改商品调配操作：" + bean.getName() + ",添加了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:" + sep.getStockOutCount());
		            log.setType(StockAdminHistoryBean.CHANGE);
		            if(!stockService.addStockAdminHistory(log))
	            	{
	            	  service.getDbOp().rollbackTransaction();
	            	  request.setAttribute("tip", "数据库操作失败");
	            	  request.setAttribute("result", "failure");
	            	  request.setAttribute("message", i+","+(productCodes.length-i));
		              request.setAttribute("productCodes", getFailureProductId(productCodes,i));
	            	  return;
	            	}
		            service.getDbOp().commitTransaction();
		        }
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
	        	service.releaseAll();
	        }
    	}
    }
    
    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-7-8
     * 
     * 说明：根据 productId 批量添加调拨商品
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void addStockExchangeItem2(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	boolean edit = false;

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
    		String [] productIds = request.getParameterValues("productId");
    		if(productIds == null || productIds.length == 0){
    			request.setAttribute("tip", "请选择调拨商品！");
	            request.setAttribute("result", "failure");
	            return;
    		}

	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	request.setAttribute("tip", "该调拨单不存在或已删除!");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }

	            if(!edit){
	            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
	                request.setAttribute("result", "failure");
	                return;
	            }

	            // 只有 未处理、出库处理中、出库审核未通过 三种状态下 可以 添加 商品库存调货记录
	            if (bean.getStatus() != StockExchangeBean.STATUS0 && bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS4) {
	                request.setAttribute("tip", "该操作已经确认，不能再更改！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            StringBuilder tipBuf = new StringBuilder();

	            //开始事务
	            service.getDbOp().startTransaction();

	            for(int j=0; j<productIds.length; j++){
		        	int productId = StringUtil.toInt(productIds[j]);
			        int stockOutCount = StringUtil.StringToId(request.getParameter("stockOutCount" + productId));
			        int stockInCount = stockOutCount;
			        if (stockOutCount == 0 && stockInCount == 0) {
			            request.setAttribute("tip", "请输入出入库量！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
	
			        WareService wareService = new WareService();
			        voProduct product = wareService.getProduct(productId);
			        wareService.releaseAll();
	
			        if (product == null) {
			            request.setAttribute("tip", "不存在这个编号的产品！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        } else {
			        	IMEIBean imeiBean = imeiService.getIMEI("product_id=" + product.getId());
			        	if (imeiBean != null) {
			        		request.setAttribute("tip", "所选商品有IMEI码，不能使用此功能添加商品！");
			        		request.setAttribute("result", "failure");
			        		service.getDbOp().rollbackTransaction();
			        		return;
			        	}
			        }
	
			        int stockInArea = bean.getStockInArea();
			        int stockInType = bean.getStockInType();
			        int stockOutArea = bean.getStockOutArea();
			        int stockOutType = bean.getStockOutType();
	
			        if(stockInArea == stockOutArea && stockInType == stockOutType){
			        	request.setAttribute("tip", "不能在同一个库中调配商品！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        
			        String cargoCode = StringUtil.convertNull(request.getParameter("cpsOut" + productId));
			        if(cargoCode.equals("")){
			        	request.setAttribute("tip", "货位号不能为空！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
	
		            ProductStockBean psIn = service.getProductStock("product_id=" + product.getId() + " and type=" + stockInType + " and area=" + stockInArea);
		            ProductStockBean psOut = service.getProductStock("product_id=" + product.getId() + " and type=" + stockOutType + " and area=" + stockOutArea);
	
		            if(psOut == null){
		            	//如果 出货的地方 没有存放这个商品的库，则添加一个空的库，库存为0
		            	request.setAttribute("tip", "库存信息异常，请联系管理员！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
		            }
		            if(psIn == null){
		            	//如果 入库的地方 没有存放这个商品的库，则添加一个空的库，库存为0
		            	request.setAttribute("tip", "库存信息异常，请联系管理员！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
		            }
	
			        if (stockOutCount > psOut.getStock()) {
			            request.setAttribute("tip", "该产品的库存为" + psOut.getStock() + "，库存不足！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
		            }
			        
			      //添加退货库库存检验的地方-----------------------------------
			        /*if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED) {
			        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_UNQUALIFY, stockOutCount)) {
			        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的不合格商品数量不足库存不足， 不可外调！");
				            request.setAttribute("result", "failure");
				            service.getDbOp().rollbackTransaction();
				            return;
			        	}
			        }*/
			      //添加从退货库到合格库的调拨的判断
			       /* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED ) {
			        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_QUALIFY, stockOutCount)) {
			        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的合格商品数量不足库存不足， 不可外调！");
				            request.setAttribute("result", "failure");
				            service.getDbOp().rollbackTransaction();
				            return;
			        	}
			        }*/
			      //新货位管理判断
			        CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockInArea());
			        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockOutArea());
			        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockOutType()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	request.setAttribute("tip", "货位号无效，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        CargoProductStockBean cpsOut = (CargoProductStockBean)cpsOutList.get(0);
			        if(bean.getStockOutType() == ProductStockBean.STOCKTYPE_QUALIFIED && cpsOut.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
			        	request.setAttribute("tip", "合格库不能从缓存区直接调拨！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        if(stockOutCount > cpsOut.getStockCount()){
			        	request.setAttribute("tip", "该货位"+cargoCode+"库存为" + cpsOut.getStockCount() + "，库存不足！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
			        
			        
			        CargoInfoBean ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2); 
			        if(ciIn == null){
			        	request.setAttribute("tip", "目的库无缓存区货位信息，请先添加货位后，再进行调拨操作！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
			            return;
			        }
	
		            if (service.getStockExchangeProduct("stock_exchange_id = " + exchangeId + " and product_id = " + product.getId() + " and stock_in_id=" + psIn.getId() + " and stock_out_id=" + psOut.getId()) != null) {
		            	if(tipBuf.length() > 0){
		            		tipBuf.append(",");
		            	}
		            	tipBuf.append(product.getOriname());
		            	continue;
//		                request.setAttribute("tip", "该调配操作已经添加，直接修改即可，不用重复添加！");
//		                request.setAttribute("result", "failure");
//		                return;
		            }
	
		            List lastInsert = service.getStockExchangeProductList("stock_exchange_id = " + exchangeId, 0, 1, "id asc");
		            StockExchangeProductBean lastSep = null;
		            if(lastInsert != null && lastInsert.size() > 0){
		            	lastSep = (StockExchangeProductBean)lastInsert.get(0);
		            }

		            //添加调配记录
		            StockExchangeProductBean sep = null;
		            sep = new StockExchangeProductBean();
		            sep.setCreateDatetime(DateUtil.getNow());
		            sep.setConfirmDatetime(null);
		            sep.setStockExchangeId(exchangeId);
		            sep.setProductId(product.getId());
		            sep.setRemark("");
		            sep.setStatus(StockExchangeProductBean.STOCKOUT_UNDEAL);
		            sep.setStockOutCount(stockOutCount);
		            sep.setStockInCount(stockInCount);
		            sep.setStockOutId(psOut.getId());
		            sep.setStockInId(psIn.getId());
		            if(lastSep == null){
			            sep.setReason(0);
			            sep.setReasonText("");
		            } else {
		            	sep.setReason(lastSep.getReason());
			            sep.setReasonText(lastSep.getReasonText());
		            }
		            if (!service.addStockExchangeProduct(sep)) {
		                request.setAttribute("tip", "添加失败！");
		                request.setAttribute("result", "failure");
		                service.getDbOp().rollbackTransaction();
		                return;
		            }
		            
		          //添加调拨产品货位信息
		            int sepId = service.getDbOp().getLastInsertId();
		            List cpsInList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockInType()+" and ci.area_id = "+inCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2, 0, 1, "ci.id asc");
		            CargoProductStockBean cpsIn = null;
			        if(cpsInList == null || cpsInList.size() == 0){
			        	cpsIn = new CargoProductStockBean();
			        	cpsIn.setCargoId(ciIn.getId());
			        	cpsIn.setProductId(product.getId());
			        	if(!cargoService.addCargoProductStock(cpsIn))
			        	{
			        	  service.getDbOp().rollbackTransaction();
			        	  request.setAttribute("tip", "数据库操作失败");
			        	  request.setAttribute("result", "failure");
			        	  return;
			        	}
			        	cpsIn.setId(cargoService.getDbOp().getLastInsertId());
			        	if(!cargoService.updateCargoInfo("status = 0", "id = "+ciIn.getId()))
			        	{
			        	  service.getDbOp().rollbackTransaction();
			        	  request.setAttribute("tip", "数据库操作失败");
			        	  request.setAttribute("result", "failure");
			        	  return;
			        	}
			        }else{
			        	cpsIn = (CargoProductStockBean)cpsInList.get(0);
			        }
			        StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
			        sepcOut.setStockExchangeProductId(sepId);
			        sepcOut.setStockExchangeId(bean.getId());
			        sepcOut.setStockCount(stockOutCount);
			        sepcOut.setCargoProductStockId(cpsOut.getId());
			        sepcOut.setCargoInfoId(cpsOut.getCargoId());
			        sepcOut.setType(0);
			        if(!service.addStockExchangeProductCargo(sepcOut))
		        	{
		        	  service.getDbOp().rollbackTransaction();
		        	  request.setAttribute("tip", "数据库操作失败");
		        	  request.setAttribute("result", "failure");
		        	  return;
		        	}
			        StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
			        sepcIn.setStockExchangeProductId(sepId);
			        sepcIn.setStockExchangeId(bean.getId());
			        sepcIn.setStockCount(stockOutCount);
			        sepcIn.setCargoProductStockId(cpsIn.getId());
			        sepcIn.setCargoInfoId(cpsIn.getCargoId());
			        sepcIn.setType(1);
			        if(!service.addStockExchangeProductCargo(sepcIn))
		        	{
		        	  service.getDbOp().rollbackTransaction();
		        	  request.setAttribute("tip", "数据库操作失败");
		        	  request.setAttribute("result", "failure");
		        	  return;
		        	}
		            //log记录
		            StockAdminHistoryBean log = new StockAdminHistoryBean();
		            log.setAdminId(user.getId());
		            log.setAdminName(user.getUsername());
		            log.setLogId(bean.getId());
		            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		            log.setOperDatetime(DateUtil.getNow());
		            log.setRemark("修改商品调配操作：" + bean.getName() + ",添加了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:" + sep.getStockOutCount());
		            log.setType(StockAdminHistoryBean.CHANGE);
		            if(!stockService.addStockAdminHistory(log))
		        	{
		        	  service.getDbOp().rollbackTransaction();
		        	  request.setAttribute("tip", "数据库操作失败");
		        	  request.setAttribute("result", "failure");
		        	  return;
		        	}
		        }

	            // 如果这个 库存调配单的状态时 未处理，改为出库处理中
	            if(bean.getStatus() == StockExchangeBean.STATUS0){
	            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
		        	{
		        	  service.getDbOp().rollbackTransaction();
		        	  request.setAttribute("tip", "数据库操作失败");
		        	  request.setAttribute("result", "failure");
		        	  return;
		        	}
	            }
	            if(tipBuf.length() > 0){
	            	request.setAttribute("tipBuf", tipBuf.toString());
	            }

	            service.getDbOp().commitTransaction();
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：修改库存调配单 调配商品数量
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void editStockExchangeItem(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	boolean edit = false;

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        String back = StringUtil.dealParam(request.getParameter("back"));
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ISystemService sService = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        WareService wareService = new WareService(service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	request.setAttribute("tip", "该调拨单不存在或已删除!");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }

	            if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS8) {
	                request.setAttribute("tip", "该操作已经完成，不能再修改！");
	                request.setAttribute("result", "failure");
	                return;
	            }

	            // 出库审核通过之前， 只能由创建者修改 调拨单的数据
	            if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() != StockExchangeBean.STATUS4){
		            if(!edit){
		            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
		                request.setAttribute("result", "failure");
		                return;
		            }
	            }

	            //相关的产品
	            int stockOutCount = 0;
	            int stockInCount = 0;
	            int reason = 0;
	            boolean hasLoss = false;
	            //相关的调配操作
	            List sepList = service.getStockExchangeProductList("stock_exchange_id=" + exchangeId, -1, -1, null);

	            Iterator itr = sepList.iterator();
	            //开始事务
	            service.getDbOp().startTransaction();
	            voProduct product = null;
	            ProductStockBean psIn = null;
	            ProductStockBean psOut = null;
	            StockExchangeProductBean sep = null;
	            Map psMap = new HashMap();

	            while (itr.hasNext()) {
	                //调配操作记录
	                sep = (StockExchangeProductBean) itr.next();
	                product = wareService.getProduct(sep.getProductId());
	                psIn = (ProductStockBean)psMap.get("psIn" + sep.getStockInId());
	                if(psIn == null){
	                	psIn = service.getProductStock("id=" + sep.getStockInId());
	                	psMap.put("psIn" + sep.getStockInId(), psIn);
	                }
	                psOut = (ProductStockBean)psMap.get("psOut" + sep.getStockOutId());
	                if(psOut == null){
	                	psOut = service.getProductStock("id=" + sep.getStockOutId());
	                	psMap.put("psOut" + sep.getStockOutId(), psOut);
	                }

	                //该产品已经处理完成
	                if (sep.status == StockExchangeProductBean.STOCKOUT_DEALED || sep.status == StockExchangeProductBean.STOCKIN_DEALED) {
	                    continue;
	                }

	                stockOutCount = StringUtil.StringToId(request.getParameter("stockOutCount" + sep.getId()).trim());
	                //stockInCount = StringUtil.StringToId(request.getParameter("stockInCount" + sep.getId()));
	                stockInCount = stockOutCount;
	                reason = StringUtil.StringToId(request.getParameter("reason" + sep.getId()));

	                //没做改变
	                if (sep.getStockInCount() == stockInCount && sep.getStockOutCount() == stockOutCount && sep.getReason() == reason) {
	                    continue;
	                }
	                if (stockOutCount == 0 && stockInCount == 0) {
	                    request.setAttribute("tip", "请输入正确的出入库量！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }
	                

	                if (stockOutCount > 0 && stockInCount != stockOutCount) {
	                    hasLoss = true;
	                }
	                if (stockOutCount <= 0) {
	                    request.setAttribute("tip", product.getName() + "必须有出库量！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }
	                if (stockOutCount > 0 && stockInCount > stockOutCount) {
	                    request.setAttribute("tip", product.getName() + "入库量不能大于出库量！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }
	                if (stockOutCount > 0 && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
	                    if (stockOutCount > psOut.getStock()) {
		                        request.setAttribute("tip", product.getName() + "的库存不足！");
		                        request.setAttribute("result", "failure");
		                        service.getDbOp().rollbackTransaction();
		                        return;
	                	} else {
	                		psOut.setStock(psOut.getStock() - stockOutCount);
	                	}
	                    
	                }
	              //添加退货库库存检验的地方-----------------------------------
                   /* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED ) {
			        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_UNQUALIFY, stockOutCount)) {
			        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的不合格商品数量不足库存不足， 不可外调！");
				            request.setAttribute("result", "failure");
				            service.getDbOp().rollbackTransaction();
				            return;
			        	}
			        }*/ 
                    
                  //添加从退货库到合格库的调拨的判断
    		       /* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED ) {
    		        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_QUALIFY, stockOutCount)) {
    		        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的合格商品数量不足库存不足， 不可外调！");
    			            request.setAttribute("result", "failure");
    			            service.getDbOp().rollbackTransaction();
    			            return;
    		        	}
    		        }*/
	                if (reason <= 0){
	                	request.setAttribute("tip", "必须选择调拨原因！");
                        request.setAttribute("result", "failure");
                        service.getDbOp().rollbackTransaction();
                        return;
	                }
	                TextResBean tr = sService.getTextRes("id=" + reason);
	                if(tr == null){
	                	request.setAttribute("tip", "没有这个调拨原因！");
                        request.setAttribute("result", "failure");
                        service.getDbOp().rollbackTransaction();
                        return;
	                }
	                
	                if(!service.updateStockExchangeProduct("reason_text='" + tr.getContent() + "', reason=" + reason, "id=" + sep.getId()))
                    {
                      service.getDbOp().rollbackTransaction();
                      request.setAttribute("tip", "数据库操作失败");
                      request.setAttribute("result", "failure");
                      return;
                    }
	                //如果产品对应有IMEI码则只处理reason
	                if (imeiService.getIMEICount("product_id="+ sep.getProductId()) > 0) {
	                	continue;
	                }

	                //出库记录
	                if ((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
	                    if(!service.updateStockExchangeProduct("stock_out_count=" + stockOutCount + ", stock_in_count=" + stockOutCount, "id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                    if(!service.updateStockExchangeProductCargo("stock_count = "+stockOutCount, "stock_exchange_product_id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                }
	                if((bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5 || bean.getStatus() == StockExchangeBean.STATUS8) && sep.getStatus() == StockExchangeProductBean.STOCKIN_UNDEAL){
	                	if(!service.updateStockExchangeProduct("stock_in_count=" + stockInCount, "id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                	if(!service.updateStockExchangeProductCargo("stock_count = "+stockOutCount, "stock_exchange_product_id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                }
		            //log记录
		            StockAdminHistoryBean log = new StockAdminHistoryBean();
		            log.setAdminId(user.getId());
		            log.setAdminName(user.getUsername());
		            log.setLogId(bean.getId());
		            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		            log.setOperDatetime(DateUtil.getNow());
		            log.setRemark("修改商品调配操作：" + bean.getName() + ",修改了了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:[" + sep.getStockOutCount() + "->" + stockOutCount + "]");
		            log.setType(StockAdminHistoryBean.CHANGE);
		            if(!stockService.addStockAdminHistory(log))
                    {
                      service.getDbOp().rollbackTransaction();
                      request.setAttribute("tip", "数据库操作失败");
                      request.setAttribute("result", "failure");
                      return;
                    }
	            }

	            // 如果这个 库存调配单的状态时 未处理/出库审核未通过，改为出库处理中
	            if(bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS4){
	            	 if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
	                 {
	                    service.getDbOp().rollbackTransaction();
	                    request.setAttribute("tip", "数据库操作失败");
	                    request.setAttribute("result", "failure");
	                    return;
	                 }
	            }
	            //如果这个 库存调配单的状态时 出库审核通过待入库/入库审核未通过，改为入库处理中
	            if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS8){
	            	 if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS5, "id=" + bean.getId()))
	                 {
	                    service.getDbOp().rollbackTransaction();
	                    request.setAttribute("tip", "数据库操作失败");
	                    request.setAttribute("result", "failure");
	                    return;
	                 }
	            }

	            //提交事务
	            service.getDbOp().commitTransaction();
	            request.setAttribute("back", back);
	            if (hasLoss) {
	                request.setAttribute("result", "hasLoss");
	            }
	        } catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}finally {
	            service.releaseAll();
	        }
    	}
    }
    

    /**
     * 
     * 功能:批量确认或入库调拨产品
     * @param request
     * @param response
     */
  public void batchCompleteStockExchange(HttpServletRequest request, HttpServletResponse response){
	    voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean edit = false;
		boolean stockIn = group.isFlag(83);
		boolean stockOut = group.isFlag(82);
		synchronized(stockLock){
			int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
			String[] stockExchangeProductIds = request.getParameterValues("stockExchangeProductIds");
            int hisStatus = StringUtil.toInt(request.getParameter("hisStatus"));
            
            if(stockExchangeProductIds==null || stockExchangeProductIds.length<1){
            	request.setAttribute("tip", "请选择调拨产品");
                request.setAttribute("result", "failure");
                return;
            }
            String stockProductIds="";
            for(int i=0;i<stockExchangeProductIds.length;i++){
            	stockProductIds+=stockExchangeProductIds[i]+",";
            }
            stockProductIds=stockProductIds.substring(0,stockProductIds.length()-1);
            
            WareService wareService = new WareService();
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	request.setAttribute("tip", "该调拨单不存在或已删除!");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }
	            
	            //当 调配单 的状态 不是 “未处理” 或者 “检测处理中” 的时候，不能进行确认操作
	            if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS5 && bean.getStatus() != StockExchangeBean.STATUS6 && bean.getStatus() != StockExchangeBean.STATUS8) {
	                request.setAttribute("tip", "该操作已经完成，不能再更改！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            if(service.getStockExchangeProductCount("stock_exchange_id=" + bean.getId()) <= 0){
	            	request.setAttribute("tip", "没有要调拨的商品，不能执行该操作！");
	                request.setAttribute("result", "failure");
	                return;
	            }

            	//如果当前状态为 未处理，则要将相关产品的 处理状态 改为 第一步处理完成
            	if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS2 || bean.getStatus() == StockExchangeBean.STATUS4){
            		if(!stockOut){
                		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
    	                request.setAttribute("result", "failure");
    	                return;
                	}
            		
                	if(!edit){
    	            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
    	                request.setAttribute("result", "failure");
    	                return;
    	            }
	            	StringBuilder buf = new StringBuilder();
		            String condition = null;
		            
		            buf.append("stock_exchange_id = ");
		            buf.append(bean.getId());
		            if (!stockProductIds.equals("")) {
		                buf.append(" and id in (");
		                buf.append(stockProductIds);
		                buf.append(") ");
		            }
		            condition = buf.toString();
	                if(hisStatus == -1){
	                	hisStatus = StockExchangeProductBean.STOCKOUT_DEALED;
	                }else if(hisStatus == StockExchangeProductBean.STOCKIN_DEALED || hisStatus == StockExchangeProductBean.STOCKIN_UNDEAL){
	                	request.setAttribute("tip", "调拨单状态错误，操作失败");
						request.setAttribute("result", "failure");
						return;
	                }
	                
		            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
		            Iterator itr = sepList.iterator();
		            StockExchangeProductBean sep = null;
		            voProduct product = null;
		            String set = null;
		            while (itr.hasNext()) {
		                sep = (StockExchangeProductBean) itr.next();
		                product = wareService.getProduct(sep.getProductId());
						if(sep.getReason() <= 0 || StringUtil.isNull(sep.getReasonText())){
							//request.setAttribute("tip", product.getName() + "没有设置调拨原因！");
							//request.setAttribute("result", "failure");
							//return;
							continue;
						}
						
						service.getDbOp().startTransaction();//开始事务
						ProductStockBean psOut = null;
						ProductStockBean psIn = null;
						// 如果是 没处理的 记录，在确认出库的时候，就要把 库存数量锁定
						if (hisStatus == StockExchangeProductBean.STOCKOUT_DEALED && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
							product = wareService.getProduct(sep.getProductId());
							psIn = service.getProductStock("id=" + sep.getStockInId());
							psOut = service.getProductStock("id=" + sep.getStockOutId());
							
							if (sep.getStockOutCount() > psOut.getStock()) {
								service.getDbOp().rollbackTransaction();
								continue;
							}
							set = "remark = '操作前库存"
									+ psOut.getStock() + ",操作后库存"
									+ (psOut.getStock() - sep.getStockOutCount())
									+ "', confirm_datetime = now()";
							if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
							if(!service.updateProductStockCount(sep.getStockOutId(), -sep.getStockOutCount())){
								service.getDbOp().rollbackTransaction();
			                    continue;
							}
							if(!service.updateProductLockCount(sep.getStockOutId(), sep.getStockOutCount())){
								service.getDbOp().rollbackTransaction();
			                    continue;
							}
							
							//如果是从合格库调拨出去，则需要自动检测库存，如果合格库的库存为0，则隐藏商品
							if(psOut.getType()==ProductStockBean.STOCKTYPE_QUALIFIED && psIn.getType() != ProductStockBean.STOCKTYPE_QUALIFIED 
									&& (product.getParentId1() == 123 || product.getParentId1() == 143
									|| product.getParentId1() == 316 || product.getParentId1() == 317
									|| product.getParentId1() == 119 || product.getParentId1() == 340
									|| product.getParentId1() == 1385 || product.getParentId1() == 1425 
									|| product.getParentId1() == 544 || product.getParentId1() == 545
									|| product.getParentId1() == 458 || product.getParentId1() == 459 
									|| product.getParentId1() == 401 || product.getParentId1() == 136 
									|| product.getParentId2() == 203 || product.getParentId2() == 204 
									|| product.getParentId2() == 205 || product.getParentId2() == 699
									|| product.getParentId1() == 145 || product.getParentId1() == 151
									|| product.getParentId1() == 197 || product.getParentId1() == 505
									|| product.getParentId1() == 163 || product.getParentId1() == 690
									|| product.getParentId1() == 908|| product.getParentId1() == 752
									|| product.getParentId1() == 803
									|| product.getParentId1() == 183 || product.getParentId1() == 184
									|| product.getParentId1() == 1093 || product.getParentId1() == 1094
									|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222)){
								service.checkProductStatus(sep.getProductId());
							}
	
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]出库");
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
							boolean flag=true;
							//锁定货位库存
							//出库
							List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
							for(int i=0;i<sepcOutList.size();i++){
								StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
								if(!cargoService.updateCargoProductStockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
			                    	//request.setAttribute("tip", "商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
			                    	//request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				//return;
			                    	flag=false;
			                    	break;
			                    }
								if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
			                    	//request.setAttribute("tip", "商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
			                    	//request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				//return;
			                    	flag=false;
			                    	break;
			                    }
							}
							if(!flag) continue;
						}
						
		                set = "status = " + hisStatus + ", confirm_datetime = now()";
	                    if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						if(hisStatus == StockExchangeProductBean.STOCKOUT_DEALED){
							log.setRemark("商品调配操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]出库");
						} else if(hisStatus == StockExchangeProductBean.STOCKOUT_UNDEAL) {
							log.setRemark("商品调配操作：" + bean.getName() + ": 取消商品[" + product.getCode() + "]的确认出库");
						}
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
						//修改调拨单状态为出库处理中
						if(!service.updateStockExchange("status="+StockExchangeBean.STATUS1, "id="+bean.getId())){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败");
							request.setAttribute("result", "failure");
							return;
						}
						service.getDbOp().commitTransaction();
		            }
            	}else if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5 || bean.getStatus() == StockExchangeBean.STATUS8){
            		if(!stockIn){
	            		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
		                request.setAttribute("result", "failure");
		                return;
	            	}
	            	StringBuilder buf = new StringBuilder();
		            String condition = null;
		            buf.append("stock_exchange_id = ");
		            buf.append(bean.getId());
		            buf.append(" and status = ");
		            buf.append(StockExchangeProductBean.STOCKIN_UNDEAL);
		            if (!stockProductIds.equals("")) {
		                buf.append(" and id in (");
		                buf.append(stockProductIds);
		                buf.append(") ");
		            }
		            condition = buf.toString();
		            if(hisStatus == -1){
	                	hisStatus = StockExchangeProductBean.STOCKIN_DEALED;
	                }else if(hisStatus == StockExchangeProductBean.STOCKOUT_DEALED || hisStatus == StockExchangeProductBean.STOCKOUT_UNDEAL){
	                	request.setAttribute("tip", "调拨单状态错误，操作失败");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
	                }
		            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
		            Iterator itr = sepList.iterator();
		            
		            StockExchangeProductBean sep = null;
		            voProduct product = null;
		            String set = null;
		            service.getDbOp().startTransaction();
		            while (itr.hasNext()) {
		            	//开始事务
		                sep = (StockExchangeProductBean) itr.next();
		                product = wareService.getProduct(sep.getProductId());
	                    set = "status = " + hisStatus + ", confirm_datetime = now()";
	                    if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品检测操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]入库");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
		            }
		            if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS8){
		            	service.updateStockExchange("status=" + StockExchangeBean.STATUS5, "id=" + bean.getId());
		            }
		            service.getDbOp().commitTransaction();
            	}   
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}finally {
	            service.releaseAll();
	        }
	        
		}
  }
    
    /**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-4-29
	 * 
	 * 说明：确认库存调配——确认出库、确认入库
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
    public void completeStockExchange(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();
    	boolean edit = false;
    	boolean stockIn = group.isFlag(83);
    	boolean stockOut = group.isFlag(82);

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
            int sepId = StringUtil.StringToId(request.getParameter("sepId"));//StockExchangeProductId
            int confirm = StringUtil.StringToId(request.getParameter("confirm"));
            int hisStatus = StringUtil.toInt(request.getParameter("hisStatus"));
	        String back = StringUtil.dealParam(request.getParameter("back"));
	        WareService wareService = new WareService();
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	request.setAttribute("tip", "该调拨单不存在或已删除!");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }

	            //当 调配单 的状态 不是 “未处理” 或者 “检测处理中” 的时候，不能进行确认操作
	            if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS5 && bean.getStatus() != StockExchangeBean.STATUS6 && bean.getStatus() != StockExchangeBean.STATUS8) {
	                request.setAttribute("tip", "该操作已经完成，不能再更改！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            if(service.getStockExchangeProductCount("stock_exchange_id=" + bean.getId()) <= 0){
	            	request.setAttribute("tip", "没有要调拨的商品，不能执行该操作！");
	                request.setAttribute("result", "failure");
	                return;
	            }

	            //开始事务
	            service.getDbOp().startTransaction();
	            //根据 调配单的不同状态，进行不同的处理
	            if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS2 || bean.getStatus() == StockExchangeBean.STATUS4){
	            	if(!stockOut){
	            		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
		                request.setAttribute("result", "failure");
		                return;
	            	}
	            	if(!edit){
		            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
		                request.setAttribute("result", "failure");
		                return;
		            }
	            	//如果当前状态为 未处理，则要将相关产品的 处理状态 改为 第一步处理完成
	            	StringBuilder buf = new StringBuilder();
		            String condition = null;
		            if(confirm == 1){
			            buf.append("stock_exchange_id = ");
			            buf.append(bean.getId());
			            buf.append(" and status = ");
			            buf.append(StockExchangeProductBean.STOCKOUT_UNDEAL);
			            if (sepId > 0) {
			                buf.append(" and id = ");
			                buf.append(sepId);
			            }
			            condition = buf.toString();
		            	int count = service.getStockExchangeProductCount(condition);
		            	if(count > 0){
		            		request.setAttribute("tip", "还有没确认的商品，不能完成出库操作！");
			                request.setAttribute("result", "failure");
			                return;
		            	}
		            	String set = "status = " + StockExchangeBean.STATUS2 + ", confirm_datetime = now(), stock_out_oper=" + user.getId()+", stock_out_oper_name='"+user.getUsername()+"'";
		            	if(!service.updateStockExchange(set, "id = " + bean.getId()))
		            	{
		            	  service.getDbOp().rollbackTransaction();
		            	  request.setAttribute("tip", "数据库操作失败");
		            	  request.setAttribute("result", "failure");
		            	  return;
		            	}
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成出库操作");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log))
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
						  return;
						}
		            } else {
			            buf.append("stock_exchange_id = ");
			            buf.append(bean.getId());
			            if (sepId > 0) {
			                buf.append(" and id = ");
			                buf.append(sepId);
			            }
			            condition = buf.toString();
		                if(hisStatus == -1){
		                	hisStatus = StockExchangeProductBean.STOCKOUT_DEALED;
		                }else if(hisStatus == StockExchangeProductBean.STOCKIN_DEALED || hisStatus == StockExchangeProductBean.STOCKIN_UNDEAL){
		                	request.setAttribute("tip", "调拨单状态错误，操作失败");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
		                }
			            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
			            Iterator itr = sepList.iterator();
			            StockExchangeProductBean sep = null;
			            voProduct product = null;
			            String set = null;
			            while (itr.hasNext()) {
			                sep = (StockExchangeProductBean) itr.next();
			                product = wareService.getProduct(sep.getProductId());
							if(sep.getReason() <= 0 || StringUtil.isNull(sep.getReasonText())){
								request.setAttribute("tip", product.getName() + "没有设置调拨原因！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}

							ProductStockBean psOut = null;
							ProductStockBean psIn = null;
							// 如果是 没处理的 记录，在确认出库的时候，就要把 库存数量锁定
							if (hisStatus == StockExchangeProductBean.STOCKOUT_DEALED && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
								product = wareService.getProduct(sep.getProductId());
								psIn = service.getProductStock("id=" + sep.getStockInId());
								psOut = service.getProductStock("id=" + sep.getStockOutId());
								
								if (sep.getStockOutCount() > psOut.getStock()) {
									request.setAttribute("tip", product.getName() + "的库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								set = "remark = '操作前库存"
										+ psOut.getStock() + ",操作后库存"
										+ (psOut.getStock() - sep.getStockOutCount())
										+ "', confirm_datetime = now()";
								if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}
								//service.updateProductStock("stock=(stock - " + sep.getStockOutCount() + "), lock_count=(lock_count + " + sep.getStockOutCount() + ")", "id=" + sep.getStockOutId());
								if(!service.updateProductStockCount(sep.getStockOutId(), -sep.getStockOutCount())){
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
				                    request.setAttribute("result", "failure");
				                    return;
								}
								if(!service.updateProductLockCount(sep.getStockOutId(), sep.getStockOutCount())){
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
				                    request.setAttribute("result", "failure");
				                    return;
								}
								//新添加的对于 源库是退货库的 特别处理 要扣除的是 退货商品列表中的不合格的数量
								/*if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED) {
									if(!service.updateUnqualifyReturnedProduct(sep.getProductId(), ReturnedProductBean.APPRAISAL_UNQUALIFY, sep.getStockOutCount())) {
										request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是退货库不合格商品数量不足");
					                    request.setAttribute("result", "failure");
					                    service.getDbOp().rollbackTransaction();
					                    return;
									}
								}*/

								//如果是从合格库调拨出去，则需要自动检测库存，如果合格库的库存为0，则隐藏商品
								if(psOut.getType()==ProductStockBean.STOCKTYPE_QUALIFIED && psIn.getType() != ProductStockBean.STOCKTYPE_QUALIFIED 
										&& (product.getParentId1() == 123 || product.getParentId1() == 143
										|| product.getParentId1() == 316 || product.getParentId1() == 317
										|| product.getParentId1() == 119 || product.getParentId1() == 340
										|| product.getParentId1() == 1385 || product.getParentId1() == 1425 
										|| product.getParentId1() == 544 || product.getParentId1() == 545
										|| product.getParentId1() == 458 || product.getParentId1() == 459 
										|| product.getParentId1() == 401 || product.getParentId1() == 136 
										|| product.getParentId2() == 203 || product.getParentId2() == 204 
										|| product.getParentId2() == 205 || product.getParentId2() == 699
										|| product.getParentId1() == 145 || product.getParentId1() == 151
										|| product.getParentId1() == 197 || product.getParentId1() == 505
										|| product.getParentId1() == 163 || product.getParentId1() == 690
										|| product.getParentId1() == 908|| product.getParentId1() == 752
										|| product.getParentId1() == 803
										|| product.getParentId1() == 183 || product.getParentId1() == 184
										|| product.getParentId1() == 1093 || product.getParentId1() == 1094
										|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222)){
									service.checkProductStatus(sep.getProductId());
								}

								// log记录
								StockAdminHistoryBean log = new StockAdminHistoryBean();
								log.setAdminId(user.getId());
								log.setAdminName(user.getUsername());
								log.setLogId(bean.getId());
								log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
								log.setOperDatetime(DateUtil.getNow());
								log.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]出库");
								log.setType(StockAdminHistoryBean.CHANGE);
								if(!stockService.addStockAdminHistory(log))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  return;
								}
								
								//锁定货位库存
								//出库
								List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
								for(int i=0;i<sepcOutList.size();i++){
									StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
									if(!cargoService.updateCargoProductStockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
				                    	request.setAttribute("tip", "商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
				                    	request.setAttribute("result", "failure");
				                    	service.getDbOp().rollbackTransaction();
				        				return;
				                    }
									if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
				                    	request.setAttribute("tip", "商品[" + product.getCode() + "],货位库存操作失败，货位库存不足！");
				                    	request.setAttribute("result", "failure");
				                    	service.getDbOp().rollbackTransaction();
				        				return;
				                    }
								}
							}

			                set = "status = " + hisStatus + ", confirm_datetime = now()";
		                    if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      request.setAttribute("tip", "数据库操作失败");
		                      request.setAttribute("result", "failure");
		                      return;
		                    }
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							if(hisStatus == StockExchangeProductBean.STOCKOUT_DEALED){
								log.setRemark("商品调配操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]出库");
							} else if(hisStatus == StockExchangeProductBean.STOCKOUT_UNDEAL) {
								log.setRemark("商品调配操作：" + bean.getName() + ": 取消商品[" + product.getCode() + "]的确认出库");
							}
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
			            }
			            if(bean.getStatus() == StockExchangeBean.STATUS4){
			            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
			            	{
			            	  service.getDbOp().rollbackTransaction();
			            	  request.setAttribute("tip", "数据库操作失败");
			            	  request.setAttribute("result", "failure");
			            	  return;
			            	}
			            }
		            }
	            } else if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5 || bean.getStatus() == StockExchangeBean.STATUS8){
	            	StringBuilder buf = new StringBuilder();
		            String condition = null;
		            buf.append("stock_exchange_id = ");
		            buf.append(bean.getId());
		            buf.append(" and status = ");
		            buf.append(StockExchangeProductBean.STOCKIN_UNDEAL);
		            if (sepId > 0) {
		                buf.append(" and id = ");
		                buf.append(sepId);
		            }
		            condition = buf.toString();
		            if(confirm == 1){
		            	int count = service.getStockExchangeProductCount(condition);
		            	if(count > 0){
		            		request.setAttribute("tip", "还有没确认的商品，不能完成入库操作！");
			                request.setAttribute("result", "failure");
			                return;
		            	}
		            	String set = "status = " + StockExchangeBean.STATUS6 + ", confirm_datetime = now(), stock_in_oper=" + user.getId()+", stock_in_oper_name='"+user.getUsername()+"'";
	                    if(!service.updateStockExchange(set, "id = " + bean.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成入库操作");
						log.setType(StockAdminHistoryBean.CHANGE);
						 if(!stockService.addStockAdminHistory(log))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      request.setAttribute("tip", "数据库操作失败");
		                      request.setAttribute("result", "failure");
		                      return;
		                    }
		            } else {
		                if(hisStatus == -1){
		                	hisStatus = StockExchangeProductBean.STOCKIN_DEALED;
		                }else if(hisStatus == StockExchangeProductBean.STOCKOUT_DEALED || hisStatus == StockExchangeProductBean.STOCKOUT_UNDEAL){
		                	request.setAttribute("tip", "调拨单状态错误，操作失败");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
		                }
			            ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
			            Iterator itr = sepList.iterator();
			            //开始事务
			            service.getDbOp().startTransaction();
			            StockExchangeProductBean sep = null;
			            voProduct product = null;
			            String set = null;
			            while (itr.hasNext()) {
			                sep = (StockExchangeProductBean) itr.next();
			                product = wareService.getProduct(sep.getProductId());
		                    set = "status = " + hisStatus + ", confirm_datetime = now()";
		                    if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      request.setAttribute("tip", "数据库操作失败");
		                      request.setAttribute("result", "failure");
		                      return;
		                    }
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品检测操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]入库");
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      request.setAttribute("tip", "数据库操作失败");
		                      request.setAttribute("result", "failure");
		                      return;
		                    }
							
			            }
			            if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS8){
			            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS5, "id=" + bean.getId()))
		                    {
		                      service.getDbOp().rollbackTransaction();
		                      request.setAttribute("tip", "数据库操作失败");
		                      request.setAttribute("result", "failure");
		                      return;
		                    }
			            }
		            }
	            }

	            //提交事务
	            service.getDbOp().commitTransaction();
	            request.setAttribute("back", back);
	        }catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：审核库存调配单——审核出库、审核入库
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void auditingStockExchange(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();
    	boolean auditingIn = group.isFlag(81);
    	boolean auditingOut = group.isFlag(80);
  
    	String audintType = StringUtil.convertNull(request.getParameter("audintType"));
    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
            int mark = StringUtil.StringToId(request.getParameter("mark"));
            WareService wareService = new WareService();
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
	            
	            //开始事务
	            service.getDbOp().startTransaction();
	            
	            //同步控制， 入库审核时 一个页面审核失败。 另一个页面审核通过 应该提示。 审核不通过调拨单的状态会变回出库审核
	            if(bean.getStatus() == StockExchangeBean.STATUS2 && audintType.equals("audintingIn")){
	            	request.setAttribute("tip", bean.getName() + "  不是入库审核状态，不能审核！");
					request.setAttribute("result", "failure");
					return;
	            }
	            
				if (bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS6) {
					request.setAttribute("tip", bean.getName() + "  不是待审核状态，不能审核！");
					request.setAttribute("result", "failure");
					return;
				}
				if (bean.getStatus() == StockExchangeBean.STATUS2) {
					if(!auditingOut){
	            		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
		                request.setAttribute("result", "failure");
		                return;
	            	}
					String condition = "stock_exchange_id = " + bean.getId() + " and status = " + StockExchangeProductBean.STOCKOUT_DEALED;
					if (mark == 1) {
						// 审核通过，要出库
						if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS3 + ", auditing_user_id=" + user.getId()+",auditing_user_name='"+user.getUsername()+"'", "id=" + bean.getId())){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						if(!service.updateStockExchangeProduct("status=" + StockExchangeProductBean.STOCKIN_UNDEAL, "stock_exchange_id=" + bean.getId())){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + "：出库审核通过");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						
					} else {
						// 审核没通过，要把状态转为处理中审核未通过
						// 同时 把已经出库，并锁定库存的 商品 还原回去
						ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
						Iterator itr = sepList.iterator();
						StockExchangeProductBean sep = null;
						voProduct product = null;
						ProductStockBean psOut = null;
						String set = null;
						while (itr.hasNext()) {
							sep = (StockExchangeProductBean) itr.next();
							product = wareService.getProduct(sep.getProductId());
							psOut = service.getProductStock("id=" + sep.getStockOutId());
							
							if (sep.getStockOutCount() > psOut.getLockCount()) {
								request.setAttribute("tip", product.getCode() + "的锁定库存不足，无法从锁定库存还原到正常库存！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								service.releaseAll();
								wareService.releaseAll();
								return;
							}
							set = "status = "
									+ StockExchangeProductBean.STOCKOUT_UNDEAL
									+ ", remark = '操作前库存"
									+ psOut.getStock() + ",操作后库存"
									+ (psOut.getStock() + sep.getStockOutCount())
									+ "', confirm_datetime = now()";
							if(!service.updateStockExchangeProduct(set, "id = " + sep.getId())){ //!!!!!!!!!!!
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
				                request.setAttribute("result", "failure");
				                return;
							}
							//service.updateProductStock("stock=(stock + " + sep.getStockOutCount() + "), lock_count=(lock_count - " + sep.getStockOutCount() + ")", "id=" + sep.getStockOutId());
							if(!service.updateProductStockCount(sep.getStockOutId(), sep.getStockOutCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			                    return;
							}
							if(!service.updateProductLockCount(sep.getStockOutId(), -sep.getStockOutCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			                    return;
							}
							//新添加的对于 源库是退货库的 特别处理 要扣除的是 退货商品列表中的不合格的数量-------------把减去的不合格量加回去
							/*if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED) {
								if(!service.updateUnqualifyReturnedProductBack(sep.getProductId(), ReturnedProductBean.APPRAISAL_UNQUALIFY, sep.getStockOutCount())) {
									request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是退货库不合格商品数量未能加回到商品列表中");
				                    request.setAttribute("result", "failure");
				                    service.getDbOp().rollbackTransaction();
				                    return;
								}
							}*/
							
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]从出库锁定还原到正常库存");
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log)){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
				                request.setAttribute("result", "failure");
				                return;
							}
							
							//还原出库锁定货位库存
							List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
							for(int i=0;i<sepcOutList.size();i++){
								StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
								if(!cargoService.updateCargoProductStockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
			                    	request.setAttribute("tip", "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！");
			                    	request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				return;
			                    }
								if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
			                    	request.setAttribute("tip", "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！");
			                    	request.setAttribute("result", "failure");
			                    	service.getDbOp().rollbackTransaction();
			        				return;
			                    }
							}
							
						}
						if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS4 + ", auditing_user_id=" + user.getId(), "id=" + bean.getId())){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						//service.updateStockExchangeProduct("status=" + StockExchangeProductBean.STOCKOUT_UNDEAL, condition);

						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + "：出库审核未通过");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log)){
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
					}
				} else if (bean.getStatus() == StockExchangeBean.STATUS6) {
					String condition = "stock_exchange_id = " + bean.getId()
							+ " and status = "
							+ StockExchangeProductBean.STOCKIN_DEALED;
					if (mark == 1) {
						
						//售后库调拨单不能使用该功能审核通过
						AfterSaleStockExchangeProduct asep=afStockService.getAfterSaleStockExchangeProduct("stock_exchange_id="+bean.getId());
						if(asep!=null){
							String err="售后调拨使用入口错误,id="+bean.getId();
							System.out.println(err);
							request.setAttribute("tip", err);
							request.setAttribute("result", "failure");
							return;
						}
						
						// 审核通过，要入库
						ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
						Iterator itr = sepList.iterator();
						StockExchangeProductBean sep = null;
						voProduct product = null;
						ProductStockBean psOut = null;
						ProductStockBean psIn = null;
						String set = null;
						while (itr.hasNext()) {
							sep = (StockExchangeProductBean) itr.next();
							product = wareService.getProduct(sep.getProductId());
							psIn = service.getProductStock("id=" + sep.getStockInId());
							psOut = service.getProductStock("id=" + sep.getStockOutId());
							if(!CargoDeptAreaService. hasCargoDeptArea(request, psIn.getArea(), psIn.getType())){
								request.setAttribute("tip", "用户只能审核自己所属库地区和库类型’的调拨单");
								request.setAttribute("result", "failure");
								return;
							}
							// 入库
							set = "status = " + StockExchangeProductBean.STOCKIN_DEALED
									+ ", remark = '操作前库存"
									+ psIn.getStock() + ",操作后库存"
									+ (psIn.getStock() + sep.getStockInCount())
									+ "', confirm_datetime = now()";
							if(!service.updateStockExchangeProduct(set, "id = " + sep.getId())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
				                request.setAttribute("result", "failure");
				                return;
							}
							//service.updateProductStock("lock_count=(lock_count - " + sep.getStockOutCount() + ")", "id=" + sep.getStockOutId());
							if(!service.updateProductLockCount(sep.getStockOutId(), -sep.getStockOutCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			                    return;
							}
							
							//service.updateProductStock("stock=(stock + " + sep.getStockInCount() + ")", "id=" + sep.getStockInId());
							if(!service.updateProductStockCount(sep.getStockInId(), sep.getStockInCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			                    return;
							}
							
							
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品调配操作：" + bean.getName() + ": 将商品[" + product.getCode() + "]入库");
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log)){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
				                request.setAttribute("result", "failure");
				                return;
							}

							
							//更新调拨单未上架数量
							if(!service.updateStockExchangeProduct("no_up_cargo_count = "+sep.getStockOutCount(), "id = "+sep.getId())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
				                request.setAttribute("result", "failure");
				                return;
							}

							BaseProductInfo baseProductInfo = new BaseProductInfo();
							baseProductInfo.setId(sep.getProductId());
							//出库
							baseProductInfo.setProductStockOutId(psOut.getId());
							//入库
							baseProductInfo.setProductStockId(psIn.getId());
							baseProductInfo.setOutCount(sep.getStockOutCount());
							baseList.add(baseProductInfo);
							
							//操作在fpa jar中进行
//							sbList = stockService.getStockBatchList(
//									"product_id="+psOut.getProductId()
//									+" and stock_type="+psOut.getType()
//									+" and stock_area="+psOut.getArea(), -1, -1, "id asc");
//							
//							double stockinPrice = 0;
//							double stockoutPrice = 0;
//							if(sbList!=null&&sbList.size()!=0){
//								int stockExchangeCount = sep.getStockOutCount();
//								int index = 0;
//								int stockBatchCount = 0;
//								
//								do {
//									//出库
//									StockBatchBean batch = (StockBatchBean)sbList.get(index);
//									int ticket = FinanceSellProductBean.queryTicket(service.getDbOp(), batch.getCode());	//是否含票 
//									int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), ticket);
//									if(stockExchangeCount>=batch.getBatchCount()){
//										if(!stockService.deleteStockBatch("id="+batch.getId())){
//											service.getDbOp().rollbackTransaction();
//											request.setAttribute("tip", "数据库操作失败！");
//							                request.setAttribute("result", "failure");
//							                return;
//										}
//										stockBatchCount = batch.getBatchCount();
//									}else{
//										if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//											service.getDbOp().rollbackTransaction();
//											request.setAttribute("tip", "数据库操作失败！");
//							                request.setAttribute("result", "failure");
//							                return;
//										}
//										stockBatchCount = stockExchangeCount;
//									}
//									
//									//添加批次操作记录
//									StockBatchLogBean batchLog = new StockBatchLogBean();
//									batchLog.setCode(bean.getCode());
//									batchLog.setStockType(batch.getStockType());
//									batchLog.setStockArea(batch.getStockArea());
//									batchLog.setBatchCode(batch.getCode());
//									batchLog.setBatchCount(stockBatchCount);
//									batchLog.setBatchPrice(batch.getPrice());
//									batchLog.setProductId(batch.getProductId());
//									batchLog.setRemark("调拨出库");
//									batchLog.setCreateDatetime(DateUtil.getNow());
//									batchLog.setUserId(user.getId());
//									if(!stockService.addStockBatchLog(batchLog)){
//										 request.setAttribute("tip", "添加失败！");
//							             request.setAttribute("result", "failure");
//							             service.getDbOp().rollbackTransaction();
//							             return;
//									}
//									
//									//财务进销存卡片---liuruilan-----
//									FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//									product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
//									int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), psOut.getArea(), psOut.getType(), ticket, batch.getProductId());
//									int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, psOut.getType(), ticket, batch.getProductId());
//									int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), psOut.getArea(), -1,ticket, batch.getProductId());
//									FinanceStockCardBean fsc = new FinanceStockCardBean();
//									fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//									fsc.setCode(bean.getCode());
//									fsc.setCreateDatetime(DateUtil.getNow());
//									fsc.setStockType(psOut.getType());
//									fsc.setStockArea(psOut.getArea());
//									fsc.setProductId(batch.getProductId());
//									fsc.setStockId(sep.getStockInId());
//									fsc.setStockInCount(stockBatchCount);
//									fsc.setStockAllArea(stockAllArea);
//									fsc.setStockAllType(stockAllType);
//									fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//									fsc.setStockPrice(product.getPrice5());
//									
//									fsc.setCurrentStock(currentStock);
//									fsc.setType(fsc.getCardType());
//									fsc.setIsTicket(ticket);
//									fsc.setStockBatchCode(batch.getCode());
//									fsc.setBalanceModeStockCount(_count - stockBatchCount);
//									if(ticket == 0){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//									}
//									if(ticket == 1){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//									}
//									double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//									fsc.setAllStockPriceSum(tmpPrice);
//									
//									if(!frfService.addFinanceStockCardBean(fsc))
//									{
//  										service.getDbOp().rollbackTransaction();
//  										request.setAttribute("tip", "数据库操作失败");
//  										request.setAttribute("result", "failure");
//  										return;
//									}
//									
////---------------liuruilan-----------
//									
//									stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//									
//									//入库
//									StockBatchBean batchBean = null;
//									//如果从质检库调拨到返厂库，那么获取批次编号为原批次号+"X"
//									if(psIn.getType()==ProductStockBean.STOCKTYPE_BACK 
//											&& psOut.getType()==ProductStockBean.STOCKTYPE_CHECK){
//										batchBean = stockService.getStockBatch("code='"+batch.getCode()+"X"
//												+"' and product_id="+batch.getProductId()+" and stock_type="
//												+psIn.getType()+" and stock_area="+psIn.getArea());
//									} else if ( psOut.getType() == ProductStockBean.STOCKTYPE_BACK && psIn.getType() != ProductStockBean.STOCKTYPE_BACK ) {
//										String code = this.removeX(batch.getCode());
//										batchBean = stockService.getStockBatch("code='"+code
//												+"' and product_id="+batch.getProductId()+" and stock_type="
//												+psIn.getType()+" and stock_area="+psIn.getArea());
//										//testtest
//									}else{
//										batchBean = stockService.getStockBatch("code='"+batch.getCode()
//												+"' and product_id="+batch.getProductId()+" and stock_type="
//												+psIn.getType()+" and stock_area="+psIn.getArea());
//									}
//									
//									if(batchBean!=null){
//										if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//											service.getDbOp().rollbackTransaction();
//											request.setAttribute("tip", "数据库操作失败！");
//							                request.setAttribute("result", "failure");
//							                return;
//										}
//									}else{
//										int _ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
//										//从待验库调拨入返厂库，新生成的批次号为出库批次+"X"
//										if(psIn.getType()==ProductStockBean.STOCKTYPE_BACK 
//												&& psOut.getType()==ProductStockBean.STOCKTYPE_CHECK){
//											StockBatchBean newBatch = new StockBatchBean();
//											newBatch.setCode(batch.getCode()+"X");
//											newBatch.setProductId(batch.getProductId());
//											newBatch.setPrice(batch.getPrice());
//											newBatch.setBatchCount(stockBatchCount);
//											newBatch.setProductStockId(psIn.getId());
//											newBatch.setStockArea(bean.getStockInArea());
//											newBatch.setStockType(psIn.getType());
//											newBatch.setTicket(_ticket);
//											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//											if(!stockService.addStockBatch(newBatch)){
//												request.setAttribute("tip", "添加失败！");
//												request.setAttribute("result", "failure");
//												service.getDbOp().rollbackTransaction();
//												return;
//											}
//										} else if (  psOut.getType() == ProductStockBean.STOCKTYPE_BACK && psIn.getType() != ProductStockBean.STOCKTYPE_BACK) {
//											StockBatchBean newBatch = new StockBatchBean();
//											String code = this.removeX(batch.getCode());
//											newBatch.setCode(code);
//											newBatch.setProductId(batch.getProductId());
//											newBatch.setPrice(batch.getPrice());
//											newBatch.setBatchCount(stockBatchCount);
//											newBatch.setProductStockId(psIn.getId());
//											newBatch.setStockArea(bean.getStockInArea());
//											newBatch.setStockType(psIn.getType());
//											newBatch.setTicket(_ticket);
//											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//											if(!stockService.addStockBatch(newBatch)){
//												request.setAttribute("tip", "添加失败！");
//												request.setAttribute("result", "failure");
//												service.getDbOp().rollbackTransaction();
//												return;
//											}
//										}else{
//											StockBatchBean newBatch = new StockBatchBean();
//											newBatch.setCode(batch.getCode());
//											newBatch.setProductId(batch.getProductId());
//											newBatch.setPrice(batch.getPrice());
//											newBatch.setBatchCount(stockBatchCount);
//											newBatch.setProductStockId(psIn.getId());
//											newBatch.setStockArea(bean.getStockInArea());
//											newBatch.setStockType(psIn.getType());
//											newBatch.setTicket(_ticket);
//											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//											if(!stockService.addStockBatch(newBatch)){
//												request.setAttribute("tip", "添加失败！");
//												request.setAttribute("result", "failure");
//												service.getDbOp().rollbackTransaction();
//												return;
//											}
//										}
//									}
//									
//									//添加批次操作记录
//									batchLog = new StockBatchLogBean();
//									batchLog.setCode(bean.getCode());
//									batchLog.setStockType(psIn.getType());
//									batchLog.setStockArea(bean.getStockInArea());
//									batchLog.setBatchCode(batch.getCode());
//									batchLog.setBatchCount(stockBatchCount);
//									batchLog.setBatchPrice(batch.getPrice());
//									batchLog.setProductId(batch.getProductId());
//									batchLog.setRemark("调拨入库");
//									batchLog.setCreateDatetime(DateUtil.getNow());
//									batchLog.setUserId(user.getId());
//									if(!stockService.addStockBatchLog(batchLog)){
//										request.setAttribute("tip", "添加失败！");
//										request.setAttribute("result", "failure");
//										service.getDbOp().rollbackTransaction();
//										return;
//									}
//									
//									//财务进销存卡片---liuruilan-----2012-11-02-----
//									int stockinCount = stockBatchCount;
//									product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
//			    					currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, batch.getProductId());
//			    					 stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1,batchLog.getStockType(), ticket, batch.getProductId());
//									 stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(),batchLog.getStockArea(), -1,ticket, batch.getProductId());
//			    					fsc = new FinanceStockCardBean();
//			    					fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//			    					fsc.setCode(bean.getCode());
//			    					fsc.setCreateDatetime(DateUtil.getNow());
//			    					fsc.setStockType(batchLog.getStockType());
//			    					fsc.setStockArea(batchLog.getStockArea());
//			    					fsc.setProductId(batch.getProductId());
//			    					fsc.setStockId(sep.getStockInId());
//			    					fsc.setStockInCount(stockinCount);	
//			    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//			    					fsc.setStockAllArea(stockAllArea);
//			    					fsc.setStockAllType(stockAllType);
//			    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//			    					fsc.setStockPrice(product.getPrice5());
//			    					
//			    					fsc.setType(fsc.getCardType());
//			    					fsc.setIsTicket(ticket);
//			    					fsc.setStockBatchCode(batchLog.getBatchCode());
//			    					fsc.setBalanceModeStockCount(_count - stockBatchCount + stockinCount);
//			    					if(ticket == 0){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockinCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//									}
//									if(ticket == 1){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockinCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//									}
//			    					tmpPrice = Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket())));
//			    					fsc.setAllStockPriceSum(tmpPrice);
//			    					if(!frfService.addFinanceStockCardBean(fsc))
//			    					{
//			    					  service.getDbOp().rollbackTransaction();
//			    					  request.setAttribute("tip", "数据库操作失败");
//			    					  request.setAttribute("result", "failure");
//			    					  return;
//			    					}
//			    					//-----------liuruilan-------------
//									
//									
//									stockExchangeCount -= batch.getBatchCount();
//									index++;
//									
//									stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//								} while (stockExchangeCount>0&&index<sbList.size());
//							}
							
							//处理进销存卡片
							product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
							//添加进销存卡片
							// 出库卡片
							StockCardBean sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
							sc.setCode(bean.getCode());
							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getStockOutType());
							sc.setStockArea(bean.getStockOutArea());
							sc.setProductId(sep.getProductId());
							sc.setStockId(sep.getStockOutId());
							sc.setStockOutCount(sep.getStockOutCount());
//							sc.setStockOutPriceSum(stockoutPrice);
							sc.setStockOutPriceSum((new BigDecimal(sep.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							sc.setCurrentStock(product.getStock(bean.getStockOutArea(), bean.getStockOutType()) + product.getLockCount(bean.getStockOutArea(), bean.getStockOutType()));
							sc.setStockAllArea(product.getStock(bean.getStockOutArea()) + product.getLockCount(bean.getStockOutArea()));
							sc.setStockAllType(product.getStockAllType(bean.getStockOutType()) + product.getLockCountAllType(bean.getStockOutType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!service.addStockCard(sc)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							
							// 入库卡片
							sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
							sc.setCode(bean.getCode());
							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getStockInType());
							sc.setStockArea(bean.getStockInArea());
							sc.setProductId(sep.getProductId());
							sc.setStockId(sep.getStockInId());
							sc.setStockInCount(sep.getStockInCount());
//							sc.setStockInPriceSum(stockinPrice);
							sc.setStockInPriceSum((new BigDecimal(sep.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							sc.setCurrentStock(product.getStock(bean.getStockInArea(), bean.getStockInType()) + product.getLockCount(bean.getStockInArea(), bean.getStockInType()));
							sc.setStockAllArea(product.getStock(bean.getStockInArea()) + product.getLockCount(bean.getStockInArea()));
							sc.setStockAllType(product.getStockAllType(bean.getStockInType()) + product.getLockCountAllType(bean.getStockInType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!service.addStockCard(sc)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return;
							}
							
							//处理货位库存
							product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
							
							//源货位
							List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
							for(int i=0;i<sepcOutList.size();i++){
								StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
								if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
									request.setAttribute("tip", "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
								
								CargoProductStockBean cpsOut = cargoService.getCargoAndProductStock("cps.id = "+sepcOut.getCargoProductStockId());
								
								//货位出库卡片
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
								csc.setCode(bean.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(bean.getStockOutType());
								csc.setStockArea(bean.getStockOutArea());
								csc.setProductId(sep.getProductId());
								csc.setStockId(cpsOut.getId());
								csc.setStockOutCount(sep.getStockOutCount());
								csc.setStockOutPriceSum((new BigDecimal(sep.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(cpsOut.getStockCount()+cpsOut.getStockLockCount());
								csc.setCargoStoreType(cpsOut.getCargoInfo().getStoreType());
								csc.setCargoWholeCode(cpsOut.getCargoInfo().getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
								if(!cargoService.addCargoStockCard(csc)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							
							//目的货位
							List sepcInList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 1", -1, -1, "id asc");
							for(int i=0;i<sepcInList.size();i++){
								StockExchangeProductCargoBean sepcIn = (StockExchangeProductCargoBean)sepcInList.get(i);
								if(!cargoService.updateCargoProductStockCount(sepcIn.getCargoProductStockId(), sepcIn.getStockCount())){
									request.setAttribute("tip", "商品"+product.getCode()+"货位库存操作失败，货位锁定库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}

								// 审核通过，就加 进销存卡片
								CargoProductStockBean cpsIn = cargoService.getCargoAndProductStock("cps.id = "+sepcIn.getCargoProductStockId());
								
								//货位入库卡片
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
								csc.setCode(bean.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(bean.getStockInType());
								csc.setStockArea(bean.getStockInArea());
								csc.setProductId(sep.getProductId());
								csc.setStockId(cpsIn.getId());
								csc.setStockInCount(sep.getStockInCount());
								csc.setStockInPriceSum((new BigDecimal(sep.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(cpsIn.getStockCount()+cpsIn.getStockLockCount());
								csc.setCargoStoreType(cpsIn.getCargoInfo().getStoreType());
								csc.setCargoWholeCode(cpsIn.getCargoInfo().getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
								if(!cargoService.addCargoStockCard(csc)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return;
								}
							}
							
						}
						if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS7 + ", auditing_user_id2=" + user.getId()+", auditing_user_name2='"+user.getUsername()+"'", "id=" + bean.getId())){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						if(bean.getStockInType() != 0){
							if(!service.updateStockExchange("up_shelf_status = 2", "id=" + bean.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
						}
						
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + "：入库审核通过");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						//目的库是返厂库，则需将imei码的状态变为返厂中
						if (bean.getStockInType() == ProductStockBean.STOCKTYPE_BACK) {
							
							List<IMEIStockExchangeBean> imeiStockExchangeList = imeiService.getIMEIStockExchangeList("stock_exchange_id="+bean.getId(), -1, -1, null);
							if (imeiStockExchangeList != null && imeiStockExchangeList.size() != 0) {
								
								IMEILogBean imeiLog = null;
								for (IMEIStockExchangeBean imeiStockExchange : imeiStockExchangeList) {
									IMEIBean imei = imeiService.getIMEI("code='"+imeiStockExchange.getIMEI()+"'");
									if (imei == null) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "IMEI码不存在！");
						                request.setAttribute("result", "failure");
						                return;
									}
									if (imei.status != IMEIBean.IMEISTATUS2) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "存在不是【可出库】状态的IMEI码商品！");
						                request.setAttribute("result", "failure");
						                return;
									}
									imeiLog = new IMEILogBean();
									imeiLog.setIMEI(imei.getCode());
									imeiLog.setOperCode(bean.getCode());
									imeiLog.setOperType(IMEILogBean.OPERTYPE3);
									imeiLog.setCreateDatetime(DateUtil.getNow());
									imeiLog.setUserId(user.getId());
									imeiLog.setUserName(user.getUsername());
									imeiLog.setContent("调拨，入库审核通过，IMEI码:"+imei.getCode()+"状态由【可出库】变为【返厂中】,"+ProductStockBean.areaMap.get(bean.getStockOutArea())+"到"+ProductStockBean.areaMap.get(bean.getStockInArea()));
									if (!imeiService.addIMEILog(imeiLog)) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "记录IMEI码操作日志失败！");
										request.setAttribute("result", "failure");
										return;
									}
									if (!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS4, "id=" + imei.getId())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "数据库操作失败！");
										request.setAttribute("result", "failure");
										return;
									}
								}
							}
						}
					} else {
						// 审核没通过，要把状态转为处理中审核未通过
						//需求变更：审核没通过的要吧状态转化为 出库审核中
						if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS2 + ", auditing_user_id2=" + user.getId(), "id=" + bean.getId())){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						if(!service.updateStockExchangeProduct("status=" + StockExchangeProductBean.STOCKOUT_DEALED, condition)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + "：入库审核未通过");
						log.setType(StockAdminHistoryBean.CHANGE);
						if(!stockService.addStockAdminHistory(log)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
			                request.setAttribute("result", "failure");
			                return;
						}
						
					}
				}
				
				if(baseList != null && baseList.size() > 0){
					FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, bean.getCode(), user.getId(), 0, 0);
				}
				
				
	
				// 提交事务
				service.getDbOp().commitTransaction();
	        } catch (Exception e) {
	        	request.setAttribute("tip", "数据库操作失败！");
	        	e.printStackTrace();
	        	service.getDbOp().rollbackTransaction();
	        	stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-4-29
	 * 
	 * 说明：删除商品调配单
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
    public void deleteStockExchange(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();
    	boolean edit = false;

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            if( bean == null ) {
	            	request.setAttribute("tip", "没有找到要操作的调拨单！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            if(bean.getCreateUserId() == user.getId()||group.isFlag(0)){
	            	edit = true;
	            }
            	if(!edit){
	            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            if (bean.getStatus() != StockExchangeBean.STATUS0 && bean.getStatus() != StockExchangeBean.STATUS1) {
	                request.setAttribute("tip", "该操作已经完成，不能再修改！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            String condition = "stock_exchange_id = " + bean.getId() + " and status <> " + StockExchangeProductBean.STOCKOUT_UNDEAL;
	            if (service.getStockExchangeProductCount(condition) > 0) {
	                request.setAttribute("tip", "该操作已经有部分出入库了，不能删除！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            service.getDbOp().startTransaction();
	            service.deleteStockExchange("id = " + exchangeId);
	            
	            service.deleteStockExchangeProduct("stock_exchange_id = " + exchangeId);
	            
	            service.deleteStockExchangeProductCargo("stock_exchange_id = " + exchangeId);
	            
	            imeiService.deleteIMEIStockExchange("stock_exchange_id = " + exchangeId);
	            
	            
	            //log记录
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setAdminId(user.getId());
	            log.setAdminName(user.getUsername());
	            log.setLogId(bean.getId());
	            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
	            log.setOperDatetime(DateUtil.getNow());
	            log.setRemark("删除商品调配操作：" + bean.getName());
	            log.setType(StockAdminHistoryBean.DELETE);
	            if(!stockService.addStockAdminHistory(log))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return;
	            }
	            if(!stockService.updateStockAdminHistory("deleted = 1", "log_type = " + StockAdminHistoryBean.STOCK_EXCHANGE + " and log_id = " + bean.getId()))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return;
	            }
	            service.getDbOp().commitTransaction();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	service.getDbOp().rollbackTransaction();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-29
     * 
     * 说明：删除商品调配单中的商品
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void deleteStockExchangeItem(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	boolean edit = false;

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        int sepId = StringUtil.StringToId(request.getParameter("sepId"));
	        WareService wareService = new WareService();
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            if ( bean == null) {
		            request.setAttribute("tip", "调拨单不存在！");
		            request.setAttribute("result", "failure");
		            return;
	            }
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }
            	if(!edit){
	            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            if (bean.getStatus() == StockExchangeBean.STATUS2) {
	                request.setAttribute("tip", "该操作已经完成，不能再修改！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            StockExchangeProductBean sep = service.getStockExchangeProduct("id=" + sepId);
	            if ( sep == null) {
		            request.setAttribute("tip", "调拨单商品不存在！");
		            request.setAttribute("result", "failure");
		            return;
	            }
	            String condition = "stock_exchange_id = " + bean.getId() + " and id = " + sepId + " and status <> " + StockExchangeProductBean.STOCKOUT_UNDEAL;
	            if (service.getStockExchangeProductCount(condition) > 0) {
	                request.setAttribute("tip", "该产品已经有出入库了，不能删除！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
		        voProduct product = wareService.getProduct(sep.getProductId());
		        if (product == null) {
		        	request.setAttribute("tip", "该产品不存在！");
	                request.setAttribute("result", "failure");
	                return;
		        }

		        service.getDbOp().startTransaction();
		        //有的产品没有imei码，不做是否删除成功的判断
	        	imeiService.deleteIMEIStockExchange("stock_exchange_id = " + exchangeId + " and product_id = " + product.getId());
	        	if(!service.deleteStockExchangeProduct("stock_exchange_id = " + exchangeId + " and id = " + sepId))
	        	{
	        		service.getDbOp().rollbackTransaction();
	        		request.setAttribute("tip", "数据库操作失败");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	        	if(! service.deleteStockExchangeProductCargo("stock_exchange_id = " + exchangeId + " and stock_exchange_product_id = " + sepId))
	        	{
	        		service.getDbOp().rollbackTransaction();
	        		request.setAttribute("tip", "数据库操作失败");
	        		request.setAttribute("result", "failure");
	        		return;
	        	}
	            // 如果这个 库存调配单的状态时 未处理/出库审核未通过，改为出库处理中
	            if(bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS4){
	            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
	            	{
	            	  service.getDbOp().rollbackTransaction();
	            	  request.setAttribute("tip", "数据库操作失败");
	            	  request.setAttribute("result", "failure");
	            	  return;
	            	}
	            }

	            //log记录
	            StockAdminHistoryBean log = new StockAdminHistoryBean();
	            log.setAdminId(user.getId());
	            log.setAdminName(user.getUsername());
	            log.setLogId(bean.getId());
	            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
	            log.setOperDatetime(DateUtil.getNow());
            	log.setRemark("修改商品调配操作：" + bean.getName() + ",删除了商品[" + product.getCode() + "]");
	            log.setType(StockAdminHistoryBean.CHANGE);
	            if(! stockService.addStockAdminHistory(log))
	            {
	              service.getDbOp().rollbackTransaction();
	              request.setAttribute("tip", "数据库操作失败");
	              request.setAttribute("result", "failure");
	              return;
	            }
	            service.getDbOp().commitTransaction();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	service.getDbOp().rollbackTransaction();
			} finally {
	            service.releaseAll();
	        }
    	}
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-4-30
     * 
     * 说明：商品库存管理
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void stockAdminHistory(HttpServletRequest request,
            HttpServletResponse response) {
        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
        String logType = StringUtil.dealParam(request.getParameter("logType"));
        if(logType == null || logType.length() == 0){
        	logType = String.valueOf(StockAdminHistoryBean.STOCK_EXCHANGE);
        }
        DbOperation dbOp = new DbOperation();
        dbOp.init(DbOperation.DB_SLAVE);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        try {
            String condition = "id = " + exchangeId;
            StockExchangeBean bean = service.getStockExchange(condition);
            condition = "log_id = " + exchangeId + " and log_type in (" + logType + ") ";
            List list = stockService.getStockAdminHistoryList(condition, 0, -1, "id");

            request.setAttribute("list", list);
            request.setAttribute("bean", bean);
        } finally {
            service.releaseAll();
        }
    }


    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-7-16
     * 
     * 说明：进销存卡片列表
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     * @throws UnsupportedEncodingException 
     */
    public void stockCardList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
    	int countPerPage = 30;

        int stockType = StringUtil.toInt(request.getParameter("stockType"));
        int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
        int stockCardType = StringUtil.toInt(request.getParameter("stockCardType"));
        String code = StringUtil.dealParam(request.getParameter("code"));
        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
        String productName = StringUtil.dealParam(request.getParameter("productName"));
        String productOriName = StringUtil.dealParam(request.getParameter("productOriName"));
        String startDate = StringUtil.dealParam(request.getParameter("startDate"));
        String endDate = StringUtil.dealParam(request.getParameter("endDate"));

        productName = Encoder.decrypt(productName);//解码为中文
		if(productName==null){//解码失败,表示已经为中文,则返回默认
			productName =StringUtil.dealParam(request.getParameter("productName"));//名称
		}
		if (productName==null) productName="";

		productOriName = Encoder.decrypt(productOriName);//解码为中文
		if(productOriName==null){//解码失败,表示已经为中文,则返回默认
			productOriName =StringUtil.dealParam(request.getParameter("productOriName"));//名称
		}
		if (productOriName==null) productOriName="";

		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
        try {
        	if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
        		Date start = DateUtil.parseDate(startDate);
        		Date end = DateUtil.parseDate(endDate);
        		if(end.getTime() < start.getTime()){
            		request.setAttribute("tip", "截止时间不能早于起始时间！");
                    request.setAttribute("result", "failure");
                    return;
        		}
        	}
            String condition = null;
            StringBuilder buf = new StringBuilder();
            StringBuilder paramBuf = new StringBuilder();
            boolean canQuery = false;
            if(stockType >= 0){
            	buf.append("stock_type=").append(stockType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockType=").append(stockType);
            }
            if(stockArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_area=").append(stockArea);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockArea=").append(stockArea);
            }
            if( stockCardType != -1 ) {
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("card_type=").append(stockCardType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockCardType=").append(stockCardType);
            }
            if(!StringUtil.isNull(code)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("code='").append(code).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("code=").append(code);
            }
            if(!StringUtil.isNull(startDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime >='").append(startDate).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("startDate=").append(startDate);
            }
            if(!StringUtil.isNull(endDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime  <='").append(endDate).append(" 23:59:59'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("endDate=").append(endDate);
            }
            voProduct p = null;
            StringBuilder pBuf = new StringBuilder();
            if(!StringUtil.isNull(productCode)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.code='").append(productCode).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productCode=").append(productCode);
            }
            if(!StringUtil.isNull(productName)){
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productName=").append(Encoder.encrypt(productName));
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.name='").append(productName).append("'");
            }
        	if(!StringUtil.isNull(productOriName)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.oriname='").append(productOriName).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productOriName=").append(Encoder.encrypt(productOriName));
        	}
        	if(pBuf.length() > 0){
        		p = wareService.getProduct2(pBuf.toString());
            	if(p != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("product_id=").append(p.getId());
            		canQuery = true;
            		request.setAttribute("product", p);
            	}
        	}
            if(buf.length() > 0){
            	condition = buf.toString();
            }
            if(canQuery && condition != null){
	            //总数
	            int totalCount = service.getStockCardCount(condition);
	            //页码
	            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
	            PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
	            List list = service.getStockCardList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "create_datetime desc,id desc");
	            Collections.sort(list, new StockCardComparator());
	            paging.setPrefixUrl("stockCardList.jsp?" + paramBuf.toString());

	            request.setAttribute("paging", paging);
	            request.setAttribute("list", list);
            }
        } finally {
        	dbOp_slave.release();
        }
    }
    
    /**
     * 
     * 作者：赵林
     * 
     * 创建日期：2012-12-03
     * 
     * 说明：进销存卡片列表直接导出
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     * @throws UnsupportedEncodingException 
     */
    public void stockCardListExport(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        int stockType = StringUtil.toInt(request.getParameter("stockType"));
        int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
        String code = StringUtil.dealParam(request.getParameter("code"));
        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
        String productName = StringUtil.dealParam(request.getParameter("productName"));
        String productOriName = StringUtil.dealParam(request.getParameter("productOriName"));
        String startDate = StringUtil.dealParam(request.getParameter("startDate"));
        String endDate = StringUtil.dealParam(request.getParameter("endDate"));

        productName = Encoder.decrypt(productName);//解码为中文
		if(productName==null){//解码失败,表示已经为中文,则返回默认
			productName =StringUtil.dealParam(request.getParameter("productName"));//名称
		}
		if (productName==null) productName="";

		productOriName = Encoder.decrypt(productOriName);//解码为中文
		if(productOriName==null){//解码失败,表示已经为中文,则返回默认
			productOriName =StringUtil.dealParam(request.getParameter("productOriName"));//名称
		}
		if (productOriName==null) productOriName="";

		DbOperation dbOp_slave2 = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave2);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave2);
        try {
        	if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
        		Date start = DateUtil.parseDate(startDate);
        		Date end = DateUtil.parseDate(endDate);
        		if(end.getTime() < start.getTime()){
            		request.setAttribute("tip", "截止时间不能早于起始时间！");
                    request.setAttribute("result", "failure");
                    return;
        		}
        	}
            String condition = null;
            StringBuilder buf = new StringBuilder();
            StringBuilder paramBuf = new StringBuilder();
            boolean canQuery = false;
            if(stockType >= 0){
            	buf.append("stock_type=").append(stockType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockType=").append(stockType);
            }
            if(stockArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_area=").append(stockArea);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockArea=").append(stockArea);
            }
            if(!StringUtil.isNull(code)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("code='").append(code).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("code=").append(code);
            }
            if(!StringUtil.isNull(startDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime >='").append(startDate).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("startDate=").append(startDate);
            }
            if(!StringUtil.isNull(endDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime  <='").append(endDate).append(" 23:59:59'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("endDate=").append(endDate);
            }
            voProduct p = null;
            StringBuilder pBuf = new StringBuilder();
            if(!StringUtil.isNull(productCode)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.code='").append(productCode).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productCode=").append(productCode);
            }
            if(!StringUtil.isNull(productName)){
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productName=").append(Encoder.encrypt(productName));
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.name='").append(productName).append("'");
            }
        	if(!StringUtil.isNull(productOriName)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.oriname='").append(productOriName).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productOriName=").append(Encoder.encrypt(productOriName));
        	}
        	if(pBuf.length() > 0){
        		p = wareService.getProduct2(pBuf.toString());
            	if(p != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("product_id=").append(p.getId());
            		canQuery = true;
            		request.setAttribute("product", p);
            	}
        	}
            if(buf.length() > 0){
            	condition = buf.toString();
            }
            if(canQuery && condition != null){
	            List list = service.getStockCardList(condition, -1, -1, "create_datetime asc");
	            Collections.sort(list, new StockCardComparator());

	            request.setAttribute("list", list);
            }
        } finally {
        	dbOp_slave2.release();
        }
    }


    
    public void updateLackOrder(int productId){
    	DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
    	DbOperation dbOp = new DbOperation(DbOperation.DB);
    	WareService wareService = new WareService(dbOp_slave);
    	IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
    	try{
    		String productIds = productId+"";
    		//查询父商品
    		List ppList = ppService.getProductPackageList("product_id=" + productId, -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
    			productIds = productIds + "," + ppBean.getParentId();
			}
    		
    		List lackOrders = wareService.getOrdersByProducts("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null);
    		lackOrders.addAll(wareService.getOrdersByPresents("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null));
    		Iterator iter = lackOrders.listIterator();
    		while(iter.hasNext()){
    			voOrder order = (voOrder)iter.next();

				// 判断订单中商品的库存是否满足，根据库存状态，设置订单发货状态
				List orderProductList = wareService.getOrderProducts(order.getId());
				List orderPresentList = wareService.getOrderPresents(order.getId());
				orderProductList.addAll(orderPresentList);

				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(service.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							detailList.add(tempVOP);
						}
					} else {
						vop.setPsList(service.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						detailList.add(vop);
					}
				}
				orderProductList = detailList;

				if (checkStock(orderProductList,ProductStockBean.AREA_GF) || checkStock(orderProductList,ProductStockBean.AREA_ZC)) {
					dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+order.getId());
					dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+order.getId());
				}
			
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			dbOp_slave.release();
			dbOp.release();
		}
    	
    }
    
    public boolean checkStock(List orderProductList,int area) {
		if (orderProductList == null) {
			return false;
		}

		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.STOCKTYPE_QUALIFIED,area) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
    /**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2012-9-05
	 * 
	 * 说明:批量入库操作
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void batchInStorage(HttpServletRequest request, HttpServletResponse response) {

		String check1 = request.getParameter("batchInHidden1");
		String check2 = request.getParameter("batchInHidden2");
		String check3 = request.getParameter("batchInHidden3");
		String check4 = null;
		if (check1 != null) {
			check4 = check1;
		} else if (check2 != null) {
			check4 = check2;
		} else if (check3 != null) {
			check4 = check3;
		}
		if(check4==null||check4.length()==0){
			request.setAttribute("tip", "请选择要操作的调拨单");
			request.setAttribute("result", "failure");
			return;
		}
		String check[] = check4.split(",");
		String type = StringUtil.dealParam(request.getParameter("type"));
		WareService wareService = new WareService();
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean stockIn = group.isFlag(83);
		boolean auditingIn = group.isFlag(81);
		synchronized (stockLock) {
			try {
				if (type != null && type.equals("1")) {// 批量入库
					if(!group.isFlag(602)){
						request.setAttribute("tip", "您没有此权限！");
						request.setAttribute("result", "failure");
						return;
					}
					if (check != null) {
						for (int i = 0; i < check.length; i++) {
							StockExchangeBean bean = service.getStockExchange("id=" + check[i]);
							if (bean != null) {
								if (bean.getStockInType() != ProductStockBean.STOCKTYPE_BACK) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）不是调入返厂库的调拨单！");
									request.setAttribute("result", "failure");
									return;
								}
								if (bean.getStatus() < StockExchangeBean.STATUS3) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）未经出库审核通过！");
									request.setAttribute("result", "failure");
									return;
								} else if (bean.getStatus() > StockExchangeBean.STATUS3) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）已入库！");
									request.setAttribute("result", "failure");
									return;
								}
							}
						}
						for (int i = 0; i < check.length; i++) {
							StockExchangeBean bean = service.getStockExchange("id = " + check[i]);

							if (bean == null) {
								request.setAttribute("tip", "该调拨单不存在或已删除!");
								request.setAttribute("result", "failure");
								return;
							}

							// 当 调配单 的状态 不是 “未处理” 或者 “检测处理中” 的时候，不能进行确认操作
							if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS5 && bean.getStatus() != StockExchangeBean.STATUS6 && bean.getStatus() != StockExchangeBean.STATUS8) {
								request.setAttribute("tip", "该操作已经完成，不能再更改！");
								request.setAttribute("result", "failure");
								return;
							}
							if (service.getStockExchangeProductCount("stock_exchange_id=" + bean.getId()) <= 0) {
								request.setAttribute("tip", "没有要调拨的商品，不能执行该操作！");
								request.setAttribute("result", "failure");
								return;
							}

							// 开始事务
							service.getDbOp().startTransaction();
							// 根据 调配单的不同状态，进行不同的处理
							if (bean.getStatus() == StockExchangeBean.STATUS3) {
								if (!stockIn) {
									request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
								StringBuilder buf = new StringBuilder();
								String condition = null;
								buf.append("stock_exchange_id = ");
								buf.append(bean.getId());
								buf.append(" and status = ");
								buf.append(StockExchangeProductBean.STOCKIN_UNDEAL);
								condition = buf.toString();
								ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
								Iterator itr = sepList.iterator();
								StockExchangeProductBean sep = null;
								voProduct product = null;
								String set = null;
								while (itr.hasNext()) {
									sep = (StockExchangeProductBean) itr.next();
									product = wareService.getProduct(sep.getProductId());
									set = "status = " + StockExchangeProductBean.STOCKIN_DEALED + ", confirm_datetime = now()";
									if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
									{
									  service.getDbOp().rollbackTransaction();
									  request.setAttribute("tip", "数据库操作失败");
									  request.setAttribute("result", "failure");
									  return;
									}
									// log记录
									StockAdminHistoryBean log = new StockAdminHistoryBean();
									log.setAdminId(user.getId());
									log.setAdminName(user.getUsername());
									log.setLogId(bean.getId());
									log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
									log.setOperDatetime(DateUtil.getNow());
									log.setRemark("商品检测操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]入库");
									log.setType(StockAdminHistoryBean.CHANGE);
									if(!stockService.addStockAdminHistory(log))
									{
									  service.getDbOp().rollbackTransaction();
									  request.setAttribute("tip", "数据库操作失败");
									  request.setAttribute("result", "failure");
									  return;
									}
									
								}
								
									if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS5, "id=" + bean.getId()))
									{
									  service.getDbOp().rollbackTransaction();
									  request.setAttribute("tip", "数据库操作失败");
									  request.setAttribute("result", "failure");
									  return;
									}
							}

							// 提交事务
							service.getDbOp().commitTransaction();
						}
					}
				} else if (type != null && type.equals("2")) {// 批量确认入库
					if(!group.isFlag(604)){
						request.setAttribute("tip", "您没有此权限！");
						request.setAttribute("result", "failure");
						return;
					}
					if (check != null) {
						for (int i = 0; i < check.length; i++) {
							StockExchangeBean bean = service.getStockExchange("id=" + check[i]);
							if (bean != null) {
								if (bean.getStockInType() != ProductStockBean.STOCKTYPE_BACK) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）不是调入返厂库的调拨单！");
									request.setAttribute("result", "failure");
									return;
								}
								if (bean.getStatus() < StockExchangeBean.STATUS5) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）未入库！");
									request.setAttribute("result", "failure");
									return;
								} else if (bean.getStatus() > StockExchangeBean.STATUS5) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）已确认入库！");
									request.setAttribute("result", "failure");
									return;
								}
							}
						}
						for (int i = 0; i < check.length; i++) {
							StockExchangeBean bean = service.getStockExchange("id = " + check[i]);

							if (bean == null) {
								request.setAttribute("tip", "该调拨单不存在或已删除!");
								request.setAttribute("result", "failure");
								return;
							}

							
							// 当 调配单 的状态 不是 “未处理” 或者 “检测处理中” 的时候，不能进行确认操作
							if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS2 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS5 && bean.getStatus() != StockExchangeBean.STATUS6 && bean.getStatus() != StockExchangeBean.STATUS8) {
								request.setAttribute("tip", "该操作已经完成，不能再更改！");
								request.setAttribute("result", "failure");
								return;
							}
							if (service.getStockExchangeProductCount("stock_exchange_id=" + bean.getId()) <= 0) {
								request.setAttribute("tip", "没有要调拨的商品，不能执行该操作！");
								request.setAttribute("result", "failure");
								return;
							}
							if (!stockIn) {
								request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
								request.setAttribute("result", "failure");
								return;
							}
							StringBuilder buf = new StringBuilder();
							String condition = null;
							buf.append("stock_exchange_id = ");
							buf.append(bean.getId());
							buf.append(" and status = ");
							buf.append(StockExchangeProductBean.STOCKIN_UNDEAL);
							condition = buf.toString();
							int count = service.getStockExchangeProductCount(condition);
							if (count > 0) {
								request.setAttribute("tip", "还有没确认的商品，不能完成入库操作！");
								request.setAttribute("result", "failure");
								return;
							}
							service.getDbOp().startTransaction();
							String set = "status = " + StockExchangeBean.STATUS6 + ", confirm_datetime = now(), stock_in_oper=" + user.getId() + ", stock_in_oper_name='" + user.getUsername() + "'";
							if(!service.updateStockExchange(set, "id = " + bean.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成入库操作");
							log.setType(StockAdminHistoryBean.CHANGE);
							if(!stockService.addStockAdminHistory(log))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return;
							}
							service.getDbOp().commitTransaction();

						}
					}
				} else if (type != null && type.equals("3")) {// 批量审核
					if(!group.isFlag(603)){
						request.setAttribute("tip", "您没有此权限！");
						request.setAttribute("result", "failure");
						return;
					}
					if (check != null) {
						for (int i = 0; i < check.length; i++) {
							StockExchangeBean bean = service.getStockExchange("id=" + check[i]);
							if (bean != null) {
								if (bean.getStockInType() != ProductStockBean.STOCKTYPE_BACK) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）不是调入返厂库的调拨单！");
									request.setAttribute("result", "failure");
									return;
								}
								if (bean.getStatus() < StockExchangeBean.STATUS6) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）未确认入库！");
									request.setAttribute("result", "failure");
									return;
								} else if (bean.getStatus() > StockExchangeBean.STATUS6) {
									request.setAttribute("tip", "调拨单（" + bean.getCode() + "）已调拨完成！");
									request.setAttribute("result", "failure");
									return;
								}
							}
						}
						for (int i = 0; i < check.length; i++) {
							StockExchangeBean bean = service.getStockExchange("id = " + check[i]);

							// 开始事务
							service.getDbOp().startTransaction();

							// 同步控制， 入库审核时 一个页面审核失败。 另一个页面审核通过 应该提示。
							// 审核不通过调拨单的状态会变回出库审核

							if (bean.getStatus() == StockExchangeBean.STATUS6) {
								if (!auditingIn) {
									request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
									request.setAttribute("result", "failure");
									return;
								}
								String condition = "stock_exchange_id = " + bean.getId() + " and status = " + StockExchangeProductBean.STOCKIN_DEALED;
								// 审核通过，要入库
								ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
								Iterator itr = sepList.iterator();
								StockExchangeProductBean sep = null;
								voProduct product = null;
								ProductStockBean psOut = null;
								ProductStockBean psIn = null;
								String set = null;
								while (itr.hasNext()) {
									sep = (StockExchangeProductBean) itr.next();
									product = wareService.getProduct(sep.getProductId());
									psIn = service.getProductStock("id=" + sep.getStockInId());
									psOut = service.getProductStock("id=" + sep.getStockOutId());
									// 入库
									set = "status = " + StockExchangeProductBean.STOCKIN_DEALED + ", remark = '操作前库存" + psIn.getStock() + ",操作后库存" + (psIn.getStock() + sep.getStockInCount()) + "', confirm_datetime = now()";
									if (!service.updateStockExchangeProduct(set, "id = " + sep.getId())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "数据库操作失败！");
										request.setAttribute("result", "failure");
										return;
									}
									if (!service.updateProductLockCount(sep.getStockOutId(), -sep.getStockOutCount())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "商品" + product.getCode() + "修改产品库存锁定数量操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}

									if (!service.updateProductStockCount(sep.getStockInId(), sep.getStockInCount())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "商品" + product.getCode() + "修改产品库存数量操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										return;
									}
									
									//对于目的库是退货库的调拨单，要加未质检量
									/*if( bean.getStockInArea() == ProductStockBean.AREA_ZC && bean.getStockInType() == ProductStockBean.STOCKTYPE_RETURN ) {
										if( !service.addUnappraisalNumberOrReturnedProduct(sep.getProductId(), product.getCode(), product.getName(), sep.getStockInCount())) {
											request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，在加退货库未质检量时出了问题");
						                    request.setAttribute("result", "failure");
						                    return;
										}
									}*/
									
									//对于从退货库调往合格库的 商品数量 要剪掉合格量 
									/*if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED ) {
										if(!service.updateUnqualifyReturnedProduct(sep.getProductId(), ReturnedProductBean.APPRAISAL_QUALIFY, sep.getStockOutCount())) {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，可能是退货库合格商品数量不足");
						                    request.setAttribute("result", "failure");
						                    return;
										}
										
										List returnedProductCargoList = service.getReturnedProductCargoList("product_id = " + product.getId(), -1, -1, "count desc");
										int leftCount = sep.getStockOutCount();
										if( returnedProductCargoList.size() > 0 ) {
											for( int k = 0; k < returnedProductCargoList.size(); k ++ ) {
												ReturnedProductCargoBean rpcb = (ReturnedProductCargoBean)returnedProductCargoList.get(k);
												if( (leftCount - rpcb.getCount()) <= 0 ) {
													if( !statService.updateReturnedProductCargo("count = count - " + leftCount, "id = " + rpcb.getId())) {
														service.getDbOp().rollbackTransaction();
														request.setAttribute("tip", "数据库存操作失败");
									                    request.setAttribute("result", "failure");
									                    return;
													}
													leftCount = 0;
												} else {
													leftCount -= rpcb.getCount();
													if( !statService.updateReturnedProductCargo("count = count - " + rpcb.getCount(), "id = " + rpcb.getId())) {
														service.getDbOp().rollbackTransaction();
														request.setAttribute("tip", "数据库存操作失败");
									                    request.setAttribute("result", "failure");
									                    return;
													}
												}
											}
											if( leftCount != 0 ) {
												service.getDbOp().rollbackTransaction();
												request.setAttribute("tip", "商品"+product.getCode()+"的绑定货位的合格量不足");
							                    request.setAttribute("result", "failure");
							                    return;
											}
										} else {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，合格量在合格量货位关联表里没有记录");
						                    request.setAttribute("result", "failure");
						                    return;
										}
									}*/

									// log记录
									StockAdminHistoryBean log = new StockAdminHistoryBean();
									log.setAdminId(user.getId());
									log.setAdminName(user.getUsername());
									log.setLogId(bean.getId());
									log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
									log.setOperDatetime(DateUtil.getNow());
									log.setRemark("商品调配操作：" + bean.getName() + ": 将商品[" + product.getCode() + "]入库");
									log.setType(StockAdminHistoryBean.CHANGE);
									if (!stockService.addStockAdminHistory(log)) {
										 service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "数据库操作失败！");
										request.setAttribute("result", "failure");
										return;
									}

									// 入库货位库存
									StockExchangeProductCargoBean sepcIn = service.getStockExchangeProductCargo("stock_exchange_id = " + bean.getId() + " and stock_exchange_product_id = " + sep.getId() + " and type = 1");
									StockExchangeProductCargoBean sepcOut = service.getStockExchangeProductCargo("stock_exchange_id = " + bean.getId() + " and stock_exchange_product_id = " + sep.getId() + " and type = 0");
									if (!cargoService.updateCargoProductStockCount(sepcIn.getCargoProductStockId(), sepcIn.getStockCount())) {
										request.setAttribute("tip", "商品" + product.getCode() + "修改产品货位库存数量操作失败，货位锁定库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}
									if (!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())) {
										request.setAttribute("tip", "商品" + product.getCode() + "修改产品货位库存冻结数量操作失败，货位锁定库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}

									// 更新调拨单未上架数量
									if (!service.updateStockExchangeProduct("no_up_cargo_count = " + sep.getStockOutCount(), "id = " + sep.getId())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "数据库操作失败！");
										request.setAttribute("result", "failure");
										return;
									}

									// 更新批次记录、添加调拨出、入库批次记录
									List sbList = stockService.getStockBatchList("product_id=" + psOut.getProductId() + " and stock_type=" + psOut.getType() + " and stock_area=" + psOut.getArea(), -1, -1, "id asc");
									double stockinPrice = 0;
									double stockoutPrice = 0;
									if (sbList != null && sbList.size() != 0) {
										int stockExchangeCount = sep.getStockOutCount();
										int index = 0;
										int stockBatchCount = 0;

										do {
											// 出库
											StockBatchBean batch = (StockBatchBean) sbList.get(index);
											if (stockExchangeCount >= batch.getBatchCount()) {
												if (!stockService.deleteStockBatch("id=" + batch.getId())) {
													service.getDbOp().rollbackTransaction();
													request.setAttribute("tip", "数据库操作失败！");
													request.setAttribute("result", "failure");
													return;
												}
												stockBatchCount = batch.getBatchCount();
											} else {
												if (!stockService.updateStockBatch("batch_count = batch_count-" + stockExchangeCount, "id=" + batch.getId())) {
													service.getDbOp().rollbackTransaction();
													request.setAttribute("tip", "数据库操作失败！");
													request.setAttribute("result", "failure");
													return;
												}
												stockBatchCount = stockExchangeCount;
											}

											// 添加批次操作记录
											StockBatchLogBean batchLog = new StockBatchLogBean();
											batchLog.setCode(bean.getCode());
											batchLog.setStockType(batch.getStockType());
											batchLog.setStockArea(batch.getStockArea());
											batchLog.setBatchCode(batch.getCode());
											batchLog.setBatchCount(stockBatchCount);
											batchLog.setBatchPrice(batch.getPrice());
											batchLog.setProductId(batch.getProductId());
											batchLog.setRemark("调拨出库");
											batchLog.setCreateDatetime(DateUtil.getNow());
											batchLog.setUserId(user.getId());
											batchLog.setSupplierId(batch.getSupplierId());
											batchLog.setTax(batch.getTax());
											if (!stockService.addStockBatchLog(batchLog)) {
												request.setAttribute("tip", "添加失败！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}

											stockoutPrice = stockoutPrice + batchLog.getBatchCount() * batchLog.getBatchPrice();

											// 入库
											StockBatchBean batchBean = stockService.getStockBatch("code='" + batch.getCode() + "' and product_id=" + batch.getProductId() + " and stock_type=" + psIn.getType() + " and stock_area=" + psIn.getArea());
											if (batchBean != null) {
												if (!stockService.updateStockBatch("batch_count = batch_count+" + stockBatchCount, "id=" + batchBean.getId())) {
													service.getDbOp().rollbackTransaction();
													request.setAttribute("tip", "数据库操作失败！");
													request.setAttribute("result", "failure");
													return;
												}
											} else {
												StockBatchBean newBatch = new StockBatchBean();
												newBatch.setCode(batch.getCode());
												newBatch.setProductId(batch.getProductId());
												newBatch.setPrice(batch.getPrice());
												newBatch.setBatchCount(stockBatchCount);
												newBatch.setProductStockId(psIn.getId());
												newBatch.setStockArea(bean.getStockInArea());
												newBatch.setStockType(psIn.getType());
												newBatch.setSupplierId(batch.getSupplierId());
												newBatch.setTax(batch.getTax());
												newBatch.setNotaxPrice(batch.getNotaxPrice());
												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
												if (!stockService.addStockBatch(newBatch)) {
													request.setAttribute("tip", "添加失败！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return;
												}
											}

											// 添加批次操作记录
											batchLog = new StockBatchLogBean();
											batchLog.setCode(bean.getCode());
											batchLog.setStockType(psIn.getType());
											batchLog.setStockArea(bean.getStockInArea());
											batchLog.setBatchCode(batch.getCode());
											batchLog.setBatchCount(stockBatchCount);
											batchLog.setBatchPrice(batch.getPrice());
											batchLog.setProductId(batch.getProductId());
											batchLog.setRemark("调拨入库");
											batchLog.setCreateDatetime(DateUtil.getNow());
											batchLog.setUserId(user.getId());
											batchLog.setSupplierId(batch.getSupplierId());
											batchLog.setTax(batch.getTax());
											if (!stockService.addStockBatchLog(batchLog)) {
												request.setAttribute("tip", "添加失败！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return;
											}

											stockExchangeCount -= batch.getBatchCount();
											index++;

											stockinPrice = stockinPrice + batchLog.getBatchCount() * batchLog.getBatchPrice();
										} while (stockExchangeCount > 0 && index < sbList.size());
									}

									// 审核通过，就加 进销存卡片
									product.setPsList(service.getProductStockList("product_id=" + sep.getProductId(), -1, -1, null));
									CargoProductStockBean cpsIn = cargoService.getCargoAndProductStock("cps.id = " + sepcIn.getCargoProductStockId());
									CargoProductStockBean cpsOut = cargoService.getCargoAndProductStock("cps.id = " + sepcOut.getCargoProductStockId());

									// 出库卡片
									StockCardBean sc = new StockCardBean();
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc.setCode(bean.getCode());
									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(bean.getStockOutType());
									sc.setStockArea(bean.getStockOutArea());
									sc.setProductId(sep.getProductId());
									sc.setStockId(sep.getStockOutId());
									sc.setStockOutCount(sep.getStockOutCount());
									// sc.setStockOutPriceSum(stockoutPrice);
									sc.setStockOutPriceSum((new BigDecimal(sep.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									sc.setCurrentStock(product.getStock(bean.getStockOutArea(), bean.getStockOutType()) + product.getLockCount(bean.getStockOutArea(), bean.getStockOutType()));
									sc.setStockAllArea(product.getStock(bean.getStockOutArea()) + product.getLockCount(bean.getStockOutArea()));
									sc.setStockAllType(product.getStockAllType(bean.getStockOutType()) + product.getLockCountAllType(bean.getStockOutType()));
									sc.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc.setStockPrice(product.getPrice5());
									sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									if (!service.addStockCard(sc)) {
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}

									// 货位出库卡片
									CargoStockCardBean csc = new CargoStockCardBean();
									csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									csc.setCode(bean.getCode());
									csc.setCreateDatetime(DateUtil.getNow());
									csc.setStockType(bean.getStockOutType());
									csc.setStockArea(bean.getStockOutArea());
									csc.setProductId(sep.getProductId());
									csc.setStockId(cpsOut.getId());
									csc.setStockOutCount(sep.getStockOutCount());
									csc.setStockOutPriceSum((new BigDecimal(sep.getStockOutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
									csc.setAllStock(product.getStockAll() + product.getLockCountAll());
									csc.setCurrentCargoStock(cpsOut.getStockCount() + cpsOut.getStockLockCount());
									csc.setCargoStoreType(cpsOut.getCargoInfo().getStoreType());
									csc.setCargoWholeCode(cpsOut.getCargoInfo().getWholeCode());
									csc.setStockPrice(product.getPrice5());
									csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									if (!cargoService.addCargoStockCard(csc)) {
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}

									// 入库卡片
									sc = new StockCardBean();
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									sc.setCode(bean.getCode());
									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(bean.getStockInType());
									sc.setStockArea(bean.getStockInArea());
									sc.setProductId(sep.getProductId());
									sc.setStockId(sep.getStockInId());
									sc.setStockInCount(sep.getStockInCount());
									// sc.setStockInPriceSum(stockinPrice);
									sc.setStockInPriceSum((new BigDecimal(sep.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									sc.setCurrentStock(product.getStock(bean.getStockInArea(), bean.getStockInType()) + product.getLockCount(bean.getStockInArea(), bean.getStockInType()));
									sc.setStockAllArea(product.getStock(bean.getStockInArea()) + product.getLockCount(bean.getStockInArea()));
									sc.setStockAllType(product.getStockAllType(bean.getStockInType()) + product.getLockCountAllType(bean.getStockInType()));
									sc.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc.setStockPrice(product.getPrice5());
									sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									if (!service.addStockCard(sc)) {
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}

									// 货位入库卡片
									csc = new CargoStockCardBean();
									csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									csc.setCode(bean.getCode());
									csc.setCreateDatetime(DateUtil.getNow());
									csc.setStockType(bean.getStockInType());
									csc.setStockArea(bean.getStockInArea());
									csc.setProductId(sep.getProductId());
									csc.setStockId(cpsIn.getId());
									csc.setStockInCount(sep.getStockInCount());
									csc.setStockInPriceSum((new BigDecimal(sep.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
									csc.setAllStock(product.getStockAll() + product.getLockCountAll());
									csc.setCurrentCargoStock(cpsIn.getStockCount() + cpsIn.getStockLockCount());
									csc.setCargoStoreType(cpsIn.getCargoInfo().getStoreType());
									csc.setCargoWholeCode(cpsIn.getCargoInfo().getWholeCode());
									csc.setStockPrice(product.getPrice5());
									csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									if (!cargoService.addCargoStockCard(csc)) {
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return;
									}

								}
								if (!service.updateStockExchange("status=" + StockExchangeBean.STATUS7 + ", auditing_user_id2=" + user.getId() + ", auditing_user_name2='" + user.getUsername() + "'", "id=" + bean.getId())) {
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "数据库操作失败！");
									request.setAttribute("result", "failure");
									return;
								}
								// log记录
								StockAdminHistoryBean log = new StockAdminHistoryBean();
								log.setAdminId(user.getId());
								log.setAdminName(user.getUsername());
								log.setLogId(bean.getId());
								log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
								log.setOperDatetime(DateUtil.getNow());
								log.setRemark("商品调配操作：" + bean.getName() + "：入库审核通过");
								log.setType(StockAdminHistoryBean.CHANGE);
								if (!stockService.addStockAdminHistory(log)) {
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "数据库操作失败！");
									request.setAttribute("result", "failure");
									return;
								}
							}
							// 提交事务
							service.getDbOp().commitTransaction();
						}
					}
				}
				request.setAttribute("tip", "操作成功");
				request.setAttribute("result", "success");
				 
				
			} catch (Exception e) {
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				service.releaseAll();
			}
		}
	}
	
	 /**
     * 
     * 作者：张晔
     * 
     * 创建日期：2013-12-17
     * 
     * 说明：删除一个IMEI码商品
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void deleteIMEIProduct(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	boolean edit = false;

    	synchronized(stockLock){
	        int exchangeId = StringUtil.StringToId(request.getParameter("exchangeId"));
	        int imeiStockExchangeId = StringUtil.StringToId(request.getParameter("imeiStockExchangeId"));
	        int sepId = StringUtil.StringToId(request.getParameter("sepId"));
	        String back = StringUtil.dealParam(request.getParameter("back"));
	        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	        IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        ISystemService sService = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        WareService wareService = new WareService(service.getDbOp());
	        IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	        try {
	            StockExchangeBean bean = service.getStockExchange("id = " + exchangeId);
	            
	            if(bean==null){
	            	request.setAttribute("tip", "该调拨单不存在或已删除!");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
	            if(bean.getCreateUserId() == user.getId()){
	            	edit = true;
	            }


	            //相关的产品
	            int stockOutCount = 0;
	            int stockInCount = 0;
	            boolean hasLoss = false;
	            //相关的调配操作
	            StockExchangeProductBean sep = service.getStockExchangeProduct("id=" + sepId);
	            
	            if (sep == null) {
	            	request.setAttribute("tip", "该调拨单商品不存在！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            
	            int imeiCount = sep.getStockOutCount();
	            
	            //开始事务
	            service.getDbOp().startTransaction();
	            request.setAttribute("imeiCount", imeiCount);
	            //删除前，如果删除该IMEI码的商品只还有一个，则进行删除商品操作，否则进行数量-1操作
	            if (imeiCount == 1) {
		            if(bean.getCreateUserId() == user.getId()){
		            	edit = true;
		            }
	            	if(!edit){
		            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
		                request.setAttribute("result", "failure");
		                service.getDbOp().rollbackTransaction();
		                return;
		            }
		            if (bean.getStatus() == StockExchangeBean.STATUS2) {
		                request.setAttribute("tip", "该操作已经完成，不能再修改！");
		                request.setAttribute("result", "failure");
		                service.getDbOp().rollbackTransaction();
		                return;
		            }
		            String condition = "stock_exchange_id = " + bean.getId() + " and id = " + sepId + " and status <> " + StockExchangeProductBean.STOCKOUT_UNDEAL;
		            if (service.getStockExchangeProductCount(condition) > 0) {
		                request.setAttribute("tip", "该产品已经有出入库了，不能删除！");
		                request.setAttribute("result", "failure");
		                service.getDbOp().rollbackTransaction();
		                return;
		            }
		            
			        voProduct product = wareService.getProduct(sep.getProductId());

		        	if(!imeiService.deleteIMEIStockExchange("id=" + imeiStockExchangeId))
		        	{
		        		service.getDbOp().rollbackTransaction();
		        		request.setAttribute("tip", "数据库操作失败");
		        		request.setAttribute("result", "failure");
		        		return;
		        	}
		        	if(!service.deleteStockExchangeProduct("stock_exchange_id = " + exchangeId + " and id = " + sepId))
		        	{
		        		service.getDbOp().rollbackTransaction();
		        		request.setAttribute("tip", "数据库操作失败");
		        		request.setAttribute("result", "failure");
		        		return;
		        	}
		        	if(! service.deleteStockExchangeProductCargo("stock_exchange_id = " + exchangeId + " and stock_exchange_product_id = " + sepId))
		        	{
		        		service.getDbOp().rollbackTransaction();
		        		request.setAttribute("tip", "数据库操作失败");
		        		request.setAttribute("result", "failure");
		        		return;
		        	}
		            // 如果这个 库存调配单的状态时 未处理/出库审核未通过，改为出库处理中
		            if(bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS4){
		            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
		            	{
		            	  service.getDbOp().rollbackTransaction();
		            	  request.setAttribute("tip", "数据库操作失败");
		            	  request.setAttribute("result", "failure");
		            	  return;
		            	}
		            }

		            //log记录
		            StockAdminHistoryBean log = new StockAdminHistoryBean();
		            log.setAdminId(user.getId());
		            log.setAdminName(user.getUsername());
		            log.setLogId(bean.getId());
		            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		            log.setOperDatetime(DateUtil.getNow());
	            	log.setRemark("修改商品调配操作：" + bean.getName() + ",删除了商品[" + product.getCode() + "]");
		            log.setType(StockAdminHistoryBean.CHANGE);
		            if(! stockService.addStockAdminHistory(log))
		            {
		              service.getDbOp().rollbackTransaction();
		              request.setAttribute("tip", "数据库操作失败");
		              request.setAttribute("result", "failure");
		              return;
		            }
	            } else {
	            	if (bean.getStatus() != StockExchangeBean.STATUS1 && bean.getStatus() != StockExchangeBean.STATUS4 && bean.getStatus() != StockExchangeBean.STATUS3 && bean.getStatus() != StockExchangeBean.STATUS8) {
	 	                request.setAttribute("tip", "该操作已经完成，不能再修改！");
	 	                request.setAttribute("result", "failure");
	 	                service.getDbOp().rollbackTransaction();
	 	                return;
	 	            }

	 	            // 出库审核通过之前， 只能由创建者修改 调拨单的数据
	 	            if(bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() != StockExchangeBean.STATUS4){
	 		            if(!edit){
	 		            	request.setAttribute("tip", "对比起，你没有权限编辑其他人的调拨单！");
	 		                request.setAttribute("result", "failure");
	 		                service.getDbOp().rollbackTransaction();
	 		                return;
	 		            }
	 	            }
	 	            
	 	            voProduct product = null;
		            ProductStockBean psIn = null;
		            ProductStockBean psOut = null;
		            Map psMap = new HashMap();
	                //调配操作记录
	                product = wareService.getProduct(sep.getProductId());
	                psIn = (ProductStockBean)psMap.get("psIn" + sep.getStockInId());
	                if(psIn == null){
	                	psIn = service.getProductStock("id=" + sep.getStockInId());
	                	psMap.put("psIn" + sep.getStockInId(), psIn);
	                }
	                psOut = (ProductStockBean)psMap.get("psOut" + sep.getStockOutId());
	                if(psOut == null){
	                	psOut = service.getProductStock("id=" + sep.getStockOutId());
	                	psMap.put("psOut" + sep.getStockOutId(), psOut);
	                }

	                //该产品已经处理完成
	                if (sep.status == StockExchangeProductBean.STOCKOUT_DEALED || sep.status == StockExchangeProductBean.STOCKIN_DEALED) {
	                	request.setAttribute("tip", "该产品已处理完成！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }

	                stockOutCount = sep.getStockOutCount() - 1;
	                stockInCount = stockOutCount;

	                if (stockOutCount == 0 && stockInCount == 0) {
	                    request.setAttribute("tip", "请输入正确的出入库量！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }
	                

	                if (stockOutCount > 0 && stockInCount != stockOutCount) {
	                    hasLoss = true;
	                }
	                if (stockOutCount <= 0) {
	                    request.setAttribute("tip", product.getName() + "必须有出库量！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }
	                if (stockOutCount > 0 && stockInCount > stockOutCount) {
	                    request.setAttribute("tip", product.getName() + "入库量不能大于出库量！");
	                    request.setAttribute("result", "failure");
	                    service.getDbOp().rollbackTransaction();
	                    return;
	                }
	                if (stockOutCount > 0 && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
	                    if (stockOutCount > psOut.getStock()) {
		                        request.setAttribute("tip", product.getName() + "的库存不足！");
		                        request.setAttribute("result", "failure");
		                        service.getDbOp().rollbackTransaction();
		                        return;
	                	} else {
	                		psOut.setStock(psOut.getStock() - stockOutCount);
	                	}
	                    
	                }
	              //添加退货库库存检验的地方-----------------------------------
                   /* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() != ProductStockBean.STOCKTYPE_QUALIFIED ) {
			        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_UNQUALIFY, stockOutCount)) {
			        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的不合格商品数量不足库存不足， 不可外调！");
				            request.setAttribute("result", "failure");
				            service.getDbOp().rollbackTransaction();
				            return;
			        	}
			        }*/ 
                    
                  //添加从退货库到合格库的调拨的判断
    		       /* if( bean.getStockOutArea() == ProductStockBean.AREA_ZC && bean.getStockOutType() == ProductStockBean.STOCKTYPE_RETURN && bean.getStockInType() == ProductStockBean.STOCKTYPE_QUALIFIED ) {
    		        	if( !service.checkUnqualifyReturnedProductNumber(product.getCode(), ReturnedProductBean.APPRAISAL_QUALIFY, stockOutCount)) {
    		        		request.setAttribute("tip", "产品" + product.getName() + "的在退货库 的合格商品数量不足库存不足， 不可外调！");
    			            request.setAttribute("result", "failure");
    			            service.getDbOp().rollbackTransaction();
    			            return;
    		        	}
    		        }*/

	                //出库记录
	                if ((bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS1 || bean.getStatus() == StockExchangeBean.STATUS4) && sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
	                    if(!service.updateStockExchangeProduct("stock_out_count=" + stockOutCount + ", stock_in_count=" + stockOutCount, "id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                    if(!service.updateStockExchangeProductCargo("stock_count = "+stockOutCount, "stock_exchange_product_id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                    if (!imeiService.deleteIMEIStockExchange("id=" + imeiStockExchangeId)) {
	                    	service.getDbOp().rollbackTransaction();
		                    request.setAttribute("tip", "数据库操作失败");
		                    request.setAttribute("result", "failure");
		                    return;
	                    }
	                }
	                if((bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS5 || bean.getStatus() == StockExchangeBean.STATUS8) && sep.getStatus() == StockExchangeProductBean.STOCKIN_UNDEAL){
	                	if(!service.updateStockExchangeProduct("stock_in_count=" + stockInCount, "id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                	if(!service.updateStockExchangeProductCargo("stock_count = "+stockOutCount, "stock_exchange_product_id=" + sep.getId()))
	                    {
	                      service.getDbOp().rollbackTransaction();
	                      request.setAttribute("tip", "数据库操作失败");
	                      request.setAttribute("result", "failure");
	                      return;
	                    }
	                }
		            //log记录
		            StockAdminHistoryBean log = new StockAdminHistoryBean();
		            log.setAdminId(user.getId());
		            log.setAdminName(user.getUsername());
		            log.setLogId(bean.getId());
		            log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		            log.setOperDatetime(DateUtil.getNow());
		            log.setRemark("修改商品调配操作：" + bean.getName() + ",修改了了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:[" + sep.getStockOutCount() + "->" + stockOutCount + "]");
		            log.setType(StockAdminHistoryBean.CHANGE);
		            if(!stockService.addStockAdminHistory(log))
                    {
                      service.getDbOp().rollbackTransaction();
                      request.setAttribute("tip", "数据库操作失败");
                      request.setAttribute("result", "failure");
                      return;
                    }
		            // 如果这个 库存调配单的状态时 未处理/出库审核未通过，改为出库处理中
		            if(bean.getStatus() == StockExchangeBean.STATUS0 || bean.getStatus() == StockExchangeBean.STATUS4){
		            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId()))
		            	{
		            		service.getDbOp().rollbackTransaction();
		            		request.setAttribute("tip", "数据库操作失败");
		            		request.setAttribute("result", "failure");
		            		return;
		            	}
		            }
		            //如果这个 库存调配单的状态时 出库审核通过待入库/入库审核未通过，改为入库处理中
		            if(bean.getStatus() == StockExchangeBean.STATUS3 || bean.getStatus() == StockExchangeBean.STATUS8){
		            	if(!service.updateStockExchange("status=" + StockExchangeBean.STATUS5, "id=" + bean.getId()))
		            	{
		            		service.getDbOp().rollbackTransaction();
		            		request.setAttribute("tip", "数据库操作失败");
		            		request.setAttribute("result", "failure");
		            		return;
		            	}
		            }
	            }
		            

	            //提交事务
	            service.getDbOp().commitTransaction();
	            request.setAttribute("back", back);
	            if (hasLoss) {
	                request.setAttribute("result", "hasLoss");
	            }
	        } catch (Exception e) {
  				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			}finally {
	            service.releaseAll();
	        }
    	}
    }
    
    /**
     * 去掉批次单后面的X
     * @param code
     * @return
     */
    public String removeX(String code) {
    	String result = "";
    	while( "X".equals(code.substring(code.length() - 1)) )  {
    		code = code.substring(0, code.length() - 1);
    	}
    	result = code;
    	return result;
    }
    
}
 