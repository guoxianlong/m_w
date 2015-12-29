package mmb.tms.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.model.BalanceCorpInfo;
import mmb.tms.model.DeliverBalanceType;
import mmb.tms.model.DeliverKpi;
import mmb.tms.model.DeliverLog;
import mmb.tms.model.DeliverMail;
import mmb.tms.model.DeliverSendDefault;
import mmb.tms.model.DeliverSendSpecial;
import mmb.tms.model.ProvinceCityDeliverTreeBean;

public interface IDeliverService {
	/**
	 * @describe 获取快递公司配置treegrid
	 * @param deleiver
	 * @return id
	 */
	public List<ProvinceCityDeliverTreeBean> getProvincesCitys(String id);
	/**
	 * @describe添加快递公司
	 * @param deleiver
	 * @return id
	 */
	public int addDeliverCorpInfoBean(DeliverCorpInfoBean deliver);
	/**
	 * @describe根据id获取快递公司
	 * @param id
	 * @return
	 */
	public DeliverCorpInfoBean getDeliverCorpInfo(Integer id);
	/**
	 * @describe获取快递公司列表
	 * @return
	 */
	public List<DeliverCorpInfoBean> getDelvierList(HashMap<String,Integer> map);
	/**
	 * @describe 更新快递公司
	 * @param deliver
	 * @return 更新条数
	 */
	public int updateDeliverCorpInfo(DeliverCorpInfoBean deliver);
	/**
	 * @describe添加快递公司与结算公司关系
	 * @param bean
	 * @return id
	 */
	public int addDeliverBalanceType(DeliverBalanceType bean);
	/**
	 * @describe根据快递公司id获取快递公司与结算公司关系
	 * @param deliverId
	 * @return
	 */
	public DeliverBalanceType getDeliverBalanceTypeByDeliverId(Integer deliverId);
	/**
	 * @describe更新快递公司与结算公司关系
	 * @param bean
	 * @return 更新条数
	 */
	public int updateDeliverBalanceType(DeliverBalanceType bean);
	/**
	 * @describe添加快递公司KPI指标
	 * @param bean
	 * @return id
	 */
	public int addDeliverKPI(DeliverKpi bean);
	/**
	 * @describe根据条件获取快递公司kpi指标列表
	 * @param map
	 * @return
	 */
	public List<DeliverKpi> getDeliverKpiList(HashMap<String,String> map);
	/**
	 * @describe更新快递公司KPI指标
	 * @param bean
	 * @return 更新条数
	 */
	public int updateDeliverKpi(DeliverKpi bean);
	/**
	 * @describe获取结算公司列表
	 * @return
	 */
	public List<BalanceCorpInfo> getBalanceCorpInfoList();
	/**
	 * @describe添加快递公司日志
	 * @param log
	 * @return 增加条数
	 */
	public int addDeliverLog(DeliverLog log);
	/**
	 * @describe根据条件获取快递公司日志列表
	 * @param condition
	 * @return
	 */
	public List<DeliverLog> getDeliverLogList(String condition);
	/**
	 * @describe根据条件获取邮件发送列表数量
	 * @param condition
	 * @return
	 */
	public int getDeliverMailCount(HashMap<String, String> map);
	/**
	 * @describe根据条件邮件发送列表
	 * @param condition
	 * @return
	 */
	public List<DeliverMail> getDeliverMailList(HashMap<String, String> map);
	/**
	 * @describe获取快递公司邮件信息
	 * 根据条件邮件发送列表1
	 * @param condition
	 * @return
	 */
	public List<DeliverCorpInfoBean> getDeliverMailList1(HashMap<String, String> map);
	/**
	 * 获取快递公司邮件信息
	 * @param condition
	 * @return
	 */
	public DeliverMail getDeliverMailInfo(HashMap<String, String> map);
	/**
	 * @describe更新快递公司邮件状态
	 * @param bean
	 * @return 更新条数
	 */
	public int updateDeliverMailStatus(DeliverMail bean);
	/**
	 * @describe添加快递公司邮件发送信息
	 * @param bean
	 * @return
	 */
	public int addDeliverMail(DeliverMail bean);
	/**
	 * @describe包裹单号监控列表
	 * @param condition
	 * @return
	 */
	public List<DeliverMail> getDeliverPackageCodeList(HashMap<String, String> map);
	/**
	 * @describe 添加快递公司配置
	 * @param bean source targetId
	 * @author syuf
	 * @date 2014-04-01
	 */
	public void addDeliverConfig(ProvinceCityDeliverTreeBean bean,byte source,int targetId);
	/**
	 * @describe 校验当前市是否已配置该快递公司
	 * @author syuf
	 * @date 2014-04-01
	 */
	public boolean checkConfig(int id, int deliverId,int areaId,int tableId);
	/**
	 * @describe 添加快递公司配置(全国剩余区域)
	 * @author syuf
	 * @date 2014-04-02
	 */
	public void addDeliverConfigDefault(ProvinceCityDeliverTreeBean bean);
	/**
	 * @describe 校验全国剩余区域是否已配置该快递公司
	 * @author syuf
	 * @date 2014-04-02
	 */
	public boolean checkConfigDefault(int deliverId, int areaId,int tableId);
	/**
	 * @describe 是否该省下有市的快递公司配置
	 * @author syuf
	 * @date 2014-04-02
	 */
	public boolean checkConfigOther(int provinceId);
	/**
	 * @describe 是否该省下已配置全境
	 * @author syuf
	 * @date 2014-04-02
	 */
	public boolean checkConfigAll(int provinceId);
	/**
	 * @describe 编辑快递公司配置
	 * @param bean source targetId
	 * @author syuf
	 * @date 2014-04-01
	 */
	public void updateDeliverConfigDefault(DeliverSendDefault bean);
	/**
	 * @describe 更新快递公司配置
	 * @param bean source targetId
	 * @author syuf
	 * @date 2014-04-01
	 */
	public void updateDeliverConfig(DeliverSendSpecial bean);
	/**
	 * @describe 删除快递公司配置(全国)
	 * @author syuf
	 * @date 2014-04-01
	 */
	public void delDeliverConfigDefault(String ids);
	/**
	 * @describe 删除快递公司配置
	 * @author syuf
	 * @date 2014-04-01
	 */
	public void delDeliverConfig(String ids);
	/**
	 * @describe 获取快递公司当日交接及时率
	 * @author syuf
	 * @date 2014-04-03
	 */
	public long[][] getDeliverTransitIntimeRate(int deliverId,String startDate,String endDate,int areaId);
	/**
	 * @describe 获取快递公司当日交接率
	 * @author syuf
	 * @date 2014-04-03
	 */
	public long[][] getDeliverTransitRate(int deliverId,String startDate,String endDate,int areaId);
	/**
	 * @describe 获取快递公司揽收及时率
	 * @author syuf
	 * @date 2014-04-03
	 */
	public long[][] getDeliverCollectRate(int deliverId,String startDate,String endDate,int areaId);
	/**
	 * @describe 获取快递公司到达当地及时率
	 * @author syuf
	 * @date 2014-04-03
	 */
	public long[][] getDeliverArriveRate(int deliverId,String startDate,String endDate,int areaId);
	/**
	 * @describe 获取快递公司投递及时率
	 * @author syuf
	 * @date 2014-04-03
	 */
	public long[][] getDeliverMailingRate(int deliverId,String startDate,String endDate,int areaId);
	/**
	 * @describe 获取复核率
	 * @author syuf
	 * @date 2014-04-09
	 */
	public long[][] getAuditOrderCount(int deliverId, String startDate,String endDate,int areaId);
	/**
	 * @describe 根据快递公司名字查找快递公司Id
	 * @author syuf
	 * @date 2014-04-04
	 */
	public int getDeliverIdByName(String deliverName);
	/**
	 * @describe获取快递公司的配送区域中所有省的名称
	 * @param condition
	 * @return
	 */
	public String getDeliverProviences(String condition);
	/**
	 * @describe获取快递公司的配送区域中所有城市的名称
	 * @param condition
	 * @return
	 */
	public String getDeliverCities(String condition);
	/**
	 * @describe 批量添加快递公司配置
	 * @author syuf
	 * @date 2014-04-10
	 */
	public void configDeliver(List<ProvinceCityDeliverTreeBean> addList,List<ProvinceCityDeliverTreeBean> editList);
	
	public void editDeliverSendConf(HttpServletRequest request);
}
