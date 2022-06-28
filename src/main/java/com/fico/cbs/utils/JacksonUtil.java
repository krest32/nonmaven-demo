package com.fico.cbs.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.DateFormat;

public class JacksonUtil
{
  private static ObjectMapper getMapper()
  {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    return mapper;
  }
  
  private static ObjectMapper getMapper(DateFormat dateFormat)
  {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.setDateFormat(dateFormat);
    return mapper;
  }
  
  public static <T> String bean2Str(T entity)
    throws JsonProcessingException
  {
    String beanStr = "";
    if (entity != null) {
      beanStr = getMapper().writeValueAsString(entity);
    }
    return beanStr;
  }
  
  public static <T> String bean2Str(T entity, DateFormat dateFormat)
    throws JsonProcessingException
  {
    String beanStr = "";
    if (entity != null) {
      beanStr = getMapper(dateFormat).writeValueAsString(entity);
    }
    return beanStr;
  }
  
  public static <T> T str2Bean(String source)
    throws JsonParseException, JsonMappingException, IOException
  {
    T entity = getMapper().readValue(source, new TypeReference<T>() {});
    return entity;
  }
  
  public static <T> T str2Bean(Class<T> clazz, String source)
    throws JsonParseException, JsonMappingException, IOException
  {
    T entity = getMapper().readValue(source, clazz);
    return entity;
  }
}