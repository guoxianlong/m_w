package adultadmin.action.cargo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.bean.cargo.CargoOperationCargoBean;


/**
 * 保存cargo相关操作的通用代码
 * @author wangtao
 */
public class CargoUtils {

	// 计算 补货/调货数量
	/**
	 * @param nowStockCount : 货位当前库存
	 * @param maxStockCount : 货位最大容量
	 * @param stockCount ：整件区剩余库存量
	 */
	public synchronized static int getPacketOrExchangeCount(int nowStockCount, int maxStockCount, int leftStockCount){
		int num = maxStockCount - nowStockCount;
		// 最大容量小于当前库存
		if(num  < 0){
			return 0;
		}
		if(num < leftStockCount){
			return num;
		}else{
			return leftStockCount;
		}
	}
	
	/**
	 * 
	 * @param header 作业单头标志    : 下架单:HWX  货位调拨:HWD   货位补货:HWB   货位上架:HWS  
	 * 1103120001 2011-03-12 0001单
	 *  @param code 数据库中Max(id)最大值 的作业单号
	 * @return 新的作业单 编号
	 */
	public  static String getOperationID(String header,String code){
		StringBuilder  id = new StringBuilder(header);
		java.util.Calendar ca = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat f =  new java.text.SimpleDateFormat ("yyyy-MM-dd");
		String data = f.format(ca.getTime());
		id.append(data.substring(2, 4));
		id.append(data.substring(5,7));
		id.append(data.substring(8,10));
		String newId = data.substring(2, 4)+data.substring(5,7)+data.substring(8,10);
		
		if(code!=null && !code.equals("")){
			String oldId = code.substring(3, 9); //得到110312 判断是否为同一天
			if(newId.equals(oldId)||newId==oldId){
				int temp=Integer.parseInt( code.substring(9));
				temp=temp+1;
				if(temp>=10000)
					id.append(temp);
				else if(temp>=1000)
					id.append("0"+temp);
				else if(temp>=100)
					id.append("00"+temp);
				else if(temp>=10)
					id.append("000"+temp);
				else if(temp>=1)
					id.append("0000"+temp);
			}else{
				id.append("00001");
			}
		}else{
			id.append("00001");
		}
		
		return id.toString();
	}
	
	/**
	 * 转变货位单list的方式
	 * @param list 里面包含 arraylist , arraylist里面包含 Cargo_operation_cargo bean对象 循环遍历arrayList 取出期值 如果Map中已经包含 则Map中对象值数量加上包含
	 * @return
	 */
	public static Map changeOperationList(List list){
		Map hashMap = new HashMap();
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				List listSub = (List)list.get(i);
				for(int j=0;j<listSub.size();j++){
					CargoOperationCargoBean bean = (CargoOperationCargoBean)listSub.get(j);
					String id=String.valueOf(bean.getProductId());
		            if (!hashMap.containsKey(id)) {
		            	hashMap.put(id, bean); 
					}else{
						CargoOperationCargoBean beanSame= (CargoOperationCargoBean)hashMap.get(id);
						beanSame.setStockCount(beanSame.getStockCount()+bean.getStockCount());
					}
				}
			}
		}
		return hashMap;
	}
	
}
