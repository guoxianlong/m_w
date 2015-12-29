package com.mmb.framework.listener;

import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import com.mmb.framework.tag.maker.TemplateMarker;
import freemarker.template.TemplateException;

public class MMBListener implements ServletContextListener {
    public static Logger logger = Logger.getLogger(MMBListener.class);
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
//        try {
//            TemplateMarker.getMarker().initCfg("/com/mmb/framework/tag/template/");//模板初始化
//        }catch (IOException e) {
//            logger.error("模板初始化异常:" + e.toString(),e);
//        }catch(TemplateException e){
//            logger.error("模板初始化异常:" + e.toString(),e);
//        }
    }

}
