package com.example.demo.bean.demo1;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ServiceAspect {

    @Pointcut("@annotation(com.example.demo.bean.demo1.Log)")
    public void pointcut() {
    }

    ;

    @Around("pointcut()")
    public Object point(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取代理对象（AOP 生成的动态代理类）
        Object proxy = joinPoint.getThis();
        // System.out.println("代理对象: " + proxy.getClass().getName());

        // 2. 获取目标对象（原始 Bean）
        Object target = joinPoint.getTarget();
        // System.out.println("目标对象: " + target.getClass().getName());

        Object[] args = joinPoint.getArgs();
        // 3. 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();  // 获取 Method 对象

        // 4. 获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // System.out.println("目标类: " + targetClass.getName());

        // 5. 获取方法上的注解
        Log annotation = method.getAnnotation(Log.class);
        // System.out.println("方法注解: " + annotation);

        // 6. 获取方法名
        String methodName = method.getName();
        // System.out.println("方法名称: " + methodName);

        System.out.println(targetClass.getSimpleName() + "." + methodName + "方法执行开始...");
        try {
            Object result = joinPoint.proceed();  // 执行目标方法
            System.out.println(targetClass.getSimpleName() + "." + methodName + "方法执行结束...");
            return result;
        } catch (Exception e) {
            System.out.println(targetClass.getSimpleName() + "." + methodName + "方法执行异常...");
            throw e;
        }
    }
}
