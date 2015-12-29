package adultadmin.service.infc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmb.cargo.model.ReturnedProductVirtual;
import mmb.rec.stat.bean.StockShareBean;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoDeptBean;
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
import adultadmin.util.db.DbOperation;


public interface ICargoService extends IBaseService {

	/**
	 * 为商品分配目的货位
	 * @param voProduct
	 * @return
	 */
	
    public boolean addCargoInfoArea(CargoInfoAreaBean bean);

    public CargoInfoAreaBean getCargoInfoArea(String condition);

    public int getCargoInfoAreaCount(String condition);

    public boolean updateCargoInfoArea(String set, String condition);

    public boolean deleteCargoInfoArea(String condition);

    public ArrayList getCargoInfoAreaList(String condition, int index,
            int count, String orderBy);
    
    public boolean addCargoInfoCity(CargoInfoCityBean bean);

    public CargoInfoCityBean getCargoInfoCity(String condition);

    public int getCargoInfoCityCount(String condition);

    public boolean updateCargoInfoCity(String set, String condition);

    public boolean deleteCargoInfoCity(String condition);

    public ArrayList getCargoInfoCityList(String condition, int index,
            int count, String orderBy);
    
    public boolean addCargoInfoStorage(CargoInfoStorageBean bean);

    public CargoInfoStorageBean getCargoInfoStorage(String condition);

    public int getCargoInfoStorageCount(String condition);

    public boolean updateCargoInfoStorage(String set, String condition);

    public boolean deleteCargoInfoStorage(String condition);

    public ArrayList getCargoInfoStorageList(String condition, int index,
            int count, String orderBy);
    
    //仓库区域
    public boolean addCargoInfoStockArea(CargoInfoStockAreaBean bean);

    public CargoInfoStockAreaBean getCargoInfoStockArea(String condition);

    public int getCargoInfoStockAreaCount(String condition);

    public boolean updateCargoInfoStockArea(String set, String condition);

    public boolean deleteCargoInfoStockArea(String condition);

    public ArrayList getCargoInfoStockAreaList(String condition, int index,
            int count, String orderBy);
    
    //货架
    public boolean addCargoInfoShelf(CargoInfoShelfBean bean);

    public CargoInfoShelfBean getCargoInfoShelf(String condition);

    public int getCargoInfoShelfCount(String condition);

    public boolean updateCargoInfoShelf(String set, String condition);

    public boolean deleteCargoInfoShelf(String condition);

    public ArrayList getCargoInfoShelfList(String condition, int index,
            int count, String orderBy);
    
    //货位信息
    public boolean addCargoInfo(CargoInfoBean bean);

    public CargoInfoBean getCargoInfo(String condition);

    public int getCargoInfoCount(String condition);

    public boolean updateCargoInfo(String set, String condition);

    public boolean deleteCargoInfo(String condition);

    public ArrayList getCargoInfoList(String condition, int index,
            int count, String orderBy);
    
    //货位商品库存信息
    public boolean addCargoProductStock(CargoProductStockBean bean);

    public CargoProductStockBean getCargoProductStock(String condition);

    public int getCargoProductStockCount(String condition);

    public boolean updateCargoProductStock(String set, String condition);

    public boolean deleteCargoProductStock(String condition);

    public ArrayList getCargoProductStockList(String condition, int index,
            int count, String orderBy);
    
    //作业单
    public boolean addCargoOperation(CargoOperationBean bean);

    public CargoOperationBean getCargoOperation(String condition);

    public int getCargoOperationCount(String condition);

    public boolean updateCargoOperation(String set, String condition);

    public boolean deleteCargoOperation(String condition);

    public ArrayList getCargoOperationList(String condition, int index,
            int count, String orderBy);
    
    //作业单货位信息表
    public boolean addCargoOperationCargo(CargoOperationCargoBean bean);

    public CargoOperationCargoBean getCargoOperationCargo(String condition);

    public int getCargoOperationCargoCount(String condition);

