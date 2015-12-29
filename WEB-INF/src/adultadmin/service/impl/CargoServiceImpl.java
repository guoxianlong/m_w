package adultadmin.service.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmb.cargo.model.ReturnedProductVirtual;
import mmb.rec.stat.bean.StockShareBean;
import mmb.util.LogUtil;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ormap.MapField;
import ormap.Mapping;
import ormap.OrMap;
import adultadmin.action.cargo.CargoUtils;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoDownShelfBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoCityBean;
import adultadmin.bean.cargo.CargoInfoLogBean;
import adultadmin.bean.cargo.CargoInfoPassageBean;
import adultadmin.bean.cargo.CargoInfoShelfBean;
import adultadmin.bean.cargo.CargoInfoStockAreaBean;
import adultadmin.bean.cargo.CargoInfoStorageBean;
import adultadmin.bean.cargo.CargoInventoryBean;
import adultadmin.bean.cargo.CargoInventoryLogBean;
import adultadmin.bean.cargo.CargoInventoryMissionBean;
import adultadmin.bean.cargo.CargoInventoryMissionProductBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


public class CargoServiceImpl extends BaseServiceImpl implements ICargoService {
	
	private static byte[] productStockLock = new byte[0];
	public Log stockUpdateLog = LogFactory.getLog("stockUpdate.Log");

	public CargoServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public CargoServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public boolean addCargoInfoArea(CargoInfoAreaBean bean) {
		
		return addXXX(bean,"cargo_info_area");
	}

	public boolean addCargoInfoCity(CargoInfoCityBean bean) {
		
		return addXXX(bean,"cargo_info_city");
	}

	public boolean addCargoInfoStorage(CargoInfoStorageBean bean) {
		
		return addXXX(bean,"cargo_info_storage");
	}

	public boolean deleteCargoInfoArea(String condition) {
		
		return deleteXXX(condition,"cargo_info_area");
	}

	public boolean deleteCargoInfoCity(String condition) {
		
		return deleteXXX(condition,"cargo_info_city");
	}

	public boolean deleteCargoInfoStorage(String condition) {
		
		return deleteXXX(condition,"cargo_info_storage");
	}

	public CargoInfoAreaBean getCargoInfoArea(String condition) {
		
		return (CargoInfoAreaBean)getXXX(condition,"cargo_info_area","adultadmin.bean.cargo.CargoInfoAreaBean");
	}

	public int getCargoInfoAreaCount(String condition) {
		
		return getXXXCount(condition, "cargo_info_area", "id");
	}

