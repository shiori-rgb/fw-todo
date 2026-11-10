package com.example.todoapp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TodoMapper {

    List<Todo> search(@Param("keyword") String keyword,
                      @Param("category") String category,
                      @Param("order") String order);

    Todo findById(Long id);

    int insert(Todo todo);

    int update(Todo todo);

    int deleteById(Long id);
}
