package mmb.rec.pda.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.StringUtil;
import mmb.rec.pda.util.JsonModelUtil;

/**
 * PDA工具类
 * @author mengqy
 *
 */
public class PDAUtil {
	private voUser user;

	/**
	 * 验证用户名密码 并 获取输入参数
	 * 
	 * @param request
	 * @param groupFlag
	 *            权限
	 * @param map
	 *            获取到的参数
	 * @param callback
	 *            回调函数
	 * @return
	 */
	public JsonModel getParamMap(HttpServletRequest request, int groupFlag, HashMap<String, Object> map, PDAParamCallBack callback) {
		try {
			JsonModel json = this.getModelAndCheck(request, groupFlag);
			if (json.getFlag() == 0) {
				return json;
			}

			int areaId = -1;
			if (json.getArea() != null) {
				areaId = StringUtil.toInt(json.getArea());
			}
			map.put("areaId",  areaId + "");

			if (callback != null && !callback.getParam(json, map)) {
				return JsonModelUtil.error((callback.getMsg() == null) ? "获取参数失败" : callback.getMsg());
			}

			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常");
		}
	}

	
	/**
	 * 获取请求数据并验证用户名密码及操作权限
	 * @param request
	 * @param groupFlag 权限id
	 * @return
	 */
	public JsonModel getModelAndCheck(HttpServletRequest request, int groupFlag) {
		// 从流中读取json数据
		JsonModel json = ReceiveJson.receiveJson(request);
		if (json == null) {
			return JsonModelUtil.error("没有收到请求数据!");
		}
		// 验证用户名密码
		if (CheckUser.checkUser(request, json.getUserName(), json.getPassword())) {
			user = (voUser) request.getSession().getAttribute("userView");
			if (!checkGroupFlag(groupFlag)) {
				return JsonModelUtil.error("您没有相应的操作权限!");
			}
		} else {
			return JsonModelUtil.error("用户名密码验证失败!");
		}

		json.setFlag(1);
		return json;
	}


	/**
	 * 判断权限
	 * @param groupFlag
	 * @return
	 */
	public boolean checkGroupFlag(int groupFlag) {
		if (groupFlag > 0) {			
			if (user == null) {
				return false;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(groupFlag)) {
				return false;
			}
		}
		return true;
	}

	public voUser getUser() {
		return user;
	}
	
	/**
	 * 说明：判断用户是否拥有对应地区售后库权限
	 * 
	 * @param areaId
	 *            地区ID
	 */
	public boolean checkAfterSaleUserGroup(int areaId) {
		return checkAfterSaleUserGroup(this.getUser(), areaId);
	}
	
	/**
	 * 说明：判断用户是否拥有对应地区售后库权限
	 * 
	 * @param user 用户
	 * @param areaId
	 *            地区ID
	 */
	public static boolean checkAfterSaleUserGroup(voUser user, int areaId) {
		UserGroupBean group = user.getGroup();
		if (areaId == ProductStockBean.AREA_SZ) {
			if (!group.isFlag(2063)) {
				return false;
			}
		} else if (areaId == ProductStockBean.AREA_GF) {
			if (!group.isFlag(2062)) {
				return false;
			}
		} else if (areaId == ProductStockBean.AREA_WX) {
			if (!group.isFlag(2188)) {
				return false;
			}
		}
		return true;
	}
}
