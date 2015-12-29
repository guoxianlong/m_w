package mmb.stock.spare.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.ProductDao;
import mmb.rec.oper.bean.StockCardBean;
import mmb.rec.oper.dao.ProductStockDao;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.dao.IMEIBeanDao;
import mmb.stock.IMEI.dao.IMEILogBeanDao;
import mmb.stock.aftersale.AfterSaleBackSupplier;
import mmb.stock.aftersale.AfterSaleBackUserProduct;
import mmb.stock.aftersale.AfterSaleDetectLogBean;
import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.aftersale.AfterSaleDetectTypeBean;
import mmb.stock.aftersale.dao.AfterSaleBackSupplierDao;
import mmb.stock.aftersale.dao.AfterSaleBackUserProductDao;
import mmb.stock.aftersale.dao.AfterSaleDetectLogDao;
import mmb.stock.aftersale.dao.AfterSaleDetectProductDao;
import mmb.stock.fitting.dao.CargoInfoBeanDao;
import mmb.stock.fitting.dao.CargoProductStockBeanDao;
import mmb.stock.fitting.model.CargoInfoBean;
import mmb.stock.fitting.model.CargoProductStockBean;
import mmb.stock.fitting.model.CargoStockCardBean;
import mmb.stock.spare.dao.AfterSaleReplaceNewProductRecordDao;
import mmb.stock.spare.dao.ImeiSpareStockinBeanDao;
import mmb.stock.spare.dao.SpareBackSupplierDao;
import mmb.stock.spare.dao.SpareBackSupplierProductDao;
import mmb.stock.spare.dao.SpareBeanDao;
import mmb.stock.spare.dao.SpareCodeBeanDao;
import mmb.stock.spare.dao.SpareStockCardDao;
import mmb.stock.spare.dao.SpareStockinBeanDao;
import mmb.stock.spare.dao.SpareStockinProductBeanDao;
import mmb.stock.spare.dao.SpareUnqualifiedReplaceRecordDao;
import mmb.stock.spare.model.AfterSaleReplaceNewProductRecord;
import mmb.stock.spare.model.ImeiSpareStockinBean;
import mmb.stock.spare.model.SpareBackSupplier;
import mmb.stock.spare.model.SpareBackSupplierProduct;
import mmb.stock.spare.model.SpareBean;
import mmb.stock.spare.model.SpareCodeBean;
import mmb.stock.spare.model.SpareProductDetailed;
import mmb.stock.spare.model.SpareStockCard;
import mmb.stock.spare.model.SpareStockinBean;
import mmb.stock.spare.model.SpareStockinProductBean;
import mmb.stock.spare.model.SpareUnqualifiedReplaceRecord;
import mmb.stock.spare.model.SpareUpShelves;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class SpareService extends BaseServiceImpl{
	@Autowired
	private SpareCodeBeanDao spareCodeBeanDao;
	@Autowired
	private SpareBeanDao spareDao;
	@Autowired
	private SpareStockinBeanDao stockInDao;
	@Autowired
	private SpareStockinProductBeanDao stockInProductDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private SpareCodeBeanDao spareCodeDao;
	@Autowired
	private IMEIBeanDao imeiDao;
	@Autowired
	private IMEILogBeanDao imeiLogDao;
	@Autowired
	private ImeiSpareStockinBeanDao imeiSpareDao;
	@Autowired
	private SpareBackSupplierDao spareBackSuppliertDao;
	@Autowired
	private SpareBackSupplierProductDao spareBackSupplierProductDao;
	@Autowired
	private SpareStockCardDao spareStockCardDao;
	@Autowired
	private ProductStockDao productStockDao;
	@Autowired
	private CargoInfoBeanDao cargoInfoBeanDao;
	@Autowired
	private CargoProductStockBeanDao cargoProductStockBeanDao;
	@Autowired
	private AfterSaleBackSupplierDao afterSaleBackSupplierDao;
	@Autowired
	private AfterSaleReplaceNewProductRecordDao replaceRecordDao;
	@Autowired
	private AfterSaleDetectLogDao afterSaleDetectLogDao;
	@Autowired
	private AfterSaleDetectProductDao detectProductDao;
	@Autowired
	private AfterSaleBackUserProductDao backUserProductDao;
	@Autowired
	private SpareUnqualifiedReplaceRecordDao spareUnqualifiedReplaceRecordDao;
	/**
	 * 打印备用单号
	 * @auth aohaichen
	 */
	@Transactional(rollbackFor=Exception.class)
	public List<String> createSpareCode(int count) throws Exception{
		List<String> result = new ArrayList<String>();
		Map<String,String> map = new HashMap<String,String>();
		String code = DateUtil.getNow().substring(2, 10).replace("-", "");
		map.put("condition", "code like 'BYJ" + code + "%' order by id desc limit 1");
		List<SpareCodeBean> list = spareCodeBeanDao.getSpareCodeBean(map);
		SpareCodeBean scb =null;
		if(list!=null){
			for(int i = 0 ; i < list.size() ; i++){
				scb =list.get(i);
			}
		}
		int number = 0;
		
		if (scb != null) {
			// 获取当日计划编号最大值
			String _code = scb.getCode();
			number = Integer.parseInt(_code.substring(_code.length() - 6));
		}
		
		for (int i = 0; i < count; i++) {
			number++;
			String detectCode = "BYJ"+ code + String.format("%06d", new Object[] { new Integer(number) });
			SpareCodeBean bean = new SpareCodeBean();
			bean.setCode(detectCode);
			if (spareCodeBeanDao.insert(bean)<0) {
				return null;
			}
			result.add(detectCode);
		}
		return result;
	}
	
	/**
	 * 获取备用机
	 * @auth aohaichen
	 */
	public SpareBean getSpare(HashMap<String,String> map){
		return spareDao.getSpareByCondition(map);
	}
	
	/**
	 * 查询原备用机单号是否是已入库并且是未上架
	 * 1：已入库并且是未上架；0：否
	 * @auth aohaichen
	 */
	public int getSpareCodeStatus(Map<String,String> map){
		return spareDao.getSpareCargoStatus(map);
	}
	
	/**
	 * 更新原IMEI码状态
	 * @auth aohaichen
	 */
	public int upDateImeiStatus(Map<String,String> map){
		return imeiDao.updateIMEIByCondition(map);
	}	
	
	/**
	 * 添加IMEIlog日志
	 */
	public int addIMEIlog(IMEILogBean lib){
		return imeiLogDao.insert(lib);
	}
	
	/**
	 * 添加新的IMEI码
	 */
	public int addIMEI(IMEIBean lb){
		return imeiDao.insert(lb);
	}
	
	/**
	 * 更新IMEI码
	 */
	public int updateIMEI(Map<String,String> map){
		return imeiDao.updateIMEIByCondition(map);
	}
	
	/**
	 * 更新备用机单号
	 */
	public int updateSpareCode(Map<String,String> map){
		return spareCodeBeanDao.updateSpareCodeBeanByCondition(map);
	}
	
	/**
	 * 更新备用机入库IMEI码
	 */
	
	public int updateIMEISpareStockin(Map<String,String> map){
		return imeiSpareDao.updateIMEISpareStockinByCondition(map);
	}
	
	
	/**
	 * 添加返还供应商备用机商品
	 */
	public int addSpareBackSupplierProduct(Map<String,String> map){
		return useConnType;
	}
	/**
	 * 更新备用机状态
	 */
	public int upDateSpareStatus(Map<String,String> map){
		return spareDao.updateSpareStatus(map);
	}
	/**
	 * 添加备用机库存卡片
	 */
	public int addSpareStockCard(SpareStockCard ssc){
		return spareStockCardDao.insert(ssc);
	}
	/**
	 * 查productStock
	 */
	public ProductStockBean getProductStock(String condition){
		return productStockDao.getProductStock(condition);
	}
	
	/**
	 * 更新productStock可用库存
	 */
	public boolean updateStockCount(int id, int count){
		return productStockDao.updateStockCount(id,count);
	}
	
	/**
	 * 获取cargoInfo
	 */
	public CargoInfoBean getCargoInfo(String condition){
		return cargoInfoBeanDao.selectByCondition(condition);
	}
	

	/**
	 * 更新cargoProductStock可用库存
	 */
	public boolean updateCargoInfoProductStockCount(int id,int count){
		return cargoProductStockBeanDao.updateStockCount(id,count);
	}
	/**
	 * 获取cargoProductStock
	 */
	public CargoProductStockBean getCargoProductStock(String condition){
		return cargoProductStockBeanDao.selectByCondition(condition);
	}
	
	/**
	 * 添加StockCard进销存卡片
	 */
	public int insertStockCardBean(StockCardBean scb){
		return productStockDao.insertStockCardBean(scb);
	}
	
	/**
	 * 添加CargoStockCard进销存卡片
	 */
	public int insertCargoStockCardBean(CargoStockCardBean scb){
		return cargoProductStockBeanDao.insertCargoStockCardBean(scb);
	}
	
	public List<Map<String,String>> getSpareBackSupplierByCondition(Map<String,String> map){
		
		return spareBackSuppliertDao.getSpareBackSupplierByCondition(map);
		
	}
	public SpareBackSupplier getSpareBackSupplier(Integer id){
		
		return spareBackSuppliertDao.selectByPrimaryKey(id);
		
	}
	
	public List<Map<String,String>> getSpareBackSupplierproductByCondition(Map<String,String> map){
		return spareBackSupplierProductDao.getSpareBackSupplierproductByCondition(map);
	}
	
	public List<SpareBackSupplierProduct> getSpareBackSupplierproductJoinProduct(Map<String,String> map){
		return spareBackSupplierProductDao.getSpareBackSupplierproductJoinProduct(map);
	}
	
	public int getSpareBackSupplierByConditionForCount(Map<String,String> map){
		return spareBackSuppliertDao.getSpareBackSupplierByConditionForCount(map);
	}
	
	/**
	 * 备用机返厂打印清单
	 * ahc
	 */
	@Transactional(rollbackFor=Exception.class)
	public int printReturnFactory(HttpServletRequest request,HttpServletResponse response,
			String deleverId,String packageCode,String price,String ourAddress,String zipCode,String receiverName,String phone,
			String total,String remark,voUser user){
		HashMap<String,String> map = new HashMap<String,String>();
		LinkedHashSet<String> spareCodes = (LinkedHashSet<String>) request.getSession().getAttribute("SpareCodeSet");
		if(!(spareCodes!=null && spareCodes.size()>0)){
			throw new RuntimeException("没有添加备用机");
		}
		String CreateDatetime = DateUtil.getNow();//用于备用机库存卡片添加记录同步
		//新增备用机出库单
		String spareCode = "";
		for(String code :spareCodes){
			if(spareCode.length()<=0){
				spareCode = code;
			}
		}
		SpareBackSupplier spareBackSupplier = (SpareBackSupplier) request.getSession().getAttribute("spareBackSupplier");
		if(spareBackSupplier==null){
			throw new RuntimeException("没有添加备用机,无法获取厂商信息!");
		}
		int supplierId = spareBackSupplier.getSupplierId();
		SpareBackSupplier sbs = new SpareBackSupplier();
		sbs.setSupplierId(supplierId);
		sbs.setPackageCode(packageCode);
		sbs.setDeliveryCost(Float.parseFloat(price));
		sbs.setDeliveryId(Integer.parseInt(deleverId));
		sbs.setAgency("无锡买卖宝信息技术有限公司深圳售后服务中心");
		sbs.setOperateUserId(user.getId());
		sbs.setOperateUserName(user.getUsername());
		sbs.setOurAddress(ourAddress);
		sbs.setOurPost(zipCode);
		sbs.setReceiverName(receiverName);
		sbs.setContractPhone(phone);
		sbs.setRemark(remark);
		map.put("condition", "WHERE s.`code` = '"+spareCode+"'");//获取备用机出库地区，目前条件为以第一个备用机号的入口地区为准
		Map<String,String> ss =this.getSpareStockinAreaId(map);
		int areaId = Integer.parseInt(String.valueOf(ss.get("areaId")));
		sbs.setAreaId(areaId);
		sbs.setCount(Integer.parseInt(total));
		sbs.setCreateDatetime(DateUtil.getNow());
		int spareBackSupplierId =this.addSpareBackSupplier(sbs);
		if(spareBackSupplierId<=0){
			throw new RuntimeException("备用机出库失败");
		}
		//新增返还供应商备用机商品
		for(String code:spareCodes){
			SpareBackSupplierProduct sbslp = new SpareBackSupplierProduct();
			sbslp.setCode(code);
			map.put("condition","s.`code` = '"+code+"'");
			SpareProductDetailed spd = spareDao.getSpareProductDetailed(map);
			String imei = StringUtil.checkNull(spd.getImei()).trim();
			int productId = spd.getProductId();
			String cargoWholeCode = StringUtil.checkNull(spd.getCargoWholeCode()).trim();
			sbslp.setImei(imei);
			sbslp.setProductId(productId);
			sbslp.setSpareBackSupplierId(spareBackSupplierId);
			if(this.addSpareBackSupplierProduct(sbslp)<=0){
				throw new RuntimeException("添加备用机出库商品失败");
			}
			
			//减备用机库存、货位库存
			voProduct vProduct = productDao.getProduct("id=" + productId);
			if (vProduct == null) {
				throw new RuntimeException("商品不存在!");
			}
			StringBuffer sb = new StringBuffer();
			sb.append("product_id= "+productId+" and area= "+areaId+" and type="+ProductStockBean.STOCKTYPE_SPARE);
			ProductStockBean psb =this.getProductStock(sb.toString());
			if(psb==null || psb.getStock()<=0){
				throw new RuntimeException("备用机库存不足");
			}
			if(!this.updateStockCount(psb.getId(),-1)){//减备用机库存
				throw new RuntimeException("备用机库存不足");
			}
			CargoInfoBean cargoInfoBean =this.getCargoInfo(" whole_code ='" +cargoWholeCode+"'");
			if(cargoInfoBean==null){
				throw new RuntimeException("备用机货位库存不足");
			}
			int cargoInfoId =cargoInfoBean.getId();
			CargoProductStockBean cpsb =this.getCargoProductStock(" cargo_id = "+cargoInfoId + " and product_id =" +productId);
			if(cpsb==null || cpsb.getStockCount()<=0){
				throw new RuntimeException("备用机货位库存不足");
			}
			if(!this.updateCargoInfoProductStockCount(cpsb.getId(), -1)){//减货位库存
				throw new RuntimeException("备用机货位商品不足");
			}
			HashMap<String, String> psMap = new HashMap<String, String>();
			psMap.put("condition", "product_id=" + vProduct.getId());
			psMap.put("index", "-1");
			psMap.put("count", "-1");
			psMap.put("orderBy", null);
			vProduct.setPsList(this.productStockDao.getProductStockList(psMap));
			
			//增加进销存卡片
			StockCardBean outsc = new StockCardBean();
			outsc.setCardType(StockCardBean.CARDTYPE_SPARE_OUT);
			outsc.setCode(code);
			outsc.setCreateDatetime(DateUtil.getNow());
			outsc.setStockType(ProductStockBean.STOCKTYPE_SPARE);
			outsc.setStockArea(areaId);
			outsc.setProductId(productId);
			outsc.setStockId(psb.getId());
			outsc.setStockOutCount(1);
			outsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			outsc.setCurrentStock(vProduct.getStock(outsc.getStockArea(), outsc.getStockType()) + vProduct.getLockCount(outsc.getStockArea(), outsc.getStockType()));
			outsc.setStockAllArea(vProduct.getStock(outsc.getStockArea()) + vProduct.getLockCount(outsc.getStockType()));
			outsc.setStockAllType(vProduct.getStockAllType(outsc.getStockType()) + vProduct.getLockCountAllType(outsc.getStockType()));
			outsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			outsc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
			outsc.setAllStockPriceSum((new BigDecimal(outsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outsc.getStockPrice()))).doubleValue());
			if (this.insertStockCardBean(outsc) <= 0) {
				throw new RuntimeException("增加进销存卡片失败！");
			}
			
			//增加货位进销存卡片
			CargoStockCardBean outcsc = new CargoStockCardBean();
			outcsc.setCardType(CargoStockCardBean.CARDTYPE_SPARE_OUT);
			outcsc.setCode(code);
			outcsc.setCreateDatetime(DateUtil.getNow());
			outcsc.setStockType(cargoInfoBean.getStockType());
			outcsc.setStockArea(cargoInfoBean.getAreaId());
			outcsc.setProductId(vProduct.getId());
			outcsc.setStockId(cpsb.getId());
			outcsc.setStockOutCount(1);
			outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
			outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			outcsc.setCurrentCargoStock(cpsb.getStockCount() + cpsb.getStockLockCount());
			outcsc.setCargoStoreType(cargoInfoBean.getStoreType());
			outcsc.setCargoWholeCode(cargoInfoBean.getWholeCode());
			outcsc.setStockPrice(vProduct.getPrice5());
			outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
			if (this.cargoProductStockBeanDao.insertCargoStockCardBean(outcsc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
			
			//更新备用机状态为2--已返回厂商
			map.put("condition", "code= '"+code+"'");
			map.put("set", "set status= '"+SpareBean.STATUS_BACK_SUPPLIER+"'");
			if (this.upDateSpareStatus(map) <= 0) {
				throw new RuntimeException("更新备用机状态失败！");
			}
			
			//新增备用机库存卡片
			SpareStockCard ssc = new SpareStockCard();
			ssc.setSpareCode(code);
			ssc.setSupplierId(supplierId);
			ssc.setProductId(productId);
			ssc.setCount(1);
			ssc.setAreaId(areaId);
			ssc.setCreateDatetime(CreateDatetime);
			ssc.setOperateId(user.getId());
			ssc.setOperateUsername(user.getUsername());
			ssc.setType(SpareStockCard.TYPE_BACK_SUPPLIER);
			ssc.setOperateItemId(spareBackSupplierId);
			ssc.setOperateItemCode(packageCode);
			if (this.addSpareStockCard(ssc) <= 0) {
				throw new RuntimeException("新增备用机库存卡片失败！");
			}
			map.put("code", imei);
			IMEIBean imeiBean = imeiDao.getIMEIByCondition(map);
			if(imeiBean == null){
				throw new RuntimeException("IMEI码不存在！");
			}
			int preStatus = imeiBean.getStatus();
			map.clear();
			//更新相对应imei码状态为7--备用机返回供应商
			map.put("condition", "code= '"+imei+"'");
			map.put("set", " status= '"+IMEIBean.IMEISTATUS7+"'");
			if (this.updateIMEI(map) <= 0) {
				throw new RuntimeException("更新IMEI码状态失败！");
			}
			
			IMEILogBean log = new IMEILogBean();
			log.setCreateDatetime(DateUtil.getNow());
			log.setIMEI(imei);
			log.setOperType(IMEILogBean.OPERTYPE15);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			log.setOperCode(packageCode);//备用机返厂单的包裹号
			log.setContent("IMEI码状态由【"+ IMEIBean.IMEIStatusMap.get(preStatus) +"】变为【返还供应商】");
			int num = imeiLogDao.insert(log);
			if(num==0){
				throw new RuntimeException("新增IMEI码日志失败！");
			}
		}
		request.getSession().removeAttribute("productDetailMap");
		request.getSession().removeAttribute("SpareCodeSet");
		request.getSession().removeAttribute("spareBackSupplier");
		return spareBackSupplierId;
	}
	
	/**
	 * 备用机更换
	 * @auth aohaichen
	 */
	@Transactional(rollbackFor=Exception.class)
	public void replacement(HttpServletRequest request,HttpServletResponse response,String oldSpareCode,String oldImei,String newSpareCode,String newImei,voUser user) throws Exception{
		HashMap<String,String> map = new HashMap<String,String>();
		//判断原备用机是否可使用
		map.put("code", oldSpareCode);
		SpareBean oldSpare = spareDao.getSpareByCondition(map);
		if(oldSpare!=null && oldSpare.getStatus()==SpareBean.STATUS_BACK_SUPPLIER){
			throw new RuntimeException("此备用机状态是不可使用, 不能更换!");
		}
		
		//查询原备用机单号是否是已入库并且是未上架
		map.clear();
		map.put("condition", "ssp.code='"+oldSpareCode+"' AND ci.store_type = "+CargoInfoBean.STORE_TYPE2+"");
		if(this.getSpareCodeStatus(map)==0){
			throw new RuntimeException("备用机"+ oldSpareCode +"不是未上架的备用机!");
		}
		//判断是否应该添IMEI码
		boolean hasIMEI = false;
		int count = imeiDao.getImeiProductId(oldSpare.getProductId());
		if(count == 1){
			hasIMEI = true;
		}
		oldImei = StringUtil.checkNull(oldImei).trim();
		newImei = StringUtil.checkNull(newImei).trim();
		if(hasIMEI){
			if(oldImei.equals("")){
				throw new RuntimeException("此备用机关联的商品属于IMEI商品,imei码不能为空!");
			}
		}else{
			if(!oldImei.equals("")){
				throw new RuntimeException("非IMEI码商品不能添写imei码商品!");
			}
		}
		map.clear();
		map.put("code", newSpareCode);
		//新备用机号是否是未使用
		SpareCodeBean codeBean = spareCodeBeanDao.getSpareCodeByCondition(map);
		if(codeBean.getStatus()==SpareCodeBean.STATUS_USE){
			throw new RuntimeException("新备用机编号已使用, 不能更换!");
		}
		
		if(hasIMEI){
			if(newImei.equals("")){
				throw new RuntimeException("此备用机关联的商品属于IMEI商品,imei码不能为空!");
			}
		}else{
			if(!newImei.equals("")){
				throw new RuntimeException("非IMEI码商品不能添写imei码商品!");
			}
		}
		SpareBean replaceSpare = replaceSpare(oldSpareCode,newSpareCode, newImei, user,oldSpare,hasIMEI);
		//新增备用机检测不合格更换单、备用机出入库卡片
		addReplaceRecordAndCard(user, oldSpare,replaceSpare);
	}

	private SpareBean replaceSpare(String oldSpareCode, String newSpareCode, String newImei, voUser user,
			SpareBean oldSpare,boolean hasIMEI) {
		HashMap<String, String> map = new HashMap<String,String>();
		//新增备用机入库
		SpareBean replaceSpare = new SpareBean();
		replaceSpare.setCode(newSpareCode);
		replaceSpare.setImei(newImei);
		replaceSpare.setCargoWholeCode(oldSpare.getCargoWholeCode());
		replaceSpare.setProductId(oldSpare.getProductId());
		replaceSpare.setSpareStockinId(oldSpare.getSpareStockinId());
		replaceSpare.setStatus(SpareBean.STATUS_STOCK_OUT);
		int replaceSpareId = spareDao.insert(replaceSpare);
		if(replaceSpareId==0){
			throw new RuntimeException("新增更换备用机失败!");
		}
		//更新新备用机号状态
		map.put("condition", "code= '"+newSpareCode+"'");
		map.put("set", "status= '"+SpareCodeBean.STATUS_USE+"'");
		if(this.updateSpareCode(map)==0){
			throw new RuntimeException("更新备用机号状态失败");
		}
		//更新原备用机状态返还供应商
		oldSpare.setStatus(SpareBean.STATUS_BACK_SUPPLIER);
		int num = spareDao.updateByPrimaryKeySelective(oldSpare);
		if(num==0){
			throw new RuntimeException("更新原备用机状态失败！");
		}
		if(hasIMEI){
			//新imei码是否存在库中，若存在则状态必须为已返还厂商才可以更换
			map.clear();
			map.put("code", newImei);
			IMEIBean newImeiBean = imeiDao.getIMEIByCondition(map);
			//true--新增imei，false--更新imei状态
			boolean flag = true;
			if(newImeiBean!=null){
				if(newImeiBean.getStatus()!=IMEIBean.IMEISTATUS7){
					throw new RuntimeException(newImei + "此IMEI码已使用,不能更换!");
				}
				flag = false;
			}
			
			if(flag){
				//添加新IMEI码
				IMEIBean imeiBean = new IMEIBean();
				imeiBean.setCode(newImei);
				imeiBean.setStatus(IMEIBean.IMEISTATUS2);
				imeiBean.setProductId(replaceSpare.getProductId());
				imeiBean.setCreateDatetime(DateUtil.getNow());
				if(this.addIMEI(imeiBean)==0){
					throw new RuntimeException("添加新IMEI码操作失败！");
				}
			}else{
				map.clear();
				map.put("code", newImei);
				IMEIBean imeiBean = imeiDao.getIMEIByCondition(map);
				imeiBean.setStatus(IMEIBean.IMEISTATUS2);
				if(imeiDao.updateByPrimaryKey(imeiBean)==0){
					throw new RuntimeException("更新IMEI码操作失败！");
				}
			}
			//添加IMEIlogo日志
			IMEILogBean imeiLogBean2 = new IMEILogBean();
			imeiLogBean2.setOperCode(newSpareCode);
			imeiLogBean2.setOperType(IMEILogBean.OPERTYPE13);
			imeiLogBean2.setIMEI(newImei);
			imeiLogBean2.setUserId(user.getId());
			imeiLogBean2.setUserName(user.getUsername());
			imeiLogBean2.setCreateDatetime(DateUtil.getNow());
			if(flag){
				imeiLogBean2.setContent("备用机入库");
			}else{
				imeiLogBean2.setContent("imei码[" + newImei + "]由[返回供应商]变成[可出库]");
			}
			if(this.addIMEIlog(imeiLogBean2)==0){
				throw new RuntimeException("添加IMEIlogo日志操作失败！");
			}
			//更新旧imei码状态
			map.clear();
			map.put("condition","code= '"+oldSpare.getImei()+"'");
			map.put("set", "status= '"+IMEIBean.IMEISTATUS7+"'");
			if(this.upDateImeiStatus(map)==0){
				throw new RuntimeException("原IMEI码状态更新失败！");
			}
			//添加IMEIlogo日志
			IMEILogBean imeiLogBean = new IMEILogBean();
			imeiLogBean.setOperCode(oldSpareCode);
			imeiLogBean.setOperType(IMEILogBean.OPERTYPE13);
			imeiLogBean.setIMEI(oldSpare.getImei());
			imeiLogBean.setContent("检测不合格备用机更换");
			imeiLogBean.setUserId(user.getId());
			imeiLogBean.setUserName(user.getUsername());
			imeiLogBean.setCreateDatetime(DateUtil.getNow());
			if(this.addIMEIlog(imeiLogBean)==0){
				throw new RuntimeException("添加IMEIlogo日志操作失败！");
			}
		}
		return replaceSpare;
	}

	/**
	 * 新增备用机检测不合格更换单、备用机出入库卡片
	 * @param user
	 * @param oldSpare
	 * @param replaceSpare
	 * 2014-11-7
	 * lining
	 */
	private void addReplaceRecordAndCard(voUser user, SpareBean oldSpare,SpareBean replaceSpare) {
		int num = 0;
		SpareUnqualifiedReplaceRecord record = new SpareUnqualifiedReplaceRecord();
		record.setOriSpareCode(oldSpare.getCode());
		record.setOriSpareId(oldSpare.getId());
		record.setOriSpareStockinId(oldSpare.getSpareStockinId());
		record.setReplaceSpareCode(replaceSpare.getCode());
		record.setReplaceSpareId(replaceSpare.getId());
		record.setCreateDatetime(DateUtil.getNow());
		record.setOperateId(user.getId());
		record.setOperateUsername(user.getUsername());
		int recordId = spareUnqualifiedReplaceRecordDao.insert(record);
		if(recordId==0){
			throw new RuntimeException("添加备用机检测不合格更换单操作失败！");
		}
		SpareStockinBean stockin = stockInDao.selectByPrimaryKey(oldSpare.getSpareStockinId());
		if(stockin==null){
			throw new RuntimeException("原备用机的入库单不存在!");
		}
		String datetime = DateUtil.getNow();
		SpareStockCard spareCard = new SpareStockCard();
		spareCard.setAreaId(stockin.getAreaId());
		spareCard.setCount(1);
		spareCard.setCreateDatetime(datetime);
		spareCard.setOperateId(user.getId());
		spareCard.setOperateUsername(user.getUsername());
		spareCard.setOperateItemId(record.getId());
		spareCard.setOperateItemCode("");
		spareCard.setProductId(oldSpare.getProductId());
		spareCard.setSpareCode(oldSpare.getCode());
		spareCard.setSupplierId(stockin.getSupplierId());
		spareCard.setType(SpareStockCard.TYPE_UNQUALIFIED_REPLACE_STOUCTOUT);
		num = spareStockCardDao.insert(spareCard);
		if(num==0){
			throw new RuntimeException("新增备用机库存卡片失败!");
		}
		spareCard.setSpareCode(replaceSpare.getCode());
		spareCard.setType(SpareStockCard.TYPE_UNQUALIFIED_REPLACE_STOUCTIN);
		num = spareStockCardDao.insert(spareCard);
		if(num==0){
			throw new RuntimeException("新增备用机库存卡片失败!");
		}
	}
	
	/**
	 * 检查是否有错误的备用机单号
	 * @auth aohaichen
	 */
	public String cheakSpareCode(String[] spareCodes,Set<String> set,HttpServletRequest request,voUser user){
		HashMap<String,String> condition = new HashMap<String,String>();
		for(String code :spareCodes){
			condition.put("code",code);
			SpareBean spare = spareDao.getSpareByCondition(condition);
			if(spare==null){
				return "备用机["+code+"]不存在!";
			}
			if(spare.getStatus()==SpareBean.STATUS_BACK_SUPPLIER){
				return "备用机["+code+"]已返还供应商!";
			}
			if(set.contains(code)){
				return "备用机号["+code+"]已添加";
			}else{
				set.add(code);
			}
			condition.clear();
		}
		request.getSession().setAttribute("SpareCodeSet",set);
		
		int supplierId = 0;
		//判断是否是同一个供应商
		for(String code : spareCodes){
			condition.put("condition","s.`code` ='"+code+"'");
			int temp =this.getSupplierIdBySpareCode(condition);
			if(supplierId==0){
				supplierId = temp;
			}else{
				if(temp != supplierId){
					return "备用机"+code+"不属于同一供应商的备用机!";
				}
			}
			condition.clear();
		}
		condition.put("condition","s.`code` ='"+spareCodes[0]+"'");
		Map<String,String> SupplierNameAndAddress = spareDao.getSupplierNameAndAddressBySpareCode(condition);
		String address = StringUtil.checkNull(SupplierNameAndAddress.get("address"));
		if(address.equals("")){
			return "请先设置厂商地址!";
		}
		SpareBackSupplier spareBackSupplier = new SpareBackSupplier();
		spareBackSupplier.setSupplierAddress(address);
		spareBackSupplier.setSupplierName(StringUtil.checkNull(SupplierNameAndAddress.get("name")));
		spareBackSupplier.setSupplierId(supplierId);
		spareBackSupplier.setOperateUserName(user.getUsername());
		spareBackSupplier.setCreateDate(DateUtil.getNowDateStr());
		request.getSession().setAttribute("spareBackSupplier", spareBackSupplier);
		return null;
	}
	/**
	 * 拼接备用机详细清单
	 * @auth aohaichen
	 */
	public void addSpareCode(String[] spareCodes,Map<Integer,SpareProductDetailed> map){
		for(String code :spareCodes){
			Map<String,String> condition = new HashMap<String,String>();
			condition.put("condition", "s.`code` = '"+code+"' AND s.`status`="+SpareBean.STATUS_STOCK_OUT+"");
			SpareProductDetailed spd = spareDao.getSpareProductDetailed(condition);
			int productId = spd.getProductId();
			if(map.containsKey(productId)){
				SpareProductDetailed bean = map.get(productId);
				String imei = StringUtil.checkNull(bean.getImei()).trim();
				String spareCode = StringUtil.checkNull(bean.getSpareCode()).trim();
				StringBuffer imeiBuffer = new StringBuffer(imei);//存放imei
				StringBuffer spareCodeBuffer = new StringBuffer(spareCode);//存放spareCode
				if(imei.length()>0 && StringUtil.checkNull(spd.getImei()).trim().length()>0){
					imeiBuffer.append(";");
					imeiBuffer.append(StringUtil.checkNull(spd.getImei()).trim());
				}
				
				if(spareCode.length()>0 && StringUtil.checkNull(code).trim().length()>0){
					spareCodeBuffer.append(";");
					spareCodeBuffer.append(StringUtil.checkNull(code).trim());
				}
				bean.setImei(imeiBuffer.toString());
				bean.setSpareCode(spareCodeBuffer.toString());
				int count = bean.getSpareCode().split(";").length;
				bean.setCount(count);
				map.put(productId,bean);
			}else{
				map.put(productId, spd);
			}
		}
	}
	
	/**
	 * 根据备用机单号获取供应商ID
	 */
	public int getSupplierIdBySpareCode(Map<String,String> map){
		
		return spareDao.getSupplierIdBySpareCode(map);
	}
	
	
	/**
	 * 获取供应商ID
	 * @auth aohaichen
	 */
	public Map<String,String> getSupplierId(Map<String,String> map){
		return stockInDao.getSupplierId(map);
	}
	
	/**
	 * 获取入库地区ID
	 * @auth aohaichen
	 */
	public Map<String,String> getSpareStockinAreaId(Map<String,String> map){
		return stockInDao.getSpareStockinAreaId(map);
	}
	/**
	 * 添加备用机出库
	 * @auth aohaichen
	 */
	public int addSpareBackSupplier(SpareBackSupplier sbsl){
		return spareBackSuppliertDao.insert(sbsl);
	}
	
	/**
	 * 新增返还供应商备用机商品
	 * @auth aohaichen
	 */
	public int addSpareBackSupplierProduct(SpareBackSupplierProduct sbsl){
		return spareBackSupplierProductDao.insert(sbsl);
	}
	
	/**
	 * 获取备用机上架列表
	 * @author aohaichen
	 */
	public List<SpareUpShelves> getSpareUpShelfList(Map<String,String> map){
		return spareDao.getSpareUpShelfList(map);
		
	}
	
	/**
	 * 新增备用机入库单
	 * @param stockin
	 * @param productCode
	 * @param spareCode
	 * @param imeiCode
	 * @param spareCodeAdd
	 * @param imeiCodeAdd
	 * @param user
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String addSpareStockIn(SpareStockinBean stockin,String productCode,List<String> spareCodeList,List<String> imeiList,voUser user,boolean hasIMEI){
		voProduct product = this.productDao.getProduct(" code = '" + productCode + "' ");
		if (product == null) {
			throw new RuntimeException("商品不存在");
		}
		String code = createCode();
		stockin.setCode(code);
		stockin.setProductId(product.getId());
		stockin.setCreateDatetime(DateUtil.getNow());
		int stockinId = stockInDao.insert(stockin);
		if(stockinId == 0){
			throw new RuntimeException("新增备用机入库单失败!");
		}
		stockin.setId(stockinId);
		List<SpareStockinProductBean> spareProductList = new ArrayList<SpareStockinProductBean>();
		//用于批量更新
		List<SpareCodeBean> spareCodeUpdateList = new ArrayList<SpareCodeBean>();
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		//需要验证备用机号和IMIE码
		//验证规则：备用机号是未使用的，IMEI码是库里没有的或者IMEI码已在库中，IMEI码的状态为已返回供应商
		if(spareCodeList!=null && spareCodeList.size()>0 && imeiList!=null && imeiList.size()>0){
			for(int i=0;i<spareCodeList.size();i++){
				String spareCodeItem = StringUtil.checkNull(spareCodeList.get(i));
				String imeiCodeItem = StringUtil.checkNull(imeiList.get(i));
				conditionMap.clear();
				conditionMap.put("code", spareCodeItem);
				SpareCodeBean scBean = spareCodeDao.getSpareCodeByCondition(conditionMap);
				if(scBean==null){
					throw new RuntimeException(spareCodeItem + "此备用机号不存在!");
				}
				if(scBean.getStatus()==SpareCodeBean.STATUS_USE){
					throw new RuntimeException(spareCodeItem + "此备用机号已使用!");
				}
				scBean.setStatus(SpareCodeBean.STATUS_USE);
				spareCodeUpdateList.add(scBean);
				if(hasIMEI){
					conditionMap.clear();
					conditionMap.put("code", imeiCodeItem);
					IMEIBean imeiBean = imeiDao.getIMEIByCondition(conditionMap);
					if(imeiBean!=null){
						if(imeiBean.getStatus()!=IMEIBean.IMEISTATUS7){
							throw new RuntimeException(imeiCodeItem + "此IMEI码已使用!");
						}
					}
				}
				SpareStockinProductBean bean = new SpareStockinProductBean();
				bean.setProductId(product.getId());
				bean.setSpareStockinId(stockinId);
				bean.setCode(spareCodeItem);
				bean.setImei(imeiCodeItem);
				spareProductList.add(bean);
			}
		}
		if(spareProductList!=null && spareProductList.size()>0){
			int num = stockInProductDao.batchAddSpareStockinProducts(spareProductList);
			if(num==0){
				throw new RuntimeException("新增备用机入库单关系表失败!");
			}
		}
		//批量更新备用机号状态
		if(spareCodeUpdateList!=null && spareCodeUpdateList.size()>0){
			int num = spareCodeBeanDao.batchUpdateSpareCodeStatus(spareCodeUpdateList);
			if(num==0){
				throw new RuntimeException("更新备用机号状态失败!");
			}
		}
		return "生成入库单成功";
	}


	/**
	 * 功能：生成备用机入库单编号
	 * 生成规则：//BR+六位日期+三位流水号，例如BR140901001
	 * @author lining
	 * @return
	 */
	private String createCode() {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String code = "BR" + sdf.format(cal.getTime()).replace("-", "");//盘点编号
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		conditionMap.put("condition", " and code like '" + StringUtil.toSql(code) + "%'");
		conditionMap.put("orderBy", " id desc ");
		conditionMap.put("index", "-1");
		conditionMap.put("count", "-1");
		List<SpareStockinBean> spareStockInList = stockInDao.getSpareStockinList(conditionMap);
		if(spareStockInList==null||spareStockInList.size()==0){
			code+="001";
		}else{
			String _code = spareStockInList.get(0).getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-3));
			number++;
			code += String.format("%03d",new Object[]{new Integer(number)});
		}
		return code;
	}
	
	/**
	 * 获取入库单列表
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 * 2014-10-24
	 * lining
	 */
	public List<SpareStockinBean> getSpareStockInList(String condition,int index,int count,String orderBy){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		if(condition!=null && condition.length()>0){
			conditionMap.put("condition", condition);
		}
		conditionMap.put("index", String.valueOf(index));
		conditionMap.put("count", String.valueOf(count));
		if(orderBy!=null && orderBy.length()>0){
			conditionMap.put("orderBy", orderBy);
		}
		return stockInDao.getSpareStockinListJoinProduct(conditionMap);
	}
	
	public int getSpareStockInCount(String condition){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		conditionMap.put("condition", condition);
		return stockInDao.getSpareStockInCount(conditionMap);
	}
	
	/**
	 * 获取入库单中的备用机号、imei码
	 * @param stockinId
	 * @return
	 * 2014-10-24
	 * lining
	 */
	public List<SpareStockinProductBean> getSpareStockinProductList(int stockinId){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		conditionMap.put("spareStockinId", String.valueOf(stockinId));
		return stockInProductDao.getSpareStockinProductBeans(conditionMap);
	}
	
	/**
	 * 审核入库单
	 * @param audit
	 * @param remark
	 * @param stockinId
	 * @return
	 * 2014-10-24
	 * lining
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String auditSocktIn(String audit,String remark,int stockinId,voUser user){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		int num = 0;
		if(audit.equals("1")){//审核通过
			List<SpareStockinProductBean> spareProductList = this.getSpareStockinProductList(stockinId);
			if(spareProductList!=null && spareProductList.size()>0){
				for(int i=0;i<spareProductList.size();i++){
					String spareCode = spareProductList.get(i).getCode();
					conditionMap.put("code", spareCode);
					SpareBean spare = spareDao.getSpareByCondition(conditionMap);
					//判断相对应的备用机号是否是未使用
					if(spare!=null){
						throw new RuntimeException(spareCode + "备用机已经存在,不能重复新增!");
					}
				}
			}
			SpareStockinBean stockInBean = stockInDao.selectByPrimaryKey(stockinId);
			if(stockInBean==null){
				throw new RuntimeException("此入库单不存在!");
			}
			voProduct product = productDao.getProduct("id=" + stockInBean.getProductId());
			if (product == null) {
				throw new RuntimeException("商品不存在!");
			}
			//增加备用机库存，货位库存
			CargoInfoBean cargo = addStockInCount(stockInBean,product);
			//新增备用机,新增imei记录，status=1入库中，imei的日志，新增imei_spare_stockin记录
			List<IMEIBean> imeiList = new ArrayList<IMEIBean>();//新增
			List<IMEILogBean> imeiLogList = new ArrayList<IMEILogBean>();
			List<IMEIBean> updateImeiList = new ArrayList<IMEIBean>();//更新
			List<ImeiSpareStockinBean> imeiStockInList = new ArrayList<ImeiSpareStockinBean>();
			if(spareProductList!=null && spareProductList.size()>0){
				//判断是否应该添IMEI码
				boolean hasIMEI = false;
				int count = imeiDao.getImeiProductId(spareProductList.get(0).getProductId());
				if(count == 1){
					hasIMEI = true;
				}
				for(int i=0;i<spareProductList.size();i++){
					String imei = spareProductList.get(i).getImei();
					SpareBean bean = new SpareBean();
					bean.setCargoWholeCode(cargo.getWholeCode());
					bean.setCode(spareProductList.get(i).getCode());
					bean.setImei(imei);
					bean.setProductId(product.getId());
					bean.setSpareStockinId(stockinId);
					bean.setStatus(SpareBean.STATUS_STOCK_OUT);
					int spareId = spareDao.insert(bean);
					if(spareId==0){
						throw new RuntimeException("新增备用机失败!");
					}
					
					if(hasIMEI){
						conditionMap.clear();
						conditionMap.put("code", imei);
						IMEIBean imeiBean = imeiDao.getIMEIByCondition(conditionMap);
						//true--imei新增，false--imei更新
						boolean flag = true;
						if(imeiBean!=null){
							if(imeiBean.status==IMEIBean.IMEISTATUS7){
								flag = false;
							}
							if(imeiBean.status==IMEIBean.IMEISTATUS2){
								throw new RuntimeException("此IMEI码已经是可出库状态了,不可重复添加!");
							}
						}
						if(flag){
							imeiBean = new IMEIBean();
							imeiBean.setCode(imei);
							imeiBean.setCreateDatetime(DateUtil.getNow());
							imeiBean.setProductId(product.getId());
							imeiBean.setStatus(IMEIBean.IMEISTATUS2);
							imeiList.add(imeiBean);
						}else{
							imeiBean.setStatus(IMEIBean.IMEISTATUS2);
							updateImeiList.add(imeiBean);
						}
						
						IMEILogBean log = new IMEILogBean();
						log.setCreateDatetime(DateUtil.getNow());
						log.setIMEI(imei);
						log.setOperType(IMEILogBean.OPERTYPE11);
						log.setUserId(user.getId());
						log.setUserName(user.getUsername());
						log.setOperCode(stockInBean.getCode());//备用机入库单的编号
						if(flag){
							log.setContent("备用机入库");
						}else{
							log.setContent("IMEI码状态由【返还供应商】变为【可出库】");
						}
						imeiLogList.add(log);
						
						ImeiSpareStockinBean issbBean = new ImeiSpareStockinBean();
						issbBean.setImei(imei);
						issbBean.setProductId(product.getId());
						issbBean.setSpareStockinId(stockinId);
						issbBean.setSpareStockinProductId(spareId);
						imeiStockInList.add(issbBean);
					}
				}
			}
			if(imeiList!=null && imeiList.size()>0){
				num = imeiDao.batchInsertIMEI(imeiList);
				if(num==0){
					throw new RuntimeException("新增imei码失败!");
				}
			}
			if(updateImeiList!=null && updateImeiList.size()>0){
				num = imeiDao.batchUpdateIMEIStatus(updateImeiList);
				if(num==0){
					throw new RuntimeException("更新imei码失败!");
				}
			}
			if(imeiStockInList!=null && imeiStockInList.size()>0){
				num = imeiSpareDao.batchInsertBean(imeiStockInList);
				if(num==0){
					throw new RuntimeException("新增imei码备用机入库单关系失败!");
				}
			}
			if(imeiLogList!=null && imeiLogList.size()>0){
				num = imeiLogDao.batchInsertIMEILog(imeiLogList);
				if(num==0){
					throw new RuntimeException("新增imei码日志失败!");
				}
			}
			auditSpareStockin(remark, stockinId, user,SpareStockinBean.STATUS_COMPLETE,spareProductList);
		}else if(audit.equals("0")){//审核不通过
			//入库单状态更新为审核不通过
			auditSpareStockin(remark, stockinId, user,SpareStockinBean.STATUS_NUN,null);
		}
		return "审核入库单成功!";
	}

	/**
	 * 审核入库单,增加备用机库存卡片
	 * @param remark
	 * @param stockinId
	 * @param user
	 * @param status
	 * 2014-10-27
	 * lining
	 */
	private void auditSpareStockin(String remark, int stockinId, voUser user,int status,List<SpareStockinProductBean> spareProductList) {
		SpareStockinBean bean = stockInDao.selectByPrimaryKey(stockinId);
		if(bean==null){
			throw new RuntimeException("此入库单不存在!");
		}
		bean.setStatus(status);
		bean.setId(stockinId);
		bean.setAuditRemark(remark);
		bean.setAuditDatetime(DateUtil.getNow());
		bean.setAuditUserId(user.getId());
		bean.setAuditUserName(user.getUsername());
		int num = stockInDao.updateByPrimaryKeySelective(bean);
		if(num==0){
			throw new RuntimeException("更新入库单失败!");
		}
		if(status==SpareStockinBean.STATUS_COMPLETE){
			//插入备用机库存卡片
			if(spareProductList!=null && spareProductList.size()>0){
				List<SpareStockCard> list = new ArrayList<SpareStockCard>();
				String datetime = DateUtil.getNow();
				for(int i=0;i<spareProductList.size();i++){
					SpareStockinProductBean spareBean = spareProductList.get(i);
					SpareStockCard card = new SpareStockCard();
					card.setAreaId(bean.getAreaId());
					card.setCount(1);
					card.setCreateDatetime(datetime);
					card.setOperateId(user.getId());
					card.setOperateUsername(user.getUsername());
					card.setOperateItemId(bean.getId());
					card.setOperateItemCode(bean.getCode());
					card.setSupplierId(bean.getSupplierId());
					card.setSpareCode(spareBean.getCode());
					card.setType(SpareStockCard.TYPE_STOCKIN);
					card.setProductId(bean.getProductId());
					list.add(card);
				}
				if(list!=null && list.size()>0){
					num = spareStockCardDao.batchInsertCard(list);
					if(num==0){
						throw new RuntimeException("批量插入备用机卡片失败!");
					}
				}
			}
		}
	}

	/**
	 * 增加备用机库存、货位库存增加库存、货位库存进销存卡片，库存均价不用计算
	 * @param stockInBean
	 * 2014-10-27
	 * lining
	 */
	private CargoInfoBean addStockInCount(SpareStockinBean stockInBean,voProduct product) {

		StringBuffer sbCondition = new StringBuffer();
		sbCondition.append(" area_id = ").append(stockInBean.getAreaId());
		sbCondition.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_SPARE);
		sbCondition.append(" AND store_type = ").append(CargoInfoBean.STORE_TYPE2);
		CargoInfoBean cargo = cargoInfoBeanDao.selectByCondition(sbCondition.toString());
		if(cargo == null){
			throw new RuntimeException("未找到[备用机库缓存区]货位");
		}
		sbCondition.setLength(0);
		sbCondition.append(" cargo_id = ").append(cargo.getId());
		sbCondition.append(" AND product_id = ").append(product.getId());
		CargoProductStockBean cpsIn = this.cargoProductStockBeanDao.selectByCondition(sbCondition.toString());
		if (cpsIn == null) {
			cpsIn = new CargoProductStockBean();
			cpsIn.setCargoId(cargo.getId());
			cpsIn.setProductId(product.getId());
			cpsIn.setStockCount(stockInBean.getCount());
			if (this.cargoProductStockBeanDao.insert(cpsIn) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
			cpsIn.setStockCount(0);
		} else {
			if (!this.cargoProductStockBeanDao.updateStockCount(cpsIn.getId(), stockInBean.getCount())) {
				throw new RuntimeException("数据库操作失败");
			}
		}

		sbCondition.setLength(0);
		sbCondition.append(" product_id = ").append(product.getId());
		sbCondition.append(" AND area = ").append(stockInBean.getAreaId());
		sbCondition.append(" AND type = ").append(ProductStockBean.STOCKTYPE_SPARE);

		ProductStockBean psBean = this.productStockDao.getProductStock(sbCondition.toString());
		if(psBean==null){
			throw new RuntimeException("商品不存在备用机库存!");
		}
		if (!this.productStockDao.updateStockCount(psBean.getId(), stockInBean.getCount())) {
			throw new RuntimeException("数据库操作失败");
		}
		
		HashMap<String, String> psMap = new HashMap<String, String>();
		psMap.put("condition", "product_id=" + product.getId());
		psMap.put("index", "-1");
		psMap.put("count", "-1");
		psMap.put("orderBy", null);
		product.setPsList(this.productStockDao.getProductStockList(psMap));
		
		CargoStockCardBean cargoStockCard = new CargoStockCardBean();
		cargoStockCard.setCardType(CargoStockCardBean.CARDTYPE_SPARE_IN);
		cargoStockCard.setCode(stockInBean.getCode());
		cargoStockCard.setCreateDatetime(DateUtil.getNow());
		cargoStockCard.setStockType(cargo.getStockType());
		cargoStockCard.setStockArea(cargo.getAreaId());
		cargoStockCard.setProductId(product.getId());
		cargoStockCard.setStockId(cpsIn.getId());
		cargoStockCard.setStockInCount(stockInBean.getCount());
		cargoStockCard.setStockInPriceSum((new BigDecimal(stockInBean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		cargoStockCard.setCurrentStock(product.getStock(cargoStockCard.getStockArea(), cargoStockCard.getStockType()) + product.getLockCount(cargoStockCard.getStockArea(), cargoStockCard.getStockType()));
		cargoStockCard.setAllStock(product.getStockAll() + product.getLockCountAll());
		cargoStockCard.setCurrentCargoStock(cpsIn.getStockCount() + cpsIn.getStockLockCount());
		cargoStockCard.setCargoStoreType(cargo.getStoreType());
		cargoStockCard.setCargoWholeCode(cargo.getWholeCode());
		cargoStockCard.setStockPrice(product.getPrice5());
		cargoStockCard.setAllStockPriceSum((new BigDecimal(cargoStockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(cargoStockCard.getStockPrice()))).doubleValue());
		if (this.cargoProductStockBeanDao.insertCargoStockCardBean(cargoStockCard) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}
					
		// 商品进销存卡片
		StockCardBean stockCard = new StockCardBean();
		stockCard.setCardType(StockCardBean.CARDTYPE_SPARE_IN);
		stockCard.setCode(stockInBean.getCode());
		stockCard.setCreateDatetime(DateUtil.getNow());
		stockCard.setStockType(ProductStockBean.STOCKTYPE_SPARE);
		stockCard.setStockArea(stockInBean.getAreaId());
		stockCard.setProductId(product.getId());
		stockCard.setStockId(psBean.getId());
		stockCard.setStockInCount(stockInBean.getCount());
		stockCard.setStockInPriceSum((new BigDecimal(stockInBean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		stockCard.setCurrentStock(product.getStock(stockCard.getStockArea(), stockCard.getStockType()) + product.getLockCount(stockCard.getStockArea(), stockCard.getStockType()));
		stockCard.setStockAllArea(product.getStock(stockCard.getStockArea()) + product.getLockCount(stockCard.getStockType()));
		stockCard.setStockAllType(product.getStockAllType(stockCard.getStockType()) + product.getLockCountAllType(stockCard.getStockType()));
		stockCard.setAllStock(product.getStockAll() + product.getLockCountAll());
		stockCard.setStockPrice(product.getPrice5());
		stockCard.setAllStockPriceSum((new BigDecimal(stockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(stockCard.getStockPrice()))).doubleValue());
		if (this.productStockDao.insertStockCardBean(stockCard) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}
		return cargo;
	}
	
	public int getAfterSaleReplaceNewProductRecordCout(String condition){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		conditionMap.put("condition", condition);
		return replaceRecordDao.getReplaceRecordCount(conditionMap);
	}
	
	/**
	 * 获取换新机列表
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 * 2014-10-28
	 * lining
	 */
	public List<AfterSaleReplaceNewProductRecord> getAfterSaleReplaceNewProductRecordList(String condition,int index,int count,String orderBy){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
			conditionMap.put("condition", condition);
		conditionMap.put("index", String.valueOf(index));
		conditionMap.put("count", String.valueOf(count));
		if(orderBy!=null && orderBy.length()>0){
			conditionMap.put("orderBy", orderBy);
		}
		return replaceRecordDao.getReplaceRecordList(conditionMap);
	}
	
	/**
	 * 换新机记录进行报价
	 * status=1--进行报价并更新换新机记录
	 * status=3--无需报价更新换新机记录
	 * status=4--进行无商品可更换动作
	 * @param recordId
	 * @param status
	 * 2014-10-28
	 * lining
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void quotePrice(int recordId,int status,voUser user,String quoteItem){
		AfterSaleReplaceNewProductRecord record = replaceRecordDao.selectByPrimaryKey(recordId);
		if(record==null){
			throw new RuntimeException("不存在此换新机记录!");
		}
		if(status == AfterSaleReplaceNewProductRecord.STATUS1){
			record.setType(AfterSaleReplaceNewProductRecord.TYPE1);
			record.setReplaceNewProductId(record.getOriProductId());
			
			AfterSaleDetectLogBean log = new AfterSaleDetectLogBean();
			log.setAfterSaleDetectProductId(record.getAfterSaleDetectProductId());
			log.setAfterSaleDetectTypeId(AfterSaleDetectTypeBean.REPLACE_QUOTE);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			log.setContent(quoteItem);
			log.setCreateDatetime(DateUtil.getNow());
			int logId = afterSaleDetectLogDao.insert(log);
			if(logId==0){
				throw new RuntimeException("插入换新机报价项失败!");
			}
			
			//销售那边插入相应的数据
			String insertSql = "insert into after_sale_warehource_record_detail "
					+ "(id,after_sale_warehource_record_id, after_sale_detect_type_id, content, create_user_id,create_user_name, create_datetime) "
					+ "values (" + logId + "," + record.getAfterSaleDetectProductId() +"," + AfterSaleDetectTypeBean.REPLACE_QUOTE +",'"
					+ StringUtil.toSql(quoteItem) + "'," + user.getId() + ",'" + StringUtil.toSql(user.getUsername()) + "','" 
					+ StringUtil.toSql(log.getCreateDatetime()) + "')" ;
			int num = afterSaleDetectLogDao.insertSaleDetectLog(insertSql);
			if(num == 0){
				throw new RuntimeException("插入销售那边换新机报价项失败!");
			}
		}
		if(status == AfterSaleReplaceNewProductRecord.STATUS3){
			record.setType(AfterSaleReplaceNewProductRecord.TYPE1);
			record.setReplaceNewProductId(record.getOriProductId());
		}
		if(status == AfterSaleReplaceNewProductRecord.STATUS4){
			//进行无商品可更换
			record.setType(AfterSaleReplaceNewProductRecord.TYPE2);
		}
		//更新销售那边的售后处理单type
		updateAfterSaleWarehourceProductType(user, record);
		
		record.setStatus(AfterSaleReplaceNewProductRecord.STATUS4);
		record.setLastOperateId(user.getId());
		record.setLastOperateUsername(user.getUsername());
		record.setLastOperateTime(DateUtil.getNow());
		int num = replaceRecordDao.updateByPrimaryKeySelective(record);
		if(num==0){
			throw new RuntimeException("更新换新机记录失败!");
		}
	}

	/**
	 * 更新销售那边的售后处理单type
	 * @param user
	 * @param record
	 * 2014-11-18
	 * lining
	 */
	private void updateAfterSaleWarehourceProductType(voUser user,
			AfterSaleReplaceNewProductRecord record) {
		//如果销售后台处理单type为16(保修换新机)-更新为18(保修换新机费用确认)
		//17(付费维修换新机)--跟新为19（付费维修换新机费用确认）
		//20(确认付费维修费用转换新机)--更新为21(确认付费维修费用转换新机费用确认)
		String querySql = "select type from after_sale_warehource_product_records where id=" + record.getAfterSaleDetectProductId();
		int type = replaceRecordDao.getAfterSaleWareHourceProductRecordType(querySql);
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("update after_sale_warehource_product_records set modify_user_id=")
						.append(user.getId()).append(",modify_user_name='").append(user.getUsername())
						.append("',modify_datetime='").append(DateUtil.getNow()).append("'");
		if(type==16){
			updateSql.append(",type=18 ");
		}else if(type==17){
			updateSql.append(",type=19 ");
		}else if(type==20){
			updateSql.append(",type=21 ");
		}else{
			throw new RuntimeException("售后处理单(销售)的类型错误!");
		}
		updateSql.append(" where id=").append(record.getAfterSaleDetectProductId());
		int num = replaceRecordDao.updateAfterSaleWareHourceProductRecord(updateSql.toString());
		if(num==0){
			throw new RuntimeException("更新销售后台处理单失败!");
		}
	}

	/**
	 * 更换备用机操作
	 * @param recordId
	 * @param replaceCode
	 * @param user
	 * 2014-10-29
	 * lining
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void replaceNewCode(int recordId, String replaceCode,voUser user) {
		AfterSaleReplaceNewProductRecord record = replaceRecordDao.selectByPrimaryKey(recordId);
		if(record==null){
			throw new RuntimeException("不存在此换新机记录!");
		}
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		conditionMap.put("code", replaceCode);
		SpareBean spare = spareDao.getSpareByCondition(conditionMap);
		if(spare==null){
			throw new RuntimeException("不存在编号为"+replaceCode+"的备用机!");
		}
		//备用机是否是可出库状态
		if(spare.getStatus()!=SpareBean.STATUS_STOCK_OUT){
			throw new RuntimeException("编号为"+replaceCode+"的备用机非可出库状态!");
		}
		//判断是否属于同sku
		if(spare.getProductId()!=record.getOriProductId()){
			throw new RuntimeException("编号为"+replaceCode+"的备用机与待更换的商品不是同sku!");
		}
		SpareStockinBean stockInBean = stockInDao.selectByPrimaryKey(spare.getSpareStockinId());
		if(stockInBean==null){
			throw new RuntimeException("编号为"+replaceCode+"的备用机的入库单不存在!");
		}
		
		//若备用机为售后处理单商品，需要判断是否已返厂
		conditionMap.clear();
		conditionMap.put("code", replaceCode);
		int count = detectProductDao.getAfterSaleBackSupplierProductCount(conditionMap);
		if(count>0){
			throw new RuntimeException("编号为"+replaceCode+"的备用机已返厂不可更换!");
		}
		
		AfterSaleDetectProductBean detectProduct = detectProductDao.getDetectProductById(record.getAfterSaleDetectProductId());
		if(detectProduct==null){
			throw new RuntimeException("编号为"+record.getAfterSaleDetectProductCode()+"的处理单不存在!");
		}
		replaceIMEIStatus(replaceCode, user, spare, detectProduct);
		
		record.setAreaId(detectProduct.getAreaId());
		record.setSpareCode(replaceCode);
		record.setStatus(AfterSaleReplaceNewProductRecord.STATUS5);
		record.setLastOperateId(user.getId());
		record.setLastOperateUsername(user.getUsername());
		record.setLastOperateTime(DateUtil.getNow());
		int num = replaceRecordDao.updateByPrimaryKeySelective(record);
		if(num==0){
			throw new RuntimeException("更新换新机记录失败!");
		}
		addSpareStockCard(user, spare, stockInBean, detectProduct, recordId);
		
		//处理单IMEI、productId更换成备用机的
		//备用机的code、imei、productId更换成处理单的
		String detectCode = detectProduct.getCode();
		String detectImei = detectProduct.getIMEI();
		int detectProductId = detectProduct.getProductId();
		int spareProductId = spare.getProductId();
		String spareImei = spare.getImei();
		spare.setCode(detectCode);
		spare.setImei(detectImei);
		spare.setProductId(detectProductId);
		num = spareDao.updateByPrimaryKeySelective(spare);
		if(num==0){
			throw new RuntimeException("更新备用机失败!");
		}
		detectProduct.setProductId(spareProductId);
		detectProduct.setIMEI(spareImei);
		num = detectProductDao.updateDetectProduct(detectProduct);
		if(num==0){
			throw new RuntimeException("更新售后处理单失败!");
		}
		//新增待寄回用户商品
		AfterSaleBackUserProduct backUserProduct = new AfterSaleBackUserProduct();
		backUserProduct.setAfterSaleDetectProductId(detectProduct.getId());
		backUserProduct.setType(AfterSaleBackUserProduct.TYPE2);
		backUserProduct.setProductId(detectProduct.getProductId());
		num = backUserProductDao.insert(backUserProduct);
		if(num == 0){
			throw new RuntimeException("新增待寄回用户商品失败!");
		}
	}

	/**
	 * 新增备用机出入库卡片
	 * @param replaceCode
	 * @param user
	 * @param spare
	 * @param stockInBean
	 * @param detectProduct
	 * 2014-10-29
	 * lining
	 */
	private void addSpareStockCard(voUser user,
			SpareBean spare, SpareStockinBean stockInBean,
			AfterSaleDetectProductBean detectProduct, int itemId) {
		//新增备用机出库卡片
		SpareStockCard card = new SpareStockCard();
		card.setOperateItemId(itemId);
		card.setOperateItemCode("");
		card.setSpareCode(spare.getCode());
		card.setAreaId(stockInBean.getAreaId());
		card.setCount(1);
		card.setCreateDatetime(DateUtil.getNow());
		card.setOperateId(user.getId());
		card.setOperateUsername(user.getUsername());
		card.setProductId(spare.getProductId());
		card.setSupplierId(stockInBean.getSupplierId());
		card.setType(SpareStockCard.TYPE_REPLACE_STOCKOUT);
		int num = spareStockCardDao.insert(card);
		if(num==0){
			throw new RuntimeException("新增备用机出库卡片失败!");
		}
		//新增备用机入库卡片
		card.setProductId(detectProduct.getProductId());
		card.setType(SpareStockCard.TYPE_REPLACE_STOCKIN);
		num = spareStockCardDao.insert(card);
		if(num==0){
			throw new RuntimeException("新增备用机入库卡片失败!");
		}
	}

	/**
	 * 更换备用机、售后处理单中的imei码的状态并记录日志
	 * @param replaceCode
	 * @param user
	 * @param spare
	 * @param detectProduct
	 * 2014-10-29
	 * lining
	 */
	private void replaceIMEIStatus(String replaceCode, voUser user,SpareBean spare,
			AfterSaleDetectProductBean detectProduct) {
		HashMap<String, String> conditionMap = new HashMap<String, String>();
		int count = imeiDao.getImeiProductId(detectProduct.getProductId());
		boolean hasIMEI = false;
		if(count == 1){
			hasIMEI = true;
		}
		if(StringUtil.checkNull(detectProduct.getIMEI()).trim().length()>0){
			conditionMap.put("code", detectProduct.getIMEI());
			IMEIBean detectImei = imeiDao.getIMEIByCondition(conditionMap);
			if(hasIMEI){
				if(detectImei==null){
					throw new RuntimeException("售后处理单"+detectProduct.getCode()+"的IMEI码记录不存在!");
				}
			}
			if(detectImei.getStatus()!=IMEIBean.IMEISTATUS6){
				detectImei.setStatus(IMEIBean.IMEISTATUS2);
				replaceDetectProductIMEIStatus(replaceCode, user,detectImei);
			}
		}
		if(StringUtil.checkNull(spare.getImei()).trim().length()>0){
			conditionMap.clear();
			conditionMap.put("condition", "code='" + spare.getImei().trim() + "'");
			conditionMap.put("set", "status=" + IMEIBean.IMEISTATUS3);
			replaceSpareIMEIStatus(conditionMap,user,spare.getImei(),detectProduct.getCode());
		}
	}

	/**
	 * 更新备用机imei码状态
	 * @param spare
	 * @param spareImei
	 * 2014年12月5日
	 * user
	 */
	private void replaceSpareIMEIStatus(HashMap<String, String> conditionMap,voUser user,String spareImeiCode,String detectCode) {
		int num = imeiDao.updateIMEIByCondition(conditionMap);
		if(num==0){
			throw new RuntimeException("更新IMEI码状态失败!");
		}
		IMEILogBean log = new IMEILogBean();
		log.setIMEI(spareImeiCode);
		log.setOperCode(detectCode);
		log.setOperType(IMEILogBean.OPERTYPE12);
		log.setContent("【更换备用机】操作更换IMEI码状态为" + IMEIBean.IMEIStatusMap.get(IMEIBean.IMEISTATUS3));
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		log.setCreateDatetime(DateUtil.getNow());
		num = imeiLogDao.insert(log);
		if(num==0){
			throw new RuntimeException("插入IMEI码日志失败!");
		}
	}

	/**
	 * 更新处理单imei码状态
	 * @param replaceCode
	 * @param user
	 * @param detectImei
	 * 2014年12月5日
	 * user
	 */
	private void replaceDetectProductIMEIStatus(String replaceCode,
			voUser user, IMEIBean detectImei) {
		int num = imeiDao.updateByPrimaryKeySelective(detectImei);
		if(num==0){
			throw new RuntimeException("更新IMEI码状态失败!");
		}
		IMEILogBean log = new IMEILogBean();
		log.setIMEI(detectImei.getCode());
		log.setOperCode(replaceCode);
		log.setOperType(IMEILogBean.OPERTYPE12);
		log.setContent("【更换备用机】操作更换IMEI码状态为" + IMEIBean.IMEIStatusMap.get(detectImei.getStatus()));
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		log.setCreateDatetime(DateUtil.getNow());
		num = imeiLogDao.insert(log);
		if(num==0){
			throw new RuntimeException("插入IMEI码日志失败!");
		}
	}
	

	private void replaceIMEIstatus(String replaceCode, voUser user,
			SpareBean spare, AfterSaleDetectProductBean detectProduct,
			HashMap<String, String> conditionMap) {
		if(StringUtil.checkNull(detectProduct.getIMEI()).equals("")){
			throw new RuntimeException("售后处理单"+detectProduct.getCode()+"的IMEI码为空!");
		}
		conditionMap.put("code", detectProduct.getIMEI());
		IMEIBean detectImei = imeiDao.getIMEIByCondition(conditionMap);
		if(detectImei==null){
			throw new RuntimeException("售后处理单"+detectProduct.getCode()+"的IMEI码记录不存在!");
		}
		conditionMap.clear();
		conditionMap.put("code", spare.getImei());
		IMEIBean spareImei = imeiDao.getIMEIByCondition(conditionMap);
		if(spareImei==null){
			throw new RuntimeException("备用机"+spare.getCode()+"的IMEI码记录不存在!");
		}
		int detectStatus = detectImei.getStatus();
		int spareStatus = spareImei.getStatus();
		detectImei.setStatus(spareStatus);
		spareImei.setStatus(detectStatus);
		int num = imeiDao.updateByPrimaryKeySelective(detectImei);
		if(num==0){
			throw new RuntimeException("更新IMEI码状态失败!");
		}
		IMEILogBean log = new IMEILogBean();
		log.setIMEI(detectImei.getCode());
		log.setOperCode(replaceCode);
		log.setOperType(IMEILogBean.OPERTYPE12);
		log.setContent("【更换备用机】操作更换IMEI码状态为" + IMEIBean.IMEIStatusMap.get(detectImei.getStatus()));
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		log.setCreateDatetime(DateUtil.getNow());
		num = imeiLogDao.insert(log);
		if(num==0){
			throw new RuntimeException("插入IMEI码日志失败!");
		}
		
		num = imeiDao.updateByPrimaryKeySelective(spareImei);
		if(num==0){
			throw new RuntimeException("更新IMEI码状态失败!");
		}
		log.setId(0);
		log.setIMEI(spareImei.getCode());
		log.setOperCode(detectProduct.getCode());
		log.setOperType(IMEILogBean.OPERTYPE12);
		log.setContent("【更换备用机】操作更换IMEI码状态为" + IMEIBean.IMEIStatusMap.get(spareImei.getStatus()));
		num = imeiLogDao.insert(log);
		if(num==0){
			throw new RuntimeException("插入IMEI码日志失败!");
		}
	}
	/**
	 * 获取历史入库数据总数
	 * @param condition
	 * @return
	 * 2014-11-3
	 * lining
	 */
	public int getHistoryStockCount(String condition){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		conditionMap.put("conditon", condition);
		return spareStockCardDao.getHistoryStockCount(conditionMap);
	}
	/**
	 * 获取历史出入入库列表
	 * @param condition
	 * @return
	 * 2014-11-3
	 * lining
	 */
	public List<SpareStockCard> getHistoryStockList(String condition,int index,int count,String orderBy){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		if(condition!=null && condition.length()>0){
			conditionMap.put("condition", condition);
		}
		conditionMap.put("index", String.valueOf(index));
		conditionMap.put("count", String.valueOf(count));
		if(orderBy!=null && orderBy.length()>0){
			conditionMap.put("orderBy", orderBy);
		}
		return spareStockCardDao.getHistoryStockList(conditionMap);
	}
	
	/**
	 * 获取备用机列表
	 * @param stockinId
	 * @param code
	 * @return
	 * 2014-11-4
	 * lining
	 */
	public List<SpareBean> getSpareList(int operateItemId,int type){
		HashMap<String,String> conditionMap = new HashMap<String,String>();
		List<SpareBean> list = new ArrayList<SpareBean>();
		if(type==1){
			conditionMap.put("stockinId", String.valueOf(operateItemId));
			list = spareDao.getSpareList(conditionMap);
		}else if(type==2){
			conditionMap.put("condition", "asrnpr.id="+operateItemId);
			SpareBean bean = spareDao.getSpareJoinReplaceRecord(conditionMap);
			list.add(bean);
		}else if(type==3){
			conditionMap.put("condition", "sbsp.spare_back_supplier_id="+operateItemId);
			list = spareDao.getSpareListJoinBackSupplierProduct(conditionMap);
		}else if(type==4){
			list = spareDao.getSpareListJoinUnqualifiedReplace(" on s.code=surr.replace_spare_code where surr.id="+operateItemId);
		}else if(type==5){
			list = spareDao.getSpareListJoinUnqualifiedReplace(" on s.code=surr.ori_spare_code where surr.id="+operateItemId);
		}
		return list;
	}
	
	public AfterSaleBackSupplier getAfterSaleBackSupplier(int supplierId){
		return afterSaleBackSupplierDao.getAfterSaleBackSupplierById(supplierId);
	}
	
	/**
	 * 判断商品是否是IMEI商品
	 * @param productCode
	 * @return true--是 false--否
	 * 2014年12月15日
	 * user
	 */
	public boolean getImeiProduct(String productCode){
		boolean flag = false;
		voProduct product = productDao.getProduct("code='" + productCode + "'");
		if(product == null){
			throw new RuntimeException("编号为" + productCode + "的商品不存在!");
		}
		int count = imeiDao.getImeiProductId(product.getId());
		if(count==1){
			flag = true;
		}
		return flag;
	}
}