	public ArrayList getCargoInfoAreaList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info_area",
		"adultadmin.bean.cargo.CargoInfoAreaBean");
	}

	public CargoInfoCityBean getCargoInfoCity(String condition) {
		
		return (CargoInfoCityBean)getXXX(condition,"cargo_info_city","adultadmin.bean.cargo.CargoInfoCityBean");
	}

	public int getCargoInfoCityCount(String condition) {
		
		return getXXXCount(condition, "cargo_info_city", "id");
	}

	public ArrayList getCargoInfoCityList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info_city",
		"adultadmin.bean.cargo.CargoInfoCityBean");
	}

	public CargoInfoStorageBean getCargoInfoStorage(String condition) {
		
		return (CargoInfoStorageBean)getXXX(condition,"cargo_info_storage","adultadmin.bean.cargo.CargoInfoStorageBean");
	}

	public int getCargoInfoStorageCount(String condition) {
		
		return getXXXCount(condition, "cargo_info_storage", "id");
	}

	public ArrayList getCargoInfoStorageList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info_storage",
		"adultadmin.bean.cargo.CargoInfoStorageBean");
	}

	public boolean updateCargoInfoArea(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info_area");
	}

	public boolean updateCargoInfoCity(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info_city");
	}

	public boolean updateCargoInfoStorage(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info_storage");
	}

	public boolean addCargoInfoStockArea(CargoInfoStockAreaBean bean) {
		
		return addXXX(bean,"cargo_info_stock_area");
	}

	public boolean deleteCargoInfoStockArea(String condition) {
		
		return deleteXXX(condition,"cargo_info_stock_area");
	}

	public CargoInfoStockAreaBean getCargoInfoStockArea(String condition) {
		
		return (CargoInfoStockAreaBean)getXXX(condition,"cargo_info_stock_area","adultadmin.bean.cargo.CargoInfoStockAreaBean");
	}

	public int getCargoInfoStockAreaCount(String condition) {
		
		return getXXXCount(condition, "cargo_info_stock_area", "id");
	}

	public ArrayList getCargoInfoStockAreaList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info_stock_area",
		"adultadmin.bean.cargo.CargoInfoStockAreaBean");
	}

	public boolean updateCargoInfoStockArea(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info_stock_area");
	}

	public boolean addCargoInfoShelf(CargoInfoShelfBean bean) {
		
		return addXXX(bean,"cargo_info_shelf");
	}

	public boolean deleteCargoInfoShelf(String condition) {
		
		return deleteXXX(condition,"cargo_info_shelf");
	}

	public CargoInfoShelfBean getCargoInfoShelf(String condition) {
		
		return (CargoInfoShelfBean)getXXX(condition,"cargo_info_shelf","adultadmin.bean.cargo.CargoInfoShelfBean");
	}

	public int getCargoInfoShelfCount(String condition) {
		
		return getXXXCount(condition, "cargo_info_shelf", "id");
	}

	public ArrayList getCargoInfoShelfList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info_shelf",
		"adultadmin.bean.cargo.CargoInfoShelfBean");
	}

	public boolean updateCargoInfoShelf(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info_shelf");
	}

	public boolean addCargoInfo(CargoInfoBean bean) {
		
		return addXXX(bean,"cargo_info");
	}

	public boolean addCargoOperation(CargoOperationBean bean) {
		
		return addXXX(bean,"cargo_operation");
	}

	public boolean addCargoProductStock(CargoProductStockBean bean) {
		
		return addXXX(bean,"cargo_product_stock");
	}

	public boolean deleteCargoInfo(String condition) {
		
		return deleteXXX(condition,"cargo_info");
	}

	public boolean deleteCargoOperation(String condition) {
		
		return deleteXXX(condition,"cargo_operation");
	}

	public boolean deleteCargoProductStock(String condition) {
		
		return deleteXXX(condition,"cargo_product_stock");
	}

	public CargoInfoBean getCargoInfo(String condition) {
		
		return (CargoInfoBean)getXXX(condition,"cargo_info","adultadmin.bean.cargo.CargoInfoBean");
	}

	public int getCargoInfoCount(String condition) {
		
		return getXXXCount(condition, "cargo_info", "id");
	}

	public ArrayList getCargoInfoList(String condition, int index, int count,
			String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info",
		"adultadmin.bean.cargo.CargoInfoBean");
	}

	public CargoOperationBean getCargoOperation(String condition) {
		
		return (CargoOperationBean)getXXX(condition,"cargo_operation","adultadmin.bean.cargo.CargoOperationBean");
	}

	public int getCargoOperationCount(String condition) {
		
		return getXXXCount(condition, "cargo_operation", "id");
	}

	public ArrayList getCargoOperationList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_operation",
		"adultadmin.bean.cargo.CargoOperationBean");
	}

	public CargoProductStockBean getCargoProductStock(String condition) {
		
		return (CargoProductStockBean)getXXX(condition,"cargo_product_stock","adultadmin.bean.cargo.CargoProductStockBean");
	}

	public int getCargoProductStockCount(String condition) {
		
		return getXXXCount(condition, "cargo_product_stock", "id");
	}

	public ArrayList getCargoProductStockList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_product_stock",
		"adultadmin.bean.cargo.CargoProductStockBean");
	}

	public boolean updateCargoInfo(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info");
	}

	public boolean updateCargoOperation(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_operation");
	}

	public boolean updateCargoProductStock(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_product_stock");
	}

	public boolean addCargoOperationCargo(CargoOperationCargoBean bean) {
		
		return addXXX(bean,"cargo_operation_cargo");
	}

	public boolean deleteCargoOperationCargo(String condition) {
		
		return deleteXXX(condition,"cargo_operation_cargo");
	}

	public CargoOperationCargoBean getCargoOperationCargo(String condition) {
		
		return (CargoOperationCargoBean)getXXX(condition,"cargo_operation_cargo","adultadmin.bean.cargo.CargoOperationCargoBean");
	}

	public int getCargoOperationCargoCount(String condition) {
		
		return getXXXCount(condition, "cargo_operation_cargo", "id");
	}

	public ArrayList getCargoOperationCargoList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_operation_cargo",
		"adultadmin.bean.cargo.CargoOperationCargoBean");
	}

	public boolean updateCargoOperationCargo(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_operation_cargo");
	}

	public boolean addCargoOperationLog(CargoOperationLogBean bean) {
		
		return addXXX(bean,"cargo_operation_log");
	}

	public boolean deleteCargoOperationLog(String condition) {
		
		return deleteXXX(condition,"cargo_operation_log");
	}

	public CargoOperationLogBean getCargoOperationLog(String condition) {
		
		return (CargoOperationLogBean)getXXX(condition,"cargo_operation_log","adultadmin.bean.cargo.CargoOperationLogBean");
	}

	public int getCargoOperationLogCount(String condition) {
		
		return getXXXCount(condition, "cargo_operation_log", "id");
	}

	public ArrayList getCargoOperationLogList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_operation_log",
		"adultadmin.bean.cargo.CargoOperationLogBean");
	}

	public boolean updateCargoOperationLog(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_operation_log");
	}
	
	public boolean addCargoInfoLog(CargoInfoLogBean bean) {
		
		return addXXX(bean,"cargo_info_log");
	}

	public boolean deleteCargoInfoLog(String condition) {
		
		return deleteXXX(condition,"cargo_info_log");
	}

	public CargoInfoLogBean getCargoInfoLog(String condition) {
		
		return (CargoInfoLogBean)getXXX(condition,"cargo_info_log","adultadmin.bean.cargo.CargoInfoLogBean");
	}

	public int getCargoInfoLogCount(String condition) {
		
		return getXXXCount(condition, "cargo_info_log", "id");
	}

	public ArrayList getCargoInfoLogList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition, index, count, orderBy,
				"cargo_info_log",
		"adultadmin.bean.cargo.CargoInfoLogBean");
	}

	public boolean updateCargoInfoLog(String set, String condition) {
		
		return updateXXX(set, condition, "cargo_info_log");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addCargoStockCard(CargoStockCardBean bean) {
		return addXXX(bean, "cargo_stock_card");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteCargoStockCard(String condition) {
		return deleteXXX(condition, "cargo_stock_card");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public CargoStockCardBean getCargoStockCard(String condition) {
		return (CargoStockCardBean) getXXX(condition, "cargo_stock_card",
				"adultadmin.bean.stock.StockCardBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getCargoStockCardCount(String condition) {
		return getXXXCount(condition, "cargo_stock_card", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getCargoStockCardList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_stock_card",
				"adultadmin.bean.stock.CargoStockCardBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateCargoStockCard(String set, String condition) {
		return updateXXX(set, condition, "cargo_stock_card");

	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addCargoOperationProcess(CargoOperationProcessBean bean) {
		return addXXX(bean, "cargo_operation_process");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteCargoOperationProcess(String condition) {
		return deleteXXX(condition, "cargo_operation_process");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public CargoOperationProcessBean getCargoOperationProcess(String condition) {
		return (CargoOperationProcessBean) getXXX(condition, "cargo_operation_process",
				"adultadmin.bean.cargo.CargoOperationProcessBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getCargoOperationProcessCount(String condition) {
		return getXXXCount(condition, "cargo_operation_process", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getCargoOperationProcessList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_operation_process",
				"adultadmin.bean.cargo.CargoOperationProcessBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateCargoOperationProcess(String set, String condition) {
		return updateXXX(set, condition, "cargo_operation_process");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addCargoOperLog(CargoOperLogBean bean) {
		return addXXX(bean, "cargo_oper_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteCargoOperLog(String condition) {
		return deleteXXX(condition, "cargo_oper_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public CargoOperLogBean getCargoOperLog(String condition) {
		return (CargoOperLogBean) getXXX(condition, "cargo_oper_log",
				"adultadmin.bean.cargo.CargoOperLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getCargoOperLogCount(String condition) {
		return getXXXCount(condition, "cargo_oper_log", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getCargoOperLogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_oper_log",
				"adultadmin.bean.cargo.CargoOperLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateCargoOperLog(String set, String condition) {
		return updateXXX(set, condition, "cargo_oper_log");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addCargoDept(CargoDeptBean bean) {
		return addXXX(bean, "cargo_dept");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteCargoDept(String condition) {
		return deleteXXX(condition, "cargo_dept");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public CargoDeptBean getCargoDept(String condition) {
		return (CargoDeptBean) getXXX(condition, "cargo_dept",
				"adultadmin.bean.cargo.CargoDeptBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getCargoDeptCount(String condition) {
		return getXXXCount(condition, "cargo_dept", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getCargoDeptList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_dept",
				"adultadmin.bean.cargo.CargoDeptBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateCargoDept(String set, String condition) {
		return updateXXX(set, condition, "cargo_dept");
	}
	
	/**
	 * 得到可以下架的散货区的商品 列表 
	 * @param query
	 * @return
	 * @throws Exception 
	 */
	public ArrayList getDownShelfList(String query,int index, int count) throws SQLException{
		ArrayList list = new ArrayList();
		DbOperation dbOp = super.getDbOp();
		ResultSet rs=null;
		query = DbOperation.getPagingQuery(query, index, count);
		try{
			rs=dbOp.executeQuery(query);
			while(rs.next()){
				CargoDownShelfBean bean = new CargoDownShelfBean();
				bean.setId(rs.getInt("ca.id"));
				bean.setCargoId(rs.getInt("ca.cargo_id"));
				bean.setCargoCode(rs.getString("info.whole_code"));
				bean.setProductId(rs.getInt("ca.product_id"));
				bean.setProductCode(rs.getString("pr.code"));
				bean.setStockCount(rs.getInt("ca.stock_count"));
				bean.setStockCountLock(rs.getInt("ca.stock_lock_count"));
				bean.setMaxStockCount(rs.getInt("info.max_stock_count"));
				bean.setWarStockCount(rs.getInt("info.warn_stock_count"));
				bean.setCargoType(rs.getInt("info.type"));
				bean.setVolume(rs.getInt("info.length")*rs.getInt("info.width")*rs.getInt("info.high"));
				bean.setProductName(rs.getString("pr.name"));
				bean.setCargoMark(rs.getString("info.remark"));
				list.add(bean);
			} 
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException("数据库错误"+e.getMessage());
		}finally{
			dbOp.release();
			rs.close();
		}
		return list;
	}
	
	public int getTablesCount(String query){
		if (query == null) {
            return 0;
        }
        int count = 0;
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return count;
        }
        ResultSet rs = null;
        //执行查询
        rs = dbOp.executeQuery(query);
        if (rs == null) {
            release(dbOp);
            return count;
        }
        try {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	release(dbOp);
        	try {
				if(rs!=null)rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        //释放数据库连接
        
        return count;
	}
	
	/**
	 * @param header 作业单头标志HWX  下架单  HWD  货位调拨HWB   货位上架HWS  1103120001 2011-03-12 0001单
	 * 得到作业单 的最后的编码 并生成新的作业单编号
	 * @return
	 */
	public synchronized  String getCargoOperationMaxIdCode(String header){
		String dateStr=DateUtil.getNowDateStr();
		dateStr=header+dateStr.substring(2, 4)+dateStr.substring(5,7)+dateStr.substring(8,10);
		String code ="";
		DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return code;
        }
        ResultSet rs = null;
        //执行查询
        rs = dbOp.executeQuery("select code code from cargo_operation where code like '"+dateStr+"%' order by id desc limit 1 ");
        if (rs == null) {
            release(dbOp);
            return code;
        }
        try {
            if (rs.next()) {
            	code = rs.getString("code");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
        	release(dbOp);
        	try {
				if(rs!=null)rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        
		return CargoUtils.getOperationID(header ,code);
	}
	
 
	public synchronized int getTablesMaxId(String table){
		return getNumber("id", table, "max", "1=1");
	}
 
	/**
	 * @param query 得到已确认上架的商品的数量（已上架数量） 或得到冻结量 上架的商品的数量 文档中：（其中冻结数量）
	 * @return  将list 转变成 productId  cargo_operation_cargo Bean 的键值对应形式。方便判断冻结量或以上架量
	 */
	public Map getCoCNumOrLockNum(String query){
		ArrayList operList = getCargoOperationList(query, 0, -1, "id asc"); //得到作业表中 对应的调拨单编号 列表
		ArrayList cargoOperCargos = new ArrayList();
		Iterator operIter = operList.listIterator();
		while(operIter.hasNext()){ //循环作业单列表 得到队友的 cargo_operaton_cargo 中的商品列表
			CargoOperationBean operBean = (CargoOperationBean)operIter.next();
			ArrayList cargoOperCargoTemp = getCargoOperationCargoList("type=0 and oper_id="+operBean.getId(), 0, -1, "id asc");
			if(cargoOperCargoTemp!=null && cargoOperCargoTemp.size()>0){
				cargoOperCargos.add(cargoOperCargoTemp);//将lis 添加到一个新的list 中
			}
		}
		//将list 转变成 productId  cargo_operation_cargo Bean 的键值对应形式。方便判断冻结量或以上架量
		return CargoUtils.changeOperationList(cargoOperCargos);
	}
	
	/**
	 * 添加下架作业单
	 */
	public boolean addCargoOperationDownShelf(ArrayList list,CargoOperationBean bean){
		DbOperation dbOp = getDbOp();
		dbOp.startTransaction();
		boolean flag=true;
		flag=addCargoOperation(bean);
		if(flag){
			int cargoOperationId= getTablesMaxId("cargo_operation");
			for(int i=0;i<list.size();i++){
				CargoProductStockBean cargoProductStockBean = (CargoProductStockBean)list.get(i);
				CargoOperationCargoBean cargoOperationCargoBean = new CargoOperationCargoBean();
				cargoOperationCargoBean.setOutCargoProductStockId(cargoProductStockBean.getCargoId());
				cargoOperationCargoBean.setOperId(cargoOperationId);
				cargoOperationCargoBean.setProductId(cargoProductStockBean.getProductId());
				cargoOperationCargoBean.setStockCount(cargoProductStockBean.getStockCount()-cargoProductStockBean.getStockLockCount());
				cargoOperationCargoBean.setOutCargoProductStockId(cargoProductStockBean.getId()); //添加源货位id cargoProductStock 表中
				CargoInfoBean cargoInfoBean = getCargoInfo("id="+cargoProductStockBean.getCargoId());//从数据库中得到 货位编号
				cargoOperationCargoBean.setOutCargoWholeCode(cargoInfoBean.getWholeCode());
				cargoOperationCargoBean.setType(0);
				boolean flag_sub= addCargoOperationCargo(cargoOperationCargoBean);
				if(!flag_sub){
					dbOp.rollbackTransaction();
					flag=false;
					break;
				}
			}
			dbOp.commitTransaction();
		}
		dbOp.release();
		return flag;
	}
	
	/**
	 * 添加上架作业单
	 */
	public boolean addCargoOperationUpShelf(ArrayList list,CargoOperationBean bean,String  whloleCode ,int cargoId){
		String conditionCargo="status=6 and type=0  and source='"+bean.getSource()+"'";
		String conditionCargoLock="status=2 or status=3 and type=0 and source='"+bean.getSource()+"'"; //得到上架的商品冻结的数量 
		Map cargoOperCarogMap = getCoCNumOrLockNum(conditionCargo); 
		Map cargoOperLockMap= getCoCNumOrLockNum(conditionCargoLock);
		boolean flag=true;
		synchronized(productStockLock){
			DbOperation dbOp = getDbOp();
			dbOp.startTransaction();
			flag=addCargoOperation(bean);
			if(flag){
				int cargoOperationId= getTablesMaxId("cargo_operation");
				bean.setId(cargoOperationId);
				for(int i=0;i<list.size();i++){
					StockExchangeProductBean stockProductBean = (StockExchangeProductBean)list.get(i);
					CargoOperationCargoBean cargoOperationCargoBean = new CargoOperationCargoBean();
					CargoProductStockBean cpsBean = new CargoProductStockBean();
					int productIdTemp = stockProductBean.getProductId();
					int stockCount=0;
					//cargoOperationCargoBean.setOutCargoProductStockId(cargoProductStockBean.getCargoId());
					if(cargoOperCarogMap.containsKey(String.valueOf(productIdTemp))){//获取已经上架数量
						CargoOperationCargoBean beanTemp= (CargoOperationCargoBean)cargoOperCarogMap.get(String.valueOf(productIdTemp));
						stockCount=stockProductBean.getStockOutCount()-beanTemp.getStockCount();
						cpsBean.setStockCount(stockCount);
					}else{
						cpsBean.setStockCount(stockProductBean.getStockOutCount());
					}
					if(cargoOperLockMap.containsKey(String.valueOf(productIdTemp))){//获取锁定的数量
						CargoOperationCargoBean beanTemp= (CargoOperationCargoBean)cargoOperLockMap.get(String.valueOf(productIdTemp));
						cpsBean.setStockLockCount(beanTemp.getStockCount());
					}else{
						cpsBean.setStockLockCount(0);
					}
					cpsBean.setCargoId(cargoId);
					cpsBean.setProductId(stockProductBean.getProductId());
					boolean flagCps = addCargoProductStock(cpsBean);
					if(!flagCps){
						dbOp.rollbackTransaction();
						flag=false;
						break;
					}
					int intCps = getTablesMaxId("cargo_product_stock");
					cargoOperationCargoBean.setOperId(cargoOperationId);
					cargoOperationCargoBean.setProductId(stockProductBean.getProductId());
					cargoOperationCargoBean.setStockCount(stockCount);
					cargoOperationCargoBean.setOutCargoProductStockId(intCps); 
					cargoOperationCargoBean.setOutCargoWholeCode(whloleCode); //得到暂存区 合格库 的货位id 只能是唯一
					cargoOperationCargoBean.setType(0);
					boolean flag_sub= addCargoOperationCargo(cargoOperationCargoBean);
					
					if(!flag_sub){
						dbOp.rollbackTransaction();
						flag=false;
						break;
					}
				}
				if(flag) dbOp.commitTransaction(); //所有插入成功提交事务
			}
			dbOp.release();
		}
		return flag;
	}
	
	
	public ArrayList getCargoOperationCascade(String query, int index,int count){
		return getCargoInfoCascade(query, index,count,"cargo_operation","adultadmin.bean.cargo.CargoOperationBean");
	}
	
	public ArrayList getStockExchangeCascade(String query,int index,int count){
		return getCargoInfoCascade(query, index,count,"stock_exchange","adultadmin.bean.stock.StockExchangeBean");
	}
	
	/**
	 * 自己组织的级联的结构 得到一张表的数据 
	 * @param query //组织好的sql
	 * @param index //开始
	 * @param count //查询数量
	 * @param table 表名
	 * @param className orpmap 类名
	 * @return
	 */
	public ArrayList getCargoInfoCascade(String query, int index,
			int count,String table,String className) {
		// TODO Auto-generated method stub
//		return getXXXList(condition, index, count, orderBy,
//				"cargo_operation",
//		"adultadmin.bean.cargo.CargoOperationBean");
		ArrayList resultList = new ArrayList();
        Object result = null;
        //取得or mapping
        Mapping mapping = OrMap.getMapping(table);
        if (mapping == null) {
            return resultList;
        }
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return resultList;
        }
        ResultSet rs = null;
        query = DbOperation.getPagingQuery(query, index, count);
        rs = dbOp.executeQuery(query); //执行查询
        if (rs == null) {
            release(dbOp);
            return null;
        }
        try {
            //把结果集封装
            Class c = Class.forName(className);
            int fieldsCount = mapping.getFields().size();
            ArrayList fields = mapping.getFields();
            MapField mapField = null;
            Field field = null;
            int i;
            String objField;
            String tableField;
            while (rs.next()) {
                result = c.newInstance();
                for (i = 0; i < fieldsCount; i++) {
                    mapField = (MapField) fields.get(i);
                    objField = mapField.getObjField();
                    tableField = mapField.getTableField();
                    field = c.getField(objField);
                    if (field == null) {
                        release(dbOp);
                        return resultList;
                    }
                    if ("int".equals(mapField.getObjType())) {//整数
                        field.setInt(result, rs.getInt(tableField));
                    }
                    else if ("float".equals(mapField.getObjType())) {
                        field.setFloat(result, rs.getFloat(tableField));
                    }
                    else if ("double".equals(mapField.getObjType())) {//double
                        field.setDouble(result, rs.getDouble(tableField));
                    }
                    else if ("String".equals(mapField.getObjType())) {//字符串
                        field.set(result, rs.getString(tableField));
                    }
                    else if ("Timestamp".equals(mapField.getObjType())) { //Timestamp
                        field.set(result, rs.getTimestamp(tableField));
                    }
                    else if ("long".equals(mapField.getObjType())) { //long
                        field.setLong(result, rs.getLong(tableField));
                    }
                    else {//其他
                        field.set(result, rs.getObject(tableField));
                    }
                }
                resultList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return resultList;
        }finally
        {//释放数据库连接
        	release(dbOp);
        }

        return resultList;
	}
	
	/* 获取货位产品库存详细信息列表，主要用于库存查询
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * 
	 */
	public ArrayList getCargoAndProductStockList(String condition, int index,int count, String orderBy){

		ArrayList list = new ArrayList();
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return list;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select * from cargo_info ci left join cargo_product_stock cps on ci.id = cps.cargo_id";
        if (condition != null) {
            query += " where " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }
        
		try {
			while(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));

				CargoProductStockBean cps = new CargoProductStockBean();
				cps.setId(rs.getInt("cps.id"));
				cps.setCargoId(rs.getInt("cps.cargo_id"));
				cps.setProductId(rs.getInt("cps.product_id"));
				cps.setStockCount(rs.getInt("cps.stock_count"));
				cps.setStockLockCount(rs.getInt("cps.stock_lock_count"));
				cps.setCargoInfo(ci);

				list.add(cps);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * 作者 郝亚斌
	 * 针对快销商品，增加了联查cargo_info_stock_area表的 获取货位产品库存详细信息列表，主要用于库存查询
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public ArrayList getCargoAndProductStockWithStockAreaCodeRestrictList(String condition, int index,int count, String orderBy, String stockAreaCode){

		ArrayList list = new ArrayList();
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return list;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select ci.*,cps.* from cargo_info ci,cargo_product_stock cps, cargo_info_stock_area cisa where ci.id = cps.cargo_id and ci.stock_area_id = cisa.id and cisa.code='"+stockAreaCode+"'";
        if (condition != null) {
            query += " and " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }
        
		try {
			while(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));

				CargoProductStockBean cps = new CargoProductStockBean();
				cps.setId(rs.getInt("cps.id"));
				cps.setCargoId(rs.getInt("cps.cargo_id"));
				cps.setProductId(rs.getInt("cps.product_id"));
				cps.setStockCount(rs.getInt("cps.stock_count"));
				cps.setStockLockCount(rs.getInt("cps.stock_lock_count"));
				cps.setCargoInfo(ci);

				list.add(cps);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			release(dbOp);
		}

		return list;
	}
	
	/**
	 * 作者 郝亚斌
	 * 针对快销商品，增加了联查cargo_info_stock_area表的 获取货位产品库存详细信息列表，主要用于库存查询
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public ArrayList getCargoInfoWithStockAreaCodeAndCPSRestrictList(String condition, int index,int count, String orderBy, String stockAreaCode){

		ArrayList list = new ArrayList();
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return list;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select ci.* from cargo_info ci,cargo_product_stock cps, cargo_info_stock_area cisa where ci.id = cps.cargo_id and ci.stock_area_id = cisa.id and cisa.code='"+stockAreaCode+"'";
        if (condition != null) {
            query += " and " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }
        
		try {
			while(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));
				list.add(ci);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			release(dbOp);
		}

		return list;
	}
	
	/**
	 * 作者 郝亚斌
	 * 在获取货位的时候加上了stockArea的限制条件的限制条件
	 * 查询时请使用别名 cargo_info —— ci  
	 * @param condition
	 * @return
	 */
	public CargoInfoBean getCargoInfoWithStockAreaCodeRestrict(String condition, String stockAreaCode ) {
		CargoInfoBean result = null;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		StringBuilder sql = new StringBuilder("select ci.* from cargo_info ci, cargo_info_stock_area cisa where ci.stock_area_id = cisa.id and cisa.code='");
		sql.append(stockAreaCode);
		sql.append("'");
		sql.append(" and ").append(condition);
		rs = dbOp.executeQuery(sql.toString());
		try {
			if (rs.next()) {
				result = new CargoInfoBean();
				result.setId(rs.getInt("ci.id"));
				result.setCode(rs.getString("ci.code"));
				result.setWholeCode(rs.getString("ci.whole_code"));
				result.setStoreType(rs.getInt("ci.stock_type"));
				result.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				result.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				result.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				result.setProductLineId(rs.getInt("ci.product_line_id"));
				result.setType(rs.getInt("ci.type"));
				result.setLength(rs.getInt("ci.length"));
				result.setWidth(rs.getInt("ci.width"));
				result.setHigh(rs.getInt("ci.high"));
				result.setStatus(rs.getInt("ci.status"));
				result.setPassageId(rs.getInt("ci.passage_id"));
				result.setShelfId(rs.getInt("ci.shelf_id"));
				result.setStockAreaId(rs.getInt("ci.stock_area_id"));
				result.setStorageId(rs.getInt("ci.storage_id"));
				result.setAreaId(rs.getInt("ci.area_id"));
				result.setCityId(rs.getInt("ci.city_id"));
				result.setRemark(rs.getString("ci.remark"));
				result.setFloorNum(rs.getInt("ci.floor_num"));
				result.setStockType(rs.getInt("ci.stock_type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-11
	 * 
	 * 说明：获取货位产品库存详细信息列表，主要用于库存查询
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * 
	 */
	public ArrayList getCargoInfoBeanist(String condition, int index,int count, String orderBy){

		ArrayList list = new ArrayList();
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return list;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select * from cargo_info ci left join cargo_product_stock cps on ci.id = cps.cargo_id";
        if (condition != null) {
            query += " where " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }
        
		try {
			while(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));
				list.add(ci);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
	
	/* 获取货位产品库存详细信息列表，主要用于库存查询
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * 
	 */
	public CargoProductStockBean getCargoAndProductStock(String condition){

		CargoProductStockBean cps = null;
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return cps;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select * from cargo_info ci left join cargo_product_stock cps on ci.id = cps.cargo_id";
        if (condition != null) {
            query += " where " + condition;
        }

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }
        
		try {
			if(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));

				cps = new CargoProductStockBean();
				cps.setId(rs.getInt("cps.id"));
				cps.setCargoId(rs.getInt("cps.cargo_id"));
				cps.setProductId(rs.getInt("cps.product_id"));
				cps.setStockCount(rs.getInt("cps.stock_count"));
				cps.setStockLockCount(rs.getInt("cps.stock_lock_count"));
				cps.setCargoInfo(ci);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cps;
	}
	
	/* 获取货位产品库存详细信息列表，主要用于库存查询
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * 
	 */
	public int getCargoAndProductStockCount(String condition){

		int count = 0;
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return count;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select count(cps.id) c from cargo_info ci join cargo_product_stock cps on ci.id = cps.cargo_id";
        if (condition != null) {
            query += " where " + condition;
        }

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return count;
        }
        
		try {
			if(rs.next()){
				count = rs.getInt("c");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return count;
	}
	
	public boolean updateCargoProductStockCount(int id, int mcount) {
		boolean result = false;

		String startTime = DateUtil.getNow();
		String log = "update cargo_product_stock set stock_count=(stock_count + "+mcount+") where id = "+id+" and stock_count >= "+mcount+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();

		DbOperation dbOp = this.getDbOp();
		String update = "update cargo_product_stock set stock_count=(stock_count + ?) where id = ? and stock_count >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);

			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新货位库存语句为："+log+"，调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.CargoServiceImpl"));
		}
		return result;
	}
	
	public boolean updateCargoProductStockLockCount(int id, int mcount) {
		boolean result = false;

		String startTime = DateUtil.getNow();
		String log = "update cargo_product_stock set stock_lock_count=(stock_lock_count + "+mcount+") where id = "+id+" and stock_lock_count >= "+mcount+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();

		DbOperation dbOp = this.getDbOp();
		String update = "update cargo_product_stock set stock_lock_count=(stock_lock_count + ?) where id = ? and stock_lock_count >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);

			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新货位库存锁定量语句为："+log+"，调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.CargoServiceImpl"));
		}
		return result;
	}
	
	public boolean updateCargoSpaceLockCount(int id, int mcount) {
		boolean result = false;
		DbOperation dbOp = this.getDbOp();
		String startTime = DateUtil.getNow();
		String log = "update cargo_info set space_lock_count=(space_lock_count"+mcount+" where id = "+id+" and space_lock_count >= "+(-mcount)+"    "+startTime+" execute :";
		long start = System.currentTimeMillis();
		String update = "update cargo_info set space_lock_count=(space_lock_count + ?) where id = ? and space_lock_count >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);
			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			if(stockUpdateLog.isInfoEnabled()){
				stockUpdateLog.info("更新语句为："+log+"，调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.CargoServiceImpl"));
			}
		}
		return result;
	}
	
	public boolean updateProductStockLockCount(int id, int mcount) {
		boolean result = false;
		DbOperation dbOp = this.getDbOp();
		String log = "update stock_exchange_product set up_cargo_lock_count=(up_cargo_lock_count"+mcount+" where id = "+id+" and up_cargo_lock_count >= "+(-mcount);
		String update = "update stock_exchange_product set up_cargo_lock_count=(up_cargo_lock_count + ?) where id = ? and up_cargo_lock_count >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);
			if(stockUpdateLog.isInfoEnabled()){
				stockUpdateLog.info("更新产品库存锁定量语句为："+log+",调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.CargoServiceImpl"));
			}
		}
		return result;
	}
	
	public boolean updateProductStockCount(int id, int mcount) {
		boolean result = false;
		DbOperation dbOp = this.getDbOp();
		String log = "update stock_exchange_product set no_up_cargo_count=(no_up_cargo_count"+mcount+" where id = "+id+" and no_up_cargo_count >= "+(-mcount);
		String update = "update stock_exchange_product set no_up_cargo_count=(no_up_cargo_count + ?) where id = ? and no_up_cargo_count >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);
			if(stockUpdateLog.isInfoEnabled()){
				stockUpdateLog.info("更新产品库存语句为："+log+",调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.CargoServiceImpl"));
			}
		}
		return result;
	}

	
	//合格库作业管理-员工管理
	public boolean addCargoStaff(CargoStaffBean bean) {
		return addXXX(bean, "cargo_staff");
	}

	public CargoStaffBean getCargoStaff(String condition) {
		return (CargoStaffBean) getXXX(condition, "cargo_staff", "adultadmin.bean.cargo.CargoStaffBean");
	}

	public int getCargoStaffCount(String condition) {
		return getXXXCount(condition, "cargo_staff", "id");
	}

    public boolean updateCargoStaff(String set, String condition) {
		return updateXXX(set, condition, "cargo_staff");
	}

    public boolean deleteCargoStaff(String condition) {
		return deleteXXX(condition, "cargo_staff");
	}

    public ArrayList getCargoStaffList(String condition, int index,
            int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_staff", "adultadmin.bean.cargo.CargoStaffBean");
	}
    
    //盘点作业单
	public boolean addCargoInventory(CargoInventoryBean bean) {
		return addXXX(bean, "cargo_inventory");
	}

	public CargoInventoryBean getCargoInventory(String condition) {
		return (CargoInventoryBean) getXXX(condition, "cargo_inventory", "adultadmin.bean.cargo.CargoInventoryBean");
	}

	public int getCargoInventoryCount(String condition) {
		return getXXXCount(condition, "cargo_inventory", "id");
	}

    public boolean updateCargoInventory(String set, String condition) {
		return updateXXX(set, condition, "cargo_inventory");
	}

    public boolean deleteCargoInventory(String condition) {
		return deleteXXX(condition, "cargo_inventory");
	}

    public ArrayList getCargoInventoryList(String condition, int index,
            int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_inventory", "adultadmin.bean.cargo.CargoInventoryBean");
	}
    
    //盘点作业单任务
	public boolean addCargoInventoryMission(CargoInventoryMissionBean bean) {
		return addXXX(bean, "cargo_inventory_mission");
	}

	public CargoInventoryMissionBean getCargoInventoryMission(String condition) {
		return (CargoInventoryMissionBean) getXXX(condition, "cargo_inventory_mission", "adultadmin.bean.cargo.CargoInventoryMissionBean");
	}

	public int getCargoInventoryMissionCount(String condition) {
		return getXXXCount(condition, "cargo_inventory_mission", "id");
	}

    public boolean updateCargoInventoryMission(String set, String condition) {
		return updateXXX(set, condition, "cargo_inventory_mission");
	}

    public boolean deleteCargoInventoryMission(String condition) {
		return deleteXXX(condition, "cargo_inventory_mission");
	}

    public ArrayList getCargoInventoryMissionList(String condition, int index,
            int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_inventory_mission", "adultadmin.bean.cargo.CargoInventoryMissionBean");
	}
    
  //盘点作业单任务商品库存
	public boolean addCargoInventoryMissionProduct(CargoInventoryMissionProductBean bean) {
		return addXXX(bean, "cargo_inventory_mission_product");
	}

	public CargoInventoryMissionProductBean getCargoInventoryMissionProduct(String condition) {
		return (CargoInventoryMissionProductBean) getXXX(condition, "cargo_inventory_mission_product", "adultadmin.bean.cargo.CargoInventoryMissionProductBean");
	}

	public int getCargoInventoryMissionProductCount(String condition) {
		return getXXXCount(condition, "cargo_inventory_mission_product", "id");
	}

    public boolean updateCargoInventoryMissionProduct(String set, String condition) {
		return updateXXX(set, condition, "cargo_inventory_mission_product");
	}

    public boolean deleteCargoInventoryMissionProduct(String condition) {
		return deleteXXX(condition, "cargo_inventory_mission_product");
	}

    public ArrayList getCargoInventoryMissionProductList(String condition, int index,
            int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_inventory_mission_product", "adultadmin.bean.cargo.CargoInventoryMissionProductBean");
	}
    
    //盘点作业单日志
	public boolean addCargoInventoryLog(CargoInventoryLogBean bean) {
		return addXXX(bean, "cargo_inventory_log");
	}

	public CargoInventoryLogBean getCargoInventoryLog(String condition) {
		return (CargoInventoryLogBean) getXXX(condition, "cargo_inventory_log", "adultadmin.bean.cargo.CargoInventoryLogBean");
	}

	public int getCargoInventoryLogCount(String condition) {
		return getXXXCount(condition, "cargo_inventory_log", "id");
	}

    public boolean updateCargoInventoryLog(String set, String condition) {
		return updateXXX(set, condition, "cargo_inventory_log");
	}

    public boolean deleteCargoInventoryLog(String condition) {
		return deleteXXX(condition, "cargo_inventory_log");
	}

    public ArrayList getCargoInventoryLogList(String condition, int index,
            int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_inventory_log", "adultadmin.bean.cargo.CargoInventoryLogBean");
	}
//	public int[] getProductStockCargoCount(int id){
//		int[] result = new int[2] ;
//		synchronized(productStockLock){
//			DbOperation dbOp = this.getDbOp();
//			String query = "select no_up_cargo_count, up_cargo_lock_count from  stock_exchange_product where id ="+id;
//			ResultSet rs =dbOp.executeQuery(query);
//			try{
//				 rs.next();
//				 result[0] = rs.getInt("no_up_cargo_count");
//				 result[1] = rs.getInt("up_cargo_lock_count");
//			} catch(Exception e){
//				e.printStackTrace();
//			} finally {
//				this.release(dbOp);
//			}
//		}
//		return result;
//	}

	public boolean addCargoInfoPassage(CargoInfoPassageBean bean) {
		return addXXX(bean, "cargo_info_passage");
	}

	public boolean deleteCargoInfoPassage(String condition) {
		return deleteXXX(condition, "cargo_info_passage");
	}

	public CargoInfoPassageBean getCargoInfoPassage(String condition) {
		return (CargoInfoPassageBean) getXXX(condition, "cargo_info_passage", "adultadmin.bean.cargo.CargoInfoPassageBean");
	}

	public int getCargoInfoPassageCount(String condition) {
		return getXXXCount(condition, "cargo_info_passage", "id");
	}

	public ArrayList getCargoInfoPassageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "cargo_info_passage", "adultadmin.bean.cargo.CargoInfoPassageBean");
	}

	public boolean updateCargoInfoPassage(String set, String condition) {
		return updateXXX(set, condition, "cargo_info_passage");
	}
	
	/*public synchronized CargoInfoBean assignProductTargetCargo(voProduct product) {
		
		
		IAdminService pService = ServiceFactory.createAdminService(this.dbOp);
		
		List cargoAndProductStockList = this.getCargoAndProductStockList(
				"ci.store_type=0 and ci.stock_type=0 and cps.product_id=" +product.getId()+" and ci.area_id=3", -1, -1, "stock_count DESC");//相同sku 库存最多的货位
		if(cargoAndProductStockList.size() > 0){
			return ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo();
		}else{
			voProductLine productLine = pService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
			if(productLine==null){
				System.out.println("result， 产品："+product.getId()+"产品线未知!");
				return null;
			}
			List cargoList = this.getCargoInfoList(
					"store_type=0 and product_line_id=" + productLine.getId()+" and area_id=3 and stock_type=0 and status=1", -1, -1, null);
			if(cargoList.size() > 0){
				CargoInfoBean icf = (CargoInfoBean)cargoList.get(0);
				this.updateCargoInfo("status=0", "id="+icf.getId());
				CargoProductStockBean cps = new CargoProductStockBean();
				cps.setCargoId(icf.getId());
				cps.setProductId(product.getId());
				cps.setStockCount(0);
				cps.setStockLockCount(0);
				this.addCargoProductStock(cps);
				return icf;
			}else{//没有对应产品线的货位，任意找一个，增城，合格库，散件区，未使用货位
				List newCargoList = this.getCargoInfoList("store_type=0 and area_id=3 and stock_type=0 and status=1", -1, -1, null);
				if(newCargoList.size() > 0){
					CargoInfoBean icf = (CargoInfoBean)newCargoList.get(0);
					this.updateCargoInfo("status=0", "id="+icf.getId());
					CargoProductStockBean cps = new CargoProductStockBean();
					cps.setCargoId(icf.getId());
					cps.setProductId(product.getId());
					cps.setStockCount(0);
					cps.setStockLockCount(0);
					this.addCargoProductStock(cps);
					return icf;
				}
				System.out.println("result,没有货位可用！");
				return null;
			}
		}
	}*/

	/**
	 * 退货上架分配货位的方法
	 */
	public CargoInfoBean getTargetCargoInfo(voProduct product, int number, int wareArea) throws Exception {
		
		if(product == null){
			throw new Exception("产品不存在");
		}
		WareService pService = new WareService(this.dbOp);
		ResultSet rs = null;
		CargoInfoAreaBean cargoArea = getCargoInfoArea("old_id = "+wareArea);
		try{
			CargoInfoBean coBean = null;
			//清理退货上架临时表
			this.cleanReturnedProductVirtual();
			//判断是否使用退货上架指向规则
			if(this.judgeConfigDirect(product, cargoArea)){
				//使用退货上架指向规则
				return this.getCargoInfoByConfig(product, cargoArea);
			}else{
				//未进行指向规则设置，则按照系统原有的退货上架指向逻辑寻找目的货位
				if( wareArea == ProductStockBean.AREA_ZC ) {
					List list = getListZC(product.getParent1().getName(), product.getCode());
					
					//根据产品线找出可选巷道ids
					String result = this.getMatchPassageIds("GZZ01", cargoArea.getId(), (List<HashMap<String, String>>)list.get(1), (String)list.get(0));
					if("".equals(result)){
						//表示 商品没有目的货位
						coBean=null;
						return coBean;
					}
					
					String sql = "select cps.product_id, ci.*,count(cps2.id) skucount from cargo_info ci, cargo_product_stock cps, cargo_product_stock cps2 where ci.id = cps.cargo_id and ci.area_id = "+ cargoArea.getId()
							+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 + " and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and cps.product_id = "
							+ product.getId() + " and " + result + " and ci.id=cps2.cargo_id group by ci.id order by (cps.stock_count + cps.stock_lock_count) desc,skucount asc limit 1";
					rs = dbOp.executeQuery(sql);
					coBean = getCargoInfoBean(rs);
					if( coBean == null ) {
						//代表它对应的区域里 没有这种商品的货位库存历史记录， 则应该找一个改区域中找sku种类小于三的货位，来存放这个商品。。。
						coBean = this.getOrtherSkuLessThanThreeCargo(cargoArea, result);
					}
					return coBean;
				}else if(wareArea ==ProductStockBean.AREA_CD){
					List list = getListCD(product.getParent1().getName(), product.getCode());
					
					//根据产品线找出可选巷道ids
					String result = this.getMatchPassageIds("SCC01", cargoArea.getId(), (List<HashMap<String, String>>)list.get(1), (String)list.get(0));
					if("".equals(result)){
						//表示 商品没有目的货位
						coBean=null;
						return coBean;
					}
					String sql = "select cps.product_id, ci.*,count(cps2.id) skucount from cargo_info ci, cargo_product_stock cps, cargo_product_stock cps2 where ci.id = cps.cargo_id and ci.area_id = "+ cargoArea.getId()
							+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 + " and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and cps.product_id = "
							+ product.getId() + " and " + result + " and ci.id=cps2.cargo_id group by ci.id order by (cps.stock_count + cps.stock_lock_count) desc,skucount asc limit 1";
					
					rs = dbOp.executeQuery(sql);
					coBean = getCargoInfoBean(rs);
					if( coBean == null ) {
						//代表它对应的区域里 没有这种商品的货位库存历史记录， 则应该找一个改区域中找sku种类小于三的货位，来存放这个商品。。。
						coBean = this.getOrtherSkuLessThanThreeCargo(cargoArea, result);
					}
					return coBean;
				}else {
					String sql = "select cps.product_id, ci.*,count(cps2.id) skucount from cargo_info ci, cargo_product_stock cps, cargo_product_stock cps2 where ci.id = cps.cargo_id and ci.area_id = "+ cargoArea.getId()
							+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 + " and ci.store_type in (0,4) and cps.product_id = "
							+ product.getId() + " and ci.whole_code not like 'GZZ01-C%'  and ci.id=cps2.cargo_id group by ci.id order by (cps.stock_count + cps.stock_lock_count) desc,skucount asc limit 1";
					
					if( product.getStatus() == 100 ) {
						//找散件区货位
						rs = dbOp.executeQuery(sql);
						coBean = getCargoInfoBean(rs);
						
						//散件区没有选择整件区货位
						if(coBean == null){
							coBean = this.getOverStockCargo(product, pService, number, cargoArea.getId());
						}
						
					}else{
						//找散件区货位
						rs = dbOp.executeQuery(sql);
						coBean = this.getCargoInfoBean(rs);
						
						//散件区没有选择其它的散件区货位
						if(coBean == null){
							coBean = this.getPartsStockCargo(product, pService, cargoArea.getId());
						}
						
					}
					return coBean;
				}
			}
		}finally{
			if(rs != null){
				rs.close();
			}
		}
	}
	
	
	private CargoInfoBean getOrtherSkuLessThanThreeCargo(
			CargoInfoAreaBean cargoArea, String passageIds) {     
		CargoInfoBean result = null;
		List<CargoInfoBean> cargoinfos=new ArrayList<CargoInfoBean>();
		ResultSet rs = null;
		try {
			String sql = "select ci.*, count(cps.id) as sku_count, sum(cps.stock_count + cps.stock_lock_count ) as totalCount from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = "
		+ cargoArea.getId()
					+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 +
					" and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and " + passageIds + "  and (cps.stock_count + cps.stock_lock_count ) > 0 group by ci.id order by sku_count asc, totalCount desc, ci.whole_code asc ";
			//sql = "select * from cargo_product_stock order by id asc";
			rs = dbOp.executeQuery(sql);
			while ( rs.next() ) {
				
				CargoInfoBean cBean  = new CargoInfoBean();
				//赋值
				cBean.setId(rs.getInt("ci.id"));
				cBean.setCode(rs.getString("ci.code"));
				cBean.setWholeCode(rs.getString("ci.whole_code"));
				cBean.setStoreType(rs.getInt("ci.store_type"));
				cBean.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				cBean.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				cBean.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				cBean.setProductLineId(rs.getInt("ci.product_line_id"));
				cBean.setType(rs.getInt("ci.type"));
				cBean.setLength(rs.getInt("ci.length"));
				cBean.setWidth(rs.getInt("ci.width"));
				cBean.setHigh(rs.getInt("ci.high"));
				cBean.setFloorNum(rs.getInt("ci.floor_num"));
				cBean.setStatus(rs.getInt("ci.status"));
				cBean.setStockType(rs.getInt("ci.stock_type"));
				cBean.setShelfId(rs.getInt("ci.shelf_id"));
				cBean.setStockAreaId(rs.getInt("ci.stock_area_id"));
				cBean.setStorageId(rs.getInt("ci.storage_id"));
				cBean.setAreaId(rs.getInt("ci.area_id"));
				cBean.setCityId(rs.getInt("ci.city_id"));
				cBean.setRemark(rs.getString("ci.remark"));
				cBean.setSkucount(rs.getInt("sku_count"));
				cargoinfos.add(cBean);
			}
			rs.close();
			if(cargoinfos.size()>0){
				for (CargoInfoBean cargoInfoBean : cargoinfos) {
					if(cargoInfoBean.getSkucount() <= 3){
							result=cargoInfoBean;
							break;
					}
				}
				if(result==null){
					for (CargoInfoBean cargoInfoBean : cargoinfos) {
						if(cargoInfoBean.getSkucount() <= 5){
								result=cargoInfoBean;
								break;
						}
					}
				}
			}	
				if( result ==null) {
					sql = "select ci.*, count(cps.id) as sku_count "
							+ " from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = "
							+ cargoArea.getId()
										+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 +
										" and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and  " + passageIds + "  and (cps.stock_count + cps.stock_lock_count ) = 0 group by ci.id order by sku_count asc, ci.whole_code asc ";
					rs = dbOp.executeQuery(sql);
					cargoinfos.clear();
					while ( rs.next() ) {
						
						CargoInfoBean cBean  = new CargoInfoBean();
						//赋值
						cBean.setId(rs.getInt("ci.id"));
						cBean.setCode(rs.getString("ci.code"));
						cBean.setWholeCode(rs.getString("ci.whole_code"));
						cBean.setStoreType(rs.getInt("ci.store_type"));
						cBean.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
						cBean.setMaxStockCount(rs.getInt("ci.max_stock_count"));
						cBean.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
						cBean.setProductLineId(rs.getInt("ci.product_line_id"));
						cBean.setType(rs.getInt("ci.type"));
						cBean.setLength(rs.getInt("ci.length"));
						cBean.setWidth(rs.getInt("ci.width"));
						cBean.setHigh(rs.getInt("ci.high"));
						cBean.setFloorNum(rs.getInt("ci.floor_num"));
						cBean.setStatus(rs.getInt("ci.status"));
						cBean.setStockType(rs.getInt("ci.stock_type"));
						cBean.setShelfId(rs.getInt("ci.shelf_id"));
						cBean.setStockAreaId(rs.getInt("ci.stock_area_id"));
						cBean.setStorageId(rs.getInt("ci.storage_id"));
						cBean.setAreaId(rs.getInt("ci.area_id"));
						cBean.setCityId(rs.getInt("ci.city_id"));
						cBean.setRemark(rs.getString("ci.remark"));
						cBean.setSkucount(rs.getInt("sku_count"));
						cargoinfos.add(cBean);
					}
					rs.close();
					if(cargoinfos.size()>0){
						for (CargoInfoBean cargoInfoBean : cargoinfos) {
								result=cargoInfoBean;
								break;
						}
					}
			  }
				
				if( result ==null) {
					sql = "select ci.* "
							+ " from cargo_info ci where  ci.area_id = "
							+ cargoArea.getId()
										+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 +
										" and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and  " + passageIds + "  and not exists (select cps.id from cargo_product_stock cps where cps.cargo_id=ci.id) order by ci.whole_code asc ";
					rs = dbOp.executeQuery(sql);
					cargoinfos.clear();
					while ( rs.next() ) {
						
						CargoInfoBean cBean  = new CargoInfoBean();
						//赋值
						cBean.setId(rs.getInt("ci.id"));
						cBean.setCode(rs.getString("ci.code"));
						cBean.setWholeCode(rs.getString("ci.whole_code"));
						cBean.setStoreType(rs.getInt("ci.store_type"));
						cBean.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
						cBean.setMaxStockCount(rs.getInt("ci.max_stock_count"));
						cBean.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
						cBean.setProductLineId(rs.getInt("ci.product_line_id"));
						cBean.setType(rs.getInt("ci.type"));
						cBean.setLength(rs.getInt("ci.length"));
						cBean.setWidth(rs.getInt("ci.width"));
						cBean.setHigh(rs.getInt("ci.high"));
						cBean.setFloorNum(rs.getInt("ci.floor_num"));
						cBean.setStatus(rs.getInt("ci.status"));
						cBean.setStockType(rs.getInt("ci.stock_type"));
						cBean.setShelfId(rs.getInt("ci.shelf_id"));
						cBean.setStockAreaId(rs.getInt("ci.stock_area_id"));
						cBean.setStorageId(rs.getInt("ci.storage_id"));
						cBean.setAreaId(rs.getInt("ci.area_id"));
						cBean.setCityId(rs.getInt("ci.city_id"));
						cBean.setRemark(rs.getString("ci.remark"));
						cargoinfos.add(cBean);
					}
					rs.close();
					if(cargoinfos.size()>0){
						for (CargoInfoBean cargoInfoBean : cargoinfos) {
								result=cargoInfoBean;
								break;
						}
					}
			  }
				
				
				if( result ==null) {
					List newCargoList = this.getCargoInfoList(" area_id = "
							+ cargoArea.getId()
							+ " and stock_type = 0 and status = " + CargoInfoBean.STATUS1 +
							" and store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and  " + passageIds + " ", -1, -1, "whole_code asc");

				
						if(newCargoList.size() > 0){
							for (Object object : newCargoList) {
								CargoInfoBean cargoInfoBean=(CargoInfoBean) object;
									result=cargoInfoBean;
									break;
							}
						}
					
			  }
				if( result == null ) {
				sql = "select ci.*, sum(cps.stock_count + cps.stock_lock_count ) as totalCount from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = "
							+ cargoArea.getId()
										+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 +
										" and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and  " + passageIds + "  and (cps.stock_count + cps.stock_lock_count ) > 0 group by ci.id order by  totalCount asc, ci.whole_code asc ";
								//System.out.println(sql);
					rs = dbOp.executeQuery(sql);
					cargoinfos.clear();
					while ( rs.next() ) {
						
						CargoInfoBean cBean  = new CargoInfoBean();
							//赋值
						cBean.setId(rs.getInt("ci.id"));
						cBean.setCode(rs.getString("ci.code"));
						cBean.setWholeCode(rs.getString("ci.whole_code"));
						cBean.setStoreType(rs.getInt("ci.store_type"));
						cBean.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
						cBean.setMaxStockCount(rs.getInt("ci.max_stock_count"));
						cBean.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
						cBean.setProductLineId(rs.getInt("ci.product_line_id"));
						cBean.setType(rs.getInt("ci.type"));
						cBean.setLength(rs.getInt("ci.length"));
						cBean.setWidth(rs.getInt("ci.width"));
						cBean.setHigh(rs.getInt("ci.high"));
						cBean.setFloorNum(rs.getInt("ci.floor_num"));
						cBean.setStatus(rs.getInt("ci.status"));
						cBean.setStockType(rs.getInt("ci.stock_type"));
						cBean.setShelfId(rs.getInt("ci.shelf_id"));
						cBean.setStockAreaId(rs.getInt("ci.stock_area_id"));
						cBean.setStorageId(rs.getInt("ci.storage_id"));
						cBean.setAreaId(rs.getInt("ci.area_id"));
						cBean.setCityId(rs.getInt("ci.city_id"));
						cBean.setRemark(rs.getString("ci.remark"));
						cargoinfos.add(cBean);
					}
					rs.close();
					if(cargoinfos.size()>0){
						for (CargoInfoBean cargoInfoBean : cargoinfos) {
								result=cargoInfoBean;
								break;
						}
					}
					
					
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null){
				try {rs.close();} catch (SQLException e) {}
			}
		}
		return result;
	}
	public String getMatchPassageIds(String head,int area ,List<HashMap<String, String>> list, String type) {
		ResultSet rs = null;
		String passageIds = "-1";
		StringBuffer result = new StringBuffer();
		try {
			if (type.equals("")) {
				for (HashMap<String, String> map : list) {
					String stockPassageCodeSqlPart = this.getStockPassageCodeForProductCatalog(head, map.get("passage"),StringUtil.toInt(map.get("start")),StringUtil.toInt(map.get("end")), type);
					String sql = "select id from cargo_info_passage where area_id="+area +" and "+stockPassageCodeSqlPart+" and stock_type="+ProductStockBean.STOCKTYPE_QUALIFIED;
					rs = dbOp.executeQuery(sql);
					passageIds = "-1";
					while ( rs.next() ) {
						passageIds += ","+rs.getInt(1);
					}
					if (result.length() <= 0) {
						result.append(" ( ");
					} else {
						result.append(" or ");
					}
					if (type.equals("")) {
						result.append(" (passage_id in ("+passageIds+")" ).append(map.get("floorNum").equals("-1") ? ")" : (" and floor_num in (" + map.get("floorNum") + "))"));
					} else {
						result.append(" (passage_id in ("+passageIds+"))");
					}
				}
				result.append(")");
			} else {
				String stockPassageCodeSqlPart = "";
				for (HashMap<String, String> map : list) {
					if (!"".equals(stockPassageCodeSqlPart)) {
						stockPassageCodeSqlPart += " and ";
					}
					stockPassageCodeSqlPart += this.getStockPassageCodeForProductCatalog(head, map.get("passage"),StringUtil.toInt(map.get("start")),StringUtil.toInt(map.get("end")), type);
				}
				String sql = "select id from cargo_info_passage where area_id="+area +" and "+stockPassageCodeSqlPart+" and stock_type="+ProductStockBean.STOCKTYPE_QUALIFIED;
				rs = dbOp.executeQuery(sql);
				passageIds = "-1";
				while ( rs.next() ) {
					passageIds += ","+rs.getInt(1);
				}
				result.append(" (passage_id in ("+passageIds+"))");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null){
				try {rs.close();} catch (SQLException e) {}
			}
		}
		return result.toString();
	}

	/**
	 * 根据给定的类型参数 返回
	 * @param mark
	 * @return
	 */
	private static String getStockPassageCodeForProductCatalog(String head,String passage, int start, int end, String type) {
		String result = "";
		if (start == -1) {
			result = " whole_code " + type + " like '"+head + "-" + passage + "%'";
		} else {
			result = " whole_code " + type + " in (" + getFixedPassageWholeCodes(head, passage, start, end) + ")";
		}
		return result;
	}
	/**
	 * 根据给定的 头，passage字母，开始数， 结束数，生产一个 passageWholecode 构成的字符串 由,隔开
	 * @param head
	 * @param passage
	 * @param start
	 * @param end
	 * @return
	 */
	private static String getFixedPassageWholeCodes(String head, String passage,
			int start, int end) {
		String result = "";
		for( int i = start; i <= end; i++ ) {
			result += "'"+head + "-"+ passage + StringUtil.addZeroLeft(i, 2) +"',";
		}
		if( result.length() > 0 ) {
			result = result.substring(0, result.length() -1 );
		}
		return result;
	}
	
	private CargoInfoBean getPartsStockCargo(voProduct product,
			WareService pService, int newWareArea) {
		
		CargoInfoBean coBean = null;
		
		List cargoAndProductStockList = this.getCargoAndProductStockList(
					"ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and ci.stock_type="
					+CargoInfoBean.STOCKTYPE_QUALIFIED+" and cps.product_id="+ product.getId() 
					+ " and ci.area_id="+newWareArea + " and ci.status="+CargoInfoBean.STATUS0+" and ci.whole_code not like 'GZZ01-C%'", -1, -1, "stock_count DESC");//相同sku 库存最多的货位
		
		if(cargoAndProductStockList.size() > 0){
			coBean = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo();
		}else{
			voProductLine productLine = pService.getProductLine(
					"product_line_catalog.catalog_id=" + product.getParentId1() 
					+ " or product_line_catalog.catalog_id="+product.getParentId2());
			
			if(productLine==null){
				return null;
			}
			
			List cargoList = this.getCargoInfoList(
					"store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and product_line_id=" + productLine.getId()
					+" and area_id="+newWareArea+" and stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and status="+CargoInfoBean.STATUS1+" and whole_code not like 'GZZ01-C%'", -1, -1, null);
			
			if(cargoList.size() > 0){
				coBean = (CargoInfoBean)cargoList.get(0);
				this.updateCargoInfo("status=0", "id="+coBean.getId());
				CargoProductStockBean cps = new CargoProductStockBean();
				cps.setCargoId(coBean.getId());
				cps.setProductId(product.getId());
				cps.setStockCount(0);
				cps.setStockLockCount(0);
				this.addCargoProductStock(cps);
				coBean.setStatus(0);
			}else{//没有对应产品线的货位，任意找一个，合格库，散件区或混合区，未使用货位
				
				List newCargoList = this.getCargoInfoList(
						"store_type in (0,4) and area_id=" + newWareArea + " and stock_type=0 and status="+CargoInfoBean.STATUS1+" and whole_code not like 'GZZ01-C%'", -1, -1, null);
				
				if(newCargoList.size() > 0){
					coBean = (CargoInfoBean)newCargoList.get(0);
					this.updateCargoInfo("status=0", "id="+coBean.getId());
					CargoProductStockBean cps = new CargoProductStockBean();
					cps.setCargoId(coBean.getId());
					cps.setProductId(product.getId());
					cps.setStockCount(0);
					cps.setStockLockCount(0);
					this.addCargoProductStock(cps);
					coBean.setStatus(0);
				}
			}
		}
		return coBean;
	}

	private CargoInfoBean getOverStockCargo(voProduct product, WareService pService, int number, int newWareArea) {
		
		CargoInfoBean cib = null;
		
		List cargoInfoList = this.getCargoAndProductStockList(
						"ci.area_id = " + newWareArea + " and ci.store_type = " + CargoInfoBean.STORE_TYPE1 
						+ " and ci.stock_type = "+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and ci.status = " + CargoInfoBean.STATUS0 
						+ " and type = " + CargoInfoBean.TYPE2 + " and cps.product_id = " + product.getId()+" and ci.whole_code not like 'GZZ01-C%'", -1, -1, "cps.stock_count desc");
		
		if( cargoInfoList == null || cargoInfoList.size() == 0 ) {
			
			List unrelateCargoInfoList = this.getCargoInfoList(
						"store_type=" + CargoInfoBean.STORE_TYPE1 + " and area_id=" 
						+ newWareArea + " and stock_type= " + CargoInfoBean.STOCKTYPE_QUALIFIED 
						+ " and status=" + CargoInfoBean.STATUS0 + " and type = " + CargoInfoBean.TYPE2+" and whole_code not like 'GZZ01-C%'", -1, -1, "whole_code asc");
			
			for( int i = 0; i < unrelateCargoInfoList.size(); i++ ) {
				CargoInfoBean tempCargo = (CargoInfoBean)unrelateCargoInfoList.get(i);
				if( !checkIsCargoFull(tempCargo, pService, number) ) {
					cib = tempCargo;
					break;
				}
			}
			return cib;
		} else {
			for( int i = 0; i < cargoInfoList.size(); i ++ ) {
				CargoInfoBean temp = ((CargoProductStockBean)cargoInfoList.get(i)).getCargoInfo();
				if(!checkIsCargoFull(temp, pService, number)) {
					cib = temp;
					break;
				}
			}
			if( cib == null ) {
				List unrelateCargoInfoList = this.getCargoInfoList(
						"store_type=" + CargoInfoBean.STORE_TYPE1 + " and area_id=" 
						+ newWareArea + " and stock_type= " + CargoInfoBean.STOCKTYPE_QUALIFIED 
						+ " and status=" + CargoInfoBean.STATUS0 + " and type = " + CargoInfoBean.TYPE2+" and whole_code not like 'GZZ01-C%'", -1, -1, "whole_code asc");
			
				for( int i = 0; i < unrelateCargoInfoList.size(); i++ ) {
					CargoInfoBean tempCargo = (CargoInfoBean)unrelateCargoInfoList.get(i);
					if( !checkIsCargoFull(tempCargo, pService, number) ) {
						cib = tempCargo;
						break;
					}
				}
			}
			return cib;
		}
		
	}

	
	private boolean checkIsCargoFull(CargoInfoBean cib, WareService pService, int number) {
		
		List cargoProductStockList = this.getCargoProductStockList("cargo_id = " + cib.getId(), -1, -1, "stock_count desc");
		int totalCount = 0;
		for( int i = 0; i < cargoProductStockList.size(); i ++ ) {
			CargoProductStockBean cpsb = (CargoProductStockBean) cargoProductStockList.get(i);
			totalCount += cpsb.getStockCount() + cpsb.getStockLockCount();
		}
		int allKindsAddUp = totalCount + cib.getSpaceLockCount() + number;
		if( allKindsAddUp  > cib.getMaxStockCount() ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	
	private CargoInfoBean getCargoInfoBean(ResultSet rs) throws SQLException {
		
		CargoInfoBean coBean = null;
		if (rs.next()) {
			coBean = new CargoInfoBean();
			//赋值
			coBean.setId(rs.getInt("ci.id"));
			coBean.setCode(rs.getString("ci.code"));
			coBean.setWholeCode(rs.getString("ci.whole_code"));
			coBean.setStoreType(rs.getInt("ci.store_type"));
			coBean.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
			coBean.setMaxStockCount(rs.getInt("ci.max_stock_count"));
			coBean.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
			coBean.setProductLineId(rs.getInt("ci.product_line_id"));
			coBean.setType(rs.getInt("ci.type"));
			coBean.setLength(rs.getInt("ci.length"));
			coBean.setWidth(rs.getInt("ci.width"));
			coBean.setHigh(rs.getInt("ci.high"));
			coBean.setFloorNum(rs.getInt("ci.floor_num"));
			coBean.setStatus(rs.getInt("ci.status"));
			coBean.setStockType(rs.getInt("ci.stock_type"));
			coBean.setShelfId(rs.getInt("ci.shelf_id"));
			coBean.setStockAreaId(rs.getInt("ci.stock_area_id"));
			coBean.setStorageId(rs.getInt("ci.storage_id"));
			coBean.setAreaId(rs.getInt("ci.area_id"));
			coBean.setCityId(rs.getInt("ci.city_id"));
			coBean.setRemark(rs.getString("ci.remark"));
		}
		return coBean;
	}

	@Override
	public boolean addCargoStaffPerformance(CargoStaffPerformanceBean bean) {
		// TODO Auto-generated method stub
		return addXXX(bean, "cargo_staff_performance");
	}

	@Override
	public boolean updateCargoStaffPerformance(String set, String condition) {
		// TODO Auto-generated method stub
		return updateXXX(set, condition, "cargo_staff_performance");
	}

	@Override
	public CargoStaffPerformanceBean getCargoStaffPerformance(String condition) {
		// TODO Auto-generated method stub
		return  (CargoStaffPerformanceBean) getXXX(condition, "cargo_staff_performance", "adultadmin.bean.cargo.CargoStaffPerformanceBean");
	}

	@Override
	public ArrayList<CargoStaffPerformanceBean> getCargoStaffPerformanceList(
			String condition, int index, int count, String orderBy) {
		// TODO Auto-generated method stub
		return getXXXList(condition, index, count, orderBy, "cargo_staff_performance", "adultadmin.bean.cargo.CargoStaffPerformanceBean");
	}

	@Override
	public List<StockShareBean> getStockShareList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_share", "mmb.rec.stat.bean.StockShareBean");
	}

	@Override
	/**作者 郝亚斌
	 * 在获得Count 的时候加上了 cargo_info_stock_area 中的条件 限制。
	 * 查询时请使用别名 cargo_info —— ci  
	 */
	public int getCargoInfoWithStockAreaCodeRestrictCount(String condition,String stockAreaCode) {
		int result = 0;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		StringBuilder sqlCount = new StringBuilder("select count(ci.id) from cargo_info ci, cargo_info_stock_area cisa where ci.stock_area_id = cisa.id and cisa.code='");
		sqlCount.append(stockAreaCode).append("'");
		sqlCount.append(" and ").append(condition);
		rs = dbOp.executeQuery(sqlCount.toString());
		try {
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	/**
	 * 查询列表的时候加上了cargo_info_stock_area 的限制。
	 * 查询时请使用别名 cargo_info —— ci 
	 */
	public List getCargoInfoWithStockAreaCodeRestrictList(String condition,int index, int count, String orderBy, String stockAreaCode) {
		ArrayList list = new ArrayList();
		
        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return list;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select ci.* from cargo_info ci, cargo_info_stock_area cisa where ci.stock_area_id = cisa.id and cisa.code='"+stockAreaCode+"'";
        if (condition != null) {
            query += " and " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        
        query = DbOperation.getPagingQuery(query, index, count);
        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }
        
		try {
			while(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));
				list.add(ci);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			release(dbOp);
		}
		return list;
	}

	@Override
	public int getOldAreaIdOfCargo(CargoInfoBean inCi) {
		CargoInfoAreaBean ciaBean = this.getCargoInfoArea("id="+inCi.getAreaId());
		if( ciaBean == null ) {
			return -1;
		} else {
			return ciaBean.getOldId();
		}
	}

	public CargoProductStockBean getCargoProductStock(int newProductId,int areaId) {
		CargoProductStockBean bean = new CargoProductStockBean();
		ResultSet rs = null;
		String query = "SELECT cps.id,cps.cargo_id,cps.product_id,cps.stock_count,cps.stock_lock_count " +
				"FROM cargo_product_stock AS cps " +
				"INNER JOIN cargo_info AS ci ON cps.cargo_id = ci.id " +
				"where cps.product_id=" + newProductId + " and ci.area_id=" + areaId;
		rs = this.dbOp.executeQuery(query);
		try {
			if (rs.next()) {
				bean.setId(rs.getInt(1));
				bean.setCargoId(rs.getInt(2));
				bean.setProductId(rs.getInt(3));
				bean.setStockCount(rs.getInt(4));
				bean.setStockLockCount(rs.getInt(5));
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	public List<?> getListZC(String cbName, String productCode) {
		ArrayList returnList = new ArrayList();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		returnList.add("");
		if (productCode.endsWith("d") || productCode.endsWith("D")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "U");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
		} else if ("、女装、品牌女装、运动男装、品牌男装、品牌运动装、运动女装、包、配饰、男装、内衣、儿童服装、情侣装、".contains("、" + cbName + "、")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "1,2,3,4");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "B");
			map.put("start", "8");
			map.put("end", "27");
			map.put("floorNum", "1,2,3");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "P");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);			
		} else if("、成人日用、彩妆香水、美容保健、护肤护理、食品、".contains("、" + cbName + "、")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "B");
			map.put("start", "1");
			map.put("end", "7");
			map.put("floorNum", "1,2,3");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "P");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);				
		} else if("、运动鞋、男时尚鞋、女时尚鞋、运动户外、品牌运动鞋、儿童鞋、".contains("、" + cbName + "、")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "5");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "B");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "4");
			list.add(map);
		} else if("、小家电、新奇特商品、儿童玩具、日用百货、婴童用品、孕妇用品、".contains("、" + cbName + "、")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "C");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);			
			map = new HashMap<String, String>();
			map.put("passage", "P");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);				
		} else if("、手机、数码产品、电脑、饰品、手表、".contains("、" + cbName + "、")) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);		
		} else if ("、公共赠品、下架产品、".contains("、" + cbName + "、")){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "P");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);			
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "1,2,3,4");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "B");
			map.put("start", "8");
			map.put("end", "27");
			map.put("floorNum", "1,2,3");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "B");
			map.put("start", "1");
			map.put("end", "7");
			map.put("floorNum", "1,2,3");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "5");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "B");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "4");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "C");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "P");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "U");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
			returnList.clear();
			returnList.add("not");
		}
		returnList.add(list);
		
		return returnList;
	}
	
	public List<?> getListCD(String cbName, String productCode) {
		ArrayList returnList = new ArrayList();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		returnList.add("");
		if("、公共赠品、品牌女装、运动女装、男装、品牌男装、运动男装、情侣装、包、内衣、下架产品、错误分类、女装、孕妇用品、儿童服装、婴童用品、品牌运动装、儿童玩具、儿童鞋、配饰、小家电、新奇特商品、".contains("、" + cbName + "、")){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "1");
			map.put("end", "30");
			map.put("floorNum", "1,2,3,4");
			list.add(map);
			//运动鞋、品牌运动鞋、运动户外、女时尚鞋、男时尚鞋
		}else if( "运动鞋".equals(cbName)|| "品牌运动鞋".equals(cbName) || "运动户外".equals(cbName)|| "男时尚鞋".equals(cbName) || "女时尚鞋".equals(cbName) ) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "1");
			map.put("end", "30");
			map.put("floorNum", "5");
			list.add(map);
		}else if( "日用百货".equals(cbName)||"家居家装".equals(cbName)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "31");
			map.put("end", "36");
			map.put("floorNum", "-1");
			list.add(map);
		}else if( "家用电器".equals(cbName)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "12");
			map.put("end", "12");
			map.put("floorNum", "-1");
			list.add(map);			
		}else if( "成人日用".equals(cbName)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "37");
			map.put("end", "38");
			map.put("floorNum", "-1");
			list.add(map);
			//护肤护理、彩妆香水、美容保健、食品
		}else if( "护肤护理".equals(cbName)||"彩妆香水".equals(cbName)||"美容保健".equals(cbName)||"食品".equals(cbName)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "39");
			map.put("end", "42");
			map.put("floorNum", "-1");
			list.add(map);
			//手机
		} else if("手机".equals(cbName)){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "1");
			map.put("end", "2");
			map.put("floorNum", "-1");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "4");
			map.put("end", "4");
			map.put("floorNum", "-1");
			list.add(map);			
			//手表、数码产品、电脑
		} else if("电脑".equals(cbName)||"数码产品".equals(cbName)||"手表".equals(cbName)){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "3");
			map.put("end", "3");
			map.put("floorNum", "-1");
			list.add(map);;
			//饰品
		}else if( "饰品".equals(cbName)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "5");
			map.put("end", "6");
			map.put("floorNum", "-1");
			list.add(map);
		}else {
			//其他区域
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("passage", "A");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
			map = new HashMap<String, String>();
			map.put("passage", "G");
			map.put("start", "-1");
			map.put("end", "-1");
			map.put("floorNum", "-1");
			list.add(map);
			returnList.clear();
			returnList.add("not");
		}
		returnList.add(list);
		
		return returnList;
	}
	/*
	 * 是否使用退货上架指向规则的判断
	 */
	private boolean judgeConfigDirect(voProduct product,CargoInfoAreaBean cargoArea) {
		StringBuilder sb = new StringBuilder("select count(rpd.id) flag from returned_product_direct rpd");
		sb.append(" join cargo_info_storage cis on rpd.storage_id=cis.id join cargo_info_area cia on cis.area_id=cia.id");
		sb.append(" join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id ");
		sb.append(" where rpd.status=1 and cia.id='"+cargoArea.getId()+"'");
		sb.append(" and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		boolean flag = false;
		ResultSet rs = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			while (rs.next()) {
				if(rs.getInt("flag")>0){
					flag=true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	/*
	 * 使用退货上架指向规则
	 */
	private CargoInfoBean getCargoInfoByConfig(voProduct product,CargoInfoAreaBean cargoArea){
		CargoInfoBean coBean = this.getCargoInfoStepA(product, cargoArea);
		if(coBean==null){
			coBean = this.getCargoInfoStepB(product, cargoArea);
		}
		return coBean;
	}
	
	/*
	 * a 有货位商品记录的(不需要限制在退货上架指向则限制在所属仓库/区域/巷道/层数下)
	 * 先取实际库存记录，再取临时表记录
	 * 取库存量最大的货位，若最大库存量相同，优先货位中SKU数少的，若SKU数也相同，优先取货位号小的
	 */
	private CargoInfoBean getCargoInfoStepA(voProduct product,CargoInfoAreaBean cargoArea){
		StringBuilder sb = new StringBuilder("");
		sb.append("select * from(");
		sb.append("		select 1 as flag,count(distinct cps2.product_id) skucount,(cps.stock_count + cps.stock_lock_count) total_count,ci.*");
		sb.append("		  from cargo_info ci");
		sb.append("		  join cargo_product_stock cps on ci.id=cps.cargo_id");
		sb.append("	      join cargo_product_stock cps2 on ci.id=cps2.cargo_id");
		sb.append("      where ci.area_id="+cargoArea.getId());
		sb.append("		   and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append("		   and ci.status="+CargoInfoBean.STATUS0);
		sb.append("        and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append("        and cps.product_id="+product.getId());
		sb.append("      group by ci.id");
		sb.append("   union");
		sb.append("     select 2 as flag,count(distinct rpv2.product_id) skucount,count(distinct rpv.id) total_count, ci.*");
		sb.append("		  from cargo_info ci");
		sb.append("		  join returned_product_virtual rpv on ci.id=rpv.cargo_id");
		sb.append("		  join returned_product_virtual rpv2 on ci.id=rpv2.cargo_id");
		sb.append("      where ci.area_id="+cargoArea.getId());
		sb.append("		   and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append("		   and ci.status="+CargoInfoBean.STATUS0);
		sb.append("        and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append("        and rpv.product_id="+product.getId());
		sb.append("      group by ci.id");
		sb.append(" ) aa order by flag,total_count desc,skucount,whole_code limit 1");
		ResultSet rs = null;
		CargoInfoBean coBean = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			coBean = this.getCargoInfoBeanB(rs);
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
		return coBean;
	}
	
	/*
	 * 无货位商品记录
	 * b.1 取库存量为0的货位
	 */
	private CargoInfoBean getCargoInfoStepB(voProduct product,CargoInfoAreaBean cargoArea){
		StringBuilder sb = new StringBuilder("");
		sb.append("select * from(");
		sb.append("		select distinct rpdc.catalog_level,sum(cps.stock_count + cps.stock_lock_count) total_count,ci.*");
		sb.append("		from cargo_info ci");
		sb.append("		join cargo_product_stock cps on ci.id=cps.cargo_id");
		sb.append("		join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.stock_area_id");
		sb.append("		join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append("		join returned_product_direct_passage rpdp on rpd.id=rpdp.direct_id and ci.passage_id=rpdp.passage_id");
		sb.append("		join returned_product_direct_floor rpdf on rpd.id=rpdf.direct_id and ci.floor_num=rpdf.floor_num");
		sb.append("    where ci.area_id="+cargoArea.getId());
		sb.append(" 	 and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 	 and ci.status="+CargoInfoBean.STATUS0);
		sb.append(" 	 and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	 and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 	 and rpd.status=1");
		sb.append("		 and not exists(select 1 from returned_product_virtual rpv where ci.id=rpv.cargo_id)");
		sb.append("    group by ci.id,rpdc.id");
		sb.append(" )aa where total_count=0");
		sb.append(" order by catalog_level desc,whole_code limit 1");
		ResultSet rs = null;
		CargoInfoBean coBean = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			coBean = this.getCargoInfoBeanB(rs);
			if(coBean==null){
				coBean = this.getCargoInfoStepC(product, cargoArea);
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		return coBean;
	}
	
	/*
	 * 无货位商品记录
	 * b.2  有库存0<SKU数<=4的，优先取货位库存量最少的；有库存4<SKU数<=6的，优先取货位库存量最少的；有库存SKU数>6的，优先取货位库存量最少的；
	 * 如果最小库存量有相同的，再优先选取SKU最少的；若最小库存相同，且最小SKU个数也相同，优先去货位号较小的； 
	 */
	private CargoInfoBean getCargoInfoStepC(voProduct product,CargoInfoAreaBean cargoArea){
		StringBuilder sb = new StringBuilder("");
		sb.append("select *,case when skuCount<=4 then 'a' when skuCount<=6 then 'b' else 'c' end sku_level from (");
		sb.append("	   select sum(bb.total_count) totalCount,sum(bb.sku_count) skuCount,bb.* from (");
		sb.append("		   select distinct sum(cps.stock_count+cps.stock_lock_count) total_count,rpdc.catalog_level,0 as sku_count,ci.*");
		sb.append("			from cargo_info ci");
		sb.append("			join cargo_product_stock cps on ci.id=cps.cargo_id and (cps.stock_count + cps.stock_lock_count)>0");
		sb.append("			join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.stock_area_id");
		sb.append("			join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append("			join returned_product_direct_passage rpdp on rpd.id=rpdp.direct_id and ci.passage_id=rpdp.passage_id");
		sb.append("			join returned_product_direct_floor rpdf on rpd.id=rpdf.direct_id and ci.floor_num=rpdf.floor_num");
		sb.append("        where ci.area_id="+cargoArea.getId());
		sb.append(" 	     and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 	     and ci.status="+CargoInfoBean.STATUS0);
		sb.append(" 	     and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	     and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 	     and rpd.status=1");
		sb.append("		   group by ci.id,rpdc.id");
		sb.append("		union all");
		sb.append("		   select distinct count(rpv.id) total_count,rpdc.catalog_level,0 as sku_count,ci.*");
		sb.append("			from cargo_info ci");
		sb.append("			join returned_product_virtual rpv on ci.id=rpv.cargo_id");
		sb.append("			join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.stock_area_id");
		sb.append("			join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append("			join returned_product_direct_passage rpdp on rpd.id=rpdp.direct_id and ci.passage_id=rpdp.passage_id");
		sb.append("			join returned_product_direct_floor rpdf on rpd.id=rpdf.direct_id and ci.floor_num=rpdf.floor_num");
		sb.append("        where ci.area_id="+cargoArea.getId());
		sb.append(" 	     and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 	     and ci.status="+CargoInfoBean.STATUS0);
		sb.append(" 	     and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	     and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 	     and rpd.status=1");
		sb.append("		   group by ci.id,rpdc.id");
		sb.append("		union all");
		sb.append("		  select 0 as total_count,cc.catalog_level,count(distinct cc.product_id) sku_count,ci.* from(");
		sb.append("			  select cps.product_id,rpdc.catalog_level,ci.id cargoId");
		sb.append("				from cargo_info ci");
		sb.append("				join cargo_product_stock cps on ci.id=cps.cargo_id and (cps.stock_count + cps.stock_lock_count)>0");
		sb.append("				join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.stock_area_id");
		sb.append("				join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append("				join returned_product_direct_passage rpdp on rpd.id=rpdp.direct_id and ci.passage_id=rpdp.passage_id");
		sb.append("				join returned_product_direct_floor rpdf on rpd.id=rpdf.direct_id and ci.floor_num=rpdf.floor_num");
		sb.append("            where ci.area_id="+cargoArea.getId());
		sb.append(" 	         and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 	         and ci.status="+CargoInfoBean.STATUS0);
		sb.append(" 	         and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	         and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 	         and rpd.status=1");
		sb.append("			union");
		sb.append("			  select rpv.product_id,rpdc.catalog_level,ci.id cargoId");
		sb.append("				from cargo_info ci");
		sb.append("				join returned_product_virtual rpv on ci.id=rpv.cargo_id");
		sb.append("				join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.stock_area_id");
		sb.append("				join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append("				join returned_product_direct_passage rpdp on rpd.id=rpdp.direct_id and ci.passage_id=rpdp.passage_id");
		sb.append("				join returned_product_direct_floor rpdf on rpd.id=rpdf.direct_id and ci.floor_num=rpdf.floor_num");
		sb.append("            where ci.area_id="+cargoArea.getId());
		sb.append(" 	         and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 	         and ci.status="+CargoInfoBean.STATUS0);
		sb.append(" 	         and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	         and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 	         and rpd.status=1");
		sb.append("			) cc join cargo_info ci on cc.cargoId=ci.id");
		sb.append("			group by cc.cargoId,cc.catalog_level");
		sb.append("		) bb group by bb.id,bb.catalog_level");
		sb.append("	) aa where max_stock_count>=totalCount+1");
		sb.append("	order by sku_level,catalog_level desc,totalCount,skuCount,whole_code limit 1");
		ResultSet rs = null;
		CargoInfoBean coBean = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			coBean = this.getCargoInfoBeanB(rs);
			if(coBean==null){
				coBean = this.getCargoInfoStepD(product, cargoArea);
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		return coBean;
	}
	/*
	 * d 货位状态是未使用的，优先选择限制区域下
	 */
	private CargoInfoBean getCargoInfoStepD(voProduct product,CargoInfoAreaBean cargoArea){
		StringBuilder sb = new StringBuilder("");
		sb.append("select * from(");
		sb.append("	  select ci.*,rpdc.catalog_level,count(distinct rpv.id) total_count,count(distinct rpv.product_id) sku_count");
		sb.append("		from cargo_info ci");
		sb.append("		left join returned_product_virtual rpv on ci.id=rpv.cargo_id");
		sb.append("		join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.stock_area_id");
		sb.append("		join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append("		join returned_product_direct_passage rpdp on rpd.id=rpdp.direct_id and ci.passage_id=rpdp.passage_id");
		sb.append("		join returned_product_direct_floor rpdf on rpd.id=rpdf.direct_id and ci.floor_num=rpdf.floor_num");
		sb.append("    where ci.area_id="+cargoArea.getId());
		sb.append(" 	 and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 	 and ci.status="+CargoInfoBean.STATUS1);
		sb.append(" 	 and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	 and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 	 and rpd.status=1");
		sb.append("	   group by ci.id,rpdc.catalog_level");
		sb.append(" )aa");
		sb.append(" order by catalog_level desc,total_count,sku_count,whole_code limit 1");
		ResultSet rs = null;
		CargoInfoBean coBean = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			coBean = this.getCargoInfoBeanB(rs);
			if(coBean==null){
				coBean = this.getCargoInfoStepE(product, cargoArea);
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		return coBean;		
	}
	/*
	 * e 若所限制的区域内找不到可用的货位或者货位不存在时，优先选择已设定的默认区域（默认区域为参数，若未设置默认区
	 * 域，系统依次从合格库区域最小区域号开始寻找，若无满足条件的依次寻找下一个区域）下，货位库存量最少的
	 */
	private CargoInfoBean getCargoInfoStepE(voProduct product,CargoInfoAreaBean cargoArea){
		StringBuilder sb = new StringBuilder("");
		sb.append(" select ci.*,count(cps.id) skucount,sum(cps.stock_count+cps.stock_lock_count) total_count,rpdc.catalog_level");
		sb.append(" 	from cargo_info ci ");
		sb.append(" 		left join cargo_product_stock cps on ci.id=cps.cargo_id");
		sb.append(" 		join returned_product_direct rpd on ci.storage_id=rpd.storage_id and ci.stock_area_id=rpd.default_stock_area_id ");
		sb.append(" 		join returned_product_direct_catalog rpdc on rpd.id=rpdc.direct_id");
		sb.append(" 	where ci.area_id="+cargoArea.getId());
		sb.append(" 		and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 		and (ci.status="+CargoInfoBean.STATUS0+" or ci.status="+CargoInfoBean.STATUS1+")");
		sb.append(" 		and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 		and rpdc.catalog_id in ('"+product.getParentId1()+"','"+product.getParentId2()+"','"+product.getParentId3()+"')");
		sb.append(" 		and rpd.status=1");
		sb.append(" 	group by ci.id,rpdc.catalog_level");
		sb.append(" 	order by catalog_level desc,total_count,skucount,whole_code limit 1");
		ResultSet rs = null;
		CargoInfoBean coBean = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			coBean = this.getCargoInfoBeanB(rs);
			if(coBean==null){
				coBean = this.getCargoInfoStepF(product, cargoArea);
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		return coBean;	
	}
	/*
	 * f 若所限制的区域内找不到可用的货位或者货位不存在时，优先选择已设定的默认区域（默认区域为参数，若未设置默认区
	 * 域，系统依次从合格库区域最小区域号开始寻找，若无满足条件的依次寻找下一个区域）下，货位库存量最少的
	 */
	private CargoInfoBean getCargoInfoStepF(voProduct product,CargoInfoAreaBean cargoArea){
		StringBuilder sb = new StringBuilder("");
		sb.append(" select ci.*,a.whole_code areaWholeCode,count(cps.id) skucount,sum(cps.stock_count+cps.stock_lock_count) total_count");
		sb.append(" 	from cargo_info ci ");
		sb.append(" 		left join cargo_product_stock cps on ci.id=cps.cargo_id");
		sb.append(" 		join cargo_info_stock_area a on ci.stock_area_id=a.id");
		sb.append(" 	where ci.area_id="+cargoArea.getId());
		sb.append(" 		and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" 		and (ci.status="+CargoInfoBean.STATUS0+" or ci.status="+CargoInfoBean.STATUS1+")");
		sb.append(" 		and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+")");
		sb.append(" 	group by ci.stock_area_id,ci.id");
		sb.append(" 	order by a.whole_code,total_count,skucount,ci.whole_code limit 1");
		ResultSet rs = null;
		CargoInfoBean coBean = null;
		try{
			rs = dbOp.executeQuery(sb.toString());
			coBean = this.getCargoInfoBeanB(rs);
		} catch (SQLException e) {
			e.printStackTrace();			
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}	
		return coBean;	
	}
	/*
	 * 封装cargo_info
	 */
	private CargoInfoBean getCargoInfoBeanB(ResultSet rs) throws SQLException {
		CargoInfoBean coBean = null;
		if (rs.next()) {
			coBean = new CargoInfoBean();
			//赋值
			coBean.setId(rs.getInt("id"));
			coBean.setCode(rs.getString("code"));
			coBean.setWholeCode(rs.getString("whole_code"));
			coBean.setStoreType(rs.getInt("store_type"));
			coBean.setWarnStockCount(rs.getInt("warn_stock_count"));
			coBean.setMaxStockCount(rs.getInt("max_stock_count"));
			coBean.setSpaceLockCount(rs.getInt("space_lock_count"));
			coBean.setProductLineId(rs.getInt("product_line_id"));
			coBean.setType(rs.getInt("type"));
			coBean.setLength(rs.getInt("length"));
			coBean.setWidth(rs.getInt("width"));
			coBean.setHigh(rs.getInt("high"));
			coBean.setFloorNum(rs.getInt("floor_num"));
			coBean.setStatus(rs.getInt("status"));
			coBean.setStockType(rs.getInt("stock_type"));
			coBean.setShelfId(rs.getInt("shelf_id"));
			coBean.setStockAreaId(rs.getInt("stock_area_id"));
			coBean.setStorageId(rs.getInt("storage_id"));
			coBean.setAreaId(rs.getInt("area_id"));
			coBean.setCityId(rs.getInt("city_id"));
			coBean.setRemark(rs.getString("remark"));
		}
		return coBean;
	}
	
	/*
	 * 添加退货上架临时表数据
	 */
	public boolean addReturnedProductVirtual(ReturnedProductVirtual bean) {
		return dbOp.executeUpdate("insert into returned_product_virtual(cargo_id,oper_id,product_id) "
				+ "values("+bean.getCargoId()+","+bean.getOperId()+","+bean.getProductId()+")");
	}
	
	/*
	 * 清理退货上架临时表
	 */
	private void cleanReturnedProductVirtual(){
		dbOp.executeUpdate("delete from returned_product_virtual "
				+ "where exists (select * from cargo_operation b where oper_id=b.id and "
				+ "(b.status=7 or b.status=8 or b.status=9 or b.effect_status=3 or b.effect_status=4))");
	}
}
