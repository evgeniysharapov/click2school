(ns lobos.config
  (:use lobos.connectivity))

(comment 
  (def db {:classname "org.postgresql.Driver"
           :subprotocol "postgresql"
           :subname "//localhost:5432/click2school"
           :user "postgres"
           :password "postgres"}))

(def db {:classname "com.mysql.jdbc.Driver"
          :subprotocol "mysql"
          :subname "//localhost:3306/click2school"
          :user "root"
          :password ""})

;;; reloading config
(when (@global-connections :default-connection)
  (close-global))
(open-global db)
