package adultadmin.util.timedtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import mmb.common.dao.ProductDao;
import mmb.common.dao.mappers.ProductMapper;
import mmb.dcheck.dao.DynamicCheckBeanDao;
import mmb.dcheck.dao.DynamicCheckCargoBeanDao;
import mmb.dcheck.dao.mappers.DynamicCheckBeanMapper;
import mmb.dcheck.dao.mappers.DynamicCheckCargoBeanMapper;
import mmb.dcheck.model.DynamicCheckBean;
import mmb.dcheck.model.DynamicCheckCargoBean;
import mmb.ware.cargo.dao.CargoInfoDao;
import mmb.ware.cargo.dao.CargoProductStockDao;
import mmb.ware.cargo.dao.mappers.CargoInfoMapper;
import mmb.ware.cargo.dao.mappers.CargoProductStockMapper;
import mmb.ware.cargo.model.CargoInfo;
import mmb.ware.cargo.model.CargoProductStock;
import mmb.ware.stock.dao.BsbyOperationnoteDao;
import mmb.ware.stock.dao.BsbyProductCargoDao;
import mmb.ware.stock.dao.SortingBatchOrderProductDao;
import mmb.ware.stock.dao.mappers.BsbyOperationnoteMapper;
import mmb.ware.stock.dao.mappers.BsbyProductCargoMapper;
import mmb.ware.stock.dao.mappers.SortingBatchOrderProductMapper;
import mmb.ware.stock.model.BsbyOperationnote;
import mmb.ware.stock.model.BsbyProductCargo;
import mmb.ware.stock.model.SortingBatchOrderProduct;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;







import com.mmb.framework.support.SpringHandler;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;


public class DynamicCheckCargoJob implements Job {
	
	public DynamicCheckBeanDao dynamicCheckMapper=SpringHandler.getBean(DynamicCheckBeanMapper.class);
	
	
	public DynamicCheckCargoBeanDao dynamicCheckCargoMapper=SpringHandler.getBean(DynamicCheckCargoBeanMapper.class);
	
	
	public  BsbyOperationnoteDao  bsbyOperationnoteMapper=SpringHandler.getBean(BsbyOperationnoteMapper.class);

	public BsbyProductCargoDao bsbyProductCargoMapper=SpringHandler.getBean(BsbyProductCargoMapper.class);

	public SortingBatchOrderProductDao sortingBatchOrderProductMapper=SpringHandler.getBean(SortingBatchOrderProductMapper.class);
	
	
	public CargoInfoDao cargoInfoMapper=SpringHandler.getBean(CargoInfoMapper.class);
	
	
	public CargoProductStockDao cargoProductStockMapper=SpringHandler.getBean(CargoProductStockMapper.class);
	

