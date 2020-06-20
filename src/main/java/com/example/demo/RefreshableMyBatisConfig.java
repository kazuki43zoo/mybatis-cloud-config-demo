package com.example.demo;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("refreshable-mybatis")
@Configuration
public class RefreshableMyBatisConfig {

  // The mybatis-spring does not support to scan mappers as refresh scope bean(scoped proxy bean) in current version.
  // This configuration is workaround.
  static class MyBatisConfig {
    @Bean
    MapperScannerConfigurer mapperScannerConfigurer() {
      MapperScannerConfigurer configurer = new MapperScannerConfigurer() {
        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
          String[] beforeBeans = registry.getBeanDefinitionNames();
          super.postProcessBeanDefinitionRegistry(registry);
          List<String> mapperBeans = new ArrayList<>(Arrays.asList(registry.getBeanDefinitionNames()));
          mapperBeans.removeAll(Arrays.asList(beforeBeans));
          mapperBeans.forEach(beanName -> {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            beanDefinition.setScope("refresh"); // Set scope to 'refresh'
            // Create scoped proxy for injecting a refresh scope mapper to the singleton component.
            // This code is unnecessary if inject to a same scope(refresh scope)  component.
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            BeanDefinitionHolder proxyDefinitionHolder =
                ScopedProxyUtils.createScopedProxy(definitionHolder, registry, false);
            if (registry.containsBeanDefinition(proxyDefinitionHolder.getBeanName())) {
              registry.removeBeanDefinition(proxyDefinitionHolder.getBeanName());
            }
            BeanDefinitionReaderUtils.registerBeanDefinition(proxyDefinitionHolder, registry);
          });
        }
      };
      configurer.setBasePackage("com.example.demo.mapper");
      configurer.setAnnotationClass(Mapper.class);
      return configurer;
    }
  }

}
