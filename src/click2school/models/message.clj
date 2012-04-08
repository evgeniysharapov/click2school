(ns click2school.models.message
  (:require [clj-record.boot]
            [click2school.models.user])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (belongs-to to-user :model user)
  (belongs-to from-user :model user)))
