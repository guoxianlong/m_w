/**
 * 
 */
package mmb.rec.oper.controller;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/FProductBarcodeController")
public class FProductBarcodeController {
	public static String noSession = "/admin/rec/oper/salesReturned/noSession.jsp";
	
	@RequestMapping("/seracheProductBarcodes")
	@ResponseBody
	public EasyuiDataGridJson seracheProductBarcodes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String barcodes = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("barcodes")));
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		if(barcodes.length() == 0) {
			return null;
		}
		String[] barcode = barcodes.split("\n");
/*		if(barcode.length>30){
			request.setAttribute("tip", "一次最多可以查30个条码。");
    		request.setAttribute("result", "failure");
    		return ;
		}*/
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService service = new WareService();
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        try {
        	StringBuilder buf = new StringBuilder();
        	buf.append("(");
        	for(int i = 0 ; i < barcode.length ;i++ ){
        		if(barcode[i].trim().length()>0){
        			buf.append("pb.barcode='");
        			buf.append(barcode[i].trim().replaceAll("\n", ""));
        			buf.append("' || ");
        		}
        	}
        	if( buf.length() > 0 ){
        		buf.replace(buf.length()-3, buf.length(), "");
        	} else {
        		request.setAttribute("msg", "查询条件错误!");
    			request.getRequestDispatcher(noSession).forward(request, response);
    			return null;
        	}
        	buf.append(") and barcode_status=0");

        	List list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc");
            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	voProduct product = (voProduct)iter.next();
            	List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
            	product.setPsList(psList);
            	//库存总数
				int allStock = product.getStock(0) + product.getStock(1) + product.getStock(2)+product.getStock(3)+product.getStock(4) + product.getLockCount(0) + product.getLockCount(1) + product.getLockCount(2) + product.getLockCount(3) + product.getLockCount(4);
				product.setAllStockCount(allStock);
				//--------------------------------
				//可发货总数
				int stock=product.getStock(0, 0) + product.getStock(1, 0) + product.getStock(2, 0)+ product.getStock(3, 0)+ product.getStock(4, 0);
				product.setCanBeShipStock(stock);
            }
            easyuiDataGridJson.setTotal((long)list.size());
            easyuiDataGridJson.setRows(list);
            return easyuiDataGridJson;
        }catch(Exception e){
			e.printStackTrace();
			request.setAttribute("msg", "程序异常!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}finally {
            psService.releaseAll();
        }
	}
}
