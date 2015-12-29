package mmb.stock.stat;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.ware.WareService;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoModelBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.db.DbOperation;

public interface ReturnedPackageService extends IBaseService{

	
	
	
	/**
	 * 
	     * 此方法描述的是：  判断是否有操作对应退货上架单的权限
	     * @author: liubo  
	     * @version: 2013-2-28 下午05:36:24
	 */
	public boolean checkCollectBill(HttpServletRequest request, String billCode)throws Exception;
	/**
	 * 
	     * 此方法描述的是：  判断上架单的货位是否和选择的仓库向符合
	     * @author: shiyaunfei  
	     * @version: 2013-2-28 下午05:36:24
	 */
	public boolean pdaCheckCollectBill(HttpServletRequest request, String billCode,int area)throws Exception;
	/**
	 * 将包裹退货
	 * @param type，0正常入库，1订单和包裹不匹配异常入库，2订单中商品数量不足，异常入库
	 * @param orderCode
	 * @param productCode
	 * @param packageCode
	 * @return//result表示，0：成功，1：包裹单号与订单号不匹配，2：订单号与商品编号不匹配，3：继续录入缺失商品,4:订单号不存在
			  //5：包裹号不存在，6：商品条码不存，7：商品不属于订单,8:缺失商品录入完成
	 * @throws Exception
	 */
	String storagePackage(String type, String orderCode, 
			String productCode, String packageCode, 
			String exceptionPCode, String payFlag,voUser user,int wareArea) throws Exception;
	
	
	
	/**
	 * 获取包裹列表
	 * @param storageTime
	 * @param storageStatus
	 * @param deliver
	 * @param startIndex
	 * @param pageCount
	 * @param orderCode 
	 * @param packageCode 
	 * @return
	 * @throws Exception
	 */
	List queryPackage(String storageTime, String[] storageStatus, int deliver, int startIndex, int pageCount, String orderCode, String packageCode, int wareArea, String availAreaIds) throws Exception;



	/**
	 * 导出退货包裹信息
	 * @param deliver
	 * @param storageStatus
	 * @param storageTime
	 * @param pakcageCode 
	 * @param orderCode 
	 * @return
	 */
	public XSSFWorkbook exportPackage( List<ReturnedPackageBean> list) throws Exception;
	
	
	public String getQueryPackageSql(String storageTime,
			String[] storageStatus, int deliver, String orderCode, String packageCode, int wareArea, String availAreaIds);
	
	

	/**
	 * 根据货位id获取货位模型类
	 * @param cargoOperationId
	 * @return
	 */
//	CargoInfoModelBean getCargInfoModelById(int cargoOperationId);



	/**
	 * 退货上架单确认，成功后将状态改为已审核
	 * @param retShelfCode
	 * @return
	 */
	String confirmRetShelf(String retShelfCode, voUser user);



//	/**
//	 * 更新合格库锁定量
//	 * @param productId
//	 * @param stockCount
//	 * @return
//	 * @throws Exception 
//	 */
//	boolean updateProductStock(int productId, int stockCount,DbOperation dbop) throws Exception;



	/**
	 * 根据商品id获取商品code
	 * @param unSanningList
	 * @param dbOperation
	 * @return
	 */
	String getRetProductCode(List unSanningList, DbOperation dbOperation) throws Exception;



	/**
	 * 回滚退货上架单
	 * @param upShelfCode
	 * @param user 
	 * @return
	 */
	String rollbackRetShelf(String upShelfCode, voUser user, WareService wareService);

	
	
	/**
	 * 更新退货库相关信息
	 * @param dbop
	 * @param preCargoInfo
	 * @param curCargoInfo
	 * @param nextCargoInfo
	 * @throws Exception 
	 */
	void updateRetProductCount(DbOperation dbop, List preCargoInfo, List curCargoInfo, List nextCargoInfo) throws Exception;
	
	
	/**
	 * 增加退货库库存锁定量，减少可用库存
	 * @param dbop
	 * @param preCargoInfo
	 * @param curCargoInfo
	 * @param nextCargoInfo
	 * @return
	 */
	boolean lockProductStock(DbOperation dbop, List preCargoInfo, List curCargoInfo, List nextCargoInfo)throws Exception;
	
	
	/**
	 * 增加源货位库存锁定量，减去可用量
	 * @param dbop
	 * @param preCargoInfo
	 * @param curCargoInfo
	 * @param nextCargoInfo
	 * @return
	 */
	boolean lockCargoProductStock(List preCargoInfo, List curCargoInfo, List nextCargoInfo, ICargoService service)throws Exception;
	
	
	/**
	 * 增加目的货位空间锁定量
	 * @param preCargoInfo
	 * @param curCargoInfo
	 * @param nextCargoInfo
	 * @param service
	 * @return
	 * @throws Exception
	 */
	boolean lockCargoProductSpaceStock(List preCargoInfo, List curCargoInfo, List nextCargoInfo, ICargoService service)throws Exception;



	/**
	 * 获取退货上架单的数量
	 * @param cargoOpStatus
	 * @param cargoOpCode
	 * @param cargoCode
	 * @param productCode
	 * @param createUser 
	 * @return
	 */
	int getRetShelfCount(String[] cargoOpStatus, String cargoOpCode,
			String cargoCode, String productCode, String createUser)throws Exception;



