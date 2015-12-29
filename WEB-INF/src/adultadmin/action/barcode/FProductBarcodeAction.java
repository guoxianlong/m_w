/**
 * 
 */
package adultadmin.action.barcode;


import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 *  <code>AddPoolTwoAction.java</code>
 *  <p>功能:产品条形码Action (查询操作)
 *  
 *  <p>Copyright 商机无限 2010 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2010-11-24 下午04:25:46	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class FProductBarcodeAction  extends BaseAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		WareService service = new WareService();
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String code = StringUtil.convertNull(request.getParameter("productCode"));
		String barcode = StringUtil.convertNull(request.getParameter("procBarcode"));
		barcode = barcode.trim();
		String actionFlag = StringUtil.convertNull(request.getParameter("actionFlag"));
		//小店名称
		String pname = StringUtil.convertNull(request.getParameter("productName"));
		// 原名称
		String oriname = StringUtil.convertNull(request.getParameter("orinameProc"));
		String type = StringUtil.convertNull(request.getParameter("type"));
		// 条形吗id
		int id = StringUtil.StringToId(request.getParameter("id"));
		//商品id
		int pid = StringUtil.StringToId(request.getParameter("pid"));
		if(type!=null && !type.trim().equals("")){
			if(pname != null && !pname.trim().equals("")){
				pname = Encoder.decrypt(pname);
			}
			if(oriname != null && !oriname.trim().equals("")){
				oriname = Encoder.decrypt(oriname);
			}
		}
		try {
			if(actionFlag!=""){
				seracheProductBarcodes(request,response);
				return mapping.findForward("barcodesearch"); 
			}
			voProduct vo = null;
			List barcodeList = null;
			if(id==0){
				// 根据条件一一排查
				if(code.trim().length()>0){
					vo = service.getProduct2("code='"+code+"'");
					if(vo!=null){
						barcodeList = bcmService.getProductBarcodeList("product_id="+vo.getId(),0,-1,"id");
					}else if(barcode.trim().length()>0){
						barcodeList = bcmService.getProductBarcodeList("barcode='"+barcode+"'",0,-1,"id");
						if(barcodeList!=null && barcodeList.size()>0){
							barcodeList = bcmService.getProductBarcodeList("product_id="+((ProductBarcodeVO)barcodeList.get(0)).getProductId(),0,-1,"id");
							vo = service.getProduct(((ProductBarcodeVO)barcodeList.get(0)).getProductId());
						}
					}else if(pname.trim().length()>0){
						vo = service.getProduct2("a.name='"+pname+"'");
						if(vo!=null)
							barcodeList = bcmService.getProductBarcodeList("product_id="+vo.getId(),0,-1,"id");
					}
				}else if(barcode.trim().length()>0){
					barcodeList = bcmService.getProductBarcodeList("barcode='"+barcode+"'",0,-1,"id");
					if(barcodeList!=null && barcodeList.size()>0){
						barcodeList = bcmService.getProductBarcodeList("product_id="+((ProductBarcodeVO)barcodeList.get(0)).getProductId(),0,-1,"id");
						vo = service.getProduct(((ProductBarcodeVO)barcodeList.get(0)).getProductId());
					}else if(pname.trim().length()>0){
						vo = service.getProduct2("a.name='"+pname+"'");
						if(vo!=null)
							barcodeList = bcmService.getProductBarcodeList("product_id="+vo.getId(),0,-1,"id");
					}
				}else if(pname.trim().length()>0){
					vo = service.getProduct2("a.name='"+pname+"'");
					if(vo!=null)
						barcodeList = bcmService.getProductBarcodeList("product_id="+vo.getId(),0,-1,"id");
					else if(oriname.trim().length()>0){
						vo = service.getProduct2("a.oriname='"+oriname+"'");
						if(vo!=null)
							barcodeList = bcmService.getProductBarcodeList("product_id="+vo.getId(),0,-1,"id");
					}
				}else if(oriname.trim().length()>0){
					vo = service.getProduct2("a.oriname='"+oriname+"'");
					if(vo!=null)
						barcodeList = bcmService.getProductBarcodeList("product_id="+vo.getId(),0,-1,"id");
				}
				request.setAttribute("product", vo);
				request.setAttribute("barcodeList", barcodeList);
				return mapping.findForward(IConstants.SUCCESS_KEY);
			}else{
				vo = service.getProduct(pid);
				if(vo==null){
					request.setAttribute("tip", "没有找到相应产品，不能修改条码。");
					return mapping.findForward("failure"); 
				}
				ProductBarcodeVO barcodeVO = bcmService.getProductBarcode("product_id="+vo.getId()+" and barcode_status=0");
				request.setAttribute("product", vo);
				request.setAttribute("barcodeVO", barcodeVO);
			}
			return mapping.findForward("mbarcode");
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 功能:批量查询产品条码信息
	 * <p>作者文齐辉 2011-1-14 下午07:02:43
	 * @param barcodes
	 */
	public void seracheProductBarcodes(	HttpServletRequest request, HttpServletResponse response){
		String barcodes = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("barcodes")));
		if(barcodes.length()==0)
			return;
		String[] barcode = barcodes.split("\r");
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
        	for(int i=0;i<barcode.length;i+=1){
        		if(barcode[i].trim().length()>0){
        			buf.append("pb.barcode='");
        			buf.append(barcode[i].trim().replaceAll("\n", ""));
        			buf.append("' || ");
        		}
        	}
        	if(buf.length()>0){
        		buf.replace(buf.length()-3, buf.length(), "");
        	}else{
        		request.setAttribute("tip", "查询条件错误");
        		request.setAttribute("result", "failure");
        		return ;
        	}
        	buf.append(") and barcode_status=0");

        	List list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc");
            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	voProduct product = (voProduct)iter.next();
            	List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
            	product.setPsList(psList);
            }
            request.setAttribute("productList", list);
        }finally {
            psService.releaseAll();
        }
	}
}
