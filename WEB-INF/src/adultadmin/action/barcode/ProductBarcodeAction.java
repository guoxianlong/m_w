/**
 * 
 */
package adultadmin.action.barcode;


import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.barcode.BarcodeLogBean;
import adultadmin.bean.barcode.ConsigPrintlogBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;

/**
 *  <code>AddPoolTwoAction.java</code>
 *  <p>功能:产品条形码Action 
 *  
 *  <p>Copyright 商机无限 2010 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2010-11-24 下午04:25:46	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class ProductBarcodeAction  extends BaseAction{
	
	private static Long lock = new Long(1);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	    voUser admin = (voUser) request.getSession().getAttribute(
                IConstants.USER_VIEW_KEY);
	    if(admin==null){
	    	request.setAttribute("tip", "对不起，你还没有登录，请先登录。");
	    	return mapping.findForward("failure");
	    }
	    UserGroupBean group = admin.getGroup();
	 	if(!group.isFlag(292)){
	 		request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
            request.setAttribute("result", "failure");
	 	}
	 	WareService service = new WareService();
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		// 条形码id
		int id = StringUtil.StringToId(request.getParameter("id"));
		// 产品id
		int pid = StringUtil.StringToId(request.getParameter("pid"));
		String barcode = StringUtil.convertNull(request.getParameter("barcode"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String procBarcode = StringUtil.convertNull(request.getParameter("procBarcode"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		String orinameProc = StringUtil.convertNull(request.getParameter("orinameProc"));
		//原条形码
		String oldBarcode = request.getParameter("oldBarcode");
//		String sysValue = StringUtil.convertNull(request.getParameter("sysValue"));
		int barcodeSource = StringUtil.StringToId(request.getParameter("barcodeSource"));
		ProductBarcodeVO barcodeVO = new ProductBarcodeVO();
		voProduct vo = null;
		barcodeVO.setId(id);
		barcodeVO.setBarcode(barcode.trim());
		// 如果条码为空，条码来源为0
		if(barcode.trim().length()==0 && barcodeSource!=0){
			barcodeVO.setBarcodeSource(0);
		}else
			barcodeVO.setBarcodeSource(barcodeSource);
		try {
			vo = service.getProduct(pid);
			if(vo==null){
				request.setAttribute("tip", "对不起， 查找产品失败，请重试。");
        		return mapping.findForward("failure");
			}
        	//如果是修改系统生成的4位原来的值加1
			if(barcodeSource==2){
				  //查询系统生成值
				 int num = 0;
				  ProductBarcodeVO tempVO = bcmService.getProductBarcode("barcode_source=2 and barcode like '"+barcode+"____' order by id desc limit 1");
				  if(tempVO==null){
					  barcodeVO.setSysValue("0001");
		          	  barcodeVO.setBarcode(barcode+"0001");
		          }else{
		        	  num = Integer.parseInt(tempVO.getSysValue());
		        	  // 格式行0001系统值
		        	  if(tempVO.getSysValue()!=null && tempVO.getSysValue().length()<=4){
		        		  DecimalFormat df = new DecimalFormat("0000");  
		          		  barcodeVO.setSysValue(df.format(++num));
		        	  }else
		        		  barcodeVO.setSysValue((++num)+"");
		          	barcodeVO.setBarcode(barcode+barcodeVO.getSysValue());
		          }
				  // 判断条码是否占用，主要是因为手动输入的时候
				  while(bcmService.getProductBarcode("barcode='"+barcodeVO.getBarcode()+"'")!=null){
      				  DecimalFormat df = new DecimalFormat("0000");  
      				  barcodeVO.setSysValue(df.format(++num));
      				  barcodeVO.setBarcode(barcode+barcodeVO.getSysValue());
				  }
			  }else{
				  barcodeVO.setSysValue(""); 
			  } 
		    // 检测手动输入条形码是否已存在
        	if(bcmService.getProductBarcode("barcode='"+barcodeVO.getBarcode()+"'")!=null){
        	 	request.setAttribute("tip", "对不起，此条形码已经存在，请重新输入。");
        		return mapping.findForward("failure");
        	}
        	barcodeVO.setProductId(pid);
        	synchronized (lock) {
        	   	int maxId = bcmService.getNumber("id", "product_barcode", "max", "product_id="+pid);
            	if(id==0 && maxId>0){
            		ProductBarcodeVO tmpPB = bcmService.getProductBarcode("id="+maxId);
            		//如果已经存在有效条码，修改失败
            		if(tmpPB.getBarcodeStatus()==0){
            			request.setAttribute("tip", "对不起， 当前数据不是最新的，请返回重新操作。");
                		return mapping.findForward("failure");
            		}
            	}else{
            		if(maxId!=id){
            			request.setAttribute("tip", "对不起， 当前数据不是最新的，请返回重新操作。");
                		return mapping.findForward("failure");
            		}	
            	}
        		// 开始事务
				bcmService.getDbOp().startTransaction();
				//已经有条码，添加
				if(id>0){
					  //把原来条形码改为无效
					  String set = "barcode_status=1";
					  bcmService.updateProductBarcode(set,"id="+barcodeVO.getId());
					  //如果条码不为空，则添加新条码
					  if(barcodeVO.getBarcode().length()>0){
						  barcodeVO.setId(0);
						  bcmService.addProductBarcode(barcodeVO);
					  }
					  //barcodeVO = bcmService.getProductBarcode("id= "+barcodeVO.getId());
				}else{ // 没有条码，第一次添加
					bcmService.addProductBarcode(barcodeVO);
					//barcodeVO = bcmService.getProductBarcode("id= "+service.getLastInsertId("product_barcode"));
				}
				if(vo!=null){
					// 添加修改条码日志
					int num = bcmService.getNumber("id", "barcode_log", "max", "id>0")+1;
					BarcodeLogBean barcodeLogBean = new BarcodeLogBean();
					barcodeLogBean.setId(num);
					barcodeLogBean.setAdminName(admin.getUsername());
					barcodeLogBean.setProductId(vo.getId());
					barcodeLogBean.setOriname(vo.getOriname());
					barcodeLogBean.setPname(vo.getName());
					barcodeLogBean.setOldBarcode(oldBarcode);
					barcodeLogBean.setBarcode(barcodeVO.getBarcode());
					barcodeLogBean.setOperDatetime(DateUtil.getNow());
					bcmService.addProductBarcodeLog(barcodeLogBean);
				}
				// 提交 事务
				bcmService.getDbOp().commitTransaction();     
        	}
		//	List barcodeList = bcmService.getProductBarcodeList("product_id="+pid,0,-1,"id");
		//	request.setAttribute("product", vo);
		//	request.setAttribute("barcodeList", barcodeList);   
		}catch(Exception e){
			e.printStackTrace(); 
			// 回滚事务
			bcmService.getDbOp().rollbackTransaction();    
		} finally {
			bcmService.releaseAll();
		} 
		//return new ActionForward("fproductBarcode.do?productCode="+vo.getCode(), true); 
		return new ActionForward("fproductBarcode.do?productCode="+productCode+"&procBarcode="+procBarcode+"&productName="+Encoder.encrypt(productName)+"&orinameProc="+Encoder.encrypt(orinameProc)+"&type=sure", true); 
	}
	
	  /**
	 * 功能:添加流水线上打印信息
	 * <p>作者文齐辉 2011-4-13 下午05:12:23
	 * @param request
	 * @param response
	 */
	public void addConsigPringLog(HttpServletRequest request,HttpServletResponse response){
		  voUser user = (voUser)request.getSession().getAttribute("userView");
		  if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			return ;
		  }
		  String printCode = StringUtil.convertNull(request.getParameter("printCode"));
		  int printType = StringUtil.StringToId(request.getParameter("printType"));
		  if(printCode.length()==0){
			  request.setAttribute("tip", "打印编号错误，操作失败！");
			  return ;
		  }
		  IBatchBarcodeService batchService = ServiceFactory.createBatchBarcodeServcie();
		  try{
			  ConsigPrintlogBean consigBean = new ConsigPrintlogBean();
			  consigBean.setPrintCode(printCode);
			  consigBean.setPrintType(printType);
			  consigBean.setUserId(user.getId());
			  consigBean.setPrintUsername(user.getUsername());
			  consigBean.setPrintDate(DateUtil.getNow());
			  batchService.addConsigPrintlog(consigBean);
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  batchService.releaseAll();
		  }
	}
		  
}
