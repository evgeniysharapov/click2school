(ns click2school.config.db)

(def db (System/getenv "DATABASE_URL"))

(comment 
  (def db {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost:5432/click2school"
           :user "postgres"
           :password "postgres"}))
(comment 
  (def db {:classname "com.mysql.jdbc.Driver"
           :subprotocol "mysql"
           :subname "//localhost:3306/click2school"
           :user "root"
           :password ""}))
