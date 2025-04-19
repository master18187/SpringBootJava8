package com.example.demo.bean.demo1;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class Service2 implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    Service1 service1;

    @Log
    public void echo() {
        System.out.println("service2");
        // service1.echo();
        System.out.println(service1);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 会报NPE，因为初始化service1时，依赖注入service2，这时候service1未实例化完成，service2是获取不到的
//        Service1 service11 = applicationContext.getBean("service1", Service1.class);
//        service11.echo();
//        System.out.println(service11);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
