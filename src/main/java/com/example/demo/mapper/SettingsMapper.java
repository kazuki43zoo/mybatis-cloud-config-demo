package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SettingsMapper {

  @Select("SELECT s.* , '${db.version:v1}' as version FROM settings s ORDER BY key")
  List<Map<String, String>> findAll();

}
