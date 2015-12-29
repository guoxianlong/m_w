package mmb.stock.fitting.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.fitting.model.CargoInfoBean;
import mmb.stock.fitting.model.FittingStockCardPostBean;
import mmb.stock.fitting.service.FittingStockCardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.util.StringUtil;

@RequestMapping("fittingStockCardController")
@Controller
public class FittingStockCardController {

	@Autowired
	private FittingStockCardService stockCardService;

	@RequestMapping("/getOutStockCardListForCustomer")
	@ResponseBody
	public EasyuiDataGridJson getOutStockCardListForCustomer(HttpServletRequest request, HttpServletResponse response, 
			FittingStockCardPostBean postBean, EasyuiDataGrid page) {
		postBean.setStockType(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);
		return this.stockCardService.getOutStockCardList(postBean, page);
	}

	@RequestMapping("/getInStockCardListForCustomer")
	@ResponseBody
	public EasyuiDataGridJson getInStockCardListForCustomer(HttpServletRequest request, HttpServletResponse response, 
			FittingStockCardPostBean postBean, EasyuiDataGrid page) {
		postBean.setStockType(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);
		return this.stockCardService.getInStockCardList(postBean, page,"-1");
	}

	@RequestMapping("/getOutStockCardListForAfterSale")
	@ResponseBody
	public EasyuiDataGridJson getOutStockCardListForAfterSale(HttpServletRequest request, HttpServletResponse response, 
			FittingStockCardPostBean postBean, EasyuiDataGrid page) {
		postBean.setStockType(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
		return this.stockCardService.getOutStockCardList(postBean, page);
	}

	@RequestMapping("/getInStockCardListForAfterSale")
	@ResponseBody
	public EasyuiDataGridJson getInStockCardListForAfterSale(HttpServletRequest request, HttpServletResponse response, 
			FittingStockCardPostBean postBean, EasyuiDataGrid page) {
		postBean.setStockType(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
		String stockInType = StringUtil.convertNull(request.getParameter("stockInType"));
		return this.stockCardService.getInStockCardList(postBean, page,stockInType);
	}

}
