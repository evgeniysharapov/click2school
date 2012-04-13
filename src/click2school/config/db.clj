(ns click2school.config.db
  (:require [clojure.string :as s])
  (:import (java.net URI)))

(defn heroku-db
  "Generate the db map according to Heroku environment when available."
  []
  (when (System/getenv "DATABASE_URL")
    (let [url (URI. (System/getenv "DATABASE_URL"))
          host (.getHost url)
          port (if (pos? (.getPort url)) (.getPort url) 5432)
          path (.getPath url)]
      (merge
       {:subname (str "//" host ":" port path)}
       (when-let [user-info (.getUserInfo url)]
         {:user (first (s/split user-info #":"))
          :password (second (s/split user-info #":"))})))))



(def db (merge {:classname "org.postgresql.Driver"
                :subprotocol "postgresql"
                :subname "//localhost:5432/click2school"}
               (heroku-db)))

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
