package mmb.stock.cargo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.system.admin.AdminService;

import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.framework.IConstants;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class CargoDeptAreaService extends BaseServiceImpl {
	public CargoDeptAreaService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public CargoDeptAreaService() {
		this.useConnType = CONN_IN_SERVICE;
	}

	public boolean addCargoDeptAreaInfo(CargoDeptAreaBean bean) {
		return addXXX(bean, "cargo_dept_area");
	}

	public ArrayList<?> getCargoDeptAreaList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_dept_area", "mmb.stock.cargo.CargoDeptAreaBean");
	}

	public int getCargoDeptAreaCount(String condition) {
		return getXXXCount(condition, "cargo_dept_area", "id");
	}

	public CargoDeptAreaBean getCargoDeptAreaInfo(String condition) {
		return (CargoDeptAreaBean) getXXX(condition, "cargo_dept_area", "mmb.stock.cargo.CargoDeptAreaBean");
	}

	public boolean updateCargoDeptAreaInfo(String set, String condition) {
		return updateXXX(set, condition, "cargo_dept_area");
	}

	public boolean deleteCargoDeptAreaInfo(String condition) {
		return deleteXXX(condition, "cargo_dept_area");
	}

	/**
	 * 判断是否有操作该地区和库类型的权限遮罩
	 * 
	 * @param request
	 * @param area
	 *            ，库地区
	 * @param stock_type
	 *            ，库类型，如果为-1则不需要判断
	 * @return，是否有此操作权限
	 */
	public static boolean hasCargoDeptArea(HttpServletRequest request, int area, int stock_type) {
		voUser vo = (voUser) request.getSession().getAttribute("userView");
		CargoStaffBean cargoStaffBean = vo.getCargoStaffBean();
		try{
			if (cargoStaffBean != null) {
				List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
				if (stock_type != -1) {
					String code = area + "-" + stock_type;
					if (cargoDeptAreaList.contains(code)) {
						return true;
					} else {
						return false;
					}
				} else {
					for (int i = 0; i < cargoDeptAreaList.size(); i++) {
						String code = (String) cargoDeptAreaList.get(i);
						String sessionArea = code.split("-")[0];
						if (StringUtil.toInt(sessionArea) == area) {
							return true;
						}
					}
	
				}
			} else {//session中没有cargoStaff的记录，重新查询一遍并放入session
				AdminService adminService = new AdminService();
				try{
					//重新查询物流员工及其相关库地区和库类型
					cargoStaffBean = adminService.getCargoStaff(adminService.getDbOp(), vo);
					if (cargoStaffBean != null) {
						vo.setCargoStaffBean(cargoStaffBean);
						request.getSession().setAttribute(IConstants.USER_VIEW_KEY, vo);
						List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
						if (stock_type != -1) {
							String code = area + "-" + stock_type;
							if (cargoDeptAreaList.contains(code)) {
								return true;
							} else {
								return false;
							}
						} else {
							for (int i = 0; i < cargoDeptAreaList.size(); i++) {
								String code = (String) cargoDeptAreaList.get(i);
								String sessionArea = code.split("-")[0];
								if (StringUtil.toInt(sessionArea) == area) {
									return true;
								}
							}
						}
					}else{//不是物流员工
						return false;
					}
				}catch (Exception e) {
					return false;
				}finally{
					adminService.releaseAll();
				}
			}
		}
		catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 获得登陆账号可以操作的库地区列表
	 * 
	 * @param request
	 * @return 库地区列表，其中元素为代表地区的数字
	 */
	public static List<String> getCargoDeptAreaList(HttpServletRequest request) {
		List<String> areaList = new ArrayList<String>();
		voUser vo = (voUser) request.getSession().getAttribute("userView");
		if(vo==null){
			return areaList;
		}
		CargoStaffBean cargoStaffBean = vo.getCargoStaffBean();
		try{
			if (cargoStaffBean != null) {
				List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
				for (int i = 0; i < cargoDeptAreaList.size(); i++) {
					String code = (String) cargoDeptAreaList.get(i);
					String sessionArea = code.split("-")[0];
					if(!areaList.contains(sessionArea)){
						areaList.add(sessionArea);
					}
				}
			}else{
				AdminService adminService = new AdminService();
				try{
					//重新查询物流员工及其相关库地区和库类型
					cargoStaffBean = adminService.getCargoStaff(adminService.getDbOp(), vo);
					if (cargoStaffBean != null) {
						vo.setCargoStaffBean(cargoStaffBean);
						request.getSession().setAttribute(IConstants.USER_VIEW_KEY, vo);
						
						List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
						for (int i = 0; i < cargoDeptAreaList.size(); i++) {
							String code = (String) cargoDeptAreaList.get(i);
							String sessionArea = code.split("-")[0];
							if(!areaList.contains(sessionArea)){
								areaList.add(sessionArea);
							}
						}
						
					}
				}catch (Exception e) {
					return areaList;
				}finally{
					adminService.releaseAll();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return areaList;
	}

	/**
	 * 获得登陆账号在某区域下可以操作的库类型列表
	 * 
	 * @param request
	 * @param area
	 *            库地区
	 * @return 库类型列表，其中元素为代表库类型的数字
	 */
	public static List<String> getCargoDeptStockTypeList(HttpServletRequest request, int area) {

		voUser vo = (voUser) request.getSession().getAttribute("userView");
		CargoStaffBean cargoStaffBean = vo.getCargoStaffBean();
		List<String> stockTypeList = new ArrayList<String>();
		try{
			if (cargoStaffBean != null) {
				List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
				for (int i = 0; i < cargoDeptAreaList.size(); i++) {
					String code = (String) cargoDeptAreaList.get(i);
					if(StringUtil.toInt(code.split("-")[0])==area){
						String sessionStockType = code.split("-")[1];
						stockTypeList.add(sessionStockType);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return stockTypeList;
	}
	
	public static List<String> getCargoDeptStockTypeList(HttpServletRequest request) {

		voUser vo = (voUser) request.getSession().getAttribute("userView");
		CargoStaffBean cargoStaffBean = vo.getCargoStaffBean();
		List<String> stockTypeList = new ArrayList<String>();
		try{
			if (cargoStaffBean != null) {
				List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
				for (int i = 0; i < cargoDeptAreaList.size(); i++) {
					String code = (String) cargoDeptAreaList.get(i);
					String sessionStockType = code.split("-")[1];
					if (!stockTypeList.contains(sessionStockType))
						stockTypeList.add(sessionStockType);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return stockTypeList;
	}
	
	public static List<String> getCargoDeptAreaList(HttpServletRequest request, int stockType) {
		List<String> areaList = new ArrayList<String>();
		voUser vo = (voUser) request.getSession().getAttribute("userView");
		if(vo==null){
			return areaList;
		}
		CargoStaffBean cargoStaffBean = vo.getCargoStaffBean();
		try{
			if (cargoStaffBean != null) {
				List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
				for (int i = 0; i < cargoDeptAreaList.size(); i++) {
					String code = (String) cargoDeptAreaList.get(i);
					String sessionArea = code.split("-")[0];
					if (code.split("-")[1].equals(stockType + ""))
						if(!areaList.contains(sessionArea)){
							areaList.add(sessionArea);
						}
				}
			}else{
				AdminService adminService = new AdminService();
				try{
					//重新查询物流员工及其相关库地区和库类型
					cargoStaffBean = adminService.getCargoStaff(adminService.getDbOp(), vo);
					if (cargoStaffBean != null) {
						vo.setCargoStaffBean(cargoStaffBean);
						request.getSession().setAttribute(IConstants.USER_VIEW_KEY, vo);
						
						List<?> cargoDeptAreaList = cargoStaffBean.getCargoDeptAreaList();
						for (int i = 0; i < cargoDeptAreaList.size(); i++) {
							String code = (String) cargoDeptAreaList.get(i);
							String sessionArea = code.split("-")[0];
							if (code.split("-")[1].equals(stockType + ""))
								if(!areaList.contains(sessionArea)){
									areaList.add(sessionArea);
								}
						}
						
					}
				}catch (Exception e) {
					return areaList;
				}finally{
					adminService.releaseAll();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return areaList;
	}
	
	/**
	 * 判断给定的AreaList 里是否含有stockAreaId
	 * @param areaList
	 * @param stockAreaId
	 * @return
	 */
	public static boolean hasStockAreaRight(List<String> areaList, int stockAreaId) {
		if( areaList == null ) {
			return false;
		} else {
			int x = areaList.size();
			if( x == 0 ) {
				return false;
			} else {
				String stockAreaIdS = Integer.toString(stockAreaId);
				for ( int i = 0 ;i < x; i++ ) {
					String temp = areaList.get(i);
					if( stockAreaIdS.equals(temp) ) {
						return true;
					}
				}
				return false;
			}
		}
	}
}
