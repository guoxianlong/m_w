package mmb.rec.pda.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.CheckUser;
import mmb.rec.pda.util.ReceiveJson;
import mmb.stock.cargo.CargoDeptAreaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.WareJobResourceBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.service.impl.WareJobResourceServiceImpl;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IWareJobResourceService;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/SoLoginController")
public class SoLoginController {

	@RequestMapping("/soLogin")
	@ResponseBody
	public JsonModel soLogin(HttpServletRequest request) {
		JsonModel jsonModel = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		try {
			IWareJobResourceService jobService = new WareJobResourceServiceImpl(IBalanceService.CONN_IN_SERVICE,dbOp);
			CargoDeptAreaService areaService = new CargoDeptAreaService(IBalanceService.CONN_IN_SERVICE,dbOp);
			//从流中读取json数据
			jsonModel = ReceiveJson.receiveJson(request);
			Map<String,Object> data = new HashMap<String, Object>();
			//验证用户名密码
			if(jsonModel==null){
				jsonModel = new JsonModel();
				jsonModel.setFlag(0);
				jsonModel.setData(null);
				jsonModel.setMessage("没有收到请求数据!");
				return jsonModel;
			}
			if(CheckUser.checkUser(request,jsonModel.getUserName(), jsonModel.getPassword())){
				int jobId = (Integer)jsonModel.getData().get("jobId");
				List<WareJobResourceBean> jobResources = new ArrayList<WareJobResourceBean>();
				if(jobId != -1){
					jobResources = jobService.getWareJobResourceList("job_id=" + jobId, -1, -1, null);
				}else{
					jobResources = jobService.getWareJobResourceList("id>0", -1, -1, null);
				}
				if(jobResources.size() > 0){
					List<String> jobs = new ArrayList<String>();
					for(WareJobResourceBean job : jobResources){
						jobs.add(job.getResource());
					}
					data.put("jobs", jobs);
				}
				// 岗位id7 为售后岗位，售后岗员工无权限遮罩
				if (jobId != 7) {
					@SuppressWarnings("static-access")
					List<String> list = areaService.getCargoDeptAreaList(request);
					if(list != null && list.size() > 0){
						Map<String,String> areaMap = new HashMap<String,String>();
						for(String area : list){//map的key是id value是名字
							areaMap.put(area, ProductStockBean.getAreaName(Integer.parseInt(area)));
						}
						data.put("areaMap", areaMap);
					}
				} else {
					Map<String,String> areaMap = new HashMap<String,String>();
					List<StockAreaBean> list = ProductStockBean.getStockAreaByType(CargoInfoBean.STOCKTYPE_AFTER_SALE);
					if (list != null && list.size() > 0) {
						for (StockAreaBean bean : list) {
							areaMap.put(bean.getId()+"", bean.getName());
						}
					}
					list = ProductStockBean.getStockAreaByType(CargoInfoBean.STOCKTYPE_CUSTOMER);
					if (list != null && list.size() > 0) {
						for (StockAreaBean bean : list) {
							areaMap.put(bean.getId()+"", bean.getName());
						}
					}
					if (areaMap.size() > 0) {
						data.put("areaMap", areaMap);
					}
				}
				jsonModel.setMessage("登录验证成功！");
				jsonModel.setFlag(1);
				jsonModel.setData(data);
				return jsonModel;
			}else{
				jsonModel.setFlag(0);
				jsonModel.setData(null);
				jsonModel.setMessage("身份验证失败!");
				return jsonModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return jsonModel;
	}

}
