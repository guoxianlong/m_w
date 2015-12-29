package com.mmb.framework.tag;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

import com.mmb.framework.utils.StringUtils;

public class MmbIfPermit  implements BodyTag{
	private PageContext pageContext;
	
	private String operate;
	private String menu;
	

	public void doInitBody() throws JspException {
		
	}

	

	public void setBodyContent(BodyContent arg0) {
		// TODO Auto-generated method stub
		
	}

	public int doAfterBody() throws JspException {
		// TODO Auto-generated method stub
		return 0;
	}


	@SuppressWarnings("unchecked")
	public int doEndTag() throws JspException {
		return 0;
	}
	
	public int doStartTag() throws JspException {
		if (StringUtils.isEmpty(operate)){
			return 1;
		}
		Map<String,Map<String,String>> map = null;
		if(map.containsKey(menu)){
			Map tmap = map.get(menu);
			if(tmap.containsKey(operate)){
				return SKIP_BODY;
			}
		}
		return 1;
	}


	public Tag getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}


	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
		
	}


	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#setParent(javax.servlet.jsp.tagext.Tag)
	 */
	public void setParent(Tag arg0) {
		// TODO Auto-generated method stub
		
	}



	public void setOperate(String operate) {
		this.operate = operate;
	}



	public void setMenu(String menu) {
		this.menu = menu;
	}

}
