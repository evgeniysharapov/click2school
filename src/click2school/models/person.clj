(ns click2school.models.person
  (:require [clj-record.boot])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (has-many user)))
