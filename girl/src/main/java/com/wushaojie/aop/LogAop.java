package com.wushaojie.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAop {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(LogAop.class);

    @Before("execution(public * com.wushaojie.controller.HelloController.*(..))")
    public void doBefore(JoinPoint joinPoint){
    ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    //url
    logger.info("url={}",request.getRequestURL());

    //method
    logger.info("method={}",request.getMethod());

    //ip
    logger.info("ip={}",request.getRemoteAddr());

    //类方法
    logger.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());

    //参数
    logger.info("args={}",joinPoint.getArgs());
    }






}
