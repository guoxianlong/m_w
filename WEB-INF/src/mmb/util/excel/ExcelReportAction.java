package mmb.util.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import adultadmin.util.Constants;

public class ExcelReportAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		ExcelReportBean bean = (ExcelReportBean)request.getAttribute("bean");
		List list =(List)request.getAttribute("list");
		if(bean==null||list==null || bean.getExcleName()==null || bean.getFielNamesEN()==null|| bean.getFielNames()==null){
			request.setAttribute("tip", "未设置好导出条件");
			return mapping.findForward(Constants.ACTION_FAILURE_KEY);
		}
		String path = ExcelReportUtil.buildReport(list, bean,servlet.getServletContext().getRealPath("/download/excle"));
		String extension = path.substring(path.indexOf("."),path.length());
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		OutputStream os = null;
//		String filePath = servlet.getServletContext().getRealPath("/" + path);
		File downloadFile = new File(path);
		try {
			bis = new BufferedInputStream(new FileInputStream(downloadFile));
			os = response.getOutputStream();
			bos = new BufferedOutputStream(os);
			response.setHeader("Content-disposition", "attachment;filename=\""
					+ (new String(bean.getExcleName().getBytes("GB2312"),"ISO8859-1")+extension + "\""));
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = bis.read(buffer, 0, 8192)) != -1) {
				bos.write(buffer, 0, bytesRead);
			}
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bis!=null) bis.close();
			if(os!=null) os.close();
			if(bos!=null) bos.close();
			downloadFile.delete();
		}
		return null;
	}
	
	//测试
//	public static void main(String args[]){
//		List<Book> list =new ArrayList<Book>();
//		for(int i=0;i<1000;i++){
//			list.add(new Book(i,"ffff"+i,i+"tttt"));
//		}
//		
//		ExcelReportBean eBean = new ExcelReportBean();
//		eBean.setClassType(Book.class);
//    	eBean.setFielNames("序号,名字,ff名称");
//    	eBean.setFielNamesEN("getId,getName,getTest");
//    	eBean.setType(2);
//    	eBean.setExcleName("12");
//    	ExcelReportUtil.buildReport(list, eBean,"E:");
//	}
}

//class Book{
//	public int id ;
//	public String name;
//	public String test;
//	
//	public Book(int id,String name,String test){
//		this.id=id;
//		this.name=name;
//		this.test=test;
//	}
//	
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getTest() {
//		return test;
//	}
//	public void setTest(String test) {
//		this.test = test;
//	}
//}
