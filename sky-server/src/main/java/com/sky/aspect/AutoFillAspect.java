package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 公共字段自动填充切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 定义切点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 定义通知
     */
    @Before("autoFillPointCut()")
    public void autoFillBefore(JoinPoint joinPoint) {
        Object [] args = joinPoint.getArgs();
        log.info("开始公共字段自动填充通知 参数：{}", args);
    }
}
