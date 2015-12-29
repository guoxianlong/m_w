package mmb.tms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import mmb.tms.dao.EffectDao;
import mmb.tms.service.IEffectService;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
@Service
public class EffectServiceImpl implements IEffectService {

	@Autowired
	private EffectDao effectDao;
	@Override
	public List getRegularClazz(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getRegularclazzList(map);
	}
	@Override
	public int getRegularClazzCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getRegularClazzCount(map);
	}
	@Override
	public List getPrescriptionList(Map<String, String> map) {
		List<Map<String,String>> list =effectDao.getPrescriptionList(map);
		List<Map<String,String>> list2 = this.getPrescriptionForThisCount(map);
		List<Map<String,String>> list3 = this.getPrescriptionForRenturnCount(map);
		for(int i = 0 ; i < list.size() ; i++){
				Map<String,String> data =list.get(i);
				Map<String,String> data2 = list2.get(i);
				Map<String,String> data3 = list3.get(i);
				data.put("glxzdl",String.valueOf(data2.get("glxzdl")));
				data.put("thwc_ddztzj", String.valueOf(data3.get("thwc_ddztzj")));
		}
		for(Map<String,String> data:list){
			int fhCount =Integer.parseInt(String.valueOf(data.get("fhCount")));
			int ttjsdd =Integer.parseInt(String.valueOf(data.get("ttjsdd")));
			int ttzdl =Integer.parseInt(String.valueOf(data.get("ttzdl")));
			int ttdlzsc =Integer.parseInt(String.valueOf(data.get("ttdlzsc")));
			int ddzxztzhs =Integer.parseInt(String.valueOf(data.get("ddzxztzhs")));
			int glxzdl =Integer.parseInt(String.valueOf(data.get("glxzdl")));
			int _24h =Integer.parseInt(String.valueOf(data.get("24h")));
			int _48h =Integer.parseInt(String.valueOf(data.get("48h")));
			int _72h =Integer.parseInt(String.valueOf(data.get("72h")));
			int _7day =Integer.parseInt(String.valueOf(data.get("7day")));
			int thwc_ddztzj =Integer.parseInt(String.valueOf(data.get("thwc_ddztzj")));
			int thwc_fhwc =Integer.parseInt(String.valueOf(data.get("thwc_fhwc")));
			int thdzl =Integer.parseInt(String.valueOf(data.get("thdzl")));
			
			data.put("ttjsl", Arith.div(ttjsdd*100,ttzdl,2)+"%");//妥投及时率
			data.put("ttpjhs", Arith.div(ttdlzsc,ttzdl,2)+"h");//妥投平均耗时
			data.put("pjtdhs", Arith.div(ddzxztzhs,glxzdl,2)+"h");//平均投递耗时
			data.put("ttl24", Arith.div(_24h*100,fhCount,2)+"%");//24小时妥投率
			data.put("ttl48", Arith.div(_48h*100,fhCount,2)+"%");//48小时妥投率
			data.put("ttl72", Arith.div(_72h*100,fhCount,2)+"%");//72小时妥投率
			data.put("zzztl7", Arith.div(_7day*100,fhCount,2)+"%");//72小时妥投率
			data.put("thTimes",Arith.div(thwc_ddztzj,thdzl*24,2)+"天");//退回天数
			data.put("thzzTimes",Arith.div(thwc_fhwc,thdzl*24,2)+"天");//退货整体周转天数
		}
		return list;
	}
	@Override
	public List getPrescriptionForThisCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getPrescriptionForThisCount(map);
	}
	
	@Override
	public List getPrescriptionForRenturnCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getPrescriptionForRenturnCount(map);
	}
	@Override
	public int getPrescriptionCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getPrescriptionCount(map);
	}
	@Override
	public List getCustomerList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getCustomerList(map);
	}
	@Override
	public int getCustomerCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getCustomerCount(map);
	}
	@Override
	public List getObservationList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getObservationList(map);
	}
	@Override
	public int getObservationCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return effectDao.getObservationCount(map);
	}
	
	
	@Override
	public void portExcel(String type,List<Map<String, String>> orderList,HttpServletResponse response) throws Exception {
		
		HashMap<String, String> map = null;
		int size = 0;
		String now = DateUtil.getNow().substring(0, 10);
		String fileName = now;
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		if("1".equals(type)){
			header.add("序号");
			header.add("发货仓");
			header.add("快递公司");
			header.add("省");
			header.add("市");
			header.add("区");
			header.add("乡/镇/街");
			header.add("产品线");
			header.add("发货量");
			header.add("妥投率");
			header.add("退回率");
			header.add("正向在途率");
			header.add("逆向在途率");
			
			size = header.size();
			if (orderList != null && orderList.size() > 0) {
				int x = orderList.size();
				for (int i = 0; i < x; i++) {
					map =  (HashMap<String, String>) orderList.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(i+1 + "");
					tmp.add(map.get("stock"));
					tmp.add(map.get("deliver"));
					tmp.add(map.get("sheng"));
					tmp.add(map.get("city"));
					tmp.add(map.get("area"));
					tmp.add(map.get("street"));
					tmp.add(map.get("chanpinxian"));
					tmp.add(String.valueOf(map.get("fhCount")));
					tmp.add(String.valueOf(map.get("ttper")));
					tmp.add(String.valueOf(map.get("thper")));
					tmp.add(String.valueOf(map.get("zxper")));
					tmp.add(String.valueOf(map.get("nxper")));
					bodies.add(tmp);
				}
			}
		}
		
		if("2".equals(type)){
			header.add("序号");
			header.add("发货仓");
			header.add("快递公司");
			header.add("省");
			header.add("市");
			header.add("区");
			header.add("乡/镇/街");
			header.add("产品线");
			header.add("妥投及时率");
			header.add("妥投平均耗时");
			header.add("平均投递耗时");
			header.add("24小时妥投率");
			header.add("48小时妥投率");
			header.add("72小时妥投率");
			header.add("正向在途超七天在途率");
			header.add("退货天数");
			header.add("退货整体周转天数");
			
			size = header.size();
			if (orderList != null && orderList.size() > 0) {
				int x = orderList.size();
				for (int i = 0; i < x; i++) {
					map =  (HashMap<String, String>) orderList.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(i+1 + "");
					tmp.add(map.get("stock"));
					tmp.add(map.get("deliver"));
					tmp.add(map.get("sheng"));
					tmp.add(map.get("city"));
					tmp.add(map.get("area"));
					tmp.add(map.get("street"));
					tmp.add(map.get("chanpinxian"));
					tmp.add(String.valueOf(map.get("ttjsl")));
					tmp.add(String.valueOf(map.get("ttpjhs")));
					tmp.add(String.valueOf(map.get("pjtdhs")));
					tmp.add(String.valueOf(map.get("ttl24")));
					tmp.add(String.valueOf(map.get("ttl48")));
					tmp.add(String.valueOf(map.get("ttl72")));
					tmp.add(String.valueOf(map.get("zzztl7")));
					tmp.add(String.valueOf(map.get("thTimes")));
					tmp.add(String.valueOf(map.get("thzzTimes")));
					bodies.add(tmp);
				}
			}
		}
		
		if("3".equals(type)){
			header.add("序号");
			header.add("发货仓");
			header.add("快递公司");
			header.add("省");
			header.add("市");
			header.add("区");
			header.add("乡/镇/街");
			header.add("产品线");
			header.add("整体客诉率");
			header.add("超时客诉率");
			header.add("态度客诉率");
			header.add("原则客诉率");
			//header.add("客诉平均处理时长");
			
			size = header.size();
			if (orderList != null && orderList.size() > 0) {
				int x = orderList.size();
				for (int i = 0; i < x; i++) {
					map =  (HashMap<String, String>) orderList.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(i+1 + "");
					tmp.add(map.get("stock"));
					tmp.add(map.get("deliver"));
					tmp.add(map.get("sheng"));
					tmp.add(map.get("city"));
					tmp.add(map.get("area"));
					tmp.add(map.get("street"));
					tmp.add(map.get("chanpinxian"));
					tmp.add(String.valueOf(map.get("zhengti")));
					tmp.add(String.valueOf(map.get("chaoshi")));
					tmp.add(String.valueOf(map.get("taidu")));
					tmp.add(String.valueOf(map.get("yuanze")));
					bodies.add(tmp);
				}
			}
		}
		
		if("4".equals(type)){
			header.add("序号");
			header.add("发货仓");
			header.add("省");
			header.add("市");
			header.add("区");
			header.add("地址级别（乡，镇，村）");
			header.add("产品线");
			header.add("COD订单占比");
			header.add("乡村镇订单占比");
			header.add("全网邮政订单占比");
			header.add("落地邮政订单占比");
			header.add("落地配订单占比");
			
			size = header.size();
			if (orderList != null && orderList.size() > 0) {
				int x = orderList.size();
				for (int i = 0; i < x; i++) {
					map =  (HashMap<String, String>) orderList.get(i);
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(i+1 + "");
					tmp.add(map.get("stock"));
					tmp.add(map.get("sheng"));
					tmp.add(map.get("city"));
					tmp.add(map.get("area"));
					tmp.add(map.get("street"));
					tmp.add(map.get("chanpinxian"));
					tmp.add(String.valueOf(map.get("codper")));
					tmp.add(String.valueOf(map.get("xcper")));
					tmp.add(String.valueOf(map.get("emsper")));
					tmp.add(String.valueOf(map.get("ldEmsCount")));
					tmp.add(String.valueOf(map.get("noEmsper")));
					bodies.add(tmp);
				}
			}
		}
		headers.add(header);
		/*允许合并列,下标从0开始，即0代表第一列*/
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		
		/*允许合并行,下标从0开始，即0代表第一行*/
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
        
		/*
		 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
		 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
		 * 
		 * */
		excel.setColMergeCount(size);
        
		
		/*
		 * 设置需要自己设置样式的行，以每个bodies为参照
		 * 具体的样式设置参考 DemoExcel.java中的setStyle方法
		 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
		 */
        List<Integer> row  = new ArrayList<Integer>();
        
        /*设置需要自己设置样式的列，以每个bodies为参照*/
        List<Integer> col  = new ArrayList<Integer>();
        
        excel.setRow(row);
        excel.setCol(col);
        
        //调用填充表头方法
        excel.buildListHeader(headers);
        
        //调用填充数据区方法
        excel.buildListBody(bodies);
        //文件输出
        excel.exportToExcel(fileName, response, "");
	}
}
