package mmb.stock.aftersale;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.easyui.Json;
import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.CheckUser;
import mmb.rec.pda.util.JsonModelUtil;
import mmb.rec.pda.util.ReceiveJson;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockTypeBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 仓内售后作业、售后库客户库盘点 PDA专用
 * 
 * @author mengqy
 * 
 */
@RequestMapping("admin/PDAAfStock")
@Controller
public class PDAAfStockController {
	private static byte[] lock = new byte[0];

	/**
	 * 客户寄回包裹签收
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/addDetectPackage")
	@ResponseBody
	public JsonModel addDetectPackage(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		int deliverId = 0;
		int returnType = 0;
		float freight = 0;
		String packageCode = "";
		int area = 1;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			deliverId = StringUtil.toInt(json.getData().get("deliverId").toString());
			returnType = StringUtil.toInt(json.getData().get("returnType").toString());
			freight = StringUtil.toFloat(json.getData().get("freight").toString());
			packageCode = json.getData().get("packageCode").toString();
			//判断包裹单号是否合法
			if(!StringUtil.isNoBlank(packageCode)){
				return returnError("包裹单号不合法");
			}
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("数据异常!");
		}

		String results = new CommonLogic().addDetectPackageBean(deliverId, returnType, freight, packageCode, tools.getUser(), area);
		return returnMsg(results);
	}

	/**
	 * 未妥投包裹签收
	 * 
	 * @author mengqy
	 * @param request
	 * @return
	 */
	@RequestMapping("/receiveBackuserPackage")
	@ResponseBody
	public JsonModel receiveBackuserPackage(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		String packageCode = "";
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			packageCode = json.getData().get("packageCode").toString();
			//判断包裹单号是否合法
			if(!StringUtil.isNoBlank(packageCode)){
				return returnError("包裹单号不合法");
			}
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		String results = new CommonLogic().receiveBackuserPackage(packageCode, tools.getUser(), 0);
		return returnMsg(results);
	}

	/**
	 * 用户库商品上架
	 * 
	 * @author mengqy
	 * @param request
	 * @return
	 */
	@RequestMapping("/productUpShelfForUser")
	@ResponseBody
	public JsonModel productUpShelfForUser(HttpServletRequest request) {
		return productUpShelf(request, -1, 1);
	}

