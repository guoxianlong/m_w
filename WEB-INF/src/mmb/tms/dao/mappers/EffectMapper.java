package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.EffectDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.util.Arith;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class EffectMapper extends AbstractDaoSupport implements EffectDao {

	@Override
	public List<Map<String,String>> getRegularclazzList(Map<String, String> map) {
		// TODO Auto-generated method stub
		List<Map<String,String>> list = getSession(DynamicDataSource.SLAVE).selectList(map);
		for(Map<String,String> data:list){
			int fhCount =Integer.parseInt(String.valueOf(data.get("fhCount")));
			int ttCount =Integer.parseInt(String.valueOf(data.get("ttCount")));
			int thCount =Integer.parseInt(String.valueOf(data.get("thCount")));
			int zxCount =Integer.parseInt(String.valueOf(data.get("zxCount")));
			int nxCount =Integer.parseInt(String.valueOf(data.get("nxCount")));
			
			
			data.put("ttper", Arith.div(ttCount*100,fhCount,2)+"%");//妥投率
			data.put("thper", Arith.div(thCount*100,fhCount,2)+"%");//退回率
			data.put("zxper", Arith.div(zxCount*100,fhCount,2)+"%");//正向在途率
			data.put("nxper", Arith.div(nxCount*100,fhCount,2)+"%");//逆向在途率
		}
		return list;
	}

	@Override
	public int getRegularClazzCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public List<Map<String, String>> getPrescriptionList(Map<String, String> map) {
		List<Map<String,String>> list = getSession(DynamicDataSource.SLAVE).selectList(map);
		return list;
	}
	
	@Override
	public List<Map<String, String>> getPrescriptionForThisCount(Map<String, String> map) {
		List<Map<String,String>> list = getSession(DynamicDataSource.SLAVE).selectList(map);
		return list;
	}
	
	@Override
	public List<Map<String, String>> getPrescriptionForRenturnCount(Map<String, String> map) {
		List<Map<String,String>> list = getSession(DynamicDataSource.SLAVE).selectList(map);
		return list;
	}

	@Override
	public int getPrescriptionCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}
	
	@Override
	public List<Map<String, String>> getCustomerList(Map<String, String> map) {
		// TODO Auto-generated method stub
		List<Map<String,String>> list = getSession(DynamicDataSource.SLAVE).selectList(map);
		List<Map<String,String>> list2 =this.getRegularclazzList(map);//取”常规类“总发货量
		for(int i = 0 ; i < list2.size() ; i++){
			for(int j = 0 ; j < list.size() ; j++){
				Map<String,String> map2 =list2.get(i);
				Map<String,String> ormap =list.get(j);
				if(String.valueOf(map2.get("tt")).equals(String.valueOf(ormap.get("tt2")))){
					ormap.put("totalOrderCount", String.valueOf(map2.get("fhCount")));//根据“常规类tt”字段和“客诉类tt2字段”，匹配总发货量
				}
			}
		}
		for(Map<String,String> data:list){
			int totalOrderCount =Integer.parseInt(String.valueOf(data.get("totalOrderCount")));
			int csksCount =Integer.parseInt(String.valueOf(data.get("csksCount")));
			int tdksCount =Integer.parseInt(String.valueOf(data.get("tdksCount")));
			int yzksCount =Integer.parseInt(String.valueOf(data.get("yzksCount")));
			
			
			data.put("zhengti",Arith.div(csksCount*100,totalOrderCount,2)+Arith.div(tdksCount*100,totalOrderCount,2)+Arith.div(yzksCount*100,totalOrderCount,2)+"%");//整体客诉类
			data.put("chaoshi", Arith.div(csksCount*100,totalOrderCount,2)+"%");//超时客诉率
			data.put("taidu", Arith.div(tdksCount*100,totalOrderCount,2)+"%");//态度客诉率
			data.put("yuanze", Arith.div(yzksCount*100,totalOrderCount,2)+"%");//原则客诉率
		}
		return list;
	}

	@Override
	public int getCustomerCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public List<Map<String, String>> getObservationList(Map<String, String> map) {
		List<Map<String,String>> list = getSession(DynamicDataSource.SLAVE).selectList(map);
		for(Map<String,String> data:list){
			int fhCount =Integer.parseInt(String.valueOf(data.get("fhCount")));
			int codCount =Integer.parseInt(String.valueOf(data.get("codCount")));
			int xcCount =Integer.parseInt(String.valueOf(data.get("xcCount")));
			int qwEmsCount =Integer.parseInt(String.valueOf(data.get("qwEmsCount")));
			int ldEmsCount =Integer.parseInt(String.valueOf(data.get("ldEmsCount")));
			int noEmsCount =Integer.parseInt(String.valueOf(data.get("noEmsCount")));
			
			data.put("codper", Arith.div(codCount*100,fhCount,2)+"%");//COD订单占比
			data.put("xcper", Arith.div(xcCount*100,fhCount,2)+"%");//乡村镇订单占比
			data.put("emsper", Arith.div(qwEmsCount*100,fhCount,2)+"%");//全网邮政体系订单占比
			data.put("ldEmsCount", Arith.div(ldEmsCount*100,fhCount,2)+"%");//落地邮政体系订单占比
			data.put("noEmsper", Arith.div(noEmsCount*100,fhCount,2)+"%");//落地配订单占比
		}
		return list;
	}

	@Override
	public int getObservationCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

}
