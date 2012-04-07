(ns lobos.migrations
  (:refer-clojure :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]]
               core schema config helpers)
        (korma
         [db :only [defdb]]
         [core :only [insert values defentity]]))
  (:require [clojure.java.jdbc :as sql]
            [clj-yaml.core :as yaml]))

(defmacro def-new-table [tname & ddl]
  `(defmigration ~(symbol (str "add-" (name  tname) "-table"))
    (up [] (create
            (tbl ~tname
                 ~@ddl
                 )))
    (down [] (drop (table ~tname)))))

;;; teachers, parents, studetns 
(def-new-table :roles
  (varchar :role_name 10))

(def-new-table :persons
  (varchar :first_name 100)
  (varchar :middle_name 100)
  (varchar :last_name 100)
  (varchar :org 100)
  (varchar :email 100)
  (boolean :gender))

(def-new-table :users
  (varchar :name 100 :unique)
  (varchar :password 100)
  (refer-to :roles)
  (refer-to :persons)
  (timestamp :last_logged_in :null)
  (check :name (> (length :name) 1))
  (check :name (> (length :password) 1)))

(def-new-table :user_groups
  (varchar :name 100)
  (refer-to :users))

(defmigration add-user-groups-link-table
  (up [] (create (table :user_group_links
                        (integer :user_id [:refer :users :id :on-delete :set-null])
                        (integer :user_group_id [:refer :user_groups :id :on-delete :set-null])
                        (timestamps)
                        (primary-key [:user_id :user_group_id]))))
  (down [] (drop (table :user_group_links))))

(def-new-table :messages
  (varchar :subject 200)
  (text :content)
  (boolean :read)
  (refer-to "to" :users)
  (refer-to "from" :users))

;;; assignment types:  form, task, etc.
(def-new-table :asgnmt_types
  (varchar :atype 25))

;;; Assignments: form, task, etc.
(def-new-table :asgnmts
  (varchar :title 100)
  (date :due_date :null)
  (text :content))

;;; Who assignment is assigned to
;;; check marks and their time show that it checked and when
(def-new-table :asgnees
  (boolean :checkmark)
  (timestamp :checked :null)
  (refer-to :asgnmts)
  (refer-to :users))

;;; Who assigned the assignment
(def-new-table :asgners
  (refer-to :asgnmts)
  (refer-to :users))

;;; Insert some test data
(defmigration add-basic-data
  (up [] (do
           (defentity roles)
           (insert roles (values (:roles (yaml/parse-string (slurp "./resources/fixture.yml")))))
           (defentity persons)
           (insert persons (values (:persons (yaml/parse-string (slurp "./resources/fixture.yml")))))
           (defentity users)
           (insert users (values (:users (yaml/parse-string (slurp "./resources/fixture.yml")))))
           (defentity messages)
           (insert messages (values (:messages (yaml/parse-string (slurp "./resources/fixture.yml")))))
           ))
  (down [] (print "delete data")))
