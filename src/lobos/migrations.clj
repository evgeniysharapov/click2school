(ns lobos.migrations
  (:refer-clojure :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]]
               core schema config helpers)))

(defmacro def-new-table [tname & ddl]
  `(defmigration ~(symbol (str "add-" (name  tname) "-table"))
    (up [] (create
            (tbl ~tname
                 ~@ddl
                 )))
    (down [] (drop (table ~tname)))))

;;; teachers, parents, studetns 
(def-new-table :roles
  (varchar :role-name 10))

(def-new-table :persons
  (varchar :first-name 100)
  (varchar :middle-name 100)
  (varchar :last-name 100)
  (varchar :org 100)
  (boolean :gender))

(def-new-table :users
  (varchar :name 100 :unique)
  (varchar :password 100)
  (refer-to :roles)
  (refer-to :persons)
  (timestamp :last-logged-in :null)
  (check :name (> (length :name) 1))
  (check :name (> (length :password) 1)))

(def-new-table :messages
  (varchar :subject 200)
  (text :content)
  (boolean :read)
  (refer-to "to" :users)
  (refer-to "from" :users))

;;; assignment types:  form, task, etc.
(def-new-table :asgnmt-types
  (varchar :atype 25))

;;; Assignments: form, task, etc.
(def-new-table :asgnmts
  (varchar :title 100)
  (date :due-date :null)
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
