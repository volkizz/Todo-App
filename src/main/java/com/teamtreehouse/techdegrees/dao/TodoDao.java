package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.exc.DaoException;
import com.teamtreehouse.techdegrees.model.Todo;

import java.util.List;

public interface TodoDao {
  void add(Todo todo) throws DaoException;
  void update (Todo todo)throws DaoException;
  void delete (int id)throws DaoException;

  Todo findById (int id);
  List<Todo> findAll();
}
