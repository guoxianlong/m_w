/**
 * 
 */
package adultadmin.service;
import mmb.rec.checkOrderStat.CheckOrderStatJobService;
import mmb.rec.orderTransmit.OrderTransmitJobService;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.stat.DeliverService;
import mmb.stock.stat.SortingInfoService;
import mmb.stock.stat.WarehousingAbnormalService;
import adultadmin.framework.exceptions.DatabaseException;
import adultadmin.service.impl.AdminLogServiceImpl;
import adultadmin.service.impl.AfterSalesServiceImpl;
import adultadmin.service.impl.BalanceServiceImpl;
import adultadmin.service.impl.BarcodeCreateManagerServiceImpl;
import adultadmin.service.impl.BatchBarcodeServiceImpl;
import adultadmin.service.impl.BsByServiceManagerServiceImpl;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.impl.CatalogServiceImpl;
import adultadmin.service.impl.CityAreaService;
import adultadmin.service.impl.ProductDiscountServiceImpl;
import adultadmin.service.impl.ProductPackageServiceImpl;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.impl.SMSServiceImpl;
import adultadmin.service.impl.SMSWithOneDBServiceImpl;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.impl.SupplierServiceImpl;
import adultadmin.service.impl.SystemServiceImpl;
import adultadmin.service.impl.UserOrderServiceImpl;
import adultadmin.service.impl.UserServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.service.infc.IProductDiscountService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.ISMSService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.service.infc.ISystemService;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 * 
 */
public class ServiceFactory implements IServiceFactory {

	private static IServiceFactory Singleton = null;

	/**
	 * 得到实现IAdminServiceFactory接口的对象实例。
	 * 
	 * 
	 * @return 实现IAdminServiceFactory接口的对象实例
	 * 
	 */
	public static IServiceFactory getInstance() {

		synchronized (ServiceFactory.class) {
			if (Singleton == null) {
				Singleton = new ServiceFactory();
				return Singleton;
			}
		}
		return Singleton;
	}

	/**
	 * 
	 * 
	 */
	private ServiceFactory() {
		init();
	}

