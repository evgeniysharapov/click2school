(ns click2school.models.question
  (:require [clj-record.boot])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (has-many answer-options :model answer_option :fk question_id)))