    public boolean updateCargoOperationCargo(String set, String condition);

    public boolean deleteCargoOperationCargo(String condition);

    public ArrayList getCargoOperationCargoList(String condition, int index,
            int count, String orderBy);
    
    //作业单操作日志表
    public boolean addCargoOperationLog(CargoOperationLogBean bean);

    public CargoOperationLogBean getCargoOperationLog(String condition);

    public int getCargoOperationLogCount(String condition);

    public boolean updateCargoOperationLog(String set, String condition);

    public boolean deleteCargoOperationLog(String condition);

    public ArrayList getCargoOperationLogList(String condition, int index,
            int count, String orderBy);
    
    //货位操作日志表
    public boolean addCargoInfoLog(CargoInfoLogBean bean);

    public CargoInfoLogBean getCargoInfoLog(String condition);

    public int getCargoInfoLogCount(String condition);

    public boolean updateCargoInfoLog(String set, String condition);

    public boolean deleteCargoInfoLog(String condition);

    public ArrayList getCargoInfoLogList(String condition, int index,
            int count, String orderBy);
    
    public ArrayList getDownShelfList(String query,int index, int count) throws SQLException;
    public int getTablesCount(String query);
    
	public String getCargoOperationMaxIdCode(String header);
	
	public boolean addCargoOperationDownShelf(ArrayList list,CargoOperationBean bean);
	
    //cargo_stock_card
    public boolean addCargoStockCard(CargoStockCardBean bean);

    public CargoStockCardBean getCargoStockCard(String condition);

    public int getCargoStockCardCount(String condition);

    public boolean updateCargoStockCard(String set, String condition);

    public boolean deleteCargoStockCard(String condition);

    public ArrayList getCargoStockCardList(String condition, int index,
            int count, String orderBy);

    //cargo_operation_process
    public boolean addCargoOperationProcess(CargoOperationProcessBean bean);

    public CargoOperationProcessBean getCargoOperationProcess(String condition);

    public int getCargoOperationProcessCount(String condition);

    public boolean updateCargoOperationProcess(String set, String condition);

    public boolean deleteCargoOperationProcess(String condition);

    public ArrayList getCargoOperationProcessList(String condition, int index,
            int count, String orderBy);
    
    //cargo_oper_log
    public boolean addCargoOperLog(CargoOperLogBean bean);

    public CargoOperLogBean getCargoOperLog(String condition);

    public int getCargoOperLogCount(String condition);

    public boolean updateCargoOperLog(String set, String condition);

    public boolean deleteCargoOperLog(String condition);

    public ArrayList getCargoOperLogList(String condition, int index,
            int count, String orderBy);
    
    //cargo_dept
    public boolean addCargoDept(CargoDeptBean bean);

    public CargoDeptBean getCargoDept(String condition);

    public int getCargoDeptCount(String condition);

    public boolean updateCargoDept(String set, String condition);

    public boolean deleteCargoDept(String condition);

    public ArrayList getCargoDeptList(String condition, int index,
            int count, String orderBy);
    
    
	/**
	 * 
	 * 功能:
	 * <p>作者 李双 Apr 15, 2011 4:18:01 PM
	 * @param list调拨单 商品列表
	 * @param bean 作业单bean
	 * @param whloleCode 拨单暂存区 货位完整编号
	 * @return
	 */
	public boolean addCargoOperationUpShelf(ArrayList list,CargoOperationBean bean,String  whloleCode ,int cargoId);
	
	public ArrayList getCargoOperationCascade(String query, int index,int count);
	
	
	 //功能:获取货位库存信息完整列表
	public ArrayList getCargoAndProductStockList(String condition, int index,int count, String orderBy);
	
	public ArrayList getCargoInfoBeanist(String condition, int index,int count, String orderBy);
	
	 //功能:获取货位库存信息完整列表
	public CargoProductStockBean getCargoAndProductStock(String condition);

	
	public ArrayList getStockExchangeCascade(String query,int index,int count);
 
	
	public int getCargoAndProductStockCount(String condition);
	
