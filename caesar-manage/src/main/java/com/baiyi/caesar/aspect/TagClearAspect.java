package com.baiyi.caesar.aspect;

import com.baiyi.caesar.domain.annotation.TagClear;
import com.baiyi.caesar.service.tag.BusinessTagService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author baiyi
 * @Date 2021/5/25 8:57 上午
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class TagClearAspect {

    @Resource
    private BusinessTagService businessTagService;

    @Pointcut(value = "@annotation(com.baiyi.caesar.domain.annotation.TagClear)")
    public void annotationPoint() {
    }

    @Around("@annotation(tagClear)")
    public Object around(ProceedingJoinPoint joinPoint, TagClear tagClear) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] params = methodSignature.getParameterNames();// 获取参数名称
        Object[] args = joinPoint.getArgs();// 获取参数值
        if (params != null && params.length != 0) {
            Integer businessId = Integer.valueOf(args[0].toString());
            log.info("清除业务标签: businessType = {} , businessId = {}", tagClear.type().getType(), businessId);
            businessTagService.deleteByBusinessTypeAndId(tagClear.type().getType(), businessId);
        }
        return joinPoint.proceed();
    }


}