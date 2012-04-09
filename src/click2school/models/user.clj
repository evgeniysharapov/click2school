(ns  click2school.models.user
  (:require [clj-record.boot]
            [click2school.models.person :as p])
  (:use [click2school.config.db]))

(clj-record.core/init-model
 (:associations
  (belongs-to person :model person)
  (belongs-to role :model role)
  (has-many messages-to :model message :fk to_user_id)
  (has-many messages-from :model message :fk from_user_id)))


;(clj-record.core/find-records "message" {:to_user_id ((get-record 1) :id)})
;(find-messages-to (get-record 1))


