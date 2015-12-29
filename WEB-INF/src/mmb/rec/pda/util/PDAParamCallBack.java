package mmb.rec.pda.util;

import java.util.HashMap;

import mmb.rec.pda.bean.JsonModel;

/**
 * 获取参数的抽象类
 * 
 * @author mengqy
 * 
 */
 public abstract class PDAParamCallBack {

	/**
	 * 错误时的提示信息
	 */
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 获取参数，需要被实现的方法
	 * 
	 * @param json
	 * @param map
	 * @return 返回获取参数是否成功
	 */
	public abstract boolean getParam(JsonModel json, HashMap<String, Object> map);

}
