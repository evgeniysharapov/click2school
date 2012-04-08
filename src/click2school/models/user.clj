(ns  click2school.models.user
  (:require [clj-record.boot]
            [click2school.models.person :as p])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (belongs-to person :model person)
  (belongs-to role :model role)))