	//货位库存操作
	/**
     * 
     * 作者：赵林
     * 
     * 创建日期：2011-04-15
     * 
     * 说明：修改产品货位库存数量，增加时，mcount为整数，减少时，mcount为负数
     * 
     * 参数及返回值说明：
     * 
     * @param id
     * @param mcount
     * @return
     */
    public boolean updateCargoProductStockCount(int id, int mcount);
    
    /**
     * 
     * 作者：赵林
     * 
     * 创建日期：2011-04-15
     * 
     * 说明：修改产品货位库存冻结数量，增加时，mcount为整数，减少时，mcount为负数
     * 
     * 参数及返回值说明：
     * 
     * @param id
     * @param mcount
     * @return
     */
    public boolean updateCargoProductStockLockCount(int id, int mcount);
    
    /**
     * 
     * 作者：赵林
     * 
     * 创建日期：2011-04-15
     * 
     * 说明：修改产品货位空间冻结数量，增加时，mcount为整数，减少时，mcount为负数
     * 
     * 参数及返回值说明：
     * 
     * @param id
     * @param mcount
     * @return
     */
    public boolean updateCargoSpaceLockCount(int id, int mcount);
 
 
	/**
	 * 
	 * 功能:
	 * <p>作者 李双 Apr 15, 2011 4:18:44 PM
	 * @param query query 得到已确认上架的商品的数量（已上架数量） 或得到冻结量 上架的商品的数量 文档中：（其中冻结数量）
	 * @return将list 转变成 productId  cargo_operation_cargo Bean 的键值对应形式。方便判断冻结量或以上架量
	 */
	public Map getCoCNumOrLockNum(String query);
	
	public boolean updateProductStockLockCount(int id, int mcount);
	
	public boolean updateProductStockCount(int id, int mcount);
	
	//合格库作业管理-员工管理
	public boolean addCargoStaff(CargoStaffBean bean);

	public CargoStaffBean getCargoStaff(String condition);

	public int getCargoStaffCount(String condition);

    public boolean updateCargoStaff(String set, String condition);

    public boolean deleteCargoStaff(String condition);

    public ArrayList getCargoStaffList(String condition, int index,
            int count, String orderBy);

	//盘点作业单cargo_inventory
	public boolean addCargoInventory(CargoInventoryBean bean);

	public CargoInventoryBean getCargoInventory(String condition);

	public int getCargoInventoryCount(String condition);

    public boolean updateCargoInventory(String set, String condition);

    public boolean deleteCargoInventory(String condition);

    public ArrayList getCargoInventoryList(String condition, int index,
            int count, String orderBy);

    //盘点作业单cargo_inventory_mission
	public boolean addCargoInventoryMission(CargoInventoryMissionBean bean);

	public CargoInventoryMissionBean getCargoInventoryMission(String condition);

	public int getCargoInventoryMissionCount(String condition);

    public boolean updateCargoInventoryMission(String set, String condition);

    public boolean deleteCargoInventoryMission(String condition);

    public ArrayList getCargoInventoryMissionList(String condition, int index,
            int count, String orderBy);
    
    //盘点作业单cargo_inventory_mission_product
	public boolean addCargoInventoryMissionProduct(CargoInventoryMissionProductBean bean);

	public CargoInventoryMissionProductBean getCargoInventoryMissionProduct(String condition);

	public int getCargoInventoryMissionProductCount(String condition);

    public boolean updateCargoInventoryMissionProduct(String set, String condition);

    public boolean deleteCargoInventoryMissionProduct(String condition);

    public ArrayList getCargoInventoryMissionProductList(String condition, int index,
            int count, String orderBy);
    
    //盘点作业单cargo_inventory_log
	public boolean addCargoInventoryLog(CargoInventoryLogBean bean);

	public CargoInventoryLogBean getCargoInventoryLog(String condition);

	public int getCargoInventoryLogCount(String condition);

    public boolean updateCargoInventoryLog(String set, String condition);

