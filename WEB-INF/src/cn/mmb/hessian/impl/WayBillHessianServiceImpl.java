/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年8月10日 下午1:57:41 
 * @version V1.0   
 */
package cn.mmb.hessian.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.stock.stat.DeliverCorpInfoBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import adultadmin.util.DateUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.YtWayBill;
import cn.mmb.delivery.domain.model.YtWayBillTrace;
import cn.mmb.delivery.domain.model.vo.TraceInfo;
import cn.mmb.delivery.domain.service.WayBillService;
import cn.mmb.delivery.domain.service.WayBillServiceFactory;
import cn.mmb.delivery.infrastructrue.persistence.WayBillMapper;
import cn.mmb.hessian.WayBillHessianService;

/** 
 * @ClassName: WayBillHessianServiceImpl 
 * @Description: 物流信息接口实现类
 * @author: 叶二鹏
 * @date: 2015年8月10日 下午1:57:41  
 */
@Service
public class WayBillHessianServiceImpl implements WayBillHessianService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private WayBillServiceFactory wayBillServiceFactory;
	
	@Resource
	private WayBillMapper wayBillMapper;

	@Override
	public String processWayBillInformation(String json) {
		String jsonResult = this.createJsonResult(200, "配送信息接口调用成功！");
		try {
			WayBillService wayBillService = wayBillServiceFactory.create(DeliverCorpInfoBean.DELIVER_ID_YT_WX);
			List<WayBillTrace> needAddInfo = this.parseDelierInfoData(json);
			String result = wayBillService.updateWayBillInfo(needAddInfo);
			if (!result.equals("OK")) {
				jsonResult = this.createJsonResult(-1, "配送信息接口调用失败，msg:" + result);
				log.info("配送信息接口更新数据："+jsonResult);
			}
		} catch (Exception e) {
			jsonResult = this.createJsonResult(-1, "配送信息接口调用失败，msg:" + e.toString());
			e.printStackTrace();
			log.info("processWayBillInformation request: " + json);
			log.info("processWayBillInformation response:" + jsonResult);
		}
		return jsonResult;
	}
	
	private String createJsonResult(int code, String message){
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("code", code);
		m.put("message", message);
		JSONObject json = JSONObject.fromObject(m);
		return json.toString();
	}
	
	/** 
	 * 解析配送信息数据
	 * @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
	 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
	 */
	private List<WayBillTrace> parseDelierInfoData(String jsonStr) throws Exception {
		List<WayBill> params = new ArrayList<WayBill>();
		WayBillTrace wayBillInfo = new YtWayBillTrace();
		JSONObject json = JSONObject.fromObject(jsonStr);
		WayBill param = new YtWayBill();
		param.setMailNo(json.getString("deliveryId"));
		params.add(param);
		wayBillInfo.setWayBill(params);
		
		List<TraceInfo> trs = new ArrayList<TraceInfo>();
		JSONArray details = json.getJSONArray("trace_api_dtos");
		for(int i=0; i<details.size(); i++){
			TraceInfo traceInfo = new TraceInfo();
			JSONObject detail = details.getJSONObject(i);
			traceInfo.setDeliverNo(json.getString("deliveryId"));
			traceInfo.setInfo(detail.getString("ope_remark"));
			traceInfo.setTime(detail.getString("ope_time"));
			//统一时间格式
			long tim = DateUtil.getTime(traceInfo.getTime(), "yyyy-MM-dd HH:mm:ss");
			if (tim == 0) {
				tim = DateUtil.getTime(traceInfo.getTime(), "yyyy/MM/dd HH:mm:ss");
			}
			traceInfo.setTime(DateUtil.formatTime(new Date(tim)));
			traceInfo.setStatus(detail.getInt("ope_status"));
			trs.add(traceInfo);
		}
		wayBillInfo.setTraceInfo(trs);
		return this.getNeedAddWayBillInfo(wayBillInfo);
	}
	
	/** 
	 * @Description: 过滤掉已经存在的物流信息
	 * @return List<WayBillTrace> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 下午1:01:18 
	 */
	public List<WayBillTrace> getNeedAddWayBillInfo(WayBillTrace wayBillTrace)
			throws Exception {
		List<WayBillTrace> InfoRsList = new ArrayList<WayBillTrace>();
		List<WayBillTrace> traceList = wayBillMapper.getWayBillTrace(wayBillTrace.getWayBill());
		//去掉数据库已存在的物流信息
		for (WayBillTrace tr : traceList) {
			for (TraceInfo tri : tr.getTraceInfo()) {
				tri.setDeliverNo(tr.getDeliverNo());
				wayBillTrace.getTraceInfo().remove(tri);
			}
		}
		//将去掉已存在物流信息后需要保存的物流数据封装
		for (WayBillTrace tr : traceList) {
			tr.getTraceInfo().clear();
			for (TraceInfo tri : wayBillTrace.getTraceInfo()) {
				if (tr.getDeliverNo().equals(tri.getDeliverNo())) {
					tr.getTraceInfo().add(tri);
				}
			}
			//物流信息按时间排序
			Collections.sort(tr.getTraceInfo(), new Comparator<TraceInfo>(){
				@Override
				public int compare(TraceInfo arg0,
						TraceInfo arg1) {
					long compare = DateUtil.getTime(arg0.getTime(), "yyyy-MM-dd HH:mm:ss") - 
							DateUtil.getTime(arg1.getTime(), "yyyy-MM-dd HH:mm:ss");
					return compare>0?1:-1;
				}
			});
			//剩余需要更新的物流信息
			if (!tr.getTraceInfo().isEmpty()) {
				//状态取最新物流的
				tr.setStatus(tr.getTraceInfo().get(tr.getTraceInfo().size() - 1).getStatus());
				InfoRsList.add(tr);
			}
		}
		return InfoRsList;
	}

}
