package mmb.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mmb.tms.model.Provinces;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import adultadmin.util.StringUtil;

/**
 * 操作excel的工具类（07,03版都可用）
 * 
 * @author lining
 * 
 */
public class ExcelUtil {

	/**
	 * 处理excel表中的各种类型
	 * 
	 * @param xssfCell
	 * @return
	 */
	public static String getValue(Cell cell) {
		String result = new String();
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:// 布尔类型
			result = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:// 数字类型
			if (DateUtil.isCellDateFormatted(cell)) {// 日期类型
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				Date date = cell.getDateCellValue();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0
						&& cal.get(Calendar.SECOND) == 0)
					result = sdf.format(date);
				else
					result = sdf1.format(date);
			} else {
				double value = cell.getNumericCellValue();
				CellStyle style = cell.getCellStyle();
				DecimalFormat format = new DecimalFormat("0.00");
				String temp = style.getDataFormatString();
				String a = "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)";
				String b = "_ * #,##0.00_ ;_ * \\-#,##0.00_ ;_ * \"-\"??_ ;_ @_ ";
				// 单元格设置成常规
				if (temp.equals("General") ) {
					if(value<Provinces.PROVINCE_FLAG){
						format = new DecimalFormat();
					}
				} else if (temp.equals(a)|| temp.equals(b)) {// 单元格设置成会计专用
					format.applyPattern(",###.00");
				}
				result = format.format(value);
				if (result.endsWith(".00")) {
					result = StringUtils.substringBefore(result, ".");
				}else{
					result = result.replace(",", "");
				}
			}
			break;
		case Cell.CELL_TYPE_FORMULA:// 函数
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
					.getCreationHelper().createFormulaEvaluator();
			evaluator.evaluateFormulaCell(cell);
			CellValue cellValue = evaluator.evaluate(cell);
			DecimalFormat format = new DecimalFormat(",###.00");
			double value = cellValue.getNumberValue();
			if (value == 0.0) {
				result = "";
			} else {
				result = format.format(value);
			}
			break;
		case Cell.CELL_TYPE_STRING:// String类型
			result = cell.getRichStringCellValue().toString();
			break;
		case Cell.CELL_TYPE_BLANK:
			result = "";
		default:
			result = "";
			break;
		}
		return result;
	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static String getMergedRegionValue(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getValue(fCell);
				}
			}
		}

		return null;
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static boolean isMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();

		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 判断单元格在不在合并单元格范围内，如果是，获取其合并的列数。
	 * 
	 * @param sheet
	 *            工作表
	 * @param cellRow
	 *            被判断的单元格的行号
	 * @param cellCol
	 *            被判断的单元格的列号
	 * @return
	 * @throws IOException
	 */
	public static int getMergerCellRegionCol(Sheet sheet, int cellRow,
			int cellCol) throws IOException {
		int retVal = 0;
		int sheetMergerCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergerCount; i++) {
			CellRangeAddress cra = (CellRangeAddress) sheet.getMergedRegion(i);
			int firstRow = cra.getFirstRow(); // 合并单元格CELL起始行
			int firstCol = cra.getFirstColumn(); // 合并单元格CELL起始列
			int lastRow = cra.getLastRow(); // 合并单元格CELL结束行
			int lastCol = cra.getLastColumn(); // 合并单元格CELL结束列
			if (cellRow >= firstRow && cellRow <= lastRow) { // 判断该单元格是否是在合并单元格中
				if (cellCol >= firstCol && cellCol <= lastCol) {
					retVal = lastCol - firstCol + 1; // 得到合并的列数
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * 判断单元格是否是合并的单格，如果是，获取其合并的行数。
	 * 
	 * @param sheet
	 *            表单
	 * @param cellRow
	 *            被判断的单元格的行号
	 * @param cellCol
	 *            被判断的单元格的列号
	 * @return
	 * @throws IOException
	 */
	public static int getMergerCellRegionRow(Sheet sheet, int cellRow,
			int cellCol) throws IOException {
		int retVal = 0;
		int sheetMergerCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergerCount; i++) {
			CellRangeAddress cra = (CellRangeAddress) sheet.getMergedRegion(i);
			int firstRow = cra.getFirstRow(); // 合并单元格CELL起始行
			int firstCol = cra.getFirstColumn(); // 合并单元格CELL起始列
			int lastRow = cra.getLastRow(); // 合并单元格CELL结束行
			int lastCol = cra.getLastColumn(); // 合并单元格CELL结束列
			if (cellRow >= firstRow && cellRow <= lastRow) { // 判断该单元格是否是在合并单元格中
				if (cellCol >= firstCol && cellCol <= lastCol) {
					retVal = lastRow - firstRow + 1; // 得到合并的行数
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * 单元格小平对齐
	 * 
	 * @param alignment
	 * @return
	 */
	public static String convertAlignToHtml(short alignment) {
		String align = "left";
		switch (alignment) {
		case CellStyle.ALIGN_LEFT:
			align = "left";
			break;
		case CellStyle.ALIGN_CENTER:
			align = "center";
			break;
		case CellStyle.ALIGN_RIGHT:
			align = "right";
			break;
		default:
			break;
		}
		return align;
	}

	/**
	 * 单元格垂直对齐
	 * 
	 * @param verticalAlignment
	 * @return
	 */
	public static String convertVerticalAlignToHtml(short verticalAlignment) {
		String valign = "middle";
		switch (verticalAlignment) {
		case CellStyle.VERTICAL_BOTTOM:
			valign = "bottom";
			break;
		case CellStyle.VERTICAL_CENTER:
			valign = "center";
			break;
		case CellStyle.VERTICAL_TOP:
			valign = "top";
			break;
		default:
			break;
		}
		return valign;
	}

	/**
	 * 导出xls
	 */

	/**
	 * 功能：将HSSFWorkbook写入Excel文件
	 * 
	 * @param wb
	 *            HSSFWorkbook
	 * @param absPath
	 *            写入文件的相对路径
	 * @param wbName
	 *            文件名
	 */
	public static String writeWorkbook(HSSFWorkbook wb, OutputStream out) {
		String rebak = "";
		try {
			wb.write(out);
			rebak = "导出成功";

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rebak = "导出失败";
		} catch (IOException e) {
			rebak = "导出失败";
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rebak;
	}

	/**
	 * 功能：创建HSSFSheet工作簿
	 * 
	 * @param wb
	 *            HSSFWorkbook
	 * @param sheetName
	 *            String
	 * @return HSSFSheet
	 */
	public static HSSFSheet createSheet(HSSFWorkbook wb, String sheetName) {
		HSSFSheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(12);
		// sheet.setGridsPrinted(false);
		// sheet.setDisplayGridlines(false);
		return sheet;
	}

	/**
	 * 功能：创建HSSFRow
	 * 
	 * @param sheet
	 *            HSSFSheet
	 * @param rowNum
	 *            int
	 * @param height
	 *            int
	 * @return HSSFRow
	 */
	public static HSSFRow createRow(HSSFSheet sheet, int rowNum) {
		HSSFRow row = sheet.createRow(rowNum);
		return row;
	}

	/**
	 * 
	 * 功能：创建带边框的CellStyle样式
	 * 
	 * @param wb
	 *            HSSFWorkbook
	 * 
	 * @param backgroundColor
	 *            背景色
	 * 
	 * @param foregroundColor
	 *            前置色
	 * 
	 * @param font
	 *            字体
	 * 
	 * @return CellStyle
	 */

	public static CellStyle createBorderCellStyle(HSSFWorkbook wb, Font font) {
		CellStyle cs = wb.createCellStyle();
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		// cs.setFillForegroundColor(HSSFColor.WHITE.index);
		// cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// cs.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		// cs.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		// cs.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// cs.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		cs.setFont(font);
		return cs;

	}

	public static CellStyle createBorderCellStyle(HSSFWorkbook wb) {
		CellStyle cs = wb.createCellStyle();
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		return cs;

	}


	/**
	 * 创建表头
	 * 
	 * @return
	 */
	public static void createTableHeader(String[] tableHeader, HSSFSheet sheet,
			CellStyle style,int[] clss) {
//		short cellNumber = (short) tableHeader.length;
		HSSFRow row = ExcelUtil.createRow(sheet, 0);
		for (int i = 0; i < clss.length; i++) {
			HSSFCell headerCell = row.createCell(i);
			headerCell.setCellValue(tableHeader[clss[i]]);
			headerCell.setCellStyle(style);
		}
	}
	
	/**
	 * 创建表头2
	 * 
	 * @param createrow
	 *            行号
	 * @return
	 */
	public static void createTableHeader(String[] tableHeader, HSSFSheet sheet,
			CellStyle style,int[] clss,int createrow) {
//		short cellNumber = (short) tableHeader.length;
		HSSFRow row = ExcelUtil.createRow(sheet, createrow);
		for (int i = 0; i < clss.length; i++) {
			HSSFCell headerCell = row.createCell(i);
			headerCell.setCellValue(tableHeader[clss[i]]);
			headerCell.setCellStyle(style);
		}
	}
	/**
	 * 创建行
	 * 
	 * @param cells
	 * @param rowIndex
	 */
	public static void createTableRow(ArrayList<String> ls, HSSFSheet sheet,
			int rowIndex, CellStyle style, int[] clss) {
		// 创建第rowIndex行
		HSSFRow row = sheet.createRow(rowIndex);
		for (int i = 0; i < clss.length; i++) {
			// 创建第i个单元格
			HSSFCell cell = row.createCell(i);
			if(!"null".equals(ls.get(clss[i]))){
				cell.setCellValue(ls.get(clss[i]));
			}else{
				cell.setCellValue("");
			}
		}
	}

	/**
	 * 创建行
	 * 
	 * @param cells
	 * @param rowIndex
	 */
	public static void createTableRow(ArrayList ls, HSSFSheet sheet, int rowIndex, CellStyle style, int[] clss,
			HSSFWorkbook wb) {
		// 创建第rowIndex行
		HSSFRow row = sheet.createRow(rowIndex);
		for (int i = 0; i < clss.length; i++) {
			// 创建第i个单元格
			HSSFCell cell = row.createCell(i);
			int index = clss[i];
				if (!"null".equals(ls.get(index))) {
					setCellValue(cell, ls.get(index), style, wb);
				} else {
					cell.setCellValue("");
				}
		}
	}
	
	/**
	 * 分析excel表
	 * 
	 * @param is
	 *            输入流
	 * @param suffix
	 *            excel表后缀
	 * @param startRow
	 *            从第几行开始读
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static List<String[]> rosolveFile(InputStream is,String suffix,int startRow)throws IOException, FileNotFoundException {
		int sheetIndex = 0;
		return rosolveFile(is, suffix, startRow, sheetIndex);
	}
	
	/**
	 * 分析excel表
	 * 
	 * @param is
	 *            输入流
	 * @param suffix
	 *            excel表后缀
	 * @param startRow
	 *            从第几行开始读
	 * @param sheetIndex
	 *            sheet index 从0开始
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static List<String[]> rosolveFile(InputStream is, String suffix, int startRow, int sheetIndex) throws IOException, FileNotFoundException {
		Workbook xssfWorkbook = null;
		if ("xls".equals(suffix)) {
			xssfWorkbook = new HSSFWorkbook(is);
		} else if ("xlsx".equals(suffix)) {
			xssfWorkbook = new XSSFWorkbook(is);
		}
		Sheet xssfSheet = xssfWorkbook.getSheetAt(sheetIndex);
		if (xssfSheet == null) {
			return null;
		}
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (int rowNum = startRow; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
			if (xssfSheet.getRow(rowNum) != null) {
				Row xssfRow = xssfSheet.getRow(rowNum);
				short firstCellNum = xssfRow.getFirstCellNum();
				short lastCellNum = xssfRow.getLastCellNum(); 
				String[] values = new String[lastCellNum];
				for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) { 
					Cell xssfCell = xssfRow.getCell(cellNum);
					if(xssfCell==null){
						values[cellNum] = "";
					}else{
						values[cellNum] = ExcelUtil.getValue(xssfCell);
					}
				}
				list.add(values);
			}
		}
		return list;
	}

	/**
	 * 
	 * 功能：创建字体
	 * @param wb
	 *            HSSFWorkbook
	 * @param boldweight
	 *            short
	 * @param color
	 *            short
	 * @return Font
	 */

	public static Font createFont(HSSFWorkbook wb, short boldweight, short color, short size) {

		Font font = wb.createFont();

		font.setBoldweight(boldweight);

		font.setColor(color);

		font.setFontHeightInPoints(size);

		return font;

	}

	/**
	 * 设置某行单元格样式
	 * 
	 * @param ls
	 * @param sheet
	 * @param rowIndex
	 * @param style
	 * @param clss
	 * @param cellNo
	 *            特殊处理单元格
	 * @param style2
	 *            特殊单元格样式
	 */
	public static void createTableRow(ArrayList<String> ls, HSSFSheet sheet, int rowIndex, CellStyle style, int[] clss,
			int cellNo, CellStyle style2) {
		// 创建第rowIndex行
		HSSFRow row = sheet.createRow(rowIndex);
		for (int i = 0; i < clss.length; i++) {
			// 创建第i个单元格
			HSSFCell cell = row.createCell(i);
			if (!"null".equals(ls.get(clss[i]))) {
				cell.setCellValue(ls.get(clss[i]));
			} else {
				cell.setCellValue("");
			}
			if (i == cellNo) {
				cell.setCellStyle(style2);
			}
		}
	}

	/**
	 * 
	 * 功能：创建CellStyle样式
	 * @param wb
	 *            HSSFWorkbook
	 * @param backgroundColor
	 *            背景色
	 * @param foregroundColor
	 *            前置色
	 * @param font
	 *            字体
	 * @return CellStyle
	 */

	public static CellStyle createCellStyle(HSSFWorkbook wb, short backgroundColor, short foregroundColor,
			short halign, Font font) {
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(halign);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cs.setFillBackgroundColor(backgroundColor);
		cs.setFillForegroundColor(foregroundColor);
		cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cs.setFont(font);
		return cs;
	}

	/**
	 * 
	 * 功能：创建带边框的CellStyle样式
	 * @param wb
	 *            HSSFWorkbook
	 * @param backgroundColor
	 *            背景色
	 * @param foregroundColor
	 *            前置色
	 * @param font
	 *            字体
	 * @return CellStyle
	 */

	public static CellStyle createBorderCellStyle(HSSFWorkbook wb, short backgroundColor, short foregroundColor,
			short halign, Font font) {
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(halign);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cs.setFillBackgroundColor(backgroundColor);
		cs.setFillForegroundColor(foregroundColor);
		cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cs.setFont(font);
		cs.setBorderLeft(CellStyle.BORDER_DASHED);
		cs.setBorderRight(CellStyle.BORDER_DASHED);
		cs.setBorderTop(CellStyle.BORDER_DASHED);
		cs.setBorderBottom(CellStyle.BORDER_DASHED);
		return cs;

	}

	/**
	 * 
	 * 功能：创建CELL
	 * @param row
	 *            HSSFRow
	 * @param cellNum
	 *            int
	 * @param style
	 *            HSSFStyle
	 * @return HSSFCell
	 */

	public static HSSFCell createCell(HSSFRow row, int cellNum, CellStyle style) {
		HSSFCell cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		return cell;
	}

	/**
	 * 
	 * 功能：合并单元格
	 * 
	 * @param sheet
	 *            HSSFSheet
	 * @param firstRow
	 *            int
	 * @param lastRow
	 *            int
	 * @param firstColumn
	 *            int
	 * @param lastColumn
	 *            int
	 * @return int 合并区域号码
	 */

	public static int mergeCell(HSSFSheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn) {
		return sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn));

	}

	public static void autoWith(Sheet sheet, int[] clss) {
		for (int i = 0; i < clss.length; i++) {
			sheet.autoSizeColumn((short) i, false);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
		}
	}

	/**
	 * 设置单元格的值
	 * 
	 * @param cell
	 *            单元格对象
	 * @param value
	 *            值
	 * @param pattern
	 *            单元格格式
	 */
	private static void setCellValue(HSSFCell cell, Object value, CellStyle style, HSSFWorkbook wb) {
		if (value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Integer
				|| value instanceof Short || value instanceof BigDecimal || value instanceof Byte) {
			cell.setCellValue(StringUtil.toDouble(value.toString()));
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);// 这应该在setValue之后
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
			cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
		} else {
			if (value != null && value.toString().startsWith("=")) {
				cell.setCellFormula(value.toString().substring(1));
				cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			} else {
				if (value instanceof Date) {// 日期
					cell.setCellValue((Date) value);
					dataFormat("yyyy-m-d", cell, style, wb);

				} else {
					cell.setCellValue(new HSSFRichTextString(value == null ? "" : value.toString()));
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				}
			}
		}
	}

	/**
	 * 设置数据格式
	 * 
	 * @param format
	 *            格式字符串
	 * @return
	 */

	public static void dataFormat(String format, HSSFCell cell, CellStyle style, HSSFWorkbook wb) {
		HSSFDataFormat hdf = wb.createDataFormat();
		style.setDataFormat(hdf.getFormat(format));
		cell.setCellStyle(style);
	}

}
