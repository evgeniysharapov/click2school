(ns lobos.migrations
  (:refer-clojure :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]]
               core schema config helpers)
        click2school.config.db
        (korma
         [db :only [defdb]]
         [core :only [insert values defentity delete]]))
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

;;; Insert some test data
(defmigration add-basic-data
  (up [] (do
           (let [fixtures (yaml/parse-string (slurp "./resources/fixture.yml"))]
             (defdb *db* db)
             (defentity roles)
             (insert roles (values (:roles fixtures)))
             (defentity persons)
             (insert persons (values (:persons fixtures)))
             (defentity users)
             (insert users (values (:users fixtures)))
             (defentity messages)
             (insert messages (values (:messages fixtures))))
           ))
  (down [] (do
             (defdb *db* db)
             (defentity messages)
             (delete messages)
             (defentity users)
             (delete users)
             (defentity persons)
             (delete persons)
             (defentity roles)
             (delete roles))))

;;; TODO: add support materials (images, etc.)
(def-new-table :questions
  (varchar :title 100)
  (varchar :qtype 25); either of CHOICE, BOOLEAN, TEXT, MULTIPLE
  (ntext   :question))
;;; BOOLEAN would just require check one box (e.g. I agree with terms
;;; and conditions)

;;; answers for CHOICE and MULTIPLE are stored here.
(def-new-table :answer_options
  (refer-to :questions)
  (boolean :correct)                    ; whether it is a correct answer or not
  (ntext :t_answer)                     ; text answer
;  (boolean :b_answer)                   ; yes or no
)
;;; This is a form (or questionnaire)
(def-new-table :forms
  (varchar :title 25)
  (varchar :description 200)
  (refer-to "composer" :users))

;;; Questions that are put in a form
(def-new-table :form_questions
  (refer-to :forms)
  (refer-to :questions)
  )

;;; Answers to the questions on the form
(def-new-table :form_q_replies
  (refer-to :form_questions)
  (refer-to "respondent" :users)
  ;; freeform answer (could be null)
  (ntext :answer)
  )

;;; when a user was choosing an answer (or choosing multiple ones)
;;; we put his choices here
(def-new-table :form_q_reply_choices
  (refer-to :form_q_replies)            ; question reply
  (refer-to :answer_options)            ; what was chosen
  )

;;; Insert some test data
(defmigration add-question-answer-data
  (up [] (do
           (let [fixtures (yaml/parse-string (slurp "./resources/fixture_01.yml"))]
             (defdb *db* db)
             (defentity questions)
             (insert questions (values (:questions fixtures)))
             (defentity answer_options)
             (insert answer_options (values (:answer_options fixtures))))
           ))
  (down [] (do (defdb *db* db)
               (defentity answer_options)
               (delete answer_options)
               (defentity questions)
               (delete questions))))


(defmigration make-forms-table-title-bigger
  (up [] (sql/with-connection click2school.config.db/db
           (sql/do-commands "ALTER TABLE forms ALTER COLUMN title TYPE varchar(100)")))
  (down [] (sql/with-connection click2school.config.db/db
             (sql/do-commands "ALTER TABLE forms ALTER COLUMN title TYPE varchar(25)"))))

