package mmb.util.excel;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import adultadmin.util.StringUtil;


import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public abstract class AbstractExcel {
    private static Logger logger = Logger.getLogger(AbstractExcel.class);

    private Workbook workbook;
    private Sheet sheet;
    public final static int HSSF = 1;//03
    public final static int XSSF = 2;//07
    public final static int SXSSF = 3;//biggrid
    private int type =0;//文档类型
    
    private final static int ROW = 1;
    private final static int COLUMN = 0;
    
    //private boolean exclusion = true;
    
    private int rowMergeCount = -1;//控制行合并
    private Map<Integer,Integer> columnMergeCount = new HashMap<Integer,Integer>();//控制列合并
    
    
    private boolean startRowMerge = true;//启用行合并
    private boolean startColumnMerge = true;//启用列合并
    
    
    private List<Integer> mayMergeColumn = new ArrayList<Integer>();//允许合并列
    private List<Integer> mayMergeRow = new ArrayList<Integer>();//允许合并行

    public AbstractExcel(){
    	this.workbook = new SXSSFWorkbook();
        this.sheet = workbook.createSheet();
    }
    
    public AbstractExcel(int workType){
        if(workType==HSSF){
            this.workbook = new HSSFWorkbook();
        }
        if(workType==XSSF){
            this.workbook = new XSSFWorkbook();
        }
        if(workType==SXSSF){
            this.workbook = new SXSSFWorkbook();
        }
        this.sheet = workbook.createSheet();
        type = workType;
    }
    
    /**
     * 合并单元格
     */
    private void mergeCell(Sheet sheet,int firstRow,int  lastRow,int  firstCol,int  lastCol,int rorc){
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));//添加合并
        int index = sheet.getNumMergedRegions();//合并列表
        if(COLUMN==rorc){
            columnMergeCount.put(firstCol+1, index-1);  // 缓存列合并下标      
        }else{
            rowMergeCount = index-1;//缓存行合并下标
        }
    }
    public void setCellStyle(){
        
    }
    /**
     * 删除合并（依据合并下标）
     */
    private void removeMerge(Sheet sheet,int mergeIndex){
        sheet.removeMergedRegion(mergeIndex);
        if(rowMergeCount > mergeIndex){
            rowMergeCount = rowMergeCount - 1;//重排缓存下标
        }
        reSort(columnMergeCount,mergeIndex);
    }
    /**
     * 重排缓存下标
     */
    private void reSort(Map<Integer,Integer> map,int index){
        for(int i : map.keySet()){
            if(map.get(i) > index){
                map.put(i, map.get(i)-1);
            }
        }
    }
    
    /**
     * 创建行
     * @param sheet 卡片
     * @param rownum 行号
     * @return
     */
    private Row createRow(Sheet sheet,int rownum){
        AssertUtils.notNull(sheet, "row can't be null!");
        AssertUtils.isTrue(rownum >= 0, "rownum must gt 0");
        return sheet.createRow(rownum);
    }
    
    /**
     * 创建单元格
     * @param row 行号
     * @param columnIndex 单元格列索引
     * @param type 类型
     * @return
     */
    private Cell createCell(Row row,int columnIndex,int type){
        AssertUtils.notNull(row,"row can't be null!");
        AssertUtils.isTrue( columnIndex >= 0,"columnIndex must gt 0");
        AssertUtils.isTrue((0 < type && type < 5),"type must gt 0 and lt 5");
        Cell cell = row.createCell(columnIndex, type);
        return cell;
    }
    
    //--------------对象元素构建表格---------------------
    public Sheet bulildObjectExcel(List<Object> bodies,List<Object> headers){
        int len = headers.get(0).getClass().getFields().length;
        setColMergeCount(len);
        buildObjectHeader(headers);
        buildObjectBody(bodies);
        return sheet;
    }
    
    public void setColMergeCount(int maxColNum){
        for(int i = 1; i<= maxColNum ; i++){
            columnMergeCount.put(i, -1);
        }
    }
    
    public Sheet buildObjectHeader(List<Object> headers){
        return buildObject(sheet,headers);
    }
    public Sheet buildObjectBody(List<Object> bodies){
        startRowMerge = false;
        return buildObject(sheet,bodies);
    }
    protected Sheet buildObject(Sheet sheet,List<Object> list){
        int lastRowNum = sheet.getLastRowNum();
        Row tmp = sheet.getRow(0);
        if(null != tmp){
            lastRowNum = lastRowNum + 1;
        }
        for(int i = 0;i<list.size();i++){
            Row row = this.createRow(sheet, i+lastRowNum);
            this.buildComplexRow(sheet,row, list.get(i),i,list);  
        }
        return sheet;
    }
    //------------------------------------
    
    
    //---------------构建简单workbook 表头区行列合并；数据区设定请用合并方式---------------------
    public Sheet bulildSimpleExcel(List<ArrayList<String>> bodies,List<ArrayList<String>> headers){
        setColMergeCount(headers.get(0).size());
        buildListHeader(headers);
        buildListBody(bodies);
        return sheet;
    }
    
    public Sheet buildListHeader(List<ArrayList<String>> headers){
        return buildList(sheet,headers);
    }
    public Sheet buildListBody(List<ArrayList<String>> bodies){
        startRowMerge = false;
       return buildList(sheet,bodies);
    }
    protected Sheet buildList(Sheet sheet,List<ArrayList<String>> list){
        int lastRowNum = sheet.getLastRowNum();
        Row tmp = sheet.getRow(0);
        if(null != tmp){
            lastRowNum = lastRowNum + 1;
        }
        
        for(int i = 0;i<list.size();i++){
            Row row = this.createRow(sheet, i+lastRowNum);
            this.buildComplexRow(sheet,row, list,list.get(i),i);  
        }
        return sheet;
    }
    //------------------------------------
    
    
    
    public abstract void setStyle(int rownum,int columnnum,List dataList,Object obj,Workbook workbook,Cell cell);//设置行样式
    
    /**
     * 
     *功能: 获取当前元素所在列中最大宽度，汉字占两个字母宽度
     *@date 2013-9-26
     * @param rownum 当前cell行
     * @param colNum 当前cell列
     * @param value 当前cell值
     * @return
     */
    private int getMaxLength(int rownum,int colNum,String value){
        int currentColLen = length(StringUtil.convertNull(value));
        if(rownum==0){
            return currentColLen;
        }else{
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(rownum-1);
            Cell cell = row.getCell((short)colNum);
            //String cellVal = cell.getStringCellValue();
            int maxColLen = sheet.getColumnWidth(colNum)/256;
            return currentColLen>maxColLen?currentColLen:maxColLen;
        }
    }
    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value  指定的字符串
     *           
     * @return 字符串的长度
     */
    public int length(String value) {
        int valueLength = 2;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;

    }

    
    private Cell buildCell(Row row,int rownum,int columnnum,String value,List dataList,Object obj){
        Cell cell = this.createCell(row, columnnum, Cell.CELL_TYPE_STRING);//创建单元格
        cell.setCellValue(null==value?"":value);//单元格赋值
        
        
        setStyle(rownum,columnnum,dataList,obj,workbook,cell);
        
        int maxLenth = getMaxLength(row.getRowNum(),columnnum,value);
        sheet.setColumnWidth(columnnum, (maxLenth>255 ? 255 : maxLenth) *256);
        return cell;
    }
    
    /**
     * 合并构建（指定合并）
     * 合并顺序：先:行合并;再列合并。
     * 列合并前校验：解决（四个方块区域合并冲突）
     */
    private void buildMerge(Sheet sheet){
        int rownum = sheet.getLastRowNum();
        int columnnum =sheet.getRow(rownum).getLastCellNum()-1;
        String value = sheet.getRow(rownum).getCell(columnnum).getStringCellValue();
        if(mayMergeRow.contains(rownum) && (columnnum > 0)){//行 合并。startRowMerge：启用行合并；columnnum：列号 要大于0 从第二列开始行合并
            int rowBegin = getRowFirstMergeIndex(value,sheet.getRow(rownum),columnnum);//开始合并的 列索引下标。
            if(rowBegin >= 0){//参与合并的下标位置判断。0：边界位置；-1 不允许合并
                if(checkRowMergeRegion(rowMergeCount)){//状态校验。checkRowMergeRegion： 判断上一个单元格是否是合并单元格
                    if(rowBegin!=0){
                        this.removeMerge(sheet,rowMergeCount);//删除合并                        
                    }else{
                        if(columnnum!=1){
                            this.removeMerge(sheet,rowMergeCount);//删除合并
                        }
                    }
                    
                }
                this.mergeCell(sheet, rownum, rownum, rowBegin, columnnum,ROW);//合并单元格
            }else{
                rowMergeCount = -1;
            }
            
        }
        // 逻辑同上
        if((rownum > 0) && mayMergeColumn.contains(columnnum) && mayMergeRow.contains(rownum)){//TODO 待处理四格子冲突
            
            int columnBegin = getColumnFirstMergeIndex(value,sheet,rownum,columnnum);
            if(columnBegin >= 0){
                if(checkColumnMergeRegion(columnnum+1) ){//
                    this.removeMerge(sheet, columnMergeCount.get(columnnum+1));
                }
                this.mergeCell(sheet, columnBegin, rownum, columnnum, columnnum,COLUMN);
                
            }else{
                    columnMergeCount.put(columnnum+1, -1);
            }
        }
    }

    
    /**
     * 构建自动合并的行列
     * @param row
     * @param list
     * @return
     */
    private Row buildComplexRow(Sheet sheet,Row row,List dataList,List<String> list,int rownum){
        for(int i = 0;i<list.size();i++){
            String value = list.get(i);
            Cell cell = buildCell(row, rownum, i, value,dataList,list);//先创建
            buildMerge(sheet);
        }
        return row;
    }
    
    /**
     * 反射调用get方法取值
     */
    private Row buildComplexRow(Sheet sheet,Row row,Object obj,int rownum,List dataList){
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(int i = 0;i<fields.length;i++){
            String name = fields[i].getName();
            Object objValue = null;
            try {
                objValue = ReflectionUtils.invokeGetterMethod(obj, name);
            }catch(Exception e){
                logger.error(e.getMessage(), e);
                throw new ExcelException(e);
            }
            String value = String.valueOf(objValue);
            Cell cell = buildCell(row, rownum, i, value,dataList,obj);//先创建
            buildMerge(sheet);//后合并
        }
        return row;
    }
    
    
    /**
     * 列合并 状态 校验
     */
    private boolean checkColumnMergeRegion(int preColumn){
        //TODO 
        return (-1 != columnMergeCount.get(preColumn)); 
    }
    /**
     * 行合并 状态 校验
     */
    private boolean checkRowMergeRegion(int status){
        return (-1 != status);
    }
    
    /**
     * 查找开始合并index
     * @param value 填充值
     * @param row 行号
     * @param rowCellIndex 行cell index
     * @return 合并起始位置
     */
    private int getRowFirstMergeIndex(String value,Row row ,int rowCellIndex){
    	int beginMergeIndex = -1;
        if(rowCellIndex > 0 && checkRowMergeRegion(rowCellIndex) && value.equals(row.getCell(rowCellIndex-1).getStringCellValue().trim())){
        	int formerIndex = getRowFirstMergeIndex(value,row,rowCellIndex-1);
        	if(formerIndex==-1){
        		beginMergeIndex = rowCellIndex-1;
        	}else{
        		beginMergeIndex = formerIndex;
        	}
        }
        return beginMergeIndex;
    }
    
    private int getColumnFirstMergeIndex(String value,Sheet sheet,int rowNum,int columnNum){
    	int beginMergeIndex = -1;
        if(rowNum>0 && null!=sheet.getRow(rowNum-1).getCell(columnNum) && value.equals(sheet.getRow(rowNum-1).getCell(columnNum).getStringCellValue())){
       	 int formerIndex = getColumnFirstMergeIndex(value,sheet,rowNum-1,columnNum);
       	 if(formerIndex == -1){
       		 beginMergeIndex = rowNum-1;
       	 }else{
       		 beginMergeIndex = formerIndex;
       	 }
        }
       return beginMergeIndex;
    }
    /**
     * 通过流输出至本地excel表中
     * @param excelname 文档名称
     * @param filedir  输出目录(暂时未使用)
     * @throws Exception
     */
    public void exportToExcel(String excelname, HttpServletResponse response, String filedir)throws Exception {
        if(type==0){//如果未传递文档参数，则默认为07版本excel
            this.type=XSSF;
        }
        String extension = this.type==1?".xls":".xlsx";
        BufferedOutputStream bos = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bos = new BufferedOutputStream(os);
            response.setHeader("Content-disposition", "attachment;filename=\""
                    + (new String(excelname.getBytes("GB2312"),"ISO8859-1")+extension + "\""));
            
            this.workbook.write(bos);
            bos.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally{
            if(os!=null) os.close();
            if(bos!=null) bos.close();
        }
    }
    
    /**
     * 设置可以合并列
     */
    public void setMayMergeColumn(List<Integer> mayMergeColumn) {
        this.mayMergeColumn = mayMergeColumn;
    }
    
    public Workbook getWorkbook() {
        return workbook;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public List<Integer> getMayMergeRow() {
        return mayMergeRow;
    }

    public void setMayMergeRow(List<Integer> mayMergeRow) {
        this.mayMergeRow = mayMergeRow;
    }     
    
}
