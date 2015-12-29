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

public class MmbHtml extends TagSupport {
    private static final long serialVersionUID = -3173938551843714504L;
    public static Logger logger = Logger.getLogger(MmbHtml.class);

    private String title;

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().write("</html>");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext
                .getRequest();
        String contextPath = request.getContextPath();
        Map paramContent = new HashMap();
        paramContent.put("contextPath", contextPath);
        String outStr = null;
        try {
            outStr = TemplateMarker.getMarker().getOutString("html.ftl",
                    paramContent);
            pageContext.getOut().write(outStr);
        } catch (TemplateException e) {
            logger.error("模板异常：", e);
        } catch (IOException e) {
            logger.error("读取模板：", e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void release() {
        title = null;
        super.release();
    }

}
