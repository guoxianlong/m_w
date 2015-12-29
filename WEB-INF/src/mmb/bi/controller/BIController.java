package mmb.bi.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.service.BiOrderFinishRateService;
import mmb.bi.service.BiProductLineDeliverService;
import mmb.bi.service.BiOrderInStockAgingService;
import mmb.bi.service.BiOrderInStockDurationService;
import mmb.bi.service.BiSplitOrderInfoService;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/BIController")
public class BIController {
	private static byte[] lock = new byte[0];
	@Autowired
	public BiProductLineDeliverService productLineDeliverService;
	
	/**
	 * @return 订单商品分析模块-产品线发货情况
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getProductLineDeliverDatagrid")
	@ResponseBody
	public List<?> getProductLineDeliverDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = productLineDeliverService.getProductLineDeliverList(request);
		if (j.isSuccess()) {
			return (List<?>)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * 导出excel订单商品分析模块-产品线发货情况
	 * @throws Exception 
	 */
	@RequestMapping("/exportProductLineDeliverList")
	@ResponseBody
	public EasyuiDataGridJson exportProductLineDeliverList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = productLineDeliverService.exportProductLineDeliverList(request, response);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * 单个产品线发货情况图表
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getProductLineDeliverChart")
	@ResponseBody
	public Json getProductLineDeliverChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return productLineDeliverService.getProductLineDeliverChart(request);
	}
	
	/**
	 * 产品线发货情况详情
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getProductLineDeliverDetail")
	@ResponseBody
	public EasyuiDataGridJson getProductLineDeliverDetail(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		Json j = productLineDeliverService.getProductLineDeliverDetail(request, easyuiDataGrid);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * 导出excel订单商品分析模块-产品线发货情况
	 * @throws Exception 
	 */
	@RequestMapping("/exportProductLineDeliverDetail")
	public void exportProductLineDeliverDetail(HttpServletRequest request,HttpServletResponse response) throws Exception {
		productLineDeliverService.exportProductLineDeliverDetail(request, response);
	}
	/**
	 * 所有产品线发货情况图表
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getProductLineDeliverCharts")
	@ResponseBody
	public Json getProductLineDeliverCharts(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return productLineDeliverService.getProductLineDeliverCharts(request);
	}
	
	@Autowired
	public BiSplitOrderInfoService splitOrderInfoServiceService;
	
	/**
	 * @return 订单越仓发货分析
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getBiSplitOrderInfoDatagrid")
	@ResponseBody
	public List<?> getBiSplitOrderInfoDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = splitOrderInfoServiceService.getBiSplitOrderList(request);
		if (j.isSuccess()) {
			return (List<?>)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * @return 订单越仓发货图表
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getBiSplitOrderCharts")
	@ResponseBody
	public Json getBiSplitOrderCharts(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return splitOrderInfoServiceService.getBiSplitOrderCharts(request);
	}
	/**
	 * 拆单、越仓发货订单明细
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getSpiltOrderInfoDetail")
	@ResponseBody
	public EasyuiDataGridJson getSpiltOrderInfoDetail(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		Json j = splitOrderInfoServiceService.getSpiltOrderInfoDetail(request, easyuiDataGrid);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * 导出excel拆单、越仓发货订单明细
	 * @throws Exception 
	 */
	@RequestMapping("/exportSpiltOrderInfoDetail")
	public void SpiltOrderInfoDetail(HttpServletRequest request,HttpServletResponse response) throws Exception {
		splitOrderInfoServiceService.excelSpiltOrderInfoDetail(request, response);
	}
	@Autowired
	public BiOrderFinishRateService biOrderFinishRateService;
	
	@Autowired
	public BiOrderInStockDurationService biOrderInStockDurationService;
	
	@Autowired
	public BiOrderInStockAgingService biOrderInStockAgingService;

	/**
	 * 当日订单完成情况
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getIntradayOrderCompleteDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getIntradayOrderCompleteDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = biOrderFinishRateService.getIntradayOrderComplete(request);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	
	/**
	 * 截单周期订单完成情况
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getCutOffOrderCompleteDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getCutOffOrderCompleteDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = biOrderFinishRateService.getCutOffOrderComplete(request);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	
	/**
	 * 订单明细
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/orderDetail")
	@ResponseBody
	public EasyuiDataGridJson orderDetail(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		Json j = biOrderFinishRateService.getOrderDetail(request, easyuiDataGrid);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	
	/**
	 * 导出订单明细
	 * @throws Exception 
	 */
	@RequestMapping("/exportOrderDetail")
	public void exportOrderDetail(HttpServletRequest request,HttpServletResponse response) throws Exception {
		biOrderFinishRateService.exportOrderDetail(request, response);
	}
	
	/**
	 * 订单各环节时间节点信息
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/orderInStockDetail")
	@ResponseBody
	public EasyuiDataGridJson orderInStockDetail(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		Json j = biOrderInStockDurationService.getOrderInStockDetail(request, easyuiDataGrid);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	
	/**
	 * 导出订单各环节时间节点信息
	 * @throws Exception 
	 */
	@RequestMapping("/exportOrderInStockDetail")
	public void exportOrderInStockDetail(HttpServletRequest request,HttpServletResponse response) throws Exception {
		biOrderInStockDurationService.exportOrderInStockDetail(request, response);
	}
	
	/**
	 * 订单完成率图表
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getOrderComplete")
	@ResponseBody
	public Json getOrderComplete(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderFinishRateService.getOrderComplete(request);
	}
	
	/**
	 * 订单完成率图表（根据地区划分）
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getOrderCompletePercent")
	@ResponseBody
	public Json getOrderCompletePercent(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderFinishRateService.getOrderCompletePercent(request);
	}
	
	public void returnErrJsp(HttpServletRequest request,HttpServletResponse response, Json j) throws ServletException, IOException {
		request.setAttribute("msg", j.getMsg());
		request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	}
	
	/**
	 * 订单在库时长
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getInStockDurationDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getInStockDurationDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = biOrderInStockDurationService.getInStockDuration(request);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	
	/**
	 * 订单在库时长
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getInStockDurationChart")
	@ResponseBody
	public Json getInStockDurationChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderInStockDurationService.getInStockDurationChart(request);
	}
	
	/**
	 * 订单在库时长(根据小时划分)
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getInStockDurationHourChart")
	@ResponseBody
	public Json getInStockDurationHourChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderInStockDurationService.getInStockDurationHourChart(request);
	}
	
	/**
	 * 各仓储环节单均处理时效
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getStoragePartInStockDurationChart")
	@ResponseBody
	public Json getStoragePartInStockDurationChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderInStockDurationService.getStoragePartInStockDurationChart(request);
	}
	
	
	/**
	 * 分环节单均处理时效
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getDividePartInStockDurationChart")
	@ResponseBody
	public Json getDividePartInStockDurationChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderInStockDurationService.getDividePartInStockDurationChart(request);
	}
	
	/**
	 * 分环节单均处理时效(根据小时划分)
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getDividePartInStockDurationHourChart")
	@ResponseBody
	public Json getDividePartInStockDurationHourChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderInStockDurationService.getDividePartInStockDurationHourChart(request);
	}
	
	/**
	 * 在库时效订单分析
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getInStockAgingDatagrid")
	@ResponseBody
	public List getInStockAgingDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = biOrderInStockAgingService.getInStockAging(request);
		if (j.isSuccess()) {
			return (List)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	
	/**
	 * 在库时效订单分析（图表）
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getInStockAgingChart")
	@ResponseBody
	public Json getInStockAgingChart(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		return biOrderInStockAgingService.getInStockAgingChart(request);
	}
}
