package mmb.dcheck.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import mmb.common.dao.ProductDao;
import mmb.dcheck.dao.DynamicCheckBeanDao;
import mmb.dcheck.dao.DynamicCheckCargoBeanDao;
import mmb.dcheck.dao.DynamicCheckCargoDifferenceBeanDao;
import mmb.dcheck.model.DynamicCheckBean;
import mmb.dcheck.model.DynamicCheckCargoBean;
import mmb.dcheck.service.DCheckService;
import mmb.ware.cargo.dao.CargoInfoAreaDao;
import mmb.ware.cargo.dao.CargoInfoDao;
import mmb.ware.cargo.dao.CargoProductStockDao;
import mmb.ware.cargo.model.CargoInfo;
import mmb.ware.cargo.model.CargoInfoArea;
import mmb.ware.cargo.model.CargoProductStock;
import mmb.ware.stock.dao.BsbyOperationnoteDao;
import mmb.ware.stock.dao.BsbyProductCargoDao;
import mmb.ware.stock.dao.SortingBatchOrderProductDao;
import mmb.ware.stock.model.BsbyOperationnote;
import mmb.ware.stock.model.BsbyProductCargo;
import mmb.ware.stock.model.SortingBatchOrderProduct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voProduct;
import adultadmin.util.DateUtil;
@Service
public class DCheckServiceImpl  implements DCheckService{
	
	@Autowired
	public DynamicCheckBeanDao dynamicCheckMapper;
	
	@Autowired
	public CargoInfoAreaDao areaMapper;
	
	@Autowired
	public DynamicCheckCargoBeanDao dynamicCheckCargoMapper;
	@Autowired
	public  BsbyOperationnoteDao  bsbyOperationnoteMapper;
	@Autowired
	public BsbyProductCargoDao bsbyProductCargoMapper;
	@Autowired
	public SortingBatchOrderProductDao sortingBatchOrderProductMapper;
	
	@Autowired
	public CargoInfoDao cargoInfoMapper;
	
	@Autowired
	public CargoProductStockDao cargoProductStockMapper;
	
	@Autowired
	public ProductDao productMapper;
	@Autowired
	public DynamicCheckCargoDifferenceBeanDao dCheckCargoDifferenceBeanMapper;
	
	@Override
	public List<DynamicCheckBean> getDynamicCheckBeans(
			Map<String, String> condition) {
		List<DynamicCheckBean> dynamicCheckBeans=dynamicCheckMapper.getDynamicCheckBeanList(condition);
		if(dynamicCheckBeans!=null && dynamicCheckBeans.size()>0){
			for (DynamicCheckBean dynamicCheckBean : dynamicCheckBeans) {
				//根据库地区id  获取库地区名称
				CargoInfoArea area=areaMapper.selectByPrimaryKey(dynamicCheckBean.getAreaId());
				if(area!=null && area.getName()!=null){
					dynamicCheckBean.setAreaName(area.getName());
				}else{
					dynamicCheckBean.setAreaName("未知地区");
				}
				Map<String, String> conditionmap=new HashMap<String, String>();
				//获取盘点完成量
				String completeCondition=" 1=1 and dynamic_check_id='"+dynamicCheckBean.getId()+"' and status=3 ";
				conditionmap.put("condition", completeCondition);
				dynamicCheckBean.setCompleteCount(dynamicCheckCargoMapper.selectCount(conditionmap));
				//获取盘点差异量
				
				String cifferenceCondition=" 1=1 and dynamic_check_id='"+dynamicCheckBean.getId()+"' and  (check_result=3 or check_result=2) ";
				conditionmap.put("condition", cifferenceCondition);
				dynamicCheckBean.setDifferenceCount(dynamicCheckCargoMapper.selectCount(conditionmap));
				
			}
		}
		
		return dynamicCheckBeans;
	}

	@Override
	public int getDynamicCheckBeanCount(Map<String, String> condition) {
		
		return dynamicCheckMapper.getDynamicCheckBeanCount(condition);
	}

