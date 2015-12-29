package mmb.cargo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.cargo.model.CargoInfoCity;
import mmb.cargo.service.ICargoService;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;
import mmb.rec.sys.easyui.Json;
import mmb.rec.sys.util.BeanColumnToTableColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.util.StringUtil;
/**
 * @Describe 货位城市控制器
 * @author syuf
 * @date 2014-01-21
 */
@Controller
@RequestMapping("/CargoInfoCity")
public class CargoInfoCityController {
	Lock lock = new ReentrantLock();
	@Autowired
	public ICargoService cargoService;
	
	/***
	 * @Describe 添加城市
	 * @param city 
	 * @return json
	 */
	@RequestMapping("/addCargoInfoCity")
	@ResponseBody
	public Json addCargoInfoCity(CargoInfoCity city){
		Json j = new Json();
		if(city == null){
			j.setMsg("接收数据异常!");
			return j;
		}
		city.setWholeCode(city.getCode());
		try {
			try{ 
				lock.lock(); 
					cargoService.addCargoInfoCity(city);
					j.setMsg("添加成功!");
			}finally{ 
				lock.unlock(); 
			} 
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
		return j;
	}
	/***
	 * @Describe 获取货位城市列表
	 * @param request
	 * @param response
	 * @param page easyui分页bean
	 * @param city 
	 * @return json格式的EasyuiDataGridJson
	 */
	@RequestMapping("/getCargoInfoCitys")
	@ResponseBody
	public EasyuiDataGridJson getCargoInfoCityDatagrid(HttpServletRequest request,HttpServletResponse response,
			EasyuiPageBean page,CargoInfoCity city){
		
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer condition = new StringBuffer();
		condition.append("1=1");
		if(!"".equals(StringUtil.checkNull(city.getCode()))){
			condition.append(" and code='" + city.getCode() + "'");
		}
		if(!"".equals(StringUtil.checkNull(city.getName()))){
			condition.append(" and name='" + city.getName() + "'");
		}
		map.put("condition",condition.toString());
		int rowCount = cargoService.getCargoInfoCityCount(map);
		datagrid.setTotal((long)rowCount);
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		String order = null;
		if (page.getSort() != null && !"".equals(page.getSort().trim())) {// 设置排序
			order = "";
			order+=BeanColumnToTableColumn.Transform(page.getSort()) + " " + page.getOrder();
		}
		if(order == null || "".equals(order)){
			order = " code asc";
		}
		map.put("order", order);
		List<CargoInfoCity> citys = cargoService.getCargoInfoCitys(map);
		datagrid.setRows(citys);
		return datagrid;
	}
}
