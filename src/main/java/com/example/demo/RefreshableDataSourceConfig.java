package com.example.demo;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Optional;

@Profile("refreshable-ds")
@Configuration
public class RefreshableDataSourceConfig {

  @Bean
  @RefreshScope
  DataSource dataSource(@Value("${db.name:current}") String dbName) {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .setName(dbName)
        .addScripts("classpath:schema.sql", "classpath:data-" + dbName + ".sql").build();
  }

  // The spring-boot cannot enable auto-configure of MyBatis Spring Boot when DataSource is refresh scope.
  // This configuration is workaround.
  @MapperScan
  @EnableConfigurationProperties(MybatisProperties.class)
  static class MyBatisConfig {
    @Bean
    SqlSessionFactory sqlSessionFactory(DataSource dataSource, MybatisProperties mybatisProperties) throws Exception {
      SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
      factoryBean.setDataSource(dataSource);
      factoryBean.setVfs(SpringBootVFS.class);
      Optional.ofNullable(mybatisProperties.getConfiguration()).ifPresent(factoryBean::setConfiguration);
      Optional.ofNullable(mybatisProperties.getConfigurationProperties()).ifPresent(factoryBean::setConfigurationProperties);
      // ...
      return factoryBean.getObject();
    }

    // The spring-boot cannot enable auto-configure of MyBatis Spring Boot when DataSource is refresh scope.
    @Bean
    SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
      return new SqlSessionTemplate(sqlSessionFactory);
    }
  }

}
