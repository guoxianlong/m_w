package mmb.rec.stat.controller;

import java.util.List;

import mmb.util.excel.AbstractExcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

public class SearchProductStockExcel extends AbstractExcel {
	private List<Integer> row;
    private List<Integer> col;
    
    private boolean isHeader = true;
    
    public SearchProductStockExcel() {
        super();
    }

    public SearchProductStockExcel(int workType) {
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

	@Override
	public void setStyle(int rownum, int columnnum, List dataList, Object obj,
			Workbook workbook, Cell cell) {
		// TODO Auto-generated method stub
		//设置表头的样式
	}

}
