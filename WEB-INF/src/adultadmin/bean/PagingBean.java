/*
 * Created on 2006-8-30
 *
 */
package adultadmin.bean;

/**
 * 作者：李北金
 * 
 * 创建日期：2006-8-30
 * 
 * 说明：分页相关。
 */
public class PagingBean {
    int totalCount;

    int totalPageCount;

    int currentPageIndex;

    String prefixUrl;

    int countPerPage;
    
    String JsFunction;//onclick="JsFunction"
    
    public PagingBean(int currentPageIndex, int totalCount, int countPerPage) {
    	this.countPerPage = countPerPage;
        totalPageCount = (totalCount - 1) / countPerPage + 1;
    	
        if (currentPageIndex >= totalPageCount) {
        	currentPageIndex = totalPageCount - 1;
        }
        if (currentPageIndex < 0) {
        	currentPageIndex = 0;
        }

    	this.totalCount = totalCount;
    	this.currentPageIndex = currentPageIndex; 
    }
    
    public PagingBean() {
    	
    }
    
    
    
    public String getJsFunction() {
		return JsFunction;
	}

	public void setJsFunction(String jsFunction) {
		JsFunction = jsFunction;
	}

	/**
     * @return Returns the currentPageIndex.
     */
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }
    /**
     * @param currentPageIndex The currentPageIndex to set.
     */
    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }
    /**
     * @return Returns the prefixUrl.
     */
    public String getPrefixUrl() {
        return prefixUrl;
    }
    /**
     * @param prefixUrl The prefixUrl to set.
     */
    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }
    /**
     * @return Returns the totalCount.
     */
    public int getTotalCount() {
        return totalCount;
    }
    /**
     * @param totalCount The totalCount to set.
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    /**
     * @return Returns the totalPageCount.
     */
    public int getTotalPageCount() {
        return totalPageCount;
    }
    /**
     * @param totalPageCount The totalPageCount to set.
     */
    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

	public int getCountPerPage() {
		return countPerPage;
	}

	public void setCountPerPage(int countPerPage) {
		this.countPerPage = countPerPage;
	}
    
}
