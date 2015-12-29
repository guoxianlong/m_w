package cn.mmb.productarrival.infrastructrue.transdto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 封装EasyUI中DataGrid的分页信息
 * <br/>因为请求参数中的‘每页数据量’与返回结果中的‘列表数据’使用的是同一个单词(rows)，所以需要特殊处理一下。
 * @author likaige
 * @create 2014年5月29日 上午11:21:21
 */
public class EasyuiPage<T> {
	
	/**
	 * 当前页
	 */
	private int page = 1;
	
	/**
	 * 每页数据量
	 */
	@JsonIgnore
	private int rows = 10;

	/**
	 * 总记录数
	 */
	private int total;
	
	/**
	 * 列表数据
	 */
	@JsonProperty("rows")
	private List<T> result = new ArrayList<T>();

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
	
	/**
	 * 获取检索起始位置
	 */
	public int getIndex() {
		return (this.page-1) * this.rows;
	}
	
	/**
	 * 获取每页显示的记录数
	 */
	public int getPageSize() {
		return this.rows;
	}
	
}
