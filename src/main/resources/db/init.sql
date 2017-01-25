-- noinspection SqlDialectInspectionForFile

-- noinspection SqlNoDataSourceInspectionForFile

CREATE TABLE IF NOT EXISTS todoList (
   id INT PRIMARY KEY auto_increment,
   name VARCHAR,
   completed BOOLEAN
);