    public boolean deleteCargoInventoryLog(String condition);

    public ArrayList getCargoInventoryLogList(String condition, int index,
            int count, String orderBy);
    
  //巷道 cargo_info_passage
	public boolean addCargoInfoPassage(CargoInfoPassageBean bean);

	public CargoInfoPassageBean getCargoInfoPassage(String condition);

	public int getCargoInfoPassageCount(String condition);

    public boolean updateCargoInfoPassage(String set, String condition);

    public boolean deleteCargoInfoPassage(String condition);
    
    public ArrayList getCargoInfoPassageList(String condition, int index,
            int count, String orderBy);
    
    /**
     *	获取商品对应货位号
     * @param product
     * @return
     * @throws Exception
     */
    CargoInfoBean getTargetCargoInfo(voProduct product, int number, int wareArea) throws Exception;
    /**
     * 作者：石远飞
     *
     * 日期：2013-2-21
     *
     * TODO 添加物流员工绩效考核表记录
     */
    public boolean addCargoStaffPerformance(CargoStaffPerformanceBean bean);
    /**
     * 作者：石远飞
     *
     * 日期：2013-2-21
     *
     * TODO 更新物流员工绩效考核表记录
     */
    public boolean updateCargoStaffPerformance(String set,String condition);
    /**
     * 作者：石远飞
     *
     * 日期：2013-2-21
     *
     * TODO 获取物流员工绩效考核表单条记录
     */
    public CargoStaffPerformanceBean getCargoStaffPerformance(String condition);
    /**
     * 作者：石远飞
     *
     * 日期：2013-2-21
     *
     * TODO 获取物流员工绩效考核表记录集合
     */
    public ArrayList<CargoStaffPerformanceBean> getCargoStaffPerformanceList(String condition, int index,
            int count, String orderBy);
    /**
     * 
     * 2013-9-24
     * 朱爱林	
     *  获取库存占比记录集合
     */
	public List<StockShareBean> getStockShareList(String condition, int index, int count, String string2);
	
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
	public ArrayList getCargoAndProductStockWithStockAreaCodeRestrictList(String condition, int index,int count, String orderBy, String stockAreaCode);
	/**
	 * 作者 郝亚斌
	 * 针对快销商品，增加了联查cargo_info_stock_area表的 ,也连上了cargo_product_stock表，主要获得货位的信息
	 * 查询时请使用别名 cargo_info —— ci  cargo_product_stock —— cps
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public ArrayList getCargoInfoWithStockAreaCodeAndCPSRestrictList(String condition, int index,int count, String orderBy, String stockAreaCode);
	
	/**
	 * 作者 郝亚斌
	 * 在获取货位的时候加上了stockArea的限制条件的限制条件
	 * @param condition
	 * @return
	 */
	public CargoInfoBean getCargoInfoWithStockAreaCodeRestrict(String condition, String stockAreaCode );

	/**
	 * 作者 郝亚斌
	 * 在获得Count 的时候加上了 cargo_info_stock_area 中的条件 限制。
	 * @param condition
	 * @param stockAreaCode
	 * @return
	 */
	public int getCargoInfoWithStockAreaCodeRestrictCount(String condition,String stockAreaCode);

	/**
	 * 查询列表的时候加上了cargo_info_stock_area 的限制。
	 * @param string
	 * @param i
	 * @param countPerPage
	 * @param string2
	 * @param string3
	 * @return
	 */
	public List getCargoInfoWithStockAreaCodeRestrictList(String condition, int index,int count, String orderBy, String stockAreaCode);

	/**
	 * 根据货位 得到它的 目的地区id 库存体系的
	 * @param inCi
	 * @return
	 */
	public int getOldAreaIdOfCargo(CargoInfoBean inCi);
	
	/**
	 * 添加退货上架临时表数据
	 * @descripion TODO
	 * @author 刘仁华
	 * @time  2015年4月1日
	 */
	public boolean addReturnedProductVirtual(ReturnedProductVirtual bean);
}
