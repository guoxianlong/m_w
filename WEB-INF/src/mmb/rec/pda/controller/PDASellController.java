package mmb.rec.pda.controller;

import net.sf.ezmorph.bean.MorphDynaBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.CheckUser;
import mmb.rec.pda.util.ReceiveJson;
import mmb.stock.stat.PDASellService;
import mmb.ware.WareService;

import org.apache.ibatis.exceptions.IbatisException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * allot:调拨
 */
/**
 * PDA 快速调拨 和 下架返厂
 * 
 * @author megnqy
 * 
 */
@Controller
@RequestMapping("admin/PDASellController")
public class PDASellController {

	private static byte[] lock = new byte[0];

	/**
	 * 快速调拨，查询源货位商品库存
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/searchForQuickAllot")
	@ResponseBody
	public JsonModel searchForQuickAllot(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		String cargoWholeCode = "";
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			cargoWholeCode = json.getData().get("cargoWholeCode").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("数据异常!");
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		PDASellService sellService = new PDASellService(dbOp);
		try {
			CargoInfoBean cargo = service.getCargoInfo(" whole_code = '" + cargoWholeCode + "' ");
			if (cargo == null) {
				return returnError("货位不存在");
			}

			List<HashMap<String, Object>> list = sellService.getCargoProductCount(cargo.getId());
			if (list == null || list.size() == 0) {
				return returnError("没有可以调拨的商品");
			}

			return returnSuccess("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
	}

	/**
	 * 快速调拨
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/quickAllot")
	@ResponseBody
	public JsonModel quickAllot(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		List<MorphDynaBean> list = null;
		String outCargoWholeCode = "";
		String inCargoWholeCode = "";
		int area = 0;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			area = StringUtil.toInt(json.area);
			outCargoWholeCode = json.getData().get("outCargoWholeCode").toString();
			inCargoWholeCode = json.getData().get("inCargoWholeCode").toString();
			list = (ArrayList<MorphDynaBean>) json.getData().get("list");
			if (list == null || list.size() == 0)
				return returnError("请选择需要调拨的商品");
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("数据异常!");
		}

		synchronized (lock) {

			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			PDASellService sellService = new PDASellService(dbOp);
			try {
				CargoInfoBean outCargo = service.getCargoInfo(" whole_code = '" + outCargoWholeCode + "' ");
				if (outCargo == null) {
					return returnError("源货位不存在");
				}

				CargoInfoBean inCargo = service.getCargoInfo(" whole_code = '" + inCargoWholeCode + "' AND status IN (0, 1) ");
				if (inCargo == null) {
					return returnError("目的货位不存在或不可用");
				}
				
				if (inCargo.getStatus() == CargoInfoBean.STATUS1) {
					if (!service.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCargo.getId())) {
						return returnError("数据库操作失败:updateCargoInfo");
					}
				}
				
				if (inCargo.getStatus() == CargoInfoBean.STATUS1) {
					if (!service.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCargo.getId())) {
						return returnError("数据库操作失败:更新货位状态失败");
					}
				}
				
				if (inCargo.getStatus() == CargoInfoBean.STATUS1) {
					if (!service.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCargo.getId())) {
						return returnError("数据库操作失败:更新货位状态失败");
					}
				}
				
				if (area != outCargo.getAreaId() || area != inCargo.getAreaId()) {
					return returnError("不可操作其他地区的货位");
				}

				if (outCargo.getStockType() != CargoInfoBean.STOCKTYPE_QUALIFIED || outCargo.getStockType() != inCargo.getStockType()) {
					return returnError("该功能为合格库货位间调拨");
				}

				dbOp.startTransaction();

				String result = sellService.quickAllot(outCargo, inCargo, list, tools.getUser());

				if (result != null) {
					dbOp.rollbackTransaction();
					return returnError(result);
				}

				dbOp.commitTransaction();
				return returnSuccess();
			} catch (Exception e) {
				dbOp.rollbackTransaction();
				e.printStackTrace();
				return returnError("发生异常");
			} finally {
				dbOp.release();
			}

		}
	}

	/**
	 * 下架返厂
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/backSupplier")
	@ResponseBody
	public JsonModel backSupplier(HttpServletRequest request) {
		CommonTools tools = new CommonTools();
		String outCargoWholeCode = "";
		int area = 0;
		int count = 0;
		String productCode = "";
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			area = StringUtil.toInt(json.area);
			outCargoWholeCode = json.getData().get("outCargoWholeCode").toString();
			count = StringUtil.toInt(json.getData().get("count").toString());
			if (count <= 0)
				return returnError("要调拨的数量不能小于等于0");
			productCode = json.getData().get("productCode").toString();
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("数据异常!");
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		PDASellService sellService = new PDASellService(dbOp);
		WareService wareService = new WareService(dbOp);
		try {
			CargoInfoBean outCargo = cargoService.getCargoInfo(" whole_code = '" + outCargoWholeCode + "' ");
			if (outCargo == null) {
				return returnError("源货位不存在");
			}
			if (area != outCargo.getAreaId()) {
				return returnError("您选择的库地区和当前操作的货位所在的库地区不一致");
			}

			// 用于PDA打印商品条码
			List<voProduct> list = wareService.searchProduct(" a.code = '" + productCode + "' ", 0, 1, null);
			if (list == null || list.size() == 0)
				return returnError("商品不存在");
			voProduct vProduct = list.get(0);

			ProductStockBean outPs = psService.getProductStock("area = " + outCargo.getAreaId() + " and type=" + outCargo.getStockType() + " and product_id=" + vProduct.getId());
			if (outPs == null || outPs.getStock() < count)
				return returnError("商品库存不足");

			CargoProductStockBean outCps = cargoService.getCargoProductStock(" cargo_id = " + outCargo.getId() + " AND product_id = " + vProduct.getId());
			if (outCps == null || outCps.getStockCount() < count) {
				return returnError("源货位库存不足");
			}

			CargoInfoBean inCargo = cargoService.getCargoInfo(" area_id = " + outCargo.getAreaId() + " AND stock_type = " + CargoInfoBean.STOCKTYPE_BACK + " AND  store_type = " + CargoInfoBean.STORE_TYPE2);
			if (inCargo == null) {
				return returnError("目的货位不存在");
			}

			ProductStockBean inPs = psService.getProductStock("area = " + inCargo.getAreaId() + " and type=" + inCargo.getStockType() + " and product_id=" + vProduct.getId());
			if (inPs == null)
				return returnError("目的库商品库存不存在");

			CargoProductStockBean inCps = cargoService.getCargoProductStock(" cargo_id = " + inCargo.getId() + " AND product_id = " + vProduct.getId());
			if (inCps == null) {
				inCps = new CargoProductStockBean();
				inCps.setCargoId(inCargo.getId());
				inCps.setProductId(vProduct.getId());
				inCps.setStockCount(0);
				inCps.setStockLockCount(0);
				if (!cargoService.addCargoProductStock(inCps)) {
					return returnError("数据库操作失败：addCargoProductStock");
				}
				inCps.setId(cargoService.getDbOp().getLastInsertId());
			}

			dbOp.startTransaction();

			String result = sellService.backSupplier(outCargo, outPs, outCps, inCargo, inPs, inCps, vProduct, count, tools.getUser());

			if (!result.startsWith("?")) {
				dbOp.rollbackTransaction();
				return returnError(result);
			}

			dbOp.commitTransaction();
			JsonModel json = returnSuccess("code", result.substring(1));
			json.getData().put("productBarcode", vProduct.getProductBarcodeVO().barcode);
			json.getData().put("inCargoWholeCode", inCargo.getWholeCode());
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollbackTransaction();
			return returnError("发生异常");
		} finally {
			dbOp.release();
		}
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
		JsonModel json = new JsonModel();
		json.setFlag(0);
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
