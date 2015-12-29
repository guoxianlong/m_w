package com.mmb.framework.support;

import java.util.List;

public abstract class Page {
	// 返回json数据
	private List<Object> Result;
	public final static String DESC = "DESC";// 倒序
	public final static String ASC = "ASC";// 正序
	// 分页条件
	private int allRow;// 总数据量
	private int totalPage=1;// 总页数
	private int currentPage=1;// 当前页
	private int index;// 检索起始位置
	private int pageSize = 15;// 每页数据量

	public int getAllRow() {
		return allRow;
	}

	public void setAllRow(int allRow) {
		this.allRow = allRow;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		 this.pageSize = pageSize;
	}

	public static int countTotalPage(final int pageSize, final int allRow) {
		int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow
				/ pageSize + 1;
		return totalPage;
	}

	public static int countOffset(final int pageSize, final int currentPage) {
		final int offset = pageSize * (currentPage - 1);
		return offset;
	}

	public static int showCurrentPage(final int pageSize, final int beginindex) {
		final int page = beginindex / pageSize + 1;
		return page;
	}

	public static int countCurrentPage(int page) {
		final int curPage = (page == 0 ? 1 : page);
		return curPage;
	}


	public List<Object> getResult() {
        return Result;
    }

    public void setResult(List<Object> result) {
        Result = result;
    }

    public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}


	public String getDESC() {
		return DESC;
	}

	public String getASC() {
		return ASC;
	}
}
