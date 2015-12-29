package mmb.stock.spare.controller;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.JsonModelUtil;
import mmb.rec.pda.util.PDAUtil;
import mmb.stock.spare.service.SparePDAService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("sparePDAController")
public class SparePDAController {

	private static byte[] lock = new byte[0];

	@Autowired
	private SparePDAService spareService;
	
	/**
	 * 获取备用机商品信息
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getSpareProductInfo")
	@ResponseBody
	public JsonModel getSpareProductInfo(HttpServletRequest request){
		PDAUtil tools = new PDAUtil();
		
		JsonModel json = tools.getModelAndCheck(request, 3018);
		if(json.getFlag() == 0){
			return json;
		}
		
		try {			
			return this.spareService.getSpareProductInfo(JsonModelUtil.getString(json, "code"));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("发生异常");
		}
	}
	
	
	/**
	 * 备用机商品上架
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/spareProductUpshelf")
	@ResponseBody
	public JsonModel spareProductUpshelf(HttpServletRequest request){
		PDAUtil tools = new PDAUtil();
		
		JsonModel json = tools.getModelAndCheck(request, 3018);
		if(json.getFlag() == 0){
			return json;
		}
		
		synchronized (lock) {			
			try {			
				String spareCode = JsonModelUtil.getString(json, "code");
				String cargo = JsonModelUtil.getString(json, "cargo");
				this.spareService.spareProductUpshelf(spareCode, cargo, tools.getUser());
				return JsonModelUtil.success();
			} catch (Exception e) {
				e.printStackTrace();
				return JsonModelUtil.error(e.getMessage());
			}			
		}
	}
	
	
	/**
	 * 备用机换新机
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/replaceReserve")
	@ResponseBody
	public JsonModel ReplaceReserve(HttpServletRequest request){
		PDAUtil tools = new PDAUtil();
		
		JsonModel json = tools.getModelAndCheck(request, 3017);
		if(json.getFlag() == 0){
			return json;
		}
		
		String afCode = JsonModelUtil.getString(json, "afCode");
		String spareCode = JsonModelUtil.getString(json, "spareCode");
				
		synchronized (lock) {			
			try {
				this.spareService.replaceReserve(afCode, spareCode, tools.getUser());
				return JsonModelUtil.success();
			} catch (Exception e) {
				e.printStackTrace();
				return JsonModelUtil.error(e.getMessage());
			}			
		}
	}
	
}
