package com.example.demo;

import com.example.demo.mapper.SettingsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class MybatisCloudConfigDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(MybatisCloudConfigDemoApplication.class, args);
  }

  // Refresh scoped controller
  @RefreshScope
  @RestController
  class MessageRestController {

    @Value("${message:Hello default}")
    private String message;

    @RequestMapping("/message")
    String getMessage() {
      return this.message;
    }

    @PostConstruct
    void init() {
      System.out.println(getClass() + " init ...");
    }
  }

  // None Refresh scoped controller
  @RestController
  class SettingsRestController {

    private final SettingsMapper settingsMapper;

    SettingsRestController(SettingsMapper settingsMapper) {
      this.settingsMapper = settingsMapper;
    }

    @RequestMapping("/settings")
    List<Map<String, String>> getSettings() {
      return settingsMapper.findAll();
    }

    @PostConstruct
    void init() {
      System.out.println(getClass() + " init ...");
    }
  }

}
