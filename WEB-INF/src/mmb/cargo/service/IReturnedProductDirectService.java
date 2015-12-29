package mmb.cargo.service;

import java.util.List;

import mmb.cargo.model.ReturnedProductDirectRequestBean;
import mmb.cargo.model.ReturnedProductVirtualRequestBean;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import adultadmin.action.vo.voUser;

public interface IReturnedProductDirectService {
	/**
	 * 
	 * @descripion 创建退货指向
	 * @author 刘仁华
	 * @time  2015年2月4日
	 */
	public boolean createDirect(ReturnedProductDirectRequestBean requetBean, voUser adminUser);

	/**
	 * 
	 * @descripion 更新退货指向
	 * @author 刘仁华
	 * @time  2015年2月4日
	 */
	public boolean updateDirect(ReturnedProductDirectRequestBean requetBean, voUser adminUser);
	
	/**
	 * 
	 * @descripion 获取退货指向列表
	 * @author 刘仁华
	 * @time  2015年2月5日
	 */
	public EasyuiDataGridJson getDirectData(ReturnedProductDirectRequestBean requestBean);
	
	/**
	 * 
	 * @descripion 作废退货指向记录
	 * @author 刘仁华
	 * @time  2015年2月5日
	 */
	public boolean cancelDirect(Integer directId, voUser adminUser);
	
	/**
	 * 
	 * @descripion 获取巷道最大层数
	 * @author 刘仁华
	 * @time  2015年2月4日
	 */
	public String getMaxFloorNum(String passage);
	
	/**
	 * 
	 * @descripion 获取巷道列表
	 * @author 刘仁华
	 * @time  2015年2月6日
	 */
	public EasyuiDataGridJson getPassageDetailLs(ReturnedProductDirectRequestBean requestBean);
	
	/**
	 * 
	 * @descripion 获取log列表
	 * @author 刘仁华
	 * @time  2015年2月6日
	 */
	public EasyuiDataGridJson getDirectLogLs(ReturnedProductDirectRequestBean requestBean);
	
	/**
	 * 
	 * @descripion 获取退货虚拟列表
	 * @author 刘仁华
	 * @time  2015年4月8日
	 */
	public EasyuiDataGridJson getVirtualData(ReturnedProductVirtualRequestBean requestBean);
	
	/**
	 * 
	 * @descripion 批量删除临时表
	 * @author 刘仁华
	 * @time  2015年4月8日
	 */
    public boolean deleteVirtualBatch(String virtualId);
}
