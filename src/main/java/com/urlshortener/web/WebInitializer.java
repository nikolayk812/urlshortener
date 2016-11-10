package com.urlshortener.web;

import com.urlshortener.AppConfig;
import com.urlshortener.LocalAppConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class, LocalAppConfig.class, WebConfig.class, SecurityConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Filter[] getServletFilters() {
        //TODO: add CORS? http://websystique.com/springmvc/spring-mvc-4-restful-web-services-crud-example-resttemplate/
        return null;
    }

}