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

public class MmbImport  extends TagSupport{
    private static final long serialVersionUID = -3035524201847480461L;
    public static Logger logger = Logger.getLogger(MmbImport.class);
    @Override
    public int doEndTag() throws JspException {
        // TODO Auto-generated method stub
        return super.doEndTag();
    }

    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        String contextPath = request.getContextPath();
        Map paramContent = new HashMap();
        String outStr = null;
        try {
            outStr = TemplateMarker.getMarker().getOutString("import.ftl", paramContent);
            pageContext.getOut().write(outStr);
        } catch (TemplateException e) {
            logger.error("模板异常：", e);
        } catch (IOException e) {
            logger.error("读取模板：", e);
        }
        return super.doStartTag();
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        super.release();
    }

}
