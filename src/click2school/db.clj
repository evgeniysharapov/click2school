(ns click2school.db
  (:require [clojure.java.jdbc :as jdbc]))

(let [db-host "localhost"
      db-port 5432
      db-name "click2school"]

  (def db {:classname "org.postgresql.Driver" ; must be in classpath
           :subprotocol "postgresql"
           :subname (str "//" db-host ":" db-port "/" db-name)
           :user "postgres"
           :password "postgres"})

  (defn create-blogs
    "Create a table to store blog entries"
    []
    (jdbc/create-table
     :blogs
     [:id :integer]
     [:title "varchar(255)"]
     [:body :text]))

  (jdbc/with-connection db
    (create-blogs))
  )