	/**
	 * 初始化。
	 * 
	 * 
	 */
	public void init() {
		try {

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public IAdminService createAdminService() throws IllegalAccessException,
	InstantiationException {
		try {
			return (IAdminService) new AdminServiceImpl();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}


	public IStatService createStatService() throws IllegalAccessException,
	InstantiationException {
		try {
			return (IStatService) new StatServiceImpl();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static mmb.stock.stat.StatService createStatServiceStat(int useConnType,
			DbOperation dbOp) {
		return new mmb.stock.stat.StatService( useConnType, dbOp);
	}

	public static IUserService createUserService(int useConnType,
			DbOperation dbOp) {
		return new UserServiceImpl(useConnType, dbOp);
	}

	public static IAdminLogService createAdminLogService() {
		return new AdminLogServiceImpl();
	}

	public static IAdminLogService createAdminLogService(int useConnType,
			DbOperation dbOp) {
		return new AdminLogServiceImpl(useConnType, dbOp);
	}

	public static IAdminService createAdminServiceLBJ() {
		try {
			return new AdminServiceImpl();
		} catch (DatabaseException e) {
			return null;
		}
	}

	public static IAdminService createAdminService(String databaseName) {
		return new AdminServiceImpl(databaseName);
	}

	public static IAdminService createAdminService(DbOperation dbOp) {
		return new AdminServiceImpl(dbOp);
	}


	public static ICatalogService createCatalogService() {
		return new CatalogServiceImpl();
	}

	public static ICatalogService createCatalogService(int useConnType,
			DbOperation dbOp) {
		return new CatalogServiceImpl(useConnType, dbOp);
	}

	public static IStockService createStockService() {
		return new StockServiceImpl();
	}

	public static IStockService createStockService(int useConnType,
			DbOperation dbOp) {
		return new StockServiceImpl(useConnType, dbOp);
	}

//	public static IProductPackageService createProductPackageService() {
//		return new ProductPackageServiceImpl();
//	}

	public static IProductPackageService createProductPackageService(
			int useConnType, DbOperation dbOp) {
		return new ProductPackageServiceImpl(useConnType, dbOp);
	}



	public static IProductStockService createProductStockService() {
		return new ProductStockServiceImpl();
	}

	public static IProductStockService createProductStockService(
			int useConnType, DbOperation dbOp) {
		return new ProductStockServiceImpl(useConnType, dbOp);
	}

	public static ISystemService createSystemService() {
		return new SystemServiceImpl();
	}

	public static ISystemService createSystemService(int useConnType,
			DbOperation dbOp) {
		return new SystemServiceImpl(useConnType, dbOp);
	}

	public static IBalanceService createBalanceService(int useConnType,
			DbOperation dbOp) {
		return new BalanceServiceImpl(useConnType, dbOp);
	}

	public static IBalanceService createBalanceService() {
		return new BalanceServiceImpl();
	}
	
	// 报损报以
	public static IBsByServiceManagerService createBsByServiceManagerService(
			int useConnType, DbOperation dbOp) {
		return new BsByServiceManagerServiceImpl(useConnType, dbOp);
	}

	public static IBsByServiceManagerService createBsByServiceManagerService() {
		return new BsByServiceManagerServiceImpl();
	}

	//售后
	public static IAfterSalesService createAfterSalesService(
			int useConnType, DbOperation dbOp) {
		return new AfterSalesServiceImpl(useConnType, dbOp);
	}

//	public static IAfterSalesService createAfterSalesService() {
//		return new AfterSalesServiceImpl();
//	}

	//SMS
	public static ISMSService createSMSService(
			int useConnType, DbOperation dbOp) {
		return new SMSServiceImpl(useConnType, dbOp);
	}

	public static ISMSService createSMSService() {
		return new SMSServiceImpl();
	}



	//订单相关
	public static IUserOrderService createUserOrderService(
			int useConnType, DbOperation dbOp) {
		return new UserOrderServiceImpl(useConnType, dbOp);
	}

	public static IUserOrderService createUserOrderService() {
		return new UserOrderServiceImpl();
	}



	/**
	 * 功能:创建条码生成管理Service
	 * <p>作者文齐辉 2011-1-6 下午12:09:10
	 * @param useConnType
	 * @param dbOp
	 */
	public static IBarcodeCreateManagerService createBarcodeCMServcie(int useConnType,DbOperation dbOp){
		return new BarcodeCreateManagerServiceImpl(useConnType,dbOp);
	}
	/**
	 * 功能:创建新数据库连接的Service
	 * <p>作者文齐辉 2011-1-6 下午12:09:10 
	 */
//	public static IBarcodeCreateManagerService createBarcodeCMServcie(){
//		return new BarcodeCreateManagerServiceImpl();
//	}
	/**
	 * 功能:创建条码生成管理Service
	 * <p>作者文齐辉 2011-1-6 下午12:09:10
	 * @param useConnType
	 * @param dbOp
	 */
	public static IBatchBarcodeService createBatchBarcodeServcie(int useConnType,DbOperation dbOp){
		return new BatchBarcodeServiceImpl(useConnType,dbOp);
	} 
	/**
	 * 功能:创建新数据库连接的Service
	 * <p>作者文齐辉 2011-1-6 下午12:09:10 
	 */
	public static IBatchBarcodeService createBatchBarcodeServcie(){
		return new BatchBarcodeServiceImpl();
	} 

//	// 供应商信息
//	public static ISupplierService createSupplierService() {
//		return new SupplierServiceImpl();
//	}

	public static ISupplierService createSupplierService(int useConnType,
			DbOperation dbOp) {
		return new SupplierServiceImpl(useConnType, dbOp);
	}

	/**
	 * 功能:订单地址下拉
	 * <p>
	 * 作者文齐辉 2011-3-2 上午10:10:00
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public static CityAreaService createCityAreaService()
			throws DatabaseException {
		return new CityAreaService();
	}
	/**
	 * 创建货位相关的Service
	 * @return
	 */
	public static ICargoService createCargoService(){
		return new CargoServiceImpl();
	}
	public static ICargoService createCargoService(
			int useConnType, DbOperation dbOp){
		return new CargoServiceImpl(useConnType,dbOp);
	}
	public static SMSWithOneDBServiceImpl createSMSWithOneDBService(
			int useConnType, DbOperation dbOp) {
		return new SMSWithOneDBServiceImpl(useConnType, dbOp);
	}

	public static SMSWithOneDBServiceImpl createSMSSMSWithOneDBService() {
		return new SMSWithOneDBServiceImpl();
	}
	//装箱管理
	public static CartonningInfoService createCartonningInfoService() {
		return new CartonningInfoService();
	}

	public static CartonningInfoService createCartonningInfoService(
			int useConnType, DbOperation dbOp) {
		return new CartonningInfoService(useConnType, dbOp);
	}
	//分拣批次管理
	public static SortingInfoService createSortingInfoService() {
		return new SortingInfoService();
	}

	public static SortingInfoService createSortingInfoService(
			int useConnType, DbOperation dbOp) {
		return new SortingInfoService(useConnType, dbOp);
	}
	//快递公司
	public static DeliverService createDeliverService() {
		return new DeliverService();
	}

	public static DeliverService createDeliverService(
			int useConnType, DbOperation dbOp) {
		return new DeliverService(useConnType, dbOp);
	}
	//部门和库地区库类型关系表
	public static CargoDeptAreaService createCargoDeptAreaService() {
		return new CargoDeptAreaService();
	}

	public static CargoDeptAreaService createCargoDeptAreaService(
			int useConnType, DbOperation dbOp) {
		return new CargoDeptAreaService(useConnType, dbOp);
	}
	//异常入库单
	public static WarehousingAbnormalService createWarehousingAbnormalService() {
		return new WarehousingAbnormalService();
	}

	public static WarehousingAbnormalService createWarehousingAbnormalService(
			int useConnType, DbOperation dbOp) {
		return new WarehousingAbnormalService(useConnType, dbOp);
	}
	
	public static IProductDiscountService createProductDiscountService(){
		return new ProductDiscountServiceImpl();
	}
	public static IProductDiscountService createProductDiscountService(
			int useConnType, DbOperation dbOp){
		return new ProductDiscountServiceImpl(useConnType,dbOp);
	}
	public static CheckOrderStatJobService createCheckOrderStatJobService() {
		return new CheckOrderStatJobService();
	}
	public static CheckOrderStatJobService createCheckOrderStatJobService(int useConnType, DbOperation dbOp){
		return new CheckOrderStatJobService();
	}
	public static OrderTransmitJobService createOrderTransmitJobService() {
		return new OrderTransmitJobService();
	}
	public static OrderTransmitJobService createOrderTransmitJobService(int useConnType, DbOperation dbOp) {
		return new OrderTransmitJobService();
	}
}