	@Override
	public int addDynamicCheckBean(DynamicCheckBean dynamicCheck) {
		int resultcount = 0;
	
		try {
			//不同的盘点类型   查询的条件也不同
			if(dynamicCheck.getCheckType() ==2){// 大盘 计划 
				//查询是否有未完成的大盘盘点计划
				Map<String, String> map=new HashMap<String, String>();
				StringBuffer condition = new StringBuffer();
				condition.append("1=1");
				condition.append(" and check_type = '"+dynamicCheck.getCheckType()+"' ");
				condition.append(" and area_id = '"+dynamicCheck.getAreaId()+"' ");
				condition.append(" and status <> '3' ");
				map.put("start", "0");
				map.put("count", "99999");
				map.put("condition", condition.toString());
			    List<DynamicCheckBean> dynamicCheckBeans=dynamicCheckMapper.getDynamicCheckBeanList(map);
			    if(dynamicCheckBeans!=null && dynamicCheckBeans.size()>0){// 说明还有未完成的大盘计划
			    	resultcount=2;	
			    }else{ 
			     int id=dynamicCheckMapper.insert(dynamicCheck);
			     if(id>0){
			    	resultcount=1;
			     }
			    	
			    }
			}
			if(dynamicCheck.getCheckType() ==1 ){//动碰盘计划
				
				//查询是否有未完成的动碰盘 盘点计划
				Map<String, String> map=new HashMap<String, String>();
				StringBuffer condition = new StringBuffer();
				condition.append("1=1");
				condition.append(" and check_type = '"+dynamicCheck.getCheckType()+"' ");
				condition.append(" and area_id = '"+dynamicCheck.getAreaId()+"' ");
				condition.append(" and status <> '3' ");
				map.put("start", "0");
				map.put("count", "99999");
				map.put("condition", condition.toString());
			    List<DynamicCheckBean> dynamicCheckBeans=dynamicCheckMapper.getDynamicCheckBeanList(map);
			    if(dynamicCheckBeans!=null &&dynamicCheckBeans.size()>0){// 说明还有未完成的动碰盘计划
			    	resultcount=2;	
			    }else{
				//状态是if_del=0 就是没有被删除的单据
				//获取报损财务待审批的货位关联商品
				   String bsbycondition = "if_del=0 and type=0 and current_type=5 ";
				   List list = bsbyOperationnoteMapper.selectList(bsbycondition, -1,
						-1, "add_time desc");
				   TreeSet<String> cargoproduct=new TreeSet<String>();
				
				   for(int i=0;i<list.size();i++){
					BsbyOperationnote bean = (BsbyOperationnote)list.get(i);  //(String condition, int index, int count, String orderBy)
					List<BsbyProductCargo> pcbs = bsbyProductCargoMapper.selectListSlave("bsby_oper_id = "+bean.getId(),0,999,null);
					if(pcbs!=null && pcbs.size()>0){
						for (BsbyProductCargo bsbyProductCargo : pcbs) {
							//将获取的货位关联商品 保存在集合中
							cargoproduct.add(bsbyProductCargo.getCargoId()+"-"+bsbyProductCargo.getBsbyProductId());
						}
					}
					
				   }
				  
				
				  //获取 分拣货位记录里存在的 货位上对应商品（库存量+锁定梁） < 10 且大于0 的
			
				 //取当天时间 0-23:59
			     String sbopcondition = "sorting_datetime  between '"+DateUtil.formatDate(new Date())+" 00:00:00" + "' and '"+DateUtil.formatDate(new Date())+" 23:59:59'";//取当天时间
				 List<CargoProductStock>  cpslistBeans=new ArrayList<CargoProductStock>();
			     List<SortingBatchOrderProduct> sbops = sortingBatchOrderProductMapper.selectListSlave(sbopcondition, -1, -1, null);
				 if(sbops.size()>0){
			     for (SortingBatchOrderProduct sortingBatchOrderProductBean : sbops) {
			    	 CargoInfo cargoInfoBean=cargoInfoMapper.selectByCondition("id="+sortingBatchOrderProductBean.getCargoId());//得到仓库id
			    	   //判断 区域是否相同 
			    	   if(cargoInfoBean!=null){
			    		   if(cargoInfoBean.getAreaId() == dynamicCheck.getAreaId()){
					          CargoProductStock cps = cargoProductStockMapper.selectByConditionSlave("product_id='"+sortingBatchOrderProductBean.getProductId()+"' and cargo_id ='"+sortingBatchOrderProductBean.getCargoId()+"'");
					           if(cps!=null ){
					           int stockcount=cps.getStockCount()+cps.getStockLockCount();
					             if(stockcount <10 && stockcount >0 ){
						          if(cargoproduct.add(cps.getCargoId()+"-"+cps.getProductId())){
							       cpslistBeans.add(cps);
						           }
					             }
					           }
			    		   }
			    	   }
				 }
				 }
				  int id=dynamicCheckMapper.insert(dynamicCheck);
				  if(id>0){ //成功
				     resultcount=1;
					  if(cpslistBeans.size()>0){
					    for (CargoProductStock cargoProductStockBean : cpslistBeans) {
						
						 //保存明细
					    	CargoInfo cargoInfoBean=cargoInfoMapper.selectByCondition("id="+cargoProductStockBean.getCargoId());//得到仓库id
						   DynamicCheckCargoBean  dCheckCargoBean=new DynamicCheckCargoBean();
						   dCheckCargoBean.setCargoId(cargoProductStockBean.getCargoId());
						   dCheckCargoBean.setCargoInfoStockAreaId(cargoInfoBean.getStockAreaId());
						   dCheckCargoBean.setCargoInfoPassageId(cargoInfoBean.getPassageId());
						 
						   if(cargoInfoBean!=null && !"".equals(cargoInfoBean.getWholeCode())){
						   dCheckCargoBean.setCargoWholeCode(cargoInfoBean.getWholeCode());
						   }
						   //取商品信息
						   voProduct product=productMapper.getProduct("id="+cargoProductStockBean.getProductId());//商品
						   if(product!=null){
						   dCheckCargoBean.setDynamicCheckId(id);
						   dCheckCargoBean.setProductId(product.getId());
						   dCheckCargoBean.setProductCode(product.getCode());
						   dCheckCargoBean.setProductName(product.getName());
						   dCheckCargoBean.setStatus(0);
						   dCheckCargoBean.setCheckResult(0);
						   }
						   dynamicCheckCargoMapper.insert(dCheckCargoBean);
					    }
				     }
				  }else{
					  resultcount=0;  
				  }
			    }
			}
		} catch (Exception e) {
			 resultcount=0;  
			e.printStackTrace();
			return resultcount;
		}
		return resultcount;
	}

