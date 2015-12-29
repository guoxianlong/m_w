package mmb.util.excel;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

 

public class ExcelReportUtil {

	/**
	 * 
	 * 功能:导出excle 
	 * <p>作者 李双 Nov 2, 2012 5:20:25 PM
	 * @param list 导出的list
	 * @param bean setFielNamesEN setFielNames 不能为空
	 * @return
	 */
	public static String buildReport(List list,ExcelReportBean bean,String path){
		Workbook  wb = new HSSFWorkbook();
		if(bean.getType()==2){//导出2007
//			wb=new XSSFWorkbook();
			wb =new SXSSFWorkbook();
		}
		Sheet sheet = wb.createSheet("sheet1");
		Row row = sheet.createRow(0);
		CellStyle cellStyle = getStyle(wb,"style");
		cellStyle.setFont(getFont(wb, 12, 5));
		for(int i=0;i<bean.getFielNames().length;i++){//创建表头
			Cell cell = row.createCell(i);
			cell.setCellValue(bean.getFielNames()[i]);
			cell.setCellStyle(cellStyle);
		}
		List<String[]> strList = new ArrayList<String[]>();
		
		for(int i=0;i<list.size();i++){
			Object object = list.get(i);
 			Method method =null;
 			String[] str = new String[bean.getFielNamesEN().length];
 			int index_str=0;
			for(int x=0;x<bean.getFielNamesEN().length;x++){
				try {
					method = object.getClass().getDeclaredMethod(bean.getFielNamesEN()[x], null);
					method.setAccessible(true);
					str[index_str] = String.valueOf(method.invoke(object, null));
					index_str++;
				}catch (Exception e) {
					e.printStackTrace();
				} 
			}
			strList.add(str);
		}
		list = null;
		for(int i=0;i<strList.size();i++){
			row=sheet.createRow(i+1);
			String[] str = strList.get(i);
			for(int x=0;x<str.length;x++){
				setCell(row,str[x], x);
			}
		}
		strList = null;
		String fileName = path+"/";
		BufferedOutputStream bout = null;
		FileOutputStream fout=null;
		
		try {
			long name = System.currentTimeMillis();
			if (bean.getType() == 1) {
				fileName += name + ".xls";
			} else {
				fileName += name + ".xlsx";
			}
			fout = new FileOutputStream(fileName);
			bout = new BufferedOutputStream(fout);
			wb.write(bout);
			fout.close();
			bout.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clearMap();
		return fileName;
	}
	
//	static void setCell(Row row,Object o,String typeName ,int cellIndex){
//		if(o==null){
//			row.createCell(cellIndex).setCellValue("");
//		}
//		String objstr = String.valueOf(o);
//		if("int".equals(typeName)){
//			row.createCell(cellIndex).setCellValue(Integer.parseInt(objstr));
//		}else if("float".equals(typeName)){
//			row.createCell(cellIndex).setCellValue(Float.parseFloat(objstr));
//		}else if("double".equals(typeName)){
//			row.createCell(cellIndex).setCellValue(Double.parseDouble(objstr));
//		}else if("long".equals(typeName)){
//			row.createCell(cellIndex).setCellValue(Long.parseLong(objstr));
//		}else{
//			row.createCell(cellIndex).setCellValue(objstr);
//		}
//	}
	
	private static void setCell(Row row,String value,int cellIndex){
		if(value==null ){
			row.createCell(cellIndex).setCellValue("");
		}else{
			if(isInt(value)){
				row.createCell(cellIndex).setCellValue(Double.parseDouble(value));
			}else{
				row.createCell(cellIndex).setCellValue(value);
			}
		}
	}
	
	static Pattern p = Pattern.compile("\\d+(|\\.\\d+)");
	
	static boolean isInt(String value){
		
		Matcher m = p.matcher(value);
		return  m.matches();
	}
	
	public static void main(String args[]){
		System.out.println(isInt("12.2"));
	}
	
	private static Map<String,Font> fontMap = new HashMap<String,Font>();
    private static Font getFont(Workbook wb, int fontHeight, int boldWeight) {
        Font font = (Font)fontMap.get(fontHeight + boldWeight+"");
        if (font == null) {
            font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short)fontHeight);
            font.setBoldweight((short)boldWeight);
            fontMap.put(fontHeight + boldWeight+"", font);
        }
        return font;
    }
    
    private static Map<String,CellStyle> styletMap = new HashMap<String,CellStyle>();
    
    private static CellStyle getStyle(Workbook wb,String key){
    	CellStyle style = (CellStyle)styletMap.get(key);
    	if(style==null){
    		style=wb.createCellStyle();
			// 边框
			style.setBorderBottom((short) 1);
			style.setBorderLeft((short) 1);
			style.setBorderRight((short) 1);
			style.setBorderTop((short) 1);
			// 对齐
			style.setAlignment(CellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	//		style.setFont();
			style.setWrapText(true);
			styletMap.put("style", style);
    	}
		return style; 
    }
    
    private static void clearMap(){
    	styletMap.clear();
    	fontMap.clear();
    }

}
