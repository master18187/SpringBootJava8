package com.example.demo.spring.dynamicbean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DynamicServiceProvider implements InitializingBean, BeanFactoryAware, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private AutowireCapableBeanFactory beanFactory;
    private AbstractAutowireCapableBeanFactory beanFactoryInstance;

    private SingletonBeanRegistry singletonBeanRegistry;

    private BeanDefinitionRegistry beanDefinitionRegistry;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * 注册方式	                    生命周期支持（@PostConstruct）	依赖注入支持（@Autowired）	适用场景
         * registerSingleton	        ❌ 需手动处理	                ❌ 需手动注入	            已完全初始化的第三方对象
         * registerBeanDefinition	    ✅ 调用 getBean() 后触发	    ✅ 自动注入	            需要 Spring 管理的动态 Bean
         * AutowireCapableBeanFactory	✅ 自动触发	                ✅ 自动注入	            完全模拟常规 Bean 的初始化
         */

        // 使用这种，getBean会触发doCreateBean，使整个Bean由spring初始化管理，而不是显示自己调用
        // 注册 BeanDefinition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(SimpleDynamicServiceImpl.class);
        beanDefinitionRegistry.registerBeanDefinition("dynamicService", beanDefinition);

        // 显式触发初始化（包括生命周期回调）
        DynamicService dynamicService = applicationContext.getBean("dynamicService", DynamicService.class);
        dynamicService.hello();

        /* ---------------------------------------------------------------------------------------------------*/
        // 这种适合插件或者第三方Bean，完全不用Spring接管，直接用
        // 手动注册实例
        DynamicService dynamicService1 = new SimpleDynamicServiceImpl();
        singletonBeanRegistry.registerSingleton("dynamicService1", dynamicService1);

        // 手动触发初始化方法
        if (dynamicService1 instanceof InitializingBean) {
            ((InitializingBean) dynamicService1).afterPropertiesSet();
        }

        // 手动调用 @PostConstruct 方法（需依赖 CommonAnnotationBeanPostProcessor）
        for (BeanPostProcessor processor : beanFactoryInstance.getBeanPostProcessors()) {
            if (processor instanceof CommonAnnotationBeanPostProcessor) {
                processor.postProcessBeforeInitialization(dynamicService1, "dynamicService1");
            }
        }

        /* ---------------------------------------------------------------------------------------------------*/
        // 这种适合和Spring有部分接管，适合Adapter和Spring有部分注入依赖
        // 创建 Bean 实例
        DynamicService dynamicService2 = new SimpleDynamicServiceImpl();

        // 自动注入依赖并执行生命周期回调
        AutowireCapableBeanFactory autowireFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireFactory.autowireBean(dynamicService2); // 依赖注入
        autowireFactory.initializeBean(dynamicService2, "dynamicService2"); // 触发 @PostConstruct 和 afterPropertiesSet()

        // 注册到容器
        singletonBeanRegistry.registerSingleton("dynamicService2", dynamicService);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
        this.beanFactoryInstance = (AbstractAutowireCapableBeanFactory) beanFactory;

        this.beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
        this.singletonBeanRegistry = (SingletonBeanRegistry) beanFactory;
    }
}