	@Override
	public int endDCheck(DynamicCheckBean dynamicCheckBean) {
		int count=0;
		try {
			StringBuffer set = new StringBuffer();
			set.append(" status = '3',complete_time='"+dynamicCheckBean.getCompleteTime()+"',complete_user_id='"+dynamicCheckBean.getCompleteUserId()+"',complete_username='"+dynamicCheckBean.getCompleteUsername()+"' ");
			String condition="id='"+dynamicCheckBean.getId()+"'";
			count=dynamicCheckMapper.updateByCondition(set.toString(), condition);
		} catch (Exception e) {
			count=0;
			e.printStackTrace();
			return count;
		}finally{
		}
		return count;
	}

	@Override
	public List<DynamicCheckCargoBean> getDynamicCheckCargoBeans(
			Map<String, String> condition) {
		
		return dynamicCheckCargoMapper.getDynamicCheckCargoBeans(condition);
	}

	@Override
	public int getDynamicCheckCargoBeanCount(Map<String, String> condition) {
		
		return dynamicCheckCargoMapper.getDynamicCheckCargoBeanCount(condition);
	}

	@Override
	public int afreshDCheck(Integer id) {
		int count=0;
		try {
			count=dynamicCheckCargoMapper.afreshDCheck(id);
		
			//
			DynamicCheckCargoBean dBean=dynamicCheckCargoMapper.selectByPrimaryKey(id);
		DynamicCheckBean dynamicCheckBean=	dynamicCheckMapper.selectByCondition("id="+dBean.getDynamicCheckId());
			if(dynamicCheckBean.getStatus()==3){
				count=-1;
			}else{
			 if(dBean!=null){
				StringBuffer set = new StringBuffer();
				set.append(" status = '3',difference='0'");
				String condition="cargo_id='"+dBean.getCargoId()+"' and product_id='"+dBean.getProductId()+"' and status=1";
				dCheckCargoDifferenceBeanMapper.updateByCondition(set.toString(), condition);
			 }
			}
		} catch (Exception e) {
			count=0;
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public DynamicCheckBean getDynamicCheckBean(String condition) {
		
		return dynamicCheckMapper.selectByCondition(condition);
	}

	@Override
	public CargoInfo getCargoByCondition(String condition) {
		return cargoInfoMapper.selectByCondition(condition);
	}

}
