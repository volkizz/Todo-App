package com.teamtreehouse.techdegrees;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;

import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.model.Todo;
import com.teamtreehouse.techdegrees.testing.ApiClient;
import com.teamtreehouse.techdegrees.testing.ApiResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;
import spark.Spark;

public class AppTest {
  public static final String PORT = "4568";
  public static final String TEST_DATASOURCE = "jdbc:h2:mem:test";
  private Sql2oTodoDao dao;
  private Sql2o sql2o;
  private Connection con;
  private ApiClient client;
  private Gson gson;

  @BeforeClass
  public static void StartServer() throws Exception {
    String[] args = {PORT, TEST_DATASOURCE};
    App.main(args);
  }

  @AfterClass
  public static void StopServer() throws Exception {
    Spark.stop();
  }

  @Before
  public void setUp() throws Exception {
    sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
    dao = new Sql2oTodoDao(sql2o);
    con = sql2o.open();
    client = new ApiClient("http://localhost:" + PORT);
    gson = new Gson();
  }

  @After
  public void tearDown() throws Exception {
    con.close();
  }

  @Test
  public void requestingTodosReturnsAll() throws Exception {
    dao.add(new Todo("First"));
    dao.add(new Todo("Second"));

    ApiResponse res = client.request("GET", "/api/v1/todos");
    Todo[] todos = gson.fromJson(res.getBody(), Todo[].class);

    assertEquals(2, todos.length);
  }

  @Test
  public void postingTodoReturns201Status() throws Exception {
    Todo todo = new Todo("Test");

    ApiResponse res = client.request("POST", "/api/v1/todos", gson.toJson(todo));

    assertEquals(201, res.getStatus());
  }

  @Test
  public void postingTodoSavesEntry() throws Exception {
    Todo todo = new Todo("test");

    client.request("POST", "/api/v1/todos", gson.toJson(todo));

    assertEquals(1, dao.findAll().size());
    assertEquals("test", dao.findById(1).getName());
    assertEquals(false, dao.findById(1).isCompleted());
  }

  @Test
  public void puttingTodoChangesNameAndCompleted() throws Exception {
    Todo todo = new Todo("test");
    dao.add(todo);
    Map<String, Object> newTodo = new HashMap<>();
    newTodo.put("name", "updated name");
    newTodo.put("completed", true);

    client.request("PUT", String.format("/api/v1/todos/%d",todo.getId()), gson.toJson(newTodo));

    assertArrayEquals(new Object[]{"updated name", true},
        new Object[]{dao.findById(todo.getId()).getName(), dao.findById(todo.getId()).isCompleted()});
  }

  @Test
  public void deleteTodoDeletesProperTodo() throws Exception {
    Todo todo = new Todo("test");
    dao.add(todo);

    client.request("DELETE", String.format("/api/v1/todos/%d", todo.getId()));

    assertEquals(dao.findAll().size(), 0);
  }

  @Test
  public void deletingReturns204Status() throws Exception {
    Todo todo = new Todo("test");
    dao.add(todo);

    ApiResponse res = client.request("DELETE", String.format("/api/v1/todos/%d", todo.getId()));

    assertEquals(204, res.getStatus());
  }

  @Test
  public void deletingReturnsEmptyBody() throws Exception {
    Todo todo = new Todo("test");
    dao.add(todo);

    ApiResponse res = client.request("DELETE", String.format("/api/v1/todos/%d", todo.getId()));

    assertEquals("", res.getBody());
  }
}