package com.teamtreehouse.techdegrees;


import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

import com.google.gson.Gson;

import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.dao.TodoDao;
import com.teamtreehouse.techdegrees.model.Todo;
import org.sql2o.Sql2o;

public class App {

  public static void main(String[] args) {
    staticFileLocation("/public");
    String datasource = "jdbc:h2:~/todo.db";
    if (args.length > 0) {
      if (args.length != 2) {
        System.out.println("java Api <port> <datasource>");
        System.exit(0);
      }
      port(Integer.parseInt(args[0]));
      datasource = args[1];
    }

    Sql2o sql2o = new Sql2o(String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", datasource), "", "");
    TodoDao dao = new Sql2oTodoDao(sql2o);
    Gson gson = new Gson();

    get("/api/v1/todos", "application/json", (req, res) -> dao.findAll(), gson::toJson);

    post("/api/v1/todos", "application/json", (req, res) -> {
      Todo todo = gson.fromJson(req.body(), Todo.class);
      dao.add(todo);
      res.status(201);
      return todo;
    }, gson::toJson);

    put("/api/v1/todos/:id", "application/json", (req, res) -> {
      Todo todo = dao.findById(Integer.parseInt(req.params("id")));
      todo.setName(gson.fromJson(req.body(), Todo.class).getName());
      todo.setCompleted(gson.fromJson(req.body(), Todo.class).isCompleted());
      dao.update(todo);
      return todo;
    }, gson::toJson);

    delete("/api/v1/todos/:id", "application/json", (req, res) -> {
      dao.delete(Integer.parseInt(req.params("id")));
      res.status(204);
      return null;
    },gson::toJson);
  }

}