	public ProductDao productMapper=SpringHandler.getBean(ProductMapper.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("动碰盘盘点计划定时任务开始");
		
		//遍历可发货地区 
		HashMap<Integer,String>  areamap =ProductStockBean.stockoutAvailableAreaMap;
		
	   Set<Integer> areaids=areamap.keySet();
	   try {
		for (Integer areaid : areaids) { //遍历 库地区id
			   
			    //查询未完成的动碰盘 盘点计划
				Map<String, String> map=new HashMap<String, String>();
				StringBuffer condition = new StringBuffer();
				condition.append("1=1");
				condition.append(" and check_type =1 ");
				condition.append(" and status <> '3' ");
				condition.append(" and area_id='"+areaid+"' ");
				map.put("start", "0");
				map.put("count", "99999");
				map.put("condition", condition.toString());
			    List<DynamicCheckBean> dynamicCheckBeans=dynamicCheckMapper.getDynamicCheckBeanList(map);
			    if(dynamicCheckBeans!=null && dynamicCheckBeans.size()>0){// 说明还有未完成的动碰盘计划
			    	DynamicCheckBean	dynamicCheck=dynamicCheckBeans.get(0);
			    	//状态是if_del=0 就是没有被删除的单据
					//获取报损财务待审批的货位关联商品
					   String bsbycondition = "if_del=0 and type=0 and current_type=5 ";
					   List list = bsbyOperationnoteMapper.selectList(bsbycondition, -1,
								-1, "add_time desc");
					   TreeSet<String> cargoproduct=new TreeSet<String>();
					
					   for(int i=0;i<list.size();i++){
						BsbyOperationnote bean = (BsbyOperationnote)list.get(i);
						List<BsbyProductCargo> pcbs = bsbyProductCargoMapper.selectListSlave("bsby_oper_id = "+bean.getId(),0,999,null);
						if(pcbs!=null && pcbs.size()>0){
							for (BsbyProductCargo bsbyProductCargo : pcbs) {
								//将获取的货位关联商品 保存在集合中
								cargoproduct.add(bsbyProductCargo.getCargoId()+"-"+bsbyProductCargo.getBsbyProductId());
							}
						}
					   }
					//获取未完成的盘点明细
						Map<String, String> mxmap = new HashMap<String, String>();
						StringBuffer mxcondition = new StringBuffer();
						mxcondition.append("1=1");
						mxcondition.append(" and dynamic_check_id='" +dynamicCheck.getId()+ "'");
						mxmap.put("condition", mxcondition.toString());
						mxmap.put("start", "0");
						mxmap.put("count", "99999");
					   List<DynamicCheckCargoBean> dynamicCheckCargoBeans =dynamicCheckCargoMapper.getDynamicCheckCargoBeans(mxmap);
					   
					   if(dynamicCheckCargoBeans!=null && dynamicCheckCargoBeans.size()>0){
						   for (DynamicCheckCargoBean dynamicCheckCargoBean : dynamicCheckCargoBeans) {
							   cargoproduct.add(dynamicCheckCargoBean.getCargoId()+"-"+dynamicCheckCargoBean.getProductId());
						}
					   }
					   
					  //获取 分拣货位记录里存在的 货位上对应商品（库存量+锁定梁） < 10 且大于0 的
				
					 //取当天时间 0-23:59
				     String sbopcondition = "sorting_datetime  between '"+DateUtil.formatDate(new Date())+" 00:00:00" + "' and '"+DateUtil.formatDate(new Date())+" 23:59:59'";//取当天时间
					 List<CargoProductStock>  cpslistBeans=new ArrayList<CargoProductStock>();
					   List<SortingBatchOrderProduct> sbops = sortingBatchOrderProductMapper.selectList(sbopcondition, -1, -1, null);
					 if(sbops!=null && sbops.size()>0){
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
					
						  int maxid=dynamicCheck.getId();
						  if(cpslistBeans.size()>0){
						    for (CargoProductStock cargoProductStockBean : cpslistBeans) {
							//保存明细
						    	CargoInfo cargoInfoBean=cargoInfoMapper.selectByCondition("id="+cargoProductStockBean.getCargoId());//得到仓库id
							   DynamicCheckCargoBean  dCheckCargoBean=new DynamicCheckCargoBean();
							   dCheckCargoBean.setCargoId(cargoProductStockBean.getCargoId());
							   dCheckCargoBean.setCargoInfoStockAreaId(cargoInfoBean.getStockAreaId());
							   dCheckCargoBean.setCargoInfoPassageId(cargoInfoBean.getPassageId());
							   //取货位号
							   if(cargoInfoBean!=null && !"".equals(cargoInfoBean.getWholeCode())){
							   dCheckCargoBean.setCargoWholeCode(cargoInfoBean.getWholeCode());
							   }
							   //取商品信息
							   voProduct product=productMapper.getProduct("id="+cargoProductStockBean.getProductId());//商品
							   if(product!=null){
							   dCheckCargoBean.setDynamicCheckId(maxid);
							   dCheckCargoBean.setProductId(product.getId());
							   dCheckCargoBean.setProductCode(product.getCode());
							   dCheckCargoBean.setProductName(product.getName());
							   dCheckCargoBean.setStatus(0);
							   dCheckCargoBean.setCheckResult(0);
							   }
							   dynamicCheckCargoMapper.insert(dCheckCargoBean);
						    }
					     }
				}
		   }
	} catch (Exception e) {
		e.printStackTrace();
	}
		
		
		
		System.out.println("动碰盘盘点计划定时任务结束");
	}

}
