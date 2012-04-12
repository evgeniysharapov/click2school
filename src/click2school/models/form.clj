(ns  click2school.models.form
  (:require [clj-record.boot])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (belongs-to user :model user :fk composer_user_id)
  (has-many form-question :model form_question :fk form_id)))

(create {:title "Super Form" :description "This is super puper form" :composer_user_id 1})
