package com.teamtreehouse.techdegrees.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.teamtreehouse.techdegrees.model.Todo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

public class Sql2oTodoDaoTest {


  private Sql2oTodoDao dao;
  private Connection conn;

  @Before
  public void setUp() throws Exception {
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    dao = new Sql2oTodoDao(sql2o);
    conn = sql2o.open();
  }

  @After
  public void tearDown() throws Exception {
    conn.close();
  }

  @Test
  public void addingTodoSetsId() throws Exception {
    Todo todo = newTestTodo();
    int originalTodoId = todo.getId();

    dao.add(todo);

    assertNotEquals(originalTodoId, todo.getId());
  }

  @Test
  public void updatingTodoUpdatesNameAndCompletedStatus() throws Exception {
    Todo todo = newTestTodo();
    String newName = "New Name";
    todo.setCompleted(true);
    todo.setName(newName);

    dao.update(todo);

    assertEquals(todo.getName(), newName);
    assertEquals(todo.isCompleted(), true);
  }

  @Test
  public void deletingTodoDeletesEntry() throws Exception {
    Todo todo = newTestTodo();
    int originalTodoId = todo.getId();

    dao.delete(todo.getId());

    assertEquals(dao.findById(originalTodoId), null);
  }

  @Test
  public void existingTodoCanBeFindById() throws Exception {
    Todo todo = newTestTodo();
    dao.add(todo);

    Todo foundTodo = dao.findById(todo.getId());

    assertEquals(todo, foundTodo);
  }

  @Test
  public void findAllReturnAllTodos() throws Exception {
    Todo todo1 = newTestTodo();
    Todo todo2 = newTestTodo();
    List<Todo> actualList = new ArrayList<>();
    actualList.add(todo1);
    actualList.add(todo2);
    
    dao.add(todo1);
    dao.add(todo2);
    List<Todo> retrievedList = dao.findAll();

    assertEquals(retrievedList, actualList);
  }

  @Test
  public void noTodosReturnEmptyList() throws Exception {
    assertEquals(0, dao.findAll().size());
  }

  private Todo newTestTodo() {
    return new Todo("Test");
  }
}