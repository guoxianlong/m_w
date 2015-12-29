package com.mmb.framework.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.mmb.framework.tag.maker.TemplateMarker;

import freemarker.template.TemplateException;


public class MmbHeader  extends TagSupport{
    private static final long serialVersionUID = -4685772581897418183L;
    public static Logger logger = Logger.getLogger(MmbHeader.class);
    private String title;
    @Override
    public int doEndTag() throws JspException {
        // TODO Auto-generated method stub
        return super.doEndTag();
    }

    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String contextPath = request.getContextPath();
        Map map = new HashMap();
        map.put("contextPath", contextPath);
        map.put("title", title);
        String outStr = null;
        try {
            outStr = TemplateMarker.getMarker().getOutString("header.ftl", map);
            pageContext.getOut().write(outStr);
        } catch (TemplateException e) {
            logger.error("模板异常：", e);
        } catch (IOException e) {
            logger.error("读取模板：", e);
        }
        return super.doStartTag();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void release() {
        this.title=null;
        super.release();
    }

}
