(ns click2school.models.answer_option
  (:require [clj-record.boot])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (belongs-to question :model question)))