	/**
	 * 售后库商品上架
	 * 
	 * @author mengqy
	 * @param request
	 * @return
	 */
	@RequestMapping("/productUpShelfForAfterSale")
	@ResponseBody
	public JsonModel productUpShelfForAfterSale(HttpServletRequest request) {
		return productUpShelf(request, -1, 2);
	}
	/**
	 * 用户库、售后库 商品上架
	 * @param request
	 * @param groupFlag
	 *            权限
	 * @param type
	 *            1用户库 2售后库
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JsonModel productUpShelf(HttpServletRequest request, int groupFlag, int type) {
		CommonTools tools = new CommonTools();
		String cargoWholeCode = "";
		List<String> afCodes = null;
		int area = 1;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			cargoWholeCode = json.getData().get("cargoWholeCode").toString();
			afCodes = (ArrayList<String>) json.getData().get("afCodes");
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}
		String results = new CommonLogic().productUpShelf(cargoWholeCode, afCodes, area, tools.getUser(), type);
		return returnMsg(results);
	}
	/**
	 * 待返厂商品下架
	 * @param request
	 * @param groupFlag 权限
	 * @author syuf
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/productDownShelf")
	@ResponseBody
	public JsonModel productDownShelf(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		CommonLogic common = new CommonLogic();
		String results = null;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			String cargoWholeCode = json.getData().get("wholeCode").toString();
			String cargoId = json.getData().get("cargoId").toString();
			List<String> afCodes = (ArrayList<String>) json.getData().get("afCodes");
			int area = StringUtil.toInt(json.getArea());
			if("".equals(StringUtil.checkNull(cargoWholeCode))){
				return returnSuccess("data",common.loadWaitBackSupplierProduct(cargoId, area));
			} else {
				results =common.productDownShelf(cargoWholeCode, afCodes, area, tools.getUser(), ProductStockBean.STOCKTYPE_CUSTOMER);
			}
		} catch (Exception e) {
			return returnError("数据异常!");
		}
		return returnMsg(results);
	}

	/**
	 * 厂商寄回商品上架
	 * 
	 * @author mengqy
	 * @param request
	 * @return
	 */
	@RequestMapping("/productUpShelfForReceiveFactory")
	@ResponseBody
	public JsonModel productUpShelfForReceiveFactory(HttpServletRequest request) {

		CommonTools tools = new CommonTools();
		String afCode = "";
		int area = 1;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			afCode = json.getData().get("afterSaleCode").toString();
			afCode = StringUtil.dealParam(afCode);
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		WareService wareService = new WareService();
		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
			if (product == null) {
				return returnError("未查询到售后处理单");
			}
			
			if(!service.checkAfterSaleUserGroup(tools.getUser(), product.getAreaId())){			
				return returnError("没有该地区售后仓内作业权限!");
			}
			
			AfterSaleBackSupplierProduct sProduct = service.getAfterSaleBackSupplierProduct(" after_sale_detect_product_id = " + product.getId());
			if (sProduct == null) {
				return returnError("该商品为非返厂商品");
			}
	
			if (sProduct.status != AfterSaleBackSupplierProduct.STATUS1) {
				return returnError("该商品还未寄回");
			}
	
			voProduct vProduct = wareService.getProduct(product.getProductId());
			if (vProduct == null)
				return returnError("未查询到商品信息");
	
			CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
			if (cargoInfo == null)
				return returnError("未查询到关联的货位");
	
			if (cargoInfo.getAreaId() != area)
				return returnError("处理单所关联的货位不在当前作业地区");
			JsonModel results = returnSuccess("cargoWholeCode", product.getCargoWholeCode());
			results.getData().put("name", vProduct.getName());
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("异常");
		} finally {
			wareService.releaseAll();
		}
		
	}

	/**
	 * 添加返厂商品
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/backSupplierProduct")
	@ResponseBody
	public JsonModel backSupplierProduct(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		int supplierId = -1;
		List<String> afCodes = null;
		String packageCode = "";
		String contract = "";

		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			supplierId = StringUtil.toInt(json.getData().get("supplierId").toString());
			packageCode = json.getData().get("packageCode").toString();
			afCodes = (ArrayList<String>) json.getData().get("afCodes");
			contract = json.getData().get("contract").toString();
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		String results = new CommonLogic().backSupplierProduct(supplierId, afCodes, packageCode, contract, tools.getUser());
		return returnMsg(results);
	}

	/**
	 * 厂商寄回商品签收——查询IMEI(已废弃)
	 *  
	 * @param request
	 * @return
	 */
	@RequestMapping("/searchForReceiveBackSupplierProduct")
	@ResponseBody
	public JsonModel searchForReceiveBackSupplierProduct(HttpServletRequest request) {

		CommonTools tools = new CommonTools();
		String afCode = "";
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			afCode = json.getData().get("afCode").toString();
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		String results = new CommonLogic().searchIMIE(afCode);
		if (!results.startsWith("?"))
			return returnError(results);

		return returnSuccess("imei", results.substring(1));
	}

	/**
	 * 厂商寄回商品签收
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/receiveBackSupplierProduct")
	@ResponseBody
	public JsonModel receiveBackSupplierProduct(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		String afCode = "";
		String imeiCode = "";
		String productCode = "";
		int type = 0;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			afCode = json.getData().get("afterSaleCode").toString();
			imeiCode = json.getData().get("imeiCode").toString();
			productCode = json.getData().get("productCode").toString();
			type = StringUtil.toInt(json.getData().get("type").toString());			
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		synchronized (lock) {
			Json j = new CommonLogic().receiveBackSupplierProduct(afCode, imeiCode, productCode, type, tools.getUser(), 0);		
			
			if(!j.isSuccess()){
				return returnError(j.getMsg());
			}
			
			if(j.getObj() == null){
				return JsonModelUtil.success();
			}
	
			return JsonModelUtil.success("isSpare", j.getObj());
		}

	}

	/**
	 * 厂商寄回商品签收--获取Code
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getCodeForBacksupplier")
	@ResponseBody
	public JsonModel getCodeForBacksupplier(HttpServletRequest request){		
		CommonTools tools = new CommonTools();
		String afCode = "";
		String imeiCode = "";
		String productCode = "";		
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			afCode = json.getData().get("afterSaleCode").toString();
			imeiCode = json.getData().get("imeiCode").toString();
			productCode = json.getData().get("productCode").toString();	
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		Json j = new CommonLogic().getCodeForBackSupplier(imeiCode, afCode, productCode);
		if(!j.isSuccess()){
			return JsonModelUtil.error(j.getMsg());
		}
		
		JsonModel json = new JsonModel();
		json.setFlag(1);
		json.setData(new HashMap<String, Object>());
		json.setMessage("操作成功");
		HashMap<String, String> map = (HashMap<String, String>) j.getObj();
		for (String key : map.keySet()) {
			json.getData().put(key, map.get(key));
		}		
		return json;
	}
	
	/**
	 * 寄回用户
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/backUser")
	@ResponseBody
	public JsonModel backUser(HttpServletRequest request) {

		CommonTools tools = new CommonTools();
		String packageCode = "";
		List<String> afCodes = null;
		float freight = 0f;
		int deliverId = 0;
		String tel = "";
		String address = "";
		String username = "";
		String results = null;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			packageCode = json.getData().get("packageCode").toString();
			tel = json.getData().get("tel").toString();
			address = json.getData().get("address").toString();
			username = json.getData().get("username").toString();
			afCodes = (ArrayList<String>) json.getData().get("afCodes");
			freight = StringUtil.toFloat(json.getData().get("freight").toString());
			deliverId = StringUtil.toInt(json.getData().get("deliverId").toString());
			results = new CommonLogic().backUser(afCodes, packageCode, freight, deliverId, tel,0.00f, address, username, tools.getUser(), "", 0);
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("数据异常!");
		}

		return returnMsg(results);
	}

	/**
	 * 查询售后单地址
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/searchAddress")
	@ResponseBody
	public JsonModel searchAddress(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		String afCode = "";
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			afCode = json.getData().get("afCode").toString();
			afCode = StringUtil.dealParam(afCode);
		} catch (Exception e) {
			return returnError("数据异常!");
		}
		DbOperation dbop = new DbOperation(DbOperation.DB);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbop);
		IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbop);
		try {
			AfterSaleDetectProductBean product = afService.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
			if (product == null) {
				return returnError("未查询到售后处理单[" + afCode + "]");
			}
	
			if (!(product.getStatus() == 4 || product.getStatus() == 7 || product.getStatus() == 10)) {
				return returnError("售后处理单[" + afCode + "]状态不正确");
			}
	
			AfterSaleOrderBean order = afterSaleService.getAfterSaleOrder(" id = " + product.getAfterSaleOrderId());
			if (order == null)
				return returnError("未查询到售后处理单[" + afCode + "]所关联的售后单");
	
			JsonModel json = returnSuccess("tel", order.getCustomerPhone());
			json.getData().put("address", order.getCustomerAddress());
			json.getData().put("username", order.getCustomerName());
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("异常!");
		} finally {
			dbop.release();
		}
	}

	/**
	 * 封箱
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/sealBox")
	@ResponseBody
	public JsonModel sealBox(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		List<String> afCodes = null;

		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			afCodes = (ArrayList<String>) json.getData().get("afCodes");
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		String results = new CommonLogic().sealBox(afCodes, tools.getUser());
		if (!results.startsWith("?"))
			return returnError(results);

		return returnSuccess("code", results.substring(1));
	}

	/**
	 * 领取售后退货入库任务（货位区入售后库）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/searchStockinForReturn")
	@ResponseBody
	public JsonModel searchStockinForReturn(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		int area = 1;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			area =  StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		synchronized (lock) {

			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				String query = " SELECT detect.cargo_whole_code FROM after_sale_detect_product AS detect , after_sale_stockin AS si, cargo_info AS ci ";
				query += " WHERE detect.id = si.after_sale_detect_product_id AND detect.cargo_whole_code = ci.whole_code AND si.status = 0 AND ci.store_type != 2  AND ci.area_id = " + area;
				query += " AND ( complete_user_id = " + tools.getUser().getId() + " OR complete_user_id IS NULL ) ";

				query += " ORDER BY detect.cargo_whole_code ASC LIMIT 1 ";
				// TODO Test Code 方便测试
				// query += " ORDER BY si.id DESC LIMIT 1 ";

				ResultSet rs = dbOp.executeQuery(query);
				if (rs == null || !rs.next()) {
					if (rs != null)
						rs.close();
					return returnError("没有查询到售后退货入库任务");
				}

				String cargo = rs.getString("cargo_whole_code");
				rs.close();

				if (cargo == null || cargo.length() == 0)
					return returnError("没有查询到售后退货入库任务");

				String condition = " si.status = 0 AND detect.cargo_whole_code = '" + cargo + "' ORDER BY si.id DESC LIMIT 30 ";
				query = " SELECT si.id AS inId, p.name, detect.id, detect.code ";
				query += " FROM after_sale_stockin AS si INNER JOIN after_sale_detect_product AS detect ON si.after_sale_detect_product_id = detect.id ";
				query += " LEFT JOIN  product AS p ON si.product_id = p.id  ";
				query += " WHERE " + condition;

				List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				List<String> inIds = new ArrayList<String>();
				rs = dbOp.executeQuery(query);
				if (rs != null) {
					while (rs.next()) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("name", rs.getString("name"));
						map.put("id", rs.getString("id"));
						map.put("code", rs.getString("code"));
						list.add(map);

						inIds.add(rs.getString("inId"));
					}
					rs.close();
				}

				if (list.size() == 0)
					return returnError("没有查询到售后退货入库任务");

				dbOp.startTransaction();

				for (String inId : inIds) {
					// 设置原货位、绑定用户, 一个入库任务只允许一个用户进行入库
					String set = " out_cargo_whole_code = '" + cargo + "', complete_user_id = " + tools.getUser().getId() + " , complete_user_name = '" + StringUtil.dealParam(tools.getUser().getUsername()) + "' ";
					if (!afService.updateAfterSaleStockin(set, " id = " + inId)) {
						dbOp.rollbackTransaction();
						return returnError("数据库操作失败");
					}
				}

				dbOp.commitTransaction();

				JsonModel result = returnSuccess("list", list);
				result.getData().put("outCargoWholeCode", cargo);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				return returnError("发生异常");
			} finally {
				dbOp.release();
			}

		}
	}

	/**
	 * 完成售后退货入库任务（货位区入售后库）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/stockinForReturn")
	@ResponseBody
	public JsonModel stockinForReturn(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		// 售后处理单号列表Id
		List<String> ids = null;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			ids = (ArrayList<String>) json.getData().get("ids");
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		String result = new CommonLogic().stockinForReturn(ids, tools.getUser());
		return returnMsg(result);
	}

	/**
	 * 缓存区入售后库
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/quickStockinForReturn")
	@ResponseBody
	public JsonModel quickStockinForReturn(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		// 售后处理单号列表
		List<String> afCodes = null;
		String cargoWholeCode = null;
		int area = 1;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0) {
				return json;
			}
			
			afCodes = (ArrayList<String>) json.getData().get("afCodes");
			if (afCodes == null || afCodes.size() == 0) {
				return returnError("请扫描处理单号");
			}
			
			cargoWholeCode = StringUtil.dealParam(json.getData().get("cargoWholeCode").toString());
			if (cargoWholeCode == null || cargoWholeCode.length() == 0) {
				return returnError("请扫描货位");
			}
			
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		List<String> ids = new ArrayList<String>();
		List<String> errorList = new ArrayList<String>();

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			// 判断是否正在盘点
			AfterSaleInventory afterSaleInventory = afService.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK);
			if (afterSaleInventory != null) {
				return returnError("存在尚未完成的盘点单，请先盘点!");
			}
			
			if (cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' AND area_id = " + area) == null) {
				return returnError("货位不存在");
			}

			for (String code : afCodes) {
				AfterSaleDetectProductBean product = afService.getAfterSaleDetectProduct(" code = '" + code + "' ");
				if (product == null) {
					errorList.add(code + ":处理单不存在");
					continue;
				}

				if (product.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
					errorList.add(code + ":处理单已锁定");
					continue;
				}

				if (!product.getCargoWholeCode().equalsIgnoreCase(cargoWholeCode)) {
					errorList.add(code + ":关联货位不正确");
					continue;
				}

				// status = 0 入库未完成
				if (afService.getAfterSaleStockin(" status = 0 AND after_sale_detect_product_id = " + product.getId()) == null) {
					errorList.add(code + ":处理单入库任务不存在");
					continue;
				}

				ids.add("" + product.getId());
			}

			if (ids.size() == 0) {
				JsonModel jsonResult = returnSuccess("error", errorList);
				jsonResult.setFlag(2);
				jsonResult.setMessage(ids.size() + "个处理单入库【成功】" + errorList.size() + "个处理单入库【失败】");
				return jsonResult;
			}

			String result = new CommonLogic().stockinForReturn(ids, tools.getUser());
			if (result != null)
				return returnError(result);

			if (errorList.size() > 0) {
				JsonModel jsonResult = returnSuccess("error", errorList);
				jsonResult.setFlag(2);
				jsonResult.setMessage(ids.size() + "个处理单入库【成功】" + errorList.size() + "个处理单入库【失败】");				
				return jsonResult;
			}

			return returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
	}

	/**
	 * 查询快递公司列表
	 * 
	 * @author
	 * @return
	 */
	@RequestMapping("/getDeliverList")
	@ResponseBody
	public JsonModel getDeliverList() {
		List<HashMap<String, String>> results = new CommonLogic().getDeliverList();
		if (results.size() == 0)
			return returnError("没有查询到快递公司");

		return returnSuccess("deliverList", results);
	}

	/**
	 * 查询返厂厂商列表
	 * 
	 * @return
	 */
	@RequestMapping("/getSupplierList")
	@ResponseBody
	public JsonModel getSupplierList() {
		List<AfterSaleBackSupplier> list = new AfStockService(IBaseService.CONN_IN_METHOD, null).getAfterSaleBackSupplierList(" id > 0 ", -1, -1, " id desc");

		if (list == null || list.size() == 0)
			return returnError("没有查询到返厂厂商!");

		List<HashMap<String, String>> suppliers = new ArrayList<HashMap<String, String>>();

		for (AfterSaleBackSupplier supplier : list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", Integer.toString(supplier.getId()));
			map.put("content", supplier.getName());
			suppliers.add(map);
		}

		return returnSuccess("supplierList", suppliers);
	}

	/**
	 * 售后库、客户库盘点，领单
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getRecordId")
	@ResponseBody
	public JsonModel getRecordId(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		// 1 客户库、2售后库
		int type = 0;
		int area = 1;
		int stockType = -1;
		try {
			JsonModel json = tools.getModelAndCheck(request, 1465);
			if (json.getFlag() == 0)
				return json;
			type = StringUtil.toInt(json.getData().get("type").toString());
			if (type == 1) {
				stockType = CargoInfoBean.STOCKTYPE_CUSTOMER;
			} else if (type == 2) {
				stockType = CargoInfoBean.STOCKTYPE_AFTER_SALE;
			} else {
				return returnError("参数错误");
			}
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" stock_area = ").append(area);
			sb.append(" AND stock_type = ").append(stockType);
			sb.append(" AND status = ").append(AfterSaleInventoryRecord.STATUS_START).append(" LIMIT 1 ");

			AfterSaleInventoryRecord record = service.getAfterSaleInventoryRecord(sb.toString());
			if (record == null)
				return returnError("没有可以盘点的任务");

			return returnSuccess("recordId", record.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
	}

	/**
	 * 售后库、客户库盘点，判断货位是否是指定货位
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/judgeCargo")
	@ResponseBody
	public JsonModel judgeCargo(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		// 货位号
		String cargoWholeCode = "";
		// 盘点记录表id
		int recordId = 0;
		int area = 1;

		try {
			JsonModel json = tools.getModelAndCheck(request, 1465);
			if (json.getFlag() == 0)
				return json;
			cargoWholeCode = StringUtil.dealParam(json.getData().get("cargoWholeCode").toString());
			recordId = StringUtil.toInt(json.getData().get("recordId").toString());
			if (cargoWholeCode == null || "".equals(cargoWholeCode)) {
				return returnError("参数错误");
			}
			
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			AfterSaleInventoryRecord record = service.getAfterSaleInventoryRecord(" id = " + recordId + " AND stock_area = " + area);
			if (record == null) {
				return returnError("该盘点任务不存在");
			}

			if (record.getStatus() == AfterSaleInventoryRecord.STATUS_END) {
				return returnError("该盘点任务已完成", 2);
			}

			CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' ");
			if (cargoInfo == null) {
				return returnError("货位不存在");
			}

			if (cargoInfo.getAreaId() != record.getStockArea() || cargoInfo.getStockType() != record.getStockType()) {
				return returnError("货位不正确，不能进行盘点", 3);
			}

			return returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
	}

	/**
	 * 售后库、客户库盘点
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/inventory")
	@ResponseBody
	public JsonModel inventory(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		// 货位号
		String cargoWholeCode = "";
		// 售后处理单号
		String afCode = "";
		// 盘点记录表id
		int recordId = 0;
		int area = 1;

		try {
			JsonModel json = tools.getModelAndCheck(request, 1465);
			if (json.getFlag() == 0)
				return json;
			cargoWholeCode = StringUtil.dealParam(json.getData().get("cargoWholeCode").toString());
			afCode = StringUtil.dealParam(json.getData().get("afCode").toString());
			recordId = StringUtil.toInt(json.getData().get("recordId").toString());

			if (cargoWholeCode == null || "".equals(cargoWholeCode) || afCode == null || "".equals(afCode)) {
				return returnError("参数错误");
			}
			
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			AfterSaleInventoryRecord record = service.getAfterSaleInventoryRecord(" id = " + recordId + " AND stock_area = " + area);
			if (record == null) {
				return returnError("该盘点任务不存在");
			}
			if (record.getStatus() == AfterSaleInventoryRecord.STATUS_END) {
				return returnError("该盘点任务已完成", 2);
			}

			CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' LIMIT 1 ");
			if (cargoInfo == null) {
				return returnError("货位不存在");
			}

			if (cargoInfo.getAreaId() != record.getStockArea() || cargoInfo.getStockType() != record.getStockType()) {
				return returnError("货位不正确，不能进行盘点", 3);
			}

			AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + afCode + "' LIMIT 1 ");
			if (product == null) {
				return returnError("该售后处理单不存在");
			}

			CargoInfoBean pCargo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' LIMIT 1 ");
			if (pCargo == null || pCargo.getAreaId() != cargoInfo.getAreaId() || pCargo.getStockType() != cargoInfo.getStockType()) {
				return returnError("该处理单不属于当前库");
			}

			// 默认为 普通
			int type = AfterSaleInventoryProduct.TYPE1;
			// 是否是报溢
			if (product.getLockStatus() == AfterSaleDetectProductBean.LOCK_STATUS2) {
				type = AfterSaleInventoryProduct.TYPE3;

				// 是否是寄出用户
			} else if (product.getStatus() == AfterSaleDetectProductBean.STATUS11 || product.getStatus() == AfterSaleDetectProductBean.STATUS8 || product.getStatus() == AfterSaleDetectProductBean.STATUS9) {
				type = AfterSaleInventoryProduct.TYPE5;
			} else {
				// 是否是 返厂商品
				AfterSaleBackSupplierProduct supplierProduct = service.getAfterSaleBackSupplierProduct(" after_sale_detect_product_id = " + product.getId() + " AND product_id = " + product.getProductId());
				if (supplierProduct != null && (supplierProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS0 || supplierProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS4)) {
					type = 1;
					// 是否是 货位不一致
				} else if (cargoInfo.getId() != pCargo.getId()) {
					type = AfterSaleInventoryProduct.TYPE4;
				}
			}
			AfterSaleInventoryProduct oldInventoryProduct = service.getAfterSaleInventoryProduct(" after_sale_inventory_record_id = " + recordId + " AND after_sale_detect_product_code = '" + afCode + "' ");

			// 本次盘点是否存在 该售后处理单的盘点记录
			if (oldInventoryProduct == null) {
				AfterSaleInventoryProduct inventoryProduct = new AfterSaleInventoryProduct();
				inventoryProduct.setAfterSaleInventoryRecordId(recordId);
				inventoryProduct.setAfterSaleDetectProductCode(product.getCode());
				inventoryProduct.setAfterSaleDetectProductStatus(product.getStatus());
				inventoryProduct.setAfterSaleOrderCode(product.getAfterSaleOrderCode());
				inventoryProduct.setProductId(product.getProductId());
				inventoryProduct.setRealWholeCode(cargoInfo.getWholeCode());
				inventoryProduct.setRecordWholeCode(pCargo.getWholeCode());
				inventoryProduct.setType(type);
				inventoryProduct.setUserId(tools.getUser().getId());
				inventoryProduct.setUserName(tools.getUser().getUsername());

				if (!service.addAfterSaleInventoryProduct(inventoryProduct)) {
					return returnError("数据库操作失败:addAfterSaleInventoryProduct");
				}

			} else {
				// type 或 记录货位不一致，则更新原有记录
				if (oldInventoryProduct.getType() != type || !cargoInfo.getWholeCode().equals(oldInventoryProduct.getRealWholeCode()) || oldInventoryProduct.getAfterSaleDetectProductStatus() != product.getStatus()) {
					StringBuilder sb = new StringBuilder();
					sb.append(" record_whole_code = '").append(cargoInfo.getWholeCode()).append("' ");
					sb.append(" , after_sale_detect_product_status = ").append(product.getStatus());
					sb.append(" , type = ").append(type);
					sb.append(" , user_id = ").append(tools.getUser().getId());
					sb.append(" , user_name = '").append(tools.getUser().getUsername()).append("' ");

					if (!service.updateAfterSaleInventoryProduct(sb.toString(), " id = " + oldInventoryProduct.getId())) {
						return returnError("数据库操作失败:updateAfterSaleInventoryProduct");
					}
				}
			}
			return returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
	}

	/**
	 * 售后库调拨，获取库地区和库类型列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getStockAreaForAllot")
	@ResponseBody
	public JsonModel getStockAreaForAllot(HttpServletRequest request) {
		
		Map<String, String> typeMap = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Map<Integer, String> tempMap = (Map<Integer, String>) CargoInfoBean.stockTypeMap;
		for (Map.Entry<Integer, String> entry : tempMap.entrySet()) {
			typeMap.put(entry.getKey().toString(), entry.getValue());
		}
		
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		Map<Integer, String> areaMap = ProductStockBean.areaMap;
		for (Map.Entry<Integer, String> entry : areaMap.entrySet()) {
			List<StockTypeBean> typeList = ProductStockBean.getStockTypeByArea(entry.getKey().intValue());
			if (typeList != null && typeList.size() > 0) {
				ArrayList<String> tempArr = new ArrayList<String>();
				for (StockTypeBean stockTypeBean : typeList) {
					if (stockTypeBean.getId() == ProductStockBean.STOCKTYPE_BACK 
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_QUALIFIED 
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_DEFECTIVE
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_RETURN
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_CHECK
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_AFTER_SALE) {
						tempArr.add(stockTypeBean.getId() + "");
					}
				}
				if (tempArr.size() > 0) {
					map.put(entry.getKey() + "", tempArr);
				}
			}
		}
		
		JsonModel model = returnSuccess("areaMap", areaMap);		
		model.getData().put("typeMap", typeMap);
		model.getData().put("map", map);
		return model;
	}

	/**
	 * 售后库调拨，判断售后处理单号是否可以进行调拨
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/judgeCodeForAllot")
	@ResponseBody
	public JsonModel judgeCodeForAllot(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		// 售后处理单号
		String code = "";
		int area = 1;

		try {
			JsonModel json = tools.getModelAndCheck(request, 1474);
			if (json.getFlag() == 0)
				return json;
			code = StringUtil.dealParam(json.getData().get("code").toString());
			if (code == null || "".equals(code)) {
				return returnError("参数错误");
			}
			area = StringUtil.toInt(json.getArea());
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + code + "' ");
			if (product == null)
				return returnError("售后处理单不存在");

			if (product.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0)
				return returnError("该商品已锁定不能调拨");

			// @mengqy 售后库调拨 不判断处理单状态

			CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
			if (cargoInfo == null) {
				return returnError("售后处理单关联货位不存在");
			}

			if (cargoInfo.getAreaId() != area || cargoInfo.getStockType() != CargoInfoBean.STOCKTYPE_AFTER_SALE) {
				return returnError("售后处理单已经不在售后库");
			}

			
			return returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
	}

	/**
	 * 完成售后库调拨，新建调拨单
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/afterSaleAllot")
	@ResponseBody
	public JsonModel afterSaleAllot(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		int inStockArea;
		int inStockType;
		ArrayList<String> codes;
		try {
			JsonModel json = tools.getModelAndCheck(request, 1474);
			if (json.getFlag() == 0)
				return json;
			codes = (ArrayList<String>) json.getData().get("codes");
			inStockArea = StringUtil.toInt(json.getData().get("inStockArea").toString());
			inStockType = StringUtil.toInt(json.getData().get("inStockType").toString());
			if (codes == null || codes.size() == 0) {
				return returnError("参数错误");
			}
		} catch (Exception e) {
			return returnError("数据异常!");
		}

		Json json = new CommonLogic().createAfterSaleAllot(inStockArea, inStockType, codes, tools.getUser(), 1, -1);
		if (json == null)
			return returnError("创建调拨作业失败");

		if (!json.isSuccess())
			return returnError(json.getMsg());

		return returnSuccess();
	}

	/**
	 * 通用工具类
	 * 
	 * @author mengqy
	 * 
	 */
	class CommonTools {
		private voUser user;

		public JsonModel getModelAndCheck(HttpServletRequest request, int groupFlag) {
			JsonModel json = ReceiveJson.receiveJson(request);// 从流中读取json数据
			if (json == null) {
				return returnError("没有收到请求数据!");
			}
			if (CheckUser.checkUser(request, json.getUserName(), json.getPassword())) {// 验证用户名密码
				user = (voUser) request.getSession().getAttribute("userView");
				if (groupFlag > 0) {
					UserGroupBean group = user.getGroup();
					if (!group.isFlag(groupFlag)) {
						return returnError("您没有相应的操作权限!");
					}
				}
			} else {
				return returnError("用户名密码验证失败!");
			}

			json.setFlag(1);
			return json;
		}

		public voUser getUser() {
			return user;
		}
	}

	private static JsonModel returnMsg(String results) {
		if (results != null)
			return returnError(results);
		return returnSuccess();
	}

	private static JsonModel returnError(String msg) {
		return returnError(msg, 0);
	}

	private static JsonModel returnError(String msg, int flag) {
		JsonModel json = new JsonModel();
		json.setFlag(flag);
		json.setData(null);
		json.setMessage(msg);
		return json;
	}

	private static JsonModel returnSuccess() {
		JsonModel json = new JsonModel();
		json.setFlag(1);
		json.setData(null);
		json.setMessage("操作成功");
		return json;
	}

	private static JsonModel returnSuccess(String key, Object value) {
		JsonModel json = new JsonModel();
		json.setFlag(1);
		json.setMessage("操作成功");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		json.setData(map);
		return json;
	}

}