	/**
	 * 获取退货操作列表
	 * @param cargoOpStatus
	 * @param cargoOpCode
	 * @param cargoCode
	 * @param productCode
	 * @param string 
	 * @param countPerPage 
	 * @param index 
	 * @param createUserName//制单人 
	 * @return
	 * @throws Exception
	 */
	List getRetShelfList(String[] cargoOpStatus, String cargoOpCode,
			String cargoCode, String productCode, int index, int countPerPage, String orderBy, String createUserName)throws Exception;



	
	/**
	 * 统计质检合格商品
	 * @param orderType
	 * @param passageCode
	 * @param storageCode
	 * @throws Exception 
	 */
	List statisticQualifiedRetProdcut(String orderType, String passageCode,
			String storageCode) throws Exception;



	/**
	 * 统计质检合格商品数量
	 * @param passageCode
	 * @param storageCode
	 * @return
	 * @throws Exception 
	 */
	int getStatisticRPCount(String passageCode, String storageCode) throws Exception;

	
	/**
	 * 获取汇总单下所有上架单的上架量
	 * @param code
	 * @param dbop
	 * @return
	 * @throws Exception 
	 */
	public int getAllProductCount(String code, DbOperation dbop) throws Exception;
	
	
	/**
	 * 获取退货汇总上架单详细信息
	 * @param parameter
	 * @return
	 */
	CargoInfoModelBean getCargInfoModelByUpShelfCode(String parameter);



	/**
	 * 更新货位库存完成商品数量，减少源货位锁定量，目的货位空间锁定量，目的货位库存，减少库存锁定量
	 * @param cargoInfoModel
	 * @param service
	 * @param wareService
	 * @param productservice
	 * @param cargoOpList 
	 * @param user 
	 * @return
	 * @throws Exception 
	 */
	String updateStockInfo(CargoInfoModelBean cargoInfoModel,
			ICargoService service, WareService wareService,
			IProductStockService productservice, List cargoOpList, voUser user) throws Exception;
	
	String updateStockInfoByProductList(List<voProduct> retProductList,
			ICargoService service, WareService wareService,
			IProductStockService productservice, List cargoOpList, voUser user) throws Exception;
	
	boolean rollbackRetProductCount(CargoOperationCargoBean cocBean, ICargoService cargoService);
	
	void constructSourceInfo(DbOperation dbop, List sourceCargoList,int wareArea,int productId)throws Exception;
	
	
	/**
	 * 保存汇总单
	 * @param user
	 * @param statService
	 * @return
	 * @throws Exception
	 */
	String saveReturnedUpShelf(voUser user, StatService statService, String passageWholeCode) throws Exception;



	/**
	 * 根据商品sku生成上架单和退货上架汇总单
	 * @param productCodeMap
	 * @param user
	 * @param date 
	 * @return
	 * @throws Exception 
	 */
	String generateUpShelf(Map productCodeMap, voUser user, String date) throws Exception;



	/**
	 * 作业完成退货汇总上架单
	 * @param rufBean
	 * @param user 
	 * @throws Exception
	 */
	String completeRetShelf(ReturnedUpShelfBean rufBean, voUser user, WareService wareService) throws Exception;



	/**
	 * 得到最优库区号
	 * @param allCargoOperationCargoList
	 * @return
	 */
	String getPassageCodeByMount(List allCargoOperationCargoList);


	/**
	 * 返回某一天生成汇总单统计信息
	 * @param createDate
	 * @return
	 * @throws Exception 
	 */
	String queryRetShelfStaticInfo(String createDate) throws Exception;
	
	/**
	 * 	质检时生成上架单，生成后状态为已提交， 库存，源货位库存，目的货位空间冻结量已锁定，各项已经锁定了
	 * @param user 用户类
	 * @param product 产品类
	 * @param mount 数量
	 * @param wareService
	 * @param service
	 * @param statService
	 * @param cargoService
	 * @return
	 */
	public CargoOperationCargoBean createUpShelfBill(voUser user, voProduct product, int mount,CargoInfoBean targetCargoInfo,WareService wareService,IProductStockService service,StatService statService,ICargoService cargoService, int wareArea) throws Exception;
	
	/**
	 * 完善 目的货位 对于 未开通的 开通，对于没有与对应商品绑定的添加上绑定关系
	 * @param cargoInfo
	 * @param product
	 * @param cargoService
	 * @param service
	 * @return
	 */
	public boolean dealTargetCargoAndProduct(CargoInfoBean cargoInfo, voProduct product, ICargoService cargoService, IProductStockService service);
	
	/**
	 * 方便连表查询 的组装实体类的方法
	 * @param sql
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<ReturnedPackageBean> getReturnedPackageListDirectly(String sql, int index, int count, String orderBy);
	
	/**
	 * 方便连表查询确定 数量的方法
	 * @param sqlCount
	 * @return
	 */
	public int getReturnedPackageCountDirectly(String sqlCount);
	
	/**
	 * 添加物流员工绩效考核关于退货上架汇总单的信息
	 * @param sqlCount
	 * @return
	 */
	public String addCargoStaffPerformanceForUpShelf(voUser user, Map retUpShelfMap, String date, ICargoService cargoService);
}
