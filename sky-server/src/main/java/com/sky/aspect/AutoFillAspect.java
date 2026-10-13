package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Signature;
import java.time.LocalDateTime;

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
    public void autoFillBefore(JoinPoint joinPoint) throws InvocationTargetException, IllegalAccessException {
        log.info("开始公共字段自动填充通知");
        // 获取数据库操作类型
        // 获取当前方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 获取注解
        AutoFill autoFill = method.getAnnotation(AutoFill.class);

        // 获取操作类型
        OperationType operationType = autoFill.value();

        // 获取数据对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        // 用ThreadLocal获取填充的值
        LocalDateTime now = LocalDateTime.now();
        Long currentUserId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型，填充不同的字段通过反射来设置值
        if(operationType == OperationType.INSERT){
            try {
                // 通过反射获取方法
                Method setUpdateTimeMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateTimeMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setCreateUserMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);

                // 调用方法设置值
                setUpdateTimeMethod.invoke(entity, now);
                setCreateTimeMethod.invoke(entity, now);
                setUpdateUserMethod.invoke(entity, currentUserId);
                setCreateUserMethod.invoke(entity, currentUserId);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else if(operationType == OperationType.UPDATE){
            // 为两个字段设置值
            try {
                // 通过反射获取方法
                Method setUpdateTimeMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 调用方法设置值
                setUpdateTimeMethod.invoke(entity, now);
                setUpdateUserMethod.invoke(entity, currentUserId);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
