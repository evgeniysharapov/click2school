(ns  click2school.models.form_question
  (:require [clj-record.boot])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (has-many question :model question)))

