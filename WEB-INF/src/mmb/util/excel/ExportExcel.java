package mmb.util.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExportExcel extends AbstractExcel {
	private List<Integer> row;
    private List<Integer> col;
    
    private boolean isHeader = true;
    
    private CellStyle cellStyle=null;
    
    public ExportExcel() {
        super();
    }

    public ExportExcel(int workType) {
        super(workType);
    }
    public List<Integer> getRow() {
        return row;
    }

    public void setRow(List<Integer> row) {
        this.row = row;
    }

    public List<Integer> getCol() {
        return col;
    }

    public void setCol(List<Integer> col) {
        this.col = col;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }
    
    public void setWidthStandard() {
    	Sheet sheet = this.getSheet();
    	for( int i = 0 ; i < 15; i++ ) {
    		sheet.setColumnWidth(i, 11 * 256);
    	}
    }

    private CellStyle getCellStyle(Workbook workbook){
    	if(cellStyle==null){
    		cellStyle=workbook.createCellStyle();
    		cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
    		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
    		cellStyle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
    		cellStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框 
    	}
    	return cellStyle;
    }
    
	@Override
	public void setStyle(int rownum, int columnnum, List dataList, Object obj,
			Workbook workbook, Cell cell) {
		// TODO Auto-generated method stub
//		 CellStyle cellStyle = workbook.createCellStyle();
//       cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 下边框
//       cellStyle.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
//       cellStyle.setBorderTop(CellStyle.BORDER_THIN);// 上边框
//       cellStyle.setBorderRight(CellStyle.BORDER_THIN);// 右边框 
//       cell.setCellStyle(cellStyle);
       
       CellStyle cellStyle=getCellStyle(workbook);
       cell.setCellStyle(cellStyle);
	}
}
