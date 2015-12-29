package cn.mmb.delivery.util;

import java.util.List;

public class Page<T> {

	private List<T> list; //每页数据
	private int totalPage; //总页数
	private int pageSize; //每页显示记录数
	private int totalRecord; //总记录数
	
	private int offset;  //limit偏移量
	private int pageNum; //要显示的页码
	
	private int startPage;  //底部页码跳转显示的开始页码
	private int endPage;  //底部页码跳转显示的结束页码
	
	public Page() {
		
	}
	
	public Page(int totalRecord,int pageNum,int pageSize){
		this.totalRecord = totalRecord;
		this.pageSize = pageSize;
		this.pageNum = pageNum;
		
		//计算总页数
		if(this.totalRecord % this.pageSize == 0){
			this.totalPage = this.totalRecord / this.pageSize;
		}else{
			this.totalPage = this.totalRecord / this.pageSize + 1;
		}
		
		//计算偏移量
		this.offset = (this.pageNum - 1)*pageSize;
		
		//计算在页面中应显示的页码
		if(this.totalPage <= 10){
			this.startPage = 1;
			this.endPage = this.totalPage;
		}else{
			this.startPage = this.pageNum - 5;
			this.endPage = pageNum + 4;
			if(this.startPage < 1){
				this.startPage = 1;
				this.endPage = 10;
			}
			if(this.endPage > this.totalPage){
				this.startPage = this.totalPage - 9;
				this.endPage = this.totalPage;
			}
		}
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
}
